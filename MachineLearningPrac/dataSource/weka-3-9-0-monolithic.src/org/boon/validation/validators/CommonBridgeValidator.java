/*   1:    */ package org.boon.validation.validators;
/*   2:    */ 
/*   3:    */ import java.lang.reflect.Method;
/*   4:    */ import org.boon.validation.ValidatorMessage;
/*   5:    */ import org.boon.validation.ValidatorMessageHolder;
/*   6:    */ 
/*   7:    */ public class CommonBridgeValidator
/*   8:    */   extends BaseValidator
/*   9:    */ {
/*  10:    */   private Class<?> validatorClass;
/*  11: 51 */   private String methodName = "isValid";
/*  12: 52 */   private String factoryMethod = "getInstance";
/*  13:    */   private Object validator;
/*  14:    */   private Method validateMethod;
/*  15:    */   
/*  16:    */   public ValidatorMessageHolder validate(Object object, String fieldLabel)
/*  17:    */   {
/*  18: 57 */     ValidatorMessage message = new ValidatorMessage();
/*  19: 58 */     if (object == null) {
/*  20: 59 */       return message;
/*  21:    */     }
/*  22: 61 */     boolean valid = false;
/*  23:    */     try
/*  24:    */     {
/*  25: 63 */       initValidatorIfNeeded();
/*  26: 64 */       initValidateMethodIfNeeded();
/*  27: 65 */       valid = ((Boolean)this.validateMethod.invoke(this.validator, new Object[] { (String)object })).booleanValue();
/*  28:    */     }
/*  29:    */     catch (Exception exception)
/*  30:    */     {
/*  31: 68 */       throw new RuntimeException("Fatal exception trying to create validator, probably a missing jar or bad class name in spring context", exception);
/*  32:    */     }
/*  33: 72 */     if (!valid) {
/*  34: 73 */       populateMessage(message, fieldLabel, new Object[0]);
/*  35:    */     }
/*  36: 75 */     return message;
/*  37:    */   }
/*  38:    */   
/*  39:    */   private void initValidateMethodIfNeeded()
/*  40:    */     throws Exception
/*  41:    */   {
/*  42: 79 */     if (this.validateMethod == null) {
/*  43: 80 */       this.validateMethod = this.validatorClass.getMethod(this.methodName, new Class[] { String.class });
/*  44:    */     }
/*  45:    */   }
/*  46:    */   
/*  47:    */   private void initValidatorIfNeeded()
/*  48:    */     throws Exception
/*  49:    */   {
/*  50: 88 */     if (this.validator == null) {
/*  51: 89 */       if (this.factoryMethod == null)
/*  52:    */       {
/*  53: 90 */         this.validator = this.validatorClass.newInstance();
/*  54:    */       }
/*  55:    */       else
/*  56:    */       {
/*  57: 92 */         Method method = this.validatorClass.getMethod(this.factoryMethod, new Class[0]);
/*  58: 93 */         this.validator = method.invoke(null, (Object[])null);
/*  59:    */       }
/*  60:    */     }
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void setMethodName(String methodName)
/*  64:    */   {
/*  65: 99 */     this.methodName = methodName;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public void setValidatorClass(Class<?> validatorClass)
/*  69:    */   {
/*  70:103 */     this.validatorClass = validatorClass;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void setFactoryMethod(String factoryMethod)
/*  74:    */   {
/*  75:107 */     this.factoryMethod = factoryMethod;
/*  76:    */   }
/*  77:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.validation.validators.CommonBridgeValidator
 * JD-Core Version:    0.7.0.1
 */