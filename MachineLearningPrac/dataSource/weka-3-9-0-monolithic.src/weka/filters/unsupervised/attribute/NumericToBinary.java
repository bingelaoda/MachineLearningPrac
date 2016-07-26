/*   1:    */ package weka.filters.unsupervised.attribute;
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
/*  13:    */ import weka.core.Range;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.SparseInstance;
/*  16:    */ import weka.core.Utils;
/*  17:    */ import weka.filters.StreamableFilter;
/*  18:    */ import weka.filters.UnsupervisedFilter;
/*  19:    */ 
/*  20:    */ public class NumericToBinary
/*  21:    */   extends PotentialClassIgnorer
/*  22:    */   implements UnsupervisedFilter, StreamableFilter
/*  23:    */ {
/*  24: 71 */   protected Range m_Cols = new Range("first-last");
/*  25: 74 */   protected String m_DefaultCols = "first-last";
/*  26:    */   static final long serialVersionUID = 2616879323359470802L;
/*  27:    */   
/*  28:    */   public String globalInfo()
/*  29:    */   {
/*  30: 87 */     return "Converts all numeric attributes into binary attributes (apart from the class attribute, if set): if the value of the numeric attribute is exactly zero, the value of the new attribute will be zero. If the value of the numeric attribute is missing, the value of the new attribute will be missing. Otherwise, the value of the new attribute will be one. The new attributes will be nominal.";
/*  31:    */   }
/*  32:    */   
/*  33:    */   public Capabilities getCapabilities()
/*  34:    */   {
/*  35:103 */     Capabilities result = super.getCapabilities();
/*  36:104 */     result.disableAll();
/*  37:    */     
/*  38:    */ 
/*  39:107 */     result.enableAllAttributes();
/*  40:108 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  41:    */     
/*  42:    */ 
/*  43:111 */     result.enableAllClasses();
/*  44:112 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  45:113 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  46:    */     
/*  47:115 */     return result;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public Enumeration<Option> listOptions()
/*  51:    */   {
/*  52:126 */     Vector<Option> result = new Vector(2);
/*  53:    */     
/*  54:128 */     result.addElement(new Option("\tSpecifies list of columns to binarize. First and last are valid indexes.\n\t(default: first-last)", "R", 1, "-R <col1,col2-col4,...>"));
/*  55:    */     
/*  56:    */ 
/*  57:    */ 
/*  58:    */ 
/*  59:133 */     result.addElement(new Option("\tInvert matching sense of column indexes.", "V", 0, "-V"));
/*  60:    */     
/*  61:    */ 
/*  62:136 */     return result.elements();
/*  63:    */   }
/*  64:    */   
/*  65:    */   public void setOptions(String[] options)
/*  66:    */     throws Exception
/*  67:    */   {
/*  68:165 */     setInvertSelection(Utils.getFlag('V', options));
/*  69:    */     
/*  70:167 */     String tmpStr = Utils.getOption('R', options);
/*  71:168 */     if (tmpStr.length() != 0) {
/*  72:169 */       setAttributeIndices(tmpStr);
/*  73:    */     } else {
/*  74:171 */       setAttributeIndices(this.m_DefaultCols);
/*  75:    */     }
/*  76:174 */     if (getInputFormat() != null) {
/*  77:175 */       setInputFormat(getInputFormat());
/*  78:    */     }
/*  79:178 */     super.setOptions(options);
/*  80:    */     
/*  81:180 */     Utils.checkForRemainingOptions(options);
/*  82:    */   }
/*  83:    */   
/*  84:    */   public String[] getOptions()
/*  85:    */   {
/*  86:186 */     Vector<String> result = new Vector();
/*  87:188 */     if (!getAttributeIndices().equals(""))
/*  88:    */     {
/*  89:189 */       result.add("-R");
/*  90:190 */       result.add(getAttributeIndices());
/*  91:    */     }
/*  92:193 */     if (getInvertSelection()) {
/*  93:194 */       result.add("-V");
/*  94:    */     }
/*  95:197 */     return (String[])result.toArray(new String[result.size()]);
/*  96:    */   }
/*  97:    */   
/*  98:    */   public String invertSelectionTipText()
/*  99:    */   {
/* 100:208 */     return "Set attribute selection mode. If false, only selected (numeric) attributes in the range will be 'binarized'; if true, only non-selected attributes will be 'binarized'.";
/* 101:    */   }
/* 102:    */   
/* 103:    */   public boolean getInvertSelection()
/* 104:    */   {
/* 105:219 */     return this.m_Cols.getInvert();
/* 106:    */   }
/* 107:    */   
/* 108:    */   public void setInvertSelection(boolean value)
/* 109:    */   {
/* 110:230 */     this.m_Cols.setInvert(value);
/* 111:    */   }
/* 112:    */   
/* 113:    */   public String attributeIndicesTipText()
/* 114:    */   {
/* 115:240 */     return "Specify range of attributes to act on. This is a comma separated list of attribute indices, with \"first\" and \"last\" valid values. Specify an inclusive range with \"-\". E.g: \"first-3,5,6-10,last\".";
/* 116:    */   }
/* 117:    */   
/* 118:    */   public String getAttributeIndices()
/* 119:    */   {
/* 120:252 */     return this.m_Cols.getRanges();
/* 121:    */   }
/* 122:    */   
/* 123:    */   public void setAttributeIndices(String value)
/* 124:    */   {
/* 125:265 */     this.m_Cols.setRanges(value);
/* 126:    */   }
/* 127:    */   
/* 128:    */   public void setAttributeIndicesArray(int[] value)
/* 129:    */   {
/* 130:278 */     setAttributeIndices(Range.indicesToRangeList(value));
/* 131:    */   }
/* 132:    */   
/* 133:    */   public boolean setInputFormat(Instances instanceInfo)
/* 134:    */     throws Exception
/* 135:    */   {
/* 136:295 */     super.setInputFormat(instanceInfo);
/* 137:296 */     setOutputFormat();
/* 138:297 */     return true;
/* 139:    */   }
/* 140:    */   
/* 141:    */   public boolean input(Instance instance)
/* 142:    */   {
/* 143:310 */     if (getInputFormat() == null) {
/* 144:311 */       throw new IllegalStateException("No input instance format defined");
/* 145:    */     }
/* 146:313 */     if (this.m_NewBatch)
/* 147:    */     {
/* 148:314 */       resetQueue();
/* 149:315 */       this.m_NewBatch = false;
/* 150:    */     }
/* 151:317 */     convertInstance(instance);
/* 152:318 */     return true;
/* 153:    */   }
/* 154:    */   
/* 155:    */   private void setOutputFormat()
/* 156:    */   {
/* 157:332 */     this.m_Cols.setUpper(getInputFormat().numAttributes() - 1);
/* 158:    */     
/* 159:    */ 
/* 160:335 */     int newClassIndex = getInputFormat().classIndex();
/* 161:336 */     ArrayList<Attribute> newAtts = new ArrayList();
/* 162:337 */     for (int j = 0; j < getInputFormat().numAttributes(); j++)
/* 163:    */     {
/* 164:338 */       Attribute att = getInputFormat().attribute(j);
/* 165:339 */       if ((j == newClassIndex) || (!att.isNumeric()) || (!this.m_Cols.isInRange(j)))
/* 166:    */       {
/* 167:340 */         newAtts.add((Attribute)att.copy());
/* 168:    */       }
/* 169:    */       else
/* 170:    */       {
/* 171:342 */         StringBuffer attributeName = new StringBuffer(att.name() + "_binarized");
/* 172:343 */         ArrayList<String> vals = new ArrayList(2);
/* 173:344 */         vals.add("0");
/* 174:345 */         vals.add("1");
/* 175:346 */         newAtts.add(new Attribute(attributeName.toString(), vals));
/* 176:    */       }
/* 177:    */     }
/* 178:349 */     Instances outputFormat = new Instances(getInputFormat().relationName(), newAtts, 0);
/* 179:350 */     outputFormat.setClassIndex(newClassIndex);
/* 180:351 */     setOutputFormat(outputFormat);
/* 181:    */   }
/* 182:    */   
/* 183:    */   private void convertInstance(Instance instance)
/* 184:    */   {
/* 185:362 */     Instance inst = null;
/* 186:363 */     if ((instance instanceof SparseInstance))
/* 187:    */     {
/* 188:364 */       double[] vals = new double[instance.numValues()];
/* 189:365 */       int[] newIndices = new int[instance.numValues()];
/* 190:366 */       for (int j = 0; j < instance.numValues(); j++)
/* 191:    */       {
/* 192:368 */         Attribute att = getInputFormat().attribute(instance.index(j));
/* 193:369 */         if ((!att.isNumeric()) || (instance.index(j) == getInputFormat().classIndex()) || (!this.m_Cols.isInRange(instance.index(j)))) {
/* 194:372 */           vals[j] = instance.valueSparse(j);
/* 195:374 */         } else if (instance.isMissingSparse(j)) {
/* 196:375 */           vals[j] = instance.valueSparse(j);
/* 197:    */         } else {
/* 198:377 */           vals[j] = 1.0D;
/* 199:    */         }
/* 200:380 */         newIndices[j] = instance.index(j);
/* 201:    */       }
/* 202:382 */       inst = new SparseInstance(instance.weight(), vals, newIndices, outputFormatPeek().numAttributes());
/* 203:    */     }
/* 204:    */     else
/* 205:    */     {
/* 206:385 */       double[] vals = new double[outputFormatPeek().numAttributes()];
/* 207:386 */       for (int j = 0; j < getInputFormat().numAttributes(); j++)
/* 208:    */       {
/* 209:387 */         Attribute att = getInputFormat().attribute(j);
/* 210:388 */         if ((!att.isNumeric()) || (j == getInputFormat().classIndex()) || (!this.m_Cols.isInRange(j))) {
/* 211:391 */           vals[j] = instance.value(j);
/* 212:393 */         } else if ((instance.isMissing(j)) || (instance.value(j) == 0.0D)) {
/* 213:394 */           vals[j] = instance.value(j);
/* 214:    */         } else {
/* 215:396 */           vals[j] = 1.0D;
/* 216:    */         }
/* 217:    */       }
/* 218:400 */       inst = new DenseInstance(instance.weight(), vals);
/* 219:    */     }
/* 220:402 */     inst.setDataset(instance.dataset());
/* 221:403 */     push(inst, false);
/* 222:    */   }
/* 223:    */   
/* 224:    */   public String getRevision()
/* 225:    */   {
/* 226:413 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 227:    */   }
/* 228:    */   
/* 229:    */   public static void main(String[] argv)
/* 230:    */   {
/* 231:422 */     runFilter(new NumericToBinary(), argv);
/* 232:    */   }
/* 233:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.NumericToBinary
 * JD-Core Version:    0.7.0.1
 */