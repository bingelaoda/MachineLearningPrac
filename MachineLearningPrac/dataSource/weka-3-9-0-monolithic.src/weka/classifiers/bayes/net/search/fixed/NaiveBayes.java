/*  1:   */ package weka.classifiers.bayes.net.search.fixed;
/*  2:   */ 
/*  3:   */ import weka.classifiers.bayes.BayesNet;
/*  4:   */ import weka.classifiers.bayes.net.ParentSet;
/*  5:   */ import weka.classifiers.bayes.net.search.SearchAlgorithm;
/*  6:   */ import weka.core.Instances;
/*  7:   */ import weka.core.RevisionUtils;
/*  8:   */ 
/*  9:   */ public class NaiveBayes
/* 10:   */   extends SearchAlgorithm
/* 11:   */ {
/* 12:   */   static final long serialVersionUID = -4808572519709755811L;
/* 13:   */   
/* 14:   */   public String globalInfo()
/* 15:   */   {
/* 16:52 */     return "The NaiveBayes class generates a fixed Bayes network structure with arrows from the class variable to each of the attribute variables.";
/* 17:   */   }
/* 18:   */   
/* 19:   */   public void buildStructure(BayesNet bayesNet, Instances instances)
/* 20:   */     throws Exception
/* 21:   */   {
/* 22:65 */     for (int iAttribute = 0; iAttribute < instances.numAttributes(); iAttribute++) {
/* 23:66 */       if (iAttribute != instances.classIndex()) {
/* 24:67 */         bayesNet.getParentSet(iAttribute).addParent(instances.classIndex(), instances);
/* 25:   */       }
/* 26:   */     }
/* 27:   */   }
/* 28:   */   
/* 29:   */   public String getRevision()
/* 30:   */   {
/* 31:78 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 32:   */   }
/* 33:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.search.fixed.NaiveBayes
 * JD-Core Version:    0.7.0.1
 */