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
/*  14:    */ @XmlType(name="", propOrder={"extension", "dataField", "taxonomy"})
/*  15:    */ @XmlRootElement(name="DataDictionary")
/*  16:    */ public class DataDictionary
/*  17:    */ {
/*  18:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected List<Extension> extension;
/*  20:    */   @XmlElement(name="DataField", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  21:    */   protected List<DataField> dataField;
/*  22:    */   @XmlElement(name="Taxonomy", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  23:    */   protected List<Taxonomy> taxonomy;
/*  24:    */   @XmlAttribute
/*  25:    */   protected BigInteger numberOfFields;
/*  26:    */   
/*  27:    */   public List<Extension> getExtension()
/*  28:    */   {
/*  29: 87 */     if (this.extension == null) {
/*  30: 88 */       this.extension = new ArrayList();
/*  31:    */     }
/*  32: 90 */     return this.extension;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public List<DataField> getDataFields()
/*  36:    */   {
/*  37:115 */     if (this.dataField == null) {
/*  38:116 */       this.dataField = new ArrayList();
/*  39:    */     }
/*  40:118 */     return this.dataField;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void addDataField(DataField field)
/*  44:    */   {
/*  45:122 */     if (this.dataField == null) {
/*  46:123 */       this.dataField = new ArrayList();
/*  47:    */     }
/*  48:125 */     this.dataField.add(field);
/*  49:    */   }
/*  50:    */   
/*  51:    */   public List<Taxonomy> getTaxonomy()
/*  52:    */   {
/*  53:151 */     if (this.taxonomy == null) {
/*  54:152 */       this.taxonomy = new ArrayList();
/*  55:    */     }
/*  56:154 */     return this.taxonomy;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public BigInteger getNumberOfFields()
/*  60:    */   {
/*  61:166 */     return this.numberOfFields;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public void setNumberOfFields(BigInteger value)
/*  65:    */   {
/*  66:178 */     this.numberOfFields = value;
/*  67:    */   }
/*  68:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.DataDictionary
 * JD-Core Version:    0.7.0.1
 */