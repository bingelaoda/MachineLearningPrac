/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import weka.core.Utils;
/*   5:    */ import weka.gui.ProgrammaticProperty;
/*   6:    */ 
/*   7:    */ public abstract class WekaAlgorithmWrapper
/*   8:    */   extends BaseStep
/*   9:    */   implements Serializable
/*  10:    */ {
/*  11:    */   private static final long serialVersionUID = -1013404060247467085L;
/*  12:    */   protected String m_iconPath;
/*  13:    */   protected String m_defaultPackageIconPath;
/*  14:    */   protected String m_defaultIconPath;
/*  15:    */   protected Object m_wrappedAlgorithm;
/*  16:    */   
/*  17:    */   public String globalInfo()
/*  18:    */   {
/*  19: 67 */     if (getWrappedAlgorithm() != null) {
/*  20: 68 */       return Utils.getGlobalInfo(getWrappedAlgorithm(), false);
/*  21:    */     }
/*  22: 70 */     return super.globalInfo();
/*  23:    */   }
/*  24:    */   
/*  25:    */   @NotPersistable
/*  26:    */   @ProgrammaticProperty
/*  27:    */   public Object getWrappedAlgorithm()
/*  28:    */   {
/*  29: 81 */     return this.m_wrappedAlgorithm;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public void setWrappedAlgorithm(Object algo)
/*  33:    */   {
/*  34: 90 */     this.m_wrappedAlgorithm = algo;
/*  35:    */     
/*  36: 92 */     String className = algo.getClass().getCanonicalName();
/*  37: 93 */     String name = className.substring(className.lastIndexOf(".") + 1);
/*  38: 94 */     String packageName = className.substring(0, className.lastIndexOf("."));
/*  39: 95 */     setName(name);
/*  40:    */     
/*  41: 97 */     this.m_defaultPackageIconPath = ("weka/gui/knowledgeflow/icons/" + packageName + ".gif");
/*  42: 98 */     this.m_iconPath = ("weka/gui/knowledgeflow/icons/" + name + ".gif");
/*  43:    */   }
/*  44:    */   
/*  45:    */   public String getIconPath()
/*  46:    */   {
/*  47:107 */     return this.m_iconPath;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public String getDefaultPackageLevelIconPath()
/*  51:    */   {
/*  52:117 */     return this.m_defaultPackageIconPath;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public String getDefaultIconPath()
/*  56:    */   {
/*  57:127 */     return this.m_defaultIconPath;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public abstract Class getWrappedAlgorithmClass();
/*  61:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.WekaAlgorithmWrapper
 * JD-Core Version:    0.7.0.1
 */