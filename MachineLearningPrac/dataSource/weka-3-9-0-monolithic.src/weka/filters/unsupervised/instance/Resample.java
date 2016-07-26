/*   1:    */ package weka.filters.unsupervised.instance;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import java.util.Random;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Capabilities;
/*   7:    */ import weka.core.Capabilities.Capability;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.Option;
/*  11:    */ import weka.core.OptionHandler;
/*  12:    */ import weka.core.RevisionUtils;
/*  13:    */ import weka.core.Utils;
/*  14:    */ import weka.filters.Filter;
/*  15:    */ import weka.filters.UnsupervisedFilter;
/*  16:    */ 
/*  17:    */ public class Resample
/*  18:    */   extends Filter
/*  19:    */   implements UnsupervisedFilter, OptionHandler
/*  20:    */ {
/*  21:    */   static final long serialVersionUID = 3119607037607101160L;
/*  22: 88 */   protected double m_SampleSizePercent = 100.0D;
/*  23: 91 */   protected int m_RandomSeed = 1;
/*  24: 94 */   protected boolean m_NoReplacement = false;
/*  25:102 */   protected boolean m_InvertSelection = false;
/*  26:    */   
/*  27:    */   public String globalInfo()
/*  28:    */   {
/*  29:111 */     return "Produces a random subsample of a dataset using either sampling with replacement or without replacement. The original dataset must fit entirely in memory. The number of instances in the generated dataset may be specified. When used in batch mode, subsequent batches are NOT resampled.";
/*  30:    */   }
/*  31:    */   
/*  32:    */   public Enumeration<Option> listOptions()
/*  33:    */   {
/*  34:125 */     Vector<Option> result = new Vector();
/*  35:    */     
/*  36:127 */     result.addElement(new Option("\tSpecify the random number seed (default 1)", "S", 1, "-S <num>"));
/*  37:    */     
/*  38:    */ 
/*  39:130 */     result.addElement(new Option("\tThe size of the output dataset, as a percentage of\n\tthe input dataset (default 100)", "Z", 1, "-Z <num>"));
/*  40:    */     
/*  41:    */ 
/*  42:    */ 
/*  43:134 */     result.addElement(new Option("\tDisables replacement of instances\n\t(default: with replacement)", "no-replacement", 0, "-no-replacement"));
/*  44:    */     
/*  45:    */ 
/*  46:    */ 
/*  47:    */ 
/*  48:139 */     result.addElement(new Option("\tInverts the selection - only available with '-no-replacement'.", "V", 0, "-V"));
/*  49:    */     
/*  50:    */ 
/*  51:    */ 
/*  52:143 */     return result.elements();
/*  53:    */   }
/*  54:    */   
/*  55:    */   public void setOptions(String[] options)
/*  56:    */     throws Exception
/*  57:    */   {
/*  58:183 */     String tmpStr = Utils.getOption('S', options);
/*  59:184 */     if (tmpStr.length() != 0) {
/*  60:185 */       setRandomSeed(Integer.parseInt(tmpStr));
/*  61:    */     } else {
/*  62:187 */       setRandomSeed(1);
/*  63:    */     }
/*  64:190 */     tmpStr = Utils.getOption('Z', options);
/*  65:191 */     if (tmpStr.length() != 0) {
/*  66:192 */       setSampleSizePercent(Double.parseDouble(tmpStr));
/*  67:    */     } else {
/*  68:194 */       setSampleSizePercent(100.0D);
/*  69:    */     }
/*  70:197 */     setNoReplacement(Utils.getFlag("no-replacement", options));
/*  71:199 */     if (getNoReplacement()) {
/*  72:200 */       setInvertSelection(Utils.getFlag('V', options));
/*  73:    */     }
/*  74:203 */     if (getInputFormat() != null) {
/*  75:204 */       setInputFormat(getInputFormat());
/*  76:    */     }
/*  77:207 */     Utils.checkForRemainingOptions(options);
/*  78:    */   }
/*  79:    */   
/*  80:    */   public String[] getOptions()
/*  81:    */   {
/*  82:218 */     Vector<String> result = new Vector();
/*  83:    */     
/*  84:220 */     result.add("-S");
/*  85:221 */     result.add("" + getRandomSeed());
/*  86:    */     
/*  87:223 */     result.add("-Z");
/*  88:224 */     result.add("" + getSampleSizePercent());
/*  89:226 */     if (getNoReplacement())
/*  90:    */     {
/*  91:227 */       result.add("-no-replacement");
/*  92:228 */       if (getInvertSelection()) {
/*  93:229 */         result.add("-V");
/*  94:    */       }
/*  95:    */     }
/*  96:233 */     return (String[])result.toArray(new String[result.size()]);
/*  97:    */   }
/*  98:    */   
/*  99:    */   public String randomSeedTipText()
/* 100:    */   {
/* 101:243 */     return "The seed used for random sampling.";
/* 102:    */   }
/* 103:    */   
/* 104:    */   public int getRandomSeed()
/* 105:    */   {
/* 106:252 */     return this.m_RandomSeed;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public void setRandomSeed(int newSeed)
/* 110:    */   {
/* 111:261 */     this.m_RandomSeed = newSeed;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public String sampleSizePercentTipText()
/* 115:    */   {
/* 116:271 */     return "Size of the subsample as a percentage of the original dataset.";
/* 117:    */   }
/* 118:    */   
/* 119:    */   public double getSampleSizePercent()
/* 120:    */   {
/* 121:280 */     return this.m_SampleSizePercent;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public void setSampleSizePercent(double newSampleSizePercent)
/* 125:    */   {
/* 126:289 */     this.m_SampleSizePercent = newSampleSizePercent;
/* 127:    */   }
/* 128:    */   
/* 129:    */   public String noReplacementTipText()
/* 130:    */   {
/* 131:299 */     return "Disables the replacement of instances.";
/* 132:    */   }
/* 133:    */   
/* 134:    */   public boolean getNoReplacement()
/* 135:    */   {
/* 136:308 */     return this.m_NoReplacement;
/* 137:    */   }
/* 138:    */   
/* 139:    */   public void setNoReplacement(boolean value)
/* 140:    */   {
/* 141:317 */     this.m_NoReplacement = value;
/* 142:    */   }
/* 143:    */   
/* 144:    */   public String invertSelectionTipText()
/* 145:    */   {
/* 146:327 */     return "Inverts the selection (only if instances are drawn WITHOUT replacement).";
/* 147:    */   }
/* 148:    */   
/* 149:    */   public boolean getInvertSelection()
/* 150:    */   {
/* 151:338 */     return this.m_InvertSelection;
/* 152:    */   }
/* 153:    */   
/* 154:    */   public void setInvertSelection(boolean value)
/* 155:    */   {
/* 156:348 */     this.m_InvertSelection = value;
/* 157:    */   }
/* 158:    */   
/* 159:    */   public Capabilities getCapabilities()
/* 160:    */   {
/* 161:359 */     Capabilities result = super.getCapabilities();
/* 162:360 */     result.disableAll();
/* 163:    */     
/* 164:    */ 
/* 165:363 */     result.enableAllAttributes();
/* 166:364 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 167:    */     
/* 168:    */ 
/* 169:367 */     result.enableAllClasses();
/* 170:368 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 171:369 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 172:    */     
/* 173:371 */     return result;
/* 174:    */   }
/* 175:    */   
/* 176:    */   public boolean setInputFormat(Instances instanceInfo)
/* 177:    */     throws Exception
/* 178:    */   {
/* 179:386 */     super.setInputFormat(instanceInfo);
/* 180:387 */     setOutputFormat(instanceInfo);
/* 181:388 */     return true;
/* 182:    */   }
/* 183:    */   
/* 184:    */   public boolean input(Instance instance)
/* 185:    */   {
/* 186:402 */     if (getInputFormat() == null) {
/* 187:403 */       throw new IllegalStateException("No input instance format defined");
/* 188:    */     }
/* 189:405 */     if (this.m_NewBatch)
/* 190:    */     {
/* 191:406 */       resetQueue();
/* 192:407 */       this.m_NewBatch = false;
/* 193:    */     }
/* 194:409 */     if (isFirstBatchDone())
/* 195:    */     {
/* 196:410 */       push(instance);
/* 197:411 */       return true;
/* 198:    */     }
/* 199:413 */     bufferInput(instance);
/* 200:414 */     return false;
/* 201:    */   }
/* 202:    */   
/* 203:    */   public boolean batchFinished()
/* 204:    */   {
/* 205:429 */     if (getInputFormat() == null) {
/* 206:430 */       throw new IllegalStateException("No input instance format defined");
/* 207:    */     }
/* 208:433 */     if (!isFirstBatchDone()) {
/* 209:435 */       createSubsample();
/* 210:    */     }
/* 211:437 */     flushInput();
/* 212:    */     
/* 213:439 */     this.m_NewBatch = true;
/* 214:440 */     this.m_FirstBatchDone = true;
/* 215:441 */     return numPendingOutput() != 0;
/* 216:    */   }
/* 217:    */   
/* 218:    */   protected void createSubsample()
/* 219:    */   {
/* 220:450 */     Instances data = getInputFormat();
/* 221:451 */     int numEligible = data.numInstances();
/* 222:452 */     int sampleSize = (int)(numEligible * this.m_SampleSizePercent / 100.0D);
/* 223:453 */     Random random = new Random(this.m_RandomSeed);
/* 224:455 */     if (getNoReplacement())
/* 225:    */     {
/* 226:458 */       int[] selected = new int[numEligible];
/* 227:459 */       for (int j = 0; j < numEligible; j++) {
/* 228:460 */         selected[j] = j;
/* 229:    */       }
/* 230:462 */       for (int i = 0; i < sampleSize; i++)
/* 231:    */       {
/* 232:465 */         int chosenLocation = random.nextInt(numEligible);
/* 233:466 */         int chosen = selected[chosenLocation];
/* 234:467 */         numEligible--;
/* 235:468 */         selected[chosenLocation] = selected[numEligible];
/* 236:469 */         selected[numEligible] = chosen;
/* 237:    */       }
/* 238:473 */       if (getInvertSelection()) {
/* 239:477 */         for (int j = 0; j < numEligible; j++) {
/* 240:478 */           push(data.instance(selected[j]), false);
/* 241:    */         }
/* 242:    */       } else {
/* 243:483 */         for (int j = numEligible; j < data.numInstances(); j++) {
/* 244:484 */           push(data.instance(selected[j]), false);
/* 245:    */         }
/* 246:    */       }
/* 247:    */     }
/* 248:    */     else
/* 249:    */     {
/* 250:490 */       for (int i = 0; i < sampleSize; i++) {
/* 251:491 */         push(data.instance(random.nextInt(numEligible)), false);
/* 252:    */       }
/* 253:    */     }
/* 254:    */   }
/* 255:    */   
/* 256:    */   public String getRevision()
/* 257:    */   {
/* 258:503 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 259:    */   }
/* 260:    */   
/* 261:    */   public static void main(String[] argv)
/* 262:    */   {
/* 263:512 */     runFilter(new Resample(), argv);
/* 264:    */   }
/* 265:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.instance.Resample
 * JD-Core Version:    0.7.0.1
 */