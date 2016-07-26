/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   7:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   8:    */ import javax.xml.bind.annotation.XmlElement;
/*   9:    */ import javax.xml.bind.annotation.XmlType;
/*  10:    */ 
/*  11:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  12:    */ @XmlType(name="COUNT-TABLE-TYPE", propOrder={"extension", "fieldValue", "fieldValueCount"})
/*  13:    */ public class COUNTTABLETYPE
/*  14:    */ {
/*  15:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  16:    */   protected List<Extension> extension;
/*  17:    */   @XmlElement(name="FieldValue", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<FieldValue> fieldValue;
/*  19:    */   @XmlElement(name="FieldValueCount", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected List<FieldValueCount> fieldValueCount;
/*  21:    */   @XmlAttribute
/*  22:    */   protected Double sample;
/*  23:    */   
/*  24:    */   public List<Extension> getExtension()
/*  25:    */   {
/*  26: 84 */     if (this.extension == null) {
/*  27: 85 */       this.extension = new ArrayList();
/*  28:    */     }
/*  29: 87 */     return this.extension;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public List<FieldValue> getFieldValue()
/*  33:    */   {
/*  34:113 */     if (this.fieldValue == null) {
/*  35:114 */       this.fieldValue = new ArrayList();
/*  36:    */     }
/*  37:116 */     return this.fieldValue;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public List<FieldValueCount> getFieldValueCount()
/*  41:    */   {
/*  42:142 */     if (this.fieldValueCount == null) {
/*  43:143 */       this.fieldValueCount = new ArrayList();
/*  44:    */     }
/*  45:145 */     return this.fieldValueCount;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public Double getSample()
/*  49:    */   {
/*  50:157 */     return this.sample;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void setSample(Double value)
/*  54:    */   {
/*  55:169 */     this.sample = value;
/*  56:    */   }
/*  57:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.COUNTTABLETYPE
 * JD-Core Version:    0.7.0.1
 */