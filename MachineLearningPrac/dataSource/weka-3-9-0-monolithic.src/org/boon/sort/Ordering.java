/*   1:    */ package org.boon.sort;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.List;
/*   5:    */ import org.boon.Lists;
/*   6:    */ 
/*   7:    */ public class Ordering
/*   8:    */ {
/*   9:    */   public static <T> T search(List<T> list, T item)
/*  10:    */   {
/*  11: 54 */     if (list.size() > 1)
/*  12:    */     {
/*  13: 56 */       Object o = list;
/*  14: 57 */       int index = Collections.binarySearch((List)o, item);
/*  15: 58 */       return list.get(index);
/*  16:    */     }
/*  17: 60 */     return null;
/*  18:    */   }
/*  19:    */   
/*  20:    */   public static int searchForIndex(List<?> list, Object item)
/*  21:    */   {
/*  22: 77 */     if (list.size() > 1)
/*  23:    */     {
/*  24: 79 */       Object o = list;
/*  25: 80 */       return Collections.binarySearch((List)o, item);
/*  26:    */     }
/*  27: 82 */     return -1;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public static <T> T max(List<T> list)
/*  31:    */   {
/*  32: 97 */     if (list.size() > 1)
/*  33:    */     {
/*  34: 98 */       Sorting.sortDesc(list);
/*  35:    */       
/*  36:100 */       return list.get(0);
/*  37:    */     }
/*  38:102 */     return null;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public static <T> T max(T[] array)
/*  42:    */   {
/*  43:116 */     if (array.length > 1)
/*  44:    */     {
/*  45:117 */       Sorting.sortDesc(array);
/*  46:    */       
/*  47:119 */       return array[0];
/*  48:    */     }
/*  49:121 */     return null;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public static <T> T firstOf(List<T> list, Sort... sorts)
/*  53:    */   {
/*  54:137 */     if (list.size() > 1)
/*  55:    */     {
/*  56:138 */       Sorting.sort(list, sorts);
/*  57:    */       
/*  58:140 */       return list.get(0);
/*  59:    */     }
/*  60:142 */     return null;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public static <T> List<T> firstOf(List<T> list, int count, Sort... sorts)
/*  64:    */   {
/*  65:157 */     if (list.size() > 1)
/*  66:    */     {
/*  67:158 */       Sorting.sort(list, sorts);
/*  68:    */       
/*  69:160 */       return Lists.sliceOf(list, 0, count);
/*  70:    */     }
/*  71:162 */     return null;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public static <T> T lastOf(List<T> list, Sort... sorts)
/*  75:    */   {
/*  76:176 */     if (list.size() > 1)
/*  77:    */     {
/*  78:177 */       Sorting.sort(list, sorts);
/*  79:    */       
/*  80:179 */       return list.get(list.size() - 1);
/*  81:    */     }
/*  82:181 */     return null;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public static <T> List<T> lastOf(List<T> list, int count, Sort... sorts)
/*  86:    */   {
/*  87:196 */     if (list.size() > 1)
/*  88:    */     {
/*  89:197 */       Sorting.sort(list, sorts);
/*  90:    */       
/*  91:199 */       return Lists.endSliceOf(list, count * -1);
/*  92:    */     }
/*  93:201 */     return null;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public static <T> T max(List<T> list, String sortBy)
/*  97:    */   {
/*  98:216 */     if (list.size() > 1)
/*  99:    */     {
/* 100:217 */       Sorting.sortDesc(list, sortBy);
/* 101:    */       
/* 102:219 */       return list.get(0);
/* 103:    */     }
/* 104:221 */     return null;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public static <T> T max(T[] array, String sortBy)
/* 108:    */   {
/* 109:237 */     if (array.length > 1)
/* 110:    */     {
/* 111:238 */       Sorting.sortDesc(array, sortBy);
/* 112:    */       
/* 113:240 */       return array[0];
/* 114:    */     }
/* 115:242 */     return null;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public static <T> List<T> least(List<T> list, int count)
/* 119:    */   {
/* 120:255 */     if (list.size() > 1)
/* 121:    */     {
/* 122:256 */       Sorting.sort(list);
/* 123:    */       
/* 124:258 */       return Lists.sliceOf(list, 0, count);
/* 125:    */     }
/* 126:260 */     return null;
/* 127:    */   }
/* 128:    */   
/* 129:    */   public static <T> List<T> least(List<T> list, String sortBy, int count)
/* 130:    */   {
/* 131:275 */     if (list.size() > 1)
/* 132:    */     {
/* 133:276 */       Sorting.sort(list, sortBy);
/* 134:    */       
/* 135:278 */       return Lists.sliceOf(list, 0, count);
/* 136:    */     }
/* 137:280 */     return null;
/* 138:    */   }
/* 139:    */   
/* 140:    */   public static <T> List<T> greatest(List<T> list, int count)
/* 141:    */   {
/* 142:294 */     if (list.size() > 1)
/* 143:    */     {
/* 144:295 */       Sorting.sortDesc(list);
/* 145:    */       
/* 146:297 */       return Lists.sliceOf(list, 0, count);
/* 147:    */     }
/* 148:299 */     return null;
/* 149:    */   }
/* 150:    */   
/* 151:    */   public static <T> List<T> greatest(List<T> list, String sortBy, int count)
/* 152:    */   {
/* 153:314 */     if (list.size() > 1)
/* 154:    */     {
/* 155:315 */       Sorting.sortDesc(list, sortBy);
/* 156:    */       
/* 157:317 */       return Lists.sliceOf(list, 0, count);
/* 158:    */     }
/* 159:319 */     return null;
/* 160:    */   }
/* 161:    */   
/* 162:    */   public static <T> T min(List<T> list)
/* 163:    */   {
/* 164:331 */     if (list.size() > 1)
/* 165:    */     {
/* 166:332 */       Sorting.sort(list);
/* 167:    */       
/* 168:334 */       return list.get(0);
/* 169:    */     }
/* 170:336 */     return null;
/* 171:    */   }
/* 172:    */   
/* 173:    */   public static <T> T min(T[] array)
/* 174:    */   {
/* 175:350 */     if (array.length > 1)
/* 176:    */     {
/* 177:351 */       Sorting.sort(array);
/* 178:    */       
/* 179:353 */       return array[0];
/* 180:    */     }
/* 181:355 */     return null;
/* 182:    */   }
/* 183:    */   
/* 184:    */   public static <T> T min(List<T> list, String sortBy)
/* 185:    */   {
/* 186:368 */     if (list.size() > 1)
/* 187:    */     {
/* 188:369 */       Sorting.sort(list, sortBy);
/* 189:    */       
/* 190:371 */       return list.get(0);
/* 191:    */     }
/* 192:373 */     return null;
/* 193:    */   }
/* 194:    */   
/* 195:    */   public static <T> T min(T[] array, String sortBy)
/* 196:    */   {
/* 197:389 */     if (array.length > 1)
/* 198:    */     {
/* 199:390 */       Sorting.sort(array, sortBy);
/* 200:    */       
/* 201:392 */       return array[0];
/* 202:    */     }
/* 203:394 */     return null;
/* 204:    */   }
/* 205:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.sort.Ordering
 * JD-Core Version:    0.7.0.1
 */