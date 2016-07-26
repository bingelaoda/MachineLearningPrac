/*   1:    */ package org.boon;
/*   2:    */ 
/*   3:    */ import org.boon.core.Handler;
/*   4:    */ import org.boon.logging.ConfigurableLogger;
/*   5:    */ import org.boon.logging.LogLevel;
/*   6:    */ import org.boon.logging.LogRecord;
/*   7:    */ import org.boon.logging.LoggerDelegate;
/*   8:    */ import org.boon.logging.TeeLoggerWrapper;
/*   9:    */ 
/*  10:    */ public class Logger
/*  11:    */ {
/*  12:    */   private volatile transient LoggerDelegate logger;
/*  13:    */   final LoggerDelegate original;
/*  14:    */   
/*  15:    */   public Logger(LoggerDelegate delegate)
/*  16:    */   {
/*  17: 57 */     this.logger = delegate;
/*  18: 58 */     this.original = this.logger;
/*  19:    */   }
/*  20:    */   
/*  21:    */   public synchronized void tee(LoggerDelegate newLogger)
/*  22:    */   {
/*  23: 62 */     this.logger = new TeeLoggerWrapper(this.logger, newLogger);
/*  24:    */   }
/*  25:    */   
/*  26:    */   public synchronized void handler(Handler<LogRecord> handler)
/*  27:    */   {
/*  28: 67 */     this.logger = new ConfigurableLogger(this.logger, handler);
/*  29:    */   }
/*  30:    */   
/*  31:    */   public synchronized void teeAndHandler(LoggerDelegate newLogger, Handler<LogRecord> handler)
/*  32:    */   {
/*  33: 72 */     this.logger = new TeeLoggerWrapper(this.logger, newLogger);
/*  34:    */   }
/*  35:    */   
/*  36:    */   public synchronized void unwrap()
/*  37:    */   {
/*  38: 77 */     this.logger = this.original;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public boolean infoOn()
/*  42:    */   {
/*  43: 81 */     return this.logger.infoOn();
/*  44:    */   }
/*  45:    */   
/*  46:    */   public boolean debugOn()
/*  47:    */   {
/*  48: 85 */     return this.logger.debugOn();
/*  49:    */   }
/*  50:    */   
/*  51:    */   public boolean traceOn()
/*  52:    */   {
/*  53: 89 */     return this.logger.traceOn();
/*  54:    */   }
/*  55:    */   
/*  56:    */   public void fatal(Object message)
/*  57:    */   {
/*  58: 93 */     this.logger.fatal(message);
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void fatal(Exception message)
/*  62:    */   {
/*  63: 98 */     this.logger.fatal("", message);
/*  64:    */   }
/*  65:    */   
/*  66:    */   public void error(Exception message)
/*  67:    */   {
/*  68:103 */     this.logger.error("", message);
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void warn(Exception message)
/*  72:    */   {
/*  73:108 */     this.logger.warn("", message);
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void fatal(Object message, Throwable t)
/*  77:    */   {
/*  78:113 */     this.logger.fatal(message, t);
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void error(Object message)
/*  82:    */   {
/*  83:117 */     this.logger.error(message);
/*  84:    */   }
/*  85:    */   
/*  86:    */   public void error(Object message, Throwable t)
/*  87:    */   {
/*  88:121 */     this.logger.error(message, t);
/*  89:    */   }
/*  90:    */   
/*  91:    */   public void warn(Object message)
/*  92:    */   {
/*  93:125 */     this.logger.warn(message);
/*  94:    */   }
/*  95:    */   
/*  96:    */   public void warn(Object message, Throwable t)
/*  97:    */   {
/*  98:129 */     this.logger.warn(message, t);
/*  99:    */   }
/* 100:    */   
/* 101:    */   public void info(Object message)
/* 102:    */   {
/* 103:133 */     this.logger.info(message);
/* 104:    */   }
/* 105:    */   
/* 106:    */   public void info(Object message, Throwable t)
/* 107:    */   {
/* 108:137 */     this.logger.info(message, t);
/* 109:    */   }
/* 110:    */   
/* 111:    */   public void debug(Object message)
/* 112:    */   {
/* 113:141 */     this.logger.debug(message);
/* 114:    */   }
/* 115:    */   
/* 116:    */   public void debug(Object message, Throwable t)
/* 117:    */   {
/* 118:145 */     this.logger.debug(message, t);
/* 119:    */   }
/* 120:    */   
/* 121:    */   public void trace(Object message)
/* 122:    */   {
/* 123:149 */     this.logger.trace(message);
/* 124:    */   }
/* 125:    */   
/* 126:    */   public void trace(Object message, Throwable t)
/* 127:    */   {
/* 128:153 */     this.logger.trace(message, t);
/* 129:    */   }
/* 130:    */   
/* 131:    */   public void level(LogLevel level)
/* 132:    */   {
/* 133:158 */     this.logger.level(level);
/* 134:    */   }
/* 135:    */   
/* 136:    */   public void turnOff()
/* 137:    */   {
/* 138:164 */     this.logger.turnOff();
/* 139:    */   }
/* 140:    */   
/* 141:    */   public void fatal(Object... messages)
/* 142:    */   {
/* 143:168 */     this.logger.fatal(messages);
/* 144:    */   }
/* 145:    */   
/* 146:    */   public void fatal(Throwable t, Object... messages)
/* 147:    */   {
/* 148:172 */     this.logger.fatal(t, messages);
/* 149:    */   }
/* 150:    */   
/* 151:    */   public void error(Object... messages)
/* 152:    */   {
/* 153:176 */     this.logger.error(messages);
/* 154:    */   }
/* 155:    */   
/* 156:    */   public void error(Throwable t, Object... messages)
/* 157:    */   {
/* 158:180 */     this.logger.error(t, messages);
/* 159:    */   }
/* 160:    */   
/* 161:    */   public void warn(Object... messages)
/* 162:    */   {
/* 163:184 */     this.logger.warn(messages);
/* 164:    */   }
/* 165:    */   
/* 166:    */   public void warn(Throwable t, Object... messages)
/* 167:    */   {
/* 168:189 */     this.logger.warn(t, messages);
/* 169:    */   }
/* 170:    */   
/* 171:    */   public void info(Object... messages)
/* 172:    */   {
/* 173:193 */     this.logger.info(messages);
/* 174:    */   }
/* 175:    */   
/* 176:    */   public void info(Throwable t, Object... messages)
/* 177:    */   {
/* 178:197 */     this.logger.info(t, messages);
/* 179:    */   }
/* 180:    */   
/* 181:    */   public void config(Object... messages)
/* 182:    */   {
/* 183:201 */     this.logger.config(messages);
/* 184:    */   }
/* 185:    */   
/* 186:    */   public void config(Throwable t, Object... messages)
/* 187:    */   {
/* 188:205 */     this.logger.config(t, messages);
/* 189:    */   }
/* 190:    */   
/* 191:    */   public void debug(Object... messages)
/* 192:    */   {
/* 193:209 */     this.logger.debug(messages);
/* 194:    */   }
/* 195:    */   
/* 196:    */   public void debug(Throwable t, Object... messages)
/* 197:    */   {
/* 198:213 */     this.logger.debug(t, messages);
/* 199:    */   }
/* 200:    */   
/* 201:    */   public void trace(Object... messages)
/* 202:    */   {
/* 203:217 */     this.logger.trace(messages);
/* 204:    */   }
/* 205:    */   
/* 206:    */   public void trace(Throwable t, Object... messages)
/* 207:    */   {
/* 208:222 */     this.logger.trace(t, messages);
/* 209:    */   }
/* 210:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.Logger
 * JD-Core Version:    0.7.0.1
 */