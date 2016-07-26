/*   1:    */ package org.boon.validation;
/*   2:    */ 
/*   3:    */ import java.beans.BeanInfo;
/*   4:    */ import java.beans.IntrospectionException;
/*   5:    */ import java.beans.Introspector;
/*   6:    */ import java.beans.PropertyDescriptor;
/*   7:    */ import java.util.ArrayList;
/*   8:    */ import java.util.Collections;
/*   9:    */ import java.util.List;
/*  10:    */ import java.util.Map;
/*  11:    */ import org.boon.Exceptions;
/*  12:    */ import org.boon.Maps;
/*  13:    */ import org.boon.core.Typ;
/*  14:    */ import org.boon.core.reflection.BeanUtils;
/*  15:    */ import org.boon.validation.readers.AnnotationValidatorMetaDataReader;
/*  16:    */ import org.boon.validation.validators.CompositeValidator;
/*  17:    */ 
/*  18:    */ public class RecursiveDescentPropertyValidator
/*  19:    */ {
/*  20:    */   protected ValidatorMetaDataReader validatorMetaDataReader;
/*  21:    */   
/*  22:    */   public RecursiveDescentPropertyValidator()
/*  23:    */   {
/*  24: 48 */     this.validatorMetaDataReader = new AnnotationValidatorMetaDataReader();
/*  25:    */   }
/*  26:    */   
/*  27:    */   public class MessageHolder
/*  28:    */   {
/*  29:    */     public final String propertyPath;
/*  30:    */     public final ValidatorMessageHolder holder;
/*  31:    */     
/*  32:    */     MessageHolder(String propertyPath, ValidatorMessageHolder holder)
/*  33:    */     {
/*  34: 55 */       this.propertyPath = propertyPath;
/*  35: 56 */       this.holder = holder;
/*  36:    */     }
/*  37:    */   }
/*  38:    */   
/*  39:    */   protected CompositeValidator createValidator(List<ValidatorMetaData> validationMetaDataList)
/*  40:    */   {
/*  41: 76 */     CompositeValidator compositeValidator = new CompositeValidator();
/*  42:    */     
/*  43:    */ 
/*  44:    */ 
/*  45:    */ 
/*  46:    */ 
/*  47:    */ 
/*  48:    */ 
/*  49:    */ 
/*  50:    */ 
/*  51:    */ 
/*  52:    */ 
/*  53:    */ 
/*  54:    */ 
/*  55: 90 */     List<FieldValidator> validatorsList = lookupTheListOfValidatorsAndInitializeThemWithMetaDataProperties(validationMetaDataList);
/*  56:    */     
/*  57:    */ 
/*  58: 93 */     compositeValidator.setValidatorList(validatorsList);
/*  59:    */     
/*  60: 95 */     return compositeValidator;
/*  61:    */   }
/*  62:    */   
/*  63:    */   private List<PropertyDescriptor> getFieldsToValidate(Object object)
/*  64:    */   {
/*  65:    */     BeanInfo beanInfo;
/*  66:    */     try
/*  67:    */     {
/*  68:103 */       beanInfo = Introspector.getBeanInfo(object.getClass());
/*  69:    */     }
/*  70:    */     catch (IntrospectionException e)
/*  71:    */     {
/*  72:106 */       throw new RuntimeException(e);
/*  73:    */     }
/*  74:108 */     PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
/*  75:109 */     List<PropertyDescriptor> properties = new ArrayList(propertyDescriptors.length);
/*  76:110 */     for (PropertyDescriptor pd : propertyDescriptors) {
/*  77:111 */       if (!pd.getName().equals("class")) {
/*  78:112 */         properties.add(pd);
/*  79:    */       }
/*  80:    */     }
/*  81:115 */     return properties;
/*  82:    */   }
/*  83:    */   
/*  84:    */   protected List<ValidatorMetaData> readMetaData(Class<?> clazz, String propertyName)
/*  85:    */   {
/*  86:120 */     return this.validatorMetaDataReader.readMetaData(clazz, propertyName);
/*  87:    */   }
/*  88:    */   
/*  89:    */   private void validateProperty(Object object, Object objectProperty, String property, List<MessageHolder> vMessageHolders)
/*  90:    */   {
/*  91:127 */     List<ValidatorMetaData> metaDataList = readMetaData(object.getClass(), property);
/*  92:    */     
/*  93:129 */     CompositeValidator cv = createValidator(metaDataList);
/*  94:130 */     ValidatorMessageHolder holder = cv.validate(objectProperty, property);
/*  95:131 */     vMessageHolders.add(new MessageHolder(ValidationContext.getBindingPath(), holder));
/*  96:    */   }
/*  97:    */   
/*  98:    */   protected boolean shouldFieldBeValidated()
/*  99:    */   {
/* 100:135 */     return true;
/* 101:    */   }
/* 102:    */   
/* 103:    */   public List<MessageHolder> validateObject(Object object)
/* 104:    */   {
/* 105:142 */     List<MessageHolder> list = Collections.EMPTY_LIST;
/* 106:    */     try
/* 107:    */     {
/* 108:144 */       ValidationContext.create();
/* 109:145 */       list = validateObjectWithMessages(object, null);
/* 110:    */     }
/* 111:    */     finally
/* 112:    */     {
/* 113:147 */       ValidationContext.destroy();
/* 114:    */     }
/* 115:149 */     return list;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public List<MessageHolder> validateObject(Object object, Map<String, Object> registry)
/* 119:    */   {
/* 120:155 */     List<MessageHolder> list = Collections.EMPTY_LIST;
/* 121:    */     try
/* 122:    */     {
/* 123:157 */       ValidationContext.create();
/* 124:158 */       ValidationContext.get().setObjectRegistry(registry);
/* 125:159 */       list = validateObjectWithMessages(object, null);
/* 126:    */     }
/* 127:    */     finally
/* 128:    */     {
/* 129:161 */       ValidationContext.destroy();
/* 130:    */     }
/* 131:163 */     return list;
/* 132:    */   }
/* 133:    */   
/* 134:    */   public List<MessageHolder> validateObjectWithMessages(Object object, List<MessageHolder> validationMessages)
/* 135:    */   {
/* 136:168 */     List<PropertyDescriptor> fieldsToValidate = getFieldsToValidate(object);
/* 137:169 */     Map<String, Object> objectPropertiesAsMap = Maps.toMap(object);
/* 138:170 */     if (validationMessages == null) {
/* 139:171 */       validationMessages = new ArrayList();
/* 140:    */     }
/* 141:174 */     for (PropertyDescriptor field : fieldsToValidate)
/* 142:    */     {
/* 143:177 */       ValidationContext.get().pushProperty(field.getName());
/* 144:178 */       ValidationContext.get().setParentObject(object);
/* 145:179 */       if (shouldFieldBeValidated())
/* 146:    */       {
/* 147:180 */         Object propertyObject = objectPropertiesAsMap.get(field.getName());
/* 148:181 */         validateProperty(object, propertyObject, field.getName(), validationMessages);
/* 149:184 */         if ((propertyObject != null) && (!Typ.isBasicType(propertyObject))) {
/* 150:185 */           validateObjectWithMessages(propertyObject, validationMessages);
/* 151:    */         }
/* 152:    */       }
/* 153:188 */       ValidationContext.get().pop();
/* 154:    */     }
/* 155:191 */     return validationMessages;
/* 156:    */   }
/* 157:    */   
/* 158:    */   private List<FieldValidator> lookupTheListOfValidatorsAndInitializeThemWithMetaDataProperties(List<ValidatorMetaData> validationMetaDataList)
/* 159:    */   {
/* 160:207 */     List<FieldValidator> validatorsList = new ArrayList();
/* 161:213 */     for (ValidatorMetaData validationMetaData : validationMetaDataList)
/* 162:    */     {
/* 163:215 */       FieldValidator validator = lookupValidatorInRegistry(validationMetaData.getName());
/* 164:    */       
/* 165:    */ 
/* 166:    */ 
/* 167:    */ 
/* 168:    */ 
/* 169:    */ 
/* 170:222 */       applyValidationMetaDataPropertiesToValidator(validationMetaData, validator);
/* 171:    */       
/* 172:224 */       validatorsList.add(validator);
/* 173:    */     }
/* 174:226 */     return validatorsList;
/* 175:    */   }
/* 176:    */   
/* 177:    */   private FieldValidator lookupValidatorInRegistry(String validationMetaDataName)
/* 178:    */   {
/* 179:239 */     Map<String, Object> applicationContext = ValidationContext.get().getObjectRegistry();
/* 180:    */     
/* 181:241 */     Exceptions.requireNonNull(applicationContext);
/* 182:    */     
/* 183:243 */     return (FieldValidator)applicationContext.get("/org/boon/validator/" + validationMetaDataName);
/* 184:    */   }
/* 185:    */   
/* 186:    */   private void applyValidationMetaDataPropertiesToValidator(ValidatorMetaData metaData, FieldValidator validator)
/* 187:    */   {
/* 188:256 */     Map<String, Object> properties = metaData.getProperties();
/* 189:257 */     ifPropertyBlankRemove(properties, "detailMessage");
/* 190:258 */     ifPropertyBlankRemove(properties, "summaryMessage");
/* 191:    */     
/* 192:260 */     BeanUtils.copyProperties(validator, properties);
/* 193:    */   }
/* 194:    */   
/* 195:    */   private void ifPropertyBlankRemove(Map<String, Object> properties, String property)
/* 196:    */   {
/* 197:275 */     Object object = properties.get(property);
/* 198:276 */     if (object == null)
/* 199:    */     {
/* 200:277 */       properties.remove(property);
/* 201:    */     }
/* 202:278 */     else if ((object instanceof String))
/* 203:    */     {
/* 204:279 */       String string = (String)object;
/* 205:280 */       if ("".equals(string.trim())) {
/* 206:281 */         properties.remove(property);
/* 207:    */       }
/* 208:    */     }
/* 209:    */   }
/* 210:    */   
/* 211:    */   public void setValidatorMetaDataReader(ValidatorMetaDataReader validatorMetaDataReader)
/* 212:    */   {
/* 213:288 */     this.validatorMetaDataReader = validatorMetaDataReader;
/* 214:    */   }
/* 215:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.validation.RecursiveDescentPropertyValidator
 * JD-Core Version:    0.7.0.1
 */