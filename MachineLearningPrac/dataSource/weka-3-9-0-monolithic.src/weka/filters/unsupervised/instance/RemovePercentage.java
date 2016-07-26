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
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ import weka.core.Utils;
/*  13:    */ import weka.filters.Filter;
/*  14:    */ import weka.filters.UnsupervisedFilter;
/*  15:    */ 
/*  16:    */ public class RemovePercentage
/*  17:    */   extends Filter
/*  18:    */   implements UnsupervisedFilter, OptionHandler
/*  19:    */ {
/*  20:    */   static final long serialVersionUID = 2150341191158533133L;
/*  21: 70 */   private double m_Percentage = 50.0D;
/*  22: 73 */   private boolean m_Inverse = false;
/*  23:    */   
/*  24:    */   public Enumeration<Option> listOptions()
/*  25:    */   {
/*  26: 83 */     Vector<Option> newVector = new Vector(2);
/*  27:    */     
/*  28: 85 */     newVector.addElement(new Option("\tSpecifies percentage of instances to select. (default 50)\n", "P", 1, "-P <percentage>"));
/*  29:    */     
/*  30:    */ 
/*  31:    */ 
/*  32: 89 */     newVector.addElement(new Option("\tSpecifies if inverse of selection is to be output.\n", "V", 0, "-V"));
/*  33:    */     
/*  34:    */ 
/*  35: 92 */     return newVector.elements();
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void setOptions(String[] options)
/*  39:    */     throws Exception
/*  40:    */   {
/*  41:120 */     String percent = Utils.getOption('P', options);
/*  42:121 */     if (percent.length() != 0) {
/*  43:122 */       setPercentage(Double.parseDouble(percent));
/*  44:    */     } else {
/*  45:124 */       setPercentage(50.0D);
/*  46:    */     }
/*  47:126 */     setInvertSelection(Utils.getFlag('V', options));
/*  48:128 */     if (getInputFormat() != null) {
/*  49:129 */       setInputFormat(getInputFormat());
/*  50:    */     }
/*  51:132 */     Utils.checkForRemainingOptions(options);
/*  52:    */   }
/*  53:    */   
/*  54:    */   public String[] getOptions()
/*  55:    */   {
/*  56:143 */     Vector<String> options = new Vector();
/*  57:    */     
/*  58:145 */     options.add("-P");
/*  59:146 */     options.add("" + getPercentage());
/*  60:147 */     if (getInvertSelection()) {
/*  61:148 */       options.add("-V");
/*  62:    */     }
/*  63:151 */     return (String[])options.toArray(new String[0]);
/*  64:    */   }
/*  65:    */   
/*  66:    */   public String globalInfo()
/*  67:    */   {
/*  68:162 */     return "A filter that removes a given percentage of a dataset.";
/*  69:    */   }
/*  70:    */   
/*  71:    */   public String percentageTipText()
/*  72:    */   {
/*  73:173 */     return "The percentage of the data to select.";
/*  74:    */   }
/*  75:    */   
/*  76:    */   public double getPercentage()
/*  77:    */   {
/*  78:183 */     return this.m_Percentage;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void setPercentage(double percent)
/*  82:    */   {
/*  83:194 */     if ((percent < 0.0D) || (percent > 100.0D)) {
/*  84:195 */       throw new IllegalArgumentException("Percentage must be between 0 and 100.");
/*  85:    */     }
/*  86:198 */     this.m_Percentage = percent;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public String invertSelectionTipText()
/*  90:    */   {
/*  91:209 */     return "Whether to invert the selection.";
/*  92:    */   }
/*  93:    */   
/*  94:    */   public boolean getInvertSelection()
/*  95:    */   {
/*  96:219 */     return this.m_Inverse;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public void setInvertSelection(boolean inverse)
/* 100:    */   {
/* 101:229 */     this.m_Inverse = inverse;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public Capabilities getCapabilities()
/* 105:    */   {
/* 106:240 */     Capabilities result = super.getCapabilities();
/* 107:241 */     result.disableAll();
/* 108:    */     
/* 109:    */ 
/* 110:244 */     result.enableAllAttributes();
/* 111:245 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 112:    */     
/* 113:    */ 
/* 114:248 */     result.enableAllClasses();
/* 115:249 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 116:250 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 117:    */     
/* 118:252 */     return result;
/* 119:    */   }
/* 120:    */   
/* 121:    */   public boolean setInputFormat(Instances instanceInfo)
/* 122:    */     throws Exception
/* 123:    */   {
/* 124:267 */     super.setInputFormat(instanceInfo);
/* 125:268 */     setOutputFormat(instanceInfo);
/* 126:269 */     return true;
/* 127:    */   }
/* 128:    */   
/* 129:    */   public boolean input(Instance instance)
/* 130:    */   {
/* 131:283 */     if (getInputFormat() == null) {
/* 132:284 */       throw new IllegalStateException("No input instance format defined");
/* 133:    */     }
/* 134:287 */     if (this.m_NewBatch)
/* 135:    */     {
/* 136:288 */       resetQueue();
/* 137:289 */       this.m_NewBatch = false;
/* 138:    */     }
/* 139:292 */     if (isFirstBatchDone())
/* 140:    */     {
/* 141:293 */       push(instance);
/* 142:294 */       return true;
/* 143:    */     }
/* 144:296 */     bufferInput(instance);
/* 145:297 */     return false;
/* 146:    */   }
/* 147:    */   
/* 148:    */   public boolean batchFinished()
/* 149:    */   {
/* 150:311 */     if (getInputFormat() == null) {
/* 151:312 */       throw new IllegalStateException("No input instance format defined");
/* 152:    */     }
/* 153:316 */     Instances toFilter = getInputFormat();
/* 154:317 */     int cutOff = (int)Math.round(toFilter.numInstances() * this.m_Percentage / 100.0D);
/* 155:319 */     if (this.m_Inverse) {
/* 156:320 */       for (int i = 0; i < cutOff; i++) {
/* 157:321 */         push(toFilter.instance(i), false);
/* 158:    */       }
/* 159:    */     } else {
/* 160:324 */       for (int i = cutOff; i < toFilter.numInstances(); i++) {
/* 161:325 */         push(toFilter.instance(i), false);
/* 162:    */       }
/* 163:    */     }
/* 164:328 */     flushInput();
/* 165:    */     
/* 166:330 */     this.m_NewBatch = true;
/* 167:331 */     this.m_FirstBatchDone = true;
/* 168:    */     
/* 169:333 */     return numPendingOutput() != 0;
/* 170:    */   }
/* 171:    */   
/* 172:    */   public String getRevision()
/* 173:    */   {
/* 174:343 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 175:    */   }
/* 176:    */   
/* 177:    */   public static void main(String[] argv)
/* 178:    */   {
/* 179:352 */     runFilter(new RemovePercentage(), argv);
/* 180:    */   }
/* 181:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.instance.RemovePercentage
 * JD-Core Version:    0.7.0.1
 */