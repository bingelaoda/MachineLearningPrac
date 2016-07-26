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
/*  14:    */ @XmlType(name="", propOrder={"extension", "fieldRef"})
/*  15:    */ @XmlRootElement(name="VectorFields")
/*  16:    */ public class VectorFields
/*  17:    */ {
/*  18:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected List<Extension> extension;
/*  20:    */   @XmlElement(name="FieldRef", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  21:    */   protected List<FieldRef> fieldRef;
/*  22:    */   @XmlAttribute
/*  23:    */   protected BigInteger numberOfFields;
/*  24:    */   
/*  25:    */   public List<Extension> getExtension()
/*  26:    */   {
/*  27: 83 */     if (this.extension == null) {
/*  28: 84 */       this.extension = new ArrayList();
/*  29:    */     }
/*  30: 86 */     return this.extension;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public List<FieldRef> getFieldRef()
/*  34:    */   {
/*  35:112 */     if (this.fieldRef == null) {
/*  36:113 */       this.fieldRef = new ArrayList();
/*  37:    */     }
/*  38:115 */     return this.fieldRef;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public BigInteger getNumberOfFields()
/*  42:    */   {
/*  43:127 */     return this.numberOfFields;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void setNumberOfFields(BigInteger value)
/*  47:    */   {
/*  48:139 */     this.numberOfFields = value;
/*  49:    */   }
/*  50:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.VectorFields
 * JD-Core Version:    0.7.0.1
 */