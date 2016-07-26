/*   1:    */ package org.boon.validation.readers;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Collection;
/*   6:    */ import java.util.HashSet;
/*   7:    */ import java.util.List;
/*   8:    */ import java.util.Map;
/*   9:    */ import java.util.Set;
/*  10:    */ import java.util.concurrent.ConcurrentHashMap;
/*  11:    */ import org.boon.core.reflection.AnnotationData;
/*  12:    */ import org.boon.core.reflection.Annotations;
/*  13:    */ import org.boon.validation.ValidatorMetaData;
/*  14:    */ import org.boon.validation.ValidatorMetaDataReader;
/*  15:    */ 
/*  16:    */ public class AnnotationValidatorMetaDataReader
/*  17:    */   implements ValidatorMetaDataReader, Serializable
/*  18:    */ {
/*  19:    */   private Map<String, List<ValidatorMetaData>> metaDataCache;
/*  20:    */   private Set<String> validationAnnotationPackages;
/*  21:    */   
/*  22:    */   public AnnotationValidatorMetaDataReader()
/*  23:    */   {
/*  24: 94 */     this.metaDataCache = new ConcurrentHashMap();
/*  25:    */     
/*  26:    */ 
/*  27:    */ 
/*  28:    */ 
/*  29:    */ 
/*  30:    */ 
/*  31:101 */     this.validationAnnotationPackages = new HashSet();
/*  32:    */     
/*  33:    */ 
/*  34:    */ 
/*  35:105 */     this.validationAnnotationPackages.add("org.boon.validation.annotation");
/*  36:    */   }
/*  37:    */   
/*  38:    */   public List<ValidatorMetaData> readMetaData(Class<?> clazz, String propertyName)
/*  39:    */   {
/*  40:121 */     String propertyKey = clazz.getName() + "." + propertyName;
/*  41:    */     
/*  42:    */ 
/*  43:124 */     List<ValidatorMetaData> validatorMetaDataList = (List)this.metaDataCache.get(propertyKey);
/*  44:127 */     if (validatorMetaDataList == null)
/*  45:    */     {
/*  46:128 */       validatorMetaDataList = extractValidatorMetaData(clazz, propertyName, validatorMetaDataList);
/*  47:    */       
/*  48:    */ 
/*  49:    */ 
/*  50:    */ 
/*  51:133 */       this.metaDataCache.put(propertyKey, validatorMetaDataList);
/*  52:    */     }
/*  53:136 */     return validatorMetaDataList;
/*  54:    */   }
/*  55:    */   
/*  56:    */   private List<ValidatorMetaData> extractValidatorMetaData(Class<?> clazz, String propertyName, List<ValidatorMetaData> validatorMetaDataList)
/*  57:    */   {
/*  58:150 */     if (validatorMetaDataList == null)
/*  59:    */     {
/*  60:152 */       Collection<AnnotationData> annotations = Annotations.getAnnotationDataForFieldAndProperty(clazz, propertyName, this.validationAnnotationPackages);
/*  61:    */       
/*  62:    */ 
/*  63:155 */       validatorMetaDataList = extractMetaDataFromAnnotations(annotations);
/*  64:    */     }
/*  65:159 */     return validatorMetaDataList;
/*  66:    */   }
/*  67:    */   
/*  68:    */   private List<ValidatorMetaData> extractMetaDataFromAnnotations(Collection<AnnotationData> annotations)
/*  69:    */   {
/*  70:170 */     List<ValidatorMetaData> list = new ArrayList();
/*  71:172 */     for (AnnotationData annotationData : annotations)
/*  72:    */     {
/*  73:173 */       ValidatorMetaData validatorMetaData = convertAnnotationDataToValidatorMetaData(annotationData);
/*  74:174 */       list.add(validatorMetaData);
/*  75:    */     }
/*  76:177 */     return list;
/*  77:    */   }
/*  78:    */   
/*  79:    */   private ValidatorMetaData convertAnnotationDataToValidatorMetaData(AnnotationData annotationData)
/*  80:    */   {
/*  81:194 */     ValidatorMetaData metaData = new ValidatorMetaData();
/*  82:195 */     metaData.setName(annotationData.getName());
/*  83:    */     
/*  84:197 */     metaData.setProperties(annotationData.getValues());
/*  85:    */     
/*  86:199 */     return metaData;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void setValidationAnnotationPackages(Set<String> validationAnnotationPackages)
/*  90:    */   {
/*  91:208 */     this.validationAnnotationPackages = validationAnnotationPackages;
/*  92:    */   }
/*  93:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.validation.readers.AnnotationValidatorMetaDataReader
 * JD-Core Version:    0.7.0.1
 */