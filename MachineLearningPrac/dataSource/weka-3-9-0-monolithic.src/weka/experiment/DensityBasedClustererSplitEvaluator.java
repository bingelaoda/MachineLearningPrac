/*   1:    */ package weka.experiment;
/*   2:    */ 
/*   3:    */ import java.io.ByteArrayOutputStream;
/*   4:    */ import java.io.ObjectOutputStream;
/*   5:    */ import java.io.ObjectStreamClass;
/*   6:    */ import java.io.PrintStream;
/*   7:    */ import java.io.Serializable;
/*   8:    */ import java.util.Arrays;
/*   9:    */ import java.util.Collections;
/*  10:    */ import java.util.Enumeration;
/*  11:    */ import java.util.Vector;
/*  12:    */ import weka.clusterers.AbstractClusterer;
/*  13:    */ import weka.clusterers.ClusterEvaluation;
/*  14:    */ import weka.clusterers.DensityBasedClusterer;
/*  15:    */ import weka.clusterers.EM;
/*  16:    */ import weka.core.AdditionalMeasureProducer;
/*  17:    */ import weka.core.Instances;
/*  18:    */ import weka.core.Option;
/*  19:    */ import weka.core.OptionHandler;
/*  20:    */ import weka.core.RevisionHandler;
/*  21:    */ import weka.core.RevisionUtils;
/*  22:    */ import weka.core.Utils;
/*  23:    */ import weka.filters.Filter;
/*  24:    */ import weka.filters.unsupervised.attribute.Remove;
/*  25:    */ 
/*  26:    */ public class DensityBasedClustererSplitEvaluator
/*  27:    */   implements SplitEvaluator, OptionHandler, AdditionalMeasureProducer, RevisionHandler
/*  28:    */ {
/*  29:    */   private static final long serialVersionUID = 5124501059135692160L;
/*  30:122 */   protected boolean m_removeClassColumn = true;
/*  31:125 */   protected DensityBasedClusterer m_clusterer = new EM();
/*  32:    */   protected ClusterEvaluation m_Evaluation;
/*  33:131 */   protected String[] m_additionalMeasures = null;
/*  34:138 */   protected boolean[] m_doesProduce = null;
/*  35:145 */   protected int m_numberAdditionalMeasures = 0;
/*  36:148 */   protected String m_result = null;
/*  37:151 */   protected String m_clustererOptions = "";
/*  38:154 */   protected String m_clustererVersion = "";
/*  39:    */   protected boolean m_NoSizeDetermination;
/*  40:    */   private static final int KEY_SIZE = 3;
/*  41:    */   private static final int RESULT_SIZE = 9;
/*  42:    */   
/*  43:    */   public DensityBasedClustererSplitEvaluator()
/*  44:    */   {
/*  45:166 */     updateOptions();
/*  46:    */   }
/*  47:    */   
/*  48:    */   public String globalInfo()
/*  49:    */   {
/*  50:176 */     return " A SplitEvaluator that produces results for a density based clusterer. ";
/*  51:    */   }
/*  52:    */   
/*  53:    */   public Enumeration<Option> listOptions()
/*  54:    */   {
/*  55:187 */     Vector<Option> newVector = new Vector(2);
/*  56:    */     
/*  57:189 */     newVector.addElement(new Option("\tSkips the determination of sizes (train/test/clusterer)\n\t(default: sizes are determined)", "no-size", 0, "-no-size"));
/*  58:    */     
/*  59:    */ 
/*  60:192 */     newVector.addElement(new Option("\tThe full class name of the density based clusterer.\n\teg: weka.clusterers.EM", "W", 1, "-W <class name>"));
/*  61:196 */     if ((this.m_clusterer != null) && ((this.m_clusterer instanceof OptionHandler)))
/*  62:    */     {
/*  63:197 */       newVector.addElement(new Option("", "", 0, "\nOptions specific to clusterer " + this.m_clusterer.getClass().getName() + ":"));
/*  64:    */       
/*  65:    */ 
/*  66:200 */       newVector.addAll(Collections.list(((OptionHandler)this.m_clusterer).listOptions()));
/*  67:    */     }
/*  68:203 */     return newVector.elements();
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void setOptions(String[] options)
/*  72:    */     throws Exception
/*  73:    */   {
/*  74:221 */     this.m_NoSizeDetermination = Utils.getFlag("no-size", options);
/*  75:    */     
/*  76:223 */     String cName = Utils.getOption('W', options);
/*  77:224 */     if (cName.length() == 0) {
/*  78:225 */       throw new Exception("A clusterer must be specified with the -W option.");
/*  79:    */     }
/*  80:231 */     setClusterer((DensityBasedClusterer)AbstractClusterer.forName(cName, null));
/*  81:232 */     if ((getClusterer() instanceof OptionHandler))
/*  82:    */     {
/*  83:233 */       ((OptionHandler)getClusterer()).setOptions(Utils.partitionOptions(options));
/*  84:    */       
/*  85:235 */       updateOptions();
/*  86:    */     }
/*  87:    */   }
/*  88:    */   
/*  89:    */   public String[] getOptions()
/*  90:    */   {
/*  91:249 */     Vector<String> result = new Vector();
/*  92:    */     
/*  93:251 */     String[] clustererOptions = new String[0];
/*  94:252 */     if ((this.m_clusterer != null) && ((this.m_clusterer instanceof OptionHandler))) {
/*  95:253 */       clustererOptions = ((OptionHandler)this.m_clusterer).getOptions();
/*  96:    */     }
/*  97:256 */     if (getClusterer() != null)
/*  98:    */     {
/*  99:257 */       result.add("-W");
/* 100:258 */       result.add(getClusterer().getClass().getName());
/* 101:    */     }
/* 102:261 */     if (getNoSizeDetermination()) {
/* 103:262 */       result.add("-no-size");
/* 104:    */     }
/* 105:265 */     result.add("--");
/* 106:266 */     result.addAll(Arrays.asList(clustererOptions));
/* 107:    */     
/* 108:268 */     return (String[])result.toArray(new String[result.size()]);
/* 109:    */   }
/* 110:    */   
/* 111:    */   public void setAdditionalMeasures(String[] additionalMeasures)
/* 112:    */   {
/* 113:282 */     this.m_additionalMeasures = additionalMeasures;
/* 114:286 */     if ((this.m_additionalMeasures != null) && (this.m_additionalMeasures.length > 0))
/* 115:    */     {
/* 116:287 */       this.m_doesProduce = new boolean[this.m_additionalMeasures.length];
/* 117:289 */       if ((this.m_clusterer instanceof AdditionalMeasureProducer))
/* 118:    */       {
/* 119:290 */         Enumeration<String> en = ((AdditionalMeasureProducer)this.m_clusterer).enumerateMeasures();
/* 120:292 */         while (en.hasMoreElements())
/* 121:    */         {
/* 122:293 */           String mname = (String)en.nextElement();
/* 123:294 */           for (int j = 0; j < this.m_additionalMeasures.length; j++) {
/* 124:295 */             if (mname.compareToIgnoreCase(this.m_additionalMeasures[j]) == 0) {
/* 125:296 */               this.m_doesProduce[j] = true;
/* 126:    */             }
/* 127:    */           }
/* 128:    */         }
/* 129:    */       }
/* 130:    */     }
/* 131:    */     else
/* 132:    */     {
/* 133:302 */       this.m_doesProduce = null;
/* 134:    */     }
/* 135:    */   }
/* 136:    */   
/* 137:    */   public Enumeration<String> enumerateMeasures()
/* 138:    */   {
/* 139:314 */     Vector<String> newVector = new Vector();
/* 140:315 */     if ((this.m_clusterer instanceof AdditionalMeasureProducer))
/* 141:    */     {
/* 142:316 */       Enumeration<String> en = ((AdditionalMeasureProducer)this.m_clusterer).enumerateMeasures();
/* 143:318 */       while (en.hasMoreElements())
/* 144:    */       {
/* 145:319 */         String mname = (String)en.nextElement();
/* 146:320 */         newVector.addElement(mname);
/* 147:    */       }
/* 148:    */     }
/* 149:323 */     return newVector.elements();
/* 150:    */   }
/* 151:    */   
/* 152:    */   public double getMeasure(String additionalMeasureName)
/* 153:    */   {
/* 154:335 */     if ((this.m_clusterer instanceof AdditionalMeasureProducer)) {
/* 155:336 */       return ((AdditionalMeasureProducer)this.m_clusterer).getMeasure(additionalMeasureName);
/* 156:    */     }
/* 157:339 */     throw new IllegalArgumentException("DensityBasedClustererSplitEvaluator: Can't return value for : " + additionalMeasureName + ". " + this.m_clusterer.getClass().getName() + " " + "is not an AdditionalMeasureProducer");
/* 158:    */   }
/* 159:    */   
/* 160:    */   public Object[] getKeyTypes()
/* 161:    */   {
/* 162:356 */     Object[] keyTypes = new Object[3];
/* 163:357 */     keyTypes[0] = "";
/* 164:358 */     keyTypes[1] = "";
/* 165:359 */     keyTypes[2] = "";
/* 166:360 */     return keyTypes;
/* 167:    */   }
/* 168:    */   
/* 169:    */   public String[] getKeyNames()
/* 170:    */   {
/* 171:372 */     String[] keyNames = new String[3];
/* 172:373 */     keyNames[0] = "Scheme";
/* 173:374 */     keyNames[1] = "Scheme_options";
/* 174:375 */     keyNames[2] = "Scheme_version_ID";
/* 175:376 */     return keyNames;
/* 176:    */   }
/* 177:    */   
/* 178:    */   public Object[] getKey()
/* 179:    */   {
/* 180:390 */     Object[] key = new Object[3];
/* 181:391 */     key[0] = this.m_clusterer.getClass().getName();
/* 182:392 */     key[1] = this.m_clustererOptions;
/* 183:393 */     key[2] = this.m_clustererVersion;
/* 184:394 */     return key;
/* 185:    */   }
/* 186:    */   
/* 187:    */   public Object[] getResultTypes()
/* 188:    */   {
/* 189:407 */     int addm = this.m_additionalMeasures != null ? this.m_additionalMeasures.length : 0;
/* 190:408 */     int overall_length = 9 + addm;
/* 191:    */     
/* 192:410 */     Object[] resultTypes = new Object[overall_length];
/* 193:411 */     Double doub = new Double(0.0D);
/* 194:412 */     int current = 0;
/* 195:    */     
/* 196:    */ 
/* 197:415 */     resultTypes[(current++)] = doub;
/* 198:416 */     resultTypes[(current++)] = doub;
/* 199:    */     
/* 200:    */ 
/* 201:419 */     resultTypes[(current++)] = doub;
/* 202:    */     
/* 203:421 */     resultTypes[(current++)] = doub;
/* 204:    */     
/* 205:    */ 
/* 206:424 */     resultTypes[(current++)] = doub;
/* 207:425 */     resultTypes[(current++)] = doub;
/* 208:    */     
/* 209:    */ 
/* 210:428 */     resultTypes[(current++)] = doub;
/* 211:429 */     resultTypes[(current++)] = doub;
/* 212:430 */     resultTypes[(current++)] = doub;
/* 213:435 */     for (int i = 0; i < addm; i++) {
/* 214:436 */       resultTypes[(current++)] = doub;
/* 215:    */     }
/* 216:438 */     if (current != overall_length) {
/* 217:439 */       throw new Error("ResultTypes didn't fit RESULT_SIZE");
/* 218:    */     }
/* 219:441 */     return resultTypes;
/* 220:    */   }
/* 221:    */   
/* 222:    */   public String[] getResultNames()
/* 223:    */   {
/* 224:452 */     int addm = this.m_additionalMeasures != null ? this.m_additionalMeasures.length : 0;
/* 225:453 */     int overall_length = 9 + addm;
/* 226:    */     
/* 227:455 */     String[] resultNames = new String[overall_length];
/* 228:456 */     int current = 0;
/* 229:457 */     resultNames[(current++)] = "Number_of_training_instances";
/* 230:458 */     resultNames[(current++)] = "Number_of_testing_instances";
/* 231:    */     
/* 232:    */ 
/* 233:461 */     resultNames[(current++)] = "Log_likelihood";
/* 234:462 */     resultNames[(current++)] = "Number_of_clusters";
/* 235:    */     
/* 236:    */ 
/* 237:465 */     resultNames[(current++)] = "Time_training";
/* 238:466 */     resultNames[(current++)] = "Time_testing";
/* 239:    */     
/* 240:    */ 
/* 241:469 */     resultNames[(current++)] = "Serialized_Model_Size";
/* 242:470 */     resultNames[(current++)] = "Serialized_Train_Set_Size";
/* 243:471 */     resultNames[(current++)] = "Serialized_Test_Set_Size";
/* 244:476 */     for (int i = 0; i < addm; i++) {
/* 245:477 */       resultNames[(current++)] = this.m_additionalMeasures[i];
/* 246:    */     }
/* 247:479 */     if (current != overall_length) {
/* 248:480 */       throw new Error("ResultNames didn't fit RESULT_SIZE");
/* 249:    */     }
/* 250:482 */     return resultNames;
/* 251:    */   }
/* 252:    */   
/* 253:    */   public Object[] getResult(Instances train, Instances test)
/* 254:    */     throws Exception
/* 255:    */   {
/* 256:497 */     if (this.m_clusterer == null) {
/* 257:498 */       throw new Exception("No clusterer has been specified");
/* 258:    */     }
/* 259:500 */     int addm = this.m_additionalMeasures != null ? this.m_additionalMeasures.length : 0;
/* 260:501 */     int overall_length = 9 + addm;
/* 261:503 */     if ((this.m_removeClassColumn) && (train.classIndex() != -1))
/* 262:    */     {
/* 263:505 */       Remove r = new Remove();
/* 264:506 */       r.setAttributeIndicesArray(new int[] { train.classIndex() });
/* 265:507 */       r.setInvertSelection(false);
/* 266:508 */       r.setInputFormat(train);
/* 267:509 */       train = Filter.useFilter(train, r);
/* 268:    */       
/* 269:511 */       test = Filter.useFilter(test, r);
/* 270:    */     }
/* 271:513 */     train.setClassIndex(-1);
/* 272:514 */     test.setClassIndex(-1);
/* 273:    */     
/* 274:516 */     ClusterEvaluation eval = new ClusterEvaluation();
/* 275:    */     
/* 276:518 */     Object[] result = new Object[overall_length];
/* 277:519 */     long trainTimeStart = System.currentTimeMillis();
/* 278:520 */     this.m_clusterer.buildClusterer(train);
/* 279:521 */     double numClusters = this.m_clusterer.numberOfClusters();
/* 280:522 */     eval.setClusterer(this.m_clusterer);
/* 281:523 */     long trainTimeElapsed = System.currentTimeMillis() - trainTimeStart;
/* 282:524 */     long testTimeStart = System.currentTimeMillis();
/* 283:525 */     eval.evaluateClusterer(test);
/* 284:526 */     long testTimeElapsed = System.currentTimeMillis() - testTimeStart;
/* 285:    */     
/* 286:    */ 
/* 287:    */ 
/* 288:    */ 
/* 289:531 */     int current = 0;
/* 290:532 */     result[(current++)] = new Double(train.numInstances());
/* 291:533 */     result[(current++)] = new Double(test.numInstances());
/* 292:    */     
/* 293:535 */     result[(current++)] = new Double(eval.getLogLikelihood());
/* 294:536 */     result[(current++)] = new Double(numClusters);
/* 295:    */     
/* 296:    */ 
/* 297:539 */     result[(current++)] = new Double(trainTimeElapsed / 1000.0D);
/* 298:540 */     result[(current++)] = new Double(testTimeElapsed / 1000.0D);
/* 299:543 */     if (this.m_NoSizeDetermination)
/* 300:    */     {
/* 301:544 */       result[(current++)] = Double.valueOf(-1.0D);
/* 302:545 */       result[(current++)] = Double.valueOf(-1.0D);
/* 303:546 */       result[(current++)] = Double.valueOf(-1.0D);
/* 304:    */     }
/* 305:    */     else
/* 306:    */     {
/* 307:548 */       ByteArrayOutputStream bastream = new ByteArrayOutputStream();
/* 308:549 */       ObjectOutputStream oostream = new ObjectOutputStream(bastream);
/* 309:550 */       oostream.writeObject(this.m_clusterer);
/* 310:551 */       result[(current++)] = new Double(bastream.size());
/* 311:552 */       bastream = new ByteArrayOutputStream();
/* 312:553 */       oostream = new ObjectOutputStream(bastream);
/* 313:554 */       oostream.writeObject(train);
/* 314:555 */       result[(current++)] = new Double(bastream.size());
/* 315:556 */       bastream = new ByteArrayOutputStream();
/* 316:557 */       oostream = new ObjectOutputStream(bastream);
/* 317:558 */       oostream.writeObject(test);
/* 318:559 */       result[(current++)] = new Double(bastream.size());
/* 319:    */     }
/* 320:562 */     for (int i = 0; i < addm; i++) {
/* 321:563 */       if (this.m_doesProduce[i] != 0) {
/* 322:    */         try
/* 323:    */         {
/* 324:565 */           double dv = ((AdditionalMeasureProducer)this.m_clusterer).getMeasure(this.m_additionalMeasures[i]);
/* 325:    */           
/* 326:567 */           Double value = new Double(dv);
/* 327:    */           
/* 328:569 */           result[(current++)] = value;
/* 329:    */         }
/* 330:    */         catch (Exception ex)
/* 331:    */         {
/* 332:571 */           System.err.println(ex);
/* 333:    */         }
/* 334:    */       } else {
/* 335:574 */         result[(current++)] = null;
/* 336:    */       }
/* 337:    */     }
/* 338:578 */     if (current != overall_length) {
/* 339:579 */       throw new Error("Results didn't fit RESULT_SIZE");
/* 340:    */     }
/* 341:582 */     this.m_Evaluation = eval;
/* 342:    */     
/* 343:584 */     return result;
/* 344:    */   }
/* 345:    */   
/* 346:    */   public String removeClassColumnTipText()
/* 347:    */   {
/* 348:594 */     return "Remove the class column (if set) from the data.";
/* 349:    */   }
/* 350:    */   
/* 351:    */   public void setRemoveClassColumn(boolean r)
/* 352:    */   {
/* 353:603 */     this.m_removeClassColumn = r;
/* 354:    */   }
/* 355:    */   
/* 356:    */   public boolean getRemoveClassColumn()
/* 357:    */   {
/* 358:612 */     return this.m_removeClassColumn;
/* 359:    */   }
/* 360:    */   
/* 361:    */   public String clustererTipText()
/* 362:    */   {
/* 363:622 */     return "The density based clusterer to use.";
/* 364:    */   }
/* 365:    */   
/* 366:    */   public DensityBasedClusterer getClusterer()
/* 367:    */   {
/* 368:632 */     return this.m_clusterer;
/* 369:    */   }
/* 370:    */   
/* 371:    */   public void setClusterer(DensityBasedClusterer newClusterer)
/* 372:    */   {
/* 373:642 */     this.m_clusterer = newClusterer;
/* 374:643 */     updateOptions();
/* 375:    */   }
/* 376:    */   
/* 377:    */   public boolean getNoSizeDetermination()
/* 378:    */   {
/* 379:652 */     return this.m_NoSizeDetermination;
/* 380:    */   }
/* 381:    */   
/* 382:    */   public void setNoSizeDetermination(boolean value)
/* 383:    */   {
/* 384:661 */     this.m_NoSizeDetermination = value;
/* 385:    */   }
/* 386:    */   
/* 387:    */   public String noSizeDeterminationTipText()
/* 388:    */   {
/* 389:671 */     return "If enabled, the size determination for train/test/clusterer is skipped.";
/* 390:    */   }
/* 391:    */   
/* 392:    */   protected void updateOptions()
/* 393:    */   {
/* 394:676 */     if ((this.m_clusterer instanceof OptionHandler)) {
/* 395:677 */       this.m_clustererOptions = Utils.joinOptions(((OptionHandler)this.m_clusterer).getOptions());
/* 396:    */     } else {
/* 397:680 */       this.m_clustererOptions = "";
/* 398:    */     }
/* 399:682 */     if ((this.m_clusterer instanceof Serializable))
/* 400:    */     {
/* 401:683 */       ObjectStreamClass obs = ObjectStreamClass.lookup(this.m_clusterer.getClass());
/* 402:684 */       this.m_clustererVersion = ("" + obs.getSerialVersionUID());
/* 403:    */     }
/* 404:    */     else
/* 405:    */     {
/* 406:686 */       this.m_clustererVersion = "";
/* 407:    */     }
/* 408:    */   }
/* 409:    */   
/* 410:    */   public void setClustererName(String newClustererName)
/* 411:    */     throws Exception
/* 412:    */   {
/* 413:    */     try
/* 414:    */     {
/* 415:700 */       setClusterer((DensityBasedClusterer)Class.forName(newClustererName).newInstance());
/* 416:    */     }
/* 417:    */     catch (Exception ex)
/* 418:    */     {
/* 419:703 */       throw new Exception("Can't find Clusterer with class name: " + newClustererName);
/* 420:    */     }
/* 421:    */   }
/* 422:    */   
/* 423:    */   public String getRawResultOutput()
/* 424:    */   {
/* 425:715 */     StringBuffer result = new StringBuffer();
/* 426:717 */     if (this.m_clusterer == null) {
/* 427:718 */       return "<null> clusterer";
/* 428:    */     }
/* 429:720 */     result.append(toString());
/* 430:721 */     result.append("Clustering model: \n" + this.m_clusterer.toString() + '\n');
/* 431:724 */     if (this.m_result != null) {
/* 432:727 */       if (this.m_doesProduce != null) {
/* 433:728 */         for (int i = 0; i < this.m_doesProduce.length; i++) {
/* 434:729 */           if (this.m_doesProduce[i] != 0) {
/* 435:    */             try
/* 436:    */             {
/* 437:731 */               double dv = ((AdditionalMeasureProducer)this.m_clusterer).getMeasure(this.m_additionalMeasures[i]);
/* 438:    */               
/* 439:733 */               Double value = new Double(dv);
/* 440:    */               
/* 441:735 */               result.append(this.m_additionalMeasures[i] + " : " + value + '\n');
/* 442:    */             }
/* 443:    */             catch (Exception ex)
/* 444:    */             {
/* 445:737 */               System.err.println(ex);
/* 446:    */             }
/* 447:    */           }
/* 448:    */         }
/* 449:    */       }
/* 450:    */     }
/* 451:743 */     return result.toString();
/* 452:    */   }
/* 453:    */   
/* 454:    */   public String toString()
/* 455:    */   {
/* 456:754 */     String result = "DensityBasedClustererSplitEvaluator: ";
/* 457:755 */     if (this.m_clusterer == null) {
/* 458:756 */       return result + "<null> clusterer";
/* 459:    */     }
/* 460:758 */     return result + this.m_clusterer.getClass().getName() + " " + this.m_clustererOptions + "(version " + this.m_clustererVersion + ")";
/* 461:    */   }
/* 462:    */   
/* 463:    */   public String getRevision()
/* 464:    */   {
/* 465:769 */     return RevisionUtils.extract("$Revision: 11323 $");
/* 466:    */   }
/* 467:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.DensityBasedClustererSplitEvaluator
 * JD-Core Version:    0.7.0.1
 */