/*   1:    */ package org.apache.commons.compress.archivers.zip;
/*   2:    */ 
/*   3:    */ public final class GeneralPurposeBit
/*   4:    */   implements Cloneable
/*   5:    */ {
/*   6:    */   private static final int ENCRYPTION_FLAG = 1;
/*   7:    */   private static final int SLIDING_DICTIONARY_SIZE_FLAG = 2;
/*   8:    */   private static final int NUMBER_OF_SHANNON_FANO_TREES_FLAG = 4;
/*   9:    */   private static final int DATA_DESCRIPTOR_FLAG = 8;
/*  10:    */   private static final int STRONG_ENCRYPTION_FLAG = 64;
/*  11:    */   public static final int UFT8_NAMES_FLAG = 2048;
/*  12: 72 */   private boolean languageEncodingFlag = false;
/*  13: 73 */   private boolean dataDescriptorFlag = false;
/*  14: 74 */   private boolean encryptionFlag = false;
/*  15: 75 */   private boolean strongEncryptionFlag = false;
/*  16:    */   private int slidingDictionarySize;
/*  17:    */   private int numberOfShannonFanoTrees;
/*  18:    */   
/*  19:    */   public boolean usesUTF8ForNames()
/*  20:    */   {
/*  21: 87 */     return this.languageEncodingFlag;
/*  22:    */   }
/*  23:    */   
/*  24:    */   public void useUTF8ForNames(boolean b)
/*  25:    */   {
/*  26: 95 */     this.languageEncodingFlag = b;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public boolean usesDataDescriptor()
/*  30:    */   {
/*  31:105 */     return this.dataDescriptorFlag;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public void useDataDescriptor(boolean b)
/*  35:    */   {
/*  36:115 */     this.dataDescriptorFlag = b;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public boolean usesEncryption()
/*  40:    */   {
/*  41:123 */     return this.encryptionFlag;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public void useEncryption(boolean b)
/*  45:    */   {
/*  46:131 */     this.encryptionFlag = b;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public boolean usesStrongEncryption()
/*  50:    */   {
/*  51:139 */     return (this.encryptionFlag) && (this.strongEncryptionFlag);
/*  52:    */   }
/*  53:    */   
/*  54:    */   public void useStrongEncryption(boolean b)
/*  55:    */   {
/*  56:147 */     this.strongEncryptionFlag = b;
/*  57:148 */     if (b) {
/*  58:149 */       useEncryption(true);
/*  59:    */     }
/*  60:    */   }
/*  61:    */   
/*  62:    */   int getSlidingDictionarySize()
/*  63:    */   {
/*  64:157 */     return this.slidingDictionarySize;
/*  65:    */   }
/*  66:    */   
/*  67:    */   int getNumberOfShannonFanoTrees()
/*  68:    */   {
/*  69:164 */     return this.numberOfShannonFanoTrees;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public byte[] encode()
/*  73:    */   {
/*  74:172 */     byte[] result = new byte[2];
/*  75:173 */     encode(result, 0);
/*  76:174 */     return result;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public void encode(byte[] buf, int offset)
/*  80:    */   {
/*  81:187 */     ZipShort.putShort((this.dataDescriptorFlag ? 8 : 0) | (this.languageEncodingFlag ? 2048 : 0) | (this.encryptionFlag ? 1 : 0) | (this.strongEncryptionFlag ? 64 : 0), buf, offset);
/*  82:    */   }
/*  83:    */   
/*  84:    */   public static GeneralPurposeBit parse(byte[] data, int offset)
/*  85:    */   {
/*  86:205 */     int generalPurposeFlag = ZipShort.getValue(data, offset);
/*  87:206 */     GeneralPurposeBit b = new GeneralPurposeBit();
/*  88:207 */     b.useDataDescriptor((generalPurposeFlag & 0x8) != 0);
/*  89:208 */     b.useUTF8ForNames((generalPurposeFlag & 0x800) != 0);
/*  90:209 */     b.useStrongEncryption((generalPurposeFlag & 0x40) != 0);
/*  91:210 */     b.useEncryption((generalPurposeFlag & 0x1) != 0);
/*  92:211 */     b.slidingDictionarySize = ((generalPurposeFlag & 0x2) != 0 ? 8192 : 4096);
/*  93:212 */     b.numberOfShannonFanoTrees = ((generalPurposeFlag & 0x4) != 0 ? 3 : 2);
/*  94:213 */     return b;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public int hashCode()
/*  98:    */   {
/*  99:218 */     return 3 * (7 * (13 * (17 * (this.encryptionFlag ? 1 : 0) + (this.strongEncryptionFlag ? 1 : 0)) + (this.languageEncodingFlag ? 1 : 0)) + (this.dataDescriptorFlag ? 1 : 0));
/* 100:    */   }
/* 101:    */   
/* 102:    */   public boolean equals(Object o)
/* 103:    */   {
/* 104:226 */     if (!(o instanceof GeneralPurposeBit)) {
/* 105:227 */       return false;
/* 106:    */     }
/* 107:229 */     GeneralPurposeBit g = (GeneralPurposeBit)o;
/* 108:230 */     return (g.encryptionFlag == this.encryptionFlag) && (g.strongEncryptionFlag == this.strongEncryptionFlag) && (g.languageEncodingFlag == this.languageEncodingFlag) && (g.dataDescriptorFlag == this.dataDescriptorFlag);
/* 109:    */   }
/* 110:    */   
/* 111:    */   public Object clone()
/* 112:    */   {
/* 113:    */     try
/* 114:    */     {
/* 115:239 */       return super.clone();
/* 116:    */     }
/* 117:    */     catch (CloneNotSupportedException ex)
/* 118:    */     {
/* 119:242 */       throw new RuntimeException("GeneralPurposeBit is not Cloneable?", ex);
/* 120:    */     }
/* 121:    */   }
/* 122:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.GeneralPurposeBit
 * JD-Core Version:    0.7.0.1
 */