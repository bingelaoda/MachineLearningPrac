/*   1:    */ package weka.classifiers.bayes;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import weka.classifiers.AbstractClassifier;
/*   5:    */ import weka.core.Attribute;
/*   6:    */ import weka.core.Capabilities;
/*   7:    */ import weka.core.Capabilities.Capability;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.RevisionUtils;
/*  11:    */ import weka.core.SpecialFunctions;
/*  12:    */ import weka.core.TechnicalInformation;
/*  13:    */ import weka.core.TechnicalInformation.Field;
/*  14:    */ import weka.core.TechnicalInformation.Type;
/*  15:    */ import weka.core.TechnicalInformationHandler;
/*  16:    */ import weka.core.Utils;
/*  17:    */ import weka.core.WeightedInstancesHandler;
/*  18:    */ 
/*  19:    */ public class NaiveBayesMultinomial
/*  20:    */   extends AbstractClassifier
/*  21:    */   implements WeightedInstancesHandler, TechnicalInformationHandler
/*  22:    */ {
/*  23:    */   static final long serialVersionUID = 5932177440181257085L;
/*  24:    */   protected double[][] m_probOfWordGivenClass;
/*  25:    */   protected double[] m_probOfClass;
/*  26:    */   protected int m_numAttributes;
/*  27:    */   protected int m_numClasses;
/*  28:100 */   protected double[] m_lnFactorialCache = { 0.0D, 0.0D };
/*  29:    */   protected Instances m_headerInfo;
/*  30:    */   
/*  31:    */   public String globalInfo()
/*  32:    */   {
/*  33:111 */     return "Class for building and using a multinomial Naive Bayes classifier. For more information see,\n\n" + getTechnicalInformation().toString() + "\n\n" + "The core equation for this classifier:\n\n" + "P[Ci|D] = (P[D|Ci] x P[Ci]) / P[D] (Bayes rule)\n\n" + "where Ci is class i and D is a document.";
/*  34:    */   }
/*  35:    */   
/*  36:    */   public TechnicalInformation getTechnicalInformation()
/*  37:    */   {
/*  38:130 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  39:131 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Andrew Mccallum and Kamal Nigam");
/*  40:132 */     result.setValue(TechnicalInformation.Field.YEAR, "1998");
/*  41:133 */     result.setValue(TechnicalInformation.Field.TITLE, "A Comparison of Event Models for Naive Bayes Text Classification");
/*  42:134 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "AAAI-98 Workshop on 'Learning for Text Categorization'");
/*  43:    */     
/*  44:136 */     return result;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public Capabilities getCapabilities()
/*  48:    */   {
/*  49:145 */     Capabilities result = super.getCapabilities();
/*  50:146 */     result.disableAll();
/*  51:    */     
/*  52:    */ 
/*  53:149 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  54:    */     
/*  55:    */ 
/*  56:152 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  57:153 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  58:    */     
/*  59:155 */     return result;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void buildClassifier(Instances instances)
/*  63:    */     throws Exception
/*  64:    */   {
/*  65:167 */     getCapabilities().testWithFail(instances);
/*  66:    */     
/*  67:    */ 
/*  68:170 */     instances = new Instances(instances);
/*  69:171 */     instances.deleteWithMissingClass();
/*  70:    */     
/*  71:173 */     this.m_headerInfo = new Instances(instances, 0);
/*  72:174 */     this.m_numClasses = instances.numClasses();
/*  73:175 */     this.m_numAttributes = instances.numAttributes();
/*  74:176 */     this.m_probOfWordGivenClass = new double[this.m_numClasses][];
/*  75:183 */     for (int c = 0; c < this.m_numClasses; c++)
/*  76:    */     {
/*  77:185 */       this.m_probOfWordGivenClass[c] = new double[this.m_numAttributes];
/*  78:186 */       for (int att = 0; att < this.m_numAttributes; att++) {
/*  79:188 */         this.m_probOfWordGivenClass[c][att] = 1.0D;
/*  80:    */       }
/*  81:    */     }
/*  82:196 */     double[] docsPerClass = new double[this.m_numClasses];
/*  83:197 */     double[] wordsPerClass = new double[this.m_numClasses];
/*  84:    */     
/*  85:199 */     Enumeration<Instance> enumInsts = instances.enumerateInstances();
/*  86:200 */     while (enumInsts.hasMoreElements())
/*  87:    */     {
/*  88:202 */       Instance instance = (Instance)enumInsts.nextElement();
/*  89:203 */       int classIndex = (int)instance.value(instance.classIndex());
/*  90:204 */       docsPerClass[classIndex] += instance.weight();
/*  91:206 */       for (int a = 0; a < instance.numValues(); a++) {
/*  92:207 */         if (instance.index(a) != instance.classIndex()) {
/*  93:209 */           if (!instance.isMissingSparse(a))
/*  94:    */           {
/*  95:211 */             double numOccurences = instance.valueSparse(a) * instance.weight();
/*  96:212 */             if (numOccurences < 0.0D) {
/*  97:213 */               throw new Exception("Numeric attribute values must all be greater or equal to zero.");
/*  98:    */             }
/*  99:214 */             wordsPerClass[classIndex] += numOccurences;
/* 100:215 */             this.m_probOfWordGivenClass[classIndex][instance.index(a)] += numOccurences;
/* 101:    */           }
/* 102:    */         }
/* 103:    */       }
/* 104:    */     }
/* 105:224 */     for (int c = 0; c < this.m_numClasses; c++) {
/* 106:225 */       for (int v = 0; v < this.m_numAttributes; v++) {
/* 107:226 */         this.m_probOfWordGivenClass[c][v] = Math.log(this.m_probOfWordGivenClass[c][v] / (wordsPerClass[c] + this.m_numAttributes - 1.0D));
/* 108:    */       }
/* 109:    */     }
/* 110:233 */     double numDocs = instances.sumOfWeights() + this.m_numClasses;
/* 111:234 */     this.m_probOfClass = new double[this.m_numClasses];
/* 112:235 */     for (int h = 0; h < this.m_numClasses; h++) {
/* 113:236 */       this.m_probOfClass[h] = ((docsPerClass[h] + 1.0D) / numDocs);
/* 114:    */     }
/* 115:    */   }
/* 116:    */   
/* 117:    */   public double[] distributionForInstance(Instance instance)
/* 118:    */     throws Exception
/* 119:    */   {
/* 120:249 */     double[] probOfClassGivenDoc = new double[this.m_numClasses];
/* 121:    */     
/* 122:    */ 
/* 123:252 */     double[] logDocGivenClass = new double[this.m_numClasses];
/* 124:253 */     for (int h = 0; h < this.m_numClasses; h++) {
/* 125:254 */       logDocGivenClass[h] = probOfDocGivenClass(instance, h);
/* 126:    */     }
/* 127:256 */     double max = logDocGivenClass[Utils.maxIndex(logDocGivenClass)];
/* 128:257 */     double probOfDoc = 0.0D;
/* 129:259 */     for (int i = 0; i < this.m_numClasses; i++)
/* 130:    */     {
/* 131:261 */       probOfClassGivenDoc[i] = (Math.exp(logDocGivenClass[i] - max) * this.m_probOfClass[i]);
/* 132:262 */       probOfDoc += probOfClassGivenDoc[i];
/* 133:    */     }
/* 134:265 */     Utils.normalize(probOfClassGivenDoc, probOfDoc);
/* 135:    */     
/* 136:267 */     return probOfClassGivenDoc;
/* 137:    */   }
/* 138:    */   
/* 139:    */   private double probOfDocGivenClass(Instance inst, int classIndex)
/* 140:    */   {
/* 141:286 */     double answer = 0.0D;
/* 142:290 */     for (int i = 0; i < inst.numValues(); i++) {
/* 143:291 */       if (inst.index(i) != inst.classIndex())
/* 144:    */       {
/* 145:293 */         double freqOfWordInDoc = inst.valueSparse(i);
/* 146:    */         
/* 147:295 */         answer += freqOfWordInDoc * this.m_probOfWordGivenClass[classIndex][inst.index(i)];
/* 148:    */       }
/* 149:    */     }
/* 150:303 */     return answer;
/* 151:    */   }
/* 152:    */   
/* 153:    */   public double lnFactorial(int n)
/* 154:    */   {
/* 155:323 */     if (n < 0) {
/* 156:323 */       return SpecialFunctions.lnFactorial(n);
/* 157:    */     }
/* 158:325 */     if (this.m_lnFactorialCache.length <= n)
/* 159:    */     {
/* 160:326 */       double[] tmp = new double[n + 1];
/* 161:327 */       System.arraycopy(this.m_lnFactorialCache, 0, tmp, 0, this.m_lnFactorialCache.length);
/* 162:328 */       for (int i = this.m_lnFactorialCache.length; i < tmp.length; i++) {
/* 163:329 */         tmp[i] = (tmp[(i - 1)] + Math.log(i));
/* 164:    */       }
/* 165:330 */       this.m_lnFactorialCache = tmp;
/* 166:    */     }
/* 167:333 */     return this.m_lnFactorialCache[n];
/* 168:    */   }
/* 169:    */   
/* 170:    */   public String toString()
/* 171:    */   {
/* 172:343 */     StringBuffer result = new StringBuffer("The independent probability of a class\n--------------------------------------\n");
/* 173:345 */     for (int c = 0; c < this.m_numClasses; c++) {
/* 174:346 */       result.append(this.m_headerInfo.classAttribute().value(c)).append("\t").append(Double.toString(this.m_probOfClass[c])).append("\n");
/* 175:    */     }
/* 176:348 */     result.append("\nThe probability of a word given the class\n-----------------------------------------\n\t");
/* 177:350 */     for (int c = 0; c < this.m_numClasses; c++) {
/* 178:351 */       result.append(this.m_headerInfo.classAttribute().value(c)).append("\t");
/* 179:    */     }
/* 180:353 */     result.append("\n");
/* 181:355 */     for (int w = 0; w < this.m_numAttributes; w++) {
/* 182:357 */       if (w != this.m_headerInfo.classIndex())
/* 183:    */       {
/* 184:358 */         result.append(this.m_headerInfo.attribute(w).name()).append("\t");
/* 185:359 */         for (int c = 0; c < this.m_numClasses; c++) {
/* 186:360 */           result.append(Double.toString(Math.exp(this.m_probOfWordGivenClass[c][w]))).append("\t");
/* 187:    */         }
/* 188:361 */         result.append("\n");
/* 189:    */       }
/* 190:    */     }
/* 191:365 */     return result.toString();
/* 192:    */   }
/* 193:    */   
/* 194:    */   public String getRevision()
/* 195:    */   {
/* 196:374 */     return RevisionUtils.extract("$Revision: 11301 $");
/* 197:    */   }
/* 198:    */   
/* 199:    */   public static void main(String[] argv)
/* 200:    */   {
/* 201:383 */     runClassifier(new NaiveBayesMultinomial(), argv);
/* 202:    */   }
/* 203:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.NaiveBayesMultinomial
 * JD-Core Version:    0.7.0.1
 */