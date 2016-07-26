/*   1:    */ package weka.attributeSelection;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.BitSet;
/*   5:    */ import weka.core.OptionHandler;
/*   6:    */ import weka.core.RevisionUtils;
/*   7:    */ 
/*   8:    */ public class CostSensitiveSubsetEval
/*   9:    */   extends CostSensitiveASEvaluation
/*  10:    */   implements Serializable, SubsetEvaluator, OptionHandler
/*  11:    */ {
/*  12:    */   static final long serialVersionUID = 2924546096103426700L;
/*  13:    */   
/*  14:    */   public CostSensitiveSubsetEval()
/*  15:    */   {
/*  16: 86 */     setEvaluator(new CfsSubsetEval());
/*  17:    */   }
/*  18:    */   
/*  19:    */   public void setEvaluator(ASEvaluation newEvaluator)
/*  20:    */     throws IllegalArgumentException
/*  21:    */   {
/*  22: 96 */     if (!(newEvaluator instanceof SubsetEvaluator)) {
/*  23: 97 */       throw new IllegalArgumentException("Evaluator must be an SubsetEvaluator!");
/*  24:    */     }
/*  25:100 */     this.m_evaluator = newEvaluator;
/*  26:    */   }
/*  27:    */   
/*  28:    */   public double evaluateSubset(BitSet subset)
/*  29:    */     throws Exception
/*  30:    */   {
/*  31:113 */     return ((SubsetEvaluator)this.m_evaluator).evaluateSubset(subset);
/*  32:    */   }
/*  33:    */   
/*  34:    */   public String getRevision()
/*  35:    */   {
/*  36:122 */     return RevisionUtils.extract("$Revision: 8109 $");
/*  37:    */   }
/*  38:    */   
/*  39:    */   public static void main(String[] args)
/*  40:    */   {
/*  41:131 */     runEvaluator(new CostSensitiveSubsetEval(), args);
/*  42:    */   }
/*  43:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.CostSensitiveSubsetEval
 * JD-Core Version:    0.7.0.1
 */