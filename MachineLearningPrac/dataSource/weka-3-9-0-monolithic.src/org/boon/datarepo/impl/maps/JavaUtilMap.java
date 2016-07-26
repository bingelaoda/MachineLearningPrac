/*   1:    */ package org.boon.datarepo.impl.maps;
/*   2:    */ 
/*   3:    */ import java.util.concurrent.ConcurrentHashMap;
/*   4:    */ import org.boon.core.Conversions;
/*   5:    */ import org.boon.datarepo.spi.TypedMap;
/*   6:    */ 
/*   7:    */ public class JavaUtilMap<K, V>
/*   8:    */   extends ConcurrentHashMap<K, V>
/*   9:    */   implements TypedMap<K, V>
/*  10:    */ {
/*  11:    */   public final boolean put(K key, boolean i)
/*  12:    */   {
/*  13: 45 */     return ((Boolean)super.put(key, Conversions.wrapAsObject(i))).booleanValue();
/*  14:    */   }
/*  15:    */   
/*  16:    */   public final boolean getBoolean(K key)
/*  17:    */   {
/*  18: 50 */     return ((Boolean)super.get(key)).booleanValue();
/*  19:    */   }
/*  20:    */   
/*  21:    */   public V put(byte key, V v)
/*  22:    */   {
/*  23: 55 */     return super.put(Conversions.wrapAsObject(key), v);
/*  24:    */   }
/*  25:    */   
/*  26:    */   public final byte put(K key, byte i)
/*  27:    */   {
/*  28: 60 */     return ((Byte)super.put(key, Conversions.wrapAsObject(i))).byteValue();
/*  29:    */   }
/*  30:    */   
/*  31:    */   public final byte getByte(K key)
/*  32:    */   {
/*  33: 65 */     return ((Byte)super.get(key)).byteValue();
/*  34:    */   }
/*  35:    */   
/*  36:    */   public V put(short key, V v)
/*  37:    */   {
/*  38: 70 */     return super.put(Conversions.wrapAsObject(key), v);
/*  39:    */   }
/*  40:    */   
/*  41:    */   public final short put(K key, short i)
/*  42:    */   {
/*  43: 75 */     return ((Short)super.put(key, Conversions.wrapAsObject(i))).shortValue();
/*  44:    */   }
/*  45:    */   
/*  46:    */   public final short getShort(K key)
/*  47:    */   {
/*  48: 80 */     return ((Short)super.get(key)).shortValue();
/*  49:    */   }
/*  50:    */   
/*  51:    */   public V put(int key, V v)
/*  52:    */   {
/*  53: 85 */     return super.put(Conversions.wrapAsObject(key), v);
/*  54:    */   }
/*  55:    */   
/*  56:    */   public final int put(K key, int i)
/*  57:    */   {
/*  58: 90 */     return ((Integer)super.put(key, Conversions.wrapAsObject(i))).intValue();
/*  59:    */   }
/*  60:    */   
/*  61:    */   public final int getInt(K key)
/*  62:    */   {
/*  63: 95 */     return ((Integer)super.get(key)).intValue();
/*  64:    */   }
/*  65:    */   
/*  66:    */   public V put(long key, V v)
/*  67:    */   {
/*  68:100 */     return super.put(Conversions.wrapAsObject(key), v);
/*  69:    */   }
/*  70:    */   
/*  71:    */   public final long put(K key, long i)
/*  72:    */   {
/*  73:105 */     return ((Long)super.put(key, Conversions.wrapAsObject(i))).longValue();
/*  74:    */   }
/*  75:    */   
/*  76:    */   public final long getLong(K key)
/*  77:    */   {
/*  78:110 */     return ((Long)super.get(key)).longValue();
/*  79:    */   }
/*  80:    */   
/*  81:    */   public V put(float key, V v)
/*  82:    */   {
/*  83:115 */     return super.put(Conversions.wrapAsObject(key), v);
/*  84:    */   }
/*  85:    */   
/*  86:    */   public final float put(K key, float i)
/*  87:    */   {
/*  88:120 */     return ((Float)super.put(key, Conversions.wrapAsObject(i))).floatValue();
/*  89:    */   }
/*  90:    */   
/*  91:    */   public final float getFloat(K key)
/*  92:    */   {
/*  93:125 */     return ((Float)super.get(key)).floatValue();
/*  94:    */   }
/*  95:    */   
/*  96:    */   public V put(double key, V v)
/*  97:    */   {
/*  98:130 */     return super.put(Conversions.wrapAsObject(key), v);
/*  99:    */   }
/* 100:    */   
/* 101:    */   public final double put(K key, double i)
/* 102:    */   {
/* 103:135 */     return ((Double)super.put(key, Conversions.wrapAsObject(i))).doubleValue();
/* 104:    */   }
/* 105:    */   
/* 106:    */   public final double getDouble(K key)
/* 107:    */   {
/* 108:140 */     return ((Double)super.get(key)).doubleValue();
/* 109:    */   }
/* 110:    */   
/* 111:    */   public final V put(char key, V v)
/* 112:    */   {
/* 113:145 */     return super.put(Conversions.wrapAsObject(key), v);
/* 114:    */   }
/* 115:    */   
/* 116:    */   public final char put(K key, char i)
/* 117:    */   {
/* 118:150 */     return ((Character)super.put(key, Conversions.wrapAsObject(i))).charValue();
/* 119:    */   }
/* 120:    */   
/* 121:    */   public final char getChar(K key)
/* 122:    */   {
/* 123:155 */     return ((Character)super.get(key)).charValue();
/* 124:    */   }
/* 125:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.impl.maps.JavaUtilMap
 * JD-Core Version:    0.7.0.1
 */