/*   1:    */ package weka.filters.unsupervised.instance;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.HashSet;
/*   7:    */ import java.util.Iterator;
/*   8:    */ import java.util.Vector;
/*   9:    */ import weka.core.Attribute;
/*  10:    */ import weka.core.AttributeStats;
/*  11:    */ import weka.core.Capabilities;
/*  12:    */ import weka.core.Capabilities.Capability;
/*  13:    */ import weka.core.Instance;
/*  14:    */ import weka.core.Instances;
/*  15:    */ import weka.core.Option;
/*  16:    */ import weka.core.OptionHandler;
/*  17:    */ import weka.core.RevisionUtils;
/*  18:    */ import weka.core.SingleIndex;
/*  19:    */ import weka.core.UnsupportedAttributeTypeException;
/*  20:    */ import weka.core.Utils;
/*  21:    */ import weka.filters.Filter;
/*  22:    */ import weka.filters.UnsupervisedFilter;
/*  23:    */ 
/*  24:    */ public class RemoveFrequentValues
/*  25:    */   extends Filter
/*  26:    */   implements OptionHandler, UnsupervisedFilter
/*  27:    */ {
/*  28:    */   static final long serialVersionUID = -2447432930070059511L;
/*  29:100 */   private final SingleIndex m_AttIndex = new SingleIndex("last");
/*  30:103 */   protected int m_NumValues = 2;
/*  31:106 */   protected boolean m_LeastValues = false;
/*  32:109 */   protected boolean m_Invert = false;
/*  33:112 */   protected boolean m_ModifyHeader = false;
/*  34:    */   protected int[] m_NominalMapping;
/*  35:118 */   protected HashSet<String> m_Values = null;
/*  36:    */   
/*  37:    */   public String globalInfo()
/*  38:    */   {
/*  39:127 */     return "Determines which values (frequent or infrequent ones) of an (nominal) attribute are retained and filters the instances accordingly. In case of values with the same frequency, they are kept in the way they appear in the original instances object. E.g. if you have the values \"1,2,3,4\" with the frequencies \"10,5,5,3\" and you chose to keep the 2 most common values, the values \"1,2\" would be returned, since the value \"2\" comes before \"3\", even though they have the same frequency.";
/*  40:    */   }
/*  41:    */   
/*  42:    */   public Enumeration<Option> listOptions()
/*  43:    */   {
/*  44:144 */     Vector<Option> newVector = new Vector(5);
/*  45:    */     
/*  46:146 */     newVector.addElement(new Option("\tChoose attribute to be used for selection.", "C", 1, "-C <num>"));
/*  47:    */     
/*  48:148 */     newVector.addElement(new Option("\tNumber of values to retain for the sepcified attribute, \n\ti.e. the ones with the most instances (default 2).", "N", 1, "-N <num>"));
/*  49:    */     
/*  50:    */ 
/*  51:    */ 
/*  52:152 */     newVector.addElement(new Option("\tInstead of values with the most instances the ones with the \n\tleast are retained.\n", "L", 0, "-L"));
/*  53:    */     
/*  54:    */ 
/*  55:155 */     newVector.addElement(new Option("\tWhen selecting on nominal attributes, removes header\n\treferences to excluded values.", "H", 0, "-H"));
/*  56:    */     
/*  57:    */ 
/*  58:158 */     newVector.addElement(new Option("\tInvert matching sense.", "V", 0, "-V"));
/*  59:    */     
/*  60:160 */     return newVector.elements();
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void setOptions(String[] options)
/*  64:    */     throws Exception
/*  65:    */   {
/*  66:205 */     String attIndex = Utils.getOption('C', options);
/*  67:206 */     if (attIndex.length() != 0) {
/*  68:207 */       setAttributeIndex(attIndex);
/*  69:    */     } else {
/*  70:209 */       setAttributeIndex("last");
/*  71:    */     }
/*  72:212 */     String numValues = Utils.getOption('N', options);
/*  73:213 */     if (numValues.length() != 0) {
/*  74:214 */       setNumValues(Integer.parseInt(numValues));
/*  75:    */     } else {
/*  76:216 */       setNumValues(2);
/*  77:    */     }
/*  78:219 */     setUseLeastValues(Utils.getFlag('L', options));
/*  79:    */     
/*  80:221 */     setModifyHeader(Utils.getFlag('H', options));
/*  81:    */     
/*  82:223 */     setInvertSelection(Utils.getFlag('V', options));
/*  83:225 */     if (getInputFormat() != null) {
/*  84:226 */       setInputFormat(getInputFormat());
/*  85:    */     }
/*  86:229 */     Utils.checkForRemainingOptions(options);
/*  87:    */   }
/*  88:    */   
/*  89:    */   public String[] getOptions()
/*  90:    */   {
/*  91:240 */     Vector<String> options = new Vector();
/*  92:    */     
/*  93:242 */     options.add("-C");
/*  94:243 */     options.add("" + getAttributeIndex());
/*  95:244 */     options.add("-N");
/*  96:245 */     options.add("" + getNumValues());
/*  97:246 */     if (getUseLeastValues()) {
/*  98:247 */       options.add("-H");
/*  99:    */     }
/* 100:249 */     if (getModifyHeader()) {
/* 101:250 */       options.add("-H");
/* 102:    */     }
/* 103:252 */     if (getInvertSelection()) {
/* 104:253 */       options.add("-V");
/* 105:    */     }
/* 106:256 */     return (String[])options.toArray(new String[0]);
/* 107:    */   }
/* 108:    */   
/* 109:    */   public String attributeIndexTipText()
/* 110:    */   {
/* 111:266 */     return "Choose attribute to be used for selection (default last).";
/* 112:    */   }
/* 113:    */   
/* 114:    */   public String getAttributeIndex()
/* 115:    */   {
/* 116:275 */     return this.m_AttIndex.getSingleIndex();
/* 117:    */   }
/* 118:    */   
/* 119:    */   public void setAttributeIndex(String attIndex)
/* 120:    */   {
/* 121:284 */     this.m_AttIndex.setSingleIndex(attIndex);
/* 122:    */   }
/* 123:    */   
/* 124:    */   public String numValuesTipText()
/* 125:    */   {
/* 126:294 */     return "The number of values to retain.";
/* 127:    */   }
/* 128:    */   
/* 129:    */   public int getNumValues()
/* 130:    */   {
/* 131:303 */     return this.m_NumValues;
/* 132:    */   }
/* 133:    */   
/* 134:    */   public void setNumValues(int numValues)
/* 135:    */   {
/* 136:312 */     this.m_NumValues = numValues;
/* 137:    */   }
/* 138:    */   
/* 139:    */   public String useLeastValuesTipText()
/* 140:    */   {
/* 141:322 */     return "Retains values with least instance instead of most.";
/* 142:    */   }
/* 143:    */   
/* 144:    */   public boolean getUseLeastValues()
/* 145:    */   {
/* 146:331 */     return this.m_LeastValues;
/* 147:    */   }
/* 148:    */   
/* 149:    */   public void setUseLeastValues(boolean leastValues)
/* 150:    */   {
/* 151:340 */     this.m_LeastValues = leastValues;
/* 152:    */   }
/* 153:    */   
/* 154:    */   public String modifyHeaderTipText()
/* 155:    */   {
/* 156:350 */     return "When selecting on nominal attributes, removes header references to excluded values.";
/* 157:    */   }
/* 158:    */   
/* 159:    */   public boolean getModifyHeader()
/* 160:    */   {
/* 161:361 */     return this.m_ModifyHeader;
/* 162:    */   }
/* 163:    */   
/* 164:    */   public void setModifyHeader(boolean newModifyHeader)
/* 165:    */   {
/* 166:371 */     this.m_ModifyHeader = newModifyHeader;
/* 167:    */   }
/* 168:    */   
/* 169:    */   public String invertSelectionTipText()
/* 170:    */   {
/* 171:381 */     return "Invert matching sense.";
/* 172:    */   }
/* 173:    */   
/* 174:    */   public boolean getInvertSelection()
/* 175:    */   {
/* 176:390 */     return this.m_Invert;
/* 177:    */   }
/* 178:    */   
/* 179:    */   public void setInvertSelection(boolean invert)
/* 180:    */   {
/* 181:400 */     this.m_Invert = invert;
/* 182:    */   }
/* 183:    */   
/* 184:    */   public boolean isNominal()
/* 185:    */   {
/* 186:409 */     if (getInputFormat() == null) {
/* 187:410 */       return false;
/* 188:    */     }
/* 189:412 */     return getInputFormat().attribute(this.m_AttIndex.getIndex()).isNominal();
/* 190:    */   }
/* 191:    */   
/* 192:    */   public void determineValues(Instances inst)
/* 193:    */   {
/* 194:430 */     this.m_AttIndex.setUpper(inst.numAttributes() - 1);
/* 195:431 */     int attIdx = this.m_AttIndex.getIndex();
/* 196:    */     
/* 197:    */ 
/* 198:434 */     this.m_Values = new HashSet();
/* 199:    */     
/* 200:    */ 
/* 201:437 */     AttributeStats stats = inst.attributeStats(attIdx);
/* 202:    */     int count;
/* 203:    */     int count;
/* 204:438 */     if (this.m_Invert) {
/* 205:439 */       count = stats.nominalCounts.length - this.m_NumValues;
/* 206:    */     } else {
/* 207:441 */       count = this.m_NumValues;
/* 208:    */     }
/* 209:444 */     if (count < 1) {
/* 210:445 */       count = 1;
/* 211:    */     }
/* 212:447 */     if (count > stats.nominalCounts.length) {
/* 213:448 */       count = stats.nominalCounts.length;
/* 214:    */     }
/* 215:452 */     Arrays.sort(stats.nominalCounts);
/* 216:    */     int max;
/* 217:    */     int min;
/* 218:    */     int max;
/* 219:453 */     if (this.m_LeastValues)
/* 220:    */     {
/* 221:454 */       int min = stats.nominalCounts[0];
/* 222:455 */       max = stats.nominalCounts[(count - 1)];
/* 223:    */     }
/* 224:    */     else
/* 225:    */     {
/* 226:457 */       min = stats.nominalCounts[(stats.nominalCounts.length - 1 - count + 1)];
/* 227:458 */       max = stats.nominalCounts[(stats.nominalCounts.length - 1)];
/* 228:    */     }
/* 229:463 */     stats = inst.attributeStats(attIdx);
/* 230:464 */     for (int i = 0; i < stats.nominalCounts.length; i++) {
/* 231:465 */       if ((stats.nominalCounts[i] >= min) && (stats.nominalCounts[i] <= max) && (this.m_Values.size() < count)) {
/* 232:467 */         this.m_Values.add(inst.attribute(attIdx).value(i));
/* 233:    */       }
/* 234:    */     }
/* 235:    */   }
/* 236:    */   
/* 237:    */   protected Instances modifyHeader(Instances instanceInfo)
/* 238:    */   {
/* 239:480 */     instanceInfo = new Instances(getInputFormat(), 0);
/* 240:481 */     Attribute oldAtt = instanceInfo.attribute(this.m_AttIndex.getIndex());
/* 241:482 */     int[] selection = new int[this.m_Values.size()];
/* 242:483 */     Iterator<String> iter = this.m_Values.iterator();
/* 243:484 */     int i = 0;
/* 244:485 */     while (iter.hasNext())
/* 245:    */     {
/* 246:486 */       selection[i] = oldAtt.indexOfValue(((String)iter.next()).toString());
/* 247:487 */       i++;
/* 248:    */     }
/* 249:489 */     ArrayList<String> newVals = new ArrayList();
/* 250:490 */     for (i = 0; i < selection.length; i++) {
/* 251:491 */       newVals.add(oldAtt.value(selection[i]));
/* 252:    */     }
/* 253:493 */     Attribute newAtt = new Attribute(oldAtt.name(), newVals);
/* 254:494 */     newAtt.setWeight(oldAtt.weight());
/* 255:495 */     instanceInfo.replaceAttributeAt(newAtt, this.m_AttIndex.getIndex());
/* 256:496 */     this.m_NominalMapping = new int[oldAtt.numValues()];
/* 257:497 */     for (i = 0; i < this.m_NominalMapping.length; i++)
/* 258:    */     {
/* 259:498 */       boolean found = false;
/* 260:499 */       for (int j = 0; j < selection.length; j++) {
/* 261:500 */         if (selection[j] == i)
/* 262:    */         {
/* 263:501 */           this.m_NominalMapping[i] = j;
/* 264:502 */           found = true;
/* 265:503 */           break;
/* 266:    */         }
/* 267:    */       }
/* 268:506 */       if (!found) {
/* 269:507 */         this.m_NominalMapping[i] = -1;
/* 270:    */       }
/* 271:    */     }
/* 272:511 */     return instanceInfo;
/* 273:    */   }
/* 274:    */   
/* 275:    */   public Capabilities getCapabilities()
/* 276:    */   {
/* 277:522 */     Capabilities result = super.getCapabilities();
/* 278:523 */     result.disableAll();
/* 279:    */     
/* 280:    */ 
/* 281:526 */     result.enableAllAttributes();
/* 282:527 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 283:    */     
/* 284:    */ 
/* 285:530 */     result.enableAllClasses();
/* 286:531 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 287:532 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 288:    */     
/* 289:534 */     return result;
/* 290:    */   }
/* 291:    */   
/* 292:    */   public boolean setInputFormat(Instances instanceInfo)
/* 293:    */     throws Exception
/* 294:    */   {
/* 295:549 */     super.setInputFormat(instanceInfo);
/* 296:    */     
/* 297:551 */     this.m_AttIndex.setUpper(instanceInfo.numAttributes() - 1);
/* 298:553 */     if (!isNominal()) {
/* 299:554 */       throw new UnsupportedAttributeTypeException("Can only handle nominal attributes.");
/* 300:    */     }
/* 301:558 */     this.m_Values = null;
/* 302:    */     
/* 303:560 */     return false;
/* 304:    */   }
/* 305:    */   
/* 306:    */   protected void setOutputFormat()
/* 307:    */   {
/* 308:573 */     if (this.m_Values == null)
/* 309:    */     {
/* 310:574 */       setOutputFormat(null); return;
/* 311:    */     }
/* 312:    */     Instances instances;
/* 313:    */     Instances instances;
/* 314:579 */     if (getModifyHeader()) {
/* 315:580 */       instances = modifyHeader(getInputFormat());
/* 316:    */     } else {
/* 317:582 */       instances = new Instances(getInputFormat(), 0);
/* 318:    */     }
/* 319:584 */     setOutputFormat(instances);
/* 320:588 */     for (int i = 0; i < getInputFormat().numInstances(); i++)
/* 321:    */     {
/* 322:589 */       Instance instance = getInputFormat().instance(i);
/* 323:590 */       if (instance.isMissing(this.m_AttIndex.getIndex()))
/* 324:    */       {
/* 325:591 */         push(instance, false);
/* 326:    */       }
/* 327:594 */       else if (this.m_Values.contains(instance.stringValue(this.m_AttIndex.getIndex())))
/* 328:    */       {
/* 329:595 */         if (getModifyHeader()) {
/* 330:596 */           instance.setValue(this.m_AttIndex.getIndex(), this.m_NominalMapping[((int)instance.value(this.m_AttIndex.getIndex()))]);
/* 331:    */         }
/* 332:599 */         push(instance, false);
/* 333:    */       }
/* 334:    */     }
/* 335:    */   }
/* 336:    */   
/* 337:    */   public boolean input(Instance instance)
/* 338:    */   {
/* 339:615 */     if (getInputFormat() == null) {
/* 340:616 */       throw new IllegalStateException("No input instance format defined");
/* 341:    */     }
/* 342:619 */     if (this.m_NewBatch)
/* 343:    */     {
/* 344:620 */       resetQueue();
/* 345:621 */       this.m_NewBatch = false;
/* 346:    */     }
/* 347:624 */     if (isFirstBatchDone())
/* 348:    */     {
/* 349:625 */       push(instance);
/* 350:626 */       return true;
/* 351:    */     }
/* 352:628 */     bufferInput(instance);
/* 353:629 */     return false;
/* 354:    */   }
/* 355:    */   
/* 356:    */   public boolean batchFinished()
/* 357:    */   {
/* 358:643 */     if (getInputFormat() == null) {
/* 359:644 */       throw new IllegalStateException("No input instance format defined");
/* 360:    */     }
/* 361:648 */     if (this.m_Values == null)
/* 362:    */     {
/* 363:649 */       determineValues(getInputFormat());
/* 364:650 */       setOutputFormat();
/* 365:    */     }
/* 366:652 */     flushInput();
/* 367:    */     
/* 368:654 */     this.m_NewBatch = true;
/* 369:655 */     this.m_FirstBatchDone = true;
/* 370:    */     
/* 371:657 */     return numPendingOutput() != 0;
/* 372:    */   }
/* 373:    */   
/* 374:    */   public String getRevision()
/* 375:    */   {
/* 376:667 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 377:    */   }
/* 378:    */   
/* 379:    */   public static void main(String[] argv)
/* 380:    */   {
/* 381:676 */     runFilter(new RemoveFrequentValues(), argv);
/* 382:    */   }
/* 383:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.instance.RemoveFrequentValues
 * JD-Core Version:    0.7.0.1
 */