/*  1:   */ package weka.core.pmml.jaxbbindings;
/*  2:   */ 
/*  3:   */ import javax.xml.bind.annotation.XmlEnum;
/*  4:   */ import javax.xml.bind.annotation.XmlEnumValue;
/*  5:   */ 
/*  6:   */ @XmlEnum
/*  7:   */ public enum OUTLIERTREATMENTMETHOD
/*  8:   */ {
/*  9:34 */   AS_EXTREME_VALUES("asExtremeValues"),  AS_IS("asIs"),  AS_MISSING_VALUES("asMissingValues");
/* 10:   */   
/* 11:   */   private final String value;
/* 12:   */   
/* 13:   */   private OUTLIERTREATMENTMETHOD(String v)
/* 14:   */   {
/* 15:43 */     this.value = v;
/* 16:   */   }
/* 17:   */   
/* 18:   */   public String value()
/* 19:   */   {
/* 20:47 */     return this.value;
/* 21:   */   }
/* 22:   */   
/* 23:   */   public static OUTLIERTREATMENTMETHOD fromValue(String v)
/* 24:   */   {
/* 25:51 */     for (OUTLIERTREATMENTMETHOD c : ) {
/* 26:52 */       if (c.value.equals(v)) {
/* 27:53 */         return c;
/* 28:   */       }
/* 29:   */     }
/* 30:56 */     throw new IllegalArgumentException(v.toString());
/* 31:   */   }
/* 32:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.OUTLIERTREATMENTMETHOD
 * JD-Core Version:    0.7.0.1
 */