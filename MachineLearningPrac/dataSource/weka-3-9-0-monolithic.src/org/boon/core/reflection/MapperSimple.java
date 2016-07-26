/*    1:     */ package org.boon.core.reflection;
/*    2:     */ 
/*    3:     */ import java.lang.reflect.Array;
/*    4:     */ import java.lang.reflect.ParameterizedType;
/*    5:     */ import java.lang.reflect.Type;
/*    6:     */ import java.util.ArrayList;
/*    7:     */ import java.util.Collection;
/*    8:     */ import java.util.Collections;
/*    9:     */ import java.util.LinkedHashMap;
/*   10:     */ import java.util.List;
/*   11:     */ import java.util.Map;
/*   12:     */ import java.util.Map.Entry;
/*   13:     */ import java.util.Set;
/*   14:     */ import org.boon.Boon;
/*   15:     */ import org.boon.Exceptions;
/*   16:     */ import org.boon.Lists;
/*   17:     */ import org.boon.core.Conversions;
/*   18:     */ import org.boon.core.Typ;
/*   19:     */ import org.boon.core.TypeType;
/*   20:     */ import org.boon.core.Value;
/*   21:     */ import org.boon.core.reflection.fields.FieldAccess;
/*   22:     */ import org.boon.core.reflection.fields.FieldAccessMode;
/*   23:     */ import org.boon.core.reflection.fields.FieldsAccessor;
/*   24:     */ import org.boon.core.value.ValueContainer;
/*   25:     */ import org.boon.core.value.ValueList;
/*   26:     */ import org.boon.core.value.ValueMap;
/*   27:     */ import org.boon.core.value.ValueMapImpl;
/*   28:     */ import org.boon.primitive.Arry;
/*   29:     */ import org.boon.primitive.CharBuf;
/*   30:     */ 
/*   31:     */ public class MapperSimple
/*   32:     */   implements Mapper
/*   33:     */ {
/*   34:     */   private FieldsAccessor fieldsAccessor;
/*   35:     */   
/*   36:     */   public MapperSimple()
/*   37:     */   {
/*   38:  43 */     this.fieldsAccessor = FieldAccessMode.FIELD_THEN_PROPERTY.create(true);
/*   39:     */   }
/*   40:     */   
/*   41:     */   public MapperSimple(FieldsAccessor fieldsAccessor)
/*   42:     */   {
/*   43:  47 */     this.fieldsAccessor = fieldsAccessor;
/*   44:     */   }
/*   45:     */   
/*   46:     */   public <T> List<T> convertListOfMapsToObjects(List<Map> list, Class<T> componentType)
/*   47:     */   {
/*   48:  60 */     List<Object> newList = new ArrayList(list.size());
/*   49:  61 */     for (Object obj : list)
/*   50:     */     {
/*   51:  63 */       if ((obj instanceof Value)) {
/*   52:  64 */         obj = ((Value)obj).toValue();
/*   53:     */       }
/*   54:  67 */       if ((obj instanceof Map))
/*   55:     */       {
/*   56:  69 */         Map map = (Map)obj;
/*   57:  70 */         if ((map instanceof ValueMapImpl)) {
/*   58:  71 */           newList.add(fromValueMap(map, componentType));
/*   59:     */         } else {
/*   60:  73 */           newList.add(fromMap(map, componentType));
/*   61:     */         }
/*   62:     */       }
/*   63:     */       else
/*   64:     */       {
/*   65:  76 */         newList.add(Conversions.coerce(componentType, obj));
/*   66:     */       }
/*   67:     */     }
/*   68:  79 */     return newList;
/*   69:     */   }
/*   70:     */   
/*   71:     */   public <T> T fromMap(Map<String, Object> map, Class<T> cls)
/*   72:     */   {
/*   73:  95 */     T toObject = Reflection.newInstance(cls);
/*   74:  96 */     Map<String, FieldAccess> fields = this.fieldsAccessor.getFields(toObject.getClass());
/*   75:  97 */     Set<Map.Entry<String, Object>> mapKeyValuesEntrySet = map.entrySet();
/*   76: 101 */     for (Map.Entry<String, Object> mapEntry : mapKeyValuesEntrySet)
/*   77:     */     {
/*   78: 104 */       String key = (String)mapEntry.getKey();
/*   79:     */       
/*   80:     */ 
/*   81:     */ 
/*   82: 108 */       FieldAccess field = (FieldAccess)fields.get(this.fieldsAccessor.isCaseInsensitive() ? key.toLowerCase() : key);
/*   83: 111 */       if ((field != null) && 
/*   84:     */       
/*   85:     */ 
/*   86:     */ 
/*   87:     */ 
/*   88: 116 */         (!field.ignore()))
/*   89:     */       {
/*   90: 121 */         Object value = mapEntry.getValue();
/*   91: 127 */         if ((value instanceof Value)) {
/*   92: 128 */           if (((Value)value).isContainer())
/*   93:     */           {
/*   94: 129 */             value = ((Value)value).toValue();
/*   95:     */           }
/*   96:     */           else
/*   97:     */           {
/*   98: 131 */             field.setFromValue(toObject, (Value)value);
/*   99: 132 */             continue;
/*  100:     */           }
/*  101:     */         }
/*  102: 140 */         if (value == null) {
/*  103: 141 */           field.setObject(toObject, null);
/*  104: 148 */         } else if ((value.getClass() == field.type()) || (field.type() == Object.class)) {
/*  105: 149 */           field.setValue(toObject, value);
/*  106: 150 */         } else if (Typ.isBasicType(value)) {
/*  107: 152 */           field.setValue(toObject, value);
/*  108: 162 */         } else if ((value instanceof Map)) {
/*  109: 163 */           setFieldValueFromMap(toObject, field, (Map)value);
/*  110: 164 */         } else if ((value instanceof Collection)) {
/*  111: 166 */           processCollectionFromMapUsingFields(toObject, field, (Collection)value);
/*  112: 167 */         } else if ((value instanceof Map[])) {
/*  113: 169 */           processArrayOfMaps(toObject, field, (Map[])value);
/*  114:     */         } else {
/*  115: 175 */           field.setValue(toObject, value);
/*  116:     */         }
/*  117:     */       }
/*  118:     */     }
/*  119: 180 */     return toObject;
/*  120:     */   }
/*  121:     */   
/*  122:     */   public <T> T fromList(List<?> argList, Class<T> clazz)
/*  123:     */   {
/*  124: 200 */     int size = argList.size();
/*  125:     */     
/*  126:     */ 
/*  127:     */ 
/*  128: 204 */     ClassMeta<T> classMeta = ClassMeta.classMeta(clazz);
/*  129:     */     
/*  130:     */ 
/*  131: 207 */     ConstructorAccess<T> constructorToMatch = null;
/*  132:     */     
/*  133:     */ 
/*  134: 210 */     Object[] finalArgs = null;
/*  135:     */     
/*  136:     */ 
/*  137: 213 */     boolean[] flag = new boolean[1];
/*  138: 214 */     List<Object> convertedArguments = null;
/*  139:     */     try
/*  140:     */     {
/*  141: 221 */       convertedArguments = new ArrayList(argList);
/*  142:     */       
/*  143: 223 */       constructorToMatch = lookupConstructorMeta(size, convertedArguments, classMeta, constructorToMatch, flag, false);
/*  144: 229 */       if (constructorToMatch == null)
/*  145:     */       {
/*  146: 230 */         convertedArguments = new ArrayList(argList);
/*  147: 231 */         constructorToMatch = lookupConstructorMeta(size, convertedArguments, classMeta, constructorToMatch, flag, true);
/*  148:     */       }
/*  149: 239 */       if (constructorToMatch != null)
/*  150:     */       {
/*  151: 240 */         finalArgs = convertedArguments.toArray(new Object[argList.size()]);
/*  152: 241 */         return constructorToMatch.create(finalArgs);
/*  153:     */       }
/*  154: 243 */       return Exceptions.die(Object.class, new Object[] { "Unable to convert list", convertedArguments, "into", clazz });
/*  155:     */     }
/*  156:     */     catch (Exception e)
/*  157:     */     {
/*  158: 252 */       if (constructorToMatch != null)
/*  159:     */       {
/*  160: 255 */         CharBuf buf = CharBuf.create(200);
/*  161: 256 */         buf.addLine();
/*  162: 257 */         buf.multiply('-', 10).add("FINAL ARGUMENTS").multiply('-', 10).addLine();
/*  163: 258 */         if (finalArgs != null) {
/*  164: 259 */           for (Object o : finalArgs) {
/*  165: 260 */             buf.puts(new Object[] { "argument type    ", Boon.className(o) });
/*  166:     */           }
/*  167:     */         }
/*  168: 265 */         buf.multiply('-', 10).add("CONSTRUCTOR").add(constructorToMatch).multiply('-', 10).addLine();
/*  169: 266 */         buf.multiply('-', 10).add("CONSTRUCTOR PARAMS").multiply('-', 10).addLine();
/*  170: 267 */         for (Class<?> c : constructorToMatch.parameterTypes()) {
/*  171: 268 */           buf.puts(new Object[] { "constructor type ", c });
/*  172:     */         }
/*  173: 271 */         buf.multiply('-', 35).addLine();
/*  174: 273 */         if (Boon.debugOn()) {
/*  175: 274 */           Boon.puts(new Object[] { buf });
/*  176:     */         }
/*  177: 279 */         buf.addLine("PARAMETER TYPES");
/*  178: 280 */         buf.add(Lists.list(constructorToMatch.parameterTypes())).addLine();
/*  179:     */         
/*  180: 282 */         buf.addLine("ORIGINAL TYPES PASSED");
/*  181: 283 */         buf.add(TypeType.gatherTypes(convertedArguments)).addLine();
/*  182:     */         
/*  183: 285 */         buf.add(TypeType.gatherActualTypes(convertedArguments)).addLine();
/*  184:     */         
/*  185: 287 */         buf.addLine("CONVERTED ARGUMENT TYPES");
/*  186: 288 */         buf.add(TypeType.gatherTypes(convertedArguments)).addLine();
/*  187: 289 */         buf.add(TypeType.gatherActualTypes(convertedArguments)).addLine();
/*  188:     */         
/*  189: 291 */         Boon.error(e, new Object[] { "unable to create object based on constructor", buf });
/*  190:     */         
/*  191:     */ 
/*  192: 294 */         return Exceptions.handle(Object.class, e, new Object[] { buf.toString() });
/*  193:     */       }
/*  194: 296 */       return Exceptions.handle(Object.class, e, new Object[] { "\nlist args after conversion", convertedArguments, "types", TypeType.gatherTypes(convertedArguments), "\noriginal args", argList, "original types", TypeType.gatherTypes(argList) });
/*  195:     */     }
/*  196:     */   }
/*  197:     */   
/*  198:     */   private void processArrayOfMaps(Object newInstance, FieldAccess field, Map<String, Object>[] maps)
/*  199:     */   {
/*  200: 317 */     List<Map<String, Object>> list = Lists.list(maps);
/*  201: 318 */     handleCollectionOfMaps(newInstance, field, list);
/*  202:     */   }
/*  203:     */   
/*  204:     */   private void handleCollectionOfMaps(Object newInstance, FieldAccess field, Collection<Map<String, Object>> collectionOfMaps)
/*  205:     */   {
/*  206: 334 */     Collection<Object> newCollection = Conversions.createCollection(field.type(), collectionOfMaps.size());
/*  207:     */     
/*  208:     */ 
/*  209: 337 */     Class<?> componentClass = field.getComponentClass();
/*  210: 339 */     if (componentClass != null)
/*  211:     */     {
/*  212: 342 */       for (Map<String, Object> mapComponent : collectionOfMaps) {
/*  213: 344 */         newCollection.add(fromMap(mapComponent, componentClass));
/*  214:     */       }
/*  215: 347 */       field.setObject(newInstance, newCollection);
/*  216:     */     }
/*  217:     */   }
/*  218:     */   
/*  219:     */   private <T> ConstructorAccess<T> lookupConstructorMeta(int size, List<Object> convertedArguments, ClassMeta<T> classMeta, ConstructorAccess<T> constructorToMatch, boolean[] flag, boolean loose)
/*  220:     */   {
/*  221: 366 */     for (ConstructorAccess constructor : classMeta.constructors())
/*  222:     */     {
/*  223: 369 */       Class[] parameterTypes = constructor.parameterTypes();
/*  224: 370 */       if (parameterTypes.length == size)
/*  225:     */       {
/*  226: 373 */         for (int index = 0;; index++)
/*  227:     */         {
/*  228: 373 */           if (index >= size) {
/*  229:     */             break label85;
/*  230:     */           }
/*  231: 375 */           if (!matchAndConvertArgs(convertedArguments, constructor, parameterTypes, index, flag, loose)) {
/*  232:     */             break;
/*  233:     */           }
/*  234:     */         }
/*  235: 378 */         constructorToMatch = constructor;
/*  236:     */       }
/*  237:     */     }
/*  238:     */     label85:
/*  239: 381 */     return constructorToMatch;
/*  240:     */   }
/*  241:     */   
/*  242:     */   private boolean matchAndConvertArgs(List<Object> convertedArgumentList, BaseAccess methodAccess, Class[] parameterTypes, int index, boolean[] flag, boolean loose)
/*  243:     */   {
/*  244: 411 */     Object value = null;
/*  245:     */     try
/*  246:     */     {
/*  247: 418 */       Class parameterClass = parameterTypes[index];
/*  248: 419 */       Object item = convertedArgumentList.get(index);
/*  249:     */       
/*  250:     */ 
/*  251: 422 */       TypeType parameterType = TypeType.getType(parameterClass);
/*  252: 425 */       if ((item instanceof ValueContainer))
/*  253:     */       {
/*  254: 426 */         item = ((ValueContainer)item).toValue();
/*  255:     */         
/*  256: 428 */         convertedArgumentList.set(index, item);
/*  257:     */       }
/*  258: 434 */       if (item == null) {
/*  259: 435 */         return true;
/*  260:     */       }
/*  261: 438 */       switch (1.$SwitchMap$org$boon$core$TypeType[parameterType.ordinal()])
/*  262:     */       {
/*  263:     */       case 5: 
/*  264:     */       case 6: 
/*  265:     */       case 7: 
/*  266:     */       case 8: 
/*  267:     */       case 9: 
/*  268:     */       case 10: 
/*  269:     */       case 11: 
/*  270:     */       case 23: 
/*  271: 447 */         if (item == null) {
/*  272: 448 */           return false;
/*  273:     */         }
/*  274:     */       case 4: 
/*  275:     */       case 12: 
/*  276:     */       case 13: 
/*  277:     */       case 14: 
/*  278:     */       case 15: 
/*  279:     */       case 16: 
/*  280:     */       case 17: 
/*  281:     */       case 18: 
/*  282:     */       case 21: 
/*  283:     */       case 24: 
/*  284: 463 */         if (!loose)
/*  285:     */         {
/*  286: 464 */           if ((item instanceof Number))
/*  287:     */           {
/*  288: 465 */             value = Conversions.coerceWithFlag(parameterType, parameterClass, flag, item);
/*  289: 466 */             convertedArgumentList.set(index, value);
/*  290:     */             
/*  291: 468 */             return flag[0];
/*  292:     */           }
/*  293: 470 */           return false;
/*  294:     */         }
/*  295: 474 */         value = Conversions.coerceWithFlag(parameterType, parameterClass, flag, item);
/*  296: 475 */         convertedArgumentList.set(index, value);
/*  297:     */         
/*  298: 477 */         return flag[0];
/*  299:     */       case 25: 
/*  300: 486 */         if ((item instanceof Enum)) {
/*  301: 487 */           return true;
/*  302:     */         }
/*  303: 490 */         if ((item instanceof CharSequence))
/*  304:     */         {
/*  305: 491 */           value = Conversions.toEnum(parameterClass, item.toString());
/*  306: 492 */           convertedArgumentList.set(index, value);
/*  307:     */           
/*  308: 494 */           return value != null;
/*  309:     */         }
/*  310: 496 */         if ((item instanceof Number))
/*  311:     */         {
/*  312: 497 */           value = Conversions.toEnum(parameterClass, ((Number)item).intValue());
/*  313: 498 */           convertedArgumentList.set(index, value);
/*  314:     */           
/*  315: 500 */           return value != null;
/*  316:     */         }
/*  317: 503 */         return false;
/*  318:     */       case 19: 
/*  319: 508 */         if ((item instanceof Class)) {
/*  320: 509 */           return true;
/*  321:     */         }
/*  322: 512 */         value = Conversions.coerceWithFlag(parameterType, parameterClass, flag, item);
/*  323: 513 */         convertedArgumentList.set(index, value);
/*  324:     */         
/*  325: 515 */         return flag[0];
/*  326:     */       case 22: 
/*  327: 520 */         if ((item instanceof String)) {
/*  328: 521 */           return true;
/*  329:     */         }
/*  330: 524 */         if ((item instanceof CharSequence))
/*  331:     */         {
/*  332: 526 */           value = item.toString();
/*  333: 527 */           convertedArgumentList.set(index, value);
/*  334: 528 */           return true;
/*  335:     */         }
/*  336: 531 */         if (loose)
/*  337:     */         {
/*  338: 533 */           value = item.toString();
/*  339: 534 */           convertedArgumentList.set(index, value);
/*  340: 535 */           return true;
/*  341:     */         }
/*  342: 537 */         return false;
/*  343:     */       case 2: 
/*  344:     */       case 3: 
/*  345: 543 */         if (!(item instanceof Map)) {
/*  346:     */           break label1718;
/*  347:     */         }
/*  348: 544 */         Map itemMap = (Map)item;
/*  349:     */         
/*  350:     */ 
/*  351:     */ 
/*  352:     */ 
/*  353:     */ 
/*  354: 550 */         Type type = methodAccess.getGenericParameterTypes()[index];
/*  355: 551 */         if ((type instanceof ParameterizedType))
/*  356:     */         {
/*  357: 552 */           ParameterizedType pType = (ParameterizedType)type;
/*  358: 553 */           Class<?> keyType = (Class)pType.getActualTypeArguments()[0];
/*  359:     */           
/*  360: 555 */           Class<?> valueType = (Class)pType.getActualTypeArguments()[1];
/*  361:     */           
/*  362:     */ 
/*  363: 558 */           Map newMap = Conversions.createMap(parameterClass, itemMap.size());
/*  364: 565 */           for (Object o : itemMap.entrySet())
/*  365:     */           {
/*  366: 566 */             Map.Entry entry = (Map.Entry)o;
/*  367:     */             
/*  368: 568 */             Object key = entry.getKey();
/*  369: 569 */             value = entry.getValue();
/*  370:     */             
/*  371: 571 */             key = ValueContainer.toObject(key);
/*  372:     */             
/*  373: 573 */             value = ValueContainer.toObject(value);
/*  374: 579 */             if ((value instanceof List)) {
/*  375: 580 */               value = fromList((List)value, valueType);
/*  376: 582 */             } else if ((value instanceof Map)) {
/*  377: 583 */               value = fromMap((Map)value, valueType);
/*  378:     */             } else {
/*  379: 586 */               value = Conversions.coerce(valueType, value);
/*  380:     */             }
/*  381: 590 */             if ((key instanceof List)) {
/*  382: 591 */               key = fromList((List)key, keyType);
/*  383: 593 */             } else if ((value instanceof Map)) {
/*  384: 594 */               key = fromMap((Map)key, keyType);
/*  385:     */             } else {
/*  386: 597 */               key = Conversions.coerce(keyType, key);
/*  387:     */             }
/*  388: 600 */             newMap.put(key, value);
/*  389:     */           }
/*  390: 602 */           convertedArgumentList.set(index, newMap);
/*  391: 603 */           return true;
/*  392:     */         }
/*  393: 605 */         break;
/*  394:     */       case 26: 
/*  395: 608 */         if (parameterClass.isInstance(item)) {
/*  396: 609 */           return true;
/*  397:     */         }
/*  398: 612 */         if ((item instanceof Map))
/*  399:     */         {
/*  400: 613 */           item = fromMap((Map)item, parameterClass);
/*  401: 614 */           convertedArgumentList.set(index, item);
/*  402: 615 */           return true;
/*  403:     */         }
/*  404: 616 */         if ((item instanceof List))
/*  405:     */         {
/*  406: 618 */           List<Object> listItem = null;
/*  407:     */           
/*  408: 620 */           listItem = (List)item;
/*  409:     */           
/*  410: 622 */           value = fromList(listItem, parameterClass);
/*  411:     */           
/*  412: 624 */           convertedArgumentList.set(index, value);
/*  413: 625 */           return true;
/*  414:     */         }
/*  415: 628 */         convertedArgumentList.set(index, Conversions.coerce(parameterClass, item));
/*  416: 629 */         return true;
/*  417:     */       case 27: 
/*  418:     */       case 28: 
/*  419: 634 */         if (parameterClass.isInstance(item)) {
/*  420: 635 */           return true;
/*  421:     */         }
/*  422: 638 */         if (!(item instanceof Map)) {
/*  423:     */           break label1718;
/*  424:     */         }
/*  425: 641 */         String className = (String)((Map)item).get("class");
/*  426: 642 */         if (className != null)
/*  427:     */         {
/*  428: 643 */           item = fromMap((Map)item, Reflection.loadClass(className));
/*  429: 644 */           convertedArgumentList.set(index, item);
/*  430: 645 */           return true;
/*  431:     */         }
/*  432: 647 */         return false;
/*  433:     */       case 29: 
/*  434:     */       case 30: 
/*  435:     */       case 31: 
/*  436:     */       case 32: 
/*  437:     */       case 33: 
/*  438:     */       case 34: 
/*  439:     */       case 35: 
/*  440:     */       case 36: 
/*  441:     */       case 37: 
/*  442: 664 */         item = Conversions.toList(item);
/*  443:     */       case 1: 
/*  444:     */       case 38: 
/*  445:     */       case 39: 
/*  446: 668 */         if ((item instanceof List))
/*  447:     */         {
/*  448: 670 */           List<Object> itemList = (List)item;
/*  449: 676 */           if ((itemList.size() > 0) && (((itemList.get(0) instanceof List)) || ((itemList.get(0) instanceof ValueContainer))))
/*  450:     */           {
/*  451: 680 */             Type type = methodAccess.getGenericParameterTypes()[index];
/*  452: 685 */             if ((type instanceof ParameterizedType))
/*  453:     */             {
/*  454: 686 */               ParameterizedType pType = (ParameterizedType)type;
/*  455:     */               Class<?> componentType;
/*  456:     */               Class<?> componentType;
/*  457: 690 */               if (!(pType.getActualTypeArguments()[0] instanceof Class)) {
/*  458: 691 */                 componentType = Object.class;
/*  459:     */               } else {
/*  460: 693 */                 componentType = (Class)pType.getActualTypeArguments()[0];
/*  461:     */               }
/*  462: 696 */               Collection newList = Conversions.createCollection(parameterClass, itemList.size());
/*  463: 698 */               for (Object o : itemList)
/*  464:     */               {
/*  465: 699 */                 if ((o instanceof ValueContainer)) {
/*  466: 700 */                   o = ((ValueContainer)o).toValue();
/*  467:     */                 }
/*  468: 703 */                 if (componentType == Object.class)
/*  469:     */                 {
/*  470: 704 */                   newList.add(o);
/*  471:     */                 }
/*  472:     */                 else
/*  473:     */                 {
/*  474: 707 */                   List fromList = (List)o;
/*  475: 708 */                   o = fromList(fromList, componentType);
/*  476: 709 */                   newList.add(o);
/*  477:     */                 }
/*  478:     */               }
/*  479: 712 */               convertedArgumentList.set(index, newList);
/*  480: 713 */               return true;
/*  481:     */             }
/*  482:     */           }
/*  483:     */           else
/*  484:     */           {
/*  485: 721 */             Type type = methodAccess.getGenericParameterTypes()[index];
/*  486: 722 */             if ((type instanceof ParameterizedType))
/*  487:     */             {
/*  488: 723 */               ParameterizedType pType = (ParameterizedType)type;
/*  489:     */               
/*  490: 725 */               Class<?> componentType = (pType.getActualTypeArguments()[0] instanceof Class) ? (Class)pType.getActualTypeArguments()[0] : Object.class;
/*  491:     */               
/*  492: 727 */               Collection newList = Conversions.createCollection(parameterClass, itemList.size());
/*  493: 730 */               for (Object o : itemList)
/*  494:     */               {
/*  495: 731 */                 if ((o instanceof ValueContainer)) {
/*  496: 732 */                   o = ((ValueContainer)o).toValue();
/*  497:     */                 }
/*  498: 734 */                 if ((o instanceof List))
/*  499:     */                 {
/*  500: 736 */                   if (componentType != Object.class)
/*  501:     */                   {
/*  502: 738 */                     List fromList = (List)o;
/*  503: 739 */                     o = fromList(fromList, componentType);
/*  504:     */                   }
/*  505: 741 */                   newList.add(o);
/*  506:     */                 }
/*  507: 742 */                 else if ((o instanceof Map))
/*  508:     */                 {
/*  509: 743 */                   Map fromMap = (Map)o;
/*  510: 744 */                   o = fromMap(fromMap, componentType);
/*  511: 745 */                   newList.add(o);
/*  512:     */                 }
/*  513:     */                 else
/*  514:     */                 {
/*  515: 748 */                   newList.add(Conversions.coerce(componentType, o));
/*  516:     */                 }
/*  517:     */               }
/*  518: 751 */               convertedArgumentList.set(index, newList);
/*  519: 752 */               return true;
/*  520:     */             }
/*  521:     */           }
/*  522:     */         }
/*  523: 758 */         return false;
/*  524:     */       }
/*  525: 762 */       TypeType itemType = TypeType.getInstanceType(item);
/*  526: 764 */       switch (1.$SwitchMap$org$boon$core$TypeType[itemType.ordinal()])
/*  527:     */       {
/*  528:     */       case 1: 
/*  529: 766 */         convertedArgumentList.set(index, fromList((List)item, parameterClass));
/*  530: 767 */         return true;
/*  531:     */       case 2: 
/*  532:     */       case 3: 
/*  533: 770 */         convertedArgumentList.set(index, fromMap((Map)item, parameterClass));
/*  534: 771 */         return true;
/*  535:     */       case 4: 
/*  536:     */       case 5: 
/*  537:     */       case 6: 
/*  538:     */       case 7: 
/*  539:     */       case 8: 
/*  540:     */       case 9: 
/*  541:     */       case 10: 
/*  542:     */       case 11: 
/*  543:     */       case 12: 
/*  544:     */       case 13: 
/*  545:     */       case 14: 
/*  546:     */       case 15: 
/*  547:     */       case 16: 
/*  548:     */       case 17: 
/*  549:     */       case 18: 
/*  550:     */       case 19: 
/*  551:     */       case 20: 
/*  552: 790 */         value = Conversions.coerceWithFlag(parameterClass, flag, item);
/*  553: 792 */         if (flag[0] == 0) {
/*  554: 793 */           return false;
/*  555:     */         }
/*  556: 795 */         convertedArgumentList.set(index, value);
/*  557: 796 */         return true;
/*  558:     */       case 21: 
/*  559:     */       case 22: 
/*  560: 803 */         value = Conversions.coerceWithFlag(parameterClass, flag, item);
/*  561: 805 */         if (flag[0] == 0) {
/*  562: 806 */           return false;
/*  563:     */         }
/*  564: 808 */         convertedArgumentList.set(index, value);
/*  565: 809 */         return true;
/*  566:     */       }
/*  567:     */       label1718:
/*  568: 820 */       if (parameterClass.isInstance(item)) {
/*  569: 821 */         return true;
/*  570:     */       }
/*  571:     */     }
/*  572:     */     catch (Exception ex)
/*  573:     */     {
/*  574: 826 */       Boon.error(ex, new Object[] { "PROBLEM WITH oldMatchAndConvertArgs", "fieldsAccessor", this.fieldsAccessor, "list", convertedArgumentList, "constructor", methodAccess, "parameters", parameterTypes, "index", Integer.valueOf(index) });
/*  575:     */       
/*  576:     */ 
/*  577:     */ 
/*  578: 830 */       return false;
/*  579:     */     }
/*  580: 833 */     return false;
/*  581:     */   }
/*  582:     */   
/*  583:     */   private void handleCollectionOfValues(Object newInstance, FieldAccess field, Collection<Value> acollectionOfValues)
/*  584:     */   {
/*  585: 849 */     Collection collectionOfValues = acollectionOfValues;
/*  586: 850 */     if (null == collectionOfValues)
/*  587:     */     {
/*  588: 851 */       field.setObject(newInstance, null);
/*  589: 852 */       return;
/*  590:     */     }
/*  591: 855 */     if (field.typeEnum() == TypeType.INSTANCE)
/*  592:     */     {
/*  593: 857 */       field.setObject(newInstance, fromList((List)acollectionOfValues, field.type()));
/*  594: 858 */       return;
/*  595:     */     }
/*  596: 862 */     if ((collectionOfValues instanceof ValueList)) {
/*  597: 863 */       collectionOfValues = ((ValueList)collectionOfValues).list();
/*  598:     */     }
/*  599: 867 */     Class<?> componentClass = field.getComponentClass();
/*  600: 874 */     switch (1.$SwitchMap$org$boon$core$TypeType[field.typeEnum().ordinal()])
/*  601:     */     {
/*  602:     */     case 1: 
/*  603:     */     case 38: 
/*  604:     */     case 39: 
/*  605: 882 */       Collection<Object> newCollection = Conversions.createCollection(field.type(), collectionOfValues.size());
/*  606: 885 */       for (Value value : (List)collectionOfValues) {
/*  607: 887 */         if (value.isContainer())
/*  608:     */         {
/*  609: 888 */           Object oValue = value.toValue();
/*  610: 889 */           if ((oValue instanceof Map)) {
/*  611: 890 */             newCollection.add(fromValueMap((Map)oValue, componentClass));
/*  612:     */           }
/*  613:     */         }
/*  614:     */         else
/*  615:     */         {
/*  616: 893 */           newCollection.add(Conversions.coerce(componentClass, value.toValue()));
/*  617:     */         }
/*  618:     */       }
/*  619: 898 */       field.setObject(newInstance, newCollection);
/*  620: 899 */       break;
/*  621:     */     case 29: 
/*  622:     */     case 30: 
/*  623:     */     case 31: 
/*  624:     */     case 32: 
/*  625:     */     case 33: 
/*  626:     */     case 34: 
/*  627:     */     case 35: 
/*  628:     */     case 36: 
/*  629:     */     case 37: 
/*  630: 912 */       TypeType componentType = field.componentType();
/*  631: 913 */       int index = 0;
/*  632: 915 */       switch (1.$SwitchMap$org$boon$core$TypeType[componentType.ordinal()])
/*  633:     */       {
/*  634:     */       case 6: 
/*  635: 917 */         int[] iarray = new int[collectionOfValues.size()];
/*  636: 918 */         for (Value value : (List)collectionOfValues)
/*  637:     */         {
/*  638: 919 */           iarray[index] = value.intValue();
/*  639: 920 */           index++;
/*  640:     */         }
/*  641: 923 */         field.setObject(newInstance, iarray);
/*  642: 924 */         return;
/*  643:     */       case 7: 
/*  644: 926 */         short[] sarray = new short[collectionOfValues.size()];
/*  645: 927 */         for (Value value : (List)collectionOfValues)
/*  646:     */         {
/*  647: 928 */           sarray[index] = value.shortValue();
/*  648: 929 */           index++;
/*  649:     */         }
/*  650: 932 */         field.setObject(newInstance, sarray);
/*  651: 933 */         return;
/*  652:     */       case 10: 
/*  653: 935 */         double[] darray = new double[collectionOfValues.size()];
/*  654: 936 */         for (Value value : (List)collectionOfValues)
/*  655:     */         {
/*  656: 937 */           darray[index] = value.doubleValue();
/*  657: 938 */           index++;
/*  658:     */         }
/*  659: 941 */         field.setObject(newInstance, darray);
/*  660: 942 */         return;
/*  661:     */       case 9: 
/*  662: 944 */         float[] farray = new float[collectionOfValues.size()];
/*  663: 945 */         for (Value value : (List)collectionOfValues)
/*  664:     */         {
/*  665: 946 */           farray[index] = value.floatValue();
/*  666: 947 */           index++;
/*  667:     */         }
/*  668: 950 */         field.setObject(newInstance, farray);
/*  669: 951 */         return;
/*  670:     */       case 11: 
/*  671: 954 */         long[] larray = new long[collectionOfValues.size()];
/*  672: 955 */         for (Value value : (List)collectionOfValues)
/*  673:     */         {
/*  674: 956 */           larray[index] = value.longValue();
/*  675: 957 */           index++;
/*  676:     */         }
/*  677: 960 */         field.setObject(newInstance, larray);
/*  678: 961 */         return;
/*  679:     */       case 8: 
/*  680: 965 */         byte[] barray = new byte[collectionOfValues.size()];
/*  681: 966 */         for (Value value : (List)collectionOfValues)
/*  682:     */         {
/*  683: 967 */           barray[index] = value.byteValue();
/*  684: 968 */           index++;
/*  685:     */         }
/*  686: 971 */         field.setObject(newInstance, barray);
/*  687: 972 */         return;
/*  688:     */       case 23: 
/*  689: 976 */         char[] chars = new char[collectionOfValues.size()];
/*  690: 977 */         for (Value value : (List)collectionOfValues)
/*  691:     */         {
/*  692: 978 */           chars[index] = value.charValue();
/*  693: 979 */           index++;
/*  694:     */         }
/*  695: 981 */         field.setObject(newInstance, chars);
/*  696: 982 */         return;
/*  697:     */       case 22: 
/*  698: 985 */         CharBuf buffer = CharBuf.create(100);
/*  699: 986 */         String[] strings = new String[collectionOfValues.size()];
/*  700: 987 */         for (Value value : (List)collectionOfValues)
/*  701:     */         {
/*  702: 988 */           strings[index] = value.stringValue(buffer);
/*  703: 989 */           index++;
/*  704:     */         }
/*  705: 991 */         field.setObject(newInstance, strings);
/*  706: 992 */         return;
/*  707:     */       }
/*  708: 996 */       Object array = Array.newInstance(componentClass, collectionOfValues.size());
/*  709: 999 */       for (Value value : (List)collectionOfValues)
/*  710:     */       {
/*  711:1000 */         if ((value instanceof ValueContainer))
/*  712:     */         {
/*  713:1001 */           Object o = value.toValue();
/*  714:1002 */           if ((o instanceof List))
/*  715:     */           {
/*  716:1003 */             o = fromList((List)o, componentClass);
/*  717:1004 */             if (!componentClass.isInstance(o)) {
/*  718:     */               break;
/*  719:     */             }
/*  720:1005 */             Array.set(array, index, o);
/*  721:     */           }
/*  722:1009 */           else if ((o instanceof Map))
/*  723:     */           {
/*  724:1010 */             o = fromMap((Map)o, componentClass);
/*  725:1011 */             if (!componentClass.isInstance(o)) {
/*  726:     */               break;
/*  727:     */             }
/*  728:1012 */             Array.set(array, index, o);
/*  729:     */           }
/*  730:     */         }
/*  731:     */         else
/*  732:     */         {
/*  733:1018 */           Object o = value.toValue();
/*  734:1019 */           if (componentClass.isInstance(o)) {
/*  735:1020 */             Array.set(array, index, o);
/*  736:     */           } else {
/*  737:1022 */             Array.set(array, index, Conversions.coerce(componentClass, o));
/*  738:     */           }
/*  739:     */         }
/*  740:1025 */         index++;
/*  741:     */       }
/*  742:1027 */       field.setValue(newInstance, array);
/*  743:     */     }
/*  744:     */   }
/*  745:     */   
/*  746:     */   public Object fromValueMap(Map<String, Value> valueMap)
/*  747:     */   {
/*  748:     */     try
/*  749:     */     {
/*  750:1050 */       String className = ((Value)valueMap.get("class")).toString();
/*  751:1051 */       Class<?> cls = Reflection.loadClass(className);
/*  752:1052 */       return fromValueMap(valueMap, cls);
/*  753:     */     }
/*  754:     */     catch (Exception ex)
/*  755:     */     {
/*  756:1054 */       return Exceptions.handle(Object.class, Boon.sputs(new Object[] { "fromValueMap", "map", valueMap, "fieldAccessor", this.fieldsAccessor }), ex);
/*  757:     */     }
/*  758:     */   }
/*  759:     */   
/*  760:     */   public <T> T fromValueMap(Map<String, Value> valueMap, Class<T> cls)
/*  761:     */   {
/*  762:1074 */     T newInstance = Reflection.newInstance(cls);
/*  763:1075 */     ValueMap map = (ValueMap)valueMap;
/*  764:     */     
/*  765:     */ 
/*  766:1078 */     Map<String, FieldAccess> fields = this.fieldsAccessor.getFields(cls);
/*  767:     */     
/*  768:     */ 
/*  769:1081 */     FieldAccess field = null;
/*  770:1082 */     String fieldName = null;
/*  771:     */     Map.Entry<String, Object>[] entries;
/*  772:     */     int size;
/*  773:     */     Map.Entry<String, Object>[] entries;
/*  774:1090 */     if (!map.hydrated())
/*  775:     */     {
/*  776:1091 */       int size = map.len();
/*  777:1092 */       entries = map.items();
/*  778:     */     }
/*  779:     */     else
/*  780:     */     {
/*  781:1094 */       size = map.size();
/*  782:1095 */       entries = (Map.Entry[])map.entrySet().toArray(new Map.Entry[size]);
/*  783:     */     }
/*  784:1100 */     if ((size == 0) || (entries == null)) {
/*  785:1101 */       return newInstance;
/*  786:     */     }
/*  787:1106 */     for (int index = 0; index < size; index++)
/*  788:     */     {
/*  789:1107 */       Object value = null;
/*  790:     */       try
/*  791:     */       {
/*  792:1110 */         Map.Entry<String, Object> entry = entries[index];
/*  793:     */         
/*  794:1112 */         fieldName = (String)entry.getKey();
/*  795:     */         
/*  796:     */ 
/*  797:     */ 
/*  798:1116 */         field = (FieldAccess)fields.get(this.fieldsAccessor.isCaseInsensitive() ? fieldName.toLowerCase() : fieldName);
/*  799:1119 */         if (field != null) {
/*  800:1125 */           if (!field.ignore())
/*  801:     */           {
/*  802:1130 */             value = entry.getValue();
/*  803:1133 */             if ((value instanceof Value)) {
/*  804:1134 */               fromValueMapHandleValueCase(newInstance, field, (Value)value);
/*  805:     */             } else {
/*  806:1136 */               fromMapHandleNonValueCase(newInstance, field, value);
/*  807:     */             }
/*  808:     */           }
/*  809:     */         }
/*  810:     */       }
/*  811:     */       catch (Exception ex)
/*  812:     */       {
/*  813:1139 */         return Exceptions.handle(Object.class, ex, new Object[] { "fieldName", fieldName, "of class", cls, "had issues for value", value, "for field", field });
/*  814:     */       }
/*  815:     */     }
/*  816:1144 */     return newInstance;
/*  817:     */   }
/*  818:     */   
/*  819:     */   private <T> void fromMapHandleNonValueCase(T newInstance, FieldAccess field, Object objectValue)
/*  820:     */   {
/*  821:     */     try
/*  822:     */     {
/*  823:1161 */       if ((objectValue instanceof Map))
/*  824:     */       {
/*  825:1162 */         Class<?> clazz = field.type();
/*  826:1163 */         if ((!clazz.isInterface()) && (!Typ.isAbstract(clazz)))
/*  827:     */         {
/*  828:1164 */           objectValue = fromValueMap((Map)objectValue, field.type());
/*  829:     */         }
/*  830:     */         else
/*  831:     */         {
/*  832:1166 */           String className = ((Value)((Map)objectValue).get("class")).toString();
/*  833:     */           
/*  834:1168 */           Class<?> cls = Reflection.loadClass(className);
/*  835:     */           
/*  836:1170 */           objectValue = fromValueMap((Map)objectValue, cls);
/*  837:     */         }
/*  838:1172 */         field.setValue(newInstance, objectValue);
/*  839:     */       }
/*  840:1173 */       else if ((objectValue instanceof Collection))
/*  841:     */       {
/*  842:1174 */         handleCollectionOfValues(newInstance, field, (Collection)objectValue);
/*  843:     */       }
/*  844:     */       else
/*  845:     */       {
/*  846:1177 */         field.setValue(newInstance, objectValue);
/*  847:     */       }
/*  848:     */     }
/*  849:     */     catch (Exception ex)
/*  850:     */     {
/*  851:1180 */       Exceptions.handle(Boon.sputs(new Object[] { "Problem handling non value case of fromValueMap", "field", field.name(), "fieldType", field.type().getName(), "object from map", objectValue }), ex);
/*  852:     */     }
/*  853:     */   }
/*  854:     */   
/*  855:     */   private <T> void fromValueMapHandleValueCase(T newInstance, FieldAccess field, Value value)
/*  856:     */   {
/*  857:1202 */     Object objValue = ValueContainer.toObject(value);
/*  858:     */     
/*  859:     */ 
/*  860:1205 */     Class<?> clazz = field.type();
/*  861:1208 */     switch (1.$SwitchMap$org$boon$core$TypeType[field.typeEnum().ordinal()])
/*  862:     */     {
/*  863:     */     case 27: 
/*  864:     */     case 28: 
/*  865:     */     case 40: 
/*  866:1213 */       if ((objValue instanceof Map))
/*  867:     */       {
/*  868:1214 */         Map<String, Value> valueMap = (Map)objValue;
/*  869:     */         
/*  870:1216 */         Value aClass = (Value)valueMap.get("class");
/*  871:1217 */         clazz = Reflection.loadClass(aClass.stringValue());
/*  872:     */       }
/*  873:     */     case 26: 
/*  874:1221 */       switch (1.$SwitchMap$org$boon$core$TypeType[value.type().ordinal()])
/*  875:     */       {
/*  876:     */       case 2: 
/*  877:1223 */         objValue = fromValueMap((Map)objValue, clazz);
/*  878:1224 */         break;
/*  879:     */       case 1: 
/*  880:1226 */         objValue = fromList((List)objValue, clazz);
/*  881:     */       }
/*  882:1231 */       field.setValue(newInstance, objValue);
/*  883:     */       
/*  884:1233 */       break;
/*  885:     */     case 2: 
/*  886:     */     case 3: 
/*  887:1239 */       Class keyType = (Class)field.getParameterizedType().getActualTypeArguments()[0];
/*  888:1240 */       Class valueType = (Class)field.getParameterizedType().getActualTypeArguments()[1];
/*  889:     */       
/*  890:1242 */       Map mapInner = (Map)objValue;
/*  891:1243 */       Set<Map.Entry> set = mapInner.entrySet();
/*  892:1244 */       Map newMap = new LinkedHashMap();
/*  893:1246 */       for (Map.Entry entry : set)
/*  894:     */       {
/*  895:1247 */         Object evalue = entry.getValue();
/*  896:     */         
/*  897:1249 */         Object key = entry.getKey();
/*  898:1251 */         if ((evalue instanceof ValueContainer)) {
/*  899:1252 */           evalue = ((ValueContainer)evalue).toValue();
/*  900:     */         }
/*  901:1255 */         key = Conversions.coerce(keyType, key);
/*  902:1256 */         evalue = Conversions.coerce(valueType, evalue);
/*  903:1257 */         newMap.put(key, evalue);
/*  904:     */       }
/*  905:1260 */       objValue = newMap;
/*  906:     */       
/*  907:1262 */       field.setValue(newInstance, objValue);
/*  908:     */       
/*  909:1264 */       break;
/*  910:     */     case 1: 
/*  911:     */     case 29: 
/*  912:     */     case 30: 
/*  913:     */     case 31: 
/*  914:     */     case 32: 
/*  915:     */     case 33: 
/*  916:     */     case 34: 
/*  917:     */     case 35: 
/*  918:     */     case 36: 
/*  919:     */     case 37: 
/*  920:     */     case 38: 
/*  921:     */     case 39: 
/*  922:1278 */       handleCollectionOfValues(newInstance, field, (Collection)objValue);
/*  923:     */       
/*  924:     */ 
/*  925:1281 */       break;
/*  926:     */     }
/*  927:1284 */     field.setFromValue(newInstance, value);
/*  928:     */   }
/*  929:     */   
/*  930:     */   private void setFieldValueFromMap(Object parentObject, FieldAccess field, Map mapInner)
/*  931:     */   {
/*  932:1299 */     Class<?> fieldClassType = field.type();
/*  933:1300 */     Object value = null;
/*  934:1303 */     if (!Typ.isMap(fieldClassType))
/*  935:     */     {
/*  936:1305 */       if ((!fieldClassType.isInterface()) && (!Typ.isAbstract(fieldClassType)))
/*  937:     */       {
/*  938:1306 */         value = fromMap(mapInner, field.type());
/*  939:     */       }
/*  940:     */       else
/*  941:     */       {
/*  942:1309 */         Object oClassName = mapInner.get("class");
/*  943:1310 */         if (oClassName != null) {
/*  944:1311 */           value = fromMap(mapInner, Reflection.loadClass(oClassName.toString()));
/*  945:     */         } else {
/*  946:1313 */           value = null;
/*  947:     */         }
/*  948:     */       }
/*  949:     */     }
/*  950:1323 */     else if (Typ.isMap(fieldClassType))
/*  951:     */     {
/*  952:     */       Class valueType;
/*  953:     */       Class keyType;
/*  954:     */       Class valueType;
/*  955:1328 */       if (field.getParameterizedType() == null)
/*  956:     */       {
/*  957:1329 */         Class keyType = String.class;
/*  958:1330 */         valueType = Object.class;
/*  959:     */       }
/*  960:     */       else
/*  961:     */       {
/*  962:1332 */         keyType = (Class)field.getParameterizedType().getActualTypeArguments()[0];
/*  963:1333 */         valueType = (Class)field.getParameterizedType().getActualTypeArguments()[1];
/*  964:     */       }
/*  965:1336 */       Set<Map.Entry> set = mapInner.entrySet();
/*  966:1337 */       Map newMap = new LinkedHashMap();
/*  967:1339 */       for (Map.Entry entry : set)
/*  968:     */       {
/*  969:1340 */         Object evalue = entry.getValue();
/*  970:     */         
/*  971:1342 */         Object key = entry.getKey();
/*  972:1344 */         if ((evalue instanceof ValueContainer)) {
/*  973:1345 */           evalue = ((ValueContainer)evalue).toValue();
/*  974:     */         }
/*  975:1348 */         key = Conversions.coerce(keyType, key);
/*  976:1349 */         evalue = Conversions.coerce(valueType, evalue);
/*  977:1350 */         newMap.put(key, evalue);
/*  978:     */       }
/*  979:1353 */       value = newMap;
/*  980:     */     }
/*  981:1357 */     field.setValue(parentObject, value);
/*  982:     */   }
/*  983:     */   
/*  984:     */   private void processCollectionFromMapUsingFields(Object newInstance, FieldAccess field, Collection<?> collection)
/*  985:     */   {
/*  986:1373 */     Class<?> fieldComponentClass = field.getComponentClass();
/*  987:     */     
/*  988:1375 */     Class<?> valueComponentClass = Reflection.getComponentType(collection);
/*  989:1381 */     if (Typ.isMap(valueComponentClass))
/*  990:     */     {
/*  991:1382 */       handleCollectionOfMaps(newInstance, field, collection);
/*  992:     */       
/*  993:1384 */       return;
/*  994:     */     }
/*  995:1389 */     if (Typ.isValue(valueComponentClass))
/*  996:     */     {
/*  997:1390 */       handleCollectionOfValues(newInstance, field, collection);
/*  998:     */       
/*  999:1392 */       return;
/* 1000:     */     }
/* 1001:1400 */     if (Typ.implementsInterface(collection.getClass(), field.type())) {
/* 1002:1402 */       if ((fieldComponentClass != null) && (fieldComponentClass.isAssignableFrom(valueComponentClass)))
/* 1003:     */       {
/* 1004:1403 */         field.setValue(newInstance, collection);
/* 1005:     */         
/* 1006:1405 */         return;
/* 1007:     */       }
/* 1008:     */     }
/* 1009:1424 */     if (!field.typeEnum().isCollection())
/* 1010:     */     {
/* 1011:1425 */       if ((collection instanceof List)) {
/* 1012:     */         try
/* 1013:     */         {
/* 1014:1427 */           Object value = fromList((List)collection, field.getComponentClass());
/* 1015:1428 */           field.setValue(newInstance, value);
/* 1016:     */         }
/* 1017:     */         catch (Exception ex)
/* 1018:     */         {
/* 1019:1431 */           field.setValue(newInstance, collection);
/* 1020:     */         }
/* 1021:     */       } else {
/* 1022:1434 */         field.setValue(newInstance, collection);
/* 1023:     */       }
/* 1024:1436 */       return;
/* 1025:     */     }
/* 1026:1445 */     Collection<Object> newCollection = Conversions.createCollection(field.type(), collection.size());
/* 1027:1447 */     if ((fieldComponentClass == null) || (fieldComponentClass.isAssignableFrom(valueComponentClass)))
/* 1028:     */     {
/* 1029:1449 */       newCollection.addAll(collection);
/* 1030:1450 */       field.setValue(newInstance, newCollection);
/* 1031:1451 */       return;
/* 1032:     */     }
/* 1033:1457 */     for (Object itemValue : collection)
/* 1034:     */     {
/* 1035:1458 */       newCollection.add(Conversions.coerce(fieldComponentClass, itemValue));
/* 1036:1459 */       field.setValue(newInstance, newCollection);
/* 1037:     */     }
/* 1038:     */   }
/* 1039:     */   
/* 1040:     */   public Object fromMap(Map<String, Object> map)
/* 1041:     */   {
/* 1042:1473 */     String clazz = (String)map.get("class");
/* 1043:1474 */     Class cls = Reflection.loadClass(clazz);
/* 1044:1475 */     return fromMap(map, cls);
/* 1045:     */   }
/* 1046:     */   
/* 1047:     */   public Map<String, Object> toMap(Object object)
/* 1048:     */   {
/* 1049:1490 */     if (object == null) {
/* 1050:1491 */       return null;
/* 1051:     */     }
/* 1052:1494 */     if ((object instanceof Map)) {
/* 1053:1495 */       return (Map)object;
/* 1054:     */     }
/* 1055:1497 */     Map<String, Object> map = new LinkedHashMap();
/* 1056:     */     
/* 1057:     */ 
/* 1058:     */ 
/* 1059:1501 */     Map<String, FieldAccess> fieldMap = Reflection.getAllAccessorFields(object.getClass());
/* 1060:1502 */     List<FieldAccess> fields = new ArrayList(fieldMap.values());
/* 1061:     */     
/* 1062:     */ 
/* 1063:1505 */     Collections.reverse(fields);
/* 1064:1508 */     for (FieldAccess access : fields)
/* 1065:     */     {
/* 1066:1510 */       String fieldName = access.name();
/* 1067:1512 */       if (!access.isStatic())
/* 1068:     */       {
/* 1069:1517 */         Object value = access.getValue(object);
/* 1070:1520 */         if (value != null) {
/* 1071:1525 */           switch (1.$SwitchMap$org$boon$core$TypeType[access.typeEnum().ordinal()])
/* 1072:     */           {
/* 1073:     */           case 5: 
/* 1074:     */           case 6: 
/* 1075:     */           case 7: 
/* 1076:     */           case 8: 
/* 1077:     */           case 9: 
/* 1078:     */           case 10: 
/* 1079:     */           case 11: 
/* 1080:     */           case 12: 
/* 1081:     */           case 13: 
/* 1082:     */           case 14: 
/* 1083:     */           case 15: 
/* 1084:     */           case 16: 
/* 1085:     */           case 17: 
/* 1086:     */           case 18: 
/* 1087:     */           case 23: 
/* 1088:     */           case 24: 
/* 1089:     */           case 41: 
/* 1090:     */           case 42: 
/* 1091:     */           case 43: 
/* 1092:     */           case 44: 
/* 1093:     */           case 45: 
/* 1094:1547 */             map.put(fieldName, value);
/* 1095:1548 */             break;
/* 1096:     */           case 29: 
/* 1097:     */           case 30: 
/* 1098:     */           case 31: 
/* 1099:     */           case 32: 
/* 1100:     */           case 33: 
/* 1101:     */           case 34: 
/* 1102:     */           case 35: 
/* 1103:     */           case 36: 
/* 1104:     */           case 37: 
/* 1105:1559 */             if (Typ.isBasicType(access.getComponentClass()))
/* 1106:     */             {
/* 1107:1560 */               map.put(fieldName, value);
/* 1108:     */             }
/* 1109:     */             else
/* 1110:     */             {
/* 1111:1562 */               int length = Arry.len(value);
/* 1112:1563 */               List<Map<String, Object>> list = new ArrayList(length);
/* 1113:1564 */               for (int index = 0; index < length; index++)
/* 1114:     */               {
/* 1115:1565 */                 Object item = Arry.fastIndex(value, index);
/* 1116:1566 */                 list.add(toMap(item));
/* 1117:     */               }
/* 1118:1568 */               map.put(fieldName, list);
/* 1119:     */             }
/* 1120:1570 */             break;
/* 1121:     */           case 1: 
/* 1122:     */           case 38: 
/* 1123:     */           case 39: 
/* 1124:1574 */             Collection<?> collection = (Collection)value;
/* 1125:1575 */             Class<?> componentType = access.getComponentClass();
/* 1126:1576 */             if (Typ.isBasicType(componentType))
/* 1127:     */             {
/* 1128:1577 */               map.put(fieldName, value);
/* 1129:     */             }
/* 1130:1578 */             else if (Typ.isEnum(componentType))
/* 1131:     */             {
/* 1132:1579 */               List<String> list = new ArrayList(collection.size());
/* 1133:1581 */               for (Object item : collection) {
/* 1134:1582 */                 if (item != null) {
/* 1135:1583 */                   list.add(item.toString());
/* 1136:     */                 }
/* 1137:     */               }
/* 1138:1586 */               map.put(fieldName, list);
/* 1139:     */             }
/* 1140:     */             else
/* 1141:     */             {
/* 1142:1589 */               List<Map<String, Object>> list = new ArrayList(collection.size());
/* 1143:1591 */               for (Object item : collection) {
/* 1144:1592 */                 if (item != null) {
/* 1145:1593 */                   list.add(toMap(item));
/* 1146:     */                 }
/* 1147:     */               }
/* 1148:1596 */               map.put(fieldName, list);
/* 1149:     */             }
/* 1150:1598 */             break;
/* 1151:     */           case 2: 
/* 1152:1600 */             map.put(fieldName, value);
/* 1153:1601 */             break;
/* 1154:     */           case 26: 
/* 1155:1604 */             map.put(fieldName, toMap(value));
/* 1156:1605 */             break;
/* 1157:     */           case 27: 
/* 1158:     */           case 28: 
/* 1159:1609 */             Map<String, Object> abstractMap = toMap(value);
/* 1160:1610 */             abstractMap.put("class", Boon.className(value));
/* 1161:1611 */             map.put(fieldName, abstractMap);
/* 1162:1612 */             break;
/* 1163:     */           case 25: 
/* 1164:1615 */             map.put(fieldName, value);
/* 1165:1616 */             break;
/* 1166:     */           case 3: 
/* 1167:     */           case 4: 
/* 1168:     */           case 19: 
/* 1169:     */           case 20: 
/* 1170:     */           case 21: 
/* 1171:     */           case 22: 
/* 1172:     */           case 40: 
/* 1173:     */           default: 
/* 1174:1622 */             map.put(fieldName, Conversions.toString(value));
/* 1175:     */           }
/* 1176:     */         }
/* 1177:     */       }
/* 1178:     */     }
/* 1179:1632 */     return map;
/* 1180:     */   }
/* 1181:     */   
/* 1182:     */   public List<Map<String, Object>> toListOfMaps(Collection<?> collection)
/* 1183:     */   {
/* 1184:1645 */     List<Map<String, Object>> list = new ArrayList();
/* 1185:1646 */     for (Object o : collection) {
/* 1186:1647 */       list.add(toMap(o));
/* 1187:     */     }
/* 1188:1649 */     return list;
/* 1189:     */   }
/* 1190:     */   
/* 1191:     */   public List<?> toList(Object object)
/* 1192:     */   {
/* 1193:1661 */     TypeType instanceType = TypeType.getInstanceType(object);
/* 1194:1663 */     switch (1.$SwitchMap$org$boon$core$TypeType[instanceType.ordinal()])
/* 1195:     */     {
/* 1196:     */     case 46: 
/* 1197:1665 */       return Lists.list(new Object[] { (Object)null });
/* 1198:     */     case 29: 
/* 1199:     */     case 30: 
/* 1200:     */     case 31: 
/* 1201:     */     case 32: 
/* 1202:     */     case 33: 
/* 1203:     */     case 34: 
/* 1204:     */     case 35: 
/* 1205:     */     case 36: 
/* 1206:     */     case 37: 
/* 1207:1676 */       return Conversions.toList(object);
/* 1208:     */     }
/* 1209:1679 */     return Lists.list(new Object[] { object });
/* 1210:     */   }
/* 1211:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.MapperSimple
 * JD-Core Version:    0.7.0.1
 */