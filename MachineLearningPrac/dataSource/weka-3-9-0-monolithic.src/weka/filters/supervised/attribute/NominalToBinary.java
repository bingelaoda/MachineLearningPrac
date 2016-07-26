/*   1:    */ package weka.filters.supervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Attribute;
/*   7:    */ import weka.core.Capabilities;
/*   8:    */ import weka.core.Capabilities.Capability;
/*   9:    */ import weka.core.DenseInstance;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.Option;
/*  13:    */ import weka.core.OptionHandler;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.SparseInstance;
/*  16:    */ import weka.core.TechnicalInformation;
/*  17:    */ import weka.core.TechnicalInformation.Field;
/*  18:    */ import weka.core.TechnicalInformation.Type;
/*  19:    */ import weka.core.TechnicalInformationHandler;
/*  20:    */ import weka.core.UnassignedClassException;
/*  21:    */ import weka.core.Utils;
/*  22:    */ import weka.filters.Filter;
/*  23:    */ import weka.filters.SupervisedFilter;
/*  24:    */ 
/*  25:    */ public class NominalToBinary
/*  26:    */   extends Filter
/*  27:    */   implements SupervisedFilter, OptionHandler, TechnicalInformationHandler
/*  28:    */ {
/*  29:    */   static final long serialVersionUID = -5004607029857673950L;
/*  30:104 */   private int[][] m_Indices = (int[][])null;
/*  31:107 */   private boolean m_Numeric = true;
/*  32:110 */   private boolean m_TransformAll = false;
/*  33:113 */   private boolean m_needToTransform = false;
/*  34:    */   
/*  35:    */   public String globalInfo()
/*  36:    */   {
/*  37:123 */     return "Converts all nominal attributes into binary numeric attributes. An attribute with k values is transformed into k binary attributes if the class is nominal (using the one-attribute-per-value approach). Binary attributes are left binary, if option '-A' is not given.If the class is numeric, k - 1 new binary attributes are generated in the manner described in \"Classification and Regression Trees\" by Breiman et al. (i.e. taking the average class value associated with each attribute value into account)\n\nFor more information, see:\n\n" + getTechnicalInformation().toString();
/*  38:    */   }
/*  39:    */   
/*  40:    */   public TechnicalInformation getTechnicalInformation()
/*  41:    */   {
/*  42:145 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.BOOK);
/*  43:146 */     result.setValue(TechnicalInformation.Field.AUTHOR, "L. Breiman and J.H. Friedman and R.A. Olshen and C.J. Stone");
/*  44:    */     
/*  45:148 */     result.setValue(TechnicalInformation.Field.TITLE, "Classification and Regression Trees");
/*  46:149 */     result.setValue(TechnicalInformation.Field.YEAR, "1984");
/*  47:150 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Wadsworth Inc");
/*  48:151 */     result.setValue(TechnicalInformation.Field.ISBN, "0412048418");
/*  49:    */     
/*  50:153 */     return result;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public Capabilities getCapabilities()
/*  54:    */   {
/*  55:164 */     Capabilities result = super.getCapabilities();
/*  56:165 */     result.disableAll();
/*  57:    */     
/*  58:    */ 
/*  59:168 */     result.enableAllAttributes();
/*  60:169 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  61:    */     
/*  62:    */ 
/*  63:172 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*  64:173 */     result.enable(Capabilities.Capability.DATE_CLASS);
/*  65:174 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  66:175 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  67:    */     
/*  68:177 */     return result;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public boolean setInputFormat(Instances instanceInfo)
/*  72:    */     throws Exception
/*  73:    */   {
/*  74:192 */     super.setInputFormat(instanceInfo);
/*  75:193 */     if (instanceInfo.classIndex() < 0) {
/*  76:194 */       throw new UnassignedClassException("No class has been assigned to the instances");
/*  77:    */     }
/*  78:197 */     setOutputFormat();
/*  79:198 */     this.m_Indices = ((int[][])null);
/*  80:199 */     if (instanceInfo.classAttribute().isNominal()) {
/*  81:200 */       return true;
/*  82:    */     }
/*  83:202 */     return false;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public boolean input(Instance instance)
/*  87:    */   {
/*  88:217 */     if (getInputFormat() == null) {
/*  89:218 */       throw new IllegalStateException("No input instance format defined");
/*  90:    */     }
/*  91:220 */     if (this.m_NewBatch)
/*  92:    */     {
/*  93:221 */       resetQueue();
/*  94:222 */       this.m_NewBatch = false;
/*  95:    */     }
/*  96:224 */     if ((this.m_Indices != null) || (getInputFormat().classAttribute().isNominal()))
/*  97:    */     {
/*  98:225 */       convertInstance((Instance)instance.copy());
/*  99:226 */       return true;
/* 100:    */     }
/* 101:228 */     bufferInput(instance);
/* 102:229 */     return false;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public boolean batchFinished()
/* 106:    */   {
/* 107:243 */     if (getInputFormat() == null) {
/* 108:244 */       throw new IllegalStateException("No input instance format defined");
/* 109:    */     }
/* 110:246 */     if ((this.m_Indices == null) && (getInputFormat().classAttribute().isNumeric()))
/* 111:    */     {
/* 112:247 */       computeAverageClassValues();
/* 113:248 */       setOutputFormat();
/* 114:252 */       for (int i = 0; i < getInputFormat().numInstances(); i++) {
/* 115:253 */         convertInstance(getInputFormat().instance(i));
/* 116:    */       }
/* 117:    */     }
/* 118:256 */     flushInput();
/* 119:    */     
/* 120:258 */     this.m_NewBatch = true;
/* 121:259 */     return numPendingOutput() != 0;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public Enumeration<Option> listOptions()
/* 125:    */   {
/* 126:270 */     Vector<Option> newVector = new Vector(2);
/* 127:    */     
/* 128:272 */     newVector.addElement(new Option("\tSets if binary attributes are to be coded as nominal ones.", "N", 0, "-N"));
/* 129:    */     
/* 130:    */ 
/* 131:    */ 
/* 132:276 */     newVector.addElement(new Option("\tFor each nominal value a new attribute is created, \n\tnot only if there are more than 2 values.", "A", 0, "-A"));
/* 133:    */     
/* 134:    */ 
/* 135:    */ 
/* 136:280 */     return newVector.elements();
/* 137:    */   }
/* 138:    */   
/* 139:    */   public void setOptions(String[] options)
/* 140:    */     throws Exception
/* 141:    */   {
/* 142:309 */     setBinaryAttributesNominal(Utils.getFlag('N', options));
/* 143:    */     
/* 144:311 */     setTransformAllValues(Utils.getFlag('A', options));
/* 145:313 */     if (getInputFormat() != null) {
/* 146:314 */       setInputFormat(getInputFormat());
/* 147:    */     }
/* 148:317 */     Utils.checkForRemainingOptions(options);
/* 149:    */   }
/* 150:    */   
/* 151:    */   public String[] getOptions()
/* 152:    */   {
/* 153:328 */     Vector<String> options = new Vector();
/* 154:330 */     if (getBinaryAttributesNominal()) {
/* 155:331 */       options.add("-N");
/* 156:    */     }
/* 157:334 */     if (getTransformAllValues()) {
/* 158:335 */       options.add("-A");
/* 159:    */     }
/* 160:338 */     return (String[])options.toArray(new String[0]);
/* 161:    */   }
/* 162:    */   
/* 163:    */   public String binaryAttributesNominalTipText()
/* 164:    */   {
/* 165:348 */     return "Whether resulting binary attributes will be nominal.";
/* 166:    */   }
/* 167:    */   
/* 168:    */   public boolean getBinaryAttributesNominal()
/* 169:    */   {
/* 170:358 */     return !this.m_Numeric;
/* 171:    */   }
/* 172:    */   
/* 173:    */   public void setBinaryAttributesNominal(boolean bool)
/* 174:    */   {
/* 175:368 */     this.m_Numeric = (!bool);
/* 176:    */   }
/* 177:    */   
/* 178:    */   public String transformAllValuesTipText()
/* 179:    */   {
/* 180:378 */     return "Whether all nominal values are turned into new attributes, not only if there are more than 2.";
/* 181:    */   }
/* 182:    */   
/* 183:    */   public boolean getTransformAllValues()
/* 184:    */   {
/* 185:389 */     return this.m_TransformAll;
/* 186:    */   }
/* 187:    */   
/* 188:    */   public void setTransformAllValues(boolean bool)
/* 189:    */   {
/* 190:400 */     this.m_TransformAll = bool;
/* 191:    */   }
/* 192:    */   
/* 193:    */   private void computeAverageClassValues()
/* 194:    */   {
/* 195:410 */     double[][] avgClassValues = new double[getInputFormat().numAttributes()][0];
/* 196:411 */     this.m_Indices = new int[getInputFormat().numAttributes()][0];
/* 197:412 */     for (int j = 0; j < getInputFormat().numAttributes(); j++)
/* 198:    */     {
/* 199:413 */       Attribute att = getInputFormat().attribute(j);
/* 200:414 */       if (att.isNominal())
/* 201:    */       {
/* 202:415 */         avgClassValues[j] = new double[att.numValues()];
/* 203:416 */         double[] counts = new double[att.numValues()];
/* 204:417 */         for (int i = 0; i < getInputFormat().numInstances(); i++)
/* 205:    */         {
/* 206:418 */           Instance instance = getInputFormat().instance(i);
/* 207:419 */           if ((!instance.classIsMissing()) && (!instance.isMissing(j)))
/* 208:    */           {
/* 209:420 */             counts[((int)instance.value(j))] += instance.weight();
/* 210:421 */             avgClassValues[j][((int)instance.value(j))] += instance.weight() * instance.classValue();
/* 211:    */           }
/* 212:    */         }
/* 213:425 */         double sum = Utils.sum(avgClassValues[j]);
/* 214:426 */         double totalCounts = Utils.sum(counts);
/* 215:427 */         if (Utils.gr(totalCounts, 0.0D)) {
/* 216:428 */           for (int k = 0; k < att.numValues(); k++) {
/* 217:429 */             if (Utils.gr(counts[k], 0.0D)) {
/* 218:430 */               avgClassValues[j][k] /= counts[k];
/* 219:    */             } else {
/* 220:432 */               avgClassValues[j][k] = (sum / totalCounts);
/* 221:    */             }
/* 222:    */           }
/* 223:    */         }
/* 224:436 */         this.m_Indices[j] = Utils.sort(avgClassValues[j]);
/* 225:    */       }
/* 226:    */     }
/* 227:    */   }
/* 228:    */   
/* 229:    */   private void setOutputFormat()
/* 230:    */   {
/* 231:444 */     if (getInputFormat().classAttribute().isNominal()) {
/* 232:445 */       setOutputFormatNominal();
/* 233:    */     } else {
/* 234:447 */       setOutputFormatNumeric();
/* 235:    */     }
/* 236:    */   }
/* 237:    */   
/* 238:    */   private void convertInstance(Instance inst)
/* 239:    */   {
/* 240:459 */     if (getInputFormat().classAttribute().isNominal()) {
/* 241:460 */       convertInstanceNominal(inst);
/* 242:    */     } else {
/* 243:462 */       convertInstanceNumeric(inst);
/* 244:    */     }
/* 245:    */   }
/* 246:    */   
/* 247:    */   private void setOutputFormatNominal()
/* 248:    */   {
/* 249:478 */     this.m_needToTransform = false;
/* 250:479 */     for (int i = 0; i < getInputFormat().numAttributes(); i++)
/* 251:    */     {
/* 252:480 */       Attribute att = getInputFormat().attribute(i);
/* 253:481 */       if ((att.isNominal()) && (i != getInputFormat().classIndex()) && ((att.numValues() > 2) || (this.m_TransformAll) || (this.m_Numeric)))
/* 254:    */       {
/* 255:483 */         this.m_needToTransform = true;
/* 256:484 */         break;
/* 257:    */       }
/* 258:    */     }
/* 259:488 */     if (!this.m_needToTransform)
/* 260:    */     {
/* 261:489 */       setOutputFormat(getInputFormat());
/* 262:490 */       return;
/* 263:    */     }
/* 264:493 */     int newClassIndex = getInputFormat().classIndex();
/* 265:494 */     ArrayList<Attribute> newAtts = new ArrayList();
/* 266:495 */     for (int j = 0; j < getInputFormat().numAttributes(); j++)
/* 267:    */     {
/* 268:496 */       Attribute att = getInputFormat().attribute(j);
/* 269:497 */       if ((!att.isNominal()) || (j == getInputFormat().classIndex()))
/* 270:    */       {
/* 271:498 */         newAtts.add((Attribute)att.copy());
/* 272:    */       }
/* 273:500 */       else if ((att.numValues() <= 2) && (!this.m_TransformAll))
/* 274:    */       {
/* 275:501 */         if (this.m_Numeric)
/* 276:    */         {
/* 277:502 */           String value = "";
/* 278:503 */           if (att.numValues() == 2) {
/* 279:504 */             value = "=" + att.value(1);
/* 280:    */           }
/* 281:506 */           newAtts.add(new Attribute(att.name() + value));
/* 282:    */         }
/* 283:    */         else
/* 284:    */         {
/* 285:508 */           newAtts.add((Attribute)att.copy());
/* 286:    */         }
/* 287:    */       }
/* 288:    */       else
/* 289:    */       {
/* 290:512 */         if (j < getInputFormat().classIndex()) {
/* 291:513 */           newClassIndex += att.numValues() - 1;
/* 292:    */         }
/* 293:517 */         for (int k = 0; k < att.numValues(); k++)
/* 294:    */         {
/* 295:518 */           StringBuffer attributeName = new StringBuffer(att.name() + "=");
/* 296:519 */           attributeName.append(att.value(k));
/* 297:520 */           if (this.m_Numeric)
/* 298:    */           {
/* 299:521 */             newAtts.add(new Attribute(attributeName.toString()));
/* 300:    */           }
/* 301:    */           else
/* 302:    */           {
/* 303:523 */             ArrayList<String> vals = new ArrayList(2);
/* 304:524 */             vals.add("f");
/* 305:525 */             vals.add("t");
/* 306:526 */             newAtts.add(new Attribute(attributeName.toString(), vals));
/* 307:    */           }
/* 308:    */         }
/* 309:    */       }
/* 310:    */     }
/* 311:532 */     Instances outputFormat = new Instances(getInputFormat().relationName(), newAtts, 0);
/* 312:533 */     outputFormat.setClassIndex(newClassIndex);
/* 313:534 */     setOutputFormat(outputFormat);
/* 314:    */   }
/* 315:    */   
/* 316:    */   private void setOutputFormatNumeric()
/* 317:    */   {
/* 318:542 */     if (this.m_Indices == null)
/* 319:    */     {
/* 320:543 */       setOutputFormat(null);
/* 321:544 */       return;
/* 322:    */     }
/* 323:554 */     this.m_needToTransform = false;
/* 324:555 */     for (int i = 0; i < getInputFormat().numAttributes(); i++)
/* 325:    */     {
/* 326:556 */       Attribute att = getInputFormat().attribute(i);
/* 327:557 */       if ((att.isNominal()) && ((att.numValues() > 2) || (this.m_Numeric) || (this.m_TransformAll)))
/* 328:    */       {
/* 329:559 */         this.m_needToTransform = true;
/* 330:560 */         break;
/* 331:    */       }
/* 332:    */     }
/* 333:564 */     if (!this.m_needToTransform)
/* 334:    */     {
/* 335:565 */       setOutputFormat(getInputFormat());
/* 336:566 */       return;
/* 337:    */     }
/* 338:569 */     int newClassIndex = getInputFormat().classIndex();
/* 339:570 */     ArrayList<Attribute> newAtts = new ArrayList();
/* 340:571 */     for (int j = 0; j < getInputFormat().numAttributes(); j++)
/* 341:    */     {
/* 342:572 */       Attribute att = getInputFormat().attribute(j);
/* 343:573 */       if ((!att.isNominal()) || (j == getInputFormat().classIndex()))
/* 344:    */       {
/* 345:574 */         newAtts.add((Attribute)att.copy());
/* 346:    */       }
/* 347:    */       else
/* 348:    */       {
/* 349:576 */         if (j < getInputFormat().classIndex()) {
/* 350:577 */           newClassIndex += att.numValues() - 2;
/* 351:    */         }
/* 352:582 */         for (int k = 1; k < att.numValues(); k++)
/* 353:    */         {
/* 354:583 */           StringBuffer attributeName = new StringBuffer(att.name() + "=");
/* 355:584 */           for (int l = k; l < att.numValues(); l++)
/* 356:    */           {
/* 357:585 */             if (l > k) {
/* 358:586 */               attributeName.append(',');
/* 359:    */             }
/* 360:588 */             attributeName.append(att.value(this.m_Indices[j][l]));
/* 361:    */           }
/* 362:590 */           if (this.m_Numeric)
/* 363:    */           {
/* 364:591 */             newAtts.add(new Attribute(attributeName.toString()));
/* 365:    */           }
/* 366:    */           else
/* 367:    */           {
/* 368:593 */             ArrayList<String> vals = new ArrayList(2);
/* 369:594 */             vals.add("f");
/* 370:595 */             vals.add("t");
/* 371:596 */             newAtts.add(new Attribute(attributeName.toString(), vals));
/* 372:    */           }
/* 373:    */         }
/* 374:    */       }
/* 375:    */     }
/* 376:601 */     Instances outputFormat = new Instances(getInputFormat().relationName(), newAtts, 0);
/* 377:602 */     outputFormat.setClassIndex(newClassIndex);
/* 378:603 */     setOutputFormat(outputFormat);
/* 379:    */   }
/* 380:    */   
/* 381:    */   private void convertInstanceNominal(Instance instance)
/* 382:    */   {
/* 383:614 */     if (!this.m_needToTransform)
/* 384:    */     {
/* 385:615 */       push(instance, false);
/* 386:616 */       return;
/* 387:    */     }
/* 388:619 */     double[] vals = new double[outputFormatPeek().numAttributes()];
/* 389:620 */     int attSoFar = 0;
/* 390:622 */     for (int j = 0; j < getInputFormat().numAttributes(); j++)
/* 391:    */     {
/* 392:623 */       Attribute att = getInputFormat().attribute(j);
/* 393:624 */       if ((!att.isNominal()) || (j == getInputFormat().classIndex()))
/* 394:    */       {
/* 395:625 */         vals[attSoFar] = instance.value(j);
/* 396:626 */         attSoFar++;
/* 397:    */       }
/* 398:628 */       else if ((att.numValues() <= 2) && (!this.m_TransformAll))
/* 399:    */       {
/* 400:629 */         vals[attSoFar] = instance.value(j);
/* 401:630 */         attSoFar++;
/* 402:    */       }
/* 403:    */       else
/* 404:    */       {
/* 405:632 */         if (instance.isMissing(j)) {
/* 406:633 */           for (int k = 0; k < att.numValues(); k++) {
/* 407:634 */             vals[(attSoFar + k)] = instance.value(j);
/* 408:    */           }
/* 409:    */         } else {
/* 410:637 */           for (int k = 0; k < att.numValues(); k++) {
/* 411:638 */             if (k == (int)instance.value(j)) {
/* 412:639 */               vals[(attSoFar + k)] = 1.0D;
/* 413:    */             } else {
/* 414:641 */               vals[(attSoFar + k)] = 0.0D;
/* 415:    */             }
/* 416:    */           }
/* 417:    */         }
/* 418:645 */         attSoFar += att.numValues();
/* 419:    */       }
/* 420:    */     }
/* 421:649 */     Instance inst = null;
/* 422:650 */     if ((instance instanceof SparseInstance)) {
/* 423:651 */       inst = new SparseInstance(instance.weight(), vals);
/* 424:    */     } else {
/* 425:653 */       inst = new DenseInstance(instance.weight(), vals);
/* 426:    */     }
/* 427:656 */     copyValues(inst, false, instance.dataset(), outputFormatPeek());
/* 428:    */     
/* 429:658 */     push(inst);
/* 430:    */   }
/* 431:    */   
/* 432:    */   private void convertInstanceNumeric(Instance instance)
/* 433:    */   {
/* 434:669 */     if (!this.m_needToTransform)
/* 435:    */     {
/* 436:670 */       push(instance, false);
/* 437:671 */       return;
/* 438:    */     }
/* 439:674 */     double[] vals = new double[outputFormatPeek().numAttributes()];
/* 440:675 */     int attSoFar = 0;
/* 441:677 */     for (int j = 0; j < getInputFormat().numAttributes(); j++)
/* 442:    */     {
/* 443:678 */       Attribute att = getInputFormat().attribute(j);
/* 444:679 */       if ((!att.isNominal()) || (j == getInputFormat().classIndex()))
/* 445:    */       {
/* 446:680 */         vals[attSoFar] = instance.value(j);
/* 447:681 */         attSoFar++;
/* 448:    */       }
/* 449:    */       else
/* 450:    */       {
/* 451:683 */         if (instance.isMissing(j))
/* 452:    */         {
/* 453:684 */           for (int k = 0; k < att.numValues() - 1; k++) {
/* 454:685 */             vals[(attSoFar + k)] = instance.value(j);
/* 455:    */           }
/* 456:    */         }
/* 457:    */         else
/* 458:    */         {
/* 459:688 */           int k = 0;
/* 460:689 */           while ((int)instance.value(j) != this.m_Indices[j][k])
/* 461:    */           {
/* 462:690 */             vals[(attSoFar + k)] = 1.0D;
/* 463:691 */             k++;
/* 464:    */           }
/* 465:693 */           while (k < att.numValues() - 1)
/* 466:    */           {
/* 467:694 */             vals[(attSoFar + k)] = 0.0D;
/* 468:695 */             k++;
/* 469:    */           }
/* 470:    */         }
/* 471:698 */         attSoFar += att.numValues() - 1;
/* 472:    */       }
/* 473:    */     }
/* 474:701 */     Instance inst = null;
/* 475:702 */     if ((instance instanceof SparseInstance)) {
/* 476:703 */       inst = new SparseInstance(instance.weight(), vals);
/* 477:    */     } else {
/* 478:705 */       inst = new DenseInstance(instance.weight(), vals);
/* 479:    */     }
/* 480:708 */     copyValues(inst, false, instance.dataset(), outputFormatPeek());
/* 481:    */     
/* 482:710 */     push(inst);
/* 483:    */   }
/* 484:    */   
/* 485:    */   public String getRevision()
/* 486:    */   {
/* 487:720 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 488:    */   }
/* 489:    */   
/* 490:    */   public static void main(String[] argv)
/* 491:    */   {
/* 492:729 */     runFilter(new NominalToBinary(), argv);
/* 493:    */   }
/* 494:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.supervised.attribute.NominalToBinary
 * JD-Core Version:    0.7.0.1
 */