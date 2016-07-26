/*  1:   */ package org.boon.json.serializers.impl;
/*  2:   */ 
/*  3:   */ import java.util.Map;
/*  4:   */ import java.util.Map.Entry;
/*  5:   */ import java.util.Set;
/*  6:   */ import org.boon.core.reflection.FastStringUtils;
/*  7:   */ import org.boon.json.serializers.JsonSerializerInternal;
/*  8:   */ import org.boon.json.serializers.MapSerializer;
/*  9:   */ import org.boon.primitive.CharBuf;
/* 10:   */ 
/* 11:   */ public class MapSerializerImpl
/* 12:   */   implements MapSerializer
/* 13:   */ {
/* 14:43 */   private static final char[] EMPTY_MAP_CHARS = { '{', '}' };
/* 15:   */   private final boolean includeNulls;
/* 16:   */   
/* 17:   */   public MapSerializerImpl(boolean includeNulls)
/* 18:   */   {
/* 19:47 */     this.includeNulls = includeNulls;
/* 20:   */   }
/* 21:   */   
/* 22:   */   private void serializeFieldName(String name, CharBuf builder)
/* 23:   */   {
/* 24:52 */     builder.addJsonFieldName(FastStringUtils.toCharArray(name));
/* 25:   */   }
/* 26:   */   
/* 27:   */   public final void serializeMap(JsonSerializerInternal serializer, Map<Object, Object> map, CharBuf builder)
/* 28:   */   {
/* 29:58 */     if (map.size() == 0)
/* 30:   */     {
/* 31:59 */       builder.addChars(EMPTY_MAP_CHARS);
/* 32:60 */       return;
/* 33:   */     }
/* 34:64 */     builder.addChar('{');
/* 35:   */     
/* 36:66 */     Set<Map.Entry<Object, Object>> entrySet = map.entrySet();
/* 37:67 */     int index = 0;
/* 38:69 */     if (!this.includeNulls) {
/* 39:70 */       for (Map.Entry<Object, Object> entry : entrySet) {
/* 40:71 */         if (entry.getValue() != null)
/* 41:   */         {
/* 42:72 */           serializeFieldName(entry.getKey().toString(), builder);
/* 43:73 */           serializer.serializeObject(entry.getValue(), builder);
/* 44:74 */           builder.addChar(',');
/* 45:75 */           index++;
/* 46:   */         }
/* 47:   */       }
/* 48:   */     } else {
/* 49:79 */       for (Map.Entry<Object, Object> entry : entrySet)
/* 50:   */       {
/* 51:80 */         serializeFieldName(entry.getKey().toString(), builder);
/* 52:81 */         serializer.serializeObject(entry.getValue(), builder);
/* 53:82 */         builder.addChar(',');
/* 54:83 */         index++;
/* 55:   */       }
/* 56:   */     }
/* 57:87 */     if (index > 0) {
/* 58:88 */       builder.removeLastChar();
/* 59:   */     }
/* 60:89 */     builder.addChar('}');
/* 61:   */   }
/* 62:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.serializers.impl.MapSerializerImpl
 * JD-Core Version:    0.7.0.1
 */