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
/*  13:    */ @XmlType(name="", propOrder={"extension", "quantile"})
/*  14:    */ @XmlRootElement(name="NumericInfo")
/*  15:    */ public class NumericInfo
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="Quantile", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected List<Quantile> quantile;
/*  21:    */   @XmlAttribute
/*  22:    */   protected Double interQuartileRange;
/*  23:    */   @XmlAttribute
/*  24:    */   protected Double maximum;
/*  25:    */   @XmlAttribute
/*  26:    */   protected Double mean;
/*  27:    */   @XmlAttribute
/*  28:    */   protected Double median;
/*  29:    */   @XmlAttribute
/*  30:    */   protected Double minimum;
/*  31:    */   @XmlAttribute
/*  32:    */   protected Double standardDeviation;
/*  33:    */   
/*  34:    */   public List<Extension> getExtension()
/*  35:    */   {
/*  36: 97 */     if (this.extension == null) {
/*  37: 98 */       this.extension = new ArrayList();
/*  38:    */     }
/*  39:100 */     return this.extension;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public List<Quantile> getQuantile()
/*  43:    */   {
/*  44:126 */     if (this.quantile == null) {
/*  45:127 */       this.quantile = new ArrayList();
/*  46:    */     }
/*  47:129 */     return this.quantile;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public Double getInterQuartileRange()
/*  51:    */   {
/*  52:141 */     return this.interQuartileRange;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public void setInterQuartileRange(Double value)
/*  56:    */   {
/*  57:153 */     this.interQuartileRange = value;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public Double getMaximum()
/*  61:    */   {
/*  62:165 */     return this.maximum;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public void setMaximum(Double value)
/*  66:    */   {
/*  67:177 */     this.maximum = value;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public Double getMean()
/*  71:    */   {
/*  72:189 */     return this.mean;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public void setMean(Double value)
/*  76:    */   {
/*  77:201 */     this.mean = value;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public Double getMedian()
/*  81:    */   {
/*  82:213 */     return this.median;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public void setMedian(Double value)
/*  86:    */   {
/*  87:225 */     this.median = value;
/*  88:    */   }
/*  89:    */   
/*  90:    */   public Double getMinimum()
/*  91:    */   {
/*  92:237 */     return this.minimum;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public void setMinimum(Double value)
/*  96:    */   {
/*  97:249 */     this.minimum = value;
/*  98:    */   }
/*  99:    */   
/* 100:    */   public Double getStandardDeviation()
/* 101:    */   {
/* 102:261 */     return this.standardDeviation;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public void setStandardDeviation(Double value)
/* 106:    */   {
/* 107:273 */     this.standardDeviation = value;
/* 108:    */   }
/* 109:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.NumericInfo
 * JD-Core Version:    0.7.0.1
 */