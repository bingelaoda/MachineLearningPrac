/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.FileInputStream;
/*   5:    */ import java.io.FileOutputStream;
/*   6:    */ import java.io.PrintStream;
/*   7:    */ import java.lang.annotation.Annotation;
/*   8:    */ import java.util.Collections;
/*   9:    */ import java.util.Enumeration;
/*  10:    */ import java.util.HashSet;
/*  11:    */ import java.util.Hashtable;
/*  12:    */ import java.util.Properties;
/*  13:    */ import java.util.StringTokenizer;
/*  14:    */ import java.util.Vector;
/*  15:    */ import weka.core.ClassDiscovery;
/*  16:    */ import weka.core.ClassDiscovery.StringCompare;
/*  17:    */ import weka.core.Utils;
/*  18:    */ import weka.core.WekaPackageManager;
/*  19:    */ 
/*  20:    */ public class GenericPropertiesCreator
/*  21:    */ {
/*  22:    */   public static final boolean VERBOSE = false;
/*  23:    */   public static final String USE_DYNAMIC = "UseDynamic";
/*  24:102 */   protected static String CREATOR_FILE = "weka/gui/GenericPropertiesCreator.props";
/*  25:109 */   protected static String EXCLUDE_FILE = "weka/gui/GenericPropertiesCreator.excludes";
/*  26:113 */   protected static String EXCLUDE_INTERFACE = "I";
/*  27:116 */   protected static String EXCLUDE_CLASS = "C";
/*  28:119 */   protected static String EXCLUDE_SUPERCLASS = "S";
/*  29:128 */   protected static String PROPERTY_FILE = "weka/gui/GenericObjectEditor.props";
/*  30:    */   protected String m_InputFilename;
/*  31:    */   protected String m_OutputFilename;
/*  32:    */   protected Properties m_InputProperties;
/*  33:    */   protected Properties m_OutputProperties;
/*  34:    */   protected static GenericPropertiesCreator GLOBAL_CREATOR;
/*  35:    */   protected static Properties GLOBAL_INPUT_PROPERTIES;
/*  36:    */   protected static Properties GLOBAL_OUTPUT_PROPERTIES;
/*  37:    */   protected boolean m_ExplicitPropsFile;
/*  38:    */   protected Hashtable<String, Hashtable<String, Vector<String>>> m_Excludes;
/*  39:    */   
/*  40:    */   static
/*  41:    */   {
/*  42:    */     try
/*  43:    */     {
/*  44:161 */       GenericPropertiesCreator creator = new GenericPropertiesCreator();
/*  45:162 */       GLOBAL_CREATOR = creator;
/*  46:163 */       if ((creator.useDynamic()) && (!WekaPackageManager.m_initialPackageLoadingInProcess))
/*  47:    */       {
/*  48:165 */         creator.execute(false, true);
/*  49:166 */         GLOBAL_INPUT_PROPERTIES = creator.getInputProperties();
/*  50:167 */         GLOBAL_OUTPUT_PROPERTIES = creator.getOutputProperties();
/*  51:    */       }
/*  52:    */       else
/*  53:    */       {
/*  54:170 */         GLOBAL_OUTPUT_PROPERTIES = Utils.readProperties("weka/gui/GenericObjectEditor.props");
/*  55:    */       }
/*  56:    */     }
/*  57:    */     catch (Exception e)
/*  58:    */     {
/*  59:175 */       e.printStackTrace();
/*  60:    */     }
/*  61:    */   }
/*  62:    */   
/*  63:    */   public static Properties getGlobalOutputProperties()
/*  64:    */   {
/*  65:185 */     return GLOBAL_OUTPUT_PROPERTIES;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public static Properties getGlobalInputProperties()
/*  69:    */   {
/*  70:194 */     return GLOBAL_INPUT_PROPERTIES;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public static void regenerateGlobalOutputProperties()
/*  74:    */   {
/*  75:202 */     if (GLOBAL_CREATOR != null) {
/*  76:    */       try
/*  77:    */       {
/*  78:204 */         GLOBAL_CREATOR.execute(false, false);
/*  79:205 */         GLOBAL_INPUT_PROPERTIES = GLOBAL_CREATOR.getInputProperties();
/*  80:206 */         GLOBAL_OUTPUT_PROPERTIES = GLOBAL_CREATOR.getOutputProperties();
/*  81:    */       }
/*  82:    */       catch (Exception e)
/*  83:    */       {
/*  84:209 */         e.printStackTrace();
/*  85:    */       }
/*  86:    */     }
/*  87:    */   }
/*  88:    */   
/*  89:    */   public GenericPropertiesCreator()
/*  90:    */     throws Exception
/*  91:    */   {
/*  92:223 */     this(CREATOR_FILE);
/*  93:224 */     this.m_ExplicitPropsFile = false;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public GenericPropertiesCreator(String filename)
/*  97:    */     throws Exception
/*  98:    */   {
/*  99:240 */     this.m_InputFilename = filename;
/* 100:241 */     this.m_OutputFilename = PROPERTY_FILE;
/* 101:242 */     this.m_InputProperties = null;
/* 102:243 */     this.m_OutputProperties = null;
/* 103:244 */     this.m_ExplicitPropsFile = true;
/* 104:245 */     this.m_Excludes = new Hashtable();
/* 105:    */   }
/* 106:    */   
/* 107:    */   public void setExplicitPropsFile(boolean value)
/* 108:    */   {
/* 109:258 */     this.m_ExplicitPropsFile = value;
/* 110:    */   }
/* 111:    */   
/* 112:    */   public boolean getExplicitPropsFile()
/* 113:    */   {
/* 114:271 */     return this.m_ExplicitPropsFile;
/* 115:    */   }
/* 116:    */   
/* 117:    */   public String getOutputFilename()
/* 118:    */   {
/* 119:280 */     return this.m_OutputFilename;
/* 120:    */   }
/* 121:    */   
/* 122:    */   public void setOutputFilename(String filename)
/* 123:    */   {
/* 124:289 */     this.m_OutputFilename = filename;
/* 125:    */   }
/* 126:    */   
/* 127:    */   public String getInputFilename()
/* 128:    */   {
/* 129:298 */     return this.m_InputFilename;
/* 130:    */   }
/* 131:    */   
/* 132:    */   public void setInputFilename(String filename)
/* 133:    */   {
/* 134:309 */     this.m_InputFilename = filename;
/* 135:310 */     setExplicitPropsFile(true);
/* 136:    */   }
/* 137:    */   
/* 138:    */   public Properties getInputProperties()
/* 139:    */   {
/* 140:319 */     return this.m_InputProperties;
/* 141:    */   }
/* 142:    */   
/* 143:    */   public Properties getOutputProperties()
/* 144:    */   {
/* 145:329 */     return this.m_OutputProperties;
/* 146:    */   }
/* 147:    */   
/* 148:    */   protected void loadInputProperties()
/* 149:    */   {
/* 150:343 */     this.m_InputProperties = new Properties();
/* 151:    */     try
/* 152:    */     {
/* 153:345 */       File f = new File(getInputFilename());
/* 154:346 */       if ((getExplicitPropsFile()) && (f.exists())) {
/* 155:347 */         this.m_InputProperties.load(new FileInputStream(getInputFilename()));
/* 156:    */       } else {
/* 157:349 */         this.m_InputProperties = Utils.readProperties(getInputFilename());
/* 158:    */       }
/* 159:353 */       this.m_Excludes.clear();
/* 160:354 */       Properties p = Utils.readProperties(EXCLUDE_FILE);
/* 161:355 */       Enumeration<?> enm = p.propertyNames();
/* 162:356 */       while (enm.hasMoreElements())
/* 163:    */       {
/* 164:357 */         String name = enm.nextElement().toString();
/* 165:    */         
/* 166:359 */         Hashtable<String, Vector<String>> t = new Hashtable();
/* 167:    */         
/* 168:361 */         this.m_Excludes.put(name, t);
/* 169:362 */         t.put(EXCLUDE_INTERFACE, new Vector());
/* 170:363 */         t.put(EXCLUDE_CLASS, new Vector());
/* 171:364 */         t.put(EXCLUDE_SUPERCLASS, new Vector());
/* 172:    */         
/* 173:    */ 
/* 174:367 */         StringTokenizer tok = new StringTokenizer(p.getProperty(name), ",");
/* 175:368 */         while (tok.hasMoreTokens())
/* 176:    */         {
/* 177:369 */           String item = tok.nextToken();
/* 178:    */           
/* 179:371 */           Vector<String> list = new Vector();
/* 180:372 */           if (item.startsWith(EXCLUDE_INTERFACE + ":")) {
/* 181:373 */             list = (Vector)t.get(EXCLUDE_INTERFACE);
/* 182:374 */           } else if (item.startsWith(EXCLUDE_CLASS + ":")) {
/* 183:375 */             list = (Vector)t.get(EXCLUDE_CLASS);
/* 184:376 */           } else if (item.startsWith(EXCLUDE_SUPERCLASS)) {
/* 185:377 */             list = (Vector)t.get(EXCLUDE_SUPERCLASS);
/* 186:    */           }
/* 187:380 */           list.add(item.substring(item.indexOf(":") + 1));
/* 188:    */         }
/* 189:    */       }
/* 190:    */     }
/* 191:    */     catch (Exception e)
/* 192:    */     {
/* 193:384 */       e.printStackTrace();
/* 194:    */     }
/* 195:    */   }
/* 196:    */   
/* 197:    */   public boolean useDynamic()
/* 198:    */   {
/* 199:394 */     if (getInputProperties() == null) {
/* 200:395 */       loadInputProperties();
/* 201:    */     }
/* 202:412 */     return Boolean.parseBoolean(getInputProperties().getProperty("UseDynamic", "true"));
/* 203:    */   }
/* 204:    */   
/* 205:    */   protected boolean isValidClassname(String classname)
/* 206:    */   {
/* 207:423 */     return classname.indexOf("$") == -1;
/* 208:    */   }
/* 209:    */   
/* 210:    */   protected boolean isValidClassname(String key, String classname)
/* 211:    */   {
/* 212:442 */     boolean result = true;
/* 213:    */     Class<?> clsCurrent;
/* 214:    */     try
/* 215:    */     {
/* 216:444 */       clsCurrent = Class.forName(classname);
/* 217:446 */       for (Annotation a : clsCurrent.getAnnotations()) {
/* 218:447 */         if ((a instanceof GPCIgnore)) {
/* 219:448 */           return false;
/* 220:    */         }
/* 221:    */       }
/* 222:    */     }
/* 223:    */     catch (Exception ex)
/* 224:    */     {
/* 225:452 */       clsCurrent = null;
/* 226:    */     }
/* 227:456 */     if (this.m_Excludes.containsKey(key))
/* 228:    */     {
/* 229:    */       Class<?> cls;
/* 230:458 */       if ((clsCurrent != null) && (result))
/* 231:    */       {
/* 232:459 */         Vector<String> list = (Vector)((Hashtable)this.m_Excludes.get(key)).get(EXCLUDE_INTERFACE);
/* 233:460 */         for (int i = 0; i < list.size(); i++) {
/* 234:    */           try
/* 235:    */           {
/* 236:462 */             cls = Class.forName(((String)list.get(i)).toString());
/* 237:463 */             if (ClassDiscovery.hasInterface(cls, clsCurrent))
/* 238:    */             {
/* 239:464 */               result = false;
/* 240:465 */               break;
/* 241:    */             }
/* 242:    */           }
/* 243:    */           catch (Exception e) {}
/* 244:    */         }
/* 245:    */       }
/* 246:474 */       if ((clsCurrent != null) && (result))
/* 247:    */       {
/* 248:475 */         Vector<String> list = (Vector)((Hashtable)this.m_Excludes.get(key)).get(EXCLUDE_SUPERCLASS);
/* 249:476 */         for (int i = 0; i < list.size(); i++) {
/* 250:    */           try
/* 251:    */           {
/* 252:478 */             cls = Class.forName(((String)list.get(i)).toString());
/* 253:479 */             if (ClassDiscovery.isSubclass(cls, clsCurrent))
/* 254:    */             {
/* 255:480 */               result = false;
/* 256:481 */               break;
/* 257:    */             }
/* 258:    */           }
/* 259:    */           catch (Exception e) {}
/* 260:    */         }
/* 261:    */       }
/* 262:490 */       if ((clsCurrent != null) && (result))
/* 263:    */       {
/* 264:491 */         Vector<String> list = (Vector)((Hashtable)this.m_Excludes.get(key)).get(EXCLUDE_CLASS);
/* 265:492 */         for (int i = 0; i < list.size(); i++) {
/* 266:    */           try
/* 267:    */           {
/* 268:494 */             cls = Class.forName(((String)list.get(i)).toString());
/* 269:495 */             if (cls.getName().equals(clsCurrent.getName())) {
/* 270:496 */               result = false;
/* 271:    */             }
/* 272:    */           }
/* 273:    */           catch (Exception e) {}
/* 274:    */         }
/* 275:    */       }
/* 276:    */     }
/* 277:505 */     return result;
/* 278:    */   }
/* 279:    */   
/* 280:    */   protected void generateOutputProperties()
/* 281:    */     throws Exception
/* 282:    */   {
/* 283:525 */     this.m_OutputProperties = new Properties();
/* 284:526 */     Enumeration<?> keys = this.m_InputProperties.propertyNames();
/* 285:527 */     while (keys.hasMoreElements())
/* 286:    */     {
/* 287:528 */       String key = keys.nextElement().toString();
/* 288:529 */       if (!key.equals("UseDynamic"))
/* 289:    */       {
/* 290:532 */         StringTokenizer tok = new StringTokenizer(this.m_InputProperties.getProperty(key), ",");
/* 291:533 */         HashSet<String> names = new HashSet();
/* 292:536 */         while (tok.hasMoreTokens())
/* 293:    */         {
/* 294:537 */           String pkg = tok.nextToken().trim();
/* 295:    */           Vector<String> classes;
/* 296:    */           try
/* 297:    */           {
/* 298:540 */             classes = ClassDiscovery.find(Class.forName(key), pkg);
/* 299:    */           }
/* 300:    */           catch (Exception e)
/* 301:    */           {
/* 302:542 */             System.out.println("Problem with '" + key + "': " + e);
/* 303:543 */             classes = new Vector();
/* 304:    */           }
/* 305:546 */           for (int i = 0; i < classes.size(); i++) {
/* 306:548 */             if (isValidClassname(((String)classes.get(i)).toString())) {
/* 307:552 */               if (isValidClassname(key, ((String)classes.get(i)).toString())) {
/* 308:555 */                 names.add(classes.get(i));
/* 309:    */               }
/* 310:    */             }
/* 311:    */           }
/* 312:    */         }
/* 313:560 */         String value = "";
/* 314:561 */         Vector<String> classes = new Vector();
/* 315:562 */         classes.addAll(names);
/* 316:563 */         Collections.sort(classes, new ClassDiscovery.StringCompare());
/* 317:564 */         for (int i = 0; i < classes.size(); i++)
/* 318:    */         {
/* 319:565 */           if (!value.equals("")) {
/* 320:566 */             value = value + ",";
/* 321:    */           }
/* 322:568 */           value = value + ((String)classes.get(i)).toString();
/* 323:    */         }
/* 324:575 */         this.m_OutputProperties.setProperty(key, value);
/* 325:    */       }
/* 326:    */     }
/* 327:    */   }
/* 328:    */   
/* 329:    */   protected void storeOutputProperties()
/* 330:    */     throws Exception
/* 331:    */   {
/* 332:590 */     this.m_OutputProperties.store(new FileOutputStream(getOutputFilename()), " Customises the list of options given by the GenericObjectEditor\n# for various superclasses.");
/* 333:    */   }
/* 334:    */   
/* 335:    */   public void execute()
/* 336:    */     throws Exception
/* 337:    */   {
/* 338:603 */     execute(true, true);
/* 339:    */   }
/* 340:    */   
/* 341:    */   public void execute(boolean store)
/* 342:    */     throws Exception
/* 343:    */   {
/* 344:613 */     execute(store, true);
/* 345:    */   }
/* 346:    */   
/* 347:    */   public void execute(boolean store, boolean loadInputProps)
/* 348:    */     throws Exception
/* 349:    */   {
/* 350:632 */     if (loadInputProps) {
/* 351:633 */       loadInputProperties();
/* 352:    */     }
/* 353:637 */     generateOutputProperties();
/* 354:640 */     if (store) {
/* 355:641 */       storeOutputProperties();
/* 356:    */     }
/* 357:    */   }
/* 358:    */   
/* 359:    */   public static void main(String[] args)
/* 360:    */     throws Exception
/* 361:    */   {
/* 362:665 */     GenericPropertiesCreator c = null;
/* 363:667 */     if (args.length == 0)
/* 364:    */     {
/* 365:668 */       c = new GenericPropertiesCreator();
/* 366:    */     }
/* 367:669 */     else if (args.length == 1)
/* 368:    */     {
/* 369:670 */       c = new GenericPropertiesCreator();
/* 370:671 */       c.setOutputFilename(args[0]);
/* 371:    */     }
/* 372:672 */     else if (args.length == 2)
/* 373:    */     {
/* 374:673 */       c = new GenericPropertiesCreator(args[0]);
/* 375:674 */       c.setOutputFilename(args[1]);
/* 376:    */     }
/* 377:    */     else
/* 378:    */     {
/* 379:676 */       System.out.println("usage: " + GenericPropertiesCreator.class.getName() + " [<input.props>] [<output.props>]");
/* 380:    */       
/* 381:678 */       System.exit(1);
/* 382:    */     }
/* 383:681 */     c.execute(true);
/* 384:    */   }
/* 385:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.GenericPropertiesCreator
 * JD-Core Version:    0.7.0.1
 */