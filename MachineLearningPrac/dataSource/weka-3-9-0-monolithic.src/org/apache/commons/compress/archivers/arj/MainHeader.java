/*   1:    */ package org.apache.commons.compress.archivers.arj;
/*   2:    */ 
/*   3:    */ import java.util.Arrays;
/*   4:    */ 
/*   5:    */ class MainHeader
/*   6:    */ {
/*   7:    */   int archiverVersionNumber;
/*   8:    */   int minVersionToExtract;
/*   9:    */   int hostOS;
/*  10:    */   int arjFlags;
/*  11:    */   int securityVersion;
/*  12:    */   int fileType;
/*  13:    */   int reserved;
/*  14:    */   int dateTimeCreated;
/*  15:    */   int dateTimeModified;
/*  16:    */   long archiveSize;
/*  17:    */   int securityEnvelopeFilePosition;
/*  18:    */   int fileSpecPosition;
/*  19:    */   int securityEnvelopeLength;
/*  20:    */   int encryptionVersion;
/*  21:    */   int lastChapter;
/*  22:    */   int arjProtectionFactor;
/*  23:    */   int arjFlags2;
/*  24:    */   String name;
/*  25:    */   String comment;
/*  26:    */   byte[] extendedHeaderBytes;
/*  27:    */   
/*  28:    */   MainHeader()
/*  29:    */   {
/*  30: 42 */     this.extendedHeaderBytes = null;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public String toString()
/*  34:    */   {
/*  35: 58 */     StringBuilder builder = new StringBuilder();
/*  36: 59 */     builder.append("MainHeader [archiverVersionNumber=");
/*  37: 60 */     builder.append(this.archiverVersionNumber);
/*  38: 61 */     builder.append(", minVersionToExtract=");
/*  39: 62 */     builder.append(this.minVersionToExtract);
/*  40: 63 */     builder.append(", hostOS=");
/*  41: 64 */     builder.append(this.hostOS);
/*  42: 65 */     builder.append(", arjFlags=");
/*  43: 66 */     builder.append(this.arjFlags);
/*  44: 67 */     builder.append(", securityVersion=");
/*  45: 68 */     builder.append(this.securityVersion);
/*  46: 69 */     builder.append(", fileType=");
/*  47: 70 */     builder.append(this.fileType);
/*  48: 71 */     builder.append(", reserved=");
/*  49: 72 */     builder.append(this.reserved);
/*  50: 73 */     builder.append(", dateTimeCreated=");
/*  51: 74 */     builder.append(this.dateTimeCreated);
/*  52: 75 */     builder.append(", dateTimeModified=");
/*  53: 76 */     builder.append(this.dateTimeModified);
/*  54: 77 */     builder.append(", archiveSize=");
/*  55: 78 */     builder.append(this.archiveSize);
/*  56: 79 */     builder.append(", securityEnvelopeFilePosition=");
/*  57: 80 */     builder.append(this.securityEnvelopeFilePosition);
/*  58: 81 */     builder.append(", fileSpecPosition=");
/*  59: 82 */     builder.append(this.fileSpecPosition);
/*  60: 83 */     builder.append(", securityEnvelopeLength=");
/*  61: 84 */     builder.append(this.securityEnvelopeLength);
/*  62: 85 */     builder.append(", encryptionVersion=");
/*  63: 86 */     builder.append(this.encryptionVersion);
/*  64: 87 */     builder.append(", lastChapter=");
/*  65: 88 */     builder.append(this.lastChapter);
/*  66: 89 */     builder.append(", arjProtectionFactor=");
/*  67: 90 */     builder.append(this.arjProtectionFactor);
/*  68: 91 */     builder.append(", arjFlags2=");
/*  69: 92 */     builder.append(this.arjFlags2);
/*  70: 93 */     builder.append(", name=");
/*  71: 94 */     builder.append(this.name);
/*  72: 95 */     builder.append(", comment=");
/*  73: 96 */     builder.append(this.comment);
/*  74: 97 */     builder.append(", extendedHeaderBytes=");
/*  75: 98 */     builder.append(Arrays.toString(this.extendedHeaderBytes));
/*  76: 99 */     builder.append("]");
/*  77:100 */     return builder.toString();
/*  78:    */   }
/*  79:    */   
/*  80:    */   static class Flags
/*  81:    */   {
/*  82:    */     static final int GARBLED = 1;
/*  83:    */     static final int OLD_SECURED_NEW_ANSI_PAGE = 2;
/*  84:    */     static final int VOLUME = 4;
/*  85:    */     static final int ARJPROT = 8;
/*  86:    */     static final int PATHSYM = 16;
/*  87:    */     static final int BACKUP = 32;
/*  88:    */     static final int SECURED = 64;
/*  89:    */     static final int ALTNAME = 128;
/*  90:    */   }
/*  91:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.arj.MainHeader
 * JD-Core Version:    0.7.0.1
 */