/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import java.util.Random;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Attribute;
/*   7:    */ import weka.core.Capabilities;
/*   8:    */ import weka.core.Capabilities.Capability;
/*   9:    */ import weka.core.Instance;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.Option;
/*  12:    */ import weka.core.OptionHandler;
/*  13:    */ import weka.core.RevisionUtils;
/*  14:    */ import weka.core.SingleIndex;
/*  15:    */ import weka.core.Utils;
/*  16:    */ import weka.filters.Filter;
/*  17:    */ import weka.filters.UnsupervisedFilter;
/*  18:    */ 
/*  19:    */ public class AddNoise
/*  20:    */   extends Filter
/*  21:    */   implements UnsupervisedFilter, OptionHandler
/*  22:    */ {
/*  23:    */   static final long serialVersionUID = -8499673222857299082L;
/*  24: 83 */   private final SingleIndex m_AttIndex = new SingleIndex("last");
/*  25: 86 */   private boolean m_UseMissing = false;
/*  26: 89 */   private int m_Percent = 10;
/*  27: 92 */   private int m_RandomSeed = 1;
/*  28:    */   
/*  29:    */   public String globalInfo()
/*  30:    */   {
/*  31:102 */     return "An instance filter that changes a percentage of a given attributes values. The attribute must be nominal. Missing value can be treated as value itself.";
/*  32:    */   }
/*  33:    */   
/*  34:    */   public Enumeration<Option> listOptions()
/*  35:    */   {
/*  36:115 */     Vector<Option> newVector = new Vector(4);
/*  37:    */     
/*  38:117 */     newVector.addElement(new Option("\tIndex of the attribute to be changed \n\t(default last attribute)", "C", 1, "-C <col>"));
/*  39:    */     
/*  40:119 */     newVector.addElement(new Option("\tTreat missing values as an extra value \n", "M", 1, "-M"));
/*  41:    */     
/*  42:121 */     newVector.addElement(new Option("\tSpecify the percentage of noise introduced \n\tto the data (default 10)", "P", 1, "-P <num>"));
/*  43:    */     
/*  44:    */ 
/*  45:124 */     newVector.addElement(new Option("\tSpecify the random number seed (default 1)", "S", 1, "-S <num>"));
/*  46:    */     
/*  47:    */ 
/*  48:127 */     return newVector.elements();
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void setOptions(String[] options)
/*  52:    */     throws Exception
/*  53:    */   {
/*  54:167 */     String indexString = Utils.getOption('C', options);
/*  55:168 */     if (indexString.length() != 0) {
/*  56:169 */       setAttributeIndex(indexString);
/*  57:    */     } else {
/*  58:171 */       setAttributeIndex("last");
/*  59:    */     }
/*  60:174 */     if (Utils.getFlag('M', options)) {
/*  61:175 */       setUseMissing(true);
/*  62:    */     }
/*  63:178 */     String percentString = Utils.getOption('P', options);
/*  64:179 */     if (percentString.length() != 0) {
/*  65:180 */       setPercent((int)Double.valueOf(percentString).doubleValue());
/*  66:    */     } else {
/*  67:182 */       setPercent(10);
/*  68:    */     }
/*  69:185 */     String seedString = Utils.getOption('S', options);
/*  70:186 */     if (seedString.length() != 0) {
/*  71:187 */       setRandomSeed(Integer.parseInt(seedString));
/*  72:    */     } else {
/*  73:189 */       setRandomSeed(1);
/*  74:    */     }
/*  75:192 */     Utils.checkForRemainingOptions(options);
/*  76:    */   }
/*  77:    */   
/*  78:    */   public String[] getOptions()
/*  79:    */   {
/*  80:203 */     Vector<String> options = new Vector();
/*  81:    */     
/*  82:205 */     options.add("-C");
/*  83:206 */     options.add("" + getAttributeIndex());
/*  84:208 */     if (getUseMissing()) {
/*  85:209 */       options.add("-M");
/*  86:    */     }
/*  87:212 */     options.add("-P");
/*  88:213 */     options.add("" + getPercent());
/*  89:    */     
/*  90:215 */     options.add("-S");
/*  91:216 */     options.add("" + getRandomSeed());
/*  92:    */     
/*  93:218 */     return (String[])options.toArray(new String[0]);
/*  94:    */   }
/*  95:    */   
/*  96:    */   public String useMissingTipText()
/*  97:    */   {
/*  98:229 */     return "Flag to set if missing values are used.";
/*  99:    */   }
/* 100:    */   
/* 101:    */   public boolean getUseMissing()
/* 102:    */   {
/* 103:239 */     return this.m_UseMissing;
/* 104:    */   }
/* 105:    */   
/* 106:    */   public void setUseMissing(boolean newUseMissing)
/* 107:    */   {
/* 108:249 */     this.m_UseMissing = newUseMissing;
/* 109:    */   }
/* 110:    */   
/* 111:    */   public String randomSeedTipText()
/* 112:    */   {
/* 113:260 */     return "Random number seed.";
/* 114:    */   }
/* 115:    */   
/* 116:    */   public int getRandomSeed()
/* 117:    */   {
/* 118:270 */     return this.m_RandomSeed;
/* 119:    */   }
/* 120:    */   
/* 121:    */   public void setRandomSeed(int newSeed)
/* 122:    */   {
/* 123:280 */     this.m_RandomSeed = newSeed;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public String percentTipText()
/* 127:    */   {
/* 128:291 */     return "Percentage of introduced noise to data.";
/* 129:    */   }
/* 130:    */   
/* 131:    */   public int getPercent()
/* 132:    */   {
/* 133:301 */     return this.m_Percent;
/* 134:    */   }
/* 135:    */   
/* 136:    */   public void setPercent(int newPercent)
/* 137:    */   {
/* 138:311 */     this.m_Percent = newPercent;
/* 139:    */   }
/* 140:    */   
/* 141:    */   public String attributeIndexTipText()
/* 142:    */   {
/* 143:322 */     return "Index of the attribute that is to changed.";
/* 144:    */   }
/* 145:    */   
/* 146:    */   public String getAttributeIndex()
/* 147:    */   {
/* 148:332 */     return this.m_AttIndex.getSingleIndex();
/* 149:    */   }
/* 150:    */   
/* 151:    */   public void setAttributeIndex(String attIndex)
/* 152:    */   {
/* 153:342 */     this.m_AttIndex.setSingleIndex(attIndex);
/* 154:    */   }
/* 155:    */   
/* 156:    */   public Capabilities getCapabilities()
/* 157:    */   {
/* 158:353 */     Capabilities result = super.getCapabilities();
/* 159:354 */     result.disableAll();
/* 160:    */     
/* 161:    */ 
/* 162:357 */     result.enableAllAttributes();
/* 163:358 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 164:    */     
/* 165:    */ 
/* 166:361 */     result.enableAllClasses();
/* 167:362 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 168:363 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 169:    */     
/* 170:365 */     return result;
/* 171:    */   }
/* 172:    */   
/* 173:    */   public boolean setInputFormat(Instances instanceInfo)
/* 174:    */     throws Exception
/* 175:    */   {
/* 176:380 */     super.setInputFormat(instanceInfo);
/* 177:    */     
/* 178:    */ 
/* 179:383 */     this.m_AttIndex.setUpper(getInputFormat().numAttributes() - 1);
/* 180:387 */     if (!getInputFormat().attribute(this.m_AttIndex.getIndex()).isNominal()) {
/* 181:388 */       throw new Exception("Adding noise is not possible:Chosen attribute is numeric.");
/* 182:    */     }
/* 183:393 */     if ((getInputFormat().attribute(this.m_AttIndex.getIndex()).numValues() < 2) && (!this.m_UseMissing)) {
/* 184:395 */       throw new Exception("Adding noise is not possible:Chosen attribute has less than two values.");
/* 185:    */     }
/* 186:399 */     setOutputFormat(getInputFormat());
/* 187:400 */     this.m_NewBatch = true;
/* 188:401 */     return false;
/* 189:    */   }
/* 190:    */   
/* 191:    */   public boolean input(Instance instance)
/* 192:    */     throws Exception
/* 193:    */   {
/* 194:415 */     if (getInputFormat() == null) {
/* 195:416 */       throw new Exception("No input instance format defined");
/* 196:    */     }
/* 197:419 */     if (this.m_NewBatch)
/* 198:    */     {
/* 199:420 */       resetQueue();
/* 200:421 */       this.m_NewBatch = false;
/* 201:    */     }
/* 202:424 */     if (isFirstBatchDone())
/* 203:    */     {
/* 204:425 */       push(instance);
/* 205:426 */       return true;
/* 206:    */     }
/* 207:428 */     bufferInput(instance);
/* 208:429 */     return false;
/* 209:    */   }
/* 210:    */   
/* 211:    */   public boolean batchFinished()
/* 212:    */     throws Exception
/* 213:    */   {
/* 214:444 */     if (getInputFormat() == null) {
/* 215:445 */       throw new Exception("No input instance format defined");
/* 216:    */     }
/* 217:449 */     addNoise(getInputFormat(), this.m_RandomSeed, this.m_Percent, this.m_AttIndex.getIndex(), this.m_UseMissing);
/* 218:452 */     for (int i = 0; i < getInputFormat().numInstances(); i++) {
/* 219:453 */       push((Instance)getInputFormat().instance(i).copy(), false);
/* 220:    */     }
/* 221:456 */     flushInput();
/* 222:    */     
/* 223:458 */     this.m_NewBatch = true;
/* 224:459 */     this.m_FirstBatchDone = true;
/* 225:460 */     return numPendingOutput() != 0;
/* 226:    */   }
/* 227:    */   
/* 228:    */   public void addNoise(Instances instances, int seed, int percent, int attIndex, boolean useMissing)
/* 229:    */   {
/* 230:483 */     double splitPercent = percent;
/* 231:    */     
/* 232:    */ 
/* 233:486 */     int[] indexList = new int[instances.numInstances()];
/* 234:487 */     for (int i = 0; i < instances.numInstances(); i++) {
/* 235:488 */       indexList[i] = i;
/* 236:    */     }
/* 237:492 */     Random random = new Random(seed);
/* 238:493 */     for (int i = instances.numInstances() - 1; i >= 0; i--)
/* 239:    */     {
/* 240:494 */       int hValue = indexList[i];
/* 241:495 */       int hIndex = (int)(random.nextDouble() * i);
/* 242:496 */       indexList[i] = indexList[hIndex];
/* 243:497 */       indexList[hIndex] = hValue;
/* 244:    */     }
/* 245:505 */     int numValues = instances.attribute(attIndex).numValues();
/* 246:    */     
/* 247:507 */     int[] partition_count = new int[numValues];
/* 248:508 */     int[] partition_max = new int[numValues];
/* 249:509 */     int missing_count = 0;
/* 250:    */     
/* 251:511 */     int missing_max = 0;
/* 252:514 */     for (int i = 0; i < numValues; i++)
/* 253:    */     {
/* 254:515 */       partition_count[i] = 0;
/* 255:516 */       partition_max[i] = 0;
/* 256:    */     }
/* 257:522 */     for (Object element : instances)
/* 258:    */     {
/* 259:523 */       Instance instance = (Instance)element;
/* 260:524 */       if (instance.isMissing(attIndex))
/* 261:    */       {
/* 262:525 */         missing_max++;
/* 263:    */       }
/* 264:    */       else
/* 265:    */       {
/* 266:527 */         instance.value(attIndex);
/* 267:528 */         partition_max[((int)instance.value(attIndex))] += 1;
/* 268:    */       }
/* 269:    */     }
/* 270:535 */     if (!useMissing) {
/* 271:536 */       missing_max = missing_count;
/* 272:    */     } else {
/* 273:538 */       missing_max = (int)(missing_max / 100.0D * splitPercent + 0.5D);
/* 274:    */     }
/* 275:540 */     int sum_max = missing_max;
/* 276:541 */     for (int i = 0; i < numValues; i++)
/* 277:    */     {
/* 278:542 */       partition_max[i] = ((int)(partition_max[i] / 100.0D * splitPercent + 0.5D));
/* 279:    */       
/* 280:544 */       sum_max += partition_max[i];
/* 281:    */     }
/* 282:549 */     int sum_count = 0;
/* 283:    */     
/* 284:    */ 
/* 285:    */ 
/* 286:    */ 
/* 287:554 */     Random randomValue = new Random(seed);
/* 288:555 */     int numOfValues = instances.attribute(attIndex).numValues();
/* 289:556 */     for (int i = 0; i < instances.numInstances(); i++)
/* 290:    */     {
/* 291:557 */       if (sum_count >= sum_max) {
/* 292:    */         break;
/* 293:    */       }
/* 294:560 */       Instance currInstance = instances.instance(indexList[i]);
/* 295:562 */       if (currInstance.isMissing(attIndex))
/* 296:    */       {
/* 297:563 */         if (missing_count < missing_max)
/* 298:    */         {
/* 299:564 */           changeValueRandomly(randomValue, numOfValues, attIndex, currInstance, useMissing);
/* 300:    */           
/* 301:566 */           missing_count++;
/* 302:567 */           sum_count++;
/* 303:    */         }
/* 304:    */       }
/* 305:    */       else
/* 306:    */       {
/* 307:571 */         int vIndex = (int)currInstance.value(attIndex);
/* 308:572 */         if (partition_count[vIndex] < partition_max[vIndex])
/* 309:    */         {
/* 310:573 */           changeValueRandomly(randomValue, numOfValues, attIndex, currInstance, useMissing);
/* 311:    */           
/* 312:575 */           partition_count[vIndex] += 1;
/* 313:576 */           sum_count++;
/* 314:    */         }
/* 315:    */       }
/* 316:    */     }
/* 317:    */   }
/* 318:    */   
/* 319:    */   private void changeValueRandomly(Random r, int numOfValues, int indexOfAtt, Instance instance, boolean useMissing)
/* 320:    */   {
/* 321:    */     int currValue;
/* 322:    */     int currValue;
/* 323:598 */     if (instance.isMissing(indexOfAtt)) {
/* 324:599 */       currValue = numOfValues;
/* 325:    */     } else {
/* 326:601 */       currValue = (int)instance.value(indexOfAtt);
/* 327:    */     }
/* 328:605 */     if ((numOfValues == 2) && (!instance.isMissing(indexOfAtt))) {
/* 329:606 */       instance.setValue(indexOfAtt, (currValue + 1) % 2);
/* 330:    */     } else {
/* 331:    */       for (;;)
/* 332:    */       {
/* 333:    */         int newValue;
/* 334:    */         int newValue;
/* 335:613 */         if (useMissing) {
/* 336:614 */           newValue = (int)(r.nextDouble() * (numOfValues + 1));
/* 337:    */         } else {
/* 338:616 */           newValue = (int)(r.nextDouble() * numOfValues);
/* 339:    */         }
/* 340:619 */         if (newValue != currValue)
/* 341:    */         {
/* 342:622 */           if (newValue == numOfValues)
/* 343:    */           {
/* 344:623 */             instance.setMissing(indexOfAtt); break;
/* 345:    */           }
/* 346:625 */           instance.setValue(indexOfAtt, newValue);
/* 347:    */           
/* 348:627 */           break;
/* 349:    */         }
/* 350:    */       }
/* 351:    */     }
/* 352:    */   }
/* 353:    */   
/* 354:    */   public String getRevision()
/* 355:    */   {
/* 356:640 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 357:    */   }
/* 358:    */   
/* 359:    */   public static void main(String[] argv)
/* 360:    */   {
/* 361:649 */     runFilter(new AddNoise(), argv);
/* 362:    */   }
/* 363:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.AddNoise
 * JD-Core Version:    0.7.0.1
 */