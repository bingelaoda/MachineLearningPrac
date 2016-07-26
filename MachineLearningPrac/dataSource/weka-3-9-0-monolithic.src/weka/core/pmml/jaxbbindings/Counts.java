/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.math.BigInteger;
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
/*  15:    */ @XmlRootElement(name="Counts")
/*  16:    */ public class Counts
/*  17:    */ {
/*  18:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected List<Extension> extension;
/*  20:    */   @XmlAttribute
/*  21:    */   protected BigInteger cardinality;
/*  22:    */   @XmlAttribute
/*  23:    */   protected Double invalidFreq;
/*  24:    */   @XmlAttribute
/*  25:    */   protected Double missingFreq;
/*  26:    */   @XmlAttribute(required=true)
/*  27:    */   protected double totalFreq;
/*  28:    */   
/*  29:    */   public List<Extension> getExtension()
/*  30:    */   {
/*  31: 88 */     if (this.extension == null) {
/*  32: 89 */       this.extension = new ArrayList();
/*  33:    */     }
/*  34: 91 */     return this.extension;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public BigInteger getCardinality()
/*  38:    */   {
/*  39:103 */     return this.cardinality;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public void setCardinality(BigInteger value)
/*  43:    */   {
/*  44:115 */     this.cardinality = value;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public Double getInvalidFreq()
/*  48:    */   {
/*  49:127 */     return this.invalidFreq;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void setInvalidFreq(Double value)
/*  53:    */   {
/*  54:139 */     this.invalidFreq = value;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public Double getMissingFreq()
/*  58:    */   {
/*  59:151 */     return this.missingFreq;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void setMissingFreq(Double value)
/*  63:    */   {
/*  64:163 */     this.missingFreq = value;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public double getTotalFreq()
/*  68:    */   {
/*  69:171 */     return this.totalFreq;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void setTotalFreq(double value)
/*  73:    */   {
/*  74:179 */     this.totalFreq = value;
/*  75:    */   }
/*  76:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Counts
 * JD-Core Version:    0.7.0.1
 */