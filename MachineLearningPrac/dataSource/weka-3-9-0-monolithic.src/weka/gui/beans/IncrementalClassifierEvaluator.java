/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.LinkedList;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.classifiers.Classifier;
/*   7:    */ import weka.classifiers.Evaluation;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Instance;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.gui.Logger;
/*  12:    */ 
/*  13:    */ public class IncrementalClassifierEvaluator
/*  14:    */   extends AbstractEvaluator
/*  15:    */   implements IncrementalClassifierListener, EventConstraints
/*  16:    */ {
/*  17:    */   private static final long serialVersionUID = -3105419818939541291L;
/*  18:    */   private transient Evaluation m_eval;
/*  19: 45 */   private final Vector<ChartListener> m_listeners = new Vector();
/*  20: 46 */   private final Vector<TextListener> m_textListeners = new Vector();
/*  21: 48 */   private Vector<String> m_dataLegend = new Vector();
/*  22: 50 */   private final ChartEvent m_ce = new ChartEvent(this);
/*  23: 51 */   private double[] m_dataPoint = new double[1];
/*  24: 52 */   private boolean m_reset = false;
/*  25: 54 */   private double m_min = 1.7976931348623157E+308D;
/*  26: 55 */   private double m_max = 4.9E-324D;
/*  27: 58 */   private int m_statusFrequency = 2000;
/*  28: 59 */   private int m_instanceCount = 0;
/*  29: 62 */   private boolean m_outputInfoRetrievalStats = false;
/*  30: 66 */   private int m_windowSize = 0;
/*  31:    */   private Evaluation m_windowEval;
/*  32:    */   private LinkedList<Instance> m_window;
/*  33:    */   private LinkedList<double[]> m_windowedPreds;
/*  34:    */   protected transient StreamThroughput m_throughput;
/*  35:    */   
/*  36:    */   public IncrementalClassifierEvaluator()
/*  37:    */   {
/*  38: 72 */     this.m_visual.loadIcons("weka/gui/beans/icons/IncrementalClassifierEvaluator.gif", "weka/gui/beans/icons/IncrementalClassifierEvaluator_animated.gif");
/*  39:    */     
/*  40:    */ 
/*  41: 75 */     this.m_visual.setText("IncrementalClassifierEvaluator");
/*  42:    */   }
/*  43:    */   
/*  44:    */   public void setCustomName(String name)
/*  45:    */   {
/*  46: 85 */     this.m_visual.setText(name);
/*  47:    */   }
/*  48:    */   
/*  49:    */   public String getCustomName()
/*  50:    */   {
/*  51: 95 */     return this.m_visual.getText();
/*  52:    */   }
/*  53:    */   
/*  54:    */   public String globalInfo()
/*  55:    */   {
/*  56:104 */     return "Evaluate the performance of incrementally trained classifiers.";
/*  57:    */   }
/*  58:    */   
/*  59:    */   public void acceptClassifier(IncrementalClassifierEvent ce)
/*  60:    */   {
/*  61:    */     try
/*  62:    */     {
/*  63:118 */       if (ce.getStatus() == 0)
/*  64:    */       {
/*  65:119 */         this.m_throughput = new StreamThroughput(statusMessagePrefix());
/*  66:120 */         this.m_throughput.setSamplePeriod(this.m_statusFrequency);
/*  67:    */         
/*  68:    */ 
/*  69:123 */         this.m_eval = new Evaluation(ce.getStructure());
/*  70:124 */         this.m_eval.useNoPriors();
/*  71:    */         
/*  72:126 */         this.m_dataLegend = new Vector();
/*  73:127 */         this.m_reset = true;
/*  74:128 */         this.m_dataPoint = new double[0];
/*  75:129 */         ce.getStructure();
/*  76:130 */         System.err.println("NEW BATCH");
/*  77:131 */         this.m_instanceCount = 0;
/*  78:133 */         if (this.m_windowSize > 0)
/*  79:    */         {
/*  80:134 */           this.m_window = new LinkedList();
/*  81:135 */           this.m_windowEval = new Evaluation(ce.getStructure());
/*  82:136 */           this.m_windowEval.useNoPriors();
/*  83:137 */           this.m_windowedPreds = new LinkedList();
/*  84:139 */           if (this.m_logger != null) {
/*  85:140 */             this.m_logger.logMessage(statusMessagePrefix() + "[IncrementalClassifierEvaluator] Chart output using windowed " + "evaluation over " + this.m_windowSize + " instances");
/*  86:    */           }
/*  87:    */         }
/*  88:    */       }
/*  89:    */       else
/*  90:    */       {
/*  91:154 */         Instance inst = ce.getCurrentInstance();
/*  92:155 */         if (inst != null)
/*  93:    */         {
/*  94:156 */           this.m_throughput.updateStart();
/*  95:157 */           this.m_instanceCount += 1;
/*  96:    */           
/*  97:159 */           double[] dist = ce.getClassifier().distributionForInstance(inst);
/*  98:160 */           double pred = 0.0D;
/*  99:161 */           if (!inst.isMissing(inst.classIndex()))
/* 100:    */           {
/* 101:162 */             if (this.m_outputInfoRetrievalStats) {
/* 102:164 */               this.m_eval.evaluateModelOnceAndRecordPrediction(dist, inst);
/* 103:    */             } else {
/* 104:166 */               this.m_eval.evaluateModelOnce(dist, inst);
/* 105:    */             }
/* 106:169 */             if (this.m_windowSize > 0)
/* 107:    */             {
/* 108:171 */               this.m_windowEval.evaluateModelOnce(dist, inst);
/* 109:172 */               this.m_window.addFirst(inst);
/* 110:173 */               this.m_windowedPreds.addFirst(dist);
/* 111:175 */               if (this.m_instanceCount > this.m_windowSize)
/* 112:    */               {
/* 113:177 */                 Instance oldest = (Instance)this.m_window.removeLast();
/* 114:    */                 
/* 115:179 */                 double[] oldDist = (double[])this.m_windowedPreds.removeLast();
/* 116:180 */                 oldest.setWeight(-oldest.weight());
/* 117:181 */                 this.m_windowEval.evaluateModelOnce(oldDist, oldest);
/* 118:182 */                 oldest.setWeight(-oldest.weight());
/* 119:    */               }
/* 120:    */             }
/* 121:    */           }
/* 122:    */           else
/* 123:    */           {
/* 124:186 */             pred = ce.getClassifier().classifyInstance(inst);
/* 125:    */           }
/* 126:188 */           if (inst.classIndex() >= 0)
/* 127:    */           {
/* 128:190 */             if (inst.attribute(inst.classIndex()).isNominal())
/* 129:    */             {
/* 130:191 */               if (!inst.isMissing(inst.classIndex()))
/* 131:    */               {
/* 132:192 */                 if (this.m_dataPoint.length < 2)
/* 133:    */                 {
/* 134:193 */                   this.m_dataPoint = new double[3];
/* 135:194 */                   this.m_dataLegend.addElement("Accuracy");
/* 136:195 */                   this.m_dataLegend.addElement("RMSE (prob)");
/* 137:196 */                   this.m_dataLegend.addElement("Kappa");
/* 138:    */                 }
/* 139:200 */                 if (this.m_windowSize > 0)
/* 140:    */                 {
/* 141:201 */                   this.m_dataPoint[1] = this.m_windowEval.rootMeanSquaredError();
/* 142:202 */                   this.m_dataPoint[2] = this.m_windowEval.kappa();
/* 143:    */                 }
/* 144:    */                 else
/* 145:    */                 {
/* 146:204 */                   this.m_dataPoint[1] = this.m_eval.rootMeanSquaredError();
/* 147:205 */                   this.m_dataPoint[2] = this.m_eval.kappa();
/* 148:    */                 }
/* 149:    */               }
/* 150:214 */               else if (this.m_dataPoint.length < 1)
/* 151:    */               {
/* 152:215 */                 this.m_dataPoint = new double[1];
/* 153:216 */                 this.m_dataLegend.addElement("Confidence");
/* 154:    */               }
/* 155:219 */               double primaryMeasure = 0.0D;
/* 156:220 */               if (!inst.isMissing(inst.classIndex()))
/* 157:    */               {
/* 158:221 */                 if (this.m_windowSize > 0) {
/* 159:222 */                   primaryMeasure = 1.0D - this.m_windowEval.errorRate();
/* 160:    */                 } else {
/* 161:224 */                   primaryMeasure = 1.0D - this.m_eval.errorRate();
/* 162:    */                 }
/* 163:    */               }
/* 164:    */               else {
/* 165:231 */                 primaryMeasure = dist[weka.core.Utils.maxIndex(dist)];
/* 166:    */               }
/* 167:234 */               this.m_dataPoint[0] = primaryMeasure;
/* 168:    */               
/* 169:    */ 
/* 170:    */ 
/* 171:    */ 
/* 172:    */ 
/* 173:    */ 
/* 174:    */ 
/* 175:242 */               this.m_ce.setLegendText(this.m_dataLegend);
/* 176:243 */               this.m_ce.setMin(0.0D);
/* 177:244 */               this.m_ce.setMax(1.0D);
/* 178:245 */               this.m_ce.setDataPoint(this.m_dataPoint);
/* 179:246 */               this.m_ce.setReset(this.m_reset);
/* 180:247 */               this.m_reset = false;
/* 181:    */             }
/* 182:    */             else
/* 183:    */             {
/* 184:250 */               if (this.m_dataPoint.length < 1)
/* 185:    */               {
/* 186:251 */                 this.m_dataPoint = new double[1];
/* 187:252 */                 if (inst.isMissing(inst.classIndex())) {
/* 188:253 */                   this.m_dataLegend.addElement("Prediction");
/* 189:    */                 } else {
/* 190:255 */                   this.m_dataLegend.addElement("RMSE");
/* 191:    */                 }
/* 192:    */               }
/* 193:258 */               if (!inst.isMissing(inst.classIndex()))
/* 194:    */               {
/* 195:    */                 double update;
/* 196:    */                 double update;
/* 197:260 */                 if (!inst.isMissing(inst.classIndex()))
/* 198:    */                 {
/* 199:    */                   double update;
/* 200:261 */                   if (this.m_windowSize > 0) {
/* 201:262 */                     update = this.m_windowEval.rootMeanSquaredError();
/* 202:    */                   } else {
/* 203:264 */                     update = this.m_eval.rootMeanSquaredError();
/* 204:    */                   }
/* 205:    */                 }
/* 206:    */                 else
/* 207:    */                 {
/* 208:267 */                   update = pred;
/* 209:    */                 }
/* 210:269 */                 this.m_dataPoint[0] = update;
/* 211:270 */                 if (update > this.m_max) {
/* 212:271 */                   this.m_max = update;
/* 213:    */                 }
/* 214:273 */                 if (update < this.m_min) {
/* 215:274 */                   this.m_min = update;
/* 216:    */                 }
/* 217:    */               }
/* 218:278 */               this.m_ce.setLegendText(this.m_dataLegend);
/* 219:279 */               this.m_ce.setMin(inst.isMissing(inst.classIndex()) ? this.m_min : 0.0D);
/* 220:280 */               this.m_ce.setMax(this.m_max);
/* 221:281 */               this.m_ce.setDataPoint(this.m_dataPoint);
/* 222:282 */               this.m_ce.setReset(this.m_reset);
/* 223:283 */               this.m_reset = false;
/* 224:    */             }
/* 225:285 */             notifyChartListeners(this.m_ce);
/* 226:    */           }
/* 227:287 */           this.m_throughput.updateEnd(this.m_logger);
/* 228:    */         }
/* 229:290 */         if ((ce.getStatus() == 2) || (inst == null))
/* 230:    */         {
/* 231:292 */           if (this.m_logger != null) {
/* 232:293 */             this.m_logger.logMessage("[IncrementalClassifierEvaluator]" + statusMessagePrefix() + " Finished processing.");
/* 233:    */           }
/* 234:296 */           this.m_throughput.finished(this.m_logger);
/* 235:    */           
/* 236:    */ 
/* 237:299 */           this.m_windowEval = null;
/* 238:300 */           this.m_window = null;
/* 239:301 */           this.m_windowedPreds = null;
/* 240:303 */           if (this.m_textListeners.size() > 0)
/* 241:    */           {
/* 242:304 */             String textTitle = ce.getClassifier().getClass().getName();
/* 243:305 */             textTitle = textTitle.substring(textTitle.lastIndexOf('.') + 1, textTitle.length());
/* 244:    */             
/* 245:307 */             String results = "=== Performance information ===\n\nScheme:   " + textTitle + "\n" + "Relation: " + this.m_eval.getHeader().relationName() + "\n\n" + this.m_eval.toSummaryString();
/* 246:311 */             if ((this.m_eval.getHeader().classIndex() >= 0) && (this.m_eval.getHeader().classAttribute().isNominal()) && (this.m_outputInfoRetrievalStats)) {
/* 247:314 */               results = results + "\n" + this.m_eval.toClassDetailsString();
/* 248:    */             }
/* 249:317 */             if ((this.m_eval.getHeader().classIndex() >= 0) && (this.m_eval.getHeader().classAttribute().isNominal())) {
/* 250:319 */               results = results + "\n" + this.m_eval.toMatrixString();
/* 251:    */             }
/* 252:321 */             textTitle = "Results: " + textTitle;
/* 253:322 */             TextEvent te = new TextEvent(this, results, textTitle);
/* 254:323 */             notifyTextListeners(te);
/* 255:    */           }
/* 256:    */         }
/* 257:    */       }
/* 258:    */     }
/* 259:    */     catch (Exception ex)
/* 260:    */     {
/* 261:328 */       if (this.m_logger != null)
/* 262:    */       {
/* 263:329 */         this.m_logger.logMessage("[IncrementalClassifierEvaluator]" + statusMessagePrefix() + " Error processing prediction " + ex.getMessage());
/* 264:    */         
/* 265:    */ 
/* 266:332 */         this.m_logger.statusMessage(statusMessagePrefix() + "ERROR: problem processing prediction (see log for details)");
/* 267:    */       }
/* 268:335 */       ex.printStackTrace();
/* 269:336 */       stop();
/* 270:    */     }
/* 271:    */   }
/* 272:    */   
/* 273:    */   public boolean eventGeneratable(String eventName)
/* 274:    */   {
/* 275:350 */     if (this.m_listenee == null) {
/* 276:351 */       return false;
/* 277:    */     }
/* 278:354 */     if (((this.m_listenee instanceof EventConstraints)) && 
/* 279:355 */       (!((EventConstraints)this.m_listenee).eventGeneratable("incrementalClassifier"))) {
/* 280:357 */       return false;
/* 281:    */     }
/* 282:360 */     return true;
/* 283:    */   }
/* 284:    */   
/* 285:    */   public void stop()
/* 286:    */   {
/* 287:369 */     if ((this.m_listenee instanceof BeanCommon)) {
/* 288:371 */       ((BeanCommon)this.m_listenee).stop();
/* 289:    */     }
/* 290:    */   }
/* 291:    */   
/* 292:    */   public boolean isBusy()
/* 293:    */   {
/* 294:383 */     return false;
/* 295:    */   }
/* 296:    */   
/* 297:    */   private void notifyChartListeners(ChartEvent ce)
/* 298:    */   {
/* 299:    */     Vector<ChartListener> l;
/* 300:389 */     synchronized (this)
/* 301:    */     {
/* 302:390 */       l = (Vector)this.m_listeners.clone();
/* 303:    */     }
/* 304:392 */     if (l.size() > 0) {
/* 305:393 */       for (int i = 0; i < l.size(); i++) {
/* 306:394 */         ((ChartListener)l.elementAt(i)).acceptDataPoint(ce);
/* 307:    */       }
/* 308:    */     }
/* 309:    */   }
/* 310:    */   
/* 311:    */   private void notifyTextListeners(TextEvent te)
/* 312:    */   {
/* 313:    */     Vector<TextListener> l;
/* 314:407 */     synchronized (this)
/* 315:    */     {
/* 316:408 */       l = (Vector)this.m_textListeners.clone();
/* 317:    */     }
/* 318:410 */     if (l.size() > 0) {
/* 319:411 */       for (int i = 0; i < l.size(); i++) {
/* 320:414 */         ((TextListener)l.elementAt(i)).acceptText(te);
/* 321:    */       }
/* 322:    */     }
/* 323:    */   }
/* 324:    */   
/* 325:    */   public void setStatusFrequency(int s)
/* 326:    */   {
/* 327:425 */     this.m_statusFrequency = s;
/* 328:    */   }
/* 329:    */   
/* 330:    */   public int getStatusFrequency()
/* 331:    */   {
/* 332:434 */     return this.m_statusFrequency;
/* 333:    */   }
/* 334:    */   
/* 335:    */   public String statusFrequencyTipText()
/* 336:    */   {
/* 337:443 */     return "How often to report progress to the status bar.";
/* 338:    */   }
/* 339:    */   
/* 340:    */   public void setOutputPerClassInfoRetrievalStats(boolean i)
/* 341:    */   {
/* 342:453 */     this.m_outputInfoRetrievalStats = i;
/* 343:    */   }
/* 344:    */   
/* 345:    */   public boolean getOutputPerClassInfoRetrievalStats()
/* 346:    */   {
/* 347:462 */     return this.m_outputInfoRetrievalStats;
/* 348:    */   }
/* 349:    */   
/* 350:    */   public String outputPerClassInfoRetrievalStatsTipText()
/* 351:    */   {
/* 352:471 */     return "Output per-class info retrieval stats. If set to true, predictions get stored so that stats such as AUC can be computed. Note: this consumes some memory.";
/* 353:    */   }
/* 354:    */   
/* 355:    */   public void setChartingEvalWindowSize(int windowSize)
/* 356:    */   {
/* 357:484 */     this.m_windowSize = windowSize;
/* 358:    */   }
/* 359:    */   
/* 360:    */   public int getChartingEvalWindowSize()
/* 361:    */   {
/* 362:496 */     return this.m_windowSize;
/* 363:    */   }
/* 364:    */   
/* 365:    */   public String chartingEvalWindowSizeTipText()
/* 366:    */   {
/* 367:505 */     return "For charting only, specify a sliding window size over which to compute performance stats. <= 0 means eval on whole stream";
/* 368:    */   }
/* 369:    */   
/* 370:    */   public synchronized void addChartListener(ChartListener cl)
/* 371:    */   {
/* 372:515 */     this.m_listeners.addElement(cl);
/* 373:    */   }
/* 374:    */   
/* 375:    */   public synchronized void removeChartListener(ChartListener cl)
/* 376:    */   {
/* 377:524 */     this.m_listeners.remove(cl);
/* 378:    */   }
/* 379:    */   
/* 380:    */   public synchronized void addTextListener(TextListener cl)
/* 381:    */   {
/* 382:533 */     this.m_textListeners.addElement(cl);
/* 383:    */   }
/* 384:    */   
/* 385:    */   public synchronized void removeTextListener(TextListener cl)
/* 386:    */   {
/* 387:542 */     this.m_textListeners.remove(cl);
/* 388:    */   }
/* 389:    */   
/* 390:    */   private String statusMessagePrefix()
/* 391:    */   {
/* 392:546 */     return getCustomName() + "$" + hashCode() + "|";
/* 393:    */   }
/* 394:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.IncrementalClassifierEvaluator
 * JD-Core Version:    0.7.0.1
 */