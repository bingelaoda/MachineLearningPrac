/*    1:     */ package org.boon.core.reflection;
/*    2:     */ 
/*    3:     */ import java.lang.reflect.Array;
/*    4:     */ import java.lang.reflect.Field;
/*    5:     */ import java.lang.reflect.ParameterizedType;
/*    6:     */ import java.util.ArrayList;
/*    7:     */ import java.util.Collection;
/*    8:     */ import java.util.Collections;
/*    9:     */ import java.util.Iterator;
/*   10:     */ import java.util.LinkedHashMap;
/*   11:     */ import java.util.List;
/*   12:     */ import java.util.Map;
/*   13:     */ import java.util.Map.Entry;
/*   14:     */ import java.util.Set;
/*   15:     */ import java.util.concurrent.ConcurrentHashMap;
/*   16:     */ import org.boon.Boon;
/*   17:     */ import org.boon.Exceptions;
/*   18:     */ import org.boon.Lists;
/*   19:     */ import org.boon.Sets;
/*   20:     */ import org.boon.Str;
/*   21:     */ import org.boon.StringScanner;
/*   22:     */ import org.boon.core.Conversions;
/*   23:     */ import org.boon.core.Typ;
/*   24:     */ import org.boon.core.TypeType;
/*   25:     */ import org.boon.core.Value;
/*   26:     */ import org.boon.core.reflection.fields.FieldAccess;
/*   27:     */ import org.boon.primitive.CharBuf;
/*   28:     */ import org.boon.primitive.CharScanner;
/*   29:     */ 
/*   30:     */ public class BeanUtils
/*   31:     */ {
/*   32:     */   public static Object getPropByPath(Object item, String... path)
/*   33:     */   {
/*   34:  66 */     Object o = item;
/*   35:  67 */     for (int index = 0; index < path.length; index++)
/*   36:     */     {
/*   37:  68 */       String propName = path[index];
/*   38:  69 */       if (o == null) {
/*   39:  70 */         return null;
/*   40:     */       }
/*   41:  71 */       if ((o.getClass().isArray()) || ((o instanceof Collection)))
/*   42:     */       {
/*   43:  74 */         o = getCollectionProp(o, propName, index, path);
/*   44:  75 */         break;
/*   45:     */       }
/*   46:  77 */       o = getProp(o, propName);
/*   47:     */     }
/*   48:  81 */     return Conversions.unifyListOrArray(o);
/*   49:     */   }
/*   50:     */   
/*   51:     */   private static Map<String, FieldAccess> getPropertyFieldAccessMap(Class<?> clazz)
/*   52:     */   {
/*   53:  92 */     return Reflection.getPropertyFieldAccessMapFieldFirst(clazz);
/*   54:     */   }
/*   55:     */   
/*   56:     */   public static FieldAccess getField(Class clazz, String name)
/*   57:     */   {
/*   58:  98 */     Map<String, FieldAccess> fields = getPropertyFieldAccessMap(clazz);
/*   59:  99 */     if (fields != null) {
/*   60: 100 */       return (FieldAccess)fields.get(name);
/*   61:     */     }
/*   62: 102 */     return null;
/*   63:     */   }
/*   64:     */   
/*   65:     */   public static FieldAccess getField(Object object, String name)
/*   66:     */   {
/*   67: 109 */     Map<String, FieldAccess> fields = getFieldsFromObject(object);
/*   68: 110 */     if (fields != null) {
/*   69: 111 */       return (FieldAccess)fields.get(name);
/*   70:     */     }
/*   71: 113 */     return null;
/*   72:     */   }
/*   73:     */   
/*   74:     */   public static Map<String, FieldAccess> getFieldsFromObject(Class<?> cls)
/*   75:     */   {
/*   76: 118 */     return getPropertyFieldAccessMap(cls);
/*   77:     */   }
/*   78:     */   
/*   79:     */   public static Map<String, FieldAccess> getFieldsFromObject(Object object)
/*   80:     */   {
/*   81:     */     try
/*   82:     */     {
/*   83:     */       Map<String, FieldAccess> fields;
/*   84: 133 */       if ((object instanceof Map)) {
/*   85: 135 */         fields = getFieldsFromMap((Map)object);
/*   86:     */       }
/*   87: 137 */       return getPropertyFieldAccessMap(object.getClass());
/*   88:     */     }
/*   89:     */     catch (Exception ex)
/*   90:     */     {
/*   91: 144 */       Exceptions.requireNonNull(object, "Item cannot be null");
/*   92: 145 */       return (Map)Exceptions.handle(Map.class, ex, new Object[] { "Unable to get fields from object", Boon.className(object) });
/*   93:     */     }
/*   94:     */   }
/*   95:     */   
/*   96:     */   private static Map<String, FieldAccess> getFieldsFromMap(Map<String, Object> map)
/*   97:     */   {
/*   98: 160 */     new Map()
/*   99:     */     {
/*  100:     */       public int size()
/*  101:     */       {
/*  102: 163 */         return this.val$map.size();
/*  103:     */       }
/*  104:     */       
/*  105:     */       public boolean isEmpty()
/*  106:     */       {
/*  107: 168 */         return this.val$map.isEmpty();
/*  108:     */       }
/*  109:     */       
/*  110:     */       public boolean containsKey(Object key)
/*  111:     */       {
/*  112: 173 */         return this.val$map.containsKey(key);
/*  113:     */       }
/*  114:     */       
/*  115:     */       public boolean containsValue(Object value)
/*  116:     */       {
/*  117: 178 */         return true;
/*  118:     */       }
/*  119:     */       
/*  120:     */       public FieldAccess get(final Object key)
/*  121:     */       {
/*  122: 183 */         new FieldAccess()
/*  123:     */         {
/*  124:     */           public boolean injectable()
/*  125:     */           {
/*  126: 186 */             return false;
/*  127:     */           }
/*  128:     */           
/*  129:     */           public boolean requiresInjection()
/*  130:     */           {
/*  131: 191 */             return false;
/*  132:     */           }
/*  133:     */           
/*  134:     */           public boolean isNamed()
/*  135:     */           {
/*  136: 196 */             return false;
/*  137:     */           }
/*  138:     */           
/*  139:     */           public boolean hasAlias()
/*  140:     */           {
/*  141: 201 */             return false;
/*  142:     */           }
/*  143:     */           
/*  144:     */           public String alias()
/*  145:     */           {
/*  146: 206 */             return null;
/*  147:     */           }
/*  148:     */           
/*  149:     */           public String named()
/*  150:     */           {
/*  151: 211 */             return key.toString();
/*  152:     */           }
/*  153:     */           
/*  154:     */           public String name()
/*  155:     */           {
/*  156: 216 */             return key.toString();
/*  157:     */           }
/*  158:     */           
/*  159:     */           public Object getValue(Object obj)
/*  160:     */           {
/*  161: 221 */             return BeanUtils.1.this.val$map.get(key);
/*  162:     */           }
/*  163:     */           
/*  164:     */           public void setValue(Object obj, Object value)
/*  165:     */           {
/*  166: 226 */             BeanUtils.1.this.val$map.put(key.toString(), value);
/*  167:     */           }
/*  168:     */           
/*  169:     */           public void setFromValue(Object obj, Value value)
/*  170:     */           {
/*  171: 231 */             BeanUtils.1.this.val$map.put(key.toString(), value.toValue());
/*  172:     */           }
/*  173:     */           
/*  174:     */           public boolean getBoolean(Object obj)
/*  175:     */           {
/*  176: 236 */             return Conversions.toBoolean(getValue(key));
/*  177:     */           }
/*  178:     */           
/*  179:     */           public void setBoolean(Object obj, boolean value)
/*  180:     */           {
/*  181: 242 */             setValue(BeanUtils.1.this.val$map, Boolean.valueOf(value));
/*  182:     */           }
/*  183:     */           
/*  184:     */           public int getInt(Object obj)
/*  185:     */           {
/*  186: 249 */             return Conversions.toInt(getValue(key));
/*  187:     */           }
/*  188:     */           
/*  189:     */           public void setInt(Object obj, int value)
/*  190:     */           {
/*  191: 255 */             setValue(BeanUtils.1.this.val$map, Integer.valueOf(value));
/*  192:     */           }
/*  193:     */           
/*  194:     */           public short getShort(Object obj)
/*  195:     */           {
/*  196: 261 */             return Conversions.toShort(getValue(key));
/*  197:     */           }
/*  198:     */           
/*  199:     */           public void setShort(Object obj, short value)
/*  200:     */           {
/*  201: 267 */             setValue(BeanUtils.1.this.val$map, Short.valueOf(value));
/*  202:     */           }
/*  203:     */           
/*  204:     */           public char getChar(Object obj)
/*  205:     */           {
/*  206: 274 */             return Conversions.toChar(getValue(key));
/*  207:     */           }
/*  208:     */           
/*  209:     */           public void setChar(Object obj, char value)
/*  210:     */           {
/*  211: 280 */             setValue(BeanUtils.1.this.val$map, Character.valueOf(value));
/*  212:     */           }
/*  213:     */           
/*  214:     */           public long getLong(Object obj)
/*  215:     */           {
/*  216: 287 */             return Conversions.toChar(getValue(key));
/*  217:     */           }
/*  218:     */           
/*  219:     */           public void setLong(Object obj, long value)
/*  220:     */           {
/*  221: 293 */             setValue(BeanUtils.1.this.val$map, Long.valueOf(value));
/*  222:     */           }
/*  223:     */           
/*  224:     */           public double getDouble(Object obj)
/*  225:     */           {
/*  226: 299 */             return Conversions.toDouble(getValue(key));
/*  227:     */           }
/*  228:     */           
/*  229:     */           public void setDouble(Object obj, double value)
/*  230:     */           {
/*  231: 306 */             setValue(BeanUtils.1.this.val$map, Double.valueOf(value));
/*  232:     */           }
/*  233:     */           
/*  234:     */           public float getFloat(Object obj)
/*  235:     */           {
/*  236: 313 */             return Conversions.toFloat(getValue(key));
/*  237:     */           }
/*  238:     */           
/*  239:     */           public void setFloat(Object obj, float value)
/*  240:     */           {
/*  241: 319 */             setValue(BeanUtils.1.this.val$map, Float.valueOf(value));
/*  242:     */           }
/*  243:     */           
/*  244:     */           public byte getByte(Object obj)
/*  245:     */           {
/*  246: 326 */             return Conversions.toByte(getValue(key));
/*  247:     */           }
/*  248:     */           
/*  249:     */           public void setByte(Object obj, byte value)
/*  250:     */           {
/*  251: 332 */             setValue(BeanUtils.1.this.val$map, Byte.valueOf(value));
/*  252:     */           }
/*  253:     */           
/*  254:     */           public Object getObject(Object obj)
/*  255:     */           {
/*  256: 339 */             return getValue(obj);
/*  257:     */           }
/*  258:     */           
/*  259:     */           public void setObject(Object obj, Object value)
/*  260:     */           {
/*  261: 345 */             setValue(obj, value);
/*  262:     */           }
/*  263:     */           
/*  264:     */           public TypeType typeEnum()
/*  265:     */           {
/*  266: 350 */             return TypeType.OBJECT;
/*  267:     */           }
/*  268:     */           
/*  269:     */           public boolean isPrimitive()
/*  270:     */           {
/*  271: 355 */             return false;
/*  272:     */           }
/*  273:     */           
/*  274:     */           public boolean isFinal()
/*  275:     */           {
/*  276: 360 */             return false;
/*  277:     */           }
/*  278:     */           
/*  279:     */           public boolean isStatic()
/*  280:     */           {
/*  281: 365 */             return false;
/*  282:     */           }
/*  283:     */           
/*  284:     */           public boolean isVolatile()
/*  285:     */           {
/*  286: 370 */             return false;
/*  287:     */           }
/*  288:     */           
/*  289:     */           public boolean isQualified()
/*  290:     */           {
/*  291: 375 */             return false;
/*  292:     */           }
/*  293:     */           
/*  294:     */           public boolean isReadOnly()
/*  295:     */           {
/*  296: 380 */             return false;
/*  297:     */           }
/*  298:     */           
/*  299:     */           public boolean isWriteOnly()
/*  300:     */           {
/*  301: 385 */             return false;
/*  302:     */           }
/*  303:     */           
/*  304:     */           public Class<?> type()
/*  305:     */           {
/*  306: 390 */             return Object.class;
/*  307:     */           }
/*  308:     */           
/*  309:     */           public Class<?> declaringParent()
/*  310:     */           {
/*  311: 395 */             return null;
/*  312:     */           }
/*  313:     */           
/*  314:     */           public Object parent()
/*  315:     */           {
/*  316: 400 */             return BeanUtils.1.this.val$map;
/*  317:     */           }
/*  318:     */           
/*  319:     */           public Field getField()
/*  320:     */           {
/*  321: 405 */             return null;
/*  322:     */           }
/*  323:     */           
/*  324:     */           public boolean include()
/*  325:     */           {
/*  326: 410 */             return true;
/*  327:     */           }
/*  328:     */           
/*  329:     */           public boolean ignore()
/*  330:     */           {
/*  331: 415 */             return false;
/*  332:     */           }
/*  333:     */           
/*  334:     */           public ParameterizedType getParameterizedType()
/*  335:     */           {
/*  336: 420 */             return null;
/*  337:     */           }
/*  338:     */           
/*  339:     */           public Class<?> getComponentClass()
/*  340:     */           {
/*  341: 425 */             return null;
/*  342:     */           }
/*  343:     */           
/*  344:     */           public boolean hasAnnotation(String annotationName)
/*  345:     */           {
/*  346: 430 */             return false;
/*  347:     */           }
/*  348:     */           
/*  349:     */           public Map<String, Object> getAnnotationData(String annotationName)
/*  350:     */           {
/*  351: 435 */             return null;
/*  352:     */           }
/*  353:     */           
/*  354:     */           public boolean isViewActive(String activeView)
/*  355:     */           {
/*  356: 440 */             return false;
/*  357:     */           }
/*  358:     */           
/*  359:     */           public void setStaticValue(Object newValue) {}
/*  360:     */           
/*  361:     */           public TypeType componentType()
/*  362:     */           {
/*  363: 450 */             return null;
/*  364:     */           }
/*  365:     */         };
/*  366:     */       }
/*  367:     */       
/*  368:     */       public FieldAccess put(String key, FieldAccess value)
/*  369:     */       {
/*  370: 457 */         return null;
/*  371:     */       }
/*  372:     */       
/*  373:     */       public FieldAccess remove(Object key)
/*  374:     */       {
/*  375: 462 */         return null;
/*  376:     */       }
/*  377:     */       
/*  378:     */       public void putAll(Map<? extends String, ? extends FieldAccess> m) {}
/*  379:     */       
/*  380:     */       public void clear() {}
/*  381:     */       
/*  382:     */       public Set<String> keySet()
/*  383:     */       {
/*  384: 477 */         return null;
/*  385:     */       }
/*  386:     */       
/*  387:     */       public Collection<FieldAccess> values()
/*  388:     */       {
/*  389: 482 */         return null;
/*  390:     */       }
/*  391:     */       
/*  392:     */       public Set<Map.Entry<String, FieldAccess>> entrySet()
/*  393:     */       {
/*  394: 487 */         return null;
/*  395:     */       }
/*  396:     */     };
/*  397:     */   }
/*  398:     */   
/*  399:     */   public static void setPropertyValue(Object root, Object newValue, String... properties)
/*  400:     */   {
/*  401: 504 */     Object object = root;
/*  402:     */     
/*  403: 506 */     int index = 0;
/*  404:     */     try
/*  405:     */     {
/*  406: 511 */       for (String property : properties)
/*  407:     */       {
/*  408: 514 */         Map<String, FieldAccess> fields = getFieldsFromObject(object);
/*  409: 515 */         FieldAccess field = (FieldAccess)fields.get(property);
/*  410: 518 */         if (StringScanner.isDigits(property))
/*  411:     */         {
/*  412: 520 */           object = idx(object, StringScanner.parseInt(property));
/*  413:     */         }
/*  414:     */         else
/*  415:     */         {
/*  416: 524 */           if (field == null) {
/*  417: 525 */             Exceptions.die(Boon.sputs(new Object[] { "We were unable to access property=", property, "\nThe properties passed were=", properties, "\nThe root object is =", root.getClass().getName(), "\nThe current object is =", object.getClass().getName() }));
/*  418:     */           }
/*  419: 535 */           if (index == properties.length - 1) {
/*  420: 536 */             field.setValue(object, newValue);
/*  421:     */           } else {
/*  422: 538 */             object = field.getObject(object);
/*  423:     */           }
/*  424:     */         }
/*  425: 542 */         index++;
/*  426:     */       }
/*  427:     */     }
/*  428:     */     catch (Exception ex)
/*  429:     */     {
/*  430: 545 */       Exceptions.requireNonNull(root, "Root cannot be null");
/*  431: 546 */       Exceptions.handle(ex, new Object[] { "Unable to set property for root object", Boon.className(root), "for property path", properties, "with new value", newValue, "last object in the tree was", Boon.className(object), "current property index", Integer.valueOf(index) });
/*  432:     */     }
/*  433:     */   }
/*  434:     */   
/*  435:     */   public static void setPropertyValue(Class<?> root, Object newValue, String... properties)
/*  436:     */   {
/*  437: 565 */     Object object = root;
/*  438:     */     
/*  439: 567 */     int index = 0;
/*  440:     */     
/*  441:     */ 
/*  442: 570 */     Map<String, FieldAccess> fields = getFieldsFromObject(root);
/*  443:     */     try
/*  444:     */     {
/*  445: 575 */       for (String property : properties)
/*  446:     */       {
/*  447: 577 */         FieldAccess field = (FieldAccess)fields.get(property);
/*  448: 580 */         if (StringScanner.isDigits(property))
/*  449:     */         {
/*  450: 582 */           object = idx(object, StringScanner.parseInt(property));
/*  451:     */         }
/*  452:     */         else
/*  453:     */         {
/*  454: 586 */           if (field == null) {
/*  455: 587 */             Exceptions.die(Boon.sputs(new Object[] { "We were unable to access property=", property, "\nThe properties passed were=", properties, "\nThe root object is =", root.getClass().getName(), "\nThe current object is =", object.getClass().getName() }));
/*  456:     */           }
/*  457: 597 */           if (index == properties.length - 1)
/*  458:     */           {
/*  459: 598 */             if ((object instanceof Class)) {
/*  460: 599 */               field.setStaticValue(newValue);
/*  461:     */             } else {
/*  462: 601 */               field.setValue(object, newValue);
/*  463:     */             }
/*  464:     */           }
/*  465:     */           else
/*  466:     */           {
/*  467: 605 */             object = field.getObject(object);
/*  468: 606 */             if (object != null) {
/*  469: 607 */               fields = getFieldsFromObject(root);
/*  470:     */             }
/*  471:     */           }
/*  472:     */         }
/*  473: 612 */         index++;
/*  474:     */       }
/*  475:     */     }
/*  476:     */     catch (Exception ex)
/*  477:     */     {
/*  478: 615 */       Exceptions.requireNonNull(root, "Root cannot be null");
/*  479: 616 */       Exceptions.handle(ex, new Object[] { "Unable to set property for root object", Boon.className(root), "for property path", properties, "with new value", newValue, "last object in the tree was", Boon.className(object), "current property index", Integer.valueOf(index) });
/*  480:     */     }
/*  481:     */   }
/*  482:     */   
/*  483:     */   public static Object getPropertyValue(Object root, String... properties)
/*  484:     */   {
/*  485: 637 */     Object object = root;
/*  486: 641 */     for (String property : properties)
/*  487:     */     {
/*  488: 643 */       if (object == null) {
/*  489: 644 */         return null;
/*  490:     */       }
/*  491: 647 */       if (property.equals("this"))
/*  492:     */       {
/*  493: 648 */         if ((object instanceof Map))
/*  494:     */         {
/*  495: 651 */           Object aThis = ((Map)object).get("this");
/*  496: 652 */           if (aThis != null) {
/*  497: 653 */             object = aThis;
/*  498:     */           }
/*  499:     */         }
/*  500:     */       }
/*  501: 661 */       else if ((object instanceof Map))
/*  502:     */       {
/*  503: 662 */         object = ((Map)object).get(property);
/*  504:     */       }
/*  505:     */       else
/*  506:     */       {
/*  507: 667 */         char c = property.charAt(0);
/*  508: 668 */         if (CharScanner.isDigit(c))
/*  509:     */         {
/*  510: 670 */           object = idx(object, StringScanner.parseInt(property));
/*  511:     */         }
/*  512: 675 */         else if ((object instanceof Collection))
/*  513:     */         {
/*  514: 677 */           object = _getFieldValuesFromCollectionOrArray(object, property);
/*  515:     */         }
/*  516: 679 */         else if (Typ.isArray(object))
/*  517:     */         {
/*  518: 682 */           Iterator iter = Conversions.iterator(object);
/*  519: 683 */           List list = Lists.list(iter);
/*  520: 684 */           object = _getFieldValuesFromCollectionOrArray(list, property);
/*  521:     */         }
/*  522:     */         else
/*  523:     */         {
/*  524: 690 */           Map<String, FieldAccess> fields = getPropertyFieldAccessMap(object.getClass());
/*  525:     */           
/*  526:     */ 
/*  527: 693 */           FieldAccess field = (FieldAccess)fields.get(property);
/*  528: 695 */           if (field == null) {
/*  529: 696 */             return null;
/*  530:     */           }
/*  531: 699 */           object = field.getValue(object);
/*  532:     */         }
/*  533:     */       }
/*  534:     */     }
/*  535: 702 */     return object;
/*  536:     */   }
/*  537:     */   
/*  538:     */   public static Class<?> getPropertyType(Object root, String property)
/*  539:     */   {
/*  540: 714 */     Map<String, FieldAccess> fields = getPropertyFieldAccessMap(root.getClass());
/*  541:     */     
/*  542: 716 */     FieldAccess field = (FieldAccess)fields.get(property);
/*  543: 717 */     return field.type();
/*  544:     */   }
/*  545:     */   
/*  546:     */   public static <T> T idxGeneric(Class<T> t, Object object, String path)
/*  547:     */   {
/*  548: 725 */     String[] properties = propertyPathAsStringArray(path);
/*  549:     */     
/*  550: 727 */     return getPropertyValue(object, properties);
/*  551:     */   }
/*  552:     */   
/*  553:     */   public static <T> List<T> idxList(Class<T> cls, Object items, String path)
/*  554:     */   {
/*  555: 734 */     String[] properties = propertyPathAsStringArray(path);
/*  556: 735 */     return (List)getPropByPath(items, properties);
/*  557:     */   }
/*  558:     */   
/*  559:     */   public static List idxList(Object items, String path)
/*  560:     */   {
/*  561: 740 */     String[] properties = propertyPathAsStringArray(path);
/*  562: 741 */     return (List)getPropByPath(items, properties);
/*  563:     */   }
/*  564:     */   
/*  565:     */   public static <T> List<T> idxRecurse(Class<T> cls, Object items, String path)
/*  566:     */   {
/*  567: 746 */     String[] properties = propertyPathAsStringArray(path);
/*  568: 747 */     return (List)getPropByPath(items, properties);
/*  569:     */   }
/*  570:     */   
/*  571:     */   public static List idxRecurse(Object items, String path)
/*  572:     */   {
/*  573: 752 */     String[] properties = propertyPathAsStringArray(path);
/*  574: 753 */     return (List)getPropByPath(items, properties);
/*  575:     */   }
/*  576:     */   
/*  577:     */   public static Object idx(Object object, String path)
/*  578:     */   {
/*  579: 769 */     String[] properties = propertyPathAsStringArray(path);
/*  580:     */     
/*  581: 771 */     return getPropertyValue(object, properties);
/*  582:     */   }
/*  583:     */   
/*  584:     */   /**
/*  585:     */    * @deprecated
/*  586:     */    */
/*  587:     */   public static Object indexOf(Object object, String path)
/*  588:     */   {
/*  589: 784 */     return atIndex(object, path);
/*  590:     */   }
/*  591:     */   
/*  592:     */   public static Object atIndex(Object object, String path)
/*  593:     */   {
/*  594: 800 */     String[] properties = propertyPathAsStringArray(path);
/*  595:     */     
/*  596: 802 */     return getPropertyValue(object, properties);
/*  597:     */   }
/*  598:     */   
/*  599: 805 */   static Map<String, String[]> splitsPathsCache = new ConcurrentHashMap();
/*  600:     */   
/*  601:     */   private static String[] propertyPathAsStringArray(String path)
/*  602:     */   {
/*  603: 808 */     String[] split = (String[])splitsPathsCache.get(path);
/*  604: 809 */     if (split != null) {
/*  605: 810 */       return split;
/*  606:     */     }
/*  607: 814 */     split = StringScanner.splitByCharsNoneEmpty(path, new char[] { '.', '[', ']', '/' });
/*  608: 815 */     splitsPathsCache.put(path, split);
/*  609: 816 */     return split;
/*  610:     */   }
/*  611:     */   
/*  612:     */   public static Object findProperty(Object context, String propertyPath)
/*  613:     */   {
/*  614: 823 */     int index = propertyPath.indexOf('|');
/*  615:     */     Object defaultValue;
/*  616: 827 */     if (index != -1)
/*  617:     */     {
/*  618: 829 */       String[] splitByPipe = Str.splitByPipe(propertyPath);
/*  619: 830 */       Object defaultValue = splitByPipe[1];
/*  620: 831 */       propertyPath = splitByPipe[0];
/*  621:     */     }
/*  622:     */     else
/*  623:     */     {
/*  624: 834 */       defaultValue = null;
/*  625:     */     }
/*  626: 838 */     Iterator iterator = Conversions.iterator(context);
/*  627: 839 */     while (iterator.hasNext())
/*  628:     */     {
/*  629: 840 */       Object ctx = iterator.next();
/*  630: 841 */       Object object = idx(ctx, propertyPath);
/*  631: 842 */       if (object != null)
/*  632:     */       {
/*  633: 844 */         if ((object instanceof List))
/*  634:     */         {
/*  635: 845 */           List list = (List)object;
/*  636: 846 */           int nulls = 0;
/*  637: 847 */           for (Object o : list) {
/*  638: 848 */             if (o == null) {
/*  639: 849 */               nulls++;
/*  640:     */             }
/*  641:     */           }
/*  642: 852 */           if (nulls == list.size()) {
/*  643:     */             break;
/*  644:     */           }
/*  645:     */         }
/*  646: 856 */         return object;
/*  647:     */       }
/*  648:     */     }
/*  649: 860 */     return defaultValue;
/*  650:     */   }
/*  651:     */   
/*  652:     */   public static void injectIntoProperty(Object object, String path, Object value)
/*  653:     */   {
/*  654: 874 */     String[] properties = propertyPathAsStringArray(path);
/*  655:     */     
/*  656: 876 */     setPropertyValue(object, value, properties);
/*  657:     */   }
/*  658:     */   
/*  659:     */   public static void idx(Object object, String path, Object value)
/*  660:     */   {
/*  661: 889 */     String[] properties = propertyPathAsStringArray(path);
/*  662:     */     
/*  663: 891 */     setPropertyValue(object, value, properties);
/*  664:     */   }
/*  665:     */   
/*  666:     */   public static void idx(Class<?> cls, String path, Object value)
/*  667:     */   {
/*  668: 904 */     String[] properties = propertyPathAsStringArray(path);
/*  669:     */     
/*  670: 906 */     setPropertyValue(cls, value, properties);
/*  671:     */   }
/*  672:     */   
/*  673:     */   private static Object getCollectionProp(Object o, String propName, int index, String[] path)
/*  674:     */   {
/*  675: 916 */     o = _getFieldValuesFromCollectionOrArray(o, propName);
/*  676: 918 */     if (index + 1 == path.length) {
/*  677: 919 */       return o;
/*  678:     */     }
/*  679: 921 */     index++;
/*  680: 922 */     return getCollectionProp(o, path[index], index, path);
/*  681:     */   }
/*  682:     */   
/*  683:     */   public static Object getProp(Object object, String property)
/*  684:     */   {
/*  685: 936 */     if (object == null) {
/*  686: 937 */       return null;
/*  687:     */     }
/*  688: 940 */     if (StringScanner.isDigits(property)) {
/*  689: 942 */       object = idx(object, StringScanner.parseInt(property));
/*  690:     */     }
/*  691: 946 */     Class<?> cls = object.getClass();
/*  692:     */     
/*  693:     */ 
/*  694: 949 */     Map<String, FieldAccess> fields = Reflection.getPropertyFieldAccessors(cls);
/*  695: 951 */     if (!fields.containsKey(property)) {
/*  696: 952 */       fields = Reflection.getAllAccessorFields(cls);
/*  697:     */     }
/*  698: 955 */     if (!fields.containsKey(property)) {
/*  699: 956 */       return null;
/*  700:     */     }
/*  701: 958 */     return ((FieldAccess)fields.get(property)).getValue(object);
/*  702:     */   }
/*  703:     */   
/*  704:     */   public static int getPropertyInt(Object root, String... properties)
/*  705:     */   {
/*  706: 970 */     String lastProperty = properties[(properties.length - 1)];
/*  707: 975 */     if (StringScanner.isDigits(lastProperty)) {
/*  708: 977 */       return Conversions.toInt(getPropertyValue(root, properties));
/*  709:     */     }
/*  710: 982 */     Object object = baseForGetProperty(root, properties);
/*  711:     */     
/*  712: 984 */     Map<String, FieldAccess> fields = getFieldsFromObject(object);
/*  713:     */     
/*  714: 986 */     FieldAccess field = (FieldAccess)fields.get(lastProperty);
/*  715: 989 */     if (field.type() == Typ.intgr) {
/*  716: 990 */       return field.getInt(object);
/*  717:     */     }
/*  718: 992 */     return Conversions.toInt(field.getValue(object));
/*  719:     */   }
/*  720:     */   
/*  721:     */   private static Object baseForGetProperty(Object root, String[] properties)
/*  722:     */   {
/*  723:1004 */     Object object = root;
/*  724:     */     
/*  725:1006 */     Map<String, FieldAccess> fields = null;
/*  726:1008 */     for (int index = 0; index < properties.length - 1; index++)
/*  727:     */     {
/*  728:1009 */       if (object == null) {
/*  729:1010 */         return null;
/*  730:     */       }
/*  731:1014 */       String property = properties[index];
/*  732:1017 */       if (!property.equals("this")) {
/*  733:1021 */         if (StringScanner.isDigits(property))
/*  734:     */         {
/*  735:1023 */           object = idx(object, StringScanner.parseInt(property));
/*  736:     */         }
/*  737:1027 */         else if ((object instanceof Collection))
/*  738:     */         {
/*  739:1028 */           object = _getFieldValuesFromCollectionOrArray(object, property);
/*  740:     */         }
/*  741:1031 */         else if (Typ.isArray(object))
/*  742:     */         {
/*  743:1033 */           Iterator iter = Conversions.iterator(object);
/*  744:1034 */           List list = Lists.list(iter);
/*  745:1035 */           object = _getFieldValuesFromCollectionOrArray(list, property);
/*  746:     */         }
/*  747:     */         else
/*  748:     */         {
/*  749:1042 */           fields = getPropertyFieldAccessMap(object.getClass());
/*  750:     */           
/*  751:1044 */           FieldAccess field = (FieldAccess)fields.get(property);
/*  752:1047 */           if (field == null) {
/*  753:1048 */             return null;
/*  754:     */           }
/*  755:1051 */           object = field.getObject(object);
/*  756:     */         }
/*  757:     */       }
/*  758:     */     }
/*  759:1054 */     return object;
/*  760:     */   }
/*  761:     */   
/*  762:     */   private static Class<?> baseForGetProperty(Class<?> root, String[] properties)
/*  763:     */   {
/*  764:1064 */     Class cls = root;
/*  765:     */     
/*  766:1066 */     Map<String, FieldAccess> fields = null;
/*  767:1068 */     for (int index = 0; index < properties.length - 1; index++)
/*  768:     */     {
/*  769:1069 */       fields = getPropertyFieldAccessMap(cls);
/*  770:     */       
/*  771:1071 */       String property = properties[index];
/*  772:1072 */       FieldAccess field = (FieldAccess)fields.get(property);
/*  773:1073 */       cls = field.type();
/*  774:     */     }
/*  775:1075 */     return cls;
/*  776:     */   }
/*  777:     */   
/*  778:     */   public static int idxInt(Object object, String path)
/*  779:     */   {
/*  780:1087 */     String[] properties = propertyPathAsStringArray(path);
/*  781:     */     
/*  782:1089 */     return getPropertyInt(object, properties);
/*  783:     */   }
/*  784:     */   
/*  785:     */   public static String idxStr(Object object, String path)
/*  786:     */   {
/*  787:1102 */     Object val = idx(object, path);
/*  788:1103 */     return Conversions.toString(val);
/*  789:     */   }
/*  790:     */   
/*  791:     */   private static String getPropertyString(Object root, String[] properties)
/*  792:     */   {
/*  793:1110 */     Object object = baseForGetProperty(root, properties);
/*  794:     */     
/*  795:1112 */     Map<String, FieldAccess> fields = getFieldsFromObject(object);
/*  796:     */     
/*  797:1114 */     String lastProperty = properties[(properties.length - 1)];
/*  798:1115 */     FieldAccess field = (FieldAccess)fields.get(lastProperty);
/*  799:1117 */     if (field.type() == Typ.string) {
/*  800:1118 */       return (String)field.getObject(object);
/*  801:     */     }
/*  802:1120 */     return Conversions.toString(field.getValue(object));
/*  803:     */   }
/*  804:     */   
/*  805:     */   public static byte getPropertyByte(Object root, String... properties)
/*  806:     */   {
/*  807:1136 */     String lastProperty = properties[(properties.length - 1)];
/*  808:1138 */     if (StringScanner.isDigits(lastProperty)) {
/*  809:1140 */       return Conversions.toByte(getPropertyValue(root, properties));
/*  810:     */     }
/*  811:1144 */     Object object = baseForGetProperty(root, properties);
/*  812:     */     
/*  813:1146 */     Map<String, FieldAccess> fields = getFieldsFromObject(object);
/*  814:     */     
/*  815:1148 */     FieldAccess field = (FieldAccess)fields.get(lastProperty);
/*  816:1150 */     if (field.type() == Typ.bt) {
/*  817:1151 */       return field.getByte(object);
/*  818:     */     }
/*  819:1153 */     return Conversions.toByte(field.getValue(object));
/*  820:     */   }
/*  821:     */   
/*  822:     */   public static byte idxByte(Object object, String path)
/*  823:     */   {
/*  824:1164 */     String[] properties = propertyPathAsStringArray(path);
/*  825:     */     
/*  826:1166 */     return getPropertyByte(object, properties);
/*  827:     */   }
/*  828:     */   
/*  829:     */   public static float getPropertyFloat(Object root, String... properties)
/*  830:     */   {
/*  831:1176 */     String lastProperty = properties[(properties.length - 1)];
/*  832:1178 */     if (StringScanner.isDigits(lastProperty)) {
/*  833:1180 */       return Conversions.toFloat(getPropertyValue(root, properties));
/*  834:     */     }
/*  835:1184 */     Object object = baseForGetProperty(root, properties);
/*  836:     */     
/*  837:1186 */     Map<String, FieldAccess> fields = getFieldsFromObject(object);
/*  838:     */     
/*  839:1188 */     FieldAccess field = (FieldAccess)fields.get(lastProperty);
/*  840:1190 */     if (field.type() == Typ.flt) {
/*  841:1191 */       return field.getFloat(object);
/*  842:     */     }
/*  843:1193 */     return Conversions.toFloat(field.getValue(object));
/*  844:     */   }
/*  845:     */   
/*  846:     */   public static float idxFloat(Object object, String path)
/*  847:     */   {
/*  848:1206 */     String[] properties = propertyPathAsStringArray(path);
/*  849:     */     
/*  850:1208 */     return getPropertyFloat(object, properties);
/*  851:     */   }
/*  852:     */   
/*  853:     */   public static short getPropertyShort(Object root, String... properties)
/*  854:     */   {
/*  855:1220 */     String lastProperty = properties[(properties.length - 1)];
/*  856:1225 */     if (StringScanner.isDigits(lastProperty)) {
/*  857:1227 */       return Conversions.toShort(getPropertyValue(root, properties));
/*  858:     */     }
/*  859:1233 */     Object object = baseForGetProperty(root, properties);
/*  860:     */     
/*  861:1235 */     Map<String, FieldAccess> fields = getFieldsFromObject(object);
/*  862:1236 */     FieldAccess field = (FieldAccess)fields.get(lastProperty);
/*  863:1239 */     if (field.type() == Typ.shrt) {
/*  864:1240 */       return field.getShort(object);
/*  865:     */     }
/*  866:1242 */     return Conversions.toShort(field.getValue(object));
/*  867:     */   }
/*  868:     */   
/*  869:     */   public static Class<?> getPropertyPathType(Object root, String... properties)
/*  870:     */   {
/*  871:1257 */     Object object = baseForGetProperty(root, properties);
/*  872:     */     
/*  873:1259 */     Map<String, FieldAccess> fields = getFieldsFromObject(object);
/*  874:1260 */     String lastProperty = properties[(properties.length - 1)];
/*  875:1261 */     FieldAccess field = (FieldAccess)fields.get(lastProperty);
/*  876:     */     
/*  877:1263 */     return field.type();
/*  878:     */   }
/*  879:     */   
/*  880:     */   public static FieldAccess getPropertyPathField(Object root, String... properties)
/*  881:     */   {
/*  882:1277 */     Object object = baseForGetProperty(root, properties);
/*  883:     */     
/*  884:1279 */     Map<String, FieldAccess> fields = getFieldsFromObject(object);
/*  885:1280 */     String lastProperty = properties[(properties.length - 1)];
/*  886:1281 */     FieldAccess field = (FieldAccess)fields.get(lastProperty);
/*  887:     */     
/*  888:1283 */     return field;
/*  889:     */   }
/*  890:     */   
/*  891:     */   public static FieldAccess getPropertyPathField(Class root, String... properties)
/*  892:     */   {
/*  893:1297 */     Class cls = baseForGetProperty(root, properties);
/*  894:     */     
/*  895:1299 */     Map<String, FieldAccess> fields = getFieldsFromObject(cls);
/*  896:1300 */     String lastProperty = properties[(properties.length - 1)];
/*  897:1301 */     FieldAccess field = (FieldAccess)fields.get(lastProperty);
/*  898:     */     
/*  899:1303 */     return field;
/*  900:     */   }
/*  901:     */   
/*  902:     */   public static short idxShort(Object object, String path)
/*  903:     */   {
/*  904:1314 */     String[] properties = propertyPathAsStringArray(path);
/*  905:     */     
/*  906:1316 */     return getPropertyShort(object, properties);
/*  907:     */   }
/*  908:     */   
/*  909:     */   public static Class idxType(Object object, String path)
/*  910:     */   {
/*  911:1327 */     String[] properties = propertyPathAsStringArray(path);
/*  912:     */     
/*  913:1329 */     return getPropertyPathType(object, properties);
/*  914:     */   }
/*  915:     */   
/*  916:     */   public static FieldAccess idxField(Object object, String path)
/*  917:     */   {
/*  918:1341 */     String[] properties = propertyPathAsStringArray(path);
/*  919:     */     
/*  920:1343 */     return getPropertyPathField(object, properties);
/*  921:     */   }
/*  922:     */   
/*  923:     */   public static FieldAccess idxField(Class<?> cls, String path)
/*  924:     */   {
/*  925:1355 */     String[] properties = propertyPathAsStringArray(path);
/*  926:     */     
/*  927:1357 */     return getPropertyPathField(cls, properties);
/*  928:     */   }
/*  929:     */   
/*  930:     */   public static char getPropertyChar(Object root, String... properties)
/*  931:     */   {
/*  932:1369 */     String lastProperty = properties[(properties.length - 1)];
/*  933:1372 */     if (StringScanner.isDigits(lastProperty)) {
/*  934:1374 */       return Conversions.toChar(getPropertyValue(root, properties));
/*  935:     */     }
/*  936:1378 */     Object object = baseForGetProperty(root, properties);
/*  937:     */     
/*  938:1380 */     Map<String, FieldAccess> fields = getFieldsFromObject(object);
/*  939:     */     
/*  940:1382 */     FieldAccess field = (FieldAccess)fields.get(lastProperty);
/*  941:1384 */     if (field.type() == Typ.chr) {
/*  942:1385 */       return field.getChar(object);
/*  943:     */     }
/*  944:1387 */     return Conversions.toChar(field.getValue(object));
/*  945:     */   }
/*  946:     */   
/*  947:     */   public static char idxChar(Object object, String path)
/*  948:     */   {
/*  949:1400 */     String[] properties = propertyPathAsStringArray(path);
/*  950:     */     
/*  951:1402 */     return getPropertyChar(object, properties);
/*  952:     */   }
/*  953:     */   
/*  954:     */   public static double getPropertyDouble(Object root, String... properties)
/*  955:     */   {
/*  956:1416 */     String lastProperty = properties[(properties.length - 1)];
/*  957:1419 */     if (StringScanner.isDigits(lastProperty)) {
/*  958:1421 */       return Conversions.toDouble(getPropertyValue(root, properties));
/*  959:     */     }
/*  960:1426 */     Object object = baseForGetProperty(root, properties);
/*  961:     */     
/*  962:1428 */     Map<String, FieldAccess> fields = getFieldsFromObject(object);
/*  963:     */     
/*  964:1430 */     FieldAccess field = (FieldAccess)fields.get(lastProperty);
/*  965:1432 */     if (field.type() == Typ.dbl) {
/*  966:1433 */       return field.getDouble(object);
/*  967:     */     }
/*  968:1435 */     return Conversions.toDouble(field.getValue(object));
/*  969:     */   }
/*  970:     */   
/*  971:     */   public static double idxDouble(Object object, String path)
/*  972:     */   {
/*  973:1448 */     String[] properties = propertyPathAsStringArray(path);
/*  974:     */     
/*  975:1450 */     return getPropertyDouble(object, properties);
/*  976:     */   }
/*  977:     */   
/*  978:     */   public static long getPropertyLong(Object root, String... properties)
/*  979:     */   {
/*  980:1462 */     String lastProperty = properties[(properties.length - 1)];
/*  981:1465 */     if (StringScanner.isDigits(lastProperty)) {
/*  982:1467 */       return Conversions.toLong(getPropertyValue(root, properties));
/*  983:     */     }
/*  984:1472 */     Object object = baseForGetProperty(root, properties);
/*  985:     */     
/*  986:1474 */     Map<String, FieldAccess> fields = getFieldsFromObject(object);
/*  987:     */     
/*  988:1476 */     FieldAccess field = (FieldAccess)fields.get(lastProperty);
/*  989:1478 */     if (field.type() == Typ.lng) {
/*  990:1479 */       return field.getLong(object);
/*  991:     */     }
/*  992:1481 */     return Conversions.toLong(field.getValue(object));
/*  993:     */   }
/*  994:     */   
/*  995:     */   public static long idxLong(Object object, String path)
/*  996:     */   {
/*  997:1494 */     String[] properties = propertyPathAsStringArray(path);
/*  998:     */     
/*  999:1496 */     return getPropertyLong(object, properties);
/* 1000:     */   }
/* 1001:     */   
/* 1002:     */   public static boolean getPropertyBoolean(Object root, String... properties)
/* 1003:     */   {
/* 1004:1509 */     String lastProperty = properties[(properties.length - 1)];
/* 1005:1512 */     if (StringScanner.isDigits(lastProperty)) {
/* 1006:1514 */       return Conversions.toBoolean(getPropertyValue(root, properties));
/* 1007:     */     }
/* 1008:1519 */     Object object = baseForGetProperty(root, properties);
/* 1009:     */     
/* 1010:1521 */     Map<String, FieldAccess> fields = getFieldsFromObject(object);
/* 1011:     */     
/* 1012:1523 */     FieldAccess field = (FieldAccess)fields.get(lastProperty);
/* 1013:1525 */     if (field.type() == Typ.bln) {
/* 1014:1526 */       return field.getBoolean(object);
/* 1015:     */     }
/* 1016:1528 */     return Conversions.toBoolean(field.getValue(object));
/* 1017:     */   }
/* 1018:     */   
/* 1019:     */   public static boolean idxBoolean(Object object, String path)
/* 1020:     */   {
/* 1021:1536 */     String[] properties = propertyPathAsStringArray(path);
/* 1022:     */     
/* 1023:1538 */     return getPropertyBoolean(object, properties);
/* 1024:     */   }
/* 1025:     */   
/* 1026:     */   public static <V> Map<String, V> collectionToMap(String propertyKey, Collection<V> values)
/* 1027:     */   {
/* 1028:1543 */     LinkedHashMap<String, V> map = new LinkedHashMap(values.size());
/* 1029:1544 */     for (V v : values)
/* 1030:     */     {
/* 1031:1545 */       String key = (String)idxGeneric(Typ.string, v, propertyKey);
/* 1032:1546 */       map.put(key, v);
/* 1033:     */     }
/* 1034:1548 */     return map;
/* 1035:     */   }
/* 1036:     */   
/* 1037:     */   public static void copyProperties(Object object, Map<String, Object> properties)
/* 1038:     */   {
/* 1039:1554 */     Set<Map.Entry<String, Object>> props = properties.entrySet();
/* 1040:1555 */     for (Map.Entry<String, Object> entry : props) {
/* 1041:1556 */       setPropertyValue(object, entry.getValue(), new String[] { (String)entry.getKey() });
/* 1042:     */     }
/* 1043:     */   }
/* 1044:     */   
/* 1045:     */   private static Object _getFieldValuesFromCollectionOrArray(Object object, String key)
/* 1046:     */   {
/* 1047:1564 */     if (object == null) {
/* 1048:1565 */       return null;
/* 1049:     */     }
/* 1050:1568 */     if ((object instanceof Collection))
/* 1051:     */     {
/* 1052:1569 */       Collection collection = (Collection)object;
/* 1053:1572 */       if (collection.size() == 0) {
/* 1054:1573 */         return Collections.EMPTY_LIST;
/* 1055:     */       }
/* 1056:1575 */       List list = new ArrayList(collection.size());
/* 1057:     */       
/* 1058:     */ 
/* 1059:1578 */       Class lastClass = null;
/* 1060:1579 */       Map<String, FieldAccess> fields = null;
/* 1061:1580 */       FieldAccess field = null;
/* 1062:1583 */       for (Object item : collection) {
/* 1063:1585 */         if ((item instanceof Map))
/* 1064:     */         {
/* 1065:1586 */           Map map = (Map)item;
/* 1066:1587 */           list.add(map.get(key));
/* 1067:     */         }
/* 1068:     */         else
/* 1069:     */         {
/* 1070:1590 */           Class currentClass = Boon.cls(item);
/* 1071:1592 */           if ((lastClass == null) || (currentClass != lastClass))
/* 1072:     */           {
/* 1073:1594 */             fields = getPropertyFieldAccessMap(currentClass);
/* 1074:1595 */             field = (FieldAccess)fields.get(key);
/* 1075:1596 */             lastClass = currentClass;
/* 1076:     */           }
/* 1077:1600 */           if (field == null) {
/* 1078:1601 */             list.add(idx(item, key));
/* 1079:     */           } else {
/* 1080:1604 */             list.add(field.getValue(item));
/* 1081:     */           }
/* 1082:     */         }
/* 1083:     */       }
/* 1084:1608 */       return list;
/* 1085:     */     }
/* 1086:1610 */     if (object.getClass().isArray())
/* 1087:     */     {
/* 1088:1611 */       int len = Array.getLength(object);
/* 1089:1612 */       List list = new ArrayList(len);
/* 1090:     */       
/* 1091:     */ 
/* 1092:1615 */       Map<String, FieldAccess> fields = getPropertyFieldAccessMap(object.getClass().getComponentType());
/* 1093:1618 */       for (int index = 0; index < len; index++) {
/* 1094:1619 */         list.add(((FieldAccess)fields.get(key)).getValue(Array.get(object, index)));
/* 1095:     */       }
/* 1096:1621 */       return list;
/* 1097:     */     }
/* 1098:1624 */     return atIndex(object, key);
/* 1099:     */   }
/* 1100:     */   
/* 1101:     */   public static <T> T copy(T item)
/* 1102:     */   {
/* 1103:1631 */     if ((item instanceof Cloneable)) {
/* 1104:1632 */       return ClassMeta.classMeta(item.getClass()).invokeUntyped(item, "clone", (Class[])null);
/* 1105:     */     }
/* 1106:1634 */     return fieldByFieldCopy(item);
/* 1107:     */   }
/* 1108:     */   
/* 1109:     */   public static <T> T fieldByFieldCopy(T item)
/* 1110:     */   {
/* 1111:1641 */     Class<T> aClass = item.getClass();
/* 1112:1642 */     Map<String, FieldAccess> fields = Reflection.getAllAccessorFields(aClass);
/* 1113:     */     
/* 1114:1644 */     T clone = Reflection.newInstance(aClass);
/* 1115:1646 */     for (FieldAccess field : fields.values()) {
/* 1116:     */       try
/* 1117:     */       {
/* 1118:1652 */         if ((field.isStatic()) || (!field.isWriteOnly())) {
/* 1119:1659 */           if (field.isPrimitive())
/* 1120:     */           {
/* 1121:1660 */             field.setValue(clone, field.getValue(item));
/* 1122:     */           }
/* 1123:     */           else
/* 1124:     */           {
/* 1125:1665 */             Object value = field.getObject(item);
/* 1126:1667 */             if (value == null)
/* 1127:     */             {
/* 1128:1669 */               field.setObject(clone, null);
/* 1129:     */             }
/* 1130:1676 */             else if ((!field.isPrimitive()) && (!Typ.isBasicType(field.type())))
/* 1131:     */             {
/* 1132:1679 */               field.setObject(clone, copy(value));
/* 1133:     */             }
/* 1134:1688 */             else if (Typ.isBasicType(field.type()))
/* 1135:     */             {
/* 1136:1690 */               field.setObject(clone, value);
/* 1137:     */             }
/* 1138:1695 */             else if (Typ.isCollection(field.type()))
/* 1139:     */             {
/* 1140:1697 */               Collection<Object> src = (Collection)value;
/* 1141:1698 */               Class<?> collectionType = field.type();
/* 1142:1699 */               Collection<Object> dst = Conversions.createCollection(collectionType, src.size());
/* 1143:1701 */               for (Object o : src) {
/* 1144:1702 */                 dst.add(copy(o));
/* 1145:     */               }
/* 1146:1705 */               field.setObject(clone, dst);
/* 1147:     */             }
/* 1148:1712 */             else if (!Typ.isMap(field.type()))
/* 1149:     */             {
/* 1150:1718 */               if (field.type().isArray())
/* 1151:     */               {
/* 1152:1720 */                 int length = Array.getLength(value);
/* 1153:1721 */                 Object dst = Array.newInstance(field.getComponentClass(), length);
/* 1154:1723 */                 for (int index = 0; index < length; index++)
/* 1155:     */                 {
/* 1156:1724 */                   Object o = Array.get(value, index);
/* 1157:1725 */                   Array.set(dst, index, copy(o));
/* 1158:     */                 }
/* 1159:1728 */                 field.setObject(clone, dst);
/* 1160:     */               }
/* 1161:     */             }
/* 1162:     */           }
/* 1163:     */         }
/* 1164:     */       }
/* 1165:     */       catch (Exception ex)
/* 1166:     */       {
/* 1167:1736 */         return Exceptions.handle(Object.class, "" + field, ex);
/* 1168:     */       }
/* 1169:     */     }
/* 1170:1739 */     return clone;
/* 1171:     */   }
/* 1172:     */   
/* 1173:     */   public static void copyProperties(Object src, Object dest)
/* 1174:     */   {
/* 1175:1746 */     fieldByFieldCopy(src, dest);
/* 1176:     */   }
/* 1177:     */   
/* 1178:     */   public static <T> T createFromSrc(Object src, Class<T> dest)
/* 1179:     */   {
/* 1180:1751 */     T instance = Reflection.newInstance(dest);
/* 1181:1752 */     fieldByFieldCopy(src, instance);
/* 1182:1753 */     return instance;
/* 1183:     */   }
/* 1184:     */   
/* 1185:     */   public static void copyProperties(Object src, Object dest, String... ignore)
/* 1186:     */   {
/* 1187:1758 */     fieldByFieldCopy(src, dest, Sets.set(ignore));
/* 1188:     */   }
/* 1189:     */   
/* 1190:     */   public static void copyProperties(Object src, Object dest, Set<String> ignore)
/* 1191:     */   {
/* 1192:1762 */     fieldByFieldCopy(src, dest, ignore);
/* 1193:     */   }
/* 1194:     */   
/* 1195:     */   private static void fieldByFieldCopy(Object src, Object dst, Set<String> ignore)
/* 1196:     */   {
/* 1197:1767 */     Class<?> srcClass = src.getClass();
/* 1198:1768 */     Map<String, FieldAccess> srcFields = Reflection.getAllAccessorFields(srcClass);
/* 1199:     */     
/* 1200:     */ 
/* 1201:1771 */     Class<?> dstClass = dst.getClass();
/* 1202:1772 */     Map<String, FieldAccess> dstFields = Reflection.getAllAccessorFields(dstClass);
/* 1203:1774 */     for (FieldAccess srcField : srcFields.values()) {
/* 1204:1776 */       if (!ignore.contains(srcField.name()))
/* 1205:     */       {
/* 1206:1780 */         FieldAccess dstField = (FieldAccess)dstFields.get(srcField.name());
/* 1207:     */         try
/* 1208:     */         {
/* 1209:1783 */           copySrcFieldToDestField(src, dst, dstField, srcField, ignore);
/* 1210:     */         }
/* 1211:     */         catch (Exception ex)
/* 1212:     */         {
/* 1213:1786 */           Exceptions.handle(Boon.sputs(new Object[] { "copying field", srcField.name(), srcClass, " to ", dstField.name(), dstClass }), ex);
/* 1214:     */         }
/* 1215:     */       }
/* 1216:     */     }
/* 1217:     */   }
/* 1218:     */   
/* 1219:     */   private static void fieldByFieldCopy(Object src, Object dst)
/* 1220:     */   {
/* 1221:1793 */     Class<?> srcClass = src.getClass();
/* 1222:1794 */     Map<String, FieldAccess> srcFields = Reflection.getAllAccessorFields(srcClass);
/* 1223:     */     
/* 1224:     */ 
/* 1225:1797 */     Class<?> dstClass = dst.getClass();
/* 1226:1798 */     Map<String, FieldAccess> dstFields = Reflection.getAllAccessorFields(dstClass);
/* 1227:1800 */     for (FieldAccess srcField : srcFields.values())
/* 1228:     */     {
/* 1229:1802 */       FieldAccess dstField = (FieldAccess)dstFields.get(srcField.name());
/* 1230:     */       try
/* 1231:     */       {
/* 1232:1805 */         copySrcFieldToDestField(src, dst, dstField, srcField, null);
/* 1233:     */       }
/* 1234:     */       catch (Exception ex)
/* 1235:     */       {
/* 1236:1808 */         Exceptions.handle(Boon.sputs(new Object[] { "copying field", srcField.name(), srcClass, " to ", dstField.name(), dstClass }), ex);
/* 1237:     */       }
/* 1238:     */     }
/* 1239:     */   }
/* 1240:     */   
/* 1241:     */   private static void copySrcFieldToDestField(Object src, Object dst, FieldAccess dstField, FieldAccess srcField, Set<String> ignore)
/* 1242:     */   {
/* 1243:1814 */     if (srcField.isStatic()) {
/* 1244:1815 */       return;
/* 1245:     */     }
/* 1246:1818 */     if (dstField == null) {
/* 1247:1819 */       return;
/* 1248:     */     }
/* 1249:1823 */     if (srcField.isPrimitive())
/* 1250:     */     {
/* 1251:1824 */       dstField.setValue(dst, srcField.getValue(src));
/* 1252:1825 */       return;
/* 1253:     */     }
/* 1254:1828 */     Object srcValue = srcField.getObject(src);
/* 1255:1831 */     if (srcValue == null)
/* 1256:     */     {
/* 1257:1832 */       if (!dstField.isPrimitive()) {
/* 1258:1833 */         dstField.setObject(dst, null);
/* 1259:     */       }
/* 1260:1835 */       return;
/* 1261:     */     }
/* 1262:1843 */     if (Typ.isBasicType(srcField.type()))
/* 1263:     */     {
/* 1264:1845 */       Object value = srcField.getObject(src);
/* 1265:1846 */       dstField.setValue(dst, value);
/* 1266:1847 */       return;
/* 1267:     */     }
/* 1268:1851 */     if (((!(srcValue instanceof Collection)) && (dstField.type() == srcValue.getClass())) || (Typ.isSuperType(dstField.type(), srcValue.getClass())))
/* 1269:     */     {
/* 1270:1854 */       dstField.setObject(dst, copy(srcField.getObject(src)));
/* 1271:1855 */       return;
/* 1272:     */     }
/* 1273:1861 */     if (((srcValue instanceof Collection)) && (dstField.getComponentClass() != null) && (Typ.isCollection(dstField.type())))
/* 1274:     */     {
/* 1275:1865 */       handleCollectionFieldCopy(dst, dstField, (Collection)srcValue);
/* 1276:1866 */       return;
/* 1277:     */     }
/* 1278:1872 */     if ((dstField.typeEnum() != TypeType.ABSTRACT) && (dstField.typeEnum() != TypeType.INTERFACE))
/* 1279:     */     {
/* 1280:1875 */       Object newInstance = Reflection.newInstance(dstField.type());
/* 1281:1876 */       if (ignore == null) {
/* 1282:1877 */         fieldByFieldCopy(srcField.getObject(src), newInstance);
/* 1283:     */       } else {
/* 1284:1879 */         fieldByFieldCopy(srcField.getObject(src), newInstance, ignore);
/* 1285:     */       }
/* 1286:1881 */       dstField.setObject(dst, newInstance);
/* 1287:     */     }
/* 1288:     */   }
/* 1289:     */   
/* 1290:     */   private static void handleCollectionFieldCopy(Object dst, FieldAccess dstField, Collection srcValue)
/* 1291:     */   {
/* 1292:1886 */     if (dstField.getComponentClass() != Typ.string)
/* 1293:     */     {
/* 1294:1888 */       Collection dstCollection = Conversions.createCollection(dstField.type(), srcValue.size());
/* 1295:1889 */       for (Object srcComponentValue : srcValue)
/* 1296:     */       {
/* 1297:1891 */         Object newInstance = Reflection.newInstance(dstField.getComponentClass());
/* 1298:1892 */         fieldByFieldCopy(srcComponentValue, newInstance);
/* 1299:1893 */         dstCollection.add(newInstance);
/* 1300:     */       }
/* 1301:1896 */       dstField.setObject(dst, dstCollection);
/* 1302:     */     }
/* 1303:     */     else
/* 1304:     */     {
/* 1305:1899 */       Collection dstCollection = Conversions.createCollection(dstField.type(), srcValue.size());
/* 1306:1900 */       for (Object srcComponentValue : srcValue) {
/* 1307:1902 */         if (srcComponentValue != null) {
/* 1308:1903 */           dstCollection.add(srcComponentValue.toString());
/* 1309:     */         }
/* 1310:     */       }
/* 1311:1907 */       dstField.setObject(dst, dstCollection);
/* 1312:     */     }
/* 1313:     */   }
/* 1314:     */   
/* 1315:     */   public static Object idx(Object object, int index)
/* 1316:     */   {
/* 1317:1913 */     if (Boon.isArray(object)) {
/* 1318:1914 */       object = Array.get(object, index);
/* 1319:1915 */     } else if ((object instanceof List)) {
/* 1320:1916 */       object = Lists.idx((List)object, index);
/* 1321:     */     }
/* 1322:1918 */     return object;
/* 1323:     */   }
/* 1324:     */   
/* 1325:     */   public static void idx(Object object, int index, Object value)
/* 1326:     */   {
/* 1327:     */     try
/* 1328:     */     {
/* 1329:1923 */       if (Boon.isArray(object)) {
/* 1330:1924 */         Array.set(object, index, value);
/* 1331:1925 */       } else if ((object instanceof List)) {
/* 1332:1926 */         Lists.idx((List)object, index, value);
/* 1333:     */       }
/* 1334:     */     }
/* 1335:     */     catch (Exception notExpected)
/* 1336:     */     {
/* 1337:1929 */       String msg = Str.lines(new String[] { "An unexpected error has occurred", "This is likely a programming error!", String.format("Object is %s, index is %s, and set is %s", new Object[] { object, Integer.valueOf(index), value }), String.format("The object is an array? %s", new Object[] { object == null ? "null" : Boolean.valueOf(object.getClass().isArray()) }), String.format("The object is of type %s", new Object[] { object == null ? "null" : object.getClass().getName() }), String.format("The set is of type %s", new Object[] { value == null ? "null" : value.getClass().getName() }), "" });
/* 1338:     */       
/* 1339:     */ 
/* 1340:     */ 
/* 1341:     */ 
/* 1342:     */ 
/* 1343:     */ 
/* 1344:     */ 
/* 1345:     */ 
/* 1346:     */ 
/* 1347:1939 */       Exceptions.handle(msg, notExpected);
/* 1348:     */     }
/* 1349:     */   }
/* 1350:     */   
/* 1351:     */   public static <T> T idx(Class<T> type, Object object, String property)
/* 1352:     */   {
/* 1353:1944 */     return idx(object, property);
/* 1354:     */   }
/* 1355:     */   
/* 1356:     */   public static <T> T atIndex(Class<T> type, Object object, String property)
/* 1357:     */   {
/* 1358:1949 */     return idx(object, property);
/* 1359:     */   }
/* 1360:     */   
/* 1361:     */   public static boolean isPropPath(String prop)
/* 1362:     */   {
/* 1363:1960 */     if (prop.contains(".")) {
/* 1364:1960 */       return true;
/* 1365:     */     }
/* 1366:1961 */     if (prop.equals("this")) {
/* 1367:1961 */       return true;
/* 1368:     */     }
/* 1369:1962 */     if (prop.contains("[")) {
/* 1370:1962 */       return true;
/* 1371:     */     }
/* 1372:1964 */     return false;
/* 1373:     */   }
/* 1374:     */   
/* 1375:     */   public static void setCollectionProperty(Collection<?> list, String propertyName, Object value)
/* 1376:     */   {
/* 1377:1970 */     for (Object object : list) {
/* 1378:1971 */       idx(object, propertyName, value);
/* 1379:     */     }
/* 1380:     */   }
/* 1381:     */   
/* 1382:     */   public static void setIterableProperty(Iterable<?> list, String propertyName, Object value)
/* 1383:     */   {
/* 1384:1977 */     for (Object object : list) {
/* 1385:1978 */       idx(object, propertyName, value);
/* 1386:     */     }
/* 1387:     */   }
/* 1388:     */   
/* 1389:     */   public static String asPrettyJsonString(Object bean)
/* 1390:     */   {
/* 1391:1984 */     CharBuf buf = CharBuf.createCharBuf();
/* 1392:1985 */     return buf.prettyPrintBean(bean).toString();
/* 1393:     */   }
/* 1394:     */   
/* 1395:     */   public static String asPrettyJsonString(Mapper mapper, Object bean)
/* 1396:     */   {
/* 1397:1989 */     CharBuf buf = CharBuf.createCharBuf();
/* 1398:1990 */     return buf.prettyPrintBean(mapper, bean).toString();
/* 1399:     */   }
/* 1400:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.BeanUtils
 * JD-Core Version:    0.7.0.1
 */