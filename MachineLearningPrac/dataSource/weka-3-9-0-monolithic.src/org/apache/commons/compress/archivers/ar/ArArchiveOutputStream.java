/*   1:    */ package org.apache.commons.compress.archivers.ar;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.OutputStream;
/*   6:    */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*   7:    */ import org.apache.commons.compress.archivers.ArchiveOutputStream;
/*   8:    */ import org.apache.commons.compress.utils.ArchiveUtils;
/*   9:    */ 
/*  10:    */ public class ArArchiveOutputStream
/*  11:    */   extends ArchiveOutputStream
/*  12:    */ {
/*  13:    */   public static final int LONGFILE_ERROR = 0;
/*  14:    */   public static final int LONGFILE_BSD = 1;
/*  15:    */   private final OutputStream out;
/*  16: 42 */   private long entryOffset = 0L;
/*  17:    */   private ArArchiveEntry prevEntry;
/*  18: 44 */   private boolean haveUnclosedEntry = false;
/*  19: 45 */   private int longFileMode = 0;
/*  20: 48 */   private boolean finished = false;
/*  21:    */   
/*  22:    */   public ArArchiveOutputStream(OutputStream pOut)
/*  23:    */   {
/*  24: 51 */     this.out = pOut;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public void setLongFileMode(int longFileMode)
/*  28:    */   {
/*  29: 63 */     this.longFileMode = longFileMode;
/*  30:    */   }
/*  31:    */   
/*  32:    */   private long writeArchiveHeader()
/*  33:    */     throws IOException
/*  34:    */   {
/*  35: 67 */     byte[] header = ArchiveUtils.toAsciiBytes("!<arch>\n");
/*  36: 68 */     this.out.write(header);
/*  37: 69 */     return header.length;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public void closeArchiveEntry()
/*  41:    */     throws IOException
/*  42:    */   {
/*  43: 74 */     if (this.finished) {
/*  44: 75 */       throw new IOException("Stream has already been finished");
/*  45:    */     }
/*  46: 77 */     if ((this.prevEntry == null) || (!this.haveUnclosedEntry)) {
/*  47: 78 */       throw new IOException("No current entry to close");
/*  48:    */     }
/*  49: 80 */     if (this.entryOffset % 2L != 0L) {
/*  50: 81 */       this.out.write(10);
/*  51:    */     }
/*  52: 83 */     this.haveUnclosedEntry = false;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public void putArchiveEntry(ArchiveEntry pEntry)
/*  56:    */     throws IOException
/*  57:    */   {
/*  58: 88 */     if (this.finished) {
/*  59: 89 */       throw new IOException("Stream has already been finished");
/*  60:    */     }
/*  61: 92 */     ArArchiveEntry pArEntry = (ArArchiveEntry)pEntry;
/*  62: 93 */     if (this.prevEntry == null)
/*  63:    */     {
/*  64: 94 */       writeArchiveHeader();
/*  65:    */     }
/*  66:    */     else
/*  67:    */     {
/*  68: 96 */       if (this.prevEntry.getLength() != this.entryOffset) {
/*  69: 97 */         throw new IOException("length does not match entry (" + this.prevEntry.getLength() + " != " + this.entryOffset);
/*  70:    */       }
/*  71:100 */       if (this.haveUnclosedEntry) {
/*  72:101 */         closeArchiveEntry();
/*  73:    */       }
/*  74:    */     }
/*  75:105 */     this.prevEntry = pArEntry;
/*  76:    */     
/*  77:107 */     writeEntryHeader(pArEntry);
/*  78:    */     
/*  79:109 */     this.entryOffset = 0L;
/*  80:110 */     this.haveUnclosedEntry = true;
/*  81:    */   }
/*  82:    */   
/*  83:    */   private long fill(long pOffset, long pNewOffset, char pFill)
/*  84:    */     throws IOException
/*  85:    */   {
/*  86:114 */     long diff = pNewOffset - pOffset;
/*  87:116 */     if (diff > 0L) {
/*  88:117 */       for (int i = 0; i < diff; i++) {
/*  89:118 */         write(pFill);
/*  90:    */       }
/*  91:    */     }
/*  92:122 */     return pNewOffset;
/*  93:    */   }
/*  94:    */   
/*  95:    */   private long write(String data)
/*  96:    */     throws IOException
/*  97:    */   {
/*  98:126 */     byte[] bytes = data.getBytes("ascii");
/*  99:127 */     write(bytes);
/* 100:128 */     return bytes.length;
/* 101:    */   }
/* 102:    */   
/* 103:    */   private long writeEntryHeader(ArArchiveEntry pEntry)
/* 104:    */     throws IOException
/* 105:    */   {
/* 106:133 */     long offset = 0L;
/* 107:134 */     boolean mustAppendName = false;
/* 108:    */     
/* 109:136 */     String n = pEntry.getName();
/* 110:137 */     if ((0 == this.longFileMode) && (n.length() > 16)) {
/* 111:138 */       throw new IOException("filename too long, > 16 chars: " + n);
/* 112:    */     }
/* 113:140 */     if ((1 == this.longFileMode) && ((n.length() > 16) || (n.contains(" "))))
/* 114:    */     {
/* 115:142 */       mustAppendName = true;
/* 116:143 */       offset += write("#1/" + String.valueOf(n.length()));
/* 117:    */     }
/* 118:    */     else
/* 119:    */     {
/* 120:146 */       offset += write(n);
/* 121:    */     }
/* 122:149 */     offset = fill(offset, 16L, ' ');
/* 123:150 */     String m = "" + pEntry.getLastModified();
/* 124:151 */     if (m.length() > 12) {
/* 125:152 */       throw new IOException("modified too long");
/* 126:    */     }
/* 127:154 */     offset += write(m);
/* 128:    */     
/* 129:156 */     offset = fill(offset, 28L, ' ');
/* 130:157 */     String u = "" + pEntry.getUserId();
/* 131:158 */     if (u.length() > 6) {
/* 132:159 */       throw new IOException("userid too long");
/* 133:    */     }
/* 134:161 */     offset += write(u);
/* 135:    */     
/* 136:163 */     offset = fill(offset, 34L, ' ');
/* 137:164 */     String g = "" + pEntry.getGroupId();
/* 138:165 */     if (g.length() > 6) {
/* 139:166 */       throw new IOException("groupid too long");
/* 140:    */     }
/* 141:168 */     offset += write(g);
/* 142:    */     
/* 143:170 */     offset = fill(offset, 40L, ' ');
/* 144:171 */     String fm = "" + Integer.toString(pEntry.getMode(), 8);
/* 145:172 */     if (fm.length() > 8) {
/* 146:173 */       throw new IOException("filemode too long");
/* 147:    */     }
/* 148:175 */     offset += write(fm);
/* 149:    */     
/* 150:177 */     offset = fill(offset, 48L, ' ');
/* 151:178 */     String s = String.valueOf(pEntry.getLength() + (mustAppendName ? n.length() : 0));
/* 152:181 */     if (s.length() > 10) {
/* 153:182 */       throw new IOException("size too long");
/* 154:    */     }
/* 155:184 */     offset += write(s);
/* 156:    */     
/* 157:186 */     offset = fill(offset, 58L, ' ');
/* 158:    */     
/* 159:188 */     offset += write("`\n");
/* 160:190 */     if (mustAppendName) {
/* 161:191 */       offset += write(n);
/* 162:    */     }
/* 163:194 */     return offset;
/* 164:    */   }
/* 165:    */   
/* 166:    */   public void write(byte[] b, int off, int len)
/* 167:    */     throws IOException
/* 168:    */   {
/* 169:199 */     this.out.write(b, off, len);
/* 170:200 */     count(len);
/* 171:201 */     this.entryOffset += len;
/* 172:    */   }
/* 173:    */   
/* 174:    */   public void close()
/* 175:    */     throws IOException
/* 176:    */   {
/* 177:209 */     if (!this.finished) {
/* 178:210 */       finish();
/* 179:    */     }
/* 180:212 */     this.out.close();
/* 181:213 */     this.prevEntry = null;
/* 182:    */   }
/* 183:    */   
/* 184:    */   public ArchiveEntry createArchiveEntry(File inputFile, String entryName)
/* 185:    */     throws IOException
/* 186:    */   {
/* 187:219 */     if (this.finished) {
/* 188:220 */       throw new IOException("Stream has already been finished");
/* 189:    */     }
/* 190:222 */     return new ArArchiveEntry(inputFile, entryName);
/* 191:    */   }
/* 192:    */   
/* 193:    */   public void finish()
/* 194:    */     throws IOException
/* 195:    */   {
/* 196:227 */     if (this.haveUnclosedEntry) {
/* 197:228 */       throw new IOException("This archive contains unclosed entries.");
/* 198:    */     }
/* 199:229 */     if (this.finished) {
/* 200:230 */       throw new IOException("This archive has already been finished");
/* 201:    */     }
/* 202:232 */     this.finished = true;
/* 203:    */   }
/* 204:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.ar.ArArchiveOutputStream
 * JD-Core Version:    0.7.0.1
 */