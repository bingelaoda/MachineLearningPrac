/*  1:   */ package org.boon.json.serializers.impl;
/*  2:   */ 
/*  3:   */ import java.util.Calendar;
/*  4:   */ import java.util.Date;
/*  5:   */ import java.util.TimeZone;
/*  6:   */ import org.boon.cache.Cache;
/*  7:   */ import org.boon.cache.CacheType;
/*  8:   */ import org.boon.cache.SimpleCache;
/*  9:   */ import org.boon.core.Dates;
/* 10:   */ import org.boon.core.reflection.FastStringUtils;
/* 11:   */ import org.boon.json.serializers.DateSerializer;
/* 12:   */ import org.boon.json.serializers.JsonSerializerInternal;
/* 13:   */ import org.boon.primitive.CharBuf;
/* 14:   */ 
/* 15:   */ public class JsonDateSerializer
/* 16:   */   implements DateSerializer
/* 17:   */ {
/* 18:49 */   private final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 19:50 */   private final Cache<Object, String> dateCache = new SimpleCache(200, CacheType.LRU);
/* 20:   */   
/* 21:   */   public final void serializeDate(JsonSerializerInternal jsonSerializer, Date date, CharBuf builder)
/* 22:   */   {
/* 23:55 */     String string = (String)this.dateCache.get(date);
/* 24:56 */     if (string == null)
/* 25:   */     {
/* 26:57 */       CharBuf buf = CharBuf.create(Dates.JSON_TIME_LENGTH);
/* 27:58 */       Dates.jsonDateChars(this.calendar, date, buf);
/* 28:59 */       string = buf.toString();
/* 29:60 */       this.dateCache.put(date, string);
/* 30:   */     }
/* 31:63 */     builder.addChars(FastStringUtils.toCharArray(string));
/* 32:   */   }
/* 33:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.serializers.impl.JsonDateSerializer
 * JD-Core Version:    0.7.0.1
 */