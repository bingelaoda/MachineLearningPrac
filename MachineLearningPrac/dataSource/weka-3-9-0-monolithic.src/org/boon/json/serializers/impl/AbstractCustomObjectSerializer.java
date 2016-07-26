/*  1:   */ package org.boon.json.serializers.impl;
/*  2:   */ 
/*  3:   */ import org.boon.json.serializers.CustomObjectSerializer;
/*  4:   */ 
/*  5:   */ public abstract class AbstractCustomObjectSerializer<T>
/*  6:   */   implements CustomObjectSerializer<T>
/*  7:   */ {
/*  8:   */   protected Class<T> clazz;
/*  9:   */   
/* 10:   */   public AbstractCustomObjectSerializer(Class<T> clazz)
/* 11:   */   {
/* 12:37 */     this.clazz = clazz;
/* 13:   */   }
/* 14:   */   
/* 15:   */   public Class<T> type()
/* 16:   */   {
/* 17:42 */     return this.clazz;
/* 18:   */   }
/* 19:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.serializers.impl.AbstractCustomObjectSerializer
 * JD-Core Version:    0.7.0.1
 */