/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import weka.clusterers.ClusterEvaluation;
/*   6:    */ import weka.clusterers.Clusterer;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.OptionHandler;
/*  10:    */ import weka.core.Utils;
/*  11:    */ import weka.core.WekaException;
/*  12:    */ import weka.knowledgeflow.Data;
/*  13:    */ import weka.knowledgeflow.StepManager;
/*  14:    */ 
/*  15:    */ @KFStep(name="ClustererPerformanceEvaluator", category="Evaluation", toolTipText="Evaluates batch clusterers", iconPath="weka/gui/knowledgeflow/icons/ClustererPerformanceEvaluator.gif")
/*  16:    */ public class ClustererPerformanceEvaluator
/*  17:    */   extends BaseStep
/*  18:    */ {
/*  19:    */   private static final long serialVersionUID = -6337375482954345717L;
/*  20:    */   
/*  21:    */   public List<String> getIncomingConnectionTypes()
/*  22:    */   {
/*  23: 60 */     List<String> result = new ArrayList();
/*  24: 62 */     if (getStepManager().numIncomingConnectionsOfType("batchClusterer") == 0) {
/*  25: 64 */       result.add("batchClusterer");
/*  26:    */     }
/*  27: 66 */     return result;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public List<String> getOutgoingConnectionTypes()
/*  31:    */   {
/*  32: 80 */     List<String> result = new ArrayList();
/*  33: 81 */     if (getStepManager().numIncomingConnections() > 0) {
/*  34: 82 */       result.add("text");
/*  35:    */     }
/*  36: 86 */     return result;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void stepInit() {}
/*  40:    */   
/*  41:    */   public void processIncoming(Data data)
/*  42:    */     throws WekaException
/*  43:    */   {
/*  44:107 */     Clusterer clusterer = (Clusterer)data.getPayloadElement("batchClusterer");
/*  45:    */     
/*  46:109 */     Instances trainData = (Instances)data.getPayloadElement("aux_trainingSet");
/*  47:    */     
/*  48:111 */     Instances testData = (Instances)data.getPayloadElement("aux_testsSet");
/*  49:    */     
/*  50:113 */     Integer setNum = (Integer)data.getPayloadElement("aux_set_num");
/*  51:    */     
/*  52:115 */     Integer maxSetNum = (Integer)data.getPayloadElement("aux_max_set_num");
/*  53:118 */     if (setNum.intValue() == 1) {
/*  54:119 */       getStepManager().processing();
/*  55:    */     }
/*  56:122 */     ClusterEvaluation eval = new ClusterEvaluation();
/*  57:123 */     eval.setClusterer(clusterer);
/*  58:    */     
/*  59:    */ 
/*  60:    */ 
/*  61:127 */     String clusterSpec = makeClustererSpec(clusterer);
/*  62:128 */     String clusterClass = clusterer.getClass().getCanonicalName();
/*  63:129 */     clusterClass = clusterClass.substring(clusterClass.lastIndexOf('.') + 1, clusterClass.length());
/*  64:131 */     if ((trainData != null) && (!isStopRequested()))
/*  65:    */     {
/*  66:132 */       getStepManager().statusMessage("Evaluating (training set " + setNum + " of " + maxSetNum + ") " + clusterSpec);
/*  67:    */       try
/*  68:    */       {
/*  69:136 */         eval.evaluateClusterer(trainData);
/*  70:    */       }
/*  71:    */       catch (Exception ex)
/*  72:    */       {
/*  73:138 */         throw new WekaException(ex);
/*  74:    */       }
/*  75:141 */       if (!isStopRequested())
/*  76:    */       {
/*  77:142 */         String resultT = "=== Evaluation result for training instances ===\n\nScheme: " + clusterSpec + "\n" + "Relation: " + trainData.relationName() + "\n\n" + eval.clusterResultsToString();
/*  78:146 */         if ((trainData.classIndex() >= 0) && (trainData.classAttribute().isNumeric())) {
/*  79:148 */           resultT = resultT + "\n\nNo class-based evaluation possible. Class attribute has to be nominal.";
/*  80:    */         }
/*  81:152 */         Data text = new Data("text", resultT);
/*  82:153 */         text.setPayloadElement("aux_textTitle", clusterClass + " train (" + setNum + " of " + maxSetNum + ")");
/*  83:    */         
/*  84:155 */         getStepManager().outputData(new Data[] { text });
/*  85:    */       }
/*  86:    */     }
/*  87:159 */     if ((testData != null) && (!isStopRequested()))
/*  88:    */     {
/*  89:160 */       getStepManager().statusMessage("Evaluating (test set " + setNum + " of " + maxSetNum + ") " + clusterSpec);
/*  90:    */       try
/*  91:    */       {
/*  92:164 */         eval.evaluateClusterer(testData);
/*  93:    */       }
/*  94:    */       catch (Exception ex)
/*  95:    */       {
/*  96:166 */         throw new WekaException(ex);
/*  97:    */       }
/*  98:169 */       if (!isStopRequested())
/*  99:    */       {
/* 100:170 */         String resultT = "=== Evaluation result for test instances ===\n\nScheme: " + clusterSpec + "\n" + "Relation: " + testData.relationName() + "\n\n" + eval.clusterResultsToString();
/* 101:173 */         if ((testData.classIndex() >= 0) && (testData.classAttribute().isNumeric())) {
/* 102:175 */           resultT = resultT + "\n\nNo class-based evaluation possible. Class attribute has to be nominal.";
/* 103:    */         }
/* 104:179 */         Data text = new Data("text", resultT);
/* 105:180 */         text.setPayloadElement("aux_textTitle", clusterClass + " test (" + setNum + " of " + maxSetNum + ")");
/* 106:    */         
/* 107:182 */         getStepManager().outputData(new Data[] { text });
/* 108:    */       }
/* 109:    */     }
/* 110:186 */     if (isStopRequested()) {
/* 111:187 */       getStepManager().interrupted();
/* 112:188 */     } else if (setNum.intValue() == maxSetNum.intValue()) {
/* 113:189 */       getStepManager().finished();
/* 114:    */     }
/* 115:    */   }
/* 116:    */   
/* 117:    */   protected String makeClustererSpec(Clusterer clusterer)
/* 118:    */   {
/* 119:194 */     String clusterSpec = clusterer.getClass().getCanonicalName();
/* 120:195 */     clusterSpec = clusterSpec.substring(clusterSpec.lastIndexOf('.') + 1, clusterSpec.length());
/* 121:    */     
/* 122:    */ 
/* 123:198 */     String opts = " ";
/* 124:199 */     if ((clusterer instanceof OptionHandler)) {
/* 125:200 */       opts = Utils.joinOptions(((OptionHandler)clusterer).getOptions());
/* 126:    */     }
/* 127:203 */     return clusterSpec + opts;
/* 128:    */   }
/* 129:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.ClustererPerformanceEvaluator
 * JD-Core Version:    0.7.0.1
 */