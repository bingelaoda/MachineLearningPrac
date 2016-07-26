/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.StringTokenizer;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.Capabilities;
/*   9:    */ import weka.core.Capabilities.Capability;
/*  10:    */ import weka.core.DenseInstance;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.Option;
/*  14:    */ import weka.core.OptionHandler;
/*  15:    */ import weka.core.Range;
/*  16:    */ import weka.core.RevisionUtils;
/*  17:    */ import weka.core.SparseInstance;
/*  18:    */ import weka.core.Utils;
/*  19:    */ import weka.filters.Filter;
/*  20:    */ import weka.filters.StreamableFilter;
/*  21:    */ import weka.filters.UnsupervisedFilter;
/*  22:    */ 
/*  23:    */ public class Reorder
/*  24:    */   extends Filter
/*  25:    */   implements UnsupervisedFilter, StreamableFilter, OptionHandler
/*  26:    */ {
/*  27:    */   static final long serialVersionUID = -1135571321097202292L;
/*  28: 82 */   protected String m_NewOrderCols = "first-last";
/*  29:    */   protected int[] m_SelectedAttributes;
/*  30:    */   protected int[] m_InputStringIndex;
/*  31:    */   
/*  32:    */   public Enumeration<Option> listOptions()
/*  33:    */   {
/*  34:104 */     Vector<Option> newVector = new Vector();
/*  35:    */     
/*  36:106 */     newVector.addElement(new Option("\tSpecify list of columns to copy. First and last are valid\n\tindexes. (default first-last)", "R", 1, "-R <index1,index2-index4,...>"));
/*  37:    */     
/*  38:    */ 
/*  39:    */ 
/*  40:    */ 
/*  41:111 */     return newVector.elements();
/*  42:    */   }
/*  43:    */   
/*  44:    */   public void setOptions(String[] options)
/*  45:    */     throws Exception
/*  46:    */   {
/*  47:135 */     String orderList = Utils.getOption('R', options);
/*  48:136 */     if (orderList.length() != 0) {
/*  49:137 */       setAttributeIndices(orderList);
/*  50:    */     }
/*  51:140 */     if (getInputFormat() != null) {
/*  52:141 */       setInputFormat(getInputFormat());
/*  53:    */     }
/*  54:144 */     Utils.checkForRemainingOptions(options);
/*  55:    */   }
/*  56:    */   
/*  57:    */   public String[] getOptions()
/*  58:    */   {
/*  59:155 */     Vector<String> options = new Vector();
/*  60:157 */     if (!getAttributeIndices().equals(""))
/*  61:    */     {
/*  62:158 */       options.add("-R");
/*  63:159 */       options.add(getAttributeIndices());
/*  64:    */     }
/*  65:162 */     return (String[])options.toArray(new String[0]);
/*  66:    */   }
/*  67:    */   
/*  68:    */   protected int determineIndex(String s, int numAttributes)
/*  69:    */     throws Exception
/*  70:    */   {
/*  71:    */     int result;
/*  72:    */     int result;
/*  73:176 */     if (s.equals("first"))
/*  74:    */     {
/*  75:177 */       result = 0;
/*  76:    */     }
/*  77:    */     else
/*  78:    */     {
/*  79:    */       int result;
/*  80:178 */       if (s.equals("last")) {
/*  81:179 */         result = numAttributes - 1;
/*  82:    */       } else {
/*  83:181 */         result = Integer.parseInt(s) - 1;
/*  84:    */       }
/*  85:    */     }
/*  86:185 */     if ((result < 0) || (result > numAttributes - 1)) {
/*  87:186 */       throw new IllegalArgumentException("'" + s + "' is not a valid index for the range '1-" + numAttributes + "'!");
/*  88:    */     }
/*  89:190 */     return result;
/*  90:    */   }
/*  91:    */   
/*  92:    */   protected int[] determineIndices(int numAttributes)
/*  93:    */     throws Exception
/*  94:    */   {
/*  95:211 */     Vector<Integer> list = new Vector();
/*  96:    */     
/*  97:    */ 
/*  98:214 */     StringTokenizer tok = new StringTokenizer(this.m_NewOrderCols, ",");
/*  99:215 */     while (tok.hasMoreTokens())
/* 100:    */     {
/* 101:216 */       String token = tok.nextToken();
/* 102:217 */       if (token.indexOf("-") > -1)
/* 103:    */       {
/* 104:218 */         String[] range = token.split("-");
/* 105:219 */         if (range.length != 2) {
/* 106:220 */           throw new IllegalArgumentException("'" + token + "' is not a valid range!");
/* 107:    */         }
/* 108:223 */         int from = determineIndex(range[0], numAttributes);
/* 109:224 */         int to = determineIndex(range[1], numAttributes);
/* 110:226 */         if (from <= to) {
/* 111:227 */           for (int i = from; i <= to; i++) {
/* 112:228 */             list.add(Integer.valueOf(i));
/* 113:    */           }
/* 114:    */         } else {
/* 115:231 */           for (int i = from; i >= to; i--) {
/* 116:232 */             list.add(Integer.valueOf(i));
/* 117:    */           }
/* 118:    */         }
/* 119:    */       }
/* 120:    */       else
/* 121:    */       {
/* 122:236 */         list.add(Integer.valueOf(determineIndex(token, numAttributes)));
/* 123:    */       }
/* 124:    */     }
/* 125:241 */     int[] result = new int[list.size()];
/* 126:242 */     for (int i = 0; i < list.size(); i++) {
/* 127:243 */       result[i] = ((Integer)list.get(i)).intValue();
/* 128:    */     }
/* 129:246 */     return result;
/* 130:    */   }
/* 131:    */   
/* 132:    */   public Capabilities getCapabilities()
/* 133:    */   {
/* 134:257 */     Capabilities result = super.getCapabilities();
/* 135:258 */     result.disableAll();
/* 136:    */     
/* 137:    */ 
/* 138:261 */     result.enableAllAttributes();
/* 139:262 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 140:    */     
/* 141:    */ 
/* 142:265 */     result.enableAllClasses();
/* 143:266 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 144:267 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 145:    */     
/* 146:269 */     return result;
/* 147:    */   }
/* 148:    */   
/* 149:    */   public boolean setInputFormat(Instances instanceInfo)
/* 150:    */     throws Exception
/* 151:    */   {
/* 152:283 */     super.setInputFormat(instanceInfo);
/* 153:    */     
/* 154:285 */     ArrayList<Attribute> attributes = new ArrayList();
/* 155:286 */     int outputClass = -1;
/* 156:287 */     this.m_SelectedAttributes = determineIndices(instanceInfo.numAttributes());
/* 157:288 */     for (int current : this.m_SelectedAttributes)
/* 158:    */     {
/* 159:289 */       if (instanceInfo.classIndex() == current) {
/* 160:290 */         outputClass = attributes.size();
/* 161:    */       }
/* 162:292 */       Attribute keep = (Attribute)instanceInfo.attribute(current).copy();
/* 163:293 */       attributes.add(keep);
/* 164:    */     }
/* 165:296 */     initInputLocators(instanceInfo, this.m_SelectedAttributes);
/* 166:    */     
/* 167:298 */     Instances outputFormat = new Instances(instanceInfo.relationName(), attributes, 0);
/* 168:    */     
/* 169:300 */     outputFormat.setClassIndex(outputClass);
/* 170:301 */     setOutputFormat(outputFormat);
/* 171:    */     
/* 172:303 */     return true;
/* 173:    */   }
/* 174:    */   
/* 175:    */   public boolean input(Instance instance)
/* 176:    */   {
/* 177:317 */     if (getInputFormat() == null) {
/* 178:318 */       throw new IllegalStateException("No input instance format defined");
/* 179:    */     }
/* 180:320 */     if (this.m_NewBatch)
/* 181:    */     {
/* 182:321 */       resetQueue();
/* 183:322 */       this.m_NewBatch = false;
/* 184:    */     }
/* 185:325 */     double[] vals = new double[outputFormatPeek().numAttributes()];
/* 186:326 */     for (int i = 0; i < this.m_SelectedAttributes.length; i++)
/* 187:    */     {
/* 188:327 */       int current = this.m_SelectedAttributes[i];
/* 189:328 */       vals[i] = instance.value(current);
/* 190:    */     }
/* 191:330 */     Instance inst = null;
/* 192:331 */     if ((instance instanceof SparseInstance)) {
/* 193:332 */       inst = new SparseInstance(instance.weight(), vals);
/* 194:    */     } else {
/* 195:334 */       inst = new DenseInstance(instance.weight(), vals);
/* 196:    */     }
/* 197:337 */     copyValues(inst, false, instance.dataset(), outputFormatPeek());
/* 198:    */     
/* 199:339 */     push(inst);
/* 200:    */     
/* 201:341 */     return true;
/* 202:    */   }
/* 203:    */   
/* 204:    */   public String globalInfo()
/* 205:    */   {
/* 206:351 */     return "A filter that generates output with a new order of the attributes. Useful if one wants to move an attribute to the end to use it as class attribute (e.g. with using \"-R 2-last,1\").\nBut it's not only possible to change the order of all the attributes, but also to leave out attributes. E.g. if you have 10 attributes, you can generate the following output order: 1,3,5,7,9,10 or 10,1-5.\nYou can also duplicate attributes, e.g. for further processing later on: e.g. 1,1,1,4,4,4,2,2,2 where the second and the third column of each attribute are processed differently and the first one, i.e. the original one is kept.\nOne can simply inverse the order of the attributes via 'last-first'.\nAfter appyling the filter, the index of the class attribute is the last attribute.";
/* 207:    */   }
/* 208:    */   
/* 209:    */   public String getAttributeIndices()
/* 210:    */   {
/* 211:372 */     return this.m_NewOrderCols;
/* 212:    */   }
/* 213:    */   
/* 214:    */   public String attributeIndicesTipText()
/* 215:    */   {
/* 216:382 */     return "Specify range of attributes to act on. This is a comma separated list of attribute indices, with \"first\" and \"last\" valid values. Specify an inclusive range with \"-\". E.g: \"first-3,5,6-10,last\".";
/* 217:    */   }
/* 218:    */   
/* 219:    */   public void setAttributeIndices(String rangeList)
/* 220:    */     throws Exception
/* 221:    */   {
/* 222:402 */     if (rangeList.replaceAll("[afilrst0-9\\-,]*", "").length() != 0) {
/* 223:403 */       throw new IllegalArgumentException("Not a valid range string!");
/* 224:    */     }
/* 225:406 */     this.m_NewOrderCols = rangeList;
/* 226:    */   }
/* 227:    */   
/* 228:    */   public void setAttributeIndicesArray(int[] attributes)
/* 229:    */     throws Exception
/* 230:    */   {
/* 231:421 */     setAttributeIndices(Range.indicesToRangeList(attributes));
/* 232:    */   }
/* 233:    */   
/* 234:    */   public String getRevision()
/* 235:    */   {
/* 236:431 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 237:    */   }
/* 238:    */   
/* 239:    */   public static void main(String[] argv)
/* 240:    */   {
/* 241:440 */     runFilter(new Reorder(), argv);
/* 242:    */   }
/* 243:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.Reorder
 * JD-Core Version:    0.7.0.1
 */