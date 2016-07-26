/*  1:   */ package org.apache.commons.compress.archivers.sevenz;
/*  2:   */ 
/*  3:   */ import java.util.BitSet;
/*  4:   */ 
/*  5:   */ class Archive
/*  6:   */ {
/*  7:   */   long packPos;
/*  8:   */   long[] packSizes;
/*  9:   */   BitSet packCrcsDefined;
/* 10:   */   long[] packCrcs;
/* 11:   */   Folder[] folders;
/* 12:   */   SubStreamsInfo subStreamsInfo;
/* 13:   */   SevenZArchiveEntry[] files;
/* 14:   */   StreamMap streamMap;
/* 15:   */   
/* 16:   */   public String toString()
/* 17:   */   {
/* 18:42 */     return "Archive with packed streams starting at offset " + this.packPos + ", " + lengthOf(this.packSizes) + " pack sizes, " + lengthOf(this.packCrcs) + " CRCs, " + lengthOf(this.folders) + " folders, " + lengthOf(this.files) + " files and " + this.streamMap;
/* 19:   */   }
/* 20:   */   
/* 21:   */   private static String lengthOf(long[] a)
/* 22:   */   {
/* 23:49 */     return a == null ? "(null)" : String.valueOf(a.length);
/* 24:   */   }
/* 25:   */   
/* 26:   */   private static String lengthOf(Object[] a)
/* 27:   */   {
/* 28:53 */     return a == null ? "(null)" : String.valueOf(a.length);
/* 29:   */   }
/* 30:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.sevenz.Archive
 * JD-Core Version:    0.7.0.1
 */