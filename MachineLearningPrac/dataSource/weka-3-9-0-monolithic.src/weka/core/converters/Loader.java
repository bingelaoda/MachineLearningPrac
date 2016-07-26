/*  1:   */ package weka.core.converters;
/*  2:   */ 
/*  3:   */ import java.io.File;
/*  4:   */ import java.io.IOException;
/*  5:   */ import java.io.InputStream;
/*  6:   */ import java.io.Serializable;
/*  7:   */ import weka.core.Instance;
/*  8:   */ import weka.core.Instances;
/*  9:   */ import weka.core.RevisionHandler;
/* 10:   */ 
/* 11:   */ public abstract interface Loader
/* 12:   */   extends Serializable, RevisionHandler
/* 13:   */ {
/* 14:   */   public static final int NONE = 0;
/* 15:   */   public static final int BATCH = 1;
/* 16:   */   public static final int INCREMENTAL = 2;
/* 17:   */   
/* 18:   */   public abstract void setRetrieval(int paramInt);
/* 19:   */   
/* 20:   */   public abstract void reset()
/* 21:   */     throws Exception;
/* 22:   */   
/* 23:   */   public abstract void setSource(File paramFile)
/* 24:   */     throws IOException;
/* 25:   */   
/* 26:   */   public abstract void setSource(InputStream paramInputStream)
/* 27:   */     throws IOException;
/* 28:   */   
/* 29:   */   public abstract Instances getStructure()
/* 30:   */     throws IOException;
/* 31:   */   
/* 32:   */   public abstract Instances getDataSet()
/* 33:   */     throws IOException;
/* 34:   */   
/* 35:   */   public abstract Instance getNextInstance(Instances paramInstances)
/* 36:   */     throws IOException;
/* 37:   */   
/* 38:   */   public static class StructureNotReadyException
/* 39:   */     extends IOException
/* 40:   */   {
/* 41:   */     private static final long serialVersionUID = 1938493033987645828L;
/* 42:   */     
/* 43:   */     public StructureNotReadyException(String message)
/* 44:   */     {
/* 45:56 */       super();
/* 46:   */     }
/* 47:   */   }
/* 48:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.Loader
 * JD-Core Version:    0.7.0.1
 */