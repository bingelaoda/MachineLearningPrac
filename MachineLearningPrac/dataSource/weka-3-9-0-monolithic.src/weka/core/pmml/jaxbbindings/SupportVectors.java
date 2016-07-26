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
/*  14:    */ @XmlType(name="", propOrder={"extension", "supportVector"})
/*  15:    */ @XmlRootElement(name="SupportVectors")
/*  16:    */ public class SupportVectors
/*  17:    */ {
/*  18:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected List<Extension> extension;
/*  20:    */   @XmlElement(name="SupportVector", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  21:    */   protected List<SupportVector> supportVector;
/*  22:    */   @XmlAttribute
/*  23:    */   protected BigInteger numberOfAttributes;
/*  24:    */   @XmlAttribute
/*  25:    */   protected BigInteger numberOfSupportVectors;
/*  26:    */   
/*  27:    */   public List<Extension> getExtension()
/*  28:    */   {
/*  29: 86 */     if (this.extension == null) {
/*  30: 87 */       this.extension = new ArrayList();
/*  31:    */     }
/*  32: 89 */     return this.extension;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public List<SupportVector> getSupportVector()
/*  36:    */   {
/*  37:115 */     if (this.supportVector == null) {
/*  38:116 */       this.supportVector = new ArrayList();
/*  39:    */     }
/*  40:118 */     return this.supportVector;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public BigInteger getNumberOfAttributes()
/*  44:    */   {
/*  45:130 */     return this.numberOfAttributes;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void setNumberOfAttributes(BigInteger value)
/*  49:    */   {
/*  50:142 */     this.numberOfAttributes = value;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public BigInteger getNumberOfSupportVectors()
/*  54:    */   {
/*  55:154 */     return this.numberOfSupportVectors;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public void setNumberOfSupportVectors(BigInteger value)
/*  59:    */   {
/*  60:166 */     this.numberOfSupportVectors = value;
/*  61:    */   }
/*  62:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.SupportVectors
 * JD-Core Version:    0.7.0.1
 */