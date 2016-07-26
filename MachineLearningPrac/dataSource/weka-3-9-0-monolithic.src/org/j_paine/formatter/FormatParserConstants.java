/*  1:   */ package org.j_paine.formatter;
/*  2:   */ 
/*  3:   */ public abstract interface FormatParserConstants
/*  4:   */ {
/*  5:   */   public static final int EOF = 0;
/*  6:   */   public static final int INTEGER = 2;
/*  7:   */   public static final int STRING = 3;
/*  8:   */   public static final int A_DESC = 4;
/*  9:   */   public static final int P_DESC = 5;
/* 10:   */   public static final int X_DESC = 6;
/* 11:   */   public static final int I_DESC = 7;
/* 12:   */   public static final int F_DESC = 8;
/* 13:   */   public static final int D_DESC = 9;
/* 14:   */   public static final int E_DESC = 10;
/* 15:   */   public static final int G_DESC = 11;
/* 16:   */   public static final int L_DESC = 12;
/* 17:   */   public static final int DEFAULT = 0;
/* 18:21 */   public static final String[] tokenImage = { "<EOF>", "<token of kind 1>", "<INTEGER>", "<STRING>", "<A_DESC>", "<P_DESC>", "<X_DESC>", "<I_DESC>", "<F_DESC>", "<D_DESC>", "<E_DESC>", "<G_DESC>", "<L_DESC>", "\".\"", "\"/\"", "\"(\"", "\")\"", "\",\"" };
/* 19:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.FormatParserConstants
 * JD-Core Version:    0.7.0.1
 */