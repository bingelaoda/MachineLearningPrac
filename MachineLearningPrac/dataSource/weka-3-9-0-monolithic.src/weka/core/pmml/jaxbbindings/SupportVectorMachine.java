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
/*  13:    */ @XmlType(name="", propOrder={"extension", "supportVectors", "coefficients"})
/*  14:    */ @XmlRootElement(name="SupportVectorMachine")
/*  15:    */ public class SupportVectorMachine
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="SupportVectors", namespace="http://www.dmg.org/PMML-4_1")
/*  20:    */   protected SupportVectors supportVectors;
/*  21:    */   @XmlElement(name="Coefficients", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  22:    */   protected Coefficients coefficients;
/*  23:    */   @XmlAttribute
/*  24:    */   protected String alternateTargetCategory;
/*  25:    */   @XmlAttribute
/*  26:    */   protected String targetCategory;
/*  27:    */   @XmlAttribute
/*  28:    */   protected Double threshold;
/*  29:    */   
/*  30:    */   public List<Extension> getExtension()
/*  31:    */   {
/*  32: 92 */     if (this.extension == null) {
/*  33: 93 */       this.extension = new ArrayList();
/*  34:    */     }
/*  35: 95 */     return this.extension;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public SupportVectors getSupportVectors()
/*  39:    */   {
/*  40:107 */     return this.supportVectors;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void setSupportVectors(SupportVectors value)
/*  44:    */   {
/*  45:119 */     this.supportVectors = value;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public Coefficients getCoefficients()
/*  49:    */   {
/*  50:131 */     return this.coefficients;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void setCoefficients(Coefficients value)
/*  54:    */   {
/*  55:143 */     this.coefficients = value;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public String getAlternateTargetCategory()
/*  59:    */   {
/*  60:155 */     return this.alternateTargetCategory;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void setAlternateTargetCategory(String value)
/*  64:    */   {
/*  65:167 */     this.alternateTargetCategory = value;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public String getTargetCategory()
/*  69:    */   {
/*  70:179 */     return this.targetCategory;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void setTargetCategory(String value)
/*  74:    */   {
/*  75:191 */     this.targetCategory = value;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public Double getThreshold()
/*  79:    */   {
/*  80:203 */     return this.threshold;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public void setThreshold(Double value)
/*  84:    */   {
/*  85:215 */     this.threshold = value;
/*  86:    */   }
/*  87:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.SupportVectorMachine
 * JD-Core Version:    0.7.0.1
 */