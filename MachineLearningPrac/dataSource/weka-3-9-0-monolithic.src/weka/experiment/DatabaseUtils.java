/*    1:     */ package weka.experiment;
/*    2:     */ 
/*    3:     */ import java.io.File;
/*    4:     */ import java.io.FileInputStream;
/*    5:     */ import java.io.PrintStream;
/*    6:     */ import java.io.Serializable;
/*    7:     */ import java.sql.Connection;
/*    8:     */ import java.sql.DatabaseMetaData;
/*    9:     */ import java.sql.DriverManager;
/*   10:     */ import java.sql.PreparedStatement;
/*   11:     */ import java.sql.ResultSet;
/*   12:     */ import java.sql.ResultSetMetaData;
/*   13:     */ import java.sql.SQLException;
/*   14:     */ import java.sql.Statement;
/*   15:     */ import java.util.Collections;
/*   16:     */ import java.util.HashSet;
/*   17:     */ import java.util.Properties;
/*   18:     */ import java.util.StringTokenizer;
/*   19:     */ import java.util.Vector;
/*   20:     */ import weka.core.RevisionHandler;
/*   21:     */ import weka.core.RevisionUtils;
/*   22:     */ import weka.core.Utils;
/*   23:     */ import weka.core.logging.Logger;
/*   24:     */ import weka.core.logging.Logger.Level;
/*   25:     */ 
/*   26:     */ public class DatabaseUtils
/*   27:     */   implements Serializable, RevisionHandler
/*   28:     */ {
/*   29:     */   static final long serialVersionUID = -8252351994547116729L;
/*   30:     */   public static final String EXP_INDEX_TABLE = "Experiment_index";
/*   31:     */   public static final String EXP_TYPE_COL = "Experiment_type";
/*   32:     */   public static final String EXP_SETUP_COL = "Experiment_setup";
/*   33:     */   public static final String EXP_RESULT_COL = "Result_table";
/*   34:     */   public static final String EXP_RESULT_PREFIX = "Results";
/*   35:     */   public static final String PROPERTY_FILE = "weka/experiment/DatabaseUtils.props";
/*   36:  88 */   protected Vector<String> DRIVERS = new Vector();
/*   37:     */   protected static Vector<String> DRIVERS_ERRORS;
/*   38:     */   protected Properties PROPERTIES;
/*   39:     */   public static final int STRING = 0;
/*   40:     */   public static final int BOOL = 1;
/*   41:     */   public static final int DOUBLE = 2;
/*   42:     */   public static final int BYTE = 3;
/*   43:     */   public static final int SHORT = 4;
/*   44:     */   public static final int INTEGER = 5;
/*   45:     */   public static final int LONG = 6;
/*   46:     */   public static final int FLOAT = 7;
/*   47:     */   public static final int DATE = 8;
/*   48:     */   public static final int TEXT = 9;
/*   49:     */   public static final int TIME = 10;
/*   50:     */   public static final int TIMESTAMP = 11;
/*   51:     */   protected String m_DatabaseURL;
/*   52:     */   protected transient PreparedStatement m_PreparedStatement;
/*   53:     */   protected transient Connection m_Connection;
/*   54: 132 */   protected boolean m_Debug = false;
/*   55: 135 */   protected String m_userName = "";
/*   56: 138 */   protected String m_password = "";
/*   57: 142 */   protected String m_stringType = "LONGVARCHAR";
/*   58: 144 */   protected String m_intType = "INT";
/*   59: 146 */   protected String m_doubleType = "DOUBLE";
/*   60: 149 */   protected boolean m_checkForUpperCaseNames = false;
/*   61: 152 */   protected boolean m_checkForLowerCaseNames = false;
/*   62: 155 */   protected boolean m_setAutoCommit = true;
/*   63: 158 */   protected boolean m_createIndex = false;
/*   64: 161 */   protected HashSet<String> m_Keywords = new HashSet();
/*   65: 164 */   protected String m_KeywordsMaskChar = "_";
/*   66:     */   
/*   67:     */   public DatabaseUtils()
/*   68:     */     throws Exception
/*   69:     */   {
/*   70: 172 */     this((Properties)null);
/*   71:     */   }
/*   72:     */   
/*   73:     */   public DatabaseUtils(File propsFile)
/*   74:     */     throws Exception
/*   75:     */   {
/*   76: 184 */     this(loadProperties(propsFile));
/*   77:     */   }
/*   78:     */   
/*   79:     */   public DatabaseUtils(Properties props)
/*   80:     */     throws Exception
/*   81:     */   {
/*   82: 194 */     if (DRIVERS_ERRORS == null) {
/*   83: 195 */       DRIVERS_ERRORS = new Vector();
/*   84:     */     }
/*   85: 198 */     initialize(props);
/*   86:     */   }
/*   87:     */   
/*   88:     */   public void initialize(File propsFile)
/*   89:     */   {
/*   90: 208 */     initialize(loadProperties(propsFile));
/*   91:     */   }
/*   92:     */   
/*   93:     */   public void initialize(Properties props)
/*   94:     */   {
/*   95:     */     try
/*   96:     */     {
/*   97: 218 */       if (props != null) {
/*   98: 219 */         this.PROPERTIES = props;
/*   99:     */       } else {
/*  100: 221 */         this.PROPERTIES = Utils.readProperties("weka/experiment/DatabaseUtils.props");
/*  101:     */       }
/*  102: 225 */       String drivers = this.PROPERTIES.getProperty("jdbcDriver", "jdbc.idbDriver");
/*  103: 227 */       if (drivers == null) {
/*  104: 228 */         throw new Exception("No database drivers (JDBC) specified");
/*  105:     */       }
/*  106: 232 */       StringTokenizer st = new StringTokenizer(drivers, ", ");
/*  107: 233 */       while (st.hasMoreTokens())
/*  108:     */       {
/*  109: 234 */         String driver = st.nextToken();
/*  110:     */         boolean result;
/*  111:     */         try
/*  112:     */         {
/*  113: 237 */           Class.forName(driver);
/*  114: 238 */           this.DRIVERS.addElement(driver);
/*  115: 239 */           result = true;
/*  116:     */         }
/*  117:     */         catch (Exception e)
/*  118:     */         {
/*  119: 241 */           result = false;
/*  120:     */         }
/*  121: 243 */         if ((!result) && (!DRIVERS_ERRORS.contains(driver))) {
/*  122: 244 */           Logger.log(Logger.Level.WARNING, "Trying to add database driver (JDBC): " + driver + " - " + "Warning, not in CLASSPATH?");
/*  123: 247 */         } else if (this.m_Debug) {
/*  124: 248 */           System.err.println("Trying to add database driver (JDBC): " + driver + " - " + (result ? "Success!" : "Warning, not in CLASSPATH?"));
/*  125:     */         }
/*  126: 251 */         if (!result) {
/*  127: 252 */           DRIVERS_ERRORS.add(driver);
/*  128:     */         }
/*  129:     */       }
/*  130:     */     }
/*  131:     */     catch (Exception ex)
/*  132:     */     {
/*  133: 256 */       System.err.println("Problem reading properties. Fix before continuing.");
/*  134: 257 */       System.err.println(ex);
/*  135:     */     }
/*  136: 260 */     this.m_DatabaseURL = this.PROPERTIES.getProperty("jdbcURL", "jdbc:idb=experiments.prp");
/*  137:     */     
/*  138: 262 */     this.m_stringType = this.PROPERTIES.getProperty("CREATE_STRING", "LONGVARCHAR");
/*  139: 263 */     this.m_intType = this.PROPERTIES.getProperty("CREATE_INT", "INT");
/*  140: 264 */     this.m_doubleType = this.PROPERTIES.getProperty("CREATE_DOUBLE", "DOUBLE");
/*  141: 265 */     this.m_checkForUpperCaseNames = this.PROPERTIES.getProperty("checkUpperCaseNames", "false").equals("true");
/*  142:     */     
/*  143: 267 */     this.m_checkForLowerCaseNames = this.PROPERTIES.getProperty("checkLowerCaseNames", "false").equals("true");
/*  144:     */     
/*  145: 269 */     this.m_setAutoCommit = this.PROPERTIES.getProperty("setAutoCommit", "true").equals("true");
/*  146:     */     
/*  147: 271 */     this.m_createIndex = this.PROPERTIES.getProperty("createIndex", "false").equals("true");
/*  148:     */     
/*  149: 273 */     setKeywords(this.PROPERTIES.getProperty("Keywords", "AND,ASC,BY,DESC,FROM,GROUP,INSERT,ORDER,SELECT,UPDATE,WHERE"));
/*  150:     */     
/*  151: 275 */     setKeywordsMaskChar(this.PROPERTIES.getProperty("KeywordsMaskChar", "_"));
/*  152:     */   }
/*  153:     */   
/*  154:     */   public String attributeCaseFix(String columnName)
/*  155:     */   {
/*  156: 286 */     if (this.m_checkForUpperCaseNames)
/*  157:     */     {
/*  158: 287 */       String ucname = columnName.toUpperCase();
/*  159: 288 */       if (ucname.equals("Experiment_type".toUpperCase())) {
/*  160: 289 */         return "Experiment_type";
/*  161:     */       }
/*  162: 290 */       if (ucname.equals("Experiment_setup".toUpperCase())) {
/*  163: 291 */         return "Experiment_setup";
/*  164:     */       }
/*  165: 292 */       if (ucname.equals("Result_table".toUpperCase())) {
/*  166: 293 */         return "Result_table";
/*  167:     */       }
/*  168: 295 */       return columnName;
/*  169:     */     }
/*  170: 297 */     if (this.m_checkForLowerCaseNames)
/*  171:     */     {
/*  172: 298 */       String ucname = columnName.toLowerCase();
/*  173: 299 */       if (ucname.equals("Experiment_type".toLowerCase())) {
/*  174: 300 */         return "Experiment_type";
/*  175:     */       }
/*  176: 301 */       if (ucname.equals("Experiment_setup".toLowerCase())) {
/*  177: 302 */         return "Experiment_setup";
/*  178:     */       }
/*  179: 303 */       if (ucname.equals("Result_table".toLowerCase())) {
/*  180: 304 */         return "Result_table";
/*  181:     */       }
/*  182: 306 */       return columnName;
/*  183:     */     }
/*  184: 309 */     return columnName;
/*  185:     */   }
/*  186:     */   
/*  187:     */   public int translateDBColumnType(String type)
/*  188:     */   {
/*  189:     */     try
/*  190:     */     {
/*  191: 331 */       String value = this.PROPERTIES.getProperty(type);
/*  192: 332 */       String typeUnderscore = type.replaceAll(" ", "_");
/*  193: 333 */       if (value == null) {
/*  194: 334 */         value = this.PROPERTIES.getProperty(typeUnderscore);
/*  195:     */       }
/*  196: 336 */       return Integer.parseInt(value);
/*  197:     */     }
/*  198:     */     catch (NumberFormatException e)
/*  199:     */     {
/*  200: 338 */       e.printStackTrace();
/*  201: 339 */       throw new IllegalArgumentException("Unknown data type: " + type + ". " + "Add entry in " + "weka/experiment/DatabaseUtils.props" + ".\n" + "If the type contains blanks, either escape them with a backslash " + "or use underscores instead of blanks.");
/*  202:     */     }
/*  203:     */   }
/*  204:     */   
/*  205:     */   public static String arrayToString(Object[] array)
/*  206:     */   {
/*  207: 354 */     String result = "";
/*  208: 355 */     if (array == null) {
/*  209: 356 */       result = "<null>";
/*  210:     */     } else {
/*  211: 358 */       for (Object element : array) {
/*  212: 359 */         if (element == null) {
/*  213: 360 */           result = result + " ?";
/*  214:     */         } else {
/*  215: 362 */           result = result + " " + element;
/*  216:     */         }
/*  217:     */       }
/*  218:     */     }
/*  219: 366 */     return result;
/*  220:     */   }
/*  221:     */   
/*  222:     */   public static String typeName(int type)
/*  223:     */   {
/*  224: 376 */     switch (type)
/*  225:     */     {
/*  226:     */     case -5: 
/*  227: 378 */       return "BIGINT ";
/*  228:     */     case -2: 
/*  229: 380 */       return "BINARY";
/*  230:     */     case -7: 
/*  231: 382 */       return "BIT";
/*  232:     */     case 1: 
/*  233: 384 */       return "CHAR";
/*  234:     */     case 91: 
/*  235: 386 */       return "DATE";
/*  236:     */     case 3: 
/*  237: 388 */       return "DECIMAL";
/*  238:     */     case 8: 
/*  239: 390 */       return "DOUBLE";
/*  240:     */     case 6: 
/*  241: 392 */       return "FLOAT";
/*  242:     */     case 4: 
/*  243: 394 */       return "INTEGER";
/*  244:     */     case -4: 
/*  245: 396 */       return "LONGVARBINARY";
/*  246:     */     case -1: 
/*  247: 398 */       return "LONGVARCHAR";
/*  248:     */     case 0: 
/*  249: 400 */       return "NULL";
/*  250:     */     case 2: 
/*  251: 402 */       return "NUMERIC";
/*  252:     */     case 1111: 
/*  253: 404 */       return "OTHER";
/*  254:     */     case 7: 
/*  255: 406 */       return "REAL";
/*  256:     */     case 5: 
/*  257: 408 */       return "SMALLINT";
/*  258:     */     case 92: 
/*  259: 410 */       return "TIME";
/*  260:     */     case 93: 
/*  261: 412 */       return "TIMESTAMP";
/*  262:     */     case -6: 
/*  263: 414 */       return "TINYINT";
/*  264:     */     case -3: 
/*  265: 416 */       return "VARBINARY";
/*  266:     */     case 12: 
/*  267: 418 */       return "VARCHAR";
/*  268:     */     }
/*  269: 420 */     return "Unknown";
/*  270:     */   }
/*  271:     */   
/*  272:     */   public String databaseURLTipText()
/*  273:     */   {
/*  274: 431 */     return "Set the URL to the database.";
/*  275:     */   }
/*  276:     */   
/*  277:     */   public String getDatabaseURL()
/*  278:     */   {
/*  279: 440 */     return this.m_DatabaseURL;
/*  280:     */   }
/*  281:     */   
/*  282:     */   public void setDatabaseURL(String newDatabaseURL)
/*  283:     */   {
/*  284: 449 */     this.m_DatabaseURL = newDatabaseURL;
/*  285:     */   }
/*  286:     */   
/*  287:     */   public String debugTipText()
/*  288:     */   {
/*  289: 459 */     return "Whether debug information is printed.";
/*  290:     */   }
/*  291:     */   
/*  292:     */   public void setDebug(boolean d)
/*  293:     */   {
/*  294: 469 */     this.m_Debug = d;
/*  295:     */   }
/*  296:     */   
/*  297:     */   public boolean getDebug()
/*  298:     */   {
/*  299: 479 */     return this.m_Debug;
/*  300:     */   }
/*  301:     */   
/*  302:     */   public String usernameTipText()
/*  303:     */   {
/*  304: 489 */     return "The user to use for connecting to the database.";
/*  305:     */   }
/*  306:     */   
/*  307:     */   public void setUsername(String username)
/*  308:     */   {
/*  309: 498 */     this.m_userName = username;
/*  310:     */   }
/*  311:     */   
/*  312:     */   public String getUsername()
/*  313:     */   {
/*  314: 507 */     return this.m_userName;
/*  315:     */   }
/*  316:     */   
/*  317:     */   public String passwordTipText()
/*  318:     */   {
/*  319: 517 */     return "The password to use for connecting to the database.";
/*  320:     */   }
/*  321:     */   
/*  322:     */   public void setPassword(String password)
/*  323:     */   {
/*  324: 526 */     this.m_password = password;
/*  325:     */   }
/*  326:     */   
/*  327:     */   public String getPassword()
/*  328:     */   {
/*  329: 535 */     return this.m_password;
/*  330:     */   }
/*  331:     */   
/*  332:     */   public void connectToDatabase()
/*  333:     */     throws Exception
/*  334:     */   {
/*  335: 544 */     if (this.m_Debug) {
/*  336: 545 */       System.err.println("Connecting to " + this.m_DatabaseURL);
/*  337:     */     }
/*  338: 547 */     if (this.m_Connection == null) {
/*  339: 548 */       if (this.m_userName.equals("")) {
/*  340:     */         try
/*  341:     */         {
/*  342: 550 */           this.m_Connection = DriverManager.getConnection(this.m_DatabaseURL);
/*  343:     */         }
/*  344:     */         catch (SQLException e)
/*  345:     */         {
/*  346: 554 */           for (int i = 0; i < this.DRIVERS.size(); i++) {
/*  347:     */             try
/*  348:     */             {
/*  349: 556 */               Class.forName((String)this.DRIVERS.elementAt(i));
/*  350:     */             }
/*  351:     */             catch (Exception ex) {}
/*  352:     */           }
/*  353: 561 */           this.m_Connection = DriverManager.getConnection(this.m_DatabaseURL);
/*  354:     */         }
/*  355:     */       } else {
/*  356:     */         try
/*  357:     */         {
/*  358: 565 */           this.m_Connection = DriverManager.getConnection(this.m_DatabaseURL, this.m_userName, this.m_password);
/*  359:     */         }
/*  360:     */         catch (SQLException e)
/*  361:     */         {
/*  362: 570 */           for (int i = 0; i < this.DRIVERS.size(); i++) {
/*  363:     */             try
/*  364:     */             {
/*  365: 572 */               Class.forName((String)this.DRIVERS.elementAt(i));
/*  366:     */             }
/*  367:     */             catch (Exception ex) {}
/*  368:     */           }
/*  369: 577 */           this.m_Connection = DriverManager.getConnection(this.m_DatabaseURL, this.m_userName, this.m_password);
/*  370:     */         }
/*  371:     */       }
/*  372:     */     }
/*  373: 582 */     this.m_Connection.setAutoCommit(this.m_setAutoCommit);
/*  374:     */   }
/*  375:     */   
/*  376:     */   public void disconnectFromDatabase()
/*  377:     */     throws Exception
/*  378:     */   {
/*  379: 591 */     if (this.m_Debug) {
/*  380: 592 */       System.err.println("Disconnecting from " + this.m_DatabaseURL);
/*  381:     */     }
/*  382: 594 */     if (this.m_Connection != null)
/*  383:     */     {
/*  384: 595 */       this.m_Connection.close();
/*  385: 596 */       this.m_Connection = null;
/*  386:     */     }
/*  387:     */   }
/*  388:     */   
/*  389:     */   public boolean isConnected()
/*  390:     */   {
/*  391: 606 */     return this.m_Connection != null;
/*  392:     */   }
/*  393:     */   
/*  394:     */   public boolean isCursorScrollSensitive()
/*  395:     */   {
/*  396: 622 */     boolean result = false;
/*  397:     */     try
/*  398:     */     {
/*  399: 625 */       if (isConnected()) {
/*  400: 626 */         result = this.m_Connection.getMetaData().supportsResultSetConcurrency(1005, 1007);
/*  401:     */       }
/*  402:     */     }
/*  403:     */     catch (Exception e) {}
/*  404: 633 */     return result;
/*  405:     */   }
/*  406:     */   
/*  407:     */   public boolean isCursorScrollable()
/*  408:     */   {
/*  409: 644 */     return getSupportedCursorScrollType() != -1;
/*  410:     */   }
/*  411:     */   
/*  412:     */   public int getSupportedCursorScrollType()
/*  413:     */   {
/*  414: 660 */     int result = -1;
/*  415:     */     try
/*  416:     */     {
/*  417: 663 */       if (isConnected())
/*  418:     */       {
/*  419: 664 */         if (this.m_Connection.getMetaData().supportsResultSetConcurrency(1005, 1007)) {
/*  420: 666 */           result = 1005;
/*  421:     */         }
/*  422: 669 */         if ((result == -1) && 
/*  423: 670 */           (this.m_Connection.getMetaData().supportsResultSetConcurrency(1004, 1007))) {
/*  424: 672 */           result = 1004;
/*  425:     */         }
/*  426:     */       }
/*  427:     */     }
/*  428:     */     catch (Exception e) {}
/*  429: 680 */     return result;
/*  430:     */   }
/*  431:     */   
/*  432:     */   public boolean execute(String query)
/*  433:     */     throws SQLException
/*  434:     */   {
/*  435: 693 */     if (!isConnected()) {
/*  436: 694 */       throw new IllegalStateException("Not connected, please connect first!");
/*  437:     */     }
/*  438: 697 */     if (!isCursorScrollable()) {
/*  439: 698 */       this.m_PreparedStatement = this.m_Connection.prepareStatement(query, 1003, 1007);
/*  440:     */     } else {
/*  441: 701 */       this.m_PreparedStatement = this.m_Connection.prepareStatement(query, getSupportedCursorScrollType(), 1007);
/*  442:     */     }
/*  443: 705 */     return this.m_PreparedStatement.execute();
/*  444:     */   }
/*  445:     */   
/*  446:     */   public ResultSet getResultSet()
/*  447:     */     throws SQLException
/*  448:     */   {
/*  449: 718 */     if (this.m_PreparedStatement != null) {
/*  450: 719 */       return this.m_PreparedStatement.getResultSet();
/*  451:     */     }
/*  452: 721 */     return null;
/*  453:     */   }
/*  454:     */   
/*  455:     */   public int update(String query)
/*  456:     */     throws SQLException
/*  457:     */   {
/*  458: 733 */     if (!isConnected()) {
/*  459: 734 */       throw new IllegalStateException("Not connected, please connect first!");
/*  460:     */     }
/*  461:     */     Statement statement;
/*  462:     */     Statement statement;
/*  463: 738 */     if (!isCursorScrollable()) {
/*  464: 739 */       statement = this.m_Connection.createStatement(1003, 1007);
/*  465:     */     } else {
/*  466: 742 */       statement = this.m_Connection.createStatement(getSupportedCursorScrollType(), 1007);
/*  467:     */     }
/*  468: 745 */     int result = statement.executeUpdate(query);
/*  469: 746 */     statement.close();
/*  470:     */     
/*  471: 748 */     return result;
/*  472:     */   }
/*  473:     */   
/*  474:     */   public ResultSet select(String query)
/*  475:     */     throws SQLException
/*  476:     */   {
/*  477: 760 */     if (!isConnected()) {
/*  478: 761 */       throw new IllegalStateException("Not connected, please connect first!");
/*  479:     */     }
/*  480:     */     Statement statement;
/*  481:     */     Statement statement;
/*  482: 765 */     if (!isCursorScrollable()) {
/*  483: 766 */       statement = this.m_Connection.createStatement(1003, 1007);
/*  484:     */     } else {
/*  485: 769 */       statement = this.m_Connection.createStatement(getSupportedCursorScrollType(), 1007);
/*  486:     */     }
/*  487: 772 */     ResultSet result = statement.executeQuery(query);
/*  488:     */     
/*  489: 774 */     return result;
/*  490:     */   }
/*  491:     */   
/*  492:     */   public void close(ResultSet rs)
/*  493:     */   {
/*  494:     */     try
/*  495:     */     {
/*  496: 786 */       Statement statement = rs.getStatement();
/*  497: 787 */       rs.close();
/*  498: 788 */       statement.close();
/*  499: 789 */       statement = null;
/*  500: 790 */       rs = null;
/*  501:     */     }
/*  502:     */     catch (Exception e) {}
/*  503:     */   }
/*  504:     */   
/*  505:     */   public void close()
/*  506:     */   {
/*  507: 800 */     if (this.m_PreparedStatement != null) {
/*  508:     */       try
/*  509:     */       {
/*  510: 802 */         this.m_PreparedStatement.close();
/*  511: 803 */         this.m_PreparedStatement = null;
/*  512:     */       }
/*  513:     */       catch (Exception e) {}
/*  514:     */     }
/*  515:     */   }
/*  516:     */   
/*  517:     */   public boolean tableExists(String tableName)
/*  518:     */     throws Exception
/*  519:     */   {
/*  520: 818 */     if (!isConnected()) {
/*  521: 819 */       throw new IllegalStateException("Not connected, please connect first!");
/*  522:     */     }
/*  523: 822 */     if (this.m_Debug) {
/*  524: 823 */       System.err.println("Checking if table " + tableName + " exists...");
/*  525:     */     }
/*  526: 825 */     DatabaseMetaData dbmd = this.m_Connection.getMetaData();
/*  527:     */     ResultSet rs;
/*  528:     */     ResultSet rs;
/*  529: 827 */     if (this.m_checkForUpperCaseNames)
/*  530:     */     {
/*  531: 828 */       rs = dbmd.getTables(null, null, tableName.toUpperCase(), null);
/*  532:     */     }
/*  533:     */     else
/*  534:     */     {
/*  535:     */       ResultSet rs;
/*  536: 829 */       if (this.m_checkForLowerCaseNames) {
/*  537: 830 */         rs = dbmd.getTables(null, null, tableName.toLowerCase(), null);
/*  538:     */       } else {
/*  539: 832 */         rs = dbmd.getTables(null, null, tableName, null);
/*  540:     */       }
/*  541:     */     }
/*  542: 834 */     boolean tableExists = rs.next();
/*  543: 835 */     if (rs.next()) {
/*  544: 836 */       throw new Exception("This table seems to exist more than once!");
/*  545:     */     }
/*  546: 838 */     rs.close();
/*  547: 839 */     if (this.m_Debug) {
/*  548: 840 */       if (tableExists) {
/*  549: 841 */         System.err.println("... " + tableName + " exists");
/*  550:     */       } else {
/*  551: 843 */         System.err.println("... " + tableName + " does not exist");
/*  552:     */       }
/*  553:     */     }
/*  554: 846 */     return tableExists;
/*  555:     */   }
/*  556:     */   
/*  557:     */   public static String processKeyString(String s)
/*  558:     */   {
/*  559: 857 */     return s.replaceAll("\\\\", "/").replaceAll("'", "''");
/*  560:     */   }
/*  561:     */   
/*  562:     */   protected boolean isKeyInTable(String tableName, ResultProducer rp, Object[] key)
/*  563:     */     throws Exception
/*  564:     */   {
/*  565: 873 */     String query = "SELECT Key_Run FROM " + tableName;
/*  566: 874 */     String[] keyNames = rp.getKeyNames();
/*  567: 875 */     if (keyNames.length != key.length) {
/*  568: 876 */       throw new Exception("Key names and key values of different lengths");
/*  569:     */     }
/*  570: 878 */     boolean first = true;
/*  571: 879 */     for (int i = 0; i < key.length; i++) {
/*  572: 880 */       if (key[i] != null)
/*  573:     */       {
/*  574: 881 */         if (first)
/*  575:     */         {
/*  576: 882 */           query = query + " WHERE ";
/*  577: 883 */           first = false;
/*  578:     */         }
/*  579:     */         else
/*  580:     */         {
/*  581: 885 */           query = query + " AND ";
/*  582:     */         }
/*  583: 887 */         query = query + "Key_" + keyNames[i] + '=';
/*  584: 888 */         if ((key[i] instanceof String)) {
/*  585: 889 */           query = query + "'" + processKeyString(key[i].toString()) + "'";
/*  586:     */         } else {
/*  587: 891 */           query = query + key[i].toString();
/*  588:     */         }
/*  589:     */       }
/*  590:     */     }
/*  591: 895 */     boolean retval = false;
/*  592: 896 */     ResultSet rs = select(query);
/*  593: 897 */     if (rs.next())
/*  594:     */     {
/*  595: 898 */       retval = true;
/*  596: 899 */       if (rs.next()) {
/*  597: 900 */         throw new Exception("More than one result entry for result key: " + query);
/*  598:     */       }
/*  599:     */     }
/*  600: 904 */     close(rs);
/*  601: 905 */     return retval;
/*  602:     */   }
/*  603:     */   
/*  604:     */   public Object[] getResultFromTable(String tableName, ResultProducer rp, Object[] key)
/*  605:     */     throws Exception
/*  606:     */   {
/*  607: 921 */     String query = "SELECT ";
/*  608: 922 */     String[] resultNames = rp.getResultNames();
/*  609: 923 */     for (int i = 0; i < resultNames.length; i++)
/*  610:     */     {
/*  611: 924 */       if (i != 0) {
/*  612: 925 */         query = query + ", ";
/*  613:     */       }
/*  614: 927 */       query = query + resultNames[i];
/*  615:     */     }
/*  616: 929 */     query = query + " FROM " + tableName;
/*  617: 930 */     String[] keyNames = rp.getKeyNames();
/*  618: 931 */     if (keyNames.length != key.length) {
/*  619: 932 */       throw new Exception("Key names and key values of different lengths");
/*  620:     */     }
/*  621: 934 */     boolean first = true;
/*  622: 935 */     for (int i = 0; i < key.length; i++) {
/*  623: 936 */       if (key[i] != null)
/*  624:     */       {
/*  625: 937 */         if (first)
/*  626:     */         {
/*  627: 938 */           query = query + " WHERE ";
/*  628: 939 */           first = false;
/*  629:     */         }
/*  630:     */         else
/*  631:     */         {
/*  632: 941 */           query = query + " AND ";
/*  633:     */         }
/*  634: 943 */         query = query + "Key_" + keyNames[i] + '=';
/*  635: 944 */         if ((key[i] instanceof String)) {
/*  636: 945 */           query = query + "'" + processKeyString(key[i].toString()) + "'";
/*  637:     */         } else {
/*  638: 947 */           query = query + key[i].toString();
/*  639:     */         }
/*  640:     */       }
/*  641:     */     }
/*  642: 951 */     ResultSet rs = select(query);
/*  643: 952 */     ResultSetMetaData md = rs.getMetaData();
/*  644: 953 */     int numAttributes = md.getColumnCount();
/*  645: 954 */     if (!rs.next()) {
/*  646: 955 */       throw new Exception("No result for query: " + query);
/*  647:     */     }
/*  648: 958 */     Object[] result = new Object[numAttributes];
/*  649: 959 */     for (int i = 1; i <= numAttributes; i++) {
/*  650: 960 */       switch (translateDBColumnType(md.getColumnTypeName(i)))
/*  651:     */       {
/*  652:     */       case 0: 
/*  653: 962 */         result[(i - 1)] = rs.getString(i);
/*  654: 963 */         if (rs.wasNull()) {
/*  655: 964 */           result[(i - 1)] = null;
/*  656:     */         }
/*  657:     */         break;
/*  658:     */       case 2: 
/*  659:     */       case 7: 
/*  660: 969 */         result[(i - 1)] = new Double(rs.getDouble(i));
/*  661: 970 */         if (rs.wasNull()) {
/*  662: 971 */           result[(i - 1)] = null;
/*  663:     */         }
/*  664:     */         break;
/*  665:     */       default: 
/*  666: 975 */         throw new Exception("Unhandled SQL result type (field " + (i + 1) + "): " + typeName(md.getColumnType(i)));
/*  667:     */       }
/*  668:     */     }
/*  669: 979 */     if (rs.next()) {
/*  670: 980 */       throw new Exception("More than one result entry for result key: " + query);
/*  671:     */     }
/*  672: 983 */     close(rs);
/*  673: 984 */     return result;
/*  674:     */   }
/*  675:     */   
/*  676:     */   public void putResultInTable(String tableName, ResultProducer rp, Object[] key, Object[] result)
/*  677:     */     throws Exception
/*  678:     */   {
/*  679:1000 */     String query = "INSERT INTO " + tableName + " VALUES ( ";
/*  680:1002 */     for (int i = 0; i < key.length; i++)
/*  681:     */     {
/*  682:1003 */       if (i != 0) {
/*  683:1004 */         query = query + ',';
/*  684:     */       }
/*  685:1006 */       if (key[i] != null)
/*  686:     */       {
/*  687:1007 */         if ((key[i] instanceof String)) {
/*  688:1008 */           query = query + "'" + processKeyString(key[i].toString()) + "'";
/*  689:1009 */         } else if ((key[i] instanceof Double)) {
/*  690:1010 */           query = query + safeDoubleToString((Double)key[i]);
/*  691:     */         } else {
/*  692:1012 */           query = query + key[i].toString();
/*  693:     */         }
/*  694:     */       }
/*  695:     */       else {
/*  696:1015 */         query = query + "NULL";
/*  697:     */       }
/*  698:     */     }
/*  699:1018 */     for (Object element : result)
/*  700:     */     {
/*  701:1019 */       query = query + ',';
/*  702:1020 */       if (element != null)
/*  703:     */       {
/*  704:1021 */         if ((element instanceof String)) {
/*  705:1022 */           query = query + "'" + element.toString() + "'";
/*  706:1023 */         } else if ((element instanceof Double)) {
/*  707:1024 */           query = query + safeDoubleToString((Double)element);
/*  708:     */         } else {
/*  709:1026 */           query = query + element.toString();
/*  710:     */         }
/*  711:     */       }
/*  712:     */       else {
/*  713:1031 */         query = query + "NULL";
/*  714:     */       }
/*  715:     */     }
/*  716:1034 */     query = query + ')';
/*  717:1036 */     if (this.m_Debug) {
/*  718:1037 */       System.err.println("Submitting result: " + query);
/*  719:     */     }
/*  720:1039 */     update(query);
/*  721:1040 */     close();
/*  722:     */   }
/*  723:     */   
/*  724:     */   private String safeDoubleToString(Double number)
/*  725:     */   {
/*  726:1052 */     if (number.isNaN()) {
/*  727:1053 */       return "NULL";
/*  728:     */     }
/*  729:1056 */     String orig = number.toString();
/*  730:     */     
/*  731:1058 */     int pos = orig.indexOf('E');
/*  732:1059 */     if ((pos == -1) || (orig.charAt(pos + 1) == '-')) {
/*  733:1060 */       return orig;
/*  734:     */     }
/*  735:1062 */     StringBuffer buff = new StringBuffer(orig);
/*  736:1063 */     buff.insert(pos + 1, '+');
/*  737:1064 */     return new String(buff);
/*  738:     */   }
/*  739:     */   
/*  740:     */   public boolean experimentIndexExists()
/*  741:     */     throws Exception
/*  742:     */   {
/*  743:1075 */     return tableExists("Experiment_index");
/*  744:     */   }
/*  745:     */   
/*  746:     */   public void createExperimentIndex()
/*  747:     */     throws Exception
/*  748:     */   {
/*  749:1084 */     if (this.m_Debug) {
/*  750:1085 */       System.err.println("Creating experiment index table...");
/*  751:     */     }
/*  752:1101 */     String query = "CREATE TABLE Experiment_index ( Experiment_type " + this.m_stringType + "," + "  " + "Experiment_setup" + " " + this.m_stringType + "," + "  " + "Result_table" + " " + this.m_intType + " )";
/*  753:     */     
/*  754:     */ 
/*  755:     */ 
/*  756:     */ 
/*  757:     */ 
/*  758:     */ 
/*  759:1108 */     update(query);
/*  760:1109 */     close();
/*  761:     */   }
/*  762:     */   
/*  763:     */   public String createExperimentIndexEntry(ResultProducer rp)
/*  764:     */     throws Exception
/*  765:     */   {
/*  766:1121 */     if (this.m_Debug) {
/*  767:1122 */       System.err.println("Creating experiment index entry...");
/*  768:     */     }
/*  769:1126 */     int numRows = 0;
/*  770:     */     
/*  771:     */ 
/*  772:     */ 
/*  773:     */ 
/*  774:     */ 
/*  775:     */ 
/*  776:     */ 
/*  777:     */ 
/*  778:     */ 
/*  779:     */ 
/*  780:     */ 
/*  781:     */ 
/*  782:1139 */     String query = "SELECT COUNT(*) FROM Experiment_index";
/*  783:1140 */     ResultSet rs = select(query);
/*  784:1141 */     if (this.m_Debug) {
/*  785:1142 */       System.err.println("...getting number of rows");
/*  786:     */     }
/*  787:1144 */     if (rs.next()) {
/*  788:1145 */       numRows = rs.getInt(1);
/*  789:     */     }
/*  790:1147 */     close(rs);
/*  791:     */     
/*  792:     */ 
/*  793:1150 */     String expType = rp.getClass().getName();
/*  794:1151 */     String expParams = rp.getCompatibilityState();
/*  795:1152 */     query = "INSERT INTO Experiment_index VALUES ('" + expType + "', '" + expParams + "', " + numRows + " )";
/*  796:1154 */     if ((update(query) > 0) && 
/*  797:1155 */       (this.m_Debug)) {
/*  798:1156 */       System.err.println("...create returned resultset");
/*  799:     */     }
/*  800:1159 */     close();
/*  801:1169 */     if (!this.m_setAutoCommit)
/*  802:     */     {
/*  803:1170 */       this.m_Connection.commit();
/*  804:1171 */       this.m_Connection.setAutoCommit(true);
/*  805:     */     }
/*  806:1175 */     String tableName = getResultsTableName(rp);
/*  807:1176 */     if (tableName == null) {
/*  808:1177 */       throw new Exception("Problem adding experiment index entry");
/*  809:     */     }
/*  810:     */     try
/*  811:     */     {
/*  812:1184 */       query = "DROP TABLE " + tableName;
/*  813:1185 */       if (this.m_Debug) {
/*  814:1186 */         System.err.println(query);
/*  815:     */       }
/*  816:1188 */       update(query);
/*  817:     */     }
/*  818:     */     catch (SQLException ex)
/*  819:     */     {
/*  820:1190 */       System.err.println(ex.getMessage());
/*  821:     */     }
/*  822:1192 */     return tableName;
/*  823:     */   }
/*  824:     */   
/*  825:     */   public String getResultsTableName(ResultProducer rp)
/*  826:     */     throws Exception
/*  827:     */   {
/*  828:1206 */     if (this.m_Debug) {
/*  829:1207 */       System.err.println("Getting results table name...");
/*  830:     */     }
/*  831:1209 */     String expType = rp.getClass().getName();
/*  832:1210 */     String expParams = rp.getCompatibilityState();
/*  833:1211 */     String query = "SELECT Result_table FROM Experiment_index WHERE Experiment_type='" + expType + "' AND " + "Experiment_setup" + "='" + expParams + "'";
/*  834:     */     
/*  835:     */ 
/*  836:1214 */     String tableName = null;
/*  837:1215 */     ResultSet rs = select(query);
/*  838:1216 */     if (rs.next())
/*  839:     */     {
/*  840:1217 */       tableName = rs.getString(1);
/*  841:1218 */       if (rs.next()) {
/*  842:1219 */         throw new Exception("More than one index entry for experiment config: " + query);
/*  843:     */       }
/*  844:     */     }
/*  845:1223 */     close(rs);
/*  846:1224 */     if (this.m_Debug) {
/*  847:1225 */       System.err.println("...results table = " + (tableName == null ? "<null>" : new StringBuilder().append("Results").append(tableName).toString()));
/*  848:     */     }
/*  849:1228 */     return "Results" + tableName;
/*  850:     */   }
/*  851:     */   
/*  852:     */   public String createResultsTable(ResultProducer rp, String tableName)
/*  853:     */     throws Exception
/*  854:     */   {
/*  855:1242 */     if (this.m_Debug) {
/*  856:1243 */       System.err.println("Creating results table " + tableName + "...");
/*  857:     */     }
/*  858:1245 */     String query = "CREATE TABLE " + tableName + " ( ";
/*  859:     */     
/*  860:1247 */     String[] names = rp.getKeyNames();
/*  861:1248 */     Object[] types = rp.getKeyTypes();
/*  862:1249 */     if (names.length != types.length) {
/*  863:1250 */       throw new Exception("key names types differ in length");
/*  864:     */     }
/*  865:1252 */     for (int i = 0; i < names.length; i++)
/*  866:     */     {
/*  867:1253 */       query = query + "Key_" + names[i] + " ";
/*  868:1254 */       if ((types[i] instanceof Double)) {
/*  869:1255 */         query = query + this.m_doubleType;
/*  870:1256 */       } else if ((types[i] instanceof String)) {
/*  871:1268 */         query = query + this.m_stringType + " ";
/*  872:     */       } else {
/*  873:1271 */         throw new Exception("Unknown/unsupported field type in key");
/*  874:     */       }
/*  875:1273 */       query = query + ", ";
/*  876:     */     }
/*  877:1276 */     names = rp.getResultNames();
/*  878:1277 */     types = rp.getResultTypes();
/*  879:1278 */     if (names.length != types.length) {
/*  880:1279 */       throw new Exception("result names and types differ in length");
/*  881:     */     }
/*  882:1281 */     for (int i = 0; i < names.length; i++)
/*  883:     */     {
/*  884:1282 */       query = query + names[i] + " ";
/*  885:1283 */       if ((types[i] instanceof Double)) {
/*  886:1284 */         query = query + this.m_doubleType;
/*  887:1285 */       } else if ((types[i] instanceof String)) {
/*  888:1297 */         query = query + this.m_stringType + " ";
/*  889:     */       } else {
/*  890:1300 */         throw new Exception("Unknown/unsupported field type in key");
/*  891:     */       }
/*  892:1302 */       if (i < names.length - 1) {
/*  893:1303 */         query = query + ", ";
/*  894:     */       }
/*  895:     */     }
/*  896:1306 */     query = query + " )";
/*  897:     */     
/*  898:1308 */     update(query);
/*  899:1309 */     if (this.m_Debug) {
/*  900:1310 */       System.err.println("table created");
/*  901:     */     }
/*  902:1312 */     close();
/*  903:1314 */     if (this.m_createIndex)
/*  904:     */     {
/*  905:1315 */       query = "CREATE UNIQUE INDEX Key_IDX ON " + tableName + " (";
/*  906:     */       
/*  907:1317 */       String[] keyNames = rp.getKeyNames();
/*  908:     */       
/*  909:1319 */       boolean first = true;
/*  910:1320 */       for (String keyName : keyNames) {
/*  911:1321 */         if (keyName != null) {
/*  912:1322 */           if (first)
/*  913:     */           {
/*  914:1323 */             first = false;
/*  915:1324 */             query = query + "Key_" + keyName;
/*  916:     */           }
/*  917:     */           else
/*  918:     */           {
/*  919:1326 */             query = query + ",Key_" + keyName;
/*  920:     */           }
/*  921:     */         }
/*  922:     */       }
/*  923:1330 */       query = query + ")";
/*  924:     */       
/*  925:1332 */       update(query);
/*  926:     */     }
/*  927:1334 */     return tableName;
/*  928:     */   }
/*  929:     */   
/*  930:     */   public void setKeywords(String value)
/*  931:     */   {
/*  932:1346 */     this.m_Keywords.clear();
/*  933:     */     
/*  934:1348 */     String[] keywords = value.replaceAll(" ", "").split(",");
/*  935:1349 */     for (int i = 0; i < keywords.length; i++) {
/*  936:1350 */       this.m_Keywords.add(keywords[i].toUpperCase());
/*  937:     */     }
/*  938:     */   }
/*  939:     */   
/*  940:     */   public String getKeywords()
/*  941:     */   {
/*  942:1364 */     Vector<String> list = new Vector(this.m_Keywords);
/*  943:1365 */     Collections.sort(list);
/*  944:     */     
/*  945:1367 */     String result = "";
/*  946:1368 */     for (int i = 0; i < list.size(); i++)
/*  947:     */     {
/*  948:1369 */       if (i > 0) {
/*  949:1370 */         result = result + ",";
/*  950:     */       }
/*  951:1372 */       result = result + (String)list.get(i);
/*  952:     */     }
/*  953:1375 */     return result;
/*  954:     */   }
/*  955:     */   
/*  956:     */   public void setKeywordsMaskChar(String value)
/*  957:     */   {
/*  958:1385 */     this.m_KeywordsMaskChar = value;
/*  959:     */   }
/*  960:     */   
/*  961:     */   public String getKeywordsMaskChar()
/*  962:     */   {
/*  963:1394 */     return this.m_KeywordsMaskChar;
/*  964:     */   }
/*  965:     */   
/*  966:     */   public boolean isKeyword(String s)
/*  967:     */   {
/*  968:1405 */     return this.m_Keywords.contains(s.toUpperCase());
/*  969:     */   }
/*  970:     */   
/*  971:     */   public String maskKeyword(String s)
/*  972:     */   {
/*  973:1418 */     if (isKeyword(s)) {
/*  974:1419 */       return s + this.m_KeywordsMaskChar;
/*  975:     */     }
/*  976:1421 */     return s;
/*  977:     */   }
/*  978:     */   
/*  979:     */   public String getRevision()
/*  980:     */   {
/*  981:1432 */     return RevisionUtils.extract("$Revision: 11885 $");
/*  982:     */   }
/*  983:     */   
/*  984:     */   private static Properties loadProperties(File propsFile)
/*  985:     */   {
/*  986:1445 */     Properties defaultProps = null;
/*  987:     */     try
/*  988:     */     {
/*  989:1447 */       defaultProps = Utils.readProperties("weka/experiment/DatabaseUtils.props");
/*  990:     */     }
/*  991:     */     catch (Exception ex)
/*  992:     */     {
/*  993:1449 */       System.err.println("Warning, unable to read default properties file(s).");
/*  994:1450 */       ex.printStackTrace();
/*  995:     */     }
/*  996:1453 */     if (propsFile == null) {
/*  997:1454 */       return defaultProps;
/*  998:     */     }
/*  999:1456 */     if ((!propsFile.exists()) || (propsFile.isDirectory())) {
/* 1000:1457 */       return defaultProps;
/* 1001:     */     }
/* 1002:     */     Properties result;
/* 1003:     */     try
/* 1004:     */     {
/* 1005:1461 */       result = new Properties(defaultProps);
/* 1006:1462 */       result.load(new FileInputStream(propsFile));
/* 1007:     */     }
/* 1008:     */     catch (Exception e)
/* 1009:     */     {
/* 1010:1464 */       result = null;
/* 1011:1465 */       System.err.println("Failed to load properties file (DatabaseUtils.java) '" + propsFile + "':");
/* 1012:     */       
/* 1013:     */ 
/* 1014:1468 */       e.printStackTrace();
/* 1015:     */     }
/* 1016:1471 */     return result;
/* 1017:     */   }
/* 1018:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.DatabaseUtils
 * JD-Core Version:    0.7.0.1
 */