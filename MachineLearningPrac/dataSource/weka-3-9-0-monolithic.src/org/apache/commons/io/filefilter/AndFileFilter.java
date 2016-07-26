/*   1:    */ package org.apache.commons.io.filefilter;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Iterator;
/*   7:    */ import java.util.List;
/*   8:    */ 
/*   9:    */ public class AndFileFilter
/*  10:    */   extends AbstractFileFilter
/*  11:    */   implements ConditionalFileFilter
/*  12:    */ {
/*  13:    */   private List fileFilters;
/*  14:    */   
/*  15:    */   public AndFileFilter()
/*  16:    */   {
/*  17: 50 */     this.fileFilters = new ArrayList();
/*  18:    */   }
/*  19:    */   
/*  20:    */   public AndFileFilter(List fileFilters)
/*  21:    */   {
/*  22: 61 */     if (fileFilters == null) {
/*  23: 62 */       this.fileFilters = new ArrayList();
/*  24:    */     } else {
/*  25: 64 */       this.fileFilters = new ArrayList(fileFilters);
/*  26:    */     }
/*  27:    */   }
/*  28:    */   
/*  29:    */   public AndFileFilter(IOFileFilter filter1, IOFileFilter filter2)
/*  30:    */   {
/*  31: 76 */     if ((filter1 == null) || (filter2 == null)) {
/*  32: 77 */       throw new IllegalArgumentException("The filters must not be null");
/*  33:    */     }
/*  34: 79 */     this.fileFilters = new ArrayList();
/*  35: 80 */     addFileFilter(filter1);
/*  36: 81 */     addFileFilter(filter2);
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void addFileFilter(IOFileFilter ioFileFilter)
/*  40:    */   {
/*  41: 88 */     this.fileFilters.add(ioFileFilter);
/*  42:    */   }
/*  43:    */   
/*  44:    */   public List getFileFilters()
/*  45:    */   {
/*  46: 95 */     return Collections.unmodifiableList(this.fileFilters);
/*  47:    */   }
/*  48:    */   
/*  49:    */   public boolean removeFileFilter(IOFileFilter ioFileFilter)
/*  50:    */   {
/*  51:102 */     return this.fileFilters.remove(ioFileFilter);
/*  52:    */   }
/*  53:    */   
/*  54:    */   public void setFileFilters(List fileFilters)
/*  55:    */   {
/*  56:109 */     this.fileFilters = new ArrayList(fileFilters);
/*  57:    */   }
/*  58:    */   
/*  59:    */   public boolean accept(File file)
/*  60:    */   {
/*  61:116 */     if (this.fileFilters.size() == 0) {
/*  62:117 */       return false;
/*  63:    */     }
/*  64:119 */     for (Iterator iter = this.fileFilters.iterator(); iter.hasNext();)
/*  65:    */     {
/*  66:120 */       IOFileFilter fileFilter = (IOFileFilter)iter.next();
/*  67:121 */       if (!fileFilter.accept(file)) {
/*  68:122 */         return false;
/*  69:    */       }
/*  70:    */     }
/*  71:125 */     return true;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public boolean accept(File file, String name)
/*  75:    */   {
/*  76:132 */     if (this.fileFilters.size() == 0) {
/*  77:133 */       return false;
/*  78:    */     }
/*  79:135 */     for (Iterator iter = this.fileFilters.iterator(); iter.hasNext();)
/*  80:    */     {
/*  81:136 */       IOFileFilter fileFilter = (IOFileFilter)iter.next();
/*  82:137 */       if (!fileFilter.accept(file, name)) {
/*  83:138 */         return false;
/*  84:    */       }
/*  85:    */     }
/*  86:141 */     return true;
/*  87:    */   }
/*  88:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.filefilter.AndFileFilter
 * JD-Core Version:    0.7.0.1
 */