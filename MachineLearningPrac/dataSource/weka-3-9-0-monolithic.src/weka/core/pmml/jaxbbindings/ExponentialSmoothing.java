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
/*  11:    */ import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
/*  12:    */ import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
/*  13:    */ 
/*  14:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  15:    */ @XmlType(name="", propOrder={"level", "trendExpoSmooth", "seasonalityExpoSmooth", "timeValue"})
/*  16:    */ @XmlRootElement(name="ExponentialSmoothing")
/*  17:    */ public class ExponentialSmoothing
/*  18:    */ {
/*  19:    */   @XmlElement(name="Level", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected Level level;
/*  21:    */   @XmlElement(name="Trend_ExpoSmooth", namespace="http://www.dmg.org/PMML-4_1")
/*  22:    */   protected TrendExpoSmooth trendExpoSmooth;
/*  23:    */   @XmlElement(name="Seasonality_ExpoSmooth", namespace="http://www.dmg.org/PMML-4_1")
/*  24:    */   protected SeasonalityExpoSmooth seasonalityExpoSmooth;
/*  25:    */   @XmlElement(name="TimeValue", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  26:    */   protected List<TimeValue> timeValue;
/*  27:    */   @XmlAttribute(name="RMSE")
/*  28:    */   protected Double rmse;
/*  29:    */   @XmlAttribute
/*  30:    */   @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
/*  31:    */   protected String transformation;
/*  32:    */   
/*  33:    */   public Level getLevel()
/*  34:    */   {
/*  35: 90 */     return this.level;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void setLevel(Level value)
/*  39:    */   {
/*  40:102 */     this.level = value;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public TrendExpoSmooth getTrendExpoSmooth()
/*  44:    */   {
/*  45:114 */     return this.trendExpoSmooth;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void setTrendExpoSmooth(TrendExpoSmooth value)
/*  49:    */   {
/*  50:126 */     this.trendExpoSmooth = value;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public SeasonalityExpoSmooth getSeasonalityExpoSmooth()
/*  54:    */   {
/*  55:138 */     return this.seasonalityExpoSmooth;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public void setSeasonalityExpoSmooth(SeasonalityExpoSmooth value)
/*  59:    */   {
/*  60:150 */     this.seasonalityExpoSmooth = value;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public List<TimeValue> getTimeValue()
/*  64:    */   {
/*  65:176 */     if (this.timeValue == null) {
/*  66:177 */       this.timeValue = new ArrayList();
/*  67:    */     }
/*  68:179 */     return this.timeValue;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public Double getRMSE()
/*  72:    */   {
/*  73:191 */     return this.rmse;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void setRMSE(Double value)
/*  77:    */   {
/*  78:203 */     this.rmse = value;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public String getTransformation()
/*  82:    */   {
/*  83:215 */     if (this.transformation == null) {
/*  84:216 */       return "none";
/*  85:    */     }
/*  86:218 */     return this.transformation;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void setTransformation(String value)
/*  90:    */   {
/*  91:231 */     this.transformation = value;
/*  92:    */   }
/*  93:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.ExponentialSmoothing
 * JD-Core Version:    0.7.0.1
 */