/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Date;
/*   5:    */ import java.util.Vector;
/*   6:    */ 
/*   7:    */ public class Tee
/*   8:    */   extends PrintStream
/*   9:    */   implements RevisionHandler
/*  10:    */ {
/*  11: 58 */   protected Vector<PrintStream> m_Streams = new Vector();
/*  12: 61 */   protected Vector<Boolean> m_Timestamps = new Vector();
/*  13: 64 */   protected Vector<String> m_Prefixes = new Vector();
/*  14: 67 */   protected PrintStream m_Default = null;
/*  15:    */   
/*  16:    */   public Tee()
/*  17:    */   {
/*  18: 73 */     this(System.out);
/*  19:    */   }
/*  20:    */   
/*  21:    */   public Tee(PrintStream def)
/*  22:    */   {
/*  23: 83 */     super(def);
/*  24:    */     
/*  25: 85 */     this.m_Default = def;
/*  26: 86 */     clear();
/*  27:    */   }
/*  28:    */   
/*  29:    */   public void clear()
/*  30:    */   {
/*  31: 96 */     this.m_Streams.clear();
/*  32: 97 */     this.m_Timestamps.clear();
/*  33: 98 */     this.m_Prefixes.clear();
/*  34:100 */     if (getDefault() != null) {
/*  35:101 */       add(getDefault());
/*  36:    */     }
/*  37:    */   }
/*  38:    */   
/*  39:    */   public PrintStream getDefault()
/*  40:    */   {
/*  41:111 */     return this.m_Default;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public void add(PrintStream p)
/*  45:    */   {
/*  46:121 */     add(p, false);
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void add(PrintStream p, boolean timestamp)
/*  50:    */   {
/*  51:131 */     add(p, timestamp, "");
/*  52:    */   }
/*  53:    */   
/*  54:    */   public void add(PrintStream p, boolean timestamp, String prefix)
/*  55:    */   {
/*  56:142 */     if (this.m_Streams.contains(p)) {
/*  57:143 */       remove(p);
/*  58:    */     }
/*  59:146 */     if (prefix == null) {
/*  60:147 */       prefix = "";
/*  61:    */     }
/*  62:149 */     this.m_Streams.add(p);
/*  63:150 */     this.m_Timestamps.add(new Boolean(timestamp));
/*  64:151 */     this.m_Prefixes.add(prefix);
/*  65:    */   }
/*  66:    */   
/*  67:    */   public PrintStream get(int index)
/*  68:    */   {
/*  69:161 */     if ((index >= 0) && (index < size())) {
/*  70:162 */       return (PrintStream)this.m_Streams.get(index);
/*  71:    */     }
/*  72:164 */     return null;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public PrintStream remove(PrintStream p)
/*  76:    */   {
/*  77:176 */     if (contains(p))
/*  78:    */     {
/*  79:177 */       int index = this.m_Streams.indexOf(p);
/*  80:178 */       this.m_Timestamps.remove(index);
/*  81:179 */       this.m_Prefixes.remove(index);
/*  82:180 */       return (PrintStream)this.m_Streams.remove(index);
/*  83:    */     }
/*  84:183 */     return null;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public PrintStream remove(int index)
/*  88:    */   {
/*  89:194 */     if ((index >= 0) && (index < size()))
/*  90:    */     {
/*  91:195 */       this.m_Timestamps.remove(index);
/*  92:196 */       this.m_Prefixes.remove(index);
/*  93:197 */       return (PrintStream)this.m_Streams.remove(index);
/*  94:    */     }
/*  95:200 */     return null;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public boolean contains(PrintStream p)
/*  99:    */   {
/* 100:211 */     return this.m_Streams.contains(p);
/* 101:    */   }
/* 102:    */   
/* 103:    */   public int size()
/* 104:    */   {
/* 105:220 */     return this.m_Streams.size();
/* 106:    */   }
/* 107:    */   
/* 108:    */   private void printHeader()
/* 109:    */   {
/* 110:228 */     for (int i = 0; i < size(); i++)
/* 111:    */     {
/* 112:230 */       if (!((String)this.m_Prefixes.get(i)).equals("")) {
/* 113:231 */         ((PrintStream)this.m_Streams.get(i)).print("[" + (String)this.m_Prefixes.get(i) + "]\t");
/* 114:    */       }
/* 115:234 */       if (((Boolean)this.m_Timestamps.get(i)).booleanValue()) {
/* 116:235 */         ((PrintStream)this.m_Streams.get(i)).print("[" + new Date() + "]\t");
/* 117:    */       }
/* 118:    */     }
/* 119:    */   }
/* 120:    */   
/* 121:    */   public void flush()
/* 122:    */   {
/* 123:243 */     for (int i = 0; i < size(); i++) {
/* 124:244 */       ((PrintStream)this.m_Streams.get(i)).flush();
/* 125:    */     }
/* 126:    */   }
/* 127:    */   
/* 128:    */   public void print(int x)
/* 129:    */   {
/* 130:253 */     printHeader();
/* 131:254 */     for (int i = 0; i < size(); i++) {
/* 132:255 */       ((PrintStream)this.m_Streams.get(i)).print(x);
/* 133:    */     }
/* 134:256 */     flush();
/* 135:    */   }
/* 136:    */   
/* 137:    */   public void print(long x)
/* 138:    */   {
/* 139:265 */     printHeader();
/* 140:266 */     for (int i = 0; i < size(); i++) {
/* 141:267 */       ((PrintStream)this.m_Streams.get(i)).print(x);
/* 142:    */     }
/* 143:268 */     flush();
/* 144:    */   }
/* 145:    */   
/* 146:    */   public void print(float x)
/* 147:    */   {
/* 148:277 */     printHeader();
/* 149:278 */     for (int i = 0; i < size(); i++) {
/* 150:279 */       ((PrintStream)this.m_Streams.get(i)).print(x);
/* 151:    */     }
/* 152:280 */     flush();
/* 153:    */   }
/* 154:    */   
/* 155:    */   public void print(double x)
/* 156:    */   {
/* 157:289 */     printHeader();
/* 158:290 */     for (int i = 0; i < size(); i++) {
/* 159:291 */       ((PrintStream)this.m_Streams.get(i)).print(x);
/* 160:    */     }
/* 161:292 */     flush();
/* 162:    */   }
/* 163:    */   
/* 164:    */   public void print(boolean x)
/* 165:    */   {
/* 166:301 */     printHeader();
/* 167:302 */     for (int i = 0; i < size(); i++) {
/* 168:303 */       ((PrintStream)this.m_Streams.get(i)).print(x);
/* 169:    */     }
/* 170:304 */     flush();
/* 171:    */   }
/* 172:    */   
/* 173:    */   public void print(char x)
/* 174:    */   {
/* 175:313 */     printHeader();
/* 176:314 */     for (int i = 0; i < size(); i++) {
/* 177:315 */       ((PrintStream)this.m_Streams.get(i)).print(x);
/* 178:    */     }
/* 179:316 */     flush();
/* 180:    */   }
/* 181:    */   
/* 182:    */   public void print(char[] x)
/* 183:    */   {
/* 184:325 */     printHeader();
/* 185:326 */     for (int i = 0; i < size(); i++) {
/* 186:327 */       ((PrintStream)this.m_Streams.get(i)).print(x);
/* 187:    */     }
/* 188:328 */     flush();
/* 189:    */   }
/* 190:    */   
/* 191:    */   public void print(String x)
/* 192:    */   {
/* 193:337 */     printHeader();
/* 194:338 */     for (int i = 0; i < size(); i++) {
/* 195:339 */       ((PrintStream)this.m_Streams.get(i)).print(x);
/* 196:    */     }
/* 197:340 */     flush();
/* 198:    */   }
/* 199:    */   
/* 200:    */   public void print(Object x)
/* 201:    */   {
/* 202:349 */     printHeader();
/* 203:350 */     for (int i = 0; i < size(); i++) {
/* 204:351 */       ((PrintStream)this.m_Streams.get(i)).print(x);
/* 205:    */     }
/* 206:352 */     flush();
/* 207:    */   }
/* 208:    */   
/* 209:    */   public void println()
/* 210:    */   {
/* 211:359 */     printHeader();
/* 212:360 */     for (int i = 0; i < size(); i++) {
/* 213:361 */       ((PrintStream)this.m_Streams.get(i)).println();
/* 214:    */     }
/* 215:362 */     flush();
/* 216:    */   }
/* 217:    */   
/* 218:    */   public void println(int x)
/* 219:    */   {
/* 220:371 */     printHeader();
/* 221:372 */     for (int i = 0; i < size(); i++) {
/* 222:373 */       ((PrintStream)this.m_Streams.get(i)).println(x);
/* 223:    */     }
/* 224:374 */     flush();
/* 225:    */   }
/* 226:    */   
/* 227:    */   public void println(long x)
/* 228:    */   {
/* 229:383 */     printHeader();
/* 230:384 */     for (int i = 0; i < size(); i++) {
/* 231:385 */       ((PrintStream)this.m_Streams.get(i)).println(x);
/* 232:    */     }
/* 233:386 */     flush();
/* 234:    */   }
/* 235:    */   
/* 236:    */   public void println(float x)
/* 237:    */   {
/* 238:395 */     printHeader();
/* 239:396 */     for (int i = 0; i < size(); i++) {
/* 240:397 */       ((PrintStream)this.m_Streams.get(i)).println(x);
/* 241:    */     }
/* 242:398 */     flush();
/* 243:    */   }
/* 244:    */   
/* 245:    */   public void println(double x)
/* 246:    */   {
/* 247:407 */     printHeader();
/* 248:408 */     for (int i = 0; i < size(); i++) {
/* 249:409 */       ((PrintStream)this.m_Streams.get(i)).println(x);
/* 250:    */     }
/* 251:410 */     flush();
/* 252:    */   }
/* 253:    */   
/* 254:    */   public void println(boolean x)
/* 255:    */   {
/* 256:419 */     printHeader();
/* 257:420 */     for (int i = 0; i < size(); i++) {
/* 258:421 */       ((PrintStream)this.m_Streams.get(i)).println(x);
/* 259:    */     }
/* 260:422 */     flush();
/* 261:    */   }
/* 262:    */   
/* 263:    */   public void println(char x)
/* 264:    */   {
/* 265:431 */     printHeader();
/* 266:432 */     for (int i = 0; i < size(); i++) {
/* 267:433 */       ((PrintStream)this.m_Streams.get(i)).println(x);
/* 268:    */     }
/* 269:434 */     flush();
/* 270:    */   }
/* 271:    */   
/* 272:    */   public void println(char[] x)
/* 273:    */   {
/* 274:443 */     printHeader();
/* 275:444 */     for (int i = 0; i < size(); i++) {
/* 276:445 */       ((PrintStream)this.m_Streams.get(i)).println(x);
/* 277:    */     }
/* 278:446 */     flush();
/* 279:    */   }
/* 280:    */   
/* 281:    */   public void println(String x)
/* 282:    */   {
/* 283:455 */     printHeader();
/* 284:456 */     for (int i = 0; i < size(); i++) {
/* 285:457 */       ((PrintStream)this.m_Streams.get(i)).println(x);
/* 286:    */     }
/* 287:458 */     flush();
/* 288:    */   }
/* 289:    */   
/* 290:    */   public void println(Object x)
/* 291:    */   {
/* 292:473 */     if ((x instanceof Throwable))
/* 293:    */     {
/* 294:474 */       Throwable t = (Throwable)x;
/* 295:475 */       StackTraceElement[] trace = t.getStackTrace();
/* 296:476 */       String line = t.toString() + "\n";
/* 297:477 */       for (int i = 0; i < trace.length; i++) {
/* 298:478 */         line = line + "\t" + trace[i].toString() + "\n";
/* 299:    */       }
/* 300:479 */       x = line;
/* 301:    */     }
/* 302:482 */     printHeader();
/* 303:483 */     for (int i = 0; i < size(); i++) {
/* 304:484 */       ((PrintStream)this.m_Streams.get(i)).println(x);
/* 305:    */     }
/* 306:485 */     flush();
/* 307:    */   }
/* 308:    */   
/* 309:    */   public void write(byte[] buf, int off, int len)
/* 310:    */   {
/* 311:503 */     printHeader();
/* 312:504 */     for (int i = 0; i < size(); i++) {
/* 313:505 */       ((PrintStream)this.m_Streams.get(i)).write(buf, off, len);
/* 314:    */     }
/* 315:506 */     flush();
/* 316:    */   }
/* 317:    */   
/* 318:    */   public void write(int b)
/* 319:    */   {
/* 320:524 */     printHeader();
/* 321:525 */     for (int i = 0; i < size(); i++) {
/* 322:526 */       ((PrintStream)this.m_Streams.get(i)).write(b);
/* 323:    */     }
/* 324:527 */     flush();
/* 325:    */   }
/* 326:    */   
/* 327:    */   public String toString()
/* 328:    */   {
/* 329:536 */     return getClass().getName() + ": " + this.m_Streams.size();
/* 330:    */   }
/* 331:    */   
/* 332:    */   public String getRevision()
/* 333:    */   {
/* 334:545 */     return RevisionUtils.extract("$Revision: 11247 $");
/* 335:    */   }
/* 336:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.Tee
 * JD-Core Version:    0.7.0.1
 */