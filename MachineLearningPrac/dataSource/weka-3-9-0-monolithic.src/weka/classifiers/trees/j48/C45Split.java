/*   1:    */ package weka.classifiers.trees.j48;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import weka.core.Attribute;
/*   5:    */ import weka.core.Instance;
/*   6:    */ import weka.core.Instances;
/*   7:    */ import weka.core.RevisionUtils;
/*   8:    */ import weka.core.Utils;
/*   9:    */ 
/*  10:    */ public class C45Split
/*  11:    */   extends ClassifierSplitModel
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = 3064079330067903161L;
/*  14:    */   protected int m_complexityIndex;
/*  15:    */   protected final int m_attIndex;
/*  16:    */   protected final int m_minNoObj;
/*  17:    */   protected final boolean m_useMDLcorrection;
/*  18:    */   protected double m_splitPoint;
/*  19:    */   protected double m_infoGain;
/*  20:    */   protected double m_gainRatio;
/*  21:    */   protected final double m_sumOfWeights;
/*  22:    */   protected int m_index;
/*  23: 70 */   protected static InfoGainSplitCrit infoGainCrit = new InfoGainSplitCrit();
/*  24: 73 */   protected static GainRatioSplitCrit gainRatioCrit = new GainRatioSplitCrit();
/*  25:    */   
/*  26:    */   public C45Split(int attIndex, int minNoObj, double sumOfWeights, boolean useMDLcorrection)
/*  27:    */   {
/*  28: 82 */     this.m_attIndex = attIndex;
/*  29:    */     
/*  30:    */ 
/*  31: 85 */     this.m_minNoObj = minNoObj;
/*  32:    */     
/*  33:    */ 
/*  34: 88 */     this.m_sumOfWeights = sumOfWeights;
/*  35:    */     
/*  36:    */ 
/*  37: 91 */     this.m_useMDLcorrection = useMDLcorrection;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public void buildClassifier(Instances trainInstances)
/*  41:    */     throws Exception
/*  42:    */   {
/*  43:104 */     this.m_numSubsets = 0;
/*  44:105 */     this.m_splitPoint = 1.7976931348623157E+308D;
/*  45:106 */     this.m_infoGain = 0.0D;
/*  46:107 */     this.m_gainRatio = 0.0D;
/*  47:111 */     if (trainInstances.attribute(this.m_attIndex).isNominal())
/*  48:    */     {
/*  49:112 */       this.m_complexityIndex = trainInstances.attribute(this.m_attIndex).numValues();
/*  50:113 */       this.m_index = this.m_complexityIndex;
/*  51:114 */       handleEnumeratedAttribute(trainInstances);
/*  52:    */     }
/*  53:    */     else
/*  54:    */     {
/*  55:116 */       this.m_complexityIndex = 2;
/*  56:117 */       this.m_index = 0;
/*  57:118 */       trainInstances.sort(trainInstances.attribute(this.m_attIndex));
/*  58:119 */       handleNumericAttribute(trainInstances);
/*  59:    */     }
/*  60:    */   }
/*  61:    */   
/*  62:    */   public final int attIndex()
/*  63:    */   {
/*  64:128 */     return this.m_attIndex;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public double splitPoint()
/*  68:    */   {
/*  69:137 */     return this.m_splitPoint;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public final double classProb(int classIndex, Instance instance, int theSubset)
/*  73:    */     throws Exception
/*  74:    */   {
/*  75:149 */     if (theSubset <= -1)
/*  76:    */     {
/*  77:150 */       double[] weights = weights(instance);
/*  78:151 */       if (weights == null) {
/*  79:152 */         return this.m_distribution.prob(classIndex);
/*  80:    */       }
/*  81:154 */       double prob = 0.0D;
/*  82:155 */       for (int i = 0; i < weights.length; i++) {
/*  83:156 */         prob += weights[i] * this.m_distribution.prob(classIndex, i);
/*  84:    */       }
/*  85:158 */       return prob;
/*  86:    */     }
/*  87:161 */     if (Utils.gr(this.m_distribution.perBag(theSubset), 0.0D)) {
/*  88:162 */       return this.m_distribution.prob(classIndex, theSubset);
/*  89:    */     }
/*  90:164 */     return this.m_distribution.prob(classIndex);
/*  91:    */   }
/*  92:    */   
/*  93:    */   public final double codingCost()
/*  94:    */   {
/*  95:175 */     return Utils.log2(this.m_index);
/*  96:    */   }
/*  97:    */   
/*  98:    */   public final double gainRatio()
/*  99:    */   {
/* 100:182 */     return this.m_gainRatio;
/* 101:    */   }
/* 102:    */   
/* 103:    */   private void handleEnumeratedAttribute(Instances trainInstances)
/* 104:    */     throws Exception
/* 105:    */   {
/* 106:195 */     this.m_distribution = new Distribution(this.m_complexityIndex, trainInstances.numClasses());
/* 107:    */     
/* 108:    */ 
/* 109:    */ 
/* 110:199 */     Enumeration<Instance> enu = trainInstances.enumerateInstances();
/* 111:200 */     while (enu.hasMoreElements())
/* 112:    */     {
/* 113:201 */       Instance instance = (Instance)enu.nextElement();
/* 114:202 */       if (!instance.isMissing(this.m_attIndex)) {
/* 115:203 */         this.m_distribution.add((int)instance.value(this.m_attIndex), instance);
/* 116:    */       }
/* 117:    */     }
/* 118:209 */     if (this.m_distribution.check(this.m_minNoObj))
/* 119:    */     {
/* 120:210 */       this.m_numSubsets = this.m_complexityIndex;
/* 121:211 */       this.m_infoGain = infoGainCrit.splitCritValue(this.m_distribution, this.m_sumOfWeights);
/* 122:212 */       this.m_gainRatio = gainRatioCrit.splitCritValue(this.m_distribution, this.m_sumOfWeights, this.m_infoGain);
/* 123:    */     }
/* 124:    */   }
/* 125:    */   
/* 126:    */   private void handleNumericAttribute(Instances trainInstances)
/* 127:    */     throws Exception
/* 128:    */   {
/* 129:226 */     int next = 1;
/* 130:227 */     int last = 0;
/* 131:228 */     int splitIndex = -1;
/* 132:    */     
/* 133:    */ 
/* 134:    */ 
/* 135:    */ 
/* 136:    */ 
/* 137:    */ 
/* 138:    */ 
/* 139:236 */     this.m_distribution = new Distribution(2, trainInstances.numClasses());
/* 140:    */     
/* 141:    */ 
/* 142:239 */     Enumeration<Instance> enu = trainInstances.enumerateInstances();
/* 143:240 */     int i = 0;
/* 144:241 */     while (enu.hasMoreElements())
/* 145:    */     {
/* 146:242 */       Instance instance = (Instance)enu.nextElement();
/* 147:243 */       if (instance.isMissing(this.m_attIndex)) {
/* 148:    */         break;
/* 149:    */       }
/* 150:246 */       this.m_distribution.add(1, instance);
/* 151:247 */       i++;
/* 152:    */     }
/* 153:249 */     int firstMiss = i;
/* 154:    */     
/* 155:    */ 
/* 156:    */ 
/* 157:253 */     double minSplit = 0.1D * this.m_distribution.total() / trainInstances.numClasses();
/* 158:254 */     if (Utils.smOrEq(minSplit, this.m_minNoObj)) {
/* 159:255 */       minSplit = this.m_minNoObj;
/* 160:256 */     } else if (Utils.gr(minSplit, 25.0D)) {
/* 161:257 */       minSplit = 25.0D;
/* 162:    */     }
/* 163:261 */     if (Utils.sm(firstMiss, 2.0D * minSplit)) {
/* 164:262 */       return;
/* 165:    */     }
/* 166:267 */     double defaultEnt = infoGainCrit.oldEnt(this.m_distribution);
/* 167:268 */     while (next < firstMiss)
/* 168:    */     {
/* 169:270 */       if (trainInstances.instance(next - 1).value(this.m_attIndex) + 1.E-005D < trainInstances.instance(next).value(this.m_attIndex))
/* 170:    */       {
/* 171:275 */         this.m_distribution.shiftRange(1, 0, trainInstances, last, next);
/* 172:279 */         if ((Utils.grOrEq(this.m_distribution.perBag(0), minSplit)) && (Utils.grOrEq(this.m_distribution.perBag(1), minSplit)))
/* 173:    */         {
/* 174:281 */           double currentInfoGain = infoGainCrit.splitCritValue(this.m_distribution, this.m_sumOfWeights, defaultEnt);
/* 175:283 */           if (Utils.gr(currentInfoGain, this.m_infoGain))
/* 176:    */           {
/* 177:284 */             this.m_infoGain = currentInfoGain;
/* 178:285 */             splitIndex = next - 1;
/* 179:    */           }
/* 180:287 */           this.m_index += 1;
/* 181:    */         }
/* 182:289 */         last = next;
/* 183:    */       }
/* 184:291 */       next++;
/* 185:    */     }
/* 186:295 */     if (this.m_index == 0) {
/* 187:296 */       return;
/* 188:    */     }
/* 189:300 */     if (this.m_useMDLcorrection) {
/* 190:301 */       this.m_infoGain -= Utils.log2(this.m_index) / this.m_sumOfWeights;
/* 191:    */     }
/* 192:303 */     if (Utils.smOrEq(this.m_infoGain, 0.0D)) {
/* 193:304 */       return;
/* 194:    */     }
/* 195:309 */     this.m_numSubsets = 2;
/* 196:310 */     this.m_splitPoint = ((trainInstances.instance(splitIndex + 1).value(this.m_attIndex) + trainInstances.instance(splitIndex).value(this.m_attIndex)) / 2.0D);
/* 197:315 */     if (this.m_splitPoint == trainInstances.instance(splitIndex + 1).value(this.m_attIndex)) {
/* 198:317 */       this.m_splitPoint = trainInstances.instance(splitIndex).value(this.m_attIndex);
/* 199:    */     }
/* 200:321 */     this.m_distribution = new Distribution(2, trainInstances.numClasses());
/* 201:322 */     this.m_distribution.addRange(0, trainInstances, 0, splitIndex + 1);
/* 202:323 */     this.m_distribution.addRange(1, trainInstances, splitIndex + 1, firstMiss);
/* 203:    */     
/* 204:    */ 
/* 205:326 */     this.m_gainRatio = gainRatioCrit.splitCritValue(this.m_distribution, this.m_sumOfWeights, this.m_infoGain);
/* 206:    */   }
/* 207:    */   
/* 208:    */   public final double infoGain()
/* 209:    */   {
/* 210:335 */     return this.m_infoGain;
/* 211:    */   }
/* 212:    */   
/* 213:    */   public final String leftSide(Instances data)
/* 214:    */   {
/* 215:346 */     return data.attribute(this.m_attIndex).name();
/* 216:    */   }
/* 217:    */   
/* 218:    */   public final String rightSide(int index, Instances data)
/* 219:    */   {
/* 220:360 */     StringBuffer text = new StringBuffer();
/* 221:361 */     if (data.attribute(this.m_attIndex).isNominal()) {
/* 222:362 */       text.append(" = " + data.attribute(this.m_attIndex).value(index));
/* 223:363 */     } else if (index == 0) {
/* 224:364 */       text.append(" <= " + Utils.doubleToString(this.m_splitPoint, 6));
/* 225:    */     } else {
/* 226:366 */       text.append(" > " + Utils.doubleToString(this.m_splitPoint, 6));
/* 227:    */     }
/* 228:368 */     return text.toString();
/* 229:    */   }
/* 230:    */   
/* 231:    */   public final String sourceExpression(int index, Instances data)
/* 232:    */   {
/* 233:382 */     StringBuffer expr = null;
/* 234:383 */     if (index < 0) {
/* 235:384 */       return "i[" + this.m_attIndex + "] == null";
/* 236:    */     }
/* 237:386 */     if (data.attribute(this.m_attIndex).isNominal())
/* 238:    */     {
/* 239:387 */       expr = new StringBuffer("i[");
/* 240:388 */       expr.append(this.m_attIndex).append("]");
/* 241:389 */       expr.append(".equals(\"").append(data.attribute(this.m_attIndex).value(index)).append("\")");
/* 242:    */     }
/* 243:    */     else
/* 244:    */     {
/* 245:392 */       expr = new StringBuffer("((Double) i[");
/* 246:393 */       expr.append(this.m_attIndex).append("])");
/* 247:394 */       if (index == 0) {
/* 248:395 */         expr.append(".doubleValue() <= ").append(this.m_splitPoint);
/* 249:    */       } else {
/* 250:397 */         expr.append(".doubleValue() > ").append(this.m_splitPoint);
/* 251:    */       }
/* 252:    */     }
/* 253:400 */     return expr.toString();
/* 254:    */   }
/* 255:    */   
/* 256:    */   public final void setSplitPoint(Instances allInstances)
/* 257:    */   {
/* 258:409 */     double newSplitPoint = -1.797693134862316E+308D;
/* 259:413 */     if ((allInstances.attribute(this.m_attIndex).isNumeric()) && (this.m_numSubsets > 1))
/* 260:    */     {
/* 261:414 */       Enumeration<Instance> enu = allInstances.enumerateInstances();
/* 262:415 */       while (enu.hasMoreElements())
/* 263:    */       {
/* 264:416 */         Instance instance = (Instance)enu.nextElement();
/* 265:417 */         if (!instance.isMissing(this.m_attIndex))
/* 266:    */         {
/* 267:418 */           double tempValue = instance.value(this.m_attIndex);
/* 268:419 */           if ((Utils.gr(tempValue, newSplitPoint)) && (Utils.smOrEq(tempValue, this.m_splitPoint))) {
/* 269:421 */             newSplitPoint = tempValue;
/* 270:    */           }
/* 271:    */         }
/* 272:    */       }
/* 273:425 */       this.m_splitPoint = newSplitPoint;
/* 274:    */     }
/* 275:    */   }
/* 276:    */   
/* 277:    */   public final double[][] minsAndMaxs(Instances data, double[][] minsAndMaxs, int index)
/* 278:    */   {
/* 279:435 */     double[][] newMinsAndMaxs = new double[data.numAttributes()][2];
/* 280:437 */     for (int i = 0; i < data.numAttributes(); i++)
/* 281:    */     {
/* 282:438 */       newMinsAndMaxs[i][0] = minsAndMaxs[i][0];
/* 283:439 */       newMinsAndMaxs[i][1] = minsAndMaxs[i][1];
/* 284:440 */       if (i == this.m_attIndex) {
/* 285:441 */         if (data.attribute(this.m_attIndex).isNominal()) {
/* 286:442 */           newMinsAndMaxs[this.m_attIndex][1] = 1.0D;
/* 287:    */         } else {
/* 288:444 */           newMinsAndMaxs[this.m_attIndex][(1 - index)] = this.m_splitPoint;
/* 289:    */         }
/* 290:    */       }
/* 291:    */     }
/* 292:449 */     return newMinsAndMaxs;
/* 293:    */   }
/* 294:    */   
/* 295:    */   public void resetDistribution(Instances data)
/* 296:    */     throws Exception
/* 297:    */   {
/* 298:458 */     Instances insts = new Instances(data, data.numInstances());
/* 299:459 */     for (int i = 0; i < data.numInstances(); i++) {
/* 300:460 */       if (whichSubset(data.instance(i)) > -1) {
/* 301:461 */         insts.add(data.instance(i));
/* 302:    */       }
/* 303:    */     }
/* 304:464 */     Distribution newD = new Distribution(insts, this);
/* 305:465 */     newD.addInstWithUnknown(data, this.m_attIndex);
/* 306:466 */     this.m_distribution = newD;
/* 307:    */   }
/* 308:    */   
/* 309:    */   public final double[] weights(Instance instance)
/* 310:    */   {
/* 311:479 */     if (instance.isMissing(this.m_attIndex))
/* 312:    */     {
/* 313:480 */       double[] weights = new double[this.m_numSubsets];
/* 314:481 */       for (int i = 0; i < this.m_numSubsets; i++) {
/* 315:482 */         weights[i] = (this.m_distribution.perBag(i) / this.m_distribution.total());
/* 316:    */       }
/* 317:484 */       return weights;
/* 318:    */     }
/* 319:486 */     return null;
/* 320:    */   }
/* 321:    */   
/* 322:    */   public final int whichSubset(Instance instance)
/* 323:    */     throws Exception
/* 324:    */   {
/* 325:499 */     if (instance.isMissing(this.m_attIndex)) {
/* 326:500 */       return -1;
/* 327:    */     }
/* 328:502 */     if (instance.attribute(this.m_attIndex).isNominal()) {
/* 329:503 */       return (int)instance.value(this.m_attIndex);
/* 330:    */     }
/* 331:504 */     if (Utils.smOrEq(instance.value(this.m_attIndex), this.m_splitPoint)) {
/* 332:505 */       return 0;
/* 333:    */     }
/* 334:507 */     return 1;
/* 335:    */   }
/* 336:    */   
/* 337:    */   public String getRevision()
/* 338:    */   {
/* 339:519 */     return RevisionUtils.extract("$Revision: 10531 $");
/* 340:    */   }
/* 341:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.j48.C45Split
 * JD-Core Version:    0.7.0.1
 */