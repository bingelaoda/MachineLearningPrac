/*   1:    */ package weka.core.converters;
/*   2:    */ 
/*   3:    */ import java.io.BufferedInputStream;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.InputStream;
/*   6:    */ import java.io.ObjectInputStream;
/*   7:    */ import weka.core.Instance;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.RevisionUtils;
/*  10:    */ 
/*  11:    */ public class SerializedInstancesLoader
/*  12:    */   extends AbstractFileLoader
/*  13:    */   implements BatchConverter, IncrementalConverter
/*  14:    */ {
/*  15:    */   static final long serialVersionUID = 2391085836269030715L;
/*  16: 51 */   public static String FILE_EXTENSION = ".bsi";
/*  17: 55 */   protected Instances m_Dataset = null;
/*  18: 58 */   protected int m_IncrementalIndex = 0;
/*  19:    */   
/*  20:    */   public String globalInfo()
/*  21:    */   {
/*  22: 67 */     return "Reads a source that contains serialized Instances.";
/*  23:    */   }
/*  24:    */   
/*  25:    */   public void reset()
/*  26:    */   {
/*  27: 73 */     this.m_Dataset = null;
/*  28: 74 */     this.m_IncrementalIndex = 0;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public String getFileExtension()
/*  32:    */   {
/*  33: 83 */     return FILE_EXTENSION;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public String[] getFileExtensions()
/*  37:    */   {
/*  38: 92 */     return new String[] { getFileExtension() };
/*  39:    */   }
/*  40:    */   
/*  41:    */   public String getFileDescription()
/*  42:    */   {
/*  43:101 */     return "Binary serialized instances";
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void setSource(InputStream in)
/*  47:    */     throws IOException
/*  48:    */   {
/*  49:113 */     ObjectInputStream oi = new ObjectInputStream(new BufferedInputStream(in));
/*  50:    */     try
/*  51:    */     {
/*  52:115 */       this.m_Dataset = ((Instances)oi.readObject());
/*  53:    */     }
/*  54:    */     catch (ClassNotFoundException ex)
/*  55:    */     {
/*  56:117 */       throw new IOException("Could not deserialize instances from this source.");
/*  57:    */     }
/*  58:121 */     oi.close();
/*  59:    */   }
/*  60:    */   
/*  61:    */   public Instances getStructure()
/*  62:    */     throws IOException
/*  63:    */   {
/*  64:133 */     if (this.m_Dataset == null) {
/*  65:134 */       throw new IOException("No source has been specified");
/*  66:    */     }
/*  67:139 */     return new Instances(this.m_Dataset, 0);
/*  68:    */   }
/*  69:    */   
/*  70:    */   public Instances getDataSet()
/*  71:    */     throws IOException
/*  72:    */   {
/*  73:152 */     if (this.m_Dataset == null) {
/*  74:153 */       throw new IOException("No source has been specified");
/*  75:    */     }
/*  76:156 */     return this.m_Dataset;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public Instance getNextInstance(Instances structure)
/*  80:    */     throws IOException
/*  81:    */   {
/*  82:173 */     if (this.m_Dataset == null) {
/*  83:174 */       throw new IOException("No source has been specified");
/*  84:    */     }
/*  85:179 */     if (this.m_IncrementalIndex == this.m_Dataset.numInstances()) {
/*  86:180 */       return null;
/*  87:    */     }
/*  88:183 */     return this.m_Dataset.instance(this.m_IncrementalIndex++);
/*  89:    */   }
/*  90:    */   
/*  91:    */   public String getRevision()
/*  92:    */   {
/*  93:192 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  94:    */   }
/*  95:    */   
/*  96:    */   public static void main(String[] args)
/*  97:    */   {
/*  98:201 */     runFileLoader(new SerializedInstancesLoader(), args);
/*  99:    */   }
/* 100:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.SerializedInstancesLoader
 * JD-Core Version:    0.7.0.1
 */