/*   1:    */ package org.apache.commons.io.output;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.FileOutputStream;
/*   5:    */ import java.io.FileWriter;
/*   6:    */ import java.io.IOException;
/*   7:    */ import java.io.OutputStream;
/*   8:    */ import java.io.OutputStreamWriter;
/*   9:    */ import java.io.Writer;
/*  10:    */ import org.apache.commons.io.FileUtils;
/*  11:    */ import org.apache.commons.io.IOUtils;
/*  12:    */ 
/*  13:    */ public class LockableFileWriter
/*  14:    */   extends Writer
/*  15:    */ {
/*  16:    */   private static final String LCK = ".lck";
/*  17:    */   private final Writer out;
/*  18:    */   private final File lockFile;
/*  19:    */   
/*  20:    */   public LockableFileWriter(String fileName)
/*  21:    */     throws IOException
/*  22:    */   {
/*  23: 71 */     this(fileName, false, null);
/*  24:    */   }
/*  25:    */   
/*  26:    */   public LockableFileWriter(String fileName, boolean append)
/*  27:    */     throws IOException
/*  28:    */   {
/*  29: 83 */     this(fileName, append, null);
/*  30:    */   }
/*  31:    */   
/*  32:    */   public LockableFileWriter(String fileName, boolean append, String lockDir)
/*  33:    */     throws IOException
/*  34:    */   {
/*  35: 96 */     this(new File(fileName), append, lockDir);
/*  36:    */   }
/*  37:    */   
/*  38:    */   public LockableFileWriter(File file)
/*  39:    */     throws IOException
/*  40:    */   {
/*  41:108 */     this(file, false, null);
/*  42:    */   }
/*  43:    */   
/*  44:    */   public LockableFileWriter(File file, boolean append)
/*  45:    */     throws IOException
/*  46:    */   {
/*  47:120 */     this(file, append, null);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public LockableFileWriter(File file, boolean append, String lockDir)
/*  51:    */     throws IOException
/*  52:    */   {
/*  53:133 */     this(file, null, append, lockDir);
/*  54:    */   }
/*  55:    */   
/*  56:    */   public LockableFileWriter(File file, String encoding)
/*  57:    */     throws IOException
/*  58:    */   {
/*  59:145 */     this(file, encoding, false, null);
/*  60:    */   }
/*  61:    */   
/*  62:    */   public LockableFileWriter(File file, String encoding, boolean append, String lockDir)
/*  63:    */     throws IOException
/*  64:    */   {
/*  65:162 */     file = file.getAbsoluteFile();
/*  66:163 */     if (file.getParentFile() != null) {
/*  67:164 */       FileUtils.forceMkdir(file.getParentFile());
/*  68:    */     }
/*  69:166 */     if (file.isDirectory()) {
/*  70:167 */       throw new IOException("File specified is a directory");
/*  71:    */     }
/*  72:171 */     if (lockDir == null) {
/*  73:172 */       lockDir = System.getProperty("java.io.tmpdir");
/*  74:    */     }
/*  75:174 */     File lockDirFile = new File(lockDir);
/*  76:175 */     FileUtils.forceMkdir(lockDirFile);
/*  77:176 */     testLockDir(lockDirFile);
/*  78:177 */     this.lockFile = new File(lockDirFile, file.getName() + ".lck");
/*  79:    */     
/*  80:    */ 
/*  81:180 */     createLock();
/*  82:    */     
/*  83:    */ 
/*  84:183 */     this.out = initWriter(file, encoding, append);
/*  85:    */   }
/*  86:    */   
/*  87:    */   private void testLockDir(File lockDir)
/*  88:    */     throws IOException
/*  89:    */   {
/*  90:195 */     if (!lockDir.exists()) {
/*  91:196 */       throw new IOException("Could not find lockDir: " + lockDir.getAbsolutePath());
/*  92:    */     }
/*  93:199 */     if (!lockDir.canWrite()) {
/*  94:200 */       throw new IOException("Could not write to lockDir: " + lockDir.getAbsolutePath());
/*  95:    */     }
/*  96:    */   }
/*  97:    */   
/*  98:    */   private void createLock()
/*  99:    */     throws IOException
/* 100:    */   {
/* 101:211 */     synchronized (LockableFileWriter.class)
/* 102:    */     {
/* 103:212 */       if (!this.lockFile.createNewFile()) {
/* 104:213 */         throw new IOException("Can't write file, lock " + this.lockFile.getAbsolutePath() + " exists");
/* 105:    */       }
/* 106:216 */       this.lockFile.deleteOnExit();
/* 107:    */     }
/* 108:    */   }
/* 109:    */   
/* 110:    */   private Writer initWriter(File file, String encoding, boolean append)
/* 111:    */     throws IOException
/* 112:    */   {
/* 113:231 */     boolean fileExistedAlready = file.exists();
/* 114:232 */     OutputStream stream = null;
/* 115:233 */     Writer writer = null;
/* 116:    */     try
/* 117:    */     {
/* 118:235 */       if (encoding == null)
/* 119:    */       {
/* 120:236 */         writer = new FileWriter(file.getAbsolutePath(), append);
/* 121:    */       }
/* 122:    */       else
/* 123:    */       {
/* 124:238 */         stream = new FileOutputStream(file.getAbsolutePath(), append);
/* 125:239 */         writer = new OutputStreamWriter(stream, encoding);
/* 126:    */       }
/* 127:    */     }
/* 128:    */     catch (IOException ex)
/* 129:    */     {
/* 130:242 */       IOUtils.closeQuietly(writer);
/* 131:243 */       IOUtils.closeQuietly(stream);
/* 132:244 */       this.lockFile.delete();
/* 133:245 */       if (!fileExistedAlready) {
/* 134:246 */         file.delete();
/* 135:    */       }
/* 136:248 */       throw ex;
/* 137:    */     }
/* 138:    */     catch (RuntimeException ex)
/* 139:    */     {
/* 140:250 */       IOUtils.closeQuietly(writer);
/* 141:251 */       IOUtils.closeQuietly(stream);
/* 142:252 */       this.lockFile.delete();
/* 143:253 */       if (!fileExistedAlready) {
/* 144:254 */         file.delete();
/* 145:    */       }
/* 146:256 */       throw ex;
/* 147:    */     }
/* 148:258 */     return writer;
/* 149:    */   }
/* 150:    */   
/* 151:    */   public void close()
/* 152:    */     throws IOException
/* 153:    */   {
/* 154:    */     try
/* 155:    */     {
/* 156:269 */       this.out.close();
/* 157:    */     }
/* 158:    */     finally
/* 159:    */     {
/* 160:271 */       this.lockFile.delete();
/* 161:    */     }
/* 162:    */   }
/* 163:    */   
/* 164:    */   public void write(int idx)
/* 165:    */     throws IOException
/* 166:    */   {
/* 167:278 */     this.out.write(idx);
/* 168:    */   }
/* 169:    */   
/* 170:    */   public void write(char[] chr)
/* 171:    */     throws IOException
/* 172:    */   {
/* 173:283 */     this.out.write(chr);
/* 174:    */   }
/* 175:    */   
/* 176:    */   public void write(char[] chr, int st, int end)
/* 177:    */     throws IOException
/* 178:    */   {
/* 179:288 */     this.out.write(chr, st, end);
/* 180:    */   }
/* 181:    */   
/* 182:    */   public void write(String str)
/* 183:    */     throws IOException
/* 184:    */   {
/* 185:293 */     this.out.write(str);
/* 186:    */   }
/* 187:    */   
/* 188:    */   public void write(String str, int st, int end)
/* 189:    */     throws IOException
/* 190:    */   {
/* 191:298 */     this.out.write(str, st, end);
/* 192:    */   }
/* 193:    */   
/* 194:    */   public void flush()
/* 195:    */     throws IOException
/* 196:    */   {
/* 197:303 */     this.out.flush();
/* 198:    */   }
/* 199:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.output.LockableFileWriter
 * JD-Core Version:    0.7.0.1
 */