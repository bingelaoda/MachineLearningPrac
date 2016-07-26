/*   1:    */ package weka.classifiers.misc;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.classifiers.AbstractClassifier;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.Capabilities;
/*   9:    */ import weka.core.Capabilities.Capability;
/*  10:    */ import weka.core.ContingencyTables;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.Option;
/*  14:    */ import weka.core.OptionHandler;
/*  15:    */ import weka.core.RevisionUtils;
/*  16:    */ import weka.core.TechnicalInformation;
/*  17:    */ import weka.core.TechnicalInformation.Field;
/*  18:    */ import weka.core.TechnicalInformation.Type;
/*  19:    */ import weka.core.TechnicalInformationHandler;
/*  20:    */ import weka.core.Utils;
/*  21:    */ import weka.core.WeightedInstancesHandler;
/*  22:    */ 
/*  23:    */ public class VFI
/*  24:    */   extends AbstractClassifier
/*  25:    */   implements OptionHandler, WeightedInstancesHandler, TechnicalInformationHandler
/*  26:    */ {
/*  27:    */   static final long serialVersionUID = 8081692166331321866L;
/*  28:    */   protected int m_ClassIndex;
/*  29:    */   protected int m_NumClasses;
/*  30:154 */   protected Instances m_Instances = null;
/*  31:    */   protected double[][][] m_counts;
/*  32:    */   protected double[] m_globalCounts;
/*  33:    */   protected double[][] m_intervalBounds;
/*  34:    */   protected double m_maxEntrop;
/*  35:169 */   protected boolean m_weightByConfidence = true;
/*  36:172 */   protected double m_bias = -0.6D;
/*  37:174 */   private double TINY = 9.999999999999999E-012D;
/*  38:    */   
/*  39:    */   public String globalInfo()
/*  40:    */   {
/*  41:183 */     return "Classification by voting feature intervals. Intervals are constucted around each class for each attribute (basically discretization). Class counts are recorded for each interval on each attribute. Classification is by voting. For more info see:\n\n" + getTechnicalInformation().toString() + "\n\n" + "Have added a simple attribute weighting scheme. Higher weight is " + "assigned to more confident intervals, where confidence is a function " + "of entropy:\nweight (att_i) = (entropy of class distrib att_i / " + "max uncertainty)^-bias";
/*  42:    */   }
/*  43:    */   
/*  44:    */   public TechnicalInformation getTechnicalInformation()
/*  45:    */   {
/*  46:206 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  47:207 */     result.setValue(TechnicalInformation.Field.AUTHOR, "G. Demiroz and A. Guvenir");
/*  48:208 */     result.setValue(TechnicalInformation.Field.TITLE, "Classification by voting feature intervals");
/*  49:209 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "9th European Conference on Machine Learning");
/*  50:    */     
/*  51:211 */     result.setValue(TechnicalInformation.Field.YEAR, "1997");
/*  52:212 */     result.setValue(TechnicalInformation.Field.PAGES, "85-92");
/*  53:213 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Springer");
/*  54:    */     
/*  55:215 */     return result;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public Enumeration<Option> listOptions()
/*  59:    */   {
/*  60:226 */     Vector<Option> newVector = new Vector(2);
/*  61:    */     
/*  62:228 */     newVector.addElement(new Option("\tDon't weight voting intervals by confidence", "C", 0, "-C"));
/*  63:    */     
/*  64:230 */     newVector.addElement(new Option("\tSet exponential bias towards confident intervals\n\t(default = 0.6)", "B", 1, "-B <bias>"));
/*  65:    */     
/*  66:    */ 
/*  67:    */ 
/*  68:234 */     newVector.addAll(Collections.list(super.listOptions()));
/*  69:    */     
/*  70:236 */     return newVector.elements();
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void setOptions(String[] options)
/*  74:    */     throws Exception
/*  75:    */   {
/*  76:266 */     setWeightByConfidence(!Utils.getFlag('C', options));
/*  77:    */     
/*  78:268 */     String optionString = Utils.getOption('B', options);
/*  79:269 */     if (optionString.length() != 0)
/*  80:    */     {
/*  81:270 */       Double temp = new Double(optionString);
/*  82:271 */       setBias(temp.doubleValue());
/*  83:    */     }
/*  84:274 */     super.setOptions(options);
/*  85:    */     
/*  86:276 */     Utils.checkForRemainingOptions(options);
/*  87:    */   }
/*  88:    */   
/*  89:    */   public String weightByConfidenceTipText()
/*  90:    */   {
/*  91:286 */     return "Weight feature intervals by confidence";
/*  92:    */   }
/*  93:    */   
/*  94:    */   public void setWeightByConfidence(boolean c)
/*  95:    */   {
/*  96:295 */     this.m_weightByConfidence = c;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public boolean getWeightByConfidence()
/* 100:    */   {
/* 101:304 */     return this.m_weightByConfidence;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public String biasTipText()
/* 105:    */   {
/* 106:314 */     return "Strength of bias towards more confident features";
/* 107:    */   }
/* 108:    */   
/* 109:    */   public void setBias(double b)
/* 110:    */   {
/* 111:323 */     this.m_bias = (-b);
/* 112:    */   }
/* 113:    */   
/* 114:    */   public double getBias()
/* 115:    */   {
/* 116:332 */     return -this.m_bias;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public String[] getOptions()
/* 120:    */   {
/* 121:343 */     Vector<String> options = new Vector();
/* 122:345 */     if (!getWeightByConfidence()) {
/* 123:346 */       options.add("-C");
/* 124:    */     }
/* 125:349 */     options.add("-B");
/* 126:350 */     options.add("" + getBias());
/* 127:    */     
/* 128:352 */     Collections.addAll(options, super.getOptions());
/* 129:    */     
/* 130:354 */     return (String[])options.toArray(new String[0]);
/* 131:    */   }
/* 132:    */   
/* 133:    */   public Capabilities getCapabilities()
/* 134:    */   {
/* 135:364 */     Capabilities result = super.getCapabilities();
/* 136:365 */     result.disableAll();
/* 137:    */     
/* 138:    */ 
/* 139:368 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 140:369 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 141:370 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/* 142:371 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 143:    */     
/* 144:    */ 
/* 145:374 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 146:375 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 147:    */     
/* 148:    */ 
/* 149:378 */     result.setMinimumNumberInstances(0);
/* 150:    */     
/* 151:380 */     return result;
/* 152:    */   }
/* 153:    */   
/* 154:    */   public void buildClassifier(Instances instances)
/* 155:    */     throws Exception
/* 156:    */   {
/* 157:392 */     if (!this.m_weightByConfidence) {
/* 158:393 */       this.TINY = 0.0D;
/* 159:    */     }
/* 160:397 */     getCapabilities().testWithFail(instances);
/* 161:    */     
/* 162:    */ 
/* 163:400 */     instances = new Instances(instances);
/* 164:401 */     instances.deleteWithMissingClass();
/* 165:    */     
/* 166:403 */     this.m_ClassIndex = instances.classIndex();
/* 167:404 */     this.m_NumClasses = instances.numClasses();
/* 168:405 */     this.m_globalCounts = new double[this.m_NumClasses];
/* 169:406 */     this.m_maxEntrop = (Math.log(this.m_NumClasses) / Math.log(2.0D));
/* 170:    */     
/* 171:408 */     this.m_Instances = new Instances(instances, 0);
/* 172:    */     
/* 173:410 */     this.m_intervalBounds = new double[instances.numAttributes()][2 + 2 * this.m_NumClasses];
/* 174:412 */     for (int j = 0; j < instances.numAttributes(); j++)
/* 175:    */     {
/* 176:413 */       boolean alt = false;
/* 177:414 */       for (int i = 0; i < this.m_NumClasses * 2 + 2; i++) {
/* 178:415 */         if (i == 0)
/* 179:    */         {
/* 180:416 */           this.m_intervalBounds[j][i] = (-1.0D / 0.0D);
/* 181:    */         }
/* 182:417 */         else if (i == this.m_NumClasses * 2 + 1)
/* 183:    */         {
/* 184:418 */           this.m_intervalBounds[j][i] = (1.0D / 0.0D);
/* 185:    */         }
/* 186:420 */         else if (alt)
/* 187:    */         {
/* 188:421 */           this.m_intervalBounds[j][i] = (-1.0D / 0.0D);
/* 189:422 */           alt = false;
/* 190:    */         }
/* 191:    */         else
/* 192:    */         {
/* 193:424 */           this.m_intervalBounds[j][i] = (1.0D / 0.0D);
/* 194:425 */           alt = true;
/* 195:    */         }
/* 196:    */       }
/* 197:    */     }
/* 198:432 */     for (int j = 0; j < instances.numAttributes(); j++) {
/* 199:433 */       if ((j != this.m_ClassIndex) && (instances.attribute(j).isNumeric())) {
/* 200:434 */         for (int i = 0; i < instances.numInstances(); i++)
/* 201:    */         {
/* 202:435 */           Instance inst = instances.instance(i);
/* 203:436 */           if (!inst.isMissing(j))
/* 204:    */           {
/* 205:437 */             if (inst.value(j) < this.m_intervalBounds[j][((int)inst.classValue() * 2 + 1)]) {
/* 206:438 */               this.m_intervalBounds[j][((int)inst.classValue() * 2 + 1)] = inst.value(j);
/* 207:    */             }
/* 208:441 */             if (inst.value(j) > this.m_intervalBounds[j][((int)inst.classValue() * 2 + 2)]) {
/* 209:442 */               this.m_intervalBounds[j][((int)inst.classValue() * 2 + 2)] = inst.value(j);
/* 210:    */             }
/* 211:    */           }
/* 212:    */         }
/* 213:    */       }
/* 214:    */     }
/* 215:450 */     this.m_counts = new double[instances.numAttributes()][][];
/* 216:453 */     for (int i = 0; i < instances.numAttributes(); i++) {
/* 217:454 */       if (instances.attribute(i).isNumeric())
/* 218:    */       {
/* 219:455 */         int[] sortedIntervals = Utils.sort(this.m_intervalBounds[i]);
/* 220:    */         
/* 221:457 */         int count = 1;
/* 222:458 */         for (int j = 1; j < sortedIntervals.length; j++) {
/* 223:459 */           if (this.m_intervalBounds[i][sortedIntervals[j]] != this.m_intervalBounds[i][sortedIntervals[(j - 1)]]) {
/* 224:460 */             count++;
/* 225:    */           }
/* 226:    */         }
/* 227:463 */         double[] reordered = new double[count];
/* 228:464 */         count = 1;
/* 229:465 */         reordered[0] = this.m_intervalBounds[i][sortedIntervals[0]];
/* 230:466 */         for (int j = 1; j < sortedIntervals.length; j++) {
/* 231:467 */           if (this.m_intervalBounds[i][sortedIntervals[j]] != this.m_intervalBounds[i][sortedIntervals[(j - 1)]])
/* 232:    */           {
/* 233:468 */             reordered[count] = this.m_intervalBounds[i][sortedIntervals[j]];
/* 234:469 */             count++;
/* 235:    */           }
/* 236:    */         }
/* 237:472 */         this.m_intervalBounds[i] = reordered;
/* 238:473 */         this.m_counts[i] = new double[count][this.m_NumClasses];
/* 239:    */       }
/* 240:474 */       else if (i != this.m_ClassIndex)
/* 241:    */       {
/* 242:475 */         this.m_counts[i] = new double[instances.attribute(i).numValues()][this.m_NumClasses];
/* 243:    */       }
/* 244:    */     }
/* 245:480 */     for (int i = 0; i < instances.numInstances(); i++)
/* 246:    */     {
/* 247:481 */       Instance inst = instances.instance(i);
/* 248:482 */       this.m_globalCounts[((int)instances.instance(i).classValue())] += inst.weight();
/* 249:483 */       for (int j = 0; j < instances.numAttributes(); j++) {
/* 250:484 */         if ((!inst.isMissing(j)) && (j != this.m_ClassIndex)) {
/* 251:485 */           if (instances.attribute(j).isNumeric())
/* 252:    */           {
/* 253:486 */             double val = inst.value(j);
/* 254:489 */             for (int k = this.m_intervalBounds[j].length - 1; k >= 0; k--)
/* 255:    */             {
/* 256:490 */               if (val > this.m_intervalBounds[j][k])
/* 257:    */               {
/* 258:491 */                 this.m_counts[j][k][((int)inst.classValue())] += inst.weight();
/* 259:492 */                 break;
/* 260:    */               }
/* 261:493 */               if (val == this.m_intervalBounds[j][k])
/* 262:    */               {
/* 263:494 */                 this.m_counts[j][k][((int)inst.classValue())] += inst.weight() / 2.0D;
/* 264:495 */                 this.m_counts[j][(k - 1)][((int)inst.classValue())] += inst.weight() / 2.0D;
/* 265:    */                 
/* 266:497 */                 break;
/* 267:    */               }
/* 268:    */             }
/* 269:    */           }
/* 270:    */           else
/* 271:    */           {
/* 272:503 */             this.m_counts[j][((int)inst.value(j))][((int)inst.classValue())] += inst.weight();
/* 273:    */           }
/* 274:    */         }
/* 275:    */       }
/* 276:    */     }
/* 277:    */   }
/* 278:    */   
/* 279:    */   public String toString()
/* 280:    */   {
/* 281:519 */     if (this.m_Instances == null) {
/* 282:520 */       return "FVI: Classifier not built yet!";
/* 283:    */     }
/* 284:522 */     StringBuffer sb = new StringBuffer("Voting feature intervals classifier\n");
/* 285:    */     
/* 286:    */ 
/* 287:    */ 
/* 288:    */ 
/* 289:    */ 
/* 290:    */ 
/* 291:    */ 
/* 292:    */ 
/* 293:    */ 
/* 294:    */ 
/* 295:    */ 
/* 296:    */ 
/* 297:    */ 
/* 298:    */ 
/* 299:    */ 
/* 300:538 */     return sb.toString();
/* 301:    */   }
/* 302:    */   
/* 303:    */   public double[] distributionForInstance(Instance instance)
/* 304:    */     throws Exception
/* 305:    */   {
/* 306:550 */     double[] dist = new double[this.m_NumClasses];
/* 307:551 */     double[] temp = new double[this.m_NumClasses];
/* 308:552 */     double weight = 1.0D;
/* 309:554 */     for (int i = 0; i < instance.numAttributes(); i++) {
/* 310:555 */       if ((i != this.m_ClassIndex) && (!instance.isMissing(i)))
/* 311:    */       {
/* 312:556 */         double val = instance.value(i);
/* 313:557 */         boolean ok = false;
/* 314:558 */         if (instance.attribute(i).isNumeric())
/* 315:    */         {
/* 316:560 */           for (int k = this.m_intervalBounds[i].length - 1; k >= 0; k--)
/* 317:    */           {
/* 318:561 */             if (val > this.m_intervalBounds[i][k])
/* 319:    */             {
/* 320:562 */               for (int j = 0; j < this.m_NumClasses; j++) {
/* 321:563 */                 if (this.m_globalCounts[j] > 0.0D) {
/* 322:564 */                   temp[j] = ((this.m_counts[i][k][j] + this.TINY) / (this.m_globalCounts[j] + this.TINY));
/* 323:    */                 }
/* 324:    */               }
/* 325:567 */               ok = true;
/* 326:568 */               break;
/* 327:    */             }
/* 328:569 */             if (val == this.m_intervalBounds[i][k])
/* 329:    */             {
/* 330:570 */               for (int j = 0; j < this.m_NumClasses; j++) {
/* 331:571 */                 if (this.m_globalCounts[j] > 0.0D)
/* 332:    */                 {
/* 333:572 */                   temp[j] = ((this.m_counts[i][k][j] + this.m_counts[i][(k - 1)][j]) / 2.0D + this.TINY);
/* 334:    */                   
/* 335:574 */                   temp[j] /= (this.m_globalCounts[j] + this.TINY);
/* 336:    */                 }
/* 337:    */               }
/* 338:577 */               ok = true;
/* 339:578 */               break;
/* 340:    */             }
/* 341:    */           }
/* 342:581 */           if (!ok) {
/* 343:582 */             throw new Exception("This shouldn't happen");
/* 344:    */           }
/* 345:    */         }
/* 346:    */         else
/* 347:    */         {
/* 348:585 */           ok = true;
/* 349:586 */           for (int j = 0; j < this.m_NumClasses; j++) {
/* 350:587 */             if (this.m_globalCounts[j] > 0.0D) {
/* 351:588 */               temp[j] = ((this.m_counts[i][((int)val)][j] + this.TINY) / (this.m_globalCounts[j] + this.TINY));
/* 352:    */             }
/* 353:    */           }
/* 354:    */         }
/* 355:593 */         double sum = Utils.sum(temp);
/* 356:594 */         if (sum <= 0.0D) {
/* 357:595 */           for (int j = 0; j < temp.length; j++) {
/* 358:596 */             temp[j] = (1.0D / temp.length);
/* 359:    */           }
/* 360:    */         } else {
/* 361:599 */           Utils.normalize(temp, sum);
/* 362:    */         }
/* 363:602 */         if (this.m_weightByConfidence)
/* 364:    */         {
/* 365:603 */           weight = ContingencyTables.entropy(temp);
/* 366:604 */           weight = Math.pow(weight, this.m_bias);
/* 367:605 */           if (weight < 1.0D) {
/* 368:606 */             weight = 1.0D;
/* 369:    */           }
/* 370:    */         }
/* 371:610 */         for (int j = 0; j < this.m_NumClasses; j++) {
/* 372:611 */           dist[j] += temp[j] * weight;
/* 373:    */         }
/* 374:    */       }
/* 375:    */     }
/* 376:616 */     double sum = Utils.sum(dist);
/* 377:617 */     if (sum <= 0.0D)
/* 378:    */     {
/* 379:618 */       for (int j = 0; j < dist.length; j++) {
/* 380:619 */         dist[j] = (1.0D / dist.length);
/* 381:    */       }
/* 382:621 */       return dist;
/* 383:    */     }
/* 384:623 */     Utils.normalize(dist, sum);
/* 385:624 */     return dist;
/* 386:    */   }
/* 387:    */   
/* 388:    */   public String getRevision()
/* 389:    */   {
/* 390:635 */     return RevisionUtils.extract("$Revision: 10390 $");
/* 391:    */   }
/* 392:    */   
/* 393:    */   public static void main(String[] args)
/* 394:    */   {
/* 395:645 */     runClassifier(new VFI(), args);
/* 396:    */   }
/* 397:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.misc.VFI
 * JD-Core Version:    0.7.0.1
 */