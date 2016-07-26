/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.beans.BeanInfo;
/*   4:    */ import java.beans.IntrospectionException;
/*   5:    */ import java.beans.Introspector;
/*   6:    */ import java.beans.PropertyDescriptor;
/*   7:    */ import java.io.File;
/*   8:    */ import java.lang.reflect.Array;
/*   9:    */ import java.lang.reflect.InvocationTargetException;
/*  10:    */ import java.lang.reflect.Method;
/*  11:    */ import java.util.ArrayList;
/*  12:    */ import java.util.HashMap;
/*  13:    */ import java.util.Iterator;
/*  14:    */ import java.util.List;
/*  15:    */ import java.util.Map;
/*  16:    */ import java.util.Vector;
/*  17:    */ 
/*  18:    */ public class Option
/*  19:    */   implements RevisionHandler
/*  20:    */ {
/*  21: 50 */   private static final Map<Class<?>, PropertyDescriptor[]> s_descriptorCache = new HashMap();
/*  22:    */   private String m_Description;
/*  23:    */   private String m_Synopsis;
/*  24:    */   private String m_Name;
/*  25:    */   private int m_NumArguments;
/*  26:    */   
/*  27:    */   public Option(String description, String name, int numArguments, String synopsis)
/*  28:    */   {
/*  29: 76 */     this.m_Description = description;
/*  30: 77 */     this.m_Name = name;
/*  31: 78 */     this.m_NumArguments = numArguments;
/*  32: 79 */     this.m_Synopsis = synopsis;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public static Vector<Option> listOptionsForClassHierarchy(Class<?> childClazz, Class<?> oldestAncestorClazz)
/*  36:    */   {
/*  37: 96 */     Vector<Option> results = listOptionsForClass(childClazz);
/*  38:    */     
/*  39: 98 */     Class<?> parent = childClazz;
/*  40:    */     do
/*  41:    */     {
/*  42:100 */       parent = parent.getSuperclass();
/*  43:101 */       if (parent == null) {
/*  44:    */         break;
/*  45:    */       }
/*  46:104 */       results.addAll(listOptionsForClass(parent));
/*  47:105 */     } while (!parent.equals(oldestAncestorClazz));
/*  48:107 */     return results;
/*  49:    */   }
/*  50:    */   
/*  51:    */   protected static void addMethodsToList(Class<?> clazz, List<Method> methList)
/*  52:    */   {
/*  53:118 */     Method[] methods = clazz.getDeclaredMethods();
/*  54:119 */     for (Method m : methods) {
/*  55:120 */       methList.add(m);
/*  56:    */     }
/*  57:    */   }
/*  58:    */   
/*  59:    */   public static Vector<Option> listOptionsForClass(Class<?> clazz)
/*  60:    */   {
/*  61:134 */     Vector<Option> results = new Vector();
/*  62:135 */     List<Method> allMethods = new ArrayList();
/*  63:136 */     addMethodsToList(clazz, allMethods);
/*  64:    */     
/*  65:138 */     Class<?>[] interfaces = clazz.getInterfaces();
/*  66:139 */     for (Class c : interfaces) {
/*  67:140 */       addMethodsToList(c, allMethods);
/*  68:    */     }
/*  69:143 */     Option[] unsorted = new Option[allMethods.size()];
/*  70:144 */     int[] opOrder = new int[allMethods.size()];
/*  71:145 */     for (int i = 0; i < opOrder.length; i++) {
/*  72:146 */       opOrder[i] = 2147483647;
/*  73:    */     }
/*  74:148 */     int index = 0;
/*  75:149 */     for (Method m : allMethods)
/*  76:    */     {
/*  77:150 */       OptionMetadata o = (OptionMetadata)m.getAnnotation(OptionMetadata.class);
/*  78:151 */       if ((o != null) && 
/*  79:152 */         (o.commandLineParamName().length() > 0))
/*  80:    */       {
/*  81:153 */         opOrder[index] = o.displayOrder();
/*  82:154 */         String description = o.description();
/*  83:155 */         if (!description.startsWith("\t")) {
/*  84:156 */           description = "\t" + description;
/*  85:    */         }
/*  86:158 */         description = description.replace("\n", "\n\t");
/*  87:159 */         String name = o.commandLineParamName();
/*  88:160 */         if (name.startsWith("-")) {
/*  89:161 */           name = name.substring(1, name.length());
/*  90:    */         }
/*  91:163 */         String synopsis = o.commandLineParamSynopsis();
/*  92:164 */         if (!synopsis.startsWith("-")) {
/*  93:165 */           synopsis = "-" + synopsis;
/*  94:    */         }
/*  95:167 */         int numParams = o.commandLineParamIsFlag() ? 0 : 1;
/*  96:168 */         Option option = new Option(description, name, numParams, synopsis);
/*  97:169 */         unsorted[index] = option;
/*  98:170 */         index++;
/*  99:    */       }
/* 100:    */     }
/* 101:175 */     int[] sortedOpts = Utils.sort(opOrder);
/* 102:176 */     for (int i = 0; i < opOrder.length; i++) {
/* 103:177 */       if (opOrder[i] < 2147483647) {
/* 104:178 */         results.add(unsorted[sortedOpts[i]]);
/* 105:    */       }
/* 106:    */     }
/* 107:182 */     return results;
/* 108:    */   }
/* 109:    */   
/* 110:    */   public static String[] getOptionsForHierarchy(Object target, Class<?> oldestAncestorClazz)
/* 111:    */   {
/* 112:199 */     ArrayList<String> options = new ArrayList();
/* 113:200 */     for (String s : getOptions(target, target.getClass())) {
/* 114:201 */       options.add(s);
/* 115:    */     }
/* 116:204 */     Class<?> parent = target.getClass();
/* 117:    */     do
/* 118:    */     {
/* 119:206 */       parent = parent.getSuperclass();
/* 120:207 */       if (parent == null) {
/* 121:    */         break;
/* 122:    */       }
/* 123:210 */       for (String s : getOptions(target, parent)) {
/* 124:211 */         options.add(s);
/* 125:    */       }
/* 126:213 */     } while (!parent.equals(oldestAncestorClazz));
/* 127:215 */     return (String[])options.toArray(new String[options.size()]);
/* 128:    */   }
/* 129:    */   
/* 130:    */   public static String[] getOptions(Object target, Class<?> targetClazz)
/* 131:    */   {
/* 132:233 */     ArrayList<String> options = new ArrayList();
/* 133:    */     try
/* 134:    */     {
/* 135:235 */       Object[] args = new Object[0];
/* 136:236 */       Class<?> parent = targetClazz.getSuperclass();
/* 137:237 */       PropertyDescriptor[] properties = getPropertyDescriptors(targetClazz, parent);
/* 138:240 */       for (PropertyDescriptor p : properties)
/* 139:    */       {
/* 140:241 */         Method getter = p.getReadMethod();
/* 141:242 */         Method setter = p.getWriteMethod();
/* 142:243 */         if ((getter != null) && (setter != null))
/* 143:    */         {
/* 144:246 */           OptionMetadata parameterDescription = null;
/* 145:247 */           parameterDescription = (OptionMetadata)getter.getAnnotation(OptionMetadata.class);
/* 146:248 */           if (parameterDescription == null) {
/* 147:249 */             parameterDescription = (OptionMetadata)setter.getAnnotation(OptionMetadata.class);
/* 148:    */           }
/* 149:252 */           if ((parameterDescription != null) && (parameterDescription.commandLineParamName().length() > 0))
/* 150:    */           {
/* 151:254 */             Object value = getter.invoke(target, args);
/* 152:255 */             if (value != null)
/* 153:    */             {
/* 154:256 */               if (!parameterDescription.commandLineParamIsFlag()) {
/* 155:257 */                 options.add("-" + parameterDescription.commandLineParamName());
/* 156:    */               }
/* 157:260 */               if (value.getClass().isArray())
/* 158:    */               {
/* 159:261 */                 if (parameterDescription.commandLineParamIsFlag()) {
/* 160:262 */                   throw new IllegalArgumentException("Getter method for a command line flag should return a boolean value");
/* 161:    */                 }
/* 162:266 */                 int index = 0;
/* 163:267 */                 for (Object element : (Object[])value)
/* 164:    */                 {
/* 165:268 */                   if (index > 0) {
/* 166:269 */                     options.add("-" + parameterDescription.commandLineParamName());
/* 167:    */                   }
/* 168:272 */                   if ((element instanceof OptionHandler)) {
/* 169:273 */                     options.add(getOptionStringForOptionHandler((OptionHandler)element));
/* 170:    */                   } else {
/* 171:276 */                     options.add(element.toString());
/* 172:    */                   }
/* 173:278 */                   index++;
/* 174:    */                 }
/* 175:    */               }
/* 176:280 */               else if ((value instanceof OptionHandler))
/* 177:    */               {
/* 178:281 */                 if (parameterDescription.commandLineParamIsFlag()) {
/* 179:282 */                   throw new IllegalArgumentException("Getter method for a command line flag should return a boolean value");
/* 180:    */                 }
/* 181:286 */                 options.add(getOptionStringForOptionHandler((OptionHandler)value));
/* 182:    */               }
/* 183:288 */               else if ((value instanceof SelectedTag))
/* 184:    */               {
/* 185:289 */                 options.add("" + ((SelectedTag)value).getSelectedTag().getReadable());
/* 186:    */               }
/* 187:293 */               else if (parameterDescription.commandLineParamIsFlag())
/* 188:    */               {
/* 189:294 */                 if (!(value instanceof Boolean)) {
/* 190:295 */                   throw new IllegalArgumentException("Getter method for a command line flag should return a boolean value");
/* 191:    */                 }
/* 192:299 */                 if (((Boolean)value).booleanValue()) {
/* 193:300 */                   options.add("-" + parameterDescription.commandLineParamName());
/* 194:    */                 }
/* 195:    */               }
/* 196:304 */               else if (value.toString().length() > 0)
/* 197:    */               {
/* 198:305 */                 options.add(value.toString());
/* 199:    */               }
/* 200:    */               else
/* 201:    */               {
/* 202:308 */                 options.remove(options.size() - 1);
/* 203:    */               }
/* 204:    */             }
/* 205:    */           }
/* 206:    */         }
/* 207:    */       }
/* 208:    */     }
/* 209:    */     catch (Exception ex)
/* 210:    */     {
/* 211:317 */       ex.printStackTrace();
/* 212:    */     }
/* 213:320 */     return (String[])options.toArray(new String[options.size()]);
/* 214:    */   }
/* 215:    */   
/* 216:    */   protected static String getOptionStringForOptionHandler(OptionHandler handler)
/* 217:    */   {
/* 218:332 */     String optHandlerClassName = handler.getClass().getCanonicalName();
/* 219:333 */     String optsVal = Utils.joinOptions(handler.getOptions());
/* 220:334 */     String totalOptVal = optHandlerClassName + " " + optsVal;
/* 221:    */     
/* 222:336 */     return totalOptVal;
/* 223:    */   }
/* 224:    */   
/* 225:    */   public static void setOptionsForHierarchy(String[] options, Object target, Class<?> oldestAncestorClazz)
/* 226:    */   {
/* 227:353 */     setOptions(options, target, target.getClass());
/* 228:    */     
/* 229:355 */     Class<?> parent = target.getClass();
/* 230:    */     do
/* 231:    */     {
/* 232:357 */       parent = parent.getSuperclass();
/* 233:358 */       if (parent == null) {
/* 234:    */         break;
/* 235:    */       }
/* 236:362 */       setOptions(options, target, parent);
/* 237:363 */     } while (!parent.equals(oldestAncestorClazz));
/* 238:    */   }
/* 239:    */   
/* 240:    */   private static PropertyDescriptor[] getPropertyDescriptors(Class<?> targetClazz, Class<?> parent)
/* 241:    */     throws IntrospectionException
/* 242:    */   {
/* 243:378 */     PropertyDescriptor[] result = (PropertyDescriptor[])s_descriptorCache.get(targetClazz);
/* 244:379 */     if (result == null)
/* 245:    */     {
/* 246:380 */       BeanInfo bi = Introspector.getBeanInfo(targetClazz, parent);
/* 247:381 */       result = bi.getPropertyDescriptors();
/* 248:382 */       s_descriptorCache.put(targetClazz, result);
/* 249:    */     }
/* 250:385 */     return result;
/* 251:    */   }
/* 252:    */   
/* 253:    */   public static void setOptions(String[] options, Object target, Class<?> targetClazz)
/* 254:    */   {
/* 255:403 */     if ((options != null) && (options.length > 0)) {
/* 256:    */       try
/* 257:    */       {
/* 258:406 */         Object[] getterArgs = new Object[0];
/* 259:407 */         Class<?> parent = targetClazz.getSuperclass();
/* 260:408 */         PropertyDescriptor[] properties = getPropertyDescriptors(targetClazz, parent);
/* 261:411 */         for (PropertyDescriptor p : properties)
/* 262:    */         {
/* 263:412 */           Method getter = p.getReadMethod();
/* 264:413 */           Method setter = p.getWriteMethod();
/* 265:414 */           if ((getter != null) && (setter != null))
/* 266:    */           {
/* 267:417 */             OptionMetadata parameterDescription = null;
/* 268:418 */             parameterDescription = (OptionMetadata)getter.getAnnotation(OptionMetadata.class);
/* 269:419 */             if (parameterDescription == null) {
/* 270:420 */               parameterDescription = (OptionMetadata)setter.getAnnotation(OptionMetadata.class);
/* 271:    */             }
/* 272:423 */             if ((parameterDescription != null) && (parameterDescription.commandLineParamName().length() > 0))
/* 273:    */             {
/* 274:425 */               boolean processOpt = false;
/* 275:426 */               String optionValue = "";
/* 276:427 */               Object valueToSet = null;
/* 277:428 */               if (parameterDescription.commandLineParamIsFlag())
/* 278:    */               {
/* 279:429 */                 processOpt = true;
/* 280:430 */                 valueToSet = Boolean.valueOf(Utils.getFlag(parameterDescription.commandLineParamName(), options));
/* 281:    */               }
/* 282:    */               else
/* 283:    */               {
/* 284:433 */                 optionValue = Utils.getOption(parameterDescription.commandLineParamName(), options);
/* 285:    */                 
/* 286:435 */                 processOpt = optionValue.length() > 0;
/* 287:    */               }
/* 288:440 */               Object value = getter.invoke(target, getterArgs);
/* 289:441 */               if ((value != null) && (processOpt))
/* 290:    */               {
/* 291:442 */                 if ((value.getClass().isArray()) && (((Object[])value).length >= 0))
/* 292:    */                 {
/* 293:445 */                   Class<?> elementType = getter.getReturnType().getComponentType();
/* 294:    */                   
/* 295:    */ 
/* 296:    */ 
/* 297:449 */                   List<String> optionValues = new ArrayList();
/* 298:450 */                   optionValues.add(optionValue);
/* 299:    */                   for (;;)
/* 300:    */                   {
/* 301:452 */                     optionValue = Utils.getOption(parameterDescription.commandLineParamName(), options);
/* 302:454 */                     if (optionValue.length() == 0) {
/* 303:    */                       break;
/* 304:    */                     }
/* 305:457 */                     optionValues.add(optionValue);
/* 306:    */                   }
/* 307:460 */                   valueToSet = Array.newInstance(elementType, optionValues.size());
/* 308:462 */                   for (int i = 0; i < optionValues.size(); i++)
/* 309:    */                   {
/* 310:463 */                     Object elementObject = null;
/* 311:464 */                     if (elementType.isAssignableFrom(File.class)) {
/* 312:465 */                       elementObject = new File((String)optionValues.get(i));
/* 313:    */                     } else {
/* 314:467 */                       elementObject = constructOptionHandlerValue((String)optionValues.get(i));
/* 315:    */                     }
/* 316:470 */                     Array.set(valueToSet, i, elementObject);
/* 317:    */                   }
/* 318:    */                 }
/* 319:472 */                 else if ((value instanceof SelectedTag))
/* 320:    */                 {
/* 321:473 */                   Tag[] legalTags = ((SelectedTag)value).getTags();
/* 322:474 */                   int tagIndex = 2147483647;
/* 323:    */                   int z;
/* 324:    */                   try
/* 325:    */                   {
/* 326:477 */                     int specifiedID = Integer.parseInt(optionValue);
/* 327:478 */                     for (int z = 0; z < legalTags.length; z++) {
/* 328:479 */                       if (legalTags[z].getID() == specifiedID)
/* 329:    */                       {
/* 330:480 */                         tagIndex = z;
/* 331:481 */                         break;
/* 332:    */                       }
/* 333:    */                     }
/* 334:    */                   }
/* 335:    */                   catch (NumberFormatException e)
/* 336:    */                   {
/* 337:486 */                     z = 0;
/* 338:    */                   }
/* 339:486 */                   for (; z < legalTags.length; z++) {
/* 340:487 */                     if (legalTags[z].getReadable().equals(optionValue.trim()))
/* 341:    */                     {
/* 342:488 */                       tagIndex = z;
/* 343:489 */                       break;
/* 344:    */                     }
/* 345:    */                   }
/* 346:493 */                   if (tagIndex != 2147483647) {
/* 347:494 */                     valueToSet = new SelectedTag(tagIndex, legalTags);
/* 348:    */                   } else {
/* 349:496 */                     throw new Exception("Unable to set option: '" + parameterDescription.commandLineParamName() + "'. This option takes a SelectedTag argument, and " + "the supplied value of '" + optionValue + "' " + "does not match any of the legal IDs or strings " + "for it.");
/* 350:    */                   }
/* 351:    */                 }
/* 352:503 */                 else if ((value instanceof OptionHandler))
/* 353:    */                 {
/* 354:504 */                   valueToSet = constructOptionHandlerValue(optionValue);
/* 355:    */                 }
/* 356:505 */                 else if ((value instanceof Number))
/* 357:    */                 {
/* 358:    */                   try
/* 359:    */                   {
/* 360:507 */                     if ((value instanceof Integer)) {
/* 361:508 */                       valueToSet = new Integer(optionValue);
/* 362:509 */                     } else if ((value instanceof Long)) {
/* 363:510 */                       valueToSet = new Long(optionValue);
/* 364:511 */                     } else if ((value instanceof Double)) {
/* 365:512 */                       valueToSet = new Double(optionValue);
/* 366:513 */                     } else if ((value instanceof Float)) {
/* 367:514 */                       valueToSet = new Float(optionValue);
/* 368:    */                     }
/* 369:    */                   }
/* 370:    */                   catch (NumberFormatException e)
/* 371:    */                   {
/* 372:517 */                     throw new Exception("Option: '" + parameterDescription.commandLineParamName() + "' requires a " + value.getClass().getCanonicalName() + " argument");
/* 373:    */                   }
/* 374:    */                 }
/* 375:522 */                 else if ((value instanceof String))
/* 376:    */                 {
/* 377:523 */                   valueToSet = optionValue;
/* 378:    */                 }
/* 379:524 */                 else if ((value instanceof File))
/* 380:    */                 {
/* 381:525 */                   valueToSet = new File(optionValue);
/* 382:    */                 }
/* 383:528 */                 if (valueToSet != null) {
/* 384:533 */                   setOption(setter, target, valueToSet);
/* 385:    */                 }
/* 386:    */               }
/* 387:    */             }
/* 388:    */           }
/* 389:    */         }
/* 390:    */       }
/* 391:    */       catch (Exception ex)
/* 392:    */       {
/* 393:539 */         ex.printStackTrace();
/* 394:    */       }
/* 395:    */     }
/* 396:    */   }
/* 397:    */   
/* 398:    */   protected static Object constructOptionHandlerValue(String optionValue)
/* 399:    */     throws Exception
/* 400:    */   {
/* 401:555 */     String[] optHandlerSpec = Utils.splitOptions(optionValue);
/* 402:556 */     if (optHandlerSpec.length == 0) {
/* 403:557 */       throw new Exception("Invalid option handler specification string '" + optionValue);
/* 404:    */     }
/* 405:560 */     String optionHandler = optHandlerSpec[0];
/* 406:561 */     optHandlerSpec[0] = "";
/* 407:562 */     Object handler = Utils.forName(null, optionHandler, optHandlerSpec);
/* 408:    */     
/* 409:564 */     return handler;
/* 410:    */   }
/* 411:    */   
/* 412:    */   public static void deleteOption(List<Option> list, String name)
/* 413:    */   {
/* 414:575 */     for (Iterator<Option> iter = list.listIterator(); iter.hasNext();)
/* 415:    */     {
/* 416:576 */       Option a = (Option)iter.next();
/* 417:577 */       if (a.name().equals(name)) {
/* 418:578 */         iter.remove();
/* 419:    */       }
/* 420:    */     }
/* 421:    */   }
/* 422:    */   
/* 423:    */   public static void deleteOptionString(List<String> list, String name)
/* 424:    */   {
/* 425:592 */     for (Iterator<String> iter = list.listIterator(); iter.hasNext();)
/* 426:    */     {
/* 427:593 */       String a = (String)iter.next();
/* 428:594 */       if (a.equals(name))
/* 429:    */       {
/* 430:595 */         iter.remove();
/* 431:596 */         iter.next();
/* 432:597 */         iter.remove();
/* 433:    */       }
/* 434:    */     }
/* 435:    */   }
/* 436:    */   
/* 437:    */   public static void deleteFlagString(List<String> list, String name)
/* 438:    */   {
/* 439:611 */     for (Iterator<String> iter = list.listIterator(); iter.hasNext();)
/* 440:    */     {
/* 441:612 */       String a = (String)iter.next();
/* 442:613 */       if (a.equals(name)) {
/* 443:614 */         iter.remove();
/* 444:    */       }
/* 445:    */     }
/* 446:    */   }
/* 447:    */   
/* 448:    */   protected static void setOption(Method setter, Object target, Object valueToSet)
/* 449:    */     throws InvocationTargetException, IllegalAccessException
/* 450:    */   {
/* 451:631 */     Object[] setterArgs = { valueToSet };
/* 452:632 */     setter.invoke(target, setterArgs);
/* 453:    */   }
/* 454:    */   
/* 455:    */   public String description()
/* 456:    */   {
/* 457:642 */     return this.m_Description;
/* 458:    */   }
/* 459:    */   
/* 460:    */   public String name()
/* 461:    */   {
/* 462:652 */     return this.m_Name;
/* 463:    */   }
/* 464:    */   
/* 465:    */   public int numArguments()
/* 466:    */   {
/* 467:662 */     return this.m_NumArguments;
/* 468:    */   }
/* 469:    */   
/* 470:    */   public String synopsis()
/* 471:    */   {
/* 472:672 */     return this.m_Synopsis;
/* 473:    */   }
/* 474:    */   
/* 475:    */   public String getRevision()
/* 476:    */   {
/* 477:681 */     return RevisionUtils.extract("$Revision: 12505 $");
/* 478:    */   }
/* 479:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.Option
 * JD-Core Version:    0.7.0.1
 */