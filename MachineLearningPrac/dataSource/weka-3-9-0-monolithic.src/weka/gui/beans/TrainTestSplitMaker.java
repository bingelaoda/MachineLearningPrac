/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Random;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.Instances;
/*   8:    */ import weka.gui.Logger;
/*   9:    */ 
/*  10:    */ public class TrainTestSplitMaker
/*  11:    */   extends AbstractTrainAndTestSetProducer
/*  12:    */   implements DataSourceListener, TrainingSetListener, TestSetListener, UserRequestAcceptor, EventConstraints, Serializable, StructureProducer
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = 7390064039444605943L;
/*  15: 45 */   private double m_trainPercentage = 66.0D;
/*  16: 46 */   private int m_randomSeed = 1;
/*  17: 48 */   private Thread m_splitThread = null;
/*  18: 50 */   private boolean m_dataProvider = false;
/*  19: 51 */   private boolean m_trainingProvider = false;
/*  20: 52 */   private boolean m_testProvider = false;
/*  21:    */   
/*  22:    */   public TrainTestSplitMaker()
/*  23:    */   {
/*  24: 55 */     this.m_visual.loadIcons("weka/gui/beans/icons/TrainTestSplitMaker.gif", "weka/gui/beans/icons/TrainTestSplittMaker_animated.gif");
/*  25:    */     
/*  26: 57 */     this.m_visual.setText("TrainTestSplitMaker");
/*  27:    */   }
/*  28:    */   
/*  29:    */   private Instances getUpstreamStructure()
/*  30:    */   {
/*  31: 61 */     if ((this.m_listenee != null) && ((this.m_listenee instanceof StructureProducer)))
/*  32:    */     {
/*  33: 62 */       if (this.m_dataProvider) {
/*  34: 63 */         return ((StructureProducer)this.m_listenee).getStructure("dataSet");
/*  35:    */       }
/*  36: 65 */       if (this.m_trainingProvider) {
/*  37: 66 */         return ((StructureProducer)this.m_listenee).getStructure("trainingSet");
/*  38:    */       }
/*  39: 68 */       if (this.m_testProvider) {
/*  40: 69 */         return ((StructureProducer)this.m_listenee).getStructure("testSet");
/*  41:    */       }
/*  42:    */     }
/*  43: 72 */     return null;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public Instances getStructure(String eventName)
/*  47:    */   {
/*  48: 90 */     if ((!eventName.equals("trainingSet")) && (!eventName.equals("testSet"))) {
/*  49: 91 */       return null;
/*  50:    */     }
/*  51: 93 */     if (this.m_listenee == null) {
/*  52: 94 */       return null;
/*  53:    */     }
/*  54: 97 */     if ((eventName.equals("trainingSet")) && (this.m_trainingListeners.size() == 0)) {
/*  55:100 */       return null;
/*  56:    */     }
/*  57:103 */     if ((eventName.equals("testSet")) && (this.m_testListeners.size() == 0)) {
/*  58:106 */       return null;
/*  59:    */     }
/*  60:109 */     return getUpstreamStructure();
/*  61:    */   }
/*  62:    */   
/*  63:    */   public synchronized void connectionNotification(String eventName, Object source)
/*  64:    */   {
/*  65:123 */     super.connectionNotification(eventName, source);
/*  66:125 */     if (connectionAllowed(eventName)) {
/*  67:126 */       if (eventName.equals("dataSet"))
/*  68:    */       {
/*  69:127 */         this.m_dataProvider = true;
/*  70:128 */         this.m_trainingProvider = false;
/*  71:129 */         this.m_testProvider = false;
/*  72:    */       }
/*  73:130 */       else if (eventName.equals("trainingSet"))
/*  74:    */       {
/*  75:131 */         this.m_dataProvider = false;
/*  76:132 */         this.m_trainingProvider = true;
/*  77:133 */         this.m_testProvider = false;
/*  78:    */       }
/*  79:134 */       else if (eventName.equals("testSet"))
/*  80:    */       {
/*  81:135 */         this.m_dataProvider = false;
/*  82:136 */         this.m_trainingProvider = false;
/*  83:137 */         this.m_testProvider = true;
/*  84:    */       }
/*  85:    */     }
/*  86:    */   }
/*  87:    */   
/*  88:    */   public synchronized void disconnectionNotification(String eventName, Object source)
/*  89:    */   {
/*  90:153 */     super.disconnectionNotification(eventName, source);
/*  91:155 */     if (this.m_listenee == null)
/*  92:    */     {
/*  93:156 */       this.m_dataProvider = false;
/*  94:157 */       this.m_trainingProvider = false;
/*  95:158 */       this.m_testProvider = false;
/*  96:    */     }
/*  97:    */   }
/*  98:    */   
/*  99:    */   public void setCustomName(String name)
/* 100:    */   {
/* 101:169 */     this.m_visual.setText(name);
/* 102:    */   }
/* 103:    */   
/* 104:    */   public String getCustomName()
/* 105:    */   {
/* 106:179 */     return this.m_visual.getText();
/* 107:    */   }
/* 108:    */   
/* 109:    */   public String globalInfo()
/* 110:    */   {
/* 111:188 */     return "Split an incoming data set into separate train and test sets.";
/* 112:    */   }
/* 113:    */   
/* 114:    */   public String trainPercentTipText()
/* 115:    */   {
/* 116:197 */     return "The percentage of data to go into the training set";
/* 117:    */   }
/* 118:    */   
/* 119:    */   public void setTrainPercent(double newTrainPercent)
/* 120:    */   {
/* 121:206 */     this.m_trainPercentage = newTrainPercent;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public double getTrainPercent()
/* 125:    */   {
/* 126:216 */     return this.m_trainPercentage;
/* 127:    */   }
/* 128:    */   
/* 129:    */   public String seedTipText()
/* 130:    */   {
/* 131:225 */     return "The randomization seed";
/* 132:    */   }
/* 133:    */   
/* 134:    */   public void setSeed(int newSeed)
/* 135:    */   {
/* 136:234 */     this.m_randomSeed = newSeed;
/* 137:    */   }
/* 138:    */   
/* 139:    */   public int getSeed()
/* 140:    */   {
/* 141:243 */     return this.m_randomSeed;
/* 142:    */   }
/* 143:    */   
/* 144:    */   public void acceptTrainingSet(TrainingSetEvent e)
/* 145:    */   {
/* 146:253 */     Instances trainingSet = e.getTrainingSet();
/* 147:254 */     DataSetEvent dse = new DataSetEvent(this, trainingSet);
/* 148:255 */     acceptDataSet(dse);
/* 149:    */   }
/* 150:    */   
/* 151:    */   public void acceptTestSet(TestSetEvent e)
/* 152:    */   {
/* 153:265 */     Instances testSet = e.getTestSet();
/* 154:266 */     DataSetEvent dse = new DataSetEvent(this, testSet);
/* 155:267 */     acceptDataSet(dse);
/* 156:    */   }
/* 157:    */   
/* 158:    */   public void acceptDataSet(DataSetEvent e)
/* 159:    */   {
/* 160:277 */     if (this.m_splitThread == null)
/* 161:    */     {
/* 162:278 */       final Instances dataSet = new Instances(e.getDataSet());
/* 163:279 */       this.m_splitThread = new Thread()
/* 164:    */       {
/* 165:    */         public void run()
/* 166:    */         {
/* 167:    */           try
/* 168:    */           {
/* 169:284 */             dataSet.randomize(new Random(TrainTestSplitMaker.this.m_randomSeed));
/* 170:285 */             int trainSize = (int)Math.round(dataSet.numInstances() * TrainTestSplitMaker.this.m_trainPercentage / 100.0D);
/* 171:    */             
/* 172:287 */             int testSize = dataSet.numInstances() - trainSize;
/* 173:    */             
/* 174:289 */             Instances train = new Instances(dataSet, 0, trainSize);
/* 175:290 */             Instances test = new Instances(dataSet, trainSize, testSize);
/* 176:    */             
/* 177:292 */             TrainingSetEvent tse = new TrainingSetEvent(TrainTestSplitMaker.this, train);
/* 178:    */             
/* 179:294 */             tse.m_setNumber = 1;
/* 180:295 */             tse.m_maxSetNumber = 1;
/* 181:296 */             if (TrainTestSplitMaker.this.m_splitThread != null) {
/* 182:297 */               TrainTestSplitMaker.this.notifyTrainingSetProduced(tse);
/* 183:    */             }
/* 184:301 */             TestSetEvent teste = new TestSetEvent(TrainTestSplitMaker.this, test);
/* 185:    */             
/* 186:303 */             teste.m_setNumber = 1;
/* 187:304 */             teste.m_maxSetNumber = 1;
/* 188:305 */             if (TrainTestSplitMaker.this.m_splitThread != null)
/* 189:    */             {
/* 190:306 */               TrainTestSplitMaker.this.notifyTestSetProduced(teste);
/* 191:    */             }
/* 192:308 */             else if (TrainTestSplitMaker.this.m_logger != null)
/* 193:    */             {
/* 194:309 */               TrainTestSplitMaker.this.m_logger.logMessage("[TrainTestSplitMaker] " + TrainTestSplitMaker.this.statusMessagePrefix() + " Split has been canceled!");
/* 195:    */               
/* 196:311 */               TrainTestSplitMaker.this.m_logger.statusMessage(TrainTestSplitMaker.this.statusMessagePrefix() + "INTERRUPTED");
/* 197:    */             }
/* 198:    */           }
/* 199:    */           catch (Exception ex)
/* 200:    */           {
/* 201:315 */             stop();
/* 202:316 */             if (TrainTestSplitMaker.this.m_logger != null)
/* 203:    */             {
/* 204:317 */               TrainTestSplitMaker.this.m_logger.statusMessage(TrainTestSplitMaker.this.statusMessagePrefix() + "ERROR (See log for details)");
/* 205:    */               
/* 206:319 */               TrainTestSplitMaker.this.m_logger.logMessage("[TrainTestSplitMaker] " + TrainTestSplitMaker.this.statusMessagePrefix() + " problem during split creation. " + ex.getMessage());
/* 207:    */             }
/* 208:323 */             ex.printStackTrace();
/* 209:    */           }
/* 210:    */           finally
/* 211:    */           {
/* 212:325 */             if ((isInterrupted()) && 
/* 213:326 */               (TrainTestSplitMaker.this.m_logger != null))
/* 214:    */             {
/* 215:327 */               TrainTestSplitMaker.this.m_logger.logMessage("[TrainTestSplitMaker] " + TrainTestSplitMaker.this.statusMessagePrefix() + " Split has been canceled!");
/* 216:    */               
/* 217:329 */               TrainTestSplitMaker.this.m_logger.statusMessage(TrainTestSplitMaker.this.statusMessagePrefix() + "INTERRUPTED");
/* 218:    */             }
/* 219:332 */             TrainTestSplitMaker.this.block(false);
/* 220:    */           }
/* 221:    */         }
/* 222:335 */       };
/* 223:336 */       this.m_splitThread.setPriority(1);
/* 224:337 */       this.m_splitThread.start();
/* 225:    */       
/* 226:    */ 
/* 227:340 */       block(true);
/* 228:    */       
/* 229:342 */       this.m_splitThread = null;
/* 230:    */     }
/* 231:    */   }
/* 232:    */   
/* 233:    */   protected void notifyTestSetProduced(TestSetEvent tse)
/* 234:    */   {
/* 235:    */     Vector<TestSetListener> l;
/* 236:354 */     synchronized (this)
/* 237:    */     {
/* 238:355 */       l = (Vector)this.m_testListeners.clone();
/* 239:    */     }
/* 240:357 */     if (l.size() > 0) {
/* 241:358 */       for (int i = 0; i < l.size(); i++)
/* 242:    */       {
/* 243:359 */         if (this.m_splitThread == null) {
/* 244:    */           break;
/* 245:    */         }
/* 246:364 */         ((TestSetListener)l.elementAt(i)).acceptTestSet(tse);
/* 247:    */       }
/* 248:    */     }
/* 249:    */   }
/* 250:    */   
/* 251:    */   protected void notifyTrainingSetProduced(TrainingSetEvent tse)
/* 252:    */   {
/* 253:    */     Vector<TrainingSetListener> l;
/* 254:377 */     synchronized (this)
/* 255:    */     {
/* 256:378 */       l = (Vector)this.m_trainingListeners.clone();
/* 257:    */     }
/* 258:380 */     if (l.size() > 0) {
/* 259:381 */       for (int i = 0; i < l.size(); i++)
/* 260:    */       {
/* 261:382 */         if (this.m_splitThread == null) {
/* 262:    */           break;
/* 263:    */         }
/* 264:387 */         ((TrainingSetListener)l.elementAt(i)).acceptTrainingSet(tse);
/* 265:    */       }
/* 266:    */     }
/* 267:    */   }
/* 268:    */   
/* 269:    */   private synchronized void block(boolean tf)
/* 270:    */   {
/* 271:399 */     if (tf) {
/* 272:    */       try
/* 273:    */       {
/* 274:402 */         if (this.m_splitThread.isAlive()) {
/* 275:403 */           wait();
/* 276:    */         }
/* 277:    */       }
/* 278:    */       catch (InterruptedException ex) {}
/* 279:    */     } else {
/* 280:408 */       notifyAll();
/* 281:    */     }
/* 282:    */   }
/* 283:    */   
/* 284:    */   public void stop()
/* 285:    */   {
/* 286:419 */     if ((this.m_listenee instanceof BeanCommon)) {
/* 287:421 */       ((BeanCommon)this.m_listenee).stop();
/* 288:    */     }
/* 289:425 */     if (this.m_splitThread != null)
/* 290:    */     {
/* 291:426 */       Thread temp = this.m_splitThread;
/* 292:427 */       this.m_splitThread = null;
/* 293:428 */       temp.interrupt();
/* 294:429 */       temp.stop();
/* 295:    */     }
/* 296:    */   }
/* 297:    */   
/* 298:    */   public boolean isBusy()
/* 299:    */   {
/* 300:441 */     return this.m_splitThread != null;
/* 301:    */   }
/* 302:    */   
/* 303:    */   public Enumeration<String> enumerateRequests()
/* 304:    */   {
/* 305:451 */     Vector<String> newVector = new Vector(0);
/* 306:452 */     if (this.m_splitThread != null) {
/* 307:453 */       newVector.addElement("Stop");
/* 308:    */     }
/* 309:455 */     return newVector.elements();
/* 310:    */   }
/* 311:    */   
/* 312:    */   public void performRequest(String request)
/* 313:    */   {
/* 314:466 */     if (request.compareTo("Stop") == 0) {
/* 315:467 */       stop();
/* 316:    */     } else {
/* 317:469 */       throw new IllegalArgumentException(request + " not supported (TrainTestSplitMaker)");
/* 318:    */     }
/* 319:    */   }
/* 320:    */   
/* 321:    */   public boolean eventGeneratable(String eventName)
/* 322:    */   {
/* 323:484 */     if (this.m_listenee == null) {
/* 324:485 */       return false;
/* 325:    */     }
/* 326:488 */     if ((this.m_listenee instanceof EventConstraints))
/* 327:    */     {
/* 328:489 */       if ((((EventConstraints)this.m_listenee).eventGeneratable("dataSet")) || (((EventConstraints)this.m_listenee).eventGeneratable("trainingSet")) || (((EventConstraints)this.m_listenee).eventGeneratable("testSet"))) {
/* 329:492 */         return true;
/* 330:    */       }
/* 331:494 */       return false;
/* 332:    */     }
/* 333:497 */     return true;
/* 334:    */   }
/* 335:    */   
/* 336:    */   private String statusMessagePrefix()
/* 337:    */   {
/* 338:501 */     return getCustomName() + "$" + hashCode() + "|";
/* 339:    */   }
/* 340:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.TrainTestSplitMaker
 * JD-Core Version:    0.7.0.1
 */