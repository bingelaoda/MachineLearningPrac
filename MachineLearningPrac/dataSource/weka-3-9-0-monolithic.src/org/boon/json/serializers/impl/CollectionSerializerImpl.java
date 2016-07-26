/*  1:   */ package org.boon.json.serializers.impl;
/*  2:   */ 
/*  3:   */ import java.lang.reflect.Array;
/*  4:   */ import java.util.Collection;
/*  5:   */ import org.boon.json.serializers.ArraySerializer;
/*  6:   */ import org.boon.json.serializers.CollectionSerializer;
/*  7:   */ import org.boon.json.serializers.JsonSerializerInternal;
/*  8:   */ import org.boon.primitive.CharBuf;
/*  9:   */ 
/* 10:   */ public class CollectionSerializerImpl
/* 11:   */   implements CollectionSerializer, ArraySerializer
/* 12:   */ {
/* 13:41 */   private static final char[] EMPTY_LIST_CHARS = { '[', ']' };
/* 14:   */   
/* 15:   */   public final void serializeCollection(JsonSerializerInternal serializer, Collection<?> collection, CharBuf builder)
/* 16:   */   {
/* 17:46 */     if (collection.size() == 0)
/* 18:   */     {
/* 19:47 */       builder.addChars(EMPTY_LIST_CHARS);
/* 20:48 */       return;
/* 21:   */     }
/* 22:51 */     builder.addChar('[');
/* 23:52 */     for (Object o : collection)
/* 24:   */     {
/* 25:53 */       serializer.serializeObject(o, builder);
/* 26:54 */       builder.addChar(',');
/* 27:   */     }
/* 28:56 */     builder.removeLastChar();
/* 29:57 */     builder.addChar(']');
/* 30:   */   }
/* 31:   */   
/* 32:   */   public void serializeArray(JsonSerializerInternal serializer, Object array, CharBuf builder)
/* 33:   */   {
/* 34:63 */     if (Array.getLength(array) == 0)
/* 35:   */     {
/* 36:64 */       builder.addChars(EMPTY_LIST_CHARS);
/* 37:65 */       return;
/* 38:   */     }
/* 39:68 */     builder.addChar('[');
/* 40:69 */     int length = Array.getLength(array);
/* 41:70 */     for (int index = 0; index < length; index++)
/* 42:   */     {
/* 43:71 */       serializer.serializeObject(Array.get(array, index), builder);
/* 44:72 */       builder.addChar(',');
/* 45:   */     }
/* 46:74 */     builder.removeLastChar();
/* 47:75 */     builder.addChar(']');
/* 48:   */   }
/* 49:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.serializers.impl.CollectionSerializerImpl
 * JD-Core Version:    0.7.0.1
 */