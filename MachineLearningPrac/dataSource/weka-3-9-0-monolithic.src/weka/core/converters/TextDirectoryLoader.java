/*   1:    */ package weka.core.converters;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.FileInputStream;
/*   6:    */ import java.io.IOException;
/*   7:    */ import java.io.InputStreamReader;
/*   8:    */ import java.io.PrintStream;
/*   9:    */ import java.util.ArrayList;
/*  10:    */ import java.util.Enumeration;
/*  11:    */ import java.util.LinkedList;
/*  12:    */ import java.util.List;
/*  13:    */ import java.util.Vector;
/*  14:    */ import weka.core.Attribute;
/*  15:    */ import weka.core.CommandlineRunnable;
/*  16:    */ import weka.core.DenseInstance;
/*  17:    */ import weka.core.Instance;
/*  18:    */ import weka.core.Instances;
/*  19:    */ import weka.core.Option;
/*  20:    */ import weka.core.OptionHandler;
/*  21:    */ import weka.core.RevisionUtils;
/*  22:    */ import weka.core.SerializedObject;
/*  23:    */ import weka.core.Utils;
/*  24:    */ 
/*  25:    */ public class TextDirectoryLoader
/*  26:    */   extends AbstractLoader
/*  27:    */   implements BatchConverter, IncrementalConverter, OptionHandler, CommandlineRunnable
/*  28:    */ {
/*  29:    */   private static final long serialVersionUID = 2592118773712247647L;
/*  30:112 */   protected Instances m_structure = null;
/*  31:115 */   protected File m_sourceFile = new File(System.getProperty("user.dir"));
/*  32:118 */   protected boolean m_Debug = false;
/*  33:121 */   protected boolean m_OutputFilename = false;
/*  34:127 */   protected String m_charSet = "";
/*  35:    */   protected List<LinkedList<String>> m_filesByClass;
/*  36:    */   
/*  37:    */   public TextDirectoryLoader()
/*  38:    */   {
/*  39:134 */     setRetrieval(0);
/*  40:    */   }
/*  41:    */   
/*  42:    */   public String globalInfo()
/*  43:    */   {
/*  44:144 */     return "Loads all text files in a directory and uses the subdirectory names as class labels. The content of the text files will be stored in a String attribute, the filename can be stored as well.";
/*  45:    */   }
/*  46:    */   
/*  47:    */   public Enumeration<Option> listOptions()
/*  48:    */   {
/*  49:157 */     Vector<Option> result = new Vector();
/*  50:    */     
/*  51:159 */     result.add(new Option("\tEnables debug output.\n\t(default: off)", "D", 0, "-D"));
/*  52:    */     
/*  53:    */ 
/*  54:162 */     result.add(new Option("\tStores the filename in an additional attribute.\n\t(default: off)", "F", 0, "-F"));
/*  55:    */     
/*  56:    */ 
/*  57:165 */     result.add(new Option("\tThe directory to work on.\n\t(default: current directory)", "dir", 0, "-dir <directory>"));
/*  58:    */     
/*  59:    */ 
/*  60:168 */     result.add(new Option("\tThe character set to use, e.g UTF-8.\n\t(default: use the default character set)", "charset", 1, "-charset <charset name>"));
/*  61:    */     
/*  62:    */ 
/*  63:    */ 
/*  64:172 */     return result.elements();
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void setOptions(String[] options)
/*  68:    */     throws Exception
/*  69:    */   {
/*  70:213 */     setDebug(Utils.getFlag("D", options));
/*  71:    */     
/*  72:215 */     setOutputFilename(Utils.getFlag("F", options));
/*  73:    */     
/*  74:217 */     setDirectory(new File(Utils.getOption("dir", options)));
/*  75:    */     
/*  76:219 */     String charSet = Utils.getOption("charset", options);
/*  77:220 */     this.m_charSet = "";
/*  78:221 */     if (charSet.length() > 0) {
/*  79:222 */       this.m_charSet = charSet;
/*  80:    */     }
/*  81:    */   }
/*  82:    */   
/*  83:    */   public String[] getOptions()
/*  84:    */   {
/*  85:233 */     Vector<String> options = new Vector();
/*  86:235 */     if (getDebug()) {
/*  87:236 */       options.add("-D");
/*  88:    */     }
/*  89:239 */     if (getOutputFilename()) {
/*  90:240 */       options.add("-F");
/*  91:    */     }
/*  92:243 */     options.add("-dir");
/*  93:244 */     options.add(getDirectory().getAbsolutePath());
/*  94:246 */     if ((this.m_charSet != null) && (this.m_charSet.length() > 0))
/*  95:    */     {
/*  96:247 */       options.add("-charset");
/*  97:248 */       options.add(this.m_charSet);
/*  98:    */     }
/*  99:251 */     return (String[])options.toArray(new String[options.size()]);
/* 100:    */   }
/* 101:    */   
/* 102:    */   public String charSetTipText()
/* 103:    */   {
/* 104:260 */     return "The character set to use when reading text files (eg UTF-8) - leave blank to use the default character set.";
/* 105:    */   }
/* 106:    */   
/* 107:    */   public void setCharSet(String charSet)
/* 108:    */   {
/* 109:271 */     this.m_charSet = charSet;
/* 110:    */   }
/* 111:    */   
/* 112:    */   public String getCharSet()
/* 113:    */   {
/* 114:282 */     return this.m_charSet;
/* 115:    */   }
/* 116:    */   
/* 117:    */   public void setDebug(boolean value)
/* 118:    */   {
/* 119:291 */     this.m_Debug = value;
/* 120:    */   }
/* 121:    */   
/* 122:    */   public boolean getDebug()
/* 123:    */   {
/* 124:300 */     return this.m_Debug;
/* 125:    */   }
/* 126:    */   
/* 127:    */   public String debugTipText()
/* 128:    */   {
/* 129:309 */     return "Whether to print additional debug information to the console.";
/* 130:    */   }
/* 131:    */   
/* 132:    */   public void setOutputFilename(boolean value)
/* 133:    */   {
/* 134:318 */     this.m_OutputFilename = value;
/* 135:319 */     reset();
/* 136:    */   }
/* 137:    */   
/* 138:    */   public boolean getOutputFilename()
/* 139:    */   {
/* 140:328 */     return this.m_OutputFilename;
/* 141:    */   }
/* 142:    */   
/* 143:    */   public String outputFilenameTipText()
/* 144:    */   {
/* 145:337 */     return "Whether to store the filename in an additional attribute.";
/* 146:    */   }
/* 147:    */   
/* 148:    */   public String getFileDescription()
/* 149:    */   {
/* 150:346 */     return "Directories";
/* 151:    */   }
/* 152:    */   
/* 153:    */   public File getDirectory()
/* 154:    */   {
/* 155:355 */     return new File(this.m_sourceFile.getAbsolutePath());
/* 156:    */   }
/* 157:    */   
/* 158:    */   public void setDirectory(File dir)
/* 159:    */     throws IOException
/* 160:    */   {
/* 161:365 */     setSource(dir);
/* 162:    */   }
/* 163:    */   
/* 164:    */   public void reset()
/* 165:    */   {
/* 166:373 */     this.m_structure = null;
/* 167:374 */     this.m_filesByClass = null;
/* 168:375 */     this.m_lastClassDir = 0;
/* 169:376 */     setRetrieval(0);
/* 170:    */   }
/* 171:    */   
/* 172:    */   public void setSource(File dir)
/* 173:    */     throws IOException
/* 174:    */   {
/* 175:388 */     reset();
/* 176:390 */     if (dir == null) {
/* 177:391 */       throw new IOException("Source directory object is null!");
/* 178:    */     }
/* 179:394 */     this.m_sourceFile = dir;
/* 180:395 */     if ((!dir.exists()) || (!dir.isDirectory())) {
/* 181:396 */       throw new IOException("Directory '" + dir + "' not found");
/* 182:    */     }
/* 183:    */   }
/* 184:    */   
/* 185:    */   public Instances getStructure()
/* 186:    */     throws IOException
/* 187:    */   {
/* 188:409 */     if (getDirectory() == null) {
/* 189:410 */       throw new IOException("No directory/source has been specified");
/* 190:    */     }
/* 191:414 */     if (this.m_structure == null)
/* 192:    */     {
/* 193:415 */       String directoryPath = getDirectory().getAbsolutePath();
/* 194:416 */       ArrayList<Attribute> atts = new ArrayList();
/* 195:417 */       ArrayList<String> classes = new ArrayList();
/* 196:    */       
/* 197:419 */       File dir = new File(directoryPath);
/* 198:420 */       String[] subdirs = dir.list();
/* 199:422 */       for (String subdir2 : subdirs)
/* 200:    */       {
/* 201:423 */         File subdir = new File(directoryPath + File.separator + subdir2);
/* 202:424 */         if (subdir.isDirectory()) {
/* 203:425 */           classes.add(subdir2);
/* 204:    */         }
/* 205:    */       }
/* 206:429 */       atts.add(new Attribute("text", (ArrayList)null));
/* 207:430 */       if (this.m_OutputFilename) {
/* 208:431 */         atts.add(new Attribute("filename", (ArrayList)null));
/* 209:    */       }
/* 210:435 */       atts.add(new Attribute("@@class@@", classes));
/* 211:    */       
/* 212:437 */       String relName = directoryPath.replaceAll("/", "_");
/* 213:438 */       relName = relName.replaceAll("\\\\", "_").replaceAll(":", "_");
/* 214:439 */       this.m_structure = new Instances(relName, atts, 0);
/* 215:440 */       this.m_structure.setClassIndex(this.m_structure.numAttributes() - 1);
/* 216:    */     }
/* 217:443 */     return this.m_structure;
/* 218:    */   }
/* 219:    */   
/* 220:    */   public Instances getDataSet()
/* 221:    */     throws IOException
/* 222:    */   {
/* 223:456 */     if (getDirectory() == null) {
/* 224:457 */       throw new IOException("No directory/source has been specified");
/* 225:    */     }
/* 226:460 */     String directoryPath = getDirectory().getAbsolutePath();
/* 227:461 */     ArrayList<String> classes = new ArrayList();
/* 228:462 */     Enumeration<Object> enm = getStructure().classAttribute().enumerateValues();
/* 229:463 */     while (enm.hasMoreElements())
/* 230:    */     {
/* 231:464 */       Object oo = enm.nextElement();
/* 232:465 */       if ((oo instanceof SerializedObject)) {
/* 233:466 */         classes.add(((SerializedObject)oo).getObject().toString());
/* 234:    */       } else {
/* 235:468 */         classes.add(oo.toString());
/* 236:    */       }
/* 237:    */     }
/* 238:472 */     Instances data = getStructure();
/* 239:473 */     int fileCount = 0;
/* 240:474 */     for (int k = 0; k < classes.size(); k++)
/* 241:    */     {
/* 242:475 */       String subdirPath = (String)classes.get(k);
/* 243:476 */       File subdir = new File(directoryPath + File.separator + subdirPath);
/* 244:477 */       String[] files = subdir.list();
/* 245:478 */       for (String file : files) {
/* 246:    */         try
/* 247:    */         {
/* 248:480 */           fileCount++;
/* 249:481 */           if (getDebug()) {
/* 250:482 */             System.err.println("processing " + fileCount + " : " + subdirPath + " : " + file);
/* 251:    */           }
/* 252:486 */           double[] newInst = null;
/* 253:487 */           if (this.m_OutputFilename) {
/* 254:488 */             newInst = new double[3];
/* 255:    */           } else {
/* 256:490 */             newInst = new double[2];
/* 257:    */           }
/* 258:492 */           File txt = new File(directoryPath + File.separator + subdirPath + File.separator + file);
/* 259:    */           BufferedReader is;
/* 260:    */           BufferedReader is;
/* 261:496 */           if ((this.m_charSet == null) || (this.m_charSet.length() == 0)) {
/* 262:497 */             is = new BufferedReader(new InputStreamReader(new FileInputStream(txt)));
/* 263:    */           } else {
/* 264:501 */             is = new BufferedReader(new InputStreamReader(new FileInputStream(txt), this.m_charSet));
/* 265:    */           }
/* 266:505 */           StringBuffer txtStr = new StringBuffer();
/* 267:    */           int c;
/* 268:507 */           while ((c = is.read()) != -1) {
/* 269:508 */             txtStr.append((char)c);
/* 270:    */           }
/* 271:511 */           newInst[0] = data.attribute(0).addStringValue(txtStr.toString());
/* 272:512 */           if (this.m_OutputFilename) {
/* 273:513 */             newInst[1] = data.attribute(1).addStringValue(subdirPath + File.separator + file);
/* 274:    */           }
/* 275:517 */           newInst[data.classIndex()] = k;
/* 276:518 */           data.add(new DenseInstance(1.0D, newInst));
/* 277:519 */           is.close();
/* 278:    */         }
/* 279:    */         catch (Exception e)
/* 280:    */         {
/* 281:521 */           System.err.println("failed to convert file: " + directoryPath + File.separator + subdirPath + File.separator + file);
/* 282:    */         }
/* 283:    */       }
/* 284:    */     }
/* 285:527 */     return data;
/* 286:    */   }
/* 287:    */   
/* 288:531 */   protected int m_lastClassDir = 0;
/* 289:    */   
/* 290:    */   public Instance getNextInstance(Instances structure)
/* 291:    */     throws IOException
/* 292:    */   {
/* 293:545 */     String directoryPath = getDirectory().getAbsolutePath();
/* 294:546 */     Attribute classAtt = structure.classAttribute();
/* 295:547 */     if (this.m_filesByClass == null)
/* 296:    */     {
/* 297:548 */       this.m_filesByClass = new ArrayList();
/* 298:549 */       for (int i = 0; i < classAtt.numValues(); i++)
/* 299:    */       {
/* 300:550 */         File classDir = new File(directoryPath + File.separator + classAtt.value(i));
/* 301:    */         
/* 302:552 */         String[] files = classDir.list();
/* 303:553 */         LinkedList<String> classDocs = new LinkedList();
/* 304:554 */         for (String cd : files)
/* 305:    */         {
/* 306:555 */           File txt = new File(directoryPath + File.separator + classAtt.value(i) + File.separator + cd);
/* 307:558 */           if (txt.isFile()) {
/* 308:559 */             classDocs.add(cd);
/* 309:    */           }
/* 310:    */         }
/* 311:562 */         this.m_filesByClass.add(classDocs);
/* 312:    */       }
/* 313:    */     }
/* 314:567 */     int count = 0;
/* 315:568 */     LinkedList<String> classContents = (LinkedList)this.m_filesByClass.get(this.m_lastClassDir);
/* 316:569 */     boolean found = classContents.size() > 0;
/* 317:570 */     while (classContents.size() == 0)
/* 318:    */     {
/* 319:571 */       this.m_lastClassDir += 1;
/* 320:572 */       count++;
/* 321:573 */       if (this.m_lastClassDir == structure.classAttribute().numValues()) {
/* 322:574 */         this.m_lastClassDir = 0;
/* 323:    */       }
/* 324:576 */       classContents = (LinkedList)this.m_filesByClass.get(this.m_lastClassDir);
/* 325:577 */       if (classContents.size() > 0) {
/* 326:578 */         found = true;
/* 327:581 */       } else if (count == structure.classAttribute().numValues()) {
/* 328:    */         break;
/* 329:    */       }
/* 330:    */     }
/* 331:586 */     if (found)
/* 332:    */     {
/* 333:587 */       String nextDoc = (String)classContents.poll();
/* 334:588 */       File txt = new File(directoryPath + File.separator + classAtt.value(this.m_lastClassDir) + File.separator + nextDoc);
/* 335:    */       BufferedReader is;
/* 336:    */       BufferedReader is;
/* 337:593 */       if ((this.m_charSet == null) || (this.m_charSet.length() == 0)) {
/* 338:594 */         is = new BufferedReader(new InputStreamReader(new FileInputStream(txt)));
/* 339:    */       } else {
/* 340:597 */         is = new BufferedReader(new InputStreamReader(new FileInputStream(txt), this.m_charSet));
/* 341:    */       }
/* 342:601 */       StringBuffer txtStr = new StringBuffer();
/* 343:    */       int c;
/* 344:603 */       while ((c = is.read()) != -1) {
/* 345:604 */         txtStr.append((char)c);
/* 346:    */       }
/* 347:607 */       double[] newInst = null;
/* 348:608 */       if (this.m_OutputFilename) {
/* 349:609 */         newInst = new double[3];
/* 350:    */       } else {
/* 351:611 */         newInst = new double[2];
/* 352:    */       }
/* 353:614 */       newInst[0] = 0.0D;
/* 354:615 */       structure.attribute(0).setStringValue(txtStr.toString());
/* 355:617 */       if (this.m_OutputFilename)
/* 356:    */       {
/* 357:618 */         newInst[1] = 0.0D;
/* 358:619 */         structure.attribute(1).setStringValue(txt.getAbsolutePath());
/* 359:    */       }
/* 360:621 */       newInst[structure.classIndex()] = this.m_lastClassDir;
/* 361:622 */       Instance inst = new DenseInstance(1.0D, newInst);
/* 362:623 */       inst.setDataset(structure);
/* 363:624 */       is.close();
/* 364:    */       
/* 365:626 */       this.m_lastClassDir += 1;
/* 366:627 */       if (this.m_lastClassDir == structure.classAttribute().numValues()) {
/* 367:628 */         this.m_lastClassDir = 0;
/* 368:    */       }
/* 369:631 */       return inst;
/* 370:    */     }
/* 371:633 */     return null;
/* 372:    */   }
/* 373:    */   
/* 374:    */   public String getRevision()
/* 375:    */   {
/* 376:644 */     return RevisionUtils.extract("$Revision: 12184 $");
/* 377:    */   }
/* 378:    */   
/* 379:    */   public static void main(String[] args)
/* 380:    */   {
/* 381:653 */     TextDirectoryLoader loader = new TextDirectoryLoader();
/* 382:654 */     loader.run(loader, args);
/* 383:    */   }
/* 384:    */   
/* 385:    */   public void preExecution()
/* 386:    */     throws Exception
/* 387:    */   {}
/* 388:    */   
/* 389:    */   public void postExecution()
/* 390:    */     throws Exception
/* 391:    */   {}
/* 392:    */   
/* 393:    */   public void run(Object toRun, String[] args)
/* 394:    */     throws IllegalArgumentException
/* 395:    */   {
/* 396:679 */     if (!(toRun instanceof TextDirectoryLoader)) {
/* 397:680 */       throw new IllegalArgumentException("Object to execute is not a TextDirectoryLoader!");
/* 398:    */     }
/* 399:684 */     TextDirectoryLoader loader = (TextDirectoryLoader)toRun;
/* 400:685 */     if (args.length > 0)
/* 401:    */     {
/* 402:    */       try
/* 403:    */       {
/* 404:687 */         loader.setOptions(args);
/* 405:    */         
/* 406:689 */         Instances structure = loader.getStructure();
/* 407:690 */         System.out.println(structure);
/* 408:    */         Instance temp;
/* 409:    */         do
/* 410:    */         {
/* 411:693 */           temp = loader.getNextInstance(structure);
/* 412:694 */           if (temp != null) {
/* 413:695 */             System.out.println(temp);
/* 414:    */           }
/* 415:697 */         } while (temp != null);
/* 416:    */       }
/* 417:    */       catch (Exception e)
/* 418:    */       {
/* 419:699 */         e.printStackTrace();
/* 420:    */       }
/* 421:    */     }
/* 422:    */     else
/* 423:    */     {
/* 424:702 */       System.err.println("\nUsage:\n\tTextDirectoryLoader [options]\n\nOptions:\n");
/* 425:    */       
/* 426:    */ 
/* 427:705 */       Enumeration<Option> enm = new TextDirectoryLoader().listOptions();
/* 428:707 */       while (enm.hasMoreElements())
/* 429:    */       {
/* 430:708 */         Option option = (Option)enm.nextElement();
/* 431:709 */         System.err.println(option.synopsis());
/* 432:710 */         System.err.println(option.description());
/* 433:    */       }
/* 434:713 */       System.err.println();
/* 435:    */     }
/* 436:    */   }
/* 437:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.TextDirectoryLoader
 * JD-Core Version:    0.7.0.1
 */