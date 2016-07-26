/*   1:    */ package weka.classifiers.trees.j48;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Random;
/*   5:    */ import weka.classifiers.bayes.NaiveBayesUpdateable;
/*   6:    */ import weka.core.Attribute;
/*   7:    */ import weka.core.Instance;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.RevisionUtils;
/*  10:    */ import weka.filters.Filter;
/*  11:    */ import weka.filters.supervised.attribute.Discretize;
/*  12:    */ 
/*  13:    */ public class NBTreeSplit
/*  14:    */   extends ClassifierSplitModel
/*  15:    */ {
/*  16:    */   private static final long serialVersionUID = 8922627123884975070L;
/*  17:    */   protected int m_complexityIndex;
/*  18:    */   protected final int m_attIndex;
/*  19:    */   protected final double m_sumOfWeights;
/*  20:    */   protected double m_errors;
/*  21:    */   protected C45Split m_c45S;
/*  22:    */   NBTreeNoSplit m_globalNB;
/*  23:    */   
/*  24:    */   public NBTreeSplit(int attIndex, int minNoObj, double sumOfWeights)
/*  25:    */   {
/*  26: 70 */     this.m_attIndex = attIndex;
/*  27:    */     
/*  28:    */ 
/*  29: 73 */     this.m_sumOfWeights = sumOfWeights;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public void buildClassifier(Instances trainInstances)
/*  33:    */     throws Exception
/*  34:    */   {
/*  35: 87 */     this.m_numSubsets = 0;
/*  36: 88 */     this.m_errors = 0.0D;
/*  37: 89 */     if (this.m_globalNB != null) {
/*  38: 90 */       this.m_errors = this.m_globalNB.getErrors();
/*  39:    */     }
/*  40: 95 */     if (trainInstances.attribute(this.m_attIndex).isNominal())
/*  41:    */     {
/*  42: 96 */       this.m_complexityIndex = trainInstances.attribute(this.m_attIndex).numValues();
/*  43: 97 */       handleEnumeratedAttribute(trainInstances);
/*  44:    */     }
/*  45:    */     else
/*  46:    */     {
/*  47: 99 */       this.m_complexityIndex = 2;
/*  48:100 */       trainInstances.sort(trainInstances.attribute(this.m_attIndex));
/*  49:101 */       handleNumericAttribute(trainInstances);
/*  50:    */     }
/*  51:    */   }
/*  52:    */   
/*  53:    */   public final int attIndex()
/*  54:    */   {
/*  55:110 */     return this.m_attIndex;
/*  56:    */   }
/*  57:    */   
/*  58:    */   private void handleEnumeratedAttribute(Instances trainInstances)
/*  59:    */     throws Exception
/*  60:    */   {
/*  61:121 */     this.m_c45S = new C45Split(this.m_attIndex, 2, this.m_sumOfWeights, true);
/*  62:122 */     this.m_c45S.buildClassifier(trainInstances);
/*  63:123 */     if (this.m_c45S.numSubsets() == 0) {
/*  64:124 */       return;
/*  65:    */     }
/*  66:126 */     this.m_errors = 0.0D;
/*  67:    */     
/*  68:    */ 
/*  69:129 */     Instances[] trainingSets = new Instances[this.m_complexityIndex];
/*  70:130 */     for (int i = 0; i < this.m_complexityIndex; i++) {
/*  71:131 */       trainingSets[i] = new Instances(trainInstances, 0);
/*  72:    */     }
/*  73:138 */     for (int i = 0; i < trainInstances.numInstances(); i++)
/*  74:    */     {
/*  75:139 */       Instance instance = trainInstances.instance(i);
/*  76:140 */       int subset = this.m_c45S.whichSubset(instance);
/*  77:141 */       if (subset > -1)
/*  78:    */       {
/*  79:142 */         trainingSets[subset].add((Instance)instance.copy());
/*  80:    */       }
/*  81:    */       else
/*  82:    */       {
/*  83:144 */         double[] weights = this.m_c45S.weights(instance);
/*  84:145 */         for (int j = 0; j < this.m_complexityIndex; j++) {
/*  85:    */           try
/*  86:    */           {
/*  87:147 */             Instance temp = (Instance)instance.copy();
/*  88:148 */             if (weights.length == this.m_complexityIndex) {
/*  89:149 */               temp.setWeight(temp.weight() * weights[j]);
/*  90:    */             } else {
/*  91:151 */               temp.setWeight(temp.weight() / this.m_complexityIndex);
/*  92:    */             }
/*  93:153 */             trainingSets[j].add(temp);
/*  94:    */           }
/*  95:    */           catch (Exception ex)
/*  96:    */           {
/*  97:155 */             ex.printStackTrace();
/*  98:156 */             System.err.println("*** " + this.m_complexityIndex);
/*  99:157 */             System.err.println(weights.length);
/* 100:158 */             System.exit(1);
/* 101:    */           }
/* 102:    */         }
/* 103:    */       }
/* 104:    */     }
/* 105:181 */     Random r = new Random(1L);
/* 106:182 */     int minNumCount = 0;
/* 107:183 */     for (int i = 0; i < this.m_complexityIndex; i++) {
/* 108:184 */       if (trainingSets[i].numInstances() >= 5)
/* 109:    */       {
/* 110:185 */         minNumCount++;
/* 111:    */         
/* 112:187 */         Discretize disc = new Discretize();
/* 113:188 */         disc.setInputFormat(trainingSets[i]);
/* 114:189 */         trainingSets[i] = Filter.useFilter(trainingSets[i], disc);
/* 115:    */         
/* 116:191 */         trainingSets[i].randomize(r);
/* 117:192 */         trainingSets[i].stratify(5);
/* 118:193 */         NaiveBayesUpdateable fullModel = new NaiveBayesUpdateable();
/* 119:194 */         fullModel.buildClassifier(trainingSets[i]);
/* 120:    */         
/* 121:    */ 
/* 122:197 */         this.m_errors += NBTreeNoSplit.crossValidate(fullModel, trainingSets[i], r);
/* 123:    */       }
/* 124:    */       else
/* 125:    */       {
/* 126:200 */         for (int j = 0; j < trainingSets[i].numInstances(); j++) {
/* 127:201 */           this.m_errors += trainingSets[i].instance(j).weight();
/* 128:    */         }
/* 129:    */       }
/* 130:    */     }
/* 131:208 */     if (minNumCount > 1) {
/* 132:209 */       this.m_numSubsets = this.m_complexityIndex;
/* 133:    */     }
/* 134:    */   }
/* 135:    */   
/* 136:    */   private void handleNumericAttribute(Instances trainInstances)
/* 137:    */     throws Exception
/* 138:    */   {
/* 139:221 */     this.m_c45S = new C45Split(this.m_attIndex, 2, this.m_sumOfWeights, true);
/* 140:222 */     this.m_c45S.buildClassifier(trainInstances);
/* 141:223 */     if (this.m_c45S.numSubsets() == 0) {
/* 142:224 */       return;
/* 143:    */     }
/* 144:226 */     this.m_errors = 0.0D;
/* 145:    */     
/* 146:228 */     Instances[] trainingSets = new Instances[this.m_complexityIndex];
/* 147:229 */     trainingSets[0] = new Instances(trainInstances, 0);
/* 148:230 */     trainingSets[1] = new Instances(trainInstances, 0);
/* 149:231 */     int subset = -1;
/* 150:234 */     for (int i = 0; i < trainInstances.numInstances(); i++)
/* 151:    */     {
/* 152:235 */       Instance instance = trainInstances.instance(i);
/* 153:236 */       subset = this.m_c45S.whichSubset(instance);
/* 154:237 */       if (subset != -1)
/* 155:    */       {
/* 156:238 */         trainingSets[subset].add((Instance)instance.copy());
/* 157:    */       }
/* 158:    */       else
/* 159:    */       {
/* 160:240 */         double[] weights = this.m_c45S.weights(instance);
/* 161:241 */         for (int j = 0; j < this.m_complexityIndex; j++)
/* 162:    */         {
/* 163:242 */           Instance temp = (Instance)instance.copy();
/* 164:243 */           if (weights.length == this.m_complexityIndex) {
/* 165:244 */             temp.setWeight(temp.weight() * weights[j]);
/* 166:    */           } else {
/* 167:246 */             temp.setWeight(temp.weight() / this.m_complexityIndex);
/* 168:    */           }
/* 169:248 */           trainingSets[j].add(temp);
/* 170:    */         }
/* 171:    */       }
/* 172:    */     }
/* 173:260 */     Random r = new Random(1L);
/* 174:261 */     int minNumCount = 0;
/* 175:262 */     for (int i = 0; i < this.m_complexityIndex; i++) {
/* 176:263 */       if (trainingSets[i].numInstances() > 5)
/* 177:    */       {
/* 178:264 */         minNumCount++;
/* 179:    */         
/* 180:266 */         Discretize disc = new Discretize();
/* 181:267 */         disc.setInputFormat(trainingSets[i]);
/* 182:268 */         trainingSets[i] = Filter.useFilter(trainingSets[i], disc);
/* 183:    */         
/* 184:270 */         trainingSets[i].randomize(r);
/* 185:271 */         trainingSets[i].stratify(5);
/* 186:272 */         NaiveBayesUpdateable fullModel = new NaiveBayesUpdateable();
/* 187:273 */         fullModel.buildClassifier(trainingSets[i]);
/* 188:    */         
/* 189:    */ 
/* 190:276 */         this.m_errors += NBTreeNoSplit.crossValidate(fullModel, trainingSets[i], r);
/* 191:    */       }
/* 192:    */       else
/* 193:    */       {
/* 194:278 */         for (int j = 0; j < trainingSets[i].numInstances(); j++) {
/* 195:279 */           this.m_errors += trainingSets[i].instance(j).weight();
/* 196:    */         }
/* 197:    */       }
/* 198:    */     }
/* 199:286 */     if (minNumCount > 1) {
/* 200:287 */       this.m_numSubsets = this.m_complexityIndex;
/* 201:    */     }
/* 202:    */   }
/* 203:    */   
/* 204:    */   public final int whichSubset(Instance instance)
/* 205:    */     throws Exception
/* 206:    */   {
/* 207:300 */     return this.m_c45S.whichSubset(instance);
/* 208:    */   }
/* 209:    */   
/* 210:    */   public final double[] weights(Instance instance)
/* 211:    */   {
/* 212:309 */     return this.m_c45S.weights(instance);
/* 213:    */   }
/* 214:    */   
/* 215:    */   public final String sourceExpression(int index, Instances data)
/* 216:    */   {
/* 217:323 */     return this.m_c45S.sourceExpression(index, data);
/* 218:    */   }
/* 219:    */   
/* 220:    */   public final String rightSide(int index, Instances data)
/* 221:    */   {
/* 222:334 */     return this.m_c45S.rightSide(index, data);
/* 223:    */   }
/* 224:    */   
/* 225:    */   public final String leftSide(Instances data)
/* 226:    */   {
/* 227:345 */     return this.m_c45S.leftSide(data);
/* 228:    */   }
/* 229:    */   
/* 230:    */   public double classProb(int classIndex, Instance instance, int theSubset)
/* 231:    */     throws Exception
/* 232:    */   {
/* 233:362 */     if (theSubset > -1) {
/* 234:363 */       return this.m_globalNB.classProb(classIndex, instance, theSubset);
/* 235:    */     }
/* 236:365 */     throw new Exception("This shouldn't happen!!!");
/* 237:    */   }
/* 238:    */   
/* 239:    */   public NBTreeNoSplit getGlobalModel()
/* 240:    */   {
/* 241:375 */     return this.m_globalNB;
/* 242:    */   }
/* 243:    */   
/* 244:    */   public void setGlobalModel(NBTreeNoSplit global)
/* 245:    */   {
/* 246:384 */     this.m_globalNB = global;
/* 247:    */   }
/* 248:    */   
/* 249:    */   public double getErrors()
/* 250:    */   {
/* 251:393 */     return this.m_errors;
/* 252:    */   }
/* 253:    */   
/* 254:    */   public String getRevision()
/* 255:    */   {
/* 256:403 */     return RevisionUtils.extract("$Revision: 10531 $");
/* 257:    */   }
/* 258:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.j48.NBTreeSplit
 * JD-Core Version:    0.7.0.1
 */