/*   1:    */ package org.apache.commons.io;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.InputStream;
/*   6:    */ import java.io.InputStreamReader;
/*   7:    */ import java.io.OutputStream;
/*   8:    */ import java.util.ArrayList;
/*   9:    */ import java.util.Arrays;
/*  10:    */ import java.util.List;
/*  11:    */ import java.util.StringTokenizer;
/*  12:    */ 
/*  13:    */ public class FileSystemUtils
/*  14:    */ {
/*  15: 52 */   private static final FileSystemUtils INSTANCE = new FileSystemUtils();
/*  16:    */   private static final int INIT_PROBLEM = -1;
/*  17:    */   private static final int OTHER = 0;
/*  18:    */   private static final int WINDOWS = 1;
/*  19:    */   private static final int UNIX = 2;
/*  20:    */   private static final int POSIX_UNIX = 3;
/*  21:    */   private static final int OS;
/*  22:    */   
/*  23:    */   static
/*  24:    */   {
/*  25: 68 */     int os = 0;
/*  26:    */     try
/*  27:    */     {
/*  28: 70 */       String osName = System.getProperty("os.name");
/*  29: 71 */       if (osName == null) {
/*  30: 72 */         throw new IOException("os.name not found");
/*  31:    */       }
/*  32: 74 */       osName = osName.toLowerCase();
/*  33: 76 */       if (osName.indexOf("windows") != -1) {
/*  34: 77 */         os = 1;
/*  35: 78 */       } else if ((osName.indexOf("linux") != -1) || (osName.indexOf("sun os") != -1) || (osName.indexOf("sunos") != -1) || (osName.indexOf("solaris") != -1) || (osName.indexOf("mpe/ix") != -1) || (osName.indexOf("freebsd") != -1) || (osName.indexOf("irix") != -1) || (osName.indexOf("digital unix") != -1) || (osName.indexOf("unix") != -1) || (osName.indexOf("mac os x") != -1)) {
/*  36: 88 */         os = 2;
/*  37: 89 */       } else if ((osName.indexOf("hp-ux") != -1) || (osName.indexOf("aix") != -1)) {
/*  38: 91 */         os = 3;
/*  39:    */       } else {
/*  40: 93 */         os = 0;
/*  41:    */       }
/*  42:    */     }
/*  43:    */     catch (Exception ex)
/*  44:    */     {
/*  45: 97 */       os = -1;
/*  46:    */     }
/*  47: 99 */     OS = os;
/*  48:    */   }
/*  49:    */   
/*  50:    */   /**
/*  51:    */    * @deprecated
/*  52:    */    */
/*  53:    */   public static long freeSpace(String path)
/*  54:    */     throws IOException
/*  55:    */   {
/*  56:137 */     return INSTANCE.freeSpaceOS(path, OS, false);
/*  57:    */   }
/*  58:    */   
/*  59:    */   public static long freeSpaceKb(String path)
/*  60:    */     throws IOException
/*  61:    */   {
/*  62:166 */     return INSTANCE.freeSpaceOS(path, OS, true);
/*  63:    */   }
/*  64:    */   
/*  65:    */   long freeSpaceOS(String path, int os, boolean kb)
/*  66:    */     throws IOException
/*  67:    */   {
/*  68:189 */     if (path == null) {
/*  69:190 */       throw new IllegalArgumentException("Path must not be empty");
/*  70:    */     }
/*  71:192 */     switch (os)
/*  72:    */     {
/*  73:    */     case 1: 
/*  74:194 */       return kb ? freeSpaceWindows(path) / 1024L : freeSpaceWindows(path);
/*  75:    */     case 2: 
/*  76:196 */       return freeSpaceUnix(path, kb, false);
/*  77:    */     case 3: 
/*  78:198 */       return freeSpaceUnix(path, kb, true);
/*  79:    */     case 0: 
/*  80:200 */       throw new IllegalStateException("Unsupported operating system");
/*  81:    */     }
/*  82:202 */     throw new IllegalStateException("Exception caught when determining operating system");
/*  83:    */   }
/*  84:    */   
/*  85:    */   long freeSpaceWindows(String path)
/*  86:    */     throws IOException
/*  87:    */   {
/*  88:216 */     path = FilenameUtils.normalize(path);
/*  89:217 */     if ((path.length() > 2) && (path.charAt(1) == ':')) {
/*  90:218 */       path = path.substring(0, 2);
/*  91:    */     }
/*  92:222 */     String[] cmdAttribs = { "cmd.exe", "/C", "dir /-c " + path };
/*  93:    */     
/*  94:    */ 
/*  95:225 */     List lines = performCommand(cmdAttribs, 2147483647);
/*  96:231 */     for (int i = lines.size() - 1; i >= 0; i--)
/*  97:    */     {
/*  98:232 */       String line = (String)lines.get(i);
/*  99:233 */       if (line.length() > 0) {
/* 100:234 */         return parseDir(line, path);
/* 101:    */       }
/* 102:    */     }
/* 103:238 */     throw new IOException("Command line 'dir /-c' did not return any info for path '" + path + "'");
/* 104:    */   }
/* 105:    */   
/* 106:    */   long parseDir(String line, String path)
/* 107:    */     throws IOException
/* 108:    */   {
/* 109:256 */     int bytesStart = 0;
/* 110:257 */     int bytesEnd = 0;
/* 111:258 */     int j = line.length() - 1;
/* 112:259 */     while (j >= 0)
/* 113:    */     {
/* 114:260 */       char c = line.charAt(j);
/* 115:261 */       if (Character.isDigit(c))
/* 116:    */       {
/* 117:264 */         bytesEnd = j + 1;
/* 118:265 */         break;
/* 119:    */       }
/* 120:267 */       j--;
/* 121:    */     }
/* 122:269 */     while (j >= 0)
/* 123:    */     {
/* 124:270 */       char c = line.charAt(j);
/* 125:271 */       if ((!Character.isDigit(c)) && (c != ',') && (c != '.'))
/* 126:    */       {
/* 127:274 */         bytesStart = j + 1;
/* 128:275 */         break;
/* 129:    */       }
/* 130:277 */       j--;
/* 131:    */     }
/* 132:279 */     if (j < 0) {
/* 133:280 */       throw new IOException("Command line 'dir /-c' did not return valid info for path '" + path + "'");
/* 134:    */     }
/* 135:286 */     StringBuffer buf = new StringBuffer(line.substring(bytesStart, bytesEnd));
/* 136:287 */     for (int k = 0; k < buf.length(); k++) {
/* 137:288 */       if ((buf.charAt(k) == ',') || (buf.charAt(k) == '.')) {
/* 138:289 */         buf.deleteCharAt(k--);
/* 139:    */       }
/* 140:    */     }
/* 141:292 */     return parseBytes(buf.toString(), path);
/* 142:    */   }
/* 143:    */   
/* 144:    */   long freeSpaceUnix(String path, boolean kb, boolean posix)
/* 145:    */     throws IOException
/* 146:    */   {
/* 147:306 */     if (path.length() == 0) {
/* 148:307 */       throw new IllegalArgumentException("Path must not be empty");
/* 149:    */     }
/* 150:309 */     path = FilenameUtils.normalize(path);
/* 151:    */     
/* 152:    */ 
/* 153:312 */     String flags = "-";
/* 154:313 */     if (kb) {
/* 155:314 */       flags = flags + "k";
/* 156:    */     }
/* 157:316 */     if (posix) {
/* 158:317 */       flags = flags + "P";
/* 159:    */     }
/* 160:319 */     String[] cmdAttribs = { "df", flags.length() > 1 ? new String[] { "df", flags, path } : path };
/* 161:    */     
/* 162:    */ 
/* 163:    */ 
/* 164:323 */     List lines = performCommand(cmdAttribs, 3);
/* 165:324 */     if (lines.size() < 2) {
/* 166:326 */       throw new IOException("Command line 'df' did not return info as expected for path '" + path + "'- response was " + lines);
/* 167:    */     }
/* 168:330 */     String line2 = (String)lines.get(1);
/* 169:    */     
/* 170:    */ 
/* 171:333 */     StringTokenizer tok = new StringTokenizer(line2, " ");
/* 172:334 */     if (tok.countTokens() < 4)
/* 173:    */     {
/* 174:336 */       if ((tok.countTokens() == 1) && (lines.size() >= 3))
/* 175:    */       {
/* 176:337 */         String line3 = (String)lines.get(2);
/* 177:338 */         tok = new StringTokenizer(line3, " ");
/* 178:    */       }
/* 179:    */       else
/* 180:    */       {
/* 181:340 */         throw new IOException("Command line 'df' did not return data as expected for path '" + path + "'- check path is valid");
/* 182:    */       }
/* 183:    */     }
/* 184:    */     else {
/* 185:345 */       tok.nextToken();
/* 186:    */     }
/* 187:347 */     tok.nextToken();
/* 188:348 */     tok.nextToken();
/* 189:349 */     String freeSpace = tok.nextToken();
/* 190:350 */     return parseBytes(freeSpace, path);
/* 191:    */   }
/* 192:    */   
/* 193:    */   long parseBytes(String freeSpace, String path)
/* 194:    */     throws IOException
/* 195:    */   {
/* 196:    */     try
/* 197:    */     {
/* 198:364 */       long bytes = Long.parseLong(freeSpace);
/* 199:365 */       if (bytes < 0L) {
/* 200:366 */         throw new IOException("Command line 'df' did not find free space in response for path '" + path + "'- check path is valid");
/* 201:    */       }
/* 202:370 */       return bytes;
/* 203:    */     }
/* 204:    */     catch (NumberFormatException ex)
/* 205:    */     {
/* 206:373 */       throw new IOException("Command line 'df' did not return numeric data as expected for path '" + path + "'- check path is valid");
/* 207:    */     }
/* 208:    */   }
/* 209:    */   
/* 210:    */   List performCommand(String[] cmdAttribs, int max)
/* 211:    */     throws IOException
/* 212:    */   {
/* 213:397 */     List lines = new ArrayList(20);
/* 214:398 */     Process proc = null;
/* 215:399 */     InputStream in = null;
/* 216:400 */     OutputStream out = null;
/* 217:401 */     InputStream err = null;
/* 218:402 */     BufferedReader inr = null;
/* 219:    */     try
/* 220:    */     {
/* 221:404 */       proc = openProcess(cmdAttribs);
/* 222:405 */       in = proc.getInputStream();
/* 223:406 */       out = proc.getOutputStream();
/* 224:407 */       err = proc.getErrorStream();
/* 225:408 */       inr = new BufferedReader(new InputStreamReader(in));
/* 226:409 */       String line = inr.readLine();
/* 227:410 */       while ((line != null) && (lines.size() < max))
/* 228:    */       {
/* 229:411 */         line = line.toLowerCase().trim();
/* 230:412 */         lines.add(line);
/* 231:413 */         line = inr.readLine();
/* 232:    */       }
/* 233:416 */       proc.waitFor();
/* 234:417 */       if (proc.exitValue() != 0) {
/* 235:419 */         throw new IOException("Command line returned OS error code '" + proc.exitValue() + "' for command " + Arrays.asList(cmdAttribs));
/* 236:    */       }
/* 237:423 */       if (lines.size() == 0) {
/* 238:425 */         throw new IOException("Command line did not return any info for command " + Arrays.asList(cmdAttribs));
/* 239:    */       }
/* 240:429 */       return lines;
/* 241:    */     }
/* 242:    */     catch (InterruptedException ex)
/* 243:    */     {
/* 244:432 */       throw new IOException("Command line threw an InterruptedException '" + ex.getMessage() + "' for command " + Arrays.asList(cmdAttribs));
/* 245:    */     }
/* 246:    */     finally
/* 247:    */     {
/* 248:436 */       IOUtils.closeQuietly(in);
/* 249:437 */       IOUtils.closeQuietly(out);
/* 250:438 */       IOUtils.closeQuietly(err);
/* 251:439 */       IOUtils.closeQuietly(inr);
/* 252:440 */       if (proc != null) {
/* 253:441 */         proc.destroy();
/* 254:    */       }
/* 255:    */     }
/* 256:    */   }
/* 257:    */   
/* 258:    */   Process openProcess(String[] cmdAttribs)
/* 259:    */     throws IOException
/* 260:    */   {
/* 261:454 */     return Runtime.getRuntime().exec(cmdAttribs);
/* 262:    */   }
/* 263:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.FileSystemUtils
 * JD-Core Version:    0.7.0.1
 */