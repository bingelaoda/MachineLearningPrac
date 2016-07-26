/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.math.BigDecimal;
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
/*  15:    */ @XmlRootElement(name="TreeModel")
/*  16:    */ public class TreeModel
/*  17:    */ {
/*  18:    */   @XmlElementRefs({@javax.xml.bind.annotation.XmlElementRef(name="ModelExplanation", namespace="http://www.dmg.org/PMML-4_1", type=ModelExplanation.class), @javax.xml.bind.annotation.XmlElementRef(name="ModelStats", namespace="http://www.dmg.org/PMML-4_1", type=ModelStats.class), @javax.xml.bind.annotation.XmlElementRef(name="Output", namespace="http://www.dmg.org/PMML-4_1", type=Output.class), @javax.xml.bind.annotation.XmlElementRef(name="Node", namespace="http://www.dmg.org/PMML-4_1", type=Node.class), @javax.xml.bind.annotation.XmlElementRef(name="LocalTransformations", namespace="http://www.dmg.org/PMML-4_1", type=LocalTransformations.class), @javax.xml.bind.annotation.XmlElementRef(name="Targets", namespace="http://www.dmg.org/PMML-4_1", type=Targets.class), @javax.xml.bind.annotation.XmlElementRef(name="MiningSchema", namespace="http://www.dmg.org/PMML-4_1", type=MiningSchema.class), @javax.xml.bind.annotation.XmlElementRef(name="ModelVerification", namespace="http://www.dmg.org/PMML-4_1", type=ModelVerification.class), @javax.xml.bind.annotation.XmlElementRef(name="Extension", namespace="http://www.dmg.org/PMML-4_1", type=Extension.class)})
/*  19:    */   protected List<Object> content;
/*  20:    */   @XmlAttribute
/*  21:    */   protected String algorithmName;
/*  22:    */   @XmlAttribute(required=true)
/*  23:    */   protected MININGFUNCTION functionName;
/*  24:    */   @XmlAttribute
/*  25:    */   protected Boolean isScorable;
/*  26:    */   @XmlAttribute
/*  27:    */   protected BigDecimal missingValuePenalty;
/*  28:    */   @XmlAttribute
/*  29:    */   protected MISSINGVALUESTRATEGY missingValueStrategy;
/*  30:    */   @XmlAttribute
/*  31:    */   protected String modelName;
/*  32:    */   @XmlAttribute
/*  33:    */   protected NOTRUECHILDSTRATEGY noTrueChildStrategy;
/*  34:    */   @XmlAttribute
/*  35:    */   protected String splitCharacteristic;
/*  36:    */   
/*  37:    */   public List<Object> getContent()
/*  38:    */   {
/*  39:145 */     if (this.content == null) {
/*  40:146 */       this.content = new ArrayList();
/*  41:    */     }
/*  42:148 */     return this.content;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public String getAlgorithmName()
/*  46:    */   {
/*  47:160 */     return this.algorithmName;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void setAlgorithmName(String value)
/*  51:    */   {
/*  52:172 */     this.algorithmName = value;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public MININGFUNCTION getFunctionName()
/*  56:    */   {
/*  57:184 */     return this.functionName;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public void setFunctionName(MININGFUNCTION value)
/*  61:    */   {
/*  62:196 */     this.functionName = value;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public boolean isIsScorable()
/*  66:    */   {
/*  67:208 */     if (this.isScorable == null) {
/*  68:209 */       return true;
/*  69:    */     }
/*  70:211 */     return this.isScorable.booleanValue();
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void setIsScorable(Boolean value)
/*  74:    */   {
/*  75:224 */     this.isScorable = value;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public BigDecimal getMissingValuePenalty()
/*  79:    */   {
/*  80:236 */     if (this.missingValuePenalty == null) {
/*  81:237 */       return new BigDecimal("1.0");
/*  82:    */     }
/*  83:239 */     return this.missingValuePenalty;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public void setMissingValuePenalty(BigDecimal value)
/*  87:    */   {
/*  88:252 */     this.missingValuePenalty = value;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public MISSINGVALUESTRATEGY getMissingValueStrategy()
/*  92:    */   {
/*  93:264 */     if (this.missingValueStrategy == null) {
/*  94:265 */       return MISSINGVALUESTRATEGY.NONE;
/*  95:    */     }
/*  96:267 */     return this.missingValueStrategy;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public void setMissingValueStrategy(MISSINGVALUESTRATEGY value)
/* 100:    */   {
/* 101:280 */     this.missingValueStrategy = value;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public String getModelName()
/* 105:    */   {
/* 106:292 */     return this.modelName;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public void setModelName(String value)
/* 110:    */   {
/* 111:304 */     this.modelName = value;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public NOTRUECHILDSTRATEGY getNoTrueChildStrategy()
/* 115:    */   {
/* 116:316 */     if (this.noTrueChildStrategy == null) {
/* 117:317 */       return NOTRUECHILDSTRATEGY.RETURN_NULL_PREDICTION;
/* 118:    */     }
/* 119:319 */     return this.noTrueChildStrategy;
/* 120:    */   }
/* 121:    */   
/* 122:    */   public void setNoTrueChildStrategy(NOTRUECHILDSTRATEGY value)
/* 123:    */   {
/* 124:332 */     this.noTrueChildStrategy = value;
/* 125:    */   }
/* 126:    */   
/* 127:    */   public String getSplitCharacteristic()
/* 128:    */   {
/* 129:344 */     if (this.splitCharacteristic == null) {
/* 130:345 */       return "multiSplit";
/* 131:    */     }
/* 132:347 */     return this.splitCharacteristic;
/* 133:    */   }
/* 134:    */   
/* 135:    */   public void setSplitCharacteristic(String value)
/* 136:    */   {
/* 137:360 */     this.splitCharacteristic = value;
/* 138:    */   }
/* 139:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.TreeModel
 * JD-Core Version:    0.7.0.1
 */