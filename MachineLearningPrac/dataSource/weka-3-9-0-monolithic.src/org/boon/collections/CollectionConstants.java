/*   1:    */ package org.boon.collections;
/*   2:    */ 
/*   3:    */ import java.util.Collection;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Iterator;
/*   6:    */ import java.util.Map;
/*   7:    */ import java.util.Map.Entry;
/*   8:    */ import java.util.Set;
/*   9:    */ 
/*  10:    */ public class CollectionConstants
/*  11:    */ {
/*  12: 12 */   public static MultiMap EMPTY_MULTI_MAP = new MultiMap()
/*  13:    */   {
/*  14: 14 */     Map empty = Collections.emptyMap();
/*  15:    */     
/*  16:    */     public Iterator<Map.Entry> iterator()
/*  17:    */     {
/*  18: 17 */       return this.empty.entrySet().iterator();
/*  19:    */     }
/*  20:    */     
/*  21:    */     public void add(Object key, Object o) {}
/*  22:    */     
/*  23:    */     public Object getFirst(Object key)
/*  24:    */     {
/*  25: 28 */       return null;
/*  26:    */     }
/*  27:    */     
/*  28:    */     public Iterable getAll(Object key)
/*  29:    */     {
/*  30: 33 */       return Collections.EMPTY_LIST;
/*  31:    */     }
/*  32:    */     
/*  33:    */     public boolean removeValueFrom(Object key, Object o)
/*  34:    */     {
/*  35: 38 */       return false;
/*  36:    */     }
/*  37:    */     
/*  38:    */     public boolean removeMulti(Object key)
/*  39:    */     {
/*  40: 44 */       return false;
/*  41:    */     }
/*  42:    */     
/*  43:    */     public Iterable keySetMulti()
/*  44:    */     {
/*  45: 49 */       return Collections.EMPTY_LIST;
/*  46:    */     }
/*  47:    */     
/*  48:    */     public Iterable valueMulti()
/*  49:    */     {
/*  50: 54 */       return Collections.EMPTY_LIST;
/*  51:    */     }
/*  52:    */     
/*  53:    */     public void putAll(MultiMap params) {}
/*  54:    */     
/*  55:    */     public Map baseMap()
/*  56:    */     {
/*  57: 64 */       return this.empty;
/*  58:    */     }
/*  59:    */     
/*  60:    */     public Object getSingleObject(Object name)
/*  61:    */     {
/*  62: 69 */       return null;
/*  63:    */     }
/*  64:    */     
/*  65:    */     public int size()
/*  66:    */     {
/*  67: 74 */       return 0;
/*  68:    */     }
/*  69:    */     
/*  70:    */     public boolean isEmpty()
/*  71:    */     {
/*  72: 79 */       return true;
/*  73:    */     }
/*  74:    */     
/*  75:    */     public boolean containsKey(Object key)
/*  76:    */     {
/*  77: 84 */       return false;
/*  78:    */     }
/*  79:    */     
/*  80:    */     public boolean containsValue(Object value)
/*  81:    */     {
/*  82: 89 */       return false;
/*  83:    */     }
/*  84:    */     
/*  85:    */     public Object get(Object key)
/*  86:    */     {
/*  87: 94 */       return this.empty.get(key);
/*  88:    */     }
/*  89:    */     
/*  90:    */     public Object put(Object key, Object value)
/*  91:    */     {
/*  92: 99 */       return this.empty.put(key, value);
/*  93:    */     }
/*  94:    */     
/*  95:    */     public Object remove(Object key)
/*  96:    */     {
/*  97:104 */       return this.empty.remove(key);
/*  98:    */     }
/*  99:    */     
/* 100:    */     public void putAll(Map m)
/* 101:    */     {
/* 102:110 */       this.empty.putAll(m);
/* 103:    */     }
/* 104:    */     
/* 105:    */     public void clear()
/* 106:    */     {
/* 107:115 */       this.empty.clear();
/* 108:    */     }
/* 109:    */     
/* 110:    */     public Set keySet()
/* 111:    */     {
/* 112:120 */       return this.empty.keySet();
/* 113:    */     }
/* 114:    */     
/* 115:    */     public Collection values()
/* 116:    */     {
/* 117:125 */       return this.empty.values();
/* 118:    */     }
/* 119:    */     
/* 120:    */     public Set<Map.Entry> entrySet()
/* 121:    */     {
/* 122:130 */       return this.empty.entrySet();
/* 123:    */     }
/* 124:    */   };
/* 125:    */   
/* 126:    */   public static final <K, V> MultiMap<K, V> emptyMultiMap()
/* 127:    */   {
/* 128:136 */     return EMPTY_MULTI_MAP;
/* 129:    */   }
/* 130:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.collections.CollectionConstants
 * JD-Core Version:    0.7.0.1
 */