/*   1:    */ package org.boon.core.value;
/*   2:    */ 
/*   3:    */ import java.util.AbstractMap;
/*   4:    */ import java.util.Collection;
/*   5:    */ import java.util.HashMap;
/*   6:    */ import java.util.Map;
/*   7:    */ import java.util.Map.Entry;
/*   8:    */ import java.util.Set;
/*   9:    */ import org.boon.Exceptions;
/*  10:    */ import org.boon.core.Value;
/*  11:    */ import org.boon.primitive.Arry;
/*  12:    */ 
/*  13:    */ public class ValueMapImpl
/*  14:    */   extends AbstractMap<String, Value>
/*  15:    */   implements ValueMap<String, Value>
/*  16:    */ {
/*  17: 47 */   private Map<String, Value> map = null;
/*  18: 50 */   private Map.Entry<String, Value>[] items = new Map.Entry[20];
/*  19: 53 */   private int len = 0;
/*  20:    */   
/*  21:    */   public void add(MapItemValue miv)
/*  22:    */   {
/*  23: 62 */     if (this.len >= this.items.length) {
/*  24: 63 */       this.items = ((Map.Entry[])Arry.grow(this.items));
/*  25:    */     }
/*  26: 65 */     this.items[this.len] = miv;
/*  27: 66 */     this.len += 1;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public int len()
/*  31:    */   {
/*  32: 71 */     return this.len;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public boolean hydrated()
/*  36:    */   {
/*  37: 76 */     return this.map != null;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public Map.Entry<String, Value>[] items()
/*  41:    */   {
/*  42: 81 */     return this.items;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public Value get(Object key)
/*  46:    */   {
/*  47: 93 */     if ((this.map == null) && (this.items.length < 20))
/*  48:    */     {
/*  49: 94 */       for (Object item : this.items)
/*  50:    */       {
/*  51: 95 */         MapItemValue miv = (MapItemValue)item;
/*  52: 96 */         if (key.equals(miv.name.toValue())) {
/*  53: 97 */           return miv.value;
/*  54:    */         }
/*  55:    */       }
/*  56:100 */       return null;
/*  57:    */     }
/*  58:102 */     if (this.map == null) {
/*  59:102 */       buildIfNeededMap();
/*  60:    */     }
/*  61:103 */     return (Value)this.map.get(key);
/*  62:    */   }
/*  63:    */   
/*  64:    */   public Value put(String key, Value value)
/*  65:    */   {
/*  66:110 */     Exceptions.die("Not that kind of map");
/*  67:111 */     return null;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public Set<Map.Entry<String, Value>> entrySet()
/*  71:    */   {
/*  72:118 */     buildIfNeededMap();
/*  73:119 */     return this.map.entrySet();
/*  74:    */   }
/*  75:    */   
/*  76:    */   private final void buildIfNeededMap()
/*  77:    */   {
/*  78:124 */     if (this.map == null)
/*  79:    */     {
/*  80:125 */       this.map = new HashMap(this.items.length);
/*  81:127 */       for (Map.Entry<String, Value> miv : this.items)
/*  82:    */       {
/*  83:128 */         if (miv == null) {
/*  84:    */           break;
/*  85:    */         }
/*  86:131 */         this.map.put(miv.getKey(), miv.getValue());
/*  87:    */       }
/*  88:    */     }
/*  89:    */   }
/*  90:    */   
/*  91:    */   public Collection<Value> values()
/*  92:    */   {
/*  93:139 */     buildIfNeededMap();
/*  94:140 */     return this.map.values();
/*  95:    */   }
/*  96:    */   
/*  97:    */   public int size()
/*  98:    */   {
/*  99:149 */     buildIfNeededMap();
/* 100:150 */     return this.map.size();
/* 101:    */   }
/* 102:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.value.ValueMapImpl
 * JD-Core Version:    0.7.0.1
 */