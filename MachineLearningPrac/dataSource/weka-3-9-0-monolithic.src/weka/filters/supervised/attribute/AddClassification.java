/*   1:    */ package weka.filters.supervised.attribute;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.FileInputStream;
/*   5:    */ import java.io.FileNotFoundException;
/*   6:    */ import java.io.ObjectInputStream;
/*   7:    */ import java.io.PrintStream;
/*   8:    */ import java.util.ArrayList;
/*   9:    */ import java.util.Collections;
/*  10:    */ import java.util.Enumeration;
/*  11:    */ import java.util.Vector;
/*  12:    */ import weka.classifiers.AbstractClassifier;
/*  13:    */ import weka.classifiers.Classifier;
/*  14:    */ import weka.classifiers.rules.ZeroR;
/*  15:    */ import weka.core.Attribute;
/*  16:    */ import weka.core.Capabilities;
/*  17:    */ import weka.core.DenseInstance;
/*  18:    */ import weka.core.Instance;
/*  19:    */ import weka.core.Instances;
/*  20:    */ import weka.core.Option;
/*  21:    */ import weka.core.OptionHandler;
/*  22:    */ import weka.core.RevisionUtils;
/*  23:    */ import weka.core.SparseInstance;
/*  24:    */ import weka.core.Utils;
/*  25:    */ import weka.core.WekaException;
/*  26:    */ import weka.filters.SimpleBatchFilter;
/*  27:    */ 
/*  28:    */ public class AddClassification
/*  29:    */   extends SimpleBatchFilter
/*  30:    */ {
/*  31:    */   private static final long serialVersionUID = -1931467132568441909L;
/*  32:115 */   protected Classifier m_Classifier = new ZeroR();
/*  33:118 */   protected File m_SerializedClassifierFile = new File(System.getProperty("user.dir"));
/*  34:122 */   protected Classifier m_ActualClassifier = null;
/*  35:125 */   protected Instances m_SerializedHeader = null;
/*  36:128 */   protected boolean m_OutputClassification = false;
/*  37:131 */   protected boolean m_RemoveOldClass = false;
/*  38:134 */   protected boolean m_OutputDistribution = false;
/*  39:137 */   protected boolean m_OutputErrorFlag = false;
/*  40:    */   
/*  41:    */   public String globalInfo()
/*  42:    */   {
/*  43:147 */     return "A filter for adding the classification, the class distribution and an error flag to a dataset with a classifier. The classifier is either trained on the data itself or provided as serialized model.";
/*  44:    */   }
/*  45:    */   
/*  46:    */   public Enumeration<Option> listOptions()
/*  47:    */   {
/*  48:160 */     Vector<Option> result = new Vector();
/*  49:    */     
/*  50:162 */     result.addElement(new Option("\tFull class name of classifier to use, followed\n\tby scheme options. eg:\n\t\t\"weka.classifiers.bayes.NaiveBayes -D\"\n\t(default: weka.classifiers.rules.ZeroR)", "W", 1, "-W <classifier specification>"));
/*  51:    */     
/*  52:    */ 
/*  53:    */ 
/*  54:    */ 
/*  55:    */ 
/*  56:    */ 
/*  57:169 */     result.addElement(new Option("\tInstead of training a classifier on the data, one can also provide\n\ta serialized model and use that for tagging the data.", "serialized", 1, "-serialized <file>"));
/*  58:    */     
/*  59:    */ 
/*  60:    */ 
/*  61:    */ 
/*  62:174 */     result.addElement(new Option("\tAdds an attribute with the actual classification.\n\t(default: off)", "classification", 0, "-classification"));
/*  63:    */     
/*  64:    */ 
/*  65:    */ 
/*  66:178 */     result.addElement(new Option("\tRemoves the old class attribute.\n\t(default: off)", "remove-old-class", 0, "-remove-old-class"));
/*  67:    */     
/*  68:    */ 
/*  69:181 */     result.addElement(new Option("\tAdds attributes with the distribution for all classes \n\t(for numeric classes this will be identical to the attribute \n\toutput with '-classification').\n\t(default: off)", "distribution", 0, "-distribution"));
/*  70:    */     
/*  71:    */ 
/*  72:    */ 
/*  73:    */ 
/*  74:    */ 
/*  75:187 */     result.addElement(new Option("\tAdds an attribute indicating whether the classifier output \n\ta wrong classification (for numeric classes this is the numeric \n\tdifference).\n\t(default: off)", "error", 0, "-error"));
/*  76:    */     
/*  77:    */ 
/*  78:    */ 
/*  79:    */ 
/*  80:    */ 
/*  81:193 */     result.addAll(Collections.list(super.listOptions()));
/*  82:    */     
/*  83:195 */     return result.elements();
/*  84:    */   }
/*  85:    */   
/*  86:    */   public void setOptions(String[] options)
/*  87:    */     throws Exception
/*  88:    */   {
/*  89:264 */     setOutputClassification(Utils.getFlag("classification", options));
/*  90:    */     
/*  91:266 */     setRemoveOldClass(Utils.getFlag("remove-old-class", options));
/*  92:    */     
/*  93:268 */     setOutputDistribution(Utils.getFlag("distribution", options));
/*  94:    */     
/*  95:270 */     setOutputErrorFlag(Utils.getFlag("error", options));
/*  96:    */     
/*  97:272 */     boolean serializedModel = false;
/*  98:273 */     String tmpStr = Utils.getOption("serialized", options);
/*  99:274 */     if (tmpStr.length() != 0)
/* 100:    */     {
/* 101:275 */       File file = new File(tmpStr);
/* 102:276 */       if (!file.exists()) {
/* 103:277 */         throw new FileNotFoundException("File '" + file.getAbsolutePath() + "' not found!");
/* 104:    */       }
/* 105:280 */       if (file.isDirectory()) {
/* 106:281 */         throw new FileNotFoundException("'" + file.getAbsolutePath() + "' points to a directory not a file!");
/* 107:    */       }
/* 108:284 */       setSerializedClassifierFile(file);
/* 109:285 */       serializedModel = true;
/* 110:    */     }
/* 111:    */     else
/* 112:    */     {
/* 113:287 */       setSerializedClassifierFile(null);
/* 114:    */     }
/* 115:290 */     if (!serializedModel)
/* 116:    */     {
/* 117:291 */       tmpStr = Utils.getOption('W', options);
/* 118:292 */       if (tmpStr.length() == 0) {
/* 119:293 */         tmpStr = ZeroR.class.getName();
/* 120:    */       }
/* 121:295 */       String[] tmpOptions = Utils.splitOptions(tmpStr);
/* 122:296 */       if (tmpOptions.length == 0) {
/* 123:297 */         throw new Exception("Invalid classifier specification string");
/* 124:    */       }
/* 125:299 */       tmpStr = tmpOptions[0];
/* 126:300 */       tmpOptions[0] = "";
/* 127:301 */       setClassifier(AbstractClassifier.forName(tmpStr, tmpOptions));
/* 128:    */     }
/* 129:304 */     super.setOptions(options);
/* 130:    */     
/* 131:306 */     Utils.checkForRemainingOptions(options);
/* 132:    */   }
/* 133:    */   
/* 134:    */   public String[] getOptions()
/* 135:    */   {
/* 136:317 */     Vector<String> result = new Vector();
/* 137:319 */     if (getOutputClassification()) {
/* 138:320 */       result.add("-classification");
/* 139:    */     }
/* 140:323 */     if (getRemoveOldClass()) {
/* 141:324 */       result.add("-remove-old-class");
/* 142:    */     }
/* 143:327 */     if (getOutputDistribution()) {
/* 144:328 */       result.add("-distribution");
/* 145:    */     }
/* 146:331 */     if (getOutputErrorFlag()) {
/* 147:332 */       result.add("-error");
/* 148:    */     }
/* 149:335 */     File file = getSerializedClassifierFile();
/* 150:336 */     if ((file != null) && (!file.isDirectory()))
/* 151:    */     {
/* 152:337 */       result.add("-serialized");
/* 153:338 */       result.add(file.getAbsolutePath());
/* 154:    */     }
/* 155:    */     else
/* 156:    */     {
/* 157:340 */       result.add("-W");
/* 158:341 */       result.add(getClassifierSpec());
/* 159:    */     }
/* 160:344 */     Collections.addAll(result, super.getOptions());
/* 161:    */     
/* 162:346 */     return (String[])result.toArray(new String[result.size()]);
/* 163:    */   }
/* 164:    */   
/* 165:    */   protected void reset()
/* 166:    */   {
/* 167:356 */     super.reset();
/* 168:    */     
/* 169:358 */     this.m_ActualClassifier = null;
/* 170:359 */     this.m_SerializedHeader = null;
/* 171:    */   }
/* 172:    */   
/* 173:    */   protected Classifier getActualClassifier()
/* 174:    */   {
/* 175:372 */     if (this.m_ActualClassifier == null) {
/* 176:    */       try
/* 177:    */       {
/* 178:374 */         File file = getSerializedClassifierFile();
/* 179:375 */         if (!file.isDirectory())
/* 180:    */         {
/* 181:376 */           ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
/* 182:377 */           this.m_ActualClassifier = ((Classifier)ois.readObject());
/* 183:378 */           this.m_SerializedHeader = null;
/* 184:    */           try
/* 185:    */           {
/* 186:381 */             this.m_SerializedHeader = ((Instances)ois.readObject());
/* 187:    */           }
/* 188:    */           catch (Exception e)
/* 189:    */           {
/* 190:384 */             this.m_SerializedHeader = null;
/* 191:    */           }
/* 192:386 */           ois.close();
/* 193:    */         }
/* 194:    */         else
/* 195:    */         {
/* 196:388 */           this.m_ActualClassifier = AbstractClassifier.makeCopy(this.m_Classifier);
/* 197:    */         }
/* 198:    */       }
/* 199:    */       catch (Exception e)
/* 200:    */       {
/* 201:391 */         this.m_ActualClassifier = null;
/* 202:392 */         System.err.println("Failed to instantiate classifier:");
/* 203:393 */         e.printStackTrace();
/* 204:    */       }
/* 205:    */     }
/* 206:397 */     return this.m_ActualClassifier;
/* 207:    */   }
/* 208:    */   
/* 209:    */   public Capabilities getCapabilities()
/* 210:    */   {
/* 211:    */     Capabilities result;
/* 212:410 */     if (getActualClassifier() == null)
/* 213:    */     {
/* 214:411 */       Capabilities result = super.getCapabilities();
/* 215:412 */       result.disableAll();
/* 216:    */     }
/* 217:    */     else
/* 218:    */     {
/* 219:414 */       result = getActualClassifier().getCapabilities();
/* 220:    */     }
/* 221:417 */     result.setMinimumNumberInstances(0);
/* 222:    */     
/* 223:419 */     return result;
/* 224:    */   }
/* 225:    */   
/* 226:    */   public String classifierTipText()
/* 227:    */   {
/* 228:429 */     return "The classifier to use for classification.";
/* 229:    */   }
/* 230:    */   
/* 231:    */   public void setClassifier(Classifier value)
/* 232:    */   {
/* 233:438 */     this.m_Classifier = value;
/* 234:    */   }
/* 235:    */   
/* 236:    */   public Classifier getClassifier()
/* 237:    */   {
/* 238:447 */     return this.m_Classifier;
/* 239:    */   }
/* 240:    */   
/* 241:    */   protected String getClassifierSpec()
/* 242:    */   {
/* 243:460 */     Classifier c = getClassifier();
/* 244:461 */     String result = c.getClass().getName();
/* 245:462 */     if ((c instanceof OptionHandler)) {
/* 246:463 */       result = result + " " + Utils.joinOptions(((OptionHandler)c).getOptions());
/* 247:    */     }
/* 248:466 */     return result;
/* 249:    */   }
/* 250:    */   
/* 251:    */   public String serializedClassifierFileTipText()
/* 252:    */   {
/* 253:476 */     return "A file containing the serialized model of a trained classifier.";
/* 254:    */   }
/* 255:    */   
/* 256:    */   public File getSerializedClassifierFile()
/* 257:    */   {
/* 258:486 */     return this.m_SerializedClassifierFile;
/* 259:    */   }
/* 260:    */   
/* 261:    */   public void setSerializedClassifierFile(File value)
/* 262:    */   {
/* 263:497 */     if ((value == null) || (!value.exists())) {
/* 264:498 */       value = new File(System.getProperty("user.dir"));
/* 265:    */     }
/* 266:501 */     this.m_SerializedClassifierFile = value;
/* 267:    */   }
/* 268:    */   
/* 269:    */   public String outputClassificationTipText()
/* 270:    */   {
/* 271:511 */     return "Whether to add an attribute with the actual classification.";
/* 272:    */   }
/* 273:    */   
/* 274:    */   public boolean getOutputClassification()
/* 275:    */   {
/* 276:520 */     return this.m_OutputClassification;
/* 277:    */   }
/* 278:    */   
/* 279:    */   public void setOutputClassification(boolean value)
/* 280:    */   {
/* 281:529 */     this.m_OutputClassification = value;
/* 282:    */   }
/* 283:    */   
/* 284:    */   public String removeOldClassTipText()
/* 285:    */   {
/* 286:539 */     return "Whether to remove the old class attribute.";
/* 287:    */   }
/* 288:    */   
/* 289:    */   public boolean getRemoveOldClass()
/* 290:    */   {
/* 291:548 */     return this.m_RemoveOldClass;
/* 292:    */   }
/* 293:    */   
/* 294:    */   public void setRemoveOldClass(boolean value)
/* 295:    */   {
/* 296:557 */     this.m_RemoveOldClass = value;
/* 297:    */   }
/* 298:    */   
/* 299:    */   public String outputDistributionTipText()
/* 300:    */   {
/* 301:567 */     return "Whether to add attributes with the distribution for all classes (for numeric classes this will be identical to the attribute output with 'outputClassification').";
/* 302:    */   }
/* 303:    */   
/* 304:    */   public boolean getOutputDistribution()
/* 305:    */   {
/* 306:578 */     return this.m_OutputDistribution;
/* 307:    */   }
/* 308:    */   
/* 309:    */   public void setOutputDistribution(boolean value)
/* 310:    */   {
/* 311:587 */     this.m_OutputDistribution = value;
/* 312:    */   }
/* 313:    */   
/* 314:    */   public String outputErrorFlagTipText()
/* 315:    */   {
/* 316:597 */     return "Whether to add an attribute indicating whether the classifier output a wrong classification (for numeric classes this is the numeric difference).";
/* 317:    */   }
/* 318:    */   
/* 319:    */   public boolean getOutputErrorFlag()
/* 320:    */   {
/* 321:608 */     return this.m_OutputErrorFlag;
/* 322:    */   }
/* 323:    */   
/* 324:    */   public void setOutputErrorFlag(boolean value)
/* 325:    */   {
/* 326:617 */     this.m_OutputErrorFlag = value;
/* 327:    */   }
/* 328:    */   
/* 329:    */   protected Instances determineOutputFormat(Instances inputFormat)
/* 330:    */     throws Exception
/* 331:    */   {
/* 332:640 */     int classindex = -1;
/* 333:    */     
/* 334:    */ 
/* 335:643 */     ArrayList<Attribute> atts = new ArrayList();
/* 336:644 */     for (int i = 0; i < inputFormat.numAttributes(); i++) {
/* 337:646 */       if ((i != inputFormat.classIndex()) || (!getRemoveOldClass()))
/* 338:    */       {
/* 339:650 */         if (i == inputFormat.classIndex()) {
/* 340:651 */           classindex = i;
/* 341:    */         }
/* 342:653 */         atts.add((Attribute)inputFormat.attribute(i).copy());
/* 343:    */       }
/* 344:    */     }
/* 345:658 */     if (getOutputClassification())
/* 346:    */     {
/* 347:660 */       if (classindex == -1) {
/* 348:661 */         classindex = atts.size();
/* 349:    */       }
/* 350:663 */       atts.add(inputFormat.classAttribute().copy("classification"));
/* 351:    */     }
/* 352:667 */     if (getOutputDistribution())
/* 353:    */     {
/* 354:668 */       if (inputFormat.classAttribute().isNominal()) {
/* 355:669 */         for (i = 0; i < inputFormat.classAttribute().numValues(); i++) {
/* 356:670 */           atts.add(new Attribute("distribution_" + inputFormat.classAttribute().value(i)));
/* 357:    */         }
/* 358:    */       }
/* 359:674 */       atts.add(new Attribute("distribution"));
/* 360:    */     }
/* 361:679 */     if (getOutputErrorFlag()) {
/* 362:680 */       if (inputFormat.classAttribute().isNominal())
/* 363:    */       {
/* 364:681 */         ArrayList<String> values = new ArrayList();
/* 365:682 */         values.add("no");
/* 366:683 */         values.add("yes");
/* 367:684 */         atts.add(new Attribute("error", values));
/* 368:    */       }
/* 369:    */       else
/* 370:    */       {
/* 371:686 */         atts.add(new Attribute("error"));
/* 372:    */       }
/* 373:    */     }
/* 374:691 */     Instances result = new Instances(inputFormat.relationName(), atts, 0);
/* 375:692 */     result.setClassIndex(classindex);
/* 376:    */     
/* 377:694 */     return result;
/* 378:    */   }
/* 379:    */   
/* 380:    */   protected Instances process(Instances instances)
/* 381:    */     throws Exception
/* 382:    */   {
/* 383:719 */     if (!isFirstBatchDone())
/* 384:    */     {
/* 385:720 */       getActualClassifier();
/* 386:721 */       if (!getSerializedClassifierFile().isDirectory())
/* 387:    */       {
/* 388:723 */         if ((this.m_SerializedHeader != null) && (!this.m_SerializedHeader.equalHeaders(instances))) {
/* 389:725 */           throw new WekaException("Training header of classifier and filter dataset don't match:\n" + this.m_SerializedHeader.equalHeadersMsg(instances));
/* 390:    */         }
/* 391:    */       }
/* 392:    */       else {
/* 393:730 */         this.m_ActualClassifier.buildClassifier(instances);
/* 394:    */       }
/* 395:    */     }
/* 396:734 */     Instances result = getOutputFormat();
/* 397:737 */     for (int i = 0; i < instances.numInstances(); i++)
/* 398:    */     {
/* 399:738 */       Instance oldInstance = instances.instance(i);
/* 400:739 */       double[] oldValues = oldInstance.toDoubleArray();
/* 401:740 */       double[] newValues = new double[result.numAttributes()];
/* 402:    */       
/* 403:742 */       int start = oldValues.length;
/* 404:743 */       if (getRemoveOldClass()) {
/* 405:744 */         start--;
/* 406:    */       }
/* 407:748 */       System.arraycopy(oldValues, 0, newValues, 0, start);
/* 408:752 */       if (getOutputClassification())
/* 409:    */       {
/* 410:753 */         newValues[start] = this.m_ActualClassifier.classifyInstance(oldInstance);
/* 411:754 */         start++;
/* 412:    */       }
/* 413:758 */       if (getOutputDistribution())
/* 414:    */       {
/* 415:759 */         double[] distribution = this.m_ActualClassifier.distributionForInstance(oldInstance);
/* 416:760 */         for (int n = 0; n < distribution.length; n++)
/* 417:    */         {
/* 418:761 */           newValues[start] = distribution[n];
/* 419:762 */           start++;
/* 420:    */         }
/* 421:    */       }
/* 422:767 */       if (getOutputErrorFlag())
/* 423:    */       {
/* 424:768 */         if (result.classAttribute().isNominal())
/* 425:    */         {
/* 426:769 */           if (oldInstance.classValue() == this.m_ActualClassifier.classifyInstance(oldInstance)) {
/* 427:771 */             newValues[start] = 0.0D;
/* 428:    */           } else {
/* 429:773 */             newValues[start] = 1.0D;
/* 430:    */           }
/* 431:    */         }
/* 432:    */         else {
/* 433:776 */           newValues[start] = (this.m_ActualClassifier.classifyInstance(oldInstance) - oldInstance.classValue());
/* 434:    */         }
/* 435:779 */         start++;
/* 436:    */       }
/* 437:    */       Instance newInstance;
/* 438:    */       Instance newInstance;
/* 439:783 */       if ((oldInstance instanceof SparseInstance)) {
/* 440:784 */         newInstance = new SparseInstance(oldInstance.weight(), newValues);
/* 441:    */       } else {
/* 442:786 */         newInstance = new DenseInstance(oldInstance.weight(), newValues);
/* 443:    */       }
/* 444:790 */       copyValues(newInstance, false, oldInstance.dataset(), outputFormatPeek());
/* 445:    */       
/* 446:792 */       result.add(newInstance);
/* 447:    */     }
/* 448:795 */     return result;
/* 449:    */   }
/* 450:    */   
/* 451:    */   public String getRevision()
/* 452:    */   {
/* 453:805 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 454:    */   }
/* 455:    */   
/* 456:    */   public static void main(String[] args)
/* 457:    */   {
/* 458:814 */     runFilter(new AddClassification(), args);
/* 459:    */   }
/* 460:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.supervised.attribute.AddClassification
 * JD-Core Version:    0.7.0.1
 */