/*  1:   */ package org.boon.json;
/*  2:   */ 
/*  3:   */ import org.boon.primitive.CharBuf;
/*  4:   */ 
/*  5:   */ public class ObjectSerializationData
/*  6:   */ {
/*  7:   */   public final Object instance;
/*  8:   */   public final Class<?> type;
/*  9:   */   public final CharBuf output;
/* 10:   */   
/* 11:   */   public ObjectSerializationData(Object instance, Class<?> type, CharBuf output)
/* 12:   */   {
/* 13:43 */     this.instance = instance;
/* 14:44 */     this.type = type;
/* 15:45 */     this.output = output;
/* 16:   */   }
/* 17:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.ObjectSerializationData
 * JD-Core Version:    0.7.0.1
 */