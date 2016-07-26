/*   1:    */ package weka.knowledgeflow;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.LinkedHashMap;
/*   5:    */ import java.util.Map;
/*   6:    */ import weka.knowledgeflow.steps.Step;
/*   7:    */ 
/*   8:    */ public class Data
/*   9:    */   implements Serializable
/*  10:    */ {
/*  11:    */   private static final long serialVersionUID = 239235113781041619L;
/*  12:    */   protected Step m_sourceStep;
/*  13:    */   protected String m_connectionName;
/*  14: 72 */   protected Map<String, Object> m_payloadMap = new LinkedHashMap();
/*  15:    */   
/*  16:    */   public Data() {}
/*  17:    */   
/*  18:    */   public Data(String connectionName)
/*  19:    */   {
/*  20: 87 */     setConnectionName(connectionName);
/*  21:    */   }
/*  22:    */   
/*  23:    */   public Data(String connectionName, Object primaryPayload)
/*  24:    */   {
/*  25: 99 */     this(connectionName);
/*  26:100 */     setPayloadElement(connectionName, primaryPayload);
/*  27:    */   }
/*  28:    */   
/*  29:    */   public void setSourceStep(Step sourceStep)
/*  30:    */   {
/*  31:109 */     this.m_sourceStep = sourceStep;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public Step getSourceStep()
/*  35:    */   {
/*  36:118 */     return this.m_sourceStep;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void setConnectionName(String name)
/*  40:    */   {
/*  41:127 */     this.m_connectionName = name;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public String getConnectionName()
/*  45:    */   {
/*  46:136 */     return this.m_connectionName;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public <T> T getPrimaryPayload()
/*  50:    */   {
/*  51:149 */     return this.m_payloadMap.get(this.m_connectionName);
/*  52:    */   }
/*  53:    */   
/*  54:    */   public <T> T getPayloadElement(String name)
/*  55:    */   {
/*  56:162 */     return this.m_payloadMap.get(name);
/*  57:    */   }
/*  58:    */   
/*  59:    */   public <T> T getPayloadElement(String name, T defaultValue)
/*  60:    */   {
/*  61:175 */     Object result = getPayloadElement(name);
/*  62:176 */     if (result == null) {
/*  63:177 */       return defaultValue;
/*  64:    */     }
/*  65:180 */     return result;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public void setPayloadElement(String name, Object value)
/*  69:    */   {
/*  70:192 */     this.m_payloadMap.put(name, value);
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void clearPayload()
/*  74:    */   {
/*  75:199 */     this.m_payloadMap.clear();
/*  76:    */   }
/*  77:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.Data
 * JD-Core Version:    0.7.0.1
 */