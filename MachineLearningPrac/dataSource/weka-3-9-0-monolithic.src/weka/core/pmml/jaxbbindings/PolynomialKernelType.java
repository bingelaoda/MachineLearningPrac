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
/*  14:    */ @XmlRootElement(name="PolynomialKernelType")
/*  15:    */ public class PolynomialKernelType
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlAttribute
/*  20:    */   protected Double coef0;
/*  21:    */   @XmlAttribute
/*  22:    */   protected Double degree;
/*  23:    */   @XmlAttribute
/*  24:    */   protected String description;
/*  25:    */   @XmlAttribute
/*  26:    */   protected Double gamma;
/*  27:    */   
/*  28:    */   public List<Extension> getExtension()
/*  29:    */   {
/*  30: 87 */     if (this.extension == null) {
/*  31: 88 */       this.extension = new ArrayList();
/*  32:    */     }
/*  33: 90 */     return this.extension;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public double getCoef0()
/*  37:    */   {
/*  38:102 */     if (this.coef0 == null) {
/*  39:103 */       return 1.0D;
/*  40:    */     }
/*  41:105 */     return this.coef0.doubleValue();
/*  42:    */   }
/*  43:    */   
/*  44:    */   public void setCoef0(Double value)
/*  45:    */   {
/*  46:118 */     this.coef0 = value;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public double getDegree()
/*  50:    */   {
/*  51:130 */     if (this.degree == null) {
/*  52:131 */       return 1.0D;
/*  53:    */     }
/*  54:133 */     return this.degree.doubleValue();
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void setDegree(Double value)
/*  58:    */   {
/*  59:146 */     this.degree = value;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public String getDescription()
/*  63:    */   {
/*  64:158 */     return this.description;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void setDescription(String value)
/*  68:    */   {
/*  69:170 */     this.description = value;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public double getGamma()
/*  73:    */   {
/*  74:182 */     if (this.gamma == null) {
/*  75:183 */       return 1.0D;
/*  76:    */     }
/*  77:185 */     return this.gamma.doubleValue();
/*  78:    */   }
/*  79:    */   
/*  80:    */   public void setGamma(Double value)
/*  81:    */   {
/*  82:198 */     this.gamma = value;
/*  83:    */   }
/*  84:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.PolynomialKernelType
 * JD-Core Version:    0.7.0.1
 */