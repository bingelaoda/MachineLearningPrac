/*  1:   */ package org.apache.commons.compress.archivers.dump;
/*  2:   */ 
/*  3:   */ public class InvalidFormatException
/*  4:   */   extends DumpArchiveException
/*  5:   */ {
/*  6:   */   private static final long serialVersionUID = 1L;
/*  7:   */   protected long offset;
/*  8:   */   
/*  9:   */   public InvalidFormatException()
/* 10:   */   {
/* 11:31 */     super("there was an error decoding a tape segment");
/* 12:   */   }
/* 13:   */   
/* 14:   */   public InvalidFormatException(long offset)
/* 15:   */   {
/* 16:35 */     super("there was an error decoding a tape segment header at offset " + offset + ".");
/* 17:   */     
/* 18:37 */     this.offset = offset;
/* 19:   */   }
/* 20:   */   
/* 21:   */   public long getOffset()
/* 22:   */   {
/* 23:41 */     return this.offset;
/* 24:   */   }
/* 25:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.dump.InvalidFormatException
 * JD-Core Version:    0.7.0.1
 */