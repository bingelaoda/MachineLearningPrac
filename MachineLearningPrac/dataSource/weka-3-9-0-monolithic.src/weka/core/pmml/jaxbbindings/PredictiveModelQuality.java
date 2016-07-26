/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   7:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   8:    */ import javax.xml.bind.annotation.XmlElement;
/*   9:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  10:    */ import javax.xml.bind.annotation.XmlType;
/*  11:    */ 
/*  12:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  13:    */ @XmlType(name="", propOrder={"extension", "confusionMatrix", "liftData", "roc"})
/*  14:    */ @XmlRootElement(name="PredictiveModelQuality")
/*  15:    */ public class PredictiveModelQuality
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="ConfusionMatrix", namespace="http://www.dmg.org/PMML-4_1")
/*  20:    */   protected ConfusionMatrix confusionMatrix;
/*  21:    */   @XmlElement(name="LiftData", namespace="http://www.dmg.org/PMML-4_1")
/*  22:    */   protected LiftData liftData;
/*  23:    */   @XmlElement(name="ROC", namespace="http://www.dmg.org/PMML-4_1")
/*  24:    */   protected ROC roc;
/*  25:    */   @XmlAttribute(name="AIC")
/*  26:    */   protected Double aic;
/*  27:    */   @XmlAttribute(name="AICc")
/*  28:    */   protected Double aiCc;
/*  29:    */   @XmlAttribute(name="BIC")
/*  30:    */   protected Double bic;
/*  31:    */   @XmlAttribute(name="adj-r-squared")
/*  32:    */   protected Double adjRSquared;
/*  33:    */   @XmlAttribute
/*  34:    */   protected String dataName;
/*  35:    */   @XmlAttribute
/*  36:    */   protected String dataUsage;
/*  37:    */   @XmlAttribute
/*  38:    */   protected Double degreesOfFreedom;
/*  39:    */   @XmlAttribute
/*  40:    */   protected Double fStatistic;
/*  41:    */   @XmlAttribute
/*  42:    */   protected Double meanAbsoluteError;
/*  43:    */   @XmlAttribute
/*  44:    */   protected Double meanError;
/*  45:    */   @XmlAttribute
/*  46:    */   protected Double meanSquaredError;
/*  47:    */   @XmlAttribute
/*  48:    */   protected Double numOfPredictors;
/*  49:    */   @XmlAttribute
/*  50:    */   protected Double numOfRecords;
/*  51:    */   @XmlAttribute
/*  52:    */   protected Double numOfRecordsWeighted;
/*  53:    */   @XmlAttribute(name="r-squared")
/*  54:    */   protected Double rSquared;
/*  55:    */   @XmlAttribute
/*  56:    */   protected Double rootMeanSquaredError;
/*  57:    */   @XmlAttribute
/*  58:    */   protected Double sumSquaredError;
/*  59:    */   @XmlAttribute
/*  60:    */   protected Double sumSquaredRegression;
/*  61:    */   @XmlAttribute(required=true)
/*  62:    */   protected String targetField;
/*  63:    */   
/*  64:    */   public List<Extension> getExtension()
/*  65:    */   {
/*  66:152 */     if (this.extension == null) {
/*  67:153 */       this.extension = new ArrayList();
/*  68:    */     }
/*  69:155 */     return this.extension;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public ConfusionMatrix getConfusionMatrix()
/*  73:    */   {
/*  74:167 */     return this.confusionMatrix;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public void setConfusionMatrix(ConfusionMatrix value)
/*  78:    */   {
/*  79:179 */     this.confusionMatrix = value;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public LiftData getLiftData()
/*  83:    */   {
/*  84:191 */     return this.liftData;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public void setLiftData(LiftData value)
/*  88:    */   {
/*  89:203 */     this.liftData = value;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public ROC getROC()
/*  93:    */   {
/*  94:215 */     return this.roc;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public void setROC(ROC value)
/*  98:    */   {
/*  99:227 */     this.roc = value;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public Double getAIC()
/* 103:    */   {
/* 104:239 */     return this.aic;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public void setAIC(Double value)
/* 108:    */   {
/* 109:251 */     this.aic = value;
/* 110:    */   }
/* 111:    */   
/* 112:    */   public Double getAICc()
/* 113:    */   {
/* 114:263 */     return this.aiCc;
/* 115:    */   }
/* 116:    */   
/* 117:    */   public void setAICc(Double value)
/* 118:    */   {
/* 119:275 */     this.aiCc = value;
/* 120:    */   }
/* 121:    */   
/* 122:    */   public Double getBIC()
/* 123:    */   {
/* 124:287 */     return this.bic;
/* 125:    */   }
/* 126:    */   
/* 127:    */   public void setBIC(Double value)
/* 128:    */   {
/* 129:299 */     this.bic = value;
/* 130:    */   }
/* 131:    */   
/* 132:    */   public Double getAdjRSquared()
/* 133:    */   {
/* 134:311 */     return this.adjRSquared;
/* 135:    */   }
/* 136:    */   
/* 137:    */   public void setAdjRSquared(Double value)
/* 138:    */   {
/* 139:323 */     this.adjRSquared = value;
/* 140:    */   }
/* 141:    */   
/* 142:    */   public String getDataName()
/* 143:    */   {
/* 144:335 */     return this.dataName;
/* 145:    */   }
/* 146:    */   
/* 147:    */   public void setDataName(String value)
/* 148:    */   {
/* 149:347 */     this.dataName = value;
/* 150:    */   }
/* 151:    */   
/* 152:    */   public String getDataUsage()
/* 153:    */   {
/* 154:359 */     if (this.dataUsage == null) {
/* 155:360 */       return "training";
/* 156:    */     }
/* 157:362 */     return this.dataUsage;
/* 158:    */   }
/* 159:    */   
/* 160:    */   public void setDataUsage(String value)
/* 161:    */   {
/* 162:375 */     this.dataUsage = value;
/* 163:    */   }
/* 164:    */   
/* 165:    */   public Double getDegreesOfFreedom()
/* 166:    */   {
/* 167:387 */     return this.degreesOfFreedom;
/* 168:    */   }
/* 169:    */   
/* 170:    */   public void setDegreesOfFreedom(Double value)
/* 171:    */   {
/* 172:399 */     this.degreesOfFreedom = value;
/* 173:    */   }
/* 174:    */   
/* 175:    */   public Double getFStatistic()
/* 176:    */   {
/* 177:411 */     return this.fStatistic;
/* 178:    */   }
/* 179:    */   
/* 180:    */   public void setFStatistic(Double value)
/* 181:    */   {
/* 182:423 */     this.fStatistic = value;
/* 183:    */   }
/* 184:    */   
/* 185:    */   public Double getMeanAbsoluteError()
/* 186:    */   {
/* 187:435 */     return this.meanAbsoluteError;
/* 188:    */   }
/* 189:    */   
/* 190:    */   public void setMeanAbsoluteError(Double value)
/* 191:    */   {
/* 192:447 */     this.meanAbsoluteError = value;
/* 193:    */   }
/* 194:    */   
/* 195:    */   public Double getMeanError()
/* 196:    */   {
/* 197:459 */     return this.meanError;
/* 198:    */   }
/* 199:    */   
/* 200:    */   public void setMeanError(Double value)
/* 201:    */   {
/* 202:471 */     this.meanError = value;
/* 203:    */   }
/* 204:    */   
/* 205:    */   public Double getMeanSquaredError()
/* 206:    */   {
/* 207:483 */     return this.meanSquaredError;
/* 208:    */   }
/* 209:    */   
/* 210:    */   public void setMeanSquaredError(Double value)
/* 211:    */   {
/* 212:495 */     this.meanSquaredError = value;
/* 213:    */   }
/* 214:    */   
/* 215:    */   public Double getNumOfPredictors()
/* 216:    */   {
/* 217:507 */     return this.numOfPredictors;
/* 218:    */   }
/* 219:    */   
/* 220:    */   public void setNumOfPredictors(Double value)
/* 221:    */   {
/* 222:519 */     this.numOfPredictors = value;
/* 223:    */   }
/* 224:    */   
/* 225:    */   public Double getNumOfRecords()
/* 226:    */   {
/* 227:531 */     return this.numOfRecords;
/* 228:    */   }
/* 229:    */   
/* 230:    */   public void setNumOfRecords(Double value)
/* 231:    */   {
/* 232:543 */     this.numOfRecords = value;
/* 233:    */   }
/* 234:    */   
/* 235:    */   public Double getNumOfRecordsWeighted()
/* 236:    */   {
/* 237:555 */     return this.numOfRecordsWeighted;
/* 238:    */   }
/* 239:    */   
/* 240:    */   public void setNumOfRecordsWeighted(Double value)
/* 241:    */   {
/* 242:567 */     this.numOfRecordsWeighted = value;
/* 243:    */   }
/* 244:    */   
/* 245:    */   public Double getRSquared()
/* 246:    */   {
/* 247:579 */     return this.rSquared;
/* 248:    */   }
/* 249:    */   
/* 250:    */   public void setRSquared(Double value)
/* 251:    */   {
/* 252:591 */     this.rSquared = value;
/* 253:    */   }
/* 254:    */   
/* 255:    */   public Double getRootMeanSquaredError()
/* 256:    */   {
/* 257:603 */     return this.rootMeanSquaredError;
/* 258:    */   }
/* 259:    */   
/* 260:    */   public void setRootMeanSquaredError(Double value)
/* 261:    */   {
/* 262:615 */     this.rootMeanSquaredError = value;
/* 263:    */   }
/* 264:    */   
/* 265:    */   public Double getSumSquaredError()
/* 266:    */   {
/* 267:627 */     return this.sumSquaredError;
/* 268:    */   }
/* 269:    */   
/* 270:    */   public void setSumSquaredError(Double value)
/* 271:    */   {
/* 272:639 */     this.sumSquaredError = value;
/* 273:    */   }
/* 274:    */   
/* 275:    */   public Double getSumSquaredRegression()
/* 276:    */   {
/* 277:651 */     return this.sumSquaredRegression;
/* 278:    */   }
/* 279:    */   
/* 280:    */   public void setSumSquaredRegression(Double value)
/* 281:    */   {
/* 282:663 */     this.sumSquaredRegression = value;
/* 283:    */   }
/* 284:    */   
/* 285:    */   public String getTargetField()
/* 286:    */   {
/* 287:675 */     return this.targetField;
/* 288:    */   }
/* 289:    */   
/* 290:    */   public void setTargetField(String value)
/* 291:    */   {
/* 292:687 */     this.targetField = value;
/* 293:    */   }
/* 294:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.PredictiveModelQuality
 * JD-Core Version:    0.7.0.1
 */