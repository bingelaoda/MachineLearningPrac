/*  1:   */ package no.uib.cipr.matrix;
/*  2:   */ 
/*  3:   */ public class NotConvergedException
/*  4:   */   extends Exception
/*  5:   */ {
/*  6:   */   private static final long serialVersionUID = -2305369220010776320L;
/*  7:   */   protected Reason reason;
/*  8:   */   
/*  9:   */   public static enum Reason
/* 10:   */   {
/* 11:38 */     Iterations,  Divergence,  Breakdown;
/* 12:   */     
/* 13:   */     private Reason() {}
/* 14:   */   }
/* 15:   */   
/* 16:   */   public NotConvergedException(Reason reason, String message)
/* 17:   */   {
/* 18:65 */     super(message);
/* 19:66 */     this.reason = reason;
/* 20:   */   }
/* 21:   */   
/* 22:   */   public NotConvergedException(Reason reason)
/* 23:   */   {
/* 24:76 */     this.reason = reason;
/* 25:   */   }
/* 26:   */   
/* 27:   */   public Reason getReason()
/* 28:   */   {
/* 29:83 */     return this.reason;
/* 30:   */   }
/* 31:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.NotConvergedException
 * JD-Core Version:    0.7.0.1
 */