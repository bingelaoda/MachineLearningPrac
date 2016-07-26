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
/*  15:    */ @XmlRootElement(name="SequenceRule")
/*  16:    */ public class SequenceRule
/*  17:    */ {
/*  18:    */   @XmlElementRefs({@javax.xml.bind.annotation.XmlElementRef(name="Time", namespace="http://www.dmg.org/PMML-4_1", type=Time.class), @javax.xml.bind.annotation.XmlElementRef(name="Delimiter", namespace="http://www.dmg.org/PMML-4_1", type=Delimiter.class), @javax.xml.bind.annotation.XmlElementRef(name="AntecedentSequence", namespace="http://www.dmg.org/PMML-4_1", type=AntecedentSequence.class), @javax.xml.bind.annotation.XmlElementRef(name="ConsequentSequence", namespace="http://www.dmg.org/PMML-4_1", type=ConsequentSequence.class), @javax.xml.bind.annotation.XmlElementRef(name="Extension", namespace="http://www.dmg.org/PMML-4_1", type=Extension.class)})
/*  19:    */   protected List<Object> content;
/*  20:    */   @XmlAttribute(required=true)
/*  21:    */   protected double confidence;
/*  22:    */   @XmlAttribute(required=true)
/*  23:    */   protected String id;
/*  24:    */   @XmlAttribute
/*  25:    */   protected Double lift;
/*  26:    */   @XmlAttribute(required=true)
/*  27:    */   protected BigInteger numberOfSets;
/*  28:    */   @XmlAttribute(required=true)
/*  29:    */   protected BigInteger occurrence;
/*  30:    */   @XmlAttribute(required=true)
/*  31:    */   protected double support;
/*  32:    */   
/*  33:    */   public List<Object> getContent()
/*  34:    */   {
/*  35:120 */     if (this.content == null) {
/*  36:121 */       this.content = new ArrayList();
/*  37:    */     }
/*  38:123 */     return this.content;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public double getConfidence()
/*  42:    */   {
/*  43:131 */     return this.confidence;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void setConfidence(double value)
/*  47:    */   {
/*  48:139 */     this.confidence = value;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public String getId()
/*  52:    */   {
/*  53:151 */     return this.id;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public void setId(String value)
/*  57:    */   {
/*  58:163 */     this.id = value;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public Double getLift()
/*  62:    */   {
/*  63:175 */     return this.lift;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public void setLift(Double value)
/*  67:    */   {
/*  68:187 */     this.lift = value;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public BigInteger getNumberOfSets()
/*  72:    */   {
/*  73:199 */     return this.numberOfSets;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void setNumberOfSets(BigInteger value)
/*  77:    */   {
/*  78:211 */     this.numberOfSets = value;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public BigInteger getOccurrence()
/*  82:    */   {
/*  83:223 */     return this.occurrence;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public void setOccurrence(BigInteger value)
/*  87:    */   {
/*  88:235 */     this.occurrence = value;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public double getSupport()
/*  92:    */   {
/*  93:243 */     return this.support;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public void setSupport(double value)
/*  97:    */   {
/*  98:251 */     this.support = value;
/*  99:    */   }
/* 100:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.SequenceRule
 * JD-Core Version:    0.7.0.1
 */