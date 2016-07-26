/*   1:    */ package weka.core.converters;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.FileNotFoundException;
/*   6:    */ import java.io.FileReader;
/*   7:    */ import java.io.IOException;
/*   8:    */ import java.io.PrintStream;
/*   9:    */ import java.io.Reader;
/*  10:    */ import java.io.StreamTokenizer;
/*  11:    */ import java.util.ArrayList;
/*  12:    */ import weka.core.Attribute;
/*  13:    */ import weka.core.DenseInstance;
/*  14:    */ import weka.core.Instance;
/*  15:    */ import weka.core.Instances;
/*  16:    */ import weka.core.RevisionUtils;
/*  17:    */ import weka.core.Utils;
/*  18:    */ 
/*  19:    */ public class C45Loader
/*  20:    */   extends AbstractFileLoader
/*  21:    */   implements BatchConverter, IncrementalConverter
/*  22:    */ {
/*  23:    */   static final long serialVersionUID = 5454329403218219L;
/*  24: 60 */   public static String FILE_EXTENSION = ".names";
/*  25: 65 */   private File m_sourceFileData = null;
/*  26: 70 */   private transient Reader m_namesReader = null;
/*  27: 75 */   private transient Reader m_dataReader = null;
/*  28:    */   private String m_fileStem;
/*  29:    */   private int m_numAttribs;
/*  30:    */   private boolean[] m_ignore;
/*  31:    */   
/*  32:    */   public String globalInfo()
/*  33:    */   {
/*  34:100 */     return "Reads a file that is C45 format. Can take a filestem or filestem with .names or .data appended. Assumes that path/<filestem>.names and path/<filestem>.data exist and contain the names and data respectively.";
/*  35:    */   }
/*  36:    */   
/*  37:    */   public void reset()
/*  38:    */     throws IOException
/*  39:    */   {
/*  40:113 */     this.m_structure = null;
/*  41:114 */     setRetrieval(0);
/*  42:116 */     if (this.m_File != null) {
/*  43:117 */       setFile(new File(this.m_File));
/*  44:    */     }
/*  45:    */   }
/*  46:    */   
/*  47:    */   public String getFileExtension()
/*  48:    */   {
/*  49:128 */     return FILE_EXTENSION;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public String[] getFileExtensions()
/*  53:    */   {
/*  54:138 */     return new String[] { ".names", ".data" };
/*  55:    */   }
/*  56:    */   
/*  57:    */   public String getFileDescription()
/*  58:    */   {
/*  59:148 */     return "C4.5 data files";
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void setSource(File file)
/*  63:    */     throws IOException
/*  64:    */   {
/*  65:160 */     this.m_structure = null;
/*  66:161 */     setRetrieval(0);
/*  67:163 */     if (file == null) {
/*  68:164 */       throw new IOException("Source file object is null!");
/*  69:    */     }
/*  70:167 */     String fname = file.getName();
/*  71:    */     
/*  72:169 */     String path = file.getParent();
/*  73:170 */     if (path != null) {
/*  74:171 */       path = path + File.separator;
/*  75:    */     } else {
/*  76:173 */       path = "";
/*  77:    */     }
/*  78:    */     String fileStem;
/*  79:175 */     if (fname.indexOf('.') < 0)
/*  80:    */     {
/*  81:176 */       String fileStem = fname;
/*  82:177 */       fname = fname + ".names";
/*  83:    */     }
/*  84:    */     else
/*  85:    */     {
/*  86:179 */       fileStem = fname.substring(0, fname.lastIndexOf('.'));
/*  87:180 */       fname = fileStem + ".names";
/*  88:    */     }
/*  89:182 */     this.m_fileStem = fileStem;
/*  90:183 */     file = new File(path + fname);
/*  91:    */     
/*  92:185 */     this.m_sourceFile = file;
/*  93:    */     try
/*  94:    */     {
/*  95:187 */       BufferedReader br = new BufferedReader(new FileReader(file));
/*  96:188 */       this.m_namesReader = br;
/*  97:    */     }
/*  98:    */     catch (FileNotFoundException ex)
/*  99:    */     {
/* 100:190 */       throw new IOException("File not found : " + path + fname);
/* 101:    */     }
/* 102:193 */     this.m_sourceFileData = new File(path + fileStem + ".data");
/* 103:    */     try
/* 104:    */     {
/* 105:195 */       BufferedReader br = new BufferedReader(new FileReader(this.m_sourceFileData));
/* 106:196 */       this.m_dataReader = br;
/* 107:    */     }
/* 108:    */     catch (FileNotFoundException ex)
/* 109:    */     {
/* 110:198 */       throw new IOException("File not found : " + path + fname);
/* 111:    */     }
/* 112:200 */     this.m_File = file.getAbsolutePath();
/* 113:    */   }
/* 114:    */   
/* 115:    */   public Instances getStructure()
/* 116:    */     throws IOException
/* 117:    */   {
/* 118:212 */     if (this.m_sourceFile == null) {
/* 119:213 */       throw new IOException("No source has beenspecified");
/* 120:    */     }
/* 121:216 */     if (this.m_structure == null)
/* 122:    */     {
/* 123:217 */       setSource(this.m_sourceFile);
/* 124:218 */       StreamTokenizer st = new StreamTokenizer(this.m_namesReader);
/* 125:219 */       initTokenizer(st);
/* 126:220 */       readHeader(st);
/* 127:    */     }
/* 128:223 */     return this.m_structure;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public Instances getDataSet()
/* 132:    */     throws IOException
/* 133:    */   {
/* 134:236 */     if (this.m_sourceFile == null) {
/* 135:237 */       throw new IOException("No source has been specified");
/* 136:    */     }
/* 137:239 */     if (getRetrieval() == 2) {
/* 138:240 */       throw new IOException("Cannot mix getting Instances in both incremental and batch modes");
/* 139:    */     }
/* 140:243 */     setRetrieval(1);
/* 141:244 */     if (this.m_structure == null) {
/* 142:245 */       getStructure();
/* 143:    */     }
/* 144:247 */     StreamTokenizer st = new StreamTokenizer(this.m_dataReader);
/* 145:248 */     initTokenizer(st);
/* 146:    */     
/* 147:250 */     Instances result = new Instances(this.m_structure);
/* 148:251 */     Instance current = getInstance(st);
/* 149:253 */     while (current != null)
/* 150:    */     {
/* 151:254 */       result.add(current);
/* 152:255 */       current = getInstance(st);
/* 153:    */     }
/* 154:    */     try
/* 155:    */     {
/* 156:259 */       this.m_dataReader.close();
/* 157:    */     }
/* 158:    */     catch (Exception ex)
/* 159:    */     {
/* 160:262 */       ex.printStackTrace();
/* 161:    */     }
/* 162:264 */     return result;
/* 163:    */   }
/* 164:    */   
/* 165:    */   public Instance getNextInstance(Instances structure)
/* 166:    */     throws IOException
/* 167:    */   {
/* 168:285 */     if (this.m_sourceFile == null) {
/* 169:286 */       throw new IOException("No source has been specified");
/* 170:    */     }
/* 171:289 */     if (getRetrieval() == 1) {
/* 172:290 */       throw new IOException("Cannot mix getting Instances in both incremental and batch modes");
/* 173:    */     }
/* 174:293 */     setRetrieval(2);
/* 175:295 */     if (this.m_structure == null) {
/* 176:296 */       getStructure();
/* 177:    */     }
/* 178:299 */     StreamTokenizer st = new StreamTokenizer(this.m_dataReader);
/* 179:300 */     initTokenizer(st);
/* 180:    */     
/* 181:302 */     Instance nextI = getInstance(st);
/* 182:303 */     if (nextI != null) {
/* 183:304 */       nextI.setDataset(this.m_structure);
/* 184:    */     } else {
/* 185:    */       try
/* 186:    */       {
/* 187:308 */         this.m_dataReader.close();
/* 188:    */       }
/* 189:    */       catch (Exception ex)
/* 190:    */       {
/* 191:311 */         ex.printStackTrace();
/* 192:    */       }
/* 193:    */     }
/* 194:314 */     return nextI;
/* 195:    */   }
/* 196:    */   
/* 197:    */   private Instance getInstance(StreamTokenizer tokenizer)
/* 198:    */     throws IOException
/* 199:    */   {
/* 200:325 */     double[] instance = new double[this.m_structure.numAttributes()];
/* 201:    */     
/* 202:327 */     StreamTokenizerUtils.getFirstToken(tokenizer);
/* 203:328 */     if (tokenizer.ttype == -1) {
/* 204:329 */       return null;
/* 205:    */     }
/* 206:332 */     int counter = 0;
/* 207:333 */     for (int i = 0; i < this.m_numAttribs; i++)
/* 208:    */     {
/* 209:334 */       if (i > 0) {
/* 210:335 */         StreamTokenizerUtils.getToken(tokenizer);
/* 211:    */       }
/* 212:338 */       if (this.m_ignore[i] == 0) {
/* 213:340 */         if (tokenizer.ttype == 63)
/* 214:    */         {
/* 215:341 */           instance[(counter++)] = Utils.missingValue();
/* 216:    */         }
/* 217:    */         else
/* 218:    */         {
/* 219:343 */           String val = tokenizer.sval;
/* 220:345 */           if (i == this.m_numAttribs - 1) {
/* 221:347 */             if (val.charAt(val.length() - 1) == '.') {
/* 222:348 */               val = val.substring(0, val.length() - 1);
/* 223:    */             }
/* 224:    */           }
/* 225:351 */           if (this.m_structure.attribute(counter).isNominal())
/* 226:    */           {
/* 227:352 */             int index = this.m_structure.attribute(counter).indexOfValue(val);
/* 228:353 */             if (index == -1) {
/* 229:354 */               StreamTokenizerUtils.errms(tokenizer, "nominal value not declared in header :" + val + " column " + i);
/* 230:    */             }
/* 231:358 */             instance[(counter++)] = index;
/* 232:    */           }
/* 233:359 */           else if (this.m_structure.attribute(counter).isNumeric())
/* 234:    */           {
/* 235:    */             try
/* 236:    */             {
/* 237:361 */               instance[(counter++)] = Double.valueOf(val).doubleValue();
/* 238:    */             }
/* 239:    */             catch (NumberFormatException e)
/* 240:    */             {
/* 241:363 */               StreamTokenizerUtils.errms(tokenizer, "number expected");
/* 242:    */             }
/* 243:    */           }
/* 244:    */           else
/* 245:    */           {
/* 246:366 */             System.err.println("Shouldn't get here");
/* 247:367 */             System.exit(1);
/* 248:    */           }
/* 249:    */         }
/* 250:    */       }
/* 251:    */     }
/* 252:373 */     return new DenseInstance(1.0D, instance);
/* 253:    */   }
/* 254:    */   
/* 255:    */   private String removeTrailingPeriod(String val)
/* 256:    */   {
/* 257:384 */     if (val.charAt(val.length() - 1) == '.') {
/* 258:385 */       val = val.substring(0, val.length() - 1);
/* 259:    */     }
/* 260:387 */     return val;
/* 261:    */   }
/* 262:    */   
/* 263:    */   private void readHeader(StreamTokenizer tokenizer)
/* 264:    */     throws IOException
/* 265:    */   {
/* 266:398 */     ArrayList<Attribute> attribDefs = new ArrayList();
/* 267:399 */     ArrayList<Integer> ignores = new ArrayList();
/* 268:400 */     StreamTokenizerUtils.getFirstToken(tokenizer);
/* 269:401 */     if (tokenizer.ttype == -1) {
/* 270:402 */       StreamTokenizerUtils.errms(tokenizer, "premature end of file");
/* 271:    */     }
/* 272:405 */     this.m_numAttribs = 1;
/* 273:    */     
/* 274:407 */     ArrayList<String> classVals = new ArrayList();
/* 275:408 */     while (tokenizer.ttype != 10)
/* 276:    */     {
/* 277:409 */       String val = tokenizer.sval.trim();
/* 278:411 */       if (val.length() > 0)
/* 279:    */       {
/* 280:412 */         val = removeTrailingPeriod(val);
/* 281:413 */         classVals.add(val);
/* 282:    */       }
/* 283:415 */       StreamTokenizerUtils.getToken(tokenizer);
/* 284:    */     }
/* 285:419 */     int counter = 0;
/* 286:420 */     while (tokenizer.ttype != -1)
/* 287:    */     {
/* 288:421 */       StreamTokenizerUtils.getFirstToken(tokenizer);
/* 289:422 */       if (tokenizer.ttype != -1)
/* 290:    */       {
/* 291:424 */         String attribName = tokenizer.sval;
/* 292:    */         
/* 293:426 */         StreamTokenizerUtils.getToken(tokenizer);
/* 294:427 */         if (tokenizer.ttype == 10) {
/* 295:428 */           StreamTokenizerUtils.errms(tokenizer, "premature end of line. Expected attribute type.");
/* 296:    */         }
/* 297:431 */         String temp = tokenizer.sval.toLowerCase().trim();
/* 298:432 */         if ((temp.startsWith("ignore")) || (temp.startsWith("label")))
/* 299:    */         {
/* 300:433 */           ignores.add(new Integer(counter));
/* 301:434 */           counter++;
/* 302:    */         }
/* 303:435 */         else if (temp.startsWith("continuous"))
/* 304:    */         {
/* 305:436 */           attribDefs.add(new Attribute(attribName));
/* 306:437 */           counter++;
/* 307:    */         }
/* 308:    */         else
/* 309:    */         {
/* 310:439 */           counter++;
/* 311:    */           
/* 312:441 */           ArrayList<String> attribVals = new ArrayList();
/* 313:443 */           while ((tokenizer.ttype != 10) && (tokenizer.ttype != -1))
/* 314:    */           {
/* 315:444 */             String val = tokenizer.sval.trim();
/* 316:446 */             if (val.length() > 0)
/* 317:    */             {
/* 318:447 */               val = removeTrailingPeriod(val);
/* 319:448 */               attribVals.add(val);
/* 320:    */             }
/* 321:450 */             StreamTokenizerUtils.getToken(tokenizer);
/* 322:    */           }
/* 323:452 */           attribDefs.add(new Attribute(attribName, attribVals));
/* 324:    */         }
/* 325:    */       }
/* 326:    */     }
/* 327:457 */     boolean ok = true;
/* 328:458 */     int i = -1;
/* 329:459 */     if (classVals.size() == 1) {
/* 330:461 */       for (i = 0; i < attribDefs.size(); i++) {
/* 331:462 */         if (((Attribute)attribDefs.get(i)).name().compareTo((String)classVals.get(0)) == 0)
/* 332:    */         {
/* 333:463 */           ok = false;
/* 334:464 */           this.m_numAttribs -= 1;
/* 335:465 */           break;
/* 336:    */         }
/* 337:    */       }
/* 338:    */     }
/* 339:470 */     if (ok) {
/* 340:471 */       attribDefs.add(new Attribute("Class", classVals));
/* 341:    */     }
/* 342:474 */     this.m_structure = new Instances(this.m_fileStem, attribDefs, 0);
/* 343:    */     try
/* 344:    */     {
/* 345:477 */       if (ok) {
/* 346:478 */         this.m_structure.setClassIndex(this.m_structure.numAttributes() - 1);
/* 347:    */       } else {
/* 348:480 */         this.m_structure.setClassIndex(i);
/* 349:    */       }
/* 350:    */     }
/* 351:    */     catch (Exception ex)
/* 352:    */     {
/* 353:483 */       ex.printStackTrace();
/* 354:    */     }
/* 355:486 */     this.m_numAttribs = (this.m_structure.numAttributes() + ignores.size());
/* 356:487 */     this.m_ignore = new boolean[this.m_numAttribs];
/* 357:488 */     for (i = 0; i < ignores.size(); i++) {
/* 358:489 */       this.m_ignore[((Integer)ignores.get(i)).intValue()] = true;
/* 359:    */     }
/* 360:    */   }
/* 361:    */   
/* 362:    */   private void initTokenizer(StreamTokenizer tokenizer)
/* 363:    */   {
/* 364:499 */     tokenizer.resetSyntax();
/* 365:500 */     tokenizer.whitespaceChars(0, 31);
/* 366:501 */     tokenizer.wordChars(32, 255);
/* 367:502 */     tokenizer.whitespaceChars(44, 44);
/* 368:503 */     tokenizer.whitespaceChars(58, 58);
/* 369:    */     
/* 370:505 */     tokenizer.commentChar(124);
/* 371:506 */     tokenizer.whitespaceChars(9, 9);
/* 372:507 */     tokenizer.quoteChar(34);
/* 373:508 */     tokenizer.quoteChar(39);
/* 374:509 */     tokenizer.eolIsSignificant(true);
/* 375:    */   }
/* 376:    */   
/* 377:    */   public String getRevision()
/* 378:    */   {
/* 379:519 */     return RevisionUtils.extract("$Revision: 9290 $");
/* 380:    */   }
/* 381:    */   
/* 382:    */   public static void main(String[] args)
/* 383:    */   {
/* 384:528 */     runFileLoader(new C45Loader(), args);
/* 385:    */   }
/* 386:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.C45Loader
 * JD-Core Version:    0.7.0.1
 */