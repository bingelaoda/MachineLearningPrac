/*   1:    */ package org.boon.sort;
/*   2:    */ 
/*   3:    */ import java.util.Comparator;
/*   4:    */ import java.util.List;
/*   5:    */ import java.util.Map;
/*   6:    */ import org.boon.Exceptions;
/*   7:    */ import org.boon.Str;
/*   8:    */ import org.boon.core.reflection.BeanUtils;
/*   9:    */ import org.boon.core.reflection.fields.FieldAccess;
/*  10:    */ import org.boon.primitive.Chr;
/*  11:    */ 
/*  12:    */ public final class UniversalComparator
/*  13:    */   implements Comparator<Object>
/*  14:    */ {
/*  15:    */   final String sortBy;
/*  16:    */   final Map<String, FieldAccess> fields;
/*  17:    */   final SortType sortType;
/*  18:    */   final List<Comparator> comparators;
/*  19:    */   private final boolean byPath;
/*  20:    */   
/*  21:    */   public UniversalComparator(String sortBy, Map<String, FieldAccess> fields, SortType sortType, List<Comparator> comparators)
/*  22:    */   {
/*  23: 58 */     this.sortBy = sortBy;
/*  24: 59 */     this.fields = fields;
/*  25: 60 */     this.sortType = sortType;
/*  26: 61 */     this.comparators = comparators;
/*  27:    */     
/*  28: 63 */     this.byPath = Str.in(Chr.array(new char[] { '.', '[', ']', '/' }), sortBy);
/*  29:    */   }
/*  30:    */   
/*  31:    */   public final int compare(Object o1, Object o2)
/*  32:    */   {
/*  33:    */     Object value2;
/*  34:    */     Object value1;
/*  35:    */     Object value2;
/*  36: 75 */     if ((this.byPath) || ((o1 instanceof Map)))
/*  37:    */     {
/*  38:    */       Object value2;
/*  39: 77 */       if (this.sortType == SortType.ASCENDING)
/*  40:    */       {
/*  41: 78 */         Object value1 = BeanUtils.atIndex(o1, this.sortBy);
/*  42: 79 */         value2 = BeanUtils.atIndex(o2, this.sortBy);
/*  43:    */       }
/*  44:    */       else
/*  45:    */       {
/*  46: 81 */         Object value1 = BeanUtils.atIndex(o2, this.sortBy);
/*  47: 82 */         value2 = BeanUtils.atIndex(o1, this.sortBy);
/*  48:    */       }
/*  49:    */     }
/*  50:    */     else
/*  51:    */     {
/*  52:    */       Object value2;
/*  53: 86 */       if ((this.sortBy.equals("this")) && ((o1 instanceof Comparable)))
/*  54:    */       {
/*  55:    */         Object value2;
/*  56: 87 */         if (this.sortType == SortType.ASCENDING)
/*  57:    */         {
/*  58: 88 */           Object value1 = o1;
/*  59: 89 */           value2 = o2;
/*  60:    */         }
/*  61:    */         else
/*  62:    */         {
/*  63: 91 */           Object value1 = o2;
/*  64: 92 */           value2 = o1;
/*  65:    */         }
/*  66:    */       }
/*  67:    */       else
/*  68:    */       {
/*  69: 98 */         FieldAccess field = (FieldAccess)this.fields.get(this.sortBy);
/*  70: 99 */         if (field == null) {
/*  71:100 */           Exceptions.die(Str.lines(new String[] { "The fields was null for sortBy " + this.sortBy, String.format("fields = %s", new Object[] { this.fields }), String.format("Outer object type = %s", new Object[] { o1.getClass().getName() }), String.format("Outer object is %s", new Object[] { o1 }) }));
/*  72:    */         }
/*  73:    */         Object value2;
/*  74:108 */         if (this.sortType == SortType.ASCENDING)
/*  75:    */         {
/*  76:109 */           Object value1 = field.getValue(o1);
/*  77:110 */           value2 = field.getValue(o2);
/*  78:    */         }
/*  79:    */         else
/*  80:    */         {
/*  81:112 */           value1 = field.getValue(o2);
/*  82:113 */           value2 = field.getValue(o1);
/*  83:    */         }
/*  84:    */       }
/*  85:    */     }
/*  86:118 */     int compare = Sorting.compare(value1, value2);
/*  87:119 */     if (compare == 0) {
/*  88:120 */       for (Comparator comparator : this.comparators)
/*  89:    */       {
/*  90:121 */         compare = comparator.compare(o1, o2);
/*  91:122 */         if (compare != 0) {
/*  92:    */           break;
/*  93:    */         }
/*  94:    */       }
/*  95:    */     }
/*  96:127 */     return compare;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public static Comparator universalComparator(String sortBy, Map<String, FieldAccess> fields, SortType sortType, List<Comparator> comparators)
/* 100:    */   {
/* 101:133 */     return new UniversalComparator(sortBy, fields, sortType, comparators);
/* 102:    */   }
/* 103:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.sort.UniversalComparator
 * JD-Core Version:    0.7.0.1
 */