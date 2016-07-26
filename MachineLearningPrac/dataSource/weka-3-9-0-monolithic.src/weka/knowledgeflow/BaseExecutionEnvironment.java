/*   1:    */ package weka.knowledgeflow;
/*   2:    */ 
/*   3:    */ import java.util.Map;
/*   4:    */ import java.util.concurrent.ExecutorService;
/*   5:    */ import java.util.concurrent.Executors;
/*   6:    */ import java.util.concurrent.Future;
/*   7:    */ import weka.core.Defaults;
/*   8:    */ import weka.core.Environment;
/*   9:    */ import weka.core.PluginManager;
/*  10:    */ import weka.core.Settings;
/*  11:    */ import weka.core.Settings.SettingKey;
/*  12:    */ import weka.core.WekaException;
/*  13:    */ import weka.gui.Logger;
/*  14:    */ import weka.knowledgeflow.steps.Step;
/*  15:    */ 
/*  16:    */ public class BaseExecutionEnvironment
/*  17:    */   implements ExecutionEnvironment
/*  18:    */ {
/*  19:    */   public static final String DESCRIPTION = "Default execution environment";
/*  20:    */   protected FlowExecutor m_flowExecutor;
/*  21:    */   protected boolean m_headless;
/*  22:    */   protected transient Environment m_envVars;
/*  23:    */   protected transient Settings m_settings;
/*  24:    */   protected transient ExecutorService m_executorService;
/*  25:    */   protected transient ExecutorService m_clientExecutorService;
/*  26:    */   protected transient Logger m_log;
/*  27:    */   protected transient LogManager m_logHandler;
/*  28:    */   protected LoggingLevel m_loggingLevel;
/*  29:    */   
/*  30:    */   static
/*  31:    */   {
/*  32: 47 */     PluginManager.addPlugin(BaseExecutionEnvironment.class.getCanonicalName(), "Default execution environment", BaseExecutionEnvironment.class.getCanonicalName());
/*  33:    */   }
/*  34:    */   
/*  35:    */   public BaseExecutionEnvironment()
/*  36:    */   {
/*  37: 61 */     this.m_envVars = Environment.getSystemWide();
/*  38:    */     
/*  39:    */ 
/*  40:    */ 
/*  41:    */ 
/*  42:    */ 
/*  43:    */ 
/*  44:    */ 
/*  45:    */ 
/*  46:    */ 
/*  47:    */ 
/*  48:    */ 
/*  49:    */ 
/*  50:    */ 
/*  51:    */ 
/*  52:    */ 
/*  53:    */ 
/*  54:    */ 
/*  55:    */ 
/*  56:    */ 
/*  57:    */ 
/*  58:    */ 
/*  59:    */ 
/*  60:    */ 
/*  61:    */ 
/*  62:    */ 
/*  63:    */ 
/*  64:    */ 
/*  65:    */ 
/*  66:    */ 
/*  67:    */ 
/*  68: 92 */     this.m_loggingLevel = LoggingLevel.BASIC;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public String getDescription()
/*  72:    */   {
/*  73:101 */     return "Default execution environment";
/*  74:    */   }
/*  75:    */   
/*  76:    */   public boolean isHeadless()
/*  77:    */   {
/*  78:111 */     return this.m_headless;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void setHeadless(boolean headless)
/*  82:    */   {
/*  83:121 */     this.m_headless = headless;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public Environment getEnvironmentVariables()
/*  87:    */   {
/*  88:131 */     return this.m_envVars;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public void setEnvironmentVariables(Environment env)
/*  92:    */   {
/*  93:141 */     this.m_envVars = env;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public void setSettings(Settings settings)
/*  97:    */   {
/*  98:146 */     this.m_settings = settings;
/*  99:    */     
/* 100:148 */     this.m_logHandler.setLoggingLevel((LoggingLevel)this.m_settings.getSetting("knowledgeflow.main", KFDefaults.LOGGING_LEVEL_KEY, KFDefaults.LOGGING_LEVEL));
/* 101:    */   }
/* 102:    */   
/* 103:    */   public Settings getSettings()
/* 104:    */   {
/* 105:155 */     if (this.m_settings == null) {
/* 106:156 */       this.m_settings = new Settings("weka", "knowledgeflow");
/* 107:    */     }
/* 108:159 */     return this.m_settings;
/* 109:    */   }
/* 110:    */   
/* 111:    */   public Logger getLog()
/* 112:    */   {
/* 113:169 */     return this.m_log;
/* 114:    */   }
/* 115:    */   
/* 116:    */   public void setLog(Logger log)
/* 117:    */   {
/* 118:179 */     this.m_log = log;
/* 119:180 */     if (this.m_logHandler == null)
/* 120:    */     {
/* 121:181 */       this.m_logHandler = new LogManager(this.m_log);
/* 122:182 */       this.m_logHandler.m_statusMessagePrefix = ("BaseExecutionEnvironment$" + hashCode() + "|");
/* 123:    */     }
/* 124:185 */     this.m_logHandler.setLog(this.m_log);
/* 125:    */   }
/* 126:    */   
/* 127:    */   public LoggingLevel getLoggingLevel()
/* 128:    */   {
/* 129:195 */     return this.m_loggingLevel;
/* 130:    */   }
/* 131:    */   
/* 132:    */   public void setLoggingLevel(LoggingLevel level)
/* 133:    */   {
/* 134:205 */     this.m_loggingLevel = level;
/* 135:    */   }
/* 136:    */   
/* 137:    */   public <T> Future<ExecutionResult<T>> submitTask(StepTask<T> stepTask)
/* 138:    */     throws WekaException
/* 139:    */   {
/* 140:221 */     this.m_logHandler.logDebug("Submitting " + stepTask.toString() + (stepTask.isResourceIntensive() ? " (resource intensive)" : ""));
/* 141:    */     
/* 142:223 */     return stepTask.isResourceIntensive() ? this.m_clientExecutorService.submit(stepTask) : this.m_executorService.submit(stepTask);
/* 143:    */   }
/* 144:    */   
/* 145:    */   public void stopProcessing()
/* 146:    */   {
/* 147:233 */     if (getFlowExecutor() != null) {
/* 148:234 */       getFlowExecutor().stopProcessing();
/* 149:    */     }
/* 150:236 */     if (this.m_executorService != null)
/* 151:    */     {
/* 152:237 */       this.m_executorService.shutdownNow();
/* 153:238 */       this.m_executorService = null;
/* 154:    */     }
/* 155:    */   }
/* 156:    */   
/* 157:    */   protected FlowExecutor getFlowExecutor()
/* 158:    */   {
/* 159:250 */     return this.m_flowExecutor;
/* 160:    */   }
/* 161:    */   
/* 162:    */   protected void setFlowExecutor(FlowExecutor executor)
/* 163:    */   {
/* 164:261 */     this.m_flowExecutor = executor;
/* 165:    */   }
/* 166:    */   
/* 167:    */   protected void startClientExecutionService(int numThreadsMain, int numThreadsHighLoad)
/* 168:    */   {
/* 169:278 */     if (this.m_executorService != null) {
/* 170:279 */       this.m_executorService.shutdownNow();
/* 171:    */     }
/* 172:282 */     this.m_logHandler.logDebug("Requested number of threads for main step executor: " + numThreadsMain);
/* 173:    */     
/* 174:    */ 
/* 175:285 */     this.m_logHandler.logDebug("Requested number of threads for high load executor: " + (numThreadsHighLoad > 0 ? numThreadsHighLoad : Runtime.getRuntime().availableProcessors()));
/* 176:    */     
/* 177:    */ 
/* 178:    */ 
/* 179:289 */     this.m_executorService = (numThreadsMain > 0 ? Executors.newFixedThreadPool(numThreadsMain) : Executors.newCachedThreadPool());
/* 180:    */     
/* 181:    */ 
/* 182:    */ 
/* 183:293 */     this.m_clientExecutorService = (numThreadsHighLoad > 0 ? Executors.newFixedThreadPool(numThreadsHighLoad) : Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
/* 184:    */   }
/* 185:    */   
/* 186:    */   protected void stopClientExecutionService()
/* 187:    */   {
/* 188:303 */     if (this.m_executorService != null) {
/* 189:304 */       this.m_executorService.shutdown();
/* 190:    */     }
/* 191:307 */     if (this.m_clientExecutorService != null) {
/* 192:308 */       this.m_clientExecutorService.shutdown();
/* 193:    */     }
/* 194:    */   }
/* 195:    */   
/* 196:    */   protected void launchStartPoint(final StepManagerImpl startPoint)
/* 197:    */     throws WekaException
/* 198:    */   {
/* 199:323 */     this.m_logHandler.logDebug("Submitting " + startPoint.getName() + (startPoint.stepIsResourceIntensive() ? " (resource intensive)" : ""));
/* 200:325 */     if (startPoint.stepIsResourceIntensive()) {
/* 201:326 */       submitTask(new StepTask(null)
/* 202:    */       {
/* 203:    */         private static final long serialVersionUID = -5466021103296024455L;
/* 204:    */         
/* 205:    */         public void process()
/* 206:    */           throws Exception
/* 207:    */         {
/* 208:333 */           startPoint.startStep();
/* 209:    */         }
/* 210:    */       });
/* 211:    */     } else {
/* 212:337 */       this.m_executorService.submit(new Runnable()
/* 213:    */       {
/* 214:    */         public void run()
/* 215:    */         {
/* 216:340 */           startPoint.startStep();
/* 217:    */         }
/* 218:    */       });
/* 219:    */     }
/* 220:    */   }
/* 221:    */   
/* 222:    */   protected void sendDataToStep(final StepManagerImpl step, final Data... data)
/* 223:    */     throws WekaException
/* 224:    */   {
/* 225:359 */     if (data != null) {
/* 226:360 */       if ((data.length == 1) && (StepManagerImpl.connectionIsIncremental(data[0])))
/* 227:    */       {
/* 228:364 */         step.processIncoming(data[0]);
/* 229:    */       }
/* 230:    */       else
/* 231:    */       {
/* 232:366 */         this.m_logHandler.logDebug("Submitting " + step.getName() + (step.stepIsResourceIntensive() ? " (resource intensive)" : ""));
/* 233:368 */         if (step.stepIsResourceIntensive()) {
/* 234:369 */           this.m_clientExecutorService.submit(new Runnable()
/* 235:    */           {
/* 236:    */             public void run()
/* 237:    */             {
/* 238:372 */               for (Data d : data) {
/* 239:373 */                 step.processIncoming(d);
/* 240:    */               }
/* 241:    */             }
/* 242:    */           });
/* 243:    */         } else {
/* 244:378 */           this.m_executorService.submit(new Runnable()
/* 245:    */           {
/* 246:    */             public void run()
/* 247:    */             {
/* 248:381 */               for (Data d : data) {
/* 249:382 */                 step.processIncoming(d);
/* 250:    */               }
/* 251:    */             }
/* 252:    */           });
/* 253:    */         }
/* 254:    */       }
/* 255:    */     }
/* 256:    */   }
/* 257:    */   
/* 258:    */   public Defaults getDefaultSettings()
/* 259:    */   {
/* 260:398 */     return new BaseExecutionEnvironmentDefaults();
/* 261:    */   }
/* 262:    */   
/* 263:    */   public static class BaseExecutionEnvironmentDefaults
/* 264:    */     extends Defaults
/* 265:    */   {
/* 266:406 */     public static final Settings.SettingKey STEP_EXECUTOR_SERVICE_NUM_THREADS_KEY = new Settings.SettingKey("knowledgeflow.stepExecutorNumThreads", "Number of threads to use in the main step executor service", "");
/* 267:    */     public static final int STEP_EXECUTOR_SERVICE_NUM_THREADS = 50;
/* 268:411 */     public static final Settings.SettingKey RESOURCE_INTENSIVE_EXECUTOR_SERVICE_NUM_THREADS_KEY = new Settings.SettingKey("knowledgeflow.highResourceExecutorNumThreads", "Number of threads to use in the resource intensive executor service", "<html>This executor service is used for executing StepTasks and<br>Steps that are marked as resource intensive. 0 = use as many<br>threads as there are cpu processors.</html>");
/* 269:    */     public static final int RESOURCE_INTENSIVE_EXECUTOR_SERVICE_NUM_THREADS = 0;
/* 270:    */     private static final long serialVersionUID = -3386792058002464330L;
/* 271:    */     
/* 272:    */     public BaseExecutionEnvironmentDefaults()
/* 273:    */     {
/* 274:424 */       super();
/* 275:    */       
/* 276:426 */       this.m_defaults.put(STEP_EXECUTOR_SERVICE_NUM_THREADS_KEY, Integer.valueOf(50));
/* 277:    */       
/* 278:428 */       this.m_defaults.put(RESOURCE_INTENSIVE_EXECUTOR_SERVICE_NUM_THREADS_KEY, Integer.valueOf(0));
/* 279:    */     }
/* 280:    */   }
/* 281:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.BaseExecutionEnvironment
 * JD-Core Version:    0.7.0.1
 */