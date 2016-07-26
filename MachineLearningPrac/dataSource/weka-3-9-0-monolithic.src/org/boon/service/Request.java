/*  1:   */ package org.boon.service;
/*  2:   */ 
/*  3:   */ public class Request
/*  4:   */ {
/*  5:   */   final String method;
/*  6:   */   final Object headers;
/*  7:   */   final Object params;
/*  8:   */   final Object payload;
/*  9:   */   final String path;
/* 10:   */   final long correlationId;
/* 11:   */   
/* 12:   */   public Request(String method, Object headers, Object params, Object payload, String path)
/* 13:   */   {
/* 14:49 */     this.method = method;
/* 15:50 */     this.params = params;
/* 16:51 */     this.headers = headers;
/* 17:52 */     this.payload = payload;
/* 18:53 */     this.path = path;
/* 19:54 */     this.correlationId = -1L;
/* 20:   */   }
/* 21:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.service.Request
 * JD-Core Version:    0.7.0.1
 */