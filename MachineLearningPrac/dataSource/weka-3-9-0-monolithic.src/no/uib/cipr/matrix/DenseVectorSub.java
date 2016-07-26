/*  1:   */ package no.uib.cipr.matrix;
/*  2:   */ 
/*  3:   */ import java.util.Arrays;
/*  4:   */ 
/*  5:   */ public class DenseVectorSub
/*  6:   */   extends AbstractVector
/*  7:   */ {
/*  8:   */   private DenseVector wrapped;
/*  9:   */   private int offset;
/* 10:   */   
/* 11:   */   public DenseVectorSub(DenseVector wrapped, int offset, int size)
/* 12:   */   {
/* 13:21 */     super(size);
/* 14:22 */     if (offset + size > wrapped.size) {
/* 15:23 */       throw new IllegalArgumentException(offset + "+" + size + ">" + wrapped.size);
/* 16:   */     }
/* 17:25 */     this.offset = offset;
/* 18:26 */     this.wrapped = wrapped;
/* 19:   */   }
/* 20:   */   
/* 21:   */   public double get(int index)
/* 22:   */   {
/* 23:31 */     check(index);
/* 24:32 */     return this.wrapped.get(this.offset + index);
/* 25:   */   }
/* 26:   */   
/* 27:   */   public void set(int index, double value)
/* 28:   */   {
/* 29:37 */     check(index);
/* 30:38 */     this.wrapped.set(this.offset + index, value);
/* 31:   */   }
/* 32:   */   
/* 33:   */   public DenseVector copy()
/* 34:   */   {
/* 35:43 */     double[] data = Arrays.copyOfRange(this.wrapped.getData(), this.offset, this.offset + this.size);
/* 36:   */     
/* 37:45 */     return new DenseVector(data, false);
/* 38:   */   }
/* 39:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.DenseVectorSub
 * JD-Core Version:    0.7.0.1
 */