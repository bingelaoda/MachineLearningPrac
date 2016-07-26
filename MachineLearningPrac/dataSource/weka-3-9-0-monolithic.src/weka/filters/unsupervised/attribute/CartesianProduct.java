/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.Capabilities;
/*   9:    */ import weka.core.Capabilities.Capability;
/*  10:    */ import weka.core.DenseInstance;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.Option;
/*  14:    */ import weka.core.Range;
/*  15:    */ import weka.core.RevisionUtils;
/*  16:    */ import weka.core.SparseInstance;
/*  17:    */ import weka.core.Utils;
/*  18:    */ import weka.filters.SimpleBatchFilter;
/*  19:    */ 
/*  20:    */ public class CartesianProduct
/*  21:    */   extends SimpleBatchFilter
/*  22:    */ {
/*  23:    */   private static final long serialVersionUID = -227979753639722020L;
/*  24: 64 */   protected Range m_Attributes = new Range("");
/*  25:    */   
/*  26:    */   public String globalInfo()
/*  27:    */   {
/*  28: 74 */     return "A filter for performing the Cartesian product of a set of nominal attributes.";
/*  29:    */   }
/*  30:    */   
/*  31:    */   public Enumeration<Option> listOptions()
/*  32:    */   {
/*  33: 85 */     Vector<Option> result = new Vector();
/*  34:    */     
/*  35: 87 */     result.addElement(new Option("\tSpecifies list of nominal attributes to use to form the product.\n\t(default none)", "R", 1, "-R <col1,col2-col4,...>"));
/*  36:    */     
/*  37:    */ 
/*  38:    */ 
/*  39: 91 */     result.addAll(Collections.list(super.listOptions()));
/*  40:    */     
/*  41: 93 */     return result.elements();
/*  42:    */   }
/*  43:    */   
/*  44:    */   public void setOptions(String[] options)
/*  45:    */     throws Exception
/*  46:    */   {
/*  47:123 */     String tmpStr = Utils.getOption("R", options);
/*  48:124 */     if (tmpStr.length() != 0) {
/*  49:125 */       setAttributeIndices(tmpStr);
/*  50:    */     } else {
/*  51:127 */       setAttributeIndices("");
/*  52:    */     }
/*  53:130 */     super.setOptions(options);
/*  54:    */     
/*  55:132 */     Utils.checkForRemainingOptions(options);
/*  56:    */   }
/*  57:    */   
/*  58:    */   public String[] getOptions()
/*  59:    */   {
/*  60:143 */     Vector<String> result = new Vector();
/*  61:145 */     if (!getAttributeIndices().equals(""))
/*  62:    */     {
/*  63:146 */       result.add("-R");
/*  64:147 */       result.add(getAttributeIndices());
/*  65:    */     }
/*  66:150 */     Collections.addAll(result, super.getOptions());
/*  67:    */     
/*  68:152 */     return (String[])result.toArray(new String[result.size()]);
/*  69:    */   }
/*  70:    */   
/*  71:    */   public String attributeIndicesTipText()
/*  72:    */   {
/*  73:162 */     return "Specify range of attributes to act on;  this is a comma separated list of attribute indices, with \"first\" and \"last\" valid values; specify an inclusive range with \"-\", eg: \"first-3,5,6-10,last\".";
/*  74:    */   }
/*  75:    */   
/*  76:    */   public String getAttributeIndices()
/*  77:    */   {
/*  78:174 */     return this.m_Attributes.getRanges();
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void setAttributeIndices(String value)
/*  82:    */   {
/*  83:188 */     this.m_Attributes.setRanges(value);
/*  84:    */   }
/*  85:    */   
/*  86:    */   public void setAttributeIndicesArray(int[] value)
/*  87:    */   {
/*  88:200 */     setAttributeIndices(Range.indicesToRangeList(value));
/*  89:    */   }
/*  90:    */   
/*  91:    */   public Capabilities getCapabilities()
/*  92:    */   {
/*  93:211 */     Capabilities result = super.getCapabilities();
/*  94:212 */     result.disableAll();
/*  95:    */     
/*  96:    */ 
/*  97:215 */     result.enableAllAttributes();
/*  98:    */     
/*  99:    */ 
/* 100:218 */     result.enableAllClasses();
/* 101:219 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 102:220 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 103:    */     
/* 104:222 */     return result;
/* 105:    */   }
/* 106:    */   
/* 107:    */   protected Instances determineOutputFormat(Instances inputFormat)
/* 108:    */     throws Exception
/* 109:    */   {
/* 110:242 */     this.m_Attributes.setUpper(inputFormat.numAttributes() - 1);
/* 111:    */     
/* 112:244 */     ArrayList<Attribute> atts = new ArrayList(inputFormat.numAttributes() + 1);
/* 113:245 */     ArrayList<String> values = new ArrayList();
/* 114:    */     
/* 115:247 */     String name = "";
/* 116:248 */     for (int i = 0; i < inputFormat.numAttributes(); i++)
/* 117:    */     {
/* 118:249 */       atts.add(inputFormat.attribute(i));
/* 119:250 */       if ((inputFormat.attribute(i).isNominal()) && (this.m_Attributes.isInRange(i)) && (i != inputFormat.classIndex())) {
/* 120:251 */         if (values.size() == 0)
/* 121:    */         {
/* 122:252 */           values = new ArrayList(inputFormat.attribute(i).numValues());
/* 123:253 */           for (int j = 0; j < inputFormat.attribute(i).numValues(); j++) {
/* 124:254 */             values.add(inputFormat.attribute(i).value(j));
/* 125:    */           }
/* 126:256 */           name = inputFormat.attribute(i).name();
/* 127:    */         }
/* 128:    */         else
/* 129:    */         {
/* 130:258 */           ArrayList<String> newValues = new ArrayList(values.size() * inputFormat.attribute(i).numValues());
/* 131:259 */           for (String value : values) {
/* 132:260 */             for (int j = 0; j < inputFormat.attribute(i).numValues(); j++) {
/* 133:261 */               newValues.add(value + "_x_" + inputFormat.attribute(i).value(j));
/* 134:    */             }
/* 135:    */           }
/* 136:264 */           name = name + "_x_" + inputFormat.attribute(i).name();
/* 137:265 */           values = newValues;
/* 138:    */         }
/* 139:    */       }
/* 140:    */     }
/* 141:269 */     if (values.size() > 0) {
/* 142:270 */       atts.add(new Attribute(name, values));
/* 143:    */     }
/* 144:274 */     Instances result = new Instances(inputFormat.relationName(), atts, 0);
/* 145:275 */     result.setClassIndex(inputFormat.classIndex());
/* 146:    */     
/* 147:277 */     return result;
/* 148:    */   }
/* 149:    */   
/* 150:    */   protected Instances process(Instances instances)
/* 151:    */     throws Exception
/* 152:    */   {
/* 153:294 */     Instances result = getOutputFormat();
/* 154:296 */     for (Instance inst : instances) {
/* 155:297 */       if (instances.numAttributes() < result.numAttributes())
/* 156:    */       {
/* 157:298 */         double[] newVals = new double[result.numAttributes()];
/* 158:299 */         for (int i = 0; i < inst.numValues(); i++) {
/* 159:300 */           newVals[inst.index(i)] = inst.valueSparse(i);
/* 160:    */         }
/* 161:302 */         String value = "";
/* 162:303 */         for (int i = 0; i < inst.numAttributes(); i++) {
/* 163:304 */           if ((instances.attribute(i).isNominal()) && (this.m_Attributes.isInRange(i)) && (i != instances.classIndex()))
/* 164:    */           {
/* 165:305 */             if (Utils.isMissingValue(newVals[i]))
/* 166:    */             {
/* 167:306 */               value = null;
/* 168:307 */               break;
/* 169:    */             }
/* 170:309 */             value = value + (value.length() > 0 ? "_x_" + instances.attribute(i).value((int)newVals[i]) : instances.attribute(i).value((int)newVals[i]));
/* 171:    */           }
/* 172:    */         }
/* 173:314 */         if (value == null) {
/* 174:315 */           newVals[(newVals.length - 1)] = (0.0D / 0.0D);
/* 175:    */         } else {
/* 176:317 */           newVals[(newVals.length - 1)] = result.attribute(result.numAttributes() - 1).indexOfValue(value);
/* 177:    */         }
/* 178:319 */         if ((inst instanceof DenseInstance)) {
/* 179:320 */           result.add(new DenseInstance(inst.weight(), newVals));
/* 180:    */         } else {
/* 181:322 */           result.add(new SparseInstance(inst.weight(), newVals));
/* 182:    */         }
/* 183:    */       }
/* 184:    */       else
/* 185:    */       {
/* 186:325 */         result.add(inst);
/* 187:    */       }
/* 188:    */     }
/* 189:329 */     return result;
/* 190:    */   }
/* 191:    */   
/* 192:    */   public String getRevision()
/* 193:    */   {
/* 194:339 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 195:    */   }
/* 196:    */   
/* 197:    */   public static void main(String[] args)
/* 198:    */   {
/* 199:348 */     runFilter(new CartesianProduct(), args);
/* 200:    */   }
/* 201:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.CartesianProduct
 * JD-Core Version:    0.7.0.1
 */