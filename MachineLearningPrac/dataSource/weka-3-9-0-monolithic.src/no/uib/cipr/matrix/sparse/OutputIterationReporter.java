/*  1:   */ package no.uib.cipr.matrix.sparse;
/*  2:   */ 
/*  3:   */ import java.io.OutputStream;
/*  4:   */ import java.io.PrintWriter;
/*  5:   */ import no.uib.cipr.matrix.Vector;
/*  6:   */ 
/*  7:   */ public class OutputIterationReporter
/*  8:   */   implements IterationReporter
/*  9:   */ {
/* 10:   */   private PrintWriter out;
/* 11:   */   
/* 12:   */   public OutputIterationReporter(OutputStream out)
/* 13:   */   {
/* 14:45 */     this.out = new PrintWriter(out, true);
/* 15:   */   }
/* 16:   */   
/* 17:   */   public OutputIterationReporter()
/* 18:   */   {
/* 19:52 */     this(System.err);
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void monitor(double r, int i)
/* 23:   */   {
/* 24:56 */     this.out.format("%10d % .12e\n", new Object[] { Integer.valueOf(i), Double.valueOf(r) });
/* 25:57 */     this.out.flush();
/* 26:   */   }
/* 27:   */   
/* 28:   */   public void monitor(double r, Vector x, int i)
/* 29:   */   {
/* 30:61 */     monitor(r, i);
/* 31:   */   }
/* 32:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.OutputIterationReporter
 * JD-Core Version:    0.7.0.1
 */