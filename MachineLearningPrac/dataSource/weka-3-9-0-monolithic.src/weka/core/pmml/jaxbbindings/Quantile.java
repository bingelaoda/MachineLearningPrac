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
/*  15:    */ @XmlRootElement(name="Quantile")
/*  16:    */ public class Quantile
/*  17:    */ {
/*  18:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected List<Extension> extension;
/*  20:    */   @XmlAttribute(required=true)
/*  21:    */   protected BigDecimal quantileLimit;
/*  22:    */   @XmlAttribute(required=true)
/*  23:    */   protected double quantileValue;
/*  24:    */   
/*  25:    */   public List<Extension> getExtension()
/*  26:    */   {
/*  27: 82 */     if (this.extension == null) {
/*  28: 83 */       this.extension = new ArrayList();
/*  29:    */     }
/*  30: 85 */     return this.extension;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public BigDecimal getQuantileLimit()
/*  34:    */   {
/*  35: 97 */     return this.quantileLimit;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void setQuantileLimit(BigDecimal value)
/*  39:    */   {
/*  40:109 */     this.quantileLimit = value;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public double getQuantileValue()
/*  44:    */   {
/*  45:117 */     return this.quantileValue;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void setQuantileValue(double value)
/*  49:    */   {
/*  50:125 */     this.quantileValue = value;
/*  51:    */   }
/*  52:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Quantile
 * JD-Core Version:    0.7.0.1
 */