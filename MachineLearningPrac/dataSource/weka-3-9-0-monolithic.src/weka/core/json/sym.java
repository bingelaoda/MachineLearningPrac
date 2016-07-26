/*  1:   */ package weka.core.json;
/*  2:   */ 
/*  3:   */ public abstract interface sym
/*  4:   */ {
/*  5:   */   public static final int LSQUARE = 3;
/*  6:   */   public static final int INTEGER = 10;
/*  7:   */   public static final int COLON = 7;
/*  8:   */   public static final int BOOLEAN = 9;
/*  9:   */   public static final int NULL = 8;
/* 10:   */   public static final int RSQUARE = 4;
/* 11:   */   public static final int STRING = 12;
/* 12:   */   public static final int EOF = 0;
/* 13:   */   public static final int DOUBLE = 11;
/* 14:   */   public static final int error = 1;
/* 15:   */   public static final int COMMA = 2;
/* 16:   */   public static final int RCURLY = 6;
/* 17:   */   public static final int LCURLY = 5;
/* 18:24 */   public static final String[] terminalNames = { "EOF", "error", "COMMA", "LSQUARE", "RSQUARE", "LCURLY", "RCURLY", "COLON", "NULL", "BOOLEAN", "INTEGER", "DOUBLE", "STRING" };
/* 19:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.json.sym
 * JD-Core Version:    0.7.0.1
 */