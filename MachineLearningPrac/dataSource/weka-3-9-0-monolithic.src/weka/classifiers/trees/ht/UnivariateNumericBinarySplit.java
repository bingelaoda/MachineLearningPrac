/*  1:   */ package weka.classifiers.trees.ht;
/*  2:   */ 
/*  3:   */ import java.io.Serializable;
/*  4:   */ import java.util.List;
/*  5:   */ import weka.core.Attribute;
/*  6:   */ import weka.core.Instance;
/*  7:   */ import weka.core.Instances;
/*  8:   */ 
/*  9:   */ public class UnivariateNumericBinarySplit
/* 10:   */   extends Split
/* 11:   */   implements Serializable
/* 12:   */ {
/* 13:   */   private static final long serialVersionUID = -7392204582942741097L;
/* 14:   */   protected double m_splitPoint;
/* 15:   */   
/* 16:   */   public UnivariateNumericBinarySplit(String attName, double splitPoint)
/* 17:   */   {
/* 18:53 */     this.m_splitAttNames.add(attName);
/* 19:54 */     this.m_splitPoint = splitPoint;
/* 20:   */   }
/* 21:   */   
/* 22:   */   public String branchForInstance(Instance inst)
/* 23:   */   {
/* 24:60 */     Attribute att = inst.dataset().attribute((String)this.m_splitAttNames.get(0));
/* 25:61 */     if ((att == null) || (inst.isMissing(att))) {
/* 26:63 */       return null;
/* 27:   */     }
/* 28:66 */     if (inst.value(att) <= this.m_splitPoint) {
/* 29:67 */       return "left";
/* 30:   */     }
/* 31:70 */     return "right";
/* 32:   */   }
/* 33:   */   
/* 34:   */   public String conditionForBranch(String branch)
/* 35:   */   {
/* 36:75 */     String result = (String)this.m_splitAttNames.get(0);
/* 37:77 */     if (branch.equalsIgnoreCase("left")) {
/* 38:78 */       result = result + " <= ";
/* 39:   */     } else {
/* 40:80 */       result = result + " > ";
/* 41:   */     }
/* 42:83 */     result = result + String.format("%-9.3f", new Object[] { Double.valueOf(this.m_splitPoint) });
/* 43:   */     
/* 44:85 */     return result;
/* 45:   */   }
/* 46:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.ht.UnivariateNumericBinarySplit
 * JD-Core Version:    0.7.0.1
 */