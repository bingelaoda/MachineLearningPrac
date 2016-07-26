/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import java.util.Vector;
/*   5:    */ import weka.core.Attribute;
/*   6:    */ import weka.core.Capabilities;
/*   7:    */ import weka.core.Capabilities.Capability;
/*   8:    */ import weka.core.DenseInstance;
/*   9:    */ import weka.core.Instance;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.Option;
/*  12:    */ import weka.core.OptionHandler;
/*  13:    */ import weka.core.Range;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.SparseInstance;
/*  16:    */ import weka.core.Utils;
/*  17:    */ import weka.filters.Filter;
/*  18:    */ import weka.filters.StreamableFilter;
/*  19:    */ import weka.filters.UnsupervisedFilter;
/*  20:    */ 
/*  21:    */ public class Copy
/*  22:    */   extends Filter
/*  23:    */   implements UnsupervisedFilter, StreamableFilter, OptionHandler
/*  24:    */ {
/*  25:    */   static final long serialVersionUID = -8543707493627441566L;
/*  26: 78 */   protected Range m_CopyCols = new Range();
/*  27:    */   protected int[] m_SelectedAttributes;
/*  28:    */   
/*  29:    */   public Enumeration<Option> listOptions()
/*  30:    */   {
/*  31: 94 */     Vector<Option> newVector = new Vector(2);
/*  32:    */     
/*  33: 96 */     newVector.addElement(new Option("\tSpecify list of columns to copy. First and last are valid\n\tindexes. (default none)", "R", 1, "-R <index1,index2-index4,...>"));
/*  34:    */     
/*  35:    */ 
/*  36:    */ 
/*  37:    */ 
/*  38:101 */     newVector.addElement(new Option("\tInvert matching sense (i.e. copy all non-specified columns)", "V", 0, "-V"));
/*  39:    */     
/*  40:    */ 
/*  41:    */ 
/*  42:105 */     return newVector.elements();
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void setOptions(String[] options)
/*  46:    */     throws Exception
/*  47:    */   {
/*  48:134 */     String copyList = Utils.getOption('R', options);
/*  49:135 */     if (copyList.length() != 0) {
/*  50:136 */       setAttributeIndices(copyList);
/*  51:    */     }
/*  52:138 */     setInvertSelection(Utils.getFlag('V', options));
/*  53:140 */     if (getInputFormat() != null) {
/*  54:141 */       setInputFormat(getInputFormat());
/*  55:    */     }
/*  56:    */   }
/*  57:    */   
/*  58:    */   public String[] getOptions()
/*  59:    */   {
/*  60:153 */     Vector<String> options = new Vector();
/*  61:155 */     if (getInvertSelection()) {
/*  62:156 */       options.add("-V");
/*  63:    */     }
/*  64:158 */     if (!getAttributeIndices().equals(""))
/*  65:    */     {
/*  66:159 */       options.add("-R");
/*  67:160 */       options.add(getAttributeIndices());
/*  68:    */     }
/*  69:163 */     return (String[])options.toArray(new String[0]);
/*  70:    */   }
/*  71:    */   
/*  72:    */   public Capabilities getCapabilities()
/*  73:    */   {
/*  74:174 */     Capabilities result = super.getCapabilities();
/*  75:175 */     result.disableAll();
/*  76:    */     
/*  77:    */ 
/*  78:178 */     result.enableAllAttributes();
/*  79:179 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  80:    */     
/*  81:    */ 
/*  82:182 */     result.enableAllClasses();
/*  83:183 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  84:184 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  85:    */     
/*  86:186 */     return result;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public boolean setInputFormat(Instances instanceInfo)
/*  90:    */     throws Exception
/*  91:    */   {
/*  92:201 */     super.setInputFormat(instanceInfo);
/*  93:    */     
/*  94:203 */     this.m_CopyCols.setUpper(instanceInfo.numAttributes() - 1);
/*  95:    */     
/*  96:    */ 
/*  97:206 */     Instances outputFormat = new Instances(instanceInfo, 0);
/*  98:207 */     this.m_SelectedAttributes = this.m_CopyCols.getSelection();
/*  99:208 */     for (int current : this.m_SelectedAttributes)
/* 100:    */     {
/* 101:210 */       Attribute origAttribute = instanceInfo.attribute(current);
/* 102:211 */       outputFormat.insertAttributeAt(origAttribute.copy("Copy of " + origAttribute.name()), outputFormat.numAttributes());
/* 103:    */     }
/* 104:218 */     int[] newIndices = new int[instanceInfo.numAttributes() + this.m_SelectedAttributes.length];
/* 105:220 */     for (int i = 0; i < instanceInfo.numAttributes(); i++) {
/* 106:221 */       newIndices[i] = i;
/* 107:    */     }
/* 108:223 */     for (int i = 0; i < this.m_SelectedAttributes.length; i++) {
/* 109:224 */       newIndices[(instanceInfo.numAttributes() + i)] = this.m_SelectedAttributes[i];
/* 110:    */     }
/* 111:226 */     initInputLocators(instanceInfo, newIndices);
/* 112:    */     
/* 113:228 */     setOutputFormat(outputFormat);
/* 114:    */     
/* 115:230 */     return true;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public boolean input(Instance instance)
/* 119:    */   {
/* 120:245 */     if (getInputFormat() == null) {
/* 121:246 */       throw new IllegalStateException("No input instance format defined");
/* 122:    */     }
/* 123:248 */     if (this.m_NewBatch)
/* 124:    */     {
/* 125:249 */       resetQueue();
/* 126:250 */       this.m_NewBatch = false;
/* 127:    */     }
/* 128:253 */     double[] vals = new double[outputFormatPeek().numAttributes()];
/* 129:254 */     for (int i = 0; i < getInputFormat().numAttributes(); i++) {
/* 130:255 */       vals[i] = instance.value(i);
/* 131:    */     }
/* 132:257 */     int j = getInputFormat().numAttributes();
/* 133:258 */     for (int i = 0; i < this.m_SelectedAttributes.length; i++)
/* 134:    */     {
/* 135:259 */       int current = this.m_SelectedAttributes[i];
/* 136:260 */       vals[(i + j)] = instance.value(current);
/* 137:    */     }
/* 138:262 */     Instance inst = null;
/* 139:263 */     if ((instance instanceof SparseInstance)) {
/* 140:264 */       inst = new SparseInstance(instance.weight(), vals);
/* 141:    */     } else {
/* 142:266 */       inst = new DenseInstance(instance.weight(), vals);
/* 143:    */     }
/* 144:269 */     copyValues(inst, false, instance.dataset(), outputFormatPeek());
/* 145:    */     
/* 146:271 */     push(inst);
/* 147:272 */     return true;
/* 148:    */   }
/* 149:    */   
/* 150:    */   public String globalInfo()
/* 151:    */   {
/* 152:283 */     return "An instance filter that copies a range of attributes in the dataset. This is used in conjunction with other filters that overwrite attribute values during the course of their operation -- this filter allows the original attributes to be kept as well as the new attributes.";
/* 153:    */   }
/* 154:    */   
/* 155:    */   public String invertSelectionTipText()
/* 156:    */   {
/* 157:297 */     return "Sets copy selected vs unselected action. If set to false, only the specified attributes will be copied; If set to true, non-specified attributes will be copied.";
/* 158:    */   }
/* 159:    */   
/* 160:    */   public boolean getInvertSelection()
/* 161:    */   {
/* 162:309 */     return this.m_CopyCols.getInvert();
/* 163:    */   }
/* 164:    */   
/* 165:    */   public void setInvertSelection(boolean invert)
/* 166:    */   {
/* 167:324 */     this.m_CopyCols.setInvert(invert);
/* 168:    */   }
/* 169:    */   
/* 170:    */   public String getAttributeIndices()
/* 171:    */   {
/* 172:334 */     return this.m_CopyCols.getRanges();
/* 173:    */   }
/* 174:    */   
/* 175:    */   public String attributeIndicesTipText()
/* 176:    */   {
/* 177:344 */     return "Specify range of attributes to act on. This is a comma separated list of attribute indices, with \"first\" and \"last\" valid values. Specify an inclusive range with \"-\". E.g: \"first-3,5,6-10,last\".";
/* 178:    */   }
/* 179:    */   
/* 180:    */   public void setAttributeIndices(String rangeList)
/* 181:    */     throws Exception
/* 182:    */   {
/* 183:364 */     this.m_CopyCols.setRanges(rangeList);
/* 184:    */   }
/* 185:    */   
/* 186:    */   public void setAttributeIndicesArray(int[] attributes)
/* 187:    */     throws Exception
/* 188:    */   {
/* 189:380 */     setAttributeIndices(Range.indicesToRangeList(attributes));
/* 190:    */   }
/* 191:    */   
/* 192:    */   public String getRevision()
/* 193:    */   {
/* 194:390 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 195:    */   }
/* 196:    */   
/* 197:    */   public static void main(String[] argv)
/* 198:    */   {
/* 199:399 */     runFilter(new Copy(), argv);
/* 200:    */   }
/* 201:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.Copy
 * JD-Core Version:    0.7.0.1
 */