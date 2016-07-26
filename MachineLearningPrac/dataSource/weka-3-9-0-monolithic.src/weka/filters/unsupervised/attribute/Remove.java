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
/*  13:    */ import weka.core.OptionHandler;
/*  14:    */ import weka.core.Range;
/*  15:    */ import weka.core.RevisionUtils;
/*  16:    */ import weka.core.SparseInstance;
/*  17:    */ import weka.core.Utils;
/*  18:    */ import weka.filters.Filter;
/*  19:    */ import weka.filters.StreamableFilter;
/*  20:    */ import weka.filters.UnsupervisedFilter;
/*  21:    */ 
/*  22:    */ public class Remove
/*  23:    */   extends Filter
/*  24:    */   implements UnsupervisedFilter, StreamableFilter, OptionHandler
/*  25:    */ {
/*  26:    */   static final long serialVersionUID = 5011337331921522847L;
/*  27: 78 */   protected Range m_SelectCols = new Range();
/*  28:    */   protected int[] m_SelectedAttributes;
/*  29:    */   
/*  30:    */   public Remove()
/*  31:    */   {
/*  32: 91 */     this.m_SelectCols.setInvert(true);
/*  33:    */   }
/*  34:    */   
/*  35:    */   public Enumeration<Option> listOptions()
/*  36:    */   {
/*  37:102 */     Vector<Option> newVector = new Vector(2);
/*  38:    */     
/*  39:104 */     newVector.addElement(new Option("\tSpecify list of columns to delete. First and last are valid\n\tindexes. (default none)", "R", 1, "-R <index1,index2-index4,...>"));
/*  40:    */     
/*  41:    */ 
/*  42:    */ 
/*  43:    */ 
/*  44:109 */     newVector.addElement(new Option("\tInvert matching sense (i.e. only keep specified columns)", "V", 0, "-V"));
/*  45:    */     
/*  46:    */ 
/*  47:    */ 
/*  48:113 */     return newVector.elements();
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void setOptions(String[] options)
/*  52:    */     throws Exception
/*  53:    */   {
/*  54:142 */     String deleteList = Utils.getOption('R', options);
/*  55:143 */     if (deleteList.length() != 0) {
/*  56:144 */       setAttributeIndices(deleteList);
/*  57:    */     }
/*  58:146 */     setInvertSelection(Utils.getFlag('V', options));
/*  59:148 */     if (getInputFormat() != null) {
/*  60:149 */       setInputFormat(getInputFormat());
/*  61:    */     }
/*  62:152 */     Utils.checkForRemainingOptions(options);
/*  63:    */   }
/*  64:    */   
/*  65:    */   public String[] getOptions()
/*  66:    */   {
/*  67:163 */     Vector<String> options = new Vector();
/*  68:165 */     if (getInvertSelection()) {
/*  69:166 */       options.add("-V");
/*  70:    */     }
/*  71:168 */     if (!getAttributeIndices().equals(""))
/*  72:    */     {
/*  73:169 */       options.add("-R");
/*  74:170 */       options.add(getAttributeIndices());
/*  75:    */     }
/*  76:173 */     return (String[])options.toArray(new String[0]);
/*  77:    */   }
/*  78:    */   
/*  79:    */   public Capabilities getCapabilities()
/*  80:    */   {
/*  81:184 */     Capabilities result = super.getCapabilities();
/*  82:185 */     result.disableAll();
/*  83:    */     
/*  84:    */ 
/*  85:188 */     result.enableAllAttributes();
/*  86:189 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  87:    */     
/*  88:    */ 
/*  89:192 */     result.enableAllClasses();
/*  90:193 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  91:194 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  92:    */     
/*  93:196 */     return result;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public boolean setInputFormat(Instances instanceInfo)
/*  97:    */     throws Exception
/*  98:    */   {
/*  99:211 */     super.setInputFormat(instanceInfo);
/* 100:    */     
/* 101:213 */     this.m_SelectCols.setUpper(instanceInfo.numAttributes() - 1);
/* 102:    */     
/* 103:    */ 
/* 104:216 */     ArrayList<Attribute> attributes = new ArrayList();
/* 105:217 */     int outputClass = -1;
/* 106:218 */     this.m_SelectedAttributes = this.m_SelectCols.getSelection();
/* 107:219 */     for (int current : this.m_SelectedAttributes)
/* 108:    */     {
/* 109:220 */       if (instanceInfo.classIndex() == current) {
/* 110:221 */         outputClass = attributes.size();
/* 111:    */       }
/* 112:223 */       Attribute keep = (Attribute)instanceInfo.attribute(current).copy();
/* 113:224 */       attributes.add(keep);
/* 114:    */     }
/* 115:227 */     initInputLocators(getInputFormat(), this.m_SelectedAttributes);
/* 116:228 */     Instances outputFormat = new Instances(instanceInfo.relationName(), attributes, 0);
/* 117:    */     
/* 118:230 */     outputFormat.setClassIndex(outputClass);
/* 119:231 */     setOutputFormat(outputFormat);
/* 120:232 */     return true;
/* 121:    */   }
/* 122:    */   
/* 123:    */   public boolean input(Instance instance)
/* 124:    */   {
/* 125:247 */     if (getInputFormat() == null) {
/* 126:248 */       throw new IllegalStateException("No input instance format defined");
/* 127:    */     }
/* 128:250 */     if (this.m_NewBatch)
/* 129:    */     {
/* 130:251 */       resetQueue();
/* 131:252 */       this.m_NewBatch = false;
/* 132:    */     }
/* 133:255 */     if (getOutputFormat().numAttributes() == 0) {
/* 134:256 */       return false;
/* 135:    */     }
/* 136:258 */     double[] vals = new double[getOutputFormat().numAttributes()];
/* 137:259 */     for (int i = 0; i < this.m_SelectedAttributes.length; i++)
/* 138:    */     {
/* 139:260 */       int current = this.m_SelectedAttributes[i];
/* 140:261 */       vals[i] = instance.value(current);
/* 141:    */     }
/* 142:263 */     Instance inst = null;
/* 143:264 */     if ((instance instanceof SparseInstance)) {
/* 144:265 */       inst = new SparseInstance(instance.weight(), vals);
/* 145:    */     } else {
/* 146:267 */       inst = new DenseInstance(instance.weight(), vals);
/* 147:    */     }
/* 148:270 */     copyValues(inst, false, instance.dataset(), outputFormatPeek());
/* 149:    */     
/* 150:272 */     push(inst);
/* 151:273 */     return true;
/* 152:    */   }
/* 153:    */   
/* 154:    */   public String globalInfo()
/* 155:    */   {
/* 156:284 */     return "A filter that removes a range of attributes from the dataset. Will re-order the remaining attributes if invert matching sense is turned on and the attribute column indices are not specified in ascending order.";
/* 157:    */   }
/* 158:    */   
/* 159:    */   public String invertSelectionTipText()
/* 160:    */   {
/* 161:300 */     return "Determines whether action is to select or delete. If set to true, only the specified attributes will be kept; If set to false, specified attributes will be deleted.";
/* 162:    */   }
/* 163:    */   
/* 164:    */   public boolean getInvertSelection()
/* 165:    */   {
/* 166:312 */     return !this.m_SelectCols.getInvert();
/* 167:    */   }
/* 168:    */   
/* 169:    */   public void setInvertSelection(boolean invert)
/* 170:    */   {
/* 171:324 */     this.m_SelectCols.setInvert(!invert);
/* 172:    */   }
/* 173:    */   
/* 174:    */   public String attributeIndicesTipText()
/* 175:    */   {
/* 176:335 */     return "Specify range of attributes to act on. This is a comma separated list of attribute indices, with \"first\" and \"last\" valid values. Specify an inclusive range with \"-\". E.g: \"first-3,5,6-10,last\".";
/* 177:    */   }
/* 178:    */   
/* 179:    */   public String getAttributeIndices()
/* 180:    */   {
/* 181:348 */     return this.m_SelectCols.getRanges();
/* 182:    */   }
/* 183:    */   
/* 184:    */   public void setAttributeIndices(String rangeList)
/* 185:    */   {
/* 186:361 */     this.m_SelectCols.setRanges(rangeList);
/* 187:    */   }
/* 188:    */   
/* 189:    */   public void setAttributeIndicesArray(int[] attributes)
/* 190:    */   {
/* 191:373 */     setAttributeIndices(Range.indicesToRangeList(attributes));
/* 192:    */   }
/* 193:    */   
/* 194:    */   public String getRevision()
/* 195:    */   {
/* 196:383 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 197:    */   }
/* 198:    */   
/* 199:    */   public static void main(String[] argv)
/* 200:    */   {
/* 201:392 */     runFilter(new Remove(), argv);
/* 202:    */   }
/* 203:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.Remove
 * JD-Core Version:    0.7.0.1
 */