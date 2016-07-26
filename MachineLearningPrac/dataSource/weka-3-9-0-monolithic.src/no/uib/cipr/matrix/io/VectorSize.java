/*  1:   */ package no.uib.cipr.matrix.io;
/*  2:   */ 
/*  3:   */ public class VectorSize
/*  4:   */ {
/*  5:   */   private int size;
/*  6:   */   private int numEntries;
/*  7:   */   
/*  8:   */   public VectorSize(int size)
/*  9:   */   {
/* 10:46 */     this.size = size;
/* 11:47 */     this.numEntries = size;
/* 12:49 */     if (size < 0) {
/* 13:50 */       throw new IllegalArgumentException("size < 0");
/* 14:   */     }
/* 15:   */   }
/* 16:   */   
/* 17:   */   public VectorSize(int size, int numEntries)
/* 18:   */   {
/* 19:62 */     this.size = size;
/* 20:63 */     this.numEntries = numEntries;
/* 21:65 */     if ((size < 0) || (numEntries < 0)) {
/* 22:66 */       throw new IllegalArgumentException("size < 0 || numEntries < 0");
/* 23:   */     }
/* 24:67 */     if (numEntries > size) {
/* 25:68 */       throw new IllegalArgumentException("numEntries > size");
/* 26:   */     }
/* 27:   */   }
/* 28:   */   
/* 29:   */   public int size()
/* 30:   */   {
/* 31:75 */     return this.size;
/* 32:   */   }
/* 33:   */   
/* 34:   */   public int numEntries()
/* 35:   */   {
/* 36:82 */     return this.numEntries;
/* 37:   */   }
/* 38:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.io.VectorSize
 * JD-Core Version:    0.7.0.1
 */