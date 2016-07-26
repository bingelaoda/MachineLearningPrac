/*   1:    */ package org.boon.validation.validators;
/*   2:    */ 
/*   3:    */ import org.boon.messages.MessageSpecification;
/*   4:    */ import org.boon.validation.ValidatorMessage;
/*   5:    */ import org.boon.validation.ValidatorMessageHolder;
/*   6:    */ 
/*   7:    */ public class LongRangeValidator
/*   8:    */   extends AbstractRangeValidator
/*   9:    */ {
/*  10:    */   private Long min;
/*  11:    */   private Long max;
/*  12:    */   private Class<?> type;
/*  13:    */   private MessageSpecification underMin;
/*  14:    */   private MessageSpecification overMax;
/*  15:    */   
/*  16:    */   public ValidatorMessageHolder validate(Object fieldValue, String fieldLabel)
/*  17:    */   {
/*  18: 64 */     ValidatorMessage validatorMessage = new ValidatorMessage();
/*  19: 65 */     if (fieldValue == null) {
/*  20: 66 */       return validatorMessage;
/*  21:    */     }
/*  22: 69 */     dynamicallyInitIfNeeded(fieldValue);
/*  23: 71 */     if (!super.isValueGreaterThanMin((Comparable)fieldValue)) {
/*  24: 72 */       populateMessage(this.underMin, validatorMessage, fieldLabel, new Object[] { this.min });
/*  25: 73 */     } else if (!super.isValueLessThanMax((Comparable)fieldValue)) {
/*  26: 74 */       populateMessage(this.overMax, validatorMessage, fieldLabel, new Object[] { this.max });
/*  27:    */     }
/*  28: 77 */     return validatorMessage;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public void init()
/*  32:    */   {
/*  33: 85 */     if (this.underMin == null)
/*  34:    */     {
/*  35: 86 */       this.underMin = new MessageSpecification();
/*  36: 87 */       this.underMin.setDetailMessage("{validator.range.underMin.detail}");
/*  37: 88 */       this.underMin.setSummaryMessage("{validator.range.underMin.summary}");
/*  38:    */     }
/*  39: 91 */     if (this.overMax == null)
/*  40:    */     {
/*  41: 92 */       this.overMax = new MessageSpecification();
/*  42: 93 */       this.overMax.setDetailMessage("{validator.range.overMax.detail}");
/*  43: 94 */       this.overMax.setSummaryMessage("{validator.range.overMax.summary");
/*  44:    */     }
/*  45: 97 */     if (this.type == null) {
/*  46: 98 */       return;
/*  47:    */     }
/*  48:103 */     if (!isInitialized()) {
/*  49:104 */       if (this.type.equals(Integer.class)) {
/*  50:105 */         init(new Integer(this.min.intValue()), new Integer(this.max.intValue()));
/*  51:106 */       } else if (this.type.equals(Byte.class)) {
/*  52:107 */         init(new Byte(this.min.byteValue()), new Byte(this.max.byteValue()));
/*  53:108 */       } else if (this.type.equals(Short.class)) {
/*  54:109 */         init(new Short((short)this.min.byteValue()), new Short((short)this.max.byteValue()));
/*  55:    */       } else {
/*  56:111 */         init(this.min, this.max);
/*  57:    */       }
/*  58:    */     }
/*  59:    */   }
/*  60:    */   
/*  61:    */   private void dynamicallyInitIfNeeded(Object value)
/*  62:    */   {
/*  63:126 */     if (!isInitialized()) {
/*  64:127 */       if ((value instanceof Integer)) {
/*  65:128 */         init(new Integer(this.min.intValue()), new Integer(this.max.intValue()));
/*  66:129 */       } else if ((value instanceof Byte)) {
/*  67:130 */         init(new Byte(this.min.byteValue()), new Byte(this.max.byteValue()));
/*  68:131 */       } else if ((value instanceof Short)) {
/*  69:132 */         init(new Short(this.min.shortValue()), new Short(this.max.shortValue()));
/*  70:    */       } else {
/*  71:134 */         init(this.min, this.max);
/*  72:    */       }
/*  73:    */     }
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void setMax(Long max)
/*  77:    */   {
/*  78:140 */     this.max = max;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void setMin(Long min)
/*  82:    */   {
/*  83:145 */     this.min = min;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public void setType(Class<?> type)
/*  87:    */   {
/*  88:149 */     this.type = type;
/*  89:    */   }
/*  90:    */   
/*  91:    */   protected void setOverMax(MessageSpecification overMax)
/*  92:    */   {
/*  93:153 */     this.overMax = overMax;
/*  94:    */   }
/*  95:    */   
/*  96:    */   protected void setUnderMin(MessageSpecification underMin)
/*  97:    */   {
/*  98:157 */     this.underMin = underMin;
/*  99:    */   }
/* 100:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.validation.validators.LongRangeValidator
 * JD-Core Version:    0.7.0.1
 */