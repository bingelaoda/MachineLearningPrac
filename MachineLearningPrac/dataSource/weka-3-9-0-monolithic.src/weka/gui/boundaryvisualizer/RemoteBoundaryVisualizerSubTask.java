/*   1:    */ package weka.gui.boundaryvisualizer;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Random;
/*   5:    */ import weka.classifiers.Classifier;
/*   6:    */ import weka.core.Attribute;
/*   7:    */ import weka.core.DenseInstance;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.Utils;
/*  11:    */ import weka.experiment.Task;
/*  12:    */ import weka.experiment.TaskStatusInfo;
/*  13:    */ 
/*  14:    */ public class RemoteBoundaryVisualizerSubTask
/*  15:    */   implements Task
/*  16:    */ {
/*  17:    */   private static final long serialVersionUID = -5275252329449241592L;
/*  18: 50 */   private final TaskStatusInfo m_status = new TaskStatusInfo();
/*  19:    */   private RemoteResult m_result;
/*  20:    */   private int m_rowNumber;
/*  21:    */   private int m_panelHeight;
/*  22:    */   private int m_panelWidth;
/*  23:    */   private Classifier m_classifier;
/*  24:    */   private DataGenerator m_dataGenerator;
/*  25:    */   private Instances m_trainingData;
/*  26:    */   private int m_xAttribute;
/*  27:    */   private int m_yAttribute;
/*  28:    */   private double m_pixHeight;
/*  29:    */   private double m_pixWidth;
/*  30:    */   private double m_minX;
/*  31:    */   private double m_minY;
/*  32: 83 */   private int m_numOfSamplesPerRegion = 2;
/*  33:    */   private int m_numOfSamplesPerGenerator;
/*  34: 87 */   private double m_samplesBase = 2.0D;
/*  35:    */   private Random m_random;
/*  36:    */   private double[] m_weightingAttsValues;
/*  37:    */   private boolean[] m_attsToWeightOn;
/*  38:    */   private double[] m_vals;
/*  39:    */   private double[] m_dist;
/*  40:    */   private Instance m_predInst;
/*  41:    */   
/*  42:    */   public void setRowNumber(int rn)
/*  43:    */   {
/*  44:104 */     this.m_rowNumber = rn;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void setPanelWidth(int pw)
/*  48:    */   {
/*  49:113 */     this.m_panelWidth = pw;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void setPanelHeight(int ph)
/*  53:    */   {
/*  54:122 */     this.m_panelHeight = ph;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void setPixHeight(double ph)
/*  58:    */   {
/*  59:131 */     this.m_pixHeight = ph;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void setPixWidth(double pw)
/*  63:    */   {
/*  64:140 */     this.m_pixWidth = pw;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void setClassifier(Classifier dc)
/*  68:    */   {
/*  69:149 */     this.m_classifier = dc;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void setDataGenerator(DataGenerator dg)
/*  73:    */   {
/*  74:158 */     this.m_dataGenerator = dg;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public void setInstances(Instances i)
/*  78:    */   {
/*  79:167 */     this.m_trainingData = i;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public void setMinMaxX(double minx, double maxx)
/*  83:    */   {
/*  84:177 */     this.m_minX = minx;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public void setMinMaxY(double miny, double maxy)
/*  88:    */   {
/*  89:187 */     this.m_minY = miny;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public void setXAttribute(int xatt)
/*  93:    */   {
/*  94:196 */     this.m_xAttribute = xatt;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public void setYAttribute(int yatt)
/*  98:    */   {
/*  99:205 */     this.m_yAttribute = yatt;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public void setNumSamplesPerRegion(int num)
/* 103:    */   {
/* 104:215 */     this.m_numOfSamplesPerRegion = num;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public void setGeneratorSamplesBase(double ksb)
/* 108:    */   {
/* 109:225 */     this.m_samplesBase = ksb;
/* 110:    */   }
/* 111:    */   
/* 112:    */   public void execute()
/* 113:    */   {
/* 114:234 */     this.m_random = new Random(this.m_rowNumber * 11);
/* 115:235 */     this.m_dataGenerator.setSeed(this.m_rowNumber * 11);
/* 116:236 */     this.m_result = new RemoteResult(this.m_rowNumber, this.m_panelWidth);
/* 117:237 */     this.m_status.setTaskResult(this.m_result);
/* 118:238 */     this.m_status.setExecutionStatus(1);
/* 119:    */     try
/* 120:    */     {
/* 121:241 */       this.m_numOfSamplesPerGenerator = ((int)Math.pow(this.m_samplesBase, this.m_trainingData.numAttributes() - 3));
/* 122:243 */       if (this.m_trainingData == null) {
/* 123:244 */         throw new Exception("No training data set (BoundaryPanel)");
/* 124:    */       }
/* 125:246 */       if (this.m_classifier == null) {
/* 126:247 */         throw new Exception("No classifier set (BoundaryPanel)");
/* 127:    */       }
/* 128:249 */       if (this.m_dataGenerator == null) {
/* 129:250 */         throw new Exception("No data generator set (BoundaryPanel)");
/* 130:    */       }
/* 131:252 */       if ((this.m_trainingData.attribute(this.m_xAttribute).isNominal()) || (this.m_trainingData.attribute(this.m_yAttribute).isNominal())) {
/* 132:254 */         throw new Exception("Visualization dimensions must be numeric (RemoteBoundaryVisualizerSubTask)");
/* 133:    */       }
/* 134:258 */       this.m_attsToWeightOn = new boolean[this.m_trainingData.numAttributes()];
/* 135:259 */       this.m_attsToWeightOn[this.m_xAttribute] = true;
/* 136:260 */       this.m_attsToWeightOn[this.m_yAttribute] = true;
/* 137:    */       
/* 138:    */ 
/* 139:263 */       this.m_weightingAttsValues = new double[this.m_attsToWeightOn.length];
/* 140:264 */       this.m_vals = new double[this.m_trainingData.numAttributes()];
/* 141:265 */       this.m_predInst = new DenseInstance(1.0D, this.m_vals);
/* 142:266 */       this.m_predInst.setDataset(this.m_trainingData);
/* 143:    */       
/* 144:268 */       System.err.println("Executing row number " + this.m_rowNumber);
/* 145:269 */       for (int j = 0; j < this.m_panelWidth; j++)
/* 146:    */       {
/* 147:270 */         double[] preds = calculateRegionProbs(j, this.m_rowNumber);
/* 148:271 */         this.m_result.setLocationProbs(j, preds);
/* 149:272 */         this.m_result.setPercentCompleted((int)(100.0D * (j / this.m_panelWidth)));
/* 150:    */       }
/* 151:    */     }
/* 152:    */     catch (Exception ex)
/* 153:    */     {
/* 154:276 */       this.m_status.setExecutionStatus(2);
/* 155:277 */       this.m_status.setStatusMessage("Row " + this.m_rowNumber + " failed.");
/* 156:278 */       System.err.print(ex);
/* 157:279 */       return;
/* 158:    */     }
/* 159:283 */     this.m_status.setExecutionStatus(3);
/* 160:284 */     this.m_status.setStatusMessage("Row " + this.m_rowNumber + " completed successfully.");
/* 161:    */   }
/* 162:    */   
/* 163:    */   private double[] calculateRegionProbs(int j, int i)
/* 164:    */     throws Exception
/* 165:    */   {
/* 166:289 */     double[] sumOfProbsForRegion = new double[this.m_trainingData.classAttribute().numValues()];
/* 167:292 */     for (int u = 0; u < this.m_numOfSamplesPerRegion; u++)
/* 168:    */     {
/* 169:294 */       double[] sumOfProbsForLocation = new double[this.m_trainingData.classAttribute().numValues()];
/* 170:    */       
/* 171:    */ 
/* 172:297 */       this.m_weightingAttsValues[this.m_xAttribute] = getRandomX(j);
/* 173:298 */       this.m_weightingAttsValues[this.m_yAttribute] = getRandomY(this.m_panelHeight - i - 1);
/* 174:    */       
/* 175:300 */       this.m_dataGenerator.setWeightingValues(this.m_weightingAttsValues);
/* 176:    */       
/* 177:302 */       double[] weights = this.m_dataGenerator.getWeights();
/* 178:303 */       double sumOfWeights = Utils.sum(weights);
/* 179:304 */       int[] indices = Utils.sort(weights);
/* 180:    */       
/* 181:    */ 
/* 182:307 */       int[] newIndices = new int[indices.length];
/* 183:308 */       double sumSoFar = 0.0D;
/* 184:309 */       double criticalMass = 0.99D * sumOfWeights;
/* 185:310 */       int index = weights.length - 1;
/* 186:311 */       int counter = 0;
/* 187:312 */       for (int z = weights.length - 1; z >= 0; z--)
/* 188:    */       {
/* 189:313 */         newIndices[(index--)] = indices[z];
/* 190:314 */         sumSoFar += weights[indices[z]];
/* 191:315 */         counter++;
/* 192:316 */         if (sumSoFar > criticalMass) {
/* 193:    */           break;
/* 194:    */         }
/* 195:    */       }
/* 196:320 */       indices = new int[counter];
/* 197:321 */       System.arraycopy(newIndices, index + 1, indices, 0, counter);
/* 198:323 */       for (int z = 0; z < this.m_numOfSamplesPerGenerator; z++)
/* 199:    */       {
/* 200:325 */         this.m_dataGenerator.setWeightingValues(this.m_weightingAttsValues);
/* 201:326 */         double[][] values = this.m_dataGenerator.generateInstances(indices);
/* 202:328 */         for (int q = 0; q < values.length; q++) {
/* 203:329 */           if (values[q] != null)
/* 204:    */           {
/* 205:330 */             System.arraycopy(values[q], 0, this.m_vals, 0, this.m_vals.length);
/* 206:331 */             this.m_vals[this.m_xAttribute] = this.m_weightingAttsValues[this.m_xAttribute];
/* 207:332 */             this.m_vals[this.m_yAttribute] = this.m_weightingAttsValues[this.m_yAttribute];
/* 208:    */             
/* 209:    */ 
/* 210:335 */             this.m_dist = this.m_classifier.distributionForInstance(this.m_predInst);
/* 211:337 */             for (int k = 0; k < sumOfProbsForLocation.length; k++) {
/* 212:338 */               sumOfProbsForLocation[k] += this.m_dist[k] * weights[q];
/* 213:    */             }
/* 214:    */           }
/* 215:    */         }
/* 216:    */       }
/* 217:344 */       for (int k = 0; k < sumOfProbsForRegion.length; k++) {
/* 218:345 */         sumOfProbsForRegion[k] += sumOfProbsForLocation[k] * sumOfWeights;
/* 219:    */       }
/* 220:    */     }
/* 221:350 */     Utils.normalize(sumOfProbsForRegion);
/* 222:    */     
/* 223:    */ 
/* 224:353 */     double[] tempDist = new double[sumOfProbsForRegion.length];
/* 225:354 */     System.arraycopy(sumOfProbsForRegion, 0, tempDist, 0, sumOfProbsForRegion.length);
/* 226:    */     
/* 227:    */ 
/* 228:357 */     return tempDist;
/* 229:    */   }
/* 230:    */   
/* 231:    */   private double getRandomX(int pix)
/* 232:    */   {
/* 233:369 */     double minPix = this.m_minX + pix * this.m_pixWidth;
/* 234:    */     
/* 235:371 */     return minPix + this.m_random.nextDouble() * this.m_pixWidth;
/* 236:    */   }
/* 237:    */   
/* 238:    */   private double getRandomY(int pix)
/* 239:    */   {
/* 240:383 */     double minPix = this.m_minY + pix * this.m_pixHeight;
/* 241:    */     
/* 242:385 */     return minPix + this.m_random.nextDouble() * this.m_pixHeight;
/* 243:    */   }
/* 244:    */   
/* 245:    */   public TaskStatusInfo getTaskStatus()
/* 246:    */   {
/* 247:395 */     return this.m_status;
/* 248:    */   }
/* 249:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.boundaryvisualizer.RemoteBoundaryVisualizerSubTask
 * JD-Core Version:    0.7.0.1
 */