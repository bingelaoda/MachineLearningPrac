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
/*  15:    */ @XmlRootElement(name="ScoreDistribution")
/*  16:    */ public class ScoreDistribution
/*  17:    */ {
/*  18:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected List<Extension> extension;
/*  20:    */   @XmlAttribute
/*  21:    */   protected BigDecimal confidence;
/*  22:    */   @XmlAttribute
/*  23:    */   protected BigDecimal probability;
/*  24:    */   @XmlAttribute(required=true)
/*  25:    */   protected double recordCount;
/*  26:    */   @XmlAttribute(required=true)
/*  27:    */   protected String value;
/*  28:    */   
/*  29:    */   public List<Extension> getExtension()
/*  30:    */   {
/*  31: 88 */     if (this.extension == null) {
/*  32: 89 */       this.extension = new ArrayList();
/*  33:    */     }
/*  34: 91 */     return this.extension;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public BigDecimal getConfidence()
/*  38:    */   {
/*  39:103 */     return this.confidence;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public void setConfidence(BigDecimal value)
/*  43:    */   {
/*  44:115 */     this.confidence = value;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public BigDecimal getProbability()
/*  48:    */   {
/*  49:127 */     return this.probability;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void setProbability(BigDecimal value)
/*  53:    */   {
/*  54:139 */     this.probability = value;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public double getRecordCount()
/*  58:    */   {
/*  59:147 */     return this.recordCount;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void setRecordCount(double value)
/*  63:    */   {
/*  64:155 */     this.recordCount = value;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public String getValue()
/*  68:    */   {
/*  69:167 */     return this.value;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void setValue(String value)
/*  73:    */   {
/*  74:179 */     this.value = value;
/*  75:    */   }
/*  76:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.ScoreDistribution
 * JD-Core Version:    0.7.0.1
 */