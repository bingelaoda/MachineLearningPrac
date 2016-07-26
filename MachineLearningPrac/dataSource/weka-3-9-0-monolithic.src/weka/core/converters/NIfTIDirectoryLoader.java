/*   1:    */ package weka.core.converters;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.util.ArrayList;
/*   7:    */ import java.util.Collections;
/*   8:    */ import java.util.Enumeration;
/*   9:    */ import java.util.LinkedList;
/*  10:    */ import java.util.List;
/*  11:    */ import java.util.Vector;
/*  12:    */ import weka.core.Attribute;
/*  13:    */ import weka.core.Instance;
/*  14:    */ import weka.core.Instances;
/*  15:    */ import weka.core.Option;
/*  16:    */ import weka.core.OptionHandler;
/*  17:    */ import weka.core.RevisionUtils;
/*  18:    */ import weka.core.SerializedObject;
/*  19:    */ import weka.core.SparseInstance;
/*  20:    */ import weka.core.Utils;
/*  21:    */ import weka.core.converters.nifti.Nifti1Dataset;
/*  22:    */ 
/*  23:    */ public class NIfTIDirectoryLoader
/*  24:    */   extends AbstractLoader
/*  25:    */   implements BatchConverter, IncrementalConverter, OptionHandler
/*  26:    */ {
/*  27:    */   private static final long serialVersionUID = 3492918763718247647L;
/*  28: 65 */   protected Instances m_structure = null;
/*  29: 68 */   protected File m_sourceFile = new File(System.getProperty("user.dir"));
/*  30: 71 */   protected File m_maskFile = new File(System.getProperty("user.dir"));
/*  31: 74 */   protected double[][][] m_mask = (double[][][])null;
/*  32: 77 */   protected boolean m_Debug = false;
/*  33:    */   protected List<LinkedList<String>> m_filesByClass;
/*  34:    */   
/*  35:    */   public NIfTIDirectoryLoader()
/*  36:    */   {
/*  37: 84 */     setRetrieval(0);
/*  38:    */   }
/*  39:    */   
/*  40:    */   public String globalInfo()
/*  41:    */   {
/*  42: 94 */     return "Package for loading a directory containing MRI data in NIfTI format. The directory to be loaded must contain as many subdirectories as there are classes of MRI data. Each subdirectory name will be used as the class label for the corresponding .nii files in that subdirectory. (This is the same strategy as the one used by WEKA's TextDirectoryLoader.)\n\n Currently, the package only reads volume information for the first time slot from each .nii file. A mask file can also be specified as a parameter. This mask is must be consistent + with the other data and is applied to every 2D/3D volume in this other data.\n\nThe readDoubleVol(short ttt) method from the Nifti1Dataset class (http://niftilib.sourceforge.net/java_api_html/Nifti1Dataset.html) is used to read the data for each volume into a sparse WEKA instance (with ttt=0). For an LxMxN volume (the dimensions must be the same for each .nii file in the directory!), the order of values in the generated instance is [(z_1, y_1, x_1), ..., (z_1, y_1, x_L), (z_1, y_2, x_1), ..., (z_1, y_M, x_L), (z_2, y_1, x_1), ..., (z_N, y_M, x_L)]. If the volume is an image and not 3D, then only x and y coordinates are used.";
/*  43:    */   }
/*  44:    */   
/*  45:    */   public Enumeration<Option> listOptions()
/*  46:    */   {
/*  47:117 */     Vector<Option> result = new Vector();
/*  48:    */     
/*  49:119 */     result.add(new Option("\tEnables debug output.\n\t(default: off)", "D", 0, "-D"));
/*  50:    */     
/*  51:    */ 
/*  52:122 */     result.add(new Option("\tThe mask data to apply to every volume.\n\t(default: current directory)", "mask", 0, "-mask <filename>"));
/*  53:    */     
/*  54:    */ 
/*  55:125 */     return result.elements();
/*  56:    */   }
/*  57:    */   
/*  58:    */   public void setOptions(String[] options)
/*  59:    */     throws Exception
/*  60:    */   {
/*  61:150 */     setDebug(Utils.getFlag("D", options));
/*  62:    */     
/*  63:152 */     setDirectory(new File(Utils.getOption("dir", options)));
/*  64:    */     
/*  65:154 */     setMask(new File(Utils.getOption("mask", options)));
/*  66:    */   }
/*  67:    */   
/*  68:    */   public String[] getOptions()
/*  69:    */   {
/*  70:164 */     Vector<String> options = new Vector();
/*  71:166 */     if (getDebug()) {
/*  72:167 */       options.add("-D");
/*  73:    */     }
/*  74:170 */     options.add("-dir");
/*  75:171 */     options.add(getDirectory().getAbsolutePath());
/*  76:    */     
/*  77:173 */     options.add("-mask");
/*  78:174 */     options.add(getMask().getAbsolutePath());
/*  79:    */     
/*  80:176 */     return (String[])options.toArray(new String[options.size()]);
/*  81:    */   }
/*  82:    */   
/*  83:    */   public void setDebug(boolean value)
/*  84:    */   {
/*  85:185 */     this.m_Debug = value;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public boolean getDebug()
/*  89:    */   {
/*  90:194 */     return this.m_Debug;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public String debugTipText()
/*  94:    */   {
/*  95:203 */     return "Whether to print additional debug information to the console.";
/*  96:    */   }
/*  97:    */   
/*  98:    */   public String getFileDescription()
/*  99:    */   {
/* 100:212 */     return "Directories";
/* 101:    */   }
/* 102:    */   
/* 103:    */   public String directoryTipText()
/* 104:    */   {
/* 105:221 */     return "The directory to load data from (not required when used from GUI).";
/* 106:    */   }
/* 107:    */   
/* 108:    */   public File getDirectory()
/* 109:    */   {
/* 110:230 */     return new File(this.m_sourceFile.getAbsolutePath());
/* 111:    */   }
/* 112:    */   
/* 113:    */   public void setDirectory(File dir)
/* 114:    */     throws IOException
/* 115:    */   {
/* 116:240 */     setSource(dir);
/* 117:    */   }
/* 118:    */   
/* 119:    */   public String maskTipText()
/* 120:    */   {
/* 121:249 */     return "The NIfTI file to load the mask data from.";
/* 122:    */   }
/* 123:    */   
/* 124:    */   public File getMask()
/* 125:    */   {
/* 126:258 */     return new File(this.m_maskFile.getAbsolutePath());
/* 127:    */   }
/* 128:    */   
/* 129:    */   public void setMask(File file)
/* 130:    */     throws IOException
/* 131:    */   {
/* 132:268 */     this.m_maskFile = file;
/* 133:    */   }
/* 134:    */   
/* 135:    */   public void reset()
/* 136:    */   {
/* 137:276 */     this.m_structure = null;
/* 138:277 */     this.m_filesByClass = null;
/* 139:278 */     this.m_lastClassDir = 0;
/* 140:279 */     setRetrieval(0);
/* 141:    */   }
/* 142:    */   
/* 143:    */   public void setSource(File dir)
/* 144:    */     throws IOException
/* 145:    */   {
/* 146:291 */     reset();
/* 147:293 */     if (dir == null) {
/* 148:294 */       throw new IOException("Source directory object is null!");
/* 149:    */     }
/* 150:297 */     this.m_sourceFile = dir;
/* 151:298 */     if ((!dir.exists()) || (!dir.isDirectory())) {
/* 152:299 */       throw new IOException("Directory '" + dir + "' not found");
/* 153:    */     }
/* 154:    */   }
/* 155:    */   
/* 156:    */   public Instances getStructure()
/* 157:    */     throws IOException
/* 158:    */   {
/* 159:312 */     if (getDirectory() == null) {
/* 160:313 */       throw new IOException("No directory/source has been specified");
/* 161:    */     }
/* 162:317 */     if (this.m_structure == null)
/* 163:    */     {
/* 164:319 */       String directoryPath = getDirectory().getAbsolutePath();
/* 165:320 */       ArrayList<Attribute> atts = new ArrayList();
/* 166:321 */       ArrayList<String> classes = new ArrayList();
/* 167:    */       
/* 168:323 */       File dir = new File(directoryPath);
/* 169:324 */       String[] subdirs = dir.list();
/* 170:    */       
/* 171:326 */       Nifti1Dataset header = null;
/* 172:327 */       for (String subdir2 : subdirs)
/* 173:    */       {
/* 174:328 */         File subdir = new File(directoryPath + File.separator + subdir2);
/* 175:329 */         if (subdir.isDirectory())
/* 176:    */         {
/* 177:330 */           classes.add(subdir2);
/* 178:331 */           String[] files = subdir.list();
/* 179:332 */           for (String file : files)
/* 180:    */           {
/* 181:333 */             String filename = directoryPath + File.separator + subdir2 + File.separator + file;
/* 182:334 */             Nifti1Dataset data = new Nifti1Dataset(filename);
/* 183:335 */             data.readHeader();
/* 184:336 */             if (!data.exists())
/* 185:    */             {
/* 186:337 */               System.err.println("The file " + filename + " is not a valid dataset in Nifti1 format -- skipping.");
/* 187:    */             }
/* 188:339 */             else if (header == null)
/* 189:    */             {
/* 190:340 */               header = new Nifti1Dataset();
/* 191:341 */               header.copyHeader(data);
/* 192:    */             }
/* 193:    */             else
/* 194:    */             {
/* 195:343 */               if (header.XDIM != data.XDIM) {
/* 196:344 */                 throw new IOException("X dimension for " + filename + " inconsistent with previous X dimensions.");
/* 197:    */               }
/* 198:346 */               if (header.YDIM != data.YDIM) {
/* 199:347 */                 throw new IOException("Y dimension for " + filename + " inconsistent with previous Y dimensions.");
/* 200:    */               }
/* 201:349 */               if (header.ZDIM != data.ZDIM) {
/* 202:350 */                 throw new IOException("Z dimension for " + filename + " inconsistent with previous Z dimensions.");
/* 203:    */               }
/* 204:    */             }
/* 205:    */           }
/* 206:    */         }
/* 207:    */       }
/* 208:359 */       Collections.sort(classes);
/* 209:    */       
/* 210:    */ 
/* 211:362 */       this.m_mask = ((double[][][])null);
/* 212:363 */       if ((this.m_maskFile.exists()) && (this.m_maskFile.isFile())) {
/* 213:    */         try
/* 214:    */         {
/* 215:365 */           String filename = this.m_maskFile.getAbsolutePath();
/* 216:366 */           Nifti1Dataset data = new Nifti1Dataset(filename);
/* 217:367 */           data.readHeader();
/* 218:368 */           if (!data.exists())
/* 219:    */           {
/* 220:369 */             System.err.println("The file " + filename + " is not a valid dataset in Nifti1 format -- skipping.");
/* 221:    */           }
/* 222:    */           else
/* 223:    */           {
/* 224:371 */             if (header.XDIM != data.XDIM) {
/* 225:372 */               throw new IOException("X dimension for mask in " + filename + " data X dimension.");
/* 226:    */             }
/* 227:374 */             if (header.YDIM != data.YDIM) {
/* 228:375 */               throw new IOException("Y dimension for mask in " + filename + " data Y dimensions.");
/* 229:    */             }
/* 230:377 */             if (header.ZDIM != data.ZDIM) {
/* 231:378 */               throw new IOException("Z dimension for mask in " + filename + " data Z dimensions.");
/* 232:    */             }
/* 233:    */           }
/* 234:381 */           this.m_mask = data.readDoubleVol((short)0);
/* 235:    */         }
/* 236:    */         catch (Exception ex)
/* 237:    */         {
/* 238:383 */           System.err.println(ex.getMessage());
/* 239:384 */           this.m_mask = ((double[][][])null);
/* 240:    */         }
/* 241:    */       }
/* 242:389 */       if (header.ZDIM == 0) {
/* 243:390 */         for (int y = 0; y < header.YDIM; y++) {
/* 244:391 */           for (int x = 0; x < header.XDIM; x++) {
/* 245:392 */             atts.add(new Attribute("X" + x + "Y" + y));
/* 246:    */           }
/* 247:    */         }
/* 248:    */       } else {
/* 249:396 */         for (int z = 0; z < header.ZDIM; z++) {
/* 250:397 */           for (int y = 0; y < header.YDIM; y++) {
/* 251:398 */             for (int x = 0; x < header.XDIM; x++) {
/* 252:399 */               atts.add(new Attribute("X" + x + "Y" + y + "Z" + z));
/* 253:    */             }
/* 254:    */           }
/* 255:    */         }
/* 256:    */       }
/* 257:405 */       atts.add(new Attribute("class", classes));
/* 258:    */       
/* 259:407 */       String relName = directoryPath.replaceAll("/", "_");
/* 260:408 */       relName = relName.replaceAll("\\\\", "_").replaceAll(":", "_");
/* 261:409 */       this.m_structure = new Instances(relName, atts, 0);
/* 262:410 */       this.m_structure.setClassIndex(this.m_structure.numAttributes() - 1);
/* 263:    */     }
/* 264:413 */     return this.m_structure;
/* 265:    */   }
/* 266:    */   
/* 267:    */   protected double[] make1Darray(Nifti1Dataset dataSet, Instances structure, int classValue)
/* 268:    */     throws IOException
/* 269:    */   {
/* 270:421 */     double[][][] doubles = dataSet.readDoubleVol((short)0);
/* 271:422 */     double[] newInst = new double[structure.numAttributes()];
/* 272:423 */     int counter = 0;
/* 273:424 */     if (dataSet.ZDIM == 0) {
/* 274:425 */       for (int y = 0; y < dataSet.YDIM; y++) {
/* 275:426 */         for (int x = 0; x < dataSet.XDIM; x++) {
/* 276:427 */           newInst[(counter++)] = ((this.m_mask == null) || (this.m_mask[0][y][x] > 0.0D) ? doubles[0][y][x] : 0.0D);
/* 277:    */         }
/* 278:    */       }
/* 279:    */     } else {
/* 280:431 */       for (int z = 0; z < dataSet.ZDIM; z++) {
/* 281:432 */         for (int y = 0; y < dataSet.YDIM; y++) {
/* 282:433 */           for (int x = 0; x < dataSet.XDIM; x++) {
/* 283:434 */             newInst[(counter++)] = ((this.m_mask == null) || (this.m_mask[z][y][x] > 0.0D) ? doubles[z][y][x] : 0.0D);
/* 284:    */           }
/* 285:    */         }
/* 286:    */       }
/* 287:    */     }
/* 288:439 */     newInst[structure.classIndex()] = classValue;
/* 289:    */     
/* 290:441 */     return newInst;
/* 291:    */   }
/* 292:    */   
/* 293:    */   public Instances getDataSet()
/* 294:    */     throws IOException
/* 295:    */   {
/* 296:454 */     if (getDirectory() == null) {
/* 297:455 */       throw new IOException("No directory/source has been specified");
/* 298:    */     }
/* 299:458 */     String directoryPath = getDirectory().getAbsolutePath();
/* 300:459 */     ArrayList<String> classes = new ArrayList();
/* 301:460 */     Enumeration<Object> enm = getStructure().classAttribute().enumerateValues();
/* 302:461 */     while (enm.hasMoreElements())
/* 303:    */     {
/* 304:462 */       Object oo = enm.nextElement();
/* 305:463 */       if ((oo instanceof SerializedObject)) {
/* 306:464 */         classes.add(((SerializedObject)oo).getObject().toString());
/* 307:    */       } else {
/* 308:466 */         classes.add(oo.toString());
/* 309:    */       }
/* 310:    */     }
/* 311:470 */     Instances data = getStructure();
/* 312:471 */     int fileCount = 0;
/* 313:472 */     for (int k = 0; k < classes.size(); k++)
/* 314:    */     {
/* 315:473 */       String subdirPath = (String)classes.get(k);
/* 316:474 */       File subdir = new File(directoryPath + File.separator + subdirPath);
/* 317:475 */       String[] files = subdir.list();
/* 318:476 */       for (String file : files) {
/* 319:    */         try
/* 320:    */         {
/* 321:478 */           fileCount++;
/* 322:479 */           if (getDebug()) {
/* 323:480 */             System.err.println("processing " + fileCount + " : " + subdirPath + " : " + file);
/* 324:    */           }
/* 325:483 */           String filename = directoryPath + File.separator + subdirPath + File.separator + file;
/* 326:484 */           Nifti1Dataset dataSet = new Nifti1Dataset(filename);
/* 327:485 */           dataSet.readHeader();
/* 328:486 */           if (!dataSet.exists()) {
/* 329:487 */             System.err.println("The file " + filename + " is not a valid dataset in Nifti1 format -- skipping.");
/* 330:    */           } else {
/* 331:489 */             data.add(new SparseInstance(1.0D, make1Darray(dataSet, this.m_structure, k)));
/* 332:    */           }
/* 333:    */         }
/* 334:    */         catch (Exception e)
/* 335:    */         {
/* 336:492 */           System.err.println("failed to convert file: " + directoryPath + File.separator + subdirPath + File.separator + file);
/* 337:    */         }
/* 338:    */       }
/* 339:    */     }
/* 340:498 */     return data;
/* 341:    */   }
/* 342:    */   
/* 343:502 */   protected int m_lastClassDir = 0;
/* 344:    */   
/* 345:    */   public Instance getNextInstance(Instances structure)
/* 346:    */     throws IOException
/* 347:    */   {
/* 348:516 */     String directoryPath = getDirectory().getAbsolutePath();
/* 349:517 */     Attribute classAtt = structure.classAttribute();
/* 350:518 */     if (this.m_filesByClass == null)
/* 351:    */     {
/* 352:519 */       this.m_filesByClass = new ArrayList();
/* 353:520 */       for (int i = 0; i < classAtt.numValues(); i++)
/* 354:    */       {
/* 355:521 */         File classDir = new File(directoryPath + File.separator + classAtt.value(i));
/* 356:    */         
/* 357:523 */         String[] files = classDir.list();
/* 358:524 */         LinkedList<String> classDocs = new LinkedList();
/* 359:525 */         for (String cd : files)
/* 360:    */         {
/* 361:526 */           File txt = new File(directoryPath + File.separator + classAtt.value(i) + File.separator + cd);
/* 362:529 */           if (txt.isFile()) {
/* 363:530 */             classDocs.add(cd);
/* 364:    */           }
/* 365:    */         }
/* 366:533 */         this.m_filesByClass.add(classDocs);
/* 367:    */       }
/* 368:    */     }
/* 369:538 */     int count = 0;
/* 370:539 */     LinkedList<String> classContents = (LinkedList)this.m_filesByClass.get(this.m_lastClassDir);
/* 371:540 */     boolean found = classContents.size() > 0;
/* 372:541 */     while (classContents.size() == 0)
/* 373:    */     {
/* 374:542 */       this.m_lastClassDir += 1;
/* 375:543 */       count++;
/* 376:544 */       if (this.m_lastClassDir == structure.classAttribute().numValues()) {
/* 377:545 */         this.m_lastClassDir = 0;
/* 378:    */       }
/* 379:547 */       classContents = (LinkedList)this.m_filesByClass.get(this.m_lastClassDir);
/* 380:548 */       if (classContents.size() > 0) {
/* 381:549 */         found = true;
/* 382:552 */       } else if (count == structure.classAttribute().numValues()) {
/* 383:    */         break;
/* 384:    */       }
/* 385:    */     }
/* 386:557 */     if (found)
/* 387:    */     {
/* 388:558 */       String nextDoc = (String)classContents.poll();
/* 389:    */       
/* 390:560 */       String filename = directoryPath + File.separator + classAtt.value(this.m_lastClassDir) + File.separator + nextDoc;
/* 391:    */       
/* 392:562 */       Nifti1Dataset dataSet = new Nifti1Dataset(filename);
/* 393:563 */       dataSet.readHeader();
/* 394:564 */       Instance inst = new SparseInstance(1.0D, make1Darray(dataSet, structure, this.m_lastClassDir));
/* 395:565 */       inst.setDataset(structure);
/* 396:    */       
/* 397:567 */       this.m_lastClassDir += 1;
/* 398:568 */       if (this.m_lastClassDir == structure.classAttribute().numValues()) {
/* 399:569 */         this.m_lastClassDir = 0;
/* 400:    */       }
/* 401:572 */       return inst;
/* 402:    */     }
/* 403:574 */     return null;
/* 404:    */   }
/* 405:    */   
/* 406:    */   public String getRevision()
/* 407:    */   {
/* 408:585 */     return RevisionUtils.extract("$Revision: 10857 $");
/* 409:    */   }
/* 410:    */   
/* 411:    */   public static void main(String[] args)
/* 412:    */   {
/* 413:594 */     if (args.length > 0)
/* 414:    */     {
/* 415:    */       try
/* 416:    */       {
/* 417:596 */         NIfTIDirectoryLoader loader = new NIfTIDirectoryLoader();
/* 418:597 */         loader.setOptions(args);
/* 419:    */         
/* 420:599 */         Instances structure = loader.getStructure();
/* 421:600 */         System.out.println(structure);
/* 422:    */         Instance temp;
/* 423:    */         do
/* 424:    */         {
/* 425:603 */           temp = loader.getNextInstance(structure);
/* 426:604 */           if (temp != null) {
/* 427:605 */             System.out.println(temp);
/* 428:    */           }
/* 429:607 */         } while (temp != null);
/* 430:    */       }
/* 431:    */       catch (Exception e)
/* 432:    */       {
/* 433:609 */         e.printStackTrace();
/* 434:    */       }
/* 435:    */     }
/* 436:    */     else
/* 437:    */     {
/* 438:612 */       System.err.println("\nUsage:\n\tNIfTIDirectoryLoader [options]\n\nOptions:\n");
/* 439:    */       
/* 440:    */ 
/* 441:615 */       Enumeration<Option> enm = new NIfTIDirectoryLoader().listOptions();
/* 442:616 */       while (enm.hasMoreElements())
/* 443:    */       {
/* 444:617 */         Option option = (Option)enm.nextElement();
/* 445:618 */         System.err.println(option.synopsis());
/* 446:619 */         System.err.println(option.description());
/* 447:    */       }
/* 448:622 */       System.err.println();
/* 449:    */     }
/* 450:    */   }
/* 451:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.NIfTIDirectoryLoader
 * JD-Core Version:    0.7.0.1
 */