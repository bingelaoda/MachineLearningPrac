/*  1:   */ package weka.core.pmml.jaxbbindings;
/*  2:   */ 
/*  3:   */ import javax.xml.bind.annotation.XmlEnum;
/*  4:   */ import javax.xml.bind.annotation.XmlEnumValue;
/*  5:   */ 
/*  6:   */ @XmlEnum
/*  7:   */ public enum MULTIPLEMODELMETHOD
/*  8:   */ {
/*  9:41 */   AVERAGE("average"),  MAJORITY_VOTE("majorityVote"),  MAX("max"),  MEDIAN("median"),  MODEL_CHAIN("modelChain"),  SELECT_ALL("selectAll"),  SELECT_FIRST("selectFirst"),  SUM("sum"),  WEIGHTED_AVERAGE("weightedAverage"),  WEIGHTED_MAJORITY_VOTE("weightedMajorityVote");
/* 10:   */   
/* 11:   */   private final String value;
/* 12:   */   
/* 13:   */   private MULTIPLEMODELMETHOD(String v)
/* 14:   */   {
/* 15:64 */     this.value = v;
/* 16:   */   }
/* 17:   */   
/* 18:   */   public String value()
/* 19:   */   {
/* 20:68 */     return this.value;
/* 21:   */   }
/* 22:   */   
/* 23:   */   public static MULTIPLEMODELMETHOD fromValue(String v)
/* 24:   */   {
/* 25:72 */     for (MULTIPLEMODELMETHOD c : ) {
/* 26:73 */       if (c.value.equals(v)) {
/* 27:74 */         return c;
/* 28:   */       }
/* 29:   */     }
/* 30:77 */     throw new IllegalArgumentException(v.toString());
/* 31:   */   }
/* 32:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.MULTIPLEMODELMETHOD
 * JD-Core Version:    0.7.0.1
 */