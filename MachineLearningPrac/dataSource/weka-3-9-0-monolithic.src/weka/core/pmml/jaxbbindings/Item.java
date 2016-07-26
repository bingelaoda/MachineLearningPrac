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
/*  14:    */ @XmlRootElement(name="Item")
/*  15:    */ public class Item
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlAttribute(required=true)
/*  20:    */   protected String id;
/*  21:    */   @XmlAttribute
/*  22:    */   protected String mappedValue;
/*  23:    */   @XmlAttribute(required=true)
/*  24:    */   protected String value;
/*  25:    */   @XmlAttribute
/*  26:    */   protected Double weight;
/*  27:    */   
/*  28:    */   public List<Extension> getExtension()
/*  29:    */   {
/*  30: 87 */     if (this.extension == null) {
/*  31: 88 */       this.extension = new ArrayList();
/*  32:    */     }
/*  33: 90 */     return this.extension;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public String getId()
/*  37:    */   {
/*  38:102 */     return this.id;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void setId(String value)
/*  42:    */   {
/*  43:114 */     this.id = value;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public String getMappedValue()
/*  47:    */   {
/*  48:126 */     return this.mappedValue;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void setMappedValue(String value)
/*  52:    */   {
/*  53:138 */     this.mappedValue = value;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public String getValue()
/*  57:    */   {
/*  58:150 */     return this.value;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void setValue(String value)
/*  62:    */   {
/*  63:162 */     this.value = value;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public Double getWeight()
/*  67:    */   {
/*  68:174 */     return this.weight;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void setWeight(Double value)
/*  72:    */   {
/*  73:186 */     this.weight = value;
/*  74:    */   }
/*  75:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Item
 * JD-Core Version:    0.7.0.1
 */