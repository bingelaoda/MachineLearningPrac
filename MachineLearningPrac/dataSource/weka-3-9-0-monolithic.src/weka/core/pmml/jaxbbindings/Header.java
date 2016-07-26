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
/*  13:    */ @XmlType(name="", propOrder={"extension", "application", "annotation", "timestamp"})
/*  14:    */ @XmlRootElement(name="Header")
/*  15:    */ public class Header
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="Application", namespace="http://www.dmg.org/PMML-4_1")
/*  20:    */   protected Application application;
/*  21:    */   @XmlElement(name="Annotation", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  22:    */   protected List<Annotation> annotation;
/*  23:    */   @XmlElement(name="Timestamp", namespace="http://www.dmg.org/PMML-4_1")
/*  24:    */   protected Timestamp timestamp;
/*  25:    */   @XmlAttribute
/*  26:    */   protected String copyright;
/*  27:    */   @XmlAttribute
/*  28:    */   protected String description;
/*  29:    */   
/*  30:    */   public List<Extension> getExtension()
/*  31:    */   {
/*  32: 93 */     if (this.extension == null) {
/*  33: 94 */       this.extension = new ArrayList();
/*  34:    */     }
/*  35: 96 */     return this.extension;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public Application getApplication()
/*  39:    */   {
/*  40:108 */     return this.application;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void setApplication(Application value)
/*  44:    */   {
/*  45:120 */     this.application = value;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public List<Annotation> getAnnotation()
/*  49:    */   {
/*  50:146 */     if (this.annotation == null) {
/*  51:147 */       this.annotation = new ArrayList();
/*  52:    */     }
/*  53:149 */     return this.annotation;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public Timestamp getTimestamp()
/*  57:    */   {
/*  58:161 */     return this.timestamp;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void setTimestamp(Timestamp value)
/*  62:    */   {
/*  63:173 */     this.timestamp = value;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public String getCopyright()
/*  67:    */   {
/*  68:185 */     return this.copyright;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void setCopyright(String value)
/*  72:    */   {
/*  73:197 */     this.copyright = value;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public String getDescription()
/*  77:    */   {
/*  78:209 */     return this.description;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void setDescription(String value)
/*  82:    */   {
/*  83:221 */     this.description = value;
/*  84:    */   }
/*  85:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Header
 * JD-Core Version:    0.7.0.1
 */