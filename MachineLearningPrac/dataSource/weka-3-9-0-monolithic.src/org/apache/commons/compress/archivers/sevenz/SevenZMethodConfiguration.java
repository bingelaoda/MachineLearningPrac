/*  1:   */ package org.apache.commons.compress.archivers.sevenz;
/*  2:   */ 
/*  3:   */ public class SevenZMethodConfiguration
/*  4:   */ {
/*  5:   */   private final SevenZMethod method;
/*  6:   */   private final Object options;
/*  7:   */   
/*  8:   */   public SevenZMethodConfiguration(SevenZMethod method)
/*  9:   */   {
/* 10:47 */     this(method, null);
/* 11:   */   }
/* 12:   */   
/* 13:   */   public SevenZMethodConfiguration(SevenZMethod method, Object options)
/* 14:   */   {
/* 15:57 */     this.method = method;
/* 16:58 */     this.options = options;
/* 17:59 */     if ((options != null) && (!Coders.findByMethod(method).canAcceptOptions(options))) {
/* 18:60 */       throw new IllegalArgumentException("The " + method + " method doesn't support options of type " + options.getClass());
/* 19:   */     }
/* 20:   */   }
/* 21:   */   
/* 22:   */   public SevenZMethod getMethod()
/* 23:   */   {
/* 24:70 */     return this.method;
/* 25:   */   }
/* 26:   */   
/* 27:   */   public Object getOptions()
/* 28:   */   {
/* 29:78 */     return this.options;
/* 30:   */   }
/* 31:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.sevenz.SevenZMethodConfiguration
 * JD-Core Version:    0.7.0.1
 */