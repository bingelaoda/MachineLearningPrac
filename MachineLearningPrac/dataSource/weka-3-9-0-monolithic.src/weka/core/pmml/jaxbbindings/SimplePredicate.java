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
/*  14:    */ @XmlRootElement(name="SimplePredicate")
/*  15:    */ public class SimplePredicate
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlAttribute(required=true)
/*  20:    */   protected String field;
/*  21:    */   @XmlAttribute(required=true)
/*  22:    */   protected String operator;
/*  23:    */   @XmlAttribute
/*  24:    */   protected String value;
/*  25:    */   
/*  26:    */   public List<Extension> getExtension()
/*  27:    */   {
/*  28: 97 */     if (this.extension == null) {
/*  29: 98 */       this.extension = new ArrayList();
/*  30:    */     }
/*  31:100 */     return this.extension;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public String getField()
/*  35:    */   {
/*  36:112 */     return this.field;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void setField(String value)
/*  40:    */   {
/*  41:124 */     this.field = value;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public String getOperator()
/*  45:    */   {
/*  46:136 */     return this.operator;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void setOperator(String value)
/*  50:    */   {
/*  51:148 */     this.operator = value;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public String getValue()
/*  55:    */   {
/*  56:160 */     return this.value;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public void setValue(String value)
/*  60:    */   {
/*  61:172 */     this.value = value;
/*  62:    */   }
/*  63:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.SimplePredicate
 * JD-Core Version:    0.7.0.1
 */