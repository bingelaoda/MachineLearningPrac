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
/*  14:    */ @XmlRootElement(name="NaiveBayesModel")
/*  15:    */ public class NaiveBayesModel
/*  16:    */ {
/*  17:    */   @XmlElementRefs({@javax.xml.bind.annotation.XmlElementRef(name="ModelExplanation", namespace="http://www.dmg.org/PMML-4_1", type=ModelExplanation.class), @javax.xml.bind.annotation.XmlElementRef(name="ModelStats", namespace="http://www.dmg.org/PMML-4_1", type=ModelStats.class), @javax.xml.bind.annotation.XmlElementRef(name="Output", namespace="http://www.dmg.org/PMML-4_1", type=Output.class), @javax.xml.bind.annotation.XmlElementRef(name="BayesInputs", namespace="http://www.dmg.org/PMML-4_1", type=BayesInputs.class), @javax.xml.bind.annotation.XmlElementRef(name="BayesOutput", namespace="http://www.dmg.org/PMML-4_1", type=BayesOutput.class), @javax.xml.bind.annotation.XmlElementRef(name="LocalTransformations", namespace="http://www.dmg.org/PMML-4_1", type=LocalTransformations.class), @javax.xml.bind.annotation.XmlElementRef(name="Targets", namespace="http://www.dmg.org/PMML-4_1", type=Targets.class), @javax.xml.bind.annotation.XmlElementRef(name="MiningSchema", namespace="http://www.dmg.org/PMML-4_1", type=MiningSchema.class), @javax.xml.bind.annotation.XmlElementRef(name="ModelVerification", namespace="http://www.dmg.org/PMML-4_1", type=ModelVerification.class), @javax.xml.bind.annotation.XmlElementRef(name="Extension", namespace="http://www.dmg.org/PMML-4_1", type=Extension.class)})
/*  18:    */   protected List<Object> content;
/*  19:    */   @XmlAttribute
/*  20:    */   protected String algorithmName;
/*  21:    */   @XmlAttribute(required=true)
/*  22:    */   protected MININGFUNCTION functionName;
/*  23:    */   @XmlAttribute
/*  24:    */   protected Boolean isScorable;
/*  25:    */   @XmlAttribute
/*  26:    */   protected String modelName;
/*  27:    */   @XmlAttribute(required=true)
/*  28:    */   protected double threshold;
/*  29:    */   
/*  30:    */   public List<Object> getContent()
/*  31:    */   {
/*  32:131 */     if (this.content == null) {
/*  33:132 */       this.content = new ArrayList();
/*  34:    */     }
/*  35:134 */     return this.content;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public String getAlgorithmName()
/*  39:    */   {
/*  40:146 */     return this.algorithmName;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void setAlgorithmName(String value)
/*  44:    */   {
/*  45:158 */     this.algorithmName = value;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public MININGFUNCTION getFunctionName()
/*  49:    */   {
/*  50:170 */     return this.functionName;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void setFunctionName(MININGFUNCTION value)
/*  54:    */   {
/*  55:182 */     this.functionName = value;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public boolean isIsScorable()
/*  59:    */   {
/*  60:194 */     if (this.isScorable == null) {
/*  61:195 */       return true;
/*  62:    */     }
/*  63:197 */     return this.isScorable.booleanValue();
/*  64:    */   }
/*  65:    */   
/*  66:    */   public void setIsScorable(Boolean value)
/*  67:    */   {
/*  68:210 */     this.isScorable = value;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public String getModelName()
/*  72:    */   {
/*  73:222 */     return this.modelName;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void setModelName(String value)
/*  77:    */   {
/*  78:234 */     this.modelName = value;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public double getThreshold()
/*  82:    */   {
/*  83:242 */     return this.threshold;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public void setThreshold(double value)
/*  87:    */   {
/*  88:250 */     this.threshold = value;
/*  89:    */   }
/*  90:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.NaiveBayesModel
 * JD-Core Version:    0.7.0.1
 */