/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.Capabilities;
/*   9:    */ import weka.core.Capabilities.Capability;
/*  10:    */ import weka.core.DenseInstance;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.Option;
/*  14:    */ import weka.core.OptionHandler;
/*  15:    */ import weka.core.RevisionUtils;
/*  16:    */ import weka.core.SelectedTag;
/*  17:    */ import weka.core.Tag;
/*  18:    */ import weka.core.TechnicalInformation;
/*  19:    */ import weka.core.TechnicalInformation.Field;
/*  20:    */ import weka.core.TechnicalInformation.Type;
/*  21:    */ import weka.core.TechnicalInformationHandler;
/*  22:    */ import weka.core.Utils;
/*  23:    */ import weka.filters.Filter;
/*  24:    */ import weka.filters.MultiFilter;
/*  25:    */ import weka.filters.SimpleBatchFilter;
/*  26:    */ 
/*  27:    */ public class Wavelet
/*  28:    */   extends SimpleBatchFilter
/*  29:    */   implements TechnicalInformationHandler
/*  30:    */ {
/*  31:    */   static final long serialVersionUID = -3335106965521265631L;
/*  32:    */   public static final int ALGORITHM_HAAR = 0;
/*  33:136 */   public static final Tag[] TAGS_ALGORITHM = { new Tag(0, "Haar") };
/*  34:    */   public static final int PADDING_ZERO = 0;
/*  35:141 */   public static final Tag[] TAGS_PADDING = { new Tag(0, "Zero") };
/*  36:144 */   protected Filter m_Filter = null;
/*  37:147 */   protected int m_Algorithm = 0;
/*  38:150 */   protected int m_Padding = 0;
/*  39:    */   
/*  40:    */   public Wavelet()
/*  41:    */   {
/*  42:158 */     this.m_Filter = new MultiFilter();
/*  43:159 */     ((MultiFilter)this.m_Filter).setFilters(new Filter[] { new ReplaceMissingValues(), new Normalize() });
/*  44:    */   }
/*  45:    */   
/*  46:    */   public String globalInfo()
/*  47:    */   {
/*  48:172 */     return "A filter for wavelet transformation.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*  49:    */   }
/*  50:    */   
/*  51:    */   public TechnicalInformation getTechnicalInformation()
/*  52:    */   {
/*  53:188 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.MISC);
/*  54:189 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Wikipedia");
/*  55:190 */     result.setValue(TechnicalInformation.Field.YEAR, "2004");
/*  56:191 */     result.setValue(TechnicalInformation.Field.TITLE, "Discrete wavelet transform");
/*  57:192 */     result.setValue(TechnicalInformation.Field.HTTP, "http://en.wikipedia.org/wiki/Discrete_wavelet_transform");
/*  58:    */     
/*  59:    */ 
/*  60:195 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.MISC);
/*  61:196 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "Kristian Sandberg");
/*  62:197 */     additional.setValue(TechnicalInformation.Field.YEAR, "2000");
/*  63:198 */     additional.setValue(TechnicalInformation.Field.TITLE, "The Haar wavelet transform");
/*  64:199 */     additional.setValue(TechnicalInformation.Field.INSTITUTION, "Dept. of Applied Mathematics");
/*  65:200 */     additional.setValue(TechnicalInformation.Field.ADDRESS, "University of Colorado at Boulder, USA");
/*  66:    */     
/*  67:202 */     additional.setValue(TechnicalInformation.Field.HTTP, "http://amath.colorado.edu/courses/5720/2000Spr/Labs/Haar/haar.html");
/*  68:    */     
/*  69:    */ 
/*  70:205 */     return result;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public Enumeration<Option> listOptions()
/*  74:    */   {
/*  75:216 */     Vector<Option> result = new Vector();
/*  76:    */     
/*  77:218 */     String param = "";
/*  78:219 */     for (int i = 0; i < TAGS_ALGORITHM.length; i++)
/*  79:    */     {
/*  80:220 */       if (i > 0) {
/*  81:221 */         param = param + "|";
/*  82:    */       }
/*  83:223 */       SelectedTag tag = new SelectedTag(TAGS_ALGORITHM[i].getID(), TAGS_ALGORITHM);
/*  84:    */       
/*  85:225 */       param = param + tag.getSelectedTag().getReadable();
/*  86:    */     }
/*  87:227 */     result.addElement(new Option("\tThe algorithm to use.\n\t(default: HAAR)", "A", 1, "-A <" + param + ">"));
/*  88:    */     
/*  89:    */ 
/*  90:230 */     param = "";
/*  91:231 */     for (int i = 0; i < TAGS_PADDING.length; i++)
/*  92:    */     {
/*  93:232 */       if (i > 0) {
/*  94:233 */         param = param + "|";
/*  95:    */       }
/*  96:235 */       SelectedTag tag = new SelectedTag(TAGS_PADDING[i].getID(), TAGS_PADDING);
/*  97:236 */       param = param + tag.getSelectedTag().getReadable();
/*  98:    */     }
/*  99:238 */     result.addElement(new Option("\tThe padding to use.\n\t(default: ZERO)", "P", 1, "-P <" + param + ">"));
/* 100:    */     
/* 101:    */ 
/* 102:241 */     result.addElement(new Option("\tThe filter to use as preprocessing step (classname and options).\n\t(default: MultiFilter with ReplaceMissingValues and Normalize)", "F", 1, "-F <filter specification>"));
/* 103:    */     
/* 104:    */ 
/* 105:    */ 
/* 106:    */ 
/* 107:246 */     result.addAll(Collections.list(super.listOptions()));
/* 108:248 */     if ((getFilter() instanceof OptionHandler))
/* 109:    */     {
/* 110:249 */       result.addElement(new Option("", "", 0, "\nOptions specific to filter " + getFilter().getClass().getName() + " ('-F'):"));
/* 111:    */       
/* 112:    */ 
/* 113:252 */       result.addAll(Collections.list(getFilter().listOptions()));
/* 114:    */     }
/* 115:256 */     return result.elements();
/* 116:    */   }
/* 117:    */   
/* 118:    */   public String[] getOptions()
/* 119:    */   {
/* 120:267 */     Vector<String> result = new Vector();
/* 121:    */     
/* 122:269 */     result.add("-A");
/* 123:270 */     result.add("" + getAlgorithm().getSelectedTag().getReadable());
/* 124:    */     
/* 125:272 */     result.add("-P");
/* 126:273 */     result.add("" + getPadding().getSelectedTag().getReadable());
/* 127:    */     
/* 128:275 */     result.add("-F");
/* 129:276 */     if ((getFilter() instanceof OptionHandler)) {
/* 130:277 */       result.add(getFilter().getClass().getName() + " " + Utils.joinOptions(getFilter().getOptions()));
/* 131:    */     } else {
/* 132:280 */       result.add(getFilter().getClass().getName());
/* 133:    */     }
/* 134:283 */     Collections.addAll(result, super.getOptions());
/* 135:    */     
/* 136:285 */     return (String[])result.toArray(new String[result.size()]);
/* 137:    */   }
/* 138:    */   
/* 139:    */   public void setOptions(String[] options)
/* 140:    */     throws Exception
/* 141:    */   {
/* 142:343 */     super.setOptions(options);
/* 143:    */     
/* 144:345 */     String tmpStr = Utils.getOption("A", options);
/* 145:346 */     if (tmpStr.length() != 0) {
/* 146:347 */       setAlgorithm(new SelectedTag(tmpStr, TAGS_ALGORITHM));
/* 147:    */     } else {
/* 148:349 */       setAlgorithm(new SelectedTag(0, TAGS_ALGORITHM));
/* 149:    */     }
/* 150:352 */     tmpStr = Utils.getOption("P", options);
/* 151:353 */     if (tmpStr.length() != 0) {
/* 152:354 */       setPadding(new SelectedTag(tmpStr, TAGS_PADDING));
/* 153:    */     } else {
/* 154:356 */       setPadding(new SelectedTag(0, TAGS_PADDING));
/* 155:    */     }
/* 156:359 */     tmpStr = Utils.getOption("F", options);
/* 157:360 */     String[] tmpOptions = Utils.splitOptions(tmpStr);
/* 158:361 */     if (tmpOptions.length != 0)
/* 159:    */     {
/* 160:362 */       tmpStr = tmpOptions[0];
/* 161:363 */       tmpOptions[0] = "";
/* 162:364 */       setFilter((Filter)Utils.forName(Filter.class, tmpStr, tmpOptions));
/* 163:    */     }
/* 164:    */     else
/* 165:    */     {
/* 166:366 */       Filter filter = new MultiFilter();
/* 167:367 */       ((MultiFilter)filter).setFilters(new Filter[] { new ReplaceMissingValues(), new Normalize() });
/* 168:    */       
/* 169:    */ 
/* 170:370 */       setFilter(filter);
/* 171:    */     }
/* 172:    */   }
/* 173:    */   
/* 174:    */   public String filterTipText()
/* 175:    */   {
/* 176:381 */     return "The preprocessing filter to use.";
/* 177:    */   }
/* 178:    */   
/* 179:    */   public void setFilter(Filter value)
/* 180:    */   {
/* 181:390 */     this.m_Filter = value;
/* 182:    */   }
/* 183:    */   
/* 184:    */   public Filter getFilter()
/* 185:    */   {
/* 186:399 */     return this.m_Filter;
/* 187:    */   }
/* 188:    */   
/* 189:    */   public String algorithmTipText()
/* 190:    */   {
/* 191:409 */     return "Sets the type of algorithm to use.";
/* 192:    */   }
/* 193:    */   
/* 194:    */   public void setAlgorithm(SelectedTag value)
/* 195:    */   {
/* 196:418 */     if (value.getTags() == TAGS_ALGORITHM) {
/* 197:419 */       this.m_Algorithm = value.getSelectedTag().getID();
/* 198:    */     }
/* 199:    */   }
/* 200:    */   
/* 201:    */   public SelectedTag getAlgorithm()
/* 202:    */   {
/* 203:429 */     return new SelectedTag(this.m_Algorithm, TAGS_ALGORITHM);
/* 204:    */   }
/* 205:    */   
/* 206:    */   public String paddingTipText()
/* 207:    */   {
/* 208:439 */     return "Sets the type of padding to use.";
/* 209:    */   }
/* 210:    */   
/* 211:    */   public void setPadding(SelectedTag value)
/* 212:    */   {
/* 213:448 */     if (value.getTags() == TAGS_PADDING) {
/* 214:449 */       this.m_Padding = value.getSelectedTag().getID();
/* 215:    */     }
/* 216:    */   }
/* 217:    */   
/* 218:    */   public SelectedTag getPadding()
/* 219:    */   {
/* 220:459 */     return new SelectedTag(this.m_Padding, TAGS_PADDING);
/* 221:    */   }
/* 222:    */   
/* 223:    */   protected static int nextPowerOf2(int n)
/* 224:    */   {
/* 225:473 */     int exp = (int)StrictMath.ceil(StrictMath.log(n) / StrictMath.log(2.0D));
/* 226:474 */     exp = StrictMath.max(2, exp);
/* 227:    */     
/* 228:476 */     return (int)StrictMath.pow(2.0D, exp);
/* 229:    */   }
/* 230:    */   
/* 231:    */   protected Instances pad(Instances data)
/* 232:    */   {
/* 233:    */     int numAtts;
/* 234:498 */     switch (this.m_Padding)
/* 235:    */     {
/* 236:    */     case 0: 
/* 237:    */       int numAtts;
/* 238:500 */       if (data.classIndex() > -1) {
/* 239:501 */         numAtts = nextPowerOf2(data.numAttributes() - 1) + 1 - data.numAttributes();
/* 240:    */       } else {
/* 241:504 */         numAtts = nextPowerOf2(data.numAttributes()) - data.numAttributes();
/* 242:    */       }
/* 243:506 */       break;
/* 244:    */     default: 
/* 245:509 */       throw new IllegalStateException("Padding " + new SelectedTag(this.m_Algorithm, TAGS_PADDING) + " not implemented!");
/* 246:    */     }
/* 247:513 */     Instances result = new Instances(data);
/* 248:514 */     String prefix = getAlgorithm().getSelectedTag().getReadable();
/* 249:517 */     if (numAtts > 0)
/* 250:    */     {
/* 251:519 */       boolean isLast = data.classIndex() == data.numAttributes() - 1;
/* 252:520 */       Vector<Integer> padded = new Vector();
/* 253:521 */       for (int i = 0; i < numAtts; i++)
/* 254:    */       {
/* 255:    */         int index;
/* 256:    */         int index;
/* 257:522 */         if (isLast) {
/* 258:523 */           index = result.numAttributes() - 1;
/* 259:    */         } else {
/* 260:525 */           index = result.numAttributes();
/* 261:    */         }
/* 262:528 */         result.insertAttributeAt(new Attribute(prefix + "_padding_" + (i + 1)), index);
/* 263:    */         
/* 264:    */ 
/* 265:    */ 
/* 266:532 */         padded.add(new Integer(index));
/* 267:    */       }
/* 268:536 */       int[] indices = new int[padded.size()];
/* 269:537 */       for (i = 0; i < padded.size(); i++) {
/* 270:538 */         indices[i] = ((Integer)padded.get(i)).intValue();
/* 271:    */       }
/* 272:542 */       switch (this.m_Padding)
/* 273:    */       {
/* 274:    */       case 0: 
/* 275:544 */         for (i = 0; i < result.numInstances(); i++) {
/* 276:545 */           for (int n = 0; n < indices.length; n++) {
/* 277:546 */             result.instance(i).setValue(indices[n], 0.0D);
/* 278:    */           }
/* 279:    */         }
/* 280:    */       }
/* 281:    */     }
/* 282:554 */     data = result;
/* 283:555 */     ArrayList<Attribute> atts = new ArrayList();
/* 284:556 */     int n = 0;
/* 285:557 */     for (int i = 0; i < data.numAttributes(); i++)
/* 286:    */     {
/* 287:558 */       n++;
/* 288:559 */       if (i == data.classIndex()) {
/* 289:560 */         atts.add((Attribute)data.attribute(i).copy());
/* 290:    */       } else {
/* 291:562 */         atts.add(new Attribute(prefix + "_" + n));
/* 292:    */       }
/* 293:    */     }
/* 294:567 */     result = new Instances(data.relationName(), atts, data.numInstances());
/* 295:568 */     result.setClassIndex(data.classIndex());
/* 296:569 */     for (i = 0; i < data.numInstances(); i++) {
/* 297:570 */       result.add(new DenseInstance(1.0D, data.instance(i).toDoubleArray()));
/* 298:    */     }
/* 299:573 */     return result;
/* 300:    */   }
/* 301:    */   
/* 302:    */   protected Instances determineOutputFormat(Instances inputFormat)
/* 303:    */     throws Exception
/* 304:    */   {
/* 305:592 */     return pad(new Instances(inputFormat, 0));
/* 306:    */   }
/* 307:    */   
/* 308:    */   protected Instances processHAAR(Instances instances)
/* 309:    */     throws Exception
/* 310:    */   {
/* 311:615 */     int clsIdx = instances.classIndex();
/* 312:616 */     double[] clsVal = null;
/* 313:617 */     Attribute clsAtt = null;
/* 314:618 */     if (clsIdx > -1)
/* 315:    */     {
/* 316:619 */       clsVal = instances.attributeToDoubleArray(clsIdx);
/* 317:620 */       clsAtt = (Attribute)instances.classAttribute().copy();
/* 318:621 */       instances.setClassIndex(-1);
/* 319:622 */       instances.deleteAttributeAt(clsIdx);
/* 320:    */     }
/* 321:624 */     Instances result = new Instances(instances, 0);
/* 322:625 */     int level = (int)StrictMath.ceil(StrictMath.log(instances.numAttributes()) / StrictMath.log(2.0D));
/* 323:628 */     for (int i = 0; i < instances.numInstances(); i++)
/* 324:    */     {
/* 325:629 */       double[] oldVal = instances.instance(i).toDoubleArray();
/* 326:630 */       double[] newVal = new double[oldVal.length];
/* 327:632 */       for (int n = level; n > 0; n--)
/* 328:    */       {
/* 329:633 */         int length = (int)StrictMath.pow(2.0D, n - 1);
/* 330:635 */         for (int j = 0; j < length; j++)
/* 331:    */         {
/* 332:636 */           newVal[j] = ((oldVal[(j * 2)] + oldVal[(j * 2 + 1)]) / StrictMath.sqrt(2.0D));
/* 333:637 */           newVal[(j + length)] = ((oldVal[(j * 2)] - oldVal[(j * 2 + 1)]) / StrictMath.sqrt(2.0D));
/* 334:    */         }
/* 335:641 */         System.arraycopy(newVal, 0, oldVal, 0, newVal.length);
/* 336:    */       }
/* 337:645 */       result.add(new DenseInstance(1.0D, newVal));
/* 338:    */     }
/* 339:649 */     if (clsIdx > -1)
/* 340:    */     {
/* 341:650 */       result.insertAttributeAt(clsAtt, clsIdx);
/* 342:651 */       result.setClassIndex(clsIdx);
/* 343:652 */       for (i = 0; i < clsVal.length; i++) {
/* 344:653 */         result.instance(i).setClassValue(clsVal[i]);
/* 345:    */       }
/* 346:    */     }
/* 347:657 */     return result;
/* 348:    */   }
/* 349:    */   
/* 350:    */   public Capabilities getCapabilities()
/* 351:    */   {
/* 352:668 */     Capabilities result = super.getCapabilities();
/* 353:669 */     result.disableAll();
/* 354:    */     
/* 355:    */ 
/* 356:672 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 357:673 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/* 358:674 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 359:    */     
/* 360:    */ 
/* 361:677 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 362:678 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/* 363:679 */     result.enable(Capabilities.Capability.DATE_CLASS);
/* 364:680 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 365:    */     
/* 366:682 */     return result;
/* 367:    */   }
/* 368:    */   
/* 369:    */   protected Instances process(Instances instances)
/* 370:    */     throws Exception
/* 371:    */   {
/* 372:696 */     if (!isFirstBatchDone()) {
/* 373:697 */       this.m_Filter.setInputFormat(instances);
/* 374:    */     }
/* 375:699 */     instances = Filter.useFilter(instances, this.m_Filter);
/* 376:701 */     switch (this.m_Algorithm)
/* 377:    */     {
/* 378:    */     case 0: 
/* 379:703 */       return processHAAR(pad(instances));
/* 380:    */     }
/* 381:705 */     throw new IllegalStateException("Algorithm type '" + this.m_Algorithm + "' is not recognized!");
/* 382:    */   }
/* 383:    */   
/* 384:    */   public String getRevision()
/* 385:    */   {
/* 386:717 */     return RevisionUtils.extract("$Revision: 10390 $");
/* 387:    */   }
/* 388:    */   
/* 389:    */   public static void main(String[] args)
/* 390:    */   {
/* 391:726 */     runFilter(new Wavelet(), args);
/* 392:    */   }
/* 393:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.Wavelet
 * JD-Core Version:    0.7.0.1
 */