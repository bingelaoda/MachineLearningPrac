/*   1:    */ package weka.core.converters;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.io.PrintWriter;
/*   6:    */ import java.util.Collections;
/*   7:    */ import java.util.Enumeration;
/*   8:    */ import java.util.Vector;
/*   9:    */ import weka.core.AbstractInstance;
/*  10:    */ import weka.core.Attribute;
/*  11:    */ import weka.core.Capabilities;
/*  12:    */ import weka.core.Capabilities.Capability;
/*  13:    */ import weka.core.DenseInstance;
/*  14:    */ import weka.core.Instance;
/*  15:    */ import weka.core.Instances;
/*  16:    */ import weka.core.Option;
/*  17:    */ import weka.core.RevisionUtils;
/*  18:    */ import weka.core.SparseInstance;
/*  19:    */ import weka.core.Utils;
/*  20:    */ 
/*  21:    */ public class CSVSaver
/*  22:    */   extends AbstractFileSaver
/*  23:    */   implements BatchConverter, IncrementalConverter, FileSourcedConverter
/*  24:    */ {
/*  25:    */   static final long serialVersionUID = 476636654410701807L;
/*  26: 98 */   protected String m_FieldSeparator = ",";
/*  27:101 */   protected String m_MissingValue = "?";
/*  28:104 */   protected int m_MaxDecimalPlaces = AbstractInstance.s_numericAfterDecimalPoint;
/*  29:107 */   protected boolean m_noHeaderRow = false;
/*  30:    */   
/*  31:    */   public CSVSaver()
/*  32:    */   {
/*  33:113 */     resetOptions();
/*  34:    */   }
/*  35:    */   
/*  36:    */   public String globalInfo()
/*  37:    */   {
/*  38:123 */     return "Writes to a destination that is in CSV (comma-separated values) format. The column separator can be chosen (default is ',') as well as the value representing missing values (default is '?').";
/*  39:    */   }
/*  40:    */   
/*  41:    */   public Enumeration<Option> listOptions()
/*  42:    */   {
/*  43:135 */     Vector<Option> result = new Vector();
/*  44:    */     
/*  45:137 */     result.addElement(new Option("\tThe field separator to be used.\n\t'\\t' can be used as well.\n\t(default: ',')", "F", 1, "-F <separator>"));
/*  46:    */     
/*  47:    */ 
/*  48:    */ 
/*  49:141 */     result.addElement(new Option("\tThe string representing a missing value.\n\t(default: ?)", "M", 1, "-M <str>"));
/*  50:    */     
/*  51:    */ 
/*  52:144 */     result.addElement(new Option("\tDon't write a header row.", "N", 0, "-N"));
/*  53:    */     
/*  54:146 */     result.addElement(new Option("\tThe maximum number of digits to print after the decimal\n\tplace for numeric values (default: 6)", "decimal", 1, "-decimal <num>"));
/*  55:    */     
/*  56:    */ 
/*  57:    */ 
/*  58:    */ 
/*  59:151 */     result.addAll(Collections.list(super.listOptions()));
/*  60:    */     
/*  61:153 */     return result.elements();
/*  62:    */   }
/*  63:    */   
/*  64:    */   public void setOptions(String[] options)
/*  65:    */     throws Exception
/*  66:    */   {
/*  67:203 */     String tmpStr = Utils.getOption('F', options);
/*  68:204 */     if (tmpStr.length() != 0) {
/*  69:205 */       setFieldSeparator(tmpStr);
/*  70:    */     } else {
/*  71:207 */       setFieldSeparator(",");
/*  72:    */     }
/*  73:210 */     tmpStr = Utils.getOption('M', options);
/*  74:211 */     if (tmpStr.length() != 0) {
/*  75:212 */       setMissingValue(tmpStr);
/*  76:    */     } else {
/*  77:214 */       setMissingValue("?");
/*  78:    */     }
/*  79:217 */     setNoHeaderRow(Utils.getFlag('N', options));
/*  80:    */     
/*  81:219 */     tmpStr = Utils.getOption("decimal", options);
/*  82:220 */     if (tmpStr.length() > 0) {
/*  83:221 */       setMaxDecimalPlaces(Integer.parseInt(tmpStr));
/*  84:    */     }
/*  85:224 */     super.setOptions(options);
/*  86:    */     
/*  87:226 */     Utils.checkForRemainingOptions(options);
/*  88:    */   }
/*  89:    */   
/*  90:    */   public String[] getOptions()
/*  91:    */   {
/*  92:236 */     Vector<String> result = new Vector();
/*  93:    */     
/*  94:238 */     result.add("-F");
/*  95:239 */     result.add(getFieldSeparator());
/*  96:    */     
/*  97:241 */     result.add("-M");
/*  98:242 */     result.add(getMissingValue());
/*  99:244 */     if (getNoHeaderRow()) {
/* 100:245 */       result.add("-N");
/* 101:    */     }
/* 102:248 */     result.add("-decimal");
/* 103:249 */     result.add("" + getMaxDecimalPlaces());
/* 104:    */     
/* 105:251 */     Collections.addAll(result, super.getOptions());
/* 106:    */     
/* 107:253 */     return (String[])result.toArray(new String[result.size()]);
/* 108:    */   }
/* 109:    */   
/* 110:    */   public String noHeaderRowTipText()
/* 111:    */   {
/* 112:263 */     return "If true then the header row is not written";
/* 113:    */   }
/* 114:    */   
/* 115:    */   public void setNoHeaderRow(boolean b)
/* 116:    */   {
/* 117:272 */     this.m_noHeaderRow = b;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public boolean getNoHeaderRow()
/* 121:    */   {
/* 122:281 */     return this.m_noHeaderRow;
/* 123:    */   }
/* 124:    */   
/* 125:    */   public void setMaxDecimalPlaces(int maxDecimal)
/* 126:    */   {
/* 127:290 */     this.m_MaxDecimalPlaces = maxDecimal;
/* 128:    */   }
/* 129:    */   
/* 130:    */   public int getMaxDecimalPlaces()
/* 131:    */   {
/* 132:299 */     return this.m_MaxDecimalPlaces;
/* 133:    */   }
/* 134:    */   
/* 135:    */   public String maxDecimalPlacesTipText()
/* 136:    */   {
/* 137:309 */     return "The maximum number of digits to print after the decimal point for numeric values";
/* 138:    */   }
/* 139:    */   
/* 140:    */   public void setFieldSeparator(String value)
/* 141:    */   {
/* 142:319 */     this.m_FieldSeparator = Utils.unbackQuoteChars(value);
/* 143:    */   }
/* 144:    */   
/* 145:    */   public String getFieldSeparator()
/* 146:    */   {
/* 147:334 */     return Utils.backQuoteChars(this.m_FieldSeparator);
/* 148:    */   }
/* 149:    */   
/* 150:    */   public String fieldSeparatorTipText()
/* 151:    */   {
/* 152:344 */     return "The character to use as separator for the columns/fields (use '\\t' for TAB).";
/* 153:    */   }
/* 154:    */   
/* 155:    */   public void setMissingValue(String value)
/* 156:    */   {
/* 157:353 */     this.m_MissingValue = value;
/* 158:    */   }
/* 159:    */   
/* 160:    */   public String getMissingValue()
/* 161:    */   {
/* 162:362 */     return this.m_MissingValue;
/* 163:    */   }
/* 164:    */   
/* 165:    */   public String missingValueTipText()
/* 166:    */   {
/* 167:372 */     return "The placeholder for missing values, default is '?'.";
/* 168:    */   }
/* 169:    */   
/* 170:    */   public String getFileDescription()
/* 171:    */   {
/* 172:382 */     return "CSV file: comma separated files";
/* 173:    */   }
/* 174:    */   
/* 175:    */   public void resetOptions()
/* 176:    */   {
/* 177:390 */     super.resetOptions();
/* 178:    */     
/* 179:392 */     setFileExtension(".csv");
/* 180:    */   }
/* 181:    */   
/* 182:    */   public Capabilities getCapabilities()
/* 183:    */   {
/* 184:403 */     Capabilities result = super.getCapabilities();
/* 185:    */     
/* 186:    */ 
/* 187:406 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 188:407 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 189:408 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/* 190:409 */     result.enable(Capabilities.Capability.STRING_ATTRIBUTES);
/* 191:410 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 192:    */     
/* 193:    */ 
/* 194:413 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 195:414 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/* 196:415 */     result.enable(Capabilities.Capability.DATE_CLASS);
/* 197:416 */     result.enable(Capabilities.Capability.STRING_CLASS);
/* 198:417 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 199:418 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 200:    */     
/* 201:420 */     return result;
/* 202:    */   }
/* 203:    */   
/* 204:    */   public void writeIncremental(Instance inst)
/* 205:    */     throws IOException
/* 206:    */   {
/* 207:434 */     int writeMode = getWriteMode();
/* 208:435 */     Instances structure = getInstances();
/* 209:436 */     PrintWriter outW = null;
/* 210:438 */     if ((getRetrieval() == 1) || (getRetrieval() == 0)) {
/* 211:439 */       throw new IOException("Batch and incremental saving cannot be mixed.");
/* 212:    */     }
/* 213:441 */     if (getWriter() != null) {
/* 214:442 */       outW = new PrintWriter(getWriter());
/* 215:    */     }
/* 216:445 */     if (writeMode == 1)
/* 217:    */     {
/* 218:446 */       if (structure == null)
/* 219:    */       {
/* 220:447 */         setWriteMode(2);
/* 221:448 */         if (inst != null) {
/* 222:449 */           System.err.println("Structure(Header Information) has to be set in advance");
/* 223:    */         }
/* 224:    */       }
/* 225:    */       else
/* 226:    */       {
/* 227:453 */         setWriteMode(3);
/* 228:    */       }
/* 229:455 */       writeMode = getWriteMode();
/* 230:    */     }
/* 231:457 */     if (writeMode == 2)
/* 232:    */     {
/* 233:458 */       if (outW != null) {
/* 234:459 */         outW.close();
/* 235:    */       }
/* 236:461 */       cancel();
/* 237:    */     }
/* 238:463 */     if (writeMode == 3)
/* 239:    */     {
/* 240:464 */       setWriteMode(0);
/* 241:466 */       if (!getNoHeaderRow()) {
/* 242:467 */         if ((retrieveFile() == null) && (outW == null))
/* 243:    */         {
/* 244:469 */           for (int i = 0; i < structure.numAttributes(); i++)
/* 245:    */           {
/* 246:470 */             System.out.print(structure.attribute(i).name());
/* 247:471 */             if (i < structure.numAttributes() - 1) {
/* 248:472 */               System.out.print(this.m_FieldSeparator);
/* 249:    */             } else {
/* 250:474 */               System.out.println();
/* 251:    */             }
/* 252:    */           }
/* 253:    */         }
/* 254:    */         else
/* 255:    */         {
/* 256:478 */           for (int i = 0; i < structure.numAttributes(); i++)
/* 257:    */           {
/* 258:479 */             outW.print(structure.attribute(i).name());
/* 259:480 */             if (i < structure.numAttributes() - 1) {
/* 260:481 */               outW.print(this.m_FieldSeparator);
/* 261:    */             } else {
/* 262:483 */               outW.println();
/* 263:    */             }
/* 264:    */           }
/* 265:486 */           outW.flush();
/* 266:    */         }
/* 267:    */       }
/* 268:489 */       writeMode = getWriteMode();
/* 269:    */     }
/* 270:491 */     if (writeMode == 0)
/* 271:    */     {
/* 272:492 */       if (structure == null) {
/* 273:493 */         throw new IOException("No instances information available.");
/* 274:    */       }
/* 275:495 */       if (inst != null)
/* 276:    */       {
/* 277:497 */         if ((retrieveFile() == null) && (outW == null))
/* 278:    */         {
/* 279:498 */           System.out.println(inst);
/* 280:    */         }
/* 281:    */         else
/* 282:    */         {
/* 283:500 */           outW.println(instanceToString(inst));
/* 284:    */           
/* 285:502 */           this.m_incrementalCounter += 1;
/* 286:503 */           if (this.m_incrementalCounter > 100)
/* 287:    */           {
/* 288:504 */             this.m_incrementalCounter = 0;
/* 289:505 */             outW.flush();
/* 290:    */           }
/* 291:    */         }
/* 292:    */       }
/* 293:    */       else
/* 294:    */       {
/* 295:510 */         if (outW != null)
/* 296:    */         {
/* 297:511 */           outW.flush();
/* 298:512 */           outW.close();
/* 299:    */         }
/* 300:514 */         this.m_incrementalCounter = 0;
/* 301:515 */         resetStructure();
/* 302:516 */         outW = null;
/* 303:517 */         resetWriter();
/* 304:    */       }
/* 305:    */     }
/* 306:    */   }
/* 307:    */   
/* 308:    */   public void writeBatch()
/* 309:    */     throws IOException
/* 310:    */   {
/* 311:531 */     if (getInstances() == null) {
/* 312:532 */       throw new IOException("No instances to save");
/* 313:    */     }
/* 314:534 */     if (getRetrieval() == 2) {
/* 315:535 */       throw new IOException("Batch and incremental saving cannot be mixed.");
/* 316:    */     }
/* 317:537 */     setRetrieval(1);
/* 318:538 */     setWriteMode(0);
/* 319:539 */     if ((retrieveFile() == null) && (getWriter() == null))
/* 320:    */     {
/* 321:541 */       if (!getNoHeaderRow()) {
/* 322:543 */         for (int i = 0; i < getInstances().numAttributes(); i++)
/* 323:    */         {
/* 324:544 */           System.out.print(getInstances().attribute(i).name());
/* 325:545 */           if (i < getInstances().numAttributes() - 1) {
/* 326:546 */             System.out.print(this.m_FieldSeparator);
/* 327:    */           } else {
/* 328:548 */             System.out.println();
/* 329:    */           }
/* 330:    */         }
/* 331:    */       }
/* 332:552 */       for (int i = 0; i < getInstances().numInstances(); i++) {
/* 333:553 */         System.out.println(instanceToString(getInstances().instance(i)));
/* 334:    */       }
/* 335:555 */       setWriteMode(1);
/* 336:556 */       return;
/* 337:    */     }
/* 338:558 */     PrintWriter outW = new PrintWriter(getWriter());
/* 339:559 */     if (!getNoHeaderRow()) {
/* 340:561 */       for (int i = 0; i < getInstances().numAttributes(); i++)
/* 341:    */       {
/* 342:562 */         outW.print(Utils.quote(getInstances().attribute(i).name()));
/* 343:563 */         if (i < getInstances().numAttributes() - 1) {
/* 344:564 */           outW.print(this.m_FieldSeparator);
/* 345:    */         } else {
/* 346:566 */           outW.println();
/* 347:    */         }
/* 348:    */       }
/* 349:    */     }
/* 350:570 */     for (int i = 0; i < getInstances().numInstances(); i++) {
/* 351:571 */       outW.println(instanceToString(getInstances().instance(i)));
/* 352:    */     }
/* 353:573 */     outW.flush();
/* 354:574 */     outW.close();
/* 355:575 */     setWriteMode(1);
/* 356:576 */     outW = null;
/* 357:577 */     resetWriter();
/* 358:578 */     setWriteMode(2);
/* 359:    */   }
/* 360:    */   
/* 361:    */   protected String instanceToString(Instance inst)
/* 362:    */   {
/* 363:593 */     StringBuffer result = new StringBuffer();
/* 364:    */     Instance outInst;
/* 365:595 */     if ((inst instanceof SparseInstance))
/* 366:    */     {
/* 367:596 */       Instance outInst = new DenseInstance(inst.weight(), inst.toDoubleArray());
/* 368:597 */       outInst.setDataset(inst.dataset());
/* 369:    */     }
/* 370:    */     else
/* 371:    */     {
/* 372:599 */       outInst = inst;
/* 373:    */     }
/* 374:602 */     for (int i = 0; i < outInst.numAttributes(); i++)
/* 375:    */     {
/* 376:603 */       if (i > 0) {
/* 377:604 */         result.append(this.m_FieldSeparator);
/* 378:    */       }
/* 379:    */       String field;
/* 380:    */       String field;
/* 381:607 */       if (outInst.isMissing(i)) {
/* 382:608 */         field = this.m_MissingValue;
/* 383:    */       } else {
/* 384:610 */         field = outInst.toString(i, this.m_MaxDecimalPlaces);
/* 385:    */       }
/* 386:615 */       if ((this.m_FieldSeparator.length() == 1) && (field.indexOf(this.m_FieldSeparator) > -1) && (!field.startsWith("'")) && (!field.endsWith("'"))) {
/* 387:618 */         field = "'" + field + "'";
/* 388:    */       }
/* 389:621 */       result.append(field);
/* 390:    */     }
/* 391:624 */     return result.toString();
/* 392:    */   }
/* 393:    */   
/* 394:    */   public String getRevision()
/* 395:    */   {
/* 396:634 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 397:    */   }
/* 398:    */   
/* 399:    */   public static void main(String[] args)
/* 400:    */   {
/* 401:643 */     runFileSaver(new CSVSaver(), args);
/* 402:    */   }
/* 403:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.CSVSaver
 * JD-Core Version:    0.7.0.1
 */