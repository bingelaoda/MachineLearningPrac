/*  1:   */ package org.boon.validation;
/*  2:   */ 
/*  3:   */ import java.io.Serializable;
/*  4:   */ 
/*  5:   */ public class ValidatorMessage
/*  6:   */   implements Serializable, ValidatorMessageHolder
/*  7:   */ {
/*  8:   */   private String detail;
/*  9:   */   private String summary;
/* 10:46 */   private boolean hasError = false;
/* 11:   */   
/* 12:   */   public ValidatorMessage(String summary, String detail)
/* 13:   */   {
/* 14:50 */     this.summary = summary;
/* 15:51 */     this.detail = detail;
/* 16:52 */     this.hasError = true;
/* 17:   */   }
/* 18:   */   
/* 19:   */   public ValidatorMessage(String message)
/* 20:   */   {
/* 21:56 */     this.summary = message;
/* 22:57 */     this.detail = message;
/* 23:58 */     this.hasError = true;
/* 24:   */   }
/* 25:   */   
/* 26:   */   public ValidatorMessage()
/* 27:   */   {
/* 28:62 */     this.summary = "Message not setup!";
/* 29:63 */     this.detail = "Message not setup!";
/* 30:64 */     this.hasError = false;
/* 31:   */   }
/* 32:   */   
/* 33:   */   public String getDetail()
/* 34:   */   {
/* 35:68 */     return this.detail;
/* 36:   */   }
/* 37:   */   
/* 38:   */   public void setDetail(String detail)
/* 39:   */   {
/* 40:72 */     this.detail = detail;
/* 41:   */   }
/* 42:   */   
/* 43:   */   public String getSummary()
/* 44:   */   {
/* 45:76 */     return this.summary;
/* 46:   */   }
/* 47:   */   
/* 48:   */   public void setSummary(String summary)
/* 49:   */   {
/* 50:80 */     this.summary = summary;
/* 51:   */   }
/* 52:   */   
/* 53:   */   public boolean hasError()
/* 54:   */   {
/* 55:84 */     return this.hasError;
/* 56:   */   }
/* 57:   */   
/* 58:   */   public void setHasError(boolean aHasError)
/* 59:   */   {
/* 60:88 */     this.hasError = aHasError;
/* 61:   */   }
/* 62:   */   
/* 63:   */   public String toString()
/* 64:   */   {
/* 65:94 */     return "ValidatorMessage{detail='" + this.detail + '\'' + ", summary='" + this.summary + '\'' + ", hasError=" + this.hasError + '}';
/* 66:   */   }
/* 67:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.validation.ValidatorMessage
 * JD-Core Version:    0.7.0.1
 */