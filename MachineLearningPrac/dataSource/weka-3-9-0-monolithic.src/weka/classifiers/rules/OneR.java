/*   1:    */ package weka.classifiers.rules;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.LinkedList;
/*   8:    */ import java.util.ListIterator;
/*   9:    */ import java.util.Vector;
/*  10:    */ import weka.classifiers.AbstractClassifier;
/*  11:    */ import weka.classifiers.Classifier;
/*  12:    */ import weka.classifiers.Sourcable;
/*  13:    */ import weka.core.Attribute;
/*  14:    */ import weka.core.Capabilities;
/*  15:    */ import weka.core.Capabilities.Capability;
/*  16:    */ import weka.core.Instance;
/*  17:    */ import weka.core.Instances;
/*  18:    */ import weka.core.Option;
/*  19:    */ import weka.core.RevisionHandler;
/*  20:    */ import weka.core.RevisionUtils;
/*  21:    */ import weka.core.TechnicalInformation;
/*  22:    */ import weka.core.TechnicalInformation.Field;
/*  23:    */ import weka.core.TechnicalInformation.Type;
/*  24:    */ import weka.core.TechnicalInformationHandler;
/*  25:    */ import weka.core.Utils;
/*  26:    */ import weka.core.WekaException;
/*  27:    */ 
/*  28:    */ public class OneR
/*  29:    */   extends AbstractClassifier
/*  30:    */   implements TechnicalInformationHandler, Sourcable
/*  31:    */ {
/*  32:    */   static final long serialVersionUID = -3459427003147861443L;
/*  33:    */   private OneRRule m_rule;
/*  34:    */   
/*  35:    */   public String globalInfo()
/*  36:    */   {
/*  37:101 */     return "Class for building and using a 1R classifier; in other words, uses the minimum-error attribute for prediction, discretizing numeric attributes. For more information, see:\n\n" + getTechnicalInformation().toString();
/*  38:    */   }
/*  39:    */   
/*  40:    */   public TechnicalInformation getTechnicalInformation()
/*  41:    */   {
/*  42:118 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  43:119 */     result.setValue(TechnicalInformation.Field.AUTHOR, "R.C. Holte");
/*  44:120 */     result.setValue(TechnicalInformation.Field.YEAR, "1993");
/*  45:121 */     result.setValue(TechnicalInformation.Field.TITLE, "Very simple classification rules perform well on most commonly used datasets");
/*  46:    */     
/*  47:    */ 
/*  48:124 */     result.setValue(TechnicalInformation.Field.JOURNAL, "Machine Learning");
/*  49:125 */     result.setValue(TechnicalInformation.Field.VOLUME, "11");
/*  50:126 */     result.setValue(TechnicalInformation.Field.PAGES, "63-91");
/*  51:    */     
/*  52:128 */     return result;
/*  53:    */   }
/*  54:    */   
/*  55:    */   private class OneRRule
/*  56:    */     implements Serializable, RevisionHandler
/*  57:    */   {
/*  58:    */     static final long serialVersionUID = 2252814630957092281L;
/*  59:    */     private final Attribute m_class;
/*  60:    */     private final int m_numInst;
/*  61:    */     private final Attribute m_attr;
/*  62:    */     private int m_correct;
/*  63:    */     private final int[] m_classifications;
/*  64:155 */     private int m_missingValueClass = -1;
/*  65:    */     private double[] m_breakpoints;
/*  66:    */     
/*  67:    */     public OneRRule(Instances data, Attribute attribute)
/*  68:    */       throws Exception
/*  69:    */     {
/*  70:169 */       this.m_class = data.classAttribute();
/*  71:170 */       this.m_numInst = data.numInstances();
/*  72:171 */       this.m_attr = attribute;
/*  73:172 */       this.m_correct = 0;
/*  74:173 */       this.m_classifications = new int[this.m_attr.numValues()];
/*  75:    */     }
/*  76:    */     
/*  77:    */     public OneRRule(Instances data, Attribute attribute, int nBreaks)
/*  78:    */       throws Exception
/*  79:    */     {
/*  80:186 */       this.m_class = data.classAttribute();
/*  81:187 */       this.m_numInst = data.numInstances();
/*  82:188 */       this.m_attr = attribute;
/*  83:189 */       this.m_correct = 0;
/*  84:190 */       this.m_classifications = new int[nBreaks];
/*  85:191 */       this.m_breakpoints = new double[nBreaks - 1];
/*  86:    */     }
/*  87:    */     
/*  88:    */     public String toString()
/*  89:    */     {
/*  90:    */       try
/*  91:    */       {
/*  92:203 */         StringBuffer text = new StringBuffer();
/*  93:204 */         text.append(this.m_attr.name() + ":\n");
/*  94:205 */         for (int v = 0; v < this.m_classifications.length; v++)
/*  95:    */         {
/*  96:206 */           text.append("\t");
/*  97:207 */           if (this.m_attr.isNominal()) {
/*  98:208 */             text.append(this.m_attr.value(v));
/*  99:209 */           } else if (v < this.m_breakpoints.length) {
/* 100:210 */             text.append("< " + this.m_breakpoints[v]);
/* 101:211 */           } else if (v > 0) {
/* 102:212 */             text.append(">= " + this.m_breakpoints[(v - 1)]);
/* 103:    */           } else {
/* 104:214 */             text.append("not ?");
/* 105:    */           }
/* 106:216 */           text.append("\t-> " + this.m_class.value(this.m_classifications[v]) + "\n");
/* 107:    */         }
/* 108:218 */         if (this.m_missingValueClass != -1) {
/* 109:219 */           text.append("\t?\t-> " + this.m_class.value(this.m_missingValueClass) + "\n");
/* 110:    */         }
/* 111:221 */         text.append("(" + this.m_correct + "/" + this.m_numInst + " instances correct)\n");
/* 112:    */         
/* 113:223 */         return text.toString();
/* 114:    */       }
/* 115:    */       catch (Exception e) {}
/* 116:225 */       return "Can't print OneR classifier!";
/* 117:    */     }
/* 118:    */     
/* 119:    */     public String getRevision()
/* 120:    */     {
/* 121:236 */       return RevisionUtils.extract("$Revision: 10153 $");
/* 122:    */     }
/* 123:    */   }
/* 124:    */   
/* 125:244 */   private int m_minBucketSize = 6;
/* 126:    */   private Classifier m_ZeroR;
/* 127:    */   
/* 128:    */   public double classifyInstance(Instance inst)
/* 129:    */     throws Exception
/* 130:    */   {
/* 131:259 */     if (this.m_ZeroR != null) {
/* 132:260 */       return this.m_ZeroR.classifyInstance(inst);
/* 133:    */     }
/* 134:263 */     int v = 0;
/* 135:264 */     if (inst.isMissing(this.m_rule.m_attr))
/* 136:    */     {
/* 137:265 */       if (this.m_rule.m_missingValueClass != -1) {
/* 138:266 */         return this.m_rule.m_missingValueClass;
/* 139:    */       }
/* 140:268 */       return 0.0D;
/* 141:    */     }
/* 142:271 */     if (this.m_rule.m_attr.isNominal()) {
/* 143:272 */       v = (int)inst.value(this.m_rule.m_attr);
/* 144:    */     } else {
/* 145:275 */       while ((v < this.m_rule.m_breakpoints.length) && (inst.value(this.m_rule.m_attr) >= this.m_rule.m_breakpoints[v])) {
/* 146:276 */         v++;
/* 147:    */       }
/* 148:    */     }
/* 149:279 */     return this.m_rule.m_classifications[v];
/* 150:    */   }
/* 151:    */   
/* 152:    */   public Capabilities getCapabilities()
/* 153:    */   {
/* 154:289 */     Capabilities result = super.getCapabilities();
/* 155:290 */     result.disableAll();
/* 156:    */     
/* 157:    */ 
/* 158:293 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 159:294 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 160:295 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/* 161:296 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 162:    */     
/* 163:    */ 
/* 164:299 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 165:300 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 166:    */     
/* 167:302 */     return result;
/* 168:    */   }
/* 169:    */   
/* 170:    */   public void buildClassifier(Instances instances)
/* 171:    */     throws Exception
/* 172:    */   {
/* 173:314 */     boolean noRule = true;
/* 174:    */     
/* 175:    */ 
/* 176:317 */     getCapabilities().testWithFail(instances);
/* 177:    */     
/* 178:    */ 
/* 179:320 */     Instances data = new Instances(instances);
/* 180:321 */     data.deleteWithMissingClass();
/* 181:324 */     if (data.numAttributes() == 1)
/* 182:    */     {
/* 183:325 */       System.err.println("Cannot build model (only class attribute present in data!), using ZeroR model instead!");
/* 184:    */       
/* 185:    */ 
/* 186:328 */       this.m_ZeroR = new ZeroR();
/* 187:329 */       this.m_ZeroR.buildClassifier(data);
/* 188:330 */       return;
/* 189:    */     }
/* 190:332 */     this.m_ZeroR = null;
/* 191:    */     
/* 192:    */ 
/* 193:    */ 
/* 194:336 */     Enumeration<Attribute> enu = instances.enumerateAttributes();
/* 195:337 */     while (enu.hasMoreElements()) {
/* 196:    */       try
/* 197:    */       {
/* 198:339 */         OneRRule r = newRule((Attribute)enu.nextElement(), data);
/* 199:342 */         if ((noRule) || (r.m_correct > this.m_rule.m_correct)) {
/* 200:343 */           this.m_rule = r;
/* 201:    */         }
/* 202:345 */         noRule = false;
/* 203:    */       }
/* 204:    */       catch (Exception ex) {}
/* 205:    */     }
/* 206:350 */     if (noRule) {
/* 207:351 */       throw new WekaException("No attributes found to work with!");
/* 208:    */     }
/* 209:    */   }
/* 210:    */   
/* 211:    */   public OneRRule newRule(Attribute attr, Instances data)
/* 212:    */     throws Exception
/* 213:    */   {
/* 214:368 */     int[] missingValueCounts = new int[data.classAttribute().numValues()];
/* 215:    */     OneRRule r;
/* 216:    */     OneRRule r;
/* 217:370 */     if (attr.isNominal()) {
/* 218:371 */       r = newNominalRule(attr, data, missingValueCounts);
/* 219:    */     } else {
/* 220:373 */       r = newNumericRule(attr, data, missingValueCounts);
/* 221:    */     }
/* 222:375 */     r.m_missingValueClass = Utils.maxIndex(missingValueCounts);
/* 223:376 */     if (missingValueCounts[r.m_missingValueClass] == 0) {
/* 224:377 */       r.m_missingValueClass = -1;
/* 225:    */     } else {
/* 226:379 */       OneRRule.access$412(r, missingValueCounts[r.m_missingValueClass]);
/* 227:    */     }
/* 228:381 */     return r;
/* 229:    */   }
/* 230:    */   
/* 231:    */   public OneRRule newNominalRule(Attribute attr, Instances data, int[] missingValueCounts)
/* 232:    */     throws Exception
/* 233:    */   {
/* 234:397 */     int[][] counts = new int[attr.numValues()][data.classAttribute().numValues()];
/* 235:    */     
/* 236:    */ 
/* 237:    */ 
/* 238:401 */     Enumeration<Instance> enu = data.enumerateInstances();
/* 239:402 */     while (enu.hasMoreElements())
/* 240:    */     {
/* 241:403 */       Instance i = (Instance)enu.nextElement();
/* 242:404 */       if (i.isMissing(attr)) {
/* 243:405 */         missingValueCounts[((int)i.classValue())] += 1;
/* 244:    */       } else {
/* 245:407 */         counts[((int)i.value(attr))][((int)i.classValue())] += 1;
/* 246:    */       }
/* 247:    */     }
/* 248:411 */     OneRRule r = new OneRRule(data, attr);
/* 249:412 */     for (int value = 0; value < attr.numValues(); value++)
/* 250:    */     {
/* 251:413 */       int best = Utils.maxIndex(counts[value]);
/* 252:414 */       r.m_classifications[value] = best;
/* 253:415 */       OneRRule.access$412(r, counts[value][best]);
/* 254:    */     }
/* 255:417 */     return r;
/* 256:    */   }
/* 257:    */   
/* 258:    */   public OneRRule newNumericRule(Attribute attr, Instances data, int[] missingValueCounts)
/* 259:    */     throws Exception
/* 260:    */   {
/* 261:435 */     data = new Instances(data);
/* 262:    */     
/* 263:437 */     int lastInstance = data.numInstances();
/* 264:    */     
/* 265:    */ 
/* 266:440 */     data.sort(attr);
/* 267:441 */     while ((lastInstance > 0) && (data.instance(lastInstance - 1).isMissing(attr)))
/* 268:    */     {
/* 269:442 */       lastInstance--;
/* 270:443 */       missingValueCounts[((int)data.instance(lastInstance).classValue())] += 1;
/* 271:    */     }
/* 272:445 */     if (lastInstance == 0) {
/* 273:446 */       throw new Exception("Only missing values in the training data!");
/* 274:    */     }
/* 275:450 */     double lastValue = 0.0D;
/* 276:451 */     LinkedList<int[]> distributions = new LinkedList();
/* 277:452 */     LinkedList<Double> values = new LinkedList();
/* 278:453 */     int[] distribution = null;
/* 279:454 */     for (int i = 0; i < lastInstance; i++)
/* 280:    */     {
/* 281:457 */       if ((i == 0) || (data.instance(i).value(attr) > lastValue))
/* 282:    */       {
/* 283:458 */         if (i != 0) {
/* 284:459 */           values.add(Double.valueOf((lastValue + data.instance(i).value(attr)) / 2.0D));
/* 285:    */         }
/* 286:461 */         lastValue = data.instance(i).value(attr);
/* 287:462 */         distribution = new int[data.numClasses()];
/* 288:463 */         distributions.add(distribution);
/* 289:    */       }
/* 290:465 */       distribution[((int)data.instance(i).classValue())] += 1;
/* 291:    */     }
/* 292:467 */     values.add(Double.valueOf(1.7976931348623157E+308D));
/* 293:    */     
/* 294:    */ 
/* 295:470 */     ListIterator<int[]> it = distributions.listIterator();
/* 296:471 */     ListIterator<Double> itVals = values.listIterator();
/* 297:472 */     int[] oldDist = null;
/* 298:473 */     while (it.hasNext())
/* 299:    */     {
/* 300:476 */       int[] newDist = (int[])it.next();
/* 301:477 */       itVals.next();
/* 302:480 */       if ((oldDist != null) && ((Utils.maxIndex(newDist) == Utils.maxIndex(oldDist)) || (oldDist[Utils.maxIndex(oldDist)] < this.m_minBucketSize)))
/* 303:    */       {
/* 304:489 */         for (int j = 0; j < oldDist.length; j++) {
/* 305:490 */           newDist[j] += oldDist[j];
/* 306:    */         }
/* 307:494 */         it.previous();
/* 308:495 */         it.previous();
/* 309:496 */         it.remove();
/* 310:497 */         it.next();
/* 311:    */         
/* 312:    */ 
/* 313:500 */         itVals.previous();
/* 314:501 */         itVals.previous();
/* 315:502 */         itVals.remove();
/* 316:503 */         itVals.next();
/* 317:    */       }
/* 318:507 */       oldDist = newDist;
/* 319:    */     }
/* 320:512 */     int numCorrect = 0;
/* 321:513 */     it = distributions.listIterator();
/* 322:514 */     itVals = values.listIterator();
/* 323:515 */     oldDist = null;
/* 324:516 */     while (it.hasNext())
/* 325:    */     {
/* 326:519 */       int[] newDist = (int[])it.next();
/* 327:520 */       itVals.next();
/* 328:    */       
/* 329:    */ 
/* 330:523 */       numCorrect += newDist[Utils.maxIndex(newDist)];
/* 331:526 */       if ((oldDist != null) && (Utils.maxIndex(newDist) == Utils.maxIndex(oldDist)))
/* 332:    */       {
/* 333:532 */         for (int j = 0; j < oldDist.length; j++) {
/* 334:533 */           newDist[j] += oldDist[j];
/* 335:    */         }
/* 336:537 */         it.previous();
/* 337:538 */         it.previous();
/* 338:539 */         it.remove();
/* 339:540 */         it.next();
/* 340:    */         
/* 341:    */ 
/* 342:543 */         itVals.previous();
/* 343:544 */         itVals.previous();
/* 344:545 */         itVals.remove();
/* 345:546 */         itVals.next();
/* 346:    */       }
/* 347:550 */       oldDist = newDist;
/* 348:    */     }
/* 349:553 */     OneRRule r = new OneRRule(data, attr, distributions.size());
/* 350:    */     
/* 351:    */ 
/* 352:556 */     r.m_correct = numCorrect;
/* 353:557 */     it = distributions.listIterator();
/* 354:558 */     itVals = values.listIterator();
/* 355:559 */     int v = 0;
/* 356:560 */     while (it.hasNext())
/* 357:    */     {
/* 358:561 */       r.m_classifications[v] = Utils.maxIndex((int[])it.next());
/* 359:562 */       double splitPoint = ((Double)itVals.next()).doubleValue();
/* 360:563 */       if (itVals.hasNext()) {
/* 361:564 */         r.m_breakpoints[v] = splitPoint;
/* 362:    */       }
/* 363:566 */       v++;
/* 364:    */     }
/* 365:569 */     return r;
/* 366:    */   }
/* 367:    */   
/* 368:    */   public Enumeration<Option> listOptions()
/* 369:    */   {
/* 370:580 */     String string = "\tThe minimum number of objects in a bucket (default: 6).";
/* 371:    */     
/* 372:582 */     Vector<Option> newVector = new Vector(1);
/* 373:    */     
/* 374:584 */     newVector.addElement(new Option(string, "B", 1, "-B <minimum bucket size>"));
/* 375:    */     
/* 376:    */ 
/* 377:587 */     newVector.addAll(Collections.list(super.listOptions()));
/* 378:    */     
/* 379:589 */     return newVector.elements();
/* 380:    */   }
/* 381:    */   
/* 382:    */   public void setOptions(String[] options)
/* 383:    */     throws Exception
/* 384:    */   {
/* 385:612 */     String bucketSizeString = Utils.getOption('B', options);
/* 386:613 */     if (bucketSizeString.length() != 0) {
/* 387:614 */       this.m_minBucketSize = Integer.parseInt(bucketSizeString);
/* 388:    */     } else {
/* 389:616 */       this.m_minBucketSize = 6;
/* 390:    */     }
/* 391:619 */     super.setOptions(options);
/* 392:    */   }
/* 393:    */   
/* 394:    */   public String[] getOptions()
/* 395:    */   {
/* 396:630 */     Vector<String> options = new Vector(1);
/* 397:    */     
/* 398:632 */     options.add("-B");
/* 399:633 */     options.add("" + this.m_minBucketSize);
/* 400:    */     
/* 401:635 */     Collections.addAll(options, super.getOptions());
/* 402:    */     
/* 403:637 */     return (String[])options.toArray(new String[0]);
/* 404:    */   }
/* 405:    */   
/* 406:    */   public String toSource(String className)
/* 407:    */     throws Exception
/* 408:    */   {
/* 409:664 */     StringBuffer result = new StringBuffer();
/* 410:666 */     if (this.m_ZeroR != null)
/* 411:    */     {
/* 412:667 */       result.append(((ZeroR)this.m_ZeroR).toSource(className));
/* 413:    */     }
/* 414:    */     else
/* 415:    */     {
/* 416:669 */       result.append("class " + className + " {\n");
/* 417:670 */       result.append("  public static double classify(Object[] i) {\n");
/* 418:671 */       result.append("    // chosen attribute: " + this.m_rule.m_attr.name() + " (" + this.m_rule.m_attr.index() + ")\n");
/* 419:    */       
/* 420:673 */       result.append("\n");
/* 421:    */       
/* 422:675 */       result.append("    // missing value?\n");
/* 423:676 */       result.append("    if (i[" + this.m_rule.m_attr.index() + "] == null)\n");
/* 424:677 */       if (this.m_rule.m_missingValueClass != -1) {
/* 425:678 */         result.append("      return Double.NaN;\n");
/* 426:    */       } else {
/* 427:680 */         result.append("      return 0;\n");
/* 428:    */       }
/* 429:682 */       result.append("\n");
/* 430:    */       
/* 431:    */ 
/* 432:685 */       result.append("    // prediction\n");
/* 433:686 */       result.append("    double v = 0;\n");
/* 434:687 */       result.append("    double[] classifications = new double[]{" + Utils.arrayToString(this.m_rule.m_classifications) + "};");
/* 435:    */       
/* 436:689 */       result.append(" // ");
/* 437:690 */       for (int i = 0; i < this.m_rule.m_classifications.length; i++)
/* 438:    */       {
/* 439:691 */         if (i > 0) {
/* 440:692 */           result.append(", ");
/* 441:    */         }
/* 442:694 */         result.append(this.m_rule.m_class.value(this.m_rule.m_classifications[i]));
/* 443:    */       }
/* 444:696 */       result.append("\n");
/* 445:697 */       if (this.m_rule.m_attr.isNominal()) {
/* 446:698 */         for (i = 0; i < this.m_rule.m_attr.numValues(); i++)
/* 447:    */         {
/* 448:699 */           result.append("    ");
/* 449:700 */           if (i > 0) {
/* 450:701 */             result.append("else ");
/* 451:    */           }
/* 452:703 */           result.append("if (((String) i[" + this.m_rule.m_attr.index() + "]).equals(\"" + this.m_rule.m_attr.value(i) + "\"))\n");
/* 453:    */           
/* 454:705 */           result.append("      v = " + i + "; // " + this.m_rule.m_class.value(this.m_rule.m_classifications[i]) + "\n");
/* 455:    */         }
/* 456:    */       }
/* 457:709 */       result.append("    double[] breakpoints = new double[]{" + Utils.arrayToString(this.m_rule.m_breakpoints) + "};\n");
/* 458:    */       
/* 459:711 */       result.append("    while (v < breakpoints.length && \n");
/* 460:712 */       result.append("           ((Double) i[" + this.m_rule.m_attr.index() + "]) >= breakpoints[(int) v]) {\n");
/* 461:    */       
/* 462:714 */       result.append("      v++;\n");
/* 463:715 */       result.append("    }\n");
/* 464:    */       
/* 465:717 */       result.append("    return classifications[(int) v];\n");
/* 466:    */       
/* 467:719 */       result.append("  }\n");
/* 468:720 */       result.append("}\n");
/* 469:    */     }
/* 470:723 */     return result.toString();
/* 471:    */   }
/* 472:    */   
/* 473:    */   public String toString()
/* 474:    */   {
/* 475:735 */     if (this.m_ZeroR != null)
/* 476:    */     {
/* 477:736 */       StringBuffer buf = new StringBuffer();
/* 478:737 */       buf.append(getClass().getName().replaceAll(".*\\.", "") + "\n");
/* 479:738 */       buf.append(getClass().getName().replaceAll(".*\\.", "").replaceAll(".", "=") + "\n\n");
/* 480:    */       
/* 481:    */ 
/* 482:741 */       buf.append("Warning: No model could be built, hence ZeroR model is used:\n\n");
/* 483:    */       
/* 484:743 */       buf.append(this.m_ZeroR.toString());
/* 485:744 */       return buf.toString();
/* 486:    */     }
/* 487:747 */     if (this.m_rule == null) {
/* 488:748 */       return "OneR: No model built yet.";
/* 489:    */     }
/* 490:750 */     return this.m_rule.toString();
/* 491:    */   }
/* 492:    */   
/* 493:    */   public String minBucketSizeTipText()
/* 494:    */   {
/* 495:760 */     return "The minimum bucket size used for discretizing numeric attributes.";
/* 496:    */   }
/* 497:    */   
/* 498:    */   public int getMinBucketSize()
/* 499:    */   {
/* 500:771 */     return this.m_minBucketSize;
/* 501:    */   }
/* 502:    */   
/* 503:    */   public void setMinBucketSize(int v)
/* 504:    */   {
/* 505:781 */     this.m_minBucketSize = v;
/* 506:    */   }
/* 507:    */   
/* 508:    */   public String getRevision()
/* 509:    */   {
/* 510:791 */     return RevisionUtils.extract("$Revision: 10153 $");
/* 511:    */   }
/* 512:    */   
/* 513:    */   public static void main(String[] argv)
/* 514:    */   {
/* 515:800 */     runClassifier(new OneR(), argv);
/* 516:    */   }
/* 517:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.rules.OneR
 * JD-Core Version:    0.7.0.1
 */