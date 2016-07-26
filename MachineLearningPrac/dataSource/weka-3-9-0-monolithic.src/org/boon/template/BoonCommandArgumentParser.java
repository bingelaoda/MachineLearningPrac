/*   1:    */ package org.boon.template;
/*   2:    */ 
/*   3:    */ import java.util.Map;
/*   4:    */ import org.boon.Str;
/*   5:    */ import org.boon.StringScanner;
/*   6:    */ import org.boon.collections.LazyMap;
/*   7:    */ import org.boon.core.reflection.FastStringUtils;
/*   8:    */ import org.boon.json.JsonParserAndMapper;
/*   9:    */ import org.boon.json.JsonParserFactory;
/*  10:    */ import org.boon.primitive.CharScanner;
/*  11:    */ 
/*  12:    */ public class BoonCommandArgumentParser
/*  13:    */ {
/*  14: 18 */   final JsonParserAndMapper jsonParser = new JsonParserFactory().lax().create();
/*  15:    */   
/*  16:    */   public Map<String, Object> parseArguments(String args)
/*  17:    */   {
/*  18: 24 */     char[] chars = FastStringUtils.toCharArray(args);
/*  19:    */     
/*  20: 26 */     int index = CharScanner.skipWhiteSpace(chars);
/*  21:    */     
/*  22: 28 */     char c = ' ';
/*  23: 30 */     if (index != chars.length) {
/*  24: 31 */       c = chars[index];
/*  25:    */     }
/*  26: 35 */     if (c == '{') {
/*  27: 36 */       return this.jsonParser.parseMap(chars);
/*  28:    */     }
/*  29: 37 */     if (c == '[')
/*  30:    */     {
/*  31: 38 */       LazyMap map = new LazyMap(1);
/*  32: 39 */       map.put("varargs", this.jsonParser.parse(chars));
/*  33: 40 */       return map;
/*  34:    */     }
/*  35: 43 */     boolean collectName = true;
/*  36:    */     
/*  37: 45 */     StringBuilder name = new StringBuilder();
/*  38: 46 */     StringBuilder value = new StringBuilder();
/*  39:    */     
/*  40: 48 */     Map<String, Object> params = new LazyMap();
/*  41:    */     
/*  42: 50 */     params.put("commandArgs", args);
/*  43:    */     
/*  44: 52 */     char closeQuoteToMatch = '"';
/*  45: 54 */     for (index = 0; index < chars.length; index++)
/*  46:    */     {
/*  47: 55 */       c = chars[index];
/*  48: 57 */       switch (c)
/*  49:    */       {
/*  50:    */       case '\t': 
/*  51:    */       case '\n': 
/*  52:    */       case '\r': 
/*  53:    */       case ' ': 
/*  54:    */         break;
/*  55:    */       case '=': 
/*  56: 66 */         collectName = false;
/*  57: 67 */         if ((chars[(index + 1)] != '\'') && (chars[(index + 1)] != '"'))
/*  58:    */         {
/*  59: 68 */           int start = index + 1;
/*  60: 69 */           int end = -1;
/*  61: 70 */           if ((chars[(index + 1)] == '$') && (chars[(index + 2)] == '{'))
/*  62:    */           {
/*  63: 72 */             end = CharScanner.findChar('}', start, chars);
/*  64: 73 */             if (end != -1) {
/*  65: 73 */               end++;
/*  66:    */             }
/*  67:    */           }
/*  68: 74 */           else if ((chars[(index + 1)] == '{') && (chars[(index + 2)] == '{'))
/*  69:    */           {
/*  70: 76 */             end = CharScanner.findChar('}', start, chars);
/*  71: 77 */             if (end != -1) {
/*  72: 77 */               end += 2;
/*  73:    */             }
/*  74:    */           }
/*  75:    */           else
/*  76:    */           {
/*  77: 80 */             end = CharScanner.findWhiteSpace(start, chars);
/*  78:    */           }
/*  79: 82 */           if (end == -1) {
/*  80: 83 */             end = chars.length;
/*  81:    */           }
/*  82: 85 */           String v = Str.slc(args, start, end);
/*  83: 86 */           params.put(name.toString(), v);
/*  84: 87 */           index += v.length();
/*  85: 88 */           name = new StringBuilder();
/*  86:    */           
/*  87: 90 */           collectName = true;
/*  88: 91 */           value = new StringBuilder();
/*  89:    */         }
/*  90:    */         else
/*  91:    */         {
/*  92: 95 */           closeQuoteToMatch = chars[(index + 1)];
/*  93: 96 */           index++;
/*  94:    */         }
/*  95: 98 */         break;
/*  96:    */       case '"': 
/*  97:    */       case '\'': 
/*  98:104 */         if (c == closeQuoteToMatch)
/*  99:    */         {
/* 100:105 */           collectName = true;
/* 101:106 */           index++;
/* 102:107 */           params.put(name.toString(), value.toString());
/* 103:108 */           name = new StringBuilder();
/* 104:109 */           value = new StringBuilder();
/* 105:    */         }
/* 106:110 */         break;
/* 107:    */       }
/* 108:115 */       if (collectName) {
/* 109:116 */         name.append(c);
/* 110:    */       } else {
/* 111:118 */         value.append(c);
/* 112:    */       }
/* 113:    */     }
/* 114:124 */     if (params.size() == 1) {
/* 115:128 */       params.put("varargs", StringScanner.splitByChars(args, new char[] { ' ', '\t', '\n' }));
/* 116:    */     }
/* 117:130 */     return params;
/* 118:    */   }
/* 119:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.template.BoonCommandArgumentParser
 * JD-Core Version:    0.7.0.1
 */