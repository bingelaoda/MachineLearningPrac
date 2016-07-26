/*  1:   */ package java_cup.runtime;
/*  2:   */ 
/*  3:   */ public class DefaultSymbolFactory
/*  4:   */   implements SymbolFactory
/*  5:   */ {
/*  6:   */   public Symbol newSymbol(String name, int id, Symbol left, Symbol right, Object value)
/*  7:   */   {
/*  8:32 */     return new Symbol(id, left, right, value);
/*  9:   */   }
/* 10:   */   
/* 11:   */   public Symbol newSymbol(String name, int id, Symbol left, Object value)
/* 12:   */   {
/* 13:35 */     return new Symbol(id, left, value);
/* 14:   */   }
/* 15:   */   
/* 16:   */   public Symbol newSymbol(String name, int id, Symbol left, Symbol right)
/* 17:   */   {
/* 18:38 */     return new Symbol(id, left, right);
/* 19:   */   }
/* 20:   */   
/* 21:   */   public Symbol newSymbol(String name, int id, int left, int right, Object value)
/* 22:   */   {
/* 23:41 */     return new Symbol(id, left, right, value);
/* 24:   */   }
/* 25:   */   
/* 26:   */   public Symbol newSymbol(String name, int id, int left, int right)
/* 27:   */   {
/* 28:44 */     return new Symbol(id, left, right);
/* 29:   */   }
/* 30:   */   
/* 31:   */   public Symbol startSymbol(String name, int id, int state)
/* 32:   */   {
/* 33:47 */     return new Symbol(id, state);
/* 34:   */   }
/* 35:   */   
/* 36:   */   public Symbol newSymbol(String name, int id)
/* 37:   */   {
/* 38:50 */     return new Symbol(id);
/* 39:   */   }
/* 40:   */   
/* 41:   */   public Symbol newSymbol(String name, int id, Object value)
/* 42:   */   {
/* 43:53 */     return new Symbol(id, value);
/* 44:   */   }
/* 45:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     java_cup.runtime.DefaultSymbolFactory
 * JD-Core Version:    0.7.0.1
 */