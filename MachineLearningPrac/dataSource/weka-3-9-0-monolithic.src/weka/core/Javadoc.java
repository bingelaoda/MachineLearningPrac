/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.FileReader;
/*   6:    */ import java.io.PrintStream;
/*   7:    */ import java.util.Enumeration;
/*   8:    */ import java.util.StringTokenizer;
/*   9:    */ import java.util.Vector;
/*  10:    */ 
/*  11:    */ public abstract class Javadoc
/*  12:    */   implements OptionHandler, RevisionHandler
/*  13:    */ {
/*  14: 40 */   protected String[] m_StartTag = null;
/*  15: 43 */   protected String[] m_EndTag = null;
/*  16: 46 */   protected String m_Classname = Javadoc.class.getName();
/*  17: 49 */   protected boolean m_UseStars = true;
/*  18: 52 */   protected String m_Dir = "";
/*  19: 55 */   protected boolean m_Silent = false;
/*  20:    */   
/*  21:    */   public Enumeration<Option> listOptions()
/*  22:    */   {
/*  23: 64 */     Vector<Option> result = new Vector();
/*  24:    */     
/*  25: 66 */     result.addElement(new Option("\tThe class to load.", "W", 1, "-W <classname>"));
/*  26:    */     
/*  27:    */ 
/*  28: 69 */     result.addElement(new Option("\tSuppresses the '*' in the Javadoc.", "nostars", 0, "-nostars"));
/*  29:    */     
/*  30:    */ 
/*  31: 72 */     result.addElement(new Option("\tThe directory above the package hierarchy of the class.", "dir", 1, "-dir <dir>"));
/*  32:    */     
/*  33:    */ 
/*  34:    */ 
/*  35: 76 */     result.addElement(new Option("\tSuppresses printing in the console.", "silent", 0, "-silent"));
/*  36:    */     
/*  37:    */ 
/*  38: 79 */     return result.elements();
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void setOptions(String[] options)
/*  42:    */     throws Exception
/*  43:    */   {
/*  44: 92 */     String tmpStr = Utils.getOption('W', options);
/*  45: 93 */     if (tmpStr.length() > 0) {
/*  46: 94 */       setClassname(tmpStr);
/*  47:    */     } else {
/*  48: 96 */       setClassname(getClass().getName());
/*  49:    */     }
/*  50: 99 */     setUseStars(!Utils.getFlag("nostars", options));
/*  51:    */     
/*  52:101 */     setDir(Utils.getOption("dir", options));
/*  53:    */     
/*  54:103 */     setSilent(Utils.getFlag("silent", options));
/*  55:    */   }
/*  56:    */   
/*  57:    */   public String[] getOptions()
/*  58:    */   {
/*  59:115 */     Vector<String> result = new Vector();
/*  60:    */     
/*  61:117 */     result.add("-W");
/*  62:118 */     result.add(getClassname());
/*  63:120 */     if (!getUseStars()) {
/*  64:121 */       result.add("-nostars");
/*  65:    */     }
/*  66:124 */     if (getDir().length() != 0)
/*  67:    */     {
/*  68:125 */       result.add("-dir");
/*  69:126 */       result.add(getDir());
/*  70:    */     }
/*  71:129 */     if (getSilent()) {
/*  72:130 */       result.add("-silent");
/*  73:    */     }
/*  74:133 */     return (String[])result.toArray(new String[result.size()]);
/*  75:    */   }
/*  76:    */   
/*  77:    */   public void setClassname(String value)
/*  78:    */   {
/*  79:142 */     this.m_Classname = value;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public String getClassname()
/*  83:    */   {
/*  84:151 */     return this.m_Classname;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public void setUseStars(boolean value)
/*  88:    */   {
/*  89:160 */     this.m_UseStars = value;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public boolean getUseStars()
/*  93:    */   {
/*  94:169 */     return this.m_UseStars;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public void setDir(String value)
/*  98:    */   {
/*  99:179 */     this.m_Dir = value;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public String getDir()
/* 103:    */   {
/* 104:189 */     return this.m_Dir;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public void setSilent(boolean value)
/* 108:    */   {
/* 109:198 */     this.m_Silent = value;
/* 110:    */   }
/* 111:    */   
/* 112:    */   public boolean getSilent()
/* 113:    */   {
/* 114:207 */     return this.m_Silent;
/* 115:    */   }
/* 116:    */   
/* 117:    */   protected void println(Object o)
/* 118:    */   {
/* 119:216 */     if (!getSilent()) {
/* 120:217 */       System.err.println(o.toString());
/* 121:    */     }
/* 122:    */   }
/* 123:    */   
/* 124:    */   protected boolean canInstantiateClass()
/* 125:    */   {
/* 126:231 */     boolean result = true;
/* 127:232 */     Class<?> cls = null;
/* 128:    */     try
/* 129:    */     {
/* 130:235 */       cls = Class.forName(getClassname());
/* 131:    */     }
/* 132:    */     catch (Exception e)
/* 133:    */     {
/* 134:237 */       result = false;
/* 135:238 */       println("Cannot instantiate '" + getClassname() + "'! Class in CLASSPATH?");
/* 136:    */     }
/* 137:242 */     if (result) {
/* 138:    */       try
/* 139:    */       {
/* 140:244 */         cls.newInstance();
/* 141:    */       }
/* 142:    */       catch (Exception e)
/* 143:    */       {
/* 144:246 */         result = false;
/* 145:247 */         println("Cannot instantiate '" + getClassname() + "'! Missing default constructor?");
/* 146:    */       }
/* 147:    */     }
/* 148:252 */     return result;
/* 149:    */   }
/* 150:    */   
/* 151:    */   protected Object getInstance()
/* 152:    */   {
/* 153:264 */     Object result = null;
/* 154:    */     try
/* 155:    */     {
/* 156:267 */       Class<?> cls = Class.forName(getClassname());
/* 157:268 */       result = cls.newInstance();
/* 158:    */     }
/* 159:    */     catch (Exception e)
/* 160:    */     {
/* 161:270 */       result = null;
/* 162:    */     }
/* 163:273 */     return result;
/* 164:    */   }
/* 165:    */   
/* 166:    */   protected String toHTML(String s)
/* 167:    */   {
/* 168:286 */     String result = s;
/* 169:    */     
/* 170:288 */     result = result.replaceAll("&", "&amp;");
/* 171:289 */     result = result.replaceAll("<", "&lt;");
/* 172:290 */     result = result.replaceAll(">", "&gt;");
/* 173:291 */     result = result.replaceAll("@", "&#64;");
/* 174:292 */     result = result.replaceAll("\n", "<br>\n");
/* 175:    */     
/* 176:294 */     return result;
/* 177:    */   }
/* 178:    */   
/* 179:    */   protected String indent(String content, int count, String indentStr)
/* 180:    */   {
/* 181:310 */     StringTokenizer tok = new StringTokenizer(content, "\n", true);
/* 182:311 */     String result = "";
/* 183:312 */     while (tok.hasMoreTokens())
/* 184:    */     {
/* 185:313 */       if ((result.endsWith("\n")) || (result.length() == 0)) {
/* 186:314 */         for (int i = 0; i < count; i++) {
/* 187:315 */           result = result + indentStr;
/* 188:    */         }
/* 189:    */       }
/* 190:318 */       result = result + tok.nextToken();
/* 191:    */     }
/* 192:321 */     return result;
/* 193:    */   }
/* 194:    */   
/* 195:    */   protected abstract String generateJavadoc(int paramInt)
/* 196:    */     throws Exception;
/* 197:    */   
/* 198:    */   protected String generateJavadoc()
/* 199:    */     throws Exception
/* 200:    */   {
/* 201:343 */     String result = "";
/* 202:345 */     for (int i = 0; i < this.m_StartTag.length; i++)
/* 203:    */     {
/* 204:346 */       if (i > 0) {
/* 205:347 */         result = result + "\n\n";
/* 206:    */       }
/* 207:349 */       result = result + generateJavadoc(i).trim();
/* 208:    */     }
/* 209:352 */     return result;
/* 210:    */   }
/* 211:    */   
/* 212:    */   protected String getIndentionString(String str)
/* 213:    */   {
/* 214:    */     String result;
/* 215:    */     String result;
/* 216:367 */     if (str.replaceAll(" ", "").length() == 0)
/* 217:    */     {
/* 218:368 */       result = " ";
/* 219:    */     }
/* 220:    */     else
/* 221:    */     {
/* 222:    */       String result;
/* 223:369 */       if (str.replaceAll("\t", "").length() == 0) {
/* 224:370 */         result = "\t";
/* 225:    */       } else {
/* 226:372 */         result = str;
/* 227:    */       }
/* 228:    */     }
/* 229:375 */     return result;
/* 230:    */   }
/* 231:    */   
/* 232:    */   protected int getIndentionLength(String str)
/* 233:    */   {
/* 234:    */     int result;
/* 235:    */     int result;
/* 236:389 */     if (str.replaceAll(" ", "").length() == 0)
/* 237:    */     {
/* 238:390 */       result = str.length();
/* 239:    */     }
/* 240:    */     else
/* 241:    */     {
/* 242:    */       int result;
/* 243:391 */       if (str.replaceAll("\t", "").length() == 0) {
/* 244:392 */         result = str.length();
/* 245:    */       } else {
/* 246:394 */         result = 1;
/* 247:    */       }
/* 248:    */     }
/* 249:397 */     return result;
/* 250:    */   }
/* 251:    */   
/* 252:    */   protected String updateJavadoc(String content, int index)
/* 253:    */     throws Exception
/* 254:    */   {
/* 255:416 */     if ((content.indexOf(this.m_StartTag[index]) == -1) || (content.indexOf(this.m_EndTag[index]) == -1))
/* 256:    */     {
/* 257:418 */       println("No start and/or end tags found: " + this.m_StartTag[index] + "/" + this.m_EndTag[index]);
/* 258:    */       
/* 259:420 */       return content;
/* 260:    */     }
/* 261:424 */     StringBuffer resultBuf = new StringBuffer();
/* 262:425 */     while (content.length() > 0) {
/* 263:426 */       if (content.indexOf(this.m_StartTag[index]) > -1)
/* 264:    */       {
/* 265:427 */         String part = content.substring(0, content.indexOf(this.m_StartTag[index]));
/* 266:429 */         if (part.endsWith("\""))
/* 267:    */         {
/* 268:430 */           resultBuf.append(part);
/* 269:431 */           resultBuf.append(this.m_StartTag[index]);
/* 270:432 */           content = content.substring(part.length() + this.m_StartTag[index].length());
/* 271:    */         }
/* 272:    */         else
/* 273:    */         {
/* 274:435 */           String tmpStr = part.substring(part.lastIndexOf("\n") + 1);
/* 275:436 */           int indentionLen = getIndentionLength(tmpStr);
/* 276:437 */           String indentionStr = getIndentionString(tmpStr);
/* 277:438 */           part = part.substring(0, part.lastIndexOf("\n") + 1);
/* 278:439 */           resultBuf.append(part);
/* 279:440 */           resultBuf.append(indent(this.m_StartTag[index], indentionLen, indentionStr) + "\n");
/* 280:    */           
/* 281:    */ 
/* 282:443 */           resultBuf.append(indent(generateJavadoc(index), indentionLen, indentionStr));
/* 283:    */           
/* 284:445 */           resultBuf.append(indent(this.m_EndTag[index], indentionLen, indentionStr));
/* 285:446 */           content = content.substring(content.indexOf(this.m_EndTag[index]));
/* 286:447 */           content = content.substring(this.m_EndTag[index].length());
/* 287:    */         }
/* 288:    */       }
/* 289:    */       else
/* 290:    */       {
/* 291:450 */         resultBuf.append(content);
/* 292:451 */         content = "";
/* 293:    */       }
/* 294:    */     }
/* 295:455 */     return resultBuf.toString().trim();
/* 296:    */   }
/* 297:    */   
/* 298:    */   protected String updateJavadoc(String content)
/* 299:    */     throws Exception
/* 300:    */   {
/* 301:469 */     String result = content;
/* 302:471 */     for (int i = 0; i < this.m_StartTag.length; i++) {
/* 303:472 */       result = updateJavadoc(result, i);
/* 304:    */     }
/* 305:475 */     return result;
/* 306:    */   }
/* 307:    */   
/* 308:    */   public String updateJavadoc()
/* 309:    */     throws Exception
/* 310:    */   {
/* 311:492 */     String result = "";
/* 312:    */     
/* 313:    */ 
/* 314:495 */     File file = new File(getDir() + "/" + getClassname().replaceAll("\\.", "/") + ".java");
/* 315:497 */     if (!file.exists())
/* 316:    */     {
/* 317:498 */       println("File '" + file.getAbsolutePath() + "' doesn't exist!");
/* 318:499 */       return result;
/* 319:    */     }
/* 320:    */     try
/* 321:    */     {
/* 322:504 */       BufferedReader reader = new BufferedReader(new FileReader(file));
/* 323:505 */       StringBuffer contentBuf = new StringBuffer();
/* 324:    */       String line;
/* 325:506 */       while ((line = reader.readLine()) != null) {
/* 326:507 */         contentBuf.append(line + "\n");
/* 327:    */       }
/* 328:509 */       reader.close();
/* 329:510 */       result = updateJavadoc(contentBuf.toString());
/* 330:    */     }
/* 331:    */     catch (Exception e)
/* 332:    */     {
/* 333:512 */       e.printStackTrace();
/* 334:    */     }
/* 335:515 */     return result.trim();
/* 336:    */   }
/* 337:    */   
/* 338:    */   public String generate()
/* 339:    */     throws Exception
/* 340:    */   {
/* 341:527 */     if (getDir().length() == 0) {
/* 342:528 */       return generateJavadoc();
/* 343:    */     }
/* 344:530 */     return updateJavadoc();
/* 345:    */   }
/* 346:    */   
/* 347:    */   public String generateHelp()
/* 348:    */   {
/* 349:544 */     String result = getClass().getName().replaceAll(".*\\.", "") + " Options:\n\n";
/* 350:545 */     Enumeration<Option> enm = listOptions();
/* 351:546 */     while (enm.hasMoreElements())
/* 352:    */     {
/* 353:547 */       Option option = (Option)enm.nextElement();
/* 354:548 */       result = result + option.synopsis() + "\n" + option.description() + "\n";
/* 355:    */     }
/* 356:551 */     return result;
/* 357:    */   }
/* 358:    */   
/* 359:    */   protected static void runJavadoc(Javadoc javadoc, String[] options)
/* 360:    */   {
/* 361:    */     try
/* 362:    */     {
/* 363:    */       try
/* 364:    */       {
/* 365:563 */         if (Utils.getFlag('h', options)) {
/* 366:564 */           throw new Exception("Help requested");
/* 367:    */         }
/* 368:567 */         javadoc.setOptions(options);
/* 369:568 */         Utils.checkForRemainingOptions(options);
/* 370:571 */         if (javadoc.getDir().length() == 0) {
/* 371:572 */           throw new Exception("No directory provided!");
/* 372:    */         }
/* 373:    */       }
/* 374:    */       catch (Exception ex)
/* 375:    */       {
/* 376:575 */         String result = "\n" + ex.getMessage() + "\n\n" + javadoc.generateHelp();
/* 377:    */         
/* 378:577 */         throw new Exception(result);
/* 379:    */       }
/* 380:580 */       System.out.println(javadoc.generate() + "\n");
/* 381:    */     }
/* 382:    */     catch (Exception ex)
/* 383:    */     {
/* 384:582 */       System.err.println(ex.getMessage());
/* 385:    */     }
/* 386:    */   }
/* 387:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.Javadoc
 * JD-Core Version:    0.7.0.1
 */