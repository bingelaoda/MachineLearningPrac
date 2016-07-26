/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.math.BigDecimal;
/*   4:    */ import java.math.BigInteger;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.List;
/*   7:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   8:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   9:    */ import javax.xml.bind.annotation.XmlAttribute;
/*  10:    */ import javax.xml.bind.annotation.XmlElement;
/*  11:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  12:    */ import javax.xml.bind.annotation.XmlType;
/*  13:    */ 
/*  14:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  15:    */ @XmlType(name="", propOrder={"extension"})
/*  16:    */ @XmlRootElement(name="MultivariateStat")
/*  17:    */ public class MultivariateStat
/*  18:    */ {
/*  19:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected List<Extension> extension;
/*  21:    */   @XmlAttribute
/*  22:    */   protected String category;
/*  23:    */   @XmlAttribute
/*  24:    */   protected Double chiSquareValue;
/*  25:    */   @XmlAttribute
/*  26:    */   protected BigDecimal confidenceLevel;
/*  27:    */   @XmlAttribute
/*  28:    */   protected Double confidenceLowerBound;
/*  29:    */   @XmlAttribute
/*  30:    */   protected Double confidenceUpperBound;
/*  31:    */   @XmlAttribute(name="dF")
/*  32:    */   protected Double df;
/*  33:    */   @XmlAttribute
/*  34:    */   protected BigInteger exponent;
/*  35:    */   @XmlAttribute
/*  36:    */   protected Double fStatistic;
/*  37:    */   @XmlAttribute
/*  38:    */   protected BigDecimal importance;
/*  39:    */   @XmlAttribute
/*  40:    */   protected Boolean isIntercept;
/*  41:    */   @XmlAttribute
/*  42:    */   protected String name;
/*  43:    */   @XmlAttribute
/*  44:    */   protected BigDecimal pValueAlpha;
/*  45:    */   @XmlAttribute
/*  46:    */   protected BigDecimal pValueFinal;
/*  47:    */   @XmlAttribute
/*  48:    */   protected BigDecimal pValueInitial;
/*  49:    */   @XmlAttribute
/*  50:    */   protected Double stdError;
/*  51:    */   @XmlAttribute
/*  52:    */   protected Double tValue;
/*  53:    */   
/*  54:    */   public List<Extension> getExtension()
/*  55:    */   {
/*  56:125 */     if (this.extension == null) {
/*  57:126 */       this.extension = new ArrayList();
/*  58:    */     }
/*  59:128 */     return this.extension;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public String getCategory()
/*  63:    */   {
/*  64:140 */     return this.category;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void setCategory(String value)
/*  68:    */   {
/*  69:152 */     this.category = value;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public Double getChiSquareValue()
/*  73:    */   {
/*  74:164 */     return this.chiSquareValue;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public void setChiSquareValue(Double value)
/*  78:    */   {
/*  79:176 */     this.chiSquareValue = value;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public BigDecimal getConfidenceLevel()
/*  83:    */   {
/*  84:188 */     if (this.confidenceLevel == null) {
/*  85:189 */       return new BigDecimal("0.95");
/*  86:    */     }
/*  87:191 */     return this.confidenceLevel;
/*  88:    */   }
/*  89:    */   
/*  90:    */   public void setConfidenceLevel(BigDecimal value)
/*  91:    */   {
/*  92:204 */     this.confidenceLevel = value;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public Double getConfidenceLowerBound()
/*  96:    */   {
/*  97:216 */     return this.confidenceLowerBound;
/*  98:    */   }
/*  99:    */   
/* 100:    */   public void setConfidenceLowerBound(Double value)
/* 101:    */   {
/* 102:228 */     this.confidenceLowerBound = value;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public Double getConfidenceUpperBound()
/* 106:    */   {
/* 107:240 */     return this.confidenceUpperBound;
/* 108:    */   }
/* 109:    */   
/* 110:    */   public void setConfidenceUpperBound(Double value)
/* 111:    */   {
/* 112:252 */     this.confidenceUpperBound = value;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public Double getDF()
/* 116:    */   {
/* 117:264 */     return this.df;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public void setDF(Double value)
/* 121:    */   {
/* 122:276 */     this.df = value;
/* 123:    */   }
/* 124:    */   
/* 125:    */   public BigInteger getExponent()
/* 126:    */   {
/* 127:288 */     if (this.exponent == null) {
/* 128:289 */       return new BigInteger("1");
/* 129:    */     }
/* 130:291 */     return this.exponent;
/* 131:    */   }
/* 132:    */   
/* 133:    */   public void setExponent(BigInteger value)
/* 134:    */   {
/* 135:304 */     this.exponent = value;
/* 136:    */   }
/* 137:    */   
/* 138:    */   public Double getFStatistic()
/* 139:    */   {
/* 140:316 */     return this.fStatistic;
/* 141:    */   }
/* 142:    */   
/* 143:    */   public void setFStatistic(Double value)
/* 144:    */   {
/* 145:328 */     this.fStatistic = value;
/* 146:    */   }
/* 147:    */   
/* 148:    */   public BigDecimal getImportance()
/* 149:    */   {
/* 150:340 */     return this.importance;
/* 151:    */   }
/* 152:    */   
/* 153:    */   public void setImportance(BigDecimal value)
/* 154:    */   {
/* 155:352 */     this.importance = value;
/* 156:    */   }
/* 157:    */   
/* 158:    */   public boolean isIsIntercept()
/* 159:    */   {
/* 160:364 */     if (this.isIntercept == null) {
/* 161:365 */       return false;
/* 162:    */     }
/* 163:367 */     return this.isIntercept.booleanValue();
/* 164:    */   }
/* 165:    */   
/* 166:    */   public void setIsIntercept(Boolean value)
/* 167:    */   {
/* 168:380 */     this.isIntercept = value;
/* 169:    */   }
/* 170:    */   
/* 171:    */   public String getName()
/* 172:    */   {
/* 173:392 */     return this.name;
/* 174:    */   }
/* 175:    */   
/* 176:    */   public void setName(String value)
/* 177:    */   {
/* 178:404 */     this.name = value;
/* 179:    */   }
/* 180:    */   
/* 181:    */   public BigDecimal getPValueAlpha()
/* 182:    */   {
/* 183:416 */     return this.pValueAlpha;
/* 184:    */   }
/* 185:    */   
/* 186:    */   public void setPValueAlpha(BigDecimal value)
/* 187:    */   {
/* 188:428 */     this.pValueAlpha = value;
/* 189:    */   }
/* 190:    */   
/* 191:    */   public BigDecimal getPValueFinal()
/* 192:    */   {
/* 193:440 */     return this.pValueFinal;
/* 194:    */   }
/* 195:    */   
/* 196:    */   public void setPValueFinal(BigDecimal value)
/* 197:    */   {
/* 198:452 */     this.pValueFinal = value;
/* 199:    */   }
/* 200:    */   
/* 201:    */   public BigDecimal getPValueInitial()
/* 202:    */   {
/* 203:464 */     return this.pValueInitial;
/* 204:    */   }
/* 205:    */   
/* 206:    */   public void setPValueInitial(BigDecimal value)
/* 207:    */   {
/* 208:476 */     this.pValueInitial = value;
/* 209:    */   }
/* 210:    */   
/* 211:    */   public Double getStdError()
/* 212:    */   {
/* 213:488 */     return this.stdError;
/* 214:    */   }
/* 215:    */   
/* 216:    */   public void setStdError(Double value)
/* 217:    */   {
/* 218:500 */     this.stdError = value;
/* 219:    */   }
/* 220:    */   
/* 221:    */   public Double getTValue()
/* 222:    */   {
/* 223:512 */     return this.tValue;
/* 224:    */   }
/* 225:    */   
/* 226:    */   public void setTValue(Double value)
/* 227:    */   {
/* 228:524 */     this.tValue = value;
/* 229:    */   }
/* 230:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.MultivariateStat
 * JD-Core Version:    0.7.0.1
 */