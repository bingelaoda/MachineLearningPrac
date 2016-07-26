/*   1:    */ package org.boon.template;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collection;
/*   5:    */ import java.util.Iterator;
/*   6:    */ import java.util.List;
/*   7:    */ import java.util.Map;
/*   8:    */ import java.util.Map.Entry;
/*   9:    */ import org.boon.Boon;
/*  10:    */ import org.boon.IO;
/*  11:    */ import org.boon.Str;
/*  12:    */ import org.boon.StringScanner;
/*  13:    */ import org.boon.collections.LazyMap;
/*  14:    */ import org.boon.core.Conversions;
/*  15:    */ import org.boon.core.reflection.BeanUtils;
/*  16:    */ import org.boon.expression.BoonExpressionContext;
/*  17:    */ import org.boon.expression.ExpressionContext;
/*  18:    */ import org.boon.primitive.CharBuf;
/*  19:    */ import org.boon.template.support.LoopTagStatus;
/*  20:    */ import org.boon.template.support.Token;
/*  21:    */ import org.boon.template.support.TokenTypes;
/*  22:    */ 
/*  23:    */ public class BoonTemplate
/*  24:    */   implements Template
/*  25:    */ {
/*  26: 27 */   private CharBuf _buf = CharBuf.create(16);
/*  27:    */   private BoonTemplate parentTemplate;
/*  28:    */   private TemplateParser parser;
/*  29:    */   private String template;
/*  30:    */   private final ExpressionContext _context;
/*  31: 32 */   private BoonCommandArgumentParser commandArgumentParser = new BoonCommandArgumentParser();
/*  32:    */   
/*  33:    */   private void output(Object object)
/*  34:    */   {
/*  35: 35 */     this._buf.add(object);
/*  36:    */   }
/*  37:    */   
/*  38:    */   private void output(Token token)
/*  39:    */   {
/*  40: 39 */     this._buf.add(this.template, token.start(), token.stop());
/*  41:    */   }
/*  42:    */   
/*  43:    */   public BoonTemplate()
/*  44:    */   {
/*  45: 45 */     this.parser = new BoonCoreTemplateParser();
/*  46:    */     
/*  47: 47 */     this._context = new BoonExpressionContext(new Object[0]);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public BoonTemplate(TemplateParser templateParser)
/*  51:    */   {
/*  52: 54 */     this.parser = templateParser;
/*  53: 55 */     this._context = new BoonExpressionContext(new Object[0]);
/*  54:    */   }
/*  55:    */   
/*  56:    */   public BoonTemplate(ExpressionContext context, TemplateParser templateParser)
/*  57:    */   {
/*  58: 61 */     this.parser = templateParser;
/*  59: 62 */     this._context = context;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public String replace(String template, Object... context)
/*  63:    */   {
/*  64: 70 */     initContext(context);
/*  65: 71 */     this.template = template;
/*  66: 72 */     this.parser.parse(template);
/*  67: 73 */     this._buf.readForRecycle();
/*  68:    */     
/*  69: 75 */     Iterator<Token> tokens = this.parser.getTokenList().iterator();
/*  70: 77 */     while (tokens.hasNext())
/*  71:    */     {
/*  72: 78 */       Token token = (Token)tokens.next();
/*  73: 79 */       processToken(tokens, token);
/*  74:    */     }
/*  75: 82 */     return this._buf.toString();
/*  76:    */   }
/*  77:    */   
/*  78:    */   private String include(String template)
/*  79:    */   {
/*  80: 88 */     this.template = template;
/*  81: 89 */     this.parser.parse(template);
/*  82: 90 */     this._buf.readForRecycle();
/*  83:    */     
/*  84: 92 */     Iterator<Token> tokens = this.parser.getTokenList().iterator();
/*  85: 94 */     while (tokens.hasNext())
/*  86:    */     {
/*  87: 95 */       Token token = (Token)tokens.next();
/*  88: 96 */       processToken(tokens, token);
/*  89:    */     }
/*  90: 99 */     return this._buf.toString();
/*  91:    */   }
/*  92:    */   
/*  93:    */   public String replaceFromResource(String resource, Object... context)
/*  94:    */   {
/*  95:104 */     String template = Boon.resource(resource);
/*  96:105 */     return replace(template, context);
/*  97:    */   }
/*  98:    */   
/*  99:    */   public String replaceFromFile(String resource, Object... context)
/* 100:    */   {
/* 101:110 */     String template = IO.read(resource);
/* 102:111 */     return replace(template, context);
/* 103:    */   }
/* 104:    */   
/* 105:    */   public String replaceFromURI(String resource, Object... context)
/* 106:    */   {
/* 107:118 */     String template = IO.readResource(resource);
/* 108:119 */     return replace(template, context);
/* 109:    */   }
/* 110:    */   
/* 111:    */   public String inlcudeFromURI(String resource)
/* 112:    */   {
/* 113:125 */     String template = IO.readResource(resource);
/* 114:126 */     return include(template);
/* 115:    */   }
/* 116:    */   
/* 117:    */   private String textFromToken(Token token)
/* 118:    */   {
/* 119:131 */     return this.template.substring(token.start(), token.stop());
/* 120:    */   }
/* 121:    */   
/* 122:    */   protected void initContext(Object... root)
/* 123:    */   {
/* 124:137 */     this._context.initContext(root);
/* 125:    */   }
/* 126:    */   
/* 127:    */   private Token handleCommand(String template, Token commandToken, Iterator<Token> tokens)
/* 128:    */   {
/* 129:144 */     this.template = template;
/* 130:    */     
/* 131:146 */     String commandText = textFromToken(commandToken);
/* 132:    */     
/* 133:148 */     int index = StringScanner.findWhiteSpace(commandText);
/* 134:    */     
/* 135:150 */     String command = Str.endSliceOf(commandText, index);
/* 136:    */     
/* 137:152 */     String args = Str.sliceOf(commandText, index);
/* 138:    */     
/* 139:    */ 
/* 140:155 */     Map<String, Object> params = this.commandArgumentParser.parseArguments(args);
/* 141:    */     
/* 142:    */ 
/* 143:158 */     Token bodyToken = (Token)tokens.next();
/* 144:    */     int bodyTokenStop;
/* 145:    */     int bodyTokenStop;
/* 146:162 */     if (bodyToken.type() == TokenTypes.COMMAND_BODY) {
/* 147:163 */       bodyTokenStop = bodyToken.stop();
/* 148:    */     } else {
/* 149:165 */       bodyTokenStop = -1;
/* 150:    */     }
/* 151:170 */     List<Token> commandTokens = new ArrayList();
/* 152:173 */     if (bodyToken.start() != bodyToken.stop()) {
/* 153:174 */       while (tokens.hasNext())
/* 154:    */       {
/* 155:175 */         Token next = (Token)tokens.next();
/* 156:176 */         commandTokens.add(next);
/* 157:177 */         if (next.stop() == bodyTokenStop) {
/* 158:    */           break;
/* 159:    */         }
/* 160:    */       }
/* 161:    */     }
/* 162:184 */     dispatchCommand(command, params, commandTokens);
/* 163:    */     
/* 164:186 */     return null;
/* 165:    */   }
/* 166:    */   
/* 167:    */   private void dispatchCommand(String command, Map<String, Object> params, List<Token> commandTokens)
/* 168:    */   {
/* 169:196 */     switch (command)
/* 170:    */     {
/* 171:    */     case "if": 
/* 172:198 */       handleIf(params, commandTokens, true);
/* 173:199 */       break;
/* 174:    */     case "unless": 
/* 175:202 */       handleIf(params, commandTokens, false);
/* 176:203 */       break;
/* 177:    */     case "set": 
/* 178:    */     case "let": 
/* 179:    */     case "var": 
/* 180:    */     case "define": 
/* 181:    */     case "def": 
/* 182:    */     case "assign": 
/* 183:211 */       handleSet(params, commandTokens);
/* 184:212 */       break;
/* 185:    */     case "insert": 
/* 186:    */     case "include": 
/* 187:    */     case "import": 
/* 188:    */     case "template": 
/* 189:219 */       handleInclude(params, commandTokens);
/* 190:    */     case "list": 
/* 191:    */     case "for": 
/* 192:    */     case "forEach": 
/* 193:    */     case "foreach": 
/* 194:    */     case "loop": 
/* 195:    */     case "each": 
/* 196:227 */       handleLoop(params, commandTokens);
/* 197:    */     }
/* 198:    */   }
/* 199:    */   
/* 200:    */   private void processCommandBodyTokens(List<Token> commandTokens)
/* 201:    */   {
/* 202:235 */     Iterator<Token> commandTokenIterators = commandTokens.iterator();
/* 203:237 */     while (commandTokenIterators.hasNext())
/* 204:    */     {
/* 205:239 */       Token token = (Token)commandTokenIterators.next();
/* 206:240 */       processToken(commandTokenIterators, token);
/* 207:    */     }
/* 208:    */   }
/* 209:    */   
/* 210:    */   private void processToken(Iterator<Token> tokens, Token token)
/* 211:    */   {
/* 212:246 */     switch (1.$SwitchMap$org$boon$template$support$TokenTypes[token.type().ordinal()])
/* 213:    */     {
/* 214:    */     case 1: 
/* 215:248 */       output(token);
/* 216:249 */       break;
/* 217:    */     case 2: 
/* 218:251 */       String path = textFromToken(token);
/* 219:252 */       output(this._context.lookup(path));
/* 220:253 */       break;
/* 221:    */     case 3: 
/* 222:255 */       handleCommand(this.template, token, tokens);
/* 223:    */     }
/* 224:    */   }
/* 225:    */   
/* 226:    */   private String getStringParam(String param, Map<String, Object> params, String defaultValue)
/* 227:    */   {
/* 228:266 */     String value = Str.toString(params.get(param));
/* 229:267 */     if (Str.isEmpty(value)) {
/* 230:268 */       return defaultValue;
/* 231:    */     }
/* 232:269 */     if (value.startsWith("$")) {
/* 233:270 */       return Conversions.toString(this._context.lookupWithDefault(value, defaultValue));
/* 234:    */     }
/* 235:273 */     return value;
/* 236:    */   }
/* 237:    */   
/* 238:    */   private int getIntParam(String param, Map<String, Object> params, int defaultValue)
/* 239:    */   {
/* 240:278 */     Object value = params.get(param);
/* 241:279 */     if (value == null) {
/* 242:280 */       return defaultValue;
/* 243:    */     }
/* 244:281 */     if (((value instanceof CharSequence)) && (value.toString().startsWith("$"))) {
/* 245:282 */       return Conversions.toInt(this._context.lookupWithDefault(value.toString(), Integer.valueOf(defaultValue)));
/* 246:    */     }
/* 247:285 */     return Conversions.toInt(value);
/* 248:    */   }
/* 249:    */   
/* 250:    */   public void displayTokens()
/* 251:    */   {
/* 252:290 */     for (Token token : this.parser.getTokenList()) {
/* 253:291 */       Boon.puts(new Object[] { "token", token, textFromToken(token) });
/* 254:    */     }
/* 255:    */   }
/* 256:    */   
/* 257:    */   private void handleLoop(Map<String, Object> params, List<Token> commandTokens)
/* 258:    */   {
/* 259:299 */     String itemsName = Str.toString(params.get("items"));
/* 260:300 */     Object object = null;
/* 261:304 */     if (!Boon.isEmpty(itemsName)) {
/* 262:305 */       object = this._context.lookup(itemsName);
/* 263:    */     }
/* 264:308 */     if (Boon.isEmpty(object)) {
/* 265:309 */       object = params.get("varargs");
/* 266:    */     }
/* 267:313 */     if (Boon.isStringArray(object))
/* 268:    */     {
/* 269:314 */       String[] array = (String[])object;
/* 270:    */       
/* 271:316 */       List holder = new ArrayList();
/* 272:318 */       for (int index = 0; index < array.length; index++) {
/* 273:319 */         if (!Str.isEmpty(array[index]))
/* 274:    */         {
/* 275:322 */           object = this._context.lookup(array[index]);
/* 276:    */           
/* 277:    */ 
/* 278:325 */           holder.addAll(Conversions.toCollection(object));
/* 279:    */         }
/* 280:    */       }
/* 281:329 */       object = holder;
/* 282:    */     }
/* 283:334 */     Collection<Object> items = Conversions.toCollection(object);
/* 284:    */     
/* 285:    */ 
/* 286:337 */     String var = getStringParam("var", params, "item");
/* 287:338 */     String varStatus = getStringParam("varStatus", params, "status");
/* 288:    */     
/* 289:    */ 
/* 290:341 */     int begin = getIntParam("begin", params, -1);
/* 291:342 */     int end = getIntParam("end", params, -1);
/* 292:343 */     int step = getIntParam("step", params, -1);
/* 293:    */     
/* 294:345 */     LoopTagStatus status = new LoopTagStatus();
/* 295:    */     
/* 296:347 */     status.setCount(items.size());
/* 297:349 */     if (end != -1) {
/* 298:350 */       status.setEnd(Integer.valueOf(end));
/* 299:    */     } else {
/* 300:352 */       end = items.size();
/* 301:    */     }
/* 302:356 */     if (begin != -1) {
/* 303:357 */       status.setBegin(Integer.valueOf(begin));
/* 304:    */     } else {
/* 305:359 */       begin = 0;
/* 306:    */     }
/* 307:363 */     if (step != -1) {
/* 308:364 */       status.setStep(Integer.valueOf(step));
/* 309:    */     } else {
/* 310:366 */       step = 1;
/* 311:    */     }
/* 312:370 */     Map<String, Object> values = new LazyMap();
/* 313:    */     
/* 314:372 */     int index = 0;
/* 315:    */     
/* 316:374 */     this._context.pushContext(values);
/* 317:376 */     for (Object item : items)
/* 318:    */     {
/* 319:378 */       if ((index >= begin) && (index < end) && (index % step == 0))
/* 320:    */       {
/* 321:380 */         values.put(var, item);
/* 322:381 */         values.put(varStatus, status);
/* 323:    */         
/* 324:    */ 
/* 325:384 */         status.setIndex(index);
/* 326:    */         
/* 327:386 */         values.put("@first", Boolean.valueOf(status.isFirst()));
/* 328:    */         
/* 329:388 */         values.put("@last", Boolean.valueOf(status.isLast()));
/* 330:    */         
/* 331:    */ 
/* 332:391 */         values.put("index", Integer.valueOf(index));
/* 333:392 */         values.put("@index", Integer.valueOf(index));
/* 334:394 */         if ((item instanceof Map.Entry))
/* 335:    */         {
/* 336:395 */           Map.Entry entry = (Map.Entry)item;
/* 337:396 */           values.put("@key", entry.getKey());
/* 338:397 */           values.put("@value", entry.getValue());
/* 339:398 */           values.put("this", entry.getValue());
/* 340:    */         }
/* 341:    */         else
/* 342:    */         {
/* 343:402 */           values.put("this", item);
/* 344:    */         }
/* 345:405 */         processCommandBodyTokens(commandTokens);
/* 346:    */       }
/* 347:407 */       index++;
/* 348:    */     }
/* 349:410 */     this._context.removeLastContext();
/* 350:    */   }
/* 351:    */   
/* 352:    */   private void handleSet(Map<String, Object> params, List<Token> commandTokens)
/* 353:    */   {
/* 354:419 */     String var = getStringParam("var", params, "");
/* 355:420 */     String propertyPath = getStringParam("property", params, "");
/* 356:421 */     String valueExpression = Str.toString(params.get("value"));
/* 357:422 */     String targetExpression = Str.toString(params.get("target"));
/* 358:    */     
/* 359:    */ 
/* 360:425 */     Object value = this._context.lookup(valueExpression);
/* 361:    */     
/* 362:427 */     Object bean = this._context.lookup(targetExpression);
/* 363:429 */     if ((!Str.isEmpty(propertyPath)) && (bean != null)) {
/* 364:430 */       BeanUtils.idx(bean, propertyPath, value);
/* 365:    */     }
/* 366:433 */     if (!Str.isEmpty(var)) {
/* 367:434 */       this._context.put(var, value);
/* 368:    */     }
/* 369:    */   }
/* 370:    */   
/* 371:    */   private void handleInclude(Map<String, Object> params, List<Token> commandTokens)
/* 372:    */   {
/* 373:443 */     Map<String, Object> newContext = new LazyMap(params.size());
/* 374:445 */     for (Map.Entry<String, Object> entry : params.entrySet()) {
/* 375:446 */       newContext.put(entry.getKey(), this._context.lookupWithDefault(entry.getValue().toString(), entry.getValue()));
/* 376:    */     }
/* 377:451 */     Object otype = params.get("type");
/* 378:    */     String type;
/* 379:    */     String type;
/* 380:454 */     if (!Boon.isEmpty(otype)) {
/* 381:455 */       type = otype.toString();
/* 382:    */     } else {
/* 383:457 */       type = "";
/* 384:    */     }
/* 385:    */     TemplateParser includeParser;
/* 386:462 */     switch (type)
/* 387:    */     {
/* 388:    */     case "": 
/* 389:    */       try
/* 390:    */       {
/* 391:465 */         includeParser = (TemplateParser)this.parser.getClass().newInstance();
/* 392:    */       }
/* 393:    */       catch (Exception e)
/* 394:    */       {
/* 395:467 */         includeParser = new BoonCoreTemplateParser();
/* 396:    */       }
/* 397:    */     case "boon": 
/* 398:    */     case "jsp": 
/* 399:    */     case "jstl": 
/* 400:473 */       includeParser = new BoonCoreTemplateParser();
/* 401:474 */       break;
/* 402:    */     case "modern": 
/* 403:    */     case "mustache": 
/* 404:    */     case "handlebar": 
/* 405:    */     case "handlebars": 
/* 406:480 */       includeParser = new BoonModernTemplateParser();
/* 407:481 */       break;
/* 408:    */     default: 
/* 409:484 */       includeParser = new BoonCoreTemplateParser();
/* 410:    */     }
/* 411:488 */     BoonTemplate template = new BoonTemplate(this._context, includeParser);
/* 412:489 */     template.parentTemplate = this;
/* 413:490 */     this._context.pushContext(newContext);
/* 414:    */     
/* 415:    */ 
/* 416:493 */     String resource = (String)params.get("resource");
/* 417:495 */     if (resource.contains(":")) {
/* 418:496 */       output(template.inlcudeFromURI(resource));
/* 419:    */     } else {
/* 420:498 */       output(template.includeFromResource(resource));
/* 421:    */     }
/* 422:501 */     this._context.removeLastContext();
/* 423:    */   }
/* 424:    */   
/* 425:    */   private String includeFromResource(String resource)
/* 426:    */   {
/* 427:507 */     String template = Boon.resource(resource);
/* 428:508 */     return include(template);
/* 429:    */   }
/* 430:    */   
/* 431:    */   private void handleIf(Map<String, Object> params, List<Token> commandTokens, boolean normal)
/* 432:    */   {
/* 433:514 */     boolean display = false;
/* 434:    */     
/* 435:516 */     String var = getStringParam("var", params, "__none");
/* 436:518 */     if (params.containsKey("test"))
/* 437:    */     {
/* 438:519 */       String test = Str.toString(params.get("test"));
/* 439:520 */       if (!Str.isEmpty(test)) {
/* 440:522 */         if ("true".equals(test))
/* 441:    */         {
/* 442:523 */           display = true;
/* 443:    */         }
/* 444:    */         else
/* 445:    */         {
/* 446:525 */           Object o = this._context.lookupWithDefault(test, "");
/* 447:    */           
/* 448:527 */           display = Conversions.toBoolean(o);
/* 449:    */         }
/* 450:    */       }
/* 451:    */     }
/* 452:    */     else
/* 453:    */     {
/* 454:531 */       Object varargsObject = params.get("varargs");
/* 455:533 */       if (Boon.isStringArray(varargsObject))
/* 456:    */       {
/* 457:535 */         String[] array = (String[])varargsObject;
/* 458:537 */         if (array.length > 0) {
/* 459:538 */           display = true;
/* 460:    */         }
/* 461:540 */         for (int index = 0; index < array.length; index++) {
/* 462:542 */           if (!Str.isEmpty(array[index]))
/* 463:    */           {
/* 464:543 */             Object o = this._context.lookup(array[index]);
/* 465:544 */             if (!Conversions.toBoolean(o))
/* 466:    */             {
/* 467:545 */               display = false;
/* 468:546 */               break;
/* 469:    */             }
/* 470:    */           }
/* 471:    */         }
/* 472:    */       }
/* 473:551 */       else if ((varargsObject instanceof Collection))
/* 474:    */       {
/* 475:552 */         Collection varargs = (Collection)varargsObject;
/* 476:555 */         if (varargs.size() > 0) {
/* 477:556 */           display = true;
/* 478:    */         }
/* 479:560 */         for (Object arg : varargs)
/* 480:    */         {
/* 481:562 */           Object value = this._context.lookup(arg.toString());
/* 482:563 */           if (!Conversions.toBoolean(value))
/* 483:    */           {
/* 484:564 */             display = false;
/* 485:565 */             break;
/* 486:    */           }
/* 487:    */         }
/* 488:    */       }
/* 489:    */     }
/* 490:571 */     display = (display) && (normal);
/* 491:573 */     if (!var.equals("__none")) {
/* 492:575 */       this._context.put(var, Boolean.valueOf(display));
/* 493:    */     }
/* 494:580 */     if (display) {
/* 495:582 */       processCommandBodyTokens(commandTokens);
/* 496:    */     }
/* 497:    */   }
/* 498:    */   
/* 499:    */   public static Template template()
/* 500:    */   {
/* 501:592 */     return new BoonTemplate(new BoonModernTemplateParser());
/* 502:    */   }
/* 503:    */   
/* 504:    */   public static Template jstl()
/* 505:    */   {
/* 506:598 */     return new BoonTemplate(new BoonCoreTemplateParser());
/* 507:    */   }
/* 508:    */   
/* 509:    */   public void addFunctions(Class<?> functions)
/* 510:    */   {
/* 511:604 */     this._context.addFunctions(functions);
/* 512:    */   }
/* 513:    */   
/* 514:    */   public void addFunctions(String prefix, Class<?> functions)
/* 515:    */   {
/* 516:609 */     this._context.addFunctions(prefix, functions);
/* 517:    */   }
/* 518:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.template.BoonTemplate
 * JD-Core Version:    0.7.0.1
 */