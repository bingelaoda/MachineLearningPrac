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
/*  14:    */ import weka.core.OptionHandler;
/*  15:    */ import weka.core.RevisionUtils;
/*  16:    */ import weka.core.SerializedObject;
/*  17:    */ import weka.core.SingleIndex;
/*  18:    */ import weka.core.UnsupportedAttributeTypeException;
/*  19:    */ import weka.core.Utils;
/*  20:    */ import weka.filters.Filter;
/*  21:    */ import weka.filters.StreamableFilter;
/*  22:    */ import weka.filters.UnsupervisedFilter;
/*  23:    */ 
/*  24:    */ public class AddValues
/*  25:    */   extends Filter
/*  26:    */   implements UnsupervisedFilter, StreamableFilter, OptionHandler
/*  27:    */ {
/*  28:    */   private static final long serialVersionUID = -8100622241742393656L;
/*  29: 88 */   protected SingleIndex m_AttIndex = new SingleIndex("last");
/*  30: 91 */   protected ArrayList<String> m_Labels = new ArrayList();
/*  31: 94 */   protected boolean m_Sort = false;
/*  32:    */   protected int[] m_SortedIndices;
/*  33:    */   
/*  34:    */   public String globalInfo()
/*  35:    */   {
/*  36:106 */     return "Adds the labels from the given list to an attribute if they are missing. The labels can also be sorted in an ascending manner. If no labels are provided then only the (optional) sorting applies.";
/*  37:    */   }
/*  38:    */   
/*  39:    */   public Enumeration<Option> listOptions()
/*  40:    */   {
/*  41:119 */     Vector<Option> result = new Vector();
/*  42:    */     
/*  43:121 */     result.addElement(new Option("\tSets the attribute index\n\t(default last).", "C", 1, "-C <col>"));
/*  44:    */     
/*  45:    */ 
/*  46:124 */     result.addElement(new Option("\tComma-separated list of labels to add.\n\t(default: none)", "L", 1, "-L <label1,label2,...>"));
/*  47:    */     
/*  48:    */ 
/*  49:127 */     result.addElement(new Option("\tTurns on the sorting of the labels.", "S", 0, "-S"));
/*  50:    */     
/*  51:    */ 
/*  52:130 */     return result.elements();
/*  53:    */   }
/*  54:    */   
/*  55:    */   public void setOptions(String[] options)
/*  56:    */     throws Exception
/*  57:    */   {
/*  58:166 */     String tmpStr = Utils.getOption('C', options);
/*  59:167 */     if (tmpStr.length() != 0) {
/*  60:168 */       setAttributeIndex(tmpStr);
/*  61:    */     } else {
/*  62:170 */       setAttributeIndex("last");
/*  63:    */     }
/*  64:173 */     tmpStr = Utils.getOption('L', options);
/*  65:174 */     if (tmpStr.length() != 0) {
/*  66:175 */       setLabels(tmpStr);
/*  67:    */     } else {
/*  68:177 */       setLabels("");
/*  69:    */     }
/*  70:180 */     setSort(Utils.getFlag('S', options));
/*  71:182 */     if (getInputFormat() != null) {
/*  72:183 */       setInputFormat(getInputFormat());
/*  73:    */     }
/*  74:186 */     Utils.checkForRemainingOptions(options);
/*  75:    */   }
/*  76:    */   
/*  77:    */   public String[] getOptions()
/*  78:    */   {
/*  79:197 */     Vector<String> result = new Vector();
/*  80:    */     
/*  81:199 */     result.add("-C");
/*  82:200 */     result.add("" + getAttributeIndex());
/*  83:    */     
/*  84:202 */     result.add("-L");
/*  85:203 */     result.add("" + getLabels());
/*  86:205 */     if (getSort()) {
/*  87:206 */       result.add("-S");
/*  88:    */     }
/*  89:209 */     return (String[])result.toArray(new String[result.size()]);
/*  90:    */   }
/*  91:    */   
/*  92:    */   public Capabilities getCapabilities()
/*  93:    */   {
/*  94:220 */     Capabilities result = super.getCapabilities();
/*  95:221 */     result.disableAll();
/*  96:    */     
/*  97:    */ 
/*  98:224 */     result.enableAllAttributes();
/*  99:225 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 100:    */     
/* 101:    */ 
/* 102:228 */     result.enableAllClasses();
/* 103:229 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 104:230 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 105:    */     
/* 106:232 */     return result;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public boolean setInputFormat(Instances instanceInfo)
/* 110:    */     throws Exception
/* 111:    */   {
/* 112:255 */     super.setInputFormat(instanceInfo);
/* 113:    */     
/* 114:257 */     this.m_AttIndex.setUpper(instanceInfo.numAttributes() - 1);
/* 115:258 */     Attribute att = instanceInfo.attribute(this.m_AttIndex.getIndex());
/* 116:259 */     if (!att.isNominal()) {
/* 117:260 */       throw new UnsupportedAttributeTypeException("Chosen attribute not nominal.");
/* 118:    */     }
/* 119:265 */     ArrayList<String> allLabels = new ArrayList();
/* 120:266 */     Enumeration<Object> enm = att.enumerateValues();
/* 121:267 */     while (enm.hasMoreElements())
/* 122:    */     {
/* 123:268 */       Object o = enm.nextElement();
/* 124:269 */       if ((o instanceof SerializedObject)) {
/* 125:270 */         o = ((SerializedObject)o).getObject();
/* 126:    */       }
/* 127:272 */       allLabels.add((String)o);
/* 128:    */     }
/* 129:275 */     for (int i = 0; i < this.m_Labels.size(); i++) {
/* 130:276 */       if (!allLabels.contains(this.m_Labels.get(i))) {
/* 131:277 */         allLabels.add(this.m_Labels.get(i));
/* 132:    */       }
/* 133:    */     }
/* 134:282 */     if (getSort()) {
/* 135:283 */       Collections.sort(allLabels);
/* 136:    */     }
/* 137:285 */     this.m_SortedIndices = new int[att.numValues()];
/* 138:286 */     enm = att.enumerateValues();
/* 139:287 */     i = 0;
/* 140:288 */     while (enm.hasMoreElements())
/* 141:    */     {
/* 142:289 */       this.m_SortedIndices[i] = allLabels.indexOf(enm.nextElement());
/* 143:290 */       i++;
/* 144:    */     }
/* 145:294 */     ArrayList<String> values = new ArrayList();
/* 146:295 */     for (i = 0; i < allLabels.size(); i++) {
/* 147:296 */       values.add(allLabels.get(i));
/* 148:    */     }
/* 149:298 */     Attribute attNew = new Attribute(att.name(), values);
/* 150:    */     
/* 151:300 */     ArrayList<Attribute> atts = new ArrayList();
/* 152:301 */     for (i = 0; i < instanceInfo.numAttributes(); i++) {
/* 153:302 */       if (i == this.m_AttIndex.getIndex()) {
/* 154:303 */         atts.add(attNew);
/* 155:    */       } else {
/* 156:305 */         atts.add(instanceInfo.attribute(i));
/* 157:    */       }
/* 158:    */     }
/* 159:309 */     Instances instNew = new Instances(instanceInfo.relationName(), atts, 0);
/* 160:310 */     instNew.setClassIndex(instanceInfo.classIndex());
/* 161:    */     
/* 162:    */ 
/* 163:313 */     setOutputFormat(instNew);
/* 164:    */     
/* 165:315 */     return true;
/* 166:    */   }
/* 167:    */   
/* 168:    */   public boolean input(Instance instance)
/* 169:    */   {
/* 170:331 */     if (getInputFormat() == null) {
/* 171:332 */       throw new IllegalStateException("No input instance format defined");
/* 172:    */     }
/* 173:335 */     if (this.m_NewBatch)
/* 174:    */     {
/* 175:336 */       resetQueue();
/* 176:337 */       this.m_NewBatch = false;
/* 177:    */     }
/* 178:341 */     double[] values = instance.toDoubleArray();
/* 179:342 */     if (!instance.isMissing(this.m_AttIndex.getIndex())) {
/* 180:343 */       values[this.m_AttIndex.getIndex()] = this.m_SortedIndices[((int)values[this.m_AttIndex.getIndex()])];
/* 181:    */     }
/* 182:346 */     Instance newInstance = new DenseInstance(instance.weight(), values);
/* 183:    */     
/* 184:    */ 
/* 185:349 */     copyValues(instance, false, instance.dataset(), outputFormatPeek());
/* 186:    */     
/* 187:351 */     push(newInstance);
/* 188:    */     
/* 189:353 */     return true;
/* 190:    */   }
/* 191:    */   
/* 192:    */   public String attributeIndexTipText()
/* 193:    */   {
/* 194:363 */     return "Sets which attribute to process. This attribute must be nominal (\"first\" and \"last\" are valid values)";
/* 195:    */   }
/* 196:    */   
/* 197:    */   public String getAttributeIndex()
/* 198:    */   {
/* 199:373 */     return this.m_AttIndex.getSingleIndex();
/* 200:    */   }
/* 201:    */   
/* 202:    */   public void setAttributeIndex(String attIndex)
/* 203:    */   {
/* 204:382 */     this.m_AttIndex.setSingleIndex(attIndex);
/* 205:    */   }
/* 206:    */   
/* 207:    */   public String labelsTipText()
/* 208:    */   {
/* 209:392 */     return "Comma-separated list of lables to add.";
/* 210:    */   }
/* 211:    */   
/* 212:    */   public String getLabels()
/* 213:    */   {
/* 214:404 */     String result = "";
/* 215:405 */     for (int i = 0; i < this.m_Labels.size(); i++)
/* 216:    */     {
/* 217:406 */       if (i > 0) {
/* 218:407 */         result = result + ",";
/* 219:    */       }
/* 220:409 */       result = result + Utils.quote((String)this.m_Labels.get(i));
/* 221:    */     }
/* 222:412 */     return result;
/* 223:    */   }
/* 224:    */   
/* 225:    */   public void setLabels(String value)
/* 226:    */   {
/* 227:426 */     this.m_Labels.clear();
/* 228:    */     
/* 229:428 */     String label = "";
/* 230:429 */     boolean quoted = false;
/* 231:430 */     boolean add = false;
/* 232:432 */     for (int i = 0; i < value.length(); i++)
/* 233:    */     {
/* 234:434 */       if (value.charAt(i) == '"')
/* 235:    */       {
/* 236:435 */         quoted = !quoted;
/* 237:436 */         if (!quoted) {
/* 238:437 */           add = true;
/* 239:    */         }
/* 240:    */       }
/* 241:441 */       else if ((value.charAt(i) == ',') && (!quoted))
/* 242:    */       {
/* 243:442 */         add = true;
/* 244:    */       }
/* 245:    */       else
/* 246:    */       {
/* 247:446 */         label = label + value.charAt(i);
/* 248:448 */         if (i == value.length() - 1) {
/* 249:449 */           add = true;
/* 250:    */         }
/* 251:    */       }
/* 252:453 */       if (add)
/* 253:    */       {
/* 254:454 */         if (label.length() != 0) {
/* 255:455 */           this.m_Labels.add(label);
/* 256:    */         }
/* 257:457 */         label = "";
/* 258:458 */         add = false;
/* 259:    */       }
/* 260:    */     }
/* 261:    */   }
/* 262:    */   
/* 263:    */   public String sortTipText()
/* 264:    */   {
/* 265:470 */     return "Whether to sort the labels alphabetically.";
/* 266:    */   }
/* 267:    */   
/* 268:    */   public boolean getSort()
/* 269:    */   {
/* 270:479 */     return this.m_Sort;
/* 271:    */   }
/* 272:    */   
/* 273:    */   public void setSort(boolean value)
/* 274:    */   {
/* 275:488 */     this.m_Sort = value;
/* 276:    */   }
/* 277:    */   
/* 278:    */   public String getRevision()
/* 279:    */   {
/* 280:498 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 281:    */   }
/* 282:    */   
/* 283:    */   public static void main(String[] args)
/* 284:    */   {
/* 285:507 */     runFilter(new AddValues(), args);
/* 286:    */   }
/* 287:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.AddValues
 * JD-Core Version:    0.7.0.1
 */