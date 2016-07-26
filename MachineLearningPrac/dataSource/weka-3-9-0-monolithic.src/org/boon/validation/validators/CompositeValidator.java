/*   1:    */ package org.boon.validation.validators;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import org.boon.validation.FieldValidator;
/*   6:    */ import org.boon.validation.ValidatorMessage;
/*   7:    */ import org.boon.validation.ValidatorMessageHolder;
/*   8:    */ import org.boon.validation.ValidatorMessages;
/*   9:    */ 
/*  10:    */ public class CompositeValidator
/*  11:    */   implements FieldValidator
/*  12:    */ {
/*  13: 49 */   private List<FieldValidator> validatorList = new ArrayList();
/*  14: 50 */   private RequiredValidator requiredValidator = null;
/*  15:    */   private List<String> detailArgs;
/*  16:    */   private List<String> summaryArgs;
/*  17: 53 */   private String stopOnRule = "";
/*  18: 54 */   private boolean stopOnFirstRule = false;
/*  19: 55 */   private boolean stopOnBlank = true;
/*  20:    */   
/*  21:    */   public void setValidatorList(List<FieldValidator> list)
/*  22:    */   {
/*  23: 58 */     this.validatorList = list;
/*  24: 59 */     StopOnRuleValidator stopOnRuleValidator = null;
/*  25: 60 */     for (FieldValidator validator : list)
/*  26:    */     {
/*  27: 61 */       if ((validator instanceof RequiredValidator)) {
/*  28: 62 */         this.requiredValidator = ((RequiredValidator)validator);
/*  29:    */       }
/*  30: 64 */       if ((validator instanceof StopOnRuleValidator)) {
/*  31: 65 */         stopOnRuleValidator = (StopOnRuleValidator)validator;
/*  32:    */       }
/*  33:    */     }
/*  34: 70 */     if (stopOnRuleValidator != null)
/*  35:    */     {
/*  36: 71 */       this.validatorList.remove(this.stopOnRule);
/*  37: 72 */       String ruleName = stopOnRuleValidator.getRuleName();
/*  38: 73 */       if (("first".equals(ruleName)) || (ruleName == null)) {
/*  39: 74 */         this.stopOnFirstRule = true;
/*  40:    */       } else {
/*  41: 76 */         this.stopOnRule = ruleName;
/*  42:    */       }
/*  43:    */     }
/*  44: 79 */     if (this.requiredValidator != null) {
/*  45: 80 */       this.validatorList.remove(this.requiredValidator);
/*  46:    */     }
/*  47:    */   }
/*  48:    */   
/*  49:    */   public ValidatorMessageHolder validate(Object object, String fieldLabel)
/*  50:    */   {
/*  51: 86 */     ValidatorMessages messages = new ValidatorMessages();
/*  52:    */     
/*  53:    */ 
/*  54: 89 */     ValidatorMessage requiredMessage = validateWithRequriedIfPresent(object, fieldLabel, messages);
/*  55:    */     
/*  56: 91 */     boolean proceed = (!this.stopOnBlank) || ((object != null) && (object.toString().trim().length() != 0));
/*  57: 96 */     if (((requiredMessage == null) || (!requiredMessage.hasError())) && 
/*  58: 97 */       (proceed)) {
/*  59: 98 */       runValidationRules(object, fieldLabel, messages);
/*  60:    */     }
/*  61:102 */     return messages;
/*  62:    */   }
/*  63:    */   
/*  64:    */   private void runValidationRules(Object object, String fieldLabel, ValidatorMessages messages)
/*  65:    */   {
/*  66:106 */     for (FieldValidator validator : this.validatorList)
/*  67:    */     {
/*  68:107 */       putArgs(validator);
/*  69:108 */       ValidatorMessage message = (ValidatorMessage)validator.validate(object, fieldLabel);
/*  70:109 */       if (message.hasError())
/*  71:    */       {
/*  72:110 */         messages.add(message);
/*  73:111 */         if (this.stopOnFirstRule) {
/*  74:    */           break;
/*  75:    */         }
/*  76:113 */         if (validator.getClass().getSimpleName().equalsIgnoreCase(this.stopOnRule)) {
/*  77:    */           break;
/*  78:    */         }
/*  79:    */       }
/*  80:    */     }
/*  81:    */   }
/*  82:    */   
/*  83:    */   private ValidatorMessage validateWithRequriedIfPresent(Object object, String fieldLabel, ValidatorMessages messages)
/*  84:    */   {
/*  85:121 */     ValidatorMessage requiredMessage = null;
/*  86:122 */     if (this.requiredValidator != null)
/*  87:    */     {
/*  88:123 */       putArgs(this.requiredValidator);
/*  89:124 */       requiredMessage = (ValidatorMessage)this.requiredValidator.validate(object, fieldLabel);
/*  90:125 */       if (requiredMessage.hasError()) {
/*  91:126 */         messages.add(requiredMessage);
/*  92:    */       }
/*  93:    */     }
/*  94:129 */     return requiredMessage;
/*  95:    */   }
/*  96:    */   
/*  97:    */   private void putArgs(FieldValidator validator)
/*  98:    */   {
/*  99:133 */     if ((validator instanceof BaseValidator))
/* 100:    */     {
/* 101:134 */       BaseValidator aValidator = (BaseValidator)validator;
/* 102:135 */       aValidator.setDetailArgs(this.detailArgs);
/* 103:136 */       aValidator.setSummaryArgs(this.summaryArgs);
/* 104:    */     }
/* 105:    */   }
/* 106:    */   
/* 107:    */   public void setDetailArgs(List<String> detailArgKeys)
/* 108:    */   {
/* 109:142 */     this.detailArgs = detailArgKeys;
/* 110:    */   }
/* 111:    */   
/* 112:    */   public void setSummaryArgs(List<String> summaryArgKeys)
/* 113:    */   {
/* 114:147 */     this.summaryArgs = summaryArgKeys;
/* 115:    */   }
/* 116:    */   
/* 117:    */   public void setStopOnBlank(boolean stopOnBlank)
/* 118:    */   {
/* 119:151 */     this.stopOnBlank = stopOnBlank;
/* 120:    */   }
/* 121:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.validation.validators.CompositeValidator
 * JD-Core Version:    0.7.0.1
 */