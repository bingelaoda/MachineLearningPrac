/*   1:    */ package weka.core.xml;
/*   2:    */ 
/*   3:    */ import java.lang.reflect.Method;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Hashtable;
/*   6:    */ import weka.core.RevisionHandler;
/*   7:    */ import weka.core.RevisionUtils;
/*   8:    */ 
/*   9:    */ public class MethodHandler
/*  10:    */   implements RevisionHandler
/*  11:    */ {
/*  12: 52 */   protected Hashtable<Object, Method> m_Methods = null;
/*  13:    */   
/*  14:    */   public MethodHandler()
/*  15:    */   {
/*  16: 59 */     this.m_Methods = new Hashtable();
/*  17:    */   }
/*  18:    */   
/*  19:    */   public Enumeration<Object> keys()
/*  20:    */   {
/*  21: 70 */     return this.m_Methods.keys();
/*  22:    */   }
/*  23:    */   
/*  24:    */   public void add(String displayName, Method method)
/*  25:    */   {
/*  26: 83 */     if (method != null) {
/*  27: 84 */       this.m_Methods.put(displayName, method);
/*  28:    */     }
/*  29:    */   }
/*  30:    */   
/*  31:    */   public void add(Class<?> c, Method method)
/*  32:    */   {
/*  33: 97 */     if (method != null) {
/*  34: 98 */       this.m_Methods.put(c, method);
/*  35:    */     }
/*  36:    */   }
/*  37:    */   
/*  38:    */   public boolean remove(String displayName)
/*  39:    */   {
/*  40:112 */     return this.m_Methods.remove(displayName) != null;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public boolean remove(Class<?> c)
/*  44:    */   {
/*  45:123 */     return this.m_Methods.remove(c) != null;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public boolean contains(String displayName)
/*  49:    */   {
/*  50:134 */     return this.m_Methods.containsKey(displayName);
/*  51:    */   }
/*  52:    */   
/*  53:    */   public boolean contains(Class<?> c)
/*  54:    */   {
/*  55:145 */     return this.m_Methods.containsKey(c);
/*  56:    */   }
/*  57:    */   
/*  58:    */   public Method get(String displayName)
/*  59:    */   {
/*  60:158 */     return (Method)this.m_Methods.get(displayName);
/*  61:    */   }
/*  62:    */   
/*  63:    */   public Method get(Class<?> c)
/*  64:    */   {
/*  65:169 */     return (Method)this.m_Methods.get(c);
/*  66:    */   }
/*  67:    */   
/*  68:    */   public int size()
/*  69:    */   {
/*  70:178 */     return this.m_Methods.size();
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void clear()
/*  74:    */   {
/*  75:185 */     this.m_Methods.clear();
/*  76:    */   }
/*  77:    */   
/*  78:    */   public String toString()
/*  79:    */   {
/*  80:196 */     return this.m_Methods.toString();
/*  81:    */   }
/*  82:    */   
/*  83:    */   public String getRevision()
/*  84:    */   {
/*  85:206 */     return RevisionUtils.extract("$Revision: 10203 $");
/*  86:    */   }
/*  87:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.xml.MethodHandler
 * JD-Core Version:    0.7.0.1
 */