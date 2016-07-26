/*   1:    */ package weka.classifiers.functions;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.classifiers.AbstractClassifier;
/*   8:    */ import weka.classifiers.UpdateableClassifier;
/*   9:    */ import weka.core.Attribute;
/*  10:    */ import weka.core.Capabilities;
/*  11:    */ import weka.core.Capabilities.Capability;
/*  12:    */ import weka.core.Instance;
/*  13:    */ import weka.core.Instances;
/*  14:    */ import weka.core.Option;
/*  15:    */ import weka.core.OptionHandler;
/*  16:    */ import weka.core.RevisionUtils;
/*  17:    */ import weka.core.SelectedTag;
/*  18:    */ import weka.core.Tag;
/*  19:    */ import weka.core.TechnicalInformation;
/*  20:    */ import weka.core.TechnicalInformation.Field;
/*  21:    */ import weka.core.TechnicalInformation.Type;
/*  22:    */ import weka.core.TechnicalInformationHandler;
/*  23:    */ import weka.core.Utils;
/*  24:    */ import weka.filters.Filter;
/*  25:    */ import weka.filters.unsupervised.attribute.NominalToBinary;
/*  26:    */ import weka.filters.unsupervised.attribute.Normalize;
/*  27:    */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*  28:    */ 
/*  29:    */ public class SPegasos
/*  30:    */   extends AbstractClassifier
/*  31:    */   implements TechnicalInformationHandler, UpdateableClassifier, OptionHandler
/*  32:    */ {
/*  33:    */   private static final long serialVersionUID = -3732968666673530290L;
/*  34:    */   protected ReplaceMissingValues m_replaceMissing;
/*  35:    */   protected NominalToBinary m_nominalToBinary;
/*  36:    */   protected Normalize m_normalize;
/*  37:123 */   protected double m_lambda = 0.0001D;
/*  38:    */   protected double[] m_weights;
/*  39:    */   protected double m_t;
/*  40:135 */   protected int m_epochs = 500;
/*  41:141 */   protected boolean m_dontNormalize = false;
/*  42:147 */   protected boolean m_dontReplaceMissing = false;
/*  43:    */   protected Instances m_data;
/*  44:    */   protected static final int HINGE = 0;
/*  45:    */   protected static final int LOGLOSS = 1;
/*  46:    */   
/*  47:    */   public Capabilities getCapabilities()
/*  48:    */   {
/*  49:159 */     Capabilities result = super.getCapabilities();
/*  50:160 */     result.disableAll();
/*  51:    */     
/*  52:    */ 
/*  53:163 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  54:164 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  55:165 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  56:    */     
/*  57:    */ 
/*  58:168 */     result.enable(Capabilities.Capability.BINARY_CLASS);
/*  59:169 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  60:    */     
/*  61:    */ 
/*  62:172 */     result.setMinimumNumberInstances(0);
/*  63:    */     
/*  64:174 */     return result;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public String lambdaTipText()
/*  68:    */   {
/*  69:184 */     return "The regularization constant. (default = 0.0001)";
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void setLambda(double lambda)
/*  73:    */   {
/*  74:193 */     this.m_lambda = lambda;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public double getLambda()
/*  78:    */   {
/*  79:202 */     return this.m_lambda;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public String epochsTipText()
/*  83:    */   {
/*  84:212 */     return "The number of epochs to perform (batch learning). The total number of iterations is epochs * num instances.";
/*  85:    */   }
/*  86:    */   
/*  87:    */   public void setEpochs(int e)
/*  88:    */   {
/*  89:222 */     this.m_epochs = e;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public int getEpochs()
/*  93:    */   {
/*  94:231 */     return this.m_epochs;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public void setDontNormalize(boolean m)
/*  98:    */   {
/*  99:240 */     this.m_dontNormalize = m;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public boolean getDontNormalize()
/* 103:    */   {
/* 104:249 */     return this.m_dontNormalize;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public String dontNormalizeTipText()
/* 108:    */   {
/* 109:259 */     return "Turn normalization off";
/* 110:    */   }
/* 111:    */   
/* 112:    */   public void setDontReplaceMissing(boolean m)
/* 113:    */   {
/* 114:269 */     this.m_dontReplaceMissing = m;
/* 115:    */   }
/* 116:    */   
/* 117:    */   public boolean getDontReplaceMissing()
/* 118:    */   {
/* 119:278 */     return this.m_dontReplaceMissing;
/* 120:    */   }
/* 121:    */   
/* 122:    */   public String dontReplaceMissingTipText()
/* 123:    */   {
/* 124:288 */     return "Turn off global replacement of missing values";
/* 125:    */   }
/* 126:    */   
/* 127:    */   public void setLossFunction(SelectedTag function)
/* 128:    */   {
/* 129:297 */     if (function.getTags() == TAGS_SELECTION) {
/* 130:298 */       this.m_loss = function.getSelectedTag().getID();
/* 131:    */     }
/* 132:    */   }
/* 133:    */   
/* 134:    */   public SelectedTag getLossFunction()
/* 135:    */   {
/* 136:308 */     return new SelectedTag(this.m_loss, TAGS_SELECTION);
/* 137:    */   }
/* 138:    */   
/* 139:    */   public String lossFunctionTipText()
/* 140:    */   {
/* 141:318 */     return "The loss function to use. Hinge loss (SVM) or log loss (logistic regression).";
/* 142:    */   }
/* 143:    */   
/* 144:    */   public Enumeration<Option> listOptions()
/* 145:    */   {
/* 146:330 */     Vector<Option> newVector = new Vector();
/* 147:331 */     newVector.add(new Option("\tSet the loss function to minimize. 0 = hinge loss (SVM), 1 = log loss (logistic regression).\n\t(default = 0)", "F", 1, "-F"));
/* 148:    */     
/* 149:    */ 
/* 150:334 */     newVector.add(new Option("\tThe lambda regularization constant (default = 0.0001)", "L", 1, "-L <double>"));
/* 151:    */     
/* 152:336 */     newVector.add(new Option("\tThe number of epochs to perform (batch learning only, default = 500)", "E", 1, "-E <integer>"));
/* 153:    */     
/* 154:338 */     newVector.add(new Option("\tDon't normalize the data", "N", 0, "-N"));
/* 155:339 */     newVector.add(new Option("\tDon't replace missing values", "M", 0, "-M"));
/* 156:    */     
/* 157:341 */     newVector.addAll(Collections.list(super.listOptions()));
/* 158:    */     
/* 159:343 */     return newVector.elements();
/* 160:    */   }
/* 161:    */   
/* 162:    */   public void setOptions(String[] options)
/* 163:    */     throws Exception
/* 164:    */   {
/* 165:381 */     reset();
/* 166:    */     
/* 167:383 */     String lossString = Utils.getOption('F', options);
/* 168:384 */     if (lossString.length() != 0) {
/* 169:385 */       setLossFunction(new SelectedTag(Integer.parseInt(lossString), TAGS_SELECTION));
/* 170:    */     } else {
/* 171:388 */       setLossFunction(new SelectedTag(0, TAGS_SELECTION));
/* 172:    */     }
/* 173:391 */     String lambdaString = Utils.getOption('L', options);
/* 174:392 */     if (lambdaString.length() > 0) {
/* 175:393 */       setLambda(Double.parseDouble(lambdaString));
/* 176:    */     }
/* 177:396 */     String epochsString = Utils.getOption("E", options);
/* 178:397 */     if (epochsString.length() > 0) {
/* 179:398 */       setEpochs(Integer.parseInt(epochsString));
/* 180:    */     }
/* 181:401 */     setDontNormalize(Utils.getFlag("N", options));
/* 182:402 */     setDontReplaceMissing(Utils.getFlag('M', options));
/* 183:    */     
/* 184:404 */     super.setOptions(options);
/* 185:    */     
/* 186:406 */     Utils.checkForRemainingOptions(options);
/* 187:    */   }
/* 188:    */   
/* 189:    */   public String[] getOptions()
/* 190:    */   {
/* 191:416 */     ArrayList<String> options = new ArrayList();
/* 192:    */     
/* 193:418 */     options.add("-F");
/* 194:419 */     options.add("" + getLossFunction().getSelectedTag().getID());
/* 195:420 */     options.add("-L");
/* 196:421 */     options.add("" + getLambda());
/* 197:422 */     options.add("-E");
/* 198:423 */     options.add("" + getEpochs());
/* 199:424 */     if (getDontNormalize()) {
/* 200:425 */       options.add("-N");
/* 201:    */     }
/* 202:427 */     if (getDontReplaceMissing()) {
/* 203:428 */       options.add("-M");
/* 204:    */     }
/* 205:431 */     Collections.addAll(options, super.getOptions());
/* 206:    */     
/* 207:433 */     return (String[])options.toArray(new String[1]);
/* 208:    */   }
/* 209:    */   
/* 210:    */   public String globalInfo()
/* 211:    */   {
/* 212:443 */     return "Implements the stochastic variant of the Pegasos (Primal Estimated sub-GrAdient SOlver for SVM) method of Shalev-Shwartz et al. (2007). This implementation globally replaces all missing values and transforms nominal attributes into binary ones. It also normalizes all attributes, so the coefficients in the output are based on the normalized data. For more information, see\n\n" + getTechnicalInformation().toString();
/* 213:    */   }
/* 214:    */   
/* 215:    */   public TechnicalInformation getTechnicalInformation()
/* 216:    */   {
/* 217:464 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/* 218:465 */     result.setValue(TechnicalInformation.Field.AUTHOR, "S. Shalev-Shwartz and Y. Singer and N. Srebro");
/* 219:    */     
/* 220:467 */     result.setValue(TechnicalInformation.Field.YEAR, "2007");
/* 221:468 */     result.setValue(TechnicalInformation.Field.TITLE, "Pegasos: Primal Estimated sub-GrAdient SOlver for SVM");
/* 222:    */     
/* 223:470 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "24th International Conference on MachineLearning");
/* 224:    */     
/* 225:472 */     result.setValue(TechnicalInformation.Field.PAGES, "807-814");
/* 226:    */     
/* 227:474 */     return result;
/* 228:    */   }
/* 229:    */   
/* 230:    */   public void reset()
/* 231:    */   {
/* 232:481 */     this.m_t = 2.0D;
/* 233:482 */     this.m_weights = null;
/* 234:    */   }
/* 235:    */   
/* 236:    */   public void buildClassifier(Instances data)
/* 237:    */     throws Exception
/* 238:    */   {
/* 239:493 */     reset();
/* 240:    */     
/* 241:    */ 
/* 242:496 */     getCapabilities().testWithFail(data);
/* 243:    */     
/* 244:498 */     data = new Instances(data);
/* 245:499 */     data.deleteWithMissingClass();
/* 246:501 */     if ((data.numInstances() > 0) && (!this.m_dontReplaceMissing))
/* 247:    */     {
/* 248:502 */       this.m_replaceMissing = new ReplaceMissingValues();
/* 249:503 */       this.m_replaceMissing.setInputFormat(data);
/* 250:504 */       data = Filter.useFilter(data, this.m_replaceMissing);
/* 251:    */     }
/* 252:508 */     boolean onlyNumeric = true;
/* 253:509 */     for (int i = 0; i < data.numAttributes(); i++) {
/* 254:510 */       if ((i != data.classIndex()) && 
/* 255:511 */         (!data.attribute(i).isNumeric()))
/* 256:    */       {
/* 257:512 */         onlyNumeric = false;
/* 258:513 */         break;
/* 259:    */       }
/* 260:    */     }
/* 261:518 */     if (!onlyNumeric)
/* 262:    */     {
/* 263:519 */       this.m_nominalToBinary = new NominalToBinary();
/* 264:520 */       this.m_nominalToBinary.setInputFormat(data);
/* 265:521 */       data = Filter.useFilter(data, this.m_nominalToBinary);
/* 266:    */     }
/* 267:524 */     if ((!this.m_dontNormalize) && (data.numInstances() > 0))
/* 268:    */     {
/* 269:526 */       this.m_normalize = new Normalize();
/* 270:527 */       this.m_normalize.setInputFormat(data);
/* 271:528 */       data = Filter.useFilter(data, this.m_normalize);
/* 272:    */     }
/* 273:531 */     this.m_weights = new double[data.numAttributes() + 1];
/* 274:532 */     this.m_data = new Instances(data, 0);
/* 275:534 */     if (data.numInstances() > 0) {
/* 276:535 */       train(data);
/* 277:    */     }
/* 278:    */   }
/* 279:    */   
/* 280:    */   private void train(Instances data)
/* 281:    */     throws Exception
/* 282:    */   {
/* 283:540 */     for (int e = 0; e < this.m_epochs; e++) {
/* 284:541 */       for (int i = 0; i < data.numInstances(); i++) {
/* 285:542 */         updateClassifier(data.instance(i));
/* 286:    */       }
/* 287:    */     }
/* 288:    */   }
/* 289:    */   
/* 290:    */   protected static double dotProd(Instance inst1, double[] weights, int classIndex)
/* 291:    */   {
/* 292:549 */     double result = 0.0D;
/* 293:    */     
/* 294:551 */     int n1 = inst1.numValues();
/* 295:552 */     int n2 = weights.length - 1;
/* 296:    */     
/* 297:554 */     int p1 = 0;
/* 298:554 */     for (int p2 = 0; (p1 < n1) && (p2 < n2);)
/* 299:    */     {
/* 300:555 */       int ind1 = inst1.index(p1);
/* 301:556 */       int ind2 = p2;
/* 302:557 */       if (ind1 == ind2)
/* 303:    */       {
/* 304:558 */         if ((ind1 != classIndex) && (!inst1.isMissingSparse(p1))) {
/* 305:559 */           result += inst1.valueSparse(p1) * weights[p2];
/* 306:    */         }
/* 307:561 */         p1++;
/* 308:562 */         p2++;
/* 309:    */       }
/* 310:563 */       else if (ind1 > ind2)
/* 311:    */       {
/* 312:564 */         p2++;
/* 313:    */       }
/* 314:    */       else
/* 315:    */       {
/* 316:566 */         p1++;
/* 317:    */       }
/* 318:    */     }
/* 319:569 */     return result;
/* 320:    */   }
/* 321:    */   
/* 322:576 */   protected int m_loss = 0;
/* 323:579 */   public static final Tag[] TAGS_SELECTION = { new Tag(0, "Hinge loss (SVM)"), new Tag(1, "Log loss (logistic regression)") };
/* 324:    */   
/* 325:    */   protected double dloss(double z)
/* 326:    */   {
/* 327:584 */     if (this.m_loss == 0) {
/* 328:585 */       return z < 1.0D ? 1.0D : 0.0D;
/* 329:    */     }
/* 330:589 */     if (z < 0.0D) {
/* 331:590 */       return 1.0D / (Math.exp(z) + 1.0D);
/* 332:    */     }
/* 333:592 */     double t = Math.exp(-z);
/* 334:593 */     return t / (t + 1.0D);
/* 335:    */   }
/* 336:    */   
/* 337:    */   public void updateClassifier(Instance instance)
/* 338:    */     throws Exception
/* 339:    */   {
/* 340:606 */     if (!instance.classIsMissing())
/* 341:    */     {
/* 342:608 */       double learningRate = 1.0D / (this.m_lambda * this.m_t);
/* 343:    */       
/* 344:610 */       double scale = 1.0D - 1.0D / this.m_t;
/* 345:611 */       double y = instance.classValue() == 0.0D ? -1.0D : 1.0D;
/* 346:612 */       double wx = dotProd(instance, this.m_weights, instance.classIndex());
/* 347:613 */       double z = y * (wx + this.m_weights[(this.m_weights.length - 1)]);
/* 348:615 */       for (int j = 0; j < this.m_weights.length - 1; j++) {
/* 349:616 */         if (j != instance.classIndex()) {
/* 350:617 */           this.m_weights[j] *= scale;
/* 351:    */         }
/* 352:    */       }
/* 353:621 */       if ((this.m_loss == 1) || (z < 1.0D))
/* 354:    */       {
/* 355:622 */         double loss = dloss(z);
/* 356:623 */         int n1 = instance.numValues();
/* 357:624 */         for (int p1 = 0; p1 < n1; p1++)
/* 358:    */         {
/* 359:625 */           int indS = instance.index(p1);
/* 360:626 */           if ((indS != instance.classIndex()) && (!instance.isMissingSparse(p1)))
/* 361:    */           {
/* 362:627 */             double m = learningRate * loss * (instance.valueSparse(p1) * y);
/* 363:628 */             this.m_weights[indS] += m;
/* 364:    */           }
/* 365:    */         }
/* 366:633 */         this.m_weights[(this.m_weights.length - 1)] += learningRate * loss * y;
/* 367:    */       }
/* 368:636 */       double norm = 0.0D;
/* 369:637 */       for (int k = 0; k < this.m_weights.length - 1; k++) {
/* 370:638 */         if (k != instance.classIndex()) {
/* 371:639 */           norm += this.m_weights[k] * this.m_weights[k];
/* 372:    */         }
/* 373:    */       }
/* 374:643 */       double scale2 = Math.min(1.0D, 1.0D / (this.m_lambda * norm));
/* 375:644 */       if (scale2 < 1.0D)
/* 376:    */       {
/* 377:645 */         scale2 = Math.sqrt(scale2);
/* 378:646 */         for (int j = 0; j < this.m_weights.length - 1; j++) {
/* 379:647 */           if (j != instance.classIndex()) {
/* 380:648 */             this.m_weights[j] *= scale2;
/* 381:    */           }
/* 382:    */         }
/* 383:    */       }
/* 384:652 */       this.m_t += 1.0D;
/* 385:    */     }
/* 386:    */   }
/* 387:    */   
/* 388:    */   public double[] distributionForInstance(Instance inst)
/* 389:    */     throws Exception
/* 390:    */   {
/* 391:665 */     double[] result = new double[2];
/* 392:667 */     if (this.m_replaceMissing != null)
/* 393:    */     {
/* 394:668 */       this.m_replaceMissing.input(inst);
/* 395:669 */       inst = this.m_replaceMissing.output();
/* 396:    */     }
/* 397:672 */     if (this.m_nominalToBinary != null)
/* 398:    */     {
/* 399:673 */       this.m_nominalToBinary.input(inst);
/* 400:674 */       inst = this.m_nominalToBinary.output();
/* 401:    */     }
/* 402:677 */     if (this.m_normalize != null)
/* 403:    */     {
/* 404:678 */       this.m_normalize.input(inst);
/* 405:679 */       inst = this.m_normalize.output();
/* 406:    */     }
/* 407:682 */     double wx = dotProd(inst, this.m_weights, inst.classIndex());
/* 408:683 */     double z = wx + this.m_weights[(this.m_weights.length - 1)];
/* 409:686 */     if (z <= 0.0D)
/* 410:    */     {
/* 411:688 */       if (this.m_loss == 1)
/* 412:    */       {
/* 413:689 */         result[0] = (1.0D / (1.0D + Math.exp(z)));
/* 414:690 */         result[1] = (1.0D - result[0]);
/* 415:    */       }
/* 416:    */       else
/* 417:    */       {
/* 418:692 */         result[0] = 1.0D;
/* 419:    */       }
/* 420:    */     }
/* 421:695 */     else if (this.m_loss == 1)
/* 422:    */     {
/* 423:696 */       result[1] = (1.0D / (1.0D + Math.exp(-z)));
/* 424:697 */       result[0] = (1.0D - result[1]);
/* 425:    */     }
/* 426:    */     else
/* 427:    */     {
/* 428:699 */       result[1] = 1.0D;
/* 429:    */     }
/* 430:702 */     return result;
/* 431:    */   }
/* 432:    */   
/* 433:    */   public String toString()
/* 434:    */   {
/* 435:712 */     if (this.m_weights == null) {
/* 436:713 */       return "SPegasos: No model built yet.\n";
/* 437:    */     }
/* 438:715 */     StringBuffer buff = new StringBuffer();
/* 439:716 */     buff.append("Loss function: ");
/* 440:717 */     if (this.m_loss == 0) {
/* 441:718 */       buff.append("Hinge loss (SVM)\n\n");
/* 442:    */     } else {
/* 443:720 */       buff.append("Log loss (logistic regression)\n\n");
/* 444:    */     }
/* 445:722 */     int printed = 0;
/* 446:724 */     for (int i = 0; i < this.m_weights.length - 1; i++) {
/* 447:725 */       if (i != this.m_data.classIndex())
/* 448:    */       {
/* 449:726 */         if (printed > 0) {
/* 450:727 */           buff.append(" + ");
/* 451:    */         } else {
/* 452:729 */           buff.append("   ");
/* 453:    */         }
/* 454:732 */         buff.append(Utils.doubleToString(this.m_weights[i], 12, 4) + " " + (this.m_normalize != null ? "(normalized) " : "") + this.m_data.attribute(i).name() + "\n");
/* 455:    */         
/* 456:    */ 
/* 457:    */ 
/* 458:736 */         printed++;
/* 459:    */       }
/* 460:    */     }
/* 461:740 */     if (this.m_weights[(this.m_weights.length - 1)] > 0.0D) {
/* 462:741 */       buff.append(" + " + Utils.doubleToString(this.m_weights[(this.m_weights.length - 1)], 12, 4));
/* 463:    */     } else {
/* 464:744 */       buff.append(" - " + Utils.doubleToString(-this.m_weights[(this.m_weights.length - 1)], 12, 4));
/* 465:    */     }
/* 466:748 */     return buff.toString();
/* 467:    */   }
/* 468:    */   
/* 469:    */   public String getRevision()
/* 470:    */   {
/* 471:758 */     return RevisionUtils.extract("$Revision: 10390 $");
/* 472:    */   }
/* 473:    */   
/* 474:    */   public static void main(String[] args)
/* 475:    */   {
/* 476:765 */     runClassifier(new SPegasos(), args);
/* 477:    */   }
/* 478:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.SPegasos
 * JD-Core Version:    0.7.0.1
 */