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
/*  14:    */ @XmlRootElement(name="RadialBasisKernelType")
/*  15:    */ public class RadialBasisKernelType
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlAttribute
/*  20:    */   protected String description;
/*  21:    */   @XmlAttribute
/*  22:    */   protected Double gamma;
/*  23:    */   
/*  24:    */   public List<Extension> getExtension()
/*  25:    */   {
/*  26: 81 */     if (this.extension == null) {
/*  27: 82 */       this.extension = new ArrayList();
/*  28:    */     }
/*  29: 84 */     return this.extension;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public String getDescription()
/*  33:    */   {
/*  34: 96 */     return this.description;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public void setDescription(String value)
/*  38:    */   {
/*  39:108 */     this.description = value;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public double getGamma()
/*  43:    */   {
/*  44:120 */     if (this.gamma == null) {
/*  45:121 */       return 1.0D;
/*  46:    */     }
/*  47:123 */     return this.gamma.doubleValue();
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void setGamma(Double value)
/*  51:    */   {
/*  52:136 */     this.gamma = value;
/*  53:    */   }
/*  54:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.RadialBasisKernelType
 * JD-Core Version:    0.7.0.1
 */