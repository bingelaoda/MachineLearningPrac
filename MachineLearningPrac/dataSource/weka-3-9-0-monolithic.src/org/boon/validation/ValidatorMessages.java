/*  1:   */ package org.boon.validation;
/*  2:   */ 
/*  3:   */ import java.io.Serializable;
/*  4:   */ import java.util.ArrayList;
/*  5:   */ import java.util.Iterator;
/*  6:   */ import java.util.List;
/*  7:   */ 
/*  8:   */ public class ValidatorMessages
/*  9:   */   implements Serializable, ValidatorMessageHolder, Iterable<ValidatorMessage>
/* 10:   */ {
/* 11:45 */   private List<ValidatorMessage> messages = new ArrayList();
/* 12:   */   
/* 13:   */   public Iterator<ValidatorMessage> iterator()
/* 14:   */   {
/* 15:48 */     return this.messages.iterator();
/* 16:   */   }
/* 17:   */   
/* 18:   */   public void add(ValidatorMessage message)
/* 19:   */   {
/* 20:52 */     this.messages.add(message);
/* 21:   */   }
/* 22:   */   
/* 23:   */   public boolean hasError()
/* 24:   */   {
/* 25:57 */     for (ValidatorMessage message : this.messages) {
/* 26:58 */       if (message.hasError()) {
/* 27:59 */         return true;
/* 28:   */       }
/* 29:   */     }
/* 30:63 */     return false;
/* 31:   */   }
/* 32:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.validation.ValidatorMessages
 * JD-Core Version:    0.7.0.1
 */