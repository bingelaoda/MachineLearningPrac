/*    1:     */ package weka.core.converters;
/*    2:     */ 
/*    3:     */ import java.io.File;
/*    4:     */ import java.io.IOException;
/*    5:     */ import java.io.PrintStream;
/*    6:     */ import java.sql.DatabaseMetaData;
/*    7:     */ import java.sql.Date;
/*    8:     */ import java.sql.ResultSet;
/*    9:     */ import java.sql.ResultSetMetaData;
/*   10:     */ import java.sql.SQLException;
/*   11:     */ import java.sql.Time;
/*   12:     */ import java.util.ArrayList;
/*   13:     */ import java.util.Enumeration;
/*   14:     */ import java.util.Hashtable;
/*   15:     */ import java.util.Properties;
/*   16:     */ import java.util.StringTokenizer;
/*   17:     */ import java.util.Vector;
/*   18:     */ import weka.core.Attribute;
/*   19:     */ import weka.core.DenseInstance;
/*   20:     */ import weka.core.Environment;
/*   21:     */ import weka.core.EnvironmentHandler;
/*   22:     */ import weka.core.Instance;
/*   23:     */ import weka.core.Instances;
/*   24:     */ import weka.core.Option;
/*   25:     */ import weka.core.OptionHandler;
/*   26:     */ import weka.core.OptionMetadata;
/*   27:     */ import weka.core.RevisionUtils;
/*   28:     */ import weka.core.SparseInstance;
/*   29:     */ import weka.core.Utils;
/*   30:     */ import weka.experiment.InstanceQuery;
/*   31:     */ import weka.gui.FilePropertyMetadata;
/*   32:     */ import weka.gui.PasswordProperty;
/*   33:     */ 
/*   34:     */ public class DatabaseLoader
/*   35:     */   extends AbstractLoader
/*   36:     */   implements BatchConverter, IncrementalConverter, DatabaseConverter, OptionHandler, EnvironmentHandler
/*   37:     */ {
/*   38:     */   static final long serialVersionUID = -7936159015338318659L;
/*   39:     */   protected Instances m_structure;
/*   40:     */   protected Instances m_datasetPseudoInc;
/*   41:     */   protected Instances m_oldStructure;
/*   42:     */   protected DatabaseConnection m_DataBaseConnection;
/*   43: 164 */   protected String m_query = "Select * from Results0";
/*   44:     */   protected boolean m_pseudoIncremental;
/*   45:     */   protected boolean m_checkForTable;
/*   46:     */   protected int m_nominalToStringLimit;
/*   47:     */   protected int m_rowCount;
/*   48:     */   protected int m_counter;
/*   49:     */   protected int m_choice;
/*   50:     */   protected boolean m_firstTime;
/*   51:     */   protected boolean m_inc;
/*   52:     */   protected ArrayList<String> m_orderBy;
/*   53:     */   protected Hashtable<String, Double>[] m_nominalIndexes;
/*   54:     */   protected ArrayList<String>[] m_nominalStrings;
/*   55:     */   protected String m_idColumn;
/*   56: 228 */   protected String m_URL = null;
/*   57: 231 */   protected String m_User = "";
/*   58: 234 */   protected String m_Password = "";
/*   59: 237 */   protected String m_Keys = "";
/*   60: 240 */   protected File m_CustomPropsFile = new File("${user.home}");
/*   61: 243 */   protected boolean m_CreateSparseData = false;
/*   62:     */   protected transient Environment m_env;
/*   63:     */   
/*   64:     */   public DatabaseLoader()
/*   65:     */     throws Exception
/*   66:     */   {
/*   67: 255 */     resetOptions();
/*   68:     */   }
/*   69:     */   
/*   70:     */   public String globalInfo()
/*   71:     */   {
/*   72: 265 */     return "Reads Instances from a Database. Can read a database in batch or incremental mode.\nIn inremental mode MySQL and HSQLDB are supported.\nFor all other DBMS set a pseudoincremental mode is used:\nIn pseudo incremental mode the instances are read into main memory all at once and then incrementally provided to the user.\nFor incremental loading the rows in the database table have to be ordered uniquely.\nThe reason for this is that every time only a single row is fetched by extending the user query by a LIMIT clause.\nIf this extension is impossible instances will be loaded pseudoincrementally. To ensure that every row is fetched exaclty once, they have to ordered.\nTherefore a (primary) key is necessary.This approach is chosen, instead of using JDBC driver facilities, because the latter one differ betweeen different drivers.\nIf you use the DatabaseSaver and save instances by generating automatically a primary key (its name is defined in DtabaseUtils), this primary key will be used for ordering but will not be part of the output. The user defined SQL query to extract the instances should not contain LIMIT and ORDER BY clauses (see -Q option).\nIn addition, for incremental loading,  you can define in the DatabaseUtils file how many distinct values a nominal attribute is allowed to have. If this number is exceeded, the column will become a string attribute.\nIn batch mode no string attributes will be created.";
/*   73:     */   }
/*   74:     */   
/*   75:     */   public void setEnvironment(Environment env)
/*   76:     */   {
/*   77: 287 */     this.m_env = env;
/*   78:     */     try
/*   79:     */     {
/*   80: 291 */       this.m_DataBaseConnection = newDatabaseConnection();
/*   81: 292 */       setUrl(this.m_URL);
/*   82: 293 */       setUser(this.m_User);
/*   83: 294 */       setPassword(this.m_Password);
/*   84:     */     }
/*   85:     */     catch (Exception ex) {}
/*   86:     */   }
/*   87:     */   
/*   88:     */   private void checkEnv()
/*   89:     */   {
/*   90: 301 */     if (this.m_env == null) {
/*   91: 302 */       this.m_env = Environment.getSystemWide();
/*   92:     */     }
/*   93:     */   }
/*   94:     */   
/*   95:     */   protected DatabaseConnection newDatabaseConnection()
/*   96:     */     throws Exception
/*   97:     */   {
/*   98: 316 */     checkEnv();
/*   99:     */     DatabaseConnection result;
/*  100:     */     DatabaseConnection result;
/*  101: 318 */     if (this.m_CustomPropsFile != null)
/*  102:     */     {
/*  103: 319 */       File pFile = new File(this.m_CustomPropsFile.getPath());
/*  104: 320 */       String pPath = this.m_CustomPropsFile.getPath();
/*  105:     */       try
/*  106:     */       {
/*  107: 322 */         pPath = this.m_env.substitute(pPath);
/*  108: 323 */         pFile = new File(pPath);
/*  109:     */       }
/*  110:     */       catch (Exception ex) {}
/*  111: 326 */       result = new DatabaseConnection(pFile);
/*  112:     */     }
/*  113:     */     else
/*  114:     */     {
/*  115: 328 */       result = new DatabaseConnection();
/*  116:     */     }
/*  117: 331 */     this.m_pseudoIncremental = false;
/*  118: 332 */     this.m_checkForTable = true;
/*  119: 333 */     String props = result.getProperties().getProperty("nominalToStringLimit");
/*  120: 334 */     this.m_nominalToStringLimit = Integer.parseInt(props);
/*  121: 335 */     this.m_idColumn = result.getProperties().getProperty("idColumn");
/*  122: 336 */     if (result.getProperties().getProperty("checkForTable", "").equalsIgnoreCase("FALSE")) {
/*  123: 338 */       this.m_checkForTable = false;
/*  124:     */     }
/*  125: 341 */     return result;
/*  126:     */   }
/*  127:     */   
/*  128:     */   public void resetOptions()
/*  129:     */   {
/*  130: 349 */     resetStructure();
/*  131:     */     try
/*  132:     */     {
/*  133: 351 */       if ((this.m_DataBaseConnection != null) && (this.m_DataBaseConnection.isConnected())) {
/*  134: 352 */         this.m_DataBaseConnection.disconnectFromDatabase();
/*  135:     */       }
/*  136: 354 */       this.m_DataBaseConnection = newDatabaseConnection();
/*  137:     */     }
/*  138:     */     catch (Exception ex)
/*  139:     */     {
/*  140: 356 */       printException(ex);
/*  141:     */     }
/*  142: 359 */     this.m_URL = this.m_DataBaseConnection.getDatabaseURL();
/*  143: 360 */     if (this.m_URL == null) {
/*  144: 361 */       this.m_URL = "none set!";
/*  145:     */     }
/*  146: 363 */     this.m_User = this.m_DataBaseConnection.getUsername();
/*  147: 364 */     if (this.m_User == null) {
/*  148: 365 */       this.m_User = "";
/*  149:     */     }
/*  150: 367 */     this.m_Password = this.m_DataBaseConnection.getPassword();
/*  151: 368 */     if (this.m_Password == null) {
/*  152: 369 */       this.m_Password = "";
/*  153:     */     }
/*  154: 371 */     this.m_orderBy = new ArrayList();
/*  155:     */   }
/*  156:     */   
/*  157:     */   public void reset()
/*  158:     */   {
/*  159: 382 */     resetStructure();
/*  160:     */     try
/*  161:     */     {
/*  162: 384 */       if ((this.m_DataBaseConnection != null) && (this.m_DataBaseConnection.isConnected())) {
/*  163: 385 */         this.m_DataBaseConnection.disconnectFromDatabase();
/*  164:     */       }
/*  165: 387 */       this.m_DataBaseConnection = newDatabaseConnection();
/*  166:     */     }
/*  167:     */     catch (Exception ex)
/*  168:     */     {
/*  169: 389 */       printException(ex);
/*  170:     */     }
/*  171: 393 */     if (this.m_URL != null) {
/*  172: 394 */       setUrl(this.m_URL);
/*  173:     */     }
/*  174: 397 */     if (this.m_User != null) {
/*  175: 398 */       setUser(this.m_User);
/*  176:     */     }
/*  177: 401 */     if (this.m_Password != null) {
/*  178: 402 */       setPassword(this.m_Password);
/*  179:     */     }
/*  180: 405 */     this.m_orderBy = new ArrayList();
/*  181: 407 */     if (this.m_Keys != null)
/*  182:     */     {
/*  183: 408 */       String k = this.m_Keys;
/*  184:     */       try
/*  185:     */       {
/*  186: 410 */         k = this.m_env.substitute(k);
/*  187:     */       }
/*  188:     */       catch (Exception ex) {}
/*  189: 413 */       setKeys(k);
/*  190:     */     }
/*  191: 416 */     this.m_inc = false;
/*  192:     */   }
/*  193:     */   
/*  194:     */   public void resetStructure()
/*  195:     */   {
/*  196: 424 */     this.m_structure = null;
/*  197: 425 */     this.m_datasetPseudoInc = null;
/*  198: 426 */     this.m_oldStructure = null;
/*  199: 427 */     this.m_rowCount = 0;
/*  200: 428 */     this.m_counter = 0;
/*  201: 429 */     this.m_choice = 0;
/*  202: 430 */     this.m_firstTime = true;
/*  203: 431 */     setRetrieval(0);
/*  204:     */   }
/*  205:     */   
/*  206:     */   public void setQuery(String q)
/*  207:     */   {
/*  208: 440 */     q = q.replaceAll("[fF][rR][oO][mM]", "FROM");
/*  209: 441 */     q = q.replaceFirst("[sS][eE][lL][eE][cC][tT]", "SELECT");
/*  210: 442 */     this.m_query = q;
/*  211:     */   }
/*  212:     */   
/*  213:     */   @OptionMetadata(displayName="Query", description="The query to execute", displayOrder=4)
/*  214:     */   public String getQuery()
/*  215:     */   {
/*  216: 453 */     return this.m_query;
/*  217:     */   }
/*  218:     */   
/*  219:     */   public String queryTipText()
/*  220:     */   {
/*  221: 463 */     return "The query that should load the instances.\n The query has to be of the form SELECT <column-list>|* FROM <table> [WHERE <conditions>]";
/*  222:     */   }
/*  223:     */   
/*  224:     */   public void setKeys(String keys)
/*  225:     */   {
/*  226: 474 */     this.m_Keys = keys;
/*  227: 475 */     this.m_orderBy.clear();
/*  228: 476 */     StringTokenizer st = new StringTokenizer(keys, ",");
/*  229: 477 */     while (st.hasMoreTokens())
/*  230:     */     {
/*  231: 478 */       String column = st.nextToken();
/*  232: 479 */       column = column.replaceAll(" ", "");
/*  233: 480 */       this.m_orderBy.add(column);
/*  234:     */     }
/*  235:     */   }
/*  236:     */   
/*  237:     */   @OptionMetadata(displayName="Key columns", description="Specific key columns to use if a primary key cannot be automatically detected. Used in incremental loading.", displayOrder=5)
/*  238:     */   public String getKeys()
/*  239:     */   {
/*  240: 497 */     StringBuffer key = new StringBuffer();
/*  241: 498 */     for (int i = 0; i < this.m_orderBy.size(); i++)
/*  242:     */     {
/*  243: 499 */       key.append((String)this.m_orderBy.get(i));
/*  244: 500 */       if (i != this.m_orderBy.size() - 1) {
/*  245: 501 */         key.append(", ");
/*  246:     */       }
/*  247:     */     }
/*  248: 504 */     return key.toString();
/*  249:     */   }
/*  250:     */   
/*  251:     */   public String keysTipText()
/*  252:     */   {
/*  253: 514 */     return "For incremental loading a unique identiefer has to be specified.\nIf the query includes all columns of a table (SELECT *...) a primary key\ncan be detected automatically depending on the JDBC driver. If that is not possible\nspecify the key columns here in a comma separated list.";
/*  254:     */   }
/*  255:     */   
/*  256:     */   public void setCustomPropsFile(File value)
/*  257:     */   {
/*  258: 527 */     this.m_CustomPropsFile = value;
/*  259:     */   }
/*  260:     */   
/*  261:     */   @OptionMetadata(displayName="DB config file", description="The custom properties that the user can use to override the default ones.", displayOrder=8)
/*  262:     */   @FilePropertyMetadata(fileChooserDialogType=0, directoriesOnly=false)
/*  263:     */   public File getCustomPropsFile()
/*  264:     */   {
/*  265: 543 */     return this.m_CustomPropsFile;
/*  266:     */   }
/*  267:     */   
/*  268:     */   public String customPropsFileTipText()
/*  269:     */   {
/*  270: 553 */     return "The custom properties that the user can use to override the default ones.";
/*  271:     */   }
/*  272:     */   
/*  273:     */   public void setUrl(String url)
/*  274:     */   {
/*  275: 563 */     checkEnv();
/*  276:     */     
/*  277: 565 */     this.m_URL = url;
/*  278: 566 */     String dbU = this.m_URL;
/*  279:     */     try
/*  280:     */     {
/*  281: 568 */       dbU = this.m_env.substitute(dbU);
/*  282:     */     }
/*  283:     */     catch (Exception ex) {}
/*  284: 572 */     this.m_DataBaseConnection.setDatabaseURL(dbU);
/*  285:     */   }
/*  286:     */   
/*  287:     */   @OptionMetadata(displayName="Database URL", description="The URL of the database", displayOrder=1)
/*  288:     */   public String getUrl()
/*  289:     */   {
/*  290: 586 */     return this.m_URL;
/*  291:     */   }
/*  292:     */   
/*  293:     */   public String urlTipText()
/*  294:     */   {
/*  295: 596 */     return "The URL of the database";
/*  296:     */   }
/*  297:     */   
/*  298:     */   public void setUser(String user)
/*  299:     */   {
/*  300: 606 */     checkEnv();
/*  301:     */     
/*  302: 608 */     this.m_User = user;
/*  303: 609 */     String userCopy = user;
/*  304:     */     try
/*  305:     */     {
/*  306: 611 */       userCopy = this.m_env.substitute(userCopy);
/*  307:     */     }
/*  308:     */     catch (Exception ex) {}
/*  309: 614 */     this.m_DataBaseConnection.setUsername(userCopy);
/*  310:     */   }
/*  311:     */   
/*  312:     */   @OptionMetadata(displayName="Username", description="The user name for the database", displayOrder=2)
/*  313:     */   public String getUser()
/*  314:     */   {
/*  315: 628 */     return this.m_User;
/*  316:     */   }
/*  317:     */   
/*  318:     */   public String userTipText()
/*  319:     */   {
/*  320: 638 */     return "The user name for the database";
/*  321:     */   }
/*  322:     */   
/*  323:     */   public void setPassword(String password)
/*  324:     */   {
/*  325: 648 */     checkEnv();
/*  326:     */     
/*  327: 650 */     this.m_Password = password;
/*  328: 651 */     String passCopy = password;
/*  329:     */     try
/*  330:     */     {
/*  331: 653 */       passCopy = this.m_env.substitute(passCopy);
/*  332:     */     }
/*  333:     */     catch (Exception ex) {}
/*  334: 656 */     this.m_DataBaseConnection.setPassword(password);
/*  335:     */   }
/*  336:     */   
/*  337:     */   @OptionMetadata(displayName="Password", description="The database password", displayOrder=3)
/*  338:     */   @PasswordProperty
/*  339:     */   public String getPassword()
/*  340:     */   {
/*  341: 669 */     return this.m_Password;
/*  342:     */   }
/*  343:     */   
/*  344:     */   public String passwordTipText()
/*  345:     */   {
/*  346: 679 */     return "The database password";
/*  347:     */   }
/*  348:     */   
/*  349:     */   public String sparseDataTipText()
/*  350:     */   {
/*  351: 689 */     return "Encode data as sparse instances.";
/*  352:     */   }
/*  353:     */   
/*  354:     */   public void setSparseData(boolean s)
/*  355:     */   {
/*  356: 698 */     this.m_CreateSparseData = s;
/*  357:     */   }
/*  358:     */   
/*  359:     */   @OptionMetadata(displayName="Create sparse instances", description="Return sparse rather than normal instances", displayOrder=6)
/*  360:     */   public boolean getSparseData()
/*  361:     */   {
/*  362: 709 */     return this.m_CreateSparseData;
/*  363:     */   }
/*  364:     */   
/*  365:     */   public void setSource(String url, String userName, String password)
/*  366:     */   {
/*  367:     */     try
/*  368:     */     {
/*  369: 722 */       this.m_DataBaseConnection = newDatabaseConnection();
/*  370: 723 */       setUrl(url);
/*  371: 724 */       setUser(userName);
/*  372: 725 */       setPassword(password);
/*  373:     */     }
/*  374:     */     catch (Exception ex)
/*  375:     */     {
/*  376: 727 */       printException(ex);
/*  377:     */     }
/*  378:     */   }
/*  379:     */   
/*  380:     */   public void setSource(String url)
/*  381:     */   {
/*  382:     */     try
/*  383:     */     {
/*  384: 739 */       this.m_DataBaseConnection = newDatabaseConnection();
/*  385: 740 */       setUrl(url);
/*  386: 741 */       this.m_User = this.m_DataBaseConnection.getUsername();
/*  387: 742 */       this.m_Password = this.m_DataBaseConnection.getPassword();
/*  388:     */     }
/*  389:     */     catch (Exception ex)
/*  390:     */     {
/*  391: 744 */       printException(ex);
/*  392:     */     }
/*  393:     */   }
/*  394:     */   
/*  395:     */   public void setSource()
/*  396:     */     throws Exception
/*  397:     */   {
/*  398: 755 */     this.m_DataBaseConnection = newDatabaseConnection();
/*  399: 756 */     this.m_URL = this.m_DataBaseConnection.getDatabaseURL();
/*  400: 757 */     this.m_User = this.m_DataBaseConnection.getUsername();
/*  401: 758 */     this.m_Password = this.m_DataBaseConnection.getPassword();
/*  402:     */   }
/*  403:     */   
/*  404:     */   public void connectToDatabase()
/*  405:     */   {
/*  406:     */     try
/*  407:     */     {
/*  408: 767 */       if (!this.m_DataBaseConnection.isConnected()) {
/*  409: 768 */         this.m_DataBaseConnection.connectToDatabase();
/*  410:     */       }
/*  411:     */     }
/*  412:     */     catch (Exception ex)
/*  413:     */     {
/*  414: 771 */       printException(ex);
/*  415:     */     }
/*  416:     */   }
/*  417:     */   
/*  418:     */   private String endOfQuery(boolean onlyTableName)
/*  419:     */   {
/*  420: 787 */     int beginIndex = this.m_query.indexOf("FROM ") + 5;
/*  421: 788 */     while (this.m_query.charAt(beginIndex) == ' ') {
/*  422: 789 */       beginIndex++;
/*  423:     */     }
/*  424: 791 */     int endIndex = this.m_query.indexOf(" ", beginIndex);
/*  425:     */     String table;
/*  426:     */     String table;
/*  427: 792 */     if ((endIndex != -1) && (onlyTableName)) {
/*  428: 793 */       table = this.m_query.substring(beginIndex, endIndex);
/*  429:     */     } else {
/*  430: 795 */       table = this.m_query.substring(beginIndex);
/*  431:     */     }
/*  432: 797 */     if (this.m_DataBaseConnection.getUpperCase()) {
/*  433: 798 */       table = table.toUpperCase();
/*  434:     */     }
/*  435: 800 */     return table;
/*  436:     */   }
/*  437:     */   
/*  438:     */   private boolean checkForKey()
/*  439:     */     throws Exception
/*  440:     */   {
/*  441: 814 */     String query = this.m_query;
/*  442:     */     
/*  443: 816 */     query = query.replaceAll(" +", " ");
/*  444: 818 */     if (!query.startsWith("SELECT *")) {
/*  445: 819 */       return false;
/*  446:     */     }
/*  447: 821 */     this.m_orderBy.clear();
/*  448: 822 */     if (!this.m_DataBaseConnection.isConnected()) {
/*  449: 823 */       this.m_DataBaseConnection.connectToDatabase();
/*  450:     */     }
/*  451: 825 */     DatabaseMetaData dmd = this.m_DataBaseConnection.getMetaData();
/*  452: 826 */     String table = endOfQuery(true);
/*  453:     */     
/*  454:     */ 
/*  455: 829 */     ResultSet rs = dmd.getPrimaryKeys(null, null, table);
/*  456: 830 */     while (rs.next()) {
/*  457: 831 */       this.m_orderBy.add(rs.getString(4));
/*  458:     */     }
/*  459: 833 */     rs.close();
/*  460: 834 */     if (this.m_orderBy.size() != 0) {
/*  461: 835 */       return true;
/*  462:     */     }
/*  463: 838 */     rs = dmd.getBestRowIdentifier(null, null, table, 2, false);
/*  464:     */     
/*  465:     */ 
/*  466: 841 */     ResultSetMetaData rmd = rs.getMetaData();
/*  467: 842 */     int help = 0;
/*  468: 843 */     while (rs.next())
/*  469:     */     {
/*  470: 844 */       this.m_orderBy.add(rs.getString(2));
/*  471: 845 */       help++;
/*  472:     */     }
/*  473: 847 */     rs.close();
/*  474: 848 */     if (help == rmd.getColumnCount()) {
/*  475: 849 */       this.m_orderBy.clear();
/*  476:     */     }
/*  477: 851 */     if (this.m_orderBy.size() != 0) {
/*  478: 852 */       return true;
/*  479:     */     }
/*  480: 855 */     return false;
/*  481:     */   }
/*  482:     */   
/*  483:     */   private void stringToNominal(ResultSet rs, int i)
/*  484:     */     throws Exception
/*  485:     */   {
/*  486: 868 */     while (rs.next())
/*  487:     */     {
/*  488: 869 */       String str = rs.getString(1);
/*  489: 870 */       if (!rs.wasNull())
/*  490:     */       {
/*  491: 871 */         Double index = (Double)this.m_nominalIndexes[(i - 1)].get(str);
/*  492: 872 */         if (index == null)
/*  493:     */         {
/*  494: 873 */           index = new Double(this.m_nominalStrings[(i - 1)].size());
/*  495: 874 */           this.m_nominalIndexes[(i - 1)].put(str, index);
/*  496: 875 */           this.m_nominalStrings[(i - 1)].add(str);
/*  497:     */         }
/*  498:     */       }
/*  499:     */     }
/*  500:     */   }
/*  501:     */   
/*  502:     */   private String limitQuery(String query, int offset, int choice)
/*  503:     */   {
/*  504: 894 */     StringBuffer order = new StringBuffer();
/*  505: 895 */     String orderByString = "";
/*  506: 897 */     if (this.m_orderBy.size() != 0)
/*  507:     */     {
/*  508: 898 */       order.append(" ORDER BY ");
/*  509: 899 */       for (int i = 0; i < this.m_orderBy.size() - 1; i++)
/*  510:     */       {
/*  511: 900 */         if (this.m_DataBaseConnection.getUpperCase()) {
/*  512: 901 */           order.append(((String)this.m_orderBy.get(i)).toUpperCase());
/*  513:     */         } else {
/*  514: 903 */           order.append((String)this.m_orderBy.get(i));
/*  515:     */         }
/*  516: 905 */         order.append(", ");
/*  517:     */       }
/*  518: 907 */       if (this.m_DataBaseConnection.getUpperCase()) {
/*  519: 908 */         order.append(((String)this.m_orderBy.get(this.m_orderBy.size() - 1)).toUpperCase());
/*  520:     */       } else {
/*  521: 910 */         order.append((String)this.m_orderBy.get(this.m_orderBy.size() - 1));
/*  522:     */       }
/*  523: 912 */       orderByString = order.toString();
/*  524:     */     }
/*  525: 914 */     if (choice == 0)
/*  526:     */     {
/*  527: 915 */       String limitedQuery = query.replaceFirst("SELECT", "SELECT LIMIT " + offset + " 1");
/*  528:     */       
/*  529: 917 */       limitedQuery = limitedQuery.concat(orderByString);
/*  530: 918 */       return limitedQuery;
/*  531:     */     }
/*  532: 920 */     if (choice == 1)
/*  533:     */     {
/*  534: 921 */       String limitedQuery = query.concat(orderByString + " LIMIT 1 OFFSET " + offset);
/*  535: 922 */       return limitedQuery;
/*  536:     */     }
/*  537: 924 */     String limitedQuery = query.concat(orderByString + " LIMIT " + offset + ", 1");
/*  538:     */     
/*  539: 926 */     return limitedQuery;
/*  540:     */   }
/*  541:     */   
/*  542:     */   private int getRowCount()
/*  543:     */     throws Exception
/*  544:     */   {
/*  545: 937 */     String query = "SELECT COUNT(*) FROM " + endOfQuery(false);
/*  546: 938 */     if (!this.m_DataBaseConnection.execute(query)) {
/*  547: 939 */       throw new Exception("Cannot count results tuples.");
/*  548:     */     }
/*  549: 941 */     ResultSet rs = this.m_DataBaseConnection.getResultSet();
/*  550: 942 */     rs.next();
/*  551: 943 */     int i = rs.getInt(1);
/*  552: 944 */     rs.close();
/*  553: 945 */     return i;
/*  554:     */   }
/*  555:     */   
/*  556:     */   public Instances getStructure()
/*  557:     */     throws IOException
/*  558:     */   {
/*  559: 958 */     if (this.m_DataBaseConnection == null) {
/*  560: 959 */       throw new IOException("No source database has been specified");
/*  561:     */     }
/*  562: 962 */     connectToDatabase();
/*  563:     */     try
/*  564:     */     {
/*  565: 964 */       if ((this.m_pseudoIncremental) && (this.m_structure == null))
/*  566:     */       {
/*  567: 965 */         if (getRetrieval() == 1) {
/*  568: 966 */           throw new IOException("Cannot mix getting instances in both incremental and batch modes");
/*  569:     */         }
/*  570: 969 */         setRetrieval(0);
/*  571: 970 */         this.m_datasetPseudoInc = getDataSet();
/*  572: 971 */         this.m_structure = new Instances(this.m_datasetPseudoInc, 0);
/*  573: 972 */         setRetrieval(0);
/*  574: 973 */         return this.m_structure;
/*  575:     */       }
/*  576: 975 */       if (this.m_structure == null)
/*  577:     */       {
/*  578: 976 */         if ((this.m_checkForTable) && 
/*  579: 977 */           (!this.m_DataBaseConnection.tableExists(endOfQuery(true)))) {
/*  580: 978 */           throw new IOException("Table does not exist according to metadata from JDBC driver. If you are convinced the table exists, set 'checkForTable' to 'False' in your DatabaseUtils.props file and try again.");
/*  581:     */         }
/*  582: 987 */         int choice = 0;
/*  583: 988 */         boolean rightChoice = false;
/*  584: 989 */         while (!rightChoice) {
/*  585:     */           try
/*  586:     */           {
/*  587: 991 */             String limitQ = limitQuery(this.m_query, 0, choice);
/*  588: 992 */             if (!this.m_DataBaseConnection.execute(limitQ)) {
/*  589: 993 */               throw new IOException("Query didn't produce results");
/*  590:     */             }
/*  591: 995 */             this.m_choice = choice;
/*  592: 996 */             rightChoice = true;
/*  593:     */           }
/*  594:     */           catch (SQLException ex)
/*  595:     */           {
/*  596: 998 */             choice++;
/*  597: 999 */             if (choice == 3)
/*  598:     */             {
/*  599:1000 */               System.out.println("Incremental loading not supported for that DBMS. Pseudoincremental mode is used if you use incremental loading.\nAll rows are loaded into memory once and retrieved incrementally from memory instead of from the database.");
/*  600:     */               
/*  601:1002 */               this.m_pseudoIncremental = true;
/*  602:     */               break label1403;
/*  603:     */             }
/*  604:     */           }
/*  605:     */         }
/*  606:1008 */         String end = endOfQuery(false);
/*  607:1009 */         ResultSet rs = this.m_DataBaseConnection.getResultSet();
/*  608:     */         
/*  609:1011 */         ResultSetMetaData md = rs.getMetaData();
/*  610:     */         
/*  611:1013 */         int numAttributes = md.getColumnCount();
/*  612:1014 */         int[] attributeTypes = new int[numAttributes];
/*  613:1015 */         this.m_nominalIndexes = ((Hashtable[])Utils.cast(new Hashtable[numAttributes]));
/*  614:1016 */         this.m_nominalStrings = ((ArrayList[])Utils.cast(new ArrayList[numAttributes]));
/*  615:1017 */         for (int i = 1; i <= numAttributes; i++)
/*  616:     */         {
/*  617:     */           String columnName;
/*  618:     */           String query;
/*  619:     */           ResultSet rs1;
/*  620:1018 */           switch (this.m_DataBaseConnection.translateDBColumnType(md.getColumnTypeName(i)))
/*  621:     */           {
/*  622:     */           case 0: 
/*  623:1022 */             columnName = md.getColumnLabel(i);
/*  624:1023 */             if (this.m_DataBaseConnection.getUpperCase()) {
/*  625:1024 */               columnName = columnName.toUpperCase();
/*  626:     */             }
/*  627:1027 */             this.m_nominalIndexes[(i - 1)] = new Hashtable();
/*  628:1028 */             this.m_nominalStrings[(i - 1)] = new ArrayList();
/*  629:1032 */             if (getRetrieval() != 2)
/*  630:     */             {
/*  631:1033 */               attributeTypes[(i - 1)] = 2;
/*  632:     */             }
/*  633:     */             else
/*  634:     */             {
/*  635:1039 */               query = "SELECT COUNT(DISTINCT( " + columnName + " )) FROM " + end;
/*  636:1041 */               if (this.m_DataBaseConnection.execute(query) == true)
/*  637:     */               {
/*  638:1042 */                 ResultSet rs1 = this.m_DataBaseConnection.getResultSet();
/*  639:1043 */                 rs1.next();
/*  640:1044 */                 int count = rs1.getInt(1);
/*  641:1045 */                 rs1.close();
/*  642:1049 */                 if ((count > this.m_nominalToStringLimit) || (!this.m_DataBaseConnection.execute("SELECT DISTINCT ( " + columnName + " ) FROM " + end + " ORDER BY " + columnName)))
/*  643:     */                 {
/*  644:1052 */                   attributeTypes[(i - 1)] = 2;
/*  645:1053 */                   continue;
/*  646:     */                 }
/*  647:1055 */                 rs1 = this.m_DataBaseConnection.getResultSet();
/*  648:     */               }
/*  649:     */               else
/*  650:     */               {
/*  651:1058 */                 attributeTypes[(i - 1)] = 2;
/*  652:1059 */                 continue;
/*  653:     */               }
/*  654:1061 */               attributeTypes[(i - 1)] = 1;
/*  655:1062 */               stringToNominal(rs1, i);
/*  656:1063 */               rs1.close();
/*  657:     */             }
/*  658:1064 */             break;
/*  659:     */           case 9: 
/*  660:1068 */             columnName = md.getColumnLabel(i);
/*  661:1069 */             if (this.m_DataBaseConnection.getUpperCase()) {
/*  662:1070 */               columnName = columnName.toUpperCase();
/*  663:     */             }
/*  664:1073 */             this.m_nominalIndexes[(i - 1)] = new Hashtable();
/*  665:1074 */             this.m_nominalStrings[(i - 1)] = new ArrayList();
/*  666:1078 */             if (getRetrieval() != 2)
/*  667:     */             {
/*  668:1079 */               attributeTypes[(i - 1)] = 2;
/*  669:     */             }
/*  670:     */             else
/*  671:     */             {
/*  672:1083 */               query = "SELECT COUNT(DISTINCT( " + columnName + " )) FROM " + end;
/*  673:1084 */               if (this.m_DataBaseConnection.execute(query) == true)
/*  674:     */               {
/*  675:1085 */                 rs1 = this.m_DataBaseConnection.getResultSet();
/*  676:1086 */                 stringToNominal(rs1, i);
/*  677:1087 */                 rs1.close();
/*  678:     */               }
/*  679:1089 */               attributeTypes[(i - 1)] = 2;
/*  680:     */             }
/*  681:1090 */             break;
/*  682:     */           case 1: 
/*  683:1093 */             attributeTypes[(i - 1)] = 1;
/*  684:1094 */             this.m_nominalIndexes[(i - 1)] = new Hashtable();
/*  685:1095 */             this.m_nominalIndexes[(i - 1)].put("false", new Double(0.0D));
/*  686:1096 */             this.m_nominalIndexes[(i - 1)].put("true", new Double(1.0D));
/*  687:1097 */             this.m_nominalStrings[(i - 1)] = new ArrayList();
/*  688:1098 */             this.m_nominalStrings[(i - 1)].add("false");
/*  689:1099 */             this.m_nominalStrings[(i - 1)].add("true");
/*  690:1100 */             break;
/*  691:     */           case 2: 
/*  692:1103 */             attributeTypes[(i - 1)] = 0;
/*  693:1104 */             break;
/*  694:     */           case 3: 
/*  695:1107 */             attributeTypes[(i - 1)] = 0;
/*  696:1108 */             break;
/*  697:     */           case 4: 
/*  698:1111 */             attributeTypes[(i - 1)] = 0;
/*  699:1112 */             break;
/*  700:     */           case 5: 
/*  701:1115 */             attributeTypes[(i - 1)] = 0;
/*  702:1116 */             break;
/*  703:     */           case 6: 
/*  704:1119 */             attributeTypes[(i - 1)] = 0;
/*  705:1120 */             break;
/*  706:     */           case 7: 
/*  707:1123 */             attributeTypes[(i - 1)] = 0;
/*  708:1124 */             break;
/*  709:     */           case 8: 
/*  710:1126 */             attributeTypes[(i - 1)] = 3;
/*  711:1127 */             break;
/*  712:     */           case 10: 
/*  713:1129 */             attributeTypes[(i - 1)] = 3;
/*  714:1130 */             break;
/*  715:     */           default: 
/*  716:1133 */             attributeTypes[(i - 1)] = 2;
/*  717:     */           }
/*  718:     */         }
/*  719:1136 */         ArrayList<Attribute> attribInfo = new ArrayList();
/*  720:1137 */         for (int i = 0; i < numAttributes; i++)
/*  721:     */         {
/*  722:1140 */           String attribName = md.getColumnLabel(i + 1);
/*  723:1141 */           switch (attributeTypes[i])
/*  724:     */           {
/*  725:     */           case 1: 
/*  726:1143 */             attribInfo.add(new Attribute(attribName, this.m_nominalStrings[i]));
/*  727:1144 */             break;
/*  728:     */           case 0: 
/*  729:1146 */             attribInfo.add(new Attribute(attribName));
/*  730:1147 */             break;
/*  731:     */           case 2: 
/*  732:1149 */             Attribute att = new Attribute(attribName, (ArrayList)null);
/*  733:1150 */             for (int n = 0; n < this.m_nominalStrings[i].size(); n++) {
/*  734:1151 */               att.addStringValue((String)this.m_nominalStrings[i].get(n));
/*  735:     */             }
/*  736:1153 */             attribInfo.add(att);
/*  737:1154 */             break;
/*  738:     */           case 3: 
/*  739:1156 */             attribInfo.add(new Attribute(attribName, (String)null));
/*  740:1157 */             break;
/*  741:     */           default: 
/*  742:1159 */             throw new IOException("Unknown attribute type");
/*  743:     */           }
/*  744:     */         }
/*  745:1162 */         this.m_structure = new Instances(endOfQuery(true), attribInfo, 0);
/*  746:1164 */         if (this.m_DataBaseConnection.getUpperCase()) {
/*  747:1165 */           this.m_idColumn = this.m_idColumn.toUpperCase();
/*  748:     */         }
/*  749:1168 */         if (this.m_structure.attribute(0).name().equals(this.m_idColumn))
/*  750:     */         {
/*  751:1169 */           this.m_oldStructure = new Instances(this.m_structure, 0);
/*  752:1170 */           this.m_oldStructure.deleteAttributeAt(0);
/*  753:     */         }
/*  754:     */         else
/*  755:     */         {
/*  756:1173 */           this.m_oldStructure = new Instances(this.m_structure, 0);
/*  757:     */         }
/*  758:1176 */         if (this.m_DataBaseConnection.getResultSet() != null) {
/*  759:1177 */           rs.close();
/*  760:     */         }
/*  761:     */       }
/*  762:1180 */       else if (this.m_oldStructure == null)
/*  763:     */       {
/*  764:1181 */         this.m_oldStructure = new Instances(this.m_structure, 0);
/*  765:     */       }
/*  766:1184 */       this.m_DataBaseConnection.disconnectFromDatabase();
/*  767:     */     }
/*  768:     */     catch (Exception ex)
/*  769:     */     {
/*  770:1186 */       ex.printStackTrace();
/*  771:1187 */       printException(ex);
/*  772:     */     }
/*  773:     */     label1403:
/*  774:1189 */     return this.m_oldStructure;
/*  775:     */   }
/*  776:     */   
/*  777:     */   public Instances getDataSet()
/*  778:     */     throws IOException
/*  779:     */   {
/*  780:1202 */     if (this.m_DataBaseConnection == null) {
/*  781:1203 */       throw new IOException("No source database has been specified");
/*  782:     */     }
/*  783:1205 */     if (getRetrieval() == 2) {
/*  784:1206 */       throw new IOException("Cannot mix getting Instances in both incremental and batch modes");
/*  785:     */     }
/*  786:1209 */     setRetrieval(1);
/*  787:     */     
/*  788:1211 */     Instances result = null;
/*  789:1212 */     checkEnv();
/*  790:     */     try
/*  791:     */     {
/*  792:1214 */       InstanceQuery iq = new InstanceQuery();
/*  793:1215 */       iq.initialize(this.m_CustomPropsFile);
/*  794:1216 */       String realURL = this.m_URL;
/*  795:     */       try
/*  796:     */       {
/*  797:1218 */         realURL = this.m_env.substitute(realURL);
/*  798:     */       }
/*  799:     */       catch (Exception ex) {}
/*  800:1221 */       iq.setDatabaseURL(realURL);
/*  801:1222 */       String realUser = this.m_User;
/*  802:     */       try
/*  803:     */       {
/*  804:1224 */         realUser = this.m_env.substitute(realUser);
/*  805:     */       }
/*  806:     */       catch (Exception ex) {}
/*  807:1227 */       iq.setUsername(realUser);
/*  808:1228 */       String realPass = this.m_Password;
/*  809:     */       try
/*  810:     */       {
/*  811:1230 */         realPass = this.m_env.substitute(realPass);
/*  812:     */       }
/*  813:     */       catch (Exception ex) {}
/*  814:1233 */       iq.setPassword(realPass);
/*  815:1234 */       String realQuery = this.m_query;
/*  816:     */       try
/*  817:     */       {
/*  818:1236 */         realQuery = this.m_env.substitute(realQuery);
/*  819:     */       }
/*  820:     */       catch (Exception ex) {}
/*  821:1239 */       iq.setQuery(realQuery);
/*  822:1240 */       iq.setSparseData(this.m_CreateSparseData);
/*  823:     */       
/*  824:1242 */       result = iq.retrieveInstances();
/*  825:1244 */       if (this.m_DataBaseConnection.getUpperCase()) {
/*  826:1245 */         this.m_idColumn = this.m_idColumn.toUpperCase();
/*  827:     */       }
/*  828:1248 */       if (result.attribute(0).name().equals(this.m_idColumn)) {
/*  829:1249 */         result.deleteAttributeAt(0);
/*  830:     */       }
/*  831:1252 */       this.m_structure = new Instances(result, 0);
/*  832:1253 */       iq.disconnectFromDatabase();
/*  833:     */     }
/*  834:     */     catch (Exception ex)
/*  835:     */     {
/*  836:1256 */       printException(ex);
/*  837:1257 */       StringBuffer text = new StringBuffer();
/*  838:1258 */       if (this.m_query.equals("Select * from Results0"))
/*  839:     */       {
/*  840:1259 */         text.append("\n\nDatabaseLoader options:\n");
/*  841:1260 */         Enumeration<Option> enumi = listOptions();
/*  842:1262 */         while (enumi.hasMoreElements())
/*  843:     */         {
/*  844:1263 */           Option option = (Option)enumi.nextElement();
/*  845:1264 */           text.append(option.synopsis() + '\n');
/*  846:1265 */           text.append(option.description() + '\n');
/*  847:     */         }
/*  848:1267 */         System.out.println(text);
/*  849:     */       }
/*  850:     */     }
/*  851:1271 */     return result;
/*  852:     */   }
/*  853:     */   
/*  854:     */   private Instance readInstance(ResultSet rs)
/*  855:     */     throws Exception
/*  856:     */   {
/*  857:1283 */     ResultSetMetaData md = rs.getMetaData();
/*  858:1284 */     int numAttributes = md.getColumnCount();
/*  859:1285 */     double[] vals = new double[numAttributes];
/*  860:1286 */     this.m_structure.delete();
/*  861:1287 */     for (int i = 1; i <= numAttributes; i++)
/*  862:     */     {
/*  863:     */       String str;
/*  864:1288 */       switch (this.m_DataBaseConnection.translateDBColumnType(md.getColumnTypeName(i)))
/*  865:     */       {
/*  866:     */       case 0: 
/*  867:1291 */         str = rs.getString(i);
/*  868:1292 */         if (rs.wasNull())
/*  869:     */         {
/*  870:1293 */           vals[(i - 1)] = Utils.missingValue();
/*  871:     */         }
/*  872:     */         else
/*  873:     */         {
/*  874:1295 */           Double index = (Double)this.m_nominalIndexes[(i - 1)].get(str);
/*  875:1296 */           if (index == null) {
/*  876:1297 */             index = new Double(this.m_structure.attribute(i - 1).addStringValue(str));
/*  877:     */           }
/*  878:1300 */           vals[(i - 1)] = index.doubleValue();
/*  879:     */         }
/*  880:1302 */         break;
/*  881:     */       case 9: 
/*  882:1304 */         str = rs.getString(i);
/*  883:1305 */         if (rs.wasNull())
/*  884:     */         {
/*  885:1306 */           vals[(i - 1)] = Utils.missingValue();
/*  886:     */         }
/*  887:     */         else
/*  888:     */         {
/*  889:1308 */           Double index = (Double)this.m_nominalIndexes[(i - 1)].get(str);
/*  890:1309 */           if (index == null) {
/*  891:1310 */             index = new Double(this.m_structure.attribute(i - 1).addStringValue(str));
/*  892:     */           }
/*  893:1313 */           vals[(i - 1)] = index.doubleValue();
/*  894:     */         }
/*  895:1315 */         break;
/*  896:     */       case 1: 
/*  897:1317 */         boolean boo = rs.getBoolean(i);
/*  898:1318 */         if (rs.wasNull()) {
/*  899:1319 */           vals[(i - 1)] = Utils.missingValue();
/*  900:     */         } else {
/*  901:1321 */           vals[(i - 1)] = (boo ? 1.0D : 0.0D);
/*  902:     */         }
/*  903:1323 */         break;
/*  904:     */       case 2: 
/*  905:1326 */         double dd = rs.getDouble(i);
/*  906:1328 */         if (rs.wasNull()) {
/*  907:1329 */           vals[(i - 1)] = Utils.missingValue();
/*  908:     */         } else {
/*  909:1332 */           vals[(i - 1)] = dd;
/*  910:     */         }
/*  911:1334 */         break;
/*  912:     */       case 3: 
/*  913:1336 */         byte by = rs.getByte(i);
/*  914:1337 */         if (rs.wasNull()) {
/*  915:1338 */           vals[(i - 1)] = Utils.missingValue();
/*  916:     */         } else {
/*  917:1340 */           vals[(i - 1)] = by;
/*  918:     */         }
/*  919:1342 */         break;
/*  920:     */       case 4: 
/*  921:1344 */         short sh = rs.getShort(i);
/*  922:1345 */         if (rs.wasNull()) {
/*  923:1346 */           vals[(i - 1)] = Utils.missingValue();
/*  924:     */         } else {
/*  925:1348 */           vals[(i - 1)] = sh;
/*  926:     */         }
/*  927:1350 */         break;
/*  928:     */       case 5: 
/*  929:1352 */         int in = rs.getInt(i);
/*  930:1353 */         if (rs.wasNull()) {
/*  931:1354 */           vals[(i - 1)] = Utils.missingValue();
/*  932:     */         } else {
/*  933:1356 */           vals[(i - 1)] = in;
/*  934:     */         }
/*  935:1358 */         break;
/*  936:     */       case 6: 
/*  937:1360 */         long lo = rs.getLong(i);
/*  938:1361 */         if (rs.wasNull()) {
/*  939:1362 */           vals[(i - 1)] = Utils.missingValue();
/*  940:     */         } else {
/*  941:1364 */           vals[(i - 1)] = lo;
/*  942:     */         }
/*  943:1366 */         break;
/*  944:     */       case 7: 
/*  945:1368 */         float fl = rs.getFloat(i);
/*  946:1369 */         if (rs.wasNull()) {
/*  947:1370 */           vals[(i - 1)] = Utils.missingValue();
/*  948:     */         } else {
/*  949:1372 */           vals[(i - 1)] = fl;
/*  950:     */         }
/*  951:1374 */         break;
/*  952:     */       case 8: 
/*  953:1376 */         Date date = rs.getDate(i);
/*  954:1377 */         if (rs.wasNull()) {
/*  955:1378 */           vals[(i - 1)] = Utils.missingValue();
/*  956:     */         } else {
/*  957:1381 */           vals[(i - 1)] = date.getTime();
/*  958:     */         }
/*  959:1383 */         break;
/*  960:     */       case 10: 
/*  961:1385 */         Time time = rs.getTime(i);
/*  962:1386 */         if (rs.wasNull()) {
/*  963:1387 */           vals[(i - 1)] = Utils.missingValue();
/*  964:     */         } else {
/*  965:1390 */           vals[(i - 1)] = time.getTime();
/*  966:     */         }
/*  967:1392 */         break;
/*  968:     */       default: 
/*  969:1394 */         vals[(i - 1)] = Utils.missingValue();
/*  970:     */       }
/*  971:     */     }
/*  972:     */     Instance inst;
/*  973:     */     Instance inst;
/*  974:1398 */     if (this.m_CreateSparseData) {
/*  975:1399 */       inst = new SparseInstance(1.0D, vals);
/*  976:     */     } else {
/*  977:1401 */       inst = new DenseInstance(1.0D, vals);
/*  978:     */     }
/*  979:1404 */     if (this.m_DataBaseConnection.getUpperCase()) {
/*  980:1405 */       this.m_idColumn = this.m_idColumn.toUpperCase();
/*  981:     */     }
/*  982:1407 */     if (this.m_structure.attribute(0).name().equals(this.m_idColumn))
/*  983:     */     {
/*  984:1408 */       inst.deleteAttributeAt(0);
/*  985:1409 */       this.m_oldStructure.add(inst);
/*  986:1410 */       inst = this.m_oldStructure.instance(0);
/*  987:1411 */       this.m_oldStructure.delete(0);
/*  988:     */     }
/*  989:     */     else
/*  990:     */     {
/*  991:1415 */       this.m_structure.add(inst);
/*  992:1416 */       inst = this.m_structure.instance(0);
/*  993:1417 */       this.m_structure.delete(0);
/*  994:     */     }
/*  995:1419 */     return inst;
/*  996:     */   }
/*  997:     */   
/*  998:     */   public Instance getNextInstance(Instances structure)
/*  999:     */     throws IOException
/* 1000:     */   {
/* 1001:1438 */     this.m_structure = structure;
/* 1002:1440 */     if (this.m_DataBaseConnection == null) {
/* 1003:1441 */       throw new IOException("No source database has been specified");
/* 1004:     */     }
/* 1005:1443 */     if (getRetrieval() == 1) {
/* 1006:1444 */       throw new IOException("Cannot mix getting Instances in both incremental and batch modes");
/* 1007:     */     }
/* 1008:1449 */     if (this.m_pseudoIncremental)
/* 1009:     */     {
/* 1010:1450 */       setRetrieval(2);
/* 1011:1451 */       if (this.m_datasetPseudoInc.numInstances() > 0)
/* 1012:     */       {
/* 1013:1452 */         Instance current = this.m_datasetPseudoInc.instance(0);
/* 1014:1453 */         this.m_datasetPseudoInc.delete(0);
/* 1015:1454 */         return current;
/* 1016:     */       }
/* 1017:1456 */       resetStructure();
/* 1018:1457 */       return null;
/* 1019:     */     }
/* 1020:1462 */     setRetrieval(2);
/* 1021:     */     try
/* 1022:     */     {
/* 1023:1464 */       if (!this.m_DataBaseConnection.isConnected()) {
/* 1024:1465 */         connectToDatabase();
/* 1025:     */       }
/* 1026:1468 */       if ((this.m_firstTime) && (this.m_orderBy.size() == 0) && 
/* 1027:1469 */         (!checkForKey())) {
/* 1028:1470 */         throw new Exception("A unique order cannot be detected automatically.\nYou have to use SELECT * in your query to enable this feature.\nMaybe JDBC driver is not able to detect key.\nDefine primary key in your database or use -P option (command line) or enter key columns in the GUI.");
/* 1029:     */       }
/* 1030:1474 */       if (this.m_firstTime)
/* 1031:     */       {
/* 1032:1475 */         this.m_firstTime = false;
/* 1033:1476 */         this.m_rowCount = getRowCount();
/* 1034:     */       }
/* 1035:1479 */       if (this.m_counter < this.m_rowCount)
/* 1036:     */       {
/* 1037:1480 */         if (!this.m_DataBaseConnection.execute(limitQuery(this.m_query, this.m_counter, this.m_choice))) {
/* 1038:1482 */           throw new Exception("Tuple could not be retrieved.");
/* 1039:     */         }
/* 1040:1484 */         this.m_counter += 1;
/* 1041:1485 */         ResultSet rs = this.m_DataBaseConnection.getResultSet();
/* 1042:1486 */         rs.next();
/* 1043:1487 */         Instance current = readInstance(rs);
/* 1044:1488 */         rs.close();
/* 1045:1489 */         return current;
/* 1046:     */       }
/* 1047:1491 */       this.m_DataBaseConnection.disconnectFromDatabase();
/* 1048:1492 */       resetStructure();
/* 1049:1493 */       return null;
/* 1050:     */     }
/* 1051:     */     catch (Exception ex)
/* 1052:     */     {
/* 1053:1496 */       printException(ex);
/* 1054:     */     }
/* 1055:1498 */     return null;
/* 1056:     */   }
/* 1057:     */   
/* 1058:     */   public String[] getOptions()
/* 1059:     */   {
/* 1060:1509 */     Vector<String> options = new Vector();
/* 1061:1511 */     if ((getUrl() != null) && (getUrl().length() != 0))
/* 1062:     */     {
/* 1063:1512 */       options.add("-url");
/* 1064:1513 */       options.add(getUrl());
/* 1065:     */     }
/* 1066:1516 */     if ((getUser() != null) && (getUser().length() != 0))
/* 1067:     */     {
/* 1068:1517 */       options.add("-user");
/* 1069:1518 */       options.add(getUser());
/* 1070:     */     }
/* 1071:1521 */     if ((getPassword() != null) && (getPassword().length() != 0))
/* 1072:     */     {
/* 1073:1522 */       options.add("-password");
/* 1074:1523 */       options.add(getPassword());
/* 1075:     */     }
/* 1076:1526 */     options.add("-Q");
/* 1077:1527 */     options.add(getQuery());
/* 1078:     */     
/* 1079:1529 */     StringBuffer text = new StringBuffer();
/* 1080:1530 */     for (int i = 0; i < this.m_orderBy.size(); i++)
/* 1081:     */     {
/* 1082:1531 */       if (i > 0) {
/* 1083:1532 */         text.append(", ");
/* 1084:     */       }
/* 1085:1534 */       text.append((String)this.m_orderBy.get(i));
/* 1086:     */     }
/* 1087:1537 */     if (text.length() > 0)
/* 1088:     */     {
/* 1089:1538 */       options.add("-P");
/* 1090:1539 */       options.add(text.toString());
/* 1091:     */     }
/* 1092:1542 */     if (this.m_inc) {
/* 1093:1543 */       options.add("-I");
/* 1094:     */     }
/* 1095:1546 */     if ((this.m_CustomPropsFile != null) && (!this.m_CustomPropsFile.isDirectory()))
/* 1096:     */     {
/* 1097:1547 */       options.add("-custom-props");
/* 1098:1548 */       options.add(this.m_CustomPropsFile.toString());
/* 1099:     */     }
/* 1100:1551 */     return (String[])options.toArray(new String[options.size()]);
/* 1101:     */   }
/* 1102:     */   
/* 1103:     */   public Enumeration<Option> listOptions()
/* 1104:     */   {
/* 1105:1562 */     Vector<Option> newVector = new Vector();
/* 1106:     */     
/* 1107:1564 */     newVector.add(new Option("\tThe JDBC URL to connect to.\n\t(default: from DatabaseUtils.props file)", "url", 1, "-url <JDBC URL>"));
/* 1108:     */     
/* 1109:     */ 
/* 1110:     */ 
/* 1111:1568 */     newVector.add(new Option("\tThe user to connect with to the database.\n\t(default: none)", "user", 1, "-user <name>"));
/* 1112:     */     
/* 1113:     */ 
/* 1114:1571 */     newVector.add(new Option("\tThe password to connect with to the database.\n\t(default: none)", "password", 1, "-password <password>"));
/* 1115:     */     
/* 1116:     */ 
/* 1117:     */ 
/* 1118:1575 */     newVector.add(new Option("\tSQL query of the form\n\t\tSELECT <list of columns>|* FROM <table> [WHERE]\n\tto execute.\n\t(default: Select * From Results0)", "Q", 1, "-Q <query>"));
/* 1119:     */     
/* 1120:     */ 
/* 1121:     */ 
/* 1122:     */ 
/* 1123:1580 */     newVector.add(new Option("\tList of column names uniquely defining a DB row\n\t(separated by ', ').\n\tUsed for incremental loading.\n\tIf not specified, the key will be determined automatically,\n\tif possible with the used JDBC driver.\n\tThe auto ID column created by the DatabaseSaver won't be loaded.", "P", 1, "-P <list of column names>"));
/* 1124:     */     
/* 1125:     */ 
/* 1126:     */ 
/* 1127:     */ 
/* 1128:     */ 
/* 1129:     */ 
/* 1130:     */ 
/* 1131:1588 */     newVector.add(new Option("\tSets incremental loading", "I", 0, "-I"));
/* 1132:     */     
/* 1133:1590 */     newVector.addElement(new Option("\tReturn sparse rather than normal instances.", "S", 0, "-S"));
/* 1134:     */     
/* 1135:     */ 
/* 1136:1593 */     newVector.add(new Option("\tThe custom properties file to use instead of default ones,\n\tcontaining the database parameters.\n\t(default: none)", "custom-props", 1, "-custom-props <file>"));
/* 1137:     */     
/* 1138:     */ 
/* 1139:     */ 
/* 1140:     */ 
/* 1141:1598 */     return newVector.elements();
/* 1142:     */   }
/* 1143:     */   
/* 1144:     */   public void setOptions(String[] options)
/* 1145:     */     throws Exception
/* 1146:     */   {
/* 1147:1658 */     String optionString = Utils.getOption('Q', options);
/* 1148:     */     
/* 1149:1660 */     String keyString = Utils.getOption('P', options);
/* 1150:     */     
/* 1151:1662 */     reset();
/* 1152:     */     
/* 1153:1664 */     String tmpStr = Utils.getOption("url", options);
/* 1154:1665 */     if (tmpStr.length() != 0) {
/* 1155:1666 */       setUrl(tmpStr);
/* 1156:     */     }
/* 1157:1669 */     tmpStr = Utils.getOption("user", options);
/* 1158:1670 */     if (tmpStr.length() != 0) {
/* 1159:1671 */       setUser(tmpStr);
/* 1160:     */     }
/* 1161:1674 */     tmpStr = Utils.getOption("password", options);
/* 1162:1675 */     if (tmpStr.length() != 0) {
/* 1163:1676 */       setPassword(tmpStr);
/* 1164:     */     }
/* 1165:1679 */     if (optionString.length() != 0) {
/* 1166:1680 */       setQuery(optionString);
/* 1167:     */     }
/* 1168:1683 */     this.m_orderBy.clear();
/* 1169:     */     
/* 1170:1685 */     this.m_inc = Utils.getFlag('I', options);
/* 1171:1687 */     if (this.m_inc)
/* 1172:     */     {
/* 1173:1688 */       StringTokenizer st = new StringTokenizer(keyString, ",");
/* 1174:1689 */       while (st.hasMoreTokens())
/* 1175:     */       {
/* 1176:1690 */         String column = st.nextToken();
/* 1177:1691 */         column = column.replaceAll(" ", "");
/* 1178:1692 */         this.m_orderBy.add(column);
/* 1179:     */       }
/* 1180:     */     }
/* 1181:1696 */     tmpStr = Utils.getOption("custom-props", options);
/* 1182:1697 */     if (tmpStr.length() == 0) {
/* 1183:1698 */       setCustomPropsFile(null);
/* 1184:     */     } else {
/* 1185:1700 */       setCustomPropsFile(new File(tmpStr));
/* 1186:     */     }
/* 1187:     */   }
/* 1188:     */   
/* 1189:     */   private void printException(Exception ex)
/* 1190:     */   {
/* 1191:1711 */     System.out.println("\n--- Exception caught ---\n");
/* 1192:1712 */     while (ex != null)
/* 1193:     */     {
/* 1194:1713 */       System.out.println("Message:   " + ex.getMessage());
/* 1195:1714 */       if ((ex instanceof SQLException))
/* 1196:     */       {
/* 1197:1715 */         System.out.println("SQLState:  " + ((SQLException)ex).getSQLState());
/* 1198:1716 */         System.out.println("ErrorCode: " + ((SQLException)ex).getErrorCode());
/* 1199:1717 */         ex = ((SQLException)ex).getNextException();
/* 1200:     */       }
/* 1201:     */       else
/* 1202:     */       {
/* 1203:1719 */         ex = null;
/* 1204:     */       }
/* 1205:1721 */       System.out.println("");
/* 1206:     */     }
/* 1207:     */   }
/* 1208:     */   
/* 1209:     */   public String getRevision()
/* 1210:     */   {
/* 1211:1733 */     return RevisionUtils.extract("$Revision: 12418 $");
/* 1212:     */   }
/* 1213:     */   
/* 1214:     */   public static void main(String[] options)
/* 1215:     */   {
/* 1216:     */     try
/* 1217:     */     {
/* 1218:1745 */       DatabaseLoader atf = new DatabaseLoader();
/* 1219:1746 */       atf.setOptions(options);
/* 1220:1747 */       atf.setSource(atf.getUrl(), atf.getUser(), atf.getPassword());
/* 1221:1748 */       if (!atf.m_inc)
/* 1222:     */       {
/* 1223:1749 */         System.out.println(atf.getDataSet());
/* 1224:     */       }
/* 1225:     */       else
/* 1226:     */       {
/* 1227:1751 */         Instances structure = atf.getStructure();
/* 1228:1752 */         System.out.println(structure);
/* 1229:     */         Instance temp;
/* 1230:     */         do
/* 1231:     */         {
/* 1232:1755 */           temp = atf.getNextInstance(structure);
/* 1233:1756 */           if (temp != null) {
/* 1234:1757 */             System.out.println(temp);
/* 1235:     */           }
/* 1236:1759 */         } while (temp != null);
/* 1237:     */       }
/* 1238:     */     }
/* 1239:     */     catch (Exception e)
/* 1240:     */     {
/* 1241:1762 */       e.printStackTrace();
/* 1242:1763 */       System.out.println("\n" + e.getMessage());
/* 1243:     */     }
/* 1244:     */   }
/* 1245:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.DatabaseLoader
 * JD-Core Version:    0.7.0.1
 */