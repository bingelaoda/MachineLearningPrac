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
/*  13:    */ @XmlType(name="", propOrder={"extension", "array"})
/*  14:    */ @XmlRootElement(name="DiscrStats")
/*  15:    */ public class DiscrStats
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="Array", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected List<ArrayType> array;
/*  21:    */   @XmlAttribute
/*  22:    */   protected String modalValue;
/*  23:    */   
/*  24:    */   public List<Extension> getExtension()
/*  25:    */   {
/*  26: 82 */     if (this.extension == null) {
/*  27: 83 */       this.extension = new ArrayList();
/*  28:    */     }
/*  29: 85 */     return this.extension;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public List<ArrayType> getArray()
/*  33:    */   {
/*  34:111 */     if (this.array == null) {
/*  35:112 */       this.array = new ArrayList();
/*  36:    */     }
/*  37:114 */     return this.array;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public String getModalValue()
/*  41:    */   {
/*  42:126 */     return this.modalValue;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void setModalValue(String value)
/*  46:    */   {
/*  47:138 */     this.modalValue = value;
/*  48:    */   }
/*  49:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.DiscrStats
 * JD-Core Version:    0.7.0.1
 */