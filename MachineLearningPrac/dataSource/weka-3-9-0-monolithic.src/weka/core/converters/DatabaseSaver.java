/*    1:     */ package weka.core.converters;
/*    2:     */ 
/*    3:     */ import java.io.File;
/*    4:     */ import java.io.IOException;
/*    5:     */ import java.io.PrintStream;
/*    6:     */ import java.sql.ResultSet;
/*    7:     */ import java.sql.SQLException;
/*    8:     */ import java.text.SimpleDateFormat;
/*    9:     */ import java.util.Enumeration;
/*   10:     */ import java.util.Properties;
/*   11:     */ import java.util.Vector;
/*   12:     */ import weka.core.Attribute;
/*   13:     */ import weka.core.Capabilities;
/*   14:     */ import weka.core.Capabilities.Capability;
/*   15:     */ import weka.core.Environment;
/*   16:     */ import weka.core.EnvironmentHandler;
/*   17:     */ import weka.core.Instance;
/*   18:     */ import weka.core.Instances;
/*   19:     */ import weka.core.Option;
/*   20:     */ import weka.core.OptionHandler;
/*   21:     */ import weka.core.OptionMetadata;
/*   22:     */ import weka.core.RevisionUtils;
/*   23:     */ import weka.core.Utils;
/*   24:     */ import weka.gui.FilePropertyMetadata;
/*   25:     */ import weka.gui.PasswordProperty;
/*   26:     */ 
/*   27:     */ public class DatabaseSaver
/*   28:     */   extends AbstractSaver
/*   29:     */   implements BatchConverter, IncrementalConverter, DatabaseConverter, OptionHandler, EnvironmentHandler
/*   30:     */ {
/*   31:     */   static final long serialVersionUID = 863971733782624956L;
/*   32:     */   protected DatabaseConnection m_DataBaseConnection;
/*   33:     */   protected String m_tableName;
/*   34:     */   protected String m_resolvedTableName;
/*   35:     */   protected String m_inputFile;
/*   36:     */   protected String m_createText;
/*   37:     */   protected String m_createDouble;
/*   38:     */   protected String m_createInt;
/*   39:     */   protected String m_createDate;
/*   40:     */   protected SimpleDateFormat m_DateFormat;
/*   41:     */   protected String m_idColumn;
/*   42:     */   protected int m_count;
/*   43:     */   protected boolean m_id;
/*   44:     */   protected boolean m_tabName;
/*   45:     */   protected String m_URL;
/*   46:     */   protected String m_Username;
/*   47: 172 */   protected String m_Password = "";
/*   48: 175 */   protected File m_CustomPropsFile = new File("${user.home}");
/*   49:     */   protected boolean m_truncate;
/*   50:     */   protected transient Environment m_env;
/*   51:     */   
/*   52:     */   public DatabaseSaver()
/*   53:     */     throws Exception
/*   54:     */   {
/*   55: 193 */     resetOptions();
/*   56:     */   }
/*   57:     */   
/*   58:     */   public static void main(String[] options)
/*   59:     */   {
/*   60: 203 */     StringBuffer text = new StringBuffer();
/*   61: 204 */     text.append("\n\nDatabaseSaver options:\n");
/*   62:     */     try
/*   63:     */     {
/*   64: 206 */       DatabaseSaver asv = new DatabaseSaver();
/*   65:     */       try
/*   66:     */       {
/*   67: 208 */         Enumeration<Option> enumi = asv.listOptions();
/*   68: 209 */         while (enumi.hasMoreElements())
/*   69:     */         {
/*   70: 210 */           Option option = (Option)enumi.nextElement();
/*   71: 211 */           text.append(option.synopsis() + '\n');
/*   72: 212 */           text.append(option.description() + '\n');
/*   73:     */         }
/*   74: 214 */         asv.setOptions(options);
/*   75: 215 */         asv.setDestination(asv.getUrl());
/*   76:     */       }
/*   77:     */       catch (Exception ex)
/*   78:     */       {
/*   79: 217 */         ex.printStackTrace();
/*   80:     */       }
/*   81: 230 */       asv.writeBatch();
/*   82:     */     }
/*   83:     */     catch (Exception ex)
/*   84:     */     {
/*   85: 232 */       ex.printStackTrace();
/*   86: 233 */       System.out.println(text);
/*   87:     */     }
/*   88:     */   }
/*   89:     */   
/*   90:     */   private void checkEnv()
/*   91:     */   {
/*   92: 239 */     if (this.m_env == null) {
/*   93: 240 */       this.m_env = Environment.getSystemWide();
/*   94:     */     }
/*   95:     */   }
/*   96:     */   
/*   97:     */   public void setEnvironment(Environment env)
/*   98:     */   {
/*   99: 251 */     this.m_env = env;
/*  100:     */     try
/*  101:     */     {
/*  102: 255 */       this.m_DataBaseConnection = newDatabaseConnection();
/*  103: 256 */       setUrl(this.m_URL);
/*  104: 257 */       setUser(this.m_Username);
/*  105: 258 */       setPassword(this.m_Password);
/*  106:     */     }
/*  107:     */     catch (Exception ex)
/*  108:     */     {
/*  109: 260 */       ex.printStackTrace();
/*  110:     */     }
/*  111:     */   }
/*  112:     */   
/*  113:     */   protected DatabaseConnection newDatabaseConnection()
/*  114:     */     throws Exception
/*  115:     */   {
/*  116: 272 */     DatabaseConnection result = new DatabaseConnection();
/*  117: 273 */     checkEnv();
/*  118: 275 */     if (this.m_CustomPropsFile != null)
/*  119:     */     {
/*  120: 276 */       File pFile = new File(this.m_CustomPropsFile.getPath());
/*  121: 277 */       String pPath = this.m_CustomPropsFile.getPath();
/*  122:     */       try
/*  123:     */       {
/*  124: 280 */         pPath = this.m_env.substitute(pPath);
/*  125: 281 */         pFile = new File(pPath);
/*  126:     */       }
/*  127:     */       catch (Exception ex) {}
/*  128: 284 */       if (pFile.isFile()) {
/*  129: 285 */         result = new DatabaseConnection(pFile);
/*  130:     */       }
/*  131:     */     }
/*  132: 289 */     this.m_createText = result.getProperties().getProperty("CREATE_STRING");
/*  133: 290 */     this.m_createDouble = result.getProperties().getProperty("CREATE_DOUBLE");
/*  134: 291 */     this.m_createInt = result.getProperties().getProperty("CREATE_INT");
/*  135: 292 */     this.m_createDate = result.getProperties().getProperty("CREATE_DATE", "DATETIME");
/*  136:     */     
/*  137: 294 */     this.m_DateFormat = new SimpleDateFormat(result.getProperties().getProperty("DateFormat", "yyyy-MM-dd HH:mm:ss"));
/*  138:     */     
/*  139:     */ 
/*  140: 297 */     this.m_idColumn = result.getProperties().getProperty("idColumn");
/*  141:     */     
/*  142: 299 */     return result;
/*  143:     */   }
/*  144:     */   
/*  145:     */   public void resetOptions()
/*  146:     */   {
/*  147: 308 */     super.resetOptions();
/*  148:     */     
/*  149: 310 */     setRetrieval(0);
/*  150:     */     try
/*  151:     */     {
/*  152: 313 */       if ((this.m_DataBaseConnection != null) && (this.m_DataBaseConnection.isConnected())) {
/*  153: 314 */         this.m_DataBaseConnection.disconnectFromDatabase();
/*  154:     */       }
/*  155: 316 */       this.m_DataBaseConnection = newDatabaseConnection();
/*  156:     */     }
/*  157:     */     catch (Exception ex)
/*  158:     */     {
/*  159: 318 */       printException(ex);
/*  160:     */     }
/*  161: 321 */     this.m_URL = this.m_DataBaseConnection.getDatabaseURL();
/*  162: 322 */     this.m_tableName = "";
/*  163: 323 */     this.m_Username = this.m_DataBaseConnection.getUsername();
/*  164: 324 */     this.m_Password = this.m_DataBaseConnection.getPassword();
/*  165: 325 */     this.m_count = 1;
/*  166: 326 */     this.m_id = false;
/*  167: 327 */     this.m_tabName = true;
/*  168:     */   }
/*  169:     */   
/*  170:     */   public void cancel()
/*  171:     */   {
/*  172: 352 */     if (getWriteMode() == 2)
/*  173:     */     {
/*  174:     */       try
/*  175:     */       {
/*  176: 354 */         this.m_DataBaseConnection.update("DROP TABLE " + this.m_resolvedTableName);
/*  177: 355 */         if (this.m_DataBaseConnection.tableExists(this.m_resolvedTableName)) {
/*  178: 356 */           System.err.println("Table cannot be dropped.");
/*  179:     */         }
/*  180:     */       }
/*  181:     */       catch (Exception ex)
/*  182:     */       {
/*  183: 359 */         printException(ex);
/*  184:     */       }
/*  185: 361 */       resetOptions();
/*  186:     */     }
/*  187:     */   }
/*  188:     */   
/*  189:     */   public String globalInfo()
/*  190:     */   {
/*  191: 372 */     return "Writes to a database (tested with MySQL, InstantDB, HSQLDB).";
/*  192:     */   }
/*  193:     */   
/*  194:     */   @OptionMetadata(displayName="Table name", description="Sets the name of the table", displayOrder=4)
/*  195:     */   public String getTableName()
/*  196:     */   {
/*  197: 384 */     return this.m_tableName;
/*  198:     */   }
/*  199:     */   
/*  200:     */   public void setTableName(String tn)
/*  201:     */   {
/*  202: 394 */     this.m_tableName = tn;
/*  203:     */   }
/*  204:     */   
/*  205:     */   public String tableNameTipText()
/*  206:     */   {
/*  207: 404 */     return "Sets the name of the table.";
/*  208:     */   }
/*  209:     */   
/*  210:     */   @OptionMetadata(displayName="Truncate table", description="Truncate (i.e. drop and recreate) table if it already exists", displayOrder=6)
/*  211:     */   public boolean getTruncate()
/*  212:     */   {
/*  213: 417 */     return this.m_truncate;
/*  214:     */   }
/*  215:     */   
/*  216:     */   public void setTruncate(boolean t)
/*  217:     */   {
/*  218: 427 */     this.m_truncate = t;
/*  219:     */   }
/*  220:     */   
/*  221:     */   public String truncateTipText()
/*  222:     */   {
/*  223: 436 */     return "Truncate (i.e. drop and recreate) table if it already exists";
/*  224:     */   }
/*  225:     */   
/*  226:     */   @OptionMetadata(displayName="Automatic primary key", description="If set to true, a primary key column is generated automatically (containing the row number as INTEGER). The name of the key is read from DatabaseUtils (idColumn) This primary key can be used for incremental loading (requires an unique key). This primary key will not be loaded as an attribute.", displayOrder=7)
/*  227:     */   public boolean getAutoKeyGeneration()
/*  228:     */   {
/*  229: 452 */     return this.m_id;
/*  230:     */   }
/*  231:     */   
/*  232:     */   public void setAutoKeyGeneration(boolean flag)
/*  233:     */   {
/*  234: 462 */     this.m_id = flag;
/*  235:     */   }
/*  236:     */   
/*  237:     */   public String autoKeyGenerationTipText()
/*  238:     */   {
/*  239: 472 */     return "If set to true, a primary key column is generated automatically (containing the row number as INTEGER). The name of the key is read from DatabaseUtils (idColumn) This primary key can be used for incremental loading (requires an unique key). This primary key will not be loaded as an attribute.";
/*  240:     */   }
/*  241:     */   
/*  242:     */   @OptionMetadata(displayName="Use relation name", description="If set to true, the relation name will be used as name for the database table. Otherwise the user has to provide a table name.", displayOrder=5)
/*  243:     */   public boolean getRelationForTableName()
/*  244:     */   {
/*  245: 489 */     return this.m_tabName;
/*  246:     */   }
/*  247:     */   
/*  248:     */   public void setRelationForTableName(boolean flag)
/*  249:     */   {
/*  250: 500 */     this.m_tabName = flag;
/*  251:     */   }
/*  252:     */   
/*  253:     */   public String relationForTableNameTipText()
/*  254:     */   {
/*  255: 510 */     return "If set to true, the relation name will be used as name for the database table. Otherwise the user has to provide a table name.";
/*  256:     */   }
/*  257:     */   
/*  258:     */   @OptionMetadata(displayName="Database URL", description="The URL of the database", displayOrder=1)
/*  259:     */   public String getUrl()
/*  260:     */   {
/*  261: 523 */     return this.m_URL;
/*  262:     */   }
/*  263:     */   
/*  264:     */   public void setUrl(String url)
/*  265:     */   {
/*  266: 533 */     checkEnv();
/*  267:     */     
/*  268: 535 */     this.m_URL = url;
/*  269: 536 */     String uCopy = this.m_URL;
/*  270:     */     try
/*  271:     */     {
/*  272: 538 */       uCopy = this.m_env.substitute(uCopy);
/*  273:     */     }
/*  274:     */     catch (Exception ex) {}
/*  275: 541 */     this.m_DataBaseConnection.setDatabaseURL(uCopy);
/*  276:     */   }
/*  277:     */   
/*  278:     */   public String urlTipText()
/*  279:     */   {
/*  280: 551 */     return "The URL of the database";
/*  281:     */   }
/*  282:     */   
/*  283:     */   public String getUser()
/*  284:     */   {
/*  285: 563 */     return this.m_Username;
/*  286:     */   }
/*  287:     */   
/*  288:     */   @OptionMetadata(displayName="Username", description="The user name for the database", displayOrder=2)
/*  289:     */   public void setUser(String user)
/*  290:     */   {
/*  291: 575 */     checkEnv();
/*  292: 576 */     this.m_Username = user;
/*  293: 577 */     String userCopy = user;
/*  294:     */     try
/*  295:     */     {
/*  296: 579 */       userCopy = this.m_env.substitute(userCopy);
/*  297:     */     }
/*  298:     */     catch (Exception ex) {}
/*  299: 583 */     this.m_DataBaseConnection.setUsername(userCopy);
/*  300:     */   }
/*  301:     */   
/*  302:     */   public String userTipText()
/*  303:     */   {
/*  304: 593 */     return "The user name for the database";
/*  305:     */   }
/*  306:     */   
/*  307:     */   @OptionMetadata(displayName="Password", description="The database password", displayOrder=3)
/*  308:     */   @PasswordProperty
/*  309:     */   public String getPassword()
/*  310:     */   {
/*  311: 606 */     return this.m_Password;
/*  312:     */   }
/*  313:     */   
/*  314:     */   public void setPassword(String password)
/*  315:     */   {
/*  316: 616 */     checkEnv();
/*  317: 617 */     this.m_Password = password;
/*  318: 618 */     String passCopy = password;
/*  319:     */     try
/*  320:     */     {
/*  321: 620 */       passCopy = this.m_env.substitute(passCopy);
/*  322:     */     }
/*  323:     */     catch (Exception ex) {}
/*  324: 623 */     this.m_DataBaseConnection.setPassword(password);
/*  325:     */   }
/*  326:     */   
/*  327:     */   public String passwordTipText()
/*  328:     */   {
/*  329: 633 */     return "The database password";
/*  330:     */   }
/*  331:     */   
/*  332:     */   @OptionMetadata(displayName="DB config file", description="The custom properties that the user can use to override the default ones.", displayOrder=8)
/*  333:     */   @FilePropertyMetadata(fileChooserDialogType=0, directoriesOnly=false)
/*  334:     */   public File getCustomPropsFile()
/*  335:     */   {
/*  336: 649 */     return this.m_CustomPropsFile;
/*  337:     */   }
/*  338:     */   
/*  339:     */   public void setCustomPropsFile(File value)
/*  340:     */   {
/*  341: 659 */     this.m_CustomPropsFile = value;
/*  342:     */   }
/*  343:     */   
/*  344:     */   public String customPropsFileTipText()
/*  345:     */   {
/*  346: 668 */     return "The custom properties that the user can use to override the default ones.";
/*  347:     */   }
/*  348:     */   
/*  349:     */   public void setDestination(String url, String userName, String password)
/*  350:     */   {
/*  351:     */     try
/*  352:     */     {
/*  353: 681 */       checkEnv();
/*  354:     */       
/*  355: 683 */       this.m_DataBaseConnection = newDatabaseConnection();
/*  356: 684 */       setUrl(url);
/*  357: 685 */       setUser(userName);
/*  358: 686 */       setPassword(password);
/*  359:     */     }
/*  360:     */     catch (Exception ex)
/*  361:     */     {
/*  362: 691 */       printException(ex);
/*  363:     */     }
/*  364:     */   }
/*  365:     */   
/*  366:     */   public void setDestination(String url)
/*  367:     */   {
/*  368:     */     try
/*  369:     */     {
/*  370: 703 */       checkEnv();
/*  371:     */       
/*  372: 705 */       this.m_DataBaseConnection = newDatabaseConnection();
/*  373:     */       
/*  374: 707 */       setUrl(url);
/*  375: 708 */       setUser(this.m_Username);
/*  376: 709 */       setPassword(this.m_Password);
/*  377:     */     }
/*  378:     */     catch (Exception ex)
/*  379:     */     {
/*  380: 713 */       printException(ex);
/*  381:     */     }
/*  382:     */   }
/*  383:     */   
/*  384:     */   public void setDestination()
/*  385:     */   {
/*  386:     */     try
/*  387:     */     {
/*  388: 721 */       checkEnv();
/*  389:     */       
/*  390: 723 */       this.m_DataBaseConnection = newDatabaseConnection();
/*  391: 724 */       setUser(this.m_Username);
/*  392: 725 */       setPassword(this.m_Password);
/*  393:     */     }
/*  394:     */     catch (Exception ex)
/*  395:     */     {
/*  396: 729 */       printException(ex);
/*  397:     */     }
/*  398:     */   }
/*  399:     */   
/*  400:     */   public Capabilities getCapabilities()
/*  401:     */   {
/*  402: 741 */     Capabilities result = super.getCapabilities();
/*  403:     */     
/*  404:     */ 
/*  405: 744 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  406: 745 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  407: 746 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  408: 747 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  409: 748 */     result.enable(Capabilities.Capability.STRING_ATTRIBUTES);
/*  410:     */     
/*  411:     */ 
/*  412: 751 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  413: 752 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*  414: 753 */     result.enable(Capabilities.Capability.DATE_CLASS);
/*  415: 754 */     result.enable(Capabilities.Capability.STRING_CLASS);
/*  416: 755 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  417: 756 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  418:     */     
/*  419: 758 */     return result;
/*  420:     */   }
/*  421:     */   
/*  422:     */   public void connectToDatabase()
/*  423:     */   {
/*  424:     */     try
/*  425:     */     {
/*  426: 768 */       if (!this.m_DataBaseConnection.isConnected()) {
/*  427: 769 */         this.m_DataBaseConnection.connectToDatabase();
/*  428:     */       }
/*  429:     */     }
/*  430:     */     catch (Exception ex)
/*  431:     */     {
/*  432: 772 */       printException(ex);
/*  433:     */     }
/*  434:     */   }
/*  435:     */   
/*  436:     */   private void writeStructure()
/*  437:     */     throws Exception
/*  438:     */   {
/*  439: 784 */     StringBuffer query = new StringBuffer();
/*  440: 785 */     Instances structure = getInstances();
/*  441: 786 */     query.append("CREATE TABLE ");
/*  442: 787 */     this.m_resolvedTableName = this.m_env.substitute(this.m_tableName);
/*  443: 788 */     if ((this.m_tabName) || (this.m_resolvedTableName.equals(""))) {
/*  444: 789 */       this.m_resolvedTableName = this.m_DataBaseConnection.maskKeyword(structure.relationName());
/*  445:     */     }
/*  446: 792 */     if (this.m_DataBaseConnection.getUpperCase())
/*  447:     */     {
/*  448: 793 */       this.m_resolvedTableName = this.m_resolvedTableName.toUpperCase();
/*  449: 794 */       this.m_createInt = this.m_createInt.toUpperCase();
/*  450: 795 */       this.m_createDouble = this.m_createDouble.toUpperCase();
/*  451: 796 */       this.m_createText = this.m_createText.toUpperCase();
/*  452: 797 */       this.m_createDate = this.m_createDate.toUpperCase();
/*  453:     */     }
/*  454: 799 */     this.m_resolvedTableName = this.m_resolvedTableName.replaceAll("[^\\w]", "_");
/*  455: 800 */     this.m_resolvedTableName = this.m_DataBaseConnection.maskKeyword(this.m_resolvedTableName);
/*  456: 801 */     query.append(this.m_resolvedTableName);
/*  457: 802 */     if (structure.numAttributes() == 0) {
/*  458: 803 */       throw new Exception("Instances have no attribute.");
/*  459:     */     }
/*  460: 805 */     query.append(" ( ");
/*  461: 807 */     if (this.m_DataBaseConnection.tableExists(this.m_resolvedTableName))
/*  462:     */     {
/*  463: 808 */       if (!this.m_truncate)
/*  464:     */       {
/*  465: 809 */         System.err.println("[DatabaseSaver] Table '" + this.m_resolvedTableName + "' already exists - will append data...");
/*  466: 814 */         if ((getRetrieval() == 2) && (this.m_id))
/*  467:     */         {
/*  468: 815 */           String countS = "SELECT COUNT(*) FROM " + this.m_resolvedTableName;
/*  469: 816 */           this.m_DataBaseConnection.execute(countS);
/*  470: 817 */           ResultSet countRS = this.m_DataBaseConnection.getResultSet();
/*  471: 818 */           countRS.next();
/*  472: 819 */           this.m_count = countRS.getInt(1);
/*  473: 820 */           countRS.close();
/*  474: 821 */           this.m_count += 1;
/*  475:     */         }
/*  476: 824 */         return;
/*  477:     */       }
/*  478: 826 */       String trunc = "DROP TABLE " + this.m_resolvedTableName;
/*  479: 827 */       this.m_DataBaseConnection.execute(trunc);
/*  480:     */     }
/*  481: 830 */     if (this.m_id)
/*  482:     */     {
/*  483: 831 */       if (this.m_DataBaseConnection.getUpperCase()) {
/*  484: 832 */         this.m_idColumn = this.m_idColumn.toUpperCase();
/*  485:     */       }
/*  486: 834 */       query.append(this.m_DataBaseConnection.maskKeyword(this.m_idColumn));
/*  487: 835 */       query.append(" ");
/*  488: 836 */       query.append(this.m_createInt);
/*  489: 837 */       query.append(" PRIMARY KEY,");
/*  490:     */     }
/*  491: 839 */     for (int i = 0; i < structure.numAttributes(); i++)
/*  492:     */     {
/*  493: 840 */       Attribute att = structure.attribute(i);
/*  494: 841 */       String attName = att.name();
/*  495: 842 */       attName = attName.replaceAll("[^\\w]", "_");
/*  496: 843 */       attName = this.m_DataBaseConnection.maskKeyword(attName);
/*  497: 844 */       if (this.m_DataBaseConnection.getUpperCase()) {
/*  498: 845 */         query.append(attName.toUpperCase());
/*  499:     */       } else {
/*  500: 847 */         query.append(attName);
/*  501:     */       }
/*  502: 849 */       if (att.isDate()) {
/*  503: 850 */         query.append(" " + this.m_createDate);
/*  504: 852 */       } else if (att.isNumeric()) {
/*  505: 853 */         query.append(" " + this.m_createDouble);
/*  506:     */       } else {
/*  507: 855 */         query.append(" " + this.m_createText);
/*  508:     */       }
/*  509: 858 */       if (i != structure.numAttributes() - 1) {
/*  510: 859 */         query.append(", ");
/*  511:     */       }
/*  512:     */     }
/*  513: 862 */     query.append(" )");
/*  514:     */     
/*  515: 864 */     this.m_DataBaseConnection.update(query.toString());
/*  516: 865 */     this.m_DataBaseConnection.close();
/*  517: 866 */     if (!this.m_DataBaseConnection.tableExists(this.m_resolvedTableName)) {
/*  518: 867 */       throw new IOException("Table cannot be built.");
/*  519:     */     }
/*  520:     */   }
/*  521:     */   
/*  522:     */   private void writeInstance(Instance inst)
/*  523:     */     throws Exception
/*  524:     */   {
/*  525: 879 */     StringBuffer insert = new StringBuffer();
/*  526: 880 */     insert.append("INSERT INTO ");
/*  527: 881 */     insert.append(this.m_resolvedTableName);
/*  528: 882 */     insert.append(" VALUES ( ");
/*  529: 883 */     if (this.m_id)
/*  530:     */     {
/*  531: 884 */       insert.append(this.m_count);
/*  532: 885 */       insert.append(", ");
/*  533: 886 */       this.m_count += 1;
/*  534:     */     }
/*  535: 888 */     for (int j = 0; j < inst.numAttributes(); j++)
/*  536:     */     {
/*  537: 889 */       if (inst.isMissing(j))
/*  538:     */       {
/*  539: 890 */         insert.append("NULL");
/*  540:     */       }
/*  541: 892 */       else if (inst.attribute(j).isDate())
/*  542:     */       {
/*  543: 893 */         insert.append("'" + this.m_DateFormat.format(Long.valueOf(inst.value(j))) + "'");
/*  544:     */       }
/*  545: 894 */       else if (inst.attribute(j).isNumeric())
/*  546:     */       {
/*  547: 895 */         insert.append(inst.value(j));
/*  548:     */       }
/*  549:     */       else
/*  550:     */       {
/*  551: 897 */         String stringInsert = "'" + inst.stringValue(j) + "'";
/*  552: 898 */         if (stringInsert.length() > 2) {
/*  553: 899 */           stringInsert = stringInsert.replaceAll("''", "'");
/*  554:     */         }
/*  555: 901 */         insert.append(stringInsert);
/*  556:     */       }
/*  557: 904 */       if (j != inst.numAttributes() - 1) {
/*  558: 905 */         insert.append(", ");
/*  559:     */       }
/*  560:     */     }
/*  561: 908 */     insert.append(" )");
/*  562: 910 */     if (this.m_DataBaseConnection.update(insert.toString()) < 1) {
/*  563: 911 */       throw new IOException("Tuple cannot be inserted.");
/*  564:     */     }
/*  565: 913 */     this.m_DataBaseConnection.close();
/*  566:     */   }
/*  567:     */   
/*  568:     */   public void writeIncremental(Instance inst)
/*  569:     */     throws IOException
/*  570:     */   {
/*  571: 928 */     int writeMode = getWriteMode();
/*  572: 929 */     Instances structure = getInstances();
/*  573: 931 */     if (this.m_DataBaseConnection == null) {
/*  574: 932 */       throw new IOException("No database has been set up.");
/*  575:     */     }
/*  576: 934 */     if (getRetrieval() == 1) {
/*  577: 935 */       throw new IOException("Batch and incremental saving cannot be mixed.");
/*  578:     */     }
/*  579: 937 */     setRetrieval(2);
/*  580:     */     try
/*  581:     */     {
/*  582: 940 */       if (!this.m_DataBaseConnection.isConnected()) {
/*  583: 941 */         connectToDatabase();
/*  584:     */       }
/*  585: 943 */       if (writeMode == 1)
/*  586:     */       {
/*  587: 944 */         if (structure == null)
/*  588:     */         {
/*  589: 945 */           setWriteMode(2);
/*  590: 946 */           if (inst != null) {
/*  591: 947 */             throw new Exception("Structure(Header Information) has to be set in advance");
/*  592:     */           }
/*  593:     */         }
/*  594:     */         else
/*  595:     */         {
/*  596: 951 */           setWriteMode(3);
/*  597:     */         }
/*  598: 953 */         writeMode = getWriteMode();
/*  599:     */       }
/*  600: 955 */       if (writeMode == 2) {
/*  601: 956 */         cancel();
/*  602:     */       }
/*  603: 958 */       if (writeMode == 3)
/*  604:     */       {
/*  605: 959 */         setWriteMode(0);
/*  606: 960 */         writeStructure();
/*  607: 961 */         writeMode = getWriteMode();
/*  608:     */       }
/*  609: 963 */       if (writeMode == 0)
/*  610:     */       {
/*  611: 964 */         if (structure == null) {
/*  612: 965 */           throw new IOException("No instances information available.");
/*  613:     */         }
/*  614: 967 */         if (inst != null)
/*  615:     */         {
/*  616: 969 */           writeInstance(inst);
/*  617:     */         }
/*  618:     */         else
/*  619:     */         {
/*  620: 972 */           this.m_DataBaseConnection.disconnectFromDatabase();
/*  621: 973 */           resetStructure();
/*  622: 974 */           this.m_count = 1;
/*  623:     */         }
/*  624:     */       }
/*  625:     */     }
/*  626:     */     catch (Exception ex)
/*  627:     */     {
/*  628: 978 */       printException(ex);
/*  629:     */     }
/*  630:     */   }
/*  631:     */   
/*  632:     */   public void writeBatch()
/*  633:     */     throws IOException
/*  634:     */   {
/*  635: 990 */     Instances instances = getInstances();
/*  636: 991 */     if (instances == null) {
/*  637: 992 */       throw new IOException("No instances to save");
/*  638:     */     }
/*  639: 994 */     if (getRetrieval() == 2) {
/*  640: 995 */       throw new IOException("Batch and incremental saving cannot be mixed.");
/*  641:     */     }
/*  642: 997 */     if (this.m_DataBaseConnection == null) {
/*  643: 998 */       throw new IOException("No database has been set up.");
/*  644:     */     }
/*  645:1000 */     setRetrieval(1);
/*  646:     */     try
/*  647:     */     {
/*  648:1002 */       if (!this.m_DataBaseConnection.isConnected()) {
/*  649:1003 */         connectToDatabase();
/*  650:     */       }
/*  651:1005 */       setWriteMode(0);
/*  652:1006 */       writeStructure();
/*  653:1007 */       for (int i = 0; i < instances.numInstances(); i++) {
/*  654:1008 */         writeInstance(instances.instance(i));
/*  655:     */       }
/*  656:1010 */       this.m_DataBaseConnection.disconnectFromDatabase();
/*  657:1011 */       setWriteMode(1);
/*  658:1012 */       resetStructure();
/*  659:1013 */       this.m_count = 1;
/*  660:     */     }
/*  661:     */     catch (Exception ex)
/*  662:     */     {
/*  663:1015 */       printException(ex);
/*  664:     */     }
/*  665:     */   }
/*  666:     */   
/*  667:     */   private void printException(Exception ex)
/*  668:     */   {
/*  669:1026 */     System.out.println("\n--- Exception caught ---\n");
/*  670:1027 */     while (ex != null)
/*  671:     */     {
/*  672:1028 */       System.out.println("Message:   " + ex.getMessage());
/*  673:1029 */       if ((ex instanceof SQLException))
/*  674:     */       {
/*  675:1030 */         System.out.println("SQLState:  " + ((SQLException)ex).getSQLState());
/*  676:1031 */         System.out.println("ErrorCode: " + ((SQLException)ex).getErrorCode());
/*  677:1032 */         ex = ((SQLException)ex).getNextException();
/*  678:     */       }
/*  679:     */       else
/*  680:     */       {
/*  681:1034 */         ex = null;
/*  682:     */       }
/*  683:1036 */       System.out.println("");
/*  684:     */     }
/*  685:     */   }
/*  686:     */   
/*  687:     */   public String[] getOptions()
/*  688:     */   {
/*  689:1048 */     Vector<String> options = new Vector();
/*  690:1050 */     if ((getUrl() != null) && (getUrl().length() != 0))
/*  691:     */     {
/*  692:1051 */       options.add("-url");
/*  693:1052 */       options.add(getUrl());
/*  694:     */     }
/*  695:1055 */     if ((getUser() != null) && (getUser().length() != 0))
/*  696:     */     {
/*  697:1056 */       options.add("-user");
/*  698:1057 */       options.add(getUser());
/*  699:     */     }
/*  700:1060 */     if ((getPassword() != null) && (getPassword().length() != 0))
/*  701:     */     {
/*  702:1061 */       options.add("-password");
/*  703:1062 */       options.add(getPassword());
/*  704:     */     }
/*  705:1065 */     if ((this.m_tableName != null) && (this.m_tableName.length() != 0))
/*  706:     */     {
/*  707:1066 */       options.add("-T");
/*  708:1067 */       options.add(this.m_tableName);
/*  709:     */     }
/*  710:1070 */     if (this.m_truncate) {
/*  711:1071 */       options.add("-truncate");
/*  712:     */     }
/*  713:1074 */     if (this.m_id) {
/*  714:1075 */       options.add("-P");
/*  715:     */     }
/*  716:1078 */     if ((this.m_inputFile != null) && (this.m_inputFile.length() != 0))
/*  717:     */     {
/*  718:1079 */       options.add("-i");
/*  719:1080 */       options.add(this.m_inputFile);
/*  720:     */     }
/*  721:1083 */     if ((this.m_CustomPropsFile != null) && (!this.m_CustomPropsFile.isDirectory()))
/*  722:     */     {
/*  723:1084 */       options.add("-custom-props");
/*  724:1085 */       options.add(this.m_CustomPropsFile.toString());
/*  725:     */     }
/*  726:1088 */     return (String[])options.toArray(new String[options.size()]);
/*  727:     */   }
/*  728:     */   
/*  729:     */   public void setOptions(String[] options)
/*  730:     */     throws Exception
/*  731:     */   {
/*  732:1156 */     resetOptions();
/*  733:     */     
/*  734:1158 */     String tmpStr = Utils.getOption("url", options);
/*  735:1159 */     if (tmpStr.length() != 0) {
/*  736:1160 */       setUrl(tmpStr);
/*  737:     */     }
/*  738:1163 */     tmpStr = Utils.getOption("user", options);
/*  739:1164 */     if (tmpStr.length() != 0) {
/*  740:1165 */       setUser(tmpStr);
/*  741:     */     }
/*  742:1168 */     tmpStr = Utils.getOption("password", options);
/*  743:1169 */     if (tmpStr.length() != 0) {
/*  744:1170 */       setPassword(tmpStr);
/*  745:     */     }
/*  746:1173 */     String tableString = Utils.getOption('T', options);
/*  747:     */     
/*  748:1175 */     this.m_truncate = Utils.getFlag("truncate", options);
/*  749:     */     
/*  750:1177 */     String inputString = Utils.getOption('i', options);
/*  751:1179 */     if (tableString.length() != 0)
/*  752:     */     {
/*  753:1180 */       this.m_tableName = tableString;
/*  754:1181 */       this.m_tabName = false;
/*  755:     */     }
/*  756:1184 */     this.m_id = Utils.getFlag('P', options);
/*  757:1186 */     if (inputString.length() != 0) {
/*  758:     */       try
/*  759:     */       {
/*  760:1188 */         this.m_inputFile = inputString;
/*  761:1189 */         ArffLoader al = new ArffLoader();
/*  762:1190 */         File inputFile = new File(inputString);
/*  763:1191 */         al.setSource(inputFile);
/*  764:1192 */         setInstances(al.getDataSet());
/*  765:1194 */         if (tableString.length() == 0) {
/*  766:1195 */           this.m_tableName = getInstances().relationName();
/*  767:     */         }
/*  768:     */       }
/*  769:     */       catch (Exception ex)
/*  770:     */       {
/*  771:1198 */         printException(ex);
/*  772:1199 */         ex.printStackTrace();
/*  773:     */       }
/*  774:     */     }
/*  775:1203 */     tmpStr = Utils.getOption("custom-props", options);
/*  776:1204 */     if (tmpStr.length() == 0) {
/*  777:1205 */       setCustomPropsFile(null);
/*  778:     */     } else {
/*  779:1207 */       setCustomPropsFile(new File(tmpStr));
/*  780:     */     }
/*  781:1210 */     Utils.checkForRemainingOptions(options);
/*  782:     */   }
/*  783:     */   
/*  784:     */   public Enumeration<Option> listOptions()
/*  785:     */   {
/*  786:1221 */     Vector<Option> newVector = new Vector();
/*  787:     */     
/*  788:1223 */     newVector.addElement(new Option("\tThe JDBC URL to connect to.\n\t(default: from DatabaseUtils.props file)", "url", 1, "-url <JDBC URL>"));
/*  789:     */     
/*  790:     */ 
/*  791:     */ 
/*  792:1227 */     newVector.addElement(new Option("\tThe user to connect with to the database.\n\t(default: none)", "user", 1, "-user <name>"));
/*  793:     */     
/*  794:     */ 
/*  795:     */ 
/*  796:1231 */     newVector.addElement(new Option("\tThe password to connect with to the database.\n\t(default: none)", "password", 1, "-password <password>"));
/*  797:     */     
/*  798:     */ 
/*  799:     */ 
/*  800:     */ 
/*  801:1236 */     newVector.addElement(new Option("\tThe name of the table.\n\t(default: the relation name)", "T", 1, "-T <table name>"));
/*  802:     */     
/*  803:     */ 
/*  804:1239 */     newVector.addElement(new Option("\tTruncate (i.e. delete any data) in table before inserting", "truncate", 0, "-truncate"));
/*  805:     */     
/*  806:     */ 
/*  807:     */ 
/*  808:1243 */     newVector.addElement(new Option("\tAdd an ID column as primary key. The name is specified\n\tin the DatabaseUtils file ('idColumn'). The DatabaseLoader\n\twon't load this column.", "P", 0, "-P"));
/*  809:     */     
/*  810:     */ 
/*  811:     */ 
/*  812:     */ 
/*  813:1248 */     newVector.add(new Option("\tThe custom properties file to use instead of default ones,\n\tcontaining the database parameters.\n\t(default: none)", "custom-props", 1, "-custom-props <file>"));
/*  814:     */     
/*  815:     */ 
/*  816:     */ 
/*  817:     */ 
/*  818:1253 */     newVector.addElement(new Option("\tInput file in arff format that should be saved in database.", "i", 1, "-i <input file name>"));
/*  819:     */     
/*  820:     */ 
/*  821:     */ 
/*  822:1257 */     return newVector.elements();
/*  823:     */   }
/*  824:     */   
/*  825:     */   public String getRevision()
/*  826:     */   {
/*  827:1267 */     return RevisionUtils.extract("$Revision: 12418 $");
/*  828:     */   }
/*  829:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.DatabaseSaver
 * JD-Core Version:    0.7.0.1
 */