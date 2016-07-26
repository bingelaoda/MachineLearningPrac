/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.text.SimpleDateFormat;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Date;
/*   7:    */ import java.util.Enumeration;
/*   8:    */ import java.util.List;
/*   9:    */ import java.util.Vector;
/*  10:    */ import weka.core.Attribute;
/*  11:    */ import weka.core.Capabilities;
/*  12:    */ import weka.core.Capabilities.Capability;
/*  13:    */ import weka.core.DenseInstance;
/*  14:    */ import weka.core.Environment;
/*  15:    */ import weka.core.EnvironmentHandler;
/*  16:    */ import weka.core.Instance;
/*  17:    */ import weka.core.Instances;
/*  18:    */ import weka.core.Option;
/*  19:    */ import weka.core.Range;
/*  20:    */ import weka.core.RevisionUtils;
/*  21:    */ import weka.core.SparseInstance;
/*  22:    */ import weka.core.Utils;
/*  23:    */ import weka.filters.StreamableFilter;
/*  24:    */ import weka.filters.UnsupervisedFilter;
/*  25:    */ 
/*  26:    */ public class ReplaceMissingWithUserConstant
/*  27:    */   extends PotentialClassIgnorer
/*  28:    */   implements UnsupervisedFilter, StreamableFilter, EnvironmentHandler
/*  29:    */ {
/*  30:    */   private static final long serialVersionUID = -7334039452189350356L;
/*  31:    */   protected transient Environment m_env;
/*  32:    */   protected Range m_selectedRange;
/*  33: 70 */   protected String m_range = "first-last";
/*  34: 72 */   protected String m_resolvedRange = "";
/*  35: 75 */   protected String m_nominalStringConstant = "";
/*  36: 78 */   protected String m_resolvedNominalStringConstant = "";
/*  37: 81 */   protected String m_numericConstant = "0";
/*  38: 84 */   protected String m_resolvedNumericConstant = "";
/*  39: 87 */   protected double m_numericConstVal = 0.0D;
/*  40: 90 */   protected String m_dateConstant = "";
/*  41: 93 */   protected String m_resolvedDateConstant = "";
/*  42: 96 */   protected double m_dateConstVal = 0.0D;
/*  43: 99 */   protected String m_defaultDateFormat = "yyyy-MM-dd'T'HH:mm:ss";
/*  44:102 */   protected String m_resolvedDateFormat = "";
/*  45:    */   
/*  46:    */   public String globalInfo()
/*  47:    */   {
/*  48:112 */     return "Replaces all missing values for nominal, string, numeric and date attributes in the dataset with user-supplied constant values.";
/*  49:    */   }
/*  50:    */   
/*  51:    */   public Capabilities getCapabilities()
/*  52:    */   {
/*  53:118 */     Capabilities result = super.getCapabilities();
/*  54:119 */     result.disableAll();
/*  55:    */     
/*  56:    */ 
/*  57:122 */     result.enableAllAttributes();
/*  58:123 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  59:    */     
/*  60:    */ 
/*  61:126 */     result.enableAllClasses();
/*  62:127 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  63:128 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  64:    */     
/*  65:130 */     return result;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public Enumeration<Option> listOptions()
/*  69:    */   {
/*  70:136 */     Vector<Option> opts = new Vector(5);
/*  71:    */     
/*  72:138 */     opts.addElement(new Option("\tSpecify list of attributes to replace missing values for \n\t(as weka range list of indices or a comma separated list of attribute names).\n\t(default: consider all attributes)", "R", 1, "-A <index1,index2-index4,... | att-name1,att-name2,...>"));
/*  73:    */     
/*  74:    */ 
/*  75:    */ 
/*  76:    */ 
/*  77:    */ 
/*  78:    */ 
/*  79:145 */     opts.addElement(new Option("\tSpecify the replacement constant for nominal/string attributes", "N", 1, "-N"));
/*  80:    */     
/*  81:    */ 
/*  82:148 */     opts.addElement(new Option("\tSpecify the replacement constant for numeric attributes\n\t(default: 0)", "R", 1, "-R"));
/*  83:    */     
/*  84:    */ 
/*  85:151 */     opts.addElement(new Option("\tSpecify the replacement constant for date attributes", "D", 1, "-D"));
/*  86:    */     
/*  87:153 */     opts.addElement(new Option("\tSpecify the date format for parsing the replacement date constant\n\t(default: yyyy-MM-dd'T'HH:mm:ss)", "F", 1, "-F"));
/*  88:    */     
/*  89:    */ 
/*  90:    */ 
/*  91:157 */     opts.addAll(Collections.list(super.listOptions()));
/*  92:    */     
/*  93:159 */     return opts.elements();
/*  94:    */   }
/*  95:    */   
/*  96:    */   public void setOptions(String[] options)
/*  97:    */     throws Exception
/*  98:    */   {
/*  99:214 */     String atts = Utils.getOption('A', options);
/* 100:215 */     if (atts.length() > 0) {
/* 101:216 */       setAttributes(atts);
/* 102:    */     }
/* 103:219 */     String nomString = Utils.getOption('N', options);
/* 104:220 */     if (nomString.length() > 0) {
/* 105:221 */       setNominalStringReplacementValue(nomString);
/* 106:    */     }
/* 107:223 */     String numString = Utils.getOption('R', options);
/* 108:224 */     if (numString.length() > 0) {
/* 109:225 */       setNumericReplacementValue(numString);
/* 110:    */     }
/* 111:227 */     String dateString = Utils.getOption('D', options);
/* 112:228 */     if (dateString.length() > 0) {
/* 113:229 */       setDateReplacementValue(dateString);
/* 114:    */     }
/* 115:231 */     String formatString = Utils.getOption('F', options);
/* 116:232 */     if (formatString.length() > 0) {
/* 117:233 */       setDateFormat(formatString);
/* 118:    */     }
/* 119:236 */     super.setOptions(options);
/* 120:    */     
/* 121:238 */     Utils.checkForRemainingOptions(options);
/* 122:    */   }
/* 123:    */   
/* 124:    */   public String[] getOptions()
/* 125:    */   {
/* 126:244 */     ArrayList<String> options = new ArrayList();
/* 127:246 */     if (getAttributes().length() > 0)
/* 128:    */     {
/* 129:247 */       options.add("-A");
/* 130:248 */       options.add(getAttributes());
/* 131:    */     }
/* 132:251 */     if (getNominalStringReplacementValue().length() > 0)
/* 133:    */     {
/* 134:252 */       options.add("-N");
/* 135:253 */       options.add(getNominalStringReplacementValue());
/* 136:    */     }
/* 137:256 */     if (getNumericReplacementValue().length() > 0)
/* 138:    */     {
/* 139:257 */       options.add("-R");
/* 140:258 */       options.add(getNumericReplacementValue());
/* 141:    */     }
/* 142:261 */     if (getDateReplacementValue().length() > 0)
/* 143:    */     {
/* 144:262 */       options.add("-D");
/* 145:263 */       options.add(getDateReplacementValue());
/* 146:    */     }
/* 147:266 */     if (getDateFormat().length() > 0)
/* 148:    */     {
/* 149:267 */       options.add("-F");
/* 150:268 */       options.add(getDateFormat());
/* 151:    */     }
/* 152:271 */     Collections.addAll(options, super.getOptions());
/* 153:    */     
/* 154:273 */     return (String[])options.toArray(new String[1]);
/* 155:    */   }
/* 156:    */   
/* 157:    */   public String attributesTipText()
/* 158:    */   {
/* 159:282 */     return "Specify range of attributes to act on. This is a comma separated list of attribute indices, with \"first\" and \"last\" valid values. Specify an inclusive range with \"-\". E.g: \"first-3,5,6-10,last\". Can alternatively specify a comma separated list of attribute names. Note that  you can't mix indices and attribute names in the same list";
/* 160:    */   }
/* 161:    */   
/* 162:    */   public void setAttributes(String range)
/* 163:    */   {
/* 164:296 */     this.m_range = range;
/* 165:    */   }
/* 166:    */   
/* 167:    */   public String getAttributes()
/* 168:    */   {
/* 169:305 */     return this.m_range;
/* 170:    */   }
/* 171:    */   
/* 172:    */   public String nominalStringReplacementValueTipText()
/* 173:    */   {
/* 174:314 */     return "The constant to replace missing values in nominal/string attributes with";
/* 175:    */   }
/* 176:    */   
/* 177:    */   public String getNominalStringReplacementValue()
/* 178:    */   {
/* 179:323 */     return this.m_nominalStringConstant;
/* 180:    */   }
/* 181:    */   
/* 182:    */   public void setNominalStringReplacementValue(String nominalStringConstant)
/* 183:    */   {
/* 184:332 */     this.m_nominalStringConstant = nominalStringConstant;
/* 185:    */   }
/* 186:    */   
/* 187:    */   public String numericReplacementValueTipText()
/* 188:    */   {
/* 189:341 */     return "The constant to replace missing values in numeric attributes with";
/* 190:    */   }
/* 191:    */   
/* 192:    */   public String getNumericReplacementValue()
/* 193:    */   {
/* 194:350 */     return this.m_numericConstant;
/* 195:    */   }
/* 196:    */   
/* 197:    */   public void setNumericReplacementValue(String numericConstant)
/* 198:    */   {
/* 199:359 */     this.m_numericConstant = numericConstant;
/* 200:    */   }
/* 201:    */   
/* 202:    */   public String dateReplacementValueTipText()
/* 203:    */   {
/* 204:368 */     return "The constant to replace missing values in date attributes with";
/* 205:    */   }
/* 206:    */   
/* 207:    */   public void setDateReplacementValue(String dateConstant)
/* 208:    */   {
/* 209:377 */     this.m_dateConstant = dateConstant;
/* 210:    */   }
/* 211:    */   
/* 212:    */   public String getDateReplacementValue()
/* 213:    */   {
/* 214:386 */     return this.m_dateConstant;
/* 215:    */   }
/* 216:    */   
/* 217:    */   public String dateFormatTipText()
/* 218:    */   {
/* 219:395 */     return "The formatting string to use for parsing the date replacement value";
/* 220:    */   }
/* 221:    */   
/* 222:    */   public void setDateFormat(String dateFormat)
/* 223:    */   {
/* 224:404 */     this.m_defaultDateFormat = dateFormat;
/* 225:    */   }
/* 226:    */   
/* 227:    */   public String getDateFormat()
/* 228:    */   {
/* 229:413 */     return this.m_defaultDateFormat;
/* 230:    */   }
/* 231:    */   
/* 232:    */   public boolean setInputFormat(Instances instanceInfo)
/* 233:    */     throws Exception
/* 234:    */   {
/* 235:419 */     super.setInputFormat(instanceInfo);
/* 236:    */     
/* 237:421 */     this.m_resolvedNominalStringConstant = this.m_nominalStringConstant;
/* 238:422 */     this.m_resolvedNumericConstant = this.m_numericConstant;
/* 239:423 */     this.m_resolvedDateConstant = this.m_dateConstant;
/* 240:424 */     this.m_resolvedDateFormat = this.m_defaultDateFormat;
/* 241:425 */     this.m_resolvedRange = this.m_range;
/* 242:427 */     if (this.m_env == null) {
/* 243:428 */       this.m_env = Environment.getSystemWide();
/* 244:    */     }
/* 245:    */     try
/* 246:    */     {
/* 247:432 */       if ((this.m_resolvedNominalStringConstant != null) && (this.m_resolvedNominalStringConstant.length() > 0)) {
/* 248:434 */         this.m_resolvedNominalStringConstant = this.m_env.substitute(this.m_resolvedNominalStringConstant);
/* 249:    */       }
/* 250:438 */       if ((this.m_resolvedNumericConstant != null) && (this.m_resolvedNumericConstant.length() > 0)) {
/* 251:440 */         this.m_resolvedNumericConstant = this.m_env.substitute(this.m_resolvedNumericConstant);
/* 252:    */       }
/* 253:443 */       if ((this.m_resolvedDateConstant != null) && (this.m_resolvedDateConstant.length() > 0)) {
/* 254:444 */         this.m_resolvedDateConstant = this.m_env.substitute(this.m_resolvedDateConstant);
/* 255:    */       }
/* 256:447 */       if ((this.m_resolvedDateFormat != null) && (this.m_resolvedDateFormat.length() > 0)) {
/* 257:448 */         this.m_resolvedDateFormat = this.m_env.substitute(this.m_resolvedDateFormat);
/* 258:    */       }
/* 259:451 */       if ((this.m_resolvedRange != null) && (this.m_resolvedRange.length() > 0)) {
/* 260:452 */         this.m_resolvedRange = this.m_env.substitute(this.m_resolvedRange);
/* 261:    */       }
/* 262:    */     }
/* 263:    */     catch (Exception ex) {}
/* 264:458 */     this.m_selectedRange = new Range(this.m_resolvedRange);
/* 265:    */     try
/* 266:    */     {
/* 267:460 */       this.m_selectedRange.setUpper(instanceInfo.numAttributes() - 1);
/* 268:    */     }
/* 269:    */     catch (IllegalArgumentException e)
/* 270:    */     {
/* 271:463 */       String[] parts = this.m_resolvedRange.split(",");
/* 272:464 */       if (parts.length == 0) {
/* 273:465 */         throw new Exception("Must specify which attributes to replace missing values for!");
/* 274:    */       }
/* 275:469 */       StringBuffer indexList = new StringBuffer();
/* 276:470 */       for (String att : parts)
/* 277:    */       {
/* 278:471 */         att = att.trim();
/* 279:472 */         Attribute a = instanceInfo.attribute(att);
/* 280:473 */         if (a == null) {
/* 281:474 */           throw new Exception("I can't find the requested attribute '" + att + "' in the incoming instances.");
/* 282:    */         }
/* 283:477 */         indexList.append(",").append(a.index() + 1);
/* 284:    */       }
/* 285:479 */       String result = indexList.toString();
/* 286:480 */       result = result.substring(1, result.length());
/* 287:481 */       this.m_selectedRange = new Range(result);
/* 288:482 */       this.m_selectedRange.setUpper(instanceInfo.numAttributes() - 1);
/* 289:    */     }
/* 290:485 */     boolean hasNominal = false;
/* 291:486 */     boolean hasString = false;
/* 292:487 */     boolean hasNumeric = false;
/* 293:488 */     boolean hasDate = false;
/* 294:490 */     for (int i = 0; i < instanceInfo.numAttributes(); i++) {
/* 295:491 */       if (this.m_selectedRange.isInRange(i)) {
/* 296:492 */         if (instanceInfo.attribute(i).isNominal()) {
/* 297:493 */           hasNominal = true;
/* 298:494 */         } else if (instanceInfo.attribute(i).isString()) {
/* 299:495 */           hasString = true;
/* 300:496 */         } else if (instanceInfo.attribute(i).isDate()) {
/* 301:497 */           hasDate = true;
/* 302:498 */         } else if (instanceInfo.attribute(i).isNumeric()) {
/* 303:499 */           hasNumeric = true;
/* 304:    */         }
/* 305:    */       }
/* 306:    */     }
/* 307:504 */     if (((hasNominal) || (hasString)) && (
/* 308:505 */       (this.m_resolvedNominalStringConstant == null) || (this.m_resolvedNominalStringConstant.length() == 0))) {
/* 309:507 */       if ((this.m_resolvedNumericConstant != null) && (this.m_resolvedNumericConstant.length() > 0)) {
/* 310:510 */         this.m_resolvedNominalStringConstant = ("" + this.m_resolvedNumericConstant);
/* 311:    */       } else {
/* 312:512 */         throw new Exception("Data contains nominal/string attributes and no replacement constant has been supplied");
/* 313:    */       }
/* 314:    */     }
/* 315:518 */     if (hasNumeric)
/* 316:    */     {
/* 317:519 */       if ((this.m_numericConstant == null) || (this.m_numericConstant.length() == 0)) {
/* 318:520 */         if ((this.m_resolvedNominalStringConstant != null) && (this.m_resolvedNominalStringConstant.length() > 0)) {
/* 319:    */           try
/* 320:    */           {
/* 321:525 */             Double.parseDouble(this.m_resolvedNominalStringConstant);
/* 322:526 */             this.m_resolvedNumericConstant = this.m_resolvedNominalStringConstant;
/* 323:    */           }
/* 324:    */           catch (NumberFormatException e)
/* 325:    */           {
/* 326:528 */             throw new Exception("Data contains numeric attributes and no numeric constant has been supplied. Unable to parse nominal constant as a number either.");
/* 327:    */           }
/* 328:    */         } else {
/* 329:534 */           throw new Exception("Data contains numeric attributes and no replacement constant has been supplied");
/* 330:    */         }
/* 331:    */       }
/* 332:    */       try
/* 333:    */       {
/* 334:540 */         this.m_numericConstVal = Double.parseDouble(this.m_resolvedNumericConstant);
/* 335:    */       }
/* 336:    */       catch (NumberFormatException e)
/* 337:    */       {
/* 338:542 */         throw new Exception("Unable to parse numeric constant");
/* 339:    */       }
/* 340:    */     }
/* 341:546 */     if (hasDate)
/* 342:    */     {
/* 343:547 */       if ((this.m_resolvedDateConstant == null) || (this.m_resolvedDateConstant.length() == 0)) {
/* 344:549 */         throw new Exception("Data contains date attributes and no replacement constant has been supplied");
/* 345:    */       }
/* 346:554 */       SimpleDateFormat sdf = new SimpleDateFormat(this.m_resolvedDateFormat);
/* 347:555 */       Date d = sdf.parse(this.m_resolvedDateConstant);
/* 348:556 */       this.m_dateConstVal = d.getTime();
/* 349:    */     }
/* 350:559 */     Instances outputFormat = new Instances(instanceInfo, 0);
/* 351:    */     
/* 352:    */ 
/* 353:    */ 
/* 354:563 */     ArrayList<Attribute> updatedNoms = new ArrayList();
/* 355:564 */     for (int i = 0; i < instanceInfo.numAttributes(); i++) {
/* 356:565 */       if ((i != instanceInfo.classIndex()) && (this.m_selectedRange.isInRange(i)))
/* 357:    */       {
/* 358:566 */         Attribute temp = instanceInfo.attribute(i);
/* 359:567 */         if ((temp.isNominal()) && 
/* 360:568 */           (temp.indexOfValue(this.m_resolvedNominalStringConstant) < 0))
/* 361:    */         {
/* 362:569 */           List<String> values = new ArrayList();
/* 363:    */           
/* 364:571 */           values.add(this.m_resolvedNominalStringConstant);
/* 365:572 */           for (int j = 0; j < temp.numValues(); j++) {
/* 366:573 */             values.add(temp.value(j));
/* 367:    */           }
/* 368:576 */           Attribute newAtt = new Attribute(temp.name(), values);
/* 369:577 */           newAtt.setWeight(temp.weight());
/* 370:578 */           updatedNoms.add(newAtt);
/* 371:    */         }
/* 372:    */       }
/* 373:    */     }
/* 374:584 */     if (updatedNoms.size() > 0)
/* 375:    */     {
/* 376:585 */       int nomCount = 0;
/* 377:586 */       ArrayList<Attribute> atts = new ArrayList();
/* 378:588 */       for (int i = 0; i < instanceInfo.numAttributes(); i++) {
/* 379:589 */         if ((i != instanceInfo.classIndex()) && (this.m_selectedRange.isInRange(i)))
/* 380:    */         {
/* 381:590 */           if (instanceInfo.attribute(i).isNominal()) {
/* 382:591 */             atts.add(updatedNoms.get(nomCount++));
/* 383:    */           } else {
/* 384:593 */             atts.add((Attribute)instanceInfo.attribute(i).copy());
/* 385:    */           }
/* 386:    */         }
/* 387:    */         else {
/* 388:596 */           atts.add((Attribute)instanceInfo.attribute(i).copy());
/* 389:    */         }
/* 390:    */       }
/* 391:600 */       outputFormat = new Instances(instanceInfo.relationName(), atts, 0);
/* 392:601 */       outputFormat.setClassIndex(getInputFormat().classIndex());
/* 393:    */     }
/* 394:604 */     setOutputFormat(outputFormat);
/* 395:    */     
/* 396:606 */     return true;
/* 397:    */   }
/* 398:    */   
/* 399:    */   public boolean input(Instance inst)
/* 400:    */     throws Exception
/* 401:    */   {
/* 402:611 */     if (getInputFormat() == null) {
/* 403:612 */       throw new IllegalStateException("No input instance format defined");
/* 404:    */     }
/* 405:614 */     if (this.m_NewBatch)
/* 406:    */     {
/* 407:615 */       resetQueue();
/* 408:616 */       this.m_NewBatch = false;
/* 409:    */     }
/* 410:619 */     double[] vals = new double[inst.numAttributes()];
/* 411:621 */     for (int i = 0; i < inst.numAttributes(); i++) {
/* 412:622 */       if ((inst.isMissing(i)) && (this.m_selectedRange.isInRange(i)))
/* 413:    */       {
/* 414:623 */         if (i != inst.classIndex())
/* 415:    */         {
/* 416:624 */           if (inst.attribute(i).isDate())
/* 417:    */           {
/* 418:625 */             vals[i] = this.m_dateConstVal;
/* 419:    */           }
/* 420:626 */           else if (inst.attribute(i).isNumeric())
/* 421:    */           {
/* 422:627 */             vals[i] = this.m_numericConstVal;
/* 423:    */           }
/* 424:628 */           else if (inst.attribute(i).isNominal())
/* 425:    */           {
/* 426:630 */             int temp = inst.attribute(i).indexOfValue(this.m_resolvedNominalStringConstant);
/* 427:    */             
/* 428:    */ 
/* 429:633 */             vals[i] = (temp >= 0 ? temp : 0.0D);
/* 430:    */           }
/* 431:634 */           else if (inst.attribute(i).isString())
/* 432:    */           {
/* 433:642 */             if (inst.attribute(i).numValues() <= 1)
/* 434:    */             {
/* 435:643 */               outputFormatPeek().attribute(i).setStringValue(this.m_resolvedNominalStringConstant);
/* 436:    */               
/* 437:645 */               vals[i] = 0.0D;
/* 438:    */             }
/* 439:    */             else
/* 440:    */             {
/* 441:647 */               vals[i] = outputFormatPeek().attribute(i).addStringValue(this.m_resolvedNominalStringConstant);
/* 442:    */             }
/* 443:    */           }
/* 444:    */           else
/* 445:    */           {
/* 446:651 */             vals[i] = inst.value(i);
/* 447:    */           }
/* 448:    */         }
/* 449:    */         else {
/* 450:654 */           vals[i] = inst.value(i);
/* 451:    */         }
/* 452:    */       }
/* 453:657 */       else if (this.m_selectedRange.isInRange(i))
/* 454:    */       {
/* 455:659 */         if (inst.attribute(i).isString())
/* 456:    */         {
/* 457:667 */           if (inst.attribute(i).numValues() <= 1) {
/* 458:668 */             outputFormatPeek().attribute(i).setStringValue(inst.stringValue(i));
/* 459:    */           } else {
/* 460:671 */             outputFormatPeek().attribute(i).addStringValue(inst.stringValue(i));
/* 461:    */           }
/* 462:674 */           vals[i] = outputFormatPeek().attribute(i).indexOfValue(inst.stringValue(i));
/* 463:    */         }
/* 464:676 */         else if ((inst.attribute(i).isNominal()) && (i != inst.classIndex()))
/* 465:    */         {
/* 466:677 */           vals[i] = (inst.value(i) + 1.0D);
/* 467:    */         }
/* 468:    */         else
/* 469:    */         {
/* 470:679 */           vals[i] = inst.value(i);
/* 471:    */         }
/* 472:    */       }
/* 473:    */       else {
/* 474:683 */         vals[i] = inst.value(i);
/* 475:    */       }
/* 476:    */     }
/* 477:688 */     Instance newInst = null;
/* 478:689 */     if ((inst instanceof SparseInstance)) {
/* 479:690 */       newInst = new SparseInstance(inst.weight(), vals);
/* 480:    */     } else {
/* 481:692 */       newInst = new DenseInstance(inst.weight(), vals);
/* 482:    */     }
/* 483:695 */     newInst.setDataset(getOutputFormat());
/* 484:    */     
/* 485:    */ 
/* 486:    */ 
/* 487:    */ 
/* 488:700 */     push(newInst, false);
/* 489:    */     
/* 490:702 */     return true;
/* 491:    */   }
/* 492:    */   
/* 493:    */   public void setEnvironment(Environment env)
/* 494:    */   {
/* 495:707 */     this.m_env = env;
/* 496:    */   }
/* 497:    */   
/* 498:    */   public String getRevision()
/* 499:    */   {
/* 500:717 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 501:    */   }
/* 502:    */   
/* 503:    */   public static void main(String[] args)
/* 504:    */   {
/* 505:726 */     runFilter(new ReplaceMissingWithUserConstant(), args);
/* 506:    */   }
/* 507:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.ReplaceMissingWithUserConstant
 * JD-Core Version:    0.7.0.1
 */