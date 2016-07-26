/*   1:    */ package weka.filters.unsupervised.instance;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Attribute;
/*   7:    */ import weka.core.Capabilities;
/*   8:    */ import weka.core.Capabilities.Capability;
/*   9:    */ import weka.core.Instance;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.Option;
/*  12:    */ import weka.core.OptionHandler;
/*  13:    */ import weka.core.Range;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.SingleIndex;
/*  16:    */ import weka.core.UnsupportedAttributeTypeException;
/*  17:    */ import weka.core.Utils;
/*  18:    */ import weka.filters.Filter;
/*  19:    */ import weka.filters.StreamableFilter;
/*  20:    */ import weka.filters.UnsupervisedFilter;
/*  21:    */ 
/*  22:    */ public class RemoveWithValues
/*  23:    */   extends Filter
/*  24:    */   implements UnsupervisedFilter, StreamableFilter, OptionHandler
/*  25:    */ {
/*  26:    */   static final long serialVersionUID = 4752870193679263361L;
/*  27:110 */   private final SingleIndex m_AttIndex = new SingleIndex("last");
/*  28:    */   protected Range m_Values;
/*  29:116 */   protected double m_Value = 0.0D;
/*  30:119 */   protected boolean m_MatchMissingValues = false;
/*  31:122 */   protected boolean m_ModifyHeader = false;
/*  32:    */   protected int[] m_NominalMapping;
/*  33:128 */   protected boolean m_dontFilterAfterFirstBatch = false;
/*  34:    */   
/*  35:    */   public String globalInfo()
/*  36:    */   {
/*  37:137 */     return "Filters instances according to the value of an attribute.";
/*  38:    */   }
/*  39:    */   
/*  40:    */   public RemoveWithValues()
/*  41:    */   {
/*  42:143 */     this.m_Values = new Range("first-last");
/*  43:144 */     this.m_Values.setInvert(true);
/*  44:    */   }
/*  45:    */   
/*  46:    */   public Enumeration<Option> listOptions()
/*  47:    */   {
/*  48:155 */     Vector<Option> newVector = new Vector(7);
/*  49:    */     
/*  50:157 */     newVector.addElement(new Option("\tChoose attribute to be used for selection.", "C", 1, "-C <num>"));
/*  51:    */     
/*  52:159 */     newVector.addElement(new Option("\tNumeric value to be used for selection on numeric\n\tattribute.\n\tInstances with values smaller than given value will\n\tbe selected. (default 0)", "S", 1, "-S <num>"));
/*  53:    */     
/*  54:    */ 
/*  55:    */ 
/*  56:    */ 
/*  57:164 */     newVector.addElement(new Option("\tRange of label indices to be used for selection on\n\tnominal attribute.\n\tFirst and last are valid indexes. (default all values)", "L", 1, "-L <index1,index2-index4,...>"));
/*  58:    */     
/*  59:    */ 
/*  60:    */ 
/*  61:    */ 
/*  62:169 */     newVector.addElement(new Option("\tMissing values count as a match. This setting is\n\tindependent of the -V option.\n\t(default missing values don't match)", "M", 0, "-M"));
/*  63:    */     
/*  64:    */ 
/*  65:    */ 
/*  66:173 */     newVector.addElement(new Option("\tInvert matching sense.", "V", 0, "-V"));
/*  67:174 */     newVector.addElement(new Option("\tWhen selecting on nominal attributes, removes header\n\treferences to excluded values.", "H", 0, "-H"));
/*  68:    */     
/*  69:    */ 
/*  70:177 */     newVector.addElement(new Option("\tDo not apply the filter to instances that arrive after the first\n\t(training) batch. The default is to apply the filter (i.e.\n\tthe filter may not return an instance if it matches the remove criteria)", "F", 0, "-F"));
/*  71:    */     
/*  72:    */ 
/*  73:    */ 
/*  74:    */ 
/*  75:    */ 
/*  76:    */ 
/*  77:184 */     return newVector.elements();
/*  78:    */   }
/*  79:    */   
/*  80:    */   public void setOptions(String[] options)
/*  81:    */     throws Exception
/*  82:    */   {
/*  83:247 */     String attIndex = Utils.getOption('C', options);
/*  84:248 */     if (attIndex.length() != 0) {
/*  85:249 */       setAttributeIndex(attIndex);
/*  86:    */     } else {
/*  87:251 */       setAttributeIndex("last");
/*  88:    */     }
/*  89:254 */     String splitPoint = Utils.getOption('S', options);
/*  90:255 */     if (splitPoint.length() != 0) {
/*  91:256 */       setSplitPoint(new Double(splitPoint).doubleValue());
/*  92:    */     } else {
/*  93:258 */       setSplitPoint(0.0D);
/*  94:    */     }
/*  95:261 */     String convertList = Utils.getOption('L', options);
/*  96:262 */     if (convertList.length() != 0) {
/*  97:263 */       setNominalIndices(convertList);
/*  98:    */     } else {
/*  99:265 */       setNominalIndices("first-last");
/* 100:    */     }
/* 101:267 */     setInvertSelection(Utils.getFlag('V', options));
/* 102:268 */     setMatchMissingValues(Utils.getFlag('M', options));
/* 103:269 */     setModifyHeader(Utils.getFlag('H', options));
/* 104:270 */     setDontFilterAfterFirstBatch(Utils.getFlag('F', options));
/* 105:273 */     if (getInputFormat() != null) {
/* 106:274 */       setInputFormat(getInputFormat());
/* 107:    */     }
/* 108:277 */     Utils.checkForRemainingOptions(options);
/* 109:    */   }
/* 110:    */   
/* 111:    */   public String[] getOptions()
/* 112:    */   {
/* 113:288 */     Vector<String> options = new Vector();
/* 114:    */     
/* 115:290 */     options.add("-S");
/* 116:291 */     options.add("" + getSplitPoint());
/* 117:292 */     options.add("-C");
/* 118:293 */     options.add("" + getAttributeIndex());
/* 119:294 */     if (!getNominalIndices().equals(""))
/* 120:    */     {
/* 121:295 */       options.add("-L");
/* 122:296 */       options.add(getNominalIndices());
/* 123:    */     }
/* 124:298 */     if (getInvertSelection()) {
/* 125:299 */       options.add("-V");
/* 126:    */     }
/* 127:301 */     if (getMatchMissingValues()) {
/* 128:302 */       options.add("-M");
/* 129:    */     }
/* 130:304 */     if (getModifyHeader()) {
/* 131:305 */       options.add("-H");
/* 132:    */     }
/* 133:307 */     if (getDontFilterAfterFirstBatch()) {
/* 134:308 */       options.add("-F");
/* 135:    */     }
/* 136:311 */     return (String[])options.toArray(new String[0]);
/* 137:    */   }
/* 138:    */   
/* 139:    */   public Capabilities getCapabilities()
/* 140:    */   {
/* 141:322 */     Capabilities result = super.getCapabilities();
/* 142:323 */     result.disableAll();
/* 143:    */     
/* 144:    */ 
/* 145:326 */     result.enableAllAttributes();
/* 146:327 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 147:    */     
/* 148:    */ 
/* 149:330 */     result.enableAllClasses();
/* 150:331 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 151:332 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 152:    */     
/* 153:334 */     return result;
/* 154:    */   }
/* 155:    */   
/* 156:    */   public boolean setInputFormat(Instances instanceInfo)
/* 157:    */     throws Exception
/* 158:    */   {
/* 159:350 */     super.setInputFormat(instanceInfo);
/* 160:    */     
/* 161:352 */     this.m_AttIndex.setUpper(instanceInfo.numAttributes() - 1);
/* 162:353 */     if ((!isNumeric()) && (!isNominal())) {
/* 163:354 */       throw new UnsupportedAttributeTypeException("Can only handle numeric or nominal attributes.");
/* 164:    */     }
/* 165:357 */     this.m_Values.setUpper(instanceInfo.attribute(this.m_AttIndex.getIndex()).numValues() - 1);
/* 166:359 */     if ((isNominal()) && (this.m_ModifyHeader))
/* 167:    */     {
/* 168:360 */       instanceInfo = new Instances(instanceInfo, 0);
/* 169:361 */       Attribute oldAtt = instanceInfo.attribute(this.m_AttIndex.getIndex());
/* 170:362 */       int[] selection = this.m_Values.getSelection();
/* 171:363 */       ArrayList<String> newVals = new ArrayList();
/* 172:364 */       for (int element : selection) {
/* 173:365 */         newVals.add(oldAtt.value(element));
/* 174:    */       }
/* 175:367 */       Attribute newAtt = new Attribute(oldAtt.name(), newVals);
/* 176:368 */       newAtt.setWeight(oldAtt.weight());
/* 177:369 */       instanceInfo.replaceAttributeAt(newAtt, this.m_AttIndex.getIndex());
/* 178:370 */       this.m_NominalMapping = new int[oldAtt.numValues()];
/* 179:371 */       for (int i = 0; i < this.m_NominalMapping.length; i++)
/* 180:    */       {
/* 181:372 */         boolean found = false;
/* 182:373 */         for (int j = 0; j < selection.length; j++) {
/* 183:374 */           if (selection[j] == i)
/* 184:    */           {
/* 185:375 */             this.m_NominalMapping[i] = j;
/* 186:376 */             found = true;
/* 187:377 */             break;
/* 188:    */           }
/* 189:    */         }
/* 190:380 */         if (!found) {
/* 191:381 */           this.m_NominalMapping[i] = -1;
/* 192:    */         }
/* 193:    */       }
/* 194:    */     }
/* 195:385 */     setOutputFormat(instanceInfo);
/* 196:386 */     return true;
/* 197:    */   }
/* 198:    */   
/* 199:    */   public boolean input(Instance instance)
/* 200:    */   {
/* 201:401 */     if (getInputFormat() == null) {
/* 202:402 */       throw new IllegalStateException("No input instance format defined");
/* 203:    */     }
/* 204:404 */     if (this.m_NewBatch)
/* 205:    */     {
/* 206:405 */       resetQueue();
/* 207:406 */       this.m_NewBatch = false;
/* 208:    */     }
/* 209:409 */     if ((isFirstBatchDone()) && (this.m_dontFilterAfterFirstBatch))
/* 210:    */     {
/* 211:410 */       push((Instance)instance.copy(), false);
/* 212:411 */       return true;
/* 213:    */     }
/* 214:414 */     if (instance.isMissing(this.m_AttIndex.getIndex()))
/* 215:    */     {
/* 216:415 */       if (!getMatchMissingValues())
/* 217:    */       {
/* 218:416 */         push((Instance)instance.copy(), false);
/* 219:417 */         return true;
/* 220:    */       }
/* 221:419 */       return false;
/* 222:    */     }
/* 223:422 */     if (isNumeric()) {
/* 224:423 */       if (!this.m_Values.getInvert())
/* 225:    */       {
/* 226:424 */         if (instance.value(this.m_AttIndex.getIndex()) < this.m_Value)
/* 227:    */         {
/* 228:425 */           push((Instance)instance.copy(), false);
/* 229:426 */           return true;
/* 230:    */         }
/* 231:    */       }
/* 232:429 */       else if (instance.value(this.m_AttIndex.getIndex()) >= this.m_Value)
/* 233:    */       {
/* 234:430 */         push((Instance)instance.copy(), false);
/* 235:431 */         return true;
/* 236:    */       }
/* 237:    */     }
/* 238:435 */     if ((isNominal()) && 
/* 239:436 */       (this.m_Values.isInRange((int)instance.value(this.m_AttIndex.getIndex()))))
/* 240:    */     {
/* 241:437 */       Instance temp = (Instance)instance.copy();
/* 242:438 */       if (getModifyHeader()) {
/* 243:439 */         temp.setValue(this.m_AttIndex.getIndex(), this.m_NominalMapping[((int)instance.value(this.m_AttIndex.getIndex()))]);
/* 244:    */       }
/* 245:442 */       push(temp, false);
/* 246:443 */       return true;
/* 247:    */     }
/* 248:446 */     return false;
/* 249:    */   }
/* 250:    */   
/* 251:    */   public boolean mayRemoveInstanceAfterFirstBatchDone()
/* 252:    */   {
/* 253:459 */     return true;
/* 254:    */   }
/* 255:    */   
/* 256:    */   public boolean isNominal()
/* 257:    */   {
/* 258:469 */     if (getInputFormat() == null) {
/* 259:470 */       return false;
/* 260:    */     }
/* 261:472 */     return getInputFormat().attribute(this.m_AttIndex.getIndex()).isNominal();
/* 262:    */   }
/* 263:    */   
/* 264:    */   public boolean isNumeric()
/* 265:    */   {
/* 266:483 */     if (getInputFormat() == null) {
/* 267:484 */       return false;
/* 268:    */     }
/* 269:486 */     return getInputFormat().attribute(this.m_AttIndex.getIndex()).isNumeric();
/* 270:    */   }
/* 271:    */   
/* 272:    */   public String modifyHeaderTipText()
/* 273:    */   {
/* 274:497 */     return "When selecting on nominal attributes, removes header references to excluded values.";
/* 275:    */   }
/* 276:    */   
/* 277:    */   public boolean getModifyHeader()
/* 278:    */   {
/* 279:509 */     return this.m_ModifyHeader;
/* 280:    */   }
/* 281:    */   
/* 282:    */   public void setModifyHeader(boolean newModifyHeader)
/* 283:    */   {
/* 284:520 */     this.m_ModifyHeader = newModifyHeader;
/* 285:    */   }
/* 286:    */   
/* 287:    */   public String attributeIndexTipText()
/* 288:    */   {
/* 289:530 */     return "Choose attribute to be used for selection (default last).";
/* 290:    */   }
/* 291:    */   
/* 292:    */   public String getAttributeIndex()
/* 293:    */   {
/* 294:540 */     return this.m_AttIndex.getSingleIndex();
/* 295:    */   }
/* 296:    */   
/* 297:    */   public void setAttributeIndex(String attIndex)
/* 298:    */   {
/* 299:550 */     this.m_AttIndex.setSingleIndex(attIndex);
/* 300:    */   }
/* 301:    */   
/* 302:    */   public String splitPointTipText()
/* 303:    */   {
/* 304:560 */     return "Numeric value to be used for selection on numeric attribute. Instances with values smaller than given value will be selected.";
/* 305:    */   }
/* 306:    */   
/* 307:    */   public double getSplitPoint()
/* 308:    */   {
/* 309:571 */     return this.m_Value;
/* 310:    */   }
/* 311:    */   
/* 312:    */   public void setSplitPoint(double value)
/* 313:    */   {
/* 314:581 */     this.m_Value = value;
/* 315:    */   }
/* 316:    */   
/* 317:    */   public String matchMissingValuesTipText()
/* 318:    */   {
/* 319:591 */     return "Missing values count as a match. This setting is independent of the invertSelection option.";
/* 320:    */   }
/* 321:    */   
/* 322:    */   public boolean getMatchMissingValues()
/* 323:    */   {
/* 324:602 */     return this.m_MatchMissingValues;
/* 325:    */   }
/* 326:    */   
/* 327:    */   public void setMatchMissingValues(boolean newMatchMissingValues)
/* 328:    */   {
/* 329:612 */     this.m_MatchMissingValues = newMatchMissingValues;
/* 330:    */   }
/* 331:    */   
/* 332:    */   public String invertSelectionTipText()
/* 333:    */   {
/* 334:622 */     return "Invert matching sense.";
/* 335:    */   }
/* 336:    */   
/* 337:    */   public boolean getInvertSelection()
/* 338:    */   {
/* 339:632 */     return !this.m_Values.getInvert();
/* 340:    */   }
/* 341:    */   
/* 342:    */   public void setInvertSelection(boolean invert)
/* 343:    */   {
/* 344:643 */     this.m_Values.setInvert(!invert);
/* 345:    */   }
/* 346:    */   
/* 347:    */   public String nominalIndicesTipText()
/* 348:    */   {
/* 349:653 */     return "Range of label indices to be used for selection on nominal attribute. First and last are valid indexes.";
/* 350:    */   }
/* 351:    */   
/* 352:    */   public String getNominalIndices()
/* 353:    */   {
/* 354:664 */     return this.m_Values.getRanges();
/* 355:    */   }
/* 356:    */   
/* 357:    */   public void setNominalIndices(String rangeList)
/* 358:    */   {
/* 359:676 */     this.m_Values.setRanges(rangeList);
/* 360:    */   }
/* 361:    */   
/* 362:    */   public void setDontFilterAfterFirstBatch(boolean b)
/* 363:    */   {
/* 364:690 */     this.m_dontFilterAfterFirstBatch = b;
/* 365:    */   }
/* 366:    */   
/* 367:    */   public boolean getDontFilterAfterFirstBatch()
/* 368:    */   {
/* 369:704 */     return this.m_dontFilterAfterFirstBatch;
/* 370:    */   }
/* 371:    */   
/* 372:    */   public String dontFilterAfterFirstBatchTipText()
/* 373:    */   {
/* 374:714 */     return "Whether to apply the filtering process to instances that are input after the first (training) batch. The default is false so instances in subsequent batches can potentially get 'consumed' by the filter.";
/* 375:    */   }
/* 376:    */   
/* 377:    */   public void setNominalIndicesArr(int[] values)
/* 378:    */   {
/* 379:729 */     String rangeList = "";
/* 380:730 */     for (int i = 0; i < values.length; i++) {
/* 381:731 */       if (i == 0) {
/* 382:732 */         rangeList = "" + (values[i] + 1);
/* 383:    */       } else {
/* 384:734 */         rangeList = rangeList + "," + (values[i] + 1);
/* 385:    */       }
/* 386:    */     }
/* 387:737 */     setNominalIndices(rangeList);
/* 388:    */   }
/* 389:    */   
/* 390:    */   public String getRevision()
/* 391:    */   {
/* 392:747 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 393:    */   }
/* 394:    */   
/* 395:    */   public static void main(String[] argv)
/* 396:    */   {
/* 397:756 */     runFilter(new RemoveWithValues(), argv);
/* 398:    */   }
/* 399:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.instance.RemoveWithValues
 * JD-Core Version:    0.7.0.1
 */