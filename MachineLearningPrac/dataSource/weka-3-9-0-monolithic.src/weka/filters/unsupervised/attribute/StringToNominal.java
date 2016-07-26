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
/*  19:    */ public class StringToNominal
/*  20:    */   extends Filter
/*  21:    */   implements UnsupervisedFilter, OptionHandler
/*  22:    */ {
/*  23:    */   private static final long serialVersionUID = 4864084427902797605L;
/*  24: 75 */   private final Range m_AttIndices = new Range("last");
/*  25:    */   
/*  26:    */   public String globalInfo()
/*  27:    */   {
/*  28: 85 */     return "Converts a range of string attributes (unspecified number of values) to nominal (set number of values). You should ensure that all string values that will appear are represented in the first batch of the data.";
/*  29:    */   }
/*  30:    */   
/*  31:    */   public Capabilities getCapabilities()
/*  32:    */   {
/*  33: 98 */     Capabilities result = super.getCapabilities();
/*  34: 99 */     result.disableAll();
/*  35:    */     
/*  36:    */ 
/*  37:102 */     result.enableAllAttributes();
/*  38:103 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  39:    */     
/*  40:    */ 
/*  41:106 */     result.enableAllClasses();
/*  42:107 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  43:108 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  44:    */     
/*  45:110 */     return result;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public boolean setInputFormat(Instances instanceInfo)
/*  49:    */     throws Exception
/*  50:    */   {
/*  51:127 */     super.setInputFormat(instanceInfo);
/*  52:128 */     this.m_AttIndices.setUpper(instanceInfo.numAttributes() - 1);
/*  53:129 */     return false;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public boolean input(Instance instance)
/*  57:    */   {
/*  58:143 */     if (getInputFormat() == null) {
/*  59:144 */       throw new IllegalStateException("No input instance format defined");
/*  60:    */     }
/*  61:146 */     if (this.m_NewBatch)
/*  62:    */     {
/*  63:147 */       resetQueue();
/*  64:148 */       this.m_NewBatch = false;
/*  65:    */     }
/*  66:151 */     if (isOutputFormatDefined())
/*  67:    */     {
/*  68:152 */       Instance newInstance = (Instance)instance.copy();
/*  69:156 */       for (int i = 0; i < newInstance.numAttributes(); i++) {
/*  70:157 */         if ((newInstance.attribute(i).isString()) && (!newInstance.isMissing(i)) && (this.m_AttIndices.isInRange(i)))
/*  71:    */         {
/*  72:159 */           Attribute outAtt = getOutputFormat().attribute(newInstance.attribute(i).name());
/*  73:    */           
/*  74:161 */           String inVal = newInstance.stringValue(i);
/*  75:162 */           int outIndex = outAtt.indexOfValue(inVal);
/*  76:163 */           if (outIndex < 0) {
/*  77:164 */             newInstance.setMissing(i);
/*  78:    */           } else {
/*  79:166 */             newInstance.setValue(i, outIndex);
/*  80:    */           }
/*  81:    */         }
/*  82:    */       }
/*  83:170 */       push(newInstance, false);
/*  84:171 */       return true;
/*  85:    */     }
/*  86:174 */     bufferInput(instance);
/*  87:175 */     return false;
/*  88:    */   }
/*  89:    */   
/*  90:    */   public boolean batchFinished()
/*  91:    */   {
/*  92:189 */     if (getInputFormat() == null) {
/*  93:190 */       throw new IllegalStateException("No input instance format defined");
/*  94:    */     }
/*  95:192 */     if (!isOutputFormatDefined())
/*  96:    */     {
/*  97:194 */       setOutputFormat();
/*  98:197 */       for (int i = 0; i < getInputFormat().numInstances(); i++) {
/*  99:198 */         push((Instance)getInputFormat().instance(i).copy(), false);
/* 100:    */       }
/* 101:    */     }
/* 102:202 */     flushInput();
/* 103:203 */     this.m_NewBatch = true;
/* 104:204 */     return numPendingOutput() != 0;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public Enumeration<Option> listOptions()
/* 108:    */   {
/* 109:215 */     Vector<Option> newVector = new Vector(1);
/* 110:    */     
/* 111:217 */     newVector.addElement(new Option("\tSets the range of attribute indices (default last).", "R", 1, "-R <col>"));
/* 112:    */     
/* 113:    */ 
/* 114:    */ 
/* 115:221 */     newVector.addElement(new Option("\tInvert the range specified by -R.", "V", 1, "-V <col>"));
/* 116:    */     
/* 117:    */ 
/* 118:224 */     return newVector.elements();
/* 119:    */   }
/* 120:    */   
/* 121:    */   public void setOptions(String[] options)
/* 122:    */     throws Exception
/* 123:    */   {
/* 124:252 */     String attIndices = Utils.getOption('R', options);
/* 125:253 */     if (attIndices.length() != 0) {
/* 126:254 */       setAttributeRange(attIndices);
/* 127:    */     } else {
/* 128:256 */       setAttributeRange("last");
/* 129:    */     }
/* 130:259 */     String invertSelection = Utils.getOption('V', options);
/* 131:260 */     if (invertSelection.length() != 0) {
/* 132:261 */       this.m_AttIndices.setInvert(true);
/* 133:    */     } else {
/* 134:263 */       this.m_AttIndices.setInvert(false);
/* 135:    */     }
/* 136:266 */     if (getInputFormat() != null) {
/* 137:267 */       setInputFormat(getInputFormat());
/* 138:    */     }
/* 139:270 */     Utils.checkForRemainingOptions(options);
/* 140:    */   }
/* 141:    */   
/* 142:    */   public String[] getOptions()
/* 143:    */   {
/* 144:281 */     Vector<String> options = new Vector();
/* 145:    */     
/* 146:283 */     options.add("-R");
/* 147:284 */     options.add("" + getAttributeRange());
/* 148:286 */     if (this.m_AttIndices.getInvert()) {
/* 149:287 */       options.add("-V");
/* 150:    */     }
/* 151:290 */     return (String[])options.toArray(new String[0]);
/* 152:    */   }
/* 153:    */   
/* 154:    */   public String attributeRangeTipText()
/* 155:    */   {
/* 156:299 */     return "Sets which attributes to process. This attributes must be string attributes (\"first\" and \"last\" are valid values as well as ranges and lists)";
/* 157:    */   }
/* 158:    */   
/* 159:    */   public String getAttributeRange()
/* 160:    */   {
/* 161:311 */     return this.m_AttIndices.getRanges();
/* 162:    */   }
/* 163:    */   
/* 164:    */   public void setAttributeRange(String rangeList)
/* 165:    */   {
/* 166:321 */     this.m_AttIndices.setRanges(rangeList);
/* 167:    */   }
/* 168:    */   
/* 169:    */   private void setOutputFormat()
/* 170:    */   {
/* 171:336 */     ArrayList<Attribute> newAtts = new ArrayList(getInputFormat().numAttributes());
/* 172:337 */     for (int j = 0; j < getInputFormat().numAttributes(); j++)
/* 173:    */     {
/* 174:338 */       Attribute att = getInputFormat().attribute(j);
/* 175:339 */       if ((!this.m_AttIndices.isInRange(j)) || (!att.isString()))
/* 176:    */       {
/* 177:343 */         newAtts.add(att);
/* 178:    */       }
/* 179:    */       else
/* 180:    */       {
/* 181:347 */         ArrayList<String> newVals = new ArrayList(att.numValues());
/* 182:348 */         for (int i = 0; i < att.numValues(); i++) {
/* 183:349 */           newVals.add(att.value(i));
/* 184:    */         }
/* 185:351 */         Attribute newAtt = new Attribute(att.name(), newVals);
/* 186:352 */         newAtt.setWeight(att.weight());
/* 187:353 */         newAtts.add(newAtt);
/* 188:    */       }
/* 189:    */     }
/* 190:358 */     Instances newData = new Instances(getInputFormat().relationName(), newAtts, 0);
/* 191:359 */     newData.setClassIndex(getInputFormat().classIndex());
/* 192:360 */     setOutputFormat(newData);
/* 193:    */   }
/* 194:    */   
/* 195:    */   public String getRevision()
/* 196:    */   {
/* 197:370 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 198:    */   }
/* 199:    */   
/* 200:    */   public static void main(String[] argv)
/* 201:    */   {
/* 202:379 */     runFilter(new StringToNominal(), argv);
/* 203:    */   }
/* 204:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.StringToNominal
 * JD-Core Version:    0.7.0.1
 */