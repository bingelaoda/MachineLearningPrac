/*   1:    */ package org.boon;
/*   2:    */ 
/*   3:    */ public class Pair<K, V>
/*   4:    */   implements Entry<K, V>
/*   5:    */ {
/*   6:    */   private K k;
/*   7:    */   private V v;
/*   8:    */   
/*   9:    */   public static <K, V> Entry<K, V> entry(K k, V v)
/*  10:    */   {
/*  11: 13 */     return new Pair(k, v);
/*  12:    */   }
/*  13:    */   
/*  14:    */   public static <K, V> Pair<K, V> pair(K k, V v)
/*  15:    */   {
/*  16: 18 */     return new Pair(k, v);
/*  17:    */   }
/*  18:    */   
/*  19:    */   public static <K, V> Entry<K, V> entry(Entry<K, V> entry)
/*  20:    */   {
/*  21: 22 */     return new Pair(entry);
/*  22:    */   }
/*  23:    */   
/*  24:    */   public Pair() {}
/*  25:    */   
/*  26:    */   public Pair(Pair<K, V> impl)
/*  27:    */   {
/*  28: 33 */     Exceptions.requireNonNull(impl);
/*  29: 34 */     Exceptions.requireNonNull(impl.k);
/*  30:    */     
/*  31: 36 */     this.k = impl.k;
/*  32: 37 */     this.v = impl.v;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public Pair(Entry<K, V> entry)
/*  36:    */   {
/*  37: 41 */     Exceptions.requireNonNull(entry);
/*  38: 42 */     Exceptions.requireNonNull(entry.key());
/*  39:    */     
/*  40: 44 */     this.k = entry.key();
/*  41: 45 */     this.v = entry.value();
/*  42:    */   }
/*  43:    */   
/*  44:    */   public Pair(K k, V v)
/*  45:    */   {
/*  46: 49 */     Exceptions.requireNonNull(k);
/*  47:    */     
/*  48: 51 */     this.k = k;
/*  49: 52 */     this.v = v;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public K key()
/*  53:    */   {
/*  54: 57 */     return this.k;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public K getFirst()
/*  58:    */   {
/*  59: 62 */     return this.k;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public V getSecond()
/*  63:    */   {
/*  64: 65 */     return this.v;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public V value()
/*  68:    */   {
/*  69: 70 */     return this.v;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public K getKey()
/*  73:    */   {
/*  74: 76 */     return this.k;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public V getValue()
/*  78:    */   {
/*  79: 81 */     return this.v;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public V setValue(V value)
/*  83:    */   {
/*  84: 86 */     V old = this.v;
/*  85: 87 */     this.v = value;
/*  86: 88 */     return old;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public boolean equals(Object o)
/*  90:    */   {
/*  91: 93 */     if (this == o) {
/*  92: 93 */       return true;
/*  93:    */     }
/*  94: 94 */     if ((o == null) || (getClass() != o.getClass())) {
/*  95: 94 */       return false;
/*  96:    */     }
/*  97: 96 */     Pair entry = (Pair)o;
/*  98: 97 */     return equals(entry);
/*  99:    */   }
/* 100:    */   
/* 101:    */   public boolean equals(Entry entry)
/* 102:    */   {
/* 103:103 */     if (this.k != null ? !this.k.equals(entry.key()) : entry.key() != null) {
/* 104:103 */       return false;
/* 105:    */     }
/* 106:104 */     return this.v != null ? this.v.equals(entry.value()) : entry.value() == null;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public int hashCode()
/* 110:    */   {
/* 111:110 */     int result = this.k != null ? this.k.hashCode() : 0;
/* 112:111 */     result = 31 * result + (this.v != null ? this.v.hashCode() : 0);
/* 113:112 */     return result;
/* 114:    */   }
/* 115:    */   
/* 116:    */   public int compareTo(Entry entry)
/* 117:    */   {
/* 118:117 */     Exceptions.requireNonNull(entry);
/* 119:118 */     return key().toString().compareTo(entry.key().toString());
/* 120:    */   }
/* 121:    */   
/* 122:    */   public String toString()
/* 123:    */   {
/* 124:123 */     return "{\"k\":" + this.k + ", \"v\":" + this.v + '}';
/* 125:    */   }
/* 126:    */   
/* 127:    */   public void setFirst(K first)
/* 128:    */   {
/* 129:130 */     this.k = first;
/* 130:    */   }
/* 131:    */   
/* 132:    */   public void setSecond(V v)
/* 133:    */   {
/* 134:134 */     this.v = v;
/* 135:    */   }
/* 136:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.Pair
 * JD-Core Version:    0.7.0.1
 */