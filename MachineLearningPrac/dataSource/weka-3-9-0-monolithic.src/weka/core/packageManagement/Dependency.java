/*  1:   */ package weka.core.packageManagement;
/*  2:   */ 
/*  3:   */ public class Dependency
/*  4:   */ {
/*  5:   */   protected Package m_sourcePackage;
/*  6:   */   protected PackageConstraint m_targetPackage;
/*  7:   */   
/*  8:   */   public Dependency(Package source, PackageConstraint target)
/*  9:   */   {
/* 10:45 */     this.m_sourcePackage = source;
/* 11:46 */     this.m_targetPackage = target;
/* 12:   */   }
/* 13:   */   
/* 14:   */   public void setSource(Package source)
/* 15:   */   {
/* 16:55 */     this.m_sourcePackage = source;
/* 17:   */   }
/* 18:   */   
/* 19:   */   public Package getSource()
/* 20:   */   {
/* 21:64 */     return this.m_sourcePackage;
/* 22:   */   }
/* 23:   */   
/* 24:   */   public void setTarget(PackageConstraint target)
/* 25:   */   {
/* 26:73 */     this.m_targetPackage = target;
/* 27:   */   }
/* 28:   */   
/* 29:   */   public PackageConstraint getTarget()
/* 30:   */   {
/* 31:82 */     return this.m_targetPackage;
/* 32:   */   }
/* 33:   */   
/* 34:   */   public String toString()
/* 35:   */   {
/* 36:86 */     return this.m_sourcePackage.toString() + " --> " + this.m_targetPackage.toString();
/* 37:   */   }
/* 38:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.packageManagement.Dependency
 * JD-Core Version:    0.7.0.1
 */