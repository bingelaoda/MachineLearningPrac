/*   1:    */ package org.boon.json;
/*   2:    */ 
/*   3:    */ import java.io.Reader;
/*   4:    */ import java.util.List;
/*   5:    */ import org.boon.json.implementation.ObjectMapperImpl;
/*   6:    */ 
/*   7:    */ public class JsonFactory
/*   8:    */ {
/*   9: 42 */   private static ObjectMapper json = ;
/*  10:    */   
/*  11:    */   public static ObjectMapper create()
/*  12:    */   {
/*  13: 45 */     JsonParserFactory jsonParserFactory = new JsonParserFactory();
/*  14: 46 */     jsonParserFactory.lax();
/*  15:    */     
/*  16: 48 */     return new ObjectMapperImpl(jsonParserFactory, new JsonSerializerFactory());
/*  17:    */   }
/*  18:    */   
/*  19:    */   public static String toJson(Object value)
/*  20:    */   {
/*  21: 52 */     return json.toJson(value);
/*  22:    */   }
/*  23:    */   
/*  24:    */   public static void toJson(Object value, Appendable appendable)
/*  25:    */   {
/*  26: 56 */     json.toJson(value, appendable);
/*  27:    */   }
/*  28:    */   
/*  29:    */   public static <T> T fromJson(String str, Class<T> clazz)
/*  30:    */   {
/*  31: 60 */     return json.fromJson(str, clazz);
/*  32:    */   }
/*  33:    */   
/*  34:    */   public static <T> List<T> fromJsonArray(String str, Class<T> clazz)
/*  35:    */   {
/*  36: 65 */     return json.parser().parseList(clazz, str);
/*  37:    */   }
/*  38:    */   
/*  39:    */   public static Object fromJson(String str)
/*  40:    */   {
/*  41: 69 */     return json.fromJson(str);
/*  42:    */   }
/*  43:    */   
/*  44:    */   public static Object fromJson(Reader reader)
/*  45:    */   {
/*  46: 73 */     return json.fromJson(reader);
/*  47:    */   }
/*  48:    */   
/*  49:    */   public static ObjectMapper create(JsonParserFactory parserFactory, JsonSerializerFactory serializerFactory)
/*  50:    */   {
/*  51: 77 */     return new ObjectMapperImpl(parserFactory, serializerFactory);
/*  52:    */   }
/*  53:    */   
/*  54:    */   public static ObjectMapper createUseProperties(boolean useJsonDates)
/*  55:    */   {
/*  56: 81 */     JsonParserFactory jpf = new JsonParserFactory();
/*  57: 82 */     jpf.usePropertiesFirst();
/*  58: 83 */     JsonSerializerFactory jsf = new JsonSerializerFactory();
/*  59:    */     
/*  60: 85 */     jsf.usePropertiesFirst();
/*  61: 87 */     if (useJsonDates) {
/*  62: 88 */       jsf.useJsonFormatForDates();
/*  63:    */     }
/*  64: 90 */     return new ObjectMapperImpl(jpf, jsf);
/*  65:    */   }
/*  66:    */   
/*  67:    */   public static ObjectMapper createUseAnnotations(boolean useJsonDates)
/*  68:    */   {
/*  69: 94 */     JsonParserFactory jpf = new JsonParserFactory();
/*  70: 95 */     JsonSerializerFactory jsf = new JsonSerializerFactory();
/*  71:    */     
/*  72: 97 */     jsf.useAnnotations();
/*  73: 99 */     if (useJsonDates) {
/*  74:100 */       jsf.useJsonFormatForDates();
/*  75:    */     }
/*  76:102 */     return new ObjectMapperImpl(jpf, jsf);
/*  77:    */   }
/*  78:    */   
/*  79:    */   public static ObjectMapper createUseJSONDates()
/*  80:    */   {
/*  81:107 */     JsonParserFactory jpf = new JsonParserFactory();
/*  82:108 */     JsonSerializerFactory jsf = new JsonSerializerFactory();
/*  83:109 */     jsf.useJsonFormatForDates();
/*  84:110 */     return new ObjectMapperImpl(jpf, jsf);
/*  85:    */   }
/*  86:    */   
/*  87:    */   public static String niceJson(String str)
/*  88:    */   {
/*  89:114 */     return str.replace('\'', '"');
/*  90:    */   }
/*  91:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.JsonFactory
 * JD-Core Version:    0.7.0.1
 */