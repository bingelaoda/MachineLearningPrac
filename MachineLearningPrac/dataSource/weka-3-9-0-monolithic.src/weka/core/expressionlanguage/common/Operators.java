/*   1:    */ package weka.core.expressionlanguage.common;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.regex.Matcher;
/*   5:    */ import java.util.regex.Pattern;
/*   6:    */ import weka.core.expressionlanguage.core.Node;
/*   7:    */ import weka.core.expressionlanguage.core.SemanticException;
/*   8:    */ 
/*   9:    */ public class Operators
/*  10:    */ {
/*  11:    */   public static Node plus(Node left, Node right)
/*  12:    */     throws SemanticException
/*  13:    */   {
/*  14: 50 */     if (((left instanceof Primitives.DoubleExpression)) && ((right instanceof Primitives.DoubleExpression))) {
/*  15: 51 */       return new Addition((Primitives.DoubleExpression)left, (Primitives.DoubleExpression)right);
/*  16:    */     }
/*  17: 52 */     if (((left instanceof Primitives.StringExpression)) && ((right instanceof Primitives.StringExpression))) {
/*  18: 53 */       return new Concatenation((Primitives.StringExpression)left, (Primitives.StringExpression)right);
/*  19:    */     }
/*  20: 56 */     throw new SemanticException("Plus is only applicable to doubles & Strings!");
/*  21:    */   }
/*  22:    */   
/*  23:    */   public static Node minus(Node left, Node right)
/*  24:    */     throws SemanticException
/*  25:    */   {
/*  26: 63 */     if (((left instanceof Primitives.DoubleExpression)) && ((right instanceof Primitives.DoubleExpression))) {
/*  27: 64 */       return new Subtraction((Primitives.DoubleExpression)left, (Primitives.DoubleExpression)right);
/*  28:    */     }
/*  29: 65 */     throw new SemanticException("Minus is only applicable to doubles!");
/*  30:    */   }
/*  31:    */   
/*  32:    */   public static Node times(Node left, Node right)
/*  33:    */     throws SemanticException
/*  34:    */   {
/*  35: 72 */     if (((left instanceof Primitives.DoubleExpression)) && ((right instanceof Primitives.DoubleExpression))) {
/*  36: 73 */       return new Multiplication((Primitives.DoubleExpression)left, (Primitives.DoubleExpression)right);
/*  37:    */     }
/*  38: 75 */     throw new SemanticException("Multiplication is only applicable to doubles!");
/*  39:    */   }
/*  40:    */   
/*  41:    */   public static Node division(Node left, Node right)
/*  42:    */     throws SemanticException
/*  43:    */   {
/*  44: 82 */     if (((left instanceof Primitives.DoubleExpression)) && ((right instanceof Primitives.DoubleExpression))) {
/*  45: 83 */       return new Division((Primitives.DoubleExpression)left, (Primitives.DoubleExpression)right);
/*  46:    */     }
/*  47: 84 */     throw new SemanticException("division is only applicable to doubles!");
/*  48:    */   }
/*  49:    */   
/*  50:    */   public static Node uplus(Node expr)
/*  51:    */     throws SemanticException
/*  52:    */   {
/*  53: 91 */     if ((expr instanceof Primitives.DoubleExpression)) {
/*  54: 92 */       return expr;
/*  55:    */     }
/*  56: 93 */     throw new SemanticException("unary minus is only applicable to doubles!");
/*  57:    */   }
/*  58:    */   
/*  59:    */   public static Node uminus(Node expr)
/*  60:    */     throws SemanticException
/*  61:    */   {
/*  62:100 */     if ((expr instanceof Primitives.DoubleExpression)) {
/*  63:101 */       return new UMinus((Primitives.DoubleExpression)expr);
/*  64:    */     }
/*  65:102 */     throw new SemanticException("unary minus is only applicable to doubles!");
/*  66:    */   }
/*  67:    */   
/*  68:    */   public static Node pow(Node left, Node right)
/*  69:    */     throws SemanticException
/*  70:    */   {
/*  71:109 */     if (((left instanceof Primitives.DoubleExpression)) && ((right instanceof Primitives.DoubleExpression))) {
/*  72:110 */       return new Pow((Primitives.DoubleExpression)left, (Primitives.DoubleExpression)right);
/*  73:    */     }
/*  74:111 */     throw new SemanticException("Power is only applicable to doubles!");
/*  75:    */   }
/*  76:    */   
/*  77:    */   public static Primitives.BooleanExpression lessThan(Node left, Node right)
/*  78:    */     throws SemanticException
/*  79:    */   {
/*  80:119 */     if (((left instanceof Primitives.DoubleExpression)) && ((right instanceof Primitives.DoubleExpression))) {
/*  81:120 */       return new LessThan((Primitives.DoubleExpression)left, (Primitives.DoubleExpression)right);
/*  82:    */     }
/*  83:121 */     throw new SemanticException("less than is only applicable to doubles!");
/*  84:    */   }
/*  85:    */   
/*  86:    */   public static Primitives.BooleanExpression lessEqual(Node left, Node right)
/*  87:    */     throws SemanticException
/*  88:    */   {
/*  89:129 */     if (((left instanceof Primitives.DoubleExpression)) && ((right instanceof Primitives.DoubleExpression))) {
/*  90:130 */       return new LessEqual((Primitives.DoubleExpression)left, (Primitives.DoubleExpression)right);
/*  91:    */     }
/*  92:131 */     throw new SemanticException("less equal is only applicable to doubles!");
/*  93:    */   }
/*  94:    */   
/*  95:    */   public static Primitives.BooleanExpression greaterThan(Node left, Node right)
/*  96:    */     throws SemanticException
/*  97:    */   {
/*  98:139 */     if (((left instanceof Primitives.DoubleExpression)) && ((right instanceof Primitives.DoubleExpression))) {
/*  99:140 */       return new GreaterThan((Primitives.DoubleExpression)left, (Primitives.DoubleExpression)right);
/* 100:    */     }
/* 101:141 */     throw new SemanticException("greater than is only applicable to doubles!");
/* 102:    */   }
/* 103:    */   
/* 104:    */   public static Primitives.BooleanExpression greaterEqual(Node left, Node right)
/* 105:    */     throws SemanticException
/* 106:    */   {
/* 107:149 */     if (((left instanceof Primitives.DoubleExpression)) && ((right instanceof Primitives.DoubleExpression))) {
/* 108:150 */       return new GreaterEqual((Primitives.DoubleExpression)left, (Primitives.DoubleExpression)right);
/* 109:    */     }
/* 110:151 */     throw new SemanticException("greater equal is only applicable to doubles!");
/* 111:    */   }
/* 112:    */   
/* 113:    */   public static Primitives.BooleanExpression equal(Node left, Node right)
/* 114:    */     throws SemanticException
/* 115:    */   {
/* 116:159 */     if (((left instanceof Primitives.DoubleExpression)) && ((right instanceof Primitives.DoubleExpression))) {
/* 117:160 */       return new Equal((Primitives.DoubleExpression)left, (Primitives.DoubleExpression)right);
/* 118:    */     }
/* 119:161 */     throw new SemanticException("equal is only applicable to doubles!");
/* 120:    */   }
/* 121:    */   
/* 122:    */   public static Primitives.BooleanExpression not(Node expr)
/* 123:    */     throws SemanticException
/* 124:    */   {
/* 125:168 */     if ((expr instanceof Primitives.BooleanExpression)) {
/* 126:169 */       return new Not((Primitives.BooleanExpression)expr);
/* 127:    */     }
/* 128:170 */     throw new SemanticException("Logical not is only applicable to booleans!");
/* 129:    */   }
/* 130:    */   
/* 131:    */   public static Primitives.BooleanExpression and(Node left, Node right)
/* 132:    */     throws SemanticException
/* 133:    */   {
/* 134:178 */     if (((left instanceof Primitives.BooleanExpression)) && ((right instanceof Primitives.BooleanExpression))) {
/* 135:179 */       return new And((Primitives.BooleanExpression)left, (Primitives.BooleanExpression)right);
/* 136:    */     }
/* 137:180 */     throw new SemanticException("Logical and is only applicable to booleans!");
/* 138:    */   }
/* 139:    */   
/* 140:    */   public static Primitives.BooleanExpression or(Node left, Node right)
/* 141:    */     throws SemanticException
/* 142:    */   {
/* 143:188 */     if (((left instanceof Primitives.BooleanExpression)) && ((right instanceof Primitives.BooleanExpression))) {
/* 144:189 */       return new Or((Primitives.BooleanExpression)left, (Primitives.BooleanExpression)right);
/* 145:    */     }
/* 146:190 */     throw new SemanticException("Logical or is only applicable to booleans!");
/* 147:    */   }
/* 148:    */   
/* 149:    */   public static Primitives.BooleanExpression is(Node left, Node right)
/* 150:    */     throws SemanticException
/* 151:    */   {
/* 152:198 */     if (((left instanceof Primitives.StringExpression)) && ((right instanceof Primitives.StringExpression))) {
/* 153:199 */       return new Is((Primitives.StringExpression)left, (Primitives.StringExpression)right);
/* 154:    */     }
/* 155:200 */     throw new SemanticException("Is operator is only applicable to strings!");
/* 156:    */   }
/* 157:    */   
/* 158:    */   public static Primitives.BooleanExpression regexp(Node left, Node right)
/* 159:    */     throws SemanticException
/* 160:    */   {
/* 161:209 */     if ((left instanceof Primitives.StringExpression))
/* 162:    */     {
/* 163:210 */       if ((right instanceof Primitives.StringConstant)) {
/* 164:211 */         return new CompiledRegexp((Primitives.StringExpression)left, ((Primitives.StringConstant)right).evaluate());
/* 165:    */       }
/* 166:213 */       if ((right instanceof Primitives.StringExpression)) {
/* 167:214 */         return new Regexp((Primitives.StringExpression)left, (Primitives.StringExpression)right);
/* 168:    */       }
/* 169:    */     }
/* 170:216 */     throw new SemanticException("Is operator is only applicable to strings!");
/* 171:    */   }
/* 172:    */   
/* 173:    */   private static abstract class DoubleBinaryExpression
/* 174:    */     implements Primitives.DoubleExpression, Serializable
/* 175:    */   {
/* 176:    */     private static final long serialVersionUID = -5632795030311662604L;
/* 177:    */     final Primitives.DoubleExpression left;
/* 178:    */     final Primitives.DoubleExpression right;
/* 179:    */     
/* 180:    */     public DoubleBinaryExpression(Primitives.DoubleExpression left, Primitives.DoubleExpression right)
/* 181:    */     {
/* 182:228 */       this.left = left;
/* 183:229 */       this.right = right;
/* 184:    */     }
/* 185:    */   }
/* 186:    */   
/* 187:    */   private static class Addition
/* 188:    */     extends Operators.DoubleBinaryExpression
/* 189:    */     implements Serializable
/* 190:    */   {
/* 191:    */     private static final long serialVersionUID = 4742624413216069408L;
/* 192:    */     
/* 193:    */     public Addition(Primitives.DoubleExpression left, Primitives.DoubleExpression right)
/* 194:    */     {
/* 195:240 */       super(right);
/* 196:    */     }
/* 197:    */     
/* 198:    */     public double evaluate()
/* 199:    */     {
/* 200:245 */       return this.left.evaluate() + this.right.evaluate();
/* 201:    */     }
/* 202:    */   }
/* 203:    */   
/* 204:    */   private static class Subtraction
/* 205:    */     extends Operators.DoubleBinaryExpression
/* 206:    */     implements Serializable
/* 207:    */   {
/* 208:    */     private static final long serialVersionUID = 2136831100085494486L;
/* 209:    */     
/* 210:    */     public Subtraction(Primitives.DoubleExpression left, Primitives.DoubleExpression right)
/* 211:    */     {
/* 212:255 */       super(right);
/* 213:    */     }
/* 214:    */     
/* 215:    */     public double evaluate()
/* 216:    */     {
/* 217:260 */       return this.left.evaluate() - this.right.evaluate();
/* 218:    */     }
/* 219:    */   }
/* 220:    */   
/* 221:    */   private static class Multiplication
/* 222:    */     extends Operators.DoubleBinaryExpression
/* 223:    */     implements Serializable
/* 224:    */   {
/* 225:    */     private static final long serialVersionUID = 3119913759352807383L;
/* 226:    */     
/* 227:    */     public Multiplication(Primitives.DoubleExpression left, Primitives.DoubleExpression right)
/* 228:    */     {
/* 229:270 */       super(right);
/* 230:    */     }
/* 231:    */     
/* 232:    */     public double evaluate()
/* 233:    */     {
/* 234:275 */       return this.left.evaluate() * this.right.evaluate();
/* 235:    */     }
/* 236:    */   }
/* 237:    */   
/* 238:    */   private static class UMinus
/* 239:    */     implements Primitives.DoubleExpression, Serializable
/* 240:    */   {
/* 241:    */     private static final long serialVersionUID = 8950381197456945108L;
/* 242:    */     private final Primitives.DoubleExpression expr;
/* 243:    */     
/* 244:    */     public UMinus(Primitives.DoubleExpression expr)
/* 245:    */     {
/* 246:287 */       this.expr = expr;
/* 247:    */     }
/* 248:    */     
/* 249:    */     public double evaluate()
/* 250:    */     {
/* 251:292 */       return -this.expr.evaluate();
/* 252:    */     }
/* 253:    */   }
/* 254:    */   
/* 255:    */   private static class Division
/* 256:    */     extends Operators.DoubleBinaryExpression
/* 257:    */     implements Serializable
/* 258:    */   {
/* 259:    */     private static final long serialVersionUID = -8438478400061106753L;
/* 260:    */     
/* 261:    */     public Division(Primitives.DoubleExpression left, Primitives.DoubleExpression right)
/* 262:    */     {
/* 263:302 */       super(right);
/* 264:    */     }
/* 265:    */     
/* 266:    */     public double evaluate()
/* 267:    */     {
/* 268:307 */       return this.left.evaluate() / this.right.evaluate();
/* 269:    */     }
/* 270:    */   }
/* 271:    */   
/* 272:    */   private static class Pow
/* 273:    */     extends Operators.DoubleBinaryExpression
/* 274:    */     implements Serializable
/* 275:    */   {
/* 276:    */     private static final long serialVersionUID = -5103792715762588751L;
/* 277:    */     
/* 278:    */     public Pow(Primitives.DoubleExpression left, Primitives.DoubleExpression right)
/* 279:    */     {
/* 280:318 */       super(right);
/* 281:    */     }
/* 282:    */     
/* 283:    */     public double evaluate()
/* 284:    */     {
/* 285:323 */       return Math.pow(this.left.evaluate(), this.right.evaluate());
/* 286:    */     }
/* 287:    */   }
/* 288:    */   
/* 289:    */   private static abstract class BooleanBinaryExpression<T>
/* 290:    */     implements Primitives.BooleanExpression, Serializable
/* 291:    */   {
/* 292:    */     private static final long serialVersionUID = -5375209267408472403L;
/* 293:    */     final T left;
/* 294:    */     final T right;
/* 295:    */     
/* 296:    */     public BooleanBinaryExpression(T left, T right)
/* 297:    */     {
/* 298:337 */       this.left = left;
/* 299:338 */       this.right = right;
/* 300:    */     }
/* 301:    */   }
/* 302:    */   
/* 303:    */   private static class LessThan
/* 304:    */     extends Operators.BooleanBinaryExpression<Primitives.DoubleExpression>
/* 305:    */     implements Serializable
/* 306:    */   {
/* 307:    */     private static final long serialVersionUID = -4323355926531143842L;
/* 308:    */     
/* 309:    */     public LessThan(Primitives.DoubleExpression left, Primitives.DoubleExpression right)
/* 310:    */     {
/* 311:348 */       super(right);
/* 312:    */     }
/* 313:    */     
/* 314:    */     public boolean evaluate()
/* 315:    */     {
/* 316:353 */       return ((Primitives.DoubleExpression)this.left).evaluate() < ((Primitives.DoubleExpression)this.right).evaluate();
/* 317:    */     }
/* 318:    */   }
/* 319:    */   
/* 320:    */   private static class LessEqual
/* 321:    */     extends Operators.BooleanBinaryExpression<Primitives.DoubleExpression>
/* 322:    */     implements Serializable
/* 323:    */   {
/* 324:    */     private static final long serialVersionUID = -1949681957973467756L;
/* 325:    */     
/* 326:    */     public LessEqual(Primitives.DoubleExpression left, Primitives.DoubleExpression right)
/* 327:    */     {
/* 328:364 */       super(right);
/* 329:    */     }
/* 330:    */     
/* 331:    */     public boolean evaluate()
/* 332:    */     {
/* 333:369 */       return ((Primitives.DoubleExpression)this.left).evaluate() <= ((Primitives.DoubleExpression)this.right).evaluate();
/* 334:    */     }
/* 335:    */   }
/* 336:    */   
/* 337:    */   private static class GreaterThan
/* 338:    */     extends Operators.BooleanBinaryExpression<Primitives.DoubleExpression>
/* 339:    */     implements Serializable
/* 340:    */   {
/* 341:    */     private static final long serialVersionUID = 4541137398510802289L;
/* 342:    */     
/* 343:    */     public GreaterThan(Primitives.DoubleExpression left, Primitives.DoubleExpression right)
/* 344:    */     {
/* 345:380 */       super(right);
/* 346:    */     }
/* 347:    */     
/* 348:    */     public boolean evaluate()
/* 349:    */     {
/* 350:385 */       return ((Primitives.DoubleExpression)this.left).evaluate() > ((Primitives.DoubleExpression)this.right).evaluate();
/* 351:    */     }
/* 352:    */   }
/* 353:    */   
/* 354:    */   private static class GreaterEqual
/* 355:    */     extends Operators.BooleanBinaryExpression<Primitives.DoubleExpression>
/* 356:    */     implements Serializable
/* 357:    */   {
/* 358:    */     private static final long serialVersionUID = 3425719763247073382L;
/* 359:    */     
/* 360:    */     public GreaterEqual(Primitives.DoubleExpression left, Primitives.DoubleExpression right)
/* 361:    */     {
/* 362:396 */       super(right);
/* 363:    */     }
/* 364:    */     
/* 365:    */     public boolean evaluate()
/* 366:    */     {
/* 367:401 */       return ((Primitives.DoubleExpression)this.left).evaluate() >= ((Primitives.DoubleExpression)this.right).evaluate();
/* 368:    */     }
/* 369:    */   }
/* 370:    */   
/* 371:    */   private static class Equal
/* 372:    */     extends Operators.BooleanBinaryExpression<Primitives.DoubleExpression>
/* 373:    */     implements Serializable
/* 374:    */   {
/* 375:    */     private static final long serialVersionUID = 4154699553290213656L;
/* 376:    */     
/* 377:    */     public Equal(Primitives.DoubleExpression left, Primitives.DoubleExpression right)
/* 378:    */     {
/* 379:412 */       super(right);
/* 380:    */     }
/* 381:    */     
/* 382:    */     public boolean evaluate()
/* 383:    */     {
/* 384:417 */       return ((Primitives.DoubleExpression)this.left).evaluate() == ((Primitives.DoubleExpression)this.right).evaluate();
/* 385:    */     }
/* 386:    */   }
/* 387:    */   
/* 388:    */   private static class And
/* 389:    */     extends Operators.BooleanBinaryExpression<Primitives.BooleanExpression>
/* 390:    */     implements Serializable
/* 391:    */   {
/* 392:    */     private static final long serialVersionUID = 6786891291372905824L;
/* 393:    */     
/* 394:    */     public And(Primitives.BooleanExpression left, Primitives.BooleanExpression right)
/* 395:    */     {
/* 396:428 */       super(right);
/* 397:    */     }
/* 398:    */     
/* 399:    */     public boolean evaluate()
/* 400:    */     {
/* 401:433 */       return (((Primitives.BooleanExpression)this.left).evaluate()) && (((Primitives.BooleanExpression)this.right).evaluate());
/* 402:    */     }
/* 403:    */   }
/* 404:    */   
/* 405:    */   private static class Or
/* 406:    */     extends Operators.BooleanBinaryExpression<Primitives.BooleanExpression>
/* 407:    */     implements Serializable
/* 408:    */   {
/* 409:    */     private static final long serialVersionUID = -5943051466425242059L;
/* 410:    */     
/* 411:    */     public Or(Primitives.BooleanExpression left, Primitives.BooleanExpression right)
/* 412:    */     {
/* 413:444 */       super(right);
/* 414:    */     }
/* 415:    */     
/* 416:    */     public boolean evaluate()
/* 417:    */     {
/* 418:449 */       return (((Primitives.BooleanExpression)this.left).evaluate()) || (((Primitives.BooleanExpression)this.right).evaluate());
/* 419:    */     }
/* 420:    */   }
/* 421:    */   
/* 422:    */   private static class Not
/* 423:    */     implements Primitives.BooleanExpression, Serializable
/* 424:    */   {
/* 425:    */     private static final long serialVersionUID = -6235716110409152192L;
/* 426:    */     private final Primitives.BooleanExpression expr;
/* 427:    */     
/* 428:    */     public Not(Primitives.BooleanExpression expr)
/* 429:    */     {
/* 430:461 */       this.expr = expr;
/* 431:    */     }
/* 432:    */     
/* 433:    */     public boolean evaluate()
/* 434:    */     {
/* 435:466 */       return !this.expr.evaluate();
/* 436:    */     }
/* 437:    */   }
/* 438:    */   
/* 439:    */   private static class Is
/* 440:    */     extends Operators.BooleanBinaryExpression<Primitives.StringExpression>
/* 441:    */     implements Serializable
/* 442:    */   {
/* 443:    */     private static final long serialVersionUID = -7519297057279624722L;
/* 444:    */     
/* 445:    */     public Is(Primitives.StringExpression left, Primitives.StringExpression right)
/* 446:    */     {
/* 447:476 */       super(right);
/* 448:    */     }
/* 449:    */     
/* 450:    */     public boolean evaluate()
/* 451:    */     {
/* 452:481 */       return ((Primitives.StringExpression)this.left).evaluate().equals(((Primitives.StringExpression)this.right).evaluate());
/* 453:    */     }
/* 454:    */   }
/* 455:    */   
/* 456:    */   private static class Regexp
/* 457:    */     extends Operators.BooleanBinaryExpression<Primitives.StringExpression>
/* 458:    */     implements Serializable
/* 459:    */   {
/* 460:    */     private static final long serialVersionUID = -3987002284718527200L;
/* 461:    */     
/* 462:    */     public Regexp(Primitives.StringExpression left, Primitives.StringExpression right)
/* 463:    */     {
/* 464:491 */       super(right);
/* 465:    */     }
/* 466:    */     
/* 467:    */     public boolean evaluate()
/* 468:    */     {
/* 469:496 */       return ((Primitives.StringExpression)this.left).evaluate().matches(((Primitives.StringExpression)this.right).evaluate());
/* 470:    */     }
/* 471:    */   }
/* 472:    */   
/* 473:    */   private static class CompiledRegexp
/* 474:    */     implements Primitives.BooleanExpression, Serializable
/* 475:    */   {
/* 476:    */     private static final long serialVersionUID = -224974827347001236L;
/* 477:    */     private final Primitives.StringExpression expr;
/* 478:    */     private final Pattern pattern;
/* 479:    */     
/* 480:    */     public CompiledRegexp(Primitives.StringExpression expr, String pattern)
/* 481:    */     {
/* 482:509 */       this.expr = expr;
/* 483:510 */       this.pattern = Pattern.compile(pattern);
/* 484:    */     }
/* 485:    */     
/* 486:    */     public boolean evaluate()
/* 487:    */     {
/* 488:515 */       return this.pattern.matcher(this.expr.evaluate()).matches();
/* 489:    */     }
/* 490:    */   }
/* 491:    */   
/* 492:    */   private static class Concatenation
/* 493:    */     implements Primitives.StringExpression, Serializable
/* 494:    */   {
/* 495:    */     private static final long serialVersionUID = 2413200029613562555L;
/* 496:    */     private final Primitives.StringExpression left;
/* 497:    */     private final Primitives.StringExpression right;
/* 498:    */     
/* 499:    */     public Concatenation(Primitives.StringExpression left, Primitives.StringExpression right)
/* 500:    */     {
/* 501:527 */       this.left = left;
/* 502:528 */       this.right = right;
/* 503:    */     }
/* 504:    */     
/* 505:    */     public String evaluate()
/* 506:    */     {
/* 507:533 */       return this.left.evaluate() + this.right.evaluate();
/* 508:    */     }
/* 509:    */   }
/* 510:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.expressionlanguage.common.Operators
 * JD-Core Version:    0.7.0.1
 */