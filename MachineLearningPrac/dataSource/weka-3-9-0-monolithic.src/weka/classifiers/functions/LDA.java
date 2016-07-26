/*   1:    */ package weka.classifiers.functions;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.classifiers.AbstractClassifier;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.Capabilities;
/*   9:    */ import weka.core.Capabilities.Capability;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.Option;
/*  13:    */ import weka.core.RevisionUtils;
/*  14:    */ import weka.core.Utils;
/*  15:    */ import weka.core.WeightedInstancesHandler;
/*  16:    */ import weka.estimators.MultivariateGaussianEstimator;
/*  17:    */ import weka.filters.Filter;
/*  18:    */ import weka.filters.unsupervised.attribute.RemoveUseless;
/*  19:    */ 
/*  20:    */ public class LDA
/*  21:    */   extends AbstractClassifier
/*  22:    */   implements WeightedInstancesHandler
/*  23:    */ {
/*  24:    */   static final long serialVersionUID = -8213283598193689271L;
/*  25:    */   protected Instances m_Data;
/*  26:    */   protected MultivariateGaussianEstimator m_Estimator;
/*  27:    */   protected double[][] m_Means;
/*  28:    */   protected double[] m_GlobalMean;
/*  29:    */   protected double[] m_LogPriors;
/*  30: 80 */   protected double m_Ridge = 1.0E-006D;
/*  31:    */   protected RemoveUseless m_RemoveUseless;
/*  32:    */   
/*  33:    */   public String globalInfo()
/*  34:    */   {
/*  35: 89 */     return "Generates an LDA model. The covariance matrix is estimated using maximum likelihood from the pooled data.";
/*  36:    */   }
/*  37:    */   
/*  38:    */   public String ridgeTipText()
/*  39:    */   {
/*  40: 99 */     return "The value of the ridge parameter.";
/*  41:    */   }
/*  42:    */   
/*  43:    */   public double getRidge()
/*  44:    */   {
/*  45:109 */     return this.m_Ridge;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void setRidge(double newRidge)
/*  49:    */   {
/*  50:119 */     this.m_Ridge = newRidge;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public Enumeration<Option> listOptions()
/*  54:    */   {
/*  55:129 */     Vector<Option> newVector = new Vector(7);
/*  56:    */     
/*  57:131 */     newVector.addElement(new Option("\tThe ridge parameter.\n\t(default is 1e-6)", "R", 0, "-R"));
/*  58:    */     
/*  59:    */ 
/*  60:    */ 
/*  61:    */ 
/*  62:136 */     newVector.addAll(Collections.list(super.listOptions()));
/*  63:    */     
/*  64:138 */     return newVector.elements();
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void setOptions(String[] options)
/*  68:    */     throws Exception
/*  69:    */   {
/*  70:166 */     String ridgeString = Utils.getOption('R', options);
/*  71:167 */     if (ridgeString.length() != 0) {
/*  72:168 */       setRidge(Double.parseDouble(ridgeString));
/*  73:    */     } else {
/*  74:170 */       setRidge(1.0E-006D);
/*  75:    */     }
/*  76:173 */     super.setOptions(options);
/*  77:    */     
/*  78:175 */     Utils.checkForRemainingOptions(options);
/*  79:    */   }
/*  80:    */   
/*  81:    */   public String[] getOptions()
/*  82:    */   {
/*  83:185 */     Vector<String> options = new Vector();
/*  84:186 */     options.add("-R");options.add("" + getRidge());
/*  85:    */     
/*  86:188 */     Collections.addAll(options, super.getOptions());
/*  87:    */     
/*  88:190 */     return (String[])options.toArray(new String[0]);
/*  89:    */   }
/*  90:    */   
/*  91:    */   public Capabilities getCapabilities()
/*  92:    */   {
/*  93:199 */     Capabilities result = super.getCapabilities();
/*  94:200 */     result.disableAll();
/*  95:    */     
/*  96:    */ 
/*  97:203 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  98:    */     
/*  99:    */ 
/* 100:206 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 101:207 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 102:    */     
/* 103:    */ 
/* 104:210 */     result.setMinimumNumberInstances(0);
/* 105:    */     
/* 106:212 */     return result;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public void buildClassifier(Instances insts)
/* 110:    */     throws Exception
/* 111:    */   {
/* 112:221 */     getCapabilities().testWithFail(insts);
/* 113:    */     
/* 114:    */ 
/* 115:224 */     this.m_RemoveUseless = new RemoveUseless();
/* 116:225 */     this.m_RemoveUseless.setInputFormat(insts);
/* 117:226 */     insts = Filter.useFilter(insts, this.m_RemoveUseless);
/* 118:227 */     insts.deleteWithMissingClass();
/* 119:229 */     if (insts.numInstances() == 0)
/* 120:    */     {
/* 121:230 */       this.m_Data = new Instances(insts, 0);
/* 122:231 */       this.m_Means = new double[insts.numClasses()][];
/* 123:232 */       return;
/* 124:    */     }
/* 125:236 */     int[] counts = new int[insts.numClasses()];
/* 126:237 */     double[] sumOfWeightsPerClass = new double[insts.numClasses()];
/* 127:238 */     for (int i = 0; i < insts.numInstances(); i++)
/* 128:    */     {
/* 129:239 */       Instance inst = insts.instance(i);
/* 130:240 */       int classIndex = (int)inst.classValue();
/* 131:241 */       counts[classIndex] += 1;
/* 132:242 */       sumOfWeightsPerClass[classIndex] += inst.weight();
/* 133:    */     }
/* 134:246 */     double[][][] data = new double[insts.numClasses()][][];
/* 135:247 */     double[][] weights = new double[insts.numClasses()][];
/* 136:248 */     for (int i = 0; i < insts.numClasses(); i++)
/* 137:    */     {
/* 138:249 */       data[i] = new double[counts[i]][insts.numAttributes() - 1];
/* 139:250 */       weights[i] = new double[counts[i]];
/* 140:    */     }
/* 141:252 */     int[] currentCount = new int[insts.numClasses()];
/* 142:253 */     for (int i = 0; i < insts.numInstances(); i++)
/* 143:    */     {
/* 144:254 */       Instance inst = insts.instance(i);
/* 145:255 */       int classIndex = (int)inst.classValue();
/* 146:256 */       weights[classIndex][currentCount[classIndex]] = inst.weight();
/* 147:257 */       int index = 0; int 
/* 148:258 */         tmp279_277 = classIndex; int[] tmp279_275 = currentCount; int tmp281_280 = tmp279_275[tmp279_277];tmp279_275[tmp279_277] = (tmp281_280 + 1);double[] row = data[classIndex][tmp281_280];
/* 149:259 */       for (int j = 0; j < inst.numAttributes(); j++) {
/* 150:260 */         if (j != insts.classIndex()) {
/* 151:261 */           row[(index++)] = inst.value(j);
/* 152:    */         }
/* 153:    */       }
/* 154:    */     }
/* 155:267 */     this.m_Estimator = new MultivariateGaussianEstimator();
/* 156:268 */     this.m_Estimator.setRidge(getRidge());
/* 157:269 */     this.m_Means = this.m_Estimator.estimatePooled(data, weights);
/* 158:270 */     this.m_GlobalMean = this.m_Estimator.getMean();
/* 159:    */     
/* 160:    */ 
/* 161:273 */     this.m_LogPriors = new double[insts.numClasses()];
/* 162:274 */     double sumOfWeights = Utils.sum(sumOfWeightsPerClass);
/* 163:275 */     for (int i = 0; i < insts.numClasses(); i++) {
/* 164:276 */       if (sumOfWeightsPerClass[i] > 0.0D) {
/* 165:277 */         this.m_LogPriors[i] = (Math.log(sumOfWeightsPerClass[i]) - Math.log(sumOfWeights));
/* 166:    */       }
/* 167:    */     }
/* 168:282 */     this.m_Data = new Instances(insts, 0);
/* 169:    */   }
/* 170:    */   
/* 171:    */   public double[] distributionForInstance(Instance inst)
/* 172:    */     throws Exception
/* 173:    */   {
/* 174:290 */     this.m_RemoveUseless.input(inst);
/* 175:291 */     inst = this.m_RemoveUseless.output();
/* 176:    */     
/* 177:    */ 
/* 178:294 */     double[] posteriorProbs = new double[this.m_Data.numClasses()];
/* 179:295 */     double[] values = new double[inst.numAttributes() - 1];
/* 180:296 */     for (int i = 0; i < this.m_Data.numClasses(); i++) {
/* 181:297 */       if (this.m_Means[i] != null)
/* 182:    */       {
/* 183:298 */         int index = 0;
/* 184:299 */         for (int j = 0; j < this.m_Data.numAttributes(); j++) {
/* 185:300 */           if (j != this.m_Data.classIndex())
/* 186:    */           {
/* 187:301 */             values[index] = (inst.value(j) - this.m_Means[i][index] + this.m_GlobalMean[index]);
/* 188:302 */             index++;
/* 189:    */           }
/* 190:    */         }
/* 191:305 */         posteriorProbs[i] = (this.m_Estimator.logDensity(values) + this.m_LogPriors[i]);
/* 192:    */       }
/* 193:    */       else
/* 194:    */       {
/* 195:307 */         posteriorProbs[i] = -1.797693134862316E+308D;
/* 196:    */       }
/* 197:    */     }
/* 198:310 */     posteriorProbs = Utils.logs2probs(posteriorProbs);
/* 199:311 */     return posteriorProbs;
/* 200:    */   }
/* 201:    */   
/* 202:    */   public String toString()
/* 203:    */   {
/* 204:320 */     if (this.m_Means == null) {
/* 205:321 */       return "No model has been built yet.";
/* 206:    */     }
/* 207:323 */     StringBuffer result = new StringBuffer();
/* 208:324 */     result.append("LDA model (multivariate Gaussian for each class)\n\n");
/* 209:    */     
/* 210:326 */     result.append("Pooled estimator\n\n" + this.m_Estimator + "\n\n");
/* 211:327 */     for (int i = 0; i < this.m_Data.numClasses(); i++) {
/* 212:328 */       if (this.m_Means[i] != null)
/* 213:    */       {
/* 214:329 */         result.append("Estimates for class value " + this.m_Data.classAttribute().value(i) + "\n\n");
/* 215:330 */         result.append("Natural logarithm of class prior probability: " + Utils.doubleToString(this.m_LogPriors[i], getNumDecimalPlaces()) + "\n");
/* 216:    */         
/* 217:332 */         result.append("Class prior probability: " + Utils.doubleToString(Math.exp(this.m_LogPriors[i]), getNumDecimalPlaces()) + "\n\n");
/* 218:    */         
/* 219:334 */         int index = 0;
/* 220:335 */         result.append("Mean vector:\n\n");
/* 221:336 */         for (int j = 0; j < this.m_Data.numAttributes(); j++) {
/* 222:337 */           if (j != this.m_Data.classIndex())
/* 223:    */           {
/* 224:338 */             result.append(this.m_Data.attribute(index).name() + ": " + Utils.doubleToString(this.m_Means[i][index], getNumDecimalPlaces()) + "\n");
/* 225:    */             
/* 226:340 */             index++;
/* 227:    */           }
/* 228:    */         }
/* 229:343 */         result.append("\n");
/* 230:    */       }
/* 231:    */     }
/* 232:346 */     return result.toString();
/* 233:    */   }
/* 234:    */   
/* 235:    */   public String getRevision()
/* 236:    */   {
/* 237:357 */     return RevisionUtils.extract("$Revision: 10382 $");
/* 238:    */   }
/* 239:    */   
/* 240:    */   public static void main(String[] argv)
/* 241:    */   {
/* 242:366 */     runClassifier(new LDA(), argv);
/* 243:    */   }
/* 244:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.LDA
 * JD-Core Version:    0.7.0.1
 */