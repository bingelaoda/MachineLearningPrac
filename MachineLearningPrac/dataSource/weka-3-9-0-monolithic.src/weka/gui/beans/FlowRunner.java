/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.FileInputStream;
/*   5:    */ import java.io.InputStream;
/*   6:    */ import java.io.ObjectInputStream;
/*   7:    */ import java.io.PrintStream;
/*   8:    */ import java.text.SimpleDateFormat;
/*   9:    */ import java.util.Date;
/*  10:    */ import java.util.Set;
/*  11:    */ import java.util.TreeMap;
/*  12:    */ import java.util.Vector;
/*  13:    */ import weka.core.Environment;
/*  14:    */ import weka.core.EnvironmentHandler;
/*  15:    */ import weka.core.RevisionHandler;
/*  16:    */ import weka.core.logging.Logger.Level;
/*  17:    */ import weka.gui.beans.xml.XMLBeans;
/*  18:    */ 
/*  19:    */ public class FlowRunner
/*  20:    */   implements RevisionHandler
/*  21:    */ {
/*  22:    */   protected Vector<Object> m_beans;
/*  23: 52 */   protected int m_runningCount = 0;
/*  24: 54 */   protected transient weka.gui.Logger m_log = null;
/*  25: 57 */   protected boolean m_registerLog = true;
/*  26:    */   protected transient Environment m_env;
/*  27: 62 */   protected boolean m_startSequentially = false;
/*  28:    */   
/*  29:    */   public static class SimpleLogger
/*  30:    */     implements weka.gui.Logger
/*  31:    */   {
/*  32: 65 */     SimpleDateFormat m_DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
/*  33:    */     
/*  34:    */     public void logMessage(String lm)
/*  35:    */     {
/*  36: 69 */       System.out.println(this.m_DateFormat.format(new Date()) + ": " + lm);
/*  37:    */     }
/*  38:    */     
/*  39:    */     public void statusMessage(String lm)
/*  40:    */     {
/*  41: 74 */       System.out.println(this.m_DateFormat.format(new Date()) + ": " + lm);
/*  42:    */     }
/*  43:    */   }
/*  44:    */   
/*  45:    */   public FlowRunner()
/*  46:    */   {
/*  47: 82 */     this(true, true);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public FlowRunner(boolean loadProps, boolean registerLog)
/*  51:    */   {
/*  52: 86 */     if (loadProps) {
/*  53: 88 */       BeansProperties.loadProperties();
/*  54:    */     }
/*  55: 90 */     this.m_registerLog = registerLog;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public void setLog(weka.gui.Logger log)
/*  59:    */   {
/*  60: 94 */     this.m_log = log;
/*  61:    */   }
/*  62:    */   
/*  63:    */   protected void runSequentially(TreeMap<Integer, Startable> startables)
/*  64:    */   {
/*  65: 98 */     Set<Integer> s = startables.keySet();
/*  66: 99 */     for (Integer i : s) {
/*  67:    */       try
/*  68:    */       {
/*  69:101 */         Startable startPoint = (Startable)startables.get(i);
/*  70:102 */         startPoint.start();
/*  71:103 */         Thread.sleep(200L);
/*  72:104 */         waitUntilFinished();
/*  73:    */       }
/*  74:    */       catch (Exception ex)
/*  75:    */       {
/*  76:106 */         ex.printStackTrace();
/*  77:107 */         if (this.m_log != null)
/*  78:    */         {
/*  79:108 */           this.m_log.logMessage(ex.getMessage());
/*  80:109 */           this.m_log.logMessage("Aborting...");
/*  81:    */         }
/*  82:    */         else
/*  83:    */         {
/*  84:111 */           System.err.println(ex.getMessage());
/*  85:112 */           System.err.println("Aborting...");
/*  86:    */         }
/*  87:114 */         break;
/*  88:    */       }
/*  89:    */     }
/*  90:    */   }
/*  91:    */   
/*  92:    */   protected synchronized void launchThread(final Startable s, int flowNum)
/*  93:    */   {
/*  94:120 */     Thread t = new Thread()
/*  95:    */     {
/*  96:    */       public void run()
/*  97:    */       {
/*  98:    */         try
/*  99:    */         {
/* 100:125 */           s.start();
/* 101:    */         }
/* 102:    */         catch (Exception ex)
/* 103:    */         {
/* 104:127 */           ex.printStackTrace();
/* 105:128 */           if (FlowRunner.this.m_log != null) {
/* 106:129 */             FlowRunner.this.m_log.logMessage(ex.getMessage());
/* 107:    */           } else {
/* 108:131 */             System.err.println(ex.getMessage());
/* 109:    */           }
/* 110:    */         }
/* 111:    */         finally
/* 112:    */         {
/* 113:139 */           FlowRunner.this.decreaseCount();
/* 114:    */         }
/* 115:    */       }
/* 116:142 */     };
/* 117:143 */     this.m_runningCount += 1;
/* 118:144 */     t.setPriority(1);
/* 119:145 */     t.start();
/* 120:    */   }
/* 121:    */   
/* 122:    */   protected synchronized void decreaseCount()
/* 123:    */   {
/* 124:149 */     this.m_runningCount -= 1;
/* 125:    */   }
/* 126:    */   
/* 127:    */   public synchronized void stopAllFlows()
/* 128:    */   {
/* 129:153 */     for (int i = 0; i < this.m_beans.size(); i++)
/* 130:    */     {
/* 131:154 */       BeanInstance temp = (BeanInstance)this.m_beans.elementAt(i);
/* 132:155 */       if ((temp.getBean() instanceof BeanCommon)) {
/* 133:157 */         ((BeanCommon)temp.getBean()).stop();
/* 134:    */       }
/* 135:    */     }
/* 136:    */   }
/* 137:    */   
/* 138:    */   public void waitUntilFinished()
/* 139:    */   {
/* 140:    */     try
/* 141:    */     {
/* 142:168 */       while (this.m_runningCount > 0) {
/* 143:169 */         Thread.sleep(200L);
/* 144:    */       }
/* 145:    */       for (;;)
/* 146:    */       {
/* 147:175 */         boolean busy = false;
/* 148:176 */         for (int i = 0; i < this.m_beans.size(); i++)
/* 149:    */         {
/* 150:177 */           BeanInstance temp = (BeanInstance)this.m_beans.elementAt(i);
/* 151:178 */           if (((temp.getBean() instanceof BeanCommon)) && 
/* 152:179 */             (((BeanCommon)temp.getBean()).isBusy()))
/* 153:    */           {
/* 154:180 */             busy = true;
/* 155:181 */             break;
/* 156:    */           }
/* 157:    */         }
/* 158:185 */         if (!busy) {
/* 159:    */           break;
/* 160:    */         }
/* 161:186 */         Thread.sleep(3000L);
/* 162:    */       }
/* 163:    */     }
/* 164:    */     catch (Exception ex)
/* 165:    */     {
/* 166:192 */       if (this.m_log != null) {
/* 167:193 */         this.m_log.logMessage("[FlowRunner] Attempting to stop all flows...");
/* 168:    */       } else {
/* 169:195 */         System.err.println("[FlowRunner] Attempting to stop all flows...");
/* 170:    */       }
/* 171:197 */       stopAllFlows();
/* 172:    */     }
/* 173:    */   }
/* 174:    */   
/* 175:    */   public void load(String fileName)
/* 176:    */     throws Exception
/* 177:    */   {
/* 178:209 */     if ((!fileName.endsWith(".kf")) && (!fileName.endsWith(".kfml"))) {
/* 179:210 */       throw new Exception("Can only load and run binary or xml serialized KnowledgeFlows (*.kf | *.kfml)");
/* 180:    */     }
/* 181:215 */     if (fileName.endsWith(".kf")) {
/* 182:216 */       loadBinary(fileName);
/* 183:217 */     } else if (fileName.endsWith(".kfml")) {
/* 184:218 */       loadXML(fileName);
/* 185:    */     }
/* 186:    */   }
/* 187:    */   
/* 188:    */   public void loadBinary(String fileName)
/* 189:    */     throws Exception
/* 190:    */   {
/* 191:230 */     if (!fileName.endsWith(".kf")) {
/* 192:231 */       throw new Exception("File must be a binary flow (*.kf)");
/* 193:    */     }
/* 194:234 */     InputStream is = new FileInputStream(fileName);
/* 195:235 */     ObjectInputStream ois = new ObjectInputStream(is);
/* 196:236 */     this.m_beans = ((Vector)ois.readObject());
/* 197:    */     
/* 198:    */ 
/* 199:239 */     ois.close();
/* 200:241 */     if (this.m_env != null)
/* 201:    */     {
/* 202:242 */       String parentDir = new File(fileName).getParent();
/* 203:243 */       if (parentDir == null) {
/* 204:244 */         parentDir = "./";
/* 205:    */       }
/* 206:246 */       this.m_env.addVariable("Internal.knowledgeflow.directory", parentDir);
/* 207:    */     }
/* 208:    */   }
/* 209:    */   
/* 210:    */   public void loadXML(String fileName)
/* 211:    */     throws Exception
/* 212:    */   {
/* 213:258 */     if (!fileName.endsWith(".kfml")) {
/* 214:259 */       throw new Exception("File must be an XML flow (*.kfml)");
/* 215:    */     }
/* 216:261 */     BeanConnection.init();
/* 217:262 */     BeanInstance.init();
/* 218:263 */     XMLBeans xml = new XMLBeans(null, null, 0);
/* 219:264 */     Vector<?> v = (Vector)xml.read(new File(fileName));
/* 220:265 */     this.m_beans = ((Vector)v.get(0));
/* 221:267 */     if (this.m_env != null)
/* 222:    */     {
/* 223:268 */       String parentDir = new File(fileName).getParent();
/* 224:269 */       if (parentDir == null) {
/* 225:270 */         parentDir = "./";
/* 226:    */       }
/* 227:272 */       this.m_env.addVariable("Internal.knowledgeflow.directory", parentDir);
/* 228:    */     }
/* 229:    */     else
/* 230:    */     {
/* 231:274 */       System.err.println("++++++++++++ Environment variables null!!...");
/* 232:    */     }
/* 233:    */   }
/* 234:    */   
/* 235:    */   public Vector<Object> getFlows()
/* 236:    */   {
/* 237:284 */     return this.m_beans;
/* 238:    */   }
/* 239:    */   
/* 240:    */   public void setFlows(Vector<Object> beans)
/* 241:    */   {
/* 242:293 */     this.m_beans = beans;
/* 243:    */   }
/* 244:    */   
/* 245:    */   public void setEnvironment(Environment env)
/* 246:    */   {
/* 247:305 */     this.m_env = env;
/* 248:    */   }
/* 249:    */   
/* 250:    */   public Environment getEnvironment()
/* 251:    */   {
/* 252:314 */     return this.m_env;
/* 253:    */   }
/* 254:    */   
/* 255:    */   public void setStartSequentially(boolean s)
/* 256:    */   {
/* 257:324 */     this.m_startSequentially = s;
/* 258:    */   }
/* 259:    */   
/* 260:    */   public boolean getStartSequentially()
/* 261:    */   {
/* 262:334 */     return this.m_startSequentially;
/* 263:    */   }
/* 264:    */   
/* 265:    */   public void run()
/* 266:    */     throws Exception
/* 267:    */   {
/* 268:343 */     if (this.m_beans == null) {
/* 269:344 */       throw new Exception("Don't seem to have any beans I can execute.");
/* 270:    */     }
/* 271:348 */     for (int i = 0; i < this.m_beans.size(); i++)
/* 272:    */     {
/* 273:349 */       BeanInstance tempB = (BeanInstance)this.m_beans.elementAt(i);
/* 274:350 */       if ((this.m_log != null) && (this.m_registerLog) && 
/* 275:351 */         ((tempB.getBean() instanceof BeanCommon))) {
/* 276:352 */         ((BeanCommon)tempB.getBean()).setLog(this.m_log);
/* 277:    */       }
/* 278:356 */       if ((tempB.getBean() instanceof EnvironmentHandler)) {
/* 279:357 */         ((EnvironmentHandler)tempB.getBean()).setEnvironment(this.m_env);
/* 280:    */       }
/* 281:    */     }
/* 282:361 */     int numFlows = 1;
/* 283:363 */     if (this.m_log != null) {
/* 284:364 */       if (this.m_startSequentially) {
/* 285:365 */         this.m_log.logMessage("[FlowRunner] launching flow start points sequentially...");
/* 286:    */       } else {
/* 287:368 */         this.m_log.logMessage("[FlowRunner] launching flow start points in parallel...");
/* 288:    */       }
/* 289:    */     }
/* 290:372 */     TreeMap<Integer, Startable> startables = new TreeMap();
/* 291:374 */     for (int i = 0; i < this.m_beans.size(); i++)
/* 292:    */     {
/* 293:375 */       BeanInstance tempB = (BeanInstance)this.m_beans.elementAt(i);
/* 294:376 */       boolean launch = true;
/* 295:378 */       if ((tempB.getBean() instanceof Startable))
/* 296:    */       {
/* 297:380 */         Startable s = (Startable)tempB.getBean();
/* 298:381 */         String beanName = s.getClass().getName();
/* 299:382 */         String customName = beanName;
/* 300:383 */         if ((s instanceof BeanCommon))
/* 301:    */         {
/* 302:384 */           customName = ((BeanCommon)s).getCustomName();
/* 303:385 */           beanName = customName;
/* 304:386 */           if ((customName.indexOf(':') > 0) && 
/* 305:387 */             (customName.substring(0, customName.indexOf(':')).startsWith("!"))) {
/* 306:389 */             launch = false;
/* 307:    */           }
/* 308:    */         }
/* 309:395 */         if (!this.m_startSequentially)
/* 310:    */         {
/* 311:396 */           if (s.getStartMessage().charAt(0) != '$')
/* 312:    */           {
/* 313:397 */             if (launch)
/* 314:    */             {
/* 315:398 */               if (this.m_log != null) {
/* 316:399 */                 this.m_log.logMessage("[FlowRunner] Launching flow " + numFlows + "...");
/* 317:    */               } else {
/* 318:402 */                 System.out.println("[FlowRunner] Launching flow " + numFlows + "...");
/* 319:    */               }
/* 320:405 */               launchThread(s, numFlows);
/* 321:406 */               numFlows++;
/* 322:    */             }
/* 323:    */           }
/* 324:409 */           else if (this.m_log != null) {
/* 325:410 */             this.m_log.logMessage("[FlowRunner] WARNING: Can't start " + beanName + " at this time.");
/* 326:    */           } else {
/* 327:413 */             System.out.println("[FlowRunner] WARNING: Can't start " + beanName + " at this time.");
/* 328:    */           }
/* 329:    */         }
/* 330:    */         else
/* 331:    */         {
/* 332:418 */           boolean ok = false;
/* 333:419 */           Integer position = null;
/* 334:421 */           if ((s instanceof BeanCommon)) {
/* 335:425 */             if (customName.indexOf(':') > 0) {
/* 336:426 */               if (customName.substring(0, customName.indexOf(':')).startsWith("!"))
/* 337:    */               {
/* 338:428 */                 launch = false;
/* 339:    */               }
/* 340:    */               else
/* 341:    */               {
/* 342:430 */                 String startPos = customName.substring(0, customName.indexOf(':'));
/* 343:    */                 try
/* 344:    */                 {
/* 345:434 */                   position = new Integer(startPos);
/* 346:435 */                   ok = true;
/* 347:    */                 }
/* 348:    */                 catch (NumberFormatException n) {}
/* 349:    */               }
/* 350:    */             }
/* 351:    */           }
/* 352:442 */           if ((!ok) && (launch)) {
/* 353:443 */             if (startables.size() == 0)
/* 354:    */             {
/* 355:444 */               position = new Integer(0);
/* 356:    */             }
/* 357:    */             else
/* 358:    */             {
/* 359:446 */               int newPos = ((Integer)startables.lastKey()).intValue();
/* 360:447 */               newPos++;
/* 361:448 */               position = new Integer(newPos);
/* 362:    */             }
/* 363:    */           }
/* 364:452 */           if (s.getStartMessage().charAt(0) != '$')
/* 365:    */           {
/* 366:453 */             if (launch)
/* 367:    */             {
/* 368:454 */               if (this.m_log != null) {
/* 369:455 */                 this.m_log.logMessage("[FlowRunner] adding start point " + beanName + " to the execution list (position " + position + ")");
/* 370:    */               } else {
/* 371:458 */                 System.out.println("[FlowRunner] adding start point " + beanName + " to the execution list (position " + position + ")");
/* 372:    */               }
/* 373:462 */               startables.put(position, s);
/* 374:    */             }
/* 375:    */           }
/* 376:465 */           else if (this.m_log != null) {
/* 377:466 */             this.m_log.logMessage("[FlowRunner] WARNING: Can't start " + beanName + " at this time.");
/* 378:    */           } else {
/* 379:469 */             System.out.println("[FlowRunner] WARNING: Can't start " + beanName + " at this time.");
/* 380:    */           }
/* 381:    */         }
/* 382:475 */         if (!launch) {
/* 383:476 */           if (this.m_log != null) {
/* 384:477 */             this.m_log.logMessage("[FlowRunner] start point " + beanName + " will not be launched.");
/* 385:    */           } else {
/* 386:480 */             System.out.println("[FlowRunner] start point " + beanName + " will not be launched.");
/* 387:    */           }
/* 388:    */         }
/* 389:    */       }
/* 390:    */     }
/* 391:487 */     if (this.m_startSequentially) {
/* 392:488 */       runSequentially(startables);
/* 393:    */     }
/* 394:    */   }
/* 395:    */   
/* 396:    */   public static void main(String[] args)
/* 397:    */   {
/* 398:502 */     System.setProperty("apple.awt.UIElement", "true");
/* 399:503 */     weka.core.logging.Logger.log(Logger.Level.INFO, "Logging started");
/* 400:505 */     if (args.length < 1) {
/* 401:506 */       System.err.println("Usage:\n\nFlowRunner <serialized kf file> [-s]\n\n\tUse -s to launch start points sequentially (default launches in parallel).");
/* 402:    */     } else {
/* 403:    */       try
/* 404:    */       {
/* 405:511 */         FlowRunner fr = new FlowRunner();
/* 406:512 */         SimpleLogger sl = new SimpleLogger();
/* 407:513 */         String fileName = args[0];
/* 408:515 */         if ((args.length == 2) && (args[1].equals("-s"))) {
/* 409:516 */           fr.setStartSequentially(true);
/* 410:    */         }
/* 411:520 */         Environment env = Environment.getSystemWide();
/* 412:    */         
/* 413:522 */         fr.setLog(sl);
/* 414:523 */         fr.setEnvironment(env);
/* 415:    */         
/* 416:525 */         fr.load(fileName);
/* 417:526 */         fr.run();
/* 418:527 */         fr.waitUntilFinished();
/* 419:528 */         System.out.println("Finished all flows.");
/* 420:529 */         System.exit(1);
/* 421:    */       }
/* 422:    */       catch (Exception ex)
/* 423:    */       {
/* 424:531 */         ex.printStackTrace();
/* 425:532 */         System.err.println(ex.getMessage());
/* 426:    */       }
/* 427:    */     }
/* 428:    */   }
/* 429:    */   
/* 430:    */   public String getRevision()
/* 431:    */   {
/* 432:539 */     return "$Revision: 10328 $";
/* 433:    */   }
/* 434:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.FlowRunner
 * JD-Core Version:    0.7.0.1
 */