/*   1:    */ package weka.classifiers.bayes;
/*   2:    */ 
/*   3:    */ import weka.classifiers.UpdateableClassifier;
/*   4:    */ import weka.core.RevisionUtils;
/*   5:    */ import weka.core.TechnicalInformation;
/*   6:    */ 
/*   7:    */ public class NaiveBayesUpdateable
/*   8:    */   extends NaiveBayes
/*   9:    */   implements UpdateableClassifier
/*  10:    */ {
/*  11:    */   static final long serialVersionUID = -5354015843807192221L;
/*  12:    */   
/*  13:    */   public String globalInfo()
/*  14:    */   {
/*  15: 88 */     return "Class for a Naive Bayes classifier using estimator classes. This is the updateable version of NaiveBayes.\nThis classifier will use a default precision of 0.1 for numeric attributes when buildClassifier is called with zero training instances.\n\nFor more information on Naive Bayes classifiers, see\n\n" + getTechnicalInformation().toString();
/*  16:    */   }
/*  17:    */   
/*  18:    */   public TechnicalInformation getTechnicalInformation()
/*  19:    */   {
/*  20:104 */     return super.getTechnicalInformation();
/*  21:    */   }
/*  22:    */   
/*  23:    */   public void setUseSupervisedDiscretization(boolean newblah)
/*  24:    */   {
/*  25:114 */     if (newblah) {
/*  26:115 */       throw new IllegalArgumentException("Can't use discretization in NaiveBayesUpdateable!");
/*  27:    */     }
/*  28:118 */     this.m_UseDiscretization = false;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public String getRevision()
/*  32:    */   {
/*  33:127 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  34:    */   }
/*  35:    */   
/*  36:    */   public static void main(String[] argv)
/*  37:    */   {
/*  38:136 */     runClassifier(new NaiveBayesUpdateable(), argv);
/*  39:    */   }
/*  40:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.NaiveBayesUpdateable
 * JD-Core Version:    0.7.0.1
 */