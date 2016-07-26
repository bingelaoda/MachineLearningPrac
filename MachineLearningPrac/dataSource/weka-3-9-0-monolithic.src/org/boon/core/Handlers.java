/* 1:  */ package org.boon.core;
/* 2:  */ 
/* 3:  */ public class Handlers
/* 4:  */ {
/* 5:  */   public static <T> Handler<T> handler(final Handler<T> handler, Handler<Throwable> errorHandler)
/* 6:  */   {
/* 7:9 */     new HandlerWithErrorHandling()
/* 8:  */     {
/* 9:  */       public Handler<Throwable> errorHandler()
/* ::  */       {
/* ;:= */         return this.val$errorHandler;
/* <:  */       }
/* =:  */       
/* >:  */       public void handle(T event)
/* ?:  */       {
/* @:B */         handler.handle(event);
/* A:  */       }
/* B:  */     };
/* C:  */   }
/* D:  */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.Handlers
 * JD-Core Version:    0.7.0.1
 */