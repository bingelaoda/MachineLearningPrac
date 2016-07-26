/*  1:   */ package org.boon.json.serializers.impl;
/*  2:   */ 
/*  3:   */ import org.boon.json.serializers.JsonSerializerInternal;
/*  4:   */ import org.boon.json.serializers.UnknownSerializer;
/*  5:   */ import org.boon.primitive.CharBuf;
/*  6:   */ 
/*  7:   */ public class UnknownSerializerImpl
/*  8:   */   implements UnknownSerializer
/*  9:   */ {
/* 10:   */   public final void serializeUnknown(JsonSerializerInternal serializer, Object unknown, CharBuf builder)
/* 11:   */   {
/* 12:41 */     builder.addQuoted(unknown.toString());
/* 13:   */   }
/* 14:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.serializers.impl.UnknownSerializerImpl
 * JD-Core Version:    0.7.0.1
 */