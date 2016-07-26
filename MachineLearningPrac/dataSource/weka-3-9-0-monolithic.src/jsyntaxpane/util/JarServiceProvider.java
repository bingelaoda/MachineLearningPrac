/*   1:    */ package jsyntaxpane.util;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.InputStream;
/*   6:    */ import java.io.InputStreamReader;
/*   7:    */ import java.net.URL;
/*   8:    */ import java.nio.charset.Charset;
/*   9:    */ import java.util.ArrayList;
/*  10:    */ import java.util.Collections;
/*  11:    */ import java.util.Enumeration;
/*  12:    */ import java.util.HashMap;
/*  13:    */ import java.util.List;
/*  14:    */ import java.util.Map;
/*  15:    */ import java.util.Map.Entry;
/*  16:    */ import java.util.Properties;
/*  17:    */ import java.util.logging.Level;
/*  18:    */ import java.util.logging.Logger;
/*  19:    */ 
/*  20:    */ public class JarServiceProvider
/*  21:    */ {
/*  22:    */   public static final String SERVICES_ROOT = "META-INF/services/";
/*  23: 40 */   private static final Logger LOG = Logger.getLogger(JarServiceProvider.class.getName());
/*  24:    */   
/*  25:    */   private static ClassLoader getClassLoader()
/*  26:    */   {
/*  27: 50 */     ClassLoader cl = JarServiceProvider.class.getClassLoader();
/*  28: 51 */     return cl == null ? ClassLoader.getSystemClassLoader() : cl;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public static List<Object> getServiceProviders(Class cls)
/*  32:    */     throws IOException
/*  33:    */   {
/*  34: 61 */     ArrayList<Object> l = new ArrayList();
/*  35: 62 */     ClassLoader cl = getClassLoader();
/*  36: 63 */     String serviceFile = "META-INF/services/" + cls.getName();
/*  37: 64 */     Enumeration<URL> e = cl.getResources(serviceFile);
/*  38: 65 */     while (e.hasMoreElements())
/*  39:    */     {
/*  40: 66 */       URL u = (URL)e.nextElement();
/*  41: 67 */       InputStream is = u.openStream();
/*  42: 68 */       BufferedReader br = null;
/*  43:    */       try
/*  44:    */       {
/*  45: 70 */         br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
/*  46:    */         
/*  47: 72 */         String str = null;
/*  48: 73 */         while ((str = br.readLine()) != null)
/*  49:    */         {
/*  50: 74 */           int commentStartIdx = str.indexOf("#");
/*  51: 75 */           if (commentStartIdx != -1) {
/*  52: 76 */             str = str.substring(0, commentStartIdx);
/*  53:    */           }
/*  54: 78 */           str = str.trim();
/*  55: 79 */           if (str.length() != 0) {
/*  56:    */             try
/*  57:    */             {
/*  58: 83 */               Object obj = cl.loadClass(str).newInstance();
/*  59: 84 */               l.add(obj);
/*  60:    */             }
/*  61:    */             catch (Exception ex)
/*  62:    */             {
/*  63: 86 */               LOG.warning("Could not load: " + str);
/*  64: 87 */               LOG.warning(ex.getMessage());
/*  65:    */             }
/*  66:    */           }
/*  67:    */         }
/*  68:    */       }
/*  69:    */       finally
/*  70:    */       {
/*  71: 91 */         if (br != null) {
/*  72: 92 */           br.close();
/*  73:    */         }
/*  74:    */       }
/*  75:    */     }
/*  76: 96 */     return l;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public static Properties readProperties(Class clazz)
/*  80:    */   {
/*  81:107 */     return readProperties(clazz.getName());
/*  82:    */   }
/*  83:    */   
/*  84:    */   public static Properties readProperties(String name)
/*  85:    */   {
/*  86:119 */     Properties props = new Properties();
/*  87:120 */     String serviceFile = name.toLowerCase();
/*  88:121 */     if (!serviceFile.endsWith(".properties")) {
/*  89:122 */       serviceFile = serviceFile + ".properties";
/*  90:    */     }
/*  91:124 */     InputStream is = findResource(serviceFile);
/*  92:125 */     if (is != null) {
/*  93:    */       try
/*  94:    */       {
/*  95:127 */         props.load(is);
/*  96:    */       }
/*  97:    */       catch (IOException ex)
/*  98:    */       {
/*  99:129 */         Logger.getLogger(JarServiceProvider.class.getName()).log(Level.SEVERE, null, ex);
/* 100:    */       }
/* 101:    */     }
/* 102:132 */     return props;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public static Map<String, String> readStringsMap(String name)
/* 106:    */   {
/* 107:143 */     Properties props = readProperties(name);
/* 108:144 */     HashMap<String, String> map = new HashMap();
/* 109:145 */     if (props != null) {
/* 110:146 */       for (Map.Entry e : props.entrySet()) {
/* 111:147 */         map.put(e.getKey().toString(), e.getValue().toString());
/* 112:    */       }
/* 113:    */     }
/* 114:150 */     return map;
/* 115:    */   }
/* 116:    */   
/* 117:    */   public static List<String> readLines(String url)
/* 118:    */   {
/* 119:166 */     InputStream is = findResource(url);
/* 120:167 */     if (is == null) {
/* 121:168 */       return Collections.EMPTY_LIST;
/* 122:    */     }
/* 123:170 */     List<String> lines = new ArrayList();
/* 124:    */     try
/* 125:    */     {
/* 126:172 */       BufferedReader br = new BufferedReader(new InputStreamReader(is));
/* 127:173 */       for (String line = br.readLine(); line != null; line = br.readLine())
/* 128:    */       {
/* 129:175 */         line = line.trim().replace("\\n", "\n").replace("\\t", "\t");
/* 130:176 */         lines.add(line);
/* 131:    */       }
/* 132:186 */       return lines;
/* 133:    */     }
/* 134:    */     catch (IOException ex)
/* 135:    */     {
/* 136:179 */       LOG.log(Level.SEVERE, null, ex);
/* 137:    */       
/* 138:    */ 
/* 139:    */ 
/* 140:    */ 
/* 141:    */ 
/* 142:    */ 
/* 143:186 */       return lines;
/* 144:    */     }
/* 145:    */     finally
/* 146:    */     {
/* 147:    */       try
/* 148:    */       {
/* 149:182 */         is.close();
/* 150:    */       }
/* 151:    */       catch (IOException ex)
/* 152:    */       {
/* 153:184 */         LOG.log(Level.SEVERE, null, ex);
/* 154:    */       }
/* 155:    */     }
/* 156:186 */     return lines;
/* 157:    */   }
/* 158:    */   
/* 159:    */   public static InputStream findResource(String url, ClassLoader cl)
/* 160:    */   {
/* 161:203 */     InputStream is = null;
/* 162:    */     
/* 163:205 */     URL loc = cl.getResource(url);
/* 164:206 */     if (loc == null) {
/* 165:207 */       loc = cl.getResource(url);
/* 166:    */     }
/* 167:209 */     if (loc == null) {
/* 168:210 */       loc = cl.getResource("META-INF/services/" + url);
/* 169:    */     }
/* 170:212 */     if (loc == null) {
/* 171:213 */       is = ClassLoader.getSystemResourceAsStream(url);
/* 172:    */     } else {
/* 173:    */       try
/* 174:    */       {
/* 175:216 */         is = loc.openStream();
/* 176:    */       }
/* 177:    */       catch (IOException ex)
/* 178:    */       {
/* 179:218 */         Logger.getLogger(JarServiceProvider.class.getName()).log(Level.SEVERE, null, ex);
/* 180:    */       }
/* 181:    */     }
/* 182:221 */     return is;
/* 183:    */   }
/* 184:    */   
/* 185:    */   public static InputStream findResource(String url)
/* 186:    */   {
/* 187:236 */     return findResource(url, getClassLoader());
/* 188:    */   }
/* 189:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.util.JarServiceProvider
 * JD-Core Version:    0.7.0.1
 */