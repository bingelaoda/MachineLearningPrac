/*   1:    */ package org.apache.commons.compress.archivers.ar;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.util.Date;
/*   5:    */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*   6:    */ 
/*   7:    */ public class ArArchiveEntry
/*   8:    */   implements ArchiveEntry
/*   9:    */ {
/*  10:    */   public static final String HEADER = "!<arch>\n";
/*  11:    */   public static final String TRAILER = "`\n";
/*  12:    */   private final String name;
/*  13:    */   private final int userId;
/*  14:    */   private final int groupId;
/*  15:    */   private final int mode;
/*  16:    */   private static final int DEFAULT_MODE = 33188;
/*  17:    */   private final long lastModified;
/*  18:    */   private final long length;
/*  19:    */   
/*  20:    */   public ArArchiveEntry(String name, long length)
/*  21:    */   {
/*  22: 85 */     this(name, length, 0, 0, 33188, System.currentTimeMillis() / 1000L);
/*  23:    */   }
/*  24:    */   
/*  25:    */   public ArArchiveEntry(String name, long length, int userId, int groupId, int mode, long lastModified)
/*  26:    */   {
/*  27:101 */     this.name = name;
/*  28:102 */     this.length = length;
/*  29:103 */     this.userId = userId;
/*  30:104 */     this.groupId = groupId;
/*  31:105 */     this.mode = mode;
/*  32:106 */     this.lastModified = lastModified;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public ArArchiveEntry(File inputFile, String entryName)
/*  36:    */   {
/*  37:116 */     this(entryName, inputFile.isFile() ? inputFile.length() : 0L, 0, 0, 33188, inputFile.lastModified() / 1000L);
/*  38:    */   }
/*  39:    */   
/*  40:    */   public long getSize()
/*  41:    */   {
/*  42:121 */     return getLength();
/*  43:    */   }
/*  44:    */   
/*  45:    */   public String getName()
/*  46:    */   {
/*  47:125 */     return this.name;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public int getUserId()
/*  51:    */   {
/*  52:129 */     return this.userId;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public int getGroupId()
/*  56:    */   {
/*  57:133 */     return this.groupId;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public int getMode()
/*  61:    */   {
/*  62:137 */     return this.mode;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public long getLastModified()
/*  66:    */   {
/*  67:145 */     return this.lastModified;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public Date getLastModifiedDate()
/*  71:    */   {
/*  72:149 */     return new Date(1000L * getLastModified());
/*  73:    */   }
/*  74:    */   
/*  75:    */   public long getLength()
/*  76:    */   {
/*  77:153 */     return this.length;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public boolean isDirectory()
/*  81:    */   {
/*  82:157 */     return false;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public int hashCode()
/*  86:    */   {
/*  87:162 */     int prime = 31;
/*  88:163 */     int result = 1;
/*  89:164 */     result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
/*  90:165 */     return result;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public boolean equals(Object obj)
/*  94:    */   {
/*  95:170 */     if (this == obj) {
/*  96:171 */       return true;
/*  97:    */     }
/*  98:173 */     if ((obj == null) || (getClass() != obj.getClass())) {
/*  99:174 */       return false;
/* 100:    */     }
/* 101:176 */     ArArchiveEntry other = (ArArchiveEntry)obj;
/* 102:177 */     if (this.name == null)
/* 103:    */     {
/* 104:178 */       if (other.name != null) {
/* 105:179 */         return false;
/* 106:    */       }
/* 107:    */     }
/* 108:181 */     else if (!this.name.equals(other.name)) {
/* 109:182 */       return false;
/* 110:    */     }
/* 111:184 */     return true;
/* 112:    */   }
/* 113:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.ar.ArArchiveEntry
 * JD-Core Version:    0.7.0.1
 */