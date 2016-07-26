/*   1:    */ package weka.core.converters;
/*   2:    */ 
/*   3:    */ import java.io.BufferedWriter;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.FileOutputStream;
/*   6:    */ import java.io.IOException;
/*   7:    */ import java.io.OutputStream;
/*   8:    */ import java.io.OutputStreamWriter;
/*   9:    */ import java.io.PrintStream;
/*  10:    */ import java.util.Enumeration;
/*  11:    */ import java.util.Vector;
/*  12:    */ import weka.core.Environment;
/*  13:    */ import weka.core.EnvironmentHandler;
/*  14:    */ import weka.core.Instances;
/*  15:    */ import weka.core.Option;
/*  16:    */ import weka.core.OptionHandler;
/*  17:    */ import weka.core.Utils;
/*  18:    */ 
/*  19:    */ public abstract class AbstractFileSaver
/*  20:    */   extends AbstractSaver
/*  21:    */   implements OptionHandler, FileSourcedConverter, EnvironmentHandler
/*  22:    */ {
/*  23:    */   private static final long serialVersionUID = 2399441762235754491L;
/*  24:    */   private File m_outputFile;
/*  25:    */   private transient BufferedWriter m_writer;
/*  26:    */   private String FILE_EXTENSION;
/*  27: 74 */   private final String FILE_EXTENSION_COMPRESSED = ".gz";
/*  28:    */   private String m_prefix;
/*  29:    */   private String m_dir;
/*  30:    */   protected int m_incrementalCounter;
/*  31: 89 */   protected boolean m_useRelativePath = false;
/*  32:    */   protected transient Environment m_env;
/*  33:    */   
/*  34:    */   public void resetOptions()
/*  35:    */   {
/*  36:101 */     super.resetOptions();
/*  37:102 */     this.m_outputFile = null;
/*  38:103 */     this.m_writer = null;
/*  39:104 */     this.m_prefix = "";
/*  40:105 */     this.m_dir = "";
/*  41:106 */     this.m_incrementalCounter = 0;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public BufferedWriter getWriter()
/*  45:    */   {
/*  46:116 */     return this.m_writer;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void resetWriter()
/*  50:    */   {
/*  51:122 */     this.m_writer = null;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public String getFileExtension()
/*  55:    */   {
/*  56:133 */     return this.FILE_EXTENSION;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public String[] getFileExtensions()
/*  60:    */   {
/*  61:143 */     return new String[] { getFileExtension() };
/*  62:    */   }
/*  63:    */   
/*  64:    */   protected void setFileExtension(String ext)
/*  65:    */   {
/*  66:153 */     this.FILE_EXTENSION = ext;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public File retrieveFile()
/*  70:    */   {
/*  71:164 */     return this.m_outputFile;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void setFile(File outputFile)
/*  75:    */     throws IOException
/*  76:    */   {
/*  77:176 */     this.m_outputFile = outputFile;
/*  78:177 */     setDestination(outputFile);
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void setFilePrefix(String prefix)
/*  82:    */   {
/*  83:189 */     this.m_prefix = prefix;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public String filePrefix()
/*  87:    */   {
/*  88:200 */     return this.m_prefix;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public void setDir(String dir)
/*  92:    */   {
/*  93:211 */     this.m_dir = dir;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public String retrieveDir()
/*  97:    */   {
/*  98:222 */     return this.m_dir;
/*  99:    */   }
/* 100:    */   
/* 101:    */   public void setEnvironment(Environment env)
/* 102:    */   {
/* 103:232 */     this.m_env = env;
/* 104:233 */     if (this.m_outputFile != null) {
/* 105:    */       try
/* 106:    */       {
/* 107:236 */         setFile(this.m_outputFile);
/* 108:    */       }
/* 109:    */       catch (IOException ex) {}
/* 110:    */     }
/* 111:    */   }
/* 112:    */   
/* 113:    */   public Enumeration<Option> listOptions()
/* 114:    */   {
/* 115:251 */     Vector<Option> newVector = Option.listOptionsForClassHierarchy(getClass(), AbstractFileSaver.class);
/* 116:    */     
/* 117:    */ 
/* 118:    */ 
/* 119:    */ 
/* 120:256 */     newVector.addElement(new Option("\tThe input file", "i", 1, "-i <the input file>"));
/* 121:    */     
/* 122:    */ 
/* 123:259 */     newVector.addElement(new Option("\tThe output file", "o", 1, "-o <the output file>"));
/* 124:    */     
/* 125:    */ 
/* 126:262 */     return newVector.elements();
/* 127:    */   }
/* 128:    */   
/* 129:    */   public void setOptions(String[] options)
/* 130:    */     throws Exception
/* 131:    */   {
/* 132:285 */     Option.setOptionsForHierarchy(options, this, AbstractFileSaver.class);
/* 133:286 */     String outputString = Utils.getOption('o', options);
/* 134:287 */     String inputString = Utils.getOption('i', options);
/* 135:    */     
/* 136:289 */     ArffLoader loader = new ArffLoader();
/* 137:    */     
/* 138:291 */     resetOptions();
/* 139:293 */     if (inputString.length() != 0) {
/* 140:    */       try
/* 141:    */       {
/* 142:295 */         File input = new File(inputString);
/* 143:296 */         loader.setFile(input);
/* 144:297 */         setInstances(loader.getDataSet());
/* 145:    */       }
/* 146:    */       catch (Exception ex)
/* 147:    */       {
/* 148:299 */         ex.printStackTrace();
/* 149:300 */         throw new IOException("No data set loaded. Data set has to be in ARFF format.");
/* 150:    */       }
/* 151:    */     }
/* 152:304 */     if (outputString.length() != 0)
/* 153:    */     {
/* 154:305 */       boolean validExt = false;
/* 155:306 */       for (String ext : getFileExtensions()) {
/* 156:307 */         if (outputString.endsWith(ext))
/* 157:    */         {
/* 158:308 */           validExt = true;
/* 159:309 */           break;
/* 160:    */         }
/* 161:    */       }
/* 162:313 */       if (!validExt) {
/* 163:314 */         if (outputString.lastIndexOf('.') != -1) {
/* 164:315 */           outputString = outputString.substring(0, outputString.lastIndexOf('.')) + this.FILE_EXTENSION;
/* 165:    */         } else {
/* 166:319 */           outputString = outputString + this.FILE_EXTENSION;
/* 167:    */         }
/* 168:    */       }
/* 169:    */       try
/* 170:    */       {
/* 171:323 */         File output = new File(outputString);
/* 172:324 */         setFile(output);
/* 173:    */       }
/* 174:    */       catch (Exception ex)
/* 175:    */       {
/* 176:326 */         throw new IOException("Cannot create output file (Reason: " + ex.toString() + "). Standard out is used.");
/* 177:    */       }
/* 178:    */     }
/* 179:    */   }
/* 180:    */   
/* 181:    */   public String[] getOptions()
/* 182:    */   {
/* 183:341 */     Vector<String> result = new Vector();
/* 184:342 */     for (String s : Option.getOptionsForHierarchy(this, AbstractFileSaver.class)) {
/* 185:344 */       result.add(s);
/* 186:    */     }
/* 187:347 */     if (this.m_outputFile != null)
/* 188:    */     {
/* 189:348 */       result.add("-o");
/* 190:349 */       result.add("" + this.m_outputFile);
/* 191:    */     }
/* 192:352 */     if (getInstances() != null)
/* 193:    */     {
/* 194:353 */       result.add("-i");
/* 195:354 */       result.add("" + getInstances().relationName());
/* 196:    */     }
/* 197:357 */     return (String[])result.toArray(new String[result.size()]);
/* 198:    */   }
/* 199:    */   
/* 200:    */   public void cancel()
/* 201:    */   {
/* 202:364 */     if (getWriteMode() == 2)
/* 203:    */     {
/* 204:365 */       if ((this.m_outputFile != null) && (this.m_outputFile.exists()) && 
/* 205:366 */         (this.m_outputFile.delete())) {
/* 206:367 */         System.out.println("File deleted.");
/* 207:    */       }
/* 208:370 */       resetOptions();
/* 209:    */     }
/* 210:    */   }
/* 211:    */   
/* 212:    */   public void setDestination(File file)
/* 213:    */     throws IOException
/* 214:    */   {
/* 215:383 */     boolean success = false;
/* 216:384 */     String tempOut = file.getPath();
/* 217:    */     try
/* 218:    */     {
/* 219:386 */       if (this.m_env == null) {
/* 220:387 */         this.m_env = Environment.getSystemWide();
/* 221:    */       }
/* 222:389 */       tempOut = this.m_env.substitute(tempOut);
/* 223:    */     }
/* 224:    */     catch (Exception ex) {}
/* 225:394 */     file = new File(tempOut);
/* 226:395 */     String out = file.getAbsolutePath();
/* 227:396 */     if (this.m_outputFile != null) {
/* 228:    */       try
/* 229:    */       {
/* 230:398 */         if ((file.exists()) && 
/* 231:399 */           (!file.delete())) {
/* 232:400 */           throw new IOException("File already exists.");
/* 233:    */         }
/* 234:403 */         if (out.lastIndexOf(File.separatorChar) == -1)
/* 235:    */         {
/* 236:404 */           success = file.createNewFile();
/* 237:    */         }
/* 238:    */         else
/* 239:    */         {
/* 240:406 */           String outPath = out.substring(0, out.lastIndexOf(File.separatorChar));
/* 241:    */           
/* 242:408 */           File dir = new File(outPath);
/* 243:409 */           if (dir.exists())
/* 244:    */           {
/* 245:410 */             success = file.createNewFile();
/* 246:    */           }
/* 247:    */           else
/* 248:    */           {
/* 249:412 */             dir.mkdirs();
/* 250:413 */             success = file.createNewFile();
/* 251:    */           }
/* 252:    */         }
/* 253:416 */         if (success)
/* 254:    */         {
/* 255:417 */           if (this.m_useRelativePath) {
/* 256:    */             try
/* 257:    */             {
/* 258:419 */               this.m_outputFile = Utils.convertToRelativePath(file);
/* 259:    */             }
/* 260:    */             catch (Exception e)
/* 261:    */             {
/* 262:421 */               this.m_outputFile = file;
/* 263:    */             }
/* 264:    */           } else {
/* 265:424 */             this.m_outputFile = file;
/* 266:    */           }
/* 267:426 */           setDestination(new FileOutputStream(this.m_outputFile));
/* 268:    */         }
/* 269:    */       }
/* 270:    */       catch (Exception ex)
/* 271:    */       {
/* 272:429 */         throw new IOException("Cannot create a new output file (Reason: " + ex.toString() + "). Standard out is used.");
/* 273:    */       }
/* 274:    */       finally
/* 275:    */       {
/* 276:432 */         if (!success)
/* 277:    */         {
/* 278:433 */           System.err.println("Cannot create a new output file. Standard out is used.");
/* 279:    */           
/* 280:435 */           this.m_outputFile = null;
/* 281:    */         }
/* 282:    */       }
/* 283:    */     }
/* 284:    */   }
/* 285:    */   
/* 286:    */   public void setDestination(OutputStream output)
/* 287:    */     throws IOException
/* 288:    */   {
/* 289:450 */     this.m_writer = new BufferedWriter(new OutputStreamWriter(output));
/* 290:    */   }
/* 291:    */   
/* 292:    */   public void setDirAndPrefix(String relationName, String add)
/* 293:    */   {
/* 294:    */     try
/* 295:    */     {
/* 296:464 */       if (this.m_dir.equals("")) {
/* 297:465 */         setDir(System.getProperty("user.dir"));
/* 298:    */       }
/* 299:467 */       if (this.m_prefix.equals(""))
/* 300:    */       {
/* 301:468 */         if (relationName.length() == 0) {
/* 302:469 */           throw new IOException("[Saver] Empty filename!!");
/* 303:    */         }
/* 304:471 */         String concat = this.m_dir + File.separator + relationName + add + this.FILE_EXTENSION;
/* 305:473 */         if ((!concat.toLowerCase().endsWith(this.FILE_EXTENSION)) && (!concat.toLowerCase().endsWith(this.FILE_EXTENSION + ".gz"))) {
/* 306:476 */           concat = concat + this.FILE_EXTENSION;
/* 307:    */         }
/* 308:478 */         setFile(new File(concat));
/* 309:    */       }
/* 310:    */       else
/* 311:    */       {
/* 312:480 */         if (relationName.length() > 0) {
/* 313:481 */           relationName = "_" + relationName;
/* 314:    */         }
/* 315:483 */         String concat = this.m_dir + File.separator + this.m_prefix + relationName + add;
/* 316:485 */         if ((!concat.toLowerCase().endsWith(this.FILE_EXTENSION)) && (!concat.toLowerCase().endsWith(this.FILE_EXTENSION + ".gz"))) {
/* 317:488 */           concat = concat + this.FILE_EXTENSION;
/* 318:    */         }
/* 319:490 */         setFile(new File(concat));
/* 320:    */       }
/* 321:    */     }
/* 322:    */     catch (Exception ex)
/* 323:    */     {
/* 324:493 */       System.err.println("File prefix and/or directory could not have been set.");
/* 325:    */       
/* 326:495 */       ex.printStackTrace();
/* 327:    */     }
/* 328:    */   }
/* 329:    */   
/* 330:    */   public abstract String getFileDescription();
/* 331:    */   
/* 332:    */   public String useRelativePathTipText()
/* 333:    */   {
/* 334:513 */     return "Use relative rather than absolute paths";
/* 335:    */   }
/* 336:    */   
/* 337:    */   public void setUseRelativePath(boolean rp)
/* 338:    */   {
/* 339:523 */     this.m_useRelativePath = rp;
/* 340:    */   }
/* 341:    */   
/* 342:    */   public boolean getUseRelativePath()
/* 343:    */   {
/* 344:533 */     return this.m_useRelativePath;
/* 345:    */   }
/* 346:    */   
/* 347:    */   protected static String makeOptionStr(AbstractFileSaver saver)
/* 348:    */   {
/* 349:547 */     StringBuffer result = new StringBuffer();
/* 350:    */     
/* 351:    */ 
/* 352:550 */     result.append("\n");
/* 353:551 */     result.append(saver.getClass().getName().replaceAll(".*\\.", ""));
/* 354:552 */     result.append(" options:\n\n");
/* 355:553 */     Enumeration<Option> enm = saver.listOptions();
/* 356:554 */     while (enm.hasMoreElements())
/* 357:    */     {
/* 358:555 */       Option option = (Option)enm.nextElement();
/* 359:556 */       result.append(option.synopsis() + "\n");
/* 360:557 */       result.append(option.description() + "\n");
/* 361:    */     }
/* 362:560 */     return result.toString();
/* 363:    */   }
/* 364:    */   
/* 365:    */   public static void runFileSaver(AbstractFileSaver saver, String[] options)
/* 366:    */   {
/* 367:    */     try
/* 368:    */     {
/* 369:572 */       String[] tmpOptions = (String[])options.clone();
/* 370:573 */       if (Utils.getFlag('h', tmpOptions))
/* 371:    */       {
/* 372:574 */         System.err.println("\nHelp requested\n" + makeOptionStr(saver));
/* 373:575 */         return;
/* 374:    */       }
/* 375:    */     }
/* 376:    */     catch (Exception e) {}
/* 377:    */     try
/* 378:    */     {
/* 379:    */       try
/* 380:    */       {
/* 381:584 */         saver.setOptions(options);
/* 382:    */       }
/* 383:    */       catch (Exception ex)
/* 384:    */       {
/* 385:586 */         System.err.println(makeOptionStr(saver));
/* 386:587 */         System.exit(1);
/* 387:    */       }
/* 388:590 */       saver.writeBatch();
/* 389:    */     }
/* 390:    */     catch (Exception ex)
/* 391:    */     {
/* 392:592 */       ex.printStackTrace();
/* 393:    */     }
/* 394:    */   }
/* 395:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.AbstractFileSaver
 * JD-Core Version:    0.7.0.1
 */