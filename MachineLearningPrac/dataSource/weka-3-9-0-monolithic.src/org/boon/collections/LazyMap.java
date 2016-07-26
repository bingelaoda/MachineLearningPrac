/*   1:    */ package org.boon.collections;
/*   2:    */ 
/*   3:    */ import java.util.AbstractMap;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.Collection;
/*   6:    */ import java.util.LinkedHashMap;
/*   7:    */ import java.util.List;
/*   8:    */ import java.util.Map;
/*   9:    */ import java.util.Map.Entry;
/*  10:    */ import java.util.Set;
/*  11:    */ import java.util.TreeMap;
/*  12:    */ import org.boon.Maps;
/*  13:    */ import org.boon.Sets;
/*  14:    */ import org.boon.core.Sys;
/*  15:    */ import org.boon.primitive.Arry;
/*  16:    */ 
/*  17:    */ public class LazyMap
/*  18:    */   extends AbstractMap<String, Object>
/*  19:    */ {
/*  20: 45 */   static final boolean althashingThreshold = System.getProperty("jdk.map.althashing.threshold") != null;
/*  21:    */   private final boolean delayMap;
/*  22:    */   private Map<String, Object> map;
/*  23:    */   private int size;
/*  24:    */   private String[] keys;
/*  25:    */   private Object[] values;
/*  26:    */   
/*  27:    */   public LazyMap()
/*  28:    */   {
/*  29: 57 */     this.keys = new String[5];
/*  30: 58 */     this.values = new Object[5];
/*  31: 59 */     this.delayMap = false;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public LazyMap(int initialSize)
/*  35:    */   {
/*  36: 64 */     this.keys = new String[initialSize];
/*  37: 65 */     this.values = new Object[initialSize];
/*  38: 66 */     this.delayMap = false;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public LazyMap(int initialSize, boolean delayMap)
/*  42:    */   {
/*  43: 72 */     this.keys = new String[initialSize];
/*  44: 73 */     this.values = new Object[initialSize];
/*  45: 74 */     this.delayMap = delayMap;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public LazyMap(List<String> keys, List values, boolean delayMap)
/*  49:    */   {
/*  50: 80 */     this.keys = ((String[])Arry.array(String.class, keys));
/*  51: 81 */     this.values = Arry.array(Object.class, values);
/*  52:    */     
/*  53: 83 */     this.size = this.keys.length;
/*  54:    */     
/*  55: 85 */     this.delayMap = delayMap;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public Object put(String key, Object value)
/*  59:    */   {
/*  60: 90 */     if (this.map == null)
/*  61:    */     {
/*  62: 91 */       this.keys[this.size] = key;
/*  63: 92 */       this.values[this.size] = value;
/*  64: 93 */       this.size += 1;
/*  65: 94 */       if (this.size == this.keys.length)
/*  66:    */       {
/*  67: 95 */         this.keys = ((String[])Arry.grow(this.keys));
/*  68: 96 */         this.values = Arry.grow(this.values);
/*  69:    */       }
/*  70: 98 */       return null;
/*  71:    */     }
/*  72:100 */     return this.map.put(key, value);
/*  73:    */   }
/*  74:    */   
/*  75:    */   public Set<Map.Entry<String, Object>> entrySet()
/*  76:    */   {
/*  77:106 */     if (this.map != null) {
/*  78:106 */       this.map.entrySet();
/*  79:    */     }
/*  80:108 */     if (this.delayMap) {
/*  81:110 */       return new FakeMapEntrySet(this.size, this.keys, this.values);
/*  82:    */     }
/*  83:112 */     buildIfNeeded();
/*  84:113 */     return this.map.entrySet();
/*  85:    */   }
/*  86:    */   
/*  87:    */   public int size()
/*  88:    */   {
/*  89:119 */     if (this.map == null) {
/*  90:120 */       return this.size;
/*  91:    */     }
/*  92:122 */     return this.map.size();
/*  93:    */   }
/*  94:    */   
/*  95:    */   public boolean isEmpty()
/*  96:    */   {
/*  97:128 */     if (this.map == null) {
/*  98:129 */       return this.size == 0;
/*  99:    */     }
/* 100:131 */     return this.map.isEmpty();
/* 101:    */   }
/* 102:    */   
/* 103:    */   public boolean containsValue(Object value)
/* 104:    */   {
/* 105:137 */     if (this.map == null) {
/* 106:138 */       throw new RuntimeException("wrong type of map");
/* 107:    */     }
/* 108:140 */     return this.map.containsValue(value);
/* 109:    */   }
/* 110:    */   
/* 111:    */   public boolean containsKey(Object key)
/* 112:    */   {
/* 113:146 */     buildIfNeeded();
/* 114:147 */     return this.map.containsKey(key);
/* 115:    */   }
/* 116:    */   
/* 117:    */   public Object get(Object key)
/* 118:    */   {
/* 119:152 */     buildIfNeeded();
/* 120:153 */     return this.map.get(key);
/* 121:    */   }
/* 122:    */   
/* 123:    */   private void buildIfNeeded()
/* 124:    */   {
/* 125:157 */     if (this.map == null)
/* 126:    */     {
/* 127:160 */       if ((Sys.is1_7OrLater()) && (althashingThreshold)) {
/* 128:161 */         this.map = new LinkedHashMap(this.size, 0.01F);
/* 129:    */       } else {
/* 130:163 */         this.map = new TreeMap();
/* 131:    */       }
/* 132:166 */       for (int index = 0; index < this.size; index++) {
/* 133:167 */         this.map.put(this.keys[index], this.values[index]);
/* 134:    */       }
/* 135:169 */       this.keys = null;
/* 136:170 */       this.values = null;
/* 137:    */     }
/* 138:    */   }
/* 139:    */   
/* 140:    */   public Object remove(Object key)
/* 141:    */   {
/* 142:177 */     buildIfNeeded();
/* 143:178 */     return this.map.remove(key);
/* 144:    */   }
/* 145:    */   
/* 146:    */   public void putAll(Map m)
/* 147:    */   {
/* 148:184 */     buildIfNeeded();
/* 149:185 */     this.map.putAll(m);
/* 150:    */   }
/* 151:    */   
/* 152:    */   public void clear()
/* 153:    */   {
/* 154:190 */     if (this.map == null) {
/* 155:191 */       this.size = 0;
/* 156:    */     } else {
/* 157:193 */       this.map.clear();
/* 158:    */     }
/* 159:    */   }
/* 160:    */   
/* 161:    */   public Set<String> keySet()
/* 162:    */   {
/* 163:201 */     if (this.map == null) {
/* 164:202 */       return Sets.set(this.size, this.keys);
/* 165:    */     }
/* 166:204 */     return this.map.keySet();
/* 167:    */   }
/* 168:    */   
/* 169:    */   public Collection<Object> values()
/* 170:    */   {
/* 171:211 */     if (this.map == null) {
/* 172:212 */       return Arrays.asList(this.values);
/* 173:    */     }
/* 174:214 */     return this.map.values();
/* 175:    */   }
/* 176:    */   
/* 177:    */   public boolean equals(Object o)
/* 178:    */   {
/* 179:221 */     buildIfNeeded();
/* 180:222 */     return this.map.equals(o);
/* 181:    */   }
/* 182:    */   
/* 183:    */   public int hashCode()
/* 184:    */   {
/* 185:227 */     buildIfNeeded();
/* 186:228 */     return this.map.hashCode();
/* 187:    */   }
/* 188:    */   
/* 189:    */   public String toString()
/* 190:    */   {
/* 191:234 */     buildIfNeeded();
/* 192:235 */     return this.map.toString();
/* 193:    */   }
/* 194:    */   
/* 195:    */   protected Object clone()
/* 196:    */     throws CloneNotSupportedException
/* 197:    */   {
/* 198:241 */     if (this.map == null) {
/* 199:242 */       return null;
/* 200:    */     }
/* 201:244 */     if ((this.map instanceof LinkedHashMap)) {
/* 202:245 */       return ((LinkedHashMap)this.map).clone();
/* 203:    */     }
/* 204:247 */     return Maps.copy(this);
/* 205:    */   }
/* 206:    */   
/* 207:    */   public LazyMap clearAndCopy()
/* 208:    */   {
/* 209:253 */     LazyMap map = new LazyMap(this.size);
/* 210:254 */     for (int index = 0; index < this.size; index++) {
/* 211:255 */       map.put(this.keys[index], this.values[index]);
/* 212:    */     }
/* 213:257 */     this.size = 0;
/* 214:258 */     return map;
/* 215:    */   }
/* 216:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.collections.LazyMap
 * JD-Core Version:    0.7.0.1
 */