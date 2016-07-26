/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.lang.annotation.Annotation;
/*   5:    */ import java.util.Map;
/*   6:    */ import weka.core.Defaults;
/*   7:    */ import weka.core.Instances;
/*   8:    */ import weka.core.WekaException;
/*   9:    */ import weka.gui.ProgrammaticProperty;
/*  10:    */ import weka.gui.knowledgeflow.StepInteractiveViewer;
/*  11:    */ import weka.knowledgeflow.Data;
/*  12:    */ import weka.knowledgeflow.LoggingLevel;
/*  13:    */ import weka.knowledgeflow.StepManager;
/*  14:    */ import weka.knowledgeflow.StepManagerImpl;
/*  15:    */ 
/*  16:    */ public abstract class BaseStep
/*  17:    */   implements Step, BaseStepExtender, Serializable
/*  18:    */ {
/*  19:    */   private static final long serialVersionUID = -1595753549991953141L;
/*  20: 51 */   protected String m_stepName = "";
/*  21:    */   protected transient StepManager m_stepManager;
/*  22:    */   protected boolean m_stepIsResourceIntensive;
/*  23:    */   
/*  24:    */   public BaseStep()
/*  25:    */   {
/*  26: 63 */     String clazzName = getClass().getCanonicalName();
/*  27: 64 */     clazzName = clazzName.substring(clazzName.lastIndexOf(".") + 1);
/*  28: 65 */     setName(clazzName);
/*  29: 66 */     Annotation[] annotations = getClass().getAnnotations();
/*  30: 67 */     for (Annotation a : annotations) {
/*  31: 68 */       if ((a instanceof KFStep))
/*  32:    */       {
/*  33: 69 */         String name = ((KFStep)a).name();
/*  34: 70 */         if (name.length() > 0)
/*  35:    */         {
/*  36: 71 */           setName(name);
/*  37: 72 */           break;
/*  38:    */         }
/*  39:    */       }
/*  40:    */     }
/*  41:    */   }
/*  42:    */   
/*  43:    */   public String globalInfo()
/*  44:    */   {
/*  45: 87 */     Annotation[] annotations = getClass().getAnnotations();
/*  46: 88 */     for (Annotation a : annotations) {
/*  47: 89 */       if ((a instanceof KFStep)) {
/*  48: 90 */         return ((KFStep)a).toolTipText();
/*  49:    */       }
/*  50:    */     }
/*  51: 94 */     return null;
/*  52:    */   }
/*  53:    */   
/*  54:    */   @NotPersistable
/*  55:    */   public StepManager getStepManager()
/*  56:    */   {
/*  57:105 */     return this.m_stepManager;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public void setStepManager(StepManager manager)
/*  61:    */   {
/*  62:115 */     this.m_stepManager = manager;
/*  63:    */   }
/*  64:    */   
/*  65:    */   @ProgrammaticProperty
/*  66:    */   public void setStepIsResourceIntensive(boolean isResourceIntensive)
/*  67:    */   {
/*  68:126 */     getStepManager().setStepIsResourceIntensive(isResourceIntensive);
/*  69:    */   }
/*  70:    */   
/*  71:    */   public boolean isResourceIntensive()
/*  72:    */   {
/*  73:135 */     return getStepManager().stepIsResourceIntensive();
/*  74:    */   }
/*  75:    */   
/*  76:    */   public String getName()
/*  77:    */   {
/*  78:145 */     return this.m_stepName;
/*  79:    */   }
/*  80:    */   
/*  81:    */   @ProgrammaticProperty
/*  82:    */   public void setName(String name)
/*  83:    */   {
/*  84:156 */     this.m_stepName = name;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public void start()
/*  88:    */     throws WekaException
/*  89:    */   {}
/*  90:    */   
/*  91:    */   public void stop()
/*  92:    */   {
/*  93:178 */     if (!(this instanceof Note))
/*  94:    */     {
/*  95:180 */       getStepManager().statusMessage("INTERRUPTED");
/*  96:181 */       getStepManager().log("Interrupted", LoggingLevel.LOW);
/*  97:    */     }
/*  98:184 */     ((StepManagerImpl)getStepManager()).setStopRequested(true);
/*  99:    */     
/* 100:    */ 
/* 101:    */ 
/* 102:    */ 
/* 103:    */ 
/* 104:190 */     getStepManager().throughputUpdateEnd();
/* 105:    */   }
/* 106:    */   
/* 107:    */   public Instances outputStructureForConnectionType(String connectionName)
/* 108:    */     throws WekaException
/* 109:    */   {
/* 110:207 */     return null;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public void processIncoming(Data data)
/* 114:    */     throws WekaException
/* 115:    */   {}
/* 116:    */   
/* 117:    */   public String getCustomEditorForStep()
/* 118:    */   {
/* 119:231 */     return null;
/* 120:    */   }
/* 121:    */   
/* 122:    */   public Map<String, String> getInteractiveViewers()
/* 123:    */   {
/* 124:256 */     return null;
/* 125:    */   }
/* 126:    */   
/* 127:    */   public Map<String, StepInteractiveViewer> getInteractiveViewersImpls()
/* 128:    */   {
/* 129:272 */     return null;
/* 130:    */   }
/* 131:    */   
/* 132:    */   public Defaults getDefaultSettings()
/* 133:    */   {
/* 134:283 */     return null;
/* 135:    */   }
/* 136:    */   
/* 137:    */   public boolean isStopRequested()
/* 138:    */   {
/* 139:292 */     return getStepManager().isStopRequested();
/* 140:    */   }
/* 141:    */   
/* 142:    */   public String environmentSubstitute(String source)
/* 143:    */   {
/* 144:302 */     return getStepManager().environmentSubstitute(source);
/* 145:    */   }
/* 146:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.BaseStep
 * JD-Core Version:    0.7.0.1
 */