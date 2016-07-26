/*  1:   */ package weka.core.pmml.jaxbbindings;
/*  2:   */ 
/*  3:   */ import javax.xml.bind.annotation.XmlEnum;
/*  4:   */ import javax.xml.bind.annotation.XmlEnumValue;
/*  5:   */ 
/*  6:   */ @XmlEnum
/*  7:   */ public enum TIMESERIESALGORITHM
/*  8:   */ {
/*  9:35 */   ARIMA("ARIMA"),  EXPONENTIAL_SMOOTHING("ExponentialSmoothing"),  SEASONAL_TREND_DECOMPOSITION("SeasonalTrendDecomposition"),  SPECTRAL_ANALYSIS("SpectralAnalysis");
/* 10:   */   
/* 11:   */   private final String value;
/* 12:   */   
/* 13:   */   private TIMESERIESALGORITHM(String v)
/* 14:   */   {
/* 15:45 */     this.value = v;
/* 16:   */   }
/* 17:   */   
/* 18:   */   public String value()
/* 19:   */   {
/* 20:49 */     return this.value;
/* 21:   */   }
/* 22:   */   
/* 23:   */   public static TIMESERIESALGORITHM fromValue(String v)
/* 24:   */   {
/* 25:53 */     for (TIMESERIESALGORITHM c : ) {
/* 26:54 */       if (c.value.equals(v)) {
/* 27:55 */         return c;
/* 28:   */       }
/* 29:   */     }
/* 30:58 */     throw new IllegalArgumentException(v.toString());
/* 31:   */   }
/* 32:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.TIMESERIESALGORITHM
 * JD-Core Version:    0.7.0.1
 */