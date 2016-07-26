/*  1:   */ package no.uib.cipr.matrix;
/*  2:   */ 
/*  3:   */  enum JobEigRange
/*  4:   */ {
/*  5:25 */   All,  Indices,  Interval;
/*  6:   */   
/*  7:   */   private JobEigRange() {}
/*  8:   */   
/*  9:   */   public String netlib()
/* 10:   */   {
/* 11:38 */     switch (1.$SwitchMap$no$uib$cipr$matrix$JobEigRange[ordinal()])
/* 12:   */     {
/* 13:   */     case 1: 
/* 14:40 */       return "A";
/* 15:   */     case 2: 
/* 16:42 */       return "I";
/* 17:   */     }
/* 18:44 */     return "V";
/* 19:   */   }
/* 20:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.JobEigRange
 * JD-Core Version:    0.7.0.1
 */