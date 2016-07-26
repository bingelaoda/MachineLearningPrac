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
/*  14:    */ @XmlRootElement(name="Value")
/*  15:    */ public class Value
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlAttribute
/*  20:    */   protected String displayValue;
/*  21:    */   @XmlAttribute
/*  22:    */   protected String property;
/*  23:    */   @XmlAttribute(required=true)
/*  24:    */   protected String value;
/*  25:    */   
/*  26:    */   public Value() {}
/*  27:    */   
/*  28:    */   public Value(String value)
/*  29:    */   {
/*  30: 72 */     this.value = value;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public List<Extension> getExtension()
/*  34:    */   {
/*  35: 98 */     if (this.extension == null) {
/*  36: 99 */       this.extension = new ArrayList();
/*  37:    */     }
/*  38:101 */     return this.extension;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public String getDisplayValue()
/*  42:    */   {
/*  43:113 */     return this.displayValue;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void setDisplayValue(String value)
/*  47:    */   {
/*  48:125 */     this.displayValue = value;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public String getProperty()
/*  52:    */   {
/*  53:137 */     if (this.property == null) {
/*  54:138 */       return "valid";
/*  55:    */     }
/*  56:140 */     return this.property;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public void setProperty(String value)
/*  60:    */   {
/*  61:153 */     this.property = value;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public String getValue()
/*  65:    */   {
/*  66:165 */     return this.value;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public void setValue(String value)
/*  70:    */   {
/*  71:177 */     this.value = value;
/*  72:    */   }
/*  73:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Value
 * JD-Core Version:    0.7.0.1
 */