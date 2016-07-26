/*   1:    */ package weka.core.tokenizers;
/*   2:    */ 
/*   3:    */ import java.util.NoSuchElementException;
/*   4:    */ import weka.core.RevisionUtils;
/*   5:    */ 
/*   6:    */ public class AlphabeticTokenizer
/*   7:    */   extends Tokenizer
/*   8:    */ {
/*   9:    */   private static final long serialVersionUID = 6705199562609861697L;
/*  10:    */   protected char[] m_Str;
/*  11:    */   protected int m_CurrentPos;
/*  12:    */   
/*  13:    */   public String globalInfo()
/*  14:    */   {
/*  15: 56 */     return "Alphabetic string tokenizer, tokens are to be formed only from contiguous alphabetic sequences.";
/*  16:    */   }
/*  17:    */   
/*  18:    */   public boolean hasMoreElements()
/*  19:    */   {
/*  20: 67 */     int beginpos = this.m_CurrentPos;
/*  21: 71 */     while ((beginpos < this.m_Str.length) && ((this.m_Str[beginpos] < 'a') || (this.m_Str[beginpos] > 'z')) && ((this.m_Str[beginpos] < 'A') || (this.m_Str[beginpos] > 'Z'))) {
/*  22: 72 */       beginpos++;
/*  23:    */     }
/*  24: 74 */     this.m_CurrentPos = beginpos;
/*  25: 76 */     if ((beginpos < this.m_Str.length) && (((this.m_Str[beginpos] >= 'a') && (this.m_Str[beginpos] <= 'z')) || ((this.m_Str[beginpos] >= 'A') && (this.m_Str[beginpos] <= 'Z')))) {
/*  26: 78 */       return true;
/*  27:    */     }
/*  28: 80 */     return false;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public String nextElement()
/*  32:    */   {
/*  33: 93 */     int beginpos = this.m_CurrentPos;
/*  34: 97 */     while ((beginpos < this.m_Str.length) && (this.m_Str[beginpos] < 'a') && (this.m_Str[beginpos] > 'z') && (this.m_Str[beginpos] < 'A') && (this.m_Str[beginpos] > 'Z')) {
/*  35: 98 */       beginpos++;
/*  36:    */     }
/*  37:    */     int endpos;
/*  38:100 */     this.m_CurrentPos = (endpos = beginpos);
/*  39:102 */     if (beginpos >= this.m_Str.length) {
/*  40:103 */       throw new NoSuchElementException("No more tokens present");
/*  41:    */     }
/*  42:107 */     while ((endpos < this.m_Str.length) && (((this.m_Str[endpos] >= 'a') && (this.m_Str[endpos] <= 'z')) || ((this.m_Str[endpos] >= 'A') && (this.m_Str[endpos] <= 'Z')))) {
/*  43:108 */       endpos++;
/*  44:    */     }
/*  45:111 */     String s = new String(this.m_Str, beginpos, endpos - this.m_CurrentPos);
/*  46:112 */     this.m_CurrentPos = endpos;
/*  47:    */     
/*  48:114 */     return s;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void tokenize(String s)
/*  52:    */   {
/*  53:124 */     this.m_CurrentPos = 0;
/*  54:125 */     this.m_Str = new char[s.length()];
/*  55:126 */     s.getChars(0, s.length(), this.m_Str, 0);
/*  56:    */   }
/*  57:    */   
/*  58:    */   public String getRevision()
/*  59:    */   {
/*  60:136 */     return RevisionUtils.extract("$Revision: 10203 $");
/*  61:    */   }
/*  62:    */   
/*  63:    */   public static void main(String[] args)
/*  64:    */   {
/*  65:146 */     runTokenizer(new AlphabeticTokenizer(), args);
/*  66:    */   }
/*  67:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.tokenizers.AlphabeticTokenizer
 * JD-Core Version:    0.7.0.1
 */