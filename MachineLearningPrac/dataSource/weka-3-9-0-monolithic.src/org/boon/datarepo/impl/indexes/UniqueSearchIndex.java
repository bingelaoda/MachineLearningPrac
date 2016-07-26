/*   1:    */ package org.boon.datarepo.impl.indexes;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Comparator;
/*   6:    */ import java.util.List;
/*   7:    */ import java.util.Map.Entry;
/*   8:    */ import java.util.NavigableMap;
/*   9:    */ import java.util.SortedMap;
/*  10:    */ import org.boon.Lists;
/*  11:    */ import org.boon.core.Function;
/*  12:    */ import org.boon.core.Supplier;
/*  13:    */ import org.boon.datarepo.spi.MapCreator;
/*  14:    */ import org.boon.datarepo.spi.SPIFactory;
/*  15:    */ import org.boon.datarepo.spi.SearchIndex;
/*  16:    */ import org.boon.primitive.CharBuf;
/*  17:    */ 
/*  18:    */ public class UniqueSearchIndex<KEY, ITEM>
/*  19:    */   extends UniqueLookupIndex<KEY, ITEM>
/*  20:    */   implements SearchIndex<KEY, ITEM>
/*  21:    */ {
/*  22:    */   private Class<?> keyType;
/*  23:    */   private NavigableMap<KEY, ITEM> navigableMap;
/*  24:    */   private Comparator collator;
/*  25:    */   
/*  26:    */   public UniqueSearchIndex(Class<?> keyType)
/*  27:    */   {
/*  28: 53 */     super(keyType);
/*  29: 54 */     this.keyType = keyType;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public UniqueSearchIndex(Class<?> keyType, List<ITEM> items, Function<ITEM, KEY> keyGetter)
/*  33:    */   {
/*  34: 59 */     super(keyType);
/*  35: 60 */     this.keyGetter = keyGetter;
/*  36: 61 */     this.map = ((MapCreator)SPIFactory.getMapCreatorFactory().get()).createMap(keyType);
/*  37:    */     
/*  38:    */ 
/*  39: 64 */     this.navigableMap = ((NavigableMap)this.map);
/*  40: 66 */     for (ITEM item : items) {
/*  41: 67 */       add(item);
/*  42:    */     }
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void setComparator(Comparator collator)
/*  46:    */   {
/*  47: 75 */     this.collator = collator;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void init()
/*  51:    */   {
/*  52: 80 */     this.map = ((MapCreator)SPIFactory.getMapCreatorFactory().get()).createNavigableMap(this.keyType, this.collator);
/*  53:    */     
/*  54:    */ 
/*  55: 83 */     this.navigableMap = ((NavigableMap)this.map);
/*  56:    */   }
/*  57:    */   
/*  58:    */   public ITEM findFirst()
/*  59:    */   {
/*  60: 89 */     return this.navigableMap.firstEntry().getValue();
/*  61:    */   }
/*  62:    */   
/*  63:    */   public ITEM findLast()
/*  64:    */   {
/*  65: 94 */     return this.navigableMap.lastEntry().getValue();
/*  66:    */   }
/*  67:    */   
/*  68:    */   public KEY findFirstKey()
/*  69:    */   {
/*  70: 99 */     return this.navigableMap.firstEntry().getKey();
/*  71:    */   }
/*  72:    */   
/*  73:    */   public KEY findLastKey()
/*  74:    */   {
/*  75:104 */     return this.navigableMap.lastEntry().getKey();
/*  76:    */   }
/*  77:    */   
/*  78:    */   public List<ITEM> findEquals(KEY key)
/*  79:    */   {
/*  80:109 */     key = getKey(key);
/*  81:110 */     return Lists.list(new Object[] { this.navigableMap.get(key) });
/*  82:    */   }
/*  83:    */   
/*  84:    */   public List<ITEM> findStartsWith(KEY keyFrag)
/*  85:    */   {
/*  86:115 */     keyFrag = getKey(keyFrag);
/*  87:119 */     if ((keyFrag instanceof String))
/*  88:    */     {
/*  89:120 */       String start = (String)keyFrag;
/*  90:121 */       if ((start.length() == 0) || (start == null)) {
/*  91:122 */         return Collections.EMPTY_LIST;
/*  92:    */       }
/*  93:125 */       char endLetter = start.charAt(start.length() - 1);
/*  94:126 */       String sub = start.substring(0, start.length() - 1);
/*  95:    */       
/*  96:128 */       CharBuf after = CharBuf.create(start.length());
/*  97:    */       
/*  98:130 */       after.add(String.valueOf(sub));
/*  99:131 */       after.add((char)(endLetter + '\001'));
/* 100:    */       
/* 101:133 */       NavigableMap<String, MultiValue<ITEM>> sortMap = this.navigableMap;
/* 102:    */       
/* 103:    */ 
/* 104:136 */       SortedMap<String, MultiValue<ITEM>> sortedSubMap = sortMap.subMap(start, after.toString());
/* 105:138 */       if (sortedSubMap.size() > 0)
/* 106:    */       {
/* 107:139 */         List<ITEM> results = new ArrayList();
/* 108:140 */         for (MultiValue<ITEM> values : sortedSubMap.values()) {
/* 109:141 */           values.addTo(results);
/* 110:    */         }
/* 111:143 */         return results;
/* 112:    */       }
/* 113:145 */       return Collections.EMPTY_LIST;
/* 114:    */     }
/* 115:147 */     return Collections.EMPTY_LIST;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public List<ITEM> findEndsWith(KEY keyFrag)
/* 119:    */   {
/* 120:153 */     throw new UnsupportedOperationException("findEndsWith Not supported");
/* 121:    */   }
/* 122:    */   
/* 123:    */   public List<ITEM> findContains(KEY keyFrag)
/* 124:    */   {
/* 125:158 */     throw new UnsupportedOperationException("findContains Not supported");
/* 126:    */   }
/* 127:    */   
/* 128:    */   public List<ITEM> findBetween(KEY start, KEY end)
/* 129:    */   {
/* 130:163 */     start = getKey(start);
/* 131:164 */     end = getKey(end);
/* 132:    */     
/* 133:166 */     SortedMap<KEY, ITEM> keyMultiValueSortedMap = this.navigableMap.subMap(start, end);
/* 134:    */     
/* 135:168 */     return new ArrayList(keyMultiValueSortedMap.values());
/* 136:    */   }
/* 137:    */   
/* 138:    */   public List<ITEM> findGreaterThan(KEY key)
/* 139:    */   {
/* 140:175 */     key = getKey(key);
/* 141:    */     
/* 142:177 */     SortedMap<KEY, ITEM> keyMultiValueSortedMap = this.navigableMap.tailMap(key, false);
/* 143:178 */     return new ArrayList(keyMultiValueSortedMap.values());
/* 144:    */   }
/* 145:    */   
/* 146:    */   public List<ITEM> findLessThan(KEY key)
/* 147:    */   {
/* 148:183 */     key = getKey(key);
/* 149:184 */     SortedMap<KEY, ITEM> keyMultiValueSortedMap = this.navigableMap.headMap(key, false);
/* 150:185 */     return new ArrayList(keyMultiValueSortedMap.values());
/* 151:    */   }
/* 152:    */   
/* 153:    */   public List<ITEM> findGreaterThanEqual(KEY key)
/* 154:    */   {
/* 155:190 */     key = getKey(key);
/* 156:191 */     SortedMap<KEY, ITEM> keyMultiValueSortedMap = this.navigableMap.tailMap(key);
/* 157:192 */     return new ArrayList(keyMultiValueSortedMap.values());
/* 158:    */   }
/* 159:    */   
/* 160:    */   public List<ITEM> findLessThanEqual(KEY key)
/* 161:    */   {
/* 162:197 */     key = getKey(key);
/* 163:198 */     SortedMap<KEY, ITEM> keyMultiValueSortedMap = this.navigableMap.headMap(key);
/* 164:199 */     return new ArrayList(keyMultiValueSortedMap.values());
/* 165:    */   }
/* 166:    */   
/* 167:    */   public ITEM min()
/* 168:    */   {
/* 169:204 */     return this.navigableMap.firstEntry().getValue();
/* 170:    */   }
/* 171:    */   
/* 172:    */   public ITEM max()
/* 173:    */   {
/* 174:209 */     return this.navigableMap.lastEntry().getValue();
/* 175:    */   }
/* 176:    */   
/* 177:    */   public List<ITEM> getAll(KEY key)
/* 178:    */   {
/* 179:214 */     return findEquals(key);
/* 180:    */   }
/* 181:    */   
/* 182:    */   public int size()
/* 183:    */   {
/* 184:219 */     return this.navigableMap.size();
/* 185:    */   }
/* 186:    */   
/* 187:    */   public int count(KEY key)
/* 188:    */   {
/* 189:225 */     return this.navigableMap.containsKey(key) ? 1 : 0;
/* 190:    */   }
/* 191:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.impl.indexes.UniqueSearchIndex
 * JD-Core Version:    0.7.0.1
 */