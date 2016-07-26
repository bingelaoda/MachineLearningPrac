/*  1:   */ package weka.classifiers;
/*  2:   */ 
/*  3:   */ import weka.core.Instances;
/*  4:   */ 
/*  5:   */ public class AggregateableEvaluation
/*  6:   */   extends Evaluation
/*  7:   */ {
/*  8:   */   private static final long serialVersionUID = 6850546230173753210L;
/*  9:   */   
/* 10:   */   public AggregateableEvaluation(Instances data)
/* 11:   */     throws Exception
/* 12:   */   {
/* 13:45 */     super(data);
/* 14:46 */     this.m_delegate = new weka.classifiers.evaluation.AggregateableEvaluation(data);
/* 15:   */   }
/* 16:   */   
/* 17:   */   public AggregateableEvaluation(Instances data, CostMatrix costMatrix)
/* 18:   */     throws Exception
/* 19:   */   {
/* 20:58 */     super(data, costMatrix);
/* 21:59 */     this.m_delegate = new weka.classifiers.evaluation.AggregateableEvaluation(data, costMatrix);
/* 22:   */   }
/* 23:   */   
/* 24:   */   public AggregateableEvaluation(Evaluation eval)
/* 25:   */     throws Exception
/* 26:   */   {
/* 27:70 */     super(eval.getHeader());
/* 28:71 */     this.m_delegate = new weka.classifiers.evaluation.AggregateableEvaluation(eval.m_delegate);
/* 29:   */   }
/* 30:   */   
/* 31:   */   public void aggregate(Evaluation evaluation)
/* 32:   */   {
/* 33:83 */     ((weka.classifiers.evaluation.AggregateableEvaluation)this.m_delegate).aggregate(evaluation.m_delegate);
/* 34:   */   }
/* 35:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.AggregateableEvaluation
 * JD-Core Version:    0.7.0.1
 */