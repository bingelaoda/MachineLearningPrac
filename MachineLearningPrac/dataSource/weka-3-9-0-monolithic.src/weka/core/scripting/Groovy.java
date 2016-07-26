/*   1:    */ package weka.core.scripting;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.io.Serializable;
/*   6:    */ import java.lang.reflect.Constructor;
/*   7:    */ import java.lang.reflect.Method;
/*   8:    */ import weka.core.RevisionHandler;
/*   9:    */ import weka.core.RevisionUtils;
/*  10:    */ 
/*  11:    */ public class Groovy
/*  12:    */   implements Serializable, RevisionHandler
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = -2628766602043134673L;
/*  15:    */   public static final String CLASS_GROOVYCLASSLOADER = "groovy.lang.GroovyClassLoader";
/*  16: 53 */   protected static boolean m_Present = false;
/*  17:    */   protected Object m_ClassLoader;
/*  18:    */   
/*  19:    */   static
/*  20:    */   {
/*  21:    */     try
/*  22:    */     {
/*  23: 56 */       Class.forName("groovy.lang.GroovyClassLoader");
/*  24: 57 */       m_Present = true;
/*  25:    */     }
/*  26:    */     catch (Exception e)
/*  27:    */     {
/*  28: 59 */       m_Present = false;
/*  29:    */     }
/*  30:    */   }
/*  31:    */   
/*  32:    */   public Groovy()
/*  33:    */   {
/*  34: 70 */     this.m_ClassLoader = newClassLoader();
/*  35:    */   }
/*  36:    */   
/*  37:    */   public Object getClassLoader()
/*  38:    */   {
/*  39: 79 */     return this.m_ClassLoader;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public Object invoke(String methodName, Class<?>[] paramClasses, Object[] paramValues)
/*  43:    */   {
/*  44: 95 */     Object result = null;
/*  45: 96 */     if (getClassLoader() != null) {
/*  46: 97 */       result = invoke(getClassLoader(), methodName, paramClasses, paramValues);
/*  47:    */     }
/*  48:100 */     return result;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public static boolean isPresent()
/*  52:    */   {
/*  53:110 */     return m_Present;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public static Object newClassLoader()
/*  57:    */   {
/*  58:123 */     Object result = null;
/*  59:125 */     if (isPresent()) {
/*  60:    */       try
/*  61:    */       {
/*  62:127 */         Class<?> cls = Class.forName("groovy.lang.GroovyClassLoader");
/*  63:128 */         Constructor<?> constr = cls.getConstructor(new Class[] { ClassLoader.class });
/*  64:129 */         result = constr.newInstance(new Object[] { Groovy.class.getClassLoader() });
/*  65:    */       }
/*  66:    */       catch (Exception e)
/*  67:    */       {
/*  68:131 */         e.printStackTrace();
/*  69:132 */         result = null;
/*  70:    */       }
/*  71:    */     }
/*  72:136 */     return result;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public static Object newInstance(File file, Class<?> template)
/*  76:    */   {
/*  77:152 */     Object result = null;
/*  78:154 */     if (!isPresent()) {
/*  79:155 */       return result;
/*  80:    */     }
/*  81:158 */     Object interpreter = newClassLoader();
/*  82:159 */     if (interpreter == null) {
/*  83:160 */       return result;
/*  84:    */     }
/*  85:    */     try
/*  86:    */     {
/*  87:164 */       Class<?> cls = (Class)invoke(interpreter, "parseClass", new Class[] { File.class }, new Object[] { file });
/*  88:    */       
/*  89:166 */       result = cls.newInstance();
/*  90:    */     }
/*  91:    */     catch (Exception e)
/*  92:    */     {
/*  93:168 */       e.printStackTrace();
/*  94:    */     }
/*  95:171 */     return result;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public static Object invoke(Object o, String methodName, Class<?>[] paramClasses, Object[] paramValues)
/*  99:    */   {
/* 100:189 */     Object result = null;
/* 101:    */     try
/* 102:    */     {
/* 103:192 */       Method m = o.getClass().getMethod(methodName, paramClasses);
/* 104:193 */       result = m.invoke(o, paramValues);
/* 105:    */     }
/* 106:    */     catch (Exception e)
/* 107:    */     {
/* 108:195 */       e.printStackTrace();
/* 109:196 */       result = null;
/* 110:    */     }
/* 111:199 */     return result;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public String getRevision()
/* 115:    */   {
/* 116:209 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 117:    */   }
/* 118:    */   
/* 119:    */   public static void main(String[] args)
/* 120:    */   {
/* 121:219 */     if (args.length == 0)
/* 122:    */     {
/* 123:220 */       System.out.println("Groovy present: " + isPresent());
/* 124:    */     }
/* 125:    */     else
/* 126:    */     {
/* 127:222 */       Groovy groovy = new Groovy();
/* 128:223 */       if (groovy.getClassLoader() == null)
/* 129:    */       {
/* 130:224 */         System.err.println("Cannot instantiate Groovy ClassLoader!");
/* 131:    */       }
/* 132:    */       else
/* 133:    */       {
/* 134:226 */         Object groovyObject = newInstance(new File(args[0]), Object.class);
/* 135:    */         
/* 136:228 */         invoke(groovyObject, "run", new Class[0], new Object[0]);
/* 137:    */       }
/* 138:    */     }
/* 139:    */   }
/* 140:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.scripting.Groovy
 * JD-Core Version:    0.7.0.1
 */