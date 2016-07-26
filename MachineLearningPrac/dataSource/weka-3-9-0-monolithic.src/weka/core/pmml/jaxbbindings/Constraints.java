/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.math.BigInteger;
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
/*  15:    */ @XmlRootElement(name="Constraints")
/*  16:    */ public class Constraints
/*  17:    */ {
/*  18:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected List<Extension> extension;
/*  20:    */   @XmlAttribute
/*  21:    */   protected Double maximumAntConsSeparationTime;
/*  22:    */   @XmlAttribute
/*  23:    */   protected Double maximumItemsetSeparationTime;
/*  24:    */   @XmlAttribute
/*  25:    */   protected BigInteger maximumNumberOfAntecedentItems;
/*  26:    */   @XmlAttribute
/*  27:    */   protected BigInteger maximumNumberOfConsequentItems;
/*  28:    */   @XmlAttribute
/*  29:    */   protected BigInteger maximumNumberOfItems;
/*  30:    */   @XmlAttribute
/*  31:    */   protected Double maximumTotalSequenceTime;
/*  32:    */   @XmlAttribute
/*  33:    */   protected Double minimumAntConsSeparationTime;
/*  34:    */   @XmlAttribute
/*  35:    */   protected Double minimumConfidence;
/*  36:    */   @XmlAttribute
/*  37:    */   protected Double minimumItemsetSeparationTime;
/*  38:    */   @XmlAttribute
/*  39:    */   protected Double minimumLift;
/*  40:    */   @XmlAttribute
/*  41:    */   protected BigInteger minimumNumberOfAntecedentItems;
/*  42:    */   @XmlAttribute
/*  43:    */   protected BigInteger minimumNumberOfConsequentItems;
/*  44:    */   @XmlAttribute
/*  45:    */   protected BigInteger minimumNumberOfItems;
/*  46:    */   @XmlAttribute
/*  47:    */   protected Double minimumSupport;
/*  48:    */   @XmlAttribute
/*  49:    */   protected Double minimumTotalSequenceTime;
/*  50:    */   
/*  51:    */   public List<Extension> getExtension()
/*  52:    */   {
/*  53:121 */     if (this.extension == null) {
/*  54:122 */       this.extension = new ArrayList();
/*  55:    */     }
/*  56:124 */     return this.extension;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public Double getMaximumAntConsSeparationTime()
/*  60:    */   {
/*  61:136 */     return this.maximumAntConsSeparationTime;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public void setMaximumAntConsSeparationTime(Double value)
/*  65:    */   {
/*  66:148 */     this.maximumAntConsSeparationTime = value;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public Double getMaximumItemsetSeparationTime()
/*  70:    */   {
/*  71:160 */     return this.maximumItemsetSeparationTime;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void setMaximumItemsetSeparationTime(Double value)
/*  75:    */   {
/*  76:172 */     this.maximumItemsetSeparationTime = value;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public BigInteger getMaximumNumberOfAntecedentItems()
/*  80:    */   {
/*  81:184 */     return this.maximumNumberOfAntecedentItems;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void setMaximumNumberOfAntecedentItems(BigInteger value)
/*  85:    */   {
/*  86:196 */     this.maximumNumberOfAntecedentItems = value;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public BigInteger getMaximumNumberOfConsequentItems()
/*  90:    */   {
/*  91:208 */     return this.maximumNumberOfConsequentItems;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public void setMaximumNumberOfConsequentItems(BigInteger value)
/*  95:    */   {
/*  96:220 */     this.maximumNumberOfConsequentItems = value;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public BigInteger getMaximumNumberOfItems()
/* 100:    */   {
/* 101:232 */     return this.maximumNumberOfItems;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public void setMaximumNumberOfItems(BigInteger value)
/* 105:    */   {
/* 106:244 */     this.maximumNumberOfItems = value;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public Double getMaximumTotalSequenceTime()
/* 110:    */   {
/* 111:256 */     return this.maximumTotalSequenceTime;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public void setMaximumTotalSequenceTime(Double value)
/* 115:    */   {
/* 116:268 */     this.maximumTotalSequenceTime = value;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public double getMinimumAntConsSeparationTime()
/* 120:    */   {
/* 121:280 */     if (this.minimumAntConsSeparationTime == null) {
/* 122:281 */       return 0.0D;
/* 123:    */     }
/* 124:283 */     return this.minimumAntConsSeparationTime.doubleValue();
/* 125:    */   }
/* 126:    */   
/* 127:    */   public void setMinimumAntConsSeparationTime(Double value)
/* 128:    */   {
/* 129:296 */     this.minimumAntConsSeparationTime = value;
/* 130:    */   }
/* 131:    */   
/* 132:    */   public double getMinimumConfidence()
/* 133:    */   {
/* 134:308 */     if (this.minimumConfidence == null) {
/* 135:309 */       return 0.0D;
/* 136:    */     }
/* 137:311 */     return this.minimumConfidence.doubleValue();
/* 138:    */   }
/* 139:    */   
/* 140:    */   public void setMinimumConfidence(Double value)
/* 141:    */   {
/* 142:324 */     this.minimumConfidence = value;
/* 143:    */   }
/* 144:    */   
/* 145:    */   public double getMinimumItemsetSeparationTime()
/* 146:    */   {
/* 147:336 */     if (this.minimumItemsetSeparationTime == null) {
/* 148:337 */       return 0.0D;
/* 149:    */     }
/* 150:339 */     return this.minimumItemsetSeparationTime.doubleValue();
/* 151:    */   }
/* 152:    */   
/* 153:    */   public void setMinimumItemsetSeparationTime(Double value)
/* 154:    */   {
/* 155:352 */     this.minimumItemsetSeparationTime = value;
/* 156:    */   }
/* 157:    */   
/* 158:    */   public double getMinimumLift()
/* 159:    */   {
/* 160:364 */     if (this.minimumLift == null) {
/* 161:365 */       return 0.0D;
/* 162:    */     }
/* 163:367 */     return this.minimumLift.doubleValue();
/* 164:    */   }
/* 165:    */   
/* 166:    */   public void setMinimumLift(Double value)
/* 167:    */   {
/* 168:380 */     this.minimumLift = value;
/* 169:    */   }
/* 170:    */   
/* 171:    */   public BigInteger getMinimumNumberOfAntecedentItems()
/* 172:    */   {
/* 173:392 */     if (this.minimumNumberOfAntecedentItems == null) {
/* 174:393 */       return new BigInteger("1");
/* 175:    */     }
/* 176:395 */     return this.minimumNumberOfAntecedentItems;
/* 177:    */   }
/* 178:    */   
/* 179:    */   public void setMinimumNumberOfAntecedentItems(BigInteger value)
/* 180:    */   {
/* 181:408 */     this.minimumNumberOfAntecedentItems = value;
/* 182:    */   }
/* 183:    */   
/* 184:    */   public BigInteger getMinimumNumberOfConsequentItems()
/* 185:    */   {
/* 186:420 */     if (this.minimumNumberOfConsequentItems == null) {
/* 187:421 */       return new BigInteger("1");
/* 188:    */     }
/* 189:423 */     return this.minimumNumberOfConsequentItems;
/* 190:    */   }
/* 191:    */   
/* 192:    */   public void setMinimumNumberOfConsequentItems(BigInteger value)
/* 193:    */   {
/* 194:436 */     this.minimumNumberOfConsequentItems = value;
/* 195:    */   }
/* 196:    */   
/* 197:    */   public BigInteger getMinimumNumberOfItems()
/* 198:    */   {
/* 199:448 */     if (this.minimumNumberOfItems == null) {
/* 200:449 */       return new BigInteger("1");
/* 201:    */     }
/* 202:451 */     return this.minimumNumberOfItems;
/* 203:    */   }
/* 204:    */   
/* 205:    */   public void setMinimumNumberOfItems(BigInteger value)
/* 206:    */   {
/* 207:464 */     this.minimumNumberOfItems = value;
/* 208:    */   }
/* 209:    */   
/* 210:    */   public double getMinimumSupport()
/* 211:    */   {
/* 212:476 */     if (this.minimumSupport == null) {
/* 213:477 */       return 0.0D;
/* 214:    */     }
/* 215:479 */     return this.minimumSupport.doubleValue();
/* 216:    */   }
/* 217:    */   
/* 218:    */   public void setMinimumSupport(Double value)
/* 219:    */   {
/* 220:492 */     this.minimumSupport = value;
/* 221:    */   }
/* 222:    */   
/* 223:    */   public double getMinimumTotalSequenceTime()
/* 224:    */   {
/* 225:504 */     if (this.minimumTotalSequenceTime == null) {
/* 226:505 */       return 0.0D;
/* 227:    */     }
/* 228:507 */     return this.minimumTotalSequenceTime.doubleValue();
/* 229:    */   }
/* 230:    */   
/* 231:    */   public void setMinimumTotalSequenceTime(Double value)
/* 232:    */   {
/* 233:520 */     this.minimumTotalSequenceTime = value;
/* 234:    */   }
/* 235:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Constraints
 * JD-Core Version:    0.7.0.1
 */