/*  1:   */ package weka.core.packageManagement;
/*  2:   */ 
/*  3:   */ public abstract class PackageConstraint
/*  4:   */ {
/*  5:   */   protected Package m_thePackage;
/*  6:   */   
/*  7:   */   public void setPackage(Package p)
/*  8:   */   {
/*  9:42 */     this.m_thePackage = p;
/* 10:   */   }
/* 11:   */   
/* 12:   */   public Package getPackage()
/* 13:   */   {
/* 14:51 */     return this.m_thePackage;
/* 15:   */   }
/* 16:   */   
/* 17:   */   public abstract boolean checkConstraint(Package paramPackage)
/* 18:   */     throws Exception;
/* 19:   */   
/* 20:   */   public abstract PackageConstraint checkConstraint(PackageConstraint paramPackageConstraint)
/* 21:   */     throws Exception;
/* 22:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.packageManagement.PackageConstraint
 * JD-Core Version:    0.7.0.1
 */