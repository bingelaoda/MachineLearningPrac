/*   1:    */ package org.apache.commons.io.filefilter;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.FileFilter;
/*   5:    */ import java.io.FilenameFilter;
/*   6:    */ import java.util.Date;
/*   7:    */ 
/*   8:    */ public class FileFilterUtils
/*   9:    */ {
/*  10:    */   private static IOFileFilter cvsFilter;
/*  11:    */   private static IOFileFilter svnFilter;
/*  12:    */   
/*  13:    */   public static IOFileFilter prefixFileFilter(String prefix)
/*  14:    */   {
/*  15: 53 */     return new PrefixFileFilter(prefix);
/*  16:    */   }
/*  17:    */   
/*  18:    */   public static IOFileFilter suffixFileFilter(String suffix)
/*  19:    */   {
/*  20: 63 */     return new SuffixFileFilter(suffix);
/*  21:    */   }
/*  22:    */   
/*  23:    */   public static IOFileFilter nameFileFilter(String name)
/*  24:    */   {
/*  25: 73 */     return new NameFileFilter(name);
/*  26:    */   }
/*  27:    */   
/*  28:    */   public static IOFileFilter directoryFileFilter()
/*  29:    */   {
/*  30: 82 */     return DirectoryFileFilter.DIRECTORY;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public static IOFileFilter fileFileFilter()
/*  34:    */   {
/*  35: 91 */     return FileFileFilter.FILE;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public static IOFileFilter andFileFilter(IOFileFilter filter1, IOFileFilter filter2)
/*  39:    */   {
/*  40:103 */     return new AndFileFilter(filter1, filter2);
/*  41:    */   }
/*  42:    */   
/*  43:    */   public static IOFileFilter orFileFilter(IOFileFilter filter1, IOFileFilter filter2)
/*  44:    */   {
/*  45:114 */     return new OrFileFilter(filter1, filter2);
/*  46:    */   }
/*  47:    */   
/*  48:    */   public static IOFileFilter notFileFilter(IOFileFilter filter)
/*  49:    */   {
/*  50:124 */     return new NotFileFilter(filter);
/*  51:    */   }
/*  52:    */   
/*  53:    */   public static IOFileFilter trueFileFilter()
/*  54:    */   {
/*  55:134 */     return TrueFileFilter.TRUE;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public static IOFileFilter falseFileFilter()
/*  59:    */   {
/*  60:143 */     return FalseFileFilter.FALSE;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public static IOFileFilter asFileFilter(FileFilter filter)
/*  64:    */   {
/*  65:155 */     return new DelegateFileFilter(filter);
/*  66:    */   }
/*  67:    */   
/*  68:    */   public static IOFileFilter asFileFilter(FilenameFilter filter)
/*  69:    */   {
/*  70:166 */     return new DelegateFileFilter(filter);
/*  71:    */   }
/*  72:    */   
/*  73:    */   public static IOFileFilter ageFileFilter(long cutoff)
/*  74:    */   {
/*  75:179 */     return new AgeFileFilter(cutoff);
/*  76:    */   }
/*  77:    */   
/*  78:    */   public static IOFileFilter ageFileFilter(long cutoff, boolean acceptOlder)
/*  79:    */   {
/*  80:191 */     return new AgeFileFilter(cutoff, acceptOlder);
/*  81:    */   }
/*  82:    */   
/*  83:    */   public static IOFileFilter ageFileFilter(Date cutoffDate)
/*  84:    */   {
/*  85:203 */     return new AgeFileFilter(cutoffDate);
/*  86:    */   }
/*  87:    */   
/*  88:    */   public static IOFileFilter ageFileFilter(Date cutoffDate, boolean acceptOlder)
/*  89:    */   {
/*  90:215 */     return new AgeFileFilter(cutoffDate, acceptOlder);
/*  91:    */   }
/*  92:    */   
/*  93:    */   public static IOFileFilter ageFileFilter(File cutoffReference)
/*  94:    */   {
/*  95:228 */     return new AgeFileFilter(cutoffReference);
/*  96:    */   }
/*  97:    */   
/*  98:    */   public static IOFileFilter ageFileFilter(File cutoffReference, boolean acceptOlder)
/*  99:    */   {
/* 100:241 */     return new AgeFileFilter(cutoffReference, acceptOlder);
/* 101:    */   }
/* 102:    */   
/* 103:    */   public static IOFileFilter sizeFileFilter(long threshold)
/* 104:    */   {
/* 105:253 */     return new SizeFileFilter(threshold);
/* 106:    */   }
/* 107:    */   
/* 108:    */   public static IOFileFilter sizeFileFilter(long threshold, boolean acceptLarger)
/* 109:    */   {
/* 110:265 */     return new SizeFileFilter(threshold, acceptLarger);
/* 111:    */   }
/* 112:    */   
/* 113:    */   public static IOFileFilter sizeRangeFileFilter(long minSizeInclusive, long maxSizeInclusive)
/* 114:    */   {
/* 115:278 */     IOFileFilter minimumFilter = new SizeFileFilter(minSizeInclusive, true);
/* 116:279 */     IOFileFilter maximumFilter = new SizeFileFilter(maxSizeInclusive + 1L, false);
/* 117:280 */     return new AndFileFilter(minimumFilter, maximumFilter);
/* 118:    */   }
/* 119:    */   
/* 120:    */   public static IOFileFilter makeCVSAware(IOFileFilter filter)
/* 121:    */   {
/* 122:300 */     if (cvsFilter == null) {
/* 123:301 */       cvsFilter = notFileFilter(andFileFilter(directoryFileFilter(), nameFileFilter("CVS")));
/* 124:    */     }
/* 125:304 */     if (filter == null) {
/* 126:305 */       return cvsFilter;
/* 127:    */     }
/* 128:307 */     return andFileFilter(filter, cvsFilter);
/* 129:    */   }
/* 130:    */   
/* 131:    */   public static IOFileFilter makeSVNAware(IOFileFilter filter)
/* 132:    */   {
/* 133:321 */     if (svnFilter == null) {
/* 134:322 */       svnFilter = notFileFilter(andFileFilter(directoryFileFilter(), nameFileFilter(".svn")));
/* 135:    */     }
/* 136:325 */     if (filter == null) {
/* 137:326 */       return svnFilter;
/* 138:    */     }
/* 139:328 */     return andFileFilter(filter, svnFilter);
/* 140:    */   }
/* 141:    */   
/* 142:    */   public static IOFileFilter makeDirectoryOnly(IOFileFilter filter)
/* 143:    */   {
/* 144:341 */     if (filter == null) {
/* 145:342 */       return DirectoryFileFilter.DIRECTORY;
/* 146:    */     }
/* 147:344 */     return new AndFileFilter(DirectoryFileFilter.DIRECTORY, filter);
/* 148:    */   }
/* 149:    */   
/* 150:    */   public static IOFileFilter makeFileOnly(IOFileFilter filter)
/* 151:    */   {
/* 152:355 */     if (filter == null) {
/* 153:356 */       return FileFileFilter.FILE;
/* 154:    */     }
/* 155:358 */     return new AndFileFilter(FileFileFilter.FILE, filter);
/* 156:    */   }
/* 157:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.filefilter.FileFilterUtils
 * JD-Core Version:    0.7.0.1
 */