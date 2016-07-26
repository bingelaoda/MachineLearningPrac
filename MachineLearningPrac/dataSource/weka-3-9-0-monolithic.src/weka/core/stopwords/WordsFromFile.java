/*   1:    */ package weka.core.stopwords;
/*   2:    */ 
/*   3:    */ import java.util.HashSet;
/*   4:    */ import java.util.List;
/*   5:    */ 
/*   6:    */ public class WordsFromFile
/*   7:    */   extends AbstractFileBasedStopwords
/*   8:    */ {
/*   9:    */   private static final long serialVersionUID = -722795295494945193L;
/*  10:    */   protected HashSet<String> m_Words;
/*  11:    */   
/*  12:    */   public String globalInfo()
/*  13:    */   {
/*  14: 64 */     return "Uses the stopwords located in the specified file (ignored _if pointing to a directory). One stopword per line. Lines starting with '#' are considered comments and ignored.";
/*  15:    */   }
/*  16:    */   
/*  17:    */   public String stopwordsTipText()
/*  18:    */   {
/*  19: 78 */     return "The file containing the stopwords.";
/*  20:    */   }
/*  21:    */   
/*  22:    */   protected void initialize()
/*  23:    */   {
/*  24: 88 */     super.initialize();
/*  25:    */     
/*  26: 90 */     this.m_Words = new HashSet();
/*  27: 91 */     List<String> words = read();
/*  28: 92 */     for (String word : words) {
/*  29: 94 */       if (!word.startsWith("#")) {
/*  30: 95 */         this.m_Words.add(word);
/*  31:    */       }
/*  32:    */     }
/*  33:    */   }
/*  34:    */   
/*  35:    */   protected synchronized boolean is(String word)
/*  36:    */   {
/*  37:107 */     return this.m_Words.contains(word.trim().toLowerCase());
/*  38:    */   }
/*  39:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.stopwords.WordsFromFile
 * JD-Core Version:    0.7.0.1
 */