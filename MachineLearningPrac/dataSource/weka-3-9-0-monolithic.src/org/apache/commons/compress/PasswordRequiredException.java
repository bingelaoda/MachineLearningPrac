/*  1:   */ package org.apache.commons.compress;
/*  2:   */ 
/*  3:   */ import java.io.IOException;
/*  4:   */ 
/*  5:   */ public class PasswordRequiredException
/*  6:   */   extends IOException
/*  7:   */ {
/*  8:   */   private static final long serialVersionUID = 1391070005491684483L;
/*  9:   */   
/* 10:   */   public PasswordRequiredException(String name)
/* 11:   */   {
/* 12:38 */     super("Cannot read encrypted content from " + name + " without a password.");
/* 13:   */   }
/* 14:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.PasswordRequiredException
 * JD-Core Version:    0.7.0.1
 */