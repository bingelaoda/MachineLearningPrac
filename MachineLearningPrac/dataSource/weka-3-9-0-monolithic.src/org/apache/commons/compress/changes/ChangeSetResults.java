/*  1:   */ package org.apache.commons.compress.changes;
/*  2:   */ 
/*  3:   */ import java.util.ArrayList;
/*  4:   */ import java.util.List;
/*  5:   */ 
/*  6:   */ public class ChangeSetResults
/*  7:   */ {
/*  8:28 */   private final List<String> addedFromChangeSet = new ArrayList();
/*  9:29 */   private final List<String> addedFromStream = new ArrayList();
/* 10:30 */   private final List<String> deleted = new ArrayList();
/* 11:   */   
/* 12:   */   void deleted(String fileName)
/* 13:   */   {
/* 14:37 */     this.deleted.add(fileName);
/* 15:   */   }
/* 16:   */   
/* 17:   */   void addedFromStream(String fileName)
/* 18:   */   {
/* 19:46 */     this.addedFromStream.add(fileName);
/* 20:   */   }
/* 21:   */   
/* 22:   */   void addedFromChangeSet(String fileName)
/* 23:   */   {
/* 24:55 */     this.addedFromChangeSet.add(fileName);
/* 25:   */   }
/* 26:   */   
/* 27:   */   public List<String> getAddedFromChangeSet()
/* 28:   */   {
/* 29:63 */     return this.addedFromChangeSet;
/* 30:   */   }
/* 31:   */   
/* 32:   */   public List<String> getAddedFromStream()
/* 33:   */   {
/* 34:71 */     return this.addedFromStream;
/* 35:   */   }
/* 36:   */   
/* 37:   */   public List<String> getDeleted()
/* 38:   */   {
/* 39:79 */     return this.deleted;
/* 40:   */   }
/* 41:   */   
/* 42:   */   boolean hasBeenAdded(String filename)
/* 43:   */   {
/* 44:88 */     if ((this.addedFromChangeSet.contains(filename)) || (this.addedFromStream.contains(filename))) {
/* 45:89 */       return true;
/* 46:   */     }
/* 47:91 */     return false;
/* 48:   */   }
/* 49:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.changes.ChangeSetResults
 * JD-Core Version:    0.7.0.1
 */