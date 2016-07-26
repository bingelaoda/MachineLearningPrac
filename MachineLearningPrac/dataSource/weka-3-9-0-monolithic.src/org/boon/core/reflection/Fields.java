/*   1:    */ package org.boon.core.reflection;
/*   2:    */ 
/*   3:    */ import java.lang.reflect.Field;
/*   4:    */ import java.lang.reflect.Modifier;
/*   5:    */ import java.util.Iterator;
/*   6:    */ import java.util.List;
/*   7:    */ import java.util.Map;
/*   8:    */ import java.util.Map.Entry;
/*   9:    */ import java.util.Set;
/*  10:    */ import org.boon.Exceptions;
/*  11:    */ import org.boon.Sets;
/*  12:    */ import org.boon.core.Typ;
/*  13:    */ import org.boon.core.reflection.fields.FieldAccess;
/*  14:    */ 
/*  15:    */ public class Fields
/*  16:    */ {
/*  17: 47 */   private static final Set<String> fieldSortNames = Sets.safeSet(new String[] { "name", "orderBy", "title", "key" });
/*  18: 48 */   private static final Set<String> fieldSortNamesSuffixes = Sets.safeSet(new String[] { "Name", "Title", "Key" });
/*  19:    */   
/*  20:    */   private static void setSortableField(Class<?> clazz, String fieldName)
/*  21:    */   {
/*  22: 51 */     Reflection.context()._sortableFields.put(clazz.getName(), fieldName);
/*  23:    */   }
/*  24:    */   
/*  25:    */   private static String getSortableField(Class<?> clazz)
/*  26:    */   {
/*  27: 55 */     return (String)Reflection.context()._sortableFields.get(clazz.getName());
/*  28:    */   }
/*  29:    */   
/*  30:    */   public static boolean hasStringField(Object value1, String name)
/*  31:    */   {
/*  32: 67 */     Class<?> clz = value1.getClass();
/*  33: 68 */     return classHasStringField(clz, name);
/*  34:    */   }
/*  35:    */   
/*  36:    */   public static boolean classHasStringField(Class<?> clz, String name)
/*  37:    */   {
/*  38: 80 */     List<Field> fields = Reflection.getAllFields(clz);
/*  39: 81 */     for (Field field : fields) {
/*  40: 82 */       if ((field.getType().equals(Typ.string)) && (field.getName().equals(name)) && (!Modifier.isStatic(field.getModifiers())) && (field.getDeclaringClass() == clz)) {
/*  41: 88 */         return true;
/*  42:    */       }
/*  43:    */     }
/*  44: 92 */     return false;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public static boolean hasField(Object value1, String name)
/*  48:    */   {
/*  49:103 */     return classHasField(value1.getClass(), name);
/*  50:    */   }
/*  51:    */   
/*  52:    */   public static boolean classHasField(Class<?> clz, String name)
/*  53:    */   {
/*  54:114 */     List<Field> fields = Reflection.getAllFields(clz);
/*  55:115 */     for (Field field : fields) {
/*  56:116 */       if ((field.getName().equals(name)) && (!Modifier.isStatic(field.getModifiers())) && (field.getDeclaringClass() == clz)) {
/*  57:119 */         return true;
/*  58:    */       }
/*  59:    */     }
/*  60:123 */     return false;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public static String getFirstComparableOrPrimitive(Object value1)
/*  64:    */   {
/*  65:133 */     return getFirstComparableOrPrimitiveFromClass(value1.getClass());
/*  66:    */   }
/*  67:    */   
/*  68:    */   public static String getFirstComparableOrPrimitiveFromClass(Class<?> clz)
/*  69:    */   {
/*  70:143 */     List<Field> fields = Reflection.getAllFields(clz);
/*  71:144 */     for (Field field : fields) {
/*  72:146 */       if ((field.getType().isPrimitive()) || ((Typ.isComparable(field.getType())) && (!Modifier.isStatic(field.getModifiers())) && (field.getDeclaringClass() == clz))) {
/*  73:150 */         return field.getName();
/*  74:    */       }
/*  75:    */     }
/*  76:154 */     return null;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public static String getFirstStringFieldNameEndsWith(Object value, String name)
/*  80:    */   {
/*  81:165 */     return getFirstStringFieldNameEndsWithFromClass(value.getClass(), name);
/*  82:    */   }
/*  83:    */   
/*  84:    */   public static String getFirstStringFieldNameEndsWithFromClass(Class<?> clz, String name)
/*  85:    */   {
/*  86:176 */     List<Field> fields = Reflection.getAllFields(clz);
/*  87:177 */     for (Field field : fields) {
/*  88:178 */       if ((field.getName().endsWith(name)) && (field.getType().equals(Typ.string)) && (!Modifier.isStatic(field.getModifiers())) && (field.getDeclaringClass() == clz)) {
/*  89:184 */         return field.getName();
/*  90:    */       }
/*  91:    */     }
/*  92:188 */     return null;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public static String getSortableField(Object value1)
/*  96:    */   {
/*  97:199 */     if ((value1 instanceof Map)) {
/*  98:200 */       return getSortableFieldFromMap((Map)value1);
/*  99:    */     }
/* 100:202 */     return getSortableFieldFromClass(value1.getClass());
/* 101:    */   }
/* 102:    */   
/* 103:    */   private static String getSortableFieldFromMap(Map<String, ?> map)
/* 104:    */   {
/* 105:211 */     for (String name : fieldSortNames) {
/* 106:212 */       if (map.containsKey(name)) {
/* 107:213 */         return name;
/* 108:    */       }
/* 109:    */     }
/* 110:220 */     for (Iterator i$ = fieldSortNamesSuffixes.iterator(); i$.hasNext();)
/* 111:    */     {
/* 112:220 */       suffix = (String)i$.next();
/* 113:221 */       for (String key : map.keySet()) {
/* 114:222 */         if (key.endsWith(suffix)) {
/* 115:223 */           return key;
/* 116:    */         }
/* 117:    */       }
/* 118:    */     }
/* 119:    */     String suffix;
/* 120:229 */     for (Object object : map.entrySet())
/* 121:    */     {
/* 122:230 */       Map.Entry<String, Object> entry = (Map.Entry)object;
/* 123:231 */       if (Typ.isBasicType(entry.getValue())) {
/* 124:232 */         return (String)entry.getKey();
/* 125:    */       }
/* 126:    */     }
/* 127:237 */     for (Object object : map.entrySet())
/* 128:    */     {
/* 129:238 */       Map.Entry<String, Object> entry = (Map.Entry)object;
/* 130:239 */       if ((entry.getValue() instanceof Comparable)) {
/* 131:240 */         return (String)entry.getKey();
/* 132:    */       }
/* 133:    */     }
/* 134:244 */     return (String)Exceptions.die(String.class, "No suitable sort key was found");
/* 135:    */   }
/* 136:    */   
/* 137:    */   public static String getSortableFieldFromClass(Class<?> clazz)
/* 138:    */   {
/* 139:258 */     String fieldName = getSortableField(clazz);
/* 140:263 */     if (fieldName == null)
/* 141:    */     {
/* 142:266 */       for (String name : fieldSortNames) {
/* 143:267 */         if (classHasStringField(clazz, name))
/* 144:    */         {
/* 145:268 */           fieldName = name;
/* 146:269 */           break;
/* 147:    */         }
/* 148:    */       }
/* 149:276 */       if (fieldName == null) {
/* 150:277 */         for (String name : fieldSortNamesSuffixes)
/* 151:    */         {
/* 152:278 */           fieldName = getFirstStringFieldNameEndsWithFromClass(clazz, name);
/* 153:279 */           if (fieldName != null) {
/* 154:    */             break;
/* 155:    */           }
/* 156:    */         }
/* 157:    */       }
/* 158:289 */       if (fieldName == null) {
/* 159:290 */         fieldName = getFirstComparableOrPrimitiveFromClass(clazz);
/* 160:    */       }
/* 161:294 */       if (fieldName == null)
/* 162:    */       {
/* 163:295 */         setSortableField(clazz, "NOT FOUND");
/* 164:296 */         Exceptions.die("Could not find a sortable field for type " + clazz);
/* 165:    */       }
/* 166:301 */       setSortableField(clazz, fieldName);
/* 167:    */     }
/* 168:303 */     return fieldName;
/* 169:    */   }
/* 170:    */   
/* 171:    */   public static boolean hasField(Class<?> aClass, String name)
/* 172:    */   {
/* 173:308 */     Map<String, FieldAccess> fields = Reflection.getAllAccessorFields(aClass);
/* 174:309 */     return fields.containsKey(name);
/* 175:    */   }
/* 176:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.Fields
 * JD-Core Version:    0.7.0.1
 */