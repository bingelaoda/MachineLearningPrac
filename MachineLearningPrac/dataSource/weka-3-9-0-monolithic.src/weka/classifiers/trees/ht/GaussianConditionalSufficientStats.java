/*   1:    */ package weka.classifiers.trees.ht;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.HashMap;
/*   6:    */ import java.util.List;
/*   7:    */ import java.util.Map;
/*   8:    */ import java.util.Map.Entry;
/*   9:    */ import java.util.TreeSet;
/*  10:    */ import weka.core.Statistics;
/*  11:    */ import weka.core.Utils;
/*  12:    */ import weka.estimators.UnivariateNormalEstimator;
/*  13:    */ 
/*  14:    */ public class GaussianConditionalSufficientStats
/*  15:    */   extends ConditionalSufficientStats
/*  16:    */   implements Serializable
/*  17:    */ {
/*  18:    */   private static final long serialVersionUID = -1527915607201784762L;
/*  19:    */   
/*  20:    */   protected class GaussianEstimator
/*  21:    */     extends UnivariateNormalEstimator
/*  22:    */     implements Serializable
/*  23:    */   {
/*  24:    */     private static final long serialVersionUID = 4756032800685001315L;
/*  25:    */     
/*  26:    */     protected GaussianEstimator() {}
/*  27:    */     
/*  28:    */     public double getSumOfWeights()
/*  29:    */     {
/*  30: 64 */       return this.m_SumOfWeights;
/*  31:    */     }
/*  32:    */     
/*  33:    */     public double probabilityDensity(double value)
/*  34:    */     {
/*  35: 68 */       updateMeanAndVariance();
/*  36: 70 */       if (this.m_SumOfWeights > 0.0D)
/*  37:    */       {
/*  38: 71 */         double stdDev = Math.sqrt(this.m_Variance);
/*  39: 72 */         if (stdDev > 0.0D)
/*  40:    */         {
/*  41: 73 */           double diff = value - this.m_Mean;
/*  42: 74 */           return 1.0D / (CONST * stdDev) * Math.exp(-(diff * diff / (2.0D * this.m_Variance)));
/*  43:    */         }
/*  44: 77 */         return value == this.m_Mean ? 1.0D : 0.0D;
/*  45:    */       }
/*  46: 80 */       return 0.0D;
/*  47:    */     }
/*  48:    */     
/*  49:    */     public double[] weightLessThanEqualAndGreaterThan(double value)
/*  50:    */     {
/*  51: 84 */       double stdDev = Math.sqrt(this.m_Variance);
/*  52: 85 */       double equalW = probabilityDensity(value) * this.m_SumOfWeights;
/*  53:    */       
/*  54: 87 */       double lessW = value < this.m_Mean ? this.m_SumOfWeights - equalW : stdDev > 0.0D ? Statistics.normalProbability((value - this.m_Mean) / stdDev) * this.m_SumOfWeights - equalW : 0.0D;
/*  55:    */       
/*  56:    */ 
/*  57:    */ 
/*  58: 91 */       double greaterW = this.m_SumOfWeights - equalW - lessW;
/*  59:    */       
/*  60: 93 */       return new double[] { lessW, equalW, greaterW };
/*  61:    */     }
/*  62:    */   }
/*  63:    */   
/*  64: 97 */   protected Map<String, Double> m_minValObservedPerClass = new HashMap();
/*  65: 98 */   protected Map<String, Double> m_maxValObservedPerClass = new HashMap();
/*  66:100 */   protected int m_numBins = 10;
/*  67:    */   
/*  68:    */   public void setNumBins(int b)
/*  69:    */   {
/*  70:103 */     this.m_numBins = b;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public int getNumBins()
/*  74:    */   {
/*  75:107 */     return this.m_numBins;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void update(double attVal, String classVal, double weight)
/*  79:    */   {
/*  80:112 */     if (!Utils.isMissingValue(attVal))
/*  81:    */     {
/*  82:113 */       GaussianEstimator norm = (GaussianEstimator)this.m_classLookup.get(classVal);
/*  83:114 */       if (norm == null)
/*  84:    */       {
/*  85:115 */         norm = new GaussianEstimator();
/*  86:116 */         this.m_classLookup.put(classVal, norm);
/*  87:117 */         this.m_minValObservedPerClass.put(classVal, Double.valueOf(attVal));
/*  88:118 */         this.m_maxValObservedPerClass.put(classVal, Double.valueOf(attVal));
/*  89:    */       }
/*  90:    */       else
/*  91:    */       {
/*  92:120 */         if (attVal < ((Double)this.m_minValObservedPerClass.get(classVal)).doubleValue()) {
/*  93:121 */           this.m_minValObservedPerClass.put(classVal, Double.valueOf(attVal));
/*  94:    */         }
/*  95:124 */         if (attVal > ((Double)this.m_maxValObservedPerClass.get(classVal)).doubleValue()) {
/*  96:125 */           this.m_maxValObservedPerClass.put(classVal, Double.valueOf(attVal));
/*  97:    */         }
/*  98:    */       }
/*  99:128 */       norm.addValue(attVal, weight);
/* 100:    */     }
/* 101:    */   }
/* 102:    */   
/* 103:    */   public double probabilityOfAttValConditionedOnClass(double attVal, String classVal)
/* 104:    */   {
/* 105:135 */     GaussianEstimator norm = (GaussianEstimator)this.m_classLookup.get(classVal);
/* 106:136 */     if (norm == null) {
/* 107:137 */       return 0.0D;
/* 108:    */     }
/* 109:141 */     return norm.probabilityDensity(attVal);
/* 110:    */   }
/* 111:    */   
/* 112:    */   protected TreeSet<Double> getSplitPointCandidates()
/* 113:    */   {
/* 114:145 */     TreeSet<Double> splits = new TreeSet();
/* 115:146 */     double min = (1.0D / 0.0D);
/* 116:147 */     double max = (-1.0D / 0.0D);
/* 117:149 */     for (String classVal : this.m_classLookup.keySet()) {
/* 118:150 */       if (this.m_minValObservedPerClass.containsKey(classVal))
/* 119:    */       {
/* 120:151 */         if (((Double)this.m_minValObservedPerClass.get(classVal)).doubleValue() < min) {
/* 121:152 */           min = ((Double)this.m_minValObservedPerClass.get(classVal)).doubleValue();
/* 122:    */         }
/* 123:155 */         if (((Double)this.m_maxValObservedPerClass.get(classVal)).doubleValue() > max) {
/* 124:156 */           max = ((Double)this.m_maxValObservedPerClass.get(classVal)).doubleValue();
/* 125:    */         }
/* 126:    */       }
/* 127:    */     }
/* 128:161 */     if (min < (1.0D / 0.0D))
/* 129:    */     {
/* 130:162 */       double bin = max - min;
/* 131:163 */       bin /= (this.m_numBins + 1);
/* 132:164 */       for (int i = 0; i < this.m_numBins; i++)
/* 133:    */       {
/* 134:165 */         double split = min + bin * (i + 1);
/* 135:167 */         if ((split > min) && (split < max)) {
/* 136:168 */           splits.add(Double.valueOf(split));
/* 137:    */         }
/* 138:    */       }
/* 139:    */     }
/* 140:172 */     return splits;
/* 141:    */   }
/* 142:    */   
/* 143:    */   protected List<Map<String, WeightMass>> classDistsAfterSplit(double splitVal)
/* 144:    */   {
/* 145:176 */     Map<String, WeightMass> lhsDist = new HashMap();
/* 146:177 */     Map<String, WeightMass> rhsDist = new HashMap();
/* 147:179 */     for (Map.Entry<String, Object> e : this.m_classLookup.entrySet())
/* 148:    */     {
/* 149:180 */       String classVal = (String)e.getKey();
/* 150:181 */       GaussianEstimator attEst = (GaussianEstimator)e.getValue();
/* 151:183 */       if (attEst != null) {
/* 152:184 */         if (splitVal < ((Double)this.m_minValObservedPerClass.get(classVal)).doubleValue())
/* 153:    */         {
/* 154:185 */           WeightMass mass = (WeightMass)rhsDist.get(classVal);
/* 155:186 */           if (mass == null)
/* 156:    */           {
/* 157:187 */             mass = new WeightMass();
/* 158:188 */             rhsDist.put(classVal, mass);
/* 159:    */           }
/* 160:190 */           mass.m_weight += attEst.getSumOfWeights();
/* 161:    */         }
/* 162:191 */         else if (splitVal > ((Double)this.m_maxValObservedPerClass.get(classVal)).doubleValue())
/* 163:    */         {
/* 164:192 */           WeightMass mass = (WeightMass)lhsDist.get(classVal);
/* 165:193 */           if (mass == null)
/* 166:    */           {
/* 167:194 */             mass = new WeightMass();
/* 168:195 */             lhsDist.put(classVal, mass);
/* 169:    */           }
/* 170:197 */           mass.m_weight += attEst.getSumOfWeights();
/* 171:    */         }
/* 172:    */         else
/* 173:    */         {
/* 174:199 */           double[] weights = attEst.weightLessThanEqualAndGreaterThan(splitVal);
/* 175:200 */           WeightMass mass = (WeightMass)lhsDist.get(classVal);
/* 176:201 */           if (mass == null)
/* 177:    */           {
/* 178:202 */             mass = new WeightMass();
/* 179:203 */             lhsDist.put(classVal, mass);
/* 180:    */           }
/* 181:205 */           mass.m_weight += weights[0] + weights[1];
/* 182:    */           
/* 183:207 */           mass = (WeightMass)rhsDist.get(classVal);
/* 184:208 */           if (mass == null)
/* 185:    */           {
/* 186:209 */             mass = new WeightMass();
/* 187:210 */             rhsDist.put(classVal, mass);
/* 188:    */           }
/* 189:212 */           mass.m_weight += weights[2];
/* 190:    */         }
/* 191:    */       }
/* 192:    */     }
/* 193:217 */     List<Map<String, WeightMass>> dists = new ArrayList();
/* 194:218 */     dists.add(lhsDist);
/* 195:219 */     dists.add(rhsDist);
/* 196:    */     
/* 197:221 */     return dists;
/* 198:    */   }
/* 199:    */   
/* 200:    */   public SplitCandidate bestSplit(SplitMetric splitMetric, Map<String, WeightMass> preSplitDist, String attName)
/* 201:    */   {
/* 202:228 */     SplitCandidate best = null;
/* 203:    */     
/* 204:230 */     TreeSet<Double> candidates = getSplitPointCandidates();
/* 205:231 */     for (Double s : candidates)
/* 206:    */     {
/* 207:232 */       List<Map<String, WeightMass>> postSplitDists = classDistsAfterSplit(s.doubleValue());
/* 208:    */       
/* 209:234 */       double splitMerit = splitMetric.evaluateSplit(preSplitDist, postSplitDists);
/* 210:237 */       if ((best == null) || (splitMerit > best.m_splitMerit))
/* 211:    */       {
/* 212:238 */         Split split = new UnivariateNumericBinarySplit(attName, s.doubleValue());
/* 213:239 */         best = new SplitCandidate(split, postSplitDists, splitMerit);
/* 214:    */       }
/* 215:    */     }
/* 216:243 */     return best;
/* 217:    */   }
/* 218:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.ht.GaussianConditionalSufficientStats
 * JD-Core Version:    0.7.0.1
 */