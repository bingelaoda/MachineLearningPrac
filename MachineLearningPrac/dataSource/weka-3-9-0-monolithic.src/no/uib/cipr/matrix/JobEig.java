/*  1:   */ package no.uib.cipr.matrix;
/*  2:   */ 
/*  3:   */  enum JobEig
/*  4:   */ {
/*  5:25 */   All,  Eigenvalues;
/*  6:   */   
/*  7:   */   private JobEig() {}
/*  8:   */   
/*  9:   */   public String netlib()
/* 10:   */   {
/* 11:35 */     if (this == All) {
/* 12:36 */       return "V";
/* 13:   */     }
/* 14:37 */     return "N";
/* 15:   */   }
/* 16:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.JobEig
 * JD-Core Version:    0.7.0.1
 */