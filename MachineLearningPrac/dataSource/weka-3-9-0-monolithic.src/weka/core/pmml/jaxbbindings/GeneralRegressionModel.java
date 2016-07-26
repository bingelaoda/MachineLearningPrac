/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.math.BigInteger;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.List;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   7:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   8:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   9:    */ import javax.xml.bind.annotation.XmlElementRefs;
/*  10:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  11:    */ import javax.xml.bind.annotation.XmlType;
/*  12:    */ 
/*  13:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  14:    */ @XmlType(name="", propOrder={"content"})
/*  15:    */ @XmlRootElement(name="GeneralRegressionModel")
/*  16:    */ public class GeneralRegressionModel
/*  17:    */ {
/*  18:    */   @XmlElementRefs({@javax.xml.bind.annotation.XmlElementRef(name="ModelExplanation", namespace="http://www.dmg.org/PMML-4_1", type=ModelExplanation.class), @javax.xml.bind.annotation.XmlElementRef(name="EventValues", namespace="http://www.dmg.org/PMML-4_1", type=EventValues.class), @javax.xml.bind.annotation.XmlElementRef(name="Output", namespace="http://www.dmg.org/PMML-4_1", type=Output.class), @javax.xml.bind.annotation.XmlElementRef(name="LocalTransformations", namespace="http://www.dmg.org/PMML-4_1", type=LocalTransformations.class), @javax.xml.bind.annotation.XmlElementRef(name="PPMatrix", namespace="http://www.dmg.org/PMML-4_1", type=PPMatrix.class), @javax.xml.bind.annotation.XmlElementRef(name="Targets", namespace="http://www.dmg.org/PMML-4_1", type=Targets.class), @javax.xml.bind.annotation.XmlElementRef(name="ParameterList", namespace="http://www.dmg.org/PMML-4_1", type=ParameterList.class), @javax.xml.bind.annotation.XmlElementRef(name="ModelVerification", namespace="http://www.dmg.org/PMML-4_1", type=ModelVerification.class), @javax.xml.bind.annotation.XmlElementRef(name="Extension", namespace="http://www.dmg.org/PMML-4_1", type=Extension.class), @javax.xml.bind.annotation.XmlElementRef(name="ModelStats", namespace="http://www.dmg.org/PMML-4_1", type=ModelStats.class), @javax.xml.bind.annotation.XmlElementRef(name="CovariateList", namespace="http://www.dmg.org/PMML-4_1", type=CovariateList.class), @javax.xml.bind.annotation.XmlElementRef(name="BaseCumHazardTables", namespace="http://www.dmg.org/PMML-4_1", type=BaseCumHazardTables.class), @javax.xml.bind.annotation.XmlElementRef(name="PCovMatrix", namespace="http://www.dmg.org/PMML-4_1", type=PCovMatrix.class), @javax.xml.bind.annotation.XmlElementRef(name="FactorList", namespace="http://www.dmg.org/PMML-4_1", type=FactorList.class), @javax.xml.bind.annotation.XmlElementRef(name="MiningSchema", namespace="http://www.dmg.org/PMML-4_1", type=MiningSchema.class), @javax.xml.bind.annotation.XmlElementRef(name="ParamMatrix", namespace="http://www.dmg.org/PMML-4_1", type=ParamMatrix.class)})
/*  19:    */   protected List<Object> content;
/*  20:    */   @XmlAttribute
/*  21:    */   protected String algorithmName;
/*  22:    */   @XmlAttribute
/*  23:    */   protected String baselineStrataVariable;
/*  24:    */   @XmlAttribute
/*  25:    */   protected CUMULATIVELINKFUNCTION cumulativeLink;
/*  26:    */   @XmlAttribute
/*  27:    */   protected Double distParameter;
/*  28:    */   @XmlAttribute
/*  29:    */   protected String distribution;
/*  30:    */   @XmlAttribute
/*  31:    */   protected String endTimeVariable;
/*  32:    */   @XmlAttribute(required=true)
/*  33:    */   protected MININGFUNCTION functionName;
/*  34:    */   @XmlAttribute
/*  35:    */   protected Boolean isScorable;
/*  36:    */   @XmlAttribute
/*  37:    */   protected LINKFUNCTION linkFunction;
/*  38:    */   @XmlAttribute
/*  39:    */   protected Double linkParameter;
/*  40:    */   @XmlAttribute
/*  41:    */   protected Double modelDF;
/*  42:    */   @XmlAttribute
/*  43:    */   protected String modelName;
/*  44:    */   @XmlAttribute(required=true)
/*  45:    */   protected String modelType;
/*  46:    */   @XmlAttribute
/*  47:    */   protected Double offsetValue;
/*  48:    */   @XmlAttribute
/*  49:    */   protected String offsetVariable;
/*  50:    */   @XmlAttribute
/*  51:    */   protected String startTimeVariable;
/*  52:    */   @XmlAttribute
/*  53:    */   protected String statusVariable;
/*  54:    */   @XmlAttribute
/*  55:    */   protected String subjectIDVariable;
/*  56:    */   @XmlAttribute
/*  57:    */   protected String targetReferenceCategory;
/*  58:    */   @XmlAttribute
/*  59:    */   protected String targetVariableName;
/*  60:    */   @XmlAttribute
/*  61:    */   protected BigInteger trialsValue;
/*  62:    */   @XmlAttribute
/*  63:    */   protected String trialsVariable;
/*  64:    */   
/*  65:    */   public List<Object> getContent()
/*  66:    */   {
/*  67:224 */     if (this.content == null) {
/*  68:225 */       this.content = new ArrayList();
/*  69:    */     }
/*  70:227 */     return this.content;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void addContent(Object object)
/*  74:    */   {
/*  75:231 */     if (this.content == null) {
/*  76:232 */       this.content = new ArrayList();
/*  77:    */     }
/*  78:234 */     this.content.add(object);
/*  79:    */   }
/*  80:    */   
/*  81:    */   public String getAlgorithmName()
/*  82:    */   {
/*  83:246 */     return this.algorithmName;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public void setAlgorithmName(String value)
/*  87:    */   {
/*  88:258 */     this.algorithmName = value;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public String getBaselineStrataVariable()
/*  92:    */   {
/*  93:270 */     return this.baselineStrataVariable;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public void setBaselineStrataVariable(String value)
/*  97:    */   {
/*  98:282 */     this.baselineStrataVariable = value;
/*  99:    */   }
/* 100:    */   
/* 101:    */   public CUMULATIVELINKFUNCTION getCumulativeLink()
/* 102:    */   {
/* 103:294 */     return this.cumulativeLink;
/* 104:    */   }
/* 105:    */   
/* 106:    */   public void setCumulativeLink(CUMULATIVELINKFUNCTION value)
/* 107:    */   {
/* 108:306 */     this.cumulativeLink = value;
/* 109:    */   }
/* 110:    */   
/* 111:    */   public Double getDistParameter()
/* 112:    */   {
/* 113:318 */     return this.distParameter;
/* 114:    */   }
/* 115:    */   
/* 116:    */   public void setDistParameter(Double value)
/* 117:    */   {
/* 118:330 */     this.distParameter = value;
/* 119:    */   }
/* 120:    */   
/* 121:    */   public String getDistribution()
/* 122:    */   {
/* 123:342 */     return this.distribution;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public void setDistribution(String value)
/* 127:    */   {
/* 128:354 */     this.distribution = value;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public String getEndTimeVariable()
/* 132:    */   {
/* 133:366 */     return this.endTimeVariable;
/* 134:    */   }
/* 135:    */   
/* 136:    */   public void setEndTimeVariable(String value)
/* 137:    */   {
/* 138:378 */     this.endTimeVariable = value;
/* 139:    */   }
/* 140:    */   
/* 141:    */   public MININGFUNCTION getFunctionName()
/* 142:    */   {
/* 143:390 */     return this.functionName;
/* 144:    */   }
/* 145:    */   
/* 146:    */   public void setFunctionName(MININGFUNCTION value)
/* 147:    */   {
/* 148:402 */     this.functionName = value;
/* 149:    */   }
/* 150:    */   
/* 151:    */   public boolean isIsScorable()
/* 152:    */   {
/* 153:414 */     if (this.isScorable == null) {
/* 154:415 */       return true;
/* 155:    */     }
/* 156:417 */     return this.isScorable.booleanValue();
/* 157:    */   }
/* 158:    */   
/* 159:    */   public void setIsScorable(Boolean value)
/* 160:    */   {
/* 161:430 */     this.isScorable = value;
/* 162:    */   }
/* 163:    */   
/* 164:    */   public LINKFUNCTION getLinkFunction()
/* 165:    */   {
/* 166:442 */     return this.linkFunction;
/* 167:    */   }
/* 168:    */   
/* 169:    */   public void setLinkFunction(LINKFUNCTION value)
/* 170:    */   {
/* 171:454 */     this.linkFunction = value;
/* 172:    */   }
/* 173:    */   
/* 174:    */   public Double getLinkParameter()
/* 175:    */   {
/* 176:466 */     return this.linkParameter;
/* 177:    */   }
/* 178:    */   
/* 179:    */   public void setLinkParameter(Double value)
/* 180:    */   {
/* 181:478 */     this.linkParameter = value;
/* 182:    */   }
/* 183:    */   
/* 184:    */   public Double getModelDF()
/* 185:    */   {
/* 186:490 */     return this.modelDF;
/* 187:    */   }
/* 188:    */   
/* 189:    */   public void setModelDF(Double value)
/* 190:    */   {
/* 191:502 */     this.modelDF = value;
/* 192:    */   }
/* 193:    */   
/* 194:    */   public String getModelName()
/* 195:    */   {
/* 196:514 */     return this.modelName;
/* 197:    */   }
/* 198:    */   
/* 199:    */   public void setModelName(String value)
/* 200:    */   {
/* 201:526 */     this.modelName = value;
/* 202:    */   }
/* 203:    */   
/* 204:    */   public String getModelType()
/* 205:    */   {
/* 206:538 */     return this.modelType;
/* 207:    */   }
/* 208:    */   
/* 209:    */   public void setModelType(String value)
/* 210:    */   {
/* 211:550 */     this.modelType = value;
/* 212:    */   }
/* 213:    */   
/* 214:    */   public Double getOffsetValue()
/* 215:    */   {
/* 216:562 */     return this.offsetValue;
/* 217:    */   }
/* 218:    */   
/* 219:    */   public void setOffsetValue(Double value)
/* 220:    */   {
/* 221:574 */     this.offsetValue = value;
/* 222:    */   }
/* 223:    */   
/* 224:    */   public String getOffsetVariable()
/* 225:    */   {
/* 226:586 */     return this.offsetVariable;
/* 227:    */   }
/* 228:    */   
/* 229:    */   public void setOffsetVariable(String value)
/* 230:    */   {
/* 231:598 */     this.offsetVariable = value;
/* 232:    */   }
/* 233:    */   
/* 234:    */   public String getStartTimeVariable()
/* 235:    */   {
/* 236:610 */     return this.startTimeVariable;
/* 237:    */   }
/* 238:    */   
/* 239:    */   public void setStartTimeVariable(String value)
/* 240:    */   {
/* 241:622 */     this.startTimeVariable = value;
/* 242:    */   }
/* 243:    */   
/* 244:    */   public String getStatusVariable()
/* 245:    */   {
/* 246:634 */     return this.statusVariable;
/* 247:    */   }
/* 248:    */   
/* 249:    */   public void setStatusVariable(String value)
/* 250:    */   {
/* 251:646 */     this.statusVariable = value;
/* 252:    */   }
/* 253:    */   
/* 254:    */   public String getSubjectIDVariable()
/* 255:    */   {
/* 256:658 */     return this.subjectIDVariable;
/* 257:    */   }
/* 258:    */   
/* 259:    */   public void setSubjectIDVariable(String value)
/* 260:    */   {
/* 261:670 */     this.subjectIDVariable = value;
/* 262:    */   }
/* 263:    */   
/* 264:    */   public String getTargetReferenceCategory()
/* 265:    */   {
/* 266:682 */     return this.targetReferenceCategory;
/* 267:    */   }
/* 268:    */   
/* 269:    */   public void setTargetReferenceCategory(String value)
/* 270:    */   {
/* 271:694 */     this.targetReferenceCategory = value;
/* 272:    */   }
/* 273:    */   
/* 274:    */   public String getTargetVariableName()
/* 275:    */   {
/* 276:706 */     return this.targetVariableName;
/* 277:    */   }
/* 278:    */   
/* 279:    */   public void setTargetVariableName(String value)
/* 280:    */   {
/* 281:718 */     this.targetVariableName = value;
/* 282:    */   }
/* 283:    */   
/* 284:    */   public BigInteger getTrialsValue()
/* 285:    */   {
/* 286:730 */     return this.trialsValue;
/* 287:    */   }
/* 288:    */   
/* 289:    */   public void setTrialsValue(BigInteger value)
/* 290:    */   {
/* 291:742 */     this.trialsValue = value;
/* 292:    */   }
/* 293:    */   
/* 294:    */   public String getTrialsVariable()
/* 295:    */   {
/* 296:754 */     return this.trialsVariable;
/* 297:    */   }
/* 298:    */   
/* 299:    */   public void setTrialsVariable(String value)
/* 300:    */   {
/* 301:766 */     this.trialsVariable = value;
/* 302:    */   }
/* 303:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.GeneralRegressionModel
 * JD-Core Version:    0.7.0.1
 */