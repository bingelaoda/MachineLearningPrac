/*   1:    */ package org.boon.datarepo.impl.indexes;
/*   2:    */ 
/*   3:    */ import java.util.Collection;
/*   4:    */ import java.util.Comparator;
/*   5:    */ import java.util.List;
/*   6:    */ import org.boon.core.Function;
/*   7:    */ import org.boon.datarepo.spi.SearchIndex;
/*   8:    */ 
/*   9:    */ public abstract class BaseIndexWrapper
/*  10:    */   implements SearchIndex
/*  11:    */ {
/*  12:    */   protected final String[] path;
/*  13: 47 */   protected SearchIndexDefault index = new SearchIndexDefault(Object.class);
/*  14:    */   
/*  15:    */   public BaseIndexWrapper(String... path)
/*  16:    */   {
/*  17: 50 */     this.path = path;
/*  18:    */   }
/*  19:    */   
/*  20:    */   public Object findFirst()
/*  21:    */   {
/*  22: 55 */     return this.index.findFirst();
/*  23:    */   }
/*  24:    */   
/*  25:    */   public Object findLast()
/*  26:    */   {
/*  27: 60 */     return this.index.findLast();
/*  28:    */   }
/*  29:    */   
/*  30:    */   public Object findFirstKey()
/*  31:    */   {
/*  32: 65 */     return this.index.findFirstKey();
/*  33:    */   }
/*  34:    */   
/*  35:    */   public Object findLastKey()
/*  36:    */   {
/*  37: 70 */     return this.index.findLastKey();
/*  38:    */   }
/*  39:    */   
/*  40:    */   public List findEquals(Object o)
/*  41:    */   {
/*  42: 75 */     return this.index.findEquals(o);
/*  43:    */   }
/*  44:    */   
/*  45:    */   public List findStartsWith(Object keyFrag)
/*  46:    */   {
/*  47: 80 */     return this.index.findEquals(keyFrag);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public List findEndsWith(Object keyFrag)
/*  51:    */   {
/*  52: 85 */     return this.index.findEndsWith(keyFrag);
/*  53:    */   }
/*  54:    */   
/*  55:    */   public List findContains(Object keyFrag)
/*  56:    */   {
/*  57: 90 */     return this.index.findContains(keyFrag);
/*  58:    */   }
/*  59:    */   
/*  60:    */   public List findBetween(Object start, Object end)
/*  61:    */   {
/*  62: 95 */     return this.index.findBetween(start, end);
/*  63:    */   }
/*  64:    */   
/*  65:    */   public List findGreaterThan(Object o)
/*  66:    */   {
/*  67:100 */     return this.index.findGreaterThan(o);
/*  68:    */   }
/*  69:    */   
/*  70:    */   public List findLessThan(Object o)
/*  71:    */   {
/*  72:105 */     return this.index.findLessThan(o);
/*  73:    */   }
/*  74:    */   
/*  75:    */   public List findGreaterThanEqual(Object o)
/*  76:    */   {
/*  77:110 */     return this.index.findGreaterThanEqual(o);
/*  78:    */   }
/*  79:    */   
/*  80:    */   public List findLessThanEqual(Object o)
/*  81:    */   {
/*  82:116 */     return this.index.findLessThanEqual(o);
/*  83:    */   }
/*  84:    */   
/*  85:    */   public Object min()
/*  86:    */   {
/*  87:121 */     return this.index.min();
/*  88:    */   }
/*  89:    */   
/*  90:    */   public Object max()
/*  91:    */   {
/*  92:126 */     return this.index.max();
/*  93:    */   }
/*  94:    */   
/*  95:    */   public int count(Object o)
/*  96:    */   {
/*  97:131 */     return this.index.count(o);
/*  98:    */   }
/*  99:    */   
/* 100:    */   public void setComparator(Comparator collator)
/* 101:    */   {
/* 102:136 */     this.index.setComparator(collator);
/* 103:    */   }
/* 104:    */   
/* 105:    */   public Object get(Object o)
/* 106:    */   {
/* 107:141 */     return this.index.get(o);
/* 108:    */   }
/* 109:    */   
/* 110:    */   public void setKeyGetter(Function keyGetter)
/* 111:    */   {
/* 112:146 */     this.index.setKeyGetter(keyGetter);
/* 113:    */   }
/* 114:    */   
/* 115:    */   public List getAll(Object o)
/* 116:    */   {
/* 117:151 */     return this.index.getAll(o);
/* 118:    */   }
/* 119:    */   
/* 120:    */   public boolean deleteByKey(Object o)
/* 121:    */   {
/* 122:156 */     return this.index.deleteByKey(o);
/* 123:    */   }
/* 124:    */   
/* 125:    */   public boolean isPrimaryKeyOnly()
/* 126:    */   {
/* 127:161 */     return this.index.isPrimaryKeyOnly();
/* 128:    */   }
/* 129:    */   
/* 130:    */   public void init()
/* 131:    */   {
/* 132:166 */     this.index.init();
/* 133:    */   }
/* 134:    */   
/* 135:    */   public void setInputKeyTransformer(Function func)
/* 136:    */   {
/* 137:171 */     this.index.setInputKeyTransformer(func);
/* 138:    */   }
/* 139:    */   
/* 140:    */   public abstract boolean add(Object paramObject);
/* 141:    */   
/* 142:    */   protected abstract List getKeys(Object paramObject);
/* 143:    */   
/* 144:    */   public abstract boolean delete(Object paramObject);
/* 145:    */   
/* 146:    */   public List all()
/* 147:    */   {
/* 148:184 */     return this.index.all();
/* 149:    */   }
/* 150:    */   
/* 151:    */   public int size()
/* 152:    */   {
/* 153:189 */     return this.index.size();
/* 154:    */   }
/* 155:    */   
/* 156:    */   public Collection toCollection()
/* 157:    */   {
/* 158:194 */     return this.index.toCollection();
/* 159:    */   }
/* 160:    */   
/* 161:    */   public void clear()
/* 162:    */   {
/* 163:199 */     this.index.clear();
/* 164:    */   }
/* 165:    */   
/* 166:    */   public void setBucketSize(int size)
/* 167:    */   {
/* 168:203 */     this.index.setBucketSize(size);
/* 169:    */   }
/* 170:    */   
/* 171:    */   public boolean has(Object key)
/* 172:    */   {
/* 173:208 */     return this.index.has(key);
/* 174:    */   }
/* 175:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.impl.indexes.BaseIndexWrapper
 * JD-Core Version:    0.7.0.1
 */