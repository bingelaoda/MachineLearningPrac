/*   1:    */ package weka.attributeSelection;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import weka.core.Capabilities;
/*   6:    */ import weka.core.CapabilitiesHandler;
/*   7:    */ import weka.core.CapabilitiesIgnorer;
/*   8:    */ import weka.core.CommandlineRunnable;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.RevisionHandler;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ import weka.core.SerializedObject;
/*  13:    */ import weka.core.Utils;
/*  14:    */ 
/*  15:    */ public abstract class ASEvaluation
/*  16:    */   implements Serializable, CapabilitiesHandler, CapabilitiesIgnorer, RevisionHandler, CommandlineRunnable
/*  17:    */ {
/*  18:    */   private static final long serialVersionUID = 2091705669885950849L;
/*  19: 49 */   protected boolean m_DoNotCheckCapabilities = false;
/*  20:    */   
/*  21:    */   public String doNotCheckCapabilitiesTipText()
/*  22:    */   {
/*  23: 58 */     return "If set, evaluator capabilities are not checked before evaluator is built (Use with caution to reduce runtime).";
/*  24:    */   }
/*  25:    */   
/*  26:    */   public void setDoNotCheckCapabilities(boolean doNotCheckCapabilities)
/*  27:    */   {
/*  28: 70 */     this.m_DoNotCheckCapabilities = doNotCheckCapabilities;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public boolean getDoNotCheckCapabilities()
/*  32:    */   {
/*  33: 81 */     return this.m_DoNotCheckCapabilities;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public abstract void buildEvaluator(Instances paramInstances)
/*  37:    */     throws Exception;
/*  38:    */   
/*  39:    */   public int[] postProcess(int[] attributeSet)
/*  40:    */     throws Exception
/*  41:    */   {
/*  42:107 */     return attributeSet;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public static ASEvaluation forName(String evaluatorName, String[] options)
/*  46:    */     throws Exception
/*  47:    */   {
/*  48:125 */     return (ASEvaluation)Utils.forName(ASEvaluation.class, evaluatorName, options);
/*  49:    */   }
/*  50:    */   
/*  51:    */   public static ASEvaluation[] makeCopies(ASEvaluation model, int num)
/*  52:    */     throws Exception
/*  53:    */   {
/*  54:142 */     if (model == null) {
/*  55:143 */       throw new Exception("No model evaluator set");
/*  56:    */     }
/*  57:145 */     ASEvaluation[] evaluators = new ASEvaluation[num];
/*  58:146 */     SerializedObject so = new SerializedObject(model);
/*  59:147 */     for (int i = 0; i < evaluators.length; i++) {
/*  60:148 */       evaluators[i] = ((ASEvaluation)so.getObject());
/*  61:    */     }
/*  62:150 */     return evaluators;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public Capabilities getCapabilities()
/*  66:    */   {
/*  67:161 */     Capabilities result = new Capabilities(this);
/*  68:162 */     result.enableAll();
/*  69:    */     
/*  70:164 */     return result;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public String getRevision()
/*  74:    */   {
/*  75:174 */     return RevisionUtils.extract("$Revision: 12201 $");
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void clean() {}
/*  79:    */   
/*  80:    */   public static void runEvaluator(ASEvaluation evaluator, String[] options)
/*  81:    */   {
/*  82:    */     try
/*  83:    */     {
/*  84:194 */       evaluator.preExecution();
/*  85:195 */       System.out.println(AttributeSelection.SelectAttributes(evaluator, options));
/*  86:    */     }
/*  87:    */     catch (Exception e)
/*  88:    */     {
/*  89:198 */       String msg = e.toString().toLowerCase();
/*  90:199 */       if ((msg.indexOf("help requested") == -1) && (msg.indexOf("no training file given") == -1)) {
/*  91:201 */         e.printStackTrace();
/*  92:    */       }
/*  93:203 */       System.err.println(e.getMessage());
/*  94:    */     }
/*  95:    */     try
/*  96:    */     {
/*  97:207 */       evaluator.postExecution();
/*  98:    */     }
/*  99:    */     catch (Exception ex)
/* 100:    */     {
/* 101:209 */       ex.printStackTrace();
/* 102:    */     }
/* 103:    */   }
/* 104:    */   
/* 105:    */   public void preExecution()
/* 106:    */     throws Exception
/* 107:    */   {}
/* 108:    */   
/* 109:    */   public void run(Object toRun, String[] options)
/* 110:    */     throws Exception
/* 111:    */   {
/* 112:232 */     if (!(toRun instanceof ASEvaluation)) {
/* 113:233 */       throw new IllegalArgumentException("Object to run is not an instance of ASEValuation!");
/* 114:    */     }
/* 115:237 */     preExecution();
/* 116:238 */     runEvaluator((ASEvaluation)toRun, options);
/* 117:239 */     postExecution();
/* 118:    */   }
/* 119:    */   
/* 120:    */   public void postExecution()
/* 121:    */     throws Exception
/* 122:    */   {}
/* 123:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.ASEvaluation
 * JD-Core Version:    0.7.0.1
 */