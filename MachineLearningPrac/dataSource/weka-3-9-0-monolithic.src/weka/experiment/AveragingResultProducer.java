/*    1:     */ package weka.experiment;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.util.ArrayList;
/*    5:     */ import java.util.Collections;
/*    6:     */ import java.util.Enumeration;
/*    7:     */ import java.util.Hashtable;
/*    8:     */ import java.util.Vector;
/*    9:     */ import weka.core.AdditionalMeasureProducer;
/*   10:     */ import weka.core.Instances;
/*   11:     */ import weka.core.Option;
/*   12:     */ import weka.core.OptionHandler;
/*   13:     */ import weka.core.RevisionHandler;
/*   14:     */ import weka.core.RevisionUtils;
/*   15:     */ import weka.core.Utils;
/*   16:     */ 
/*   17:     */ public class AveragingResultProducer
/*   18:     */   implements ResultListener, ResultProducer, OptionHandler, AdditionalMeasureProducer, RevisionHandler
/*   19:     */ {
/*   20:     */   static final long serialVersionUID = 2551284958501991352L;
/*   21:     */   protected Instances m_Instances;
/*   22: 160 */   protected ResultListener m_ResultListener = new CSVResultListener();
/*   23: 163 */   protected ResultProducer m_ResultProducer = new CrossValidationResultProducer();
/*   24: 166 */   protected String[] m_AdditionalMeasures = null;
/*   25: 169 */   protected int m_ExpectedResultsPerAverage = 10;
/*   26:     */   protected boolean m_CalculateStdDevs;
/*   27: 178 */   protected String m_CountFieldName = "Num_" + CrossValidationResultProducer.FOLD_FIELD_NAME;
/*   28: 182 */   protected String m_KeyFieldName = CrossValidationResultProducer.FOLD_FIELD_NAME;
/*   29: 185 */   protected int m_KeyIndex = -1;
/*   30: 188 */   protected ArrayList<Object[]> m_Keys = new ArrayList();
/*   31: 191 */   protected ArrayList<Object[]> m_Results = new ArrayList();
/*   32:     */   
/*   33:     */   public String globalInfo()
/*   34:     */   {
/*   35: 200 */     return "Takes the results from a ResultProducer and submits the average to the result listener. Normally used with a CrossValidationResultProducer to perform n x m fold cross validation. For non-numeric result fields, the first value is used.";
/*   36:     */   }
/*   37:     */   
/*   38:     */   protected int findKeyIndex()
/*   39:     */   {
/*   40: 215 */     this.m_KeyIndex = -1;
/*   41:     */     try
/*   42:     */     {
/*   43: 217 */       if (this.m_ResultProducer != null)
/*   44:     */       {
/*   45: 218 */         String[] keyNames = this.m_ResultProducer.getKeyNames();
/*   46: 219 */         for (int i = 0; i < keyNames.length; i++) {
/*   47: 220 */           if (keyNames[i].equals(this.m_KeyFieldName))
/*   48:     */           {
/*   49: 221 */             this.m_KeyIndex = i;
/*   50: 222 */             break;
/*   51:     */           }
/*   52:     */         }
/*   53:     */       }
/*   54:     */     }
/*   55:     */     catch (Exception ex) {}
/*   56: 228 */     return this.m_KeyIndex;
/*   57:     */   }
/*   58:     */   
/*   59:     */   public String[] determineColumnConstraints(ResultProducer rp)
/*   60:     */     throws Exception
/*   61:     */   {
/*   62: 245 */     return null;
/*   63:     */   }
/*   64:     */   
/*   65:     */   protected Object[] determineTemplate(int run)
/*   66:     */     throws Exception
/*   67:     */   {
/*   68: 258 */     if (this.m_Instances == null) {
/*   69: 259 */       throw new Exception("No Instances set");
/*   70:     */     }
/*   71: 261 */     this.m_ResultProducer.setInstances(this.m_Instances);
/*   72:     */     
/*   73:     */ 
/*   74: 264 */     this.m_Keys.clear();
/*   75: 265 */     this.m_Results.clear();
/*   76:     */     
/*   77: 267 */     this.m_ResultProducer.doRunKeys(run);
/*   78: 268 */     checkForMultipleDifferences();
/*   79:     */     
/*   80: 270 */     Object[] template = (Object[])((Object[])this.m_Keys.get(0)).clone();
/*   81: 271 */     template[this.m_KeyIndex] = null;
/*   82:     */     
/*   83: 273 */     checkForDuplicateKeys(template);
/*   84:     */     
/*   85: 275 */     return template;
/*   86:     */   }
/*   87:     */   
/*   88:     */   public void doRunKeys(int run)
/*   89:     */     throws Exception
/*   90:     */   {
/*   91: 290 */     Object[] template = determineTemplate(run);
/*   92: 291 */     String[] newKey = new String[template.length - 1];
/*   93: 292 */     System.arraycopy(template, 0, newKey, 0, this.m_KeyIndex);
/*   94: 293 */     System.arraycopy(template, this.m_KeyIndex + 1, newKey, this.m_KeyIndex, template.length - this.m_KeyIndex - 1);
/*   95:     */     
/*   96: 295 */     this.m_ResultListener.acceptResult(this, newKey, null);
/*   97:     */   }
/*   98:     */   
/*   99:     */   public void doRun(int run)
/*  100:     */     throws Exception
/*  101:     */   {
/*  102: 310 */     Object[] template = determineTemplate(run);
/*  103: 311 */     String[] newKey = new String[template.length - 1];
/*  104: 312 */     System.arraycopy(template, 0, newKey, 0, this.m_KeyIndex);
/*  105: 313 */     System.arraycopy(template, this.m_KeyIndex + 1, newKey, this.m_KeyIndex, template.length - this.m_KeyIndex - 1);
/*  106: 316 */     if (this.m_ResultListener.isResultRequired(this, newKey))
/*  107:     */     {
/*  108: 318 */       this.m_Keys.clear();
/*  109: 319 */       this.m_Results.clear();
/*  110:     */       
/*  111: 321 */       this.m_ResultProducer.doRun(run);
/*  112:     */       
/*  113:     */ 
/*  114:     */ 
/*  115:     */ 
/*  116:     */ 
/*  117: 327 */       checkForMultipleDifferences();
/*  118:     */       
/*  119: 329 */       template = (Object[])((Object[])this.m_Keys.get(0)).clone();
/*  120: 330 */       template[this.m_KeyIndex] = null;
/*  121:     */       
/*  122: 332 */       checkForDuplicateKeys(template);
/*  123:     */       
/*  124: 334 */       doAverageResult(template);
/*  125:     */     }
/*  126:     */   }
/*  127:     */   
/*  128:     */   protected boolean matchesTemplate(Object[] template, Object[] test)
/*  129:     */   {
/*  130: 349 */     if (template.length != test.length) {
/*  131: 350 */       return false;
/*  132:     */     }
/*  133: 352 */     for (int i = 0; i < test.length; i++) {
/*  134: 353 */       if ((template[i] != null) && (!template[i].equals(test[i]))) {
/*  135: 354 */         return false;
/*  136:     */       }
/*  137:     */     }
/*  138: 357 */     return true;
/*  139:     */   }
/*  140:     */   
/*  141:     */   protected void doAverageResult(Object[] template)
/*  142:     */     throws Exception
/*  143:     */   {
/*  144: 371 */     String[] newKey = new String[template.length - 1];
/*  145: 372 */     System.arraycopy(template, 0, newKey, 0, this.m_KeyIndex);
/*  146: 373 */     System.arraycopy(template, this.m_KeyIndex + 1, newKey, this.m_KeyIndex, template.length - this.m_KeyIndex - 1);
/*  147: 375 */     if (this.m_ResultListener.isResultRequired(this, newKey))
/*  148:     */     {
/*  149: 376 */       Object[] resultTypes = this.m_ResultProducer.getResultTypes();
/*  150: 377 */       Stats[] stats = new Stats[resultTypes.length];
/*  151: 378 */       for (int i = 0; i < stats.length; i++) {
/*  152: 379 */         stats[i] = new Stats();
/*  153:     */       }
/*  154: 381 */       Object[] result = getResultTypes();
/*  155: 382 */       int numMatches = 0;
/*  156: 383 */       for (int i = 0; i < this.m_Keys.size(); i++)
/*  157:     */       {
/*  158: 384 */         Object[] currentKey = (Object[])this.m_Keys.get(i);
/*  159: 386 */         if (matchesTemplate(template, currentKey))
/*  160:     */         {
/*  161: 390 */           Object[] currentResult = (Object[])this.m_Results.get(i);
/*  162: 391 */           numMatches++;
/*  163: 392 */           for (int j = 0; j < resultTypes.length; j++) {
/*  164: 393 */             if ((resultTypes[j] instanceof Double))
/*  165:     */             {
/*  166: 394 */               if (currentResult[j] == null) {
/*  167: 399 */                 if (stats[j] != null) {
/*  168: 400 */                   stats[j] = null;
/*  169:     */                 }
/*  170:     */               }
/*  171: 409 */               if (stats[j] != null)
/*  172:     */               {
/*  173: 410 */                 double currentVal = ((Double)currentResult[j]).doubleValue();
/*  174: 411 */                 stats[j].add(currentVal);
/*  175:     */               }
/*  176:     */             }
/*  177:     */           }
/*  178:     */         }
/*  179:     */       }
/*  180: 416 */       if (numMatches != this.m_ExpectedResultsPerAverage) {
/*  181: 417 */         throw new Exception("Expected " + this.m_ExpectedResultsPerAverage + " results matching key \"" + DatabaseUtils.arrayToString(template) + "\" but got " + numMatches);
/*  182:     */       }
/*  183: 421 */       result[0] = new Double(numMatches);
/*  184: 422 */       Object[] currentResult = (Object[])this.m_Results.get(0);
/*  185: 423 */       int k = 1;
/*  186: 424 */       for (int j = 0; j < resultTypes.length; j++) {
/*  187: 425 */         if ((resultTypes[j] instanceof Double))
/*  188:     */         {
/*  189: 426 */           if (stats[j] != null)
/*  190:     */           {
/*  191: 427 */             stats[j].calculateDerived();
/*  192: 428 */             result[(k++)] = new Double(stats[j].mean);
/*  193:     */           }
/*  194:     */           else
/*  195:     */           {
/*  196: 430 */             result[(k++)] = null;
/*  197:     */           }
/*  198: 432 */           if (getCalculateStdDevs()) {
/*  199: 433 */             if (stats[j] != null) {
/*  200: 434 */               result[(k++)] = new Double(stats[j].stdDev);
/*  201:     */             } else {
/*  202: 436 */               result[(k++)] = null;
/*  203:     */             }
/*  204:     */           }
/*  205:     */         }
/*  206:     */         else
/*  207:     */         {
/*  208: 440 */           result[(k++)] = currentResult[j];
/*  209:     */         }
/*  210:     */       }
/*  211: 443 */       this.m_ResultListener.acceptResult(this, newKey, result);
/*  212:     */     }
/*  213:     */   }
/*  214:     */   
/*  215:     */   protected void checkForDuplicateKeys(Object[] template)
/*  216:     */     throws Exception
/*  217:     */   {
/*  218: 456 */     Hashtable<Object, Object> hash = new Hashtable();
/*  219: 457 */     int numMatches = 0;
/*  220: 458 */     for (int i = 0; i < this.m_Keys.size(); i++)
/*  221:     */     {
/*  222: 459 */       Object[] current = (Object[])this.m_Keys.get(i);
/*  223: 461 */       if (matchesTemplate(template, current))
/*  224:     */       {
/*  225: 464 */         if (hash.containsKey(current[this.m_KeyIndex])) {
/*  226: 465 */           throw new Exception("Duplicate result received:" + DatabaseUtils.arrayToString(current));
/*  227:     */         }
/*  228: 468 */         numMatches++;
/*  229: 469 */         hash.put(current[this.m_KeyIndex], current[this.m_KeyIndex]);
/*  230:     */       }
/*  231:     */     }
/*  232: 471 */     if (numMatches != this.m_ExpectedResultsPerAverage) {
/*  233: 472 */       throw new Exception("Expected " + this.m_ExpectedResultsPerAverage + " results matching key \"" + DatabaseUtils.arrayToString(template) + "\" but got " + numMatches);
/*  234:     */     }
/*  235:     */   }
/*  236:     */   
/*  237:     */   protected void checkForMultipleDifferences()
/*  238:     */     throws Exception
/*  239:     */   {
/*  240: 489 */     Object[] firstKey = (Object[])this.m_Keys.get(0);
/*  241: 490 */     Object[] lastKey = (Object[])this.m_Keys.get(this.m_Keys.size() - 1);
/*  242: 495 */     for (int i = 0; i < firstKey.length; i++) {
/*  243: 496 */       if ((i != this.m_KeyIndex) && (!firstKey[i].equals(lastKey[i]))) {
/*  244: 497 */         throw new Exception("Keys differ on fields other than \"" + this.m_KeyFieldName + "\" -- time to implement multiple averaging");
/*  245:     */       }
/*  246:     */     }
/*  247:     */   }
/*  248:     */   
/*  249:     */   public void preProcess(ResultProducer rp)
/*  250:     */     throws Exception
/*  251:     */   {
/*  252: 512 */     if (this.m_ResultListener == null) {
/*  253: 513 */       throw new Exception("No ResultListener set");
/*  254:     */     }
/*  255: 515 */     this.m_ResultListener.preProcess(this);
/*  256:     */   }
/*  257:     */   
/*  258:     */   public void preProcess()
/*  259:     */     throws Exception
/*  260:     */   {
/*  261: 527 */     if (this.m_ResultProducer == null) {
/*  262: 528 */       throw new Exception("No ResultProducer set");
/*  263:     */     }
/*  264: 531 */     this.m_ResultProducer.setResultListener(this);
/*  265: 532 */     findKeyIndex();
/*  266: 533 */     if (this.m_KeyIndex == -1) {
/*  267: 534 */       throw new Exception("No key field called " + this.m_KeyFieldName + " produced by " + this.m_ResultProducer.getClass().getName());
/*  268:     */     }
/*  269: 537 */     this.m_ResultProducer.preProcess();
/*  270:     */   }
/*  271:     */   
/*  272:     */   public void postProcess(ResultProducer rp)
/*  273:     */     throws Exception
/*  274:     */   {
/*  275: 550 */     this.m_ResultListener.postProcess(this);
/*  276:     */   }
/*  277:     */   
/*  278:     */   public void postProcess()
/*  279:     */     throws Exception
/*  280:     */   {
/*  281: 563 */     this.m_ResultProducer.postProcess();
/*  282:     */   }
/*  283:     */   
/*  284:     */   public void acceptResult(ResultProducer rp, Object[] key, Object[] result)
/*  285:     */     throws Exception
/*  286:     */   {
/*  287: 580 */     if (this.m_ResultProducer != rp) {
/*  288: 581 */       throw new Error("Unrecognized ResultProducer sending results!!");
/*  289:     */     }
/*  290: 583 */     this.m_Keys.add(key);
/*  291: 584 */     this.m_Results.add(result);
/*  292:     */   }
/*  293:     */   
/*  294:     */   public boolean isResultRequired(ResultProducer rp, Object[] key)
/*  295:     */     throws Exception
/*  296:     */   {
/*  297: 600 */     if (this.m_ResultProducer != rp) {
/*  298: 601 */       throw new Error("Unrecognized ResultProducer sending results!!");
/*  299:     */     }
/*  300: 603 */     return true;
/*  301:     */   }
/*  302:     */   
/*  303:     */   public String[] getKeyNames()
/*  304:     */     throws Exception
/*  305:     */   {
/*  306: 615 */     if (this.m_KeyIndex == -1) {
/*  307: 616 */       throw new Exception("No key field called " + this.m_KeyFieldName + " produced by " + this.m_ResultProducer.getClass().getName());
/*  308:     */     }
/*  309: 619 */     String[] keyNames = this.m_ResultProducer.getKeyNames();
/*  310: 620 */     String[] newKeyNames = new String[keyNames.length - 1];
/*  311: 621 */     System.arraycopy(keyNames, 0, newKeyNames, 0, this.m_KeyIndex);
/*  312: 622 */     System.arraycopy(keyNames, this.m_KeyIndex + 1, newKeyNames, this.m_KeyIndex, keyNames.length - this.m_KeyIndex - 1);
/*  313:     */     
/*  314: 624 */     return newKeyNames;
/*  315:     */   }
/*  316:     */   
/*  317:     */   public Object[] getKeyTypes()
/*  318:     */     throws Exception
/*  319:     */   {
/*  320: 639 */     if (this.m_KeyIndex == -1) {
/*  321: 640 */       throw new Exception("No key field called " + this.m_KeyFieldName + " produced by " + this.m_ResultProducer.getClass().getName());
/*  322:     */     }
/*  323: 643 */     Object[] keyTypes = this.m_ResultProducer.getKeyTypes();
/*  324:     */     
/*  325: 645 */     Object[] newKeyTypes = new String[keyTypes.length - 1];
/*  326: 646 */     System.arraycopy(keyTypes, 0, newKeyTypes, 0, this.m_KeyIndex);
/*  327: 647 */     System.arraycopy(keyTypes, this.m_KeyIndex + 1, newKeyTypes, this.m_KeyIndex, keyTypes.length - this.m_KeyIndex - 1);
/*  328:     */     
/*  329: 649 */     return newKeyTypes;
/*  330:     */   }
/*  331:     */   
/*  332:     */   public String[] getResultNames()
/*  333:     */     throws Exception
/*  334:     */   {
/*  335: 666 */     String[] resultNames = this.m_ResultProducer.getResultNames();
/*  336: 668 */     if (getCalculateStdDevs())
/*  337:     */     {
/*  338: 669 */       Object[] resultTypes = this.m_ResultProducer.getResultTypes();
/*  339: 670 */       int numNumeric = 0;
/*  340: 671 */       for (Object resultType : resultTypes) {
/*  341: 672 */         if ((resultType instanceof Double)) {
/*  342: 673 */           numNumeric++;
/*  343:     */         }
/*  344:     */       }
/*  345: 676 */       String[] newResultNames = new String[resultNames.length + 1 + numNumeric];
/*  346: 677 */       newResultNames[0] = this.m_CountFieldName;
/*  347: 678 */       int j = 1;
/*  348: 679 */       for (int i = 0; i < resultNames.length; i++)
/*  349:     */       {
/*  350: 680 */         newResultNames[(j++)] = ("Avg_" + resultNames[i]);
/*  351: 681 */         if ((resultTypes[i] instanceof Double)) {
/*  352: 682 */           newResultNames[(j++)] = ("Dev_" + resultNames[i]);
/*  353:     */         }
/*  354:     */       }
/*  355: 685 */       return newResultNames;
/*  356:     */     }
/*  357: 687 */     String[] newResultNames = new String[resultNames.length + 1];
/*  358: 688 */     newResultNames[0] = this.m_CountFieldName;
/*  359: 689 */     System.arraycopy(resultNames, 0, newResultNames, 1, resultNames.length);
/*  360: 690 */     return newResultNames;
/*  361:     */   }
/*  362:     */   
/*  363:     */   public Object[] getResultTypes()
/*  364:     */     throws Exception
/*  365:     */   {
/*  366: 705 */     Object[] resultTypes = this.m_ResultProducer.getResultTypes();
/*  367: 707 */     if (getCalculateStdDevs())
/*  368:     */     {
/*  369: 708 */       int numNumeric = 0;
/*  370: 709 */       for (Object resultType : resultTypes) {
/*  371: 710 */         if ((resultType instanceof Double)) {
/*  372: 711 */           numNumeric++;
/*  373:     */         }
/*  374:     */       }
/*  375: 714 */       Object[] newResultTypes = new Object[resultTypes.length + 1 + numNumeric];
/*  376: 715 */       newResultTypes[0] = new Double(0.0D);
/*  377: 716 */       int j = 1;
/*  378: 717 */       for (Object resultType : resultTypes)
/*  379:     */       {
/*  380: 718 */         newResultTypes[(j++)] = resultType;
/*  381: 719 */         if ((resultType instanceof Double)) {
/*  382: 720 */           newResultTypes[(j++)] = new Double(0.0D);
/*  383:     */         }
/*  384:     */       }
/*  385: 723 */       return newResultTypes;
/*  386:     */     }
/*  387: 725 */     Object[] newResultTypes = new Object[resultTypes.length + 1];
/*  388: 726 */     newResultTypes[0] = new Double(0.0D);
/*  389: 727 */     System.arraycopy(resultTypes, 0, newResultTypes, 1, resultTypes.length);
/*  390: 728 */     return newResultTypes;
/*  391:     */   }
/*  392:     */   
/*  393:     */   public String getCompatibilityState()
/*  394:     */   {
/*  395: 748 */     String result = " -X " + getExpectedResultsPerAverage() + " ";
/*  396: 750 */     if (getCalculateStdDevs()) {
/*  397: 751 */       result = result + "-S ";
/*  398:     */     }
/*  399: 753 */     if (this.m_ResultProducer == null)
/*  400:     */     {
/*  401: 754 */       result = result + "<null ResultProducer>";
/*  402:     */     }
/*  403:     */     else
/*  404:     */     {
/*  405: 756 */       result = result + "-W " + this.m_ResultProducer.getClass().getName();
/*  406: 757 */       result = result + " -- " + this.m_ResultProducer.getCompatibilityState();
/*  407:     */     }
/*  408: 760 */     return result.trim();
/*  409:     */   }
/*  410:     */   
/*  411:     */   public Enumeration<Option> listOptions()
/*  412:     */   {
/*  413: 771 */     Vector<Option> newVector = new Vector(2);
/*  414:     */     
/*  415: 773 */     newVector.addElement(new Option("\tThe name of the field to average over.\n\t(default \"Fold\")", "F", 1, "-F <field name>"));
/*  416:     */     
/*  417:     */ 
/*  418: 776 */     newVector.addElement(new Option("\tThe number of results expected per average.\n\t(default 10)", "X", 1, "-X <num results>"));
/*  419:     */     
/*  420:     */ 
/*  421: 779 */     newVector.addElement(new Option("\tCalculate standard deviations.\n\t(default only averages)", "S", 0, "-S"));
/*  422:     */     
/*  423: 781 */     newVector.addElement(new Option("\tThe full class name of a ResultProducer.\n\teg: weka.experiment.CrossValidationResultProducer", "W", 1, "-W <class name>"));
/*  424: 786 */     if ((this.m_ResultProducer != null) && ((this.m_ResultProducer instanceof OptionHandler)))
/*  425:     */     {
/*  426: 788 */       newVector.addElement(new Option("", "", 0, "\nOptions specific to result producer " + this.m_ResultProducer.getClass().getName() + ":"));
/*  427:     */       
/*  428:     */ 
/*  429:     */ 
/*  430: 792 */       newVector.addAll(Collections.list(((OptionHandler)this.m_ResultProducer).listOptions()));
/*  431:     */     }
/*  432: 795 */     return newVector.elements();
/*  433:     */   }
/*  434:     */   
/*  435:     */   public void setOptions(String[] options)
/*  436:     */     throws Exception
/*  437:     */   {
/*  438: 909 */     String keyFieldName = Utils.getOption('F', options);
/*  439: 910 */     if (keyFieldName.length() != 0) {
/*  440: 911 */       setKeyFieldName(keyFieldName);
/*  441:     */     } else {
/*  442: 913 */       setKeyFieldName(CrossValidationResultProducer.FOLD_FIELD_NAME);
/*  443:     */     }
/*  444: 916 */     String numResults = Utils.getOption('X', options);
/*  445: 917 */     if (numResults.length() != 0) {
/*  446: 918 */       setExpectedResultsPerAverage(Integer.parseInt(numResults));
/*  447:     */     } else {
/*  448: 920 */       setExpectedResultsPerAverage(10);
/*  449:     */     }
/*  450: 923 */     setCalculateStdDevs(Utils.getFlag('S', options));
/*  451:     */     
/*  452: 925 */     String rpName = Utils.getOption('W', options);
/*  453: 926 */     if (rpName.length() == 0) {
/*  454: 927 */       throw new Exception("A ResultProducer must be specified with the -W option.");
/*  455:     */     }
/*  456: 933 */     setResultProducer((ResultProducer)Utils.forName(ResultProducer.class, rpName, null));
/*  457: 935 */     if ((getResultProducer() instanceof OptionHandler)) {
/*  458: 936 */       ((OptionHandler)getResultProducer()).setOptions(Utils.partitionOptions(options));
/*  459:     */     }
/*  460:     */   }
/*  461:     */   
/*  462:     */   public String[] getOptions()
/*  463:     */   {
/*  464: 949 */     String[] seOptions = new String[0];
/*  465: 950 */     if ((this.m_ResultProducer != null) && ((this.m_ResultProducer instanceof OptionHandler))) {
/*  466: 952 */       seOptions = ((OptionHandler)this.m_ResultProducer).getOptions();
/*  467:     */     }
/*  468: 955 */     String[] options = new String[seOptions.length + 8];
/*  469: 956 */     int current = 0;
/*  470:     */     
/*  471: 958 */     options[(current++)] = "-F";
/*  472: 959 */     options[(current++)] = ("" + getKeyFieldName());
/*  473: 960 */     options[(current++)] = "-X";
/*  474: 961 */     options[(current++)] = ("" + getExpectedResultsPerAverage());
/*  475: 962 */     if (getCalculateStdDevs()) {
/*  476: 963 */       options[(current++)] = "-S";
/*  477:     */     }
/*  478: 965 */     if (getResultProducer() != null)
/*  479:     */     {
/*  480: 966 */       options[(current++)] = "-W";
/*  481: 967 */       options[(current++)] = getResultProducer().getClass().getName();
/*  482:     */     }
/*  483: 969 */     options[(current++)] = "--";
/*  484:     */     
/*  485: 971 */     System.arraycopy(seOptions, 0, options, current, seOptions.length);
/*  486: 972 */     current += seOptions.length;
/*  487: 973 */     while (current < options.length) {
/*  488: 974 */       options[(current++)] = "";
/*  489:     */     }
/*  490: 976 */     return options;
/*  491:     */   }
/*  492:     */   
/*  493:     */   public void setAdditionalMeasures(String[] additionalMeasures)
/*  494:     */   {
/*  495: 989 */     this.m_AdditionalMeasures = additionalMeasures;
/*  496: 991 */     if (this.m_ResultProducer != null)
/*  497:     */     {
/*  498: 992 */       System.err.println("AveragingResultProducer: setting additional measures for ResultProducer");
/*  499:     */       
/*  500: 994 */       this.m_ResultProducer.setAdditionalMeasures(this.m_AdditionalMeasures);
/*  501:     */     }
/*  502:     */   }
/*  503:     */   
/*  504:     */   public Enumeration<String> enumerateMeasures()
/*  505:     */   {
/*  506:1006 */     Vector<String> newVector = new Vector();
/*  507:1007 */     if ((this.m_ResultProducer instanceof AdditionalMeasureProducer))
/*  508:     */     {
/*  509:1008 */       Enumeration<String> en = ((AdditionalMeasureProducer)this.m_ResultProducer).enumerateMeasures();
/*  510:1010 */       while (en.hasMoreElements())
/*  511:     */       {
/*  512:1011 */         String mname = (String)en.nextElement();
/*  513:1012 */         newVector.addElement(mname);
/*  514:     */       }
/*  515:     */     }
/*  516:1015 */     return newVector.elements();
/*  517:     */   }
/*  518:     */   
/*  519:     */   public double getMeasure(String additionalMeasureName)
/*  520:     */   {
/*  521:1027 */     if ((this.m_ResultProducer instanceof AdditionalMeasureProducer)) {
/*  522:1028 */       return ((AdditionalMeasureProducer)this.m_ResultProducer).getMeasure(additionalMeasureName);
/*  523:     */     }
/*  524:1031 */     throw new IllegalArgumentException("AveragingResultProducer: Can't return value for : " + additionalMeasureName + ". " + this.m_ResultProducer.getClass().getName() + " " + "is not an AdditionalMeasureProducer");
/*  525:     */   }
/*  526:     */   
/*  527:     */   public void setInstances(Instances instances)
/*  528:     */   {
/*  529:1046 */     this.m_Instances = instances;
/*  530:     */   }
/*  531:     */   
/*  532:     */   public String calculateStdDevsTipText()
/*  533:     */   {
/*  534:1056 */     return "Record standard deviations for each run.";
/*  535:     */   }
/*  536:     */   
/*  537:     */   public boolean getCalculateStdDevs()
/*  538:     */   {
/*  539:1066 */     return this.m_CalculateStdDevs;
/*  540:     */   }
/*  541:     */   
/*  542:     */   public void setCalculateStdDevs(boolean newCalculateStdDevs)
/*  543:     */   {
/*  544:1076 */     this.m_CalculateStdDevs = newCalculateStdDevs;
/*  545:     */   }
/*  546:     */   
/*  547:     */   public String expectedResultsPerAverageTipText()
/*  548:     */   {
/*  549:1086 */     return "Set the expected number of results to average per run. For example if a CrossValidationResultProducer is being used (with the number of folds set to 10), then the expected number of results per run is 10.";
/*  550:     */   }
/*  551:     */   
/*  552:     */   public int getExpectedResultsPerAverage()
/*  553:     */   {
/*  554:1099 */     return this.m_ExpectedResultsPerAverage;
/*  555:     */   }
/*  556:     */   
/*  557:     */   public void setExpectedResultsPerAverage(int newExpectedResultsPerAverage)
/*  558:     */   {
/*  559:1110 */     this.m_ExpectedResultsPerAverage = newExpectedResultsPerAverage;
/*  560:     */   }
/*  561:     */   
/*  562:     */   public String keyFieldNameTipText()
/*  563:     */   {
/*  564:1120 */     return "Set the field name that will be unique for a run.";
/*  565:     */   }
/*  566:     */   
/*  567:     */   public String getKeyFieldName()
/*  568:     */   {
/*  569:1130 */     return this.m_KeyFieldName;
/*  570:     */   }
/*  571:     */   
/*  572:     */   public void setKeyFieldName(String newKeyFieldName)
/*  573:     */   {
/*  574:1140 */     this.m_KeyFieldName = newKeyFieldName;
/*  575:1141 */     this.m_CountFieldName = ("Num_" + this.m_KeyFieldName);
/*  576:1142 */     findKeyIndex();
/*  577:     */   }
/*  578:     */   
/*  579:     */   public void setResultListener(ResultListener listener)
/*  580:     */   {
/*  581:1153 */     this.m_ResultListener = listener;
/*  582:     */   }
/*  583:     */   
/*  584:     */   public String resultProducerTipText()
/*  585:     */   {
/*  586:1163 */     return "Set the resultProducer for which results are to be averaged.";
/*  587:     */   }
/*  588:     */   
/*  589:     */   public ResultProducer getResultProducer()
/*  590:     */   {
/*  591:1173 */     return this.m_ResultProducer;
/*  592:     */   }
/*  593:     */   
/*  594:     */   public void setResultProducer(ResultProducer newResultProducer)
/*  595:     */   {
/*  596:1183 */     this.m_ResultProducer = newResultProducer;
/*  597:1184 */     this.m_ResultProducer.setResultListener(this);
/*  598:1185 */     findKeyIndex();
/*  599:     */   }
/*  600:     */   
/*  601:     */   public String toString()
/*  602:     */   {
/*  603:1196 */     String result = "AveragingResultProducer: ";
/*  604:1197 */     result = result + getCompatibilityState();
/*  605:1198 */     if (this.m_Instances == null) {
/*  606:1199 */       result = result + ": <null Instances>";
/*  607:     */     } else {
/*  608:1201 */       result = result + ": " + Utils.backQuoteChars(this.m_Instances.relationName());
/*  609:     */     }
/*  610:1203 */     return result;
/*  611:     */   }
/*  612:     */   
/*  613:     */   public String getRevision()
/*  614:     */   {
/*  615:1213 */     return RevisionUtils.extract("$Revision: 10203 $");
/*  616:     */   }
/*  617:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.AveragingResultProducer
 * JD-Core Version:    0.7.0.1
 */