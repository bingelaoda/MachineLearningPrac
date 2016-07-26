/*  1:   */ package weka.core.converters;
/*  2:   */ 
/*  3:   */ import java.io.File;
/*  4:   */ import java.io.IOException;
/*  5:   */ import java.io.InputStream;
/*  6:   */ import weka.core.Instance;
/*  7:   */ import weka.core.Instances;
/*  8:   */ 
/*  9:   */ public abstract class AbstractLoader
/* 10:   */   implements Loader
/* 11:   */ {
/* 12:   */   private static final long serialVersionUID = 2425432084900694551L;
/* 13:   */   protected int m_retrieval;
/* 14:   */   
/* 15:   */   public void setRetrieval(int mode)
/* 16:   */   {
/* 17:54 */     this.m_retrieval = mode;
/* 18:   */   }
/* 19:   */   
/* 20:   */   protected int getRetrieval()
/* 21:   */   {
/* 22:64 */     return this.m_retrieval;
/* 23:   */   }
/* 24:   */   
/* 25:   */   public void setSource(File file)
/* 26:   */     throws IOException
/* 27:   */   {
/* 28:76 */     throw new IOException("Setting File as source not supported");
/* 29:   */   }
/* 30:   */   
/* 31:   */   public void reset()
/* 32:   */     throws Exception
/* 33:   */   {
/* 34:86 */     this.m_retrieval = 0;
/* 35:   */   }
/* 36:   */   
/* 37:   */   public void setSource(InputStream input)
/* 38:   */     throws IOException
/* 39:   */   {
/* 40:98 */     throw new IOException("Setting InputStream as source not supported");
/* 41:   */   }
/* 42:   */   
/* 43:   */   public abstract Instances getStructure()
/* 44:   */     throws IOException;
/* 45:   */   
/* 46:   */   public abstract Instances getDataSet()
/* 47:   */     throws IOException;
/* 48:   */   
/* 49:   */   public abstract Instance getNextInstance(Instances paramInstances)
/* 50:   */     throws IOException;
/* 51:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.AbstractLoader
 * JD-Core Version:    0.7.0.1
 */