/*   1:    */ package weka.filters.unsupervised.instance;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import java.util.Vector;
/*   5:    */ import weka.core.Capabilities;
/*   6:    */ import weka.core.Capabilities.Capability;
/*   7:    */ import weka.core.Instance;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.Option;
/*  10:    */ import weka.core.OptionHandler;
/*  11:    */ import weka.core.Range;
/*  12:    */ import weka.core.RevisionUtils;
/*  13:    */ import weka.core.Utils;
/*  14:    */ import weka.filters.Filter;
/*  15:    */ import weka.filters.UnsupervisedFilter;
/*  16:    */ 
/*  17:    */ public class RemoveRange
/*  18:    */   extends Filter
/*  19:    */   implements UnsupervisedFilter, OptionHandler
/*  20:    */ {
/*  21:    */   static final long serialVersionUID = -3064641215340828695L;
/*  22: 71 */   private final Range m_Range = new Range("first-last");
/*  23:    */   
/*  24:    */   public Enumeration<Option> listOptions()
/*  25:    */   {
/*  26: 81 */     Vector<Option> newVector = new Vector(2);
/*  27:    */     
/*  28: 83 */     newVector.addElement(new Option("\tSpecifies list of instances to select. First and last\n\tare valid indexes. (required)\n", "R", 1, "-R <inst1,inst2-inst4,...>"));
/*  29:    */     
/*  30:    */ 
/*  31:    */ 
/*  32:    */ 
/*  33: 88 */     newVector.addElement(new Option("\tSpecifies if inverse of selection is to be output.\n", "V", 0, "-V"));
/*  34:    */     
/*  35:    */ 
/*  36: 91 */     return newVector.elements();
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void setOptions(String[] options)
/*  40:    */     throws Exception
/*  41:    */   {
/*  42:120 */     String str = Utils.getOption('R', options);
/*  43:121 */     if (str.length() != 0) {
/*  44:122 */       setInstancesIndices(str);
/*  45:    */     } else {
/*  46:124 */       setInstancesIndices("first-last");
/*  47:    */     }
/*  48:126 */     setInvertSelection(Utils.getFlag('V', options));
/*  49:128 */     if (getInputFormat() != null) {
/*  50:129 */       setInputFormat(getInputFormat());
/*  51:    */     }
/*  52:132 */     Utils.checkForRemainingOptions(options);
/*  53:    */   }
/*  54:    */   
/*  55:    */   public String[] getOptions()
/*  56:    */   {
/*  57:143 */     Vector<String> options = new Vector();
/*  58:145 */     if (getInvertSelection()) {
/*  59:146 */       options.add("-V");
/*  60:    */     }
/*  61:148 */     options.add("-R");
/*  62:149 */     options.add(getInstancesIndices());
/*  63:    */     
/*  64:151 */     return (String[])options.toArray(new String[0]);
/*  65:    */   }
/*  66:    */   
/*  67:    */   public String globalInfo()
/*  68:    */   {
/*  69:161 */     return "A filter that removes a given range of instances of a dataset.";
/*  70:    */   }
/*  71:    */   
/*  72:    */   public String instancesIndicesTipText()
/*  73:    */   {
/*  74:172 */     return "The range of instances to select. First and last are valid indexes.";
/*  75:    */   }
/*  76:    */   
/*  77:    */   public String getInstancesIndices()
/*  78:    */   {
/*  79:182 */     return this.m_Range.getRanges();
/*  80:    */   }
/*  81:    */   
/*  82:    */   public void setInstancesIndices(String rangeList)
/*  83:    */   {
/*  84:195 */     this.m_Range.setRanges(rangeList);
/*  85:    */   }
/*  86:    */   
/*  87:    */   public String invertSelectionTipText()
/*  88:    */   {
/*  89:206 */     return "Whether to invert the selection.";
/*  90:    */   }
/*  91:    */   
/*  92:    */   public boolean getInvertSelection()
/*  93:    */   {
/*  94:216 */     return this.m_Range.getInvert();
/*  95:    */   }
/*  96:    */   
/*  97:    */   public void setInvertSelection(boolean inverse)
/*  98:    */   {
/*  99:226 */     this.m_Range.setInvert(inverse);
/* 100:    */   }
/* 101:    */   
/* 102:    */   public Capabilities getCapabilities()
/* 103:    */   {
/* 104:237 */     Capabilities result = super.getCapabilities();
/* 105:238 */     result.disableAll();
/* 106:    */     
/* 107:    */ 
/* 108:241 */     result.enableAllAttributes();
/* 109:242 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 110:    */     
/* 111:    */ 
/* 112:245 */     result.enableAllClasses();
/* 113:246 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 114:247 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 115:    */     
/* 116:249 */     return result;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public boolean setInputFormat(Instances instanceInfo)
/* 120:    */     throws Exception
/* 121:    */   {
/* 122:264 */     super.setInputFormat(instanceInfo);
/* 123:265 */     setOutputFormat(instanceInfo);
/* 124:266 */     return true;
/* 125:    */   }
/* 126:    */   
/* 127:    */   public boolean input(Instance instance)
/* 128:    */   {
/* 129:280 */     if (getInputFormat() == null) {
/* 130:281 */       throw new IllegalStateException("No input instance format defined");
/* 131:    */     }
/* 132:283 */     if (this.m_NewBatch)
/* 133:    */     {
/* 134:284 */       resetQueue();
/* 135:285 */       this.m_NewBatch = false;
/* 136:    */     }
/* 137:287 */     if (isFirstBatchDone())
/* 138:    */     {
/* 139:288 */       push(instance);
/* 140:289 */       return true;
/* 141:    */     }
/* 142:291 */     bufferInput(instance);
/* 143:292 */     return false;
/* 144:    */   }
/* 145:    */   
/* 146:    */   public boolean batchFinished()
/* 147:    */   {
/* 148:306 */     if (getInputFormat() == null) {
/* 149:307 */       throw new IllegalStateException("No input instance format defined");
/* 150:    */     }
/* 151:310 */     if (!isFirstBatchDone())
/* 152:    */     {
/* 153:312 */       this.m_Range.setUpper(getInputFormat().numInstances() - 1);
/* 154:313 */       for (int i = 0; i < getInputFormat().numInstances(); i++) {
/* 155:314 */         if (!this.m_Range.isInRange(i)) {
/* 156:315 */           push(getInputFormat().instance(i), false);
/* 157:    */         }
/* 158:    */       }
/* 159:    */     }
/* 160:    */     else
/* 161:    */     {
/* 162:319 */       for (int i = 0; i < getInputFormat().numInstances(); i++) {
/* 163:320 */         push(getInputFormat().instance(i), false);
/* 164:    */       }
/* 165:    */     }
/* 166:324 */     flushInput();
/* 167:    */     
/* 168:326 */     this.m_NewBatch = true;
/* 169:327 */     this.m_FirstBatchDone = true;
/* 170:    */     
/* 171:329 */     return numPendingOutput() != 0;
/* 172:    */   }
/* 173:    */   
/* 174:    */   public String getRevision()
/* 175:    */   {
/* 176:339 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 177:    */   }
/* 178:    */   
/* 179:    */   public static void main(String[] argv)
/* 180:    */   {
/* 181:348 */     runFilter(new RemoveRange(), argv);
/* 182:    */   }
/* 183:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.instance.RemoveRange
 * JD-Core Version:    0.7.0.1
 */