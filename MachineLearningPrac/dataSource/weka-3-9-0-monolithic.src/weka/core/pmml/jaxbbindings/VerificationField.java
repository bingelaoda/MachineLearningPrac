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
/*  14:    */ @XmlRootElement(name="VerificationField")
/*  15:    */ public class VerificationField
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlAttribute
/*  20:    */   protected String column;
/*  21:    */   @XmlAttribute(required=true)
/*  22:    */   protected String field;
/*  23:    */   @XmlAttribute
/*  24:    */   protected Double precision;
/*  25:    */   @XmlAttribute
/*  26:    */   protected Double zeroThreshold;
/*  27:    */   
/*  28:    */   public List<Extension> getExtension()
/*  29:    */   {
/*  30: 87 */     if (this.extension == null) {
/*  31: 88 */       this.extension = new ArrayList();
/*  32:    */     }
/*  33: 90 */     return this.extension;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public String getColumn()
/*  37:    */   {
/*  38:102 */     return this.column;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void setColumn(String value)
/*  42:    */   {
/*  43:114 */     this.column = value;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public String getField()
/*  47:    */   {
/*  48:126 */     return this.field;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void setField(String value)
/*  52:    */   {
/*  53:138 */     this.field = value;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public double getPrecision()
/*  57:    */   {
/*  58:150 */     if (this.precision == null) {
/*  59:151 */       return 1.0E-006D;
/*  60:    */     }
/*  61:153 */     return this.precision.doubleValue();
/*  62:    */   }
/*  63:    */   
/*  64:    */   public void setPrecision(Double value)
/*  65:    */   {
/*  66:166 */     this.precision = value;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public double getZeroThreshold()
/*  70:    */   {
/*  71:178 */     if (this.zeroThreshold == null) {
/*  72:179 */       return 1.0E-016D;
/*  73:    */     }
/*  74:181 */     return this.zeroThreshold.doubleValue();
/*  75:    */   }
/*  76:    */   
/*  77:    */   public void setZeroThreshold(Double value)
/*  78:    */   {
/*  79:194 */     this.zeroThreshold = value;
/*  80:    */   }
/*  81:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.VerificationField
 * JD-Core Version:    0.7.0.1
 */