/*  1:   */ package no.uib.cipr.matrix;
/*  2:   */ 
/*  3:   */  enum UpLo
/*  4:   */ {
/*  5:26 */   Upper,  Lower;
/*  6:   */   
/*  7:   */   private UpLo() {}
/*  8:   */   
/*  9:   */   public String netlib()
/* 10:   */   {
/* 11:33 */     if (this == Upper) {
/* 12:34 */       return "U";
/* 13:   */     }
/* 14:35 */     return "L";
/* 15:   */   }
/* 16:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.UpLo
 * JD-Core Version:    0.7.0.1
 */