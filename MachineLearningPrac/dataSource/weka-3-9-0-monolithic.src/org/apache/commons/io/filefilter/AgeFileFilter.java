/*   1:    */ package org.apache.commons.io.filefilter;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.util.Date;
/*   5:    */ import org.apache.commons.io.FileUtils;
/*   6:    */ 
/*   7:    */ public class AgeFileFilter
/*   8:    */   extends AbstractFileFilter
/*   9:    */ {
/*  10:    */   private long cutoff;
/*  11:    */   private boolean acceptOlder;
/*  12:    */   
/*  13:    */   public AgeFileFilter(long cutoff)
/*  14:    */   {
/*  15: 59 */     this(cutoff, true);
/*  16:    */   }
/*  17:    */   
/*  18:    */   public AgeFileFilter(long cutoff, boolean acceptOlder)
/*  19:    */   {
/*  20: 71 */     this.acceptOlder = acceptOlder;
/*  21: 72 */     this.cutoff = cutoff;
/*  22:    */   }
/*  23:    */   
/*  24:    */   public AgeFileFilter(Date cutoffDate)
/*  25:    */   {
/*  26: 82 */     this(cutoffDate, true);
/*  27:    */   }
/*  28:    */   
/*  29:    */   public AgeFileFilter(Date cutoffDate, boolean acceptOlder)
/*  30:    */   {
/*  31: 94 */     this(cutoffDate.getTime(), acceptOlder);
/*  32:    */   }
/*  33:    */   
/*  34:    */   public AgeFileFilter(File cutoffReference)
/*  35:    */   {
/*  36:105 */     this(cutoffReference, true);
/*  37:    */   }
/*  38:    */   
/*  39:    */   public AgeFileFilter(File cutoffReference, boolean acceptOlder)
/*  40:    */   {
/*  41:119 */     this(cutoffReference.lastModified(), acceptOlder);
/*  42:    */   }
/*  43:    */   
/*  44:    */   public boolean accept(File file)
/*  45:    */   {
/*  46:136 */     boolean newer = FileUtils.isFileNewer(file, this.cutoff);
/*  47:137 */     return this.acceptOlder ? false : !newer ? true : newer;
/*  48:    */   }
/*  49:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.filefilter.AgeFileFilter
 * JD-Core Version:    0.7.0.1
 */