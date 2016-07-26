/*   1:    */ package weka.classifiers.functions.neural;
/*   2:    */ 
/*   3:    */ import java.util.Random;
/*   4:    */ import weka.core.RevisionUtils;
/*   5:    */ 
/*   6:    */ public class NeuralNode
/*   7:    */   extends NeuralConnection
/*   8:    */ {
/*   9:    */   private static final long serialVersionUID = -1085750607680839163L;
/*  10:    */   private double[] m_weights;
/*  11:    */   private double[] m_bestWeights;
/*  12:    */   private double[] m_changeInWeights;
/*  13:    */   private Random m_random;
/*  14:    */   private NeuralMethod m_methods;
/*  15:    */   
/*  16:    */   public NeuralNode(String id, Random r, NeuralMethod m)
/*  17:    */   {
/*  18: 60 */     super(id);
/*  19: 61 */     this.m_weights = new double[1];
/*  20: 62 */     this.m_bestWeights = new double[1];
/*  21: 63 */     this.m_changeInWeights = new double[1];
/*  22:    */     
/*  23: 65 */     this.m_random = r;
/*  24:    */     
/*  25: 67 */     this.m_weights[0] = (this.m_random.nextDouble() * 0.1D - 0.05D);
/*  26: 68 */     this.m_changeInWeights[0] = 0.0D;
/*  27:    */     
/*  28: 70 */     this.m_methods = m;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public void setMethod(NeuralMethod m)
/*  32:    */   {
/*  33: 79 */     this.m_methods = m;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public NeuralMethod getMethod()
/*  37:    */   {
/*  38: 83 */     return this.m_methods;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public double outputValue(boolean calculate)
/*  42:    */   {
/*  43: 94 */     if ((Double.isNaN(this.m_unitValue)) && (calculate)) {
/*  44: 96 */       this.m_unitValue = this.m_methods.outputValue(this);
/*  45:    */     }
/*  46: 99 */     return this.m_unitValue;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public double errorValue(boolean calculate)
/*  50:    */   {
/*  51:111 */     if ((!Double.isNaN(this.m_unitValue)) && (Double.isNaN(this.m_unitError)) && (calculate)) {
/*  52:113 */       this.m_unitError = this.m_methods.errorValue(this);
/*  53:    */     }
/*  54:115 */     return this.m_unitError;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void reset()
/*  58:    */   {
/*  59:126 */     if ((!Double.isNaN(this.m_unitValue)) || (!Double.isNaN(this.m_unitError)))
/*  60:    */     {
/*  61:127 */       this.m_unitValue = (0.0D / 0.0D);
/*  62:128 */       this.m_unitError = (0.0D / 0.0D);
/*  63:129 */       this.m_weightsUpdated = false;
/*  64:130 */       for (int noa = 0; noa < this.m_numInputs; noa++) {
/*  65:131 */         this.m_inputList[noa].reset();
/*  66:    */       }
/*  67:    */     }
/*  68:    */   }
/*  69:    */   
/*  70:    */   public void saveWeights()
/*  71:    */   {
/*  72:142 */     System.arraycopy(this.m_weights, 0, this.m_bestWeights, 0, this.m_weights.length);
/*  73:145 */     for (int i = 0; i < this.m_numInputs; i++) {
/*  74:146 */       this.m_inputList[i].saveWeights();
/*  75:    */     }
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void restoreWeights()
/*  79:    */   {
/*  80:156 */     System.arraycopy(this.m_bestWeights, 0, this.m_weights, 0, this.m_weights.length);
/*  81:159 */     for (int i = 0; i < this.m_numInputs; i++) {
/*  82:160 */       this.m_inputList[i].restoreWeights();
/*  83:    */     }
/*  84:    */   }
/*  85:    */   
/*  86:    */   public double weightValue(int n)
/*  87:    */   {
/*  88:173 */     if ((n >= this.m_numInputs) || (n < -1)) {
/*  89:174 */       return (0.0D / 0.0D);
/*  90:    */     }
/*  91:176 */     return this.m_weights[(n + 1)];
/*  92:    */   }
/*  93:    */   
/*  94:    */   public double[] getWeights()
/*  95:    */   {
/*  96:185 */     return this.m_weights;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public double[] getChangeInWeights()
/* 100:    */   {
/* 101:194 */     return this.m_changeInWeights;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public void updateWeights(double l, double m)
/* 105:    */   {
/* 106:207 */     if ((!this.m_weightsUpdated) && (!Double.isNaN(this.m_unitError)))
/* 107:    */     {
/* 108:208 */       this.m_methods.updateWeights(this, l, m);
/* 109:    */       
/* 110:    */ 
/* 111:    */ 
/* 112:    */ 
/* 113:    */ 
/* 114:214 */       super.updateWeights(l, m);
/* 115:    */     }
/* 116:    */   }
/* 117:    */   
/* 118:    */   protected boolean connectInput(NeuralConnection i, int n)
/* 119:    */   {
/* 120:228 */     if (!super.connectInput(i, n)) {
/* 121:229 */       return false;
/* 122:    */     }
/* 123:234 */     this.m_weights[this.m_numInputs] = (this.m_random.nextDouble() * 0.1D - 0.05D);
/* 124:235 */     this.m_changeInWeights[this.m_numInputs] = 0.0D;
/* 125:    */     
/* 126:237 */     return true;
/* 127:    */   }
/* 128:    */   
/* 129:    */   protected void allocateInputs()
/* 130:    */   {
/* 131:246 */     NeuralConnection[] temp1 = new NeuralConnection[this.m_inputList.length + 15];
/* 132:247 */     int[] temp2 = new int[this.m_inputNums.length + 15];
/* 133:248 */     double[] temp4 = new double[this.m_weights.length + 15];
/* 134:249 */     double[] temp5 = new double[this.m_changeInWeights.length + 15];
/* 135:250 */     double[] temp6 = new double[this.m_bestWeights.length + 15];
/* 136:    */     
/* 137:252 */     temp4[0] = this.m_weights[0];
/* 138:253 */     temp5[0] = this.m_changeInWeights[0];
/* 139:254 */     temp6[0] = this.m_bestWeights[0];
/* 140:255 */     for (int noa = 0; noa < this.m_numInputs; noa++)
/* 141:    */     {
/* 142:256 */       temp1[noa] = this.m_inputList[noa];
/* 143:257 */       temp2[noa] = this.m_inputNums[noa];
/* 144:258 */       temp4[(noa + 1)] = this.m_weights[(noa + 1)];
/* 145:259 */       temp5[(noa + 1)] = this.m_changeInWeights[(noa + 1)];
/* 146:260 */       temp6[(noa + 1)] = this.m_bestWeights[(noa + 1)];
/* 147:    */     }
/* 148:263 */     this.m_inputList = temp1;
/* 149:264 */     this.m_inputNums = temp2;
/* 150:265 */     this.m_weights = temp4;
/* 151:266 */     this.m_changeInWeights = temp5;
/* 152:267 */     this.m_bestWeights = temp6;
/* 153:    */   }
/* 154:    */   
/* 155:    */   protected boolean disconnectInput(NeuralConnection i, int n)
/* 156:    */   {
/* 157:284 */     int loc = -1;
/* 158:285 */     boolean removed = false;
/* 159:    */     do
/* 160:    */     {
/* 161:287 */       loc = -1;
/* 162:288 */       for (int noa = 0; noa < this.m_numInputs; noa++) {
/* 163:289 */         if ((i == this.m_inputList[noa]) && ((n == -1) || (n == this.m_inputNums[noa])))
/* 164:    */         {
/* 165:290 */           loc = noa;
/* 166:291 */           break;
/* 167:    */         }
/* 168:    */       }
/* 169:295 */       if (loc >= 0)
/* 170:    */       {
/* 171:296 */         for (int noa = loc + 1; noa < this.m_numInputs; noa++)
/* 172:    */         {
/* 173:297 */           this.m_inputList[(noa - 1)] = this.m_inputList[noa];
/* 174:298 */           this.m_inputNums[(noa - 1)] = this.m_inputNums[noa];
/* 175:    */           
/* 176:300 */           this.m_weights[noa] = this.m_weights[(noa + 1)];
/* 177:301 */           this.m_changeInWeights[noa] = this.m_changeInWeights[(noa + 1)];
/* 178:    */           
/* 179:303 */           this.m_inputList[(noa - 1)].changeOutputNum(this.m_inputNums[(noa - 1)], noa - 1);
/* 180:    */         }
/* 181:305 */         this.m_numInputs -= 1;
/* 182:306 */         removed = true;
/* 183:    */       }
/* 184:308 */     } while ((n == -1) && (loc != -1));
/* 185:309 */     return removed;
/* 186:    */   }
/* 187:    */   
/* 188:    */   public void removeAllInputs()
/* 189:    */   {
/* 190:317 */     super.removeAllInputs();
/* 191:    */     
/* 192:319 */     double temp1 = this.m_weights[0];
/* 193:320 */     double temp2 = this.m_changeInWeights[0];
/* 194:    */     
/* 195:322 */     this.m_weights = new double[1];
/* 196:323 */     this.m_changeInWeights = new double[1];
/* 197:    */     
/* 198:325 */     this.m_weights[0] = temp1;
/* 199:326 */     this.m_changeInWeights[0] = temp2;
/* 200:    */   }
/* 201:    */   
/* 202:    */   public String getRevision()
/* 203:    */   {
/* 204:336 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 205:    */   }
/* 206:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.neural.NeuralNode
 * JD-Core Version:    0.7.0.1
 */