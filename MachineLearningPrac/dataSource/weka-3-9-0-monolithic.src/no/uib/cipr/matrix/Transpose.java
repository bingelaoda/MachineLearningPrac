/*  1:   */ package no.uib.cipr.matrix;
/*  2:   */ 
/*  3:   */  enum Transpose
/*  4:   */ {
/*  5:25 */   NoTranspose,  Transpose;
/*  6:   */   
/*  7:   */   private Transpose() {}
/*  8:   */   
/*  9:   */   public String netlib()
/* 10:   */   {
/* 11:35 */     if (this == NoTranspose) {
/* 12:36 */       return "N";
/* 13:   */     }
/* 14:37 */     return "T";
/* 15:   */   }
/* 16:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.Transpose
 * JD-Core Version:    0.7.0.1
 */