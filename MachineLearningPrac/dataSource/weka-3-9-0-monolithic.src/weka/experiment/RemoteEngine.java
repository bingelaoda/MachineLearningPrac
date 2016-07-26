/*   1:    */ package weka.experiment;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.net.InetAddress;
/*   5:    */ import java.net.URL;
/*   6:    */ import java.net.URLClassLoader;
/*   7:    */ import java.rmi.Naming;
/*   8:    */ import java.rmi.RMISecurityManager;
/*   9:    */ import java.rmi.RemoteException;
/*  10:    */ import java.rmi.registry.LocateRegistry;
/*  11:    */ import java.rmi.server.UnicastRemoteObject;
/*  12:    */ import java.security.AccessControlException;
/*  13:    */ import java.util.Enumeration;
/*  14:    */ import java.util.Hashtable;
/*  15:    */ import weka.core.Queue;
/*  16:    */ import weka.core.RevisionHandler;
/*  17:    */ import weka.core.RevisionUtils;
/*  18:    */ import weka.core.Utils;
/*  19:    */ import weka.gui.GenericObjectEditor;
/*  20:    */ 
/*  21:    */ public class RemoteEngine
/*  22:    */   extends UnicastRemoteObject
/*  23:    */   implements Compute, RevisionHandler
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = -1021538162895448259L;
/*  26: 52 */   private String m_HostName = "local";
/*  27: 55 */   private final Queue m_TaskQueue = new Queue();
/*  28: 58 */   private final Queue m_TaskIdQueue = new Queue();
/*  29: 61 */   private final Hashtable<String, TaskStatusInfo> m_TaskStatus = new Hashtable();
/*  30: 64 */   private boolean m_TaskRunning = false;
/*  31: 67 */   protected static long CLEANUPTIMEOUT = 3600000L;
/*  32:    */   
/*  33:    */   public RemoteEngine(String hostName)
/*  34:    */     throws RemoteException
/*  35:    */   {
/*  36: 77 */     this.m_HostName = hostName;
/*  37:    */     
/*  38:    */ 
/*  39:    */ 
/*  40:    */ 
/*  41:    */ 
/*  42:    */ 
/*  43:    */ 
/*  44: 85 */     Thread cleanUpThread = new Thread()
/*  45:    */     {
/*  46:    */       public void run()
/*  47:    */       {
/*  48:    */         for (;;)
/*  49:    */         {
/*  50:    */           try
/*  51:    */           {
/*  52: 91 */             Thread.sleep(RemoteEngine.CLEANUPTIMEOUT);
/*  53:    */           }
/*  54:    */           catch (InterruptedException ie) {}
/*  55: 95 */           if (RemoteEngine.this.m_TaskStatus.size() > 0) {
/*  56: 96 */             RemoteEngine.this.purge();
/*  57:    */           } else {
/*  58: 98 */             System.err.println("RemoteEngine : purge - no tasks to check.");
/*  59:    */           }
/*  60:    */         }
/*  61:    */       }
/*  62:102 */     };
/*  63:103 */     cleanUpThread.setPriority(1);
/*  64:104 */     cleanUpThread.setDaemon(true);
/*  65:105 */     cleanUpThread.start();
/*  66:    */   }
/*  67:    */   
/*  68:    */   public synchronized Object executeTask(Task t)
/*  69:    */     throws RemoteException
/*  70:    */   {
/*  71:117 */     String taskId = "" + System.currentTimeMillis() + ":";
/*  72:118 */     taskId = taskId + t.hashCode();
/*  73:119 */     addTaskToQueue(t, taskId);
/*  74:    */     
/*  75:121 */     return taskId;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public Object checkStatus(Object taskId)
/*  79:    */     throws Exception
/*  80:    */   {
/*  81:135 */     TaskStatusInfo inf = (TaskStatusInfo)this.m_TaskStatus.get(taskId);
/*  82:137 */     if (inf == null) {
/*  83:138 */       throw new Exception("RemoteEngine (" + this.m_HostName + ") : Task not found.");
/*  84:    */     }
/*  85:141 */     TaskStatusInfo result = new TaskStatusInfo();
/*  86:142 */     result.setExecutionStatus(inf.getExecutionStatus());
/*  87:143 */     result.setStatusMessage(inf.getStatusMessage());
/*  88:144 */     result.setTaskResult(inf.getTaskResult());
/*  89:146 */     if ((inf.getExecutionStatus() == 3) || (inf.getExecutionStatus() == 2))
/*  90:    */     {
/*  91:148 */       System.err.println("Finished/failed Task id : " + taskId + " checked by client. Removing.");
/*  92:    */       
/*  93:150 */       inf.setTaskResult(null);
/*  94:151 */       inf = null;
/*  95:152 */       this.m_TaskStatus.remove(taskId);
/*  96:    */     }
/*  97:154 */     inf = null;
/*  98:155 */     return result;
/*  99:    */   }
/* 100:    */   
/* 101:    */   private synchronized void addTaskToQueue(Task t, String taskId)
/* 102:    */   {
/* 103:165 */     TaskStatusInfo newTask = t.getTaskStatus();
/* 104:166 */     if (newTask == null) {
/* 105:167 */       newTask = new TaskStatusInfo();
/* 106:    */     }
/* 107:169 */     this.m_TaskQueue.push(t);
/* 108:170 */     this.m_TaskIdQueue.push(taskId);
/* 109:171 */     newTask.setStatusMessage("RemoteEngine (" + this.m_HostName + ") : task " + taskId + " queued at postion: " + this.m_TaskQueue.size());
/* 110:    */     
/* 111:    */ 
/* 112:174 */     this.m_TaskStatus.put(taskId, newTask);
/* 113:175 */     System.err.println("Task id : " + taskId + " Queued.");
/* 114:176 */     if (!this.m_TaskRunning) {
/* 115:177 */       startTask();
/* 116:    */     }
/* 117:    */   }
/* 118:    */   
/* 119:    */   private void startTask()
/* 120:    */   {
/* 121:187 */     if ((!this.m_TaskRunning) && (this.m_TaskQueue.size() > 0))
/* 122:    */     {
/* 123:189 */       Thread activeTaskThread = new Thread()
/* 124:    */       {
/* 125:    */         public void run()
/* 126:    */         {
/* 127:192 */           RemoteEngine.this.m_TaskRunning = true;
/* 128:193 */           Task currentTask = (Task)RemoteEngine.this.m_TaskQueue.pop();
/* 129:194 */           String taskId = (String)RemoteEngine.this.m_TaskIdQueue.pop();
/* 130:195 */           TaskStatusInfo tsi = (TaskStatusInfo)RemoteEngine.this.m_TaskStatus.get(taskId);
/* 131:196 */           tsi.setExecutionStatus(1);
/* 132:197 */           tsi.setStatusMessage("RemoteEngine (" + RemoteEngine.this.m_HostName + ") : task " + taskId + " running...");
/* 133:    */           try
/* 134:    */           {
/* 135:200 */             System.err.println("Launching task id : " + taskId + "...");
/* 136:201 */             currentTask.execute();
/* 137:202 */             TaskStatusInfo runStatus = currentTask.getTaskStatus();
/* 138:203 */             tsi.setExecutionStatus(runStatus.getExecutionStatus());
/* 139:204 */             tsi.setStatusMessage("RemoteExperiment (" + RemoteEngine.this.m_HostName + ") " + runStatus.getStatusMessage());
/* 140:    */             
/* 141:206 */             tsi.setTaskResult(runStatus.getTaskResult());
/* 142:    */           }
/* 143:    */           catch (Error er)
/* 144:    */           {
/* 145:210 */             tsi.setExecutionStatus(2);
/* 146:211 */             if ((er.getCause() instanceof AccessControlException))
/* 147:    */             {
/* 148:212 */               tsi.setStatusMessage("RemoteEngine (" + RemoteEngine.this.m_HostName + ") : security error, check remote policy file.");
/* 149:    */               
/* 150:214 */               System.err.println("Task id " + taskId + " Failed! Check remote policy file");
/* 151:    */             }
/* 152:    */             else
/* 153:    */             {
/* 154:217 */               tsi.setStatusMessage("RemoteEngine (" + RemoteEngine.this.m_HostName + ") : unknown initialization error.");
/* 155:    */               
/* 156:219 */               System.err.println("Task id " + taskId + " Unknown initialization error");
/* 157:    */               
/* 158:221 */               er.printStackTrace();
/* 159:222 */               System.err.println("Detailed message " + er.getMessage());
/* 160:223 */               System.err.println("Detailed cause: " + er.getCause().toString());
/* 161:    */             }
/* 162:    */           }
/* 163:    */           catch (Exception ex)
/* 164:    */           {
/* 165:226 */             tsi.setExecutionStatus(2);
/* 166:227 */             tsi.setStatusMessage("RemoteEngine (" + RemoteEngine.this.m_HostName + ") : " + ex.getMessage());
/* 167:    */             
/* 168:229 */             System.err.println("Task id " + taskId + " Failed, " + ex.getMessage());
/* 169:    */           }
/* 170:    */           finally
/* 171:    */           {
/* 172:232 */             if (RemoteEngine.this.m_TaskStatus.size() == 0) {
/* 173:233 */               RemoteEngine.this.purgeClasses();
/* 174:    */             }
/* 175:235 */             RemoteEngine.this.m_TaskRunning = false;
/* 176:    */             
/* 177:237 */             RemoteEngine.this.startTask();
/* 178:    */           }
/* 179:    */         }
/* 180:240 */       };
/* 181:241 */       activeTaskThread.setPriority(1);
/* 182:242 */       activeTaskThread.start();
/* 183:    */     }
/* 184:    */   }
/* 185:    */   
/* 186:    */   private void purgeClasses()
/* 187:    */   {
/* 188:    */     try
/* 189:    */     {
/* 190:253 */       ClassLoader prevCl = Thread.currentThread().getContextClassLoader();
/* 191:254 */       ClassLoader urlCl = URLClassLoader.newInstance(new URL[] { new URL("file:.") }, prevCl);
/* 192:    */       
/* 193:256 */       Thread.currentThread().setContextClassLoader(urlCl);
/* 194:    */     }
/* 195:    */     catch (Exception ex)
/* 196:    */     {
/* 197:258 */       ex.printStackTrace();
/* 198:    */     }
/* 199:    */   }
/* 200:    */   
/* 201:    */   private void purge()
/* 202:    */   {
/* 203:272 */     Enumeration<String> keys = this.m_TaskStatus.keys();
/* 204:273 */     long currentTime = System.currentTimeMillis();
/* 205:274 */     System.err.println("RemoteEngine purge. Current time : " + currentTime);
/* 206:275 */     while (keys.hasMoreElements())
/* 207:    */     {
/* 208:276 */       String taskId = (String)keys.nextElement();
/* 209:277 */       System.err.print("Examining task id : " + taskId + "... ");
/* 210:278 */       String timeString = taskId.substring(0, taskId.indexOf(':'));
/* 211:279 */       long ts = Long.valueOf(timeString).longValue();
/* 212:280 */       if (currentTime - ts > CLEANUPTIMEOUT)
/* 213:    */       {
/* 214:281 */         TaskStatusInfo tsi = (TaskStatusInfo)this.m_TaskStatus.get(taskId);
/* 215:282 */         if ((tsi != null) && ((tsi.getExecutionStatus() == 3) || (tsi.getExecutionStatus() == 2)))
/* 216:    */         {
/* 217:285 */           System.err.println("\nTask id : " + taskId + " has gone stale. Removing.");
/* 218:    */           
/* 219:287 */           this.m_TaskStatus.remove(taskId);
/* 220:288 */           tsi.setTaskResult(null);
/* 221:289 */           tsi = null;
/* 222:    */         }
/* 223:    */       }
/* 224:    */       else
/* 225:    */       {
/* 226:292 */         System.err.println("ok.");
/* 227:    */       }
/* 228:    */     }
/* 229:295 */     if (this.m_TaskStatus.size() == 0) {
/* 230:296 */       purgeClasses();
/* 231:    */     }
/* 232:    */   }
/* 233:    */   
/* 234:    */   public String getRevision()
/* 235:    */   {
/* 236:307 */     return RevisionUtils.extract("$Revision: 11247 $");
/* 237:    */   }
/* 238:    */   
/* 239:    */   public static void main(String[] args)
/* 240:    */   {
/* 241:    */     
/* 242:322 */     if (System.getSecurityManager() == null) {
/* 243:323 */       System.setSecurityManager(new RMISecurityManager());
/* 244:    */     }
/* 245:326 */     int port = 1099;
/* 246:327 */     InetAddress localhost = null;
/* 247:    */     try
/* 248:    */     {
/* 249:329 */       localhost = InetAddress.getLocalHost();
/* 250:330 */       System.err.println("Host name : " + localhost.getHostName());
/* 251:    */     }
/* 252:    */     catch (Exception ex)
/* 253:    */     {
/* 254:332 */       ex.printStackTrace();
/* 255:    */     }
/* 256:    */     String name;
/* 257:335 */     if (localhost != null) {
/* 258:336 */       name = localhost.getHostName();
/* 259:    */     } else {
/* 260:338 */       name = "localhost";
/* 261:    */     }
/* 262:    */     try
/* 263:    */     {
/* 264:343 */       String portOption = Utils.getOption("p", args);
/* 265:344 */       if (!portOption.equals("")) {
/* 266:345 */         port = Integer.parseInt(portOption);
/* 267:    */       }
/* 268:    */     }
/* 269:    */     catch (Exception ex)
/* 270:    */     {
/* 271:348 */       System.err.println("Usage : -p <port>");
/* 272:    */     }
/* 273:351 */     if (port != 1099) {
/* 274:352 */       name = name + ":" + port;
/* 275:    */     }
/* 276:354 */     String name = "//" + name + "/RemoteEngine";
/* 277:    */     try
/* 278:    */     {
/* 279:357 */       Compute engine = new RemoteEngine(name);
/* 280:    */       try
/* 281:    */       {
/* 282:360 */         Naming.rebind(name, engine);
/* 283:361 */         System.out.println("RemoteEngine bound in RMI registry");
/* 284:    */       }
/* 285:    */       catch (RemoteException ex)
/* 286:    */       {
/* 287:364 */         System.err.println("Attempting to start RMI registry on port " + port + "...");
/* 288:    */         
/* 289:366 */         LocateRegistry.createRegistry(port);
/* 290:367 */         Naming.bind(name, engine);
/* 291:368 */         System.out.println("RemoteEngine bound in RMI registry");
/* 292:    */       }
/* 293:    */     }
/* 294:    */     catch (Exception e)
/* 295:    */     {
/* 296:372 */       System.err.println("RemoteEngine exception: " + e.getMessage());
/* 297:373 */       e.printStackTrace();
/* 298:    */     }
/* 299:    */   }
/* 300:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.RemoteEngine
 * JD-Core Version:    0.7.0.1
 */