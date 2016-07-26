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
/*  17:    */ import weka.core.UnsupportedAttributeTypeException;
/*  18:    */ import weka.core.Utils;
/*  19:    */ import weka.filters.Filter;
/*  20:    */ import weka.filters.StreamableFilter;
/*  21:    */ import weka.filters.UnsupervisedFilter;
/*  22:    */ 
/*  23:    */ public class FirstOrder
/*  24:    */   extends Filter
/*  25:    */   implements UnsupervisedFilter, StreamableFilter, OptionHandler
/*  26:    */ {
/*  27:    */   static final long serialVersionUID = -7500464545400454179L;
/*  28: 87 */   protected Range m_DeltaCols = new Range();
/*  29:    */   
/*  30:    */   public String globalInfo()
/*  31:    */   {
/*  32: 97 */     return "This instance filter takes a range of N numeric attributes and replaces them with N-1 numeric attributes, the values of which are the difference between consecutive attribute values from the original instance. eg: \n\nOriginal attribute values\n\n   0.1, 0.2, 0.3, 0.1, 0.3\n\nNew attribute values\n\n   0.1, 0.1, -0.2, 0.2\n\nThe range of attributes used is taken in numeric order. That is, a range spec of 7-11,3-5 will use the attribute ordering 3,4,5,7,8,9,10,11 for the differences, NOT 7,8,9,10,11,3,4,5.";
/*  33:    */   }
/*  34:    */   
/*  35:    */   public Enumeration<Option> listOptions()
/*  36:    */   {
/*  37:117 */     Vector<Option> newVector = new Vector(1);
/*  38:    */     
/*  39:119 */     newVector.addElement(new Option("\tSpecify list of columns to take the differences between.\n\tFirst and last are valid indexes.\n\t(default none)", "R", 1, "-R <index1,index2-index4,...>"));
/*  40:    */     
/*  41:    */ 
/*  42:    */ 
/*  43:    */ 
/*  44:124 */     return newVector.elements();
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void setOptions(String[] options)
/*  48:    */     throws Exception
/*  49:    */   {
/*  50:149 */     String deltaList = Utils.getOption('R', options);
/*  51:150 */     if (deltaList.length() != 0) {
/*  52:151 */       setAttributeIndices(deltaList);
/*  53:    */     } else {
/*  54:153 */       setAttributeIndices("");
/*  55:    */     }
/*  56:156 */     if (getInputFormat() != null) {
/*  57:157 */       setInputFormat(getInputFormat());
/*  58:    */     }
/*  59:160 */     Utils.checkForRemainingOptions(options);
/*  60:    */   }
/*  61:    */   
/*  62:    */   public String[] getOptions()
/*  63:    */   {
/*  64:171 */     Vector<String> options = new Vector();
/*  65:173 */     if (!getAttributeIndices().equals(""))
/*  66:    */     {
/*  67:174 */       options.add("-R");
/*  68:175 */       options.add(getAttributeIndices());
/*  69:    */     }
/*  70:178 */     return (String[])options.toArray(new String[0]);
/*  71:    */   }
/*  72:    */   
/*  73:    */   public Capabilities getCapabilities()
/*  74:    */   {
/*  75:189 */     Capabilities result = super.getCapabilities();
/*  76:190 */     result.disableAll();
/*  77:    */     
/*  78:    */ 
/*  79:193 */     result.enableAllAttributes();
/*  80:194 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  81:    */     
/*  82:    */ 
/*  83:197 */     result.enableAllClasses();
/*  84:198 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  85:199 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  86:    */     
/*  87:201 */     return result;
/*  88:    */   }
/*  89:    */   
/*  90:    */   public boolean setInputFormat(Instances instanceInfo)
/*  91:    */     throws Exception
/*  92:    */   {
/*  93:218 */     super.setInputFormat(instanceInfo);
/*  94:    */     
/*  95:220 */     this.m_DeltaCols.setUpper(getInputFormat().numAttributes() - 1);
/*  96:221 */     int selectedCount = 0;
/*  97:222 */     for (int i = getInputFormat().numAttributes() - 1; i >= 0; i--) {
/*  98:223 */       if (this.m_DeltaCols.isInRange(i))
/*  99:    */       {
/* 100:224 */         selectedCount++;
/* 101:225 */         if (!getInputFormat().attribute(i).isNumeric()) {
/* 102:226 */           throw new UnsupportedAttributeTypeException("Selected attributes must be all numeric");
/* 103:    */         }
/* 104:    */       }
/* 105:    */     }
/* 106:231 */     if (selectedCount == 1) {
/* 107:232 */       throw new Exception("Cannot select only one attribute.");
/* 108:    */     }
/* 109:236 */     ArrayList<Attribute> newAtts = new ArrayList();
/* 110:237 */     boolean inRange = false;
/* 111:238 */     String foName = null;
/* 112:239 */     int clsIndex = -1;
/* 113:240 */     for (int i = 0; i < instanceInfo.numAttributes(); i++) {
/* 114:241 */       if ((this.m_DeltaCols.isInRange(i)) && (i != instanceInfo.classIndex()))
/* 115:    */       {
/* 116:242 */         if (inRange)
/* 117:    */         {
/* 118:243 */           Attribute newAttrib = new Attribute(foName);
/* 119:244 */           newAtts.add(newAttrib);
/* 120:    */         }
/* 121:246 */         foName = instanceInfo.attribute(i).name();
/* 122:247 */         foName = "'FO " + foName.replace('\'', ' ').trim() + '\'';
/* 123:248 */         inRange = true;
/* 124:    */       }
/* 125:    */       else
/* 126:    */       {
/* 127:250 */         newAtts.add((Attribute)instanceInfo.attribute(i).copy());
/* 128:251 */         if (i == instanceInfo.classIndex()) {
/* 129:252 */           clsIndex = newAtts.size() - 1;
/* 130:    */         }
/* 131:    */       }
/* 132:    */     }
/* 133:256 */     Instances data = new Instances(instanceInfo.relationName(), newAtts, 0);
/* 134:257 */     data.setClassIndex(clsIndex);
/* 135:258 */     setOutputFormat(data);
/* 136:259 */     return true;
/* 137:    */   }
/* 138:    */   
/* 139:    */   public boolean input(Instance instance)
/* 140:    */   {
/* 141:274 */     if (getInputFormat() == null) {
/* 142:275 */       throw new IllegalStateException("No input instance format defined");
/* 143:    */     }
/* 144:277 */     if (this.m_NewBatch)
/* 145:    */     {
/* 146:278 */       resetQueue();
/* 147:279 */       this.m_NewBatch = false;
/* 148:    */     }
/* 149:282 */     Instances outputFormat = outputFormatPeek();
/* 150:283 */     double[] vals = new double[outputFormat.numAttributes()];
/* 151:284 */     boolean inRange = false;
/* 152:285 */     double lastVal = Utils.missingValue();
/* 153:    */     
/* 154:287 */     int i = 0;
/* 155:287 */     for (int j = 0; j < outputFormat.numAttributes(); i++) {
/* 156:288 */       if ((this.m_DeltaCols.isInRange(i)) && (i != instance.classIndex()))
/* 157:    */       {
/* 158:289 */         if (inRange)
/* 159:    */         {
/* 160:290 */           if ((Utils.isMissingValue(lastVal)) || (instance.isMissing(i))) {
/* 161:291 */             vals[(j++)] = Utils.missingValue();
/* 162:    */           } else {
/* 163:293 */             vals[(j++)] = (instance.value(i) - lastVal);
/* 164:    */           }
/* 165:    */         }
/* 166:    */         else {
/* 167:296 */           inRange = true;
/* 168:    */         }
/* 169:298 */         lastVal = instance.value(i);
/* 170:    */       }
/* 171:    */       else
/* 172:    */       {
/* 173:300 */         vals[(j++)] = instance.value(i);
/* 174:    */       }
/* 175:    */     }
/* 176:304 */     Instance inst = null;
/* 177:305 */     if ((instance instanceof SparseInstance)) {
/* 178:306 */       inst = new SparseInstance(instance.weight(), vals);
/* 179:    */     } else {
/* 180:308 */       inst = new DenseInstance(instance.weight(), vals);
/* 181:    */     }
/* 182:311 */     copyValues(inst, false, instance.dataset(), outputFormatPeek());
/* 183:    */     
/* 184:313 */     push(inst);
/* 185:314 */     return true;
/* 186:    */   }
/* 187:    */   
/* 188:    */   public String attributeIndicesTipText()
/* 189:    */   {
/* 190:324 */     return "Specify range of attributes to act on. This is a comma separated list of attribute indices, with \"first\" and \"last\" valid values. Specify an inclusive range with \"-\". E.g: \"first-3,5,6-10,last\".";
/* 191:    */   }
/* 192:    */   
/* 193:    */   public String getAttributeIndices()
/* 194:    */   {
/* 195:337 */     return this.m_DeltaCols.getRanges();
/* 196:    */   }
/* 197:    */   
/* 198:    */   public void setAttributeIndices(String rangeList)
/* 199:    */     throws Exception
/* 200:    */   {
/* 201:351 */     this.m_DeltaCols.setRanges(rangeList);
/* 202:    */   }
/* 203:    */   
/* 204:    */   public void setAttributeIndicesArray(int[] attributes)
/* 205:    */     throws Exception
/* 206:    */   {
/* 207:364 */     setAttributeIndices(Range.indicesToRangeList(attributes));
/* 208:    */   }
/* 209:    */   
/* 210:    */   public String getRevision()
/* 211:    */   {
/* 212:374 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 213:    */   }
/* 214:    */   
/* 215:    */   public static void main(String[] argv)
/* 216:    */   {
/* 217:383 */     runFilter(new FirstOrder(), argv);
/* 218:    */   }
/* 219:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.FirstOrder
 * JD-Core Version:    0.7.0.1
 */