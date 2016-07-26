/*  1:   */ package org.boon.template.support;
/*  2:   */ 
/*  3:   */ public enum TokenTypes
/*  4:   */ {
/*  5: 9 */   COMMAND(""),  COMMAND_BODY(""),  EXPRESSION(""),  COMMAND_START("<c:", "{{#"),  COMMAND_END_START(">", "{{/"),  COMMAND_START_TAG_END("/>"),  COMMAND_START_END("</c:"),  EXPRESSION_START("${", "{{"),  EXPRESSION_END("}", "}}"),  TEXT("");
/*  6:   */   
/*  7:   */   private char[] JSTL_STYLE;
/*  8:   */   private char[] HANDLE_BAR_STYLE;
/*  9:   */   
/* 10:   */   private TokenTypes(String jstl)
/* 11:   */   {
/* 12:26 */     this.JSTL_STYLE = jstl.toCharArray();
/* 13:   */   }
/* 14:   */   
/* 15:   */   private TokenTypes(String jstl, String handlebar)
/* 16:   */   {
/* 17:31 */     this.JSTL_STYLE = jstl.toCharArray();
/* 18:32 */     this.HANDLE_BAR_STYLE = handlebar.toCharArray();
/* 19:   */   }
/* 20:   */   
/* 21:   */   private TokenTypes()
/* 22:   */   {
/* 23:39 */     this.JSTL_STYLE = "".toCharArray();
/* 24:40 */     this.HANDLE_BAR_STYLE = "".toCharArray();
/* 25:   */   }
/* 26:   */   
/* 27:   */   public char[] jstlStyle()
/* 28:   */   {
/* 29:46 */     return this.JSTL_STYLE;
/* 30:   */   }
/* 31:   */   
/* 32:   */   public char[] handleBarStyle()
/* 33:   */   {
/* 34:50 */     return this.HANDLE_BAR_STYLE;
/* 35:   */   }
/* 36:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.template.support.TokenTypes
 * JD-Core Version:    0.7.0.1
 */