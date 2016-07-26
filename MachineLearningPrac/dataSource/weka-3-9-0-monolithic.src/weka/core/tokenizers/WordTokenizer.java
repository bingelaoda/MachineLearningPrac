/*   1:    */ package weka.core.tokenizers;
/*   2:    */ 
/*   3:    */ import java.util.StringTokenizer;
/*   4:    */ import weka.core.RevisionUtils;
/*   5:    */ 
/*   6:    */ public class WordTokenizer
/*   7:    */   extends CharacterDelimitedTokenizer
/*   8:    */ {
/*   9:    */   private static final long serialVersionUID = -930893034037880773L;
/*  10:    */   protected transient StringTokenizer m_Tokenizer;
/*  11:    */   
/*  12:    */   public String globalInfo()
/*  13:    */   {
/*  14: 63 */     return "A simple tokenizer that is using the java.util.StringTokenizer class to tokenize the strings.";
/*  15:    */   }
/*  16:    */   
/*  17:    */   public boolean hasMoreElements()
/*  18:    */   {
/*  19: 75 */     return this.m_Tokenizer.hasMoreElements();
/*  20:    */   }
/*  21:    */   
/*  22:    */   public String nextElement()
/*  23:    */   {
/*  24: 86 */     return this.m_Tokenizer.nextToken();
/*  25:    */   }
/*  26:    */   
/*  27:    */   public void tokenize(String s)
/*  28:    */   {
/*  29: 96 */     this.m_Tokenizer = new StringTokenizer(s, getDelimiters());
/*  30:    */   }
/*  31:    */   
/*  32:    */   public String getRevision()
/*  33:    */   {
/*  34:106 */     return RevisionUtils.extract("$Revision: 10203 $");
/*  35:    */   }
/*  36:    */   
/*  37:    */   public static void main(String[] args)
/*  38:    */   {
/*  39:116 */     runTokenizer(new WordTokenizer(), args);
/*  40:    */   }
/*  41:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.tokenizers.WordTokenizer
 * JD-Core Version:    0.7.0.1
 */