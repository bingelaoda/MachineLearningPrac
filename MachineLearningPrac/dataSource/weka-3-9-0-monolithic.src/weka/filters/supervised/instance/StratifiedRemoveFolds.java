/*   1:    */ package weka.filters.supervised.instance;
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
/*  15:    */ import weka.filters.SupervisedFilter;
/*  16:    */ 
/*  17:    */ public class StratifiedRemoveFolds
/*  18:    */   extends Filter
/*  19:    */   implements SupervisedFilter, OptionHandler
/*  20:    */ {
/*  21:    */   static final long serialVersionUID = -7069148179905814324L;
/*  22: 82 */   private boolean m_Inverse = false;
/*  23: 85 */   private int m_NumFolds = 10;
/*  24: 88 */   private int m_Fold = 1;
/*  25: 91 */   private long m_Seed = 0L;
/*  26:    */   
/*  27:    */   public Enumeration<Option> listOptions()
/*  28:    */   {
/*  29:101 */     Vector<Option> newVector = new Vector(4);
/*  30:    */     
/*  31:103 */     newVector.addElement(new Option("\tSpecifies if inverse of selection is to be output.\n", "V", 0, "-V"));
/*  32:    */     
/*  33:    */ 
/*  34:106 */     newVector.addElement(new Option("\tSpecifies number of folds dataset is split into. \n\t(default 10)\n", "N", 1, "-N <number of folds>"));
/*  35:    */     
/*  36:    */ 
/*  37:    */ 
/*  38:110 */     newVector.addElement(new Option("\tSpecifies which fold is selected. (default 1)\n", "F", 1, "-F <fold>"));
/*  39:    */     
/*  40:    */ 
/*  41:    */ 
/*  42:    */ 
/*  43:115 */     newVector.addElement(new Option("\tSpecifies random number seed. (default 0, no randomizing)\n", "S", 1, "-S <seed>"));
/*  44:    */     
/*  45:    */ 
/*  46:    */ 
/*  47:119 */     return newVector.elements();
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void setOptions(String[] options)
/*  51:    */     throws Exception
/*  52:    */   {
/*  53:158 */     setInvertSelection(Utils.getFlag('V', options));
/*  54:159 */     String numFolds = Utils.getOption('N', options);
/*  55:160 */     if (numFolds.length() != 0) {
/*  56:161 */       setNumFolds(Integer.parseInt(numFolds));
/*  57:    */     } else {
/*  58:163 */       setNumFolds(10);
/*  59:    */     }
/*  60:165 */     String fold = Utils.getOption('F', options);
/*  61:166 */     if (fold.length() != 0) {
/*  62:167 */       setFold(Integer.parseInt(fold));
/*  63:    */     } else {
/*  64:169 */       setFold(1);
/*  65:    */     }
/*  66:171 */     String seed = Utils.getOption('S', options);
/*  67:172 */     if (seed.length() != 0) {
/*  68:173 */       setSeed(Integer.parseInt(seed));
/*  69:    */     } else {
/*  70:175 */       setSeed(0L);
/*  71:    */     }
/*  72:177 */     if (getInputFormat() != null) {
/*  73:178 */       setInputFormat(getInputFormat());
/*  74:    */     }
/*  75:181 */     Utils.checkForRemainingOptions(options);
/*  76:    */   }
/*  77:    */   
/*  78:    */   public String[] getOptions()
/*  79:    */   {
/*  80:192 */     Vector<String> options = new Vector();
/*  81:    */     
/*  82:194 */     options.add("-S");
/*  83:195 */     options.add("" + getSeed());
/*  84:196 */     if (getInvertSelection()) {
/*  85:197 */       options.add("-V");
/*  86:    */     }
/*  87:199 */     options.add("-N");
/*  88:200 */     options.add("" + getNumFolds());
/*  89:201 */     options.add("-F");
/*  90:202 */     options.add("" + getFold());
/*  91:    */     
/*  92:204 */     return (String[])options.toArray(new String[0]);
/*  93:    */   }
/*  94:    */   
/*  95:    */   public String globalInfo()
/*  96:    */   {
/*  97:214 */     return "This filter takes a dataset and outputs a specified fold for cross validation. If you do not want the folds to be stratified use the unsupervised version.";
/*  98:    */   }
/*  99:    */   
/* 100:    */   public String invertSelectionTipText()
/* 101:    */   {
/* 102:227 */     return "Whether to invert the selection.";
/* 103:    */   }
/* 104:    */   
/* 105:    */   public boolean getInvertSelection()
/* 106:    */   {
/* 107:237 */     return this.m_Inverse;
/* 108:    */   }
/* 109:    */   
/* 110:    */   public void setInvertSelection(boolean inverse)
/* 111:    */   {
/* 112:247 */     this.m_Inverse = inverse;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public String numFoldsTipText()
/* 116:    */   {
/* 117:258 */     return "The number of folds to split the dataset into.";
/* 118:    */   }
/* 119:    */   
/* 120:    */   public int getNumFolds()
/* 121:    */   {
/* 122:268 */     return this.m_NumFolds;
/* 123:    */   }
/* 124:    */   
/* 125:    */   public void setNumFolds(int numFolds)
/* 126:    */   {
/* 127:280 */     if (numFolds < 0) {
/* 128:281 */       throw new IllegalArgumentException("Number of folds has to be positive or zero.");
/* 129:    */     }
/* 130:284 */     this.m_NumFolds = numFolds;
/* 131:    */   }
/* 132:    */   
/* 133:    */   public String foldTipText()
/* 134:    */   {
/* 135:295 */     return "The fold which is selected.";
/* 136:    */   }
/* 137:    */   
/* 138:    */   public int getFold()
/* 139:    */   {
/* 140:305 */     return this.m_Fold;
/* 141:    */   }
/* 142:    */   
/* 143:    */   public void setFold(int fold)
/* 144:    */   {
/* 145:316 */     if (fold < 1) {
/* 146:317 */       throw new IllegalArgumentException("Fold's index has to be greater than 0.");
/* 147:    */     }
/* 148:320 */     this.m_Fold = fold;
/* 149:    */   }
/* 150:    */   
/* 151:    */   public String seedTipText()
/* 152:    */   {
/* 153:331 */     return "the random number seed for shuffling the dataset. If seed is negative, shuffling will not be performed.";
/* 154:    */   }
/* 155:    */   
/* 156:    */   public long getSeed()
/* 157:    */   {
/* 158:341 */     return this.m_Seed;
/* 159:    */   }
/* 160:    */   
/* 161:    */   public void setSeed(long seed)
/* 162:    */   {
/* 163:352 */     this.m_Seed = seed;
/* 164:    */   }
/* 165:    */   
/* 166:    */   public Capabilities getCapabilities()
/* 167:    */   {
/* 168:363 */     Capabilities result = super.getCapabilities();
/* 169:364 */     result.disableAll();
/* 170:    */     
/* 171:    */ 
/* 172:367 */     result.enableAllAttributes();
/* 173:368 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 174:    */     
/* 175:    */ 
/* 176:371 */     result.enableAllClasses();
/* 177:372 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 178:    */     
/* 179:374 */     return result;
/* 180:    */   }
/* 181:    */   
/* 182:    */   public boolean setInputFormat(Instances instanceInfo)
/* 183:    */     throws Exception
/* 184:    */   {
/* 185:389 */     if ((this.m_NumFolds > 0) && (this.m_NumFolds < this.m_Fold)) {
/* 186:390 */       throw new IllegalArgumentException("Fold has to be smaller or equal to number of folds.");
/* 187:    */     }
/* 188:393 */     super.setInputFormat(instanceInfo);
/* 189:394 */     setOutputFormat(instanceInfo);
/* 190:395 */     return true;
/* 191:    */   }
/* 192:    */   
/* 193:    */   public boolean input(Instance instance)
/* 194:    */   {
/* 195:409 */     if (getInputFormat() == null) {
/* 196:410 */       throw new IllegalStateException("No input instance format defined");
/* 197:    */     }
/* 198:412 */     if (this.m_NewBatch)
/* 199:    */     {
/* 200:413 */       resetQueue();
/* 201:414 */       this.m_NewBatch = false;
/* 202:    */     }
/* 203:416 */     if (isFirstBatchDone())
/* 204:    */     {
/* 205:417 */       push(instance);
/* 206:418 */       return true;
/* 207:    */     }
/* 208:420 */     bufferInput(instance);
/* 209:421 */     return false;
/* 210:    */   }
/* 211:    */   
/* 212:    */   public boolean batchFinished()
/* 213:    */   {
/* 214:435 */     if (getInputFormat() == null) {
/* 215:436 */       throw new IllegalStateException("No input instance format defined");
/* 216:    */     }
/* 217:    */     Instances instances;
/* 218:    */     Instances instances;
/* 219:441 */     if (!isFirstBatchDone())
/* 220:    */     {
/* 221:442 */       if (this.m_Seed > 0L) {
/* 222:444 */         getInputFormat().randomize(new Random(this.m_Seed));
/* 223:    */       }
/* 224:448 */       getInputFormat().stratify(this.m_NumFolds);
/* 225:    */       Instances instances;
/* 226:449 */       if (!this.m_Inverse) {
/* 227:450 */         instances = getInputFormat().testCV(this.m_NumFolds, this.m_Fold - 1);
/* 228:    */       } else {
/* 229:452 */         instances = getInputFormat().trainCV(this.m_NumFolds, this.m_Fold - 1);
/* 230:    */       }
/* 231:    */     }
/* 232:    */     else
/* 233:    */     {
/* 234:455 */       instances = getInputFormat();
/* 235:    */     }
/* 236:458 */     flushInput();
/* 237:460 */     for (int i = 0; i < instances.numInstances(); i++) {
/* 238:461 */       push(instances.instance(i), false);
/* 239:    */     }
/* 240:464 */     this.m_NewBatch = true;
/* 241:465 */     this.m_FirstBatchDone = true;
/* 242:466 */     return numPendingOutput() != 0;
/* 243:    */   }
/* 244:    */   
/* 245:    */   public String getRevision()
/* 246:    */   {
/* 247:476 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 248:    */   }
/* 249:    */   
/* 250:    */   public static void main(String[] argv)
/* 251:    */   {
/* 252:485 */     runFilter(new StratifiedRemoveFolds(), argv);
/* 253:    */   }
/* 254:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.supervised.instance.StratifiedRemoveFolds
 * JD-Core Version:    0.7.0.1
 */