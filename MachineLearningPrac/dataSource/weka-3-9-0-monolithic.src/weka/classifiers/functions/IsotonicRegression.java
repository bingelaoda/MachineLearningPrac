/*   1:    */ package weka.classifiers.functions;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import weka.classifiers.AbstractClassifier;
/*   6:    */ import weka.classifiers.Classifier;
/*   7:    */ import weka.classifiers.Evaluation;
/*   8:    */ import weka.classifiers.rules.ZeroR;
/*   9:    */ import weka.core.Attribute;
/*  10:    */ import weka.core.Capabilities;
/*  11:    */ import weka.core.Capabilities.Capability;
/*  12:    */ import weka.core.Instance;
/*  13:    */ import weka.core.Instances;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.Utils;
/*  16:    */ import weka.core.WeightedInstancesHandler;
/*  17:    */ 
/*  18:    */ public class IsotonicRegression
/*  19:    */   extends AbstractClassifier
/*  20:    */   implements WeightedInstancesHandler
/*  21:    */ {
/*  22:    */   static final long serialVersionUID = 1679336022835454137L;
/*  23:    */   private Attribute m_attribute;
/*  24:    */   private double[] m_cuts;
/*  25:    */   private double[] m_values;
/*  26:    */   private double m_minMsq;
/*  27:    */   private Classifier m_ZeroR;
/*  28:    */   
/*  29:    */   public String globalInfo()
/*  30:    */   {
/*  31: 82 */     return "Learns an isotonic regression model. Picks the attribute that results in the lowest squared error. Missing values are not allowed. Can only deal with numeric attributes.Considers the monotonically increasing case as well as the monotonicallydecreasing case";
/*  32:    */   }
/*  33:    */   
/*  34:    */   public double classifyInstance(Instance inst)
/*  35:    */     throws Exception
/*  36:    */   {
/*  37: 99 */     if (this.m_ZeroR != null) {
/*  38:100 */       return this.m_ZeroR.classifyInstance(inst);
/*  39:    */     }
/*  40:103 */     if (inst.isMissing(this.m_attribute.index())) {
/*  41:104 */       throw new Exception("IsotonicRegression: No missing values!");
/*  42:    */     }
/*  43:106 */     int index = Arrays.binarySearch(this.m_cuts, inst.value(this.m_attribute));
/*  44:107 */     if (index < 0) {
/*  45:108 */       return this.m_values[(-index - 1)];
/*  46:    */     }
/*  47:110 */     return this.m_values[(index + 1)];
/*  48:    */   }
/*  49:    */   
/*  50:    */   public Capabilities getCapabilities()
/*  51:    */   {
/*  52:120 */     Capabilities result = super.getCapabilities();
/*  53:121 */     result.disableAll();
/*  54:    */     
/*  55:    */ 
/*  56:124 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  57:125 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  58:    */     
/*  59:    */ 
/*  60:128 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*  61:129 */     result.enable(Capabilities.Capability.DATE_CLASS);
/*  62:130 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  63:    */     
/*  64:132 */     return result;
/*  65:    */   }
/*  66:    */   
/*  67:    */   protected void regress(Attribute attribute, Instances insts, boolean ascending)
/*  68:    */     throws Exception
/*  69:    */   {
/*  70:142 */     insts.sort(attribute);
/*  71:    */     
/*  72:    */ 
/*  73:145 */     double[] values = new double[insts.numInstances()];
/*  74:146 */     double[] weights = new double[insts.numInstances()];
/*  75:147 */     double[] cuts = new double[insts.numInstances() - 1];
/*  76:148 */     int size = 0;
/*  77:149 */     values[0] = insts.instance(0).classValue();
/*  78:150 */     weights[0] = insts.instance(0).weight();
/*  79:151 */     for (int i = 1; i < insts.numInstances(); i++)
/*  80:    */     {
/*  81:152 */       if (insts.instance(i).value(attribute) > insts.instance(i - 1).value(attribute))
/*  82:    */       {
/*  83:154 */         cuts[size] = ((insts.instance(i).value(attribute) + insts.instance(i - 1).value(attribute)) / 2.0D);
/*  84:    */         
/*  85:156 */         size++;
/*  86:    */       }
/*  87:158 */       values[size] += insts.instance(i).classValue();
/*  88:159 */       weights[size] += insts.instance(i).weight();
/*  89:    */     }
/*  90:161 */     size++;
/*  91:    */     boolean violators;
/*  92:    */     do
/*  93:    */     {
/*  94:166 */       violators = false;
/*  95:    */       
/*  96:    */ 
/*  97:169 */       double[] tempValues = new double[size];
/*  98:170 */       double[] tempWeights = new double[size];
/*  99:171 */       double[] tempCuts = new double[size - 1];
/* 100:    */       
/* 101:    */ 
/* 102:174 */       int newSize = 0;
/* 103:175 */       tempValues[0] = values[0];
/* 104:176 */       tempWeights[0] = weights[0];
/* 105:177 */       for (int j = 1; j < size; j++) {
/* 106:178 */         if (((ascending) && (values[j] / weights[j] > tempValues[newSize] / tempWeights[newSize])) || ((!ascending) && (values[j] / weights[j] < tempValues[newSize] / tempWeights[newSize])))
/* 107:    */         {
/* 108:182 */           tempCuts[newSize] = cuts[(j - 1)];
/* 109:183 */           newSize++;
/* 110:184 */           tempValues[newSize] = values[j];
/* 111:185 */           tempWeights[newSize] = weights[j];
/* 112:    */         }
/* 113:    */         else
/* 114:    */         {
/* 115:187 */           tempWeights[newSize] += weights[j];
/* 116:188 */           tempValues[newSize] += values[j];
/* 117:189 */           violators = true;
/* 118:    */         }
/* 119:    */       }
/* 120:192 */       newSize++;
/* 121:    */       
/* 122:    */ 
/* 123:195 */       values = tempValues;
/* 124:196 */       weights = tempWeights;
/* 125:197 */       cuts = tempCuts;
/* 126:198 */       size = newSize;
/* 127:199 */     } while (violators);
/* 128:202 */     for (int i = 0; i < size; i++) {
/* 129:203 */       values[i] /= weights[i];
/* 130:    */     }
/* 131:207 */     Attribute attributeBackedup = this.m_attribute;
/* 132:208 */     double[] cutsBackedup = this.m_cuts;
/* 133:209 */     double[] valuesBackedup = this.m_values;
/* 134:    */     
/* 135:    */ 
/* 136:212 */     this.m_attribute = attribute;
/* 137:213 */     this.m_cuts = cuts;
/* 138:214 */     this.m_values = values;
/* 139:    */     
/* 140:    */ 
/* 141:217 */     Evaluation eval = new Evaluation(insts);
/* 142:218 */     eval.evaluateModel(this, insts, new Object[0]);
/* 143:219 */     double msq = eval.rootMeanSquaredError();
/* 144:222 */     if (msq < this.m_minMsq)
/* 145:    */     {
/* 146:223 */       this.m_minMsq = msq;
/* 147:    */     }
/* 148:    */     else
/* 149:    */     {
/* 150:225 */       this.m_attribute = attributeBackedup;
/* 151:226 */       this.m_cuts = cutsBackedup;
/* 152:227 */       this.m_values = valuesBackedup;
/* 153:    */     }
/* 154:    */   }
/* 155:    */   
/* 156:    */   public void buildClassifier(Instances insts)
/* 157:    */     throws Exception
/* 158:    */   {
/* 159:240 */     getCapabilities().testWithFail(insts);
/* 160:    */     
/* 161:    */ 
/* 162:243 */     insts = new Instances(insts);
/* 163:244 */     insts.deleteWithMissingClass();
/* 164:247 */     if (insts.numAttributes() == 1)
/* 165:    */     {
/* 166:248 */       System.err.println("Cannot build model (only class attribute present in data!), using ZeroR model instead!");
/* 167:    */       
/* 168:    */ 
/* 169:251 */       this.m_ZeroR = new ZeroR();
/* 170:252 */       this.m_ZeroR.buildClassifier(insts);
/* 171:253 */       return;
/* 172:    */     }
/* 173:256 */     this.m_ZeroR = null;
/* 174:    */     
/* 175:    */ 
/* 176:    */ 
/* 177:260 */     this.m_minMsq = 1.7976931348623157E+308D;
/* 178:261 */     this.m_attribute = null;
/* 179:262 */     for (int a = 0; a < insts.numAttributes(); a++) {
/* 180:263 */       if (a != insts.classIndex())
/* 181:    */       {
/* 182:264 */         regress(insts.attribute(a), insts, true);
/* 183:265 */         regress(insts.attribute(a), insts, false);
/* 184:    */       }
/* 185:    */     }
/* 186:    */   }
/* 187:    */   
/* 188:    */   public String toString()
/* 189:    */   {
/* 190:278 */     if (this.m_ZeroR != null)
/* 191:    */     {
/* 192:279 */       StringBuffer buf = new StringBuffer();
/* 193:280 */       buf.append(getClass().getName().replaceAll(".*\\.", "") + "\n");
/* 194:281 */       buf.append(getClass().getName().replaceAll(".*\\.", "").replaceAll(".", "=") + "\n\n");
/* 195:282 */       buf.append("Warning: No model could be built, hence ZeroR model is used:\n\n");
/* 196:283 */       buf.append(this.m_ZeroR.toString());
/* 197:284 */       return buf.toString();
/* 198:    */     }
/* 199:287 */     StringBuffer text = new StringBuffer();
/* 200:288 */     text.append("Isotonic regression\n\n");
/* 201:289 */     if (this.m_attribute == null)
/* 202:    */     {
/* 203:290 */       text.append("No model built yet!");
/* 204:    */     }
/* 205:    */     else
/* 206:    */     {
/* 207:293 */       text.append("Based on attribute: " + this.m_attribute.name() + "\n\n");
/* 208:294 */       for (int i = 0; i < this.m_values.length; i++)
/* 209:    */       {
/* 210:295 */         text.append("prediction: " + Utils.doubleToString(this.m_values[i], 10, 2));
/* 211:296 */         if (i < this.m_cuts.length) {
/* 212:297 */           text.append("\t\tcut point: " + Utils.doubleToString(this.m_cuts[i], 10, 2) + "\n");
/* 213:    */         }
/* 214:    */       }
/* 215:    */     }
/* 216:301 */     return text.toString();
/* 217:    */   }
/* 218:    */   
/* 219:    */   public String getRevision()
/* 220:    */   {
/* 221:310 */     return RevisionUtils.extract("$Revision: 8109 $");
/* 222:    */   }
/* 223:    */   
/* 224:    */   public static void main(String[] argv)
/* 225:    */   {
/* 226:319 */     runClassifier(new IsotonicRegression(), argv);
/* 227:    */   }
/* 228:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.IsotonicRegression
 * JD-Core Version:    0.7.0.1
 */