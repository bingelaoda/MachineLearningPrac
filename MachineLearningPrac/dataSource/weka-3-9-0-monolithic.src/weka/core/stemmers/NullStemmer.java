/*  1:   */ package weka.core.stemmers;
/*  2:   */ 
/*  3:   */ import weka.core.RevisionUtils;
/*  4:   */ 
/*  5:   */ public class NullStemmer
/*  6:   */   implements Stemmer
/*  7:   */ {
/*  8:   */   static final long serialVersionUID = -3671261636532625496L;
/*  9:   */   
/* 10:   */   public String globalInfo()
/* 11:   */   {
/* 12:47 */     return "A dummy stemmer that performs no stemming at all.";
/* 13:   */   }
/* 14:   */   
/* 15:   */   public String stem(String word)
/* 16:   */   {
/* 17:58 */     return new String(word);
/* 18:   */   }
/* 19:   */   
/* 20:   */   public String toString()
/* 21:   */   {
/* 22:67 */     return getClass().getName();
/* 23:   */   }
/* 24:   */   
/* 25:   */   public String getRevision()
/* 26:   */   {
/* 27:76 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 28:   */   }
/* 29:   */   
/* 30:   */   public static void main(String[] args)
/* 31:   */   {
/* 32:   */     try
/* 33:   */     {
/* 34:86 */       Stemming.useStemmer(new NullStemmer(), args);
/* 35:   */     }
/* 36:   */     catch (Exception e)
/* 37:   */     {
/* 38:89 */       e.printStackTrace();
/* 39:   */     }
/* 40:   */   }
/* 41:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.stemmers.NullStemmer
 * JD-Core Version:    0.7.0.1
 */