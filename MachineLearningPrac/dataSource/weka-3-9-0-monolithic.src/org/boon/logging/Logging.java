/*   1:    */ package org.boon.logging;
/*   2:    */ 
/*   3:    */ import java.lang.ref.WeakReference;
/*   4:    */ import java.util.concurrent.ConcurrentHashMap;
/*   5:    */ import java.util.concurrent.ConcurrentMap;
/*   6:    */ import org.boon.core.Sys;
/*   7:    */ 
/*   8:    */ public class Logging
/*   9:    */ {
/*  10:    */   private static final Context _context;
/*  11: 45 */   private static WeakReference<Context> weakContext = new WeakReference(null);
/*  12:    */   public static final String LOGGER_FACTORY_CLASS_NAME = "org.boon.logger-logger-factory-class-name";
/*  13:    */   
/*  14:    */   private static class Context
/*  15:    */   {
/*  16:    */     private static volatile LoggerFactory factory;
/*  17: 53 */     private static final ConcurrentMap<String, LoggerDelegate> loggers = new ConcurrentHashMap();
/*  18: 56 */     private static final ConcurrentMap<String, ConfigurableLogger> configurableLoggers = new ConcurrentHashMap();
/*  19:    */   }
/*  20:    */   
/*  21:    */   private static Context context()
/*  22:    */   {
/*  23: 80 */     if (_context != null) {
/*  24: 81 */       return _context;
/*  25:    */     }
/*  26: 83 */     Context context = (Context)weakContext.get();
/*  27: 84 */     if (context == null)
/*  28:    */     {
/*  29: 85 */       context = new Context(null);
/*  30: 86 */       weakContext = new WeakReference(context);
/*  31:    */     }
/*  32: 88 */     return context;
/*  33:    */   }
/*  34:    */   
/*  35:    */   static
/*  36:    */   {
/*  37: 64 */     boolean noStatics = Boolean.getBoolean("org.boon.noStatics");
/*  38: 65 */     if ((noStatics) || (Sys.inContainer()))
/*  39:    */     {
/*  40: 67 */       _context = null;
/*  41: 68 */       weakContext = new WeakReference(new Context(null));
/*  42:    */     }
/*  43:    */     else
/*  44:    */     {
/*  45: 71 */       _context = new Context(null);
/*  46:    */     }
/*  47: 97 */     init();
/*  48:    */   }
/*  49:    */   
/*  50:    */   public static synchronized void init()
/*  51:    */   {
/*  52:103 */     String className = JDKLoggerFactory.class.getName();
/*  53:    */     try
/*  54:    */     {
/*  55:105 */       className = System.getProperty("org.boon.logger-logger-factory-class-name");
/*  56:    */     }
/*  57:    */     catch (Exception e) {}
/*  58:    */     LoggerFactory factory;
/*  59:109 */     if (className != null)
/*  60:    */     {
/*  61:110 */       ClassLoader loader = Thread.currentThread().getContextClassLoader();
/*  62:    */       try
/*  63:    */       {
/*  64:112 */         Class<?> clz = loader.loadClass(className);
/*  65:113 */         factory = (LoggerFactory)clz.newInstance();
/*  66:    */       }
/*  67:    */       catch (Exception e)
/*  68:    */       {
/*  69:    */         LoggerFactory factory;
/*  70:115 */         throw new IllegalArgumentException("Error instantiating transformer class \"" + className + "\"", e);
/*  71:    */       }
/*  72:    */     }
/*  73:    */     else
/*  74:    */     {
/*  75:119 */       factory = new JDKLoggerFactory();
/*  76:    */     }
/*  77:122 */     context();Context.access$102(factory);
/*  78:    */   }
/*  79:    */   
/*  80:    */   public static ConfigurableLogger configurableLogger(Class<?> clazz)
/*  81:    */   {
/*  82:127 */     return configurableLogger(clazz.getName());
/*  83:    */   }
/*  84:    */   
/*  85:    */   public static ConfigurableLogger configurableLogger(String name)
/*  86:    */   {
/*  87:132 */     context();ConfigurableLogger loggerDelegate = (ConfigurableLogger)Context.configurableLoggers.get(name);
/*  88:134 */     if (loggerDelegate == null)
/*  89:    */     {
/*  90:136 */       loggerDelegate = new ConfigurableLogger(logger(name));
/*  91:    */       
/*  92:138 */       context();ConfigurableLogger oldLoggerDelegate = (ConfigurableLogger)Context.configurableLoggers.putIfAbsent(name, loggerDelegate);
/*  93:140 */       if (oldLoggerDelegate != null) {
/*  94:141 */         loggerDelegate = oldLoggerDelegate;
/*  95:    */       }
/*  96:    */     }
/*  97:145 */     return loggerDelegate;
/*  98:    */   }
/*  99:    */   
/* 100:    */   public static LoggerDelegate logger(Class<?> clazz)
/* 101:    */   {
/* 102:150 */     return logger(clazz.getName());
/* 103:    */   }
/* 104:    */   
/* 105:    */   public static LoggerDelegate logger(String name)
/* 106:    */   {
/* 107:154 */     context();LoggerDelegate loggerDelegate = (LoggerDelegate)Context.loggers.get(name);
/* 108:156 */     if (loggerDelegate == null)
/* 109:    */     {
/* 110:158 */       context();loggerDelegate = Context.factory.logger(name);
/* 111:    */       
/* 112:160 */       context();LoggerDelegate oldLoggerDelegate = (LoggerDelegate)Context.loggers.putIfAbsent(name, loggerDelegate);
/* 113:162 */       if (oldLoggerDelegate != null) {
/* 114:163 */         loggerDelegate = oldLoggerDelegate;
/* 115:    */       }
/* 116:    */     }
/* 117:167 */     return loggerDelegate;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public static void setLevel(String name, LogLevel level)
/* 121:    */   {
/* 122:171 */     logger(name).level(level);
/* 123:    */   }
/* 124:    */   
/* 125:    */   public static void turnOnInMemoryConfigLoggerAll(String name)
/* 126:    */   {
/* 127:177 */     ConfigurableLogger configurableLogger = configurableLogger(name);
/* 128:178 */     configurableLogger.tee(new InMemoryThreadLocalLogger(LogLevel.ALL));
/* 129:    */   }
/* 130:    */   
/* 131:    */   public static void turnOffInMemoryConfigLoggerAll(String name)
/* 132:    */   {
/* 133:184 */     ConfigurableLogger configurableLogger = configurableLogger(name);
/* 134:185 */     configurableLogger.unwrap();
/* 135:    */   }
/* 136:    */   
/* 137:    */   public static void removeLogger(String name)
/* 138:    */   {
/* 139:189 */     context();Context.loggers.remove(name);
/* 140:190 */     context();Context.configurableLoggers.remove(name);
/* 141:    */   }
/* 142:    */   
/* 143:    */   public static void removeLogger(Class<?> clazz)
/* 144:    */   {
/* 145:195 */     context();Context.loggers.remove(clazz.getName());
/* 146:196 */     context();Context.configurableLoggers.remove(clazz.getName());
/* 147:    */   }
/* 148:    */   
/* 149:    */   public static Object contextToHold()
/* 150:    */   {
/* 151:202 */     return context();
/* 152:    */   }
/* 153:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.logging.Logging
 * JD-Core Version:    0.7.0.1
 */