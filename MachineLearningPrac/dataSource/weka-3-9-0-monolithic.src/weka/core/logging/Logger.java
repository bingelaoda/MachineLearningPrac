/*   1:    */ package weka.core.logging;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.PrintWriter;
/*   5:    */ import java.io.StringWriter;
/*   6:    */ import java.text.SimpleDateFormat;
/*   7:    */ import java.util.Properties;
/*   8:    */ import weka.core.RevisionHandler;
/*   9:    */ import weka.core.Utils;
/*  10:    */ import weka.gui.LogPanel;
/*  11:    */ 
/*  12:    */ public abstract class Logger
/*  13:    */   implements RevisionHandler
/*  14:    */ {
/*  15:    */   public static final String PROPERTIES_FILE = "weka/core/logging/Logging.props";
/*  16:    */   protected static Logger m_Singleton;
/*  17:    */   protected static Properties m_Properties;
/*  18:    */   protected static SimpleDateFormat m_DateFormat;
/*  19:    */   protected Level m_MinLevel;
/*  20:    */   
/*  21:    */   static
/*  22:    */   {
/*  23:    */     try
/*  24:    */     {
/*  25: 50 */       m_Properties = Utils.readProperties("weka/core/logging/Logging.props");
/*  26:    */     }
/*  27:    */     catch (Exception e)
/*  28:    */     {
/*  29: 52 */       System.err.println("Error reading the logging properties 'weka/core/logging/Logging.props': " + e);
/*  30:    */       
/*  31: 54 */       m_Properties = new Properties();
/*  32:    */     }
/*  33:    */   }
/*  34:    */   
/*  35:    */   public Logger()
/*  36:    */   {
/*  37: 66 */     initialize();
/*  38:    */   }
/*  39:    */   
/*  40:    */   protected static String[] getLocation()
/*  41:    */   {
/*  42: 81 */     String[] result = new String[3];
/*  43:    */     
/*  44: 83 */     Throwable t = new Throwable();
/*  45: 84 */     t.fillInStackTrace();
/*  46: 85 */     StackTraceElement[] trace = t.getStackTrace();
/*  47: 87 */     for (int i = 0; i < trace.length; i++) {
/*  48: 89 */       if (!trace[i].getClassName().equals(Logger.class.getName())) {
/*  49: 92 */         if (!trace[i].getClassName().equals(LogPanel.class.getName()))
/*  50:    */         {
/*  51: 96 */           result[0] = trace[i].getClassName();
/*  52: 97 */           result[1] = trace[i].getMethodName();
/*  53: 98 */           result[2] = ("" + trace[i].getLineNumber());
/*  54: 99 */           break;
/*  55:    */         }
/*  56:    */       }
/*  57:    */     }
/*  58:102 */     return result;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public static Logger getSingleton()
/*  62:    */   {
/*  63:113 */     if (m_Singleton == null)
/*  64:    */     {
/*  65:115 */       String classname = m_Properties.getProperty("weka.core.logging.Logger", ConsoleLogger.class.getName());
/*  66:    */       try
/*  67:    */       {
/*  68:119 */         m_Singleton = (Logger)Class.forName(classname).newInstance();
/*  69:    */       }
/*  70:    */       catch (Exception e)
/*  71:    */       {
/*  72:121 */         e.printStackTrace();
/*  73:    */       }
/*  74:125 */       m_DateFormat = new SimpleDateFormat(m_Properties.getProperty("weka.core.logging.DateFormat", "yyyy-MM-dd HH:mm:ss"));
/*  75:    */     }
/*  76:130 */     return m_Singleton;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public static void log(Level level, String msg)
/*  80:    */   {
/*  81:144 */     Logger logger = getSingleton();
/*  82:145 */     if (logger == null) {
/*  83:146 */       return;
/*  84:    */     }
/*  85:148 */     synchronized (logger)
/*  86:    */     {
/*  87:149 */       boolean log = false;
/*  88:150 */       if (logger.getMinLevel() == Level.ALL) {
/*  89:151 */         log = true;
/*  90:152 */       } else if (level.getOrder() >= logger.getMinLevel().getOrder()) {
/*  91:153 */         log = true;
/*  92:    */       }
/*  93:154 */       if (!log) {
/*  94:155 */         return;
/*  95:    */       }
/*  96:156 */       String[] location = getLocation();
/*  97:157 */       logger.doLog(level, msg, location[0], location[1], Integer.parseInt(location[2]));
/*  98:    */     }
/*  99:    */   }
/* 100:    */   
/* 101:    */   public static void log(Level level, Throwable t)
/* 102:    */   {
/* 103:172 */     StringWriter swriter = new StringWriter();
/* 104:173 */     PrintWriter pwriter = new PrintWriter(swriter);
/* 105:174 */     t.printStackTrace(pwriter);
/* 106:175 */     pwriter.close();
/* 107:    */     
/* 108:177 */     log(level, swriter.toString());
/* 109:    */   }
/* 110:    */   
/* 111:    */   protected void initialize()
/* 112:    */   {
/* 113:184 */     this.m_MinLevel = Level.valueOf(m_Properties.getProperty("weka.core.logging.MinLevel", "INFO"));
/* 114:    */   }
/* 115:    */   
/* 116:    */   public Level getMinLevel()
/* 117:    */   {
/* 118:196 */     return this.m_MinLevel;
/* 119:    */   }
/* 120:    */   
/* 121:    */   protected abstract void doLog(Level paramLevel, String paramString1, String paramString2, String paramString3, int paramInt);
/* 122:    */   
/* 123:    */   public static enum Level
/* 124:    */   {
/* 125:220 */     ALL(0),  FINEST(1),  FINER(2),  FINE(3),  INFO(4),  WARNING(5),  SEVERE(6),  OFF(10);
/* 126:    */     
/* 127:    */     private int m_Order;
/* 128:    */     
/* 129:    */     private Level(int order)
/* 130:    */     {
/* 131:245 */       this.m_Order = order;
/* 132:    */     }
/* 133:    */     
/* 134:    */     public int getOrder()
/* 135:    */     {
/* 136:254 */       return this.m_Order;
/* 137:    */     }
/* 138:    */   }
/* 139:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.logging.Logger
 * JD-Core Version:    0.7.0.1
 */