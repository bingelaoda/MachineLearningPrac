/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   7:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   8:    */ import javax.xml.bind.annotation.XmlElement;
/*   9:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  10:    */ import javax.xml.bind.annotation.XmlType;
/*  11:    */ 
/*  12:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  13:    */ @XmlType(name="", propOrder={"extension"})
/*  14:    */ @XmlRootElement(name="AnyDistribution")
/*  15:    */ public class AnyDistribution
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlAttribute(required=true)
/*  20:    */   protected double mean;
/*  21:    */   @XmlAttribute(required=true)
/*  22:    */   protected double variance;
/*  23:    */   
/*  24:    */   public List<Extension> getExtension()
/*  25:    */   {
/*  26: 81 */     if (this.extension == null) {
/*  27: 82 */       this.extension = new ArrayList();
/*  28:    */     }
/*  29: 84 */     return this.extension;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public double getMean()
/*  33:    */   {
/*  34: 92 */     return this.mean;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public void setMean(double value)
/*  38:    */   {
/*  39:100 */     this.mean = value;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public double getVariance()
/*  43:    */   {
/*  44:108 */     return this.variance;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void setVariance(double value)
/*  48:    */   {
/*  49:116 */     this.variance = value;
/*  50:    */   }
/*  51:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.AnyDistribution
 * JD-Core Version:    0.7.0.1
 */