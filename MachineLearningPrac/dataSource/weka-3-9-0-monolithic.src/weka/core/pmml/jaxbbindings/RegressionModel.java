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
/*  14:    */ @XmlRootElement(name="RegressionModel")
/*  15:    */ public class RegressionModel
/*  16:    */ {
/*  17:    */   @XmlElementRefs({@javax.xml.bind.annotation.XmlElementRef(name="ModelExplanation", namespace="http://www.dmg.org/PMML-4_1", type=ModelExplanation.class), @javax.xml.bind.annotation.XmlElementRef(name="ModelStats", namespace="http://www.dmg.org/PMML-4_1", type=ModelStats.class), @javax.xml.bind.annotation.XmlElementRef(name="Output", namespace="http://www.dmg.org/PMML-4_1", type=Output.class), @javax.xml.bind.annotation.XmlElementRef(name="LocalTransformations", namespace="http://www.dmg.org/PMML-4_1", type=LocalTransformations.class), @javax.xml.bind.annotation.XmlElementRef(name="Targets", namespace="http://www.dmg.org/PMML-4_1", type=Targets.class), @javax.xml.bind.annotation.XmlElementRef(name="MiningSchema", namespace="http://www.dmg.org/PMML-4_1", type=MiningSchema.class), @javax.xml.bind.annotation.XmlElementRef(name="ModelVerification", namespace="http://www.dmg.org/PMML-4_1", type=ModelVerification.class), @javax.xml.bind.annotation.XmlElementRef(name="RegressionTable", namespace="http://www.dmg.org/PMML-4_1", type=RegressionTable.class), @javax.xml.bind.annotation.XmlElementRef(name="Extension", namespace="http://www.dmg.org/PMML-4_1", type=Extension.class)})
/*  18:    */   protected List<Object> content;
/*  19:    */   @XmlAttribute
/*  20:    */   protected String algorithmName;
/*  21:    */   @XmlAttribute(required=true)
/*  22:    */   protected MININGFUNCTION functionName;
/*  23:    */   @XmlAttribute
/*  24:    */   protected Boolean isScorable;
/*  25:    */   @XmlAttribute
/*  26:    */   protected String modelName;
/*  27:    */   @XmlAttribute
/*  28:    */   protected String modelType;
/*  29:    */   @XmlAttribute
/*  30:    */   protected REGRESSIONNORMALIZATIONMETHOD normalizationMethod;
/*  31:    */   @XmlAttribute
/*  32:    */   protected String targetFieldName;
/*  33:    */   
/*  34:    */   public List<Object> getContent()
/*  35:    */   {
/*  36:142 */     if (this.content == null) {
/*  37:143 */       this.content = new ArrayList();
/*  38:    */     }
/*  39:145 */     return this.content;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public void addContent(Object object)
/*  43:    */   {
/*  44:149 */     if (this.content == null) {
/*  45:150 */       this.content = new ArrayList();
/*  46:    */     }
/*  47:152 */     this.content.add(object);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public String getAlgorithmName()
/*  51:    */   {
/*  52:164 */     return this.algorithmName;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public void setAlgorithmName(String value)
/*  56:    */   {
/*  57:176 */     this.algorithmName = value;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public MININGFUNCTION getFunctionName()
/*  61:    */   {
/*  62:188 */     return this.functionName;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public void setFunctionName(MININGFUNCTION value)
/*  66:    */   {
/*  67:200 */     this.functionName = value;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public boolean isIsScorable()
/*  71:    */   {
/*  72:212 */     if (this.isScorable == null) {
/*  73:213 */       return true;
/*  74:    */     }
/*  75:215 */     return this.isScorable.booleanValue();
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void setIsScorable(Boolean value)
/*  79:    */   {
/*  80:228 */     this.isScorable = value;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public String getModelName()
/*  84:    */   {
/*  85:240 */     return this.modelName;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public void setModelName(String value)
/*  89:    */   {
/*  90:252 */     this.modelName = value;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public String getModelType()
/*  94:    */   {
/*  95:264 */     return this.modelType;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public void setModelType(String value)
/*  99:    */   {
/* 100:276 */     this.modelType = value;
/* 101:    */   }
/* 102:    */   
/* 103:    */   public REGRESSIONNORMALIZATIONMETHOD getNormalizationMethod()
/* 104:    */   {
/* 105:288 */     if (this.normalizationMethod == null) {
/* 106:289 */       return REGRESSIONNORMALIZATIONMETHOD.NONE;
/* 107:    */     }
/* 108:291 */     return this.normalizationMethod;
/* 109:    */   }
/* 110:    */   
/* 111:    */   public void setNormalizationMethod(REGRESSIONNORMALIZATIONMETHOD value)
/* 112:    */   {
/* 113:304 */     this.normalizationMethod = value;
/* 114:    */   }
/* 115:    */   
/* 116:    */   public String getTargetFieldName()
/* 117:    */   {
/* 118:316 */     return this.targetFieldName;
/* 119:    */   }
/* 120:    */   
/* 121:    */   public void setTargetFieldName(String value)
/* 122:    */   {
/* 123:328 */     this.targetFieldName = value;
/* 124:    */   }
/* 125:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.RegressionModel
 * JD-Core Version:    0.7.0.1
 */