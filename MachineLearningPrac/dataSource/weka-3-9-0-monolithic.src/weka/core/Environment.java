/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Iterator;
/*   6:    */ import java.util.Map;
/*   7:    */ import java.util.Properties;
/*   8:    */ import java.util.Set;
/*   9:    */ import java.util.TreeMap;
/*  10:    */ 
/*  11:    */ public class Environment
/*  12:    */   implements RevisionHandler
/*  13:    */ {
/*  14: 43 */   private static Environment m_systemWide = new Environment();
/*  15: 46 */   private final Map<String, String> m_envVars = new TreeMap();
/*  16:    */   
/*  17:    */   public Environment()
/*  18:    */   {
/*  19: 50 */     Map<String, String> env = System.getenv();
/*  20: 51 */     Set<String> keys = env.keySet();
/*  21: 52 */     Iterator<String> i = keys.iterator();
/*  22: 53 */     while (i.hasNext())
/*  23:    */     {
/*  24: 54 */       String kv = (String)i.next();
/*  25: 55 */       String value = (String)env.get(kv);
/*  26: 56 */       this.m_envVars.put(kv, value);
/*  27:    */     }
/*  28: 60 */     Properties jvmProps = System.getProperties();
/*  29: 61 */     Enumeration<?> pKeys = jvmProps.propertyNames();
/*  30: 62 */     while (pKeys.hasMoreElements())
/*  31:    */     {
/*  32: 63 */       String kv = (String)pKeys.nextElement();
/*  33: 64 */       String value = jvmProps.getProperty(kv);
/*  34: 65 */       this.m_envVars.put(kv, value);
/*  35:    */     }
/*  36: 67 */     this.m_envVars.put("weka.version", Version.VERSION);
/*  37:    */   }
/*  38:    */   
/*  39:    */   public static Environment getSystemWide()
/*  40:    */   {
/*  41: 77 */     return m_systemWide;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public static boolean containsEnvVariables(String source)
/*  45:    */   {
/*  46: 87 */     return source.indexOf("${") >= 0;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public String substitute(String source)
/*  50:    */     throws Exception
/*  51:    */   {
/*  52: 99 */     int index = source.indexOf("${");
/*  53:101 */     while (index >= 0)
/*  54:    */     {
/*  55:102 */       index += 2;
/*  56:103 */       int endIndex = source.indexOf('}');
/*  57:104 */       if ((endIndex < 0) || (endIndex <= index + 1)) {
/*  58:    */         break;
/*  59:    */       }
/*  60:105 */       String key = source.substring(index, endIndex);
/*  61:    */       
/*  62:    */ 
/*  63:108 */       String replace = (String)this.m_envVars.get(key);
/*  64:109 */       if (replace != null)
/*  65:    */       {
/*  66:110 */         String toReplace = "${" + key + "}";
/*  67:111 */         source = source.replace(toReplace, replace);
/*  68:    */       }
/*  69:    */       else
/*  70:    */       {
/*  71:113 */         throw new Exception("[Environment] Variable " + key + " doesn't seem to be set.");
/*  72:    */       }
/*  73:119 */       index = source.indexOf("${");
/*  74:    */     }
/*  75:121 */     return source;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void addVariable(String key, String value)
/*  79:    */   {
/*  80:131 */     this.m_envVars.put(key, value);
/*  81:    */   }
/*  82:    */   
/*  83:    */   public void addVariableSystemWide(String key, String value)
/*  84:    */   {
/*  85:142 */     addVariable(key, value);
/*  86:145 */     if (this != getSystemWide()) {
/*  87:146 */       getSystemWide().addVariableSystemWide(key, value);
/*  88:    */     }
/*  89:148 */     System.setProperty(key, value);
/*  90:    */   }
/*  91:    */   
/*  92:    */   public void removeVariable(String key)
/*  93:    */   {
/*  94:157 */     this.m_envVars.remove(key);
/*  95:    */   }
/*  96:    */   
/*  97:    */   public Set<String> getVariableNames()
/*  98:    */   {
/*  99:166 */     return this.m_envVars.keySet();
/* 100:    */   }
/* 101:    */   
/* 102:    */   public String getVariableValue(String key)
/* 103:    */   {
/* 104:177 */     return (String)this.m_envVars.get(key);
/* 105:    */   }
/* 106:    */   
/* 107:    */   public static void main(String[] args)
/* 108:    */   {
/* 109:187 */     Environment t = new Environment();
/* 110:191 */     if (args.length == 0) {
/* 111:192 */       System.err.println("Usage: java weka.core.Environment <string> <string> ...");
/* 112:    */     } else {
/* 113:    */       try
/* 114:    */       {
/* 115:196 */         for (String arg : args)
/* 116:    */         {
/* 117:197 */           String newS = t.substitute(arg);
/* 118:198 */           System.out.println("Original string:\n" + arg + "\n\nNew string:\n" + newS);
/* 119:    */         }
/* 120:    */       }
/* 121:    */       catch (Exception ex)
/* 122:    */       {
/* 123:202 */         ex.printStackTrace();
/* 124:    */       }
/* 125:    */     }
/* 126:    */   }
/* 127:    */   
/* 128:    */   public String getRevision()
/* 129:    */   {
/* 130:214 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 131:    */   }
/* 132:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.Environment
 * JD-Core Version:    0.7.0.1
 */