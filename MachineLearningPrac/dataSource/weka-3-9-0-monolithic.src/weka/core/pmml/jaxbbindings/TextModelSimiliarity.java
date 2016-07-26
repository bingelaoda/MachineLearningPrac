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
/*  14:    */ @XmlRootElement(name="TextModelSimiliarity")
/*  15:    */ public class TextModelSimiliarity
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlAttribute
/*  20:    */   protected String similarityType;
/*  21:    */   
/*  22:    */   public List<Extension> getExtension()
/*  23:    */   {
/*  24: 85 */     if (this.extension == null) {
/*  25: 86 */       this.extension = new ArrayList();
/*  26:    */     }
/*  27: 88 */     return this.extension;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public String getSimilarityType()
/*  31:    */   {
/*  32:100 */     return this.similarityType;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public void setSimilarityType(String value)
/*  36:    */   {
/*  37:112 */     this.similarityType = value;
/*  38:    */   }
/*  39:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.TextModelSimiliarity
 * JD-Core Version:    0.7.0.1
 */