/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Random;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.Capabilities;
/*   8:    */ import weka.core.Capabilities.Capability;
/*   9:    */ import weka.core.DenseInstance;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.Option;
/*  13:    */ import weka.core.Randomizable;
/*  14:    */ import weka.core.Range;
/*  15:    */ import weka.core.RevisionUtils;
/*  16:    */ import weka.core.SparseInstance;
/*  17:    */ import weka.core.Utils;
/*  18:    */ import weka.filters.SimpleBatchFilter;
/*  19:    */ import weka.filters.UnsupervisedFilter;
/*  20:    */ 
/*  21:    */ public class ReplaceWithMissingValue
/*  22:    */   extends SimpleBatchFilter
/*  23:    */   implements UnsupervisedFilter, Randomizable
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = -2356630932899796239L;
/*  26: 71 */   protected Range m_Cols = new Range("first-last");
/*  27: 74 */   protected String m_DefaultCols = "first-last";
/*  28: 77 */   protected int m_Seed = 1;
/*  29: 80 */   protected double m_Probability = 0.1D;
/*  30: 83 */   protected boolean m_IgnoreClass = false;
/*  31:    */   
/*  32:    */   public Enumeration<Option> listOptions()
/*  33:    */   {
/*  34: 93 */     Vector<Option> result = new Vector(4);
/*  35:    */     
/*  36: 95 */     result.addElement(new Option("\tSpecifies list of columns to modify. First and last are valid indexes.\n\t(default: first-last)", "R", 1, "-R <col1,col2-col4,...>"));
/*  37:    */     
/*  38:    */ 
/*  39:    */ 
/*  40:    */ 
/*  41:100 */     result.addElement(new Option("\tInvert matching sense of column indexes.", "V", 0, "-V"));
/*  42:    */     
/*  43:    */ 
/*  44:103 */     result.addElement(new Option("\tSpecify the random number seed (default 1)", "S", 1, "-S <num>"));
/*  45:    */     
/*  46:    */ 
/*  47:106 */     result.addElement(new Option("\tSpecify the probability  (default 0.1)", "P", 1, "-P <double>"));
/*  48:    */     
/*  49:    */ 
/*  50:109 */     result.addElement(new Option("\tUnsets the class index temporarily before the filter is\n\tapplied to the data.\n\t(default: no)", "unset-class-temporarily", 1, "-unset-class-temporarily"));
/*  51:    */     
/*  52:    */ 
/*  53:    */ 
/*  54:    */ 
/*  55:114 */     return result.elements();
/*  56:    */   }
/*  57:    */   
/*  58:    */   public void setOptions(String[] options)
/*  59:    */     throws Exception
/*  60:    */   {
/*  61:150 */     setInvertSelection(Utils.getFlag('V', options));
/*  62:    */     
/*  63:152 */     String tmpStr = Utils.getOption('R', options);
/*  64:153 */     if (tmpStr.length() != 0) {
/*  65:154 */       setAttributeIndices(tmpStr);
/*  66:    */     } else {
/*  67:156 */       setAttributeIndices(this.m_DefaultCols);
/*  68:    */     }
/*  69:159 */     if (getInputFormat() != null) {
/*  70:160 */       setInputFormat(getInputFormat());
/*  71:    */     }
/*  72:163 */     String seedString = Utils.getOption('S', options);
/*  73:164 */     if (seedString.length() != 0) {
/*  74:165 */       setSeed(Integer.parseInt(seedString));
/*  75:    */     } else {
/*  76:167 */       setSeed(1);
/*  77:    */     }
/*  78:170 */     String probString = Utils.getOption('P', options);
/*  79:171 */     if (probString.length() != 0) {
/*  80:172 */       setProbability(Double.parseDouble(probString));
/*  81:    */     } else {
/*  82:174 */       setProbability(0.1D);
/*  83:    */     }
/*  84:177 */     setIgnoreClass(Utils.getFlag("unset-class-temporarily", options));
/*  85:    */     
/*  86:179 */     super.setOptions(options);
/*  87:    */     
/*  88:181 */     Utils.checkForRemainingOptions(options);
/*  89:    */   }
/*  90:    */   
/*  91:    */   public String[] getOptions()
/*  92:    */   {
/*  93:192 */     Vector<String> result = new Vector();
/*  94:194 */     if (!getAttributeIndices().equals(""))
/*  95:    */     {
/*  96:195 */       result.add("-R");
/*  97:196 */       result.add(getAttributeIndices());
/*  98:    */     }
/*  99:199 */     if (getInvertSelection()) {
/* 100:200 */       result.add("-V");
/* 101:    */     }
/* 102:203 */     result.add("-S");
/* 103:204 */     result.add("" + getSeed());
/* 104:    */     
/* 105:206 */     result.add("-P");
/* 106:207 */     result.add("" + getProbability());
/* 107:209 */     if (getIgnoreClass()) {
/* 108:210 */       result.add("-unset-class-temporarily");
/* 109:    */     }
/* 110:213 */     Collections.addAll(result, super.getOptions());
/* 111:    */     
/* 112:215 */     return (String[])result.toArray(new String[result.size()]);
/* 113:    */   }
/* 114:    */   
/* 115:    */   public String ignoreClassTipText()
/* 116:    */   {
/* 117:225 */     return "The class index will be unset temporarily before the filter is applied.";
/* 118:    */   }
/* 119:    */   
/* 120:    */   public void setIgnoreClass(boolean newIgnoreClass)
/* 121:    */   {
/* 122:235 */     this.m_IgnoreClass = newIgnoreClass;
/* 123:    */   }
/* 124:    */   
/* 125:    */   public boolean getIgnoreClass()
/* 126:    */   {
/* 127:245 */     return this.m_IgnoreClass;
/* 128:    */   }
/* 129:    */   
/* 130:    */   public String probabilityTipText()
/* 131:    */   {
/* 132:254 */     return "Probability to use for replacement.";
/* 133:    */   }
/* 134:    */   
/* 135:    */   public double getProbability()
/* 136:    */   {
/* 137:264 */     return this.m_Probability;
/* 138:    */   }
/* 139:    */   
/* 140:    */   public void setProbability(double newProbability)
/* 141:    */   {
/* 142:274 */     this.m_Probability = newProbability;
/* 143:    */   }
/* 144:    */   
/* 145:    */   public String seedTipText()
/* 146:    */   {
/* 147:283 */     return "Seed for the random number generator.";
/* 148:    */   }
/* 149:    */   
/* 150:    */   public int getSeed()
/* 151:    */   {
/* 152:293 */     return this.m_Seed;
/* 153:    */   }
/* 154:    */   
/* 155:    */   public void setSeed(int newSeed)
/* 156:    */   {
/* 157:303 */     this.m_Seed = newSeed;
/* 158:    */   }
/* 159:    */   
/* 160:    */   public String invertSelectionTipText()
/* 161:    */   {
/* 162:313 */     return "Set attribute selection mode. If false, only selected attributes will be modified'; if true, only non-selected attributes will be modified.";
/* 163:    */   }
/* 164:    */   
/* 165:    */   public boolean getInvertSelection()
/* 166:    */   {
/* 167:324 */     return this.m_Cols.getInvert();
/* 168:    */   }
/* 169:    */   
/* 170:    */   public void setInvertSelection(boolean value)
/* 171:    */   {
/* 172:335 */     this.m_Cols.setInvert(value);
/* 173:    */   }
/* 174:    */   
/* 175:    */   public String attributeIndicesTipText()
/* 176:    */   {
/* 177:345 */     return "Specify range of attributes to act on. This is a comma separated list of attribute indices, with \"first\" and \"last\" valid values. Specify an inclusive range with \"-\". E.g: \"first-3,5,6-10,last\".";
/* 178:    */   }
/* 179:    */   
/* 180:    */   public String getAttributeIndices()
/* 181:    */   {
/* 182:357 */     return this.m_Cols.getRanges();
/* 183:    */   }
/* 184:    */   
/* 185:    */   public void setAttributeIndices(String value)
/* 186:    */   {
/* 187:370 */     this.m_Cols.setRanges(value);
/* 188:    */   }
/* 189:    */   
/* 190:    */   public void setAttributeIndicesArray(int[] value)
/* 191:    */   {
/* 192:383 */     setAttributeIndices(Range.indicesToRangeList(value));
/* 193:    */   }
/* 194:    */   
/* 195:    */   public Capabilities getCapabilities()
/* 196:    */   {
/* 197:394 */     Capabilities result = super.getCapabilities();
/* 198:395 */     result.disableAll();
/* 199:    */     
/* 200:    */ 
/* 201:398 */     result.enableAllAttributes();
/* 202:399 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 203:    */     
/* 204:    */ 
/* 205:402 */     result.enableAllClasses();
/* 206:403 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 207:404 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 208:    */     
/* 209:406 */     return result;
/* 210:    */   }
/* 211:    */   
/* 212:    */   protected boolean hasImmediateOutputFormat()
/* 213:    */   {
/* 214:418 */     return true;
/* 215:    */   }
/* 216:    */   
/* 217:    */   protected Instances determineOutputFormat(Instances inputFormat)
/* 218:    */     throws Exception
/* 219:    */   {
/* 220:430 */     return inputFormat;
/* 221:    */   }
/* 222:    */   
/* 223:    */   public String globalInfo()
/* 224:    */   {
/* 225:441 */     return "A filter that can be used to introduce missing values in a dataset. The specified probability is used to flip a biased coin to decide whether to replace a particular attribute value in an instance with a missing value (i.e., a probability of 0.9 means 90% of values will be replaced with missing values). This filter only modifies the first batch of data that is processed.";
/* 226:    */   }
/* 227:    */   
/* 228:    */   protected Instances process(Instances instances)
/* 229:    */     throws Exception
/* 230:    */   {
/* 231:459 */     if (isFirstBatchDone()) {
/* 232:460 */       return instances;
/* 233:    */     }
/* 234:463 */     Instances newData = new Instances(instances, instances.numInstances());
/* 235:464 */     Random random = new Random(getSeed());
/* 236:    */     
/* 237:466 */     this.m_Cols.setUpper(newData.numAttributes() - 1);
/* 238:467 */     for (Instance inst : instances)
/* 239:    */     {
/* 240:468 */       double[] values = inst.toDoubleArray();
/* 241:469 */       for (int i = 0; i < values.length; i++) {
/* 242:470 */         if ((this.m_Cols.isInRange(i)) && ((i != instances.classIndex()) || (getIgnoreClass())) && 
/* 243:471 */           (random.nextDouble() < getProbability())) {
/* 244:472 */           values[i] = Utils.missingValue();
/* 245:    */         }
/* 246:    */       }
/* 247:476 */       if ((inst instanceof SparseInstance)) {
/* 248:477 */         newData.add(new SparseInstance(inst.weight(), values));
/* 249:    */       } else {
/* 250:479 */         newData.add(new DenseInstance(inst.weight(), values));
/* 251:    */       }
/* 252:    */     }
/* 253:483 */     return newData;
/* 254:    */   }
/* 255:    */   
/* 256:    */   public String getRevision()
/* 257:    */   {
/* 258:493 */     return RevisionUtils.extract("$Revision: 10215 $");
/* 259:    */   }
/* 260:    */   
/* 261:    */   public static void main(String[] argv)
/* 262:    */   {
/* 263:503 */     runFilter(new ReplaceWithMissingValue(), argv);
/* 264:    */   }
/* 265:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.ReplaceWithMissingValue
 * JD-Core Version:    0.7.0.1
 */