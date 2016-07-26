/*   1:    */ package org.boon.json;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.Reader;
/*   6:    */ import java.io.StringReader;
/*   7:    */ import java.net.URL;
/*   8:    */ import java.nio.charset.Charset;
/*   9:    */ import java.nio.charset.StandardCharsets;
/*  10:    */ import java.nio.file.Files;
/*  11:    */ import java.util.Map;
/*  12:    */ import org.boon.HTTP;
/*  13:    */ import org.boon.IO;
/*  14:    */ import org.boon.json.implementation.JsonParserUsingCharacterSource;
/*  15:    */ 
/*  16:    */ public class JsonSlurper
/*  17:    */ {
/*  18:    */   public Object parseText(String text)
/*  19:    */   {
/*  20: 72 */     if ((text == null) || (text.length() == 0)) {
/*  21: 73 */       throw new IllegalArgumentException("The JSON input text should neither be null nor empty.");
/*  22:    */     }
/*  23: 76 */     return JsonFactory.create().fromJson(text);
/*  24:    */   }
/*  25:    */   
/*  26:    */   public Object parse(Reader reader)
/*  27:    */   {
/*  28: 86 */     return new JsonParserUsingCharacterSource().parse(reader);
/*  29:    */   }
/*  30:    */   
/*  31:    */   public Object parse(File file)
/*  32:    */   {
/*  33: 98 */     return parseFile(file, null);
/*  34:    */   }
/*  35:    */   
/*  36:    */   public Object parse(File file, String charset)
/*  37:    */   {
/*  38:110 */     return parseFile(file, charset);
/*  39:    */   }
/*  40:    */   
/*  41:    */   private Object parseFile(File file, String scharset)
/*  42:    */   {
/*  43:115 */     charset = (scharset == null) || (scharset.length() == 0) ? StandardCharsets.UTF_8 : Charset.forName(scharset);
/*  44:117 */     if (file.length() > 2000000L) {
/*  45:    */       try
/*  46:    */       {
/*  47:118 */         Reader reader = Files.newBufferedReader(IO.path(file.toString()), charset);Throwable localThrowable2 = null;
/*  48:    */         try
/*  49:    */         {
/*  50:119 */           return parse(reader);
/*  51:    */         }
/*  52:    */         catch (Throwable localThrowable3)
/*  53:    */         {
/*  54:118 */           localThrowable2 = localThrowable3;throw localThrowable3;
/*  55:    */         }
/*  56:    */         finally
/*  57:    */         {
/*  58:120 */           if (reader != null) {
/*  59:120 */             if (localThrowable2 != null) {
/*  60:    */               try
/*  61:    */               {
/*  62:120 */                 reader.close();
/*  63:    */               }
/*  64:    */               catch (Throwable x2)
/*  65:    */               {
/*  66:120 */                 localThrowable2.addSuppressed(x2);
/*  67:    */               }
/*  68:    */             } else {
/*  69:120 */               reader.close();
/*  70:    */             }
/*  71:    */           }
/*  72:    */         }
/*  73:    */         try
/*  74:    */         {
/*  75:125 */           return JsonFactory.create().fromJson(Files.newBufferedReader(IO.path(file.toString()), charset));
/*  76:    */         }
/*  77:    */         catch (IOException e)
/*  78:    */         {
/*  79:127 */           throw new JsonException("Unable to process file: " + file.getPath(), e);
/*  80:    */         }
/*  81:    */       }
/*  82:    */       catch (IOException ioe)
/*  83:    */       {
/*  84:121 */         throw new JsonException("Unable to process file: " + file.getPath(), ioe);
/*  85:    */       }
/*  86:    */     }
/*  87:    */   }
/*  88:    */   
/*  89:    */   public Object parse(URL url)
/*  90:    */   {
/*  91:140 */     return parseURL(url, null);
/*  92:    */   }
/*  93:    */   
/*  94:    */   public Object parse(URL url, Map params)
/*  95:    */   {
/*  96:152 */     return parseURL(url, params);
/*  97:    */   }
/*  98:    */   
/*  99:    */   public Object parse(Map params, URL url)
/* 100:    */   {
/* 101:164 */     return parseURL(url, params);
/* 102:    */   }
/* 103:    */   
/* 104:    */   private Object parseURL(URL url, Map params)
/* 105:    */   {
/* 106:168 */     return parse(new StringReader(IO.read(url.toString())));
/* 107:    */   }
/* 108:    */   
/* 109:    */   public Object parse(URL url, String charset)
/* 110:    */   {
/* 111:180 */     return parseURL(url, null, charset);
/* 112:    */   }
/* 113:    */   
/* 114:    */   public Object parse(URL url, Map params, String charset)
/* 115:    */   {
/* 116:193 */     return parseURL(url, params, charset);
/* 117:    */   }
/* 118:    */   
/* 119:    */   public Object parse(Map params, URL url, String charset)
/* 120:    */   {
/* 121:206 */     return parseURL(url, params, charset);
/* 122:    */   }
/* 123:    */   
/* 124:    */   private Object parseURL(URL url, Map params, String charset)
/* 125:    */   {
/* 126:210 */     return parse(new StringReader(HTTP.getJSONWithParams(url.toString(), null, params)));
/* 127:    */   }
/* 128:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.JsonSlurper
 * JD-Core Version:    0.7.0.1
 */