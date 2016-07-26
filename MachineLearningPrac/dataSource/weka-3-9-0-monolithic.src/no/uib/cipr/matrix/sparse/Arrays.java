/*   1:    */ package no.uib.cipr.matrix.sparse;
/*   2:    */ 
/*   3:    */ @Deprecated
/*   4:    */ class Arrays
/*   5:    */ {
/*   6:    */   public static int binarySearchGreater(int[] index, int key, int begin, int end)
/*   7:    */   {
/*   8: 53 */     return binarySearchInterval(index, key, begin, end, true);
/*   9:    */   }
/*  10:    */   
/*  11:    */   public static int binarySearchGreater(int[] index, int key)
/*  12:    */   {
/*  13: 68 */     return binarySearchInterval(index, key, 0, index.length, true);
/*  14:    */   }
/*  15:    */   
/*  16:    */   public static int binarySearchSmaller(int[] index, int key, int begin, int end)
/*  17:    */   {
/*  18: 88 */     return binarySearchInterval(index, key, begin, end, false);
/*  19:    */   }
/*  20:    */   
/*  21:    */   public static int binarySearchSmaller(int[] index, int key)
/*  22:    */   {
/*  23:103 */     return binarySearchInterval(index, key, 0, index.length, false);
/*  24:    */   }
/*  25:    */   
/*  26:    */   public static int binarySearch(int[] index, int key, int begin, int end)
/*  27:    */   {
/*  28:120 */     return java.util.Arrays.binarySearch(index, begin, end, key);
/*  29:    */   }
/*  30:    */   
/*  31:    */   private static int binarySearchInterval(int[] index, int key, int begin, int end, boolean greater)
/*  32:    */   {
/*  33:127 */     if (begin == end)
/*  34:    */     {
/*  35:128 */       if (greater) {
/*  36:129 */         return end;
/*  37:    */       }
/*  38:131 */       return begin - 1;
/*  39:    */     }
/*  40:133 */     end--;
/*  41:134 */     int mid = end + begin >> 1;
/*  42:137 */     while (begin <= end)
/*  43:    */     {
/*  44:138 */       mid = end + begin >> 1;
/*  45:140 */       if (index[mid] < key) {
/*  46:141 */         begin = mid + 1;
/*  47:142 */       } else if (index[mid] > key) {
/*  48:143 */         end = mid - 1;
/*  49:    */       } else {
/*  50:145 */         return mid;
/*  51:    */       }
/*  52:    */     }
/*  53:149 */     if (((greater) && (index[mid] >= key)) || ((!greater) && (index[mid] <= key))) {
/*  54:150 */       return mid;
/*  55:    */     }
/*  56:152 */     if (greater) {
/*  57:153 */       return mid + 1;
/*  58:    */     }
/*  59:155 */     return mid - 1;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public static int[] bandwidth(int num, int[] ind)
/*  63:    */   {
/*  64:169 */     int[] nz = new int[num];
/*  65:171 */     for (int i = 0; i < ind.length; i++) {
/*  66:172 */       nz[ind[i]] += 1;
/*  67:    */     }
/*  68:174 */     return nz;
/*  69:    */   }
/*  70:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.Arrays
 * JD-Core Version:    0.7.0.1
 */