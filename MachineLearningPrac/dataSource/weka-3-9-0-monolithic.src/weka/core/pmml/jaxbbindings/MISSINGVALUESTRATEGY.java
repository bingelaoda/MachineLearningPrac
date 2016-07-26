/*  1:   */ package weka.core.pmml.jaxbbindings;
/*  2:   */ 
/*  3:   */ import javax.xml.bind.annotation.XmlEnum;
/*  4:   */ import javax.xml.bind.annotation.XmlEnumValue;
/*  5:   */ 
/*  6:   */ @XmlEnum
/*  7:   */ public enum MISSINGVALUESTRATEGY
/*  8:   */ {
/*  9:37 */   AGGREGATE_NODES("aggregateNodes"),  DEFAULT_CHILD("defaultChild"),  LAST_PREDICTION("lastPrediction"),  NONE("none"),  NULL_PREDICTION("nullPrediction"),  WEIGHTED_CONFIDENCE("weightedConfidence");
/* 10:   */   
/* 11:   */   private final String value;
/* 12:   */   
/* 13:   */   private MISSINGVALUESTRATEGY(String v)
/* 14:   */   {
/* 15:52 */     this.value = v;
/* 16:   */   }
/* 17:   */   
/* 18:   */   public String value()
/* 19:   */   {
/* 20:56 */     return this.value;
/* 21:   */   }
/* 22:   */   
/* 23:   */   public static MISSINGVALUESTRATEGY fromValue(String v)
/* 24:   */   {
/* 25:60 */     for (MISSINGVALUESTRATEGY c : ) {
/* 26:61 */       if (c.value.equals(v)) {
/* 27:62 */         return c;
/* 28:   */       }
/* 29:   */     }
/* 30:65 */     throw new IllegalArgumentException(v.toString());
/* 31:   */   }
/* 32:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.MISSINGVALUESTRATEGY
 * JD-Core Version:    0.7.0.1
 */