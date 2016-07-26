/*   1:    */ package weka.classifiers;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import java.util.concurrent.CountDownLatch;
/*   8:    */ import java.util.concurrent.ExecutorService;
/*   9:    */ import java.util.concurrent.Executors;
/*  10:    */ import java.util.concurrent.atomic.AtomicInteger;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.Option;
/*  13:    */ import weka.core.Utils;
/*  14:    */ 
/*  15:    */ public abstract class ParallelIteratedSingleClassifierEnhancer
/*  16:    */   extends IteratedSingleClassifierEnhancer
/*  17:    */ {
/*  18:    */   private static final long serialVersionUID = -5026378741833046436L;
/*  19: 51 */   protected int m_numExecutionSlots = 1;
/*  20:    */   
/*  21:    */   public Enumeration<Option> listOptions()
/*  22:    */   {
/*  23: 61 */     Vector<Option> newVector = new Vector(2);
/*  24:    */     
/*  25: 63 */     newVector.addElement(new Option("\tNumber of execution slots.\n\t(default 1 - i.e. no parallelism)\n\t(use 0 to auto-detect number of cores)", "num-slots", 1, "-num-slots <num>"));
/*  26:    */     
/*  27:    */ 
/*  28:    */ 
/*  29:    */ 
/*  30: 68 */     newVector.addAll(Collections.list(super.listOptions()));
/*  31:    */     
/*  32: 70 */     return newVector.elements();
/*  33:    */   }
/*  34:    */   
/*  35:    */   public void setOptions(String[] options)
/*  36:    */     throws Exception
/*  37:    */   {
/*  38: 90 */     String iterations = Utils.getOption("num-slots", options);
/*  39: 91 */     if (iterations.length() != 0) {
/*  40: 92 */       setNumExecutionSlots(Integer.parseInt(iterations));
/*  41:    */     } else {
/*  42: 94 */       setNumExecutionSlots(1);
/*  43:    */     }
/*  44: 97 */     super.setOptions(options);
/*  45:    */   }
/*  46:    */   
/*  47:    */   public String[] getOptions()
/*  48:    */   {
/*  49:108 */     String[] superOptions = super.getOptions();
/*  50:109 */     String[] options = new String[superOptions.length + 2];
/*  51:    */     
/*  52:111 */     int current = 0;
/*  53:112 */     options[(current++)] = "-num-slots";
/*  54:113 */     options[(current++)] = ("" + getNumExecutionSlots());
/*  55:    */     
/*  56:115 */     System.arraycopy(superOptions, 0, options, current, superOptions.length);
/*  57:    */     
/*  58:117 */     return options;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void setNumExecutionSlots(int numSlots)
/*  62:    */   {
/*  63:127 */     this.m_numExecutionSlots = numSlots;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public int getNumExecutionSlots()
/*  67:    */   {
/*  68:137 */     return this.m_numExecutionSlots;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public String numExecutionSlotsTipText()
/*  72:    */   {
/*  73:147 */     return "The number of execution slots (threads) to use for constructing the ensemble.";
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void buildClassifier(Instances data)
/*  77:    */     throws Exception
/*  78:    */   {
/*  79:159 */     super.buildClassifier(data);
/*  80:161 */     if (this.m_numExecutionSlots < 0) {
/*  81:162 */       throw new Exception("Number of execution slots needs to be >= 0!");
/*  82:    */     }
/*  83:    */   }
/*  84:    */   
/*  85:    */   protected void buildClassifiers()
/*  86:    */     throws Exception
/*  87:    */   {
/*  88:177 */     if (this.m_numExecutionSlots != 1)
/*  89:    */     {
/*  90:179 */       int numCores = this.m_numExecutionSlots == 0 ? Runtime.getRuntime().availableProcessors() : this.m_numExecutionSlots;
/*  91:    */       
/*  92:    */ 
/*  93:182 */       ExecutorService executorPool = Executors.newFixedThreadPool(numCores);
/*  94:    */       
/*  95:184 */       final CountDownLatch doneSignal = new CountDownLatch(this.m_Classifiers.length);
/*  96:    */       
/*  97:186 */       final AtomicInteger numFailed = new AtomicInteger();
/*  98:188 */       for (int i = 0; i < this.m_Classifiers.length; i++)
/*  99:    */       {
/* 100:190 */         final Classifier currentClassifier = this.m_Classifiers[i];
/* 101:192 */         if (currentClassifier != null)
/* 102:    */         {
/* 103:194 */           final int iteration = i;
/* 104:196 */           if (this.m_Debug) {
/* 105:197 */             System.out.print("Training classifier (" + (i + 1) + ")");
/* 106:    */           }
/* 107:199 */           Runnable newTask = new Runnable()
/* 108:    */           {
/* 109:    */             public void run()
/* 110:    */             {
/* 111:    */               try
/* 112:    */               {
/* 113:203 */                 currentClassifier.buildClassifier(ParallelIteratedSingleClassifierEnhancer.this.getTrainingSet(iteration));
/* 114:    */               }
/* 115:    */               catch (Throwable ex)
/* 116:    */               {
/* 117:205 */                 ex.printStackTrace();
/* 118:206 */                 numFailed.incrementAndGet();
/* 119:207 */                 if (ParallelIteratedSingleClassifierEnhancer.this.m_Debug) {
/* 120:208 */                   System.err.println("Iteration " + iteration + " failed!");
/* 121:    */                 }
/* 122:    */               }
/* 123:    */               finally
/* 124:    */               {
/* 125:211 */                 doneSignal.countDown();
/* 126:    */               }
/* 127:    */             }
/* 128:215 */           };
/* 129:216 */           executorPool.submit(newTask);
/* 130:    */         }
/* 131:    */       }
/* 132:219 */       doneSignal.await();
/* 133:220 */       executorPool.shutdownNow();
/* 134:221 */       if ((this.m_Debug) && (numFailed.intValue() > 0)) {
/* 135:222 */         System.err.println("Problem building classifiers - some iterations failed.");
/* 136:    */       }
/* 137:    */     }
/* 138:    */     else
/* 139:    */     {
/* 140:228 */       for (int i = 0; i < this.m_Classifiers.length; i++) {
/* 141:229 */         this.m_Classifiers[i].buildClassifier(getTrainingSet(i));
/* 142:    */       }
/* 143:    */     }
/* 144:    */   }
/* 145:    */   
/* 146:    */   protected abstract Instances getTrainingSet(int paramInt)
/* 147:    */     throws Exception;
/* 148:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.ParallelIteratedSingleClassifierEnhancer
 * JD-Core Version:    0.7.0.1
 */