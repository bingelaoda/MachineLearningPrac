/*   1:    */ package weka.classifiers.lazy.kstar;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import weka.core.RevisionHandler;
/*   5:    */ import weka.core.RevisionUtils;
/*   6:    */ 
/*   7:    */ public class KStarCache
/*   8:    */   implements Serializable, RevisionHandler
/*   9:    */ {
/*  10:    */   private static final long serialVersionUID = -7693632394267140678L;
/*  11:    */   CacheTable m_Cache;
/*  12:    */   
/*  13:    */   public KStarCache()
/*  14:    */   {
/*  15: 46 */     this.m_Cache = new CacheTable();
/*  16:    */   }
/*  17:    */   
/*  18:    */   public void store(double key, double value, double pmiss)
/*  19:    */   {
/*  20: 57 */     if (!this.m_Cache.containsKey(key)) {
/*  21: 58 */       this.m_Cache.insert(key, value, pmiss);
/*  22:    */     }
/*  23:    */   }
/*  24:    */   
/*  25:    */   public boolean containsKey(double key)
/*  26:    */   {
/*  27: 68 */     if (this.m_Cache.containsKey(key)) {
/*  28: 69 */       return true;
/*  29:    */     }
/*  30: 71 */     return false;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public TableEntry getCacheValues(double key)
/*  34:    */   {
/*  35: 80 */     if (this.m_Cache.containsKey(key)) {
/*  36: 81 */       return this.m_Cache.getEntry(key);
/*  37:    */     }
/*  38: 83 */     return null;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public class CacheTable
/*  42:    */     implements Serializable, RevisionHandler
/*  43:    */   {
/*  44:    */     private static final long serialVersionUID = -8086106452588253423L;
/*  45:    */     private KStarCache.TableEntry[] m_Table;
/*  46:    */     private int m_Count;
/*  47:    */     private int m_Threshold;
/*  48:    */     private final float m_LoadFactor;
/*  49:108 */     private final double EPSILON = 1.E-005D;
/*  50:    */     
/*  51:    */     public CacheTable(int size, float loadFactor)
/*  52:    */     {
/*  53:114 */       this.m_Table = new KStarCache.TableEntry[size];
/*  54:115 */       this.m_LoadFactor = loadFactor;
/*  55:116 */       this.m_Threshold = ((int)(size * loadFactor));
/*  56:117 */       this.m_Count = 0;
/*  57:    */     }
/*  58:    */     
/*  59:    */     public CacheTable()
/*  60:    */     {
/*  61:124 */       this(101, 0.75F);
/*  62:    */     }
/*  63:    */     
/*  64:    */     public boolean containsKey(double key)
/*  65:    */     {
/*  66:131 */       KStarCache.TableEntry[] table = this.m_Table;
/*  67:132 */       int hash = hashCode(key);
/*  68:133 */       int index = (hash & 0x7FFFFFFF) % table.length;
/*  69:134 */       for (KStarCache.TableEntry e = table[index]; e != null; e = e.next) {
/*  70:135 */         if ((e.hash == hash) && (Math.abs(e.key - key) < 1.E-005D)) {
/*  71:136 */           return true;
/*  72:    */         }
/*  73:    */       }
/*  74:139 */       return false;
/*  75:    */     }
/*  76:    */     
/*  77:    */     public void insert(double key, double value, double pmiss)
/*  78:    */     {
/*  79:149 */       KStarCache.TableEntry[] table = this.m_Table;
/*  80:150 */       int hash = hashCode(key);
/*  81:151 */       int index = (hash & 0x7FFFFFFF) % table.length;
/*  82:153 */       for (KStarCache.TableEntry e = table[index]; e != null; e = e.next) {
/*  83:154 */         if ((e.hash == hash) && (Math.abs(e.key - key) < 1.E-005D)) {
/*  84:155 */           return;
/*  85:    */         }
/*  86:    */       }
/*  87:160 */       KStarCache.TableEntry ne = new KStarCache.TableEntry(KStarCache.this, hash, key, value, pmiss, table[index]);
/*  88:    */       
/*  89:162 */       table[index] = ne;
/*  90:163 */       this.m_Count += 1;
/*  91:165 */       if (this.m_Count >= this.m_Threshold) {
/*  92:166 */         rehash();
/*  93:    */       }
/*  94:    */     }
/*  95:    */     
/*  96:    */     public KStarCache.TableEntry getEntry(double key)
/*  97:    */     {
/*  98:177 */       KStarCache.TableEntry[] table = this.m_Table;
/*  99:178 */       int hash = hashCode(key);
/* 100:179 */       int index = (hash & 0x7FFFFFFF) % table.length;
/* 101:180 */       for (KStarCache.TableEntry e = table[index]; e != null; e = e.next) {
/* 102:181 */         if ((e.hash == hash) && (Math.abs(e.key - key) < 1.E-005D)) {
/* 103:182 */           return e;
/* 104:    */         }
/* 105:    */       }
/* 106:185 */       return null;
/* 107:    */     }
/* 108:    */     
/* 109:    */     public int size()
/* 110:    */     {
/* 111:194 */       return this.m_Count;
/* 112:    */     }
/* 113:    */     
/* 114:    */     public boolean isEmpty()
/* 115:    */     {
/* 116:203 */       return this.m_Count == 0;
/* 117:    */     }
/* 118:    */     
/* 119:    */     public void clear()
/* 120:    */     {
/* 121:210 */       KStarCache.TableEntry[] table = this.m_Table;
/* 122:211 */       int index = table.length;
/* 123:    */       for (;;)
/* 124:    */       {
/* 125:211 */         index--;
/* 126:211 */         if (index < 0) {
/* 127:    */           break;
/* 128:    */         }
/* 129:212 */         table[index] = null;
/* 130:    */       }
/* 131:214 */       this.m_Count = 0;
/* 132:    */     }
/* 133:    */     
/* 134:    */     private void rehash()
/* 135:    */     {
/* 136:223 */       int oldCapacity = this.m_Table.length;
/* 137:224 */       KStarCache.TableEntry[] oldTable = this.m_Table;
/* 138:225 */       int newCapacity = oldCapacity * 2 + 1;
/* 139:226 */       KStarCache.TableEntry[] newTable = new KStarCache.TableEntry[newCapacity];
/* 140:227 */       this.m_Threshold = ((int)(newCapacity * this.m_LoadFactor));
/* 141:228 */       this.m_Table = newTable;
/* 142:230 */       for (int i = oldCapacity; i-- > 0;) {
/* 143:231 */         for (old = oldTable[i]; old != null;)
/* 144:    */         {
/* 145:232 */           KStarCache.TableEntry e = old;
/* 146:233 */           old = old.next;
/* 147:234 */           int index = (e.hash & 0x7FFFFFFF) % newCapacity;
/* 148:235 */           e.next = newTable[index];
/* 149:236 */           newTable[index] = e;
/* 150:    */         }
/* 151:    */       }
/* 152:    */       KStarCache.TableEntry old;
/* 153:    */     }
/* 154:    */     
/* 155:    */     private int hashCode(double key)
/* 156:    */     {
/* 157:247 */       long bits = Double.doubleToLongBits(key);
/* 158:248 */       return (int)(bits ^ bits >> 32);
/* 159:    */     }
/* 160:    */     
/* 161:    */     public String getRevision()
/* 162:    */     {
/* 163:258 */       return RevisionUtils.extract("$Revision: 10153 $");
/* 164:    */     }
/* 165:    */   }
/* 166:    */   
/* 167:    */   public class TableEntry
/* 168:    */     implements Serializable, RevisionHandler
/* 169:    */   {
/* 170:    */     private static final long serialVersionUID = 4057602386766259138L;
/* 171:    */     public int hash;
/* 172:    */     public double key;
/* 173:    */     public double value;
/* 174:    */     public double pmiss;
/* 175:283 */     public TableEntry next = null;
/* 176:    */     
/* 177:    */     public TableEntry(int hash, double key, double value, double pmiss, TableEntry next)
/* 178:    */     {
/* 179:288 */       this.hash = hash;
/* 180:289 */       this.key = key;
/* 181:290 */       this.value = value;
/* 182:291 */       this.pmiss = pmiss;
/* 183:292 */       this.next = next;
/* 184:    */     }
/* 185:    */     
/* 186:    */     public String getRevision()
/* 187:    */     {
/* 188:302 */       return RevisionUtils.extract("$Revision: 10153 $");
/* 189:    */     }
/* 190:    */   }
/* 191:    */   
/* 192:    */   public String getRevision()
/* 193:    */   {
/* 194:313 */     return RevisionUtils.extract("$Revision: 10153 $");
/* 195:    */   }
/* 196:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.lazy.kstar.KStarCache
 * JD-Core Version:    0.7.0.1
 */