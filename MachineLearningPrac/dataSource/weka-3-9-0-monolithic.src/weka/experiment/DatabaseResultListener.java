/*   1:    */ package weka.experiment;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.sql.Connection;
/*   5:    */ import java.sql.DatabaseMetaData;
/*   6:    */ import java.sql.ResultSet;
/*   7:    */ import java.util.ArrayList;
/*   8:    */ import weka.core.RevisionUtils;
/*   9:    */ 
/*  10:    */ public class DatabaseResultListener
/*  11:    */   extends DatabaseUtils
/*  12:    */   implements ResultListener
/*  13:    */ {
/*  14:    */   static final long serialVersionUID = 7388014746954652818L;
/*  15:    */   protected ResultProducer m_ResultProducer;
/*  16:    */   protected String m_ResultsTableName;
/*  17: 52 */   protected String m_CacheKeyName = "";
/*  18:    */   protected int m_CacheKeyIndex;
/*  19:    */   protected Object[] m_CacheKey;
/*  20: 61 */   protected ArrayList<String> m_Cache = new ArrayList();
/*  21:    */   
/*  22:    */   public String globalInfo()
/*  23:    */   {
/*  24: 70 */     return "Takes results from a result producer and sends them to a database.";
/*  25:    */   }
/*  26:    */   
/*  27:    */   public DatabaseResultListener()
/*  28:    */     throws Exception
/*  29:    */   {}
/*  30:    */   
/*  31:    */   public void preProcess(ResultProducer rp)
/*  32:    */     throws Exception
/*  33:    */   {
/*  34: 93 */     this.m_ResultProducer = rp;
/*  35:    */     
/*  36:    */ 
/*  37: 96 */     updateResultsTableName(this.m_ResultProducer);
/*  38:    */   }
/*  39:    */   
/*  40:    */   public void postProcess(ResultProducer rp)
/*  41:    */     throws Exception
/*  42:    */   {
/*  43:109 */     if (this.m_ResultProducer != rp) {
/*  44:110 */       throw new Error("Unrecognized ResultProducer calling postProcess!!");
/*  45:    */     }
/*  46:112 */     disconnectFromDatabase();
/*  47:    */   }
/*  48:    */   
/*  49:    */   public String[] determineColumnConstraints(ResultProducer rp)
/*  50:    */     throws Exception
/*  51:    */   {
/*  52:130 */     ArrayList<String> cNames = new ArrayList();
/*  53:131 */     updateResultsTableName(rp);
/*  54:132 */     DatabaseMetaData dbmd = this.m_Connection.getMetaData();
/*  55:    */     ResultSet rs;
/*  56:    */     ResultSet rs;
/*  57:135 */     if (this.m_checkForUpperCaseNames) {
/*  58:136 */       rs = dbmd.getColumns(null, null, this.m_ResultsTableName.toUpperCase(), null);
/*  59:    */     } else {
/*  60:138 */       rs = dbmd.getColumns(null, null, this.m_ResultsTableName, null);
/*  61:    */     }
/*  62:140 */     boolean tableExists = false;
/*  63:141 */     int numColumns = 0;
/*  64:143 */     while (rs.next())
/*  65:    */     {
/*  66:144 */       tableExists = true;
/*  67:    */       
/*  68:146 */       String name = rs.getString(4);
/*  69:147 */       if (name.toLowerCase().startsWith("measure"))
/*  70:    */       {
/*  71:148 */         numColumns++;
/*  72:149 */         cNames.add(name);
/*  73:    */       }
/*  74:    */     }
/*  75:154 */     if (!tableExists) {
/*  76:155 */       return null;
/*  77:    */     }
/*  78:159 */     String[] columnNames = new String[numColumns];
/*  79:160 */     for (int i = 0; i < numColumns; i++) {
/*  80:161 */       columnNames[i] = ((String)cNames.get(i));
/*  81:    */     }
/*  82:164 */     return columnNames;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public void acceptResult(ResultProducer rp, Object[] key, Object[] result)
/*  86:    */     throws Exception
/*  87:    */   {
/*  88:179 */     if (this.m_ResultProducer != rp) {
/*  89:180 */       throw new Error("Unrecognized ResultProducer calling acceptResult!!");
/*  90:    */     }
/*  91:184 */     if (result != null) {
/*  92:185 */       putResultInTable(this.m_ResultsTableName, rp, key, result);
/*  93:    */     }
/*  94:    */   }
/*  95:    */   
/*  96:    */   public boolean isResultRequired(ResultProducer rp, Object[] key)
/*  97:    */     throws Exception
/*  98:    */   {
/*  99:202 */     if (this.m_ResultProducer != rp) {
/* 100:203 */       throw new Error("Unrecognized ResultProducer calling isResultRequired!");
/* 101:    */     }
/* 102:205 */     if (this.m_Debug)
/* 103:    */     {
/* 104:206 */       System.err.print("Is result required...");
/* 105:207 */       for (Object element : key) {
/* 106:208 */         System.err.print(" " + element);
/* 107:    */       }
/* 108:210 */       System.err.flush();
/* 109:    */     }
/* 110:212 */     boolean retval = false;
/* 111:215 */     if (!this.m_CacheKeyName.equals(""))
/* 112:    */     {
/* 113:216 */       if (!isCacheValid(key)) {
/* 114:217 */         loadCache(rp, key);
/* 115:    */       }
/* 116:219 */       retval = !isKeyInCache(rp, key);
/* 117:    */     }
/* 118:    */     else
/* 119:    */     {
/* 120:222 */       retval = !isKeyInTable(this.m_ResultsTableName, rp, key);
/* 121:    */     }
/* 122:225 */     if (this.m_Debug)
/* 123:    */     {
/* 124:226 */       System.err.println(" ..." + (retval ? "required" : "not required") + (this.m_CacheKeyName.equals("") ? "" : " (cache)"));
/* 125:    */       
/* 126:228 */       System.err.flush();
/* 127:    */     }
/* 128:230 */     return retval;
/* 129:    */   }
/* 130:    */   
/* 131:    */   protected void updateResultsTableName(ResultProducer rp)
/* 132:    */     throws Exception
/* 133:    */   {
/* 134:243 */     if (!isConnected()) {
/* 135:244 */       connectToDatabase();
/* 136:    */     }
/* 137:246 */     if (!experimentIndexExists()) {
/* 138:247 */       createExperimentIndex();
/* 139:    */     }
/* 140:250 */     String tableName = getResultsTableName(rp);
/* 141:251 */     if (tableName == null) {
/* 142:252 */       tableName = createExperimentIndexEntry(rp);
/* 143:    */     }
/* 144:254 */     if (!tableExists(tableName)) {
/* 145:255 */       createResultsTable(rp, tableName);
/* 146:    */     }
/* 147:257 */     this.m_ResultsTableName = tableName;
/* 148:    */   }
/* 149:    */   
/* 150:    */   public String cacheKeyNameTipText()
/* 151:    */   {
/* 152:267 */     return "Set the name of the key field by which to cache.";
/* 153:    */   }
/* 154:    */   
/* 155:    */   public String getCacheKeyName()
/* 156:    */   {
/* 157:277 */     return this.m_CacheKeyName;
/* 158:    */   }
/* 159:    */   
/* 160:    */   public void setCacheKeyName(String newCacheKeyName)
/* 161:    */   {
/* 162:287 */     this.m_CacheKeyName = newCacheKeyName;
/* 163:    */   }
/* 164:    */   
/* 165:    */   protected boolean isCacheValid(Object[] key)
/* 166:    */   {
/* 167:298 */     if (this.m_CacheKey == null) {
/* 168:299 */       return false;
/* 169:    */     }
/* 170:301 */     if (this.m_CacheKey.length != key.length) {
/* 171:302 */       return false;
/* 172:    */     }
/* 173:304 */     for (int i = 0; i < key.length; i++) {
/* 174:305 */       if ((i != this.m_CacheKeyIndex) && (!this.m_CacheKey[i].equals(key[i]))) {
/* 175:306 */         return false;
/* 176:    */       }
/* 177:    */     }
/* 178:309 */     return true;
/* 179:    */   }
/* 180:    */   
/* 181:    */   protected boolean isKeyInCache(ResultProducer rp, Object[] key)
/* 182:    */     throws Exception
/* 183:    */   {
/* 184:324 */     for (int i = 0; i < this.m_Cache.size(); i++) {
/* 185:325 */       if (((String)this.m_Cache.get(i)).equals(key[this.m_CacheKeyIndex])) {
/* 186:326 */         return true;
/* 187:    */       }
/* 188:    */     }
/* 189:329 */     return false;
/* 190:    */   }
/* 191:    */   
/* 192:    */   protected void loadCache(ResultProducer rp, Object[] key)
/* 193:    */     throws Exception
/* 194:    */   {
/* 195:341 */     System.err.print(" (updating cache)");
/* 196:342 */     System.err.flush();
/* 197:343 */     this.m_Cache.clear();
/* 198:344 */     this.m_CacheKey = null;
/* 199:345 */     String query = "SELECT Key_" + this.m_CacheKeyName + " FROM " + this.m_ResultsTableName;
/* 200:    */     
/* 201:347 */     String[] keyNames = rp.getKeyNames();
/* 202:348 */     if (keyNames.length != key.length) {
/* 203:349 */       throw new Exception("Key names and key values of different lengths");
/* 204:    */     }
/* 205:351 */     this.m_CacheKeyIndex = -1;
/* 206:352 */     for (int i = 0; i < keyNames.length; i++) {
/* 207:353 */       if (keyNames[i].equalsIgnoreCase(this.m_CacheKeyName))
/* 208:    */       {
/* 209:354 */         this.m_CacheKeyIndex = i;
/* 210:355 */         break;
/* 211:    */       }
/* 212:    */     }
/* 213:358 */     if (this.m_CacheKeyIndex == -1) {
/* 214:359 */       throw new Exception("No key field named " + this.m_CacheKeyName + " (as specified for caching)");
/* 215:    */     }
/* 216:362 */     boolean first = true;
/* 217:363 */     for (int i = 0; i < key.length; i++) {
/* 218:364 */       if ((key[i] != null) && (i != this.m_CacheKeyIndex))
/* 219:    */       {
/* 220:365 */         if (first)
/* 221:    */         {
/* 222:366 */           query = query + " WHERE ";
/* 223:367 */           first = false;
/* 224:    */         }
/* 225:    */         else
/* 226:    */         {
/* 227:369 */           query = query + " AND ";
/* 228:    */         }
/* 229:371 */         query = query + "Key_" + keyNames[i] + '=';
/* 230:372 */         if ((key[i] instanceof String)) {
/* 231:373 */           query = query + "'" + DatabaseUtils.processKeyString(key[i].toString()) + "'";
/* 232:    */         } else {
/* 233:376 */           query = query + key[i].toString();
/* 234:    */         }
/* 235:    */       }
/* 236:    */     }
/* 237:380 */     ResultSet rs = select(query);
/* 238:381 */     while (rs.next())
/* 239:    */     {
/* 240:382 */       String keyVal = rs.getString(1);
/* 241:383 */       if (!rs.wasNull()) {
/* 242:384 */         this.m_Cache.add(keyVal);
/* 243:    */       }
/* 244:    */     }
/* 245:387 */     close(rs);
/* 246:388 */     this.m_CacheKey = ((Object[])key.clone());
/* 247:    */   }
/* 248:    */   
/* 249:    */   public String getRevision()
/* 250:    */   {
/* 251:398 */     return RevisionUtils.extract("$Revision: 11247 $");
/* 252:    */   }
/* 253:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.DatabaseResultListener
 * JD-Core Version:    0.7.0.1
 */