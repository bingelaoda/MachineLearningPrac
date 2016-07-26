/*   1:    */ package org.boon.datarepo.spi;
/*   2:    */ 
/*   3:    */ import org.boon.core.Function;
/*   4:    */ import org.boon.core.Supplier;
/*   5:    */ import org.boon.core.Typ;
/*   6:    */ import org.boon.datarepo.Filter;
/*   7:    */ import org.boon.datarepo.LookupIndex;
/*   8:    */ import org.boon.datarepo.RepoBuilder;
/*   9:    */ import org.boon.datarepo.impl.FilterDefault;
/*  10:    */ import org.boon.datarepo.impl.ObjectEditorDefault;
/*  11:    */ import org.boon.datarepo.impl.RepoBuilderDefault;
/*  12:    */ import org.boon.datarepo.impl.RepoDefault;
/*  13:    */ import org.boon.datarepo.impl.SearchableCollectionDefault;
/*  14:    */ import org.boon.datarepo.impl.indexes.LookupIndexDefault;
/*  15:    */ import org.boon.datarepo.impl.indexes.SearchIndexDefault;
/*  16:    */ import org.boon.datarepo.impl.indexes.UniqueLookupIndex;
/*  17:    */ import org.boon.datarepo.impl.indexes.UniqueSearchIndex;
/*  18:    */ import org.boon.datarepo.impl.maps.MapCreatorImpl;
/*  19:    */ 
/*  20:    */ public class SPIFactory
/*  21:    */ {
/*  22: 50 */   static Supplier<MapCreator> mapCreatorFactory = null;
/*  23: 51 */   static Supplier<RepoBuilder> repoBuilderFactory = null;
/*  24: 52 */   static Function<Class, SearchIndex> searchIndexFactory = null;
/*  25: 53 */   static Function<Class, LookupIndex> uniqueLookupIndexFactory = null;
/*  26: 54 */   static Function<Class, SearchIndex> uniqueSearchIndexFactory = null;
/*  27: 55 */   static Function<Class, LookupIndex> lookupIndexFactory = null;
/*  28: 56 */   static Supplier<RepoComposer> repoFactory = null;
/*  29: 57 */   static Supplier<Filter> filterFactory = null;
/*  30: 58 */   static Supplier<SearchableCollectionComposer> searchableCollectionFactory = null;
/*  31:    */   static Supplier<ObjectEditorComposer> objectEditorFactory;
/*  32:    */   
/*  33:    */   public static Supplier<MapCreator> getMapCreatorFactory()
/*  34:    */   {
/*  35: 62 */     return mapCreatorFactory;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public static Supplier<SearchableCollectionComposer> getSearchableCollectionFactory()
/*  39:    */   {
/*  40: 66 */     return searchableCollectionFactory;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public static Supplier<RepoBuilder> getRepoBuilderFactory()
/*  44:    */   {
/*  45: 70 */     return repoBuilderFactory;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public static Function<Class, SearchIndex> getSearchIndexFactory()
/*  49:    */   {
/*  50: 74 */     return searchIndexFactory;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public static Function<Class, SearchIndex> getUniqueSearchIndexFactory()
/*  54:    */   {
/*  55: 78 */     return uniqueSearchIndexFactory;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public static Function<Class, LookupIndex> getLookupIndexFactory()
/*  59:    */   {
/*  60: 82 */     return lookupIndexFactory;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public static Function<Class, LookupIndex> getUniqueLookupIndexFactory()
/*  64:    */   {
/*  65: 86 */     return uniqueLookupIndexFactory;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public static Supplier<RepoComposer> getRepoFactory()
/*  69:    */   {
/*  70: 90 */     return repoFactory;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public static Supplier<Filter> getFilterFactory()
/*  74:    */   {
/*  75: 94 */     return filterFactory;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public static void init()
/*  79:    */   {
/*  80: 99 */     if (mapCreatorFactory == null) {
/*  81:100 */       mapCreatorFactory = new Supplier()
/*  82:    */       {
/*  83:    */         public MapCreator get()
/*  84:    */         {
/*  85:103 */           return new MapCreatorImpl();
/*  86:    */         }
/*  87:    */       };
/*  88:    */     }
/*  89:107 */     if (repoBuilderFactory == null) {
/*  90:108 */       repoBuilderFactory = new Supplier()
/*  91:    */       {
/*  92:    */         public RepoBuilder get()
/*  93:    */         {
/*  94:111 */           return new RepoBuilderDefault();
/*  95:    */         }
/*  96:    */       };
/*  97:    */     }
/*  98:115 */     if (searchIndexFactory == null) {
/*  99:116 */       searchIndexFactory = new Function()
/* 100:    */       {
/* 101:    */         public SearchIndex apply(Class keyType)
/* 102:    */         {
/* 103:118 */           if (keyType == Typ.string) {
/* 104:119 */             return new SearchIndexDefault(keyType);
/* 105:    */           }
/* 106:122 */           return new SearchIndexDefault(keyType);
/* 107:    */         }
/* 108:    */       };
/* 109:    */     }
/* 110:127 */     if (lookupIndexFactory == null) {
/* 111:128 */       lookupIndexFactory = new Function()
/* 112:    */       {
/* 113:    */         public LookupIndex apply(Class keyType)
/* 114:    */         {
/* 115:131 */           return new LookupIndexDefault(keyType);
/* 116:    */         }
/* 117:    */       };
/* 118:    */     }
/* 119:135 */     if (uniqueLookupIndexFactory == null) {
/* 120:136 */       uniqueLookupIndexFactory = new Function()
/* 121:    */       {
/* 122:    */         public LookupIndex apply(Class keyType)
/* 123:    */         {
/* 124:139 */           return new UniqueLookupIndex(keyType);
/* 125:    */         }
/* 126:    */       };
/* 127:    */     }
/* 128:143 */     if (uniqueSearchIndexFactory == null) {
/* 129:144 */       uniqueSearchIndexFactory = new Function()
/* 130:    */       {
/* 131:    */         public SearchIndex apply(Class keyType)
/* 132:    */         {
/* 133:147 */           return new UniqueSearchIndex(keyType);
/* 134:    */         }
/* 135:    */       };
/* 136:    */     }
/* 137:152 */     if (repoFactory == null) {
/* 138:153 */       repoFactory = new Supplier()
/* 139:    */       {
/* 140:    */         public RepoComposer get()
/* 141:    */         {
/* 142:156 */           return new RepoDefault();
/* 143:    */         }
/* 144:    */       };
/* 145:    */     }
/* 146:161 */     if (filterFactory == null) {
/* 147:162 */       filterFactory = new Supplier()
/* 148:    */       {
/* 149:    */         public Filter get()
/* 150:    */         {
/* 151:165 */           return new FilterDefault();
/* 152:    */         }
/* 153:    */       };
/* 154:    */     }
/* 155:170 */     if (searchableCollectionFactory == null) {
/* 156:171 */       searchableCollectionFactory = new Supplier()
/* 157:    */       {
/* 158:    */         public SearchableCollectionComposer get()
/* 159:    */         {
/* 160:174 */           return new SearchableCollectionDefault();
/* 161:    */         }
/* 162:    */       };
/* 163:    */     }
/* 164:179 */     if (objectEditorFactory == null) {
/* 165:180 */       objectEditorFactory = new Supplier()
/* 166:    */       {
/* 167:    */         public ObjectEditorComposer get()
/* 168:    */         {
/* 169:183 */           return new ObjectEditorDefault();
/* 170:    */         }
/* 171:    */       };
/* 172:    */     }
/* 173:    */   }
/* 174:    */   
/* 175:    */   static
/* 176:    */   {
/* 177:191 */     init();
/* 178:    */   }
/* 179:    */   
/* 180:    */   public static void setMapCreatorFactory(Supplier<MapCreator> mapCreatorFactory)
/* 181:    */   {
/* 182:196 */     mapCreatorFactory = mapCreatorFactory;
/* 183:    */   }
/* 184:    */   
/* 185:    */   public static void setRepoBuilderFactory(Supplier<RepoBuilder> repoBuilderFactory)
/* 186:    */   {
/* 187:200 */     repoBuilderFactory = repoBuilderFactory;
/* 188:    */   }
/* 189:    */   
/* 190:    */   public static void setSearchIndexFactory(Function<Class, SearchIndex> searchIndexFactory)
/* 191:    */   {
/* 192:204 */     searchIndexFactory = searchIndexFactory;
/* 193:    */   }
/* 194:    */   
/* 195:    */   public static void setLookupIndexFactory(Function<Class, LookupIndex> lookupIndexFactory)
/* 196:    */   {
/* 197:208 */     lookupIndexFactory = lookupIndexFactory;
/* 198:    */   }
/* 199:    */   
/* 200:    */   public static void setUniqueLookupIndexFactory(Function<Class, LookupIndex> lookupIndexFactory)
/* 201:    */   {
/* 202:213 */     uniqueLookupIndexFactory = lookupIndexFactory;
/* 203:    */   }
/* 204:    */   
/* 205:    */   public static void setUniqueSearchIndexFactory(Function<Class, SearchIndex> factory)
/* 206:    */   {
/* 207:217 */     uniqueSearchIndexFactory = factory;
/* 208:    */   }
/* 209:    */   
/* 210:    */   public static void setRepoFactory(Supplier<RepoComposer> repoFactory)
/* 211:    */   {
/* 212:221 */     repoFactory = repoFactory;
/* 213:    */   }
/* 214:    */   
/* 215:    */   public static void setFilterFactory(Supplier<Filter> filterFactory)
/* 216:    */   {
/* 217:225 */     filterFactory = filterFactory;
/* 218:    */   }
/* 219:    */   
/* 220:    */   public static Supplier<ObjectEditorComposer> getObjectEditorFactory()
/* 221:    */   {
/* 222:229 */     return objectEditorFactory;
/* 223:    */   }
/* 224:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.spi.SPIFactory
 * JD-Core Version:    0.7.0.1
 */