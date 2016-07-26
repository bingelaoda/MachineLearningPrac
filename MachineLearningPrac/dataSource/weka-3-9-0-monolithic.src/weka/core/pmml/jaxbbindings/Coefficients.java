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
/*  14:    */ @XmlType(name="", propOrder={"extension", "coefficient"})
/*  15:    */ @XmlRootElement(name="Coefficients")
/*  16:    */ public class Coefficients
/*  17:    */ {
/*  18:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected List<Extension> extension;
/*  20:    */   @XmlElement(name="Coefficient", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  21:    */   protected List<Coefficient> coefficient;
/*  22:    */   @XmlAttribute
/*  23:    */   protected Double absoluteValue;
/*  24:    */   @XmlAttribute
/*  25:    */   protected BigInteger numberOfCoefficients;
/*  26:    */   
/*  27:    */   public List<Extension> getExtension()
/*  28:    */   {
/*  29: 86 */     if (this.extension == null) {
/*  30: 87 */       this.extension = new ArrayList();
/*  31:    */     }
/*  32: 89 */     return this.extension;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public List<Coefficient> getCoefficient()
/*  36:    */   {
/*  37:115 */     if (this.coefficient == null) {
/*  38:116 */       this.coefficient = new ArrayList();
/*  39:    */     }
/*  40:118 */     return this.coefficient;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public double getAbsoluteValue()
/*  44:    */   {
/*  45:130 */     if (this.absoluteValue == null) {
/*  46:131 */       return 0.0D;
/*  47:    */     }
/*  48:133 */     return this.absoluteValue.doubleValue();
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void setAbsoluteValue(Double value)
/*  52:    */   {
/*  53:146 */     this.absoluteValue = value;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public BigInteger getNumberOfCoefficients()
/*  57:    */   {
/*  58:158 */     return this.numberOfCoefficients;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void setNumberOfCoefficients(BigInteger value)
/*  62:    */   {
/*  63:170 */     this.numberOfCoefficients = value;
/*  64:    */   }
/*  65:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Coefficients
 * JD-Core Version:    0.7.0.1
 */