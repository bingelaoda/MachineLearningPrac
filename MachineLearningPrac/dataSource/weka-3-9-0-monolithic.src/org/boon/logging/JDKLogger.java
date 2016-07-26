/*   1:    */ package org.boon.logging;
/*   2:    */ 
/*   3:    */ import java.util.logging.Level;
/*   4:    */ import java.util.logging.Logger;
/*   5:    */ import org.boon.Boon;
/*   6:    */ import org.boon.Str;
/*   7:    */ 
/*   8:    */ public class JDKLogger
/*   9:    */   implements LoggerDelegate
/*  10:    */ {
/*  11:    */   private final Logger logger;
/*  12:    */   
/*  13:    */   JDKLogger(String name)
/*  14:    */   {
/*  15: 44 */     this.logger = Logger.getLogger(name);
/*  16:    */   }
/*  17:    */   
/*  18:    */   public boolean infoOn()
/*  19:    */   {
/*  20: 50 */     return this.logger.isLoggable(Level.INFO);
/*  21:    */   }
/*  22:    */   
/*  23:    */   public boolean debugOn()
/*  24:    */   {
/*  25: 55 */     return this.logger.isLoggable(Level.FINE);
/*  26:    */   }
/*  27:    */   
/*  28:    */   public boolean traceOn()
/*  29:    */   {
/*  30: 60 */     return this.logger.isLoggable(Level.FINEST);
/*  31:    */   }
/*  32:    */   
/*  33:    */   public void fatal(Object... messages)
/*  34:    */   {
/*  35: 66 */     this.logger.log(Level.SEVERE, Boon.sputs(messages));
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void fatal(Throwable t, Object... messages)
/*  39:    */   {
/*  40: 72 */     this.logger.log(Level.SEVERE, Boon.sputs(messages), t);
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void fatal(Exception message)
/*  44:    */   {
/*  45: 77 */     this.logger.log(Level.SEVERE, "", message);
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void error(Exception message)
/*  49:    */   {
/*  50: 82 */     this.logger.log(Level.SEVERE, "", message);
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void warn(Exception message)
/*  54:    */   {
/*  55: 87 */     this.logger.log(Level.WARNING, "", message);
/*  56:    */   }
/*  57:    */   
/*  58:    */   public void error(Object... messages)
/*  59:    */   {
/*  60: 93 */     this.logger.log(Level.SEVERE, Boon.sputs(messages));
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void error(Throwable t, Object... messages)
/*  64:    */   {
/*  65:100 */     this.logger.log(Level.SEVERE, Boon.sputs(messages), t);
/*  66:    */   }
/*  67:    */   
/*  68:    */   public void warn(Object... messages)
/*  69:    */   {
/*  70:107 */     this.logger.log(Level.WARNING, Boon.sputs(messages));
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void warn(Throwable t, Object... messages)
/*  74:    */   {
/*  75:114 */     this.logger.log(Level.WARNING, Boon.sputs(messages), t);
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void info(Object... messages)
/*  79:    */   {
/*  80:122 */     this.logger.log(Level.INFO, Boon.sputs(messages));
/*  81:    */   }
/*  82:    */   
/*  83:    */   public void info(Throwable t, Object... messages)
/*  84:    */   {
/*  85:128 */     this.logger.log(Level.INFO, Boon.sputs(messages), t);
/*  86:    */   }
/*  87:    */   
/*  88:    */   public void config(Object... messages)
/*  89:    */   {
/*  90:137 */     this.logger.log(Level.CONFIG, Boon.sputs(messages));
/*  91:    */   }
/*  92:    */   
/*  93:    */   public void config(Throwable t, Object... messages)
/*  94:    */   {
/*  95:143 */     this.logger.log(Level.CONFIG, Boon.sputs(messages), t);
/*  96:    */   }
/*  97:    */   
/*  98:    */   public void debug(Object... messages)
/*  99:    */   {
/* 100:150 */     this.logger.log(Level.FINE, Boon.sputs(messages));
/* 101:    */   }
/* 102:    */   
/* 103:    */   public void debug(Throwable t, Object... messages)
/* 104:    */   {
/* 105:157 */     this.logger.log(Level.FINE, Boon.sputs(messages), t);
/* 106:    */   }
/* 107:    */   
/* 108:    */   public void trace(Object... messages)
/* 109:    */   {
/* 110:164 */     this.logger.log(Level.FINEST, Boon.sputs(messages));
/* 111:    */   }
/* 112:    */   
/* 113:    */   public void trace(Throwable t, Object... messages)
/* 114:    */   {
/* 115:171 */     this.logger.log(Level.FINEST, Boon.sputs(messages), t);
/* 116:    */   }
/* 117:    */   
/* 118:    */   public LogLevel level()
/* 119:    */   {
/* 120:177 */     Level level = this.logger.getLevel();
/* 121:178 */     if (level == Level.FINE) {
/* 122:179 */       return LogLevel.DEBUG;
/* 123:    */     }
/* 124:180 */     if (level == Level.FINEST) {
/* 125:181 */       return LogLevel.TRACE;
/* 126:    */     }
/* 127:182 */     if (level == Level.CONFIG) {
/* 128:183 */       return LogLevel.CONFIG;
/* 129:    */     }
/* 130:184 */     if (level == Level.INFO) {
/* 131:185 */       return LogLevel.INFO;
/* 132:    */     }
/* 133:186 */     if (level == Level.WARNING) {
/* 134:187 */       return LogLevel.WARN;
/* 135:    */     }
/* 136:188 */     if (level == Level.SEVERE) {
/* 137:189 */       return LogLevel.FATAL;
/* 138:    */     }
/* 139:191 */     return LogLevel.ERROR;
/* 140:    */   }
/* 141:    */   
/* 142:    */   public LoggerDelegate level(LogLevel level)
/* 143:    */   {
/* 144:195 */     switch (1.$SwitchMap$org$boon$logging$LogLevel[level.ordinal()])
/* 145:    */     {
/* 146:    */     case 1: 
/* 147:197 */       this.logger.setLevel(Level.FINE);
/* 148:198 */       break;
/* 149:    */     case 2: 
/* 150:201 */       this.logger.setLevel(Level.FINEST);
/* 151:202 */       break;
/* 152:    */     case 3: 
/* 153:205 */       this.logger.setLevel(Level.CONFIG);
/* 154:206 */       break;
/* 155:    */     case 4: 
/* 156:209 */       this.logger.setLevel(Level.INFO);
/* 157:210 */       break;
/* 158:    */     case 5: 
/* 159:213 */       this.logger.setLevel(Level.WARNING);
/* 160:214 */       break;
/* 161:    */     case 6: 
/* 162:218 */       this.logger.setLevel(Level.SEVERE);
/* 163:219 */       break;
/* 164:    */     case 7: 
/* 165:222 */       this.logger.setLevel(Level.SEVERE);
/* 166:    */     }
/* 167:226 */     return this;
/* 168:    */   }
/* 169:    */   
/* 170:    */   public LoggerDelegate turnOff()
/* 171:    */   {
/* 172:231 */     this.logger.setLevel(Level.OFF);
/* 173:232 */     return this;
/* 174:    */   }
/* 175:    */   
/* 176:    */   public void fatal(Object message)
/* 177:    */   {
/* 178:237 */     this.logger.log(Level.SEVERE, Str.str(message));
/* 179:    */   }
/* 180:    */   
/* 181:    */   public void fatal(Object message, Throwable t)
/* 182:    */   {
/* 183:241 */     this.logger.log(Level.SEVERE, Str.str(message), t);
/* 184:    */   }
/* 185:    */   
/* 186:    */   public void error(Object message)
/* 187:    */   {
/* 188:245 */     this.logger.log(Level.SEVERE, Str.str(message));
/* 189:    */   }
/* 190:    */   
/* 191:    */   public void error(Object message, Throwable t)
/* 192:    */   {
/* 193:249 */     this.logger.log(Level.SEVERE, Str.str(message), t);
/* 194:    */   }
/* 195:    */   
/* 196:    */   public void warn(Object message)
/* 197:    */   {
/* 198:254 */     this.logger.log(Level.WARNING, Str.str(message));
/* 199:    */   }
/* 200:    */   
/* 201:    */   public void warn(Object message, Throwable t)
/* 202:    */   {
/* 203:258 */     this.logger.log(Level.WARNING, Str.str(message), t);
/* 204:    */   }
/* 205:    */   
/* 206:    */   public void info(Object message)
/* 207:    */   {
/* 208:262 */     this.logger.log(Level.INFO, Str.str(message));
/* 209:    */   }
/* 210:    */   
/* 211:    */   public void info(Object message, Throwable t)
/* 212:    */   {
/* 213:266 */     this.logger.log(Level.INFO, Str.str(message), t);
/* 214:    */   }
/* 215:    */   
/* 216:    */   public void debug(Object message)
/* 217:    */   {
/* 218:270 */     this.logger.log(Level.FINE, Str.str(message));
/* 219:    */   }
/* 220:    */   
/* 221:    */   public void debug(Object message, Throwable t)
/* 222:    */   {
/* 223:274 */     this.logger.log(Level.FINE, Str.str(message), t);
/* 224:    */   }
/* 225:    */   
/* 226:    */   public void trace(Object message)
/* 227:    */   {
/* 228:278 */     this.logger.log(Level.FINEST, Str.str(message));
/* 229:    */   }
/* 230:    */   
/* 231:    */   public void trace(Object message, Throwable t)
/* 232:    */   {
/* 233:282 */     this.logger.log(Level.FINEST, Str.str(message), t);
/* 234:    */   }
/* 235:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.logging.JDKLogger
 * JD-Core Version:    0.7.0.1
 */