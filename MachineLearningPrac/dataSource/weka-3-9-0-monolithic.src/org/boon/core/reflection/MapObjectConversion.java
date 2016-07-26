/*   1:    */ package org.boon.core.reflection;
/*   2:    */ 
/*   3:    */ import java.util.Collection;
/*   4:    */ import java.util.List;
/*   5:    */ import java.util.Map;
/*   6:    */ import java.util.Set;
/*   7:    */ import org.boon.Sets;
/*   8:    */ import org.boon.core.Value;
/*   9:    */ import org.boon.core.reflection.fields.FieldAccessMode;
/*  10:    */ import org.boon.core.reflection.fields.FieldsAccessor;
/*  11:    */ 
/*  12:    */ public class MapObjectConversion
/*  13:    */ {
/*  14: 56 */   private static final Mapper mapper = new MapperSimple();
/*  15: 58 */   private static final Mapper mapperWithType = new MapperComplex();
/*  16: 62 */   private static final Mapper prettyMapper = new MapperComplex(false, FieldAccessMode.PROPERTY_THEN_FIELD, true, false, null, null, true, true);
/*  17:    */   
/*  18:    */   public static <T> T fromList(List<?> argList, Class<T> clazz)
/*  19:    */   {
/*  20: 85 */     return mapper.fromList(argList, clazz);
/*  21:    */   }
/*  22:    */   
/*  23:    */   public static <T> T fromList(boolean respectIgnore, String view, FieldsAccessor fieldsAccessor, List<?> argList, Class<T> clazz, Set<String> ignoreSet)
/*  24:    */   {
/*  25:106 */     return new MapperComplex(fieldsAccessor, ignoreSet, view, respectIgnore).fromList(argList, clazz);
/*  26:    */   }
/*  27:    */   
/*  28:    */   public static <T> T fromList(FieldsAccessor fieldsAccessor, List<?> argList, Class<T> clazz)
/*  29:    */   {
/*  30:123 */     return new MapperComplex(fieldsAccessor, null, null, false).fromList(argList, clazz);
/*  31:    */   }
/*  32:    */   
/*  33:    */   public static List<?> toList(Object object)
/*  34:    */   {
/*  35:135 */     return mapper.toList(object);
/*  36:    */   }
/*  37:    */   
/*  38:    */   public static <T> T fromMap(Map<String, Object> map, Class<T> clazz)
/*  39:    */   {
/*  40:148 */     return mapper.fromMap(map, clazz);
/*  41:    */   }
/*  42:    */   
/*  43:    */   public static <T> T fromMap(Map<String, Object> map, Class<T> clazz, String... excludeProperties)
/*  44:    */   {
/*  45:164 */     Set<String> ignoreProps = excludeProperties.length > 0 ? Sets.set(excludeProperties) : null;
/*  46:165 */     return new MapperComplex(FieldAccessMode.FIELD_THEN_PROPERTY.create(false), ignoreProps, null, true).fromMap(map, clazz);
/*  47:    */   }
/*  48:    */   
/*  49:    */   public static Object fromMap(Map<String, Object> map)
/*  50:    */   {
/*  51:178 */     return mapper.fromMap(map);
/*  52:    */   }
/*  53:    */   
/*  54:    */   public static <T> T fromMap(boolean respectIgnore, String view, FieldsAccessor fieldsAccessor, Map<String, Object> map, Class<T> cls, Set<String> ignoreSet)
/*  55:    */   {
/*  56:196 */     Mapper mapper = new MapperComplex(ignoreSet, view, respectIgnore);
/*  57:    */     
/*  58:198 */     return mapper.fromMap(map, cls);
/*  59:    */   }
/*  60:    */   
/*  61:    */   public static <T> T fromValueMap(boolean respectIgnore, String view, FieldsAccessor fieldsAccessor, Map<String, Value> valueMap, Class<T> cls, Set<String> ignoreSet)
/*  62:    */   {
/*  63:221 */     Mapper mapper = new MapperComplex(fieldsAccessor, ignoreSet, view, respectIgnore);
/*  64:222 */     return mapper.fromValueMap(valueMap, cls);
/*  65:    */   }
/*  66:    */   
/*  67:    */   public static Map<String, Object> toMap(Object object, String... ignore)
/*  68:    */   {
/*  69:234 */     return toMap(object, Sets.set(ignore));
/*  70:    */   }
/*  71:    */   
/*  72:    */   public static Map<String, Object> toMap(Object object, Set<String> ignore)
/*  73:    */   {
/*  74:248 */     return new MapperComplex(ignore).toMap(object);
/*  75:    */   }
/*  76:    */   
/*  77:    */   public static Map<String, Object> toMap(Object object)
/*  78:    */   {
/*  79:264 */     return mapper.toMap(object);
/*  80:    */   }
/*  81:    */   
/*  82:    */   public static Map<String, Object> toMapWithType(Object object)
/*  83:    */   {
/*  84:277 */     return mapperWithType.toMap(object);
/*  85:    */   }
/*  86:    */   
/*  87:    */   public static <T> List<T> convertListOfMapsToObjects(boolean respectIgnore, String view, FieldsAccessor fieldsAccessor, Class<T> componentType, List<Map> list, Set<String> ignoreProperties)
/*  88:    */   {
/*  89:296 */     return new MapperComplex(fieldsAccessor, ignoreProperties, view, respectIgnore).convertListOfMapsToObjects(list, componentType);
/*  90:    */   }
/*  91:    */   
/*  92:    */   public static <T> List<T> convertListOfMapsToObjects(Class<T> componentType, List<Map> list)
/*  93:    */   {
/*  94:307 */     return mapper.convertListOfMapsToObjects(list, componentType);
/*  95:    */   }
/*  96:    */   
/*  97:    */   public static List<Map<String, Object>> toListOfMaps(Collection<?> collection)
/*  98:    */   {
/*  99:317 */     return mapper.toListOfMaps(collection);
/* 100:    */   }
/* 101:    */   
/* 102:    */   public static Map toPrettyMap(Object object)
/* 103:    */   {
/* 104:321 */     return prettyMapper.toMap(object);
/* 105:    */   }
/* 106:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.MapObjectConversion
 * JD-Core Version:    0.7.0.1
 */