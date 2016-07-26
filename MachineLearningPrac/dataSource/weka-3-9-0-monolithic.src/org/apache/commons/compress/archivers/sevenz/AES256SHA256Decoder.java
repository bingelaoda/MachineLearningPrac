/*  1:   */ package org.apache.commons.compress.archivers.sevenz;
/*  2:   */ 
/*  3:   */ import java.io.IOException;
/*  4:   */ import java.io.InputStream;
/*  5:   */ import java.security.GeneralSecurityException;
/*  6:   */ import java.security.MessageDigest;
/*  7:   */ import java.security.NoSuchAlgorithmException;
/*  8:   */ import javax.crypto.Cipher;
/*  9:   */ import javax.crypto.CipherInputStream;
/* 10:   */ import javax.crypto.SecretKey;
/* 11:   */ import javax.crypto.spec.IvParameterSpec;
/* 12:   */ import javax.crypto.spec.SecretKeySpec;
/* 13:   */ import org.apache.commons.compress.PasswordRequiredException;
/* 14:   */ 
/* 15:   */ class AES256SHA256Decoder
/* 16:   */   extends CoderBase
/* 17:   */ {
/* 18:   */   AES256SHA256Decoder()
/* 19:   */   {
/* 20:32 */     super(new Class[0]);
/* 21:   */   }
/* 22:   */   
/* 23:   */   InputStream decode(final String archiveName, final InputStream in, long uncompressedLength, final Coder coder, final byte[] passwordBytes)
/* 24:   */     throws IOException
/* 25:   */   {
/* 26:36 */     new InputStream()
/* 27:   */     {
/* 28:37 */       private boolean isInitialized = false;
/* 29:38 */       private CipherInputStream cipherInputStream = null;
/* 30:   */       
/* 31:   */       private CipherInputStream init()
/* 32:   */         throws IOException
/* 33:   */       {
/* 34:41 */         if (this.isInitialized) {
/* 35:42 */           return this.cipherInputStream;
/* 36:   */         }
/* 37:44 */         int byte0 = 0xFF & coder.properties[0];
/* 38:45 */         int numCyclesPower = byte0 & 0x3F;
/* 39:46 */         int byte1 = 0xFF & coder.properties[1];
/* 40:47 */         int ivSize = (byte0 >> 6 & 0x1) + (byte1 & 0xF);
/* 41:48 */         int saltSize = (byte0 >> 7 & 0x1) + (byte1 >> 4);
/* 42:49 */         if (2 + saltSize + ivSize > coder.properties.length) {
/* 43:50 */           throw new IOException("Salt size + IV size too long in " + archiveName);
/* 44:   */         }
/* 45:52 */         byte[] salt = new byte[saltSize];
/* 46:53 */         System.arraycopy(coder.properties, 2, salt, 0, saltSize);
/* 47:54 */         byte[] iv = new byte[16];
/* 48:55 */         System.arraycopy(coder.properties, 2 + saltSize, iv, 0, ivSize);
/* 49:57 */         if (passwordBytes == null) {
/* 50:58 */           throw new PasswordRequiredException(archiveName);
/* 51:   */         }
/* 52:   */         byte[] aesKeyBytes;
/* 53:61 */         if (numCyclesPower == 63)
/* 54:   */         {
/* 55:62 */           byte[] aesKeyBytes = new byte[32];
/* 56:63 */           System.arraycopy(salt, 0, aesKeyBytes, 0, saltSize);
/* 57:64 */           System.arraycopy(passwordBytes, 0, aesKeyBytes, saltSize, Math.min(passwordBytes.length, aesKeyBytes.length - saltSize));
/* 58:   */         }
/* 59:   */         else
/* 60:   */         {
/* 61:   */           MessageDigest digest;
/* 62:   */           try
/* 63:   */           {
/* 64:69 */             digest = MessageDigest.getInstance("SHA-256");
/* 65:   */           }
/* 66:   */           catch (NoSuchAlgorithmException noSuchAlgorithmException)
/* 67:   */           {
/* 68:71 */             IOException ioe = new IOException("SHA-256 is unsupported by your Java implementation");
/* 69:72 */             ioe.initCause(noSuchAlgorithmException);
/* 70:73 */             throw ioe;
/* 71:   */           }
/* 72:78 */           byte[] extra = new byte[8];
/* 73:79 */           for (long j = 0L; j < 1L << numCyclesPower; j += 1L)
/* 74:   */           {
/* 75:80 */             digest.update(salt);
/* 76:81 */             digest.update(passwordBytes);
/* 77:82 */             digest.update(extra);
/* 78:83 */             for (int k = 0; k < extra.length; k++)
/* 79:   */             {
/* 80:84 */               int tmp326_324 = k; byte[] tmp326_322 = extra;tmp326_322[tmp326_324] = ((byte)(tmp326_322[tmp326_324] + 1));
/* 81:85 */               if (extra[k] != 0) {
/* 82:   */                 break;
/* 83:   */               }
/* 84:   */             }
/* 85:   */           }
/* 86:90 */           aesKeyBytes = digest.digest();
/* 87:   */         }
/* 88:93 */         SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");
/* 89:   */         try
/* 90:   */         {
/* 91:95 */           Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
/* 92:96 */           cipher.init(2, aesKey, new IvParameterSpec(iv));
/* 93:97 */           this.cipherInputStream = new CipherInputStream(in, cipher);
/* 94:98 */           this.isInitialized = true;
/* 95:99 */           return this.cipherInputStream;
/* 96:   */         }
/* 97:   */         catch (GeneralSecurityException generalSecurityException)
/* 98:   */         {
/* 99::1 */           IOException ioe = new IOException("Decryption error (do you have the JCE Unlimited Strength Jurisdiction Policy Files installed?)");
/* :0:   */           
/* :1::3 */           ioe.initCause(generalSecurityException);
/* :2::4 */           throw ioe;
/* :3:   */         }
/* :4:   */       }
/* :5:   */       
/* :6:   */       public int read()
/* :7:   */         throws IOException
/* :8:   */       {
/* :9:;4 */         return init().read();
/* ;0:   */       }
/* ;1:   */       
/* ;2:   */       public int read(byte[] b, int off, int len)
/* ;3:   */         throws IOException
/* ;4:   */       {
/* ;5:;9 */         return init().read(b, off, len);
/* ;6:   */       }
/* ;7:   */       
/* ;8:   */       public void close() {}
/* ;9:   */     };
/* <0:   */   }
/* <1:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.sevenz.AES256SHA256Decoder
 * JD-Core Version:    0.7.0.1
 */