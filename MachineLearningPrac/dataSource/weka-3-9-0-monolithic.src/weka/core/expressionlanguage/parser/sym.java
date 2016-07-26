/*  1:   */ package weka.core.expressionlanguage.parser;
/*  2:   */ 
/*  3:   */ public abstract interface sym
/*  4:   */ {
/*  5:   */   public static final int TIMES = 11;
/*  6:   */   public static final int AND = 16;
/*  7:   */   public static final int IS = 24;
/*  8:   */   public static final int LT = 20;
/*  9:   */   public static final int PLUS = 9;
/* 10:   */   public static final int OR = 17;
/* 11:   */   public static final int RPAREN = 7;
/* 12:   */   public static final int EQUAL = 19;
/* 13:   */   public static final int DIVISION = 12;
/* 14:   */   public static final int NOT = 18;
/* 15:   */   public static final int IDENTIFIER = 2;
/* 16:   */   public static final int POW = 13;
/* 17:   */   public static final int GT = 22;
/* 18:   */   public static final int LPAREN = 6;
/* 19:   */   public static final int LE = 21;
/* 20:   */   public static final int REGEXP = 25;
/* 21:   */   public static final int BOOLEAN = 5;
/* 22:   */   public static final int STRING = 4;
/* 23:   */   public static final int COMMA = 8;
/* 24:   */   public static final int FLOAT = 3;
/* 25:   */   public static final int EOF = 0;
/* 26:   */   public static final int UPLUS = 15;
/* 27:   */   public static final int GE = 23;
/* 28:   */   public static final int MINUS = 10;
/* 29:   */   public static final int error = 1;
/* 30:   */   public static final int UMINUS = 14;
/* 31:37 */   public static final String[] terminalNames = { "EOF", "error", "IDENTIFIER", "FLOAT", "STRING", "BOOLEAN", "LPAREN", "RPAREN", "COMMA", "PLUS", "MINUS", "TIMES", "DIVISION", "POW", "UMINUS", "UPLUS", "AND", "OR", "NOT", "EQUAL", "LT", "LE", "GT", "GE", "IS", "REGEXP" };
/* 32:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.expressionlanguage.parser.sym
 * JD-Core Version:    0.7.0.1
 */