/*   1:    */ package no.uib.cipr.matrix.sparse;
/*   2:    */ 
/*   3:    */ import java.util.Iterator;
/*   4:    */ import java.util.List;
/*   5:    */ 
/*   6:    */ class SuperIterator<T extends Iterable<E>, E>
/*   7:    */   implements Iterator<SuperIteratorEntry>
/*   8:    */ {
/*   9:    */   private SuperIteratorEntry<E> entry;
/*  10: 41 */   private int nextIndex = 0;
/*  11: 41 */   private int currentIndex = 0;
/*  12:    */   private Iterator<E> next;
/*  13:    */   private Iterator<E> current;
/*  14:    */   private List<T> iterable;
/*  15:    */   
/*  16:    */   public SuperIterator(List<T> iterable)
/*  17:    */   {
/*  18: 55 */     this.iterable = iterable;
/*  19: 56 */     this.entry = new SuperIteratorEntry();
/*  20: 59 */     if (iterable.size() == 0)
/*  21:    */     {
/*  22: 60 */       this.current = new DummyIterator(null);
/*  23: 61 */       this.next = new DummyIterator(null);
/*  24:    */     }
/*  25:    */     else
/*  26:    */     {
/*  27: 65 */       this.next = ((Iterable)iterable.get(this.nextIndex)).iterator();
/*  28: 66 */       moveNext();
/*  29:    */       
/*  30:    */ 
/*  31: 69 */       this.current = ((Iterable)iterable.get(this.currentIndex)).iterator();
/*  32: 70 */       moveCurrent();
/*  33: 73 */       if (this.next.hasNext()) {
/*  34: 74 */         this.next.next();
/*  35:    */       }
/*  36:    */     }
/*  37:    */   }
/*  38:    */   
/*  39:    */   private void moveNext()
/*  40:    */   {
/*  41: 79 */     while ((this.nextIndex < this.iterable.size() - 1) && (!this.next.hasNext())) {
/*  42: 80 */       this.next = ((Iterable)this.iterable.get(++this.nextIndex)).iterator();
/*  43:    */     }
/*  44:    */   }
/*  45:    */   
/*  46:    */   private void moveCurrent()
/*  47:    */   {
/*  48: 84 */     while ((this.currentIndex < this.iterable.size() - 1) && (!this.current.hasNext())) {
/*  49: 85 */       this.current = ((Iterable)this.iterable.get(++this.currentIndex)).iterator();
/*  50:    */     }
/*  51:    */   }
/*  52:    */   
/*  53:    */   public boolean hasNext()
/*  54:    */   {
/*  55: 89 */     return (this.current.hasNext()) || (this.next.hasNext());
/*  56:    */   }
/*  57:    */   
/*  58:    */   public SuperIteratorEntry<E> next()
/*  59:    */   {
/*  60: 94 */     this.entry.update(this.currentIndex, this.current.next());
/*  61:    */     
/*  62:    */ 
/*  63: 97 */     moveCurrent();
/*  64:    */     
/*  65:    */ 
/*  66:100 */     moveNext();
/*  67:101 */     if (this.next.hasNext()) {
/*  68:102 */       this.next.next();
/*  69:    */     }
/*  70:104 */     return this.entry;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void remove()
/*  74:    */   {
/*  75:108 */     this.current.remove();
/*  76:    */   }
/*  77:    */   
/*  78:    */   private class DummyIterator
/*  79:    */     implements Iterator<E>
/*  80:    */   {
/*  81:    */     private DummyIterator() {}
/*  82:    */     
/*  83:    */     public boolean hasNext()
/*  84:    */     {
/*  85:117 */       return false;
/*  86:    */     }
/*  87:    */     
/*  88:    */     public E next()
/*  89:    */     {
/*  90:121 */       return null;
/*  91:    */     }
/*  92:    */     
/*  93:    */     public void remove()
/*  94:    */     {
/*  95:125 */       throw new UnsupportedOperationException();
/*  96:    */     }
/*  97:    */   }
/*  98:    */   
/*  99:    */   public static class SuperIteratorEntry<F>
/* 100:    */   {
/* 101:    */     private int i;
/* 102:    */     private F o;
/* 103:    */     
/* 104:    */     void update(int i, F o)
/* 105:    */     {
/* 106:145 */       this.i = i;
/* 107:146 */       this.o = o;
/* 108:    */     }
/* 109:    */     
/* 110:    */     public int index()
/* 111:    */     {
/* 112:150 */       return this.i;
/* 113:    */     }
/* 114:    */     
/* 115:    */     public F get()
/* 116:    */     {
/* 117:154 */       return this.o;
/* 118:    */     }
/* 119:    */   }
/* 120:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.SuperIterator
 * JD-Core Version:    0.7.0.1
 */