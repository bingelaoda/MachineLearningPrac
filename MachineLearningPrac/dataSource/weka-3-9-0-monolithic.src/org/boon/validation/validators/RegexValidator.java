/*   1:    */ package org.boon.validation.validators;
/*   2:    */ 
/*   3:    */ import java.util.HashMap;
/*   4:    */ import java.util.Map;
/*   5:    */ import java.util.regex.Matcher;
/*   6:    */ import java.util.regex.Pattern;
/*   7:    */ import org.boon.validation.ValidatorMessage;
/*   8:    */ import org.boon.validation.ValidatorMessageHolder;
/*   9:    */ 
/*  10:    */ public class RegexValidator
/*  11:    */   extends BaseValidator
/*  12:    */ {
/*  13:    */   private String match;
/*  14:    */   private boolean negate;
/*  15: 53 */   private Map<String, Pattern> compiledRegexCache = new HashMap();
/*  16:    */   
/*  17:    */   public boolean isNegate()
/*  18:    */   {
/*  19: 56 */     return this.negate;
/*  20:    */   }
/*  21:    */   
/*  22:    */   public void setNegate(boolean negate)
/*  23:    */   {
/*  24: 60 */     this.negate = negate;
/*  25:    */   }
/*  26:    */   
/*  27:    */   protected String getMatch()
/*  28:    */   {
/*  29: 69 */     return this.match;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public void setMatch(String regex)
/*  33:    */   {
/*  34: 73 */     this.match = regex;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public ValidatorMessageHolder validate(Object object, String fieldLabel)
/*  38:    */   {
/*  39: 77 */     ValidatorMessage message = new ValidatorMessage();
/*  40: 78 */     if (object == null) {
/*  41: 79 */       return message;
/*  42:    */     }
/*  43: 81 */     String string = object.toString();
/*  44: 82 */     Pattern pattern = compileRegex();
/*  45:    */     boolean valid;
/*  46:    */     boolean valid;
/*  47: 84 */     if (this.negate) {
/*  48: 85 */       valid = !pattern.matcher(string).matches();
/*  49:    */     } else {
/*  50: 87 */       valid = pattern.matcher(string).matches();
/*  51:    */     }
/*  52: 90 */     if (!valid)
/*  53:    */     {
/*  54: 91 */       populateMessage(message, fieldLabel, new Object[0]);
/*  55: 92 */       return message;
/*  56:    */     }
/*  57: 95 */     return message;
/*  58:    */   }
/*  59:    */   
/*  60:    */   private Pattern compileRegex()
/*  61:    */   {
/*  62:105 */     Pattern pattern = (Pattern)this.compiledRegexCache.get(getMatch());
/*  63:106 */     if (pattern == null)
/*  64:    */     {
/*  65:107 */       pattern = Pattern.compile(getMatch());
/*  66:108 */       this.compiledRegexCache.put(getMatch(), pattern);
/*  67:    */     }
/*  68:110 */     return pattern;
/*  69:    */   }
/*  70:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.validation.validators.RegexValidator
 * JD-Core Version:    0.7.0.1
 */