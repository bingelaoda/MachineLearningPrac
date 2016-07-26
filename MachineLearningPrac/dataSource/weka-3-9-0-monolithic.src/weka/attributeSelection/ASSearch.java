/*   1:    */ package weka.attributeSelection;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import weka.core.Instances;
/*   5:    */ import weka.core.RevisionHandler;
/*   6:    */ import weka.core.RevisionUtils;
/*   7:    */ import weka.core.SerializedObject;
/*   8:    */ import weka.core.Utils;
/*   9:    */ 
/*  10:    */ public abstract class ASSearch
/*  11:    */   implements Serializable, RevisionHandler
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = 7591673350342236548L;
/*  14:    */   
/*  15:    */   public String getRevision()
/*  16:    */   {
/*  17: 54 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  18:    */   }
/*  19:    */   
/*  20:    */   public abstract int[] search(ASEvaluation paramASEvaluation, Instances paramInstances)
/*  21:    */     throws Exception;
/*  22:    */   
/*  23:    */   public static ASSearch forName(String searchName, String[] options)
/*  24:    */     throws Exception
/*  25:    */   {
/*  26: 83 */     return (ASSearch)Utils.forName(ASSearch.class, searchName, options);
/*  27:    */   }
/*  28:    */   
/*  29:    */   public static ASSearch[] makeCopies(ASSearch model, int num)
/*  30:    */     throws Exception
/*  31:    */   {
/*  32:101 */     if (model == null) {
/*  33:102 */       throw new Exception("No model search scheme set");
/*  34:    */     }
/*  35:104 */     ASSearch[] result = new ASSearch[num];
/*  36:105 */     SerializedObject so = new SerializedObject(model);
/*  37:106 */     for (int i = 0; i < result.length; i++) {
/*  38:107 */       result[i] = ((ASSearch)so.getObject());
/*  39:    */     }
/*  40:109 */     return result;
/*  41:    */   }
/*  42:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.ASSearch
 * JD-Core Version:    0.7.0.1
 */