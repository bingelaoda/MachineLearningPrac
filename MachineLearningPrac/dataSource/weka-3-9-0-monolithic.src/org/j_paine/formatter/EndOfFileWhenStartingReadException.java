/*  1:   */ package org.j_paine.formatter;
/*  2:   */ 
/*  3:   */ public class EndOfFileWhenStartingReadException
/*  4:   */   extends InputFormatException
/*  5:   */ {
/*  6:   */   public EndOfFileWhenStartingReadException(int paramInt1, String paramString1, String paramString2, int paramInt2)
/*  7:   */   {
/*  8:16 */     this("End of file when starting read of formatted data:\n  Index  = " + paramInt1 + "\n" + "  Format = " + paramString1 + "\n" + "Last line was number " + paramInt2 + ":\n" + paramString2);
/*  9:   */   }
/* 10:   */   
/* 11:   */   public EndOfFileWhenStartingReadException(String paramString)
/* 12:   */   {
/* 13:26 */     super(paramString);
/* 14:   */   }
/* 15:   */   
/* 16:   */   public EndOfFileWhenStartingReadException() {}
/* 17:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.EndOfFileWhenStartingReadException
 * JD-Core Version:    0.7.0.1
 */