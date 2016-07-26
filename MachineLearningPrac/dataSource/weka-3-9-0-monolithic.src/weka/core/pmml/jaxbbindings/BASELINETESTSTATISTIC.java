/*  1:   */ package weka.core.pmml.jaxbbindings;
/*  2:   */ 
/*  3:   */ import javax.xml.bind.annotation.XmlEnum;
/*  4:   */ import javax.xml.bind.annotation.XmlEnumValue;
/*  5:   */ 
/*  6:   */ @XmlEnum
/*  7:   */ public enum BASELINETESTSTATISTIC
/*  8:   */ {
/*  9:36 */   CHI_SQUARE_DISTRIBUTION("chiSquareDistribution"),  CHI_SQUARE_INDEPENDENCE("chiSquareIndependence"),  CUSUM("CUSUM"),  SCALAR_PRODUCT("scalarProduct"),  Z_VALUE("zValue");
/* 10:   */   
/* 11:   */   private final String value;
/* 12:   */   
/* 13:   */   private BASELINETESTSTATISTIC(String v)
/* 14:   */   {
/* 15:48 */     this.value = v;
/* 16:   */   }
/* 17:   */   
/* 18:   */   public String value()
/* 19:   */   {
/* 20:52 */     return this.value;
/* 21:   */   }
/* 22:   */   
/* 23:   */   public static BASELINETESTSTATISTIC fromValue(String v)
/* 24:   */   {
/* 25:56 */     for (BASELINETESTSTATISTIC c : ) {
/* 26:57 */       if (c.value.equals(v)) {
/* 27:58 */         return c;
/* 28:   */       }
/* 29:   */     }
/* 30:61 */     throw new IllegalArgumentException(v.toString());
/* 31:   */   }
/* 32:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.BASELINETESTSTATISTIC
 * JD-Core Version:    0.7.0.1
 */