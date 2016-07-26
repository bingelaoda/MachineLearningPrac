/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import java.util.Vector;
/*   5:    */ import weka.core.Attribute;
/*   6:    */ import weka.core.AttributeStats;
/*   7:    */ import weka.core.Capabilities;
/*   8:    */ import weka.core.Capabilities.Capability;
/*   9:    */ import weka.core.Instance;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.Option;
/*  12:    */ import weka.core.OptionHandler;
/*  13:    */ import weka.core.RevisionUtils;
/*  14:    */ import weka.core.Utils;
/*  15:    */ import weka.filters.Filter;
/*  16:    */ import weka.filters.UnsupervisedFilter;
/*  17:    */ 
/*  18:    */ public class RemoveUseless
/*  19:    */   extends Filter
/*  20:    */   implements UnsupervisedFilter, OptionHandler
/*  21:    */ {
/*  22:    */   static final long serialVersionUID = -8659417851407640038L;
/*  23: 67 */   protected Remove m_removeFilter = null;
/*  24: 70 */   protected double m_maxVariancePercentage = 99.0D;
/*  25:    */   
/*  26:    */   public Capabilities getCapabilities()
/*  27:    */   {
/*  28: 80 */     Capabilities result = super.getCapabilities();
/*  29:    */     
/*  30:    */ 
/*  31: 83 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  32: 84 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  33: 85 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  34: 86 */     result.enable(Capabilities.Capability.STRING_ATTRIBUTES);
/*  35: 87 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  36:    */     
/*  37:    */ 
/*  38: 90 */     result.enableAllClasses();
/*  39: 91 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  40: 92 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  41:    */     
/*  42: 94 */     return result;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public boolean setInputFormat(Instances instanceInfo)
/*  46:    */     throws Exception
/*  47:    */   {
/*  48:109 */     super.setInputFormat(instanceInfo);
/*  49:110 */     this.m_removeFilter = null;
/*  50:111 */     return false;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public boolean input(Instance instance)
/*  54:    */   {
/*  55:123 */     if (getInputFormat() == null) {
/*  56:124 */       throw new IllegalStateException("No input instance format defined");
/*  57:    */     }
/*  58:126 */     if (this.m_NewBatch)
/*  59:    */     {
/*  60:127 */       resetQueue();
/*  61:128 */       this.m_NewBatch = false;
/*  62:    */     }
/*  63:130 */     if (this.m_removeFilter != null)
/*  64:    */     {
/*  65:131 */       this.m_removeFilter.input(instance);
/*  66:132 */       Instance processed = this.m_removeFilter.output();
/*  67:    */       
/*  68:134 */       copyValues(processed, false, instance.dataset(), outputFormatPeek());
/*  69:    */       
/*  70:136 */       push(processed, false);
/*  71:137 */       return true;
/*  72:    */     }
/*  73:139 */     bufferInput(instance);
/*  74:140 */     return false;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public boolean batchFinished()
/*  78:    */     throws Exception
/*  79:    */   {
/*  80:152 */     if (getInputFormat() == null) {
/*  81:153 */       throw new IllegalStateException("No input instance format defined");
/*  82:    */     }
/*  83:155 */     if (this.m_removeFilter == null)
/*  84:    */     {
/*  85:159 */       Instances toFilter = getInputFormat();
/*  86:160 */       int[] attsToDelete = new int[toFilter.numAttributes()];
/*  87:161 */       int numToDelete = 0;
/*  88:162 */       for (int i = 0; i < toFilter.numAttributes(); i++) {
/*  89:163 */         if (i != toFilter.classIndex())
/*  90:    */         {
/*  91:166 */           AttributeStats stats = toFilter.attributeStats(i);
/*  92:167 */           if (stats.missingCount == toFilter.numInstances())
/*  93:    */           {
/*  94:168 */             attsToDelete[(numToDelete++)] = i;
/*  95:    */           }
/*  96:169 */           else if (stats.distinctCount < 2)
/*  97:    */           {
/*  98:171 */             attsToDelete[(numToDelete++)] = i;
/*  99:    */           }
/* 100:172 */           else if (toFilter.attribute(i).isNominal())
/* 101:    */           {
/* 102:174 */             double variancePercent = stats.distinctCount / (stats.totalCount - stats.missingCount) * 100.0D;
/* 103:176 */             if (variancePercent > this.m_maxVariancePercentage) {
/* 104:177 */               attsToDelete[(numToDelete++)] = i;
/* 105:    */             }
/* 106:    */           }
/* 107:    */         }
/* 108:    */       }
/* 109:182 */       int[] finalAttsToDelete = new int[numToDelete];
/* 110:183 */       System.arraycopy(attsToDelete, 0, finalAttsToDelete, 0, numToDelete);
/* 111:    */       
/* 112:185 */       this.m_removeFilter = new Remove();
/* 113:186 */       this.m_removeFilter.setAttributeIndicesArray(finalAttsToDelete);
/* 114:187 */       this.m_removeFilter.setInvertSelection(false);
/* 115:188 */       this.m_removeFilter.setInputFormat(toFilter);
/* 116:190 */       for (int i = 0; i < toFilter.numInstances(); i++) {
/* 117:191 */         this.m_removeFilter.input(toFilter.instance(i));
/* 118:    */       }
/* 119:193 */       this.m_removeFilter.batchFinished();
/* 120:    */       
/* 121:    */ 
/* 122:196 */       Instances outputDataset = this.m_removeFilter.getOutputFormat();
/* 123:    */       
/* 124:    */ 
/* 125:199 */       outputDataset.setRelationName(toFilter.relationName());
/* 126:    */       
/* 127:201 */       setOutputFormat(outputDataset);
/* 128:    */       Instance processed;
/* 129:202 */       while ((processed = this.m_removeFilter.output()) != null)
/* 130:    */       {
/* 131:203 */         processed.setDataset(outputDataset);
/* 132:204 */         push(processed, false);
/* 133:    */       }
/* 134:    */     }
/* 135:207 */     flushInput();
/* 136:    */     
/* 137:209 */     this.m_NewBatch = true;
/* 138:210 */     return numPendingOutput() != 0;
/* 139:    */   }
/* 140:    */   
/* 141:    */   public Enumeration<Option> listOptions()
/* 142:    */   {
/* 143:221 */     Vector<Option> newVector = new Vector(1);
/* 144:    */     
/* 145:223 */     newVector.addElement(new Option("\tMaximum variance percentage allowed (default 99)", "M", 1, "-M <max variance %>"));
/* 146:    */     
/* 147:    */ 
/* 148:    */ 
/* 149:227 */     return newVector.elements();
/* 150:    */   }
/* 151:    */   
/* 152:    */   public void setOptions(String[] options)
/* 153:    */     throws Exception
/* 154:    */   {
/* 155:250 */     String mString = Utils.getOption('M', options);
/* 156:251 */     if (mString.length() != 0) {
/* 157:252 */       setMaximumVariancePercentageAllowed((int)Double.valueOf(mString).doubleValue());
/* 158:    */     } else {
/* 159:255 */       setMaximumVariancePercentageAllowed(99.0D);
/* 160:    */     }
/* 161:258 */     if (getInputFormat() != null) {
/* 162:259 */       setInputFormat(getInputFormat());
/* 163:    */     }
/* 164:262 */     Utils.checkForRemainingOptions(options);
/* 165:    */   }
/* 166:    */   
/* 167:    */   public String[] getOptions()
/* 168:    */   {
/* 169:273 */     Vector<String> options = new Vector();
/* 170:    */     
/* 171:275 */     options.add("-M");
/* 172:276 */     options.add("" + getMaximumVariancePercentageAllowed());
/* 173:    */     
/* 174:278 */     return (String[])options.toArray(new String[0]);
/* 175:    */   }
/* 176:    */   
/* 177:    */   public String globalInfo()
/* 178:    */   {
/* 179:288 */     return "This filter removes attributes that do not vary at all or that vary too much. All constant attributes are deleted automatically, along with any that exceed the maximum percentage of variance parameter. The maximum variance test is only applied to nominal attributes.";
/* 180:    */   }
/* 181:    */   
/* 182:    */   public String maximumVariancePercentageAllowedTipText()
/* 183:    */   {
/* 184:302 */     return "Set the threshold for the highest variance allowed before a nominal attribute will be deleted.Specifically, if (number_of_distinct_values / total_number_of_values * 100) is greater than this value then the attribute will be removed.";
/* 185:    */   }
/* 186:    */   
/* 187:    */   public void setMaximumVariancePercentageAllowed(double maxVariance)
/* 188:    */   {
/* 189:315 */     this.m_maxVariancePercentage = maxVariance;
/* 190:    */   }
/* 191:    */   
/* 192:    */   public double getMaximumVariancePercentageAllowed()
/* 193:    */   {
/* 194:326 */     return this.m_maxVariancePercentage;
/* 195:    */   }
/* 196:    */   
/* 197:    */   public String getRevision()
/* 198:    */   {
/* 199:336 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 200:    */   }
/* 201:    */   
/* 202:    */   public static void main(String[] argv)
/* 203:    */   {
/* 204:345 */     runFilter(new RemoveUseless(), argv);
/* 205:    */   }
/* 206:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.RemoveUseless
 * JD-Core Version:    0.7.0.1
 */