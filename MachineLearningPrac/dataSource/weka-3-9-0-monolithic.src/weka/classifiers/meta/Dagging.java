/*   1:    */ package weka.classifiers.meta;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.classifiers.Classifier;
/*   8:    */ import weka.classifiers.RandomizableSingleClassifierEnhancer;
/*   9:    */ import weka.classifiers.trees.DecisionStump;
/*  10:    */ import weka.core.Capabilities;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.Option;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.TechnicalInformation;
/*  16:    */ import weka.core.TechnicalInformation.Field;
/*  17:    */ import weka.core.TechnicalInformation.Type;
/*  18:    */ import weka.core.TechnicalInformationHandler;
/*  19:    */ import weka.core.Utils;
/*  20:    */ 
/*  21:    */ public class Dagging
/*  22:    */   extends RandomizableSingleClassifierEnhancer
/*  23:    */   implements TechnicalInformationHandler
/*  24:    */ {
/*  25:    */   static final long serialVersionUID = 4560165876570074309L;
/*  26:124 */   protected int m_NumFolds = 10;
/*  27:127 */   protected Vote m_Vote = null;
/*  28:130 */   protected boolean m_Verbose = false;
/*  29:    */   
/*  30:    */   public String globalInfo()
/*  31:    */   {
/*  32:139 */     return "This meta classifier creates a number of disjoint, stratified folds out of the data and feeds each chunk of data to a copy of the supplied base classifier. Predictions are made via averaging, since all the generated base classifiers are put into the Vote meta classifier. \nUseful for base classifiers that are quadratic or worse in time behavior, regarding number of instances in the training data. \n\nFor more information, see: \n" + getTechnicalInformation().toString();
/*  33:    */   }
/*  34:    */   
/*  35:    */   public TechnicalInformation getTechnicalInformation()
/*  36:    */   {
/*  37:161 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  38:162 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Ting, K. M. and Witten, I. H.");
/*  39:163 */     result.setValue(TechnicalInformation.Field.TITLE, "Stacking Bagged and Dagged Models");
/*  40:164 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Fourteenth international Conference on Machine Learning");
/*  41:    */     
/*  42:166 */     result.setValue(TechnicalInformation.Field.EDITOR, "D. H. Fisher");
/*  43:167 */     result.setValue(TechnicalInformation.Field.YEAR, "1997");
/*  44:168 */     result.setValue(TechnicalInformation.Field.PAGES, "367-375");
/*  45:169 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Morgan Kaufmann Publishers");
/*  46:170 */     result.setValue(TechnicalInformation.Field.ADDRESS, "San Francisco, CA");
/*  47:    */     
/*  48:172 */     return result;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public Dagging()
/*  52:    */   {
/*  53:179 */     this.m_Classifier = new DecisionStump();
/*  54:    */   }
/*  55:    */   
/*  56:    */   protected String defaultClassifierString()
/*  57:    */   {
/*  58:189 */     return DecisionStump.class.getName();
/*  59:    */   }
/*  60:    */   
/*  61:    */   public Enumeration<Option> listOptions()
/*  62:    */   {
/*  63:199 */     Vector<Option> result = new Vector();
/*  64:    */     
/*  65:201 */     result.addElement(new Option("\tThe number of folds for splitting the training set into\n\tsmaller chunks for the base classifier.\n\t(default 10)", "F", 1, "-F <folds>"));
/*  66:    */     
/*  67:    */ 
/*  68:    */ 
/*  69:    */ 
/*  70:206 */     result.addElement(new Option("\tWhether to print some more information during building the\n\tclassifier.\n\t(default is off)", "verbose", 0, "-verbose"));
/*  71:    */     
/*  72:    */ 
/*  73:    */ 
/*  74:210 */     result.addAll(Collections.list(super.listOptions()));
/*  75:    */     
/*  76:212 */     return result.elements();
/*  77:    */   }
/*  78:    */   
/*  79:    */   public void setOptions(String[] options)
/*  80:    */     throws Exception
/*  81:    */   {
/*  82:266 */     String tmpStr = Utils.getOption('F', options);
/*  83:267 */     if (tmpStr.length() != 0) {
/*  84:268 */       setNumFolds(Integer.parseInt(tmpStr));
/*  85:    */     } else {
/*  86:270 */       setNumFolds(10);
/*  87:    */     }
/*  88:273 */     setVerbose(Utils.getFlag("verbose", options));
/*  89:    */     
/*  90:275 */     super.setOptions(options);
/*  91:    */     
/*  92:277 */     Utils.checkForRemainingOptions(options);
/*  93:    */   }
/*  94:    */   
/*  95:    */   public String[] getOptions()
/*  96:    */   {
/*  97:288 */     Vector<String> result = new Vector();
/*  98:    */     
/*  99:290 */     result.add("-F");
/* 100:291 */     result.add("" + getNumFolds());
/* 101:293 */     if (getVerbose()) {
/* 102:294 */       result.add("-verbose");
/* 103:    */     }
/* 104:297 */     Collections.addAll(result, super.getOptions());
/* 105:    */     
/* 106:299 */     return (String[])result.toArray(new String[result.size()]);
/* 107:    */   }
/* 108:    */   
/* 109:    */   public int getNumFolds()
/* 110:    */   {
/* 111:308 */     return this.m_NumFolds;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public void setNumFolds(int value)
/* 115:    */   {
/* 116:317 */     if (value > 0) {
/* 117:318 */       this.m_NumFolds = value;
/* 118:    */     } else {
/* 119:320 */       System.out.println("At least 1 fold is necessary (provided: " + value + ")!");
/* 120:    */     }
/* 121:    */   }
/* 122:    */   
/* 123:    */   public String numFoldsTipText()
/* 124:    */   {
/* 125:332 */     return "The number of folds to use for splitting the training set into smaller chunks for the base classifier.";
/* 126:    */   }
/* 127:    */   
/* 128:    */   public void setVerbose(boolean value)
/* 129:    */   {
/* 130:341 */     this.m_Verbose = value;
/* 131:    */   }
/* 132:    */   
/* 133:    */   public boolean getVerbose()
/* 134:    */   {
/* 135:350 */     return this.m_Verbose;
/* 136:    */   }
/* 137:    */   
/* 138:    */   public String verboseTipText()
/* 139:    */   {
/* 140:360 */     return "Whether to ouput some additional information during building.";
/* 141:    */   }
/* 142:    */   
/* 143:    */   public void buildClassifier(Instances data)
/* 144:    */     throws Exception
/* 145:    */   {
/* 146:381 */     getCapabilities().testWithFail(data);
/* 147:    */     
/* 148:    */ 
/* 149:384 */     data = new Instances(data);
/* 150:385 */     data.deleteWithMissingClass();
/* 151:    */     
/* 152:387 */     this.m_Vote = new Vote();
/* 153:388 */     Classifier[] base = new Classifier[getNumFolds()];
/* 154:393 */     if (getNumFolds() > 1)
/* 155:    */     {
/* 156:394 */       data.randomize(data.getRandomNumberGenerator(getSeed()));
/* 157:395 */       data.stratify(getNumFolds());
/* 158:    */     }
/* 159:399 */     for (int i = 0; i < getNumFolds(); i++)
/* 160:    */     {
/* 161:400 */       base[i] = makeCopy(getClassifier());
/* 162:    */       Instances train;
/* 163:    */       Instances train;
/* 164:403 */       if (getNumFolds() > 1)
/* 165:    */       {
/* 166:405 */         if (getVerbose()) {
/* 167:406 */           System.out.print(".");
/* 168:    */         }
/* 169:409 */         train = data.testCV(getNumFolds(), i);
/* 170:    */       }
/* 171:    */       else
/* 172:    */       {
/* 173:411 */         train = data;
/* 174:    */       }
/* 175:415 */       base[i].buildClassifier(train);
/* 176:    */     }
/* 177:419 */     this.m_Vote.setClassifiers(base);
/* 178:421 */     if (getVerbose()) {
/* 179:422 */       System.out.println();
/* 180:    */     }
/* 181:    */   }
/* 182:    */   
/* 183:    */   public double[] distributionForInstance(Instance instance)
/* 184:    */     throws Exception
/* 185:    */   {
/* 186:435 */     return this.m_Vote.distributionForInstance(instance);
/* 187:    */   }
/* 188:    */   
/* 189:    */   public String toString()
/* 190:    */   {
/* 191:445 */     if (this.m_Vote == null) {
/* 192:446 */       return getClass().getName().replaceAll(".*\\.", "") + ": No model built yet.";
/* 193:    */     }
/* 194:449 */     return this.m_Vote.toString();
/* 195:    */   }
/* 196:    */   
/* 197:    */   public String getRevision()
/* 198:    */   {
/* 199:460 */     return RevisionUtils.extract("$Revision: 10338 $");
/* 200:    */   }
/* 201:    */   
/* 202:    */   public static void main(String[] args)
/* 203:    */   {
/* 204:469 */     runClassifier(new Dagging(), args);
/* 205:    */   }
/* 206:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.Dagging
 * JD-Core Version:    0.7.0.1
 */