/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import java.util.Vector;
/*   5:    */ import weka.core.Attribute;
/*   6:    */ import weka.core.Capabilities;
/*   7:    */ import weka.core.Capabilities.Capability;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.Option;
/*  11:    */ import weka.core.OptionHandler;
/*  12:    */ import weka.core.RevisionUtils;
/*  13:    */ import weka.core.SingleIndex;
/*  14:    */ import weka.core.Utils;
/*  15:    */ import weka.filters.Filter;
/*  16:    */ import weka.filters.UnsupervisedFilter;
/*  17:    */ 
/*  18:    */ public class AddID
/*  19:    */   extends Filter
/*  20:    */   implements UnsupervisedFilter, OptionHandler
/*  21:    */ {
/*  22:    */   static final long serialVersionUID = 4734383199819293390L;
/*  23: 73 */   protected SingleIndex m_Index = new SingleIndex("first");
/*  24: 76 */   protected String m_Name = "ID";
/*  25: 79 */   protected int m_Counter = -1;
/*  26:    */   
/*  27:    */   public String globalInfo()
/*  28:    */   {
/*  29: 88 */     return "An instance filter that adds an ID attribute to the dataset. The new attribute contains a unique ID for each instance.\nNote: The ID is not reset for the second batch of files (using -b and -r and -s).";
/*  30:    */   }
/*  31:    */   
/*  32:    */   public Enumeration<Option> listOptions()
/*  33:    */   {
/*  34:102 */     Vector<Option> result = new Vector();
/*  35:    */     
/*  36:104 */     result.addElement(new Option("\tSpecify where to insert the ID. First and last\n\tare valid indexes.(default first)", "C", 1, "-C <index>"));
/*  37:    */     
/*  38:    */ 
/*  39:    */ 
/*  40:108 */     result.addElement(new Option("\tName of the new attribute.\n\t(default = 'ID')", "N", 1, "-N <name>"));
/*  41:    */     
/*  42:    */ 
/*  43:111 */     return result.elements();
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void setOptions(String[] options)
/*  47:    */     throws Exception
/*  48:    */   {
/*  49:142 */     String tmpStr = Utils.getOption('C', options);
/*  50:143 */     if (tmpStr.length() != 0) {
/*  51:144 */       this.m_Index.setSingleIndex(tmpStr);
/*  52:    */     } else {
/*  53:146 */       this.m_Index.setSingleIndex("first");
/*  54:    */     }
/*  55:149 */     tmpStr = Utils.getOption('N', options);
/*  56:150 */     if (tmpStr.length() != 0) {
/*  57:151 */       this.m_Name = tmpStr;
/*  58:    */     } else {
/*  59:153 */       this.m_Name = "ID";
/*  60:    */     }
/*  61:156 */     if (getInputFormat() != null) {
/*  62:157 */       setInputFormat(getInputFormat());
/*  63:    */     }
/*  64:160 */     Utils.checkForRemainingOptions(options);
/*  65:    */   }
/*  66:    */   
/*  67:    */   public String[] getOptions()
/*  68:    */   {
/*  69:171 */     Vector<String> result = new Vector();
/*  70:    */     
/*  71:173 */     result.add("-C");
/*  72:174 */     result.add(getIDIndex());
/*  73:    */     
/*  74:176 */     result.add("-N");
/*  75:177 */     result.add(getAttributeName());
/*  76:    */     
/*  77:179 */     return (String[])result.toArray(new String[result.size()]);
/*  78:    */   }
/*  79:    */   
/*  80:    */   public String attributeNameTipText()
/*  81:    */   {
/*  82:189 */     return "Set the new attribute's name.";
/*  83:    */   }
/*  84:    */   
/*  85:    */   public String getAttributeName()
/*  86:    */   {
/*  87:198 */     return this.m_Name;
/*  88:    */   }
/*  89:    */   
/*  90:    */   public void setAttributeName(String value)
/*  91:    */   {
/*  92:207 */     this.m_Name = value;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public String IDIndexTipText()
/*  96:    */   {
/*  97:217 */     return "The position (starting from 1) where the attribute will be inserted (first and last are valid indices).";
/*  98:    */   }
/*  99:    */   
/* 100:    */   public String getIDIndex()
/* 101:    */   {
/* 102:227 */     return this.m_Index.getSingleIndex();
/* 103:    */   }
/* 104:    */   
/* 105:    */   public void setIDIndex(String value)
/* 106:    */   {
/* 107:236 */     this.m_Index.setSingleIndex(value);
/* 108:    */   }
/* 109:    */   
/* 110:    */   public Capabilities getCapabilities()
/* 111:    */   {
/* 112:247 */     Capabilities result = super.getCapabilities();
/* 113:248 */     result.disableAll();
/* 114:    */     
/* 115:    */ 
/* 116:251 */     result.enableAllAttributes();
/* 117:252 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 118:    */     
/* 119:    */ 
/* 120:255 */     result.enableAllClasses();
/* 121:256 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 122:257 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 123:    */     
/* 124:259 */     return result;
/* 125:    */   }
/* 126:    */   
/* 127:    */   public boolean setInputFormat(Instances instanceInfo)
/* 128:    */     throws Exception
/* 129:    */   {
/* 130:276 */     super.setInputFormat(instanceInfo);
/* 131:    */     
/* 132:278 */     this.m_Counter = -1;
/* 133:279 */     this.m_Index.setUpper(instanceInfo.numAttributes());
/* 134:280 */     Instances outputFormat = new Instances(instanceInfo, 0);
/* 135:281 */     Attribute newAttribute = new Attribute(this.m_Name);
/* 136:283 */     if ((this.m_Index.getIndex() < 0) || (this.m_Index.getIndex() > getInputFormat().numAttributes())) {
/* 137:285 */       throw new IllegalArgumentException("Index out of range");
/* 138:    */     }
/* 139:288 */     outputFormat.insertAttributeAt(newAttribute, this.m_Index.getIndex());
/* 140:289 */     setOutputFormat(outputFormat);
/* 141:    */     
/* 142:291 */     return true;
/* 143:    */   }
/* 144:    */   
/* 145:    */   public boolean input(Instance instance)
/* 146:    */   {
/* 147:304 */     if (getInputFormat() == null) {
/* 148:305 */       throw new IllegalStateException("No input instance format defined");
/* 149:    */     }
/* 150:308 */     if (this.m_NewBatch)
/* 151:    */     {
/* 152:309 */       resetQueue();
/* 153:310 */       this.m_NewBatch = false;
/* 154:    */     }
/* 155:313 */     if (!isFirstBatchDone())
/* 156:    */     {
/* 157:314 */       bufferInput(instance);
/* 158:315 */       return false;
/* 159:    */     }
/* 160:317 */     convertInstance(instance);
/* 161:318 */     return true;
/* 162:    */   }
/* 163:    */   
/* 164:    */   public boolean batchFinished()
/* 165:    */   {
/* 166:332 */     if (getInputFormat() == null) {
/* 167:333 */       throw new IllegalStateException("No input instance format defined");
/* 168:    */     }
/* 169:336 */     if (!isFirstBatchDone())
/* 170:    */     {
/* 171:337 */       this.m_Counter = 0;
/* 172:340 */       for (int i = 0; i < getInputFormat().numInstances(); i++) {
/* 173:341 */         convertInstance(getInputFormat().instance(i));
/* 174:    */       }
/* 175:    */     }
/* 176:346 */     flushInput();
/* 177:    */     
/* 178:348 */     this.m_NewBatch = true;
/* 179:349 */     this.m_FirstBatchDone = true;
/* 180:    */     
/* 181:351 */     return numPendingOutput() != 0;
/* 182:    */   }
/* 183:    */   
/* 184:    */   protected void convertInstance(Instance instance)
/* 185:    */   {
/* 186:363 */     this.m_Counter += 1;
/* 187:    */     try
/* 188:    */     {
/* 189:367 */       Instance inst = (Instance)instance.copy();
/* 190:    */       
/* 191:    */ 
/* 192:370 */       copyValues(inst, true, inst.dataset(), outputFormatPeek());
/* 193:    */       
/* 194:    */ 
/* 195:373 */       inst.setDataset(null);
/* 196:374 */       inst.insertAttributeAt(this.m_Index.getIndex());
/* 197:375 */       inst.setValue(this.m_Index.getIndex(), this.m_Counter);
/* 198:    */       
/* 199:377 */       push(inst);
/* 200:    */     }
/* 201:    */     catch (Exception e)
/* 202:    */     {
/* 203:379 */       e.printStackTrace();
/* 204:    */     }
/* 205:    */   }
/* 206:    */   
/* 207:    */   public String getRevision()
/* 208:    */   {
/* 209:390 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 210:    */   }
/* 211:    */   
/* 212:    */   public static void main(String[] args)
/* 213:    */   {
/* 214:399 */     runFilter(new AddID(), args);
/* 215:    */   }
/* 216:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.AddID
 * JD-Core Version:    0.7.0.1
 */