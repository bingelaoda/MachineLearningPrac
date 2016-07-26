/*   1:    */ package org.apache.commons.compress.archivers.zip;
/*   2:    */ 
/*   3:    */ import java.util.zip.ZipException;
/*   4:    */ 
/*   5:    */ public final class JarMarker
/*   6:    */   implements ZipExtraField
/*   7:    */ {
/*   8: 29 */   private static final ZipShort ID = new ZipShort(51966);
/*   9: 30 */   private static final ZipShort NULL = new ZipShort(0);
/*  10: 31 */   private static final byte[] NO_BYTES = new byte[0];
/*  11: 32 */   private static final JarMarker DEFAULT = new JarMarker();
/*  12:    */   
/*  13:    */   public static JarMarker getInstance()
/*  14:    */   {
/*  15: 44 */     return DEFAULT;
/*  16:    */   }
/*  17:    */   
/*  18:    */   public ZipShort getHeaderId()
/*  19:    */   {
/*  20: 52 */     return ID;
/*  21:    */   }
/*  22:    */   
/*  23:    */   public ZipShort getLocalFileDataLength()
/*  24:    */   {
/*  25: 61 */     return NULL;
/*  26:    */   }
/*  27:    */   
/*  28:    */   public ZipShort getCentralDirectoryLength()
/*  29:    */   {
/*  30: 70 */     return NULL;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public byte[] getLocalFileDataData()
/*  34:    */   {
/*  35: 79 */     return NO_BYTES;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public byte[] getCentralDirectoryData()
/*  39:    */   {
/*  40: 88 */     return NO_BYTES;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void parseFromLocalFileData(byte[] data, int offset, int length)
/*  44:    */     throws ZipException
/*  45:    */   {
/*  46:101 */     if (length != 0) {
/*  47:102 */       throw new ZipException("JarMarker doesn't expect any data");
/*  48:    */     }
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length)
/*  52:    */     throws ZipException
/*  53:    */   {
/*  54:113 */     parseFromLocalFileData(buffer, offset, length);
/*  55:    */   }
/*  56:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.JarMarker
 * JD-Core Version:    0.7.0.1
 */