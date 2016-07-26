/*  1:   */ package weka.core.expressionlanguage.common;
/*  2:   */ 
/*  3:   */ import weka.core.expressionlanguage.core.Node;
/*  4:   */ import weka.core.expressionlanguage.core.VariableDeclarations;
/*  5:   */ 
/*  6:   */ public class NoVariables
/*  7:   */   implements VariableDeclarations
/*  8:   */ {
/*  9:   */   public boolean hasVariable(String name)
/* 10:   */   {
/* 11:43 */     return false;
/* 12:   */   }
/* 13:   */   
/* 14:   */   public Node getVariable(String name)
/* 15:   */   {
/* 16:56 */     throw new RuntimeException("Variable '" + name + "' doesn't exist!");
/* 17:   */   }
/* 18:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.expressionlanguage.common.NoVariables
 * JD-Core Version:    0.7.0.1
 */