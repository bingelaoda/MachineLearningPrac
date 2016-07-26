/*    1:     */ package weka.filters;
/*    2:     */ 
/*    3:     */ import java.io.FileOutputStream;
/*    4:     */ import java.io.PrintStream;
/*    5:     */ import java.io.PrintWriter;
/*    6:     */ import java.io.Serializable;
/*    7:     */ import java.util.Date;
/*    8:     */ import java.util.Enumeration;
/*    9:     */ import java.util.Iterator;
/*   10:     */ import java.util.Vector;
/*   11:     */ import weka.core.Attribute;
/*   12:     */ import weka.core.Capabilities;
/*   13:     */ import weka.core.Capabilities.Capability;
/*   14:     */ import weka.core.CapabilitiesHandler;
/*   15:     */ import weka.core.CapabilitiesIgnorer;
/*   16:     */ import weka.core.CommandlineRunnable;
/*   17:     */ import weka.core.Instance;
/*   18:     */ import weka.core.Instances;
/*   19:     */ import weka.core.Option;
/*   20:     */ import weka.core.OptionHandler;
/*   21:     */ import weka.core.Queue;
/*   22:     */ import weka.core.RelationalLocator;
/*   23:     */ import weka.core.RevisionHandler;
/*   24:     */ import weka.core.RevisionUtils;
/*   25:     */ import weka.core.SerializedObject;
/*   26:     */ import weka.core.StringLocator;
/*   27:     */ import weka.core.UnsupportedAttributeTypeException;
/*   28:     */ import weka.core.Utils;
/*   29:     */ import weka.core.Version;
/*   30:     */ import weka.core.converters.ConverterUtils.DataSource;
/*   31:     */ 
/*   32:     */ public abstract class Filter
/*   33:     */   implements Serializable, CapabilitiesHandler, RevisionHandler, OptionHandler, CapabilitiesIgnorer, CommandlineRunnable
/*   34:     */ {
/*   35:     */   private static final long serialVersionUID = -8835063755891851218L;
/*   36:  90 */   private Instances m_OutputFormat = null;
/*   37:  93 */   private Queue m_OutputQueue = null;
/*   38:  96 */   protected StringLocator m_OutputStringAtts = null;
/*   39:  99 */   protected StringLocator m_InputStringAtts = null;
/*   40: 102 */   protected RelationalLocator m_OutputRelAtts = null;
/*   41: 105 */   protected RelationalLocator m_InputRelAtts = null;
/*   42: 108 */   private Instances m_InputFormat = null;
/*   43: 111 */   protected boolean m_NewBatch = true;
/*   44: 114 */   protected boolean m_FirstBatchDone = false;
/*   45: 117 */   protected boolean m_Debug = false;
/*   46: 120 */   protected boolean m_DoNotCheckCapabilities = false;
/*   47:     */   
/*   48:     */   public boolean isNewBatch()
/*   49:     */   {
/*   50: 131 */     return this.m_NewBatch;
/*   51:     */   }
/*   52:     */   
/*   53:     */   public boolean isFirstBatchDone()
/*   54:     */   {
/*   55: 144 */     return this.m_FirstBatchDone;
/*   56:     */   }
/*   57:     */   
/*   58:     */   public boolean mayRemoveInstanceAfterFirstBatchDone()
/*   59:     */   {
/*   60: 156 */     return false;
/*   61:     */   }
/*   62:     */   
/*   63:     */   public Capabilities getCapabilities()
/*   64:     */   {
/*   65: 170 */     Capabilities result = new Capabilities(this);
/*   66: 171 */     result.enableAll();
/*   67:     */     
/*   68: 173 */     result.setMinimumNumberInstances(0);
/*   69:     */     
/*   70: 175 */     return result;
/*   71:     */   }
/*   72:     */   
/*   73:     */   public String getRevision()
/*   74:     */   {
/*   75: 185 */     return RevisionUtils.extract("$Revision: 12377 $");
/*   76:     */   }
/*   77:     */   
/*   78:     */   public Capabilities getCapabilities(Instances data)
/*   79:     */   {
/*   80: 204 */     Capabilities result = getCapabilities();
/*   81: 207 */     if (data.classIndex() == -1)
/*   82:     */     {
/*   83: 208 */       Capabilities classes = result.getClassCapabilities();
/*   84: 209 */       Iterator<Capabilities.Capability> iter = classes.capabilities();
/*   85: 210 */       while (iter.hasNext())
/*   86:     */       {
/*   87: 211 */         Capabilities.Capability cap = (Capabilities.Capability)iter.next();
/*   88: 212 */         if (cap != Capabilities.Capability.NO_CLASS)
/*   89:     */         {
/*   90: 213 */           result.disable(cap);
/*   91: 214 */           result.disableDependency(cap);
/*   92:     */         }
/*   93:     */       }
/*   94:     */     }
/*   95: 220 */     result.disable(Capabilities.Capability.NO_CLASS);
/*   96: 221 */     result.disableDependency(Capabilities.Capability.NO_CLASS);
/*   97:     */     
/*   98:     */ 
/*   99: 224 */     return result;
/*  100:     */   }
/*  101:     */   
/*  102:     */   protected void setOutputFormat(Instances outputFormat)
/*  103:     */   {
/*  104: 236 */     if (outputFormat != null)
/*  105:     */     {
/*  106: 237 */       this.m_OutputFormat = outputFormat.stringFreeStructure();
/*  107: 238 */       initOutputLocators(this.m_OutputFormat, null);
/*  108:     */       
/*  109:     */ 
/*  110: 241 */       String relationName = outputFormat.relationName() + "-" + getClass().getName();
/*  111: 243 */       if ((this instanceof OptionHandler))
/*  112:     */       {
/*  113: 244 */         String[] options = getOptions();
/*  114: 245 */         for (String option : options) {
/*  115: 246 */           relationName = relationName + option.trim();
/*  116:     */         }
/*  117:     */       }
/*  118: 249 */       this.m_OutputFormat.setRelationName(relationName);
/*  119:     */     }
/*  120:     */     else
/*  121:     */     {
/*  122: 251 */       this.m_OutputFormat = null;
/*  123:     */     }
/*  124: 253 */     this.m_OutputQueue = new Queue();
/*  125:     */   }
/*  126:     */   
/*  127:     */   protected Instances getInputFormat()
/*  128:     */   {
/*  129: 264 */     return this.m_InputFormat;
/*  130:     */   }
/*  131:     */   
/*  132:     */   protected Instances inputFormatPeek()
/*  133:     */   {
/*  134: 274 */     return this.m_InputFormat;
/*  135:     */   }
/*  136:     */   
/*  137:     */   protected Instances outputFormatPeek()
/*  138:     */   {
/*  139: 284 */     return this.m_OutputFormat;
/*  140:     */   }
/*  141:     */   
/*  142:     */   protected void push(Instance instance)
/*  143:     */   {
/*  144: 297 */     push(instance, true);
/*  145:     */   }
/*  146:     */   
/*  147:     */   protected void push(Instance instance, boolean copyInstance)
/*  148:     */   {
/*  149: 311 */     if (instance != null)
/*  150:     */     {
/*  151: 312 */       if (instance.dataset() != null)
/*  152:     */       {
/*  153: 313 */         if (copyInstance) {
/*  154: 314 */           instance = (Instance)instance.copy();
/*  155:     */         }
/*  156: 316 */         copyValues(instance, false);
/*  157:     */       }
/*  158: 318 */       instance.setDataset(this.m_OutputFormat);
/*  159: 319 */       this.m_OutputQueue.push(instance);
/*  160:     */     }
/*  161:     */   }
/*  162:     */   
/*  163:     */   protected void resetQueue()
/*  164:     */   {
/*  165: 328 */     this.m_OutputQueue = new Queue();
/*  166:     */   }
/*  167:     */   
/*  168:     */   protected void bufferInput(Instance instance)
/*  169:     */   {
/*  170: 340 */     if (instance != null)
/*  171:     */     {
/*  172: 341 */       instance = (Instance)instance.copy();
/*  173: 342 */       copyValues(instance, true);
/*  174: 343 */       this.m_InputFormat.add(instance);
/*  175:     */     }
/*  176:     */   }
/*  177:     */   
/*  178:     */   protected void initInputLocators(Instances data, int[] indices)
/*  179:     */   {
/*  180: 356 */     if (indices == null)
/*  181:     */     {
/*  182: 357 */       this.m_InputStringAtts = new StringLocator(data);
/*  183: 358 */       this.m_InputRelAtts = new RelationalLocator(data);
/*  184:     */     }
/*  185:     */     else
/*  186:     */     {
/*  187: 360 */       this.m_InputStringAtts = new StringLocator(data, indices);
/*  188: 361 */       this.m_InputRelAtts = new RelationalLocator(data, indices);
/*  189:     */     }
/*  190:     */   }
/*  191:     */   
/*  192:     */   protected void initOutputLocators(Instances data, int[] indices)
/*  193:     */   {
/*  194: 374 */     if (indices == null)
/*  195:     */     {
/*  196: 375 */       this.m_OutputStringAtts = new StringLocator(data);
/*  197: 376 */       this.m_OutputRelAtts = new RelationalLocator(data);
/*  198:     */     }
/*  199:     */     else
/*  200:     */     {
/*  201: 378 */       this.m_OutputStringAtts = new StringLocator(data, indices);
/*  202: 379 */       this.m_OutputRelAtts = new RelationalLocator(data, indices);
/*  203:     */     }
/*  204:     */   }
/*  205:     */   
/*  206:     */   protected void copyValues(Instance instance, boolean isInput)
/*  207:     */   {
/*  208: 395 */     RelationalLocator.copyRelationalValues(instance, isInput ? this.m_InputFormat : this.m_OutputFormat, isInput ? this.m_InputRelAtts : this.m_OutputRelAtts);
/*  209:     */     
/*  210:     */ 
/*  211:     */ 
/*  212: 399 */     StringLocator.copyStringValues(instance, isInput ? this.m_InputFormat : this.m_OutputFormat, isInput ? this.m_InputStringAtts : this.m_OutputStringAtts);
/*  213:     */   }
/*  214:     */   
/*  215:     */   protected void copyValues(Instance instance, boolean instSrcCompat, Instances srcDataset, Instances destDataset)
/*  216:     */   {
/*  217: 431 */     RelationalLocator.copyRelationalValues(instance, instSrcCompat, srcDataset, this.m_InputRelAtts, destDataset, this.m_OutputRelAtts);
/*  218:     */     
/*  219:     */ 
/*  220: 434 */     StringLocator.copyStringValues(instance, instSrcCompat, srcDataset, this.m_InputStringAtts, destDataset, this.m_OutputStringAtts);
/*  221:     */   }
/*  222:     */   
/*  223:     */   protected void flushInput()
/*  224:     */   {
/*  225: 444 */     if ((this.m_InputStringAtts.getAttributeIndices().length > 0) || (this.m_InputRelAtts.getAttributeIndices().length > 0))
/*  226:     */     {
/*  227: 446 */       this.m_InputFormat = this.m_InputFormat.stringFreeStructure();
/*  228: 447 */       this.m_InputStringAtts = new StringLocator(this.m_InputFormat, this.m_InputStringAtts.getAllowedIndices());
/*  229:     */       
/*  230: 449 */       this.m_InputRelAtts = new RelationalLocator(this.m_InputFormat, this.m_InputRelAtts.getAllowedIndices());
/*  231:     */     }
/*  232:     */     else
/*  233:     */     {
/*  234: 453 */       this.m_InputFormat.delete();
/*  235:     */     }
/*  236:     */   }
/*  237:     */   
/*  238:     */   protected void testInputFormat(Instances instanceInfo)
/*  239:     */     throws Exception
/*  240:     */   {
/*  241: 464 */     getCapabilities(instanceInfo).testWithFail(instanceInfo);
/*  242:     */   }
/*  243:     */   
/*  244:     */   public boolean setInputFormat(Instances instanceInfo)
/*  245:     */     throws Exception
/*  246:     */   {
/*  247: 482 */     testInputFormat(instanceInfo);
/*  248:     */     
/*  249: 484 */     this.m_InputFormat = instanceInfo.stringFreeStructure();
/*  250: 485 */     this.m_OutputFormat = null;
/*  251: 486 */     this.m_OutputQueue = new Queue();
/*  252: 487 */     this.m_NewBatch = true;
/*  253: 488 */     this.m_FirstBatchDone = false;
/*  254: 489 */     initInputLocators(this.m_InputFormat, null);
/*  255: 490 */     return false;
/*  256:     */   }
/*  257:     */   
/*  258:     */   public Instances getOutputFormat()
/*  259:     */   {
/*  260: 505 */     if (this.m_OutputFormat == null) {
/*  261: 506 */       throw new NullPointerException("No output format defined.");
/*  262:     */     }
/*  263: 508 */     return new Instances(this.m_OutputFormat, 0);
/*  264:     */   }
/*  265:     */   
/*  266:     */   public boolean input(Instance instance)
/*  267:     */     throws Exception
/*  268:     */   {
/*  269: 527 */     if (this.m_InputFormat == null) {
/*  270: 528 */       throw new NullPointerException("No input instance format defined");
/*  271:     */     }
/*  272: 530 */     if (this.m_NewBatch)
/*  273:     */     {
/*  274: 531 */       this.m_OutputQueue = new Queue();
/*  275: 532 */       this.m_NewBatch = false;
/*  276:     */     }
/*  277: 534 */     bufferInput(instance);
/*  278: 535 */     return false;
/*  279:     */   }
/*  280:     */   
/*  281:     */   public boolean batchFinished()
/*  282:     */     throws Exception
/*  283:     */   {
/*  284: 553 */     if (this.m_InputFormat == null) {
/*  285: 554 */       throw new NullPointerException("No input instance format defined");
/*  286:     */     }
/*  287: 556 */     flushInput();
/*  288: 557 */     this.m_NewBatch = true;
/*  289: 558 */     this.m_FirstBatchDone = true;
/*  290: 560 */     if (this.m_OutputQueue.empty()) {
/*  291: 562 */       if ((this.m_OutputStringAtts.getAttributeIndices().length > 0) || (this.m_OutputRelAtts.getAttributeIndices().length > 0))
/*  292:     */       {
/*  293: 564 */         this.m_OutputFormat = this.m_OutputFormat.stringFreeStructure();
/*  294: 565 */         this.m_OutputStringAtts = new StringLocator(this.m_OutputFormat, this.m_OutputStringAtts.getAllowedIndices());
/*  295:     */       }
/*  296:     */     }
/*  297: 570 */     return numPendingOutput() != 0;
/*  298:     */   }
/*  299:     */   
/*  300:     */   public Instance output()
/*  301:     */   {
/*  302: 582 */     if (this.m_OutputFormat == null) {
/*  303: 583 */       throw new NullPointerException("No output instance format defined");
/*  304:     */     }
/*  305: 585 */     if (this.m_OutputQueue.empty()) {
/*  306: 586 */       return null;
/*  307:     */     }
/*  308: 588 */     Instance result = (Instance)this.m_OutputQueue.pop();
/*  309:     */     
/*  310:     */ 
/*  311:     */ 
/*  312:     */ 
/*  313:     */ 
/*  314:     */ 
/*  315:     */ 
/*  316:     */ 
/*  317: 597 */     return result;
/*  318:     */   }
/*  319:     */   
/*  320:     */   public Instance outputPeek()
/*  321:     */   {
/*  322: 609 */     if (this.m_OutputFormat == null) {
/*  323: 610 */       throw new NullPointerException("No output instance format defined");
/*  324:     */     }
/*  325: 612 */     if (this.m_OutputQueue.empty()) {
/*  326: 613 */       return null;
/*  327:     */     }
/*  328: 615 */     Instance result = (Instance)this.m_OutputQueue.peek();
/*  329: 616 */     return result;
/*  330:     */   }
/*  331:     */   
/*  332:     */   public int numPendingOutput()
/*  333:     */   {
/*  334: 627 */     if (this.m_OutputFormat == null) {
/*  335: 628 */       throw new NullPointerException("No output instance format defined");
/*  336:     */     }
/*  337: 630 */     return this.m_OutputQueue.size();
/*  338:     */   }
/*  339:     */   
/*  340:     */   public boolean isOutputFormatDefined()
/*  341:     */   {
/*  342: 640 */     return this.m_OutputFormat != null;
/*  343:     */   }
/*  344:     */   
/*  345:     */   public static Filter makeCopy(Filter model)
/*  346:     */     throws Exception
/*  347:     */   {
/*  348: 651 */     return (Filter)new SerializedObject(model).getObject();
/*  349:     */   }
/*  350:     */   
/*  351:     */   public static Filter[] makeCopies(Filter model, int num)
/*  352:     */     throws Exception
/*  353:     */   {
/*  354: 665 */     if (model == null) {
/*  355: 666 */       throw new Exception("No model filter set");
/*  356:     */     }
/*  357: 668 */     Filter[] filters = new Filter[num];
/*  358: 669 */     SerializedObject so = new SerializedObject(model);
/*  359: 670 */     for (int i = 0; i < filters.length; i++) {
/*  360: 671 */       filters[i] = ((Filter)so.getObject());
/*  361:     */     }
/*  362: 673 */     return filters;
/*  363:     */   }
/*  364:     */   
/*  365:     */   public static Instances useFilter(Instances data, Filter filter)
/*  366:     */     throws Exception
/*  367:     */   {
/*  368: 691 */     for (int i = 0; i < data.numInstances(); i++) {
/*  369: 692 */       filter.input(data.instance(i));
/*  370:     */     }
/*  371: 694 */     filter.batchFinished();
/*  372: 695 */     Instances newData = filter.getOutputFormat();
/*  373:     */     Instance processed;
/*  374: 697 */     while ((processed = filter.output()) != null) {
/*  375: 698 */       newData.add(processed);
/*  376:     */     }
/*  377: 705 */     return newData;
/*  378:     */   }
/*  379:     */   
/*  380:     */   public String toString()
/*  381:     */   {
/*  382: 715 */     return getClass().getName();
/*  383:     */   }
/*  384:     */   
/*  385:     */   public static String wekaStaticWrapper(Sourcable filter, String className, Instances input, Instances output)
/*  386:     */     throws Exception
/*  387:     */   {
/*  388: 735 */     StringBuffer result = new StringBuffer();
/*  389:     */     
/*  390: 737 */     result.append("// Generated with Weka " + Version.VERSION + "\n");
/*  391: 738 */     result.append("//\n");
/*  392: 739 */     result.append("// This code is public domain and comes with no warranty.\n");
/*  393:     */     
/*  394: 741 */     result.append("//\n");
/*  395: 742 */     result.append("// Timestamp: " + new Date() + "\n");
/*  396: 743 */     result.append("// Relation: " + input.relationName() + "\n");
/*  397: 744 */     result.append("\n");
/*  398:     */     
/*  399: 746 */     result.append("package weka.filters;\n");
/*  400: 747 */     result.append("\n");
/*  401: 748 */     result.append("import weka.core.Attribute;\n");
/*  402: 749 */     result.append("import weka.core.Capabilities;\n");
/*  403: 750 */     result.append("import weka.core.Capabilities.Capability;\n");
/*  404: 751 */     result.append("import weka.core.DenseInstance;\n");
/*  405: 752 */     result.append("import weka.core.Instance;\n");
/*  406: 753 */     result.append("import weka.core.Instances;\n");
/*  407: 754 */     result.append("import weka.core.Utils;\n");
/*  408: 755 */     result.append("import weka.filters.Filter;\n");
/*  409: 756 */     result.append("import java.util.ArrayList;\n");
/*  410: 757 */     result.append("\n");
/*  411: 758 */     result.append("public class WekaWrapper\n");
/*  412: 759 */     result.append("  extends Filter {\n");
/*  413:     */     
/*  414:     */ 
/*  415: 762 */     result.append("\n");
/*  416: 763 */     result.append("  /**\n");
/*  417: 764 */     result.append("   * Returns only the toString() method.\n");
/*  418: 765 */     result.append("   *\n");
/*  419: 766 */     result.append("   * @return a string describing the filter\n");
/*  420: 767 */     result.append("   */\n");
/*  421: 768 */     result.append("  public String globalInfo() {\n");
/*  422: 769 */     result.append("    return toString();\n");
/*  423: 770 */     result.append("  }\n");
/*  424:     */     
/*  425:     */ 
/*  426: 773 */     result.append("\n");
/*  427: 774 */     result.append("  /**\n");
/*  428: 775 */     result.append("   * Returns the capabilities of this filter.\n");
/*  429: 776 */     result.append("   *\n");
/*  430: 777 */     result.append("   * @return the capabilities\n");
/*  431: 778 */     result.append("   */\n");
/*  432: 779 */     result.append("  public Capabilities getCapabilities() {\n");
/*  433: 780 */     result.append(((Filter)filter).getCapabilities().toSource("result", 4));
/*  434: 781 */     result.append("    return result;\n");
/*  435: 782 */     result.append("  }\n");
/*  436:     */     
/*  437:     */ 
/*  438: 785 */     result.append("\n");
/*  439: 786 */     result.append("  /**\n");
/*  440: 787 */     result.append("   * turns array of Objects into an Instance object\n");
/*  441: 788 */     result.append("   *\n");
/*  442: 789 */     result.append("   * @param obj\tthe Object array to turn into an Instance\n");
/*  443:     */     
/*  444: 791 */     result.append("   * @param format\tthe data format to use\n");
/*  445: 792 */     result.append("   * @return\t\tthe generated Instance object\n");
/*  446: 793 */     result.append("   */\n");
/*  447: 794 */     result.append("  protected Instance objectsToInstance(Object[] obj, Instances format) {\n");
/*  448:     */     
/*  449: 796 */     result.append("    Instance\t\tresult;\n");
/*  450: 797 */     result.append("    double[]\t\tvalues;\n");
/*  451: 798 */     result.append("    int\t\ti;\n");
/*  452: 799 */     result.append("\n");
/*  453: 800 */     result.append("    values = new double[obj.length];\n");
/*  454: 801 */     result.append("\n");
/*  455: 802 */     result.append("    for (i = 0 ; i < obj.length; i++) {\n");
/*  456: 803 */     result.append("      if (obj[i] == null)\n");
/*  457: 804 */     result.append("        values[i] = Utils.missingValue();\n");
/*  458: 805 */     result.append("      else if (format.attribute(i).isNumeric())\n");
/*  459: 806 */     result.append("        values[i] = (Double) obj[i];\n");
/*  460: 807 */     result.append("      else if (format.attribute(i).isNominal())\n");
/*  461: 808 */     result.append("        values[i] = format.attribute(i).indexOfValue((String) obj[i]);\n");
/*  462:     */     
/*  463: 810 */     result.append("    }\n");
/*  464: 811 */     result.append("\n");
/*  465: 812 */     result.append("    // create new instance\n");
/*  466: 813 */     result.append("    result = new DenseInstance(1.0, values);\n");
/*  467: 814 */     result.append("    result.setDataset(format);\n");
/*  468: 815 */     result.append("\n");
/*  469: 816 */     result.append("    return result;\n");
/*  470: 817 */     result.append("  }\n");
/*  471:     */     
/*  472:     */ 
/*  473: 820 */     result.append("\n");
/*  474: 821 */     result.append("  /**\n");
/*  475: 822 */     result.append("   * turns the Instance object into an array of Objects\n");
/*  476: 823 */     result.append("   *\n");
/*  477: 824 */     result.append("   * @param inst\tthe instance to turn into an array\n");
/*  478: 825 */     result.append("   * @return\t\tthe Object array representing the instance\n");
/*  479:     */     
/*  480: 827 */     result.append("   */\n");
/*  481: 828 */     result.append("  protected Object[] instanceToObjects(Instance inst) {\n");
/*  482: 829 */     result.append("    Object[]\tresult;\n");
/*  483: 830 */     result.append("    int\t\ti;\n");
/*  484: 831 */     result.append("\n");
/*  485: 832 */     result.append("    result = new Object[inst.numAttributes()];\n");
/*  486: 833 */     result.append("\n");
/*  487: 834 */     result.append("    for (i = 0 ; i < inst.numAttributes(); i++) {\n");
/*  488: 835 */     result.append("      if (inst.isMissing(i))\n");
/*  489: 836 */     result.append("  \tresult[i] = null;\n");
/*  490: 837 */     result.append("      else if (inst.attribute(i).isNumeric())\n");
/*  491: 838 */     result.append("  \tresult[i] = inst.value(i);\n");
/*  492: 839 */     result.append("      else\n");
/*  493: 840 */     result.append("  \tresult[i] = inst.stringValue(i);\n");
/*  494: 841 */     result.append("    }\n");
/*  495: 842 */     result.append("\n");
/*  496: 843 */     result.append("    return result;\n");
/*  497: 844 */     result.append("  }\n");
/*  498:     */     
/*  499:     */ 
/*  500: 847 */     result.append("\n");
/*  501: 848 */     result.append("  /**\n");
/*  502: 849 */     result.append("   * turns the Instances object into an array of Objects\n");
/*  503: 850 */     result.append("   *\n");
/*  504: 851 */     result.append("   * @param data\tthe instances to turn into an array\n");
/*  505: 852 */     result.append("   * @return\t\tthe Object array representing the instances\n");
/*  506:     */     
/*  507: 854 */     result.append("   */\n");
/*  508: 855 */     result.append("  protected Object[][] instancesToObjects(Instances data) {\n");
/*  509:     */     
/*  510: 857 */     result.append("    Object[][]\tresult;\n");
/*  511: 858 */     result.append("    int\t\ti;\n");
/*  512: 859 */     result.append("\n");
/*  513: 860 */     result.append("    result = new Object[data.numInstances()][];\n");
/*  514: 861 */     result.append("\n");
/*  515: 862 */     result.append("    for (i = 0; i < data.numInstances(); i++)\n");
/*  516: 863 */     result.append("      result[i] = instanceToObjects(data.instance(i));\n");
/*  517: 864 */     result.append("\n");
/*  518: 865 */     result.append("    return result;\n");
/*  519: 866 */     result.append("  }\n");
/*  520:     */     
/*  521:     */ 
/*  522: 869 */     result.append("\n");
/*  523: 870 */     result.append("  /**\n");
/*  524: 871 */     result.append("   * Only tests the input data.\n");
/*  525: 872 */     result.append("   *\n");
/*  526: 873 */     result.append("   * @param instanceInfo the format of the data to convert\n");
/*  527:     */     
/*  528: 875 */     result.append("   * @return always true, to indicate that the output format can \n");
/*  529:     */     
/*  530: 877 */     result.append("   *         be collected immediately.\n");
/*  531: 878 */     result.append("   */\n");
/*  532: 879 */     result.append("  public boolean setInputFormat(Instances instanceInfo) throws Exception {\n");
/*  533:     */     
/*  534: 881 */     result.append("    super.setInputFormat(instanceInfo);\n");
/*  535: 882 */     result.append("    \n");
/*  536: 883 */     result.append("    // generate output format\n");
/*  537: 884 */     result.append("    ArrayList<Attribute> atts = new ArrayList<Attribute>();\n");
/*  538:     */     
/*  539: 886 */     result.append("    ArrayList<String> attValues;\n");
/*  540: 887 */     for (int i = 0; i < output.numAttributes(); i++)
/*  541:     */     {
/*  542: 888 */       result.append("    // " + output.attribute(i).name() + "\n");
/*  543: 889 */       if (output.attribute(i).isNumeric())
/*  544:     */       {
/*  545: 890 */         result.append("    atts.add(new Attribute(\"" + output.attribute(i).name() + "\"));\n");
/*  546:     */       }
/*  547: 892 */       else if (output.attribute(i).isNominal())
/*  548:     */       {
/*  549: 893 */         result.append("    attValues = new ArrayList<String>();\n");
/*  550: 894 */         for (int n = 0; n < output.attribute(i).numValues(); n++) {
/*  551: 895 */           result.append("    attValues.add(\"" + output.attribute(i).value(n) + "\");\n");
/*  552:     */         }
/*  553: 898 */         result.append("    atts.add(new Attribute(\"" + output.attribute(i).name() + "\", attValues));\n");
/*  554:     */       }
/*  555:     */       else
/*  556:     */       {
/*  557: 901 */         throw new UnsupportedAttributeTypeException("Attribute type '" + output.attribute(i).type() + "' (position " + (i + 1) + ") is not supported!");
/*  558:     */       }
/*  559:     */     }
/*  560: 906 */     result.append("    \n");
/*  561: 907 */     result.append("    Instances format = new Instances(\"" + output.relationName() + "\", atts, 0);\n");
/*  562:     */     
/*  563: 909 */     result.append("    format.setClassIndex(" + output.classIndex() + ");\n");
/*  564: 910 */     result.append("    setOutputFormat(format);\n");
/*  565: 911 */     result.append("    \n");
/*  566: 912 */     result.append("    return true;\n");
/*  567: 913 */     result.append("  }\n");
/*  568:     */     
/*  569:     */ 
/*  570: 916 */     result.append("\n");
/*  571: 917 */     result.append("  /**\n");
/*  572: 918 */     result.append("   * Directly filters the instance.\n");
/*  573: 919 */     result.append("   *\n");
/*  574: 920 */     result.append("   * @param instance the instance to convert\n");
/*  575: 921 */     result.append("   * @return always true, to indicate that the output can \n");
/*  576:     */     
/*  577: 923 */     result.append("   *         be collected immediately.\n");
/*  578: 924 */     result.append("   */\n");
/*  579: 925 */     result.append("  public boolean input(Instance instance) throws Exception {\n");
/*  580:     */     
/*  581: 927 */     result.append("    Object[] filtered = " + className + ".filter(instanceToObjects(instance));\n");
/*  582:     */     
/*  583: 929 */     result.append("    push(objectsToInstance(filtered, getOutputFormat()));\n");
/*  584:     */     
/*  585: 931 */     result.append("    return true;\n");
/*  586: 932 */     result.append("  }\n");
/*  587:     */     
/*  588:     */ 
/*  589: 935 */     result.append("\n");
/*  590: 936 */     result.append("  /**\n");
/*  591: 937 */     result.append("   * Performs a batch filtering of the buffered data, if any available.\n");
/*  592:     */     
/*  593: 939 */     result.append("   *\n");
/*  594: 940 */     result.append("   * @return true if instances were filtered otherwise false\n");
/*  595:     */     
/*  596: 942 */     result.append("   */\n");
/*  597: 943 */     result.append("  public boolean batchFinished() throws Exception {\n");
/*  598: 944 */     result.append("    if (getInputFormat() == null)\n");
/*  599: 945 */     result.append("      throw new NullPointerException(\"No input instance format defined\");;\n");
/*  600:     */     
/*  601: 947 */     result.append("\n");
/*  602: 948 */     result.append("    Instances inst = getInputFormat();\n");
/*  603: 949 */     result.append("    if (inst.numInstances() > 0) {\n");
/*  604: 950 */     result.append("      Object[][] filtered = " + className + ".filter(instancesToObjects(inst));\n");
/*  605:     */     
/*  606: 952 */     result.append("      for (int i = 0; i < filtered.length; i++) {\n");
/*  607: 953 */     result.append("        push(objectsToInstance(filtered[i], getOutputFormat()));\n");
/*  608:     */     
/*  609: 955 */     result.append("      }\n");
/*  610: 956 */     result.append("    }\n");
/*  611: 957 */     result.append("\n");
/*  612: 958 */     result.append("    flushInput();\n");
/*  613: 959 */     result.append("    m_NewBatch = true;\n");
/*  614: 960 */     result.append("    m_FirstBatchDone = true;\n");
/*  615: 961 */     result.append("\n");
/*  616: 962 */     result.append("    return (inst.numInstances() > 0);\n");
/*  617: 963 */     result.append("  }\n");
/*  618:     */     
/*  619:     */ 
/*  620: 966 */     result.append("\n");
/*  621: 967 */     result.append("  /**\n");
/*  622: 968 */     result.append("   * Returns only the classnames and what filter it is based on.\n");
/*  623:     */     
/*  624: 970 */     result.append("   *\n");
/*  625: 971 */     result.append("   * @return a short description\n");
/*  626: 972 */     result.append("   */\n");
/*  627: 973 */     result.append("  public String toString() {\n");
/*  628: 974 */     result.append("    return \"Auto-generated filter wrapper, based on " + filter.getClass().getName() + " (generated with Weka " + Version.VERSION + ").\\n" + "\" + this.getClass().getName() + \"/" + className + "\";\n");
/*  629:     */     
/*  630:     */ 
/*  631: 977 */     result.append("  }\n");
/*  632:     */     
/*  633:     */ 
/*  634: 980 */     result.append("\n");
/*  635: 981 */     result.append("  /**\n");
/*  636: 982 */     result.append("   * Runs the filter from commandline.\n");
/*  637: 983 */     result.append("   *\n");
/*  638: 984 */     result.append("   * @param args the commandline arguments\n");
/*  639: 985 */     result.append("   */\n");
/*  640: 986 */     result.append("  public static void main(String args[]) {\n");
/*  641: 987 */     result.append("    runFilter(new WekaWrapper(), args);\n");
/*  642: 988 */     result.append("  }\n");
/*  643: 989 */     result.append("}\n");
/*  644:     */     
/*  645:     */ 
/*  646: 992 */     result.append("\n");
/*  647: 993 */     result.append(filter.toSource(className, input));
/*  648:     */     
/*  649: 995 */     return result.toString();
/*  650:     */   }
/*  651:     */   
/*  652:     */   public static void filterFile(Filter filter, String[] options)
/*  653:     */     throws Exception
/*  654:     */   {
/*  655:1017 */     boolean debug = false;
/*  656:1018 */     Instances data = null;
/*  657:1019 */     ConverterUtils.DataSource input = null;
/*  658:1020 */     PrintWriter output = null;
/*  659:     */     
/*  660:1022 */     String sourceCode = "";
/*  661:1023 */     int maxDecimalPlaces = 6;
/*  662:     */     try
/*  663:     */     {
/*  664:1026 */       boolean helpRequest = Utils.getFlag('h', options);
/*  665:1028 */       if (Utils.getFlag('d', options)) {
/*  666:1029 */         debug = true;
/*  667:     */       }
/*  668:1031 */       String infileName = Utils.getOption('i', options);
/*  669:1032 */       String outfileName = Utils.getOption('o', options);
/*  670:1033 */       String classIndex = Utils.getOption('c', options);
/*  671:1034 */       if ((filter instanceof Sourcable)) {
/*  672:1035 */         sourceCode = Utils.getOption('z', options);
/*  673:     */       }
/*  674:1038 */       String tmpStr = Utils.getOption("decimal", options);
/*  675:1039 */       if (tmpStr.length() > 0) {
/*  676:1040 */         maxDecimalPlaces = Integer.parseInt(tmpStr);
/*  677:     */       }
/*  678:1043 */       if ((filter instanceof OptionHandler)) {
/*  679:1044 */         filter.setOptions(options);
/*  680:     */       }
/*  681:1047 */       Utils.checkForRemainingOptions(options);
/*  682:1048 */       if (helpRequest) {
/*  683:1049 */         throw new Exception("Help requested.\n");
/*  684:     */       }
/*  685:1051 */       if (infileName.length() != 0) {
/*  686:1052 */         input = new ConverterUtils.DataSource(infileName);
/*  687:     */       } else {
/*  688:1054 */         input = new ConverterUtils.DataSource(System.in);
/*  689:     */       }
/*  690:1056 */       if (outfileName.length() != 0) {
/*  691:1057 */         output = new PrintWriter(new FileOutputStream(outfileName));
/*  692:     */       } else {
/*  693:1059 */         output = new PrintWriter(System.out);
/*  694:     */       }
/*  695:1062 */       data = input.getStructure();
/*  696:1063 */       if (classIndex.length() != 0) {
/*  697:1064 */         if (classIndex.equals("first")) {
/*  698:1065 */           data.setClassIndex(0);
/*  699:1066 */         } else if (classIndex.equals("last")) {
/*  700:1067 */           data.setClassIndex(data.numAttributes() - 1);
/*  701:     */         } else {
/*  702:1069 */           data.setClassIndex(Integer.parseInt(classIndex) - 1);
/*  703:     */         }
/*  704:     */       }
/*  705:     */     }
/*  706:     */     catch (Exception ex)
/*  707:     */     {
/*  708:1073 */       String filterOptions = "";
/*  709:1075 */       if ((filter instanceof OptionHandler))
/*  710:     */       {
/*  711:1076 */         filterOptions = filterOptions + "\nFilter options:\n\n";
/*  712:1077 */         Enumeration<Option> enu = filter.listOptions();
/*  713:1078 */         while (enu.hasMoreElements())
/*  714:     */         {
/*  715:1079 */           Option option = (Option)enu.nextElement();
/*  716:1080 */           filterOptions = filterOptions + option.synopsis() + '\n' + option.description() + "\n";
/*  717:     */         }
/*  718:     */       }
/*  719:1085 */       String genericOptions = "\nGeneral options:\n\n-h\n\tGet help on available options.\n\t(use -b -h for help on batch mode.)\n-i <file>\n\tThe name of the file containing input instances.\n\tIf not supplied then instances will be read from stdin.\n-o <file>\n\tThe name of the file output instances will be written to.\n\tIf not supplied then instances will be written to stdout.\n-c <class index>\n\tThe number of the attribute to use as the class.\n\t\"first\" and \"last\" are also valid entries.\n\tIf not supplied then no class is assigned.\n-decimal <integer>\n\tThe maximum number of digits to print after the decimal\n\tplace for numeric values (default: 6)\n";
/*  720:1101 */       if ((filter instanceof Sourcable)) {
/*  721:1102 */         genericOptions = genericOptions + "-z <class name>\n\tOutputs the source code representing the trained filter.\n";
/*  722:     */       }
/*  723:1106 */       throw new Exception('\n' + ex.getMessage() + filterOptions + genericOptions);
/*  724:     */     }
/*  725:1110 */     if (debug) {
/*  726:1111 */       System.err.println("Setting input format");
/*  727:     */     }
/*  728:1113 */     boolean printedHeader = false;
/*  729:1114 */     if (filter.setInputFormat(data))
/*  730:     */     {
/*  731:1115 */       if (debug) {
/*  732:1116 */         System.err.println("Getting output format");
/*  733:     */       }
/*  734:1118 */       output.println(filter.getOutputFormat().toString());
/*  735:1119 */       printedHeader = true;
/*  736:     */     }
/*  737:1124 */     while (input.hasMoreElements(data))
/*  738:     */     {
/*  739:1125 */       Instance inst = input.nextElement(data);
/*  740:1126 */       if (debug) {
/*  741:1127 */         System.err.println("Input instance to filter");
/*  742:     */       }
/*  743:1129 */       if (filter.input(inst))
/*  744:     */       {
/*  745:1130 */         if (debug) {
/*  746:1131 */           System.err.println("Filter said collect immediately");
/*  747:     */         }
/*  748:1133 */         if (!printedHeader) {
/*  749:1134 */           throw new Error("Filter didn't return true from setInputFormat() earlier!");
/*  750:     */         }
/*  751:1137 */         if (debug) {
/*  752:1138 */           System.err.println("Getting output instance");
/*  753:     */         }
/*  754:1140 */         output.println(filter.output().toStringMaxDecimalDigits(maxDecimalPlaces));
/*  755:     */       }
/*  756:     */     }
/*  757:1146 */     if (debug) {
/*  758:1147 */       System.err.println("Setting end of batch");
/*  759:     */     }
/*  760:1149 */     if (filter.batchFinished())
/*  761:     */     {
/*  762:1150 */       if (debug) {
/*  763:1151 */         System.err.println("Filter said collect output");
/*  764:     */       }
/*  765:1153 */       if (!printedHeader)
/*  766:     */       {
/*  767:1154 */         if (debug) {
/*  768:1155 */           System.err.println("Getting output format");
/*  769:     */         }
/*  770:1157 */         output.println(filter.getOutputFormat().toString());
/*  771:     */       }
/*  772:1159 */       if (debug) {
/*  773:1160 */         System.err.println("Getting output instance");
/*  774:     */       }
/*  775:1162 */       while (filter.numPendingOutput() > 0)
/*  776:     */       {
/*  777:1163 */         output.println(filter.output().toStringMaxDecimalDigits(maxDecimalPlaces));
/*  778:1165 */         if (debug) {
/*  779:1166 */           System.err.println("Getting output instance");
/*  780:     */         }
/*  781:     */       }
/*  782:     */     }
/*  783:1170 */     if (debug) {
/*  784:1171 */       System.err.println("Done");
/*  785:     */     }
/*  786:1174 */     if (output != null) {
/*  787:1175 */       output.close();
/*  788:     */     }
/*  789:1178 */     if (sourceCode.length() != 0) {
/*  790:1179 */       System.out.println(wekaStaticWrapper((Sourcable)filter, sourceCode, data, filter.getOutputFormat()));
/*  791:     */     }
/*  792:     */   }
/*  793:     */   
/*  794:     */   public static void batchFilterFile(Filter filter, String[] options)
/*  795:     */     throws Exception
/*  796:     */   {
/*  797:1205 */     Instances firstData = null;
/*  798:1206 */     Instances secondData = null;
/*  799:1207 */     ConverterUtils.DataSource firstInput = null;
/*  800:1208 */     ConverterUtils.DataSource secondInput = null;
/*  801:1209 */     PrintWriter firstOutput = null;
/*  802:1210 */     PrintWriter secondOutput = null;
/*  803:     */     
/*  804:1212 */     String sourceCode = "";
/*  805:1213 */     int maxDecimalPlaces = 6;
/*  806:     */     try
/*  807:     */     {
/*  808:1216 */       boolean helpRequest = Utils.getFlag('h', options);
/*  809:     */       
/*  810:1218 */       String fileName = Utils.getOption('i', options);
/*  811:1219 */       if (fileName.length() != 0) {
/*  812:1220 */         firstInput = new ConverterUtils.DataSource(fileName);
/*  813:     */       } else {
/*  814:1222 */         throw new Exception("No first input file given.\n");
/*  815:     */       }
/*  816:1225 */       fileName = Utils.getOption('r', options);
/*  817:1226 */       if (fileName.length() != 0) {
/*  818:1227 */         secondInput = new ConverterUtils.DataSource(fileName);
/*  819:     */       } else {
/*  820:1229 */         throw new Exception("No second input file given.\n");
/*  821:     */       }
/*  822:1232 */       fileName = Utils.getOption('o', options);
/*  823:1233 */       if (fileName.length() != 0) {
/*  824:1234 */         firstOutput = new PrintWriter(new FileOutputStream(fileName));
/*  825:     */       } else {
/*  826:1236 */         firstOutput = new PrintWriter(System.out);
/*  827:     */       }
/*  828:1239 */       fileName = Utils.getOption('s', options);
/*  829:1240 */       if (fileName.length() != 0) {
/*  830:1241 */         secondOutput = new PrintWriter(new FileOutputStream(fileName));
/*  831:     */       } else {
/*  832:1243 */         secondOutput = new PrintWriter(System.out);
/*  833:     */       }
/*  834:1245 */       String classIndex = Utils.getOption('c', options);
/*  835:1246 */       if ((filter instanceof Sourcable)) {
/*  836:1247 */         sourceCode = Utils.getOption('z', options);
/*  837:     */       }
/*  838:1250 */       String tmpStr = Utils.getOption("decimal", options);
/*  839:1251 */       if (tmpStr.length() > 0) {
/*  840:1252 */         maxDecimalPlaces = Integer.parseInt(tmpStr);
/*  841:     */       }
/*  842:1255 */       if ((filter instanceof OptionHandler)) {
/*  843:1256 */         filter.setOptions(options);
/*  844:     */       }
/*  845:1258 */       Utils.checkForRemainingOptions(options);
/*  846:1260 */       if (helpRequest) {
/*  847:1261 */         throw new Exception("Help requested.\n");
/*  848:     */       }
/*  849:1263 */       firstData = firstInput.getStructure();
/*  850:1264 */       secondData = secondInput.getStructure();
/*  851:1265 */       if (!secondData.equalHeaders(firstData)) {
/*  852:1266 */         throw new Exception("Input file formats differ.\n" + secondData.equalHeadersMsg(firstData) + "\n");
/*  853:     */       }
/*  854:1269 */       if (classIndex.length() != 0) {
/*  855:1270 */         if (classIndex.equals("first"))
/*  856:     */         {
/*  857:1271 */           firstData.setClassIndex(0);
/*  858:1272 */           secondData.setClassIndex(0);
/*  859:     */         }
/*  860:1273 */         else if (classIndex.equals("last"))
/*  861:     */         {
/*  862:1274 */           firstData.setClassIndex(firstData.numAttributes() - 1);
/*  863:1275 */           secondData.setClassIndex(secondData.numAttributes() - 1);
/*  864:     */         }
/*  865:     */         else
/*  866:     */         {
/*  867:1277 */           firstData.setClassIndex(Integer.parseInt(classIndex) - 1);
/*  868:1278 */           secondData.setClassIndex(Integer.parseInt(classIndex) - 1);
/*  869:     */         }
/*  870:     */       }
/*  871:     */     }
/*  872:     */     catch (Exception ex)
/*  873:     */     {
/*  874:1282 */       String filterOptions = "";
/*  875:1284 */       if ((filter instanceof OptionHandler))
/*  876:     */       {
/*  877:1285 */         filterOptions = filterOptions + "\nFilter options:\n\n";
/*  878:1286 */         Enumeration<Option> enu = filter.listOptions();
/*  879:1287 */         while (enu.hasMoreElements())
/*  880:     */         {
/*  881:1288 */           Option option = (Option)enu.nextElement();
/*  882:1289 */           filterOptions = filterOptions + option.synopsis() + '\n' + option.description() + "\n";
/*  883:     */         }
/*  884:     */       }
/*  885:1294 */       String genericOptions = "\nGeneral options:\n\n-h\n\tGet help on available options.\n-i <filename>\n\tThe file containing first input instances.\n-o <filename>\n\tThe file first output instances will be written to.\n-r <filename>\n\tThe file containing second input instances.\n-s <filename>\n\tThe file second output instances will be written to.\n-c <class index>\n\tThe number of the attribute to use as the class.\n\t\"first\" and \"last\" are also valid entries.\n\tIf not supplied then no class is assigned.\n-decimal <integer>\n\tThe maximum number of digits to print after the decimal\n\tplace for numeric values (default: 6)\n";
/*  886:1309 */       if ((filter instanceof Sourcable)) {
/*  887:1310 */         genericOptions = genericOptions + "-z <class name>\n\tOutputs the source code representing the trained filter.\n";
/*  888:     */       }
/*  889:1314 */       throw new Exception('\n' + ex.getMessage() + filterOptions + genericOptions);
/*  890:     */     }
/*  891:1317 */     boolean printedHeader = false;
/*  892:1318 */     if (filter.setInputFormat(firstData))
/*  893:     */     {
/*  894:1319 */       firstOutput.println(filter.getOutputFormat().toString());
/*  895:1320 */       printedHeader = true;
/*  896:     */     }
/*  897:1325 */     while (firstInput.hasMoreElements(firstData))
/*  898:     */     {
/*  899:1326 */       Instance inst = firstInput.nextElement(firstData);
/*  900:1327 */       if (filter.input(inst))
/*  901:     */       {
/*  902:1328 */         if (!printedHeader) {
/*  903:1329 */           throw new Error("Filter didn't return true from setInputFormat() earlier!");
/*  904:     */         }
/*  905:1332 */         firstOutput.println(filter.output().toStringMaxDecimalDigits(maxDecimalPlaces));
/*  906:     */       }
/*  907:     */     }
/*  908:1338 */     if (filter.batchFinished())
/*  909:     */     {
/*  910:1339 */       if (!printedHeader) {
/*  911:1340 */         firstOutput.println(filter.getOutputFormat().toString());
/*  912:     */       }
/*  913:1342 */       while (filter.numPendingOutput() > 0) {
/*  914:1343 */         firstOutput.println(filter.output().toStringMaxDecimalDigits(maxDecimalPlaces));
/*  915:     */       }
/*  916:     */     }
/*  917:1348 */     if (firstOutput != null) {
/*  918:1349 */       firstOutput.close();
/*  919:     */     }
/*  920:1351 */     printedHeader = false;
/*  921:1352 */     if (filter.isOutputFormatDefined())
/*  922:     */     {
/*  923:1353 */       secondOutput.println(filter.getOutputFormat().toString());
/*  924:1354 */       printedHeader = true;
/*  925:     */     }
/*  926:1357 */     while (secondInput.hasMoreElements(secondData))
/*  927:     */     {
/*  928:1358 */       Instance inst = secondInput.nextElement(secondData);
/*  929:1359 */       if (filter.input(inst))
/*  930:     */       {
/*  931:1360 */         if (!printedHeader) {
/*  932:1361 */           throw new Error("Filter didn't return true from isOutputFormatDefined() earlier!");
/*  933:     */         }
/*  934:1364 */         secondOutput.println(filter.output().toStringMaxDecimalDigits(maxDecimalPlaces));
/*  935:     */       }
/*  936:     */     }
/*  937:1370 */     if (filter.batchFinished())
/*  938:     */     {
/*  939:1371 */       if (!printedHeader) {
/*  940:1372 */         secondOutput.println(filter.getOutputFormat().toString());
/*  941:     */       }
/*  942:1374 */       while (filter.numPendingOutput() > 0) {
/*  943:1375 */         secondOutput.println(filter.output().toStringMaxDecimalDigits(maxDecimalPlaces));
/*  944:     */       }
/*  945:     */     }
/*  946:1379 */     if (secondOutput != null) {
/*  947:1380 */       secondOutput.close();
/*  948:     */     }
/*  949:1383 */     if (sourceCode.length() != 0) {
/*  950:1384 */       System.out.println(wekaStaticWrapper((Sourcable)filter, sourceCode, firstData, filter.getOutputFormat()));
/*  951:     */     }
/*  952:     */   }
/*  953:     */   
/*  954:     */   public static void runFilter(Filter filter, String[] options)
/*  955:     */   {
/*  956:     */     try
/*  957:     */     {
/*  958:1397 */       filter.preExecution();
/*  959:1398 */       if (Utils.getFlag('b', options)) {
/*  960:1399 */         batchFilterFile(filter, options);
/*  961:     */       } else {
/*  962:1401 */         filterFile(filter, options);
/*  963:     */       }
/*  964:     */     }
/*  965:     */     catch (Exception e)
/*  966:     */     {
/*  967:1404 */       if ((e.toString().indexOf("Help requested") == -1) && (e.toString().indexOf("Filter options") == -1)) {
/*  968:1406 */         e.printStackTrace();
/*  969:     */       } else {
/*  970:1408 */         System.err.println(e.getMessage());
/*  971:     */       }
/*  972:     */     }
/*  973:     */     try
/*  974:     */     {
/*  975:1412 */       filter.postExecution();
/*  976:     */     }
/*  977:     */     catch (Exception ex)
/*  978:     */     {
/*  979:1414 */       ex.printStackTrace();
/*  980:     */     }
/*  981:     */   }
/*  982:     */   
/*  983:     */   public Enumeration<Option> listOptions()
/*  984:     */   {
/*  985:1426 */     Vector<Option> newVector = Option.listOptionsForClassHierarchy(getClass(), Filter.class);
/*  986:     */     
/*  987:     */ 
/*  988:1429 */     newVector.addElement(new Option("\tIf set, filter is run in debug mode and\n\tmay output additional info to the console", "output-debug-info", 0, "-output-debug-info"));
/*  989:     */     
/*  990:     */ 
/*  991:     */ 
/*  992:1433 */     newVector.addElement(new Option("\tIf set, filter capabilities are not checked before filter is built\n\t(use with caution).", "-do-not-check-capabilities", 0, "-do-not-check-capabilities"));
/*  993:     */     
/*  994:     */ 
/*  995:     */ 
/*  996:     */ 
/*  997:1438 */     return newVector.elements();
/*  998:     */   }
/*  999:     */   
/* 1000:     */   public void setOptions(String[] options)
/* 1001:     */     throws Exception
/* 1002:     */   {
/* 1003:1461 */     Option.setOptionsForHierarchy(options, this, Filter.class);
/* 1004:1462 */     setDebug(Utils.getFlag("output-debug-info", options));
/* 1005:1463 */     setDoNotCheckCapabilities(Utils.getFlag("do-not-check-capabilities", options));
/* 1006:     */   }
/* 1007:     */   
/* 1008:     */   public String[] getOptions()
/* 1009:     */   {
/* 1010:1475 */     Vector<String> options = new Vector();
/* 1011:1476 */     for (String s : Option.getOptionsForHierarchy(this, Filter.class)) {
/* 1012:1477 */       options.add(s);
/* 1013:     */     }
/* 1014:1480 */     if (getDebug()) {
/* 1015:1481 */       options.add("-output-debug-info");
/* 1016:     */     }
/* 1017:1483 */     if (getDoNotCheckCapabilities()) {
/* 1018:1484 */       options.add("-do-not-check-capabilities");
/* 1019:     */     }
/* 1020:1487 */     return (String[])options.toArray(new String[0]);
/* 1021:     */   }
/* 1022:     */   
/* 1023:     */   public void setDebug(boolean debug)
/* 1024:     */   {
/* 1025:1497 */     this.m_Debug = debug;
/* 1026:     */   }
/* 1027:     */   
/* 1028:     */   public boolean getDebug()
/* 1029:     */   {
/* 1030:1507 */     return this.m_Debug;
/* 1031:     */   }
/* 1032:     */   
/* 1033:     */   public String debugTipText()
/* 1034:     */   {
/* 1035:1517 */     return "If set to true, filter may output additional info to the console.";
/* 1036:     */   }
/* 1037:     */   
/* 1038:     */   public void setDoNotCheckCapabilities(boolean doNotCheckCapabilities)
/* 1039:     */   {
/* 1040:1528 */     this.m_DoNotCheckCapabilities = doNotCheckCapabilities;
/* 1041:     */   }
/* 1042:     */   
/* 1043:     */   public boolean getDoNotCheckCapabilities()
/* 1044:     */   {
/* 1045:1538 */     return this.m_DoNotCheckCapabilities;
/* 1046:     */   }
/* 1047:     */   
/* 1048:     */   public String doNotCheckCapabilitiesTipText()
/* 1049:     */   {
/* 1050:1548 */     return "If set, filters capabilities are not checked before filter is built (Use with caution to reduce runtime).";
/* 1051:     */   }
/* 1052:     */   
/* 1053:     */   public void preExecution()
/* 1054:     */     throws Exception
/* 1055:     */   {}
/* 1056:     */   
/* 1057:     */   public void run(Object toRun, String[] options)
/* 1058:     */     throws Exception
/* 1059:     */   {
/* 1060:1571 */     if (!(toRun instanceof Filter)) {
/* 1061:1572 */       throw new IllegalArgumentException("Object to run is not a Filter!");
/* 1062:     */     }
/* 1063:1574 */     runFilter((Filter)toRun, options);
/* 1064:     */   }
/* 1065:     */   
/* 1066:     */   public void postExecution()
/* 1067:     */     throws Exception
/* 1068:     */   {}
/* 1069:     */   
/* 1070:     */   public static void main(String[] args)
/* 1071:     */   {
/* 1072:     */     try
/* 1073:     */     {
/* 1074:1595 */       if (args.length == 0) {
/* 1075:1596 */         throw new Exception("First argument must be the class name of a Filter");
/* 1076:     */       }
/* 1077:1599 */       String fname = args[0];
/* 1078:1600 */       Filter f = (Filter)Class.forName(fname).newInstance();
/* 1079:1601 */       args[0] = "";
/* 1080:1602 */       runFilter(f, args);
/* 1081:     */     }
/* 1082:     */     catch (Exception ex)
/* 1083:     */     {
/* 1084:1604 */       ex.printStackTrace();
/* 1085:1605 */       System.err.println(ex.getMessage());
/* 1086:     */     }
/* 1087:     */   }
/* 1088:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.Filter
 * JD-Core Version:    0.7.0.1
 */