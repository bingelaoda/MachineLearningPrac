/*   1:    */ package org.boon.logging;
/*   2:    */ 
/*   3:    */ import org.boon.core.Handler;
/*   4:    */ 
/*   5:    */ public final class TeeLoggerWrapper
/*   6:    */   extends ConfigurableLogger
/*   7:    */ {
/*   8:    */   private final LoggerDelegate logger;
/*   9: 39 */   public static final HandlerNoOP noOP = new HandlerNoOP();
/*  10:    */   
/*  11:    */   public TeeLoggerWrapper(LoggerDelegate delegate1, LoggerDelegate delegate2, Handler<LogRecord> logRecordHandler)
/*  12:    */   {
/*  13: 42 */     super(delegate1, logRecordHandler);
/*  14: 43 */     this.logger = delegate2;
/*  15:    */   }
/*  16:    */   
/*  17:    */   public TeeLoggerWrapper(LoggerDelegate delegate1, LoggerDelegate delegate2)
/*  18:    */   {
/*  19: 48 */     super(delegate1, noOP);
/*  20: 49 */     this.logger = delegate2;
/*  21:    */   }
/*  22:    */   
/*  23:    */   public boolean infoOn()
/*  24:    */   {
/*  25: 54 */     return super.infoOn();
/*  26:    */   }
/*  27:    */   
/*  28:    */   public boolean debugOn()
/*  29:    */   {
/*  30: 59 */     return super.debugOn();
/*  31:    */   }
/*  32:    */   
/*  33:    */   public boolean traceOn()
/*  34:    */   {
/*  35: 64 */     return super.traceOn();
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void fatal(Object message)
/*  39:    */   {
/*  40: 69 */     super.fatal(message);
/*  41: 70 */     this.logger.fatal(message);
/*  42:    */   }
/*  43:    */   
/*  44:    */   public void fatal(Object message, Throwable throwable)
/*  45:    */   {
/*  46: 77 */     super.fatal(message, throwable);
/*  47: 78 */     this.logger.fatal(message, throwable);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void error(Object message)
/*  51:    */   {
/*  52: 84 */     super.error(message);
/*  53: 85 */     this.logger.error(message);
/*  54:    */   }
/*  55:    */   
/*  56:    */   public void error(Object message, Throwable throwable)
/*  57:    */   {
/*  58: 91 */     super.error(message, throwable);
/*  59: 92 */     this.logger.error(message, throwable);
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void warn(Object message)
/*  63:    */   {
/*  64: 98 */     super.warn(message);
/*  65: 99 */     this.logger.warn(message);
/*  66:    */   }
/*  67:    */   
/*  68:    */   public void warn(Object message, Throwable throwable)
/*  69:    */   {
/*  70:105 */     super.warn(message, throwable);
/*  71:106 */     this.logger.warn(message, throwable);
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void info(Object message)
/*  75:    */   {
/*  76:112 */     super.info(message);
/*  77:113 */     this.logger.info(message);
/*  78:    */   }
/*  79:    */   
/*  80:    */   public void info(Object message, Throwable throwable)
/*  81:    */   {
/*  82:119 */     super.info(message, throwable);
/*  83:120 */     this.logger.info(message, throwable);
/*  84:    */   }
/*  85:    */   
/*  86:    */   public void debug(Object message)
/*  87:    */   {
/*  88:126 */     super.debug(message);
/*  89:127 */     this.logger.debug(message);
/*  90:    */   }
/*  91:    */   
/*  92:    */   public void debug(Object message, Throwable throwable)
/*  93:    */   {
/*  94:133 */     super.debug(message, throwable);
/*  95:134 */     this.logger.debug(message, throwable);
/*  96:    */   }
/*  97:    */   
/*  98:    */   public void trace(Object message)
/*  99:    */   {
/* 100:140 */     super.trace(message);
/* 101:141 */     this.logger.trace(message);
/* 102:    */   }
/* 103:    */   
/* 104:    */   public void trace(Object message, Throwable throwable)
/* 105:    */   {
/* 106:147 */     super.trace(message, throwable);
/* 107:148 */     this.logger.trace(message, throwable);
/* 108:    */   }
/* 109:    */   
/* 110:    */   public LoggerDelegate level(LogLevel level)
/* 111:    */   {
/* 112:154 */     super.level(level);
/* 113:155 */     this.logger.level(level);
/* 114:156 */     return this;
/* 115:    */   }
/* 116:    */   
/* 117:    */   public LoggerDelegate turnOff()
/* 118:    */   {
/* 119:162 */     super.turnOff();
/* 120:163 */     this.logger.turnOff();
/* 121:164 */     return this;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public void fatal(Object... messages)
/* 125:    */   {
/* 126:169 */     super.fatal(messages);
/* 127:170 */     this.logger.fatal(messages);
/* 128:    */   }
/* 129:    */   
/* 130:    */   public void fatal(Throwable throwable, Object... messages)
/* 131:    */   {
/* 132:176 */     super.fatal(throwable, messages);
/* 133:177 */     this.logger.fatal(throwable, messages);
/* 134:    */   }
/* 135:    */   
/* 136:    */   public void error(Object... messages)
/* 137:    */   {
/* 138:183 */     super.error(messages);
/* 139:184 */     this.logger.error(messages);
/* 140:    */   }
/* 141:    */   
/* 142:    */   public void error(Throwable throwable, Object... messages)
/* 143:    */   {
/* 144:190 */     super.error(throwable, messages);
/* 145:191 */     this.logger.error(throwable, messages);
/* 146:    */   }
/* 147:    */   
/* 148:    */   public void warn(Object... messages)
/* 149:    */   {
/* 150:197 */     super.warn(messages);
/* 151:198 */     this.logger.warn(messages);
/* 152:    */   }
/* 153:    */   
/* 154:    */   public void warn(Throwable throwable, Object... messages)
/* 155:    */   {
/* 156:204 */     super.warn(throwable, messages);
/* 157:205 */     this.logger.warn(throwable, messages);
/* 158:    */   }
/* 159:    */   
/* 160:    */   public void info(Object... messages)
/* 161:    */   {
/* 162:211 */     super.info(messages);
/* 163:212 */     this.logger.info(messages);
/* 164:    */   }
/* 165:    */   
/* 166:    */   public void info(Throwable throwable, Object... messages)
/* 167:    */   {
/* 168:218 */     super.info(throwable, messages);
/* 169:219 */     this.logger.info(throwable, messages);
/* 170:    */   }
/* 171:    */   
/* 172:    */   public void config(Object... messages)
/* 173:    */   {
/* 174:225 */     super.config(messages);
/* 175:226 */     this.logger.config(messages);
/* 176:    */   }
/* 177:    */   
/* 178:    */   public void config(Throwable throwable, Object... messages)
/* 179:    */   {
/* 180:232 */     super.config(throwable, messages);
/* 181:233 */     this.logger.config(throwable, messages);
/* 182:    */   }
/* 183:    */   
/* 184:    */   public void debug(Object... messages)
/* 185:    */   {
/* 186:239 */     super.debug(messages);
/* 187:240 */     this.logger.debug(messages);
/* 188:    */   }
/* 189:    */   
/* 190:    */   public void debug(Throwable throwable, Object... messages)
/* 191:    */   {
/* 192:246 */     super.debug(throwable, messages);
/* 193:247 */     this.logger.debug(throwable, messages);
/* 194:    */   }
/* 195:    */   
/* 196:    */   public void trace(Object... messages)
/* 197:    */   {
/* 198:253 */     super.trace(messages);
/* 199:254 */     this.logger.trace(messages);
/* 200:    */   }
/* 201:    */   
/* 202:    */   public void trace(Throwable throwable, Object... messages)
/* 203:    */   {
/* 204:260 */     super.trace(throwable, messages);
/* 205:261 */     this.logger.trace(throwable, messages);
/* 206:    */   }
/* 207:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.logging.TeeLoggerWrapper
 * JD-Core Version:    0.7.0.1
 */