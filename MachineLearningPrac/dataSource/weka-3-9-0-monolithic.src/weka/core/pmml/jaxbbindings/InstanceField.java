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
/*  14:    */ @XmlRootElement(name="InstanceField")
/*  15:    */ public class InstanceField
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlAttribute
/*  20:    */   protected String column;
/*  21:    */   @XmlAttribute(required=true)
/*  22:    */   protected String field;
/*  23:    */   
/*  24:    */   public List<Extension> getExtension()
/*  25:    */   {
/*  26: 81 */     if (this.extension == null) {
/*  27: 82 */       this.extension = new ArrayList();
/*  28:    */     }
/*  29: 84 */     return this.extension;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public String getColumn()
/*  33:    */   {
/*  34: 96 */     return this.column;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public void setColumn(String value)
/*  38:    */   {
/*  39:108 */     this.column = value;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public String getField()
/*  43:    */   {
/*  44:120 */     return this.field;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void setField(String value)
/*  48:    */   {
/*  49:132 */     this.field = value;
/*  50:    */   }
/*  51:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.InstanceField
 * JD-Core Version:    0.7.0.1
 */