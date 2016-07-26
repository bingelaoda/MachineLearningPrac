/*   1:    */ package weka.core.packageManagement;
/*   2:    */ 
/*   3:    */ import java.awt.GraphicsEnvironment;
/*   4:    */ import java.beans.Beans;
/*   5:    */ import java.io.File;
/*   6:    */ import java.io.FileInputStream;
/*   7:    */ import java.io.PrintStream;
/*   8:    */ import java.net.Authenticator;
/*   9:    */ import java.net.InetSocketAddress;
/*  10:    */ import java.net.PasswordAuthentication;
/*  11:    */ import java.net.Proxy;
/*  12:    */ import java.net.Proxy.Type;
/*  13:    */ import java.net.ProxySelector;
/*  14:    */ import java.net.URISyntaxException;
/*  15:    */ import java.net.URL;
/*  16:    */ import java.util.List;
/*  17:    */ import java.util.Map;
/*  18:    */ import java.util.Properties;
/*  19:    */ import org.bounce.net.DefaultAuthenticator;
/*  20:    */ import weka.core.Defaults;
/*  21:    */ import weka.core.Settings;
/*  22:    */ 
/*  23:    */ public abstract class PackageManager
/*  24:    */ {
/*  25:    */   protected File m_packageHome;
/*  26:    */   protected URL m_packageRepository;
/*  27:    */   protected String m_baseSystemName;
/*  28:    */   protected Object m_baseSystemVersion;
/*  29:    */   protected transient Proxy m_httpProxy;
/*  30:    */   protected transient String m_proxyUsername;
/*  31:    */   protected transient String m_proxyPassword;
/*  32:    */   protected transient boolean m_authenticatorSet;
/*  33:    */   
/*  34:    */   public static PackageManager create()
/*  35:    */   {
/*  36: 53 */     PackageManager pm = new DefaultPackageManager();
/*  37: 54 */     pm.establishProxy();
/*  38:    */     try
/*  39:    */     {
/*  40: 58 */       String managerName = System.getProperty("org.pentaho.packageManagement.manager");
/*  41: 60 */       if ((managerName != null) && (managerName.length() > 0))
/*  42:    */       {
/*  43: 61 */         Object manager = Beans.instantiate(pm.getClass().getClassLoader(), managerName);
/*  44: 63 */         if ((manager instanceof PackageManager)) {
/*  45: 64 */           pm = (PackageManager)manager;
/*  46:    */         }
/*  47:    */       }
/*  48:    */       else
/*  49:    */       {
/*  50: 71 */         File packageManagerPropsFile = new File(System.getProperty("user.home") + File.separator + "PackageManager.props");
/*  51: 73 */         if (packageManagerPropsFile.exists())
/*  52:    */         {
/*  53: 74 */           Properties pmProps = new Properties();
/*  54: 75 */           pmProps.load(new FileInputStream(packageManagerPropsFile));
/*  55: 76 */           managerName = pmProps.getProperty("org.pentaho.packageManager.manager");
/*  56: 78 */           if ((managerName != null) && (managerName.length() > 0))
/*  57:    */           {
/*  58: 79 */             Object manager = Beans.instantiate(pm.getClass().getClassLoader(), managerName);
/*  59: 81 */             if ((manager instanceof PackageManager)) {
/*  60: 82 */               pm = (PackageManager)manager;
/*  61:    */             }
/*  62:    */           }
/*  63:    */         }
/*  64:    */       }
/*  65:    */     }
/*  66:    */     catch (Exception ex)
/*  67:    */     {
/*  68: 89 */       System.err.println("Problem instantiating package manager. Using DefaultPackageManager.");
/*  69:    */     }
/*  70: 92 */     return pm;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void establishProxy()
/*  74:    */   {
/*  75:137 */     String proxyHost = System.getProperty("http.proxyHost");
/*  76:138 */     String proxyPort = System.getProperty("http.proxyPort");
/*  77:139 */     if ((proxyHost != null) && (proxyHost.length() > 0))
/*  78:    */     {
/*  79:140 */       int portNum = 80;
/*  80:141 */       if ((proxyPort != null) && (proxyPort.length() > 0)) {
/*  81:142 */         portNum = Integer.parseInt(proxyPort);
/*  82:    */       }
/*  83:144 */       InetSocketAddress sa = new InetSocketAddress(proxyHost, portNum);
/*  84:145 */       setProxy(new Proxy(Proxy.Type.HTTP, sa));
/*  85:    */     }
/*  86:149 */     String proxyUserName = System.getProperty("http.proxyUser");
/*  87:150 */     String proxyPassword = System.getProperty("http.proxyPassword");
/*  88:151 */     if ((proxyUserName != null) && (proxyUserName.length() > 0) && (proxyPassword != null) && (proxyPassword.length() > 0))
/*  89:    */     {
/*  90:154 */       setProxyUsername(proxyUserName);
/*  91:155 */       setProxyPassword(proxyPassword);
/*  92:    */     }
/*  93:    */   }
/*  94:    */   
/*  95:    */   public synchronized boolean setProxyAuthentication(URL urlToConnectTo)
/*  96:    */   {
/*  97:167 */     if (this.m_httpProxy == null)
/*  98:    */     {
/*  99:169 */       ProxySelector ps = ProxySelector.getDefault();
/* 100:    */       try
/* 101:    */       {
/* 102:172 */         List<Proxy> proxyList = ps.select(urlToConnectTo.toURI());
/* 103:173 */         Proxy proxy = (Proxy)proxyList.get(0);
/* 104:174 */         setProxy(proxy);
/* 105:    */       }
/* 106:    */       catch (URISyntaxException e)
/* 107:    */       {
/* 108:176 */         e.printStackTrace();
/* 109:    */       }
/* 110:    */     }
/* 111:180 */     if (this.m_httpProxy != null)
/* 112:    */     {
/* 113:181 */       if ((this.m_proxyUsername != null) && (this.m_proxyPassword != null) && (!this.m_authenticatorSet))
/* 114:    */       {
/* 115:183 */         Authenticator.setDefault(new Authenticator()
/* 116:    */         {
/* 117:    */           protected PasswordAuthentication getPasswordAuthentication()
/* 118:    */           {
/* 119:186 */             return new PasswordAuthentication(PackageManager.this.m_proxyUsername, PackageManager.this.m_proxyPassword.toCharArray());
/* 120:    */           }
/* 121:    */         });
/* 122:    */       }
/* 123:191 */       else if ((!this.m_authenticatorSet) && (!GraphicsEnvironment.isHeadless()))
/* 124:    */       {
/* 125:192 */         Authenticator.setDefault(new DefaultAuthenticator(null));
/* 126:    */         
/* 127:194 */         this.m_authenticatorSet = true;
/* 128:    */       }
/* 129:198 */       return true;
/* 130:    */     }
/* 131:201 */     if (this.m_httpProxy != null) {
/* 132:202 */       return true;
/* 133:    */     }
/* 134:205 */     return false;
/* 135:    */   }
/* 136:    */   
/* 137:    */   public void setPackageHome(File packageHome)
/* 138:    */   {
/* 139:214 */     this.m_packageHome = packageHome;
/* 140:    */   }
/* 141:    */   
/* 142:    */   public File getPackageHome()
/* 143:    */   {
/* 144:223 */     return this.m_packageHome;
/* 145:    */   }
/* 146:    */   
/* 147:    */   public void setBaseSystemName(String baseS)
/* 148:    */   {
/* 149:232 */     this.m_baseSystemName = baseS;
/* 150:    */   }
/* 151:    */   
/* 152:    */   public String getBaseSystemName()
/* 153:    */   {
/* 154:241 */     return this.m_baseSystemName;
/* 155:    */   }
/* 156:    */   
/* 157:    */   public void setBaseSystemVersion(Object systemV)
/* 158:    */   {
/* 159:250 */     this.m_baseSystemVersion = systemV;
/* 160:    */   }
/* 161:    */   
/* 162:    */   public Object getBaseSystemVersion()
/* 163:    */   {
/* 164:260 */     return this.m_baseSystemVersion;
/* 165:    */   }
/* 166:    */   
/* 167:    */   public void setPackageRepositoryURL(URL repositoryURL)
/* 168:    */   {
/* 169:269 */     this.m_packageRepository = repositoryURL;
/* 170:    */   }
/* 171:    */   
/* 172:    */   public URL getPackageRepositoryURL()
/* 173:    */   {
/* 174:278 */     return this.m_packageRepository;
/* 175:    */   }
/* 176:    */   
/* 177:    */   public void setProxy(Proxy proxyToUse)
/* 178:    */   {
/* 179:287 */     this.m_httpProxy = proxyToUse;
/* 180:    */   }
/* 181:    */   
/* 182:    */   public Proxy getProxy()
/* 183:    */   {
/* 184:296 */     return this.m_httpProxy;
/* 185:    */   }
/* 186:    */   
/* 187:    */   public void setProxyUsername(String proxyUsername)
/* 188:    */   {
/* 189:305 */     this.m_proxyUsername = proxyUsername;
/* 190:    */   }
/* 191:    */   
/* 192:    */   public void setProxyPassword(String proxyPassword)
/* 193:    */   {
/* 194:314 */     this.m_proxyPassword = proxyPassword;
/* 195:    */   }
/* 196:    */   
/* 197:    */   public Defaults getDefaultSettings()
/* 198:    */   {
/* 199:324 */     return null;
/* 200:    */   }
/* 201:    */   
/* 202:    */   public void applySettings(Settings settings) {}
/* 203:    */   
/* 204:    */   public abstract byte[] getRepositoryPackageMetaDataOnlyAsZip(PrintStream... paramVarArgs)
/* 205:    */     throws Exception;
/* 206:    */   
/* 207:    */   public abstract Package getPackageArchiveInfo(String paramString)
/* 208:    */     throws Exception;
/* 209:    */   
/* 210:    */   public abstract Package getInstalledPackageInfo(String paramString)
/* 211:    */     throws Exception;
/* 212:    */   
/* 213:    */   public abstract Package getRepositoryPackageInfo(String paramString)
/* 214:    */     throws Exception;
/* 215:    */   
/* 216:    */   public abstract Package getRepositoryPackageInfo(String paramString, Object paramObject)
/* 217:    */     throws Exception;
/* 218:    */   
/* 219:    */   public abstract List<Object> getRepositoryPackageVersions(String paramString)
/* 220:    */     throws Exception;
/* 221:    */   
/* 222:    */   public abstract Package getURLPackageInfo(URL paramURL)
/* 223:    */     throws Exception;
/* 224:    */   
/* 225:    */   public abstract String installPackageFromArchive(String paramString, PrintStream... paramVarArgs)
/* 226:    */     throws Exception;
/* 227:    */   
/* 228:    */   public abstract void installPackageFromRepository(String paramString, Object paramObject, PrintStream... paramVarArgs)
/* 229:    */     throws Exception;
/* 230:    */   
/* 231:    */   public abstract String installPackageFromURL(URL paramURL, PrintStream... paramVarArgs)
/* 232:    */     throws Exception;
/* 233:    */   
/* 234:    */   public abstract void installPackages(List<Package> paramList, PrintStream... paramVarArgs)
/* 235:    */     throws Exception;
/* 236:    */   
/* 237:    */   public abstract void uninstallPackage(String paramString, PrintStream... paramVarArgs)
/* 238:    */     throws Exception;
/* 239:    */   
/* 240:    */   public abstract List<Package> getInstalledPackages()
/* 241:    */     throws Exception;
/* 242:    */   
/* 243:    */   public abstract List<Package> getAllPackages(PrintStream... paramVarArgs)
/* 244:    */     throws Exception;
/* 245:    */   
/* 246:    */   public abstract List<Package> getAvailablePackages()
/* 247:    */     throws Exception;
/* 248:    */   
/* 249:    */   public abstract List<Dependency> getAllDependenciesForPackage(Package paramPackage, Map<String, List<Dependency>> paramMap)
/* 250:    */     throws Exception;
/* 251:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.packageManagement.PackageManager
 * JD-Core Version:    0.7.0.1
 */