/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.text.ParseException;
/*   4:    */ import java.text.SimpleDateFormat;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Capabilities;
/*  10:    */ import weka.core.Capabilities.Capability;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.Option;
/*  14:    */ import weka.core.OptionHandler;
/*  15:    */ import weka.core.RevisionUtils;
/*  16:    */ import weka.core.SingleIndex;
/*  17:    */ import weka.core.UnsupportedAttributeTypeException;
/*  18:    */ import weka.core.Utils;
/*  19:    */ import weka.filters.Filter;
/*  20:    */ import weka.filters.StreamableFilter;
/*  21:    */ import weka.filters.UnsupervisedFilter;
/*  22:    */ 
/*  23:    */ public class ChangeDateFormat
/*  24:    */   extends Filter
/*  25:    */   implements UnsupervisedFilter, StreamableFilter, OptionHandler
/*  26:    */ {
/*  27:    */   static final long serialVersionUID = -1609344074013448737L;
/*  28: 79 */   private static final SimpleDateFormat DEFAULT_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
/*  29: 83 */   private final SingleIndex m_AttIndex = new SingleIndex("last");
/*  30: 86 */   private SimpleDateFormat m_DateFormat = DEFAULT_FORMAT;
/*  31:    */   private Attribute m_OutputAttribute;
/*  32:    */   
/*  33:    */   public String globalInfo()
/*  34:    */   {
/*  35: 98 */     return "Changes the date format used by a date attribute. This is most useful for converting to a format with less precision, for example, from an absolute date to day of year, etc. This changes the format string, and changes the date values to those that would be parsed by the new format.";
/*  36:    */   }
/*  37:    */   
/*  38:    */   public Capabilities getCapabilities()
/*  39:    */   {
/*  40:113 */     Capabilities result = super.getCapabilities();
/*  41:114 */     result.disableAll();
/*  42:    */     
/*  43:    */ 
/*  44:117 */     result.enableAllAttributes();
/*  45:118 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  46:    */     
/*  47:    */ 
/*  48:121 */     result.enableAllClasses();
/*  49:122 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  50:123 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  51:    */     
/*  52:125 */     return result;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public boolean setInputFormat(Instances instanceInfo)
/*  56:    */     throws Exception
/*  57:    */   {
/*  58:140 */     super.setInputFormat(instanceInfo);
/*  59:141 */     this.m_AttIndex.setUpper(instanceInfo.numAttributes() - 1);
/*  60:142 */     if (!instanceInfo.attribute(this.m_AttIndex.getIndex()).isDate()) {
/*  61:143 */       throw new UnsupportedAttributeTypeException("Chosen attribute not date.");
/*  62:    */     }
/*  63:146 */     setOutputFormat();
/*  64:147 */     return true;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public boolean input(Instance instance)
/*  68:    */     throws Exception
/*  69:    */   {
/*  70:161 */     if (getInputFormat() == null) {
/*  71:162 */       throw new IllegalStateException("No input instance format defined");
/*  72:    */     }
/*  73:164 */     if (this.m_NewBatch)
/*  74:    */     {
/*  75:165 */       resetQueue();
/*  76:166 */       this.m_NewBatch = false;
/*  77:    */     }
/*  78:168 */     Instance newInstance = (Instance)instance.copy();
/*  79:169 */     int index = this.m_AttIndex.getIndex();
/*  80:170 */     if (!newInstance.isMissing(index))
/*  81:    */     {
/*  82:171 */       double value = instance.value(index);
/*  83:    */       try
/*  84:    */       {
/*  85:175 */         value = this.m_OutputAttribute.parseDate(this.m_OutputAttribute.formatDate(value));
/*  86:    */       }
/*  87:    */       catch (ParseException pe)
/*  88:    */       {
/*  89:178 */         throw new RuntimeException("Output date format couldn't parse its own output!!");
/*  90:    */       }
/*  91:181 */       newInstance.setValue(index, value);
/*  92:    */     }
/*  93:183 */     push(newInstance, false);
/*  94:184 */     return true;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public Enumeration<Option> listOptions()
/*  98:    */   {
/*  99:195 */     Vector<Option> newVector = new Vector(2);
/* 100:    */     
/* 101:197 */     newVector.addElement(new Option("\tSets the attribute index (default last).", "C", 1, "-C <col>"));
/* 102:    */     
/* 103:    */ 
/* 104:200 */     newVector.addElement(new Option("\tSets the output date format string (default corresponds to ISO-8601).", "F", 1, "-F <value index>"));
/* 105:    */     
/* 106:    */ 
/* 107:    */ 
/* 108:    */ 
/* 109:205 */     return newVector.elements();
/* 110:    */   }
/* 111:    */   
/* 112:    */   public void setOptions(String[] options)
/* 113:    */     throws Exception
/* 114:    */   {
/* 115:233 */     String attIndex = Utils.getOption('C', options);
/* 116:234 */     if (attIndex.length() != 0) {
/* 117:235 */       setAttributeIndex(attIndex);
/* 118:    */     } else {
/* 119:237 */       setAttributeIndex("last");
/* 120:    */     }
/* 121:240 */     String formatString = Utils.getOption('F', options);
/* 122:241 */     if (formatString.length() != 0) {
/* 123:242 */       setDateFormat(formatString);
/* 124:    */     } else {
/* 125:244 */       setDateFormat(DEFAULT_FORMAT);
/* 126:    */     }
/* 127:247 */     if (getInputFormat() != null) {
/* 128:248 */       setInputFormat(getInputFormat());
/* 129:    */     }
/* 130:251 */     Utils.checkForRemainingOptions(options);
/* 131:    */   }
/* 132:    */   
/* 133:    */   public String[] getOptions()
/* 134:    */   {
/* 135:262 */     Vector<String> options = new Vector();
/* 136:    */     
/* 137:264 */     options.add("-C");
/* 138:265 */     options.add("" + getAttributeIndex());
/* 139:266 */     options.add("-F");
/* 140:267 */     options.add("" + getDateFormat().toPattern());
/* 141:    */     
/* 142:269 */     return (String[])options.toArray(new String[0]);
/* 143:    */   }
/* 144:    */   
/* 145:    */   public String attributeIndexTipText()
/* 146:    */   {
/* 147:278 */     return "Sets which attribute to process. This attribute must be of type date (\"first\" and \"last\" are valid values)";
/* 148:    */   }
/* 149:    */   
/* 150:    */   public String getAttributeIndex()
/* 151:    */   {
/* 152:289 */     return this.m_AttIndex.getSingleIndex();
/* 153:    */   }
/* 154:    */   
/* 155:    */   public void setAttributeIndex(String attIndex)
/* 156:    */   {
/* 157:299 */     this.m_AttIndex.setSingleIndex(attIndex);
/* 158:    */   }
/* 159:    */   
/* 160:    */   public String dateFormatTipText()
/* 161:    */   {
/* 162:308 */     return "The date format to change to. This should be a format understood by Java's SimpleDateFormat class.";
/* 163:    */   }
/* 164:    */   
/* 165:    */   public SimpleDateFormat getDateFormat()
/* 166:    */   {
/* 167:319 */     return this.m_DateFormat;
/* 168:    */   }
/* 169:    */   
/* 170:    */   public void setDateFormat(String dateFormat)
/* 171:    */   {
/* 172:329 */     setDateFormat(new SimpleDateFormat(dateFormat));
/* 173:    */   }
/* 174:    */   
/* 175:    */   public void setDateFormat(SimpleDateFormat dateFormat)
/* 176:    */   {
/* 177:338 */     if (dateFormat == null) {
/* 178:339 */       throw new NullPointerException();
/* 179:    */     }
/* 180:341 */     this.m_DateFormat = dateFormat;
/* 181:    */   }
/* 182:    */   
/* 183:    */   private void setOutputFormat()
/* 184:    */   {
/* 185:350 */     ArrayList<Attribute> newAtts = new ArrayList(getInputFormat().numAttributes());
/* 186:352 */     for (int j = 0; j < getInputFormat().numAttributes(); j++)
/* 187:    */     {
/* 188:353 */       Attribute att = getInputFormat().attribute(j);
/* 189:354 */       if (j == this.m_AttIndex.getIndex()) {
/* 190:355 */         newAtts.add(new Attribute(att.name(), getDateFormat().toPattern()));
/* 191:    */       } else {
/* 192:357 */         newAtts.add((Attribute)att.copy());
/* 193:    */       }
/* 194:    */     }
/* 195:362 */     Instances newData = new Instances(getInputFormat().relationName(), newAtts, 0);
/* 196:    */     
/* 197:364 */     newData.setClassIndex(getInputFormat().classIndex());
/* 198:365 */     this.m_OutputAttribute = newData.attribute(this.m_AttIndex.getIndex());
/* 199:366 */     setOutputFormat(newData);
/* 200:    */   }
/* 201:    */   
/* 202:    */   public String getRevision()
/* 203:    */   {
/* 204:376 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 205:    */   }
/* 206:    */   
/* 207:    */   public static void main(String[] argv)
/* 208:    */   {
/* 209:385 */     runFilter(new ChangeDateFormat(), argv);
/* 210:    */   }
/* 211:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.ChangeDateFormat
 * JD-Core Version:    0.7.0.1
 */