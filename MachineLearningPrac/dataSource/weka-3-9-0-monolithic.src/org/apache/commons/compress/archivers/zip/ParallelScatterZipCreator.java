/*   1:    */ package org.apache.commons.compress.archivers.zip;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Collections;
/*   7:    */ import java.util.List;
/*   8:    */ import java.util.concurrent.Callable;
/*   9:    */ import java.util.concurrent.ExecutionException;
/*  10:    */ import java.util.concurrent.ExecutorService;
/*  11:    */ import java.util.concurrent.Executors;
/*  12:    */ import java.util.concurrent.Future;
/*  13:    */ import java.util.concurrent.TimeUnit;
/*  14:    */ import java.util.concurrent.atomic.AtomicInteger;
/*  15:    */ import org.apache.commons.compress.parallel.FileBasedScatterGatherBackingStore;
/*  16:    */ import org.apache.commons.compress.parallel.InputStreamSupplier;
/*  17:    */ import org.apache.commons.compress.parallel.ScatterGatherBackingStore;
/*  18:    */ import org.apache.commons.compress.parallel.ScatterGatherBackingStoreSupplier;
/*  19:    */ 
/*  20:    */ public class ParallelScatterZipCreator
/*  21:    */ {
/*  22: 55 */   private final List<ScatterZipOutputStream> streams = Collections.synchronizedList(new ArrayList());
/*  23:    */   private final ExecutorService es;
/*  24:    */   private final ScatterGatherBackingStoreSupplier backingStoreSupplier;
/*  25: 58 */   private final List<Future<Object>> futures = new ArrayList();
/*  26: 60 */   private final long startedAt = System.currentTimeMillis();
/*  27: 61 */   private long compressionDoneAt = 0L;
/*  28:    */   private long scatterDoneAt;
/*  29:    */   
/*  30:    */   private static class DefaultBackingStoreSupplier
/*  31:    */     implements ScatterGatherBackingStoreSupplier
/*  32:    */   {
/*  33: 65 */     final AtomicInteger storeNum = new AtomicInteger(0);
/*  34:    */     
/*  35:    */     public ScatterGatherBackingStore get()
/*  36:    */       throws IOException
/*  37:    */     {
/*  38: 68 */       File tempFile = File.createTempFile("parallelscatter", "n" + this.storeNum.incrementAndGet());
/*  39: 69 */       return new FileBasedScatterGatherBackingStore(tempFile);
/*  40:    */     }
/*  41:    */   }
/*  42:    */   
/*  43:    */   private ScatterZipOutputStream createDeferred(ScatterGatherBackingStoreSupplier scatterGatherBackingStoreSupplier)
/*  44:    */     throws IOException
/*  45:    */   {
/*  46: 75 */     ScatterGatherBackingStore bs = scatterGatherBackingStoreSupplier.get();
/*  47: 76 */     StreamCompressor sc = StreamCompressor.create(-1, bs);
/*  48: 77 */     return new ScatterZipOutputStream(bs, sc);
/*  49:    */   }
/*  50:    */   
/*  51: 80 */   private final ThreadLocal<ScatterZipOutputStream> tlScatterStreams = new ThreadLocal()
/*  52:    */   {
/*  53:    */     protected ScatterZipOutputStream initialValue()
/*  54:    */     {
/*  55:    */       try
/*  56:    */       {
/*  57: 84 */         ScatterZipOutputStream scatterStream = ParallelScatterZipCreator.this.createDeferred(ParallelScatterZipCreator.this.backingStoreSupplier);
/*  58: 85 */         ParallelScatterZipCreator.this.streams.add(scatterStream);
/*  59: 86 */         return scatterStream;
/*  60:    */       }
/*  61:    */       catch (IOException e)
/*  62:    */       {
/*  63: 88 */         throw new RuntimeException(e);
/*  64:    */       }
/*  65:    */     }
/*  66:    */   };
/*  67:    */   
/*  68:    */   public ParallelScatterZipCreator()
/*  69:    */   {
/*  70: 98 */     this(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
/*  71:    */   }
/*  72:    */   
/*  73:    */   public ParallelScatterZipCreator(ExecutorService executorService)
/*  74:    */   {
/*  75:108 */     this(executorService, new DefaultBackingStoreSupplier(null));
/*  76:    */   }
/*  77:    */   
/*  78:    */   public ParallelScatterZipCreator(ExecutorService executorService, ScatterGatherBackingStoreSupplier backingStoreSupplier)
/*  79:    */   {
/*  80:120 */     this.backingStoreSupplier = backingStoreSupplier;
/*  81:121 */     this.es = executorService;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void addArchiveEntry(ZipArchiveEntry zipArchiveEntry, InputStreamSupplier source)
/*  85:    */   {
/*  86:135 */     submit(createCallable(zipArchiveEntry, source));
/*  87:    */   }
/*  88:    */   
/*  89:    */   public final void submit(Callable<Object> callable)
/*  90:    */   {
/*  91:146 */     this.futures.add(this.es.submit(callable));
/*  92:    */   }
/*  93:    */   
/*  94:    */   public final Callable<Object> createCallable(ZipArchiveEntry zipArchiveEntry, InputStreamSupplier source)
/*  95:    */   {
/*  96:168 */     int method = zipArchiveEntry.getMethod();
/*  97:169 */     if (method == -1) {
/*  98:170 */       throw new IllegalArgumentException("Method must be set on zipArchiveEntry: " + zipArchiveEntry);
/*  99:    */     }
/* 100:172 */     final ZipArchiveEntryRequest zipArchiveEntryRequest = ZipArchiveEntryRequest.createZipArchiveEntryRequest(zipArchiveEntry, source);
/* 101:173 */     new Callable()
/* 102:    */     {
/* 103:    */       public Object call()
/* 104:    */         throws Exception
/* 105:    */       {
/* 106:175 */         ((ScatterZipOutputStream)ParallelScatterZipCreator.this.tlScatterStreams.get()).addArchiveEntry(zipArchiveEntryRequest);
/* 107:176 */         return null;
/* 108:    */       }
/* 109:    */     };
/* 110:    */   }
/* 111:    */   
/* 112:    */   public void writeTo(ZipArchiveOutputStream targetStream)
/* 113:    */     throws IOException, InterruptedException, ExecutionException
/* 114:    */   {
/* 115:198 */     for (Future<?> future : this.futures) {
/* 116:199 */       future.get();
/* 117:    */     }
/* 118:202 */     this.es.shutdown();
/* 119:203 */     this.es.awaitTermination(60000L, TimeUnit.SECONDS);
/* 120:    */     
/* 121:    */ 
/* 122:206 */     this.compressionDoneAt = System.currentTimeMillis();
/* 123:208 */     for (ScatterZipOutputStream scatterStream : this.streams)
/* 124:    */     {
/* 125:209 */       scatterStream.writeTo(targetStream);
/* 126:210 */       scatterStream.close();
/* 127:    */     }
/* 128:213 */     this.scatterDoneAt = System.currentTimeMillis();
/* 129:    */   }
/* 130:    */   
/* 131:    */   public ScatterStatistics getStatisticsMessage()
/* 132:    */   {
/* 133:222 */     return new ScatterStatistics(this.compressionDoneAt - this.startedAt, this.scatterDoneAt - this.compressionDoneAt);
/* 134:    */   }
/* 135:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.ParallelScatterZipCreator
 * JD-Core Version:    0.7.0.1
 */