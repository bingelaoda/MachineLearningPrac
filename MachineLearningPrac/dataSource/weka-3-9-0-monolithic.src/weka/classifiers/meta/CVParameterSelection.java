/*   1:    */ package weka.classifiers.meta;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.Random;
/*   8:    */ import java.util.Vector;
/*   9:    */ import weka.classifiers.Classifier;
/*  10:    */ import weka.classifiers.Evaluation;
/*  11:    */ import weka.classifiers.RandomizableSingleClassifierEnhancer;
/*  12:    */ import weka.core.Attribute;
/*  13:    */ import weka.core.Capabilities;
/*  14:    */ import weka.core.Drawable;
/*  15:    */ import weka.core.Instance;
/*  16:    */ import weka.core.Instances;
/*  17:    */ import weka.core.Option;
/*  18:    */ import weka.core.OptionHandler;
/*  19:    */ import weka.core.RevisionHandler;
/*  20:    */ import weka.core.RevisionUtils;
/*  21:    */ import weka.core.Summarizable;
/*  22:    */ import weka.core.TechnicalInformation;
/*  23:    */ import weka.core.TechnicalInformation.Field;
/*  24:    */ import weka.core.TechnicalInformation.Type;
/*  25:    */ import weka.core.TechnicalInformationHandler;
/*  26:    */ import weka.core.Utils;
/*  27:    */ 
/*  28:    */ public class CVParameterSelection
/*  29:    */   extends RandomizableSingleClassifierEnhancer
/*  30:    */   implements Drawable, Summarizable, TechnicalInformationHandler
/*  31:    */ {
/*  32:    */   static final long serialVersionUID = -6529603380876641265L;
/*  33:    */   protected String[] m_ClassifierOptions;
/*  34:    */   protected String[] m_BestClassifierOptions;
/*  35:    */   protected String[] m_InitOptions;
/*  36:    */   protected double m_BestPerformance;
/*  37:    */   
/*  38:    */   protected class CVParameter
/*  39:    */     implements Serializable, RevisionHandler
/*  40:    */   {
/*  41:    */     static final long serialVersionUID = -4668812017709421953L;
/*  42:    */     private String m_ParamChar;
/*  43:    */     private double m_Lower;
/*  44:    */     private double m_Upper;
/*  45:    */     private double m_Steps;
/*  46:    */     private double m_ParamValue;
/*  47:    */     private boolean m_AddAtEnd;
/*  48:    */     private boolean m_RoundParam;
/*  49:    */     
/*  50:    */     public CVParameter(String param)
/*  51:    */       throws Exception
/*  52:    */     {
/*  53:160 */       String[] parts = param.split(" ");
/*  54:161 */       if ((parts.length < 4) || (parts.length > 5)) {
/*  55:162 */         throw new Exception("CVParameter " + param + ": four or five components expected!");
/*  56:    */       }
/*  57:    */       try
/*  58:    */       {
/*  59:167 */         Double.parseDouble(parts[0]);
/*  60:168 */         throw new Exception("CVParameter " + param + ": Character parameter identifier expected");
/*  61:    */       }
/*  62:    */       catch (NumberFormatException n)
/*  63:    */       {
/*  64:171 */         this.m_ParamChar = parts[0];
/*  65:    */         try
/*  66:    */         {
/*  67:175 */           this.m_Lower = Double.parseDouble(parts[1]);
/*  68:    */         }
/*  69:    */         catch (NumberFormatException n)
/*  70:    */         {
/*  71:177 */           throw new Exception("CVParameter " + param + ": Numeric lower bound expected");
/*  72:    */         }
/*  73:181 */         if (parts[2].equals("A")) {
/*  74:182 */           this.m_Upper = (this.m_Lower - 1.0D);
/*  75:183 */         } else if (parts[2].equals("I")) {
/*  76:184 */           this.m_Upper = (this.m_Lower - 2.0D);
/*  77:    */         } else {
/*  78:    */           try
/*  79:    */           {
/*  80:187 */             this.m_Upper = Double.parseDouble(parts[2]);
/*  81:189 */             if (this.m_Upper < this.m_Lower) {
/*  82:190 */               throw new Exception("CVParameter " + param + ": Upper bound is less than lower bound");
/*  83:    */             }
/*  84:    */           }
/*  85:    */           catch (NumberFormatException n)
/*  86:    */           {
/*  87:194 */             throw new Exception("CVParameter " + param + ": Upper bound must be numeric, or 'A' or 'N'");
/*  88:    */           }
/*  89:    */         }
/*  90:    */         try
/*  91:    */         {
/*  92:200 */           this.m_Steps = Double.parseDouble(parts[3]);
/*  93:    */         }
/*  94:    */         catch (NumberFormatException n)
/*  95:    */         {
/*  96:202 */           throw new Exception("CVParameter " + param + ": Numeric number of steps expected");
/*  97:    */         }
/*  98:206 */         if ((parts.length == 5) && (parts[4].equals("R"))) {
/*  99:207 */           this.m_RoundParam = true;
/* 100:    */         }
/* 101:    */       }
/* 102:    */     }
/* 103:    */     
/* 104:    */     public String toString()
/* 105:    */     {
/* 106:218 */       String result = this.m_ParamChar + " " + this.m_Lower + " ";
/* 107:219 */       switch ((int)(this.m_Lower - this.m_Upper + 0.5D))
/* 108:    */       {
/* 109:    */       case 1: 
/* 110:221 */         result = result + "A";
/* 111:222 */         break;
/* 112:    */       case 2: 
/* 113:224 */         result = result + "I";
/* 114:225 */         break;
/* 115:    */       default: 
/* 116:227 */         result = result + this.m_Upper;
/* 117:    */       }
/* 118:230 */       result = result + " " + this.m_Steps;
/* 119:231 */       if (this.m_RoundParam) {
/* 120:232 */         result = result + " R";
/* 121:    */       }
/* 122:234 */       return result;
/* 123:    */     }
/* 124:    */     
/* 125:    */     public String getRevision()
/* 126:    */     {
/* 127:243 */       return RevisionUtils.extract("$Revision: 10141 $");
/* 128:    */     }
/* 129:    */   }
/* 130:    */   
/* 131:264 */   protected Vector<CVParameter> m_CVParams = new Vector();
/* 132:    */   protected int m_NumAttributes;
/* 133:    */   protected int m_TrainFoldSize;
/* 134:273 */   protected int m_NumFolds = 10;
/* 135:    */   
/* 136:    */   protected String[] createOptions()
/* 137:    */   {
/* 138:284 */     String[] options = new String[this.m_ClassifierOptions.length + 2 * this.m_CVParams.size()];
/* 139:    */     
/* 140:286 */     int start = 0;int end = options.length;
/* 141:289 */     for (int i = 0; i < this.m_CVParams.size(); i++)
/* 142:    */     {
/* 143:290 */       CVParameter cvParam = (CVParameter)this.m_CVParams.elementAt(i);
/* 144:291 */       double paramValue = cvParam.m_ParamValue;
/* 145:292 */       if (cvParam.m_RoundParam) {
/* 146:294 */         paramValue = Math.rint(paramValue);
/* 147:    */       }
/* 148:296 */       boolean isInt = paramValue - (int)paramValue == 0.0D;
/* 149:298 */       if (cvParam.m_AddAtEnd)
/* 150:    */       {
/* 151:299 */         options[(--end)] = ("" + ((cvParam.m_RoundParam) || (isInt) ? Utils.doubleToString(paramValue, 4) : Double.valueOf(cvParam.m_ParamValue)));
/* 152:    */         
/* 153:    */ 
/* 154:302 */         options[(--end)] = ("-" + cvParam.m_ParamChar);
/* 155:    */       }
/* 156:    */       else
/* 157:    */       {
/* 158:304 */         options[(start++)] = ("-" + cvParam.m_ParamChar);
/* 159:305 */         options[(start++)] = ("" + ((cvParam.m_RoundParam) || (isInt) ? Utils.doubleToString(paramValue, 4) : Double.valueOf(cvParam.m_ParamValue)));
/* 160:    */       }
/* 161:    */     }
/* 162:311 */     System.arraycopy(this.m_ClassifierOptions, 0, options, start, this.m_ClassifierOptions.length);
/* 163:    */     
/* 164:    */ 
/* 165:    */ 
/* 166:315 */     return options;
/* 167:    */   }
/* 168:    */   
/* 169:    */   protected void findParamsByCrossValidation(int depth, Instances trainData, Random random)
/* 170:    */     throws Exception
/* 171:    */   {
/* 172:331 */     if (depth < this.m_CVParams.size())
/* 173:    */     {
/* 174:332 */       CVParameter cvParam = (CVParameter)this.m_CVParams.elementAt(depth);
/* 175:    */       double upper;
/* 176:335 */       switch ((int)(cvParam.m_Lower - cvParam.m_Upper + 0.5D))
/* 177:    */       {
/* 178:    */       case 1: 
/* 179:337 */         upper = this.m_NumAttributes;
/* 180:338 */         break;
/* 181:    */       case 2: 
/* 182:340 */         upper = this.m_TrainFoldSize;
/* 183:341 */         break;
/* 184:    */       default: 
/* 185:343 */         upper = cvParam.m_Upper;
/* 186:    */       }
/* 187:346 */       double increment = (upper - cvParam.m_Lower) / (cvParam.m_Steps - 1.0D);
/* 188:347 */       cvParam.m_ParamValue = cvParam.m_Lower;
/* 189:348 */       for (; cvParam.m_ParamValue <= upper; CVParameter.access$018(cvParam, increment)) {
/* 190:350 */         findParamsByCrossValidation(depth + 1, trainData, random);
/* 191:    */       }
/* 192:    */     }
/* 193:    */     else
/* 194:    */     {
/* 195:354 */       Evaluation evaluation = new Evaluation(trainData);
/* 196:    */       
/* 197:    */ 
/* 198:357 */       String[] options = createOptions();
/* 199:358 */       if (this.m_Debug)
/* 200:    */       {
/* 201:359 */         System.err.print("Setting options for " + this.m_Classifier.getClass().getName() + ":");
/* 202:361 */         for (int i = 0; i < options.length; i++) {
/* 203:362 */           System.err.print(" " + options[i]);
/* 204:    */         }
/* 205:364 */         System.err.println("");
/* 206:    */       }
/* 207:366 */       ((OptionHandler)this.m_Classifier).setOptions(options);
/* 208:367 */       for (int j = 0; j < this.m_NumFolds; j++)
/* 209:    */       {
/* 210:371 */         Instances train = trainData.trainCV(this.m_NumFolds, j, new Random(1L));
/* 211:372 */         Instances test = trainData.testCV(this.m_NumFolds, j);
/* 212:373 */         this.m_Classifier.buildClassifier(train);
/* 213:374 */         evaluation.setPriors(train);
/* 214:375 */         evaluation.evaluateModel(this.m_Classifier, test, new Object[0]);
/* 215:    */       }
/* 216:377 */       double error = evaluation.errorRate();
/* 217:378 */       if (this.m_Debug) {
/* 218:379 */         System.err.println("Cross-validated error rate: " + Utils.doubleToString(error, 6, 4));
/* 219:    */       }
/* 220:382 */       if ((this.m_BestPerformance == -99.0D) || (error < this.m_BestPerformance))
/* 221:    */       {
/* 222:384 */         this.m_BestPerformance = error;
/* 223:385 */         this.m_BestClassifierOptions = createOptions();
/* 224:    */       }
/* 225:    */     }
/* 226:    */   }
/* 227:    */   
/* 228:    */   public String globalInfo()
/* 229:    */   {
/* 230:396 */     return "Class for performing parameter selection by cross-validation for any classifier.\n\nFor more information, see:\n\n" + getTechnicalInformation().toString();
/* 231:    */   }
/* 232:    */   
/* 233:    */   public TechnicalInformation getTechnicalInformation()
/* 234:    */   {
/* 235:412 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.PHDTHESIS);
/* 236:413 */     result.setValue(TechnicalInformation.Field.AUTHOR, "R. Kohavi");
/* 237:414 */     result.setValue(TechnicalInformation.Field.YEAR, "1995");
/* 238:415 */     result.setValue(TechnicalInformation.Field.TITLE, "Wrappers for Performance Enhancement and Oblivious Decision Graphs");
/* 239:416 */     result.setValue(TechnicalInformation.Field.SCHOOL, "Stanford University");
/* 240:417 */     result.setValue(TechnicalInformation.Field.ADDRESS, "Department of Computer Science, Stanford University");
/* 241:    */     
/* 242:419 */     return result;
/* 243:    */   }
/* 244:    */   
/* 245:    */   public Enumeration<Option> listOptions()
/* 246:    */   {
/* 247:429 */     Vector<Option> newVector = new Vector(2);
/* 248:    */     
/* 249:431 */     newVector.addElement(new Option("\tNumber of folds used for cross validation (default 10).", "X", 1, "-X <number of folds>"));
/* 250:    */     
/* 251:    */ 
/* 252:434 */     newVector.addElement(new Option("\tClassifier parameter options.\n\teg: \"N 1 5 10\" Sets an optimisation parameter for the\n\tclassifier with name -N, with lower bound 1, upper bound\n\t5, and 10 optimisation steps. The upper bound may be the\n\tcharacter 'A' or 'I' to substitute the number of\n\tattributes or instances in the training data,\n\trespectively. This parameter may be supplied more than\n\tonce to optimise over several classifier options\n\tsimultaneously.", "P", 1, "-P <classifier parameter>"));
/* 253:    */     
/* 254:    */ 
/* 255:    */ 
/* 256:    */ 
/* 257:    */ 
/* 258:    */ 
/* 259:    */ 
/* 260:    */ 
/* 261:    */ 
/* 262:    */ 
/* 263:    */ 
/* 264:446 */     newVector.addAll(Collections.list(super.listOptions()));
/* 265:    */     
/* 266:448 */     return newVector.elements();
/* 267:    */   }
/* 268:    */   
/* 269:    */   public void setOptions(String[] options)
/* 270:    */     throws Exception
/* 271:    */   {
/* 272:501 */     String foldsString = Utils.getOption('X', options);
/* 273:502 */     if (foldsString.length() != 0) {
/* 274:503 */       setNumFolds(Integer.parseInt(foldsString));
/* 275:    */     } else {
/* 276:505 */       setNumFolds(10);
/* 277:    */     }
/* 278:509 */     this.m_CVParams = new Vector();
/* 279:    */     String cvParam;
/* 280:    */     do
/* 281:    */     {
/* 282:511 */       cvParam = Utils.getOption('P', options);
/* 283:512 */       if (cvParam.length() != 0) {
/* 284:513 */         addCVParameter(cvParam);
/* 285:    */       }
/* 286:515 */     } while (cvParam.length() != 0);
/* 287:517 */     super.setOptions(options);
/* 288:    */     
/* 289:519 */     Utils.checkForRemainingOptions(options);
/* 290:    */   }
/* 291:    */   
/* 292:    */   public String[] getOptions()
/* 293:    */   {
/* 294:529 */     Vector<String> options = new Vector();
/* 295:531 */     if (this.m_InitOptions != null) {
/* 296:    */       try
/* 297:    */       {
/* 298:533 */         ((OptionHandler)this.m_Classifier).setOptions((String[])this.m_InitOptions.clone());
/* 299:534 */         ((OptionHandler)this.m_Classifier).setOptions((String[])this.m_BestClassifierOptions.clone());
/* 300:    */       }
/* 301:    */       catch (Exception e)
/* 302:    */       {
/* 303:536 */         throw new RuntimeException("CVParameterSelection: could not set options in getOptions().");
/* 304:    */       }
/* 305:    */     }
/* 306:540 */     for (int i = 0; i < this.m_CVParams.size(); i++)
/* 307:    */     {
/* 308:541 */       options.add("-P");options.add("" + getCVParameter(i));
/* 309:    */     }
/* 310:543 */     options.add("-X");options.add("" + getNumFolds());
/* 311:    */     
/* 312:545 */     Collections.addAll(options, super.getOptions());
/* 313:    */     
/* 314:547 */     return (String[])options.toArray(new String[0]);
/* 315:    */   }
/* 316:    */   
/* 317:    */   public String[] getBestClassifierOptions()
/* 318:    */   {
/* 319:556 */     return (String[])this.m_BestClassifierOptions.clone();
/* 320:    */   }
/* 321:    */   
/* 322:    */   public Capabilities getCapabilities()
/* 323:    */   {
/* 324:565 */     Capabilities result = super.getCapabilities();
/* 325:    */     
/* 326:567 */     result.setMinimumNumberInstances(this.m_NumFolds);
/* 327:    */     
/* 328:569 */     return result;
/* 329:    */   }
/* 330:    */   
/* 331:    */   public void buildClassifier(Instances instances)
/* 332:    */     throws Exception
/* 333:    */   {
/* 334:581 */     getCapabilities().testWithFail(instances);
/* 335:    */     
/* 336:    */ 
/* 337:584 */     Instances trainData = new Instances(instances);
/* 338:585 */     trainData.deleteWithMissingClass();
/* 339:587 */     if (!(this.m_Classifier instanceof OptionHandler)) {
/* 340:588 */       throw new IllegalArgumentException("Base classifier should be OptionHandler.");
/* 341:    */     }
/* 342:590 */     this.m_InitOptions = ((OptionHandler)this.m_Classifier).getOptions();
/* 343:591 */     this.m_BestPerformance = -99.0D;
/* 344:592 */     this.m_NumAttributes = trainData.numAttributes();
/* 345:593 */     Random random = new Random(this.m_Seed);
/* 346:594 */     trainData.randomize(random);
/* 347:595 */     this.m_TrainFoldSize = trainData.trainCV(this.m_NumFolds, 0).numInstances();
/* 348:598 */     if (this.m_CVParams.size() == 0)
/* 349:    */     {
/* 350:599 */       this.m_Classifier.buildClassifier(trainData);
/* 351:600 */       this.m_BestClassifierOptions = this.m_InitOptions;
/* 352:601 */       return;
/* 353:    */     }
/* 354:604 */     if (trainData.classAttribute().isNominal()) {
/* 355:605 */       trainData.stratify(this.m_NumFolds);
/* 356:    */     }
/* 357:607 */     this.m_BestClassifierOptions = null;
/* 358:    */     
/* 359:    */ 
/* 360:    */ 
/* 361:611 */     this.m_ClassifierOptions = ((OptionHandler)this.m_Classifier).getOptions();
/* 362:612 */     for (int i = 0; i < this.m_CVParams.size(); i++) {
/* 363:613 */       Utils.getOption(((CVParameter)this.m_CVParams.elementAt(i)).m_ParamChar, this.m_ClassifierOptions);
/* 364:    */     }
/* 365:616 */     findParamsByCrossValidation(0, trainData, random);
/* 366:    */     
/* 367:618 */     String[] options = (String[])this.m_BestClassifierOptions.clone();
/* 368:619 */     ((OptionHandler)this.m_Classifier).setOptions(options);
/* 369:620 */     this.m_Classifier.buildClassifier(trainData);
/* 370:    */   }
/* 371:    */   
/* 372:    */   public double[] distributionForInstance(Instance instance)
/* 373:    */     throws Exception
/* 374:    */   {
/* 375:633 */     return this.m_Classifier.distributionForInstance(instance);
/* 376:    */   }
/* 377:    */   
/* 378:    */   public void addCVParameter(String cvParam)
/* 379:    */     throws Exception
/* 380:    */   {
/* 381:649 */     CVParameter newCV = new CVParameter(cvParam);
/* 382:    */     
/* 383:651 */     this.m_CVParams.addElement(newCV);
/* 384:    */   }
/* 385:    */   
/* 386:    */   public String getCVParameter(int index)
/* 387:    */   {
/* 388:662 */     if (this.m_CVParams.size() <= index) {
/* 389:663 */       return "";
/* 390:    */     }
/* 391:665 */     return ((CVParameter)this.m_CVParams.elementAt(index)).toString();
/* 392:    */   }
/* 393:    */   
/* 394:    */   public String CVParametersTipText()
/* 395:    */   {
/* 396:674 */     return "Sets the scheme parameters which are to be set by cross-validation.\nThe format for each string should be:\nparam_char lower_bound upper_bound number_of_steps\neg to search a parameter -P from 1 to 10 by increments of 1:\n    \"P 1 10 10\" ";
/* 397:    */   }
/* 398:    */   
/* 399:    */   public Object[] getCVParameters()
/* 400:    */   {
/* 401:689 */     Object[] CVParams = this.m_CVParams.toArray();
/* 402:    */     
/* 403:691 */     String[] params = new String[CVParams.length];
/* 404:693 */     for (int i = 0; i < CVParams.length; i++) {
/* 405:694 */       params[i] = CVParams[i].toString();
/* 406:    */     }
/* 407:696 */     return params;
/* 408:    */   }
/* 409:    */   
/* 410:    */   public void setCVParameters(Object[] params)
/* 411:    */     throws Exception
/* 412:    */   {
/* 413:708 */     Vector<CVParameter> backup = this.m_CVParams;
/* 414:709 */     this.m_CVParams = new Vector();
/* 415:711 */     for (int i = 0; i < params.length; i++) {
/* 416:    */       try
/* 417:    */       {
/* 418:713 */         addCVParameter((String)params[i]);
/* 419:    */       }
/* 420:    */       catch (Exception ex)
/* 421:    */       {
/* 422:715 */         this.m_CVParams = backup;throw ex;
/* 423:    */       }
/* 424:    */     }
/* 425:    */   }
/* 426:    */   
/* 427:    */   public String numFoldsTipText()
/* 428:    */   {
/* 429:725 */     return "Get the number of folds used for cross-validation.";
/* 430:    */   }
/* 431:    */   
/* 432:    */   public int getNumFolds()
/* 433:    */   {
/* 434:735 */     return this.m_NumFolds;
/* 435:    */   }
/* 436:    */   
/* 437:    */   public void setNumFolds(int numFolds)
/* 438:    */     throws Exception
/* 439:    */   {
/* 440:746 */     if (numFolds < 0) {
/* 441:747 */       throw new IllegalArgumentException("Stacking: Number of cross-validation folds must be positive.");
/* 442:    */     }
/* 443:750 */     this.m_NumFolds = numFolds;
/* 444:    */   }
/* 445:    */   
/* 446:    */   public int graphType()
/* 447:    */   {
/* 448:761 */     if ((this.m_Classifier instanceof Drawable)) {
/* 449:762 */       return ((Drawable)this.m_Classifier).graphType();
/* 450:    */     }
/* 451:764 */     return 0;
/* 452:    */   }
/* 453:    */   
/* 454:    */   public String graph()
/* 455:    */     throws Exception
/* 456:    */   {
/* 457:775 */     if ((this.m_Classifier instanceof Drawable)) {
/* 458:776 */       return ((Drawable)this.m_Classifier).graph();
/* 459:    */     }
/* 460:777 */     throw new Exception("Classifier: " + this.m_Classifier.getClass().getName() + " " + Utils.joinOptions(this.m_BestClassifierOptions) + " cannot be graphed");
/* 461:    */   }
/* 462:    */   
/* 463:    */   public String toString()
/* 464:    */   {
/* 465:790 */     if (this.m_InitOptions == null) {
/* 466:791 */       return "CVParameterSelection: No model built yet.";
/* 467:    */     }
/* 468:793 */     String result = "Cross-validated Parameter selection.\nClassifier: " + this.m_Classifier.getClass().getName() + "\n";
/* 469:    */     try
/* 470:    */     {
/* 471:796 */       for (int i = 0; i < this.m_CVParams.size(); i++)
/* 472:    */       {
/* 473:797 */         CVParameter cvParam = (CVParameter)this.m_CVParams.elementAt(i);
/* 474:798 */         result = result + "Cross-validation Parameter: '-" + cvParam.m_ParamChar + "'" + " ranged from " + cvParam.m_Lower + " to ";
/* 475:802 */         switch ((int)(cvParam.m_Lower - cvParam.m_Upper + 0.5D))
/* 476:    */         {
/* 477:    */         case 1: 
/* 478:804 */           result = result + this.m_NumAttributes;
/* 479:805 */           break;
/* 480:    */         case 2: 
/* 481:807 */           result = result + this.m_TrainFoldSize;
/* 482:808 */           break;
/* 483:    */         default: 
/* 484:810 */           result = result + cvParam.m_Upper;
/* 485:    */         }
/* 486:813 */         result = result + " with " + cvParam.m_Steps + " steps\n";
/* 487:    */       }
/* 488:    */     }
/* 489:    */     catch (Exception ex)
/* 490:    */     {
/* 491:816 */       result = result + ex.getMessage();
/* 492:    */     }
/* 493:818 */     result = result + "Classifier Options: " + Utils.joinOptions(this.m_BestClassifierOptions) + "\n\n" + this.m_Classifier.toString();
/* 494:    */     
/* 495:    */ 
/* 496:821 */     return result;
/* 497:    */   }
/* 498:    */   
/* 499:    */   public String toSummaryString()
/* 500:    */   {
/* 501:831 */     String result = "Selected values: " + Utils.joinOptions(this.m_BestClassifierOptions);
/* 502:    */     
/* 503:833 */     return result + '\n';
/* 504:    */   }
/* 505:    */   
/* 506:    */   public String getRevision()
/* 507:    */   {
/* 508:842 */     return RevisionUtils.extract("$Revision: 10141 $");
/* 509:    */   }
/* 510:    */   
/* 511:    */   public static void main(String[] argv)
/* 512:    */   {
/* 513:851 */     runClassifier(new CVParameterSelection(), argv);
/* 514:    */   }
/* 515:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.CVParameterSelection
 * JD-Core Version:    0.7.0.1
 */