/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Random;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.gui.Logger;
/*  11:    */ 
/*  12:    */ public class CrossValidationFoldMaker
/*  13:    */   extends AbstractTrainAndTestSetProducer
/*  14:    */   implements DataSourceListener, TrainingSetListener, TestSetListener, UserRequestAcceptor, EventConstraints, Serializable, StructureProducer
/*  15:    */ {
/*  16:    */   private static final long serialVersionUID = -6350179298851891512L;
/*  17: 45 */   private int m_numFolds = 10;
/*  18: 46 */   private int m_randomSeed = 1;
/*  19: 48 */   private boolean m_preserveOrder = false;
/*  20: 50 */   private transient Thread m_foldThread = null;
/*  21: 52 */   private boolean m_dataProvider = false;
/*  22: 53 */   private boolean m_trainingProvider = false;
/*  23: 54 */   private boolean m_testProvider = false;
/*  24:    */   
/*  25:    */   public CrossValidationFoldMaker()
/*  26:    */   {
/*  27: 57 */     this.m_visual.loadIcons("weka/gui/beans/icons/CrossValidationFoldMaker.gif", "weka/gui/beans/icons/CrossValidationFoldMaker_animated.gif");
/*  28:    */     
/*  29: 59 */     this.m_visual.setText("CrossValidationFoldMaker");
/*  30:    */   }
/*  31:    */   
/*  32:    */   private Instances getUpstreamStructure()
/*  33:    */   {
/*  34: 63 */     if ((this.m_listenee != null) && ((this.m_listenee instanceof StructureProducer)))
/*  35:    */     {
/*  36: 64 */       if (this.m_dataProvider) {
/*  37: 65 */         return ((StructureProducer)this.m_listenee).getStructure("dataSet");
/*  38:    */       }
/*  39: 67 */       if (this.m_trainingProvider) {
/*  40: 68 */         return ((StructureProducer)this.m_listenee).getStructure("trainingSet");
/*  41:    */       }
/*  42: 70 */       if (this.m_testProvider) {
/*  43: 71 */         return ((StructureProducer)this.m_listenee).getStructure("testSet");
/*  44:    */       }
/*  45:    */     }
/*  46: 74 */     return null;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public Instances getStructure(String eventName)
/*  50:    */   {
/*  51: 92 */     if ((!eventName.equals("trainingSet")) && (!eventName.equals("testSet"))) {
/*  52: 93 */       return null;
/*  53:    */     }
/*  54: 95 */     if (this.m_listenee == null) {
/*  55: 96 */       return null;
/*  56:    */     }
/*  57: 99 */     if ((eventName.equals("trainingSet")) && (this.m_trainingListeners.size() == 0)) {
/*  58:102 */       return null;
/*  59:    */     }
/*  60:105 */     if ((eventName.equals("testSet")) && (this.m_testListeners.size() == 0)) {
/*  61:108 */       return null;
/*  62:    */     }
/*  63:111 */     return getUpstreamStructure();
/*  64:    */   }
/*  65:    */   
/*  66:    */   public synchronized void connectionNotification(String eventName, Object source)
/*  67:    */   {
/*  68:125 */     super.connectionNotification(eventName, source);
/*  69:127 */     if (connectionAllowed(eventName)) {
/*  70:128 */       if (eventName.equals("dataSet"))
/*  71:    */       {
/*  72:129 */         this.m_dataProvider = true;
/*  73:130 */         this.m_trainingProvider = false;
/*  74:131 */         this.m_testProvider = false;
/*  75:    */       }
/*  76:132 */       else if (eventName.equals("trainingSet"))
/*  77:    */       {
/*  78:133 */         this.m_dataProvider = false;
/*  79:134 */         this.m_trainingProvider = true;
/*  80:135 */         this.m_testProvider = false;
/*  81:    */       }
/*  82:136 */       else if (eventName.equals("testSet"))
/*  83:    */       {
/*  84:137 */         this.m_dataProvider = false;
/*  85:138 */         this.m_trainingProvider = false;
/*  86:139 */         this.m_testProvider = true;
/*  87:    */       }
/*  88:    */     }
/*  89:    */   }
/*  90:    */   
/*  91:    */   public synchronized void disconnectionNotification(String eventName, Object source)
/*  92:    */   {
/*  93:155 */     super.disconnectionNotification(eventName, source);
/*  94:157 */     if (this.m_listenee == null)
/*  95:    */     {
/*  96:158 */       this.m_dataProvider = false;
/*  97:159 */       this.m_trainingProvider = false;
/*  98:160 */       this.m_testProvider = false;
/*  99:    */     }
/* 100:    */   }
/* 101:    */   
/* 102:    */   public void setCustomName(String name)
/* 103:    */   {
/* 104:171 */     this.m_visual.setText(name);
/* 105:    */   }
/* 106:    */   
/* 107:    */   public String getCustomName()
/* 108:    */   {
/* 109:181 */     return this.m_visual.getText();
/* 110:    */   }
/* 111:    */   
/* 112:    */   public String globalInfo()
/* 113:    */   {
/* 114:190 */     return "Split an incoming data set into cross validation folds. Separate train and test sets are produced for each of the k folds.";
/* 115:    */   }
/* 116:    */   
/* 117:    */   public void acceptTrainingSet(TrainingSetEvent e)
/* 118:    */   {
/* 119:201 */     Instances trainingSet = e.getTrainingSet();
/* 120:202 */     DataSetEvent dse = new DataSetEvent(this, trainingSet);
/* 121:203 */     acceptDataSet(dse);
/* 122:    */   }
/* 123:    */   
/* 124:    */   public void acceptTestSet(TestSetEvent e)
/* 125:    */   {
/* 126:213 */     Instances testSet = e.getTestSet();
/* 127:214 */     DataSetEvent dse = new DataSetEvent(this, testSet);
/* 128:215 */     acceptDataSet(dse);
/* 129:    */   }
/* 130:    */   
/* 131:    */   public void acceptDataSet(DataSetEvent e)
/* 132:    */   {
/* 133:225 */     if (e.isStructureOnly())
/* 134:    */     {
/* 135:227 */       TrainingSetEvent tse = new TrainingSetEvent(this, e.getDataSet());
/* 136:228 */       TestSetEvent tsee = new TestSetEvent(this, e.getDataSet());
/* 137:229 */       notifyTrainingSetProduced(tse);
/* 138:230 */       notifyTestSetProduced(tsee);
/* 139:231 */       return;
/* 140:    */     }
/* 141:233 */     if (this.m_foldThread == null)
/* 142:    */     {
/* 143:234 */       final Instances dataSet = new Instances(e.getDataSet());
/* 144:235 */       this.m_foldThread = new Thread()
/* 145:    */       {
/* 146:    */         public void run()
/* 147:    */         {
/* 148:238 */           boolean errorOccurred = false;
/* 149:    */           try
/* 150:    */           {
/* 151:240 */             Random random = new Random(CrossValidationFoldMaker.this.getSeed());
/* 152:241 */             if (!CrossValidationFoldMaker.this.m_preserveOrder) {
/* 153:242 */               dataSet.randomize(random);
/* 154:    */             }
/* 155:244 */             if ((dataSet.classIndex() >= 0) && (dataSet.attribute(dataSet.classIndex()).isNominal()) && (!CrossValidationFoldMaker.this.m_preserveOrder))
/* 156:    */             {
/* 157:247 */               dataSet.stratify(CrossValidationFoldMaker.this.getFolds());
/* 158:248 */               if (CrossValidationFoldMaker.this.m_logger != null) {
/* 159:249 */                 CrossValidationFoldMaker.this.m_logger.logMessage("[" + CrossValidationFoldMaker.this.getCustomName() + "] " + "stratifying data");
/* 160:    */               }
/* 161:    */             }
/* 162:254 */             for (int i = 0; i < CrossValidationFoldMaker.this.getFolds(); i++)
/* 163:    */             {
/* 164:255 */               if (CrossValidationFoldMaker.this.m_foldThread == null)
/* 165:    */               {
/* 166:256 */                 if (CrossValidationFoldMaker.this.m_logger == null) {
/* 167:    */                   break;
/* 168:    */                 }
/* 169:257 */                 CrossValidationFoldMaker.this.m_logger.logMessage("[" + CrossValidationFoldMaker.this.getCustomName() + "] Cross validation has been canceled!"); break;
/* 170:    */               }
/* 171:263 */               Instances train = !CrossValidationFoldMaker.this.m_preserveOrder ? dataSet.trainCV(CrossValidationFoldMaker.this.getFolds(), i, random) : dataSet.trainCV(CrossValidationFoldMaker.this.getFolds(), i);
/* 172:    */               
/* 173:265 */               Instances test = dataSet.testCV(CrossValidationFoldMaker.this.getFolds(), i);
/* 174:    */               
/* 175:    */ 
/* 176:268 */               TrainingSetEvent tse = new TrainingSetEvent(this, train);
/* 177:269 */               tse.m_setNumber = (i + 1);
/* 178:270 */               tse.m_maxSetNumber = CrossValidationFoldMaker.this.getFolds();
/* 179:271 */               String msg = CrossValidationFoldMaker.this.getCustomName() + "$" + CrossValidationFoldMaker.this.hashCode() + "|";
/* 180:273 */               if (CrossValidationFoldMaker.this.m_logger != null) {
/* 181:274 */                 CrossValidationFoldMaker.this.m_logger.statusMessage(msg + "seed: " + CrossValidationFoldMaker.this.getSeed() + " folds: " + CrossValidationFoldMaker.this.getFolds() + "|Training fold " + (i + 1));
/* 182:    */               }
/* 183:277 */               if (CrossValidationFoldMaker.this.m_foldThread != null) {
/* 184:279 */                 CrossValidationFoldMaker.this.notifyTrainingSetProduced(tse);
/* 185:    */               }
/* 186:284 */               TestSetEvent teste = new TestSetEvent(this, test);
/* 187:285 */               teste.m_setNumber = (i + 1);
/* 188:286 */               teste.m_maxSetNumber = CrossValidationFoldMaker.this.getFolds();
/* 189:288 */               if (CrossValidationFoldMaker.this.m_logger != null) {
/* 190:289 */                 CrossValidationFoldMaker.this.m_logger.statusMessage(msg + "seed: " + CrossValidationFoldMaker.this.getSeed() + " folds: " + CrossValidationFoldMaker.this.getFolds() + "|Test fold " + (i + 1));
/* 191:    */               }
/* 192:292 */               if (CrossValidationFoldMaker.this.m_foldThread != null) {
/* 193:293 */                 CrossValidationFoldMaker.this.notifyTestSetProduced(teste);
/* 194:    */               }
/* 195:    */             }
/* 196:    */           }
/* 197:    */           catch (Exception ex)
/* 198:    */           {
/* 199:    */             String msg;
/* 200:    */             String msg;
/* 201:298 */             errorOccurred = true;
/* 202:299 */             if (CrossValidationFoldMaker.this.m_logger != null) {
/* 203:300 */               CrossValidationFoldMaker.this.m_logger.logMessage("[" + CrossValidationFoldMaker.this.getCustomName() + "] problem during fold creation. " + ex.getMessage());
/* 204:    */             }
/* 205:303 */             ex.printStackTrace();
/* 206:304 */             CrossValidationFoldMaker.this.stop();
/* 207:    */           }
/* 208:    */           finally
/* 209:    */           {
/* 210:    */             String msg;
/* 211:    */             String msg;
/* 212:306 */             CrossValidationFoldMaker.this.m_foldThread = null;
/* 213:308 */             if (errorOccurred)
/* 214:    */             {
/* 215:309 */               if (CrossValidationFoldMaker.this.m_logger != null) {
/* 216:310 */                 CrossValidationFoldMaker.this.m_logger.statusMessage(CrossValidationFoldMaker.this.getCustomName() + "$" + CrossValidationFoldMaker.this.hashCode() + "|" + "ERROR (See log for details).");
/* 217:    */               }
/* 218:    */             }
/* 219:314 */             else if (isInterrupted())
/* 220:    */             {
/* 221:315 */               String msg = "[" + CrossValidationFoldMaker.this.getCustomName() + "] Cross validation interrupted";
/* 222:317 */               if (CrossValidationFoldMaker.this.m_logger != null)
/* 223:    */               {
/* 224:318 */                 CrossValidationFoldMaker.this.m_logger.logMessage("[" + CrossValidationFoldMaker.this.getCustomName() + "] Cross validation interrupted");
/* 225:    */                 
/* 226:320 */                 CrossValidationFoldMaker.this.m_logger.statusMessage(CrossValidationFoldMaker.this.getCustomName() + "$" + CrossValidationFoldMaker.this.hashCode() + "|" + "INTERRUPTED");
/* 227:    */               }
/* 228:    */               else
/* 229:    */               {
/* 230:324 */                 System.err.println(msg);
/* 231:    */               }
/* 232:    */             }
/* 233:    */             else
/* 234:    */             {
/* 235:327 */               String msg = CrossValidationFoldMaker.this.getCustomName() + "$" + CrossValidationFoldMaker.this.hashCode() + "|";
/* 236:329 */               if (CrossValidationFoldMaker.this.m_logger != null) {
/* 237:330 */                 CrossValidationFoldMaker.this.m_logger.statusMessage(msg + "Finished.");
/* 238:    */               }
/* 239:    */             }
/* 240:333 */             CrossValidationFoldMaker.this.block(false);
/* 241:    */           }
/* 242:    */         }
/* 243:336 */       };
/* 244:337 */       this.m_foldThread.setPriority(1);
/* 245:338 */       this.m_foldThread.start();
/* 246:    */       
/* 247:    */ 
/* 248:341 */       block(true);
/* 249:    */       
/* 250:343 */       this.m_foldThread = null;
/* 251:    */     }
/* 252:    */   }
/* 253:    */   
/* 254:    */   private void notifyTestSetProduced(TestSetEvent tse)
/* 255:    */   {
/* 256:    */     Vector<TestSetListener> l;
/* 257:355 */     synchronized (this)
/* 258:    */     {
/* 259:356 */       l = (Vector)this.m_testListeners.clone();
/* 260:    */     }
/* 261:358 */     if (l.size() > 0) {
/* 262:359 */       for (int i = 0; i < l.size(); i++)
/* 263:    */       {
/* 264:360 */         if (this.m_foldThread == null) {
/* 265:    */           break;
/* 266:    */         }
/* 267:365 */         ((TestSetListener)l.elementAt(i)).acceptTestSet(tse);
/* 268:    */       }
/* 269:    */     }
/* 270:    */   }
/* 271:    */   
/* 272:    */   protected void notifyTrainingSetProduced(TrainingSetEvent tse)
/* 273:    */   {
/* 274:    */     Vector<TrainingSetListener> l;
/* 275:378 */     synchronized (this)
/* 276:    */     {
/* 277:379 */       l = (Vector)this.m_trainingListeners.clone();
/* 278:    */     }
/* 279:381 */     if (l.size() > 0) {
/* 280:382 */       for (int i = 0; i < l.size(); i++)
/* 281:    */       {
/* 282:383 */         if (this.m_foldThread == null) {
/* 283:    */           break;
/* 284:    */         }
/* 285:388 */         ((TrainingSetListener)l.elementAt(i)).acceptTrainingSet(tse);
/* 286:    */       }
/* 287:    */     }
/* 288:    */   }
/* 289:    */   
/* 290:    */   public void setFolds(int numFolds)
/* 291:    */   {
/* 292:399 */     this.m_numFolds = numFolds;
/* 293:    */   }
/* 294:    */   
/* 295:    */   public int getFolds()
/* 296:    */   {
/* 297:408 */     return this.m_numFolds;
/* 298:    */   }
/* 299:    */   
/* 300:    */   public String foldsTipText()
/* 301:    */   {
/* 302:417 */     return "The number of train and test splits to produce";
/* 303:    */   }
/* 304:    */   
/* 305:    */   public void setSeed(int randomSeed)
/* 306:    */   {
/* 307:426 */     this.m_randomSeed = randomSeed;
/* 308:    */   }
/* 309:    */   
/* 310:    */   public int getSeed()
/* 311:    */   {
/* 312:435 */     return this.m_randomSeed;
/* 313:    */   }
/* 314:    */   
/* 315:    */   public String seedTipText()
/* 316:    */   {
/* 317:444 */     return "The randomization seed";
/* 318:    */   }
/* 319:    */   
/* 320:    */   public boolean getPreserveOrder()
/* 321:    */   {
/* 322:455 */     return this.m_preserveOrder;
/* 323:    */   }
/* 324:    */   
/* 325:    */   public void setPreserveOrder(boolean p)
/* 326:    */   {
/* 327:465 */     this.m_preserveOrder = p;
/* 328:    */   }
/* 329:    */   
/* 330:    */   public boolean isBusy()
/* 331:    */   {
/* 332:476 */     return this.m_foldThread != null;
/* 333:    */   }
/* 334:    */   
/* 335:    */   public void stop()
/* 336:    */   {
/* 337:486 */     if ((this.m_listenee instanceof BeanCommon)) {
/* 338:488 */       ((BeanCommon)this.m_listenee).stop();
/* 339:    */     }
/* 340:492 */     if (this.m_foldThread != null)
/* 341:    */     {
/* 342:493 */       Thread temp = this.m_foldThread;
/* 343:494 */       this.m_foldThread = null;
/* 344:495 */       temp.interrupt();
/* 345:496 */       temp.stop();
/* 346:    */     }
/* 347:    */   }
/* 348:    */   
/* 349:    */   private synchronized void block(boolean tf)
/* 350:    */   {
/* 351:507 */     if (tf) {
/* 352:    */       try
/* 353:    */       {
/* 354:510 */         if ((this.m_foldThread != null) && (this.m_foldThread.isAlive())) {
/* 355:511 */           wait();
/* 356:    */         }
/* 357:    */       }
/* 358:    */       catch (InterruptedException ex) {}
/* 359:    */     } else {
/* 360:516 */       notifyAll();
/* 361:    */     }
/* 362:    */   }
/* 363:    */   
/* 364:    */   public Enumeration<String> enumerateRequests()
/* 365:    */   {
/* 366:527 */     Vector<String> newVector = new Vector(0);
/* 367:528 */     if (this.m_foldThread != null) {
/* 368:529 */       newVector.addElement("Stop");
/* 369:    */     }
/* 370:531 */     return newVector.elements();
/* 371:    */   }
/* 372:    */   
/* 373:    */   public void performRequest(String request)
/* 374:    */   {
/* 375:542 */     if (request.compareTo("Stop") == 0) {
/* 376:543 */       stop();
/* 377:    */     } else {
/* 378:545 */       throw new IllegalArgumentException(request + " not supported (CrossValidation)");
/* 379:    */     }
/* 380:    */   }
/* 381:    */   
/* 382:    */   public boolean eventGeneratable(String eventName)
/* 383:    */   {
/* 384:560 */     if (this.m_listenee == null) {
/* 385:561 */       return false;
/* 386:    */     }
/* 387:564 */     if ((this.m_listenee instanceof EventConstraints))
/* 388:    */     {
/* 389:565 */       if ((((EventConstraints)this.m_listenee).eventGeneratable("dataSet")) || (((EventConstraints)this.m_listenee).eventGeneratable("trainingSet")) || (((EventConstraints)this.m_listenee).eventGeneratable("testSet"))) {
/* 390:568 */         return true;
/* 391:    */       }
/* 392:570 */       return false;
/* 393:    */     }
/* 394:573 */     return true;
/* 395:    */   }
/* 396:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.CrossValidationFoldMaker
 * JD-Core Version:    0.7.0.1
 */