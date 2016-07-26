/*   1:    */ package weka.core.xml;
/*   2:    */ 
/*   3:    */ import java.lang.reflect.Method;
/*   4:    */ import org.w3c.dom.Element;
/*   5:    */ import weka.core.RevisionHandler;
/*   6:    */ import weka.core.RevisionUtils;
/*   7:    */ 
/*   8:    */ public class XMLSerializationMethodHandler
/*   9:    */   implements RevisionHandler
/*  10:    */ {
/*  11: 47 */   protected MethodHandler m_ReadMethods = null;
/*  12: 50 */   protected MethodHandler m_WriteMethods = null;
/*  13: 53 */   protected Object owner = null;
/*  14:    */   
/*  15:    */   public XMLSerializationMethodHandler(Object owner)
/*  16:    */     throws Exception
/*  17:    */   {
/*  18: 66 */     this.owner = owner;
/*  19: 67 */     this.m_ReadMethods = new MethodHandler();
/*  20: 68 */     this.m_WriteMethods = new MethodHandler();
/*  21:    */     
/*  22: 70 */     clear();
/*  23:    */   }
/*  24:    */   
/*  25:    */   protected void addMethods(MethodHandler handler, Method template, Method[] methods)
/*  26:    */   {
/*  27: 88 */     for (int i = 0; i < methods.length; i++)
/*  28:    */     {
/*  29: 89 */       Method method = methods[i];
/*  30: 92 */       if (!template.equals(method)) {
/*  31: 98 */         if (template.getReturnType().equals(method.getReturnType())) {
/*  32:103 */           if (template.getParameterTypes().length == method.getParameterTypes().length)
/*  33:    */           {
/*  34:107 */             boolean equal = true;
/*  35:108 */             for (int n = 0; n < template.getParameterTypes().length; n++) {
/*  36:109 */               if (!template.getParameterTypes()[n].equals(method.getParameterTypes()[n]))
/*  37:    */               {
/*  38:111 */                 equal = false;
/*  39:112 */                 break;
/*  40:    */               }
/*  41:    */             }
/*  42:117 */             if (equal)
/*  43:    */             {
/*  44:118 */               String name = method.getName();
/*  45:119 */               name = name.replaceAll("read|write", "");
/*  46:120 */               name = name.substring(0, 1).toLowerCase() + name.substring(1);
/*  47:121 */               handler.add(name, method);
/*  48:    */             }
/*  49:    */           }
/*  50:    */         }
/*  51:    */       }
/*  52:    */     }
/*  53:    */   }
/*  54:    */   
/*  55:    */   protected void addMethods()
/*  56:    */     throws Exception
/*  57:    */   {
/*  58:140 */     Class<?>[] params = new Class[1];
/*  59:141 */     params[0] = Element.class;
/*  60:142 */     Method method = this.owner.getClass().getMethod("readFromXML", params);
/*  61:143 */     addMethods(this.m_ReadMethods, method, this.owner.getClass().getMethods());
/*  62:    */     
/*  63:    */ 
/*  64:146 */     params = new Class[3];
/*  65:147 */     params[0] = Element.class;
/*  66:148 */     params[1] = Object.class;
/*  67:149 */     params[2] = String.class;
/*  68:150 */     method = this.owner.getClass().getMethod("writeToXML", params);
/*  69:151 */     addMethods(this.m_WriteMethods, method, this.owner.getClass().getMethods());
/*  70:    */   }
/*  71:    */   
/*  72:    */   public static Method findReadMethod(Object o, String name)
/*  73:    */   {
/*  74:168 */     Method result = null;
/*  75:    */     
/*  76:170 */     Class<?>[] params = new Class[1];
/*  77:171 */     params[0] = Element.class;
/*  78:    */     try
/*  79:    */     {
/*  80:173 */       result = o.getClass().getMethod(name, params);
/*  81:    */     }
/*  82:    */     catch (Exception e)
/*  83:    */     {
/*  84:175 */       result = null;
/*  85:    */     }
/*  86:178 */     return result;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public static Method findWriteMethod(Object o, String name)
/*  90:    */   {
/*  91:195 */     Method result = null;
/*  92:    */     
/*  93:197 */     Class<?>[] params = new Class[3];
/*  94:198 */     params[0] = Element.class;
/*  95:199 */     params[1] = Object.class;
/*  96:200 */     params[2] = String.class;
/*  97:    */     try
/*  98:    */     {
/*  99:202 */       result = o.getClass().getMethod(name, params);
/* 100:    */     }
/* 101:    */     catch (Exception e)
/* 102:    */     {
/* 103:204 */       result = null;
/* 104:    */     }
/* 105:207 */     return result;
/* 106:    */   }
/* 107:    */   
/* 108:    */   public void clear()
/* 109:    */   {
/* 110:215 */     this.m_ReadMethods.clear();
/* 111:216 */     this.m_WriteMethods.clear();
/* 112:    */     try
/* 113:    */     {
/* 114:219 */       addMethods();
/* 115:    */     }
/* 116:    */     catch (Exception e)
/* 117:    */     {
/* 118:221 */       e.printStackTrace();
/* 119:    */     }
/* 120:    */   }
/* 121:    */   
/* 122:    */   public MethodHandler read()
/* 123:    */   {
/* 124:231 */     return this.m_ReadMethods;
/* 125:    */   }
/* 126:    */   
/* 127:    */   public MethodHandler write()
/* 128:    */   {
/* 129:240 */     return this.m_WriteMethods;
/* 130:    */   }
/* 131:    */   
/* 132:    */   public void register(Object handler, Class<?> cls, String name)
/* 133:    */   {
/* 134:252 */     read().add(cls, findReadMethod(handler, "read" + name));
/* 135:    */     
/* 136:254 */     write().add(cls, findWriteMethod(handler, "write" + name));
/* 137:    */   }
/* 138:    */   
/* 139:    */   public String toString()
/* 140:    */   {
/* 141:265 */     return "Read Methods:\n" + read() + "\n\n" + "Write Methods:\n" + write();
/* 142:    */   }
/* 143:    */   
/* 144:    */   public String getRevision()
/* 145:    */   {
/* 146:275 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 147:    */   }
/* 148:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.xml.XMLSerializationMethodHandler
 * JD-Core Version:    0.7.0.1
 */