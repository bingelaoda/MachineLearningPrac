/*   1:    */ package org.boon.validation;
/*   2:    */ 
/*   3:    */ import org.boon.Lists;
/*   4:    */ import org.boon.validation.validators.BaseValidator;
/*   5:    */ import org.boon.validation.validators.CompositeValidator;
/*   6:    */ import org.boon.validation.validators.LengthValidator;
/*   7:    */ import org.boon.validation.validators.RegexValidator;
/*   8:    */ import org.boon.validation.validators.RequiredValidator;
/*   9:    */ 
/*  10:    */ public class Validators
/*  11:    */ {
/*  12:    */   public static RequiredValidator required(String detailMessage, String summaryMessage)
/*  13:    */   {
/*  14: 38 */     RequiredValidator validator = new RequiredValidator();
/*  15: 39 */     init(detailMessage, summaryMessage, validator);
/*  16: 40 */     return validator;
/*  17:    */   }
/*  18:    */   
/*  19:    */   public static RequiredValidator required(String detailMessage)
/*  20:    */   {
/*  21: 44 */     RequiredValidator validator = new RequiredValidator();
/*  22: 45 */     init(detailMessage, validator);
/*  23: 46 */     return validator;
/*  24:    */   }
/*  25:    */   
/*  26:    */   private static void init(String detailMessage, BaseValidator validator)
/*  27:    */   {
/*  28: 50 */     validator.setDetailMessage(detailMessage);
/*  29: 51 */     validator.setNoSummary(true);
/*  30: 52 */     validator.init();
/*  31:    */   }
/*  32:    */   
/*  33:    */   private static void init(String detailMessage, String summaryMessage, BaseValidator validator)
/*  34:    */   {
/*  35: 56 */     validator.setDetailMessage(detailMessage);
/*  36: 57 */     validator.setSummaryMessage(summaryMessage);
/*  37: 58 */     validator.init();
/*  38:    */   }
/*  39:    */   
/*  40:    */   public static LengthValidator length(int min, int max, String message)
/*  41:    */   {
/*  42: 63 */     LengthValidator v = new LengthValidator();
/*  43: 64 */     v.setMax(max);
/*  44: 65 */     v.setMin(min);
/*  45: 66 */     init(message, v);
/*  46: 67 */     return v;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public static LengthValidator length(String message, String summary)
/*  50:    */   {
/*  51: 72 */     LengthValidator eq = new LengthValidator();
/*  52: 73 */     init(message, summary, eq);
/*  53: 74 */     return eq;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public static CompositeValidator validators(FieldValidator... validators)
/*  57:    */   {
/*  58: 79 */     CompositeValidator compositeValidator = new CompositeValidator();
/*  59: 80 */     compositeValidator.setValidatorList(Lists.list(validators));
/*  60: 81 */     return compositeValidator;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public static RegexValidator match(String match, String message, String summary)
/*  64:    */   {
/*  65: 87 */     RegexValidator v = new RegexValidator();
/*  66: 88 */     v.setMatch(match);
/*  67: 89 */     init(message, summary, v);
/*  68: 90 */     return v;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public static RegexValidator dontMatch(String match, String message, String summary)
/*  72:    */   {
/*  73: 95 */     RegexValidator v = new RegexValidator();
/*  74: 96 */     v.setMatch(match);
/*  75: 97 */     v.setNegate(true);
/*  76: 98 */     init(message, summary, v);
/*  77: 99 */     return v;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public static RegexValidator noIdenticalThreeDigits(String message, String summary)
/*  81:    */   {
/*  82:103 */     return dontMatch("000|111|222|333|444|555|666|777|888|999", message, summary);
/*  83:    */   }
/*  84:    */   
/*  85:    */   public static RegexValidator mustBeNumeric(String message, String summary)
/*  86:    */   {
/*  87:107 */     return match("^[0-9]*$", message, summary);
/*  88:    */   }
/*  89:    */   
/*  90:    */   public static RegexValidator mustBeThreeDigitNumeric(String message, String summary)
/*  91:    */   {
/*  92:112 */     return match("^[0-9]{3}$", message, summary);
/*  93:    */   }
/*  94:    */   
/*  95:    */   public static RegexValidator mustBeFourDigitNumeric(String message, String summary)
/*  96:    */   {
/*  97:116 */     return match("^[0-9]{4}$", message, summary);
/*  98:    */   }
/*  99:    */   
/* 100:    */   public static RegexValidator personName(String message, String summary)
/* 101:    */   {
/* 102:120 */     return match("^([a-zA-Z]|[ -])*$", message, summary);
/* 103:    */   }
/* 104:    */   
/* 105:    */   public static RegexValidator properNoun(String message, String summary)
/* 106:    */   {
/* 107:124 */     return match("^([a-zA-Z]|[ -])*$", message, summary);
/* 108:    */   }
/* 109:    */   
/* 110:    */   public static RegexValidator address(String message, String summary)
/* 111:    */   {
/* 112:128 */     return match("^(\\d+ \\w+)|(\\w+ \\d+)$", message, summary);
/* 113:    */   }
/* 114:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.validation.Validators
 * JD-Core Version:    0.7.0.1
 */