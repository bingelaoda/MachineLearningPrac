/*   1:    */ package weka.experiment;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import javax.swing.DefaultListModel;
/*   6:    */ import weka.core.RevisionHandler;
/*   7:    */ import weka.core.RevisionUtils;
/*   8:    */ 
/*   9:    */ public class RemoteExperimentSubTask
/*  10:    */   implements Task, RevisionHandler
/*  11:    */ {
/*  12:    */   private static final long serialVersionUID = -1674092706571603720L;
/*  13: 42 */   private TaskStatusInfo m_result = new TaskStatusInfo();
/*  14:    */   private Experiment m_experiment;
/*  15:    */   
/*  16:    */   public RemoteExperimentSubTask()
/*  17:    */   {
/*  18: 48 */     this.m_result.setStatusMessage("Not running.");
/*  19: 49 */     this.m_result.setExecutionStatus(0);
/*  20:    */   }
/*  21:    */   
/*  22:    */   public void setExperiment(Experiment task)
/*  23:    */   {
/*  24: 58 */     this.m_experiment = task;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public Experiment getExperiment()
/*  28:    */   {
/*  29: 67 */     return this.m_experiment;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public void execute()
/*  33:    */   {
/*  34: 76 */     this.m_result = new TaskStatusInfo();
/*  35: 77 */     this.m_result.setStatusMessage("Running...");
/*  36: 78 */     String goodResult = "(sub)experiment completed successfully";
/*  37:    */     String subTaskType;
/*  38:    */     String subTaskType;
/*  39: 80 */     if (this.m_experiment.getRunLower() != this.m_experiment.getRunUpper()) {
/*  40: 81 */       subTaskType = "(dataset " + ((File)this.m_experiment.getDatasets().elementAt(0)).getName();
/*  41:    */     } else {
/*  42: 84 */       subTaskType = "(exp run # " + this.m_experiment.getRunLower();
/*  43:    */     }
/*  44:    */     try
/*  45:    */     {
/*  46: 87 */       System.err.println("Initializing " + subTaskType + ")...");
/*  47: 88 */       this.m_experiment.initialize();
/*  48: 89 */       System.err.println("Iterating " + subTaskType + ")...");
/*  49: 91 */       while (this.m_experiment.hasMoreIterations()) {
/*  50: 92 */         this.m_experiment.nextIteration();
/*  51:    */       }
/*  52: 94 */       System.err.println("Postprocessing " + subTaskType + ")...");
/*  53: 95 */       this.m_experiment.postProcess();
/*  54:    */     }
/*  55:    */     catch (Exception ex)
/*  56:    */     {
/*  57: 97 */       ex.printStackTrace();
/*  58: 98 */       String badResult = "(sub)experiment " + subTaskType + ") failed : " + ex.toString();
/*  59:    */       
/*  60:100 */       this.m_result.setExecutionStatus(2);
/*  61:    */       
/*  62:    */ 
/*  63:103 */       this.m_result.setStatusMessage(badResult);
/*  64:104 */       this.m_result.setTaskResult("Failed");
/*  65:    */       
/*  66:106 */       return;
/*  67:    */     }
/*  68:110 */     this.m_result.setExecutionStatus(3);
/*  69:111 */     this.m_result.setStatusMessage(goodResult + " " + subTaskType + ").");
/*  70:112 */     this.m_result.setTaskResult("No errors");
/*  71:    */   }
/*  72:    */   
/*  73:    */   public TaskStatusInfo getTaskStatus()
/*  74:    */   {
/*  75:118 */     return this.m_result;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public String getRevision()
/*  79:    */   {
/*  80:128 */     return RevisionUtils.extract("$Revision: 10204 $");
/*  81:    */   }
/*  82:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.RemoteExperimentSubTask
 * JD-Core Version:    0.7.0.1
 */