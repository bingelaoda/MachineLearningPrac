/*   1:    */ package weka.core.pmml;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import org.w3c.dom.Element;
/*   5:    */ import weka.core.Attribute;
/*   6:    */ import weka.core.Instances;
/*   7:    */ import weka.core.Utils;
/*   8:    */ 
/*   9:    */ public class MiningFieldMetaInfo
/*  10:    */   extends FieldMetaInfo
/*  11:    */   implements Serializable
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = -1256774332779563185L;
/*  14:    */   
/*  15:    */   static enum Usage
/*  16:    */   {
/*  17: 44 */     ACTIVE("active"),  PREDICTED("predicted"),  SUPPLEMENTARY("supplementary"),  GROUP("group"),  ORDER("order");
/*  18:    */     
/*  19:    */     private final String m_stringVal;
/*  20:    */     
/*  21:    */     private Usage(String name)
/*  22:    */     {
/*  23: 52 */       this.m_stringVal = name;
/*  24:    */     }
/*  25:    */     
/*  26:    */     public String toString()
/*  27:    */     {
/*  28: 56 */       return this.m_stringVal;
/*  29:    */     }
/*  30:    */   }
/*  31:    */   
/*  32: 61 */   Usage m_usageType = Usage.ACTIVE;
/*  33:    */   
/*  34:    */   static enum Outlier
/*  35:    */   {
/*  36: 64 */     ASIS("asIs"),  ASMISSINGVALUES("asMissingValues"),  ASEXTREMEVALUES("asExtremeValues");
/*  37:    */     
/*  38:    */     private final String m_stringVal;
/*  39:    */     
/*  40:    */     private Outlier(String name)
/*  41:    */     {
/*  42: 70 */       this.m_stringVal = name;
/*  43:    */     }
/*  44:    */     
/*  45:    */     public String toString()
/*  46:    */     {
/*  47: 74 */       return this.m_stringVal;
/*  48:    */     }
/*  49:    */   }
/*  50:    */   
/*  51: 78 */   protected Outlier m_outlierTreatmentMethod = Outlier.ASIS;
/*  52:    */   protected double m_lowValue;
/*  53:    */   protected double m_highValue;
/*  54:    */   
/*  55:    */   static enum Missing
/*  56:    */   {
/*  57: 86 */     ASIS("asIs"),  ASMEAN("asMean"),  ASMODE("asMode"),  ASMEDIAN("asMedian"),  ASVALUE("asValue");
/*  58:    */     
/*  59:    */     private final String m_stringVal;
/*  60:    */     
/*  61:    */     private Missing(String name)
/*  62:    */     {
/*  63: 94 */       this.m_stringVal = name;
/*  64:    */     }
/*  65:    */     
/*  66:    */     public String toString()
/*  67:    */     {
/*  68: 98 */       return this.m_stringVal;
/*  69:    */     }
/*  70:    */   }
/*  71:    */   
/*  72:102 */   protected Missing m_missingValueTreatmentMethod = Missing.ASIS;
/*  73:    */   protected String m_missingValueReplacementNominal;
/*  74:    */   protected double m_missingValueReplacementNumeric;
/*  75:110 */   protected FieldMetaInfo.Optype m_optypeOverride = FieldMetaInfo.Optype.NONE;
/*  76:    */   protected int m_index;
/*  77:    */   protected double m_importance;
/*  78:119 */   Instances m_miningSchemaI = null;
/*  79:    */   
/*  80:    */   protected void setMiningSchemaInstances(Instances miningSchemaI)
/*  81:    */   {
/*  82:131 */     this.m_miningSchemaI = miningSchemaI;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public Usage getUsageType()
/*  86:    */   {
/*  87:140 */     return this.m_usageType;
/*  88:    */   }
/*  89:    */   
/*  90:    */   public String toString()
/*  91:    */   {
/*  92:149 */     StringBuffer temp = new StringBuffer();
/*  93:150 */     temp.append(this.m_miningSchemaI.attribute(this.m_index));
/*  94:151 */     temp.append("\n\tusage: " + this.m_usageType + "\n\toutlier treatment: " + this.m_outlierTreatmentMethod);
/*  95:153 */     if (this.m_outlierTreatmentMethod == Outlier.ASEXTREMEVALUES) {
/*  96:154 */       temp.append(" (lowValue = " + this.m_lowValue + " highValue = " + this.m_highValue + ")");
/*  97:    */     }
/*  98:157 */     temp.append("\n\tmissing value treatment: " + this.m_missingValueTreatmentMethod);
/*  99:159 */     if (this.m_missingValueTreatmentMethod != Missing.ASIS) {
/* 100:160 */       temp.append(" (replacementValue = " + (this.m_missingValueReplacementNominal != null ? this.m_missingValueReplacementNominal : Utils.doubleToString(this.m_missingValueReplacementNumeric, 4)) + ")");
/* 101:    */     }
/* 102:167 */     return temp.toString();
/* 103:    */   }
/* 104:    */   
/* 105:    */   public void setIndex(int index)
/* 106:    */   {
/* 107:177 */     this.m_index = index;
/* 108:    */   }
/* 109:    */   
/* 110:    */   public String getName()
/* 111:    */   {
/* 112:186 */     return this.m_fieldName;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public Outlier getOutlierTreatmentMethod()
/* 116:    */   {
/* 117:195 */     return this.m_outlierTreatmentMethod;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public Missing getMissingValueTreatmentMethod()
/* 121:    */   {
/* 122:204 */     return this.m_missingValueTreatmentMethod;
/* 123:    */   }
/* 124:    */   
/* 125:    */   public double applyMissingValueTreatment(double value)
/* 126:    */     throws Exception
/* 127:    */   {
/* 128:215 */     double newVal = value;
/* 129:216 */     if ((this.m_missingValueTreatmentMethod != Missing.ASIS) && (Utils.isMissingValue(value))) {
/* 130:218 */       if (this.m_missingValueReplacementNominal != null)
/* 131:    */       {
/* 132:219 */         Attribute att = this.m_miningSchemaI.attribute(this.m_index);
/* 133:220 */         int valIndex = att.indexOfValue(this.m_missingValueReplacementNominal);
/* 134:221 */         if (valIndex < 0) {
/* 135:222 */           throw new Exception("[MiningSchema] Nominal missing value replacement value doesn't exist in the mining schema Instances!");
/* 136:    */         }
/* 137:225 */         newVal = valIndex;
/* 138:    */       }
/* 139:    */       else
/* 140:    */       {
/* 141:227 */         newVal = this.m_missingValueReplacementNumeric;
/* 142:    */       }
/* 143:    */     }
/* 144:230 */     return newVal;
/* 145:    */   }
/* 146:    */   
/* 147:    */   public double applyOutlierTreatment(double value)
/* 148:    */     throws Exception
/* 149:    */   {
/* 150:241 */     double newVal = value;
/* 151:242 */     if (this.m_outlierTreatmentMethod != Outlier.ASIS) {
/* 152:243 */       if (this.m_outlierTreatmentMethod == Outlier.ASMISSINGVALUES) {
/* 153:244 */         newVal = applyMissingValueTreatment(value);
/* 154:246 */       } else if (value < this.m_lowValue) {
/* 155:247 */         newVal = this.m_lowValue;
/* 156:248 */       } else if (value > this.m_highValue) {
/* 157:249 */         newVal = this.m_highValue;
/* 158:    */       }
/* 159:    */     }
/* 160:253 */     return newVal;
/* 161:    */   }
/* 162:    */   
/* 163:    */   public Attribute getFieldAsAttribute()
/* 164:    */   {
/* 165:262 */     return this.m_miningSchemaI.attribute(this.m_index);
/* 166:    */   }
/* 167:    */   
/* 168:    */   public MiningFieldMetaInfo(Element field)
/* 169:    */     throws Exception
/* 170:    */   {
/* 171:271 */     super(field);
/* 172:    */     
/* 173:    */ 
/* 174:    */ 
/* 175:275 */     String usage = field.getAttribute("usageType");
/* 176:276 */     for (Usage u : Usage.values()) {
/* 177:277 */       if (u.toString().equals(usage))
/* 178:    */       {
/* 179:278 */         this.m_usageType = u;
/* 180:279 */         break;
/* 181:    */       }
/* 182:    */     }
/* 183:296 */     String importance = field.getAttribute("importance");
/* 184:297 */     if (importance.length() > 0) {
/* 185:298 */       this.m_importance = Double.parseDouble(importance);
/* 186:    */     }
/* 187:302 */     String outliers = field.getAttribute("outliers");
/* 188:303 */     for (Outlier o : Outlier.values()) {
/* 189:304 */       if (o.toString().equals(outliers))
/* 190:    */       {
/* 191:305 */         this.m_outlierTreatmentMethod = o;
/* 192:306 */         break;
/* 193:    */       }
/* 194:    */     }
/* 195:310 */     if ((outliers.length() > 0) && (this.m_outlierTreatmentMethod == Outlier.ASEXTREMEVALUES))
/* 196:    */     {
/* 197:312 */       String lowValue = field.getAttribute("lowValue");
/* 198:313 */       if (lowValue.length() > 0) {
/* 199:314 */         this.m_lowValue = Double.parseDouble(lowValue);
/* 200:    */       } else {
/* 201:316 */         throw new Exception("[MiningFieldMetaInfo] as extreme values outlier treatment specified, but no low value defined!");
/* 202:    */       }
/* 203:319 */       String highValue = field.getAttribute("highValue");
/* 204:320 */       if (highValue.length() > 0) {
/* 205:321 */         this.m_highValue = Double.parseDouble(highValue);
/* 206:    */       } else {
/* 207:323 */         throw new Exception("[MiningFieldMetaInfo] as extreme values outlier treatment specified, but no high value defined!");
/* 208:    */       }
/* 209:    */     }
/* 210:330 */     String missingReplacement = field.getAttribute("missingValueReplacement");
/* 211:331 */     if (missingReplacement.length() > 0)
/* 212:    */     {
/* 213:    */       try
/* 214:    */       {
/* 215:334 */         this.m_missingValueReplacementNumeric = Double.parseDouble(missingReplacement);
/* 216:    */       }
/* 217:    */       catch (IllegalArgumentException ex)
/* 218:    */       {
/* 219:337 */         this.m_missingValueReplacementNominal = missingReplacement;
/* 220:    */       }
/* 221:341 */       String missingTreatment = field.getAttribute("missingValueTreatment");
/* 222:342 */       for (Missing m : Missing.values()) {
/* 223:343 */         if (m.toString().equals(missingTreatment))
/* 224:    */         {
/* 225:344 */           this.m_missingValueTreatmentMethod = m;
/* 226:345 */           break;
/* 227:    */         }
/* 228:    */       }
/* 229:    */     }
/* 230:    */   }
/* 231:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.MiningFieldMetaInfo
 * JD-Core Version:    0.7.0.1
 */