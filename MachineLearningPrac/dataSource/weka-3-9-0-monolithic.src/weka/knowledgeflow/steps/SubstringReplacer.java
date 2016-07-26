/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.util.Arrays;
/*   4:    */ import java.util.List;
/*   5:    */ import java.util.Map;
/*   6:    */ import java.util.Map.Entry;
/*   7:    */ import weka.core.Instance;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.WekaException;
/*  10:    */ import weka.gui.ProgrammaticProperty;
/*  11:    */ import weka.gui.beans.SubstringReplacerRules;
/*  12:    */ import weka.knowledgeflow.Data;
/*  13:    */ import weka.knowledgeflow.ExecutionEnvironment;
/*  14:    */ import weka.knowledgeflow.StepManager;
/*  15:    */ import weka.knowledgeflow.StepManagerImpl;
/*  16:    */ 
/*  17:    */ @KFStep(name="SubstringReplacer", category="Tools", toolTipText="Replace substrings in String attribute values using either literal match-and-replace or regular expression matching. The attributes to apply the match and replace rules to can be selected via a range string (e.g. 1-5,6-last) or by a comma-separated list of attribute names (/first and /last can be used to indicate the first and last attribute respectively)", iconPath="weka/gui/knowledgeflow/icons/DefaultFilter.gif")
/*  18:    */ public class SubstringReplacer
/*  19:    */   extends BaseStep
/*  20:    */ {
/*  21:    */   private static final long serialVersionUID = -8786642000811852824L;
/*  22: 64 */   protected String m_matchReplaceDetails = "";
/*  23:    */   protected transient SubstringReplacerRules m_mr;
/*  24:    */   protected Data m_streamingData;
/*  25:    */   protected boolean m_isReset;
/*  26:    */   
/*  27:    */   @ProgrammaticProperty
/*  28:    */   public void setMatchReplaceDetails(String details)
/*  29:    */   {
/*  30: 82 */     this.m_matchReplaceDetails = details;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public String getMatchReplaceDetails()
/*  34:    */   {
/*  35: 91 */     return this.m_matchReplaceDetails;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void stepInit()
/*  39:    */     throws WekaException
/*  40:    */   {
/*  41:101 */     this.m_isReset = true;
/*  42:102 */     this.m_streamingData = new Data("instance");
/*  43:    */   }
/*  44:    */   
/*  45:    */   public List<String> getIncomingConnectionTypes()
/*  46:    */   {
/*  47:116 */     if (getStepManager().numIncomingConnections() == 0) {
/*  48:117 */       return Arrays.asList(new String[] { "instance" });
/*  49:    */     }
/*  50:120 */     return null;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public List<String> getOutgoingConnectionTypes()
/*  54:    */   {
/*  55:134 */     if (getStepManager().numIncomingConnections() > 0) {
/*  56:135 */       return Arrays.asList(new String[] { "instance" });
/*  57:    */     }
/*  58:137 */     return null;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void processIncoming(Data data)
/*  62:    */     throws WekaException
/*  63:    */   {
/*  64:148 */     Instance inst = (Instance)data.getPrimaryPayload();
/*  65:149 */     if (this.m_isReset)
/*  66:    */     {
/*  67:150 */       this.m_isReset = false;
/*  68:151 */       Instances structure = inst.dataset();
/*  69:152 */       this.m_mr = new SubstringReplacerRules(this.m_matchReplaceDetails, structure, ((StepManagerImpl)getStepManager()).stepStatusMessagePrefix(), getStepManager().getLog(), getStepManager().getExecutionEnvironment().getEnvironmentVariables());
/*  70:    */     }
/*  71:159 */     if (getStepManager().isStreamFinished(data))
/*  72:    */     {
/*  73:160 */       this.m_streamingData.clearPayload();
/*  74:161 */       getStepManager().throughputFinished(new Data[] { this.m_streamingData });
/*  75:    */     }
/*  76:163 */     else if (!isStopRequested())
/*  77:    */     {
/*  78:164 */       getStepManager().throughputUpdateStart();
/*  79:165 */       Instance outInst = this.m_mr.makeOutputInstance(inst);
/*  80:166 */       getStepManager().throughputUpdateEnd();
/*  81:167 */       this.m_streamingData.setPayloadElement("instance", outInst);
/*  82:168 */       getStepManager().outputData(new Data[] { this.m_streamingData });
/*  83:    */     }
/*  84:    */     else
/*  85:    */     {
/*  86:170 */       getStepManager().interrupted();
/*  87:    */     }
/*  88:    */   }
/*  89:    */   
/*  90:    */   public Instances outputStructureForConnectionType(String connectionName)
/*  91:    */     throws WekaException
/*  92:    */   {
/*  93:189 */     if (getStepManager().numIncomingConnections() > 0) {
/*  94:190 */       for (Map.Entry<String, List<StepManager>> e : getStepManager().getIncomingConnections().entrySet()) {
/*  95:192 */         if (((List)e.getValue()).size() > 0)
/*  96:    */         {
/*  97:193 */           StepManager incoming = (StepManager)((List)e.getValue()).get(0);
/*  98:194 */           String incomingConnType = (String)e.getKey();
/*  99:195 */           return getStepManager().getIncomingStructureFromStep(incoming, incomingConnType);
/* 100:    */         }
/* 101:    */       }
/* 102:    */     }
/* 103:201 */     return null;
/* 104:    */   }
/* 105:    */   
/* 106:    */   public String getCustomEditorForStep()
/* 107:    */   {
/* 108:214 */     return "weka.gui.knowledgeflow.steps.SubstringReplacerStepEditorDialog";
/* 109:    */   }
/* 110:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.SubstringReplacer
 * JD-Core Version:    0.7.0.1
 */