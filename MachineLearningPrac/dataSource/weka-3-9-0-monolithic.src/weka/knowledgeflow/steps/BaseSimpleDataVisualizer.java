/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.text.SimpleDateFormat;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Arrays;
/*   6:    */ import java.util.Date;
/*   7:    */ import java.util.List;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.WekaException;
/*  10:    */ import weka.knowledgeflow.Data;
/*  11:    */ import weka.knowledgeflow.StepManager;
/*  12:    */ 
/*  13:    */ public abstract class BaseSimpleDataVisualizer
/*  14:    */   extends BaseStep
/*  15:    */   implements DataCollector
/*  16:    */ {
/*  17:    */   private static final long serialVersionUID = 4955068920302509451L;
/*  18: 48 */   protected List<Data> m_data = new ArrayList();
/*  19:    */   
/*  20:    */   public void stepInit()
/*  21:    */     throws WekaException
/*  22:    */   {}
/*  23:    */   
/*  24:    */   public synchronized void processIncoming(Data data)
/*  25:    */     throws WekaException
/*  26:    */   {
/*  27: 63 */     processIncoming(data, true);
/*  28:    */   }
/*  29:    */   
/*  30:    */   protected synchronized void processIncoming(Data data, boolean notifyFinished)
/*  31:    */   {
/*  32: 75 */     getStepManager().processing();
/*  33: 76 */     Instances toPlot = (Instances)data.getPrimaryPayload();
/*  34: 77 */     String name = new SimpleDateFormat("HH:mm:ss.SSS - ").format(new Date());
/*  35: 78 */     String title = name + toPlot.relationName();
/*  36: 79 */     int setNum = ((Integer)data.getPayloadElement("aux_set_num", Integer.valueOf(1))).intValue();
/*  37: 80 */     int maxSetNum = ((Integer)data.getPayloadElement("aux_max_set_num", Integer.valueOf(1))).intValue();
/*  38:    */     
/*  39:    */ 
/*  40: 83 */     title = title + " set " + setNum + " of " + maxSetNum;
/*  41: 84 */     getStepManager().logDetailed("Processing " + title);
/*  42: 85 */     data.setPayloadElement("aux_textTitle", title);
/*  43: 86 */     this.m_data.add(data);
/*  44: 88 */     if (notifyFinished) {
/*  45: 89 */       getStepManager().finished();
/*  46:    */     }
/*  47:    */   }
/*  48:    */   
/*  49:    */   public List<String> getIncomingConnectionTypes()
/*  50:    */   {
/*  51:101 */     return Arrays.asList(new String[] { "dataSet", "trainingSet", "testSet" });
/*  52:    */   }
/*  53:    */   
/*  54:    */   public List<String> getOutgoingConnectionTypes()
/*  55:    */   {
/*  56:114 */     return null;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public List<Data> getDatasets()
/*  60:    */   {
/*  61:123 */     return this.m_data;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public Object retrieveData()
/*  65:    */   {
/*  66:128 */     return getDatasets();
/*  67:    */   }
/*  68:    */   
/*  69:    */   public void restoreData(Object data)
/*  70:    */     throws WekaException
/*  71:    */   {
/*  72:134 */     if (!(data instanceof List)) {
/*  73:135 */       throw new WekaException("Was expecting an instance of a List");
/*  74:    */     }
/*  75:138 */     this.m_data = ((List)data);
/*  76:    */   }
/*  77:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.BaseSimpleDataVisualizer
 * JD-Core Version:    0.7.0.1
 */