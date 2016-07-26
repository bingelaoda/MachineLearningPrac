/*   1:    */ package org.boon.criteria.internal;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.List;
/*   6:    */ import org.boon.Lists;
/*   7:    */ import org.boon.core.reflection.Invoker;
/*   8:    */ import org.boon.criteria.ObjectFilter;
/*   9:    */ 
/*  10:    */ public abstract class Group
/*  11:    */   extends Criteria
/*  12:    */ {
/*  13:    */   protected List<Criteria> expressions;
/*  14: 46 */   private Grouping grouping = Grouping.AND;
/*  15:    */   
/*  16:    */   public Group(Grouping grouping, Criteria... expressions)
/*  17:    */   {
/*  18: 52 */     this.grouping = grouping;
/*  19: 53 */     this.expressions = Lists.list(expressions);
/*  20:    */   }
/*  21:    */   
/*  22:    */   public Group(Grouping grouping, Class<?> cls, List<?> list)
/*  23:    */   {
/*  24: 60 */     this.grouping = grouping;
/*  25:    */     
/*  26: 62 */     ArrayList<Criteria> criteriaArrayList = new ArrayList();
/*  27: 63 */     List<List<?>> lists = list;
/*  28: 64 */     for (List args : lists)
/*  29:    */     {
/*  30: 65 */       args = new ArrayList(args);
/*  31: 66 */       args.add(1, cls);
/*  32:    */       
/*  33: 68 */       Object o = Lists.atIndex(args, -1);
/*  34: 69 */       if (!(o instanceof List)) {
/*  35: 70 */         Lists.atIndex(args, -1, Collections.singletonList(o));
/*  36:    */       }
/*  37: 72 */       Criteria criteria = (Criteria)Invoker.invokeFromObject(ObjectFilter.class, "createCriteriaFromClass", args);
/*  38: 73 */       criteriaArrayList.add(criteria);
/*  39:    */     }
/*  40: 75 */     this.expressions = criteriaArrayList;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public Grouping getGrouping()
/*  44:    */   {
/*  45: 81 */     return this.grouping;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public List<Criteria> getExpressions()
/*  49:    */   {
/*  50: 86 */     return this.expressions;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public boolean equals(Object o)
/*  54:    */   {
/*  55: 92 */     if (this == o) {
/*  56: 92 */       return true;
/*  57:    */     }
/*  58: 93 */     if ((o == null) || (getClass() != o.getClass())) {
/*  59: 93 */       return false;
/*  60:    */     }
/*  61: 95 */     Group group = (Group)o;
/*  62: 97 */     if (this.expressions != null ? !this.expressions.equals(group.expressions) : group.expressions != null) {
/*  63: 97 */       return false;
/*  64:    */     }
/*  65: 98 */     if (this.grouping != group.grouping) {
/*  66: 98 */       return false;
/*  67:    */     }
/*  68:100 */     return true;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public static class And
/*  72:    */     extends Group
/*  73:    */   {
/*  74:    */     public And(Criteria... expressions)
/*  75:    */     {
/*  76:109 */       super(expressions);
/*  77:    */     }
/*  78:    */     
/*  79:    */     public And(Class<?> cls, List<?> list)
/*  80:    */     {
/*  81:113 */       super(cls, list);
/*  82:    */     }
/*  83:    */     
/*  84:    */     public boolean test(Object owner)
/*  85:    */     {
/*  86:118 */       for (Criteria c : this.expressions) {
/*  87:119 */         if (!c.test(owner)) {
/*  88:120 */           return false;
/*  89:    */         }
/*  90:    */       }
/*  91:123 */       return true;
/*  92:    */     }
/*  93:    */   }
/*  94:    */   
/*  95:    */   public static class Or
/*  96:    */     extends Group
/*  97:    */   {
/*  98:    */     public Or(Criteria... expressions)
/*  99:    */     {
/* 100:130 */       super(expressions);
/* 101:    */     }
/* 102:    */     
/* 103:    */     public Or(Class<?> cls, List<?> list)
/* 104:    */     {
/* 105:134 */       super(cls, list);
/* 106:    */     }
/* 107:    */     
/* 108:    */     public boolean test(Object owner)
/* 109:    */     {
/* 110:140 */       for (Criteria c : this.expressions) {
/* 111:141 */         if (c.test(owner)) {
/* 112:142 */           return true;
/* 113:    */         }
/* 114:    */       }
/* 115:145 */       return false;
/* 116:    */     }
/* 117:    */   }
/* 118:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.criteria.internal.Group
 * JD-Core Version:    0.7.0.1
 */