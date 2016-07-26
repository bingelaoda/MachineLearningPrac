/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.util.HashSet;
/*   4:    */ import java.util.Vector;
/*   5:    */ 
/*   6:    */ public class AllJavadoc
/*   7:    */   extends Javadoc
/*   8:    */ {
/*   9:    */   protected static Vector<Javadoc> m_Javadocs;
/*  10:    */   
/*  11:    */   static
/*  12:    */   {
/*  13: 65 */     HashSet<String> set = new HashSet(ClassDiscovery.find(Javadoc.class, Javadoc.class.getPackage().getName()));
/*  14: 67 */     if (set.contains(AllJavadoc.class.getName())) {
/*  15: 68 */       set.remove(AllJavadoc.class.getName());
/*  16:    */     }
/*  17: 72 */     m_Javadocs = new Vector();
/*  18: 73 */     for (String classname : set) {
/*  19:    */       try
/*  20:    */       {
/*  21: 75 */         Class<?> cls = Class.forName(classname);
/*  22: 76 */         m_Javadocs.add((Javadoc)cls.newInstance());
/*  23:    */       }
/*  24:    */       catch (Exception e)
/*  25:    */       {
/*  26: 78 */         e.printStackTrace();
/*  27:    */       }
/*  28:    */     }
/*  29:    */   }
/*  30:    */   
/*  31:    */   public void setClassname(String value)
/*  32:    */   {
/*  33: 90 */     super.setClassname(value);
/*  34: 91 */     for (int i = 0; i < m_Javadocs.size(); i++) {
/*  35: 92 */       ((Javadoc)m_Javadocs.get(i)).setClassname(value);
/*  36:    */     }
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void setUseStars(boolean value)
/*  40:    */   {
/*  41:103 */     super.setUseStars(value);
/*  42:104 */     for (int i = 0; i < m_Javadocs.size(); i++) {
/*  43:105 */       ((Javadoc)m_Javadocs.get(i)).setUseStars(value);
/*  44:    */     }
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void setSilent(boolean value)
/*  48:    */   {
/*  49:116 */     super.setSilent(value);
/*  50:117 */     for (int i = 0; i < m_Javadocs.size(); i++) {
/*  51:118 */       ((Javadoc)m_Javadocs.get(i)).setSilent(value);
/*  52:    */     }
/*  53:    */   }
/*  54:    */   
/*  55:    */   protected String generateJavadoc(int index)
/*  56:    */     throws Exception
/*  57:    */   {
/*  58:131 */     throw new Exception("Not used!");
/*  59:    */   }
/*  60:    */   
/*  61:    */   protected String updateJavadoc(String content)
/*  62:    */     throws Exception
/*  63:    */   {
/*  64:147 */     String result = content;
/*  65:149 */     for (int i = 0; i < m_Javadocs.size(); i++) {
/*  66:150 */       result = ((Javadoc)m_Javadocs.get(i)).updateJavadoc(result);
/*  67:    */     }
/*  68:153 */     return result;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public String getRevision()
/*  72:    */   {
/*  73:163 */     return RevisionUtils.extract("$Revision: 11736 $");
/*  74:    */   }
/*  75:    */   
/*  76:    */   public static void main(String[] args)
/*  77:    */   {
/*  78:172 */     runJavadoc(new AllJavadoc(), args);
/*  79:    */   }
/*  80:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.AllJavadoc
 * JD-Core Version:    0.7.0.1
 */