/*   1:    */ package weka.knowledgeflow;
/*   2:    */ 
/*   3:    */ import java.beans.BeanInfo;
/*   4:    */ import java.beans.Beans;
/*   5:    */ import java.beans.Introspector;
/*   6:    */ import java.beans.PropertyDescriptor;
/*   7:    */ import java.io.BufferedReader;
/*   8:    */ import java.io.BufferedWriter;
/*   9:    */ import java.io.File;
/*  10:    */ import java.io.FileNotFoundException;
/*  11:    */ import java.io.FileReader;
/*  12:    */ import java.io.FileWriter;
/*  13:    */ import java.io.IOException;
/*  14:    */ import java.io.InputStream;
/*  15:    */ import java.io.InputStreamReader;
/*  16:    */ import java.io.OutputStream;
/*  17:    */ import java.io.OutputStreamWriter;
/*  18:    */ import java.io.PrintStream;
/*  19:    */ import java.io.Reader;
/*  20:    */ import java.io.StringReader;
/*  21:    */ import java.io.Writer;
/*  22:    */ import java.lang.annotation.Annotation;
/*  23:    */ import java.lang.reflect.Method;
/*  24:    */ import java.util.Iterator;
/*  25:    */ import java.util.List;
/*  26:    */ import java.util.Map;
/*  27:    */ import java.util.Map.Entry;
/*  28:    */ import weka.core.EnumHelper;
/*  29:    */ import weka.core.Environment;
/*  30:    */ import weka.core.EnvironmentHandler;
/*  31:    */ import weka.core.OptionHandler;
/*  32:    */ import weka.core.Settings;
/*  33:    */ import weka.core.Utils;
/*  34:    */ import weka.core.WekaException;
/*  35:    */ import weka.core.converters.AbstractFileLoader;
/*  36:    */ import weka.core.converters.AbstractFileSaver;
/*  37:    */ import weka.core.converters.ArffLoader;
/*  38:    */ import weka.core.converters.CSVSaver;
/*  39:    */ import weka.core.converters.FileSourcedConverter;
/*  40:    */ import weka.core.json.JSONNode;
/*  41:    */ import weka.gui.FilePropertyMetadata;
/*  42:    */ import weka.gui.knowledgeflow.StepVisual;
/*  43:    */ import weka.knowledgeflow.steps.ClassAssigner;
/*  44:    */ import weka.knowledgeflow.steps.NotPersistable;
/*  45:    */ import weka.knowledgeflow.steps.Step;
/*  46:    */ import weka.knowledgeflow.steps.TrainingSetMaker;
/*  47:    */ 
/*  48:    */ public class JSONFlowUtils
/*  49:    */ {
/*  50:    */   public static final String FLOW_NAME = "flow_name";
/*  51:    */   public static final String STEPS = "steps";
/*  52:    */   public static final String OPTIONHANDLER = "optionHandler";
/*  53:    */   public static final String OPTIONS = "options";
/*  54:    */   public static final String LOADER = "loader";
/*  55:    */   public static final String SAVER = "saver";
/*  56:    */   public static final String ENUM_HELPER = "enumHelper";
/*  57:    */   public static final String CLASS = "class";
/*  58:    */   public static final String PROPERTIES = "properties";
/*  59:    */   public static final String CONNECTIONS = "connections";
/*  60:    */   public static final String COORDINATES = "coordinates";
/*  61:    */   
/*  62:    */   protected static void addNameValue(StringBuilder b, String name, String value, boolean comma)
/*  63:    */   {
/*  64: 92 */     b.append(name).append(" : ").append(value);
/*  65: 93 */     if (comma) {
/*  66: 94 */       b.append(",");
/*  67:    */     }
/*  68:    */   }
/*  69:    */   
/*  70:    */   protected static void addOptionHandler(String propName, OptionHandler handler, JSONNode json)
/*  71:    */   {
/*  72:107 */     JSONNode optionNode = json.addObject(propName);
/*  73:108 */     optionNode.addPrimitive("type", "optionHandler");
/*  74:109 */     optionNode.addPrimitive("class", handler.getClass().getCanonicalName());
/*  75:110 */     optionNode.addPrimitive("options", Utils.joinOptions(handler.getOptions()));
/*  76:    */   }
/*  77:    */   
/*  78:    */   protected static void addEnum(String propName, Enum ee, JSONNode json)
/*  79:    */   {
/*  80:121 */     JSONNode enumNode = json.addObject(propName);
/*  81:122 */     enumNode.addPrimitive("type", "enumHelper");
/*  82:123 */     EnumHelper helper = new EnumHelper(ee);
/*  83:124 */     enumNode.addPrimitive("class", helper.getEnumClass());
/*  84:125 */     enumNode.addPrimitive("value", helper.getSelectedEnumValue());
/*  85:    */   }
/*  86:    */   
/*  87:    */   protected static void addSaver(String propName, weka.core.converters.Saver saver, JSONNode json)
/*  88:    */   {
/*  89:137 */     JSONNode saverNode = json.addObject(propName);
/*  90:138 */     saverNode.addPrimitive("type", "saver");
/*  91:139 */     saverNode.addPrimitive("class", saver.getClass().getCanonicalName());
/*  92:    */     
/*  93:141 */     String prefix = "";
/*  94:142 */     String dir = "";
/*  95:144 */     if ((saver instanceof AbstractFileSaver))
/*  96:    */     {
/*  97:145 */       ((AbstractFileSaver)saver).retrieveFile();
/*  98:146 */       prefix = ((AbstractFileSaver)saver).filePrefix();
/*  99:147 */       dir = ((AbstractFileSaver)saver).retrieveDir();
/* 100:    */       
/* 101:    */ 
/* 102:    */ 
/* 103:151 */       dir = dir.replace('\\', '/');
/* 104:    */     }
/* 105:154 */     Boolean relativeB = null;
/* 106:155 */     if ((saver instanceof FileSourcedConverter)) {
/* 107:156 */       relativeB = Boolean.valueOf(((FileSourcedConverter)saver).getUseRelativePath());
/* 108:    */     }
/* 109:161 */     saverNode.addPrimitive("filePath", "");
/* 110:162 */     saverNode.addPrimitive("dir", dir);
/* 111:163 */     saverNode.addPrimitive("prefix", prefix);
/* 112:165 */     if (relativeB != null) {
/* 113:166 */       saverNode.addPrimitive("useRelativePath", relativeB);
/* 114:    */     }
/* 115:169 */     if ((saver instanceof OptionHandler))
/* 116:    */     {
/* 117:170 */       String optsString = Utils.joinOptions(((OptionHandler)saver).getOptions());
/* 118:    */       
/* 119:172 */       saverNode.addPrimitive("options", optsString);
/* 120:    */     }
/* 121:    */   }
/* 122:    */   
/* 123:    */   protected static void addLoader(String propName, weka.core.converters.Loader loader, JSONNode json)
/* 124:    */   {
/* 125:185 */     JSONNode loaderNode = json.addObject(propName);
/* 126:186 */     loaderNode.addPrimitive("type", "loader");
/* 127:187 */     loaderNode.addPrimitive("class", loader.getClass().getCanonicalName());
/* 128:    */     
/* 129:189 */     File file = null;
/* 130:190 */     if ((loader instanceof AbstractFileLoader)) {
/* 131:191 */       file = ((AbstractFileLoader)loader).retrieveFile();
/* 132:    */     }
/* 133:194 */     Boolean relativeB = null;
/* 134:195 */     if ((loader instanceof FileSourcedConverter)) {
/* 135:196 */       relativeB = Boolean.valueOf(((FileSourcedConverter)loader).getUseRelativePath());
/* 136:    */     }
/* 137:201 */     if ((file != null) && (!file.isDirectory()))
/* 138:    */     {
/* 139:202 */       String withResourceSeparators = file.getPath().replace(File.pathSeparatorChar, '/');
/* 140:    */       
/* 141:204 */       boolean notAbsolute = (((AbstractFileLoader)loader).getUseRelativePath()) || (((loader instanceof EnvironmentHandler)) && (Environment.containsEnvVariables(file.getPath()))) || (JSONFlowUtils.class.getClassLoader().getResource(withResourceSeparators) != null) || (!file.exists());
/* 142:    */       
/* 143:    */ 
/* 144:    */ 
/* 145:    */ 
/* 146:    */ 
/* 147:    */ 
/* 148:    */ 
/* 149:212 */       String path = notAbsolute ? file.getPath() : file.getAbsolutePath();
/* 150:    */       
/* 151:    */ 
/* 152:    */ 
/* 153:216 */       path = path.replace('\\', '/');
/* 154:    */       
/* 155:218 */       loaderNode.addPrimitive("filePath", path);
/* 156:    */     }
/* 157:    */     else
/* 158:    */     {
/* 159:220 */       loaderNode.addPrimitive("filePath", "");
/* 160:    */     }
/* 161:223 */     if (relativeB != null) {
/* 162:224 */       loaderNode.addPrimitive("useRelativePath", relativeB);
/* 163:    */     }
/* 164:227 */     if ((loader instanceof OptionHandler))
/* 165:    */     {
/* 166:228 */       String optsString = Utils.joinOptions(((OptionHandler)loader).getOptions());
/* 167:    */       
/* 168:230 */       loaderNode.addPrimitive("options", optsString);
/* 169:    */     }
/* 170:    */   }
/* 171:    */   
/* 172:    */   protected static void addStepJSONtoFlowArray(JSONNode stepArray, StepManagerImpl stepManager)
/* 173:    */     throws WekaException
/* 174:    */   {
/* 175:245 */     JSONNode step = stepArray.addObjectArrayElement();
/* 176:246 */     step.addPrimitive("class", stepManager.getManagedStep().getClass().getCanonicalName());
/* 177:    */     
/* 178:    */ 
/* 179:249 */     JSONNode properties = step.addObject("properties");
/* 180:    */     try
/* 181:    */     {
/* 182:251 */       Step theStep = stepManager.getManagedStep();
/* 183:252 */       BeanInfo bi = Introspector.getBeanInfo(theStep.getClass());
/* 184:253 */       PropertyDescriptor[] stepProps = bi.getPropertyDescriptors();
/* 185:255 */       for (PropertyDescriptor p : stepProps) {
/* 186:256 */         if ((!p.isHidden()) && (!p.isExpert()))
/* 187:    */         {
/* 188:260 */           String name = p.getDisplayName();
/* 189:261 */           Method getter = p.getReadMethod();
/* 190:262 */           Method setter = p.getWriteMethod();
/* 191:263 */           if ((getter != null) && (setter != null))
/* 192:    */           {
/* 193:266 */             boolean skip = false;
/* 194:267 */             for (Annotation a : getter.getAnnotations()) {
/* 195:268 */               if ((a instanceof NotPersistable))
/* 196:    */               {
/* 197:269 */                 skip = true;
/* 198:270 */                 break;
/* 199:    */               }
/* 200:    */             }
/* 201:273 */             if (!skip)
/* 202:    */             {
/* 203:277 */               Object[] args = new Object[0];
/* 204:278 */               Object propValue = getter.invoke(theStep, args);
/* 205:279 */               if (propValue == null) {
/* 206:280 */                 properties.addNull(name);
/* 207:281 */               } else if ((propValue instanceof Boolean)) {
/* 208:282 */                 properties.addPrimitive(name, (Boolean)propValue);
/* 209:283 */               } else if (((propValue instanceof Integer)) || ((propValue instanceof Long))) {
/* 210:284 */                 properties.addPrimitive(name, new Integer(((Number)propValue).intValue()));
/* 211:286 */               } else if ((propValue instanceof Double)) {
/* 212:287 */                 properties.addPrimitive(name, (Double)propValue);
/* 213:288 */               } else if ((propValue instanceof Number)) {
/* 214:289 */                 properties.addPrimitive(name, new Double(((Number)propValue).doubleValue()));
/* 215:291 */               } else if ((propValue instanceof weka.core.converters.Loader)) {
/* 216:292 */                 addLoader(name, (weka.core.converters.Loader)propValue, properties);
/* 217:293 */               } else if ((propValue instanceof weka.core.converters.Saver)) {
/* 218:294 */                 addSaver(name, (weka.core.converters.Saver)propValue, properties);
/* 219:295 */               } else if ((propValue instanceof OptionHandler)) {
/* 220:296 */                 addOptionHandler(name, (OptionHandler)propValue, properties);
/* 221:297 */               } else if ((propValue instanceof Enum)) {
/* 222:298 */                 addEnum(name, (Enum)propValue, properties);
/* 223:    */               } else {
/* 224:300 */                 properties.addPrimitive(name, propValue.toString());
/* 225:    */               }
/* 226:    */             }
/* 227:    */           }
/* 228:    */         }
/* 229:    */       }
/* 230:    */     }
/* 231:    */     catch (Exception ex)
/* 232:    */     {
/* 233:304 */       throw new WekaException(ex);
/* 234:    */     }
/* 235:307 */     JSONNode connections = step.addObject("connections");
/* 236:308 */     for (Map.Entry<String, List<StepManager>> e : stepManager.m_connectedByTypeOutgoing.entrySet())
/* 237:    */     {
/* 238:310 */       String connName = (String)e.getKey();
/* 239:311 */       connTypeArray = connections.addArray(connName);
/* 240:312 */       for (StepManager c : (List)e.getValue()) {
/* 241:313 */         connTypeArray.addArrayElement(c.getName());
/* 242:    */       }
/* 243:    */     }
/* 244:    */     JSONNode connTypeArray;
/* 245:317 */     if (stepManager.getStepVisual() != null)
/* 246:    */     {
/* 247:318 */       String coords = "" + stepManager.getStepVisual().getX() + "," + stepManager.getStepVisual().getY();
/* 248:    */       
/* 249:    */ 
/* 250:321 */       step.addPrimitive("coordinates", coords);
/* 251:    */     }
/* 252:    */   }
/* 253:    */   
/* 254:    */   protected static weka.core.converters.Loader readStepPropertyLoader(JSONNode loaderNode)
/* 255:    */     throws WekaException
/* 256:    */   {
/* 257:335 */     String clazz = loaderNode.getChild("class").getValue().toString();
/* 258:    */     try
/* 259:    */     {
/* 260:337 */       weka.core.converters.Loader loader = (weka.core.converters.Loader)Beans.instantiate(JSONFlowUtils.class.getClassLoader(), clazz);
/* 261:341 */       if ((loader instanceof OptionHandler))
/* 262:    */       {
/* 263:342 */         String optionString = loaderNode.getChild("options").getValue().toString();
/* 264:344 */         if ((optionString != null) && (optionString.length() > 0)) {
/* 265:345 */           ((OptionHandler)loader).setOptions(Utils.splitOptions(optionString));
/* 266:    */         }
/* 267:    */       }
/* 268:349 */       if ((loader instanceof AbstractFileLoader))
/* 269:    */       {
/* 270:350 */         String filePath = loaderNode.getChild("filePath").getValue().toString();
/* 271:351 */         if (filePath.length() > 0) {
/* 272:353 */           ((AbstractFileLoader)loader).setSource(new File(filePath));
/* 273:    */         }
/* 274:    */       }
/* 275:358 */       if ((loader instanceof FileSourcedConverter))
/* 276:    */       {
/* 277:359 */         Boolean relativePath = (Boolean)loaderNode.getChild("useRelativePath").getValue();
/* 278:    */         
/* 279:361 */         ((FileSourcedConverter)loader).setUseRelativePath(relativePath.booleanValue());
/* 280:    */       }
/* 281:366 */       return loader;
/* 282:    */     }
/* 283:    */     catch (Exception ex)
/* 284:    */     {
/* 285:368 */       throw new WekaException(ex);
/* 286:    */     }
/* 287:    */   }
/* 288:    */   
/* 289:    */   protected static weka.core.converters.Saver readStepPropertySaver(JSONNode saverNode)
/* 290:    */     throws WekaException
/* 291:    */   {
/* 292:381 */     String clazz = saverNode.getChild("class").getValue().toString();
/* 293:    */     try
/* 294:    */     {
/* 295:383 */       weka.core.converters.Saver saver = (weka.core.converters.Saver)Beans.instantiate(JSONFlowUtils.class.getClassLoader(), clazz);
/* 296:387 */       if ((saver instanceof OptionHandler))
/* 297:    */       {
/* 298:388 */         String optionString = saverNode.getChild("options").getValue().toString();
/* 299:389 */         if ((optionString != null) && (optionString.length() > 0)) {
/* 300:390 */           ((OptionHandler)saver).setOptions(Utils.splitOptions(optionString));
/* 301:    */         }
/* 302:    */       }
/* 303:394 */       if ((saver instanceof AbstractFileSaver))
/* 304:    */       {
/* 305:395 */         String dir = saverNode.getChild("dir").getValue().toString();
/* 306:396 */         String prefix = saverNode.getChild("prefix").getValue().toString();
/* 307:397 */         if ((dir != null) && (prefix != null))
/* 308:    */         {
/* 309:398 */           ((AbstractFileSaver)saver).setDir(dir);
/* 310:399 */           ((AbstractFileSaver)saver).setFilePrefix(prefix);
/* 311:    */         }
/* 312:    */       }
/* 313:404 */       if ((saver instanceof FileSourcedConverter))
/* 314:    */       {
/* 315:405 */         Boolean relativePath = (Boolean)saverNode.getChild("useRelativePath").getValue();
/* 316:    */         
/* 317:407 */         ((FileSourcedConverter)saver).setUseRelativePath(relativePath.booleanValue());
/* 318:    */       }
/* 319:410 */       return saver;
/* 320:    */     }
/* 321:    */     catch (Exception ex)
/* 322:    */     {
/* 323:412 */       throw new WekaException(ex);
/* 324:    */     }
/* 325:    */   }
/* 326:    */   
/* 327:    */   protected static OptionHandler readStepPropertyOptionHandler(JSONNode optionHNode)
/* 328:    */     throws WekaException
/* 329:    */   {
/* 330:426 */     String clazz = optionHNode.getChild("class").getValue().toString();
/* 331:    */     try
/* 332:    */     {
/* 333:428 */       OptionHandler oh = (OptionHandler)Beans.instantiate(JSONFlowUtils.class.getClassLoader(), clazz);
/* 334:    */       
/* 335:    */ 
/* 336:431 */       String optionString = optionHNode.getChild("options").getValue().toString();
/* 337:432 */       if ((optionString != null) && (optionString.length() > 0))
/* 338:    */       {
/* 339:433 */         String[] options = Utils.splitOptions(optionString);
/* 340:    */         
/* 341:435 */         oh.setOptions(options);
/* 342:    */       }
/* 343:438 */       return oh;
/* 344:    */     }
/* 345:    */     catch (Exception ex)
/* 346:    */     {
/* 347:440 */       throw new WekaException(ex);
/* 348:    */     }
/* 349:    */   }
/* 350:    */   
/* 351:    */   protected static Object readStepPropertyEnum(JSONNode enumNode)
/* 352:    */     throws WekaException
/* 353:    */   {
/* 354:453 */     EnumHelper helper = new EnumHelper();
/* 355:454 */     String clazz = enumNode.getChild("class").getValue().toString();
/* 356:455 */     String value = enumNode.getChild("value").getValue().toString();
/* 357:    */     try
/* 358:    */     {
/* 359:458 */       return EnumHelper.valueFromString(clazz, value);
/* 360:    */     }
/* 361:    */     catch (Exception ex)
/* 362:    */     {
/* 363:460 */       throw new WekaException(ex);
/* 364:    */     }
/* 365:    */   }
/* 366:    */   
/* 367:    */   protected static Object readStepObjectProperty(JSONNode propNode)
/* 368:    */     throws Exception
/* 369:    */   {
/* 370:475 */     String type = propNode.getChild("type").getValue().toString();
/* 371:476 */     if (type.equals("optionHandler")) {
/* 372:477 */       return readStepPropertyOptionHandler(propNode);
/* 373:    */     }
/* 374:478 */     if (type.equals("loader")) {
/* 375:479 */       return readStepPropertyLoader(propNode);
/* 376:    */     }
/* 377:480 */     if (type.equals("saver")) {
/* 378:481 */       return readStepPropertySaver(propNode);
/* 379:    */     }
/* 380:482 */     if (type.equals("enumHelper")) {
/* 381:483 */       return readStepPropertyEnum(propNode);
/* 382:    */     }
/* 383:485 */     throw new WekaException("Unknown object property type: " + type);
/* 384:    */   }
/* 385:    */   
/* 386:    */   protected static File checkForFileProp(Object theValue, PropertyDescriptor propD)
/* 387:    */   {
/* 388:501 */     Method writeMethod = propD.getWriteMethod();
/* 389:502 */     Method readMethod = propD.getReadMethod();
/* 390:503 */     if ((writeMethod != null) && (readMethod != null))
/* 391:    */     {
/* 392:504 */       FilePropertyMetadata fM = (FilePropertyMetadata)writeMethod.getAnnotation(FilePropertyMetadata.class);
/* 393:506 */       if (fM == null) {
/* 394:507 */         fM = (FilePropertyMetadata)readMethod.getAnnotation(FilePropertyMetadata.class);
/* 395:    */       }
/* 396:510 */       if (fM != null) {
/* 397:511 */         return new File(theValue.toString());
/* 398:    */       }
/* 399:    */     }
/* 400:514 */     return null;
/* 401:    */   }
/* 402:    */   
/* 403:    */   protected static void readStep(JSONNode stepNode, Flow flow)
/* 404:    */     throws WekaException
/* 405:    */   {
/* 406:526 */     String clazz = stepNode.getChild("class").getValue().toString();
/* 407:527 */     Object step = null;
/* 408:528 */     Step theStep = null;
/* 409:    */     try
/* 410:    */     {
/* 411:530 */       step = Beans.instantiate(JSONFlowUtils.class.getClassLoader(), clazz);
/* 412:532 */       if (!(step instanceof Step)) {
/* 413:533 */         throw new WekaException("Instantiated a knowledge flow step that does not implement StepComponent!");
/* 414:    */       }
/* 415:536 */       theStep = (Step)step;
/* 416:    */       
/* 417:538 */       JSONNode properties = stepNode.getChild("properties");
/* 418:539 */       for (int i = 0; i < properties.getChildCount(); i++)
/* 419:    */       {
/* 420:540 */         JSONNode aProp = (JSONNode)properties.getChildAt(i);
/* 421:    */         
/* 422:542 */         Object valueToSet = null;
/* 423:543 */         if (aProp.isObject()) {
/* 424:544 */           valueToSet = readStepObjectProperty(aProp);
/* 425:545 */         } else if (!aProp.isArray()) {
/* 426:548 */           valueToSet = aProp.getValue();
/* 427:    */         }
/* 428:551 */         if (valueToSet != null)
/* 429:    */         {
/* 430:552 */           PropertyDescriptor propD = new PropertyDescriptor(aProp.getName(), theStep.getClass());
/* 431:    */           
/* 432:554 */           File checkForFileProp = checkForFileProp(valueToSet, propD);
/* 433:555 */           if (checkForFileProp != null) {
/* 434:556 */             valueToSet = checkForFileProp;
/* 435:    */           }
/* 436:558 */           Method writeMethod = propD.getWriteMethod();
/* 437:559 */           if (writeMethod == null)
/* 438:    */           {
/* 439:560 */             System.err.println("Unable to obtain a setter method for property '" + aProp.getName() + "' in step class '" + clazz);
/* 440:    */           }
/* 441:    */           else
/* 442:    */           {
/* 443:565 */             Object[] arguments = { valueToSet };
/* 444:566 */             writeMethod.invoke(theStep, arguments);
/* 445:    */           }
/* 446:    */         }
/* 447:    */       }
/* 448:    */     }
/* 449:    */     catch (Exception ex)
/* 450:    */     {
/* 451:572 */       throw new WekaException(ex);
/* 452:    */     }
/* 453:575 */     StepManagerImpl manager = new StepManagerImpl(theStep);
/* 454:    */     
/* 455:577 */     flow.addStep(manager);
/* 456:    */     
/* 457:    */ 
/* 458:580 */     JSONNode coords = stepNode.getChild("coordinates");
/* 459:581 */     if (coords != null)
/* 460:    */     {
/* 461:582 */       String[] vals = coords.getValue().toString().split(",");
/* 462:583 */       int x = Integer.parseInt(vals[0]);
/* 463:584 */       int y = Integer.parseInt(vals[1]);
/* 464:585 */       manager.m_x = x;
/* 465:586 */       manager.m_y = y;
/* 466:    */     }
/* 467:    */   }
/* 468:    */   
/* 469:    */   protected static void readConnectionsForStep(JSONNode step, Flow flow)
/* 470:    */     throws WekaException
/* 471:    */   {
/* 472:599 */     readConnectionsForStep(step, flow, false);
/* 473:    */   }
/* 474:    */   
/* 475:    */   protected static void readConnectionsForStep(JSONNode step, Flow flow, boolean dontComplainAboutMissingConnections)
/* 476:    */     throws WekaException
/* 477:    */   {
/* 478:614 */     JSONNode properties = step.getChild("properties");
/* 479:615 */     String stepName = properties.getChild("name").getValue().toString();
/* 480:616 */     StepManagerImpl manager = flow.findStep(stepName);
/* 481:    */     
/* 482:618 */     JSONNode connections = step.getChild("connections");
/* 483:619 */     for (int i = 0; i < connections.getChildCount(); i++)
/* 484:    */     {
/* 485:620 */       JSONNode conn = (JSONNode)connections.getChildAt(i);
/* 486:621 */       String conName = conn.getName();
/* 487:623 */       if (!conn.isArray()) {
/* 488:624 */         throw new WekaException("Was expecting an array of connected step names for a the connection '" + conName + "'");
/* 489:    */       }
/* 490:629 */       for (int j = 0; j < conn.getChildCount(); j++)
/* 491:    */       {
/* 492:630 */         JSONNode connectedStepName = (JSONNode)conn.getChildAt(j);
/* 493:631 */         StepManagerImpl targetManager = flow.findStep(connectedStepName.getValue().toString());
/* 494:633 */         if ((targetManager == null) && (!dontComplainAboutMissingConnections)) {
/* 495:634 */           throw new WekaException("Could not find the target step '" + connectedStepName.getValue().toString() + "' for connection " + "'" + connectedStepName.getValue().toString());
/* 496:    */         }
/* 497:639 */         if (targetManager != null) {
/* 498:640 */           manager.addOutgoingConnection(conName, targetManager, true);
/* 499:    */         }
/* 500:    */       }
/* 501:    */     }
/* 502:    */   }
/* 503:    */   
/* 504:    */   public static void writeFlow(Flow flow, Writer writer)
/* 505:    */     throws WekaException
/* 506:    */   {
/* 507:    */     try
/* 508:    */     {
/* 509:656 */       String flowJSON = flowToJSON(flow);
/* 510:657 */       writer.write(flowJSON); return;
/* 511:    */     }
/* 512:    */     catch (IOException ex)
/* 513:    */     {
/* 514:659 */       throw new WekaException(ex);
/* 515:    */     }
/* 516:    */     finally
/* 517:    */     {
/* 518:    */       try
/* 519:    */       {
/* 520:662 */         writer.flush();
/* 521:663 */         writer.close();
/* 522:    */       }
/* 523:    */       catch (IOException e)
/* 524:    */       {
/* 525:665 */         e.printStackTrace();
/* 526:    */       }
/* 527:    */     }
/* 528:    */   }
/* 529:    */   
/* 530:    */   public static void writeFlow(Flow flow, OutputStream os)
/* 531:    */     throws WekaException
/* 532:    */   {
/* 533:679 */     OutputStreamWriter osw = new OutputStreamWriter(os);
/* 534:680 */     writeFlow(flow, osw);
/* 535:    */   }
/* 536:    */   
/* 537:    */   public static void writeFlow(Flow flow, File file)
/* 538:    */     throws WekaException
/* 539:    */   {
/* 540:    */     try
/* 541:    */     {
/* 542:692 */       Writer w = new BufferedWriter(new FileWriter(file));
/* 543:693 */       writeFlow(flow, w);
/* 544:    */     }
/* 545:    */     catch (IOException ex)
/* 546:    */     {
/* 547:695 */       throw new WekaException(ex);
/* 548:    */     }
/* 549:    */   }
/* 550:    */   
/* 551:    */   public static Flow readFlow(File file)
/* 552:    */     throws WekaException
/* 553:    */   {
/* 554:707 */     return readFlow(file, false);
/* 555:    */   }
/* 556:    */   
/* 557:    */   public static Flow readFlow(File file, boolean dontComplainAboutMissingConnections)
/* 558:    */     throws WekaException
/* 559:    */   {
/* 560:    */     try
/* 561:    */     {
/* 562:722 */       Reader r = new BufferedReader(new FileReader(file));
/* 563:723 */       return readFlow(r, dontComplainAboutMissingConnections);
/* 564:    */     }
/* 565:    */     catch (FileNotFoundException e)
/* 566:    */     {
/* 567:725 */       throw new WekaException(e);
/* 568:    */     }
/* 569:    */   }
/* 570:    */   
/* 571:    */   public static Flow readFlow(InputStream is)
/* 572:    */     throws WekaException
/* 573:    */   {
/* 574:737 */     return readFlow(is, false);
/* 575:    */   }
/* 576:    */   
/* 577:    */   public static Flow readFlow(InputStream is, boolean dontComplainAboutMissingConnections)
/* 578:    */     throws WekaException
/* 579:    */   {
/* 580:751 */     InputStreamReader isr = new InputStreamReader(is);
/* 581:    */     
/* 582:753 */     return readFlow(isr, dontComplainAboutMissingConnections);
/* 583:    */   }
/* 584:    */   
/* 585:    */   public static Flow readFlow(Reader sr)
/* 586:    */     throws WekaException
/* 587:    */   {
/* 588:764 */     return readFlow(sr, false);
/* 589:    */   }
/* 590:    */   
/* 591:    */   public static Flow readFlow(Reader sr, boolean dontComplainAboutMissingConnections)
/* 592:    */     throws WekaException
/* 593:    */   {
/* 594:778 */     flow = new Flow();
/* 595:    */     try
/* 596:    */     {
/* 597:781 */       JSONNode root = JSONNode.read(sr);
/* 598:782 */       flow.setFlowName(root.getChild("flow_name").getValue().toString());
/* 599:783 */       JSONNode stepsArray = root.getChild("steps");
/* 600:784 */       if (stepsArray == null) {
/* 601:785 */         throw new WekaException("Flow JSON does not contain a steps array!");
/* 602:    */       }
/* 603:788 */       for (int i = 0; i < stepsArray.getChildCount(); i++)
/* 604:    */       {
/* 605:789 */         JSONNode aStep = (JSONNode)stepsArray.getChildAt(i);
/* 606:790 */         readStep(aStep, flow);
/* 607:    */       }
/* 608:794 */       for (int i = 0; i < stepsArray.getChildCount(); i++)
/* 609:    */       {
/* 610:795 */         JSONNode aStep = (JSONNode)stepsArray.getChildAt(i);
/* 611:796 */         readConnectionsForStep(aStep, flow, dontComplainAboutMissingConnections);
/* 612:    */       }
/* 613:808 */       return flow;
/* 614:    */     }
/* 615:    */     catch (Exception ex)
/* 616:    */     {
/* 617:799 */       throw new WekaException(ex);
/* 618:    */     }
/* 619:    */     finally
/* 620:    */     {
/* 621:    */       try
/* 622:    */       {
/* 623:802 */         sr.close();
/* 624:    */       }
/* 625:    */       catch (IOException e)
/* 626:    */       {
/* 627:804 */         e.printStackTrace();
/* 628:    */       }
/* 629:    */     }
/* 630:    */   }
/* 631:    */   
/* 632:    */   public static Flow JSONToFlow(String flowJSON, boolean dontComplainAboutMissingConnections)
/* 633:    */     throws WekaException
/* 634:    */   {
/* 635:823 */     StringReader sr = new StringReader(flowJSON);
/* 636:824 */     return readFlow(sr, dontComplainAboutMissingConnections);
/* 637:    */   }
/* 638:    */   
/* 639:    */   public static String flowToJSON(Flow flow)
/* 640:    */     throws WekaException
/* 641:    */   {
/* 642:836 */     JSONNode flowRoot = new JSONNode();
/* 643:837 */     flowRoot.addPrimitive("flow_name", flow.getFlowName());
/* 644:838 */     JSONNode flowArray = flowRoot.addArray("steps");
/* 645:839 */     Iterator<StepManagerImpl> iter = flow.iterator();
/* 646:840 */     if (iter.hasNext()) {
/* 647:841 */       while (iter.hasNext())
/* 648:    */       {
/* 649:842 */         StepManagerImpl next = (StepManagerImpl)iter.next();
/* 650:843 */         addStepJSONtoFlowArray(flowArray, next);
/* 651:    */       }
/* 652:    */     }
/* 653:847 */     StringBuffer b = new StringBuffer();
/* 654:848 */     flowRoot.toString(b);
/* 655:849 */     return b.toString();
/* 656:    */   }
/* 657:    */   
/* 658:    */   public static void main(String[] args)
/* 659:    */   {
/* 660:    */     try
/* 661:    */     {
/* 662:859 */       weka.knowledgeflow.steps.Loader step = new weka.knowledgeflow.steps.Loader();
/* 663:    */       
/* 664:861 */       ArffLoader arffL = new ArffLoader();
/* 665:    */       
/* 666:863 */       arffL.setFile(new File("${user.home}/datasets/UCI/iris.arff"));
/* 667:864 */       step.setLoader(arffL);
/* 668:    */       
/* 669:866 */       StepManagerImpl manager = new StepManagerImpl(step);
/* 670:    */       
/* 671:868 */       Flow flow = new Flow();
/* 672:869 */       flow.addStep(manager);
/* 673:    */       
/* 674:871 */       TrainingSetMaker step2 = new TrainingSetMaker();
/* 675:872 */       StepManagerImpl trainManager = new StepManagerImpl(step2);
/* 676:873 */       flow.addStep(trainManager);
/* 677:874 */       manager.addOutgoingConnection("dataSet", trainManager);
/* 678:    */       
/* 679:876 */       ClassAssigner step3 = new ClassAssigner();
/* 680:877 */       StepManagerImpl assignerManager = new StepManagerImpl(step3);
/* 681:878 */       flow.addStep(assignerManager);
/* 682:879 */       trainManager.addOutgoingConnection("trainingSet", assignerManager);
/* 683:    */       
/* 684:    */ 
/* 685:882 */       weka.knowledgeflow.steps.Saver step4 = new weka.knowledgeflow.steps.Saver();
/* 686:883 */       CSVSaver arffS = new CSVSaver();
/* 687:884 */       arffS.setDir(".");
/* 688:885 */       arffS.setFilePrefix("");
/* 689:    */       
/* 690:887 */       step4.setSaver(arffS);
/* 691:888 */       StepManagerImpl saverManager = new StepManagerImpl(step4);
/* 692:    */       
/* 693:890 */       flow.addStep(saverManager);
/* 694:891 */       assignerManager.addOutgoingConnection("trainingSet", saverManager);
/* 695:    */       
/* 696:    */ 
/* 697:    */ 
/* 698:    */ 
/* 699:    */ 
/* 700:    */ 
/* 701:    */ 
/* 702:    */ 
/* 703:    */ 
/* 704:    */ 
/* 705:    */ 
/* 706:    */ 
/* 707:    */ 
/* 708:    */ 
/* 709:    */ 
/* 710:    */ 
/* 711:908 */       FlowRunner fr = new FlowRunner(new Settings("weka", "knowledgeflow"));
/* 712:909 */       fr.setFlow(flow);
/* 713:910 */       fr.run();
/* 714:    */     }
/* 715:    */     catch (Exception ex)
/* 716:    */     {
/* 717:912 */       ex.printStackTrace();
/* 718:    */     }
/* 719:    */   }
/* 720:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.JSONFlowUtils
 * JD-Core Version:    0.7.0.1
 */