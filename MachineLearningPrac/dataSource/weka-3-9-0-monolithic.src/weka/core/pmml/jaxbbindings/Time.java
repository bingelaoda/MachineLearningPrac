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
/*  14:    */ @XmlRootElement(name="Time")
/*  15:    */ public class Time
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlAttribute
/*  20:    */   protected Double max;
/*  21:    */   @XmlAttribute
/*  22:    */   protected Double mean;
/*  23:    */   @XmlAttribute
/*  24:    */   protected Double min;
/*  25:    */   @XmlAttribute
/*  26:    */   protected Double standardDeviation;
/*  27:    */   
/*  28:    */   public List<Extension> getExtension()
/*  29:    */   {
/*  30: 87 */     if (this.extension == null) {
/*  31: 88 */       this.extension = new ArrayList();
/*  32:    */     }
/*  33: 90 */     return this.extension;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public Double getMax()
/*  37:    */   {
/*  38:102 */     return this.max;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void setMax(Double value)
/*  42:    */   {
/*  43:114 */     this.max = value;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public Double getMean()
/*  47:    */   {
/*  48:126 */     return this.mean;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void setMean(Double value)
/*  52:    */   {
/*  53:138 */     this.mean = value;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public Double getMin()
/*  57:    */   {
/*  58:150 */     return this.min;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void setMin(Double value)
/*  62:    */   {
/*  63:162 */     this.min = value;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public Double getStandardDeviation()
/*  67:    */   {
/*  68:174 */     return this.standardDeviation;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void setStandardDeviation(Double value)
/*  72:    */   {
/*  73:186 */     this.standardDeviation = value;
/*  74:    */   }
/*  75:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Time
 * JD-Core Version:    0.7.0.1
 */