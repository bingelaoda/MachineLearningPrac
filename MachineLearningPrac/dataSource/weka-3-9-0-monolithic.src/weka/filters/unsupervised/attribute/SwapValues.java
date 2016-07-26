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
/*  13:    */ import weka.core.RevisionUtils;
/*  14:    */ import weka.core.SingleIndex;
/*  15:    */ import weka.core.UnsupportedAttributeTypeException;
/*  16:    */ import weka.core.Utils;
/*  17:    */ import weka.filters.Filter;
/*  18:    */ import weka.filters.StreamableFilter;
/*  19:    */ import weka.filters.UnsupervisedFilter;
/*  20:    */ 
/*  21:    */ public class SwapValues
/*  22:    */   extends Filter
/*  23:    */   implements UnsupervisedFilter, StreamableFilter, OptionHandler
/*  24:    */ {
/*  25:    */   static final long serialVersionUID = 6155834679414275855L;
/*  26: 78 */   private final SingleIndex m_AttIndex = new SingleIndex("last");
/*  27: 81 */   private final SingleIndex m_FirstIndex = new SingleIndex("first");
/*  28: 84 */   private final SingleIndex m_SecondIndex = new SingleIndex("last");
/*  29:    */   
/*  30:    */   public String globalInfo()
/*  31:    */   {
/*  32: 94 */     return "Swaps two values of a nominal attribute.";
/*  33:    */   }
/*  34:    */   
/*  35:    */   public Capabilities getCapabilities()
/*  36:    */   {
/*  37:105 */     Capabilities result = super.getCapabilities();
/*  38:106 */     result.disableAll();
/*  39:    */     
/*  40:    */ 
/*  41:109 */     result.enableAllAttributes();
/*  42:110 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  43:    */     
/*  44:    */ 
/*  45:113 */     result.enableAllClasses();
/*  46:114 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  47:115 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  48:    */     
/*  49:117 */     return result;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public boolean setInputFormat(Instances instanceInfo)
/*  53:    */     throws Exception
/*  54:    */   {
/*  55:134 */     super.setInputFormat(instanceInfo);
/*  56:135 */     this.m_AttIndex.setUpper(instanceInfo.numAttributes() - 1);
/*  57:136 */     this.m_FirstIndex.setUpper(instanceInfo.attribute(this.m_AttIndex.getIndex()).numValues() - 1);
/*  58:    */     
/*  59:138 */     this.m_SecondIndex.setUpper(instanceInfo.attribute(this.m_AttIndex.getIndex()).numValues() - 1);
/*  60:140 */     if (!instanceInfo.attribute(this.m_AttIndex.getIndex()).isNominal()) {
/*  61:141 */       throw new UnsupportedAttributeTypeException("Chosen attribute not nominal.");
/*  62:    */     }
/*  63:144 */     if (instanceInfo.attribute(this.m_AttIndex.getIndex()).numValues() < 2) {
/*  64:145 */       throw new UnsupportedAttributeTypeException("Chosen attribute has less than two values.");
/*  65:    */     }
/*  66:148 */     setOutputFormat();
/*  67:149 */     return true;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public boolean input(Instance instance)
/*  71:    */   {
/*  72:163 */     if (getInputFormat() == null) {
/*  73:164 */       throw new IllegalStateException("No input instance format defined");
/*  74:    */     }
/*  75:166 */     if (this.m_NewBatch)
/*  76:    */     {
/*  77:167 */       resetQueue();
/*  78:168 */       this.m_NewBatch = false;
/*  79:    */     }
/*  80:170 */     Instance newInstance = (Instance)instance.copy();
/*  81:171 */     if (!newInstance.isMissing(this.m_AttIndex.getIndex())) {
/*  82:172 */       if ((int)newInstance.value(this.m_AttIndex.getIndex()) == this.m_SecondIndex.getIndex()) {
/*  83:174 */         newInstance.setValue(this.m_AttIndex.getIndex(), this.m_FirstIndex.getIndex());
/*  84:175 */       } else if ((int)newInstance.value(this.m_AttIndex.getIndex()) == this.m_FirstIndex.getIndex()) {
/*  85:177 */         newInstance.setValue(this.m_AttIndex.getIndex(), this.m_SecondIndex.getIndex());
/*  86:    */       }
/*  87:    */     }
/*  88:180 */     push(newInstance, false);
/*  89:181 */     return true;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public Enumeration<Option> listOptions()
/*  93:    */   {
/*  94:192 */     Vector<Option> newVector = new Vector(3);
/*  95:    */     
/*  96:194 */     newVector.addElement(new Option("\tSets the attribute index (default last).", "C", 1, "-C <col>"));
/*  97:    */     
/*  98:    */ 
/*  99:197 */     newVector.addElement(new Option("\tSets the first value's index (default first).", "F", 1, "-F <value index>"));
/* 100:    */     
/* 101:    */ 
/* 102:    */ 
/* 103:201 */     newVector.addElement(new Option("\tSets the second value's index (default last).", "S", 1, "-S <value index>"));
/* 104:    */     
/* 105:    */ 
/* 106:    */ 
/* 107:205 */     return newVector.elements();
/* 108:    */   }
/* 109:    */   
/* 110:    */   public void setOptions(String[] options)
/* 111:    */     throws Exception
/* 112:    */   {
/* 113:238 */     String attIndex = Utils.getOption('C', options);
/* 114:239 */     if (attIndex.length() != 0) {
/* 115:240 */       setAttributeIndex(attIndex);
/* 116:    */     } else {
/* 117:242 */       setAttributeIndex("last");
/* 118:    */     }
/* 119:245 */     String firstValIndex = Utils.getOption('F', options);
/* 120:246 */     if (firstValIndex.length() != 0) {
/* 121:247 */       setFirstValueIndex(firstValIndex);
/* 122:    */     } else {
/* 123:249 */       setFirstValueIndex("first");
/* 124:    */     }
/* 125:252 */     String secondValIndex = Utils.getOption('S', options);
/* 126:253 */     if (secondValIndex.length() != 0) {
/* 127:254 */       setSecondValueIndex(secondValIndex);
/* 128:    */     } else {
/* 129:256 */       setSecondValueIndex("last");
/* 130:    */     }
/* 131:259 */     if (getInputFormat() != null) {
/* 132:260 */       setInputFormat(getInputFormat());
/* 133:    */     }
/* 134:263 */     Utils.checkForRemainingOptions(options);
/* 135:    */   }
/* 136:    */   
/* 137:    */   public String[] getOptions()
/* 138:    */   {
/* 139:274 */     Vector<String> options = new Vector();
/* 140:    */     
/* 141:276 */     options.add("-C");
/* 142:277 */     options.add("" + getAttributeIndex());
/* 143:278 */     options.add("-F");
/* 144:279 */     options.add("" + getFirstValueIndex());
/* 145:280 */     options.add("-S");
/* 146:281 */     options.add("" + getSecondValueIndex());
/* 147:    */     
/* 148:283 */     return (String[])options.toArray(new String[0]);
/* 149:    */   }
/* 150:    */   
/* 151:    */   public String attributeIndexTipText()
/* 152:    */   {
/* 153:292 */     return "Sets which attribute to process. This attribute must be nominal (\"first\" and \"last\" are valid values)";
/* 154:    */   }
/* 155:    */   
/* 156:    */   public String getAttributeIndex()
/* 157:    */   {
/* 158:303 */     return this.m_AttIndex.getSingleIndex();
/* 159:    */   }
/* 160:    */   
/* 161:    */   public void setAttributeIndex(String attIndex)
/* 162:    */   {
/* 163:313 */     this.m_AttIndex.setSingleIndex(attIndex);
/* 164:    */   }
/* 165:    */   
/* 166:    */   public String firstValueIndexTipText()
/* 167:    */   {
/* 168:322 */     return "The index of the first value.(\"first\" and \"last\" are valid values)";
/* 169:    */   }
/* 170:    */   
/* 171:    */   public String getFirstValueIndex()
/* 172:    */   {
/* 173:333 */     return this.m_FirstIndex.getSingleIndex();
/* 174:    */   }
/* 175:    */   
/* 176:    */   public void setFirstValueIndex(String firstIndex)
/* 177:    */   {
/* 178:343 */     this.m_FirstIndex.setSingleIndex(firstIndex);
/* 179:    */   }
/* 180:    */   
/* 181:    */   public String secondValueIndexTipText()
/* 182:    */   {
/* 183:352 */     return "The index of the second value.(\"first\" and \"last\" are valid values)";
/* 184:    */   }
/* 185:    */   
/* 186:    */   public String getSecondValueIndex()
/* 187:    */   {
/* 188:363 */     return this.m_SecondIndex.getSingleIndex();
/* 189:    */   }
/* 190:    */   
/* 191:    */   public void setSecondValueIndex(String secondIndex)
/* 192:    */   {
/* 193:373 */     this.m_SecondIndex.setSingleIndex(secondIndex);
/* 194:    */   }
/* 195:    */   
/* 196:    */   private void setOutputFormat()
/* 197:    */   {
/* 198:388 */     ArrayList<Attribute> newAtts = new ArrayList(getInputFormat().numAttributes());
/* 199:389 */     for (int j = 0; j < getInputFormat().numAttributes(); j++)
/* 200:    */     {
/* 201:390 */       Attribute att = getInputFormat().attribute(j);
/* 202:391 */       if (j != this.m_AttIndex.getIndex())
/* 203:    */       {
/* 204:392 */         newAtts.add((Attribute)att.copy());
/* 205:    */       }
/* 206:    */       else
/* 207:    */       {
/* 208:397 */         ArrayList<String> newVals = new ArrayList(att.numValues());
/* 209:398 */         for (int i = 0; i < att.numValues(); i++) {
/* 210:399 */           if (i == this.m_FirstIndex.getIndex()) {
/* 211:400 */             newVals.add(att.value(this.m_SecondIndex.getIndex()));
/* 212:401 */           } else if (i == this.m_SecondIndex.getIndex()) {
/* 213:402 */             newVals.add(att.value(this.m_FirstIndex.getIndex()));
/* 214:    */           } else {
/* 215:404 */             newVals.add(att.value(i));
/* 216:    */           }
/* 217:    */         }
/* 218:407 */         Attribute newAtt = new Attribute(att.name(), newVals);
/* 219:408 */         newAtt.setWeight(att.weight());
/* 220:409 */         newAtts.add(newAtt);
/* 221:    */       }
/* 222:    */     }
/* 223:415 */     Instances newData = new Instances(getInputFormat().relationName(), newAtts, 0);
/* 224:416 */     newData.setClassIndex(getInputFormat().classIndex());
/* 225:417 */     setOutputFormat(newData);
/* 226:    */   }
/* 227:    */   
/* 228:    */   public String getRevision()
/* 229:    */   {
/* 230:427 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 231:    */   }
/* 232:    */   
/* 233:    */   public static void main(String[] argv)
/* 234:    */   {
/* 235:436 */     runFilter(new SwapValues(), argv);
/* 236:    */   }
/* 237:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.SwapValues
 * JD-Core Version:    0.7.0.1
 */