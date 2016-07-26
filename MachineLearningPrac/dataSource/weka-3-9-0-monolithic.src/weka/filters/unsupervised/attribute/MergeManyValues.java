/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.Capabilities;
/*   9:    */ import weka.core.Capabilities.Capability;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.Option;
/*  13:    */ import weka.core.OptionHandler;
/*  14:    */ import weka.core.Range;
/*  15:    */ import weka.core.SingleIndex;
/*  16:    */ import weka.core.UnsupportedAttributeTypeException;
/*  17:    */ import weka.core.Utils;
/*  18:    */ import weka.filters.StreamableFilter;
/*  19:    */ import weka.filters.UnsupervisedFilter;
/*  20:    */ 
/*  21:    */ public class MergeManyValues
/*  22:    */   extends PotentialClassIgnorer
/*  23:    */   implements UnsupervisedFilter, StreamableFilter, OptionHandler
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = 4649332102154713625L;
/*  26: 84 */   protected SingleIndex m_AttIndex = new SingleIndex("last");
/*  27: 87 */   protected String m_Label = "merged";
/*  28: 90 */   protected Range m_MergeRange = new Range("1,2");
/*  29:    */   
/*  30:    */   public String globalInfo()
/*  31:    */   {
/*  32: 99 */     return "Merges many values of a nominal attribute into one value.";
/*  33:    */   }
/*  34:    */   
/*  35:    */   public Enumeration<Option> listOptions()
/*  36:    */   {
/*  37:110 */     Vector<Option> newVector = new Vector(3);
/*  38:    */     
/*  39:112 */     newVector.addElement(new Option("\tSets the attribute index\n\t(default: last)", "C", 1, "-C <col>"));
/*  40:    */     
/*  41:    */ 
/*  42:115 */     newVector.addElement(new Option("\tSets the label of the newly merged classes\n\t(default: 'merged')", "L", 1, "-L <label>"));
/*  43:    */     
/*  44:    */ 
/*  45:    */ 
/*  46:119 */     newVector.addElement(new Option("\tSets the merge range. 'first and 'last' are accepted as well.'\n\tE.g.: first-5,7,9,20-last\n\t(default: 1,2)", "R", 1, "-R <range>"));
/*  47:    */     
/*  48:    */ 
/*  49:    */ 
/*  50:    */ 
/*  51:124 */     Enumeration<Option> superOpts = super.listOptions();
/*  52:125 */     while (superOpts.hasMoreElements()) {
/*  53:126 */       newVector.add(superOpts.nextElement());
/*  54:    */     }
/*  55:129 */     return newVector.elements();
/*  56:    */   }
/*  57:    */   
/*  58:    */   public void setOptions(String[] options)
/*  59:    */     throws Exception
/*  60:    */   {
/*  61:166 */     String tmpStr = Utils.getOption('C', options);
/*  62:167 */     if (tmpStr.length() != 0) {
/*  63:168 */       setAttributeIndex(tmpStr);
/*  64:    */     } else {
/*  65:170 */       setAttributeIndex("last");
/*  66:    */     }
/*  67:173 */     tmpStr = Utils.getOption('L', options);
/*  68:174 */     if (tmpStr.length() != 0) {
/*  69:175 */       setLabel(tmpStr);
/*  70:    */     } else {
/*  71:177 */       setLabel("merged");
/*  72:    */     }
/*  73:180 */     tmpStr = Utils.getOption('R', options);
/*  74:181 */     if (tmpStr.length() != 0) {
/*  75:182 */       setMergeValueRange(tmpStr);
/*  76:    */     } else {
/*  77:184 */       setMergeValueRange("1,2");
/*  78:    */     }
/*  79:187 */     if (getInputFormat() != null) {
/*  80:188 */       setInputFormat(getInputFormat());
/*  81:    */     }
/*  82:191 */     super.setOptions(options);
/*  83:    */     
/*  84:193 */     Utils.checkForRemainingOptions(options);
/*  85:    */   }
/*  86:    */   
/*  87:    */   public String[] getOptions()
/*  88:    */   {
/*  89:204 */     Vector<String> result = new Vector();
/*  90:    */     
/*  91:206 */     result.add("-C");
/*  92:207 */     result.add(getAttributeIndex());
/*  93:    */     
/*  94:209 */     result.add("-L");
/*  95:210 */     result.add(getLabel());
/*  96:    */     
/*  97:212 */     result.add("-R");
/*  98:213 */     result.add(getMergeValueRange());
/*  99:    */     
/* 100:215 */     String[] superOpts = super.getOptions();
/* 101:216 */     result.addAll(Arrays.asList(superOpts));
/* 102:    */     
/* 103:218 */     return (String[])result.toArray(new String[result.size()]);
/* 104:    */   }
/* 105:    */   
/* 106:    */   public Capabilities getCapabilities()
/* 107:    */   {
/* 108:229 */     Capabilities result = super.getCapabilities();
/* 109:    */     
/* 110:    */ 
/* 111:232 */     result.enableAllAttributes();
/* 112:233 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 113:    */     
/* 114:    */ 
/* 115:236 */     result.enableAllClasses();
/* 116:237 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 117:238 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 118:    */     
/* 119:240 */     return result;
/* 120:    */   }
/* 121:    */   
/* 122:    */   public boolean setInputFormat(Instances instanceInfo)
/* 123:    */     throws Exception
/* 124:    */   {
/* 125:254 */     super.setInputFormat(instanceInfo);
/* 126:    */     
/* 127:256 */     this.m_AttIndex.setUpper(inputFormatPeek().numAttributes() - 1);
/* 128:    */     
/* 129:258 */     this.m_MergeRange.setUpper(inputFormatPeek().attribute(this.m_AttIndex.getIndex()).numValues() - 1);
/* 130:260 */     if ((inputFormatPeek().classIndex() > -1) && (inputFormatPeek().classIndex() == this.m_AttIndex.getIndex())) {
/* 131:262 */       throw new Exception("Cannot process class attribute.");
/* 132:    */     }
/* 133:264 */     if (!inputFormatPeek().attribute(this.m_AttIndex.getIndex()).isNominal()) {
/* 134:265 */       throw new UnsupportedAttributeTypeException("Chosen attribute not nominal.");
/* 135:    */     }
/* 136:268 */     if (inputFormatPeek().attribute(this.m_AttIndex.getIndex()).numValues() < 2) {
/* 137:269 */       throw new UnsupportedAttributeTypeException("Chosen attribute has less than two values.");
/* 138:    */     }
/* 139:273 */     setOutputFormat();
/* 140:274 */     return true;
/* 141:    */   }
/* 142:    */   
/* 143:    */   private void setOutputFormat()
/* 144:    */   {
/* 145:287 */     ArrayList<Attribute> newAtts = new ArrayList(getInputFormat().numAttributes());
/* 146:288 */     for (int j = 0; j < getInputFormat().numAttributes(); j++)
/* 147:    */     {
/* 148:289 */       Attribute att = getInputFormat().attribute(j);
/* 149:290 */       if (j != this.m_AttIndex.getIndex())
/* 150:    */       {
/* 151:291 */         newAtts.add((Attribute)att.copy());
/* 152:    */       }
/* 153:    */       else
/* 154:    */       {
/* 155:295 */         ArrayList<String> newVals = new ArrayList(att.numValues() - 1);
/* 156:296 */         for (int i = 0; i < att.numValues(); i++)
/* 157:    */         {
/* 158:297 */           boolean inMergeList = false;
/* 159:299 */           if (att.value(i).equalsIgnoreCase(this.m_Label)) {
/* 160:301 */             inMergeList = true;
/* 161:    */           } else {
/* 162:303 */             inMergeList = this.m_MergeRange.isInRange(i);
/* 163:    */           }
/* 164:306 */           if (!inMergeList) {
/* 165:308 */             newVals.add(att.value(i));
/* 166:    */           }
/* 167:    */         }
/* 168:311 */         newVals.add(this.m_Label);
/* 169:    */         
/* 170:313 */         Attribute newAtt = new Attribute(att.name(), newVals);
/* 171:314 */         newAtt.setWeight(getInputFormat().attribute(j).weight());
/* 172:315 */         newAtts.add(newAtt);
/* 173:    */       }
/* 174:    */     }
/* 175:320 */     Instances newData = new Instances(getInputFormat().relationName(), newAtts, 0);
/* 176:321 */     newData.setClassIndex(getInputFormat().classIndex());
/* 177:322 */     setOutputFormat(newData);
/* 178:    */   }
/* 179:    */   
/* 180:    */   public boolean input(Instance instance)
/* 181:    */   {
/* 182:335 */     if (getInputFormat() == null) {
/* 183:336 */       throw new IllegalStateException("No input instance format defined");
/* 184:    */     }
/* 185:338 */     if (this.m_NewBatch)
/* 186:    */     {
/* 187:339 */       resetQueue();
/* 188:340 */       this.m_NewBatch = false;
/* 189:    */     }
/* 190:343 */     Attribute att = getInputFormat().attribute(this.m_AttIndex.getIndex());
/* 191:344 */     ArrayList<String> newVals = new ArrayList(att.numValues() - 1);
/* 192:345 */     for (int i = 0; i < att.numValues(); i++)
/* 193:    */     {
/* 194:346 */       boolean inMergeList = false;
/* 195:348 */       if (att.value(i).equalsIgnoreCase(this.m_Label)) {
/* 196:350 */         inMergeList = true;
/* 197:    */       } else {
/* 198:352 */         inMergeList = this.m_MergeRange.isInRange(i);
/* 199:    */       }
/* 200:355 */       if (!inMergeList) {
/* 201:357 */         newVals.add(att.value(i));
/* 202:    */       }
/* 203:    */     }
/* 204:360 */     newVals.add(this.m_Label);
/* 205:    */     
/* 206:362 */     Attribute temp = new Attribute(att.name(), newVals);
/* 207:    */     
/* 208:364 */     Instance newInstance = (Instance)instance.copy();
/* 209:365 */     if (!newInstance.isMissing(this.m_AttIndex.getIndex()))
/* 210:    */     {
/* 211:366 */       String currValue = newInstance.stringValue(this.m_AttIndex.getIndex());
/* 212:367 */       if (temp.indexOfValue(currValue) == -1) {
/* 213:368 */         newInstance.setValue(this.m_AttIndex.getIndex(), temp.indexOfValue(this.m_Label));
/* 214:    */       } else {
/* 215:370 */         newInstance.setValue(this.m_AttIndex.getIndex(), temp.indexOfValue(currValue));
/* 216:    */       }
/* 217:    */     }
/* 218:375 */     push(newInstance, false);
/* 219:376 */     return true;
/* 220:    */   }
/* 221:    */   
/* 222:    */   public String attributeIndexTipText()
/* 223:    */   {
/* 224:386 */     return "Sets which attribute to process. This attribute must be nominal (\"first\" and \"last\" are valid values)";
/* 225:    */   }
/* 226:    */   
/* 227:    */   public String getAttributeIndex()
/* 228:    */   {
/* 229:396 */     return this.m_AttIndex.getSingleIndex();
/* 230:    */   }
/* 231:    */   
/* 232:    */   public void setAttributeIndex(String attIndex)
/* 233:    */   {
/* 234:405 */     this.m_AttIndex.setSingleIndex(attIndex);
/* 235:    */   }
/* 236:    */   
/* 237:    */   public String labelTipText()
/* 238:    */   {
/* 239:415 */     return "The new label for the merged values.";
/* 240:    */   }
/* 241:    */   
/* 242:    */   public String getLabel()
/* 243:    */   {
/* 244:424 */     return this.m_Label;
/* 245:    */   }
/* 246:    */   
/* 247:    */   public void setLabel(String alabel)
/* 248:    */   {
/* 249:433 */     this.m_Label = alabel;
/* 250:    */   }
/* 251:    */   
/* 252:    */   public String getMergeValueRange()
/* 253:    */   {
/* 254:442 */     return this.m_MergeRange.getRanges();
/* 255:    */   }
/* 256:    */   
/* 257:    */   public String mergeValueRangeTipText()
/* 258:    */   {
/* 259:452 */     return "The range of values to merge.";
/* 260:    */   }
/* 261:    */   
/* 262:    */   public void setMergeValueRange(String range)
/* 263:    */   {
/* 264:461 */     this.m_MergeRange.setRanges(range);
/* 265:    */   }
/* 266:    */   
/* 267:    */   public String getRevision()
/* 268:    */   {
/* 269:471 */     return "$Revision: 12037 $";
/* 270:    */   }
/* 271:    */   
/* 272:    */   public static void main(String[] args)
/* 273:    */   {
/* 274:480 */     runFilter(new MergeManyValues(), args);
/* 275:    */   }
/* 276:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.MergeManyValues
 * JD-Core Version:    0.7.0.1
 */