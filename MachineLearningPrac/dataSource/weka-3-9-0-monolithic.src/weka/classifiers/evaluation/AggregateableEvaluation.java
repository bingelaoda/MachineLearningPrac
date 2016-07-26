/*   1:    */ package weka.classifiers.evaluation;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import weka.classifiers.CostMatrix;
/*   5:    */ import weka.core.Aggregateable;
/*   6:    */ import weka.core.Instances;
/*   7:    */ 
/*   8:    */ public class AggregateableEvaluation
/*   9:    */   extends Evaluation
/*  10:    */   implements Aggregateable<Evaluation>
/*  11:    */ {
/*  12:    */   private static final long serialVersionUID = 8734675926526110924L;
/*  13:    */   
/*  14:    */   public AggregateableEvaluation(Instances data)
/*  15:    */     throws Exception
/*  16:    */   {
/*  17: 51 */     super(data);
/*  18:    */   }
/*  19:    */   
/*  20:    */   public AggregateableEvaluation(Instances data, CostMatrix costMatrix)
/*  21:    */     throws Exception
/*  22:    */   {
/*  23: 62 */     super(data, costMatrix);
/*  24:    */   }
/*  25:    */   
/*  26:    */   public AggregateableEvaluation(Evaluation eval)
/*  27:    */     throws Exception
/*  28:    */   {
/*  29: 72 */     super(eval.m_Header, eval.m_CostMatrix);
/*  30:    */     
/*  31: 74 */     this.m_NoPriors = eval.m_NoPriors;
/*  32: 75 */     this.m_NumTrainClassVals = eval.m_NumTrainClassVals;
/*  33: 76 */     this.m_TrainClassVals = eval.m_TrainClassVals;
/*  34: 77 */     this.m_TrainClassWeights = eval.m_TrainClassWeights;
/*  35: 78 */     this.m_PriorEstimator = eval.m_PriorEstimator;
/*  36: 79 */     this.m_MinTarget = eval.m_MinTarget;
/*  37: 80 */     this.m_MaxTarget = eval.m_MaxTarget;
/*  38: 81 */     this.m_ClassPriorsSum = eval.m_ClassPriorsSum;
/*  39: 82 */     this.m_ClassPriors = eval.m_ClassPriors;
/*  40: 83 */     this.m_MinTarget = eval.m_MinTarget;
/*  41: 84 */     this.m_MaxTarget = eval.m_MaxTarget;
/*  42: 85 */     this.m_TrainClassVals = eval.m_TrainClassVals;
/*  43: 86 */     this.m_TrainClassWeights = eval.m_TrainClassWeights;
/*  44: 87 */     this.m_NumTrainClassVals = eval.m_NumTrainClassVals;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public AggregateableEvaluation aggregate(Evaluation evaluation)
/*  48:    */   {
/*  49: 99 */     this.m_Incorrect += evaluation.incorrect();
/*  50:100 */     this.m_Correct += evaluation.correct();
/*  51:101 */     this.m_Unclassified += evaluation.unclassified();
/*  52:102 */     this.m_MissingClass += evaluation.m_MissingClass;
/*  53:103 */     this.m_WithClass += evaluation.m_WithClass;
/*  54:105 */     if (evaluation.m_ConfusionMatrix != null)
/*  55:    */     {
/*  56:106 */       double[][] newMatrix = evaluation.confusionMatrix();
/*  57:107 */       if (newMatrix != null) {
/*  58:108 */         for (int i = 0; i < this.m_ConfusionMatrix.length; i++) {
/*  59:109 */           for (int j = 0; j < this.m_ConfusionMatrix[i].length; j++) {
/*  60:110 */             this.m_ConfusionMatrix[i][j] += newMatrix[i][j];
/*  61:    */           }
/*  62:    */         }
/*  63:    */       }
/*  64:    */     }
/*  65:116 */     double[] newClassPriors = evaluation.m_ClassPriors;
/*  66:117 */     if ((newClassPriors != null) && (this.m_ClassPriors != null)) {
/*  67:118 */       for (int i = 0; i < this.m_ClassPriors.length; i++) {
/*  68:119 */         this.m_ClassPriors[i] = newClassPriors[i];
/*  69:    */       }
/*  70:    */     }
/*  71:123 */     this.m_ClassPriorsSum = evaluation.m_ClassPriorsSum;
/*  72:124 */     this.m_TotalCost += evaluation.totalCost();
/*  73:125 */     this.m_SumErr += evaluation.m_SumErr;
/*  74:126 */     this.m_SumAbsErr += evaluation.m_SumAbsErr;
/*  75:127 */     this.m_SumSqrErr += evaluation.m_SumSqrErr;
/*  76:128 */     this.m_SumClass += evaluation.m_SumClass;
/*  77:129 */     this.m_SumSqrClass += evaluation.m_SumSqrClass;
/*  78:130 */     this.m_SumPredicted += evaluation.m_SumPredicted;
/*  79:131 */     this.m_SumSqrPredicted += evaluation.m_SumSqrPredicted;
/*  80:132 */     this.m_SumClassPredicted += evaluation.m_SumClassPredicted;
/*  81:133 */     this.m_SumPriorAbsErr += evaluation.m_SumPriorAbsErr;
/*  82:134 */     this.m_SumPriorSqrErr += evaluation.m_SumPriorSqrErr;
/*  83:135 */     this.m_SumKBInfo += evaluation.m_SumKBInfo;
/*  84:136 */     double[] newMarginCounts = evaluation.m_MarginCounts;
/*  85:137 */     if (newMarginCounts != null) {
/*  86:138 */       for (int i = 0; i < this.m_MarginCounts.length; i++) {
/*  87:139 */         this.m_MarginCounts[i] += newMarginCounts[i];
/*  88:    */       }
/*  89:    */     }
/*  90:142 */     this.m_ComplexityStatisticsAvailable = evaluation.m_ComplexityStatisticsAvailable;
/*  91:143 */     this.m_CoverageStatisticsAvailable = evaluation.m_CoverageStatisticsAvailable;
/*  92:144 */     this.m_SumPriorEntropy += evaluation.m_SumPriorEntropy;
/*  93:145 */     this.m_SumSchemeEntropy += evaluation.m_SumSchemeEntropy;
/*  94:146 */     this.m_TotalSizeOfRegions += evaluation.m_TotalSizeOfRegions;
/*  95:147 */     this.m_TotalCoverage += evaluation.m_TotalCoverage;
/*  96:    */     
/*  97:149 */     ArrayList<Prediction> predsToAdd = evaluation.m_Predictions;
/*  98:150 */     if (predsToAdd != null)
/*  99:    */     {
/* 100:151 */       if (this.m_Predictions == null) {
/* 101:152 */         this.m_Predictions = new ArrayList();
/* 102:    */       }
/* 103:154 */       for (int i = 0; i < predsToAdd.size(); i++) {
/* 104:155 */         this.m_Predictions.add(predsToAdd.get(i));
/* 105:    */       }
/* 106:    */     }
/* 107:159 */     return this;
/* 108:    */   }
/* 109:    */   
/* 110:    */   public void finalizeAggregation() {}
/* 111:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.evaluation.AggregateableEvaluation
 * JD-Core Version:    0.7.0.1
 */