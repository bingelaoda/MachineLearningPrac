/*  1:   */ package org.j_paine.formatter;
/*  2:   */ 
/*  3:   */ public abstract interface NumberParserConstants
/*  4:   */ {
/*  5:   */   public static final int EOF = 0;
/*  6:   */   public static final int INTEGER_LITERAL = 1;
/*  7:   */   public static final int DECIMAL_LITERAL = 2;
/*  8:   */   public static final int LOGICAL_LITERAL = 3;
/*  9:   */   public static final int FLOATING_POINT_LITERAL = 4;
/* 10:   */   public static final int EXPONENT = 5;
/* 11:   */   public static final int DEFAULT = 0;
/* 12:15 */   public static final String[] tokenImage = { "<EOF>", "<INTEGER_LITERAL>", "<DECIMAL_LITERAL>", "<LOGICAL_LITERAL>", "<FLOATING_POINT_LITERAL>", "<EXPONENT>", "\" \"", "\"-\"", "\"+\"" };
/* 13:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.NumberParserConstants
 * JD-Core Version:    0.7.0.1
 */