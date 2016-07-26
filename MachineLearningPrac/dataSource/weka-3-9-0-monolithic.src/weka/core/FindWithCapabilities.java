/*    1:     */ package weka.core;
/*    2:     */ 
/*    3:     */ import java.io.BufferedReader;
/*    4:     */ import java.io.FileReader;
/*    5:     */ import java.io.PrintStream;
/*    6:     */ import java.util.Enumeration;
/*    7:     */ import java.util.Iterator;
/*    8:     */ import java.util.Properties;
/*    9:     */ import java.util.StringTokenizer;
/*   10:     */ import java.util.Vector;
/*   11:     */ import weka.gui.GenericPropertiesCreator;
/*   12:     */ 
/*   13:     */ public class FindWithCapabilities
/*   14:     */   implements OptionHandler, CapabilitiesHandler, RevisionHandler
/*   15:     */ {
/*   16: 205 */   protected Capabilities m_Capabilities = new Capabilities(this);
/*   17: 208 */   protected Capabilities m_NotCapabilities = new Capabilities(this);
/*   18: 211 */   protected Vector<String> m_Packages = new Vector();
/*   19: 214 */   protected CapabilitiesHandler m_Handler = null;
/*   20: 217 */   protected String m_Filename = "";
/*   21: 220 */   protected SingleIndex m_ClassIndex = new SingleIndex();
/*   22: 226 */   protected String m_Superclass = "";
/*   23: 229 */   protected boolean m_GenericPropertiesCreator = false;
/*   24: 232 */   protected Vector<String> m_Matches = new Vector();
/*   25: 235 */   protected Vector<String> m_Misses = new Vector();
/*   26: 238 */   protected boolean m_DoNotCheckCapabilities = false;
/*   27:     */   
/*   28:     */   public void setDoNotCheckCapabilities(boolean doNotCheckCapabilities)
/*   29:     */   {
/*   30: 247 */     this.m_DoNotCheckCapabilities = doNotCheckCapabilities;
/*   31:     */   }
/*   32:     */   
/*   33:     */   public boolean getDoNotCheckCapabilities()
/*   34:     */   {
/*   35: 257 */     return this.m_DoNotCheckCapabilities;
/*   36:     */   }
/*   37:     */   
/*   38:     */   public Enumeration<Option> listOptions()
/*   39:     */   {
/*   40: 267 */     Vector<Option> result = new Vector();
/*   41:     */     
/*   42: 269 */     result.addElement(new Option("", "", 0, "All class and attribute options can be prefixed with 'not',\ne.g., '-not-numeric-class'. This makes sure that the returned\nschemes 'cannot' handle numeric classes."));
/*   43:     */     
/*   44:     */ 
/*   45:     */ 
/*   46:     */ 
/*   47: 274 */     result.addElement(new Option("\tThe minimum number of instances (default 1).", "num-instances", 1, "-num-instances <num>"));
/*   48:     */     
/*   49:     */ 
/*   50:     */ 
/*   51: 278 */     result.addElement(new Option("\tMust handle unray classes.", "unary-class", 0, "-unary-class"));
/*   52:     */     
/*   53:     */ 
/*   54: 281 */     result.addElement(new Option("\tMust handle binary classes.", "binary-class", 0, "-binary-class"));
/*   55:     */     
/*   56:     */ 
/*   57: 284 */     result.addElement(new Option("\tMust handle nominal classes.", "nominal-class", 0, "-nominal-class"));
/*   58:     */     
/*   59:     */ 
/*   60: 287 */     result.addElement(new Option("\tMust handle numeric classes.", "numeric-class", 0, "-numeric-class"));
/*   61:     */     
/*   62:     */ 
/*   63: 290 */     result.addElement(new Option("\tMust handle string classes.", "string-class", 0, "-string-class"));
/*   64:     */     
/*   65:     */ 
/*   66: 293 */     result.addElement(new Option("\tMust handle date classes.", "date-class", 0, "-date-class"));
/*   67:     */     
/*   68:     */ 
/*   69: 296 */     result.addElement(new Option("\tMust handle relational classes.", "relational-class", 0, "-relational-class"));
/*   70:     */     
/*   71:     */ 
/*   72: 299 */     result.addElement(new Option("\tMust handle missing class values.", "missing-class-values", 0, "-missing-class-values"));
/*   73:     */     
/*   74:     */ 
/*   75: 302 */     result.addElement(new Option("\tDoesn't need a class.", "no-class", 0, "-no-class"));
/*   76:     */     
/*   77:     */ 
/*   78: 305 */     result.addElement(new Option("\tMust handle unary attributes.", "unary-atts", 0, "-unary-atts"));
/*   79:     */     
/*   80:     */ 
/*   81: 308 */     result.addElement(new Option("\tMust handle binary attributes.", "binary-atts", 0, "-binary-atts"));
/*   82:     */     
/*   83:     */ 
/*   84: 311 */     result.addElement(new Option("\tMust handle nominal attributes.", "nominal-atts", 0, "-nominal-atts"));
/*   85:     */     
/*   86:     */ 
/*   87: 314 */     result.addElement(new Option("\tMust handle numeric attributes.", "numeric-atts", 0, "-numeric-atts"));
/*   88:     */     
/*   89:     */ 
/*   90: 317 */     result.addElement(new Option("\tMust handle string attributes.", "string-atts", 0, "-string-atts"));
/*   91:     */     
/*   92:     */ 
/*   93: 320 */     result.addElement(new Option("\tMust handle date attributes.", "date-atts", 0, "-date-atts"));
/*   94:     */     
/*   95:     */ 
/*   96: 323 */     result.addElement(new Option("\tMust handle relational attributes.", "relational-atts", 0, "-relational-atts"));
/*   97:     */     
/*   98:     */ 
/*   99: 326 */     result.addElement(new Option("\tMust handle missing attribute values.", "missing-att-values", 0, "-missing-att-values"));
/*  100:     */     
/*  101:     */ 
/*  102: 329 */     result.addElement(new Option("\tMust handle multi-instance data.", "only-multiinstance", 0, "-only-multiinstance"));
/*  103:     */     
/*  104:     */ 
/*  105: 332 */     result.addElement(new Option("\tThe Capabilities handler to base the handling on.\n\tThe other parameters can be used to override the ones\n\tdetermined from the handler. Additional parameters for\n\thandler can be passed on after the '--'.\n\tEither '-W' or '-t' can be used.", "W", 1, "-W <classname>"));
/*  106:     */     
/*  107:     */ 
/*  108:     */ 
/*  109:     */ 
/*  110:     */ 
/*  111:     */ 
/*  112: 339 */     result.addElement(new Option("\tThe dataset to base the capabilities on.\n\tThe other parameters can be used to override the ones\n\tdetermined from the handler.\n\tEither '-t' or '-W' can be used.", "t", 1, "-t <file>"));
/*  113:     */     
/*  114:     */ 
/*  115:     */ 
/*  116:     */ 
/*  117: 344 */     result.addElement(new Option("\tThe index of the class attribute, -1 for none.\n\t'first' and 'last' are also valid.\n\tOnly in conjunction with option '-t'.", "c", 1, "-c <num>"));
/*  118:     */     
/*  119:     */ 
/*  120:     */ 
/*  121:     */ 
/*  122: 349 */     result.addElement(new Option("\tSuperclass to look for in the packages.\n", "superclass", 1, "-superclass"));
/*  123:     */     
/*  124:     */ 
/*  125: 352 */     result.addElement(new Option("\tComma-separated list of packages to search in.", "packages", 1, "-packages"));
/*  126:     */     
/*  127:     */ 
/*  128:     */ 
/*  129: 356 */     result.addElement(new Option("\tRetrieves the package list from the GenericPropertiesCreator\n\tfor the given superclass. (overrides -packages <list>).", "generic", 1, "-generic"));
/*  130:     */     
/*  131:     */ 
/*  132:     */ 
/*  133:     */ 
/*  134: 361 */     result.addElement(new Option("\tAlso prints the classname that didn't match the criteria.", "misses", 0, "-misses"));
/*  135:     */     
/*  136:     */ 
/*  137:     */ 
/*  138: 365 */     return result.elements();
/*  139:     */   }
/*  140:     */   
/*  141:     */   public void setOptions(String[] options)
/*  142:     */     throws Exception
/*  143:     */   {
/*  144: 384 */     this.m_Capabilities = new Capabilities(this);
/*  145: 385 */     boolean initialized = false;
/*  146:     */     
/*  147: 387 */     String tmpStr = Utils.getOption('W', options);
/*  148: 388 */     if (tmpStr.length() != 0)
/*  149:     */     {
/*  150: 389 */       Class<?> cls = Class.forName(tmpStr);
/*  151: 390 */       if (ClassDiscovery.hasInterface(CapabilitiesHandler.class, cls))
/*  152:     */       {
/*  153: 391 */         initialized = true;
/*  154: 392 */         CapabilitiesHandler handler = (CapabilitiesHandler)cls.newInstance();
/*  155: 393 */         if ((handler instanceof OptionHandler)) {
/*  156: 394 */           ((OptionHandler)handler).setOptions(Utils.partitionOptions(options));
/*  157:     */         }
/*  158: 396 */         setHandler(handler);
/*  159:     */       }
/*  160:     */       else
/*  161:     */       {
/*  162: 398 */         throw new IllegalArgumentException("Class '" + tmpStr + "' is not a CapabilitiesHandler!");
/*  163:     */       }
/*  164:     */     }
/*  165:     */     else
/*  166:     */     {
/*  167: 402 */       tmpStr = Utils.getOption('c', options);
/*  168: 403 */       if (tmpStr.length() != 0) {
/*  169: 404 */         setClassIndex(tmpStr);
/*  170:     */       } else {
/*  171: 406 */         setClassIndex("last");
/*  172:     */       }
/*  173: 409 */       tmpStr = Utils.getOption('t', options);
/*  174: 410 */       setFilename(tmpStr);
/*  175:     */     }
/*  176: 413 */     tmpStr = Utils.getOption("num-instances", options);
/*  177: 414 */     if (tmpStr.length() != 0) {
/*  178: 415 */       this.m_Capabilities.setMinimumNumberInstances(Integer.parseInt(tmpStr));
/*  179: 416 */     } else if (!initialized) {
/*  180: 417 */       this.m_Capabilities.setMinimumNumberInstances(1);
/*  181:     */     }
/*  182: 421 */     if (Utils.getFlag("no-class", options)) {
/*  183: 422 */       enable(Capabilities.Capability.NO_CLASS);
/*  184:     */     }
/*  185: 425 */     if (Utils.getFlag("not-no-class", options)) {
/*  186: 426 */       enableNot(Capabilities.Capability.NO_CLASS);
/*  187:     */     }
/*  188: 429 */     if (!this.m_Capabilities.handles(Capabilities.Capability.NO_CLASS))
/*  189:     */     {
/*  190: 431 */       if (Utils.getFlag("nominal-class", options))
/*  191:     */       {
/*  192: 432 */         enable(Capabilities.Capability.NOMINAL_CLASS);
/*  193: 433 */         disable(Capabilities.Capability.BINARY_CLASS);
/*  194:     */       }
/*  195: 435 */       if (Utils.getFlag("binary-class", options))
/*  196:     */       {
/*  197: 436 */         enable(Capabilities.Capability.BINARY_CLASS);
/*  198: 437 */         disable(Capabilities.Capability.UNARY_CLASS);
/*  199:     */       }
/*  200: 439 */       if (Utils.getFlag("unary-class", options)) {
/*  201: 440 */         enable(Capabilities.Capability.UNARY_CLASS);
/*  202:     */       }
/*  203: 442 */       if (Utils.getFlag("numeric-class", options)) {
/*  204: 443 */         enable(Capabilities.Capability.NUMERIC_CLASS);
/*  205:     */       }
/*  206: 445 */       if (Utils.getFlag("string-class", options)) {
/*  207: 446 */         enable(Capabilities.Capability.STRING_CLASS);
/*  208:     */       }
/*  209: 448 */       if (Utils.getFlag("date-class", options)) {
/*  210: 449 */         enable(Capabilities.Capability.DATE_CLASS);
/*  211:     */       }
/*  212: 451 */       if (Utils.getFlag("relational-class", options)) {
/*  213: 452 */         enable(Capabilities.Capability.RELATIONAL_CLASS);
/*  214:     */       }
/*  215: 454 */       if (Utils.getFlag("missing-class-values", options)) {
/*  216: 455 */         enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  217:     */       }
/*  218:     */     }
/*  219: 459 */     if (Utils.getFlag("not-nominal-class", options))
/*  220:     */     {
/*  221: 460 */       enableNot(Capabilities.Capability.NOMINAL_CLASS);
/*  222: 461 */       disableNot(Capabilities.Capability.BINARY_CLASS);
/*  223:     */     }
/*  224: 463 */     if (Utils.getFlag("not-binary-class", options))
/*  225:     */     {
/*  226: 464 */       enableNot(Capabilities.Capability.BINARY_CLASS);
/*  227: 465 */       disableNot(Capabilities.Capability.UNARY_CLASS);
/*  228:     */     }
/*  229: 467 */     if (Utils.getFlag("not-unary-class", options)) {
/*  230: 468 */       enableNot(Capabilities.Capability.UNARY_CLASS);
/*  231:     */     }
/*  232: 470 */     if (Utils.getFlag("not-numeric-class", options)) {
/*  233: 471 */       enableNot(Capabilities.Capability.NUMERIC_CLASS);
/*  234:     */     }
/*  235: 473 */     if (Utils.getFlag("not-string-class", options)) {
/*  236: 474 */       enableNot(Capabilities.Capability.STRING_CLASS);
/*  237:     */     }
/*  238: 476 */     if (Utils.getFlag("not-date-class", options)) {
/*  239: 477 */       enableNot(Capabilities.Capability.DATE_CLASS);
/*  240:     */     }
/*  241: 479 */     if (Utils.getFlag("not-relational-class", options)) {
/*  242: 480 */       enableNot(Capabilities.Capability.RELATIONAL_CLASS);
/*  243:     */     }
/*  244: 482 */     if (Utils.getFlag("not-relational-class", options)) {
/*  245: 483 */       enableNot(Capabilities.Capability.RELATIONAL_CLASS);
/*  246:     */     }
/*  247: 485 */     if (Utils.getFlag("not-missing-class-values", options)) {
/*  248: 486 */       enableNot(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  249:     */     }
/*  250: 490 */     if (Utils.getFlag("nominal-atts", options))
/*  251:     */     {
/*  252: 491 */       enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  253: 492 */       disable(Capabilities.Capability.BINARY_ATTRIBUTES);
/*  254:     */     }
/*  255: 494 */     if (Utils.getFlag("binary-atts", options))
/*  256:     */     {
/*  257: 495 */       enable(Capabilities.Capability.BINARY_ATTRIBUTES);
/*  258: 496 */       disable(Capabilities.Capability.UNARY_ATTRIBUTES);
/*  259:     */     }
/*  260: 498 */     if (Utils.getFlag("unary-atts", options)) {
/*  261: 499 */       enable(Capabilities.Capability.UNARY_ATTRIBUTES);
/*  262:     */     }
/*  263: 501 */     if (Utils.getFlag("numeric-atts", options)) {
/*  264: 502 */       enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  265:     */     }
/*  266: 504 */     if (Utils.getFlag("string-atts", options)) {
/*  267: 505 */       enable(Capabilities.Capability.STRING_ATTRIBUTES);
/*  268:     */     }
/*  269: 507 */     if (Utils.getFlag("date-atts", options)) {
/*  270: 508 */       enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  271:     */     }
/*  272: 510 */     if (Utils.getFlag("relational-atts", options)) {
/*  273: 511 */       enable(Capabilities.Capability.RELATIONAL_ATTRIBUTES);
/*  274:     */     }
/*  275: 513 */     if (Utils.getFlag("missing-att-values", options)) {
/*  276: 514 */       enable(Capabilities.Capability.MISSING_VALUES);
/*  277:     */     }
/*  278: 517 */     if (Utils.getFlag("not-nominal-atts", options))
/*  279:     */     {
/*  280: 518 */       enableNot(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  281: 519 */       disableNot(Capabilities.Capability.BINARY_ATTRIBUTES);
/*  282:     */     }
/*  283: 521 */     if (Utils.getFlag("not-binary-atts", options))
/*  284:     */     {
/*  285: 522 */       enableNot(Capabilities.Capability.BINARY_ATTRIBUTES);
/*  286: 523 */       disableNot(Capabilities.Capability.UNARY_ATTRIBUTES);
/*  287:     */     }
/*  288: 525 */     if (Utils.getFlag("not-unary-atts", options)) {
/*  289: 526 */       enableNot(Capabilities.Capability.UNARY_ATTRIBUTES);
/*  290:     */     }
/*  291: 528 */     if (Utils.getFlag("not-numeric-atts", options)) {
/*  292: 529 */       enableNot(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  293:     */     }
/*  294: 531 */     if (Utils.getFlag("not-string-atts", options)) {
/*  295: 532 */       enableNot(Capabilities.Capability.STRING_ATTRIBUTES);
/*  296:     */     }
/*  297: 534 */     if (Utils.getFlag("not-date-atts", options)) {
/*  298: 535 */       enableNot(Capabilities.Capability.DATE_ATTRIBUTES);
/*  299:     */     }
/*  300: 537 */     if (Utils.getFlag("not-relational-atts", options)) {
/*  301: 538 */       enableNot(Capabilities.Capability.RELATIONAL_ATTRIBUTES);
/*  302:     */     }
/*  303: 540 */     if (Utils.getFlag("not-missing-att-values", options)) {
/*  304: 541 */       enableNot(Capabilities.Capability.MISSING_VALUES);
/*  305:     */     }
/*  306: 544 */     if (Utils.getFlag("only-multiinstance", options)) {
/*  307: 545 */       enable(Capabilities.Capability.ONLY_MULTIINSTANCE);
/*  308:     */     }
/*  309: 548 */     tmpStr = Utils.getOption("superclass", options);
/*  310: 549 */     if (tmpStr.length() != 0) {
/*  311: 550 */       this.m_Superclass = tmpStr;
/*  312:     */     } else {
/*  313: 552 */       throw new IllegalArgumentException("A superclass has to be specified!");
/*  314:     */     }
/*  315: 555 */     tmpStr = Utils.getOption("packages", options);
/*  316: 556 */     if (tmpStr.length() != 0)
/*  317:     */     {
/*  318: 557 */       StringTokenizer tok = new StringTokenizer(tmpStr, ",");
/*  319: 558 */       this.m_Packages = new Vector();
/*  320: 559 */       while (tok.hasMoreTokens()) {
/*  321: 560 */         this.m_Packages.add(tok.nextToken());
/*  322:     */       }
/*  323:     */     }
/*  324: 564 */     if (Utils.getFlag("generic", options))
/*  325:     */     {
/*  326: 565 */       GenericPropertiesCreator creator = new GenericPropertiesCreator();
/*  327: 566 */       creator.execute(false);
/*  328: 567 */       Properties props = creator.getInputProperties();
/*  329: 568 */       StringTokenizer tok = new StringTokenizer(props.getProperty(this.m_Superclass), ",");
/*  330: 569 */       this.m_Packages = new Vector();
/*  331: 570 */       while (tok.hasMoreTokens()) {
/*  332: 571 */         this.m_Packages.add(tok.nextToken());
/*  333:     */       }
/*  334:     */     }
/*  335:     */   }
/*  336:     */   
/*  337:     */   public String[] getOptions()
/*  338:     */   {
/*  339: 587 */     Vector<String> result = new Vector();
/*  340:     */     
/*  341: 589 */     result.add("-num-instances");
/*  342: 590 */     result.add("" + this.m_Capabilities.getMinimumNumberInstances());
/*  343: 592 */     if (isEnabled(Capabilities.Capability.NO_CLASS))
/*  344:     */     {
/*  345: 593 */       result.add("-no-class");
/*  346:     */     }
/*  347:     */     else
/*  348:     */     {
/*  349: 595 */       if (isEnabled(Capabilities.Capability.UNARY_CLASS)) {
/*  350: 596 */         result.add("-unary-class");
/*  351:     */       }
/*  352: 598 */       if (isEnabled(Capabilities.Capability.BINARY_CLASS)) {
/*  353: 599 */         result.add("-binary-class");
/*  354:     */       }
/*  355: 601 */       if (isEnabled(Capabilities.Capability.NOMINAL_CLASS)) {
/*  356: 602 */         result.add("-nominal-class");
/*  357:     */       }
/*  358: 604 */       if (isEnabled(Capabilities.Capability.NUMERIC_CLASS)) {
/*  359: 605 */         result.add("-numeric-class");
/*  360:     */       }
/*  361: 607 */       if (isEnabled(Capabilities.Capability.STRING_CLASS)) {
/*  362: 608 */         result.add("-string-class");
/*  363:     */       }
/*  364: 610 */       if (isEnabled(Capabilities.Capability.DATE_CLASS)) {
/*  365: 611 */         result.add("-date-class");
/*  366:     */       }
/*  367: 613 */       if (isEnabled(Capabilities.Capability.RELATIONAL_CLASS)) {
/*  368: 614 */         result.add("-relational-class");
/*  369:     */       }
/*  370: 616 */       if (isEnabled(Capabilities.Capability.MISSING_CLASS_VALUES)) {
/*  371: 617 */         result.add("-missing-class-values");
/*  372:     */       }
/*  373:     */     }
/*  374: 621 */     if (isEnabled(Capabilities.Capability.UNARY_ATTRIBUTES)) {
/*  375: 622 */       result.add("-unary-atts");
/*  376:     */     }
/*  377: 624 */     if (isEnabled(Capabilities.Capability.BINARY_ATTRIBUTES)) {
/*  378: 625 */       result.add("-binary-atts");
/*  379:     */     }
/*  380: 627 */     if (isEnabled(Capabilities.Capability.NOMINAL_ATTRIBUTES)) {
/*  381: 628 */       result.add("-nominal-atts");
/*  382:     */     }
/*  383: 630 */     if (isEnabled(Capabilities.Capability.NUMERIC_ATTRIBUTES)) {
/*  384: 631 */       result.add("-numeric-atts");
/*  385:     */     }
/*  386: 633 */     if (isEnabled(Capabilities.Capability.STRING_ATTRIBUTES)) {
/*  387: 634 */       result.add("-string-atts");
/*  388:     */     }
/*  389: 636 */     if (isEnabled(Capabilities.Capability.DATE_ATTRIBUTES)) {
/*  390: 637 */       result.add("-date-atts");
/*  391:     */     }
/*  392: 639 */     if (isEnabled(Capabilities.Capability.RELATIONAL_ATTRIBUTES)) {
/*  393: 640 */       result.add("-relational-atts");
/*  394:     */     }
/*  395: 642 */     if (isEnabled(Capabilities.Capability.MISSING_VALUES)) {
/*  396: 643 */       result.add("-missing-att-values");
/*  397:     */     }
/*  398: 647 */     if (isEnabledNot(Capabilities.Capability.NO_CLASS)) {
/*  399: 648 */       result.add("-not-no-class");
/*  400:     */     }
/*  401: 650 */     if (isEnabledNot(Capabilities.Capability.UNARY_CLASS)) {
/*  402: 651 */       result.add("-not-unary-class");
/*  403:     */     }
/*  404: 653 */     if (isEnabledNot(Capabilities.Capability.BINARY_CLASS)) {
/*  405: 654 */       result.add("-not-binary-class");
/*  406:     */     }
/*  407: 656 */     if (isEnabledNot(Capabilities.Capability.NOMINAL_CLASS)) {
/*  408: 657 */       result.add("-not-nominal-class");
/*  409:     */     }
/*  410: 659 */     if (isEnabledNot(Capabilities.Capability.NUMERIC_CLASS)) {
/*  411: 660 */       result.add("-not-numeric-class");
/*  412:     */     }
/*  413: 662 */     if (isEnabledNot(Capabilities.Capability.STRING_CLASS)) {
/*  414: 663 */       result.add("-not-string-class");
/*  415:     */     }
/*  416: 665 */     if (isEnabledNot(Capabilities.Capability.DATE_CLASS)) {
/*  417: 666 */       result.add("-not-date-class");
/*  418:     */     }
/*  419: 668 */     if (isEnabledNot(Capabilities.Capability.RELATIONAL_CLASS)) {
/*  420: 669 */       result.add("-not-relational-class");
/*  421:     */     }
/*  422: 671 */     if (isEnabledNot(Capabilities.Capability.MISSING_CLASS_VALUES)) {
/*  423: 672 */       result.add("-not-missing-class-values");
/*  424:     */     }
/*  425: 675 */     if (isEnabledNot(Capabilities.Capability.UNARY_ATTRIBUTES)) {
/*  426: 676 */       result.add("-not-unary-atts");
/*  427:     */     }
/*  428: 678 */     if (isEnabledNot(Capabilities.Capability.BINARY_ATTRIBUTES)) {
/*  429: 679 */       result.add("-not-binary-atts");
/*  430:     */     }
/*  431: 681 */     if (isEnabledNot(Capabilities.Capability.NOMINAL_ATTRIBUTES)) {
/*  432: 682 */       result.add("-not-nominal-atts");
/*  433:     */     }
/*  434: 684 */     if (isEnabledNot(Capabilities.Capability.NUMERIC_ATTRIBUTES)) {
/*  435: 685 */       result.add("-not-numeric-atts");
/*  436:     */     }
/*  437: 687 */     if (isEnabledNot(Capabilities.Capability.STRING_ATTRIBUTES)) {
/*  438: 688 */       result.add("-not-string-atts");
/*  439:     */     }
/*  440: 690 */     if (isEnabledNot(Capabilities.Capability.DATE_ATTRIBUTES)) {
/*  441: 691 */       result.add("-not-date-atts");
/*  442:     */     }
/*  443: 693 */     if (isEnabledNot(Capabilities.Capability.RELATIONAL_ATTRIBUTES)) {
/*  444: 694 */       result.add("-not-relational-atts");
/*  445:     */     }
/*  446: 696 */     if (isEnabledNot(Capabilities.Capability.MISSING_VALUES)) {
/*  447: 697 */       result.add("-not-missing-att-values");
/*  448:     */     }
/*  449: 700 */     if (isEnabled(Capabilities.Capability.ONLY_MULTIINSTANCE)) {
/*  450: 701 */       result.add("-only-multi-instance");
/*  451:     */     }
/*  452: 704 */     if (getHandler() != null)
/*  453:     */     {
/*  454: 705 */       result.add("-W");
/*  455: 706 */       result.add(getHandler().getClass().getName());
/*  456: 707 */       if ((getHandler() instanceof OptionHandler))
/*  457:     */       {
/*  458: 708 */         result.add("--");
/*  459: 709 */         String[] options = ((OptionHandler)getHandler()).getOptions();
/*  460: 710 */         for (int i = 0; i < options.length; i++) {
/*  461: 711 */           result.add(options[i]);
/*  462:     */         }
/*  463:     */       }
/*  464:     */     }
/*  465: 714 */     else if (getFilename().length() != 0)
/*  466:     */     {
/*  467: 715 */       result.add("-t");
/*  468: 716 */       result.add(getFilename());
/*  469: 717 */       result.add("-c");
/*  470: 718 */       result.add(this.m_ClassIndex.getSingleIndex());
/*  471:     */     }
/*  472: 721 */     if (this.m_Superclass.length() != 0)
/*  473:     */     {
/*  474: 722 */       result.add("-superclass");
/*  475: 723 */       result.add(this.m_Superclass);
/*  476:     */     }
/*  477:     */     else
/*  478:     */     {
/*  479: 725 */       result.add("-packages");
/*  480: 726 */       result.add(this.m_Packages.toString().replaceAll("\\[", "").replaceAll("\\]", ""));
/*  481:     */     }
/*  482: 730 */     return (String[])result.toArray(new String[result.size()]);
/*  483:     */   }
/*  484:     */   
/*  485:     */   public void setHandler(CapabilitiesHandler value)
/*  486:     */   {
/*  487: 739 */     this.m_Handler = value;
/*  488: 740 */     setCapabilities(this.m_Handler.getCapabilities());
/*  489:     */   }
/*  490:     */   
/*  491:     */   public CapabilitiesHandler getHandler()
/*  492:     */   {
/*  493: 750 */     return this.m_Handler;
/*  494:     */   }
/*  495:     */   
/*  496:     */   public void setFilename(String value)
/*  497:     */   {
/*  498: 762 */     this.m_Filename = value;
/*  499: 764 */     if (this.m_Filename.length() != 0) {
/*  500:     */       try
/*  501:     */       {
/*  502: 766 */         Instances insts = new Instances(new BufferedReader(new FileReader(this.m_Filename)));
/*  503: 767 */         this.m_ClassIndex.setUpper(insts.numAttributes());
/*  504: 768 */         insts.setClassIndex(Integer.parseInt(getClassIndex()) - 1);
/*  505:     */         
/*  506: 770 */         setCapabilities(Capabilities.forInstances(insts));
/*  507:     */       }
/*  508:     */       catch (Exception e)
/*  509:     */       {
/*  510: 772 */         e.printStackTrace();
/*  511:     */       }
/*  512:     */     }
/*  513:     */   }
/*  514:     */   
/*  515:     */   public String getFilename()
/*  516:     */   {
/*  517: 783 */     return this.m_Filename;
/*  518:     */   }
/*  519:     */   
/*  520:     */   public void setClassIndex(String value)
/*  521:     */   {
/*  522: 792 */     if (value.equals("-1")) {
/*  523: 793 */       this.m_ClassIndex = null;
/*  524:     */     } else {
/*  525: 795 */       this.m_ClassIndex = new SingleIndex(value);
/*  526:     */     }
/*  527:     */   }
/*  528:     */   
/*  529:     */   public String getClassIndex()
/*  530:     */   {
/*  531: 805 */     if (this.m_ClassIndex == null) {
/*  532: 806 */       return "-1";
/*  533:     */     }
/*  534: 808 */     return "" + this.m_ClassIndex.getIndex();
/*  535:     */   }
/*  536:     */   
/*  537:     */   public void enable(Capabilities.Capability c)
/*  538:     */   {
/*  539: 818 */     this.m_Capabilities.enable(c);
/*  540:     */   }
/*  541:     */   
/*  542:     */   public boolean isEnabled(Capabilities.Capability c)
/*  543:     */   {
/*  544: 828 */     return this.m_Capabilities.handles(c);
/*  545:     */   }
/*  546:     */   
/*  547:     */   public void disable(Capabilities.Capability c)
/*  548:     */   {
/*  549: 837 */     this.m_Capabilities.disable(c);
/*  550:     */   }
/*  551:     */   
/*  552:     */   public void enableNot(Capabilities.Capability c)
/*  553:     */   {
/*  554: 846 */     this.m_NotCapabilities.enable(c);
/*  555:     */   }
/*  556:     */   
/*  557:     */   public boolean isEnabledNot(Capabilities.Capability c)
/*  558:     */   {
/*  559: 856 */     return this.m_NotCapabilities.handles(c);
/*  560:     */   }
/*  561:     */   
/*  562:     */   public void disableNot(Capabilities.Capability c)
/*  563:     */   {
/*  564: 865 */     this.m_NotCapabilities.disable(c);
/*  565:     */   }
/*  566:     */   
/*  567:     */   public boolean handles(Capabilities.Capability c)
/*  568:     */   {
/*  569: 875 */     return this.m_Capabilities.handles(c);
/*  570:     */   }
/*  571:     */   
/*  572:     */   public Capabilities getCapabilities()
/*  573:     */   {
/*  574: 886 */     return this.m_Capabilities;
/*  575:     */   }
/*  576:     */   
/*  577:     */   public void setCapabilities(Capabilities c)
/*  578:     */   {
/*  579: 895 */     this.m_Capabilities = ((Capabilities)c.clone());
/*  580:     */   }
/*  581:     */   
/*  582:     */   public Capabilities getNotCapabilities()
/*  583:     */   {
/*  584: 905 */     return this.m_NotCapabilities;
/*  585:     */   }
/*  586:     */   
/*  587:     */   public void setNotCapabilities(Capabilities c)
/*  588:     */   {
/*  589: 914 */     this.m_NotCapabilities = ((Capabilities)c.clone());
/*  590:     */   }
/*  591:     */   
/*  592:     */   public Vector<String> getMatches()
/*  593:     */   {
/*  594: 923 */     return this.m_Matches;
/*  595:     */   }
/*  596:     */   
/*  597:     */   public Vector<String> getMisses()
/*  598:     */   {
/*  599: 932 */     return this.m_Misses;
/*  600:     */   }
/*  601:     */   
/*  602:     */   public Vector<String> find()
/*  603:     */   {
/*  604: 949 */     this.m_Matches = new Vector();
/*  605: 950 */     this.m_Misses = new Vector();
/*  606:     */     
/*  607: 952 */     Vector<String> list = ClassDiscovery.find(this.m_Superclass, (String[])this.m_Packages.toArray(new String[this.m_Packages.size()]));
/*  608: 954 */     for (int i = 0; i < list.size(); i++) {
/*  609:     */       try
/*  610:     */       {
/*  611: 956 */         Class<?> cls = Class.forName((String)list.get(i));
/*  612: 957 */         Object obj = cls.newInstance();
/*  613: 960 */         if (cls != getClass()) {
/*  614: 965 */           if ((obj instanceof CapabilitiesHandler))
/*  615:     */           {
/*  616: 970 */             CapabilitiesHandler handler = (CapabilitiesHandler)obj;
/*  617: 971 */             Capabilities caps = handler.getCapabilities();
/*  618: 972 */             boolean fits = true;
/*  619: 973 */             for (Capabilities.Capability cap : Capabilities.Capability.values()) {
/*  620: 974 */               if ((this.m_Capabilities.handles(cap)) && 
/*  621: 975 */                 (!caps.handles(cap)))
/*  622:     */               {
/*  623: 976 */                 fits = false;
/*  624: 977 */                 break;
/*  625:     */               }
/*  626:     */             }
/*  627: 981 */             if (!fits)
/*  628:     */             {
/*  629: 982 */               this.m_Misses.add(list.get(i));
/*  630:     */             }
/*  631:     */             else
/*  632:     */             {
/*  633: 987 */               for (Capabilities.Capability cap : Capabilities.Capability.values()) {
/*  634: 988 */                 if ((this.m_NotCapabilities.handles(cap)) && 
/*  635: 989 */                   (caps.handles(cap)))
/*  636:     */                 {
/*  637: 990 */                   fits = false;
/*  638: 991 */                   break;
/*  639:     */                 }
/*  640:     */               }
/*  641: 995 */               if (!fits) {
/*  642: 996 */                 this.m_Misses.add(list.get(i));
/*  643:1001 */               } else if (caps.getMinimumNumberInstances() > this.m_Capabilities.getMinimumNumberInstances()) {
/*  644:1003 */                 this.m_Misses.add(list.get(i));
/*  645:     */               } else {
/*  646:1008 */                 this.m_Matches.add(list.get(i));
/*  647:     */               }
/*  648:     */             }
/*  649:     */           }
/*  650:     */         }
/*  651:     */       }
/*  652:     */       catch (Exception e) {}
/*  653:     */     }
/*  654:1014 */     return this.m_Matches;
/*  655:     */   }
/*  656:     */   
/*  657:     */   public String getRevision()
/*  658:     */   {
/*  659:1024 */     return RevisionUtils.extract("$Revision: 11004 $");
/*  660:     */   }
/*  661:     */   
/*  662:     */   public static void main(String[] args)
/*  663:     */   {
/*  664:1041 */     boolean printMisses = false;
/*  665:     */     try
/*  666:     */     {
/*  667:1044 */       FindWithCapabilities find = new FindWithCapabilities();
/*  668:     */       try
/*  669:     */       {
/*  670:1047 */         printMisses = Utils.getFlag("misses", args);
/*  671:1048 */         find.setOptions(args);
/*  672:1049 */         Utils.checkForRemainingOptions(args);
/*  673:     */       }
/*  674:     */       catch (Exception ex)
/*  675:     */       {
/*  676:1051 */         String result = ex.getMessage() + "\n\n" + find.getClass().getName().replaceAll(".*\\.", "") + " Options:\n\n";
/*  677:     */         
/*  678:1053 */         Enumeration<Option> enm = find.listOptions();
/*  679:1054 */         while (enm.hasMoreElements())
/*  680:     */         {
/*  681:1055 */           Option option = (Option)enm.nextElement();
/*  682:1056 */           result = result + option.synopsis() + "\n" + option.description() + "\n";
/*  683:     */         }
/*  684:1058 */         throw new Exception(result);
/*  685:     */       }
/*  686:1061 */       System.out.println("\nSearching for the following Capabilities:");
/*  687:     */       
/*  688:1063 */       System.out.print("- allowed: ");
/*  689:1064 */       Iterator<Capabilities.Capability> iter = find.getCapabilities().capabilities();
/*  690:1065 */       boolean first = true;
/*  691:1066 */       while (iter.hasNext())
/*  692:     */       {
/*  693:1067 */         if (!first) {
/*  694:1068 */           System.out.print(", ");
/*  695:     */         }
/*  696:1070 */         first = false;
/*  697:1071 */         System.out.print(iter.next());
/*  698:     */       }
/*  699:1073 */       System.out.println();
/*  700:     */       
/*  701:1075 */       System.out.print("- not allowed: ");
/*  702:1076 */       iter = find.getNotCapabilities().capabilities();
/*  703:1077 */       first = true;
/*  704:1078 */       if (iter.hasNext())
/*  705:     */       {
/*  706:1079 */         while (iter.hasNext())
/*  707:     */         {
/*  708:1080 */           if (!first) {
/*  709:1081 */             System.out.print(", ");
/*  710:     */           }
/*  711:1083 */           first = false;
/*  712:1084 */           System.out.print(iter.next());
/*  713:     */         }
/*  714:1086 */         System.out.println();
/*  715:     */       }
/*  716:     */       else
/*  717:     */       {
/*  718:1088 */         System.out.println("-");
/*  719:     */       }
/*  720:1091 */       find.find();
/*  721:     */       
/*  722:     */ 
/*  723:1094 */       Vector<String> list = find.getMatches();
/*  724:1095 */       if (list.size() == 1) {
/*  725:1096 */         System.out.println("\nFound " + list.size() + " class that matched the criteria:\n");
/*  726:     */       } else {
/*  727:1099 */         System.out.println("\nFound " + list.size() + " classes that matched the criteria:\n");
/*  728:     */       }
/*  729:1102 */       for (int i = 0; i < list.size(); i++) {
/*  730:1103 */         System.out.println((String)list.get(i));
/*  731:     */       }
/*  732:1107 */       if (printMisses)
/*  733:     */       {
/*  734:1108 */         list = find.getMisses();
/*  735:1109 */         if (list.size() == 1) {
/*  736:1110 */           System.out.println("\nFound " + list.size() + " class that didn't match the criteria:\n");
/*  737:     */         } else {
/*  738:1113 */           System.out.println("\nFound " + list.size() + " classes that didn't match the criteria:\n");
/*  739:     */         }
/*  740:1116 */         for (i = 0; i < list.size(); i++) {
/*  741:1117 */           System.out.println((String)list.get(i));
/*  742:     */         }
/*  743:     */       }
/*  744:1121 */       System.out.println();
/*  745:     */     }
/*  746:     */     catch (Exception ex)
/*  747:     */     {
/*  748:1123 */       System.err.println(ex.getMessage());
/*  749:     */     }
/*  750:     */   }
/*  751:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.FindWithCapabilities
 * JD-Core Version:    0.7.0.1
 */