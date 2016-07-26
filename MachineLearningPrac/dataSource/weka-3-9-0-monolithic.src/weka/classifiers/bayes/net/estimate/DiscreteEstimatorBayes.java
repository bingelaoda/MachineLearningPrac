/*   1:    */ package weka.classifiers.bayes.net.estimate;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import weka.classifiers.bayes.net.search.local.Scoreable;
/*   5:    */ import weka.core.RevisionUtils;
/*   6:    */ import weka.core.Statistics;
/*   7:    */ import weka.core.Utils;
/*   8:    */ import weka.estimators.DiscreteEstimator;
/*   9:    */ import weka.estimators.Estimator;
/*  10:    */ 
/*  11:    */ public class DiscreteEstimatorBayes
/*  12:    */   extends Estimator
/*  13:    */   implements Scoreable
/*  14:    */ {
/*  15:    */   static final long serialVersionUID = 4215400230843212684L;
/*  16:    */   protected double[] m_Counts;
/*  17:    */   protected double m_SumOfCounts;
/*  18: 56 */   protected int m_nSymbols = 0;
/*  19: 61 */   protected double m_fPrior = 0.0D;
/*  20:    */   
/*  21:    */   public DiscreteEstimatorBayes(int nSymbols, double fPrior)
/*  22:    */   {
/*  23: 70 */     this.m_fPrior = fPrior;
/*  24: 71 */     this.m_nSymbols = nSymbols;
/*  25: 72 */     this.m_Counts = new double[this.m_nSymbols];
/*  26: 74 */     for (int iSymbol = 0; iSymbol < this.m_nSymbols; iSymbol++) {
/*  27: 75 */       this.m_Counts[iSymbol] = this.m_fPrior;
/*  28:    */     }
/*  29: 78 */     this.m_SumOfCounts = (this.m_fPrior * this.m_nSymbols);
/*  30:    */   }
/*  31:    */   
/*  32:    */   public void addValue(double data, double weight)
/*  33:    */   {
/*  34: 88 */     this.m_Counts[((int)data)] += weight;
/*  35: 89 */     this.m_SumOfCounts += weight;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public double getProbability(double data)
/*  39:    */   {
/*  40: 99 */     if (this.m_SumOfCounts == 0.0D) {
/*  41:102 */       return 0.0D;
/*  42:    */     }
/*  43:105 */     return this.m_Counts[((int)data)] / this.m_SumOfCounts;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public double getCount(double data)
/*  47:    */   {
/*  48:115 */     if (this.m_SumOfCounts == 0.0D) {
/*  49:117 */       return 0.0D;
/*  50:    */     }
/*  51:120 */     return this.m_Counts[((int)data)];
/*  52:    */   }
/*  53:    */   
/*  54:    */   public int getNumSymbols()
/*  55:    */   {
/*  56:129 */     return this.m_Counts == null ? 0 : this.m_Counts.length;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public double logScore(int nType, int nCardinality)
/*  60:    */   {
/*  61:138 */     double fScore = 0.0D;
/*  62:140 */     switch (nType)
/*  63:    */     {
/*  64:    */     case 0: 
/*  65:143 */       for (int iSymbol = 0; iSymbol < this.m_nSymbols; iSymbol++) {
/*  66:144 */         fScore += Statistics.lnGamma(this.m_Counts[iSymbol]);
/*  67:    */       }
/*  68:147 */       fScore -= Statistics.lnGamma(this.m_SumOfCounts);
/*  69:148 */       if (this.m_fPrior != 0.0D)
/*  70:    */       {
/*  71:149 */         fScore -= this.m_nSymbols * Statistics.lnGamma(this.m_fPrior);
/*  72:150 */         fScore += Statistics.lnGamma(this.m_nSymbols * this.m_fPrior);
/*  73:    */       }
/*  74:    */       break;
/*  75:    */     case 1: 
/*  76:156 */       for (int iSymbol = 0; iSymbol < this.m_nSymbols; iSymbol++) {
/*  77:157 */         fScore += Statistics.lnGamma(this.m_Counts[iSymbol]);
/*  78:    */       }
/*  79:160 */       fScore -= Statistics.lnGamma(this.m_SumOfCounts);
/*  80:    */       
/*  81:    */ 
/*  82:163 */       fScore -= this.m_nSymbols * Statistics.lnGamma(1.0D / (this.m_nSymbols * nCardinality));
/*  83:164 */       fScore += Statistics.lnGamma(1.0D / nCardinality);
/*  84:    */       
/*  85:166 */       break;
/*  86:    */     case 2: 
/*  87:    */     case 3: 
/*  88:    */     case 4: 
/*  89:173 */       for (int iSymbol = 0; iSymbol < this.m_nSymbols; iSymbol++)
/*  90:    */       {
/*  91:174 */         double fP = getProbability(iSymbol);
/*  92:    */         
/*  93:176 */         fScore += this.m_Counts[iSymbol] * Math.log(fP);
/*  94:    */       }
/*  95:180 */       break;
/*  96:    */     }
/*  97:185 */     return fScore;
/*  98:    */   }
/*  99:    */   
/* 100:    */   public String toString()
/* 101:    */   {
/* 102:194 */     String result = "Discrete Estimator. Counts = ";
/* 103:196 */     if (this.m_SumOfCounts > 1.0D)
/* 104:    */     {
/* 105:197 */       for (int i = 0; i < this.m_Counts.length; i++) {
/* 106:198 */         result = result + " " + Utils.doubleToString(this.m_Counts[i], 2);
/* 107:    */       }
/* 108:201 */       result = result + "  (Total = " + Utils.doubleToString(this.m_SumOfCounts, 2) + ")\n";
/* 109:    */     }
/* 110:    */     else
/* 111:    */     {
/* 112:204 */       for (int i = 0; i < this.m_Counts.length; i++) {
/* 113:205 */         result = result + " " + this.m_Counts[i];
/* 114:    */       }
/* 115:208 */       result = result + "  (Total = " + this.m_SumOfCounts + ")\n";
/* 116:    */     }
/* 117:211 */     return result;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public String getRevision()
/* 121:    */   {
/* 122:220 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 123:    */   }
/* 124:    */   
/* 125:    */   public static void main(String[] argv)
/* 126:    */   {
/* 127:    */     try
/* 128:    */     {
/* 129:231 */       if (argv.length == 0)
/* 130:    */       {
/* 131:232 */         System.out.println("Please specify a set of instances.");
/* 132:    */         
/* 133:234 */         return;
/* 134:    */       }
/* 135:237 */       int current = Integer.parseInt(argv[0]);
/* 136:238 */       int max = current;
/* 137:240 */       for (int i = 1; i < argv.length; i++)
/* 138:    */       {
/* 139:241 */         current = Integer.parseInt(argv[i]);
/* 140:243 */         if (current > max) {
/* 141:244 */           max = current;
/* 142:    */         }
/* 143:    */       }
/* 144:248 */       DiscreteEstimator newEst = new DiscreteEstimator(max + 1, true);
/* 145:250 */       for (int i = 0; i < argv.length; i++)
/* 146:    */       {
/* 147:251 */         current = Integer.parseInt(argv[i]);
/* 148:    */         
/* 149:253 */         System.out.println(newEst);
/* 150:254 */         System.out.println("Prediction for " + current + " = " + newEst.getProbability(current));
/* 151:    */         
/* 152:256 */         newEst.addValue(current, 1.0D);
/* 153:    */       }
/* 154:    */     }
/* 155:    */     catch (Exception e)
/* 156:    */     {
/* 157:259 */       System.out.println(e.getMessage());
/* 158:    */     }
/* 159:    */   }
/* 160:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.estimate.DiscreteEstimatorBayes
 * JD-Core Version:    0.7.0.1
 */