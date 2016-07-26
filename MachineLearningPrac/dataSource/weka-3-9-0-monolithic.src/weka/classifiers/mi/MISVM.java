/*   1:    */ package weka.classifiers.mi;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.classifiers.AbstractClassifier;
/*   8:    */ import weka.classifiers.functions.SMO;
/*   9:    */ import weka.classifiers.functions.SMO.BinarySMO;
/*  10:    */ import weka.classifiers.functions.supportVector.Kernel;
/*  11:    */ import weka.classifiers.functions.supportVector.PolyKernel;
/*  12:    */ import weka.core.Capabilities;
/*  13:    */ import weka.core.Capabilities.Capability;
/*  14:    */ import weka.core.Instance;
/*  15:    */ import weka.core.Instances;
/*  16:    */ import weka.core.MultiInstanceCapabilitiesHandler;
/*  17:    */ import weka.core.Option;
/*  18:    */ import weka.core.OptionHandler;
/*  19:    */ import weka.core.RevisionUtils;
/*  20:    */ import weka.core.SelectedTag;
/*  21:    */ import weka.core.Tag;
/*  22:    */ import weka.core.TechnicalInformation;
/*  23:    */ import weka.core.TechnicalInformation.Field;
/*  24:    */ import weka.core.TechnicalInformation.Type;
/*  25:    */ import weka.core.TechnicalInformationHandler;
/*  26:    */ import weka.core.Utils;
/*  27:    */ import weka.filters.Filter;
/*  28:    */ import weka.filters.unsupervised.attribute.MultiInstanceToPropositional;
/*  29:    */ import weka.filters.unsupervised.attribute.Normalize;
/*  30:    */ import weka.filters.unsupervised.attribute.Standardize;
/*  31:    */ import weka.filters.unsupervised.instance.SparseToNonSparse;
/*  32:    */ 
/*  33:    */ public class MISVM
/*  34:    */   extends AbstractClassifier
/*  35:    */   implements OptionHandler, MultiInstanceCapabilitiesHandler, TechnicalInformationHandler
/*  36:    */ {
/*  37:    */   static final long serialVersionUID = 7622231064035278145L;
/*  38:    */   protected Filter m_SparseFilter;
/*  39:    */   protected SVM m_SVM;
/*  40:    */   protected Kernel m_kernel;
/*  41:    */   protected double m_C;
/*  42:    */   protected Filter m_Filter;
/*  43:    */   protected int m_filterType;
/*  44:    */   public static final int FILTER_NORMALIZE = 0;
/*  45:    */   public static final int FILTER_STANDARDIZE = 1;
/*  46:    */   public static final int FILTER_NONE = 2;
/*  47:184 */   public static final Tag[] TAGS_FILTER = { new Tag(0, "Normalize training data"), new Tag(1, "Standardize training data"), new Tag(2, "No normalization/standardization") };
/*  48:    */   protected int m_MaxIterations;
/*  49:    */   protected MultiInstanceToPropositional m_ConvertToProp;
/*  50:    */   
/*  51:    */   public MISVM()
/*  52:    */   {
/*  53:160 */     this.m_SparseFilter = new SparseToNonSparse();
/*  54:    */     
/*  55:    */ 
/*  56:    */ 
/*  57:    */ 
/*  58:    */ 
/*  59:166 */     this.m_kernel = new PolyKernel();
/*  60:    */     
/*  61:    */ 
/*  62:169 */     this.m_C = 1.0D;
/*  63:    */     
/*  64:    */ 
/*  65:172 */     this.m_Filter = null;
/*  66:    */     
/*  67:    */ 
/*  68:175 */     this.m_filterType = 0;
/*  69:    */     
/*  70:    */ 
/*  71:    */ 
/*  72:    */ 
/*  73:    */ 
/*  74:    */ 
/*  75:    */ 
/*  76:    */ 
/*  77:    */ 
/*  78:    */ 
/*  79:    */ 
/*  80:    */ 
/*  81:    */ 
/*  82:    */ 
/*  83:190 */     this.m_MaxIterations = 500;
/*  84:    */     
/*  85:    */ 
/*  86:193 */     this.m_ConvertToProp = new MultiInstanceToPropositional();
/*  87:    */   }
/*  88:    */   
/*  89:    */   public String globalInfo()
/*  90:    */   {
/*  91:202 */     return "Implements Stuart Andrews' mi_SVM (Maximum pattern Margin Formulation of MIL). Applying weka.classifiers.functions.SMO to solve multiple instances problem.\nThe algorithm first assign the bag label to each instance in the bag as its initial class label.  After that applying SMO to compute SVM solution for all instances in positive bags And then reassign the class label of each instance in the positive bag according to the SVM result Keep on iteration until labels do not change anymore.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*  92:    */   }
/*  93:    */   
/*  94:    */   public TechnicalInformation getTechnicalInformation()
/*  95:    */   {
/*  96:225 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  97:226 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Stuart Andrews and Ioannis Tsochantaridis and Thomas Hofmann");
/*  98:    */     
/*  99:228 */     result.setValue(TechnicalInformation.Field.YEAR, "2003");
/* 100:229 */     result.setValue(TechnicalInformation.Field.TITLE, "Support Vector Machines for Multiple-Instance Learning");
/* 101:    */     
/* 102:231 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Advances in Neural Information Processing Systems 15");
/* 103:    */     
/* 104:233 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "MIT Press");
/* 105:234 */     result.setValue(TechnicalInformation.Field.PAGES, "561-568");
/* 106:    */     
/* 107:236 */     return result;
/* 108:    */   }
/* 109:    */   
/* 110:    */   public Enumeration<Option> listOptions()
/* 111:    */   {
/* 112:247 */     Vector<Option> result = new Vector();
/* 113:    */     
/* 114:249 */     result.addElement(new Option("\tThe complexity constant C. (default 1)", "C", 1, "-C <double>"));
/* 115:    */     
/* 116:    */ 
/* 117:252 */     result.addElement(new Option("\tWhether to 0=normalize/1=standardize/2=neither.\n\t(default: 0=normalize)", "N", 1, "-N <default 0>"));
/* 118:    */     
/* 119:    */ 
/* 120:    */ 
/* 121:256 */     result.addElement(new Option("\tThe maximum number of iterations to perform.\n\t(default: 500)", "I", 1, "-I <num>"));
/* 122:    */     
/* 123:    */ 
/* 124:    */ 
/* 125:260 */     result.addElement(new Option("\tThe Kernel to use.\n\t(default: weka.classifiers.functions.supportVector.PolyKernel)", "K", 1, "-K <classname and parameters>"));
/* 126:    */     
/* 127:    */ 
/* 128:    */ 
/* 129:264 */     result.addAll(Collections.list(super.listOptions()));
/* 130:    */     
/* 131:266 */     result.addElement(new Option("", "", 0, "\nOptions specific to kernel " + getKernel().getClass().getName() + ":"));
/* 132:    */     
/* 133:    */ 
/* 134:269 */     result.addAll(Collections.list(getKernel().listOptions()));
/* 135:    */     
/* 136:    */ 
/* 137:272 */     return result.elements();
/* 138:    */   }
/* 139:    */   
/* 140:    */   public void setOptions(String[] options)
/* 141:    */     throws Exception
/* 142:    */   {
/* 143:350 */     String tmpStr = Utils.getOption('C', options);
/* 144:351 */     if (tmpStr.length() != 0) {
/* 145:352 */       setC(Double.parseDouble(tmpStr));
/* 146:    */     } else {
/* 147:354 */       setC(1.0D);
/* 148:    */     }
/* 149:357 */     tmpStr = Utils.getOption('N', options);
/* 150:358 */     if (tmpStr.length() != 0) {
/* 151:359 */       setFilterType(new SelectedTag(Integer.parseInt(tmpStr), TAGS_FILTER));
/* 152:    */     } else {
/* 153:361 */       setFilterType(new SelectedTag(0, TAGS_FILTER));
/* 154:    */     }
/* 155:364 */     tmpStr = Utils.getOption('I', options);
/* 156:365 */     if (tmpStr.length() != 0) {
/* 157:366 */       setMaxIterations(Integer.parseInt(tmpStr));
/* 158:    */     } else {
/* 159:368 */       setMaxIterations(500);
/* 160:    */     }
/* 161:371 */     tmpStr = Utils.getOption('K', options);
/* 162:372 */     String[] tmpOptions = Utils.splitOptions(tmpStr);
/* 163:373 */     if (tmpOptions.length != 0)
/* 164:    */     {
/* 165:374 */       tmpStr = tmpOptions[0];
/* 166:375 */       tmpOptions[0] = "";
/* 167:376 */       setKernel(Kernel.forName(tmpStr, tmpOptions));
/* 168:    */     }
/* 169:379 */     super.setOptions(options);
/* 170:    */     
/* 171:381 */     Utils.checkForRemainingOptions(tmpOptions);
/* 172:    */   }
/* 173:    */   
/* 174:    */   public String[] getOptions()
/* 175:    */   {
/* 176:392 */     Vector<String> result = new Vector();
/* 177:    */     
/* 178:394 */     result.add("-C");
/* 179:395 */     result.add("" + getC());
/* 180:    */     
/* 181:397 */     result.add("-N");
/* 182:398 */     result.add("" + this.m_filterType);
/* 183:    */     
/* 184:400 */     result.add("-K");
/* 185:401 */     result.add("" + getKernel().getClass().getName() + " " + Utils.joinOptions(getKernel().getOptions()));
/* 186:    */     
/* 187:    */ 
/* 188:404 */     Collections.addAll(result, super.getOptions());
/* 189:    */     
/* 190:406 */     return (String[])result.toArray(new String[result.size()]);
/* 191:    */   }
/* 192:    */   
/* 193:    */   public String kernelTipText()
/* 194:    */   {
/* 195:416 */     return "The kernel to use.";
/* 196:    */   }
/* 197:    */   
/* 198:    */   public Kernel getKernel()
/* 199:    */   {
/* 200:425 */     return this.m_kernel;
/* 201:    */   }
/* 202:    */   
/* 203:    */   public void setKernel(Kernel value)
/* 204:    */   {
/* 205:434 */     this.m_kernel = value;
/* 206:    */   }
/* 207:    */   
/* 208:    */   public String filterTypeTipText()
/* 209:    */   {
/* 210:444 */     return "The filter type for transforming the training data.";
/* 211:    */   }
/* 212:    */   
/* 213:    */   public void setFilterType(SelectedTag newType)
/* 214:    */   {
/* 215:455 */     if (newType.getTags() == TAGS_FILTER) {
/* 216:456 */       this.m_filterType = newType.getSelectedTag().getID();
/* 217:    */     }
/* 218:    */   }
/* 219:    */   
/* 220:    */   public SelectedTag getFilterType()
/* 221:    */   {
/* 222:468 */     return new SelectedTag(this.m_filterType, TAGS_FILTER);
/* 223:    */   }
/* 224:    */   
/* 225:    */   public String cTipText()
/* 226:    */   {
/* 227:478 */     return "The value for C.";
/* 228:    */   }
/* 229:    */   
/* 230:    */   public double getC()
/* 231:    */   {
/* 232:488 */     return this.m_C;
/* 233:    */   }
/* 234:    */   
/* 235:    */   public void setC(double v)
/* 236:    */   {
/* 237:497 */     this.m_C = v;
/* 238:    */   }
/* 239:    */   
/* 240:    */   public String maxIterationsTipText()
/* 241:    */   {
/* 242:507 */     return "The maximum number of iterations to perform.";
/* 243:    */   }
/* 244:    */   
/* 245:    */   public int getMaxIterations()
/* 246:    */   {
/* 247:516 */     return this.m_MaxIterations;
/* 248:    */   }
/* 249:    */   
/* 250:    */   public void setMaxIterations(int value)
/* 251:    */   {
/* 252:525 */     if (value < 1) {
/* 253:526 */       System.out.println("At least 1 iteration is necessary (provided: " + value + ")!");
/* 254:    */     } else {
/* 255:529 */       this.m_MaxIterations = value;
/* 256:    */     }
/* 257:    */   }
/* 258:    */   
/* 259:    */   private class SVM
/* 260:    */     extends SMO
/* 261:    */   {
/* 262:    */     static final long serialVersionUID = -8325638229658828931L;
/* 263:    */     
/* 264:    */     protected SVM() {}
/* 265:    */     
/* 266:    */     protected double output(int index, Instance inst)
/* 267:    */       throws Exception
/* 268:    */     {
/* 269:557 */       double output = 0.0D;
/* 270:558 */       output = this.m_classifiers[0][1].SVMOutput(index, inst);
/* 271:559 */       return output;
/* 272:    */     }
/* 273:    */     
/* 274:    */     public String getRevision()
/* 275:    */     {
/* 276:569 */       return RevisionUtils.extract("$Revision: 10369 $");
/* 277:    */     }
/* 278:    */   }
/* 279:    */   
/* 280:    */   public Capabilities getCapabilities()
/* 281:    */   {
/* 282:580 */     Capabilities result = super.getCapabilities();
/* 283:581 */     result.disableAll();
/* 284:    */     
/* 285:    */ 
/* 286:584 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 287:585 */     result.enable(Capabilities.Capability.RELATIONAL_ATTRIBUTES);
/* 288:    */     
/* 289:    */ 
/* 290:588 */     result.disableAllClasses();
/* 291:589 */     result.disableAllClassDependencies();
/* 292:590 */     result.enable(Capabilities.Capability.BINARY_CLASS);
/* 293:591 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 294:    */     
/* 295:    */ 
/* 296:594 */     result.enable(Capabilities.Capability.ONLY_MULTIINSTANCE);
/* 297:    */     
/* 298:596 */     return result;
/* 299:    */   }
/* 300:    */   
/* 301:    */   public Capabilities getMultiInstanceCapabilities()
/* 302:    */   {
/* 303:611 */     SVM classifier = null;
/* 304:612 */     Capabilities result = null;
/* 305:    */     try
/* 306:    */     {
/* 307:615 */       classifier = new SVM();
/* 308:616 */       classifier.setKernel(Kernel.makeCopy(getKernel()));
/* 309:617 */       result = classifier.getCapabilities();
/* 310:618 */       result.setOwner(this);
/* 311:    */     }
/* 312:    */     catch (Exception e)
/* 313:    */     {
/* 314:620 */       e.printStackTrace();
/* 315:    */     }
/* 316:624 */     result.disableAllClasses();
/* 317:625 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 318:    */     
/* 319:627 */     return result;
/* 320:    */   }
/* 321:    */   
/* 322:    */   public void buildClassifier(Instances train)
/* 323:    */     throws Exception
/* 324:    */   {
/* 325:641 */     getCapabilities().testWithFail(train);
/* 326:    */     
/* 327:    */ 
/* 328:644 */     train = new Instances(train);
/* 329:645 */     train.deleteWithMissingClass();
/* 330:    */     
/* 331:647 */     int numBags = train.numInstances();
/* 332:648 */     int[] bagSize = new int[numBags];
/* 333:649 */     int[] classes = new int[numBags];
/* 334:    */     
/* 335:651 */     Vector<Double> instLabels = new Vector();
/* 336:    */     
/* 337:    */ 
/* 338:654 */     Vector<Double> pre_instLabels = new Vector();
/* 339:656 */     for (int h = 0; h < numBags; h++)
/* 340:    */     {
/* 341:657 */       classes[h] = ((int)train.instance(h).classValue());
/* 342:658 */       bagSize[h] = train.instance(h).relationalValue(1).numInstances();
/* 343:659 */       for (int i = 0; i < bagSize[h]; i++) {
/* 344:660 */         instLabels.addElement(new Double(classes[h]));
/* 345:    */       }
/* 346:    */     }
/* 347:665 */     this.m_ConvertToProp.setWeightMethod(new SelectedTag(1, MultiInstanceToPropositional.TAGS_WEIGHTMETHOD));
/* 348:    */     
/* 349:    */ 
/* 350:668 */     this.m_ConvertToProp.setInputFormat(train);
/* 351:669 */     train = Filter.useFilter(train, this.m_ConvertToProp);
/* 352:670 */     train.deleteAttributeAt(0);
/* 353:672 */     if (this.m_filterType == 1) {
/* 354:673 */       this.m_Filter = new Standardize();
/* 355:674 */     } else if (this.m_filterType == 0) {
/* 356:675 */       this.m_Filter = new Normalize();
/* 357:    */     } else {
/* 358:677 */       this.m_Filter = null;
/* 359:    */     }
/* 360:680 */     if (this.m_Filter != null)
/* 361:    */     {
/* 362:681 */       this.m_Filter.setInputFormat(train);
/* 363:682 */       train = Filter.useFilter(train, this.m_Filter);
/* 364:    */     }
/* 365:685 */     if (this.m_Debug) {
/* 366:686 */       System.out.println("\nIteration History...");
/* 367:    */     }
/* 368:689 */     if (getDebug()) {
/* 369:690 */       System.out.println("\nstart building model ...");
/* 370:    */     }
/* 371:695 */     Vector<Integer> max_index = new Vector();
/* 372:696 */     Instance inst = null;
/* 373:    */     
/* 374:698 */     int loopNum = 0;
/* 375:    */     do
/* 376:    */     {
/* 377:700 */       loopNum++;
/* 378:701 */       int index = -1;
/* 379:702 */       if (this.m_Debug) {
/* 380:703 */         System.out.println("=====================loop: " + loopNum);
/* 381:    */       }
/* 382:707 */       pre_instLabels = (Vector)instLabels.clone();
/* 383:    */       
/* 384:    */ 
/* 385:710 */       this.m_SVM = new SVM();
/* 386:711 */       this.m_SVM.setC(getC());
/* 387:712 */       this.m_SVM.setKernel(Kernel.makeCopy(getKernel()));
/* 388:    */       
/* 389:    */ 
/* 390:715 */       this.m_SVM.setFilterType(new SelectedTag(2, TAGS_FILTER));
/* 391:    */       
/* 392:717 */       this.m_SVM.buildClassifier(train);
/* 393:719 */       for (int h = 0; h < numBags; h++) {
/* 394:720 */         if (classes[h] == 1)
/* 395:    */         {
/* 396:721 */           if (this.m_Debug) {
/* 397:722 */             System.out.println("--------------- " + h + " ----------------");
/* 398:    */           }
/* 399:724 */           double sum = 0.0D;
/* 400:727 */           for (int i = 0; i < bagSize[h]; i++)
/* 401:    */           {
/* 402:728 */             index++;
/* 403:    */             
/* 404:730 */             inst = train.instance(index);
/* 405:731 */             double output = this.m_SVM.output(-1, inst);
/* 406:732 */             if (output <= 0.0D)
/* 407:    */             {
/* 408:733 */               if (inst.classValue() == 1.0D)
/* 409:    */               {
/* 410:734 */                 train.instance(index).setClassValue(0.0D);
/* 411:735 */                 instLabels.set(index, new Double(0.0D));
/* 412:737 */                 if (this.m_Debug) {
/* 413:738 */                   System.out.println(index + "- changed to 0");
/* 414:    */                 }
/* 415:    */               }
/* 416:    */             }
/* 417:742 */             else if (inst.classValue() == 0.0D)
/* 418:    */             {
/* 419:743 */               train.instance(index).setClassValue(1.0D);
/* 420:744 */               instLabels.set(index, new Double(1.0D));
/* 421:746 */               if (this.m_Debug) {
/* 422:747 */                 System.out.println(index + "+ changed to 1");
/* 423:    */               }
/* 424:    */             }
/* 425:751 */             sum += train.instance(index).classValue();
/* 426:    */           }
/* 427:759 */           if (sum == 0.0D)
/* 428:    */           {
/* 429:761 */             double max_output = -1.797693134862316E+308D;
/* 430:762 */             max_index.clear();
/* 431:763 */             for (int j = index - bagSize[h] + 1; j < index + 1; j++)
/* 432:    */             {
/* 433:764 */               inst = train.instance(j);
/* 434:765 */               double output = this.m_SVM.output(-1, inst);
/* 435:766 */               if (max_output < output)
/* 436:    */               {
/* 437:767 */                 max_output = output;
/* 438:768 */                 max_index.clear();
/* 439:769 */                 max_index.add(new Integer(j));
/* 440:    */               }
/* 441:770 */               else if (max_output == output)
/* 442:    */               {
/* 443:771 */                 max_index.add(new Integer(j));
/* 444:    */               }
/* 445:    */             }
/* 446:776 */             for (int vecIndex = 0; vecIndex < max_index.size(); vecIndex++)
/* 447:    */             {
/* 448:777 */               Integer i = (Integer)max_index.get(vecIndex);
/* 449:778 */               train.instance(i.intValue()).setClassValue(1.0D);
/* 450:779 */               instLabels.set(i.intValue(), new Double(1.0D));
/* 451:781 */               if (this.m_Debug) {
/* 452:782 */                 System.out.println("##change to 1 ###outpput: " + max_output + " max_index: " + i + " bag: " + h);
/* 453:    */               }
/* 454:    */             }
/* 455:    */           }
/* 456:    */         }
/* 457:    */         else
/* 458:    */         {
/* 459:789 */           index += bagSize[h];
/* 460:    */         }
/* 461:    */       }
/* 462:792 */     } while ((!instLabels.equals(pre_instLabels)) && (loopNum < this.m_MaxIterations));
/* 463:794 */     if (getDebug()) {
/* 464:795 */       System.out.println("finish building model.");
/* 465:    */     }
/* 466:    */   }
/* 467:    */   
/* 468:    */   public double[] distributionForInstance(Instance exmp)
/* 469:    */     throws Exception
/* 470:    */   {
/* 471:809 */     double sum = 0.0D;
/* 472:    */     
/* 473:811 */     double[] distribution = new double[2];
/* 474:    */     
/* 475:813 */     Instances testData = new Instances(exmp.dataset(), 0);
/* 476:814 */     testData.add(exmp);
/* 477:    */     
/* 478:    */ 
/* 479:817 */     testData = Filter.useFilter(testData, this.m_ConvertToProp);
/* 480:818 */     testData.deleteAttributeAt(0);
/* 481:820 */     if (this.m_Filter != null) {
/* 482:821 */       testData = Filter.useFilter(testData, this.m_Filter);
/* 483:    */     }
/* 484:824 */     for (int j = 0; j < testData.numInstances(); j++)
/* 485:    */     {
/* 486:825 */       Instance inst = testData.instance(j);
/* 487:826 */       double output = this.m_SVM.output(-1, inst);
/* 488:    */       double classValue;
/* 489:    */       double classValue;
/* 490:827 */       if (output <= 0.0D) {
/* 491:828 */         classValue = 0.0D;
/* 492:    */       } else {
/* 493:830 */         classValue = 1.0D;
/* 494:    */       }
/* 495:832 */       sum += classValue;
/* 496:    */     }
/* 497:834 */     if (sum == 0.0D) {
/* 498:835 */       distribution[0] = 1.0D;
/* 499:    */     } else {
/* 500:837 */       distribution[0] = 0.0D;
/* 501:    */     }
/* 502:839 */     distribution[1] = (1.0D - distribution[0]);
/* 503:    */     
/* 504:841 */     return distribution;
/* 505:    */   }
/* 506:    */   
/* 507:    */   public String getRevision()
/* 508:    */   {
/* 509:851 */     return RevisionUtils.extract("$Revision: 10369 $");
/* 510:    */   }
/* 511:    */   
/* 512:    */   public static void main(String[] argv)
/* 513:    */   {
/* 514:861 */     runClassifier(new MISVM(), argv);
/* 515:    */   }
/* 516:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.MISVM
 * JD-Core Version:    0.7.0.1
 */