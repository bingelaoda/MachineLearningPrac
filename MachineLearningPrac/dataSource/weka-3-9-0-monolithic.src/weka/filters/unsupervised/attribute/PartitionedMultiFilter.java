/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.DenseInstance;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.Option;
/*  13:    */ import weka.core.OptionHandler;
/*  14:    */ import weka.core.Range;
/*  15:    */ import weka.core.RevisionUtils;
/*  16:    */ import weka.core.SparseInstance;
/*  17:    */ import weka.core.Utils;
/*  18:    */ import weka.filters.AllFilter;
/*  19:    */ import weka.filters.Filter;
/*  20:    */ import weka.filters.SimpleBatchFilter;
/*  21:    */ 
/*  22:    */ public class PartitionedMultiFilter
/*  23:    */   extends SimpleBatchFilter
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = -6293720886005713120L;
/*  26: 90 */   protected Filter[] m_Filters = { new AllFilter() };
/*  27: 93 */   protected Range[] m_Ranges = { new Range("first-last") };
/*  28: 96 */   protected boolean m_RemoveUnused = false;
/*  29: 99 */   protected int[] m_IndicesUnused = new int[0];
/*  30:    */   
/*  31:    */   public String globalInfo()
/*  32:    */   {
/*  33:109 */     return "A filter that applies filters on subsets of attributes and assembles the output into a new dataset. Attributes that are not covered by any of the ranges can be either retained or removed from the output.";
/*  34:    */   }
/*  35:    */   
/*  36:    */   public Enumeration<Option> listOptions()
/*  37:    */   {
/*  38:123 */     Vector<Option> result = new Vector();
/*  39:    */     
/*  40:125 */     result.addElement(new Option("\tA filter to apply (can be specified multiple times).", "F", 1, "-F <classname [options]>"));
/*  41:    */     
/*  42:    */ 
/*  43:    */ 
/*  44:129 */     result.addElement(new Option("\tAn attribute range (can be specified multiple times).\n\tFor each filter a range must be supplied. 'first' and 'last'\n\tare valid indices. 'inv(...)' around the range denotes an\n\tinverted range.", "R", 1, "-R <range>"));
/*  45:    */     
/*  46:    */ 
/*  47:    */ 
/*  48:    */ 
/*  49:    */ 
/*  50:135 */     result.addElement(new Option("\tFlag for leaving unused attributes out of the output, by default\n\tthese are included in the filter output.", "U", 0, "-U"));
/*  51:    */     
/*  52:    */ 
/*  53:    */ 
/*  54:139 */     result.addAll(Collections.list(super.listOptions()));
/*  55:    */     
/*  56:141 */     return result.elements();
/*  57:    */   }
/*  58:    */   
/*  59:    */   public void setOptions(String[] options)
/*  60:    */     throws Exception
/*  61:    */   {
/*  62:188 */     setRemoveUnused(Utils.getFlag("U", options));
/*  63:    */     
/*  64:190 */     Vector<Object> objects = new Vector();
/*  65:    */     String tmpStr;
/*  66:191 */     while ((tmpStr = Utils.getOption("F", options)).length() != 0)
/*  67:    */     {
/*  68:192 */       String[] options2 = Utils.splitOptions(tmpStr);
/*  69:193 */       String classname = options2[0];
/*  70:194 */       options2[0] = "";
/*  71:195 */       objects.add(Utils.forName(Filter.class, classname, options2));
/*  72:    */     }
/*  73:199 */     if (objects.size() == 0) {
/*  74:200 */       objects.add(new AllFilter());
/*  75:    */     }
/*  76:203 */     setFilters((Filter[])objects.toArray(new Filter[objects.size()]));
/*  77:    */     
/*  78:205 */     objects = new Vector();
/*  79:206 */     while ((tmpStr = Utils.getOption("R", options)).length() != 0)
/*  80:    */     {
/*  81:    */       Range range;
/*  82:207 */       if ((tmpStr.startsWith("inv(")) && (tmpStr.endsWith(")")))
/*  83:    */       {
/*  84:208 */         Range range = new Range(tmpStr.substring(4, tmpStr.length() - 1));
/*  85:209 */         range.setInvert(true);
/*  86:    */       }
/*  87:    */       else
/*  88:    */       {
/*  89:211 */         range = new Range(tmpStr);
/*  90:    */       }
/*  91:213 */       objects.add(range);
/*  92:    */     }
/*  93:217 */     if (objects.size() == 0) {
/*  94:218 */       objects.add(new Range("first-last"));
/*  95:    */     }
/*  96:221 */     setRanges((Range[])objects.toArray(new Range[objects.size()]));
/*  97:    */     
/*  98:    */ 
/*  99:224 */     checkDimensions();
/* 100:    */     
/* 101:226 */     super.setOptions(options);
/* 102:    */     
/* 103:228 */     Utils.checkForRemainingOptions(options);
/* 104:    */   }
/* 105:    */   
/* 106:    */   public String[] getOptions()
/* 107:    */   {
/* 108:239 */     Vector<String> result = new Vector();
/* 109:241 */     if (getRemoveUnused()) {
/* 110:242 */       result.add("-U");
/* 111:    */     }
/* 112:245 */     for (int i = 0; i < getFilters().length; i++)
/* 113:    */     {
/* 114:246 */       result.add("-F");
/* 115:247 */       result.add(getFilterSpec(getFilter(i)));
/* 116:    */     }
/* 117:250 */     for (int i = 0; i < getRanges().length; i++)
/* 118:    */     {
/* 119:251 */       String tmpStr = getRange(i).getRanges();
/* 120:252 */       if (getRange(i).getInvert()) {
/* 121:253 */         tmpStr = "inv(" + tmpStr + ")";
/* 122:    */       }
/* 123:255 */       result.add("-R");
/* 124:256 */       result.add(tmpStr);
/* 125:    */     }
/* 126:259 */     Collections.addAll(result, super.getOptions());
/* 127:    */     
/* 128:261 */     return (String[])result.toArray(new String[result.size()]);
/* 129:    */   }
/* 130:    */   
/* 131:    */   protected void checkDimensions()
/* 132:    */     throws Exception
/* 133:    */   {
/* 134:270 */     if (getFilters().length != getRanges().length) {
/* 135:271 */       throw new IllegalArgumentException("Number of filters (= " + getFilters().length + ") " + "and ranges (= " + getRanges().length + ") don't match!");
/* 136:    */     }
/* 137:    */   }
/* 138:    */   
/* 139:    */   protected void testInputFormat(Instances instanceInfo)
/* 140:    */     throws Exception
/* 141:    */   {
/* 142:285 */     for (int i = 0; i < getRanges().length; i++)
/* 143:    */     {
/* 144:286 */       Instances newi = new Instances(instanceInfo, 0);
/* 145:287 */       if (instanceInfo.size() > 0) {
/* 146:288 */         newi.add((Instance)instanceInfo.get(0).copy());
/* 147:    */       }
/* 148:290 */       Range range = getRanges()[i];
/* 149:291 */       range.setUpper(instanceInfo.numAttributes() - 1);
/* 150:292 */       Instances subset = generateSubset(newi, range);
/* 151:293 */       getFilters()[i].setInputFormat(subset);
/* 152:    */     }
/* 153:    */   }
/* 154:    */   
/* 155:    */   public void setRemoveUnused(boolean value)
/* 156:    */   {
/* 157:304 */     this.m_RemoveUnused = value;
/* 158:    */   }
/* 159:    */   
/* 160:    */   public boolean getRemoveUnused()
/* 161:    */   {
/* 162:314 */     return this.m_RemoveUnused;
/* 163:    */   }
/* 164:    */   
/* 165:    */   public String removeUnusedTipText()
/* 166:    */   {
/* 167:324 */     return "If true then unused attributes (ones that are not covered by any of the ranges) will be removed from the output.";
/* 168:    */   }
/* 169:    */   
/* 170:    */   public void setFilters(Filter[] filters)
/* 171:    */   {
/* 172:336 */     this.m_Filters = filters;
/* 173:337 */     reset();
/* 174:    */   }
/* 175:    */   
/* 176:    */   public Filter[] getFilters()
/* 177:    */   {
/* 178:346 */     return this.m_Filters;
/* 179:    */   }
/* 180:    */   
/* 181:    */   public String filtersTipText()
/* 182:    */   {
/* 183:356 */     return "The base filters to be used.";
/* 184:    */   }
/* 185:    */   
/* 186:    */   public Filter getFilter(int index)
/* 187:    */   {
/* 188:366 */     return this.m_Filters[index];
/* 189:    */   }
/* 190:    */   
/* 191:    */   protected String getFilterSpec(Filter filter)
/* 192:    */   {
/* 193:    */     String result;
/* 194:    */     String result;
/* 195:378 */     if (filter == null)
/* 196:    */     {
/* 197:379 */       result = "";
/* 198:    */     }
/* 199:    */     else
/* 200:    */     {
/* 201:381 */       result = filter.getClass().getName();
/* 202:382 */       if ((filter instanceof OptionHandler)) {
/* 203:383 */         result = result + " " + Utils.joinOptions(filter.getOptions());
/* 204:    */       }
/* 205:    */     }
/* 206:388 */     return result;
/* 207:    */   }
/* 208:    */   
/* 209:    */   public void setRanges(Range[] Ranges)
/* 210:    */   {
/* 211:399 */     this.m_Ranges = Ranges;
/* 212:400 */     reset();
/* 213:    */   }
/* 214:    */   
/* 215:    */   public Range[] getRanges()
/* 216:    */   {
/* 217:409 */     return this.m_Ranges;
/* 218:    */   }
/* 219:    */   
/* 220:    */   public String rangesTipText()
/* 221:    */   {
/* 222:419 */     return "The attribute ranges to be used; 'inv(...)' denotes an inverted range.";
/* 223:    */   }
/* 224:    */   
/* 225:    */   public Range getRange(int index)
/* 226:    */   {
/* 227:429 */     return this.m_Ranges[index];
/* 228:    */   }
/* 229:    */   
/* 230:    */   protected void determineUnusedIndices(Instances data)
/* 231:    */   {
/* 232:446 */     Vector<Integer> indices = new Vector();
/* 233:447 */     for (int i = 0; i < data.numAttributes(); i++) {
/* 234:448 */       if (i != data.classIndex())
/* 235:    */       {
/* 236:452 */         boolean covered = false;
/* 237:453 */         for (int n = 0; n < getRanges().length; n++) {
/* 238:454 */           if (getRanges()[n].isInRange(i))
/* 239:    */           {
/* 240:455 */             covered = true;
/* 241:456 */             break;
/* 242:    */           }
/* 243:    */         }
/* 244:460 */         if (!covered) {
/* 245:461 */           indices.add(new Integer(i));
/* 246:    */         }
/* 247:    */       }
/* 248:    */     }
/* 249:466 */     this.m_IndicesUnused = new int[indices.size()];
/* 250:467 */     for (i = 0; i < indices.size(); i++) {
/* 251:468 */       this.m_IndicesUnused[i] = ((Integer)indices.get(i)).intValue();
/* 252:    */     }
/* 253:471 */     if (getDebug()) {
/* 254:472 */       System.out.println("Unused indices: " + Utils.arrayToString(this.m_IndicesUnused));
/* 255:    */     }
/* 256:    */   }
/* 257:    */   
/* 258:    */   protected Instances generateSubset(Instances data, Range range)
/* 259:    */     throws Exception
/* 260:    */   {
/* 261:495 */     int[] indices = range.getSelection();
/* 262:496 */     StringBuilder atts = new StringBuilder();
/* 263:497 */     for (int i = 0; i < indices.length; i++)
/* 264:    */     {
/* 265:498 */       if (i > 0) {
/* 266:499 */         atts.append(",");
/* 267:    */       }
/* 268:501 */       atts.append("" + (indices[i] + 1));
/* 269:    */     }
/* 270:503 */     if ((data.classIndex() > -1) && (!range.isInRange(data.classIndex()))) {
/* 271:504 */       atts.append("," + (data.classIndex() + 1));
/* 272:    */     }
/* 273:508 */     Remove filter = new Remove();
/* 274:509 */     filter.setAttributeIndices(atts.toString());
/* 275:510 */     filter.setInvertSelection(true);
/* 276:511 */     filter.setInputFormat(data);
/* 277:    */     
/* 278:    */ 
/* 279:514 */     Instances result = Filter.useFilter(data, filter);
/* 280:    */     
/* 281:516 */     return result;
/* 282:    */   }
/* 283:    */   
/* 284:    */   protected Instances renameAttributes(Instances data, String prefix)
/* 285:    */     throws Exception
/* 286:    */   {
/* 287:535 */     ArrayList<Attribute> atts = new ArrayList();
/* 288:536 */     for (int i = 0; i < data.numAttributes(); i++) {
/* 289:537 */       if (i == data.classIndex()) {
/* 290:538 */         atts.add((Attribute)data.attribute(i).copy());
/* 291:    */       } else {
/* 292:540 */         atts.add(data.attribute(i).copy(prefix + data.attribute(i).name()));
/* 293:    */       }
/* 294:    */     }
/* 295:545 */     Instances result = new Instances(data.relationName(), atts, data.numInstances());
/* 296:546 */     for (i = 0; i < data.numInstances(); i++) {
/* 297:547 */       result.add((Instance)data.instance(i).copy());
/* 298:    */     }
/* 299:551 */     if (data.classIndex() > -1) {
/* 300:552 */       result.setClassIndex(data.classIndex());
/* 301:    */     }
/* 302:555 */     return result;
/* 303:    */   }
/* 304:    */   
/* 305:    */   protected Instances determineOutputFormat(Instances inputFormat)
/* 306:    */     throws Exception
/* 307:    */   {
/* 308:    */     Instances result;
/* 309:580 */     if (!isFirstBatchDone())
/* 310:    */     {
/* 311:582 */       if (inputFormat.numInstances() == 0) {
/* 312:583 */         return null;
/* 313:    */       }
/* 314:586 */       checkDimensions();
/* 315:    */       
/* 316:    */ 
/* 317:589 */       determineUnusedIndices(inputFormat);
/* 318:    */       
/* 319:591 */       ArrayList<Attribute> atts = new ArrayList();
/* 320:592 */       for (int i = 0; i < getFilters().length; i++)
/* 321:    */       {
/* 322:593 */         if (!isFirstBatchDone())
/* 323:    */         {
/* 324:595 */           Instances processed = generateSubset(inputFormat, getRange(i));
/* 325:597 */           if (!getFilter(i).setInputFormat(processed)) {
/* 326:598 */             Filter.useFilter(processed, getFilter(i));
/* 327:    */           }
/* 328:    */         }
/* 329:603 */         Instances processed = getFilter(i).getOutputFormat();
/* 330:    */         
/* 331:    */ 
/* 332:606 */         processed = renameAttributes(processed, "filtered-" + i + "-");
/* 333:609 */         for (int n = 0; n < processed.numAttributes(); n++) {
/* 334:610 */           if (n != processed.classIndex()) {
/* 335:613 */             atts.add((Attribute)processed.attribute(n).copy());
/* 336:    */           }
/* 337:    */         }
/* 338:    */       }
/* 339:618 */       if (!getRemoveUnused()) {
/* 340:619 */         for (i = 0; i < this.m_IndicesUnused.length; i++)
/* 341:    */         {
/* 342:620 */           Attribute att = inputFormat.attribute(this.m_IndicesUnused[i]);
/* 343:621 */           atts.add(att.copy("unfiltered-" + att.name()));
/* 344:    */         }
/* 345:    */       }
/* 346:626 */       if (inputFormat.classIndex() > -1) {
/* 347:627 */         atts.add((Attribute)inputFormat.classAttribute().copy());
/* 348:    */       }
/* 349:631 */       Instances result = new Instances(inputFormat.relationName(), atts, 0);
/* 350:632 */       if (inputFormat.classIndex() > -1) {
/* 351:633 */         result.setClassIndex(result.numAttributes() - 1);
/* 352:    */       }
/* 353:    */     }
/* 354:    */     else
/* 355:    */     {
/* 356:636 */       result = getOutputFormat();
/* 357:    */     }
/* 358:639 */     return result;
/* 359:    */   }
/* 360:    */   
/* 361:    */   protected Instances process(Instances instances)
/* 362:    */     throws Exception
/* 363:    */   {
/* 364:664 */     if (!isFirstBatchDone())
/* 365:    */     {
/* 366:665 */       checkDimensions();
/* 367:668 */       for (int i = 0; i < this.m_Ranges.length; i++) {
/* 368:669 */         this.m_Ranges[i].setUpper(instances.numAttributes() - 1);
/* 369:    */       }
/* 370:673 */       determineUnusedIndices(instances);
/* 371:    */     }
/* 372:677 */     Instances[] processed = new Instances[getFilters().length];
/* 373:678 */     for (int i = 0; i < getFilters().length; i++)
/* 374:    */     {
/* 375:679 */       processed[i] = generateSubset(instances, getRange(i));
/* 376:680 */       if (!isFirstBatchDone()) {
/* 377:681 */         getFilter(i).setInputFormat(processed[i]);
/* 378:    */       }
/* 379:683 */       processed[i] = Filter.useFilter(processed[i], getFilter(i));
/* 380:    */     }
/* 381:    */     Instances result;
/* 382:687 */     if (!isFirstBatchDone())
/* 383:    */     {
/* 384:688 */       Instances result = determineOutputFormat(instances);
/* 385:689 */       setOutputFormat(result);
/* 386:    */     }
/* 387:    */     else
/* 388:    */     {
/* 389:691 */       result = getOutputFormat();
/* 390:    */     }
/* 391:695 */     Vector<Integer> errors = new Vector();
/* 392:696 */     for (i = 0; i < processed.length; i++) {
/* 393:697 */       if (processed[i].numInstances() != instances.numInstances()) {
/* 394:698 */         errors.add(new Integer(i));
/* 395:    */       }
/* 396:    */     }
/* 397:701 */     if (errors.size() > 0) {
/* 398:702 */       throw new IllegalStateException("The following filter(s) changed the number of instances: " + errors);
/* 399:    */     }
/* 400:707 */     for (i = 0; i < instances.numInstances(); i++)
/* 401:    */     {
/* 402:708 */       Instance inst = instances.instance(i);
/* 403:709 */       double[] values = new double[result.numAttributes()];
/* 404:    */       
/* 405:    */ 
/* 406:712 */       int index = 0;
/* 407:713 */       for (int n = 0; n < processed.length; n++) {
/* 408:714 */         for (int m = 0; m < processed[n].numAttributes(); m++) {
/* 409:715 */           if (m != processed[n].classIndex())
/* 410:    */           {
/* 411:718 */             if (result.attribute(index).isString()) {
/* 412:719 */               values[index] = result.attribute(index).addStringValue(processed[n].instance(i).stringValue(m));
/* 413:721 */             } else if (result.attribute(index).isRelationValued()) {
/* 414:722 */               values[index] = result.attribute(index).addRelation(processed[n].instance(i).relationalValue(m));
/* 415:    */             } else {
/* 416:725 */               values[index] = processed[n].instance(i).value(m);
/* 417:    */             }
/* 418:727 */             index++;
/* 419:    */           }
/* 420:    */         }
/* 421:    */       }
/* 422:732 */       if (!getRemoveUnused()) {
/* 423:733 */         for (n = 0; n < this.m_IndicesUnused.length; n++)
/* 424:    */         {
/* 425:734 */           if (result.attribute(index).isString()) {
/* 426:735 */             values[index] = result.attribute(index).addStringValue(inst.stringValue(this.m_IndicesUnused[n]));
/* 427:737 */           } else if (result.attribute(index).isRelationValued()) {
/* 428:738 */             values[index] = result.attribute(index).addRelation(inst.relationalValue(this.m_IndicesUnused[n]));
/* 429:    */           } else {
/* 430:741 */             values[index] = inst.value(this.m_IndicesUnused[n]);
/* 431:    */           }
/* 432:743 */           index++;
/* 433:    */         }
/* 434:    */       }
/* 435:748 */       if (instances.classIndex() > -1) {
/* 436:749 */         values[(values.length - 1)] = inst.value(instances.classIndex());
/* 437:    */       }
/* 438:    */       Instance newInst;
/* 439:    */       Instance newInst;
/* 440:753 */       if ((inst instanceof SparseInstance)) {
/* 441:754 */         newInst = new SparseInstance(instances.instance(i).weight(), values);
/* 442:    */       } else {
/* 443:756 */         newInst = new DenseInstance(instances.instance(i).weight(), values);
/* 444:    */       }
/* 445:758 */       result.add(newInst);
/* 446:    */     }
/* 447:761 */     return result;
/* 448:    */   }
/* 449:    */   
/* 450:    */   public String getRevision()
/* 451:    */   {
/* 452:771 */     return RevisionUtils.extract("$Revision: 10215 $");
/* 453:    */   }
/* 454:    */   
/* 455:    */   public static void main(String[] args)
/* 456:    */   {
/* 457:780 */     runFilter(new PartitionedMultiFilter(), args);
/* 458:    */   }
/* 459:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.PartitionedMultiFilter
 * JD-Core Version:    0.7.0.1
 */