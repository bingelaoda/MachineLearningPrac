/*   1:    */ package weka.classifiers.trees;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import weka.classifiers.AbstractClassifier;
/*   5:    */ import weka.classifiers.Classifier;
/*   6:    */ import weka.classifiers.Sourcable;
/*   7:    */ import weka.classifiers.rules.ZeroR;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Capabilities;
/*  10:    */ import weka.core.Capabilities.Capability;
/*  11:    */ import weka.core.ContingencyTables;
/*  12:    */ import weka.core.Instance;
/*  13:    */ import weka.core.Instances;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.Utils;
/*  16:    */ import weka.core.WeightedInstancesHandler;
/*  17:    */ 
/*  18:    */ public class DecisionStump
/*  19:    */   extends AbstractClassifier
/*  20:    */   implements WeightedInstancesHandler, Sourcable
/*  21:    */ {
/*  22:    */   static final long serialVersionUID = 1618384535950391L;
/*  23:    */   protected int m_AttIndex;
/*  24:    */   protected double m_SplitPoint;
/*  25:    */   protected double[][] m_Distribution;
/*  26:    */   protected Instances m_Instances;
/*  27:    */   protected Classifier m_ZeroR;
/*  28:    */   
/*  29:    */   public String globalInfo()
/*  30:    */   {
/*  31: 88 */     return "Class for building and using a decision stump. Usually used in conjunction with a boosting algorithm. Does regression (based on mean-squared error) or classification (based on entropy). Missing is treated as a separate value.";
/*  32:    */   }
/*  33:    */   
/*  34:    */   public Capabilities getCapabilities()
/*  35:    */   {
/*  36:100 */     Capabilities result = super.getCapabilities();
/*  37:101 */     result.disableAll();
/*  38:    */     
/*  39:    */ 
/*  40:104 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  41:105 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  42:106 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  43:107 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  44:    */     
/*  45:    */ 
/*  46:110 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  47:111 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*  48:112 */     result.enable(Capabilities.Capability.DATE_CLASS);
/*  49:113 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  50:    */     
/*  51:115 */     return result;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public void buildClassifier(Instances instances)
/*  55:    */     throws Exception
/*  56:    */   {
/*  57:126 */     double bestVal = 1.7976931348623157E+308D;
/*  58:127 */     double bestPoint = -1.797693134862316E+308D;
/*  59:128 */     int bestAtt = -1;
/*  60:    */     
/*  61:    */ 
/*  62:131 */     getCapabilities().testWithFail(instances);
/*  63:    */     
/*  64:    */ 
/*  65:134 */     instances = new Instances(instances);
/*  66:135 */     instances.deleteWithMissingClass();
/*  67:138 */     if (instances.numAttributes() == 1)
/*  68:    */     {
/*  69:139 */       System.err.println("Cannot build model (only class attribute present in data!), using ZeroR model instead!");
/*  70:    */       
/*  71:    */ 
/*  72:142 */       this.m_ZeroR = new ZeroR();
/*  73:143 */       this.m_ZeroR.buildClassifier(instances);
/*  74:144 */       return;
/*  75:    */     }
/*  76:147 */     this.m_ZeroR = null;
/*  77:    */     
/*  78:    */ 
/*  79:150 */     double[][] bestDist = new double[3][instances.numClasses()];
/*  80:    */     
/*  81:152 */     this.m_Instances = new Instances(instances);
/*  82:    */     int numClasses;
/*  83:    */     int numClasses;
/*  84:154 */     if (this.m_Instances.classAttribute().isNominal()) {
/*  85:155 */       numClasses = this.m_Instances.numClasses();
/*  86:    */     } else {
/*  87:157 */       numClasses = 1;
/*  88:    */     }
/*  89:161 */     boolean first = true;
/*  90:162 */     for (int i = 0; i < this.m_Instances.numAttributes(); i++) {
/*  91:163 */       if (i != this.m_Instances.classIndex())
/*  92:    */       {
/*  93:166 */         this.m_Distribution = new double[3][numClasses];
/*  94:    */         double currVal;
/*  95:    */         double currVal;
/*  96:169 */         if (this.m_Instances.attribute(i).isNominal()) {
/*  97:170 */           currVal = findSplitNominal(i);
/*  98:    */         } else {
/*  99:172 */           currVal = findSplitNumeric(i);
/* 100:    */         }
/* 101:174 */         if ((first) || (currVal < bestVal))
/* 102:    */         {
/* 103:175 */           bestVal = currVal;
/* 104:176 */           bestAtt = i;
/* 105:177 */           bestPoint = this.m_SplitPoint;
/* 106:178 */           for (int j = 0; j < 3; j++) {
/* 107:179 */             System.arraycopy(this.m_Distribution[j], 0, bestDist[j], 0, numClasses);
/* 108:    */           }
/* 109:    */         }
/* 110:185 */         first = false;
/* 111:    */       }
/* 112:    */     }
/* 113:190 */     this.m_AttIndex = bestAtt;
/* 114:191 */     this.m_SplitPoint = bestPoint;
/* 115:192 */     this.m_Distribution = bestDist;
/* 116:193 */     if (this.m_Instances.classAttribute().isNominal()) {
/* 117:194 */       for (int i = 0; i < this.m_Distribution.length; i++)
/* 118:    */       {
/* 119:195 */         double sumCounts = Utils.sum(this.m_Distribution[i]);
/* 120:196 */         if (sumCounts == 0.0D)
/* 121:    */         {
/* 122:197 */           System.arraycopy(this.m_Distribution[2], 0, this.m_Distribution[i], 0, this.m_Distribution[2].length);
/* 123:    */           
/* 124:199 */           Utils.normalize(this.m_Distribution[i]);
/* 125:    */         }
/* 126:    */         else
/* 127:    */         {
/* 128:201 */           Utils.normalize(this.m_Distribution[i], sumCounts);
/* 129:    */         }
/* 130:    */       }
/* 131:    */     }
/* 132:207 */     this.m_Instances = new Instances(this.m_Instances, 0);
/* 133:    */   }
/* 134:    */   
/* 135:    */   public double[] distributionForInstance(Instance instance)
/* 136:    */     throws Exception
/* 137:    */   {
/* 138:220 */     if (this.m_ZeroR != null) {
/* 139:221 */       return this.m_ZeroR.distributionForInstance(instance);
/* 140:    */     }
/* 141:224 */     return this.m_Distribution[whichSubset(instance)];
/* 142:    */   }
/* 143:    */   
/* 144:    */   public String toSource(String className)
/* 145:    */     throws Exception
/* 146:    */   {
/* 147:236 */     StringBuffer text = new StringBuffer("class ");
/* 148:237 */     Attribute c = this.m_Instances.classAttribute();
/* 149:238 */     text.append(className).append(" {\n  public static double classify(Object[] i) {\n");
/* 150:    */     
/* 151:    */ 
/* 152:241 */     text.append("    /* " + this.m_Instances.attribute(this.m_AttIndex).name() + " */\n");
/* 153:242 */     text.append("    if (i[").append(this.m_AttIndex);
/* 154:243 */     text.append("] == null) { return ");
/* 155:244 */     text.append(sourceClass(c, this.m_Distribution[2])).append(";");
/* 156:245 */     if (this.m_Instances.attribute(this.m_AttIndex).isNominal())
/* 157:    */     {
/* 158:246 */       text.append(" } else if (((String)i[").append(this.m_AttIndex);
/* 159:247 */       text.append("]).equals(\"");
/* 160:248 */       text.append(this.m_Instances.attribute(this.m_AttIndex).value((int)this.m_SplitPoint));
/* 161:249 */       text.append("\")");
/* 162:    */     }
/* 163:    */     else
/* 164:    */     {
/* 165:251 */       text.append(" } else if (((Double)i[").append(this.m_AttIndex);
/* 166:252 */       text.append("]).doubleValue() <= ").append(this.m_SplitPoint);
/* 167:    */     }
/* 168:254 */     text.append(") { return ");
/* 169:255 */     text.append(sourceClass(c, this.m_Distribution[0])).append(";");
/* 170:256 */     text.append(" } else { return ");
/* 171:257 */     text.append(sourceClass(c, this.m_Distribution[1])).append(";");
/* 172:258 */     text.append(" }\n  }\n}\n");
/* 173:259 */     return text.toString();
/* 174:    */   }
/* 175:    */   
/* 176:    */   protected String sourceClass(Attribute c, double[] dist)
/* 177:    */   {
/* 178:271 */     if (c.isNominal()) {
/* 179:272 */       return Integer.toString(Utils.maxIndex(dist));
/* 180:    */     }
/* 181:274 */     return Double.toString(dist[0]);
/* 182:    */   }
/* 183:    */   
/* 184:    */   public String toString()
/* 185:    */   {
/* 186:286 */     if (this.m_ZeroR != null)
/* 187:    */     {
/* 188:287 */       StringBuffer buf = new StringBuffer();
/* 189:288 */       buf.append(getClass().getName().replaceAll(".*\\.", "") + "\n");
/* 190:289 */       buf.append(getClass().getName().replaceAll(".*\\.", "").replaceAll(".", "=") + "\n\n");
/* 191:290 */       buf.append("Warning: No model could be built, hence ZeroR model is used:\n\n");
/* 192:291 */       buf.append(this.m_ZeroR.toString());
/* 193:292 */       return buf.toString();
/* 194:    */     }
/* 195:295 */     if (this.m_Instances == null) {
/* 196:296 */       return "Decision Stump: No model built yet.";
/* 197:    */     }
/* 198:    */     try
/* 199:    */     {
/* 200:299 */       StringBuffer text = new StringBuffer();
/* 201:    */       
/* 202:301 */       text.append("Decision Stump\n\n");
/* 203:302 */       text.append("Classifications\n\n");
/* 204:303 */       Attribute att = this.m_Instances.attribute(this.m_AttIndex);
/* 205:304 */       if (att.isNominal())
/* 206:    */       {
/* 207:305 */         text.append(att.name() + " = " + att.value((int)this.m_SplitPoint) + " : ");
/* 208:    */         
/* 209:307 */         text.append(printClass(this.m_Distribution[0]));
/* 210:308 */         text.append(att.name() + " != " + att.value((int)this.m_SplitPoint) + " : ");
/* 211:    */         
/* 212:310 */         text.append(printClass(this.m_Distribution[1]));
/* 213:    */       }
/* 214:    */       else
/* 215:    */       {
/* 216:312 */         text.append(att.name() + " <= " + this.m_SplitPoint + " : ");
/* 217:313 */         text.append(printClass(this.m_Distribution[0]));
/* 218:314 */         text.append(att.name() + " > " + this.m_SplitPoint + " : ");
/* 219:315 */         text.append(printClass(this.m_Distribution[1]));
/* 220:    */       }
/* 221:317 */       text.append(att.name() + " is missing : ");
/* 222:318 */       text.append(printClass(this.m_Distribution[2]));
/* 223:320 */       if (this.m_Instances.classAttribute().isNominal())
/* 224:    */       {
/* 225:321 */         text.append("\nClass distributions\n\n");
/* 226:322 */         if (att.isNominal())
/* 227:    */         {
/* 228:323 */           text.append(att.name() + " = " + att.value((int)this.m_SplitPoint) + "\n");
/* 229:    */           
/* 230:325 */           text.append(printDist(this.m_Distribution[0]));
/* 231:326 */           text.append(att.name() + " != " + att.value((int)this.m_SplitPoint) + "\n");
/* 232:    */           
/* 233:328 */           text.append(printDist(this.m_Distribution[1]));
/* 234:    */         }
/* 235:    */         else
/* 236:    */         {
/* 237:330 */           text.append(att.name() + " <= " + this.m_SplitPoint + "\n");
/* 238:331 */           text.append(printDist(this.m_Distribution[0]));
/* 239:332 */           text.append(att.name() + " > " + this.m_SplitPoint + "\n");
/* 240:333 */           text.append(printDist(this.m_Distribution[1]));
/* 241:    */         }
/* 242:335 */         text.append(att.name() + " is missing\n");
/* 243:336 */         text.append(printDist(this.m_Distribution[2]));
/* 244:    */       }
/* 245:339 */       return text.toString();
/* 246:    */     }
/* 247:    */     catch (Exception e) {}
/* 248:341 */     return "Can't print decision stump classifier!";
/* 249:    */   }
/* 250:    */   
/* 251:    */   protected String printDist(double[] dist)
/* 252:    */     throws Exception
/* 253:    */   {
/* 254:354 */     StringBuffer text = new StringBuffer();
/* 255:356 */     if (this.m_Instances.classAttribute().isNominal())
/* 256:    */     {
/* 257:357 */       for (int i = 0; i < this.m_Instances.numClasses(); i++) {
/* 258:358 */         text.append(this.m_Instances.classAttribute().value(i) + "\t");
/* 259:    */       }
/* 260:360 */       text.append("\n");
/* 261:361 */       for (int i = 0; i < this.m_Instances.numClasses(); i++) {
/* 262:362 */         text.append(dist[i] + "\t");
/* 263:    */       }
/* 264:364 */       text.append("\n");
/* 265:    */     }
/* 266:367 */     return text.toString();
/* 267:    */   }
/* 268:    */   
/* 269:    */   protected String printClass(double[] dist)
/* 270:    */     throws Exception
/* 271:    */   {
/* 272:379 */     StringBuffer text = new StringBuffer();
/* 273:381 */     if (this.m_Instances.classAttribute().isNominal()) {
/* 274:382 */       text.append(this.m_Instances.classAttribute().value(Utils.maxIndex(dist)));
/* 275:    */     } else {
/* 276:384 */       text.append(dist[0]);
/* 277:    */     }
/* 278:387 */     return text.toString() + "\n";
/* 279:    */   }
/* 280:    */   
/* 281:    */   protected double findSplitNominal(int index)
/* 282:    */     throws Exception
/* 283:    */   {
/* 284:399 */     if (this.m_Instances.classAttribute().isNominal()) {
/* 285:400 */       return findSplitNominalNominal(index);
/* 286:    */     }
/* 287:402 */     return findSplitNominalNumeric(index);
/* 288:    */   }
/* 289:    */   
/* 290:    */   protected double findSplitNominalNominal(int index)
/* 291:    */     throws Exception
/* 292:    */   {
/* 293:416 */     double bestVal = 1.7976931348623157E+308D;
/* 294:417 */     double[][] counts = new double[this.m_Instances.attribute(index).numValues() + 1][this.m_Instances.numClasses()];
/* 295:    */     
/* 296:419 */     double[] sumCounts = new double[this.m_Instances.numClasses()];
/* 297:420 */     double[][] bestDist = new double[3][this.m_Instances.numClasses()];
/* 298:421 */     int numMissing = 0;
/* 299:424 */     for (int i = 0; i < this.m_Instances.numInstances(); i++)
/* 300:    */     {
/* 301:425 */       Instance inst = this.m_Instances.instance(i);
/* 302:426 */       if (inst.isMissing(index))
/* 303:    */       {
/* 304:427 */         numMissing++;
/* 305:428 */         counts[this.m_Instances.attribute(index).numValues()][((int)inst.classValue())] += inst.weight();
/* 306:    */       }
/* 307:    */       else
/* 308:    */       {
/* 309:431 */         counts[((int)inst.value(index))][((int)inst.classValue())] += inst.weight();
/* 310:    */       }
/* 311:    */     }
/* 312:437 */     for (int i = 0; i < this.m_Instances.attribute(index).numValues(); i++) {
/* 313:438 */       for (int j = 0; j < this.m_Instances.numClasses(); j++) {
/* 314:439 */         sumCounts[j] += counts[i][j];
/* 315:    */       }
/* 316:    */     }
/* 317:444 */     System.arraycopy(counts[this.m_Instances.attribute(index).numValues()], 0, this.m_Distribution[2], 0, this.m_Instances.numClasses());
/* 318:446 */     for (int i = 0; i < this.m_Instances.attribute(index).numValues(); i++)
/* 319:    */     {
/* 320:447 */       for (int j = 0; j < this.m_Instances.numClasses(); j++)
/* 321:    */       {
/* 322:448 */         this.m_Distribution[0][j] = counts[i][j];
/* 323:449 */         this.m_Distribution[1][j] = (sumCounts[j] - counts[i][j]);
/* 324:    */       }
/* 325:451 */       double currVal = ContingencyTables.entropyConditionedOnRows(this.m_Distribution);
/* 326:452 */       if (currVal < bestVal)
/* 327:    */       {
/* 328:453 */         bestVal = currVal;
/* 329:454 */         this.m_SplitPoint = i;
/* 330:455 */         for (int j = 0; j < 3; j++) {
/* 331:456 */           System.arraycopy(this.m_Distribution[j], 0, bestDist[j], 0, this.m_Instances.numClasses());
/* 332:    */         }
/* 333:    */       }
/* 334:    */     }
/* 335:463 */     if (numMissing == 0) {
/* 336:464 */       System.arraycopy(sumCounts, 0, bestDist[2], 0, this.m_Instances.numClasses());
/* 337:    */     }
/* 338:468 */     this.m_Distribution = bestDist;
/* 339:469 */     return bestVal;
/* 340:    */   }
/* 341:    */   
/* 342:    */   protected double findSplitNominalNumeric(int index)
/* 343:    */     throws Exception
/* 344:    */   {
/* 345:482 */     double bestVal = 1.7976931348623157E+308D;
/* 346:483 */     double[] sumsSquaresPerValue = new double[this.m_Instances.attribute(index).numValues()];
/* 347:    */     
/* 348:485 */     double[] sumsPerValue = new double[this.m_Instances.attribute(index).numValues()];
/* 349:486 */     double[] weightsPerValue = new double[this.m_Instances.attribute(index).numValues()];
/* 350:487 */     double totalSumSquaresW = 0.0D;double totalSumW = 0.0D;double totalSumOfWeightsW = 0.0D;
/* 351:488 */     double totalSumOfWeights = 0.0D;double totalSum = 0.0D;
/* 352:489 */     double[] sumsSquares = new double[3];double[] sumOfWeights = new double[3];
/* 353:490 */     double[][] bestDist = new double[3][1];
/* 354:493 */     for (int i = 0; i < this.m_Instances.numInstances(); i++)
/* 355:    */     {
/* 356:494 */       Instance inst = this.m_Instances.instance(i);
/* 357:495 */       if (inst.isMissing(index))
/* 358:    */       {
/* 359:496 */         this.m_Distribution[2][0] += inst.classValue() * inst.weight();
/* 360:497 */         sumsSquares[2] += inst.classValue() * inst.classValue() * inst.weight();
/* 361:    */         
/* 362:499 */         sumOfWeights[2] += inst.weight();
/* 363:    */       }
/* 364:    */       else
/* 365:    */       {
/* 366:501 */         weightsPerValue[((int)inst.value(index))] += inst.weight();
/* 367:502 */         sumsPerValue[((int)inst.value(index))] += inst.classValue() * inst.weight();
/* 368:    */         
/* 369:504 */         sumsSquaresPerValue[((int)inst.value(index))] += inst.classValue() * inst.classValue() * inst.weight();
/* 370:    */       }
/* 371:507 */       totalSumOfWeights += inst.weight();
/* 372:508 */       totalSum += inst.classValue() * inst.weight();
/* 373:    */     }
/* 374:512 */     if (totalSumOfWeights <= 0.0D) {
/* 375:513 */       return bestVal;
/* 376:    */     }
/* 377:517 */     for (int i = 0; i < this.m_Instances.attribute(index).numValues(); i++)
/* 378:    */     {
/* 379:518 */       totalSumOfWeightsW += weightsPerValue[i];
/* 380:519 */       totalSumSquaresW += sumsSquaresPerValue[i];
/* 381:520 */       totalSumW += sumsPerValue[i];
/* 382:    */     }
/* 383:524 */     for (int i = 0; i < this.m_Instances.attribute(index).numValues(); i++)
/* 384:    */     {
/* 385:526 */       this.m_Distribution[0][0] = sumsPerValue[i];
/* 386:527 */       sumsSquares[0] = sumsSquaresPerValue[i];
/* 387:528 */       sumOfWeights[0] = weightsPerValue[i];
/* 388:529 */       this.m_Distribution[1][0] = (totalSumW - sumsPerValue[i]);
/* 389:530 */       sumsSquares[1] = (totalSumSquaresW - sumsSquaresPerValue[i]);
/* 390:531 */       sumOfWeights[1] = (totalSumOfWeightsW - weightsPerValue[i]);
/* 391:    */       
/* 392:533 */       double currVal = variance(this.m_Distribution, sumsSquares, sumOfWeights);
/* 393:535 */       if (currVal < bestVal)
/* 394:    */       {
/* 395:536 */         bestVal = currVal;
/* 396:537 */         this.m_SplitPoint = i;
/* 397:538 */         for (int j = 0; j < 3; j++) {
/* 398:539 */           if (sumOfWeights[j] > 0.0D) {
/* 399:540 */             bestDist[j][0] = (this.m_Distribution[j][0] / sumOfWeights[j]);
/* 400:    */           } else {
/* 401:542 */             bestDist[j][0] = (totalSum / totalSumOfWeights);
/* 402:    */           }
/* 403:    */         }
/* 404:    */       }
/* 405:    */     }
/* 406:548 */     this.m_Distribution = bestDist;
/* 407:549 */     return bestVal;
/* 408:    */   }
/* 409:    */   
/* 410:    */   protected double findSplitNumeric(int index)
/* 411:    */     throws Exception
/* 412:    */   {
/* 413:561 */     if (this.m_Instances.classAttribute().isNominal()) {
/* 414:562 */       return findSplitNumericNominal(index);
/* 415:    */     }
/* 416:564 */     return findSplitNumericNumeric(index);
/* 417:    */   }
/* 418:    */   
/* 419:    */   protected double findSplitNumericNominal(int index)
/* 420:    */     throws Exception
/* 421:    */   {
/* 422:578 */     double bestVal = 1.7976931348623157E+308D;
/* 423:579 */     int numMissing = 0;
/* 424:580 */     double[] sum = new double[this.m_Instances.numClasses()];
/* 425:581 */     double[][] bestDist = new double[3][this.m_Instances.numClasses()];
/* 426:584 */     for (int i = 0; i < this.m_Instances.numInstances(); i++)
/* 427:    */     {
/* 428:585 */       Instance inst = this.m_Instances.instance(i);
/* 429:586 */       if (!inst.isMissing(index))
/* 430:    */       {
/* 431:587 */         this.m_Distribution[1][((int)inst.classValue())] += inst.weight();
/* 432:    */       }
/* 433:    */       else
/* 434:    */       {
/* 435:589 */         this.m_Distribution[2][((int)inst.classValue())] += inst.weight();
/* 436:590 */         numMissing++;
/* 437:    */       }
/* 438:    */     }
/* 439:593 */     System.arraycopy(this.m_Distribution[1], 0, sum, 0, this.m_Instances.numClasses());
/* 440:596 */     for (int j = 0; j < 3; j++) {
/* 441:597 */       System.arraycopy(this.m_Distribution[j], 0, bestDist[j], 0, this.m_Instances.numClasses());
/* 442:    */     }
/* 443:602 */     this.m_Instances.sort(index);
/* 444:605 */     for (int i = 0; i < this.m_Instances.numInstances() - (numMissing + 1); i++)
/* 445:    */     {
/* 446:606 */       Instance inst = this.m_Instances.instance(i);
/* 447:607 */       Instance instPlusOne = this.m_Instances.instance(i + 1);
/* 448:608 */       this.m_Distribution[0][((int)inst.classValue())] += inst.weight();
/* 449:609 */       this.m_Distribution[1][((int)inst.classValue())] -= inst.weight();
/* 450:610 */       if (inst.value(index) < instPlusOne.value(index))
/* 451:    */       {
/* 452:611 */         double currCutPoint = (inst.value(index) + instPlusOne.value(index)) / 2.0D;
/* 453:612 */         double currVal = ContingencyTables.entropyConditionedOnRows(this.m_Distribution);
/* 454:613 */         if (currVal < bestVal)
/* 455:    */         {
/* 456:614 */           this.m_SplitPoint = currCutPoint;
/* 457:615 */           bestVal = currVal;
/* 458:616 */           for (int j = 0; j < 3; j++) {
/* 459:617 */             System.arraycopy(this.m_Distribution[j], 0, bestDist[j], 0, this.m_Instances.numClasses());
/* 460:    */           }
/* 461:    */         }
/* 462:    */       }
/* 463:    */     }
/* 464:625 */     if (numMissing == 0) {
/* 465:626 */       System.arraycopy(sum, 0, bestDist[2], 0, this.m_Instances.numClasses());
/* 466:    */     }
/* 467:629 */     this.m_Distribution = bestDist;
/* 468:630 */     return bestVal;
/* 469:    */   }
/* 470:    */   
/* 471:    */   protected double findSplitNumericNumeric(int index)
/* 472:    */     throws Exception
/* 473:    */   {
/* 474:643 */     double bestVal = 1.7976931348623157E+308D;
/* 475:644 */     int numMissing = 0;
/* 476:645 */     double[] sumsSquares = new double[3];double[] sumOfWeights = new double[3];
/* 477:646 */     double[][] bestDist = new double[3][1];
/* 478:647 */     double totalSum = 0.0D;double totalSumOfWeights = 0.0D;
/* 479:650 */     for (int i = 0; i < this.m_Instances.numInstances(); i++)
/* 480:    */     {
/* 481:651 */       Instance inst = this.m_Instances.instance(i);
/* 482:652 */       if (!inst.isMissing(index))
/* 483:    */       {
/* 484:653 */         this.m_Distribution[1][0] += inst.classValue() * inst.weight();
/* 485:654 */         sumsSquares[1] += inst.classValue() * inst.classValue() * inst.weight();
/* 486:    */         
/* 487:656 */         sumOfWeights[1] += inst.weight();
/* 488:    */       }
/* 489:    */       else
/* 490:    */       {
/* 491:658 */         this.m_Distribution[2][0] += inst.classValue() * inst.weight();
/* 492:659 */         sumsSquares[2] += inst.classValue() * inst.classValue() * inst.weight();
/* 493:    */         
/* 494:661 */         sumOfWeights[2] += inst.weight();
/* 495:662 */         numMissing++;
/* 496:    */       }
/* 497:664 */       totalSumOfWeights += inst.weight();
/* 498:665 */       totalSum += inst.classValue() * inst.weight();
/* 499:    */     }
/* 500:669 */     if (totalSumOfWeights <= 0.0D) {
/* 501:670 */       return bestVal;
/* 502:    */     }
/* 503:674 */     this.m_Instances.sort(index);
/* 504:677 */     for (int i = 0; i < this.m_Instances.numInstances() - (numMissing + 1); i++)
/* 505:    */     {
/* 506:678 */       Instance inst = this.m_Instances.instance(i);
/* 507:679 */       Instance instPlusOne = this.m_Instances.instance(i + 1);
/* 508:680 */       this.m_Distribution[0][0] += inst.classValue() * inst.weight();
/* 509:681 */       sumsSquares[0] += inst.classValue() * inst.classValue() * inst.weight();
/* 510:682 */       sumOfWeights[0] += inst.weight();
/* 511:683 */       this.m_Distribution[1][0] -= inst.classValue() * inst.weight();
/* 512:684 */       sumsSquares[1] -= inst.classValue() * inst.classValue() * inst.weight();
/* 513:685 */       sumOfWeights[1] -= inst.weight();
/* 514:686 */       if (inst.value(index) < instPlusOne.value(index))
/* 515:    */       {
/* 516:687 */         double currCutPoint = (inst.value(index) + instPlusOne.value(index)) / 2.0D;
/* 517:688 */         double currVal = variance(this.m_Distribution, sumsSquares, sumOfWeights);
/* 518:689 */         if (currVal < bestVal)
/* 519:    */         {
/* 520:690 */           this.m_SplitPoint = currCutPoint;
/* 521:691 */           bestVal = currVal;
/* 522:692 */           for (int j = 0; j < 3; j++) {
/* 523:693 */             if (sumOfWeights[j] > 0.0D) {
/* 524:694 */               bestDist[j][0] = (this.m_Distribution[j][0] / sumOfWeights[j]);
/* 525:    */             } else {
/* 526:696 */               bestDist[j][0] = (totalSum / totalSumOfWeights);
/* 527:    */             }
/* 528:    */           }
/* 529:    */         }
/* 530:    */       }
/* 531:    */     }
/* 532:703 */     this.m_Distribution = bestDist;
/* 533:704 */     return bestVal;
/* 534:    */   }
/* 535:    */   
/* 536:    */   protected double variance(double[][] s, double[] sS, double[] sumOfWeights)
/* 537:    */   {
/* 538:717 */     double var = 0.0D;
/* 539:719 */     for (int i = 0; i < s.length; i++) {
/* 540:720 */       if (sumOfWeights[i] > 0.0D) {
/* 541:721 */         var += sS[i] - s[i][0] * s[i][0] / sumOfWeights[i];
/* 542:    */       }
/* 543:    */     }
/* 544:725 */     return var;
/* 545:    */   }
/* 546:    */   
/* 547:    */   protected int whichSubset(Instance instance)
/* 548:    */     throws Exception
/* 549:    */   {
/* 550:737 */     if (instance.isMissing(this.m_AttIndex)) {
/* 551:738 */       return 2;
/* 552:    */     }
/* 553:739 */     if (instance.attribute(this.m_AttIndex).isNominal())
/* 554:    */     {
/* 555:740 */       if ((int)instance.value(this.m_AttIndex) == this.m_SplitPoint) {
/* 556:741 */         return 0;
/* 557:    */       }
/* 558:743 */       return 1;
/* 559:    */     }
/* 560:746 */     if (instance.value(this.m_AttIndex) <= this.m_SplitPoint) {
/* 561:747 */       return 0;
/* 562:    */     }
/* 563:749 */     return 1;
/* 564:    */   }
/* 565:    */   
/* 566:    */   public String getRevision()
/* 567:    */   {
/* 568:760 */     return RevisionUtils.extract("$Revision: 9171 $");
/* 569:    */   }
/* 570:    */   
/* 571:    */   public static void main(String[] argv)
/* 572:    */   {
/* 573:769 */     runClassifier(new DecisionStump(), argv);
/* 574:    */   }
/* 575:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.DecisionStump
 * JD-Core Version:    0.7.0.1
 */