/*   1:    */ package weka.attributeSelection;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import weka.core.OptionHandler;
/*   5:    */ import weka.core.RevisionUtils;
/*   6:    */ 
/*   7:    */ public class CostSensitiveAttributeEval
/*   8:    */   extends CostSensitiveASEvaluation
/*   9:    */   implements Serializable, AttributeEvaluator, OptionHandler
/*  10:    */ {
/*  11:    */   static final long serialVersionUID = 4484876541145458447L;
/*  12:    */   
/*  13:    */   public CostSensitiveAttributeEval()
/*  14:    */   {
/*  15:125 */     setEvaluator(new ReliefFAttributeEval());
/*  16:    */   }
/*  17:    */   
/*  18:    */   public String defaultEvaluatorString()
/*  19:    */   {
/*  20:135 */     return "weka.attributeSelection.ReliefFAttributeEval";
/*  21:    */   }
/*  22:    */   
/*  23:    */   public void setEvaluator(ASEvaluation newEvaluator)
/*  24:    */     throws IllegalArgumentException
/*  25:    */   {
/*  26:148 */     if (!(newEvaluator instanceof AttributeEvaluator)) {
/*  27:149 */       throw new IllegalArgumentException("Evaluator must be an AttributeEvaluator!");
/*  28:    */     }
/*  29:153 */     this.m_evaluator = newEvaluator;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public double evaluateAttribute(int attribute)
/*  33:    */     throws Exception
/*  34:    */   {
/*  35:166 */     return ((AttributeEvaluator)this.m_evaluator).evaluateAttribute(attribute);
/*  36:    */   }
/*  37:    */   
/*  38:    */   public String getRevision()
/*  39:    */   {
/*  40:176 */     return RevisionUtils.extract("$Revision: 10337 $");
/*  41:    */   }
/*  42:    */   
/*  43:    */   public static void main(String[] args)
/*  44:    */   {
/*  45:185 */     runEvaluator(new CostSensitiveAttributeEval(), args);
/*  46:    */   }
/*  47:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.CostSensitiveAttributeEval
 * JD-Core Version:    0.7.0.1
 */