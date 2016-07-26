/*  1:   */ package org.apache.commons.compress.archivers.dump;
/*  2:   */ 
/*  3:   */ import java.io.IOException;
/*  4:   */ 
/*  5:   */ public class DumpArchiveException
/*  6:   */   extends IOException
/*  7:   */ {
/*  8:   */   private static final long serialVersionUID = 1L;
/*  9:   */   
/* 10:   */   public DumpArchiveException() {}
/* 11:   */   
/* 12:   */   public DumpArchiveException(String msg)
/* 13:   */   {
/* 14:34 */     super(msg);
/* 15:   */   }
/* 16:   */   
/* 17:   */   public DumpArchiveException(Throwable cause)
/* 18:   */   {
/* 19:38 */     initCause(cause);
/* 20:   */   }
/* 21:   */   
/* 22:   */   public DumpArchiveException(String msg, Throwable cause)
/* 23:   */   {
/* 24:42 */     super(msg);
/* 25:43 */     initCause(cause);
/* 26:   */   }
/* 27:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.dump.DumpArchiveException
 * JD-Core Version:    0.7.0.1
 */