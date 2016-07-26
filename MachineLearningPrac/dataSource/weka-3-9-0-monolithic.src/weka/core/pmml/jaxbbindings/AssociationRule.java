/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.math.BigDecimal;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.List;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   7:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   8:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   9:    */ import javax.xml.bind.annotation.XmlElement;
/*  10:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  11:    */ import javax.xml.bind.annotation.XmlType;
/*  12:    */ 
/*  13:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  14:    */ @XmlType(name="", propOrder={"extension"})
/*  15:    */ @XmlRootElement(name="AssociationRule")
/*  16:    */ public class AssociationRule
/*  17:    */ {
/*  18:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected List<Extension> extension;
/*  20:    */   @XmlAttribute
/*  21:    */   protected BigDecimal affinity;
/*  22:    */   @XmlAttribute(required=true)
/*  23:    */   protected String antecedent;
/*  24:    */   @XmlAttribute(required=true)
/*  25:    */   protected BigDecimal confidence;
/*  26:    */   @XmlAttribute(required=true)
/*  27:    */   protected String consequent;
/*  28:    */   @XmlAttribute
/*  29:    */   protected String id;
/*  30:    */   @XmlAttribute
/*  31:    */   protected Float leverage;
/*  32:    */   @XmlAttribute
/*  33:    */   protected Float lift;
/*  34:    */   @XmlAttribute(required=true)
/*  35:    */   protected BigDecimal support;
/*  36:    */   
/*  37:    */   public List<Extension> getExtension()
/*  38:    */   {
/*  39:100 */     if (this.extension == null) {
/*  40:101 */       this.extension = new ArrayList();
/*  41:    */     }
/*  42:103 */     return this.extension;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public BigDecimal getAffinity()
/*  46:    */   {
/*  47:115 */     return this.affinity;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void setAffinity(BigDecimal value)
/*  51:    */   {
/*  52:127 */     this.affinity = value;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public String getAntecedent()
/*  56:    */   {
/*  57:139 */     return this.antecedent;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public void setAntecedent(String value)
/*  61:    */   {
/*  62:151 */     this.antecedent = value;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public BigDecimal getConfidence()
/*  66:    */   {
/*  67:163 */     return this.confidence;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public void setConfidence(BigDecimal value)
/*  71:    */   {
/*  72:175 */     this.confidence = value;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public String getConsequent()
/*  76:    */   {
/*  77:187 */     return this.consequent;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public void setConsequent(String value)
/*  81:    */   {
/*  82:199 */     this.consequent = value;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public String getId()
/*  86:    */   {
/*  87:211 */     return this.id;
/*  88:    */   }
/*  89:    */   
/*  90:    */   public void setId(String value)
/*  91:    */   {
/*  92:223 */     this.id = value;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public Float getLeverage()
/*  96:    */   {
/*  97:235 */     return this.leverage;
/*  98:    */   }
/*  99:    */   
/* 100:    */   public void setLeverage(Float value)
/* 101:    */   {
/* 102:247 */     this.leverage = value;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public Float getLift()
/* 106:    */   {
/* 107:259 */     return this.lift;
/* 108:    */   }
/* 109:    */   
/* 110:    */   public void setLift(Float value)
/* 111:    */   {
/* 112:271 */     this.lift = value;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public BigDecimal getSupport()
/* 116:    */   {
/* 117:283 */     return this.support;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public void setSupport(BigDecimal value)
/* 121:    */   {
/* 122:295 */     this.support = value;
/* 123:    */   }
/* 124:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.AssociationRule
 * JD-Core Version:    0.7.0.1
 */