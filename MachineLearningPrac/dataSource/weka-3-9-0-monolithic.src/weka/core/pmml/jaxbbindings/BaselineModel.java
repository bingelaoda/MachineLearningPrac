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
/*  14:    */ @XmlRootElement(name="BaselineModel")
/*  15:    */ public class BaselineModel
/*  16:    */ {
/*  17:    */   @XmlElementRefs({@javax.xml.bind.annotation.XmlElementRef(name="ModelExplanation", namespace="http://www.dmg.org/PMML-4_1", type=ModelExplanation.class), @javax.xml.bind.annotation.XmlElementRef(name="ModelStats", namespace="http://www.dmg.org/PMML-4_1", type=ModelStats.class), @javax.xml.bind.annotation.XmlElementRef(name="Output", namespace="http://www.dmg.org/PMML-4_1", type=Output.class), @javax.xml.bind.annotation.XmlElementRef(name="LocalTransformations", namespace="http://www.dmg.org/PMML-4_1", type=LocalTransformations.class), @javax.xml.bind.annotation.XmlElementRef(name="TestDistributions", namespace="http://www.dmg.org/PMML-4_1", type=TestDistributions.class), @javax.xml.bind.annotation.XmlElementRef(name="Targets", namespace="http://www.dmg.org/PMML-4_1", type=Targets.class), @javax.xml.bind.annotation.XmlElementRef(name="MiningSchema", namespace="http://www.dmg.org/PMML-4_1", type=MiningSchema.class), @javax.xml.bind.annotation.XmlElementRef(name="ModelVerification", namespace="http://www.dmg.org/PMML-4_1", type=ModelVerification.class), @javax.xml.bind.annotation.XmlElementRef(name="Extension", namespace="http://www.dmg.org/PMML-4_1", type=Extension.class)})
/*  18:    */   protected List<Object> content;
/*  19:    */   @XmlAttribute
/*  20:    */   protected String algorithmName;
/*  21:    */   @XmlAttribute(required=true)
/*  22:    */   protected MININGFUNCTION functionName;
/*  23:    */   @XmlAttribute
/*  24:    */   protected Boolean isScorable;
/*  25:    */   @XmlAttribute
/*  26:    */   protected String modelName;
/*  27:    */   
/*  28:    */   public List<Object> getContent()
/*  29:    */   {
/*  30:125 */     if (this.content == null) {
/*  31:126 */       this.content = new ArrayList();
/*  32:    */     }
/*  33:128 */     return this.content;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public String getAlgorithmName()
/*  37:    */   {
/*  38:140 */     return this.algorithmName;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void setAlgorithmName(String value)
/*  42:    */   {
/*  43:152 */     this.algorithmName = value;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public MININGFUNCTION getFunctionName()
/*  47:    */   {
/*  48:164 */     return this.functionName;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void setFunctionName(MININGFUNCTION value)
/*  52:    */   {
/*  53:176 */     this.functionName = value;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public boolean isIsScorable()
/*  57:    */   {
/*  58:188 */     if (this.isScorable == null) {
/*  59:189 */       return true;
/*  60:    */     }
/*  61:191 */     return this.isScorable.booleanValue();
/*  62:    */   }
/*  63:    */   
/*  64:    */   public void setIsScorable(Boolean value)
/*  65:    */   {
/*  66:204 */     this.isScorable = value;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public String getModelName()
/*  70:    */   {
/*  71:216 */     return this.modelName;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void setModelName(String value)
/*  75:    */   {
/*  76:228 */     this.modelName = value;
/*  77:    */   }
/*  78:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.BaselineModel
 * JD-Core Version:    0.7.0.1
 */