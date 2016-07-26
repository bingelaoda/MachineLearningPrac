/*  1:   */ package no.uib.cipr.matrix;
/*  2:   */ 
/*  3:   */ public class MatrixNotSPDException
/*  4:   */   extends RuntimeException
/*  5:   */ {
/*  6:   */   private static final long serialVersionUID = 4806417891899193518L;
/*  7:   */   
/*  8:   */   public MatrixNotSPDException() {}
/*  9:   */   
/* 10:   */   public MatrixNotSPDException(String message)
/* 11:   */   {
/* 12:44 */     super(message);
/* 13:   */   }
/* 14:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.MatrixNotSPDException
 * JD-Core Version:    0.7.0.1
 */