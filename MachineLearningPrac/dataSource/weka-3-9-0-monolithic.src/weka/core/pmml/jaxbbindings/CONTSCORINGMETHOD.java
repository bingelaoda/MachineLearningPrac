/*  1:   */ package weka.core.pmml.jaxbbindings;
/*  2:   */ 
/*  3:   */ import javax.xml.bind.annotation.XmlEnum;
/*  4:   */ import javax.xml.bind.annotation.XmlEnumValue;
/*  5:   */ 
/*  6:   */ @XmlEnum
/*  7:   */ public enum CONTSCORINGMETHOD
/*  8:   */ {
/*  9:34 */   AVERAGE("average"),  MEDIAN("median"),  WEIGHTED_AVERAGE("weightedAverage");
/* 10:   */   
/* 11:   */   private final String value;
/* 12:   */   
/* 13:   */   private CONTSCORINGMETHOD(String v)
/* 14:   */   {
/* 15:43 */     this.value = v;
/* 16:   */   }
/* 17:   */   
/* 18:   */   public String value()
/* 19:   */   {
/* 20:47 */     return this.value;
/* 21:   */   }
/* 22:   */   
/* 23:   */   public static CONTSCORINGMETHOD fromValue(String v)
/* 24:   */   {
/* 25:51 */     for (CONTSCORINGMETHOD c : ) {
/* 26:52 */       if (c.value.equals(v)) {
/* 27:53 */         return c;
/* 28:   */       }
/* 29:   */     }
/* 30:56 */     throw new IllegalArgumentException(v.toString());
/* 31:   */   }
/* 32:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.CONTSCORINGMETHOD
 * JD-Core Version:    0.7.0.1
 */