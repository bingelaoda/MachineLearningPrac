/*  1:   */ package org.boon.json;
/*  2:   */ 
/*  3:   */ import java.util.LinkedHashMap;
/*  4:   */ import java.util.List;
/*  5:   */ import java.util.Map;
/*  6:   */ import org.boon.IO;
/*  7:   */ import org.boon.di.Creator;
/*  8:   */ 
/*  9:   */ public class JsonCreator
/* 10:   */ {
/* 11:   */   public static <T> T createFromJsonMap(Class<T> type, String str)
/* 12:   */   {
/* 13:46 */     Map<String, Object> config = new JsonParserFactory().createLaxParser().parseMap(str);
/* 14:47 */     return Creator.create(type, config);
/* 15:   */   }
/* 16:   */   
/* 17:   */   public static <T> T createFromJsonMapResource(Class<T> type, String resource)
/* 18:   */   {
/* 19:51 */     Map<String, Object> config = null;
/* 20:52 */     JsonParserAndMapper laxParser = new JsonParserFactory().createLaxParser();
/* 21:54 */     if (resource.endsWith(".json"))
/* 22:   */     {
/* 23:55 */       config = laxParser.parseMap(IO.read(resource));
/* 24:   */     }
/* 25:56 */     else if (resource.endsWith("/"))
/* 26:   */     {
/* 27:57 */       config = new LinkedHashMap();
/* 28:58 */       handleDir(config, laxParser, resource);
/* 29:   */     }
/* 30:60 */     return Creator.create(type, config);
/* 31:   */   }
/* 32:   */   
/* 33:   */   private static void handleDir(Map<String, Object> config, JsonParserAndMapper laxParser, String resource)
/* 34:   */   {
/* 35:66 */     List<String> jsonFiles = IO.listByExt(resource, ".json");
/* 36:67 */     for (String jsonFile : jsonFiles)
/* 37:   */     {
/* 38:68 */       String contents = IO.read(jsonFile);
/* 39:69 */       Map<String, Object> fileConfig = laxParser.parseMap(contents);
/* 40:70 */       config.putAll(fileConfig);
/* 41:   */     }
/* 42:   */   }
/* 43:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.JsonCreator
 * JD-Core Version:    0.7.0.1
 */