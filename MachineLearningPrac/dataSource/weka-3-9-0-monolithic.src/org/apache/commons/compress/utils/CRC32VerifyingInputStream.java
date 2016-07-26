/*  1:   */ package org.apache.commons.compress.utils;
/*  2:   */ 
/*  3:   */ import java.io.InputStream;
/*  4:   */ import java.util.zip.CRC32;
/*  5:   */ 
/*  6:   */ public class CRC32VerifyingInputStream
/*  7:   */   extends ChecksumVerifyingInputStream
/*  8:   */ {
/*  9:   */   public CRC32VerifyingInputStream(InputStream in, long size, int expectedCrc32)
/* 10:   */   {
/* 11:37 */     this(in, size, expectedCrc32 & 0xFFFFFFFF);
/* 12:   */   }
/* 13:   */   
/* 14:   */   public CRC32VerifyingInputStream(InputStream in, long size, long expectedCrc32)
/* 15:   */   {
/* 16:47 */     super(new CRC32(), in, size, expectedCrc32);
/* 17:   */   }
/* 18:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.utils.CRC32VerifyingInputStream
 * JD-Core Version:    0.7.0.1
 */