/*   1:    */ package weka.core.expressionlanguage.common;
/*   2:    */ 
/*   3:    */ import weka.core.expressionlanguage.core.Macro;
/*   4:    */ import weka.core.expressionlanguage.core.MacroDeclarations;
/*   5:    */ import weka.core.expressionlanguage.core.Node;
/*   6:    */ import weka.core.expressionlanguage.core.SemanticException;
/*   7:    */ 
/*   8:    */ public class IfElseMacro
/*   9:    */   implements MacroDeclarations, Macro
/*  10:    */ {
/*  11:    */   private static final String IF_ELSE = "ifelse";
/*  12:    */   
/*  13:    */   public boolean hasMacro(String name)
/*  14:    */   {
/*  15: 67 */     return "ifelse".equals(name);
/*  16:    */   }
/*  17:    */   
/*  18:    */   public Macro getMacro(String name)
/*  19:    */   {
/*  20: 80 */     if ("ifelse".equals(name)) {
/*  21: 81 */       return this;
/*  22:    */     }
/*  23: 82 */     throw new RuntimeException("Undefined Macro '" + name + "'!");
/*  24:    */   }
/*  25:    */   
/*  26:    */   public Node evaluate(Node... params)
/*  27:    */     throws SemanticException
/*  28:    */   {
/*  29: 93 */     if (params.length != 3) {
/*  30: 94 */       throw new SemanticException("ifelse takes exactly 3 arguments!");
/*  31:    */     }
/*  32: 96 */     if (!(params[0] instanceof Primitives.BooleanExpression)) {
/*  33: 97 */       throw new SemanticException("ifelse's first parameter must be boolean!");
/*  34:    */     }
/*  35: 99 */     if (((params[1] instanceof Primitives.BooleanExpression)) && ((params[2] instanceof Primitives.BooleanExpression))) {
/*  36:102 */       return new BooleanIfElse((Primitives.BooleanExpression)params[0], (Primitives.BooleanExpression)params[1], (Primitives.BooleanExpression)params[2]);
/*  37:    */     }
/*  38:107 */     if (((params[1] instanceof Primitives.DoubleExpression)) && ((params[2] instanceof Primitives.DoubleExpression))) {
/*  39:110 */       return new DoubleIfElse((Primitives.BooleanExpression)params[0], (Primitives.DoubleExpression)params[1], (Primitives.DoubleExpression)params[2]);
/*  40:    */     }
/*  41:115 */     if (((params[1] instanceof Primitives.StringExpression)) && ((params[2] instanceof Primitives.StringExpression))) {
/*  42:118 */       return new StringIfElse((Primitives.BooleanExpression)params[0], (Primitives.StringExpression)params[1], (Primitives.StringExpression)params[2]);
/*  43:    */     }
/*  44:125 */     throw new SemanticException("ifelse's second and third parameter must be doubles, booleans or Strings!");
/*  45:    */   }
/*  46:    */   
/*  47:    */   private static class DoubleIfElse
/*  48:    */     implements Primitives.DoubleExpression
/*  49:    */   {
/*  50:    */     private final Primitives.BooleanExpression condition;
/*  51:    */     private final Primitives.DoubleExpression ifPart;
/*  52:    */     private final Primitives.DoubleExpression elsePart;
/*  53:    */     
/*  54:    */     public DoubleIfElse(Primitives.BooleanExpression condition, Primitives.DoubleExpression ifPart, Primitives.DoubleExpression elsePart)
/*  55:    */     {
/*  56:138 */       this.condition = condition;
/*  57:139 */       this.ifPart = ifPart;
/*  58:140 */       this.elsePart = elsePart;
/*  59:    */     }
/*  60:    */     
/*  61:    */     public double evaluate()
/*  62:    */     {
/*  63:145 */       if (this.condition.evaluate()) {
/*  64:146 */         return this.ifPart.evaluate();
/*  65:    */       }
/*  66:147 */       return this.elsePart.evaluate();
/*  67:    */     }
/*  68:    */   }
/*  69:    */   
/*  70:    */   private static class BooleanIfElse
/*  71:    */     implements Primitives.BooleanExpression
/*  72:    */   {
/*  73:    */     private final Primitives.BooleanExpression condition;
/*  74:    */     private final Primitives.BooleanExpression ifPart;
/*  75:    */     private final Primitives.BooleanExpression elsePart;
/*  76:    */     
/*  77:    */     public BooleanIfElse(Primitives.BooleanExpression condition, Primitives.BooleanExpression ifPart, Primitives.BooleanExpression elsePart)
/*  78:    */     {
/*  79:161 */       this.condition = condition;
/*  80:162 */       this.ifPart = ifPart;
/*  81:163 */       this.elsePart = elsePart;
/*  82:    */     }
/*  83:    */     
/*  84:    */     public boolean evaluate()
/*  85:    */     {
/*  86:168 */       if (this.condition.evaluate()) {
/*  87:169 */         return this.ifPart.evaluate();
/*  88:    */       }
/*  89:170 */       return this.elsePart.evaluate();
/*  90:    */     }
/*  91:    */   }
/*  92:    */   
/*  93:    */   private static class StringIfElse
/*  94:    */     implements Primitives.StringExpression
/*  95:    */   {
/*  96:    */     private final Primitives.BooleanExpression condition;
/*  97:    */     private final Primitives.StringExpression ifPart;
/*  98:    */     private final Primitives.StringExpression elsePart;
/*  99:    */     
/* 100:    */     public StringIfElse(Primitives.BooleanExpression condition, Primitives.StringExpression ifPart, Primitives.StringExpression elsePart)
/* 101:    */     {
/* 102:184 */       this.condition = condition;
/* 103:185 */       this.ifPart = ifPart;
/* 104:186 */       this.elsePart = elsePart;
/* 105:    */     }
/* 106:    */     
/* 107:    */     public String evaluate()
/* 108:    */     {
/* 109:191 */       if (this.condition.evaluate()) {
/* 110:192 */         return this.ifPart.evaluate();
/* 111:    */       }
/* 112:193 */       return this.elsePart.evaluate();
/* 113:    */     }
/* 114:    */   }
/* 115:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.expressionlanguage.common.IfElseMacro
 * JD-Core Version:    0.7.0.1
 */