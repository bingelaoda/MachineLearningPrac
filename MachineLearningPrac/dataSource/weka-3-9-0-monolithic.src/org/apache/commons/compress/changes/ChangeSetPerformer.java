/*   1:    */ package org.apache.commons.compress.changes;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Iterator;
/*   7:    */ import java.util.LinkedHashSet;
/*   8:    */ import java.util.Set;
/*   9:    */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*  10:    */ import org.apache.commons.compress.archivers.ArchiveInputStream;
/*  11:    */ import org.apache.commons.compress.archivers.ArchiveOutputStream;
/*  12:    */ import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
/*  13:    */ import org.apache.commons.compress.archivers.zip.ZipFile;
/*  14:    */ import org.apache.commons.compress.utils.IOUtils;
/*  15:    */ 
/*  16:    */ public class ChangeSetPerformer
/*  17:    */ {
/*  18:    */   private final Set<Change> changes;
/*  19:    */   
/*  20:    */   public ChangeSetPerformer(ChangeSet changeSet)
/*  21:    */   {
/*  22: 52 */     this.changes = changeSet.getChanges();
/*  23:    */   }
/*  24:    */   
/*  25:    */   public ChangeSetResults perform(ArchiveInputStream in, ArchiveOutputStream out)
/*  26:    */     throws IOException
/*  27:    */   {
/*  28: 72 */     return perform(new ArchiveInputStreamIterator(in), out);
/*  29:    */   }
/*  30:    */   
/*  31:    */   public ChangeSetResults perform(ZipFile in, ArchiveOutputStream out)
/*  32:    */     throws IOException
/*  33:    */   {
/*  34: 93 */     return perform(new ZipFileIterator(in), out);
/*  35:    */   }
/*  36:    */   
/*  37:    */   private ChangeSetResults perform(ArchiveEntryIterator entryIterator, ArchiveOutputStream out)
/*  38:    */     throws IOException
/*  39:    */   {
/*  40:114 */     ChangeSetResults results = new ChangeSetResults();
/*  41:    */     
/*  42:116 */     Set<Change> workingSet = new LinkedHashSet(this.changes);
/*  43:118 */     for (Iterator<Change> it = workingSet.iterator(); it.hasNext();)
/*  44:    */     {
/*  45:119 */       Change change = (Change)it.next();
/*  46:121 */       if ((change.type() == 2) && (change.isReplaceMode()))
/*  47:    */       {
/*  48:122 */         copyStream(change.getInput(), out, change.getEntry());
/*  49:123 */         it.remove();
/*  50:124 */         results.addedFromChangeSet(change.getEntry().getName());
/*  51:    */       }
/*  52:    */     }
/*  53:128 */     while (entryIterator.hasNext())
/*  54:    */     {
/*  55:129 */       ArchiveEntry entry = entryIterator.next();
/*  56:130 */       boolean copy = true;
/*  57:132 */       for (Iterator<Change> it = workingSet.iterator(); it.hasNext();)
/*  58:    */       {
/*  59:133 */         Change change = (Change)it.next();
/*  60:    */         
/*  61:135 */         int type = change.type();
/*  62:136 */         String name = entry.getName();
/*  63:137 */         if ((type == 1) && (name != null))
/*  64:    */         {
/*  65:138 */           if (name.equals(change.targetFile()))
/*  66:    */           {
/*  67:139 */             copy = false;
/*  68:140 */             it.remove();
/*  69:141 */             results.deleted(name);
/*  70:142 */             break;
/*  71:    */           }
/*  72:    */         }
/*  73:144 */         else if ((type == 4) && (name != null)) {
/*  74:146 */           if (name.startsWith(change.targetFile() + "/"))
/*  75:    */           {
/*  76:147 */             copy = false;
/*  77:148 */             results.deleted(name);
/*  78:149 */             break;
/*  79:    */           }
/*  80:    */         }
/*  81:    */       }
/*  82:154 */       if ((copy) && (!isDeletedLater(workingSet, entry)) && (!results.hasBeenAdded(entry.getName())))
/*  83:    */       {
/*  84:157 */         copyStream(entryIterator.getInputStream(), out, entry);
/*  85:158 */         results.addedFromStream(entry.getName());
/*  86:    */       }
/*  87:    */     }
/*  88:163 */     for (Iterator<Change> it = workingSet.iterator(); it.hasNext();)
/*  89:    */     {
/*  90:164 */       Change change = (Change)it.next();
/*  91:166 */       if ((change.type() == 2) && (!change.isReplaceMode()) && (!results.hasBeenAdded(change.getEntry().getName())))
/*  92:    */       {
/*  93:169 */         copyStream(change.getInput(), out, change.getEntry());
/*  94:170 */         it.remove();
/*  95:171 */         results.addedFromChangeSet(change.getEntry().getName());
/*  96:    */       }
/*  97:    */     }
/*  98:174 */     out.finish();
/*  99:175 */     return results;
/* 100:    */   }
/* 101:    */   
/* 102:    */   private boolean isDeletedLater(Set<Change> workingSet, ArchiveEntry entry)
/* 103:    */   {
/* 104:188 */     String source = entry.getName();
/* 105:190 */     if (!workingSet.isEmpty()) {
/* 106:191 */       for (Change change : workingSet)
/* 107:    */       {
/* 108:192 */         int type = change.type();
/* 109:193 */         String target = change.targetFile();
/* 110:194 */         if ((type == 1) && (source.equals(target))) {
/* 111:195 */           return true;
/* 112:    */         }
/* 113:198 */         if ((type == 4) && (source.startsWith(target + "/"))) {
/* 114:199 */           return true;
/* 115:    */         }
/* 116:    */       }
/* 117:    */     }
/* 118:203 */     return false;
/* 119:    */   }
/* 120:    */   
/* 121:    */   private void copyStream(InputStream in, ArchiveOutputStream out, ArchiveEntry entry)
/* 122:    */     throws IOException
/* 123:    */   {
/* 124:220 */     out.putArchiveEntry(entry);
/* 125:221 */     IOUtils.copy(in, out);
/* 126:222 */     out.closeArchiveEntry();
/* 127:    */   }
/* 128:    */   
/* 129:    */   static abstract interface ArchiveEntryIterator
/* 130:    */   {
/* 131:    */     public abstract boolean hasNext()
/* 132:    */       throws IOException;
/* 133:    */     
/* 134:    */     public abstract ArchiveEntry next();
/* 135:    */     
/* 136:    */     public abstract InputStream getInputStream()
/* 137:    */       throws IOException;
/* 138:    */   }
/* 139:    */   
/* 140:    */   private static class ArchiveInputStreamIterator
/* 141:    */     implements ChangeSetPerformer.ArchiveEntryIterator
/* 142:    */   {
/* 143:    */     private final ArchiveInputStream in;
/* 144:    */     private ArchiveEntry next;
/* 145:    */     
/* 146:    */     ArchiveInputStreamIterator(ArchiveInputStream in)
/* 147:    */     {
/* 148:245 */       this.in = in;
/* 149:    */     }
/* 150:    */     
/* 151:    */     public boolean hasNext()
/* 152:    */       throws IOException
/* 153:    */     {
/* 154:248 */       return (this.next = this.in.getNextEntry()) != null;
/* 155:    */     }
/* 156:    */     
/* 157:    */     public ArchiveEntry next()
/* 158:    */     {
/* 159:251 */       return this.next;
/* 160:    */     }
/* 161:    */     
/* 162:    */     public InputStream getInputStream()
/* 163:    */     {
/* 164:254 */       return this.in;
/* 165:    */     }
/* 166:    */   }
/* 167:    */   
/* 168:    */   private static class ZipFileIterator
/* 169:    */     implements ChangeSetPerformer.ArchiveEntryIterator
/* 170:    */   {
/* 171:    */     private final ZipFile in;
/* 172:    */     private final Enumeration<ZipArchiveEntry> nestedEnum;
/* 173:    */     private ZipArchiveEntry current;
/* 174:    */     
/* 175:    */     ZipFileIterator(ZipFile in)
/* 176:    */     {
/* 177:264 */       this.in = in;
/* 178:265 */       this.nestedEnum = in.getEntriesInPhysicalOrder();
/* 179:    */     }
/* 180:    */     
/* 181:    */     public boolean hasNext()
/* 182:    */     {
/* 183:268 */       return this.nestedEnum.hasMoreElements();
/* 184:    */     }
/* 185:    */     
/* 186:    */     public ArchiveEntry next()
/* 187:    */     {
/* 188:271 */       return this.current = (ZipArchiveEntry)this.nestedEnum.nextElement();
/* 189:    */     }
/* 190:    */     
/* 191:    */     public InputStream getInputStream()
/* 192:    */       throws IOException
/* 193:    */     {
/* 194:274 */       return this.in.getInputStream(this.current);
/* 195:    */     }
/* 196:    */   }
/* 197:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.changes.ChangeSetPerformer
 * JD-Core Version:    0.7.0.1
 */