/*  1:   */ package weka.core.pmml.jaxbbindings;
/*  2:   */ 
/*  3:   */ import javax.xml.bind.annotation.XmlEnum;
/*  4:   */ import javax.xml.bind.annotation.XmlEnumValue;
/*  5:   */ 
/*  6:   */ @XmlEnum
/*  7:   */ public enum TIMEEXCEPTIONTYPE
/*  8:   */ {
/*  9:33 */   EXCLUDE("exclude"),  INCLUDE("include");
/* 10:   */   
/* 11:   */   private final String value;
/* 12:   */   
/* 13:   */   private TIMEEXCEPTIONTYPE(String v)
/* 14:   */   {
/* 15:40 */     this.value = v;
/* 16:   */   }
/* 17:   */   
/* 18:   */   public String value()
/* 19:   */   {
/* 20:44 */     return this.value;
/* 21:   */   }
/* 22:   */   
/* 23:   */   public static TIMEEXCEPTIONTYPE fromValue(String v)
/* 24:   */   {
/* 25:48 */     for (TIMEEXCEPTIONTYPE c : ) {
/* 26:49 */       if (c.value.equals(v)) {
/* 27:50 */         return c;
/* 28:   */       }
/* 29:   */     }
/* 30:53 */     throw new IllegalArgumentException(v.toString());
/* 31:   */   }
/* 32:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.TIMEEXCEPTIONTYPE
 * JD-Core Version:    0.7.0.1
 */