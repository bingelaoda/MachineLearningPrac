/*   1:    */ package weka.estimators;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import weka.core.RevisionUtils;
/*   5:    */ 
/*   6:    */ public class DNConditionalEstimator
/*   7:    */   implements ConditionalEstimator
/*   8:    */ {
/*   9:    */   private NormalEstimator[] m_Estimators;
/*  10:    */   private DiscreteEstimator m_Weights;
/*  11:    */   
/*  12:    */   public DNConditionalEstimator(int numSymbols, double precision)
/*  13:    */   {
/*  14: 51 */     this.m_Estimators = new NormalEstimator[numSymbols];
/*  15: 52 */     for (int i = 0; i < numSymbols; i++) {
/*  16: 53 */       this.m_Estimators[i] = new NormalEstimator(precision);
/*  17:    */     }
/*  18: 55 */     this.m_Weights = new DiscreteEstimator(numSymbols, true);
/*  19:    */   }
/*  20:    */   
/*  21:    */   public void addValue(double data, double given, double weight)
/*  22:    */   {
/*  23: 67 */     this.m_Estimators[((int)data)].addValue(given, weight);
/*  24: 68 */     this.m_Weights.addValue((int)data, weight);
/*  25:    */   }
/*  26:    */   
/*  27:    */   public Estimator getEstimator(double given)
/*  28:    */   {
/*  29: 79 */     Estimator result = new DiscreteEstimator(this.m_Estimators.length, false);
/*  30: 80 */     for (int i = 0; i < this.m_Estimators.length; i++) {
/*  31: 81 */       result.addValue(i, this.m_Weights.getProbability(i) * this.m_Estimators[i].getProbability(given));
/*  32:    */     }
/*  33: 84 */     return result;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public double getProbability(double data, double given)
/*  37:    */   {
/*  38: 96 */     return getEstimator(given).getProbability(data);
/*  39:    */   }
/*  40:    */   
/*  41:    */   public String toString()
/*  42:    */   {
/*  43:102 */     String result = "DN Conditional Estimator. " + this.m_Estimators.length + " sub-estimators:\n";
/*  44:104 */     for (int i = 0; i < this.m_Estimators.length; i++) {
/*  45:105 */       result = result + "Sub-estimator " + i + ": " + this.m_Estimators[i];
/*  46:    */     }
/*  47:107 */     result = result + "Weights of each estimator given by " + this.m_Weights;
/*  48:108 */     return result;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public String getRevision()
/*  52:    */   {
/*  53:117 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  54:    */   }
/*  55:    */   
/*  56:    */   public static void main(String[] argv)
/*  57:    */   {
/*  58:    */     try
/*  59:    */     {
/*  60:129 */       if (argv.length == 0)
/*  61:    */       {
/*  62:130 */         System.out.println("Please specify a set of instances.");
/*  63:131 */         return;
/*  64:    */       }
/*  65:133 */       int currentA = Integer.parseInt(argv[0]);
/*  66:134 */       int maxA = currentA;
/*  67:135 */       int currentB = Integer.parseInt(argv[1]);
/*  68:136 */       int maxB = currentB;
/*  69:137 */       for (int i = 2; i < argv.length - 1; i += 2)
/*  70:    */       {
/*  71:138 */         currentA = Integer.parseInt(argv[i]);
/*  72:139 */         currentB = Integer.parseInt(argv[(i + 1)]);
/*  73:140 */         if (currentA > maxA) {
/*  74:141 */           maxA = currentA;
/*  75:    */         }
/*  76:143 */         if (currentB > maxB) {
/*  77:144 */           maxB = currentB;
/*  78:    */         }
/*  79:    */       }
/*  80:147 */       DNConditionalEstimator newEst = new DNConditionalEstimator(maxA + 1, 1.0D);
/*  81:149 */       for (int i = 0; i < argv.length - 1; i += 2)
/*  82:    */       {
/*  83:150 */         currentA = Integer.parseInt(argv[i]);
/*  84:151 */         currentB = Integer.parseInt(argv[(i + 1)]);
/*  85:152 */         System.out.println(newEst);
/*  86:153 */         System.out.println("Prediction for " + currentA + '|' + currentB + " = " + newEst.getProbability(currentA, currentB));
/*  87:    */         
/*  88:    */ 
/*  89:156 */         newEst.addValue(currentA, currentB, 1.0D);
/*  90:    */       }
/*  91:    */     }
/*  92:    */     catch (Exception e)
/*  93:    */     {
/*  94:159 */       System.out.println(e.getMessage());
/*  95:    */     }
/*  96:    */   }
/*  97:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.estimators.DNConditionalEstimator
 * JD-Core Version:    0.7.0.1
 */