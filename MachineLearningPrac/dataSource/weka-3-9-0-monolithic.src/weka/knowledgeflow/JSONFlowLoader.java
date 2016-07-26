/*   1:    */ package weka.knowledgeflow;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.io.Reader;
/*   6:    */ import weka.core.WekaException;
/*   7:    */ import weka.gui.Logger;
/*   8:    */ 
/*   9:    */ public class JSONFlowLoader
/*  10:    */   implements FlowLoader
/*  11:    */ {
/*  12:    */   protected Logger m_log;
/*  13:    */   public static final String EXTENSION = "kf";
/*  14:    */   
/*  15:    */   public void setLog(Logger log)
/*  16:    */   {
/*  17: 52 */     this.m_log = log;
/*  18:    */   }
/*  19:    */   
/*  20:    */   public String getFlowFileExtension()
/*  21:    */   {
/*  22: 62 */     return "kf";
/*  23:    */   }
/*  24:    */   
/*  25:    */   public String getFlowFileExtensionDescription()
/*  26:    */   {
/*  27: 72 */     return "JSON Knowledge Flow configuration files";
/*  28:    */   }
/*  29:    */   
/*  30:    */   public Flow readFlow(File flowFile)
/*  31:    */     throws WekaException
/*  32:    */   {
/*  33: 84 */     return JSONFlowUtils.readFlow(flowFile);
/*  34:    */   }
/*  35:    */   
/*  36:    */   public Flow readFlow(InputStream is)
/*  37:    */     throws WekaException
/*  38:    */   {
/*  39: 96 */     return JSONFlowUtils.readFlow(is);
/*  40:    */   }
/*  41:    */   
/*  42:    */   public Flow readFlow(Reader r)
/*  43:    */     throws WekaException
/*  44:    */   {
/*  45:108 */     return JSONFlowUtils.readFlow(r);
/*  46:    */   }
/*  47:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.JSONFlowLoader
 * JD-Core Version:    0.7.0.1
 */