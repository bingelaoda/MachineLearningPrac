/*    1:     */ package weka.core.converters;
/*    2:     */ 
/*    3:     */ import java.io.BufferedReader;
/*    4:     */ import java.io.File;
/*    5:     */ import java.io.IOException;
/*    6:     */ import java.io.InputStream;
/*    7:     */ import java.io.InputStreamReader;
/*    8:     */ import java.io.Reader;
/*    9:     */ import java.io.StreamTokenizer;
/*   10:     */ import java.io.StringReader;
/*   11:     */ import java.net.URL;
/*   12:     */ import java.text.ParseException;
/*   13:     */ import java.util.ArrayList;
/*   14:     */ import java.util.List;
/*   15:     */ import weka.core.Attribute;
/*   16:     */ import weka.core.DenseInstance;
/*   17:     */ import weka.core.Instance;
/*   18:     */ import weka.core.Instances;
/*   19:     */ import weka.core.RevisionHandler;
/*   20:     */ import weka.core.RevisionUtils;
/*   21:     */ import weka.core.SparseInstance;
/*   22:     */ import weka.core.Utils;
/*   23:     */ 
/*   24:     */ public class ArffLoader
/*   25:     */   extends AbstractFileLoader
/*   26:     */   implements BatchConverter, IncrementalConverter, URLSourcedLoader
/*   27:     */ {
/*   28:     */   static final long serialVersionUID = 2726929550544048587L;
/*   29:  64 */   public static String FILE_EXTENSION = ".arff";
/*   30:  65 */   public static String FILE_EXTENSION_COMPRESSED = FILE_EXTENSION + ".gz";
/*   31:     */   protected String m_URL;
/*   32:     */   protected transient Reader m_sourceReader;
/*   33:     */   protected transient ArffReader m_ArffReader;
/*   34:     */   protected boolean m_retainStringVals;
/*   35:     */   
/*   36:     */   public ArffLoader()
/*   37:     */   {
/*   38:  68 */     this.m_URL = "http://";
/*   39:     */     
/*   40:     */ 
/*   41:  71 */     this.m_sourceReader = null;
/*   42:     */     
/*   43:     */ 
/*   44:  74 */     this.m_ArffReader = null;
/*   45:     */   }
/*   46:     */   
/*   47:     */   public static class ArffReader
/*   48:     */     implements RevisionHandler
/*   49:     */   {
/*   50:     */     protected StreamTokenizer m_Tokenizer;
/*   51:     */     protected double[] m_ValueBuffer;
/*   52:     */     protected int[] m_IndicesBuffer;
/*   53:     */     protected List<Integer> m_stringAttIndices;
/*   54:     */     protected Instances m_Data;
/*   55:     */     protected int m_Lines;
/*   56: 134 */     protected boolean m_batchMode = true;
/*   57: 140 */     protected boolean m_retainStringValues = false;
/*   58:     */     protected String m_fieldSeparator;
/*   59:     */     protected List<String> m_enclosures;
/*   60:     */     
/*   61:     */     public ArffReader(Reader reader)
/*   62:     */       throws IOException
/*   63:     */     {
/*   64: 157 */       this.m_retainStringValues = true;
/*   65: 158 */       this.m_batchMode = true;
/*   66: 159 */       this.m_Tokenizer = new StreamTokenizer(reader);
/*   67: 160 */       initTokenizer();
/*   68:     */       
/*   69: 162 */       readHeader(1000);
/*   70: 163 */       initBuffers();
/*   71:     */       Instance inst;
/*   72: 166 */       while ((inst = readInstance(this.m_Data)) != null) {
/*   73: 167 */         this.m_Data.add(inst);
/*   74:     */       }
/*   75: 170 */       compactify();
/*   76:     */     }
/*   77:     */     
/*   78:     */     public ArffReader(Reader reader, int capacity)
/*   79:     */       throws IOException
/*   80:     */     {
/*   81: 174 */       this(reader, capacity, true);
/*   82:     */     }
/*   83:     */     
/*   84:     */     public ArffReader(Reader reader, int capacity, boolean batch)
/*   85:     */       throws IOException
/*   86:     */     {
/*   87: 192 */       this.m_batchMode = batch;
/*   88: 193 */       if (batch) {
/*   89: 194 */         this.m_retainStringValues = true;
/*   90:     */       }
/*   91: 197 */       if (capacity < 0) {
/*   92: 198 */         throw new IllegalArgumentException("Capacity has to be positive!");
/*   93:     */       }
/*   94: 201 */       this.m_Tokenizer = new StreamTokenizer(reader);
/*   95: 202 */       initTokenizer();
/*   96:     */       
/*   97: 204 */       readHeader(capacity);
/*   98: 205 */       initBuffers();
/*   99:     */     }
/*  100:     */     
/*  101:     */     public ArffReader(Reader reader, Instances template, int lines, String... fieldSepAndEnclosures)
/*  102:     */       throws IOException
/*  103:     */     {
/*  104: 225 */       this(reader, template, lines, 100, true, fieldSepAndEnclosures);
/*  105:     */       Instance inst;
/*  106: 228 */       while ((inst = readInstance(this.m_Data)) != null) {
/*  107: 229 */         this.m_Data.add(inst);
/*  108:     */       }
/*  109: 232 */       compactify();
/*  110:     */     }
/*  111:     */     
/*  112:     */     public ArffReader(Reader reader, Instances template, int lines, int capacity, String... fieldSepAndEnclosures)
/*  113:     */       throws IOException
/*  114:     */     {
/*  115: 254 */       this(reader, template, lines, capacity, false, fieldSepAndEnclosures);
/*  116:     */     }
/*  117:     */     
/*  118:     */     public ArffReader(Reader reader, Instances template, int lines, int capacity, boolean batch, String... fieldSepAndEnclosures)
/*  119:     */       throws IOException
/*  120:     */     {
/*  121: 278 */       this.m_batchMode = batch;
/*  122: 279 */       if (batch) {
/*  123: 280 */         this.m_retainStringValues = true;
/*  124:     */       }
/*  125: 283 */       if ((fieldSepAndEnclosures != null) && (fieldSepAndEnclosures.length > 0))
/*  126:     */       {
/*  127: 284 */         if ((fieldSepAndEnclosures[0] != null) && (fieldSepAndEnclosures[0].length() > 0)) {
/*  128: 286 */           this.m_fieldSeparator = fieldSepAndEnclosures[0];
/*  129:     */         }
/*  130: 289 */         if (fieldSepAndEnclosures.length > 1)
/*  131:     */         {
/*  132: 291 */           this.m_enclosures = new ArrayList();
/*  133: 292 */           for (int i = 1; i < fieldSepAndEnclosures.length; i++) {
/*  134: 293 */             if ((fieldSepAndEnclosures[i] != null) && (fieldSepAndEnclosures[i].length() > 0)) {
/*  135: 295 */               this.m_enclosures.add(fieldSepAndEnclosures[i]);
/*  136:     */             }
/*  137:     */           }
/*  138: 299 */           if (this.m_enclosures.size() == 0) {
/*  139: 300 */             this.m_enclosures = null;
/*  140:     */           }
/*  141:     */         }
/*  142:     */       }
/*  143: 305 */       this.m_Lines = lines;
/*  144: 306 */       this.m_Tokenizer = new StreamTokenizer(reader);
/*  145: 307 */       initTokenizer();
/*  146:     */       
/*  147: 309 */       this.m_Data = new Instances(template, capacity);
/*  148: 310 */       initBuffers();
/*  149:     */     }
/*  150:     */     
/*  151:     */     protected void initBuffers()
/*  152:     */     {
/*  153: 320 */       this.m_ValueBuffer = new double[this.m_Data.numAttributes()];
/*  154: 321 */       this.m_IndicesBuffer = new int[this.m_Data.numAttributes()];
/*  155:     */       
/*  156: 323 */       this.m_stringAttIndices = new ArrayList();
/*  157: 324 */       if (this.m_Data.checkForStringAttributes()) {
/*  158: 325 */         for (int i = 0; i < this.m_Data.numAttributes(); i++) {
/*  159: 326 */           if (this.m_Data.attribute(i).isString()) {
/*  160: 327 */             this.m_stringAttIndices.add(Integer.valueOf(i));
/*  161:     */           }
/*  162:     */         }
/*  163:     */       }
/*  164:     */     }
/*  165:     */     
/*  166:     */     protected void compactify()
/*  167:     */     {
/*  168: 337 */       if (this.m_Data != null) {
/*  169: 338 */         this.m_Data.compactify();
/*  170:     */       }
/*  171:     */     }
/*  172:     */     
/*  173:     */     protected void errorMessage(String msg)
/*  174:     */       throws IOException
/*  175:     */     {
/*  176: 349 */       String str = msg + ", read " + this.m_Tokenizer.toString();
/*  177: 350 */       if (this.m_Lines > 0)
/*  178:     */       {
/*  179: 351 */         int line = Integer.parseInt(str.replaceAll(".* line ", ""));
/*  180: 352 */         str = str.replaceAll(" line .*", " line " + (this.m_Lines + line - 1));
/*  181:     */       }
/*  182: 354 */       throw new IOException(str);
/*  183:     */     }
/*  184:     */     
/*  185:     */     public int getLineNo()
/*  186:     */     {
/*  187: 363 */       return this.m_Lines + this.m_Tokenizer.lineno();
/*  188:     */     }
/*  189:     */     
/*  190:     */     protected void getFirstToken()
/*  191:     */       throws IOException
/*  192:     */     {
/*  193: 372 */       while (this.m_Tokenizer.nextToken() == 10) {}
/*  194: 376 */       if ((this.m_Tokenizer.ttype == 39) || (this.m_Tokenizer.ttype == 34)) {
/*  195: 377 */         this.m_Tokenizer.ttype = -3;
/*  196: 378 */       } else if ((this.m_Tokenizer.ttype == -3) && (this.m_Tokenizer.sval.equals("?"))) {
/*  197: 380 */         this.m_Tokenizer.ttype = 63;
/*  198:     */       }
/*  199:     */     }
/*  200:     */     
/*  201:     */     protected void getIndex()
/*  202:     */       throws IOException
/*  203:     */     {
/*  204: 390 */       if (this.m_Tokenizer.nextToken() == 10) {
/*  205: 391 */         errorMessage("premature end of line");
/*  206:     */       }
/*  207: 393 */       if (this.m_Tokenizer.ttype == -1) {
/*  208: 394 */         errorMessage("premature end of file");
/*  209:     */       }
/*  210:     */     }
/*  211:     */     
/*  212:     */     protected void getLastToken(boolean endOfFileOk)
/*  213:     */       throws IOException
/*  214:     */     {
/*  215: 405 */       if ((this.m_Tokenizer.nextToken() != 10) && ((this.m_Tokenizer.ttype != -1) || (!endOfFileOk))) {
/*  216: 407 */         errorMessage("end of line expected");
/*  217:     */       }
/*  218:     */     }
/*  219:     */     
/*  220:     */     protected double getInstanceWeight()
/*  221:     */       throws IOException
/*  222:     */     {
/*  223: 418 */       double weight = (0.0D / 0.0D);
/*  224: 419 */       this.m_Tokenizer.nextToken();
/*  225: 420 */       if ((this.m_Tokenizer.ttype == 10) || (this.m_Tokenizer.ttype == -1)) {
/*  226: 422 */         return weight;
/*  227:     */       }
/*  228: 426 */       if (this.m_Tokenizer.ttype == 123)
/*  229:     */       {
/*  230: 427 */         this.m_Tokenizer.nextToken();
/*  231: 428 */         String weightS = this.m_Tokenizer.sval;
/*  232:     */         try
/*  233:     */         {
/*  234: 431 */           weight = Double.parseDouble(weightS);
/*  235:     */         }
/*  236:     */         catch (NumberFormatException e)
/*  237:     */         {
/*  238: 434 */           return weight;
/*  239:     */         }
/*  240: 437 */         this.m_Tokenizer.nextToken();
/*  241: 438 */         if (this.m_Tokenizer.ttype != 125) {
/*  242: 439 */           errorMessage("Problem reading instance weight");
/*  243:     */         }
/*  244:     */       }
/*  245: 442 */       return weight;
/*  246:     */     }
/*  247:     */     
/*  248:     */     protected void getNextToken()
/*  249:     */       throws IOException
/*  250:     */     {
/*  251: 451 */       if (this.m_Tokenizer.nextToken() == 10) {
/*  252: 452 */         errorMessage("premature end of line");
/*  253:     */       }
/*  254: 454 */       if (this.m_Tokenizer.ttype == -1) {
/*  255: 455 */         errorMessage("premature end of file");
/*  256: 456 */       } else if ((this.m_Tokenizer.ttype == 39) || (this.m_Tokenizer.ttype == 34)) {
/*  257: 457 */         this.m_Tokenizer.ttype = -3;
/*  258: 458 */       } else if ((this.m_Tokenizer.ttype == -3) && (this.m_Tokenizer.sval.equals("?"))) {
/*  259: 460 */         this.m_Tokenizer.ttype = 63;
/*  260:     */       }
/*  261:     */     }
/*  262:     */     
/*  263:     */     protected void initTokenizer()
/*  264:     */     {
/*  265: 468 */       this.m_Tokenizer.resetSyntax();
/*  266: 469 */       this.m_Tokenizer.whitespaceChars(0, 32);
/*  267: 470 */       this.m_Tokenizer.wordChars(33, 255);
/*  268: 471 */       if (this.m_fieldSeparator != null) {
/*  269: 472 */         this.m_Tokenizer.whitespaceChars(this.m_fieldSeparator.charAt(0), this.m_fieldSeparator.charAt(0));
/*  270:     */       } else {
/*  271: 475 */         this.m_Tokenizer.whitespaceChars(44, 44);
/*  272:     */       }
/*  273: 477 */       this.m_Tokenizer.commentChar(37);
/*  274: 478 */       if ((this.m_enclosures != null) && (this.m_enclosures.size() > 0))
/*  275:     */       {
/*  276: 479 */         for (String e : this.m_enclosures) {
/*  277: 480 */           this.m_Tokenizer.quoteChar(e.charAt(0));
/*  278:     */         }
/*  279:     */       }
/*  280:     */       else
/*  281:     */       {
/*  282: 483 */         this.m_Tokenizer.quoteChar(34);
/*  283: 484 */         this.m_Tokenizer.quoteChar(39);
/*  284:     */       }
/*  285: 486 */       this.m_Tokenizer.ordinaryChar(123);
/*  286: 487 */       this.m_Tokenizer.ordinaryChar(125);
/*  287: 488 */       this.m_Tokenizer.eolIsSignificant(true);
/*  288:     */     }
/*  289:     */     
/*  290:     */     public Instance readInstance(Instances structure)
/*  291:     */       throws IOException
/*  292:     */     {
/*  293: 500 */       return readInstance(structure, true);
/*  294:     */     }
/*  295:     */     
/*  296:     */     public Instance readInstance(Instances structure, boolean flag)
/*  297:     */       throws IOException
/*  298:     */     {
/*  299: 514 */       return getInstance(structure, flag);
/*  300:     */     }
/*  301:     */     
/*  302:     */     protected Instance getInstance(Instances structure, boolean flag)
/*  303:     */       throws IOException
/*  304:     */     {
/*  305: 528 */       this.m_Data = structure;
/*  306: 531 */       if (this.m_Data.numAttributes() == 0) {
/*  307: 532 */         errorMessage("no header information available");
/*  308:     */       }
/*  309: 536 */       getFirstToken();
/*  310: 537 */       if (this.m_Tokenizer.ttype == -1) {
/*  311: 538 */         return null;
/*  312:     */       }
/*  313: 542 */       if (this.m_Tokenizer.ttype == 123) {
/*  314: 543 */         return getInstanceSparse(flag);
/*  315:     */       }
/*  316: 545 */       return getInstanceFull(flag);
/*  317:     */     }
/*  318:     */     
/*  319:     */     protected Instance getInstanceSparse(boolean flag)
/*  320:     */       throws IOException
/*  321:     */     {
/*  322: 557 */       int numValues = 0;int maxIndex = -1;
/*  323: 561 */       if ((!this.m_batchMode) && (!this.m_retainStringValues) && (this.m_stringAttIndices != null)) {
/*  324: 562 */         for (int i = 0; i < this.m_stringAttIndices.size(); i++) {
/*  325: 563 */           this.m_Data.attribute(((Integer)this.m_stringAttIndices.get(i)).intValue()).setStringValue(null);
/*  326:     */         }
/*  327:     */       }
/*  328:     */       for (;;)
/*  329:     */       {
/*  330: 570 */         getIndex();
/*  331: 571 */         if (this.m_Tokenizer.ttype == 125) {
/*  332:     */           break;
/*  333:     */         }
/*  334:     */         try
/*  335:     */         {
/*  336: 577 */           this.m_IndicesBuffer[numValues] = Integer.valueOf(this.m_Tokenizer.sval).intValue();
/*  337:     */         }
/*  338:     */         catch (NumberFormatException e)
/*  339:     */         {
/*  340: 580 */           errorMessage("index number expected");
/*  341:     */         }
/*  342: 582 */         if (this.m_IndicesBuffer[numValues] <= maxIndex) {
/*  343: 583 */           errorMessage("indices have to be ordered");
/*  344:     */         }
/*  345: 585 */         if ((this.m_IndicesBuffer[numValues] < 0) || (this.m_IndicesBuffer[numValues] >= this.m_Data.numAttributes())) {
/*  346: 587 */           errorMessage("index out of bounds");
/*  347:     */         }
/*  348: 589 */         maxIndex = this.m_IndicesBuffer[numValues];
/*  349:     */         
/*  350:     */ 
/*  351: 592 */         getNextToken();
/*  352: 595 */         if (this.m_Tokenizer.ttype == 63)
/*  353:     */         {
/*  354: 596 */           this.m_ValueBuffer[numValues] = Utils.missingValue();
/*  355:     */         }
/*  356:     */         else
/*  357:     */         {
/*  358: 600 */           if (this.m_Tokenizer.ttype != -3) {
/*  359: 601 */             errorMessage("not a valid value");
/*  360:     */           }
/*  361: 603 */           switch (this.m_Data.attribute(this.m_IndicesBuffer[numValues]).type())
/*  362:     */           {
/*  363:     */           case 1: 
/*  364: 606 */             int valIndex = this.m_Data.attribute(this.m_IndicesBuffer[numValues]).indexOfValue(this.m_Tokenizer.sval);
/*  365: 609 */             if (valIndex == -1) {
/*  366: 610 */               errorMessage("nominal value not declared in header");
/*  367:     */             }
/*  368: 612 */             this.m_ValueBuffer[numValues] = valIndex;
/*  369: 613 */             break;
/*  370:     */           case 0: 
/*  371:     */             try
/*  372:     */             {
/*  373: 617 */               this.m_ValueBuffer[numValues] = Double.valueOf(this.m_Tokenizer.sval).doubleValue();
/*  374:     */             }
/*  375:     */             catch (NumberFormatException e)
/*  376:     */             {
/*  377: 620 */               errorMessage("number expected");
/*  378:     */             }
/*  379:     */           case 2: 
/*  380: 624 */             if ((this.m_batchMode) || (this.m_retainStringValues))
/*  381:     */             {
/*  382: 625 */               this.m_ValueBuffer[numValues] = this.m_Data.attribute(this.m_IndicesBuffer[numValues]).addStringValue(this.m_Tokenizer.sval);
/*  383:     */             }
/*  384:     */             else
/*  385:     */             {
/*  386: 629 */               this.m_ValueBuffer[numValues] = 0.0D;
/*  387: 630 */               this.m_Data.attribute(this.m_IndicesBuffer[numValues]).addStringValue(this.m_Tokenizer.sval);
/*  388:     */             }
/*  389: 633 */             break;
/*  390:     */           case 3: 
/*  391:     */             try
/*  392:     */             {
/*  393: 636 */               this.m_ValueBuffer[numValues] = this.m_Data.attribute(this.m_IndicesBuffer[numValues]).parseDate(this.m_Tokenizer.sval);
/*  394:     */             }
/*  395:     */             catch (ParseException e)
/*  396:     */             {
/*  397: 640 */               errorMessage("unparseable date: " + this.m_Tokenizer.sval);
/*  398:     */             }
/*  399:     */           case 4: 
/*  400:     */             try
/*  401:     */             {
/*  402: 645 */               ArffReader arff = new ArffReader(new StringReader(this.m_Tokenizer.sval), this.m_Data.attribute(this.m_IndicesBuffer[numValues]).relation(), 0, new String[0]);
/*  403:     */               
/*  404:     */ 
/*  405: 648 */               Instances data = arff.getData();
/*  406: 649 */               this.m_ValueBuffer[numValues] = this.m_Data.attribute(this.m_IndicesBuffer[numValues]).addRelation(data);
/*  407:     */             }
/*  408:     */             catch (Exception e)
/*  409:     */             {
/*  410: 652 */               throw new IOException(e.toString() + " of line " + getLineNo());
/*  411:     */             }
/*  412:     */           default: 
/*  413: 656 */             errorMessage("unknown attribute type in column " + this.m_IndicesBuffer[numValues]);
/*  414:     */           }
/*  415:     */         }
/*  416: 660 */         numValues++;
/*  417:     */       }
/*  418: 663 */       double weight = 1.0D;
/*  419: 664 */       if (flag)
/*  420:     */       {
/*  421: 666 */         weight = getInstanceWeight();
/*  422: 667 */         if (!Double.isNaN(weight)) {
/*  423: 668 */           getLastToken(true);
/*  424:     */         } else {
/*  425: 670 */           weight = 1.0D;
/*  426:     */         }
/*  427:     */       }
/*  428: 675 */       double[] tempValues = new double[numValues];
/*  429: 676 */       int[] tempIndices = new int[numValues];
/*  430: 677 */       System.arraycopy(this.m_ValueBuffer, 0, tempValues, 0, numValues);
/*  431: 678 */       System.arraycopy(this.m_IndicesBuffer, 0, tempIndices, 0, numValues);
/*  432: 679 */       Instance inst = new SparseInstance(weight, tempValues, tempIndices, this.m_Data.numAttributes());
/*  433:     */       
/*  434:     */ 
/*  435: 682 */       inst.setDataset(this.m_Data);
/*  436:     */       
/*  437: 684 */       return inst;
/*  438:     */     }
/*  439:     */     
/*  440:     */     protected Instance getInstanceFull(boolean flag)
/*  441:     */       throws IOException
/*  442:     */     {
/*  443: 695 */       double[] instance = new double[this.m_Data.numAttributes()];
/*  444: 699 */       for (int i = 0; i < this.m_Data.numAttributes(); i++)
/*  445:     */       {
/*  446: 701 */         if (i > 0) {
/*  447: 702 */           getNextToken();
/*  448:     */         }
/*  449: 706 */         if (this.m_Tokenizer.ttype == 63)
/*  450:     */         {
/*  451: 707 */           instance[i] = Utils.missingValue();
/*  452:     */         }
/*  453:     */         else
/*  454:     */         {
/*  455: 711 */           if (this.m_Tokenizer.ttype != -3) {
/*  456: 712 */             errorMessage("not a valid value");
/*  457:     */           }
/*  458: 714 */           switch (this.m_Data.attribute(i).type())
/*  459:     */           {
/*  460:     */           case 1: 
/*  461: 717 */             int index = this.m_Data.attribute(i).indexOfValue(this.m_Tokenizer.sval);
/*  462: 718 */             if (index == -1) {
/*  463: 719 */               errorMessage("nominal value not declared in header");
/*  464:     */             }
/*  465: 721 */             instance[i] = index;
/*  466: 722 */             break;
/*  467:     */           case 0: 
/*  468:     */             try
/*  469:     */             {
/*  470: 726 */               instance[i] = Double.valueOf(this.m_Tokenizer.sval).doubleValue();
/*  471:     */             }
/*  472:     */             catch (NumberFormatException e)
/*  473:     */             {
/*  474: 728 */               errorMessage("number expected");
/*  475:     */             }
/*  476:     */           case 2: 
/*  477: 732 */             if ((this.m_batchMode) || (this.m_retainStringValues))
/*  478:     */             {
/*  479: 733 */               instance[i] = this.m_Data.attribute(i).addStringValue(this.m_Tokenizer.sval);
/*  480:     */             }
/*  481:     */             else
/*  482:     */             {
/*  483: 736 */               instance[i] = 0.0D;
/*  484: 737 */               this.m_Data.attribute(i).setStringValue(this.m_Tokenizer.sval);
/*  485:     */             }
/*  486: 739 */             break;
/*  487:     */           case 3: 
/*  488:     */             try
/*  489:     */             {
/*  490: 742 */               instance[i] = this.m_Data.attribute(i).parseDate(this.m_Tokenizer.sval);
/*  491:     */             }
/*  492:     */             catch (ParseException e)
/*  493:     */             {
/*  494: 744 */               errorMessage("unparseable date: " + this.m_Tokenizer.sval);
/*  495:     */             }
/*  496:     */           case 4: 
/*  497:     */             try
/*  498:     */             {
/*  499: 749 */               ArffReader arff = new ArffReader(new StringReader(this.m_Tokenizer.sval), this.m_Data.attribute(i).relation(), 0, new String[0]);
/*  500:     */               
/*  501:     */ 
/*  502: 752 */               Instances data = arff.getData();
/*  503: 753 */               instance[i] = this.m_Data.attribute(i).addRelation(data);
/*  504:     */             }
/*  505:     */             catch (Exception e)
/*  506:     */             {
/*  507: 755 */               throw new IOException(e.toString() + " of line " + getLineNo());
/*  508:     */             }
/*  509:     */           default: 
/*  510: 759 */             errorMessage("unknown attribute type in column " + i);
/*  511:     */           }
/*  512:     */         }
/*  513:     */       }
/*  514: 764 */       double weight = 1.0D;
/*  515: 765 */       if (flag)
/*  516:     */       {
/*  517: 767 */         weight = getInstanceWeight();
/*  518: 768 */         if (!Double.isNaN(weight)) {
/*  519: 769 */           getLastToken(true);
/*  520:     */         } else {
/*  521: 771 */           weight = 1.0D;
/*  522:     */         }
/*  523:     */       }
/*  524: 776 */       Instance inst = new DenseInstance(weight, instance);
/*  525: 777 */       inst.setDataset(this.m_Data);
/*  526:     */       
/*  527: 779 */       return inst;
/*  528:     */     }
/*  529:     */     
/*  530:     */     protected void readHeader(int capacity)
/*  531:     */       throws IOException
/*  532:     */     {
/*  533: 789 */       this.m_Lines = 0;
/*  534: 790 */       String relationName = "";
/*  535:     */       
/*  536:     */ 
/*  537: 793 */       getFirstToken();
/*  538: 794 */       if (this.m_Tokenizer.ttype == -1) {
/*  539: 795 */         errorMessage("premature end of file");
/*  540:     */       }
/*  541: 797 */       if ("@relation".equalsIgnoreCase(this.m_Tokenizer.sval))
/*  542:     */       {
/*  543: 798 */         getNextToken();
/*  544: 799 */         relationName = this.m_Tokenizer.sval;
/*  545: 800 */         getLastToken(false);
/*  546:     */       }
/*  547:     */       else
/*  548:     */       {
/*  549: 802 */         errorMessage("keyword @relation expected");
/*  550:     */       }
/*  551: 806 */       ArrayList<Attribute> attributes = new ArrayList();
/*  552:     */       
/*  553:     */ 
/*  554: 809 */       getFirstToken();
/*  555: 810 */       if (this.m_Tokenizer.ttype == -1) {
/*  556: 811 */         errorMessage("premature end of file");
/*  557:     */       }
/*  558: 814 */       while ("@attribute".equalsIgnoreCase(this.m_Tokenizer.sval)) {
/*  559: 815 */         attributes = parseAttribute(attributes);
/*  560:     */       }
/*  561: 819 */       if (!"@data".equalsIgnoreCase(this.m_Tokenizer.sval)) {
/*  562: 820 */         errorMessage("keyword @data expected");
/*  563:     */       }
/*  564: 824 */       if (attributes.size() == 0) {
/*  565: 825 */         errorMessage("no attributes declared");
/*  566:     */       }
/*  567: 828 */       this.m_Data = new Instances(relationName, attributes, capacity);
/*  568:     */     }
/*  569:     */     
/*  570:     */     protected ArrayList<Attribute> parseAttribute(ArrayList<Attribute> attributes)
/*  571:     */       throws IOException
/*  572:     */     {
/*  573: 844 */       getNextToken();
/*  574: 845 */       String attributeName = this.m_Tokenizer.sval;
/*  575: 846 */       getNextToken();
/*  576: 849 */       if (this.m_Tokenizer.ttype == -3)
/*  577:     */       {
/*  578: 852 */         if ((this.m_Tokenizer.sval.equalsIgnoreCase("real")) || (this.m_Tokenizer.sval.equalsIgnoreCase("integer")) || (this.m_Tokenizer.sval.equalsIgnoreCase("numeric")))
/*  579:     */         {
/*  580: 857 */           attributes.add(new Attribute(attributeName, attributes.size()));
/*  581: 858 */           readTillEOL();
/*  582:     */         }
/*  583: 859 */         else if (this.m_Tokenizer.sval.equalsIgnoreCase("string"))
/*  584:     */         {
/*  585: 861 */           attributes.add(new Attribute(attributeName, (ArrayList)null, attributes.size()));
/*  586:     */           
/*  587: 863 */           readTillEOL();
/*  588:     */         }
/*  589: 864 */         else if (this.m_Tokenizer.sval.equalsIgnoreCase("date"))
/*  590:     */         {
/*  591: 866 */           String format = null;
/*  592: 867 */           if (this.m_Tokenizer.nextToken() != 10)
/*  593:     */           {
/*  594: 868 */             if ((this.m_Tokenizer.ttype != -3) && (this.m_Tokenizer.ttype != 39) && (this.m_Tokenizer.ttype != 34)) {
/*  595: 870 */               errorMessage("not a valid date format");
/*  596:     */             }
/*  597: 872 */             format = this.m_Tokenizer.sval;
/*  598: 873 */             readTillEOL();
/*  599:     */           }
/*  600:     */           else
/*  601:     */           {
/*  602: 875 */             this.m_Tokenizer.pushBack();
/*  603:     */           }
/*  604: 877 */           attributes.add(new Attribute(attributeName, format, attributes.size()));
/*  605:     */         }
/*  606: 880 */         else if (this.m_Tokenizer.sval.equalsIgnoreCase("relational"))
/*  607:     */         {
/*  608: 882 */           readTillEOL();
/*  609:     */           
/*  610:     */ 
/*  611:     */ 
/*  612: 886 */           ArrayList<Attribute> atts = attributes;
/*  613: 887 */           attributes = new ArrayList();
/*  614:     */           
/*  615:     */ 
/*  616:     */ 
/*  617: 891 */           getFirstToken();
/*  618: 892 */           if (this.m_Tokenizer.ttype == -1) {
/*  619: 893 */             errorMessage("premature end of file");
/*  620:     */           }
/*  621:     */           for (;;)
/*  622:     */           {
/*  623: 896 */             if ("@attribute".equalsIgnoreCase(this.m_Tokenizer.sval))
/*  624:     */             {
/*  625: 897 */               attributes = parseAttribute(attributes);
/*  626:     */             }
/*  627:     */             else
/*  628:     */             {
/*  629: 898 */               if ("@end".equalsIgnoreCase(this.m_Tokenizer.sval))
/*  630:     */               {
/*  631: 900 */                 getNextToken();
/*  632: 901 */                 if (attributeName.equalsIgnoreCase(this.m_Tokenizer.sval)) {
/*  633:     */                   break;
/*  634:     */                 }
/*  635: 902 */                 errorMessage("declaration of subrelation " + attributeName + " must be terminated by " + "@end " + attributeName); break;
/*  636:     */               }
/*  637: 907 */               errorMessage("declaration of subrelation " + attributeName + " must be terminated by " + "@end " + attributeName);
/*  638:     */             }
/*  639:     */           }
/*  640: 913 */           Instances relation = new Instances(attributeName, attributes, 0);
/*  641: 914 */           attributes = atts;
/*  642: 915 */           attributes.add(new Attribute(attributeName, relation, attributes.size()));
/*  643:     */         }
/*  644:     */         else
/*  645:     */         {
/*  646: 918 */           errorMessage("no valid attribute type or invalid enumeration");
/*  647:     */         }
/*  648:     */       }
/*  649:     */       else
/*  650:     */       {
/*  651: 923 */         ArrayList<String> attributeValues = new ArrayList();
/*  652: 924 */         this.m_Tokenizer.pushBack();
/*  653: 927 */         if (this.m_Tokenizer.nextToken() != 123) {
/*  654: 928 */           errorMessage("{ expected at beginning of enumeration");
/*  655:     */         }
/*  656: 930 */         while (this.m_Tokenizer.nextToken() != 125) {
/*  657: 931 */           if (this.m_Tokenizer.ttype == 10) {
/*  658: 932 */             errorMessage("} expected at end of enumeration");
/*  659:     */           } else {
/*  660: 934 */             attributeValues.add(this.m_Tokenizer.sval);
/*  661:     */           }
/*  662:     */         }
/*  663: 937 */         attributes.add(new Attribute(attributeName, attributeValues, attributes.size()));
/*  664:     */       }
/*  665: 940 */       getLastToken(false);
/*  666: 941 */       getFirstToken();
/*  667: 942 */       if (this.m_Tokenizer.ttype == -1) {
/*  668: 943 */         errorMessage("premature end of file");
/*  669:     */       }
/*  670: 946 */       return attributes;
/*  671:     */     }
/*  672:     */     
/*  673:     */     protected void readTillEOL()
/*  674:     */       throws IOException
/*  675:     */     {
/*  676: 955 */       while (this.m_Tokenizer.nextToken() != 10) {}
/*  677: 958 */       this.m_Tokenizer.pushBack();
/*  678:     */     }
/*  679:     */     
/*  680:     */     public Instances getStructure()
/*  681:     */     {
/*  682: 967 */       return new Instances(this.m_Data, 0);
/*  683:     */     }
/*  684:     */     
/*  685:     */     public Instances getData()
/*  686:     */     {
/*  687: 976 */       return this.m_Data;
/*  688:     */     }
/*  689:     */     
/*  690:     */     public void setRetainStringValues(boolean retain)
/*  691:     */     {
/*  692: 987 */       this.m_retainStringValues = retain;
/*  693:     */     }
/*  694:     */     
/*  695:     */     public boolean getRetainStringValues()
/*  696:     */     {
/*  697: 998 */       return this.m_retainStringValues;
/*  698:     */     }
/*  699:     */     
/*  700:     */     public String getRevision()
/*  701:     */     {
/*  702:1008 */       return RevisionUtils.extract("$Revision: 11506 $");
/*  703:     */     }
/*  704:     */   }
/*  705:     */   
/*  706:     */   public String globalInfo()
/*  707:     */   {
/*  708:1019 */     return "Reads a source that is in arff (attribute relation file format) format. ";
/*  709:     */   }
/*  710:     */   
/*  711:     */   public String retainStringValsTipText()
/*  712:     */   {
/*  713:1029 */     return "If true then the values of string attributes are retained in memory when reading incrementally. Leave this set to false when using incremental classifiers in the Knowledge Flow.";
/*  714:     */   }
/*  715:     */   
/*  716:     */   public void setRetainStringVals(boolean retain)
/*  717:     */   {
/*  718:1043 */     this.m_retainStringVals = retain;
/*  719:     */   }
/*  720:     */   
/*  721:     */   public boolean getRetainStringVals()
/*  722:     */   {
/*  723:1054 */     return this.m_retainStringVals;
/*  724:     */   }
/*  725:     */   
/*  726:     */   public String getFileExtension()
/*  727:     */   {
/*  728:1064 */     return FILE_EXTENSION;
/*  729:     */   }
/*  730:     */   
/*  731:     */   public String[] getFileExtensions()
/*  732:     */   {
/*  733:1074 */     return new String[] { FILE_EXTENSION, FILE_EXTENSION_COMPRESSED };
/*  734:     */   }
/*  735:     */   
/*  736:     */   public String getFileDescription()
/*  737:     */   {
/*  738:1084 */     return "Arff data files";
/*  739:     */   }
/*  740:     */   
/*  741:     */   public void reset()
/*  742:     */     throws IOException
/*  743:     */   {
/*  744:1094 */     this.m_structure = null;
/*  745:1095 */     this.m_ArffReader = null;
/*  746:1096 */     setRetrieval(0);
/*  747:1098 */     if ((this.m_File != null) && (!new File(this.m_File).isDirectory())) {
/*  748:1099 */       setFile(new File(this.m_File));
/*  749:1100 */     } else if ((this.m_URL != null) && (!this.m_URL.equals("http://"))) {
/*  750:1101 */       setURL(this.m_URL);
/*  751:     */     }
/*  752:     */   }
/*  753:     */   
/*  754:     */   public void setSource(URL url)
/*  755:     */     throws IOException
/*  756:     */   {
/*  757:1113 */     this.m_structure = null;
/*  758:1114 */     setRetrieval(0);
/*  759:     */     
/*  760:1116 */     setSource(url.openStream());
/*  761:     */     
/*  762:1118 */     this.m_URL = url.toString();
/*  763:     */     
/*  764:     */ 
/*  765:1121 */     this.m_File = null;
/*  766:     */   }
/*  767:     */   
/*  768:     */   public File retrieveFile()
/*  769:     */   {
/*  770:1131 */     return new File(this.m_File);
/*  771:     */   }
/*  772:     */   
/*  773:     */   public void setFile(File file)
/*  774:     */     throws IOException
/*  775:     */   {
/*  776:1142 */     this.m_File = file.getPath();
/*  777:1143 */     setSource(file);
/*  778:     */   }
/*  779:     */   
/*  780:     */   public void setURL(String url)
/*  781:     */     throws IOException
/*  782:     */   {
/*  783:1154 */     this.m_URL = url;
/*  784:1155 */     setSource(new URL(url));
/*  785:     */   }
/*  786:     */   
/*  787:     */   public String retrieveURL()
/*  788:     */   {
/*  789:1165 */     return this.m_URL;
/*  790:     */   }
/*  791:     */   
/*  792:     */   public void setSource(InputStream in)
/*  793:     */     throws IOException
/*  794:     */   {
/*  795:1177 */     this.m_File = new File(System.getProperty("user.dir")).getAbsolutePath();
/*  796:1178 */     this.m_URL = "http://";
/*  797:     */     
/*  798:1180 */     this.m_sourceReader = new BufferedReader(new InputStreamReader(in));
/*  799:     */   }
/*  800:     */   
/*  801:     */   public Instances getStructure()
/*  802:     */     throws IOException
/*  803:     */   {
/*  804:1193 */     if (this.m_structure == null)
/*  805:     */     {
/*  806:1194 */       if (this.m_sourceReader == null) {
/*  807:1195 */         throw new IOException("No source has been specified");
/*  808:     */       }
/*  809:     */       try
/*  810:     */       {
/*  811:1199 */         this.m_ArffReader = new ArffReader(this.m_sourceReader, 1, getRetrieval() == 1);
/*  812:     */         
/*  813:1201 */         this.m_ArffReader.setRetainStringValues(getRetainStringVals());
/*  814:1202 */         this.m_structure = this.m_ArffReader.getStructure();
/*  815:     */       }
/*  816:     */       catch (Exception ex)
/*  817:     */       {
/*  818:1204 */         throw new IOException("Unable to determine structure as arff (Reason: " + ex.toString() + ").");
/*  819:     */       }
/*  820:     */     }
/*  821:1209 */     return new Instances(this.m_structure, 0);
/*  822:     */   }
/*  823:     */   
/*  824:     */   public Instances getDataSet()
/*  825:     */     throws IOException
/*  826:     */   {
/*  827:1223 */     Instances insts = null;
/*  828:     */     try
/*  829:     */     {
/*  830:1225 */       if (this.m_sourceReader == null) {
/*  831:1226 */         throw new IOException("No source has been specified");
/*  832:     */       }
/*  833:1228 */       if (getRetrieval() == 2) {
/*  834:1229 */         throw new IOException("Cannot mix getting Instances in both incremental and batch modes");
/*  835:     */       }
/*  836:1232 */       setRetrieval(1);
/*  837:1233 */       if (this.m_structure == null) {
/*  838:1234 */         getStructure();
/*  839:     */       }
/*  840:1238 */       insts = new Instances(this.m_structure, 0);
/*  841:     */       Instance inst;
/*  842:1240 */       while ((inst = this.m_ArffReader.readInstance(this.m_structure)) != null) {
/*  843:1241 */         insts.add(inst);
/*  844:     */       }
/*  845:     */     }
/*  846:     */     finally
/*  847:     */     {
/*  848:1247 */       if (this.m_sourceReader != null) {
/*  849:1249 */         this.m_sourceReader.close();
/*  850:     */       }
/*  851:     */     }
/*  852:1253 */     return insts;
/*  853:     */   }
/*  854:     */   
/*  855:     */   public Instance getNextInstance(Instances structure)
/*  856:     */     throws IOException
/*  857:     */   {
/*  858:1271 */     this.m_structure = structure;
/*  859:1273 */     if (getRetrieval() == 1) {
/*  860:1274 */       throw new IOException("Cannot mix getting Instances in both incremental and batch modes");
/*  861:     */     }
/*  862:1277 */     setRetrieval(2);
/*  863:     */     
/*  864:1279 */     Instance current = null;
/*  865:1280 */     if (this.m_sourceReader != null) {
/*  866:1281 */       current = this.m_ArffReader.readInstance(this.m_structure);
/*  867:     */     }
/*  868:1284 */     if ((this.m_sourceReader != null) && (current == null)) {
/*  869:     */       try
/*  870:     */       {
/*  871:1287 */         this.m_sourceReader.close();
/*  872:1288 */         this.m_sourceReader = null;
/*  873:     */       }
/*  874:     */       catch (Exception ex)
/*  875:     */       {
/*  876:1291 */         ex.printStackTrace();
/*  877:     */       }
/*  878:     */     }
/*  879:1294 */     return current;
/*  880:     */   }
/*  881:     */   
/*  882:     */   public String getRevision()
/*  883:     */   {
/*  884:1304 */     return RevisionUtils.extract("$Revision: 11506 $");
/*  885:     */   }
/*  886:     */   
/*  887:     */   public static void main(String[] args)
/*  888:     */   {
/*  889:1313 */     runFileLoader(new ArffLoader(), args);
/*  890:     */   }
/*  891:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.ArffLoader
 * JD-Core Version:    0.7.0.1
 */