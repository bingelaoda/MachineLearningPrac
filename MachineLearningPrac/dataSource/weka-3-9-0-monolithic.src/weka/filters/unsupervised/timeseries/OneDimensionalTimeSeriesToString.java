/*   1:    */ package weka.filters.unsupervised.timeseries;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.List;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Capabilities;
/*  10:    */ import weka.core.Capabilities.Capability;
/*  11:    */ import weka.core.DenseInstance;
/*  12:    */ import weka.core.Instance;
/*  13:    */ import weka.core.Instances;
/*  14:    */ import weka.core.Option;
/*  15:    */ import weka.core.Range;
/*  16:    */ import weka.core.RelationalLocator;
/*  17:    */ import weka.core.SparseInstance;
/*  18:    */ import weka.core.StringLocator;
/*  19:    */ import weka.core.Utils;
/*  20:    */ import weka.filters.SimpleStreamFilter;
/*  21:    */ 
/*  22:    */ public class OneDimensionalTimeSeriesToString
/*  23:    */   extends SimpleStreamFilter
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = -5844666358579212500L;
/*  26:    */   
/*  27:    */   private static final Range getDefaultAttributes()
/*  28:    */   {
/*  29: 73 */     return new Range();
/*  30:    */   }
/*  31:    */   
/*  32: 77 */   private Range m_TimeSeriesAttributes = new Range(getDefaultAttributes().getRanges());
/*  33: 81 */   private StringLocator m_StringAttributes = null;
/*  34: 84 */   private RelationalLocator m_RelationalAttributes = null;
/*  35:    */   
/*  36:    */   public Capabilities getCapabilities()
/*  37:    */   {
/*  38: 92 */     Capabilities result = super.getCapabilities();
/*  39: 93 */     result.disableAll();
/*  40:    */     
/*  41:    */ 
/*  42: 96 */     result.enableAllAttributes();
/*  43: 97 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  44:    */     
/*  45:    */ 
/*  46:100 */     result.enableAllClasses();
/*  47:101 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  48:102 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  49:    */     
/*  50:104 */     return result;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public Enumeration<Option> listOptions()
/*  54:    */   {
/*  55:112 */     List<Option> options = new ArrayList(2);
/*  56:    */     
/*  57:114 */     options.add(new Option("\tSpecifies the attributes that should be transformed.\n\tThe attributes must be relational attributes and must contain only\n\tone attribute.\n\tFirst and last are valid indices. (default " + getDefaultAttributes() + ")", "R", 1, "-R <index1,index2-index3,...>"));
/*  58:    */     
/*  59:    */ 
/*  60:    */ 
/*  61:    */ 
/*  62:    */ 
/*  63:    */ 
/*  64:    */ 
/*  65:122 */     options.add(new Option("\tInverts the specified attribute range (default don't invert)", "V", 0, "-V"));
/*  66:    */     
/*  67:    */ 
/*  68:    */ 
/*  69:126 */     return Collections.enumeration(options);
/*  70:    */   }
/*  71:    */   
/*  72:    */   public String[] getOptions()
/*  73:    */   {
/*  74:135 */     List<String> options = new ArrayList();
/*  75:137 */     if (!this.m_TimeSeriesAttributes.equals(getDefaultAttributes()))
/*  76:    */     {
/*  77:138 */       options.add("-R");
/*  78:139 */       options.add(this.m_TimeSeriesAttributes.getRanges());
/*  79:    */     }
/*  80:142 */     if (this.m_TimeSeriesAttributes.getInvert()) {
/*  81:143 */       options.add("-V");
/*  82:    */     }
/*  83:146 */     return (String[])options.toArray(new String[0]);
/*  84:    */   }
/*  85:    */   
/*  86:    */   public void setOptions(String[] options)
/*  87:    */     throws Exception
/*  88:    */   {
/*  89:172 */     String timeSeriesAttributes = Utils.getOption('R', options);
/*  90:173 */     if (timeSeriesAttributes.length() != 0) {
/*  91:174 */       this.m_TimeSeriesAttributes = new Range(timeSeriesAttributes);
/*  92:    */     }
/*  93:176 */     if (Utils.getFlag('V', options)) {
/*  94:177 */       this.m_TimeSeriesAttributes.setInvert(true);
/*  95:    */     }
/*  96:179 */     if (getInputFormat() != null) {
/*  97:180 */       setInputFormat(getInputFormat());
/*  98:    */     }
/*  99:182 */     Utils.checkForRemainingOptions(options);
/* 100:    */   }
/* 101:    */   
/* 102:    */   public String getRange()
/* 103:    */   {
/* 104:191 */     return this.m_TimeSeriesAttributes.getRanges();
/* 105:    */   }
/* 106:    */   
/* 107:    */   public void setRange(String range)
/* 108:    */   {
/* 109:200 */     this.m_TimeSeriesAttributes.setRanges(range);
/* 110:    */   }
/* 111:    */   
/* 112:    */   public String rangeTipText()
/* 113:    */   {
/* 114:209 */     return "The attribute ranges to which the filter should be applied to";
/* 115:    */   }
/* 116:    */   
/* 117:    */   public boolean getInvertRange()
/* 118:    */   {
/* 119:218 */     return this.m_TimeSeriesAttributes.getInvert();
/* 120:    */   }
/* 121:    */   
/* 122:    */   public void setInvertRange(boolean inversion)
/* 123:    */   {
/* 124:227 */     this.m_TimeSeriesAttributes.setInvert(inversion);
/* 125:    */   }
/* 126:    */   
/* 127:    */   public String invertRangeTipText()
/* 128:    */   {
/* 129:236 */     return "Whether the specified attribute range should be inverted";
/* 130:    */   }
/* 131:    */   
/* 132:    */   public String globalInfo()
/* 133:    */   {
/* 134:241 */     return "A filter to concatenate the string representation of each data point of a one dimensional time series.";
/* 135:    */   }
/* 136:    */   
/* 137:    */   protected boolean hasImmediateOutputFormat()
/* 138:    */   {
/* 139:256 */     return true;
/* 140:    */   }
/* 141:    */   
/* 142:    */   protected Instances determineOutputFormat(Instances inputFormat)
/* 143:    */     throws Exception
/* 144:    */   {
/* 145:269 */     this.m_TimeSeriesAttributes.setUpper(inputFormat.numAttributes() - 1);
/* 146:    */     
/* 147:271 */     ArrayList<Attribute> newAttributes = new ArrayList(inputFormat.numAttributes());
/* 148:274 */     for (int att = 0; att < inputFormat.numAttributes(); att++) {
/* 149:275 */       newAttributes.add((Attribute)inputFormat.attribute(att).copy());
/* 150:    */     }
/* 151:278 */     Instances outputFormat = new Instances(inputFormat.relationName(), newAttributes, 0);
/* 152:281 */     for (int index : this.m_TimeSeriesAttributes.getSelection())
/* 153:    */     {
/* 154:282 */       if (!inputFormat.attribute(index).isRelationValued()) {
/* 155:283 */         throw new Exception(String.format("Attribute '%s' isn't relational!", new Object[] { inputFormat.attribute(index).name() }));
/* 156:    */       }
/* 157:289 */       if (inputFormat.attribute(index).relation().numAttributes() != 1) {
/* 158:290 */         throw new Exception(String.format("More than one dimension!", new Object[] { inputFormat.attribute(index).relation().numAttributes() + "(%d)" }));
/* 159:    */       }
/* 160:294 */       outputFormat.replaceAttributeAt(new Attribute(inputFormat.attribute(index).name(), (List)null), index);
/* 161:    */     }
/* 162:301 */     this.m_TimeSeriesAttributes.setInvert(!this.m_TimeSeriesAttributes.getInvert());
/* 163:302 */     this.m_StringAttributes = new StringLocator(inputFormat, this.m_TimeSeriesAttributes.getSelection());
/* 164:    */     
/* 165:304 */     this.m_RelationalAttributes = new RelationalLocator(inputFormat, this.m_TimeSeriesAttributes.getSelection());
/* 166:    */     
/* 167:306 */     this.m_TimeSeriesAttributes.setInvert(!this.m_TimeSeriesAttributes.getInvert());
/* 168:    */     
/* 169:308 */     outputFormat.setClassIndex(inputFormat.classIndex());
/* 170:    */     
/* 171:310 */     return outputFormat;
/* 172:    */   }
/* 173:    */   
/* 174:    */   protected Instance process(Instance inputInstance)
/* 175:    */     throws Exception
/* 176:    */   {
/* 177:323 */     StringLocator.copyStringValues(inputInstance, getOutputFormat(), this.m_StringAttributes);
/* 178:    */     
/* 179:325 */     RelationalLocator.copyRelationalValues(inputInstance, getOutputFormat(), this.m_RelationalAttributes);
/* 180:    */     
/* 181:    */ 
/* 182:328 */     Instance outputInstance = null;
/* 183:329 */     if ((inputInstance instanceof DenseInstance)) {
/* 184:330 */       outputInstance = new DenseInstance(inputInstance.weight(), Arrays.copyOf(inputInstance.toDoubleArray(), inputInstance.numAttributes()));
/* 185:332 */     } else if ((inputInstance instanceof SparseInstance)) {
/* 186:333 */       outputInstance = new SparseInstance(inputInstance.weight(), Arrays.copyOf(inputInstance.toDoubleArray(), inputInstance.numAttributes()));
/* 187:    */     } else {
/* 188:336 */       throw new Exception("Input instance is neither sparse nor dense!");
/* 189:    */     }
/* 190:339 */     outputInstance.setDataset(getOutputFormat());
/* 191:341 */     for (int index : this.m_TimeSeriesAttributes.getSelection()) {
/* 192:343 */       if (!inputInstance.isMissing(index))
/* 193:    */       {
/* 194:346 */         Instances timeSeries = inputInstance.relationalValue(index);
/* 195:347 */         StringBuilder str = new StringBuilder();
/* 196:349 */         for (int i = 0; i < timeSeries.numInstances(); i++) {
/* 197:350 */           str.append(timeSeries.get(i).toString(0));
/* 198:    */         }
/* 199:353 */         int stringIndex = getOutputFormat().attribute(index).addStringValue(str.toString());
/* 200:    */         
/* 201:355 */         outputInstance.setValue(index, stringIndex);
/* 202:    */       }
/* 203:    */     }
/* 204:359 */     return outputInstance;
/* 205:    */   }
/* 206:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.timeseries.OneDimensionalTimeSeriesToString
 * JD-Core Version:    0.7.0.1
 */