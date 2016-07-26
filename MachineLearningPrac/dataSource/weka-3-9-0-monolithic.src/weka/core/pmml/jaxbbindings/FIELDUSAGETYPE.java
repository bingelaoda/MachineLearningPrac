/*  1:   */ package weka.core.pmml.jaxbbindings;
/*  2:   */ 
/*  3:   */ import javax.xml.bind.annotation.XmlEnum;
/*  4:   */ import javax.xml.bind.annotation.XmlEnumValue;
/*  5:   */ 
/*  6:   */ @XmlEnum
/*  7:   */ public enum FIELDUSAGETYPE
/*  8:   */ {
/*  9:38 */   ACTIVE("active"),  ANALYSIS_WEIGHT("analysisWeight"),  FREQUENCY_WEIGHT("frequencyWeight"),  GROUP("group"),  ORDER("order"),  PREDICTED("predicted"),  SUPPLEMENTARY("supplementary");
/* 10:   */   
/* 11:   */   private final String value;
/* 12:   */   
/* 13:   */   private FIELDUSAGETYPE(String v)
/* 14:   */   {
/* 15:55 */     this.value = v;
/* 16:   */   }
/* 17:   */   
/* 18:   */   public String value()
/* 19:   */   {
/* 20:59 */     return this.value;
/* 21:   */   }
/* 22:   */   
/* 23:   */   public static FIELDUSAGETYPE fromValue(String v)
/* 24:   */   {
/* 25:63 */     for (FIELDUSAGETYPE c : ) {
/* 26:64 */       if (c.value.equals(v)) {
/* 27:65 */         return c;
/* 28:   */       }
/* 29:   */     }
/* 30:68 */     throw new IllegalArgumentException(v.toString());
/* 31:   */   }
/* 32:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.FIELDUSAGETYPE
 * JD-Core Version:    0.7.0.1
 */