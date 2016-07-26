/*   1:    */ package weka.classifiers.bayes;
/*   2:    */ 
/*   3:    */ import weka.classifiers.UpdateableClassifier;
/*   4:    */ import weka.core.Attribute;
/*   5:    */ import weka.core.Capabilities;
/*   6:    */ import weka.core.Instance;
/*   7:    */ import weka.core.Instances;
/*   8:    */ import weka.core.RevisionUtils;
/*   9:    */ import weka.core.Utils;
/*  10:    */ 
/*  11:    */ public class NaiveBayesMultinomialUpdateable
/*  12:    */   extends NaiveBayesMultinomial
/*  13:    */   implements UpdateableClassifier
/*  14:    */ {
/*  15:    */   private static final long serialVersionUID = -7204398796974263186L;
/*  16:    */   protected double[] m_wordsPerClass;
/*  17:    */   
/*  18:    */   public String globalInfo()
/*  19:    */   {
/*  20: 94 */     return super.globalInfo() + "\n\n" + "Incremental version of the algorithm.";
/*  21:    */   }
/*  22:    */   
/*  23:    */   public void buildClassifier(Instances instances)
/*  24:    */     throws Exception
/*  25:    */   {
/*  26:107 */     getCapabilities().testWithFail(instances);
/*  27:    */     
/*  28:    */ 
/*  29:110 */     instances = new Instances(instances);
/*  30:111 */     instances.deleteWithMissingClass();
/*  31:    */     
/*  32:113 */     this.m_headerInfo = new Instances(instances, 0);
/*  33:114 */     this.m_numClasses = instances.numClasses();
/*  34:115 */     this.m_numAttributes = instances.numAttributes();
/*  35:116 */     this.m_probOfWordGivenClass = new double[this.m_numClasses][];
/*  36:117 */     this.m_wordsPerClass = new double[this.m_numClasses];
/*  37:118 */     this.m_probOfClass = new double[this.m_numClasses];
/*  38:    */     
/*  39:    */ 
/*  40:    */ 
/*  41:    */ 
/*  42:123 */     double laplace = 1.0D;
/*  43:124 */     for (int c = 0; c < this.m_numClasses; c++)
/*  44:    */     {
/*  45:125 */       this.m_probOfWordGivenClass[c] = new double[this.m_numAttributes];
/*  46:126 */       this.m_probOfClass[c] = laplace;
/*  47:127 */       this.m_wordsPerClass[c] = (laplace * this.m_numAttributes);
/*  48:128 */       for (int att = 0; att < this.m_numAttributes; att++) {
/*  49:129 */         this.m_probOfWordGivenClass[c][att] = laplace;
/*  50:    */       }
/*  51:    */     }
/*  52:133 */     for (int i = 0; i < instances.numInstances(); i++) {
/*  53:134 */       updateClassifier(instances.instance(i));
/*  54:    */     }
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void updateClassifier(Instance instance)
/*  58:    */     throws Exception
/*  59:    */   {
/*  60:146 */     int classIndex = (int)instance.value(instance.classIndex());
/*  61:147 */     this.m_probOfClass[classIndex] += instance.weight();
/*  62:149 */     for (int a = 0; a < instance.numValues(); a++) {
/*  63:150 */       if ((instance.index(a) != instance.classIndex()) && (!instance.isMissingSparse(a)))
/*  64:    */       {
/*  65:155 */         double numOccurences = instance.valueSparse(a) * instance.weight();
/*  66:    */         
/*  67:    */ 
/*  68:    */ 
/*  69:    */ 
/*  70:160 */         this.m_wordsPerClass[classIndex] += numOccurences;
/*  71:161 */         if (this.m_wordsPerClass[classIndex] < 0.0D) {
/*  72:162 */           throw new Exception("Can't have a negative number of words for class " + (classIndex + 1));
/*  73:    */         }
/*  74:165 */         this.m_probOfWordGivenClass[classIndex][instance.index(a)] += numOccurences;
/*  75:166 */         if (this.m_probOfWordGivenClass[classIndex][instance.index(a)] < 0.0D) {
/*  76:167 */           throw new Exception("Can't have a negative conditional sum for attribute " + instance.index(a));
/*  77:    */         }
/*  78:    */       }
/*  79:    */     }
/*  80:    */   }
/*  81:    */   
/*  82:    */   public double[] distributionForInstance(Instance instance)
/*  83:    */     throws Exception
/*  84:    */   {
/*  85:183 */     double[] probOfClassGivenDoc = new double[this.m_numClasses];
/*  86:    */     
/*  87:    */ 
/*  88:186 */     double[] logDocGivenClass = new double[this.m_numClasses];
/*  89:187 */     for (int c = 0; c < this.m_numClasses; c++)
/*  90:    */     {
/*  91:188 */       logDocGivenClass[c] += Math.log(this.m_probOfClass[c]);
/*  92:189 */       int allWords = 0;
/*  93:190 */       for (int i = 0; i < instance.numValues(); i++) {
/*  94:191 */         if (instance.index(i) != instance.classIndex())
/*  95:    */         {
/*  96:194 */           double frequencies = instance.valueSparse(i);
/*  97:195 */           allWords = (int)(allWords + frequencies);
/*  98:196 */           logDocGivenClass[c] += frequencies * Math.log(this.m_probOfWordGivenClass[c][instance.index(i)]);
/*  99:    */         }
/* 100:    */       }
/* 101:199 */       logDocGivenClass[c] -= allWords * Math.log(this.m_wordsPerClass[c]);
/* 102:    */     }
/* 103:202 */     double max = logDocGivenClass[Utils.maxIndex(logDocGivenClass)];
/* 104:203 */     for (int i = 0; i < this.m_numClasses; i++) {
/* 105:204 */       probOfClassGivenDoc[i] = Math.exp(logDocGivenClass[i] - max);
/* 106:    */     }
/* 107:207 */     Utils.normalize(probOfClassGivenDoc);
/* 108:    */     
/* 109:209 */     return probOfClassGivenDoc;
/* 110:    */   }
/* 111:    */   
/* 112:    */   public String toString()
/* 113:    */   {
/* 114:219 */     StringBuffer result = new StringBuffer();
/* 115:    */     
/* 116:221 */     result.append("Dictionary size: " + this.m_numAttributes).append("\n\n");
/* 117:    */     
/* 118:223 */     result.append("The independent frequency of a class\n");
/* 119:224 */     result.append("--------------------------------------\n");
/* 120:226 */     for (int c = 0; c < this.m_numClasses; c++) {
/* 121:227 */       result.append(this.m_headerInfo.classAttribute().value(c)).append("\t").append(Double.toString(this.m_probOfClass[c])).append("\n");
/* 122:    */     }
/* 123:231 */     result.append("\nThe frequency of a word given the class\n");
/* 124:232 */     result.append("-----------------------------------------\n");
/* 125:234 */     for (int c = 0; c < this.m_numClasses; c++) {
/* 126:235 */       result.append(Utils.padLeft(this.m_headerInfo.classAttribute().value(c), 11)).append("\t");
/* 127:    */     }
/* 128:239 */     result.append("\n");
/* 129:241 */     for (int w = 0; w < this.m_numAttributes; w++) {
/* 130:242 */       if (w != this.m_headerInfo.classIndex())
/* 131:    */       {
/* 132:245 */         for (int c = 0; c < this.m_numClasses; c++) {
/* 133:246 */           result.append(Utils.padLeft(Double.toString(this.m_probOfWordGivenClass[c][w]), 11)).append("\t");
/* 134:    */         }
/* 135:250 */         result.append(this.m_headerInfo.attribute(w).name());
/* 136:251 */         result.append("\n");
/* 137:    */       }
/* 138:    */     }
/* 139:254 */     return result.toString();
/* 140:    */   }
/* 141:    */   
/* 142:    */   public String getRevision()
/* 143:    */   {
/* 144:264 */     return RevisionUtils.extract("$Revision: 11301 $");
/* 145:    */   }
/* 146:    */   
/* 147:    */   public static void main(String[] args)
/* 148:    */   {
/* 149:273 */     runClassifier(new NaiveBayesMultinomialUpdateable(), args);
/* 150:    */   }
/* 151:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.NaiveBayesMultinomialUpdateable
 * JD-Core Version:    0.7.0.1
 */