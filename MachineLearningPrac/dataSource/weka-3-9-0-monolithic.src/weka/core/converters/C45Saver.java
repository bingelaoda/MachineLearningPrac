/*   1:    */ package weka.core.converters;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.io.PrintWriter;
/*   7:    */ import java.util.Collections;
/*   8:    */ import java.util.Enumeration;
/*   9:    */ import java.util.Vector;
/*  10:    */ import weka.core.Attribute;
/*  11:    */ import weka.core.Capabilities;
/*  12:    */ import weka.core.Capabilities.Capability;
/*  13:    */ import weka.core.Instance;
/*  14:    */ import weka.core.Instances;
/*  15:    */ import weka.core.Option;
/*  16:    */ import weka.core.OptionHandler;
/*  17:    */ import weka.core.RevisionUtils;
/*  18:    */ import weka.core.Utils;
/*  19:    */ 
/*  20:    */ public class C45Saver
/*  21:    */   extends AbstractFileSaver
/*  22:    */   implements BatchConverter, IncrementalConverter, OptionHandler
/*  23:    */ {
/*  24:    */   static final long serialVersionUID = -821428878384253377L;
/*  25:    */   
/*  26:    */   public C45Saver()
/*  27:    */   {
/*  28: 81 */     resetOptions();
/*  29:    */   }
/*  30:    */   
/*  31:    */   public String globalInfo()
/*  32:    */   {
/*  33: 91 */     return "Writes to a destination that is in the format used by the C4.5 algorithm.\nTherefore it outputs a names and a data file.";
/*  34:    */   }
/*  35:    */   
/*  36:    */   public String getFileDescription()
/*  37:    */   {
/*  38:101 */     return "C4.5 file format";
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void resetOptions()
/*  42:    */   {
/*  43:110 */     super.resetOptions();
/*  44:111 */     setFileExtension(".names");
/*  45:    */   }
/*  46:    */   
/*  47:    */   public Capabilities getCapabilities()
/*  48:    */   {
/*  49:122 */     Capabilities result = super.getCapabilities();
/*  50:    */     
/*  51:    */ 
/*  52:125 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  53:126 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  54:127 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  55:128 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  56:    */     
/*  57:    */ 
/*  58:131 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  59:132 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*  60:133 */     result.enable(Capabilities.Capability.DATE_CLASS);
/*  61:134 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  62:    */     
/*  63:136 */     return result;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public void writeIncremental(Instance inst)
/*  67:    */     throws IOException
/*  68:    */   {
/*  69:150 */     int writeMode = getWriteMode();
/*  70:151 */     Instances structure = getInstances();
/*  71:152 */     PrintWriter outW = null;
/*  72:154 */     if (structure != null)
/*  73:    */     {
/*  74:155 */       if (structure.classIndex() == -1)
/*  75:    */       {
/*  76:156 */         structure.setClassIndex(structure.numAttributes() - 1);
/*  77:157 */         System.err.println("No class specified. Last attribute is used as class attribute.");
/*  78:    */       }
/*  79:160 */       if (structure.attribute(structure.classIndex()).isNumeric()) {
/*  80:161 */         throw new IOException("To save in C4.5 format the class attribute cannot be numeric.");
/*  81:    */       }
/*  82:    */     }
/*  83:165 */     if ((getRetrieval() == 1) || (getRetrieval() == 0)) {
/*  84:166 */       throw new IOException("Batch and incremental saving cannot be mixed.");
/*  85:    */     }
/*  86:168 */     if ((retrieveFile() == null) || (getWriter() == null)) {
/*  87:169 */       throw new IOException("C4.5 format requires two files. Therefore no output to standard out can be generated.\nPlease specifiy output files using the -o option.");
/*  88:    */     }
/*  89:173 */     outW = new PrintWriter(getWriter());
/*  90:175 */     if (writeMode == 1)
/*  91:    */     {
/*  92:176 */       if (structure == null)
/*  93:    */       {
/*  94:177 */         setWriteMode(2);
/*  95:178 */         if (inst != null) {
/*  96:179 */           System.err.println("Structure(Header Information) has to be set in advance");
/*  97:    */         }
/*  98:    */       }
/*  99:    */       else
/* 100:    */       {
/* 101:183 */         setWriteMode(3);
/* 102:    */       }
/* 103:185 */       writeMode = getWriteMode();
/* 104:    */     }
/* 105:187 */     if (writeMode == 2)
/* 106:    */     {
/* 107:188 */       if (outW != null) {
/* 108:189 */         outW.close();
/* 109:    */       }
/* 110:191 */       cancel();
/* 111:    */     }
/* 112:193 */     if (writeMode == 3)
/* 113:    */     {
/* 114:194 */       setWriteMode(0);
/* 115:196 */       for (int i = 0; i < structure.attribute(structure.classIndex()).numValues(); i++)
/* 116:    */       {
/* 117:198 */         outW.write(structure.attribute(structure.classIndex()).value(i));
/* 118:199 */         if (i < structure.attribute(structure.classIndex()).numValues() - 1) {
/* 119:200 */           outW.write(",");
/* 120:    */         } else {
/* 121:202 */           outW.write(".\n");
/* 122:    */         }
/* 123:    */       }
/* 124:205 */       for (int i = 0; i < structure.numAttributes(); i++) {
/* 125:206 */         if (i != structure.classIndex())
/* 126:    */         {
/* 127:207 */           outW.write(structure.attribute(i).name() + ": ");
/* 128:208 */           if ((structure.attribute(i).isNumeric()) || (structure.attribute(i).isDate()))
/* 129:    */           {
/* 130:210 */             outW.write("continuous.\n");
/* 131:    */           }
/* 132:    */           else
/* 133:    */           {
/* 134:212 */             Attribute temp = structure.attribute(i);
/* 135:213 */             for (int j = 0; j < temp.numValues(); j++)
/* 136:    */             {
/* 137:214 */               outW.write(temp.value(j));
/* 138:215 */               if (j < temp.numValues() - 1) {
/* 139:216 */                 outW.write(",");
/* 140:    */               } else {
/* 141:218 */                 outW.write(".\n");
/* 142:    */               }
/* 143:    */             }
/* 144:    */           }
/* 145:    */         }
/* 146:    */       }
/* 147:224 */       outW.flush();
/* 148:225 */       outW.close();
/* 149:    */       
/* 150:227 */       writeMode = getWriteMode();
/* 151:    */       
/* 152:229 */       String out = retrieveFile().getAbsolutePath();
/* 153:230 */       setFileExtension(".data");
/* 154:231 */       out = out.substring(0, out.lastIndexOf('.')) + getFileExtension();
/* 155:232 */       File namesFile = new File(out);
/* 156:    */       try
/* 157:    */       {
/* 158:234 */         setFile(namesFile);
/* 159:    */       }
/* 160:    */       catch (Exception ex)
/* 161:    */       {
/* 162:236 */         throw new IOException("Cannot create data file, only names file created.");
/* 163:    */       }
/* 164:239 */       if ((retrieveFile() == null) || (getWriter() == null)) {
/* 165:240 */         throw new IOException("Cannot create data file, only names file created.");
/* 166:    */       }
/* 167:243 */       outW = new PrintWriter(getWriter());
/* 168:    */     }
/* 169:245 */     if (writeMode == 0)
/* 170:    */     {
/* 171:246 */       if (structure == null) {
/* 172:247 */         throw new IOException("No instances information available.");
/* 173:    */       }
/* 174:249 */       if (inst != null)
/* 175:    */       {
/* 176:251 */         for (int j = 0; j < inst.numAttributes(); j++) {
/* 177:252 */           if (j != structure.classIndex()) {
/* 178:253 */             if (inst.isMissing(j)) {
/* 179:254 */               outW.write("?,");
/* 180:255 */             } else if ((structure.attribute(j).isNominal()) || (structure.attribute(j).isString())) {
/* 181:257 */               outW.write(structure.attribute(j).value((int)inst.value(j)) + ",");
/* 182:    */             } else {
/* 183:260 */               outW.write("" + inst.value(j) + ",");
/* 184:    */             }
/* 185:    */           }
/* 186:    */         }
/* 187:265 */         if (inst.isMissing(structure.classIndex())) {
/* 188:266 */           outW.write("?");
/* 189:    */         } else {
/* 190:268 */           outW.write(structure.attribute(structure.classIndex()).value((int)inst.value(structure.classIndex())));
/* 191:    */         }
/* 192:271 */         outW.write("\n");
/* 193:    */         
/* 194:273 */         this.m_incrementalCounter += 1;
/* 195:274 */         if (this.m_incrementalCounter > 100)
/* 196:    */         {
/* 197:275 */           this.m_incrementalCounter = 0;
/* 198:276 */           outW.flush();
/* 199:    */         }
/* 200:    */       }
/* 201:    */       else
/* 202:    */       {
/* 203:280 */         if (outW != null)
/* 204:    */         {
/* 205:281 */           outW.flush();
/* 206:282 */           outW.close();
/* 207:    */         }
/* 208:284 */         setFileExtension(".names");
/* 209:285 */         this.m_incrementalCounter = 0;
/* 210:286 */         resetStructure();
/* 211:287 */         outW = null;
/* 212:288 */         resetWriter();
/* 213:    */       }
/* 214:    */     }
/* 215:    */   }
/* 216:    */   
/* 217:    */   public void writeBatch()
/* 218:    */     throws IOException
/* 219:    */   {
/* 220:302 */     Instances instances = getInstances();
/* 221:304 */     if (instances == null) {
/* 222:305 */       throw new IOException("No instances to save");
/* 223:    */     }
/* 224:307 */     if (instances.classIndex() == -1)
/* 225:    */     {
/* 226:308 */       instances.setClassIndex(instances.numAttributes() - 1);
/* 227:309 */       System.err.println("No class specified. Last attribute is used as class attribute.");
/* 228:    */     }
/* 229:312 */     if (instances.attribute(instances.classIndex()).isNumeric()) {
/* 230:313 */       throw new IOException("To save in C4.5 format the class attribute cannot be numeric.");
/* 231:    */     }
/* 232:316 */     if (getRetrieval() == 2) {
/* 233:317 */       throw new IOException("Batch and incremental saving cannot be mixed.");
/* 234:    */     }
/* 235:320 */     setRetrieval(1);
/* 236:321 */     if ((retrieveFile() == null) || (getWriter() == null)) {
/* 237:322 */       throw new IOException("C4.5 format requires two files. Therefore no output to standard out can be generated.\nPlease specifiy output files using the -o option.");
/* 238:    */     }
/* 239:325 */     setWriteMode(0);
/* 240:    */     
/* 241:327 */     setFileExtension(".names");
/* 242:328 */     PrintWriter outW = new PrintWriter(getWriter());
/* 243:329 */     for (int i = 0; i < instances.attribute(instances.classIndex()).numValues(); i++)
/* 244:    */     {
/* 245:330 */       outW.write(instances.attribute(instances.classIndex()).value(i));
/* 246:331 */       if (i < instances.attribute(instances.classIndex()).numValues() - 1) {
/* 247:332 */         outW.write(",");
/* 248:    */       } else {
/* 249:334 */         outW.write(".\n");
/* 250:    */       }
/* 251:    */     }
/* 252:337 */     for (int i = 0; i < instances.numAttributes(); i++) {
/* 253:338 */       if (i != instances.classIndex())
/* 254:    */       {
/* 255:339 */         outW.write(instances.attribute(i).name() + ": ");
/* 256:340 */         if ((instances.attribute(i).isNumeric()) || (instances.attribute(i).isDate()))
/* 257:    */         {
/* 258:342 */           outW.write("continuous.\n");
/* 259:    */         }
/* 260:    */         else
/* 261:    */         {
/* 262:344 */           Attribute temp = instances.attribute(i);
/* 263:345 */           for (int j = 0; j < temp.numValues(); j++)
/* 264:    */           {
/* 265:346 */             outW.write(temp.value(j));
/* 266:347 */             if (j < temp.numValues() - 1) {
/* 267:348 */               outW.write(",");
/* 268:    */             } else {
/* 269:350 */               outW.write(".\n");
/* 270:    */             }
/* 271:    */           }
/* 272:    */         }
/* 273:    */       }
/* 274:    */     }
/* 275:356 */     outW.flush();
/* 276:357 */     outW.close();
/* 277:    */     
/* 278:    */ 
/* 279:360 */     String out = retrieveFile().getAbsolutePath();
/* 280:361 */     setFileExtension(".data");
/* 281:362 */     out = out.substring(0, out.lastIndexOf('.')) + getFileExtension();
/* 282:363 */     File namesFile = new File(out);
/* 283:    */     try
/* 284:    */     {
/* 285:365 */       setFile(namesFile);
/* 286:    */     }
/* 287:    */     catch (Exception ex)
/* 288:    */     {
/* 289:367 */       throw new IOException("Cannot create data file, only names file created (Reason: " + ex.toString() + ").");
/* 290:    */     }
/* 291:371 */     if ((retrieveFile() == null) || (getWriter() == null)) {
/* 292:372 */       throw new IOException("Cannot create data file, only names file created.");
/* 293:    */     }
/* 294:374 */     outW = new PrintWriter(getWriter());
/* 295:376 */     for (int i = 0; i < instances.numInstances(); i++)
/* 296:    */     {
/* 297:377 */       Instance temp = instances.instance(i);
/* 298:378 */       for (int j = 0; j < temp.numAttributes(); j++) {
/* 299:379 */         if (j != instances.classIndex()) {
/* 300:380 */           if (temp.isMissing(j)) {
/* 301:381 */             outW.write("?,");
/* 302:382 */           } else if ((instances.attribute(j).isNominal()) || (instances.attribute(j).isString())) {
/* 303:384 */             outW.write(instances.attribute(j).value((int)temp.value(j)) + ",");
/* 304:    */           } else {
/* 305:386 */             outW.write("" + temp.value(j) + ",");
/* 306:    */           }
/* 307:    */         }
/* 308:    */       }
/* 309:391 */       if (temp.isMissing(instances.classIndex())) {
/* 310:392 */         outW.write("?");
/* 311:    */       } else {
/* 312:394 */         outW.write(instances.attribute(instances.classIndex()).value((int)temp.value(instances.classIndex())));
/* 313:    */       }
/* 314:397 */       outW.write("\n");
/* 315:    */     }
/* 316:399 */     outW.flush();
/* 317:400 */     outW.close();
/* 318:401 */     setFileExtension(".names");
/* 319:402 */     setWriteMode(1);
/* 320:403 */     outW = null;
/* 321:404 */     resetWriter();
/* 322:405 */     setWriteMode(2);
/* 323:    */   }
/* 324:    */   
/* 325:    */   public Enumeration<Option> listOptions()
/* 326:    */   {
/* 327:415 */     Vector<Option> result = new Vector();
/* 328:    */     
/* 329:417 */     result.addElement(new Option("The class index", "c", 1, "-c <the class index>"));
/* 330:    */     
/* 331:    */ 
/* 332:420 */     result.addAll(Collections.list(super.listOptions()));
/* 333:    */     
/* 334:422 */     return result.elements();
/* 335:    */   }
/* 336:    */   
/* 337:    */   public void setOptions(String[] options)
/* 338:    */     throws Exception
/* 339:    */   {
/* 340:455 */     String outputString = Utils.getOption('o', options);
/* 341:456 */     String inputString = Utils.getOption('i', options);
/* 342:457 */     String indexString = Utils.getOption('c', options);
/* 343:    */     
/* 344:459 */     ArffLoader loader = new ArffLoader();
/* 345:    */     
/* 346:461 */     resetOptions();
/* 347:    */     
/* 348:    */ 
/* 349:464 */     int index = -1;
/* 350:465 */     if (indexString.length() != 0) {
/* 351:466 */       if (indexString.equals("first")) {
/* 352:467 */         index = 0;
/* 353:469 */       } else if (indexString.equals("last")) {
/* 354:470 */         index = -1;
/* 355:    */       } else {
/* 356:472 */         index = Integer.parseInt(indexString);
/* 357:    */       }
/* 358:    */     }
/* 359:477 */     if (inputString.length() != 0) {
/* 360:    */       try
/* 361:    */       {
/* 362:479 */         File input = new File(inputString);
/* 363:480 */         loader.setFile(input);
/* 364:481 */         Instances inst = loader.getDataSet();
/* 365:482 */         if (index == -1) {
/* 366:483 */           inst.setClassIndex(inst.numAttributes() - 1);
/* 367:    */         } else {
/* 368:485 */           inst.setClassIndex(index);
/* 369:    */         }
/* 370:487 */         setInstances(inst);
/* 371:    */       }
/* 372:    */       catch (Exception ex)
/* 373:    */       {
/* 374:489 */         throw new IOException("No data set loaded. Data set has to be arff format (Reason: " + ex.toString() + ").");
/* 375:    */       }
/* 376:    */     } else {
/* 377:494 */       throw new IOException("No data set to save.");
/* 378:    */     }
/* 379:497 */     if (outputString.length() != 0)
/* 380:    */     {
/* 381:499 */       if (!outputString.endsWith(getFileExtension())) {
/* 382:500 */         if (outputString.lastIndexOf('.') != -1) {
/* 383:501 */           outputString = outputString.substring(0, outputString.lastIndexOf('.')) + getFileExtension();
/* 384:    */         } else {
/* 385:505 */           outputString = outputString + getFileExtension();
/* 386:    */         }
/* 387:    */       }
/* 388:    */       try
/* 389:    */       {
/* 390:509 */         File output = new File(outputString);
/* 391:510 */         setFile(output);
/* 392:    */       }
/* 393:    */       catch (Exception ex)
/* 394:    */       {
/* 395:512 */         throw new IOException("Cannot create output file.");
/* 396:    */       }
/* 397:    */     }
/* 398:516 */     if (index == -1) {
/* 399:517 */       index = getInstances().numAttributes() - 1;
/* 400:    */     }
/* 401:519 */     getInstances().setClassIndex(index);
/* 402:    */     
/* 403:521 */     super.setOptions(options);
/* 404:    */     
/* 405:523 */     Utils.checkForRemainingOptions(options);
/* 406:    */   }
/* 407:    */   
/* 408:    */   public String[] getOptions()
/* 409:    */   {
/* 410:534 */     Vector<String> options = new Vector();
/* 411:536 */     if (retrieveFile() != null)
/* 412:    */     {
/* 413:537 */       options.add("-o");
/* 414:538 */       options.add("" + retrieveFile());
/* 415:    */     }
/* 416:    */     else
/* 417:    */     {
/* 418:540 */       options.add("-o");
/* 419:541 */       options.add("");
/* 420:    */     }
/* 421:543 */     if (getInstances() != null)
/* 422:    */     {
/* 423:544 */       options.add("-i");
/* 424:545 */       options.add("" + getInstances().relationName());
/* 425:546 */       options.add("-c");
/* 426:547 */       options.add("" + getInstances().classIndex());
/* 427:    */     }
/* 428:    */     else
/* 429:    */     {
/* 430:549 */       options.add("-i");
/* 431:550 */       options.add("");
/* 432:551 */       options.add("-c");
/* 433:552 */       options.add("");
/* 434:    */     }
/* 435:555 */     Collections.addAll(options, super.getOptions());
/* 436:    */     
/* 437:557 */     return (String[])options.toArray(new String[0]);
/* 438:    */   }
/* 439:    */   
/* 440:    */   public String getRevision()
/* 441:    */   {
/* 442:567 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 443:    */   }
/* 444:    */   
/* 445:    */   public static void main(String[] args)
/* 446:    */   {
/* 447:576 */     runFileSaver(new C45Saver(), args);
/* 448:    */   }
/* 449:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.C45Saver
 * JD-Core Version:    0.7.0.1
 */