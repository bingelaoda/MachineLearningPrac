/*   1:    */ package org.boon.logging;
/*   2:    */ 
/*   3:    */ import org.boon.primitive.CharBuf;
/*   4:    */ 
/*   5:    */ public class InMemoryLogger
/*   6:    */   implements LoggerDelegate
/*   7:    */ {
/*   8: 39 */   private LogLevel level = LogLevel.DEBUG;
/*   9: 41 */   private CharBuf buf = CharBuf.create(100);
/*  10:    */   
/*  11:    */   public boolean infoOn()
/*  12:    */   {
/*  13: 51 */     switch (1.$SwitchMap$org$boon$logging$LogLevel[this.level.ordinal()])
/*  14:    */     {
/*  15:    */     case 1: 
/*  16:    */     case 2: 
/*  17:    */     case 3: 
/*  18:    */     case 4: 
/*  19: 56 */       return true;
/*  20:    */     }
/*  21: 58 */     return false;
/*  22:    */   }
/*  23:    */   
/*  24:    */   public boolean debugOn()
/*  25:    */   {
/*  26: 63 */     switch (1.$SwitchMap$org$boon$logging$LogLevel[this.level.ordinal()])
/*  27:    */     {
/*  28:    */     case 1: 
/*  29:    */     case 2: 
/*  30:    */     case 3: 
/*  31:    */     case 4: 
/*  32:    */     case 5: 
/*  33:    */     case 6: 
/*  34: 70 */       return true;
/*  35:    */     }
/*  36: 72 */     return false;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public boolean traceOn()
/*  40:    */   {
/*  41: 77 */     switch (1.$SwitchMap$org$boon$logging$LogLevel[this.level.ordinal()])
/*  42:    */     {
/*  43:    */     case 1: 
/*  44:    */     case 2: 
/*  45:    */     case 3: 
/*  46:    */     case 4: 
/*  47:    */     case 5: 
/*  48:    */     case 6: 
/*  49:    */     case 7: 
/*  50: 85 */       return true;
/*  51:    */     }
/*  52: 87 */     return false;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public LoggerDelegate level(LogLevel level)
/*  56:    */   {
/*  57: 92 */     this.level = level;
/*  58: 93 */     return this;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public LoggerDelegate turnOff()
/*  62:    */   {
/*  63: 98 */     this.level = LogLevel.OFF;
/*  64: 99 */     return this;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void fatal(Object... messages)
/*  68:    */   {
/*  69:104 */     if (this.level.ordinal() <= LogLevel.FATAL.ordinal()) {
/*  70:105 */       this.buf.puts(new Object[] { LogLevel.FATAL, messages });
/*  71:    */     }
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void fatal(Throwable t, Object... messages)
/*  75:    */   {
/*  76:111 */     if (this.level.ordinal() <= LogLevel.FATAL.ordinal()) {
/*  77:112 */       this.buf.puts(new Object[] { LogLevel.FATAL, t, messages });
/*  78:    */     }
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void error(Object... messages)
/*  82:    */   {
/*  83:119 */     if (this.level.ordinal() <= LogLevel.ERROR.ordinal()) {
/*  84:120 */       this.buf.puts(new Object[] { LogLevel.ERROR, messages });
/*  85:    */     }
/*  86:    */   }
/*  87:    */   
/*  88:    */   public void error(Throwable t, Object... messages)
/*  89:    */   {
/*  90:127 */     if (this.level.ordinal() <= LogLevel.ERROR.ordinal()) {
/*  91:128 */       this.buf.puts(new Object[] { LogLevel.ERROR, t, messages });
/*  92:    */     }
/*  93:    */   }
/*  94:    */   
/*  95:    */   public void warn(Object... messages)
/*  96:    */   {
/*  97:135 */     if (this.level.ordinal() <= LogLevel.WARN.ordinal()) {
/*  98:136 */       this.buf.puts(new Object[] { LogLevel.WARN, messages });
/*  99:    */     }
/* 100:    */   }
/* 101:    */   
/* 102:    */   public void warn(Throwable t, Object... messages)
/* 103:    */   {
/* 104:143 */     if (this.level.ordinal() <= LogLevel.WARN.ordinal()) {
/* 105:144 */       this.buf.puts(new Object[] { LogLevel.WARN, t, messages });
/* 106:    */     }
/* 107:    */   }
/* 108:    */   
/* 109:    */   public void info(Object... messages)
/* 110:    */   {
/* 111:151 */     if (this.level.ordinal() <= LogLevel.INFO.ordinal()) {
/* 112:152 */       this.buf.puts(new Object[] { LogLevel.INFO, messages });
/* 113:    */     }
/* 114:    */   }
/* 115:    */   
/* 116:    */   public void info(Throwable t, Object... messages)
/* 117:    */   {
/* 118:159 */     if (this.level.ordinal() <= LogLevel.INFO.ordinal()) {
/* 119:160 */       this.buf.puts(new Object[] { LogLevel.INFO, t, messages });
/* 120:    */     }
/* 121:    */   }
/* 122:    */   
/* 123:    */   public void config(Object... messages)
/* 124:    */   {
/* 125:167 */     if (this.level.ordinal() <= LogLevel.CONFIG.ordinal()) {
/* 126:168 */       this.buf.puts(new Object[] { LogLevel.CONFIG, messages });
/* 127:    */     }
/* 128:    */   }
/* 129:    */   
/* 130:    */   public void config(Throwable t, Object... messages)
/* 131:    */   {
/* 132:175 */     if (this.level.ordinal() <= LogLevel.CONFIG.ordinal()) {
/* 133:176 */       this.buf.puts(new Object[] { LogLevel.CONFIG, t, messages });
/* 134:    */     }
/* 135:    */   }
/* 136:    */   
/* 137:    */   public void debug(Object... messages)
/* 138:    */   {
/* 139:183 */     if (this.level.ordinal() <= LogLevel.DEBUG.ordinal()) {
/* 140:184 */       this.buf.puts(new Object[] { LogLevel.DEBUG, messages });
/* 141:    */     }
/* 142:    */   }
/* 143:    */   
/* 144:    */   public void debug(Throwable t, Object... messages)
/* 145:    */   {
/* 146:191 */     if (this.level.ordinal() <= LogLevel.DEBUG.ordinal()) {
/* 147:192 */       this.buf.puts(new Object[] { LogLevel.DEBUG, t, messages });
/* 148:    */     }
/* 149:    */   }
/* 150:    */   
/* 151:    */   public void trace(Object... messages)
/* 152:    */   {
/* 153:199 */     if (this.level.ordinal() <= LogLevel.TRACE.ordinal()) {
/* 154:200 */       this.buf.puts(new Object[] { LogLevel.TRACE, messages });
/* 155:    */     }
/* 156:    */   }
/* 157:    */   
/* 158:    */   public void trace(Throwable t, Object... messages)
/* 159:    */   {
/* 160:207 */     if (this.level.ordinal() <= LogLevel.TRACE.ordinal()) {
/* 161:208 */       this.buf.puts(new Object[] { LogLevel.TRACE, t, messages });
/* 162:    */     }
/* 163:    */   }
/* 164:    */   
/* 165:    */   public void fatal(Object message)
/* 166:    */   {
/* 167:215 */     if (this.level.ordinal() <= LogLevel.FATAL.ordinal()) {
/* 168:216 */       this.buf.puts(new Object[] { LogLevel.FATAL, message });
/* 169:    */     }
/* 170:    */   }
/* 171:    */   
/* 172:    */   public void fatal(Object message, Throwable t)
/* 173:    */   {
/* 174:223 */     if (this.level.ordinal() <= LogLevel.FATAL.ordinal()) {
/* 175:224 */       this.buf.puts(new Object[] { LogLevel.FATAL, t, message });
/* 176:    */     }
/* 177:    */   }
/* 178:    */   
/* 179:    */   public void error(Object message)
/* 180:    */   {
/* 181:231 */     if (this.level.ordinal() <= LogLevel.ERROR.ordinal()) {
/* 182:232 */       this.buf.puts(new Object[] { LogLevel.ERROR, message });
/* 183:    */     }
/* 184:    */   }
/* 185:    */   
/* 186:    */   public void error(Object message, Throwable t)
/* 187:    */   {
/* 188:239 */     if (this.level.ordinal() <= LogLevel.ERROR.ordinal()) {
/* 189:240 */       this.buf.puts(new Object[] { LogLevel.ERROR, t, message });
/* 190:    */     }
/* 191:    */   }
/* 192:    */   
/* 193:    */   public void warn(Object message)
/* 194:    */   {
/* 195:247 */     if (this.level.ordinal() <= LogLevel.WARN.ordinal()) {
/* 196:248 */       this.buf.puts(new Object[] { LogLevel.WARN, message });
/* 197:    */     }
/* 198:    */   }
/* 199:    */   
/* 200:    */   public void warn(Object message, Throwable t)
/* 201:    */   {
/* 202:255 */     if (this.level.ordinal() <= LogLevel.WARN.ordinal()) {
/* 203:256 */       this.buf.puts(new Object[] { LogLevel.WARN, t, message });
/* 204:    */     }
/* 205:    */   }
/* 206:    */   
/* 207:    */   public void info(Object message)
/* 208:    */   {
/* 209:263 */     if (this.level.ordinal() <= LogLevel.INFO.ordinal()) {
/* 210:264 */       this.buf.puts(new Object[] { LogLevel.INFO, message });
/* 211:    */     }
/* 212:    */   }
/* 213:    */   
/* 214:    */   public void info(Object message, Throwable t)
/* 215:    */   {
/* 216:271 */     if (this.level.ordinal() <= LogLevel.INFO.ordinal()) {
/* 217:272 */       this.buf.puts(new Object[] { LogLevel.INFO, t, message });
/* 218:    */     }
/* 219:    */   }
/* 220:    */   
/* 221:    */   public void debug(Object message)
/* 222:    */   {
/* 223:279 */     if (this.level.ordinal() <= LogLevel.DEBUG.ordinal()) {
/* 224:280 */       this.buf.puts(new Object[] { LogLevel.DEBUG, message });
/* 225:    */     }
/* 226:    */   }
/* 227:    */   
/* 228:    */   public void debug(Object message, Throwable t)
/* 229:    */   {
/* 230:287 */     if (this.level.ordinal() <= LogLevel.DEBUG.ordinal()) {
/* 231:288 */       this.buf.puts(new Object[] { LogLevel.DEBUG, t, message });
/* 232:    */     }
/* 233:    */   }
/* 234:    */   
/* 235:    */   public void trace(Object message)
/* 236:    */   {
/* 237:295 */     if (this.level.ordinal() <= LogLevel.TRACE.ordinal()) {
/* 238:296 */       this.buf.puts(new Object[] { LogLevel.TRACE, message });
/* 239:    */     }
/* 240:    */   }
/* 241:    */   
/* 242:    */   public void trace(Object message, Throwable t)
/* 243:    */   {
/* 244:303 */     if (this.level.ordinal() <= LogLevel.TRACE.ordinal()) {
/* 245:304 */       this.buf.puts(new Object[] { LogLevel.TRACE, t, message });
/* 246:    */     }
/* 247:    */   }
/* 248:    */   
/* 249:    */   public CharBuf getBuf()
/* 250:    */   {
/* 251:309 */     return this.buf;
/* 252:    */   }
/* 253:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.logging.InMemoryLogger
 * JD-Core Version:    0.7.0.1
 */