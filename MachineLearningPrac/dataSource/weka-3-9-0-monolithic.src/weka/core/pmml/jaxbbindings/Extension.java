/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   7:    */ import javax.xml.bind.annotation.XmlAnyElement;
/*   8:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   9:    */ import javax.xml.bind.annotation.XmlMixed;
/*  10:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  11:    */ import javax.xml.bind.annotation.XmlType;
/*  12:    */ 
/*  13:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  14:    */ @XmlType(name="", propOrder={"content"})
/*  15:    */ @XmlRootElement(name="Extension")
/*  16:    */ public class Extension
/*  17:    */ {
/*  18:    */   @XmlMixed
/*  19:    */   @XmlAnyElement
/*  20:    */   protected List<Object> content;
/*  21:    */   @XmlAttribute
/*  22:    */   protected String extender;
/*  23:    */   @XmlAttribute
/*  24:    */   protected String name;
/*  25:    */   @XmlAttribute
/*  26:    */   protected String value;
/*  27:    */   
/*  28:    */   public List<Object> getContent()
/*  29:    */   {
/*  30: 88 */     if (this.content == null) {
/*  31: 89 */       this.content = new ArrayList();
/*  32:    */     }
/*  33: 91 */     return this.content;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public String getExtender()
/*  37:    */   {
/*  38:103 */     return this.extender;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void setExtender(String value)
/*  42:    */   {
/*  43:115 */     this.extender = value;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public String getName()
/*  47:    */   {
/*  48:127 */     return this.name;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void setName(String value)
/*  52:    */   {
/*  53:139 */     this.name = value;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public String getValue()
/*  57:    */   {
/*  58:151 */     return this.value;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void setValue(String value)
/*  62:    */   {
/*  63:163 */     this.value = value;
/*  64:    */   }
/*  65:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Extension
 * JD-Core Version:    0.7.0.1
 */