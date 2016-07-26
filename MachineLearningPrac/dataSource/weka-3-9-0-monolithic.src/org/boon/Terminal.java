/*  1:   */ package org.boon;
/*  2:   */ 
/*  3:   */ public class Terminal
/*  4:   */ {
/*  5:   */   public static enum Escape
/*  6:   */   {
/*  7:10 */     RESET("\033[0m"),  BOLD_ON("\033[1m"),  ITALICS_ON("\033[3m"),  UNDERLINE_ON("\033[4m"),  INVERSE_ON("\033[7m"),  STRIKETHROUGH_ON("\033[9m"),  BOLD_OFF("\033[22m"),  ITALICS_OFF("\033[23m"),  UNDERLINE_OFF("\033[24m"),  INVERSE_OFF("\033[27m"),  STRIKETHROUGH_OFF("\033[29m"),  FG_BLACK("\033[30m"),  FG_RED("\033[31m"),  FG_GREEN("\033[32m"),  FG_YELLOW("\033[33m"),  FG_BLUE("\033[34m"),  FG_MAGENTA("\033[35m"),  FG_CYAN("\033[36m"),  FG_WHITE("\033[37m"),  FG_DEFAULT("\033[39m"),  BG_BLACK("\033[40m"),  BG_RED("\033[41m"),  BG_GREEN("\033[42m"),  BG_YELLOW("\033[43m"),  BG_BLUE("\033[44m"),  BG_MAGENTA("\033[45m"),  BG_CYAN("\033[46m"),  BG_WHITE("\033[47m"),  BG_DEFAULT("\033[49m");
/*  8:   */     
/*  9:   */     private final String value;
/* 10:   */     
/* 11:   */     private Escape(String s)
/* 12:   */     {
/* 13:46 */       this.value = s;
/* 14:   */     }
/* 15:   */     
/* 16:   */     public String toString()
/* 17:   */     {
/* 18:51 */       return this.value;
/* 19:   */     }
/* 20:   */   }
/* 21:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.Terminal
 * JD-Core Version:    0.7.0.1
 */