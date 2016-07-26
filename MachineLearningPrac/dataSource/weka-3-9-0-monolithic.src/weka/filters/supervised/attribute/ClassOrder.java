/*   1:    */ package weka.filters.supervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Random;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.Attribute;
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
/*  19:    */ public class ClassOrder
/*  20:    */   extends Filter
/*  21:    */   implements SupervisedFilter, OptionHandler
/*  22:    */ {
/*  23:    */   static final long serialVersionUID = -2116226838887628411L;
/*  24: 82 */   private long m_Seed = 1L;
/*  25: 85 */   private Random m_Random = null;
/*  26: 90 */   private int[] m_Converter = null;
/*  27: 93 */   private Attribute m_ClassAttribute = null;
/*  28: 96 */   private int m_ClassOrder = 0;
/*  29:    */   public static final int FREQ_ASCEND = 0;
/*  30:    */   public static final int FREQ_DESCEND = 1;
/*  31:    */   public static final int RANDOM = 2;
/*  32:111 */   private double[] m_ClassCounts = null;
/*  33:    */   
/*  34:    */   public String globalInfo()
/*  35:    */   {
/*  36:121 */     return "Changes the order of the classes so that the class values are no longer of in the order specified in the header. The values will be in the order specified by the user -- it could be either in ascending/descending order by the class frequency or in random order. Note that this filter currently does not change the header, only the class values of the instances, so there is not much point in using it in conjunction with the FilteredClassifier. The value can also be converted back using 'originalValue(double value)' procedure.";
/*  37:    */   }
/*  38:    */   
/*  39:    */   public Enumeration<Option> listOptions()
/*  40:    */   {
/*  41:140 */     Vector<Option> newVector = new Vector(2);
/*  42:    */     
/*  43:142 */     newVector.addElement(new Option("\tSpecify the seed of randomization\n\tused to randomize the class\n\torder (default: 1)", "R", 1, "-R <seed>"));
/*  44:    */     
/*  45:    */ 
/*  46:    */ 
/*  47:146 */     newVector.addElement(new Option("\tSpecify the class order to be\n\tsorted, could be 0: ascending\n\t1: descending and 2: random.(default: 0)", "C", 1, "-C <order>"));
/*  48:    */     
/*  49:    */ 
/*  50:    */ 
/*  51:150 */     return newVector.elements();
/*  52:    */   }
/*  53:    */   
/*  54:    */   public void setOptions(String[] options)
/*  55:    */     throws Exception
/*  56:    */   {
/*  57:182 */     String seedString = Utils.getOption('R', options);
/*  58:183 */     if (seedString.length() != 0) {
/*  59:184 */       this.m_Seed = Long.parseLong(seedString);
/*  60:    */     } else {
/*  61:186 */       this.m_Seed = 1L;
/*  62:    */     }
/*  63:189 */     String orderString = Utils.getOption('C', options);
/*  64:190 */     if (orderString.length() != 0) {
/*  65:191 */       this.m_ClassOrder = Integer.parseInt(orderString);
/*  66:    */     } else {
/*  67:193 */       this.m_ClassOrder = 0;
/*  68:    */     }
/*  69:196 */     if (getInputFormat() != null) {
/*  70:197 */       setInputFormat(getInputFormat());
/*  71:    */     }
/*  72:200 */     this.m_Random = null;
/*  73:    */     
/*  74:202 */     Utils.checkForRemainingOptions(options);
/*  75:    */   }
/*  76:    */   
/*  77:    */   public String[] getOptions()
/*  78:    */   {
/*  79:213 */     Vector<String> options = new Vector();
/*  80:    */     
/*  81:215 */     options.add("-R");
/*  82:216 */     options.add("" + this.m_Seed);
/*  83:217 */     options.add("-C");
/*  84:218 */     options.add("" + this.m_ClassOrder);
/*  85:    */     
/*  86:220 */     return (String[])options.toArray(new String[0]);
/*  87:    */   }
/*  88:    */   
/*  89:    */   public String seedTipText()
/*  90:    */   {
/*  91:230 */     return "Specify the seed of randomization of the class order";
/*  92:    */   }
/*  93:    */   
/*  94:    */   public long getSeed()
/*  95:    */   {
/*  96:239 */     return this.m_Seed;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public void setSeed(long seed)
/* 100:    */   {
/* 101:248 */     this.m_Seed = seed;
/* 102:249 */     this.m_Random = null;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public String classOrderTipText()
/* 106:    */   {
/* 107:259 */     return "Specify the class order after the filtering";
/* 108:    */   }
/* 109:    */   
/* 110:    */   public int getClassOrder()
/* 111:    */   {
/* 112:268 */     return this.m_ClassOrder;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public void setClassOrder(int order)
/* 116:    */   {
/* 117:277 */     this.m_ClassOrder = order;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public Capabilities getCapabilities()
/* 121:    */   {
/* 122:288 */     Capabilities result = super.getCapabilities();
/* 123:289 */     result.disableAll();
/* 124:    */     
/* 125:    */ 
/* 126:292 */     result.enableAllAttributes();
/* 127:293 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 128:    */     
/* 129:    */ 
/* 130:296 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 131:    */     
/* 132:298 */     return result;
/* 133:    */   }
/* 134:    */   
/* 135:    */   public boolean setInputFormat(Instances instanceInfo)
/* 136:    */     throws Exception
/* 137:    */   {
/* 138:313 */     super.setInputFormat(new Instances(instanceInfo, 0));
/* 139:    */     
/* 140:315 */     this.m_ClassAttribute = instanceInfo.classAttribute();
/* 141:316 */     this.m_Random = new Random(this.m_Seed);
/* 142:317 */     this.m_Converter = null;
/* 143:    */     
/* 144:319 */     int numClasses = instanceInfo.numClasses();
/* 145:320 */     this.m_ClassCounts = new double[numClasses];
/* 146:321 */     return false;
/* 147:    */   }
/* 148:    */   
/* 149:    */   public boolean input(Instance instance)
/* 150:    */   {
/* 151:336 */     if (getInputFormat() == null) {
/* 152:337 */       throw new IllegalStateException("No input instance format defined");
/* 153:    */     }
/* 154:339 */     if (this.m_NewBatch)
/* 155:    */     {
/* 156:340 */       resetQueue();
/* 157:341 */       this.m_NewBatch = false;
/* 158:    */     }
/* 159:346 */     if (this.m_Converter != null)
/* 160:    */     {
/* 161:347 */       Instance datum = (Instance)instance.copy();
/* 162:348 */       if (!datum.isMissing(this.m_ClassAttribute)) {
/* 163:349 */         datum.setClassValue(this.m_Converter[((int)datum.classValue())]);
/* 164:    */       }
/* 165:351 */       push(datum, false);
/* 166:352 */       return true;
/* 167:    */     }
/* 168:355 */     if (!instance.isMissing(this.m_ClassAttribute)) {
/* 169:356 */       this.m_ClassCounts[((int)instance.classValue())] += instance.weight();
/* 170:    */     }
/* 171:359 */     bufferInput(instance);
/* 172:360 */     return false;
/* 173:    */   }
/* 174:    */   
/* 175:    */   public boolean batchFinished()
/* 176:    */     throws Exception
/* 177:    */   {
/* 178:379 */     Instances data = getInputFormat();
/* 179:380 */     if (data == null) {
/* 180:381 */       throw new IllegalStateException("No input instance format defined");
/* 181:    */     }
/* 182:384 */     if (this.m_Converter == null)
/* 183:    */     {
/* 184:387 */       int[] randomIndices = new int[this.m_ClassCounts.length];
/* 185:388 */       for (int i = 0; i < randomIndices.length; i++) {
/* 186:389 */         randomIndices[i] = i;
/* 187:    */       }
/* 188:391 */       for (int j = randomIndices.length - 1; j > 0; j--)
/* 189:    */       {
/* 190:392 */         int toSwap = this.m_Random.nextInt(j + 1);
/* 191:393 */         int tmpIndex = randomIndices[j];
/* 192:394 */         randomIndices[j] = randomIndices[toSwap];
/* 193:395 */         randomIndices[toSwap] = tmpIndex;
/* 194:    */       }
/* 195:398 */       double[] randomizedCounts = new double[this.m_ClassCounts.length];
/* 196:399 */       for (int i = 0; i < randomizedCounts.length; i++) {
/* 197:400 */         randomizedCounts[i] = this.m_ClassCounts[randomIndices[i]];
/* 198:    */       }
/* 199:405 */       if (this.m_ClassOrder == 2)
/* 200:    */       {
/* 201:406 */         this.m_Converter = randomIndices;
/* 202:407 */         this.m_ClassCounts = randomizedCounts;
/* 203:    */       }
/* 204:    */       else
/* 205:    */       {
/* 206:409 */         int[] sorted = Utils.sort(randomizedCounts);
/* 207:410 */         this.m_Converter = new int[sorted.length];
/* 208:411 */         if (this.m_ClassOrder == 0) {
/* 209:412 */           for (int i = 0; i < sorted.length; i++) {
/* 210:413 */             this.m_Converter[i] = randomIndices[sorted[i]];
/* 211:    */           }
/* 212:415 */         } else if (this.m_ClassOrder == 1) {
/* 213:416 */           for (int i = 0; i < sorted.length; i++) {
/* 214:417 */             this.m_Converter[i] = randomIndices[sorted[(sorted.length - i - 1)]];
/* 215:    */           }
/* 216:    */         } else {
/* 217:420 */           throw new IllegalArgumentException("Class order not defined!");
/* 218:    */         }
/* 219:424 */         double[] tmp2 = new double[this.m_ClassCounts.length];
/* 220:425 */         for (int i = 0; i < this.m_Converter.length; i++) {
/* 221:426 */           tmp2[i] = this.m_ClassCounts[this.m_Converter[i]];
/* 222:    */         }
/* 223:428 */         this.m_ClassCounts = tmp2;
/* 224:    */       }
/* 225:432 */       ArrayList<String> values = new ArrayList(data.classAttribute().numValues());
/* 226:434 */       for (int i = 0; i < data.numClasses(); i++) {
/* 227:435 */         values.add(data.classAttribute().value(this.m_Converter[i]));
/* 228:    */       }
/* 229:437 */       ArrayList<Attribute> newVec = new ArrayList(data.numAttributes());
/* 230:439 */       for (int i = 0; i < data.numAttributes(); i++) {
/* 231:440 */         if (i == data.classIndex()) {
/* 232:441 */           newVec.add(new Attribute(data.classAttribute().name(), values, data.classAttribute().getMetadata()));
/* 233:    */         } else {
/* 234:444 */           newVec.add(data.attribute(i));
/* 235:    */         }
/* 236:    */       }
/* 237:447 */       Instances newInsts = new Instances(data.relationName(), newVec, 0);
/* 238:448 */       newInsts.setClassIndex(data.classIndex());
/* 239:449 */       setOutputFormat(newInsts);
/* 240:    */       
/* 241:    */ 
/* 242:452 */       int[] temp = new int[this.m_Converter.length];
/* 243:453 */       for (int i = 0; i < temp.length; i++) {
/* 244:454 */         temp[this.m_Converter[i]] = i;
/* 245:    */       }
/* 246:456 */       this.m_Converter = temp;
/* 247:459 */       for (int xyz = 0; xyz < data.numInstances(); xyz++)
/* 248:    */       {
/* 249:460 */         Instance datum = data.instance(xyz);
/* 250:461 */         if (!datum.isMissing(datum.classIndex())) {
/* 251:462 */           datum.setClassValue(this.m_Converter[((int)datum.classValue())]);
/* 252:    */         }
/* 253:464 */         push(datum, false);
/* 254:    */       }
/* 255:    */     }
/* 256:467 */     flushInput();
/* 257:468 */     this.m_NewBatch = true;
/* 258:469 */     return numPendingOutput() != 0;
/* 259:    */   }
/* 260:    */   
/* 261:    */   public double[] getClassCounts()
/* 262:    */   {
/* 263:480 */     if (this.m_ClassAttribute.isNominal()) {
/* 264:481 */       return this.m_ClassCounts;
/* 265:    */     }
/* 266:483 */     return null;
/* 267:    */   }
/* 268:    */   
/* 269:    */   public double[] distributionsByOriginalIndex(double[] before)
/* 270:    */   {
/* 271:496 */     double[] after = new double[this.m_Converter.length];
/* 272:497 */     for (int i = 0; i < this.m_Converter.length; i++) {
/* 273:498 */       after[i] = before[this.m_Converter[i]];
/* 274:    */     }
/* 275:501 */     return after;
/* 276:    */   }
/* 277:    */   
/* 278:    */   public double originalValue(double value)
/* 279:    */     throws Exception
/* 280:    */   {
/* 281:516 */     if (this.m_Converter == null) {
/* 282:517 */       throw new IllegalStateException("Coverter table not defined yet!");
/* 283:    */     }
/* 284:520 */     for (int i = 0; i < this.m_Converter.length; i++) {
/* 285:521 */       if ((int)value == this.m_Converter[i]) {
/* 286:522 */         return i;
/* 287:    */       }
/* 288:    */     }
/* 289:526 */     return -1.0D;
/* 290:    */   }
/* 291:    */   
/* 292:    */   public String getRevision()
/* 293:    */   {
/* 294:536 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 295:    */   }
/* 296:    */   
/* 297:    */   public static void main(String[] argv)
/* 298:    */   {
/* 299:545 */     runFilter(new ClassOrder(), argv);
/* 300:    */   }
/* 301:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.supervised.attribute.ClassOrder
 * JD-Core Version:    0.7.0.1
 */