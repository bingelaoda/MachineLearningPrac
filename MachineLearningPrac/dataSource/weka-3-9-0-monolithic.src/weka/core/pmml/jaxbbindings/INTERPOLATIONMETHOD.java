/*  1:   */ package weka.core.pmml.jaxbbindings;
/*  2:   */ 
/*  3:   */ import javax.xml.bind.annotation.XmlEnum;
/*  4:   */ import javax.xml.bind.annotation.XmlEnumValue;
/*  5:   */ 
/*  6:   */ @XmlEnum
/*  7:   */ public enum INTERPOLATIONMETHOD
/*  8:   */ {
/*  9:35 */   CUBIC_SPLINE("cubicSpline"),  EXPONENTIAL_SPLINE("exponentialSpline"),  LINEAR("linear"),  NONE("none");
/* 10:   */   
/* 11:   */   private final String value;
/* 12:   */   
/* 13:   */   private INTERPOLATIONMETHOD(String v)
/* 14:   */   {
/* 15:46 */     this.value = v;
/* 16:   */   }
/* 17:   */   
/* 18:   */   public String value()
/* 19:   */   {
/* 20:50 */     return this.value;
/* 21:   */   }
/* 22:   */   
/* 23:   */   public static INTERPOLATIONMETHOD fromValue(String v)
/* 24:   */   {
/* 25:54 */     for (INTERPOLATIONMETHOD c : ) {
/* 26:55 */       if (c.value.equals(v)) {
/* 27:56 */         return c;
/* 28:   */       }
/* 29:   */     }
/* 30:59 */     throw new IllegalArgumentException(v.toString());
/* 31:   */   }
/* 32:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.INTERPOLATIONMETHOD
 * JD-Core Version:    0.7.0.1
 */