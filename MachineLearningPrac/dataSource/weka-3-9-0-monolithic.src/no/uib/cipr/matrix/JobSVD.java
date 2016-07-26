/*  1:   */ package no.uib.cipr.matrix;
/*  2:   */ 
/*  3:   */  enum JobSVD
/*  4:   */ {
/*  5:28 */   All,  None,  Overwrite,  Part;
/*  6:   */   
/*  7:   */   private JobSVD() {}
/*  8:   */   
/*  9:   */   public String netlib()
/* 10:   */   {
/* 11:51 */     switch (1.$SwitchMap$no$uib$cipr$matrix$JobSVD[ordinal()])
/* 12:   */     {
/* 13:   */     case 1: 
/* 14:53 */       return "A";
/* 15:   */     case 2: 
/* 16:55 */       return "S";
/* 17:   */     case 3: 
/* 18:57 */       return "O";
/* 19:   */     }
/* 20:59 */     return "N";
/* 21:   */   }
/* 22:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.JobSVD
 * JD-Core Version:    0.7.0.1
 */