/*   1:    */ package weka.knowledgeflow;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.PrintWriter;
/*   5:    */ import java.io.StringWriter;
/*   6:    */ import weka.core.LogHandler;
/*   7:    */ import weka.core.OptionHandler;
/*   8:    */ import weka.core.Utils;
/*   9:    */ import weka.gui.Logger;
/*  10:    */ import weka.knowledgeflow.steps.Step;
/*  11:    */ import weka.knowledgeflow.steps.WekaAlgorithmWrapper;
/*  12:    */ 
/*  13:    */ public class LogManager
/*  14:    */   implements LogHandler
/*  15:    */ {
/*  16: 46 */   protected String m_statusMessagePrefix = "";
/*  17:    */   protected Logger m_log;
/*  18:    */   protected boolean m_status;
/*  19: 60 */   protected LoggingLevel m_levelToLogAt = LoggingLevel.BASIC;
/*  20:    */   
/*  21:    */   public LogManager(Step source)
/*  22:    */   {
/*  23: 68 */     this.m_status = true;
/*  24: 69 */     String prefix = (source != null ? source.getName() : "Unknown") + "$";
/*  25:    */     
/*  26: 71 */     prefix = prefix + (source != null ? source.hashCode() : 1) + "|";
/*  27: 72 */     if ((source instanceof WekaAlgorithmWrapper))
/*  28:    */     {
/*  29: 73 */       Object wrappedAlgo = ((WekaAlgorithmWrapper)source).getWrappedAlgorithm();
/*  30: 75 */       if ((wrappedAlgo instanceof OptionHandler)) {
/*  31: 76 */         prefix = prefix + Utils.joinOptions(((OptionHandler)wrappedAlgo).getOptions()) + "|";
/*  32:    */       }
/*  33:    */     }
/*  34: 81 */     this.m_statusMessagePrefix = prefix;
/*  35: 82 */     if (source != null)
/*  36:    */     {
/*  37: 83 */       this.m_log = ((StepManagerImpl)source.getStepManager()).getLog();
/*  38: 84 */       setLoggingLevel(((StepManagerImpl)source.getStepManager()).getLoggingLevel());
/*  39:    */     }
/*  40:    */   }
/*  41:    */   
/*  42:    */   public LogManager(Logger log)
/*  43:    */   {
/*  44: 95 */     this(log, true);
/*  45:    */   }
/*  46:    */   
/*  47:    */   public LogManager(Logger log, boolean status)
/*  48:    */   {
/*  49:106 */     this.m_log = log;
/*  50:107 */     this.m_status = status;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public static String stackTraceToString(Throwable throwable)
/*  54:    */   {
/*  55:117 */     StringWriter sw = new StringWriter();
/*  56:118 */     PrintWriter pw = new PrintWriter(sw);
/*  57:119 */     throwable.printStackTrace(pw);
/*  58:    */     
/*  59:121 */     return sw.toString();
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void setLog(Logger log)
/*  63:    */   {
/*  64:130 */     this.m_log = log;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public Logger getLog()
/*  68:    */   {
/*  69:139 */     return this.m_log;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public LoggingLevel getLoggingLevel()
/*  73:    */   {
/*  74:148 */     return this.m_levelToLogAt;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public void setLoggingLevel(LoggingLevel level)
/*  78:    */   {
/*  79:157 */     this.m_levelToLogAt = level;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public void logLow(String message)
/*  83:    */   {
/*  84:166 */     log(message, LoggingLevel.LOW);
/*  85:    */   }
/*  86:    */   
/*  87:    */   public void logBasic(String message)
/*  88:    */   {
/*  89:175 */     log(message, LoggingLevel.BASIC);
/*  90:    */   }
/*  91:    */   
/*  92:    */   public void logDetailed(String message)
/*  93:    */   {
/*  94:184 */     log(message, LoggingLevel.DETAILED);
/*  95:    */   }
/*  96:    */   
/*  97:    */   public void logDebug(String message)
/*  98:    */   {
/*  99:193 */     log(message, LoggingLevel.DEBUGGING);
/* 100:    */   }
/* 101:    */   
/* 102:    */   public void logWarning(String message)
/* 103:    */   {
/* 104:202 */     log(message, LoggingLevel.WARNING);
/* 105:203 */     if (this.m_status) {
/* 106:204 */       statusMessage("WARNING: " + message);
/* 107:    */     }
/* 108:    */   }
/* 109:    */   
/* 110:    */   public void logError(String message, Exception cause)
/* 111:    */   {
/* 112:215 */     log(message, LoggingLevel.ERROR, cause);
/* 113:216 */     if (this.m_status) {
/* 114:217 */       statusMessage("ERROR: " + message);
/* 115:    */     }
/* 116:    */   }
/* 117:    */   
/* 118:    */   public void statusMessage(String message)
/* 119:    */   {
/* 120:227 */     if (this.m_log != null) {
/* 121:228 */       this.m_log.statusMessage(statusMessagePrefix() + message);
/* 122:    */     }
/* 123:    */   }
/* 124:    */   
/* 125:    */   public void log(String message, LoggingLevel messageLevel)
/* 126:    */   {
/* 127:239 */     log(message, messageLevel, null);
/* 128:    */   }
/* 129:    */   
/* 130:    */   protected void log(String message, LoggingLevel messageLevel, Throwable cause)
/* 131:    */   {
/* 132:251 */     if ((messageLevel == LoggingLevel.WARNING) || (messageLevel == LoggingLevel.ERROR) || (messageLevel.ordinal() <= this.m_levelToLogAt.ordinal()))
/* 133:    */     {
/* 134:254 */       String logMessage = "[" + messageLevel.toString() + "] " + statusMessagePrefix() + message;
/* 135:256 */       if (cause != null) {
/* 136:257 */         logMessage = logMessage + "\n" + stackTraceToString(cause);
/* 137:    */       }
/* 138:259 */       if (this.m_log != null)
/* 139:    */       {
/* 140:260 */         this.m_log.logMessage(logMessage);
/* 141:261 */         if ((messageLevel == LoggingLevel.ERROR) || (messageLevel == LoggingLevel.WARNING)) {
/* 142:263 */           statusMessage(messageLevel.toString() + " (see log for details)");
/* 143:    */         }
/* 144:    */       }
/* 145:    */       else
/* 146:    */       {
/* 147:266 */         System.err.println(logMessage);
/* 148:    */       }
/* 149:    */     }
/* 150:    */   }
/* 151:    */   
/* 152:    */   private String statusMessagePrefix()
/* 153:    */   {
/* 154:272 */     return this.m_statusMessagePrefix;
/* 155:    */   }
/* 156:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.LogManager
 * JD-Core Version:    0.7.0.1
 */