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
/*  12:    */ @XmlType(name="", propOrder={"extension", "correlationFields", "correlationValues", "correlationMethods"})
/*  13:    */ @XmlRootElement(name="Correlations")
/*  14:    */ public class Correlations
/*  15:    */ {
/*  16:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  17:    */   protected List<Extension> extension;
/*  18:    */   @XmlElement(name="CorrelationFields", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected CorrelationFields correlationFields;
/*  20:    */   @XmlElement(name="CorrelationValues", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  21:    */   protected CorrelationValues correlationValues;
/*  22:    */   @XmlElement(name="CorrelationMethods", namespace="http://www.dmg.org/PMML-4_1")
/*  23:    */   protected CorrelationMethods correlationMethods;
/*  24:    */   
/*  25:    */   public List<Extension> getExtension()
/*  26:    */   {
/*  27: 86 */     if (this.extension == null) {
/*  28: 87 */       this.extension = new ArrayList();
/*  29:    */     }
/*  30: 89 */     return this.extension;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public CorrelationFields getCorrelationFields()
/*  34:    */   {
/*  35:101 */     return this.correlationFields;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void setCorrelationFields(CorrelationFields value)
/*  39:    */   {
/*  40:113 */     this.correlationFields = value;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public CorrelationValues getCorrelationValues()
/*  44:    */   {
/*  45:125 */     return this.correlationValues;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void setCorrelationValues(CorrelationValues value)
/*  49:    */   {
/*  50:137 */     this.correlationValues = value;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public CorrelationMethods getCorrelationMethods()
/*  54:    */   {
/*  55:149 */     return this.correlationMethods;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public void setCorrelationMethods(CorrelationMethods value)
/*  59:    */   {
/*  60:161 */     this.correlationMethods = value;
/*  61:    */   }
/*  62:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Correlations
 * JD-Core Version:    0.7.0.1
 */