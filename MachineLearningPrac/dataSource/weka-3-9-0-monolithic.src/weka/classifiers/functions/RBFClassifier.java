/*   1:    */ package weka.classifiers.functions;
/*   2:    */ 
/*   3:    */ import java.util.Arrays;
/*   4:    */ import java.util.Random;
/*   5:    */ import weka.core.Capabilities;
/*   6:    */ import weka.core.Capabilities.Capability;
/*   7:    */ import weka.core.Instance;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.Utils;
/*  10:    */ import weka.core.WeightedInstancesHandler;
/*  11:    */ 
/*  12:    */ public class RBFClassifier
/*  13:    */   extends RBFModel
/*  14:    */   implements WeightedInstancesHandler
/*  15:    */ {
/*  16:    */   private static final long serialVersionUID = -7847475556438394611L;
/*  17:    */   
/*  18:    */   public Capabilities getCapabilities()
/*  19:    */   {
/*  20:120 */     Capabilities result = super.getCapabilities();
/*  21:    */     
/*  22:    */ 
/*  23:123 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  24:124 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  25:    */     
/*  26:126 */     return result;
/*  27:    */   }
/*  28:    */   
/*  29:    */   protected void initializeOutputLayer(Random random)
/*  30:    */   {
/*  31:135 */     for (int i = 0; i < this.m_numUnits + 1; i++) {
/*  32:136 */       for (int j = 0; j < this.m_numClasses; j++) {
/*  33:137 */         this.m_RBFParameters[(this.OFFSET_WEIGHTS + j * (this.m_numUnits + 1) + i)] = (0.1D * random.nextGaussian());
/*  34:    */       }
/*  35:    */     }
/*  36:    */   }
/*  37:    */   
/*  38:    */   protected double calculateError(double[] outputs, Instance inst)
/*  39:    */   {
/*  40:150 */     double SE = 0.0D;
/*  41:153 */     for (int i = 0; i < this.m_numClasses; i++)
/*  42:    */     {
/*  43:157 */       double target = (int)inst.value(this.m_classIndex) == i ? 0.99D : 0.01D;
/*  44:    */       
/*  45:    */ 
/*  46:160 */       double err = getOutput(i, outputs, null) - target;
/*  47:161 */       SE += inst.weight() * err * err;
/*  48:    */     }
/*  49:163 */     return SE;
/*  50:    */   }
/*  51:    */   
/*  52:    */   protected double postprocessError(double error)
/*  53:    */   {
/*  54:174 */     double squaredSumOfWeights = 0.0D;
/*  55:175 */     for (int k = 0; k < this.m_numUnits; k++) {
/*  56:176 */       for (int i = 0; i < this.m_numClasses; i++) {
/*  57:177 */         squaredSumOfWeights += this.m_RBFParameters[(this.OFFSET_WEIGHTS + i * (this.m_numUnits + 1) + k)] * this.m_RBFParameters[(this.OFFSET_WEIGHTS + i * (this.m_numUnits + 1) + k)];
/*  58:    */       }
/*  59:    */     }
/*  60:183 */     return (error + this.m_ridge * squaredSumOfWeights) / this.m_data.sumOfWeights();
/*  61:    */   }
/*  62:    */   
/*  63:    */   protected void postprocessGradient(double[] grad)
/*  64:    */   {
/*  65:193 */     for (int k = 0; k < this.m_numUnits; k++) {
/*  66:194 */       for (int i = 0; i < this.m_numClasses; i++) {
/*  67:195 */         grad[(this.OFFSET_WEIGHTS + i * (this.m_numUnits + 1) + k)] += this.m_ridge * 2.0D * this.m_RBFParameters[(this.OFFSET_WEIGHTS + i * (this.m_numUnits + 1) + k)];
/*  68:    */       }
/*  69:    */     }
/*  70:200 */     double factor = 1.0D / this.m_data.sumOfWeights();
/*  71:201 */     for (int i = 0; i < grad.length; i++) {
/*  72:202 */       grad[i] *= factor;
/*  73:    */     }
/*  74:    */   }
/*  75:    */   
/*  76:    */   protected void updateGradient(double[] grad, Instance inst, double[] outputs, double[] sigmoidDerivativeOutput, double[] deltaHidden)
/*  77:    */   {
/*  78:214 */     Arrays.fill(deltaHidden, 0.0D);
/*  79:217 */     for (int j = 0; j < this.m_numClasses; j++)
/*  80:    */     {
/*  81:220 */       double pred = getOutput(j, outputs, sigmoidDerivativeOutput);
/*  82:    */       
/*  83:    */ 
/*  84:    */ 
/*  85:224 */       double target = (int)inst.value(this.m_classIndex) == j ? 0.99D : 0.01D;
/*  86:    */       
/*  87:    */ 
/*  88:227 */       double deltaOut = inst.weight() * (pred - target) * sigmoidDerivativeOutput[0];
/*  89:230 */       if ((deltaOut > this.m_tolerance) || (deltaOut < -this.m_tolerance))
/*  90:    */       {
/*  91:235 */         int offsetOW = this.OFFSET_WEIGHTS + j * (this.m_numUnits + 1);
/*  92:238 */         for (int i = 0; i < this.m_numUnits; i++) {
/*  93:239 */           deltaHidden[i] += deltaOut * this.m_RBFParameters[(offsetOW + i)];
/*  94:    */         }
/*  95:243 */         for (int i = 0; i < this.m_numUnits; i++) {
/*  96:244 */           grad[(offsetOW + i)] += deltaOut * outputs[i];
/*  97:    */         }
/*  98:248 */         grad[(offsetOW + this.m_numUnits)] += deltaOut;
/*  99:    */       }
/* 100:    */     }
/* 101:    */   }
/* 102:    */   
/* 103:    */   protected double getOutput(int unit, double[] outputs, double[] d)
/* 104:    */   {
/* 105:258 */     double result = 0.0D;
/* 106:259 */     for (int i = 0; i < this.m_numUnits; i++) {
/* 107:260 */       result += this.m_RBFParameters[(this.OFFSET_WEIGHTS + unit * (this.m_numUnits + 1) + i)] * outputs[i];
/* 108:    */     }
/* 109:263 */     result += this.m_RBFParameters[(this.OFFSET_WEIGHTS + unit * (this.m_numUnits + 1) + this.m_numUnits)];
/* 110:    */     
/* 111:265 */     return sigmoid(-result, d, 0);
/* 112:    */   }
/* 113:    */   
/* 114:    */   protected double approxExp(double x, double[] d, int index)
/* 115:    */   {
/* 116:274 */     double y = 1.0D + x / 4096.0D;
/* 117:275 */     x = y * y;
/* 118:276 */     x *= x;
/* 119:277 */     x *= x;
/* 120:278 */     x *= x;
/* 121:279 */     x *= x;
/* 122:280 */     x *= x;
/* 123:281 */     x *= x;
/* 124:282 */     x *= x;
/* 125:283 */     x *= x;
/* 126:284 */     x *= x;
/* 127:285 */     x *= x;
/* 128:286 */     x *= x;
/* 129:289 */     if (d != null) {
/* 130:290 */       d[index] = (x / y);
/* 131:    */     }
/* 132:293 */     return x;
/* 133:    */   }
/* 134:    */   
/* 135:    */   protected double sigmoid(double x, double[] d, int index)
/* 136:    */   {
/* 137:303 */     double y = 1.0D + x / 4096.0D;
/* 138:304 */     x = y * y;
/* 139:305 */     x *= x;
/* 140:306 */     x *= x;
/* 141:307 */     x *= x;
/* 142:308 */     x *= x;
/* 143:309 */     x *= x;
/* 144:310 */     x *= x;
/* 145:311 */     x *= x;
/* 146:312 */     x *= x;
/* 147:313 */     x *= x;
/* 148:314 */     x *= x;
/* 149:315 */     x *= x;
/* 150:316 */     double output = 1.0D / (1.0D + x);
/* 151:319 */     if (d != null) {
/* 152:320 */       d[index] = (output * (1.0D - output) / y);
/* 153:    */     }
/* 154:323 */     return output;
/* 155:    */   }
/* 156:    */   
/* 157:    */   protected double[] getDistribution(double[] outputs)
/* 158:    */   {
/* 159:332 */     double[] dist = new double[this.m_numClasses];
/* 160:333 */     for (int i = 0; i < this.m_numClasses; i++)
/* 161:    */     {
/* 162:334 */       dist[i] = getOutput(i, outputs, null);
/* 163:335 */       if (dist[i] < 0.0D) {
/* 164:336 */         dist[i] = 0.0D;
/* 165:337 */       } else if (dist[i] > 1.0D) {
/* 166:338 */         dist[i] = 1.0D;
/* 167:    */       }
/* 168:    */     }
/* 169:341 */     Utils.normalize(dist);
/* 170:    */     
/* 171:343 */     return dist;
/* 172:    */   }
/* 173:    */   
/* 174:    */   public String toString()
/* 175:    */   {
/* 176:352 */     if (this.m_RBFParameters == null) {
/* 177:353 */       return "Classifier not built yet.";
/* 178:    */     }
/* 179:356 */     String s = "";
/* 180:358 */     for (int i = 0; i < this.m_numUnits; i++)
/* 181:    */     {
/* 182:359 */       if (i > 0) {
/* 183:360 */         s = s + "\n\n";
/* 184:    */       }
/* 185:362 */       s = s + "Output weights for different classes:\n";
/* 186:363 */       for (int j = 0; j < this.m_numClasses; j++) {
/* 187:364 */         s = s + this.m_RBFParameters[(this.OFFSET_WEIGHTS + j * (this.m_numUnits + 1) + i)] + "\t";
/* 188:    */       }
/* 189:367 */       s = s + "\n\nUnit center:\n";
/* 190:368 */       for (int j = 0; j < this.m_numAttributes; j++) {
/* 191:369 */         if (j != this.m_classIndex) {
/* 192:370 */           s = s + this.m_RBFParameters[(this.OFFSET_CENTERS + i * this.m_numAttributes + j)] + "\t";
/* 193:    */         }
/* 194:    */       }
/* 195:374 */       if (this.m_scaleOptimizationOption == 3)
/* 196:    */       {
/* 197:375 */         s = s + "\n\nUnit scales:\n";
/* 198:376 */         for (int j = 0; j < this.m_numAttributes; j++) {
/* 199:377 */           if (j != this.m_classIndex) {
/* 200:378 */             s = s + this.m_RBFParameters[(this.OFFSET_SCALES + i * this.m_numAttributes + j)] + "\t";
/* 201:    */           }
/* 202:    */         }
/* 203:    */       }
/* 204:382 */       else if (this.m_scaleOptimizationOption == 2)
/* 205:    */       {
/* 206:383 */         s = s + "\n\nUnit scale:\n";
/* 207:384 */         s = s + this.m_RBFParameters[(this.OFFSET_SCALES + i)] + "\t";
/* 208:    */       }
/* 209:    */     }
/* 210:387 */     if (this.m_scaleOptimizationOption == 1)
/* 211:    */     {
/* 212:388 */       s = s + "\n\nScale:\n";
/* 213:389 */       s = s + this.m_RBFParameters[this.OFFSET_SCALES] + "\t";
/* 214:    */     }
/* 215:391 */     if (this.m_useAttributeWeights)
/* 216:    */     {
/* 217:392 */       s = s + "\n\nAttribute weights:\n";
/* 218:393 */       for (int j = 0; j < this.m_numAttributes; j++) {
/* 219:394 */         if (j != this.m_classIndex) {
/* 220:395 */           s = s + this.m_RBFParameters[(this.OFFSET_ATTRIBUTE_WEIGHTS + j)] + "\t";
/* 221:    */         }
/* 222:    */       }
/* 223:    */     }
/* 224:400 */     s = s + "\n\nBias weights for different classes:\n";
/* 225:401 */     for (int j = 0; j < this.m_numClasses; j++) {
/* 226:402 */       s = s + this.m_RBFParameters[(this.OFFSET_WEIGHTS + j * (this.m_numUnits + 1) + this.m_numUnits)] + "\t";
/* 227:    */     }
/* 228:406 */     return s;
/* 229:    */   }
/* 230:    */   
/* 231:    */   public static void main(String[] argv)
/* 232:    */   {
/* 233:415 */     runClassifier(new RBFClassifier(), argv);
/* 234:    */   }
/* 235:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.RBFClassifier
 * JD-Core Version:    0.7.0.1
 */