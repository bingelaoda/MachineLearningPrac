/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collection;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ 
/*   7:    */ @Deprecated
/*   8:    */ public class FastVector<E>
/*   9:    */   extends ArrayList<E>
/*  10:    */   implements Copyable, RevisionHandler
/*  11:    */ {
/*  12:    */   private static final long serialVersionUID = -2173635135622930169L;
/*  13:    */   
/*  14:    */   public FastVector() {}
/*  15:    */   
/*  16:    */   public FastVector(int capacity)
/*  17:    */   {
/*  18: 54 */     super(capacity);
/*  19:    */   }
/*  20:    */   
/*  21:    */   public final void addElement(E element)
/*  22:    */   {
/*  23: 64 */     add(element);
/*  24:    */   }
/*  25:    */   
/*  26:    */   public final FastVector<E> copy()
/*  27:    */   {
/*  28: 74 */     return (FastVector)Utils.cast(clone());
/*  29:    */   }
/*  30:    */   
/*  31:    */   public final FastVector<E> copyElements()
/*  32:    */   {
/*  33: 85 */     FastVector<E> copy = copy();
/*  34: 86 */     for (int i = 0; i < size(); i++) {
/*  35: 87 */       copy.set(i, Utils.cast(((Copyable)get(i)).copy()));
/*  36:    */     }
/*  37: 89 */     return copy;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public final E elementAt(int index)
/*  41:    */   {
/*  42: 99 */     return get(index);
/*  43:    */   }
/*  44:    */   
/*  45:    */   public final Enumeration<E> elements()
/*  46:    */   {
/*  47:108 */     return new WekaEnumeration(this);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public final Enumeration<E> elements(int index)
/*  51:    */   {
/*  52:119 */     return new WekaEnumeration(this, index);
/*  53:    */   }
/*  54:    */   
/*  55:    */   public final E firstElement()
/*  56:    */   {
/*  57:128 */     return get(0);
/*  58:    */   }
/*  59:    */   
/*  60:    */   public final void insertElementAt(E element, int index)
/*  61:    */   {
/*  62:138 */     add(index, element);
/*  63:    */   }
/*  64:    */   
/*  65:    */   public final E lastElement()
/*  66:    */   {
/*  67:147 */     return get(size() - 1);
/*  68:    */   }
/*  69:    */   
/*  70:    */   public final void removeElementAt(int index)
/*  71:    */   {
/*  72:156 */     remove(index);
/*  73:    */   }
/*  74:    */   
/*  75:    */   public final void removeAllElements()
/*  76:    */   {
/*  77:163 */     clear();
/*  78:    */   }
/*  79:    */   
/*  80:    */   public final void appendElements(Collection<? extends E> toAppend)
/*  81:    */   {
/*  82:172 */     addAll(toAppend);
/*  83:    */   }
/*  84:    */   
/*  85:    */   public final void setCapacity(int capacity)
/*  86:    */   {
/*  87:181 */     ensureCapacity(capacity);
/*  88:    */   }
/*  89:    */   
/*  90:    */   public final void setElementAt(E element, int index)
/*  91:    */   {
/*  92:191 */     set(index, element);
/*  93:    */   }
/*  94:    */   
/*  95:    */   public final void swap(int first, int second)
/*  96:    */   {
/*  97:202 */     E in = get(first);
/*  98:203 */     set(first, get(second));
/*  99:204 */     set(second, in);
/* 100:    */   }
/* 101:    */   
/* 102:    */   public String getRevision()
/* 103:    */   {
/* 104:214 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 105:    */   }
/* 106:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.FastVector
 * JD-Core Version:    0.7.0.1
 */