/*  1:   */ package weka.classifiers.trees.j48;
/*  2:   */ 
/*  3:   */ import java.io.Serializable;
/*  4:   */ import weka.core.Instances;
/*  5:   */ import weka.core.RevisionHandler;
/*  6:   */ 
/*  7:   */ public abstract class ModelSelection
/*  8:   */   implements Serializable, RevisionHandler
/*  9:   */ {
/* 10:   */   private static final long serialVersionUID = -4850147125096133642L;
/* 11:   */   
/* 12:   */   public abstract ClassifierSplitModel selectModel(Instances paramInstances)
/* 13:   */     throws Exception;
/* 14:   */   
/* 15:   */   public ClassifierSplitModel selectModel(Instances train, Instances test)
/* 16:   */     throws Exception
/* 17:   */   {
/* 18:56 */     throw new Exception("Model selection method not implemented");
/* 19:   */   }
/* 20:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.j48.ModelSelection
 * JD-Core Version:    0.7.0.1
 */