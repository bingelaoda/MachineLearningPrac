/*  1:   */ package org.boon.validation.readers;
/*  2:   */ 
/*  3:   */ import java.util.ArrayList;
/*  4:   */ import java.util.LinkedHashMap;
/*  5:   */ import java.util.List;
/*  6:   */ import java.util.Map;
/*  7:   */ import org.boon.validation.ValidatorMetaData;
/*  8:   */ import org.boon.validation.ValidatorMetaDataReader;
/*  9:   */ 
/* 10:   */ public class ChainValidatorMetaDataReader
/* 11:   */   implements ValidatorMetaDataReader
/* 12:   */ {
/* 13:   */   public static final String OVERRIDE_NAME = "validator.override.name";
/* 14:   */   private List<ValidatorMetaDataReader> chain;
/* 15:   */   
/* 16:   */   public void setChain(List<ValidatorMetaDataReader> chain)
/* 17:   */   {
/* 18:61 */     this.chain = chain;
/* 19:   */   }
/* 20:   */   
/* 21:   */   public List<ValidatorMetaData> readMetaData(Class<?> clazz, String propertyName)
/* 22:   */   {
/* 23:65 */     Map<String, ValidatorMetaData> overrideMap = new LinkedHashMap();
/* 24:71 */     for (ValidatorMetaDataReader reader : this.chain)
/* 25:   */     {
/* 26:73 */       List<ValidatorMetaData> list = reader.readMetaData(clazz, propertyName);
/* 27:78 */       for (ValidatorMetaData data : list)
/* 28:   */       {
/* 29:79 */         String overrideName = data.getName();
/* 30:80 */         if ((data.getProperties() != null) && (data.getProperties().get("validator.override.name") != null))
/* 31:   */         {
/* 32:81 */           overrideName = (String)data.getProperties().get("validator.override.name");
/* 33:82 */           data.getProperties().remove("validator.override.name");
/* 34:   */         }
/* 35:84 */         overrideMap.put(overrideName, data);
/* 36:   */       }
/* 37:   */     }
/* 38:88 */     return new ArrayList(overrideMap.values());
/* 39:   */   }
/* 40:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.validation.readers.ChainValidatorMetaDataReader
 * JD-Core Version:    0.7.0.1
 */