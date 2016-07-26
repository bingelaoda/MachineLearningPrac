/*  1:   */ package weka.core.expressionlanguage.common;
/*  2:   */ 
/*  3:   */ import weka.core.expressionlanguage.core.Node;
/*  4:   */ import weka.core.expressionlanguage.core.VariableDeclarations;
/*  5:   */ 
/*  6:   */ public class VariableDeclarationsCompositor
/*  7:   */   implements VariableDeclarations
/*  8:   */ {
/*  9:   */   private VariableDeclarations[] declarations;
/* 10:   */   
/* 11:   */   public VariableDeclarationsCompositor(VariableDeclarations... declarations)
/* 12:   */   {
/* 13:57 */     this.declarations = declarations;
/* 14:   */   }
/* 15:   */   
/* 16:   */   public boolean hasVariable(String name)
/* 17:   */   {
/* 18:68 */     for (VariableDeclarations declaration : this.declarations) {
/* 19:69 */       if (declaration.hasVariable(name)) {
/* 20:70 */         return true;
/* 21:   */       }
/* 22:   */     }
/* 23:71 */     return false;
/* 24:   */   }
/* 25:   */   
/* 26:   */   public Node getVariable(String name)
/* 27:   */   {
/* 28:84 */     for (VariableDeclarations declaration : this.declarations) {
/* 29:85 */       if (declaration.hasVariable(name)) {
/* 30:86 */         return declaration.getVariable(name);
/* 31:   */       }
/* 32:   */     }
/* 33:87 */     throw new RuntimeException("Variable '" + name + "' doesn't exist!");
/* 34:   */   }
/* 35:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.expressionlanguage.common.VariableDeclarationsCompositor
 * JD-Core Version:    0.7.0.1
 */