/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.math.BigDecimal;
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
/*  14:    */ @XmlType(name="", propOrder={"extension"})
/*  15:    */ @XmlRootElement(name="AnovaRow")
/*  16:    */ public class AnovaRow
/*  17:    */ {
/*  18:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected List<Extension> extension;
/*  20:    */   @XmlAttribute(required=true)
/*  21:    */   protected double degreesOfFreedom;
/*  22:    */   @XmlAttribute
/*  23:    */   protected Double fValue;
/*  24:    */   @XmlAttribute
/*  25:    */   protected Double meanOfSquares;
/*  26:    */   @XmlAttribute
/*  27:    */   protected BigDecimal pValue;
/*  28:    */   @XmlAttribute(required=true)
/*  29:    */   protected double sumOfSquares;
/*  30:    */   @XmlAttribute(required=true)
/*  31:    */   protected String type;
/*  32:    */   
/*  33:    */   public List<Extension> getExtension()
/*  34:    */   {
/*  35:102 */     if (this.extension == null) {
/*  36:103 */       this.extension = new ArrayList();
/*  37:    */     }
/*  38:105 */     return this.extension;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public double getDegreesOfFreedom()
/*  42:    */   {
/*  43:113 */     return this.degreesOfFreedom;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void setDegreesOfFreedom(double value)
/*  47:    */   {
/*  48:121 */     this.degreesOfFreedom = value;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public Double getFValue()
/*  52:    */   {
/*  53:133 */     return this.fValue;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public void setFValue(Double value)
/*  57:    */   {
/*  58:145 */     this.fValue = value;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public Double getMeanOfSquares()
/*  62:    */   {
/*  63:157 */     return this.meanOfSquares;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public void setMeanOfSquares(Double value)
/*  67:    */   {
/*  68:169 */     this.meanOfSquares = value;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public BigDecimal getPValue()
/*  72:    */   {
/*  73:181 */     return this.pValue;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void setPValue(BigDecimal value)
/*  77:    */   {
/*  78:193 */     this.pValue = value;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public double getSumOfSquares()
/*  82:    */   {
/*  83:201 */     return this.sumOfSquares;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public void setSumOfSquares(double value)
/*  87:    */   {
/*  88:209 */     this.sumOfSquares = value;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public String getType()
/*  92:    */   {
/*  93:221 */     return this.type;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public void setType(String value)
/*  97:    */   {
/*  98:233 */     this.type = value;
/*  99:    */   }
/* 100:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.AnovaRow
 * JD-Core Version:    0.7.0.1
 */