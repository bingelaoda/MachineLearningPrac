/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   7:    */ import javax.xml.bind.annotation.XmlElement;
/*   8:    */ import javax.xml.bind.annotation.XmlRootElement;
/*   9:    */ import javax.xml.bind.annotation.XmlType;
/*  10:    */ 
/*  11:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  12:    */ @XmlType(name="", propOrder={"extension", "taxonomy", "array"})
/*  13:    */ @XmlRootElement(name="TextDictionary")
/*  14:    */ public class TextDictionary
/*  15:    */ {
/*  16:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  17:    */   protected List<Extension> extension;
/*  18:    */   @XmlElement(name="Taxonomy", namespace="http://www.dmg.org/PMML-4_1")
/*  19:    */   protected Taxonomy taxonomy;
/*  20:    */   @XmlElement(name="Array", namespace="http://www.dmg.org/PMML-4_1")
/*  21:    */   protected ArrayType array;
/*  22:    */   
/*  23:    */   public List<Extension> getExtension()
/*  24:    */   {
/*  25: 82 */     if (this.extension == null) {
/*  26: 83 */       this.extension = new ArrayList();
/*  27:    */     }
/*  28: 85 */     return this.extension;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public Taxonomy getTaxonomy()
/*  32:    */   {
/*  33: 97 */     return this.taxonomy;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public void setTaxonomy(Taxonomy value)
/*  37:    */   {
/*  38:109 */     this.taxonomy = value;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public ArrayType getArray()
/*  42:    */   {
/*  43:121 */     return this.array;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void setArray(ArrayType value)
/*  47:    */   {
/*  48:133 */     this.array = value;
/*  49:    */   }
/*  50:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.TextDictionary
 * JD-Core Version:    0.7.0.1
 */