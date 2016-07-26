/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Capabilities;
/*  10:    */ import weka.core.Capabilities.Capability;
/*  11:    */ import weka.core.DenseInstance;
/*  12:    */ import weka.core.Instance;
/*  13:    */ import weka.core.Instances;
/*  14:    */ import weka.core.Option;
/*  15:    */ import weka.core.Range;
/*  16:    */ import weka.core.RevisionUtils;
/*  17:    */ import weka.core.Utils;
/*  18:    */ import weka.filters.SimpleBatchFilter;
/*  19:    */ import weka.filters.UnsupervisedFilter;
/*  20:    */ 
/*  21:    */ public class MergeInfrequentNominalValues
/*  22:    */   extends SimpleBatchFilter
/*  23:    */   implements UnsupervisedFilter
/*  24:    */ {
/*  25:    */   static final long serialVersionUID = 4444337331921333847L;
/*  26: 86 */   protected int m_MinimumFrequency = 2;
/*  27: 89 */   protected Range m_SelectCols = new Range();
/*  28:    */   protected int[] m_SelectedAttributes;
/*  29:    */   protected boolean[] m_AttToBeModified;
/*  30:    */   protected int[][] m_NewValues;
/*  31:101 */   protected boolean m_UseShortIDs = false;
/*  32:    */   
/*  33:    */   public String globalInfo()
/*  34:    */   {
/*  35:111 */     return "Merges all values of the specified nominal attribute that are sufficiently infrequent.";
/*  36:    */   }
/*  37:    */   
/*  38:    */   public Enumeration<Option> listOptions()
/*  39:    */   {
/*  40:122 */     Vector<Option> result = new Vector(3);
/*  41:    */     
/*  42:124 */     result.addElement(new Option("\tThe minimum frequency for a value to remain (default: 2).\n", "-N", 1, "-N <int>"));
/*  43:    */     
/*  44:    */ 
/*  45:    */ 
/*  46:128 */     result.addElement(new Option("\tSets list of attributes to act on (or its inverse). 'first and 'last' are accepted as well.'\n\tE.g.: first-5,7,9,20-last\n\t(default: 1,2)", "R", 1, "-R <range>"));
/*  47:    */     
/*  48:    */ 
/*  49:    */ 
/*  50:    */ 
/*  51:133 */     result.addElement(new Option("\tInvert matching sense (i.e. act on all attributes not specified in list)", "V", 0, "-V"));
/*  52:    */     
/*  53:    */ 
/*  54:    */ 
/*  55:137 */     result.addElement(new Option("\tUse short IDs for merged attribute values.", "S", 0, "-S"));
/*  56:    */     
/*  57:139 */     result.addAll(Collections.list(super.listOptions()));
/*  58:    */     
/*  59:141 */     return result.elements();
/*  60:    */   }
/*  61:    */   
/*  62:    */   public String[] getOptions()
/*  63:    */   {
/*  64:152 */     Vector<String> result = new Vector();
/*  65:    */     
/*  66:154 */     result.add("-N");
/*  67:155 */     result.add("" + getMinimumFrequency());
/*  68:    */     
/*  69:157 */     result.add("-R");
/*  70:158 */     result.add(getAttributeIndices());
/*  71:160 */     if (getInvertSelection()) {
/*  72:161 */       result.add("-V");
/*  73:    */     }
/*  74:164 */     if (getUseShortIDs()) {
/*  75:165 */       result.add("-S");
/*  76:    */     }
/*  77:168 */     Collections.addAll(result, super.getOptions());
/*  78:    */     
/*  79:170 */     return (String[])result.toArray(new String[result.size()]);
/*  80:    */   }
/*  81:    */   
/*  82:    */   public void setOptions(String[] options)
/*  83:    */     throws Exception
/*  84:    */   {
/*  85:211 */     String minFrequencyString = Utils.getOption('N', options);
/*  86:212 */     if (minFrequencyString.length() != 0) {
/*  87:213 */       setMinimumFrequency(Integer.parseInt(minFrequencyString));
/*  88:    */     } else {
/*  89:215 */       setMinimumFrequency(2);
/*  90:    */     }
/*  91:218 */     String tmpStr = Utils.getOption('R', options);
/*  92:219 */     if (tmpStr.length() != 0) {
/*  93:220 */       setAttributeIndices(tmpStr);
/*  94:    */     } else {
/*  95:222 */       setAttributeIndices("");
/*  96:    */     }
/*  97:225 */     setInvertSelection(Utils.getFlag('V', options));
/*  98:    */     
/*  99:227 */     setUseShortIDs(Utils.getFlag('S', options));
/* 100:    */     
/* 101:229 */     super.setOptions(options);
/* 102:    */     
/* 103:231 */     Utils.checkForRemainingOptions(options);
/* 104:    */   }
/* 105:    */   
/* 106:    */   public String minimumFrequencyTipText()
/* 107:    */   {
/* 108:242 */     return "The minimum frequency for a value to remain.";
/* 109:    */   }
/* 110:    */   
/* 111:    */   public int getMinimumFrequency()
/* 112:    */   {
/* 113:252 */     return this.m_MinimumFrequency;
/* 114:    */   }
/* 115:    */   
/* 116:    */   public void setMinimumFrequency(int minF)
/* 117:    */   {
/* 118:262 */     this.m_MinimumFrequency = minF;
/* 119:    */   }
/* 120:    */   
/* 121:    */   public String attributeIndicesTipText()
/* 122:    */   {
/* 123:273 */     return "Specify range of attributes to act on (or its inverse). This is a comma separated list of attribute indices, with \"first\" and \"last\" valid values. Specify an inclusive range with \"-\". E.g: \"first-3,5,6-10,last\".";
/* 124:    */   }
/* 125:    */   
/* 126:    */   public String getAttributeIndices()
/* 127:    */   {
/* 128:286 */     return this.m_SelectCols.getRanges();
/* 129:    */   }
/* 130:    */   
/* 131:    */   public void setAttributeIndices(String rangeList)
/* 132:    */   {
/* 133:299 */     this.m_SelectCols.setRanges(rangeList);
/* 134:    */   }
/* 135:    */   
/* 136:    */   public void setAttributeIndicesArray(int[] attributes)
/* 137:    */   {
/* 138:311 */     setAttributeIndices(Range.indicesToRangeList(attributes));
/* 139:    */   }
/* 140:    */   
/* 141:    */   public String invertSelectionTipText()
/* 142:    */   {
/* 143:322 */     return "Determines whether selected attributes are to be acted on or all other attributes are used instead.";
/* 144:    */   }
/* 145:    */   
/* 146:    */   public boolean getInvertSelection()
/* 147:    */   {
/* 148:334 */     return this.m_SelectCols.getInvert();
/* 149:    */   }
/* 150:    */   
/* 151:    */   public void setInvertSelection(boolean invert)
/* 152:    */   {
/* 153:344 */     this.m_SelectCols.setInvert(invert);
/* 154:    */   }
/* 155:    */   
/* 156:    */   public String useShortIDsTipText()
/* 157:    */   {
/* 158:355 */     return "If true, short IDs will be used for merged attribute values.";
/* 159:    */   }
/* 160:    */   
/* 161:    */   public boolean getUseShortIDs()
/* 162:    */   {
/* 163:364 */     return this.m_UseShortIDs;
/* 164:    */   }
/* 165:    */   
/* 166:    */   public void setUseShortIDs(boolean m_UseShortIDs)
/* 167:    */   {
/* 168:373 */     this.m_UseShortIDs = m_UseShortIDs;
/* 169:    */   }
/* 170:    */   
/* 171:    */   public boolean allowAccessToFullInputFormat()
/* 172:    */   {
/* 173:381 */     return true;
/* 174:    */   }
/* 175:    */   
/* 176:    */   protected Instances determineOutputFormat(Instances inputFormat)
/* 177:    */   {
/* 178:394 */     this.m_SelectCols.setUpper(inputFormat.numAttributes() - 1);
/* 179:    */     
/* 180:    */ 
/* 181:397 */     this.m_SelectedAttributes = this.m_SelectCols.getSelection();
/* 182:    */     
/* 183:    */ 
/* 184:400 */     int[][] freqs = new int[inputFormat.numAttributes()][];
/* 185:401 */     for (int m_SelectedAttribute : this.m_SelectedAttributes)
/* 186:    */     {
/* 187:402 */       int current = m_SelectedAttribute;
/* 188:403 */       Attribute att = inputFormat.attribute(current);
/* 189:404 */       if ((current != inputFormat.classIndex()) && (att.isNominal())) {
/* 190:405 */         freqs[current] = new int[att.numValues()];
/* 191:    */       }
/* 192:    */     }
/* 193:410 */     for (Instance inst : inputFormat) {
/* 194:411 */       for (int m_SelectedAttribute : this.m_SelectedAttributes)
/* 195:    */       {
/* 196:412 */         int current = m_SelectedAttribute;
/* 197:413 */         if ((current != inputFormat.classIndex()) && (inputFormat.attribute(current).isNominal())) {
/* 198:415 */           if (!inst.isMissing(current)) {
/* 199:416 */             freqs[current][((int)inst.value(current))] += 1;
/* 200:    */           }
/* 201:    */         }
/* 202:    */       }
/* 203:    */     }
/* 204:423 */     int[] numInfrequentValues = new int[inputFormat.numAttributes()];
/* 205:424 */     for (int m_SelectedAttribute : this.m_SelectedAttributes)
/* 206:    */     {
/* 207:425 */       int current = m_SelectedAttribute;
/* 208:426 */       Attribute att = inputFormat.attribute(current);
/* 209:427 */       if ((current != inputFormat.classIndex()) && (att.isNominal())) {
/* 210:428 */         for (int k = 0; k < att.numValues(); k++)
/* 211:    */         {
/* 212:429 */           if (this.m_Debug) {
/* 213:430 */             System.err.println("Attribute: " + att.name() + " Value: " + att.value(k) + " Freq.: " + freqs[current][k]);
/* 214:    */           }
/* 215:433 */           if (freqs[current][k] < this.m_MinimumFrequency) {
/* 216:434 */             numInfrequentValues[current] += 1;
/* 217:    */           }
/* 218:    */         }
/* 219:    */       }
/* 220:    */     }
/* 221:442 */     this.m_AttToBeModified = new boolean[inputFormat.numAttributes()];
/* 222:443 */     this.m_NewValues = new int[inputFormat.numAttributes()][];
/* 223:444 */     for (int m_SelectedAttribute : this.m_SelectedAttributes)
/* 224:    */     {
/* 225:445 */       int current = m_SelectedAttribute;
/* 226:446 */       Attribute att = inputFormat.attribute(current);
/* 227:447 */       if (numInfrequentValues[current] > 1)
/* 228:    */       {
/* 229:450 */         this.m_AttToBeModified[current] = true;
/* 230:    */         
/* 231:    */ 
/* 232:453 */         int j = 1;
/* 233:454 */         this.m_NewValues[current] = new int[att.numValues()];
/* 234:455 */         for (int k = 0; k < att.numValues(); k++) {
/* 235:456 */           if (freqs[current][k] < this.m_MinimumFrequency) {
/* 236:457 */             this.m_NewValues[current][k] = 0;
/* 237:    */           } else {
/* 238:459 */             this.m_NewValues[current][k] = (j++);
/* 239:    */           }
/* 240:    */         }
/* 241:    */       }
/* 242:    */     }
/* 243:466 */     ArrayList<Attribute> atts = new ArrayList();
/* 244:467 */     for (int i = 0; i < inputFormat.numAttributes(); i++)
/* 245:    */     {
/* 246:468 */       int current = i;
/* 247:469 */       Attribute att = inputFormat.attribute(current);
/* 248:470 */       if (this.m_AttToBeModified[i] != 0)
/* 249:    */       {
/* 250:471 */         ArrayList<String> vals = new ArrayList();
/* 251:472 */         StringBuilder sb = new StringBuilder();
/* 252:473 */         vals.add("");
/* 253:474 */         for (int j = 0; j < att.numValues(); j++) {
/* 254:475 */           if (this.m_NewValues[current][j] == 0)
/* 255:    */           {
/* 256:476 */             if (sb.length() != 0) {
/* 257:477 */               sb.append("_or_");
/* 258:    */             }
/* 259:479 */             sb.append(att.value(j));
/* 260:    */           }
/* 261:    */           else
/* 262:    */           {
/* 263:481 */             vals.add(att.value(j));
/* 264:    */           }
/* 265:    */         }
/* 266:484 */         if (this.m_UseShortIDs) {
/* 267:485 */           vals.set(0, "" + sb.toString().hashCode());
/* 268:    */         } else {
/* 269:487 */           vals.set(0, sb.toString());
/* 270:    */         }
/* 271:489 */         atts.add(new Attribute(att.name() + "_merged_infrequent_values", vals));
/* 272:    */       }
/* 273:    */       else
/* 274:    */       {
/* 275:491 */         atts.add((Attribute)att.copy());
/* 276:    */       }
/* 277:    */     }
/* 278:496 */     Instances data = new Instances(inputFormat.relationName(), atts, 0);
/* 279:497 */     data.setClassIndex(inputFormat.classIndex());
/* 280:498 */     return data;
/* 281:    */   }
/* 282:    */   
/* 283:    */   public Capabilities getCapabilities()
/* 284:    */   {
/* 285:511 */     Capabilities result = super.getCapabilities();
/* 286:512 */     result.disableAll();
/* 287:    */     
/* 288:    */ 
/* 289:515 */     result.enableAllAttributes();
/* 290:516 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 291:    */     
/* 292:    */ 
/* 293:519 */     result.enableAllClasses();
/* 294:520 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 295:521 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 296:    */     
/* 297:523 */     return result;
/* 298:    */   }
/* 299:    */   
/* 300:    */   protected Instances process(Instances instances)
/* 301:    */     throws Exception
/* 302:    */   {
/* 303:537 */     Instances result = new Instances(getOutputFormat(), instances.numInstances());
/* 304:539 */     for (int i = 0; i < instances.numInstances(); i++)
/* 305:    */     {
/* 306:540 */       Instance inst = instances.instance(i);
/* 307:541 */       double[] newData = new double[instances.numAttributes()];
/* 308:542 */       for (int j = 0; j < instances.numAttributes(); j++) {
/* 309:543 */         if ((this.m_AttToBeModified[j] != 0) && (!inst.isMissing(j))) {
/* 310:544 */           newData[j] = this.m_NewValues[j][((int)inst.value(j))];
/* 311:    */         } else {
/* 312:546 */           newData[j] = inst.value(j);
/* 313:    */         }
/* 314:    */       }
/* 315:549 */       DenseInstance instNew = new DenseInstance(1.0D, newData);
/* 316:550 */       instNew.setDataset(result);
/* 317:    */       
/* 318:    */ 
/* 319:553 */       copyValues(instNew, false, inst.dataset(), outputFormatPeek());
/* 320:    */       
/* 321:    */ 
/* 322:556 */       result.add(instNew);
/* 323:    */     }
/* 324:558 */     return result;
/* 325:    */   }
/* 326:    */   
/* 327:    */   public String getRevision()
/* 328:    */   {
/* 329:568 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 330:    */   }
/* 331:    */   
/* 332:    */   public static void main(String[] args)
/* 333:    */   {
/* 334:577 */     runFilter(new MergeInfrequentNominalValues(), args);
/* 335:    */   }
/* 336:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.MergeInfrequentNominalValues
 * JD-Core Version:    0.7.0.1
 */