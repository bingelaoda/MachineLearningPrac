/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.math.BigInteger;
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
/*  15:    */ @XmlRootElement(name="PCell")
/*  16:    */ public class PCell
/*  17:    */ {
/*  18:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected List<Extension> extension;
/*  20:    */   @XmlAttribute(required=true)
/*  21:    */   protected double beta;
/*  22:    */   @XmlAttribute
/*  23:    */   protected BigInteger df;
/*  24:    */   @XmlAttribute(required=true)
/*  25:    */   protected String parameterName;
/*  26:    */   @XmlAttribute
/*  27:    */   protected String targetCategory;
/*  28:    */   
/*  29:    */   public PCell() {}
/*  30:    */   
/*  31:    */   public PCell(String targetCategory, String parameterName, BigInteger df, double beta)
/*  32:    */   {
/*  33: 68 */     this.targetCategory = targetCategory;
/*  34: 69 */     this.parameterName = parameterName;
/*  35: 70 */     this.df = df;
/*  36: 71 */     this.beta = beta;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public List<Extension> getExtension()
/*  40:    */   {
/*  41: 97 */     if (this.extension == null) {
/*  42: 98 */       this.extension = new ArrayList();
/*  43:    */     }
/*  44:100 */     return this.extension;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public double getBeta()
/*  48:    */   {
/*  49:108 */     return this.beta;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void setBeta(double value)
/*  53:    */   {
/*  54:116 */     this.beta = value;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public BigInteger getDf()
/*  58:    */   {
/*  59:128 */     return this.df;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void setDf(BigInteger value)
/*  63:    */   {
/*  64:140 */     this.df = value;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public String getParameterName()
/*  68:    */   {
/*  69:152 */     return this.parameterName;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void setParameterName(String value)
/*  73:    */   {
/*  74:164 */     this.parameterName = value;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public String getTargetCategory()
/*  78:    */   {
/*  79:176 */     return this.targetCategory;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public void setTargetCategory(String value)
/*  83:    */   {
/*  84:188 */     this.targetCategory = value;
/*  85:    */   }
/*  86:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.PCell
 * JD-Core Version:    0.7.0.1
 */