/*   1:    */ package org.apache.commons.compress.archivers.zip;
/*   2:    */ 
/*   3:    */ public final class UnparseableExtraFieldData
/*   4:    */   implements ZipExtraField
/*   5:    */ {
/*   6: 32 */   private static final ZipShort HEADER_ID = new ZipShort(44225);
/*   7:    */   private byte[] localFileData;
/*   8:    */   private byte[] centralDirectoryData;
/*   9:    */   
/*  10:    */   public ZipShort getHeaderId()
/*  11:    */   {
/*  12: 43 */     return HEADER_ID;
/*  13:    */   }
/*  14:    */   
/*  15:    */   public ZipShort getLocalFileDataLength()
/*  16:    */   {
/*  17: 52 */     return new ZipShort(this.localFileData == null ? 0 : this.localFileData.length);
/*  18:    */   }
/*  19:    */   
/*  20:    */   public ZipShort getCentralDirectoryLength()
/*  21:    */   {
/*  22: 61 */     return this.centralDirectoryData == null ? getLocalFileDataLength() : new ZipShort(this.centralDirectoryData.length);
/*  23:    */   }
/*  24:    */   
/*  25:    */   public byte[] getLocalFileDataData()
/*  26:    */   {
/*  27: 72 */     return ZipUtil.copy(this.localFileData);
/*  28:    */   }
/*  29:    */   
/*  30:    */   public byte[] getCentralDirectoryData()
/*  31:    */   {
/*  32: 81 */     return this.centralDirectoryData == null ? getLocalFileDataData() : ZipUtil.copy(this.centralDirectoryData);
/*  33:    */   }
/*  34:    */   
/*  35:    */   public void parseFromLocalFileData(byte[] buffer, int offset, int length)
/*  36:    */   {
/*  37: 93 */     this.localFileData = new byte[length];
/*  38: 94 */     System.arraycopy(buffer, offset, this.localFileData, 0, length);
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length)
/*  42:    */   {
/*  43:106 */     this.centralDirectoryData = new byte[length];
/*  44:107 */     System.arraycopy(buffer, offset, this.centralDirectoryData, 0, length);
/*  45:108 */     if (this.localFileData == null) {
/*  46:109 */       parseFromLocalFileData(buffer, offset, length);
/*  47:    */     }
/*  48:    */   }
/*  49:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.UnparseableExtraFieldData
 * JD-Core Version:    0.7.0.1
 */