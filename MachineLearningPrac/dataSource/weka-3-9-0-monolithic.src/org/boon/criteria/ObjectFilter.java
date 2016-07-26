/*    1:     */ package org.boon.criteria;
/*    2:     */ 
/*    3:     */ import java.util.ArrayList;
/*    4:     */ import java.util.Collection;
/*    5:     */ import java.util.Collections;
/*    6:     */ import java.util.Iterator;
/*    7:     */ import java.util.List;
/*    8:     */ import java.util.Set;
/*    9:     */ import org.boon.Boon;
/*   10:     */ import org.boon.Exceptions;
/*   11:     */ import org.boon.Lists;
/*   12:     */ import org.boon.Ok;
/*   13:     */ import org.boon.core.Conversions;
/*   14:     */ import org.boon.core.Predicate;
/*   15:     */ import org.boon.core.Typ;
/*   16:     */ import org.boon.core.TypeType;
/*   17:     */ import org.boon.core.reflection.BeanUtils;
/*   18:     */ import org.boon.core.reflection.Invoker;
/*   19:     */ import org.boon.core.reflection.fields.FieldAccess;
/*   20:     */ import org.boon.criteria.internal.Criteria;
/*   21:     */ import org.boon.criteria.internal.Group;
/*   22:     */ import org.boon.criteria.internal.Group.And;
/*   23:     */ import org.boon.criteria.internal.Group.Or;
/*   24:     */ import org.boon.criteria.internal.Not;
/*   25:     */ import org.boon.criteria.internal.Operator;
/*   26:     */ 
/*   27:     */ public class ObjectFilter
/*   28:     */ {
/*   29:     */   public static boolean matches(Object obj, Criteria... exp)
/*   30:     */   {
/*   31:  66 */     return and(exp).test(obj);
/*   32:     */   }
/*   33:     */   
/*   34:     */   public static boolean matches(Object obj, Predicate exp)
/*   35:     */   {
/*   36:  76 */     return exp.test(obj);
/*   37:     */   }
/*   38:     */   
/*   39:     */   public static boolean matches(Object obj, List<Criteria> expressions)
/*   40:     */   {
/*   41:  81 */     return and((Criteria[])expressions.toArray(new Criteria[expressions.size()])).test(obj);
/*   42:     */   }
/*   43:     */   
/*   44:     */   public static <T> List<T> filter(Collection<T> items, List<Criteria> expressions)
/*   45:     */   {
/*   46:  85 */     if (items.size() == 0) {
/*   47:  86 */       return Collections.EMPTY_LIST;
/*   48:     */     }
/*   49:  89 */     List<T> results = new ArrayList();
/*   50:  90 */     for (T item : items) {
/*   51:  91 */       if (and((Criteria[])expressions.toArray(new Criteria[expressions.size()])).test(item)) {
/*   52:  92 */         results.add(item);
/*   53:     */       }
/*   54:     */     }
/*   55:  95 */     return results;
/*   56:     */   }
/*   57:     */   
/*   58:     */   public static <T> List<T> filter(Collection<T> items, Criteria... exp)
/*   59:     */   {
/*   60:  99 */     if (items.size() == 0) {
/*   61: 100 */       return Collections.EMPTY_LIST;
/*   62:     */     }
/*   63: 103 */     Group and = and(exp);
/*   64:     */     
/*   65: 105 */     List<T> results = new ArrayList();
/*   66: 106 */     for (T item : items) {
/*   67: 107 */       if (and.test(item)) {
/*   68: 108 */         results.add(item);
/*   69:     */       }
/*   70:     */     }
/*   71: 111 */     return results;
/*   72:     */   }
/*   73:     */   
/*   74:     */   public static Not not(Criteria expression)
/*   75:     */   {
/*   76: 115 */     return new Not(expression);
/*   77:     */   }
/*   78:     */   
/*   79:     */   public static Group and(Criteria... expressions)
/*   80:     */   {
/*   81: 119 */     return new Group.And(expressions);
/*   82:     */   }
/*   83:     */   
/*   84:     */   public static Group or(Criteria... expressions)
/*   85:     */   {
/*   86: 125 */     return new Group.Or(expressions);
/*   87:     */   }
/*   88:     */   
/*   89:     */   public static Criterion eq(Object name, Object value)
/*   90:     */   {
/*   91: 129 */     new Criterion(name.toString(), Operator.EQUAL, new Object[] { value })
/*   92:     */     {
/*   93:     */       public boolean resolve(Object owner)
/*   94:     */       {
/*   95: 132 */         return Boon.equals(fieldValue(), value());
/*   96:     */       }
/*   97:     */     };
/*   98:     */   }
/*   99:     */   
/*  100:     */   public static Criterion typeOf(final String className)
/*  101:     */   {
/*  102: 139 */     new Criterion("_type", Operator.EQUAL, new Object[] { className })
/*  103:     */     {
/*  104:     */       public boolean resolve(Object owner)
/*  105:     */       {
/*  106: 143 */         Class cls = owner.getClass();
/*  107: 144 */         while (cls != Typ.object)
/*  108:     */         {
/*  109: 145 */           String simpleName = cls.getSimpleName();
/*  110: 146 */           String name = cls.getName();
/*  111: 147 */           if ((simpleName.equals(className)) || (name.equals(className))) {
/*  112: 148 */             return true;
/*  113:     */           }
/*  114: 152 */           Class[] interfaces = cls.getInterfaces();
/*  115: 153 */           for (Class<?> i : interfaces)
/*  116:     */           {
/*  117: 154 */             simpleName = i.getSimpleName();
/*  118: 155 */             name = i.getName();
/*  119: 157 */             if ((simpleName.equals(className)) || (name.equals(className))) {
/*  120: 158 */               return true;
/*  121:     */             }
/*  122:     */           }
/*  123: 161 */           cls = cls.getSuperclass();
/*  124:     */         }
/*  125: 163 */         return false;
/*  126:     */       }
/*  127:     */     };
/*  128:     */   }
/*  129:     */   
/*  130:     */   public static Criterion instanceOf(final Class<?> cls)
/*  131:     */   {
/*  132: 169 */     new Criterion("_type", Operator.EQUAL, new Object[] { cls.getName() })
/*  133:     */     {
/*  134:     */       public boolean resolve(Object owner)
/*  135:     */       {
/*  136: 172 */         return Typ.isSuperClass(owner.getClass(), cls);
/*  137:     */       }
/*  138:     */     };
/*  139:     */   }
/*  140:     */   
/*  141:     */   public static Criterion implementsInterface(final Class<?> cls)
/*  142:     */   {
/*  143: 178 */     new Criterion("_type", Operator.EQUAL, new Object[] { cls.getName() })
/*  144:     */     {
/*  145:     */       public boolean resolve(Object owner)
/*  146:     */       {
/*  147: 181 */         return Typ.implementsInterface(owner.getClass(), cls);
/*  148:     */       }
/*  149:     */     };
/*  150:     */   }
/*  151:     */   
/*  152:     */   public static Criterion notEq(Object name, Object value)
/*  153:     */   {
/*  154: 187 */     new Criterion(name.toString(), Operator.NOT_EQUAL, new Object[] { value })
/*  155:     */     {
/*  156:     */       public boolean resolve(Object owner)
/*  157:     */       {
/*  158: 190 */         return !fieldValue().equals(value());
/*  159:     */       }
/*  160:     */     };
/*  161:     */   }
/*  162:     */   
/*  163:     */   public static Criterion notIn(Object name, Object... values)
/*  164:     */   {
/*  165: 197 */     new Criterion(name.toString(), Operator.NOT_IN, values)
/*  166:     */     {
/*  167:     */       public boolean resolve(Object owner)
/*  168:     */       {
/*  169: 203 */         Object fieldValue = fieldValue();
/*  170: 204 */         if (this.value == null) {
/*  171: 205 */           return false;
/*  172:     */         }
/*  173: 207 */         return !valueSet().contains(fieldValue);
/*  174:     */       }
/*  175:     */     };
/*  176:     */   }
/*  177:     */   
/*  178:     */   public static Criterion in(Object name, Object... values)
/*  179:     */   {
/*  180: 213 */     new Criterion(name.toString(), Operator.IN, values)
/*  181:     */     {
/*  182:     */       public boolean resolve(Object owner)
/*  183:     */       {
/*  184: 219 */         Object fieldValue = fieldValue();
/*  185: 220 */         if (this.value == null) {
/*  186: 221 */           return false;
/*  187:     */         }
/*  188: 223 */         return valueSet().contains(fieldValue);
/*  189:     */       }
/*  190:     */     };
/*  191:     */   }
/*  192:     */   
/*  193:     */   public static Criterion lt(Object name, Object value)
/*  194:     */   {
/*  195: 229 */     new Criterion(name.toString(), Operator.LESS_THAN, new Object[] { value })
/*  196:     */     {
/*  197:     */       public boolean resolve(Object owner)
/*  198:     */       {
/*  199: 233 */         return ((Comparable)value()).compareTo(fieldValue()) > 0;
/*  200:     */       }
/*  201:     */     };
/*  202:     */   }
/*  203:     */   
/*  204:     */   public static Criterion lte(Object name, Object value)
/*  205:     */   {
/*  206: 239 */     new Criterion(name.toString(), Operator.LESS_THAN_EQUAL, new Object[] { value })
/*  207:     */     {
/*  208:     */       public boolean resolve(Object owner)
/*  209:     */       {
/*  210: 242 */         return ((Comparable)value()).compareTo(fieldValue()) >= 0;
/*  211:     */       }
/*  212:     */     };
/*  213:     */   }
/*  214:     */   
/*  215:     */   public static Criterion gt(Object name, Object value)
/*  216:     */   {
/*  217: 248 */     new Criterion(name.toString(), Operator.GREATER_THAN, new Object[] { value })
/*  218:     */     {
/*  219:     */       public boolean resolve(Object owner)
/*  220:     */       {
/*  221: 251 */         return ((Comparable)value()).compareTo(fieldValue()) < 0;
/*  222:     */       }
/*  223:     */     };
/*  224:     */   }
/*  225:     */   
/*  226:     */   public static Criterion gte(Object name, Object value)
/*  227:     */   {
/*  228: 257 */     new Criterion(name.toString(), Operator.GREATER_THAN_EQUAL, new Object[] { value })
/*  229:     */     {
/*  230:     */       public boolean resolve(Object owner)
/*  231:     */       {
/*  232: 260 */         return ((Comparable)value()).compareTo(fieldValue()) <= 0;
/*  233:     */       }
/*  234:     */     };
/*  235:     */   }
/*  236:     */   
/*  237:     */   public static Criterion between(Object name, Object value, Object value2)
/*  238:     */   {
/*  239: 266 */     new Criterion(name.toString(), Operator.BETWEEN, new Object[] { value, value2 })
/*  240:     */     {
/*  241:     */       public boolean resolve(Object owner)
/*  242:     */       {
/*  243: 270 */         return (((Comparable)value()).compareTo(fieldValue()) <= 0) && (((Comparable)value2()).compareTo(fieldValue()) >= 0);
/*  244:     */       }
/*  245:     */     };
/*  246:     */   }
/*  247:     */   
/*  248:     */   public static Criterion between(Class clazz, Object name, String svalue, String svalue2)
/*  249:     */   {
/*  250: 280 */     FieldAccess field = null;
/*  251: 281 */     Criterion c = null;
/*  252: 283 */     if (clazz != null) {
/*  253: 284 */       field = BeanUtils.getField(clazz, name.toString());
/*  254:     */     }
/*  255: 287 */     if (field == null)
/*  256:     */     {
/*  257: 288 */       c = between(name, svalue, svalue2);
/*  258:     */     }
/*  259:     */     else
/*  260:     */     {
/*  261: 291 */       Object o = Conversions.coerce(field.type(), svalue);
/*  262: 292 */       Object o2 = Conversions.coerce(field.type(), svalue2);
/*  263:     */       
/*  264: 294 */       c = between(name, o, o2);
/*  265:     */     }
/*  266: 300 */     return c;
/*  267:     */   }
/*  268:     */   
/*  269:     */   public static Criterion startsWith(Object name, Object value)
/*  270:     */   {
/*  271: 306 */     new Criterion(name.toString(), Operator.STARTS_WITH, new Object[] { value })
/*  272:     */     {
/*  273:     */       public boolean resolve(Object owner)
/*  274:     */       {
/*  275: 311 */         Object fieldValue = fieldValue();
/*  276: 313 */         if (fieldValue == null) {
/*  277: 314 */           return false;
/*  278:     */         }
/*  279: 317 */         return fieldValue.toString().startsWith(this.value.toString());
/*  280:     */       }
/*  281:     */     };
/*  282:     */   }
/*  283:     */   
/*  284:     */   public static Criterion endsWith(Object name, Object value)
/*  285:     */   {
/*  286: 324 */     new Criterion(name.toString(), Operator.ENDS_WITH, new Object[] { value })
/*  287:     */     {
/*  288:     */       public boolean resolve(Object owner)
/*  289:     */       {
/*  290: 329 */         Object fieldValue = fieldValue();
/*  291: 331 */         if (fieldValue == null) {
/*  292: 332 */           return false;
/*  293:     */         }
/*  294: 335 */         return fieldValue.toString().startsWith(this.value.toString());
/*  295:     */       }
/*  296:     */     };
/*  297:     */   }
/*  298:     */   
/*  299:     */   public static Criterion notContains(Object name, Object value)
/*  300:     */   {
/*  301: 341 */     return doContains(name, value, true);
/*  302:     */   }
/*  303:     */   
/*  304:     */   public static Criterion contains(Object name, Object value)
/*  305:     */   {
/*  306: 345 */     return doContains(name, value, false);
/*  307:     */   }
/*  308:     */   
/*  309:     */   private static Criterion doContains(Object name, Object value, final boolean not)
/*  310:     */   {
/*  311: 349 */     new Criterion(name.toString(), not ? Operator.NOT_CONTAINS : Operator.CONTAINS, new Object[] { value })
/*  312:     */     {
/*  313:     */       public boolean resolve(Object owner)
/*  314:     */       {
/*  315: 355 */         FieldAccess field = field();
/*  316:     */         
/*  317: 357 */         Object fieldValue = fieldValue();
/*  318: 358 */         Object value = value();
/*  319:     */         boolean returnVal;
/*  320:     */         boolean returnVal;
/*  321: 359 */         if (Typ.implementsInterface(field.type(), Typ.collection))
/*  322:     */         {
/*  323: 360 */           Collection collection = (Collection)fieldValue;
/*  324: 361 */           returnVal = collection.contains(value);
/*  325:     */         }
/*  326: 362 */         else if (field.type().isArray())
/*  327:     */         {
/*  328: 363 */           boolean returnVal = false;
/*  329: 364 */           Object array = fieldValue;
/*  330: 365 */           Iterator iter = Boon.iterator(array);
/*  331: 366 */           while (iter.hasNext())
/*  332:     */           {
/*  333: 367 */             Object i = iter.next();
/*  334: 368 */             if (i.equals(value)) {
/*  335: 369 */               returnVal = true;
/*  336:     */             }
/*  337:     */           }
/*  338:     */         }
/*  339:     */         else
/*  340:     */         {
/*  341: 373 */           returnVal = fieldValue.toString().contains(value.toString());
/*  342:     */         }
/*  343: 375 */         return not ? false : !returnVal ? true : returnVal;
/*  344:     */       }
/*  345:     */     };
/*  346:     */   }
/*  347:     */   
/*  348:     */   public static Criterion notEmpty(Object name)
/*  349:     */   {
/*  350: 381 */     return doEmpty(name, true);
/*  351:     */   }
/*  352:     */   
/*  353:     */   public static Criterion empty(Object name)
/*  354:     */   {
/*  355: 385 */     return doEmpty(name, false);
/*  356:     */   }
/*  357:     */   
/*  358:     */   private static Criterion doEmpty(Object name, final boolean not)
/*  359:     */   {
/*  360: 389 */     new Criterion(name.toString(), not ? Operator.NOT_EMPTY : Operator.IS_EMPTY, new Object[] { "" })
/*  361:     */     {
/*  362: 390 */       String sValue = (this.value instanceof String) ? (String)this.value : this.value.toString();
/*  363:     */       
/*  364:     */       public boolean resolve(Object owner)
/*  365:     */       {
/*  366: 397 */         FieldAccess field = field();
/*  367:     */         
/*  368: 399 */         Object fieldValue = fieldValue();
/*  369:     */         boolean returnVal;
/*  370:     */         boolean returnVal;
/*  371: 401 */         if (Typ.implementsInterface(field.type(), Typ.collection))
/*  372:     */         {
/*  373: 402 */           Collection collection = (Collection)fieldValue;
/*  374: 403 */           returnVal = (collection == null) || (collection.isEmpty());
/*  375:     */         }
/*  376:     */         else
/*  377:     */         {
/*  378:     */           boolean returnVal;
/*  379: 404 */           if (field.type().isArray())
/*  380:     */           {
/*  381: 405 */             Object array = fieldValue;
/*  382: 406 */             returnVal = (array == null) || (Boon.len(array) == 0);
/*  383:     */           }
/*  384:     */           else
/*  385:     */           {
/*  386: 408 */             returnVal = (fieldValue == null) || (Boon.len(fieldValue) == 0);
/*  387:     */           }
/*  388:     */         }
/*  389: 410 */         return not ? false : !returnVal ? true : returnVal;
/*  390:     */       }
/*  391:     */     };
/*  392:     */   }
/*  393:     */   
/*  394:     */   public static Criterion notNull(Object name)
/*  395:     */   {
/*  396: 416 */     return doIsNull(name, true);
/*  397:     */   }
/*  398:     */   
/*  399:     */   public static Criterion isNull(Object name)
/*  400:     */   {
/*  401: 420 */     return doIsNull(name, false);
/*  402:     */   }
/*  403:     */   
/*  404:     */   private static Criterion doIsNull(Object name, final boolean not)
/*  405:     */   {
/*  406: 424 */     new Criterion(name.toString(), not ? Operator.NOT_NULL : Operator.IS_NULL, new Object[] { "" })
/*  407:     */     {
/*  408:     */       public boolean resolve(Object owner)
/*  409:     */       {
/*  410: 430 */         Object fieldValue = fieldValue();
/*  411:     */         
/*  412: 432 */         boolean isNull = fieldValue == null;
/*  413: 433 */         return not ? false : !isNull ? true : isNull;
/*  414:     */       }
/*  415:     */     };
/*  416:     */   }
/*  417:     */   
/*  418:     */   public static Criterion eqInt(Object name, final int compareValue)
/*  419:     */   {
/*  420: 483 */     new Criterion(name.toString(), Operator.EQUAL, new Object[] { Integer.valueOf(compareValue) })
/*  421:     */     {
/*  422:     */       public boolean resolve(Object owner)
/*  423:     */       {
/*  424: 487 */         return fieldInt() == compareValue;
/*  425:     */       }
/*  426:     */     };
/*  427:     */   }
/*  428:     */   
/*  429:     */   public static Criterion notEqInt(Object name, final int compareValue)
/*  430:     */   {
/*  431: 494 */     new Criterion(name.toString(), Operator.NOT_EQUAL, new Object[] { Integer.valueOf(compareValue) })
/*  432:     */     {
/*  433:     */       public boolean resolve(Object owner)
/*  434:     */       {
/*  435: 497 */         return fieldInt() != compareValue;
/*  436:     */       }
/*  437:     */     };
/*  438:     */   }
/*  439:     */   
/*  440:     */   public static Criterion notInInts(Object name, final int... compareValues)
/*  441:     */   {
/*  442: 503 */     new Criterion(name.toString(), Operator.NOT_IN, new Object[] { compareValues })
/*  443:     */     {
/*  444:     */       public boolean resolve(Object owner)
/*  445:     */       {
/*  446: 506 */         int value = fieldInt();
/*  447: 508 */         for (int compareValue : compareValues) {
/*  448: 509 */           if (value == compareValue) {
/*  449: 510 */             return false;
/*  450:     */           }
/*  451:     */         }
/*  452: 513 */         return true;
/*  453:     */       }
/*  454:     */     };
/*  455:     */   }
/*  456:     */   
/*  457:     */   public static Criterion inInts(Object name, final int... compareValues)
/*  458:     */   {
/*  459: 519 */     new Criterion(name.toString(), Operator.IN, new Object[] { compareValues })
/*  460:     */     {
/*  461:     */       public boolean resolve(Object owner)
/*  462:     */       {
/*  463: 522 */         int value = fieldInt();
/*  464: 524 */         for (int compareValue : compareValues) {
/*  465: 525 */           if (value == compareValue) {
/*  466: 526 */             return true;
/*  467:     */           }
/*  468:     */         }
/*  469: 529 */         return false;
/*  470:     */       }
/*  471:     */     };
/*  472:     */   }
/*  473:     */   
/*  474:     */   public static Criterion ltInt(Object name, final int compareValue)
/*  475:     */   {
/*  476: 535 */     new Criterion(name.toString(), Operator.LESS_THAN, new Object[] { Integer.valueOf(compareValue) })
/*  477:     */     {
/*  478:     */       public boolean resolve(Object owner)
/*  479:     */       {
/*  480: 538 */         int value = fieldInt();
/*  481: 539 */         return value < compareValue;
/*  482:     */       }
/*  483:     */     };
/*  484:     */   }
/*  485:     */   
/*  486:     */   public static Criterion lteInt(Object name, final int compareValue)
/*  487:     */   {
/*  488: 545 */     new Criterion(name.toString(), Operator.LESS_THAN_EQUAL, new Object[] { Integer.valueOf(compareValue) })
/*  489:     */     {
/*  490:     */       public boolean resolve(Object owner)
/*  491:     */       {
/*  492: 548 */         int value = fieldInt();
/*  493: 549 */         return value <= compareValue;
/*  494:     */       }
/*  495:     */     };
/*  496:     */   }
/*  497:     */   
/*  498:     */   public static Criterion gtInt(Object name, final int compareValue)
/*  499:     */   {
/*  500: 555 */     new Criterion(name.toString(), Operator.GREATER_THAN, new Object[] { Integer.valueOf(compareValue) })
/*  501:     */     {
/*  502:     */       public boolean resolve(Object owner)
/*  503:     */       {
/*  504: 558 */         int value = fieldInt();
/*  505: 559 */         return value > compareValue;
/*  506:     */       }
/*  507:     */     };
/*  508:     */   }
/*  509:     */   
/*  510:     */   public static Criterion gteInt(Object name, final int compareValue)
/*  511:     */   {
/*  512: 565 */     new Criterion(name.toString(), Operator.GREATER_THAN_EQUAL, new Object[] { Integer.valueOf(compareValue) })
/*  513:     */     {
/*  514:     */       public boolean resolve(Object owner)
/*  515:     */       {
/*  516: 568 */         int value = fieldInt();
/*  517: 569 */         return value >= compareValue;
/*  518:     */       }
/*  519:     */     };
/*  520:     */   }
/*  521:     */   
/*  522:     */   public static Criterion betweenInt(Object name, final int start, final int stop)
/*  523:     */   {
/*  524: 575 */     new Criterion(name.toString(), Operator.BETWEEN, new Object[] { Integer.valueOf(start), Integer.valueOf(stop) })
/*  525:     */     {
/*  526:     */       public boolean resolve(Object owner)
/*  527:     */       {
/*  528: 578 */         int value = fieldInt();
/*  529: 579 */         return (value >= start) && (value <= stop);
/*  530:     */       }
/*  531:     */     };
/*  532:     */   }
/*  533:     */   
/*  534:     */   public static Criterion eqFloat(Object name, final float compareValue)
/*  535:     */   {
/*  536: 589 */     new Criterion(name.toString(), Operator.EQUAL, new Object[] { Float.valueOf(compareValue) })
/*  537:     */     {
/*  538:     */       public boolean resolve(Object owner)
/*  539:     */       {
/*  540: 592 */         float value = fieldFloat();
/*  541: 593 */         return value == compareValue;
/*  542:     */       }
/*  543:     */     };
/*  544:     */   }
/*  545:     */   
/*  546:     */   public static Criterion notEqFloat(Object name, final float compareValue)
/*  547:     */   {
/*  548: 600 */     new Criterion(name.toString(), Operator.NOT_EQUAL, new Object[] { Float.valueOf(compareValue) })
/*  549:     */     {
/*  550:     */       public boolean resolve(Object owner)
/*  551:     */       {
/*  552: 603 */         float value = fieldFloat();
/*  553: 604 */         return value != compareValue;
/*  554:     */       }
/*  555:     */     };
/*  556:     */   }
/*  557:     */   
/*  558:     */   public static Criterion notInFloats(Object name, final float... compareValues)
/*  559:     */   {
/*  560: 610 */     new Criterion(name.toString(), Operator.NOT_IN, new Object[] { compareValues })
/*  561:     */     {
/*  562:     */       public boolean resolve(Object owner)
/*  563:     */       {
/*  564: 613 */         float value = fieldFloat();
/*  565: 615 */         for (float compareValue : compareValues) {
/*  566: 616 */           if (value == compareValue) {
/*  567: 617 */             return false;
/*  568:     */           }
/*  569:     */         }
/*  570: 620 */         return true;
/*  571:     */       }
/*  572:     */     };
/*  573:     */   }
/*  574:     */   
/*  575:     */   public static Criterion inFloats(Object name, final float... compareValues)
/*  576:     */   {
/*  577: 626 */     new Criterion(name.toString(), Operator.IN, new Object[] { compareValues })
/*  578:     */     {
/*  579:     */       public boolean resolve(Object owner)
/*  580:     */       {
/*  581: 629 */         float value = fieldFloat();
/*  582: 631 */         for (float compareValue : compareValues) {
/*  583: 632 */           if (value == compareValue) {
/*  584: 633 */             return true;
/*  585:     */           }
/*  586:     */         }
/*  587: 636 */         return false;
/*  588:     */       }
/*  589:     */     };
/*  590:     */   }
/*  591:     */   
/*  592:     */   public static Criterion ltFloat(Object name, final float compareValue)
/*  593:     */   {
/*  594: 642 */     new Criterion(name.toString(), Operator.LESS_THAN, new Object[] { Float.valueOf(compareValue) })
/*  595:     */     {
/*  596:     */       public boolean resolve(Object owner)
/*  597:     */       {
/*  598: 645 */         float value = fieldFloat();
/*  599: 646 */         return value < compareValue;
/*  600:     */       }
/*  601:     */     };
/*  602:     */   }
/*  603:     */   
/*  604:     */   public static Criterion lteFloat(Object name, final float compareValue)
/*  605:     */   {
/*  606: 652 */     new Criterion(name.toString(), Operator.LESS_THAN_EQUAL, new Object[] { Float.valueOf(compareValue) })
/*  607:     */     {
/*  608:     */       public boolean resolve(Object owner)
/*  609:     */       {
/*  610: 655 */         float value = fieldFloat();
/*  611: 656 */         return value <= compareValue;
/*  612:     */       }
/*  613:     */     };
/*  614:     */   }
/*  615:     */   
/*  616:     */   public static Criterion gtFloat(Object name, final float compareValue)
/*  617:     */   {
/*  618: 662 */     new Criterion(name.toString(), Operator.GREATER_THAN, new Object[] { Float.valueOf(compareValue) })
/*  619:     */     {
/*  620:     */       public boolean resolve(Object owner)
/*  621:     */       {
/*  622: 665 */         float value = fieldFloat();
/*  623: 666 */         return value > compareValue;
/*  624:     */       }
/*  625:     */     };
/*  626:     */   }
/*  627:     */   
/*  628:     */   public static Criterion gteFloat(Object name, final float compareValue)
/*  629:     */   {
/*  630: 672 */     new Criterion(name.toString(), Operator.GREATER_THAN_EQUAL, new Object[] { Float.valueOf(compareValue) })
/*  631:     */     {
/*  632:     */       public boolean resolve(Object owner)
/*  633:     */       {
/*  634: 675 */         float value = fieldFloat();
/*  635: 676 */         return value >= compareValue;
/*  636:     */       }
/*  637:     */     };
/*  638:     */   }
/*  639:     */   
/*  640:     */   public static Criterion betweenFloat(Object name, final float start, final float stop)
/*  641:     */   {
/*  642: 682 */     new Criterion(name.toString(), Operator.BETWEEN, new Object[] { Float.valueOf(start), Float.valueOf(stop) })
/*  643:     */     {
/*  644:     */       public boolean resolve(Object owner)
/*  645:     */       {
/*  646: 685 */         float value = fieldFloat();
/*  647: 686 */         return (value >= start) && (value <= stop);
/*  648:     */       }
/*  649:     */     };
/*  650:     */   }
/*  651:     */   
/*  652:     */   public static Criterion eqBoolean(Object name, final boolean compareValue)
/*  653:     */   {
/*  654: 693 */     new Criterion(name.toString(), Operator.EQUAL, new Object[] { Boolean.valueOf(compareValue) })
/*  655:     */     {
/*  656:     */       public boolean resolve(Object owner)
/*  657:     */       {
/*  658: 696 */         boolean value = fieldBoolean();
/*  659: 697 */         return value == compareValue;
/*  660:     */       }
/*  661:     */     };
/*  662:     */   }
/*  663:     */   
/*  664:     */   public static Criterion notEqBoolean(Object name, final boolean compareValue)
/*  665:     */   {
/*  666: 704 */     new Criterion(name.toString(), Operator.NOT_EQUAL, new Object[] { Boolean.valueOf(compareValue) })
/*  667:     */     {
/*  668:     */       public boolean resolve(Object owner)
/*  669:     */       {
/*  670: 707 */         boolean value = fieldBoolean();
/*  671: 708 */         return value != compareValue;
/*  672:     */       }
/*  673:     */     };
/*  674:     */   }
/*  675:     */   
/*  676:     */   public static Criterion eqDouble(Object name, final double compareValue)
/*  677:     */   {
/*  678: 718 */     new Criterion(name.toString(), Operator.EQUAL, new Object[] { Double.valueOf(compareValue) })
/*  679:     */     {
/*  680:     */       public boolean resolve(Object owner)
/*  681:     */       {
/*  682: 721 */         double value = fieldDouble();
/*  683: 722 */         return value == compareValue;
/*  684:     */       }
/*  685:     */     };
/*  686:     */   }
/*  687:     */   
/*  688:     */   public static Criterion notEqDouble(Object name, final double compareValue)
/*  689:     */   {
/*  690: 729 */     new Criterion(name.toString(), Operator.NOT_EQUAL, new Object[] { Double.valueOf(compareValue) })
/*  691:     */     {
/*  692:     */       public boolean resolve(Object owner)
/*  693:     */       {
/*  694: 732 */         double value = fieldDouble();
/*  695: 733 */         return value != compareValue;
/*  696:     */       }
/*  697:     */     };
/*  698:     */   }
/*  699:     */   
/*  700:     */   public static Criterion notInDoubles(Object name, final double... compareValues)
/*  701:     */   {
/*  702: 739 */     new Criterion(name.toString(), Operator.NOT_IN, new Object[] { compareValues })
/*  703:     */     {
/*  704:     */       public boolean resolve(Object owner)
/*  705:     */       {
/*  706: 742 */         double value = fieldDouble();
/*  707: 744 */         for (double compareValue : compareValues) {
/*  708: 745 */           if (value == compareValue) {
/*  709: 746 */             return false;
/*  710:     */           }
/*  711:     */         }
/*  712: 749 */         return true;
/*  713:     */       }
/*  714:     */     };
/*  715:     */   }
/*  716:     */   
/*  717:     */   public static Criterion inDoubles(Object name, final double... compareValues)
/*  718:     */   {
/*  719: 755 */     new Criterion(name.toString(), Operator.IN, new Object[] { compareValues })
/*  720:     */     {
/*  721:     */       public boolean resolve(Object owner)
/*  722:     */       {
/*  723: 758 */         double value = fieldDouble();
/*  724: 760 */         for (double compareValue : compareValues) {
/*  725: 761 */           if (value == compareValue) {
/*  726: 762 */             return true;
/*  727:     */           }
/*  728:     */         }
/*  729: 765 */         return false;
/*  730:     */       }
/*  731:     */     };
/*  732:     */   }
/*  733:     */   
/*  734:     */   public static Criterion ltDouble(Object name, final double compareValue)
/*  735:     */   {
/*  736: 771 */     new Criterion(name.toString(), Operator.LESS_THAN, new Object[] { Double.valueOf(compareValue) })
/*  737:     */     {
/*  738:     */       public boolean resolve(Object owner)
/*  739:     */       {
/*  740: 774 */         double value = fieldDouble();
/*  741: 775 */         return value < compareValue;
/*  742:     */       }
/*  743:     */     };
/*  744:     */   }
/*  745:     */   
/*  746:     */   public static Criterion lteDouble(Object name, final double compareValue)
/*  747:     */   {
/*  748: 781 */     new Criterion(name.toString(), Operator.LESS_THAN_EQUAL, new Object[] { Double.valueOf(compareValue) })
/*  749:     */     {
/*  750:     */       public boolean resolve(Object owner)
/*  751:     */       {
/*  752: 784 */         double value = fieldDouble();
/*  753: 785 */         return value <= compareValue;
/*  754:     */       }
/*  755:     */     };
/*  756:     */   }
/*  757:     */   
/*  758:     */   public static Criterion gtDouble(Object name, final double compareValue)
/*  759:     */   {
/*  760: 791 */     new Criterion(name.toString(), Operator.GREATER_THAN, new Object[] { Double.valueOf(compareValue) })
/*  761:     */     {
/*  762:     */       public boolean resolve(Object owner)
/*  763:     */       {
/*  764: 794 */         double value = fieldDouble();
/*  765: 795 */         return value > compareValue;
/*  766:     */       }
/*  767:     */     };
/*  768:     */   }
/*  769:     */   
/*  770:     */   public static Criterion gteDouble(Object name, final double compareValue)
/*  771:     */   {
/*  772: 801 */     new Criterion(name.toString(), Operator.GREATER_THAN_EQUAL, new Object[] { Double.valueOf(compareValue) })
/*  773:     */     {
/*  774:     */       public boolean resolve(Object owner)
/*  775:     */       {
/*  776: 804 */         double value = fieldDouble();
/*  777: 805 */         return value >= compareValue;
/*  778:     */       }
/*  779:     */     };
/*  780:     */   }
/*  781:     */   
/*  782:     */   public static Criterion betweenDouble(Object name, final double start, double stop)
/*  783:     */   {
/*  784: 811 */     new Criterion(name.toString(), Operator.BETWEEN, new Object[] { Double.valueOf(start), Double.valueOf(stop) })
/*  785:     */     {
/*  786:     */       public boolean resolve(Object owner)
/*  787:     */       {
/*  788: 814 */         double value = fieldDouble();
/*  789: 815 */         return (value >= start) && (value <= this.val$stop);
/*  790:     */       }
/*  791:     */     };
/*  792:     */   }
/*  793:     */   
/*  794:     */   public static Criterion eqShort(Object name, final short compareValue)
/*  795:     */   {
/*  796: 825 */     new Criterion(name.toString(), Operator.EQUAL, new Object[] { Short.valueOf(compareValue) })
/*  797:     */     {
/*  798:     */       public boolean resolve(Object owner)
/*  799:     */       {
/*  800: 828 */         short value = fieldShort();
/*  801: 829 */         return value == compareValue;
/*  802:     */       }
/*  803:     */     };
/*  804:     */   }
/*  805:     */   
/*  806:     */   public static Criterion notEqShort(Object name, final short compareValue)
/*  807:     */   {
/*  808: 836 */     new Criterion(name.toString(), Operator.NOT_EQUAL, new Object[] { Short.valueOf(compareValue) })
/*  809:     */     {
/*  810:     */       public boolean resolve(Object owner)
/*  811:     */       {
/*  812: 839 */         short value = fieldShort();
/*  813: 840 */         return value != compareValue;
/*  814:     */       }
/*  815:     */     };
/*  816:     */   }
/*  817:     */   
/*  818:     */   public static Criterion notInShorts(Object name, final short... compareValues)
/*  819:     */   {
/*  820: 846 */     new Criterion(name.toString(), Operator.NOT_IN, new Object[] { compareValues })
/*  821:     */     {
/*  822:     */       public boolean resolve(Object owner)
/*  823:     */       {
/*  824: 849 */         short value = fieldShort();
/*  825: 851 */         for (short compareValue : compareValues) {
/*  826: 852 */           if (value == compareValue) {
/*  827: 853 */             return false;
/*  828:     */           }
/*  829:     */         }
/*  830: 856 */         return true;
/*  831:     */       }
/*  832:     */     };
/*  833:     */   }
/*  834:     */   
/*  835:     */   public static Criterion inShorts(Object name, final short... compareValues)
/*  836:     */   {
/*  837: 862 */     new Criterion(name.toString(), Operator.IN, new Object[] { compareValues })
/*  838:     */     {
/*  839:     */       public boolean resolve(Object owner)
/*  840:     */       {
/*  841: 865 */         short value = fieldShort();
/*  842: 867 */         for (short compareValue : compareValues) {
/*  843: 868 */           if (value == compareValue) {
/*  844: 869 */             return true;
/*  845:     */           }
/*  846:     */         }
/*  847: 872 */         return false;
/*  848:     */       }
/*  849:     */     };
/*  850:     */   }
/*  851:     */   
/*  852:     */   public static Criterion ltShort(Object name, final short compareValue)
/*  853:     */   {
/*  854: 878 */     new Criterion(name.toString(), Operator.LESS_THAN, new Object[] { Short.valueOf(compareValue) })
/*  855:     */     {
/*  856:     */       public boolean resolve(Object owner)
/*  857:     */       {
/*  858: 881 */         short value = fieldShort();
/*  859: 882 */         return value < compareValue;
/*  860:     */       }
/*  861:     */     };
/*  862:     */   }
/*  863:     */   
/*  864:     */   public static Criterion lteShort(Object name, final short compareValue)
/*  865:     */   {
/*  866: 888 */     new Criterion(name.toString(), Operator.LESS_THAN_EQUAL, new Object[] { Short.valueOf(compareValue) })
/*  867:     */     {
/*  868:     */       public boolean resolve(Object owner)
/*  869:     */       {
/*  870: 891 */         short value = fieldShort();
/*  871: 892 */         return value <= compareValue;
/*  872:     */       }
/*  873:     */     };
/*  874:     */   }
/*  875:     */   
/*  876:     */   public static Criterion gtShort(Object name, final short compareValue)
/*  877:     */   {
/*  878: 898 */     new Criterion(name.toString(), Operator.GREATER_THAN, new Object[] { Short.valueOf(compareValue) })
/*  879:     */     {
/*  880:     */       public boolean resolve(Object owner)
/*  881:     */       {
/*  882: 901 */         short value = fieldShort();
/*  883: 902 */         return value > compareValue;
/*  884:     */       }
/*  885:     */     };
/*  886:     */   }
/*  887:     */   
/*  888:     */   public static Criterion gteShort(Object name, final short compareValue)
/*  889:     */   {
/*  890: 908 */     new Criterion(name.toString(), Operator.GREATER_THAN_EQUAL, new Object[] { Short.valueOf(compareValue) })
/*  891:     */     {
/*  892:     */       public boolean resolve(Object owner)
/*  893:     */       {
/*  894: 911 */         short value = fieldShort();
/*  895: 912 */         return value >= compareValue;
/*  896:     */       }
/*  897:     */     };
/*  898:     */   }
/*  899:     */   
/*  900:     */   public static Criterion betweenShort(Object name, final short start, final short stop)
/*  901:     */   {
/*  902: 918 */     new Criterion(name.toString(), Operator.BETWEEN, new Object[] { Short.valueOf(start), Short.valueOf(stop) })
/*  903:     */     {
/*  904:     */       public boolean resolve(Object owner)
/*  905:     */       {
/*  906: 921 */         short value = fieldShort();
/*  907: 922 */         return (value >= start) && (value <= stop);
/*  908:     */       }
/*  909:     */     };
/*  910:     */   }
/*  911:     */   
/*  912:     */   public static Criterion eqByte(Object name, final byte compareValue)
/*  913:     */   {
/*  914: 932 */     new Criterion(name.toString(), Operator.EQUAL, new Object[] { Byte.valueOf(compareValue) })
/*  915:     */     {
/*  916:     */       public boolean resolve(Object owner)
/*  917:     */       {
/*  918: 935 */         byte value = fieldByte();
/*  919: 936 */         return value == compareValue;
/*  920:     */       }
/*  921:     */     };
/*  922:     */   }
/*  923:     */   
/*  924:     */   public static Criterion notEqByte(Object name, final byte compareValue)
/*  925:     */   {
/*  926: 943 */     new Criterion(name.toString(), Operator.NOT_EQUAL, new Object[] { Byte.valueOf(compareValue) })
/*  927:     */     {
/*  928:     */       public boolean resolve(Object owner)
/*  929:     */       {
/*  930: 946 */         byte value = fieldByte();
/*  931: 947 */         return value != compareValue;
/*  932:     */       }
/*  933:     */     };
/*  934:     */   }
/*  935:     */   
/*  936:     */   public static Criterion notInBytes(Object name, final byte... compareValues)
/*  937:     */   {
/*  938: 953 */     new Criterion(name.toString(), Operator.NOT_IN, new Object[] { compareValues })
/*  939:     */     {
/*  940:     */       public boolean resolve(Object owner)
/*  941:     */       {
/*  942: 956 */         byte value = fieldByte();
/*  943: 958 */         for (byte compareValue : compareValues) {
/*  944: 959 */           if (value == compareValue) {
/*  945: 960 */             return false;
/*  946:     */           }
/*  947:     */         }
/*  948: 963 */         return true;
/*  949:     */       }
/*  950:     */     };
/*  951:     */   }
/*  952:     */   
/*  953:     */   public static Criterion inBytes(Object name, final byte... compareValues)
/*  954:     */   {
/*  955: 969 */     new Criterion(name.toString(), Operator.IN, new Object[] { compareValues })
/*  956:     */     {
/*  957:     */       public boolean resolve(Object owner)
/*  958:     */       {
/*  959: 972 */         byte value = fieldByte();
/*  960: 974 */         for (byte compareValue : compareValues) {
/*  961: 975 */           if (value == compareValue) {
/*  962: 976 */             return true;
/*  963:     */           }
/*  964:     */         }
/*  965: 979 */         return false;
/*  966:     */       }
/*  967:     */     };
/*  968:     */   }
/*  969:     */   
/*  970:     */   public static Criterion ltByte(Object name, final byte compareValue)
/*  971:     */   {
/*  972: 985 */     new Criterion(name.toString(), Operator.LESS_THAN, new Object[] { Byte.valueOf(compareValue) })
/*  973:     */     {
/*  974:     */       public boolean resolve(Object owner)
/*  975:     */       {
/*  976: 988 */         byte value = fieldByte();
/*  977: 989 */         return value < compareValue;
/*  978:     */       }
/*  979:     */     };
/*  980:     */   }
/*  981:     */   
/*  982:     */   public static Criterion lteByte(Object name, final byte compareValue)
/*  983:     */   {
/*  984: 995 */     new Criterion(name.toString(), Operator.LESS_THAN_EQUAL, new Object[] { Byte.valueOf(compareValue) })
/*  985:     */     {
/*  986:     */       public boolean resolve(Object owner)
/*  987:     */       {
/*  988: 998 */         byte value = fieldByte();
/*  989: 999 */         return value <= compareValue;
/*  990:     */       }
/*  991:     */     };
/*  992:     */   }
/*  993:     */   
/*  994:     */   public static Criterion gtByte(Object name, final byte compareValue)
/*  995:     */   {
/*  996:1005 */     new Criterion(name.toString(), Operator.GREATER_THAN, new Object[] { Byte.valueOf(compareValue) })
/*  997:     */     {
/*  998:     */       public boolean resolve(Object owner)
/*  999:     */       {
/* 1000:1008 */         byte value = fieldByte();
/* 1001:1009 */         return value > compareValue;
/* 1002:     */       }
/* 1003:     */     };
/* 1004:     */   }
/* 1005:     */   
/* 1006:     */   public static Criterion gteByte(Object name, final byte compareValue)
/* 1007:     */   {
/* 1008:1015 */     new Criterion(name.toString(), Operator.GREATER_THAN_EQUAL, new Object[] { Byte.valueOf(compareValue) })
/* 1009:     */     {
/* 1010:     */       public boolean resolve(Object owner)
/* 1011:     */       {
/* 1012:1018 */         byte value = fieldByte();
/* 1013:1019 */         return value >= compareValue;
/* 1014:     */       }
/* 1015:     */     };
/* 1016:     */   }
/* 1017:     */   
/* 1018:     */   public static Criterion betweenByte(Object name, final byte start, final byte stop)
/* 1019:     */   {
/* 1020:1025 */     new Criterion(name.toString(), Operator.BETWEEN, new Object[] { Byte.valueOf(start), Byte.valueOf(stop) })
/* 1021:     */     {
/* 1022:     */       public boolean resolve(Object owner)
/* 1023:     */       {
/* 1024:1028 */         byte value = fieldByte();
/* 1025:1029 */         return (value >= start) && (value <= stop);
/* 1026:     */       }
/* 1027:     */     };
/* 1028:     */   }
/* 1029:     */   
/* 1030:     */   public static Criterion eqLong(Object name, final long compareValue)
/* 1031:     */   {
/* 1032:1039 */     new Criterion(name.toString(), Operator.EQUAL, new Object[] { Long.valueOf(compareValue) })
/* 1033:     */     {
/* 1034:     */       public boolean resolve(Object owner)
/* 1035:     */       {
/* 1036:1042 */         long value = fieldLong();
/* 1037:1043 */         return value == compareValue;
/* 1038:     */       }
/* 1039:     */     };
/* 1040:     */   }
/* 1041:     */   
/* 1042:     */   public static Criterion notEqLong(Object name, final long compareValue)
/* 1043:     */   {
/* 1044:1050 */     new Criterion(name.toString(), Operator.NOT_EQUAL, new Object[] { Long.valueOf(compareValue) })
/* 1045:     */     {
/* 1046:     */       public boolean resolve(Object owner)
/* 1047:     */       {
/* 1048:1053 */         long value = fieldLong();
/* 1049:1054 */         return value != compareValue;
/* 1050:     */       }
/* 1051:     */     };
/* 1052:     */   }
/* 1053:     */   
/* 1054:     */   public static Criterion notInLongs(Object name, final long... compareValues)
/* 1055:     */   {
/* 1056:1060 */     new Criterion(name.toString(), Operator.NOT_IN, new Object[] { compareValues })
/* 1057:     */     {
/* 1058:     */       public boolean resolve(Object owner)
/* 1059:     */       {
/* 1060:1063 */         long value = fieldLong();
/* 1061:1065 */         for (long compareValue : compareValues) {
/* 1062:1066 */           if (value == compareValue) {
/* 1063:1067 */             return false;
/* 1064:     */           }
/* 1065:     */         }
/* 1066:1070 */         return true;
/* 1067:     */       }
/* 1068:     */     };
/* 1069:     */   }
/* 1070:     */   
/* 1071:     */   public static Criterion inLongs(Object name, final long... compareValues)
/* 1072:     */   {
/* 1073:1076 */     new Criterion(name.toString(), Operator.IN, new Object[] { compareValues })
/* 1074:     */     {
/* 1075:     */       public boolean resolve(Object owner)
/* 1076:     */       {
/* 1077:1079 */         long value = fieldLong();
/* 1078:1081 */         for (long compareValue : compareValues) {
/* 1079:1082 */           if (value == compareValue) {
/* 1080:1083 */             return true;
/* 1081:     */           }
/* 1082:     */         }
/* 1083:1086 */         return false;
/* 1084:     */       }
/* 1085:     */     };
/* 1086:     */   }
/* 1087:     */   
/* 1088:     */   public static Criterion ltLong(Object name, final long compareValue)
/* 1089:     */   {
/* 1090:1111 */     new Criterion(name.toString(), Operator.LESS_THAN, new Object[] { Long.valueOf(compareValue) })
/* 1091:     */     {
/* 1092:     */       public boolean resolve(Object owner)
/* 1093:     */       {
/* 1094:1114 */         long value = fieldLong();
/* 1095:1115 */         return value < compareValue;
/* 1096:     */       }
/* 1097:     */     };
/* 1098:     */   }
/* 1099:     */   
/* 1100:     */   public static Criterion lteLong(Object name, final long compareValue)
/* 1101:     */   {
/* 1102:1121 */     new Criterion(name.toString(), Operator.LESS_THAN_EQUAL, new Object[] { Long.valueOf(compareValue) })
/* 1103:     */     {
/* 1104:     */       public boolean resolve(Object owner)
/* 1105:     */       {
/* 1106:1124 */         long value = fieldLong();
/* 1107:1125 */         return value <= compareValue;
/* 1108:     */       }
/* 1109:     */     };
/* 1110:     */   }
/* 1111:     */   
/* 1112:     */   public static Criterion gtLong(Object name, final long compareValue)
/* 1113:     */   {
/* 1114:1131 */     new Criterion(name.toString(), Operator.GREATER_THAN, new Object[] { Long.valueOf(compareValue) })
/* 1115:     */     {
/* 1116:     */       public boolean resolve(Object owner)
/* 1117:     */       {
/* 1118:1134 */         long value = fieldLong();
/* 1119:1135 */         return value > compareValue;
/* 1120:     */       }
/* 1121:     */     };
/* 1122:     */   }
/* 1123:     */   
/* 1124:     */   public static Criterion gteLong(Object name, final long compareValue)
/* 1125:     */   {
/* 1126:1141 */     new Criterion(name.toString(), Operator.GREATER_THAN_EQUAL, new Object[] { Long.valueOf(compareValue) })
/* 1127:     */     {
/* 1128:     */       public boolean resolve(Object owner)
/* 1129:     */       {
/* 1130:1144 */         long value = fieldLong();
/* 1131:1145 */         return value >= compareValue;
/* 1132:     */       }
/* 1133:     */     };
/* 1134:     */   }
/* 1135:     */   
/* 1136:     */   public static Criterion betweenLong(Object name, final long start, long stop)
/* 1137:     */   {
/* 1138:1151 */     new Criterion(name.toString(), Operator.BETWEEN, new Object[] { Long.valueOf(start), Long.valueOf(stop) })
/* 1139:     */     {
/* 1140:     */       public boolean resolve(Object owner)
/* 1141:     */       {
/* 1142:1154 */         long value = fieldLong();
/* 1143:1155 */         return (value >= start) && (value <= this.val$stop);
/* 1144:     */       }
/* 1145:     */     };
/* 1146:     */   }
/* 1147:     */   
/* 1148:     */   public static Criterion eqChar(Object name, final char compareValue)
/* 1149:     */   {
/* 1150:1165 */     new Criterion(name.toString(), Operator.EQUAL, new Object[] { Character.valueOf(compareValue) })
/* 1151:     */     {
/* 1152:     */       public boolean resolve(Object owner)
/* 1153:     */       {
/* 1154:1168 */         char value = fieldChar();
/* 1155:1169 */         return value == compareValue;
/* 1156:     */       }
/* 1157:     */     };
/* 1158:     */   }
/* 1159:     */   
/* 1160:     */   public static Criterion notEqChar(Object name, final char compareValue)
/* 1161:     */   {
/* 1162:1176 */     new Criterion(name.toString(), Operator.NOT_EQUAL, new Object[] { Character.valueOf(compareValue) })
/* 1163:     */     {
/* 1164:     */       public boolean resolve(Object owner)
/* 1165:     */       {
/* 1166:1179 */         char value = fieldChar();
/* 1167:1180 */         return value != compareValue;
/* 1168:     */       }
/* 1169:     */     };
/* 1170:     */   }
/* 1171:     */   
/* 1172:     */   public static Criterion notInChars(Object name, final char... compareValues)
/* 1173:     */   {
/* 1174:1186 */     new Criterion(name.toString(), Operator.NOT_IN, new Object[] { compareValues })
/* 1175:     */     {
/* 1176:     */       public boolean resolve(Object owner)
/* 1177:     */       {
/* 1178:1189 */         char value = fieldChar();
/* 1179:1191 */         for (char compareValue : compareValues) {
/* 1180:1192 */           if (value == compareValue) {
/* 1181:1193 */             return false;
/* 1182:     */           }
/* 1183:     */         }
/* 1184:1196 */         return true;
/* 1185:     */       }
/* 1186:     */     };
/* 1187:     */   }
/* 1188:     */   
/* 1189:     */   public static Criterion inChars(Object name, final char... compareValues)
/* 1190:     */   {
/* 1191:1202 */     new Criterion(name.toString(), Operator.IN, new Object[] { compareValues })
/* 1192:     */     {
/* 1193:     */       public boolean resolve(Object owner)
/* 1194:     */       {
/* 1195:1205 */         char value = fieldChar();
/* 1196:1207 */         for (char compareValue : compareValues) {
/* 1197:1208 */           if (value == compareValue) {
/* 1198:1209 */             return true;
/* 1199:     */           }
/* 1200:     */         }
/* 1201:1212 */         return false;
/* 1202:     */       }
/* 1203:     */     };
/* 1204:     */   }
/* 1205:     */   
/* 1206:     */   public static Criterion ltChar(Object name, final char compareValue)
/* 1207:     */   {
/* 1208:1218 */     new Criterion(name.toString(), Operator.LESS_THAN, new Object[] { Character.valueOf(compareValue) })
/* 1209:     */     {
/* 1210:     */       public boolean resolve(Object owner)
/* 1211:     */       {
/* 1212:1221 */         char value = fieldChar();
/* 1213:1222 */         return value < compareValue;
/* 1214:     */       }
/* 1215:     */     };
/* 1216:     */   }
/* 1217:     */   
/* 1218:     */   public static Criterion lteChar(Object name, final char compareValue)
/* 1219:     */   {
/* 1220:1228 */     new Criterion(name.toString(), Operator.LESS_THAN_EQUAL, new Object[] { Character.valueOf(compareValue) })
/* 1221:     */     {
/* 1222:     */       public boolean resolve(Object owner)
/* 1223:     */       {
/* 1224:1231 */         char value = fieldChar();
/* 1225:1232 */         return value <= compareValue;
/* 1226:     */       }
/* 1227:     */     };
/* 1228:     */   }
/* 1229:     */   
/* 1230:     */   public static Criterion gtChar(Object name, final char compareValue)
/* 1231:     */   {
/* 1232:1238 */     new Criterion(name.toString(), Operator.GREATER_THAN, new Object[] { Character.valueOf(compareValue) })
/* 1233:     */     {
/* 1234:     */       public boolean resolve(Object owner)
/* 1235:     */       {
/* 1236:1241 */         char value = fieldChar();
/* 1237:1242 */         return value > compareValue;
/* 1238:     */       }
/* 1239:     */     };
/* 1240:     */   }
/* 1241:     */   
/* 1242:     */   public static Criterion gteChar(Object name, final char compareValue)
/* 1243:     */   {
/* 1244:1248 */     new Criterion(name.toString(), Operator.GREATER_THAN_EQUAL, new Object[] { Character.valueOf(compareValue) })
/* 1245:     */     {
/* 1246:     */       public boolean resolve(Object owner)
/* 1247:     */       {
/* 1248:1251 */         char value = fieldChar();
/* 1249:1252 */         return value >= compareValue;
/* 1250:     */       }
/* 1251:     */     };
/* 1252:     */   }
/* 1253:     */   
/* 1254:     */   public static Criterion betweenChar(Object name, final char start, final char stop)
/* 1255:     */   {
/* 1256:1258 */     new Criterion(name.toString(), Operator.BETWEEN, new Object[] { Character.valueOf(start), Character.valueOf(stop) })
/* 1257:     */     {
/* 1258:     */       public boolean resolve(Object owner)
/* 1259:     */       {
/* 1260:1261 */         char value = fieldChar();
/* 1261:1262 */         return (value >= start) && (value <= stop);
/* 1262:     */       }
/* 1263:     */     };
/* 1264:     */   }
/* 1265:     */   
/* 1266:     */   public static Criteria createCriteria(String name, Operator operator, TypeType type, Class<Object> classType, List<?> values)
/* 1267:     */   {
/* 1268:1277 */     Ok.okOrDie("Values must be passed", values);
/* 1269:     */     
/* 1270:1279 */     Object value = values.get(0);
/* 1271:1281 */     switch (83.$SwitchMap$org$boon$criteria$internal$Operator[operator.ordinal()])
/* 1272:     */     {
/* 1273:     */     case 1: 
/* 1274:1283 */       switch (83.$SwitchMap$org$boon$core$TypeType[type.ordinal()])
/* 1275:     */       {
/* 1276:     */       case 1: 
/* 1277:1285 */         return eqChar(name, Conversions.toChar(value));
/* 1278:     */       case 2: 
/* 1279:1288 */         return eqByte(name, Conversions.toByte(value));
/* 1280:     */       case 3: 
/* 1281:1290 */         return eqBoolean(name, Conversions.toBoolean(value));
/* 1282:     */       case 4: 
/* 1283:1292 */         return eqInt(name, Conversions.toInt(value));
/* 1284:     */       case 5: 
/* 1285:1294 */         return eqFloat(name, Conversions.toFloat(value));
/* 1286:     */       case 6: 
/* 1287:1296 */         return eqShort(name, Conversions.toShort(value));
/* 1288:     */       case 7: 
/* 1289:1298 */         return eqDouble(name, Conversions.toDouble(value));
/* 1290:     */       case 8: 
/* 1291:1300 */         return eqLong(name, Conversions.toLong(value));
/* 1292:     */       }
/* 1293:1303 */       return eq(name, Conversions.coerce(classType, value));
/* 1294:     */     case 2: 
/* 1295:1306 */       switch (83.$SwitchMap$org$boon$core$TypeType[type.ordinal()])
/* 1296:     */       {
/* 1297:     */       case 1: 
/* 1298:1308 */         return notEqChar(name, Conversions.toChar(value));
/* 1299:     */       case 2: 
/* 1300:1310 */         return notEqByte(name, Conversions.toByte(value));
/* 1301:     */       case 3: 
/* 1302:1312 */         return notEqBoolean(name, Conversions.toBoolean(value));
/* 1303:     */       case 4: 
/* 1304:1314 */         return notEqInt(name, Conversions.toInt(value));
/* 1305:     */       case 5: 
/* 1306:1316 */         return notEqFloat(name, Conversions.toFloat(value));
/* 1307:     */       case 6: 
/* 1308:1318 */         return notEqShort(name, Conversions.toShort(value));
/* 1309:     */       case 7: 
/* 1310:1320 */         return notEqDouble(name, Conversions.toDouble(value));
/* 1311:     */       case 8: 
/* 1312:1322 */         return notEqLong(name, Conversions.toLong(value));
/* 1313:     */       }
/* 1314:1325 */       return notEq(name, Conversions.coerce(classType, value));
/* 1315:     */     case 3: 
/* 1316:1328 */       switch (83.$SwitchMap$org$boon$core$TypeType[type.ordinal()])
/* 1317:     */       {
/* 1318:     */       case 1: 
/* 1319:1330 */         return gtChar(name, Conversions.toChar(value));
/* 1320:     */       case 2: 
/* 1321:1332 */         return gtByte(name, Conversions.toByte(value));
/* 1322:     */       case 4: 
/* 1323:1334 */         return gtInt(name, Conversions.toInt(value));
/* 1324:     */       case 5: 
/* 1325:1336 */         return gtFloat(name, Conversions.toFloat(value));
/* 1326:     */       case 6: 
/* 1327:1338 */         return gtShort(name, Conversions.toShort(value));
/* 1328:     */       case 7: 
/* 1329:1340 */         return gtDouble(name, Conversions.toDouble(value));
/* 1330:     */       case 8: 
/* 1331:1342 */         return gtLong(name, Conversions.toLong(value));
/* 1332:     */       }
/* 1333:1345 */       return gt(name, Conversions.coerce(classType, value));
/* 1334:     */     case 4: 
/* 1335:1348 */       switch (83.$SwitchMap$org$boon$core$TypeType[type.ordinal()])
/* 1336:     */       {
/* 1337:     */       case 1: 
/* 1338:1350 */         return ltChar(name, Conversions.toChar(value));
/* 1339:     */       case 2: 
/* 1340:1352 */         return ltByte(name, Conversions.toByte(value));
/* 1341:     */       case 4: 
/* 1342:1354 */         return ltInt(name, Conversions.toInt(value));
/* 1343:     */       case 5: 
/* 1344:1356 */         return ltFloat(name, Conversions.toFloat(value));
/* 1345:     */       case 6: 
/* 1346:1358 */         return ltShort(name, Conversions.toShort(value));
/* 1347:     */       case 7: 
/* 1348:1360 */         return ltDouble(name, Conversions.toDouble(value));
/* 1349:     */       case 8: 
/* 1350:1362 */         return ltLong(name, Conversions.toLong(value));
/* 1351:     */       }
/* 1352:1365 */       return lt(name, Conversions.coerce(classType, value));
/* 1353:     */     case 5: 
/* 1354:1368 */       switch (83.$SwitchMap$org$boon$core$TypeType[type.ordinal()])
/* 1355:     */       {
/* 1356:     */       case 1: 
/* 1357:1370 */         return gteChar(name, Conversions.toChar(value));
/* 1358:     */       case 2: 
/* 1359:1372 */         return gteByte(name, Conversions.toByte(value));
/* 1360:     */       case 4: 
/* 1361:1374 */         return gteInt(name, Conversions.toInt(value));
/* 1362:     */       case 5: 
/* 1363:1376 */         return gteFloat(name, Conversions.toFloat(value));
/* 1364:     */       case 6: 
/* 1365:1378 */         return gteShort(name, Conversions.toShort(value));
/* 1366:     */       case 7: 
/* 1367:1380 */         return gteDouble(name, Conversions.toDouble(value));
/* 1368:     */       case 8: 
/* 1369:1382 */         return gteLong(name, Conversions.toLong(value));
/* 1370:     */       }
/* 1371:1385 */       return gte(name, Conversions.coerce(classType, value));
/* 1372:     */     case 6: 
/* 1373:1388 */       switch (83.$SwitchMap$org$boon$core$TypeType[type.ordinal()])
/* 1374:     */       {
/* 1375:     */       case 1: 
/* 1376:1390 */         return lteChar(name, Conversions.toChar(value));
/* 1377:     */       case 2: 
/* 1378:1392 */         return lteByte(name, Conversions.toByte(value));
/* 1379:     */       case 4: 
/* 1380:1394 */         return lteInt(name, Conversions.toInt(value));
/* 1381:     */       case 5: 
/* 1382:1396 */         return lteFloat(name, Conversions.toFloat(value));
/* 1383:     */       case 6: 
/* 1384:1398 */         return lteShort(name, Conversions.toShort(value));
/* 1385:     */       case 7: 
/* 1386:1400 */         return lteDouble(name, Conversions.toDouble(value));
/* 1387:     */       case 8: 
/* 1388:1402 */         return lteLong(name, Conversions.toLong(value));
/* 1389:     */       }
/* 1390:1405 */       return lte(name, Conversions.coerce(classType, value));
/* 1391:     */     case 7: 
/* 1392:1410 */       Ok.okOrDie("Values must be at least 2 in size", values.size() > 1);
/* 1393:1411 */       Object value2 = values.get(1);
/* 1394:1412 */       switch (83.$SwitchMap$org$boon$core$TypeType[type.ordinal()])
/* 1395:     */       {
/* 1396:     */       case 1: 
/* 1397:1414 */         return betweenChar(name, Conversions.toChar(value), Conversions.toChar(value2));
/* 1398:     */       case 2: 
/* 1399:1417 */         return betweenByte(name, Conversions.toByte(value), Conversions.toByte(value2));
/* 1400:     */       case 4: 
/* 1401:1420 */         return betweenInt(name, Conversions.toInt(value), Conversions.toInt(value2));
/* 1402:     */       case 5: 
/* 1403:1423 */         return betweenFloat(name, Conversions.toFloat(value), Conversions.toFloat(value2));
/* 1404:     */       case 6: 
/* 1405:1426 */         return betweenShort(name, Conversions.toShort(value), Conversions.toShort(value2));
/* 1406:     */       case 7: 
/* 1407:1429 */         return betweenDouble(name, Conversions.toDouble(value), Conversions.toDouble(value2));
/* 1408:     */       case 8: 
/* 1409:1432 */         return betweenLong(name, Conversions.toLong(value), Conversions.toLong(value2));
/* 1410:     */       }
/* 1411:1436 */       return between(name, Conversions.coerce(classType, value), Conversions.coerce(classType, value2));
/* 1412:     */     case 8: 
/* 1413:1440 */       switch (83.$SwitchMap$org$boon$core$TypeType[type.ordinal()])
/* 1414:     */       {
/* 1415:     */       case 1: 
/* 1416:1442 */         return inChars(name, Conversions.carray(values));
/* 1417:     */       case 2: 
/* 1418:1444 */         return inBytes(name, Conversions.barray(values));
/* 1419:     */       case 4: 
/* 1420:1446 */         return inInts(name, Conversions.iarray(values));
/* 1421:     */       case 5: 
/* 1422:1448 */         return inFloats(name, Conversions.farray(values));
/* 1423:     */       case 6: 
/* 1424:1450 */         return inShorts(name, Conversions.sarray(values));
/* 1425:     */       case 7: 
/* 1426:1452 */         return inDoubles(name, Conversions.darray(values));
/* 1427:     */       case 8: 
/* 1428:1454 */         return inLongs(name, Conversions.larray(values));
/* 1429:     */       }
/* 1430:1457 */       return in(name, Conversions.toArray(classType, values));
/* 1431:     */     case 9: 
/* 1432:1461 */       switch (83.$SwitchMap$org$boon$core$TypeType[type.ordinal()])
/* 1433:     */       {
/* 1434:     */       case 1: 
/* 1435:1463 */         return notInChars(name, Conversions.carray(values));
/* 1436:     */       case 2: 
/* 1437:1465 */         return notInBytes(name, Conversions.barray(values));
/* 1438:     */       case 4: 
/* 1439:1467 */         return notInInts(name, Conversions.iarray(values));
/* 1440:     */       case 5: 
/* 1441:1469 */         return notInFloats(name, Conversions.farray(values));
/* 1442:     */       case 6: 
/* 1443:1471 */         return notInShorts(name, Conversions.sarray(values));
/* 1444:     */       case 7: 
/* 1445:1473 */         return notInDoubles(name, Conversions.darray(values));
/* 1446:     */       case 8: 
/* 1447:1475 */         return notInLongs(name, Conversions.larray(values));
/* 1448:     */       }
/* 1449:1478 */       return notIn(name, Conversions.toArray(classType, values));
/* 1450:     */     case 10: 
/* 1451:1482 */       return contains(name, Conversions.coerce(classType, value));
/* 1452:     */     case 11: 
/* 1453:1485 */       return notContains(name, Conversions.coerce(classType, value));
/* 1454:     */     case 12: 
/* 1455:1489 */       return startsWith(name, value);
/* 1456:     */     case 13: 
/* 1457:1492 */       return endsWith(name, value);
/* 1458:     */     case 14: 
/* 1459:1495 */       return notEmpty(name);
/* 1460:     */     case 15: 
/* 1461:1498 */       return empty(name);
/* 1462:     */     }
/* 1463:1506 */     return (Criteria)Exceptions.die(Criteria.class, new Object[] { "Not Found", name, operator, type, values });
/* 1464:     */   }
/* 1465:     */   
/* 1466:     */   public static Criteria createCriteriaFromClass(String name, Class<?> cls, Operator operator, List<?> values)
/* 1467:     */   {
/* 1468:1518 */     if (operator == Operator.AND) {
/* 1469:1519 */       return new Group.And(cls, values);
/* 1470:     */     }
/* 1471:1520 */     if (operator == Operator.OR) {
/* 1472:1522 */       return new Group.Or(cls, values);
/* 1473:     */     }
/* 1474:1524 */     FieldAccess fieldAccess = BeanUtils.idxField(cls, name);
/* 1475:1525 */     return createCriteria(name, operator, fieldAccess.typeEnum(), fieldAccess.type(), values);
/* 1476:     */   }
/* 1477:     */   
/* 1478:     */   public static Criteria criteriaFromList(List<?> list)
/* 1479:     */   {
/* 1480:1537 */     List<Object> args = new ArrayList(list);
/* 1481:     */     
/* 1482:1539 */     Object o = Lists.atIndex(args, -1);
/* 1483:1540 */     if (!(o instanceof List)) {
/* 1484:1541 */       Lists.atIndex(args, -1, Collections.singletonList(o));
/* 1485:     */     }
/* 1486:1545 */     return (Criteria)Invoker.invokeFromList(ObjectFilter.class, "createCriteriaFromClass", args);
/* 1487:     */   }
/* 1488:     */   
/* 1489:     */   public static Criteria criteriaFromJson(String json)
/* 1490:     */   {
/* 1491:1557 */     return (Criteria)Invoker.invokeFromObject(ObjectFilter.class, "createCriteriaFromClass", Boon.fromJson(json));
/* 1492:     */   }
/* 1493:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.criteria.ObjectFilter
 * JD-Core Version:    0.7.0.1
 */