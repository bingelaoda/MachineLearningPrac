/*   1:    */ package weka.gui.boundaryvisualizer;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.Random;
/*   5:    */ import weka.core.Attribute;
/*   6:    */ import weka.core.Instance;
/*   7:    */ import weka.core.Instances;
/*   8:    */ import weka.core.Utils;
/*   9:    */ 
/*  10:    */ public class KDDataGenerator
/*  11:    */   implements DataGenerator, Serializable
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = -958573275606402792L;
/*  14:    */   private Instances m_instances;
/*  15:    */   private double[] m_globalMeansOrModes;
/*  16: 63 */   private final double m_laplaceConst = 1.0D;
/*  17: 66 */   private int m_seed = 1;
/*  18:    */   private Random m_random;
/*  19:    */   private boolean[] m_weightingDimensions;
/*  20:    */   private double[] m_weightingValues;
/*  21: 82 */   private static double m_normConst = Math.sqrt(6.283185307179586D);
/*  22: 85 */   private int m_kernelBandwidth = 3;
/*  23:    */   private double[][] m_kernelParams;
/*  24:    */   protected double[] m_Min;
/*  25:    */   protected double[] m_Max;
/*  26:    */   
/*  27:    */   public void buildGenerator(Instances inputInstances)
/*  28:    */     throws Exception
/*  29:    */   {
/*  30:107 */     this.m_random = new Random(this.m_seed);
/*  31:    */     
/*  32:109 */     this.m_instances = inputInstances;
/*  33:    */     
/*  34:111 */     this.m_globalMeansOrModes = new double[this.m_instances.numAttributes()];
/*  35:112 */     if (this.m_weightingDimensions == null) {
/*  36:113 */       this.m_weightingDimensions = new boolean[this.m_instances.numAttributes()];
/*  37:    */     }
/*  38:126 */     for (int i = 0; i < this.m_instances.numAttributes(); i++) {
/*  39:127 */       if (i != this.m_instances.classIndex()) {
/*  40:128 */         this.m_globalMeansOrModes[i] = this.m_instances.meanOrMode(i);
/*  41:    */       }
/*  42:    */     }
/*  43:132 */     this.m_kernelParams = new double[this.m_instances.numInstances()][this.m_instances.numAttributes()];
/*  44:    */     
/*  45:134 */     computeParams();
/*  46:    */   }
/*  47:    */   
/*  48:    */   public double[] getWeights()
/*  49:    */   {
/*  50:140 */     double[] weights = new double[this.m_instances.numInstances()];
/*  51:142 */     for (int k = 0; k < this.m_instances.numInstances(); k++)
/*  52:    */     {
/*  53:143 */       double weight = 1.0D;
/*  54:144 */       for (int i = 0; i < this.m_instances.numAttributes(); i++) {
/*  55:145 */         if (this.m_weightingDimensions[i] != 0)
/*  56:    */         {
/*  57:146 */           double mean = 0.0D;
/*  58:147 */           if (!this.m_instances.instance(k).isMissing(i)) {
/*  59:148 */             mean = this.m_instances.instance(k).value(i);
/*  60:    */           } else {
/*  61:150 */             mean = this.m_globalMeansOrModes[i];
/*  62:    */           }
/*  63:152 */           double wm = 1.0D;
/*  64:    */           
/*  65:    */ 
/*  66:    */ 
/*  67:156 */           wm = normalDens(this.m_weightingValues[i], mean, this.m_kernelParams[k][i]);
/*  68:    */           
/*  69:158 */           weight *= wm;
/*  70:    */         }
/*  71:    */       }
/*  72:161 */       weights[k] = weight;
/*  73:    */     }
/*  74:163 */     return weights;
/*  75:    */   }
/*  76:    */   
/*  77:    */   private double[] computeCumulativeDistribution(double[] dist)
/*  78:    */   {
/*  79:174 */     double[] cumDist = new double[dist.length];
/*  80:175 */     double sum = 0.0D;
/*  81:176 */     for (int i = 0; i < dist.length; i++)
/*  82:    */     {
/*  83:177 */       sum += dist[i];
/*  84:178 */       cumDist[i] = sum;
/*  85:    */     }
/*  86:181 */     return cumDist;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public double[][] generateInstances(int[] indices)
/*  90:    */     throws Exception
/*  91:    */   {
/*  92:194 */     double[][] values = new double[this.m_instances.numInstances()][];
/*  93:196 */     for (int k = 0; k < indices.length; k++)
/*  94:    */     {
/*  95:197 */       values[indices[k]] = new double[this.m_instances.numAttributes()];
/*  96:198 */       for (int i = 0; i < this.m_instances.numAttributes(); i++) {
/*  97:199 */         if ((this.m_weightingDimensions[i] == 0) && (i != this.m_instances.classIndex())) {
/*  98:200 */           if (this.m_instances.attribute(i).isNumeric())
/*  99:    */           {
/* 100:201 */             double mean = 0.0D;
/* 101:202 */             double val = this.m_random.nextGaussian();
/* 102:203 */             if (!this.m_instances.instance(indices[k]).isMissing(i)) {
/* 103:204 */               mean = this.m_instances.instance(indices[k]).value(i);
/* 104:    */             } else {
/* 105:206 */               mean = this.m_globalMeansOrModes[i];
/* 106:    */             }
/* 107:209 */             val *= this.m_kernelParams[indices[k]][i];
/* 108:210 */             val += mean;
/* 109:    */             
/* 110:212 */             values[indices[k]][i] = val;
/* 111:    */           }
/* 112:    */           else
/* 113:    */           {
/* 114:215 */             double[] dist = new double[this.m_instances.attribute(i).numValues()];
/* 115:216 */             for (int j = 0; j < dist.length; j++) {
/* 116:217 */               dist[j] = 1.0D;
/* 117:    */             }
/* 118:219 */             if (!this.m_instances.instance(indices[k]).isMissing(i)) {
/* 119:220 */               dist[((int)this.m_instances.instance(indices[k]).value(i))] += 1.0D;
/* 120:    */             } else {
/* 121:222 */               dist[((int)this.m_globalMeansOrModes[i])] += 1.0D;
/* 122:    */             }
/* 123:224 */             Utils.normalize(dist);
/* 124:225 */             double[] cumDist = computeCumulativeDistribution(dist);
/* 125:226 */             double randomVal = this.m_random.nextDouble();
/* 126:227 */             int instVal = 0;
/* 127:228 */             for (int j = 0; j < cumDist.length; j++) {
/* 128:229 */               if (randomVal <= cumDist[j])
/* 129:    */               {
/* 130:230 */                 instVal = j;
/* 131:231 */                 break;
/* 132:    */               }
/* 133:    */             }
/* 134:234 */             values[indices[k]][i] = instVal;
/* 135:    */           }
/* 136:    */         }
/* 137:    */       }
/* 138:    */     }
/* 139:239 */     return values;
/* 140:    */   }
/* 141:    */   
/* 142:    */   private double normalDens(double x, double mean, double stdDev)
/* 143:    */   {
/* 144:250 */     double diff = x - mean;
/* 145:    */     
/* 146:252 */     return 1.0D / (m_normConst * stdDev) * Math.exp(-(diff * diff / (2.0D * stdDev * stdDev)));
/* 147:    */   }
/* 148:    */   
/* 149:    */   public void setWeightingDimensions(boolean[] dims)
/* 150:    */   {
/* 151:264 */     this.m_weightingDimensions = dims;
/* 152:    */   }
/* 153:    */   
/* 154:    */   public void setWeightingValues(double[] vals)
/* 155:    */   {
/* 156:277 */     this.m_weightingValues = vals;
/* 157:    */   }
/* 158:    */   
/* 159:    */   public int getNumGeneratingModels()
/* 160:    */   {
/* 161:287 */     if (this.m_instances != null) {
/* 162:288 */       return this.m_instances.numInstances();
/* 163:    */     }
/* 164:290 */     return 0;
/* 165:    */   }
/* 166:    */   
/* 167:    */   public void setKernelBandwidth(int kb)
/* 168:    */   {
/* 169:299 */     this.m_kernelBandwidth = kb;
/* 170:    */   }
/* 171:    */   
/* 172:    */   public int getKernelBandwidth()
/* 173:    */   {
/* 174:308 */     return this.m_kernelBandwidth;
/* 175:    */   }
/* 176:    */   
/* 177:    */   public void setSeed(int seed)
/* 178:    */   {
/* 179:318 */     this.m_seed = seed;
/* 180:319 */     this.m_random = new Random(this.m_seed);
/* 181:    */   }
/* 182:    */   
/* 183:    */   private double distance(Instance first, Instance second)
/* 184:    */   {
/* 185:331 */     double distance = 0.0D;
/* 186:333 */     for (int i = 0; i < this.m_instances.numAttributes(); i++) {
/* 187:334 */       if (i != this.m_instances.classIndex())
/* 188:    */       {
/* 189:337 */         double firstVal = this.m_globalMeansOrModes[i];
/* 190:338 */         double secondVal = this.m_globalMeansOrModes[i];
/* 191:    */         double diff;
/* 192:340 */         switch (this.m_instances.attribute(i).type())
/* 193:    */         {
/* 194:    */         case 0: 
/* 195:343 */           if (!first.isMissing(i)) {
/* 196:344 */             firstVal = first.value(i);
/* 197:    */           }
/* 198:347 */           if (!second.isMissing(i)) {
/* 199:348 */             secondVal = second.value(i);
/* 200:    */           }
/* 201:351 */           diff = norm(firstVal, i) - norm(secondVal, i);
/* 202:    */           
/* 203:353 */           break;
/* 204:    */         default: 
/* 205:355 */           diff = 0.0D;
/* 206:    */         }
/* 207:358 */         distance += diff * diff;
/* 208:    */       }
/* 209:    */     }
/* 210:360 */     return Math.sqrt(distance);
/* 211:    */   }
/* 212:    */   
/* 213:    */   private double norm(double x, int i)
/* 214:    */   {
/* 215:371 */     if ((Double.isNaN(this.m_Min[i])) || (Utils.eq(this.m_Max[i], this.m_Min[i]))) {
/* 216:372 */       return 0.0D;
/* 217:    */     }
/* 218:374 */     return (x - this.m_Min[i]) / (this.m_Max[i] - this.m_Min[i]);
/* 219:    */   }
/* 220:    */   
/* 221:    */   private void updateMinMax(Instance instance)
/* 222:    */   {
/* 223:386 */     for (int j = 0; j < this.m_instances.numAttributes(); j++) {
/* 224:387 */       if (!instance.isMissing(j)) {
/* 225:388 */         if (Double.isNaN(this.m_Min[j]))
/* 226:    */         {
/* 227:389 */           this.m_Min[j] = instance.value(j);
/* 228:390 */           this.m_Max[j] = instance.value(j);
/* 229:    */         }
/* 230:391 */         else if (instance.value(j) < this.m_Min[j])
/* 231:    */         {
/* 232:392 */           this.m_Min[j] = instance.value(j);
/* 233:    */         }
/* 234:393 */         else if (instance.value(j) > this.m_Max[j])
/* 235:    */         {
/* 236:394 */           this.m_Max[j] = instance.value(j);
/* 237:    */         }
/* 238:    */       }
/* 239:    */     }
/* 240:    */   }
/* 241:    */   
/* 242:    */   private void computeParams()
/* 243:    */     throws Exception
/* 244:    */   {
/* 245:402 */     this.m_Min = new double[this.m_instances.numAttributes()];
/* 246:403 */     this.m_Max = new double[this.m_instances.numAttributes()];
/* 247:404 */     for (int i = 0; i < this.m_instances.numAttributes(); i++)
/* 248:    */     {
/* 249:405 */       double tmp52_49 = (0.0D / 0.0D);this.m_Max[i] = tmp52_49;this.m_Min[i] = tmp52_49;
/* 250:    */     }
/* 251:407 */     for (int i = 0; i < this.m_instances.numInstances(); i++) {
/* 252:408 */       updateMinMax(this.m_instances.instance(i));
/* 253:    */     }
/* 254:411 */     double[] distances = new double[this.m_instances.numInstances()];
/* 255:412 */     for (int i = 0; i < this.m_instances.numInstances(); i++)
/* 256:    */     {
/* 257:413 */       Instance current = this.m_instances.instance(i);
/* 258:414 */       for (int j = 0; j < this.m_instances.numInstances(); j++) {
/* 259:415 */         distances[j] = distance(current, this.m_instances.instance(j));
/* 260:    */       }
/* 261:417 */       int[] sorted = Utils.sort(distances);
/* 262:418 */       int k = this.m_kernelBandwidth;
/* 263:419 */       double bandwidth = distances[sorted[k]];
/* 264:422 */       if (bandwidth <= 0.0D)
/* 265:    */       {
/* 266:423 */         for (int j = k + 1; j < sorted.length; j++) {
/* 267:424 */           if (distances[sorted[j]] > bandwidth)
/* 268:    */           {
/* 269:425 */             bandwidth = distances[sorted[j]];
/* 270:426 */             break;
/* 271:    */           }
/* 272:    */         }
/* 273:429 */         if (bandwidth <= 0.0D) {
/* 274:430 */           throw new Exception("All training instances coincide with test instance!");
/* 275:    */         }
/* 276:    */       }
/* 277:434 */       for (int j = 0; j < this.m_instances.numAttributes(); j++) {
/* 278:435 */         if (this.m_Max[j] - this.m_Min[j] > 0.0D) {
/* 279:436 */           this.m_kernelParams[i][j] = (bandwidth * (this.m_Max[j] - this.m_Min[j]));
/* 280:    */         }
/* 281:    */       }
/* 282:    */     }
/* 283:    */   }
/* 284:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.boundaryvisualizer.KDDataGenerator
 * JD-Core Version:    0.7.0.1
 */