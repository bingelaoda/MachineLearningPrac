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
/*  13:    */ @XmlType(name="", propOrder={"extension", "derivedField", "pairCounts"})
/*  14:    */ @XmlRootElement(name="BayesInput")
/*  15:    */ public class BayesInput
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="DerivedField", namespace="http://www.dmg.org/PMML-4_1")
/*  20:    */   protected DerivedField derivedField;
/*  21:    */   @XmlElement(name="PairCounts", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  22:    */   protected List<PairCounts> pairCounts;
/*  23:    */   @XmlAttribute(required=true)
/*  24:    */   protected String fieldName;
/*  25:    */   
/*  26:    */   public List<Extension> getExtension()
/*  27:    */   {
/*  28: 86 */     if (this.extension == null) {
/*  29: 87 */       this.extension = new ArrayList();
/*  30:    */     }
/*  31: 89 */     return this.extension;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public DerivedField getDerivedField()
/*  35:    */   {
/*  36:101 */     return this.derivedField;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void setDerivedField(DerivedField value)
/*  40:    */   {
/*  41:113 */     this.derivedField = value;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public List<PairCounts> getPairCounts()
/*  45:    */   {
/*  46:139 */     if (this.pairCounts == null) {
/*  47:140 */       this.pairCounts = new ArrayList();
/*  48:    */     }
/*  49:142 */     return this.pairCounts;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public String getFieldName()
/*  53:    */   {
/*  54:154 */     return this.fieldName;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void setFieldName(String value)
/*  58:    */   {
/*  59:166 */     this.fieldName = value;
/*  60:    */   }
/*  61:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.BayesInput
 * JD-Core Version:    0.7.0.1
 */