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
/*  14:    */ @XmlType(name="", propOrder={"extension", "decisions", "constant", "fieldRef", "normContinuous", "normDiscrete", "discretize", "mapValues", "apply", "aggregate"})
/*  15:    */ @XmlRootElement(name="OutputField")
/*  16:    */ public class OutputField
/*  17:    */ {
/*  18:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected List<Extension> extension;
/*  20:    */   @XmlElement(name="Decisions", namespace="http://www.dmg.org/PMML-4_1")
/*  21:    */   protected Decisions decisions;
/*  22:    */   @XmlElement(name="Constant", namespace="http://www.dmg.org/PMML-4_1")
/*  23:    */   protected Constant constant;
/*  24:    */   @XmlElement(name="FieldRef", namespace="http://www.dmg.org/PMML-4_1")
/*  25:    */   protected FieldRef fieldRef;
/*  26:    */   @XmlElement(name="NormContinuous", namespace="http://www.dmg.org/PMML-4_1")
/*  27:    */   protected NormContinuous normContinuous;
/*  28:    */   @XmlElement(name="NormDiscrete", namespace="http://www.dmg.org/PMML-4_1")
/*  29:    */   protected NormDiscrete normDiscrete;
/*  30:    */   @XmlElement(name="Discretize", namespace="http://www.dmg.org/PMML-4_1")
/*  31:    */   protected Discretize discretize;
/*  32:    */   @XmlElement(name="MapValues", namespace="http://www.dmg.org/PMML-4_1")
/*  33:    */   protected MapValues mapValues;
/*  34:    */   @XmlElement(name="Apply", namespace="http://www.dmg.org/PMML-4_1")
/*  35:    */   protected Apply apply;
/*  36:    */   @XmlElement(name="Aggregate", namespace="http://www.dmg.org/PMML-4_1")
/*  37:    */   protected Aggregate aggregate;
/*  38:    */   @XmlAttribute
/*  39:    */   protected String algorithm;
/*  40:    */   @XmlAttribute
/*  41:    */   protected DATATYPE dataType;
/*  42:    */   @XmlAttribute
/*  43:    */   protected String displayName;
/*  44:    */   @XmlAttribute
/*  45:    */   protected RESULTFEATURE feature;
/*  46:    */   @XmlAttribute
/*  47:    */   protected String isMultiValued;
/*  48:    */   @XmlAttribute(required=true)
/*  49:    */   protected String name;
/*  50:    */   @XmlAttribute
/*  51:    */   protected OPTYPE optype;
/*  52:    */   @XmlAttribute
/*  53:    */   protected BigInteger rank;
/*  54:    */   @XmlAttribute
/*  55:    */   protected String rankBasis;
/*  56:    */   @XmlAttribute
/*  57:    */   protected String rankOrder;
/*  58:    */   @XmlAttribute
/*  59:    */   protected RULEFEATURE ruleFeature;
/*  60:    */   @XmlAttribute
/*  61:    */   protected String segmentId;
/*  62:    */   @XmlAttribute
/*  63:    */   protected String targetField;
/*  64:    */   @XmlAttribute
/*  65:    */   protected String value;
/*  66:    */   
/*  67:    */   public List<Extension> getExtension()
/*  68:    */   {
/*  69:174 */     if (this.extension == null) {
/*  70:175 */       this.extension = new ArrayList();
/*  71:    */     }
/*  72:177 */     return this.extension;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public Decisions getDecisions()
/*  76:    */   {
/*  77:189 */     return this.decisions;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public void setDecisions(Decisions value)
/*  81:    */   {
/*  82:201 */     this.decisions = value;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public Constant getConstant()
/*  86:    */   {
/*  87:213 */     return this.constant;
/*  88:    */   }
/*  89:    */   
/*  90:    */   public void setConstant(Constant value)
/*  91:    */   {
/*  92:225 */     this.constant = value;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public FieldRef getFieldRef()
/*  96:    */   {
/*  97:237 */     return this.fieldRef;
/*  98:    */   }
/*  99:    */   
/* 100:    */   public void setFieldRef(FieldRef value)
/* 101:    */   {
/* 102:249 */     this.fieldRef = value;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public NormContinuous getNormContinuous()
/* 106:    */   {
/* 107:261 */     return this.normContinuous;
/* 108:    */   }
/* 109:    */   
/* 110:    */   public void setNormContinuous(NormContinuous value)
/* 111:    */   {
/* 112:273 */     this.normContinuous = value;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public NormDiscrete getNormDiscrete()
/* 116:    */   {
/* 117:285 */     return this.normDiscrete;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public void setNormDiscrete(NormDiscrete value)
/* 121:    */   {
/* 122:297 */     this.normDiscrete = value;
/* 123:    */   }
/* 124:    */   
/* 125:    */   public Discretize getDiscretize()
/* 126:    */   {
/* 127:309 */     return this.discretize;
/* 128:    */   }
/* 129:    */   
/* 130:    */   public void setDiscretize(Discretize value)
/* 131:    */   {
/* 132:321 */     this.discretize = value;
/* 133:    */   }
/* 134:    */   
/* 135:    */   public MapValues getMapValues()
/* 136:    */   {
/* 137:333 */     return this.mapValues;
/* 138:    */   }
/* 139:    */   
/* 140:    */   public void setMapValues(MapValues value)
/* 141:    */   {
/* 142:345 */     this.mapValues = value;
/* 143:    */   }
/* 144:    */   
/* 145:    */   public Apply getApply()
/* 146:    */   {
/* 147:357 */     return this.apply;
/* 148:    */   }
/* 149:    */   
/* 150:    */   public void setApply(Apply value)
/* 151:    */   {
/* 152:369 */     this.apply = value;
/* 153:    */   }
/* 154:    */   
/* 155:    */   public Aggregate getAggregate()
/* 156:    */   {
/* 157:381 */     return this.aggregate;
/* 158:    */   }
/* 159:    */   
/* 160:    */   public void setAggregate(Aggregate value)
/* 161:    */   {
/* 162:393 */     this.aggregate = value;
/* 163:    */   }
/* 164:    */   
/* 165:    */   public String getAlgorithm()
/* 166:    */   {
/* 167:405 */     if (this.algorithm == null) {
/* 168:406 */       return "exclusiveRecommendation";
/* 169:    */     }
/* 170:408 */     return this.algorithm;
/* 171:    */   }
/* 172:    */   
/* 173:    */   public void setAlgorithm(String value)
/* 174:    */   {
/* 175:421 */     this.algorithm = value;
/* 176:    */   }
/* 177:    */   
/* 178:    */   public DATATYPE getDataType()
/* 179:    */   {
/* 180:433 */     return this.dataType;
/* 181:    */   }
/* 182:    */   
/* 183:    */   public void setDataType(DATATYPE value)
/* 184:    */   {
/* 185:445 */     this.dataType = value;
/* 186:    */   }
/* 187:    */   
/* 188:    */   public String getDisplayName()
/* 189:    */   {
/* 190:457 */     return this.displayName;
/* 191:    */   }
/* 192:    */   
/* 193:    */   public void setDisplayName(String value)
/* 194:    */   {
/* 195:469 */     this.displayName = value;
/* 196:    */   }
/* 197:    */   
/* 198:    */   public RESULTFEATURE getFeature()
/* 199:    */   {
/* 200:481 */     return this.feature;
/* 201:    */   }
/* 202:    */   
/* 203:    */   public void setFeature(RESULTFEATURE value)
/* 204:    */   {
/* 205:493 */     this.feature = value;
/* 206:    */   }
/* 207:    */   
/* 208:    */   public String getIsMultiValued()
/* 209:    */   {
/* 210:505 */     if (this.isMultiValued == null) {
/* 211:506 */       return "0";
/* 212:    */     }
/* 213:508 */     return this.isMultiValued;
/* 214:    */   }
/* 215:    */   
/* 216:    */   public void setIsMultiValued(String value)
/* 217:    */   {
/* 218:521 */     this.isMultiValued = value;
/* 219:    */   }
/* 220:    */   
/* 221:    */   public String getName()
/* 222:    */   {
/* 223:533 */     return this.name;
/* 224:    */   }
/* 225:    */   
/* 226:    */   public void setName(String value)
/* 227:    */   {
/* 228:545 */     this.name = value;
/* 229:    */   }
/* 230:    */   
/* 231:    */   public OPTYPE getOptype()
/* 232:    */   {
/* 233:557 */     return this.optype;
/* 234:    */   }
/* 235:    */   
/* 236:    */   public void setOptype(OPTYPE value)
/* 237:    */   {
/* 238:569 */     this.optype = value;
/* 239:    */   }
/* 240:    */   
/* 241:    */   public BigInteger getRank()
/* 242:    */   {
/* 243:581 */     if (this.rank == null) {
/* 244:582 */       return new BigInteger("1");
/* 245:    */     }
/* 246:584 */     return this.rank;
/* 247:    */   }
/* 248:    */   
/* 249:    */   public void setRank(BigInteger value)
/* 250:    */   {
/* 251:597 */     this.rank = value;
/* 252:    */   }
/* 253:    */   
/* 254:    */   public String getRankBasis()
/* 255:    */   {
/* 256:609 */     if (this.rankBasis == null) {
/* 257:610 */       return "confidence";
/* 258:    */     }
/* 259:612 */     return this.rankBasis;
/* 260:    */   }
/* 261:    */   
/* 262:    */   public void setRankBasis(String value)
/* 263:    */   {
/* 264:625 */     this.rankBasis = value;
/* 265:    */   }
/* 266:    */   
/* 267:    */   public String getRankOrder()
/* 268:    */   {
/* 269:637 */     if (this.rankOrder == null) {
/* 270:638 */       return "descending";
/* 271:    */     }
/* 272:640 */     return this.rankOrder;
/* 273:    */   }
/* 274:    */   
/* 275:    */   public void setRankOrder(String value)
/* 276:    */   {
/* 277:653 */     this.rankOrder = value;
/* 278:    */   }
/* 279:    */   
/* 280:    */   public RULEFEATURE getRuleFeature()
/* 281:    */   {
/* 282:665 */     if (this.ruleFeature == null) {
/* 283:666 */       return RULEFEATURE.CONSEQUENT;
/* 284:    */     }
/* 285:668 */     return this.ruleFeature;
/* 286:    */   }
/* 287:    */   
/* 288:    */   public void setRuleFeature(RULEFEATURE value)
/* 289:    */   {
/* 290:681 */     this.ruleFeature = value;
/* 291:    */   }
/* 292:    */   
/* 293:    */   public String getSegmentId()
/* 294:    */   {
/* 295:693 */     return this.segmentId;
/* 296:    */   }
/* 297:    */   
/* 298:    */   public void setSegmentId(String value)
/* 299:    */   {
/* 300:705 */     this.segmentId = value;
/* 301:    */   }
/* 302:    */   
/* 303:    */   public String getTargetField()
/* 304:    */   {
/* 305:717 */     return this.targetField;
/* 306:    */   }
/* 307:    */   
/* 308:    */   public void setTargetField(String value)
/* 309:    */   {
/* 310:729 */     this.targetField = value;
/* 311:    */   }
/* 312:    */   
/* 313:    */   public String getValue()
/* 314:    */   {
/* 315:741 */     return this.value;
/* 316:    */   }
/* 317:    */   
/* 318:    */   public void setValue(String value)
/* 319:    */   {
/* 320:753 */     this.value = value;
/* 321:    */   }
/* 322:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.OutputField
 * JD-Core Version:    0.7.0.1
 */