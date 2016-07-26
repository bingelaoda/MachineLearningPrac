/*   1:    */ package weka.core.xml;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import java.util.HashSet;
/*   5:    */ import java.util.Hashtable;
/*   6:    */ import weka.core.RevisionHandler;
/*   7:    */ import weka.core.RevisionUtils;
/*   8:    */ 
/*   9:    */ public class PropertyHandler
/*  10:    */   implements RevisionHandler
/*  11:    */ {
/*  12: 47 */   protected Hashtable<Object, HashSet<String>> m_Ignored = null;
/*  13: 60 */   protected Hashtable<Object, HashSet<String>> m_Allowed = null;
/*  14:    */   
/*  15:    */   public PropertyHandler()
/*  16:    */   {
/*  17: 68 */     this.m_Ignored = new Hashtable();
/*  18: 69 */     this.m_Allowed = new Hashtable();
/*  19:    */   }
/*  20:    */   
/*  21:    */   public Enumeration<Object> ignored()
/*  22:    */   {
/*  23: 83 */     return this.m_Ignored.keys();
/*  24:    */   }
/*  25:    */   
/*  26:    */   public void addIgnored(String displayName)
/*  27:    */   {
/*  28: 98 */     HashSet<String> list = new HashSet();
/*  29: 99 */     list.add(displayName);
/*  30:    */     
/*  31:101 */     this.m_Ignored.put(displayName, list);
/*  32:    */   }
/*  33:    */   
/*  34:    */   public void addIgnored(Class<?> c, String displayName)
/*  35:    */   {
/*  36:    */     HashSet<String> list;
/*  37:    */     HashSet<String> list;
/*  38:116 */     if (this.m_Ignored.containsKey(c))
/*  39:    */     {
/*  40:117 */       list = (HashSet)this.m_Ignored.get(c);
/*  41:    */     }
/*  42:    */     else
/*  43:    */     {
/*  44:119 */       list = new HashSet();
/*  45:120 */       this.m_Ignored.put(c, list);
/*  46:    */     }
/*  47:123 */     list.add(displayName);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public boolean removeIgnored(String displayName)
/*  51:    */   {
/*  52:135 */     return this.m_Ignored.remove(displayName) != null;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public boolean removeIgnored(Class<?> c, String displayName)
/*  56:    */   {
/*  57:    */     HashSet<String> list;
/*  58:    */     HashSet<String> list;
/*  59:152 */     if (this.m_Ignored.containsKey(c)) {
/*  60:153 */       list = (HashSet)this.m_Ignored.get(c);
/*  61:    */     } else {
/*  62:155 */       list = new HashSet();
/*  63:    */     }
/*  64:158 */     return list.remove(displayName);
/*  65:    */   }
/*  66:    */   
/*  67:    */   public boolean isIgnored(String displayName)
/*  68:    */   {
/*  69:169 */     return this.m_Ignored.containsKey(displayName);
/*  70:    */   }
/*  71:    */   
/*  72:    */   public boolean isIgnored(Class<?> c, String displayName)
/*  73:    */   {
/*  74:    */     HashSet<String> list;
/*  75:    */     HashSet<String> list;
/*  76:188 */     if (this.m_Ignored.containsKey(c)) {
/*  77:189 */       list = (HashSet)this.m_Ignored.get(c);
/*  78:    */     } else {
/*  79:191 */       list = new HashSet();
/*  80:    */     }
/*  81:194 */     return list.contains(displayName);
/*  82:    */   }
/*  83:    */   
/*  84:    */   public boolean isIgnored(Object o, String displayName)
/*  85:    */   {
/*  86:215 */     boolean result = false;
/*  87:    */     
/*  88:217 */     Enumeration<Object> enm = ignored();
/*  89:218 */     while (enm.hasMoreElements())
/*  90:    */     {
/*  91:219 */       Object element = enm.nextElement();
/*  92:222 */       if ((element instanceof Class))
/*  93:    */       {
/*  94:226 */         Class<?> c = (Class)element;
/*  95:229 */         if (c.isInstance(o))
/*  96:    */         {
/*  97:230 */           HashSet<String> list = (HashSet)this.m_Ignored.get(c);
/*  98:231 */           result = list.contains(displayName);
/*  99:    */         }
/* 100:    */       }
/* 101:    */     }
/* 102:236 */     return result;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public Enumeration<Object> allowed()
/* 106:    */   {
/* 107:246 */     return this.m_Allowed.keys();
/* 108:    */   }
/* 109:    */   
/* 110:    */   public void addAllowed(Class<?> c, String displayName)
/* 111:    */   {
/* 112:261 */     HashSet<String> list = (HashSet)this.m_Allowed.get(c);
/* 113:262 */     if (list == null)
/* 114:    */     {
/* 115:263 */       list = new HashSet();
/* 116:264 */       this.m_Allowed.put(c, list);
/* 117:    */     }
/* 118:268 */     list.add(displayName);
/* 119:    */   }
/* 120:    */   
/* 121:    */   public boolean removeAllowed(Class<?> c, String displayName)
/* 122:    */   {
/* 123:284 */     boolean result = false;
/* 124:    */     
/* 125:    */ 
/* 126:287 */     HashSet<String> list = (HashSet)this.m_Allowed.get(c);
/* 127:290 */     if (list != null) {
/* 128:291 */       result = list.remove(displayName);
/* 129:    */     }
/* 130:294 */     return result;
/* 131:    */   }
/* 132:    */   
/* 133:    */   public boolean isAllowed(Class<?> c, String displayName)
/* 134:    */   {
/* 135:313 */     boolean result = true;
/* 136:    */     
/* 137:    */ 
/* 138:316 */     HashSet<String> list = (HashSet)this.m_Allowed.get(c);
/* 139:319 */     if (list != null) {
/* 140:320 */       result = list.contains(displayName);
/* 141:    */     }
/* 142:323 */     return result;
/* 143:    */   }
/* 144:    */   
/* 145:    */   public boolean isAllowed(Object o, String displayName)
/* 146:    */   {
/* 147:342 */     boolean result = true;
/* 148:    */     
/* 149:344 */     Enumeration<Object> enm = allowed();
/* 150:345 */     while (enm.hasMoreElements())
/* 151:    */     {
/* 152:346 */       Class<?> c = (Class)enm.nextElement();
/* 153:349 */       if (c.isInstance(o))
/* 154:    */       {
/* 155:350 */         HashSet<String> list = (HashSet)this.m_Allowed.get(c);
/* 156:351 */         result = list.contains(displayName);
/* 157:    */       }
/* 158:    */     }
/* 159:356 */     return result;
/* 160:    */   }
/* 161:    */   
/* 162:    */   public String getRevision()
/* 163:    */   {
/* 164:366 */     return RevisionUtils.extract("$Revision: 11247 $");
/* 165:    */   }
/* 166:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.xml.PropertyHandler
 * JD-Core Version:    0.7.0.1
 */