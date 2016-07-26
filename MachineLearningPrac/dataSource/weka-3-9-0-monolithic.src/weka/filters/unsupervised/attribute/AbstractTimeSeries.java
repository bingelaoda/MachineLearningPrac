/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import java.util.Vector;
/*   5:    */ import weka.core.Instance;
/*   6:    */ import weka.core.Instances;
/*   7:    */ import weka.core.Option;
/*   8:    */ import weka.core.OptionHandler;
/*   9:    */ import weka.core.Queue;
/*  10:    */ import weka.core.Range;
/*  11:    */ import weka.core.Utils;
/*  12:    */ import weka.filters.Filter;
/*  13:    */ import weka.filters.UnsupervisedFilter;
/*  14:    */ 
/*  15:    */ public abstract class AbstractTimeSeries
/*  16:    */   extends Filter
/*  17:    */   implements UnsupervisedFilter, OptionHandler
/*  18:    */ {
/*  19:    */   private static final long serialVersionUID = -3795656792078022357L;
/*  20: 78 */   protected Range m_SelectedCols = new Range();
/*  21: 84 */   protected boolean m_FillWithMissing = true;
/*  22: 90 */   protected int m_InstanceRange = -1;
/*  23:    */   protected Queue m_History;
/*  24:    */   
/*  25:    */   public Enumeration<Option> listOptions()
/*  26:    */   {
/*  27:103 */     Vector<Option> newVector = new Vector(4);
/*  28:    */     
/*  29:105 */     newVector.addElement(new Option("\tSpecify list of columns to translate in time. First and\n\tlast are valid indexes. (default none)", "R", 1, "-R <index1,index2-index4,...>"));
/*  30:    */     
/*  31:    */ 
/*  32:    */ 
/*  33:109 */     newVector.addElement(new Option("\tInvert matching sense (i.e. calculate for all non-specified columns)", "V", 0, "-V"));
/*  34:    */     
/*  35:    */ 
/*  36:112 */     newVector.addElement(new Option("\tThe number of instances forward to translate values\n\tbetween. A negative number indicates taking values from\n\ta past instance. (default -1)", "I", 1, "-I <num>"));
/*  37:    */     
/*  38:    */ 
/*  39:    */ 
/*  40:116 */     newVector.addElement(new Option("\tFor instances at the beginning or end of the dataset where\n\tthe translated values are not known, remove those instances\n\t(default is to use missing values).", "M", 0, "-M"));
/*  41:    */     
/*  42:    */ 
/*  43:    */ 
/*  44:    */ 
/*  45:121 */     return newVector.elements();
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void setOptions(String[] options)
/*  49:    */     throws Exception
/*  50:    */   {
/*  51:155 */     String copyList = Utils.getOption('R', options);
/*  52:156 */     if (copyList.length() != 0) {
/*  53:157 */       setAttributeIndices(copyList);
/*  54:    */     } else {
/*  55:159 */       setAttributeIndices("");
/*  56:    */     }
/*  57:162 */     setInvertSelection(Utils.getFlag('V', options));
/*  58:    */     
/*  59:164 */     setFillWithMissing(!Utils.getFlag('M', options));
/*  60:    */     
/*  61:166 */     String instanceRange = Utils.getOption('I', options);
/*  62:167 */     if (instanceRange.length() != 0) {
/*  63:168 */       setInstanceRange(Integer.parseInt(instanceRange));
/*  64:    */     } else {
/*  65:170 */       setInstanceRange(-1);
/*  66:    */     }
/*  67:173 */     if (getInputFormat() != null) {
/*  68:174 */       setInputFormat(getInputFormat());
/*  69:    */     }
/*  70:177 */     Utils.checkForRemainingOptions(options);
/*  71:    */   }
/*  72:    */   
/*  73:    */   public String[] getOptions()
/*  74:    */   {
/*  75:188 */     Vector<String> options = new Vector();
/*  76:190 */     if (!getAttributeIndices().equals(""))
/*  77:    */     {
/*  78:191 */       options.add("-R");
/*  79:192 */       options.add(getAttributeIndices());
/*  80:    */     }
/*  81:194 */     if (getInvertSelection()) {
/*  82:195 */       options.add("-V");
/*  83:    */     }
/*  84:197 */     options.add("-I");
/*  85:198 */     options.add("" + getInstanceRange());
/*  86:199 */     if (!getFillWithMissing()) {
/*  87:200 */       options.add("-M");
/*  88:    */     }
/*  89:203 */     return (String[])options.toArray(new String[0]);
/*  90:    */   }
/*  91:    */   
/*  92:    */   public boolean setInputFormat(Instances instanceInfo)
/*  93:    */     throws Exception
/*  94:    */   {
/*  95:218 */     super.setInputFormat(instanceInfo);
/*  96:219 */     resetHistory();
/*  97:220 */     this.m_SelectedCols.setUpper(instanceInfo.numAttributes() - 1);
/*  98:221 */     return false;
/*  99:    */   }
/* 100:    */   
/* 101:    */   public boolean input(Instance instance)
/* 102:    */     throws Exception
/* 103:    */   {
/* 104:237 */     if (getInputFormat() == null) {
/* 105:238 */       throw new NullPointerException("No input instance format defined");
/* 106:    */     }
/* 107:240 */     if (this.m_NewBatch)
/* 108:    */     {
/* 109:241 */       resetQueue();
/* 110:242 */       this.m_NewBatch = false;
/* 111:243 */       resetHistory();
/* 112:    */     }
/* 113:246 */     Instance newInstance = historyInput(instance);
/* 114:247 */     if (newInstance != null)
/* 115:    */     {
/* 116:248 */       push(newInstance);
/* 117:249 */       return true;
/* 118:    */     }
/* 119:251 */     return false;
/* 120:    */   }
/* 121:    */   
/* 122:    */   public boolean batchFinished()
/* 123:    */   {
/* 124:266 */     if (getInputFormat() == null) {
/* 125:267 */       throw new IllegalStateException("No input instance format defined");
/* 126:    */     }
/* 127:269 */     if ((getFillWithMissing()) && (this.m_InstanceRange > 0)) {
/* 128:270 */       while (!this.m_History.empty()) {
/* 129:271 */         push(mergeInstances(null, (Instance)this.m_History.pop()));
/* 130:    */       }
/* 131:    */     }
/* 132:274 */     flushInput();
/* 133:275 */     this.m_NewBatch = true;
/* 134:276 */     this.m_FirstBatchDone = true;
/* 135:277 */     return numPendingOutput() != 0;
/* 136:    */   }
/* 137:    */   
/* 138:    */   public String fillWithMissingTipText()
/* 139:    */   {
/* 140:287 */     return "For instances at the beginning or end of the dataset where the translated values are not known, use missing values (default is to remove those instances)";
/* 141:    */   }
/* 142:    */   
/* 143:    */   public boolean getFillWithMissing()
/* 144:    */   {
/* 145:300 */     return this.m_FillWithMissing;
/* 146:    */   }
/* 147:    */   
/* 148:    */   public void setFillWithMissing(boolean newFillWithMissing)
/* 149:    */   {
/* 150:311 */     this.m_FillWithMissing = newFillWithMissing;
/* 151:    */   }
/* 152:    */   
/* 153:    */   public String instanceRangeTipText()
/* 154:    */   {
/* 155:321 */     return "The number of instances forward/backward to merge values between. A negative number indicates taking values from a past instance.";
/* 156:    */   }
/* 157:    */   
/* 158:    */   public int getInstanceRange()
/* 159:    */   {
/* 160:333 */     return this.m_InstanceRange;
/* 161:    */   }
/* 162:    */   
/* 163:    */   public void setInstanceRange(int newInstanceRange)
/* 164:    */   {
/* 165:344 */     this.m_InstanceRange = newInstanceRange;
/* 166:    */   }
/* 167:    */   
/* 168:    */   public String invertSelectionTipText()
/* 169:    */   {
/* 170:354 */     return "Invert matching sense. ie calculate for all non-specified columns.";
/* 171:    */   }
/* 172:    */   
/* 173:    */   public boolean getInvertSelection()
/* 174:    */   {
/* 175:364 */     return this.m_SelectedCols.getInvert();
/* 176:    */   }
/* 177:    */   
/* 178:    */   public void setInvertSelection(boolean invert)
/* 179:    */   {
/* 180:376 */     this.m_SelectedCols.setInvert(invert);
/* 181:    */   }
/* 182:    */   
/* 183:    */   public String attributeIndicesTipText()
/* 184:    */   {
/* 185:386 */     return "Specify range of attributes to act on. This is a comma separated list of attribute indices, with \"first\" and \"last\" valid values. Specify an inclusive range with \"-\". E.g: \"first-3,5,6-10,last\".";
/* 186:    */   }
/* 187:    */   
/* 188:    */   public String getAttributeIndices()
/* 189:    */   {
/* 190:399 */     return this.m_SelectedCols.getRanges();
/* 191:    */   }
/* 192:    */   
/* 193:    */   public void setAttributeIndices(String rangeList)
/* 194:    */   {
/* 195:412 */     this.m_SelectedCols.setRanges(rangeList);
/* 196:    */   }
/* 197:    */   
/* 198:    */   public void setAttributeIndicesArray(int[] attributes)
/* 199:    */   {
/* 200:424 */     setAttributeIndices(Range.indicesToRangeList(attributes));
/* 201:    */   }
/* 202:    */   
/* 203:    */   protected void resetHistory()
/* 204:    */   {
/* 205:430 */     if (this.m_History == null) {
/* 206:431 */       this.m_History = new Queue();
/* 207:    */     } else {
/* 208:433 */       this.m_History.removeAllElements();
/* 209:    */     }
/* 210:    */   }
/* 211:    */   
/* 212:    */   protected Instance historyInput(Instance instance)
/* 213:    */   {
/* 214:448 */     this.m_History.push(instance);
/* 215:449 */     if (this.m_History.size() <= Math.abs(this.m_InstanceRange))
/* 216:    */     {
/* 217:450 */       if ((getFillWithMissing()) && (this.m_InstanceRange < 0)) {
/* 218:451 */         return mergeInstances(null, instance);
/* 219:    */       }
/* 220:453 */       return null;
/* 221:    */     }
/* 222:456 */     if (this.m_InstanceRange < 0) {
/* 223:457 */       return mergeInstances((Instance)this.m_History.pop(), instance);
/* 224:    */     }
/* 225:459 */     return mergeInstances(instance, (Instance)this.m_History.pop());
/* 226:    */   }
/* 227:    */   
/* 228:    */   protected abstract Instance mergeInstances(Instance paramInstance1, Instance paramInstance2);
/* 229:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.AbstractTimeSeries
 * JD-Core Version:    0.7.0.1
 */