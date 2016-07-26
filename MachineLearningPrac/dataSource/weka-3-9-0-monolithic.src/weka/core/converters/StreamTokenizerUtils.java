/*  1:   */ package weka.core.converters;
/*  2:   */ 
/*  3:   */ import java.io.IOException;
/*  4:   */ import java.io.Serializable;
/*  5:   */ import java.io.StreamTokenizer;
/*  6:   */ import weka.core.RevisionHandler;
/*  7:   */ import weka.core.RevisionUtils;
/*  8:   */ 
/*  9:   */ public class StreamTokenizerUtils
/* 10:   */   implements Serializable, RevisionHandler
/* 11:   */ {
/* 12:   */   private static final long serialVersionUID = -5786996944597404253L;
/* 13:   */   
/* 14:   */   public String getRevision()
/* 15:   */   {
/* 16:44 */     return RevisionUtils.extract("$Revision: 9284 $");
/* 17:   */   }
/* 18:   */   
/* 19:   */   public static void getFirstToken(StreamTokenizer tokenizer)
/* 20:   */     throws IOException
/* 21:   */   {
/* 22:56 */     while (tokenizer.nextToken() == 10) {}
/* 23:59 */     if ((tokenizer.ttype == 39) || (tokenizer.ttype == 34)) {
/* 24:60 */       tokenizer.ttype = -3;
/* 25:61 */     } else if ((tokenizer.ttype == -3) && (tokenizer.sval.equals("?"))) {
/* 26:63 */       tokenizer.ttype = 63;
/* 27:   */     }
/* 28:   */   }
/* 29:   */   
/* 30:   */   public static void getToken(StreamTokenizer tokenizer)
/* 31:   */     throws IOException
/* 32:   */   {
/* 33:75 */     tokenizer.nextToken();
/* 34:76 */     if (tokenizer.ttype == 10) {
/* 35:77 */       return;
/* 36:   */     }
/* 37:80 */     if ((tokenizer.ttype == 39) || (tokenizer.ttype == 34)) {
/* 38:81 */       tokenizer.ttype = -3;
/* 39:82 */     } else if ((tokenizer.ttype == -3) && (tokenizer.sval.equals("?"))) {
/* 40:84 */       tokenizer.ttype = 63;
/* 41:   */     }
/* 42:   */   }
/* 43:   */   
/* 44:   */   public static void errms(StreamTokenizer tokenizer, String theMsg)
/* 45:   */     throws IOException
/* 46:   */   {
/* 47:98 */     throw new IOException(theMsg + ", read " + tokenizer.toString());
/* 48:   */   }
/* 49:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.StreamTokenizerUtils
 * JD-Core Version:    0.7.0.1
 */