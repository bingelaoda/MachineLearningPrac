/*   1:    */ package org.apache.commons.compress.archivers.arj;
/*   2:    */ 
/*   3:    */ import java.util.Arrays;
/*   4:    */ 
/*   5:    */ class LocalFileHeader
/*   6:    */ {
/*   7:    */   int archiverVersionNumber;
/*   8:    */   int minVersionToExtract;
/*   9:    */   int hostOS;
/*  10:    */   int arjFlags;
/*  11:    */   int method;
/*  12:    */   int fileType;
/*  13:    */   int reserved;
/*  14:    */   int dateTimeModified;
/*  15:    */   long compressedSize;
/*  16:    */   long originalSize;
/*  17:    */   long originalCrc32;
/*  18:    */   int fileSpecPosition;
/*  19:    */   int fileAccessMode;
/*  20:    */   int firstChapter;
/*  21:    */   int lastChapter;
/*  22:    */   int extendedFilePosition;
/*  23:    */   int dateTimeAccessed;
/*  24:    */   int dateTimeCreated;
/*  25:    */   int originalSizeEvenForVolumes;
/*  26:    */   String name;
/*  27:    */   String comment;
/*  28:    */   byte[][] extendedHeaders;
/*  29:    */   
/*  30:    */   LocalFileHeader()
/*  31:    */   {
/*  32: 47 */     this.extendedHeaders = ((byte[][])null);
/*  33:    */   }
/*  34:    */   
/*  35:    */   public String toString()
/*  36:    */   {
/*  37: 75 */     StringBuilder builder = new StringBuilder();
/*  38: 76 */     builder.append("LocalFileHeader [archiverVersionNumber=");
/*  39: 77 */     builder.append(this.archiverVersionNumber);
/*  40: 78 */     builder.append(", minVersionToExtract=");
/*  41: 79 */     builder.append(this.minVersionToExtract);
/*  42: 80 */     builder.append(", hostOS=");
/*  43: 81 */     builder.append(this.hostOS);
/*  44: 82 */     builder.append(", arjFlags=");
/*  45: 83 */     builder.append(this.arjFlags);
/*  46: 84 */     builder.append(", method=");
/*  47: 85 */     builder.append(this.method);
/*  48: 86 */     builder.append(", fileType=");
/*  49: 87 */     builder.append(this.fileType);
/*  50: 88 */     builder.append(", reserved=");
/*  51: 89 */     builder.append(this.reserved);
/*  52: 90 */     builder.append(", dateTimeModified=");
/*  53: 91 */     builder.append(this.dateTimeModified);
/*  54: 92 */     builder.append(", compressedSize=");
/*  55: 93 */     builder.append(this.compressedSize);
/*  56: 94 */     builder.append(", originalSize=");
/*  57: 95 */     builder.append(this.originalSize);
/*  58: 96 */     builder.append(", originalCrc32=");
/*  59: 97 */     builder.append(this.originalCrc32);
/*  60: 98 */     builder.append(", fileSpecPosition=");
/*  61: 99 */     builder.append(this.fileSpecPosition);
/*  62:100 */     builder.append(", fileAccessMode=");
/*  63:101 */     builder.append(this.fileAccessMode);
/*  64:102 */     builder.append(", firstChapter=");
/*  65:103 */     builder.append(this.firstChapter);
/*  66:104 */     builder.append(", lastChapter=");
/*  67:105 */     builder.append(this.lastChapter);
/*  68:106 */     builder.append(", extendedFilePosition=");
/*  69:107 */     builder.append(this.extendedFilePosition);
/*  70:108 */     builder.append(", dateTimeAccessed=");
/*  71:109 */     builder.append(this.dateTimeAccessed);
/*  72:110 */     builder.append(", dateTimeCreated=");
/*  73:111 */     builder.append(this.dateTimeCreated);
/*  74:112 */     builder.append(", originalSizeEvenForVolumes=");
/*  75:113 */     builder.append(this.originalSizeEvenForVolumes);
/*  76:114 */     builder.append(", name=");
/*  77:115 */     builder.append(this.name);
/*  78:116 */     builder.append(", comment=");
/*  79:117 */     builder.append(this.comment);
/*  80:118 */     builder.append(", extendedHeaders=");
/*  81:119 */     builder.append(Arrays.toString(this.extendedHeaders));
/*  82:120 */     builder.append("]");
/*  83:121 */     return builder.toString();
/*  84:    */   }
/*  85:    */   
/*  86:    */   static class Methods
/*  87:    */   {
/*  88:    */     static final int STORED = 0;
/*  89:    */     static final int COMPRESSED_MOST = 1;
/*  90:    */     static final int COMPRESSED_FASTEST = 4;
/*  91:    */     static final int NO_DATA_NO_CRC = 8;
/*  92:    */     static final int NO_DATA = 9;
/*  93:    */   }
/*  94:    */   
/*  95:    */   static class FileTypes
/*  96:    */   {
/*  97:    */     static final int BINARY = 0;
/*  98:    */     static final int SEVEN_BIT_TEXT = 1;
/*  99:    */     static final int DIRECTORY = 3;
/* 100:    */     static final int VOLUME_LABEL = 4;
/* 101:    */     static final int CHAPTER_LABEL = 5;
/* 102:    */   }
/* 103:    */   
/* 104:    */   static class Flags
/* 105:    */   {
/* 106:    */     static final int GARBLED = 1;
/* 107:    */     static final int VOLUME = 4;
/* 108:    */     static final int EXTFILE = 8;
/* 109:    */     static final int PATHSYM = 16;
/* 110:    */     static final int BACKUP = 32;
/* 111:    */   }
/* 112:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.arj.LocalFileHeader
 * JD-Core Version:    0.7.0.1
 */