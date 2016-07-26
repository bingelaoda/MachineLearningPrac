/*   1:    */ package weka.classifiers.evaluation;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.HashMap;
/*   6:    */ import java.util.Iterator;
/*   7:    */ import java.util.List;
/*   8:    */ import java.util.Map;
/*   9:    */ 
/*  10:    */ public class EvaluationMetricHelper
/*  11:    */ {
/*  12:    */   protected Evaluation m_eval;
/*  13: 43 */   protected Map<String, Integer> m_builtin = new HashMap();
/*  14: 46 */   protected Map<String, AbstractEvaluationMetric> m_pluginMetrics = new HashMap();
/*  15:    */   
/*  16:    */   public EvaluationMetricHelper(Evaluation eval)
/*  17:    */   {
/*  18: 55 */     for (int i = 0; i < Evaluation.BUILT_IN_EVAL_METRICS.length; i++) {
/*  19: 56 */       this.m_builtin.put(Evaluation.BUILT_IN_EVAL_METRICS[i].toLowerCase(), Integer.valueOf(i));
/*  20:    */     }
/*  21: 59 */     setEvaluation(eval);
/*  22:    */   }
/*  23:    */   
/*  24:    */   public void setEvaluation(Evaluation eval)
/*  25:    */   {
/*  26: 68 */     this.m_eval = eval;
/*  27: 69 */     initializeWithPluginMetrics();
/*  28:    */   }
/*  29:    */   
/*  30:    */   protected void initializeWithPluginMetrics()
/*  31:    */   {
/*  32: 76 */     this.m_pluginMetrics.clear();
/*  33: 77 */     List<AbstractEvaluationMetric> pluginMetrics = this.m_eval.getPluginMetrics();
/*  34:    */     Iterator i$;
/*  35: 78 */     if ((pluginMetrics != null) && (pluginMetrics.size() > 0)) {
/*  36: 79 */       for (i$ = pluginMetrics.iterator(); i$.hasNext();)
/*  37:    */       {
/*  38: 79 */         m = (AbstractEvaluationMetric)i$.next();
/*  39: 80 */         List<String> statNames = m.getStatisticNames();
/*  40: 81 */         for (String s : statNames) {
/*  41: 82 */           this.m_pluginMetrics.put(s.toLowerCase(), m);
/*  42:    */         }
/*  43:    */       }
/*  44:    */     }
/*  45:    */     AbstractEvaluationMetric m;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public static List<String> getBuiltInMetricNames()
/*  49:    */   {
/*  50: 94 */     List<String> builtIn = new ArrayList();
/*  51: 95 */     builtIn.addAll(Arrays.asList(Evaluation.BUILT_IN_EVAL_METRICS));
/*  52:    */     
/*  53: 97 */     return builtIn;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public static List<String> getPluginMetricNames()
/*  57:    */   {
/*  58:106 */     List<String> pluginNames = new ArrayList();
/*  59:107 */     List<AbstractEvaluationMetric> pluginMetrics = AbstractEvaluationMetric.getPluginMetrics();
/*  60:110 */     if (pluginMetrics != null) {
/*  61:111 */       for (AbstractEvaluationMetric m : pluginMetrics)
/*  62:    */       {
/*  63:112 */         List<String> statNames = m.getStatisticNames();
/*  64:113 */         for (String s : statNames) {
/*  65:114 */           pluginNames.add(s.toLowerCase());
/*  66:    */         }
/*  67:    */       }
/*  68:    */     }
/*  69:119 */     return pluginNames;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public static List<String> getAllMetricNames()
/*  73:    */   {
/*  74:128 */     List<String> metrics = getBuiltInMetricNames();
/*  75:129 */     metrics.addAll(getPluginMetricNames());
/*  76:    */     
/*  77:131 */     return metrics;
/*  78:    */   }
/*  79:    */   
/*  80:    */   protected boolean builtInMetricIsMaximisable(int metricIndex)
/*  81:    */     throws Exception
/*  82:    */   {
/*  83:143 */     switch (metricIndex)
/*  84:    */     {
/*  85:    */     case 0: 
/*  86:146 */       return true;
/*  87:    */     case 1: 
/*  88:149 */       return false;
/*  89:    */     case 2: 
/*  90:152 */       return true;
/*  91:    */     case 3: 
/*  92:155 */       return false;
/*  93:    */     case 4: 
/*  94:158 */       return false;
/*  95:    */     case 5: 
/*  96:161 */       return false;
/*  97:    */     case 6: 
/*  98:164 */       return false;
/*  99:    */     case 7: 
/* 100:167 */       return true;
/* 101:    */     case 8: 
/* 102:170 */       return false;
/* 103:    */     case 9: 
/* 104:173 */       return false;
/* 105:    */     case 10: 
/* 106:176 */       return true;
/* 107:    */     case 11: 
/* 108:179 */       return false;
/* 109:    */     case 12: 
/* 110:182 */       return false;
/* 111:    */     case 13: 
/* 112:185 */       return false;
/* 113:    */     case 14: 
/* 114:188 */       return false;
/* 115:    */     case 15: 
/* 116:191 */       return true;
/* 117:    */     case 16: 
/* 118:194 */       return false;
/* 119:    */     case 17: 
/* 120:197 */       return true;
/* 121:    */     case 18: 
/* 122:200 */       return false;
/* 123:    */     case 19: 
/* 124:203 */       return true;
/* 125:    */     case 20: 
/* 126:206 */       return true;
/* 127:    */     case 21: 
/* 128:209 */       return true;
/* 129:    */     case 22: 
/* 130:212 */       return true;
/* 131:    */     case 23: 
/* 132:215 */       return true;
/* 133:    */     case 24: 
/* 134:218 */       return true;
/* 135:    */     }
/* 136:221 */     throw new Exception("Unknown built-in metric");
/* 137:    */   }
/* 138:    */   
/* 139:    */   protected double getBuiltinMetricValue(int metricIndex, int... classValIndex)
/* 140:    */     throws Exception
/* 141:    */   {
/* 142:235 */     boolean hasValIndex = (classValIndex != null) && (classValIndex.length == 1);
/* 143:237 */     switch (metricIndex)
/* 144:    */     {
/* 145:    */     case 0: 
/* 146:239 */       return this.m_eval.correct();
/* 147:    */     case 1: 
/* 148:241 */       return this.m_eval.incorrect();
/* 149:    */     case 2: 
/* 150:243 */       return this.m_eval.kappa();
/* 151:    */     case 3: 
/* 152:245 */       return this.m_eval.totalCost();
/* 153:    */     case 4: 
/* 154:247 */       return this.m_eval.avgCost();
/* 155:    */     case 5: 
/* 156:249 */       return this.m_eval.KBRelativeInformation();
/* 157:    */     case 6: 
/* 158:251 */       return this.m_eval.KBInformation();
/* 159:    */     case 7: 
/* 160:253 */       return this.m_eval.correlationCoefficient();
/* 161:    */     case 8: 
/* 162:255 */       return this.m_eval.SFPriorEntropy();
/* 163:    */     case 9: 
/* 164:257 */       return this.m_eval.SFSchemeEntropy();
/* 165:    */     case 10: 
/* 166:259 */       return this.m_eval.SFEntropyGain();
/* 167:    */     case 11: 
/* 168:261 */       return this.m_eval.meanAbsoluteError();
/* 169:    */     case 12: 
/* 170:263 */       return this.m_eval.rootMeanSquaredError();
/* 171:    */     case 13: 
/* 172:265 */       return this.m_eval.relativeAbsoluteError();
/* 173:    */     case 14: 
/* 174:267 */       return this.m_eval.rootRelativeSquaredError();
/* 175:    */     case 15: 
/* 176:269 */       return this.m_eval.coverageOfTestCasesByPredictedRegions();
/* 177:    */     case 16: 
/* 178:271 */       return this.m_eval.sizeOfPredictedRegions();
/* 179:    */     case 17: 
/* 180:273 */       return hasValIndex ? this.m_eval.truePositiveRate(classValIndex[0]) : this.m_eval.weightedTruePositiveRate();
/* 181:    */     case 18: 
/* 182:276 */       return hasValIndex ? this.m_eval.falsePositiveRate(classValIndex[0]) : this.m_eval.weightedFalsePositiveRate();
/* 183:    */     case 19: 
/* 184:279 */       return hasValIndex ? this.m_eval.precision(classValIndex[0]) : this.m_eval.weightedPrecision();
/* 185:    */     case 20: 
/* 186:282 */       return hasValIndex ? this.m_eval.recall(classValIndex[0]) : this.m_eval.weightedRecall();
/* 187:    */     case 21: 
/* 188:285 */       return hasValIndex ? this.m_eval.fMeasure(classValIndex[0]) : this.m_eval.weightedFMeasure();
/* 189:    */     case 22: 
/* 190:288 */       return hasValIndex ? this.m_eval.matthewsCorrelationCoefficient(classValIndex[0]) : this.m_eval.weightedMatthewsCorrelation();
/* 191:    */     case 23: 
/* 192:292 */       return hasValIndex ? this.m_eval.areaUnderROC(classValIndex[0]) : this.m_eval.weightedAreaUnderROC();
/* 193:    */     case 24: 
/* 194:295 */       return hasValIndex ? this.m_eval.areaUnderPRC(classValIndex[0]) : this.m_eval.weightedAreaUnderPRC();
/* 195:    */     }
/* 196:299 */     throw new Exception("Unknown built-in metric");
/* 197:    */   }
/* 198:    */   
/* 199:    */   protected double getPluginMetricValue(AbstractEvaluationMetric m, String statName, int... classValIndex)
/* 200:    */     throws Exception
/* 201:    */   {
/* 202:314 */     boolean hasValIndex = (classValIndex != null) && (classValIndex.length == 1);
/* 203:316 */     if ((m instanceof InformationRetrievalEvaluationMetric)) {
/* 204:317 */       return hasValIndex ? ((InformationRetrievalEvaluationMetric)m).getStatistic(statName, classValIndex[0]) : ((InformationRetrievalEvaluationMetric)m).getClassWeightedAverageStatistic(statName);
/* 205:    */     }
/* 206:323 */     return m.getStatistic(statName);
/* 207:    */   }
/* 208:    */   
/* 209:    */   protected boolean pluginMetricIsMaximisable(AbstractEvaluationMetric m, String statName)
/* 210:    */   {
/* 211:335 */     return m.statisticIsMaximisable(statName);
/* 212:    */   }
/* 213:    */   
/* 214:    */   public double getNamedMetric(String statName, int... classValIndex)
/* 215:    */     throws Exception
/* 216:    */   {
/* 217:351 */     if ((classValIndex != null) && (classValIndex.length > 1)) {
/* 218:352 */       throw new IllegalArgumentException("Only one class value index should be supplied");
/* 219:    */     }
/* 220:356 */     Integer builtinIndex = (Integer)this.m_builtin.get(statName.toLowerCase());
/* 221:357 */     if (builtinIndex != null) {
/* 222:358 */       return getBuiltinMetricValue(builtinIndex.intValue(), classValIndex);
/* 223:    */     }
/* 224:360 */     AbstractEvaluationMetric m = (AbstractEvaluationMetric)this.m_pluginMetrics.get(statName.toLowerCase());
/* 225:361 */     if (m == null) {
/* 226:362 */       throw new Exception("Unknown evaluation metric: " + statName);
/* 227:    */     }
/* 228:364 */     return getPluginMetricValue(m, statName, classValIndex);
/* 229:    */   }
/* 230:    */   
/* 231:    */   public double[] getNamedMetricThresholds(String statName)
/* 232:    */     throws Exception
/* 233:    */   {
/* 234:379 */     Integer builtinIndex = (Integer)this.m_builtin.get(statName.toLowerCase());
/* 235:380 */     if (builtinIndex != null) {
/* 236:381 */       return null;
/* 237:    */     }
/* 238:383 */     AbstractEvaluationMetric m = (AbstractEvaluationMetric)this.m_pluginMetrics.get(statName.toLowerCase());
/* 239:384 */     if (m == null) {
/* 240:385 */       throw new Exception("Unknown evaluation metric: " + statName);
/* 241:    */     }
/* 242:387 */     if ((m instanceof ThresholdProducingMetric)) {
/* 243:388 */       return ((ThresholdProducingMetric)m).getThresholds();
/* 244:    */     }
/* 245:390 */     return null;
/* 246:    */   }
/* 247:    */   
/* 248:    */   public boolean metricIsMaximisable(String statName)
/* 249:    */     throws Exception
/* 250:    */   {
/* 251:403 */     Integer builtinIndex = (Integer)this.m_builtin.get(statName.toLowerCase());
/* 252:404 */     if (builtinIndex != null) {
/* 253:405 */       return builtInMetricIsMaximisable(builtinIndex.intValue());
/* 254:    */     }
/* 255:407 */     AbstractEvaluationMetric m = (AbstractEvaluationMetric)this.m_pluginMetrics.get(statName.toLowerCase());
/* 256:408 */     if (m == null) {
/* 257:409 */       throw new Exception("Unknown evaluation metric: " + statName);
/* 258:    */     }
/* 259:411 */     return pluginMetricIsMaximisable(m, statName);
/* 260:    */   }
/* 261:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.evaluation.EvaluationMetricHelper
 * JD-Core Version:    0.7.0.1
 */