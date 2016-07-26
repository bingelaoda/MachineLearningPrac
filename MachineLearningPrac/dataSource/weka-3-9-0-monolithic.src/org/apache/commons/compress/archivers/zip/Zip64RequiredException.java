/*  1:   */ package org.apache.commons.compress.archivers.zip;
/*  2:   */ 
/*  3:   */ import java.util.zip.ZipException;
/*  4:   */ 
/*  5:   */ public class Zip64RequiredException
/*  6:   */   extends ZipException
/*  7:   */ {
/*  8:   */   private static final long serialVersionUID = 20110809L;
/*  9:   */   static final String ARCHIVE_TOO_BIG_MESSAGE = "archive's size exceeds the limit of 4GByte.";
/* 10:   */   static final String TOO_MANY_ENTRIES_MESSAGE = "archive contains more than 65535 entries.";
/* 11:   */   
/* 12:   */   static String getEntryTooBigMessage(ZipArchiveEntry ze)
/* 13:   */   {
/* 14:37 */     return ze.getName() + "'s size exceeds the limit of 4GByte.";
/* 15:   */   }
/* 16:   */   
/* 17:   */   public Zip64RequiredException(String reason)
/* 18:   */   {
/* 19:47 */     super(reason);
/* 20:   */   }
/* 21:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.Zip64RequiredException
 * JD-Core Version:    0.7.0.1
 */