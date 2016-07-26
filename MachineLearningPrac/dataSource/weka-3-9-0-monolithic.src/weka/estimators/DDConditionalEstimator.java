/*   1:    */ package weka.estimators;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import weka.core.RevisionUtils;
/*   5:    */ 
/*   6:    */ public class DDConditionalEstimator
/*   7:    */   implements ConditionalEstimator
/*   8:    */ {
/*   9:    */   private DiscreteEstimator[] m_Estimators;
/*  10:    */   
/*  11:    */   public DDConditionalEstimator(int numSymbols, int numCondSymbols, boolean laplace)
/*  12:    */   {
/*  13: 49 */     this.m_Estimators = new DiscreteEstimator[numCondSymbols];
/*  14: 50 */     for (int i = 0; i < numCondSymbols; i++) {
/*  15: 51 */       this.m_Estimators[i] = new DiscreteEstimator(numSymbols, laplace);
/*  16:    */     }
/*  17:    */   }
/*  18:    */   
/*  19:    */   public void addValue(double data, double given, double weight)
/*  20:    */   {
/*  21: 64 */     this.m_Estimators[((int)given)].addValue(data, weight);
/*  22:    */   }
/*  23:    */   
/*  24:    */   public Estimator getEstimator(double given)
/*  25:    */   {
/*  26: 75 */     return this.m_Estimators[((int)given)];
/*  27:    */   }
/*  28:    */   
/*  29:    */   public double getProbability(double data, double given)
/*  30:    */   {
/*  31: 87 */     return getEstimator(given).getProbability(data);
/*  32:    */   }
/*  33:    */   
/*  34:    */   public String toString()
/*  35:    */   {
/*  36: 93 */     String result = "DD Conditional Estimator. " + this.m_Estimators.length + " sub-estimators:\n";
/*  37: 95 */     for (int i = 0; i < this.m_Estimators.length; i++) {
/*  38: 96 */       result = result + "Sub-estimator " + i + ": " + this.m_Estimators[i];
/*  39:    */     }
/*  40: 98 */     return result;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public String getRevision()
/*  44:    */   {
/*  45:107 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  46:    */   }
/*  47:    */   
/*  48:    */   public static void main(String[] argv)
/*  49:    */   {
/*  50:    */     try
/*  51:    */     {
/*  52:119 */       if (argv.length == 0)
/*  53:    */       {
/*  54:120 */         System.out.println("Please specify a set of instances.");
/*  55:121 */         return;
/*  56:    */       }
/*  57:123 */       int currentA = Integer.parseInt(argv[0]);
/*  58:124 */       int maxA = currentA;
/*  59:125 */       int currentB = Integer.parseInt(argv[1]);
/*  60:126 */       int maxB = currentB;
/*  61:127 */       for (int i = 2; i < argv.length - 1; i += 2)
/*  62:    */       {
/*  63:128 */         currentA = Integer.parseInt(argv[i]);
/*  64:129 */         currentB = Integer.parseInt(argv[(i + 1)]);
/*  65:130 */         if (currentA > maxA) {
/*  66:131 */           maxA = currentA;
/*  67:    */         }
/*  68:133 */         if (currentB > maxB) {
/*  69:134 */           maxB = currentB;
/*  70:    */         }
/*  71:    */       }
/*  72:137 */       DDConditionalEstimator newEst = new DDConditionalEstimator(maxA + 1, maxB + 1, true);
/*  73:140 */       for (int i = 0; i < argv.length - 1; i += 2)
/*  74:    */       {
/*  75:141 */         currentA = Integer.parseInt(argv[i]);
/*  76:142 */         currentB = Integer.parseInt(argv[(i + 1)]);
/*  77:143 */         System.out.println(newEst);
/*  78:144 */         System.out.println("Prediction for " + currentA + '|' + currentB + " = " + newEst.getProbability(currentA, currentB));
/*  79:    */         
/*  80:    */ 
/*  81:147 */         newEst.addValue(currentA, currentB, 1.0D);
/*  82:    */       }
/*  83:    */     }
/*  84:    */     catch (Exception e)
/*  85:    */     {
/*  86:150 */       System.out.println(e.getMessage());
/*  87:    */     }
/*  88:    */   }
/*  89:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.estimators.DDConditionalEstimator
 * JD-Core Version:    0.7.0.1
 */