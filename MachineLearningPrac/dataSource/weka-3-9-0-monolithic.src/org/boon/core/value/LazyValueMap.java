/*   1:    */ package org.boon.core.value;
/*   2:    */ 
/*   3:    */ import java.util.AbstractMap;
/*   4:    */ import java.util.Collection;
/*   5:    */ import java.util.HashMap;
/*   6:    */ import java.util.Map;
/*   7:    */ import java.util.Map.Entry;
/*   8:    */ import java.util.Set;
/*   9:    */ import java.util.TreeMap;
/*  10:    */ import org.boon.core.Sys;
/*  11:    */ import org.boon.core.Value;
/*  12:    */ import org.boon.primitive.Arry;
/*  13:    */ 
/*  14:    */ public class LazyValueMap
/*  15:    */   extends AbstractMap<String, Object>
/*  16:    */   implements ValueMap<String, Object>
/*  17:    */ {
/*  18: 61 */   private Map<String, Object> map = null;
/*  19:    */   private Map.Entry<String, Value>[] items;
/*  20: 65 */   private int len = 0;
/*  21:    */   private final boolean lazyChop;
/*  22: 70 */   boolean mapChopped = false;
/*  23:    */   
/*  24:    */   public LazyValueMap(boolean lazyChop)
/*  25:    */   {
/*  26: 78 */     this.items = new Map.Entry[5];
/*  27: 79 */     this.lazyChop = lazyChop;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public LazyValueMap(boolean lazyChop, int initialSize)
/*  31:    */   {
/*  32: 83 */     this.items = new Map.Entry[initialSize];
/*  33: 84 */     this.lazyChop = lazyChop;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public final void add(MapItemValue miv)
/*  37:    */   {
/*  38: 92 */     if (this.len >= this.items.length) {
/*  39: 93 */       this.items = ((Map.Entry[])Arry.grow(this.items));
/*  40:    */     }
/*  41: 95 */     this.items[this.len] = miv;
/*  42: 96 */     this.len += 1;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public final Object get(Object key)
/*  46:    */   {
/*  47:109 */     Object object = null;
/*  48:112 */     if (this.map == null) {
/*  49:113 */       buildMap();
/*  50:    */     }
/*  51:115 */     object = this.map.get(key);
/*  52:    */     
/*  53:117 */     lazyChopIfNeeded(object);
/*  54:    */     
/*  55:119 */     return object;
/*  56:    */   }
/*  57:    */   
/*  58:    */   private void lazyChopIfNeeded(Object object)
/*  59:    */   {
/*  60:125 */     if (this.lazyChop) {
/*  61:126 */       if ((object instanceof LazyValueMap))
/*  62:    */       {
/*  63:127 */         LazyValueMap m = (LazyValueMap)object;
/*  64:128 */         m.chopMap();
/*  65:    */       }
/*  66:129 */       else if ((object instanceof ValueList))
/*  67:    */       {
/*  68:130 */         ValueList list = (ValueList)object;
/*  69:131 */         list.chopList();
/*  70:    */       }
/*  71:    */     }
/*  72:    */   }
/*  73:    */   
/*  74:    */   public final void chopMap()
/*  75:    */   {
/*  76:142 */     if (this.mapChopped) {
/*  77:143 */       return;
/*  78:    */     }
/*  79:145 */     this.mapChopped = true;
/*  80:149 */     if (this.map == null) {
/*  81:150 */       for (int index = 0; index < this.len; index++)
/*  82:    */       {
/*  83:151 */         MapItemValue entry = (MapItemValue)this.items[index];
/*  84:    */         
/*  85:153 */         Value value = entry.getValue();
/*  86:154 */         if (value != null) {
/*  87:155 */           if (value.isContainer()) {
/*  88:156 */             chopContainer(value);
/*  89:    */           } else {
/*  90:158 */             value.chop();
/*  91:    */           }
/*  92:    */         }
/*  93:    */       }
/*  94:    */     } else {
/*  95:163 */       for (Map.Entry<String, Object> entry : this.map.entrySet())
/*  96:    */       {
/*  97:165 */         Object object = entry.getValue();
/*  98:166 */         if ((object instanceof Value))
/*  99:    */         {
/* 100:167 */           Value value = (Value)object;
/* 101:168 */           if (value.isContainer()) {
/* 102:169 */             chopContainer(value);
/* 103:    */           } else {
/* 104:171 */             value.chop();
/* 105:    */           }
/* 106:    */         }
/* 107:173 */         else if ((object instanceof LazyValueMap))
/* 108:    */         {
/* 109:174 */           LazyValueMap m = (LazyValueMap)object;
/* 110:175 */           m.chopMap();
/* 111:    */         }
/* 112:176 */         else if ((object instanceof ValueList))
/* 113:    */         {
/* 114:177 */           ValueList list = (ValueList)object;
/* 115:178 */           list.chopList();
/* 116:    */         }
/* 117:    */       }
/* 118:    */     }
/* 119:    */   }
/* 120:    */   
/* 121:    */   private void chopContainer(Value value)
/* 122:    */   {
/* 123:187 */     Object obj = value.toValue();
/* 124:188 */     if ((obj instanceof LazyValueMap))
/* 125:    */     {
/* 126:189 */       LazyValueMap map = (LazyValueMap)obj;
/* 127:190 */       map.chopMap();
/* 128:    */     }
/* 129:191 */     else if ((obj instanceof ValueList))
/* 130:    */     {
/* 131:192 */       ValueList list = (ValueList)obj;
/* 132:193 */       list.chopList();
/* 133:    */     }
/* 134:    */   }
/* 135:    */   
/* 136:    */   public Object put(String key, Object value)
/* 137:    */   {
/* 138:202 */     if (this.map == null) {
/* 139:203 */       buildMap();
/* 140:    */     }
/* 141:206 */     return this.map.put(key, value);
/* 142:    */   }
/* 143:    */   
/* 144:    */   public Set<Map.Entry<String, Object>> entrySet()
/* 145:    */   {
/* 146:213 */     if (this.map == null) {
/* 147:214 */       buildMap();
/* 148:    */     }
/* 149:216 */     return this.map.entrySet();
/* 150:    */   }
/* 151:    */   
/* 152:219 */   static final boolean althashingThreshold = System.getProperty("jdk.map.althashing.threshold") != null;
/* 153:    */   
/* 154:    */   private final void buildMap()
/* 155:    */   {
/* 156:225 */     if ((Sys.is1_7OrLater()) && (althashingThreshold)) {
/* 157:226 */       this.map = new HashMap(this.items.length);
/* 158:    */     } else {
/* 159:228 */       this.map = new TreeMap();
/* 160:    */     }
/* 161:232 */     for (Map.Entry<String, Value> miv : this.items)
/* 162:    */     {
/* 163:233 */       if (miv == null) {
/* 164:    */         break;
/* 165:    */       }
/* 166:236 */       this.map.put(miv.getKey(), ((Value)miv.getValue()).toValue());
/* 167:    */     }
/* 168:239 */     this.len = 0;
/* 169:240 */     this.items = null;
/* 170:    */   }
/* 171:    */   
/* 172:    */   public Collection<Object> values()
/* 173:    */   {
/* 174:245 */     if (this.map == null) {
/* 175:245 */       buildMap();
/* 176:    */     }
/* 177:246 */     return this.map.values();
/* 178:    */   }
/* 179:    */   
/* 180:    */   public int size()
/* 181:    */   {
/* 182:251 */     if (this.map == null) {
/* 183:251 */       buildMap();
/* 184:    */     }
/* 185:252 */     return this.map.size();
/* 186:    */   }
/* 187:    */   
/* 188:    */   public String toString()
/* 189:    */   {
/* 190:257 */     if (this.map == null) {
/* 191:257 */       buildMap();
/* 192:    */     }
/* 193:258 */     return this.map.toString();
/* 194:    */   }
/* 195:    */   
/* 196:    */   public int len()
/* 197:    */   {
/* 198:263 */     return this.len;
/* 199:    */   }
/* 200:    */   
/* 201:    */   public boolean hydrated()
/* 202:    */   {
/* 203:268 */     return this.map != null;
/* 204:    */   }
/* 205:    */   
/* 206:    */   public Map.Entry<String, Value>[] items()
/* 207:    */   {
/* 208:273 */     return this.items;
/* 209:    */   }
/* 210:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.value.LazyValueMap
 * JD-Core Version:    0.7.0.1
 */