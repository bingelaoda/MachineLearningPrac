/*  1:   */ package org.boon.validation.validators;
/*  2:   */ 
/*  3:   */ import java.util.HashMap;
/*  4:   */ import java.util.Map;
/*  5:   */ import java.util.regex.Matcher;
/*  6:   */ import java.util.regex.Pattern;
/*  7:   */ import org.boon.validation.ValidatorMessage;
/*  8:   */ import org.boon.validation.ValidatorMessageHolder;
/*  9:   */ 
/* 10:   */ public class MatchAnyRegexValidator
/* 11:   */   extends BaseValidator
/* 12:   */ {
/* 13:   */   private String[] matches;
/* 14:53 */   private Map<String, Pattern> compiledRegexCache = new HashMap();
/* 15:   */   
/* 16:   */   public ValidatorMessageHolder validate(Object object, String fieldLabel)
/* 17:   */   {
/* 18:57 */     ValidatorMessage message = new ValidatorMessage();
/* 19:58 */     if (object == null) {
/* 20:59 */       return message;
/* 21:   */     }
/* 22:61 */     String string = object.toString();
/* 23:62 */     int validCount = 0;
/* 24:64 */     for (String match : this.matches)
/* 25:   */     {
/* 26:65 */       Pattern pattern = compileRegex(match);
/* 27:66 */       if (pattern.matcher(string).matches()) {
/* 28:67 */         validCount++;
/* 29:   */       }
/* 30:   */     }
/* 31:71 */     if (validCount == 0)
/* 32:   */     {
/* 33:72 */       populateMessage(message, fieldLabel, new Object[0]);
/* 34:73 */       return message;
/* 35:   */     }
/* 36:76 */     return message;
/* 37:   */   }
/* 38:   */   
/* 39:   */   private Pattern compileRegex(String match)
/* 40:   */   {
/* 41:86 */     Pattern pattern = (Pattern)this.compiledRegexCache.get(match);
/* 42:87 */     if (pattern == null)
/* 43:   */     {
/* 44:88 */       pattern = Pattern.compile(match);
/* 45:89 */       this.compiledRegexCache.put(match, pattern);
/* 46:   */     }
/* 47:91 */     return pattern;
/* 48:   */   }
/* 49:   */   
/* 50:   */   public void setMatches(String[] matches)
/* 51:   */   {
/* 52:95 */     this.matches = matches;
/* 53:   */   }
/* 54:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.validation.validators.MatchAnyRegexValidator
 * JD-Core Version:    0.7.0.1
 */