/*  1:   */ package no.uib.cipr.matrix;
/*  2:   */ 
/*  3:   */  enum Side
/*  4:   */ {
/*  5:25 */   Left,  Right;
/*  6:   */   
/*  7:   */   private Side() {}
/*  8:   */   
/*  9:   */   public String netlib()
/* 10:   */   {
/* 11:35 */     if (this == Left) {
/* 12:36 */       return "L";
/* 13:   */     }
/* 14:37 */     return "R";
/* 15:   */   }
/* 16:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.Side
 * JD-Core Version:    0.7.0.1
 */