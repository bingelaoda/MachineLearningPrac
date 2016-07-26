/*  1:   */ package org.boon.json.serializers.impl;
/*  2:   */ 
/*  3:   */ import org.boon.core.reflection.fields.FieldAccess;
/*  4:   */ import org.boon.json.serializers.CustomFieldSerializer;
/*  5:   */ import org.boon.json.serializers.JsonSerializerInternal;
/*  6:   */ import org.boon.primitive.CharBuf;
/*  7:   */ 
/*  8:   */ public abstract class AbstractCustomFieldSerializer
/*  9:   */   implements CustomFieldSerializer
/* 10:   */ {
/* 11:   */   private final Class<?> parentClass;
/* 12:   */   private final String fieldName;
/* 13:   */   
/* 14:   */   public AbstractCustomFieldSerializer(Class<?> parentClass, String fieldName)
/* 15:   */   {
/* 16:24 */     this.fieldName = fieldName;
/* 17:25 */     this.parentClass = parentClass;
/* 18:   */   }
/* 19:   */   
/* 20:   */   public boolean serializeField(JsonSerializerInternal serializer, Object parent, FieldAccess fieldAccess, CharBuf builder)
/* 21:   */   {
/* 22:31 */     if ((this.parentClass == parent.getClass()) && (this.fieldName.equals(fieldAccess.name())))
/* 23:   */     {
/* 24:32 */       Object value = fieldAccess.getValue(parent);
/* 25:33 */       serialize(serializer, parent, fieldAccess, value, builder);
/* 26:34 */       return true;
/* 27:   */     }
/* 28:36 */     return false;
/* 29:   */   }
/* 30:   */   
/* 31:   */   protected abstract void serialize(JsonSerializerInternal paramJsonSerializerInternal, Object paramObject1, FieldAccess paramFieldAccess, Object paramObject2, CharBuf paramCharBuf);
/* 32:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.serializers.impl.AbstractCustomFieldSerializer
 * JD-Core Version:    0.7.0.1
 */