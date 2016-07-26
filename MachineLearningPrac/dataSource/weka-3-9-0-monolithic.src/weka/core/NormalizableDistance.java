/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.neighboursearch.PerformanceStats;
/*   7:    */ 
/*   8:    */ public abstract class NormalizableDistance
/*   9:    */   implements DistanceFunction, OptionHandler, Serializable, RevisionHandler
/*  10:    */ {
/*  11:    */   private static final long serialVersionUID = -2806520224161351708L;
/*  12:    */   public static final int R_MIN = 0;
/*  13:    */   public static final int R_MAX = 1;
/*  14:    */   public static final int R_WIDTH = 2;
/*  15: 58 */   protected Instances m_Data = null;
/*  16: 61 */   protected boolean m_DontNormalize = false;
/*  17:    */   protected double[][] m_Ranges;
/*  18: 67 */   protected Range m_AttributeIndices = new Range("first-last");
/*  19:    */   protected boolean[] m_ActiveIndices;
/*  20:    */   protected boolean m_Validated;
/*  21:    */   
/*  22:    */   public NormalizableDistance()
/*  23:    */   {
/*  24: 79 */     invalidate();
/*  25:    */   }
/*  26:    */   
/*  27:    */   public NormalizableDistance(Instances data)
/*  28:    */   {
/*  29: 88 */     setInstances(data);
/*  30:    */   }
/*  31:    */   
/*  32:    */   public abstract String globalInfo();
/*  33:    */   
/*  34:    */   public Enumeration<Option> listOptions()
/*  35:    */   {
/*  36:106 */     Vector<Option> result = new Vector();
/*  37:    */     
/*  38:108 */     result.add(new Option("\tTurns off the normalization of attribute \n\tvalues in distance calculation.", "D", 0, "-D"));
/*  39:    */     
/*  40:    */ 
/*  41:111 */     result.addElement(new Option("\tSpecifies list of columns to used in the calculation of the \n\tdistance. 'first' and 'last' are valid indices.\n\t(default: first-last)", "R", 1, "-R <col1,col2-col4,...>"));
/*  42:    */     
/*  43:    */ 
/*  44:    */ 
/*  45:    */ 
/*  46:116 */     result.addElement(new Option("\tInvert matching sense of column indices.", "V", 0, "-V"));
/*  47:    */     
/*  48:    */ 
/*  49:119 */     return result.elements();
/*  50:    */   }
/*  51:    */   
/*  52:    */   public String[] getOptions()
/*  53:    */   {
/*  54:131 */     Vector<String> result = new Vector();
/*  55:133 */     if (getDontNormalize()) {
/*  56:134 */       result.add("-D");
/*  57:    */     }
/*  58:137 */     result.add("-R");
/*  59:138 */     result.add(getAttributeIndices());
/*  60:140 */     if (getInvertSelection()) {
/*  61:141 */       result.add("-V");
/*  62:    */     }
/*  63:144 */     return (String[])result.toArray(new String[result.size()]);
/*  64:    */   }
/*  65:    */   
/*  66:    */   public void setOptions(String[] options)
/*  67:    */     throws Exception
/*  68:    */   {
/*  69:157 */     setDontNormalize(Utils.getFlag('D', options));
/*  70:    */     
/*  71:159 */     String tmpStr = Utils.getOption('R', options);
/*  72:160 */     if (tmpStr.length() != 0) {
/*  73:161 */       setAttributeIndices(tmpStr);
/*  74:    */     } else {
/*  75:163 */       setAttributeIndices("first-last");
/*  76:    */     }
/*  77:166 */     setInvertSelection(Utils.getFlag('V', options));
/*  78:    */   }
/*  79:    */   
/*  80:    */   public String dontNormalizeTipText()
/*  81:    */   {
/*  82:176 */     return "Whether if the normalization of attributes should be turned off for distance calculation (Default: false i.e. attribute values are normalized). ";
/*  83:    */   }
/*  84:    */   
/*  85:    */   public void setDontNormalize(boolean dontNormalize)
/*  86:    */   {
/*  87:188 */     this.m_DontNormalize = dontNormalize;
/*  88:189 */     invalidate();
/*  89:    */   }
/*  90:    */   
/*  91:    */   public boolean getDontNormalize()
/*  92:    */   {
/*  93:199 */     return this.m_DontNormalize;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public String attributeIndicesTipText()
/*  97:    */   {
/*  98:209 */     return "Specify range of attributes to act on. This is a comma separated list of attribute indices, with \"first\" and \"last\" valid values. Specify an inclusive range with \"-\". E.g: \"first-3,5,6-10,last\".";
/*  99:    */   }
/* 100:    */   
/* 101:    */   public void setAttributeIndices(String value)
/* 102:    */   {
/* 103:224 */     this.m_AttributeIndices.setRanges(value);
/* 104:225 */     invalidate();
/* 105:    */   }
/* 106:    */   
/* 107:    */   public String getAttributeIndices()
/* 108:    */   {
/* 109:235 */     return this.m_AttributeIndices.getRanges();
/* 110:    */   }
/* 111:    */   
/* 112:    */   public String invertSelectionTipText()
/* 113:    */   {
/* 114:245 */     return "Set attribute selection mode. If false, only selected attributes in the range will be used in the distance calculation; if true, only non-selected attributes will be used for the calculation.";
/* 115:    */   }
/* 116:    */   
/* 117:    */   public void setInvertSelection(boolean value)
/* 118:    */   {
/* 119:257 */     this.m_AttributeIndices.setInvert(value);
/* 120:258 */     invalidate();
/* 121:    */   }
/* 122:    */   
/* 123:    */   public boolean getInvertSelection()
/* 124:    */   {
/* 125:268 */     return this.m_AttributeIndices.getInvert();
/* 126:    */   }
/* 127:    */   
/* 128:    */   protected void invalidate()
/* 129:    */   {
/* 130:275 */     this.m_Validated = false;
/* 131:    */   }
/* 132:    */   
/* 133:    */   protected void validate()
/* 134:    */   {
/* 135:282 */     if (!this.m_Validated)
/* 136:    */     {
/* 137:283 */       initialize();
/* 138:284 */       this.m_Validated = true;
/* 139:    */     }
/* 140:    */   }
/* 141:    */   
/* 142:    */   protected void initialize()
/* 143:    */   {
/* 144:292 */     initializeAttributeIndices();
/* 145:293 */     initializeRanges();
/* 146:    */   }
/* 147:    */   
/* 148:    */   protected void initializeAttributeIndices()
/* 149:    */   {
/* 150:300 */     this.m_AttributeIndices.setUpper(this.m_Data.numAttributes() - 1);
/* 151:301 */     this.m_ActiveIndices = new boolean[this.m_Data.numAttributes()];
/* 152:302 */     for (int i = 0; i < this.m_ActiveIndices.length; i++) {
/* 153:303 */       this.m_ActiveIndices[i] = this.m_AttributeIndices.isInRange(i);
/* 154:    */     }
/* 155:    */   }
/* 156:    */   
/* 157:    */   public void setInstances(Instances insts)
/* 158:    */   {
/* 159:314 */     this.m_Data = insts;
/* 160:315 */     invalidate();
/* 161:    */   }
/* 162:    */   
/* 163:    */   public Instances getInstances()
/* 164:    */   {
/* 165:325 */     return this.m_Data;
/* 166:    */   }
/* 167:    */   
/* 168:    */   public void postProcessDistances(double[] distances) {}
/* 169:    */   
/* 170:    */   public void update(Instance ins)
/* 171:    */   {
/* 172:344 */     validate();
/* 173:    */     
/* 174:346 */     this.m_Ranges = updateRanges(ins, this.m_Ranges);
/* 175:    */   }
/* 176:    */   
/* 177:    */   public double distance(Instance first, Instance second)
/* 178:    */   {
/* 179:358 */     return distance(first, second, null);
/* 180:    */   }
/* 181:    */   
/* 182:    */   public double distance(Instance first, Instance second, PerformanceStats stats)
/* 183:    */   {
/* 184:371 */     return distance(first, second, (1.0D / 0.0D), stats);
/* 185:    */   }
/* 186:    */   
/* 187:    */   public double distance(Instance first, Instance second, double cutOffValue)
/* 188:    */   {
/* 189:391 */     return distance(first, second, cutOffValue, null);
/* 190:    */   }
/* 191:    */   
/* 192:    */   public double distance(Instance first, Instance second, double cutOffValue, PerformanceStats stats)
/* 193:    */   {
/* 194:413 */     double distance = 0.0D;
/* 195:    */     
/* 196:415 */     int firstNumValues = first.numValues();
/* 197:416 */     int secondNumValues = second.numValues();
/* 198:417 */     int numAttributes = this.m_Data.numAttributes();
/* 199:418 */     int classIndex = this.m_Data.classIndex();
/* 200:    */     
/* 201:420 */     validate();
/* 202:    */     
/* 203:422 */     int p1 = 0;
/* 204:422 */     for (int p2 = 0; (p1 < firstNumValues) || (p2 < secondNumValues);)
/* 205:    */     {
/* 206:    */       int firstI;
/* 207:    */       int firstI;
/* 208:423 */       if (p1 >= firstNumValues) {
/* 209:424 */         firstI = numAttributes;
/* 210:    */       } else {
/* 211:426 */         firstI = first.index(p1);
/* 212:    */       }
/* 213:    */       int secondI;
/* 214:    */       int secondI;
/* 215:429 */       if (p2 >= secondNumValues) {
/* 216:430 */         secondI = numAttributes;
/* 217:    */       } else {
/* 218:432 */         secondI = second.index(p2);
/* 219:    */       }
/* 220:435 */       if (firstI == classIndex)
/* 221:    */       {
/* 222:436 */         p1++;
/* 223:    */       }
/* 224:439 */       else if ((firstI < numAttributes) && (this.m_ActiveIndices[firstI] == 0))
/* 225:    */       {
/* 226:440 */         p1++;
/* 227:    */       }
/* 228:444 */       else if (secondI == classIndex)
/* 229:    */       {
/* 230:445 */         p2++;
/* 231:    */       }
/* 232:448 */       else if ((secondI < numAttributes) && (this.m_ActiveIndices[secondI] == 0))
/* 233:    */       {
/* 234:449 */         p2++;
/* 235:    */       }
/* 236:    */       else
/* 237:    */       {
/* 238:    */         double diff;
/* 239:455 */         if (firstI == secondI)
/* 240:    */         {
/* 241:456 */           double diff = difference(firstI, first.valueSparse(p1), second.valueSparse(p2));
/* 242:457 */           p1++;
/* 243:458 */           p2++;
/* 244:    */         }
/* 245:459 */         else if (firstI > secondI)
/* 246:    */         {
/* 247:460 */           double diff = difference(secondI, 0.0D, second.valueSparse(p2));
/* 248:461 */           p2++;
/* 249:    */         }
/* 250:    */         else
/* 251:    */         {
/* 252:463 */           diff = difference(firstI, first.valueSparse(p1), 0.0D);
/* 253:464 */           p1++;
/* 254:    */         }
/* 255:466 */         if (stats != null) {
/* 256:467 */           stats.incrCoordCount();
/* 257:    */         }
/* 258:470 */         distance = updateDistance(distance, diff);
/* 259:471 */         if (distance > cutOffValue) {
/* 260:472 */           return (1.0D / 0.0D);
/* 261:    */         }
/* 262:    */       }
/* 263:    */     }
/* 264:476 */     return distance;
/* 265:    */   }
/* 266:    */   
/* 267:    */   protected abstract double updateDistance(double paramDouble1, double paramDouble2);
/* 268:    */   
/* 269:    */   protected double norm(double x, int i)
/* 270:    */   {
/* 271:499 */     if ((Double.isNaN(this.m_Ranges[i][0])) || (this.m_Ranges[i][1] == this.m_Ranges[i][0])) {
/* 272:501 */       return 0.0D;
/* 273:    */     }
/* 274:503 */     return (x - this.m_Ranges[i][0]) / this.m_Ranges[i][2];
/* 275:    */   }
/* 276:    */   
/* 277:    */   protected double difference(int index, double val1, double val2)
/* 278:    */   {
/* 279:516 */     switch (this.m_Data.attribute(index).type())
/* 280:    */     {
/* 281:    */     case 1: 
/* 282:518 */       if ((Utils.isMissingValue(val1)) || (Utils.isMissingValue(val2)) || ((int)val1 != (int)val2)) {
/* 283:520 */         return 1.0D;
/* 284:    */       }
/* 285:522 */       return 0.0D;
/* 286:    */     case 0: 
/* 287:526 */       if ((Utils.isMissingValue(val1)) || (Utils.isMissingValue(val2)))
/* 288:    */       {
/* 289:527 */         if ((Utils.isMissingValue(val1)) && (Utils.isMissingValue(val2)))
/* 290:    */         {
/* 291:528 */           if (!this.m_DontNormalize) {
/* 292:529 */             return 1.0D;
/* 293:    */           }
/* 294:531 */           return this.m_Ranges[index][1] - this.m_Ranges[index][0];
/* 295:    */         }
/* 296:    */         double diff;
/* 297:    */         double diff;
/* 298:535 */         if (Utils.isMissingValue(val2)) {
/* 299:536 */           diff = !this.m_DontNormalize ? norm(val1, index) : val1;
/* 300:    */         } else {
/* 301:538 */           diff = !this.m_DontNormalize ? norm(val2, index) : val2;
/* 302:    */         }
/* 303:540 */         if ((!this.m_DontNormalize) && (diff < 0.5D))
/* 304:    */         {
/* 305:541 */           diff = 1.0D - diff;
/* 306:    */         }
/* 307:542 */         else if (this.m_DontNormalize)
/* 308:    */         {
/* 309:543 */           if (this.m_Ranges[index][1] - diff > diff - this.m_Ranges[index][0]) {
/* 310:544 */             return this.m_Ranges[index][1] - diff;
/* 311:    */           }
/* 312:546 */           return diff - this.m_Ranges[index][0];
/* 313:    */         }
/* 314:549 */         return diff;
/* 315:    */       }
/* 316:552 */       return !this.m_DontNormalize ? norm(val1, index) - norm(val2, index) : val1 - val2;
/* 317:    */     }
/* 318:557 */     return 0.0D;
/* 319:    */   }
/* 320:    */   
/* 321:    */   public double[][] initializeRanges()
/* 322:    */   {
/* 323:567 */     if (this.m_Data == null)
/* 324:    */     {
/* 325:568 */       this.m_Ranges = ((double[][])null);
/* 326:569 */       return this.m_Ranges;
/* 327:    */     }
/* 328:572 */     int numAtt = this.m_Data.numAttributes();
/* 329:573 */     double[][] ranges = new double[numAtt][3];
/* 330:575 */     if (this.m_Data.numInstances() <= 0)
/* 331:    */     {
/* 332:576 */       initializeRangesEmpty(numAtt, ranges);
/* 333:577 */       this.m_Ranges = ranges;
/* 334:578 */       return this.m_Ranges;
/* 335:    */     }
/* 336:581 */     updateRangesFirst(this.m_Data.instance(0), numAtt, ranges);
/* 337:585 */     for (int i = 1; i < this.m_Data.numInstances(); i++) {
/* 338:586 */       updateRanges(this.m_Data.instance(i), numAtt, ranges);
/* 339:    */     }
/* 340:589 */     this.m_Ranges = ranges;
/* 341:    */     
/* 342:591 */     return this.m_Ranges;
/* 343:    */   }
/* 344:    */   
/* 345:    */   public void updateRangesFirst(Instance instance, int numAtt, double[][] ranges)
/* 346:    */   {
/* 347:604 */     for (int j = 0; j < numAtt; j++) {
/* 348:605 */       if (!instance.isMissing(j))
/* 349:    */       {
/* 350:606 */         ranges[j][0] = instance.value(j);
/* 351:607 */         ranges[j][1] = instance.value(j);
/* 352:608 */         ranges[j][2] = 0.0D;
/* 353:    */       }
/* 354:    */       else
/* 355:    */       {
/* 356:610 */         ranges[j][0] = (1.0D / 0.0D);
/* 357:611 */         ranges[j][1] = (-1.0D / 0.0D);
/* 358:612 */         ranges[j][2] = (1.0D / 0.0D);
/* 359:    */       }
/* 360:    */     }
/* 361:    */   }
/* 362:    */   
/* 363:    */   public void updateRanges(Instance instance, int numAtt, double[][] ranges)
/* 364:    */   {
/* 365:627 */     for (int j = 0; j < numAtt; j++)
/* 366:    */     {
/* 367:628 */       double value = instance.value(j);
/* 368:629 */       if (!instance.isMissing(j)) {
/* 369:630 */         if (value < ranges[j][0])
/* 370:    */         {
/* 371:631 */           ranges[j][0] = value;
/* 372:632 */           ranges[j][2] = (ranges[j][1] - ranges[j][0]);
/* 373:633 */           if (value > ranges[j][1])
/* 374:    */           {
/* 375:634 */             ranges[j][1] = value;
/* 376:635 */             ranges[j][2] = (ranges[j][1] - ranges[j][0]);
/* 377:    */           }
/* 378:    */         }
/* 379:638 */         else if (value > ranges[j][1])
/* 380:    */         {
/* 381:639 */           ranges[j][1] = value;
/* 382:640 */           ranges[j][2] = (ranges[j][1] - ranges[j][0]);
/* 383:    */         }
/* 384:    */       }
/* 385:    */     }
/* 386:    */   }
/* 387:    */   
/* 388:    */   public void initializeRangesEmpty(int numAtt, double[][] ranges)
/* 389:    */   {
/* 390:654 */     for (int j = 0; j < numAtt; j++)
/* 391:    */     {
/* 392:655 */       ranges[j][0] = (1.0D / 0.0D);
/* 393:656 */       ranges[j][1] = (-1.0D / 0.0D);
/* 394:657 */       ranges[j][2] = (1.0D / 0.0D);
/* 395:    */     }
/* 396:    */   }
/* 397:    */   
/* 398:    */   public double[][] updateRanges(Instance instance, double[][] ranges)
/* 399:    */   {
/* 400:670 */     for (int j = 0; j < ranges.length; j++)
/* 401:    */     {
/* 402:671 */       double value = instance.value(j);
/* 403:672 */       if (!instance.isMissing(j)) {
/* 404:673 */         if (value < ranges[j][0])
/* 405:    */         {
/* 406:674 */           ranges[j][0] = value;
/* 407:675 */           ranges[j][2] = (ranges[j][1] - ranges[j][0]);
/* 408:    */         }
/* 409:677 */         else if (instance.value(j) > ranges[j][1])
/* 410:    */         {
/* 411:678 */           ranges[j][1] = value;
/* 412:679 */           ranges[j][2] = (ranges[j][1] - ranges[j][0]);
/* 413:    */         }
/* 414:    */       }
/* 415:    */     }
/* 416:685 */     return ranges;
/* 417:    */   }
/* 418:    */   
/* 419:    */   public double[][] initializeRanges(int[] instList)
/* 420:    */     throws Exception
/* 421:    */   {
/* 422:697 */     if (this.m_Data == null) {
/* 423:698 */       throw new Exception("No instances supplied.");
/* 424:    */     }
/* 425:701 */     int numAtt = this.m_Data.numAttributes();
/* 426:702 */     double[][] ranges = new double[numAtt][3];
/* 427:704 */     if (this.m_Data.numInstances() <= 0)
/* 428:    */     {
/* 429:705 */       initializeRangesEmpty(numAtt, ranges);
/* 430:706 */       return ranges;
/* 431:    */     }
/* 432:709 */     updateRangesFirst(this.m_Data.instance(instList[0]), numAtt, ranges);
/* 433:711 */     for (int i = 1; i < instList.length; i++) {
/* 434:712 */       updateRanges(this.m_Data.instance(instList[i]), numAtt, ranges);
/* 435:    */     }
/* 436:715 */     return ranges;
/* 437:    */   }
/* 438:    */   
/* 439:    */   public double[][] initializeRanges(int[] instList, int startIdx, int endIdx)
/* 440:    */     throws Exception
/* 441:    */   {
/* 442:732 */     if (this.m_Data == null) {
/* 443:733 */       throw new Exception("No instances supplied.");
/* 444:    */     }
/* 445:736 */     int numAtt = this.m_Data.numAttributes();
/* 446:737 */     double[][] ranges = new double[numAtt][3];
/* 447:739 */     if (this.m_Data.numInstances() <= 0)
/* 448:    */     {
/* 449:740 */       initializeRangesEmpty(numAtt, ranges);
/* 450:741 */       return ranges;
/* 451:    */     }
/* 452:744 */     updateRangesFirst(this.m_Data.instance(instList[startIdx]), numAtt, ranges);
/* 453:746 */     for (int i = startIdx + 1; i <= endIdx; i++) {
/* 454:747 */       updateRanges(this.m_Data.instance(instList[i]), numAtt, ranges);
/* 455:    */     }
/* 456:751 */     return ranges;
/* 457:    */   }
/* 458:    */   
/* 459:    */   public void updateRanges(Instance instance)
/* 460:    */   {
/* 461:760 */     validate();
/* 462:    */     
/* 463:762 */     this.m_Ranges = updateRanges(instance, this.m_Ranges);
/* 464:    */   }
/* 465:    */   
/* 466:    */   public boolean inRanges(Instance instance, double[][] ranges)
/* 467:    */   {
/* 468:773 */     boolean isIn = true;
/* 469:776 */     for (int j = 0; (isIn) && (j < ranges.length); j++) {
/* 470:777 */       if (!instance.isMissing(j))
/* 471:    */       {
/* 472:778 */         double value = instance.value(j);
/* 473:779 */         isIn = value <= ranges[j][1];
/* 474:780 */         if (isIn) {
/* 475:781 */           isIn = value >= ranges[j][0];
/* 476:    */         }
/* 477:    */       }
/* 478:    */     }
/* 479:786 */     return isIn;
/* 480:    */   }
/* 481:    */   
/* 482:    */   public boolean rangesSet()
/* 483:    */   {
/* 484:795 */     return this.m_Ranges != null;
/* 485:    */   }
/* 486:    */   
/* 487:    */   public double[][] getRanges()
/* 488:    */     throws Exception
/* 489:    */   {
/* 490:805 */     validate();
/* 491:807 */     if (this.m_Ranges == null) {
/* 492:808 */       throw new Exception("Ranges not yet set.");
/* 493:    */     }
/* 494:811 */     return this.m_Ranges;
/* 495:    */   }
/* 496:    */   
/* 497:    */   public void clean()
/* 498:    */   {
/* 499:816 */     this.m_Data = new Instances(this.m_Data, 0);
/* 500:    */   }
/* 501:    */   
/* 502:    */   public String toString()
/* 503:    */   {
/* 504:826 */     return "";
/* 505:    */   }
/* 506:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.NormalizableDistance
 * JD-Core Version:    0.7.0.1
 */