/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   4:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   5:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   6:    */ import javax.xml.bind.annotation.XmlElement;
/*   7:    */ import javax.xml.bind.annotation.XmlRootElement;
/*   8:    */ import javax.xml.bind.annotation.XmlType;
/*   9:    */ import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
/*  10:    */ import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
/*  11:    */ 
/*  12:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  13:    */ @XmlType(name="", propOrder={"array"})
/*  14:    */ @XmlRootElement(name="Trend_ExpoSmooth")
/*  15:    */ public class TrendExpoSmooth
/*  16:    */ {
/*  17:    */   @XmlElement(name="Array", namespace="http://www.dmg.org/PMML-4_1")
/*  18:    */   protected ArrayType array;
/*  19:    */   @XmlAttribute
/*  20:    */   protected Double gamma;
/*  21:    */   @XmlAttribute
/*  22:    */   protected Double phi;
/*  23:    */   @XmlAttribute
/*  24:    */   protected Double smoothedValue;
/*  25:    */   @XmlAttribute
/*  26:    */   @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
/*  27:    */   protected String trend;
/*  28:    */   
/*  29:    */   public ArrayType getArray()
/*  30:    */   {
/*  31: 84 */     return this.array;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public void setArray(ArrayType value)
/*  35:    */   {
/*  36: 96 */     this.array = value;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public Double getGamma()
/*  40:    */   {
/*  41:108 */     return this.gamma;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public void setGamma(Double value)
/*  45:    */   {
/*  46:120 */     this.gamma = value;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public double getPhi()
/*  50:    */   {
/*  51:132 */     if (this.phi == null) {
/*  52:133 */       return 1.0D;
/*  53:    */     }
/*  54:135 */     return this.phi.doubleValue();
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void setPhi(Double value)
/*  58:    */   {
/*  59:148 */     this.phi = value;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public Double getSmoothedValue()
/*  63:    */   {
/*  64:160 */     return this.smoothedValue;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void setSmoothedValue(Double value)
/*  68:    */   {
/*  69:172 */     this.smoothedValue = value;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public String getTrend()
/*  73:    */   {
/*  74:184 */     if (this.trend == null) {
/*  75:185 */       return "additive";
/*  76:    */     }
/*  77:187 */     return this.trend;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public void setTrend(String value)
/*  81:    */   {
/*  82:200 */     this.trend = value;
/*  83:    */   }
/*  84:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.TrendExpoSmooth
 * JD-Core Version:    0.7.0.1
 */