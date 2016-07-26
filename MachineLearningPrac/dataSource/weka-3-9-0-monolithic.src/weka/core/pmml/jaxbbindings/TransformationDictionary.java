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
/*  12:    */ @XmlType(name="", propOrder={"extension", "defineFunction", "derivedField"})
/*  13:    */ @XmlRootElement(name="TransformationDictionary")
/*  14:    */ public class TransformationDictionary
/*  15:    */ {
/*  16:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  17:    */   protected List<Extension> extension;
/*  18:    */   @XmlElement(name="DefineFunction", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected List<DefineFunction> defineFunction;
/*  20:    */   @XmlElement(name="DerivedField", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  21:    */   protected List<DerivedField> derivedField;
/*  22:    */   
/*  23:    */   public List<Extension> getExtension()
/*  24:    */   {
/*  25: 82 */     if (this.extension == null) {
/*  26: 83 */       this.extension = new ArrayList();
/*  27:    */     }
/*  28: 85 */     return this.extension;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public List<DefineFunction> getDefineFunction()
/*  32:    */   {
/*  33:111 */     if (this.defineFunction == null) {
/*  34:112 */       this.defineFunction = new ArrayList();
/*  35:    */     }
/*  36:114 */     return this.defineFunction;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public List<DerivedField> getDerivedField()
/*  40:    */   {
/*  41:140 */     if (this.derivedField == null) {
/*  42:141 */       this.derivedField = new ArrayList();
/*  43:    */     }
/*  44:143 */     return this.derivedField;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void addDerivedField(DerivedField field)
/*  48:    */   {
/*  49:147 */     if (this.derivedField == null) {
/*  50:148 */       this.derivedField = new ArrayList();
/*  51:    */     }
/*  52:150 */     this.derivedField.add(field);
/*  53:    */   }
/*  54:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.TransformationDictionary
 * JD-Core Version:    0.7.0.1
 */