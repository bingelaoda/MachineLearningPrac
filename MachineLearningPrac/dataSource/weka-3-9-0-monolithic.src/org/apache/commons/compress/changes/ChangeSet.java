/*   1:    */ package org.apache.commons.compress.changes;
/*   2:    */ 
/*   3:    */ import java.io.InputStream;
/*   4:    */ import java.util.Iterator;
/*   5:    */ import java.util.LinkedHashSet;
/*   6:    */ import java.util.Set;
/*   7:    */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*   8:    */ 
/*   9:    */ public final class ChangeSet
/*  10:    */ {
/*  11: 37 */   private final Set<Change> changes = new LinkedHashSet();
/*  12:    */   
/*  13:    */   public void delete(String filename)
/*  14:    */   {
/*  15: 46 */     addDeletion(new Change(filename, 1));
/*  16:    */   }
/*  17:    */   
/*  18:    */   public void deleteDir(String dirName)
/*  19:    */   {
/*  20: 56 */     addDeletion(new Change(dirName, 4));
/*  21:    */   }
/*  22:    */   
/*  23:    */   public void add(ArchiveEntry pEntry, InputStream pInput)
/*  24:    */   {
/*  25: 68 */     add(pEntry, pInput, true);
/*  26:    */   }
/*  27:    */   
/*  28:    */   public void add(ArchiveEntry pEntry, InputStream pInput, boolean replace)
/*  29:    */   {
/*  30: 84 */     addAddition(new Change(pEntry, pInput, replace));
/*  31:    */   }
/*  32:    */   
/*  33:    */   private void addAddition(Change pChange)
/*  34:    */   {
/*  35: 94 */     if ((2 != pChange.type()) || (pChange.getInput() == null)) {
/*  36:    */       return;
/*  37:    */     }
/*  38:    */     Iterator<Change> it;
/*  39: 99 */     if (!this.changes.isEmpty()) {
/*  40:100 */       for (it = this.changes.iterator(); it.hasNext();)
/*  41:    */       {
/*  42:101 */         Change change = (Change)it.next();
/*  43:102 */         if ((change.type() == 2) && (change.getEntry() != null))
/*  44:    */         {
/*  45:104 */           ArchiveEntry entry = change.getEntry();
/*  46:106 */           if (entry.equals(pChange.getEntry()))
/*  47:    */           {
/*  48:107 */             if (pChange.isReplaceMode())
/*  49:    */             {
/*  50:108 */               it.remove();
/*  51:109 */               this.changes.add(pChange);
/*  52:110 */               return;
/*  53:    */             }
/*  54:113 */             return;
/*  55:    */           }
/*  56:    */         }
/*  57:    */       }
/*  58:    */     }
/*  59:119 */     this.changes.add(pChange);
/*  60:    */   }
/*  61:    */   
/*  62:    */   private void addDeletion(Change pChange)
/*  63:    */   {
/*  64:129 */     if (((1 != pChange.type()) && (4 != pChange.type())) || (pChange.targetFile() == null)) {
/*  65:132 */       return;
/*  66:    */     }
/*  67:134 */     String source = pChange.targetFile();
/*  68:    */     Iterator<Change> it;
/*  69:136 */     if ((source != null) && (!this.changes.isEmpty())) {
/*  70:137 */       for (it = this.changes.iterator(); it.hasNext();)
/*  71:    */       {
/*  72:138 */         Change change = (Change)it.next();
/*  73:139 */         if ((change.type() == 2) && (change.getEntry() != null))
/*  74:    */         {
/*  75:141 */           String target = change.getEntry().getName();
/*  76:143 */           if (target != null) {
/*  77:147 */             if ((1 == pChange.type()) && (source.equals(target))) {
/*  78:148 */               it.remove();
/*  79:149 */             } else if ((4 == pChange.type()) && (target.matches(source + "/.*"))) {
/*  80:151 */               it.remove();
/*  81:    */             }
/*  82:    */           }
/*  83:    */         }
/*  84:    */       }
/*  85:    */     }
/*  86:156 */     this.changes.add(pChange);
/*  87:    */   }
/*  88:    */   
/*  89:    */   Set<Change> getChanges()
/*  90:    */   {
/*  91:165 */     return new LinkedHashSet(this.changes);
/*  92:    */   }
/*  93:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.changes.ChangeSet
 * JD-Core Version:    0.7.0.1
 */