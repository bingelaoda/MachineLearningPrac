/*   1:    */ package org.boon.logging;
/*   2:    */ 
/*   3:    */ import org.boon.core.Handler;
/*   4:    */ import org.boon.primitive.Arry;
/*   5:    */ 
/*   6:    */ public class ConfigurableLogger
/*   7:    */   implements LoggerDelegate
/*   8:    */ {
/*   9:    */   private volatile transient LoggerDelegate logger;
/*  10:    */   private final Handler<LogRecord> handler;
/*  11:    */   final LoggerDelegate original;
/*  12: 47 */   public static final HandlerNoOP noOP = new HandlerNoOP();
/*  13:    */   
/*  14:    */   public synchronized void unwrap()
/*  15:    */   {
/*  16: 52 */     this.logger = this.original;
/*  17:    */   }
/*  18:    */   
/*  19:    */   public ConfigurableLogger(LoggerDelegate delegate, Handler<LogRecord> logRecordHandler)
/*  20:    */   {
/*  21: 56 */     this.logger = delegate;
/*  22: 57 */     this.handler = logRecordHandler;
/*  23: 58 */     this.original = this.logger;
/*  24:    */   }
/*  25:    */   
/*  26:    */   public ConfigurableLogger(LoggerDelegate delegate)
/*  27:    */   {
/*  28: 63 */     this.logger = delegate;
/*  29: 64 */     this.handler = noOP;
/*  30: 65 */     this.original = this.logger;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public synchronized void tee(LoggerDelegate newLogger)
/*  34:    */   {
/*  35: 70 */     this.logger = new TeeLoggerWrapper(this.logger, newLogger);
/*  36:    */   }
/*  37:    */   
/*  38:    */   public synchronized void handler(Handler<LogRecord> handler)
/*  39:    */   {
/*  40: 75 */     this.logger = new ConfigurableLogger(this.logger, handler);
/*  41:    */   }
/*  42:    */   
/*  43:    */   public synchronized void teeAndHandler(LoggerDelegate newLogger, Handler<LogRecord> handler)
/*  44:    */   {
/*  45: 80 */     this.logger = new TeeLoggerWrapper(this.logger, newLogger);
/*  46:    */   }
/*  47:    */   
/*  48:    */   public boolean infoOn()
/*  49:    */   {
/*  50: 85 */     return this.logger.infoOn();
/*  51:    */   }
/*  52:    */   
/*  53:    */   public boolean debugOn()
/*  54:    */   {
/*  55: 89 */     return this.logger.debugOn();
/*  56:    */   }
/*  57:    */   
/*  58:    */   public boolean traceOn()
/*  59:    */   {
/*  60: 93 */     return this.logger.traceOn();
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void fatal(Object message)
/*  64:    */   {
/*  65: 97 */     this.handler.handle(LogRecord.before(Arry.array(new Object[] { message }), LogLevel.FATAL));
/*  66: 98 */     this.logger.fatal(message);
/*  67:    */     
/*  68:100 */     this.handler.handle(LogRecord.after(Arry.array(new Object[] { message }), LogLevel.FATAL));
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void fatal(Object message, Throwable throwable)
/*  72:    */   {
/*  73:105 */     this.handler.handle(LogRecord.before(Arry.array(new Object[] { message }), throwable, LogLevel.FATAL));
/*  74:106 */     this.logger.fatal(message, throwable);
/*  75:    */     
/*  76:108 */     this.handler.handle(LogRecord.after(Arry.array(new Object[] { message }), throwable, LogLevel.FATAL));
/*  77:    */   }
/*  78:    */   
/*  79:    */   public void error(Object message)
/*  80:    */   {
/*  81:113 */     this.handler.handle(LogRecord.before(Arry.array(new Object[] { message }), LogLevel.ERROR));
/*  82:114 */     this.logger.error(message);
/*  83:    */     
/*  84:116 */     this.handler.handle(LogRecord.after(Arry.array(new Object[] { message }), LogLevel.ERROR));
/*  85:    */   }
/*  86:    */   
/*  87:    */   public void error(Object message, Throwable throwable)
/*  88:    */   {
/*  89:121 */     this.handler.handle(LogRecord.before(Arry.array(new Object[] { message }), throwable, LogLevel.ERROR));
/*  90:122 */     this.logger.error(message, throwable);
/*  91:    */     
/*  92:124 */     this.handler.handle(LogRecord.after(Arry.array(new Object[] { message }), throwable, LogLevel.ERROR));
/*  93:    */   }
/*  94:    */   
/*  95:    */   public void warn(Object message)
/*  96:    */   {
/*  97:129 */     this.handler.handle(LogRecord.before(Arry.array(new Object[] { message }), LogLevel.WARN));
/*  98:130 */     this.logger.warn(message);
/*  99:    */     
/* 100:132 */     this.handler.handle(LogRecord.after(Arry.array(new Object[] { message }), LogLevel.WARN));
/* 101:    */   }
/* 102:    */   
/* 103:    */   public void warn(Object message, Throwable throwable)
/* 104:    */   {
/* 105:137 */     this.handler.handle(LogRecord.before(Arry.array(new Object[] { message }), throwable, LogLevel.WARN));
/* 106:138 */     this.logger.warn(message, throwable);
/* 107:    */     
/* 108:140 */     this.handler.handle(LogRecord.after(Arry.array(new Object[] { message }), throwable, LogLevel.WARN));
/* 109:    */   }
/* 110:    */   
/* 111:    */   public void info(Object message)
/* 112:    */   {
/* 113:145 */     this.handler.handle(LogRecord.before(Arry.array(new Object[] { message }), LogLevel.INFO));
/* 114:146 */     this.logger.info(message);
/* 115:    */     
/* 116:148 */     this.handler.handle(LogRecord.after(Arry.array(new Object[] { message }), LogLevel.INFO));
/* 117:    */   }
/* 118:    */   
/* 119:    */   public void info(Object message, Throwable throwable)
/* 120:    */   {
/* 121:153 */     this.handler.handle(LogRecord.before(Arry.array(new Object[] { message }), throwable, LogLevel.INFO));
/* 122:154 */     this.logger.info(message, throwable);
/* 123:    */     
/* 124:156 */     this.handler.handle(LogRecord.after(Arry.array(new Object[] { message }), throwable, LogLevel.INFO));
/* 125:    */   }
/* 126:    */   
/* 127:    */   public void debug(Object message)
/* 128:    */   {
/* 129:161 */     this.handler.handle(LogRecord.before(Arry.array(new Object[] { message }), LogLevel.DEBUG));
/* 130:162 */     this.logger.debug(message);
/* 131:163 */     this.handler.handle(LogRecord.after(Arry.array(new Object[] { message }), LogLevel.DEBUG));
/* 132:    */   }
/* 133:    */   
/* 134:    */   public void debug(Object message, Throwable throwable)
/* 135:    */   {
/* 136:169 */     this.handler.handle(LogRecord.before(Arry.array(new Object[] { message }), throwable, LogLevel.DEBUG));
/* 137:170 */     this.logger.debug(message, throwable);
/* 138:171 */     this.handler.handle(LogRecord.after(Arry.array(new Object[] { message }), throwable, LogLevel.DEBUG));
/* 139:    */   }
/* 140:    */   
/* 141:    */   public void trace(Object message)
/* 142:    */   {
/* 143:176 */     this.handler.handle(LogRecord.before(Arry.array(new Object[] { message }), LogLevel.TRACE));
/* 144:177 */     this.logger.trace(message);
/* 145:    */     
/* 146:179 */     this.handler.handle(LogRecord.after(Arry.array(new Object[] { message }), LogLevel.TRACE));
/* 147:    */   }
/* 148:    */   
/* 149:    */   public void trace(Object message, Throwable throwable)
/* 150:    */   {
/* 151:184 */     this.handler.handle(LogRecord.before(Arry.array(new Object[] { message }), throwable, LogLevel.TRACE));
/* 152:185 */     this.logger.trace(message, throwable);
/* 153:186 */     this.handler.handle(LogRecord.after(Arry.array(new Object[] { message }), throwable, LogLevel.TRACE));
/* 154:    */   }
/* 155:    */   
/* 156:    */   public LoggerDelegate level(LogLevel level)
/* 157:    */   {
/* 158:191 */     this.logger.level(level);
/* 159:192 */     return this;
/* 160:    */   }
/* 161:    */   
/* 162:    */   public LoggerDelegate turnOff()
/* 163:    */   {
/* 164:198 */     this.logger.turnOff();
/* 165:199 */     return this;
/* 166:    */   }
/* 167:    */   
/* 168:    */   public void fatal(Object... messages)
/* 169:    */   {
/* 170:204 */     this.handler.handle(LogRecord.before(messages, LogLevel.FATAL));
/* 171:205 */     this.logger.fatal(messages);
/* 172:    */     
/* 173:207 */     this.handler.handle(LogRecord.after(messages, LogLevel.FATAL));
/* 174:    */   }
/* 175:    */   
/* 176:    */   public void fatal(Throwable throwable, Object... messages)
/* 177:    */   {
/* 178:212 */     this.handler.handle(LogRecord.before(messages, throwable, LogLevel.FATAL));
/* 179:213 */     this.logger.fatal(throwable, messages);
/* 180:    */     
/* 181:215 */     this.handler.handle(LogRecord.after(messages, throwable, LogLevel.FATAL));
/* 182:    */   }
/* 183:    */   
/* 184:    */   public void error(Object... messages)
/* 185:    */   {
/* 186:220 */     this.handler.handle(LogRecord.before(messages, LogLevel.ERROR));
/* 187:221 */     this.logger.error(messages);
/* 188:    */     
/* 189:223 */     this.handler.handle(LogRecord.after(messages, LogLevel.ERROR));
/* 190:    */   }
/* 191:    */   
/* 192:    */   public void error(Throwable throwable, Object... messages)
/* 193:    */   {
/* 194:228 */     this.handler.handle(LogRecord.before(messages, throwable, LogLevel.ERROR));
/* 195:229 */     this.logger.error(throwable, messages);
/* 196:    */     
/* 197:231 */     this.handler.handle(LogRecord.after(messages, throwable, LogLevel.ERROR));
/* 198:    */   }
/* 199:    */   
/* 200:    */   public void warn(Object... messages)
/* 201:    */   {
/* 202:236 */     this.handler.handle(LogRecord.before(messages, LogLevel.WARN));
/* 203:237 */     this.logger.warn(messages);
/* 204:    */     
/* 205:239 */     this.handler.handle(LogRecord.after(messages, LogLevel.WARN));
/* 206:    */   }
/* 207:    */   
/* 208:    */   public void warn(Throwable throwable, Object... messages)
/* 209:    */   {
/* 210:244 */     this.handler.handle(LogRecord.before(messages, throwable, LogLevel.WARN));
/* 211:    */     
/* 212:246 */     this.logger.warn(throwable, messages);
/* 213:    */     
/* 214:248 */     this.handler.handle(LogRecord.after(messages, throwable, LogLevel.WARN));
/* 215:    */   }
/* 216:    */   
/* 217:    */   public void info(Object... messages)
/* 218:    */   {
/* 219:253 */     this.handler.handle(LogRecord.before(messages, LogLevel.INFO));
/* 220:254 */     this.logger.info(messages);
/* 221:    */     
/* 222:256 */     this.handler.handle(LogRecord.after(messages, LogLevel.INFO));
/* 223:    */   }
/* 224:    */   
/* 225:    */   public void info(Throwable throwable, Object... messages)
/* 226:    */   {
/* 227:261 */     this.handler.handle(LogRecord.before(messages, throwable, LogLevel.INFO));
/* 228:262 */     this.logger.info(throwable, messages);
/* 229:    */     
/* 230:264 */     this.handler.handle(LogRecord.after(messages, throwable, LogLevel.INFO));
/* 231:    */   }
/* 232:    */   
/* 233:    */   public void config(Object... messages)
/* 234:    */   {
/* 235:269 */     this.handler.handle(LogRecord.before(messages, LogLevel.CONFIG));
/* 236:270 */     this.logger.config(messages);
/* 237:    */     
/* 238:272 */     this.handler.handle(LogRecord.after(messages, LogLevel.CONFIG));
/* 239:    */   }
/* 240:    */   
/* 241:    */   public void config(Throwable throwable, Object... messages)
/* 242:    */   {
/* 243:277 */     this.handler.handle(LogRecord.before(messages, throwable, LogLevel.CONFIG));
/* 244:278 */     this.logger.config(throwable, messages);
/* 245:    */     
/* 246:280 */     this.handler.handle(LogRecord.after(messages, throwable, LogLevel.CONFIG));
/* 247:    */   }
/* 248:    */   
/* 249:    */   public void debug(Object... messages)
/* 250:    */   {
/* 251:285 */     this.handler.handle(LogRecord.before(messages, LogLevel.DEBUG));
/* 252:286 */     this.logger.debug(messages);
/* 253:    */     
/* 254:288 */     this.handler.handle(LogRecord.after(messages, LogLevel.DEBUG));
/* 255:    */   }
/* 256:    */   
/* 257:    */   public void debug(Throwable throwable, Object... messages)
/* 258:    */   {
/* 259:293 */     this.handler.handle(LogRecord.before(messages, throwable, LogLevel.DEBUG));
/* 260:294 */     this.logger.debug(throwable, messages);
/* 261:    */     
/* 262:296 */     this.handler.handle(LogRecord.after(messages, throwable, LogLevel.DEBUG));
/* 263:    */   }
/* 264:    */   
/* 265:    */   public void trace(Object... messages)
/* 266:    */   {
/* 267:301 */     this.handler.handle(LogRecord.before(messages, LogLevel.TRACE));
/* 268:302 */     this.logger.trace(messages);
/* 269:    */     
/* 270:304 */     this.handler.handle(LogRecord.after(messages, LogLevel.TRACE));
/* 271:    */   }
/* 272:    */   
/* 273:    */   public void trace(Throwable throwable, Object... messages)
/* 274:    */   {
/* 275:310 */     this.handler.handle(LogRecord.before(messages, throwable, LogLevel.TRACE));
/* 276:311 */     this.logger.trace(throwable, messages);
/* 277:    */     
/* 278:313 */     this.handler.handle(LogRecord.after(messages, throwable, LogLevel.TRACE));
/* 279:    */   }
/* 280:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.logging.ConfigurableLogger
 * JD-Core Version:    0.7.0.1
 */