/*   1:    */ package org.apache.commons.compress.archivers.zip;
/*   2:    */ 
/*   3:    */ import java.io.Closeable;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.FileNotFoundException;
/*   6:    */ import java.io.IOException;
/*   7:    */ import java.io.InputStream;
/*   8:    */ import java.util.Queue;
/*   9:    */ import java.util.concurrent.ConcurrentLinkedQueue;
/*  10:    */ import org.apache.commons.compress.parallel.FileBasedScatterGatherBackingStore;
/*  11:    */ import org.apache.commons.compress.parallel.ScatterGatherBackingStore;
/*  12:    */ import org.apache.commons.compress.utils.BoundedInputStream;
/*  13:    */ 
/*  14:    */ public class ScatterZipOutputStream
/*  15:    */   implements Closeable
/*  16:    */ {
/*  17: 49 */   private final Queue<CompressedEntry> items = new ConcurrentLinkedQueue();
/*  18:    */   private final ScatterGatherBackingStore backingStore;
/*  19:    */   private final StreamCompressor streamCompressor;
/*  20:    */   
/*  21:    */   private static class CompressedEntry
/*  22:    */   {
/*  23:    */     final ZipArchiveEntryRequest zipArchiveEntryRequest;
/*  24:    */     final long crc;
/*  25:    */     final long compressedSize;
/*  26:    */     final long size;
/*  27:    */     
/*  28:    */     public CompressedEntry(ZipArchiveEntryRequest zipArchiveEntryRequest, long crc, long compressedSize, long size)
/*  29:    */     {
/*  30: 60 */       this.zipArchiveEntryRequest = zipArchiveEntryRequest;
/*  31: 61 */       this.crc = crc;
/*  32: 62 */       this.compressedSize = compressedSize;
/*  33: 63 */       this.size = size;
/*  34:    */     }
/*  35:    */     
/*  36:    */     public ZipArchiveEntry transferToArchiveEntry()
/*  37:    */     {
/*  38: 73 */       ZipArchiveEntry entry = this.zipArchiveEntryRequest.getZipArchiveEntry();
/*  39: 74 */       entry.setCompressedSize(this.compressedSize);
/*  40: 75 */       entry.setSize(this.size);
/*  41: 76 */       entry.setCrc(this.crc);
/*  42: 77 */       entry.setMethod(this.zipArchiveEntryRequest.getMethod());
/*  43: 78 */       return entry;
/*  44:    */     }
/*  45:    */   }
/*  46:    */   
/*  47:    */   public ScatterZipOutputStream(ScatterGatherBackingStore backingStore, StreamCompressor streamCompressor)
/*  48:    */   {
/*  49: 84 */     this.backingStore = backingStore;
/*  50: 85 */     this.streamCompressor = streamCompressor;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void addArchiveEntry(ZipArchiveEntryRequest zipArchiveEntryRequest)
/*  54:    */     throws IOException
/*  55:    */   {
/*  56: 95 */     InputStream payloadStream = zipArchiveEntryRequest.getPayloadStream();
/*  57:    */     try
/*  58:    */     {
/*  59: 97 */       this.streamCompressor.deflate(payloadStream, zipArchiveEntryRequest.getMethod());
/*  60:    */     }
/*  61:    */     finally
/*  62:    */     {
/*  63: 99 */       payloadStream.close();
/*  64:    */     }
/*  65:101 */     this.items.add(new CompressedEntry(zipArchiveEntryRequest, this.streamCompressor.getCrc32(), this.streamCompressor.getBytesWrittenForLastEntry(), this.streamCompressor.getBytesRead()));
/*  66:    */   }
/*  67:    */   
/*  68:    */   public void writeTo(ZipArchiveOutputStream target)
/*  69:    */     throws IOException
/*  70:    */   {
/*  71:112 */     this.backingStore.closeForWriting();
/*  72:113 */     InputStream data = this.backingStore.getInputStream();
/*  73:114 */     for (CompressedEntry compressedEntry : this.items)
/*  74:    */     {
/*  75:115 */       BoundedInputStream rawStream = new BoundedInputStream(data, compressedEntry.compressedSize);
/*  76:116 */       target.addRawArchiveEntry(compressedEntry.transferToArchiveEntry(), rawStream);
/*  77:117 */       rawStream.close();
/*  78:    */     }
/*  79:119 */     data.close();
/*  80:    */   }
/*  81:    */   
/*  82:    */   public void close()
/*  83:    */     throws IOException
/*  84:    */   {
/*  85:128 */     this.backingStore.close();
/*  86:    */   }
/*  87:    */   
/*  88:    */   public static ScatterZipOutputStream fileBased(File file)
/*  89:    */     throws FileNotFoundException
/*  90:    */   {
/*  91:139 */     return fileBased(file, -1);
/*  92:    */   }
/*  93:    */   
/*  94:    */   public static ScatterZipOutputStream fileBased(File file, int compressionLevel)
/*  95:    */     throws FileNotFoundException
/*  96:    */   {
/*  97:151 */     ScatterGatherBackingStore bs = new FileBasedScatterGatherBackingStore(file);
/*  98:152 */     StreamCompressor sc = StreamCompressor.create(compressionLevel, bs);
/*  99:153 */     return new ScatterZipOutputStream(bs, sc);
/* 100:    */   }
/* 101:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.ScatterZipOutputStream
 * JD-Core Version:    0.7.0.1
 */