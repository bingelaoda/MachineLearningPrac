/*   1:    */ package org.boon.datarepo.impl.indexes;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collection;
/*   5:    */ import java.util.HashSet;
/*   6:    */ import java.util.List;
/*   7:    */ import java.util.Map;
/*   8:    */ import java.util.logging.Level;
/*   9:    */ import java.util.logging.Logger;
/*  10:    */ import org.boon.Lists;
/*  11:    */ import org.boon.core.Function;
/*  12:    */ import org.boon.core.Supplier;
/*  13:    */ import org.boon.datarepo.LookupIndex;
/*  14:    */ import org.boon.datarepo.spi.MapCreator;
/*  15:    */ import org.boon.datarepo.spi.SPIFactory;
/*  16:    */ 
/*  17:    */ public class UniqueLookupIndex<KEY, ITEM>
/*  18:    */   implements LookupIndex<KEY, ITEM>
/*  19:    */ {
/*  20:    */   protected Function<ITEM, KEY> keyGetter;
/*  21: 44 */   protected Map<KEY, ITEM> map = null;
/*  22: 47 */   private Logger log = Logger.getLogger(UniqueLookupIndex.class.getName());
/*  23:    */   private Function<Object, KEY> keyTransformer;
/*  24:    */   
/*  25:    */   public UniqueLookupIndex(Class<?> keyType)
/*  26:    */   {
/*  27: 52 */     if (keyType == null) {
/*  28: 53 */       return;
/*  29:    */     }
/*  30: 55 */     this.map = ((MapCreator)SPIFactory.getMapCreatorFactory().get()).createMap(keyType);
/*  31:    */   }
/*  32:    */   
/*  33:    */   public ITEM get(KEY key)
/*  34:    */   {
/*  35: 61 */     key = getKey(key);
/*  36: 62 */     return this.map.get(key);
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void setKeyGetter(Function<ITEM, KEY> keyGetter)
/*  40:    */   {
/*  41: 67 */     this.keyGetter = keyGetter;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public boolean add(ITEM item)
/*  45:    */   {
/*  46: 73 */     if (this.log.isLoggable(Level.FINE)) {
/*  47: 74 */       this.log.fine(String.format("addObject item = %s", new Object[] { item }));
/*  48:    */     }
/*  49: 77 */     KEY key = this.keyGetter.apply(item);
/*  50: 78 */     if (key == null) {
/*  51: 79 */       return false;
/*  52:    */     }
/*  53: 83 */     if (this.map.containsKey(key)) {
/*  54: 84 */       return false;
/*  55:    */     }
/*  56: 87 */     this.map.put(key, item);
/*  57: 88 */     return true;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public boolean delete(ITEM item)
/*  61:    */   {
/*  62: 95 */     if (this.log.isLoggable(Level.FINE)) {
/*  63: 96 */       this.log.fine(String.format("delete item = %s", new Object[] { item }));
/*  64:    */     }
/*  65: 99 */     KEY key = this.keyGetter.apply(item);
/*  66:100 */     key = getKey(key);
/*  67:101 */     return this.map.remove(key) != null;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public List<ITEM> all()
/*  71:    */   {
/*  72:107 */     if (this.log.isLoggable(Level.FINE)) {
/*  73:108 */       this.log.fine("all called ");
/*  74:    */     }
/*  75:111 */     return new ArrayList(this.map.values());
/*  76:    */   }
/*  77:    */   
/*  78:    */   public List<ITEM> getAll(KEY key)
/*  79:    */   {
/*  80:117 */     if (this.log.isLoggable(Level.FINE)) {
/*  81:118 */       this.log.fine("getAll called ");
/*  82:    */     }
/*  83:121 */     return Lists.list(new Object[] { get(key) });
/*  84:    */   }
/*  85:    */   
/*  86:    */   public int size()
/*  87:    */   {
/*  88:126 */     return this.map.size();
/*  89:    */   }
/*  90:    */   
/*  91:    */   public Collection<ITEM> toCollection()
/*  92:    */   {
/*  93:131 */     return new HashSet(this.map.values());
/*  94:    */   }
/*  95:    */   
/*  96:    */   public void clear()
/*  97:    */   {
/*  98:136 */     this.map.clear();
/*  99:    */   }
/* 100:    */   
/* 101:    */   public boolean deleteByKey(KEY key)
/* 102:    */   {
/* 103:141 */     key = getKey(key);
/* 104:142 */     return this.map.remove(key) != null;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public boolean isPrimaryKeyOnly()
/* 108:    */   {
/* 109:147 */     return false;
/* 110:    */   }
/* 111:    */   
/* 112:    */   public void init() {}
/* 113:    */   
/* 114:    */   public boolean has(KEY key)
/* 115:    */   {
/* 116:157 */     if (key == null) {
/* 117:158 */       return false;
/* 118:    */     }
/* 119:160 */     return this.map.containsKey(key);
/* 120:    */   }
/* 121:    */   
/* 122:    */   public void setInputKeyTransformer(Function<Object, KEY> func)
/* 123:    */   {
/* 124:166 */     this.keyTransformer = func;
/* 125:    */   }
/* 126:    */   
/* 127:    */   public void setBucketSize(int size) {}
/* 128:    */   
/* 129:    */   protected KEY getKey(KEY key)
/* 130:    */   {
/* 131:175 */     if (this.keyTransformer != null) {
/* 132:176 */       key = this.keyTransformer.apply(key);
/* 133:    */     }
/* 134:178 */     return key;
/* 135:    */   }
/* 136:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.impl.indexes.UniqueLookupIndex
 * JD-Core Version:    0.7.0.1
 */