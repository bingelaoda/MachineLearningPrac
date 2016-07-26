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
/*  15:    */ @XmlRootElement(name="NearestNeighborModel")
/*  16:    */ public class NearestNeighborModel
/*  17:    */ {
/*  18:    */   @XmlElementRefs({@javax.xml.bind.annotation.XmlElementRef(name="ModelExplanation", namespace="http://www.dmg.org/PMML-4_1", type=ModelExplanation.class), @javax.xml.bind.annotation.XmlElementRef(name="ModelStats", namespace="http://www.dmg.org/PMML-4_1", type=ModelStats.class), @javax.xml.bind.annotation.XmlElementRef(name="Output", namespace="http://www.dmg.org/PMML-4_1", type=Output.class), @javax.xml.bind.annotation.XmlElementRef(name="TrainingInstances", namespace="http://www.dmg.org/PMML-4_1", type=TrainingInstances.class), @javax.xml.bind.annotation.XmlElementRef(name="ComparisonMeasure", namespace="http://www.dmg.org/PMML-4_1", type=ComparisonMeasure.class), @javax.xml.bind.annotation.XmlElementRef(name="LocalTransformations", namespace="http://www.dmg.org/PMML-4_1", type=LocalTransformations.class), @javax.xml.bind.annotation.XmlElementRef(name="Targets", namespace="http://www.dmg.org/PMML-4_1", type=Targets.class), @javax.xml.bind.annotation.XmlElementRef(name="MiningSchema", namespace="http://www.dmg.org/PMML-4_1", type=MiningSchema.class), @javax.xml.bind.annotation.XmlElementRef(name="KNNInputs", namespace="http://www.dmg.org/PMML-4_1", type=KNNInputs.class), @javax.xml.bind.annotation.XmlElementRef(name="ModelVerification", namespace="http://www.dmg.org/PMML-4_1", type=ModelVerification.class), @javax.xml.bind.annotation.XmlElementRef(name="Extension", namespace="http://www.dmg.org/PMML-4_1", type=Extension.class)})
/*  19:    */   protected List<Object> content;
/*  20:    */   @XmlAttribute
/*  21:    */   protected String algorithmName;
/*  22:    */   @XmlAttribute
/*  23:    */   protected CATSCORINGMETHOD categoricalScoringMethod;
/*  24:    */   @XmlAttribute
/*  25:    */   protected CONTSCORINGMETHOD continuousScoringMethod;
/*  26:    */   @XmlAttribute(required=true)
/*  27:    */   protected MININGFUNCTION functionName;
/*  28:    */   @XmlAttribute
/*  29:    */   protected String instanceIdVariable;
/*  30:    */   @XmlAttribute
/*  31:    */   protected Boolean isScorable;
/*  32:    */   @XmlAttribute
/*  33:    */   protected String modelName;
/*  34:    */   @XmlAttribute(required=true)
/*  35:    */   protected BigInteger numberOfNeighbors;
/*  36:    */   @XmlAttribute
/*  37:    */   protected Double threshold;
/*  38:    */   
/*  39:    */   public List<Object> getContent()
/*  40:    */   {
/*  41:147 */     if (this.content == null) {
/*  42:148 */       this.content = new ArrayList();
/*  43:    */     }
/*  44:150 */     return this.content;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public String getAlgorithmName()
/*  48:    */   {
/*  49:162 */     return this.algorithmName;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void setAlgorithmName(String value)
/*  53:    */   {
/*  54:174 */     this.algorithmName = value;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public CATSCORINGMETHOD getCategoricalScoringMethod()
/*  58:    */   {
/*  59:186 */     if (this.categoricalScoringMethod == null) {
/*  60:187 */       return CATSCORINGMETHOD.MAJORITY_VOTE;
/*  61:    */     }
/*  62:189 */     return this.categoricalScoringMethod;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public void setCategoricalScoringMethod(CATSCORINGMETHOD value)
/*  66:    */   {
/*  67:202 */     this.categoricalScoringMethod = value;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public CONTSCORINGMETHOD getContinuousScoringMethod()
/*  71:    */   {
/*  72:214 */     if (this.continuousScoringMethod == null) {
/*  73:215 */       return CONTSCORINGMETHOD.AVERAGE;
/*  74:    */     }
/*  75:217 */     return this.continuousScoringMethod;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void setContinuousScoringMethod(CONTSCORINGMETHOD value)
/*  79:    */   {
/*  80:230 */     this.continuousScoringMethod = value;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public MININGFUNCTION getFunctionName()
/*  84:    */   {
/*  85:242 */     return this.functionName;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public void setFunctionName(MININGFUNCTION value)
/*  89:    */   {
/*  90:254 */     this.functionName = value;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public String getInstanceIdVariable()
/*  94:    */   {
/*  95:266 */     return this.instanceIdVariable;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public void setInstanceIdVariable(String value)
/*  99:    */   {
/* 100:278 */     this.instanceIdVariable = value;
/* 101:    */   }
/* 102:    */   
/* 103:    */   public boolean isIsScorable()
/* 104:    */   {
/* 105:290 */     if (this.isScorable == null) {
/* 106:291 */       return true;
/* 107:    */     }
/* 108:293 */     return this.isScorable.booleanValue();
/* 109:    */   }
/* 110:    */   
/* 111:    */   public void setIsScorable(Boolean value)
/* 112:    */   {
/* 113:306 */     this.isScorable = value;
/* 114:    */   }
/* 115:    */   
/* 116:    */   public String getModelName()
/* 117:    */   {
/* 118:318 */     return this.modelName;
/* 119:    */   }
/* 120:    */   
/* 121:    */   public void setModelName(String value)
/* 122:    */   {
/* 123:330 */     this.modelName = value;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public BigInteger getNumberOfNeighbors()
/* 127:    */   {
/* 128:342 */     return this.numberOfNeighbors;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public void setNumberOfNeighbors(BigInteger value)
/* 132:    */   {
/* 133:354 */     this.numberOfNeighbors = value;
/* 134:    */   }
/* 135:    */   
/* 136:    */   public double getThreshold()
/* 137:    */   {
/* 138:366 */     if (this.threshold == null) {
/* 139:367 */       return 0.001D;
/* 140:    */     }
/* 141:369 */     return this.threshold.doubleValue();
/* 142:    */   }
/* 143:    */   
/* 144:    */   public void setThreshold(Double value)
/* 145:    */   {
/* 146:382 */     this.threshold = value;
/* 147:    */   }
/* 148:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.NearestNeighborModel
 * JD-Core Version:    0.7.0.1
 */