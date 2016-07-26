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
/*  13:    */ @XmlType(name="", propOrder={"extension", "multivariateStat"})
/*  14:    */ @XmlRootElement(name="MultivariateStats")
/*  15:    */ public class MultivariateStats
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="MultivariateStat", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected List<MultivariateStat> multivariateStat;
/*  21:    */   @XmlAttribute
/*  22:    */   protected String targetCategory;
/*  23:    */   
/*  24:    */   public List<Extension> getExtension()
/*  25:    */   {
/*  26: 82 */     if (this.extension == null) {
/*  27: 83 */       this.extension = new ArrayList();
/*  28:    */     }
/*  29: 85 */     return this.extension;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public List<MultivariateStat> getMultivariateStat()
/*  33:    */   {
/*  34:111 */     if (this.multivariateStat == null) {
/*  35:112 */       this.multivariateStat = new ArrayList();
/*  36:    */     }
/*  37:114 */     return this.multivariateStat;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public String getTargetCategory()
/*  41:    */   {
/*  42:126 */     return this.targetCategory;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void setTargetCategory(String value)
/*  46:    */   {
/*  47:138 */     this.targetCategory = value;
/*  48:    */   }
/*  49:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.MultivariateStats
 * JD-Core Version:    0.7.0.1
 */