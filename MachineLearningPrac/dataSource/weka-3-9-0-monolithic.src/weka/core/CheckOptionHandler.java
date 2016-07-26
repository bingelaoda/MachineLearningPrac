/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.classifiers.rules.ZeroR;
/*   7:    */ 
/*   8:    */ public class CheckOptionHandler
/*   9:    */   extends Check
/*  10:    */ {
/*  11: 80 */   protected OptionHandler m_OptionHandler = new ZeroR();
/*  12: 83 */   protected String[] m_UserOptions = new String[0];
/*  13:    */   protected boolean m_Success;
/*  14:    */   
/*  15:    */   public Enumeration<Option> listOptions()
/*  16:    */   {
/*  17: 95 */     Vector<Option> result = new Vector();
/*  18:    */     
/*  19: 97 */     result.addAll(Collections.list(super.listOptions()));
/*  20:    */     
/*  21: 99 */     result.addElement(new Option("\tFull name of the OptionHandler analysed.\n\teg: weka.classifiers.rules.ZeroR\n\t(default weka.classifiers.rules.ZeroR)", "W", 1, "-W"));
/*  22:103 */     if (this.m_OptionHandler != null)
/*  23:    */     {
/*  24:104 */       result.addElement(new Option("", "", 0, "\nOptions specific to option handler " + this.m_OptionHandler.getClass().getName() + ":"));
/*  25:    */       
/*  26:    */ 
/*  27:    */ 
/*  28:108 */       result.addAll(Collections.list(this.m_OptionHandler.listOptions()));
/*  29:    */     }
/*  30:111 */     return result.elements();
/*  31:    */   }
/*  32:    */   
/*  33:    */   public void setOptions(String[] options)
/*  34:    */     throws Exception
/*  35:    */   {
/*  36:157 */     super.setOptions(options);
/*  37:    */     
/*  38:159 */     String tmpStr = Utils.getOption('W', options);
/*  39:160 */     if (tmpStr.length() == 0) {
/*  40:161 */       tmpStr = ZeroR.class.getName();
/*  41:    */     }
/*  42:163 */     setUserOptions(Utils.partitionOptions(options));
/*  43:164 */     setOptionHandler((OptionHandler)Utils.forName(OptionHandler.class, tmpStr, null));
/*  44:    */   }
/*  45:    */   
/*  46:    */   public String[] getOptions()
/*  47:    */   {
/*  48:175 */     Vector<String> result = new Vector();
/*  49:    */     
/*  50:177 */     Collections.addAll(result, super.getOptions());
/*  51:179 */     if (getOptionHandler() != null)
/*  52:    */     {
/*  53:180 */       result.add("-W");
/*  54:181 */       result.add(getOptionHandler().getClass().getName());
/*  55:    */     }
/*  56:184 */     if (this.m_OptionHandler != null)
/*  57:    */     {
/*  58:185 */       String[] options = this.m_OptionHandler.getOptions();
/*  59:186 */       result.add("--");
/*  60:187 */       Collections.addAll(result, options);
/*  61:    */     }
/*  62:190 */     return (String[])result.toArray(new String[result.size()]);
/*  63:    */   }
/*  64:    */   
/*  65:    */   public void setOptionHandler(OptionHandler value)
/*  66:    */   {
/*  67:199 */     this.m_OptionHandler = value;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public OptionHandler getOptionHandler()
/*  71:    */   {
/*  72:208 */     return this.m_OptionHandler;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public void setUserOptions(String[] value)
/*  76:    */   {
/*  77:217 */     this.m_UserOptions = getCopy(value);
/*  78:    */   }
/*  79:    */   
/*  80:    */   public String[] getUserOptions()
/*  81:    */   {
/*  82:226 */     return getCopy(this.m_UserOptions);
/*  83:    */   }
/*  84:    */   
/*  85:    */   public boolean getSuccess()
/*  86:    */   {
/*  87:235 */     return this.m_Success;
/*  88:    */   }
/*  89:    */   
/*  90:    */   protected String printOptions(String[] options)
/*  91:    */   {
/*  92:245 */     if (options == null) {
/*  93:246 */       return "<null>";
/*  94:    */     }
/*  95:248 */     return Utils.joinOptions(options);
/*  96:    */   }
/*  97:    */   
/*  98:    */   protected void compareOptions(String[] options1, String[] options2)
/*  99:    */     throws Exception
/* 100:    */   {
/* 101:262 */     if (options1 == null) {
/* 102:263 */       throw new Exception("first set of options is null!");
/* 103:    */     }
/* 104:265 */     if (options2 == null) {
/* 105:266 */       throw new Exception("second set of options is null!");
/* 106:    */     }
/* 107:268 */     if (options1.length != options2.length) {
/* 108:269 */       throw new Exception("problem found!\nFirst set: " + printOptions(options1) + '\n' + "Second set: " + printOptions(options2) + '\n' + "options differ in length");
/* 109:    */     }
/* 110:273 */     for (int i = 0; i < options1.length; i++) {
/* 111:274 */       if (!options1[i].equals(options2[i])) {
/* 112:276 */         throw new Exception("problem found!\n\tFirst set: " + printOptions(options1) + '\n' + "\tSecond set: " + printOptions(options2) + '\n' + '\t' + options1[i] + " != " + options2[i]);
/* 113:    */       }
/* 114:    */     }
/* 115:    */   }
/* 116:    */   
/* 117:    */   protected String[] getCopy(String[] options)
/* 118:    */   {
/* 119:293 */     String[] result = new String[options.length];
/* 120:294 */     System.arraycopy(options, 0, result, 0, options.length);
/* 121:    */     
/* 122:296 */     return result;
/* 123:    */   }
/* 124:    */   
/* 125:    */   protected OptionHandler getDefaultHandler()
/* 126:    */   {
/* 127:    */     OptionHandler result;
/* 128:    */     try
/* 129:    */     {
/* 130:308 */       result = (OptionHandler)this.m_OptionHandler.getClass().newInstance();
/* 131:    */     }
/* 132:    */     catch (Exception e)
/* 133:    */     {
/* 134:310 */       e.printStackTrace();
/* 135:311 */       result = null;
/* 136:    */     }
/* 137:314 */     return result;
/* 138:    */   }
/* 139:    */   
/* 140:    */   protected String[] getDefaultOptions()
/* 141:    */   {
/* 142:326 */     OptionHandler o = getDefaultHandler();
/* 143:    */     String[] result;
/* 144:    */     String[] result;
/* 145:327 */     if (o == null)
/* 146:    */     {
/* 147:328 */       println("WARNING: couldn't create default handler, cannot use default options!");
/* 148:329 */       result = new String[0];
/* 149:    */     }
/* 150:    */     else
/* 151:    */     {
/* 152:331 */       result = o.getOptions();
/* 153:    */     }
/* 154:334 */     return result;
/* 155:    */   }
/* 156:    */   
/* 157:    */   public boolean checkListOptions()
/* 158:    */   {
/* 159:345 */     print("ListOptions...");
/* 160:    */     boolean result;
/* 161:    */     try
/* 162:    */     {
/* 163:348 */       Enumeration<Option> enu = getOptionHandler().listOptions();
/* 164:349 */       if ((getDebug()) && (enu.hasMoreElements())) {
/* 165:350 */         println("");
/* 166:    */       }
/* 167:352 */       while (enu.hasMoreElements())
/* 168:    */       {
/* 169:353 */         Option option = (Option)enu.nextElement();
/* 170:354 */         if (getDebug())
/* 171:    */         {
/* 172:355 */           println(option.synopsis());
/* 173:356 */           println(option.description());
/* 174:    */         }
/* 175:    */       }
/* 176:360 */       println("yes");
/* 177:361 */       result = true;
/* 178:    */     }
/* 179:    */     catch (Exception e)
/* 180:    */     {
/* 181:363 */       println("no");
/* 182:364 */       result = false;
/* 183:366 */       if (getDebug()) {
/* 184:367 */         println(e);
/* 185:    */       }
/* 186:    */     }
/* 187:371 */     return result;
/* 188:    */   }
/* 189:    */   
/* 190:    */   public boolean checkSetOptions()
/* 191:    */   {
/* 192:382 */     print("SetOptions...");
/* 193:    */     boolean result;
/* 194:    */     try
/* 195:    */     {
/* 196:385 */       getDefaultHandler().setOptions(getUserOptions());
/* 197:386 */       println("yes");
/* 198:387 */       result = true;
/* 199:    */     }
/* 200:    */     catch (Exception e)
/* 201:    */     {
/* 202:389 */       println("no");
/* 203:390 */       result = false;
/* 204:392 */       if (getDebug()) {
/* 205:393 */         println(e);
/* 206:    */       }
/* 207:    */     }
/* 208:397 */     return result;
/* 209:    */   }
/* 210:    */   
/* 211:    */   public boolean checkDefaultOptions()
/* 212:    */   {
/* 213:410 */     print("Default options...");
/* 214:    */     
/* 215:412 */     String[] options = getDefaultOptions();
/* 216:    */     boolean result;
/* 217:    */     try
/* 218:    */     {
/* 219:415 */       getDefaultHandler().setOptions(options);
/* 220:416 */       Utils.checkForRemainingOptions(options);
/* 221:417 */       println("yes");
/* 222:418 */       result = true;
/* 223:    */     }
/* 224:    */     catch (Exception e)
/* 225:    */     {
/* 226:420 */       println("no");
/* 227:421 */       result = false;
/* 228:423 */       if (getDebug()) {
/* 229:424 */         println(e);
/* 230:    */       }
/* 231:    */     }
/* 232:428 */     return result;
/* 233:    */   }
/* 234:    */   
/* 235:    */   public boolean checkRemainingOptions()
/* 236:    */   {
/* 237:441 */     print("Remaining options...");
/* 238:    */     
/* 239:443 */     String[] options = getUserOptions();
/* 240:    */     boolean result;
/* 241:    */     try
/* 242:    */     {
/* 243:446 */       getDefaultHandler().setOptions(options);
/* 244:447 */       if (getDebug()) {
/* 245:448 */         println("\n  remaining: " + printOptions(options));
/* 246:    */       }
/* 247:450 */       println("yes");
/* 248:451 */       result = true;
/* 249:    */     }
/* 250:    */     catch (Exception e)
/* 251:    */     {
/* 252:453 */       println("no");
/* 253:454 */       result = false;
/* 254:456 */       if (getDebug()) {
/* 255:457 */         println(e);
/* 256:    */       }
/* 257:    */     }
/* 258:461 */     return result;
/* 259:    */   }
/* 260:    */   
/* 261:    */   public boolean checkCanonicalUserOptions()
/* 262:    */   {
/* 263:476 */     print("Canonical user options...");
/* 264:    */     boolean result;
/* 265:    */     try
/* 266:    */     {
/* 267:479 */       OptionHandler handler = getDefaultHandler();
/* 268:480 */       handler.setOptions(getUserOptions());
/* 269:481 */       if (getDebug()) {
/* 270:482 */         print("\n  Getting canonical user options: ");
/* 271:    */       }
/* 272:484 */       String[] userOptions = handler.getOptions();
/* 273:485 */       if (getDebug()) {
/* 274:486 */         println(printOptions(userOptions));
/* 275:    */       }
/* 276:488 */       if (getDebug()) {
/* 277:489 */         println("  Setting canonical user options");
/* 278:    */       }
/* 279:491 */       handler.setOptions((String[])userOptions.clone());
/* 280:492 */       if (getDebug()) {
/* 281:493 */         println("  Checking canonical user options");
/* 282:    */       }
/* 283:495 */       String[] userOptionsCheck = handler.getOptions();
/* 284:496 */       compareOptions(userOptions, userOptionsCheck);
/* 285:    */       
/* 286:498 */       println("yes");
/* 287:499 */       result = true;
/* 288:    */     }
/* 289:    */     catch (Exception e)
/* 290:    */     {
/* 291:501 */       println("no");
/* 292:502 */       result = false;
/* 293:504 */       if (getDebug()) {
/* 294:505 */         println(e);
/* 295:    */       }
/* 296:    */     }
/* 297:509 */     return result;
/* 298:    */   }
/* 299:    */   
/* 300:    */   public boolean checkResettingOptions()
/* 301:    */   {
/* 302:524 */     print("Resetting options...");
/* 303:    */     boolean result;
/* 304:    */     try
/* 305:    */     {
/* 306:527 */       if (getDebug()) {
/* 307:528 */         println("\n  Setting user options");
/* 308:    */       }
/* 309:530 */       OptionHandler handler = getDefaultHandler();
/* 310:531 */       handler.setOptions(getUserOptions());
/* 311:532 */       String[] defaultOptions = getDefaultOptions();
/* 312:533 */       if (getDebug()) {
/* 313:534 */         println("  Resetting to default options");
/* 314:    */       }
/* 315:536 */       handler.setOptions(getCopy(defaultOptions));
/* 316:537 */       if (getDebug()) {
/* 317:538 */         println("  Checking default options match previous default");
/* 318:    */       }
/* 319:540 */       String[] defaultOptionsCheck = handler.getOptions();
/* 320:541 */       compareOptions(defaultOptions, defaultOptionsCheck);
/* 321:    */       
/* 322:543 */       println("yes");
/* 323:544 */       result = true;
/* 324:    */     }
/* 325:    */     catch (Exception e)
/* 326:    */     {
/* 327:546 */       println("no");
/* 328:547 */       result = false;
/* 329:549 */       if (getDebug()) {
/* 330:550 */         println(e);
/* 331:    */       }
/* 332:    */     }
/* 333:554 */     return result;
/* 334:    */   }
/* 335:    */   
/* 336:    */   public void doTests()
/* 337:    */   {
/* 338:563 */     println("OptionHandler: " + this.m_OptionHandler.getClass().getName() + "\n");
/* 339:565 */     if (getDebug())
/* 340:    */     {
/* 341:566 */       println("--> Info");
/* 342:567 */       print("Default options: ");
/* 343:568 */       println(printOptions(getDefaultOptions()));
/* 344:569 */       print("User options: ");
/* 345:570 */       println(printOptions(getUserOptions()));
/* 346:    */     }
/* 347:573 */     println("--> Tests");
/* 348:574 */     this.m_Success = checkListOptions();
/* 349:576 */     if (this.m_Success) {
/* 350:577 */       this.m_Success = checkSetOptions();
/* 351:    */     }
/* 352:580 */     if (this.m_Success) {
/* 353:581 */       this.m_Success = checkDefaultOptions();
/* 354:    */     }
/* 355:584 */     if (this.m_Success) {
/* 356:585 */       this.m_Success = checkRemainingOptions();
/* 357:    */     }
/* 358:588 */     if (this.m_Success) {
/* 359:589 */       this.m_Success = checkCanonicalUserOptions();
/* 360:    */     }
/* 361:592 */     if (this.m_Success) {
/* 362:593 */       this.m_Success = checkResettingOptions();
/* 363:    */     }
/* 364:    */   }
/* 365:    */   
/* 366:    */   public String getRevision()
/* 367:    */   {
/* 368:604 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 369:    */   }
/* 370:    */   
/* 371:    */   public static void main(String[] args)
/* 372:    */   {
/* 373:613 */     CheckOptionHandler check = new CheckOptionHandler();
/* 374:614 */     runCheck(check, args);
/* 375:615 */     if (check.getSuccess()) {
/* 376:616 */       System.exit(0);
/* 377:    */     } else {
/* 378:618 */       System.exit(1);
/* 379:    */     }
/* 380:    */   }
/* 381:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.CheckOptionHandler
 * JD-Core Version:    0.7.0.1
 */