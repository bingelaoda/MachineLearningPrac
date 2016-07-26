/*   1:    */ package weka.datagenerators;
/*   2:    */ 
/*   3:    */ import java.io.FileOutputStream;
/*   4:    */ import java.io.OutputStreamWriter;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.io.PrintWriter;
/*   7:    */ import java.io.Serializable;
/*   8:    */ import java.util.Collections;
/*   9:    */ import java.util.Enumeration;
/*  10:    */ import java.util.HashSet;
/*  11:    */ import java.util.Hashtable;
/*  12:    */ import java.util.Random;
/*  13:    */ import java.util.Vector;
/*  14:    */ import weka.core.Instance;
/*  15:    */ import weka.core.Instances;
/*  16:    */ import weka.core.Option;
/*  17:    */ import weka.core.OptionHandler;
/*  18:    */ import weka.core.Randomizable;
/*  19:    */ import weka.core.RevisionHandler;
/*  20:    */ import weka.core.Utils;
/*  21:    */ 
/*  22:    */ public abstract class DataGenerator
/*  23:    */   implements OptionHandler, Randomizable, Serializable, RevisionHandler
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = -3698585946221802578L;
/*  26: 56 */   protected boolean m_Debug = false;
/*  27: 59 */   protected Instances m_DatasetFormat = null;
/*  28: 62 */   protected String m_RelationName = "";
/*  29:    */   protected int m_NumExamplesAct;
/*  30: 71 */   protected transient PrintWriter m_DefaultOutput = new PrintWriter(new OutputStreamWriter(System.out));
/*  31: 75 */   protected transient PrintWriter m_Output = this.m_DefaultOutput;
/*  32:    */   protected int m_Seed;
/*  33: 81 */   protected Random m_Random = null;
/*  34: 84 */   protected boolean m_CreatingRelationName = false;
/*  35: 94 */   protected static HashSet<String> m_OptionBlacklist = new HashSet();
/*  36:    */   
/*  37:    */   public DataGenerator()
/*  38:    */   {
/*  39:105 */     clearBlacklist();
/*  40:    */     
/*  41:107 */     setNumExamplesAct(defaultNumExamplesAct());
/*  42:108 */     setSeed(defaultSeed());
/*  43:    */   }
/*  44:    */   
/*  45:    */   public Enumeration<Option> listOptions()
/*  46:    */   {
/*  47:119 */     Vector<Option> result = new Vector();
/*  48:    */     
/*  49:121 */     result.addElement(new Option("\tPrints this help.", "h", 1, "-h"));
/*  50:    */     
/*  51:123 */     result.addElement(new Option("\tThe name of the output file, otherwise the generated data is\n\tprinted to stdout.", "o", 1, "-o <file>"));
/*  52:    */     
/*  53:    */ 
/*  54:    */ 
/*  55:127 */     result.addElement(new Option("\tThe name of the relation.", "r", 1, "-r <name>"));
/*  56:    */     
/*  57:    */ 
/*  58:130 */     result.addElement(new Option("\tWhether to print debug informations.", "d", 0, "-d"));
/*  59:    */     
/*  60:    */ 
/*  61:133 */     result.addElement(new Option("\tThe seed for random function (default " + defaultSeed() + ")", "S", 1, "-S"));
/*  62:    */     
/*  63:    */ 
/*  64:136 */     return result.elements();
/*  65:    */   }
/*  66:    */   
/*  67:    */   public Vector<Option> enumToVector(Enumeration<Option> enu)
/*  68:    */   {
/*  69:144 */     Vector<Option> options = new Vector();
/*  70:145 */     options.addAll(Collections.list(enu));
/*  71:146 */     return options;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void setOptions(String[] options)
/*  75:    */     throws Exception
/*  76:    */   {
/*  77:164 */     options = removeBlacklist(options);
/*  78:    */     
/*  79:166 */     String tmpStr = Utils.getOption('r', options);
/*  80:167 */     if (tmpStr.length() != 0) {
/*  81:168 */       setRelationName(Utils.unquote(tmpStr));
/*  82:    */     } else {
/*  83:170 */       setRelationName("");
/*  84:    */     }
/*  85:173 */     tmpStr = Utils.getOption('o', options);
/*  86:174 */     if (tmpStr.length() != 0) {
/*  87:175 */       setOutput(new PrintWriter(new FileOutputStream(tmpStr)));
/*  88:176 */     } else if (getOutput() == null) {
/*  89:177 */       throw new Exception("No Output defined!");
/*  90:    */     }
/*  91:180 */     setDebug(Utils.getFlag('d', options));
/*  92:    */     
/*  93:182 */     tmpStr = Utils.getOption('S', options);
/*  94:183 */     if (tmpStr.length() != 0) {
/*  95:184 */       setSeed(Integer.parseInt(tmpStr));
/*  96:    */     } else {
/*  97:186 */       setSeed(defaultSeed());
/*  98:    */     }
/*  99:    */   }
/* 100:    */   
/* 101:    */   public String[] getOptions()
/* 102:    */   {
/* 103:200 */     Vector<String> result = new Vector();
/* 104:203 */     if (!this.m_CreatingRelationName)
/* 105:    */     {
/* 106:204 */       result.add("-r");
/* 107:205 */       result.add(Utils.quote(getRelationNameToUse()));
/* 108:    */     }
/* 109:208 */     if (getDebug()) {
/* 110:209 */       result.add("-d");
/* 111:    */     }
/* 112:212 */     result.add("-S");
/* 113:213 */     result.add("" + getSeed());
/* 114:    */     
/* 115:215 */     return (String[])result.toArray(new String[result.size()]);
/* 116:    */   }
/* 117:    */   
/* 118:    */   public Instances defineDataFormat()
/* 119:    */     throws Exception
/* 120:    */   {
/* 121:228 */     if (getRelationName().length() == 0) {
/* 122:229 */       setRelationName(defaultRelationName());
/* 123:    */     }
/* 124:232 */     return this.m_DatasetFormat;
/* 125:    */   }
/* 126:    */   
/* 127:    */   public abstract Instance generateExample()
/* 128:    */     throws Exception;
/* 129:    */   
/* 130:    */   public abstract Instances generateExamples()
/* 131:    */     throws Exception;
/* 132:    */   
/* 133:    */   public abstract String generateStart()
/* 134:    */     throws Exception;
/* 135:    */   
/* 136:    */   public abstract String generateFinished()
/* 137:    */     throws Exception;
/* 138:    */   
/* 139:    */   public abstract boolean getSingleModeFlag()
/* 140:    */     throws Exception;
/* 141:    */   
/* 142:    */   public void setDebug(boolean debug)
/* 143:    */   {
/* 144:289 */     this.m_Debug = debug;
/* 145:    */   }
/* 146:    */   
/* 147:    */   public boolean getDebug()
/* 148:    */   {
/* 149:298 */     return this.m_Debug;
/* 150:    */   }
/* 151:    */   
/* 152:    */   public String debugTipText()
/* 153:    */   {
/* 154:308 */     return "Whether the generator is run in debug mode or not.";
/* 155:    */   }
/* 156:    */   
/* 157:    */   public void setRelationName(String relationName)
/* 158:    */   {
/* 159:317 */     this.m_RelationName = relationName;
/* 160:    */   }
/* 161:    */   
/* 162:    */   protected String defaultRelationName()
/* 163:    */   {
/* 164:331 */     this.m_CreatingRelationName = true;
/* 165:    */     
/* 166:333 */     StringBuffer result = new StringBuffer(getClass().getName());
/* 167:    */     
/* 168:335 */     String[] options = getOptions();
/* 169:336 */     for (int i = 0; i < options.length; i++)
/* 170:    */     {
/* 171:337 */       String option = options[i].trim();
/* 172:338 */       if (i > 0) {
/* 173:339 */         result.append("_");
/* 174:    */       }
/* 175:341 */       result.append(option.replaceAll(" ", "_"));
/* 176:    */     }
/* 177:344 */     this.m_CreatingRelationName = false;
/* 178:    */     
/* 179:346 */     return result.toString();
/* 180:    */   }
/* 181:    */   
/* 182:    */   protected String getRelationNameToUse()
/* 183:    */   {
/* 184:361 */     String result = getRelationName();
/* 185:362 */     if (result.length() == 0) {
/* 186:363 */       result = defaultRelationName();
/* 187:    */     }
/* 188:366 */     return result;
/* 189:    */   }
/* 190:    */   
/* 191:    */   public String getRelationName()
/* 192:    */   {
/* 193:375 */     return this.m_RelationName;
/* 194:    */   }
/* 195:    */   
/* 196:    */   public String relationNameTipText()
/* 197:    */   {
/* 198:385 */     return "The relation name of the generated data (if empty, a generic one will be supplied).";
/* 199:    */   }
/* 200:    */   
/* 201:    */   protected int defaultNumExamplesAct()
/* 202:    */   {
/* 203:394 */     return 0;
/* 204:    */   }
/* 205:    */   
/* 206:    */   protected void setNumExamplesAct(int numExamplesAct)
/* 207:    */   {
/* 208:403 */     this.m_NumExamplesAct = numExamplesAct;
/* 209:    */   }
/* 210:    */   
/* 211:    */   public int getNumExamplesAct()
/* 212:    */   {
/* 213:412 */     return this.m_NumExamplesAct;
/* 214:    */   }
/* 215:    */   
/* 216:    */   protected String numExamplesActTipText()
/* 217:    */   {
/* 218:422 */     return "The actual number of examples to generate.";
/* 219:    */   }
/* 220:    */   
/* 221:    */   public void setOutput(PrintWriter newOutput)
/* 222:    */   {
/* 223:431 */     this.m_Output = newOutput;
/* 224:432 */     this.m_DefaultOutput = null;
/* 225:    */   }
/* 226:    */   
/* 227:    */   public PrintWriter getOutput()
/* 228:    */   {
/* 229:441 */     return this.m_Output;
/* 230:    */   }
/* 231:    */   
/* 232:    */   public PrintWriter defaultOutput()
/* 233:    */   {
/* 234:451 */     return this.m_DefaultOutput;
/* 235:    */   }
/* 236:    */   
/* 237:    */   public String outputTipText()
/* 238:    */   {
/* 239:461 */     return "The output writer to use for printing the generated data.";
/* 240:    */   }
/* 241:    */   
/* 242:    */   public void setDatasetFormat(Instances newFormat)
/* 243:    */   {
/* 244:470 */     this.m_DatasetFormat = new Instances(newFormat, 0);
/* 245:    */   }
/* 246:    */   
/* 247:    */   public Instances getDatasetFormat()
/* 248:    */   {
/* 249:479 */     if (this.m_DatasetFormat != null) {
/* 250:480 */       return new Instances(this.m_DatasetFormat, 0);
/* 251:    */     }
/* 252:482 */     return null;
/* 253:    */   }
/* 254:    */   
/* 255:    */   public String formatTipText()
/* 256:    */   {
/* 257:493 */     return "The data format to use.";
/* 258:    */   }
/* 259:    */   
/* 260:    */   protected int defaultSeed()
/* 261:    */   {
/* 262:502 */     return 1;
/* 263:    */   }
/* 264:    */   
/* 265:    */   public int getSeed()
/* 266:    */   {
/* 267:512 */     return this.m_Seed;
/* 268:    */   }
/* 269:    */   
/* 270:    */   public void setSeed(int newSeed)
/* 271:    */   {
/* 272:522 */     this.m_Seed = newSeed;
/* 273:523 */     this.m_Random = new Random(newSeed);
/* 274:    */   }
/* 275:    */   
/* 276:    */   public String seedTipText()
/* 277:    */   {
/* 278:533 */     return "The seed value for the random number generator.";
/* 279:    */   }
/* 280:    */   
/* 281:    */   public Random getRandom()
/* 282:    */   {
/* 283:542 */     if (this.m_Random == null) {
/* 284:543 */       this.m_Random = new Random(getSeed());
/* 285:    */     }
/* 286:546 */     return this.m_Random;
/* 287:    */   }
/* 288:    */   
/* 289:    */   public void setRandom(Random newRandom)
/* 290:    */   {
/* 291:555 */     this.m_Random = newRandom;
/* 292:    */   }
/* 293:    */   
/* 294:    */   public String randomTipText()
/* 295:    */   {
/* 296:565 */     return "The random number generator to use.";
/* 297:    */   }
/* 298:    */   
/* 299:    */   protected String toStringFormat()
/* 300:    */   {
/* 301:574 */     if (this.m_DatasetFormat == null) {
/* 302:575 */       return "";
/* 303:    */     }
/* 304:577 */     return this.m_DatasetFormat.toString();
/* 305:    */   }
/* 306:    */   
/* 307:    */   protected static void clearBlacklist()
/* 308:    */   {
/* 309:584 */     m_OptionBlacklist.clear();
/* 310:    */   }
/* 311:    */   
/* 312:    */   protected static void addToBlacklist(String option)
/* 313:    */   {
/* 314:595 */     m_OptionBlacklist.add(option);
/* 315:    */   }
/* 316:    */   
/* 317:    */   protected static boolean isOnBlacklist(String option)
/* 318:    */   {
/* 319:607 */     return m_OptionBlacklist.contains(option);
/* 320:    */   }
/* 321:    */   
/* 322:    */   protected String[] removeBlacklist(String[] options)
/* 323:    */   {
/* 324:622 */     Enumeration<Option> enm = listOptions();
/* 325:623 */     Hashtable<String, Option> pool = new Hashtable();
/* 326:624 */     while (enm.hasMoreElements())
/* 327:    */     {
/* 328:625 */       Option option = (Option)enm.nextElement();
/* 329:626 */       if (isOnBlacklist(option.name())) {
/* 330:627 */         pool.put(option.name(), option);
/* 331:    */       }
/* 332:    */     }
/* 333:632 */     Enumeration<String> enm2 = pool.keys();
/* 334:633 */     while (enm2.hasMoreElements())
/* 335:    */     {
/* 336:634 */       Option option = (Option)pool.get(enm2.nextElement());
/* 337:    */       try
/* 338:    */       {
/* 339:636 */         if (option.numArguments() == 0) {
/* 340:637 */           Utils.getFlag(option.name(), options);
/* 341:    */         } else {
/* 342:639 */           Utils.getOption(option.name(), options);
/* 343:    */         }
/* 344:    */       }
/* 345:    */       catch (Exception e)
/* 346:    */       {
/* 347:642 */         e.printStackTrace();
/* 348:    */       }
/* 349:    */     }
/* 350:646 */     return options;
/* 351:    */   }
/* 352:    */   
/* 353:    */   protected static String makeOptionString(DataGenerator generator)
/* 354:    */   {
/* 355:660 */     StringBuffer result = new StringBuffer();
/* 356:661 */     result.append("\nData Generator options:\n\n");
/* 357:    */     
/* 358:663 */     Enumeration<Option> enm = generator.listOptions();
/* 359:664 */     while (enm.hasMoreElements())
/* 360:    */     {
/* 361:665 */       Option option = (Option)enm.nextElement();
/* 362:667 */       if (!isOnBlacklist(option.name())) {
/* 363:670 */         result.append(option.synopsis() + "\n" + option.description() + "\n");
/* 364:    */       }
/* 365:    */     }
/* 366:673 */     return result.toString();
/* 367:    */   }
/* 368:    */   
/* 369:    */   public static void makeData(DataGenerator generator, String[] options)
/* 370:    */     throws Exception
/* 371:    */   {
/* 372:691 */     boolean printhelp = Utils.getFlag('h', options);
/* 373:    */     int i;
/* 374:694 */     if (!printhelp) {
/* 375:    */       try
/* 376:    */       {
/* 377:696 */         options = generator.removeBlacklist(options);
/* 378:697 */         generator.setOptions(options);
/* 379:    */         
/* 380:    */ 
/* 381:700 */         Vector<String> unknown = new Vector();
/* 382:701 */         for (i = 0; i < options.length; i++) {
/* 383:702 */           if (options[i].length() != 0) {
/* 384:703 */             unknown.add(options[i]);
/* 385:    */           }
/* 386:    */         }
/* 387:706 */         if (unknown.size() > 0)
/* 388:    */         {
/* 389:707 */           System.out.print("Unknown options:");
/* 390:708 */           for (i = 0; i < unknown.size(); i++) {
/* 391:709 */             System.out.print(" " + (String)unknown.get(i));
/* 392:    */           }
/* 393:711 */           System.out.println();
/* 394:    */         }
/* 395:    */       }
/* 396:    */       catch (Exception e)
/* 397:    */       {
/* 398:714 */         e.printStackTrace();
/* 399:715 */         printhelp = true;
/* 400:    */       }
/* 401:    */     }
/* 402:719 */     if (printhelp)
/* 403:    */     {
/* 404:720 */       System.out.println(makeOptionString(generator));
/* 405:721 */       return;
/* 406:    */     }
/* 407:726 */     generator.setDatasetFormat(generator.defineDataFormat());
/* 408:    */     
/* 409:    */ 
/* 410:729 */     PrintWriter output = generator.getOutput();
/* 411:    */     
/* 412:    */ 
/* 413:732 */     output.println("%");
/* 414:733 */     output.println("% Commandline");
/* 415:734 */     output.println("%");
/* 416:735 */     output.println("% " + generator.getClass().getName() + " " + Utils.joinOptions(generator.getOptions()));
/* 417:    */     
/* 418:737 */     output.println("%");
/* 419:    */     
/* 420:    */ 
/* 421:740 */     String commentAtStart = generator.generateStart();
/* 422:742 */     if (commentAtStart.length() > 0)
/* 423:    */     {
/* 424:743 */       output.println("%");
/* 425:744 */       output.println("% Prologue");
/* 426:745 */       output.println("%");
/* 427:746 */       output.println(commentAtStart.trim());
/* 428:747 */       output.println("%");
/* 429:    */     }
/* 430:751 */     boolean singleMode = generator.getSingleModeFlag();
/* 431:754 */     if (singleMode)
/* 432:    */     {
/* 433:756 */       output.println(generator.toStringFormat());
/* 434:757 */       for (i = 0; i < generator.getNumExamplesAct(); i++)
/* 435:    */       {
/* 436:759 */         Instance inst = generator.generateExample();
/* 437:760 */         output.println(inst);
/* 438:    */       }
/* 439:    */     }
/* 440:763 */     Instances dataset = generator.generateExamples();
/* 441:    */     
/* 442:765 */     output.println(dataset);
/* 443:    */     
/* 444:    */ 
/* 445:768 */     String commentAtEnd = generator.generateFinished();
/* 446:770 */     if (commentAtEnd.length() > 0)
/* 447:    */     {
/* 448:771 */       output.println("%");
/* 449:772 */       output.println("% Epilogue");
/* 450:773 */       output.println("%");
/* 451:774 */       output.println(commentAtEnd.trim());
/* 452:775 */       output.println("%");
/* 453:    */     }
/* 454:778 */     output.flush();
/* 455:780 */     if (generator.getOutput() != generator.defaultOutput()) {
/* 456:781 */       output.close();
/* 457:    */     }
/* 458:    */   }
/* 459:    */   
/* 460:    */   public static void runDataGenerator(DataGenerator datagenerator, String[] options)
/* 461:    */   {
/* 462:    */     try
/* 463:    */     {
/* 464:794 */       makeData(datagenerator, options);
/* 465:    */     }
/* 466:    */     catch (Exception e)
/* 467:    */     {
/* 468:796 */       if ((e.getMessage() != null) && (e.getMessage().indexOf("Data Generator options") == -1)) {
/* 469:798 */         e.printStackTrace();
/* 470:    */       } else {
/* 471:800 */         System.err.println(e.getMessage());
/* 472:    */       }
/* 473:    */     }
/* 474:    */   }
/* 475:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.datagenerators.DataGenerator
 * JD-Core Version:    0.7.0.1
 */