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
/*  13:    */ @XmlType(name="", propOrder={"extension", "fieldValue", "fieldValueCount"})
/*  14:    */ @XmlRootElement(name="FieldValue")
/*  15:    */ public class FieldValue
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="FieldValue", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected List<FieldValue> fieldValue;
/*  21:    */   @XmlElement(name="FieldValueCount", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  22:    */   protected List<FieldValueCount> fieldValueCount;
/*  23:    */   @XmlAttribute(required=true)
/*  24:    */   protected String field;
/*  25:    */   @XmlAttribute(required=true)
/*  26:    */   protected String value;
/*  27:    */   
/*  28:    */   public List<Extension> getExtension()
/*  29:    */   {
/*  30: 91 */     if (this.extension == null) {
/*  31: 92 */       this.extension = new ArrayList();
/*  32:    */     }
/*  33: 94 */     return this.extension;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public List<FieldValue> getFieldValue()
/*  37:    */   {
/*  38:120 */     if (this.fieldValue == null) {
/*  39:121 */       this.fieldValue = new ArrayList();
/*  40:    */     }
/*  41:123 */     return this.fieldValue;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public List<FieldValueCount> getFieldValueCount()
/*  45:    */   {
/*  46:149 */     if (this.fieldValueCount == null) {
/*  47:150 */       this.fieldValueCount = new ArrayList();
/*  48:    */     }
/*  49:152 */     return this.fieldValueCount;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public String getField()
/*  53:    */   {
/*  54:164 */     return this.field;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void setField(String value)
/*  58:    */   {
/*  59:176 */     this.field = value;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public String getValue()
/*  63:    */   {
/*  64:188 */     return this.value;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void setValue(String value)
/*  68:    */   {
/*  69:200 */     this.value = value;
/*  70:    */   }
/*  71:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.FieldValue
 * JD-Core Version:    0.7.0.1
 */