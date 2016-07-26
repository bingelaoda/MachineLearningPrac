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
/*  14:    */ @XmlRootElement(name="SigmoidKernelType")
/*  15:    */ public class SigmoidKernelType
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlAttribute
/*  20:    */   protected Double coef0;
/*  21:    */   @XmlAttribute
/*  22:    */   protected String description;
/*  23:    */   @XmlAttribute
/*  24:    */   protected Double gamma;
/*  25:    */   
/*  26:    */   public List<Extension> getExtension()
/*  27:    */   {
/*  28: 84 */     if (this.extension == null) {
/*  29: 85 */       this.extension = new ArrayList();
/*  30:    */     }
/*  31: 87 */     return this.extension;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public double getCoef0()
/*  35:    */   {
/*  36: 99 */     if (this.coef0 == null) {
/*  37:100 */       return 1.0D;
/*  38:    */     }
/*  39:102 */     return this.coef0.doubleValue();
/*  40:    */   }
/*  41:    */   
/*  42:    */   public void setCoef0(Double value)
/*  43:    */   {
/*  44:115 */     this.coef0 = value;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public String getDescription()
/*  48:    */   {
/*  49:127 */     return this.description;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void setDescription(String value)
/*  53:    */   {
/*  54:139 */     this.description = value;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public double getGamma()
/*  58:    */   {
/*  59:151 */     if (this.gamma == null) {
/*  60:152 */       return 1.0D;
/*  61:    */     }
/*  62:154 */     return this.gamma.doubleValue();
/*  63:    */   }
/*  64:    */   
/*  65:    */   public void setGamma(Double value)
/*  66:    */   {
/*  67:167 */     this.gamma = value;
/*  68:    */   }
/*  69:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.SigmoidKernelType
 * JD-Core Version:    0.7.0.1
 */