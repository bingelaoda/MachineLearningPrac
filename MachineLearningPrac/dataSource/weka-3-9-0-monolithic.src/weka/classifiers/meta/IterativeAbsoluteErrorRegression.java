/*   1:    */ package weka.classifiers.meta;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Random;
/*   5:    */ import weka.classifiers.AbstractClassifier;
/*   6:    */ import weka.classifiers.Classifier;
/*   7:    */ import weka.classifiers.RandomizableSingleClassifierEnhancer;
/*   8:    */ import weka.classifiers.trees.DecisionStump;
/*   9:    */ import weka.core.BatchPredictor;
/*  10:    */ import weka.core.Capabilities;
/*  11:    */ import weka.core.Capabilities.Capability;
/*  12:    */ import weka.core.Instance;
/*  13:    */ import weka.core.Instances;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.TechnicalInformation;
/*  16:    */ import weka.core.TechnicalInformation.Field;
/*  17:    */ import weka.core.TechnicalInformation.Type;
/*  18:    */ import weka.core.TechnicalInformationHandler;
/*  19:    */ import weka.core.Utils;
/*  20:    */ import weka.core.WeightedInstancesHandler;
/*  21:    */ 
/*  22:    */ public class IterativeAbsoluteErrorRegression
/*  23:    */   extends RandomizableSingleClassifierEnhancer
/*  24:    */   implements TechnicalInformationHandler
/*  25:    */ {
/*  26:    */   static final long serialVersionUID = -2368837579670527151L;
/*  27:113 */   protected int m_NumIterations = -1;
/*  28:    */   
/*  29:    */   public String globalInfo()
/*  30:    */   {
/*  31:121 */     return "Iteratively fits a regression model by attempting to minimize absolute error, usinga base learner that minimizes weighted squared error.\n\nWeights are bounded from below by 1.0 / Utils.SMALL.\n\nResamples data based on weights if base learner is not a WeightedInstancesHandler.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*  32:    */   }
/*  33:    */   
/*  34:    */   public TechnicalInformation getTechnicalInformation()
/*  35:    */   {
/*  36:139 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  37:140 */     result.setValue(TechnicalInformation.Field.AUTHOR, "E. J. Schlossmacher");
/*  38:141 */     result.setValue(TechnicalInformation.Field.YEAR, "1973");
/*  39:142 */     result.setValue(TechnicalInformation.Field.TITLE, "An Iterative Technique for Absolute Deviations Curve Fitting");
/*  40:143 */     result.setValue(TechnicalInformation.Field.JOURNAL, "Journal of the American Statistical Association");
/*  41:144 */     result.setValue(TechnicalInformation.Field.VOLUME, "68");
/*  42:145 */     result.setValue(TechnicalInformation.Field.NUMBER, "344");
/*  43:    */     
/*  44:147 */     return result;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public IterativeAbsoluteErrorRegression()
/*  48:    */   {
/*  49:155 */     this(new DecisionStump());
/*  50:    */   }
/*  51:    */   
/*  52:    */   public IterativeAbsoluteErrorRegression(Classifier classifier)
/*  53:    */   {
/*  54:165 */     this.m_Classifier = classifier;
/*  55:    */   }
/*  56:    */   
/*  57:    */   protected String defaultClassifierString()
/*  58:    */   {
/*  59:175 */     return "weka.classifiers.trees.DecisionStump";
/*  60:    */   }
/*  61:    */   
/*  62:    */   public Capabilities getCapabilities()
/*  63:    */   {
/*  64:184 */     Capabilities result = super.getCapabilities();
/*  65:    */     
/*  66:    */ 
/*  67:187 */     result.disableAllClasses();
/*  68:188 */     result.disableAllClassDependencies();
/*  69:189 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*  70:190 */     result.enable(Capabilities.Capability.DATE_CLASS);
/*  71:    */     
/*  72:192 */     return result;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public void buildClassifier(Instances data)
/*  76:    */     throws Exception
/*  77:    */   {
/*  78:201 */     getCapabilities().testWithFail(data);
/*  79:    */     
/*  80:    */ 
/*  81:204 */     data = new Instances(data);
/*  82:205 */     data.deleteWithMissingClass();
/*  83:208 */     for (Instance inst : data) {
/*  84:209 */       inst.setWeight(1.0D);
/*  85:    */     }
/*  86:213 */     Classifier classifier = AbstractClassifier.makeCopy(getClassifier());
/*  87:214 */     classifier.buildClassifier(data);
/*  88:215 */     double[] residuals = new double[data.numInstances()];
/*  89:216 */     double oldError = 0.0D;
/*  90:217 */     if (((classifier instanceof BatchPredictor)) && (((BatchPredictor)classifier).implementsMoreEfficientBatchPrediction()))
/*  91:    */     {
/*  92:219 */       double[][] p = ((BatchPredictor)classifier).distributionsForInstances(data);
/*  93:220 */       for (int i = 0; i < residuals.length; i++)
/*  94:    */       {
/*  95:221 */         residuals[i] = Math.abs(p[i][0] - data.instance(i).classValue());
/*  96:222 */         oldError += residuals[i];
/*  97:    */       }
/*  98:    */     }
/*  99:    */     else
/* 100:    */     {
/* 101:225 */       for (int i = 0; i < residuals.length; i++)
/* 102:    */       {
/* 103:226 */         residuals[i] = Math.abs(classifier.classifyInstance(data.instance(i)) - data.instance(i).classValue());
/* 104:227 */         oldError += residuals[i];
/* 105:    */       }
/* 106:    */     }
/* 107:232 */     double mean = data.meanOrMode(data.classIndex());
/* 108:233 */     double AEforMean = 0.0D;
/* 109:234 */     for (int i = 0; i < data.numInstances(); i++) {
/* 110:235 */       AEforMean += Math.abs(mean - data.instance(i).classValue());
/* 111:    */     }
/* 112:239 */     oldError /= AEforMean;
/* 113:241 */     if (getDebug()) {
/* 114:242 */       System.err.println("Initial relative absolute error: " + oldError);
/* 115:    */     }
/* 116:246 */     Random random = data.getRandomNumberGenerator(getSeed());
/* 117:    */     
/* 118:    */ 
/* 119:249 */     double newError = 0.0D;
/* 120:250 */     Classifier savedClassifier = classifier;
/* 121:251 */     this.m_NumIterations = 1;
/* 122:    */     for (;;)
/* 123:    */     {
/* 124:255 */       double sumOfWeights = 0.0D;
/* 125:256 */       for (int i = 0; i < residuals.length; i++)
/* 126:    */       {
/* 127:257 */         double weight = Math.abs(residuals[i]) > Utils.SMALL ? 1.0D / Math.abs(residuals[i]) : 1.0D / Utils.SMALL;
/* 128:258 */         data.instance(i).setWeight(weight);
/* 129:259 */         sumOfWeights += weight;
/* 130:    */       }
/* 131:263 */       for (int i = 0; i < residuals.length; i++) {
/* 132:264 */         data.instance(i).setWeight(data.numInstances() * data.instance(i).weight() / sumOfWeights);
/* 133:    */       }
/* 134:268 */       classifier = AbstractClassifier.makeCopy(getClassifier());
/* 135:269 */       if (!(classifier instanceof WeightedInstancesHandler)) {
/* 136:270 */         classifier.buildClassifier(data.resampleWithWeights(random));
/* 137:    */       } else {
/* 138:272 */         classifier.buildClassifier(data);
/* 139:    */       }
/* 140:276 */       newError = 0.0D;
/* 141:277 */       if (((classifier instanceof BatchPredictor)) && (((BatchPredictor)classifier).implementsMoreEfficientBatchPrediction()))
/* 142:    */       {
/* 143:279 */         double[][] p = ((BatchPredictor)classifier).distributionsForInstances(data);
/* 144:280 */         for (int i = 0; i < residuals.length; i++)
/* 145:    */         {
/* 146:281 */           residuals[i] = Math.abs(p[i][0] - data.instance(i).classValue());
/* 147:282 */           newError += residuals[i];
/* 148:    */         }
/* 149:    */       }
/* 150:    */       else
/* 151:    */       {
/* 152:285 */         for (int i = 0; i < residuals.length; i++)
/* 153:    */         {
/* 154:286 */           residuals[i] = Math.abs(classifier.classifyInstance(data.instance(i)) - data.instance(i).classValue());
/* 155:287 */           newError += residuals[i];
/* 156:    */         }
/* 157:    */       }
/* 158:293 */       newError /= AEforMean;
/* 159:295 */       if (getDebug()) {
/* 160:296 */         System.err.println("Relative absolute error in iteration " + this.m_NumIterations + ": " + newError);
/* 161:    */       }
/* 162:300 */       if (oldError <= newError + Utils.SMALL) {
/* 163:    */         break;
/* 164:    */       }
/* 165:305 */       oldError = newError;
/* 166:306 */       savedClassifier = classifier;
/* 167:307 */       this.m_NumIterations += 1;
/* 168:    */     }
/* 169:309 */     this.m_Classifier = savedClassifier;
/* 170:    */   }
/* 171:    */   
/* 172:    */   public double classifyInstance(Instance inst)
/* 173:    */     throws Exception
/* 174:    */   {
/* 175:321 */     return getClassifier().classifyInstance(inst);
/* 176:    */   }
/* 177:    */   
/* 178:    */   public boolean implementsMoreEfficientBatchPrediction()
/* 179:    */   {
/* 180:332 */     if (!(getClassifier() instanceof BatchPredictor)) {
/* 181:333 */       return super.implementsMoreEfficientBatchPrediction();
/* 182:    */     }
/* 183:336 */     return ((BatchPredictor)getClassifier()).implementsMoreEfficientBatchPrediction();
/* 184:    */   }
/* 185:    */   
/* 186:    */   public double[][] distributionsForInstances(Instances insts)
/* 187:    */     throws Exception
/* 188:    */   {
/* 189:352 */     if ((getClassifier() instanceof BatchPredictor)) {
/* 190:353 */       return ((BatchPredictor)getClassifier()).distributionsForInstances(insts);
/* 191:    */     }
/* 192:355 */     return super.distributionsForInstances(insts);
/* 193:    */   }
/* 194:    */   
/* 195:    */   public String toString()
/* 196:    */   {
/* 197:365 */     if (this.m_NumIterations == -1) {
/* 198:366 */       return "Classifier hasn't been built yet!";
/* 199:    */     }
/* 200:368 */     StringBuffer text = new StringBuffer();
/* 201:369 */     text.append("Iterative Absolute Error Regression (" + this.m_NumIterations + ")\n\n");
/* 202:370 */     text.append("Final model\n\n" + getClassifier());
/* 203:371 */     return text.toString();
/* 204:    */   }
/* 205:    */   
/* 206:    */   public String getRevision()
/* 207:    */   {
/* 208:380 */     return RevisionUtils.extract("$Revision: 11192 $");
/* 209:    */   }
/* 210:    */   
/* 211:    */   public static void main(String[] argv)
/* 212:    */   {
/* 213:390 */     runClassifier(new IterativeAbsoluteErrorRegression(), argv);
/* 214:    */   }
/* 215:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.IterativeAbsoluteErrorRegression
 * JD-Core Version:    0.7.0.1
 */