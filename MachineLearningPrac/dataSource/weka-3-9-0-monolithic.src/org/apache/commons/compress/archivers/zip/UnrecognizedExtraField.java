/*   1:    */ package org.apache.commons.compress.archivers.zip;
/*   2:    */ 
/*   3:    */ public class UnrecognizedExtraField
/*   4:    */   implements ZipExtraField
/*   5:    */ {
/*   6:    */   private ZipShort headerId;
/*   7:    */   private byte[] localData;
/*   8:    */   private byte[] centralData;
/*   9:    */   
/*  10:    */   public void setHeaderId(ZipShort headerId)
/*  11:    */   {
/*  12: 41 */     this.headerId = headerId;
/*  13:    */   }
/*  14:    */   
/*  15:    */   public ZipShort getHeaderId()
/*  16:    */   {
/*  17: 49 */     return this.headerId;
/*  18:    */   }
/*  19:    */   
/*  20:    */   public void setLocalFileDataData(byte[] data)
/*  21:    */   {
/*  22: 64 */     this.localData = ZipUtil.copy(data);
/*  23:    */   }
/*  24:    */   
/*  25:    */   public ZipShort getLocalFileDataLength()
/*  26:    */   {
/*  27: 72 */     return new ZipShort(this.localData != null ? this.localData.length : 0);
/*  28:    */   }
/*  29:    */   
/*  30:    */   public byte[] getLocalFileDataData()
/*  31:    */   {
/*  32: 80 */     return ZipUtil.copy(this.localData);
/*  33:    */   }
/*  34:    */   
/*  35:    */   public void setCentralDirectoryData(byte[] data)
/*  36:    */   {
/*  37: 94 */     this.centralData = ZipUtil.copy(data);
/*  38:    */   }
/*  39:    */   
/*  40:    */   public ZipShort getCentralDirectoryLength()
/*  41:    */   {
/*  42:103 */     if (this.centralData != null) {
/*  43:104 */       return new ZipShort(this.centralData.length);
/*  44:    */     }
/*  45:106 */     return getLocalFileDataLength();
/*  46:    */   }
/*  47:    */   
/*  48:    */   public byte[] getCentralDirectoryData()
/*  49:    */   {
/*  50:114 */     if (this.centralData != null) {
/*  51:115 */       return ZipUtil.copy(this.centralData);
/*  52:    */     }
/*  53:117 */     return getLocalFileDataData();
/*  54:    */   }
/*  55:    */   
/*  56:    */   public void parseFromLocalFileData(byte[] data, int offset, int length)
/*  57:    */   {
/*  58:127 */     byte[] tmp = new byte[length];
/*  59:128 */     System.arraycopy(data, offset, tmp, 0, length);
/*  60:129 */     setLocalFileDataData(tmp);
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void parseFromCentralDirectoryData(byte[] data, int offset, int length)
/*  64:    */   {
/*  65:140 */     byte[] tmp = new byte[length];
/*  66:141 */     System.arraycopy(data, offset, tmp, 0, length);
/*  67:142 */     setCentralDirectoryData(tmp);
/*  68:143 */     if (this.localData == null) {
/*  69:144 */       setLocalFileDataData(tmp);
/*  70:    */     }
/*  71:    */   }
/*  72:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.UnrecognizedExtraField
 * JD-Core Version:    0.7.0.1
 */