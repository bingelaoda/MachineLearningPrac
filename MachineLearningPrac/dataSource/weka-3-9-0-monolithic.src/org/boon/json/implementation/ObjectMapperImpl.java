/*   1:    */ package org.boon.json.implementation;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.InputStream;
/*   6:    */ import java.io.OutputStream;
/*   7:    */ import java.io.Reader;
/*   8:    */ import java.io.Writer;
/*   9:    */ import java.nio.charset.Charset;
/*  10:    */ import java.nio.charset.StandardCharsets;
/*  11:    */ import java.util.Collection;
/*  12:    */ import java.util.HashSet;
/*  13:    */ import java.util.LinkedHashSet;
/*  14:    */ import java.util.List;
/*  15:    */ import java.util.Set;
/*  16:    */ import org.boon.Exceptions;
/*  17:    */ import org.boon.IO;
/*  18:    */ import org.boon.json.JsonParserAndMapper;
/*  19:    */ import org.boon.json.JsonParserFactory;
/*  20:    */ import org.boon.json.JsonSerializer;
/*  21:    */ import org.boon.json.JsonSerializerFactory;
/*  22:    */ import org.boon.json.ObjectMapper;
/*  23:    */ import org.boon.primitive.CharBuf;
/*  24:    */ 
/*  25:    */ public class ObjectMapperImpl
/*  26:    */   implements ObjectMapper
/*  27:    */ {
/*  28:    */   private final JsonParserFactory parserFactory;
/*  29:    */   private final JsonSerializerFactory serializerFactory;
/*  30:    */   
/*  31:    */   public ObjectMapperImpl(JsonParserFactory parserFactory, JsonSerializerFactory serializerFactory)
/*  32:    */   {
/*  33: 49 */     this.parserFactory = parserFactory;
/*  34: 50 */     this.serializerFactory = serializerFactory;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public ObjectMapperImpl()
/*  38:    */   {
/*  39: 56 */     this.parserFactory = new JsonParserFactory();
/*  40: 57 */     this.serializerFactory = new JsonSerializerFactory();
/*  41:    */     
/*  42: 59 */     this.serializerFactory.useFieldsOnly();
/*  43:    */   }
/*  44:    */   
/*  45:    */   public <T> T readValue(String src, Class<T> valueType)
/*  46:    */   {
/*  47: 67 */     return this.parserFactory.create().parse(valueType, src);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public <T> T readValue(File src, Class<T> valueType)
/*  51:    */   {
/*  52: 73 */     return this.parserFactory.create().parseFile(valueType, src.toString());
/*  53:    */   }
/*  54:    */   
/*  55:    */   public <T> T readValue(byte[] src, Class<T> valueType)
/*  56:    */   {
/*  57: 78 */     return this.parserFactory.create().parse(valueType, src);
/*  58:    */   }
/*  59:    */   
/*  60:    */   public <T> T readValue(char[] src, Class<T> valueType)
/*  61:    */   {
/*  62: 83 */     return this.parserFactory.create().parse(valueType, src);
/*  63:    */   }
/*  64:    */   
/*  65:    */   public <T> T readValue(Reader src, Class<T> valueType)
/*  66:    */   {
/*  67: 88 */     return this.parserFactory.create().parse(valueType, src);
/*  68:    */   }
/*  69:    */   
/*  70:    */   public <T> T readValue(InputStream src, Class<T> valueType)
/*  71:    */   {
/*  72: 93 */     return this.parserFactory.create().parse(valueType, src);
/*  73:    */   }
/*  74:    */   
/*  75:    */   public <T extends Collection<C>, C> T readValue(String src, Class<T> valueType, Class<C> componentType)
/*  76:    */   {
/*  77: 99 */     Class<?> type = valueType;
/*  78:100 */     if (type == List.class) {
/*  79:101 */       return this.parserFactory.create().parseList(componentType, src);
/*  80:    */     }
/*  81:102 */     if (type == Set.class) {
/*  82:103 */       return new HashSet(this.parserFactory.create().parseList(componentType, src));
/*  83:    */     }
/*  84:104 */     if (type == LinkedHashSet.class) {
/*  85:105 */       return new LinkedHashSet(this.parserFactory.create().parseList(componentType, src));
/*  86:    */     }
/*  87:107 */     return this.parserFactory.create().parseList(componentType, src);
/*  88:    */   }
/*  89:    */   
/*  90:    */   public <T extends Collection<C>, C> T readValue(File src, Class<T> valueType, Class<C> componentType)
/*  91:    */   {
/*  92:113 */     Class<?> type = valueType;
/*  93:114 */     if (type == List.class) {
/*  94:115 */       return this.parserFactory.create().parseListFromFile(componentType, src.toString());
/*  95:    */     }
/*  96:116 */     if (type == Set.class) {
/*  97:117 */       return new HashSet(this.parserFactory.create().parseListFromFile(componentType, src.toString()));
/*  98:    */     }
/*  99:118 */     if (type == LinkedHashSet.class) {
/* 100:119 */       return new LinkedHashSet(this.parserFactory.create().parseListFromFile(componentType, src.toString()));
/* 101:    */     }
/* 102:121 */     return this.parserFactory.create().parseListFromFile(componentType, src.toString());
/* 103:    */   }
/* 104:    */   
/* 105:    */   public <T extends Collection<C>, C> T readValue(byte[] src, Class<T> valueType, Class<C> componentType)
/* 106:    */   {
/* 107:127 */     Class<?> type = valueType;
/* 108:128 */     if (type == List.class) {
/* 109:129 */       return this.parserFactory.create().parseList(componentType, src);
/* 110:    */     }
/* 111:130 */     if (type == Set.class) {
/* 112:131 */       return new HashSet(this.parserFactory.create().parseList(componentType, src));
/* 113:    */     }
/* 114:132 */     if (type == LinkedHashSet.class) {
/* 115:133 */       return new LinkedHashSet(this.parserFactory.create().parseList(componentType, src));
/* 116:    */     }
/* 117:135 */     return this.parserFactory.create().parseList(componentType, src);
/* 118:    */   }
/* 119:    */   
/* 120:    */   public <T extends Collection<C>, C> T readValue(char[] src, Class<T> valueType, Class<C> componentType)
/* 121:    */   {
/* 122:141 */     Class<?> type = valueType;
/* 123:142 */     if (type == List.class) {
/* 124:143 */       return this.parserFactory.create().parseList(componentType, src);
/* 125:    */     }
/* 126:144 */     if (type == Set.class) {
/* 127:145 */       return new HashSet(this.parserFactory.create().parseList(componentType, src));
/* 128:    */     }
/* 129:146 */     if (type == LinkedHashSet.class) {
/* 130:147 */       return new LinkedHashSet(this.parserFactory.create().parseList(componentType, src));
/* 131:    */     }
/* 132:149 */     return this.parserFactory.create().parseList(componentType, src);
/* 133:    */   }
/* 134:    */   
/* 135:    */   public <T extends Collection<C>, C> T readValue(Reader src, Class<T> valueType, Class<C> componentType)
/* 136:    */   {
/* 137:155 */     Class<?> type = valueType;
/* 138:156 */     if (type == List.class) {
/* 139:157 */       return this.parserFactory.create().parseList(componentType, src);
/* 140:    */     }
/* 141:158 */     if (type == Set.class) {
/* 142:159 */       return new HashSet(this.parserFactory.create().parseList(componentType, src));
/* 143:    */     }
/* 144:160 */     if (type == LinkedHashSet.class) {
/* 145:161 */       return new LinkedHashSet(this.parserFactory.create().parseList(componentType, src));
/* 146:    */     }
/* 147:163 */     return this.parserFactory.create().parseList(componentType, src);
/* 148:    */   }
/* 149:    */   
/* 150:    */   public <T extends Collection<C>, C> T readValue(InputStream src, Class<T> valueType, Class<C> componentType)
/* 151:    */   {
/* 152:169 */     Class<?> type = valueType;
/* 153:170 */     if (type == List.class) {
/* 154:171 */       return this.parserFactory.create().parseList(componentType, src);
/* 155:    */     }
/* 156:172 */     if (type == Set.class) {
/* 157:173 */       return new HashSet(this.parserFactory.create().parseList(componentType, src));
/* 158:    */     }
/* 159:174 */     if (type == LinkedHashSet.class) {
/* 160:175 */       return new LinkedHashSet(this.parserFactory.create().parseList(componentType, src));
/* 161:    */     }
/* 162:177 */     return this.parserFactory.create().parseList(componentType, src);
/* 163:    */   }
/* 164:    */   
/* 165:    */   public <T extends Collection<C>, C> T readValue(byte[] src, Charset charset, Class<T> valueType, Class<C> componentType)
/* 166:    */   {
/* 167:183 */     Class<?> type = valueType;
/* 168:184 */     if (type == List.class) {
/* 169:185 */       return this.parserFactory.create().parseList(componentType, src, charset);
/* 170:    */     }
/* 171:186 */     if (type == Set.class) {
/* 172:187 */       return new HashSet(this.parserFactory.create().parseList(componentType, src, charset));
/* 173:    */     }
/* 174:188 */     if (type == LinkedHashSet.class) {
/* 175:189 */       return new LinkedHashSet(this.parserFactory.create().parseList(componentType, src, charset));
/* 176:    */     }
/* 177:191 */     return this.parserFactory.create().parseList(componentType, src, charset);
/* 178:    */   }
/* 179:    */   
/* 180:    */   public <T extends Collection<C>, C> T readValue(InputStream src, Charset charset, Class<T> valueType, Class<C> componentType)
/* 181:    */   {
/* 182:197 */     Class<?> type = valueType;
/* 183:198 */     if (type == List.class) {
/* 184:199 */       return this.parserFactory.create().parseList(componentType, src, charset);
/* 185:    */     }
/* 186:200 */     if (type == Set.class) {
/* 187:201 */       return new HashSet(this.parserFactory.create().parseList(componentType, src, charset));
/* 188:    */     }
/* 189:202 */     if (type == LinkedHashSet.class) {
/* 190:203 */       return new LinkedHashSet(this.parserFactory.create().parseList(componentType, src, charset));
/* 191:    */     }
/* 192:205 */     return this.parserFactory.create().parseList(componentType, src, charset);
/* 193:    */   }
/* 194:    */   
/* 195:    */   public void writeValue(File dest, Object value)
/* 196:    */   {
/* 197:211 */     IO.write(IO.path(dest.toString()), this.serializerFactory.create().serialize(value).toString());
/* 198:    */   }
/* 199:    */   
/* 200:    */   public void writeValue(OutputStream dest, Object value)
/* 201:    */   {
/* 202:217 */     IO.writeNoClose(dest, this.serializerFactory.create().serialize(value).toString());
/* 203:    */   }
/* 204:    */   
/* 205:    */   public void writeValue(Writer dest, Object value)
/* 206:    */   {
/* 207:223 */     char[] chars = this.serializerFactory.create().serialize(value).toCharArray();
/* 208:    */     try
/* 209:    */     {
/* 210:226 */       dest.write(chars);
/* 211:    */     }
/* 212:    */     catch (IOException e)
/* 213:    */     {
/* 214:228 */       Exceptions.handle(e);
/* 215:    */     }
/* 216:    */   }
/* 217:    */   
/* 218:    */   public String writeValueAsString(Object value)
/* 219:    */   {
/* 220:234 */     return this.serializerFactory.create().serialize(value).toString();
/* 221:    */   }
/* 222:    */   
/* 223:    */   public char[] writeValueAsCharArray(Object value)
/* 224:    */   {
/* 225:239 */     return this.serializerFactory.create().serialize(value).toCharArray();
/* 226:    */   }
/* 227:    */   
/* 228:    */   public byte[] writeValueAsBytes(Object value)
/* 229:    */   {
/* 230:244 */     return this.serializerFactory.create().serialize(value).toString().getBytes(StandardCharsets.UTF_8);
/* 231:    */   }
/* 232:    */   
/* 233:    */   public byte[] writeValueAsBytes(Object value, Charset charset)
/* 234:    */   {
/* 235:249 */     return this.serializerFactory.create().serialize(value).toString().getBytes(charset);
/* 236:    */   }
/* 237:    */   
/* 238:    */   public JsonParserAndMapper parser()
/* 239:    */   {
/* 240:254 */     return this.parserFactory.create();
/* 241:    */   }
/* 242:    */   
/* 243:    */   public JsonSerializer serializer()
/* 244:    */   {
/* 245:259 */     return this.serializerFactory.create();
/* 246:    */   }
/* 247:    */   
/* 248:    */   public String toJson(Object value)
/* 249:    */   {
/* 250:264 */     return writeValueAsString(value);
/* 251:    */   }
/* 252:    */   
/* 253:    */   public void toJson(Object value, Appendable appendable)
/* 254:    */   {
/* 255:    */     try
/* 256:    */     {
/* 257:270 */       appendable.append(writeValueAsString(value));
/* 258:    */     }
/* 259:    */     catch (IOException e)
/* 260:    */     {
/* 261:272 */       Exceptions.handle(e);
/* 262:    */     }
/* 263:    */   }
/* 264:    */   
/* 265:    */   public <T> T fromJson(String json, Class<T> clazz)
/* 266:    */   {
/* 267:278 */     return readValue(json, clazz);
/* 268:    */   }
/* 269:    */   
/* 270:    */   public <T> T fromJson(byte[] bytes, Class<T> clazz)
/* 271:    */   {
/* 272:283 */     return readValue(bytes, clazz);
/* 273:    */   }
/* 274:    */   
/* 275:    */   public <T> T fromJson(char[] chars, Class<T> clazz)
/* 276:    */   {
/* 277:288 */     return readValue(chars, clazz);
/* 278:    */   }
/* 279:    */   
/* 280:    */   public <T> T fromJson(Reader reader, Class<T> clazz)
/* 281:    */   {
/* 282:293 */     return readValue(reader, clazz);
/* 283:    */   }
/* 284:    */   
/* 285:    */   public <T> T fromJson(InputStream inputStream, Class<T> clazz)
/* 286:    */   {
/* 287:298 */     return readValue(inputStream, clazz);
/* 288:    */   }
/* 289:    */   
/* 290:    */   public Object fromJson(String json)
/* 291:    */   {
/* 292:303 */     return parser().parse(json);
/* 293:    */   }
/* 294:    */   
/* 295:    */   public Object fromJson(Reader reader)
/* 296:    */   {
/* 297:308 */     return parser().parse(reader);
/* 298:    */   }
/* 299:    */   
/* 300:    */   public Object fromJson(byte[] bytes)
/* 301:    */   {
/* 302:313 */     return parser().parse(bytes);
/* 303:    */   }
/* 304:    */   
/* 305:    */   public Object fromJson(char[] chars)
/* 306:    */   {
/* 307:318 */     return parser().parse(chars);
/* 308:    */   }
/* 309:    */   
/* 310:    */   public Object fromJson(InputStream reader)
/* 311:    */   {
/* 312:323 */     return parser().parse(reader);
/* 313:    */   }
/* 314:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.implementation.ObjectMapperImpl
 * JD-Core Version:    0.7.0.1
 */