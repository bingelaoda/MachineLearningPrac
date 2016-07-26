/*  1:   */ package org.boon.json.serializers.impl;
/*  2:   */ 
/*  3:   */ import org.boon.json.serializers.JsonSerializerInternal;
/*  4:   */ import org.boon.json.serializers.StringSerializer;
/*  5:   */ import org.boon.primitive.CharBuf;
/*  6:   */ 
/*  7:   */ public class StringSerializerImpl
/*  8:   */   implements StringSerializer
/*  9:   */ {
/* 10:   */   final boolean encodeStrings;
/* 11:   */   final boolean asAscii;
/* 12:   */   
/* 13:   */   public StringSerializerImpl(boolean encodeStrings, boolean asAscii)
/* 14:   */   {
/* 15:45 */     this.encodeStrings = encodeStrings;
/* 16:46 */     this.asAscii = asAscii;
/* 17:   */   }
/* 18:   */   
/* 19:   */   public final void serializeString(JsonSerializerInternal serializer, String string, CharBuf builder)
/* 20:   */   {
/* 21:51 */     if (this.encodeStrings) {
/* 22:53 */       builder.asJsonString(string, this.asAscii);
/* 23:   */     } else {
/* 24:55 */       builder.addQuoted(string);
/* 25:   */     }
/* 26:   */   }
/* 27:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.serializers.impl.StringSerializerImpl
 * JD-Core Version:    0.7.0.1
 */