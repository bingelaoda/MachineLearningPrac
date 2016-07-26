/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.math.BigInteger;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.List;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   7:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   8:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   9:    */ import javax.xml.bind.annotation.XmlElement;
/*  10:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  11:    */ import javax.xml.bind.annotation.XmlType;
/*  12:    */ 
/*  13:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  14:    */ @XmlType(name="", propOrder={"extension"})
/*  15:    */ @XmlRootElement(name="TextDocument")
/*  16:    */ public class TextDocument
/*  17:    */ {
/*  18:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected List<Extension> extension;
/*  20:    */   @XmlAttribute
/*  21:    */   protected String file;
/*  22:    */   @XmlAttribute(required=true)
/*  23:    */   protected String id;
/*  24:    */   @XmlAttribute
/*  25:    */   protected BigInteger length;
/*  26:    */   @XmlAttribute
/*  27:    */   protected String name;
/*  28:    */   
/*  29:    */   public List<Extension> getExtension()
/*  30:    */   {
/*  31: 88 */     if (this.extension == null) {
/*  32: 89 */       this.extension = new ArrayList();
/*  33:    */     }
/*  34: 91 */     return this.extension;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public String getFile()
/*  38:    */   {
/*  39:103 */     return this.file;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public void setFile(String value)
/*  43:    */   {
/*  44:115 */     this.file = value;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public String getId()
/*  48:    */   {
/*  49:127 */     return this.id;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void setId(String value)
/*  53:    */   {
/*  54:139 */     this.id = value;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public BigInteger getLength()
/*  58:    */   {
/*  59:151 */     return this.length;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void setLength(BigInteger value)
/*  63:    */   {
/*  64:163 */     this.length = value;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public String getName()
/*  68:    */   {
/*  69:175 */     return this.name;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void setName(String value)
/*  73:    */   {
/*  74:187 */     this.name = value;
/*  75:    */   }
/*  76:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.TextDocument
 * JD-Core Version:    0.7.0.1
 */