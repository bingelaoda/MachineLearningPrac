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
/*  20:    */ public class QDA
/*  21:    */   extends AbstractClassifier
/*  22:    */   implements WeightedInstancesHandler
/*  23:    */ {
/*  24:    */   static final long serialVersionUID = -9113383498193689291L;
/*  25:    */   protected Instances m_Data;
/*  26:    */   protected MultivariateGaussianEstimator[] m_Estimators;
/*  27:    */   protected double[] m_LogPriors;
/*  28: 76 */   protected double m_Ridge = 1.0E-006D;
/*  29:    */   protected RemoveUseless m_RemoveUseless;
/*  30:    */   
/*  31:    */   public String globalInfo()
/*  32:    */   {
/*  33: 85 */     return "Generates a QDA model. The covariance matrices are estimated using maximum likelihood from the per-class data.";
/*  34:    */   }
/*  35:    */   
/*  36:    */   public String ridgeTipText()
/*  37:    */   {
/*  38: 95 */     return "The value of the ridge parameter.";
/*  39:    */   }
/*  40:    */   
/*  41:    */   public double getRidge()
/*  42:    */   {
/*  43:105 */     return this.m_Ridge;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void setRidge(double newRidge)
/*  47:    */   {
/*  48:115 */     this.m_Ridge = newRidge;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public Enumeration<Option> listOptions()
/*  52:    */   {
/*  53:125 */     Vector<Option> newVector = new Vector(7);
/*  54:    */     
/*  55:127 */     newVector.addElement(new Option("\tThe ridge parameter.\n\t(default is 1e-6)", "R", 0, "-R"));
/*  56:    */     
/*  57:    */ 
/*  58:    */ 
/*  59:    */ 
/*  60:132 */     newVector.addAll(Collections.list(super.listOptions()));
/*  61:    */     
/*  62:134 */     return newVector.elements();
/*  63:    */   }
/*  64:    */   
/*  65:    */   public void setOptions(String[] options)
/*  66:    */     throws Exception
/*  67:    */   {
/*  68:162 */     String ridgeString = Utils.getOption('R', options);
/*  69:163 */     if (ridgeString.length() != 0) {
/*  70:164 */       setRidge(Double.parseDouble(ridgeString));
/*  71:    */     } else {
/*  72:166 */       setRidge(1.0E-006D);
/*  73:    */     }
/*  74:169 */     super.setOptions(options);
/*  75:    */     
/*  76:171 */     Utils.checkForRemainingOptions(options);
/*  77:    */   }
/*  78:    */   
/*  79:    */   public String[] getOptions()
/*  80:    */   {
/*  81:181 */     Vector<String> options = new Vector();
/*  82:182 */     options.add("-R");options.add("" + getRidge());
/*  83:    */     
/*  84:184 */     Collections.addAll(options, super.getOptions());
/*  85:    */     
/*  86:186 */     return (String[])options.toArray(new String[0]);
/*  87:    */   }
/*  88:    */   
/*  89:    */   public Capabilities getCapabilities()
/*  90:    */   {
/*  91:195 */     Capabilities result = super.getCapabilities();
/*  92:196 */     result.disableAll();
/*  93:    */     
/*  94:    */ 
/*  95:199 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  96:    */     
/*  97:    */ 
/*  98:202 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  99:203 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 100:    */     
/* 101:    */ 
/* 102:206 */     result.setMinimumNumberInstances(0);
/* 103:    */     
/* 104:208 */     return result;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public void buildClassifier(Instances insts)
/* 108:    */     throws Exception
/* 109:    */   {
/* 110:217 */     getCapabilities().testWithFail(insts);
/* 111:    */     
/* 112:    */ 
/* 113:220 */     this.m_RemoveUseless = new RemoveUseless();
/* 114:221 */     this.m_RemoveUseless.setInputFormat(insts);
/* 115:222 */     insts = Filter.useFilter(insts, this.m_RemoveUseless);
/* 116:223 */     insts.deleteWithMissingClass();
/* 117:    */     
/* 118:    */ 
/* 119:226 */     int[] counts = new int[insts.numClasses()];
/* 120:227 */     double[] sumOfWeightsPerClass = new double[insts.numClasses()];
/* 121:228 */     for (int i = 0; i < insts.numInstances(); i++)
/* 122:    */     {
/* 123:229 */       Instance inst = insts.instance(i);
/* 124:230 */       int classIndex = (int)inst.classValue();
/* 125:231 */       counts[classIndex] += 1;
/* 126:232 */       sumOfWeightsPerClass[classIndex] += inst.weight();
/* 127:    */     }
/* 128:236 */     double[][][] data = new double[insts.numClasses()][][];
/* 129:237 */     double[][] weights = new double[insts.numClasses()][];
/* 130:238 */     for (int i = 0; i < insts.numClasses(); i++)
/* 131:    */     {
/* 132:239 */       data[i] = new double[counts[i]][insts.numAttributes() - 1];
/* 133:240 */       weights[i] = new double[counts[i]];
/* 134:    */     }
/* 135:242 */     int[] currentCount = new int[insts.numClasses()];
/* 136:243 */     for (int i = 0; i < insts.numInstances(); i++)
/* 137:    */     {
/* 138:244 */       Instance inst = insts.instance(i);
/* 139:245 */       int classIndex = (int)inst.classValue();
/* 140:246 */       weights[classIndex][currentCount[classIndex]] = inst.weight();
/* 141:247 */       int index = 0; int 
/* 142:248 */         tmp247_245 = classIndex; int[] tmp247_243 = currentCount; int tmp249_248 = tmp247_243[tmp247_245];tmp247_243[tmp247_245] = (tmp249_248 + 1);double[] row = data[classIndex][tmp249_248];
/* 143:249 */       for (int j = 0; j < inst.numAttributes(); j++) {
/* 144:250 */         if (j != insts.classIndex()) {
/* 145:251 */           row[(index++)] = inst.value(j);
/* 146:    */         }
/* 147:    */       }
/* 148:    */     }
/* 149:257 */     this.m_Estimators = new MultivariateGaussianEstimator[insts.numClasses()];
/* 150:258 */     for (int i = 0; i < insts.numClasses(); i++) {
/* 151:259 */       if (sumOfWeightsPerClass[i] > 0.0D)
/* 152:    */       {
/* 153:260 */         this.m_Estimators[i] = new MultivariateGaussianEstimator();
/* 154:261 */         this.m_Estimators[i].setRidge(getRidge());
/* 155:262 */         this.m_Estimators[i].estimate(data[i], weights[i]);
/* 156:    */       }
/* 157:    */     }
/* 158:267 */     this.m_LogPriors = new double[insts.numClasses()];
/* 159:268 */     double sumOfWeights = Utils.sum(sumOfWeightsPerClass);
/* 160:269 */     for (int i = 0; i < insts.numClasses(); i++) {
/* 161:270 */       if (sumOfWeightsPerClass[i] > 0.0D) {
/* 162:271 */         this.m_LogPriors[i] = (Math.log(sumOfWeightsPerClass[i]) - Math.log(sumOfWeights));
/* 163:    */       }
/* 164:    */     }
/* 165:276 */     this.m_Data = new Instances(insts, 0);
/* 166:    */   }
/* 167:    */   
/* 168:    */   public double[] distributionForInstance(Instance inst)
/* 169:    */     throws Exception
/* 170:    */   {
/* 171:285 */     this.m_RemoveUseless.input(inst);
/* 172:286 */     inst = this.m_RemoveUseless.output();
/* 173:    */     
/* 174:    */ 
/* 175:289 */     double[] values = new double[inst.numAttributes() - 1];
/* 176:290 */     int index = 0;
/* 177:291 */     for (int i = 0; i < this.m_Data.numAttributes(); i++) {
/* 178:292 */       if (i != this.m_Data.classIndex()) {
/* 179:293 */         values[(index++)] = inst.value(i);
/* 180:    */       }
/* 181:    */     }
/* 182:296 */     double[] posteriorProbs = new double[this.m_Data.numClasses()];
/* 183:297 */     for (int i = 0; i < this.m_Data.numClasses(); i++) {
/* 184:298 */       if (this.m_Estimators[i] != null) {
/* 185:299 */         posteriorProbs[i] = (this.m_Estimators[i].logDensity(values) + this.m_LogPriors[i]);
/* 186:    */       } else {
/* 187:301 */         posteriorProbs[i] = -1.797693134862316E+308D;
/* 188:    */       }
/* 189:    */     }
/* 190:304 */     posteriorProbs = Utils.logs2probs(posteriorProbs);
/* 191:305 */     return posteriorProbs;
/* 192:    */   }
/* 193:    */   
/* 194:    */   public String toString()
/* 195:    */   {
/* 196:314 */     if (this.m_LogPriors == null) {
/* 197:315 */       return "No model has been built yet.";
/* 198:    */     }
/* 199:317 */     StringBuffer result = new StringBuffer();
/* 200:318 */     result.append("QDA model (multivariate Gaussian for each class)\n\n");
/* 201:320 */     for (int i = 0; i < this.m_Data.numClasses(); i++) {
/* 202:321 */       if (this.m_Estimators[i] != null)
/* 203:    */       {
/* 204:322 */         result.append("Estimates for class " + this.m_Data.classAttribute().value(i) + "\n\n");
/* 205:323 */         result.append("Natural logarithm of class prior probability: " + Utils.doubleToString(this.m_LogPriors[i], getNumDecimalPlaces()) + "\n");
/* 206:    */         
/* 207:325 */         result.append("Class prior probability: " + Utils.doubleToString(Math.exp(this.m_LogPriors[i]), getNumDecimalPlaces()) + "\n\n");
/* 208:    */         
/* 209:327 */         result.append("Multivariate Gaussian estimator:\n\n" + this.m_Estimators[i] + "\n");
/* 210:    */       }
/* 211:    */     }
/* 212:330 */     return result.toString();
/* 213:    */   }
/* 214:    */   
/* 215:    */   public String getRevision()
/* 216:    */   {
/* 217:341 */     return RevisionUtils.extract("$Revision: 10382 $");
/* 218:    */   }
/* 219:    */   
/* 220:    */   public static void main(String[] argv)
/* 221:    */   {
/* 222:350 */     runClassifier(new QDA(), argv);
/* 223:    */   }
/* 224:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.QDA
 * JD-Core Version:    0.7.0.1
 */