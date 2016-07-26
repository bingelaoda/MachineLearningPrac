/*   1:    */ package org.boon.core;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.lang.reflect.Modifier;
/*   5:    */ import java.math.BigDecimal;
/*   6:    */ import java.math.BigInteger;
/*   7:    */ import java.nio.file.Path;
/*   8:    */ import java.util.Calendar;
/*   9:    */ import java.util.Collection;
/*  10:    */ import java.util.Currency;
/*  11:    */ import java.util.Date;
/*  12:    */ import java.util.Iterator;
/*  13:    */ import java.util.List;
/*  14:    */ import java.util.Map;
/*  15:    */ import java.util.Set;
/*  16:    */ import java.util.SortedSet;
/*  17:    */ import org.boon.Sets;
/*  18:    */ 
/*  19:    */ public class Typ
/*  20:    */ {
/*  21: 44 */   public static final Class<Object> object = Object.class;
/*  22: 45 */   public static final Class<String> string = String.class;
/*  23: 46 */   public static final Class<List> list = List.class;
/*  24: 47 */   public static final Class<CharSequence> chars = CharSequence.class;
/*  25: 48 */   public static final Class<Set> set = Set.class;
/*  26: 49 */   public static final Class<Collection> collection = Collection.class;
/*  27: 52 */   public static final Class<Comparable> comparable = Comparable.class;
/*  28: 54 */   public static final Class<Boolean> bool = Boolean.class;
/*  29: 55 */   public static final Class<Integer> integer = Integer.class;
/*  30: 56 */   public static final Class<Long> longWrapper = Long.class;
/*  31: 57 */   public static final Class<Double> doubleWrapper = Double.class;
/*  32: 58 */   public static final Class<Float> floatWrapper = Float.class;
/*  33: 59 */   public static final Class<Byte> byteWrapper = Byte.class;
/*  34: 60 */   public static final Class<Short> shortWrapper = Short.class;
/*  35: 61 */   public static final Class<BigInteger> bigInteger = BigInteger.class;
/*  36: 62 */   public static final Class<BigDecimal> bigDecimal = BigDecimal.class;
/*  37: 64 */   public static final Class<Number> number = Number.class;
/*  38: 68 */   public static final Class<?> flt = Float.TYPE;
/*  39: 69 */   public static final Class<?> lng = Long.TYPE;
/*  40: 70 */   public static final Class<?> dbl = Double.TYPE;
/*  41: 71 */   public static final Class<?> intgr = Integer.TYPE;
/*  42: 72 */   public static final Class<?> bln = Boolean.TYPE;
/*  43: 73 */   public static final Class<?> shrt = Short.TYPE;
/*  44: 74 */   public static final Class<?> chr = Character.TYPE;
/*  45: 75 */   public static final Class<?> bt = Byte.TYPE;
/*  46: 79 */   public static final Class<Date> date = Date.class;
/*  47: 80 */   public static final Class<Calendar> calendar = Calendar.class;
/*  48: 81 */   public static final Class<File> file = File.class;
/*  49: 82 */   public static final Class<Path> path = Path.class;
/*  50: 86 */   public static final Class<String[]> stringArray = [Ljava.lang.String.class;
/*  51: 87 */   public static final Class<int[]> intArray = [I.class;
/*  52: 88 */   public static final Class<byte[]> byteArray = [B.class;
/*  53: 89 */   public static final Class<short[]> shortArray = [S.class;
/*  54: 90 */   public static final Class<char[]> charArray = [C.class;
/*  55: 91 */   public static final Class<long[]> longArray = [J.class;
/*  56: 92 */   public static final Class<float[]> floatArray = [F.class;
/*  57: 93 */   public static final Class<double[]> doubleArray = [D.class;
/*  58: 94 */   public static final Class<Object[]> objectArray = [Ljava.lang.Object.class;
/*  59:    */   
/*  60:    */   public static boolean doesMapHaveKeyTypeString(Object value)
/*  61:    */   {
/*  62: 97 */     return getKeyType((Map)value) == string;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public static boolean isBasicType(Object value)
/*  66:    */   {
/*  67:101 */     return ((value instanceof Number)) || ((value instanceof CharSequence)) || ((value instanceof Date)) || ((value instanceof Calendar)) || ((value instanceof Currency)) || ((value instanceof Boolean));
/*  68:    */   }
/*  69:    */   
/*  70:    */   public static boolean isPrimitiveOrWrapper(Class<?> theClass)
/*  71:    */   {
/*  72:108 */     return (number.isAssignableFrom(theClass)) || (date.isAssignableFrom(theClass)) || (calendar.isAssignableFrom(theClass)) || (bool.isAssignableFrom(theClass)) || (theClass.isPrimitive());
/*  73:    */   }
/*  74:    */   
/*  75:    */   public static boolean isBasicType(Class<?> theClass)
/*  76:    */   {
/*  77:116 */     return (number.isAssignableFrom(theClass)) || (chars.isAssignableFrom(theClass)) || (date.isAssignableFrom(theClass)) || (calendar.isAssignableFrom(theClass)) || (bool.isAssignableFrom(theClass)) || (theClass.isPrimitive());
/*  78:    */   }
/*  79:    */   
/*  80:    */   public static boolean isMap(Class<?> thisType)
/*  81:    */   {
/*  82:125 */     return isSuperType(thisType, Map.class);
/*  83:    */   }
/*  84:    */   
/*  85:    */   public static boolean isValue(Class<?> thisType)
/*  86:    */   {
/*  87:130 */     return isSuperType(thisType, Value.class);
/*  88:    */   }
/*  89:    */   
/*  90:    */   public static boolean isCharSequence(Class<?> thisType)
/*  91:    */   {
/*  92:134 */     return isSuperType(thisType, CharSequence.class);
/*  93:    */   }
/*  94:    */   
/*  95:    */   public static boolean isCollection(Class<?> thisType)
/*  96:    */   {
/*  97:138 */     return isSuperType(thisType, Collection.class);
/*  98:    */   }
/*  99:    */   
/* 100:    */   public static boolean isList(Class<?> thisType)
/* 101:    */   {
/* 102:142 */     return isSuperType(thisType, List.class);
/* 103:    */   }
/* 104:    */   
/* 105:    */   public static boolean isSet(Class<?> thisType)
/* 106:    */   {
/* 107:146 */     return isSuperType(thisType, Set.class);
/* 108:    */   }
/* 109:    */   
/* 110:    */   public static boolean isSortedSet(Class<?> thisType)
/* 111:    */   {
/* 112:150 */     return isSuperType(thisType, SortedSet.class);
/* 113:    */   }
/* 114:    */   
/* 115:    */   public static boolean isType(Class<?> thisType, Class<?> isThisType)
/* 116:    */   {
/* 117:154 */     return isSuperType(thisType, isThisType);
/* 118:    */   }
/* 119:    */   
/* 120:    */   public static boolean isComparable(Object o)
/* 121:    */   {
/* 122:158 */     return o instanceof Comparable;
/* 123:    */   }
/* 124:    */   
/* 125:    */   public static boolean isComparable(Class<?> type)
/* 126:    */   {
/* 127:162 */     return implementsInterface(type, comparable);
/* 128:    */   }
/* 129:    */   
/* 130:    */   public static boolean isSuperClass(Class<?> type, Class<?> possibleSuperType)
/* 131:    */   {
/* 132:166 */     if (possibleSuperType.isInterface()) {
/* 133:167 */       return false;
/* 134:    */     }
/* 135:169 */     return possibleSuperType.isAssignableFrom(type);
/* 136:    */   }
/* 137:    */   
/* 138:    */   public static boolean isSuperType(Class<?> type, Class<?> possibleSuperType)
/* 139:    */   {
/* 140:175 */     return possibleSuperType.isAssignableFrom(type);
/* 141:    */   }
/* 142:    */   
/* 143:    */   public static boolean implementsInterface(Class<?> type, Class<?> interfaceType)
/* 144:    */   {
/* 145:179 */     if (!interfaceType.isInterface()) {
/* 146:180 */       return false;
/* 147:    */     }
/* 148:182 */     return interfaceType.isAssignableFrom(type);
/* 149:    */   }
/* 150:    */   
/* 151:    */   public static Class<?> getKeyType(Map<?, ?> value)
/* 152:    */   {
/* 153:188 */     if (value.size() > 0) {
/* 154:189 */       return value.keySet().iterator().next().getClass();
/* 155:    */     }
/* 156:191 */     return Object.class;
/* 157:    */   }
/* 158:    */   
/* 159:    */   public static Class<?> getComponentType(Collection value)
/* 160:    */   {
/* 161:196 */     if (value.size() > 0) {
/* 162:197 */       return value.iterator().next().getClass();
/* 163:    */     }
/* 164:199 */     return Object.class;
/* 165:    */   }
/* 166:    */   
/* 167:    */   public static <T> boolean isAbstract(Class<T> clazz)
/* 168:    */   {
/* 169:204 */     return Modifier.isAbstract(clazz.getModifiers());
/* 170:    */   }
/* 171:    */   
/* 172:208 */   private static Set<Class> basicTypeOrCollection = Sets.safeSet(new Class[] { Integer.TYPE, Float.TYPE, Short.TYPE, Character.TYPE, Byte.TYPE, Double.TYPE, Long.TYPE, Long.class, List.class, Set.class, Map.class, String.class, StringBuilder.class, Integer.class, Float.class, Double.class, Short.class, Byte.class, Character.class, BigInteger.class, BigDecimal.class, Boolean.TYPE, Boolean.class });
/* 173:214 */   private static Set<Class> primitiveNumber = Sets.safeSet(new Class[] { Integer.TYPE, Float.TYPE, Short.TYPE, Character.TYPE, Byte.TYPE, Double.TYPE, Long.TYPE });
/* 174:    */   
/* 175:    */   public static boolean isBasicTypeOrCollection(Class<?> type)
/* 176:    */   {
/* 177:218 */     return basicTypeOrCollection.contains(type);
/* 178:    */   }
/* 179:    */   
/* 180:    */   public static boolean isPrimitiveNumber(Class<?> arg1)
/* 181:    */   {
/* 182:222 */     return primitiveNumber.contains(arg1);
/* 183:    */   }
/* 184:    */   
/* 185:    */   public static boolean isArray(Object obj)
/* 186:    */   {
/* 187:226 */     if (obj == null) {
/* 188:226 */       return false;
/* 189:    */     }
/* 190:227 */     return obj.getClass().isArray();
/* 191:    */   }
/* 192:    */   
/* 193:    */   public static boolean isStringArray(Object obj)
/* 194:    */   {
/* 195:231 */     if (obj == null) {
/* 196:231 */       return false;
/* 197:    */     }
/* 198:232 */     return (obj.getClass().isArray()) && (obj.getClass().getComponentType() == String.class);
/* 199:    */   }
/* 200:    */   
/* 201:    */   public static boolean isEnum(Class<?> componentType)
/* 202:    */   {
/* 203:237 */     return componentType.isEnum();
/* 204:    */   }
/* 205:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.Typ
 * JD-Core Version:    0.7.0.1
 */