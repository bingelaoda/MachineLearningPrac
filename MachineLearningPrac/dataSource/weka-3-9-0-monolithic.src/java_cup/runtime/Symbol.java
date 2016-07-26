/*   1:    */ package java_cup.runtime;
/*   2:    */ 
/*   3:    */ public class Symbol
/*   4:    */ {
/*   5:    */   public int sym;
/*   6:    */   public int parse_state;
/*   7:    */   
/*   8:    */   public Symbol(int id, Symbol left, Symbol right, Object o)
/*   9:    */   {
/*  10: 32 */     this(id, left.left, right.right, o);
/*  11:    */   }
/*  12:    */   
/*  13:    */   public Symbol(int id, Symbol left, Symbol right)
/*  14:    */   {
/*  15: 35 */     this(id, left.left, right.right);
/*  16:    */   }
/*  17:    */   
/*  18:    */   public Symbol(int id, Symbol left, Object o)
/*  19:    */   {
/*  20: 38 */     this(id, left.right, left.right, o);
/*  21:    */   }
/*  22:    */   
/*  23:    */   public Symbol(int id, int l, int r, Object o)
/*  24:    */   {
/*  25: 45 */     this(id);
/*  26: 46 */     this.left = l;
/*  27: 47 */     this.right = r;
/*  28: 48 */     this.value = o;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public Symbol(int id, Object o)
/*  32:    */   {
/*  33: 56 */     this(id, -1, -1, o);
/*  34:    */   }
/*  35:    */   
/*  36:    */   public Symbol(int id, int l, int r)
/*  37:    */   {
/*  38: 64 */     this(id, l, r, null);
/*  39:    */   }
/*  40:    */   
/*  41:    */   public Symbol(int sym_num)
/*  42:    */   {
/*  43: 72 */     this(sym_num, -1);
/*  44: 73 */     this.left = -1;
/*  45: 74 */     this.right = -1;
/*  46:    */   }
/*  47:    */   
/*  48:    */   Symbol(int sym_num, int state)
/*  49:    */   {
/*  50: 82 */     this.sym = sym_num;
/*  51: 83 */     this.parse_state = state;
/*  52:    */   }
/*  53:    */   
/*  54:100 */   boolean used_by_parser = false;
/*  55:    */   public int left;
/*  56:    */   public int right;
/*  57:    */   public Object value;
/*  58:    */   
/*  59:    */   public String toString()
/*  60:    */   {
/*  61:112 */     return "#" + this.sym;
/*  62:    */   }
/*  63:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     java_cup.runtime.Symbol
 * JD-Core Version:    0.7.0.1
 */