/*  1:   */ package org.apache.commons.compress.archivers;
/*  2:   */ 
/*  3:   */ public class ArchiveException
/*  4:   */   extends Exception
/*  5:   */ {
/*  6:   */   private static final long serialVersionUID = 2772690708123267100L;
/*  7:   */   
/*  8:   */   public ArchiveException(String message)
/*  9:   */   {
/* 10:37 */     super(message);
/* 11:   */   }
/* 12:   */   
/* 13:   */   public ArchiveException(String message, Exception cause)
/* 14:   */   {
/* 15:49 */     super(message);
/* 16:50 */     initCause(cause);
/* 17:   */   }
/* 18:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.ArchiveException
 * JD-Core Version:    0.7.0.1
 */