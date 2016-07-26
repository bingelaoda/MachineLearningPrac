/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Attribute;
/*   7:    */ import weka.core.Capabilities;
/*   8:    */ import weka.core.Capabilities.Capability;
/*   9:    */ import weka.core.Instance;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.Option;
/*  12:    */ import weka.core.OptionHandler;
/*  13:    */ import weka.core.Range;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.Utils;
/*  16:    */ import weka.filters.Filter;
/*  17:    */ import weka.filters.UnsupervisedFilter;
/*  18:    */ 
/*  19:    */ public class NominalToString
/*  20:    */   extends Filter
/*  21:    */   implements UnsupervisedFilter, OptionHandler
/*  22:    */ {
/*  23:    */   static final long serialVersionUID = 8655492378380068939L;
/*  24: 68 */   private final Range m_AttIndex = new Range("last");
/*  25:    */   
/*  26:    */   public String globalInfo()
/*  27:    */   {
/*  28: 77 */     return "Converts a nominal attribute (that is, a set number of values) to string (that is, an unspecified number of values).";
/*  29:    */   }
/*  30:    */   
/*  31:    */   public Capabilities getCapabilities()
/*  32:    */   {
/*  33: 89 */     Capabilities result = super.getCapabilities();
/*  34: 90 */     result.disableAll();
/*  35:    */     
/*  36:    */ 
/*  37: 93 */     result.enableAllAttributes();
/*  38: 94 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  39:    */     
/*  40:    */ 
/*  41: 97 */     result.enableAllClasses();
/*  42: 98 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  43: 99 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  44:    */     
/*  45:101 */     return result;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public boolean setInputFormat(Instances instanceInfo)
/*  49:    */     throws Exception
/*  50:    */   {
/*  51:117 */     super.setInputFormat(instanceInfo);
/*  52:    */     
/*  53:119 */     this.m_AttIndex.setUpper(instanceInfo.numAttributes() - 1);
/*  54:    */     
/*  55:    */ 
/*  56:    */ 
/*  57:    */ 
/*  58:    */ 
/*  59:    */ 
/*  60:    */ 
/*  61:127 */     return false;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public boolean input(Instance instance)
/*  65:    */   {
/*  66:140 */     if (getInputFormat() == null) {
/*  67:141 */       throw new IllegalStateException("No input instance format defined");
/*  68:    */     }
/*  69:144 */     if (this.m_NewBatch)
/*  70:    */     {
/*  71:145 */       resetQueue();
/*  72:146 */       this.m_NewBatch = false;
/*  73:    */     }
/*  74:149 */     if (isOutputFormatDefined())
/*  75:    */     {
/*  76:150 */       Instance newInstance = (Instance)instance.copy();
/*  77:151 */       push(newInstance, false);
/*  78:152 */       return true;
/*  79:    */     }
/*  80:155 */     bufferInput(instance);
/*  81:156 */     return false;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public boolean batchFinished()
/*  85:    */   {
/*  86:169 */     if (getInputFormat() == null) {
/*  87:170 */       throw new IllegalStateException("No input instance format defined");
/*  88:    */     }
/*  89:173 */     if (!isOutputFormatDefined())
/*  90:    */     {
/*  91:174 */       setOutputFormat();
/*  92:177 */       for (int i = 0; i < getInputFormat().numInstances(); i++) {
/*  93:178 */         push((Instance)getInputFormat().instance(i).copy(), false);
/*  94:    */       }
/*  95:    */     }
/*  96:182 */     flushInput();
/*  97:183 */     this.m_NewBatch = true;
/*  98:    */     
/*  99:185 */     return numPendingOutput() != 0;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public Enumeration<Option> listOptions()
/* 103:    */   {
/* 104:196 */     Vector<Option> result = new Vector();
/* 105:    */     
/* 106:198 */     result.addElement(new Option("\tSets the range of attributes to convert (default last).", "C", 1, "-C <col>"));
/* 107:    */     
/* 108:    */ 
/* 109:    */ 
/* 110:202 */     return result.elements();
/* 111:    */   }
/* 112:    */   
/* 113:    */   public void setOptions(String[] options)
/* 114:    */     throws Exception
/* 115:    */   {
/* 116:225 */     String tmpStr = Utils.getOption('C', options);
/* 117:226 */     if (tmpStr.length() != 0) {
/* 118:227 */       setAttributeIndexes(tmpStr);
/* 119:    */     } else {
/* 120:229 */       setAttributeIndexes("last");
/* 121:    */     }
/* 122:232 */     if (getInputFormat() != null) {
/* 123:233 */       setInputFormat(getInputFormat());
/* 124:    */     }
/* 125:236 */     Utils.checkForRemainingOptions(options);
/* 126:    */   }
/* 127:    */   
/* 128:    */   public String[] getOptions()
/* 129:    */   {
/* 130:247 */     Vector<String> result = new Vector();
/* 131:    */     
/* 132:249 */     result.add("-C");
/* 133:250 */     result.add("" + getAttributeIndexes());
/* 134:    */     
/* 135:252 */     return (String[])result.toArray(new String[result.size()]);
/* 136:    */   }
/* 137:    */   
/* 138:    */   public String attributeIndexesTipText()
/* 139:    */   {
/* 140:262 */     return "Sets a range attributes to process. Any non-nominal attributes in the range are left untouched (\"first\" and \"last\" are valid values)";
/* 141:    */   }
/* 142:    */   
/* 143:    */   public String getAttributeIndexes()
/* 144:    */   {
/* 145:273 */     return this.m_AttIndex.getRanges();
/* 146:    */   }
/* 147:    */   
/* 148:    */   public void setAttributeIndexes(String attIndex)
/* 149:    */   {
/* 150:283 */     this.m_AttIndex.setRanges(attIndex);
/* 151:    */   }
/* 152:    */   
/* 153:    */   private void setOutputFormat()
/* 154:    */   {
/* 155:292 */     ArrayList<Attribute> newAtts = new ArrayList(getInputFormat().numAttributes());
/* 156:294 */     for (int j = 0; j < getInputFormat().numAttributes(); j++)
/* 157:    */     {
/* 158:295 */       Attribute att = getInputFormat().attribute(j);
/* 159:297 */       if ((!att.isNominal()) || (!this.m_AttIndex.isInRange(j)))
/* 160:    */       {
/* 161:298 */         newAtts.add(att);
/* 162:    */       }
/* 163:    */       else
/* 164:    */       {
/* 165:300 */         Attribute newAtt = new Attribute(att.name(), (ArrayList)null);
/* 166:301 */         newAtt.setWeight(getInputFormat().attribute(j).weight());
/* 167:302 */         newAtts.add(newAtt);
/* 168:    */       }
/* 169:    */     }
/* 170:307 */     Instances newData = new Instances(getInputFormat().relationName(), newAtts, 0);
/* 171:308 */     newData.setClassIndex(getInputFormat().classIndex());
/* 172:    */     
/* 173:310 */     setOutputFormat(newData);
/* 174:    */   }
/* 175:    */   
/* 176:    */   public String getRevision()
/* 177:    */   {
/* 178:320 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 179:    */   }
/* 180:    */   
/* 181:    */   public static void main(String[] args)
/* 182:    */   {
/* 183:329 */     runFilter(new NominalToString(), args);
/* 184:    */   }
/* 185:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.NominalToString
 * JD-Core Version:    0.7.0.1
 */