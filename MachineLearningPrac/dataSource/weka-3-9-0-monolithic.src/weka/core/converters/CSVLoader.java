/*    1:     */ package weka.core.converters;
/*    2:     */ 
/*    3:     */ import java.io.BufferedReader;
/*    4:     */ import java.io.BufferedWriter;
/*    5:     */ import java.io.File;
/*    6:     */ import java.io.FileReader;
/*    7:     */ import java.io.FileWriter;
/*    8:     */ import java.io.IOException;
/*    9:     */ import java.io.InputStream;
/*   10:     */ import java.io.InputStreamReader;
/*   11:     */ import java.io.PrintStream;
/*   12:     */ import java.io.PrintWriter;
/*   13:     */ import java.io.Reader;
/*   14:     */ import java.io.StreamTokenizer;
/*   15:     */ import java.io.StringReader;
/*   16:     */ import java.io.Writer;
/*   17:     */ import java.text.ParseException;
/*   18:     */ import java.text.SimpleDateFormat;
/*   19:     */ import java.util.ArrayList;
/*   20:     */ import java.util.Date;
/*   21:     */ import java.util.Enumeration;
/*   22:     */ import java.util.HashMap;
/*   23:     */ import java.util.LinkedHashSet;
/*   24:     */ import java.util.List;
/*   25:     */ import java.util.Map;
/*   26:     */ import java.util.Vector;
/*   27:     */ import weka.core.Attribute;
/*   28:     */ import weka.core.DenseInstance;
/*   29:     */ import weka.core.Instance;
/*   30:     */ import weka.core.Instances;
/*   31:     */ import weka.core.Option;
/*   32:     */ import weka.core.OptionHandler;
/*   33:     */ import weka.core.Range;
/*   34:     */ import weka.core.Utils;
/*   35:     */ 
/*   36:     */ public class CSVLoader
/*   37:     */   extends AbstractFileLoader
/*   38:     */   implements BatchConverter, IncrementalConverter, OptionHandler
/*   39:     */ {
/*   40:     */   private static final long serialVersionUID = -1300595850715808438L;
/*   41: 139 */   public static String FILE_EXTENSION = ".csv";
/*   42:     */   protected transient BufferedReader m_sourceReader;
/*   43:     */   protected transient StreamTokenizer m_st;
/*   44:     */   protected transient File m_tempFile;
/*   45:     */   protected transient PrintWriter m_dataDumper;
/*   46: 151 */   protected String m_FieldSeparator = ",";
/*   47: 154 */   protected String m_MissingValue = "?";
/*   48: 157 */   protected Range m_NominalAttributes = new Range();
/*   49: 160 */   protected List<String> m_nominalLabelSpecs = new ArrayList();
/*   50: 163 */   protected Range m_StringAttributes = new Range();
/*   51: 166 */   protected Range m_dateAttributes = new Range();
/*   52: 169 */   protected Range m_numericAttributes = new Range();
/*   53: 172 */   protected String m_dateFormat = "yyyy-MM-dd'T'HH:mm:ss";
/*   54:     */   protected SimpleDateFormat m_formatter;
/*   55: 178 */   protected boolean m_noHeaderRow = false;
/*   56: 181 */   protected String m_Enclosures = "\",'";
/*   57:     */   protected List<String> m_rowBuffer;
/*   58: 187 */   protected int m_bufferSize = 100;
/*   59:     */   protected Map<Integer, LinkedHashSet<String>> m_nominalVals;
/*   60:     */   protected ArffLoader.ArffReader m_incrementalReader;
/*   61:     */   protected transient int m_rowCount;
/*   62:     */   protected String[] m_fieldSeparatorAndEnclosures;
/*   63:     */   protected ArrayList<Object> m_current;
/*   64:     */   protected TYPE[] m_types;
/*   65:     */   private int m_numBufferedRows;
/*   66:     */   
/*   67:     */   public CSVLoader()
/*   68:     */   {
/*   69: 211 */     setRetrieval(0);
/*   70:     */   }
/*   71:     */   
/*   72:     */   public static void main(String[] args)
/*   73:     */   {
/*   74: 220 */     runFileLoader(new CSVLoader(), args);
/*   75:     */   }
/*   76:     */   
/*   77:     */   public String globalInfo()
/*   78:     */   {
/*   79: 230 */     return "Reads a source that is in comma separated format (the default). One can also change the column separator from comma to tab or another character, specify string enclosures, specify whether aheader row is present or not and specify which attributes are to beforced to be nominal or date. Can operate in batch or incremental mode. In batch mode, a buffer is used to process a fixed number of rows in memory at any one time and the data is dumped to a temporary file. This allows the legal values for nominal attributes to be automatically determined. The final ARFF file is produced in a second pass over the temporary file using the structure determined on the first pass. In incremental mode, the first buffer full of rows is used to determine the structure automatically. Following this all rows are read and output incrementally. An error will occur if a row containing nominal values not seen in the initial buffer is encountered. In this case, the size of the initial buffer can be increased, or the user can explicitly provide the legal values of all nominal attributes using the -L (setNominalLabelSpecs) option.";
/*   80:     */   }
/*   81:     */   
/*   82:     */   public String getFileExtension()
/*   83:     */   {
/*   84: 251 */     return FILE_EXTENSION;
/*   85:     */   }
/*   86:     */   
/*   87:     */   public String[] getFileExtensions()
/*   88:     */   {
/*   89: 256 */     return new String[] { getFileExtension() };
/*   90:     */   }
/*   91:     */   
/*   92:     */   public String getFileDescription()
/*   93:     */   {
/*   94: 261 */     return "CSV data files";
/*   95:     */   }
/*   96:     */   
/*   97:     */   public String getRevision()
/*   98:     */   {
/*   99: 266 */     return "$Revision: 11831 $";
/*  100:     */   }
/*  101:     */   
/*  102:     */   public String noHeaderRowPresentTipText()
/*  103:     */   {
/*  104: 276 */     return "First row of data does not contain attribute names";
/*  105:     */   }
/*  106:     */   
/*  107:     */   public boolean getNoHeaderRowPresent()
/*  108:     */   {
/*  109: 285 */     return this.m_noHeaderRow;
/*  110:     */   }
/*  111:     */   
/*  112:     */   public void setNoHeaderRowPresent(boolean b)
/*  113:     */   {
/*  114: 294 */     this.m_noHeaderRow = b;
/*  115:     */   }
/*  116:     */   
/*  117:     */   public String getMissingValue()
/*  118:     */   {
/*  119: 303 */     return this.m_MissingValue;
/*  120:     */   }
/*  121:     */   
/*  122:     */   public void setMissingValue(String value)
/*  123:     */   {
/*  124: 312 */     this.m_MissingValue = value;
/*  125:     */   }
/*  126:     */   
/*  127:     */   public String missingValueTipText()
/*  128:     */   {
/*  129: 322 */     return "The placeholder for missing values, default is '?'.";
/*  130:     */   }
/*  131:     */   
/*  132:     */   public String getStringAttributes()
/*  133:     */   {
/*  134: 331 */     return this.m_StringAttributes.getRanges();
/*  135:     */   }
/*  136:     */   
/*  137:     */   public void setStringAttributes(String value)
/*  138:     */   {
/*  139: 340 */     this.m_StringAttributes.setRanges(value);
/*  140:     */   }
/*  141:     */   
/*  142:     */   public String stringAttributesTipText()
/*  143:     */   {
/*  144: 350 */     return "The range of attributes to force to be of type STRING, example ranges: 'first-last', '1,4,7-14,50-last'.";
/*  145:     */   }
/*  146:     */   
/*  147:     */   public String getNominalAttributes()
/*  148:     */   {
/*  149: 360 */     return this.m_NominalAttributes.getRanges();
/*  150:     */   }
/*  151:     */   
/*  152:     */   public void setNominalAttributes(String value)
/*  153:     */   {
/*  154: 369 */     this.m_NominalAttributes.setRanges(value);
/*  155:     */   }
/*  156:     */   
/*  157:     */   public String nominalAttributesTipText()
/*  158:     */   {
/*  159: 379 */     return "The range of attributes to force to be of type NOMINAL, example ranges: 'first-last', '1,4,7-14,50-last'.";
/*  160:     */   }
/*  161:     */   
/*  162:     */   public String getNumericAttributes()
/*  163:     */   {
/*  164: 389 */     return this.m_numericAttributes.getRanges();
/*  165:     */   }
/*  166:     */   
/*  167:     */   public void setNumericAttributes(String value)
/*  168:     */   {
/*  169: 398 */     this.m_numericAttributes.setRanges(value);
/*  170:     */   }
/*  171:     */   
/*  172:     */   public String numericAttributesTipText()
/*  173:     */   {
/*  174: 408 */     return "The range of attributes to force to be of type NUMERIC, example ranges: 'first-last', '1,4,7-14,50-last'.";
/*  175:     */   }
/*  176:     */   
/*  177:     */   public String getDateFormat()
/*  178:     */   {
/*  179: 419 */     return this.m_dateFormat;
/*  180:     */   }
/*  181:     */   
/*  182:     */   public void setDateFormat(String value)
/*  183:     */   {
/*  184: 428 */     this.m_dateFormat = value;
/*  185: 429 */     this.m_formatter = null;
/*  186:     */   }
/*  187:     */   
/*  188:     */   public String dateFormatTipText()
/*  189:     */   {
/*  190: 439 */     return "The format to use for parsing date values.";
/*  191:     */   }
/*  192:     */   
/*  193:     */   public String getDateAttributes()
/*  194:     */   {
/*  195: 448 */     return this.m_dateAttributes.getRanges();
/*  196:     */   }
/*  197:     */   
/*  198:     */   public void setDateAttributes(String value)
/*  199:     */   {
/*  200: 457 */     this.m_dateAttributes.setRanges(value);
/*  201:     */   }
/*  202:     */   
/*  203:     */   public String dateAttributesTipText()
/*  204:     */   {
/*  205: 467 */     return "The range of attributes to force to type DATE, example ranges: 'first-last', '1,4,7-14, 50-last'.";
/*  206:     */   }
/*  207:     */   
/*  208:     */   public String enclosureCharactersTipText()
/*  209:     */   {
/*  210: 478 */     return "The characters to use as enclosures for strings. E.g. \",'";
/*  211:     */   }
/*  212:     */   
/*  213:     */   public String getEnclosureCharacters()
/*  214:     */   {
/*  215: 487 */     return this.m_Enclosures;
/*  216:     */   }
/*  217:     */   
/*  218:     */   public void setEnclosureCharacters(String enclosure)
/*  219:     */   {
/*  220: 496 */     this.m_Enclosures = enclosure;
/*  221:     */   }
/*  222:     */   
/*  223:     */   public String getFieldSeparator()
/*  224:     */   {
/*  225: 505 */     return Utils.backQuoteChars(this.m_FieldSeparator);
/*  226:     */   }
/*  227:     */   
/*  228:     */   public void setFieldSeparator(String value)
/*  229:     */   {
/*  230: 514 */     this.m_FieldSeparator = Utils.unbackQuoteChars(value);
/*  231: 515 */     if (this.m_FieldSeparator.length() != 1)
/*  232:     */     {
/*  233: 516 */       this.m_FieldSeparator = ",";
/*  234: 517 */       System.err.println("Field separator can only be a single character (exception being '\t'), defaulting back to '" + this.m_FieldSeparator + "'!");
/*  235:     */     }
/*  236:     */   }
/*  237:     */   
/*  238:     */   public String fieldSeparatorTipText()
/*  239:     */   {
/*  240: 530 */     return "The character to use as separator for the columns/fields (use '\\t' for TAB).";
/*  241:     */   }
/*  242:     */   
/*  243:     */   public int getBufferSize()
/*  244:     */   {
/*  245: 540 */     return this.m_bufferSize;
/*  246:     */   }
/*  247:     */   
/*  248:     */   public void setBufferSize(int buff)
/*  249:     */   {
/*  250: 550 */     this.m_bufferSize = buff;
/*  251:     */   }
/*  252:     */   
/*  253:     */   public String bufferSizeTipText()
/*  254:     */   {
/*  255: 560 */     return "The number of rows to process in memory at any one time.";
/*  256:     */   }
/*  257:     */   
/*  258:     */   public Object[] getNominalLabelSpecs()
/*  259:     */   {
/*  260: 569 */     return this.m_nominalLabelSpecs.toArray(new String[0]);
/*  261:     */   }
/*  262:     */   
/*  263:     */   public void setNominalLabelSpecs(Object[] specs)
/*  264:     */   {
/*  265: 578 */     this.m_nominalLabelSpecs.clear();
/*  266: 579 */     for (Object s : specs) {
/*  267: 580 */       this.m_nominalLabelSpecs.add(s.toString());
/*  268:     */     }
/*  269:     */   }
/*  270:     */   
/*  271:     */   public String nominalLabelSpecsTipText()
/*  272:     */   {
/*  273: 591 */     return "Optional specification of legal labels for nominal attributes. May be specified multiple times. Batch mode can determine this automatically (and so can incremental mode if the first in memory buffer load of instances contains an example of each legal value). The spec contains two parts separated by a \":\". The first part can be a range of attribute indexes or a comma-separated list off attruibute names; the second part is a comma-separated list of labels. E.g \"1,2,4-6:red,green,blue\" or \"att1,att2:red,green,blue\"";
/*  274:     */   }
/*  275:     */   
/*  276:     */   public Enumeration<Option> listOptions()
/*  277:     */   {
/*  278: 606 */     Vector<Option> result = new Vector();
/*  279:     */     
/*  280: 608 */     result.add(new Option("\tNo header row present in the data.", "H", 0, "-H"));
/*  281:     */     
/*  282: 610 */     result.add(new Option("\tThe range of attributes to force type to be NOMINAL.\n\t'first' and 'last' are accepted as well.\n\tExamples: \"first-last\", \"1,4,5-27,50-last\"\n\t(default: -none-)", "N", 1, "-N <range>"));
/*  283:     */     
/*  284:     */ 
/*  285:     */ 
/*  286:     */ 
/*  287:     */ 
/*  288: 616 */     result.add(new Option("\tOptional specification of legal labels for nominal\n\tattributes. May be specified multiple times.\n\tBatch mode can determine this\n\tautomatically (and so can incremental mode if\n\tthe first in memory buffer load of instances\n\tcontains an example of each legal value). The\n\tspec contains two parts separated by a \":\". The\n\tfirst part can be a range of attribute indexes or\n\ta comma-separated list off attruibute names; the\n\tsecond part is a comma-separated list of labels. E.g\n\t\"1,2,4-6:red,green,blue\" or \"att1,att2:red,green,blue\"", "L", 1, "-L <nominal label spec>"));
/*  289:     */     
/*  290:     */ 
/*  291:     */ 
/*  292:     */ 
/*  293:     */ 
/*  294:     */ 
/*  295:     */ 
/*  296:     */ 
/*  297:     */ 
/*  298:     */ 
/*  299:     */ 
/*  300:     */ 
/*  301:     */ 
/*  302: 630 */     result.add(new Option("\tThe range of attribute to force type to be STRING.\n\t'first' and 'last' are accepted as well.\n\tExamples: \"first-last\", \"1,4,5-27,50-last\"\n\t(default: -none-)", "S", 1, "-S <range>"));
/*  303:     */     
/*  304:     */ 
/*  305:     */ 
/*  306:     */ 
/*  307:     */ 
/*  308: 636 */     result.add(new Option("\tThe range of attribute to force type to be DATE.\n\t'first' and 'last' are accepted as well.\n\tExamples: \"first-last\", \"1,4,5-27,50-last\"\n\t(default: -none-)", "D", 1, "-D <range>"));
/*  309:     */     
/*  310:     */ 
/*  311:     */ 
/*  312:     */ 
/*  313:     */ 
/*  314: 642 */     result.add(new Option("\tThe date formatting string to use to parse date values.\n\t(default: \"yyyy-MM-dd'T'HH:mm:ss\")", "format", 1, "-format <date format>"));
/*  315:     */     
/*  316:     */ 
/*  317:     */ 
/*  318:     */ 
/*  319: 647 */     result.add(new Option("\tThe range of attribute to force type to be NUMERIC.\n\t'first' and 'last' are accepted as well.\n\tExamples: \"first-last\", \"1,4,5-27,50-last\"\n\t(default: -none-)", "R", 1, "-R <range>"));
/*  320:     */     
/*  321:     */ 
/*  322:     */ 
/*  323:     */ 
/*  324:     */ 
/*  325: 653 */     result.add(new Option("\tThe string representing a missing value.\n\t(default: ?)", "M", 1, "-M <str>"));
/*  326:     */     
/*  327:     */ 
/*  328: 656 */     result.addElement(new Option("\tThe field separator to be used.\n\t'\\t' can be used as well.\n\t(default: ',')", "F", 1, "-F <separator>"));
/*  329:     */     
/*  330:     */ 
/*  331:     */ 
/*  332: 660 */     result.addElement(new Option("\tThe enclosure character(s) to use for strings.\n\tSpecify as a comma separated list (e.g. \",' (default: \",')", "E", 1, "-E <enclosures>"));
/*  333:     */     
/*  334:     */ 
/*  335:     */ 
/*  336:     */ 
/*  337:     */ 
/*  338: 666 */     result.add(new Option("\tThe size of the in memory buffer (in rows).\n\t(default: 100)", "B", 1, "-B <num>"));
/*  339:     */     
/*  340:     */ 
/*  341: 669 */     return result.elements();
/*  342:     */   }
/*  343:     */   
/*  344:     */   public String[] getOptions()
/*  345:     */   {
/*  346: 674 */     Vector<String> result = new Vector();
/*  347: 676 */     if (getNominalAttributes().length() > 0)
/*  348:     */     {
/*  349: 677 */       result.add("-N");
/*  350: 678 */       result.add(getNominalAttributes());
/*  351:     */     }
/*  352: 681 */     if (getStringAttributes().length() > 0)
/*  353:     */     {
/*  354: 682 */       result.add("-S");
/*  355: 683 */       result.add(getStringAttributes());
/*  356:     */     }
/*  357: 686 */     if (getDateAttributes().length() > 0)
/*  358:     */     {
/*  359: 687 */       result.add("-D");
/*  360: 688 */       result.add(getDateAttributes());
/*  361: 689 */       result.add("-format");
/*  362: 690 */       result.add(getDateFormat());
/*  363:     */     }
/*  364: 693 */     if (getNumericAttributes().length() > 0)
/*  365:     */     {
/*  366: 694 */       result.add("-R");
/*  367: 695 */       result.add(getNumericAttributes());
/*  368:     */     }
/*  369: 698 */     result.add("-M");
/*  370: 699 */     result.add(getMissingValue());
/*  371:     */     
/*  372: 701 */     result.add("-B");
/*  373: 702 */     result.add("" + getBufferSize());
/*  374:     */     
/*  375: 704 */     result.add("-E");
/*  376: 705 */     result.add(getEnclosureCharacters());
/*  377:     */     
/*  378: 707 */     result.add("-F");
/*  379: 708 */     result.add(getFieldSeparator());
/*  380: 710 */     for (String spec : this.m_nominalLabelSpecs)
/*  381:     */     {
/*  382: 711 */       result.add("-L");
/*  383: 712 */       result.add(spec);
/*  384:     */     }
/*  385: 715 */     return (String[])result.toArray(new String[result.size()]);
/*  386:     */   }
/*  387:     */   
/*  388:     */   public void setOptions(String[] options)
/*  389:     */     throws Exception
/*  390:     */   {
/*  391: 722 */     setNoHeaderRowPresent(Utils.getFlag('H', options));
/*  392:     */     
/*  393: 724 */     String tmpStr = Utils.getOption('N', options);
/*  394: 725 */     if (tmpStr.length() != 0) {
/*  395: 726 */       setNominalAttributes(tmpStr);
/*  396:     */     } else {
/*  397: 728 */       setNominalAttributes("");
/*  398:     */     }
/*  399: 731 */     tmpStr = Utils.getOption('S', options);
/*  400: 732 */     if (tmpStr.length() != 0) {
/*  401: 733 */       setStringAttributes(tmpStr);
/*  402:     */     } else {
/*  403: 735 */       setStringAttributes("");
/*  404:     */     }
/*  405: 738 */     tmpStr = Utils.getOption('D', options);
/*  406: 739 */     if (tmpStr.length() > 0) {
/*  407: 740 */       setDateAttributes(tmpStr);
/*  408:     */     }
/*  409: 742 */     tmpStr = Utils.getOption("format", options);
/*  410: 743 */     if (tmpStr.length() > 0) {
/*  411: 744 */       setDateFormat(tmpStr);
/*  412:     */     }
/*  413: 747 */     tmpStr = Utils.getOption('R', options);
/*  414: 748 */     if (tmpStr.length() > 0) {
/*  415: 749 */       setNumericAttributes(tmpStr);
/*  416:     */     }
/*  417: 752 */     tmpStr = Utils.getOption('M', options);
/*  418: 753 */     if (tmpStr.length() != 0) {
/*  419: 754 */       setMissingValue(tmpStr);
/*  420:     */     } else {
/*  421: 756 */       setMissingValue("?");
/*  422:     */     }
/*  423: 759 */     tmpStr = Utils.getOption('F', options);
/*  424: 760 */     if (tmpStr.length() != 0) {
/*  425: 761 */       setFieldSeparator(tmpStr);
/*  426:     */     } else {
/*  427: 763 */       setFieldSeparator(",");
/*  428:     */     }
/*  429: 766 */     tmpStr = Utils.getOption('B', options);
/*  430: 767 */     if (tmpStr.length() > 0)
/*  431:     */     {
/*  432: 768 */       int buff = Integer.parseInt(tmpStr);
/*  433: 769 */       if (buff < 1) {
/*  434: 770 */         throw new Exception("Buffer size must be >= 1");
/*  435:     */       }
/*  436: 772 */       setBufferSize(buff);
/*  437:     */     }
/*  438: 775 */     tmpStr = Utils.getOption("E", options);
/*  439: 776 */     if (tmpStr.length() > 0) {
/*  440: 777 */       setEnclosureCharacters(tmpStr);
/*  441:     */     }
/*  442:     */     for (;;)
/*  443:     */     {
/*  444: 781 */       tmpStr = Utils.getOption('L', options);
/*  445: 782 */       if (tmpStr.length() == 0) {
/*  446:     */         break;
/*  447:     */       }
/*  448: 786 */       this.m_nominalLabelSpecs.add(tmpStr);
/*  449:     */     }
/*  450:     */   }
/*  451:     */   
/*  452:     */   public Instance getNextInstance(Instances structure)
/*  453:     */     throws IOException
/*  454:     */   {
/*  455: 792 */     this.m_structure = structure;
/*  456: 793 */     if (getRetrieval() == 1) {
/*  457: 794 */       throw new IOException("Cannot mix getting instances in both incremental and batch modes");
/*  458:     */     }
/*  459: 797 */     setRetrieval(2);
/*  460: 799 */     if (this.m_dataDumper != null)
/*  461:     */     {
/*  462: 801 */       this.m_dataDumper.close();
/*  463: 802 */       this.m_dataDumper = null;
/*  464:     */     }
/*  465: 805 */     if ((this.m_rowBuffer.size() > 0) && (this.m_incrementalReader == null))
/*  466:     */     {
/*  467: 806 */       StringBuilder tempB = new StringBuilder();
/*  468: 807 */       for (String r : this.m_rowBuffer) {
/*  469: 808 */         tempB.append(r).append("\n");
/*  470:     */       }
/*  471: 810 */       this.m_numBufferedRows = this.m_rowBuffer.size();
/*  472: 811 */       Reader batchReader = new BufferedReader(new StringReader(tempB.toString()));
/*  473:     */       
/*  474:     */ 
/*  475: 814 */       this.m_incrementalReader = new ArffLoader.ArffReader(batchReader, this.m_structure, 0, 0, this.m_fieldSeparatorAndEnclosures);
/*  476:     */       
/*  477:     */ 
/*  478:     */ 
/*  479: 818 */       this.m_rowBuffer.clear();
/*  480:     */     }
/*  481: 821 */     if (this.m_numBufferedRows == 0)
/*  482:     */     {
/*  483: 824 */       this.m_numBufferedRows = -1;
/*  484:     */       
/*  485: 826 */       this.m_st = new StreamTokenizer(this.m_sourceReader);
/*  486: 827 */       initTokenizer(this.m_st);
/*  487: 828 */       this.m_st.ordinaryChar(this.m_FieldSeparator.charAt(0));
/*  488:     */       
/*  489: 830 */       this.m_incrementalReader = null;
/*  490:     */     }
/*  491: 833 */     Instance current = null;
/*  492: 834 */     if (this.m_sourceReader != null)
/*  493:     */     {
/*  494: 835 */       if (this.m_incrementalReader != null) {
/*  495: 836 */         current = this.m_incrementalReader.readInstance(this.m_structure);
/*  496: 838 */       } else if (getInstance(this.m_st) != null) {
/*  497: 839 */         current = makeInstance();
/*  498:     */       }
/*  499: 842 */       if ((current != null) || 
/*  500:     */       
/*  501: 844 */         (this.m_numBufferedRows > 0)) {
/*  502: 845 */         this.m_numBufferedRows -= 1;
/*  503:     */       }
/*  504:     */     }
/*  505: 849 */     if ((this.m_sourceReader != null) && (current == null)) {
/*  506:     */       try
/*  507:     */       {
/*  508: 852 */         this.m_sourceReader.close();
/*  509: 853 */         this.m_sourceReader = null;
/*  510:     */       }
/*  511:     */       catch (Exception ex)
/*  512:     */       {
/*  513: 856 */         ex.printStackTrace();
/*  514:     */       }
/*  515:     */     }
/*  516: 860 */     return current;
/*  517:     */   }
/*  518:     */   
/*  519:     */   public Instances getDataSet()
/*  520:     */     throws IOException
/*  521:     */   {
/*  522: 866 */     if (this.m_sourceReader == null) {
/*  523: 867 */       throw new IOException("No source has been specified");
/*  524:     */     }
/*  525: 870 */     if (getRetrieval() == 2) {
/*  526: 871 */       throw new IOException("Cannot mix getting instances in both incremental and batch modes");
/*  527:     */     }
/*  528: 874 */     setRetrieval(1);
/*  529: 876 */     if (this.m_structure == null) {
/*  530: 877 */       getStructure();
/*  531:     */     }
/*  532: 880 */     while (readData(true)) {}
/*  533: 884 */     this.m_dataDumper.flush();
/*  534: 885 */     this.m_dataDumper.close();
/*  535:     */     
/*  536:     */ 
/*  537: 888 */     makeStructure();
/*  538:     */     
/*  539: 890 */     Reader sr = new BufferedReader(new FileReader(this.m_tempFile));
/*  540: 891 */     ArffLoader.ArffReader initialArff = new ArffLoader.ArffReader(sr, this.m_structure, 0, this.m_fieldSeparatorAndEnclosures);
/*  541:     */     
/*  542:     */ 
/*  543: 894 */     Instances initialInsts = initialArff.getData();
/*  544: 895 */     sr.close();
/*  545: 896 */     initialArff = null;
/*  546:     */     
/*  547: 898 */     return initialInsts;
/*  548:     */   }
/*  549:     */   
/*  550:     */   private boolean readData(boolean dump)
/*  551:     */     throws IOException
/*  552:     */   {
/*  553: 902 */     if (this.m_sourceReader == null) {
/*  554: 903 */       throw new IOException("No source has been specified");
/*  555:     */     }
/*  556: 906 */     boolean finished = false;
/*  557:     */     do
/*  558:     */     {
/*  559: 908 */       String checked = getInstance(this.m_st);
/*  560: 909 */       if (checked == null) {
/*  561: 910 */         return false;
/*  562:     */       }
/*  563: 913 */       if (dump) {
/*  564: 914 */         dumpRow(checked);
/*  565:     */       }
/*  566: 916 */       this.m_rowBuffer.add(checked);
/*  567: 918 */       if (this.m_rowBuffer.size() == this.m_bufferSize)
/*  568:     */       {
/*  569: 919 */         finished = true;
/*  570: 921 */         if (getRetrieval() == 1) {
/*  571: 922 */           this.m_rowBuffer.clear();
/*  572:     */         }
/*  573:     */       }
/*  574: 925 */     } while (!finished);
/*  575: 927 */     return true;
/*  576:     */   }
/*  577:     */   
/*  578:     */   public void setSource(InputStream input)
/*  579:     */     throws IOException
/*  580:     */   {
/*  581: 939 */     this.m_structure = null;
/*  582: 940 */     this.m_sourceFile = null;
/*  583: 941 */     this.m_File = null;
/*  584:     */     
/*  585: 943 */     this.m_sourceReader = new BufferedReader(new InputStreamReader(input));
/*  586:     */   }
/*  587:     */   
/*  588:     */   public void setSource(File file)
/*  589:     */     throws IOException
/*  590:     */   {
/*  591: 955 */     super.setSource(file);
/*  592:     */   }
/*  593:     */   
/*  594:     */   public Instances getStructure()
/*  595:     */     throws IOException
/*  596:     */   {
/*  597: 961 */     if (this.m_sourceReader == null) {
/*  598: 962 */       throw new IOException("No source has been specified");
/*  599:     */     }
/*  600: 965 */     this.m_fieldSeparatorAndEnclosures = separatorAndEnclosuresToArray();
/*  601: 967 */     if (this.m_structure == null) {
/*  602: 968 */       readHeader();
/*  603:     */     }
/*  604: 971 */     return this.m_structure;
/*  605:     */   }
/*  606:     */   
/*  607:     */   protected Instance makeInstance()
/*  608:     */     throws IOException
/*  609:     */   {
/*  610: 976 */     if (this.m_current == null) {
/*  611: 977 */       return null;
/*  612:     */     }
/*  613: 980 */     double[] vals = new double[this.m_structure.numAttributes()];
/*  614: 981 */     for (int i = 0; i < this.m_structure.numAttributes(); i++)
/*  615:     */     {
/*  616: 982 */       Object val = this.m_current.get(i);
/*  617: 983 */       if (val.toString().equals("?"))
/*  618:     */       {
/*  619: 984 */         vals[i] = Utils.missingValue();
/*  620:     */       }
/*  621: 985 */       else if (this.m_structure.attribute(i).isString())
/*  622:     */       {
/*  623: 986 */         vals[i] = 0.0D;
/*  624: 987 */         this.m_structure.attribute(i).setStringValue(Utils.unquote(val.toString()));
/*  625:     */       }
/*  626: 988 */       else if (this.m_structure.attribute(i).isDate())
/*  627:     */       {
/*  628: 989 */         String format = this.m_structure.attribute(i).getDateFormat();
/*  629: 990 */         SimpleDateFormat sdf = new SimpleDateFormat(format);
/*  630: 991 */         String dateVal = Utils.unquote(val.toString());
/*  631:     */         try
/*  632:     */         {
/*  633: 993 */           vals[i] = sdf.parse(dateVal).getTime();
/*  634:     */         }
/*  635:     */         catch (ParseException e)
/*  636:     */         {
/*  637: 995 */           throw new IOException("Unable to parse date value " + dateVal + " using date format " + format + " for date attribute " + this.m_structure.attribute(i) + " (line: " + this.m_rowCount + ")");
/*  638:     */         }
/*  639:     */       }
/*  640: 999 */       else if (this.m_structure.attribute(i).isNumeric())
/*  641:     */       {
/*  642:     */         try
/*  643:     */         {
/*  644:1001 */           Double v = Double.valueOf(Double.parseDouble(val.toString()));
/*  645:1002 */           vals[i] = v.doubleValue();
/*  646:     */         }
/*  647:     */         catch (NumberFormatException ex)
/*  648:     */         {
/*  649:1004 */           throw new IOException("Was expecting a number for attribute " + this.m_structure.attribute(i).name() + " but read " + val.toString() + " instead. (line: " + this.m_rowCount + ")");
/*  650:     */         }
/*  651:     */       }
/*  652:     */       else
/*  653:     */       {
/*  654:1010 */         double index = this.m_structure.attribute(i).indexOfValue(Utils.unquote(val.toString()));
/*  655:1012 */         if (index < 0.0D) {
/*  656:1013 */           throw new IOException("Read unknown nominal value " + val.toString() + "for attribute " + this.m_structure.attribute(i).name() + " (line: " + this.m_rowCount + "). Try increasing the size of the memory buffer" + " (-B option) or explicitly specify legal nominal values with " + "the -L option.");
/*  657:     */         }
/*  658:1019 */         vals[i] = index;
/*  659:     */       }
/*  660:     */     }
/*  661:1023 */     DenseInstance inst = new DenseInstance(1.0D, vals);
/*  662:1024 */     inst.setDataset(this.m_structure);
/*  663:     */     
/*  664:1026 */     return inst;
/*  665:     */   }
/*  666:     */   
/*  667:     */   protected void makeStructure()
/*  668:     */   {
/*  669:1031 */     ArrayList<Attribute> attribs = new ArrayList();
/*  670:1032 */     for (int i = 0; i < this.m_types.length; i++) {
/*  671:1033 */       if ((this.m_types[i] == TYPE.STRING) || (this.m_types[i] == TYPE.UNDETERMINED))
/*  672:     */       {
/*  673:1034 */         attribs.add(new Attribute(this.m_structure.attribute(i).name(), (List)null));
/*  674:     */       }
/*  675:1036 */       else if (this.m_types[i] == TYPE.NUMERIC)
/*  676:     */       {
/*  677:1037 */         attribs.add(new Attribute(this.m_structure.attribute(i).name()));
/*  678:     */       }
/*  679:1038 */       else if (this.m_types[i] == TYPE.NOMINAL)
/*  680:     */       {
/*  681:1039 */         LinkedHashSet<String> vals = (LinkedHashSet)this.m_nominalVals.get(Integer.valueOf(i));
/*  682:1040 */         ArrayList<String> theVals = new ArrayList();
/*  683:1041 */         if (vals.size() > 0) {
/*  684:1042 */           for (String v : vals) {
/*  685:1047 */             theVals.add(v);
/*  686:     */           }
/*  687:     */         } else {
/*  688:1050 */           theVals.add("*unknown*");
/*  689:     */         }
/*  690:1052 */         attribs.add(new Attribute(this.m_structure.attribute(i).name(), theVals));
/*  691:     */       }
/*  692:     */       else
/*  693:     */       {
/*  694:1054 */         attribs.add(new Attribute(this.m_structure.attribute(i).name(), this.m_dateFormat));
/*  695:     */       }
/*  696:     */     }
/*  697:1058 */     this.m_structure = new Instances(this.m_structure.relationName(), attribs, 0);
/*  698:     */   }
/*  699:     */   
/*  700:     */   private void readHeader()
/*  701:     */     throws IOException
/*  702:     */   {
/*  703:1062 */     this.m_rowCount = 1;
/*  704:1063 */     this.m_incrementalReader = null;
/*  705:1064 */     this.m_current = new ArrayList();
/*  706:1065 */     openTempFiles();
/*  707:     */     
/*  708:1067 */     this.m_rowBuffer = new ArrayList();
/*  709:     */     
/*  710:1069 */     String firstRow = this.m_sourceReader.readLine();
/*  711:1070 */     if (firstRow == null) {
/*  712:1071 */       throw new IOException("No data in the file!");
/*  713:     */     }
/*  714:1073 */     if (this.m_noHeaderRow) {
/*  715:1074 */       this.m_rowBuffer.add(firstRow);
/*  716:     */     }
/*  717:1077 */     ArrayList<Attribute> attribNames = new ArrayList();
/*  718:     */     
/*  719:     */ 
/*  720:     */ 
/*  721:1081 */     StringReader sr = new StringReader(firstRow + "\n");
/*  722:     */     
/*  723:1083 */     this.m_st = new StreamTokenizer(sr);
/*  724:1084 */     initTokenizer(this.m_st);
/*  725:     */     
/*  726:1086 */     this.m_st.ordinaryChar(this.m_FieldSeparator.charAt(0));
/*  727:     */     
/*  728:1088 */     int attNum = 1;
/*  729:1089 */     StreamTokenizerUtils.getFirstToken(this.m_st);
/*  730:1090 */     if (this.m_st.ttype == -1) {
/*  731:1091 */       StreamTokenizerUtils.errms(this.m_st, "premature end of file");
/*  732:     */     }
/*  733:1093 */     boolean first = true;
/*  734:1097 */     while ((this.m_st.ttype != 10) && (this.m_st.ttype != -1))
/*  735:     */     {
/*  736:1100 */       if (!first) {
/*  737:1101 */         StreamTokenizerUtils.getToken(this.m_st);
/*  738:     */       }
/*  739:     */       boolean wasSep;
/*  740:     */       boolean wasSep;
/*  741:1104 */       if ((this.m_st.ttype == this.m_FieldSeparator.charAt(0)) || (this.m_st.ttype == 10))
/*  742:     */       {
/*  743:1106 */         wasSep = true;
/*  744:     */       }
/*  745:     */       else
/*  746:     */       {
/*  747:1108 */         wasSep = false;
/*  748:     */         
/*  749:1110 */         String attName = null;
/*  750:1112 */         if (this.m_noHeaderRow)
/*  751:     */         {
/*  752:1113 */           attName = "att" + attNum;
/*  753:1114 */           attNum++;
/*  754:     */         }
/*  755:     */         else
/*  756:     */         {
/*  757:1116 */           attName = this.m_st.sval;
/*  758:     */         }
/*  759:1119 */         attribNames.add(new Attribute(attName, (List)null));
/*  760:     */       }
/*  761:1121 */       if (!wasSep) {
/*  762:1122 */         StreamTokenizerUtils.getToken(this.m_st);
/*  763:     */       }
/*  764:1124 */       first = false;
/*  765:     */     }
/*  766:     */     String relationName;
/*  767:     */     String relationName;
/*  768:1127 */     if (this.m_sourceFile != null) {
/*  769:1128 */       relationName = this.m_sourceFile.getName().replaceAll("\\.[cC][sS][vV]$", "");
/*  770:     */     } else {
/*  771:1131 */       relationName = "stream";
/*  772:     */     }
/*  773:1133 */     this.m_structure = new Instances(relationName, attribNames, 0);
/*  774:1134 */     this.m_NominalAttributes.setUpper(this.m_structure.numAttributes() - 1);
/*  775:1135 */     this.m_StringAttributes.setUpper(this.m_structure.numAttributes() - 1);
/*  776:1136 */     this.m_dateAttributes.setUpper(this.m_structure.numAttributes() - 1);
/*  777:1137 */     this.m_numericAttributes.setUpper(this.m_structure.numAttributes() - 1);
/*  778:1138 */     this.m_nominalVals = new HashMap();
/*  779:     */     
/*  780:1140 */     this.m_types = new TYPE[this.m_structure.numAttributes()];
/*  781:1141 */     for (int i = 0; i < this.m_structure.numAttributes(); i++) {
/*  782:1142 */       if (this.m_NominalAttributes.isInRange(i))
/*  783:     */       {
/*  784:1143 */         this.m_types[i] = TYPE.NOMINAL;
/*  785:1144 */         LinkedHashSet<String> ts = new LinkedHashSet();
/*  786:1145 */         this.m_nominalVals.put(Integer.valueOf(i), ts);
/*  787:     */       }
/*  788:1146 */       else if (this.m_StringAttributes.isInRange(i))
/*  789:     */       {
/*  790:1147 */         this.m_types[i] = TYPE.STRING;
/*  791:     */       }
/*  792:1148 */       else if (this.m_dateAttributes.isInRange(i))
/*  793:     */       {
/*  794:1149 */         this.m_types[i] = TYPE.DATE;
/*  795:     */       }
/*  796:1150 */       else if (this.m_numericAttributes.isInRange(i))
/*  797:     */       {
/*  798:1151 */         this.m_types[i] = TYPE.NUMERIC;
/*  799:     */       }
/*  800:     */       else
/*  801:     */       {
/*  802:1153 */         this.m_types[i] = TYPE.UNDETERMINED;
/*  803:     */       }
/*  804:     */     }
/*  805:1157 */     if (this.m_nominalLabelSpecs.size() > 0) {
/*  806:1158 */       for (String spec : this.m_nominalLabelSpecs)
/*  807:     */       {
/*  808:1159 */         String[] attsAndLabels = spec.split(":");
/*  809:1160 */         if (attsAndLabels.length == 2)
/*  810:     */         {
/*  811:1161 */           String[] labels = attsAndLabels[1].split(",");
/*  812:     */           try
/*  813:     */           {
/*  814:1164 */             Range tempR = new Range();
/*  815:1165 */             tempR.setRanges(attsAndLabels[0].trim());
/*  816:1166 */             tempR.setUpper(this.m_structure.numAttributes() - 1);
/*  817:     */             
/*  818:1168 */             int[] rangeIndexes = tempR.getSelection();
/*  819:1169 */             for (int i = 0; i < rangeIndexes.length; i++)
/*  820:     */             {
/*  821:1170 */               this.m_types[rangeIndexes[i]] = TYPE.NOMINAL;
/*  822:1171 */               LinkedHashSet<String> ts = new LinkedHashSet();
/*  823:1172 */               for (String lab : labels) {
/*  824:1173 */                 ts.add(lab);
/*  825:     */               }
/*  826:1175 */               this.m_nominalVals.put(Integer.valueOf(rangeIndexes[i]), ts);
/*  827:     */             }
/*  828:     */           }
/*  829:     */           catch (IllegalArgumentException e)
/*  830:     */           {
/*  831:1179 */             String[] attNames = attsAndLabels[0].split(",");
/*  832:1180 */             for (String attN : attNames)
/*  833:     */             {
/*  834:1181 */               Attribute a = this.m_structure.attribute(attN.trim());
/*  835:1182 */               if (a != null)
/*  836:     */               {
/*  837:1183 */                 int attIndex = a.index();
/*  838:1184 */                 this.m_types[attIndex] = TYPE.NOMINAL;
/*  839:1185 */                 LinkedHashSet<String> ts = new LinkedHashSet();
/*  840:1186 */                 for (String lab : labels) {
/*  841:1187 */                   ts.add(lab);
/*  842:     */                 }
/*  843:1189 */                 this.m_nominalVals.put(Integer.valueOf(attIndex), ts);
/*  844:     */               }
/*  845:     */             }
/*  846:     */           }
/*  847:     */         }
/*  848:     */       }
/*  849:     */     }
/*  850:1200 */     if ((this.m_noHeaderRow) && (getRetrieval() == 1))
/*  851:     */     {
/*  852:1201 */       StreamTokenizer tempT = new StreamTokenizer(new StringReader(firstRow));
/*  853:1202 */       initTokenizer(tempT);
/*  854:1203 */       tempT.ordinaryChar(this.m_FieldSeparator.charAt(0));
/*  855:1204 */       String checked = getInstance(tempT);
/*  856:1205 */       dumpRow(checked);
/*  857:     */     }
/*  858:1208 */     this.m_st = new StreamTokenizer(this.m_sourceReader);
/*  859:1209 */     initTokenizer(this.m_st);
/*  860:1210 */     this.m_st.ordinaryChar(this.m_FieldSeparator.charAt(0));
/*  861:     */     
/*  862:     */ 
/*  863:1213 */     readData(getRetrieval() == 1);
/*  864:1214 */     makeStructure();
/*  865:     */   }
/*  866:     */   
/*  867:     */   protected void openTempFiles()
/*  868:     */     throws IOException
/*  869:     */   {
/*  870:1218 */     String tempPrefix = "" + Math.random() + "arffOut";
/*  871:1219 */     this.m_tempFile = File.createTempFile(tempPrefix, null);
/*  872:1220 */     this.m_tempFile.deleteOnExit();
/*  873:1221 */     Writer os2 = new FileWriter(this.m_tempFile);
/*  874:1222 */     this.m_dataDumper = new PrintWriter(new BufferedWriter(os2));
/*  875:     */   }
/*  876:     */   
/*  877:     */   protected void dumpRow(String row)
/*  878:     */     throws IOException
/*  879:     */   {
/*  880:1226 */     this.m_dataDumper.println(row);
/*  881:     */   }
/*  882:     */   
/*  883:     */   private String[] separatorAndEnclosuresToArray()
/*  884:     */   {
/*  885:1235 */     String[] parts = this.m_Enclosures.split(",");
/*  886:     */     
/*  887:1237 */     String[] result = new String[parts.length + 1];
/*  888:1238 */     result[0] = this.m_FieldSeparator;
/*  889:1239 */     int index = 1;
/*  890:1240 */     for (String e : parts)
/*  891:     */     {
/*  892:1241 */       if ((e.length() > 1) || (e.length() == 0)) {
/*  893:1242 */         throw new IllegalArgumentException("Enclosures can only be single characters");
/*  894:     */       }
/*  895:1245 */       result[(index++)] = e;
/*  896:     */     }
/*  897:1248 */     return result;
/*  898:     */   }
/*  899:     */   
/*  900:     */   private void initTokenizer(StreamTokenizer tokenizer)
/*  901:     */   {
/*  902:1257 */     tokenizer.resetSyntax();
/*  903:1258 */     tokenizer.whitespaceChars(0, 31);
/*  904:1259 */     tokenizer.wordChars(32, 255);
/*  905:1260 */     tokenizer.whitespaceChars(this.m_FieldSeparator.charAt(0), this.m_FieldSeparator.charAt(0));
/*  906:     */     
/*  907:     */ 
/*  908:     */ 
/*  909:1264 */     String[] parts = this.m_Enclosures.split(",");
/*  910:1265 */     for (String e : parts)
/*  911:     */     {
/*  912:1266 */       if ((e.length() > 1) || (e.length() == 0)) {
/*  913:1267 */         throw new IllegalArgumentException("Enclosures can only be single characters");
/*  914:     */       }
/*  915:1270 */       tokenizer.quoteChar(e.charAt(0));
/*  916:     */     }
/*  917:1273 */     tokenizer.eolIsSignificant(true);
/*  918:     */   }
/*  919:     */   
/*  920:     */   private String getInstance(StreamTokenizer tokenizer)
/*  921:     */     throws IOException
/*  922:     */   {
/*  923:     */     try
/*  924:     */     {
/*  925:1301 */       StreamTokenizerUtils.getFirstToken(tokenizer);
/*  926:1302 */       if (tokenizer.ttype == -1) {
/*  927:1303 */         return null;
/*  928:     */       }
/*  929:1306 */       boolean first = true;
/*  930:     */       
/*  931:1308 */       this.m_current.clear();
/*  932:     */       
/*  933:1310 */       int i = 0;
/*  934:1312 */       while ((tokenizer.ttype != 10) && (tokenizer.ttype != -1))
/*  935:     */       {
/*  936:1315 */         if (!first) {
/*  937:1316 */           StreamTokenizerUtils.getToken(tokenizer);
/*  938:     */         }
/*  939:     */         boolean wasSep;
/*  940:     */         boolean wasSep;
/*  941:1319 */         if ((tokenizer.ttype == this.m_FieldSeparator.charAt(0)) || (tokenizer.ttype == 10))
/*  942:     */         {
/*  943:1321 */           this.m_current.add("?");
/*  944:1322 */           wasSep = true;
/*  945:     */         }
/*  946:     */         else
/*  947:     */         {
/*  948:1324 */           wasSep = false;
/*  949:1325 */           if ((tokenizer.sval.equals(this.m_MissingValue)) || (tokenizer.sval.trim().length() == 0))
/*  950:     */           {
/*  951:1327 */             this.m_current.add("?");
/*  952:     */           }
/*  953:1328 */           else if ((this.m_types[i] == TYPE.NUMERIC) || (this.m_types[i] == TYPE.UNDETERMINED))
/*  954:     */           {
/*  955:     */             try
/*  956:     */             {
/*  957:1332 */               Double.parseDouble(tokenizer.sval);
/*  958:1333 */               this.m_current.add(tokenizer.sval);
/*  959:1334 */               this.m_types[i] = TYPE.NUMERIC;
/*  960:     */             }
/*  961:     */             catch (NumberFormatException e)
/*  962:     */             {
/*  963:1337 */               this.m_current.add(Utils.quote(tokenizer.sval));
/*  964:1338 */               if (this.m_types[i] == TYPE.UNDETERMINED)
/*  965:     */               {
/*  966:1339 */                 this.m_types[i] = TYPE.NOMINAL;
/*  967:1340 */                 LinkedHashSet<String> ts = new LinkedHashSet();
/*  968:1341 */                 ts.add(tokenizer.sval);
/*  969:1342 */                 this.m_nominalVals.put(Integer.valueOf(i), ts);
/*  970:     */               }
/*  971:     */               else
/*  972:     */               {
/*  973:1344 */                 this.m_types[i] = TYPE.STRING;
/*  974:     */               }
/*  975:     */             }
/*  976:     */           }
/*  977:1347 */           else if ((this.m_types[i] == TYPE.STRING) || (this.m_types[i] == TYPE.DATE))
/*  978:     */           {
/*  979:1348 */             this.m_current.add(Utils.quote(tokenizer.sval));
/*  980:     */           }
/*  981:1349 */           else if (this.m_types[i] == TYPE.NOMINAL)
/*  982:     */           {
/*  983:1350 */             this.m_current.add(Utils.quote(tokenizer.sval));
/*  984:1351 */             ((LinkedHashSet)this.m_nominalVals.get(Integer.valueOf(i))).add(tokenizer.sval);
/*  985:     */           }
/*  986:     */         }
/*  987:1355 */         if (!wasSep) {
/*  988:1356 */           StreamTokenizerUtils.getToken(tokenizer);
/*  989:     */         }
/*  990:1358 */         first = false;
/*  991:1359 */         i++;
/*  992:     */       }
/*  993:1363 */       if (this.m_current.size() != this.m_structure.numAttributes())
/*  994:     */       {
/*  995:1364 */         for (Object o : this.m_current) {
/*  996:1365 */           System.out.print(o.toString() + "|||");
/*  997:     */         }
/*  998:1367 */         System.out.println();
/*  999:1368 */         StreamTokenizerUtils.errms(tokenizer, "wrong number of values. Read " + this.m_current.size() + ", expected " + this.m_structure.numAttributes());
/* 1000:     */       }
/* 1001:     */     }
/* 1002:     */     catch (Exception ex)
/* 1003:     */     {
/* 1004:1373 */       throw new IOException(ex.getMessage() + " Problem encountered on line: " + (this.m_rowCount + 1));
/* 1005:     */     }
/* 1006:1377 */     StringBuilder temp = new StringBuilder();
/* 1007:1378 */     for (Object o : this.m_current) {
/* 1008:1379 */       temp.append(o.toString()).append(this.m_FieldSeparator);
/* 1009:     */     }
/* 1010:1381 */     this.m_rowCount += 1;
/* 1011:     */     
/* 1012:1383 */     return temp.substring(0, temp.length() - 1);
/* 1013:     */   }
/* 1014:     */   
/* 1015:     */   public void reset()
/* 1016:     */     throws IOException
/* 1017:     */   {
/* 1018:1388 */     this.m_structure = null;
/* 1019:1389 */     this.m_rowBuffer = null;
/* 1020:1391 */     if (this.m_dataDumper != null)
/* 1021:     */     {
/* 1022:1393 */       this.m_dataDumper.close();
/* 1023:1394 */       this.m_dataDumper = null;
/* 1024:     */     }
/* 1025:1396 */     if (this.m_sourceReader != null) {
/* 1026:1397 */       this.m_sourceReader.close();
/* 1027:     */     }
/* 1028:1400 */     if (this.m_File != null) {
/* 1029:1401 */       setFile(new File(this.m_File));
/* 1030:     */     }
/* 1031:     */   }
/* 1032:     */   
/* 1033:     */   static enum TYPE
/* 1034:     */   {
/* 1035:1406 */     UNDETERMINED,  NUMERIC,  NOMINAL,  STRING,  DATE;
/* 1036:     */     
/* 1037:     */     private TYPE() {}
/* 1038:     */   }
/* 1039:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.CSVLoader
 * JD-Core Version:    0.7.0.1
 */