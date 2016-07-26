/*  1:   */ package org.apache.commons.compress.archivers.dump;
/*  2:   */ 
/*  3:   */ public class ShortFileException
/*  4:   */   extends DumpArchiveException
/*  5:   */ {
/*  6:   */   private static final long serialVersionUID = 1L;
/*  7:   */   
/*  8:   */   public ShortFileException()
/*  9:   */   {
/* 10:30 */     super("unexpected EOF");
/* 11:   */   }
/* 12:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.dump.ShortFileException
 * JD-Core Version:    0.7.0.1
 */