/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.LinkedList;
/*   6:    */ import java.util.List;
/*   7:    */ import weka.classifiers.Classifier;
/*   8:    */ import weka.classifiers.evaluation.Evaluation;
/*   9:    */ import weka.core.Attribute;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.WekaException;
/*  13:    */ import weka.knowledgeflow.Data;
/*  14:    */ import weka.knowledgeflow.StepManager;
/*  15:    */ 
/*  16:    */ @KFStep(name="IncrementalClassifierEvaluator", category="Evaluation", toolTipText="Evaluate the performance of incrementally training classifiers", iconPath="weka/gui/knowledgeflow/icons/IncrementalClassifierEvaluator.gif")
/*  17:    */ public class IncrementalClassifierEvaluator
/*  18:    */   extends BaseStep
/*  19:    */ {
/*  20:    */   private static final long serialVersionUID = -5951569492213633100L;
/*  21:    */   protected List<String> m_dataLegend;
/*  22:    */   protected double[] m_dataPoint;
/*  23: 59 */   protected Data m_chartData = new Data("chart");
/*  24: 61 */   protected double m_min = 1.7976931348623157E+308D;
/*  25: 62 */   protected double m_max = 4.9E-324D;
/*  26: 65 */   protected int m_statusFrequency = 2000;
/*  27:    */   protected int m_instanceCount;
/*  28:    */   protected boolean m_outputInfoRetrievalStats;
/*  29:    */   protected Evaluation m_eval;
/*  30:    */   protected int m_windowSize;
/*  31:    */   protected Evaluation m_windowEval;
/*  32:    */   protected LinkedList<Instance> m_window;
/*  33:    */   protected LinkedList<double[]> m_windowedPreds;
/*  34:    */   protected boolean m_reset;
/*  35:    */   protected String m_classifierName;
/*  36:    */   
/*  37:    */   public void stepInit()
/*  38:    */     throws WekaException
/*  39:    */   {
/*  40:104 */     this.m_instanceCount = 0;
/*  41:105 */     this.m_dataPoint = new double[1];
/*  42:106 */     this.m_dataLegend = new ArrayList();
/*  43:107 */     if (this.m_windowSize > 0)
/*  44:    */     {
/*  45:108 */       this.m_window = new LinkedList();
/*  46:109 */       this.m_windowedPreds = new LinkedList();
/*  47:110 */       getStepManager().logBasic("Chart output using windowed evaluation over " + this.m_windowSize + " instances");
/*  48:    */     }
/*  49:114 */     this.m_reset = true;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public List<String> getIncomingConnectionTypes()
/*  53:    */   {
/*  54:128 */     if (getStepManager().numIncomingConnections() == 0) {
/*  55:129 */       return Arrays.asList(new String[] { "incrementalClassifier" });
/*  56:    */     }
/*  57:132 */     return new ArrayList();
/*  58:    */   }
/*  59:    */   
/*  60:    */   public List<String> getOutgoingConnectionTypes()
/*  61:    */   {
/*  62:146 */     List<String> result = new ArrayList();
/*  63:147 */     if (getStepManager().numIncomingConnectionsOfType("incrementalClassifier") > 0)
/*  64:    */     {
/*  65:149 */       result.add("text");
/*  66:150 */       result.add("chart");
/*  67:    */     }
/*  68:153 */     return result;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void processIncoming(Data data)
/*  72:    */     throws WekaException
/*  73:    */   {
/*  74:164 */     if (isStopRequested()) {
/*  75:165 */       return;
/*  76:    */     }
/*  77:168 */     if (getStepManager().isStreamFinished(data))
/*  78:    */     {
/*  79:171 */       Data d = new Data("chart");
/*  80:172 */       getStepManager().throughputFinished(new Data[] { d });
/*  81:    */       
/*  82:    */ 
/*  83:175 */       this.m_windowEval = null;
/*  84:176 */       this.m_window = null;
/*  85:177 */       this.m_windowedPreds = null;
/*  86:179 */       if (getStepManager().numOutgoingConnectionsOfType("text") > 0) {
/*  87:    */         try
/*  88:    */         {
/*  89:181 */           String textTitle = this.m_classifierName;
/*  90:182 */           String results = "=== Performance information ===\n\nScheme:   " + textTitle + "\n" + "Relation: " + this.m_eval.getHeader().relationName() + "\n\n" + this.m_eval.toSummaryString();
/*  91:186 */           if ((this.m_eval.getHeader().classIndex() >= 0) && (this.m_eval.getHeader().classAttribute().isNominal()) && (this.m_outputInfoRetrievalStats)) {
/*  92:189 */             results = results + "\n" + this.m_eval.toClassDetailsString();
/*  93:    */           }
/*  94:191 */           if ((this.m_eval.getHeader().classIndex() >= 0) && (this.m_eval.getHeader().classAttribute().isNominal())) {
/*  95:193 */             results = results + "\n" + this.m_eval.toMatrixString();
/*  96:    */           }
/*  97:195 */           textTitle = "Results: " + textTitle;
/*  98:196 */           Data textData = new Data("text");
/*  99:197 */           textData.setPayloadElement("text", results);
/* 100:198 */           textData.setPayloadElement("aux_textTitle", textTitle);
/* 101:    */           
/* 102:200 */           getStepManager().outputData(new Data[] { textData });
/* 103:    */         }
/* 104:    */         catch (Exception ex)
/* 105:    */         {
/* 106:202 */           throw new WekaException(ex);
/* 107:    */         }
/* 108:    */       }
/* 109:206 */       return;
/* 110:    */     }
/* 111:209 */     Classifier classifier = (Classifier)data.getPayloadElement("incrementalClassifier");
/* 112:    */     
/* 113:    */ 
/* 114:212 */     Instance instance = (Instance)data.getPayloadElement("aux_testInstance");
/* 115:    */     try
/* 116:    */     {
/* 117:216 */       if (this.m_reset)
/* 118:    */       {
/* 119:217 */         this.m_reset = false;
/* 120:218 */         this.m_classifierName = classifier.getClass().getName();
/* 121:219 */         this.m_classifierName = this.m_classifierName.substring(this.m_classifierName.lastIndexOf(".") + 1, this.m_classifierName.length());
/* 122:    */         
/* 123:    */ 
/* 124:222 */         this.m_eval = new Evaluation(instance.dataset());
/* 125:223 */         this.m_eval.useNoPriors();
/* 126:224 */         if (this.m_windowSize > 0)
/* 127:    */         {
/* 128:225 */           this.m_windowEval = new Evaluation(instance.dataset());
/* 129:226 */           this.m_windowEval.useNoPriors();
/* 130:    */         }
/* 131:229 */         if (instance.classAttribute().isNominal())
/* 132:    */         {
/* 133:230 */           if (!instance.classIsMissing())
/* 134:    */           {
/* 135:231 */             this.m_dataPoint = new double[3];
/* 136:232 */             this.m_dataLegend.add("Accuracy");
/* 137:233 */             this.m_dataLegend.add("RMSE (prob)");
/* 138:234 */             this.m_dataLegend.add("Kappa");
/* 139:    */           }
/* 140:    */           else
/* 141:    */           {
/* 142:236 */             this.m_dataPoint = new double[1];
/* 143:237 */             this.m_dataLegend.add("Confidence");
/* 144:    */           }
/* 145:    */         }
/* 146:    */         else
/* 147:    */         {
/* 148:240 */           this.m_dataPoint = new double[1];
/* 149:241 */           if (instance.classIsMissing()) {
/* 150:242 */             this.m_dataLegend.add("Prediction");
/* 151:    */           } else {
/* 152:244 */             this.m_dataLegend.add("RMSE");
/* 153:    */           }
/* 154:    */         }
/* 155:    */       }
/* 156:249 */       getStepManager().throughputUpdateStart();
/* 157:250 */       this.m_instanceCount += 1;
/* 158:251 */       double[] dist = classifier.distributionForInstance(instance);
/* 159:252 */       double pred = 0.0D;
/* 160:253 */       if (!instance.classIsMissing())
/* 161:    */       {
/* 162:254 */         if (this.m_outputInfoRetrievalStats) {
/* 163:255 */           this.m_eval.evaluateModelOnceAndRecordPrediction(dist, instance);
/* 164:    */         } else {
/* 165:257 */           this.m_eval.evaluateModelOnce(dist, instance);
/* 166:    */         }
/* 167:260 */         if (this.m_windowSize > 0)
/* 168:    */         {
/* 169:261 */           this.m_windowEval.evaluateModelOnce(dist, instance);
/* 170:262 */           this.m_window.addFirst(instance);
/* 171:263 */           this.m_windowedPreds.addFirst(dist);
/* 172:265 */           if (this.m_instanceCount > this.m_windowSize)
/* 173:    */           {
/* 174:267 */             Instance oldest = (Instance)this.m_window.removeLast();
/* 175:268 */             double[] oldDist = (double[])this.m_windowedPreds.removeLast();
/* 176:    */             
/* 177:270 */             oldest.setWeight(-oldest.weight());
/* 178:271 */             this.m_windowEval.evaluateModelOnce(oldDist, oldest);
/* 179:272 */             oldest.setWeight(-oldest.weight());
/* 180:    */           }
/* 181:    */         }
/* 182:    */       }
/* 183:    */       else
/* 184:    */       {
/* 185:276 */         pred = classifier.classifyInstance(instance);
/* 186:    */       }
/* 187:278 */       if (instance.classIndex() >= 0)
/* 188:    */       {
/* 189:280 */         if (instance.classAttribute().isNominal())
/* 190:    */         {
/* 191:281 */           if (!instance.classIsMissing()) {
/* 192:282 */             if (this.m_windowSize > 0)
/* 193:    */             {
/* 194:283 */               this.m_dataPoint[1] = this.m_windowEval.rootMeanSquaredError();
/* 195:284 */               this.m_dataPoint[2] = this.m_windowEval.kappa();
/* 196:    */             }
/* 197:    */             else
/* 198:    */             {
/* 199:286 */               this.m_dataPoint[1] = this.m_eval.rootMeanSquaredError();
/* 200:287 */               this.m_dataPoint[2] = this.m_eval.kappa();
/* 201:    */             }
/* 202:    */           }
/* 203:290 */           double primaryMeasure = 0.0D;
/* 204:291 */           if (!instance.classIsMissing()) {
/* 205:292 */             primaryMeasure = this.m_windowSize > 0 ? 1.0D - this.m_windowEval.errorRate() : 1.0D - this.m_eval.errorRate();
/* 206:    */           } else {
/* 207:300 */             primaryMeasure = dist[weka.core.Utils.maxIndex(dist)];
/* 208:    */           }
/* 209:302 */           this.m_dataPoint[0] = primaryMeasure;
/* 210:303 */           this.m_chartData.setPayloadElement("chart_min", Double.valueOf(0.0D));
/* 211:    */           
/* 212:305 */           this.m_chartData.setPayloadElement("chart_max", Double.valueOf(1.0D));
/* 213:    */           
/* 214:307 */           this.m_chartData.setPayloadElement("chart_legend", this.m_dataLegend);
/* 215:    */           
/* 216:309 */           this.m_chartData.setPayloadElement("chart_data_point", this.m_dataPoint);
/* 217:    */         }
/* 218:    */         else
/* 219:    */         {
/* 220:    */           double update;
/* 221:    */           double update;
/* 222:314 */           if (!instance.classIsMissing()) {
/* 223:315 */             update = this.m_windowSize > 0 ? this.m_windowEval.rootMeanSquaredError() : this.m_eval.rootMeanSquaredError();
/* 224:    */           } else {
/* 225:319 */             update = pred;
/* 226:    */           }
/* 227:321 */           this.m_dataPoint[0] = update;
/* 228:322 */           if (update > this.m_max) {
/* 229:323 */             this.m_max = update;
/* 230:    */           }
/* 231:325 */           if (update < this.m_min) {
/* 232:326 */             this.m_min = update;
/* 233:    */           }
/* 234:328 */           this.m_chartData.setPayloadElement("chart_min", Double.valueOf(instance.classIsMissing() ? this.m_min : 0.0D));
/* 235:    */           
/* 236:330 */           this.m_chartData.setPayloadElement("chart_max", Double.valueOf(this.m_max));
/* 237:    */           
/* 238:332 */           this.m_chartData.setPayloadElement("chart_legend", this.m_dataLegend);
/* 239:    */           
/* 240:334 */           this.m_chartData.setPayloadElement("chart_data_point", this.m_dataPoint);
/* 241:    */         }
/* 242:338 */         if (isStopRequested()) {
/* 243:339 */           return;
/* 244:    */         }
/* 245:341 */         getStepManager().throughputUpdateEnd();
/* 246:342 */         getStepManager().outputData(this.m_chartData.getConnectionName(), this.m_chartData);
/* 247:    */       }
/* 248:    */     }
/* 249:    */     catch (Exception ex)
/* 250:    */     {
/* 251:346 */       throw new WekaException(ex);
/* 252:    */     }
/* 253:    */   }
/* 254:    */   
/* 255:    */   public void setStatusFrequency(int s)
/* 256:    */   {
/* 257:356 */     this.m_statusFrequency = s;
/* 258:    */   }
/* 259:    */   
/* 260:    */   public int getStatusFrequency()
/* 261:    */   {
/* 262:365 */     return this.m_statusFrequency;
/* 263:    */   }
/* 264:    */   
/* 265:    */   public String statusFrequencyTipText()
/* 266:    */   {
/* 267:374 */     return "How often to report progress to the status bar.";
/* 268:    */   }
/* 269:    */   
/* 270:    */   public void setOutputPerClassInfoRetrievalStats(boolean i)
/* 271:    */   {
/* 272:384 */     this.m_outputInfoRetrievalStats = i;
/* 273:    */   }
/* 274:    */   
/* 275:    */   public boolean getOutputPerClassInfoRetrievalStats()
/* 276:    */   {
/* 277:393 */     return this.m_outputInfoRetrievalStats;
/* 278:    */   }
/* 279:    */   
/* 280:    */   public String outputPerClassInfoRetrievalStatsTipText()
/* 281:    */   {
/* 282:402 */     return "Output per-class info retrieval stats. If set to true, predictions get stored so that stats such as AUC can be computed. Note: this consumes some memory.";
/* 283:    */   }
/* 284:    */   
/* 285:    */   public void setChartingEvalWindowSize(int windowSize)
/* 286:    */   {
/* 287:415 */     this.m_windowSize = windowSize;
/* 288:    */   }
/* 289:    */   
/* 290:    */   public int getChartingEvalWindowSize()
/* 291:    */   {
/* 292:427 */     return this.m_windowSize;
/* 293:    */   }
/* 294:    */   
/* 295:    */   public String chartingEvalWindowSizeTipText()
/* 296:    */   {
/* 297:436 */     return "For charting only, specify a sliding window size over which to compute performance stats. <= 0 means eval on whole stream";
/* 298:    */   }
/* 299:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.IncrementalClassifierEvaluator
 * JD-Core Version:    0.7.0.1
 */