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
/*  12:    */ @XmlType(name="", propOrder={"extension", "outputField"})
/*  13:    */ @XmlRootElement(name="Output")
/*  14:    */ public class Output
/*  15:    */ {
/*  16:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  17:    */   protected List<Extension> extension;
/*  18:    */   @XmlElement(name="OutputField", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected List<OutputField> outputField;
/*  20:    */   
/*  21:    */   public List<Extension> getExtension()
/*  22:    */   {
/*  23: 78 */     if (this.extension == null) {
/*  24: 79 */       this.extension = new ArrayList();
/*  25:    */     }
/*  26: 81 */     return this.extension;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public List<OutputField> getOutputFields()
/*  30:    */   {
/*  31:107 */     if (this.outputField == null) {
/*  32:108 */       this.outputField = new ArrayList();
/*  33:    */     }
/*  34:110 */     return this.outputField;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public void addOutputField(OutputField field)
/*  38:    */   {
/*  39:114 */     if (this.outputField == null) {
/*  40:115 */       this.outputField = new ArrayList();
/*  41:    */     }
/*  42:117 */     this.outputField.add(field);
/*  43:    */   }
/*  44:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Output
 * JD-Core Version:    0.7.0.1
 */