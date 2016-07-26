/*   1:    */ package org.boon.datarepo.impl.maps;
/*   2:    */ 
/*   3:    */ import java.util.Comparator;
/*   4:    */ import java.util.concurrent.ConcurrentSkipListMap;
/*   5:    */ import org.boon.core.Conversions;
/*   6:    */ import org.boon.datarepo.spi.TypedMap;
/*   7:    */ 
/*   8:    */ public class JavaUtilNavigableMap<K, V>
/*   9:    */   extends ConcurrentSkipListMap<K, V>
/*  10:    */   implements TypedMap<K, V>
/*  11:    */ {
/*  12:    */   public JavaUtilNavigableMap() {}
/*  13:    */   
/*  14:    */   public JavaUtilNavigableMap(Comparator<? super K> comparator)
/*  15:    */   {
/*  16: 45 */     super(comparator);
/*  17:    */   }
/*  18:    */   
/*  19:    */   public final boolean put(K key, boolean i)
/*  20:    */   {
/*  21: 50 */     return ((Boolean)super.put(key, Conversions.wrapAsObject(i))).booleanValue();
/*  22:    */   }
/*  23:    */   
/*  24:    */   public final boolean getBoolean(K key)
/*  25:    */   {
/*  26: 55 */     return ((Boolean)super.get(key)).booleanValue();
/*  27:    */   }
/*  28:    */   
/*  29:    */   public V put(byte key, V v)
/*  30:    */   {
/*  31: 60 */     return super.put(Conversions.wrapAsObject(key), v);
/*  32:    */   }
/*  33:    */   
/*  34:    */   public final byte put(K key, byte i)
/*  35:    */   {
/*  36: 65 */     return ((Byte)super.put(key, Conversions.wrapAsObject(i))).byteValue();
/*  37:    */   }
/*  38:    */   
/*  39:    */   public final byte getByte(K key)
/*  40:    */   {
/*  41: 70 */     return ((Byte)super.get(key)).byteValue();
/*  42:    */   }
/*  43:    */   
/*  44:    */   public V put(short key, V v)
/*  45:    */   {
/*  46: 75 */     return super.put(Conversions.wrapAsObject(key), v);
/*  47:    */   }
/*  48:    */   
/*  49:    */   public final short put(K key, short i)
/*  50:    */   {
/*  51: 80 */     return ((Short)super.put(key, Conversions.wrapAsObject(i))).shortValue();
/*  52:    */   }
/*  53:    */   
/*  54:    */   public final short getShort(K key)
/*  55:    */   {
/*  56: 85 */     return ((Short)super.get(key)).shortValue();
/*  57:    */   }
/*  58:    */   
/*  59:    */   public V put(int key, V v)
/*  60:    */   {
/*  61: 90 */     return super.put(Conversions.wrapAsObject(key), v);
/*  62:    */   }
/*  63:    */   
/*  64:    */   public final int put(K key, int i)
/*  65:    */   {
/*  66: 95 */     return ((Integer)super.put(key, Conversions.wrapAsObject(i))).intValue();
/*  67:    */   }
/*  68:    */   
/*  69:    */   public final int getInt(K key)
/*  70:    */   {
/*  71:100 */     return ((Integer)super.get(key)).intValue();
/*  72:    */   }
/*  73:    */   
/*  74:    */   public V put(long key, V v)
/*  75:    */   {
/*  76:105 */     return super.put(Conversions.wrapAsObject(key), v);
/*  77:    */   }
/*  78:    */   
/*  79:    */   public final long put(K key, long i)
/*  80:    */   {
/*  81:110 */     return ((Long)super.put(key, Conversions.wrapAsObject(i))).longValue();
/*  82:    */   }
/*  83:    */   
/*  84:    */   public final long getLong(K key)
/*  85:    */   {
/*  86:115 */     return ((Long)super.get(key)).longValue();
/*  87:    */   }
/*  88:    */   
/*  89:    */   public V put(float key, V v)
/*  90:    */   {
/*  91:120 */     return super.put(Conversions.wrapAsObject(key), v);
/*  92:    */   }
/*  93:    */   
/*  94:    */   public final float put(K key, float i)
/*  95:    */   {
/*  96:125 */     return ((Float)super.put(key, Conversions.wrapAsObject(i))).floatValue();
/*  97:    */   }
/*  98:    */   
/*  99:    */   public final float getFloat(K key)
/* 100:    */   {
/* 101:130 */     return ((Float)super.get(key)).floatValue();
/* 102:    */   }
/* 103:    */   
/* 104:    */   public V put(double key, V v)
/* 105:    */   {
/* 106:135 */     return super.put(Conversions.wrapAsObject(key), v);
/* 107:    */   }
/* 108:    */   
/* 109:    */   public final double put(K key, double i)
/* 110:    */   {
/* 111:140 */     return ((Double)super.put(key, Conversions.wrapAsObject(i))).doubleValue();
/* 112:    */   }
/* 113:    */   
/* 114:    */   public final double getDouble(K key)
/* 115:    */   {
/* 116:145 */     return ((Double)super.get(key)).doubleValue();
/* 117:    */   }
/* 118:    */   
/* 119:    */   public final V put(char key, V v)
/* 120:    */   {
/* 121:150 */     return super.put(Conversions.wrapAsObject(key), v);
/* 122:    */   }
/* 123:    */   
/* 124:    */   public final char put(K key, char i)
/* 125:    */   {
/* 126:155 */     return ((Character)super.put(key, Conversions.wrapAsObject(i))).charValue();
/* 127:    */   }
/* 128:    */   
/* 129:    */   public final char getChar(K key)
/* 130:    */   {
/* 131:160 */     return ((Character)super.get(key)).charValue();
/* 132:    */   }
/* 133:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.impl.maps.JavaUtilNavigableMap
 * JD-Core Version:    0.7.0.1
 */