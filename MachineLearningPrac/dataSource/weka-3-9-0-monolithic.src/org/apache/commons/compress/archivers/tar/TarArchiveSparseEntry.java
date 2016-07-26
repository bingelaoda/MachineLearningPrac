/*  1:   */ package org.apache.commons.compress.archivers.tar;
/*  2:   */ 
/*  3:   */ import java.io.IOException;
/*  4:   */ 
/*  5:   */ public class TarArchiveSparseEntry
/*  6:   */   implements TarConstants
/*  7:   */ {
/*  8:   */   private final boolean isExtended;
/*  9:   */   
/* 10:   */   public TarArchiveSparseEntry(byte[] headerBuf)
/* 11:   */     throws IOException
/* 12:   */   {
/* 13:55 */     int offset = 0;
/* 14:56 */     offset += 504;
/* 15:57 */     this.isExtended = TarUtils.parseBoolean(headerBuf, offset);
/* 16:   */   }
/* 17:   */   
/* 18:   */   public boolean isExtended()
/* 19:   */   {
/* 20:61 */     return this.isExtended;
/* 21:   */   }
/* 22:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.tar.TarArchiveSparseEntry
 * JD-Core Version:    0.7.0.1
 */