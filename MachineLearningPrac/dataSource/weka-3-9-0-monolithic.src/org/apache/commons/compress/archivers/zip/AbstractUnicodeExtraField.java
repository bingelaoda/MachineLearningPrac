/*   1:    */ package org.apache.commons.compress.archivers.zip;
/*   2:    */ 
/*   3:    */ import java.io.UnsupportedEncodingException;
/*   4:    */ import java.util.zip.CRC32;
/*   5:    */ import java.util.zip.ZipException;
/*   6:    */ 
/*   7:    */ public abstract class AbstractUnicodeExtraField
/*   8:    */   implements ZipExtraField
/*   9:    */ {
/*  10:    */   private long nameCRC32;
/*  11:    */   private byte[] unicodeName;
/*  12:    */   private byte[] data;
/*  13:    */   
/*  14:    */   protected AbstractUnicodeExtraField() {}
/*  15:    */   
/*  16:    */   protected AbstractUnicodeExtraField(String text, byte[] bytes, int off, int len)
/*  17:    */   {
/*  18: 52 */     CRC32 crc32 = new CRC32();
/*  19: 53 */     crc32.update(bytes, off, len);
/*  20: 54 */     this.nameCRC32 = crc32.getValue();
/*  21:    */     try
/*  22:    */     {
/*  23: 57 */       this.unicodeName = text.getBytes("UTF-8");
/*  24:    */     }
/*  25:    */     catch (UnsupportedEncodingException e)
/*  26:    */     {
/*  27: 59 */       throw new RuntimeException("FATAL: UTF-8 encoding not supported.", e);
/*  28:    */     }
/*  29:    */   }
/*  30:    */   
/*  31:    */   protected AbstractUnicodeExtraField(String text, byte[] bytes)
/*  32:    */   {
/*  33: 72 */     this(text, bytes, 0, bytes.length);
/*  34:    */   }
/*  35:    */   
/*  36:    */   private void assembleData()
/*  37:    */   {
/*  38: 76 */     if (this.unicodeName == null) {
/*  39: 77 */       return;
/*  40:    */     }
/*  41: 80 */     this.data = new byte[5 + this.unicodeName.length];
/*  42:    */     
/*  43: 82 */     this.data[0] = 1;
/*  44: 83 */     System.arraycopy(ZipLong.getBytes(this.nameCRC32), 0, this.data, 1, 4);
/*  45: 84 */     System.arraycopy(this.unicodeName, 0, this.data, 5, this.unicodeName.length);
/*  46:    */   }
/*  47:    */   
/*  48:    */   public long getNameCRC32()
/*  49:    */   {
/*  50: 92 */     return this.nameCRC32;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void setNameCRC32(long nameCRC32)
/*  54:    */   {
/*  55:100 */     this.nameCRC32 = nameCRC32;
/*  56:101 */     this.data = null;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public byte[] getUnicodeName()
/*  60:    */   {
/*  61:108 */     byte[] b = null;
/*  62:109 */     if (this.unicodeName != null)
/*  63:    */     {
/*  64:110 */       b = new byte[this.unicodeName.length];
/*  65:111 */       System.arraycopy(this.unicodeName, 0, b, 0, b.length);
/*  66:    */     }
/*  67:113 */     return b;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public void setUnicodeName(byte[] unicodeName)
/*  71:    */   {
/*  72:120 */     if (unicodeName != null)
/*  73:    */     {
/*  74:121 */       this.unicodeName = new byte[unicodeName.length];
/*  75:122 */       System.arraycopy(unicodeName, 0, this.unicodeName, 0, unicodeName.length);
/*  76:    */     }
/*  77:    */     else
/*  78:    */     {
/*  79:125 */       this.unicodeName = null;
/*  80:    */     }
/*  81:127 */     this.data = null;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public byte[] getCentralDirectoryData()
/*  85:    */   {
/*  86:131 */     if (this.data == null) {
/*  87:132 */       assembleData();
/*  88:    */     }
/*  89:134 */     byte[] b = null;
/*  90:135 */     if (this.data != null)
/*  91:    */     {
/*  92:136 */       b = new byte[this.data.length];
/*  93:137 */       System.arraycopy(this.data, 0, b, 0, b.length);
/*  94:    */     }
/*  95:139 */     return b;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public ZipShort getCentralDirectoryLength()
/*  99:    */   {
/* 100:143 */     if (this.data == null) {
/* 101:144 */       assembleData();
/* 102:    */     }
/* 103:146 */     return new ZipShort(this.data != null ? this.data.length : 0);
/* 104:    */   }
/* 105:    */   
/* 106:    */   public byte[] getLocalFileDataData()
/* 107:    */   {
/* 108:150 */     return getCentralDirectoryData();
/* 109:    */   }
/* 110:    */   
/* 111:    */   public ZipShort getLocalFileDataLength()
/* 112:    */   {
/* 113:154 */     return getCentralDirectoryLength();
/* 114:    */   }
/* 115:    */   
/* 116:    */   public void parseFromLocalFileData(byte[] buffer, int offset, int length)
/* 117:    */     throws ZipException
/* 118:    */   {
/* 119:160 */     if (length < 5) {
/* 120:161 */       throw new ZipException("UniCode path extra data must have at least 5 bytes.");
/* 121:    */     }
/* 122:164 */     int version = buffer[offset];
/* 123:166 */     if (version != 1) {
/* 124:167 */       throw new ZipException("Unsupported version [" + version + "] for UniCode path extra data.");
/* 125:    */     }
/* 126:171 */     this.nameCRC32 = ZipLong.getValue(buffer, offset + 1);
/* 127:172 */     this.unicodeName = new byte[length - 5];
/* 128:173 */     System.arraycopy(buffer, offset + 5, this.unicodeName, 0, length - 5);
/* 129:174 */     this.data = null;
/* 130:    */   }
/* 131:    */   
/* 132:    */   public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length)
/* 133:    */     throws ZipException
/* 134:    */   {
/* 135:184 */     parseFromLocalFileData(buffer, offset, length);
/* 136:    */   }
/* 137:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.AbstractUnicodeExtraField
 * JD-Core Version:    0.7.0.1
 */