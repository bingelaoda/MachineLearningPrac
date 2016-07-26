/*   1:    */ package org.boon.logging;
/*   2:    */ 
/*   3:    */ import org.boon.primitive.CharBuf;
/*   4:    */ 
/*   5:    */ public class InMemoryThreadLocalLogger
/*   6:    */   implements LoggerDelegate
/*   7:    */ {
/*   8: 44 */   private LogLevel level = LogLevel.DEBUG;
/*   9: 46 */   private static ThreadLocal<CharBuf> bufTL = new ThreadLocal();
/*  10:    */   
/*  11:    */   public InMemoryThreadLocalLogger(LogLevel level)
/*  12:    */   {
/*  13: 50 */     this.level = level;
/*  14:    */   }
/*  15:    */   
/*  16:    */   private static CharBuf buf()
/*  17:    */   {
/*  18: 54 */     CharBuf buf = (CharBuf)bufTL.get();
/*  19: 55 */     if (buf == null)
/*  20:    */     {
/*  21: 56 */       buf = CharBuf.create(100);
/*  22: 57 */       bufTL.set(buf);
/*  23:    */     }
/*  24: 59 */     return buf;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public static void start()
/*  28:    */   {
/*  29: 64 */     buf();
/*  30:    */   }
/*  31:    */   
/*  32:    */   public static CharBuf getBuffer()
/*  33:    */   {
/*  34: 69 */     return buf();
/*  35:    */   }
/*  36:    */   
/*  37:    */   public static void clear()
/*  38:    */   {
/*  39: 73 */     bufTL.set(null);
/*  40:    */   }
/*  41:    */   
/*  42:    */   public boolean infoOn()
/*  43:    */   {
/*  44: 84 */     switch (1.$SwitchMap$org$boon$logging$LogLevel[this.level.ordinal()])
/*  45:    */     {
/*  46:    */     case 1: 
/*  47:    */     case 2: 
/*  48:    */     case 3: 
/*  49:    */     case 4: 
/*  50: 89 */       return true;
/*  51:    */     }
/*  52: 91 */     return false;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public boolean debugOn()
/*  56:    */   {
/*  57: 96 */     switch (1.$SwitchMap$org$boon$logging$LogLevel[this.level.ordinal()])
/*  58:    */     {
/*  59:    */     case 1: 
/*  60:    */     case 2: 
/*  61:    */     case 3: 
/*  62:    */     case 4: 
/*  63:    */     case 5: 
/*  64:    */     case 6: 
/*  65:103 */       return true;
/*  66:    */     }
/*  67:105 */     return false;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public boolean traceOn()
/*  71:    */   {
/*  72:110 */     switch (1.$SwitchMap$org$boon$logging$LogLevel[this.level.ordinal()])
/*  73:    */     {
/*  74:    */     case 1: 
/*  75:    */     case 2: 
/*  76:    */     case 3: 
/*  77:    */     case 4: 
/*  78:    */     case 5: 
/*  79:    */     case 6: 
/*  80:    */     case 7: 
/*  81:118 */       return true;
/*  82:    */     }
/*  83:120 */     return false;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public LoggerDelegate level(LogLevel level)
/*  87:    */   {
/*  88:125 */     this.level = level;
/*  89:126 */     return this;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public LoggerDelegate turnOff()
/*  93:    */   {
/*  94:131 */     this.level = LogLevel.OFF;
/*  95:132 */     return this;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public void fatal(Object... messages)
/*  99:    */   {
/* 100:137 */     if (this.level.ordinal() <= LogLevel.FATAL.ordinal()) {
/* 101:138 */       buf().puts(new Object[] { LogLevel.FATAL, messages });
/* 102:    */     }
/* 103:    */   }
/* 104:    */   
/* 105:    */   public void fatal(Throwable t, Object... messages)
/* 106:    */   {
/* 107:144 */     if (this.level.ordinal() <= LogLevel.FATAL.ordinal()) {
/* 108:145 */       buf().puts(new Object[] { LogLevel.FATAL, t, messages });
/* 109:    */     }
/* 110:    */   }
/* 111:    */   
/* 112:    */   public void error(Object... messages)
/* 113:    */   {
/* 114:152 */     if (this.level.ordinal() <= LogLevel.ERROR.ordinal()) {
/* 115:153 */       buf().puts(new Object[] { LogLevel.ERROR, messages });
/* 116:    */     }
/* 117:    */   }
/* 118:    */   
/* 119:    */   public void error(Throwable t, Object... messages)
/* 120:    */   {
/* 121:160 */     if (this.level.ordinal() <= LogLevel.ERROR.ordinal()) {
/* 122:161 */       buf().puts(new Object[] { LogLevel.ERROR, t, messages });
/* 123:    */     }
/* 124:    */   }
/* 125:    */   
/* 126:    */   public void warn(Object... messages)
/* 127:    */   {
/* 128:168 */     if (this.level.ordinal() <= LogLevel.WARN.ordinal()) {
/* 129:169 */       buf().puts(new Object[] { LogLevel.WARN, messages });
/* 130:    */     }
/* 131:    */   }
/* 132:    */   
/* 133:    */   public void warn(Throwable t, Object... messages)
/* 134:    */   {
/* 135:176 */     if (this.level.ordinal() <= LogLevel.WARN.ordinal()) {
/* 136:177 */       buf().puts(new Object[] { LogLevel.WARN, t, messages });
/* 137:    */     }
/* 138:    */   }
/* 139:    */   
/* 140:    */   public void info(Object... messages)
/* 141:    */   {
/* 142:184 */     if (this.level.ordinal() <= LogLevel.INFO.ordinal()) {
/* 143:185 */       buf().puts(new Object[] { LogLevel.INFO, messages });
/* 144:    */     }
/* 145:    */   }
/* 146:    */   
/* 147:    */   public void info(Throwable t, Object... messages)
/* 148:    */   {
/* 149:192 */     if (this.level.ordinal() <= LogLevel.INFO.ordinal()) {
/* 150:193 */       buf().puts(new Object[] { LogLevel.INFO, t, messages });
/* 151:    */     }
/* 152:    */   }
/* 153:    */   
/* 154:    */   public void config(Object... messages)
/* 155:    */   {
/* 156:200 */     if (this.level.ordinal() <= LogLevel.CONFIG.ordinal()) {
/* 157:201 */       buf().puts(new Object[] { LogLevel.CONFIG, messages });
/* 158:    */     }
/* 159:    */   }
/* 160:    */   
/* 161:    */   public void config(Throwable t, Object... messages)
/* 162:    */   {
/* 163:208 */     if (this.level.ordinal() <= LogLevel.CONFIG.ordinal()) {
/* 164:209 */       buf().puts(new Object[] { LogLevel.CONFIG, t, messages });
/* 165:    */     }
/* 166:    */   }
/* 167:    */   
/* 168:    */   public void debug(Object... messages)
/* 169:    */   {
/* 170:216 */     if (this.level.ordinal() <= LogLevel.DEBUG.ordinal()) {
/* 171:217 */       buf().puts(new Object[] { LogLevel.DEBUG, messages });
/* 172:    */     }
/* 173:    */   }
/* 174:    */   
/* 175:    */   public void debug(Throwable t, Object... messages)
/* 176:    */   {
/* 177:224 */     if (this.level.ordinal() <= LogLevel.DEBUG.ordinal()) {
/* 178:225 */       buf().puts(new Object[] { LogLevel.DEBUG, t, messages });
/* 179:    */     }
/* 180:    */   }
/* 181:    */   
/* 182:    */   public void trace(Object... messages)
/* 183:    */   {
/* 184:232 */     if (this.level.ordinal() <= LogLevel.TRACE.ordinal()) {
/* 185:233 */       buf().puts(new Object[] { LogLevel.TRACE, messages });
/* 186:    */     }
/* 187:    */   }
/* 188:    */   
/* 189:    */   public void trace(Throwable t, Object... messages)
/* 190:    */   {
/* 191:240 */     if (this.level.ordinal() <= LogLevel.TRACE.ordinal()) {
/* 192:241 */       buf().puts(new Object[] { LogLevel.TRACE, t, messages });
/* 193:    */     }
/* 194:    */   }
/* 195:    */   
/* 196:    */   public void fatal(Object message)
/* 197:    */   {
/* 198:248 */     if (this.level.ordinal() <= LogLevel.FATAL.ordinal()) {
/* 199:249 */       buf().puts(new Object[] { LogLevel.FATAL, message });
/* 200:    */     }
/* 201:    */   }
/* 202:    */   
/* 203:    */   public void fatal(Object message, Throwable t)
/* 204:    */   {
/* 205:256 */     if (this.level.ordinal() <= LogLevel.FATAL.ordinal()) {
/* 206:257 */       buf().puts(new Object[] { LogLevel.FATAL, t, message });
/* 207:    */     }
/* 208:    */   }
/* 209:    */   
/* 210:    */   public void error(Object message)
/* 211:    */   {
/* 212:264 */     if (this.level.ordinal() <= LogLevel.ERROR.ordinal()) {
/* 213:265 */       buf().puts(new Object[] { LogLevel.ERROR, message });
/* 214:    */     }
/* 215:    */   }
/* 216:    */   
/* 217:    */   public void error(Object message, Throwable t)
/* 218:    */   {
/* 219:272 */     if (this.level.ordinal() <= LogLevel.ERROR.ordinal()) {
/* 220:273 */       buf().puts(new Object[] { LogLevel.ERROR, t, message });
/* 221:    */     }
/* 222:    */   }
/* 223:    */   
/* 224:    */   public void warn(Object message)
/* 225:    */   {
/* 226:280 */     if (this.level.ordinal() <= LogLevel.WARN.ordinal()) {
/* 227:281 */       buf().puts(new Object[] { LogLevel.WARN, message });
/* 228:    */     }
/* 229:    */   }
/* 230:    */   
/* 231:    */   public void warn(Object message, Throwable t)
/* 232:    */   {
/* 233:288 */     if (this.level.ordinal() <= LogLevel.WARN.ordinal()) {
/* 234:289 */       buf().puts(new Object[] { LogLevel.WARN, t, message });
/* 235:    */     }
/* 236:    */   }
/* 237:    */   
/* 238:    */   public void info(Object message)
/* 239:    */   {
/* 240:296 */     if (this.level.ordinal() <= LogLevel.INFO.ordinal()) {
/* 241:297 */       buf().puts(new Object[] { LogLevel.INFO, message });
/* 242:    */     }
/* 243:    */   }
/* 244:    */   
/* 245:    */   public void info(Object message, Throwable t)
/* 246:    */   {
/* 247:304 */     if (this.level.ordinal() <= LogLevel.INFO.ordinal()) {
/* 248:305 */       buf().puts(new Object[] { LogLevel.INFO, t, message });
/* 249:    */     }
/* 250:    */   }
/* 251:    */   
/* 252:    */   public void debug(Object message)
/* 253:    */   {
/* 254:312 */     if (this.level.ordinal() <= LogLevel.DEBUG.ordinal()) {
/* 255:313 */       buf().puts(new Object[] { LogLevel.DEBUG, message });
/* 256:    */     }
/* 257:    */   }
/* 258:    */   
/* 259:    */   public void debug(Object message, Throwable t)
/* 260:    */   {
/* 261:320 */     if (this.level.ordinal() <= LogLevel.DEBUG.ordinal()) {
/* 262:321 */       buf().puts(new Object[] { LogLevel.DEBUG, t, message });
/* 263:    */     }
/* 264:    */   }
/* 265:    */   
/* 266:    */   public void trace(Object message)
/* 267:    */   {
/* 268:328 */     if (this.level.ordinal() <= LogLevel.TRACE.ordinal()) {
/* 269:329 */       buf().puts(new Object[] { LogLevel.TRACE, message });
/* 270:    */     }
/* 271:    */   }
/* 272:    */   
/* 273:    */   public void trace(Object message, Throwable t)
/* 274:    */   {
/* 275:336 */     if (this.level.ordinal() <= LogLevel.TRACE.ordinal()) {
/* 276:337 */       buf().puts(new Object[] { LogLevel.TRACE, t, message });
/* 277:    */     }
/* 278:    */   }
/* 279:    */   
/* 280:    */   public CharBuf getBuf()
/* 281:    */   {
/* 282:342 */     return buf();
/* 283:    */   }
/* 284:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.logging.InMemoryThreadLocalLogger
 * JD-Core Version:    0.7.0.1
 */