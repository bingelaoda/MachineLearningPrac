/*  1:   */ package org.apache.commons.compress.archivers.dump;
/*  2:   */ 
/*  3:   */ public class UnsupportedCompressionAlgorithmException
/*  4:   */   extends DumpArchiveException
/*  5:   */ {
/*  6:   */   private static final long serialVersionUID = 1L;
/*  7:   */   
/*  8:   */   public UnsupportedCompressionAlgorithmException()
/*  9:   */   {
/* 10:31 */     super("this file uses an unsupported compression algorithm.");
/* 11:   */   }
/* 12:   */   
/* 13:   */   public UnsupportedCompressionAlgorithmException(String alg)
/* 14:   */   {
/* 15:35 */     super("this file uses an unsupported compression algorithm: " + alg + ".");
/* 16:   */   }
/* 17:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.dump.UnsupportedCompressionAlgorithmException
 * JD-Core Version:    0.7.0.1
 */