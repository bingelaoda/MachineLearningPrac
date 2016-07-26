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
/*  17:    */ public class Randomize
/*  18:    */   extends Filter
/*  19:    */   implements UnsupervisedFilter, OptionHandler
/*  20:    */ {
/*  21:    */   static final long serialVersionUID = 8854479785121877582L;
/*  22: 66 */   protected int m_Seed = 42;
/*  23:    */   protected Random m_Random;
/*  24:    */   
/*  25:    */   public String globalInfo()
/*  26:    */   {
/*  27: 78 */     return "Randomly shuffles the order of instances passed through it. The random number generator is reset with the seed value whenever a new set of instances is passed in.";
/*  28:    */   }
/*  29:    */   
/*  30:    */   public Enumeration<Option> listOptions()
/*  31:    */   {
/*  32: 91 */     Vector<Option> newVector = new Vector(1);
/*  33:    */     
/*  34: 93 */     newVector.addElement(new Option("\tSpecify the random number seed (default 42)", "S", 1, "-S <num>"));
/*  35:    */     
/*  36:    */ 
/*  37: 96 */     return newVector.elements();
/*  38:    */   }
/*  39:    */   
/*  40:    */   public void setOptions(String[] options)
/*  41:    */     throws Exception
/*  42:    */   {
/*  43:119 */     String seedString = Utils.getOption('S', options);
/*  44:120 */     if (seedString.length() != 0) {
/*  45:121 */       setRandomSeed(Integer.parseInt(seedString));
/*  46:    */     } else {
/*  47:123 */       setRandomSeed(42);
/*  48:    */     }
/*  49:126 */     if (getInputFormat() != null) {
/*  50:127 */       setInputFormat(getInputFormat());
/*  51:    */     }
/*  52:    */   }
/*  53:    */   
/*  54:    */   public String[] getOptions()
/*  55:    */   {
/*  56:139 */     Vector<String> options = new Vector();
/*  57:    */     
/*  58:141 */     options.add("-S");
/*  59:142 */     options.add("" + getRandomSeed());
/*  60:    */     
/*  61:144 */     return (String[])options.toArray(new String[0]);
/*  62:    */   }
/*  63:    */   
/*  64:    */   public String randomSeedTipText()
/*  65:    */   {
/*  66:154 */     return "Seed for the random number generator.";
/*  67:    */   }
/*  68:    */   
/*  69:    */   public int getRandomSeed()
/*  70:    */   {
/*  71:164 */     return this.m_Seed;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void setRandomSeed(int newRandomSeed)
/*  75:    */   {
/*  76:174 */     this.m_Seed = newRandomSeed;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public Capabilities getCapabilities()
/*  80:    */   {
/*  81:185 */     Capabilities result = super.getCapabilities();
/*  82:186 */     result.disableAll();
/*  83:    */     
/*  84:    */ 
/*  85:189 */     result.enableAllAttributes();
/*  86:190 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  87:    */     
/*  88:    */ 
/*  89:193 */     result.enableAllClasses();
/*  90:194 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  91:195 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  92:    */     
/*  93:197 */     return result;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public boolean setInputFormat(Instances instanceInfo)
/*  97:    */     throws Exception
/*  98:    */   {
/*  99:212 */     super.setInputFormat(instanceInfo);
/* 100:213 */     setOutputFormat(instanceInfo);
/* 101:214 */     this.m_Random = new Random(this.m_Seed);
/* 102:215 */     return true;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public boolean input(Instance instance)
/* 106:    */   {
/* 107:229 */     if (getInputFormat() == null) {
/* 108:230 */       throw new IllegalStateException("No input instance format defined");
/* 109:    */     }
/* 110:232 */     if (this.m_NewBatch)
/* 111:    */     {
/* 112:233 */       resetQueue();
/* 113:234 */       this.m_NewBatch = false;
/* 114:    */     }
/* 115:236 */     if (isFirstBatchDone())
/* 116:    */     {
/* 117:237 */       push(instance);
/* 118:238 */       return true;
/* 119:    */     }
/* 120:240 */     bufferInput(instance);
/* 121:241 */     return false;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public boolean batchFinished()
/* 125:    */   {
/* 126:259 */     if (getInputFormat() == null) {
/* 127:260 */       throw new IllegalStateException("No input instance format defined");
/* 128:    */     }
/* 129:263 */     if (!isFirstBatchDone()) {
/* 130:264 */       getInputFormat().randomize(this.m_Random);
/* 131:    */     }
/* 132:266 */     for (int i = 0; i < getInputFormat().numInstances(); i++) {
/* 133:267 */       push(getInputFormat().instance(i), false);
/* 134:    */     }
/* 135:269 */     flushInput();
/* 136:    */     
/* 137:271 */     this.m_NewBatch = true;
/* 138:272 */     this.m_FirstBatchDone = true;
/* 139:273 */     return numPendingOutput() != 0;
/* 140:    */   }
/* 141:    */   
/* 142:    */   public String getRevision()
/* 143:    */   {
/* 144:283 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 145:    */   }
/* 146:    */   
/* 147:    */   public static void main(String[] argv)
/* 148:    */   {
/* 149:292 */     runFilter(new Randomize(), argv);
/* 150:    */   }
/* 151:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.instance.Randomize
 * JD-Core Version:    0.7.0.1
 */