/*    1:     */ package weka.core.converters;
/*    2:     */ 
/*    3:     */ import java.io.File;
/*    4:     */ import java.io.IOException;
/*    5:     */ import java.io.InputStream;
/*    6:     */ import java.io.OutputStream;
/*    7:     */ import java.io.PrintStream;
/*    8:     */ import java.io.Serializable;
/*    9:     */ import java.io.StreamTokenizer;
/*   10:     */ import java.net.URL;
/*   11:     */ import java.util.Arrays;
/*   12:     */ import java.util.Collections;
/*   13:     */ import java.util.Enumeration;
/*   14:     */ import java.util.Hashtable;
/*   15:     */ import java.util.Properties;
/*   16:     */ import java.util.Vector;
/*   17:     */ import weka.core.ClassDiscovery;
/*   18:     */ import weka.core.Instance;
/*   19:     */ import weka.core.Instances;
/*   20:     */ import weka.core.RevisionHandler;
/*   21:     */ import weka.core.RevisionUtils;
/*   22:     */ import weka.gui.ConverterFileChooser;
/*   23:     */ import weka.gui.GenericObjectEditor;
/*   24:     */ import weka.gui.GenericPropertiesCreator;
/*   25:     */ 
/*   26:     */ public class ConverterUtils
/*   27:     */   implements Serializable, RevisionHandler
/*   28:     */ {
/*   29:     */   static final long serialVersionUID = -2460855349276148760L;
/*   30:     */   
/*   31:     */   public static class DataSource
/*   32:     */     implements Serializable, RevisionHandler
/*   33:     */   {
/*   34:     */     private static final long serialVersionUID = -613122395928757332L;
/*   35:     */     protected File m_File;
/*   36:     */     protected URL m_URL;
/*   37:     */     protected Loader m_Loader;
/*   38:     */     protected boolean m_Incremental;
/*   39:     */     protected int m_BatchCounter;
/*   40:     */     protected Instance m_IncrementalBuffer;
/*   41:     */     protected Instances m_BatchBuffer;
/*   42:     */     
/*   43:     */     public DataSource(String location)
/*   44:     */       throws Exception
/*   45:     */     {
/*   46: 114 */       if ((location.startsWith("http://")) || (location.startsWith("https://")) || (location.startsWith("ftp://")) || (location.startsWith("file://"))) {
/*   47: 116 */         this.m_URL = new URL(location);
/*   48:     */       } else {
/*   49: 118 */         this.m_File = new File(location);
/*   50:     */       }
/*   51: 122 */       if (isArff(location))
/*   52:     */       {
/*   53: 123 */         this.m_Loader = new ArffLoader();
/*   54:     */       }
/*   55:     */       else
/*   56:     */       {
/*   57: 125 */         if (this.m_File != null) {
/*   58: 126 */           this.m_Loader = ConverterUtils.getLoaderForFile(location);
/*   59:     */         } else {
/*   60: 128 */           this.m_Loader = ConverterUtils.getURLLoaderForFile(location);
/*   61:     */         }
/*   62: 132 */         if (this.m_Loader == null) {
/*   63: 133 */           throw new IllegalArgumentException("No suitable converter found for '" + location + "'!");
/*   64:     */         }
/*   65:     */       }
/*   66: 139 */       this.m_Incremental = (this.m_Loader instanceof IncrementalConverter);
/*   67:     */       
/*   68: 141 */       reset();
/*   69:     */     }
/*   70:     */     
/*   71:     */     public DataSource(Instances inst)
/*   72:     */     {
/*   73: 152 */       this.m_BatchBuffer = inst;
/*   74: 153 */       this.m_Loader = null;
/*   75: 154 */       this.m_File = null;
/*   76: 155 */       this.m_URL = null;
/*   77: 156 */       this.m_Incremental = false;
/*   78:     */     }
/*   79:     */     
/*   80:     */     public DataSource(Loader loader)
/*   81:     */     {
/*   82: 167 */       this.m_BatchBuffer = null;
/*   83: 168 */       this.m_Loader = loader;
/*   84: 169 */       this.m_File = null;
/*   85: 170 */       this.m_URL = null;
/*   86: 171 */       this.m_Incremental = (this.m_Loader instanceof IncrementalConverter);
/*   87:     */       
/*   88: 173 */       initBatchBuffer();
/*   89:     */     }
/*   90:     */     
/*   91:     */     public DataSource(InputStream stream)
/*   92:     */     {
/*   93: 185 */       this.m_BatchBuffer = null;
/*   94: 186 */       this.m_Loader = new ArffLoader();
/*   95:     */       try
/*   96:     */       {
/*   97: 188 */         this.m_Loader.setSource(stream);
/*   98:     */       }
/*   99:     */       catch (Exception e)
/*  100:     */       {
/*  101: 190 */         this.m_Loader = null;
/*  102:     */       }
/*  103: 192 */       this.m_File = null;
/*  104: 193 */       this.m_URL = null;
/*  105: 194 */       this.m_Incremental = (this.m_Loader instanceof IncrementalConverter);
/*  106:     */       
/*  107: 196 */       initBatchBuffer();
/*  108:     */     }
/*  109:     */     
/*  110:     */     protected void initBatchBuffer()
/*  111:     */     {
/*  112:     */       try
/*  113:     */       {
/*  114: 205 */         if (!isIncremental()) {
/*  115: 206 */           this.m_BatchBuffer = this.m_Loader.getDataSet();
/*  116:     */         } else {
/*  117: 208 */           this.m_BatchBuffer = null;
/*  118:     */         }
/*  119:     */       }
/*  120:     */       catch (Exception e)
/*  121:     */       {
/*  122: 211 */         e.printStackTrace();
/*  123:     */       }
/*  124:     */     }
/*  125:     */     
/*  126:     */     public static boolean isArff(String location)
/*  127:     */     {
/*  128: 223 */       if ((location.toLowerCase().endsWith(ArffLoader.FILE_EXTENSION.toLowerCase())) || (location.toLowerCase().endsWith(ArffLoader.FILE_EXTENSION_COMPRESSED.toLowerCase()))) {
/*  129: 227 */         return true;
/*  130:     */       }
/*  131: 229 */       return false;
/*  132:     */     }
/*  133:     */     
/*  134:     */     public boolean isIncremental()
/*  135:     */     {
/*  136: 239 */       return this.m_Incremental;
/*  137:     */     }
/*  138:     */     
/*  139:     */     public Loader getLoader()
/*  140:     */     {
/*  141: 249 */       return this.m_Loader;
/*  142:     */     }
/*  143:     */     
/*  144:     */     public Instances getDataSet()
/*  145:     */       throws Exception
/*  146:     */     {
/*  147: 261 */       Instances result = null;
/*  148:     */       
/*  149:     */ 
/*  150: 264 */       reset();
/*  151:     */       try
/*  152:     */       {
/*  153: 267 */         if (this.m_BatchBuffer == null) {
/*  154: 268 */           result = this.m_Loader.getDataSet();
/*  155:     */         } else {
/*  156: 270 */           result = this.m_BatchBuffer;
/*  157:     */         }
/*  158:     */       }
/*  159:     */       catch (Exception e)
/*  160:     */       {
/*  161: 273 */         e.printStackTrace();
/*  162: 274 */         result = null;
/*  163:     */       }
/*  164: 277 */       return result;
/*  165:     */     }
/*  166:     */     
/*  167:     */     public Instances getDataSet(int classIndex)
/*  168:     */       throws Exception
/*  169:     */     {
/*  170: 291 */       Instances result = getDataSet();
/*  171: 292 */       if (result != null) {
/*  172: 293 */         result.setClassIndex(classIndex);
/*  173:     */       }
/*  174: 296 */       return result;
/*  175:     */     }
/*  176:     */     
/*  177:     */     public void reset()
/*  178:     */       throws Exception
/*  179:     */     {
/*  180: 305 */       if (this.m_File != null) {
/*  181: 306 */         ((AbstractFileLoader)this.m_Loader).setFile(this.m_File);
/*  182: 307 */       } else if (this.m_URL != null) {
/*  183: 308 */         ((URLSourcedLoader)this.m_Loader).setURL(this.m_URL.toString());
/*  184: 309 */       } else if (this.m_Loader != null) {
/*  185: 310 */         this.m_Loader.reset();
/*  186:     */       }
/*  187: 313 */       this.m_BatchCounter = 0;
/*  188: 314 */       this.m_IncrementalBuffer = null;
/*  189: 316 */       if (this.m_Loader != null) {
/*  190: 317 */         if (!isIncremental()) {
/*  191: 318 */           this.m_BatchBuffer = this.m_Loader.getDataSet();
/*  192:     */         } else {
/*  193: 320 */           this.m_BatchBuffer = null;
/*  194:     */         }
/*  195:     */       }
/*  196:     */     }
/*  197:     */     
/*  198:     */     public Instances getStructure()
/*  199:     */       throws Exception
/*  200:     */     {
/*  201: 332 */       if (this.m_BatchBuffer == null) {
/*  202: 333 */         return this.m_Loader.getStructure();
/*  203:     */       }
/*  204: 335 */       return new Instances(this.m_BatchBuffer, 0);
/*  205:     */     }
/*  206:     */     
/*  207:     */     public Instances getStructure(int classIndex)
/*  208:     */       throws Exception
/*  209:     */     {
/*  210: 349 */       Instances result = getStructure();
/*  211: 350 */       if (result != null) {
/*  212: 351 */         result.setClassIndex(classIndex);
/*  213:     */       }
/*  214: 354 */       return result;
/*  215:     */     }
/*  216:     */     
/*  217:     */     public boolean hasMoreElements(Instances structure)
/*  218:     */     {
/*  219: 367 */       boolean result = false;
/*  220: 369 */       if (isIncremental())
/*  221:     */       {
/*  222: 371 */         if (this.m_IncrementalBuffer != null) {
/*  223: 372 */           result = true;
/*  224:     */         } else {
/*  225:     */           try
/*  226:     */           {
/*  227: 375 */             this.m_IncrementalBuffer = this.m_Loader.getNextInstance(structure);
/*  228: 376 */             result = this.m_IncrementalBuffer != null;
/*  229:     */           }
/*  230:     */           catch (Exception e)
/*  231:     */           {
/*  232: 378 */             e.printStackTrace();
/*  233: 379 */             result = false;
/*  234:     */           }
/*  235:     */         }
/*  236:     */       }
/*  237:     */       else {
/*  238: 383 */         result = this.m_BatchCounter < this.m_BatchBuffer.numInstances();
/*  239:     */       }
/*  240: 386 */       return result;
/*  241:     */     }
/*  242:     */     
/*  243:     */     public Instance nextElement(Instances dataset)
/*  244:     */     {
/*  245: 399 */       Instance result = null;
/*  246: 401 */       if (isIncremental())
/*  247:     */       {
/*  248: 403 */         if (this.m_IncrementalBuffer != null)
/*  249:     */         {
/*  250: 404 */           result = this.m_IncrementalBuffer;
/*  251: 405 */           this.m_IncrementalBuffer = null;
/*  252:     */         }
/*  253:     */         else
/*  254:     */         {
/*  255:     */           try
/*  256:     */           {
/*  257: 408 */             result = this.m_Loader.getNextInstance(dataset);
/*  258:     */           }
/*  259:     */           catch (Exception e)
/*  260:     */           {
/*  261: 410 */             e.printStackTrace();
/*  262: 411 */             result = null;
/*  263:     */           }
/*  264:     */         }
/*  265:     */       }
/*  266: 415 */       else if (this.m_BatchCounter < this.m_BatchBuffer.numInstances())
/*  267:     */       {
/*  268: 416 */         result = this.m_BatchBuffer.instance(this.m_BatchCounter);
/*  269: 417 */         this.m_BatchCounter += 1;
/*  270:     */       }
/*  271: 421 */       if (result != null) {
/*  272: 422 */         result.setDataset(dataset);
/*  273:     */       }
/*  274: 425 */       return result;
/*  275:     */     }
/*  276:     */     
/*  277:     */     public static Instances read(String location)
/*  278:     */       throws Exception
/*  279:     */     {
/*  280: 439 */       DataSource source = new DataSource(location);
/*  281: 440 */       Instances result = source.getDataSet();
/*  282:     */       
/*  283: 442 */       return result;
/*  284:     */     }
/*  285:     */     
/*  286:     */     public static Instances read(InputStream stream)
/*  287:     */       throws Exception
/*  288:     */     {
/*  289: 456 */       DataSource source = new DataSource(stream);
/*  290: 457 */       Instances result = source.getDataSet();
/*  291:     */       
/*  292: 459 */       return result;
/*  293:     */     }
/*  294:     */     
/*  295:     */     public static Instances read(Loader loader)
/*  296:     */       throws Exception
/*  297:     */     {
/*  298: 473 */       DataSource source = new DataSource(loader);
/*  299: 474 */       Instances result = source.getDataSet();
/*  300:     */       
/*  301: 476 */       return result;
/*  302:     */     }
/*  303:     */     
/*  304:     */     public static void main(String[] args)
/*  305:     */       throws Exception
/*  306:     */     {
/*  307: 486 */       if (args.length != 1)
/*  308:     */       {
/*  309: 487 */         System.out.println("\nUsage: " + DataSource.class.getName() + " <file>\n");
/*  310:     */         
/*  311: 489 */         System.exit(1);
/*  312:     */       }
/*  313: 492 */       DataSource loader = new DataSource(args[0]);
/*  314:     */       
/*  315: 494 */       System.out.println("Incremental? " + loader.isIncremental());
/*  316: 495 */       System.out.println("Loader: " + loader.getLoader().getClass().getName());
/*  317: 496 */       System.out.println("Data:\n");
/*  318: 497 */       Instances structure = loader.getStructure();
/*  319: 498 */       System.out.println(structure);
/*  320: 499 */       while (loader.hasMoreElements(structure)) {
/*  321: 500 */         System.out.println(loader.nextElement(structure));
/*  322:     */       }
/*  323: 503 */       Instances inst = loader.getDataSet();
/*  324: 504 */       loader = new DataSource(inst);
/*  325: 505 */       System.out.println("\n\nProxy-Data:\n");
/*  326: 506 */       System.out.println(loader.getStructure());
/*  327: 507 */       while (loader.hasMoreElements(structure)) {
/*  328: 508 */         System.out.println(loader.nextElement(inst));
/*  329:     */       }
/*  330:     */     }
/*  331:     */     
/*  332:     */     public String getRevision()
/*  333:     */     {
/*  334: 519 */       return RevisionUtils.extract("$Revision: 10203 $");
/*  335:     */     }
/*  336:     */   }
/*  337:     */   
/*  338:     */   public static class DataSink
/*  339:     */     implements Serializable, RevisionHandler
/*  340:     */   {
/*  341:     */     private static final long serialVersionUID = -1504966891136411204L;
/*  342: 538 */     protected Saver m_Saver = null;
/*  343: 541 */     protected OutputStream m_Stream = null;
/*  344:     */     
/*  345:     */     public DataSink(String filename)
/*  346:     */       throws Exception
/*  347:     */     {
/*  348: 550 */       this.m_Stream = null;
/*  349: 552 */       if (ConverterUtils.DataSource.isArff(filename)) {
/*  350: 553 */         this.m_Saver = new ArffSaver();
/*  351:     */       } else {
/*  352: 555 */         this.m_Saver = ConverterUtils.getSaverForFile(filename);
/*  353:     */       }
/*  354: 558 */       ((AbstractFileSaver)this.m_Saver).setFile(new File(filename));
/*  355:     */     }
/*  356:     */     
/*  357:     */     public DataSink(Saver saver)
/*  358:     */     {
/*  359: 568 */       this.m_Saver = saver;
/*  360: 569 */       this.m_Stream = null;
/*  361:     */     }
/*  362:     */     
/*  363:     */     public DataSink(OutputStream stream)
/*  364:     */     {
/*  365: 580 */       this.m_Saver = null;
/*  366: 581 */       this.m_Stream = stream;
/*  367:     */     }
/*  368:     */     
/*  369:     */     public void write(Instances data)
/*  370:     */       throws Exception
/*  371:     */     {
/*  372: 593 */       if (this.m_Saver != null)
/*  373:     */       {
/*  374: 594 */         this.m_Saver.setInstances(data);
/*  375: 595 */         this.m_Saver.writeBatch();
/*  376:     */       }
/*  377:     */       else
/*  378:     */       {
/*  379: 597 */         this.m_Stream.write(data.toString().getBytes());
/*  380: 598 */         this.m_Stream.flush();
/*  381:     */       }
/*  382:     */     }
/*  383:     */     
/*  384:     */     public static void write(String filename, Instances data)
/*  385:     */       throws Exception
/*  386:     */     {
/*  387: 612 */       DataSink sink = new DataSink(filename);
/*  388: 613 */       sink.write(data);
/*  389:     */     }
/*  390:     */     
/*  391:     */     public static void write(Saver saver, Instances data)
/*  392:     */       throws Exception
/*  393:     */     {
/*  394: 626 */       DataSink sink = new DataSink(saver);
/*  395: 627 */       sink.write(data);
/*  396:     */     }
/*  397:     */     
/*  398:     */     public static void write(OutputStream stream, Instances data)
/*  399:     */       throws Exception
/*  400:     */     {
/*  401: 641 */       DataSink sink = new DataSink(stream);
/*  402: 642 */       sink.write(data);
/*  403:     */     }
/*  404:     */     
/*  405:     */     public static void main(String[] args)
/*  406:     */       throws Exception
/*  407:     */     {
/*  408: 653 */       if (args.length != 2)
/*  409:     */       {
/*  410: 654 */         System.out.println("\nUsage: " + ConverterUtils.DataSource.class.getName() + " <input-file> <output-file>\n");
/*  411:     */         
/*  412: 656 */         System.exit(1);
/*  413:     */       }
/*  414: 660 */       Instances data = ConverterUtils.DataSource.read(args[0]);
/*  415:     */       
/*  416:     */ 
/*  417: 663 */       write(args[1], data);
/*  418:     */     }
/*  419:     */     
/*  420:     */     public String getRevision()
/*  421:     */     {
/*  422: 673 */       return RevisionUtils.extract("$Revision: 10203 $");
/*  423:     */     }
/*  424:     */   }
/*  425:     */   
/*  426: 681 */   public static final String CORE_FILE_LOADERS = ArffLoader.class.getName() + "," + CSVLoader.class.getName() + "," + DatabaseConverter.class.getName() + "," + SerializedInstancesLoader.class.getName() + "," + TextDirectoryLoader.class.getName() + "," + XRFFLoader.class.getName();
/*  427: 702 */   public static final String CORE_FILE_SAVERS = ArffSaver.class.getName() + "," + CSVSaver.class.getName() + "," + DatabaseConverter.class.getName() + "," + SerializedInstancesSaver.class.getName() + "," + XRFFSaver.class.getName();
/*  428:     */   protected static Hashtable<String, String> m_FileLoaders;
/*  429:     */   protected static Hashtable<String, String> m_URLFileLoaders;
/*  430:     */   protected static Hashtable<String, String> m_FileSavers;
/*  431:     */   
/*  432:     */   static
/*  433:     */   {
/*  434: 728 */     initialize();
/*  435:     */   }
/*  436:     */   
/*  437:     */   public static void initialize()
/*  438:     */   {
/*  439:     */     try
/*  440:     */     {
/*  441: 736 */       m_FileLoaders = new Hashtable();
/*  442: 737 */       m_URLFileLoaders = new Hashtable();
/*  443: 738 */       m_FileSavers = new Hashtable();
/*  444:     */       
/*  445:     */ 
/*  446:     */ 
/*  447:     */ 
/*  448: 743 */       Properties props = GenericPropertiesCreator.getGlobalOutputProperties();
/*  449: 744 */       if (props == null)
/*  450:     */       {
/*  451: 745 */         GenericPropertiesCreator creator = new GenericPropertiesCreator();
/*  452:     */         
/*  453: 747 */         creator.execute(false);
/*  454: 748 */         props = creator.getOutputProperties();
/*  455:     */       }
/*  456: 752 */       m_FileLoaders = getFileConverters(props.getProperty(Loader.class.getName(), CORE_FILE_LOADERS), new String[] { FileSourcedConverter.class.getName() });
/*  457:     */       
/*  458:     */ 
/*  459:     */ 
/*  460:     */ 
/*  461: 757 */       m_URLFileLoaders = getFileConverters(props.getProperty(Loader.class.getName(), CORE_FILE_LOADERS), new String[] { FileSourcedConverter.class.getName(), URLSourcedLoader.class.getName() });
/*  462:     */       
/*  463:     */ 
/*  464:     */ 
/*  465:     */ 
/*  466:     */ 
/*  467: 763 */       m_FileSavers = getFileConverters(props.getProperty(Saver.class.getName(), CORE_FILE_SAVERS), new String[] { FileSourcedConverter.class.getName() });
/*  468:     */     }
/*  469:     */     catch (Exception e)
/*  470:     */     {
/*  471:     */       Vector<String> classnames;
/*  472:     */       Vector<String> classnames;
/*  473: 767 */       e.printStackTrace();
/*  474:     */     }
/*  475:     */     finally
/*  476:     */     {
/*  477:     */       Vector<String> classnames;
/*  478:     */       Vector<String> classnames;
/*  479:     */       Vector<String> classnames;
/*  480: 771 */       if (m_FileLoaders.size() == 0)
/*  481:     */       {
/*  482: 772 */         classnames = GenericObjectEditor.getClassnames(AbstractFileLoader.class.getName());
/*  483: 774 */         if (classnames.size() > 0) {
/*  484: 775 */           m_FileLoaders = getFileConverters(classnames, new String[] { FileSourcedConverter.class.getName() });
/*  485:     */         } else {
/*  486: 778 */           m_FileLoaders = getFileConverters(CORE_FILE_LOADERS, new String[] { FileSourcedConverter.class.getName() });
/*  487:     */         }
/*  488:     */       }
/*  489: 784 */       if (m_URLFileLoaders.size() == 0)
/*  490:     */       {
/*  491: 785 */         Vector<String> classnames = GenericObjectEditor.getClassnames(AbstractFileLoader.class.getName());
/*  492: 787 */         if (classnames.size() > 0) {
/*  493: 788 */           m_URLFileLoaders = getFileConverters(classnames, new String[] { FileSourcedConverter.class.getName(), URLSourcedLoader.class.getName() });
/*  494:     */         } else {
/*  495: 792 */           m_URLFileLoaders = getFileConverters(CORE_FILE_LOADERS, new String[] { FileSourcedConverter.class.getName(), URLSourcedLoader.class.getName() });
/*  496:     */         }
/*  497:     */       }
/*  498: 799 */       if (m_FileSavers.size() == 0)
/*  499:     */       {
/*  500: 800 */         Vector<String> classnames = GenericObjectEditor.getClassnames(AbstractFileSaver.class.getName());
/*  501: 802 */         if (classnames.size() > 0) {
/*  502: 803 */           m_FileSavers = getFileConverters(classnames, new String[] { FileSourcedConverter.class.getName() });
/*  503:     */         } else {
/*  504: 806 */           m_FileSavers = getFileConverters(CORE_FILE_SAVERS, new String[] { FileSourcedConverter.class.getName() });
/*  505:     */         }
/*  506:     */       }
/*  507:     */     }
/*  508: 812 */     ConverterFileChooser.initDefaultFilters();
/*  509:     */   }
/*  510:     */   
/*  511:     */   protected static Hashtable<String, String> getFileConverters(String classnames, String[] intf)
/*  512:     */   {
/*  513: 830 */     Vector<String> list = new Vector();
/*  514: 831 */     String[] names = classnames.split(",");
/*  515: 832 */     for (int i = 0; i < names.length; i++) {
/*  516: 833 */       list.add(names[i]);
/*  517:     */     }
/*  518: 836 */     return getFileConverters(list, intf);
/*  519:     */   }
/*  520:     */   
/*  521:     */   protected static Hashtable<String, String> getFileConverters(Vector<String> classnames, String[] intf)
/*  522:     */   {
/*  523: 858 */     Hashtable<String, String> result = new Hashtable();
/*  524: 860 */     for (int i = 0; i < classnames.size(); i++)
/*  525:     */     {
/*  526: 861 */       String classname = (String)classnames.get(i);
/*  527: 864 */       for (int n = 0; n < intf.length; n++) {
/*  528: 865 */         if (ClassDiscovery.hasInterface(intf[n], classname)) {}
/*  529:     */       }
/*  530:     */       FileSourcedConverter converter;
/*  531:     */       String[] ext;
/*  532:     */       try
/*  533:     */       {
/*  534: 872 */         cls = Class.forName(classname);
/*  535: 873 */         converter = (FileSourcedConverter)cls.newInstance();
/*  536: 874 */         ext = converter.getFileExtensions();
/*  537:     */       }
/*  538:     */       catch (Exception e)
/*  539:     */       {
/*  540: 876 */         Class<?> cls = null;
/*  541: 877 */         converter = null;
/*  542: 878 */         ext = new String[0];
/*  543:     */       }
/*  544: 881 */       if (converter != null) {
/*  545: 885 */         for (n = 0; n < ext.length; n++) {
/*  546: 886 */           result.put(ext[n], classname);
/*  547:     */         }
/*  548:     */       }
/*  549:     */     }
/*  550: 890 */     return result;
/*  551:     */   }
/*  552:     */   
/*  553:     */   public static void getFirstToken(StreamTokenizer tokenizer)
/*  554:     */     throws IOException
/*  555:     */   {
/*  556: 901 */     StreamTokenizerUtils.getFirstToken(tokenizer);
/*  557:     */   }
/*  558:     */   
/*  559:     */   public static void getToken(StreamTokenizer tokenizer)
/*  560:     */     throws IOException
/*  561:     */   {
/*  562: 911 */     StreamTokenizerUtils.getToken(tokenizer);
/*  563:     */   }
/*  564:     */   
/*  565:     */   public static void errms(StreamTokenizer tokenizer, String theMsg)
/*  566:     */     throws IOException
/*  567:     */   {
/*  568: 924 */     throw new IOException(theMsg + ", read " + tokenizer.toString());
/*  569:     */   }
/*  570:     */   
/*  571:     */   protected static Vector<String> getConverters(Hashtable<String, String> ht)
/*  572:     */   {
/*  573: 939 */     Vector<String> result = new Vector();
/*  574:     */     
/*  575:     */ 
/*  576: 942 */     Enumeration<String> enm = ht.elements();
/*  577: 943 */     while (enm.hasMoreElements())
/*  578:     */     {
/*  579: 944 */       String converter = (String)enm.nextElement();
/*  580: 945 */       if (!result.contains(converter)) {
/*  581: 946 */         result.add(converter);
/*  582:     */       }
/*  583:     */     }
/*  584: 951 */     Collections.sort(result);
/*  585:     */     
/*  586: 953 */     return result;
/*  587:     */   }
/*  588:     */   
/*  589:     */   protected static Object getConverterForFile(String filename, Hashtable<String, String> ht)
/*  590:     */   {
/*  591: 970 */     Object result = null;
/*  592:     */     
/*  593: 972 */     int index = filename.lastIndexOf('.');
/*  594: 973 */     if (index > -1)
/*  595:     */     {
/*  596: 974 */       String extension = filename.substring(index).toLowerCase();
/*  597: 975 */       result = getConverterForExtension(extension, ht);
/*  598: 977 */       if ((extension.equals(".gz")) && (result == null))
/*  599:     */       {
/*  600: 978 */         index = filename.lastIndexOf('.', index - 1);
/*  601: 979 */         extension = filename.substring(index).toLowerCase();
/*  602: 980 */         result = getConverterForExtension(extension, ht);
/*  603:     */       }
/*  604:     */     }
/*  605: 984 */     return result;
/*  606:     */   }
/*  607:     */   
/*  608:     */   protected static Object getConverterForExtension(String extension, Hashtable<String, String> ht)
/*  609:     */   {
/*  610:1000 */     Object result = null;
/*  611:1001 */     String classname = (String)ht.get(extension);
/*  612:1002 */     if (classname != null) {
/*  613:     */       try
/*  614:     */       {
/*  615:1004 */         result = Class.forName(classname).newInstance();
/*  616:     */       }
/*  617:     */       catch (Exception e)
/*  618:     */       {
/*  619:1006 */         result = null;
/*  620:1007 */         e.printStackTrace();
/*  621:     */       }
/*  622:     */     }
/*  623:1011 */     return result;
/*  624:     */   }
/*  625:     */   
/*  626:     */   public static boolean isCoreFileLoader(String classname)
/*  627:     */   {
/*  628:1025 */     String[] classnames = CORE_FILE_LOADERS.split(",");
/*  629:1026 */     boolean result = Arrays.binarySearch(classnames, classname) >= 0;
/*  630:     */     
/*  631:1028 */     return result;
/*  632:     */   }
/*  633:     */   
/*  634:     */   public static Vector<String> getFileLoaders()
/*  635:     */   {
/*  636:1037 */     return getConverters(m_FileLoaders);
/*  637:     */   }
/*  638:     */   
/*  639:     */   public static AbstractFileLoader getLoaderForFile(String filename)
/*  640:     */   {
/*  641:1048 */     return (AbstractFileLoader)getConverterForFile(filename, m_FileLoaders);
/*  642:     */   }
/*  643:     */   
/*  644:     */   public static AbstractFileLoader getLoaderForFile(File file)
/*  645:     */   {
/*  646:1059 */     return getLoaderForFile(file.getAbsolutePath());
/*  647:     */   }
/*  648:     */   
/*  649:     */   public static AbstractFileLoader getLoaderForExtension(String extension)
/*  650:     */   {
/*  651:1070 */     return (AbstractFileLoader)getConverterForExtension(extension, m_FileLoaders);
/*  652:     */   }
/*  653:     */   
/*  654:     */   public static Vector<String> getURLFileLoaders()
/*  655:     */   {
/*  656:1080 */     return getConverters(m_URLFileLoaders);
/*  657:     */   }
/*  658:     */   
/*  659:     */   public static AbstractFileLoader getURLLoaderForFile(String filename)
/*  660:     */   {
/*  661:1091 */     return (AbstractFileLoader)getConverterForFile(filename, m_URLFileLoaders);
/*  662:     */   }
/*  663:     */   
/*  664:     */   public static AbstractFileLoader getURLLoaderForFile(File file)
/*  665:     */   {
/*  666:1102 */     return getURLLoaderForFile(file.getAbsolutePath());
/*  667:     */   }
/*  668:     */   
/*  669:     */   public static AbstractFileLoader getURLLoaderForExtension(String extension)
/*  670:     */   {
/*  671:1113 */     return (AbstractFileLoader)getConverterForExtension(extension, m_URLFileLoaders);
/*  672:     */   }
/*  673:     */   
/*  674:     */   public static boolean isCoreFileSaver(String classname)
/*  675:     */   {
/*  676:1128 */     String[] classnames = CORE_FILE_SAVERS.split(",");
/*  677:1129 */     boolean result = Arrays.binarySearch(classnames, classname) >= 0;
/*  678:     */     
/*  679:1131 */     return result;
/*  680:     */   }
/*  681:     */   
/*  682:     */   public static Vector<String> getFileSavers()
/*  683:     */   {
/*  684:1140 */     return getConverters(m_FileSavers);
/*  685:     */   }
/*  686:     */   
/*  687:     */   public static AbstractFileSaver getSaverForFile(String filename)
/*  688:     */   {
/*  689:1151 */     return (AbstractFileSaver)getConverterForFile(filename, m_FileSavers);
/*  690:     */   }
/*  691:     */   
/*  692:     */   public static AbstractFileSaver getSaverForFile(File file)
/*  693:     */   {
/*  694:1162 */     return getSaverForFile(file.getAbsolutePath());
/*  695:     */   }
/*  696:     */   
/*  697:     */   public static AbstractFileSaver getSaverForExtension(String extension)
/*  698:     */   {
/*  699:1173 */     return (AbstractFileSaver)getConverterForExtension(extension, m_FileSavers);
/*  700:     */   }
/*  701:     */   
/*  702:     */   public String getRevision()
/*  703:     */   {
/*  704:1183 */     return RevisionUtils.extract("$Revision: 10203 $");
/*  705:     */   }
/*  706:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.ConverterUtils
 * JD-Core Version:    0.7.0.1
 */