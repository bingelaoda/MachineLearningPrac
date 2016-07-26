/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.awt.Font;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.IOException;
/*   6:    */ import java.io.Serializable;
/*   7:    */ import java.util.Collection;
/*   8:    */ import java.util.HashMap;
/*   9:    */ import java.util.Iterator;
/*  10:    */ import java.util.LinkedHashMap;
/*  11:    */ import java.util.List;
/*  12:    */ import java.util.Map;
/*  13:    */ import java.util.Map.Entry;
/*  14:    */ import java.util.Set;
/*  15:    */ import weka.core.metastore.MetaStore;
/*  16:    */ import weka.core.metastore.XMLFileBasedMetaStore;
/*  17:    */ import weka.knowledgeflow.LoggingLevel;
/*  18:    */ 
/*  19:    */ public class Settings
/*  20:    */   implements Serializable
/*  21:    */ {
/*  22:    */   private static final long serialVersionUID = -4005372566372478008L;
/*  23: 61 */   protected Map<String, Map<SettingKey, Object>> m_settings = new LinkedHashMap();
/*  24: 67 */   protected String m_storeName = "";
/*  25: 72 */   protected String m_ID = "";
/*  26:    */   
/*  27:    */   public void loadSettings()
/*  28:    */     throws IOException
/*  29:    */   {
/*  30: 81 */     MetaStore store = new XMLFileBasedMetaStore();
/*  31:    */     
/*  32: 83 */     Map<String, Map<SettingKey, Object>> loaded = (Map)store.getEntry(this.m_storeName, this.m_ID, Map.class);
/*  33: 87 */     if (loaded != null) {
/*  34: 88 */       this.m_settings = loaded;
/*  35:    */     }
/*  36: 92 */     for (Iterator i$ = this.m_settings.values().iterator(); i$.hasNext();)
/*  37:    */     {
/*  38: 92 */       s = (Map)i$.next();
/*  39: 93 */       for (Map.Entry<SettingKey, Object> e : s.entrySet()) {
/*  40: 94 */         if ((e.getValue() instanceof EnumHelper))
/*  41:    */         {
/*  42: 95 */           SettingKey key = (SettingKey)e.getKey();
/*  43: 96 */           EnumHelper eHelper = (EnumHelper)e.getValue();
/*  44:    */           try
/*  45:    */           {
/*  46: 99 */             Object actualValue = EnumHelper.valueFromString(eHelper.getEnumClass(), eHelper.getSelectedEnumValue());
/*  47:    */             
/*  48:    */ 
/*  49:102 */             s.put(key, actualValue);
/*  50:    */           }
/*  51:    */           catch (Exception ex)
/*  52:    */           {
/*  53:104 */             throw new IOException(ex);
/*  54:    */           }
/*  55:    */         }
/*  56:106 */         else if ((e.getValue() instanceof FontHelper))
/*  57:    */         {
/*  58:107 */           SettingKey key = (SettingKey)e.getKey();
/*  59:108 */           FontHelper fHelper = (FontHelper)e.getValue();
/*  60:109 */           Font f = fHelper.getFont();
/*  61:110 */           s.put(key, f);
/*  62:    */         }
/*  63:111 */         else if ((e.getValue() instanceof FileHelper))
/*  64:    */         {
/*  65:112 */           SettingKey key = (SettingKey)e.getKey();
/*  66:113 */           FileHelper fileHelper = (FileHelper)e.getValue();
/*  67:114 */           File f = fileHelper.getFile();
/*  68:115 */           s.put(key, f);
/*  69:    */         }
/*  70:    */       }
/*  71:    */     }
/*  72:    */     Map<SettingKey, Object> s;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public Settings(String storeName, String ID)
/*  76:    */   {
/*  77:129 */     this.m_storeName = storeName;
/*  78:130 */     this.m_ID = ID;
/*  79:    */     try
/*  80:    */     {
/*  81:132 */       loadSettings();
/*  82:    */     }
/*  83:    */     catch (IOException ex)
/*  84:    */     {
/*  85:134 */       throw new IllegalStateException(ex);
/*  86:    */     }
/*  87:    */   }
/*  88:    */   
/*  89:    */   public String getID()
/*  90:    */   {
/*  91:144 */     return this.m_ID;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public String getStoreName()
/*  95:    */   {
/*  96:153 */     return this.m_storeName;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public void applyDefaults(Defaults defaults)
/* 100:    */   {
/* 101:164 */     if (defaults == null) {
/* 102:165 */       return;
/* 103:    */     }
/* 104:167 */     Map<SettingKey, Object> settingsForID = (Map)this.m_settings.get(defaults.getID());
/* 105:168 */     if (settingsForID == null)
/* 106:    */     {
/* 107:169 */       settingsForID = new LinkedHashMap();
/* 108:170 */       this.m_settings.put(defaults.getID(), settingsForID);
/* 109:    */     }
/* 110:174 */     for (Map.Entry<SettingKey, Object> e : defaults.getDefaults().entrySet()) {
/* 111:175 */       if (!settingsForID.containsKey(e.getKey())) {
/* 112:176 */         settingsForID.put(e.getKey(), e.getValue());
/* 113:    */       }
/* 114:    */     }
/* 115:    */   }
/* 116:    */   
/* 117:    */   public Map<SettingKey, Object> getSettings(String settingsID)
/* 118:    */   {
/* 119:188 */     return (Map)this.m_settings.get(settingsID);
/* 120:    */   }
/* 121:    */   
/* 122:    */   public Set<String> getSettingsIDs()
/* 123:    */   {
/* 124:197 */     return this.m_settings.keySet();
/* 125:    */   }
/* 126:    */   
/* 127:    */   public <T> T getSetting(String ID, String key, T defaultValue, Environment env)
/* 128:    */   {
/* 129:202 */     SettingKey tempKey = new SettingKey(key, "", "");
/* 130:203 */     return getSetting(ID, tempKey, defaultValue, env);
/* 131:    */   }
/* 132:    */   
/* 133:    */   public <T> T getSetting(String ID, SettingKey key, T defaultValue)
/* 134:    */   {
/* 135:217 */     return getSetting(ID, key, defaultValue, Environment.getSystemWide());
/* 136:    */   }
/* 137:    */   
/* 138:    */   public <T> T getSetting(String ID, SettingKey key, T defaultValue, Environment env)
/* 139:    */   {
/* 140:236 */     Map<SettingKey, Object> settingsForID = (Map)this.m_settings.get(ID);
/* 141:237 */     T value = null;
/* 142:238 */     if ((settingsForID != null) && (settingsForID.size() > 0))
/* 143:    */     {
/* 144:239 */       value = settingsForID.get(key);
/* 145:240 */       if ((value instanceof String)) {
/* 146:    */         try
/* 147:    */         {
/* 148:242 */           value = env.substitute((String)value);
/* 149:    */         }
/* 150:    */         catch (Exception ex) {}
/* 151:    */       }
/* 152:    */     }
/* 153:249 */     if (value == null) {
/* 154:251 */       if (env != null)
/* 155:    */       {
/* 156:252 */         String val = env.getVariableValue(key.getKey());
/* 157:253 */         if (val != null) {
/* 158:254 */           value = stringToT(val, defaultValue);
/* 159:    */         }
/* 160:    */       }
/* 161:    */     }
/* 162:260 */     if (value == null)
/* 163:    */     {
/* 164:261 */       String val = System.getProperty(key.getKey());
/* 165:262 */       if (val != null) {
/* 166:263 */         value = stringToT(val, defaultValue);
/* 167:    */       }
/* 168:    */     }
/* 169:267 */     return value != null ? value : defaultValue;
/* 170:    */   }
/* 171:    */   
/* 172:    */   public void setSetting(String ID, SettingKey propName, Object value)
/* 173:    */   {
/* 174:279 */     Map<SettingKey, Object> settingsForID = (Map)this.m_settings.get(ID);
/* 175:280 */     if (settingsForID == null)
/* 176:    */     {
/* 177:281 */       settingsForID = new LinkedHashMap();
/* 178:282 */       this.m_settings.put(ID, settingsForID);
/* 179:    */     }
/* 180:284 */     settingsForID.put(propName, value);
/* 181:    */   }
/* 182:    */   
/* 183:    */   public boolean hasSettings(String settingsID)
/* 184:    */   {
/* 185:294 */     return this.m_settings.containsKey(settingsID);
/* 186:    */   }
/* 187:    */   
/* 188:    */   public boolean hasSetting(String settingsID, String propName)
/* 189:    */   {
/* 190:305 */     if (!hasSettings(settingsID)) {
/* 191:306 */       return false;
/* 192:    */     }
/* 193:309 */     return ((Map)this.m_settings.get(settingsID)).containsKey(propName);
/* 194:    */   }
/* 195:    */   
/* 196:    */   public void saveSettings()
/* 197:    */     throws IOException
/* 198:    */   {
/* 199:320 */     Map<String, Map<SettingKey, Object>> settingsCopy = new LinkedHashMap();
/* 200:322 */     for (Map.Entry<String, Map<SettingKey, Object>> e : this.m_settings.entrySet())
/* 201:    */     {
/* 202:323 */       s = new LinkedHashMap();
/* 203:324 */       settingsCopy.put(e.getKey(), s);
/* 204:326 */       for (Map.Entry<SettingKey, Object> ee : ((Map)e.getValue()).entrySet()) {
/* 205:327 */         if ((ee.getValue() instanceof Enum))
/* 206:    */         {
/* 207:328 */           EnumHelper wrapper = new EnumHelper((Enum)ee.getValue());
/* 208:329 */           s.put(ee.getKey(), wrapper);
/* 209:    */         }
/* 210:330 */         else if ((ee.getValue() instanceof Font))
/* 211:    */         {
/* 212:331 */           FontHelper wrapper = new FontHelper((Font)ee.getValue());
/* 213:332 */           s.put(ee.getKey(), wrapper);
/* 214:    */         }
/* 215:333 */         else if ((ee.getValue() instanceof File))
/* 216:    */         {
/* 217:334 */           FileHelper wrapper = new FileHelper((File)ee.getValue());
/* 218:335 */           s.put(ee.getKey(), wrapper);
/* 219:    */         }
/* 220:    */         else
/* 221:    */         {
/* 222:337 */           s.put(ee.getKey(), ee.getValue());
/* 223:    */         }
/* 224:    */       }
/* 225:    */     }
/* 226:    */     Map<SettingKey, Object> s;
/* 227:342 */     XMLFileBasedMetaStore store = new XMLFileBasedMetaStore();
/* 228:344 */     if (this.m_settings.size() > 0) {
/* 229:345 */       store.storeEntry(this.m_storeName, this.m_ID, settingsCopy);
/* 230:    */     }
/* 231:    */   }
/* 232:    */   
/* 233:    */   protected static <T> T stringToT(String propVal, T defaultVal)
/* 234:    */   {
/* 235:358 */     if ((defaultVal instanceof String)) {
/* 236:359 */       return propVal;
/* 237:    */     }
/* 238:362 */     if ((defaultVal instanceof Boolean)) {
/* 239:363 */       return Boolean.valueOf(propVal);
/* 240:    */     }
/* 241:366 */     if ((defaultVal instanceof Double)) {
/* 242:367 */       return Double.valueOf(propVal);
/* 243:    */     }
/* 244:370 */     if ((defaultVal instanceof Integer)) {
/* 245:371 */       return Integer.valueOf(propVal);
/* 246:    */     }
/* 247:374 */     if ((defaultVal instanceof Long)) {
/* 248:375 */       return Long.valueOf(propVal);
/* 249:    */     }
/* 250:378 */     if ((defaultVal instanceof LoggingLevel)) {
/* 251:379 */       return LoggingLevel.stringToLevel(propVal);
/* 252:    */     }
/* 253:382 */     return null;
/* 254:    */   }
/* 255:    */   
/* 256:    */   public static class SettingKey
/* 257:    */     implements Serializable
/* 258:    */   {
/* 259:    */     protected String m_key;
/* 260:    */     protected String m_description;
/* 261:    */     protected String m_toolTip;
/* 262:    */     protected List<String> m_pickList;
/* 263:    */     protected Map<String, String> m_meta;
/* 264:    */     
/* 265:    */     public SettingKey()
/* 266:    */     {
/* 267:413 */       this("", "", "");
/* 268:    */     }
/* 269:    */     
/* 270:    */     public SettingKey(String key, String description, String toolTip)
/* 271:    */     {
/* 272:425 */       this(key, description, toolTip, null);
/* 273:    */     }
/* 274:    */     
/* 275:    */     public SettingKey(String key, String description, String toolTip, List<String> pickList)
/* 276:    */     {
/* 277:439 */       this.m_key = key;
/* 278:440 */       this.m_description = description;
/* 279:441 */       this.m_toolTip = toolTip;
/* 280:442 */       this.m_pickList = pickList;
/* 281:    */     }
/* 282:    */     
/* 283:    */     public void setKey(String key)
/* 284:    */     {
/* 285:451 */       this.m_key = key;
/* 286:    */     }
/* 287:    */     
/* 288:    */     public String getKey()
/* 289:    */     {
/* 290:460 */       return this.m_key;
/* 291:    */     }
/* 292:    */     
/* 293:    */     public void setDescription(String description)
/* 294:    */     {
/* 295:469 */       this.m_description = description;
/* 296:    */     }
/* 297:    */     
/* 298:    */     public String getDescription()
/* 299:    */     {
/* 300:478 */       return this.m_description;
/* 301:    */     }
/* 302:    */     
/* 303:    */     public void setToolTip(String toolTip)
/* 304:    */     {
/* 305:487 */       this.m_toolTip = toolTip;
/* 306:    */     }
/* 307:    */     
/* 308:    */     public String getToolTip()
/* 309:    */     {
/* 310:496 */       return this.m_toolTip;
/* 311:    */     }
/* 312:    */     
/* 313:    */     public void setMetadataElement(String key, String value)
/* 314:    */     {
/* 315:506 */       if (this.m_meta == null) {
/* 316:507 */         this.m_meta = new HashMap();
/* 317:    */       }
/* 318:510 */       this.m_meta.put(key, value);
/* 319:    */     }
/* 320:    */     
/* 321:    */     public String getMetadataElement(String key)
/* 322:    */     {
/* 323:520 */       if (this.m_meta == null) {
/* 324:521 */         return null;
/* 325:    */       }
/* 326:524 */       return (String)this.m_meta.get(key);
/* 327:    */     }
/* 328:    */     
/* 329:    */     public String getMetadataElement(String key, String defaultValue)
/* 330:    */     {
/* 331:536 */       String result = getMetadataElement(key);
/* 332:537 */       return result == null ? defaultValue : result;
/* 333:    */     }
/* 334:    */     
/* 335:    */     public void setMetadata(Map<String, String> metadata)
/* 336:    */     {
/* 337:546 */       this.m_meta = metadata;
/* 338:    */     }
/* 339:    */     
/* 340:    */     public Map<String, String> getMetadata()
/* 341:    */     {
/* 342:555 */       return this.m_meta;
/* 343:    */     }
/* 344:    */     
/* 345:    */     public List<String> getPickList()
/* 346:    */     {
/* 347:565 */       return this.m_pickList;
/* 348:    */     }
/* 349:    */     
/* 350:    */     public void setPickList(List<String> pickList)
/* 351:    */     {
/* 352:575 */       this.m_pickList = pickList;
/* 353:    */     }
/* 354:    */     
/* 355:    */     public int hashCode()
/* 356:    */     {
/* 357:585 */       return this.m_key.hashCode();
/* 358:    */     }
/* 359:    */     
/* 360:    */     public boolean equals(Object other)
/* 361:    */     {
/* 362:596 */       if (!(other instanceof SettingKey)) {
/* 363:597 */         return false;
/* 364:    */       }
/* 365:599 */       return this.m_key.equals(((SettingKey)other).getKey());
/* 366:    */     }
/* 367:    */     
/* 368:    */     public String toString()
/* 369:    */     {
/* 370:609 */       return this.m_description;
/* 371:    */     }
/* 372:    */   }
/* 373:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.Settings
 * JD-Core Version:    0.7.0.1
 */