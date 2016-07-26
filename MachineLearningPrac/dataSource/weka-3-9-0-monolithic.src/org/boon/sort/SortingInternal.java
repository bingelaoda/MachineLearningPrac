/*   1:    */ package org.boon.sort;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.List;
/*   5:    */ import java.util.Map;
/*   6:    */ import org.boon.Exceptions;
/*   7:    */ import org.boon.core.reflection.fields.FieldAccess;
/*   8:    */ 
/*   9:    */ public class SortingInternal
/*  10:    */ {
/*  11:    */   public static void sort(List list, String sortBy, Map<String, FieldAccess> fields, boolean ascending)
/*  12:    */   {
/*  13: 56 */     sort(list, sortBy, fields, ascending, false);
/*  14:    */   }
/*  15:    */   
/*  16:    */   public static void sort(List list, String sortBy, Map<String, FieldAccess> fields, boolean ascending, boolean nullsFirst)
/*  17:    */   {
/*  18:    */     try
/*  19:    */     {
/*  20: 74 */       if ((list == null) || (list.size() == 0)) {
/*  21: 75 */         return;
/*  22:    */       }
/*  23: 79 */       Object o = list.get(0);
/*  24: 84 */       if (sortBy.equals("this"))
/*  25:    */       {
/*  26: 86 */         Collections.sort(list, Sorting.thisUniversalComparator(ascending, nullsFirst));
/*  27: 87 */         return;
/*  28:    */       }
/*  29: 92 */       FieldAccess field = (FieldAccess)fields.get(sortBy);
/*  30: 94 */       if (field != null) {
/*  31: 96 */         Collections.sort(list, Sorting.universalComparator(field, ascending, nullsFirst));
/*  32:    */       }
/*  33:    */     }
/*  34:    */     catch (Exception ex)
/*  35:    */     {
/*  36:101 */       Exceptions.handle(ex, new Object[] { "list", list, "\nsortBy", sortBy, "fields", fields, "ascending", Boolean.valueOf(ascending), "nullFirst", Boolean.valueOf(nullsFirst) });
/*  37:    */     }
/*  38:    */   }
/*  39:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.sort.SortingInternal
 * JD-Core Version:    0.7.0.1
 */