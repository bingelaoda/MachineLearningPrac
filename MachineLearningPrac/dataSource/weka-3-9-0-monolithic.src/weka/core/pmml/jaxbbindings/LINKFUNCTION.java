/*  1:   */ package weka.core.pmml.jaxbbindings;
/*  2:   */ 
/*  3:   */ import javax.xml.bind.annotation.XmlEnum;
/*  4:   */ import javax.xml.bind.annotation.XmlEnumValue;
/*  5:   */ 
/*  6:   */ @XmlEnum
/*  7:   */ public enum LINKFUNCTION
/*  8:   */ {
/*  9:41 */   CLOGLOG("cloglog"),  IDENTITY("identity"),  LOG("log"),  LOGC("logc"),  LOGIT("logit"),  LOGLOG("loglog"),  NEGBIN("negbin"),  ODDSPOWER("oddspower"),  POWER("power"),  PROBIT("probit");
/* 10:   */   
/* 11:   */   private final String value;
/* 12:   */   
/* 13:   */   private LINKFUNCTION(String v)
/* 14:   */   {
/* 15:64 */     this.value = v;
/* 16:   */   }
/* 17:   */   
/* 18:   */   public String value()
/* 19:   */   {
/* 20:68 */     return this.value;
/* 21:   */   }
/* 22:   */   
/* 23:   */   public static LINKFUNCTION fromValue(String v)
/* 24:   */   {
/* 25:72 */     for (LINKFUNCTION c : ) {
/* 26:73 */       if (c.value.equals(v)) {
/* 27:74 */         return c;
/* 28:   */       }
/* 29:   */     }
/* 30:77 */     throw new IllegalArgumentException(v.toString());
/* 31:   */   }
/* 32:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.LINKFUNCTION
 * JD-Core Version:    0.7.0.1
 */