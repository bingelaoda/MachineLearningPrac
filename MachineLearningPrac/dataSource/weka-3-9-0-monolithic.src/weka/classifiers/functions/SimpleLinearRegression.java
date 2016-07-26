/*   1:    */ package weka.classifiers.functions;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.classifiers.AbstractClassifier;
/*   8:    */ import weka.classifiers.evaluation.RegressionAnalysis;
/*   9:    */ import weka.core.Attribute;
/*  10:    */ import weka.core.Capabilities;
/*  11:    */ import weka.core.Capabilities.Capability;
/*  12:    */ import weka.core.Instance;
/*  13:    */ import weka.core.Instances;
/*  14:    */ import weka.core.Option;
/*  15:    */ import weka.core.RevisionUtils;
/*  16:    */ import weka.core.Utils;
/*  17:    */ import weka.core.WeightedInstancesHandler;
/*  18:    */ 
/*  19:    */ public class SimpleLinearRegression
/*  20:    */   extends AbstractClassifier
/*  21:    */   implements WeightedInstancesHandler
/*  22:    */ {
/*  23:    */   static final long serialVersionUID = 1679336022895414137L;
/*  24:    */   private Attribute m_attribute;
/*  25:    */   private int m_attributeIndex;
/*  26:    */   private double m_slope;
/*  27:    */   private double m_intercept;
/*  28:    */   private double m_classMeanForMissing;
/*  29:    */   protected boolean m_outputAdditionalStats;
/*  30:    */   private int m_df;
/*  31:105 */   private double m_seSlope = (0.0D / 0.0D);
/*  32:108 */   private double m_seIntercept = (0.0D / 0.0D);
/*  33:111 */   private double m_tstatSlope = (0.0D / 0.0D);
/*  34:114 */   private double m_tstatIntercept = (0.0D / 0.0D);
/*  35:117 */   private double m_rsquared = (0.0D / 0.0D);
/*  36:120 */   private double m_rsquaredAdj = (0.0D / 0.0D);
/*  37:123 */   private double m_fstat = (0.0D / 0.0D);
/*  38:126 */   private boolean m_suppressErrorMessage = false;
/*  39:    */   
/*  40:    */   public String globalInfo()
/*  41:    */   {
/*  42:135 */     return "Learns a simple linear regression model. Picks the attribute that results in the lowest squared error. Can only deal with numeric attributes.";
/*  43:    */   }
/*  44:    */   
/*  45:    */   public Enumeration<Option> listOptions()
/*  46:    */   {
/*  47:147 */     Vector<Option> newVector = new Vector();
/*  48:    */     
/*  49:149 */     newVector.addElement(new Option("\tOutput additional statistics.", "additional-stats", 0, "-additional-stats"));
/*  50:    */     
/*  51:    */ 
/*  52:152 */     newVector.addAll(Collections.list(super.listOptions()));
/*  53:    */     
/*  54:154 */     return newVector.elements();
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void setOptions(String[] options)
/*  58:    */     throws Exception
/*  59:    */   {
/*  60:190 */     setOutputAdditionalStats(Utils.getFlag("additional-stats", options));
/*  61:    */     
/*  62:192 */     super.setOptions(options);
/*  63:193 */     Utils.checkForRemainingOptions(options);
/*  64:    */   }
/*  65:    */   
/*  66:    */   public String[] getOptions()
/*  67:    */   {
/*  68:203 */     Vector<String> result = new Vector();
/*  69:205 */     if (getOutputAdditionalStats()) {
/*  70:206 */       result.add("-additional-stats");
/*  71:    */     }
/*  72:209 */     Collections.addAll(result, super.getOptions());
/*  73:    */     
/*  74:211 */     return (String[])result.toArray(new String[result.size()]);
/*  75:    */   }
/*  76:    */   
/*  77:    */   public String outputAdditionalStatsTipText()
/*  78:    */   {
/*  79:221 */     return "Output additional statistics (such as std deviation of coefficients and t-statistics)";
/*  80:    */   }
/*  81:    */   
/*  82:    */   public void setOutputAdditionalStats(boolean additional)
/*  83:    */   {
/*  84:232 */     this.m_outputAdditionalStats = additional;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public boolean getOutputAdditionalStats()
/*  88:    */   {
/*  89:242 */     return this.m_outputAdditionalStats;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public double classifyInstance(Instance inst)
/*  93:    */     throws Exception
/*  94:    */   {
/*  95:255 */     if (this.m_attribute == null) {
/*  96:256 */       return this.m_intercept;
/*  97:    */     }
/*  98:258 */     if (inst.isMissing(this.m_attributeIndex)) {
/*  99:259 */       return this.m_classMeanForMissing;
/* 100:    */     }
/* 101:261 */     return this.m_intercept + this.m_slope * inst.value(this.m_attributeIndex);
/* 102:    */   }
/* 103:    */   
/* 104:    */   public Capabilities getCapabilities()
/* 105:    */   {
/* 106:272 */     Capabilities result = super.getCapabilities();
/* 107:273 */     result.disableAll();
/* 108:    */     
/* 109:    */ 
/* 110:276 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 111:277 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/* 112:278 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 113:    */     
/* 114:    */ 
/* 115:281 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/* 116:282 */     result.enable(Capabilities.Capability.DATE_CLASS);
/* 117:283 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 118:    */     
/* 119:285 */     return result;
/* 120:    */   }
/* 121:    */   
/* 122:    */   public void buildClassifier(Instances insts)
/* 123:    */     throws Exception
/* 124:    */   {
/* 125:298 */     getCapabilities().testWithFail(insts);
/* 126:300 */     if (this.m_outputAdditionalStats)
/* 127:    */     {
/* 128:304 */       boolean ok = true;
/* 129:305 */       for (int i = 0; i < insts.numInstances(); i++) {
/* 130:306 */         if (insts.instance(i).weight() != 1.0D)
/* 131:    */         {
/* 132:307 */           ok = false;
/* 133:308 */           break;
/* 134:    */         }
/* 135:    */       }
/* 136:311 */       if (!ok) {
/* 137:312 */         throw new Exception("Can only compute additional statistics on unweighted data");
/* 138:    */       }
/* 139:    */     }
/* 140:318 */     double[] sum = new double[insts.numAttributes()];
/* 141:319 */     double[] count = new double[insts.numAttributes()];
/* 142:320 */     double[] classSumForMissing = new double[insts.numAttributes()];
/* 143:321 */     double[] classSumSquaredForMissing = new double[insts.numAttributes()];
/* 144:322 */     double classCount = 0.0D;
/* 145:323 */     double classSum = 0.0D;
/* 146:324 */     for (int j = 0; j < insts.numInstances(); j++)
/* 147:    */     {
/* 148:325 */       Instance inst = insts.instance(j);
/* 149:326 */       if (!inst.classIsMissing())
/* 150:    */       {
/* 151:327 */         for (int i = 0; i < insts.numAttributes(); i++) {
/* 152:328 */           if (!inst.isMissing(i))
/* 153:    */           {
/* 154:329 */             sum[i] += inst.weight() * inst.value(i);
/* 155:330 */             count[i] += inst.weight();
/* 156:    */           }
/* 157:    */           else
/* 158:    */           {
/* 159:332 */             classSumForMissing[i] += inst.classValue() * inst.weight();
/* 160:333 */             classSumSquaredForMissing[i] += inst.classValue() * inst.classValue() * inst.weight();
/* 161:    */           }
/* 162:    */         }
/* 163:337 */         classCount += inst.weight();
/* 164:338 */         classSum += inst.weight() * inst.classValue();
/* 165:    */       }
/* 166:    */     }
/* 167:343 */     double[] mean = new double[insts.numAttributes()];
/* 168:344 */     double[] classMeanForMissing = new double[insts.numAttributes()];
/* 169:345 */     double[] classMeanForKnown = new double[insts.numAttributes()];
/* 170:346 */     for (int i = 0; i < insts.numAttributes(); i++) {
/* 171:347 */       if (i != insts.classIndex())
/* 172:    */       {
/* 173:348 */         if (count[i] > 0.0D) {
/* 174:349 */           sum[i] /= count[i];
/* 175:    */         }
/* 176:351 */         if (classCount - count[i] > 0.0D) {
/* 177:352 */           classSumForMissing[i] /= (classCount - count[i]);
/* 178:    */         }
/* 179:355 */         if (count[i] > 0.0D) {
/* 180:356 */           classMeanForKnown[i] = ((classSum - classSumForMissing[i]) / count[i]);
/* 181:    */         }
/* 182:    */       }
/* 183:    */     }
/* 184:360 */     sum = null;
/* 185:361 */     count = null;
/* 186:    */     
/* 187:363 */     double[] slopes = new double[insts.numAttributes()];
/* 188:364 */     double[] sumWeightedDiffsSquared = new double[insts.numAttributes()];
/* 189:365 */     double[] sumWeightedClassDiffsSquared = new double[insts.numAttributes()];
/* 190:368 */     for (int j = 0; j < insts.numInstances(); j++)
/* 191:    */     {
/* 192:369 */       Instance inst = insts.instance(j);
/* 193:372 */       if (!inst.classIsMissing()) {
/* 194:375 */         for (int i = 0; i < insts.numAttributes(); i++) {
/* 195:376 */           if ((!inst.isMissing(i)) && (i != insts.classIndex()))
/* 196:    */           {
/* 197:377 */             double yDiff = inst.classValue() - classMeanForKnown[i];
/* 198:378 */             double weightedYDiff = inst.weight() * yDiff;
/* 199:379 */             double diff = inst.value(i) - mean[i];
/* 200:380 */             double weightedDiff = inst.weight() * diff;
/* 201:381 */             slopes[i] += weightedYDiff * diff;
/* 202:382 */             sumWeightedDiffsSquared[i] += weightedDiff * diff;
/* 203:383 */             sumWeightedClassDiffsSquared[i] += weightedYDiff * yDiff;
/* 204:    */           }
/* 205:    */         }
/* 206:    */       }
/* 207:    */     }
/* 208:390 */     double minSSE = 1.7976931348623157E+308D;
/* 209:391 */     this.m_attribute = null;
/* 210:392 */     int chosen = -1;
/* 211:393 */     double chosenSlope = (0.0D / 0.0D);
/* 212:394 */     double chosenIntercept = (0.0D / 0.0D);
/* 213:395 */     double chosenMeanForMissing = (0.0D / 0.0D);
/* 214:396 */     for (int i = 0; i < insts.numAttributes(); i++)
/* 215:    */     {
/* 216:399 */       double sseForMissing = classSumSquaredForMissing[i] - classSumForMissing[i] * classMeanForMissing[i];
/* 217:403 */       if ((i != insts.classIndex()) && (sumWeightedDiffsSquared[i] != 0.0D))
/* 218:    */       {
/* 219:408 */         double numerator = slopes[i];
/* 220:409 */         slopes[i] /= sumWeightedDiffsSquared[i];
/* 221:410 */         double intercept = classMeanForKnown[i] - slopes[i] * mean[i];
/* 222:    */         
/* 223:    */ 
/* 224:413 */         double sse = sumWeightedClassDiffsSquared[i] - slopes[i] * numerator;
/* 225:    */         
/* 226:    */ 
/* 227:416 */         sse += sseForMissing;
/* 228:419 */         if (sse < minSSE)
/* 229:    */         {
/* 230:420 */           minSSE = sse;
/* 231:421 */           chosen = i;
/* 232:422 */           chosenSlope = slopes[i];
/* 233:423 */           chosenIntercept = intercept;
/* 234:424 */           chosenMeanForMissing = classMeanForMissing[i];
/* 235:    */         }
/* 236:    */       }
/* 237:    */     }
/* 238:429 */     if (chosen == -1)
/* 239:    */     {
/* 240:430 */       if (!this.m_suppressErrorMessage) {
/* 241:431 */         System.err.println("----- no useful attribute found");
/* 242:    */       }
/* 243:433 */       this.m_attribute = null;
/* 244:434 */       this.m_attributeIndex = 0;
/* 245:435 */       this.m_slope = 0.0D;
/* 246:436 */       this.m_intercept = (classSum / classCount);
/* 247:437 */       this.m_classMeanForMissing = 0.0D;
/* 248:    */     }
/* 249:    */     else
/* 250:    */     {
/* 251:439 */       this.m_attribute = insts.attribute(chosen);
/* 252:440 */       this.m_attributeIndex = chosen;
/* 253:441 */       this.m_slope = chosenSlope;
/* 254:442 */       this.m_intercept = chosenIntercept;
/* 255:443 */       this.m_classMeanForMissing = chosenMeanForMissing;
/* 256:445 */       if (this.m_outputAdditionalStats)
/* 257:    */       {
/* 258:448 */         Instances newInsts = new Instances(insts, insts.numInstances());
/* 259:449 */         for (int i = 0; i < insts.numInstances(); i++)
/* 260:    */         {
/* 261:450 */           Instance inst = insts.instance(i);
/* 262:451 */           if ((!inst.classIsMissing()) && (!inst.isMissing(this.m_attributeIndex))) {
/* 263:452 */             newInsts.add(inst);
/* 264:    */           }
/* 265:    */         }
/* 266:455 */         insts = newInsts;
/* 267:    */         
/* 268:    */ 
/* 269:458 */         this.m_df = (insts.numInstances() - 2);
/* 270:459 */         double[] stdErrors = RegressionAnalysis.calculateStdErrorOfCoef(insts, this.m_attribute, this.m_slope, this.m_intercept, this.m_df);
/* 271:    */         
/* 272:461 */         this.m_seSlope = stdErrors[0];
/* 273:462 */         this.m_seIntercept = stdErrors[1];
/* 274:463 */         double[] coef = new double[2];
/* 275:464 */         coef[0] = this.m_slope;
/* 276:465 */         coef[1] = this.m_intercept;
/* 277:466 */         double[] tStats = RegressionAnalysis.calculateTStats(coef, stdErrors, 2);
/* 278:    */         
/* 279:468 */         this.m_tstatSlope = tStats[0];
/* 280:469 */         this.m_tstatIntercept = tStats[1];
/* 281:470 */         double ssr = RegressionAnalysis.calculateSSR(insts, this.m_attribute, this.m_slope, this.m_intercept);
/* 282:    */         
/* 283:472 */         this.m_rsquared = RegressionAnalysis.calculateRSquared(insts, ssr);
/* 284:473 */         this.m_rsquaredAdj = RegressionAnalysis.calculateAdjRSquared(this.m_rsquared, insts.numInstances(), 2);
/* 285:    */         
/* 286:475 */         this.m_fstat = RegressionAnalysis.calculateFStat(this.m_rsquared, insts.numInstances(), 2);
/* 287:    */       }
/* 288:    */     }
/* 289:    */   }
/* 290:    */   
/* 291:    */   public boolean foundUsefulAttribute()
/* 292:    */   {
/* 293:487 */     return this.m_attribute != null;
/* 294:    */   }
/* 295:    */   
/* 296:    */   public int getAttributeIndex()
/* 297:    */   {
/* 298:496 */     return this.m_attributeIndex;
/* 299:    */   }
/* 300:    */   
/* 301:    */   public double getSlope()
/* 302:    */   {
/* 303:505 */     return this.m_slope;
/* 304:    */   }
/* 305:    */   
/* 306:    */   public double getIntercept()
/* 307:    */   {
/* 308:514 */     return this.m_intercept;
/* 309:    */   }
/* 310:    */   
/* 311:    */   public void setSuppressErrorMessage(boolean s)
/* 312:    */   {
/* 313:524 */     this.m_suppressErrorMessage = s;
/* 314:    */   }
/* 315:    */   
/* 316:    */   public String toString()
/* 317:    */   {
/* 318:535 */     StringBuffer text = new StringBuffer();
/* 319:536 */     if (this.m_attribute == null)
/* 320:    */     {
/* 321:537 */       text.append("Predicting constant " + this.m_intercept);
/* 322:    */     }
/* 323:    */     else
/* 324:    */     {
/* 325:539 */       text.append("Linear regression on " + this.m_attribute.name() + "\n\n");
/* 326:540 */       text.append(Utils.doubleToString(this.m_slope, 2) + " * " + this.m_attribute.name());
/* 327:542 */       if (this.m_intercept > 0.0D) {
/* 328:543 */         text.append(" + " + Utils.doubleToString(this.m_intercept, 2));
/* 329:    */       } else {
/* 330:545 */         text.append(" - " + Utils.doubleToString(-this.m_intercept, 2));
/* 331:    */       }
/* 332:547 */       text.append("\n\nPredicting " + Utils.doubleToString(this.m_classMeanForMissing, 2) + " if attribute value is missing.");
/* 333:551 */       if (this.m_outputAdditionalStats)
/* 334:    */       {
/* 335:553 */         int attNameLength = this.m_attribute.name().length() + 3;
/* 336:554 */         if (attNameLength < "Variable".length() + 3) {
/* 337:555 */           attNameLength = "Variable".length() + 3;
/* 338:    */         }
/* 339:557 */         text.append("\n\nRegression Analysis:\n\n" + Utils.padRight("Variable", attNameLength) + "  Coefficient     SE of Coef        t-Stat");
/* 340:    */         
/* 341:    */ 
/* 342:    */ 
/* 343:561 */         text.append("\n" + Utils.padRight(this.m_attribute.name(), attNameLength));
/* 344:562 */         text.append(Utils.doubleToString(this.m_slope, 12, 4));
/* 345:563 */         text.append("   " + Utils.doubleToString(this.m_seSlope, 12, 5));
/* 346:564 */         text.append("   " + Utils.doubleToString(this.m_tstatSlope, 12, 5));
/* 347:565 */         text.append(Utils.padRight("\nconst", attNameLength + 1) + Utils.doubleToString(this.m_intercept, 12, 4));
/* 348:    */         
/* 349:567 */         text.append("   " + Utils.doubleToString(this.m_seIntercept, 12, 5));
/* 350:568 */         text.append("   " + Utils.doubleToString(this.m_tstatIntercept, 12, 5));
/* 351:569 */         text.append("\n\nDegrees of freedom = " + Integer.toString(this.m_df));
/* 352:570 */         text.append("\nR^2 value = " + Utils.doubleToString(this.m_rsquared, 5));
/* 353:571 */         text.append("\nAdjusted R^2 = " + Utils.doubleToString(this.m_rsquaredAdj, 5));
/* 354:    */         
/* 355:573 */         text.append("\nF-statistic = " + Utils.doubleToString(this.m_fstat, 5));
/* 356:    */       }
/* 357:    */     }
/* 358:576 */     text.append("\n");
/* 359:577 */     return text.toString();
/* 360:    */   }
/* 361:    */   
/* 362:    */   public String getRevision()
/* 363:    */   {
/* 364:587 */     return RevisionUtils.extract("$Revision: 11130 $");
/* 365:    */   }
/* 366:    */   
/* 367:    */   public static void main(String[] argv)
/* 368:    */   {
/* 369:596 */     runClassifier(new SimpleLinearRegression(), argv);
/* 370:    */   }
/* 371:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.SimpleLinearRegression
 * JD-Core Version:    0.7.0.1
 */