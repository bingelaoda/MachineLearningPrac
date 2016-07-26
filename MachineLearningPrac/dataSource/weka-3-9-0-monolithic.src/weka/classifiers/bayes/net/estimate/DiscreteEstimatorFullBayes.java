/*   1:    */ package weka.classifiers.bayes.net.estimate;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import weka.core.RevisionUtils;
/*   5:    */ import weka.estimators.DiscreteEstimator;
/*   6:    */ 
/*   7:    */ public class DiscreteEstimatorFullBayes
/*   8:    */   extends DiscreteEstimatorBayes
/*   9:    */ {
/*  10:    */   static final long serialVersionUID = 6774941981423312133L;
/*  11:    */   
/*  12:    */   public DiscreteEstimatorFullBayes(int nSymbols, double w1, double w2, DiscreteEstimatorBayes EmptyDist, DiscreteEstimatorBayes ClassDist, double fPrior)
/*  13:    */   {
/*  14: 54 */     super(nSymbols, fPrior);
/*  15:    */     
/*  16: 56 */     this.m_SumOfCounts = 0.0D;
/*  17: 57 */     for (int iSymbol = 0; iSymbol < this.m_nSymbols; iSymbol++)
/*  18:    */     {
/*  19: 58 */       double p1 = EmptyDist.getProbability(iSymbol);
/*  20: 59 */       double p2 = ClassDist.getProbability(iSymbol);
/*  21: 60 */       this.m_Counts[iSymbol] = (w1 * p1 + w2 * p2);
/*  22: 61 */       this.m_SumOfCounts += this.m_Counts[iSymbol];
/*  23:    */     }
/*  24:    */   }
/*  25:    */   
/*  26:    */   public String getRevision()
/*  27:    */   {
/*  28: 71 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  29:    */   }
/*  30:    */   
/*  31:    */   public static void main(String[] argv)
/*  32:    */   {
/*  33:    */     try
/*  34:    */     {
/*  35: 82 */       if (argv.length == 0)
/*  36:    */       {
/*  37: 83 */         System.out.println("Please specify a set of instances.");
/*  38:    */         
/*  39: 85 */         return;
/*  40:    */       }
/*  41: 88 */       int current = Integer.parseInt(argv[0]);
/*  42: 89 */       int max = current;
/*  43: 91 */       for (int i = 1; i < argv.length; i++)
/*  44:    */       {
/*  45: 92 */         current = Integer.parseInt(argv[i]);
/*  46: 94 */         if (current > max) {
/*  47: 95 */           max = current;
/*  48:    */         }
/*  49:    */       }
/*  50: 99 */       DiscreteEstimator newEst = new DiscreteEstimator(max + 1, true);
/*  51:101 */       for (int i = 0; i < argv.length; i++)
/*  52:    */       {
/*  53:102 */         current = Integer.parseInt(argv[i]);
/*  54:    */         
/*  55:104 */         System.out.println(newEst);
/*  56:105 */         System.out.println("Prediction for " + current + " = " + newEst.getProbability(current));
/*  57:    */         
/*  58:107 */         newEst.addValue(current, 1.0D);
/*  59:    */       }
/*  60:    */     }
/*  61:    */     catch (Exception e)
/*  62:    */     {
/*  63:110 */       System.out.println(e.getMessage());
/*  64:    */     }
/*  65:    */   }
/*  66:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.estimate.DiscreteEstimatorFullBayes
 * JD-Core Version:    0.7.0.1
 */