/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.LinkedHashMap;
/*   5:    */ import java.util.List;
/*   6:    */ import java.util.Map;
/*   7:    */ import java.util.Map.Entry;
/*   8:    */ import weka.core.Environment;
/*   9:    */ import weka.core.WekaException;
/*  10:    */ import weka.gui.ProgrammaticProperty;
/*  11:    */ import weka.knowledgeflow.ExecutionEnvironment;
/*  12:    */ import weka.knowledgeflow.StepManager;
/*  13:    */ 
/*  14:    */ @KFStep(name="SetVariables", category="Flow", toolTipText="Assign default values for variables if they are not already set", iconPath="weka/gui/knowledgeflow/icons/DiamondPlain.gif")
/*  15:    */ public class SetVariables
/*  16:    */   extends BaseStep
/*  17:    */ {
/*  18:    */   public static final String SEP1 = "@@vv@@";
/*  19:    */   public static final String SEP2 = "@v@v";
/*  20:    */   private static final long serialVersionUID = 8042350408846800738L;
/*  21: 59 */   protected String m_internalRep = "";
/*  22: 62 */   protected Map<String, String> m_varsToSet = new LinkedHashMap();
/*  23:    */   
/*  24:    */   @ProgrammaticProperty
/*  25:    */   public void setVarsInternalRep(String rep)
/*  26:    */   {
/*  27: 72 */     this.m_internalRep = rep;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public String getVarsInternalRep()
/*  31:    */   {
/*  32: 81 */     return this.m_internalRep;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public void stepInit()
/*  36:    */     throws WekaException
/*  37:    */   {
/*  38: 92 */     this.m_varsToSet = internalToMap(this.m_internalRep);
/*  39:    */     
/*  40: 94 */     Environment currentEnv = getStepManager().getExecutionEnvironment().getEnvironmentVariables();
/*  41: 96 */     if (currentEnv == null) {
/*  42: 97 */       throw new WekaException("The execution environment doesn't seem to have any support for variables");
/*  43:    */     }
/*  44:101 */     for (Map.Entry<String, String> e : this.m_varsToSet.entrySet())
/*  45:    */     {
/*  46:102 */       String key = (String)e.getKey();
/*  47:103 */       String value = (String)e.getValue();
/*  48:105 */       if ((key != null) && (key.length() > 0) && (value != null) && (currentEnv.getVariableValue(key) == null))
/*  49:    */       {
/*  50:107 */         getStepManager().logDetailed("Setting variable: " + key + " = " + value);
/*  51:    */         
/*  52:109 */         currentEnv.addVariable(key, value);
/*  53:    */       }
/*  54:    */     }
/*  55:    */   }
/*  56:    */   
/*  57:    */   public List<String> getIncomingConnectionTypes()
/*  58:    */   {
/*  59:125 */     return new ArrayList();
/*  60:    */   }
/*  61:    */   
/*  62:    */   public List<String> getOutgoingConnectionTypes()
/*  63:    */   {
/*  64:139 */     return new ArrayList();
/*  65:    */   }
/*  66:    */   
/*  67:    */   public String getCustomEditorForStep()
/*  68:    */   {
/*  69:152 */     return "weka.gui.knowledgeflow.steps.SetVariablesStepEditorDialog";
/*  70:    */   }
/*  71:    */   
/*  72:    */   public static Map<String, String> internalToMap(String internalRep)
/*  73:    */   {
/*  74:163 */     Map<String, String> varsToSet = new LinkedHashMap();
/*  75:164 */     if ((internalRep != null) || (internalRep.length() > 0))
/*  76:    */     {
/*  77:165 */       String[] parts = internalRep.split("@@vv@@");
/*  78:166 */       for (String p : parts)
/*  79:    */       {
/*  80:167 */         String[] keyVal = p.trim().split("@v@v");
/*  81:168 */         if (keyVal.length == 2) {
/*  82:169 */           varsToSet.put(keyVal[0].trim(), keyVal[1]);
/*  83:    */         }
/*  84:    */       }
/*  85:    */     }
/*  86:174 */     return varsToSet;
/*  87:    */   }
/*  88:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.SetVariables
 * JD-Core Version:    0.7.0.1
 */