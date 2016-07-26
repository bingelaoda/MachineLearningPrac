/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.lang.reflect.Method;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Attribute;
/*   7:    */ import weka.core.Capabilities;
/*   8:    */ import weka.core.Capabilities.Capability;
/*   9:    */ import weka.core.DenseInstance;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.Option;
/*  13:    */ import weka.core.OptionHandler;
/*  14:    */ import weka.core.Range;
/*  15:    */ import weka.core.RevisionUtils;
/*  16:    */ import weka.core.SparseInstance;
/*  17:    */ import weka.core.Utils;
/*  18:    */ import weka.filters.Filter;
/*  19:    */ import weka.filters.StreamableFilter;
/*  20:    */ import weka.filters.UnsupervisedFilter;
/*  21:    */ 
/*  22:    */ public class NumericTransform
/*  23:    */   extends Filter
/*  24:    */   implements UnsupervisedFilter, StreamableFilter, OptionHandler
/*  25:    */ {
/*  26:    */   static final long serialVersionUID = -8561413333351366934L;
/*  27: 88 */   private final Range m_Cols = new Range();
/*  28:    */   private String m_Class;
/*  29:    */   private String m_Method;
/*  30:    */   
/*  31:    */   public String globalInfo()
/*  32:    */   {
/*  33:104 */     return "Transforms numeric attributes using a given transformation method.";
/*  34:    */   }
/*  35:    */   
/*  36:    */   public NumericTransform()
/*  37:    */   {
/*  38:113 */     this.m_Class = "java.lang.Math";
/*  39:114 */     this.m_Method = "abs";
/*  40:    */   }
/*  41:    */   
/*  42:    */   public Capabilities getCapabilities()
/*  43:    */   {
/*  44:125 */     Capabilities result = super.getCapabilities();
/*  45:126 */     result.disableAll();
/*  46:    */     
/*  47:    */ 
/*  48:129 */     result.enableAllAttributes();
/*  49:130 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  50:    */     
/*  51:    */ 
/*  52:133 */     result.enableAllClasses();
/*  53:134 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  54:135 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  55:    */     
/*  56:137 */     return result;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public boolean setInputFormat(Instances instanceInfo)
/*  60:    */     throws Exception
/*  61:    */   {
/*  62:152 */     if (this.m_Class == null) {
/*  63:153 */       throw new IllegalStateException("No class has been set.");
/*  64:    */     }
/*  65:155 */     if (this.m_Method == null) {
/*  66:156 */       throw new IllegalStateException("No method has been set.");
/*  67:    */     }
/*  68:158 */     super.setInputFormat(instanceInfo);
/*  69:159 */     this.m_Cols.setUpper(instanceInfo.numAttributes() - 1);
/*  70:160 */     setOutputFormat(instanceInfo);
/*  71:161 */     return true;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public boolean input(Instance instance)
/*  75:    */     throws Exception
/*  76:    */   {
/*  77:177 */     if (getInputFormat() == null) {
/*  78:178 */       throw new IllegalStateException("No input instance format defined");
/*  79:    */     }
/*  80:180 */     if (this.m_NewBatch)
/*  81:    */     {
/*  82:181 */       resetQueue();
/*  83:182 */       this.m_NewBatch = false;
/*  84:    */     }
/*  85:185 */     Method m = Class.forName(this.m_Class).getMethod(this.m_Method, new Class[] { Double.TYPE });
/*  86:    */     
/*  87:    */ 
/*  88:188 */     double[] vals = new double[instance.numAttributes()];
/*  89:189 */     Double[] params = new Double[1];
/*  90:191 */     for (int i = 0; i < instance.numAttributes(); i++) {
/*  91:192 */       if (instance.isMissing(i))
/*  92:    */       {
/*  93:193 */         vals[i] = Utils.missingValue();
/*  94:    */       }
/*  95:195 */       else if ((this.m_Cols.isInRange(i)) && (instance.attribute(i).isNumeric()))
/*  96:    */       {
/*  97:196 */         params[0] = new Double(instance.value(i));
/*  98:197 */         Double newVal = (Double)m.invoke(null, (Object[])params);
/*  99:198 */         if ((newVal.isNaN()) || (newVal.isInfinite())) {
/* 100:199 */           vals[i] = Utils.missingValue();
/* 101:    */         } else {
/* 102:201 */           vals[i] = newVal.doubleValue();
/* 103:    */         }
/* 104:    */       }
/* 105:    */       else
/* 106:    */       {
/* 107:204 */         vals[i] = instance.value(i);
/* 108:    */       }
/* 109:    */     }
/* 110:208 */     Instance inst = null;
/* 111:209 */     if ((instance instanceof SparseInstance)) {
/* 112:210 */       inst = new SparseInstance(instance.weight(), vals);
/* 113:    */     } else {
/* 114:212 */       inst = new DenseInstance(instance.weight(), vals);
/* 115:    */     }
/* 116:214 */     inst.setDataset(instance.dataset());
/* 117:215 */     push(inst, false);
/* 118:216 */     return true;
/* 119:    */   }
/* 120:    */   
/* 121:    */   public Enumeration<Option> listOptions()
/* 122:    */   {
/* 123:227 */     Vector<Option> newVector = new Vector(4);
/* 124:    */     
/* 125:229 */     newVector.addElement(new Option("\tSpecify list of columns to transform. First and last are\n\tvalid indexes (default none). Non-numeric columns are \n\tskipped.", "R", 1, "-R <index1,index2-index4,...>"));
/* 126:    */     
/* 127:    */ 
/* 128:    */ 
/* 129:    */ 
/* 130:234 */     newVector.addElement(new Option("\tInvert matching sense.", "V", 0, "-V"));
/* 131:    */     
/* 132:236 */     newVector.addElement(new Option("\tSets the class containing transformation method.\n\t(default java.lang.Math)", "C", 1, "-C <string>"));
/* 133:    */     
/* 134:    */ 
/* 135:    */ 
/* 136:240 */     newVector.addElement(new Option("\tSets the method. (default abs)", "M", 1, "-M <string>"));
/* 137:    */     
/* 138:    */ 
/* 139:243 */     return newVector.elements();
/* 140:    */   }
/* 141:    */   
/* 142:    */   public void setOptions(String[] options)
/* 143:    */     throws Exception
/* 144:    */   {
/* 145:284 */     setAttributeIndices(Utils.getOption('R', options));
/* 146:285 */     setInvertSelection(Utils.getFlag('V', options));
/* 147:286 */     String classString = Utils.getOption('C', options);
/* 148:287 */     if (classString.length() != 0) {
/* 149:288 */       setClassName(classString);
/* 150:    */     }
/* 151:290 */     String methodString = Utils.getOption('M', options);
/* 152:291 */     if (methodString.length() != 0) {
/* 153:292 */       setMethodName(methodString);
/* 154:    */     }
/* 155:295 */     if (getInputFormat() != null) {
/* 156:296 */       setInputFormat(getInputFormat());
/* 157:    */     }
/* 158:299 */     Utils.checkForRemainingOptions(options);
/* 159:    */   }
/* 160:    */   
/* 161:    */   public String[] getOptions()
/* 162:    */   {
/* 163:310 */     Vector<String> options = new Vector();
/* 164:312 */     if (getInvertSelection()) {
/* 165:313 */       options.add("-V");
/* 166:    */     }
/* 167:315 */     if (!getAttributeIndices().equals(""))
/* 168:    */     {
/* 169:316 */       options.add("-R");
/* 170:317 */       options.add(getAttributeIndices());
/* 171:    */     }
/* 172:319 */     if (this.m_Class != null)
/* 173:    */     {
/* 174:320 */       options.add("-C");
/* 175:321 */       options.add(getClassName());
/* 176:    */     }
/* 177:323 */     if (this.m_Method != null)
/* 178:    */     {
/* 179:324 */       options.add("-M");
/* 180:325 */       options.add(getMethodName());
/* 181:    */     }
/* 182:328 */     return (String[])options.toArray(new String[0]);
/* 183:    */   }
/* 184:    */   
/* 185:    */   public String classNameTipText()
/* 186:    */   {
/* 187:338 */     return "Name of the class containing the method used for the transformation.";
/* 188:    */   }
/* 189:    */   
/* 190:    */   public String getClassName()
/* 191:    */   {
/* 192:348 */     return this.m_Class;
/* 193:    */   }
/* 194:    */   
/* 195:    */   public void setClassName(String name)
/* 196:    */     throws ClassNotFoundException
/* 197:    */   {
/* 198:359 */     this.m_Class = name;
/* 199:    */   }
/* 200:    */   
/* 201:    */   public String methodNameTipText()
/* 202:    */   {
/* 203:369 */     return "Name of the method used for the transformation.";
/* 204:    */   }
/* 205:    */   
/* 206:    */   public String getMethodName()
/* 207:    */   {
/* 208:379 */     return this.m_Method;
/* 209:    */   }
/* 210:    */   
/* 211:    */   public void setMethodName(String name)
/* 212:    */     throws NoSuchMethodException
/* 213:    */   {
/* 214:390 */     this.m_Method = name;
/* 215:    */   }
/* 216:    */   
/* 217:    */   public String invertSelectionTipText()
/* 218:    */   {
/* 219:400 */     return "Whether to process the inverse of the given attribute ranges.";
/* 220:    */   }
/* 221:    */   
/* 222:    */   public boolean getInvertSelection()
/* 223:    */   {
/* 224:410 */     return this.m_Cols.getInvert();
/* 225:    */   }
/* 226:    */   
/* 227:    */   public void setInvertSelection(boolean invert)
/* 228:    */   {
/* 229:420 */     this.m_Cols.setInvert(invert);
/* 230:    */   }
/* 231:    */   
/* 232:    */   public String attributeIndicesTipText()
/* 233:    */   {
/* 234:430 */     return "Specify range of attributes to act on. This is a comma separated list of attribute indices, with \"first\" and \"last\" valid values. Specify an inclusive range with \"-\". E.g: \"first-3,5,6-10,last\".";
/* 235:    */   }
/* 236:    */   
/* 237:    */   public String getAttributeIndices()
/* 238:    */   {
/* 239:443 */     return this.m_Cols.getRanges();
/* 240:    */   }
/* 241:    */   
/* 242:    */   public void setAttributeIndices(String rangeList)
/* 243:    */   {
/* 244:458 */     this.m_Cols.setRanges(rangeList);
/* 245:    */   }
/* 246:    */   
/* 247:    */   public void setAttributeIndicesArray(int[] attributes)
/* 248:    */   {
/* 249:471 */     setAttributeIndices(Range.indicesToRangeList(attributes));
/* 250:    */   }
/* 251:    */   
/* 252:    */   public String getRevision()
/* 253:    */   {
/* 254:481 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 255:    */   }
/* 256:    */   
/* 257:    */   public static void main(String[] argv)
/* 258:    */   {
/* 259:490 */     runFilter(new NumericTransform(), argv);
/* 260:    */   }
/* 261:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.NumericTransform
 * JD-Core Version:    0.7.0.1
 */