/*  1:   */ package org.apache.commons.compress.archivers.sevenz;
/*  2:   */ 
/*  3:   */ class StreamMap
/*  4:   */ {
/*  5:   */   int[] folderFirstPackStreamIndex;
/*  6:   */   long[] packStreamOffsets;
/*  7:   */   int[] folderFirstFileIndex;
/*  8:   */   int[] fileFolderIndex;
/*  9:   */   
/* 10:   */   public String toString()
/* 11:   */   {
/* 12:33 */     return "StreamMap with indices of " + this.folderFirstPackStreamIndex.length + " folders, offsets of " + this.packStreamOffsets.length + " packed streams," + " first files of " + this.folderFirstFileIndex.length + " folders and" + " folder indices for " + this.fileFolderIndex.length + " files";
/* 13:   */   }
/* 14:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.sevenz.StreamMap
 * JD-Core Version:    0.7.0.1
 */