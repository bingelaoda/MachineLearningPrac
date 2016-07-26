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
/*  15:    */ import weka.filters.StreamableFilter;
/*  16:    */ import weka.filters.UnsupervisedFilter;
/*  17:    */ 
/*  18:    */ public class ReservoirSample
/*  19:    */   extends Filter
/*  20:    */   implements UnsupervisedFilter, OptionHandler, StreamableFilter
/*  21:    */ {
/*  22:    */   static final long serialVersionUID = 3119607037607101160L;
/*  23: 88 */   protected int m_SampleSize = 100;
/*  24:    */   protected Instance[] m_subSample;
/*  25:    */   protected int m_currentInst;
/*  26: 97 */   protected int m_RandomSeed = 1;
/*  27:    */   protected Random m_random;
/*  28:    */   
/*  29:    */   public String globalInfo()
/*  30:    */   {
/*  31:109 */     return "Produces a random subsample of a dataset using the reservoir sampling Algorithm \"R\" by Vitter. The original data set does not have to fit into main memory, but the reservoir does. ";
/*  32:    */   }
/*  33:    */   
/*  34:    */   public Enumeration<Option> listOptions()
/*  35:    */   {
/*  36:122 */     Vector<Option> result = new Vector();
/*  37:    */     
/*  38:124 */     result.addElement(new Option("\tSpecify the random number seed (default 1)", "S", 1, "-S <num>"));
/*  39:    */     
/*  40:    */ 
/*  41:127 */     result.addElement(new Option("\tThe size of the output dataset - number of instances\n\t(default 100)", "Z", 1, "-Z <num>"));
/*  42:    */     
/*  43:    */ 
/*  44:    */ 
/*  45:131 */     return result.elements();
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void setOptions(String[] options)
/*  49:    */     throws Exception
/*  50:    */   {
/*  51:160 */     String tmpStr = Utils.getOption('S', options);
/*  52:161 */     if (tmpStr.length() != 0) {
/*  53:162 */       setRandomSeed(Integer.parseInt(tmpStr));
/*  54:    */     } else {
/*  55:164 */       setRandomSeed(1);
/*  56:    */     }
/*  57:167 */     tmpStr = Utils.getOption('Z', options);
/*  58:168 */     if (tmpStr.length() != 0) {
/*  59:169 */       setSampleSize(Integer.parseInt(tmpStr));
/*  60:    */     } else {
/*  61:171 */       setSampleSize(100);
/*  62:    */     }
/*  63:174 */     Utils.checkForRemainingOptions(options);
/*  64:    */   }
/*  65:    */   
/*  66:    */   public String[] getOptions()
/*  67:    */   {
/*  68:185 */     Vector<String> result = new Vector();
/*  69:    */     
/*  70:187 */     result.add("-S");
/*  71:188 */     result.add("" + getRandomSeed());
/*  72:    */     
/*  73:190 */     result.add("-Z");
/*  74:191 */     result.add("" + getSampleSize());
/*  75:    */     
/*  76:193 */     return (String[])result.toArray(new String[result.size()]);
/*  77:    */   }
/*  78:    */   
/*  79:    */   public String randomSeedTipText()
/*  80:    */   {
/*  81:203 */     return "The seed used for random sampling.";
/*  82:    */   }
/*  83:    */   
/*  84:    */   public int getRandomSeed()
/*  85:    */   {
/*  86:212 */     return this.m_RandomSeed;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void setRandomSeed(int newSeed)
/*  90:    */   {
/*  91:221 */     this.m_RandomSeed = newSeed;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public String sampleSizeTipText()
/*  95:    */   {
/*  96:231 */     return "Size of the subsample (reservoir). i.e. the number of instances.";
/*  97:    */   }
/*  98:    */   
/*  99:    */   public int getSampleSize()
/* 100:    */   {
/* 101:240 */     return this.m_SampleSize;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public void setSampleSize(int newSampleSize)
/* 105:    */   {
/* 106:249 */     this.m_SampleSize = newSampleSize;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public Capabilities getCapabilities()
/* 110:    */   {
/* 111:260 */     Capabilities result = super.getCapabilities();
/* 112:261 */     result.disableAll();
/* 113:    */     
/* 114:    */ 
/* 115:264 */     result.enableAllAttributes();
/* 116:265 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 117:    */     
/* 118:    */ 
/* 119:268 */     result.enableAllClasses();
/* 120:269 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 121:270 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 122:    */     
/* 123:272 */     return result;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public boolean setInputFormat(Instances instanceInfo)
/* 127:    */     throws Exception
/* 128:    */   {
/* 129:287 */     super.setInputFormat(instanceInfo);
/* 130:288 */     setOutputFormat(instanceInfo);
/* 131:    */     
/* 132:290 */     this.m_subSample = new Instance[this.m_SampleSize];
/* 133:291 */     this.m_currentInst = 0;
/* 134:292 */     this.m_random = new Random(this.m_RandomSeed);
/* 135:    */     
/* 136:294 */     return true;
/* 137:    */   }
/* 138:    */   
/* 139:    */   protected void processInstance(Instance instance)
/* 140:    */   {
/* 141:303 */     if (this.m_currentInst < this.m_SampleSize)
/* 142:    */     {
/* 143:304 */       this.m_subSample[this.m_currentInst] = ((Instance)instance.copy());
/* 144:    */     }
/* 145:    */     else
/* 146:    */     {
/* 147:306 */       double r = this.m_random.nextDouble();
/* 148:307 */       if (r < this.m_SampleSize / this.m_currentInst)
/* 149:    */       {
/* 150:308 */         r = this.m_random.nextDouble();
/* 151:309 */         int replace = (int)(this.m_SampleSize * r);
/* 152:310 */         this.m_subSample[replace] = ((Instance)instance.copy());
/* 153:    */       }
/* 154:    */     }
/* 155:313 */     this.m_currentInst += 1;
/* 156:    */   }
/* 157:    */   
/* 158:    */   public boolean input(Instance instance)
/* 159:    */   {
/* 160:327 */     if (getInputFormat() == null) {
/* 161:328 */       throw new IllegalStateException("No input instance format defined");
/* 162:    */     }
/* 163:330 */     if (this.m_NewBatch)
/* 164:    */     {
/* 165:331 */       resetQueue();
/* 166:332 */       this.m_NewBatch = false;
/* 167:    */     }
/* 168:334 */     if (isFirstBatchDone())
/* 169:    */     {
/* 170:335 */       push(instance);
/* 171:336 */       return true;
/* 172:    */     }
/* 173:339 */     copyValues(instance, false);
/* 174:340 */     processInstance(instance);
/* 175:341 */     return false;
/* 176:    */   }
/* 177:    */   
/* 178:    */   public boolean batchFinished()
/* 179:    */   {
/* 180:356 */     if (getInputFormat() == null) {
/* 181:357 */       throw new IllegalStateException("No input instance format defined");
/* 182:    */     }
/* 183:360 */     if (!isFirstBatchDone()) {
/* 184:362 */       createSubsample();
/* 185:    */     }
/* 186:364 */     flushInput();
/* 187:    */     
/* 188:366 */     this.m_NewBatch = true;
/* 189:367 */     this.m_FirstBatchDone = true;
/* 190:368 */     return numPendingOutput() != 0;
/* 191:    */   }
/* 192:    */   
/* 193:    */   protected void createSubsample()
/* 194:    */   {
/* 195:377 */     for (int i = 0; i < this.m_SampleSize; i++)
/* 196:    */     {
/* 197:378 */       if (this.m_subSample[i] == null) {
/* 198:    */         break;
/* 199:    */       }
/* 200:379 */       Instance copy = (Instance)this.m_subSample[i].copy();
/* 201:380 */       push(copy, false);
/* 202:    */     }
/* 203:387 */     this.m_subSample = null;
/* 204:    */   }
/* 205:    */   
/* 206:    */   public String getRevision()
/* 207:    */   {
/* 208:397 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 209:    */   }
/* 210:    */   
/* 211:    */   public static void main(String[] argv)
/* 212:    */   {
/* 213:406 */     runFilter(new ReservoirSample(), argv);
/* 214:    */   }
/* 215:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.instance.ReservoirSample
 * JD-Core Version:    0.7.0.1
 */