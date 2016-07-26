/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.Capabilities;
/*   9:    */ import weka.core.Capabilities.Capability;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.Option;
/*  13:    */ import weka.core.Range;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.Utils;
/*  16:    */ import weka.filters.SimpleStreamFilter;
/*  17:    */ 
/*  18:    */ public class NumericCleaner
/*  19:    */   extends SimpleStreamFilter
/*  20:    */ {
/*  21:    */   private static final long serialVersionUID = -352890679895066592L;
/*  22:120 */   protected double m_MinThreshold = -1.797693134862316E+308D;
/*  23:123 */   protected double m_MinDefault = -1.797693134862316E+308D;
/*  24:126 */   protected double m_MaxThreshold = 1.7976931348623157E+308D;
/*  25:129 */   protected double m_MaxDefault = 1.7976931348623157E+308D;
/*  26:132 */   protected double m_CloseTo = 0.0D;
/*  27:135 */   protected double m_CloseToDefault = 0.0D;
/*  28:140 */   protected double m_CloseToTolerance = 1.0E-006D;
/*  29:143 */   protected Range m_Cols = new Range("first-last");
/*  30:146 */   protected boolean m_IncludeClass = false;
/*  31:149 */   protected int m_Decimals = -1;
/*  32:    */   
/*  33:    */   public String globalInfo()
/*  34:    */   {
/*  35:159 */     return "A filter that 'cleanses' the numeric data from values that are too small, too big or very close to a certain value (e.g., 0) and sets these values to a pre-defined default.";
/*  36:    */   }
/*  37:    */   
/*  38:    */   public Enumeration<Option> listOptions()
/*  39:    */   {
/*  40:172 */     Vector<Option> result = new Vector(11);
/*  41:    */     
/*  42:174 */     result.addElement(new Option("\tThe minimum threshold. (default -Double.MAX_VALUE)", "min", 1, "-min <double>"));
/*  43:    */     
/*  44:    */ 
/*  45:    */ 
/*  46:178 */     result.addElement(new Option("\tThe replacement for values smaller than the minimum threshold.\n\t(default -Double.MAX_VALUE)", "min-default", 1, "-min-default <double>"));
/*  47:    */     
/*  48:    */ 
/*  49:    */ 
/*  50:    */ 
/*  51:183 */     result.addElement(new Option("\tThe maximum threshold. (default Double.MAX_VALUE)", "max", 1, "-max <double>"));
/*  52:    */     
/*  53:    */ 
/*  54:    */ 
/*  55:187 */     result.addElement(new Option("\tThe replacement for values larger than the maximum threshold.\n\t(default Double.MAX_VALUE)", "max-default", 1, "-max-default <double>"));
/*  56:    */     
/*  57:    */ 
/*  58:    */ 
/*  59:    */ 
/*  60:192 */     result.addElement(new Option("\tThe number values are checked for closeness. (default 0)", "closeto", 1, "-closeto <double>"));
/*  61:    */     
/*  62:    */ 
/*  63:    */ 
/*  64:196 */     result.addElement(new Option("\tThe replacement for values that are close to '-closeto'.\n\t(default 0)", "closeto-default", 1, "-closeto-default <double>"));
/*  65:    */     
/*  66:    */ 
/*  67:    */ 
/*  68:200 */     result.addElement(new Option("\tThe tolerance below which numbers are considered being close to \n\tto each other. (default 1E-6)", "closeto-tolerance", 1, "-closeto-tolerance <double>"));
/*  69:    */     
/*  70:    */ 
/*  71:    */ 
/*  72:    */ 
/*  73:205 */     result.addElement(new Option("\tThe number of decimals to round to, -1 means no rounding at all.\n\t(default -1)", "decimals", 1, "-decimals <int>"));
/*  74:    */     
/*  75:    */ 
/*  76:    */ 
/*  77:209 */     result.addElement(new Option("\tThe list of columns to cleanse, e.g., first-last or first-3,5-last.\n\t(default first-last)", "R", 1, "-R <col1,col2,...>"));
/*  78:    */     
/*  79:    */ 
/*  80:    */ 
/*  81:213 */     result.addElement(new Option("\tInverts the matching sense.", "V", 0, "-V"));
/*  82:    */     
/*  83:    */ 
/*  84:216 */     result.addElement(new Option("\tWhether to include the class in the cleansing.\n\tThe class column will always be skipped, if this flag is not\n\tpresent. (default no)", "include-class", 0, "-include-class"));
/*  85:    */     
/*  86:    */ 
/*  87:    */ 
/*  88:    */ 
/*  89:221 */     result.addAll(Collections.list(super.listOptions()));
/*  90:    */     
/*  91:223 */     return result.elements();
/*  92:    */   }
/*  93:    */   
/*  94:    */   public String[] getOptions()
/*  95:    */   {
/*  96:234 */     Vector<String> result = new Vector(20);
/*  97:    */     
/*  98:236 */     result.add("-min");
/*  99:237 */     result.add("" + this.m_MinThreshold);
/* 100:    */     
/* 101:239 */     result.add("-min-default");
/* 102:240 */     result.add("" + this.m_MinDefault);
/* 103:    */     
/* 104:242 */     result.add("-max");
/* 105:243 */     result.add("" + this.m_MaxThreshold);
/* 106:    */     
/* 107:245 */     result.add("-max-default");
/* 108:246 */     result.add("" + this.m_MaxDefault);
/* 109:    */     
/* 110:248 */     result.add("-closeto");
/* 111:249 */     result.add("" + this.m_CloseTo);
/* 112:    */     
/* 113:251 */     result.add("-closeto-default");
/* 114:252 */     result.add("" + this.m_CloseToDefault);
/* 115:    */     
/* 116:254 */     result.add("-closeto-tolerance");
/* 117:255 */     result.add("" + this.m_CloseToTolerance);
/* 118:    */     
/* 119:257 */     result.add("-R");
/* 120:258 */     result.add("" + this.m_Cols.getRanges());
/* 121:260 */     if (this.m_Cols.getInvert()) {
/* 122:261 */       result.add("-V");
/* 123:    */     }
/* 124:264 */     if (this.m_IncludeClass) {
/* 125:265 */       result.add("-include-class");
/* 126:    */     }
/* 127:268 */     result.add("-decimals");
/* 128:269 */     result.add("" + getDecimals());
/* 129:    */     
/* 130:271 */     Collections.addAll(result, super.getOptions());
/* 131:    */     
/* 132:273 */     return (String[])result.toArray(new String[result.size()]);
/* 133:    */   }
/* 134:    */   
/* 135:    */   public void setOptions(String[] options)
/* 136:    */     throws Exception
/* 137:    */   {
/* 138:359 */     String tmpStr = Utils.getOption("min", options);
/* 139:360 */     if (tmpStr.length() != 0) {
/* 140:361 */       setMinThreshold(Double.parseDouble(tmpStr));
/* 141:    */     } else {
/* 142:363 */       setMinThreshold(-1.797693134862316E+308D);
/* 143:    */     }
/* 144:366 */     tmpStr = Utils.getOption("min-default", options);
/* 145:367 */     if (tmpStr.length() != 0) {
/* 146:368 */       setMinDefault(Double.parseDouble(tmpStr));
/* 147:    */     } else {
/* 148:370 */       setMinDefault(-1.797693134862316E+308D);
/* 149:    */     }
/* 150:373 */     tmpStr = Utils.getOption("max", options);
/* 151:374 */     if (tmpStr.length() != 0) {
/* 152:375 */       setMaxThreshold(Double.parseDouble(tmpStr));
/* 153:    */     } else {
/* 154:377 */       setMaxThreshold(1.7976931348623157E+308D);
/* 155:    */     }
/* 156:380 */     tmpStr = Utils.getOption("max-default", options);
/* 157:381 */     if (tmpStr.length() != 0) {
/* 158:382 */       setMaxDefault(Double.parseDouble(tmpStr));
/* 159:    */     } else {
/* 160:384 */       setMaxDefault(1.7976931348623157E+308D);
/* 161:    */     }
/* 162:387 */     tmpStr = Utils.getOption("closeto", options);
/* 163:388 */     if (tmpStr.length() != 0) {
/* 164:389 */       setCloseTo(Double.parseDouble(tmpStr));
/* 165:    */     } else {
/* 166:391 */       setCloseTo(0.0D);
/* 167:    */     }
/* 168:394 */     tmpStr = Utils.getOption("closeto-default", options);
/* 169:395 */     if (tmpStr.length() != 0) {
/* 170:396 */       setCloseToDefault(Double.parseDouble(tmpStr));
/* 171:    */     } else {
/* 172:398 */       setCloseToDefault(0.0D);
/* 173:    */     }
/* 174:401 */     tmpStr = Utils.getOption("closeto-tolerance", options);
/* 175:402 */     if (tmpStr.length() != 0) {
/* 176:403 */       setCloseToTolerance(Double.parseDouble(tmpStr));
/* 177:    */     } else {
/* 178:405 */       setCloseToTolerance(1.0E-006D);
/* 179:    */     }
/* 180:408 */     tmpStr = Utils.getOption("R", options);
/* 181:409 */     if (tmpStr.length() != 0) {
/* 182:410 */       setAttributeIndices(tmpStr);
/* 183:    */     } else {
/* 184:412 */       setAttributeIndices("first-last");
/* 185:    */     }
/* 186:415 */     setInvertSelection(Utils.getFlag("V", options));
/* 187:    */     
/* 188:417 */     setIncludeClass(Utils.getFlag("include-class", options));
/* 189:    */     
/* 190:419 */     tmpStr = Utils.getOption("decimals", options);
/* 191:420 */     if (tmpStr.length() != 0) {
/* 192:421 */       setDecimals(Integer.parseInt(tmpStr));
/* 193:    */     } else {
/* 194:423 */       setDecimals(-1);
/* 195:    */     }
/* 196:426 */     super.setOptions(options);
/* 197:    */     
/* 198:428 */     Utils.checkForRemainingOptions(options);
/* 199:    */   }
/* 200:    */   
/* 201:    */   public Capabilities getCapabilities()
/* 202:    */   {
/* 203:439 */     Capabilities result = super.getCapabilities();
/* 204:440 */     result.disableAll();
/* 205:    */     
/* 206:    */ 
/* 207:443 */     result.enableAllAttributes();
/* 208:444 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 209:    */     
/* 210:    */ 
/* 211:447 */     result.enableAllClasses();
/* 212:448 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 213:449 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 214:    */     
/* 215:451 */     return result;
/* 216:    */   }
/* 217:    */   
/* 218:    */   protected Instances determineOutputFormat(Instances inputFormat)
/* 219:    */     throws Exception
/* 220:    */   {
/* 221:470 */     this.m_Cols.setUpper(inputFormat.numAttributes() - 1);
/* 222:    */     
/* 223:472 */     return new Instances(inputFormat);
/* 224:    */   }
/* 225:    */   
/* 226:    */   protected Instance process(Instance instance)
/* 227:    */     throws Exception
/* 228:    */   {
/* 229:490 */     double[] result = new double[instance.numAttributes()];
/* 230:    */     double factor;
/* 231:    */     double factor;
/* 232:492 */     if (this.m_Decimals > -1) {
/* 233:493 */       factor = StrictMath.pow(10.0D, this.m_Decimals);
/* 234:    */     } else {
/* 235:495 */       factor = 1.0D;
/* 236:    */     }
/* 237:498 */     for (int i = 0; i < instance.numAttributes(); i++)
/* 238:    */     {
/* 239:501 */       result[i] = instance.value(i);
/* 240:504 */       if (instance.attribute(i).isNumeric()) {
/* 241:509 */         if (this.m_Cols.isInRange(i)) {
/* 242:514 */           if ((instance.classIndex() != i) || (this.m_IncludeClass))
/* 243:    */           {
/* 244:519 */             if (result[i] < this.m_MinThreshold)
/* 245:    */             {
/* 246:520 */               if (getDebug()) {
/* 247:521 */                 System.out.println("Too small: " + result[i] + " -> " + this.m_MinDefault);
/* 248:    */               }
/* 249:524 */               result[i] = this.m_MinDefault;
/* 250:    */             }
/* 251:527 */             else if (result[i] > this.m_MaxThreshold)
/* 252:    */             {
/* 253:528 */               if (getDebug()) {
/* 254:529 */                 System.out.println("Too big: " + result[i] + " -> " + this.m_MaxDefault);
/* 255:    */               }
/* 256:532 */               result[i] = this.m_MaxDefault;
/* 257:    */             }
/* 258:535 */             else if ((result[i] - this.m_CloseTo < this.m_CloseToTolerance) && (this.m_CloseTo - result[i] < this.m_CloseToTolerance) && (result[i] != this.m_CloseTo))
/* 259:    */             {
/* 260:538 */               if (getDebug()) {
/* 261:539 */                 System.out.println("Too close: " + result[i] + " -> " + this.m_CloseToDefault);
/* 262:    */               }
/* 263:542 */               result[i] = this.m_CloseToDefault;
/* 264:    */             }
/* 265:546 */             if ((this.m_Decimals > -1) && (!Utils.isMissingValue(result[i])))
/* 266:    */             {
/* 267:547 */               double val = result[i];
/* 268:548 */               val = StrictMath.round(val * factor) / factor;
/* 269:549 */               result[i] = val;
/* 270:    */             }
/* 271:    */           }
/* 272:    */         }
/* 273:    */       }
/* 274:    */     }
/* 275:553 */     return instance.copy(result);
/* 276:    */   }
/* 277:    */   
/* 278:    */   public String minThresholdTipText()
/* 279:    */   {
/* 280:563 */     return "The minimum threshold below values are replaced by a default.";
/* 281:    */   }
/* 282:    */   
/* 283:    */   public double getMinThreshold()
/* 284:    */   {
/* 285:572 */     return this.m_MinThreshold;
/* 286:    */   }
/* 287:    */   
/* 288:    */   public void setMinThreshold(double value)
/* 289:    */   {
/* 290:581 */     this.m_MinThreshold = value;
/* 291:    */   }
/* 292:    */   
/* 293:    */   public String minDefaultTipText()
/* 294:    */   {
/* 295:591 */     return "The default value to replace values that are below the minimum threshold.";
/* 296:    */   }
/* 297:    */   
/* 298:    */   public double getMinDefault()
/* 299:    */   {
/* 300:600 */     return this.m_MinDefault;
/* 301:    */   }
/* 302:    */   
/* 303:    */   public void setMinDefault(double value)
/* 304:    */   {
/* 305:609 */     this.m_MinDefault = value;
/* 306:    */   }
/* 307:    */   
/* 308:    */   public String maxThresholdTipText()
/* 309:    */   {
/* 310:619 */     return "The maximum threshold above values are replaced by a default.";
/* 311:    */   }
/* 312:    */   
/* 313:    */   public double getMaxThreshold()
/* 314:    */   {
/* 315:628 */     return this.m_MaxThreshold;
/* 316:    */   }
/* 317:    */   
/* 318:    */   public void setMaxThreshold(double value)
/* 319:    */   {
/* 320:637 */     this.m_MaxThreshold = value;
/* 321:    */   }
/* 322:    */   
/* 323:    */   public String maxDefaultTipText()
/* 324:    */   {
/* 325:647 */     return "The default value to replace values that are above the maximum threshold.";
/* 326:    */   }
/* 327:    */   
/* 328:    */   public double getMaxDefault()
/* 329:    */   {
/* 330:656 */     return this.m_MaxDefault;
/* 331:    */   }
/* 332:    */   
/* 333:    */   public void setMaxDefault(double value)
/* 334:    */   {
/* 335:665 */     this.m_MaxDefault = value;
/* 336:    */   }
/* 337:    */   
/* 338:    */   public String closeToTipText()
/* 339:    */   {
/* 340:675 */     return "The number values are checked for whether they are too close to and get replaced by a default.";
/* 341:    */   }
/* 342:    */   
/* 343:    */   public double getCloseTo()
/* 344:    */   {
/* 345:685 */     return this.m_CloseTo;
/* 346:    */   }
/* 347:    */   
/* 348:    */   public void setCloseTo(double value)
/* 349:    */   {
/* 350:694 */     this.m_CloseTo = value;
/* 351:    */   }
/* 352:    */   
/* 353:    */   public String closeToDefaultTipText()
/* 354:    */   {
/* 355:704 */     return "The default value to replace values with that are too close.";
/* 356:    */   }
/* 357:    */   
/* 358:    */   public double getCloseToDefault()
/* 359:    */   {
/* 360:713 */     return this.m_CloseToDefault;
/* 361:    */   }
/* 362:    */   
/* 363:    */   public void setCloseToDefault(double value)
/* 364:    */   {
/* 365:722 */     this.m_CloseToDefault = value;
/* 366:    */   }
/* 367:    */   
/* 368:    */   public String closeToToleranceTipText()
/* 369:    */   {
/* 370:732 */     return "The value below which values are considered close to.";
/* 371:    */   }
/* 372:    */   
/* 373:    */   public double getCloseToTolerance()
/* 374:    */   {
/* 375:741 */     return this.m_CloseToTolerance;
/* 376:    */   }
/* 377:    */   
/* 378:    */   public void setCloseToTolerance(double value)
/* 379:    */   {
/* 380:750 */     this.m_CloseToTolerance = value;
/* 381:    */   }
/* 382:    */   
/* 383:    */   public String attributeIndicesTipText()
/* 384:    */   {
/* 385:760 */     return "The selection of columns to use in the cleansing processs, first and last are valid indices.";
/* 386:    */   }
/* 387:    */   
/* 388:    */   public String getAttributeIndices()
/* 389:    */   {
/* 390:769 */     return this.m_Cols.getRanges();
/* 391:    */   }
/* 392:    */   
/* 393:    */   public void setAttributeIndices(String value)
/* 394:    */   {
/* 395:778 */     this.m_Cols.setRanges(value);
/* 396:    */   }
/* 397:    */   
/* 398:    */   public String invertSelectionTipText()
/* 399:    */   {
/* 400:788 */     return "If enabled the selection of the columns is inverted.";
/* 401:    */   }
/* 402:    */   
/* 403:    */   public boolean getInvertSelection()
/* 404:    */   {
/* 405:797 */     return this.m_Cols.getInvert();
/* 406:    */   }
/* 407:    */   
/* 408:    */   public void setInvertSelection(boolean value)
/* 409:    */   {
/* 410:806 */     this.m_Cols.setInvert(value);
/* 411:    */   }
/* 412:    */   
/* 413:    */   public String includeClassTipText()
/* 414:    */   {
/* 415:816 */     return "If disabled, the class attribute will be always left out of the cleaning process.";
/* 416:    */   }
/* 417:    */   
/* 418:    */   public boolean getIncludeClass()
/* 419:    */   {
/* 420:826 */     return this.m_IncludeClass;
/* 421:    */   }
/* 422:    */   
/* 423:    */   public void setIncludeClass(boolean value)
/* 424:    */   {
/* 425:835 */     this.m_IncludeClass = value;
/* 426:    */   }
/* 427:    */   
/* 428:    */   public String decimalsTipText()
/* 429:    */   {
/* 430:845 */     return "The number of decimals to round to, -1 means no rounding at all.";
/* 431:    */   }
/* 432:    */   
/* 433:    */   public int getDecimals()
/* 434:    */   {
/* 435:854 */     return this.m_Decimals;
/* 436:    */   }
/* 437:    */   
/* 438:    */   public void setDecimals(int value)
/* 439:    */   {
/* 440:863 */     this.m_Decimals = value;
/* 441:    */   }
/* 442:    */   
/* 443:    */   public String getRevision()
/* 444:    */   {
/* 445:873 */     return RevisionUtils.extract("$Revision: 12473 $");
/* 446:    */   }
/* 447:    */   
/* 448:    */   public static void main(String[] args)
/* 449:    */   {
/* 450:882 */     runFilter(new NumericCleaner(), args);
/* 451:    */   }
/* 452:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.NumericCleaner
 * JD-Core Version:    0.7.0.1
 */