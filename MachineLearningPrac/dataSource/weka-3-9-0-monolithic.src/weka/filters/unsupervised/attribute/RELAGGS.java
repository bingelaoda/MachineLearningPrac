/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.Hashtable;
/*   8:    */ import java.util.Vector;
/*   9:    */ import weka.core.Attribute;
/*  10:    */ import weka.core.AttributeStats;
/*  11:    */ import weka.core.Capabilities;
/*  12:    */ import weka.core.Capabilities.Capability;
/*  13:    */ import weka.core.DenseInstance;
/*  14:    */ import weka.core.Instance;
/*  15:    */ import weka.core.Instances;
/*  16:    */ import weka.core.Option;
/*  17:    */ import weka.core.Range;
/*  18:    */ import weka.core.RevisionUtils;
/*  19:    */ import weka.core.SparseInstance;
/*  20:    */ import weka.core.TechnicalInformation;
/*  21:    */ import weka.core.TechnicalInformation.Field;
/*  22:    */ import weka.core.TechnicalInformation.Type;
/*  23:    */ import weka.core.TechnicalInformationHandler;
/*  24:    */ import weka.core.Utils;
/*  25:    */ import weka.experiment.Stats;
/*  26:    */ import weka.filters.SimpleBatchFilter;
/*  27:    */ 
/*  28:    */ public class RELAGGS
/*  29:    */   extends SimpleBatchFilter
/*  30:    */   implements TechnicalInformationHandler
/*  31:    */ {
/*  32:    */   private static final long serialVersionUID = -3333791375278589231L;
/*  33:115 */   protected int m_MaxCardinality = 20;
/*  34:120 */   protected Range m_SelectedRange = new Range("first-last");
/*  35:126 */   protected Hashtable<String, AttributeStats> m_AttStats = null;
/*  36:129 */   protected boolean m_GenerateSparseInstances = false;
/*  37:132 */   protected boolean m_DisableMIN = false;
/*  38:133 */   protected boolean m_DisableMAX = false;
/*  39:134 */   protected boolean m_DisableAVG = false;
/*  40:135 */   protected boolean m_DisableSTDEV = false;
/*  41:136 */   protected boolean m_DisableSUM = false;
/*  42:    */   
/*  43:    */   public String globalInfo()
/*  44:    */   {
/*  45:146 */     return "A propositionalization filter inspired by the RELAGGS algorithm.\nIt processes all relational attributes that fall into the user defined range. Currently, the filter only processes one level of nesting.\nThe class attribute is not touched.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*  46:    */   }
/*  47:    */   
/*  48:    */   public TechnicalInformation getTechnicalInformation()
/*  49:    */   {
/*  50:164 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  51:165 */     result.setValue(TechnicalInformation.Field.AUTHOR, "M.-A. Krogel and S. Wrobel");
/*  52:166 */     result.setValue(TechnicalInformation.Field.TITLE, "Facets of Aggregation Approaches to Propositionalization");
/*  53:    */     
/*  54:168 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Work-in-Progress Track at the Thirteenth International Conference on Inductive Logic Programming (ILP)");
/*  55:    */     
/*  56:    */ 
/*  57:    */ 
/*  58:172 */     result.setValue(TechnicalInformation.Field.EDITOR, "T. Horvath and A. Yamamoto");
/*  59:173 */     result.setValue(TechnicalInformation.Field.YEAR, "2003");
/*  60:174 */     result.setValue(TechnicalInformation.Field.PDF, "http://kd.cs.uni-magdeburg.de/~krogel/papers/aggs.pdf");
/*  61:    */     
/*  62:    */ 
/*  63:177 */     return result;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public Enumeration<Option> listOptions()
/*  67:    */   {
/*  68:188 */     Vector<Option> result = new Vector(3);
/*  69:    */     
/*  70:190 */     result.addElement(new Option("\tSpecify list of string attributes to convert to words.\n\t(default: select all relational attributes)", "R", 1, "-R <index1,index2-index4,...>"));
/*  71:    */     
/*  72:    */ 
/*  73:    */ 
/*  74:    */ 
/*  75:195 */     result.addElement(new Option("\tInverts the matching sense of the selection.", "V", 0, "-V"));
/*  76:    */     
/*  77:    */ 
/*  78:198 */     result.addElement(new Option("\tMax. cardinality of nominal attributes. If a nominal attribute\n\thas more values than this upper limit, then it will be skipped.\n\t(default: 20)", "C", 1, "-C <num>"));
/*  79:    */     
/*  80:    */ 
/*  81:    */ 
/*  82:    */ 
/*  83:203 */     result.addElement(new Option("\tGenerate sparse instances.", "S", 0, "-S"));
/*  84:    */     
/*  85:    */ 
/*  86:206 */     result.addElement(new Option("\tDisable out of MIN statistic.", "disable-min", 0, "-disable-min"));
/*  87:    */     
/*  88:    */ 
/*  89:209 */     result.addElement(new Option("\tDisable out of MAX statistic.", "disable-max", 0, "-disable-max"));
/*  90:    */     
/*  91:    */ 
/*  92:212 */     result.addElement(new Option("\tDisable out of AVG statistic.", "disable-avg", 0, "-disable-avg"));
/*  93:    */     
/*  94:    */ 
/*  95:215 */     result.addElement(new Option("\tDisable out of STDEV statistic.", "disable-stdev", 0, "-disable-stdev"));
/*  96:    */     
/*  97:    */ 
/*  98:218 */     result.addElement(new Option("\tDisable out of SUM statistic.", "disable-sum", 0, "-disable-sum"));
/*  99:    */     
/* 100:    */ 
/* 101:221 */     result.addAll(Collections.list(super.listOptions()));
/* 102:    */     
/* 103:223 */     return result.elements();
/* 104:    */   }
/* 105:    */   
/* 106:    */   public void setOptions(String[] options)
/* 107:    */     throws Exception
/* 108:    */   {
/* 109:280 */     String tmpStr = Utils.getOption('R', options);
/* 110:281 */     if (tmpStr.length() != 0) {
/* 111:282 */       setSelectedRange(tmpStr);
/* 112:    */     } else {
/* 113:284 */       setSelectedRange("first-last");
/* 114:    */     }
/* 115:287 */     setInvertSelection(Utils.getFlag('V', options));
/* 116:    */     
/* 117:289 */     tmpStr = Utils.getOption('C', options);
/* 118:290 */     if (tmpStr.length() != 0) {
/* 119:291 */       setMaxCardinality(Integer.parseInt(tmpStr));
/* 120:    */     } else {
/* 121:293 */       setMaxCardinality(20);
/* 122:    */     }
/* 123:296 */     setGenerateSparseInstances(Utils.getFlag('S', options));
/* 124:    */     
/* 125:298 */     setDisableMIN(Utils.getFlag("disable-min", options));
/* 126:    */     
/* 127:300 */     setDisableMAX(Utils.getFlag("disable-max", options));
/* 128:    */     
/* 129:302 */     setDisableAVG(Utils.getFlag("disable-avg", options));
/* 130:    */     
/* 131:304 */     setDisableSTDEV(Utils.getFlag("disable-stdev", options));
/* 132:    */     
/* 133:306 */     setDisableSUM(Utils.getFlag("disable-sum", options));
/* 134:    */     
/* 135:308 */     super.setOptions(options);
/* 136:    */     
/* 137:310 */     Utils.checkForRemainingOptions(options);
/* 138:    */   }
/* 139:    */   
/* 140:    */   public String[] getOptions()
/* 141:    */   {
/* 142:321 */     Vector<String> result = new Vector();
/* 143:    */     
/* 144:323 */     result.add("-R");
/* 145:324 */     result.add(getSelectedRange().getRanges());
/* 146:326 */     if (getInvertSelection()) {
/* 147:327 */       result.add("-V");
/* 148:    */     }
/* 149:330 */     result.add("-C");
/* 150:331 */     result.add("" + getMaxCardinality());
/* 151:333 */     if (getGenerateSparseInstances()) {
/* 152:334 */       result.add("-S");
/* 153:    */     }
/* 154:337 */     if (getDisableMIN()) {
/* 155:338 */       result.add("-disable-min");
/* 156:    */     }
/* 157:341 */     if (getDisableMAX()) {
/* 158:342 */       result.add("-disable-max");
/* 159:    */     }
/* 160:345 */     if (getDisableAVG()) {
/* 161:346 */       result.add("-disable-avg");
/* 162:    */     }
/* 163:349 */     if (getDisableSTDEV()) {
/* 164:350 */       result.add("-disable-stdev");
/* 165:    */     }
/* 166:353 */     if (getDisableSUM()) {
/* 167:354 */       result.add("-disable-sum");
/* 168:    */     }
/* 169:357 */     Collections.addAll(result, super.getOptions());
/* 170:    */     
/* 171:359 */     return (String[])result.toArray(new String[result.size()]);
/* 172:    */   }
/* 173:    */   
/* 174:    */   public String disableMINTipText()
/* 175:    */   {
/* 176:369 */     return "Do not include MAX attributes in output.";
/* 177:    */   }
/* 178:    */   
/* 179:    */   public boolean getDisableMIN()
/* 180:    */   {
/* 181:377 */     return this.m_DisableMIN;
/* 182:    */   }
/* 183:    */   
/* 184:    */   public void setDisableMIN(boolean m_DisableMIN)
/* 185:    */   {
/* 186:385 */     this.m_DisableMIN = m_DisableMIN;
/* 187:    */   }
/* 188:    */   
/* 189:    */   public String disableMAXTipText()
/* 190:    */   {
/* 191:395 */     return "Do not include MAX attributes in output.";
/* 192:    */   }
/* 193:    */   
/* 194:    */   public boolean getDisableMAX()
/* 195:    */   {
/* 196:403 */     return this.m_DisableMAX;
/* 197:    */   }
/* 198:    */   
/* 199:    */   public void setDisableMAX(boolean m_DisableMAX)
/* 200:    */   {
/* 201:411 */     this.m_DisableMAX = m_DisableMAX;
/* 202:    */   }
/* 203:    */   
/* 204:    */   public String disableAVGTipText()
/* 205:    */   {
/* 206:421 */     return "Do not include AVG attributes in output.";
/* 207:    */   }
/* 208:    */   
/* 209:    */   public boolean getDisableAVG()
/* 210:    */   {
/* 211:429 */     return this.m_DisableAVG;
/* 212:    */   }
/* 213:    */   
/* 214:    */   public void setDisableAVG(boolean m_DisableAVG)
/* 215:    */   {
/* 216:437 */     this.m_DisableAVG = m_DisableAVG;
/* 217:    */   }
/* 218:    */   
/* 219:    */   public String disableSTDEVTipText()
/* 220:    */   {
/* 221:447 */     return "Do not include STDEV attributes in output.";
/* 222:    */   }
/* 223:    */   
/* 224:    */   public boolean getDisableSTDEV()
/* 225:    */   {
/* 226:455 */     return this.m_DisableSTDEV;
/* 227:    */   }
/* 228:    */   
/* 229:    */   public void setDisableSTDEV(boolean m_DisableSTDEV)
/* 230:    */   {
/* 231:463 */     this.m_DisableSTDEV = m_DisableSTDEV;
/* 232:    */   }
/* 233:    */   
/* 234:    */   public String disableSUMTipText()
/* 235:    */   {
/* 236:473 */     return "Do not include SUM attributes in output.";
/* 237:    */   }
/* 238:    */   
/* 239:    */   public boolean getDisableSUM()
/* 240:    */   {
/* 241:481 */     return this.m_DisableSUM;
/* 242:    */   }
/* 243:    */   
/* 244:    */   public void setDisableSUM(boolean m_DisableSUM)
/* 245:    */   {
/* 246:490 */     this.m_DisableSUM = m_DisableSUM;
/* 247:    */   }
/* 248:    */   
/* 249:    */   public String maxCardinalityTipText()
/* 250:    */   {
/* 251:500 */     return "The maximum number of values a nominal attribute can have before it's skipped.";
/* 252:    */   }
/* 253:    */   
/* 254:    */   public void setMaxCardinality(int value)
/* 255:    */   {
/* 256:510 */     this.m_MaxCardinality = value;
/* 257:    */   }
/* 258:    */   
/* 259:    */   public int getMaxCardinality()
/* 260:    */   {
/* 261:520 */     return this.m_MaxCardinality;
/* 262:    */   }
/* 263:    */   
/* 264:    */   public String attributeIndicesTipText()
/* 265:    */   {
/* 266:530 */     return "Specify range of attributes to act on; this is a comma separated list of attribute indices, with \"first\" and \"last\" valid values; Specify an inclusive range with \"-\"; eg: \"first-3,5,6-10,last\".";
/* 267:    */   }
/* 268:    */   
/* 269:    */   public void setSelectedRange(String value)
/* 270:    */   {
/* 271:542 */     this.m_SelectedRange = new Range(value);
/* 272:    */   }
/* 273:    */   
/* 274:    */   public Range getSelectedRange()
/* 275:    */   {
/* 276:551 */     return this.m_SelectedRange;
/* 277:    */   }
/* 278:    */   
/* 279:    */   public String invertSelectionTipText()
/* 280:    */   {
/* 281:561 */     return "Set attribute selection mode. If false, only selected attributes in the range will be worked on; if true, only non-selected attributes will be processed.";
/* 282:    */   }
/* 283:    */   
/* 284:    */   public void setInvertSelection(boolean value)
/* 285:    */   {
/* 286:572 */     this.m_SelectedRange.setInvert(value);
/* 287:    */   }
/* 288:    */   
/* 289:    */   public boolean getInvertSelection()
/* 290:    */   {
/* 291:581 */     return this.m_SelectedRange.getInvert();
/* 292:    */   }
/* 293:    */   
/* 294:    */   public String generateSparseInstancesTipText()
/* 295:    */   {
/* 296:591 */     return "Generate sparse instances rather than dense ones.";
/* 297:    */   }
/* 298:    */   
/* 299:    */   public void setGenerateSparseInstances(boolean value)
/* 300:    */   {
/* 301:600 */     this.m_GenerateSparseInstances = value;
/* 302:    */   }
/* 303:    */   
/* 304:    */   public boolean getGenerateSparseInstances()
/* 305:    */   {
/* 306:609 */     return this.m_GenerateSparseInstances;
/* 307:    */   }
/* 308:    */   
/* 309:    */   public Capabilities getCapabilities()
/* 310:    */   {
/* 311:620 */     Capabilities result = super.getCapabilities();
/* 312:621 */     result.disableAll();
/* 313:    */     
/* 314:    */ 
/* 315:624 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 316:625 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 317:626 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/* 318:627 */     result.enable(Capabilities.Capability.RELATIONAL_ATTRIBUTES);
/* 319:628 */     result.enable(Capabilities.Capability.STRING_ATTRIBUTES);
/* 320:629 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 321:    */     
/* 322:    */ 
/* 323:632 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 324:633 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/* 325:634 */     result.enable(Capabilities.Capability.DATE_CLASS);
/* 326:635 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 327:636 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 328:    */     
/* 329:638 */     return result;
/* 330:    */   }
/* 331:    */   
/* 332:    */   protected Instances determineOutputFormat(Instances inputFormat)
/* 333:    */     throws Exception
/* 334:    */   {
/* 335:667 */     this.m_SelectedRange.setUpper(inputFormat.numAttributes() - 1);
/* 336:    */     
/* 337:669 */     ArrayList<Attribute> atts = new ArrayList();
/* 338:670 */     int clsIndex = -1;
/* 339:671 */     for (int i = 0; i < inputFormat.numAttributes(); i++) {
/* 340:673 */       if (i == inputFormat.classIndex())
/* 341:    */       {
/* 342:674 */         clsIndex = atts.size();
/* 343:675 */         atts.add((Attribute)inputFormat.attribute(i).copy());
/* 344:    */       }
/* 345:679 */       else if (!inputFormat.attribute(i).isRelationValued())
/* 346:    */       {
/* 347:680 */         atts.add((Attribute)inputFormat.attribute(i).copy());
/* 348:    */       }
/* 349:684 */       else if (!this.m_SelectedRange.isInRange(i))
/* 350:    */       {
/* 351:685 */         Attribute relAtt = inputFormat.attribute(i);
/* 352:686 */         atts.add(new Attribute(relAtt.name(), new Instances(relAtt.relation(), 0), atts.size()));
/* 353:    */       }
/* 354:    */       else
/* 355:    */       {
/* 356:691 */         String prefix = inputFormat.attribute(i).name() + "_";
/* 357:692 */         Instances relFormat = inputFormat.attribute(i).relation();
/* 358:693 */         for (int n = 0; n < relFormat.numAttributes(); n++)
/* 359:    */         {
/* 360:694 */           Attribute att = relFormat.attribute(n);
/* 361:696 */           if (att.isNumeric())
/* 362:    */           {
/* 363:697 */             if (!getDisableMIN()) {
/* 364:698 */               atts.add(new Attribute(prefix + att.name() + "_MIN"));
/* 365:    */             }
/* 366:700 */             if (!getDisableMAX()) {
/* 367:701 */               atts.add(new Attribute(prefix + att.name() + "_MAX"));
/* 368:    */             }
/* 369:703 */             if (!getDisableAVG()) {
/* 370:704 */               atts.add(new Attribute(prefix + att.name() + "_AVG"));
/* 371:    */             }
/* 372:706 */             if (!getDisableSTDEV()) {
/* 373:707 */               atts.add(new Attribute(prefix + att.name() + "_STDEV"));
/* 374:    */             }
/* 375:709 */             if (!getDisableSUM()) {
/* 376:710 */               atts.add(new Attribute(prefix + att.name() + "_SUM"));
/* 377:    */             }
/* 378:    */           }
/* 379:712 */           else if (att.isNominal())
/* 380:    */           {
/* 381:713 */             if (att.numValues() <= this.m_MaxCardinality) {
/* 382:714 */               for (int m = 0; m < att.numValues(); m++) {
/* 383:715 */                 atts.add(new Attribute(prefix + att.name() + "_" + att.value(m) + "_CNT"));
/* 384:    */               }
/* 385:    */             }
/* 386:719 */             if (getDebug()) {
/* 387:720 */               System.out.println("Attribute " + (i + 1) + "/" + (n + 1) + " (" + inputFormat.attribute(i).name() + "/" + att.name() + ") skipped, " + att.numValues() + " > " + this.m_MaxCardinality + ".");
/* 388:    */             }
/* 389:    */           }
/* 390:727 */           else if (getDebug())
/* 391:    */           {
/* 392:728 */             System.out.println("Attribute " + (i + 1) + "/" + (n + 1) + " (" + inputFormat.attribute(i).name() + "/" + att.name() + ") skipped.");
/* 393:    */           }
/* 394:    */         }
/* 395:    */       }
/* 396:    */     }
/* 397:737 */     Instances result = new Instances(inputFormat.relationName(), atts, 0);
/* 398:738 */     result.setClassIndex(clsIndex);
/* 399:    */     
/* 400:740 */     return result;
/* 401:    */   }
/* 402:    */   
/* 403:    */   protected Instances process(Instances instances)
/* 404:    */     throws Exception
/* 405:    */   {
/* 406:766 */     Instances result = getOutputFormat();
/* 407:    */     
/* 408:    */ 
/* 409:769 */     this.m_AttStats = new Hashtable();
/* 410:772 */     for (int i = 0; i < instances.numAttributes(); i++) {
/* 411:773 */       if (i != instances.classIndex()) {
/* 412:777 */         if (instances.attribute(i).isRelationValued()) {
/* 413:781 */           if (this.m_SelectedRange.isInRange(i)) {
/* 414:786 */             for (int k = 0; k < instances.numInstances(); k++)
/* 415:    */             {
/* 416:787 */               Instances relInstances = instances.instance(k).relationalValue(i);
/* 417:789 */               for (int n = 0; n < relInstances.numAttributes(); n++)
/* 418:    */               {
/* 419:790 */                 Attribute att = relInstances.attribute(n);
/* 420:791 */                 if ((att.isNumeric()) || ((att.isNominal()) && (att.numValues() <= this.m_MaxCardinality)))
/* 421:    */                 {
/* 422:793 */                   AttributeStats stats = relInstances.attributeStats(n);
/* 423:794 */                   this.m_AttStats.put(k + "-" + i + "-" + n, stats);
/* 424:    */                 }
/* 425:    */               }
/* 426:    */             }
/* 427:    */           }
/* 428:    */         }
/* 429:    */       }
/* 430:    */     }
/* 431:801 */     for (int k = 0; k < instances.numInstances(); k++)
/* 432:    */     {
/* 433:802 */       Instance inst = instances.instance(k);
/* 434:    */       
/* 435:804 */       double[] values = new double[result.numAttributes()];
/* 436:805 */       int l = 0;
/* 437:806 */       for (i = 0; i < instances.numAttributes(); i++) {
/* 438:807 */         if ((!instances.attribute(i).isRelationValued()) || (!this.m_SelectedRange.isInRange(i)))
/* 439:    */         {
/* 440:808 */           values[(l++)] = inst.value(i);
/* 441:    */         }
/* 442:    */         else
/* 443:    */         {
/* 444:812 */           Instances relInstances = inst.relationalValue(i);
/* 445:813 */           for (int n = 0; n < relInstances.numAttributes(); n++)
/* 446:    */           {
/* 447:814 */             Attribute att = relInstances.attribute(n);
/* 448:815 */             AttributeStats stats = (AttributeStats)this.m_AttStats.get(k + "-" + i + "-" + n);
/* 449:817 */             if (att.isNumeric())
/* 450:    */             {
/* 451:818 */               if (!getDisableMIN()) {
/* 452:819 */                 values[(l++)] = stats.numericStats.min;
/* 453:    */               }
/* 454:821 */               if (!getDisableMAX()) {
/* 455:822 */                 values[(l++)] = stats.numericStats.max;
/* 456:    */               }
/* 457:824 */               if (!getDisableAVG()) {
/* 458:825 */                 values[(l++)] = stats.numericStats.mean;
/* 459:    */               }
/* 460:827 */               if (!getDisableSTDEV()) {
/* 461:828 */                 values[(l++)] = stats.numericStats.stdDev;
/* 462:    */               }
/* 463:830 */               if (!getDisableSUM()) {
/* 464:831 */                 values[(l++)] = stats.numericStats.sum;
/* 465:    */               }
/* 466:    */             }
/* 467:833 */             else if ((att.isNominal()) && (att.numValues() <= this.m_MaxCardinality))
/* 468:    */             {
/* 469:834 */               for (int m = 0; m < att.numValues(); m++) {
/* 470:835 */                 values[(l++)] = stats.nominalCounts[m];
/* 471:    */               }
/* 472:    */             }
/* 473:    */           }
/* 474:    */         }
/* 475:    */       }
/* 476:842 */       if (this.m_GenerateSparseInstances) {
/* 477:843 */         result.add(new SparseInstance(inst.weight(), values));
/* 478:    */       } else {
/* 479:845 */         result.add(new DenseInstance(inst.weight(), values));
/* 480:    */       }
/* 481:    */     }
/* 482:849 */     return result;
/* 483:    */   }
/* 484:    */   
/* 485:    */   public String getRevision()
/* 486:    */   {
/* 487:859 */     return RevisionUtils.extract("$Revision: 12460 $");
/* 488:    */   }
/* 489:    */   
/* 490:    */   public static void main(String[] args)
/* 491:    */   {
/* 492:868 */     runFilter(new RELAGGS(), args);
/* 493:    */   }
/* 494:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.RELAGGS
 * JD-Core Version:    0.7.0.1
 */