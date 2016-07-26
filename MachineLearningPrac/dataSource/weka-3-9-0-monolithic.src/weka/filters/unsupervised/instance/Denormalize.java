/*   1:    */ package weka.filters.unsupervised.instance;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.HashMap;
/*   7:    */ import java.util.Map;
/*   8:    */ import java.util.Vector;
/*   9:    */ import weka.core.Attribute;
/*  10:    */ import weka.core.Capabilities;
/*  11:    */ import weka.core.Capabilities.Capability;
/*  12:    */ import weka.core.DenseInstance;
/*  13:    */ import weka.core.Instance;
/*  14:    */ import weka.core.Instances;
/*  15:    */ import weka.core.Option;
/*  16:    */ import weka.core.OptionHandler;
/*  17:    */ import weka.core.RevisionUtils;
/*  18:    */ import weka.core.SelectedTag;
/*  19:    */ import weka.core.SparseInstance;
/*  20:    */ import weka.core.Tag;
/*  21:    */ import weka.core.Utils;
/*  22:    */ import weka.filters.Filter;
/*  23:    */ import weka.filters.StreamableFilter;
/*  24:    */ import weka.filters.UnsupervisedFilter;
/*  25:    */ 
/*  26:    */ public class Denormalize
/*  27:    */   extends Filter
/*  28:    */   implements UnsupervisedFilter, OptionHandler, StreamableFilter
/*  29:    */ {
/*  30:    */   private static final long serialVersionUID = -6334763153733741054L;
/*  31:    */   
/*  32:    */   public static enum NumericAggregation
/*  33:    */   {
/*  34:100 */     AVG("Average"),  SUM("Sum"),  MIN("Minimum"),  MAX("Maximum");
/*  35:    */     
/*  36:    */     private final String m_stringVal;
/*  37:    */     
/*  38:    */     private NumericAggregation(String name)
/*  39:    */     {
/*  40:105 */       this.m_stringVal = name;
/*  41:    */     }
/*  42:    */     
/*  43:    */     public String toString()
/*  44:    */     {
/*  45:110 */       return this.m_stringVal;
/*  46:    */     }
/*  47:    */   }
/*  48:    */   
/*  49:115 */   public static final Tag[] TAGS_SELECTION = { new Tag(NumericAggregation.AVG.ordinal(), "Average"), new Tag(NumericAggregation.SUM.ordinal(), "Sum"), new Tag(NumericAggregation.MIN.ordinal(), "Minimum"), new Tag(NumericAggregation.MAX.ordinal(), "Maximum") };
/*  50:125 */   protected String m_groupingAttribute = "first";
/*  51:128 */   protected int m_groupingIndex = -1;
/*  52:134 */   protected boolean m_nonSparseMarketBasketFormat = false;
/*  53:139 */   protected boolean m_sparseFormat = true;
/*  54:142 */   protected NumericAggregation m_numericAggregation = NumericAggregation.SUM;
/*  55:    */   
/*  56:    */   public String globalInfo()
/*  57:    */   {
/*  58:151 */     return "An instance filter that collapses instances with a common grouping ID value into a single instance. Useful for converting transactional data into a format that Weka's association rule learners can handle. IMPORTANT: assumes that the incoming batch of instances has been sorted on the grouping attribute. The values of nominal attributes are converted to indicator attributes. These can be either binary (with f and t values) or unary with missing values used to indicate absence. The later is Weka's old market basket format, which is useful for Apriori. Numeric attributes can be aggregated within groups by computing the average, sum, minimum or maximum.";
/*  59:    */   }
/*  60:    */   
/*  61:    */   public Capabilities getCapabilities()
/*  62:    */   {
/*  63:175 */     Capabilities result = super.getCapabilities();
/*  64:176 */     result.disableAll();
/*  65:177 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  66:178 */     result.enableAllClasses();
/*  67:179 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  68:180 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  69:181 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  70:182 */     result.enable(Capabilities.Capability.STRING_ATTRIBUTES);
/*  71:183 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  72:    */     
/*  73:185 */     return result;
/*  74:    */   }
/*  75:    */   
/*  76:188 */   private Map<String, Integer> m_newFormatIndexes = null;
/*  77:    */   
/*  78:    */   protected void createOutputFormat(Instances instanceInfo)
/*  79:    */     throws Exception
/*  80:    */   {
/*  81:197 */     this.m_newFormatIndexes = new HashMap();
/*  82:198 */     ArrayList<Attribute> attInfo = new ArrayList();
/*  83:    */     
/*  84:200 */     int count = 0;
/*  85:201 */     for (int i = 0; i < instanceInfo.numAttributes(); i++) {
/*  86:202 */       if (i == this.m_groupingIndex)
/*  87:    */       {
/*  88:203 */         attInfo.add(instanceInfo.attribute(this.m_groupingIndex));
/*  89:204 */         this.m_newFormatIndexes.put(instanceInfo.attribute(this.m_groupingIndex).name(), new Integer(count));
/*  90:    */         
/*  91:206 */         count++;
/*  92:    */       }
/*  93:207 */       else if (instanceInfo.attribute(i).isNumeric())
/*  94:    */       {
/*  95:208 */         String newName = this.m_numericAggregation.toString() + "_" + instanceInfo.attribute(i).name();
/*  96:    */         
/*  97:    */ 
/*  98:211 */         attInfo.add(new Attribute(newName));
/*  99:212 */         this.m_newFormatIndexes.put(newName, new Integer(count));
/* 100:213 */         count++;
/* 101:    */       }
/* 102:214 */       else if (instanceInfo.attribute(i).isNominal())
/* 103:    */       {
/* 104:218 */         for (int j = 0; j < instanceInfo.attribute(i).numValues(); j++)
/* 105:    */         {
/* 106:219 */           ArrayList<String> vals = new ArrayList();
/* 107:220 */           if (this.m_nonSparseMarketBasketFormat)
/* 108:    */           {
/* 109:221 */             vals.add("t");
/* 110:    */           }
/* 111:    */           else
/* 112:    */           {
/* 113:223 */             vals.add("f");
/* 114:224 */             vals.add("t");
/* 115:    */           }
/* 116:227 */           String newName = instanceInfo.attribute(i).name() + "_" + instanceInfo.attribute(i).value(j);
/* 117:    */           
/* 118:    */ 
/* 119:230 */           attInfo.add(new Attribute(newName, vals));
/* 120:231 */           this.m_newFormatIndexes.put(newName, new Integer(count));
/* 121:232 */           count++;
/* 122:    */         }
/* 123:    */       }
/* 124:    */     }
/* 125:237 */     Instances outputFormat = new Instances(instanceInfo.relationName() + "_denormalized", attInfo, 0);
/* 126:    */     
/* 127:    */ 
/* 128:240 */     setOutputFormat(outputFormat);
/* 129:    */   }
/* 130:    */   
/* 131:    */   public boolean setInputFormat(Instances instanceInfo)
/* 132:    */     throws Exception
/* 133:    */   {
/* 134:254 */     super.setInputFormat(instanceInfo);
/* 135:    */     
/* 136:256 */     this.m_groupingIndex = -1;
/* 137:257 */     this.m_currentGroup = -1.0D;
/* 138:    */     int i;
/* 139:    */     try
/* 140:    */     {
/* 141:260 */       this.m_groupingIndex = Integer.parseInt(this.m_groupingAttribute);
/* 142:261 */       this.m_groupingIndex -= 1;
/* 143:    */     }
/* 144:    */     catch (NumberFormatException e)
/* 145:    */     {
/* 146:263 */       if (this.m_groupingAttribute.equalsIgnoreCase("first"))
/* 147:    */       {
/* 148:264 */         this.m_groupingIndex = 0;
/* 149:    */         break label130;
/* 150:    */       }
/* 151:265 */       if (this.m_groupingAttribute.equalsIgnoreCase("last"))
/* 152:    */       {
/* 153:266 */         this.m_groupingIndex = (instanceInfo.numAttributes() - 1);
/* 154:    */         break label130;
/* 155:    */       }
/* 156:269 */       i = 0;
/* 157:    */     }
/* 158:269 */     for (; i < instanceInfo.numAttributes(); i++) {
/* 159:270 */       if (instanceInfo.attribute(i).name().equalsIgnoreCase(this.m_groupingAttribute))
/* 160:    */       {
/* 161:272 */         this.m_groupingIndex = i;
/* 162:273 */         break;
/* 163:    */       }
/* 164:    */     }
/* 165:    */     label130:
/* 166:279 */     if (this.m_groupingIndex == -1) {
/* 167:280 */       throw new Exception("Unable to determine which attribute should be used for grouping!");
/* 168:    */     }
/* 169:284 */     if (instanceInfo.attribute(this.m_groupingIndex).isString()) {
/* 170:285 */       throw new Exception("The grouping attribute must be either numeric or nominal!");
/* 171:    */     }
/* 172:289 */     createOutputFormat(instanceInfo);
/* 173:290 */     System.err.println("[Denormalize] WARNING: this filter expects the incoming batch of instances to be sorted in order by the grouping attribute.");
/* 174:    */     
/* 175:    */ 
/* 176:    */ 
/* 177:294 */     return true;
/* 178:    */   }
/* 179:    */   
/* 180:297 */   private double m_currentGroup = -1.0D;
/* 181:298 */   private double[] m_tempVals = null;
/* 182:299 */   private double[] m_counts = null;
/* 183:    */   
/* 184:    */   protected boolean convertInstance(Instance instance)
/* 185:    */     throws Exception
/* 186:    */   {
/* 187:311 */     Instances outputFormat = outputFormatPeek();
/* 188:312 */     boolean instanceOutputted = false;
/* 189:314 */     if ((this.m_currentGroup == -1.0D) || (instance.value(this.m_groupingIndex) != this.m_currentGroup))
/* 190:    */     {
/* 191:316 */       if (this.m_tempVals != null)
/* 192:    */       {
/* 193:318 */         for (int i = 0; i < outputFormat.numAttributes(); i++) {
/* 194:319 */           if (outputFormat.attribute(i).isNominal())
/* 195:    */           {
/* 196:320 */             if (this.m_nonSparseMarketBasketFormat)
/* 197:    */             {
/* 198:322 */               if (this.m_tempVals[i] == -1.0D) {
/* 199:323 */                 this.m_tempVals[i] = Utils.missingValue();
/* 200:    */               }
/* 201:    */             }
/* 202:325 */             else if (this.m_tempVals[i] == -1.0D) {
/* 203:327 */               this.m_tempVals[i] = 0.0D;
/* 204:    */             }
/* 205:    */           }
/* 206:329 */           else if ((outputFormat.attribute(i).isNumeric()) && 
/* 207:330 */             (this.m_numericAggregation == NumericAggregation.AVG) && (this.m_counts[i] > 0.0D)) {
/* 208:332 */             this.m_tempVals[i] /= this.m_counts[i];
/* 209:    */           }
/* 210:    */         }
/* 211:337 */         Instance tempI = null;
/* 212:338 */         tempI = new DenseInstance(1.0D, this.m_tempVals);
/* 213:339 */         if ((this.m_sparseFormat) && (!this.m_nonSparseMarketBasketFormat)) {
/* 214:340 */           tempI = new SparseInstance(tempI);
/* 215:    */         }
/* 216:344 */         push(tempI);
/* 217:345 */         instanceOutputted = true;
/* 218:    */       }
/* 219:353 */       if ((instance != null) && (
/* 220:354 */         (this.m_currentGroup == -1.0D) || (instance.value(this.m_groupingIndex) != this.m_currentGroup)))
/* 221:    */       {
/* 222:356 */         this.m_currentGroup = instance.value(this.m_groupingIndex);
/* 223:357 */         this.m_tempVals = new double[outputFormat.numAttributes()];
/* 224:359 */         for (int i = 0; i < outputFormat.numAttributes(); i++) {
/* 225:360 */           if (outputFormat.attribute(i).isNominal()) {
/* 226:361 */             this.m_tempVals[i] = -1.0D;
/* 227:362 */           } else if (outputFormat.attribute(i).isNumeric()) {
/* 228:363 */             if (this.m_numericAggregation == NumericAggregation.MAX) {
/* 229:364 */               this.m_tempVals[i] = 4.9E-324D;
/* 230:365 */             } else if (this.m_numericAggregation == NumericAggregation.MIN) {
/* 231:366 */               this.m_tempVals[i] = 1.7976931348623157E+308D;
/* 232:    */             }
/* 233:    */           }
/* 234:    */         }
/* 235:371 */         this.m_counts = new double[outputFormat.numAttributes()];
/* 236:    */       }
/* 237:    */     }
/* 238:376 */     if (instance != null) {
/* 239:378 */       for (int i = 0; i < instance.numAttributes(); i++) {
/* 240:380 */         if (!Utils.isMissingValue(instance.value(i))) {
/* 241:381 */           if (i == this.m_groupingIndex)
/* 242:    */           {
/* 243:382 */             int newIndex = ((Integer)this.m_newFormatIndexes.get(instance.attribute(this.m_groupingIndex).name())).intValue();
/* 244:    */             
/* 245:384 */             this.m_tempVals[newIndex] = instance.value(this.m_groupingIndex);
/* 246:    */           }
/* 247:386 */           else if (instance.attribute(i).isNominal())
/* 248:    */           {
/* 249:387 */             String newName = instance.attribute(i).name() + "_" + instance.attribute(i).value((int)instance.value(i));
/* 250:    */             
/* 251:389 */             Integer nn = (Integer)this.m_newFormatIndexes.get(newName);
/* 252:391 */             if (nn != null)
/* 253:    */             {
/* 254:397 */               int newIndex = nn.intValue();
/* 255:399 */               if (this.m_nonSparseMarketBasketFormat) {
/* 256:400 */                 this.m_tempVals[newIndex] = 0.0D;
/* 257:    */               } else {
/* 258:403 */                 this.m_tempVals[newIndex] = 1.0D;
/* 259:    */               }
/* 260:    */             }
/* 261:    */           }
/* 262:405 */           else if (instance.attribute(i).isNumeric())
/* 263:    */           {
/* 264:406 */             String newName = this.m_numericAggregation.toString() + "_" + instance.attribute(i).name();
/* 265:    */             
/* 266:    */ 
/* 267:409 */             Integer nn = (Integer)this.m_newFormatIndexes.get(newName);
/* 268:411 */             if (nn != null)
/* 269:    */             {
/* 270:416 */               int newIndex = nn.intValue();
/* 271:    */               
/* 272:418 */               this.m_counts[newIndex] += 1.0D;
/* 273:419 */               switch (1.$SwitchMap$weka$filters$unsupervised$instance$Denormalize$NumericAggregation[this.m_numericAggregation.ordinal()])
/* 274:    */               {
/* 275:    */               case 1: 
/* 276:    */               case 2: 
/* 277:423 */                 this.m_tempVals[newIndex] += instance.value(i);
/* 278:424 */                 break;
/* 279:    */               case 3: 
/* 280:426 */                 if (instance.value(i) < this.m_tempVals[newIndex]) {
/* 281:427 */                   this.m_tempVals[newIndex] = instance.value(i);
/* 282:    */                 }
/* 283:    */                 break;
/* 284:    */               case 4: 
/* 285:431 */                 if (instance.value(i) > this.m_tempVals[newIndex]) {
/* 286:432 */                   this.m_tempVals[newIndex] = instance.value(i);
/* 287:    */                 }
/* 288:    */                 break;
/* 289:    */               }
/* 290:    */             }
/* 291:    */           }
/* 292:    */         }
/* 293:    */       }
/* 294:    */     }
/* 295:441 */     return instanceOutputted;
/* 296:    */   }
/* 297:    */   
/* 298:    */   public boolean input(Instance instance)
/* 299:    */     throws Exception
/* 300:    */   {
/* 301:455 */     if (getInputFormat() == null) {
/* 302:456 */       throw new IllegalStateException("No input instance format defined");
/* 303:    */     }
/* 304:459 */     if (this.m_NewBatch)
/* 305:    */     {
/* 306:460 */       resetQueue();
/* 307:461 */       this.m_NewBatch = false;
/* 308:    */     }
/* 309:464 */     return convertInstance(instance);
/* 310:    */   }
/* 311:    */   
/* 312:    */   public boolean batchFinished()
/* 313:    */     throws Exception
/* 314:    */   {
/* 315:475 */     if (getInputFormat() == null) {
/* 316:476 */       throw new IllegalStateException("No input instance format defined");
/* 317:    */     }
/* 318:479 */     if (outputFormatPeek() == null) {
/* 319:480 */       createOutputFormat(getInputFormat());
/* 320:    */     }
/* 321:483 */     if (this.m_tempVals != null)
/* 322:    */     {
/* 323:485 */       this.m_currentGroup = -1.0D;
/* 324:486 */       convertInstance(null);
/* 325:    */     }
/* 326:489 */     flushInput();
/* 327:490 */     this.m_NewBatch = true;
/* 328:491 */     this.m_currentGroup = -1.0D;
/* 329:492 */     this.m_tempVals = null;
/* 330:493 */     this.m_counts = null;
/* 331:494 */     return numPendingOutput() != 0;
/* 332:    */   }
/* 333:    */   
/* 334:    */   public String groupingAttributeTipText()
/* 335:    */   {
/* 336:504 */     return "Set the attribute that defines the groups (e.g. transaction ID).";
/* 337:    */   }
/* 338:    */   
/* 339:    */   public void setGroupingAttribute(String groupAtt)
/* 340:    */   {
/* 341:514 */     this.m_groupingAttribute = groupAtt;
/* 342:    */   }
/* 343:    */   
/* 344:    */   public String getGroupingAttribute()
/* 345:    */   {
/* 346:524 */     return this.m_groupingAttribute;
/* 347:    */   }
/* 348:    */   
/* 349:    */   public void setUseOldMarketBasketFormat(boolean m)
/* 350:    */   {
/* 351:535 */     this.m_nonSparseMarketBasketFormat = m;
/* 352:    */   }
/* 353:    */   
/* 354:    */   public boolean getUseOldMarketBasketFormat()
/* 355:    */   {
/* 356:544 */     return this.m_nonSparseMarketBasketFormat;
/* 357:    */   }
/* 358:    */   
/* 359:    */   public String useOldMarketBasketFormatTipText()
/* 360:    */   {
/* 361:554 */     return "Output instances that contain unary attributes with absence indicated by missing values. Apriori operates faster with this format.";
/* 362:    */   }
/* 363:    */   
/* 364:    */   public void setUseSparseFormat(boolean s)
/* 365:    */   {
/* 366:566 */     this.m_sparseFormat = s;
/* 367:    */   }
/* 368:    */   
/* 369:    */   public boolean getUseSparseFormat()
/* 370:    */   {
/* 371:575 */     return this.m_sparseFormat;
/* 372:    */   }
/* 373:    */   
/* 374:    */   public String useSparseFormatTipText()
/* 375:    */   {
/* 376:585 */     return "Output sparse instances (can't be used in conjunction with useOldMarketBasketFormat).";
/* 377:    */   }
/* 378:    */   
/* 379:    */   public void setAggregationType(SelectedTag d)
/* 380:    */   {
/* 381:596 */     int ordinal = d.getSelectedTag().getID();
/* 382:598 */     for (NumericAggregation n : NumericAggregation.values()) {
/* 383:599 */       if (n.ordinal() == ordinal)
/* 384:    */       {
/* 385:600 */         this.m_numericAggregation = n;
/* 386:601 */         break;
/* 387:    */       }
/* 388:    */     }
/* 389:    */   }
/* 390:    */   
/* 391:    */   public SelectedTag getAggregationType()
/* 392:    */   {
/* 393:613 */     return new SelectedTag(this.m_numericAggregation.ordinal(), TAGS_SELECTION);
/* 394:    */   }
/* 395:    */   
/* 396:    */   public String aggregationTypeTipText()
/* 397:    */   {
/* 398:623 */     return "The type of aggregation to apply to numeric attributes.";
/* 399:    */   }
/* 400:    */   
/* 401:    */   public Enumeration<Option> listOptions()
/* 402:    */   {
/* 403:633 */     Vector<Option> newVector = new Vector();
/* 404:    */     
/* 405:635 */     newVector.add(new Option("\tIndex or name of attribute to group by. e.g. transaction ID\n\t(default: first)", "G", 1, "-G <index | name | first | last>"));
/* 406:    */     
/* 407:    */ 
/* 408:638 */     newVector.add(new Option("\tOutput instances in Weka's old market basket format (i.e. unary attributes with absence indicated\n\t by missing values.", "B", 0, "-B"));
/* 409:    */     
/* 410:    */ 
/* 411:641 */     newVector.add(new Option("\tOutput sparse instances (can't be used in conjunction with -B)", "S", 0, "-S"));
/* 412:    */     
/* 413:643 */     newVector.add(new Option("\tAggregation function for numeric attributes.\n\t(default: sum).", "A", 1, "-A <Average | Sum | Maximum | Minimum>"));
/* 414:    */     
/* 415:    */ 
/* 416:    */ 
/* 417:    */ 
/* 418:648 */     return newVector.elements();
/* 419:    */   }
/* 420:    */   
/* 421:    */   public void setOptions(String[] options)
/* 422:    */     throws Exception
/* 423:    */   {
/* 424:688 */     String groupName = Utils.getOption('G', options);
/* 425:689 */     if (groupName.length() != 0) {
/* 426:690 */       setGroupingAttribute(groupName);
/* 427:    */     }
/* 428:693 */     setUseOldMarketBasketFormat(Utils.getFlag('B', options));
/* 429:694 */     setUseSparseFormat(Utils.getFlag('S', options));
/* 430:    */     
/* 431:696 */     String aggregation = Utils.getOption('A', options);
/* 432:697 */     if (aggregation.length() != 0)
/* 433:    */     {
/* 434:698 */       NumericAggregation selected = null;
/* 435:699 */       for (NumericAggregation n : NumericAggregation.values()) {
/* 436:700 */         if (n.toString().equalsIgnoreCase(aggregation))
/* 437:    */         {
/* 438:701 */           selected = n;
/* 439:702 */           break;
/* 440:    */         }
/* 441:    */       }
/* 442:705 */       if (selected == null) {
/* 443:706 */         throw new Exception("Unknown aggregation type: " + aggregation + "!");
/* 444:    */       }
/* 445:709 */       setAggregationType(new SelectedTag(selected.ordinal(), TAGS_SELECTION));
/* 446:    */     }
/* 447:712 */     Utils.checkForRemainingOptions(options);
/* 448:    */   }
/* 449:    */   
/* 450:    */   public String[] getOptions()
/* 451:    */   {
/* 452:722 */     ArrayList<String> options = new ArrayList();
/* 453:    */     
/* 454:724 */     options.add("-G");
/* 455:725 */     options.add(getGroupingAttribute());
/* 456:726 */     options.add("-A");
/* 457:727 */     options.add(this.m_numericAggregation.toString());
/* 458:728 */     if (getUseOldMarketBasketFormat()) {
/* 459:729 */       options.add("-B");
/* 460:730 */     } else if (getUseSparseFormat()) {
/* 461:731 */       options.add("-S");
/* 462:    */     }
/* 463:734 */     return (String[])options.toArray(new String[1]);
/* 464:    */   }
/* 465:    */   
/* 466:    */   public String getRevision()
/* 467:    */   {
/* 468:744 */     return RevisionUtils.extract("$Revision: 10339 $");
/* 469:    */   }
/* 470:    */   
/* 471:    */   public static void main(String[] args)
/* 472:    */   {
/* 473:753 */     runFilter(new Denormalize(), args);
/* 474:    */   }
/* 475:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.instance.Denormalize
 * JD-Core Version:    0.7.0.1
 */