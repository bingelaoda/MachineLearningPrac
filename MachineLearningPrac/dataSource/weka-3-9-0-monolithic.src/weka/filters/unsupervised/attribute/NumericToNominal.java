/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.HashSet;
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
/*  17:    */ import weka.core.SparseInstance;
/*  18:    */ import weka.core.Utils;
/*  19:    */ import weka.filters.SimpleBatchFilter;
/*  20:    */ 
/*  21:    */ public class NumericToNominal
/*  22:    */   extends SimpleBatchFilter
/*  23:    */ {
/*  24:    */   private static final long serialVersionUID = -6614630932899796239L;
/*  25:    */   protected static final int MAX_DECIMALS = 6;
/*  26: 79 */   protected Range m_Cols = new Range("first-last");
/*  27: 82 */   protected String m_DefaultCols = "first-last";
/*  28:    */   
/*  29:    */   public String globalInfo()
/*  30:    */   {
/*  31: 92 */     return "A filter for turning numeric attributes into nominal ones. Unlike discretization, it just takes all numeric values and adds them to the list of nominal values of that attribute. Useful after CSV imports, to enforce certain attributes to become nominal, e.g., the class attribute, containing values from 1 to 5.";
/*  32:    */   }
/*  33:    */   
/*  34:    */   public Enumeration<Option> listOptions()
/*  35:    */   {
/*  36:107 */     Vector<Option> result = new Vector(2);
/*  37:    */     
/*  38:109 */     result.addElement(new Option("\tSpecifies list of columns to Discretize. First and last are valid indexes.\n\t(default: first-last)", "R", 1, "-R <col1,col2-col4,...>"));
/*  39:    */     
/*  40:    */ 
/*  41:    */ 
/*  42:    */ 
/*  43:114 */     result.addElement(new Option("\tInvert matching sense of column indexes.", "V", 0, "-V"));
/*  44:    */     
/*  45:    */ 
/*  46:117 */     return result.elements();
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void setOptions(String[] options)
/*  50:    */     throws Exception
/*  51:    */   {
/*  52:146 */     setInvertSelection(Utils.getFlag('V', options));
/*  53:    */     
/*  54:148 */     String tmpStr = Utils.getOption('R', options);
/*  55:149 */     if (tmpStr.length() != 0) {
/*  56:150 */       setAttributeIndices(tmpStr);
/*  57:    */     } else {
/*  58:152 */       setAttributeIndices(this.m_DefaultCols);
/*  59:    */     }
/*  60:155 */     if (getInputFormat() != null) {
/*  61:156 */       setInputFormat(getInputFormat());
/*  62:    */     }
/*  63:159 */     super.setOptions(options);
/*  64:    */     
/*  65:161 */     Utils.checkForRemainingOptions(options);
/*  66:    */   }
/*  67:    */   
/*  68:    */   public String[] getOptions()
/*  69:    */   {
/*  70:172 */     Vector<String> result = new Vector();
/*  71:174 */     if (!getAttributeIndices().equals(""))
/*  72:    */     {
/*  73:175 */       result.add("-R");
/*  74:176 */       result.add(getAttributeIndices());
/*  75:    */     }
/*  76:179 */     if (getInvertSelection()) {
/*  77:180 */       result.add("-V");
/*  78:    */     }
/*  79:183 */     Collections.addAll(result, super.getOptions());
/*  80:    */     
/*  81:185 */     return (String[])result.toArray(new String[result.size()]);
/*  82:    */   }
/*  83:    */   
/*  84:    */   public String invertSelectionTipText()
/*  85:    */   {
/*  86:195 */     return "Set attribute selection mode. If false, only selected (numeric) attributes in the range will be 'nominalized'; if true, only non-selected attributes will be 'nominalized'.";
/*  87:    */   }
/*  88:    */   
/*  89:    */   public boolean getInvertSelection()
/*  90:    */   {
/*  91:206 */     return this.m_Cols.getInvert();
/*  92:    */   }
/*  93:    */   
/*  94:    */   public void setInvertSelection(boolean value)
/*  95:    */   {
/*  96:217 */     this.m_Cols.setInvert(value);
/*  97:    */   }
/*  98:    */   
/*  99:    */   public String attributeIndicesTipText()
/* 100:    */   {
/* 101:227 */     return "Specify range of attributes to act on. This is a comma separated list of attribute indices, with \"first\" and \"last\" valid values. Specify an inclusive range with \"-\". E.g: \"first-3,5,6-10,last\".";
/* 102:    */   }
/* 103:    */   
/* 104:    */   public String getAttributeIndices()
/* 105:    */   {
/* 106:239 */     return this.m_Cols.getRanges();
/* 107:    */   }
/* 108:    */   
/* 109:    */   public void setAttributeIndices(String value)
/* 110:    */   {
/* 111:252 */     this.m_Cols.setRanges(value);
/* 112:    */   }
/* 113:    */   
/* 114:    */   public void setAttributeIndicesArray(int[] value)
/* 115:    */   {
/* 116:265 */     setAttributeIndices(Range.indicesToRangeList(value));
/* 117:    */   }
/* 118:    */   
/* 119:    */   public Capabilities getCapabilities()
/* 120:    */   {
/* 121:276 */     Capabilities result = super.getCapabilities();
/* 122:277 */     result.disableAll();
/* 123:    */     
/* 124:    */ 
/* 125:280 */     result.enableAllAttributes();
/* 126:281 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 127:    */     
/* 128:    */ 
/* 129:284 */     result.enableAllClasses();
/* 130:285 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 131:286 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 132:    */     
/* 133:288 */     return result;
/* 134:    */   }
/* 135:    */   
/* 136:    */   protected Instances determineOutputFormat(Instances inputFormat)
/* 137:    */     throws Exception
/* 138:    */   {
/* 139:318 */     this.m_Cols.setUpper(inputFormat.numAttributes() - 1);
/* 140:319 */     Instances data = new Instances(inputFormat);
/* 141:320 */     ArrayList<Attribute> atts = new ArrayList();
/* 142:321 */     for (int i = 0; i < data.numAttributes(); i++) {
/* 143:322 */       if ((!this.m_Cols.isInRange(i)) || (!data.attribute(i).isNumeric()))
/* 144:    */       {
/* 145:323 */         atts.add(data.attribute(i));
/* 146:    */       }
/* 147:    */       else
/* 148:    */       {
/* 149:328 */         boolean isDate = data.attribute(i).type() == 3;
/* 150:    */         
/* 151:    */ 
/* 152:331 */         HashSet<Double> hash = new HashSet();
/* 153:332 */         for (int n = 0; n < data.numInstances(); n++)
/* 154:    */         {
/* 155:333 */           Instance inst = data.instance(n);
/* 156:334 */           if (!inst.isMissing(i)) {
/* 157:338 */             hash.add(new Double(inst.value(i)));
/* 158:    */           }
/* 159:    */         }
/* 160:342 */         Vector<Double> sorted = new Vector();
/* 161:343 */         for (Double o : hash) {
/* 162:344 */           sorted.add(o);
/* 163:    */         }
/* 164:346 */         Collections.sort(sorted);
/* 165:    */         
/* 166:    */ 
/* 167:349 */         ArrayList<String> values = new ArrayList();
/* 168:350 */         for (Double o : sorted) {
/* 169:351 */           if (isDate) {
/* 170:352 */             values.add(data.attribute(i).formatDate(o.doubleValue()));
/* 171:    */           } else {
/* 172:354 */             values.add(Utils.doubleToString(o.doubleValue(), 6));
/* 173:    */           }
/* 174:    */         }
/* 175:357 */         Attribute newAtt = new Attribute(data.attribute(i).name(), values);
/* 176:358 */         newAtt.setWeight(data.attribute(i).weight());
/* 177:359 */         atts.add(newAtt);
/* 178:    */       }
/* 179:    */     }
/* 180:362 */     Instances result = new Instances(inputFormat.relationName(), atts, 0);
/* 181:363 */     result.setClassIndex(inputFormat.classIndex());
/* 182:    */     
/* 183:365 */     return result;
/* 184:    */   }
/* 185:    */   
/* 186:    */   protected Instances process(Instances instances)
/* 187:    */     throws Exception
/* 188:    */   {
/* 189:388 */     if (!isFirstBatchDone()) {
/* 190:389 */       setOutputFormat(determineOutputFormat(getInputFormat()));
/* 191:    */     }
/* 192:392 */     Instances result = new Instances(getOutputFormat());
/* 193:394 */     for (int i = 0; i < instances.numInstances(); i++)
/* 194:    */     {
/* 195:395 */       Instance inst = instances.instance(i);
/* 196:396 */       double[] values = inst.toDoubleArray();
/* 197:398 */       for (int n = 0; n < values.length; n++) {
/* 198:399 */         if ((this.m_Cols.isInRange(n)) && (instances.attribute(n).isNumeric()) && (!inst.isMissing(n)))
/* 199:    */         {
/* 200:    */           String value;
/* 201:    */           String value;
/* 202:405 */           if (instances.attribute(n).type() == 3) {
/* 203:406 */             value = inst.stringValue(n);
/* 204:    */           } else {
/* 205:408 */             value = Utils.doubleToString(inst.value(n), 6);
/* 206:    */           }
/* 207:411 */           int index = result.attribute(n).indexOfValue(value);
/* 208:412 */           if (index == -1) {
/* 209:413 */             values[n] = Utils.missingValue();
/* 210:    */           } else {
/* 211:415 */             values[n] = index;
/* 212:    */           }
/* 213:    */         }
/* 214:    */       }
/* 215:    */       Instance newInst;
/* 216:    */       Instance newInst;
/* 217:420 */       if ((inst instanceof SparseInstance)) {
/* 218:421 */         newInst = new SparseInstance(inst.weight(), values);
/* 219:    */       } else {
/* 220:423 */         newInst = new DenseInstance(inst.weight(), values);
/* 221:    */       }
/* 222:427 */       copyValues(newInst, false, inst.dataset(), outputFormatPeek());
/* 223:    */       
/* 224:429 */       result.add(newInst);
/* 225:    */     }
/* 226:432 */     return result;
/* 227:    */   }
/* 228:    */   
/* 229:    */   public String getRevision()
/* 230:    */   {
/* 231:442 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 232:    */   }
/* 233:    */   
/* 234:    */   public static void main(String[] args)
/* 235:    */   {
/* 236:451 */     runFilter(new NumericToNominal(), args);
/* 237:    */   }
/* 238:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.NumericToNominal
 * JD-Core Version:    0.7.0.1
 */