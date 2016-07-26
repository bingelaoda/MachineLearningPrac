/*  1:   */ package weka.core.pmml.jaxbbindings;
/*  2:   */ 
/*  3:   */ import javax.xml.bind.annotation.XmlEnum;
/*  4:   */ import javax.xml.bind.annotation.XmlEnumValue;
/*  5:   */ 
/*  6:   */ @XmlEnum
/*  7:   */ public enum TIMEANCHOR2
/*  8:   */ {
/*  9:48 */   DATE_DAYS_SINCE_0("dateDaysSince[0]"),  DATE_DAYS_SINCE_1960("dateDaysSince[1960]"),  DATE_DAYS_SINCE_1970("dateDaysSince[1970]"),  DATE_DAYS_SINCE_1980("dateDaysSince[1980]"),  DATE_MONTHS_SINCE_0("dateMonthsSince[0]"),  DATE_MONTHS_SINCE_1960("dateMonthsSince[1960]"),  DATE_MONTHS_SINCE_1970("dateMonthsSince[1970]"),  DATE_MONTHS_SINCE_1980("dateMonthsSince[1980]"),  DATE_TIME_MILLISECONDS_SINCE_0("dateTimeMillisecondsSince[0]"),  DATE_TIME_MILLISECONDS_SINCE_1960("dateTimeMillisecondsSince[1960]"),  DATE_TIME_MILLISECONDS_SINCE_1970("dateTimeMillisecondsSince[1970]"),  DATE_TIME_MILLISECONDS_SINCE_1980("dateTimeMillisecondsSince[1980]"),  DATE_TIME_SECONDS_SINCE_0("dateTimeSecondsSince[0]"),  DATE_TIME_SECONDS_SINCE_1960("dateTimeSecondsSince[1960]"),  DATE_TIME_SECONDS_SINCE_1970("dateTimeSecondsSince[1970]"),  DATE_TIME_SECONDS_SINCE_1980("dateTimeSecondsSince[1980]"),  DATE_YEARS_SINCE_0("dateYearsSince[0]");
/* 10:   */   
/* 11:   */   private final String value;
/* 12:   */   
/* 13:   */   private TIMEANCHOR2(String v)
/* 14:   */   {
/* 15:85 */     this.value = v;
/* 16:   */   }
/* 17:   */   
/* 18:   */   public String value()
/* 19:   */   {
/* 20:89 */     return this.value;
/* 21:   */   }
/* 22:   */   
/* 23:   */   public static TIMEANCHOR2 fromValue(String v)
/* 24:   */   {
/* 25:93 */     for (TIMEANCHOR2 c : ) {
/* 26:94 */       if (c.value.equals(v)) {
/* 27:95 */         return c;
/* 28:   */       }
/* 29:   */     }
/* 30:98 */     throw new IllegalArgumentException(v.toString());
/* 31:   */   }
/* 32:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.TIMEANCHOR2
 * JD-Core Version:    0.7.0.1
 */