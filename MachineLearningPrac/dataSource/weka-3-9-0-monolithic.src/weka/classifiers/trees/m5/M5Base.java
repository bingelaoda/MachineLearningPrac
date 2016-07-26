/*   1:    */ package weka.classifiers.trees.m5;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.Random;
/*   8:    */ import java.util.Vector;
/*   9:    */ import weka.classifiers.AbstractClassifier;
/*  10:    */ import weka.classifiers.functions.LinearRegression;
/*  11:    */ import weka.core.AdditionalMeasureProducer;
/*  12:    */ import weka.core.Capabilities;
/*  13:    */ import weka.core.Instance;
/*  14:    */ import weka.core.Instances;
/*  15:    */ import weka.core.Option;
/*  16:    */ import weka.core.TechnicalInformation;
/*  17:    */ import weka.core.TechnicalInformation.Field;
/*  18:    */ import weka.core.TechnicalInformation.Type;
/*  19:    */ import weka.core.TechnicalInformationHandler;
/*  20:    */ import weka.core.Utils;
/*  21:    */ import weka.filters.Filter;
/*  22:    */ import weka.filters.supervised.attribute.NominalToBinary;
/*  23:    */ import weka.filters.unsupervised.attribute.RemoveUseless;
/*  24:    */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*  25:    */ 
/*  26:    */ public abstract class M5Base
/*  27:    */   extends AbstractClassifier
/*  28:    */   implements AdditionalMeasureProducer, TechnicalInformationHandler
/*  29:    */ {
/*  30:    */   private static final long serialVersionUID = -4022221950191647679L;
/*  31:    */   private Instances m_instances;
/*  32:    */   protected ArrayList<Rule> m_ruleSet;
/*  33:    */   private boolean m_generateRules;
/*  34:    */   private boolean m_unsmoothedPredictions;
/*  35:    */   private ReplaceMissingValues m_replaceMissing;
/*  36:    */   private NominalToBinary m_nominalToBinary;
/*  37:    */   private RemoveUseless m_removeUseless;
/*  38:123 */   protected boolean m_saveInstances = false;
/*  39:    */   protected boolean m_regressionTree;
/*  40:133 */   protected boolean m_useUnpruned = false;
/*  41:138 */   protected double m_minNumInstances = 4.0D;
/*  42:    */   
/*  43:    */   public M5Base()
/*  44:    */   {
/*  45:144 */     this.m_generateRules = false;
/*  46:145 */     this.m_unsmoothedPredictions = false;
/*  47:146 */     this.m_useUnpruned = false;
/*  48:147 */     this.m_minNumInstances = 4.0D;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public String globalInfo()
/*  52:    */   {
/*  53:157 */     return "M5Base. Implements base routines for generating M5 Model trees and rules\nThe original algorithm M5 was invented by R. Quinlan and Yong Wang made improvements.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*  54:    */   }
/*  55:    */   
/*  56:    */   public TechnicalInformation getTechnicalInformation()
/*  57:    */   {
/*  58:177 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  59:178 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Ross J. Quinlan");
/*  60:179 */     result.setValue(TechnicalInformation.Field.TITLE, "Learning with Continuous Classes");
/*  61:180 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "5th Australian Joint Conference on Artificial Intelligence");
/*  62:    */     
/*  63:182 */     result.setValue(TechnicalInformation.Field.YEAR, "1992");
/*  64:183 */     result.setValue(TechnicalInformation.Field.PAGES, "343-348");
/*  65:184 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "World Scientific");
/*  66:185 */     result.setValue(TechnicalInformation.Field.ADDRESS, "Singapore");
/*  67:    */     
/*  68:187 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.INPROCEEDINGS);
/*  69:188 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "Y. Wang and I. H. Witten");
/*  70:189 */     additional.setValue(TechnicalInformation.Field.TITLE, "Induction of model trees for predicting continuous classes");
/*  71:    */     
/*  72:191 */     additional.setValue(TechnicalInformation.Field.BOOKTITLE, "Poster papers of the 9th European Conference on Machine Learning");
/*  73:    */     
/*  74:193 */     additional.setValue(TechnicalInformation.Field.YEAR, "1997");
/*  75:194 */     additional.setValue(TechnicalInformation.Field.PUBLISHER, "Springer");
/*  76:    */     
/*  77:196 */     return result;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public Enumeration<Option> listOptions()
/*  81:    */   {
/*  82:206 */     Vector<Option> newVector = new Vector(4);
/*  83:    */     
/*  84:208 */     newVector.add(new Option("\tUse unpruned tree/rules", "N", 0, "-N"));
/*  85:    */     
/*  86:210 */     newVector.add(new Option("\tUse unsmoothed predictions", "U", 0, "-U"));
/*  87:    */     
/*  88:212 */     newVector.add(new Option("\tBuild regression tree/rule rather than a model tree/rule", "R", 0, "-R"));
/*  89:    */     
/*  90:    */ 
/*  91:215 */     newVector.add(new Option("\tSet minimum number of instances per leaf\n\t(default 4)", "M", 1, "-M <minimum number of instances>"));
/*  92:    */     
/*  93:    */ 
/*  94:218 */     newVector.addAll(Collections.list(super.listOptions()));
/*  95:    */     
/*  96:220 */     return newVector.elements();
/*  97:    */   }
/*  98:    */   
/*  99:    */   public void setOptions(String[] options)
/* 100:    */     throws Exception
/* 101:    */   {
/* 102:243 */     setUnpruned(Utils.getFlag('N', options));
/* 103:244 */     setUseUnsmoothed(Utils.getFlag('U', options));
/* 104:245 */     setBuildRegressionTree(Utils.getFlag('R', options));
/* 105:246 */     String optionString = Utils.getOption('M', options);
/* 106:247 */     if (optionString.length() != 0) {
/* 107:248 */       setMinNumInstances(new Double(optionString).doubleValue());
/* 108:    */     }
/* 109:250 */     super.setOptions(options);
/* 110:251 */     Utils.checkForRemainingOptions(options);
/* 111:    */   }
/* 112:    */   
/* 113:    */   public String[] getOptions()
/* 114:    */   {
/* 115:262 */     Vector<String> result = new Vector();
/* 116:264 */     if (getUnpruned()) {
/* 117:265 */       result.add("-N");
/* 118:    */     }
/* 119:268 */     if (getUseUnsmoothed()) {
/* 120:269 */       result.add("-U");
/* 121:    */     }
/* 122:272 */     if (getBuildRegressionTree()) {
/* 123:273 */       result.add("-R");
/* 124:    */     }
/* 125:276 */     result.add("-M");
/* 126:277 */     result.add("" + getMinNumInstances());
/* 127:    */     
/* 128:279 */     Collections.addAll(result, super.getOptions());
/* 129:    */     
/* 130:281 */     return (String[])result.toArray(new String[result.size()]);
/* 131:    */   }
/* 132:    */   
/* 133:    */   public String unprunedTipText()
/* 134:    */   {
/* 135:291 */     return "Whether unpruned tree/rules are to be generated.";
/* 136:    */   }
/* 137:    */   
/* 138:    */   public void setUnpruned(boolean unpruned)
/* 139:    */   {
/* 140:300 */     this.m_useUnpruned = unpruned;
/* 141:    */   }
/* 142:    */   
/* 143:    */   public boolean getUnpruned()
/* 144:    */   {
/* 145:309 */     return this.m_useUnpruned;
/* 146:    */   }
/* 147:    */   
/* 148:    */   public String generateRulesTipText()
/* 149:    */   {
/* 150:319 */     return "Whether to generate rules (decision list) rather than a tree.";
/* 151:    */   }
/* 152:    */   
/* 153:    */   protected void setGenerateRules(boolean u)
/* 154:    */   {
/* 155:328 */     this.m_generateRules = u;
/* 156:    */   }
/* 157:    */   
/* 158:    */   protected boolean getGenerateRules()
/* 159:    */   {
/* 160:337 */     return this.m_generateRules;
/* 161:    */   }
/* 162:    */   
/* 163:    */   public String useUnsmoothedTipText()
/* 164:    */   {
/* 165:347 */     return "Whether to use unsmoothed predictions.";
/* 166:    */   }
/* 167:    */   
/* 168:    */   public void setUseUnsmoothed(boolean s)
/* 169:    */   {
/* 170:356 */     this.m_unsmoothedPredictions = s;
/* 171:    */   }
/* 172:    */   
/* 173:    */   public boolean getUseUnsmoothed()
/* 174:    */   {
/* 175:365 */     return this.m_unsmoothedPredictions;
/* 176:    */   }
/* 177:    */   
/* 178:    */   public String buildRegressionTreeTipText()
/* 179:    */   {
/* 180:375 */     return "Whether to generate a regression tree/rule instead of a model tree/rule.";
/* 181:    */   }
/* 182:    */   
/* 183:    */   public boolean getBuildRegressionTree()
/* 184:    */   {
/* 185:385 */     return this.m_regressionTree;
/* 186:    */   }
/* 187:    */   
/* 188:    */   public void setBuildRegressionTree(boolean newregressionTree)
/* 189:    */   {
/* 190:395 */     this.m_regressionTree = newregressionTree;
/* 191:    */   }
/* 192:    */   
/* 193:    */   public String minNumInstancesTipText()
/* 194:    */   {
/* 195:405 */     return "The minimum number of instances to allow at a leaf node.";
/* 196:    */   }
/* 197:    */   
/* 198:    */   public void setMinNumInstances(double minNum)
/* 199:    */   {
/* 200:414 */     this.m_minNumInstances = minNum;
/* 201:    */   }
/* 202:    */   
/* 203:    */   public double getMinNumInstances()
/* 204:    */   {
/* 205:423 */     return this.m_minNumInstances;
/* 206:    */   }
/* 207:    */   
/* 208:    */   public Capabilities getCapabilities()
/* 209:    */   {
/* 210:433 */     return new LinearRegression().getCapabilities();
/* 211:    */   }
/* 212:    */   
/* 213:    */   public void buildClassifier(Instances data)
/* 214:    */     throws Exception
/* 215:    */   {
/* 216:445 */     getCapabilities().testWithFail(data);
/* 217:    */     
/* 218:    */ 
/* 219:448 */     data = new Instances(data);
/* 220:449 */     data.deleteWithMissingClass();
/* 221:    */     
/* 222:451 */     this.m_instances = new Instances(data);
/* 223:    */     
/* 224:453 */     this.m_replaceMissing = new ReplaceMissingValues();
/* 225:454 */     this.m_replaceMissing.setInputFormat(this.m_instances);
/* 226:455 */     this.m_instances = Filter.useFilter(this.m_instances, this.m_replaceMissing);
/* 227:    */     
/* 228:457 */     this.m_nominalToBinary = new NominalToBinary();
/* 229:458 */     this.m_nominalToBinary.setInputFormat(this.m_instances);
/* 230:459 */     this.m_instances = Filter.useFilter(this.m_instances, this.m_nominalToBinary);
/* 231:    */     
/* 232:461 */     this.m_removeUseless = new RemoveUseless();
/* 233:462 */     this.m_removeUseless.setInputFormat(this.m_instances);
/* 234:463 */     this.m_instances = Filter.useFilter(this.m_instances, this.m_removeUseless);
/* 235:    */     
/* 236:465 */     this.m_instances.randomize(new Random(1L));
/* 237:    */     
/* 238:467 */     this.m_ruleSet = new ArrayList();
/* 239:471 */     if (this.m_generateRules)
/* 240:    */     {
/* 241:472 */       Instances tempInst = this.m_instances;
/* 242:    */       do
/* 243:    */       {
/* 244:475 */         Rule tempRule = new Rule();
/* 245:476 */         tempRule.setSmoothing(!this.m_unsmoothedPredictions);
/* 246:477 */         tempRule.setRegressionTree(this.m_regressionTree);
/* 247:478 */         tempRule.setUnpruned(this.m_useUnpruned);
/* 248:479 */         tempRule.setSaveInstances(false);
/* 249:480 */         tempRule.setMinNumInstances(this.m_minNumInstances);
/* 250:481 */         tempRule.buildClassifier(tempInst);
/* 251:482 */         this.m_ruleSet.add(tempRule);
/* 252:    */         
/* 253:484 */         tempInst = tempRule.notCoveredInstances();
/* 254:485 */         tempRule.freeNotCoveredInstances();
/* 255:486 */       } while (tempInst.numInstances() > 0);
/* 256:    */     }
/* 257:    */     else
/* 258:    */     {
/* 259:489 */       Rule tempRule = new Rule();
/* 260:    */       
/* 261:491 */       tempRule.setUseTree(true);
/* 262:    */       
/* 263:493 */       tempRule.setSmoothing(!this.m_unsmoothedPredictions);
/* 264:494 */       tempRule.setSaveInstances(this.m_saveInstances);
/* 265:495 */       tempRule.setRegressionTree(this.m_regressionTree);
/* 266:496 */       tempRule.setUnpruned(this.m_useUnpruned);
/* 267:497 */       tempRule.setMinNumInstances(this.m_minNumInstances);
/* 268:    */       
/* 269:    */ 
/* 270:    */ 
/* 271:501 */       Instances temp_train = this.m_instances;
/* 272:    */       
/* 273:503 */       tempRule.buildClassifier(temp_train);
/* 274:    */       
/* 275:505 */       this.m_ruleSet.add(tempRule);
/* 276:    */     }
/* 277:511 */     this.m_instances = new Instances(this.m_instances, 0);
/* 278:    */   }
/* 279:    */   
/* 280:    */   public double classifyInstance(Instance inst)
/* 281:    */     throws Exception
/* 282:    */   {
/* 283:525 */     double prediction = 0.0D;
/* 284:526 */     boolean success = false;
/* 285:    */     
/* 286:528 */     this.m_replaceMissing.input(inst);
/* 287:529 */     inst = this.m_replaceMissing.output();
/* 288:530 */     this.m_nominalToBinary.input(inst);
/* 289:531 */     inst = this.m_nominalToBinary.output();
/* 290:532 */     this.m_removeUseless.input(inst);
/* 291:533 */     inst = this.m_removeUseless.output();
/* 292:535 */     if (this.m_ruleSet == null) {
/* 293:536 */       throw new Exception("Classifier has not been built yet!");
/* 294:    */     }
/* 295:539 */     if (!this.m_generateRules)
/* 296:    */     {
/* 297:540 */       Rule temp = (Rule)this.m_ruleSet.get(0);
/* 298:541 */       return temp.classifyInstance(inst);
/* 299:    */     }
/* 300:547 */     for (int i = 0; i < this.m_ruleSet.size(); i++)
/* 301:    */     {
/* 302:548 */       boolean cont = false;
/* 303:549 */       Rule temp = (Rule)this.m_ruleSet.get(i);
/* 304:    */       try
/* 305:    */       {
/* 306:552 */         prediction = temp.classifyInstance(inst);
/* 307:553 */         success = true;
/* 308:    */       }
/* 309:    */       catch (Exception e)
/* 310:    */       {
/* 311:555 */         cont = true;
/* 312:    */       }
/* 313:558 */       if (!cont) {
/* 314:    */         break;
/* 315:    */       }
/* 316:    */     }
/* 317:563 */     if (!success) {
/* 318:564 */       System.out.println("Error in predicting (DecList)");
/* 319:    */     }
/* 320:566 */     return prediction;
/* 321:    */   }
/* 322:    */   
/* 323:    */   public String toString()
/* 324:    */   {
/* 325:576 */     StringBuffer text = new StringBuffer();
/* 326:579 */     if (this.m_ruleSet == null) {
/* 327:580 */       return "Classifier hasn't been built yet!";
/* 328:    */     }
/* 329:583 */     if (this.m_generateRules)
/* 330:    */     {
/* 331:584 */       text.append("M5 " + (this.m_useUnpruned == true ? "unpruned " : "pruned ") + (this.m_regressionTree == true ? "regression " : "model ") + "rules ");
/* 332:587 */       if (!this.m_unsmoothedPredictions) {
/* 333:588 */         text.append("\n(using smoothed linear models) ");
/* 334:    */       }
/* 335:591 */       text.append(":\n");
/* 336:    */       
/* 337:593 */       text.append("Number of Rules : " + this.m_ruleSet.size() + "\n\n");
/* 338:595 */       for (int j = 0; j < this.m_ruleSet.size(); j++)
/* 339:    */       {
/* 340:596 */         Rule temp = (Rule)this.m_ruleSet.get(j);
/* 341:    */         
/* 342:598 */         text.append("Rule: " + (j + 1) + "\n");
/* 343:599 */         text.append(temp.toString());
/* 344:    */       }
/* 345:    */     }
/* 346:    */     else
/* 347:    */     {
/* 348:602 */       Rule temp = (Rule)this.m_ruleSet.get(0);
/* 349:603 */       text.append(temp.toString());
/* 350:    */     }
/* 351:605 */     return text.toString();
/* 352:    */   }
/* 353:    */   
/* 354:    */   public Enumeration<String> enumerateMeasures()
/* 355:    */   {
/* 356:615 */     Vector<String> newVector = new Vector(1);
/* 357:616 */     newVector.add("measureNumRules");
/* 358:617 */     return newVector.elements();
/* 359:    */   }
/* 360:    */   
/* 361:    */   public double getMeasure(String additionalMeasureName)
/* 362:    */   {
/* 363:629 */     if (additionalMeasureName.compareToIgnoreCase("measureNumRules") == 0) {
/* 364:630 */       return measureNumRules();
/* 365:    */     }
/* 366:632 */     throw new IllegalArgumentException(additionalMeasureName + " not supported (M5)");
/* 367:    */   }
/* 368:    */   
/* 369:    */   public double measureNumRules()
/* 370:    */   {
/* 371:644 */     if (this.m_generateRules) {
/* 372:645 */       return this.m_ruleSet.size();
/* 373:    */     }
/* 374:647 */     return ((Rule)this.m_ruleSet.get(0)).m_topOfTree.numberOfLinearModels();
/* 375:    */   }
/* 376:    */   
/* 377:    */   public RuleNode getM5RootNode()
/* 378:    */   {
/* 379:651 */     Rule temp = (Rule)this.m_ruleSet.get(0);
/* 380:652 */     return temp.getM5RootNode();
/* 381:    */   }
/* 382:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.m5.M5Base
 * JD-Core Version:    0.7.0.1
 */