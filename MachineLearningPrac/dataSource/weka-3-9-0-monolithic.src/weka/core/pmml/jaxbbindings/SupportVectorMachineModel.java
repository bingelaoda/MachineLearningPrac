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
/*  14:    */ @XmlRootElement(name="SupportVectorMachineModel")
/*  15:    */ public class SupportVectorMachineModel
/*  16:    */ {
/*  17:    */   @XmlElementRefs({@javax.xml.bind.annotation.XmlElementRef(name="ModelExplanation", namespace="http://www.dmg.org/PMML-4_1", type=ModelExplanation.class), @javax.xml.bind.annotation.XmlElementRef(name="ModelStats", namespace="http://www.dmg.org/PMML-4_1", type=ModelStats.class), @javax.xml.bind.annotation.XmlElementRef(name="PolynomialKernelType", namespace="http://www.dmg.org/PMML-4_1", type=PolynomialKernelType.class), @javax.xml.bind.annotation.XmlElementRef(name="Output", namespace="http://www.dmg.org/PMML-4_1", type=Output.class), @javax.xml.bind.annotation.XmlElementRef(name="SupportVectorMachine", namespace="http://www.dmg.org/PMML-4_1", type=SupportVectorMachine.class), @javax.xml.bind.annotation.XmlElementRef(name="LinearKernelType", namespace="http://www.dmg.org/PMML-4_1", type=LinearKernelType.class), @javax.xml.bind.annotation.XmlElementRef(name="SigmoidKernelType", namespace="http://www.dmg.org/PMML-4_1", type=SigmoidKernelType.class), @javax.xml.bind.annotation.XmlElementRef(name="LocalTransformations", namespace="http://www.dmg.org/PMML-4_1", type=LocalTransformations.class), @javax.xml.bind.annotation.XmlElementRef(name="VectorDictionary", namespace="http://www.dmg.org/PMML-4_1", type=VectorDictionary.class), @javax.xml.bind.annotation.XmlElementRef(name="Targets", namespace="http://www.dmg.org/PMML-4_1", type=Targets.class), @javax.xml.bind.annotation.XmlElementRef(name="RadialBasisKernelType", namespace="http://www.dmg.org/PMML-4_1", type=RadialBasisKernelType.class), @javax.xml.bind.annotation.XmlElementRef(name="MiningSchema", namespace="http://www.dmg.org/PMML-4_1", type=MiningSchema.class), @javax.xml.bind.annotation.XmlElementRef(name="ModelVerification", namespace="http://www.dmg.org/PMML-4_1", type=ModelVerification.class), @javax.xml.bind.annotation.XmlElementRef(name="Extension", namespace="http://www.dmg.org/PMML-4_1", type=Extension.class)})
/*  18:    */   protected List<Object> content;
/*  19:    */   @XmlAttribute
/*  20:    */   protected String algorithmName;
/*  21:    */   @XmlAttribute
/*  22:    */   protected SVMCLASSIFICATIONMETHOD classificationMethod;
/*  23:    */   @XmlAttribute(required=true)
/*  24:    */   protected MININGFUNCTION functionName;
/*  25:    */   @XmlAttribute
/*  26:    */   protected Boolean isScorable;
/*  27:    */   @XmlAttribute
/*  28:    */   protected String modelName;
/*  29:    */   @XmlAttribute
/*  30:    */   protected SVMREPRESENTATION svmRepresentation;
/*  31:    */   @XmlAttribute
/*  32:    */   protected Double threshold;
/*  33:    */   
/*  34:    */   public List<Object> getContent()
/*  35:    */   {
/*  36:153 */     if (this.content == null) {
/*  37:154 */       this.content = new ArrayList();
/*  38:    */     }
/*  39:156 */     return this.content;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public String getAlgorithmName()
/*  43:    */   {
/*  44:168 */     return this.algorithmName;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void setAlgorithmName(String value)
/*  48:    */   {
/*  49:180 */     this.algorithmName = value;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public SVMCLASSIFICATIONMETHOD getClassificationMethod()
/*  53:    */   {
/*  54:192 */     if (this.classificationMethod == null) {
/*  55:193 */       return SVMCLASSIFICATIONMETHOD.ONE_AGAINST_ALL;
/*  56:    */     }
/*  57:195 */     return this.classificationMethod;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public void setClassificationMethod(SVMCLASSIFICATIONMETHOD value)
/*  61:    */   {
/*  62:208 */     this.classificationMethod = value;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public MININGFUNCTION getFunctionName()
/*  66:    */   {
/*  67:220 */     return this.functionName;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public void setFunctionName(MININGFUNCTION value)
/*  71:    */   {
/*  72:232 */     this.functionName = value;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public boolean isIsScorable()
/*  76:    */   {
/*  77:244 */     if (this.isScorable == null) {
/*  78:245 */       return true;
/*  79:    */     }
/*  80:247 */     return this.isScorable.booleanValue();
/*  81:    */   }
/*  82:    */   
/*  83:    */   public void setIsScorable(Boolean value)
/*  84:    */   {
/*  85:260 */     this.isScorable = value;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public String getModelName()
/*  89:    */   {
/*  90:272 */     return this.modelName;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public void setModelName(String value)
/*  94:    */   {
/*  95:284 */     this.modelName = value;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public SVMREPRESENTATION getSvmRepresentation()
/*  99:    */   {
/* 100:296 */     if (this.svmRepresentation == null) {
/* 101:297 */       return SVMREPRESENTATION.SUPPORT_VECTORS;
/* 102:    */     }
/* 103:299 */     return this.svmRepresentation;
/* 104:    */   }
/* 105:    */   
/* 106:    */   public void setSvmRepresentation(SVMREPRESENTATION value)
/* 107:    */   {
/* 108:312 */     this.svmRepresentation = value;
/* 109:    */   }
/* 110:    */   
/* 111:    */   public double getThreshold()
/* 112:    */   {
/* 113:324 */     if (this.threshold == null) {
/* 114:325 */       return 0.0D;
/* 115:    */     }
/* 116:327 */     return this.threshold.doubleValue();
/* 117:    */   }
/* 118:    */   
/* 119:    */   public void setThreshold(Double value)
/* 120:    */   {
/* 121:340 */     this.threshold = value;
/* 122:    */   }
/* 123:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.SupportVectorMachineModel
 * JD-Core Version:    0.7.0.1
 */