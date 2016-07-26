/*   1:    */ package org.apache.commons.codec.digest;
/*   2:    */ 
/*   3:    */ import java.security.MessageDigest;
/*   4:    */ import java.security.NoSuchAlgorithmException;
/*   5:    */ import org.apache.commons.codec.binary.Hex;
/*   6:    */ 
/*   7:    */ public class DigestUtils
/*   8:    */ {
/*   9:    */   static MessageDigest getDigest(String algorithm)
/*  10:    */   {
/*  11:    */     try
/*  12:    */     {
/*  13: 41 */       return MessageDigest.getInstance(algorithm);
/*  14:    */     }
/*  15:    */     catch (NoSuchAlgorithmException e)
/*  16:    */     {
/*  17: 43 */       throw new RuntimeException(e.getMessage());
/*  18:    */     }
/*  19:    */   }
/*  20:    */   
/*  21:    */   private static MessageDigest getMd5Digest()
/*  22:    */   {
/*  23: 54 */     return getDigest("MD5");
/*  24:    */   }
/*  25:    */   
/*  26:    */   private static MessageDigest getShaDigest()
/*  27:    */   {
/*  28: 64 */     return getDigest("SHA");
/*  29:    */   }
/*  30:    */   
/*  31:    */   public static byte[] md5(byte[] data)
/*  32:    */   {
/*  33: 75 */     return getMd5Digest().digest(data);
/*  34:    */   }
/*  35:    */   
/*  36:    */   public static byte[] md5(String data)
/*  37:    */   {
/*  38: 86 */     return md5(data.getBytes());
/*  39:    */   }
/*  40:    */   
/*  41:    */   public static String md5Hex(byte[] data)
/*  42:    */   {
/*  43: 97 */     return new String(Hex.encodeHex(md5(data)));
/*  44:    */   }
/*  45:    */   
/*  46:    */   public static String md5Hex(String data)
/*  47:    */   {
/*  48:108 */     return new String(Hex.encodeHex(md5(data)));
/*  49:    */   }
/*  50:    */   
/*  51:    */   public static byte[] sha(byte[] data)
/*  52:    */   {
/*  53:119 */     return getShaDigest().digest(data);
/*  54:    */   }
/*  55:    */   
/*  56:    */   public static byte[] sha(String data)
/*  57:    */   {
/*  58:130 */     return sha(data.getBytes());
/*  59:    */   }
/*  60:    */   
/*  61:    */   public static String shaHex(byte[] data)
/*  62:    */   {
/*  63:140 */     return new String(Hex.encodeHex(sha(data)));
/*  64:    */   }
/*  65:    */   
/*  66:    */   public static String shaHex(String data)
/*  67:    */   {
/*  68:150 */     return new String(Hex.encodeHex(sha(data)));
/*  69:    */   }
/*  70:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.codec.digest.DigestUtils
 * JD-Core Version:    0.7.0.1
 */