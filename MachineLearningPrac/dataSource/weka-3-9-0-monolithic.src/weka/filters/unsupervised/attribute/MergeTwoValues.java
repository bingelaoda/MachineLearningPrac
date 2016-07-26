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
/*  21:    */ public class MergeTwoValues
/*  22:    */   extends Filter
/*  23:    */   implements UnsupervisedFilter, StreamableFilter, OptionHandler
/*  24:    */ {
/*  25:    */   static final long serialVersionUID = 2925048980504034018L;
/*  26: 79 */   private final SingleIndex m_AttIndex = new SingleIndex("last");
/*  27: 82 */   private final SingleIndex m_FirstIndex = new SingleIndex("first");
/*  28: 85 */   private final SingleIndex m_SecondIndex = new SingleIndex("last");
/*  29:    */   
/*  30:    */   public String globalInfo()
/*  31:    */   {
/*  32: 95 */     return "Merges two values of a nominal attribute into one value.";
/*  33:    */   }
/*  34:    */   
/*  35:    */   public Capabilities getCapabilities()
/*  36:    */   {
/*  37:106 */     Capabilities result = super.getCapabilities();
/*  38:107 */     result.disableAll();
/*  39:    */     
/*  40:    */ 
/*  41:110 */     result.enableAllAttributes();
/*  42:111 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  43:    */     
/*  44:    */ 
/*  45:114 */     result.enableAllClasses();
/*  46:115 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  47:116 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  48:    */     
/*  49:118 */     return result;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public boolean setInputFormat(Instances instanceInfo)
/*  53:    */     throws Exception
/*  54:    */   {
/*  55:133 */     super.setInputFormat(instanceInfo);
/*  56:134 */     this.m_AttIndex.setUpper(instanceInfo.numAttributes() - 1);
/*  57:135 */     this.m_FirstIndex.setUpper(instanceInfo.attribute(this.m_AttIndex.getIndex()).numValues() - 1);
/*  58:    */     
/*  59:137 */     this.m_SecondIndex.setUpper(instanceInfo.attribute(this.m_AttIndex.getIndex()).numValues() - 1);
/*  60:139 */     if ((instanceInfo.classIndex() > -1) && (instanceInfo.classIndex() == this.m_AttIndex.getIndex())) {
/*  61:141 */       throw new Exception("Cannot process class attribute.");
/*  62:    */     }
/*  63:143 */     if (!instanceInfo.attribute(this.m_AttIndex.getIndex()).isNominal()) {
/*  64:144 */       throw new UnsupportedAttributeTypeException("Chosen attribute not nominal.");
/*  65:    */     }
/*  66:147 */     if (instanceInfo.attribute(this.m_AttIndex.getIndex()).numValues() < 2) {
/*  67:148 */       throw new UnsupportedAttributeTypeException("Chosen attribute has less than two values.");
/*  68:    */     }
/*  69:151 */     if (this.m_SecondIndex.getIndex() <= this.m_FirstIndex.getIndex()) {
/*  70:153 */       throw new Exception("The second index has to be greater than the first.");
/*  71:    */     }
/*  72:156 */     setOutputFormat();
/*  73:157 */     return true;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public boolean input(Instance instance)
/*  77:    */   {
/*  78:171 */     if (getInputFormat() == null) {
/*  79:172 */       throw new IllegalStateException("No input instance format defined");
/*  80:    */     }
/*  81:174 */     if (this.m_NewBatch)
/*  82:    */     {
/*  83:175 */       resetQueue();
/*  84:176 */       this.m_NewBatch = false;
/*  85:    */     }
/*  86:178 */     Instance newInstance = (Instance)instance.copy();
/*  87:179 */     if ((int)newInstance.value(this.m_AttIndex.getIndex()) == this.m_SecondIndex.getIndex()) {
/*  88:181 */       newInstance.setValue(this.m_AttIndex.getIndex(), this.m_FirstIndex.getIndex());
/*  89:182 */     } else if ((int)newInstance.value(this.m_AttIndex.getIndex()) > this.m_SecondIndex.getIndex()) {
/*  90:184 */       newInstance.setValue(this.m_AttIndex.getIndex(), newInstance.value(this.m_AttIndex.getIndex()) - 1.0D);
/*  91:    */     }
/*  92:187 */     push(newInstance, false);
/*  93:188 */     return true;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public Enumeration<Option> listOptions()
/*  97:    */   {
/*  98:199 */     Vector<Option> newVector = new Vector(3);
/*  99:    */     
/* 100:201 */     newVector.addElement(new Option("\tSets the attribute index (default last).", "C", 1, "-C <col>"));
/* 101:    */     
/* 102:    */ 
/* 103:204 */     newVector.addElement(new Option("\tSets the first value's index (default first).", "F", 1, "-F <value index>"));
/* 104:    */     
/* 105:    */ 
/* 106:    */ 
/* 107:208 */     newVector.addElement(new Option("\tSets the second value's index (default last).", "S", 1, "-S <value index>"));
/* 108:    */     
/* 109:    */ 
/* 110:    */ 
/* 111:212 */     return newVector.elements();
/* 112:    */   }
/* 113:    */   
/* 114:    */   public void setOptions(String[] options)
/* 115:    */     throws Exception
/* 116:    */   {
/* 117:245 */     String attIndex = Utils.getOption('C', options);
/* 118:246 */     if (attIndex.length() != 0) {
/* 119:247 */       setAttributeIndex(attIndex);
/* 120:    */     } else {
/* 121:249 */       setAttributeIndex("last");
/* 122:    */     }
/* 123:252 */     String firstValIndex = Utils.getOption('F', options);
/* 124:253 */     if (firstValIndex.length() != 0) {
/* 125:254 */       setFirstValueIndex(firstValIndex);
/* 126:    */     } else {
/* 127:256 */       setFirstValueIndex("first");
/* 128:    */     }
/* 129:259 */     String secondValIndex = Utils.getOption('S', options);
/* 130:260 */     if (secondValIndex.length() != 0) {
/* 131:261 */       setSecondValueIndex(secondValIndex);
/* 132:    */     } else {
/* 133:263 */       setSecondValueIndex("last");
/* 134:    */     }
/* 135:266 */     if (getInputFormat() != null) {
/* 136:267 */       setInputFormat(getInputFormat());
/* 137:    */     }
/* 138:270 */     Utils.checkForRemainingOptions(options);
/* 139:    */   }
/* 140:    */   
/* 141:    */   public String[] getOptions()
/* 142:    */   {
/* 143:281 */     Vector<String> options = new Vector();
/* 144:    */     
/* 145:283 */     options.add("-C");
/* 146:284 */     options.add("" + getAttributeIndex());
/* 147:285 */     options.add("-F");
/* 148:286 */     options.add("" + getFirstValueIndex());
/* 149:287 */     options.add("-S");
/* 150:288 */     options.add("" + getSecondValueIndex());
/* 151:    */     
/* 152:290 */     return (String[])options.toArray(new String[0]);
/* 153:    */   }
/* 154:    */   
/* 155:    */   public String attributeIndexTipText()
/* 156:    */   {
/* 157:299 */     return "Sets which attribute to process. This attribute must be nominal (\"first\" and \"last\" are valid values)";
/* 158:    */   }
/* 159:    */   
/* 160:    */   public String getAttributeIndex()
/* 161:    */   {
/* 162:310 */     return this.m_AttIndex.getSingleIndex();
/* 163:    */   }
/* 164:    */   
/* 165:    */   public void setAttributeIndex(String attIndex)
/* 166:    */   {
/* 167:320 */     this.m_AttIndex.setSingleIndex(attIndex);
/* 168:    */   }
/* 169:    */   
/* 170:    */   public String firstValueIndexTipText()
/* 171:    */   {
/* 172:329 */     return "Sets the first value to be merged. (\"first\" and \"last\" are valid values)";
/* 173:    */   }
/* 174:    */   
/* 175:    */   public String getFirstValueIndex()
/* 176:    */   {
/* 177:340 */     return this.m_FirstIndex.getSingleIndex();
/* 178:    */   }
/* 179:    */   
/* 180:    */   public void setFirstValueIndex(String firstIndex)
/* 181:    */   {
/* 182:350 */     this.m_FirstIndex.setSingleIndex(firstIndex);
/* 183:    */   }
/* 184:    */   
/* 185:    */   public String secondValueIndexTipText()
/* 186:    */   {
/* 187:359 */     return "Sets the second value to be merged. (\"first\" and \"last\" are valid values)";
/* 188:    */   }
/* 189:    */   
/* 190:    */   public String getSecondValueIndex()
/* 191:    */   {
/* 192:370 */     return this.m_SecondIndex.getSingleIndex();
/* 193:    */   }
/* 194:    */   
/* 195:    */   public void setSecondValueIndex(String secondIndex)
/* 196:    */   {
/* 197:380 */     this.m_SecondIndex.setSingleIndex(secondIndex);
/* 198:    */   }
/* 199:    */   
/* 200:    */   private void setOutputFormat()
/* 201:    */   {
/* 202:392 */     boolean firstEndsWithPrime = false;boolean secondEndsWithPrime = false;
/* 203:393 */     StringBuffer text = new StringBuffer();
/* 204:    */     
/* 205:    */ 
/* 206:    */ 
/* 207:397 */     ArrayList<Attribute> newAtts = new ArrayList(getInputFormat().numAttributes());
/* 208:398 */     for (int j = 0; j < getInputFormat().numAttributes(); j++)
/* 209:    */     {
/* 210:399 */       Attribute att = getInputFormat().attribute(j);
/* 211:400 */       if (j != this.m_AttIndex.getIndex())
/* 212:    */       {
/* 213:401 */         newAtts.add((Attribute)att.copy());
/* 214:    */       }
/* 215:    */       else
/* 216:    */       {
/* 217:406 */         if (att.value(this.m_FirstIndex.getIndex()).endsWith("'")) {
/* 218:407 */           firstEndsWithPrime = true;
/* 219:    */         }
/* 220:409 */         if (att.value(this.m_SecondIndex.getIndex()).endsWith("'")) {
/* 221:410 */           secondEndsWithPrime = true;
/* 222:    */         }
/* 223:412 */         if ((firstEndsWithPrime) || (secondEndsWithPrime)) {
/* 224:413 */           text.append("'");
/* 225:    */         }
/* 226:415 */         if (firstEndsWithPrime) {
/* 227:416 */           text.append(att.value(this.m_FirstIndex.getIndex()).substring(1, att.value(this.m_FirstIndex.getIndex()).length() - 1));
/* 228:    */         } else {
/* 229:419 */           text.append(att.value(this.m_FirstIndex.getIndex()));
/* 230:    */         }
/* 231:421 */         text.append('_');
/* 232:422 */         if (secondEndsWithPrime) {
/* 233:423 */           text.append(att.value(this.m_SecondIndex.getIndex()).substring(1, att.value(this.m_SecondIndex.getIndex()).length() - 1));
/* 234:    */         } else {
/* 235:426 */           text.append(att.value(this.m_SecondIndex.getIndex()));
/* 236:    */         }
/* 237:428 */         if ((firstEndsWithPrime) || (secondEndsWithPrime)) {
/* 238:429 */           text.append("'");
/* 239:    */         }
/* 240:434 */         ArrayList<String> newVals = new ArrayList(att.numValues() - 1);
/* 241:435 */         for (int i = 0; i < att.numValues(); i++) {
/* 242:436 */           if (i == this.m_FirstIndex.getIndex()) {
/* 243:437 */             newVals.add(text.toString());
/* 244:438 */           } else if (i != this.m_SecondIndex.getIndex()) {
/* 245:439 */             newVals.add(att.value(i));
/* 246:    */           }
/* 247:    */         }
/* 248:443 */         Attribute newAtt = new Attribute(att.name(), newVals);
/* 249:444 */         newAtt.setWeight(getInputFormat().attribute(j).weight());
/* 250:    */         
/* 251:446 */         newAtts.add(newAtt);
/* 252:    */       }
/* 253:    */     }
/* 254:452 */     Instances newData = new Instances(getInputFormat().relationName(), newAtts, 0);
/* 255:453 */     newData.setClassIndex(getInputFormat().classIndex());
/* 256:454 */     setOutputFormat(newData);
/* 257:    */   }
/* 258:    */   
/* 259:    */   public String getRevision()
/* 260:    */   {
/* 261:464 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 262:    */   }
/* 263:    */   
/* 264:    */   public static void main(String[] argv)
/* 265:    */   {
/* 266:473 */     runFilter(new MergeTwoValues(), argv);
/* 267:    */   }
/* 268:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.MergeTwoValues
 * JD-Core Version:    0.7.0.1
 */