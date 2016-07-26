/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.text.SimpleDateFormat;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Arrays;
/*   6:    */ import java.util.Date;
/*   7:    */ import java.util.LinkedHashMap;
/*   8:    */ import java.util.List;
/*   9:    */ import java.util.Map;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.WekaException;
/*  12:    */ import weka.knowledgeflow.Data;
/*  13:    */ import weka.knowledgeflow.StepManager;
/*  14:    */ 
/*  15:    */ @KFStep(name="TextViewer", category="Visualization", toolTipText="View textual output", iconPath="weka/gui/knowledgeflow/icons/DefaultText.gif")
/*  16:    */ public class TextViewer
/*  17:    */   extends BaseStep
/*  18:    */   implements DataCollector
/*  19:    */ {
/*  20:    */   private static final long serialVersionUID = 8602416209256135064L;
/*  21:    */   protected Map<String, String> m_results;
/*  22:    */   protected transient TextNotificationListener m_viewerListener;
/*  23:    */   
/*  24:    */   public TextViewer()
/*  25:    */   {
/*  26: 52 */     this.m_results = new LinkedHashMap();
/*  27:    */   }
/*  28:    */   
/*  29:    */   public void stepInit() {}
/*  30:    */   
/*  31:    */   public List<String> getIncomingConnectionTypes()
/*  32:    */   {
/*  33: 79 */     return Arrays.asList(new String[] { "text", "dataSet", "trainingSet", "testSet" });
/*  34:    */   }
/*  35:    */   
/*  36:    */   public List<String> getOutgoingConnectionTypes()
/*  37:    */   {
/*  38: 94 */     return getStepManager().numIncomingConnections() > 0 ? Arrays.asList(new String[] { "text" }) : new ArrayList();
/*  39:    */   }
/*  40:    */   
/*  41:    */   public synchronized void processIncoming(Data data)
/*  42:    */     throws WekaException
/*  43:    */   {
/*  44:106 */     getStepManager().processing();
/*  45:107 */     String title = (String)data.getPayloadElement("aux_textTitle");
/*  46:109 */     if ((title == null) && ((data.getConnectionName().equals("dataSet")) || (data.getConnectionName().equals("trainingSet")) || (data.getConnectionName().equals("testSet")))) {
/*  47:113 */       title = ((Instances)data.getPrimaryPayload()).relationName();
/*  48:    */     }
/*  49:116 */     if (title != null) {
/*  50:117 */       getStepManager().logDetailed("Storing result: " + title);
/*  51:    */     }
/*  52:120 */     String body = data.getPayloadElement(data.getConnectionName()).toString();
/*  53:121 */     Integer setNum = (Integer)data.getPayloadElement("aux_set_num");
/*  54:123 */     if ((title != null) && (body != null))
/*  55:    */     {
/*  56:124 */       String name = new SimpleDateFormat("HH:mm:ss.SSS - ").format(new Date());
/*  57:    */       
/*  58:126 */       this.m_results.put(name + title + (setNum != null ? " (" + setNum + ")" : ""), body);
/*  59:128 */       if (this.m_viewerListener != null) {
/*  60:129 */         this.m_viewerListener.acceptTextResult(name + title + (setNum != null ? " (" + setNum + ")" : ""), body);
/*  61:    */       }
/*  62:    */     }
/*  63:135 */     getStepManager().outputData(new Data[] { data });
/*  64:136 */     getStepManager().finished();
/*  65:    */   }
/*  66:    */   
/*  67:    */   public Map<String, String> getInteractiveViewers()
/*  68:    */   {
/*  69:161 */     Map<String, String> views = new LinkedHashMap();
/*  70:163 */     if (this.m_viewerListener == null) {
/*  71:164 */       views.put("Show results", "weka.gui.knowledgeflow.steps.TextViewerInteractiveView");
/*  72:    */     }
/*  73:168 */     return views;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public synchronized Map<String, String> getResults()
/*  77:    */   {
/*  78:177 */     return this.m_results;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public Object retrieveData()
/*  82:    */   {
/*  83:187 */     return getResults();
/*  84:    */   }
/*  85:    */   
/*  86:    */   public void restoreData(Object data)
/*  87:    */   {
/*  88:198 */     if (!(data instanceof Map)) {
/*  89:199 */       throw new IllegalArgumentException("Argument must be a Map");
/*  90:    */     }
/*  91:201 */     this.m_results = ((Map)data);
/*  92:    */   }
/*  93:    */   
/*  94:    */   public void setTextNotificationListener(TextNotificationListener l)
/*  95:    */   {
/*  96:210 */     this.m_viewerListener = l;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public void removeTextNotificationListener(TextNotificationListener l)
/* 100:    */   {
/* 101:219 */     if (l == this.m_viewerListener) {
/* 102:220 */       this.m_viewerListener = null;
/* 103:    */     }
/* 104:    */   }
/* 105:    */   
/* 106:    */   public static abstract interface TextNotificationListener
/* 107:    */   {
/* 108:    */     public abstract void acceptTextResult(String paramString1, String paramString2);
/* 109:    */   }
/* 110:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.TextViewer
 * JD-Core Version:    0.7.0.1
 */