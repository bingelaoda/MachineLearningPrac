/*   1:    */ package org.apache.commons.compress.archivers.zip;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.nio.ByteOrder;
/*   6:    */ import org.apache.commons.compress.compressors.lzw.LZWInputStream;
/*   7:    */ 
/*   8:    */ class UnshrinkingInputStream
/*   9:    */   extends LZWInputStream
/*  10:    */ {
/*  11:    */   private static final int MAX_CODE_SIZE = 13;
/*  12:    */   private static final int MAX_TABLE_SIZE = 8192;
/*  13:    */   private final boolean[] isUsed;
/*  14:    */   
/*  15:    */   public UnshrinkingInputStream(InputStream inputStream)
/*  16:    */     throws IOException
/*  17:    */   {
/*  18: 38 */     super(inputStream, ByteOrder.LITTLE_ENDIAN);
/*  19: 39 */     setClearCode(9);
/*  20: 40 */     initializeTables(13);
/*  21: 41 */     this.isUsed = new boolean[getPrefixesLength()];
/*  22: 42 */     for (int i = 0; i < 256; i++) {
/*  23: 43 */       this.isUsed[i] = true;
/*  24:    */     }
/*  25: 45 */     setTableSize(getClearCode() + 1);
/*  26:    */   }
/*  27:    */   
/*  28:    */   protected int addEntry(int previousCode, byte character)
/*  29:    */     throws IOException
/*  30:    */   {
/*  31: 50 */     int tableSize = getTableSize();
/*  32: 51 */     while ((tableSize < 8192) && (this.isUsed[tableSize] != 0)) {
/*  33: 52 */       tableSize++;
/*  34:    */     }
/*  35: 54 */     setTableSize(tableSize);
/*  36: 55 */     int idx = addEntry(previousCode, character, 8192);
/*  37: 56 */     if (idx >= 0) {
/*  38: 57 */       this.isUsed[idx] = true;
/*  39:    */     }
/*  40: 59 */     return idx;
/*  41:    */   }
/*  42:    */   
/*  43:    */   private void partialClear()
/*  44:    */   {
/*  45: 63 */     boolean[] isParent = new boolean[8192];
/*  46: 64 */     for (int i = 0; i < this.isUsed.length; i++) {
/*  47: 65 */       if ((this.isUsed[i] != 0) && (getPrefix(i) != -1)) {
/*  48: 66 */         isParent[getPrefix(i)] = true;
/*  49:    */       }
/*  50:    */     }
/*  51: 69 */     for (int i = getClearCode() + 1; i < isParent.length; i++) {
/*  52: 70 */       if (isParent[i] == 0)
/*  53:    */       {
/*  54: 71 */         this.isUsed[i] = false;
/*  55: 72 */         setPrefix(i, -1);
/*  56:    */       }
/*  57:    */     }
/*  58:    */   }
/*  59:    */   
/*  60:    */   protected int decompressNextSymbol()
/*  61:    */     throws IOException
/*  62:    */   {
/*  63: 91 */     int code = readNextCode();
/*  64: 92 */     if (code < 0) {
/*  65: 93 */       return -1;
/*  66:    */     }
/*  67: 94 */     if (code == getClearCode())
/*  68:    */     {
/*  69: 95 */       int subCode = readNextCode();
/*  70: 96 */       if (subCode < 0) {
/*  71: 97 */         throw new IOException("Unexpected EOF;");
/*  72:    */       }
/*  73: 98 */       if (subCode == 1)
/*  74:    */       {
/*  75: 99 */         if (getCodeSize() < 13) {
/*  76:100 */           incrementCodeSize();
/*  77:    */         } else {
/*  78:102 */           throw new IOException("Attempt to increase code size beyond maximum");
/*  79:    */         }
/*  80:    */       }
/*  81:104 */       else if (subCode == 2)
/*  82:    */       {
/*  83:105 */         partialClear();
/*  84:106 */         setTableSize(getClearCode() + 1);
/*  85:    */       }
/*  86:    */       else
/*  87:    */       {
/*  88:108 */         throw new IOException("Invalid clear code subcode " + subCode);
/*  89:    */       }
/*  90:110 */       return 0;
/*  91:    */     }
/*  92:112 */     boolean addedUnfinishedEntry = false;
/*  93:113 */     int effectiveCode = code;
/*  94:114 */     if (this.isUsed[code] == 0)
/*  95:    */     {
/*  96:115 */       effectiveCode = addRepeatOfPreviousCode();
/*  97:116 */       addedUnfinishedEntry = true;
/*  98:    */     }
/*  99:118 */     return expandCodeToOutputStack(effectiveCode, addedUnfinishedEntry);
/* 100:    */   }
/* 101:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.UnshrinkingInputStream
 * JD-Core Version:    0.7.0.1
 */