/*  1:   */ package weka.core.pmml.jaxbbindings;
/*  2:   */ 
/*  3:   */ import javax.xml.bind.annotation.XmlEnum;
/*  4:   */ import javax.xml.bind.annotation.XmlEnumValue;
/*  5:   */ 
/*  6:   */ @XmlEnum
/*  7:   */ public enum VALIDTIMESPEC
/*  8:   */ {
/*  9:36 */   EXCLUDE_FROM_TO("excludeFromTo"),  EXCLUDE_SET("excludeSet"),  INCLUDE_ALL("includeAll"),  INCLUDE_FROM_TO("includeFromTo"),  INCLUDE_SET("includeSet");
/* 10:   */   
/* 11:   */   private final String value;
/* 12:   */   
/* 13:   */   private VALIDTIMESPEC(String v)
/* 14:   */   {
/* 15:49 */     this.value = v;
/* 16:   */   }
/* 17:   */   
/* 18:   */   public String value()
/* 19:   */   {
/* 20:53 */     return this.value;
/* 21:   */   }
/* 22:   */   
/* 23:   */   public static VALIDTIMESPEC fromValue(String v)
/* 24:   */   {
/* 25:57 */     for (VALIDTIMESPEC c : ) {
/* 26:58 */       if (c.value.equals(v)) {
/* 27:59 */         return c;
/* 28:   */       }
/* 29:   */     }
/* 30:62 */     throw new IllegalArgumentException(v.toString());
/* 31:   */   }
/* 32:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.VALIDTIMESPEC
 * JD-Core Version:    0.7.0.1
 */