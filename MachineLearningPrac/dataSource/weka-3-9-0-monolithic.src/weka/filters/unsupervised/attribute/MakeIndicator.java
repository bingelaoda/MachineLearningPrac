/*   1:    */ package weka.filters.unsupervised.attribute;
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
/*  22:    */ public class MakeIndicator
/*  23:    */   extends Filter
/*  24:    */   implements UnsupervisedFilter, StreamableFilter, OptionHandler
/*  25:    */ {
/*  26:    */   static final long serialVersionUID = 766001176862773163L;
/*  27: 84 */   private final SingleIndex m_AttIndex = new SingleIndex("last");
/*  28:    */   private final Range m_ValIndex;
/*  29: 90 */   private boolean m_Numeric = true;
/*  30:    */   
/*  31:    */   public MakeIndicator()
/*  32:    */   {
/*  33: 97 */     this.m_ValIndex = new Range("last");
/*  34:    */   }
/*  35:    */   
/*  36:    */   public Capabilities getCapabilities()
/*  37:    */   {
/*  38:108 */     Capabilities result = super.getCapabilities();
/*  39:109 */     result.disableAll();
/*  40:    */     
/*  41:    */ 
/*  42:112 */     result.enableAllAttributes();
/*  43:113 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  44:    */     
/*  45:    */ 
/*  46:116 */     result.enableAllClasses();
/*  47:117 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  48:118 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  49:    */     
/*  50:120 */     return result;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public boolean setInputFormat(Instances instanceInfo)
/*  54:    */     throws Exception
/*  55:    */   {
/*  56:138 */     super.setInputFormat(instanceInfo);
/*  57:139 */     this.m_AttIndex.setUpper(instanceInfo.numAttributes() - 1);
/*  58:140 */     this.m_ValIndex.setUpper(instanceInfo.attribute(this.m_AttIndex.getIndex()).numValues() - 1);
/*  59:142 */     if (!instanceInfo.attribute(this.m_AttIndex.getIndex()).isNominal()) {
/*  60:143 */       throw new UnsupportedAttributeTypeException("Chosen attribute not nominal.");
/*  61:    */     }
/*  62:146 */     if (instanceInfo.attribute(this.m_AttIndex.getIndex()).numValues() < 2) {
/*  63:147 */       throw new UnsupportedAttributeTypeException("Chosen attribute has less than two values.");
/*  64:    */     }
/*  65:150 */     setOutputFormat();
/*  66:151 */     return true;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public boolean input(Instance instance)
/*  70:    */   {
/*  71:165 */     if (getInputFormat() == null) {
/*  72:166 */       throw new IllegalStateException("No input instance format defined");
/*  73:    */     }
/*  74:168 */     if (this.m_NewBatch)
/*  75:    */     {
/*  76:169 */       resetQueue();
/*  77:170 */       this.m_NewBatch = false;
/*  78:    */     }
/*  79:172 */     Instance newInstance = (Instance)instance.copy();
/*  80:173 */     if (!newInstance.isMissing(this.m_AttIndex.getIndex())) {
/*  81:174 */       if (this.m_ValIndex.isInRange((int)newInstance.value(this.m_AttIndex.getIndex()))) {
/*  82:175 */         newInstance.setValue(this.m_AttIndex.getIndex(), 1.0D);
/*  83:    */       } else {
/*  84:177 */         newInstance.setValue(this.m_AttIndex.getIndex(), 0.0D);
/*  85:    */       }
/*  86:    */     }
/*  87:180 */     push(newInstance, false);
/*  88:181 */     return true;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public Enumeration<Option> listOptions()
/*  92:    */   {
/*  93:192 */     Vector<Option> newVector = new Vector(3);
/*  94:    */     
/*  95:194 */     newVector.addElement(new Option("\tSets the attribute index.", "C", 1, "-C <col>"));
/*  96:    */     
/*  97:    */ 
/*  98:197 */     newVector.addElement(new Option("\tSpecify the list of values to indicate. First and last are\n\tvalid indexes (default last)", "V", 1, "-V <index1,index2-index4,...>"));
/*  99:    */     
/* 100:    */ 
/* 101:    */ 
/* 102:201 */     newVector.addElement(new Option("\tSet if new boolean attribute nominal.", "N", 0, "-N <index>"));
/* 103:    */     
/* 104:    */ 
/* 105:204 */     return newVector.elements();
/* 106:    */   }
/* 107:    */   
/* 108:    */   public void setOptions(String[] options)
/* 109:    */     throws Exception
/* 110:    */   {
/* 111:238 */     String attIndex = Utils.getOption('C', options);
/* 112:239 */     if (attIndex.length() != 0) {
/* 113:240 */       setAttributeIndex(attIndex);
/* 114:    */     } else {
/* 115:242 */       setAttributeIndex("last");
/* 116:    */     }
/* 117:245 */     String valIndex = Utils.getOption('V', options);
/* 118:246 */     if (valIndex.length() != 0) {
/* 119:247 */       setValueIndices(valIndex);
/* 120:    */     } else {
/* 121:249 */       setValueIndices("last");
/* 122:    */     }
/* 123:252 */     setNumeric(!Utils.getFlag('N', options));
/* 124:254 */     if (getInputFormat() != null) {
/* 125:255 */       setInputFormat(getInputFormat());
/* 126:    */     }
/* 127:258 */     Utils.checkForRemainingOptions(options);
/* 128:    */   }
/* 129:    */   
/* 130:    */   public String[] getOptions()
/* 131:    */   {
/* 132:269 */     Vector<String> options = new Vector();
/* 133:    */     
/* 134:271 */     options.add("-C");
/* 135:272 */     options.add("" + getAttributeIndex());
/* 136:273 */     options.add("-V");
/* 137:274 */     options.add(getValueIndices());
/* 138:275 */     if (!getNumeric()) {
/* 139:276 */       options.add("-N");
/* 140:    */     }
/* 141:279 */     return (String[])options.toArray(new String[0]);
/* 142:    */   }
/* 143:    */   
/* 144:    */   public String globalInfo()
/* 145:    */   {
/* 146:288 */     return "A filter that creates a new dataset with a boolean attribute replacing a nominal attribute.  In the new dataset, a value of 1 is assigned to an instance that exhibits a particular range of attribute values, a 0 to an instance that doesn't. The boolean attribute is coded as numeric by default.";
/* 147:    */   }
/* 148:    */   
/* 149:    */   public String attributeIndexTipText()
/* 150:    */   {
/* 151:301 */     return "Sets which attribute should be replaced by the indicator. This attribute must be nominal.";
/* 152:    */   }
/* 153:    */   
/* 154:    */   public String getAttributeIndex()
/* 155:    */   {
/* 156:312 */     return this.m_AttIndex.getSingleIndex();
/* 157:    */   }
/* 158:    */   
/* 159:    */   public void setAttributeIndex(String attIndex)
/* 160:    */   {
/* 161:322 */     this.m_AttIndex.setSingleIndex(attIndex);
/* 162:    */   }
/* 163:    */   
/* 164:    */   public Range getValueRange()
/* 165:    */   {
/* 166:332 */     return this.m_ValIndex;
/* 167:    */   }
/* 168:    */   
/* 169:    */   public String valueIndicesTipText()
/* 170:    */   {
/* 171:341 */     return "Specify range of nominal values to act on. This is a comma separated list of attribute indices (numbered from 1), with \"first\" and \"last\" valid values. Specify an inclusive range with \"-\". E.g: \"first-3,5,6-10,last\".";
/* 172:    */   }
/* 173:    */   
/* 174:    */   public String getValueIndices()
/* 175:    */   {
/* 176:354 */     return this.m_ValIndex.getRanges();
/* 177:    */   }
/* 178:    */   
/* 179:    */   public void setValueIndices(String range)
/* 180:    */   {
/* 181:365 */     this.m_ValIndex.setRanges(range);
/* 182:    */   }
/* 183:    */   
/* 184:    */   public void setValueIndex(int index)
/* 185:    */   {
/* 186:375 */     setValueIndices("" + (index + 1));
/* 187:    */   }
/* 188:    */   
/* 189:    */   public void setValueIndicesArray(int[] indices)
/* 190:    */   {
/* 191:388 */     setValueIndices(Range.indicesToRangeList(indices));
/* 192:    */   }
/* 193:    */   
/* 194:    */   public String numericTipText()
/* 195:    */   {
/* 196:397 */     return "Determines whether the output indicator attribute is numeric. If this is set to false, the output attribute will be nominal.";
/* 197:    */   }
/* 198:    */   
/* 199:    */   public void setNumeric(boolean bool)
/* 200:    */   {
/* 201:408 */     this.m_Numeric = bool;
/* 202:    */   }
/* 203:    */   
/* 204:    */   public boolean getNumeric()
/* 205:    */   {
/* 206:418 */     return this.m_Numeric;
/* 207:    */   }
/* 208:    */   
/* 209:    */   private void setOutputFormat()
/* 210:    */   {
/* 211:432 */     ArrayList<Attribute> newAtts = new ArrayList(getInputFormat().numAttributes());
/* 212:433 */     for (int j = 0; j < getInputFormat().numAttributes(); j++)
/* 213:    */     {
/* 214:434 */       Attribute att = getInputFormat().attribute(j);
/* 215:435 */       if (j != this.m_AttIndex.getIndex())
/* 216:    */       {
/* 217:439 */         newAtts.add(att);
/* 218:    */       }
/* 219:441 */       else if (this.m_Numeric)
/* 220:    */       {
/* 221:442 */         newAtts.add(new Attribute(att.name()));
/* 222:    */       }
/* 223:    */       else
/* 224:    */       {
/* 225:445 */         int[] sel = this.m_ValIndex.getSelection();
/* 226:    */         String vals;
/* 227:    */         String vals;
/* 228:446 */         if (sel.length == 1) {
/* 229:447 */           vals = att.value(sel[0]);
/* 230:    */         } else {
/* 231:449 */           vals = this.m_ValIndex.getRanges().replace(',', '_');
/* 232:    */         }
/* 233:451 */         ArrayList<String> newVals = new ArrayList(2);
/* 234:452 */         newVals.add("neg_" + vals);
/* 235:453 */         newVals.add("pos_" + vals);
/* 236:454 */         newAtts.add(new Attribute(att.name(), newVals));
/* 237:    */       }
/* 238:    */     }
/* 239:460 */     Instances newData = new Instances(getInputFormat().relationName(), newAtts, 0);
/* 240:461 */     newData.setClassIndex(getInputFormat().classIndex());
/* 241:462 */     setOutputFormat(newData);
/* 242:    */   }
/* 243:    */   
/* 244:    */   public String getRevision()
/* 245:    */   {
/* 246:472 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 247:    */   }
/* 248:    */   
/* 249:    */   public static void main(String[] argv)
/* 250:    */   {
/* 251:481 */     runFilter(new MakeIndicator(), argv);
/* 252:    */   }
/* 253:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.MakeIndicator
 * JD-Core Version:    0.7.0.1
 */