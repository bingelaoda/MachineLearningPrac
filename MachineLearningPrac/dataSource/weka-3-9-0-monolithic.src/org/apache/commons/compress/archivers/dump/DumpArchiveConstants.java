/*  1:   */ package org.apache.commons.compress.archivers.dump;
/*  2:   */ 
/*  3:   */ public final class DumpArchiveConstants
/*  4:   */ {
/*  5:   */   public static final int TP_SIZE = 1024;
/*  6:   */   public static final int NTREC = 10;
/*  7:   */   public static final int HIGH_DENSITY_NTREC = 32;
/*  8:   */   public static final int OFS_MAGIC = 60011;
/*  9:   */   public static final int NFS_MAGIC = 60012;
/* 10:   */   public static final int FS_UFS2_MAGIC = 424935705;
/* 11:   */   public static final int CHECKSUM = 84446;
/* 12:   */   public static final int LBLSIZE = 16;
/* 13:   */   public static final int NAMELEN = 64;
/* 14:   */   
/* 15:   */   public static enum SEGMENT_TYPE
/* 16:   */   {
/* 17:43 */     TAPE(1),  INODE(2),  BITS(3),  ADDR(4),  END(5),  CLRI(6);
/* 18:   */     
/* 19:   */     int code;
/* 20:   */     
/* 21:   */     private SEGMENT_TYPE(int code)
/* 22:   */     {
/* 23:53 */       this.code = code;
/* 24:   */     }
/* 25:   */     
/* 26:   */     public static SEGMENT_TYPE find(int code)
/* 27:   */     {
/* 28:57 */       for (SEGMENT_TYPE t : ) {
/* 29:58 */         if (t.code == code) {
/* 30:59 */           return t;
/* 31:   */         }
/* 32:   */       }
/* 33:63 */       return null;
/* 34:   */     }
/* 35:   */   }
/* 36:   */   
/* 37:   */   public static enum COMPRESSION_TYPE
/* 38:   */   {
/* 39:71 */     ZLIB(0),  BZLIB(1),  LZO(2);
/* 40:   */     
/* 41:   */     int code;
/* 42:   */     
/* 43:   */     private COMPRESSION_TYPE(int code)
/* 44:   */     {
/* 45:78 */       this.code = code;
/* 46:   */     }
/* 47:   */     
/* 48:   */     public static COMPRESSION_TYPE find(int code)
/* 49:   */     {
/* 50:82 */       for (COMPRESSION_TYPE t : ) {
/* 51:83 */         if (t.code == code) {
/* 52:84 */           return t;
/* 53:   */         }
/* 54:   */       }
/* 55:88 */       return null;
/* 56:   */     }
/* 57:   */   }
/* 58:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.dump.DumpArchiveConstants
 * JD-Core Version:    0.7.0.1
 */