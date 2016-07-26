/*  1:   */ package org.boon.json.serializers.impl;
/*  2:   */ 
/*  3:   */ import java.util.Date;
/*  4:   */ import org.boon.json.serializers.DateSerializer;
/*  5:   */ import org.boon.json.serializers.JsonSerializerInternal;
/*  6:   */ import org.boon.primitive.CharBuf;
/*  7:   */ 
/*  8:   */ public class DateSerializerImpl
/*  9:   */   implements DateSerializer
/* 10:   */ {
/* 11:   */   public final void serializeDate(JsonSerializerInternal jsonSerializer, Date date, CharBuf builder)
/* 12:   */   {
/* 13:43 */     builder.addLong(date.getTime());
/* 14:   */   }
/* 15:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.serializers.impl.DateSerializerImpl
 * JD-Core Version:    0.7.0.1
 */