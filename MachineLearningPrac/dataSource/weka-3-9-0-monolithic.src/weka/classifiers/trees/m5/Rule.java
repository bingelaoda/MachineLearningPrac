/*   1:    */ package weka.classifiers.trees.m5;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import weka.core.Attribute;
/*   5:    */ import weka.core.Instance;
/*   6:    */ import weka.core.Instances;
/*   7:    */ import weka.core.RevisionHandler;
/*   8:    */ import weka.core.RevisionUtils;
/*   9:    */ import weka.core.Utils;
/*  10:    */ 
/*  11:    */ public class Rule
/*  12:    */   implements Serializable, RevisionHandler
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = -4458627451682483204L;
/*  15: 43 */   protected static int LEFT = 0;
/*  16: 44 */   protected static int RIGHT = 1;
/*  17:    */   private Instances m_instances;
/*  18:    */   private int m_classIndex;
/*  19:    */   private int m_numInstances;
/*  20:    */   private int[] m_splitAtts;
/*  21:    */   private double[] m_splitVals;
/*  22:    */   private RuleNode[] m_internalNodes;
/*  23:    */   private int[] m_relOps;
/*  24:    */   private RuleNode m_ruleModel;
/*  25:    */   protected RuleNode m_topOfTree;
/*  26:    */   private double m_globalStdDev;
/*  27:    */   private double m_globalAbsDev;
/*  28:    */   private Instances m_covered;
/*  29:    */   private int m_numCovered;
/*  30:    */   private Instances m_notCovered;
/*  31:    */   private boolean m_useTree;
/*  32:    */   private boolean m_smoothPredictions;
/*  33:    */   private boolean m_saveInstances;
/*  34:    */   private boolean m_regressionTree;
/*  35:    */   private boolean m_useUnpruned;
/*  36:    */   private double m_minNumInstances;
/*  37:    */   
/*  38:    */   public Rule()
/*  39:    */   {
/*  40:151 */     this.m_useTree = false;
/*  41:152 */     this.m_smoothPredictions = false;
/*  42:153 */     this.m_useUnpruned = false;
/*  43:154 */     this.m_minNumInstances = 4.0D;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void buildClassifier(Instances data)
/*  47:    */     throws Exception
/*  48:    */   {
/*  49:164 */     this.m_instances = null;
/*  50:165 */     this.m_topOfTree = null;
/*  51:166 */     this.m_covered = null;
/*  52:167 */     this.m_notCovered = null;
/*  53:168 */     this.m_ruleModel = null;
/*  54:169 */     this.m_splitAtts = null;
/*  55:170 */     this.m_splitVals = null;
/*  56:171 */     this.m_relOps = null;
/*  57:172 */     this.m_internalNodes = null;
/*  58:173 */     this.m_instances = data;
/*  59:174 */     this.m_classIndex = this.m_instances.classIndex();
/*  60:175 */     this.m_numInstances = this.m_instances.numInstances();
/*  61:    */     
/*  62:    */ 
/*  63:178 */     this.m_globalStdDev = stdDev(this.m_classIndex, this.m_instances);
/*  64:179 */     this.m_globalAbsDev = absDev(this.m_classIndex, this.m_instances);
/*  65:    */     
/*  66:181 */     this.m_topOfTree = new RuleNode(this.m_globalStdDev, this.m_globalAbsDev, null);
/*  67:182 */     this.m_topOfTree.setSaveInstances(this.m_saveInstances);
/*  68:183 */     this.m_topOfTree.setRegressionTree(this.m_regressionTree);
/*  69:184 */     this.m_topOfTree.setMinNumInstances(this.m_minNumInstances);
/*  70:185 */     this.m_topOfTree.buildClassifier(this.m_instances);
/*  71:187 */     if (!this.m_useUnpruned) {
/*  72:188 */       this.m_topOfTree.prune();
/*  73:    */     } else {
/*  74:190 */       this.m_topOfTree.installLinearModels();
/*  75:    */     }
/*  76:193 */     if (this.m_smoothPredictions) {
/*  77:194 */       this.m_topOfTree.installSmoothedModels();
/*  78:    */     }
/*  79:197 */     this.m_topOfTree.numLeaves(0);
/*  80:199 */     if (!this.m_useTree) {
/*  81:200 */       makeRule();
/*  82:    */     }
/*  83:206 */     this.m_instances = new Instances(this.m_instances, 0);
/*  84:    */   }
/*  85:    */   
/*  86:    */   public double classifyInstance(Instance instance)
/*  87:    */     throws Exception
/*  88:    */   {
/*  89:218 */     if (this.m_useTree) {
/*  90:219 */       return this.m_topOfTree.classifyInstance(instance);
/*  91:    */     }
/*  92:223 */     if (this.m_splitAtts.length > 0) {
/*  93:224 */       for (int i = 0; i < this.m_relOps.length; i++) {
/*  94:225 */         if (this.m_relOps[i] == LEFT)
/*  95:    */         {
/*  96:227 */           if (instance.value(this.m_splitAtts[i]) > this.m_splitVals[i]) {
/*  97:228 */             throw new Exception("Rule does not classify instance");
/*  98:    */           }
/*  99:    */         }
/* 100:231 */         else if (instance.value(this.m_splitAtts[i]) <= this.m_splitVals[i]) {
/* 101:232 */           throw new Exception("Rule does not classify instance");
/* 102:    */         }
/* 103:    */       }
/* 104:    */     }
/* 105:239 */     return this.m_ruleModel.classifyInstance(instance);
/* 106:    */   }
/* 107:    */   
/* 108:    */   public RuleNode topOfTree()
/* 109:    */   {
/* 110:247 */     return this.m_topOfTree;
/* 111:    */   }
/* 112:    */   
/* 113:    */   private void makeRule()
/* 114:    */     throws Exception
/* 115:    */   {
/* 116:256 */     RuleNode[] best_leaf = new RuleNode[1];
/* 117:257 */     double[] best_cov = new double[1];
/* 118:    */     
/* 119:    */ 
/* 120:260 */     this.m_notCovered = new Instances(this.m_instances, 0);
/* 121:261 */     this.m_covered = new Instances(this.m_instances, 0);
/* 122:262 */     best_cov[0] = -1.0D;
/* 123:263 */     best_leaf[0] = null;
/* 124:    */     
/* 125:265 */     this.m_topOfTree.findBestLeaf(best_cov, best_leaf);
/* 126:    */     
/* 127:267 */     RuleNode temp = best_leaf[0];
/* 128:269 */     if (temp == null) {
/* 129:270 */       throw new Exception("Unable to generate rule!");
/* 130:    */     }
/* 131:274 */     this.m_ruleModel = temp;
/* 132:    */     
/* 133:276 */     int count = 0;
/* 134:278 */     while (temp.parentNode() != null)
/* 135:    */     {
/* 136:279 */       count++;
/* 137:280 */       temp = temp.parentNode();
/* 138:    */     }
/* 139:283 */     temp = best_leaf[0];
/* 140:284 */     this.m_relOps = new int[count];
/* 141:285 */     this.m_splitAtts = new int[count];
/* 142:286 */     this.m_splitVals = new double[count];
/* 143:287 */     if (this.m_smoothPredictions) {
/* 144:288 */       this.m_internalNodes = new RuleNode[count];
/* 145:    */     }
/* 146:292 */     int i = 0;
/* 147:294 */     while (temp.parentNode() != null)
/* 148:    */     {
/* 149:295 */       this.m_splitAtts[i] = temp.parentNode().splitAtt();
/* 150:296 */       this.m_splitVals[i] = temp.parentNode().splitVal();
/* 151:298 */       if (temp.parentNode().leftNode() == temp)
/* 152:    */       {
/* 153:299 */         this.m_relOps[i] = LEFT;
/* 154:300 */         temp.parentNode().m_right = null;
/* 155:    */       }
/* 156:    */       else
/* 157:    */       {
/* 158:302 */         this.m_relOps[i] = RIGHT;
/* 159:303 */         temp.parentNode().m_left = null;
/* 160:    */       }
/* 161:306 */       if (this.m_smoothPredictions) {
/* 162:307 */         this.m_internalNodes[i] = temp.parentNode();
/* 163:    */       }
/* 164:310 */       temp = temp.parentNode();
/* 165:311 */       i++;
/* 166:    */     }
/* 167:317 */     for (i = 0; i < this.m_numInstances; i++)
/* 168:    */     {
/* 169:318 */       boolean ok = true;
/* 170:320 */       for (int j = 0; j < this.m_relOps.length; j++) {
/* 171:321 */         if (this.m_relOps[j] == LEFT)
/* 172:    */         {
/* 173:322 */           if (this.m_instances.instance(i).value(this.m_splitAtts[j]) > this.m_splitVals[j])
/* 174:    */           {
/* 175:323 */             this.m_notCovered.add(this.m_instances.instance(i));
/* 176:324 */             ok = false;
/* 177:325 */             break;
/* 178:    */           }
/* 179:    */         }
/* 180:328 */         else if (this.m_instances.instance(i).value(this.m_splitAtts[j]) <= this.m_splitVals[j])
/* 181:    */         {
/* 182:329 */           this.m_notCovered.add(this.m_instances.instance(i));
/* 183:330 */           ok = false;
/* 184:331 */           break;
/* 185:    */         }
/* 186:    */       }
/* 187:336 */       if (ok) {
/* 188:337 */         this.m_numCovered += 1;
/* 189:    */       }
/* 190:    */     }
/* 191:    */   }
/* 192:    */   
/* 193:    */   public String toString()
/* 194:    */   {
/* 195:350 */     if (this.m_useTree) {
/* 196:351 */       return treeToString();
/* 197:    */     }
/* 198:353 */     return ruleToString();
/* 199:    */   }
/* 200:    */   
/* 201:    */   private String treeToString()
/* 202:    */   {
/* 203:363 */     StringBuffer text = new StringBuffer();
/* 204:365 */     if (this.m_topOfTree == null) {
/* 205:366 */       return "Tree/Rule has not been built yet!";
/* 206:    */     }
/* 207:369 */     text.append("M5 " + (this.m_useUnpruned ? "unpruned " : "pruned ") + (this.m_regressionTree ? "regression " : "model ") + "tree:\n");
/* 208:372 */     if (this.m_smoothPredictions == true) {
/* 209:373 */       text.append("(using smoothed linear models)\n");
/* 210:    */     }
/* 211:376 */     text.append(this.m_topOfTree.treeToString(0));
/* 212:377 */     text.append(this.m_topOfTree.printLeafModels());
/* 213:378 */     text.append("\nNumber of Rules : " + this.m_topOfTree.numberOfLinearModels());
/* 214:    */     
/* 215:380 */     return text.toString();
/* 216:    */   }
/* 217:    */   
/* 218:    */   private String ruleToString()
/* 219:    */   {
/* 220:389 */     StringBuffer text = new StringBuffer();
/* 221:391 */     if (this.m_splitAtts.length > 0)
/* 222:    */     {
/* 223:392 */       text.append("IF\n");
/* 224:394 */       for (int i = this.m_splitAtts.length - 1; i >= 0; i--)
/* 225:    */       {
/* 226:395 */         text.append("\t" + this.m_covered.attribute(this.m_splitAtts[i]).name() + " ");
/* 227:397 */         if (this.m_relOps[i] == 0) {
/* 228:398 */           text.append("<= ");
/* 229:    */         } else {
/* 230:400 */           text.append("> ");
/* 231:    */         }
/* 232:403 */         text.append(Utils.doubleToString(this.m_splitVals[i], 1, 3) + "\n");
/* 233:    */       }
/* 234:406 */       text.append("THEN\n");
/* 235:    */     }
/* 236:409 */     if (this.m_ruleModel != null) {
/* 237:    */       try
/* 238:    */       {
/* 239:411 */         text.append(this.m_ruleModel.printNodeLinearModel());
/* 240:412 */         text.append(" [" + this.m_numCovered);
/* 241:414 */         if (this.m_globalAbsDev > 0.0D) {
/* 242:415 */           text.append("/" + Utils.doubleToString(100.0D * this.m_ruleModel.rootMeanSquaredError() / this.m_globalStdDev, 1, 3) + "%]\n\n");
/* 243:    */         } else {
/* 244:421 */           text.append("]\n\n");
/* 245:    */         }
/* 246:    */       }
/* 247:    */       catch (Exception e)
/* 248:    */       {
/* 249:424 */         return "Can't print rule";
/* 250:    */       }
/* 251:    */     }
/* 252:429 */     return text.toString();
/* 253:    */   }
/* 254:    */   
/* 255:    */   public void setUnpruned(boolean unpruned)
/* 256:    */   {
/* 257:438 */     this.m_useUnpruned = unpruned;
/* 258:    */   }
/* 259:    */   
/* 260:    */   public boolean getUnpruned()
/* 261:    */   {
/* 262:447 */     return this.m_useUnpruned;
/* 263:    */   }
/* 264:    */   
/* 265:    */   public void setUseTree(boolean u)
/* 266:    */   {
/* 267:456 */     this.m_useTree = u;
/* 268:    */   }
/* 269:    */   
/* 270:    */   public boolean getUseTree()
/* 271:    */   {
/* 272:465 */     return this.m_useTree;
/* 273:    */   }
/* 274:    */   
/* 275:    */   public void setSmoothing(boolean s)
/* 276:    */   {
/* 277:474 */     this.m_smoothPredictions = s;
/* 278:    */   }
/* 279:    */   
/* 280:    */   public boolean getSmoothing()
/* 281:    */   {
/* 282:483 */     return this.m_smoothPredictions;
/* 283:    */   }
/* 284:    */   
/* 285:    */   public Instances notCoveredInstances()
/* 286:    */   {
/* 287:492 */     return this.m_notCovered;
/* 288:    */   }
/* 289:    */   
/* 290:    */   public void freeNotCoveredInstances()
/* 291:    */   {
/* 292:499 */     this.m_notCovered = null;
/* 293:    */   }
/* 294:    */   
/* 295:    */   protected static final double stdDev(int attr, Instances inst)
/* 296:    */   {
/* 297:519 */     int count = 0;
/* 298:520 */     double sum = 0.0D;double sqrSum = 0.0D;
/* 299:522 */     for (int i = 0; i <= inst.numInstances() - 1; i++)
/* 300:    */     {
/* 301:523 */       count++;
/* 302:524 */       double value = inst.instance(i).value(attr);
/* 303:525 */       sum += value;
/* 304:526 */       sqrSum += value * value;
/* 305:    */     }
/* 306:    */     double sd;
/* 307:    */     double sd;
/* 308:529 */     if (count > 1)
/* 309:    */     {
/* 310:530 */       double va = (sqrSum - sum * sum / count) / count;
/* 311:531 */       va = Math.abs(va);
/* 312:532 */       sd = Math.sqrt(va);
/* 313:    */     }
/* 314:    */     else
/* 315:    */     {
/* 316:534 */       sd = 0.0D;
/* 317:    */     }
/* 318:537 */     return sd;
/* 319:    */   }
/* 320:    */   
/* 321:    */   protected static final double absDev(int attr, Instances inst)
/* 322:    */   {
/* 323:549 */     double average = 0.0D;double absdiff = 0.0D;
/* 324:551 */     for (int i = 0; i <= inst.numInstances() - 1; i++) {
/* 325:552 */       average += inst.instance(i).value(attr);
/* 326:    */     }
/* 327:    */     double absDev;
/* 328:    */     double absDev;
/* 329:554 */     if (inst.numInstances() > 1)
/* 330:    */     {
/* 331:555 */       average /= inst.numInstances();
/* 332:556 */       for (i = 0; i <= inst.numInstances() - 1; i++) {
/* 333:557 */         absdiff += Math.abs(inst.instance(i).value(attr) - average);
/* 334:    */       }
/* 335:559 */       absDev = absdiff / inst.numInstances();
/* 336:    */     }
/* 337:    */     else
/* 338:    */     {
/* 339:561 */       absDev = 0.0D;
/* 340:    */     }
/* 341:564 */     return absDev;
/* 342:    */   }
/* 343:    */   
/* 344:    */   protected void setSaveInstances(boolean save)
/* 345:    */   {
/* 346:574 */     this.m_saveInstances = save;
/* 347:    */   }
/* 348:    */   
/* 349:    */   public boolean getRegressionTree()
/* 350:    */   {
/* 351:584 */     return this.m_regressionTree;
/* 352:    */   }
/* 353:    */   
/* 354:    */   public void setRegressionTree(boolean newregressionTree)
/* 355:    */   {
/* 356:594 */     this.m_regressionTree = newregressionTree;
/* 357:    */   }
/* 358:    */   
/* 359:    */   public void setMinNumInstances(double minNum)
/* 360:    */   {
/* 361:603 */     this.m_minNumInstances = minNum;
/* 362:    */   }
/* 363:    */   
/* 364:    */   public double getMinNumInstances()
/* 365:    */   {
/* 366:612 */     return this.m_minNumInstances;
/* 367:    */   }
/* 368:    */   
/* 369:    */   public RuleNode getM5RootNode()
/* 370:    */   {
/* 371:616 */     return this.m_topOfTree;
/* 372:    */   }
/* 373:    */   
/* 374:    */   public String getRevision()
/* 375:    */   {
/* 376:626 */     return RevisionUtils.extract("$Revision: 10169 $");
/* 377:    */   }
/* 378:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.m5.Rule
 * JD-Core Version:    0.7.0.1
 */