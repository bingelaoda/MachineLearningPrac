/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.lang.reflect.Method;
/*   4:    */ 
/*   5:    */ public class GlobalInfoJavadoc
/*   6:    */   extends Javadoc
/*   7:    */ {
/*   8:    */   public static final String GLOBALINFO_METHOD = "globalInfo";
/*   9:    */   public static final String GLOBALINFO_STARTTAG = "<!-- globalinfo-start -->";
/*  10:    */   public static final String GLOBALINFO_ENDTAG = "<!-- globalinfo-end -->";
/*  11:    */   
/*  12:    */   public GlobalInfoJavadoc()
/*  13:    */   {
/*  14: 72 */     this.m_StartTag = new String[1];
/*  15: 73 */     this.m_EndTag = new String[1];
/*  16: 74 */     this.m_StartTag[0] = "<!-- globalinfo-start -->";
/*  17: 75 */     this.m_EndTag[0] = "<!-- globalinfo-end -->";
/*  18:    */   }
/*  19:    */   
/*  20:    */   protected String generateJavadoc(int index)
/*  21:    */     throws Exception
/*  22:    */   {
/*  23: 89 */     String result = "";
/*  24: 91 */     if (index == 0)
/*  25:    */     {
/*  26: 92 */       if (!canInstantiateClass()) {
/*  27: 93 */         return result;
/*  28:    */       }
/*  29:    */       Method method;
/*  30:    */       try
/*  31:    */       {
/*  32: 96 */         method = getInstance().getClass().getMethod("globalInfo", (Class[])null);
/*  33:    */       }
/*  34:    */       catch (Exception e)
/*  35:    */       {
/*  36:100 */         return result;
/*  37:    */       }
/*  38:104 */       result = toHTML((String)method.invoke(getInstance(), (Object[])null));
/*  39:105 */       result = result.trim() + "\n<br><br>\n";
/*  40:108 */       if (getUseStars()) {
/*  41:109 */         result = indent(result, 1, "* ");
/*  42:    */       }
/*  43:    */     }
/*  44:112 */     return result;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public String getRevision()
/*  48:    */   {
/*  49:121 */     return RevisionUtils.extract("$Revision: 11736 $");
/*  50:    */   }
/*  51:    */   
/*  52:    */   public static void main(String[] args)
/*  53:    */   {
/*  54:130 */     runJavadoc(new GlobalInfoJavadoc(), args);
/*  55:    */   }
/*  56:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.GlobalInfoJavadoc
 * JD-Core Version:    0.7.0.1
 */