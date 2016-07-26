/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Arrays;
/*   5:    */ 
/*   6:    */ public abstract class ConjugateGradientOptimization
/*   7:    */   extends Optimization
/*   8:    */   implements RevisionHandler
/*   9:    */ {
/*  10:    */   public TechnicalInformation getTechnicalInformation()
/*  11:    */   {
/*  12: 56 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  13: 57 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Y.H. Dai and Y. Yuan");
/*  14: 58 */     result.setValue(TechnicalInformation.Field.YEAR, "2001");
/*  15: 59 */     result.setValue(TechnicalInformation.Field.TITLE, "An Efficient Hybrid Conjugate Gradient Method for Unconstrained Optimization");
/*  16:    */     
/*  17:    */ 
/*  18: 62 */     result.setValue(TechnicalInformation.Field.JOURNAL, "Annals of Operations Research");
/*  19: 63 */     result.setValue(TechnicalInformation.Field.VOLUME, "103");
/*  20: 64 */     result.setValue(TechnicalInformation.Field.PAGES, "33-47");
/*  21:    */     
/*  22: 66 */     result.add(TechnicalInformation.Type.ARTICLE);
/*  23: 67 */     result.setValue(TechnicalInformation.Field.AUTHOR, "W.W. Hager and H. Zhang");
/*  24: 68 */     result.setValue(TechnicalInformation.Field.YEAR, "2006");
/*  25: 69 */     result.setValue(TechnicalInformation.Field.TITLE, "A survey of nonlinear conjugate gradient methods");
/*  26:    */     
/*  27: 71 */     result.setValue(TechnicalInformation.Field.JOURNAL, "Pacific Journal of Optimization");
/*  28: 72 */     result.setValue(TechnicalInformation.Field.VOLUME, "2");
/*  29: 73 */     result.setValue(TechnicalInformation.Field.PAGES, "35-58");
/*  30:    */     
/*  31: 75 */     return result;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public ConjugateGradientOptimization()
/*  35:    */   {
/*  36: 83 */     setMaxIteration(2000);
/*  37: 84 */     this.m_BETA = 0.1D;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public double[] findArgmin(double[] initX, double[][] constraints)
/*  41:    */     throws Exception
/*  42:    */   {
/*  43:100 */     int l = initX.length;
/*  44:    */     
/*  45:    */ 
/*  46:103 */     this.m_f = objectiveFunction(initX);
/*  47:104 */     if (Double.isNaN(this.m_f)) {
/*  48:105 */       throw new Exception("Objective function value is NaN!");
/*  49:    */     }
/*  50:109 */     double[] grad = evaluateGradient(initX);double[] deltaX = new double[l];double[] direct = new double[l];double[] x = new double[l];
/*  51:    */     
/*  52:    */ 
/*  53:112 */     double sum = 0.0D;
/*  54:113 */     for (int i = 0; i < grad.length; i++)
/*  55:    */     {
/*  56:114 */       direct[i] = (-grad[i]);
/*  57:115 */       sum += grad[i] * grad[i];
/*  58:    */     }
/*  59:119 */     double stpmax = this.m_STPMX * Math.max(Math.sqrt(sum), l);
/*  60:    */     
/*  61:121 */     boolean[] isFixed = new boolean[initX.length];
/*  62:122 */     Optimization.DynamicIntArray wsBdsIndx = new Optimization.DynamicIntArray(this, initX.length);
/*  63:123 */     double[][] consts = new double[2][initX.length];
/*  64:124 */     for (int i = 0; i < initX.length; i++)
/*  65:    */     {
/*  66:125 */       if ((!Double.isNaN(constraints[0][i])) || (!Double.isNaN(constraints[1][i]))) {
/*  67:127 */         throw new Exception("Cannot deal with constraints, sorry.");
/*  68:    */       }
/*  69:129 */       consts[0][i] = constraints[0][i];
/*  70:130 */       consts[1][i] = constraints[1][i];
/*  71:131 */       x[i] = initX[i];
/*  72:    */     }
/*  73:134 */     boolean finished = false;
/*  74:135 */     for (int step = 0; step < this.m_MAXITS; step++)
/*  75:    */     {
/*  76:137 */       if (this.m_Debug) {
/*  77:138 */         System.err.println("\nIteration # " + step + ":");
/*  78:    */       }
/*  79:141 */       double[] oldX = x;
/*  80:142 */       double[] oldGrad = grad;
/*  81:    */       
/*  82:    */ 
/*  83:145 */       double[] directB = Arrays.copyOf(direct, direct.length);
/*  84:    */       
/*  85:    */ 
/*  86:148 */       this.m_IsZeroStep = false;
/*  87:149 */       x = lnsrch(x, grad, directB, stpmax, isFixed, constraints, wsBdsIndx);
/*  88:150 */       if (this.m_IsZeroStep) {
/*  89:151 */         throw new Exception("Exiting due to zero step.");
/*  90:    */       }
/*  91:154 */       double test = 0.0D;
/*  92:155 */       for (int h = 0; h < x.length; h++)
/*  93:    */       {
/*  94:156 */         x[h] -= oldX[h];
/*  95:157 */         double tmp = Math.abs(deltaX[h]) / Math.max(Math.abs(x[h]), 1.0D);
/*  96:158 */         if (tmp > test) {
/*  97:159 */           test = tmp;
/*  98:    */         }
/*  99:    */       }
/* 100:162 */       if (test < m_Zero)
/* 101:    */       {
/* 102:163 */         if (this.m_Debug) {
/* 103:164 */           System.err.println("\nDeltaX converged: " + test);
/* 104:    */         }
/* 105:166 */         finished = true;
/* 106:167 */         break;
/* 107:    */       }
/* 108:171 */       grad = evaluateGradient(x);
/* 109:172 */       test = 0.0D;
/* 110:173 */       for (int g = 0; g < l; g++)
/* 111:    */       {
/* 112:174 */         double tmp = Math.abs(grad[g]) * Math.max(Math.abs(directB[g]), 1.0D) / Math.max(Math.abs(this.m_f), 1.0D);
/* 113:176 */         if (tmp > test) {
/* 114:177 */           test = tmp;
/* 115:    */         }
/* 116:    */       }
/* 117:181 */       if (test < m_Zero)
/* 118:    */       {
/* 119:182 */         if (this.m_Debug)
/* 120:    */         {
/* 121:183 */           for (int i = 0; i < l; i++) {
/* 122:184 */             System.out.println(grad[i] + " " + directB[i] + " " + this.m_f);
/* 123:    */           }
/* 124:186 */           System.err.println("Gradient converged: " + test);
/* 125:    */         }
/* 126:188 */         finished = true;
/* 127:189 */         break;
/* 128:    */       }
/* 129:193 */       double betaHSNumerator = 0.0D;double betaDYNumerator = 0.0D;
/* 130:194 */       double betaHSandDYDenominator = 0.0D;
/* 131:195 */       for (int i = 0; i < grad.length; i++)
/* 132:    */       {
/* 133:196 */         betaDYNumerator += grad[i] * grad[i];
/* 134:197 */         betaHSNumerator += (grad[i] - oldGrad[i]) * grad[i];
/* 135:198 */         betaHSandDYDenominator += (grad[i] - oldGrad[i]) * direct[i];
/* 136:    */       }
/* 137:200 */       double betaHS = betaHSNumerator / betaHSandDYDenominator;
/* 138:201 */       double betaDY = betaDYNumerator / betaHSandDYDenominator;
/* 139:203 */       if (this.m_Debug)
/* 140:    */       {
/* 141:204 */         System.err.println("Beta HS: " + betaHS);
/* 142:205 */         System.err.println("Beta DY: " + betaDY);
/* 143:    */       }
/* 144:208 */       for (int i = 0; i < direct.length; i++) {
/* 145:209 */         direct[i] = (-grad[i] + Math.max(0.0D, Math.min(betaHS, betaDY)) * direct[i]);
/* 146:    */       }
/* 147:    */     }
/* 148:214 */     if (finished)
/* 149:    */     {
/* 150:215 */       if (this.m_Debug) {
/* 151:216 */         System.err.println("Minimum found.");
/* 152:    */       }
/* 153:218 */       this.m_f = objectiveFunction(x);
/* 154:219 */       if (Double.isNaN(this.m_f)) {
/* 155:220 */         throw new Exception("Objective function value is NaN!");
/* 156:    */       }
/* 157:222 */       return x;
/* 158:    */     }
/* 159:225 */     if (this.m_Debug) {
/* 160:226 */       System.err.println("Cannot find minimum -- too many iterations!");
/* 161:    */     }
/* 162:228 */     this.m_X = x;
/* 163:229 */     return null;
/* 164:    */   }
/* 165:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.ConjugateGradientOptimization
 * JD-Core Version:    0.7.0.1
 */