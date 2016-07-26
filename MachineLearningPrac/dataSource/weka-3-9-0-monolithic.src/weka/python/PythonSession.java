/*   1:    */ package weka.python;
/*   2:    */ 
/*   3:    */ import java.awt.image.BufferedImage;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.IOException;
/*   6:    */ import java.io.PrintStream;
/*   7:    */ import java.io.StringWriter;
/*   8:    */ import java.net.ServerSocket;
/*   9:    */ import java.net.Socket;
/*  10:    */ import java.util.List;
/*  11:    */ import org.apache.commons.io.IOUtils;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.WekaException;
/*  14:    */ import weka.core.WekaPackageManager;
/*  15:    */ import weka.gui.Logger;
/*  16:    */ 
/*  17:    */ public class PythonSession
/*  18:    */ {
/*  19:    */   private String m_pythonCommand;
/*  20:    */   private static PythonSession s_sessionSingleton;
/*  21:    */   private static Object s_sessionHolder;
/*  22:    */   
/*  23:    */   public static enum PythonVariableType
/*  24:    */   {
/*  25: 50 */     DataFrame,  Image,  String,  Unknown;
/*  26:    */     
/*  27:    */     private PythonVariableType() {}
/*  28:    */   }
/*  29:    */   
/*  30: 63 */   private static String s_pythonEnvCheckResults = "";
/*  31: 66 */   protected SessionMutex m_mutex = new SessionMutex();
/*  32:    */   protected ServerSocket m_serverSocket;
/*  33:    */   protected Socket m_localSocket;
/*  34:    */   protected Process m_serverProcess;
/*  35:    */   protected boolean m_shutdown;
/*  36:    */   protected Thread m_shutdownHook;
/*  37: 84 */   protected int m_pythonPID = -1;
/*  38:    */   protected boolean m_debug;
/*  39:    */   protected Logger m_log;
/*  40:    */   
/*  41:    */   public static PythonSession acquireSession(Object requester)
/*  42:    */     throws WekaException
/*  43:    */   {
/*  44:101 */     return s_sessionSingleton.getSession(requester);
/*  45:    */   }
/*  46:    */   
/*  47:    */   public static void releaseSession(Object requester)
/*  48:    */   {
/*  49:111 */     s_sessionSingleton.dropSession(requester);
/*  50:    */   }
/*  51:    */   
/*  52:    */   public static boolean pythonAvailable()
/*  53:    */   {
/*  54:120 */     return s_sessionSingleton != null;
/*  55:    */   }
/*  56:    */   
/*  57:    */   private PythonSession(String pythonCommand, boolean debug)
/*  58:    */     throws IOException
/*  59:    */   {
/*  60:131 */     this.m_debug = debug;
/*  61:132 */     this.m_pythonCommand = pythonCommand;
/*  62:133 */     s_sessionSingleton = null;
/*  63:134 */     s_pythonEnvCheckResults = "";
/*  64:135 */     String tester = WekaPackageManager.PACKAGES_DIR.getAbsolutePath() + File.separator + "wekaPython" + File.separator + "resources" + File.separator + "py" + File.separator + "pyCheck.py";
/*  65:    */     
/*  66:    */ 
/*  67:    */ 
/*  68:139 */     ProcessBuilder builder = new ProcessBuilder(new String[] { pythonCommand, tester });
/*  69:140 */     Process pyProcess = builder.start();
/*  70:141 */     StringWriter writer = new StringWriter();
/*  71:142 */     IOUtils.copy(pyProcess.getInputStream(), writer);
/*  72:143 */     s_pythonEnvCheckResults = writer.toString();
/*  73:144 */     s_sessionSingleton = this;
/*  74:145 */     this.m_shutdown = false;
/*  75:148 */     if (s_pythonEnvCheckResults.length() < 5) {
/*  76:149 */       launchServer(true);
/*  77:    */     }
/*  78:    */   }
/*  79:    */   
/*  80:    */   private synchronized PythonSession getSession(Object requester)
/*  81:    */     throws WekaException
/*  82:    */   {
/*  83:162 */     if (s_sessionSingleton == null) {
/*  84:163 */       throw new WekaException("Python not available!");
/*  85:    */     }
/*  86:166 */     if (s_sessionHolder == requester) {
/*  87:167 */       return this;
/*  88:    */     }
/*  89:170 */     this.m_mutex.safeLock();
/*  90:171 */     s_sessionHolder = requester;
/*  91:172 */     return this;
/*  92:    */   }
/*  93:    */   
/*  94:    */   private void dropSession(Object requester)
/*  95:    */   {
/*  96:181 */     if (requester == s_sessionHolder)
/*  97:    */     {
/*  98:182 */       s_sessionHolder = null;
/*  99:183 */       this.m_mutex.unlock();
/* 100:    */     }
/* 101:    */   }
/* 102:    */   
/* 103:    */   private void launchServer(boolean startPython)
/* 104:    */     throws IOException
/* 105:    */   {
/* 106:198 */     if (this.m_debug) {
/* 107:199 */       System.err.println("Launching server socket...");
/* 108:    */     }
/* 109:201 */     this.m_serverSocket = new ServerSocket(0);
/* 110:202 */     this.m_serverSocket.setSoTimeout(10000);
/* 111:203 */     int localPort = this.m_serverSocket.getLocalPort();
/* 112:204 */     if (this.m_debug) {
/* 113:205 */       System.err.println("Local port: " + localPort);
/* 114:    */     }
/* 115:207 */     Thread acceptThread = new Thread()
/* 116:    */     {
/* 117:    */       public void run()
/* 118:    */       {
/* 119:    */         try
/* 120:    */         {
/* 121:211 */           PythonSession.this.m_localSocket = PythonSession.this.m_serverSocket.accept();
/* 122:    */         }
/* 123:    */         catch (IOException e)
/* 124:    */         {
/* 125:213 */           PythonSession.this.m_localSocket = null;
/* 126:    */         }
/* 127:    */       }
/* 128:216 */     };
/* 129:217 */     acceptThread.start();
/* 130:219 */     if (startPython)
/* 131:    */     {
/* 132:220 */       String serverScript = WekaPackageManager.PACKAGES_DIR.getAbsolutePath() + File.separator + "wekaPython" + File.separator + "resources" + File.separator + "py" + File.separator + "pyServer.py";
/* 133:    */       
/* 134:    */ 
/* 135:    */ 
/* 136:224 */       ProcessBuilder processBuilder = new ProcessBuilder(new String[] { this.m_pythonCommand, serverScript, "" + localPort, this.m_debug ? "debug" : "" });
/* 137:    */       
/* 138:    */ 
/* 139:227 */       this.m_serverProcess = processBuilder.start();
/* 140:    */     }
/* 141:    */     try
/* 142:    */     {
/* 143:230 */       acceptThread.join();
/* 144:    */     }
/* 145:    */     catch (InterruptedException e) {}
/* 146:234 */     if (this.m_localSocket == null)
/* 147:    */     {
/* 148:235 */       shutdown();
/* 149:236 */       throw new IOException("Was unable to start python server");
/* 150:    */     }
/* 151:238 */     this.m_pythonPID = ServerUtils.receiveServerPIDAck(this.m_localSocket.getInputStream());
/* 152:    */     
/* 153:    */ 
/* 154:241 */     this.m_shutdownHook = new Thread()
/* 155:    */     {
/* 156:    */       public void run()
/* 157:    */       {
/* 158:244 */         PythonSession.this.shutdown();
/* 159:    */       }
/* 160:246 */     };
/* 161:247 */     Runtime.getRuntime().addShutdownHook(this.m_shutdownHook);
/* 162:    */   }
/* 163:    */   
/* 164:    */   public void setLog(Logger log)
/* 165:    */   {
/* 166:257 */     this.m_log = log;
/* 167:    */   }
/* 168:    */   
/* 169:    */   public PythonVariableType getPythonVariableType(String varName, boolean debug)
/* 170:    */     throws WekaException
/* 171:    */   {
/* 172:    */     try
/* 173:    */     {
/* 174:276 */       return ServerUtils.getPythonVariableType(varName, this.m_localSocket.getOutputStream(), this.m_localSocket.getInputStream(), this.m_log, debug);
/* 175:    */     }
/* 176:    */     catch (Exception ex)
/* 177:    */     {
/* 178:280 */       throw new WekaException(ex);
/* 179:    */     }
/* 180:    */   }
/* 181:    */   
/* 182:    */   public void instancesToPython(Instances instances, String pythonFrameName, boolean debug)
/* 183:    */     throws WekaException
/* 184:    */   {
/* 185:    */     try
/* 186:    */     {
/* 187:295 */       ServerUtils.sendInstances(instances, pythonFrameName, this.m_localSocket.getOutputStream(), this.m_localSocket.getInputStream(), this.m_log, debug);
/* 188:    */     }
/* 189:    */     catch (Exception ex)
/* 190:    */     {
/* 191:299 */       throw new WekaException(ex);
/* 192:    */     }
/* 193:    */   }
/* 194:    */   
/* 195:    */   public void instancesToPythonAsScikitLearn(Instances instances, String pythonFrameName, boolean debug)
/* 196:    */     throws WekaException
/* 197:    */   {
/* 198:    */     try
/* 199:    */     {
/* 200:317 */       ServerUtils.sendInstancesScikitLearn(instances, pythonFrameName, this.m_localSocket.getOutputStream(), this.m_localSocket.getInputStream(), this.m_log, debug);
/* 201:    */     }
/* 202:    */     catch (Exception ex)
/* 203:    */     {
/* 204:321 */       throw new WekaException(ex);
/* 205:    */     }
/* 206:    */   }
/* 207:    */   
/* 208:    */   public Instances getDataFrameAsInstances(String frameName, boolean debug)
/* 209:    */     throws WekaException
/* 210:    */   {
/* 211:    */     try
/* 212:    */     {
/* 213:339 */       return ServerUtils.receiveInstances(frameName, this.m_localSocket.getOutputStream(), this.m_localSocket.getInputStream(), this.m_log, debug);
/* 214:    */     }
/* 215:    */     catch (IOException ex)
/* 216:    */     {
/* 217:343 */       throw new WekaException(ex);
/* 218:    */     }
/* 219:    */   }
/* 220:    */   
/* 221:    */   public List<String> executeScript(String pyScript, boolean debug)
/* 222:    */     throws WekaException
/* 223:    */   {
/* 224:    */     try
/* 225:    */     {
/* 226:359 */       return ServerUtils.executeUserScript(pyScript, this.m_localSocket.getOutputStream(), this.m_localSocket.getInputStream(), this.m_log, debug);
/* 227:    */     }
/* 228:    */     catch (IOException ex)
/* 229:    */     {
/* 230:363 */       throw new WekaException(ex);
/* 231:    */     }
/* 232:    */   }
/* 233:    */   
/* 234:    */   public boolean checkIfPythonVariableIsSet(String varName, boolean debug)
/* 235:    */     throws WekaException
/* 236:    */   {
/* 237:    */     try
/* 238:    */     {
/* 239:378 */       return ServerUtils.checkIfPythonVariableIsSet(varName, this.m_localSocket.getOutputStream(), this.m_localSocket.getInputStream(), this.m_log, debug);
/* 240:    */     }
/* 241:    */     catch (IOException ex)
/* 242:    */     {
/* 243:382 */       throw new WekaException(ex);
/* 244:    */     }
/* 245:    */   }
/* 246:    */   
/* 247:    */   public Object getVariableValueFromPythonAsJson(String varName, boolean debug)
/* 248:    */     throws WekaException
/* 249:    */   {
/* 250:    */     try
/* 251:    */     {
/* 252:400 */       return ServerUtils.receiveJsonVariableValue(varName, this.m_localSocket.getOutputStream(), this.m_localSocket.getInputStream(), this.m_log, debug);
/* 253:    */     }
/* 254:    */     catch (IOException ex)
/* 255:    */     {
/* 256:404 */       throw new WekaException(ex);
/* 257:    */     }
/* 258:    */   }
/* 259:    */   
/* 260:    */   public String getVariableValueFromPythonAsPickledObject(String varName, boolean debug)
/* 261:    */     throws WekaException
/* 262:    */   {
/* 263:    */     try
/* 264:    */     {
/* 265:421 */       return ServerUtils.receivePickledVariableValue(varName, this.m_localSocket.getOutputStream(), this.m_localSocket.getInputStream(), false, this.m_log, debug);
/* 266:    */     }
/* 267:    */     catch (IOException ex)
/* 268:    */     {
/* 269:425 */       throw new WekaException(ex);
/* 270:    */     }
/* 271:    */   }
/* 272:    */   
/* 273:    */   public String getVariableValueFromPythonAsPlainString(String varName, boolean debug)
/* 274:    */     throws WekaException
/* 275:    */   {
/* 276:    */     try
/* 277:    */     {
/* 278:441 */       return ServerUtils.receivePickledVariableValue(varName, this.m_localSocket.getOutputStream(), this.m_localSocket.getInputStream(), true, this.m_log, debug);
/* 279:    */     }
/* 280:    */     catch (IOException ex)
/* 281:    */     {
/* 282:445 */       throw new WekaException(ex);
/* 283:    */     }
/* 284:    */   }
/* 285:    */   
/* 286:    */   public List<String[]> getVariableListFromPython(boolean debug)
/* 287:    */     throws WekaException
/* 288:    */   {
/* 289:    */     try
/* 290:    */     {
/* 291:462 */       return ServerUtils.receiveVariableList(this.m_localSocket.getOutputStream(), this.m_localSocket.getInputStream(), this.m_log, debug);
/* 292:    */     }
/* 293:    */     catch (IOException ex)
/* 294:    */     {
/* 295:465 */       throw new WekaException(ex);
/* 296:    */     }
/* 297:    */   }
/* 298:    */   
/* 299:    */   public void setPythonPickledVariableValue(String varName, String varValue, boolean debug)
/* 300:    */     throws WekaException
/* 301:    */   {
/* 302:    */     try
/* 303:    */     {
/* 304:482 */       ServerUtils.sendPickledVariableValue(varName, varValue, this.m_localSocket.getOutputStream(), this.m_localSocket.getInputStream(), this.m_log, debug);
/* 305:    */     }
/* 306:    */     catch (IOException ex)
/* 307:    */     {
/* 308:486 */       throw new WekaException(ex);
/* 309:    */     }
/* 310:    */   }
/* 311:    */   
/* 312:    */   public List<String> getPythonDebugBuffer(boolean debug)
/* 313:    */     throws WekaException
/* 314:    */   {
/* 315:    */     try
/* 316:    */     {
/* 317:506 */       return ServerUtils.receiveDebugBuffer(this.m_localSocket.getOutputStream(), this.m_localSocket.getInputStream(), this.m_log, debug);
/* 318:    */     }
/* 319:    */     catch (IOException ex)
/* 320:    */     {
/* 321:509 */       throw new WekaException(ex);
/* 322:    */     }
/* 323:    */   }
/* 324:    */   
/* 325:    */   public BufferedImage getImageFromPython(String varName, boolean debug)
/* 326:    */     throws WekaException
/* 327:    */   {
/* 328:    */     try
/* 329:    */     {
/* 330:527 */       return ServerUtils.getPNGImageFromPython(varName, this.m_localSocket.getOutputStream(), this.m_localSocket.getInputStream(), this.m_log, debug);
/* 331:    */     }
/* 332:    */     catch (IOException ex)
/* 333:    */     {
/* 334:531 */       throw new WekaException(ex);
/* 335:    */     }
/* 336:    */   }
/* 337:    */   
/* 338:    */   private void shutdown()
/* 339:    */   {
/* 340:539 */     if (!this.m_shutdown) {
/* 341:    */       try
/* 342:    */       {
/* 343:541 */         this.m_shutdown = true;
/* 344:542 */         if (this.m_localSocket != null)
/* 345:    */         {
/* 346:543 */           if (this.m_debug) {
/* 347:544 */             System.err.println("Sending shutdown command...");
/* 348:    */           }
/* 349:546 */           if (this.m_debug)
/* 350:    */           {
/* 351:547 */             List<String> outAndErr = ServerUtils.receiveDebugBuffer(this.m_localSocket.getOutputStream(), this.m_localSocket.getInputStream(), this.m_log, this.m_debug);
/* 352:550 */             if (((String)outAndErr.get(0)).length() > 0) {
/* 353:551 */               System.err.println("Python debug std out:\n" + (String)outAndErr.get(0) + "\n");
/* 354:    */             }
/* 355:554 */             if (((String)outAndErr.get(1)).length() > 0) {
/* 356:555 */               System.err.println("Python debug std err:\n" + (String)outAndErr.get(1) + "\n");
/* 357:    */             }
/* 358:    */           }
/* 359:559 */           ServerUtils.sendServerShutdown(this.m_localSocket.getOutputStream());
/* 360:560 */           this.m_localSocket.close();
/* 361:561 */           if (this.m_serverProcess != null) {
/* 362:562 */             this.m_serverProcess.destroy();
/* 363:    */           }
/* 364:    */         }
/* 365:566 */         if (this.m_serverSocket != null) {
/* 366:567 */           this.m_serverSocket.close();
/* 367:    */         }
/* 368:569 */         s_sessionSingleton = null;
/* 369:    */       }
/* 370:    */       catch (Exception ex)
/* 371:    */       {
/* 372:571 */         ex.printStackTrace();
/* 373:    */       }
/* 374:    */     }
/* 375:    */   }
/* 376:    */   
/* 377:    */   public static boolean initSession(String pythonCommand, boolean debug)
/* 378:    */     throws WekaException
/* 379:    */   {
/* 380:588 */     if (s_sessionSingleton != null) {
/* 381:589 */       throw new WekaException("The python environment is already available!");
/* 382:    */     }
/* 383:    */     try
/* 384:    */     {
/* 385:593 */       new PythonSession(pythonCommand, debug);
/* 386:    */     }
/* 387:    */     catch (IOException ex)
/* 388:    */     {
/* 389:595 */       throw new WekaException(ex);
/* 390:    */     }
/* 391:598 */     return s_pythonEnvCheckResults.length() < 5;
/* 392:    */   }
/* 393:    */   
/* 394:    */   public static String getPythonEnvCheckResults()
/* 395:    */   {
/* 396:607 */     return s_pythonEnvCheckResults;
/* 397:    */   }
/* 398:    */   
/* 399:    */   public static void main(String[] args)
/* 400:    */   {
/* 401:    */     try
/* 402:    */     {
/* 403:617 */       if (!initSession("python", true))
/* 404:    */       {
/* 405:618 */         System.err.println("Initialization failed!");
/* 406:619 */         System.exit(1);
/* 407:    */       }
/* 408:622 */       String temp = "";
/* 409:623 */       PythonSession session = acquireSession(temp);
/* 410:    */       
/* 411:    */ 
/* 412:    */ 
/* 413:    */ 
/* 414:628 */       String script = "from sklearn import datasets\nfrom pandas import DataFrame\ndiabetes = datasets.load_diabetes()\ndd = DataFrame(diabetes.data)\n";
/* 415:    */       
/* 416:    */ 
/* 417:631 */       session.executeScript(script, true);
/* 418:    */       
/* 419:633 */       script = "def foo():\n\treturn 100\n\nx = foo()\n";
/* 420:634 */       session.executeScript(script, true);
/* 421:    */       
/* 422:    */ 
/* 423:637 */       List<String[]> vars = session.getVariableListFromPython(true);
/* 424:638 */       for (String[] v : vars) {
/* 425:639 */         System.err.println(v[0] + ":" + v[1]);
/* 426:    */       }
/* 427:642 */       Object result = session.getVariableValueFromPythonAsJson("x", true);
/* 428:643 */       System.err.println("Value of x: " + result.toString());
/* 429:    */     }
/* 430:    */     catch (Exception ex)
/* 431:    */     {
/* 432:645 */       ex.printStackTrace();
/* 433:    */     }
/* 434:    */   }
/* 435:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.python.PythonSession
 * JD-Core Version:    0.7.0.1
 */