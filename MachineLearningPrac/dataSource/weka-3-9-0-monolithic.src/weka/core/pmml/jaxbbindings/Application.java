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
/*  14:    */ @XmlRootElement(name="Application")
/*  15:    */ public class Application
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlAttribute(required=true)
/*  20:    */   protected String name;
/*  21:    */   @XmlAttribute
/*  22:    */   protected String version;
/*  23:    */   
/*  24:    */   public Application() {}
/*  25:    */   
/*  26:    */   public Application(String name, String version)
/*  27:    */   {
/*  28: 61 */     this.name = name;
/*  29: 62 */     this.version = version;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public List<Extension> getExtension()
/*  33:    */   {
/*  34: 88 */     if (this.extension == null) {
/*  35: 89 */       this.extension = new ArrayList();
/*  36:    */     }
/*  37: 91 */     return this.extension;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public String getName()
/*  41:    */   {
/*  42:103 */     return this.name;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void setName(String value)
/*  46:    */   {
/*  47:115 */     this.name = value;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public String getVersion()
/*  51:    */   {
/*  52:127 */     return this.version;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public void setVersion(String value)
/*  56:    */   {
/*  57:139 */     this.version = value;
/*  58:    */   }
/*  59:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Application
 * JD-Core Version:    0.7.0.1
 */