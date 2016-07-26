/*  1:   */ package org.boon.validation;
/*  2:   */ 
/*  3:   */ import java.util.HashMap;
/*  4:   */ import java.util.Map;
/*  5:   */ 
/*  6:   */ public class ValidatorMetaData
/*  7:   */ {
/*  8:42 */   private String name = null;
/*  9:44 */   private Map<String, Object> properties = new HashMap();
/* 10:   */   
/* 11:   */   public static ValidatorMetaData validatorMeta(String name, Map<String, Object> properties)
/* 12:   */   {
/* 13:48 */     return new ValidatorMetaData(name, properties);
/* 14:   */   }
/* 15:   */   
/* 16:   */   public ValidatorMetaData() {}
/* 17:   */   
/* 18:   */   public ValidatorMetaData(String name, Map<String, Object> properties)
/* 19:   */   {
/* 20:55 */     this.name = name;
/* 21:56 */     this.properties = properties;
/* 22:   */   }
/* 23:   */   
/* 24:   */   public void setName(String name)
/* 25:   */   {
/* 26:60 */     this.name = name;
/* 27:   */   }
/* 28:   */   
/* 29:   */   public String getName()
/* 30:   */   {
/* 31:64 */     return this.name;
/* 32:   */   }
/* 33:   */   
/* 34:   */   public void setProperties(Map<String, Object> properties)
/* 35:   */   {
/* 36:68 */     this.properties = properties;
/* 37:   */   }
/* 38:   */   
/* 39:   */   public Map<String, Object> getProperties()
/* 40:   */   {
/* 41:72 */     return this.properties;
/* 42:   */   }
/* 43:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.validation.ValidatorMetaData
 * JD-Core Version:    0.7.0.1
 */