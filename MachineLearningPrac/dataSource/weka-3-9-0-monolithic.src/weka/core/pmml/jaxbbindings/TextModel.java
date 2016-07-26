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
/*  15:    */ @XmlRootElement(name="TextModel")
/*  16:    */ public class TextModel
/*  17:    */ {
/*  18:    */   @XmlElementRefs({@javax.xml.bind.annotation.XmlElementRef(name="ModelExplanation", namespace="http://www.dmg.org/PMML-4_1", type=ModelExplanation.class), @javax.xml.bind.annotation.XmlElementRef(name="ModelStats", namespace="http://www.dmg.org/PMML-4_1", type=ModelStats.class), @javax.xml.bind.annotation.XmlElementRef(name="Output", namespace="http://www.dmg.org/PMML-4_1", type=Output.class), @javax.xml.bind.annotation.XmlElementRef(name="TextCorpus", namespace="http://www.dmg.org/PMML-4_1", type=TextCorpus.class), @javax.xml.bind.annotation.XmlElementRef(name="DocumentTermMatrix", namespace="http://www.dmg.org/PMML-4_1", type=DocumentTermMatrix.class), @javax.xml.bind.annotation.XmlElementRef(name="LocalTransformations", namespace="http://www.dmg.org/PMML-4_1", type=LocalTransformations.class), @javax.xml.bind.annotation.XmlElementRef(name="TextModelSimiliarity", namespace="http://www.dmg.org/PMML-4_1", type=TextModelSimiliarity.class), @javax.xml.bind.annotation.XmlElementRef(name="Targets", namespace="http://www.dmg.org/PMML-4_1", type=Targets.class), @javax.xml.bind.annotation.XmlElementRef(name="TextDictionary", namespace="http://www.dmg.org/PMML-4_1", type=TextDictionary.class), @javax.xml.bind.annotation.XmlElementRef(name="MiningSchema", namespace="http://www.dmg.org/PMML-4_1", type=MiningSchema.class), @javax.xml.bind.annotation.XmlElementRef(name="TextModelNormalization", namespace="http://www.dmg.org/PMML-4_1", type=TextModelNormalization.class), @javax.xml.bind.annotation.XmlElementRef(name="ModelVerification", namespace="http://www.dmg.org/PMML-4_1", type=ModelVerification.class), @javax.xml.bind.annotation.XmlElementRef(name="Extension", namespace="http://www.dmg.org/PMML-4_1", type=Extension.class)})
/*  19:    */   protected List<Object> content;
/*  20:    */   @XmlAttribute
/*  21:    */   protected String algorithmName;
/*  22:    */   @XmlAttribute(required=true)
/*  23:    */   protected MININGFUNCTION functionName;
/*  24:    */   @XmlAttribute
/*  25:    */   protected Boolean isScorable;
/*  26:    */   @XmlAttribute
/*  27:    */   protected String modelName;
/*  28:    */   @XmlAttribute(required=true)
/*  29:    */   protected BigInteger numberOfDocuments;
/*  30:    */   @XmlAttribute(required=true)
/*  31:    */   protected BigInteger numberOfTerms;
/*  32:    */   
/*  33:    */   public List<Object> getContent()
/*  34:    */   {
/*  35:144 */     if (this.content == null) {
/*  36:145 */       this.content = new ArrayList();
/*  37:    */     }
/*  38:147 */     return this.content;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public String getAlgorithmName()
/*  42:    */   {
/*  43:159 */     return this.algorithmName;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void setAlgorithmName(String value)
/*  47:    */   {
/*  48:171 */     this.algorithmName = value;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public MININGFUNCTION getFunctionName()
/*  52:    */   {
/*  53:183 */     return this.functionName;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public void setFunctionName(MININGFUNCTION value)
/*  57:    */   {
/*  58:195 */     this.functionName = value;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public boolean isIsScorable()
/*  62:    */   {
/*  63:207 */     if (this.isScorable == null) {
/*  64:208 */       return true;
/*  65:    */     }
/*  66:210 */     return this.isScorable.booleanValue();
/*  67:    */   }
/*  68:    */   
/*  69:    */   public void setIsScorable(Boolean value)
/*  70:    */   {
/*  71:223 */     this.isScorable = value;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public String getModelName()
/*  75:    */   {
/*  76:235 */     return this.modelName;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public void setModelName(String value)
/*  80:    */   {
/*  81:247 */     this.modelName = value;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public BigInteger getNumberOfDocuments()
/*  85:    */   {
/*  86:259 */     return this.numberOfDocuments;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void setNumberOfDocuments(BigInteger value)
/*  90:    */   {
/*  91:271 */     this.numberOfDocuments = value;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public BigInteger getNumberOfTerms()
/*  95:    */   {
/*  96:283 */     return this.numberOfTerms;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public void setNumberOfTerms(BigInteger value)
/* 100:    */   {
/* 101:295 */     this.numberOfTerms = value;
/* 102:    */   }
/* 103:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.TextModel
 * JD-Core Version:    0.7.0.1
 */