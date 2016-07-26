/*   1:    */ package weka.associations;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.Hashtable;
/*   5:    */ import java.util.Random;
/*   6:    */ import weka.core.Attribute;
/*   7:    */ import weka.core.Instances;
/*   8:    */ import weka.core.RevisionHandler;
/*   9:    */ import weka.core.RevisionUtils;
/*  10:    */ import weka.core.SpecialFunctions;
/*  11:    */ import weka.core.Utils;
/*  12:    */ 
/*  13:    */ public class PriorEstimation
/*  14:    */   implements Serializable, RevisionHandler
/*  15:    */ {
/*  16:    */   private static final long serialVersionUID = 5570863216522496271L;
/*  17:    */   protected int m_numRandRules;
/*  18:    */   protected int m_numIntervals;
/*  19:    */   protected static final int SEED = 0;
/*  20:    */   protected static final int MAX_N = 1024;
/*  21:    */   protected Random m_randNum;
/*  22:    */   protected Instances m_instances;
/*  23:    */   protected boolean m_CARs;
/*  24:    */   protected Hashtable<String, Double> m_distribution;
/*  25:    */   protected Hashtable<Double, Double> m_priors;
/*  26:    */   protected double m_sum;
/*  27:    */   protected double[] m_midPoints;
/*  28:    */   
/*  29:    */   public PriorEstimation(Instances instances, int numRules, int numIntervals, boolean car)
/*  30:    */   {
/*  31:104 */     this.m_instances = instances;
/*  32:105 */     this.m_CARs = car;
/*  33:106 */     this.m_numRandRules = numRules;
/*  34:107 */     this.m_numIntervals = numIntervals;
/*  35:108 */     this.m_randNum = this.m_instances.getRandomNumberGenerator(0L);
/*  36:    */   }
/*  37:    */   
/*  38:    */   public final void generateDistribution()
/*  39:    */     throws Exception
/*  40:    */   {
/*  41:119 */     int maxLength = this.m_instances.numAttributes();
/*  42:    */     
/*  43:121 */     this.m_distribution = new Hashtable(maxLength * this.m_numIntervals);
/*  44:123 */     if (this.m_instances.numAttributes() == 0) {
/*  45:124 */       throw new Exception("Dataset has no attributes!");
/*  46:    */     }
/*  47:126 */     if (this.m_instances.numAttributes() >= 1024) {
/*  48:127 */       throw new Exception("Dataset has to many attributes for prior estimation!");
/*  49:    */     }
/*  50:130 */     if (this.m_instances.numInstances() == 0) {
/*  51:131 */       throw new Exception("Dataset has no instances!");
/*  52:    */     }
/*  53:133 */     for (int h = 0; h < maxLength; h++) {
/*  54:134 */       if (this.m_instances.attribute(h).isNumeric()) {
/*  55:135 */         throw new Exception("Can't handle numeric attributes!");
/*  56:    */       }
/*  57:    */     }
/*  58:138 */     if ((this.m_numIntervals == 0) || (this.m_numRandRules == 0)) {
/*  59:139 */       throw new Exception("Prior initialisation impossible");
/*  60:    */     }
/*  61:143 */     midPoints();
/*  62:147 */     for (int i = 1; i <= maxLength; i++)
/*  63:    */     {
/*  64:148 */       this.m_sum = 0.0D;
/*  65:149 */       int j = 0;
/*  66:150 */       while (j < this.m_numRandRules)
/*  67:    */       {
/*  68:151 */         boolean jump = false;
/*  69:    */         RuleItem current;
/*  70:    */         int[] itemArray;
/*  71:    */         RuleItem current;
/*  72:152 */         if (!this.m_CARs)
/*  73:    */         {
/*  74:153 */           int[] itemArray = randomRule(maxLength, i, this.m_randNum);
/*  75:154 */           current = splitItemSet(this.m_randNum.nextInt(i), itemArray);
/*  76:    */         }
/*  77:    */         else
/*  78:    */         {
/*  79:156 */           itemArray = randomCARule(maxLength, i, this.m_randNum);
/*  80:157 */           current = addCons(itemArray);
/*  81:    */         }
/*  82:159 */         int[] ruleItem = new int[maxLength];
/*  83:160 */         for (int k = 0; k < itemArray.length; k++) {
/*  84:161 */           if (current.m_premise.m_items[k] != -1) {
/*  85:162 */             ruleItem[k] = current.m_premise.m_items[k];
/*  86:163 */           } else if (current.m_consequence.m_items[k] != -1) {
/*  87:164 */             ruleItem[k] = current.m_consequence.m_items[k];
/*  88:    */           } else {
/*  89:166 */             ruleItem[k] = -1;
/*  90:    */           }
/*  91:    */         }
/*  92:169 */         ItemSet rule = new ItemSet(ruleItem);
/*  93:170 */         updateCounters(rule);
/*  94:171 */         int ruleCounter = rule.m_counter;
/*  95:172 */         if (ruleCounter > 0) {
/*  96:173 */           jump = true;
/*  97:    */         }
/*  98:175 */         updateCounters(current.m_premise);
/*  99:176 */         j++;
/* 100:177 */         if (jump) {
/* 101:178 */           buildDistribution(ruleCounter / current.m_premise.m_counter, i);
/* 102:    */         }
/* 103:    */       }
/* 104:184 */       if (this.m_sum > 0.0D)
/* 105:    */       {
/* 106:185 */         for (double m_midPoint : this.m_midPoints)
/* 107:    */         {
/* 108:186 */           String key = String.valueOf(m_midPoint).concat(String.valueOf(i));
/* 109:    */           
/* 110:188 */           Double oldValue = (Double)this.m_distribution.remove(key);
/* 111:189 */           if (oldValue == null)
/* 112:    */           {
/* 113:190 */             this.m_distribution.put(key, new Double(1.0D / this.m_numIntervals));
/* 114:191 */             this.m_sum += 1.0D / this.m_numIntervals;
/* 115:    */           }
/* 116:    */           else
/* 117:    */           {
/* 118:193 */             this.m_distribution.put(key, oldValue);
/* 119:    */           }
/* 120:    */         }
/* 121:196 */         for (double m_midPoint : this.m_midPoints)
/* 122:    */         {
/* 123:197 */           double conf = 0.0D;
/* 124:198 */           String key = String.valueOf(m_midPoint).concat(String.valueOf(i));
/* 125:    */           
/* 126:200 */           Double oldValue = (Double)this.m_distribution.remove(key);
/* 127:201 */           if (oldValue != null)
/* 128:    */           {
/* 129:202 */             conf = oldValue.doubleValue() / this.m_sum;
/* 130:203 */             this.m_distribution.put(key, new Double(conf));
/* 131:    */           }
/* 132:    */         }
/* 133:    */       }
/* 134:    */       else
/* 135:    */       {
/* 136:207 */         for (double m_midPoint : this.m_midPoints)
/* 137:    */         {
/* 138:208 */           String key = String.valueOf(m_midPoint).concat(String.valueOf(i));
/* 139:    */           
/* 140:210 */           this.m_distribution.put(key, new Double(1.0D / this.m_numIntervals));
/* 141:    */         }
/* 142:    */       }
/* 143:    */     }
/* 144:    */   }
/* 145:    */   
/* 146:    */   public final int[] randomRule(int maxLength, int actualLength, Random randNum)
/* 147:    */   {
/* 148:229 */     int[] itemArray = new int[maxLength];
/* 149:230 */     for (int k = 0; k < itemArray.length; k++) {
/* 150:231 */       itemArray[k] = -1;
/* 151:    */     }
/* 152:233 */     int help = actualLength;
/* 153:234 */     if (help == maxLength)
/* 154:    */     {
/* 155:235 */       help = 0;
/* 156:236 */       for (int h = 0; h < itemArray.length; h++) {
/* 157:237 */         itemArray[h] = this.m_randNum.nextInt(this.m_instances.attribute(h).numValues());
/* 158:    */       }
/* 159:    */     }
/* 160:241 */     while (help > 0)
/* 161:    */     {
/* 162:242 */       int mark = randNum.nextInt(maxLength);
/* 163:243 */       if (itemArray[mark] == -1)
/* 164:    */       {
/* 165:244 */         help--;
/* 166:245 */         itemArray[mark] = this.m_randNum.nextInt(this.m_instances.attribute(mark).numValues());
/* 167:    */       }
/* 168:    */     }
/* 169:249 */     return itemArray;
/* 170:    */   }
/* 171:    */   
/* 172:    */   public final int[] randomCARule(int maxLength, int actualLength, Random randNum)
/* 173:    */   {
/* 174:265 */     int[] itemArray = new int[maxLength];
/* 175:266 */     for (int k = 0; k < itemArray.length; k++) {
/* 176:267 */       itemArray[k] = -1;
/* 177:    */     }
/* 178:269 */     if (actualLength == 1) {
/* 179:270 */       return itemArray;
/* 180:    */     }
/* 181:272 */     int help = actualLength - 1;
/* 182:273 */     if (help == maxLength - 1)
/* 183:    */     {
/* 184:274 */       help = 0;
/* 185:275 */       for (int h = 0; h < itemArray.length; h++) {
/* 186:276 */         if (h != this.m_instances.classIndex()) {
/* 187:277 */           itemArray[h] = this.m_randNum.nextInt(this.m_instances.attribute(h).numValues());
/* 188:    */         }
/* 189:    */       }
/* 190:    */     }
/* 191:282 */     while (help > 0)
/* 192:    */     {
/* 193:283 */       int mark = randNum.nextInt(maxLength);
/* 194:284 */       if ((itemArray[mark] == -1) && (mark != this.m_instances.classIndex()))
/* 195:    */       {
/* 196:285 */         help--;
/* 197:286 */         itemArray[mark] = this.m_randNum.nextInt(this.m_instances.attribute(mark).numValues());
/* 198:    */       }
/* 199:    */     }
/* 200:290 */     return itemArray;
/* 201:    */   }
/* 202:    */   
/* 203:    */   public final void buildDistribution(double conf, double length)
/* 204:    */   {
/* 205:303 */     double mPoint = findIntervall(conf);
/* 206:304 */     String key = String.valueOf(mPoint).concat(String.valueOf(length));
/* 207:305 */     this.m_sum += conf;
/* 208:306 */     Double oldValue = (Double)this.m_distribution.remove(key);
/* 209:307 */     if (oldValue != null) {
/* 210:308 */       conf += oldValue.doubleValue();
/* 211:    */     }
/* 212:310 */     this.m_distribution.put(key, new Double(conf));
/* 213:    */   }
/* 214:    */   
/* 215:    */   public final double findIntervall(double conf)
/* 216:    */   {
/* 217:322 */     if (conf == 1.0D) {
/* 218:323 */       return this.m_midPoints[(this.m_midPoints.length - 1)];
/* 219:    */     }
/* 220:325 */     int end = this.m_midPoints.length - 1;
/* 221:326 */     int start = 0;
/* 222:327 */     while (Math.abs(end - start) > 1)
/* 223:    */     {
/* 224:328 */       int mid = (start + end) / 2;
/* 225:329 */       if (conf > this.m_midPoints[mid]) {
/* 226:330 */         start = mid + 1;
/* 227:    */       }
/* 228:332 */       if (conf < this.m_midPoints[mid]) {
/* 229:333 */         end = mid - 1;
/* 230:    */       }
/* 231:335 */       if (conf == this.m_midPoints[mid]) {
/* 232:336 */         return this.m_midPoints[mid];
/* 233:    */       }
/* 234:    */     }
/* 235:339 */     if (Math.abs(conf - this.m_midPoints[start]) <= Math.abs(conf - this.m_midPoints[end])) {
/* 236:341 */       return this.m_midPoints[start];
/* 237:    */     }
/* 238:343 */     return this.m_midPoints[end];
/* 239:    */   }
/* 240:    */   
/* 241:    */   public final double calculatePriorSum(boolean weighted, double mPoint)
/* 242:    */   {
/* 243:357 */     double sum = 0.0D;double max = logbinomialCoefficient(this.m_instances.numAttributes(), this.m_instances.numAttributes() / 2);
/* 244:360 */     for (int i = 1; i <= this.m_instances.numAttributes(); i++) {
/* 245:362 */       if (weighted)
/* 246:    */       {
/* 247:363 */         String key = String.valueOf(mPoint).concat(String.valueOf(i));
/* 248:    */         
/* 249:365 */         Double hashValue = (Double)this.m_distribution.get(key);
/* 250:    */         double distr;
/* 251:    */         double distr;
/* 252:367 */         if (hashValue != null) {
/* 253:368 */           distr = hashValue.doubleValue();
/* 254:    */         } else {
/* 255:370 */           distr = 0.0D;
/* 256:    */         }
/* 257:373 */         if (distr != 0.0D)
/* 258:    */         {
/* 259:374 */           double addend = Utils.log2(distr) - max + Utils.log2(Math.pow(2.0D, i) - 1.0D) + logbinomialCoefficient(this.m_instances.numAttributes(), i);
/* 260:    */           
/* 261:    */ 
/* 262:377 */           sum += Math.pow(2.0D, addend);
/* 263:    */         }
/* 264:    */       }
/* 265:    */       else
/* 266:    */       {
/* 267:380 */         double addend = Utils.log2(Math.pow(2.0D, i) - 1.0D) - max + logbinomialCoefficient(this.m_instances.numAttributes(), i);
/* 268:    */         
/* 269:382 */         sum += Math.pow(2.0D, addend);
/* 270:    */       }
/* 271:    */     }
/* 272:385 */     return sum;
/* 273:    */   }
/* 274:    */   
/* 275:    */   public static final double logbinomialCoefficient(int upperIndex, int lowerIndex)
/* 276:    */   {
/* 277:398 */     double result = 1.0D;
/* 278:399 */     if ((upperIndex == lowerIndex) || (lowerIndex == 0)) {
/* 279:400 */       return result;
/* 280:    */     }
/* 281:402 */     result = SpecialFunctions.log2Binomial(upperIndex, lowerIndex);
/* 282:403 */     return result;
/* 283:    */   }
/* 284:    */   
/* 285:    */   public final Hashtable<Double, Double> estimatePrior()
/* 286:    */     throws Exception
/* 287:    */   {
/* 288:416 */     Hashtable<Double, Double> m_priors = new Hashtable(this.m_numIntervals);
/* 289:    */     
/* 290:418 */     double denominator = calculatePriorSum(false, 1.0D);
/* 291:419 */     generateDistribution();
/* 292:420 */     for (int i = 0; i < this.m_numIntervals; i++)
/* 293:    */     {
/* 294:421 */       double mPoint = this.m_midPoints[i];
/* 295:422 */       double prior = calculatePriorSum(true, mPoint) / denominator;
/* 296:423 */       m_priors.put(new Double(mPoint), new Double(prior));
/* 297:    */     }
/* 298:425 */     return m_priors;
/* 299:    */   }
/* 300:    */   
/* 301:    */   public final void midPoints()
/* 302:    */   {
/* 303:434 */     this.m_midPoints = new double[this.m_numIntervals];
/* 304:435 */     for (int i = 0; i < this.m_numIntervals; i++) {
/* 305:436 */       this.m_midPoints[i] = midPoint(1.0D / this.m_numIntervals, i);
/* 306:    */     }
/* 307:    */   }
/* 308:    */   
/* 309:    */   public double midPoint(double size, int number)
/* 310:    */   {
/* 311:450 */     return size * number + size / 2.0D;
/* 312:    */   }
/* 313:    */   
/* 314:    */   public final double[] getMidPoints()
/* 315:    */   {
/* 316:460 */     return this.m_midPoints;
/* 317:    */   }
/* 318:    */   
/* 319:    */   public final RuleItem splitItemSet(int premiseLength, int[] itemArray)
/* 320:    */   {
/* 321:474 */     int[] cons = new int[this.m_instances.numAttributes()];
/* 322:475 */     System.arraycopy(itemArray, 0, cons, 0, itemArray.length);
/* 323:476 */     int help = premiseLength;
/* 324:477 */     while (help > 0)
/* 325:    */     {
/* 326:478 */       int mark = this.m_randNum.nextInt(itemArray.length);
/* 327:479 */       if (cons[mark] != -1)
/* 328:    */       {
/* 329:480 */         help--;
/* 330:481 */         cons[mark] = -1;
/* 331:    */       }
/* 332:    */     }
/* 333:484 */     if (premiseLength == 0) {
/* 334:485 */       for (int i = 0; i < itemArray.length; i++) {
/* 335:486 */         itemArray[i] = -1;
/* 336:    */       }
/* 337:    */     } else {
/* 338:489 */       for (int i = 0; i < itemArray.length; i++) {
/* 339:490 */         if (cons[i] != -1) {
/* 340:491 */           itemArray[i] = -1;
/* 341:    */         }
/* 342:    */       }
/* 343:    */     }
/* 344:495 */     ItemSet premise = new ItemSet(itemArray);
/* 345:496 */     ItemSet consequence = new ItemSet(cons);
/* 346:497 */     RuleItem current = new RuleItem();
/* 347:498 */     current.m_premise = premise;
/* 348:499 */     current.m_consequence = consequence;
/* 349:500 */     return current;
/* 350:    */   }
/* 351:    */   
/* 352:    */   public final RuleItem addCons(int[] itemArray)
/* 353:    */   {
/* 354:513 */     ItemSet premise = new ItemSet(itemArray);
/* 355:514 */     int[] cons = new int[itemArray.length];
/* 356:515 */     for (int i = 0; i < itemArray.length; i++) {
/* 357:516 */       cons[i] = -1;
/* 358:    */     }
/* 359:518 */     cons[this.m_instances.classIndex()] = this.m_randNum.nextInt(this.m_instances.attribute(this.m_instances.classIndex()).numValues());
/* 360:    */     
/* 361:520 */     ItemSet consequence = new ItemSet(cons);
/* 362:521 */     RuleItem current = new RuleItem();
/* 363:522 */     current.m_premise = premise;
/* 364:523 */     current.m_consequence = consequence;
/* 365:524 */     return current;
/* 366:    */   }
/* 367:    */   
/* 368:    */   public final void updateCounters(ItemSet itemSet)
/* 369:    */   {
/* 370:534 */     for (int i = 0; i < this.m_instances.numInstances(); i++) {
/* 371:535 */       itemSet.upDateCounter(this.m_instances.instance(i));
/* 372:    */     }
/* 373:    */   }
/* 374:    */   
/* 375:    */   public String getRevision()
/* 376:    */   {
/* 377:546 */     return RevisionUtils.extract("$Revision: 10201 $");
/* 378:    */   }
/* 379:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.associations.PriorEstimation
 * JD-Core Version:    0.7.0.1
 */