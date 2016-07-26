/*    1:     */ package weka.datagenerators.classifiers.classification;
/*    2:     */ 
/*    3:     */ import java.util.ArrayList;
/*    4:     */ import java.util.Collections;
/*    5:     */ import java.util.Enumeration;
/*    6:     */ import java.util.Random;
/*    7:     */ import java.util.Vector;
/*    8:     */ import weka.core.Attribute;
/*    9:     */ import weka.core.DenseInstance;
/*   10:     */ import weka.core.Instance;
/*   11:     */ import weka.core.Instances;
/*   12:     */ import weka.core.Option;
/*   13:     */ import weka.core.RevisionUtils;
/*   14:     */ import weka.core.SelectedTag;
/*   15:     */ import weka.core.Tag;
/*   16:     */ import weka.core.TechnicalInformation;
/*   17:     */ import weka.core.TechnicalInformation.Field;
/*   18:     */ import weka.core.TechnicalInformation.Type;
/*   19:     */ import weka.core.TechnicalInformationHandler;
/*   20:     */ import weka.core.Utils;
/*   21:     */ import weka.datagenerators.ClassificationGenerator;
/*   22:     */ 
/*   23:     */ public class Agrawal
/*   24:     */   extends ClassificationGenerator
/*   25:     */   implements TechnicalInformationHandler
/*   26:     */ {
/*   27:     */   static final long serialVersionUID = 2254651939636143025L;
/*   28: 161 */   protected static ClassFunction[] builtInFunctions = { new ClassFunction()new ClassFunction
/*   29:     */   {
/*   30:     */     public long determineClass(double salary, double commission, int age, int elevel, int car, int zipcode, double hvalue, int hyears, double loan)
/*   31:     */     {
/*   32: 167 */       if ((age < 40) || (60 <= age)) {
/*   33: 168 */         return 0L;
/*   34:     */       }
/*   35: 170 */       return 1L;
/*   36:     */     }
/*   37: 161 */   }, new ClassFunction()new ClassFunction
/*   38:     */   {
/*   39:     */     public long determineClass(double salary, double commission, int age, int elevel, int car, int zipcode, double hvalue, int hyears, double loan)
/*   40:     */     {
/*   41: 179 */       if (age < 40)
/*   42:     */       {
/*   43: 180 */         if ((50000.0D <= salary) && (salary <= 100000.0D)) {
/*   44: 181 */           return 0L;
/*   45:     */         }
/*   46: 183 */         return 1L;
/*   47:     */       }
/*   48: 185 */       if (age < 60)
/*   49:     */       {
/*   50: 186 */         if ((75000.0D <= salary) && (salary <= 125000.0D)) {
/*   51: 187 */           return 0L;
/*   52:     */         }
/*   53: 189 */         return 1L;
/*   54:     */       }
/*   55: 192 */       if ((25000.0D <= salary) && (salary <= 75000.0D)) {
/*   56: 193 */         return 0L;
/*   57:     */       }
/*   58: 195 */       return 1L;
/*   59:     */     }
/*   60: 161 */   }, new ClassFunction()new ClassFunction
/*   61:     */   {
/*   62:     */     public long determineClass(double salary, double commission, int age, int elevel, int car, int zipcode, double hvalue, int hyears, double loan)
/*   63:     */     {
/*   64: 204 */       if (age < 40)
/*   65:     */       {
/*   66: 205 */         if ((elevel == 0) || (elevel == 1)) {
/*   67: 206 */           return 0L;
/*   68:     */         }
/*   69: 208 */         return 1L;
/*   70:     */       }
/*   71: 210 */       if (age < 60)
/*   72:     */       {
/*   73: 211 */         if ((elevel == 1) || (elevel == 2) || (elevel == 3)) {
/*   74: 212 */           return 0L;
/*   75:     */         }
/*   76: 214 */         return 1L;
/*   77:     */       }
/*   78: 217 */       if ((elevel == 2) || (elevel == 3) || (elevel == 4)) {
/*   79: 218 */         return 0L;
/*   80:     */       }
/*   81: 220 */       return 1L;
/*   82:     */     }
/*   83: 161 */   }, new ClassFunction()new ClassFunction
/*   84:     */   {
/*   85:     */     public long determineClass(double salary, double commission, int age, int elevel, int car, int zipcode, double hvalue, int hyears, double loan)
/*   86:     */     {
/*   87: 229 */       if (age < 40)
/*   88:     */       {
/*   89: 230 */         if ((elevel == 0) || (elevel == 1))
/*   90:     */         {
/*   91: 231 */           if ((25000.0D <= salary) && (salary <= 75000.0D)) {
/*   92: 232 */             return 0L;
/*   93:     */           }
/*   94: 234 */           return 1L;
/*   95:     */         }
/*   96: 236 */         if ((50000.0D <= salary) && (salary <= 100000.0D)) {
/*   97: 237 */           return 0L;
/*   98:     */         }
/*   99: 239 */         return 1L;
/*  100:     */       }
/*  101: 241 */       if (age < 60)
/*  102:     */       {
/*  103: 242 */         if ((elevel == 1) || (elevel == 2) || (elevel == 3))
/*  104:     */         {
/*  105: 243 */           if ((50000.0D <= salary) && (salary <= 100000.0D)) {
/*  106: 244 */             return 0L;
/*  107:     */           }
/*  108: 246 */           return 1L;
/*  109:     */         }
/*  110: 248 */         if ((75000.0D <= salary) && (salary <= 125000.0D)) {
/*  111: 249 */           return 0L;
/*  112:     */         }
/*  113: 251 */         return 1L;
/*  114:     */       }
/*  115: 254 */       if ((elevel == 2) || (elevel == 3) || (elevel == 4))
/*  116:     */       {
/*  117: 255 */         if ((50000.0D <= salary) && (salary <= 100000.0D)) {
/*  118: 256 */           return 0L;
/*  119:     */         }
/*  120: 258 */         return 1L;
/*  121:     */       }
/*  122: 260 */       if ((25000.0D <= salary) && (salary <= 75000.0D)) {
/*  123: 261 */         return 0L;
/*  124:     */       }
/*  125: 263 */       return 1L;
/*  126:     */     }
/*  127: 161 */   }, new ClassFunction()new ClassFunction
/*  128:     */   {
/*  129:     */     public long determineClass(double salary, double commission, int age, int elevel, int car, int zipcode, double hvalue, int hyears, double loan)
/*  130:     */     {
/*  131: 272 */       if (age < 40)
/*  132:     */       {
/*  133: 273 */         if ((50000.0D <= salary) && (salary <= 100000.0D))
/*  134:     */         {
/*  135: 274 */           if ((100000.0D <= loan) && (loan <= 300000.0D)) {
/*  136: 275 */             return 0L;
/*  137:     */           }
/*  138: 277 */           return 1L;
/*  139:     */         }
/*  140: 279 */         if ((200000.0D <= loan) && (loan <= 400000.0D)) {
/*  141: 280 */           return 0L;
/*  142:     */         }
/*  143: 282 */         return 1L;
/*  144:     */       }
/*  145: 284 */       if (age < 60)
/*  146:     */       {
/*  147: 285 */         if ((75000.0D <= salary) && (salary <= 125000.0D))
/*  148:     */         {
/*  149: 286 */           if ((200000.0D <= loan) && (loan <= 400000.0D)) {
/*  150: 287 */             return 0L;
/*  151:     */           }
/*  152: 289 */           return 1L;
/*  153:     */         }
/*  154: 291 */         if ((300000.0D <= loan) && (loan <= 500000.0D)) {
/*  155: 292 */           return 0L;
/*  156:     */         }
/*  157: 294 */         return 1L;
/*  158:     */       }
/*  159: 297 */       if ((25000.0D <= salary) && (salary <= 75000.0D))
/*  160:     */       {
/*  161: 298 */         if ((300000.0D <= loan) && (loan <= 500000.0D)) {
/*  162: 299 */           return 0L;
/*  163:     */         }
/*  164: 301 */         return 1L;
/*  165:     */       }
/*  166: 303 */       if ((100000.0D <= loan) && (loan <= 300000.0D)) {
/*  167: 304 */         return 0L;
/*  168:     */       }
/*  169: 306 */       return 1L;
/*  170:     */     }
/*  171: 161 */   }, new ClassFunction()new ClassFunction
/*  172:     */   {
/*  173:     */     public long determineClass(double salary, double commission, int age, int elevel, int car, int zipcode, double hvalue, int hyears, double loan)
/*  174:     */     {
/*  175: 315 */       double totalSalary = salary + commission;
/*  176: 316 */       if (age < 40)
/*  177:     */       {
/*  178: 317 */         if ((50000.0D <= totalSalary) && (totalSalary <= 100000.0D)) {
/*  179: 318 */           return 0L;
/*  180:     */         }
/*  181: 320 */         return 1L;
/*  182:     */       }
/*  183: 322 */       if (age < 60)
/*  184:     */       {
/*  185: 323 */         if ((75000.0D <= totalSalary) && (totalSalary <= 125000.0D)) {
/*  186: 324 */           return 0L;
/*  187:     */         }
/*  188: 326 */         return 1L;
/*  189:     */       }
/*  190: 329 */       if ((25000.0D <= totalSalary) && (totalSalary <= 75000.0D)) {
/*  191: 330 */         return 0L;
/*  192:     */       }
/*  193: 332 */       return 1L;
/*  194:     */     }
/*  195: 161 */   }, new ClassFunction()new ClassFunction
/*  196:     */   {
/*  197:     */     public long determineClass(double salary, double commission, int age, int elevel, int car, int zipcode, double hvalue, int hyears, double loan)
/*  198:     */     {
/*  199: 341 */       double disposable = 2.0D * (salary + commission) / 3.0D - loan / 5.0D - 20000.0D;
/*  200: 342 */       return disposable > 0.0D ? 0L : 1L;
/*  201:     */     }
/*  202: 161 */   }, new ClassFunction()new ClassFunction
/*  203:     */   {
/*  204:     */     public long determineClass(double salary, double commission, int age, int elevel, int car, int zipcode, double hvalue, int hyears, double loan)
/*  205:     */     {
/*  206: 350 */       double disposable = 2.0D * (salary + commission) / 3.0D - 5000.0D * elevel - 20000.0D;
/*  207:     */       
/*  208: 352 */       return disposable > 0.0D ? 0L : 1L;
/*  209:     */     }
/*  210: 161 */   }, new ClassFunction()new ClassFunction
/*  211:     */   {
/*  212:     */     public long determineClass(double salary, double commission, int age, int elevel, int car, int zipcode, double hvalue, int hyears, double loan)
/*  213:     */     {
/*  214: 360 */       double disposable = 2.0D * (salary + commission) / 3.0D - 5000.0D * elevel - loan / 5.0D - 10000.0D;
/*  215:     */       
/*  216: 362 */       return disposable > 0.0D ? 0L : 1L;
/*  217:     */     }
/*  218: 161 */   }, new ClassFunction()
/*  219:     */   {
/*  220:     */     public long determineClass(double salary, double commission, int age, int elevel, int car, int zipcode, double hvalue, int hyears, double loan)
/*  221:     */     {
/*  222: 370 */       double equity = 0.0D;
/*  223: 371 */       if (hyears >= 20) {
/*  224: 372 */         equity = hvalue * (hyears - 20.0D) / 10.0D;
/*  225:     */       }
/*  226: 374 */       double disposable = 2.0D * (salary + commission) / 3.0D - 5000.0D * elevel + equity / 5.0D - 10000.0D;
/*  227:     */       
/*  228: 376 */       return disposable > 0.0D ? 0L : 1L;
/*  229:     */     }
/*  230: 161 */   } };
/*  231:     */   public static final int FUNCTION_1 = 1;
/*  232:     */   public static final int FUNCTION_2 = 2;
/*  233:     */   public static final int FUNCTION_3 = 3;
/*  234:     */   public static final int FUNCTION_4 = 4;
/*  235:     */   public static final int FUNCTION_5 = 5;
/*  236:     */   public static final int FUNCTION_6 = 6;
/*  237:     */   public static final int FUNCTION_7 = 7;
/*  238:     */   public static final int FUNCTION_8 = 8;
/*  239:     */   public static final int FUNCTION_9 = 9;
/*  240:     */   public static final int FUNCTION_10 = 10;
/*  241: 401 */   public static final Tag[] FUNCTION_TAGS = { new Tag(1, "Function 1"), new Tag(2, "Function 2"), new Tag(3, "Function 3"), new Tag(4, "Function 4"), new Tag(5, "Function 5"), new Tag(6, "Function 6"), new Tag(7, "Function 7"), new Tag(8, "Function 8"), new Tag(9, "Function 9"), new Tag(10, "Function 10") };
/*  242:     */   protected int m_Function;
/*  243:     */   protected boolean m_BalanceClass;
/*  244:     */   protected double m_PerturbationFraction;
/*  245:     */   protected boolean m_nextClassShouldBeZero;
/*  246:     */   protected double m_lastLabel;
/*  247:     */   
/*  248:     */   public Agrawal()
/*  249:     */   {
/*  250: 429 */     setFunction(defaultFunction());
/*  251: 430 */     setBalanceClass(defaultBalanceClass());
/*  252: 431 */     setPerturbationFraction(defaultPerturbationFraction());
/*  253:     */   }
/*  254:     */   
/*  255:     */   public String globalInfo()
/*  256:     */   {
/*  257: 441 */     return "Generates a people database and is based on the paper by Agrawal et al.:\n" + getTechnicalInformation().toString();
/*  258:     */   }
/*  259:     */   
/*  260:     */   public TechnicalInformation getTechnicalInformation()
/*  261:     */   {
/*  262: 456 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  263: 457 */     result.setValue(TechnicalInformation.Field.AUTHOR, "R. Agrawal and T. Imielinski and A. Swami");
/*  264: 458 */     result.setValue(TechnicalInformation.Field.YEAR, "1993");
/*  265: 459 */     result.setValue(TechnicalInformation.Field.TITLE, "Database Mining: A Performance Perspective");
/*  266: 460 */     result.setValue(TechnicalInformation.Field.JOURNAL, "IEEE Transactions on Knowledge and Data Engineering");
/*  267:     */     
/*  268: 462 */     result.setValue(TechnicalInformation.Field.VOLUME, "5");
/*  269: 463 */     result.setValue(TechnicalInformation.Field.NUMBER, "6");
/*  270: 464 */     result.setValue(TechnicalInformation.Field.PAGES, "914-925");
/*  271: 465 */     result.setValue(TechnicalInformation.Field.NOTE, "Special issue on Learning and Discovery in Knowledge-Based Databases");
/*  272:     */     
/*  273: 467 */     result.setValue(TechnicalInformation.Field.URL, "http://www.almaden.ibm.com/software/quest/Publications/ByDate.html");
/*  274:     */     
/*  275: 469 */     result.setValue(TechnicalInformation.Field.PDF, "http://www.almaden.ibm.com/software/quest/Publications/papers/tkde93.pdf");
/*  276:     */     
/*  277:     */ 
/*  278:     */ 
/*  279: 473 */     return result;
/*  280:     */   }
/*  281:     */   
/*  282:     */   public Enumeration<Option> listOptions()
/*  283:     */   {
/*  284: 483 */     Vector<Option> result = enumToVector(super.listOptions());
/*  285:     */     
/*  286: 485 */     result.add(new Option("\tThe function to use for generating the data. (default " + defaultFunction().getSelectedTag().getID() + ")", "F", 1, "-F <num>"));
/*  287:     */     
/*  288:     */ 
/*  289:     */ 
/*  290:     */ 
/*  291:     */ 
/*  292: 491 */     result.add(new Option("\tWhether to balance the class.", "B", 0, "-B"));
/*  293:     */     
/*  294: 493 */     result.add(new Option("\tThe perturbation factor. (default " + defaultPerturbationFraction() + ")", "P", 1, "-P <num>"));
/*  295:     */     
/*  296:     */ 
/*  297: 496 */     return result.elements();
/*  298:     */   }
/*  299:     */   
/*  300:     */   public void setOptions(String[] options)
/*  301:     */     throws Exception
/*  302:     */   {
/*  303: 561 */     super.setOptions(options);
/*  304:     */     
/*  305: 563 */     String tmpStr = Utils.getOption('F', options);
/*  306: 564 */     if (tmpStr.length() != 0) {
/*  307: 565 */       setFunction(new SelectedTag(Integer.parseInt(tmpStr), FUNCTION_TAGS));
/*  308:     */     } else {
/*  309: 567 */       setFunction(defaultFunction());
/*  310:     */     }
/*  311: 570 */     setBalanceClass(Utils.getFlag('B', options));
/*  312:     */     
/*  313: 572 */     tmpStr = Utils.getOption('P', options);
/*  314: 573 */     if (tmpStr.length() != 0) {
/*  315: 574 */       setPerturbationFraction(Double.parseDouble(tmpStr));
/*  316:     */     } else {
/*  317: 576 */       setPerturbationFraction(defaultPerturbationFraction());
/*  318:     */     }
/*  319:     */   }
/*  320:     */   
/*  321:     */   public String[] getOptions()
/*  322:     */   {
/*  323: 587 */     Vector<String> result = new Vector();
/*  324:     */     
/*  325: 589 */     Collections.addAll(result, super.getOptions());
/*  326:     */     
/*  327: 591 */     result.add("-F");
/*  328: 592 */     result.add("" + this.m_Function);
/*  329: 594 */     if (getBalanceClass()) {
/*  330: 595 */       result.add("-B");
/*  331:     */     }
/*  332: 598 */     result.add("-P");
/*  333: 599 */     result.add("" + getPerturbationFraction());
/*  334:     */     
/*  335: 601 */     return (String[])result.toArray(new String[result.size()]);
/*  336:     */   }
/*  337:     */   
/*  338:     */   protected SelectedTag defaultFunction()
/*  339:     */   {
/*  340: 610 */     return new SelectedTag(1, FUNCTION_TAGS);
/*  341:     */   }
/*  342:     */   
/*  343:     */   public SelectedTag getFunction()
/*  344:     */   {
/*  345: 620 */     return new SelectedTag(this.m_Function, FUNCTION_TAGS);
/*  346:     */   }
/*  347:     */   
/*  348:     */   public void setFunction(SelectedTag value)
/*  349:     */   {
/*  350: 630 */     if (value.getTags() == FUNCTION_TAGS) {
/*  351: 631 */       this.m_Function = value.getSelectedTag().getID();
/*  352:     */     }
/*  353:     */   }
/*  354:     */   
/*  355:     */   public String functionTipText()
/*  356:     */   {
/*  357: 642 */     return "The function to use for generating the data.";
/*  358:     */   }
/*  359:     */   
/*  360:     */   protected boolean defaultBalanceClass()
/*  361:     */   {
/*  362: 651 */     return false;
/*  363:     */   }
/*  364:     */   
/*  365:     */   public boolean getBalanceClass()
/*  366:     */   {
/*  367: 660 */     return this.m_BalanceClass;
/*  368:     */   }
/*  369:     */   
/*  370:     */   public void setBalanceClass(boolean value)
/*  371:     */   {
/*  372: 669 */     this.m_BalanceClass = value;
/*  373:     */   }
/*  374:     */   
/*  375:     */   public String balanceClassTipText()
/*  376:     */   {
/*  377: 679 */     return "Whether to balance the class.";
/*  378:     */   }
/*  379:     */   
/*  380:     */   protected double defaultPerturbationFraction()
/*  381:     */   {
/*  382: 688 */     return 0.05D;
/*  383:     */   }
/*  384:     */   
/*  385:     */   public double getPerturbationFraction()
/*  386:     */   {
/*  387: 697 */     return this.m_PerturbationFraction;
/*  388:     */   }
/*  389:     */   
/*  390:     */   public void setPerturbationFraction(double value)
/*  391:     */   {
/*  392: 706 */     if ((value >= 0.0D) && (value <= 1.0D)) {
/*  393: 707 */       this.m_PerturbationFraction = value;
/*  394:     */     } else {
/*  395: 709 */       throw new IllegalArgumentException("Perturbation fraction must be in [0,1] (provided: " + value + ")!");
/*  396:     */     }
/*  397:     */   }
/*  398:     */   
/*  399:     */   public String perturbationFractionTipText()
/*  400:     */   {
/*  401: 721 */     return "The perturbation fraction: 0 <= fraction <= 1.";
/*  402:     */   }
/*  403:     */   
/*  404:     */   public boolean getSingleModeFlag()
/*  405:     */     throws Exception
/*  406:     */   {
/*  407: 733 */     return true;
/*  408:     */   }
/*  409:     */   
/*  410:     */   public Instances defineDataFormat()
/*  411:     */     throws Exception
/*  412:     */   {
/*  413: 751 */     this.m_Random = new Random(getSeed());
/*  414: 752 */     this.m_nextClassShouldBeZero = true;
/*  415: 753 */     this.m_lastLabel = (0.0D / 0.0D);
/*  416:     */     
/*  417:     */ 
/*  418: 756 */     setNumExamplesAct(getNumExamples());
/*  419:     */     
/*  420:     */ 
/*  421: 759 */     ArrayList<Attribute> atts = new ArrayList();
/*  422:     */     
/*  423: 761 */     atts.add(new Attribute("salary"));
/*  424:     */     
/*  425: 763 */     atts.add(new Attribute("commission"));
/*  426:     */     
/*  427: 765 */     atts.add(new Attribute("age"));
/*  428:     */     
/*  429: 767 */     ArrayList<String> attValues = new ArrayList();
/*  430: 768 */     for (int i = 0; i < 5; i++) {
/*  431: 769 */       attValues.add("" + i);
/*  432:     */     }
/*  433: 771 */     atts.add(new Attribute("elevel", attValues));
/*  434:     */     
/*  435: 773 */     attValues = new ArrayList();
/*  436: 774 */     for (i = 1; i <= 20; i++) {
/*  437: 775 */       attValues.add("" + i);
/*  438:     */     }
/*  439: 777 */     atts.add(new Attribute("car", attValues));
/*  440:     */     
/*  441: 779 */     attValues = new ArrayList();
/*  442: 780 */     for (i = 0; i < 9; i++) {
/*  443: 781 */       attValues.add("" + i);
/*  444:     */     }
/*  445: 783 */     atts.add(new Attribute("zipcode", attValues));
/*  446:     */     
/*  447: 785 */     atts.add(new Attribute("hvalue"));
/*  448:     */     
/*  449: 787 */     atts.add(new Attribute("hyears"));
/*  450:     */     
/*  451: 789 */     atts.add(new Attribute("loan"));
/*  452:     */     
/*  453: 791 */     attValues = new ArrayList();
/*  454: 792 */     for (i = 0; i < 2; i++) {
/*  455: 793 */       attValues.add("" + i);
/*  456:     */     }
/*  457: 795 */     atts.add(new Attribute("group", attValues));
/*  458:     */     
/*  459:     */ 
/*  460: 798 */     this.m_DatasetFormat = new Instances(getRelationNameToUse(), atts, 0);
/*  461:     */     
/*  462: 800 */     return this.m_DatasetFormat;
/*  463:     */   }
/*  464:     */   
/*  465:     */   protected double perturbValue(double val, double min, double max)
/*  466:     */   {
/*  467: 812 */     return perturbValue(val, max - min, min, max);
/*  468:     */   }
/*  469:     */   
/*  470:     */   protected double perturbValue(double val, double range, double min, double max)
/*  471:     */   {
/*  472: 826 */     val += range * (2.0D * (getRandom().nextDouble() - 0.5D)) * getPerturbationFraction();
/*  473: 829 */     if (val < min) {
/*  474: 830 */       val = min;
/*  475: 831 */     } else if (val > max) {
/*  476: 832 */       val = max;
/*  477:     */     }
/*  478: 835 */     return val;
/*  479:     */   }
/*  480:     */   
/*  481:     */   public Instance generateExample()
/*  482:     */     throws Exception
/*  483:     */   {
/*  484: 863 */     Instance result = null;
/*  485: 864 */     Random random = getRandom();
/*  486: 866 */     if (this.m_DatasetFormat == null) {
/*  487: 867 */       throw new Exception("Dataset format not defined.");
/*  488:     */     }
/*  489: 870 */     double salary = 0.0D;
/*  490: 871 */     double commission = 0.0D;
/*  491: 872 */     double hvalue = 0.0D;
/*  492: 873 */     double loan = 0.0D;
/*  493: 874 */     int age = 0;
/*  494: 875 */     int elevel = 0;
/*  495: 876 */     int car = 0;
/*  496: 877 */     int zipcode = 0;
/*  497: 878 */     int hyears = 0;
/*  498: 879 */     boolean desiredClassFound = false;
/*  499: 880 */     ClassFunction classFunction = builtInFunctions[(this.m_Function - 1)];
/*  500: 882 */     while (!desiredClassFound)
/*  501:     */     {
/*  502: 884 */       salary = 20000.0D + 130000.0D * random.nextDouble();
/*  503: 885 */       commission = salary >= 75000.0D ? 0.0D : 10000.0D + 65000.0D * random.nextDouble();
/*  504:     */       
/*  505: 887 */       age = 20 + random.nextInt(61);
/*  506: 888 */       elevel = random.nextInt(5);
/*  507: 889 */       car = 1 + random.nextInt(20);
/*  508: 890 */       zipcode = random.nextInt(9);
/*  509: 891 */       hvalue = (9.0D - zipcode) * 100000.0D * (0.5D + random.nextDouble());
/*  510: 892 */       hyears = 1 + random.nextInt(30);
/*  511: 893 */       loan = random.nextDouble() * 500000.0D;
/*  512:     */       
/*  513:     */ 
/*  514: 896 */       this.m_lastLabel = classFunction.determineClass(salary, commission, age, elevel, car, zipcode, hvalue, hyears, loan);
/*  515: 898 */       if (!getBalanceClass())
/*  516:     */       {
/*  517: 899 */         desiredClassFound = true;
/*  518:     */       }
/*  519: 902 */       else if (((this.m_nextClassShouldBeZero) && (this.m_lastLabel == 0.0D)) || ((!this.m_nextClassShouldBeZero) && (this.m_lastLabel == 1.0D)))
/*  520:     */       {
/*  521: 904 */         desiredClassFound = true;
/*  522: 905 */         this.m_nextClassShouldBeZero = (!this.m_nextClassShouldBeZero);
/*  523:     */       }
/*  524:     */     }
/*  525: 911 */     if (getPerturbationFraction() > 0.0D)
/*  526:     */     {
/*  527: 912 */       salary = perturbValue(salary, 20000.0D, 150000.0D);
/*  528: 913 */       if (commission > 0.0D) {
/*  529: 914 */         commission = perturbValue(commission, 10000.0D, 75000.0D);
/*  530:     */       }
/*  531: 916 */       age = (int)Math.round(perturbValue(age, 20.0D, 80.0D));
/*  532: 917 */       hvalue = perturbValue(hvalue, (9.0D - zipcode) * 100000.0D, 0.0D, 135000.0D);
/*  533: 918 */       hyears = (int)Math.round(perturbValue(hyears, 1.0D, 30.0D));
/*  534: 919 */       loan = perturbValue(loan, 0.0D, 500000.0D);
/*  535:     */     }
/*  536: 923 */     double[] atts = new double[this.m_DatasetFormat.numAttributes()];
/*  537: 924 */     atts[0] = salary;
/*  538: 925 */     atts[1] = commission;
/*  539: 926 */     atts[2] = age;
/*  540: 927 */     atts[3] = elevel;
/*  541: 928 */     atts[4] = (car - 1);
/*  542: 929 */     atts[5] = zipcode;
/*  543: 930 */     atts[6] = hvalue;
/*  544: 931 */     atts[7] = hyears;
/*  545: 932 */     atts[8] = loan;
/*  546: 933 */     atts[9] = this.m_lastLabel;
/*  547: 934 */     result = new DenseInstance(1.0D, atts);
/*  548: 935 */     result.setDataset(this.m_DatasetFormat);
/*  549:     */     
/*  550: 937 */     return result;
/*  551:     */   }
/*  552:     */   
/*  553:     */   public Instances generateExamples()
/*  554:     */     throws Exception
/*  555:     */   {
/*  556: 955 */     Instances result = new Instances(this.m_DatasetFormat, 0);
/*  557: 956 */     this.m_Random = new Random(getSeed());
/*  558: 958 */     for (int i = 0; i < getNumExamplesAct(); i++) {
/*  559: 959 */       result.add(generateExample());
/*  560:     */     }
/*  561: 962 */     return result;
/*  562:     */   }
/*  563:     */   
/*  564:     */   public String generateStart()
/*  565:     */   {
/*  566: 974 */     return "";
/*  567:     */   }
/*  568:     */   
/*  569:     */   public String generateFinished()
/*  570:     */     throws Exception
/*  571:     */   {
/*  572: 986 */     return "";
/*  573:     */   }
/*  574:     */   
/*  575:     */   public String getRevision()
/*  576:     */   {
/*  577: 996 */     return RevisionUtils.extract("$Revision: 10203 $");
/*  578:     */   }
/*  579:     */   
/*  580:     */   public static void main(String[] args)
/*  581:     */   {
/*  582:1005 */     runDataGenerator(new Agrawal(), args);
/*  583:     */   }
/*  584:     */   
/*  585:     */   protected static abstract interface ClassFunction
/*  586:     */   {
/*  587:     */     public abstract long determineClass(double paramDouble1, double paramDouble2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, double paramDouble3, int paramInt5, double paramDouble4);
/*  588:     */   }
/*  589:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.datagenerators.classifiers.classification.Agrawal
 * JD-Core Version:    0.7.0.1
 */