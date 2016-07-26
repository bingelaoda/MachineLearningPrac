/*   1:    */ package org.apache.commons.compress.archivers.arj;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.util.Date;
/*   5:    */ import java.util.regex.Matcher;
/*   6:    */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*   7:    */ import org.apache.commons.compress.archivers.zip.ZipUtil;
/*   8:    */ 
/*   9:    */ public class ArjArchiveEntry
/*  10:    */   implements ArchiveEntry
/*  11:    */ {
/*  12:    */   private final LocalFileHeader localFileHeader;
/*  13:    */   
/*  14:    */   public ArjArchiveEntry()
/*  15:    */   {
/*  16: 37 */     this.localFileHeader = new LocalFileHeader();
/*  17:    */   }
/*  18:    */   
/*  19:    */   ArjArchiveEntry(LocalFileHeader localFileHeader)
/*  20:    */   {
/*  21: 41 */     this.localFileHeader = localFileHeader;
/*  22:    */   }
/*  23:    */   
/*  24:    */   public String getName()
/*  25:    */   {
/*  26: 50 */     if ((this.localFileHeader.arjFlags & 0x10) != 0) {
/*  27: 51 */       return this.localFileHeader.name.replaceAll("/", Matcher.quoteReplacement(File.separator));
/*  28:    */     }
/*  29: 54 */     return this.localFileHeader.name;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public long getSize()
/*  33:    */   {
/*  34: 64 */     return this.localFileHeader.originalSize;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public boolean isDirectory()
/*  38:    */   {
/*  39: 72 */     return this.localFileHeader.fileType == 3;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public Date getLastModifiedDate()
/*  43:    */   {
/*  44: 91 */     long ts = isHostOsUnix() ? this.localFileHeader.dateTimeModified * 1000L : ZipUtil.dosToJavaTime(0xFFFFFFFF & this.localFileHeader.dateTimeModified);
/*  45:    */     
/*  46: 93 */     return new Date(ts);
/*  47:    */   }
/*  48:    */   
/*  49:    */   public int getMode()
/*  50:    */   {
/*  51:104 */     return this.localFileHeader.fileAccessMode;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public int getUnixMode()
/*  55:    */   {
/*  56:115 */     return isHostOsUnix() ? getMode() : 0;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public int getHostOs()
/*  60:    */   {
/*  61:124 */     return this.localFileHeader.hostOS;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public boolean isHostOsUnix()
/*  65:    */   {
/*  66:134 */     return (getHostOs() == 2) || (getHostOs() == 8);
/*  67:    */   }
/*  68:    */   
/*  69:    */   int getMethod()
/*  70:    */   {
/*  71:138 */     return this.localFileHeader.method;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public static class HostOs
/*  75:    */   {
/*  76:    */     public static final int DOS = 0;
/*  77:    */     public static final int PRIMOS = 1;
/*  78:    */     public static final int UNIX = 2;
/*  79:    */     public static final int AMIGA = 3;
/*  80:    */     public static final int MAC_OS = 4;
/*  81:    */     public static final int OS_2 = 5;
/*  82:    */     public static final int APPLE_GS = 6;
/*  83:    */     public static final int ATARI_ST = 7;
/*  84:    */     public static final int NEXT = 8;
/*  85:    */     public static final int VAX_VMS = 9;
/*  86:    */     public static final int WIN95 = 10;
/*  87:    */     public static final int WIN32 = 11;
/*  88:    */   }
/*  89:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.arj.ArjArchiveEntry
 * JD-Core Version:    0.7.0.1
 */