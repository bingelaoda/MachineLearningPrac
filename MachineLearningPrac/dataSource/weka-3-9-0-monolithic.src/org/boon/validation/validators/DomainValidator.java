/*   1:    */ package org.boon.validation.validators;
/*   2:    */ 
/*   3:    */ import java.lang.reflect.InvocationTargetException;
/*   4:    */ import java.lang.reflect.Method;
/*   5:    */ import java.util.HashSet;
/*   6:    */ import java.util.List;
/*   7:    */ import java.util.Map;
/*   8:    */ import java.util.Set;
/*   9:    */ import org.boon.Maps;
/*  10:    */ import org.boon.core.reflection.AnnotationData;
/*  11:    */ import org.boon.core.reflection.Annotations;
/*  12:    */ import org.boon.core.reflection.BeanUtils;
/*  13:    */ import org.boon.validation.ValidationContext;
/*  14:    */ import org.boon.validation.ValidatorMessage;
/*  15:    */ import org.boon.validation.ValidatorMessageHolder;
/*  16:    */ 
/*  17:    */ public class DomainValidator
/*  18:    */   extends BaseValidator
/*  19:    */ {
/*  20:    */   private static final long serialVersionUID = 1L;
/*  21:    */   private Object rootObject;
/*  22:    */   
/*  23:    */   public void setRootObject(Object rootObject)
/*  24:    */   {
/*  25: 70 */     this.rootObject = rootObject;
/*  26:    */   }
/*  27:    */   
/*  28: 73 */   private static Set<String> allowedPackages = new HashSet();
/*  29:    */   
/*  30:    */   static
/*  31:    */   {
/*  32: 76 */     allowedPackages.add("org.boon.annotation.validation");
/*  33:    */   }
/*  34:    */   
/*  35:    */   public ValidatorMessageHolder validate(Object fieldValue, String fieldLabel)
/*  36:    */   {
/*  37: 88 */     String detailMessage = "";
/*  38: 89 */     String summaryMessage = "";
/*  39: 90 */     this.noMessages = true;
/*  40: 91 */     boolean error = false;
/*  41:    */     Object child;
/*  42:    */     Object child;
/*  43: 95 */     if (this.rootObject == null) {
/*  44: 97 */       child = ValidationContext.getCurrentInstance().getParentObject();
/*  45:    */     } else {
/*  46:100 */       child = this.rootObject;
/*  47:    */     }
/*  48:104 */     List<AnnotationData> annotationDataForProperty = Annotations.getAnnotationDataForProperty(child.getClass(), fieldLabel, false, allowedPackages);
/*  49:105 */     if (annotationDataForProperty.size() == 0) {
/*  50:106 */       annotationDataForProperty = Annotations.getAnnotationDataForField(child.getClass(), fieldLabel, allowedPackages);
/*  51:    */     }
/*  52:109 */     Map map = Maps.toMap("name", annotationDataForProperty);
/*  53:    */     
/*  54:111 */     boolean found = map.get("domainValidation") != null;
/*  55:112 */     boolean sameLevel = false;
/*  56:113 */     boolean noArgs = false;
/*  57:115 */     if (found)
/*  58:    */     {
/*  59:116 */       AnnotationData ad = (AnnotationData)map.get("domainValidation");
/*  60:117 */       Object parentReference = ad.getValues().get("parentProperty");
/*  61:118 */       Object validator = null;
/*  62:120 */       if ((parentReference != null) && (!"".equals(parentReference)))
/*  63:    */       {
/*  64:123 */         BeanUtils.getPropertyValue(child, new String[] { (String)parentReference });
/*  65:    */       }
/*  66:    */       else
/*  67:    */       {
/*  68:127 */         validator = child;
/*  69:128 */         sameLevel = true;
/*  70:    */       }
/*  71:131 */       noArgs = ((Boolean)ad.getValues().get("global")).booleanValue();
/*  72:    */       
/*  73:    */ 
/*  74:134 */       Method m = null;
/*  75:136 */       if (validator != null) {
/*  76:    */         try
/*  77:    */         {
/*  78:139 */           String methodName = (String)ad.getValues().get("method");
/*  79:141 */           if (noArgs)
/*  80:    */           {
/*  81:142 */             m = validator.getClass().getDeclaredMethod(methodName, new Class[0]);
/*  82:    */           }
/*  83:143 */           else if (sameLevel)
/*  84:    */           {
/*  85:144 */             Class[] parameters = new Class[1];
/*  86:    */             
/*  87:146 */             parameters[0] = BeanUtils.getPropertyType(child, fieldLabel);
/*  88:147 */             m = validator.getClass().getDeclaredMethod(methodName, parameters);
/*  89:    */           }
/*  90:    */           else
/*  91:    */           {
/*  92:149 */             Class[] parameters = new Class[2];
/*  93:150 */             parameters[0] = child.getClass();
/*  94:    */             
/*  95:    */ 
/*  96:153 */             parameters[1] = BeanUtils.getPropertyType(child, fieldLabel);
/*  97:    */             
/*  98:155 */             m = validator.getClass().getDeclaredMethod(methodName, parameters);
/*  99:    */           }
/* 100:    */         }
/* 101:    */         catch (NoSuchMethodException nsme)
/* 102:    */         {
/* 103:159 */           detailMessage = nsme.getMessage();
/* 104:160 */           summaryMessage = nsme.getMessage();
/* 105:161 */           error = true;
/* 106:    */         }
/* 107:    */         catch (Exception e)
/* 108:    */         {
/* 109:163 */           detailMessage = e.getMessage();
/* 110:164 */           summaryMessage = e.getMessage();
/* 111:165 */           error = true;
/* 112:    */         }
/* 113:    */       }
/* 114:    */       try
/* 115:    */       {
/* 116:171 */         if (noArgs) {
/* 117:172 */           m.invoke(validator, new Object[0]);
/* 118:173 */         } else if (sameLevel) {
/* 119:174 */           m.invoke(validator, new Object[] { fieldValue });
/* 120:    */         } else {
/* 121:176 */           m.invoke(validator, new Object[] { child, fieldValue });
/* 122:    */         }
/* 123:    */       }
/* 124:    */       catch (IllegalAccessException iae)
/* 125:    */       {
/* 126:179 */         detailMessage = iae.getCause().getMessage();
/* 127:180 */         summaryMessage = iae.getCause().getMessage();
/* 128:181 */         error = true;
/* 129:    */       }
/* 130:    */       catch (InvocationTargetException ite)
/* 131:    */       {
/* 132:183 */         detailMessage = ite.getCause().getMessage();
/* 133:184 */         summaryMessage = ite.getCause().getMessage();
/* 134:185 */         error = true;
/* 135:    */       }
/* 136:    */       catch (Exception e)
/* 137:    */       {
/* 138:187 */         detailMessage = e.getCause().getMessage();
/* 139:188 */         summaryMessage = e.getCause().getMessage();
/* 140:189 */         error = true;
/* 141:    */       }
/* 142:    */     }
/* 143:193 */     ValidatorMessage message = new ValidatorMessage();
/* 144:196 */     if (error)
/* 145:    */     {
/* 146:197 */       message = new ValidatorMessage(summaryMessage, detailMessage);
/* 147:198 */       populateMessage(message, noArgs ? null : fieldLabel, new Object[0]);
/* 148:    */     }
/* 149:201 */     return message;
/* 150:    */   }
/* 151:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.validation.validators.DomainValidator
 * JD-Core Version:    0.7.0.1
 */