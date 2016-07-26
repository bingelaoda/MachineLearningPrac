/*   1:    */ package weka.classifiers.trees.j48;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import weka.core.Attribute;
/*   5:    */ import weka.core.Instance;
/*   6:    */ import weka.core.Instances;
/*   7:    */ import weka.core.RevisionUtils;
/*   8:    */ import weka.core.Utils;
/*   9:    */ 
/*  10:    */ public class BinC45Split
/*  11:    */   extends ClassifierSplitModel
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = -1278776919563022474L;
/*  14:    */   protected final int m_attIndex;
/*  15:    */   protected final int m_minNoObj;
/*  16:    */   protected final boolean m_useMDLcorrection;
/*  17:    */   protected double m_splitPoint;
/*  18:    */   protected double m_infoGain;
/*  19:    */   protected double m_gainRatio;
/*  20:    */   protected final double m_sumOfWeights;
/*  21: 64 */   protected static InfoGainSplitCrit m_infoGainCrit = new InfoGainSplitCrit();
/*  22: 67 */   protected static GainRatioSplitCrit m_gainRatioCrit = new GainRatioSplitCrit();
/*  23:    */   
/*  24:    */   public BinC45Split(int attIndex, int minNoObj, double sumOfWeights, boolean useMDLcorrection)
/*  25:    */   {
/*  26: 76 */     this.m_attIndex = attIndex;
/*  27:    */     
/*  28:    */ 
/*  29: 79 */     this.m_minNoObj = minNoObj;
/*  30:    */     
/*  31:    */ 
/*  32: 82 */     this.m_sumOfWeights = sumOfWeights;
/*  33:    */     
/*  34:    */ 
/*  35: 85 */     this.m_useMDLcorrection = useMDLcorrection;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void buildClassifier(Instances trainInstances)
/*  39:    */     throws Exception
/*  40:    */   {
/*  41: 97 */     this.m_numSubsets = 0;
/*  42: 98 */     this.m_splitPoint = 1.7976931348623157E+308D;
/*  43: 99 */     this.m_infoGain = 0.0D;
/*  44:100 */     this.m_gainRatio = 0.0D;
/*  45:104 */     if (trainInstances.attribute(this.m_attIndex).isNominal())
/*  46:    */     {
/*  47:105 */       handleEnumeratedAttribute(trainInstances);
/*  48:    */     }
/*  49:    */     else
/*  50:    */     {
/*  51:107 */       trainInstances.sort(trainInstances.attribute(this.m_attIndex));
/*  52:108 */       handleNumericAttribute(trainInstances);
/*  53:    */     }
/*  54:    */   }
/*  55:    */   
/*  56:    */   public final int attIndex()
/*  57:    */   {
/*  58:117 */     return this.m_attIndex;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public double splitPoint()
/*  62:    */   {
/*  63:126 */     return this.m_splitPoint;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public final double gainRatio()
/*  67:    */   {
/*  68:133 */     return this.m_gainRatio;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public final double classProb(int classIndex, Instance instance, int theSubset)
/*  72:    */     throws Exception
/*  73:    */   {
/*  74:145 */     if (theSubset <= -1)
/*  75:    */     {
/*  76:146 */       double[] weights = weights(instance);
/*  77:147 */       if (weights == null) {
/*  78:148 */         return this.m_distribution.prob(classIndex);
/*  79:    */       }
/*  80:150 */       double prob = 0.0D;
/*  81:151 */       for (int i = 0; i < weights.length; i++) {
/*  82:152 */         prob += weights[i] * this.m_distribution.prob(classIndex, i);
/*  83:    */       }
/*  84:154 */       return prob;
/*  85:    */     }
/*  86:157 */     if (Utils.gr(this.m_distribution.perBag(theSubset), 0.0D)) {
/*  87:158 */       return this.m_distribution.prob(classIndex, theSubset);
/*  88:    */     }
/*  89:160 */     return this.m_distribution.prob(classIndex);
/*  90:    */   }
/*  91:    */   
/*  92:    */   private void handleEnumeratedAttribute(Instances trainInstances)
/*  93:    */     throws Exception
/*  94:    */   {
/*  95:179 */     int numAttValues = trainInstances.attribute(this.m_attIndex).numValues();
/*  96:180 */     Distribution newDistribution = new Distribution(numAttValues, trainInstances.numClasses());
/*  97:    */     
/*  98:    */ 
/*  99:    */ 
/* 100:184 */     Enumeration<Instance> enu = trainInstances.enumerateInstances();
/* 101:185 */     while (enu.hasMoreElements())
/* 102:    */     {
/* 103:186 */       Instance instance = (Instance)enu.nextElement();
/* 104:187 */       if (!instance.isMissing(this.m_attIndex)) {
/* 105:188 */         newDistribution.add((int)instance.value(this.m_attIndex), instance);
/* 106:    */       }
/* 107:    */     }
/* 108:191 */     this.m_distribution = newDistribution;
/* 109:194 */     for (int i = 0; i < numAttValues; i++) {
/* 110:196 */       if (Utils.grOrEq(newDistribution.perBag(i), this.m_minNoObj))
/* 111:    */       {
/* 112:197 */         Distribution secondDistribution = new Distribution(newDistribution, i);
/* 113:201 */         if (secondDistribution.check(this.m_minNoObj))
/* 114:    */         {
/* 115:202 */           this.m_numSubsets = 2;
/* 116:203 */           double currIG = m_infoGainCrit.splitCritValue(secondDistribution, this.m_sumOfWeights);
/* 117:    */           
/* 118:205 */           double currGR = m_gainRatioCrit.splitCritValue(secondDistribution, this.m_sumOfWeights, currIG);
/* 119:207 */           if ((i == 0) || (Utils.gr(currGR, this.m_gainRatio)))
/* 120:    */           {
/* 121:208 */             this.m_gainRatio = currGR;
/* 122:209 */             this.m_infoGain = currIG;
/* 123:210 */             this.m_splitPoint = i;
/* 124:211 */             this.m_distribution = secondDistribution;
/* 125:    */           }
/* 126:    */         }
/* 127:    */       }
/* 128:    */     }
/* 129:    */   }
/* 130:    */   
/* 131:    */   private void handleNumericAttribute(Instances trainInstances)
/* 132:    */     throws Exception
/* 133:    */   {
/* 134:227 */     int next = 1;
/* 135:228 */     int last = 0;
/* 136:229 */     int index = 0;
/* 137:230 */     int splitIndex = -1;
/* 138:    */     
/* 139:    */ 
/* 140:    */ 
/* 141:    */ 
/* 142:    */ 
/* 143:    */ 
/* 144:    */ 
/* 145:238 */     this.m_distribution = new Distribution(2, trainInstances.numClasses());
/* 146:    */     
/* 147:    */ 
/* 148:241 */     Enumeration<Instance> enu = trainInstances.enumerateInstances();
/* 149:242 */     int i = 0;
/* 150:243 */     while (enu.hasMoreElements())
/* 151:    */     {
/* 152:244 */       Instance instance = (Instance)enu.nextElement();
/* 153:245 */       if (instance.isMissing(this.m_attIndex)) {
/* 154:    */         break;
/* 155:    */       }
/* 156:248 */       this.m_distribution.add(1, instance);
/* 157:249 */       i++;
/* 158:    */     }
/* 159:251 */     int firstMiss = i;
/* 160:    */     
/* 161:    */ 
/* 162:    */ 
/* 163:255 */     double minSplit = 0.1D * this.m_distribution.total() / trainInstances.numClasses();
/* 164:256 */     if (Utils.smOrEq(minSplit, this.m_minNoObj)) {
/* 165:257 */       minSplit = this.m_minNoObj;
/* 166:258 */     } else if (Utils.gr(minSplit, 25.0D)) {
/* 167:259 */       minSplit = 25.0D;
/* 168:    */     }
/* 169:263 */     if (Utils.sm(firstMiss, 2.0D * minSplit)) {
/* 170:264 */       return;
/* 171:    */     }
/* 172:269 */     double defaultEnt = m_infoGainCrit.oldEnt(this.m_distribution);
/* 173:270 */     while (next < firstMiss)
/* 174:    */     {
/* 175:272 */       if (trainInstances.instance(next - 1).value(this.m_attIndex) + 1.E-005D < trainInstances.instance(next).value(this.m_attIndex))
/* 176:    */       {
/* 177:277 */         this.m_distribution.shiftRange(1, 0, trainInstances, last, next);
/* 178:281 */         if ((Utils.grOrEq(this.m_distribution.perBag(0), minSplit)) && (Utils.grOrEq(this.m_distribution.perBag(1), minSplit)))
/* 179:    */         {
/* 180:283 */           double currentInfoGain = m_infoGainCrit.splitCritValue(this.m_distribution, this.m_sumOfWeights, defaultEnt);
/* 181:285 */           if (Utils.gr(currentInfoGain, this.m_infoGain))
/* 182:    */           {
/* 183:286 */             this.m_infoGain = currentInfoGain;
/* 184:287 */             splitIndex = next - 1;
/* 185:    */           }
/* 186:289 */           index++;
/* 187:    */         }
/* 188:291 */         last = next;
/* 189:    */       }
/* 190:293 */       next++;
/* 191:    */     }
/* 192:297 */     if (index == 0) {
/* 193:298 */       return;
/* 194:    */     }
/* 195:302 */     if (this.m_useMDLcorrection) {
/* 196:303 */       this.m_infoGain -= Utils.log2(index) / this.m_sumOfWeights;
/* 197:    */     }
/* 198:305 */     if (Utils.smOrEq(this.m_infoGain, 0.0D)) {
/* 199:306 */       return;
/* 200:    */     }
/* 201:311 */     this.m_numSubsets = 2;
/* 202:312 */     this.m_splitPoint = ((trainInstances.instance(splitIndex + 1).value(this.m_attIndex) + trainInstances.instance(splitIndex).value(this.m_attIndex)) / 2.0D);
/* 203:317 */     if (this.m_splitPoint == trainInstances.instance(splitIndex + 1).value(this.m_attIndex)) {
/* 204:319 */       this.m_splitPoint = trainInstances.instance(splitIndex).value(this.m_attIndex);
/* 205:    */     }
/* 206:323 */     this.m_distribution = new Distribution(2, trainInstances.numClasses());
/* 207:324 */     this.m_distribution.addRange(0, trainInstances, 0, splitIndex + 1);
/* 208:325 */     this.m_distribution.addRange(1, trainInstances, splitIndex + 1, firstMiss);
/* 209:    */     
/* 210:    */ 
/* 211:328 */     this.m_gainRatio = m_gainRatioCrit.splitCritValue(this.m_distribution, this.m_sumOfWeights, this.m_infoGain);
/* 212:    */   }
/* 213:    */   
/* 214:    */   public final double infoGain()
/* 215:    */   {
/* 216:337 */     return this.m_infoGain;
/* 217:    */   }
/* 218:    */   
/* 219:    */   public final String leftSide(Instances data)
/* 220:    */   {
/* 221:349 */     return data.attribute(this.m_attIndex).name();
/* 222:    */   }
/* 223:    */   
/* 224:    */   public final String rightSide(int index, Instances data)
/* 225:    */   {
/* 226:362 */     StringBuffer text = new StringBuffer();
/* 227:363 */     if (data.attribute(this.m_attIndex).isNominal())
/* 228:    */     {
/* 229:364 */       if (index == 0) {
/* 230:365 */         text.append(" = " + data.attribute(this.m_attIndex).value((int)this.m_splitPoint));
/* 231:    */       } else {
/* 232:368 */         text.append(" != " + data.attribute(this.m_attIndex).value((int)this.m_splitPoint));
/* 233:    */       }
/* 234:    */     }
/* 235:371 */     else if (index == 0) {
/* 236:372 */       text.append(" <= " + this.m_splitPoint);
/* 237:    */     } else {
/* 238:374 */       text.append(" > " + this.m_splitPoint);
/* 239:    */     }
/* 240:377 */     return text.toString();
/* 241:    */   }
/* 242:    */   
/* 243:    */   public final String sourceExpression(int index, Instances data)
/* 244:    */   {
/* 245:391 */     StringBuffer expr = null;
/* 246:392 */     if (index < 0) {
/* 247:393 */       return "i[" + this.m_attIndex + "] == null";
/* 248:    */     }
/* 249:395 */     if (data.attribute(this.m_attIndex).isNominal())
/* 250:    */     {
/* 251:396 */       if (index == 0) {
/* 252:397 */         expr = new StringBuffer("i[");
/* 253:    */       } else {
/* 254:399 */         expr = new StringBuffer("!i[");
/* 255:    */       }
/* 256:401 */       expr.append(this.m_attIndex).append("]");
/* 257:402 */       expr.append(".equals(\"").append(data.attribute(this.m_attIndex).value((int)this.m_splitPoint)).append("\")");
/* 258:    */     }
/* 259:    */     else
/* 260:    */     {
/* 261:406 */       expr = new StringBuffer("((Double) i[");
/* 262:407 */       expr.append(this.m_attIndex).append("])");
/* 263:408 */       if (index == 0) {
/* 264:409 */         expr.append(".doubleValue() <= ").append(this.m_splitPoint);
/* 265:    */       } else {
/* 266:411 */         expr.append(".doubleValue() > ").append(this.m_splitPoint);
/* 267:    */       }
/* 268:    */     }
/* 269:414 */     return expr.toString();
/* 270:    */   }
/* 271:    */   
/* 272:    */   public final void setSplitPoint(Instances allInstances)
/* 273:    */   {
/* 274:423 */     double newSplitPoint = -1.797693134862316E+308D;
/* 275:427 */     if ((!allInstances.attribute(this.m_attIndex).isNominal()) && (this.m_numSubsets > 1))
/* 276:    */     {
/* 277:428 */       Enumeration<Instance> enu = allInstances.enumerateInstances();
/* 278:429 */       while (enu.hasMoreElements())
/* 279:    */       {
/* 280:430 */         Instance instance = (Instance)enu.nextElement();
/* 281:431 */         if (!instance.isMissing(this.m_attIndex))
/* 282:    */         {
/* 283:432 */           double tempValue = instance.value(this.m_attIndex);
/* 284:433 */           if ((Utils.gr(tempValue, newSplitPoint)) && (Utils.smOrEq(tempValue, this.m_splitPoint))) {
/* 285:435 */             newSplitPoint = tempValue;
/* 286:    */           }
/* 287:    */         }
/* 288:    */       }
/* 289:439 */       this.m_splitPoint = newSplitPoint;
/* 290:    */     }
/* 291:    */   }
/* 292:    */   
/* 293:    */   public void resetDistribution(Instances data)
/* 294:    */     throws Exception
/* 295:    */   {
/* 296:449 */     Instances insts = new Instances(data, data.numInstances());
/* 297:450 */     for (int i = 0; i < data.numInstances(); i++) {
/* 298:451 */       if (whichSubset(data.instance(i)) > -1) {
/* 299:452 */         insts.add(data.instance(i));
/* 300:    */       }
/* 301:    */     }
/* 302:455 */     Distribution newD = new Distribution(insts, this);
/* 303:456 */     newD.addInstWithUnknown(data, this.m_attIndex);
/* 304:457 */     this.m_distribution = newD;
/* 305:    */   }
/* 306:    */   
/* 307:    */   public final double[] weights(Instance instance)
/* 308:    */   {
/* 309:470 */     if (instance.isMissing(this.m_attIndex))
/* 310:    */     {
/* 311:471 */       double[] weights = new double[this.m_numSubsets];
/* 312:472 */       for (int i = 0; i < this.m_numSubsets; i++) {
/* 313:473 */         weights[i] = (this.m_distribution.perBag(i) / this.m_distribution.total());
/* 314:    */       }
/* 315:475 */       return weights;
/* 316:    */     }
/* 317:477 */     return null;
/* 318:    */   }
/* 319:    */   
/* 320:    */   public final int whichSubset(Instance instance)
/* 321:    */     throws Exception
/* 322:    */   {
/* 323:491 */     if (instance.isMissing(this.m_attIndex)) {
/* 324:492 */       return -1;
/* 325:    */     }
/* 326:494 */     if (instance.attribute(this.m_attIndex).isNominal())
/* 327:    */     {
/* 328:495 */       if ((int)this.m_splitPoint == (int)instance.value(this.m_attIndex)) {
/* 329:496 */         return 0;
/* 330:    */       }
/* 331:498 */       return 1;
/* 332:    */     }
/* 333:500 */     if (Utils.smOrEq(instance.value(this.m_attIndex), this.m_splitPoint)) {
/* 334:501 */       return 0;
/* 335:    */     }
/* 336:503 */     return 1;
/* 337:    */   }
/* 338:    */   
/* 339:    */   public String getRevision()
/* 340:    */   {
/* 341:515 */     return RevisionUtils.extract("$Revision: 10531 $");
/* 342:    */   }
/* 343:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.j48.BinC45Split
 * JD-Core Version:    0.7.0.1
 */