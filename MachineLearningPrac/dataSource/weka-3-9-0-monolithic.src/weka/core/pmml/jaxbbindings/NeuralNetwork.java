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
/*  15:    */ @XmlRootElement(name="NeuralNetwork")
/*  16:    */ public class NeuralNetwork
/*  17:    */ {
/*  18:    */   @XmlElementRefs({@javax.xml.bind.annotation.XmlElementRef(name="ModelExplanation", namespace="http://www.dmg.org/PMML-4_1", type=ModelExplanation.class), @javax.xml.bind.annotation.XmlElementRef(name="ModelStats", namespace="http://www.dmg.org/PMML-4_1", type=ModelStats.class), @javax.xml.bind.annotation.XmlElementRef(name="Output", namespace="http://www.dmg.org/PMML-4_1", type=Output.class), @javax.xml.bind.annotation.XmlElementRef(name="LocalTransformations", namespace="http://www.dmg.org/PMML-4_1", type=LocalTransformations.class), @javax.xml.bind.annotation.XmlElementRef(name="NeuralLayer", namespace="http://www.dmg.org/PMML-4_1", type=NeuralLayer.class), @javax.xml.bind.annotation.XmlElementRef(name="Targets", namespace="http://www.dmg.org/PMML-4_1", type=Targets.class), @javax.xml.bind.annotation.XmlElementRef(name="NeuralOutputs", namespace="http://www.dmg.org/PMML-4_1", type=NeuralOutputs.class), @javax.xml.bind.annotation.XmlElementRef(name="MiningSchema", namespace="http://www.dmg.org/PMML-4_1", type=MiningSchema.class), @javax.xml.bind.annotation.XmlElementRef(name="NeuralInputs", namespace="http://www.dmg.org/PMML-4_1", type=NeuralInputs.class), @javax.xml.bind.annotation.XmlElementRef(name="ModelVerification", namespace="http://www.dmg.org/PMML-4_1", type=ModelVerification.class), @javax.xml.bind.annotation.XmlElementRef(name="Extension", namespace="http://www.dmg.org/PMML-4_1", type=Extension.class)})
/*  19:    */   protected List<Object> content;
/*  20:    */   @XmlAttribute(required=true)
/*  21:    */   protected ACTIVATIONFUNCTION activationFunction;
/*  22:    */   @XmlAttribute
/*  23:    */   protected String algorithmName;
/*  24:    */   @XmlAttribute
/*  25:    */   protected Double altitude;
/*  26:    */   @XmlAttribute(required=true)
/*  27:    */   protected MININGFUNCTION functionName;
/*  28:    */   @XmlAttribute
/*  29:    */   protected Boolean isScorable;
/*  30:    */   @XmlAttribute
/*  31:    */   protected String modelName;
/*  32:    */   @XmlAttribute
/*  33:    */   protected NNNORMALIZATIONMETHOD normalizationMethod;
/*  34:    */   @XmlAttribute
/*  35:    */   protected BigInteger numberOfLayers;
/*  36:    */   @XmlAttribute
/*  37:    */   protected Double threshold;
/*  38:    */   @XmlAttribute
/*  39:    */   protected Double width;
/*  40:    */   
/*  41:    */   public List<Object> getContent()
/*  42:    */   {
/*  43:150 */     if (this.content == null) {
/*  44:151 */       this.content = new ArrayList();
/*  45:    */     }
/*  46:153 */     return this.content;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public ACTIVATIONFUNCTION getActivationFunction()
/*  50:    */   {
/*  51:165 */     return this.activationFunction;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public void setActivationFunction(ACTIVATIONFUNCTION value)
/*  55:    */   {
/*  56:177 */     this.activationFunction = value;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public String getAlgorithmName()
/*  60:    */   {
/*  61:189 */     return this.algorithmName;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public void setAlgorithmName(String value)
/*  65:    */   {
/*  66:201 */     this.algorithmName = value;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public double getAltitude()
/*  70:    */   {
/*  71:213 */     if (this.altitude == null) {
/*  72:214 */       return 1.0D;
/*  73:    */     }
/*  74:216 */     return this.altitude.doubleValue();
/*  75:    */   }
/*  76:    */   
/*  77:    */   public void setAltitude(Double value)
/*  78:    */   {
/*  79:229 */     this.altitude = value;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public MININGFUNCTION getFunctionName()
/*  83:    */   {
/*  84:241 */     return this.functionName;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public void setFunctionName(MININGFUNCTION value)
/*  88:    */   {
/*  89:253 */     this.functionName = value;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public boolean isIsScorable()
/*  93:    */   {
/*  94:265 */     if (this.isScorable == null) {
/*  95:266 */       return true;
/*  96:    */     }
/*  97:268 */     return this.isScorable.booleanValue();
/*  98:    */   }
/*  99:    */   
/* 100:    */   public void setIsScorable(Boolean value)
/* 101:    */   {
/* 102:281 */     this.isScorable = value;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public String getModelName()
/* 106:    */   {
/* 107:293 */     return this.modelName;
/* 108:    */   }
/* 109:    */   
/* 110:    */   public void setModelName(String value)
/* 111:    */   {
/* 112:305 */     this.modelName = value;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public NNNORMALIZATIONMETHOD getNormalizationMethod()
/* 116:    */   {
/* 117:317 */     if (this.normalizationMethod == null) {
/* 118:318 */       return NNNORMALIZATIONMETHOD.NONE;
/* 119:    */     }
/* 120:320 */     return this.normalizationMethod;
/* 121:    */   }
/* 122:    */   
/* 123:    */   public void setNormalizationMethod(NNNORMALIZATIONMETHOD value)
/* 124:    */   {
/* 125:333 */     this.normalizationMethod = value;
/* 126:    */   }
/* 127:    */   
/* 128:    */   public BigInteger getNumberOfLayers()
/* 129:    */   {
/* 130:345 */     return this.numberOfLayers;
/* 131:    */   }
/* 132:    */   
/* 133:    */   public void setNumberOfLayers(BigInteger value)
/* 134:    */   {
/* 135:357 */     this.numberOfLayers = value;
/* 136:    */   }
/* 137:    */   
/* 138:    */   public double getThreshold()
/* 139:    */   {
/* 140:369 */     if (this.threshold == null) {
/* 141:370 */       return 0.0D;
/* 142:    */     }
/* 143:372 */     return this.threshold.doubleValue();
/* 144:    */   }
/* 145:    */   
/* 146:    */   public void setThreshold(Double value)
/* 147:    */   {
/* 148:385 */     this.threshold = value;
/* 149:    */   }
/* 150:    */   
/* 151:    */   public Double getWidth()
/* 152:    */   {
/* 153:397 */     return this.width;
/* 154:    */   }
/* 155:    */   
/* 156:    */   public void setWidth(Double value)
/* 157:    */   {
/* 158:409 */     this.width = value;
/* 159:    */   }
/* 160:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.NeuralNetwork
 * JD-Core Version:    0.7.0.1
 */