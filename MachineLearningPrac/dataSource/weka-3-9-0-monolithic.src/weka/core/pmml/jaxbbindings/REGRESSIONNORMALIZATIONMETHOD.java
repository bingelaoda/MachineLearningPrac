/*  1:   */ package weka.core.pmml.jaxbbindings;
/*  2:   */ 
/*  3:   */ import javax.xml.bind.annotation.XmlEnum;
/*  4:   */ import javax.xml.bind.annotation.XmlEnumValue;
/*  5:   */ 
/*  6:   */ @XmlEnum
/*  7:   */ public enum REGRESSIONNORMALIZATIONMETHOD
/*  8:   */ {
/*  9:40 */   CAUCHIT("cauchit"),  CLOGLOG("cloglog"),  EXP("exp"),  LOGIT("logit"),  LOGLOG("loglog"),  NONE("none"),  PROBIT("probit"),  SIMPLEMAX("simplemax"),  SOFTMAX("softmax");
/* 10:   */   
/* 11:   */   private final String value;
/* 12:   */   
/* 13:   */   private REGRESSIONNORMALIZATIONMETHOD(String v)
/* 14:   */   {
/* 15:61 */     this.value = v;
/* 16:   */   }
/* 17:   */   
/* 18:   */   public String value()
/* 19:   */   {
/* 20:65 */     return this.value;
/* 21:   */   }
/* 22:   */   
/* 23:   */   public static REGRESSIONNORMALIZATIONMETHOD fromValue(String v)
/* 24:   */   {
/* 25:69 */     for (REGRESSIONNORMALIZATIONMETHOD c : ) {
/* 26:70 */       if (c.value.equals(v)) {
/* 27:71 */         return c;
/* 28:   */       }
/* 29:   */     }
/* 30:74 */     throw new IllegalArgumentException(v.toString());
/* 31:   */   }
/* 32:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.REGRESSIONNORMALIZATIONMETHOD
 * JD-Core Version:    0.7.0.1
 */