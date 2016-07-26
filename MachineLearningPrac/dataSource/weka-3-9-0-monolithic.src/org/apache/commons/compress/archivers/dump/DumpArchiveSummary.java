/*   1:    */ package org.apache.commons.compress.archivers.dump;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.util.Date;
/*   5:    */ import org.apache.commons.compress.archivers.zip.ZipEncoding;
/*   6:    */ 
/*   7:    */ public class DumpArchiveSummary
/*   8:    */ {
/*   9:    */   private long dumpDate;
/*  10:    */   private long previousDumpDate;
/*  11:    */   private int volume;
/*  12:    */   private String label;
/*  13:    */   private int level;
/*  14:    */   private String filesys;
/*  15:    */   private String devname;
/*  16:    */   private String hostname;
/*  17:    */   private int flags;
/*  18:    */   private int firstrec;
/*  19:    */   private int ntrec;
/*  20:    */   
/*  21:    */   DumpArchiveSummary(byte[] buffer, ZipEncoding encoding)
/*  22:    */     throws IOException
/*  23:    */   {
/*  24: 47 */     this.dumpDate = (1000L * DumpArchiveUtil.convert32(buffer, 4));
/*  25: 48 */     this.previousDumpDate = (1000L * DumpArchiveUtil.convert32(buffer, 8));
/*  26: 49 */     this.volume = DumpArchiveUtil.convert32(buffer, 12);
/*  27: 50 */     this.label = DumpArchiveUtil.decode(encoding, buffer, 676, 16).trim();
/*  28: 51 */     this.level = DumpArchiveUtil.convert32(buffer, 692);
/*  29: 52 */     this.filesys = DumpArchiveUtil.decode(encoding, buffer, 696, 64).trim();
/*  30: 53 */     this.devname = DumpArchiveUtil.decode(encoding, buffer, 760, 64).trim();
/*  31: 54 */     this.hostname = DumpArchiveUtil.decode(encoding, buffer, 824, 64).trim();
/*  32: 55 */     this.flags = DumpArchiveUtil.convert32(buffer, 888);
/*  33: 56 */     this.firstrec = DumpArchiveUtil.convert32(buffer, 892);
/*  34: 57 */     this.ntrec = DumpArchiveUtil.convert32(buffer, 896);
/*  35:    */   }
/*  36:    */   
/*  37:    */   public Date getDumpDate()
/*  38:    */   {
/*  39: 67 */     return new Date(this.dumpDate);
/*  40:    */   }
/*  41:    */   
/*  42:    */   public void setDumpDate(Date dumpDate)
/*  43:    */   {
/*  44: 75 */     this.dumpDate = dumpDate.getTime();
/*  45:    */   }
/*  46:    */   
/*  47:    */   public Date getPreviousDumpDate()
/*  48:    */   {
/*  49: 83 */     return new Date(this.previousDumpDate);
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void setPreviousDumpDate(Date previousDumpDate)
/*  53:    */   {
/*  54: 91 */     this.previousDumpDate = previousDumpDate.getTime();
/*  55:    */   }
/*  56:    */   
/*  57:    */   public int getVolume()
/*  58:    */   {
/*  59: 99 */     return this.volume;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void setVolume(int volume)
/*  63:    */   {
/*  64:107 */     this.volume = volume;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public int getLevel()
/*  68:    */   {
/*  69:119 */     return this.level;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void setLevel(int level)
/*  73:    */   {
/*  74:127 */     this.level = level;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public String getLabel()
/*  78:    */   {
/*  79:136 */     return this.label;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public void setLabel(String label)
/*  83:    */   {
/*  84:144 */     this.label = label;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public String getFilesystem()
/*  88:    */   {
/*  89:152 */     return this.filesys;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public void setFilesystem(String filesystem)
/*  93:    */   {
/*  94:160 */     this.filesys = filesystem;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public String getDevname()
/*  98:    */   {
/*  99:168 */     return this.devname;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public void setDevname(String devname)
/* 103:    */   {
/* 104:176 */     this.devname = devname;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public String getHostname()
/* 108:    */   {
/* 109:184 */     return this.hostname;
/* 110:    */   }
/* 111:    */   
/* 112:    */   public void setHostname(String hostname)
/* 113:    */   {
/* 114:192 */     this.hostname = hostname;
/* 115:    */   }
/* 116:    */   
/* 117:    */   public int getFlags()
/* 118:    */   {
/* 119:200 */     return this.flags;
/* 120:    */   }
/* 121:    */   
/* 122:    */   public void setFlags(int flags)
/* 123:    */   {
/* 124:208 */     this.flags = flags;
/* 125:    */   }
/* 126:    */   
/* 127:    */   public int getFirstRecord()
/* 128:    */   {
/* 129:216 */     return this.firstrec;
/* 130:    */   }
/* 131:    */   
/* 132:    */   public void setFirstRecord(int firstrec)
/* 133:    */   {
/* 134:224 */     this.firstrec = firstrec;
/* 135:    */   }
/* 136:    */   
/* 137:    */   public int getNTRec()
/* 138:    */   {
/* 139:233 */     return this.ntrec;
/* 140:    */   }
/* 141:    */   
/* 142:    */   public void setNTRec(int ntrec)
/* 143:    */   {
/* 144:241 */     this.ntrec = ntrec;
/* 145:    */   }
/* 146:    */   
/* 147:    */   public boolean isNewHeader()
/* 148:    */   {
/* 149:251 */     return (this.flags & 0x1) == 1;
/* 150:    */   }
/* 151:    */   
/* 152:    */   public boolean isNewInode()
/* 153:    */   {
/* 154:260 */     return (this.flags & 0x2) == 2;
/* 155:    */   }
/* 156:    */   
/* 157:    */   public boolean isCompressed()
/* 158:    */   {
/* 159:269 */     return (this.flags & 0x80) == 128;
/* 160:    */   }
/* 161:    */   
/* 162:    */   public boolean isMetaDataOnly()
/* 163:    */   {
/* 164:277 */     return (this.flags & 0x100) == 256;
/* 165:    */   }
/* 166:    */   
/* 167:    */   public boolean isExtendedAttributes()
/* 168:    */   {
/* 169:285 */     return (this.flags & 0x8000) == 32768;
/* 170:    */   }
/* 171:    */   
/* 172:    */   public int hashCode()
/* 173:    */   {
/* 174:290 */     int hash = 17;
/* 175:292 */     if (this.label != null) {
/* 176:293 */       hash = this.label.hashCode();
/* 177:    */     }
/* 178:296 */     hash = (int)(hash + 31L * this.dumpDate);
/* 179:298 */     if (this.hostname != null) {
/* 180:299 */       hash = 31 * this.hostname.hashCode() + 17;
/* 181:    */     }
/* 182:302 */     if (this.devname != null) {
/* 183:303 */       hash = 31 * this.devname.hashCode() + 17;
/* 184:    */     }
/* 185:306 */     return hash;
/* 186:    */   }
/* 187:    */   
/* 188:    */   public boolean equals(Object o)
/* 189:    */   {
/* 190:311 */     if (this == o) {
/* 191:312 */       return true;
/* 192:    */     }
/* 193:315 */     if ((o == null) || (!o.getClass().equals(getClass()))) {
/* 194:316 */       return false;
/* 195:    */     }
/* 196:319 */     DumpArchiveSummary rhs = (DumpArchiveSummary)o;
/* 197:321 */     if (this.dumpDate != rhs.dumpDate) {
/* 198:322 */       return false;
/* 199:    */     }
/* 200:325 */     if ((getHostname() == null) || (!getHostname().equals(rhs.getHostname()))) {
/* 201:327 */       return false;
/* 202:    */     }
/* 203:330 */     if ((getDevname() == null) || (!getDevname().equals(rhs.getDevname()))) {
/* 204:331 */       return false;
/* 205:    */     }
/* 206:334 */     return true;
/* 207:    */   }
/* 208:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.dump.DumpArchiveSummary
 * JD-Core Version:    0.7.0.1
 */