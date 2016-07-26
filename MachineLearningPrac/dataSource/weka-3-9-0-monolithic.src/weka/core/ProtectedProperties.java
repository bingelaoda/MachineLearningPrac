/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.InputStream;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Map;
/*   6:    */ import java.util.Properties;
/*   7:    */ 
/*   8:    */ public class ProtectedProperties
/*   9:    */   extends Properties
/*  10:    */   implements RevisionHandler
/*  11:    */ {
/*  12:    */   private static final long serialVersionUID = 3876658672657323985L;
/*  13: 42 */   private boolean closed = false;
/*  14:    */   
/*  15:    */   public ProtectedProperties(Properties props)
/*  16:    */   {
/*  17: 51 */     Enumeration<?> propEnum = props.propertyNames();
/*  18: 52 */     while (propEnum.hasMoreElements())
/*  19:    */     {
/*  20: 53 */       String propName = (String)propEnum.nextElement();
/*  21: 54 */       String propValue = props.getProperty(propName);
/*  22: 55 */       super.setProperty(propName, propValue);
/*  23:    */     }
/*  24: 57 */     this.closed = true;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public Object setProperty(String key, String value)
/*  28:    */   {
/*  29: 69 */     if (this.closed) {
/*  30: 70 */       throw new UnsupportedOperationException("ProtectedProperties cannot be modified!");
/*  31:    */     }
/*  32: 73 */     return super.setProperty(key, value);
/*  33:    */   }
/*  34:    */   
/*  35:    */   public void load(InputStream inStream)
/*  36:    */   {
/*  37: 85 */     throw new UnsupportedOperationException("ProtectedProperties cannot be modified!");
/*  38:    */   }
/*  39:    */   
/*  40:    */   public void clear()
/*  41:    */   {
/*  42: 97 */     throw new UnsupportedOperationException("ProtectedProperties cannot be modified!");
/*  43:    */   }
/*  44:    */   
/*  45:    */   public Object put(Object key, Object value)
/*  46:    */   {
/*  47:110 */     if (this.closed) {
/*  48:111 */       throw new UnsupportedOperationException("ProtectedProperties cannot be modified!");
/*  49:    */     }
/*  50:114 */     return super.put(key, value);
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void putAll(Map<? extends Object, ? extends Object> t)
/*  54:    */   {
/*  55:126 */     throw new UnsupportedOperationException("ProtectedProperties cannot be modified!");
/*  56:    */   }
/*  57:    */   
/*  58:    */   public Object remove(Object key)
/*  59:    */   {
/*  60:139 */     throw new UnsupportedOperationException("ProtectedProperties cannot be modified!");
/*  61:    */   }
/*  62:    */   
/*  63:    */   public String getRevision()
/*  64:    */   {
/*  65:150 */     return RevisionUtils.extract("$Revision: 10203 $");
/*  66:    */   }
/*  67:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.ProtectedProperties
 * JD-Core Version:    0.7.0.1
 */