/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   7:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   8:    */ import javax.xml.bind.annotation.XmlElementRefs;
/*   9:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  10:    */ import javax.xml.bind.annotation.XmlType;
/*  11:    */ 
/*  12:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  13:    */ @XmlType(name="", propOrder={"content"})
/*  14:    */ @XmlRootElement(name="Scorecard")
/*  15:    */ public class Scorecard
/*  16:    */ {
/*  17:    */   @XmlElementRefs({@javax.xml.bind.annotation.XmlElementRef(name="ModelExplanation", namespace="http://www.dmg.org/PMML-4_1", type=ModelExplanation.class), @javax.xml.bind.annotation.XmlElementRef(name="ModelStats", namespace="http://www.dmg.org/PMML-4_1", type=ModelStats.class), @javax.xml.bind.annotation.XmlElementRef(name="Characteristics", namespace="http://www.dmg.org/PMML-4_1", type=Characteristics.class), @javax.xml.bind.annotation.XmlElementRef(name="Output", namespace="http://www.dmg.org/PMML-4_1", type=Output.class), @javax.xml.bind.annotation.XmlElementRef(name="LocalTransformations", namespace="http://www.dmg.org/PMML-4_1", type=LocalTransformations.class), @javax.xml.bind.annotation.XmlElementRef(name="Targets", namespace="http://www.dmg.org/PMML-4_1", type=Targets.class), @javax.xml.bind.annotation.XmlElementRef(name="MiningSchema", namespace="http://www.dmg.org/PMML-4_1", type=MiningSchema.class), @javax.xml.bind.annotation.XmlElementRef(name="ModelVerification", namespace="http://www.dmg.org/PMML-4_1", type=ModelVerification.class), @javax.xml.bind.annotation.XmlElementRef(name="Extension", namespace="http://www.dmg.org/PMML-4_1", type=Extension.class)})
/*  18:    */   protected List<Object> content;
/*  19:    */   @XmlAttribute
/*  20:    */   protected String algorithmName;
/*  21:    */   @XmlAttribute
/*  22:    */   protected String baselineMethod;
/*  23:    */   @XmlAttribute
/*  24:    */   protected Double baselineScore;
/*  25:    */   @XmlAttribute(required=true)
/*  26:    */   protected MININGFUNCTION functionName;
/*  27:    */   @XmlAttribute
/*  28:    */   protected Double initialScore;
/*  29:    */   @XmlAttribute
/*  30:    */   protected Boolean isScorable;
/*  31:    */   @XmlAttribute
/*  32:    */   protected String modelName;
/*  33:    */   @XmlAttribute
/*  34:    */   protected String reasonCodeAlgorithm;
/*  35:    */   @XmlAttribute
/*  36:    */   protected Boolean useReasonCodes;
/*  37:    */   
/*  38:    */   public List<Object> getContent()
/*  39:    */   {
/*  40:157 */     if (this.content == null) {
/*  41:158 */       this.content = new ArrayList();
/*  42:    */     }
/*  43:160 */     return this.content;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public String getAlgorithmName()
/*  47:    */   {
/*  48:172 */     return this.algorithmName;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void setAlgorithmName(String value)
/*  52:    */   {
/*  53:184 */     this.algorithmName = value;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public String getBaselineMethod()
/*  57:    */   {
/*  58:196 */     if (this.baselineMethod == null) {
/*  59:197 */       return "other";
/*  60:    */     }
/*  61:199 */     return this.baselineMethod;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public void setBaselineMethod(String value)
/*  65:    */   {
/*  66:212 */     this.baselineMethod = value;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public Double getBaselineScore()
/*  70:    */   {
/*  71:224 */     return this.baselineScore;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void setBaselineScore(Double value)
/*  75:    */   {
/*  76:236 */     this.baselineScore = value;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public MININGFUNCTION getFunctionName()
/*  80:    */   {
/*  81:248 */     return this.functionName;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void setFunctionName(MININGFUNCTION value)
/*  85:    */   {
/*  86:260 */     this.functionName = value;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public double getInitialScore()
/*  90:    */   {
/*  91:272 */     if (this.initialScore == null) {
/*  92:273 */       return 0.0D;
/*  93:    */     }
/*  94:275 */     return this.initialScore.doubleValue();
/*  95:    */   }
/*  96:    */   
/*  97:    */   public void setInitialScore(Double value)
/*  98:    */   {
/*  99:288 */     this.initialScore = value;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public boolean isIsScorable()
/* 103:    */   {
/* 104:300 */     if (this.isScorable == null) {
/* 105:301 */       return true;
/* 106:    */     }
/* 107:303 */     return this.isScorable.booleanValue();
/* 108:    */   }
/* 109:    */   
/* 110:    */   public void setIsScorable(Boolean value)
/* 111:    */   {
/* 112:316 */     this.isScorable = value;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public String getModelName()
/* 116:    */   {
/* 117:328 */     return this.modelName;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public void setModelName(String value)
/* 121:    */   {
/* 122:340 */     this.modelName = value;
/* 123:    */   }
/* 124:    */   
/* 125:    */   public String getReasonCodeAlgorithm()
/* 126:    */   {
/* 127:352 */     if (this.reasonCodeAlgorithm == null) {
/* 128:353 */       return "pointsBelow";
/* 129:    */     }
/* 130:355 */     return this.reasonCodeAlgorithm;
/* 131:    */   }
/* 132:    */   
/* 133:    */   public void setReasonCodeAlgorithm(String value)
/* 134:    */   {
/* 135:368 */     this.reasonCodeAlgorithm = value;
/* 136:    */   }
/* 137:    */   
/* 138:    */   public boolean isUseReasonCodes()
/* 139:    */   {
/* 140:380 */     if (this.useReasonCodes == null) {
/* 141:381 */       return true;
/* 142:    */     }
/* 143:383 */     return this.useReasonCodes.booleanValue();
/* 144:    */   }
/* 145:    */   
/* 146:    */   public void setUseReasonCodes(Boolean value)
/* 147:    */   {
/* 148:396 */     this.useReasonCodes = value;
/* 149:    */   }
/* 150:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Scorecard
 * JD-Core Version:    0.7.0.1
 */