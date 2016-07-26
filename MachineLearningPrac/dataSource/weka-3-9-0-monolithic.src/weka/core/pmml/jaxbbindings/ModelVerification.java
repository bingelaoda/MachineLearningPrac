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
/*  14:    */ @XmlType(name="", propOrder={"extension", "verificationFields", "inlineTable"})
/*  15:    */ @XmlRootElement(name="ModelVerification")
/*  16:    */ public class ModelVerification
/*  17:    */ {
/*  18:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected List<Extension> extension;
/*  20:    */   @XmlElement(name="VerificationFields", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  21:    */   protected VerificationFields verificationFields;
/*  22:    */   @XmlElement(name="InlineTable", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  23:    */   protected InlineTable inlineTable;
/*  24:    */   @XmlAttribute
/*  25:    */   protected BigInteger fieldCount;
/*  26:    */   @XmlAttribute
/*  27:    */   protected BigInteger recordCount;
/*  28:    */   
/*  29:    */   public List<Extension> getExtension()
/*  30:    */   {
/*  31: 90 */     if (this.extension == null) {
/*  32: 91 */       this.extension = new ArrayList();
/*  33:    */     }
/*  34: 93 */     return this.extension;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public VerificationFields getVerificationFields()
/*  38:    */   {
/*  39:105 */     return this.verificationFields;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public void setVerificationFields(VerificationFields value)
/*  43:    */   {
/*  44:117 */     this.verificationFields = value;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public InlineTable getInlineTable()
/*  48:    */   {
/*  49:129 */     return this.inlineTable;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void setInlineTable(InlineTable value)
/*  53:    */   {
/*  54:141 */     this.inlineTable = value;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public BigInteger getFieldCount()
/*  58:    */   {
/*  59:153 */     return this.fieldCount;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void setFieldCount(BigInteger value)
/*  63:    */   {
/*  64:165 */     this.fieldCount = value;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public BigInteger getRecordCount()
/*  68:    */   {
/*  69:177 */     return this.recordCount;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void setRecordCount(BigInteger value)
/*  73:    */   {
/*  74:189 */     this.recordCount = value;
/*  75:    */   }
/*  76:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.ModelVerification
 * JD-Core Version:    0.7.0.1
 */