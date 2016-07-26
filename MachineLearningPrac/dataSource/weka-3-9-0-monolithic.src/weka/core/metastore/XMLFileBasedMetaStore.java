/*   1:    */ package weka.core.metastore;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.util.HashSet;
/*   6:    */ import java.util.LinkedHashMap;
/*   7:    */ import java.util.Map;
/*   8:    */ import java.util.Map.Entry;
/*   9:    */ import java.util.Set;
/*  10:    */ import weka.associations.Associator;
/*  11:    */ import weka.attributeSelection.ASEvaluation;
/*  12:    */ import weka.attributeSelection.ASSearch;
/*  13:    */ import weka.classifiers.Classifier;
/*  14:    */ import weka.clusterers.Clusterer;
/*  15:    */ import weka.core.WekaPackageManager;
/*  16:    */ import weka.core.converters.DatabaseLoader;
/*  17:    */ import weka.core.converters.DatabaseSaver;
/*  18:    */ import weka.core.converters.Loader;
/*  19:    */ import weka.core.converters.Saver;
/*  20:    */ import weka.core.converters.TextDirectoryLoader;
/*  21:    */ import weka.core.xml.PropertyHandler;
/*  22:    */ import weka.core.xml.XMLBasicSerialization;
/*  23:    */ import weka.experiment.ResultProducer;
/*  24:    */ import weka.experiment.SplitEvaluator;
/*  25:    */ import weka.filters.Filter;
/*  26:    */ 
/*  27:    */ public class XMLFileBasedMetaStore
/*  28:    */   implements MetaStore
/*  29:    */ {
/*  30: 45 */   public static final String DEFAULT_STORE_LOCATION = WekaPackageManager.WEKA_HOME.toString() + File.separator + "wekaMetaStore";
/*  31: 49 */   protected File m_storeHome = new File(DEFAULT_STORE_LOCATION);
/*  32:    */   protected boolean m_storeDirOK;
/*  33: 57 */   protected Map<String, Map<String, File>> m_stores = new LinkedHashMap();
/*  34:    */   
/*  35:    */   protected synchronized void establishStoreHome()
/*  36:    */     throws IOException
/*  37:    */   {
/*  38: 66 */     if (this.m_storeDirOK) {
/*  39: 67 */       return;
/*  40:    */     }
/*  41: 70 */     if ((!this.m_storeHome.exists()) && 
/*  42: 71 */       (!this.m_storeHome.mkdir())) {
/*  43: 72 */       throw new IOException("Unable to create the metastore directory: " + this.m_storeHome.toString());
/*  44:    */     }
/*  45: 77 */     if (!this.m_storeHome.isDirectory()) {
/*  46: 78 */       throw new IOException("The metastore (" + this.m_storeHome + ") seems to exist, but it isn't a directory!");
/*  47:    */     }
/*  48: 82 */     this.m_storeDirOK = true;
/*  49:    */     
/*  50: 84 */     lockStore();
/*  51:    */     
/*  52: 86 */     File[] contents = this.m_storeHome.listFiles();
/*  53: 87 */     for (File f : contents) {
/*  54: 88 */       if (f.isDirectory())
/*  55:    */       {
/*  56: 89 */         Map<String, File> store = new LinkedHashMap();
/*  57: 90 */         this.m_stores.put(f.getName(), store);
/*  58:    */         
/*  59: 92 */         File[] storeEntries = f.listFiles();
/*  60: 93 */         for (File se : storeEntries) {
/*  61: 94 */           store.put(se.getName(), se);
/*  62:    */         }
/*  63:    */       }
/*  64:    */     }
/*  65: 98 */     unlockStore();
/*  66:    */   }
/*  67:    */   
/*  68:    */   public Set<String> listMetaStores()
/*  69:    */     throws IOException
/*  70:    */   {
/*  71:103 */     return this.m_stores.keySet();
/*  72:    */   }
/*  73:    */   
/*  74:    */   public Set<String> listMetaStoreEntries(String storeName)
/*  75:    */     throws IOException
/*  76:    */   {
/*  77:108 */     establishStoreHome();
/*  78:    */     
/*  79:110 */     Set<String> results = new HashSet();
/*  80:111 */     Map<String, File> store = (Map)this.m_stores.get(storeName);
/*  81:112 */     if (store != null) {
/*  82:113 */       results.addAll(store.keySet());
/*  83:    */     }
/*  84:116 */     return results;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public synchronized Set<String> listMetaStoreEntries(String storeName, String prefix)
/*  88:    */     throws IOException
/*  89:    */   {
/*  90:122 */     establishStoreHome();
/*  91:123 */     Set<String> matches = new HashSet();
/*  92:124 */     Map<String, File> store = (Map)this.m_stores.get(storeName);
/*  93:126 */     if (store != null) {
/*  94:127 */       for (Map.Entry<String, File> e : store.entrySet()) {
/*  95:128 */         if (((String)e.getKey()).startsWith(prefix)) {
/*  96:129 */           matches.add(e.getKey());
/*  97:    */         }
/*  98:    */       }
/*  99:    */     }
/* 100:134 */     return matches;
/* 101:    */   }
/* 102:    */   
/* 103:    */   public Object getEntry(String storeName, String name, Class<?> clazz)
/* 104:    */     throws IOException
/* 105:    */   {
/* 106:140 */     establishStoreHome();
/* 107:    */     
/* 108:142 */     Map<String, File> store = (Map)this.m_stores.get(storeName);
/* 109:144 */     if ((store != null) && 
/* 110:145 */       (store.containsKey(name)))
/* 111:    */     {
/* 112:146 */       File toLoad = (File)store.get(name);
/* 113:    */       try
/* 114:    */       {
/* 115:149 */         lockStore();
/* 116:150 */         XMLBasicSerialization deserializer = getSerializer();
/* 117:151 */         Object loaded = deserializer.read(toLoad);
/* 118:153 */         if (loaded.getClass().equals(clazz)) {
/* 119:154 */           throw new IOException("Deserialized entry (" + loaded.getClass().getName() + ") was not " + "the expected class: " + clazz.getName());
/* 120:    */         }
/* 121:159 */         return loaded;
/* 122:    */       }
/* 123:    */       catch (Exception ex)
/* 124:    */       {
/* 125:161 */         throw new IOException(ex);
/* 126:    */       }
/* 127:    */       finally
/* 128:    */       {
/* 129:163 */         unlockStore();
/* 130:    */       }
/* 131:    */     }
/* 132:168 */     return null;
/* 133:    */   }
/* 134:    */   
/* 135:    */   public void createStore(String storeName)
/* 136:    */     throws IOException
/* 137:    */   {
/* 138:173 */     File store = new File(this.m_storeHome, storeName);
/* 139:174 */     if (store.exists()) {
/* 140:175 */       throw new IOException("Meta store '" + storeName + "' already exists!");
/* 141:    */     }
/* 142:178 */     lockStore();
/* 143:    */     try
/* 144:    */     {
/* 145:180 */       if (!store.mkdir()) {
/* 146:181 */         throw new IOException("Unable to create meta store '" + storeName + "'");
/* 147:    */       }
/* 148:    */     }
/* 149:    */     finally
/* 150:    */     {
/* 151:185 */       unlockStore();
/* 152:    */     }
/* 153:    */   }
/* 154:    */   
/* 155:    */   public synchronized void storeEntry(String storeName, String name, Object toStore)
/* 156:    */     throws IOException
/* 157:    */   {
/* 158:192 */     establishStoreHome();
/* 159:193 */     Map<String, File> store = (Map)this.m_stores.get(storeName);
/* 160:194 */     if (store == null)
/* 161:    */     {
/* 162:195 */       createStore(storeName);
/* 163:196 */       store = new LinkedHashMap();
/* 164:197 */       this.m_stores.put(storeName, store);
/* 165:    */     }
/* 166:200 */     File loc = new File(this.m_storeHome.toString() + File.separator + storeName, name);
/* 167:    */     
/* 168:202 */     store.put(name, loc);
/* 169:    */     try
/* 170:    */     {
/* 171:204 */       lockStore();
/* 172:205 */       XMLBasicSerialization serializer = getSerializer();
/* 173:206 */       serializer.write(loc, toStore);
/* 174:    */     }
/* 175:    */     catch (Exception ex)
/* 176:    */     {
/* 177:208 */       throw new IOException(ex);
/* 178:    */     }
/* 179:    */     finally
/* 180:    */     {
/* 181:210 */       unlockStore();
/* 182:    */     }
/* 183:    */   }
/* 184:    */   
/* 185:    */   protected void lockStore()
/* 186:    */     throws IOException
/* 187:    */   {
/* 188:220 */     int totalWaitTime = 0;
/* 189:    */     for (;;)
/* 190:    */     {
/* 191:222 */       File lock = new File(this.m_storeHome, ".lock");
/* 192:224 */       if (lock.createNewFile()) {
/* 193:225 */         return;
/* 194:    */       }
/* 195:    */       try
/* 196:    */       {
/* 197:228 */         Thread.sleep(200L);
/* 198:    */       }
/* 199:    */       catch (InterruptedException ex)
/* 200:    */       {
/* 201:230 */         throw new RuntimeException(ex);
/* 202:    */       }
/* 203:232 */       totalWaitTime += 200;
/* 204:233 */       if (totalWaitTime > 5000) {
/* 205:234 */         throw new IOException("Unable to lock store within 5 seconds");
/* 206:    */       }
/* 207:    */     }
/* 208:    */   }
/* 209:    */   
/* 210:    */   protected void unlockStore()
/* 211:    */   {
/* 212:243 */     File lock = new File(this.m_storeHome, ".lock");
/* 213:244 */     lock.delete();
/* 214:    */   }
/* 215:    */   
/* 216:    */   protected XMLBasicSerialization getSerializer()
/* 217:    */   {
/* 218:    */     try
/* 219:    */     {
/* 220:256 */       new XMLBasicSerialization()
/* 221:    */       {
/* 222:    */         public void clear()
/* 223:    */           throws Exception
/* 224:    */         {
/* 225:258 */           super.clear();
/* 226:    */           
/* 227:260 */           this.m_Properties.addAllowed(Classifier.class, "debug");
/* 228:261 */           this.m_Properties.addAllowed(Classifier.class, "options");
/* 229:262 */           this.m_Properties.addAllowed(Associator.class, "options");
/* 230:    */           
/* 231:264 */           this.m_Properties.addAllowed(Clusterer.class, "options");
/* 232:265 */           this.m_Properties.addAllowed(Filter.class, "options");
/* 233:266 */           this.m_Properties.addAllowed(Saver.class, "options");
/* 234:267 */           this.m_Properties.addAllowed(Loader.class, "options");
/* 235:268 */           this.m_Properties.addAllowed(ASSearch.class, "options");
/* 236:    */           
/* 237:270 */           this.m_Properties.addAllowed(ASEvaluation.class, "options");
/* 238:    */           
/* 239:    */ 
/* 240:273 */           this.m_Properties.addAllowed(DatabaseSaver.class, "options");
/* 241:    */           
/* 242:275 */           this.m_Properties.addAllowed(DatabaseLoader.class, "options");
/* 243:    */           
/* 244:277 */           this.m_Properties.addAllowed(TextDirectoryLoader.class, "options");
/* 245:    */           
/* 246:    */ 
/* 247:    */ 
/* 248:    */ 
/* 249:282 */           this.m_Properties.addAllowed(SplitEvaluator.class, "options");
/* 250:    */           
/* 251:    */ 
/* 252:    */ 
/* 253:286 */           this.m_Properties.addAllowed(ResultProducer.class, "options");
/* 254:    */         }
/* 255:    */       };
/* 256:    */     }
/* 257:    */     catch (Exception ex)
/* 258:    */     {
/* 259:293 */       ex.printStackTrace();
/* 260:    */     }
/* 261:296 */     return null;
/* 262:    */   }
/* 263:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.metastore.XMLFileBasedMetaStore
 * JD-Core Version:    0.7.0.1
 */