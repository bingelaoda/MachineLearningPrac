/*   1:    */ package weka.gui.boundaryvisualizer;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ 
/*   5:    */ public class RemoteResult
/*   6:    */   implements Serializable
/*   7:    */ {
/*   8:    */   private static final long serialVersionUID = 1873271280044633808L;
/*   9:    */   private final double[][] m_probabilities;
/*  10:    */   private int m_percentCompleted;
/*  11:    */   
/*  12:    */   public RemoteResult(int rowNum, int rowLength)
/*  13:    */   {
/*  14: 63 */     this.m_probabilities = new double[rowLength][0];
/*  15:    */   }
/*  16:    */   
/*  17:    */   public void setLocationProbs(int index, double[] distribution)
/*  18:    */   {
/*  19: 74 */     this.m_probabilities[index] = distribution;
/*  20:    */   }
/*  21:    */   
/*  22:    */   public double[][] getProbabilities()
/*  23:    */   {
/*  24: 83 */     return this.m_probabilities;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public void setPercentCompleted(int pc)
/*  28:    */   {
/*  29: 92 */     this.m_percentCompleted = pc;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public int getPercentCompleted()
/*  33:    */   {
/*  34:101 */     return this.m_percentCompleted;
/*  35:    */   }
/*  36:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.boundaryvisualizer.RemoteResult
 * JD-Core Version:    0.7.0.1
 */