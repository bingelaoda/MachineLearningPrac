/*   1:    */ package weka.filters.supervised.instance;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Hashtable;
/*   6:    */ import java.util.Random;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.core.Capabilities;
/*   9:    */ import weka.core.Capabilities.Capability;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.Option;
/*  13:    */ import weka.core.OptionHandler;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.Utils;
/*  16:    */ import weka.filters.Filter;
/*  17:    */ import weka.filters.SupervisedFilter;
/*  18:    */ 
/*  19:    */ public class SpreadSubsample
/*  20:    */   extends Filter
/*  21:    */   implements SupervisedFilter, OptionHandler
/*  22:    */ {
/*  23:    */   static final long serialVersionUID = -3947033795243930016L;
/*  24: 90 */   private int m_RandomSeed = 1;
/*  25:    */   private int m_MaxCount;
/*  26: 96 */   private double m_DistributionSpread = 0.0D;
/*  27:102 */   private boolean m_AdjustWeights = false;
/*  28:    */   
/*  29:    */   public String globalInfo()
/*  30:    */   {
/*  31:112 */     return "Produces a random subsample of a dataset. The original dataset must fit entirely in memory. This filter allows you to specify the maximum \"spread\" between the rarest and most common class. For example, you may specify that there be at most a 2:1 difference in class frequencies. When used in batch mode, subsequent batches are NOT resampled.";
/*  32:    */   }
/*  33:    */   
/*  34:    */   public String adjustWeightsTipText()
/*  35:    */   {
/*  36:127 */     return "Wether instance weights will be adjusted to maintain total weight per class.";
/*  37:    */   }
/*  38:    */   
/*  39:    */   public boolean getAdjustWeights()
/*  40:    */   {
/*  41:140 */     return this.m_AdjustWeights;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public void setAdjustWeights(boolean newAdjustWeights)
/*  45:    */   {
/*  46:151 */     this.m_AdjustWeights = newAdjustWeights;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public Enumeration<Option> listOptions()
/*  50:    */   {
/*  51:162 */     Vector<Option> newVector = new Vector(4);
/*  52:    */     
/*  53:164 */     newVector.addElement(new Option("\tSpecify the random number seed (default 1)", "S", 1, "-S <num>"));
/*  54:    */     
/*  55:166 */     newVector.addElement(new Option("\tThe maximum class distribution spread.\n\t0 = no maximum spread, 1 = uniform distribution, 10 = allow at most\n\ta 10:1 ratio between the classes (default 0)", "M", 1, "-M <num>"));
/*  56:    */     
/*  57:    */ 
/*  58:    */ 
/*  59:    */ 
/*  60:    */ 
/*  61:172 */     newVector.addElement(new Option("\tAdjust weights so that total weight per class is maintained.\n\tIndividual instance weighting is not preserved. (default no\n\tweights adjustment", "W", 0, "-W"));
/*  62:    */     
/*  63:    */ 
/*  64:    */ 
/*  65:176 */     newVector.addElement(new Option("\tThe maximum count for any class value (default 0 = unlimited).\n", "X", 0, "-X <num>"));
/*  66:    */     
/*  67:    */ 
/*  68:    */ 
/*  69:180 */     return newVector.elements();
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void setOptions(String[] options)
/*  73:    */     throws Exception
/*  74:    */   {
/*  75:222 */     String seedString = Utils.getOption('S', options);
/*  76:223 */     if (seedString.length() != 0) {
/*  77:224 */       setRandomSeed(Integer.parseInt(seedString));
/*  78:    */     } else {
/*  79:226 */       setRandomSeed(1);
/*  80:    */     }
/*  81:229 */     String maxString = Utils.getOption('M', options);
/*  82:230 */     if (maxString.length() != 0) {
/*  83:231 */       setDistributionSpread(Double.valueOf(maxString).doubleValue());
/*  84:    */     } else {
/*  85:233 */       setDistributionSpread(0.0D);
/*  86:    */     }
/*  87:236 */     String maxCount = Utils.getOption('X', options);
/*  88:237 */     if (maxCount.length() != 0) {
/*  89:238 */       setMaxCount(Double.valueOf(maxCount).doubleValue());
/*  90:    */     } else {
/*  91:240 */       setMaxCount(0.0D);
/*  92:    */     }
/*  93:243 */     setAdjustWeights(Utils.getFlag('W', options));
/*  94:245 */     if (getInputFormat() != null) {
/*  95:246 */       setInputFormat(getInputFormat());
/*  96:    */     }
/*  97:249 */     Utils.checkForRemainingOptions(options);
/*  98:    */   }
/*  99:    */   
/* 100:    */   public String[] getOptions()
/* 101:    */   {
/* 102:260 */     Vector<String> options = new Vector();
/* 103:    */     
/* 104:262 */     options.add("-M");
/* 105:263 */     options.add("" + getDistributionSpread());
/* 106:    */     
/* 107:265 */     options.add("-X");
/* 108:266 */     options.add("" + getMaxCount());
/* 109:    */     
/* 110:268 */     options.add("-S");
/* 111:269 */     options.add("" + getRandomSeed());
/* 112:271 */     if (getAdjustWeights()) {
/* 113:272 */       options.add("-W");
/* 114:    */     }
/* 115:275 */     return (String[])options.toArray(new String[0]);
/* 116:    */   }
/* 117:    */   
/* 118:    */   public String distributionSpreadTipText()
/* 119:    */   {
/* 120:285 */     return "The maximum class distribution spread. (0 = no maximum spread, 1 = uniform distribution, 10 = allow at most a 10:1 ratio between the classes).";
/* 121:    */   }
/* 122:    */   
/* 123:    */   public void setDistributionSpread(double spread)
/* 124:    */   {
/* 125:297 */     this.m_DistributionSpread = spread;
/* 126:    */   }
/* 127:    */   
/* 128:    */   public double getDistributionSpread()
/* 129:    */   {
/* 130:307 */     return this.m_DistributionSpread;
/* 131:    */   }
/* 132:    */   
/* 133:    */   public String maxCountTipText()
/* 134:    */   {
/* 135:317 */     return "The maximum count for any class value (0 = unlimited).";
/* 136:    */   }
/* 137:    */   
/* 138:    */   public void setMaxCount(double maxcount)
/* 139:    */   {
/* 140:327 */     this.m_MaxCount = ((int)maxcount);
/* 141:    */   }
/* 142:    */   
/* 143:    */   public double getMaxCount()
/* 144:    */   {
/* 145:337 */     return this.m_MaxCount;
/* 146:    */   }
/* 147:    */   
/* 148:    */   public String randomSeedTipText()
/* 149:    */   {
/* 150:347 */     return "Sets the random number seed for subsampling.";
/* 151:    */   }
/* 152:    */   
/* 153:    */   public int getRandomSeed()
/* 154:    */   {
/* 155:357 */     return this.m_RandomSeed;
/* 156:    */   }
/* 157:    */   
/* 158:    */   public void setRandomSeed(int newSeed)
/* 159:    */   {
/* 160:367 */     this.m_RandomSeed = newSeed;
/* 161:    */   }
/* 162:    */   
/* 163:    */   public Capabilities getCapabilities()
/* 164:    */   {
/* 165:378 */     Capabilities result = super.getCapabilities();
/* 166:379 */     result.disableAll();
/* 167:    */     
/* 168:    */ 
/* 169:382 */     result.enableAllAttributes();
/* 170:383 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 171:    */     
/* 172:    */ 
/* 173:386 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 174:    */     
/* 175:388 */     return result;
/* 176:    */   }
/* 177:    */   
/* 178:    */   public boolean setInputFormat(Instances instanceInfo)
/* 179:    */     throws Exception
/* 180:    */   {
/* 181:405 */     super.setInputFormat(instanceInfo);
/* 182:406 */     setOutputFormat(instanceInfo);
/* 183:407 */     return true;
/* 184:    */   }
/* 185:    */   
/* 186:    */   public boolean input(Instance instance)
/* 187:    */   {
/* 188:421 */     if (getInputFormat() == null) {
/* 189:422 */       throw new IllegalStateException("No input instance format defined");
/* 190:    */     }
/* 191:424 */     if (this.m_NewBatch)
/* 192:    */     {
/* 193:425 */       resetQueue();
/* 194:426 */       this.m_NewBatch = false;
/* 195:    */     }
/* 196:428 */     if (isFirstBatchDone())
/* 197:    */     {
/* 198:429 */       push(instance);
/* 199:430 */       return true;
/* 200:    */     }
/* 201:432 */     bufferInput(instance);
/* 202:433 */     return false;
/* 203:    */   }
/* 204:    */   
/* 205:    */   public boolean batchFinished()
/* 206:    */   {
/* 207:448 */     if (getInputFormat() == null) {
/* 208:449 */       throw new IllegalStateException("No input instance format defined");
/* 209:    */     }
/* 210:452 */     if (!isFirstBatchDone()) {
/* 211:454 */       createSubsample();
/* 212:    */     }
/* 213:457 */     flushInput();
/* 214:458 */     this.m_NewBatch = true;
/* 215:459 */     this.m_FirstBatchDone = true;
/* 216:460 */     return numPendingOutput() != 0;
/* 217:    */   }
/* 218:    */   
/* 219:    */   private void createSubsample()
/* 220:    */   {
/* 221:469 */     int classI = getInputFormat().classIndex();
/* 222:    */     
/* 223:471 */     getInputFormat().sort(classI);
/* 224:    */     
/* 225:473 */     int[] classIndices = getClassIndices();
/* 226:    */     
/* 227:    */ 
/* 228:476 */     int[] counts = new int[getInputFormat().numClasses()];
/* 229:477 */     double[] weights = new double[getInputFormat().numClasses()];
/* 230:478 */     int min = -1;
/* 231:479 */     for (int i = 0; i < getInputFormat().numInstances(); i++)
/* 232:    */     {
/* 233:480 */       Instance current = getInputFormat().instance(i);
/* 234:481 */       if (!current.classIsMissing())
/* 235:    */       {
/* 236:482 */         counts[((int)current.classValue())] += 1;
/* 237:483 */         weights[((int)current.classValue())] += current.weight();
/* 238:    */       }
/* 239:    */     }
/* 240:488 */     for (int i = 0; i < counts.length; i++) {
/* 241:489 */       if (counts[i] > 0) {
/* 242:490 */         weights[i] /= counts[i];
/* 243:    */       }
/* 244:    */     }
/* 245:500 */     int minIndex = -1;
/* 246:501 */     for (int i = 0; i < counts.length; i++) {
/* 247:502 */       if ((min < 0) && (counts[i] > 0))
/* 248:    */       {
/* 249:503 */         min = counts[i];
/* 250:504 */         minIndex = i;
/* 251:    */       }
/* 252:505 */       else if ((counts[i] < min) && (counts[i] > 0))
/* 253:    */       {
/* 254:506 */         min = counts[i];
/* 255:507 */         minIndex = i;
/* 256:    */       }
/* 257:    */     }
/* 258:511 */     if (min < 0)
/* 259:    */     {
/* 260:512 */       System.err.println("SpreadSubsample: *warning* none of the classes have any values in them.");
/* 261:    */       
/* 262:514 */       return;
/* 263:    */     }
/* 264:518 */     int[] new_counts = new int[getInputFormat().numClasses()];
/* 265:519 */     for (int i = 0; i < counts.length; i++)
/* 266:    */     {
/* 267:520 */       new_counts[i] = ((int)Math.abs(Math.min(counts[i], min * this.m_DistributionSpread)));
/* 268:522 */       if ((i == minIndex) && 
/* 269:523 */         (this.m_DistributionSpread > 0.0D) && (this.m_DistributionSpread < 1.0D)) {
/* 270:525 */         new_counts[i] = counts[i];
/* 271:    */       }
/* 272:528 */       if (this.m_DistributionSpread == 0.0D) {
/* 273:529 */         new_counts[i] = counts[i];
/* 274:    */       }
/* 275:532 */       if (this.m_MaxCount > 0) {
/* 276:533 */         new_counts[i] = Math.min(new_counts[i], this.m_MaxCount);
/* 277:    */       }
/* 278:    */     }
/* 279:538 */     Random random = new Random(this.m_RandomSeed);
/* 280:539 */     Hashtable<String, String> t = new Hashtable();
/* 281:540 */     for (int j = 0; j < new_counts.length; j++)
/* 282:    */     {
/* 283:541 */       double newWeight = 1.0D;
/* 284:542 */       if ((this.m_AdjustWeights) && (new_counts[j] > 0)) {
/* 285:543 */         newWeight = weights[j] * counts[j] / new_counts[j];
/* 286:    */       }
/* 287:551 */       for (int k = 0; k < new_counts[j]; k++)
/* 288:    */       {
/* 289:552 */         boolean ok = false;
/* 290:    */         do
/* 291:    */         {
/* 292:554 */           int index = classIndices[j] + random.nextInt(classIndices[(j + 1)] - classIndices[j]);
/* 293:557 */           if (t.get("" + index) == null)
/* 294:    */           {
/* 295:559 */             t.put("" + index, "");
/* 296:560 */             ok = true;
/* 297:561 */             if (index >= 0)
/* 298:    */             {
/* 299:562 */               Instance newInst = (Instance)getInputFormat().instance(index).copy();
/* 300:564 */               if (this.m_AdjustWeights) {
/* 301:565 */                 newInst.setWeight(newWeight);
/* 302:    */               }
/* 303:567 */               push(newInst, false);
/* 304:    */             }
/* 305:    */           }
/* 306:570 */         } while (!ok);
/* 307:    */       }
/* 308:    */     }
/* 309:    */   }
/* 310:    */   
/* 311:    */   private int[] getClassIndices()
/* 312:    */   {
/* 313:584 */     int[] classIndices = new int[getInputFormat().numClasses() + 1];
/* 314:585 */     int currentClass = 0;
/* 315:586 */     classIndices[currentClass] = 0;
/* 316:587 */     for (int i = 0; i < getInputFormat().numInstances(); i++)
/* 317:    */     {
/* 318:588 */       Instance current = getInputFormat().instance(i);
/* 319:589 */       if (current.classIsMissing())
/* 320:    */       {
/* 321:590 */         for (int j = currentClass + 1; j < classIndices.length; j++) {
/* 322:591 */           classIndices[j] = i;
/* 323:    */         }
/* 324:593 */         break;
/* 325:    */       }
/* 326:594 */       if (current.classValue() != currentClass)
/* 327:    */       {
/* 328:595 */         for (int j = currentClass + 1; j <= current.classValue(); j++) {
/* 329:596 */           classIndices[j] = i;
/* 330:    */         }
/* 331:598 */         currentClass = (int)current.classValue();
/* 332:    */       }
/* 333:    */     }
/* 334:601 */     if (currentClass <= getInputFormat().numClasses()) {
/* 335:602 */       for (int j = currentClass + 1; j < classIndices.length; j++) {
/* 336:603 */         classIndices[j] = getInputFormat().numInstances();
/* 337:    */       }
/* 338:    */     }
/* 339:606 */     return classIndices;
/* 340:    */   }
/* 341:    */   
/* 342:    */   public String getRevision()
/* 343:    */   {
/* 344:616 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 345:    */   }
/* 346:    */   
/* 347:    */   public static void main(String[] argv)
/* 348:    */   {
/* 349:625 */     runFilter(new SpreadSubsample(), argv);
/* 350:    */   }
/* 351:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.supervised.instance.SpreadSubsample
 * JD-Core Version:    0.7.0.1
 */