/*   1:    */ package org.boon.validation.readers;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Arrays;
/*   6:    */ import java.util.HashMap;
/*   7:    */ import java.util.List;
/*   8:    */ import java.util.Map;
/*   9:    */ import java.util.Properties;
/*  10:    */ import org.boon.validation.ValidatorMetaData;
/*  11:    */ import org.boon.validation.ValidatorMetaDataReader;
/*  12:    */ 
/*  13:    */ public class PropertiesFileValidatorMetaDataReader
/*  14:    */   implements ValidatorMetaDataReader
/*  15:    */ {
/*  16: 81 */   private Map<String, Properties> metaDataPropsCache = new HashMap();
/*  17: 87 */   private Map<String, List<ValidatorMetaData>> metaDataCache = new HashMap();
/*  18:    */   
/*  19:    */   public List<ValidatorMetaData> readMetaData(Class<?> clazz, String propertyName)
/*  20:    */   {
/*  21: 96 */     Properties props = loadMetaDataPropsFile(clazz);
/*  22:    */     
/*  23: 98 */     String unparsedString = props.getProperty(propertyName);
/*  24:    */     
/*  25:100 */     return extractMetaDataFromString(clazz, propertyName, unparsedString);
/*  26:    */   }
/*  27:    */   
/*  28:    */   private Properties loadMetaDataPropsFile(Class<?> clazzWhoseValidationMetaDataWeAreReading)
/*  29:    */   {
/*  30:113 */     String className = clazzWhoseValidationMetaDataWeAreReading.getName();
/*  31:    */     
/*  32:    */ 
/*  33:    */ 
/*  34:    */ 
/*  35:    */ 
/*  36:119 */     className = className.split("[$]")[0];
/*  37:    */     
/*  38:    */ 
/*  39:    */ 
/*  40:    */ 
/*  41:    */ 
/*  42:125 */     String[] sourceParts = className.split("[.]");
/*  43:126 */     String resourceName = sourceParts[(sourceParts.length - 1)] + ".properties";
/*  44:    */     
/*  45:    */ 
/*  46:129 */     Properties validationMetaDataProps = (Properties)this.metaDataPropsCache.get(resourceName);
/*  47:132 */     if (validationMetaDataProps == null)
/*  48:    */     {
/*  49:133 */       validationMetaDataProps = new Properties();
/*  50:    */       try
/*  51:    */       {
/*  52:139 */         validationMetaDataProps.load(getClass().getClassLoader().getResourceAsStream(resourceName));
/*  53:    */       }
/*  54:    */       catch (IOException ioex) {}
/*  55:153 */       this.metaDataPropsCache.put(resourceName, validationMetaDataProps);
/*  56:    */     }
/*  57:156 */     assert (validationMetaDataProps != null) : "Properties for validation meta-data were loaded";
/*  58:157 */     return validationMetaDataProps;
/*  59:    */   }
/*  60:    */   
/*  61:    */   private List<ValidatorMetaData> extractMetaDataFromString(Class<?> clazz, String propertyName, String unparsedString)
/*  62:    */   {
/*  63:170 */     String propertyKey = clazz.getName() + "." + propertyName;
/*  64:    */     
/*  65:    */ 
/*  66:173 */     List<ValidatorMetaData> validatorMetaDataList = (List)this.metaDataCache.get(propertyKey);
/*  67:178 */     if (validatorMetaDataList == null)
/*  68:    */     {
/*  69:180 */       validatorMetaDataList = new ArrayList();
/*  70:    */       
/*  71:    */ 
/*  72:    */ 
/*  73:    */ 
/*  74:    */ 
/*  75:186 */       String[] validatorsParts = unparsedString.split("[;]");
/*  76:    */       ValidatorMetaData validatorMetaData;
/*  77:193 */       for (String validatorString : validatorsParts)
/*  78:    */       {
/*  79:194 */         validatorMetaData = new ValidatorMetaData();
/*  80:195 */         validatorMetaDataList.add(validatorMetaData);
/*  81:    */         
/*  82:    */ 
/*  83:    */ 
/*  84:    */ 
/*  85:    */ 
/*  86:201 */         String[] parts = validatorString.trim().split("[ ,]");
/*  87:    */         
/*  88:    */ 
/*  89:    */ 
/*  90:    */ 
/*  91:    */ 
/*  92:207 */         validatorMetaData.setName(parts[0]);
/*  93:214 */         if (parts.length > 1)
/*  94:    */         {
/*  95:224 */           List<String> values = Arrays.asList(parts).subList(1, parts.length);
/*  96:228 */           for (String value : values) {
/*  97:230 */             if (value.indexOf("=") != -1)
/*  98:    */             {
/*  99:232 */               String[] valueParts = value.split("[=]");
/* 100:    */               
/* 101:    */ 
/* 102:    */ 
/* 103:236 */               validatorMetaData.getProperties().put(valueParts[0], valueParts[1]);
/* 104:    */             }
/* 105:    */           }
/* 106:    */         }
/* 107:    */       }
/* 108:242 */       this.metaDataCache.put(propertyKey, validatorMetaDataList);
/* 109:    */     }
/* 110:244 */     return validatorMetaDataList;
/* 111:    */   }
/* 112:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.validation.readers.PropertiesFileValidatorMetaDataReader
 * JD-Core Version:    0.7.0.1
 */