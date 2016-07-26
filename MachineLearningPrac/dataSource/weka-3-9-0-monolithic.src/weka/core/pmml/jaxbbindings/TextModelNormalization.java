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
/*  14:    */ @XmlRootElement(name="TextModelNormalization")
/*  15:    */ public class TextModelNormalization
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlAttribute
/*  20:    */   protected String documentNormalization;
/*  21:    */   @XmlAttribute
/*  22:    */   protected String globalTermWeights;
/*  23:    */   @XmlAttribute
/*  24:    */   protected String localTermWeights;
/*  25:    */   
/*  26:    */   public List<Extension> getExtension()
/*  27:    */   {
/*  28:110 */     if (this.extension == null) {
/*  29:111 */       this.extension = new ArrayList();
/*  30:    */     }
/*  31:113 */     return this.extension;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public String getDocumentNormalization()
/*  35:    */   {
/*  36:125 */     if (this.documentNormalization == null) {
/*  37:126 */       return "none";
/*  38:    */     }
/*  39:128 */     return this.documentNormalization;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public void setDocumentNormalization(String value)
/*  43:    */   {
/*  44:141 */     this.documentNormalization = value;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public String getGlobalTermWeights()
/*  48:    */   {
/*  49:153 */     if (this.globalTermWeights == null) {
/*  50:154 */       return "inverseDocumentFrequency";
/*  51:    */     }
/*  52:156 */     return this.globalTermWeights;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public void setGlobalTermWeights(String value)
/*  56:    */   {
/*  57:169 */     this.globalTermWeights = value;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public String getLocalTermWeights()
/*  61:    */   {
/*  62:181 */     if (this.localTermWeights == null) {
/*  63:182 */       return "termFrequency";
/*  64:    */     }
/*  65:184 */     return this.localTermWeights;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public void setLocalTermWeights(String value)
/*  69:    */   {
/*  70:197 */     this.localTermWeights = value;
/*  71:    */   }
/*  72:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.TextModelNormalization
 * JD-Core Version:    0.7.0.1
 */