/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.InputStream;
/*   6:    */ import java.util.List;
/*   7:    */ import java.util.Map;
/*   8:    */ import java.util.Properties;
/*   9:    */ import java.util.Set;
/*  10:    */ 
/*  11:    */ @Deprecated
/*  12:    */ public class PluginManager
/*  13:    */ {
/*  14:    */   public static synchronized void addToDisabledList(List<String> classnames)
/*  15:    */   {
/*  16: 51 */     weka.core.PluginManager.addToDisabledList(classnames);
/*  17:    */   }
/*  18:    */   
/*  19:    */   public static synchronized void addToDisabledList(String classname)
/*  20:    */   {
/*  21: 60 */     weka.core.PluginManager.addToDisabledList(classname);
/*  22:    */   }
/*  23:    */   
/*  24:    */   public static synchronized void removeFromDisabledList(List<String> classnames)
/*  25:    */   {
/*  26: 71 */     weka.core.PluginManager.removeFromDisabledList(classnames);
/*  27:    */   }
/*  28:    */   
/*  29:    */   public static synchronized void removeFromDisabledList(String classname)
/*  30:    */   {
/*  31: 81 */     weka.core.PluginManager.removeFromDisabledList(classname);
/*  32:    */   }
/*  33:    */   
/*  34:    */   public static boolean isInDisabledList(String classname)
/*  35:    */   {
/*  36: 92 */     return weka.core.PluginManager.isInDisabledList(classname);
/*  37:    */   }
/*  38:    */   
/*  39:    */   public static synchronized void addFromProperties(File propsFile)
/*  40:    */     throws Exception
/*  41:    */   {
/*  42:103 */     weka.core.PluginManager.addFromProperties(propsFile);
/*  43:    */   }
/*  44:    */   
/*  45:    */   public static synchronized void addFromProperties(File propsFile, boolean maintainInsertionOrder)
/*  46:    */     throws Exception
/*  47:    */   {
/*  48:116 */     weka.core.PluginManager.addFromProperties(propsFile, maintainInsertionOrder);
/*  49:    */   }
/*  50:    */   
/*  51:    */   public static synchronized void addFromProperties(InputStream propsStream)
/*  52:    */     throws Exception
/*  53:    */   {
/*  54:128 */     weka.core.PluginManager.addFromProperties(propsStream);
/*  55:    */   }
/*  56:    */   
/*  57:    */   public static synchronized void addFromProperties(InputStream propsStream, boolean maintainInsertionOrder)
/*  58:    */     throws Exception
/*  59:    */   {
/*  60:141 */     weka.core.PluginManager.addFromProperties(propsStream, maintainInsertionOrder);
/*  61:    */   }
/*  62:    */   
/*  63:    */   public static synchronized void addFromProperties(Properties props)
/*  64:    */     throws Exception
/*  65:    */   {
/*  66:153 */     weka.core.PluginManager.addFromProperties(props);
/*  67:    */   }
/*  68:    */   
/*  69:    */   public static synchronized void addFromProperties(Properties props, boolean maintainInsertionOrder)
/*  70:    */     throws Exception
/*  71:    */   {
/*  72:166 */     weka.core.PluginManager.addFromProperties(props, maintainInsertionOrder);
/*  73:    */   }
/*  74:    */   
/*  75:    */   public static synchronized void addPluginResource(String resourceGroupID, String resourceDescription, String resourcePath)
/*  76:    */   {
/*  77:179 */     weka.core.PluginManager.addPlugin(resourceGroupID, resourceDescription, resourcePath);
/*  78:    */   }
/*  79:    */   
/*  80:    */   public static InputStream getPluginResourceAsStream(String resourceGroupID, String resourceDescription)
/*  81:    */     throws IOException
/*  82:    */   {
/*  83:195 */     return weka.core.PluginManager.getPluginResourceAsStream(resourceGroupID, resourceDescription);
/*  84:    */   }
/*  85:    */   
/*  86:    */   public static int numResourcesForWithGroupID(String resourceGroupID)
/*  87:    */   {
/*  88:206 */     return weka.core.PluginManager.numResourcesForWithGroupID(resourceGroupID);
/*  89:    */   }
/*  90:    */   
/*  91:    */   public static Map<String, String> getResourcesWithGroupID(String resourceGroupID)
/*  92:    */   {
/*  93:219 */     return weka.core.PluginManager.getResourcesWithGroupID(resourceGroupID);
/*  94:    */   }
/*  95:    */   
/*  96:    */   public static Set<String> getPluginNamesOfType(String interfaceName)
/*  97:    */   {
/*  98:231 */     return weka.core.PluginManager.getPluginNamesOfType(interfaceName);
/*  99:    */   }
/* 100:    */   
/* 101:    */   public static void addPlugin(String interfaceName, String name, String concreteType)
/* 102:    */   {
/* 103:246 */     weka.core.PluginManager.addPlugin(interfaceName, name, concreteType);
/* 104:    */   }
/* 105:    */   
/* 106:    */   public static void addPlugin(String interfaceName, String name, String concreteType, boolean maintainInsertionOrder)
/* 107:    */   {
/* 108:263 */     weka.core.PluginManager.addPlugin(interfaceName, name, concreteType, maintainInsertionOrder);
/* 109:    */   }
/* 110:    */   
/* 111:    */   public static void removePlugins(String interfaceName, List<String> names)
/* 112:    */   {
/* 113:275 */     for (String name : names) {
/* 114:276 */       weka.core.PluginManager.removePlugins(interfaceName, names);
/* 115:    */     }
/* 116:    */   }
/* 117:    */   
/* 118:    */   public static void removePlugin(String interfaceName, String name)
/* 119:    */   {
/* 120:289 */     weka.core.PluginManager.removePlugin(interfaceName, name);
/* 121:    */   }
/* 122:    */   
/* 123:    */   public static Object getPluginInstance(String interfaceType, String name)
/* 124:    */     throws Exception
/* 125:    */   {
/* 126:302 */     return weka.core.PluginManager.getPluginInstance(interfaceType, name);
/* 127:    */   }
/* 128:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.PluginManager
 * JD-Core Version:    0.7.0.1
 */