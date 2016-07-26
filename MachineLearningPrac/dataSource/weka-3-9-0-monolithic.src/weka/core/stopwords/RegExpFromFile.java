/*   1:    */ package weka.core.stopwords;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import java.util.regex.Matcher;
/*   6:    */ import java.util.regex.Pattern;
/*   7:    */ 
/*   8:    */ public class RegExpFromFile
/*   9:    */   extends AbstractFileBasedStopwords
/*  10:    */ {
/*  11:    */   private static final long serialVersionUID = -722795295494945193L;
/*  12:    */   protected List<Pattern> m_Patterns;
/*  13:    */   
/*  14:    */   public String globalInfo()
/*  15:    */   {
/*  16: 65 */     return "Uses the regular expressions stored in the file for determining whether a word is a stopword (ignored if pointing to a directory). One expression per line.\nMore information on regular expressions:\nhttp://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html";
/*  17:    */   }
/*  18:    */   
/*  19:    */   public String stopwordsTipText()
/*  20:    */   {
/*  21: 81 */     return "The file containing the regular expressions.";
/*  22:    */   }
/*  23:    */   
/*  24:    */   protected void initialize()
/*  25:    */   {
/*  26: 91 */     super.initialize();
/*  27:    */     
/*  28: 93 */     this.m_Patterns = new ArrayList();
/*  29: 94 */     List<String> patterns = read();
/*  30: 95 */     for (String pattern : patterns) {
/*  31: 96 */       this.m_Patterns.add(Pattern.compile(pattern));
/*  32:    */     }
/*  33:    */   }
/*  34:    */   
/*  35:    */   protected synchronized boolean is(String word)
/*  36:    */   {
/*  37:108 */     for (Pattern pattern : this.m_Patterns)
/*  38:    */     {
/*  39:109 */       if (pattern.matcher(word.trim().toLowerCase()).matches())
/*  40:    */       {
/*  41:110 */         if (this.m_Debug) {
/*  42:111 */           debug(pattern.pattern() + " --> true");
/*  43:    */         }
/*  44:112 */         return true;
/*  45:    */       }
/*  46:115 */       if (this.m_Debug) {
/*  47:116 */         debug(pattern.pattern() + " --> false");
/*  48:    */       }
/*  49:    */     }
/*  50:119 */     return false;
/*  51:    */   }
/*  52:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.stopwords.RegExpFromFile
 * JD-Core Version:    0.7.0.1
 */