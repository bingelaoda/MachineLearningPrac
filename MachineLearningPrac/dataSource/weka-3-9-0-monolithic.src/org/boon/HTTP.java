/*   1:    */ package org.boon;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.net.HttpURLConnection;
/*   6:    */ import java.net.URL;
/*   7:    */ import java.net.URLConnection;
/*   8:    */ import java.net.URLEncoder;
/*   9:    */ import java.nio.charset.Charset;
/*  10:    */ import java.nio.charset.StandardCharsets;
/*  11:    */ import java.util.Map;
/*  12:    */ import java.util.Map.Entry;
/*  13:    */ import java.util.Set;
/*  14:    */ import org.boon.core.Sys;
/*  15:    */ import org.boon.primitive.ByteBuf;
/*  16:    */ import org.boon.service.Response;
/*  17:    */ 
/*  18:    */ public class HTTP
/*  19:    */ {
/*  20: 49 */   public static final int DEFAULT_TIMEOUT_SECONDS = Sys.sysProp("org.boon.HTTP.timeout.seconds", 30);
/*  21:    */   public static final String APPLICATION_JSON = "application/json";
/*  22:    */   
/*  23:    */   public static String get(String url)
/*  24:    */   {
/*  25: 55 */     (String)Exceptions.tryIt(String.class, new Exceptions.TrialWithReturn()
/*  26:    */     {
/*  27:    */       public String tryIt()
/*  28:    */         throws Exception
/*  29:    */       {
/*  30: 60 */         Map<String, String> accept = Maps.map("Accept", "text/html,application/xhtml+xml,application/xml,application/json,text/plain;");
/*  31:    */         
/*  32:    */ 
/*  33:    */ 
/*  34: 64 */         URLConnection connection = HTTP.doGet(this.val$url, accept, null, null);
/*  35: 65 */         return HTTP.extractResponseString(connection);
/*  36:    */       }
/*  37:    */     });
/*  38:    */   }
/*  39:    */   
/*  40:    */   public static String post(String url, String body)
/*  41:    */   {
/*  42: 76 */     return postBodyTextWithContentType(url, "text/plain", body);
/*  43:    */   }
/*  44:    */   
/*  45:    */   public static Response getResponse(String url)
/*  46:    */   {
/*  47: 83 */     (Response)Exceptions.tryIt(Response.class, new Exceptions.TrialWithReturn()
/*  48:    */     {
/*  49:    */       public Response tryIt()
/*  50:    */         throws Exception
/*  51:    */       {
/*  52: 88 */         Map<String, String> accept = Maps.map("Accept", "text/html,application/xhtml+xml,application/xml,application/json,text/plain;");
/*  53:    */         
/*  54:    */ 
/*  55:    */ 
/*  56: 92 */         URLConnection connection = HTTP.doGet(this.val$url, accept, null, null);
/*  57: 93 */         return HTTP.extractResponseObject(connection);
/*  58:    */       }
/*  59:    */     });
/*  60:    */   }
/*  61:    */   
/*  62:    */   public static byte[] getBytes(String url, final String contentType)
/*  63:    */   {
/*  64:104 */     (byte[])Exceptions.tryIt([B.class, new Exceptions.TrialWithReturn()
/*  65:    */     {
/*  66:    */       public byte[] tryIt()
/*  67:    */         throws Exception
/*  68:    */       {
/*  69:108 */         URLConnection connection = HTTP.doGet(this.val$url, null, contentType, null, true);
/*  70:109 */         return HTTP.extractResponseBytes(connection);
/*  71:    */       }
/*  72:    */     });
/*  73:    */   }
/*  74:    */   
/*  75:    */   public static byte[] getBytesWithHeaders(String url, final String contentType, final Map<String, ?> headers)
/*  76:    */   {
/*  77:118 */     (byte[])Exceptions.tryIt([B.class, new Exceptions.TrialWithReturn()
/*  78:    */     {
/*  79:    */       public byte[] tryIt()
/*  80:    */         throws Exception
/*  81:    */       {
/*  82:122 */         URLConnection connection = HTTP.doGet(this.val$url, headers, contentType, null, true);
/*  83:123 */         return HTTP.extractResponseBytes(connection);
/*  84:    */       }
/*  85:    */     });
/*  86:    */   }
/*  87:    */   
/*  88:    */   public static String getWithHeaders(String url, final Map<String, ?> headers)
/*  89:    */   {
/*  90:133 */     (String)Exceptions.tryIt(String.class, new Exceptions.TrialWithReturn()
/*  91:    */     {
/*  92:    */       public String tryIt()
/*  93:    */         throws Exception
/*  94:    */       {
/*  95:137 */         URLConnection connection = HTTP.doGet(this.val$url, headers, null, null);
/*  96:138 */         return HTTP.extractResponseString(connection);
/*  97:    */       }
/*  98:    */     });
/*  99:    */   }
/* 100:    */   
/* 101:    */   public static String getWithContentType(String url, final Map<String, ?> headers, final String contentType)
/* 102:    */   {
/* 103:149 */     (String)Exceptions.tryIt(String.class, new Exceptions.TrialWithReturn()
/* 104:    */     {
/* 105:    */       public String tryIt()
/* 106:    */         throws Exception
/* 107:    */       {
/* 108:153 */         URLConnection connection = HTTP.doGet(this.val$url, headers, contentType, null);
/* 109:154 */         return HTTP.extractResponseString(connection);
/* 110:    */       }
/* 111:    */     });
/* 112:    */   }
/* 113:    */   
/* 114:    */   public static String getWithCharSet(String url, final Map<String, ?> headers, final String contentType, final String charSet)
/* 115:    */   {
/* 116:167 */     (String)Exceptions.tryIt(String.class, new Exceptions.TrialWithReturn()
/* 117:    */     {
/* 118:    */       public String tryIt()
/* 119:    */         throws Exception
/* 120:    */       {
/* 121:171 */         URLConnection connection = HTTP.doGet(this.val$url, headers, contentType, charSet);
/* 122:172 */         return HTTP.extractResponseString(connection);
/* 123:    */       }
/* 124:    */     });
/* 125:    */   }
/* 126:    */   
/* 127:    */   public static String postText(String url, String body)
/* 128:    */   {
/* 129:181 */     return postBodyTextWithContentType(url, "text/plain", body);
/* 130:    */   }
/* 131:    */   
/* 132:    */   public static String postBodyTextWithContentType(String url, final String contentType, final String body)
/* 133:    */   {
/* 134:190 */     (String)Exceptions.tryIt(String.class, new Exceptions.TrialWithReturn()
/* 135:    */     {
/* 136:    */       public String tryIt()
/* 137:    */         throws Exception
/* 138:    */       {
/* 139:194 */         URLConnection connection = HTTP.doPost(this.val$url, null, contentType, null, body);
/* 140:195 */         return HTTP.extractResponseString(connection);
/* 141:    */       }
/* 142:    */     });
/* 143:    */   }
/* 144:    */   
/* 145:    */   public static String postJSON(String url, String jsonString)
/* 146:    */   {
/* 147:206 */     return postBodyTextWithContentType(url, "application/json", jsonString);
/* 148:    */   }
/* 149:    */   
/* 150:    */   public static Response jsonRestCallViaPOST(String url, String jsonString)
/* 151:    */   {
/* 152:213 */     return postBodyTextWithContentTypeReturnResponse(url, "application/json", jsonString);
/* 153:    */   }
/* 154:    */   
/* 155:    */   public static String getJSON(String url, final Map<String, ?> headers)
/* 156:    */   {
/* 157:222 */     (String)Exceptions.tryIt(String.class, new Exceptions.TrialWithReturn()
/* 158:    */     {
/* 159:    */       public String tryIt()
/* 160:    */         throws Exception
/* 161:    */       {
/* 162:226 */         URLConnection connection = HTTP.doGet(this.val$url, headers, "application/json", null);
/* 163:227 */         return HTTP.extractResponseString(connection);
/* 164:    */       }
/* 165:    */     });
/* 166:    */   }
/* 167:    */   
/* 168:    */   public static Response jsonRestCallWithHeaders(String url, final Map<String, ?> headers)
/* 169:    */   {
/* 170:239 */     (Response)Exceptions.tryIt(Response.class, new Exceptions.TrialWithReturn()
/* 171:    */     {
/* 172:    */       public Response tryIt()
/* 173:    */         throws Exception
/* 174:    */       {
/* 175:243 */         URLConnection connection = HTTP.doGet(this.val$url, headers, "application/json", null);
/* 176:244 */         return HTTP.extractResponseObject(connection);
/* 177:    */       }
/* 178:    */     });
/* 179:    */   }
/* 180:    */   
/* 181:    */   public static Response jsonRestCall(String url)
/* 182:    */   {
/* 183:254 */     (Response)Exceptions.tryIt(Response.class, new Exceptions.TrialWithReturn()
/* 184:    */     {
/* 185:    */       public Response tryIt()
/* 186:    */         throws Exception
/* 187:    */       {
/* 188:258 */         URLConnection connection = HTTP.doGet(this.val$url, null, "application/json", null);
/* 189:259 */         return HTTP.extractResponseObject(connection);
/* 190:    */       }
/* 191:    */     });
/* 192:    */   }
/* 193:    */   
/* 194:    */   public static Response postBodyTextWithContentTypeReturnResponse(String url, final String contentType, final String body)
/* 195:    */   {
/* 196:272 */     (Response)Exceptions.tryIt(Response.class, new Exceptions.TrialWithReturn()
/* 197:    */     {
/* 198:    */       public Response tryIt()
/* 199:    */         throws Exception
/* 200:    */       {
/* 201:276 */         URLConnection connection = HTTP.doPost(this.val$url, null, contentType, null, body);
/* 202:277 */         return HTTP.extractResponseObject(connection);
/* 203:    */       }
/* 204:    */     });
/* 205:    */   }
/* 206:    */   
/* 207:    */   public static String getJSONWithParams(String url, final Map<String, ?> headers, final Map<String, ?> params)
/* 208:    */   {
/* 209:290 */     (String)Exceptions.tryIt(String.class, new Exceptions.TrialWithReturn()
/* 210:    */     {
/* 211:    */       public String tryIt()
/* 212:    */         throws Exception
/* 213:    */       {
/* 214:294 */         URLConnection connection = HTTP.doGet(this.val$url, headers, "application/json", null, params);
/* 215:295 */         return HTTP.extractResponseString(connection);
/* 216:    */       }
/* 217:    */     });
/* 218:    */   }
/* 219:    */   
/* 220:    */   public static String postXML(String url, String jsonString)
/* 221:    */   {
/* 222:305 */     return postBodyTextWithContentType(url, "text/xml", jsonString);
/* 223:    */   }
/* 224:    */   
/* 225:    */   public static String postWithHeaders(String url, final Map<String, ?> headers, final String body)
/* 226:    */   {
/* 227:313 */     (String)Exceptions.tryIt(String.class, new Exceptions.TrialWithReturn()
/* 228:    */     {
/* 229:    */       public String tryIt()
/* 230:    */         throws Exception
/* 231:    */       {
/* 232:317 */         URLConnection connection = HTTP.doPost(this.val$url, headers, "text/plain", null, body);
/* 233:318 */         return HTTP.extractResponseString(connection);
/* 234:    */       }
/* 235:    */     });
/* 236:    */   }
/* 237:    */   
/* 238:    */   public static String postWithContentType(String url, final Map<String, ?> headers, final String contentType, final String body)
/* 239:    */   {
/* 240:332 */     (String)Exceptions.tryIt(String.class, new Exceptions.TrialWithReturn()
/* 241:    */     {
/* 242:    */       public String tryIt()
/* 243:    */         throws Exception
/* 244:    */       {
/* 245:336 */         URLConnection connection = HTTP.doPost(this.val$url, headers, contentType, null, body);
/* 246:337 */         return HTTP.extractResponseString(connection);
/* 247:    */       }
/* 248:    */     });
/* 249:    */   }
/* 250:    */   
/* 251:    */   public static String postWithCharset(String url, final Map<String, ?> headers, final String contentType, final String charSet, final String body)
/* 252:    */   {
/* 253:352 */     (String)Exceptions.tryIt(String.class, new Exceptions.TrialWithReturn()
/* 254:    */     {
/* 255:    */       public String tryIt()
/* 256:    */         throws Exception
/* 257:    */       {
/* 258:356 */         URLConnection connection = HTTP.doPost(this.val$url, headers, contentType, charSet, body);
/* 259:357 */         return HTTP.extractResponseString(connection);
/* 260:    */       }
/* 261:    */     });
/* 262:    */   }
/* 263:    */   
/* 264:    */   private static URLConnection doPost(String url, Map<String, ?> headers, String contentType, String charset, String body)
/* 265:    */     throws IOException
/* 266:    */   {
/* 267:369 */     HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
/* 268:370 */     connection.setConnectTimeout(DEFAULT_TIMEOUT_SECONDS * 1000);
/* 269:    */     
/* 270:372 */     connection.setDoOutput(true);
/* 271:373 */     manageContentTypeHeaders(contentType, charset, connection);
/* 272:    */     
/* 273:375 */     manageHeaders(headers, connection);
/* 274:    */     
/* 275:    */ 
/* 276:378 */     IO.write(connection.getOutputStream(), body, IO.DEFAULT_CHARSET);
/* 277:379 */     return connection;
/* 278:    */   }
/* 279:    */   
/* 280:    */   public static String postForm(String url, final Map<String, ?> headers, final Map<String, Object> formData)
/* 281:    */   {
/* 282:385 */     (String)Exceptions.tryIt(String.class, new Exceptions.TrialWithReturn()
/* 283:    */     {
/* 284:    */       public String tryIt()
/* 285:    */         throws Exception
/* 286:    */       {
/* 287:389 */         URLConnection connection = HTTP.doPostFormData(this.val$url, headers, formData);
/* 288:390 */         return HTTP.extractResponseString(connection);
/* 289:    */       }
/* 290:    */     });
/* 291:    */   }
/* 292:    */   
/* 293:    */   private static URLConnection doPostFormData(String url, Map<String, ?> headers, Map<String, Object> formData)
/* 294:    */     throws IOException
/* 295:    */   {
/* 296:402 */     HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
/* 297:403 */     connection.setConnectTimeout(DEFAULT_TIMEOUT_SECONDS * 1000);
/* 298:    */     
/* 299:405 */     connection.setDoOutput(true);
/* 300:    */     
/* 301:407 */     connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
/* 302:    */     
/* 303:409 */     ByteBuf buf = ByteBuf.create(244);
/* 304:    */     
/* 305:411 */     Set<String> keys = formData.keySet();
/* 306:    */     
/* 307:413 */     int index = 0;
/* 308:414 */     for (String key : keys)
/* 309:    */     {
/* 310:416 */       Object value = formData.get(key);
/* 311:418 */       if (index > 0) {
/* 312:419 */         buf.addByte(38);
/* 313:    */       }
/* 314:423 */       buf.addUrlEncoded(key);
/* 315:424 */       buf.addByte(61);
/* 316:426 */       if (!(value instanceof byte[])) {
/* 317:427 */         buf.addUrlEncoded(value.toString());
/* 318:    */       } else {
/* 319:429 */         buf.addUrlEncodedByteArray((byte[])value);
/* 320:    */       }
/* 321:431 */       index++;
/* 322:    */     }
/* 323:434 */     manageContentTypeHeaders("application/x-www-form-urlencoded", StandardCharsets.UTF_8.name(), connection);
/* 324:    */     
/* 325:    */ 
/* 326:437 */     manageHeaders(headers, connection);
/* 327:    */     
/* 328:    */ 
/* 329:440 */     int len = buf.len();
/* 330:441 */     IO.write(connection.getOutputStream(), new String(buf.readForRecycle(), 0, len, StandardCharsets.UTF_8), IO.DEFAULT_CHARSET);
/* 331:    */     
/* 332:443 */     return connection;
/* 333:    */   }
/* 334:    */   
/* 335:    */   private static void manageHeaders(Map<String, ?> headers, URLConnection connection)
/* 336:    */   {
/* 337:447 */     if (headers != null) {
/* 338:448 */       for (Map.Entry<String, ?> entry : headers.entrySet()) {
/* 339:449 */         connection.setRequestProperty((String)entry.getKey(), entry.getValue().toString());
/* 340:    */       }
/* 341:    */     }
/* 342:    */   }
/* 343:    */   
/* 344:    */   private static void manageContentTypeHeaders(String contentType, String charset, URLConnection connection, boolean binary)
/* 345:    */   {
/* 346:457 */     if (!binary) {
/* 347:458 */       connection.setRequestProperty("Accept-Charset", charset == null ? StandardCharsets.UTF_8.displayName() : charset);
/* 348:    */     }
/* 349:460 */     if ((contentType != null) && (!contentType.isEmpty())) {
/* 350:461 */       connection.setRequestProperty("Content-Type", contentType);
/* 351:    */     }
/* 352:    */   }
/* 353:    */   
/* 354:    */   private static URLConnection doGet(String url, Map<String, ?> headers, String contentType, String charset, boolean binary)
/* 355:    */     throws IOException
/* 356:    */   {
/* 357:469 */     URLConnection connection = new URL(url).openConnection();
/* 358:470 */     connection.setConnectTimeout(DEFAULT_TIMEOUT_SECONDS * 1000);
/* 359:    */     
/* 360:472 */     manageContentTypeHeaders(contentType, charset, connection, binary);
/* 361:    */     
/* 362:474 */     manageHeaders(headers, connection);
/* 363:    */     
/* 364:476 */     return connection;
/* 365:    */   }
/* 366:    */   
/* 367:    */   private static String extractResponseString(URLConnection connection)
/* 368:    */     throws IOException
/* 369:    */   {
/* 370:483 */     HttpURLConnection http = (HttpURLConnection)connection;
/* 371:484 */     int status = http.getResponseCode();
/* 372:485 */     String charset = getCharset(connection.getHeaderField("Content-Type"));
/* 373:490 */     if (status == 200) {
/* 374:491 */       return readResponseBody(http, charset);
/* 375:    */     }
/* 376:493 */     return readErrorResponseBody(http, status, charset);
/* 377:    */   }
/* 378:    */   
/* 379:    */   private static Response extractResponseObject(URLConnection connection)
/* 380:    */     throws IOException
/* 381:    */   {
/* 382:501 */     HttpURLConnection http = (HttpURLConnection)connection;
/* 383:502 */     int status = http.getResponseCode();
/* 384:    */     
/* 385:504 */     String charset = getCharset(connection.getHeaderField("Content-Type"));
/* 386:    */     String body;
/* 387:    */     String body;
/* 388:510 */     if (status == 200) {
/* 389:511 */       body = readResponseBody(http, charset);
/* 390:    */     } else {
/* 391:513 */       body = readErrorResponseBodyDoNotDie(http, status, charset);
/* 392:    */     }
/* 393:516 */     return Response.response(status, http.getHeaderFields(), http.getResponseMessage(), body);
/* 394:    */   }
/* 395:    */   
/* 396:    */   private static byte[] extractResponseBytes(URLConnection connection)
/* 397:    */     throws IOException
/* 398:    */   {
/* 399:522 */     HttpURLConnection http = (HttpURLConnection)connection;
/* 400:523 */     int status = http.getResponseCode();
/* 401:528 */     if (status == 200) {
/* 402:529 */       return readResponseBodyAsBytes(http);
/* 403:    */     }
/* 404:531 */     String charset = getCharset(connection.getHeaderField("Content-Type"));
/* 405:    */     
/* 406:533 */     readErrorResponseBody(http, status, charset);
/* 407:534 */     return null;
/* 408:    */   }
/* 409:    */   
/* 410:    */   private static byte[] readResponseBodyAsBytes(HttpURLConnection http)
/* 411:    */   {
/* 412:    */     try
/* 413:    */     {
/* 414:540 */       return IO.input(http.getInputStream());
/* 415:    */     }
/* 416:    */     catch (IOException e)
/* 417:    */     {
/* 418:542 */       return (byte[])Exceptions.handle([B.class, e);
/* 419:    */     }
/* 420:    */   }
/* 421:    */   
/* 422:    */   private static String readErrorResponseBody(HttpURLConnection http, int status, String charset)
/* 423:    */   {
/* 424:548 */     InputStream errorStream = http.getErrorStream();
/* 425:549 */     if (errorStream != null)
/* 426:    */     {
/* 427:550 */       String error = charset == null ? IO.read(errorStream) : IO.read(errorStream, charset);
/* 428:    */       
/* 429:552 */       return (String)Exceptions.die(String.class, "STATUS CODE =" + status + "\n\n" + error);
/* 430:    */     }
/* 431:554 */     return (String)Exceptions.die(String.class, "STATUS CODE =" + status);
/* 432:    */   }
/* 433:    */   
/* 434:    */   private static String readErrorResponseBodyDoNotDie(HttpURLConnection http, int status, String charset)
/* 435:    */   {
/* 436:560 */     InputStream errorStream = http.getErrorStream();
/* 437:561 */     if (errorStream != null)
/* 438:    */     {
/* 439:562 */       String error = charset == null ? IO.read(errorStream) : IO.read(errorStream, charset);
/* 440:    */       
/* 441:564 */       return error;
/* 442:    */     }
/* 443:566 */     return "";
/* 444:    */   }
/* 445:    */   
/* 446:    */   private static String readResponseBody(HttpURLConnection http, String charset)
/* 447:    */     throws IOException
/* 448:    */   {
/* 449:571 */     if (charset != null) {
/* 450:572 */       return IO.read(http.getInputStream(), charset);
/* 451:    */     }
/* 452:574 */     return IO.read(http.getInputStream());
/* 453:    */   }
/* 454:    */   
/* 455:    */   private static String getCharset(String contentType)
/* 456:    */   {
/* 457:579 */     if (contentType == null) {
/* 458:580 */       return null;
/* 459:    */     }
/* 460:582 */     String charset = null;
/* 461:583 */     for (String param : contentType.replace(" ", "").split(";")) {
/* 462:584 */       if (param.startsWith("charset="))
/* 463:    */       {
/* 464:585 */         charset = param.split("=", 2)[1];
/* 465:586 */         break;
/* 466:    */       }
/* 467:    */     }
/* 468:589 */     charset = charset == null ? StandardCharsets.UTF_8.displayName() : charset;
/* 469:    */     
/* 470:591 */     return charset;
/* 471:    */   }
/* 472:    */   
/* 473:    */   private static void manageContentTypeHeaders(String contentType, String charset, URLConnection connection)
/* 474:    */   {
/* 475:596 */     connection.setRequestProperty("Accept-Charset", charset == null ? StandardCharsets.UTF_8.displayName() : charset);
/* 476:597 */     if ((contentType != null) && (!contentType.isEmpty())) {
/* 477:598 */       connection.setRequestProperty("Content-Type", contentType);
/* 478:    */     }
/* 479:    */   }
/* 480:    */   
/* 481:    */   private static URLConnection doGet(String url, Map<String, ?> headers, String contentType, String charset)
/* 482:    */     throws IOException
/* 483:    */   {
/* 484:605 */     URLConnection connection = new URL(url).openConnection();
/* 485:606 */     connection.setConnectTimeout(DEFAULT_TIMEOUT_SECONDS * 1000);
/* 486:    */     
/* 487:608 */     manageContentTypeHeaders(contentType, charset, connection);
/* 488:    */     
/* 489:610 */     manageHeaders(headers, connection);
/* 490:    */     
/* 491:612 */     return connection;
/* 492:    */   }
/* 493:    */   
/* 494:    */   private static URLConnection doGet(String url, Map<String, ?> headers, String contentType, String charset, Map<String, ?> params)
/* 495:    */     throws IOException
/* 496:    */   {
/* 497:618 */     if (charset == null) {
/* 498:619 */       charset = StandardCharsets.UTF_8.name();
/* 499:    */     }
/* 500:622 */     URLConnection connection = new URL(url).openConnection();
/* 501:623 */     connection.setConnectTimeout(DEFAULT_TIMEOUT_SECONDS * 1000);
/* 502:    */     
/* 503:625 */     manageContentTypeHeaders(contentType, charset, connection);
/* 504:    */     
/* 505:627 */     manageHeaders(headers, connection);
/* 506:    */     
/* 507:629 */     Set<String> keys = params.keySet();
/* 508:631 */     for (String key : keys)
/* 509:    */     {
/* 510:633 */       Object value = params.get(key);
/* 511:634 */       connection.addRequestProperty(URLEncoder.encode(key, charset), URLEncoder.encode(value.toString(), charset));
/* 512:    */     }
/* 513:636 */     return connection;
/* 514:    */   }
/* 515:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.HTTP
 * JD-Core Version:    0.7.0.1
 */