/*  1:   */ package org.apache.commons.compress.archivers;
/*  2:   */ 
/*  3:   */ public class StreamingNotSupportedException
/*  4:   */   extends ArchiveException
/*  5:   */ {
/*  6:   */   private static final long serialVersionUID = 1L;
/*  7:   */   private final String format;
/*  8:   */   
/*  9:   */   public StreamingNotSupportedException(String format)
/* 10:   */   {
/* 11:38 */     super("The " + format + " doesn't support streaming.");
/* 12:39 */     this.format = format;
/* 13:   */   }
/* 14:   */   
/* 15:   */   public String getFormat()
/* 16:   */   {
/* 17:48 */     return this.format;
/* 18:   */   }
/* 19:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.StreamingNotSupportedException
 * JD-Core Version:    0.7.0.1
 */