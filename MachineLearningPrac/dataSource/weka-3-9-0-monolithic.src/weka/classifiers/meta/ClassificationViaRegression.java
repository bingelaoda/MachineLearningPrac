/*   1:    */ package weka.classifiers.meta;
/*   2:    */ 
/*   3:    */ import weka.classifiers.AbstractClassifier;
/*   4:    */ import weka.classifiers.Classifier;
/*   5:    */ import weka.classifiers.SingleClassifierEnhancer;
/*   6:    */ import weka.classifiers.trees.M5P;
/*   7:    */ import weka.core.Capabilities;
/*   8:    */ import weka.core.Capabilities.Capability;
/*   9:    */ import weka.core.Instance;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ import weka.core.TechnicalInformation;
/*  13:    */ import weka.core.TechnicalInformation.Field;
/*  14:    */ import weka.core.TechnicalInformation.Type;
/*  15:    */ import weka.core.TechnicalInformationHandler;
/*  16:    */ import weka.core.UnassignedClassException;
/*  17:    */ import weka.core.Utils;
/*  18:    */ import weka.filters.Filter;
/*  19:    */ import weka.filters.unsupervised.attribute.MakeIndicator;
/*  20:    */ 
/*  21:    */ public class ClassificationViaRegression
/*  22:    */   extends SingleClassifierEnhancer
/*  23:    */   implements TechnicalInformationHandler
/*  24:    */ {
/*  25:    */   static final long serialVersionUID = 4500023123618669859L;
/*  26:    */   private Classifier[] m_Classifiers;
/*  27:    */   private MakeIndicator[] m_ClassFilters;
/*  28:    */   
/*  29:    */   public ClassificationViaRegression()
/*  30:    */   {
/*  31:121 */     this.m_Classifier = new M5P();
/*  32:    */   }
/*  33:    */   
/*  34:    */   public String globalInfo()
/*  35:    */   {
/*  36:131 */     return "Class for doing classification using regression methods. Class is binarized and one regression model is built for each class value. For more information, see, for example\n\n" + getTechnicalInformation().toString();
/*  37:    */   }
/*  38:    */   
/*  39:    */   public TechnicalInformation getTechnicalInformation()
/*  40:    */   {
/*  41:147 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  42:148 */     result.setValue(TechnicalInformation.Field.AUTHOR, "E. Frank and Y. Wang and S. Inglis and G. Holmes and I.H. Witten");
/*  43:149 */     result.setValue(TechnicalInformation.Field.YEAR, "1998");
/*  44:150 */     result.setValue(TechnicalInformation.Field.TITLE, "Using model trees for classification");
/*  45:151 */     result.setValue(TechnicalInformation.Field.JOURNAL, "Machine Learning");
/*  46:152 */     result.setValue(TechnicalInformation.Field.VOLUME, "32");
/*  47:153 */     result.setValue(TechnicalInformation.Field.NUMBER, "1");
/*  48:154 */     result.setValue(TechnicalInformation.Field.PAGES, "63-76");
/*  49:    */     
/*  50:156 */     return result;
/*  51:    */   }
/*  52:    */   
/*  53:    */   protected String defaultClassifierString()
/*  54:    */   {
/*  55:166 */     return "weka.classifiers.trees.M5P";
/*  56:    */   }
/*  57:    */   
/*  58:    */   public Capabilities getCapabilities()
/*  59:    */   {
/*  60:175 */     Capabilities result = super.getCapabilities();
/*  61:    */     
/*  62:    */ 
/*  63:178 */     result.disableAllClasses();
/*  64:179 */     result.disableAllClassDependencies();
/*  65:180 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  66:    */     
/*  67:182 */     return result;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public void buildClassifier(Instances insts)
/*  71:    */     throws Exception
/*  72:    */   {
/*  73:196 */     getCapabilities().testWithFail(insts);
/*  74:    */     
/*  75:    */ 
/*  76:199 */     insts = new Instances(insts);
/*  77:200 */     insts.deleteWithMissingClass();
/*  78:    */     
/*  79:202 */     this.m_Classifiers = AbstractClassifier.makeCopies(this.m_Classifier, insts.numClasses());
/*  80:203 */     this.m_ClassFilters = new MakeIndicator[insts.numClasses()];
/*  81:204 */     for (int i = 0; i < insts.numClasses(); i++)
/*  82:    */     {
/*  83:205 */       this.m_ClassFilters[i] = new MakeIndicator();
/*  84:206 */       this.m_ClassFilters[i].setAttributeIndex("" + (insts.classIndex() + 1));
/*  85:207 */       this.m_ClassFilters[i].setValueIndex(i);
/*  86:208 */       this.m_ClassFilters[i].setNumeric(true);
/*  87:209 */       this.m_ClassFilters[i].setInputFormat(insts);
/*  88:210 */       Instances newInsts = Filter.useFilter(insts, this.m_ClassFilters[i]);
/*  89:211 */       this.m_Classifiers[i].buildClassifier(newInsts);
/*  90:    */     }
/*  91:    */   }
/*  92:    */   
/*  93:    */   public double[] distributionForInstance(Instance inst)
/*  94:    */     throws Exception
/*  95:    */   {
/*  96:224 */     double[] probs = new double[inst.numClasses()];
/*  97:    */     
/*  98:226 */     double sum = 0.0D;
/*  99:228 */     for (int i = 0; i < inst.numClasses(); i++)
/* 100:    */     {
/* 101:229 */       this.m_ClassFilters[i].input(inst);
/* 102:230 */       this.m_ClassFilters[i].batchFinished();
/* 103:231 */       Instance newInst = this.m_ClassFilters[i].output();
/* 104:232 */       probs[i] = this.m_Classifiers[i].classifyInstance(newInst);
/* 105:233 */       if (Utils.isMissingValue(probs[i])) {
/* 106:234 */         throw new UnassignedClassException("ClassificationViaRegression: base learner predicted missing value.");
/* 107:    */       }
/* 108:236 */       if (probs[i] > 1.0D) {
/* 109:237 */         probs[i] = 1.0D;
/* 110:    */       }
/* 111:239 */       if (probs[i] < 0.0D) {
/* 112:240 */         probs[i] = 0.0D;
/* 113:    */       }
/* 114:242 */       sum += probs[i];
/* 115:    */     }
/* 116:244 */     if (sum != 0.0D) {
/* 117:245 */       Utils.normalize(probs, sum);
/* 118:    */     }
/* 119:247 */     return probs;
/* 120:    */   }
/* 121:    */   
/* 122:    */   public String toString()
/* 123:    */   {
/* 124:257 */     if (this.m_Classifiers == null) {
/* 125:258 */       return "Classification via Regression: No model built yet.";
/* 126:    */     }
/* 127:260 */     StringBuffer text = new StringBuffer();
/* 128:261 */     text.append("Classification via Regression\n\n");
/* 129:262 */     for (int i = 0; i < this.m_Classifiers.length; i++)
/* 130:    */     {
/* 131:263 */       text.append("Classifier for class with index " + i + ":\n\n");
/* 132:264 */       text.append(this.m_Classifiers[i].toString() + "\n\n");
/* 133:    */     }
/* 134:266 */     return text.toString();
/* 135:    */   }
/* 136:    */   
/* 137:    */   public String getRevision()
/* 138:    */   {
/* 139:275 */     return RevisionUtils.extract("$Revision: 10470 $");
/* 140:    */   }
/* 141:    */   
/* 142:    */   public static void main(String[] argv)
/* 143:    */   {
/* 144:284 */     runClassifier(new ClassificationViaRegression(), argv);
/* 145:    */   }
/* 146:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.ClassificationViaRegression
 * JD-Core Version:    0.7.0.1
 */