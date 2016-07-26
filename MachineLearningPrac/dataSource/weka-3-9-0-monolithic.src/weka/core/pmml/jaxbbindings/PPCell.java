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
/*  14:    */ @XmlRootElement(name="PPCell")
/*  15:    */ public class PPCell
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlAttribute(required=true)
/*  20:    */   protected String parameterName;
/*  21:    */   @XmlAttribute(required=true)
/*  22:    */   protected String predictorName;
/*  23:    */   @XmlAttribute
/*  24:    */   protected String targetCategory;
/*  25:    */   @XmlAttribute(required=true)
/*  26:    */   protected String value;
/*  27:    */   
/*  28:    */   public List<Extension> getExtension()
/*  29:    */   {
/*  30: 87 */     if (this.extension == null) {
/*  31: 88 */       this.extension = new ArrayList();
/*  32:    */     }
/*  33: 90 */     return this.extension;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public String getParameterName()
/*  37:    */   {
/*  38:102 */     return this.parameterName;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void setParameterName(String value)
/*  42:    */   {
/*  43:114 */     this.parameterName = value;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public String getPredictorName()
/*  47:    */   {
/*  48:126 */     return this.predictorName;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void setPredictorName(String value)
/*  52:    */   {
/*  53:138 */     this.predictorName = value;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public String getTargetCategory()
/*  57:    */   {
/*  58:150 */     return this.targetCategory;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void setTargetCategory(String value)
/*  62:    */   {
/*  63:162 */     this.targetCategory = value;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public String getValue()
/*  67:    */   {
/*  68:174 */     return this.value;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void setValue(String value)
/*  72:    */   {
/*  73:186 */     this.value = value;
/*  74:    */   }
/*  75:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.PPCell
 * JD-Core Version:    0.7.0.1
 */