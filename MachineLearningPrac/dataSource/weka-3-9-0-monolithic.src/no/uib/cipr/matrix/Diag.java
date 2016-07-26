/*  1:   */ package no.uib.cipr.matrix;
/*  2:   */ 
/*  3:   */  enum Diag
/*  4:   */ {
/*  5:25 */   NonUnit,  Unit;
/*  6:   */   
/*  7:   */   private Diag() {}
/*  8:   */   
/*  9:   */   public String netlib()
/* 10:   */   {
/* 11:35 */     if (this == NonUnit) {
/* 12:36 */       return "N";
/* 13:   */     }
/* 14:37 */     return "U";
/* 15:   */   }
/* 16:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.Diag
 * JD-Core Version:    0.7.0.1
 */