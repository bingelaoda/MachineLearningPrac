/*    1:     */ package org.boon.core.reflection;
/*    2:     */ 
/*    3:     */ import java.lang.reflect.Array;
/*    4:     */ import java.lang.reflect.ParameterizedType;
/*    5:     */ import java.lang.reflect.Type;
/*    6:     */ import java.util.ArrayList;
/*    7:     */ import java.util.Arrays;
/*    8:     */ import java.util.Collection;
/*    9:     */ import java.util.Collections;
/*   10:     */ import java.util.LinkedHashMap;
/*   11:     */ import java.util.List;
/*   12:     */ import java.util.Map;
/*   13:     */ import java.util.Map.Entry;
/*   14:     */ import java.util.Set;
/*   15:     */ import org.boon.Boon;
/*   16:     */ import org.boon.Exceptions;
/*   17:     */ import org.boon.Lists;
/*   18:     */ import org.boon.core.Conversions;
/*   19:     */ import org.boon.core.Typ;
/*   20:     */ import org.boon.core.TypeType;
/*   21:     */ import org.boon.core.Value;
/*   22:     */ import org.boon.core.reflection.fields.FieldAccess;
/*   23:     */ import org.boon.core.reflection.fields.FieldAccessMode;
/*   24:     */ import org.boon.core.reflection.fields.FieldsAccessor;
/*   25:     */ import org.boon.core.reflection.fields.FieldsAccessorFieldThenProp;
/*   26:     */ import org.boon.core.value.ValueContainer;
/*   27:     */ import org.boon.core.value.ValueList;
/*   28:     */ import org.boon.core.value.ValueMap;
/*   29:     */ import org.boon.core.value.ValueMapImpl;
/*   30:     */ import org.boon.primitive.Arry;
/*   31:     */ import org.boon.primitive.CharBuf;
/*   32:     */ 
/*   33:     */ public class MapperComplex
/*   34:     */   implements Mapper
/*   35:     */ {
/*   36:     */   private final FieldsAccessor fieldsAccessor;
/*   37:     */   private final Set<String> ignoreSet;
/*   38:     */   private final String view;
/*   39:     */   private final boolean respectIgnore;
/*   40:     */   private final boolean acceptSingleValueAsArray;
/*   41:     */   private final boolean outputType;
/*   42:     */   
/*   43:     */   public MapperComplex(boolean outputType, FieldAccessMode fieldAccessType, boolean useAnnotations, boolean caseInsensitiveFields, Set<String> ignoreSet, String view, boolean respectIgnore, boolean acceptSingleValueAsArray)
/*   44:     */   {
/*   45:  78 */     this.fieldsAccessor = FieldAccessMode.create(fieldAccessType, useAnnotations, caseInsensitiveFields);
/*   46:  79 */     this.ignoreSet = ignoreSet;
/*   47:  80 */     this.view = view;
/*   48:  81 */     this.respectIgnore = respectIgnore;
/*   49:  82 */     this.acceptSingleValueAsArray = acceptSingleValueAsArray;
/*   50:  83 */     this.outputType = outputType;
/*   51:     */   }
/*   52:     */   
/*   53:     */   public MapperComplex(FieldAccessMode fieldAccessType, boolean useAnnotations, boolean caseInsensitiveFields, Set<String> ignoreSet, String view, boolean respectIgnore, boolean acceptSingleValueAsArray)
/*   54:     */   {
/*   55:  89 */     this.fieldsAccessor = FieldAccessMode.create(fieldAccessType, useAnnotations, caseInsensitiveFields);
/*   56:  90 */     this.ignoreSet = ignoreSet;
/*   57:  91 */     this.view = view;
/*   58:  92 */     this.respectIgnore = respectIgnore;
/*   59:  93 */     this.acceptSingleValueAsArray = acceptSingleValueAsArray;
/*   60:  94 */     this.outputType = true;
/*   61:     */   }
/*   62:     */   
/*   63:     */   public MapperComplex(FieldsAccessor fieldsAccessor, Set<String> ignoreSet, String view, boolean respectIgnore)
/*   64:     */   {
/*   65:  97 */     this.fieldsAccessor = fieldsAccessor;
/*   66:  98 */     this.ignoreSet = ignoreSet;
/*   67:  99 */     this.view = view;
/*   68: 100 */     this.respectIgnore = respectIgnore;
/*   69: 101 */     this.acceptSingleValueAsArray = false;
/*   70: 102 */     this.outputType = true;
/*   71:     */   }
/*   72:     */   
/*   73:     */   public MapperComplex(Set<String> ignoreSet, String view, boolean respectIgnore)
/*   74:     */   {
/*   75: 106 */     this.fieldsAccessor = new FieldsAccessorFieldThenProp(true);
/*   76: 107 */     this.ignoreSet = ignoreSet;
/*   77: 108 */     this.view = view;
/*   78: 109 */     this.respectIgnore = respectIgnore;
/*   79: 110 */     this.acceptSingleValueAsArray = false;
/*   80: 111 */     this.outputType = true;
/*   81:     */   }
/*   82:     */   
/*   83:     */   public MapperComplex(Set<String> ignoreSet)
/*   84:     */   {
/*   85: 116 */     this.fieldsAccessor = new FieldsAccessorFieldThenProp(true);
/*   86: 117 */     this.ignoreSet = ignoreSet;
/*   87: 118 */     this.view = null;
/*   88: 119 */     this.respectIgnore = true;
/*   89: 120 */     this.acceptSingleValueAsArray = false;
/*   90: 121 */     this.outputType = true;
/*   91:     */   }
/*   92:     */   
/*   93:     */   public MapperComplex(boolean acceptSingleValueAsArray)
/*   94:     */   {
/*   95: 125 */     this.fieldsAccessor = new FieldsAccessorFieldThenProp(true);
/*   96:     */     
/*   97: 127 */     this.ignoreSet = null;
/*   98: 128 */     this.view = null;
/*   99: 129 */     this.respectIgnore = true;
/*  100: 130 */     this.acceptSingleValueAsArray = acceptSingleValueAsArray;
/*  101: 131 */     this.outputType = true;
/*  102:     */   }
/*  103:     */   
/*  104:     */   public MapperComplex()
/*  105:     */   {
/*  106: 136 */     this.fieldsAccessor = new FieldsAccessorFieldThenProp(true);
/*  107:     */     
/*  108: 138 */     this.ignoreSet = null;
/*  109: 139 */     this.view = null;
/*  110: 140 */     this.respectIgnore = true;
/*  111: 141 */     this.acceptSingleValueAsArray = false;
/*  112: 142 */     this.outputType = true;
/*  113:     */   }
/*  114:     */   
/*  115:     */   public <T> List<T> convertListOfMapsToObjects(List<Map> list, Class<T> componentType)
/*  116:     */   {
/*  117: 156 */     List<Object> newList = new ArrayList(list.size());
/*  118: 157 */     for (Object obj : list)
/*  119:     */     {
/*  120: 159 */       if ((obj instanceof Value)) {
/*  121: 160 */         obj = ((Value)obj).toValue();
/*  122:     */       }
/*  123: 163 */       if ((obj instanceof Map))
/*  124:     */       {
/*  125: 165 */         Map map = (Map)obj;
/*  126: 166 */         if ((map instanceof ValueMapImpl)) {
/*  127: 167 */           newList.add(fromValueMap(map, componentType));
/*  128:     */         } else {
/*  129: 169 */           newList.add(fromMap(map, componentType));
/*  130:     */         }
/*  131:     */       }
/*  132:     */       else
/*  133:     */       {
/*  134: 172 */         newList.add(Conversions.coerce(componentType, obj));
/*  135:     */       }
/*  136:     */     }
/*  137: 175 */     return newList;
/*  138:     */   }
/*  139:     */   
/*  140:     */   public <T> T fromMap(Map<String, Object> map, Class<T> cls)
/*  141:     */   {
/*  142: 191 */     T toObject = Reflection.newInstance(cls);
/*  143: 192 */     Map<String, FieldAccess> fields = this.fieldsAccessor.getFields(toObject.getClass());
/*  144: 193 */     Set<Map.Entry<String, Object>> mapKeyValuesEntrySet = map.entrySet();
/*  145: 197 */     for (Map.Entry<String, Object> mapEntry : mapKeyValuesEntrySet)
/*  146:     */     {
/*  147: 200 */       String key = (String)mapEntry.getKey();
/*  148: 202 */       if ((this.ignoreSet == null) || 
/*  149: 203 */         (!this.ignoreSet.contains(key)))
/*  150:     */       {
/*  151: 209 */         FieldAccess field = (FieldAccess)fields.get(this.fieldsAccessor.isCaseInsensitive() ? key.toLowerCase() : key);
/*  152: 212 */         if ((field != null) && 
/*  153:     */         
/*  154:     */ 
/*  155:     */ 
/*  156:     */ 
/*  157:     */ 
/*  158:     */ 
/*  159: 219 */           ((this.view == null) || 
/*  160: 220 */           (field.isViewActive(this.view))) && (
/*  161:     */           
/*  162:     */ 
/*  163:     */ 
/*  164:     */ 
/*  165:     */ 
/*  166:     */ 
/*  167:     */ 
/*  168:     */ 
/*  169: 229 */           (!this.respectIgnore) || 
/*  170: 230 */           (!field.ignore())))
/*  171:     */         {
/*  172: 236 */           Object value = mapEntry.getValue();
/*  173: 242 */           if ((value instanceof Value)) {
/*  174: 243 */             if (((Value)value).isContainer())
/*  175:     */             {
/*  176: 244 */               value = ((Value)value).toValue();
/*  177:     */             }
/*  178:     */             else
/*  179:     */             {
/*  180: 246 */               field.setFromValue(toObject, (Value)value);
/*  181: 247 */               continue;
/*  182:     */             }
/*  183:     */           }
/*  184: 255 */           if (value == null) {
/*  185: 256 */             field.setObject(toObject, null);
/*  186: 263 */           } else if ((value.getClass() == field.type()) || (field.type() == Object.class)) {
/*  187: 264 */             field.setValue(toObject, value);
/*  188: 265 */           } else if (Typ.isBasicType(value)) {
/*  189: 267 */             field.setValue(toObject, value);
/*  190: 277 */           } else if ((value instanceof Map)) {
/*  191: 278 */             setFieldValueFromMap(toObject, field, (Map)value);
/*  192: 279 */           } else if ((value instanceof Collection)) {
/*  193: 281 */             processCollectionFromMapUsingFields(toObject, field, (Collection)value);
/*  194: 282 */           } else if ((value instanceof Map[])) {
/*  195: 284 */             processArrayOfMaps(toObject, field, (Map[])value);
/*  196:     */           } else {
/*  197: 290 */             field.setValue(toObject, value);
/*  198:     */           }
/*  199:     */         }
/*  200:     */       }
/*  201:     */     }
/*  202: 295 */     return toObject;
/*  203:     */   }
/*  204:     */   
/*  205:     */   public <T> T fromList(List<?> argList, Class<T> clazz)
/*  206:     */   {
/*  207: 315 */     int size = argList.size();
/*  208:     */     
/*  209:     */ 
/*  210:     */ 
/*  211: 319 */     ClassMeta<T> classMeta = ClassMeta.classMeta(clazz);
/*  212:     */     
/*  213:     */ 
/*  214: 322 */     ConstructorAccess<T> constructorToMatch = null;
/*  215:     */     
/*  216:     */ 
/*  217: 325 */     Object[] finalArgs = null;
/*  218:     */     
/*  219:     */ 
/*  220: 328 */     boolean[] flag = new boolean[1];
/*  221: 329 */     List<Object> convertedArguments = null;
/*  222:     */     try
/*  223:     */     {
/*  224: 336 */       convertedArguments = new ArrayList(argList);
/*  225:     */       
/*  226: 338 */       constructorToMatch = lookupConstructorMeta(size, convertedArguments, classMeta, constructorToMatch, flag, false);
/*  227: 344 */       if (constructorToMatch == null)
/*  228:     */       {
/*  229: 345 */         convertedArguments = new ArrayList(argList);
/*  230: 346 */         constructorToMatch = lookupConstructorMeta(size, convertedArguments, classMeta, constructorToMatch, flag, true);
/*  231:     */       }
/*  232: 354 */       if (constructorToMatch != null)
/*  233:     */       {
/*  234: 355 */         finalArgs = convertedArguments.toArray(new Object[argList.size()]);
/*  235: 356 */         return constructorToMatch.create(finalArgs);
/*  236:     */       }
/*  237: 358 */       return Exceptions.die(Object.class, new Object[] { "Unable to convert list", convertedArguments, "into", clazz });
/*  238:     */     }
/*  239:     */     catch (Exception e)
/*  240:     */     {
/*  241: 367 */       if (constructorToMatch != null)
/*  242:     */       {
/*  243: 370 */         CharBuf buf = CharBuf.create(200);
/*  244: 371 */         buf.addLine();
/*  245: 372 */         buf.multiply('-', 10).add("FINAL ARGUMENTS").multiply('-', 10).addLine();
/*  246: 373 */         if (finalArgs != null) {
/*  247: 374 */           for (Object o : finalArgs) {
/*  248: 375 */             buf.puts(new Object[] { "argument type    ", Boon.className(o) });
/*  249:     */           }
/*  250:     */         }
/*  251: 380 */         buf.multiply('-', 10).add("CONSTRUCTOR").add(constructorToMatch).multiply('-', 10).addLine();
/*  252: 381 */         buf.multiply('-', 10).add("CONSTRUCTOR PARAMS").multiply('-', 10).addLine();
/*  253: 382 */         for (Class<?> c : constructorToMatch.parameterTypes()) {
/*  254: 383 */           buf.puts(new Object[] { "constructor type ", c });
/*  255:     */         }
/*  256: 386 */         buf.multiply('-', 35).addLine();
/*  257: 388 */         if (Boon.debugOn()) {
/*  258: 389 */           Boon.puts(new Object[] { buf });
/*  259:     */         }
/*  260: 394 */         buf.addLine("PARAMETER TYPES");
/*  261: 395 */         buf.add(Lists.list(constructorToMatch.parameterTypes())).addLine();
/*  262:     */         
/*  263: 397 */         buf.addLine("ORIGINAL TYPES PASSED");
/*  264: 398 */         buf.add(TypeType.gatherTypes(convertedArguments)).addLine();
/*  265:     */         
/*  266: 400 */         buf.add(TypeType.gatherActualTypes(convertedArguments)).addLine();
/*  267:     */         
/*  268: 402 */         buf.addLine("CONVERTED ARGUMENT TYPES");
/*  269: 403 */         buf.add(TypeType.gatherTypes(convertedArguments)).addLine();
/*  270: 404 */         buf.add(TypeType.gatherActualTypes(convertedArguments)).addLine();
/*  271:     */         
/*  272: 406 */         Boon.error(e, new Object[] { "unable to create object based on constructor", buf });
/*  273:     */         
/*  274:     */ 
/*  275: 409 */         return Exceptions.handle(Object.class, e, new Object[] { buf.toString() });
/*  276:     */       }
/*  277: 411 */       return Exceptions.handle(Object.class, e, new Object[] { "\nlist args after conversion", convertedArguments, "types", TypeType.gatherTypes(convertedArguments), "\noriginal args", argList, "original types", TypeType.gatherTypes(argList) });
/*  278:     */     }
/*  279:     */   }
/*  280:     */   
/*  281:     */   private void processArrayOfMaps(Object newInstance, FieldAccess field, Map<String, Object>[] maps)
/*  282:     */   {
/*  283: 432 */     List<Map<String, Object>> list = Lists.list(maps);
/*  284: 433 */     handleCollectionOfMaps(newInstance, field, list);
/*  285:     */   }
/*  286:     */   
/*  287:     */   private void handleCollectionOfMaps(Object newInstance, FieldAccess field, Collection<Map<String, Object>> collectionOfMaps)
/*  288:     */   {
/*  289: 449 */     Collection<Object> newCollection = Conversions.createCollection(field.type(), collectionOfMaps.size());
/*  290:     */     
/*  291:     */ 
/*  292: 452 */     Class<?> componentClass = field.getComponentClass();
/*  293: 454 */     if (componentClass != null)
/*  294:     */     {
/*  295: 457 */       for (Map<String, Object> mapComponent : collectionOfMaps) {
/*  296: 459 */         newCollection.add(fromMap(mapComponent, componentClass));
/*  297:     */       }
/*  298: 462 */       field.setObject(newInstance, newCollection);
/*  299:     */     }
/*  300:     */   }
/*  301:     */   
/*  302:     */   private <T> ConstructorAccess<T> lookupConstructorMeta(int size, List<Object> convertedArguments, ClassMeta<T> classMeta, ConstructorAccess<T> constructorToMatch, boolean[] flag, boolean loose)
/*  303:     */   {
/*  304: 481 */     for (ConstructorAccess constructor : classMeta.constructors())
/*  305:     */     {
/*  306: 484 */       Class[] parameterTypes = constructor.parameterTypes();
/*  307: 485 */       if (parameterTypes.length == size)
/*  308:     */       {
/*  309: 488 */         for (int index = 0;; index++)
/*  310:     */         {
/*  311: 488 */           if (index >= size) {
/*  312:     */             break label85;
/*  313:     */           }
/*  314: 490 */           if (!matchAndConvertArgs(convertedArguments, constructor, parameterTypes, index, flag, loose)) {
/*  315:     */             break;
/*  316:     */           }
/*  317:     */         }
/*  318: 493 */         constructorToMatch = constructor;
/*  319:     */       }
/*  320:     */     }
/*  321:     */     label85:
/*  322: 496 */     return constructorToMatch;
/*  323:     */   }
/*  324:     */   
/*  325:     */   private boolean matchAndConvertArgs(List<Object> convertedArgumentList, BaseAccess methodAccess, Class[] parameterTypes, int index, boolean[] flag, boolean loose)
/*  326:     */   {
/*  327: 526 */     Object value = null;
/*  328:     */     try
/*  329:     */     {
/*  330: 533 */       Class parameterClass = parameterTypes[index];
/*  331: 534 */       Object item = convertedArgumentList.get(index);
/*  332:     */       
/*  333:     */ 
/*  334: 537 */       TypeType parameterType = TypeType.getType(parameterClass);
/*  335: 540 */       if ((item instanceof ValueContainer))
/*  336:     */       {
/*  337: 541 */         item = ((ValueContainer)item).toValue();
/*  338:     */         
/*  339: 543 */         convertedArgumentList.set(index, item);
/*  340:     */       }
/*  341: 549 */       if (item == null) {
/*  342: 550 */         return true;
/*  343:     */       }
/*  344: 553 */       switch (1.$SwitchMap$org$boon$core$TypeType[parameterType.ordinal()])
/*  345:     */       {
/*  346:     */       case 5: 
/*  347:     */       case 6: 
/*  348:     */       case 7: 
/*  349:     */       case 8: 
/*  350:     */       case 9: 
/*  351:     */       case 10: 
/*  352:     */       case 11: 
/*  353:     */       case 23: 
/*  354: 562 */         if (item == null) {
/*  355: 563 */           return false;
/*  356:     */         }
/*  357:     */       case 4: 
/*  358:     */       case 12: 
/*  359:     */       case 13: 
/*  360:     */       case 14: 
/*  361:     */       case 15: 
/*  362:     */       case 16: 
/*  363:     */       case 17: 
/*  364:     */       case 18: 
/*  365:     */       case 21: 
/*  366:     */       case 24: 
/*  367: 578 */         if (!loose)
/*  368:     */         {
/*  369: 579 */           if ((item instanceof Number))
/*  370:     */           {
/*  371: 580 */             value = Conversions.coerceWithFlag(parameterType, parameterClass, flag, item);
/*  372: 581 */             convertedArgumentList.set(index, value);
/*  373:     */             
/*  374: 583 */             return flag[0];
/*  375:     */           }
/*  376: 585 */           return false;
/*  377:     */         }
/*  378: 589 */         value = Conversions.coerceWithFlag(parameterType, parameterClass, flag, item);
/*  379: 590 */         convertedArgumentList.set(index, value);
/*  380:     */         
/*  381: 592 */         return flag[0];
/*  382:     */       case 25: 
/*  383: 601 */         if ((item instanceof Enum)) {
/*  384: 602 */           return true;
/*  385:     */         }
/*  386: 605 */         if ((item instanceof CharSequence))
/*  387:     */         {
/*  388: 606 */           value = Conversions.toEnum(parameterClass, item.toString());
/*  389: 607 */           convertedArgumentList.set(index, value);
/*  390:     */           
/*  391: 609 */           return value != null;
/*  392:     */         }
/*  393: 611 */         if ((item instanceof Number))
/*  394:     */         {
/*  395: 612 */           value = Conversions.toEnum(parameterClass, ((Number)item).intValue());
/*  396: 613 */           convertedArgumentList.set(index, value);
/*  397:     */           
/*  398: 615 */           return value != null;
/*  399:     */         }
/*  400: 618 */         return false;
/*  401:     */       case 19: 
/*  402: 623 */         if ((item instanceof Class)) {
/*  403: 624 */           return true;
/*  404:     */         }
/*  405: 627 */         value = Conversions.coerceWithFlag(parameterType, parameterClass, flag, item);
/*  406: 628 */         convertedArgumentList.set(index, value);
/*  407:     */         
/*  408: 630 */         return flag[0];
/*  409:     */       case 22: 
/*  410: 635 */         if ((item instanceof String)) {
/*  411: 636 */           return true;
/*  412:     */         }
/*  413: 639 */         if ((item instanceof CharSequence))
/*  414:     */         {
/*  415: 641 */           value = item.toString();
/*  416: 642 */           convertedArgumentList.set(index, value);
/*  417: 643 */           return true;
/*  418:     */         }
/*  419: 646 */         if (loose)
/*  420:     */         {
/*  421: 648 */           value = item.toString();
/*  422: 649 */           convertedArgumentList.set(index, value);
/*  423: 650 */           return true;
/*  424:     */         }
/*  425: 652 */         return false;
/*  426:     */       case 2: 
/*  427:     */       case 3: 
/*  428: 658 */         if (!(item instanceof Map)) {
/*  429:     */           break label1686;
/*  430:     */         }
/*  431: 659 */         Map itemMap = (Map)item;
/*  432:     */         
/*  433:     */ 
/*  434:     */ 
/*  435:     */ 
/*  436:     */ 
/*  437: 665 */         Type type = methodAccess.getGenericParameterTypes()[index];
/*  438: 666 */         if ((type instanceof ParameterizedType))
/*  439:     */         {
/*  440: 667 */           ParameterizedType pType = (ParameterizedType)type;
/*  441: 668 */           Class<?> keyType = (Class)pType.getActualTypeArguments()[0];
/*  442:     */           
/*  443: 670 */           Class<?> valueType = (Class)pType.getActualTypeArguments()[1];
/*  444:     */           
/*  445:     */ 
/*  446: 673 */           Map newMap = Conversions.createMap(parameterClass, itemMap.size());
/*  447: 680 */           for (Object o : itemMap.entrySet())
/*  448:     */           {
/*  449: 681 */             Map.Entry entry = (Map.Entry)o;
/*  450:     */             
/*  451: 683 */             Object key = entry.getKey();
/*  452: 684 */             value = entry.getValue();
/*  453:     */             
/*  454: 686 */             key = ValueContainer.toObject(key);
/*  455:     */             
/*  456: 688 */             value = ValueContainer.toObject(value);
/*  457: 694 */             if ((value instanceof List)) {
/*  458: 695 */               value = fromList((List)value, valueType);
/*  459: 697 */             } else if ((value instanceof Map)) {
/*  460: 698 */               value = fromMap((Map)value, valueType);
/*  461:     */             } else {
/*  462: 701 */               value = Conversions.coerce(valueType, value);
/*  463:     */             }
/*  464: 705 */             if ((key instanceof List)) {
/*  465: 706 */               key = fromList((List)key, keyType);
/*  466: 708 */             } else if ((value instanceof Map)) {
/*  467: 709 */               key = fromMap((Map)key, keyType);
/*  468:     */             } else {
/*  469: 712 */               key = Conversions.coerce(keyType, key);
/*  470:     */             }
/*  471: 715 */             newMap.put(key, value);
/*  472:     */           }
/*  473: 717 */           convertedArgumentList.set(index, newMap);
/*  474: 718 */           return true;
/*  475:     */         }
/*  476: 720 */         break;
/*  477:     */       case 26: 
/*  478: 723 */         if (parameterClass.isInstance(item)) {
/*  479: 724 */           return true;
/*  480:     */         }
/*  481: 727 */         if ((item instanceof Map))
/*  482:     */         {
/*  483: 728 */           item = fromMap((Map)item, parameterClass);
/*  484: 729 */           convertedArgumentList.set(index, item);
/*  485: 730 */           return true;
/*  486:     */         }
/*  487: 731 */         if ((item instanceof List))
/*  488:     */         {
/*  489: 733 */           List<Object> listItem = null;
/*  490:     */           
/*  491: 735 */           listItem = (List)item;
/*  492:     */           
/*  493: 737 */           value = fromList(listItem, parameterClass);
/*  494:     */           
/*  495: 739 */           convertedArgumentList.set(index, value);
/*  496: 740 */           return true;
/*  497:     */         }
/*  498: 743 */         convertedArgumentList.set(index, Conversions.coerce(parameterClass, item));
/*  499: 744 */         return true;
/*  500:     */       case 27: 
/*  501:     */       case 28: 
/*  502: 749 */         if (parameterClass.isInstance(item)) {
/*  503: 750 */           return true;
/*  504:     */         }
/*  505: 753 */         if (!(item instanceof Map)) {
/*  506:     */           break label1686;
/*  507:     */         }
/*  508: 756 */         String className = (String)((Map)item).get("class");
/*  509: 757 */         if (className != null)
/*  510:     */         {
/*  511: 758 */           item = fromMap((Map)item, Reflection.loadClass(className));
/*  512: 759 */           convertedArgumentList.set(index, item);
/*  513: 760 */           return true;
/*  514:     */         }
/*  515: 762 */         return false;
/*  516:     */       case 29: 
/*  517: 770 */         item = Conversions.toList(item);
/*  518: 771 */         return true;
/*  519:     */       case 1: 
/*  520:     */       case 30: 
/*  521:     */       case 31: 
/*  522: 776 */         if ((item instanceof List))
/*  523:     */         {
/*  524: 778 */           List<Object> itemList = (List)item;
/*  525: 784 */           if ((itemList.size() > 0) && (((itemList.get(0) instanceof List)) || ((itemList.get(0) instanceof ValueContainer))))
/*  526:     */           {
/*  527: 788 */             Type type = methodAccess.getGenericParameterTypes()[index];
/*  528: 793 */             if ((type instanceof ParameterizedType))
/*  529:     */             {
/*  530: 794 */               ParameterizedType pType = (ParameterizedType)type;
/*  531:     */               Class<?> componentType;
/*  532:     */               Class<?> componentType;
/*  533: 798 */               if (!(pType.getActualTypeArguments()[0] instanceof Class)) {
/*  534: 799 */                 componentType = Object.class;
/*  535:     */               } else {
/*  536: 801 */                 componentType = (Class)pType.getActualTypeArguments()[0];
/*  537:     */               }
/*  538: 804 */               Collection newList = Conversions.createCollection(parameterClass, itemList.size());
/*  539: 806 */               for (Object o : itemList)
/*  540:     */               {
/*  541: 807 */                 if ((o instanceof ValueContainer)) {
/*  542: 808 */                   o = ((ValueContainer)o).toValue();
/*  543:     */                 }
/*  544: 811 */                 if (componentType == Object.class)
/*  545:     */                 {
/*  546: 812 */                   newList.add(o);
/*  547:     */                 }
/*  548:     */                 else
/*  549:     */                 {
/*  550: 815 */                   List fromList = (List)o;
/*  551: 816 */                   o = fromList(fromList, componentType);
/*  552: 817 */                   newList.add(o);
/*  553:     */                 }
/*  554:     */               }
/*  555: 820 */               convertedArgumentList.set(index, newList);
/*  556: 821 */               return true;
/*  557:     */             }
/*  558:     */           }
/*  559:     */           else
/*  560:     */           {
/*  561: 829 */             Type type = methodAccess.getGenericParameterTypes()[index];
/*  562: 830 */             if ((type instanceof ParameterizedType))
/*  563:     */             {
/*  564: 831 */               ParameterizedType pType = (ParameterizedType)type;
/*  565:     */               
/*  566: 833 */               Class<?> componentType = (pType.getActualTypeArguments()[0] instanceof Class) ? (Class)pType.getActualTypeArguments()[0] : Object.class;
/*  567:     */               
/*  568: 835 */               Collection newList = Conversions.createCollection(parameterClass, itemList.size());
/*  569: 838 */               for (Object o : itemList)
/*  570:     */               {
/*  571: 839 */                 if ((o instanceof ValueContainer)) {
/*  572: 840 */                   o = ((ValueContainer)o).toValue();
/*  573:     */                 }
/*  574: 842 */                 if ((o instanceof List))
/*  575:     */                 {
/*  576: 844 */                   if (componentType != Object.class)
/*  577:     */                   {
/*  578: 846 */                     List fromList = (List)o;
/*  579: 847 */                     o = fromList(fromList, componentType);
/*  580:     */                   }
/*  581: 849 */                   newList.add(o);
/*  582:     */                 }
/*  583: 850 */                 else if ((o instanceof Map))
/*  584:     */                 {
/*  585: 851 */                   Map fromMap = (Map)o;
/*  586: 852 */                   o = fromMap(fromMap, componentType);
/*  587: 853 */                   newList.add(o);
/*  588:     */                 }
/*  589:     */                 else
/*  590:     */                 {
/*  591: 856 */                   newList.add(Conversions.coerce(componentType, o));
/*  592:     */                 }
/*  593:     */               }
/*  594: 859 */               convertedArgumentList.set(index, newList);
/*  595: 860 */               return true;
/*  596:     */             }
/*  597:     */           }
/*  598:     */         }
/*  599: 866 */         return false;
/*  600:     */       }
/*  601: 870 */       TypeType itemType = TypeType.getInstanceType(item);
/*  602: 872 */       switch (1.$SwitchMap$org$boon$core$TypeType[itemType.ordinal()])
/*  603:     */       {
/*  604:     */       case 1: 
/*  605: 874 */         convertedArgumentList.set(index, fromList((List)item, parameterClass));
/*  606: 875 */         return true;
/*  607:     */       case 2: 
/*  608:     */       case 3: 
/*  609: 878 */         convertedArgumentList.set(index, fromMap((Map)item, parameterClass));
/*  610: 879 */         return true;
/*  611:     */       case 4: 
/*  612:     */       case 5: 
/*  613:     */       case 6: 
/*  614:     */       case 7: 
/*  615:     */       case 8: 
/*  616:     */       case 9: 
/*  617:     */       case 10: 
/*  618:     */       case 11: 
/*  619:     */       case 12: 
/*  620:     */       case 13: 
/*  621:     */       case 14: 
/*  622:     */       case 15: 
/*  623:     */       case 16: 
/*  624:     */       case 17: 
/*  625:     */       case 18: 
/*  626:     */       case 19: 
/*  627:     */       case 20: 
/*  628: 898 */         value = Conversions.coerceWithFlag(parameterClass, flag, item);
/*  629: 900 */         if (flag[0] == 0) {
/*  630: 901 */           return false;
/*  631:     */         }
/*  632: 903 */         convertedArgumentList.set(index, value);
/*  633: 904 */         return true;
/*  634:     */       case 21: 
/*  635:     */       case 22: 
/*  636: 911 */         value = Conversions.coerceWithFlag(parameterClass, flag, item);
/*  637: 913 */         if (flag[0] == 0) {
/*  638: 914 */           return false;
/*  639:     */         }
/*  640: 916 */         convertedArgumentList.set(index, value);
/*  641: 917 */         return true;
/*  642:     */       }
/*  643:     */       label1686:
/*  644: 928 */       if (parameterClass.isInstance(item)) {
/*  645: 929 */         return true;
/*  646:     */       }
/*  647:     */     }
/*  648:     */     catch (Exception ex)
/*  649:     */     {
/*  650: 934 */       Boon.error(ex, new Object[] { "PROBLEM WITH oldMatchAndConvertArgs", "respectIgnore", Boolean.valueOf(this.respectIgnore), "view", this.view, "fieldsAccessor", this.fieldsAccessor, "list", convertedArgumentList, "constructor", methodAccess, "parameters", parameterTypes, "index", Integer.valueOf(index), "ignoreSet", this.ignoreSet });
/*  651:     */       
/*  652:     */ 
/*  653:     */ 
/*  654:     */ 
/*  655: 939 */       return false;
/*  656:     */     }
/*  657: 942 */     return false;
/*  658:     */   }
/*  659:     */   
/*  660:     */   private void handleCollectionOfValues(Object newInstance, FieldAccess field, Collection<Value> acollectionOfValues)
/*  661:     */   {
/*  662: 958 */     Collection collectionOfValues = acollectionOfValues;
/*  663: 959 */     if (null == collectionOfValues)
/*  664:     */     {
/*  665: 960 */       field.setObject(newInstance, null);
/*  666: 961 */       return;
/*  667:     */     }
/*  668: 964 */     if (field.typeEnum() == TypeType.INSTANCE)
/*  669:     */     {
/*  670: 966 */       field.setObject(newInstance, fromList((List)acollectionOfValues, field.type()));
/*  671: 967 */       return;
/*  672:     */     }
/*  673: 971 */     if ((collectionOfValues instanceof ValueList)) {
/*  674: 972 */       collectionOfValues = ((ValueList)collectionOfValues).list();
/*  675:     */     }
/*  676: 976 */     Class<?> componentClass = field.getComponentClass();
/*  677: 983 */     switch (1.$SwitchMap$org$boon$core$TypeType[field.typeEnum().ordinal()])
/*  678:     */     {
/*  679:     */     case 1: 
/*  680:     */     case 30: 
/*  681:     */     case 31: 
/*  682: 991 */       Collection<Object> newCollection = Conversions.createCollection(field.type(), collectionOfValues.size());
/*  683: 994 */       for (Value value : (List)collectionOfValues) {
/*  684: 996 */         if (value.isContainer())
/*  685:     */         {
/*  686: 997 */           Object oValue = value.toValue();
/*  687: 998 */           if ((oValue instanceof Map)) {
/*  688: 999 */             newCollection.add(fromValueMap((Map)oValue, componentClass));
/*  689:     */           }
/*  690:     */         }
/*  691:     */         else
/*  692:     */         {
/*  693:1002 */           newCollection.add(Conversions.coerce(componentClass, value.toValue()));
/*  694:     */         }
/*  695:     */       }
/*  696:1007 */       field.setObject(newInstance, newCollection);
/*  697:1008 */       break;
/*  698:     */     case 29: 
/*  699:     */     case 32: 
/*  700:     */     case 33: 
/*  701:     */     case 34: 
/*  702:     */     case 35: 
/*  703:     */     case 36: 
/*  704:     */     case 37: 
/*  705:     */     case 38: 
/*  706:     */     case 39: 
/*  707:1020 */       TypeType componentType = field.componentType();
/*  708:1021 */       int index = 0;
/*  709:1023 */       switch (1.$SwitchMap$org$boon$core$TypeType[componentType.ordinal()])
/*  710:     */       {
/*  711:     */       case 6: 
/*  712:1025 */         int[] iarray = new int[collectionOfValues.size()];
/*  713:1026 */         for (Value value : (List)collectionOfValues)
/*  714:     */         {
/*  715:1027 */           iarray[index] = value.intValue();
/*  716:1028 */           index++;
/*  717:     */         }
/*  718:1031 */         field.setObject(newInstance, iarray);
/*  719:1032 */         return;
/*  720:     */       case 7: 
/*  721:1034 */         short[] sarray = new short[collectionOfValues.size()];
/*  722:1035 */         for (Value value : (List)collectionOfValues)
/*  723:     */         {
/*  724:1036 */           sarray[index] = value.shortValue();
/*  725:1037 */           index++;
/*  726:     */         }
/*  727:1040 */         field.setObject(newInstance, sarray);
/*  728:1041 */         return;
/*  729:     */       case 10: 
/*  730:1043 */         double[] darray = new double[collectionOfValues.size()];
/*  731:1044 */         for (Value value : (List)collectionOfValues)
/*  732:     */         {
/*  733:1045 */           darray[index] = value.doubleValue();
/*  734:1046 */           index++;
/*  735:     */         }
/*  736:1049 */         field.setObject(newInstance, darray);
/*  737:1050 */         return;
/*  738:     */       case 9: 
/*  739:1052 */         float[] farray = new float[collectionOfValues.size()];
/*  740:1053 */         for (Value value : (List)collectionOfValues)
/*  741:     */         {
/*  742:1054 */           farray[index] = value.floatValue();
/*  743:1055 */           index++;
/*  744:     */         }
/*  745:1058 */         field.setObject(newInstance, farray);
/*  746:1059 */         return;
/*  747:     */       case 11: 
/*  748:1062 */         long[] larray = new long[collectionOfValues.size()];
/*  749:1063 */         for (Value value : (List)collectionOfValues)
/*  750:     */         {
/*  751:1064 */           larray[index] = value.longValue();
/*  752:1065 */           index++;
/*  753:     */         }
/*  754:1068 */         field.setObject(newInstance, larray);
/*  755:1069 */         return;
/*  756:     */       case 8: 
/*  757:1073 */         byte[] barray = new byte[collectionOfValues.size()];
/*  758:1074 */         for (Value value : (List)collectionOfValues)
/*  759:     */         {
/*  760:1075 */           barray[index] = value.byteValue();
/*  761:1076 */           index++;
/*  762:     */         }
/*  763:1079 */         field.setObject(newInstance, barray);
/*  764:1080 */         return;
/*  765:     */       case 23: 
/*  766:1084 */         char[] chars = new char[collectionOfValues.size()];
/*  767:1085 */         for (Value value : (List)collectionOfValues)
/*  768:     */         {
/*  769:1086 */           chars[index] = value.charValue();
/*  770:1087 */           index++;
/*  771:     */         }
/*  772:1089 */         field.setObject(newInstance, chars);
/*  773:1090 */         return;
/*  774:     */       case 22: 
/*  775:1093 */         CharBuf buffer = CharBuf.create(100);
/*  776:1094 */         String[] strings = new String[collectionOfValues.size()];
/*  777:1095 */         for (Value value : (List)collectionOfValues)
/*  778:     */         {
/*  779:1096 */           strings[index] = value.stringValue(buffer);
/*  780:1097 */           index++;
/*  781:     */         }
/*  782:1099 */         field.setObject(newInstance, strings);
/*  783:1100 */         return;
/*  784:     */       }
/*  785:1104 */       Object array = Array.newInstance(componentClass, collectionOfValues.size());
/*  786:1107 */       for (Value value : (List)collectionOfValues)
/*  787:     */       {
/*  788:1108 */         if ((value instanceof ValueContainer))
/*  789:     */         {
/*  790:1109 */           Object o = value.toValue();
/*  791:1110 */           if ((o instanceof List))
/*  792:     */           {
/*  793:1111 */             o = fromList((List)o, componentClass);
/*  794:1112 */             if (!componentClass.isInstance(o)) {
/*  795:     */               break;
/*  796:     */             }
/*  797:1113 */             Array.set(array, index, o);
/*  798:     */           }
/*  799:1117 */           else if ((o instanceof Map))
/*  800:     */           {
/*  801:1118 */             o = fromMap((Map)o, componentClass);
/*  802:1119 */             if (!componentClass.isInstance(o)) {
/*  803:     */               break;
/*  804:     */             }
/*  805:1120 */             Array.set(array, index, o);
/*  806:     */           }
/*  807:     */         }
/*  808:     */         else
/*  809:     */         {
/*  810:1126 */           Object o = value.toValue();
/*  811:1127 */           if (componentClass.isInstance(o)) {
/*  812:1128 */             Array.set(array, index, o);
/*  813:     */           } else {
/*  814:1130 */             Array.set(array, index, Conversions.coerce(componentClass, o));
/*  815:     */           }
/*  816:     */         }
/*  817:1133 */         index++;
/*  818:     */       }
/*  819:1135 */       field.setValue(newInstance, array);
/*  820:     */     }
/*  821:     */   }
/*  822:     */   
/*  823:     */   public Object fromValueMap(Map<String, Value> valueMap)
/*  824:     */   {
/*  825:     */     try
/*  826:     */     {
/*  827:1158 */       String className = ((Value)valueMap.get("class")).toString();
/*  828:1159 */       Class<?> cls = Reflection.loadClass(className);
/*  829:1160 */       return fromValueMap(valueMap, cls);
/*  830:     */     }
/*  831:     */     catch (Exception ex)
/*  832:     */     {
/*  833:1162 */       return Exceptions.handle(Object.class, Boon.sputs(new Object[] { "fromValueMap", "map", valueMap, "fieldAccessor", this.fieldsAccessor }), ex);
/*  834:     */     }
/*  835:     */   }
/*  836:     */   
/*  837:     */   public <T> T fromValueMap(Map<String, Value> valueMap, Class<T> cls)
/*  838:     */   {
/*  839:1182 */     T newInstance = Reflection.newInstance(cls);
/*  840:1183 */     ValueMap map = (ValueMap)valueMap;
/*  841:     */     
/*  842:     */ 
/*  843:1186 */     Map<String, FieldAccess> fields = this.fieldsAccessor.getFields(cls);
/*  844:     */     
/*  845:     */ 
/*  846:1189 */     FieldAccess field = null;
/*  847:1190 */     String fieldName = null;
/*  848:     */     Map.Entry<String, Object>[] entries;
/*  849:     */     int size;
/*  850:     */     Map.Entry<String, Object>[] entries;
/*  851:1198 */     if (!map.hydrated())
/*  852:     */     {
/*  853:1199 */       int size = map.len();
/*  854:1200 */       entries = map.items();
/*  855:     */     }
/*  856:     */     else
/*  857:     */     {
/*  858:1202 */       size = map.size();
/*  859:1203 */       entries = (Map.Entry[])map.entrySet().toArray(new Map.Entry[size]);
/*  860:     */     }
/*  861:1208 */     if ((size == 0) || (entries == null)) {
/*  862:1209 */       return newInstance;
/*  863:     */     }
/*  864:1214 */     for (int index = 0; index < size; index++)
/*  865:     */     {
/*  866:1215 */       Object value = null;
/*  867:     */       try
/*  868:     */       {
/*  869:1218 */         Map.Entry<String, Object> entry = entries[index];
/*  870:     */         
/*  871:1220 */         fieldName = (String)entry.getKey();
/*  872:1223 */         if ((this.ignoreSet == null) || 
/*  873:1224 */           (!this.ignoreSet.contains(fieldName)))
/*  874:     */         {
/*  875:1229 */           field = (FieldAccess)fields.get(this.fieldsAccessor.isCaseInsensitive() ? fieldName.toLowerCase() : fieldName);
/*  876:1232 */           if (field != null) {
/*  877:1236 */             if ((this.view == null) || 
/*  878:1237 */               (field.isViewActive(this.view))) {
/*  879:1243 */               if ((!this.respectIgnore) || 
/*  880:1244 */                 (!field.ignore()))
/*  881:     */               {
/*  882:1250 */                 value = entry.getValue();
/*  883:1253 */                 if ((value instanceof Value)) {
/*  884:1254 */                   fromValueMapHandleValueCase(newInstance, field, (Value)value);
/*  885:     */                 } else {
/*  886:1256 */                   fromMapHandleNonValueCase(newInstance, field, value);
/*  887:     */                 }
/*  888:     */               }
/*  889:     */             }
/*  890:     */           }
/*  891:     */         }
/*  892:     */       }
/*  893:     */       catch (Exception ex)
/*  894:     */       {
/*  895:1259 */         return Exceptions.handle(Object.class, ex, new Object[] { "fieldName", fieldName, "of class", cls, "had issues for value", value, "for field", field });
/*  896:     */       }
/*  897:     */     }
/*  898:1264 */     return newInstance;
/*  899:     */   }
/*  900:     */   
/*  901:     */   private <T> void fromMapHandleNonValueCase(T newInstance, FieldAccess field, Object objectValue)
/*  902:     */   {
/*  903:     */     try
/*  904:     */     {
/*  905:1281 */       if ((objectValue instanceof Map))
/*  906:     */       {
/*  907:1282 */         Class<?> clazz = field.type();
/*  908:1283 */         if ((!clazz.isInterface()) && (!Typ.isAbstract(clazz)))
/*  909:     */         {
/*  910:1284 */           objectValue = fromValueMap((Map)objectValue, field.type());
/*  911:     */         }
/*  912:     */         else
/*  913:     */         {
/*  914:1286 */           String className = ((Value)((Map)objectValue).get("class")).toString();
/*  915:     */           
/*  916:1288 */           Class<?> cls = Reflection.loadClass(className);
/*  917:     */           
/*  918:1290 */           objectValue = fromValueMap((Map)objectValue, cls);
/*  919:     */         }
/*  920:1292 */         field.setValue(newInstance, objectValue);
/*  921:     */       }
/*  922:1293 */       else if ((objectValue instanceof Collection))
/*  923:     */       {
/*  924:1294 */         handleCollectionOfValues(newInstance, field, (Collection)objectValue);
/*  925:     */       }
/*  926:     */       else
/*  927:     */       {
/*  928:1297 */         field.setValue(newInstance, objectValue);
/*  929:     */       }
/*  930:     */     }
/*  931:     */     catch (Exception ex)
/*  932:     */     {
/*  933:1300 */       Exceptions.handle(Boon.sputs(new Object[] { "Problem handling non value case of fromValueMap", "field", field.name(), "fieldType", field.type().getName(), "object from map", objectValue }), ex);
/*  934:     */     }
/*  935:     */   }
/*  936:     */   
/*  937:     */   private <T> void fromValueMapHandleValueCase(T newInstance, FieldAccess field, Value value)
/*  938:     */   {
/*  939:1322 */     Object objValue = ValueContainer.toObject(value);
/*  940:     */     
/*  941:     */ 
/*  942:1325 */     Class<?> clazz = field.type();
/*  943:1328 */     switch (1.$SwitchMap$org$boon$core$TypeType[field.typeEnum().ordinal()])
/*  944:     */     {
/*  945:     */     case 27: 
/*  946:     */     case 28: 
/*  947:     */     case 40: 
/*  948:1333 */       if ((objValue instanceof Map))
/*  949:     */       {
/*  950:1334 */         Map<String, Value> valueMap = (Map)objValue;
/*  951:     */         
/*  952:1336 */         Value aClass = (Value)valueMap.get("class");
/*  953:1337 */         clazz = Reflection.loadClass(aClass.stringValue());
/*  954:     */       }
/*  955:     */     case 26: 
/*  956:1341 */       switch (1.$SwitchMap$org$boon$core$TypeType[value.type().ordinal()])
/*  957:     */       {
/*  958:     */       case 2: 
/*  959:1343 */         objValue = fromValueMap((Map)objValue, clazz);
/*  960:1344 */         break;
/*  961:     */       case 1: 
/*  962:1346 */         objValue = fromList((List)objValue, clazz);
/*  963:     */       }
/*  964:1351 */       field.setValue(newInstance, objValue);
/*  965:     */       
/*  966:1353 */       break;
/*  967:     */     case 2: 
/*  968:     */     case 3: 
/*  969:1359 */       Class keyType = (Class)field.getParameterizedType().getActualTypeArguments()[0];
/*  970:1360 */       Class valueType = (Class)field.getParameterizedType().getActualTypeArguments()[1];
/*  971:     */       
/*  972:1362 */       Map mapInner = (Map)objValue;
/*  973:1363 */       Set<Map.Entry> set = mapInner.entrySet();
/*  974:1364 */       Map newMap = new LinkedHashMap();
/*  975:1366 */       for (Map.Entry entry : set)
/*  976:     */       {
/*  977:1367 */         Object evalue = entry.getValue();
/*  978:     */         
/*  979:1369 */         Object key = entry.getKey();
/*  980:1371 */         if ((evalue instanceof ValueContainer)) {
/*  981:1372 */           evalue = ((ValueContainer)evalue).toValue();
/*  982:     */         }
/*  983:1375 */         key = Conversions.coerce(keyType, key);
/*  984:1376 */         evalue = Conversions.coerce(valueType, evalue);
/*  985:1377 */         newMap.put(key, evalue);
/*  986:     */       }
/*  987:1380 */       objValue = newMap;
/*  988:     */       
/*  989:1382 */       field.setValue(newInstance, objValue);
/*  990:     */       
/*  991:1384 */       break;
/*  992:     */     case 1: 
/*  993:     */     case 29: 
/*  994:     */     case 30: 
/*  995:     */     case 31: 
/*  996:     */     case 32: 
/*  997:     */     case 33: 
/*  998:     */     case 34: 
/*  999:     */     case 35: 
/* 1000:     */     case 36: 
/* 1001:     */     case 37: 
/* 1002:     */     case 38: 
/* 1003:     */     case 39: 
/* 1004:1398 */       if ((this.acceptSingleValueAsArray) && (ValueContainer.NULL != value) && (!(objValue instanceof Collection))) {
/* 1005:1399 */         if ((objValue instanceof ValueMapImpl)) {
/* 1006:1400 */           objValue = Arrays.asList(new ValueContainer[] { new ValueContainer(objValue, TypeType.MAP, false) });
/* 1007:     */         } else {
/* 1008:1402 */           objValue = Arrays.asList(new Object[] { objValue });
/* 1009:     */         }
/* 1010:     */       }
/* 1011:1406 */       handleCollectionOfValues(newInstance, field, (Collection)objValue);
/* 1012:     */       
/* 1013:     */ 
/* 1014:1409 */       break;
/* 1015:     */     }
/* 1016:1412 */     field.setFromValue(newInstance, value);
/* 1017:     */   }
/* 1018:     */   
/* 1019:     */   private void setFieldValueFromMap(Object parentObject, FieldAccess field, Map mapInner)
/* 1020:     */   {
/* 1021:1427 */     Class<?> fieldClassType = field.type();
/* 1022:1428 */     Object value = null;
/* 1023:1431 */     if (!Typ.isMap(fieldClassType))
/* 1024:     */     {
/* 1025:1433 */       if ((!fieldClassType.isInterface()) && (!Typ.isAbstract(fieldClassType)))
/* 1026:     */       {
/* 1027:1434 */         value = fromMap(mapInner, field.type());
/* 1028:     */       }
/* 1029:     */       else
/* 1030:     */       {
/* 1031:1437 */         Object oClassName = mapInner.get("class");
/* 1032:1438 */         if (oClassName != null) {
/* 1033:1439 */           value = fromMap(mapInner, Reflection.loadClass(oClassName.toString()));
/* 1034:     */         } else {
/* 1035:1441 */           value = null;
/* 1036:     */         }
/* 1037:     */       }
/* 1038:     */     }
/* 1039:1451 */     else if (Typ.isMap(fieldClassType))
/* 1040:     */     {
/* 1041:1452 */       Class keyType = (Class)field.getParameterizedType().getActualTypeArguments()[0];
/* 1042:1453 */       Class valueType = (Class)field.getParameterizedType().getActualTypeArguments()[1];
/* 1043:     */       
/* 1044:1455 */       Set<Map.Entry> set = mapInner.entrySet();
/* 1045:1456 */       Map newMap = new LinkedHashMap();
/* 1046:1458 */       for (Map.Entry entry : set)
/* 1047:     */       {
/* 1048:1459 */         Object evalue = entry.getValue();
/* 1049:     */         
/* 1050:1461 */         Object key = entry.getKey();
/* 1051:1463 */         if ((evalue instanceof ValueContainer)) {
/* 1052:1464 */           evalue = ((ValueContainer)evalue).toValue();
/* 1053:     */         }
/* 1054:1467 */         key = Conversions.coerce(keyType, key);
/* 1055:1468 */         evalue = Conversions.coerce(valueType, evalue);
/* 1056:1469 */         newMap.put(key, evalue);
/* 1057:     */       }
/* 1058:1472 */       value = newMap;
/* 1059:     */     }
/* 1060:1476 */     field.setValue(parentObject, value);
/* 1061:     */   }
/* 1062:     */   
/* 1063:     */   private void processCollectionFromMapUsingFields(Object newInstance, FieldAccess field, Collection<?> collection)
/* 1064:     */   {
/* 1065:1492 */     Class<?> fieldComponentClass = field.getComponentClass();
/* 1066:     */     
/* 1067:1494 */     Class<?> valueComponentClass = Reflection.getComponentType(collection);
/* 1068:1500 */     if (Typ.isMap(valueComponentClass))
/* 1069:     */     {
/* 1070:1501 */       handleCollectionOfMaps(newInstance, field, collection);
/* 1071:     */       
/* 1072:1503 */       return;
/* 1073:     */     }
/* 1074:1508 */     if (Typ.isValue(valueComponentClass))
/* 1075:     */     {
/* 1076:1509 */       handleCollectionOfValues(newInstance, field, collection);
/* 1077:     */       
/* 1078:1511 */       return;
/* 1079:     */     }
/* 1080:1519 */     if (Typ.implementsInterface(collection.getClass(), field.type())) {
/* 1081:1521 */       if ((fieldComponentClass != null) && (fieldComponentClass.isAssignableFrom(valueComponentClass)))
/* 1082:     */       {
/* 1083:1522 */         field.setValue(newInstance, collection);
/* 1084:     */         
/* 1085:1524 */         return;
/* 1086:     */       }
/* 1087:     */     }
/* 1088:1543 */     if (!field.typeEnum().isCollection())
/* 1089:     */     {
/* 1090:1544 */       if ((collection instanceof List)) {
/* 1091:     */         try
/* 1092:     */         {
/* 1093:1546 */           Object value = fromList((List)collection, field.getComponentClass());
/* 1094:1547 */           field.setValue(newInstance, value);
/* 1095:     */         }
/* 1096:     */         catch (Exception ex)
/* 1097:     */         {
/* 1098:1550 */           field.setValue(newInstance, collection);
/* 1099:     */         }
/* 1100:     */       } else {
/* 1101:1553 */         field.setValue(newInstance, collection);
/* 1102:     */       }
/* 1103:1555 */       return;
/* 1104:     */     }
/* 1105:1564 */     Collection<Object> newCollection = Conversions.createCollection(field.type(), collection.size());
/* 1106:1566 */     if ((fieldComponentClass == null) || (fieldComponentClass.isAssignableFrom(valueComponentClass)))
/* 1107:     */     {
/* 1108:1568 */       newCollection.addAll(collection);
/* 1109:1569 */       field.setValue(newInstance, newCollection);
/* 1110:1570 */       return;
/* 1111:     */     }
/* 1112:1576 */     for (Object itemValue : collection)
/* 1113:     */     {
/* 1114:1577 */       newCollection.add(Conversions.coerce(fieldComponentClass, itemValue));
/* 1115:1578 */       field.setValue(newInstance, newCollection);
/* 1116:     */     }
/* 1117:     */   }
/* 1118:     */   
/* 1119:     */   public Object fromMap(Map<String, Object> map)
/* 1120:     */   {
/* 1121:1592 */     String clazz = (String)map.get("class");
/* 1122:1593 */     Class cls = Reflection.loadClass(clazz);
/* 1123:1594 */     return fromMap(map, cls);
/* 1124:     */   }
/* 1125:     */   
/* 1126:     */   public Map<String, Object> toMap(Object object)
/* 1127:     */   {
/* 1128:1609 */     if (object == null) {
/* 1129:1610 */       return null;
/* 1130:     */     }
/* 1131:1613 */     if ((object instanceof Map)) {
/* 1132:1614 */       return (Map)object;
/* 1133:     */     }
/* 1134:1616 */     Map<String, Object> map = new LinkedHashMap();
/* 1135:     */     
/* 1136:     */ 
/* 1137:     */ 
/* 1138:1620 */     Map<String, FieldAccess> fieldMap = Reflection.getAllAccessorFields(object.getClass());
/* 1139:1621 */     List<FieldAccess> fields = new ArrayList(fieldMap.values());
/* 1140:     */     
/* 1141:     */ 
/* 1142:1624 */     Collections.reverse(fields);
/* 1143:1628 */     if (this.outputType) {
/* 1144:1629 */       map.put("class", object.getClass().getName());
/* 1145:     */     }
/* 1146:1632 */     for (FieldAccess access : fields)
/* 1147:     */     {
/* 1148:1634 */       String fieldName = access.name();
/* 1149:1636 */       if ((!access.isStatic()) && (
/* 1150:     */       
/* 1151:     */ 
/* 1152:     */ 
/* 1153:1640 */         (this.ignoreSet == null) || 
/* 1154:1641 */         (!this.ignoreSet.contains(fieldName))))
/* 1155:     */       {
/* 1156:1646 */         Object value = access.getValue(object);
/* 1157:1649 */         if (value != null) {
/* 1158:1654 */           switch (1.$SwitchMap$org$boon$core$TypeType[access.typeEnum().ordinal()])
/* 1159:     */           {
/* 1160:     */           case 5: 
/* 1161:     */           case 6: 
/* 1162:     */           case 7: 
/* 1163:     */           case 8: 
/* 1164:     */           case 9: 
/* 1165:     */           case 10: 
/* 1166:     */           case 11: 
/* 1167:     */           case 12: 
/* 1168:     */           case 13: 
/* 1169:     */           case 14: 
/* 1170:     */           case 15: 
/* 1171:     */           case 16: 
/* 1172:     */           case 17: 
/* 1173:     */           case 18: 
/* 1174:     */           case 23: 
/* 1175:     */           case 24: 
/* 1176:     */           case 41: 
/* 1177:     */           case 42: 
/* 1178:     */           case 43: 
/* 1179:     */           case 44: 
/* 1180:     */           case 45: 
/* 1181:1676 */             map.put(fieldName, value);
/* 1182:1677 */             break;
/* 1183:     */           case 29: 
/* 1184:     */           case 32: 
/* 1185:     */           case 33: 
/* 1186:     */           case 34: 
/* 1187:     */           case 35: 
/* 1188:     */           case 36: 
/* 1189:     */           case 37: 
/* 1190:     */           case 38: 
/* 1191:     */           case 39: 
/* 1192:1688 */             if (Typ.isBasicType(access.getComponentClass()))
/* 1193:     */             {
/* 1194:1689 */               map.put(fieldName, value);
/* 1195:     */             }
/* 1196:     */             else
/* 1197:     */             {
/* 1198:1691 */               int length = Arry.len(value);
/* 1199:1692 */               List<Map<String, Object>> list = new ArrayList(length);
/* 1200:1693 */               for (int index = 0; index < length; index++)
/* 1201:     */               {
/* 1202:1694 */                 Object item = Arry.fastIndex(value, index);
/* 1203:1695 */                 list.add(toMap(item));
/* 1204:     */               }
/* 1205:1697 */               map.put(fieldName, list);
/* 1206:     */             }
/* 1207:1699 */             break;
/* 1208:     */           case 1: 
/* 1209:     */           case 30: 
/* 1210:     */           case 31: 
/* 1211:1703 */             Collection<?> collection = (Collection)value;
/* 1212:1704 */             Class<?> componentType = access.getComponentClass();
/* 1213:1705 */             if (Typ.isBasicType(componentType))
/* 1214:     */             {
/* 1215:1706 */               map.put(fieldName, value);
/* 1216:     */             }
/* 1217:1707 */             else if (Typ.isEnum(componentType))
/* 1218:     */             {
/* 1219:1708 */               List<String> list = new ArrayList(collection.size());
/* 1220:1710 */               for (Object item : collection) {
/* 1221:1711 */                 if (item != null) {
/* 1222:1712 */                   list.add(item.toString());
/* 1223:     */                 }
/* 1224:     */               }
/* 1225:1715 */               map.put(fieldName, list);
/* 1226:     */             }
/* 1227:     */             else
/* 1228:     */             {
/* 1229:1718 */               List<Map<String, Object>> list = new ArrayList(collection.size());
/* 1230:1720 */               for (Object item : collection) {
/* 1231:1721 */                 if (item != null) {
/* 1232:1722 */                   list.add(toMap(item));
/* 1233:     */                 }
/* 1234:     */               }
/* 1235:1725 */               map.put(fieldName, list);
/* 1236:     */             }
/* 1237:1727 */             break;
/* 1238:     */           case 2: 
/* 1239:1729 */             map.put(fieldName, value);
/* 1240:1730 */             break;
/* 1241:     */           case 26: 
/* 1242:1733 */             map.put(fieldName, toMap(value));
/* 1243:1734 */             break;
/* 1244:     */           case 27: 
/* 1245:     */           case 28: 
/* 1246:1739 */             Map<String, Object> abstractMap = toMap(value);
/* 1247:1740 */             abstractMap.put("class", Boon.className(value));
/* 1248:1741 */             map.put(fieldName, abstractMap);
/* 1249:1742 */             break;
/* 1250:     */           case 25: 
/* 1251:1745 */             map.put(fieldName, value);
/* 1252:1746 */             break;
/* 1253:     */           case 3: 
/* 1254:     */           case 4: 
/* 1255:     */           case 19: 
/* 1256:     */           case 20: 
/* 1257:     */           case 21: 
/* 1258:     */           case 22: 
/* 1259:     */           case 40: 
/* 1260:     */           default: 
/* 1261:1752 */             map.put(fieldName, Conversions.toString(value));
/* 1262:     */           }
/* 1263:     */         }
/* 1264:     */       }
/* 1265:     */     }
/* 1266:1762 */     return map;
/* 1267:     */   }
/* 1268:     */   
/* 1269:     */   public List<Map<String, Object>> toListOfMaps(Collection<?> collection)
/* 1270:     */   {
/* 1271:1801 */     List<Map<String, Object>> list = new ArrayList();
/* 1272:1802 */     for (Object o : collection) {
/* 1273:1803 */       list.add(toMap(o));
/* 1274:     */     }
/* 1275:1805 */     return list;
/* 1276:     */   }
/* 1277:     */   
/* 1278:     */   public List<?> toList(Object object)
/* 1279:     */   {
/* 1280:1817 */     TypeType instanceType = TypeType.getInstanceType(object);
/* 1281:1819 */     switch (1.$SwitchMap$org$boon$core$TypeType[instanceType.ordinal()])
/* 1282:     */     {
/* 1283:     */     case 46: 
/* 1284:1821 */       return Lists.list(new Object[] { (Object)null });
/* 1285:     */     case 29: 
/* 1286:     */     case 32: 
/* 1287:     */     case 33: 
/* 1288:     */     case 34: 
/* 1289:     */     case 35: 
/* 1290:     */     case 36: 
/* 1291:     */     case 37: 
/* 1292:     */     case 38: 
/* 1293:     */     case 39: 
/* 1294:1833 */       return Conversions.toList(object);
/* 1295:     */     case 26: 
/* 1296:1836 */       if (Reflection.respondsTo(object, "toList")) {
/* 1297:1837 */         return (List)Reflection.invoke(object, "toList", new Object[0]);
/* 1298:     */       }
/* 1299:     */       break;
/* 1300:     */     }
/* 1301:1841 */     return Lists.list(new Object[] { object });
/* 1302:     */   }
/* 1303:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.MapperComplex
 * JD-Core Version:    0.7.0.1
 */