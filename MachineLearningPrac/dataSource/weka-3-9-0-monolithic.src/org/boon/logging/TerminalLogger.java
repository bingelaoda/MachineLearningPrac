/*   1:    */ package org.boon.logging;
/*   2:    */ 
/*   3:    */ import org.boon.Boon;
/*   4:    */ 
/*   5:    */ public class TerminalLogger
/*   6:    */   implements LoggerDelegate
/*   7:    */ {
/*   8: 44 */   private LogLevel level = LogLevel.DEBUG;
/*   9:    */   
/*  10:    */   public boolean infoOn()
/*  11:    */   {
/*  12: 54 */     switch (1.$SwitchMap$org$boon$logging$LogLevel[this.level.ordinal()])
/*  13:    */     {
/*  14:    */     case 1: 
/*  15:    */     case 2: 
/*  16:    */     case 3: 
/*  17:    */     case 4: 
/*  18: 59 */       return true;
/*  19:    */     }
/*  20: 61 */     return false;
/*  21:    */   }
/*  22:    */   
/*  23:    */   public boolean debugOn()
/*  24:    */   {
/*  25: 66 */     switch (1.$SwitchMap$org$boon$logging$LogLevel[this.level.ordinal()])
/*  26:    */     {
/*  27:    */     case 1: 
/*  28:    */     case 2: 
/*  29:    */     case 3: 
/*  30:    */     case 4: 
/*  31:    */     case 5: 
/*  32:    */     case 6: 
/*  33: 73 */       return true;
/*  34:    */     }
/*  35: 75 */     return false;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public boolean traceOn()
/*  39:    */   {
/*  40: 80 */     switch (1.$SwitchMap$org$boon$logging$LogLevel[this.level.ordinal()])
/*  41:    */     {
/*  42:    */     case 1: 
/*  43:    */     case 2: 
/*  44:    */     case 3: 
/*  45:    */     case 4: 
/*  46:    */     case 5: 
/*  47:    */     case 6: 
/*  48:    */     case 7: 
/*  49: 88 */       return true;
/*  50:    */     }
/*  51: 90 */     return false;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public TerminalLogger level(LogLevel level)
/*  55:    */   {
/*  56: 95 */     this.level = level;
/*  57: 96 */     return this;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public LoggerDelegate turnOff()
/*  61:    */   {
/*  62:101 */     this.level = LogLevel.OFF;
/*  63:102 */     return this;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public void fatal(Object... messages)
/*  67:    */   {
/*  68:107 */     if (this.level.ordinal() <= LogLevel.FATAL.ordinal()) {
/*  69:108 */       Boon.puts(new Object[] { LogLevel.FATAL, messages });
/*  70:    */     }
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void fatal(Throwable t, Object... messages)
/*  74:    */   {
/*  75:114 */     if (this.level.ordinal() <= LogLevel.FATAL.ordinal()) {
/*  76:115 */       Boon.puts(new Object[] { LogLevel.FATAL, t, messages });
/*  77:    */     }
/*  78:    */   }
/*  79:    */   
/*  80:    */   public void error(Object... messages)
/*  81:    */   {
/*  82:122 */     if (this.level.ordinal() <= LogLevel.ERROR.ordinal()) {
/*  83:123 */       Boon.puts(new Object[] { LogLevel.ERROR, messages });
/*  84:    */     }
/*  85:    */   }
/*  86:    */   
/*  87:    */   public void error(Throwable t, Object... messages)
/*  88:    */   {
/*  89:130 */     if (this.level.ordinal() <= LogLevel.ERROR.ordinal()) {
/*  90:131 */       Boon.puts(new Object[] { LogLevel.ERROR, t, messages });
/*  91:    */     }
/*  92:    */   }
/*  93:    */   
/*  94:    */   public void warn(Object... messages)
/*  95:    */   {
/*  96:138 */     if (this.level.ordinal() <= LogLevel.WARN.ordinal()) {
/*  97:139 */       Boon.puts(new Object[] { LogLevel.WARN, messages });
/*  98:    */     }
/*  99:    */   }
/* 100:    */   
/* 101:    */   public void warn(Throwable t, Object... messages)
/* 102:    */   {
/* 103:146 */     if (this.level.ordinal() <= LogLevel.WARN.ordinal()) {
/* 104:147 */       Boon.puts(new Object[] { LogLevel.WARN, t, messages });
/* 105:    */     }
/* 106:    */   }
/* 107:    */   
/* 108:    */   public void info(Object... messages)
/* 109:    */   {
/* 110:154 */     if (this.level.ordinal() <= LogLevel.INFO.ordinal()) {
/* 111:155 */       Boon.puts(new Object[] { LogLevel.INFO, messages });
/* 112:    */     }
/* 113:    */   }
/* 114:    */   
/* 115:    */   public void info(Throwable t, Object... messages)
/* 116:    */   {
/* 117:162 */     if (this.level.ordinal() <= LogLevel.INFO.ordinal()) {
/* 118:163 */       Boon.puts(new Object[] { LogLevel.INFO, t, messages });
/* 119:    */     }
/* 120:    */   }
/* 121:    */   
/* 122:    */   public void config(Object... messages)
/* 123:    */   {
/* 124:170 */     if (this.level.ordinal() <= LogLevel.CONFIG.ordinal()) {
/* 125:171 */       Boon.puts(new Object[] { LogLevel.CONFIG, messages });
/* 126:    */     }
/* 127:    */   }
/* 128:    */   
/* 129:    */   public void config(Throwable t, Object... messages)
/* 130:    */   {
/* 131:178 */     if (this.level.ordinal() <= LogLevel.CONFIG.ordinal()) {
/* 132:179 */       Boon.puts(new Object[] { LogLevel.CONFIG, t, messages });
/* 133:    */     }
/* 134:    */   }
/* 135:    */   
/* 136:    */   public void debug(Object... messages)
/* 137:    */   {
/* 138:186 */     if (this.level.ordinal() <= LogLevel.DEBUG.ordinal()) {
/* 139:187 */       Boon.puts(new Object[] { LogLevel.DEBUG, messages });
/* 140:    */     }
/* 141:    */   }
/* 142:    */   
/* 143:    */   public void debug(Throwable t, Object... messages)
/* 144:    */   {
/* 145:194 */     if (this.level.ordinal() <= LogLevel.DEBUG.ordinal()) {
/* 146:195 */       Boon.puts(new Object[] { LogLevel.DEBUG, t, messages });
/* 147:    */     }
/* 148:    */   }
/* 149:    */   
/* 150:    */   public void trace(Object... messages)
/* 151:    */   {
/* 152:202 */     if (this.level.ordinal() <= LogLevel.TRACE.ordinal()) {
/* 153:203 */       Boon.puts(new Object[] { LogLevel.TRACE, messages });
/* 154:    */     }
/* 155:    */   }
/* 156:    */   
/* 157:    */   public void trace(Throwable t, Object... messages)
/* 158:    */   {
/* 159:210 */     if (this.level.ordinal() <= LogLevel.TRACE.ordinal()) {
/* 160:211 */       Boon.puts(new Object[] { LogLevel.TRACE, t, messages });
/* 161:    */     }
/* 162:    */   }
/* 163:    */   
/* 164:    */   public void fatal(Object message)
/* 165:    */   {
/* 166:218 */     if (this.level.ordinal() <= LogLevel.FATAL.ordinal()) {
/* 167:219 */       Boon.puts(new Object[] { LogLevel.FATAL, message });
/* 168:    */     }
/* 169:    */   }
/* 170:    */   
/* 171:    */   public void fatal(Object message, Throwable t)
/* 172:    */   {
/* 173:226 */     if (this.level.ordinal() <= LogLevel.FATAL.ordinal()) {
/* 174:227 */       Boon.puts(new Object[] { LogLevel.FATAL, t, message });
/* 175:    */     }
/* 176:    */   }
/* 177:    */   
/* 178:    */   public void error(Object message)
/* 179:    */   {
/* 180:234 */     if (this.level.ordinal() <= LogLevel.ERROR.ordinal()) {
/* 181:235 */       Boon.puts(new Object[] { LogLevel.ERROR, message });
/* 182:    */     }
/* 183:    */   }
/* 184:    */   
/* 185:    */   public void error(Object message, Throwable t)
/* 186:    */   {
/* 187:242 */     if (this.level.ordinal() <= LogLevel.ERROR.ordinal()) {
/* 188:243 */       Boon.puts(new Object[] { LogLevel.ERROR, t, message });
/* 189:    */     }
/* 190:    */   }
/* 191:    */   
/* 192:    */   public void warn(Object message)
/* 193:    */   {
/* 194:250 */     if (this.level.ordinal() <= LogLevel.WARN.ordinal()) {
/* 195:251 */       Boon.puts(new Object[] { LogLevel.WARN, message });
/* 196:    */     }
/* 197:    */   }
/* 198:    */   
/* 199:    */   public void warn(Object message, Throwable t)
/* 200:    */   {
/* 201:258 */     if (this.level.ordinal() <= LogLevel.WARN.ordinal()) {
/* 202:259 */       Boon.puts(new Object[] { LogLevel.WARN, t, message });
/* 203:    */     }
/* 204:    */   }
/* 205:    */   
/* 206:    */   public void info(Object message)
/* 207:    */   {
/* 208:266 */     if (this.level.ordinal() <= LogLevel.INFO.ordinal()) {
/* 209:267 */       Boon.puts(new Object[] { LogLevel.INFO, message });
/* 210:    */     }
/* 211:    */   }
/* 212:    */   
/* 213:    */   public void info(Object message, Throwable t)
/* 214:    */   {
/* 215:274 */     if (this.level.ordinal() <= LogLevel.INFO.ordinal()) {
/* 216:275 */       Boon.puts(new Object[] { LogLevel.INFO, t, message });
/* 217:    */     }
/* 218:    */   }
/* 219:    */   
/* 220:    */   public void debug(Object message)
/* 221:    */   {
/* 222:282 */     if (this.level.ordinal() <= LogLevel.DEBUG.ordinal()) {
/* 223:283 */       Boon.puts(new Object[] { LogLevel.DEBUG, message });
/* 224:    */     }
/* 225:    */   }
/* 226:    */   
/* 227:    */   public void debug(Object message, Throwable t)
/* 228:    */   {
/* 229:290 */     if (this.level.ordinal() <= LogLevel.DEBUG.ordinal()) {
/* 230:291 */       Boon.puts(new Object[] { LogLevel.DEBUG, t, message });
/* 231:    */     }
/* 232:    */   }
/* 233:    */   
/* 234:    */   public void trace(Object message)
/* 235:    */   {
/* 236:298 */     if (this.level.ordinal() <= LogLevel.TRACE.ordinal()) {
/* 237:299 */       Boon.puts(new Object[] { LogLevel.TRACE, message });
/* 238:    */     }
/* 239:    */   }
/* 240:    */   
/* 241:    */   public void trace(Object message, Throwable t)
/* 242:    */   {
/* 243:306 */     if (this.level.ordinal() <= LogLevel.TRACE.ordinal()) {
/* 244:307 */       Boon.puts(new Object[] { LogLevel.TRACE, t, message });
/* 245:    */     }
/* 246:    */   }
/* 247:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.logging.TerminalLogger
 * JD-Core Version:    0.7.0.1
 */