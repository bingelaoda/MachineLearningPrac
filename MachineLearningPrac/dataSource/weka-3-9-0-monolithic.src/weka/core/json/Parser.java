/*   1:    */ package weka.core.json;
/*   2:    */ 
/*   3:    */ import java.io.FileInputStream;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.HashMap;
/*   6:    */ import java.util.Stack;
/*   7:    */ import java_cup.runtime.DefaultSymbolFactory;
/*   8:    */ import java_cup.runtime.Symbol;
/*   9:    */ import java_cup.runtime.SymbolFactory;
/*  10:    */ import java_cup.runtime.lr_parser;
/*  11:    */ 
/*  12:    */ public class Parser
/*  13:    */   extends lr_parser
/*  14:    */ {
/*  15:    */   public final Class getSymbolContainer()
/*  16:    */   {
/*  17: 19 */     return sym.class;
/*  18:    */   }
/*  19:    */   
/*  20:    */   public Parser() {}
/*  21:    */   
/*  22:    */   public Parser(java_cup.runtime.Scanner s)
/*  23:    */   {
/*  24: 26 */     super(s);
/*  25:    */   }
/*  26:    */   
/*  27:    */   public Parser(java_cup.runtime.Scanner s, SymbolFactory sf)
/*  28:    */   {
/*  29: 29 */     super(s, sf);
/*  30:    */   }
/*  31:    */   
/*  32: 32 */   protected static final short[][] _production_table = unpackFromStrings(new String[] { "" });
/*  33:    */   
/*  34:    */   public short[][] production_table()
/*  35:    */   {
/*  36: 50 */     return _production_table;
/*  37:    */   }
/*  38:    */   
/*  39: 53 */   protected static final short[][] _action_table = unpackFromStrings(new String[] { "" });
/*  40:    */   
/*  41:    */   public short[][] action_table()
/*  42:    */   {
/*  43: 98 */     return _action_table;
/*  44:    */   }
/*  45:    */   
/*  46:101 */   protected static final short[][] _reduce_table = unpackFromStrings(new String[] { "" });
/*  47:    */   protected CUP.Parser.actions action_obj;
/*  48:    */   protected HashMap m_Symbols;
/*  49:    */   protected JSONNode m_Result;
/*  50:    */   protected Stack<JSONNode> m_Stack;
/*  51:    */   
/*  52:    */   public short[][] reduce_table()
/*  53:    */   {
/*  54:136 */     return _reduce_table;
/*  55:    */   }
/*  56:    */   
/*  57:    */   protected void init_actions()
/*  58:    */   {
/*  59:144 */     this.action_obj = new CUP.Parser.actions(this);
/*  60:    */   }
/*  61:    */   
/*  62:    */   public Symbol do_action(int act_num, lr_parser parser, Stack stack, int top)
/*  63:    */     throws Exception
/*  64:    */   {
/*  65:156 */     return this.action_obj.CUP$Parser$do_action(act_num, parser, stack, top);
/*  66:    */   }
/*  67:    */   
/*  68:    */   public int start_state()
/*  69:    */   {
/*  70:160 */     return 0;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public int start_production()
/*  74:    */   {
/*  75:162 */     return 1;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public int EOF_sym()
/*  79:    */   {
/*  80:165 */     return 0;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public int error_sym()
/*  84:    */   {
/*  85:168 */     return 1;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public void user_init()
/*  89:    */     throws Exception
/*  90:    */   {
/*  91:175 */     this.m_Symbols = new HashMap();
/*  92:176 */     this.m_Result = new JSONNode();
/*  93:177 */     this.m_Stack = new Stack();
/*  94:178 */     this.m_Stack.push(this.m_Result);
/*  95:    */   }
/*  96:    */   
/*  97:    */   public JSONNode getResult()
/*  98:    */   {
/*  99:198 */     return this.m_Result;
/* 100:    */   }
/* 101:    */   
/* 102:    */   protected Stack<JSONNode> getStack()
/* 103:    */   {
/* 104:208 */     return this.m_Stack;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public static void main(String[] args)
/* 108:    */     throws Exception
/* 109:    */   {
/* 110:219 */     if (args.length != 1)
/* 111:    */     {
/* 112:220 */       System.err.println("No JSON file specified!");
/* 113:221 */       System.exit(1);
/* 114:    */     }
/* 115:224 */     FileInputStream stream = new FileInputStream(args[0]);
/* 116:225 */     SymbolFactory sf = new DefaultSymbolFactory();
/* 117:226 */     Parser parser = new Parser(new Scanner(stream, sf), sf);
/* 118:227 */     parser.parse();
/* 119:228 */     StringBuffer buffer = new StringBuffer();
/* 120:229 */     parser.getResult().toString(buffer);
/* 121:230 */     System.out.println(buffer.toString());
/* 122:    */   }
/* 123:    */   
/* 124:    */   class CUP$Parser$actions
/* 125:    */   {
/* 126:    */     private final Parser parser;
/* 127:    */     
/* 128:    */     CUP$Parser$actions(Parser parser)
/* 129:    */     {
/* 130:241 */       this.parser = parser;
/* 131:    */     }
/* 132:    */     
/* 133:    */     public final Symbol CUP$Parser$do_action_part00000000(int CUP$Parser$act_num, lr_parser CUP$Parser$parser, Stack CUP$Parser$stack, int CUP$Parser$top)
/* 134:    */       throws Exception
/* 135:    */     {
/* 136:    */       Symbol CUP$Parser$result;
/* 137:256 */       switch (CUP$Parser$act_num)
/* 138:    */       {
/* 139:    */       case 0: 
/* 140:261 */         Object RESULT = null;
/* 141:    */         
/* 142:263 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("json", 0, RESULT);
/* 143:    */         
/* 144:265 */         return CUP$Parser$result;
/* 145:    */       case 1: 
/* 146:270 */         Object RESULT = null;
/* 147:271 */         Object start_val = ((Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top - 1)).value;
/* 148:272 */         RESULT = start_val;
/* 149:273 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("$START", 0, RESULT);
/* 150:    */         
/* 151:    */ 
/* 152:276 */         CUP$Parser$parser.done_parsing();
/* 153:277 */         return CUP$Parser$result;
/* 154:    */       case 2: 
/* 155:282 */         Object RESULT = null;
/* 156:    */         
/* 157:284 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("json", 0, RESULT);
/* 158:    */         
/* 159:286 */         return CUP$Parser$result;
/* 160:    */       case 3: 
/* 161:291 */         Object RESULT = null;
/* 162:    */         
/* 163:293 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("pairs", 1, RESULT);
/* 164:    */         
/* 165:295 */         return CUP$Parser$result;
/* 166:    */       case 4: 
/* 167:300 */         Object RESULT = null;
/* 168:    */         
/* 169:302 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("pairs", 1, RESULT);
/* 170:    */         
/* 171:304 */         return CUP$Parser$result;
/* 172:    */       case 5: 
/* 173:309 */         Object RESULT = null;
/* 174:    */         
/* 175:311 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("pair", 2, RESULT);
/* 176:    */         
/* 177:313 */         return CUP$Parser$result;
/* 178:    */       case 6: 
/* 179:318 */         Object RESULT = null;
/* 180:    */         
/* 181:320 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("pair", 2, RESULT);
/* 182:    */         
/* 183:322 */         return CUP$Parser$result;
/* 184:    */       case 7: 
/* 185:327 */         Object RESULT = null;
/* 186:    */         
/* 187:329 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("pair", 2, RESULT);
/* 188:    */         
/* 189:331 */         return CUP$Parser$result;
/* 190:    */       case 8: 
/* 191:336 */         Object RESULT = null;
/* 192:    */         
/* 193:338 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("primitive", 3, RESULT);
/* 194:    */         
/* 195:340 */         return CUP$Parser$result;
/* 196:    */       case 9: 
/* 197:345 */         Object RESULT = null;
/* 198:    */         
/* 199:347 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("primitive", 3, RESULT);
/* 200:    */         
/* 201:349 */         return CUP$Parser$result;
/* 202:    */       case 10: 
/* 203:354 */         Object RESULT = null;
/* 204:    */         
/* 205:356 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("primitive", 3, RESULT);
/* 206:    */         
/* 207:358 */         return CUP$Parser$result;
/* 208:    */       case 11: 
/* 209:363 */         Object RESULT = null;
/* 210:    */         
/* 211:365 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("primitive", 3, RESULT);
/* 212:    */         
/* 213:367 */         return CUP$Parser$result;
/* 214:    */       case 12: 
/* 215:372 */         Object RESULT = null;
/* 216:    */         
/* 217:374 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("primitive", 3, RESULT);
/* 218:    */         
/* 219:376 */         return CUP$Parser$result;
/* 220:    */       case 13: 
/* 221:381 */         Object RESULT = null;
/* 222:382 */         String name = (String)((Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top - 2)).value;
/* 223:    */         
/* 224:384 */         ((JSONNode)this.parser.getStack().peek()).addNull(name);
/* 225:    */         
/* 226:386 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("null", 4, RESULT);
/* 227:    */         
/* 228:388 */         return CUP$Parser$result;
/* 229:    */       case 14: 
/* 230:393 */         Boolean RESULT = null;
/* 231:394 */         String name = (String)((Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top - 2)).value;
/* 232:395 */         Boolean b = (Boolean)((Symbol)CUP$Parser$stack.peek()).value;
/* 233:    */         
/* 234:397 */         ((JSONNode)this.parser.getStack().peek()).addPrimitive(name, b);
/* 235:    */         
/* 236:399 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("boolean", 5, RESULT);
/* 237:    */         
/* 238:401 */         return CUP$Parser$result;
/* 239:    */       case 15: 
/* 240:406 */         Integer RESULT = null;
/* 241:407 */         String name = (String)((Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top - 2)).value;
/* 242:408 */         Integer i = (Integer)((Symbol)CUP$Parser$stack.peek()).value;
/* 243:    */         
/* 244:410 */         ((JSONNode)this.parser.getStack().peek()).addPrimitive(name, i);
/* 245:    */         
/* 246:412 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("integer", 6, RESULT);
/* 247:    */         
/* 248:414 */         return CUP$Parser$result;
/* 249:    */       case 16: 
/* 250:419 */         Double RESULT = null;
/* 251:420 */         String name = (String)((Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top - 2)).value;
/* 252:421 */         Double d = (Double)((Symbol)CUP$Parser$stack.peek()).value;
/* 253:    */         
/* 254:423 */         ((JSONNode)this.parser.getStack().peek()).addPrimitive(name, d);
/* 255:    */         
/* 256:425 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("double", 7, RESULT);
/* 257:    */         
/* 258:427 */         return CUP$Parser$result;
/* 259:    */       case 17: 
/* 260:432 */         String RESULT = null;
/* 261:433 */         String name = (String)((Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top - 2)).value;
/* 262:434 */         String s = (String)((Symbol)CUP$Parser$stack.peek()).value;
/* 263:    */         
/* 264:436 */         ((JSONNode)this.parser.getStack().peek()).addPrimitive(name, s);
/* 265:    */         
/* 266:438 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("string", 8, RESULT);
/* 267:    */         
/* 268:440 */         return CUP$Parser$result;
/* 269:    */       case 18: 
/* 270:445 */         Object RESULT = null;
/* 271:    */         
/* 272:447 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("named_object", 10, RESULT);
/* 273:    */         
/* 274:449 */         return CUP$Parser$result;
/* 275:    */       case 19: 
/* 276:454 */         Object RESULT = null;
/* 277:    */         
/* 278:456 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("named_object", 10, RESULT);
/* 279:    */         
/* 280:458 */         return CUP$Parser$result;
/* 281:    */       case 20: 
/* 282:463 */         Object RESULT = null;
/* 283:464 */         String name = (String)((Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top - 2)).value;
/* 284:    */         
/* 285:466 */         JSONNode node = ((JSONNode)this.parser.getStack().peek()).addObject(name);
/* 286:467 */         this.parser.getStack().push(node);
/* 287:    */         
/* 288:469 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("named_object_start", 11, RESULT);
/* 289:    */         
/* 290:471 */         return CUP$Parser$result;
/* 291:    */       case 21: 
/* 292:476 */         Object RESULT = null;
/* 293:    */         
/* 294:478 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("anon_object", 9, RESULT);
/* 295:    */         
/* 296:480 */         return CUP$Parser$result;
/* 297:    */       case 22: 
/* 298:485 */         Object RESULT = null;
/* 299:    */         
/* 300:487 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("anon_object", 9, RESULT);
/* 301:    */         
/* 302:489 */         return CUP$Parser$result;
/* 303:    */       case 23: 
/* 304:494 */         Object RESULT = null;
/* 305:    */         
/* 306:496 */         JSONNode node = ((JSONNode)this.parser.getStack().peek()).addObject(null);
/* 307:497 */         this.parser.getStack().push(node);
/* 308:    */         
/* 309:499 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("anon_object_start", 12, RESULT);
/* 310:    */         
/* 311:501 */         return CUP$Parser$result;
/* 312:    */       case 24: 
/* 313:506 */         Object RESULT = null;
/* 314:    */         
/* 315:508 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("object_content", 13, RESULT);
/* 316:    */         
/* 317:510 */         return CUP$Parser$result;
/* 318:    */       case 25: 
/* 319:515 */         Object RESULT = null;
/* 320:    */         
/* 321:517 */         this.parser.getStack().pop();
/* 322:    */         
/* 323:519 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("object_end", 14, RESULT);
/* 324:    */         
/* 325:521 */         return CUP$Parser$result;
/* 326:    */       case 26: 
/* 327:526 */         Object RESULT = null;
/* 328:    */         
/* 329:528 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("named_array", 16, RESULT);
/* 330:    */         
/* 331:530 */         return CUP$Parser$result;
/* 332:    */       case 27: 
/* 333:535 */         Object RESULT = null;
/* 334:    */         
/* 335:537 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("named_array", 16, RESULT);
/* 336:    */         
/* 337:539 */         return CUP$Parser$result;
/* 338:    */       case 28: 
/* 339:544 */         Object RESULT = null;
/* 340:545 */         String name = (String)((Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top - 2)).value;
/* 341:    */         
/* 342:547 */         JSONNode node = ((JSONNode)this.parser.getStack().peek()).addArray(name);
/* 343:548 */         this.parser.getStack().push(node);
/* 344:    */         
/* 345:550 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("named_array_start", 17, RESULT);
/* 346:    */         
/* 347:552 */         return CUP$Parser$result;
/* 348:    */       case 29: 
/* 349:557 */         Object RESULT = null;
/* 350:    */         
/* 351:559 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("anon_array", 15, RESULT);
/* 352:    */         
/* 353:561 */         return CUP$Parser$result;
/* 354:    */       case 30: 
/* 355:566 */         Object RESULT = null;
/* 356:    */         
/* 357:568 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("anon_array", 15, RESULT);
/* 358:    */         
/* 359:570 */         return CUP$Parser$result;
/* 360:    */       case 31: 
/* 361:575 */         Object RESULT = null;
/* 362:    */         
/* 363:577 */         JSONNode node = ((JSONNode)this.parser.getStack().peek()).addArray(null);
/* 364:578 */         this.parser.getStack().push(node);
/* 365:    */         
/* 366:580 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("anon_array_start", 18, RESULT);
/* 367:    */         
/* 368:582 */         return CUP$Parser$result;
/* 369:    */       case 32: 
/* 370:587 */         Object RESULT = null;
/* 371:    */         
/* 372:589 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("array_content", 19, RESULT);
/* 373:    */         
/* 374:591 */         return CUP$Parser$result;
/* 375:    */       case 33: 
/* 376:596 */         Object RESULT = null;
/* 377:    */         
/* 378:598 */         this.parser.getStack().pop();
/* 379:    */         
/* 380:600 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("array_end", 20, RESULT);
/* 381:    */         
/* 382:602 */         return CUP$Parser$result;
/* 383:    */       case 34: 
/* 384:607 */         Object RESULT = null;
/* 385:    */         
/* 386:609 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("elements", 21, RESULT);
/* 387:    */         
/* 388:611 */         return CUP$Parser$result;
/* 389:    */       case 35: 
/* 390:616 */         Object RESULT = null;
/* 391:    */         
/* 392:618 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("elements", 21, RESULT);
/* 393:    */         
/* 394:620 */         return CUP$Parser$result;
/* 395:    */       case 36: 
/* 396:625 */         Object RESULT = null;
/* 397:    */         
/* 398:627 */         ((JSONNode)this.parser.getStack().peek()).addArrayElement(null);
/* 399:    */         
/* 400:629 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("element", 22, RESULT);
/* 401:    */         
/* 402:631 */         return CUP$Parser$result;
/* 403:    */       case 37: 
/* 404:636 */         Object RESULT = null;
/* 405:637 */         Boolean b = (Boolean)((Symbol)CUP$Parser$stack.peek()).value;
/* 406:    */         
/* 407:639 */         ((JSONNode)this.parser.getStack().peek()).addArrayElement(b);
/* 408:    */         
/* 409:641 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("element", 22, RESULT);
/* 410:    */         
/* 411:643 */         return CUP$Parser$result;
/* 412:    */       case 38: 
/* 413:648 */         Object RESULT = null;
/* 414:649 */         Integer i = (Integer)((Symbol)CUP$Parser$stack.peek()).value;
/* 415:    */         
/* 416:651 */         ((JSONNode)this.parser.getStack().peek()).addArrayElement(i);
/* 417:    */         
/* 418:653 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("element", 22, RESULT);
/* 419:    */         
/* 420:655 */         return CUP$Parser$result;
/* 421:    */       case 39: 
/* 422:660 */         Object RESULT = null;
/* 423:661 */         Double d = (Double)((Symbol)CUP$Parser$stack.peek()).value;
/* 424:    */         
/* 425:663 */         ((JSONNode)this.parser.getStack().peek()).addArrayElement(d);
/* 426:    */         
/* 427:665 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("element", 22, RESULT);
/* 428:    */         
/* 429:667 */         return CUP$Parser$result;
/* 430:    */       case 40: 
/* 431:672 */         Object RESULT = null;
/* 432:673 */         String s = (String)((Symbol)CUP$Parser$stack.peek()).value;
/* 433:    */         
/* 434:675 */         ((JSONNode)this.parser.getStack().peek()).addArrayElement(s);
/* 435:    */         
/* 436:677 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("element", 22, RESULT);
/* 437:    */         
/* 438:679 */         return CUP$Parser$result;
/* 439:    */       case 41: 
/* 440:684 */         Object RESULT = null;
/* 441:    */         
/* 442:686 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("element", 22, RESULT);
/* 443:    */         
/* 444:688 */         return CUP$Parser$result;
/* 445:    */       case 42: 
/* 446:693 */         Object RESULT = null;
/* 447:    */         
/* 448:695 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("element", 22, RESULT);
/* 449:    */         
/* 450:697 */         return CUP$Parser$result;
/* 451:    */       }
/* 452:701 */       throw new Exception("Invalid action number " + CUP$Parser$act_num + "found in internal parse table");
/* 453:    */     }
/* 454:    */     
/* 455:    */     public final Symbol CUP$Parser$do_action(int CUP$Parser$act_num, lr_parser CUP$Parser$parser, Stack CUP$Parser$stack, int CUP$Parser$top)
/* 456:    */       throws Exception
/* 457:    */     {
/* 458:715 */       return CUP$Parser$do_action_part00000000(CUP$Parser$act_num, CUP$Parser$parser, CUP$Parser$stack, CUP$Parser$top);
/* 459:    */     }
/* 460:    */   }
/* 461:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.json.Parser
 * JD-Core Version:    0.7.0.1
 */