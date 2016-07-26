/*   1:    */ package weka.gui.scripting;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.io.Serializable;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.HashSet;
/*   8:    */ import java.util.Iterator;
/*   9:    */ import java.util.Vector;
/*  10:    */ import javax.swing.event.DocumentEvent;
/*  11:    */ import javax.swing.event.DocumentListener;
/*  12:    */ import javax.swing.text.Document;
/*  13:    */ import weka.core.Option;
/*  14:    */ import weka.core.OptionHandler;
/*  15:    */ import weka.core.SerializedObject;
/*  16:    */ import weka.core.Utils;
/*  17:    */ import weka.core.WekaException;
/*  18:    */ import weka.gui.ExtensionFileFilter;
/*  19:    */ import weka.gui.scripting.event.ScriptExecutionEvent;
/*  20:    */ import weka.gui.scripting.event.ScriptExecutionEvent.Type;
/*  21:    */ import weka.gui.scripting.event.ScriptExecutionListener;
/*  22:    */ 
/*  23:    */ public abstract class Script
/*  24:    */   implements OptionHandler, Serializable
/*  25:    */ {
/*  26:    */   private static final long serialVersionUID = 5053328052680586401L;
/*  27:    */   public static final String BACKUP_EXTENSION = ".bak";
/*  28:    */   protected Document m_Document;
/*  29:    */   protected File m_Filename;
/*  30:    */   protected String m_NewLine;
/*  31:    */   protected boolean m_Modified;
/*  32:    */   protected transient ScriptThread m_ScriptThread;
/*  33:    */   protected HashSet<ScriptExecutionListener> m_FinishedListeners;
/*  34:    */   
/*  35:    */   public static abstract class ScriptThread
/*  36:    */     extends Thread
/*  37:    */   {
/*  38:    */     protected Script m_Owner;
/*  39:    */     protected String[] m_Args;
/*  40:    */     protected boolean m_Stopped;
/*  41:    */     
/*  42:    */     public ScriptThread(Script owner, String[] args)
/*  43:    */     {
/*  44: 81 */       this.m_Owner = owner;
/*  45: 82 */       this.m_Args = ((String[])args.clone());
/*  46:    */     }
/*  47:    */     
/*  48:    */     public Script getOwner()
/*  49:    */     {
/*  50: 91 */       return this.m_Owner;
/*  51:    */     }
/*  52:    */     
/*  53:    */     public String[] getArgs()
/*  54:    */     {
/*  55:100 */       return this.m_Args;
/*  56:    */     }
/*  57:    */     
/*  58:    */     protected abstract void doRun();
/*  59:    */     
/*  60:    */     public void run()
/*  61:    */     {
/*  62:113 */       this.m_Stopped = false;
/*  63:    */       
/*  64:115 */       getOwner().notifyScriptFinishedListeners(new ScriptExecutionEvent(this.m_Owner, ScriptExecutionEvent.Type.STARTED));
/*  65:    */       try
/*  66:    */       {
/*  67:118 */         doRun();
/*  68:119 */         if (!this.m_Stopped) {
/*  69:120 */           getOwner().notifyScriptFinishedListeners(new ScriptExecutionEvent(this.m_Owner, ScriptExecutionEvent.Type.FINISHED));
/*  70:    */         }
/*  71:    */       }
/*  72:    */       catch (Exception e)
/*  73:    */       {
/*  74:124 */         e.printStackTrace();
/*  75:125 */         getOwner().notifyScriptFinishedListeners(new ScriptExecutionEvent(this.m_Owner, ScriptExecutionEvent.Type.ERROR, e));
/*  76:    */       }
/*  77:128 */       getOwner().m_ScriptThread = null;
/*  78:    */     }
/*  79:    */     
/*  80:    */     public void stopScript()
/*  81:    */     {
/*  82:136 */       if (isAlive())
/*  83:    */       {
/*  84:137 */         this.m_Stopped = true;
/*  85:    */         try
/*  86:    */         {
/*  87:139 */           stop();
/*  88:    */         }
/*  89:    */         catch (Exception e) {}
/*  90:    */       }
/*  91:    */     }
/*  92:    */   }
/*  93:    */   
/*  94:    */   public Script()
/*  95:    */   {
/*  96:172 */     this(null);
/*  97:    */   }
/*  98:    */   
/*  99:    */   public Script(Document doc)
/* 100:    */   {
/* 101:181 */     this(doc, null);
/* 102:    */   }
/* 103:    */   
/* 104:    */   public Script(Document doc, File file)
/* 105:    */   {
/* 106:192 */     initialize();
/* 107:    */     
/* 108:194 */     this.m_Document = doc;
/* 109:196 */     if (this.m_Document != null) {
/* 110:197 */       this.m_Document.addDocumentListener(new DocumentListener()
/* 111:    */       {
/* 112:    */         public void changedUpdate(DocumentEvent e)
/* 113:    */         {
/* 114:200 */           Script.this.m_Modified = true;
/* 115:    */         }
/* 116:    */         
/* 117:    */         public void insertUpdate(DocumentEvent e)
/* 118:    */         {
/* 119:205 */           Script.this.m_Modified = true;
/* 120:    */         }
/* 121:    */         
/* 122:    */         public void removeUpdate(DocumentEvent e)
/* 123:    */         {
/* 124:210 */           Script.this.m_Modified = true;
/* 125:    */         }
/* 126:    */       });
/* 127:    */     }
/* 128:215 */     if (file != null) {
/* 129:216 */       open(file);
/* 130:    */     }
/* 131:    */   }
/* 132:    */   
/* 133:    */   protected void initialize()
/* 134:    */   {
/* 135:224 */     this.m_Filename = null;
/* 136:225 */     this.m_NewLine = System.getProperty("line.separator");
/* 137:226 */     this.m_Modified = false;
/* 138:227 */     this.m_ScriptThread = null;
/* 139:228 */     this.m_FinishedListeners = new HashSet();
/* 140:    */   }
/* 141:    */   
/* 142:    */   public Enumeration<Option> listOptions()
/* 143:    */   {
/* 144:238 */     return new Vector().elements();
/* 145:    */   }
/* 146:    */   
/* 147:    */   public void setOptions(String[] options)
/* 148:    */     throws Exception
/* 149:    */   {}
/* 150:    */   
/* 151:    */   public String[] getOptions()
/* 152:    */   {
/* 153:258 */     return new String[0];
/* 154:    */   }
/* 155:    */   
/* 156:    */   public abstract ExtensionFileFilter[] getFilters();
/* 157:    */   
/* 158:    */   public abstract String getDefaultExtension();
/* 159:    */   
/* 160:    */   public File getFilename()
/* 161:    */   {
/* 162:283 */     return this.m_Filename;
/* 163:    */   }
/* 164:    */   
/* 165:    */   public String getNewLine()
/* 166:    */   {
/* 167:292 */     return this.m_NewLine;
/* 168:    */   }
/* 169:    */   
/* 170:    */   public boolean isModified()
/* 171:    */   {
/* 172:301 */     return this.m_Modified;
/* 173:    */   }
/* 174:    */   
/* 175:    */   public String getContent()
/* 176:    */   {
/* 177:312 */     if (this.m_Document == null) {
/* 178:313 */       return "";
/* 179:    */     }
/* 180:    */     String result;
/* 181:    */     try
/* 182:    */     {
/* 183:317 */       synchronized (this.m_Document)
/* 184:    */       {
/* 185:318 */         result = this.m_Document.getText(0, this.m_Document.getLength());
/* 186:    */       }
/* 187:    */     }
/* 188:    */     catch (Exception e)
/* 189:    */     {
/* 190:321 */       e.printStackTrace();
/* 191:322 */       result = null;
/* 192:    */     }
/* 193:325 */     return result;
/* 194:    */   }
/* 195:    */   
/* 196:    */   public void setContent(String value)
/* 197:    */   {
/* 198:334 */     if (this.m_Document == null) {
/* 199:335 */       return;
/* 200:    */     }
/* 201:    */     try
/* 202:    */     {
/* 203:339 */       this.m_Document.insertString(0, value, null);
/* 204:    */     }
/* 205:    */     catch (Exception e)
/* 206:    */     {
/* 207:341 */       e.printStackTrace();
/* 208:    */     }
/* 209:    */   }
/* 210:    */   
/* 211:    */   protected boolean checkExtension(File file)
/* 212:    */   {
/* 213:358 */     boolean result = false;
/* 214:359 */     ExtensionFileFilter[] filters = getFilters();
/* 215:360 */     for (int i = 0; i < filters.length; i++)
/* 216:    */     {
/* 217:361 */       String[] exts = filters[i].getExtensions();
/* 218:362 */       for (int n = 0; n < exts.length; n++) {
/* 219:363 */         if (file.getName().endsWith(exts[n]))
/* 220:    */         {
/* 221:364 */           result = true;
/* 222:365 */           break;
/* 223:    */         }
/* 224:    */       }
/* 225:368 */       if (result) {
/* 226:    */         break;
/* 227:    */       }
/* 228:    */     }
/* 229:373 */     return result;
/* 230:    */   }
/* 231:    */   
/* 232:    */   public void empty()
/* 233:    */   {
/* 234:380 */     if (this.m_Document != null) {
/* 235:    */       try
/* 236:    */       {
/* 237:382 */         this.m_Document.remove(0, this.m_Document.getLength());
/* 238:    */       }
/* 239:    */       catch (Exception e) {}
/* 240:    */     }
/* 241:388 */     this.m_Modified = false;
/* 242:389 */     this.m_Filename = null;
/* 243:    */   }
/* 244:    */   
/* 245:    */   public boolean open(File file)
/* 246:    */   {
/* 247:402 */     if (this.m_Document == null) {
/* 248:403 */       return true;
/* 249:    */     }
/* 250:407 */     if (!checkExtension(file)) {
/* 251:408 */       System.err.println("Extension of file '" + file + "' is unknown!");
/* 252:    */     }
/* 253:    */     boolean result;
/* 254:    */     try
/* 255:    */     {
/* 256:413 */       this.m_Document.remove(0, this.m_Document.getLength());
/* 257:    */       
/* 258:    */ 
/* 259:416 */       String content = ScriptUtils.load(file);
/* 260:417 */       if (content == null) {
/* 261:418 */         throw new WekaException("Error reading content of file '" + file + "'!");
/* 262:    */       }
/* 263:420 */       this.m_Document.insertString(0, content, null);
/* 264:    */       
/* 265:422 */       this.m_Modified = false;
/* 266:423 */       this.m_Filename = file;
/* 267:424 */       result = true;
/* 268:    */     }
/* 269:    */     catch (Exception e)
/* 270:    */     {
/* 271:426 */       e.printStackTrace();
/* 272:    */       try
/* 273:    */       {
/* 274:428 */         this.m_Document.remove(0, this.m_Document.getLength());
/* 275:    */       }
/* 276:    */       catch (Exception ex) {}
/* 277:432 */       result = false;
/* 278:433 */       this.m_Filename = null;
/* 279:    */     }
/* 280:436 */     return result;
/* 281:    */   }
/* 282:    */   
/* 283:    */   public boolean save()
/* 284:    */   {
/* 285:445 */     if (this.m_Filename == null) {
/* 286:446 */       return false;
/* 287:    */     }
/* 288:448 */     return saveAs(this.m_Filename);
/* 289:    */   }
/* 290:    */   
/* 291:    */   public boolean saveAs(File file)
/* 292:    */   {
/* 293:463 */     if (this.m_Document == null) {
/* 294:464 */       return true;
/* 295:    */     }
/* 296:468 */     if (!checkExtension(file)) {
/* 297:469 */       file = new File(file.getPath() + getDefaultExtension());
/* 298:    */     }
/* 299:473 */     if (file.exists())
/* 300:    */     {
/* 301:474 */       File backupFile = new File(file.getPath() + ".bak");
/* 302:    */       try
/* 303:    */       {
/* 304:476 */         ScriptUtils.copy(file, backupFile);
/* 305:    */       }
/* 306:    */       catch (Exception e)
/* 307:    */       {
/* 308:478 */         e.printStackTrace();
/* 309:    */       }
/* 310:    */     }
/* 311:    */     boolean result;
/* 312:    */     try
/* 313:    */     {
/* 314:484 */       result = ScriptUtils.save(file, this.m_Document.getText(0, this.m_Document.getLength()));
/* 315:    */       
/* 316:486 */       this.m_Filename = file;
/* 317:487 */       this.m_Modified = false;
/* 318:    */     }
/* 319:    */     catch (Exception e)
/* 320:    */     {
/* 321:489 */       e.printStackTrace();
/* 322:490 */       result = false;
/* 323:    */     }
/* 324:493 */     return result;
/* 325:    */   }
/* 326:    */   
/* 327:    */   protected abstract boolean canExecuteScripts();
/* 328:    */   
/* 329:    */   public abstract ScriptThread newThread(String[] paramArrayOfString);
/* 330:    */   
/* 331:    */   protected void preCheck(String[] args)
/* 332:    */     throws Exception
/* 333:    */   {
/* 334:524 */     if (this.m_ScriptThread != null) {
/* 335:525 */       throw new Exception("A script is currently running!");
/* 336:    */     }
/* 337:527 */     if (this.m_Modified) {
/* 338:528 */       throw new Exception("The Script has been modified!");
/* 339:    */     }
/* 340:530 */     if (this.m_Filename == null) {
/* 341:531 */       throw new Exception("The Script contains no content?");
/* 342:    */     }
/* 343:    */   }
/* 344:    */   
/* 345:    */   protected void execute(String[] args)
/* 346:    */   {
/* 347:541 */     this.m_ScriptThread = newThread(args);
/* 348:    */     try
/* 349:    */     {
/* 350:543 */       this.m_ScriptThread.start();
/* 351:    */     }
/* 352:    */     catch (Exception e)
/* 353:    */     {
/* 354:545 */       e.printStackTrace();
/* 355:    */     }
/* 356:    */   }
/* 357:    */   
/* 358:    */   public void start(String[] args)
/* 359:    */     throws Exception
/* 360:    */   {
/* 361:556 */     if (args == null) {
/* 362:557 */       args = new String[0];
/* 363:    */     }
/* 364:560 */     preCheck(args);
/* 365:    */     
/* 366:562 */     execute(args);
/* 367:    */   }
/* 368:    */   
/* 369:    */   public void stop()
/* 370:    */   {
/* 371:569 */     if (isRunning())
/* 372:    */     {
/* 373:570 */       this.m_ScriptThread.stopScript();
/* 374:571 */       this.m_ScriptThread = null;
/* 375:572 */       notifyScriptFinishedListeners(new ScriptExecutionEvent(this, ScriptExecutionEvent.Type.STOPPED));
/* 376:    */     }
/* 377:    */   }
/* 378:    */   
/* 379:    */   public void run(File file, String[] args)
/* 380:    */   {
/* 381:    */     try
/* 382:    */     {
/* 383:586 */       Script script = (Script)new SerializedObject(this).getObject();
/* 384:587 */       script.m_Filename = file;
/* 385:588 */       script.m_Modified = false;
/* 386:589 */       script.start(args);
/* 387:    */     }
/* 388:    */     catch (Exception e)
/* 389:    */     {
/* 390:591 */       e.printStackTrace();
/* 391:    */     }
/* 392:    */   }
/* 393:    */   
/* 394:    */   public boolean isRunning()
/* 395:    */   {
/* 396:601 */     return this.m_ScriptThread != null;
/* 397:    */   }
/* 398:    */   
/* 399:    */   public void addScriptFinishedListener(ScriptExecutionListener l)
/* 400:    */   {
/* 401:610 */     this.m_FinishedListeners.add(l);
/* 402:    */   }
/* 403:    */   
/* 404:    */   public void removeScriptFinishedListener(ScriptExecutionListener l)
/* 405:    */   {
/* 406:619 */     this.m_FinishedListeners.remove(l);
/* 407:    */   }
/* 408:    */   
/* 409:    */   protected void notifyScriptFinishedListeners(ScriptExecutionEvent e)
/* 410:    */   {
/* 411:630 */     Iterator<ScriptExecutionListener> iter = this.m_FinishedListeners.iterator();
/* 412:631 */     while (iter.hasNext()) {
/* 413:632 */       ((ScriptExecutionListener)iter.next()).scriptFinished(e);
/* 414:    */     }
/* 415:    */   }
/* 416:    */   
/* 417:    */   public String toString()
/* 418:    */   {
/* 419:    */     String result;
/* 420:    */     try
/* 421:    */     {
/* 422:    */       String result;
/* 423:646 */       if (this.m_Document == null) {
/* 424:647 */         result = "";
/* 425:    */       } else {
/* 426:649 */         result = this.m_Document.getText(0, this.m_Document.getLength());
/* 427:    */       }
/* 428:    */     }
/* 429:    */     catch (Exception e)
/* 430:    */     {
/* 431:652 */       result = "";
/* 432:    */     }
/* 433:655 */     return result.toString();
/* 434:    */   }
/* 435:    */   
/* 436:    */   protected static String makeOptionString(Script script)
/* 437:    */   {
/* 438:669 */     StringBuffer result = new StringBuffer("");
/* 439:    */     
/* 440:671 */     result.append("\nHelp requested:\n\n");
/* 441:672 */     result.append("-h or -help\n");
/* 442:673 */     result.append("\tDisplays this help screen.\n");
/* 443:674 */     result.append("-s <file>\n");
/* 444:675 */     result.append("\tThe script to execute.\n");
/* 445:    */     
/* 446:677 */     Enumeration<Option> enm = script.listOptions();
/* 447:678 */     while (enm.hasMoreElements())
/* 448:    */     {
/* 449:679 */       Option option = (Option)enm.nextElement();
/* 450:680 */       result.append(option.synopsis() + '\n');
/* 451:681 */       result.append(option.description() + "\n");
/* 452:    */     }
/* 453:684 */     result.append("\n");
/* 454:685 */     result.append("Any additional options are passed on to the script as\n");
/* 455:686 */     result.append("command-line parameters.\n");
/* 456:687 */     result.append("\n");
/* 457:    */     
/* 458:689 */     return result.toString();
/* 459:    */   }
/* 460:    */   
/* 461:    */   public static void runScript(Script script, String[] args)
/* 462:    */     throws Exception
/* 463:    */   {
/* 464:707 */     if ((Utils.getFlag('h', args)) || (Utils.getFlag("help", args)))
/* 465:    */     {
/* 466:708 */       System.out.println(makeOptionString(script));
/* 467:    */     }
/* 468:    */     else
/* 469:    */     {
/* 470:711 */       String tmpStr = Utils.getOption('s', args);
/* 471:712 */       if (tmpStr.length() == 0) {
/* 472:713 */         throw new WekaException("No script supplied!");
/* 473:    */       }
/* 474:715 */       File scriptFile = new File(tmpStr);
/* 475:    */       
/* 476:717 */       script.setOptions(args);
/* 477:    */       
/* 478:    */ 
/* 479:720 */       Vector<String> options = new Vector();
/* 480:721 */       for (int i = 0; i < args.length; i++) {
/* 481:722 */         if (args[i].length() > 0) {
/* 482:723 */           options.add(args[i]);
/* 483:    */         }
/* 484:    */       }
/* 485:728 */       script.run(scriptFile, (String[])options.toArray(new String[options.size()]));
/* 486:    */     }
/* 487:    */   }
/* 488:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.scripting.Script
 * JD-Core Version:    0.7.0.1
 */