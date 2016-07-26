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
/*  14:    */ @XmlRootElement(name="CategoricalPredictor")
/*  15:    */ public class CategoricalPredictor
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlAttribute(required=true)
/*  20:    */   protected double coefficient;
/*  21:    */   @XmlAttribute(required=true)
/*  22:    */   protected String name;
/*  23:    */   @XmlAttribute(required=true)
/*  24:    */   protected String value;
/*  25:    */   
/*  26:    */   public List<Extension> getExtension()
/*  27:    */   {
/*  28: 84 */     if (this.extension == null) {
/*  29: 85 */       this.extension = new ArrayList();
/*  30:    */     }
/*  31: 87 */     return this.extension;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public double getCoefficient()
/*  35:    */   {
/*  36: 95 */     return this.coefficient;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void setCoefficient(double value)
/*  40:    */   {
/*  41:103 */     this.coefficient = value;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public String getName()
/*  45:    */   {
/*  46:115 */     return this.name;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void setName(String value)
/*  50:    */   {
/*  51:127 */     this.name = value;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public String getValue()
/*  55:    */   {
/*  56:139 */     return this.value;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public void setValue(String value)
/*  60:    */   {
/*  61:151 */     this.value = value;
/*  62:    */   }
/*  63:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.CategoricalPredictor
 * JD-Core Version:    0.7.0.1
 */