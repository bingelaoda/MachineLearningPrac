/*   1:    */ package org.boon;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.OutputStreamWriter;
/*   6:    */ import java.io.PrintWriter;
/*   7:    */ import java.nio.file.Files;
/*   8:    */ import java.nio.file.LinkOption;
/*   9:    */ import java.nio.file.Path;
/*  10:    */ import java.util.ArrayList;
/*  11:    */ import java.util.List;
/*  12:    */ import java.util.Map;
/*  13:    */ import java.util.Scanner;
/*  14:    */ import java.util.concurrent.ArrayBlockingQueue;
/*  15:    */ import java.util.concurrent.BlockingQueue;
/*  16:    */ import java.util.concurrent.ExecutorService;
/*  17:    */ import java.util.concurrent.Executors;
/*  18:    */ import java.util.concurrent.Future;
/*  19:    */ import java.util.concurrent.ScheduledExecutorService;
/*  20:    */ import java.util.concurrent.ScheduledFuture;
/*  21:    */ import java.util.concurrent.TimeUnit;
/*  22:    */ import java.util.concurrent.atomic.AtomicBoolean;
/*  23:    */ 
/*  24:    */ public class Runner
/*  25:    */ {
/*  26:    */   private static final String shell;
/*  27:    */   private static final String shellArgument;
/*  28:    */   
/*  29:    */   static
/*  30:    */   {
/*  31: 59 */     if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0)
/*  32:    */     {
/*  33: 60 */       shell = "cmd.exe";
/*  34: 61 */       shellArgument = "/C";
/*  35:    */     }
/*  36:    */     else
/*  37:    */     {
/*  38: 66 */       shell = "/bin/sh";
/*  39: 67 */       shellArgument = "-c";
/*  40:    */     }
/*  41:    */   }
/*  42:    */   
/*  43:    */   public static List<Path> path()
/*  44:    */   {
/*  45: 73 */     String[] paths = StringScanner.splitByDelimiters((String)System.getenv().get("PATH"), ":;");
/*  46: 74 */     return Lists.mapBy(paths, IO.convertToPathFunction);
/*  47:    */   }
/*  48:    */   
/*  49:    */   public static int exec(String... args)
/*  50:    */   {
/*  51: 79 */     ProcessRunner runner = new ProcessRunner(null, null, 0, null, false, args);
/*  52: 80 */     return runner.exec();
/*  53:    */   }
/*  54:    */   
/*  55:    */   public static int exec(int timeout, String... args)
/*  56:    */   {
/*  57: 84 */     ProcessRunner runner = new ProcessRunner(null, null, timeout, null, false, args);
/*  58: 85 */     return runner.exec();
/*  59:    */   }
/*  60:    */   
/*  61:    */   public static String run(int timeout, String... args)
/*  62:    */   {
/*  63: 91 */     return run(timeout, null, args);
/*  64:    */   }
/*  65:    */   
/*  66:    */   public static String runAt(String cwd, int timeout, String... args)
/*  67:    */   {
/*  68: 97 */     return runAt(cwd, timeout, null, args);
/*  69:    */   }
/*  70:    */   
/*  71:    */   public static String run(int timeout, List<Path> path, String... args)
/*  72:    */   {
/*  73:102 */     return doRun(timeout, path, false, args);
/*  74:    */   }
/*  75:    */   
/*  76:    */   public static String runAt(String cwd, int timeout, List<Path> path, String... args)
/*  77:    */   {
/*  78:107 */     return doRunAt(cwd, timeout, path, false, args);
/*  79:    */   }
/*  80:    */   
/*  81:    */   public static ProcessOut runProcess(int timeout, List<Path> path, boolean verbose, String... args)
/*  82:    */   {
/*  83:111 */     return runProcessAt(null, timeout, path, verbose, args);
/*  84:    */   }
/*  85:    */   
/*  86:    */   public static ProcessOut runProcessAt(String cwd, int timeout, List<Path> path, boolean verbose, String... args)
/*  87:    */   {
/*  88:117 */     ProcessOut out = new ProcessOut();
/*  89:118 */     ProcessRunner runner = new ProcessRunner(null, null, timeout, path, verbose, args);
/*  90:119 */     runner.cwd = cwd;
/*  91:120 */     out.exit = runner.exec();
/*  92:121 */     out.stdout = runner.stdOut();
/*  93:122 */     out.stderr = runner.stdErr();
/*  94:123 */     out.commandLine = Str.joinCollection(' ', runner.commandLine);
/*  95:124 */     return out;
/*  96:    */   }
/*  97:    */   
/*  98:    */   private static String doRun(int timeout, List<Path> path, boolean verbose, String... args)
/*  99:    */   {
/* 100:131 */     ProcessOut out = runProcess(timeout, path, verbose, args);
/* 101:132 */     if (out.getExit() != 0) {
/* 102:133 */       throw new ProcessException(Boon.sputs(new Object[] { "EXIT CODE", Integer.valueOf(out.getExit()), out.getStderr() }));
/* 103:    */     }
/* 104:135 */     return out.getStdout();
/* 105:    */   }
/* 106:    */   
/* 107:    */   private static String doRunAt(String cwd, int timeout, List<Path> path, boolean verbose, String... args)
/* 108:    */   {
/* 109:145 */     ProcessOut out = runProcessAt(cwd, timeout, path, verbose, args);
/* 110:146 */     if (out.getExit() != 0) {
/* 111:147 */       throw new ProcessException(Boon.sputs(new Object[] { "EXIT CODE", Integer.valueOf(out.getExit()), out.getStderr() }));
/* 112:    */     }
/* 113:149 */     return out.getStdout();
/* 114:    */   }
/* 115:    */   
/* 116:    */   public static ProcessInOut launchProcess(int timeout, List<Path> path, boolean verbose, String... args)
/* 117:    */   {
/* 118:157 */     ProcessInOut process = new ProcessInOut();
/* 119:158 */     process.run(timeout, path, verbose, args);
/* 120:    */     
/* 121:160 */     return process;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public static String run(String... args)
/* 125:    */   {
/* 126:165 */     return run(0, args);
/* 127:    */   }
/* 128:    */   
/* 129:    */   public static String runAt(String cwd, String... args)
/* 130:    */   {
/* 131:170 */     return runAt(cwd, 0, args);
/* 132:    */   }
/* 133:    */   
/* 134:    */   public static String runShell(String... args)
/* 135:    */   {
/* 136:176 */     List<String> list = new ArrayList(args.length + 2);
/* 137:177 */     list.add(shell);
/* 138:178 */     list.add(shellArgument);
/* 139:179 */     for (String arg : args) {
/* 140:180 */       list.add(arg);
/* 141:    */     }
/* 142:183 */     return run(0, (String[])list.toArray(new String[list.size()]));
/* 143:    */   }
/* 144:    */   
/* 145:    */   public static String runShell(int timeout, String... args)
/* 146:    */   {
/* 147:188 */     List<String> list = new ArrayList(args.length + 2);
/* 148:189 */     list.add(shell);
/* 149:190 */     list.add(shellArgument);
/* 150:191 */     for (String arg : args) {
/* 151:192 */       list.add(arg);
/* 152:    */     }
/* 153:195 */     return run(timeout, (String[])list.toArray(new String[list.size()]));
/* 154:    */   }
/* 155:    */   
/* 156:    */   public static int execShell(String... args)
/* 157:    */   {
/* 158:201 */     List<String> list = new ArrayList(args.length + 2);
/* 159:202 */     list.add(shell);
/* 160:203 */     list.add(shellArgument);
/* 161:204 */     for (String arg : args) {
/* 162:205 */       list.add(arg);
/* 163:    */     }
/* 164:208 */     return exec(0, (String[])list.toArray(new String[list.size()]));
/* 165:    */   }
/* 166:    */   
/* 167:    */   public static int execShell(int timeout, String... args)
/* 168:    */   {
/* 169:214 */     List<String> list = new ArrayList(args.length + 2);
/* 170:215 */     list.add(shell);
/* 171:216 */     list.add(shellArgument);
/* 172:217 */     for (String arg : args) {
/* 173:218 */       list.add(arg);
/* 174:    */     }
/* 175:221 */     return exec(timeout, (String[])list.toArray(new String[list.size()]));
/* 176:    */   }
/* 177:    */   
/* 178:    */   public static class ProcessInOut
/* 179:    */   {
/* 180:    */     private Runner.ProcessRunner runner;
/* 181:    */     private Runner.ProcessOut out;
/* 182:230 */     private AtomicBoolean done = new AtomicBoolean(false);
/* 183:    */     private BlockingQueue<String> queueOut;
/* 184:    */     private BlockingQueue<String> queueErr;
/* 185:    */     private ExecutorService executorService;
/* 186:    */     
/* 187:    */     public ProcessInOut()
/* 188:    */     {
/* 189:240 */       this.queueOut = new ArrayBlockingQueue(100);
/* 190:241 */       this.queueErr = new ArrayBlockingQueue(100);
/* 191:    */     }
/* 192:    */     
/* 193:    */     public void run(int timeout, List<Path> path, boolean verbose, String... args)
/* 194:    */     {
/* 195:245 */       this.done.set(false);
/* 196:246 */       this.out = new Runner.ProcessOut();
/* 197:247 */       this.runner = new Runner.ProcessRunner(this, null, timeout, path, verbose, args);
/* 198:    */       
/* 199:249 */       this.executorService = Executors.newSingleThreadExecutor();
/* 200:    */       
/* 201:251 */       Runnable task = new Runnable()
/* 202:    */       {
/* 203:    */         public void run()
/* 204:    */         {
/* 205:255 */           Runner.ProcessInOut.this.out.exit = Runner.ProcessInOut.this.runner.exec();
/* 206:256 */           Runner.ProcessInOut.this.out.stdout = Runner.ProcessInOut.this.runner.stdOut();
/* 207:257 */           Runner.ProcessInOut.this.out.stderr = Runner.ProcessInOut.this.runner.stdErr();
/* 208:258 */           Runner.ProcessInOut.this.out.commandLine = Str.joinCollection(' ', Runner.ProcessInOut.access$700(Runner.ProcessInOut.this).commandLine);
/* 209:259 */           Runner.ProcessInOut.this.done.set(true);
/* 210:    */         }
/* 211:263 */       };
/* 212:264 */       this.executorService.submit(task);
/* 213:    */     }
/* 214:    */     
/* 215:    */     public boolean isDone()
/* 216:    */     {
/* 217:269 */       return this.done.get();
/* 218:    */     }
/* 219:    */     
/* 220:    */     public Runner.ProcessOut processOut()
/* 221:    */     {
/* 222:273 */       return this.out;
/* 223:    */     }
/* 224:    */     
/* 225:    */     public BlockingQueue<String> getStdOut()
/* 226:    */     {
/* 227:277 */       return this.queueOut;
/* 228:    */     }
/* 229:    */     
/* 230:    */     public BlockingQueue<String> getStdErr()
/* 231:    */     {
/* 232:281 */       return this.queueErr;
/* 233:    */     }
/* 234:    */     
/* 235:    */     public void kill()
/* 236:    */     {
/* 237:286 */       this.runner.process.destroy();
/* 238:    */     }
/* 239:    */   }
/* 240:    */   
/* 241:    */   public static class ProcessOut
/* 242:    */   {
/* 243:    */     private int exit;
/* 244:    */     private String stdout;
/* 245:    */     private String stderr;
/* 246:    */     private String commandLine;
/* 247:    */     
/* 248:    */     public int getExit()
/* 249:    */     {
/* 250:299 */       return this.exit;
/* 251:    */     }
/* 252:    */     
/* 253:    */     public String getStdout()
/* 254:    */     {
/* 255:304 */       return this.stdout;
/* 256:    */     }
/* 257:    */     
/* 258:    */     public String getStderr()
/* 259:    */     {
/* 260:309 */       return this.stderr;
/* 261:    */     }
/* 262:    */     
/* 263:    */     public String getCommandLine()
/* 264:    */     {
/* 265:313 */       return this.commandLine;
/* 266:    */     }
/* 267:    */     
/* 268:    */     public String toString()
/* 269:    */     {
/* 270:319 */       return "ProcessOut [\nexit=" + this.exit + ", \nstdout=" + this.stdout + ", \nstderr=" + this.stderr + ", \ncommandLine=" + this.commandLine + "\n]";
/* 271:    */     }
/* 272:    */   }
/* 273:    */   
/* 274:    */   private static void handle(Exception ex)
/* 275:    */   {
/* 276:327 */     throw new ProcessException(ex);
/* 277:    */   }
/* 278:    */   
/* 279:    */   public static class ProcessException
/* 280:    */     extends RuntimeException
/* 281:    */   {
/* 282:    */     public ProcessException() {}
/* 283:    */     
/* 284:    */     public ProcessException(String m, Throwable t)
/* 285:    */     {
/* 286:339 */       super(t);
/* 287:    */     }
/* 288:    */     
/* 289:    */     public ProcessException(String m)
/* 290:    */     {
/* 291:343 */       super();
/* 292:    */     }
/* 293:    */     
/* 294:    */     public ProcessException(Throwable t)
/* 295:    */     {
/* 296:347 */       super();
/* 297:    */     }
/* 298:    */   }
/* 299:    */   
/* 300:    */   public static class ProcessRunner
/* 301:    */   {
/* 302:    */     private List<String> commandLine;
/* 303:    */     private String password;
/* 304:    */     private List<Path> path;
/* 305:    */     private Runner.ProcessIODrainer fromProcessOutput;
/* 306:    */     private Runner.ProcessIODrainer fromProcessError;
/* 307:358 */     private int timeoutInSeconds = 0;
/* 308:    */     private boolean verbose;
/* 309:    */     private PrintWriter toProcess;
/* 310:    */     private Runner.ProcessInOut inout;
/* 311:    */     private Process process;
/* 312:    */     private ExecutorService executorService;
/* 313:    */     private ScheduledExecutorService scheduledExecutorService;
/* 314:    */     private String cwd;
/* 315:    */     
/* 316:    */     public ProcessRunner(Runner.ProcessInOut inout, String password, int timeoutInSeconds, List<Path> path, boolean verbose, String... cmdLine)
/* 317:    */     {
/* 318:378 */       if (timeoutInSeconds == 0) {
/* 319:379 */         timeoutInSeconds = 5;
/* 320:    */       }
/* 321:381 */       if (cmdLine.length == 1) {
/* 322:382 */         cmdLine = Str.split(cmdLine[0]);
/* 323:    */       }
/* 324:386 */       this.inout = inout;
/* 325:387 */       this.commandLine = Lists.list(cmdLine);
/* 326:388 */       this.password = password;
/* 327:389 */       this.timeoutInSeconds = timeoutInSeconds;
/* 328:390 */       this.path = path;
/* 329:391 */       this.verbose = verbose;
/* 330:394 */       if (this.path == null) {
/* 331:395 */         this.path = Runner.path();
/* 332:    */       }
/* 333:398 */       this.executorService = Executors.newFixedThreadPool(2);
/* 334:    */     }
/* 335:    */     
/* 336:    */     public int exec()
/* 337:    */       throws Runner.ProcessException
/* 338:    */     {
/* 339:403 */       int exit = -666;
/* 340:    */       
/* 341:405 */       initializePath();
/* 342:    */       
/* 343:    */ 
/* 344:408 */       ProcessBuilder processBuilder = new ProcessBuilder(this.commandLine);
/* 345:410 */       if (this.cwd != null) {
/* 346:411 */         processBuilder.directory(new File(this.cwd));
/* 347:    */       }
/* 348:414 */       String envPath = Str.joinCollection(File.pathSeparatorChar, this.path);
/* 349:415 */       processBuilder.environment().put("PATH", envPath);
/* 350:    */       try
/* 351:    */       {
/* 352:419 */         initializeDrainersScannersAndWriters(processBuilder);
/* 353:    */         
/* 354:    */ 
/* 355:422 */         Future<?> fromProcessErrorFuture = this.executorService.submit(this.fromProcessError);
/* 356:423 */         Future<?> fromProcessOutputFuture = this.executorService.submit(this.fromProcessOutput);
/* 357:426 */         if (this.timeoutInSeconds == -1) {
/* 358:427 */           exit = this.process.waitFor();
/* 359:    */         } else {
/* 360:431 */           exit = runWithTimeoutTimer(fromProcessErrorFuture, fromProcessOutputFuture);
/* 361:    */         }
/* 362:436 */         fromProcessErrorFuture.get();
/* 363:437 */         fromProcessOutputFuture.get();
/* 364:    */       }
/* 365:    */       catch (Exception e)
/* 366:    */       {
/* 367:441 */         Thread.interrupted();
/* 368:442 */         Runner.handle(e);
/* 369:    */       }
/* 370:444 */       return exit;
/* 371:    */     }
/* 372:    */     
/* 373:    */     private void initializePath()
/* 374:    */     {
/* 375:448 */       String cmd = (String)this.commandLine.get(0);
/* 376:449 */       Path pathCommand = IO.path(cmd);
/* 377:450 */       if (!Files.exists(pathCommand, new LinkOption[0])) {
/* 378:451 */         for (Path dir : this.path)
/* 379:    */         {
/* 380:452 */           pathCommand = IO.path(dir, new String[] { cmd });
/* 381:453 */           if (Files.exists(pathCommand, new LinkOption[0]))
/* 382:    */           {
/* 383:454 */             cmd = pathCommand.toAbsolutePath().toString();
/* 384:455 */             break;
/* 385:    */           }
/* 386:    */         }
/* 387:    */       }
/* 388:459 */       this.commandLine.set(0, cmd);
/* 389:    */     }
/* 390:    */     
/* 391:    */     private void initializeDrainersScannersAndWriters(ProcessBuilder processBuilder)
/* 392:    */       throws IOException
/* 393:    */     {
/* 394:464 */       this.process = processBuilder.start();
/* 395:    */       
/* 396:466 */       this.toProcess = new PrintWriter(new OutputStreamWriter(this.process.getOutputStream()));
/* 397:    */       
/* 398:468 */       Scanner stdOut = new Scanner(this.process.getInputStream());
/* 399:469 */       Scanner stdErr = new Scanner(this.process.getErrorStream());
/* 400:471 */       if (this.inout == null)
/* 401:    */       {
/* 402:472 */         this.fromProcessError = new Runner.ProcessIODrainer(stdErr, this.verbose);
/* 403:473 */         this.fromProcessOutput = new Runner.ProcessIODrainer(stdOut, this.toProcess, this.password, false, this.verbose);
/* 404:    */       }
/* 405:    */       else
/* 406:    */       {
/* 407:476 */         this.fromProcessError = new Runner.ProcessIODrainer(Runner.ProcessInOut.access$1100(this.inout), stdErr, this.verbose);
/* 408:477 */         this.fromProcessOutput = new Runner.ProcessIODrainer(Runner.ProcessInOut.access$1200(this.inout), stdOut, this.toProcess, this.password, false, this.verbose);
/* 409:    */       }
/* 410:    */     }
/* 411:    */     
/* 412:    */     private int runWithTimeoutTimer(final Future<?> fromProcessErrorFuture, final Future<?> fromProcessOutputFuture)
/* 413:    */       throws InterruptedException
/* 414:    */     {
/* 415:484 */       Runnable command = new Runnable()
/* 416:    */       {
/* 417:    */         public void run()
/* 418:    */         {
/* 419:488 */           Runner.ProcessRunner.this.process.destroy();
/* 420:489 */           fromProcessErrorFuture.cancel(true);
/* 421:490 */           fromProcessOutputFuture.cancel(true);
/* 422:    */         }
/* 423:494 */       };
/* 424:495 */       this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
/* 425:496 */       ScheduledFuture<?> scheduledFuture = this.scheduledExecutorService.scheduleWithFixedDelay(command, this.timeoutInSeconds, this.timeoutInSeconds, TimeUnit.SECONDS);
/* 426:497 */       int exit = this.process.waitFor();
/* 427:498 */       scheduledFuture.cancel(true);
/* 428:499 */       return exit;
/* 429:    */     }
/* 430:    */     
/* 431:    */     public String stdOut()
/* 432:    */     {
/* 433:503 */       return this.fromProcessOutput.getOutput();
/* 434:    */     }
/* 435:    */     
/* 436:    */     public String stdErr()
/* 437:    */     {
/* 438:507 */       return this.fromProcessError.getOutput();
/* 439:    */     }
/* 440:    */   }
/* 441:    */   
/* 442:    */   static class ProcessIODrainer
/* 443:    */     implements Runnable
/* 444:    */   {
/* 445:    */     private Scanner fromProcess;
/* 446:    */     private String password;
/* 447:    */     private PrintWriter toProcess;
/* 448:516 */     private StringBuilder outputBuffer = new StringBuilder(1024);
/* 449:    */     private boolean sudo;
/* 450:    */     private boolean verbose;
/* 451:    */     private BlockingQueue<String> queue;
/* 452:    */     
/* 453:    */     ProcessIODrainer(Scanner fromProcess, boolean verbose)
/* 454:    */     {
/* 455:522 */       this.fromProcess = fromProcess;
/* 456:523 */       this.verbose = verbose;
/* 457:    */     }
/* 458:    */     
/* 459:    */     ProcessIODrainer(BlockingQueue<String> queueOut, Scanner fromProcess, boolean verbose)
/* 460:    */     {
/* 461:527 */       this.queue = queueOut;
/* 462:528 */       this.fromProcess = fromProcess;
/* 463:529 */       this.verbose = verbose;
/* 464:    */     }
/* 465:    */     
/* 466:    */     ProcessIODrainer(Scanner fromProcess, PrintWriter toProcess, String password, boolean sudo, boolean verbose)
/* 467:    */     {
/* 468:534 */       this.sudo = sudo;
/* 469:535 */       this.fromProcess = fromProcess;
/* 470:536 */       this.toProcess = toProcess;
/* 471:537 */       this.verbose = verbose;
/* 472:538 */       this.password = password;
/* 473:    */     }
/* 474:    */     
/* 475:    */     public ProcessIODrainer(BlockingQueue<String> queueOut, Scanner fromProcess, PrintWriter toProcess, String password, boolean sudo, boolean verbose)
/* 476:    */     {
/* 477:543 */       this.queue = queueOut;
/* 478:544 */       this.sudo = sudo;
/* 479:545 */       this.fromProcess = fromProcess;
/* 480:546 */       this.toProcess = toProcess;
/* 481:547 */       this.verbose = verbose;
/* 482:548 */       this.password = password;
/* 483:    */     }
/* 484:    */     
/* 485:    */     public void run()
/* 486:    */     {
/* 487:552 */       if (this.sudo)
/* 488:    */       {
/* 489:    */         try
/* 490:    */         {
/* 491:554 */           Thread.sleep(100L);
/* 492:    */         }
/* 493:    */         catch (InterruptedException e)
/* 494:    */         {
/* 495:556 */           Thread.interrupted();
/* 496:    */         }
/* 497:558 */         this.toProcess.println(this.password);
/* 498:559 */         this.toProcess.flush();
/* 499:    */       }
/* 500:    */       try
/* 501:    */       {
/* 502:563 */         while (this.fromProcess.hasNextLine())
/* 503:    */         {
/* 504:564 */           String line = this.fromProcess.nextLine();
/* 505:566 */           if (this.queue != null) {
/* 506:    */             try
/* 507:    */             {
/* 508:569 */               this.queue.put(line);
/* 509:    */             }
/* 510:    */             catch (InterruptedException e)
/* 511:    */             {
/* 512:572 */               while (!Thread.currentThread().isInterrupted()) {}
/* 513:    */             }
/* 514:    */           }
/* 515:581 */           if (this.verbose) {
/* 516:582 */             Boon.puts(new Object[] { line });
/* 517:    */           }
/* 518:584 */           this.outputBuffer.append(line).append('\n');
/* 519:    */         }
/* 520:    */       }
/* 521:    */       finally
/* 522:    */       {
/* 523:588 */         this.fromProcess.close();
/* 524:    */       }
/* 525:    */     }
/* 526:    */     
/* 527:    */     public String getOutput()
/* 528:    */     {
/* 529:593 */       return this.outputBuffer.toString();
/* 530:    */     }
/* 531:    */   }
/* 532:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.Runner
 * JD-Core Version:    0.7.0.1
 */