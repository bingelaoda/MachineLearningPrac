/*   1:    */ package weka.classifiers.functions;
/*   2:    */ 
/*   3:    */ import java.util.Arrays;
/*   4:    */ import java.util.Random;
/*   5:    */ import weka.core.Capabilities;
/*   6:    */ import weka.core.Capabilities.Capability;
/*   7:    */ import weka.core.Instance;
/*   8:    */ import weka.core.WeightedInstancesHandler;
/*   9:    */ 
/*  10:    */ public class RBFRegressor
/*  11:    */   extends RBFModel
/*  12:    */   implements WeightedInstancesHandler
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = -7847474276438394611L;
/*  15:    */   
/*  16:    */   public Capabilities getCapabilities()
/*  17:    */   {
/*  18:119 */     Capabilities result = super.getCapabilities();
/*  19:    */     
/*  20:    */ 
/*  21:122 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*  22:123 */     result.enable(Capabilities.Capability.DATE_CLASS);
/*  23:124 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  24:    */     
/*  25:126 */     return result;
/*  26:    */   }
/*  27:    */   
/*  28:    */   protected void initializeOutputLayer(Random random)
/*  29:    */   {
/*  30:135 */     for (int i = 0; i < this.m_numUnits + 1; i++) {
/*  31:136 */       this.m_RBFParameters[(this.OFFSET_WEIGHTS + i)] = ((random.nextDouble() - 0.5D) / 2.0D);
/*  32:    */     }
/*  33:    */   }
/*  34:    */   
/*  35:    */   protected double calculateError(double[] outputs, Instance inst)
/*  36:    */   {
/*  37:146 */     double err = getOutput(outputs) - inst.classValue();
/*  38:    */     
/*  39:    */ 
/*  40:149 */     return inst.weight() * err * err;
/*  41:    */   }
/*  42:    */   
/*  43:    */   protected double postprocessError(double error)
/*  44:    */   {
/*  45:159 */     double squaredSumOfWeights = 0.0D;
/*  46:160 */     for (int k = 0; k < this.m_numUnits; k++) {
/*  47:161 */       squaredSumOfWeights += this.m_RBFParameters[(this.OFFSET_WEIGHTS + k)] * this.m_RBFParameters[(this.OFFSET_WEIGHTS + k)];
/*  48:    */     }
/*  49:165 */     return error + this.m_ridge * squaredSumOfWeights;
/*  50:    */   }
/*  51:    */   
/*  52:    */   protected void postprocessGradient(double[] grad)
/*  53:    */   {
/*  54:175 */     for (int k = 0; k < this.m_numUnits; k++) {
/*  55:176 */       grad[(this.OFFSET_WEIGHTS + k)] += this.m_ridge * 2.0D * this.m_RBFParameters[(this.OFFSET_WEIGHTS + k)];
/*  56:    */     }
/*  57:    */   }
/*  58:    */   
/*  59:    */   protected void updateGradient(double[] grad, Instance inst, double[] outputs, double[] derivativesOutputs, double[] deltaHidden)
/*  60:    */   {
/*  61:189 */     Arrays.fill(deltaHidden, 0.0D);
/*  62:    */     
/*  63:    */ 
/*  64:192 */     double deltaOut = inst.weight() * (getOutput(outputs) - inst.classValue());
/*  65:195 */     if ((deltaOut <= this.m_tolerance) && (deltaOut >= -this.m_tolerance)) {
/*  66:196 */       return;
/*  67:    */     }
/*  68:200 */     int offsetOW = this.OFFSET_WEIGHTS;
/*  69:203 */     for (int i = 0; i < this.m_numUnits; i++) {
/*  70:204 */       deltaHidden[i] += deltaOut * this.m_RBFParameters[(offsetOW + i)];
/*  71:    */     }
/*  72:208 */     for (int i = 0; i < this.m_numUnits; i++) {
/*  73:209 */       grad[(offsetOW + i)] += deltaOut * outputs[i];
/*  74:    */     }
/*  75:213 */     grad[(offsetOW + this.m_numUnits)] += deltaOut;
/*  76:    */   }
/*  77:    */   
/*  78:    */   protected double getOutput(double[] outputs)
/*  79:    */   {
/*  80:222 */     double result = 0.0D;
/*  81:223 */     for (int i = 0; i < this.m_numUnits; i++) {
/*  82:224 */       result += this.m_RBFParameters[(this.OFFSET_WEIGHTS + i)] * outputs[i];
/*  83:    */     }
/*  84:226 */     result += this.m_RBFParameters[(this.OFFSET_WEIGHTS + this.m_numUnits)];
/*  85:227 */     return result;
/*  86:    */   }
/*  87:    */   
/*  88:    */   protected double[] getDistribution(double[] outputs)
/*  89:    */   {
/*  90:236 */     double[] dist = new double[1];
/*  91:237 */     dist[0] = (getOutput(outputs) * this.m_x1 + this.m_x0);
/*  92:238 */     return dist;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public String toString()
/*  96:    */   {
/*  97:247 */     if (this.m_RBFParameters == null) {
/*  98:248 */       return "Classifier not built yet.";
/*  99:    */     }
/* 100:251 */     String s = "";
/* 101:253 */     for (int i = 0; i < this.m_numUnits; i++)
/* 102:    */     {
/* 103:254 */       s = s + "\n\nOutput weight: " + this.m_RBFParameters[(this.OFFSET_WEIGHTS + i)];
/* 104:255 */       s = s + "\n\nUnit center:\n";
/* 105:256 */       for (int j = 0; j < this.m_numAttributes; j++) {
/* 106:257 */         if (j != this.m_classIndex) {
/* 107:258 */           s = s + this.m_RBFParameters[(this.OFFSET_CENTERS + i * this.m_numAttributes + j)] + "\t";
/* 108:    */         }
/* 109:    */       }
/* 110:262 */       if (this.m_scaleOptimizationOption == 3)
/* 111:    */       {
/* 112:263 */         s = s + "\n\nUnit scales:\n";
/* 113:264 */         for (int j = 0; j < this.m_numAttributes; j++) {
/* 114:265 */           if (j != this.m_classIndex) {
/* 115:266 */             s = s + this.m_RBFParameters[(this.OFFSET_SCALES + i * this.m_numAttributes + j)] + "\t";
/* 116:    */           }
/* 117:    */         }
/* 118:    */       }
/* 119:270 */       else if (this.m_scaleOptimizationOption == 2)
/* 120:    */       {
/* 121:271 */         s = s + "\n\nUnit scale:\n";
/* 122:272 */         s = s + this.m_RBFParameters[(this.OFFSET_SCALES + i)] + "\t";
/* 123:    */       }
/* 124:    */     }
/* 125:275 */     if (this.m_scaleOptimizationOption == 1)
/* 126:    */     {
/* 127:276 */       s = s + "\n\nScale:\n";
/* 128:277 */       s = s + this.m_RBFParameters[this.OFFSET_SCALES] + "\t";
/* 129:    */     }
/* 130:279 */     if (this.m_useAttributeWeights)
/* 131:    */     {
/* 132:280 */       s = s + "\n\nAttribute weights:\n";
/* 133:281 */       for (int j = 0; j < this.m_numAttributes; j++) {
/* 134:282 */         if (j != this.m_classIndex) {
/* 135:283 */           s = s + this.m_RBFParameters[(this.OFFSET_ATTRIBUTE_WEIGHTS + j)] + "\t";
/* 136:    */         }
/* 137:    */       }
/* 138:    */     }
/* 139:287 */     s = s + "\n\nBias weight: " + this.m_RBFParameters[(this.OFFSET_WEIGHTS + this.m_numUnits)];
/* 140:    */     
/* 141:289 */     return s;
/* 142:    */   }
/* 143:    */   
/* 144:    */   public static void main(String[] argv)
/* 145:    */   {
/* 146:298 */     runClassifier(new RBFRegressor(), argv);
/* 147:    */   }
/* 148:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.RBFRegressor
 * JD-Core Version:    0.7.0.1
 */