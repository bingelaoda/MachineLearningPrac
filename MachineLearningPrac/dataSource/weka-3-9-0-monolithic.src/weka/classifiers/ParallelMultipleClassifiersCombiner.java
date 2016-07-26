/*   1:    */ package weka.classifiers;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import java.util.concurrent.LinkedBlockingQueue;
/*   8:    */ import java.util.concurrent.ThreadPoolExecutor;
/*   9:    */ import java.util.concurrent.TimeUnit;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.Option;
/*  12:    */ import weka.core.Utils;
/*  13:    */ 
/*  14:    */ public abstract class ParallelMultipleClassifiersCombiner
/*  15:    */   extends MultipleClassifiersCombiner
/*  16:    */ {
/*  17:    */   private static final long serialVersionUID = 728109028953726626L;
/*  18: 50 */   protected int m_numExecutionSlots = 1;
/*  19:    */   protected transient ThreadPoolExecutor m_executorPool;
/*  20:    */   protected int m_completed;
/*  21:    */   protected int m_failed;
/*  22:    */   
/*  23:    */   public Enumeration<Option> listOptions()
/*  24:    */   {
/*  25: 71 */     Vector<Option> newVector = new Vector(1);
/*  26:    */     
/*  27: 73 */     newVector.addElement(new Option("\tNumber of execution slots.\n\t(default 1 - i.e. no parallelism)", "num-slots", 1, "-num-slots <num>"));
/*  28:    */     
/*  29:    */ 
/*  30:    */ 
/*  31:    */ 
/*  32: 78 */     newVector.addAll(Collections.list(super.listOptions()));
/*  33:    */     
/*  34: 80 */     return newVector.elements();
/*  35:    */   }
/*  36:    */   
/*  37:    */   public void setOptions(String[] options)
/*  38:    */     throws Exception
/*  39:    */   {
/*  40: 96 */     String iterations = Utils.getOption("num-slots", options);
/*  41: 97 */     if (iterations.length() != 0) {
/*  42: 98 */       setNumExecutionSlots(Integer.parseInt(iterations));
/*  43:    */     } else {
/*  44:100 */       setNumExecutionSlots(1);
/*  45:    */     }
/*  46:103 */     super.setOptions(options);
/*  47:    */   }
/*  48:    */   
/*  49:    */   public String[] getOptions()
/*  50:    */   {
/*  51:113 */     Vector<String> options = new Vector();
/*  52:    */     
/*  53:115 */     options.add("-num-slots");
/*  54:116 */     options.add("" + getNumExecutionSlots());
/*  55:    */     
/*  56:118 */     Collections.addAll(options, super.getOptions());
/*  57:    */     
/*  58:120 */     return (String[])options.toArray(new String[0]);
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void setNumExecutionSlots(int numSlots)
/*  62:    */   {
/*  63:130 */     this.m_numExecutionSlots = numSlots;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public int getNumExecutionSlots()
/*  67:    */   {
/*  68:140 */     return this.m_numExecutionSlots;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public String numExecutionSlotsTipText()
/*  72:    */   {
/*  73:149 */     return "The number of execution slots (threads) to use for constructing the ensemble.";
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void buildClassifier(Instances data)
/*  77:    */     throws Exception
/*  78:    */   {
/*  79:161 */     if (this.m_numExecutionSlots < 1) {
/*  80:162 */       throw new Exception("Number of execution slots needs to be >= 1!");
/*  81:    */     }
/*  82:165 */     if (this.m_numExecutionSlots > 1)
/*  83:    */     {
/*  84:166 */       if (this.m_Debug) {
/*  85:167 */         System.out.println("Starting executor pool with " + this.m_numExecutionSlots + " slots...");
/*  86:    */       }
/*  87:170 */       startExecutorPool();
/*  88:    */     }
/*  89:172 */     this.m_completed = 0;
/*  90:173 */     this.m_failed = 0;
/*  91:    */   }
/*  92:    */   
/*  93:    */   protected void startExecutorPool()
/*  94:    */   {
/*  95:180 */     if (this.m_executorPool != null) {
/*  96:181 */       this.m_executorPool.shutdownNow();
/*  97:    */     }
/*  98:184 */     this.m_executorPool = new ThreadPoolExecutor(this.m_numExecutionSlots, this.m_numExecutionSlots, 120L, TimeUnit.SECONDS, new LinkedBlockingQueue());
/*  99:    */   }
/* 100:    */   
/* 101:    */   private synchronized void block(boolean tf)
/* 102:    */   {
/* 103:189 */     if (tf) {
/* 104:    */       try
/* 105:    */       {
/* 106:191 */         if ((this.m_numExecutionSlots > 1) && (this.m_completed + this.m_failed < this.m_Classifiers.length)) {
/* 107:192 */           wait();
/* 108:    */         }
/* 109:    */       }
/* 110:    */       catch (InterruptedException ex) {}
/* 111:    */     } else {
/* 112:197 */       notifyAll();
/* 113:    */     }
/* 114:    */   }
/* 115:    */   
/* 116:    */   protected synchronized void buildClassifiers(final Instances data)
/* 117:    */     throws Exception
/* 118:    */   {
/* 119:209 */     for (int i = 0; i < this.m_Classifiers.length; i++) {
/* 120:210 */       if (this.m_numExecutionSlots > 1)
/* 121:    */       {
/* 122:211 */         final Classifier currentClassifier = this.m_Classifiers[i];
/* 123:212 */         final int iteration = i;
/* 124:213 */         Runnable newTask = new Runnable()
/* 125:    */         {
/* 126:    */           public void run()
/* 127:    */           {
/* 128:    */             try
/* 129:    */             {
/* 130:216 */               if (ParallelMultipleClassifiersCombiner.this.m_Debug) {
/* 131:217 */                 System.out.println("Training classifier (" + (iteration + 1) + ")");
/* 132:    */               }
/* 133:219 */               currentClassifier.buildClassifier(data);
/* 134:220 */               if (ParallelMultipleClassifiersCombiner.this.m_Debug) {
/* 135:221 */                 System.out.println("Finished classifier (" + (iteration + 1) + ")");
/* 136:    */               }
/* 137:223 */               ParallelMultipleClassifiersCombiner.this.completedClassifier(iteration, true);
/* 138:    */             }
/* 139:    */             catch (Exception ex)
/* 140:    */             {
/* 141:225 */               ex.printStackTrace();
/* 142:226 */               ParallelMultipleClassifiersCombiner.this.completedClassifier(iteration, false);
/* 143:    */             }
/* 144:    */           }
/* 145:231 */         };
/* 146:232 */         this.m_executorPool.execute(newTask);
/* 147:    */       }
/* 148:    */       else
/* 149:    */       {
/* 150:234 */         this.m_Classifiers[i].buildClassifier(data);
/* 151:    */       }
/* 152:    */     }
/* 153:238 */     if ((this.m_numExecutionSlots > 1) && (this.m_completed + this.m_failed < this.m_Classifiers.length)) {
/* 154:239 */       block(true);
/* 155:    */     }
/* 156:    */   }
/* 157:    */   
/* 158:    */   protected synchronized void completedClassifier(int iteration, boolean success)
/* 159:    */   {
/* 160:253 */     if (!success)
/* 161:    */     {
/* 162:254 */       this.m_failed += 1;
/* 163:255 */       if (this.m_Debug) {
/* 164:256 */         System.err.println("Iteration " + iteration + " failed!");
/* 165:    */       }
/* 166:    */     }
/* 167:    */     else
/* 168:    */     {
/* 169:259 */       this.m_completed += 1;
/* 170:    */     }
/* 171:262 */     if (this.m_completed + this.m_failed == this.m_Classifiers.length)
/* 172:    */     {
/* 173:263 */       if ((this.m_failed > 0) && 
/* 174:264 */         (this.m_Debug)) {
/* 175:265 */         System.err.println("Problem building classifiers - some iterations failed.");
/* 176:    */       }
/* 177:272 */       this.m_executorPool.shutdown();
/* 178:273 */       block(false);
/* 179:    */     }
/* 180:    */   }
/* 181:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.ParallelMultipleClassifiersCombiner
 * JD-Core Version:    0.7.0.1
 */