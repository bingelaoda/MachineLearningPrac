/*   1:    */ package weka.classifiers.evaluation;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import weka.core.RevisionHandler;
/*   5:    */ import weka.core.RevisionUtils;
/*   6:    */ 
/*   7:    */ public class NominalPrediction
/*   8:    */   implements Prediction, Serializable, RevisionHandler
/*   9:    */ {
/*  10:    */   static final long serialVersionUID = -8871333992740492788L;
/*  11:    */   private double[] m_Distribution;
/*  12: 49 */   private double m_Actual = MISSING_VALUE;
/*  13: 52 */   private double m_Predicted = MISSING_VALUE;
/*  14: 55 */   private double m_Weight = 1.0D;
/*  15:    */   
/*  16:    */   public NominalPrediction(double actual, double[] distribution)
/*  17:    */   {
/*  18: 66 */     this(actual, distribution, 1.0D);
/*  19:    */   }
/*  20:    */   
/*  21:    */   public NominalPrediction(double actual, double[] distribution, double weight)
/*  22:    */   {
/*  23: 80 */     if (distribution == null) {
/*  24: 81 */       throw new NullPointerException("Null distribution in NominalPrediction.");
/*  25:    */     }
/*  26: 83 */     this.m_Actual = actual;
/*  27: 84 */     this.m_Distribution = ((double[])distribution.clone());
/*  28: 85 */     this.m_Weight = weight;
/*  29: 86 */     updatePredicted();
/*  30:    */   }
/*  31:    */   
/*  32:    */   public double[] distribution()
/*  33:    */   {
/*  34: 96 */     return this.m_Distribution;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public double actual()
/*  38:    */   {
/*  39:107 */     return this.m_Actual;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public double predicted()
/*  43:    */   {
/*  44:118 */     return this.m_Predicted;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public double weight()
/*  48:    */   {
/*  49:129 */     return this.m_Weight;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public double margin()
/*  53:    */   {
/*  54:143 */     if ((this.m_Actual == MISSING_VALUE) || (this.m_Predicted == MISSING_VALUE)) {
/*  55:145 */       return MISSING_VALUE;
/*  56:    */     }
/*  57:147 */     double probActual = this.m_Distribution[((int)this.m_Actual)];
/*  58:148 */     double probNext = 0.0D;
/*  59:149 */     for (int i = 0; i < this.m_Distribution.length; i++) {
/*  60:150 */       if ((i != this.m_Actual) && (this.m_Distribution[i] > probNext)) {
/*  61:152 */         probNext = this.m_Distribution[i];
/*  62:    */       }
/*  63:    */     }
/*  64:154 */     return probActual - probNext;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public static double[] makeDistribution(double predictedClass, int numClasses)
/*  68:    */   {
/*  69:172 */     double[] dist = new double[numClasses];
/*  70:173 */     if (predictedClass == MISSING_VALUE) {
/*  71:174 */       return dist;
/*  72:    */     }
/*  73:176 */     dist[((int)predictedClass)] = 1.0D;
/*  74:177 */     return dist;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public static double[] makeUniformDistribution(int numClasses)
/*  78:    */   {
/*  79:190 */     double[] dist = new double[numClasses];
/*  80:191 */     for (int i = 0; i < numClasses; i++) {
/*  81:192 */       dist[i] = (1.0D / numClasses);
/*  82:    */     }
/*  83:194 */     return dist;
/*  84:    */   }
/*  85:    */   
/*  86:    */   private void updatePredicted()
/*  87:    */   {
/*  88:205 */     int predictedClass = -1;
/*  89:206 */     double bestProb = 0.0D;
/*  90:207 */     for (int i = 0; i < this.m_Distribution.length; i++) {
/*  91:208 */       if (this.m_Distribution[i] > bestProb)
/*  92:    */       {
/*  93:209 */         predictedClass = i;
/*  94:210 */         bestProb = this.m_Distribution[i];
/*  95:    */       }
/*  96:    */     }
/*  97:214 */     if (predictedClass != -1) {
/*  98:215 */       this.m_Predicted = predictedClass;
/*  99:    */     } else {
/* 100:217 */       this.m_Predicted = MISSING_VALUE;
/* 101:    */     }
/* 102:    */   }
/* 103:    */   
/* 104:    */   public String toString()
/* 105:    */   {
/* 106:228 */     StringBuffer sb = new StringBuffer();
/* 107:229 */     sb.append("NOM: ").append(actual()).append(" ").append(predicted());
/* 108:230 */     sb.append(' ').append(weight());
/* 109:231 */     double[] dist = distribution();
/* 110:232 */     for (int i = 0; i < dist.length; i++) {
/* 111:233 */       sb.append(' ').append(dist[i]);
/* 112:    */     }
/* 113:235 */     return sb.toString();
/* 114:    */   }
/* 115:    */   
/* 116:    */   public String getRevision()
/* 117:    */   {
/* 118:244 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 119:    */   }
/* 120:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.evaluation.NominalPrediction
 * JD-Core Version:    0.7.0.1
 */