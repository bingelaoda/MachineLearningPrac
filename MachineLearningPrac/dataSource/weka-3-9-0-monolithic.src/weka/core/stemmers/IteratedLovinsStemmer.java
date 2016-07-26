/*   1:    */ package weka.core.stemmers;
/*   2:    */ 
/*   3:    */ import weka.core.RevisionUtils;
/*   4:    */ import weka.core.TechnicalInformation;
/*   5:    */ 
/*   6:    */ public class IteratedLovinsStemmer
/*   7:    */   extends LovinsStemmer
/*   8:    */ {
/*   9:    */   static final long serialVersionUID = 960689687163788264L;
/*  10:    */   
/*  11:    */   public String globalInfo()
/*  12:    */   {
/*  13: 67 */     return "An iterated version of the Lovins stemmer. It stems the word (in case it's longer than 2 characters) until it no further changes.\n\nFor more information about the Lovins stemmer see:\n\n" + getTechnicalInformation().toString();
/*  14:    */   }
/*  15:    */   
/*  16:    */   public String stem(String str)
/*  17:    */   {
/*  18: 83 */     if (str.length() <= 2) {
/*  19: 84 */       return str;
/*  20:    */     }
/*  21: 86 */     String stemmed = super.stem(str);
/*  22: 87 */     while (!stemmed.equals(str))
/*  23:    */     {
/*  24: 88 */       str = stemmed;
/*  25: 89 */       stemmed = super.stem(stemmed);
/*  26:    */     }
/*  27: 91 */     return stemmed;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public String getRevision()
/*  31:    */   {
/*  32:100 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  33:    */   }
/*  34:    */   
/*  35:    */   public static void main(String[] args)
/*  36:    */   {
/*  37:    */     try
/*  38:    */     {
/*  39:110 */       Stemming.useStemmer(new IteratedLovinsStemmer(), args);
/*  40:    */     }
/*  41:    */     catch (Exception e)
/*  42:    */     {
/*  43:113 */       e.printStackTrace();
/*  44:    */     }
/*  45:    */   }
/*  46:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.stemmers.IteratedLovinsStemmer
 * JD-Core Version:    0.7.0.1
 */