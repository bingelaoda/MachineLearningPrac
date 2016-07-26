/*  1:   */ package weka.core.expressionlanguage.common;
/*  2:   */ 
/*  3:   */ import weka.core.expressionlanguage.core.Macro;
/*  4:   */ import weka.core.expressionlanguage.core.MacroDeclarations;
/*  5:   */ 
/*  6:   */ public class MacroDeclarationsCompositor
/*  7:   */   implements MacroDeclarations
/*  8:   */ {
/*  9:   */   private final MacroDeclarations[] declarations;
/* 10:   */   
/* 11:   */   public MacroDeclarationsCompositor(MacroDeclarations... declarations)
/* 12:   */   {
/* 13:57 */     this.declarations = declarations;
/* 14:   */   }
/* 15:   */   
/* 16:   */   public boolean hasMacro(String name)
/* 17:   */   {
/* 18:68 */     for (MacroDeclarations declaration : this.declarations) {
/* 19:69 */       if (declaration.hasMacro(name)) {
/* 20:70 */         return true;
/* 21:   */       }
/* 22:   */     }
/* 23:71 */     return false;
/* 24:   */   }
/* 25:   */   
/* 26:   */   public Macro getMacro(String name)
/* 27:   */   {
/* 28:84 */     for (MacroDeclarations declaration : this.declarations) {
/* 29:85 */       if (declaration.hasMacro(name)) {
/* 30:86 */         return declaration.getMacro(name);
/* 31:   */       }
/* 32:   */     }
/* 33:87 */     throw new RuntimeException("Macro '" + name + "' doesn't exist!");
/* 34:   */   }
/* 35:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.expressionlanguage.common.MacroDeclarationsCompositor
 * JD-Core Version:    0.7.0.1
 */