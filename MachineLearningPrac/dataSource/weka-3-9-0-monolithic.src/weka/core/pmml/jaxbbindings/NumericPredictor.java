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
/*  15:    */ @XmlRootElement(name="NumericPredictor")
/*  16:    */ public class NumericPredictor
/*  17:    */ {
/*  18:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected List<Extension> extension;
/*  20:    */   @XmlAttribute(required=true)
/*  21:    */   protected double coefficient;
/*  22:    */   @XmlAttribute
/*  23:    */   protected BigInteger exponent;
/*  24:    */   @XmlAttribute(required=true)
/*  25:    */   protected String name;
/*  26:    */   
/*  27:    */   public NumericPredictor() {}
/*  28:    */   
/*  29:    */   public NumericPredictor(String name, BigInteger exponent, double coefficient)
/*  30:    */   {
/*  31: 65 */     this.name = name;
/*  32: 66 */     this.exponent = exponent;
/*  33: 67 */     this.coefficient = coefficient;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public List<Extension> getExtension()
/*  37:    */   {
/*  38: 93 */     if (this.extension == null) {
/*  39: 94 */       this.extension = new ArrayList();
/*  40:    */     }
/*  41: 96 */     return this.extension;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public double getCoefficient()
/*  45:    */   {
/*  46:104 */     return this.coefficient;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void setCoefficient(double value)
/*  50:    */   {
/*  51:112 */     this.coefficient = value;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public BigInteger getExponent()
/*  55:    */   {
/*  56:124 */     if (this.exponent == null) {
/*  57:125 */       return new BigInteger("1");
/*  58:    */     }
/*  59:127 */     return this.exponent;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void setExponent(BigInteger value)
/*  63:    */   {
/*  64:140 */     this.exponent = value;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public String getName()
/*  68:    */   {
/*  69:152 */     return this.name;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void setName(String value)
/*  73:    */   {
/*  74:164 */     this.name = value;
/*  75:    */   }
/*  76:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.NumericPredictor
 * JD-Core Version:    0.7.0.1
 */