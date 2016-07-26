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
/*  14:    */ @XmlRootElement(name="KNNInput")
/*  15:    */ public class KNNInput
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlAttribute
/*  20:    */   protected COMPAREFUNCTION compareFunction;
/*  21:    */   @XmlAttribute(required=true)
/*  22:    */   protected String field;
/*  23:    */   @XmlAttribute
/*  24:    */   protected Double fieldWeight;
/*  25:    */   
/*  26:    */   public List<Extension> getExtension()
/*  27:    */   {
/*  28: 84 */     if (this.extension == null) {
/*  29: 85 */       this.extension = new ArrayList();
/*  30:    */     }
/*  31: 87 */     return this.extension;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public COMPAREFUNCTION getCompareFunction()
/*  35:    */   {
/*  36: 99 */     return this.compareFunction;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void setCompareFunction(COMPAREFUNCTION value)
/*  40:    */   {
/*  41:111 */     this.compareFunction = value;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public String getField()
/*  45:    */   {
/*  46:123 */     return this.field;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void setField(String value)
/*  50:    */   {
/*  51:135 */     this.field = value;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public double getFieldWeight()
/*  55:    */   {
/*  56:147 */     if (this.fieldWeight == null) {
/*  57:148 */       return 1.0D;
/*  58:    */     }
/*  59:150 */     return this.fieldWeight.doubleValue();
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void setFieldWeight(Double value)
/*  63:    */   {
/*  64:163 */     this.fieldWeight = value;
/*  65:    */   }
/*  66:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.KNNInput
 * JD-Core Version:    0.7.0.1
 */