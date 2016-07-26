/*  1:   */ package org.boon.core.value;
/*  2:   */ 
/*  3:   */ import java.util.Map.Entry;
/*  4:   */ import java.util.concurrent.ConcurrentHashMap;
/*  5:   */ import org.boon.Exceptions;
/*  6:   */ import org.boon.core.Value;
/*  7:   */ 
/*  8:   */ public class MapItemValue
/*  9:   */   implements Map.Entry<String, Value>
/* 10:   */ {
/* 11:   */   final Value name;
/* 12:   */   final Value value;
/* 13:46 */   private String key = null;
/* 14:48 */   private static final boolean internKeys = Boolean.parseBoolean(System.getProperty("org.boon.json.implementation.internKeys", "false"));
/* 15:   */   protected static ConcurrentHashMap<String, String> internedKeysCache;
/* 16:   */   
/* 17:   */   static
/* 18:   */   {
/* 19:57 */     if (internKeys) {
/* 20:58 */       internedKeysCache = new ConcurrentHashMap();
/* 21:   */     }
/* 22:   */   }
/* 23:   */   
/* 24:   */   public MapItemValue(Value name, Value value)
/* 25:   */   {
/* 26:64 */     this.name = name;
/* 27:65 */     this.value = value;
/* 28:   */   }
/* 29:   */   
/* 30:   */   public String getKey()
/* 31:   */   {
/* 32:71 */     if (this.key == null) {
/* 33:72 */       if (internKeys)
/* 34:   */       {
/* 35:74 */         this.key = this.name.toString();
/* 36:   */         
/* 37:76 */         String keyPrime = (String)internedKeysCache.get(this.key);
/* 38:77 */         if (keyPrime == null)
/* 39:   */         {
/* 40:78 */           this.key = this.key.intern();
/* 41:79 */           internedKeysCache.put(this.key, this.key);
/* 42:   */         }
/* 43:   */         else
/* 44:   */         {
/* 45:81 */           this.key = keyPrime;
/* 46:   */         }
/* 47:   */       }
/* 48:   */       else
/* 49:   */       {
/* 50:85 */         this.key = this.name.toString();
/* 51:   */       }
/* 52:   */     }
/* 53:88 */     return this.key;
/* 54:   */   }
/* 55:   */   
/* 56:   */   public Value getValue()
/* 57:   */   {
/* 58:93 */     return this.value;
/* 59:   */   }
/* 60:   */   
/* 61:   */   public Value setValue(Value value)
/* 62:   */   {
/* 63:98 */     Exceptions.die("not that kind of Entry");
/* 64:99 */     return null;
/* 65:   */   }
/* 66:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.value.MapItemValue
 * JD-Core Version:    0.7.0.1
 */