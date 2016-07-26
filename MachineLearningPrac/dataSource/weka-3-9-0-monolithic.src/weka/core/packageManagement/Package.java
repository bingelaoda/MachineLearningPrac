/*   1:    */ package weka.core.packageManagement;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.net.URL;
/*   5:    */ import java.util.List;
/*   6:    */ import java.util.Map;
/*   7:    */ 
/*   8:    */ public abstract class Package
/*   9:    */   implements Cloneable, Serializable
/*  10:    */ {
/*  11:    */   private static final long serialVersionUID = -8193697646938632764L;
/*  12:    */   protected Map<?, ?> m_packageMetaData;
/*  13:    */   
/*  14:    */   public void setPackageMetaData(Map<?, ?> metaData)
/*  15:    */   {
/*  16: 53 */     this.m_packageMetaData = metaData;
/*  17:    */   }
/*  18:    */   
/*  19:    */   public Map<?, ?> getPackageMetaData()
/*  20:    */   {
/*  21: 62 */     return this.m_packageMetaData;
/*  22:    */   }
/*  23:    */   
/*  24:    */   public abstract String getName();
/*  25:    */   
/*  26:    */   public abstract URL getPackageURL()
/*  27:    */     throws Exception;
/*  28:    */   
/*  29:    */   public boolean equals(Package toCompare)
/*  30:    */   {
/*  31: 91 */     return this.m_packageMetaData.equals(toCompare.getPackageMetaData());
/*  32:    */   }
/*  33:    */   
/*  34:    */   public abstract List<Dependency> getDependencies()
/*  35:    */     throws Exception;
/*  36:    */   
/*  37:    */   public abstract boolean isInstalled();
/*  38:    */   
/*  39:    */   public abstract void install()
/*  40:    */     throws Exception;
/*  41:    */   
/*  42:    */   public abstract boolean isCompatibleBaseSystem()
/*  43:    */     throws Exception;
/*  44:    */   
/*  45:    */   public abstract List<Dependency> getBaseSystemDependency()
/*  46:    */     throws Exception;
/*  47:    */   
/*  48:    */   public abstract List<Dependency> getMissingDependencies()
/*  49:    */     throws Exception;
/*  50:    */   
/*  51:    */   public abstract List<Dependency> getMissingDependencies(List<Package> paramList)
/*  52:    */     throws Exception;
/*  53:    */   
/*  54:    */   public abstract List<Dependency> getIncompatibleDependencies()
/*  55:    */     throws Exception;
/*  56:    */   
/*  57:    */   public abstract List<Dependency> getIncompatibleDependencies(List<Package> paramList)
/*  58:    */     throws Exception;
/*  59:    */   
/*  60:    */   public Object getPackageMetaDataElement(Object key)
/*  61:    */   {
/*  62:199 */     if (this.m_packageMetaData == null) {
/*  63:200 */       return null;
/*  64:    */     }
/*  65:203 */     return this.m_packageMetaData.get(key);
/*  66:    */   }
/*  67:    */   
/*  68:    */   public abstract void setPackageMetaDataElement(Object paramObject1, Object paramObject2)
/*  69:    */     throws Exception;
/*  70:    */   
/*  71:    */   public abstract Object clone();
/*  72:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.packageManagement.Package
 * JD-Core Version:    0.7.0.1
 */