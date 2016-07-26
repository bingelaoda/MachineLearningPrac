/*  1:   */ package weka.core.pmml.jaxbbindings;
/*  2:   */ 
/*  3:   */ import javax.xml.bind.annotation.XmlEnum;
/*  4:   */ import javax.xml.bind.annotation.XmlEnumValue;
/*  5:   */ 
/*  6:   */ @XmlEnum
/*  7:   */ public enum ACTIVATIONFUNCTION
/*  8:   */ {
/*  9:44 */   ARCTAN("arctan"),  COSINE("cosine"),  ELLIOTT("Elliott"),  EXPONENTIAL("exponential"),  GAUSS("Gauss"),  IDENTITY("identity"),  LOGISTIC("logistic"),  RADIAL_BASIS("radialBasis"),  RECIPROCAL("reciprocal"),  SINE("sine"),  SQUARE("square"),  TANH("tanh"),  THRESHOLD("threshold");
/* 10:   */   
/* 11:   */   private final String value;
/* 12:   */   
/* 13:   */   private ACTIVATIONFUNCTION(String v)
/* 14:   */   {
/* 15:73 */     this.value = v;
/* 16:   */   }
/* 17:   */   
/* 18:   */   public String value()
/* 19:   */   {
/* 20:77 */     return this.value;
/* 21:   */   }
/* 22:   */   
/* 23:   */   public static ACTIVATIONFUNCTION fromValue(String v)
/* 24:   */   {
/* 25:81 */     for (ACTIVATIONFUNCTION c : ) {
/* 26:82 */       if (c.value.equals(v)) {
/* 27:83 */         return c;
/* 28:   */       }
/* 29:   */     }
/* 30:86 */     throw new IllegalArgumentException(v.toString());
/* 31:   */   }
/* 32:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.ACTIVATIONFUNCTION
 * JD-Core Version:    0.7.0.1
 */