/*   1:    */ package org.boon.expression;
/*   2:    */ 
/*   3:    */ import java.util.List;
/*   4:    */ import org.boon.Exceptions;
/*   5:    */ import org.boon.Str;
/*   6:    */ import org.boon.StringScanner;
/*   7:    */ import org.boon.core.Conversions;
/*   8:    */ import org.boon.sort.Sort;
/*   9:    */ import org.boon.sort.Sorting;
/*  10:    */ 
/*  11:    */ public class StandardFunctions
/*  12:    */ {
/*  13:    */   public static boolean contains(Object string, Object searchString)
/*  14:    */   {
/*  15: 24 */     if ((string == null) || (searchString == null)) {
/*  16: 25 */       return false;
/*  17:    */     }
/*  18: 28 */     return string.toString().contains(searchString.toString());
/*  19:    */   }
/*  20:    */   
/*  21:    */   public static boolean containsIgnoreCase(Object string, Object searchString)
/*  22:    */   {
/*  23: 33 */     if ((string == null) || (searchString == null)) {
/*  24: 34 */       return false;
/*  25:    */     }
/*  26: 37 */     return string.toString().toUpperCase().contains(searchString.toString().toUpperCase());
/*  27:    */   }
/*  28:    */   
/*  29:    */   public static boolean endsWith(Object string, Object searchString)
/*  30:    */   {
/*  31: 44 */     if ((string == null) || (searchString == null)) {
/*  32: 45 */       return false;
/*  33:    */     }
/*  34: 48 */     return string.toString().endsWith(searchString.toString());
/*  35:    */   }
/*  36:    */   
/*  37:    */   public static boolean startsWith(Object string, Object searchString)
/*  38:    */   {
/*  39: 54 */     if ((string == null) || (searchString == null)) {
/*  40: 55 */       return false;
/*  41:    */     }
/*  42: 58 */     return string.toString().startsWith(searchString.toString());
/*  43:    */   }
/*  44:    */   
/*  45:    */   public static String escapeXml(Object string)
/*  46:    */   {
/*  47: 63 */     return string.toString().replace("<", "&lt;");
/*  48:    */   }
/*  49:    */   
/*  50:    */   public static int indexOf(Object string, Object searchString)
/*  51:    */   {
/*  52: 70 */     if ((string == null) || (searchString == null)) {
/*  53: 71 */       return -1;
/*  54:    */     }
/*  55: 73 */     return string.toString().indexOf(searchString.toString());
/*  56:    */   }
/*  57:    */   
/*  58:    */   public static int length(Object item)
/*  59:    */   {
/*  60: 79 */     if (item == null) {
/*  61: 80 */       return 0;
/*  62:    */     }
/*  63: 82 */     return Conversions.len(item);
/*  64:    */   }
/*  65:    */   
/*  66:    */   public static int len(Object item)
/*  67:    */   {
/*  68: 89 */     if (item == null) {
/*  69: 90 */       return 0;
/*  70:    */     }
/*  71: 92 */     return Conversions.len(item);
/*  72:    */   }
/*  73:    */   
/*  74:    */   public static String replace(Object string, Object orgSubStr, Object newSubStr)
/*  75:    */   {
/*  76: 99 */     if (string == null) {
/*  77:100 */       return "";
/*  78:    */     }
/*  79:103 */     if ((orgSubStr == null) || (newSubStr == null)) {
/*  80:104 */       return string.toString();
/*  81:    */     }
/*  82:107 */     return string.toString().replace(orgSubStr.toString(), newSubStr.toString());
/*  83:    */   }
/*  84:    */   
/*  85:    */   public static String[] split(Object string, Object split)
/*  86:    */   {
/*  87:113 */     if (string == null) {
/*  88:114 */       return new String[0];
/*  89:    */     }
/*  90:117 */     if (Str.isEmpty(split)) {
/*  91:118 */       return new String[] { string.toString() };
/*  92:    */     }
/*  93:121 */     if (split.toString().length() == 1) {
/*  94:122 */       return StringScanner.split(string.toString(), split.toString().charAt(0));
/*  95:    */     }
/*  96:125 */     return string.toString().split(split.toString());
/*  97:    */   }
/*  98:    */   
/*  99:    */   public static String substring(Object string, int start, int stop)
/* 100:    */   {
/* 101:132 */     if (string == null) {
/* 102:133 */       return "";
/* 103:    */     }
/* 104:137 */     return Str.slc(string.toString(), start, stop);
/* 105:    */   }
/* 106:    */   
/* 107:    */   public static String slc(Object string, int start, int stop)
/* 108:    */   {
/* 109:144 */     if (string == null) {
/* 110:145 */       return "";
/* 111:    */     }
/* 112:149 */     return Str.slc(string.toString(), start, stop);
/* 113:    */   }
/* 114:    */   
/* 115:    */   public static String slc(Object string, int start)
/* 116:    */   {
/* 117:156 */     if (string == null) {
/* 118:157 */       return "";
/* 119:    */     }
/* 120:161 */     return Str.slc(string.toString(), start);
/* 121:    */   }
/* 122:    */   
/* 123:    */   public static String slcEnd(Object string, int end)
/* 124:    */   {
/* 125:168 */     if (string == null) {
/* 126:169 */       return "";
/* 127:    */     }
/* 128:173 */     return Str.slcEnd(string.toString(), end);
/* 129:    */   }
/* 130:    */   
/* 131:    */   public static String substringAfter(Object string, Object after)
/* 132:    */   {
/* 133:181 */     if (string == null) {
/* 134:182 */       return "";
/* 135:    */     }
/* 136:185 */     if (Str.isEmpty(after)) {
/* 137:186 */       return string.toString();
/* 138:    */     }
/* 139:189 */     return StringScanner.substringAfter(string.toString(), after.toString());
/* 140:    */   }
/* 141:    */   
/* 142:    */   public static String substringBefore(Object string, Object before)
/* 143:    */   {
/* 144:196 */     if (string == null) {
/* 145:197 */       return "";
/* 146:    */     }
/* 147:200 */     if (Str.isEmpty(before)) {
/* 148:201 */       return string.toString();
/* 149:    */     }
/* 150:204 */     return StringScanner.substringBefore(string.toString(), before.toString());
/* 151:    */   }
/* 152:    */   
/* 153:    */   public static String toLowerCase(Object string)
/* 154:    */   {
/* 155:211 */     if (string == null) {
/* 156:212 */       return "";
/* 157:    */     }
/* 158:216 */     return Str.lower(string.toString());
/* 159:    */   }
/* 160:    */   
/* 161:    */   public static String lower(Object string)
/* 162:    */   {
/* 163:223 */     if (string == null) {
/* 164:224 */       return "";
/* 165:    */     }
/* 166:228 */     return Str.lower(string.toString());
/* 167:    */   }
/* 168:    */   
/* 169:    */   public static String upper(Object string)
/* 170:    */   {
/* 171:234 */     if (string == null) {
/* 172:235 */       return "";
/* 173:    */     }
/* 174:239 */     return Str.upper(string.toString());
/* 175:    */   }
/* 176:    */   
/* 177:    */   public static String trim(Object string)
/* 178:    */   {
/* 179:245 */     if (string == null) {
/* 180:246 */       return "";
/* 181:    */     }
/* 182:250 */     return string.toString().trim();
/* 183:    */   }
/* 184:    */   
/* 185:    */   public static String join(Object objects, Object join)
/* 186:    */   {
/* 187:254 */     return Str.joinCollection(join.toString(), Conversions.toList(objects));
/* 188:    */   }
/* 189:    */   
/* 190:    */   public static Object sortBy(Object... args)
/* 191:    */   {
/* 192:259 */     Object collection = args[0];
/* 193:260 */     List list = Conversions.toList(collection);
/* 194:263 */     if (args.length == 2)
/* 195:    */     {
/* 196:264 */       String property = (String)args[1];
/* 197:265 */       Sort.sortBy(property).sort(list);
/* 198:266 */       return list;
/* 199:    */     }
/* 200:267 */     if (args.length > 2)
/* 201:    */     {
/* 202:269 */       String property = (String)args[1];
/* 203:    */       boolean split;
/* 204:    */       boolean split;
/* 205:    */       boolean asc;
/* 206:274 */       if (property.endsWith("_asc"))
/* 207:    */       {
/* 208:275 */         boolean asc = true;
/* 209:276 */         split = true;
/* 210:    */       }
/* 211:    */       else
/* 212:    */       {
/* 213:    */         boolean split;
/* 214:278 */         if (property.endsWith("_desc"))
/* 215:    */         {
/* 216:279 */           boolean asc = false;
/* 217:280 */           split = true;
/* 218:    */         }
/* 219:    */         else
/* 220:    */         {
/* 221:282 */           split = false;
/* 222:283 */           asc = true;
/* 223:    */         }
/* 224:    */       }
/* 225:286 */       if (split) {
/* 226:287 */         property = StringScanner.split(property, '_')[0];
/* 227:    */       }
/* 228:290 */       Sort sort = asc ? Sort.sortBy(property) : Sort.sortByDesc(property);
/* 229:291 */       for (int index = 2; index < args.length; index++)
/* 230:    */       {
/* 231:292 */         property = args[index].toString();
/* 232:294 */         if (property.endsWith("_asc"))
/* 233:    */         {
/* 234:295 */           asc = true;
/* 235:296 */           split = true;
/* 236:    */         }
/* 237:298 */         else if (property.endsWith("_desc"))
/* 238:    */         {
/* 239:299 */           asc = false;
/* 240:300 */           split = true;
/* 241:    */         }
/* 242:    */         else
/* 243:    */         {
/* 244:302 */           split = false;
/* 245:303 */           asc = true;
/* 246:    */         }
/* 247:306 */         if (split) {
/* 248:307 */           property = StringScanner.split(property, '_')[0];
/* 249:    */         }
/* 250:309 */         if (asc) {
/* 251:310 */           sort.thenAsc(property);
/* 252:    */         } else {
/* 253:312 */           sort.thenDesc(property);
/* 254:    */         }
/* 255:    */       }
/* 256:315 */       sort.sort(list);
/* 257:316 */       return list;
/* 258:    */     }
/* 259:318 */     Exceptions.die(new Object[] { "Wrong number of arguments to sort", args });
/* 260:319 */     return "";
/* 261:    */   }
/* 262:    */   
/* 263:    */   public static Object sort(Object collection)
/* 264:    */   {
/* 265:327 */     List list = Conversions.toList(collection);
/* 266:    */     
/* 267:329 */     Sorting.sort(list);
/* 268:330 */     return list;
/* 269:    */   }
/* 270:    */   
/* 271:    */   public static Object sortByDesc(Object... args)
/* 272:    */   {
/* 273:334 */     Object collection = args[0];
/* 274:335 */     List list = Conversions.toList(collection);
/* 275:338 */     if (args.length == 2)
/* 276:    */     {
/* 277:339 */       String property = (String)args[1];
/* 278:340 */       Sort.sortBy(property).sort(list);
/* 279:341 */       return list;
/* 280:    */     }
/* 281:342 */     if (args.length > 2)
/* 282:    */     {
/* 283:344 */       String property = (String)args[1];
/* 284:    */       
/* 285:346 */       Sort sort = Sort.sortByDesc(property);
/* 286:347 */       for (int index = 2; index < args.length; index++) {
/* 287:348 */         Sort.sortByDesc(args[index].toString());
/* 288:    */       }
/* 289:350 */       sort.sort(list);
/* 290:351 */       return list;
/* 291:    */     }
/* 292:353 */     Exceptions.die(new Object[] { "Wrong number of arguments to sort", args });
/* 293:354 */     return "";
/* 294:    */   }
/* 295:    */   
/* 296:    */   public static Object sortDesc(Object collection)
/* 297:    */   {
/* 298:361 */     List list = Conversions.toList(collection);
/* 299:    */     
/* 300:363 */     Sorting.sortDesc(list);
/* 301:364 */     return list;
/* 302:    */   }
/* 303:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.expression.StandardFunctions
 * JD-Core Version:    0.7.0.1
 */