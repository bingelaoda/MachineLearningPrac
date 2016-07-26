/*   1:    */ package weka.core.expressionlanguage.parser;
/*   2:    */ 
/*   3:    */ import java.io.StringReader;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.List;
/*   6:    */ import java.util.Stack;
/*   7:    */ import java_cup.runtime.Symbol;
/*   8:    */ import java_cup.runtime.SymbolFactory;
/*   9:    */ import java_cup.runtime.lr_parser;
/*  10:    */ import weka.core.expressionlanguage.common.NoMacros;
/*  11:    */ import weka.core.expressionlanguage.common.NoVariables;
/*  12:    */ import weka.core.expressionlanguage.common.Operators;
/*  13:    */ import weka.core.expressionlanguage.common.Primitives.BooleanConstant;
/*  14:    */ import weka.core.expressionlanguage.common.Primitives.DoubleConstant;
/*  15:    */ import weka.core.expressionlanguage.common.Primitives.StringConstant;
/*  16:    */ import weka.core.expressionlanguage.core.Macro;
/*  17:    */ import weka.core.expressionlanguage.core.MacroDeclarations;
/*  18:    */ import weka.core.expressionlanguage.core.Node;
/*  19:    */ import weka.core.expressionlanguage.core.SemanticException;
/*  20:    */ import weka.core.expressionlanguage.core.SyntaxException;
/*  21:    */ import weka.core.expressionlanguage.core.VariableDeclarations;
/*  22:    */ 
/*  23:    */ public class Parser
/*  24:    */   extends lr_parser
/*  25:    */ {
/*  26:    */   public final Class getSymbolContainer()
/*  27:    */   {
/*  28: 32 */     return sym.class;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public Parser() {}
/*  32:    */   
/*  33:    */   public Parser(java_cup.runtime.Scanner s)
/*  34:    */   {
/*  35: 39 */     super(s);
/*  36:    */   }
/*  37:    */   
/*  38:    */   public Parser(java_cup.runtime.Scanner s, SymbolFactory sf)
/*  39:    */   {
/*  40: 42 */     super(s, sf);
/*  41:    */   }
/*  42:    */   
/*  43: 45 */   protected static final short[][] _production_table = unpackFromStrings(new String[] { "" });
/*  44:    */   
/*  45:    */   public short[][] production_table()
/*  46:    */   {
/*  47: 59 */     return _production_table;
/*  48:    */   }
/*  49:    */   
/*  50: 62 */   protected static final short[][] _action_table = unpackFromStrings(new String[] { "" });
/*  51:    */   
/*  52:    */   public short[][] action_table()
/*  53:    */   {
/*  54:175 */     return _action_table;
/*  55:    */   }
/*  56:    */   
/*  57:178 */   protected static final short[][] _reduce_table = unpackFromStrings(new String[] { "" });
/*  58:    */   protected CUP.Parser.actions action_obj;
/*  59:    */   
/*  60:    */   public short[][] reduce_table()
/*  61:    */   {
/*  62:202 */     return _reduce_table;
/*  63:    */   }
/*  64:    */   
/*  65:    */   protected void init_actions()
/*  66:    */   {
/*  67:210 */     this.action_obj = new CUP.Parser.actions(this);
/*  68:    */   }
/*  69:    */   
/*  70:    */   public Symbol do_action(int act_num, lr_parser parser, Stack stack, int top)
/*  71:    */     throws Exception
/*  72:    */   {
/*  73:222 */     return this.action_obj.CUP$Parser$do_action(act_num, parser, stack, top);
/*  74:    */   }
/*  75:    */   
/*  76:    */   public int start_state()
/*  77:    */   {
/*  78:226 */     return 0;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public int start_production()
/*  82:    */   {
/*  83:228 */     return 0;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public int EOF_sym()
/*  87:    */   {
/*  88:231 */     return 0;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public int error_sym()
/*  92:    */   {
/*  93:234 */     return 1;
/*  94:    */   }
/*  95:    */   
/*  96:239 */   private VariableDeclarations variables = new NoVariables();
/*  97:242 */   private MacroDeclarations macros = new NoMacros();
/*  98:    */   private Node root;
/*  99:    */   
/* 100:    */   public void setVariables(VariableDeclarations variables)
/* 101:    */   {
/* 102:253 */     this.variables = variables;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public void setMacros(MacroDeclarations macros)
/* 106:    */   {
/* 107:262 */     this.macros = macros;
/* 108:    */   }
/* 109:    */   
/* 110:    */   private void setRoot(Node root)
/* 111:    */   {
/* 112:269 */     this.root = root;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public Node getRoot()
/* 116:    */   {
/* 117:278 */     return this.root;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public static Node parse(String expr, VariableDeclarations variables, MacroDeclarations macros)
/* 121:    */     throws Exception
/* 122:    */   {
/* 123:294 */     Parser parser = new Parser(new Scanner(new StringReader(expr)));
/* 124:295 */     parser.setVariables(variables);
/* 125:296 */     parser.setMacros(macros);
/* 126:297 */     parser.parse();
/* 127:298 */     return parser.getRoot();
/* 128:    */   }
/* 129:    */   
/* 130:    */   public void unrecovered_syntax_error(Symbol token)
/* 131:    */     throws SyntaxException
/* 132:    */   {
/* 133:302 */     throw new SyntaxException("Syntax error at token '" + sym.terminalNames[token.sym] + "'!");
/* 134:    */   }
/* 135:    */   
/* 136:    */   class CUP$Parser$actions
/* 137:    */   {
/* 138:    */     private final Parser parser;
/* 139:    */     
/* 140:    */     CUP$Parser$actions(Parser parser)
/* 141:    */     {
/* 142:314 */       this.parser = parser;
/* 143:    */     }
/* 144:    */     
/* 145:    */     public final Symbol CUP$Parser$do_action_part00000000(int CUP$Parser$act_num, lr_parser CUP$Parser$parser, Stack CUP$Parser$stack, int CUP$Parser$top)
/* 146:    */       throws Exception
/* 147:    */     {
/* 148:    */       Symbol CUP$Parser$result;
/* 149:329 */       switch (CUP$Parser$act_num)
/* 150:    */       {
/* 151:    */       case 0: 
/* 152:334 */         Object RESULT = null;
/* 153:335 */         Node start_val = (Node)((Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top - 1)).value;
/* 154:336 */         RESULT = start_val;
/* 155:337 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("$START", 0, RESULT);
/* 156:    */         
/* 157:    */ 
/* 158:340 */         CUP$Parser$parser.done_parsing();
/* 159:341 */         return CUP$Parser$result;
/* 160:    */       case 1: 
/* 161:346 */         Node RESULT = null;
/* 162:347 */         Node e = (Node)((Symbol)CUP$Parser$stack.peek()).value;
/* 163:    */         
/* 164:349 */         RESULT = e;
/* 165:350 */         Parser.this.setRoot(RESULT);
/* 166:    */         
/* 167:352 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("unit", 0, RESULT);
/* 168:    */         
/* 169:354 */         return CUP$Parser$result;
/* 170:    */       case 2: 
/* 171:359 */         List<Node> RESULT = null;
/* 172:360 */         List<Node> l = (List)((Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top - 2)).value;
/* 173:361 */         Node e = (Node)((Symbol)CUP$Parser$stack.peek()).value;
/* 174:    */         
/* 175:363 */         l.add(e);
/* 176:364 */         RESULT = l;
/* 177:    */         
/* 178:366 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("paramlist", 3, RESULT);
/* 179:    */         
/* 180:368 */         return CUP$Parser$result;
/* 181:    */       case 3: 
/* 182:373 */         List<Node> RESULT = null;
/* 183:374 */         Node e = (Node)((Symbol)CUP$Parser$stack.peek()).value;
/* 184:    */         
/* 185:376 */         List<Node> l = new ArrayList();
/* 186:377 */         l.add(e);
/* 187:378 */         RESULT = l;
/* 188:    */         
/* 189:380 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("paramlist", 3, RESULT);
/* 190:    */         
/* 191:382 */         return CUP$Parser$result;
/* 192:    */       case 4: 
/* 193:387 */         List<Node> RESULT = null;
/* 194:388 */         List<Node> l = (List)((Symbol)CUP$Parser$stack.peek()).value;
/* 195:    */         
/* 196:390 */         RESULT = l;
/* 197:    */         
/* 198:392 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("paramlistOpt", 2, RESULT);
/* 199:    */         
/* 200:394 */         return CUP$Parser$result;
/* 201:    */       case 5: 
/* 202:399 */         List<Node> RESULT = null;
/* 203:    */         
/* 204:401 */         RESULT = new ArrayList();
/* 205:    */         
/* 206:403 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("paramlistOpt", 2, RESULT);
/* 207:    */         
/* 208:405 */         return CUP$Parser$result;
/* 209:    */       case 6: 
/* 210:410 */         Node RESULT = null;
/* 211:411 */         Node e = (Node)((Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top - 1)).value;
/* 212:    */         
/* 213:413 */         RESULT = e;
/* 214:    */         
/* 215:415 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("expr", 1, RESULT);
/* 216:    */         
/* 217:417 */         return CUP$Parser$result;
/* 218:    */       case 7: 
/* 219:422 */         Node RESULT = null;
/* 220:423 */         String m = (String)((Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top - 3)).value;
/* 221:424 */         List<Node> p = (List)((Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top - 1)).value;
/* 222:426 */         if (!Parser.this.macros.hasMacro(m)) {
/* 223:427 */           throw new SemanticException("Macro '" + m + "' is undefined!");
/* 224:    */         }
/* 225:428 */         RESULT = Parser.this.macros.getMacro(m).evaluate((Node[])p.toArray(new Node[0]));
/* 226:    */         
/* 227:430 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("expr", 1, RESULT);
/* 228:    */         
/* 229:432 */         return CUP$Parser$result;
/* 230:    */       case 8: 
/* 231:437 */         Node RESULT = null;
/* 232:438 */         String v = (String)((Symbol)CUP$Parser$stack.peek()).value;
/* 233:440 */         if (!Parser.this.variables.hasVariable(v)) {
/* 234:441 */           throw new SemanticException("Variable '" + v + "' is undefined!");
/* 235:    */         }
/* 236:442 */         RESULT = Parser.this.variables.getVariable(v);
/* 237:    */         
/* 238:444 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("expr", 1, RESULT);
/* 239:    */         
/* 240:446 */         return CUP$Parser$result;
/* 241:    */       case 9: 
/* 242:451 */         Node RESULT = null;
/* 243:452 */         Double f = (Double)((Symbol)CUP$Parser$stack.peek()).value;
/* 244:    */         
/* 245:454 */         RESULT = new Primitives.DoubleConstant(f.doubleValue());
/* 246:    */         
/* 247:456 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("expr", 1, RESULT);
/* 248:    */         
/* 249:458 */         return CUP$Parser$result;
/* 250:    */       case 10: 
/* 251:463 */         Node RESULT = null;
/* 252:464 */         Boolean b = (Boolean)((Symbol)CUP$Parser$stack.peek()).value;
/* 253:    */         
/* 254:466 */         RESULT = new Primitives.BooleanConstant(b.booleanValue());
/* 255:    */         
/* 256:468 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("expr", 1, RESULT);
/* 257:    */         
/* 258:470 */         return CUP$Parser$result;
/* 259:    */       case 11: 
/* 260:475 */         Node RESULT = null;
/* 261:476 */         String s = (String)((Symbol)CUP$Parser$stack.peek()).value;
/* 262:    */         
/* 263:478 */         RESULT = new Primitives.StringConstant(s);
/* 264:    */         
/* 265:480 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("expr", 1, RESULT);
/* 266:    */         
/* 267:482 */         return CUP$Parser$result;
/* 268:    */       case 12: 
/* 269:487 */         Node RESULT = null;
/* 270:488 */         Node e = (Node)((Symbol)CUP$Parser$stack.peek()).value;
/* 271:    */         
/* 272:490 */         RESULT = Operators.uplus(e);
/* 273:    */         
/* 274:492 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("expr", 1, RESULT);
/* 275:    */         
/* 276:494 */         return CUP$Parser$result;
/* 277:    */       case 13: 
/* 278:499 */         Node RESULT = null;
/* 279:500 */         Node e = (Node)((Symbol)CUP$Parser$stack.peek()).value;
/* 280:    */         
/* 281:502 */         RESULT = Operators.uminus(e);
/* 282:    */         
/* 283:504 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("expr", 1, RESULT);
/* 284:    */         
/* 285:506 */         return CUP$Parser$result;
/* 286:    */       case 14: 
/* 287:511 */         Node RESULT = null;
/* 288:512 */         Node l = (Node)((Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top - 2)).value;
/* 289:513 */         Node r = (Node)((Symbol)CUP$Parser$stack.peek()).value;
/* 290:    */         
/* 291:515 */         RESULT = Operators.pow(l, r);
/* 292:    */         
/* 293:517 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("expr", 1, RESULT);
/* 294:    */         
/* 295:519 */         return CUP$Parser$result;
/* 296:    */       case 15: 
/* 297:524 */         Node RESULT = null;
/* 298:525 */         Node l = (Node)((Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top - 2)).value;
/* 299:526 */         Node r = (Node)((Symbol)CUP$Parser$stack.peek()).value;
/* 300:    */         
/* 301:528 */         RESULT = Operators.plus(l, r);
/* 302:    */         
/* 303:530 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("expr", 1, RESULT);
/* 304:    */         
/* 305:532 */         return CUP$Parser$result;
/* 306:    */       case 16: 
/* 307:537 */         Node RESULT = null;
/* 308:538 */         Node l = (Node)((Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top - 2)).value;
/* 309:539 */         Node r = (Node)((Symbol)CUP$Parser$stack.peek()).value;
/* 310:    */         
/* 311:541 */         RESULT = Operators.minus(l, r);
/* 312:    */         
/* 313:543 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("expr", 1, RESULT);
/* 314:    */         
/* 315:545 */         return CUP$Parser$result;
/* 316:    */       case 17: 
/* 317:550 */         Node RESULT = null;
/* 318:551 */         Node l = (Node)((Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top - 2)).value;
/* 319:552 */         Node r = (Node)((Symbol)CUP$Parser$stack.peek()).value;
/* 320:    */         
/* 321:554 */         RESULT = Operators.times(l, r);
/* 322:    */         
/* 323:556 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("expr", 1, RESULT);
/* 324:    */         
/* 325:558 */         return CUP$Parser$result;
/* 326:    */       case 18: 
/* 327:563 */         Node RESULT = null;
/* 328:564 */         Node l = (Node)((Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top - 2)).value;
/* 329:565 */         Node r = (Node)((Symbol)CUP$Parser$stack.peek()).value;
/* 330:    */         
/* 331:567 */         RESULT = Operators.division(l, r);
/* 332:    */         
/* 333:569 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("expr", 1, RESULT);
/* 334:    */         
/* 335:571 */         return CUP$Parser$result;
/* 336:    */       case 19: 
/* 337:576 */         Node RESULT = null;
/* 338:577 */         Node l = (Node)((Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top - 2)).value;
/* 339:578 */         Node r = (Node)((Symbol)CUP$Parser$stack.peek()).value;
/* 340:    */         
/* 341:580 */         RESULT = Operators.and(l, r);
/* 342:    */         
/* 343:582 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("expr", 1, RESULT);
/* 344:    */         
/* 345:584 */         return CUP$Parser$result;
/* 346:    */       case 20: 
/* 347:589 */         Node RESULT = null;
/* 348:590 */         Node l = (Node)((Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top - 2)).value;
/* 349:591 */         Node r = (Node)((Symbol)CUP$Parser$stack.peek()).value;
/* 350:    */         
/* 351:593 */         RESULT = Operators.or(l, r);
/* 352:    */         
/* 353:595 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("expr", 1, RESULT);
/* 354:    */         
/* 355:597 */         return CUP$Parser$result;
/* 356:    */       case 21: 
/* 357:602 */         Node RESULT = null;
/* 358:603 */         Node e = (Node)((Symbol)CUP$Parser$stack.peek()).value;
/* 359:    */         
/* 360:605 */         RESULT = Operators.not(e);
/* 361:    */         
/* 362:607 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("expr", 1, RESULT);
/* 363:    */         
/* 364:609 */         return CUP$Parser$result;
/* 365:    */       case 22: 
/* 366:614 */         Node RESULT = null;
/* 367:615 */         Node l = (Node)((Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top - 2)).value;
/* 368:616 */         Node r = (Node)((Symbol)CUP$Parser$stack.peek()).value;
/* 369:    */         
/* 370:618 */         RESULT = Operators.equal(l, r);
/* 371:    */         
/* 372:620 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("expr", 1, RESULT);
/* 373:    */         
/* 374:622 */         return CUP$Parser$result;
/* 375:    */       case 23: 
/* 376:627 */         Node RESULT = null;
/* 377:628 */         Node l = (Node)((Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top - 2)).value;
/* 378:629 */         Node r = (Node)((Symbol)CUP$Parser$stack.peek()).value;
/* 379:    */         
/* 380:631 */         RESULT = Operators.lessThan(l, r);
/* 381:    */         
/* 382:633 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("expr", 1, RESULT);
/* 383:    */         
/* 384:635 */         return CUP$Parser$result;
/* 385:    */       case 24: 
/* 386:640 */         Node RESULT = null;
/* 387:641 */         Node l = (Node)((Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top - 2)).value;
/* 388:642 */         Node r = (Node)((Symbol)CUP$Parser$stack.peek()).value;
/* 389:    */         
/* 390:644 */         RESULT = Operators.lessEqual(l, r);
/* 391:    */         
/* 392:646 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("expr", 1, RESULT);
/* 393:    */         
/* 394:648 */         return CUP$Parser$result;
/* 395:    */       case 25: 
/* 396:653 */         Node RESULT = null;
/* 397:654 */         Node l = (Node)((Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top - 2)).value;
/* 398:655 */         Node r = (Node)((Symbol)CUP$Parser$stack.peek()).value;
/* 399:    */         
/* 400:657 */         RESULT = Operators.greaterThan(l, r);
/* 401:    */         
/* 402:659 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("expr", 1, RESULT);
/* 403:    */         
/* 404:661 */         return CUP$Parser$result;
/* 405:    */       case 26: 
/* 406:666 */         Node RESULT = null;
/* 407:667 */         Node l = (Node)((Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top - 2)).value;
/* 408:668 */         Node r = (Node)((Symbol)CUP$Parser$stack.peek()).value;
/* 409:    */         
/* 410:670 */         RESULT = Operators.greaterEqual(l, r);
/* 411:    */         
/* 412:672 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("expr", 1, RESULT);
/* 413:    */         
/* 414:674 */         return CUP$Parser$result;
/* 415:    */       case 27: 
/* 416:679 */         Node RESULT = null;
/* 417:680 */         Node l = (Node)((Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top - 2)).value;
/* 418:681 */         Node r = (Node)((Symbol)CUP$Parser$stack.peek()).value;
/* 419:    */         
/* 420:683 */         RESULT = Operators.is(l, r);
/* 421:    */         
/* 422:685 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("expr", 1, RESULT);
/* 423:    */         
/* 424:687 */         return CUP$Parser$result;
/* 425:    */       case 28: 
/* 426:692 */         Node RESULT = null;
/* 427:693 */         Node l = (Node)((Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top - 2)).value;
/* 428:694 */         Node r = (Node)((Symbol)CUP$Parser$stack.peek()).value;
/* 429:    */         
/* 430:696 */         RESULT = Operators.regexp(l, r);
/* 431:    */         
/* 432:698 */         CUP$Parser$result = this.parser.getSymbolFactory().newSymbol("expr", 1, RESULT);
/* 433:    */         
/* 434:700 */         return CUP$Parser$result;
/* 435:    */       }
/* 436:704 */       throw new Exception("Invalid action number " + CUP$Parser$act_num + "found in internal parse table");
/* 437:    */     }
/* 438:    */     
/* 439:    */     public final Symbol CUP$Parser$do_action(int CUP$Parser$act_num, lr_parser CUP$Parser$parser, Stack CUP$Parser$stack, int CUP$Parser$top)
/* 440:    */       throws Exception
/* 441:    */     {
/* 442:718 */       return CUP$Parser$do_action_part00000000(CUP$Parser$act_num, CUP$Parser$parser, CUP$Parser$stack, CUP$Parser$top);
/* 443:    */     }
/* 444:    */   }
/* 445:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.expressionlanguage.parser.Parser
 * JD-Core Version:    0.7.0.1
 */