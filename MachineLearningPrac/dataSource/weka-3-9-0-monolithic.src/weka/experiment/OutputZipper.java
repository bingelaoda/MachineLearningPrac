/*   1:    */ package weka.experiment;
/*   2:    */ 
/*   3:    */ import java.io.DataOutputStream;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.FileOutputStream;
/*   6:    */ import java.io.PrintStream;
/*   7:    */ import java.util.zip.GZIPOutputStream;
/*   8:    */ import java.util.zip.ZipEntry;
/*   9:    */ import java.util.zip.ZipOutputStream;
/*  10:    */ import weka.core.RevisionHandler;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ 
/*  13:    */ public class OutputZipper
/*  14:    */   implements RevisionHandler
/*  15:    */ {
/*  16:    */   File m_destination;
/*  17: 50 */   DataOutputStream m_zipOut = null;
/*  18: 51 */   ZipOutputStream m_zs = null;
/*  19:    */   
/*  20:    */   public OutputZipper(File destination)
/*  21:    */     throws Exception
/*  22:    */   {
/*  23: 61 */     this.m_destination = destination;
/*  24: 65 */     if (!this.m_destination.isDirectory())
/*  25:    */     {
/*  26: 66 */       this.m_zs = new ZipOutputStream(new FileOutputStream(this.m_destination));
/*  27: 67 */       this.m_zipOut = new DataOutputStream(this.m_zs);
/*  28:    */     }
/*  29:    */   }
/*  30:    */   
/*  31:    */   public void zipit(String outString, String name)
/*  32:    */     throws Exception
/*  33:    */   {
/*  34: 83 */     if (this.m_zipOut == null)
/*  35:    */     {
/*  36: 84 */       File saveFile = new File(this.m_destination, name + ".gz");
/*  37: 85 */       DataOutputStream dout = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(saveFile)));
/*  38:    */       
/*  39:    */ 
/*  40:    */ 
/*  41: 89 */       dout.writeBytes(outString);
/*  42: 90 */       dout.close();
/*  43:    */     }
/*  44:    */     else
/*  45:    */     {
/*  46: 92 */       ZipEntry ze = new ZipEntry(name);
/*  47: 93 */       this.m_zs.putNextEntry(ze);
/*  48: 94 */       this.m_zipOut.writeBytes(outString);
/*  49: 95 */       this.m_zs.closeEntry();
/*  50:    */     }
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void finished()
/*  54:    */     throws Exception
/*  55:    */   {
/*  56:105 */     if (this.m_zipOut != null) {
/*  57:106 */       this.m_zipOut.close();
/*  58:    */     }
/*  59:    */   }
/*  60:    */   
/*  61:    */   public String getRevision()
/*  62:    */   {
/*  63:116 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  64:    */   }
/*  65:    */   
/*  66:    */   public static void main(String[] args)
/*  67:    */   {
/*  68:    */     try
/*  69:    */     {
/*  70:125 */       File testF = new File(new File(System.getProperty("user.dir")), "testOut.zip");
/*  71:    */       
/*  72:127 */       OutputZipper oz = new OutputZipper(testF);
/*  73:    */       
/*  74:    */ 
/*  75:    */ 
/*  76:131 */       oz.zipit("Here is some test text to be zipped", "testzip");
/*  77:132 */       oz.zipit("Here is a second entry to be zipped", "testzip2");
/*  78:133 */       oz.finished();
/*  79:    */     }
/*  80:    */     catch (Exception ex)
/*  81:    */     {
/*  82:135 */       ex.printStackTrace();
/*  83:136 */       System.err.println(ex.getMessage());
/*  84:    */     }
/*  85:    */   }
/*  86:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.OutputZipper
 * JD-Core Version:    0.7.0.1
 */