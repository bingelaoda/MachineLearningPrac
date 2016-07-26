/*   1:    */ package weka.clusterers;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.List;
/*   5:    */ import weka.core.BatchPredictor;
/*   6:    */ import weka.core.Capabilities;
/*   7:    */ import weka.core.Capabilities.Capability;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.OptionMetadata;
/*  11:    */ import weka.core.SelectedTag;
/*  12:    */ import weka.core.Tag;
/*  13:    */ import weka.core.WekaException;
/*  14:    */ import weka.filters.Filter;
/*  15:    */ import weka.filters.unsupervised.attribute.NominalToBinary;
/*  16:    */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*  17:    */ import weka.python.PythonSession;
/*  18:    */ 
/*  19:    */ public class ScikitLearnClusterer
/*  20:    */   extends AbstractClusterer
/*  21:    */   implements BatchPredictor
/*  22:    */ {
/*  23:    */   protected static final String TRAINING_DATA_ID = "scikit_clusterer_training";
/*  24:    */   protected static final String TEST_DATA_ID = "scikit_clusterer_test";
/*  25:    */   protected static final String MODEL_ID = "weka_scikit_clusterer";
/*  26:    */   private static final long serialVersionUID = -1292576437716874848L;
/*  27:    */   
/*  28:    */   public static enum Clusterer
/*  29:    */   {
/*  30: 57 */     AffinityPropagation("cluster", "\taffinity='euclidean', convergence_iter=15, copy=True,\n\tdamping=0.5, max_iter=200, preference=None, verbose=False", true),  AgglomerativeClustering("cluster", "\taffinity='euclidean', compute_full_tree='auto',\n\tconnectivity=None, linkage='ward',\n\tmemory=Memory(cachedir=None), n_clusters=2, n_components=None,\n\tpooling_func=<function mean at 0x10c4dc6e0>", false),  Birch("cluster", "\tbranching_factor=50, compute_labels=True, copy=True, n_clusters=3,\n\tthreshold=0.5", true),  DBSCAN("cluster", "\talgorithm='auto', eps=0.5, leaf_size=30, metric='euclidean',\n\tmin_samples=5, p=None, random_state=None", false),  KMeans("\tcluster", "copy_x=True, init='k-means++', max_iter=300, n_clusters=8, n_init=10,\n\tn_jobs=1, precompute_distances='auto', random_state=None, tol=0.0001,\n\tverbose=0", true),  MiniBatchKMeans("cluster", "\tbatch_size=100, compute_labels=True, init='k-means++',\n\tinit_size=None, max_iter=100, max_no_improvement=10, n_clusters=8,\n\tn_init=3, random_state=None, reassignment_ratio=0.01, tol=0.0,\n\tverbose=0", true),  MeanShift("cluster", "\tbandwidth=None, bin_seeding=False, cluster_all=True, min_bin_freq=1,\n\tseeds=None", true),  SpectralClustering("cluster", "\taffinity='rbf', assign_labels='kmeans', coef0=1, degree=3,\n\teigen_solver=None, eigen_tol=0.0, gamma=1.0, kernel_params=None,\n\tn_clusters=8, n_init=10, n_neighbors=10, random_state=None", false),  Ward("cluster", "\tcompute_full_tree='auto', connectivity=None,\n\tmemory=Memory(cachedir=None), n_clusters=2, n_components=None,\n\tpooling_func=<function mean at 0x10130d6e0>", false);
/*  31:    */     
/*  32:    */     private String m_defaultParameters;
/*  33:    */     private String m_module;
/*  34:    */     private boolean m_canClusterNewData;
/*  35:    */     
/*  36:    */     private Clusterer(String module, String defaultParams, boolean canClusterNewData)
/*  37:    */     {
/*  38:100 */       this.m_module = module;
/*  39:101 */       this.m_defaultParameters = defaultParams;
/*  40:102 */       this.m_canClusterNewData = canClusterNewData;
/*  41:    */     }
/*  42:    */     
/*  43:    */     public String getModule()
/*  44:    */     {
/*  45:106 */       return this.m_module;
/*  46:    */     }
/*  47:    */     
/*  48:    */     public String getDefaultParameters()
/*  49:    */     {
/*  50:110 */       return this.m_defaultParameters;
/*  51:    */     }
/*  52:    */     
/*  53:    */     public boolean canClusterNewData()
/*  54:    */     {
/*  55:114 */       return this.m_canClusterNewData;
/*  56:    */     }
/*  57:    */   }
/*  58:    */   
/*  59:119 */   public static final Tag[] TAGS_LEARNER = new Tag[Clusterer.values().length];
/*  60:    */   
/*  61:    */   static
/*  62:    */   {
/*  63:122 */     for (Clusterer l : Clusterer.values()) {
/*  64:123 */       TAGS_LEARNER[l.ordinal()] = new Tag(l.ordinal(), l.toString());
/*  65:    */     }
/*  66:    */   }
/*  67:    */   
/*  68:128 */   protected Clusterer m_clusterer = Clusterer.KMeans;
/*  69:131 */   protected String m_learnerOpts = "";
/*  70:134 */   protected Filter m_nominalToBinary = new NominalToBinary();
/*  71:138 */   protected Filter m_replaceMissing = new ReplaceMissingValues();
/*  72:    */   protected String m_pickledModel;
/*  73:145 */   protected String m_learnerToString = "";
/*  74:    */   protected String m_modelHash;
/*  75:153 */   protected String m_batchPredictSize = "100";
/*  76:    */   protected boolean m_continueOnSysErr;
/*  77:165 */   protected int m_numberOfClustersLearned = -1;
/*  78:    */   protected double[][] m_trainingPreds;
/*  79:175 */   protected int m_minClusterNum = 0;
/*  80:    */   
/*  81:    */   public String globalInfo()
/*  82:    */   {
/*  83:183 */     StringBuilder b = new StringBuilder();
/*  84:184 */     b.append("A wrapper for clusterers implemented in the scikit-learn python library. The following learners are available:\n\n");
/*  85:186 */     for (Clusterer l : Clusterer.values())
/*  86:    */     {
/*  87:187 */       b.append(l.toString()).append("\n");
/*  88:188 */       b.append("\nDefault parameters:\n");
/*  89:189 */       b.append(l.getDefaultParameters()).append("\n");
/*  90:    */     }
/*  91:191 */     return b.toString();
/*  92:    */   }
/*  93:    */   
/*  94:    */   public Capabilities getCapabilities()
/*  95:    */   {
/*  96:201 */     Capabilities result = super.getCapabilities();
/*  97:202 */     result.disableAll();
/*  98:    */     
/*  99:204 */     boolean pythonAvailable = true;
/* 100:205 */     if (!PythonSession.pythonAvailable()) {
/* 101:    */       try
/* 102:    */       {
/* 103:208 */         if (!PythonSession.initSession("python", getDebug())) {
/* 104:209 */           pythonAvailable = false;
/* 105:    */         }
/* 106:    */       }
/* 107:    */       catch (WekaException ex)
/* 108:    */       {
/* 109:212 */         pythonAvailable = false;
/* 110:    */       }
/* 111:    */     }
/* 112:216 */     if (pythonAvailable)
/* 113:    */     {
/* 114:217 */       result.enable(Capabilities.Capability.NO_CLASS);
/* 115:    */       
/* 116:    */ 
/* 117:220 */       result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 118:221 */       result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 119:222 */       result.enable(Capabilities.Capability.MISSING_VALUES);
/* 120:    */     }
/* 121:225 */     return result;
/* 122:    */   }
/* 123:    */   
/* 124:    */   @OptionMetadata(displayName="Scikit-learn clusterer", description="Scikit-learn clusterer to use.\nAvailable clusterers:\nAffinityPropagation, KMeans, DBSCAN", commandLineParamName="clusterer", commandLineParamSynopsis="-clusterer <clusterer name>", displayOrder=1)
/* 125:    */   public SelectedTag getClusterer()
/* 126:    */   {
/* 127:234 */     return new SelectedTag(this.m_clusterer.ordinal(), TAGS_LEARNER);
/* 128:    */   }
/* 129:    */   
/* 130:    */   public void setClusterer(SelectedTag learner)
/* 131:    */   {
/* 132:243 */     int learnerID = learner.getSelectedTag().getID();
/* 133:244 */     for (Clusterer c : Clusterer.values()) {
/* 134:245 */       if (c.ordinal() == learnerID)
/* 135:    */       {
/* 136:246 */         this.m_clusterer = c;
/* 137:247 */         break;
/* 138:    */       }
/* 139:    */     }
/* 140:    */   }
/* 141:    */   
/* 142:    */   @OptionMetadata(displayName="Learner parameters", description="learner parameters to use", displayOrder=2, commandLineParamName="parameters", commandLineParamSynopsis="-parameters <comma-separated list of name=value pairs>")
/* 143:    */   public String getLearnerOpts()
/* 144:    */   {
/* 145:265 */     return this.m_learnerOpts;
/* 146:    */   }
/* 147:    */   
/* 148:    */   public void setLearnerOpts(String opts)
/* 149:    */   {
/* 150:274 */     this.m_learnerOpts = opts;
/* 151:    */   }
/* 152:    */   
/* 153:    */   public void setBatchSize(String size)
/* 154:    */   {
/* 155:279 */     this.m_batchPredictSize = size;
/* 156:    */   }
/* 157:    */   
/* 158:    */   @OptionMetadata(displayName="Batch size", description="The preferred number of instances to transfer into python for prediction\n(if operatingin batch prediction mode). More or fewer instances than this will be accepted.", commandLineParamName="batch", commandLineParamSynopsis="-batch <batch size>", displayOrder=4)
/* 159:    */   public String getBatchSize()
/* 160:    */   {
/* 161:292 */     return this.m_batchPredictSize;
/* 162:    */   }
/* 163:    */   
/* 164:    */   public boolean implementsMoreEfficientBatchPrediction()
/* 165:    */   {
/* 166:302 */     return true;
/* 167:    */   }
/* 168:    */   
/* 169:    */   public void setContinueOnSysErr(boolean c)
/* 170:    */   {
/* 171:313 */     this.m_continueOnSysErr = c;
/* 172:    */   }
/* 173:    */   
/* 174:    */   @OptionMetadata(displayName="Try to continue after sys err output from script", description="Try to continue after sys err output from script.\nSome schemesreport warnings to the system error stream.", displayOrder=5, commandLineParamName="continue-on-err", commandLineParamSynopsis="-continue-on-err", commandLineParamIsFlag=true)
/* 175:    */   public boolean getContinueOnSysErr()
/* 176:    */   {
/* 177:332 */     return this.m_continueOnSysErr;
/* 178:    */   }
/* 179:    */   
/* 180:    */   public void buildClusterer(Instances data)
/* 181:    */     throws Exception
/* 182:    */   {
/* 183:344 */     getCapabilities().testWithFail(data);
/* 184:346 */     if (!PythonSession.pythonAvailable()) {
/* 185:348 */       if (!PythonSession.initSession("python", getDebug()))
/* 186:    */       {
/* 187:349 */         String envEvalResults = PythonSession.getPythonEnvCheckResults();
/* 188:350 */         throw new WekaException("Was unable to start python environment: " + envEvalResults);
/* 189:    */       }
/* 190:    */     }
/* 191:355 */     if (this.m_modelHash == null) {
/* 192:356 */       this.m_modelHash = ("" + hashCode());
/* 193:    */     }
/* 194:359 */     data = new Instances(data);
/* 195:    */     
/* 196:361 */     this.m_replaceMissing.setInputFormat(data);
/* 197:362 */     data = Filter.useFilter(data, this.m_replaceMissing);
/* 198:363 */     this.m_nominalToBinary.setInputFormat(data);
/* 199:364 */     data = Filter.useFilter(data, this.m_nominalToBinary);
/* 200:    */     try
/* 201:    */     {
/* 202:367 */       PythonSession session = PythonSession.acquireSession(this);
/* 203:    */       
/* 204:369 */       session.instancesToPythonAsScikitLearn(data, "scikit_clusterer_training", getDebug());
/* 205:    */       
/* 206:    */ 
/* 207:    */ 
/* 208:    */ 
/* 209:    */ 
/* 210:375 */       StringBuilder learnScript = new StringBuilder();
/* 211:376 */       learnScript.append("from sklearn import *").append("\n");
/* 212:377 */       learnScript.append("weka_scikit_clusterer" + this.m_modelHash + " = " + this.m_clusterer.getModule() + "." + this.m_clusterer.toString() + "(" + (getLearnerOpts().length() > 0 ? getLearnerOpts() : "") + ")").append("\n");
/* 213:    */       
/* 214:    */ 
/* 215:    */ 
/* 216:    */ 
/* 217:382 */       learnScript.append("preds = weka_scikit_clusterer" + this.m_modelHash + ".fit_predict(X)\n").append("preds = preds.tolist()\n").append("\np_set = set(preds)\n").append("unique_clusters = list(p_set)\n");
/* 218:    */       
/* 219:    */ 
/* 220:    */ 
/* 221:    */ 
/* 222:387 */       List<String> outAndErr = session.executeScript(learnScript.toString(), getDebug());
/* 223:389 */       if ((outAndErr.size() == 2) && (((String)outAndErr.get(1)).length() > 0)) {
/* 224:390 */         if (this.m_continueOnSysErr) {
/* 225:391 */           System.err.println((String)outAndErr.get(1));
/* 226:    */         } else {
/* 227:393 */           throw new WekaException((String)outAndErr.get(1));
/* 228:    */         }
/* 229:    */       }
/* 230:397 */       this.m_learnerToString = (session.getVariableValueFromPythonAsPlainString(new StringBuilder().append("weka_scikit_clusterer").append(this.m_modelHash).toString(), getDebug()) + "\n\n");
/* 231:    */       
/* 232:    */ 
/* 233:    */ 
/* 234:    */ 
/* 235:402 */       this.m_pickledModel = session.getVariableValueFromPythonAsPickledObject("weka_scikit_clusterer" + this.m_modelHash, getDebug());
/* 236:    */       
/* 237:    */ 
/* 238:    */ 
/* 239:    */ 
/* 240:407 */       List<Object> uniqueClusters = (List)session.getVariableValueFromPythonAsJson("unique_clusters", getDebug());
/* 241:    */       
/* 242:    */ 
/* 243:410 */       this.m_minClusterNum = 2147483647;
/* 244:411 */       for (Object o : uniqueClusters) {
/* 245:412 */         if (((Number)o).intValue() < this.m_minClusterNum) {
/* 246:413 */           this.m_minClusterNum = ((Number)o).intValue();
/* 247:    */         }
/* 248:    */       }
/* 249:417 */       if (uniqueClusters == null) {
/* 250:418 */         throw new Exception("Unable to determine the number of clusters learned!");
/* 251:    */       }
/* 252:422 */       this.m_numberOfClustersLearned = uniqueClusters.size();
/* 253:423 */       if (!this.m_clusterer.canClusterNewData())
/* 254:    */       {
/* 255:425 */         List<Object> trainingPreds = (List)session.getVariableValueFromPythonAsJson("preds", getDebug());
/* 256:428 */         if (trainingPreds == null) {
/* 257:429 */           throw new WekaException("Was unable to get predictions for the training data");
/* 258:    */         }
/* 259:432 */         if (trainingPreds.size() != data.numInstances()) {
/* 260:433 */           throw new WekaException("The number of predictions obtained does not match the number of training instances!");
/* 261:    */         }
/* 262:438 */         this.m_trainingPreds = new double[data.numInstances()][this.m_numberOfClustersLearned];
/* 263:    */         
/* 264:440 */         j = 0;
/* 265:441 */         for (Object o : trainingPreds)
/* 266:    */         {
/* 267:442 */           Number p = (Number)o;
/* 268:443 */           this.m_trainingPreds[(j++)][(p.intValue() - this.m_minClusterNum)] = 1.0D;
/* 269:    */         }
/* 270:    */       }
/* 271:    */     }
/* 272:    */     finally
/* 273:    */     {
/* 274:    */       int j;
/* 275:448 */       PythonSession.releaseSession(this);
/* 276:    */     }
/* 277:    */   }
/* 278:    */   
/* 279:    */   public int numberOfClusters()
/* 280:    */     throws Exception
/* 281:    */   {
/* 282:454 */     return this.m_numberOfClustersLearned;
/* 283:    */   }
/* 284:    */   
/* 285:    */   public double[][] distributionsForInstances(Instances insts)
/* 286:    */     throws Exception
/* 287:    */   {
/* 288:468 */     if (this.m_trainingPreds != null)
/* 289:    */     {
/* 290:470 */       if (insts.numInstances() != this.m_trainingPreds.length) {
/* 291:471 */         throw new WekaException("This scikit-learn clusterer cannot produce predictions for new data. We can only return predictions that were stored for the training data (and the supplied test set does not seem to match the training data)");
/* 292:    */       }
/* 293:477 */       return this.m_trainingPreds;
/* 294:    */     }
/* 295:480 */     if (!PythonSession.pythonAvailable()) {
/* 296:482 */       if (!PythonSession.initSession("python", getDebug()))
/* 297:    */       {
/* 298:483 */         String envEvalResults = PythonSession.getPythonEnvCheckResults();
/* 299:484 */         throw new Exception("Was unable to start python environment: " + envEvalResults);
/* 300:    */       }
/* 301:    */     }
/* 302:488 */     insts = Filter.useFilter(insts, this.m_replaceMissing);
/* 303:489 */     insts = Filter.useFilter(insts, this.m_nominalToBinary);
/* 304:    */     try
/* 305:    */     {
/* 306:492 */       PythonSession session = PythonSession.acquireSession(this);
/* 307:493 */       session.instancesToPythonAsScikitLearn(insts, "scikit_clusterer_test", getDebug());
/* 308:494 */       StringBuilder predictScript = new StringBuilder();
/* 309:497 */       if (!session.checkIfPythonVariableIsSet("weka_scikit_clusterer" + this.m_modelHash, getDebug()))
/* 310:    */       {
/* 311:499 */         if ((this.m_pickledModel == null) || (this.m_pickledModel.length() == 0)) {
/* 312:500 */           throw new Exception("There is no model to transfer into Python!");
/* 313:    */         }
/* 314:502 */         session.setPythonPickledVariableValue("weka_scikit_clusterer" + this.m_modelHash, this.m_pickledModel, getDebug());
/* 315:    */       }
/* 316:506 */       predictScript.append("from sklearn import *").append("\n");
/* 317:507 */       predictScript.append("preds = weka_scikit_clusterer" + this.m_modelHash + ".predict(X)").append("\npreds = preds.tolist()\n");
/* 318:    */       
/* 319:    */ 
/* 320:510 */       List<String> outAndErr = session.executeScript(predictScript.toString(), getDebug());
/* 321:512 */       if ((outAndErr.size() == 2) && (((String)outAndErr.get(1)).length() > 0)) {
/* 322:513 */         if (this.m_continueOnSysErr) {
/* 323:514 */           System.err.println((String)outAndErr.get(1));
/* 324:    */         } else {
/* 325:516 */           throw new Exception((String)outAndErr.get(1));
/* 326:    */         }
/* 327:    */       }
/* 328:520 */       List<Object> preds = (List)session.getVariableValueFromPythonAsJson("preds", getDebug());
/* 329:523 */       if (preds == null) {
/* 330:524 */         throw new Exception("Was unable to retrieve predictions from python");
/* 331:    */       }
/* 332:527 */       if (preds.size() != insts.numInstances()) {
/* 333:528 */         throw new Exception("Learner did not return as many predictions as there are test instances");
/* 334:    */       }
/* 335:533 */       double[][] dists = new double[insts.numInstances()][this.m_numberOfClustersLearned];
/* 336:    */       
/* 337:535 */       int j = 0;
/* 338:536 */       for (Object o : preds)
/* 339:    */       {
/* 340:537 */         Number p = (Number)o;
/* 341:538 */         dists[(j++)][(p.intValue() - this.m_minClusterNum)] = 1.0D;
/* 342:    */       }
/* 343:540 */       return dists;
/* 344:    */     }
/* 345:    */     finally
/* 346:    */     {
/* 347:542 */       PythonSession.releaseSession(this);
/* 348:    */     }
/* 349:    */   }
/* 350:    */   
/* 351:    */   public double[] distributionForInstance(Instance inst)
/* 352:    */     throws Exception
/* 353:    */   {
/* 354:555 */     if (this.m_trainingPreds != null) {
/* 355:556 */       throw new WekaException("distributionForInstance() can only be used with scikit-learn clusterers that support predicting new data");
/* 356:    */     }
/* 357:561 */     Instances temp = new Instances(inst.dataset(), 0);
/* 358:562 */     temp.add(inst);
/* 359:    */     
/* 360:564 */     return distributionsForInstances(temp)[0];
/* 361:    */   }
/* 362:    */   
/* 363:    */   public String toString()
/* 364:    */   {
/* 365:573 */     if ((this.m_learnerToString == null) || (this.m_learnerToString.length() == 0)) {
/* 366:574 */       return "ScikitLearnClusterer: model not built yet!";
/* 367:    */     }
/* 368:577 */     return this.m_learnerToString;
/* 369:    */   }
/* 370:    */   
/* 371:    */   public static void main(String[] args)
/* 372:    */   {
/* 373:586 */     runClusterer(new ScikitLearnClusterer(), args);
/* 374:    */   }
/* 375:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.clusterers.ScikitLearnClusterer
 * JD-Core Version:    0.7.0.1
 */