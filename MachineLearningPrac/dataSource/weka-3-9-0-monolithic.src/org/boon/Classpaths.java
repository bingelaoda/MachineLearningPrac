/*   1:    */ package org.boon;
/*   2:    */ 
/*   3:    */ import java.net.URI;
/*   4:    */ import java.net.URL;
/*   5:    */ import java.nio.file.FileSystem;
/*   6:    */ import java.nio.file.Path;
/*   7:    */ import java.util.Enumeration;
/*   8:    */ import java.util.HashMap;
/*   9:    */ import java.util.List;
/*  10:    */ import java.util.Map;
/*  11:    */ 
/*  12:    */ public class Classpaths
/*  13:    */ {
/*  14:    */   public static List<URL> classpathResources(ClassLoader loader, String resource)
/*  15:    */   {
/*  16:    */     try
/*  17:    */     {
/*  18: 49 */       Enumeration<URL> resources = loader.getResources(resource);
/*  19: 50 */       List<URL> list = Lists.list(resources);
/*  20: 52 */       if ((Lists.isEmpty(list)) && (resource.startsWith("/")))
/*  21:    */       {
/*  22: 53 */         resource = resource.substring(1);
/*  23: 54 */         return classpathResources(loader, resource);
/*  24:    */       }
/*  25: 57 */       return list;
/*  26:    */     }
/*  27:    */     catch (Exception ex)
/*  28:    */     {
/*  29: 62 */       return (List)Exceptions.handle(List.class, Boon.sputs(new Object[] { "Unable to load listFromClassLoader for", resource }), ex);
/*  30:    */     }
/*  31:    */   }
/*  32:    */   
/*  33:    */   public static List<URL> classpathResources(Class<?> clazz, String resource)
/*  34:    */   {
/*  35: 72 */     List<URL> list = classpathResources(Thread.currentThread().getContextClassLoader(), resource);
/*  36: 74 */     if (Lists.isEmpty(list)) {
/*  37: 75 */       list = classpathResources(clazz.getClassLoader(), resource);
/*  38:    */     }
/*  39: 79 */     if ((Lists.isEmpty(list)) && (resource.startsWith("/")))
/*  40:    */     {
/*  41: 80 */       resource = resource.substring(1);
/*  42: 81 */       return classpathResources(clazz, resource);
/*  43:    */     }
/*  44: 84 */     return list;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public static List<String> resources(Class<?> clazz, String resource)
/*  48:    */   {
/*  49: 90 */     List<String> list = listFromClassLoader(Thread.currentThread().getContextClassLoader(), resource);
/*  50: 92 */     if (Lists.isEmpty(list)) {
/*  51: 93 */       list = listFromClassLoader(clazz.getClassLoader(), resource);
/*  52:    */     }
/*  53: 97 */     if ((Lists.isEmpty(list)) && (resource.startsWith("/")))
/*  54:    */     {
/*  55: 98 */       resource = resource.substring(1);
/*  56: 99 */       return resources(clazz, resource);
/*  57:    */     }
/*  58:102 */     return list;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public static List<Path> paths(Class<?> clazz, String resource)
/*  62:    */   {
/*  63:109 */     List<Path> list = pathsFromClassLoader(Thread.currentThread().getContextClassLoader(), resource);
/*  64:111 */     if (Lists.isEmpty(list)) {
/*  65:112 */       list = pathsFromClassLoader(clazz.getClassLoader(), resource);
/*  66:    */     }
/*  67:116 */     if ((Lists.isEmpty(list)) && (resource.startsWith("/")))
/*  68:    */     {
/*  69:117 */       resource = resource.substring(1);
/*  70:118 */       return paths(clazz, resource);
/*  71:    */     }
/*  72:121 */     return list;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public static List<String> listFromClassLoader(ClassLoader loader, String resource)
/*  76:    */   {
/*  77:131 */     List<URL> resourceURLs = classpathResources(loader, resource);
/*  78:132 */     List<String> resourcePaths = Lists.list(String.class);
/*  79:133 */     Map<URI, FileSystem> pathToZipFileSystems = new HashMap();
/*  80:134 */     for (URL resourceURL : resourceURLs) {
/*  81:136 */       if (resourceURL.getProtocol().equals("jar")) {
/*  82:137 */         resourcesFromJar(resourcePaths, resourceURL, pathToZipFileSystems);
/*  83:    */       } else {
/*  84:140 */         resourcesFromFileSystem(resourcePaths, resourceURL);
/*  85:    */       }
/*  86:    */     }
/*  87:143 */     return resourcePaths;
/*  88:    */   }
/*  89:    */   
/*  90:    */   public static List<Path> pathsFromClassLoader(ClassLoader loader, String resource)
/*  91:    */   {
/*  92:154 */     List<URL> resourceURLs = classpathResources(loader, resource);
/*  93:155 */     List<Path> resourcePaths = Lists.list(Path.class);
/*  94:156 */     Map<URI, FileSystem> pathToZipFileSystems = new HashMap();
/*  95:157 */     for (URL resourceURL : resourceURLs) {
/*  96:159 */       if (resourceURL.getProtocol().equals("jar")) {
/*  97:160 */         pathsFromJar(resourcePaths, resourceURL, pathToZipFileSystems);
/*  98:    */       } else {
/*  99:163 */         pathsFromFileSystem(resourcePaths, resourceURL);
/* 100:    */       }
/* 101:    */     }
/* 102:166 */     return resourcePaths;
/* 103:    */   }
/* 104:    */   
/* 105:    */   private static void resourcesFromFileSystem(List<String> resourcePaths, URL u)
/* 106:    */   {
/* 107:172 */     URI fileURI = IO.createURI(u.toString());
/* 108:    */     
/* 109:    */ 
/* 110:175 */     Lists.add(resourcePaths, IO.uriToPath(fileURI).toString());
/* 111:    */   }
/* 112:    */   
/* 113:    */   private static void pathsFromFileSystem(List<Path> resourcePaths, URL u)
/* 114:    */   {
/* 115:181 */     URI fileURI = IO.createURI(u.toString());
/* 116:    */     
/* 117:    */ 
/* 118:184 */     Lists.add(resourcePaths, IO.uriToPath(fileURI));
/* 119:    */   }
/* 120:    */   
/* 121:    */   private static void resourcesFromJar(List<String> resourcePaths, URL resourceURL, Map<URI, FileSystem> pathToZipFileSystems)
/* 122:    */   {
/* 123:189 */     String str = resourceURL.toString();
/* 124:    */     
/* 125:191 */     String[] strings = StringScanner.split(str, '!');
/* 126:    */     
/* 127:193 */     URI fileJarURI = URI.create(strings[0]);
/* 128:194 */     String resourcePath = strings[1];
/* 129:196 */     if (!pathToZipFileSystems.containsKey(fileJarURI)) {
/* 130:197 */       pathToZipFileSystems.put(fileJarURI, IO.zipFileSystem(fileJarURI));
/* 131:    */     }
/* 132:200 */     FileSystem fileSystem = (FileSystem)pathToZipFileSystems.get(fileJarURI);
/* 133:    */     
/* 134:202 */     Path path = fileSystem.getPath(resourcePath, new String[0]);
/* 135:204 */     if (path != null) {
/* 136:205 */       Lists.add(resourcePaths, str);
/* 137:    */     }
/* 138:    */   }
/* 139:    */   
/* 140:    */   private static void pathsFromJar(List<Path> resourcePaths, URL resourceURL, Map<URI, FileSystem> pathToZipFileSystems)
/* 141:    */   {
/* 142:211 */     String str = resourceURL.toString();
/* 143:    */     
/* 144:213 */     String[] strings = StringScanner.split(str, '!');
/* 145:    */     
/* 146:215 */     URI fileJarURI = URI.create(strings[0]);
/* 147:216 */     String resourcePath = strings[1];
/* 148:218 */     if (!pathToZipFileSystems.containsKey(fileJarURI)) {
/* 149:219 */       pathToZipFileSystems.put(fileJarURI, IO.zipFileSystem(fileJarURI));
/* 150:    */     }
/* 151:222 */     FileSystem fileSystem = (FileSystem)pathToZipFileSystems.get(fileJarURI);
/* 152:    */     
/* 153:224 */     Path path = fileSystem.getPath(resourcePath, new String[0]);
/* 154:226 */     if (path != null) {
/* 155:227 */       Lists.add(resourcePaths, path);
/* 156:    */     }
/* 157:    */   }
/* 158:    */   
/* 159:    */   private static void resourcePathsFromJar(List<Path> resourcePaths, URL resourceURL, Map<URI, FileSystem> pathToZipFileSystems)
/* 160:    */   {
/* 161:234 */     String str = resourceURL.toString();
/* 162:    */     
/* 163:236 */     String[] strings = StringScanner.split(str, '!');
/* 164:    */     
/* 165:238 */     URI fileJarURI = URI.create(strings[0]);
/* 166:239 */     String resourcePath = strings[1];
/* 167:241 */     if (!pathToZipFileSystems.containsKey(fileJarURI)) {
/* 168:242 */       pathToZipFileSystems.put(fileJarURI, IO.zipFileSystem(fileJarURI));
/* 169:    */     }
/* 170:245 */     FileSystem fileSystem = (FileSystem)pathToZipFileSystems.get(fileJarURI);
/* 171:    */     
/* 172:247 */     Path path = fileSystem.getPath(resourcePath, new String[0]);
/* 173:249 */     if (path != null) {
/* 174:250 */       Lists.add(resourcePaths, path);
/* 175:    */     }
/* 176:    */   }
/* 177:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.Classpaths
 * JD-Core Version:    0.7.0.1
 */