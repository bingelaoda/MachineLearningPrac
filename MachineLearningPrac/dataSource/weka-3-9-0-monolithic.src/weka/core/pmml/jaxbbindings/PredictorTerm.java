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
/*  13:    */ @XmlType(name="", propOrder={"extension", "fieldRef"})
/*  14:    */ @XmlRootElement(name="PredictorTerm")
/*  15:    */ public class PredictorTerm
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="FieldRef", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected List<FieldRef> fieldRef;
/*  21:    */   @XmlAttribute(required=true)
/*  22:    */   protected double coefficient;
/*  23:    */   @XmlAttribute
/*  24:    */   protected String name;
/*  25:    */   
/*  26:    */   public List<Extension> getExtension()
/*  27:    */   {
/*  28: 85 */     if (this.extension == null) {
/*  29: 86 */       this.extension = new ArrayList();
/*  30:    */     }
/*  31: 88 */     return this.extension;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public List<FieldRef> getFieldRef()
/*  35:    */   {
/*  36:114 */     if (this.fieldRef == null) {
/*  37:115 */       this.fieldRef = new ArrayList();
/*  38:    */     }
/*  39:117 */     return this.fieldRef;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public double getCoefficient()
/*  43:    */   {
/*  44:125 */     return this.coefficient;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void setCoefficient(double value)
/*  48:    */   {
/*  49:133 */     this.coefficient = value;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public String getName()
/*  53:    */   {
/*  54:145 */     return this.name;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void setName(String value)
/*  58:    */   {
/*  59:157 */     this.name = value;
/*  60:    */   }
/*  61:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.PredictorTerm
 * JD-Core Version:    0.7.0.1
 */