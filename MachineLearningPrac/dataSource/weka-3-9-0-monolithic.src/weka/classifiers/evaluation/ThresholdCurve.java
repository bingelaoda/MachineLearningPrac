/*   1:    */ package weka.classifiers.evaluation;
/*   2:    */ 
/*   3:    */ import java.io.InputStreamReader;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import weka.classifiers.Classifier;
/*   7:    */ import weka.classifiers.functions.Logistic;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.DenseInstance;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.RevisionHandler;
/*  13:    */ import weka.core.RevisionUtils;
/*  14:    */ import weka.core.Utils;
/*  15:    */ 
/*  16:    */ public class ThresholdCurve
/*  17:    */   implements RevisionHandler
/*  18:    */ {
/*  19:    */   public static final String RELATION_NAME = "ThresholdCurve";
/*  20:    */   public static final String TRUE_POS_NAME = "True Positives";
/*  21:    */   public static final String FALSE_NEG_NAME = "False Negatives";
/*  22:    */   public static final String FALSE_POS_NAME = "False Positives";
/*  23:    */   public static final String TRUE_NEG_NAME = "True Negatives";
/*  24:    */   public static final String FP_RATE_NAME = "False Positive Rate";
/*  25:    */   public static final String TP_RATE_NAME = "True Positive Rate";
/*  26:    */   public static final String PRECISION_NAME = "Precision";
/*  27:    */   public static final String RECALL_NAME = "Recall";
/*  28:    */   public static final String FALLOUT_NAME = "Fallout";
/*  29:    */   public static final String FMEASURE_NAME = "FMeasure";
/*  30:    */   public static final String SAMPLE_SIZE_NAME = "Sample Size";
/*  31:    */   public static final String LIFT_NAME = "Lift";
/*  32:    */   public static final String THRESHOLD_NAME = "Threshold";
/*  33:    */   
/*  34:    */   public Instances getCurve(ArrayList<Prediction> predictions)
/*  35:    */   {
/*  36:108 */     if (predictions.size() == 0) {
/*  37:109 */       return null;
/*  38:    */     }
/*  39:111 */     return getCurve(predictions, ((NominalPrediction)predictions.get(0)).distribution().length - 1);
/*  40:    */   }
/*  41:    */   
/*  42:    */   public Instances getCurve(ArrayList<Prediction> predictions, int classIndex)
/*  43:    */   {
/*  44:125 */     if ((predictions.size() == 0) || (((NominalPrediction)predictions.get(0)).distribution().length <= classIndex)) {
/*  45:127 */       return null;
/*  46:    */     }
/*  47:130 */     double totPos = 0.0D;double totNeg = 0.0D;
/*  48:131 */     double[] probs = getProbabilities(predictions, classIndex);
/*  49:134 */     for (int i = 0; i < probs.length; i++)
/*  50:    */     {
/*  51:135 */       NominalPrediction pred = (NominalPrediction)predictions.get(i);
/*  52:136 */       if (pred.actual() == Prediction.MISSING_VALUE) {
/*  53:137 */         System.err.println(getClass().getName() + " Skipping prediction with missing class value");
/*  54:141 */       } else if (pred.weight() < 0.0D) {
/*  55:142 */         System.err.println(getClass().getName() + " Skipping prediction with negative weight");
/*  56:146 */       } else if (pred.actual() == classIndex) {
/*  57:147 */         totPos += pred.weight();
/*  58:    */       } else {
/*  59:149 */         totNeg += pred.weight();
/*  60:    */       }
/*  61:    */     }
/*  62:153 */     Instances insts = makeHeader();
/*  63:154 */     int[] sorted = Utils.sort(probs);
/*  64:155 */     TwoClassStats tc = new TwoClassStats(totPos, totNeg, 0.0D, 0.0D);
/*  65:156 */     double threshold = 0.0D;
/*  66:157 */     double cumulativePos = 0.0D;
/*  67:158 */     double cumulativeNeg = 0.0D;
/*  68:160 */     for (int i = 0; i < sorted.length; i++)
/*  69:    */     {
/*  70:162 */       if ((i == 0) || (probs[sorted[i]] > threshold))
/*  71:    */       {
/*  72:163 */         tc.setTruePositive(tc.getTruePositive() - cumulativePos);
/*  73:164 */         tc.setFalseNegative(tc.getFalseNegative() + cumulativePos);
/*  74:165 */         tc.setFalsePositive(tc.getFalsePositive() - cumulativeNeg);
/*  75:166 */         tc.setTrueNegative(tc.getTrueNegative() + cumulativeNeg);
/*  76:167 */         threshold = probs[sorted[i]];
/*  77:168 */         insts.add(makeInstance(tc, threshold));
/*  78:169 */         cumulativePos = 0.0D;
/*  79:170 */         cumulativeNeg = 0.0D;
/*  80:171 */         if (i == sorted.length - 1) {
/*  81:    */           break;
/*  82:    */         }
/*  83:    */       }
/*  84:176 */       NominalPrediction pred = (NominalPrediction)predictions.get(sorted[i]);
/*  85:178 */       if (pred.actual() == Prediction.MISSING_VALUE) {
/*  86:179 */         System.err.println(getClass().getName() + " Skipping prediction with missing class value");
/*  87:183 */       } else if (pred.weight() < 0.0D) {
/*  88:184 */         System.err.println(getClass().getName() + " Skipping prediction with negative weight");
/*  89:188 */       } else if (pred.actual() == classIndex) {
/*  90:189 */         cumulativePos += pred.weight();
/*  91:    */       } else {
/*  92:191 */         cumulativeNeg += pred.weight();
/*  93:    */       }
/*  94:    */     }
/*  95:206 */     if ((tc.getFalseNegative() != totPos) || (tc.getTrueNegative() != totNeg))
/*  96:    */     {
/*  97:207 */       tc = new TwoClassStats(0.0D, 0.0D, totNeg, totPos);
/*  98:208 */       threshold = probs[sorted[(sorted.length - 1)]] + 1.E-005D;
/*  99:209 */       insts.add(makeInstance(tc, threshold));
/* 100:    */     }
/* 101:212 */     return insts;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public static double getNPointPrecision(Instances tcurve, int n)
/* 105:    */   {
/* 106:225 */     if ((!"ThresholdCurve".equals(tcurve.relationName())) || (tcurve.numInstances() == 0)) {
/* 107:227 */       return (0.0D / 0.0D);
/* 108:    */     }
/* 109:229 */     int recallInd = tcurve.attribute("Recall").index();
/* 110:230 */     int precisInd = tcurve.attribute("Precision").index();
/* 111:231 */     double[] recallVals = tcurve.attributeToDoubleArray(recallInd);
/* 112:232 */     int[] sorted = Utils.sort(recallVals);
/* 113:233 */     double isize = 1.0D / (n - 1);
/* 114:234 */     double psum = 0.0D;
/* 115:235 */     for (int i = 0; i < n; i++)
/* 116:    */     {
/* 117:236 */       int pos = binarySearch(sorted, recallVals, i * isize);
/* 118:237 */       double recall = recallVals[sorted[pos]];
/* 119:238 */       double precis = tcurve.instance(sorted[pos]).value(precisInd);
/* 120:244 */       while ((pos != 0) && (pos < sorted.length - 1))
/* 121:    */       {
/* 122:245 */         pos++;
/* 123:246 */         double recall2 = recallVals[sorted[pos]];
/* 124:247 */         if (recall2 != recall)
/* 125:    */         {
/* 126:248 */           double precis2 = tcurve.instance(sorted[pos]).value(precisInd);
/* 127:249 */           double slope = (precis2 - precis) / (recall2 - recall);
/* 128:250 */           double offset = precis - recall * slope;
/* 129:251 */           precis = isize * i * slope + offset;
/* 130:    */           
/* 131:    */ 
/* 132:    */ 
/* 133:    */ 
/* 134:256 */           break;
/* 135:    */         }
/* 136:    */       }
/* 137:259 */       psum += precis;
/* 138:    */     }
/* 139:261 */     return psum / n;
/* 140:    */   }
/* 141:    */   
/* 142:    */   public static double getPRCArea(Instances tcurve)
/* 143:    */   {
/* 144:272 */     int n = tcurve.numInstances();
/* 145:273 */     if ((!"ThresholdCurve".equals(tcurve.relationName())) || (n == 0)) {
/* 146:274 */       return (0.0D / 0.0D);
/* 147:    */     }
/* 148:277 */     int pInd = tcurve.attribute("Precision").index();
/* 149:278 */     int rInd = tcurve.attribute("Recall").index();
/* 150:279 */     double[] pVals = tcurve.attributeToDoubleArray(pInd);
/* 151:280 */     double[] rVals = tcurve.attributeToDoubleArray(rInd);
/* 152:    */     
/* 153:282 */     double area = 0.0D;
/* 154:283 */     double xlast = rVals[(n - 1)];
/* 155:286 */     for (int i = n - 2; i >= 0; i--)
/* 156:    */     {
/* 157:287 */       double recallDelta = rVals[i] - xlast;
/* 158:288 */       area += pVals[i] * recallDelta;
/* 159:    */       
/* 160:290 */       xlast = rVals[i];
/* 161:    */     }
/* 162:293 */     if (area == 0.0D) {
/* 163:294 */       return Utils.missingValue();
/* 164:    */     }
/* 165:296 */     return area;
/* 166:    */   }
/* 167:    */   
/* 168:    */   public static double getROCArea(Instances tcurve)
/* 169:    */   {
/* 170:309 */     int n = tcurve.numInstances();
/* 171:310 */     if ((!"ThresholdCurve".equals(tcurve.relationName())) || (n == 0)) {
/* 172:311 */       return (0.0D / 0.0D);
/* 173:    */     }
/* 174:313 */     int tpInd = tcurve.attribute("True Positives").index();
/* 175:314 */     int fpInd = tcurve.attribute("False Positives").index();
/* 176:315 */     double[] tpVals = tcurve.attributeToDoubleArray(tpInd);
/* 177:316 */     double[] fpVals = tcurve.attributeToDoubleArray(fpInd);
/* 178:    */     
/* 179:318 */     double area = 0.0D;double cumNeg = 0.0D;
/* 180:319 */     double totalPos = tpVals[0];
/* 181:320 */     double totalNeg = fpVals[0];
/* 182:321 */     for (int i = 0; i < n; i++)
/* 183:    */     {
/* 184:    */       double cin;
/* 185:    */       double cip;
/* 186:    */       double cin;
/* 187:323 */       if (i < n - 1)
/* 188:    */       {
/* 189:324 */         double cip = tpVals[i] - tpVals[(i + 1)];
/* 190:325 */         cin = fpVals[i] - fpVals[(i + 1)];
/* 191:    */       }
/* 192:    */       else
/* 193:    */       {
/* 194:327 */         cip = tpVals[(n - 1)];
/* 195:328 */         cin = fpVals[(n - 1)];
/* 196:    */       }
/* 197:330 */       area += cip * (cumNeg + 0.5D * cin);
/* 198:331 */       cumNeg += cin;
/* 199:    */     }
/* 200:333 */     area /= totalNeg * totalPos;
/* 201:    */     
/* 202:335 */     return area;
/* 203:    */   }
/* 204:    */   
/* 205:    */   public static int getThresholdInstance(Instances tcurve, double threshold)
/* 206:    */   {
/* 207:350 */     if ((!"ThresholdCurve".equals(tcurve.relationName())) || (tcurve.numInstances() == 0) || (threshold < 0.0D) || (threshold > 1.0D)) {
/* 208:352 */       return -1;
/* 209:    */     }
/* 210:354 */     if (tcurve.numInstances() == 1) {
/* 211:355 */       return 0;
/* 212:    */     }
/* 213:357 */     double[] tvals = tcurve.attributeToDoubleArray(tcurve.numAttributes() - 1);
/* 214:358 */     int[] sorted = Utils.sort(tvals);
/* 215:359 */     return binarySearch(sorted, tvals, threshold);
/* 216:    */   }
/* 217:    */   
/* 218:    */   private static int binarySearch(int[] index, double[] vals, double target)
/* 219:    */   {
/* 220:372 */     int lo = 0;int hi = index.length - 1;
/* 221:373 */     while (hi - lo > 1)
/* 222:    */     {
/* 223:374 */       int mid = lo + (hi - lo) / 2;
/* 224:375 */       double midval = vals[index[mid]];
/* 225:376 */       if (target > midval)
/* 226:    */       {
/* 227:377 */         lo = mid;
/* 228:    */       }
/* 229:378 */       else if (target < midval)
/* 230:    */       {
/* 231:379 */         hi = mid;
/* 232:    */       }
/* 233:    */       else
/* 234:    */       {
/* 235:381 */         while ((mid > 0) && (vals[index[(mid - 1)]] == target)) {
/* 236:382 */           mid--;
/* 237:    */         }
/* 238:384 */         return mid;
/* 239:    */       }
/* 240:    */     }
/* 241:387 */     return lo;
/* 242:    */   }
/* 243:    */   
/* 244:    */   private double[] getProbabilities(ArrayList<Prediction> predictions, int classIndex)
/* 245:    */   {
/* 246:400 */     double[] probs = new double[predictions.size()];
/* 247:401 */     for (int i = 0; i < probs.length; i++)
/* 248:    */     {
/* 249:402 */       NominalPrediction pred = (NominalPrediction)predictions.get(i);
/* 250:403 */       probs[i] = pred.distribution()[classIndex];
/* 251:    */     }
/* 252:405 */     return probs;
/* 253:    */   }
/* 254:    */   
/* 255:    */   private Instances makeHeader()
/* 256:    */   {
/* 257:415 */     ArrayList<Attribute> fv = new ArrayList();
/* 258:416 */     fv.add(new Attribute("True Positives"));
/* 259:417 */     fv.add(new Attribute("False Negatives"));
/* 260:418 */     fv.add(new Attribute("False Positives"));
/* 261:419 */     fv.add(new Attribute("True Negatives"));
/* 262:420 */     fv.add(new Attribute("False Positive Rate"));
/* 263:421 */     fv.add(new Attribute("True Positive Rate"));
/* 264:422 */     fv.add(new Attribute("Precision"));
/* 265:423 */     fv.add(new Attribute("Recall"));
/* 266:424 */     fv.add(new Attribute("Fallout"));
/* 267:425 */     fv.add(new Attribute("FMeasure"));
/* 268:426 */     fv.add(new Attribute("Sample Size"));
/* 269:427 */     fv.add(new Attribute("Lift"));
/* 270:428 */     fv.add(new Attribute("Threshold"));
/* 271:429 */     return new Instances("ThresholdCurve", fv, 100);
/* 272:    */   }
/* 273:    */   
/* 274:    */   private Instance makeInstance(TwoClassStats tc, double prob)
/* 275:    */   {
/* 276:441 */     int count = 0;
/* 277:442 */     double[] vals = new double[13];
/* 278:443 */     vals[(count++)] = tc.getTruePositive();
/* 279:444 */     vals[(count++)] = tc.getFalseNegative();
/* 280:445 */     vals[(count++)] = tc.getFalsePositive();
/* 281:446 */     vals[(count++)] = tc.getTrueNegative();
/* 282:447 */     vals[(count++)] = tc.getFalsePositiveRate();
/* 283:448 */     vals[(count++)] = tc.getTruePositiveRate();
/* 284:449 */     vals[(count++)] = tc.getPrecision();
/* 285:450 */     vals[(count++)] = tc.getRecall();
/* 286:451 */     vals[(count++)] = tc.getFallout();
/* 287:452 */     vals[(count++)] = tc.getFMeasure();
/* 288:453 */     double ss = (tc.getTruePositive() + tc.getFalsePositive()) / (tc.getTruePositive() + tc.getFalsePositive() + tc.getTrueNegative() + tc.getFalseNegative());
/* 289:    */     
/* 290:    */ 
/* 291:456 */     vals[(count++)] = ss;
/* 292:457 */     double expectedByChance = ss * (tc.getTruePositive() + tc.getFalseNegative());
/* 293:459 */     if (expectedByChance < 1.0D) {
/* 294:460 */       vals[(count++)] = Utils.missingValue();
/* 295:    */     } else {
/* 296:462 */       vals[(count++)] = (tc.getTruePositive() / expectedByChance);
/* 297:    */     }
/* 298:465 */     vals[(count++)] = prob;
/* 299:466 */     return new DenseInstance(1.0D, vals);
/* 300:    */   }
/* 301:    */   
/* 302:    */   public String getRevision()
/* 303:    */   {
/* 304:476 */     return RevisionUtils.extract("$Revision: 10153 $");
/* 305:    */   }
/* 306:    */   
/* 307:    */   public static void main(String[] args)
/* 308:    */   {
/* 309:    */     try
/* 310:    */     {
/* 311:489 */       Instances inst = new Instances(new InputStreamReader(System.in));
/* 312:490 */       if (0.0D != Math.log(1.0D))
/* 313:    */       {
/* 314:491 */         System.out.println(getNPointPrecision(inst, 11));
/* 315:    */       }
/* 316:    */       else
/* 317:    */       {
/* 318:493 */         inst.setClassIndex(inst.numAttributes() - 1);
/* 319:494 */         ThresholdCurve tc = new ThresholdCurve();
/* 320:495 */         EvaluationUtils eu = new EvaluationUtils();
/* 321:496 */         Classifier classifier = new Logistic();
/* 322:497 */         ArrayList<Prediction> predictions = new ArrayList();
/* 323:498 */         for (int i = 0; i < 2; i++)
/* 324:    */         {
/* 325:499 */           eu.setSeed(i);
/* 326:500 */           predictions.addAll(eu.getCVPredictions(classifier, inst, 10));
/* 327:    */         }
/* 328:503 */         Instances result = tc.getCurve(predictions);
/* 329:504 */         System.out.println(result);
/* 330:    */       }
/* 331:    */     }
/* 332:    */     catch (Exception ex)
/* 333:    */     {
/* 334:507 */       ex.printStackTrace();
/* 335:    */     }
/* 336:    */   }
/* 337:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.evaluation.ThresholdCurve
 * JD-Core Version:    0.7.0.1
 */