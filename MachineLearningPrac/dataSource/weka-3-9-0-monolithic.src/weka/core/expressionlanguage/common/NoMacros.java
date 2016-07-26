/*  1:   */ package weka.core.expressionlanguage.common;
/*  2:   */ 
/*  3:   */ import weka.core.expressionlanguage.core.Macro;
/*  4:   */ import weka.core.expressionlanguage.core.MacroDeclarations;
/*  5:   */ 
/*  6:   */ public class NoMacros
/*  7:   */   implements MacroDeclarations
/*  8:   */ {
/*  9:   */   public boolean hasMacro(String name)
/* 10:   */   {
/* 11:43 */     return false;
/* 12:   */   }
/* 13:   */   
/* 14:   */   public Macro getMacro(String name)
/* 15:   */   {
/* 16:56 */     throw new RuntimeException("Macro '" + name + "' doesn't exist!");
/* 17:   */   }
/* 18:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.expressionlanguage.common.NoMacros
 * JD-Core Version:    0.7.0.1
 */