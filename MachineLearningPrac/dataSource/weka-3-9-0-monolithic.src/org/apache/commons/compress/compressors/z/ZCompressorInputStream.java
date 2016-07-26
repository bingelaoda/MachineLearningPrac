/*   1:    */ package org.apache.commons.compress.compressors.z;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.nio.ByteOrder;
/*   6:    */ import org.apache.commons.compress.compressors.lzw.LZWInputStream;
/*   7:    */ import org.apache.commons.compress.utils.BitInputStream;
/*   8:    */ 
/*   9:    */ public class ZCompressorInputStream
/*  10:    */   extends LZWInputStream
/*  11:    */ {
/*  12:    */   private static final int MAGIC_1 = 31;
/*  13:    */   private static final int MAGIC_2 = 157;
/*  14:    */   private static final int BLOCK_MODE_MASK = 128;
/*  15:    */   private static final int MAX_CODE_SIZE_MASK = 31;
/*  16:    */   private final boolean blockMode;
/*  17:    */   private final int maxCodeSize;
/*  18: 39 */   private long totalCodesRead = 0L;
/*  19:    */   
/*  20:    */   public ZCompressorInputStream(InputStream inputStream)
/*  21:    */     throws IOException
/*  22:    */   {
/*  23: 42 */     super(inputStream, ByteOrder.LITTLE_ENDIAN);
/*  24: 43 */     int firstByte = (int)this.in.readBits(8);
/*  25: 44 */     int secondByte = (int)this.in.readBits(8);
/*  26: 45 */     int thirdByte = (int)this.in.readBits(8);
/*  27: 46 */     if ((firstByte != 31) || (secondByte != 157) || (thirdByte < 0)) {
/*  28: 47 */       throw new IOException("Input is not in .Z format");
/*  29:    */     }
/*  30: 49 */     this.blockMode = ((thirdByte & 0x80) != 0);
/*  31: 50 */     this.maxCodeSize = (thirdByte & 0x1F);
/*  32: 51 */     if (this.blockMode) {
/*  33: 52 */       setClearCode(9);
/*  34:    */     }
/*  35: 54 */     initializeTables(this.maxCodeSize);
/*  36: 55 */     clearEntries();
/*  37:    */   }
/*  38:    */   
/*  39:    */   private void clearEntries()
/*  40:    */   {
/*  41: 59 */     setTableSize(256 + (this.blockMode ? 1 : 0));
/*  42:    */   }
/*  43:    */   
/*  44:    */   protected int readNextCode()
/*  45:    */     throws IOException
/*  46:    */   {
/*  47: 70 */     int code = super.readNextCode();
/*  48: 71 */     if (code >= 0) {
/*  49: 72 */       this.totalCodesRead += 1L;
/*  50:    */     }
/*  51: 74 */     return code;
/*  52:    */   }
/*  53:    */   
/*  54:    */   private void reAlignReading()
/*  55:    */     throws IOException
/*  56:    */   {
/*  57: 82 */     long codeReadsToThrowAway = 8L - this.totalCodesRead % 8L;
/*  58: 83 */     if (codeReadsToThrowAway == 8L) {
/*  59: 84 */       codeReadsToThrowAway = 0L;
/*  60:    */     }
/*  61: 86 */     for (long i = 0L; i < codeReadsToThrowAway; i += 1L) {
/*  62: 87 */       readNextCode();
/*  63:    */     }
/*  64: 89 */     this.in.clearBitCache();
/*  65:    */   }
/*  66:    */   
/*  67:    */   protected int addEntry(int previousCode, byte character)
/*  68:    */     throws IOException
/*  69:    */   {
/*  70:100 */     int maxTableSize = 1 << getCodeSize();
/*  71:101 */     int r = addEntry(previousCode, character, maxTableSize);
/*  72:102 */     if ((getTableSize() == maxTableSize) && (getCodeSize() < this.maxCodeSize))
/*  73:    */     {
/*  74:103 */       reAlignReading();
/*  75:104 */       incrementCodeSize();
/*  76:    */     }
/*  77:106 */     return r;
/*  78:    */   }
/*  79:    */   
/*  80:    */   protected int decompressNextSymbol()
/*  81:    */     throws IOException
/*  82:    */   {
/*  83:129 */     int code = readNextCode();
/*  84:130 */     if (code < 0) {
/*  85:131 */       return -1;
/*  86:    */     }
/*  87:132 */     if ((this.blockMode) && (code == getClearCode()))
/*  88:    */     {
/*  89:133 */       clearEntries();
/*  90:134 */       reAlignReading();
/*  91:135 */       resetCodeSize();
/*  92:136 */       resetPreviousCode();
/*  93:137 */       return 0;
/*  94:    */     }
/*  95:139 */     boolean addedUnfinishedEntry = false;
/*  96:140 */     if (code == getTableSize())
/*  97:    */     {
/*  98:141 */       addRepeatOfPreviousCode();
/*  99:142 */       addedUnfinishedEntry = true;
/* 100:    */     }
/* 101:143 */     else if (code > getTableSize())
/* 102:    */     {
/* 103:144 */       throw new IOException(String.format("Invalid %d bit code 0x%x", new Object[] { Integer.valueOf(getCodeSize()), Integer.valueOf(code) }));
/* 104:    */     }
/* 105:146 */     return expandCodeToOutputStack(code, addedUnfinishedEntry);
/* 106:    */   }
/* 107:    */   
/* 108:    */   public static boolean matches(byte[] signature, int length)
/* 109:    */   {
/* 110:163 */     return (length > 3) && (signature[0] == 31) && (signature[1] == -99);
/* 111:    */   }
/* 112:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.z.ZCompressorInputStream
 * JD-Core Version:    0.7.0.1
 */