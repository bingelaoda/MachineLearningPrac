/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.BufferedInputStream;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.FileInputStream;
/*   6:    */ import java.io.IOException;
/*   7:    */ import java.io.InputStream;
/*   8:    */ import java.io.PrintStream;
/*   9:    */ import java.util.Enumeration;
/*  10:    */ import java.util.HashMap;
/*  11:    */ import java.util.HashSet;
/*  12:    */ import java.util.LinkedHashMap;
/*  13:    */ import java.util.LinkedHashSet;
/*  14:    */ import java.util.List;
/*  15:    */ import java.util.Map;
/*  16:    */ import java.util.Properties;
/*  17:    */ import java.util.Set;
/*  18:    */ import java.util.TreeMap;
/*  19:    */ 
/*  20:    */ public class PluginManager
/*  21:    */ {
/*  22: 55 */   protected static Map<String, Map<String, String>> PLUGINS = new HashMap();
/*  23: 64 */   protected static Set<String> DISABLED = new HashSet();
/*  24: 72 */   protected static Map<String, Map<String, String>> RESOURCES = new HashMap();
/*  25:    */   
/*  26:    */   public static synchronized void addToDisabledList(List<String> classnames)
/*  27:    */   {
/*  28: 81 */     for (String s : classnames) {
/*  29: 82 */       addToDisabledList(s);
/*  30:    */     }
/*  31:    */   }
/*  32:    */   
/*  33:    */   public static synchronized void addToDisabledList(String classname)
/*  34:    */   {
/*  35: 92 */     DISABLED.add(classname);
/*  36:    */   }
/*  37:    */   
/*  38:    */   public static synchronized void removeFromDisabledList(List<String> classnames)
/*  39:    */   {
/*  40:103 */     for (String s : classnames) {
/*  41:104 */       removeFromDisabledList(s);
/*  42:    */     }
/*  43:    */   }
/*  44:    */   
/*  45:    */   public static synchronized void removeFromDisabledList(String classname)
/*  46:    */   {
/*  47:115 */     DISABLED.remove(classname);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public static boolean isInDisabledList(String classname)
/*  51:    */   {
/*  52:126 */     return DISABLED.contains(classname);
/*  53:    */   }
/*  54:    */   
/*  55:    */   public static synchronized void addFromProperties(File propsFile)
/*  56:    */     throws Exception
/*  57:    */   {
/*  58:137 */     addFromProperties(propsFile, false);
/*  59:    */   }
/*  60:    */   
/*  61:    */   public static synchronized void addFromProperties(File propsFile, boolean maintainInsertionOrder)
/*  62:    */     throws Exception
/*  63:    */   {
/*  64:150 */     BufferedInputStream bi = new BufferedInputStream(new FileInputStream(propsFile));
/*  65:    */     
/*  66:152 */     addFromProperties(bi);
/*  67:    */   }
/*  68:    */   
/*  69:    */   public static synchronized void addFromProperties(InputStream propsStream)
/*  70:    */     throws Exception
/*  71:    */   {
/*  72:163 */     addFromProperties(propsStream, false);
/*  73:    */   }
/*  74:    */   
/*  75:    */   public static synchronized void addFromProperties(InputStream propsStream, boolean maintainInsertionOrder)
/*  76:    */     throws Exception
/*  77:    */   {
/*  78:176 */     Properties expProps = new Properties();
/*  79:    */     
/*  80:178 */     expProps.load(propsStream);
/*  81:179 */     propsStream.close();
/*  82:180 */     propsStream = null;
/*  83:    */     
/*  84:182 */     addFromProperties(expProps, maintainInsertionOrder);
/*  85:    */   }
/*  86:    */   
/*  87:    */   public static synchronized void addFromProperties(Properties props)
/*  88:    */     throws Exception
/*  89:    */   {
/*  90:193 */     addFromProperties(props, false);
/*  91:    */   }
/*  92:    */   
/*  93:    */   public static synchronized void addFromProperties(Properties props, boolean maintainInsertionOrder)
/*  94:    */     throws Exception
/*  95:    */   {
/*  96:206 */     Enumeration<?> keys = props.propertyNames();
/*  97:208 */     while (keys.hasMoreElements())
/*  98:    */     {
/*  99:209 */       String baseType = (String)keys.nextElement();
/* 100:210 */       String implementations = props.getProperty(baseType);
/* 101:211 */       if (baseType.equalsIgnoreCase("*resources*"))
/* 102:    */       {
/* 103:212 */         addPluginResourcesFromProperty(implementations);
/* 104:    */       }
/* 105:214 */       else if ((implementations != null) && (implementations.length() > 0))
/* 106:    */       {
/* 107:215 */         String[] parts = implementations.split(",");
/* 108:216 */         for (String impl : parts)
/* 109:    */         {
/* 110:217 */           impl = impl.trim();
/* 111:218 */           String name = impl;
/* 112:219 */           if (impl.charAt(0) == '[')
/* 113:    */           {
/* 114:220 */             name = impl.substring(1, impl.indexOf(']'));
/* 115:221 */             impl = impl.substring(impl.indexOf(']') + 1);
/* 116:    */           }
/* 117:223 */           addPlugin(baseType, name.trim(), impl, maintainInsertionOrder);
/* 118:    */         }
/* 119:    */       }
/* 120:    */     }
/* 121:    */   }
/* 122:    */   
/* 123:    */   protected static synchronized void addPluginResourcesFromProperty(String resourceList)
/* 124:    */   {
/* 125:246 */     String[] resources = resourceList.split(",");
/* 126:247 */     for (String r : resources)
/* 127:    */     {
/* 128:248 */       r = r.trim();
/* 129:249 */       if ((!r.startsWith("[")) || (!r.endsWith("]")))
/* 130:    */       {
/* 131:250 */         System.err.println("[PluginManager] Malformed resource in: " + resourceList);
/* 132:    */       }
/* 133:    */       else
/* 134:    */       {
/* 135:255 */         r = r.replace("[", "").replace("]", "");
/* 136:256 */         String[] rParts = r.split("\\|");
/* 137:257 */         if (rParts.length != 3)
/* 138:    */         {
/* 139:258 */           System.err.println("[PluginManager] Was expecting 3 pipe separated parts in resource spec: " + r);
/* 140:    */         }
/* 141:    */         else
/* 142:    */         {
/* 143:264 */           String groupID = rParts[0].trim();
/* 144:265 */           String resourceDesc = rParts[1].trim();
/* 145:266 */           String resourcePath = rParts[2].trim();
/* 146:267 */           if ((groupID.length() == 0) || (resourceDesc.length() == 0) || (resourcePath.length() == 0)) {
/* 147:269 */             System.err.println("[PluginManager] Empty part in resource spec: " + r);
/* 148:    */           } else {
/* 149:272 */             addPluginResource(groupID, resourceDesc, resourcePath);
/* 150:    */           }
/* 151:    */         }
/* 152:    */       }
/* 153:    */     }
/* 154:    */   }
/* 155:    */   
/* 156:    */   public static synchronized void addPluginResource(String resourceGroupID, String resourceDescription, String resourcePath)
/* 157:    */   {
/* 158:286 */     Map<String, String> groupMap = (Map)RESOURCES.get(resourceGroupID);
/* 159:287 */     if (groupMap == null)
/* 160:    */     {
/* 161:288 */       groupMap = new LinkedHashMap();
/* 162:289 */       RESOURCES.put(resourceGroupID, groupMap);
/* 163:    */     }
/* 164:292 */     groupMap.put(resourceDescription, resourcePath);
/* 165:    */   }
/* 166:    */   
/* 167:    */   public static InputStream getPluginResourceAsStream(String resourceGroupID, String resourceDescription)
/* 168:    */     throws IOException
/* 169:    */   {
/* 170:307 */     Map<String, String> groupMap = (Map)RESOURCES.get(resourceGroupID);
/* 171:308 */     if (groupMap == null) {
/* 172:309 */       throw new IOException("Unknown resource group ID: " + resourceGroupID);
/* 173:    */     }
/* 174:312 */     String resourcePath = (String)groupMap.get(resourceDescription);
/* 175:313 */     if (resourcePath == null) {
/* 176:314 */       throw new IOException("Unknown resource: " + resourceDescription);
/* 177:    */     }
/* 178:317 */     return PluginManager.class.getClassLoader().getResourceAsStream(resourcePath);
/* 179:    */   }
/* 180:    */   
/* 181:    */   public static int numResourcesForWithGroupID(String resourceGroupID)
/* 182:    */   {
/* 183:328 */     Map<String, String> groupMap = (Map)RESOURCES.get(resourceGroupID);
/* 184:329 */     return groupMap == null ? 0 : groupMap.size();
/* 185:    */   }
/* 186:    */   
/* 187:    */   public static Map<String, String> getResourcesWithGroupID(String resourceGroupID)
/* 188:    */   {
/* 189:342 */     return (Map)RESOURCES.get(resourceGroupID);
/* 190:    */   }
/* 191:    */   
/* 192:    */   public static Set<String> getPluginNamesOfType(String interfaceName)
/* 193:    */   {
/* 194:354 */     if (PLUGINS.get(interfaceName) != null)
/* 195:    */     {
/* 196:355 */       Set<String> match = ((Map)PLUGINS.get(interfaceName)).keySet();
/* 197:356 */       Set<String> result = new LinkedHashSet();
/* 198:357 */       for (String s : match)
/* 199:    */       {
/* 200:358 */         String impl = (String)((Map)PLUGINS.get(interfaceName)).get(s);
/* 201:359 */         if (!DISABLED.contains(impl)) {
/* 202:360 */           result.add(s);
/* 203:    */         }
/* 204:    */       }
/* 205:364 */       return result;
/* 206:    */     }
/* 207:367 */     return null;
/* 208:    */   }
/* 209:    */   
/* 210:    */   public static void addPlugin(String interfaceName, String name, String concreteType)
/* 211:    */   {
/* 212:382 */     addPlugin(interfaceName, name, concreteType, false);
/* 213:    */   }
/* 214:    */   
/* 215:    */   public static void addPlugin(String interfaceName, String name, String concreteType, boolean maintainInsertionOrder)
/* 216:    */   {
/* 217:400 */     if (PLUGINS.get(interfaceName) == null)
/* 218:    */     {
/* 219:401 */       Map<String, String> pluginsOfInterfaceType = maintainInsertionOrder ? new LinkedHashMap() : new TreeMap();
/* 220:    */       
/* 221:    */ 
/* 222:404 */       pluginsOfInterfaceType.put(name, concreteType);
/* 223:405 */       PLUGINS.put(interfaceName, pluginsOfInterfaceType);
/* 224:    */     }
/* 225:    */     else
/* 226:    */     {
/* 227:407 */       ((Map)PLUGINS.get(interfaceName)).put(name, concreteType);
/* 228:    */     }
/* 229:    */   }
/* 230:    */   
/* 231:    */   public static void removePlugins(String interfaceName, List<String> names)
/* 232:    */   {
/* 233:419 */     for (String name : names) {
/* 234:420 */       removePlugin(interfaceName, name);
/* 235:    */     }
/* 236:    */   }
/* 237:    */   
/* 238:    */   public static void removePlugin(String interfaceName, String name)
/* 239:    */   {
/* 240:433 */     if (PLUGINS.get(interfaceName) != null) {
/* 241:434 */       ((Map)PLUGINS.get(interfaceName)).remove(name);
/* 242:    */     }
/* 243:    */   }
/* 244:    */   
/* 245:    */   public static boolean pluginRegistered(String interfaceType, String name)
/* 246:    */   {
/* 247:446 */     Map<String, String> pluginsOfInterfaceType = (Map)PLUGINS.get(interfaceType);
/* 248:447 */     return pluginsOfInterfaceType.get(name) != null;
/* 249:    */   }
/* 250:    */   
/* 251:    */   public static Object getPluginInstance(String interfaceType, String name)
/* 252:    */     throws Exception
/* 253:    */   {
/* 254:460 */     if ((PLUGINS.get(interfaceType) == null) || (((Map)PLUGINS.get(interfaceType)).size() == 0)) {
/* 255:462 */       throw new Exception("No plugins of interface type: " + interfaceType + " available!!");
/* 256:    */     }
/* 257:466 */     Map<String, String> pluginsOfInterfaceType = (Map)PLUGINS.get(interfaceType);
/* 258:467 */     if (pluginsOfInterfaceType.get(name) == null) {
/* 259:468 */       throw new Exception("Can't find named plugin '" + name + "' of type '" + interfaceType + "'!");
/* 260:    */     }
/* 261:472 */     String concreteImpl = (String)pluginsOfInterfaceType.get(name);
/* 262:473 */     Object plugin = null;
/* 263:474 */     if (!DISABLED.contains(concreteImpl)) {
/* 264:475 */       plugin = Class.forName(concreteImpl).newInstance();
/* 265:    */     }
/* 266:478 */     return plugin;
/* 267:    */   }
/* 268:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.PluginManager
 * JD-Core Version:    0.7.0.1
 */