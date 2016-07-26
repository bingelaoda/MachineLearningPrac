/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.math.BigDecimal;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.List;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   7:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   8:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   9:    */ import javax.xml.bind.annotation.XmlElement;
/*  10:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  11:    */ import javax.xml.bind.annotation.XmlType;
/*  12:    */ 
/*  13:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  14:    */ @XmlType(name="", propOrder={"extension"})
/*  15:    */ @XmlRootElement(name="MiningField")
/*  16:    */ public class MiningField
/*  17:    */ {
/*  18:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected List<Extension> extension;
/*  20:    */   @XmlAttribute
/*  21:    */   protected Double highValue;
/*  22:    */   @XmlAttribute
/*  23:    */   protected BigDecimal importance;
/*  24:    */   @XmlAttribute
/*  25:    */   protected INVALIDVALUETREATMENTMETHOD invalidValueTreatment;
/*  26:    */   @XmlAttribute
/*  27:    */   protected Double lowValue;
/*  28:    */   @XmlAttribute
/*  29:    */   protected String missingValueReplacement;
/*  30:    */   @XmlAttribute
/*  31:    */   protected MISSINGVALUETREATMENTMETHOD missingValueTreatment;
/*  32:    */   @XmlAttribute(required=true)
/*  33:    */   protected String name;
/*  34:    */   @XmlAttribute
/*  35:    */   protected OPTYPE optype;
/*  36:    */   @XmlAttribute
/*  37:    */   protected OUTLIERTREATMENTMETHOD outliers;
/*  38:    */   @XmlAttribute
/*  39:    */   protected FIELDUSAGETYPE usageType;
/*  40:    */   
/*  41:    */   public MiningField() {}
/*  42:    */   
/*  43:    */   public MiningField(String name, FIELDUSAGETYPE usageType)
/*  44:    */   {
/*  45: 86 */     this.name = name;
/*  46: 87 */     this.usageType = usageType;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public MiningField(String name, FIELDUSAGETYPE usageType, MISSINGVALUETREATMENTMETHOD missingValueTreatment, String missingValueReplacement)
/*  50:    */   {
/*  51: 91 */     this.name = name;
/*  52: 92 */     this.usageType = usageType;
/*  53: 93 */     this.missingValueTreatment = missingValueTreatment;
/*  54: 94 */     this.missingValueReplacement = missingValueReplacement;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public List<Extension> getExtension()
/*  58:    */   {
/*  59:120 */     if (this.extension == null) {
/*  60:121 */       this.extension = new ArrayList();
/*  61:    */     }
/*  62:123 */     return this.extension;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public Double getHighValue()
/*  66:    */   {
/*  67:135 */     return this.highValue;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public void setHighValue(Double value)
/*  71:    */   {
/*  72:147 */     this.highValue = value;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public BigDecimal getImportance()
/*  76:    */   {
/*  77:159 */     return this.importance;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public void setImportance(BigDecimal value)
/*  81:    */   {
/*  82:171 */     this.importance = value;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public INVALIDVALUETREATMENTMETHOD getInvalidValueTreatment()
/*  86:    */   {
/*  87:183 */     if (this.invalidValueTreatment == null) {
/*  88:184 */       return INVALIDVALUETREATMENTMETHOD.RETURN_INVALID;
/*  89:    */     }
/*  90:186 */     return this.invalidValueTreatment;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public void setInvalidValueTreatment(INVALIDVALUETREATMENTMETHOD value)
/*  94:    */   {
/*  95:199 */     this.invalidValueTreatment = value;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public Double getLowValue()
/*  99:    */   {
/* 100:211 */     return this.lowValue;
/* 101:    */   }
/* 102:    */   
/* 103:    */   public void setLowValue(Double value)
/* 104:    */   {
/* 105:223 */     this.lowValue = value;
/* 106:    */   }
/* 107:    */   
/* 108:    */   public String getMissingValueReplacement()
/* 109:    */   {
/* 110:235 */     return this.missingValueReplacement;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public void setMissingValueReplacement(String value)
/* 114:    */   {
/* 115:247 */     this.missingValueReplacement = value;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public MISSINGVALUETREATMENTMETHOD getMissingValueTreatment()
/* 119:    */   {
/* 120:259 */     return this.missingValueTreatment;
/* 121:    */   }
/* 122:    */   
/* 123:    */   public void setMissingValueTreatment(MISSINGVALUETREATMENTMETHOD value)
/* 124:    */   {
/* 125:271 */     this.missingValueTreatment = value;
/* 126:    */   }
/* 127:    */   
/* 128:    */   public String getName()
/* 129:    */   {
/* 130:283 */     return this.name;
/* 131:    */   }
/* 132:    */   
/* 133:    */   public void setName(String value)
/* 134:    */   {
/* 135:295 */     this.name = value;
/* 136:    */   }
/* 137:    */   
/* 138:    */   public OPTYPE getOptype()
/* 139:    */   {
/* 140:307 */     return this.optype;
/* 141:    */   }
/* 142:    */   
/* 143:    */   public void setOptype(OPTYPE value)
/* 144:    */   {
/* 145:319 */     this.optype = value;
/* 146:    */   }
/* 147:    */   
/* 148:    */   public OUTLIERTREATMENTMETHOD getOutliers()
/* 149:    */   {
/* 150:331 */     if (this.outliers == null) {
/* 151:332 */       return OUTLIERTREATMENTMETHOD.AS_IS;
/* 152:    */     }
/* 153:334 */     return this.outliers;
/* 154:    */   }
/* 155:    */   
/* 156:    */   public void setOutliers(OUTLIERTREATMENTMETHOD value)
/* 157:    */   {
/* 158:347 */     this.outliers = value;
/* 159:    */   }
/* 160:    */   
/* 161:    */   public FIELDUSAGETYPE getUsageType()
/* 162:    */   {
/* 163:359 */     if (this.usageType == null) {
/* 164:360 */       return FIELDUSAGETYPE.ACTIVE;
/* 165:    */     }
/* 166:362 */     return this.usageType;
/* 167:    */   }
/* 168:    */   
/* 169:    */   public void setUsageType(FIELDUSAGETYPE value)
/* 170:    */   {
/* 171:375 */     this.usageType = value;
/* 172:    */   }
/* 173:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.MiningField
 * JD-Core Version:    0.7.0.1
 */