/*  1:   */ package weka.core.pmml.jaxbbindings;
/*  2:   */ 
/*  3:   */ import javax.xml.bind.annotation.XmlEnum;
/*  4:   */ import javax.xml.bind.annotation.XmlEnumValue;
/*  5:   */ 
/*  6:   */ @XmlEnum
/*  7:   */ public enum RESULTFEATURE
/*  8:   */ {
/*  9:46 */   AFFINITY("affinity"),  CLUSTER_AFFINITY("clusterAffinity"),  CLUSTER_ID("clusterId"),  DECISION("decision"),  ENTITY_AFFINITY("entityAffinity"),  ENTITY_ID("entityId"),  PREDICTED_DISPLAY_VALUE("predictedDisplayValue"),  PREDICTED_VALUE("predictedValue"),  PROBABILITY("probability"),  REASON_CODE("reasonCode"),  RESIDUAL("residual"),  RULE_VALUE("ruleValue"),  STANDARD_ERROR("standardError"),  TRANSFORMED_VALUE("transformedValue"),  WARNING("warning");
/* 10:   */   
/* 11:   */   private final String value;
/* 12:   */   
/* 13:   */   private RESULTFEATURE(String v)
/* 14:   */   {
/* 15:79 */     this.value = v;
/* 16:   */   }
/* 17:   */   
/* 18:   */   public String value()
/* 19:   */   {
/* 20:83 */     return this.value;
/* 21:   */   }
/* 22:   */   
/* 23:   */   public static RESULTFEATURE fromValue(String v)
/* 24:   */   {
/* 25:87 */     for (RESULTFEATURE c : ) {
/* 26:88 */       if (c.value.equals(v)) {
/* 27:89 */         return c;
/* 28:   */       }
/* 29:   */     }
/* 30:92 */     throw new IllegalArgumentException(v.toString());
/* 31:   */   }
/* 32:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.RESULTFEATURE
 * JD-Core Version:    0.7.0.1
 */