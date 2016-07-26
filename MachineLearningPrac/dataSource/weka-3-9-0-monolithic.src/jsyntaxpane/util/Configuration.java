/*   1:    */ package jsyntaxpane.util;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.util.Collection;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.HashMap;
/*   7:    */ import java.util.HashSet;
/*   8:    */ import java.util.Map;
/*   9:    */ import java.util.Map.Entry;
/*  10:    */ import java.util.Set;
/*  11:    */ import java.util.logging.Level;
/*  12:    */ import java.util.logging.Logger;
/*  13:    */ import java.util.regex.Matcher;
/*  14:    */ import java.util.regex.Pattern;
/*  15:    */ 
/*  16:    */ public class Configuration
/*  17:    */   implements Map<String, String>
/*  18:    */ {
/*  19:    */   Configuration parent;
/*  20:    */   Class clazz;
/*  21:    */   Map<String, String> props;
/*  22:    */   
/*  23:    */   public Configuration(Class theClass, Configuration parent)
/*  24:    */   {
/*  25: 61 */     this(theClass);
/*  26: 62 */     this.parent = parent;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public Configuration(Class theClass)
/*  30:    */   {
/*  31: 71 */     this.clazz = theClass;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public String getString(String key)
/*  35:    */   {
/*  36: 88 */     String value = null;
/*  37: 89 */     if (this.props != null) {
/*  38: 90 */       value = (String)this.props.get(key);
/*  39:    */     }
/*  40: 92 */     for (Configuration p = this.parent; (value == null) && (p != null); p = p.parent) {
/*  41: 93 */       value = p.get(key);
/*  42:    */     }
/*  43: 96 */     if (value != null)
/*  44:    */     {
/*  45: 97 */       Matcher m = PARENT_KEY.matcher(value);
/*  46: 98 */       StringBuffer sb = new StringBuffer();
/*  47: 99 */       while (m.find())
/*  48:    */       {
/*  49:100 */         String p_key = m.group(1);
/*  50:101 */         String p_value = getString(p_key);
/*  51:102 */         if (p_key.equals("class_path"))
/*  52:    */         {
/*  53:103 */           p_value = this.clazz.getName().replace(".", "/").toLowerCase();
/*  54:    */         }
/*  55:104 */         else if (p_key.equals("class_simpleName"))
/*  56:    */         {
/*  57:105 */           p_value = this.clazz.getSimpleName();
/*  58:    */         }
/*  59:    */         else
/*  60:    */         {
/*  61:107 */           p_value = getString(p_key);
/*  62:108 */           if (p_value == null) {
/*  63:109 */             Logger.getLogger(getClass().getName()).warning("no value for ${" + p_key + "} is defined");
/*  64:    */           }
/*  65:    */         }
/*  66:114 */         m.appendReplacement(sb, p_value);
/*  67:    */       }
/*  68:116 */       m.appendTail(sb);
/*  69:117 */       value = sb.toString();
/*  70:    */     }
/*  71:119 */     return value;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public String getString(String key, String defaultValue)
/*  75:    */   {
/*  76:131 */     if (defaultValue == null) {
/*  77:132 */       throw new NullPointerException("defaultValue cannot be null");
/*  78:    */     }
/*  79:134 */     String value = getString(key);
/*  80:135 */     return value == null ? defaultValue : value;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public int getInteger(String key, int Default)
/*  84:    */   {
/*  85:147 */     String v = getString(key);
/*  86:148 */     if (v == null) {
/*  87:149 */       return Default;
/*  88:    */     }
/*  89:    */     try
/*  90:    */     {
/*  91:152 */       return Integer.decode(v).intValue();
/*  92:    */     }
/*  93:    */     catch (NumberFormatException e)
/*  94:    */     {
/*  95:155 */       LOG.log(Level.WARNING, null, e);
/*  96:    */     }
/*  97:156 */     return Default;
/*  98:    */   }
/*  99:    */   
/* 100:    */   public String[] getPropertyList(String key)
/* 101:    */   {
/* 102:170 */     String v = getString(key);
/* 103:171 */     if (v == null) {
/* 104:172 */       return EMPTY_LIST;
/* 105:    */     }
/* 106:174 */     return COMMA_SEPARATOR.split(v);
/* 107:    */   }
/* 108:    */   
/* 109:    */   public boolean getBoolean(String key, boolean Default)
/* 110:    */   {
/* 111:185 */     String b = getString(key);
/* 112:186 */     if (b == null) {
/* 113:187 */       return Default;
/* 114:    */     }
/* 115:189 */     return Boolean.parseBoolean(b.trim());
/* 116:    */   }
/* 117:    */   
/* 118:    */   public Color getColor(String key, Color Default)
/* 119:    */   {
/* 120:199 */     String c = getString(key);
/* 121:200 */     if (c == null) {
/* 122:201 */       return Default;
/* 123:    */     }
/* 124:    */     try
/* 125:    */     {
/* 126:204 */       return Color.decode(c);
/* 127:    */     }
/* 128:    */     catch (NumberFormatException e) {}
/* 129:206 */     return Default;
/* 130:    */   }
/* 131:    */   
/* 132:    */   public void putAll(Map config)
/* 133:    */   {
/* 134:214 */     if (this.props == null) {
/* 135:215 */       this.props = new HashMap();
/* 136:    */     }
/* 137:217 */     this.props.putAll(config);
/* 138:    */   }
/* 139:    */   
/* 140:    */   public Set<String> stringPropertyNames()
/* 141:    */   {
/* 142:228 */     Set<String> propNames = new HashSet();
/* 143:229 */     if (this.parent != null) {
/* 144:230 */       propNames.addAll(this.parent.stringPropertyNames());
/* 145:    */     }
/* 146:232 */     if (this.props != null) {
/* 147:233 */       for (Object k : this.props.keySet()) {
/* 148:234 */         propNames.add(k.toString());
/* 149:    */       }
/* 150:    */     }
/* 151:237 */     return propNames;
/* 152:    */   }
/* 153:    */   
/* 154:    */   public String put(String key, String value)
/* 155:    */   {
/* 156:242 */     if (this.props == null) {
/* 157:243 */       this.props = new HashMap();
/* 158:    */     }
/* 159:245 */     Object old = this.props.put(key, value);
/* 160:246 */     return old == null ? null : old.toString();
/* 161:    */   }
/* 162:    */   
/* 163:    */   public int size()
/* 164:    */   {
/* 165:251 */     return this.props == null ? 0 : this.props.size();
/* 166:    */   }
/* 167:    */   
/* 168:    */   public boolean isEmpty()
/* 169:    */   {
/* 170:256 */     return this.props == null ? true : this.props.isEmpty();
/* 171:    */   }
/* 172:    */   
/* 173:    */   public boolean containsKey(Object key)
/* 174:    */   {
/* 175:261 */     return this.props == null ? false : this.props.containsKey(key);
/* 176:    */   }
/* 177:    */   
/* 178:    */   public boolean containsValue(Object value)
/* 179:    */   {
/* 180:266 */     return this.props == null ? false : this.props.containsValue(value);
/* 181:    */   }
/* 182:    */   
/* 183:    */   public String get(Object key)
/* 184:    */   {
/* 185:271 */     return this.props == null ? null : (String)this.props.get(key);
/* 186:    */   }
/* 187:    */   
/* 188:    */   public String remove(Object key)
/* 189:    */   {
/* 190:276 */     if (this.props == null) {
/* 191:277 */       return null;
/* 192:    */     }
/* 193:279 */     Object old = this.props.remove(key);
/* 194:280 */     return old == null ? null : old.toString();
/* 195:    */   }
/* 196:    */   
/* 197:    */   public void clear()
/* 198:    */   {
/* 199:285 */     if (this.props != null) {
/* 200:286 */       this.props.clear();
/* 201:    */     }
/* 202:    */   }
/* 203:    */   
/* 204:    */   public Set<String> keySet()
/* 205:    */   {
/* 206:293 */     if (this.props == null) {
/* 207:294 */       return Collections.EMPTY_SET;
/* 208:    */     }
/* 209:296 */     return this.props.keySet();
/* 210:    */   }
/* 211:    */   
/* 212:    */   public Collection<String> values()
/* 213:    */   {
/* 214:303 */     if (this.props == null) {
/* 215:304 */       return Collections.EMPTY_SET;
/* 216:    */     }
/* 217:306 */     return this.props.values();
/* 218:    */   }
/* 219:    */   
/* 220:    */   public Set<Map.Entry<String, String>> entrySet()
/* 221:    */   {
/* 222:313 */     if (this.props == null) {
/* 223:314 */       return Collections.EMPTY_SET;
/* 224:    */     }
/* 225:316 */     return this.props.entrySet();
/* 226:    */   }
/* 227:    */   
/* 228:    */   public String toString()
/* 229:    */   {
/* 230:322 */     return "Configuration " + this.clazz + " for " + this.parent;
/* 231:    */   }
/* 232:    */   
/* 233:    */   public static class StringKeyMatcher
/* 234:    */   {
/* 235:    */     public final String key;
/* 236:    */     public final Matcher matcher;
/* 237:    */     public final String group1;
/* 238:    */     public final String value;
/* 239:    */     
/* 240:    */     private StringKeyMatcher(String key, Matcher matcher, String group1, String value)
/* 241:    */     {
/* 242:331 */       this.key = key;
/* 243:332 */       this.matcher = matcher;
/* 244:333 */       this.group1 = group1;
/* 245:334 */       this.value = value;
/* 246:    */     }
/* 247:    */   }
/* 248:    */   
/* 249:    */   public Set<StringKeyMatcher> getKeys(Pattern pattern)
/* 250:    */   {
/* 251:362 */     Set<StringKeyMatcher> matched = new HashSet();
/* 252:363 */     Set<String> all = stringPropertyNames();
/* 253:364 */     for (String k : all)
/* 254:    */     {
/* 255:365 */       Matcher m = pattern.matcher(k);
/* 256:366 */       if (m.matches())
/* 257:    */       {
/* 258:367 */         StringKeyMatcher skm = new StringKeyMatcher(k, m, m.groupCount() >= 1 ? m.group(1) : null, getString(k), null);
/* 259:    */         
/* 260:    */ 
/* 261:370 */         matched.add(skm);
/* 262:    */       }
/* 263:    */     }
/* 264:373 */     return matched;
/* 265:    */   }
/* 266:    */   
/* 267:375 */   public static final String[] EMPTY_LIST = new String[0];
/* 268:376 */   public static final Pattern COMMA_SEPARATOR = Pattern.compile("\\s*,\\s*");
/* 269:377 */   private static Pattern PARENT_KEY = Pattern.compile("\\$\\{(\\w+)\\}");
/* 270:378 */   private static final Logger LOG = Logger.getLogger(Configuration.class.getName());
/* 271:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.util.Configuration
 * JD-Core Version:    0.7.0.1
 */