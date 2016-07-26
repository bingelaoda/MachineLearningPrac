/*   1:    */ package weka.core.converters;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.util.ArrayList;
/*   7:    */ import java.util.Collections;
/*   8:    */ import java.util.Enumeration;
/*   9:    */ import java.util.Vector;
/*  10:    */ import weka.core.Attribute;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.Option;
/*  14:    */ import weka.core.OptionHandler;
/*  15:    */ import weka.core.RevisionUtils;
/*  16:    */ import weka.core.SparseInstance;
/*  17:    */ import weka.core.Utils;
/*  18:    */ import weka.core.converters.nifti.Nifti1Dataset;
/*  19:    */ 
/*  20:    */ public class NIfTIFileLoader
/*  21:    */   extends AbstractFileLoader
/*  22:    */   implements BatchConverter, IncrementalConverter, OptionHandler
/*  23:    */ {
/*  24:    */   private static final long serialVersionUID = 3764733651132196582L;
/*  25: 57 */   public static String FILE_EXTENSION = ".nii";
/*  26: 60 */   public static String FILE_EXTENSION_COMPRESSED = FILE_EXTENSION + ".gz";
/*  27: 63 */   protected Nifti1Dataset m_dataSet = null;
/*  28: 66 */   protected File m_attributesFile = new File(System.getProperty("user.dir"));
/*  29: 69 */   protected Loader m_attributeLoader = new CSVLoader();
/*  30: 72 */   protected Instances m_attributeData = null;
/*  31: 75 */   protected File m_maskFile = new File(System.getProperty("user.dir"));
/*  32: 78 */   protected double[][][] m_mask = (double[][][])null;
/*  33: 81 */   protected int m_currentTimeSlot = 0;
/*  34:    */   
/*  35:    */   public String globalInfo()
/*  36:    */   {
/*  37: 90 */     return "Reads a file in NIfTI format. It automatically decompresses the data if the extension is '" + FILE_EXTENSION_COMPRESSED + "'.\n\nA mask file can be specified as a parameter. The mask must be consistent" + " with the main dataset and it is applied to every 2D/3D volume in the main dataset.\n\n" + "A file with volume attributes (e.g., class labels) can also be specified as a parameter. " + "The number of records with attributes must be the same as the number of volumes in the main dataset.\n\n" + "The attributes are read using the loader that is specified as a third parameter. The loader must be " + "configured appropriately to read the attribute information correctly.\n\n" + "The readDoubleVol(short ttt) method from the Nifti1Dataset class" + " (http://niftilib.sourceforge.net/java_api_html/Nifti1Dataset.html) is used to read the data for each" + " volume into a sparse WEKA instance. For an LxMxN volume , the order of values in the generated instance" + " is [(z_1, y_1, x_1), ..., (z_1, y_1, x_L), (z_1, y_2, x_1), ..., (z_1, y_M, x_L), (z_2, y_1, x_1), ...," + " (z_N, y_M, x_L)]. If the volume is an image and not 3D, then only x and y coordinates are used." + " The loader is currently very slow.";
/*  38:    */   }
/*  39:    */   
/*  40:    */   protected String defaultLoaderString()
/*  41:    */   {
/*  42:110 */     return "weka.core.converters.CSVLoader -H -N first-last -F \" \"";
/*  43:    */   }
/*  44:    */   
/*  45:    */   public NIfTIFileLoader()
/*  46:    */   {
/*  47:    */     try
/*  48:    */     {
/*  49:118 */       String[] loaderSpec = Utils.splitOptions(defaultLoaderString());
/*  50:119 */       if (loaderSpec.length == 0) {
/*  51:120 */         throw new IllegalArgumentException("Invalid loader specification string");
/*  52:    */       }
/*  53:122 */       String loaderName = loaderSpec[0];
/*  54:123 */       loaderSpec[0] = "";
/*  55:124 */       setAttributeLoader((Loader)Utils.forName(Loader.class, loaderName, loaderSpec));
/*  56:    */     }
/*  57:    */     catch (Exception ex)
/*  58:    */     {
/*  59:126 */       System.err.println("Could not parse default loader string in NIfTIFileLoader: " + ex.getMessage());
/*  60:    */     }
/*  61:    */   }
/*  62:    */   
/*  63:    */   public Enumeration<Option> listOptions()
/*  64:    */   {
/*  65:138 */     Vector<Option> result = new Vector();
/*  66:    */     
/*  67:140 */     result.add(new Option("\tThe mask data to apply to every volume.\n\t(default: user home directory)", "mask", 0, "-mask <filename>"));
/*  68:    */     
/*  69:    */ 
/*  70:143 */     result.add(new Option("\tThe attribute data for every volume.\n\t(default: user home directory)", "attributes", 0, "-attributes <filename>"));
/*  71:    */     
/*  72:145 */     result.addElement(new Option("\tClass name of loader to use, followed by loader options.\n\t(default: " + defaultLoaderString() + ")", "attributeLoader", 1, "-attributeLoader <loader specification>"));
/*  73:150 */     if ((getAttributeLoader() instanceof OptionHandler))
/*  74:    */     {
/*  75:151 */       result.addElement(new Option("", "", 0, "\nOptions specific to loader " + getAttributeLoader().getClass().getName() + ":"));
/*  76:    */       
/*  77:153 */       result.addAll(Collections.list(((OptionHandler)getAttributeLoader()).listOptions()));
/*  78:    */     }
/*  79:155 */     return result.elements();
/*  80:    */   }
/*  81:    */   
/*  82:    */   public void setOptions(String[] options)
/*  83:    */     throws Exception
/*  84:    */   {
/*  85:250 */     String tmpStr = Utils.getOption("mask", options);
/*  86:251 */     if (tmpStr.length() != 0) {
/*  87:252 */       setMaskFile(new File(tmpStr));
/*  88:    */     } else {
/*  89:254 */       setMaskFile(new File(System.getProperty("user.dir")));
/*  90:    */     }
/*  91:257 */     tmpStr = Utils.getOption("attributes", options);
/*  92:258 */     if (tmpStr.length() != 0) {
/*  93:259 */       setAttributesFile(new File(tmpStr));
/*  94:    */     } else {
/*  95:261 */       setAttributesFile(new File(System.getProperty("user.dir")));
/*  96:    */     }
/*  97:264 */     String loaderString = Utils.getOption("attributeLoader", options);
/*  98:265 */     if (loaderString.length() <= 0) {
/*  99:266 */       loaderString = defaultLoaderString();
/* 100:    */     }
/* 101:268 */     String[] loaderSpec = Utils.splitOptions(loaderString);
/* 102:269 */     if (loaderSpec.length == 0) {
/* 103:270 */       throw new IllegalArgumentException("Invalid loader specification string");
/* 104:    */     }
/* 105:272 */     String loaderName = loaderSpec[0];
/* 106:273 */     loaderSpec[0] = "";
/* 107:274 */     setAttributeLoader((Loader)Utils.forName(Loader.class, loaderName, loaderSpec));
/* 108:    */     
/* 109:276 */     Utils.checkForRemainingOptions(options);
/* 110:    */   }
/* 111:    */   
/* 112:    */   public String[] getOptions()
/* 113:    */   {
/* 114:286 */     Vector<String> options = new Vector();
/* 115:    */     
/* 116:288 */     options.add("-mask");
/* 117:289 */     options.add(getMaskFile().getAbsolutePath());
/* 118:    */     
/* 119:291 */     options.add("-attributes");
/* 120:292 */     options.add(getAttributesFile().getAbsolutePath());
/* 121:    */     
/* 122:294 */     options.add("-attributeLoader");
/* 123:295 */     Loader c = getAttributeLoader();
/* 124:296 */     if ((c instanceof OptionHandler)) {
/* 125:297 */       options.add(c.getClass().getName() + " " + Utils.joinOptions(((OptionHandler)c).getOptions()));
/* 126:    */     } else {
/* 127:299 */       options.add(c.getClass().getName());
/* 128:    */     }
/* 129:302 */     return (String[])options.toArray(new String[options.size()]);
/* 130:    */   }
/* 131:    */   
/* 132:    */   public String attributesFileTipText()
/* 133:    */   {
/* 134:311 */     return "The file with the attributes for each volume, in CSV format.";
/* 135:    */   }
/* 136:    */   
/* 137:    */   public File getAttributesFile()
/* 138:    */   {
/* 139:320 */     return new File(this.m_attributesFile.getAbsolutePath());
/* 140:    */   }
/* 141:    */   
/* 142:    */   public void setAttributesFile(File file)
/* 143:    */     throws IOException
/* 144:    */   {
/* 145:330 */     this.m_attributesFile = file;
/* 146:    */   }
/* 147:    */   
/* 148:    */   public String attributeLoaderTipText()
/* 149:    */   {
/* 150:340 */     return "The character to use as separator for the attributes file (use '\\t' for TAB).";
/* 151:    */   }
/* 152:    */   
/* 153:    */   public Loader getAttributeLoader()
/* 154:    */   {
/* 155:349 */     return this.m_attributeLoader;
/* 156:    */   }
/* 157:    */   
/* 158:    */   public void setAttributeLoader(Loader value)
/* 159:    */   {
/* 160:358 */     this.m_attributeLoader = value;
/* 161:    */   }
/* 162:    */   
/* 163:    */   public String maskFileTipText()
/* 164:    */   {
/* 165:367 */     return "The NIfTI file to load the mask data from.";
/* 166:    */   }
/* 167:    */   
/* 168:    */   public File getMaskFile()
/* 169:    */   {
/* 170:376 */     return new File(this.m_maskFile.getAbsolutePath());
/* 171:    */   }
/* 172:    */   
/* 173:    */   public void setMaskFile(File file)
/* 174:    */     throws IOException
/* 175:    */   {
/* 176:386 */     this.m_maskFile = file;
/* 177:    */   }
/* 178:    */   
/* 179:    */   public String getFileExtension()
/* 180:    */   {
/* 181:395 */     return FILE_EXTENSION;
/* 182:    */   }
/* 183:    */   
/* 184:    */   public String[] getFileExtensions()
/* 185:    */   {
/* 186:404 */     return new String[] { FILE_EXTENSION, FILE_EXTENSION_COMPRESSED };
/* 187:    */   }
/* 188:    */   
/* 189:    */   public String getFileDescription()
/* 190:    */   {
/* 191:413 */     return "NIfTI .nii files";
/* 192:    */   }
/* 193:    */   
/* 194:    */   public void reset()
/* 195:    */     throws IOException
/* 196:    */   {
/* 197:422 */     this.m_structure = null;
/* 198:    */     
/* 199:424 */     setRetrieval(0);
/* 200:426 */     if (this.m_File != null) {
/* 201:427 */       setFile(new File(this.m_File));
/* 202:    */     }
/* 203:    */   }
/* 204:    */   
/* 205:    */   public void setSource(File file)
/* 206:    */     throws IOException
/* 207:    */   {
/* 208:439 */     this.m_structure = null;
/* 209:    */     
/* 210:441 */     setRetrieval(0);
/* 211:443 */     if (file == null) {
/* 212:444 */       throw new IOException("Source file object is null!");
/* 213:    */     }
/* 214:446 */     this.m_sourceFile = file;
/* 215:447 */     this.m_File = file.getPath();
/* 216:    */   }
/* 217:    */   
/* 218:    */   public Instances getStructure()
/* 219:    */     throws IOException
/* 220:    */   {
/* 221:460 */     if (this.m_sourceFile == null) {
/* 222:461 */       throw new IOException("No source has been specified");
/* 223:    */     }
/* 224:464 */     if (this.m_structure == null)
/* 225:    */     {
/* 226:465 */       this.m_dataSet = new Nifti1Dataset(this.m_File);
/* 227:466 */       this.m_dataSet.readHeader();
/* 228:467 */       if (!this.m_dataSet.exists()) {
/* 229:468 */         System.err.println("The file " + this.m_File + " is not a valid dataset in Nifti1 format -- skipping.");
/* 230:    */       }
/* 231:473 */       this.m_mask = ((double[][][])null);
/* 232:474 */       if ((this.m_maskFile.exists()) && (this.m_maskFile.isFile())) {
/* 233:    */         try
/* 234:    */         {
/* 235:476 */           String filename = this.m_maskFile.getAbsolutePath();
/* 236:477 */           Nifti1Dataset mask = new Nifti1Dataset(filename);
/* 237:478 */           mask.readHeader();
/* 238:479 */           if (!mask.exists())
/* 239:    */           {
/* 240:480 */             System.err.println("The file " + filename + " is not a valid dataset in Nifti1 format -- skipping.");
/* 241:    */           }
/* 242:    */           else
/* 243:    */           {
/* 244:482 */             if (mask.XDIM != this.m_dataSet.XDIM) {
/* 245:483 */               throw new IOException("X dimension for mask in " + filename + " not equal to data X dimension.");
/* 246:    */             }
/* 247:485 */             if (mask.YDIM != this.m_dataSet.YDIM) {
/* 248:486 */               throw new IOException("Y dimension for mask in " + filename + " not equal to data Y dimension.");
/* 249:    */             }
/* 250:488 */             if (mask.ZDIM != this.m_dataSet.ZDIM) {
/* 251:489 */               throw new IOException("Z dimension for mask in " + filename + " not equal to data Z dimension.");
/* 252:    */             }
/* 253:    */           }
/* 254:492 */           this.m_mask = mask.readDoubleVol((short)0);
/* 255:    */         }
/* 256:    */         catch (Exception ex)
/* 257:    */         {
/* 258:494 */           System.err.println("Skipping mask file.");
/* 259:495 */           System.err.println(ex.getMessage());
/* 260:496 */           this.m_mask = ((double[][][])null);
/* 261:    */         }
/* 262:    */       }
/* 263:501 */       this.m_attributeData = null;
/* 264:502 */       if ((this.m_attributesFile.exists()) && (this.m_attributesFile.isFile())) {
/* 265:    */         try
/* 266:    */         {
/* 267:504 */           this.m_attributeLoader.setSource(this.m_attributesFile);
/* 268:505 */           this.m_attributeData = this.m_attributeLoader.getDataSet();
/* 269:506 */           if (((this.m_dataSet.TDIM == 0) && (this.m_attributeData.numInstances() != 1)) || (this.m_attributeData.numInstances() != this.m_dataSet.TDIM))
/* 270:    */           {
/* 271:508 */             System.err.println("WARNING: Attribute information inconsistent with number of time slots in NIfTI dataset, ignoring attribute information");
/* 272:    */             
/* 273:510 */             this.m_attributeData = null;
/* 274:    */           }
/* 275:    */         }
/* 276:    */         catch (Exception ex)
/* 277:    */         {
/* 278:513 */           System.err.println("Skipping attributes file.");
/* 279:514 */           System.err.println(ex.getMessage());
/* 280:515 */           this.m_attributeData = null;
/* 281:    */         }
/* 282:    */       }
/* 283:520 */       ArrayList<Attribute> atts = new ArrayList();
/* 284:521 */       if (this.m_attributeData != null) {
/* 285:522 */         for (int i = 0; i < this.m_attributeData.numAttributes(); i++) {
/* 286:523 */           atts.add((Attribute)this.m_attributeData.attribute(i).copy());
/* 287:    */         }
/* 288:    */       }
/* 289:526 */       if (this.m_dataSet.ZDIM == 0) {
/* 290:527 */         for (int y = 0; y < this.m_dataSet.YDIM; y++) {
/* 291:528 */           for (int x = 0; x < this.m_dataSet.XDIM; x++) {
/* 292:529 */             atts.add(new Attribute("X" + x + "Y" + y));
/* 293:    */           }
/* 294:    */         }
/* 295:    */       } else {
/* 296:533 */         for (int z = 0; z < this.m_dataSet.ZDIM; z++) {
/* 297:534 */           for (int y = 0; y < this.m_dataSet.YDIM; y++) {
/* 298:535 */             for (int x = 0; x < this.m_dataSet.XDIM; x++) {
/* 299:536 */               atts.add(new Attribute("X" + x + "Y" + y + "Z" + z));
/* 300:    */             }
/* 301:    */           }
/* 302:    */         }
/* 303:    */       }
/* 304:542 */       String relName = this.m_File.replaceAll("/", "_");
/* 305:543 */       relName = relName.replaceAll("\\\\", "_").replaceAll(":", "_");
/* 306:544 */       this.m_structure = new Instances(relName, atts, 0);
/* 307:545 */       this.m_structure.setClassIndex(this.m_structure.numAttributes() - 1);
/* 308:    */     }
/* 309:549 */     this.m_currentTimeSlot = 0;
/* 310:    */     
/* 311:551 */     return new Instances(this.m_structure, 0);
/* 312:    */   }
/* 313:    */   
/* 314:    */   public Instances getDataSet()
/* 315:    */     throws IOException
/* 316:    */   {
/* 317:564 */     if (this.m_sourceFile == null) {
/* 318:565 */       throw new IOException("No source has been specified");
/* 319:    */     }
/* 320:567 */     if (getRetrieval() == 2) {
/* 321:568 */       throw new IOException("Cannot mix getting Instances in both incremental and batch modes");
/* 322:    */     }
/* 323:570 */     setRetrieval(1);
/* 324:571 */     Instances data = getStructure();
/* 325:572 */     if (this.m_dataSet.TDIM == 0) {
/* 326:573 */       data.add(new SparseInstance(1.0D, make1Darray(0)));
/* 327:    */     } else {
/* 328:575 */       for (int i = 0; i < this.m_dataSet.TDIM; i++) {
/* 329:576 */         data.add(new SparseInstance(1.0D, make1Darray(i)));
/* 330:    */       }
/* 331:    */     }
/* 332:581 */     return data;
/* 333:    */   }
/* 334:    */   
/* 335:    */   protected double[] make1Darray(int timeSlot)
/* 336:    */     throws IOException
/* 337:    */   {
/* 338:590 */     double[][][] doubles = this.m_dataSet.readDoubleVol((short)timeSlot);
/* 339:591 */     double[] newInst = new double[this.m_structure.numAttributes()];
/* 340:592 */     int counter = 0;
/* 341:593 */     if (this.m_attributeData != null) {
/* 342:594 */       for (int i = 0; i < this.m_attributeData.numAttributes(); i++) {
/* 343:595 */         newInst[(counter++)] = this.m_attributeData.instance(timeSlot).value(i);
/* 344:    */       }
/* 345:    */     }
/* 346:598 */     if (this.m_dataSet.ZDIM == 0) {
/* 347:599 */       for (int y = 0; y < this.m_dataSet.YDIM; y++) {
/* 348:600 */         for (int x = 0; x < this.m_dataSet.XDIM; x++) {
/* 349:601 */           newInst[(counter++)] = ((this.m_mask == null) || (this.m_mask[0][y][x] > 0.0D) ? doubles[0][y][x] : 0.0D);
/* 350:    */         }
/* 351:    */       }
/* 352:    */     } else {
/* 353:605 */       for (int z = 0; z < this.m_dataSet.ZDIM; z++) {
/* 354:606 */         for (int y = 0; y < this.m_dataSet.YDIM; y++) {
/* 355:607 */           for (int x = 0; x < this.m_dataSet.XDIM; x++) {
/* 356:608 */             newInst[(counter++)] = ((this.m_mask == null) || (this.m_mask[z][y][x] > 0.0D) ? doubles[z][y][x] : 0.0D);
/* 357:    */           }
/* 358:    */         }
/* 359:    */       }
/* 360:    */     }
/* 361:614 */     return newInst;
/* 362:    */   }
/* 363:    */   
/* 364:    */   public Instance getNextInstance(Instances structure)
/* 365:    */     throws IOException
/* 366:    */   {
/* 367:622 */     if (getRetrieval() == 1) {
/* 368:623 */       throw new IOException("Cannot mix getting instances in both incremental and batch modes");
/* 369:    */     }
/* 370:625 */     this.m_structure = structure;
/* 371:626 */     setRetrieval(2);
/* 372:629 */     if (((this.m_currentTimeSlot == 0) && (this.m_dataSet.TDIM == 0)) || (this.m_currentTimeSlot < this.m_dataSet.TDIM))
/* 373:    */     {
/* 374:630 */       Instance inst = new SparseInstance(1.0D, make1Darray(this.m_currentTimeSlot++));
/* 375:631 */       inst.setDataset(this.m_structure);
/* 376:632 */       return inst;
/* 377:    */     }
/* 378:634 */     return null;
/* 379:    */   }
/* 380:    */   
/* 381:    */   public String getRevision()
/* 382:    */   {
/* 383:644 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 384:    */   }
/* 385:    */   
/* 386:    */   public static void main(String[] args)
/* 387:    */   {
/* 388:653 */     runFileLoader(new NIfTIFileLoader(), args);
/* 389:    */   }
/* 390:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.NIfTIFileLoader
 * JD-Core Version:    0.7.0.1
 */