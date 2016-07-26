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
/*  14:    */ @XmlRootElement(name="SimpleSetPredicate")
/*  15:    */ public class SimpleSetPredicate
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="Array", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected ArrayType array;
/*  21:    */   @XmlAttribute(required=true)
/*  22:    */   protected String booleanOperator;
/*  23:    */   @XmlAttribute(required=true)
/*  24:    */   protected String field;
/*  25:    */   
/*  26:    */   public List<Extension> getExtension()
/*  27:    */   {
/*  28: 92 */     if (this.extension == null) {
/*  29: 93 */       this.extension = new ArrayList();
/*  30:    */     }
/*  31: 95 */     return this.extension;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public ArrayType getArray()
/*  35:    */   {
/*  36:107 */     return this.array;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void setArray(ArrayType value)
/*  40:    */   {
/*  41:119 */     this.array = value;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public String getBooleanOperator()
/*  45:    */   {
/*  46:131 */     return this.booleanOperator;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void setBooleanOperator(String value)
/*  50:    */   {
/*  51:143 */     this.booleanOperator = value;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public String getField()
/*  55:    */   {
/*  56:155 */     return this.field;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public void setField(String value)
/*  60:    */   {
/*  61:167 */     this.field = value;
/*  62:    */   }
/*  63:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.SimpleSetPredicate
 * JD-Core Version:    0.7.0.1
 */