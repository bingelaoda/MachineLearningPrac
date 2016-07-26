/*  1:   */ package org.boon.template;
/*  2:   */ 
/*  3:   */ import org.boon.core.Conversions;
/*  4:   */ 
/*  5:   */ public enum Commands
/*  6:   */ {
/*  7:37 */   EACH,  IF,  WITH,  UNLESS,  LOG,  INCLUDE,  UNKNOWN;
/*  8:   */   
/*  9:   */   private Commands() {}
/* 10:   */   
/* 11:   */   public static Commands command(String value)
/* 12:   */   {
/* 13:51 */     return (Commands)Conversions.toEnum(Commands.class, value.toUpperCase(), UNKNOWN);
/* 14:   */   }
/* 15:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.template.Commands
 * JD-Core Version:    0.7.0.1
 */