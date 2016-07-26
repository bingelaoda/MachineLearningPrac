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
/*  14:    */ @XmlRootElement(name="SetPredicate")
/*  15:    */ public class SetPredicate
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="Array", namespace="http://www.dmg.org/PMML-4_1")
/*  20:    */   protected ArrayType array;
/*  21:    */   @XmlAttribute(required=true)
/*  22:    */   protected String field;
/*  23:    */   @XmlAttribute(required=true)
/*  24:    */   protected String id;
/*  25:    */   @XmlAttribute
/*  26:    */   protected String operator;
/*  27:    */   
/*  28:    */   public List<Extension> getExtension()
/*  29:    */   {
/*  30: 88 */     if (this.extension == null) {
/*  31: 89 */       this.extension = new ArrayList();
/*  32:    */     }
/*  33: 91 */     return this.extension;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public ArrayType getArray()
/*  37:    */   {
/*  38:103 */     return this.array;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void setArray(ArrayType value)
/*  42:    */   {
/*  43:115 */     this.array = value;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public String getField()
/*  47:    */   {
/*  48:127 */     return this.field;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void setField(String value)
/*  52:    */   {
/*  53:139 */     this.field = value;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public String getId()
/*  57:    */   {
/*  58:151 */     return this.id;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void setId(String value)
/*  62:    */   {
/*  63:163 */     this.id = value;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public String getOperator()
/*  67:    */   {
/*  68:175 */     if (this.operator == null) {
/*  69:176 */       return "supersetOf";
/*  70:    */     }
/*  71:178 */     return this.operator;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void setOperator(String value)
/*  75:    */   {
/*  76:191 */     this.operator = value;
/*  77:    */   }
/*  78:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.SetPredicate
 * JD-Core Version:    0.7.0.1
 */