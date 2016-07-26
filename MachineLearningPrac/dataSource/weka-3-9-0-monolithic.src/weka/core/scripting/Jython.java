/*   1:    */ package weka.core.scripting;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.io.Serializable;
/*   7:    */ import java.lang.reflect.Constructor;
/*   8:    */ import java.lang.reflect.Method;
/*   9:    */ import java.util.HashSet;
/*  10:    */ import weka.core.RevisionHandler;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ 
/*  13:    */ public class Jython
/*  14:    */   implements Serializable, RevisionHandler
/*  15:    */ {
/*  16:    */   private static final long serialVersionUID = -6972298704460209252L;
/*  17:    */   public static final String CLASS_PYTHONINERPRETER = "org.python.util.PythonInterpreter";
/*  18:    */   public static final String CLASS_PYTHONOBJECTINPUTSTREAM = "org.python.util.PythonObjectInputStream";
/*  19: 52 */   protected static boolean m_Present = false;
/*  20:    */   protected Object m_Interpreter;
/*  21:    */   
/*  22:    */   static
/*  23:    */   {
/*  24:    */     try
/*  25:    */     {
/*  26: 55 */       Class.forName("org.python.util.PythonInterpreter");
/*  27: 56 */       m_Present = true;
/*  28:    */     }
/*  29:    */     catch (Exception e)
/*  30:    */     {
/*  31: 58 */       m_Present = false;
/*  32:    */     }
/*  33:    */   }
/*  34:    */   
/*  35:    */   public Jython()
/*  36:    */   {
/*  37: 69 */     this.m_Interpreter = newInterpreter();
/*  38:    */   }
/*  39:    */   
/*  40:    */   public Object getInterpreter()
/*  41:    */   {
/*  42: 78 */     return this.m_Interpreter;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public Object invoke(String methodName, Class<?>[] paramClasses, Object[] paramValues)
/*  46:    */   {
/*  47: 94 */     Object result = null;
/*  48: 95 */     if (getInterpreter() != null) {
/*  49: 96 */       result = invoke(getInterpreter(), methodName, paramClasses, paramValues);
/*  50:    */     }
/*  51: 99 */     return result;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public static boolean isPresent()
/*  55:    */   {
/*  56:109 */     return m_Present;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public static Object newInterpreter()
/*  60:    */   {
/*  61:120 */     Object result = null;
/*  62:122 */     if (isPresent()) {
/*  63:    */       try
/*  64:    */       {
/*  65:124 */         result = Class.forName("org.python.util.PythonInterpreter").newInstance();
/*  66:    */       }
/*  67:    */       catch (Exception e)
/*  68:    */       {
/*  69:126 */         e.printStackTrace();
/*  70:127 */         result = null;
/*  71:    */       }
/*  72:    */     }
/*  73:131 */     return result;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public static Object newInstance(File file, Class<?> template)
/*  77:    */   {
/*  78:143 */     return newInstance(file, template, new File[0]);
/*  79:    */   }
/*  80:    */   
/*  81:    */   public static Object newInstance(File file, Class<?> template, File[] paths)
/*  82:    */   {
/*  83:168 */     Object result = null;
/*  84:170 */     if (!isPresent()) {
/*  85:171 */       return result;
/*  86:    */     }
/*  87:174 */     Object interpreter = newInterpreter();
/*  88:175 */     if (interpreter == null) {
/*  89:176 */       return result;
/*  90:    */     }
/*  91:180 */     if (paths.length > 0)
/*  92:    */     {
/*  93:181 */       invoke(interpreter, "exec", new Class[] { String.class }, new Object[] { "import sys" });
/*  94:    */       
/*  95:    */ 
/*  96:    */ 
/*  97:185 */       String instanceName = "syspath";
/*  98:186 */       invoke(interpreter, "exec", new Class[] { String.class }, new Object[] { instanceName + " = sys.path" });
/*  99:    */       
/* 100:188 */       HashSet<String> currentPaths = new HashSet();
/* 101:    */       try
/* 102:    */       {
/* 103:190 */         String[] tmpPaths = (String[])invoke(interpreter, "get", new Class[] { String.class, Class.class }, new Object[] { instanceName, [Ljava.lang.String.class });
/* 104:193 */         for (i = 0; i < tmpPaths.length; i++) {
/* 105:194 */           currentPaths.add(tmpPaths[i]);
/* 106:    */         }
/* 107:    */       }
/* 108:    */       catch (Exception ex)
/* 109:    */       {
/* 110:197 */         ex.printStackTrace();
/* 111:    */       }
/* 112:201 */       for (int i = 0; i < paths.length; i++) {
/* 113:202 */         if (!currentPaths.contains(paths[i].getAbsolutePath())) {
/* 114:203 */           invoke(interpreter, "exec", new Class[] { String.class }, new Object[] { "sys.path.append('" + paths[i].getAbsolutePath() + "')" });
/* 115:    */         }
/* 116:    */       }
/* 117:    */     }
/* 118:211 */     String filename = file.getAbsolutePath();
/* 119:212 */     invoke(interpreter, "execfile", new Class[] { String.class }, new Object[] { filename });
/* 120:    */     
/* 121:214 */     String tempName = filename.substring(filename.lastIndexOf("/") + 1);
/* 122:215 */     tempName = tempName.substring(0, tempName.indexOf("."));
/* 123:216 */     String instanceName = tempName.toLowerCase();
/* 124:217 */     String javaClassName = tempName.substring(0, 1).toUpperCase() + tempName.substring(1);
/* 125:    */     
/* 126:219 */     String objectDef = "=" + javaClassName + "()";
/* 127:220 */     invoke(interpreter, "exec", new Class[] { String.class }, new Object[] { instanceName + objectDef });
/* 128:    */     try
/* 129:    */     {
/* 130:223 */       result = invoke(interpreter, "get", new Class[] { String.class, Class.class }, new Object[] { instanceName, template });
/* 131:    */     }
/* 132:    */     catch (Exception ex)
/* 133:    */     {
/* 134:226 */       ex.printStackTrace();
/* 135:    */     }
/* 136:229 */     return result;
/* 137:    */   }
/* 138:    */   
/* 139:    */   public static Object invoke(Object o, String methodName, Class<?>[] paramClasses, Object[] paramValues)
/* 140:    */   {
/* 141:247 */     Object result = null;
/* 142:    */     try
/* 143:    */     {
/* 144:250 */       Method m = o.getClass().getMethod(methodName, paramClasses);
/* 145:251 */       result = m.invoke(o, paramValues);
/* 146:    */     }
/* 147:    */     catch (Exception e)
/* 148:    */     {
/* 149:253 */       e.printStackTrace();
/* 150:254 */       result = null;
/* 151:    */     }
/* 152:257 */     return result;
/* 153:    */   }
/* 154:    */   
/* 155:    */   public static Object deserialize(InputStream in)
/* 156:    */   {
/* 157:274 */     Object result = null;
/* 158:    */     try
/* 159:    */     {
/* 160:277 */       Class<?> cls = Class.forName("org.python.util.PythonObjectInputStream");
/* 161:278 */       Class<?>[] paramTypes = { InputStream.class };
/* 162:279 */       Constructor<?> constr = cls.getConstructor(paramTypes);
/* 163:280 */       Object[] arglist = { in };
/* 164:281 */       Object obj = constr.newInstance(arglist);
/* 165:282 */       result = invoke(obj, "readObject", new Class[0], new Object[0]);
/* 166:    */     }
/* 167:    */     catch (Exception e)
/* 168:    */     {
/* 169:284 */       e.printStackTrace();
/* 170:    */     }
/* 171:287 */     return result;
/* 172:    */   }
/* 173:    */   
/* 174:    */   public String getRevision()
/* 175:    */   {
/* 176:297 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 177:    */   }
/* 178:    */   
/* 179:    */   public static void main(String[] args)
/* 180:    */   {
/* 181:307 */     if (args.length == 0)
/* 182:    */     {
/* 183:308 */       System.out.println("Jython present: " + isPresent());
/* 184:    */     }
/* 185:    */     else
/* 186:    */     {
/* 187:310 */       Jython jython = new Jython();
/* 188:311 */       if (jython.getInterpreter() == null) {
/* 189:312 */         System.err.println("Cannot instantiate Python Interpreter!");
/* 190:    */       } else {
/* 191:314 */         jython.invoke("execfile", new Class[] { String.class }, new Object[] { args[0] });
/* 192:    */       }
/* 193:    */     }
/* 194:    */   }
/* 195:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.scripting.Jython
 * JD-Core Version:    0.7.0.1
 */