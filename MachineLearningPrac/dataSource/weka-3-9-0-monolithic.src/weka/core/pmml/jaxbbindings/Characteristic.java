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
/*  13:    */ @XmlType(name="", propOrder={"extension", "attribute"})
/*  14:    */ @XmlRootElement(name="Characteristic")
/*  15:    */ public class Characteristic
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="Attribute", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected List<Attribute> attribute;
/*  21:    */   @XmlAttribute
/*  22:    */   protected Double baselineScore;
/*  23:    */   @XmlAttribute
/*  24:    */   protected String name;
/*  25:    */   @XmlAttribute
/*  26:    */   protected String reasonCode;
/*  27:    */   
/*  28:    */   public List<Extension> getExtension()
/*  29:    */   {
/*  30: 88 */     if (this.extension == null) {
/*  31: 89 */       this.extension = new ArrayList();
/*  32:    */     }
/*  33: 91 */     return this.extension;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public List<Attribute> getAttribute()
/*  37:    */   {
/*  38:117 */     if (this.attribute == null) {
/*  39:118 */       this.attribute = new ArrayList();
/*  40:    */     }
/*  41:120 */     return this.attribute;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public Double getBaselineScore()
/*  45:    */   {
/*  46:132 */     return this.baselineScore;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void setBaselineScore(Double value)
/*  50:    */   {
/*  51:144 */     this.baselineScore = value;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public String getName()
/*  55:    */   {
/*  56:156 */     return this.name;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public void setName(String value)
/*  60:    */   {
/*  61:168 */     this.name = value;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public String getReasonCode()
/*  65:    */   {
/*  66:180 */     return this.reasonCode;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public void setReasonCode(String value)
/*  70:    */   {
/*  71:192 */     this.reasonCode = value;
/*  72:    */   }
/*  73:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Characteristic
 * JD-Core Version:    0.7.0.1
 */