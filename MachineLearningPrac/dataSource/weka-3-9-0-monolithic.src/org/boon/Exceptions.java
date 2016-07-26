/*   1:    */ package org.boon;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.PrintWriter;
/*   5:    */ import java.io.StringWriter;
/*   6:    */ import java.util.ArrayList;
/*   7:    */ import java.util.HashSet;
/*   8:    */ import java.util.List;
/*   9:    */ import java.util.Map;
/*  10:    */ import java.util.Set;
/*  11:    */ import org.boon.primitive.Arry;
/*  12:    */ import org.boon.primitive.CharBuf;
/*  13:    */ 
/*  14:    */ public class Exceptions
/*  15:    */ {
/*  16: 49 */   private static final Set<String> ignorePackages = Sets.set(new String[] { "sun.", "com.sun.", "javax.java", "java.", "oracle.", "com.oracle.", "org.junit", "org.boon.Exceptions", "com.intellij" });
/*  17:    */   
/*  18:    */   public static void requireNonNull(Object obj)
/*  19:    */   {
/*  20: 55 */     if (obj == null) {
/*  21: 56 */       die("Required object assertion exception");
/*  22:    */     }
/*  23:    */   }
/*  24:    */   
/*  25:    */   public static void requireNonNulls(String message, Object... array)
/*  26:    */   {
/*  27: 61 */     int index = 0;
/*  28: 62 */     for (Object obj : array)
/*  29:    */     {
/*  30: 63 */       if (obj == null) {
/*  31: 64 */         die(new Object[] { message, Integer.valueOf(index) });
/*  32:    */       }
/*  33: 66 */       index++;
/*  34:    */     }
/*  35:    */   }
/*  36:    */   
/*  37:    */   public static void dieIfAnyParametersAreNull(String methodName, Object... parameters)
/*  38:    */   {
/*  39: 73 */     requireNonNull(Boon.sputs(new Object[] { "METHOD", methodName, "Parameter at index was null: index=" }));
/*  40:    */   }
/*  41:    */   
/*  42:    */   public static void requireNonNull(Object obj, String message)
/*  43:    */   {
/*  44: 77 */     if (obj == null) {
/*  45: 78 */       die(message);
/*  46:    */     }
/*  47:    */   }
/*  48:    */   
/*  49:    */   public static boolean die()
/*  50:    */   {
/*  51: 83 */     throw new SoftenedException("died");
/*  52:    */   }
/*  53:    */   
/*  54:    */   public static boolean die(String message)
/*  55:    */   {
/*  56: 87 */     throw new SoftenedException(message);
/*  57:    */   }
/*  58:    */   
/*  59:    */   public static boolean die(Object... messages)
/*  60:    */   {
/*  61: 92 */     throw new SoftenedException(Boon.sputs(messages));
/*  62:    */   }
/*  63:    */   
/*  64:    */   public static <T> T die(Class<T> clazz, String message)
/*  65:    */   {
/*  66: 97 */     throw new SoftenedException(message);
/*  67:    */   }
/*  68:    */   
/*  69:    */   public static <T> T die(Class<T> clazz, Object... messages)
/*  70:    */   {
/*  71:103 */     throw new SoftenedException(Boon.sputs(messages));
/*  72:    */   }
/*  73:    */   
/*  74:    */   public static void handle(Exception e)
/*  75:    */   {
/*  76:107 */     throw new SoftenedException(e);
/*  77:    */   }
/*  78:    */   
/*  79:    */   public static <T> T handle(Class<T> clazz, Exception e)
/*  80:    */   {
/*  81:113 */     if ((e instanceof SoftenedException)) {
/*  82:114 */       throw ((SoftenedException)e);
/*  83:    */     }
/*  84:116 */     throw new SoftenedException(e);
/*  85:    */   }
/*  86:    */   
/*  87:    */   public static <T> T handle(Class<T> clazz, String message, Throwable e)
/*  88:    */   {
/*  89:121 */     throw new SoftenedException(message, e);
/*  90:    */   }
/*  91:    */   
/*  92:    */   public static <T> T handle(Class<T> clazz, Throwable e, Object... messages)
/*  93:    */   {
/*  94:128 */     throw new SoftenedException(Boon.sputs(messages), e);
/*  95:    */   }
/*  96:    */   
/*  97:    */   public static void handle(Throwable e, Object... messages)
/*  98:    */   {
/*  99:133 */     throw new SoftenedException(Boon.sputs(messages), e);
/* 100:    */   }
/* 101:    */   
/* 102:    */   public static void printStackTrace(CharBuf charBuf, StackTraceElement[] stackTrace)
/* 103:    */   {
/* 104:138 */     for (StackTraceElement st : stackTrace) {
/* 105:139 */       if (!st.getClassName().contains("org.boon.Exceptions")) {
/* 106:142 */         charBuf.indent(10).println(st);
/* 107:    */       }
/* 108:    */     }
/* 109:    */   }
/* 110:    */   
/* 111:    */   public static <T> T tryIt(Class<T> clazz, TrialWithReturn<T> tryIt)
/* 112:    */   {
/* 113:    */     try
/* 114:    */     {
/* 115:148 */       return tryIt.tryIt();
/* 116:    */     }
/* 117:    */     catch (Exception ex)
/* 118:    */     {
/* 119:150 */       throw new SoftenedException(ex);
/* 120:    */     }
/* 121:    */   }
/* 122:    */   
/* 123:    */   public static void tryIt(Trial tryIt)
/* 124:    */   {
/* 125:    */     try
/* 126:    */     {
/* 127:157 */       tryIt.tryIt();
/* 128:    */     }
/* 129:    */     catch (Exception ex)
/* 130:    */     {
/* 131:159 */       throw new SoftenedException(ex);
/* 132:    */     }
/* 133:    */   }
/* 134:    */   
/* 135:    */   public static void handle(String message, Throwable e)
/* 136:    */   {
/* 137:164 */     throw new SoftenedException(message, e);
/* 138:    */   }
/* 139:    */   
/* 140:    */   public static void tryIt(String message, Trial tryIt)
/* 141:    */   {
/* 142:    */     try
/* 143:    */     {
/* 144:169 */       tryIt.tryIt();
/* 145:    */     }
/* 146:    */     catch (Exception ex)
/* 147:    */     {
/* 148:171 */       throw new SoftenedException(message, ex);
/* 149:    */     }
/* 150:    */   }
/* 151:    */   
/* 152:    */   public static StackTraceElement[] getFilteredStackTrace(StackTraceElement[] stackTrace)
/* 153:    */   {
/* 154:190 */     if ((stackTrace == null) || (stackTrace.length == 0)) {
/* 155:191 */       return new StackTraceElement[0];
/* 156:    */     }
/* 157:193 */     List<StackTraceElement> list = new ArrayList();
/* 158:194 */     Set<String> seenThisBefore = new HashSet();
/* 159:196 */     for (StackTraceElement st : stackTrace) {
/* 160:197 */       if (!Str.startsWithItemInCollection(st.getClassName(), ignorePackages))
/* 161:    */       {
/* 162:202 */         String key = Boon.sputs(new Object[] { st.getClassName(), st.getFileName(), st.getMethodName(), Integer.valueOf(st.getLineNumber()) });
/* 163:203 */         if (!seenThisBefore.contains(key))
/* 164:    */         {
/* 165:206 */           seenThisBefore.add(key);
/* 166:    */           
/* 167:    */ 
/* 168:209 */           list.add(st);
/* 169:    */         }
/* 170:    */       }
/* 171:    */     }
/* 172:212 */     return (StackTraceElement[])Arry.array(StackTraceElement.class, list);
/* 173:    */   }
/* 174:    */   
/* 175:    */   public static class SoftenedException
/* 176:    */     extends RuntimeException
/* 177:    */   {
/* 178:    */     public SoftenedException(String message)
/* 179:    */     {
/* 180:220 */       super();
/* 181:    */     }
/* 182:    */     
/* 183:    */     public SoftenedException(String message, Throwable cause)
/* 184:    */     {
/* 185:224 */       super(cause);
/* 186:    */     }
/* 187:    */     
/* 188:    */     public SoftenedException(Throwable cause)
/* 189:    */     {
/* 190:228 */       super(cause);
/* 191:    */     }
/* 192:    */     
/* 193:    */     public String getMessage()
/* 194:    */     {
/* 195:235 */       return super.getMessage() + (getCause() == null ? "" : getCauseMessage());
/* 196:    */     }
/* 197:    */     
/* 198:    */     private String getCauseMessage()
/* 199:    */     {
/* 200:240 */       return "\n CAUSE " + getCause().getClass().getName() + " :: " + getCause().getMessage();
/* 201:    */     }
/* 202:    */     
/* 203:    */     public String getLocalizedMessage()
/* 204:    */     {
/* 205:246 */       return getMessage();
/* 206:    */     }
/* 207:    */     
/* 208:    */     public StackTraceElement[] getStackTrace()
/* 209:    */     {
/* 210:251 */       if (getRootCause() != null) {
/* 211:252 */         return (StackTraceElement[])Arry.add(getRootCause().getStackTrace(), super.getStackTrace());
/* 212:    */       }
/* 213:254 */       return super.getStackTrace();
/* 214:    */     }
/* 215:    */     
/* 216:    */     public Throwable getCause()
/* 217:    */     {
/* 218:260 */       return super.getCause();
/* 219:    */     }
/* 220:    */     
/* 221:    */     public Throwable getRootCause()
/* 222:    */     {
/* 223:266 */       Throwable cause = super.getCause();
/* 224:    */       
/* 225:268 */       Throwable lastCause = super.getCause();
/* 226:270 */       while (cause != null)
/* 227:    */       {
/* 228:271 */         lastCause = cause;
/* 229:272 */         cause = cause.getCause();
/* 230:    */       }
/* 231:275 */       return lastCause;
/* 232:    */     }
/* 233:    */     
/* 234:    */     public void printStackTrace(CharBuf charBuf)
/* 235:    */     {
/* 236:283 */       charBuf.puts(new Object[] { "MESSAGE:", getMessage() });
/* 237:284 */       if (getRootCause() != null) {
/* 238:285 */         charBuf.puts(new Object[] { "ROOT CAUSE MESSAGE:", getRootCause().getMessage() });
/* 239:286 */       } else if (getCause() != null) {
/* 240:287 */         charBuf.puts(new Object[] { "CAUSE MESSAGE:", getCause().getMessage() });
/* 241:    */       }
/* 242:291 */       StackTraceElement[] stackTrace = getFilteredStackTrace();
/* 243:293 */       if (stackTrace.length > 0)
/* 244:    */       {
/* 245:294 */         charBuf.indent(5).addLine("This happens around this area in your code.");
/* 246:295 */         Exceptions.printStackTrace(charBuf, stackTrace);
/* 247:    */       }
/* 248:300 */       if (getRootCause() != null)
/* 249:    */       {
/* 250:301 */         charBuf.addLine().puts(new Object[] { "Caused by:", "message:", getRootCause().getMessage(), "type", getRootCause().getClass().getName() });
/* 251:302 */         stackTrace = getRootCause().getStackTrace();
/* 252:303 */         Exceptions.printStackTrace(charBuf, stackTrace);
/* 253:    */       }
/* 254:306 */       charBuf.addLine().multiply('-', 50).addLine().multiply('-', 50).addLine();
/* 255:    */       
/* 256:308 */       StringWriter writer = new StringWriter();
/* 257:    */       
/* 258:310 */       super.printStackTrace(new PrintWriter(writer));
/* 259:    */       
/* 260:312 */       charBuf.add(writer);
/* 261:    */       
/* 262:314 */       charBuf.addLine().multiply('-', 50).addLine();
/* 263:    */     }
/* 264:    */     
/* 265:    */     public StackTraceElement[] getFilteredStackTrace()
/* 266:    */     {
/* 267:323 */       StackTraceElement[] filteredStackTrace = Exceptions.getFilteredStackTrace(super.getStackTrace());
/* 268:324 */       if (filteredStackTrace.length > 0)
/* 269:    */       {
/* 270:326 */         if (super.getCause() != null)
/* 271:    */         {
/* 272:327 */           StackTraceElement[] cause = Exceptions.getFilteredStackTrace(super.getCause().getStackTrace());
/* 273:329 */           if (cause.length > 0) {
/* 274:330 */             filteredStackTrace = (StackTraceElement[])Arry.add(cause, filteredStackTrace);
/* 275:    */           }
/* 276:    */         }
/* 277:    */       }
/* 278:334 */       else if (super.getCause() != null) {
/* 279:336 */         filteredStackTrace = Exceptions.getFilteredStackTrace(super.getCause().getStackTrace());
/* 280:    */       }
/* 281:340 */       return Exceptions.getFilteredStackTrace(super.getStackTrace());
/* 282:    */     }
/* 283:    */     
/* 284:    */     public CharBuf printStackTraceIntoCharBuf()
/* 285:    */     {
/* 286:347 */       CharBuf out = CharBuf.create(100);
/* 287:348 */       printStackTrace(out);
/* 288:349 */       return out;
/* 289:    */     }
/* 290:    */     
/* 291:    */     public void printStackTrace(PrintStream s)
/* 292:    */     {
/* 293:355 */       s.print(printStackTraceIntoCharBuf().toString());
/* 294:    */     }
/* 295:    */     
/* 296:    */     public void printStackTrace(PrintWriter s)
/* 297:    */     {
/* 298:361 */       s.print(printStackTraceIntoCharBuf().toString());
/* 299:    */     }
/* 300:    */     
/* 301:    */     public void printStackTrace()
/* 302:    */     {
/* 303:366 */       System.err.print(printStackTraceIntoCharBuf().toString());
/* 304:    */     }
/* 305:    */   }
/* 306:    */   
/* 307:    */   public static String toString(Exception ex)
/* 308:    */   {
/* 309:372 */     CharBuf buffer = CharBuf.create(255);
/* 310:373 */     buffer.addLine(ex.getLocalizedMessage());
/* 311:    */     
/* 312:375 */     StackTraceElement[] stackTrace = ex.getStackTrace();
/* 313:376 */     for (StackTraceElement element : stackTrace)
/* 314:    */     {
/* 315:377 */       buffer.add(element.getClassName());
/* 316:378 */       Boon.sputs(new Object[] { "      ", buffer, "class", element.getClassName(), "method", element.getMethodName(), "line", Integer.valueOf(element.getLineNumber()) });
/* 317:    */     }
/* 318:382 */     return buffer.toString();
/* 319:    */   }
/* 320:    */   
/* 321:    */   public static String asJson(Exception ex)
/* 322:    */   {
/* 323:388 */     CharBuf buffer = CharBuf.create(255);
/* 324:    */     
/* 325:390 */     buffer.add('{');
/* 326:    */     
/* 327:392 */     buffer.addLine().indent(5).addJsonFieldName("message").asJsonString(ex.getMessage()).addLine(Character.valueOf(','));
/* 328:395 */     if (ex.getCause() != null)
/* 329:    */     {
/* 330:396 */       buffer.addLine().indent(5).addJsonFieldName("causeMessage").asJsonString(ex.getCause().getMessage()).addLine(Character.valueOf(','));
/* 331:400 */       if (ex.getCause().getCause() != null)
/* 332:    */       {
/* 333:401 */         buffer.addLine().indent(5).addJsonFieldName("cause2Message").asJsonString(ex.getCause().getCause().getMessage()).addLine(Character.valueOf(','));
/* 334:404 */         if (ex.getCause().getCause().getCause() != null)
/* 335:    */         {
/* 336:405 */           buffer.addLine().indent(5).addJsonFieldName("cause3Message").asJsonString(ex.getCause().getCause().getCause().getMessage()).addLine(Character.valueOf(','));
/* 337:408 */           if (ex.getCause().getCause().getCause().getCause() != null) {
/* 338:409 */             buffer.addLine().indent(5).addJsonFieldName("cause4Message").asJsonString(ex.getCause().getCause().getCause().getCause().getMessage()).addLine(Character.valueOf(','));
/* 339:    */           }
/* 340:    */         }
/* 341:    */       }
/* 342:    */     }
/* 343:424 */     StackTraceElement[] stackTrace = getFilteredStackTrace(ex.getStackTrace());
/* 344:426 */     if ((stackTrace != null) && (stackTrace.length > 0))
/* 345:    */     {
/* 346:428 */       buffer.addLine().indent(5).addJsonFieldName("stackTrace").addLine();
/* 347:    */       
/* 348:430 */       stackTraceToJson(buffer, stackTrace);
/* 349:    */       
/* 350:432 */       buffer.add(',');
/* 351:    */     }
/* 352:435 */     buffer.addLine().indent(5).addJsonFieldName("fullStackTrace").addLine();
/* 353:436 */     stackTrace = ex.getStackTrace();
/* 354:437 */     stackTraceToJson(buffer, stackTrace);
/* 355:    */     
/* 356:439 */     buffer.add('}');
/* 357:440 */     return buffer.toString();
/* 358:    */   }
/* 359:    */   
/* 360:    */   public static Map asMap(Exception ex)
/* 361:    */   {
/* 362:446 */     StackTraceElement[] stackTrace = getFilteredStackTrace(ex.getStackTrace());
/* 363:    */     
/* 364:448 */     List stackTraceList = Lists.list(stackTrace);
/* 365:    */     
/* 366:450 */     List fullStackTrace = Lists.list(ex.getStackTrace());
/* 367:    */     
/* 368:    */ 
/* 369:453 */     return Maps.map("message", ex.getMessage(), "causeMessage", ex.getCause() != null ? ex.getCause().getMessage() : "none", "stackTrace", stackTraceList, "fullStackTrace", fullStackTrace);
/* 370:    */   }
/* 371:    */   
/* 372:    */   public static void stackTraceToJson(CharBuf buffer, StackTraceElement[] stackTrace)
/* 373:    */   {
/* 374:467 */     if (stackTrace.length == 0)
/* 375:    */     {
/* 376:468 */       buffer.addLine("[]");
/* 377:469 */       return;
/* 378:    */     }
/* 379:473 */     buffer.multiply(' ', 16).addLine(Character.valueOf('['));
/* 380:475 */     for (int index = 0; index < stackTrace.length; index++)
/* 381:    */     {
/* 382:476 */       StackTraceElement element = stackTrace[index];
/* 383:478 */       if (!element.getClassName().contains("org.boon.Exceptions"))
/* 384:    */       {
/* 385:481 */         buffer.indent(17).add("[  ").asJsonString(element.getMethodName()).add(',');
/* 386:    */         
/* 387:    */ 
/* 388:    */ 
/* 389:485 */         buffer.indent(3).asJsonString(element.getClassName());
/* 390:488 */         if (element.getLineNumber() > 0)
/* 391:    */         {
/* 392:489 */           buffer.add(",");
/* 393:490 */           buffer.indent(3).asJsonString("" + element.getLineNumber()).addLine("   ],");
/* 394:    */         }
/* 395:    */         else
/* 396:    */         {
/* 397:493 */           buffer.addLine(" ],");
/* 398:    */         }
/* 399:    */       }
/* 400:    */     }
/* 401:497 */     buffer.removeLastChar();
/* 402:498 */     buffer.removeLastChar();
/* 403:    */     
/* 404:500 */     buffer.addLine().multiply(' ', 15).add(']');
/* 405:    */   }
/* 406:    */   
/* 407:    */   public static abstract interface TrialWithReturn<T>
/* 408:    */   {
/* 409:    */     public abstract T tryIt()
/* 410:    */       throws Exception;
/* 411:    */   }
/* 412:    */   
/* 413:    */   public static abstract interface Trial
/* 414:    */   {
/* 415:    */     public abstract void tryIt()
/* 416:    */       throws Exception;
/* 417:    */   }
/* 418:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.Exceptions
 * JD-Core Version:    0.7.0.1
 */