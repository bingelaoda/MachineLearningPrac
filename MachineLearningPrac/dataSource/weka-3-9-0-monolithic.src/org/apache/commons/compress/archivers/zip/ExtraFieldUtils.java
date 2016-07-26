/*   1:    */ package org.apache.commons.compress.archivers.zip;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import java.util.Map;
/*   6:    */ import java.util.concurrent.ConcurrentHashMap;
/*   7:    */ import java.util.zip.ZipException;
/*   8:    */ 
/*   9:    */ public class ExtraFieldUtils
/*  10:    */ {
/*  11:    */   private static final int WORD = 4;
/*  12: 41 */   private static final Map<ZipShort, Class<?>> implementations = new ConcurrentHashMap();
/*  13:    */   
/*  14:    */   static
/*  15:    */   {
/*  16: 42 */     register(AsiExtraField.class);
/*  17: 43 */     register(X5455_ExtendedTimestamp.class);
/*  18: 44 */     register(X7875_NewUnix.class);
/*  19: 45 */     register(JarMarker.class);
/*  20: 46 */     register(UnicodePathExtraField.class);
/*  21: 47 */     register(UnicodeCommentExtraField.class);
/*  22: 48 */     register(Zip64ExtendedInformationExtraField.class);
/*  23:    */   }
/*  24:    */   
/*  25:    */   public static void register(Class<?> c)
/*  26:    */   {
/*  27:    */     try
/*  28:    */     {
/*  29: 60 */       ZipExtraField ze = (ZipExtraField)c.newInstance();
/*  30: 61 */       implementations.put(ze.getHeaderId(), c);
/*  31:    */     }
/*  32:    */     catch (ClassCastException cc)
/*  33:    */     {
/*  34: 63 */       throw new RuntimeException(c + " doesn't implement ZipExtraField");
/*  35:    */     }
/*  36:    */     catch (InstantiationException ie)
/*  37:    */     {
/*  38: 65 */       throw new RuntimeException(c + " is not a concrete class");
/*  39:    */     }
/*  40:    */     catch (IllegalAccessException ie)
/*  41:    */     {
/*  42: 67 */       throw new RuntimeException(c + "'s no-arg constructor is not public");
/*  43:    */     }
/*  44:    */   }
/*  45:    */   
/*  46:    */   public static ZipExtraField createExtraField(ZipShort headerId)
/*  47:    */     throws InstantiationException, IllegalAccessException
/*  48:    */   {
/*  49: 81 */     Class<?> c = (Class)implementations.get(headerId);
/*  50: 82 */     if (c != null) {
/*  51: 83 */       return (ZipExtraField)c.newInstance();
/*  52:    */     }
/*  53: 85 */     UnrecognizedExtraField u = new UnrecognizedExtraField();
/*  54: 86 */     u.setHeaderId(headerId);
/*  55: 87 */     return u;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public static ZipExtraField[] parse(byte[] data)
/*  59:    */     throws ZipException
/*  60:    */   {
/*  61: 99 */     return parse(data, true, UnparseableExtraField.THROW);
/*  62:    */   }
/*  63:    */   
/*  64:    */   public static ZipExtraField[] parse(byte[] data, boolean local)
/*  65:    */     throws ZipException
/*  66:    */   {
/*  67:113 */     return parse(data, local, UnparseableExtraField.THROW);
/*  68:    */   }
/*  69:    */   
/*  70:    */   public static ZipExtraField[] parse(byte[] data, boolean local, UnparseableExtraField onUnparseableData)
/*  71:    */     throws ZipException
/*  72:    */   {
/*  73:132 */     List<ZipExtraField> v = new ArrayList();
/*  74:133 */     int start = 0;
/*  75:135 */     while (start <= data.length - 4)
/*  76:    */     {
/*  77:136 */       ZipShort headerId = new ZipShort(data, start);
/*  78:137 */       int length = new ZipShort(data, start + 2).getValue();
/*  79:138 */       if (start + 4 + length > data.length) {
/*  80:139 */         switch (onUnparseableData.getKey())
/*  81:    */         {
/*  82:    */         case 0: 
/*  83:141 */           throw new ZipException("bad extra field starting at " + start + ".  Block length of " + length + " bytes exceeds remaining" + " data of " + (data.length - start - 4) + " bytes.");
/*  84:    */         case 2: 
/*  85:148 */           UnparseableExtraFieldData field = new UnparseableExtraFieldData();
/*  86:150 */           if (local) {
/*  87:151 */             field.parseFromLocalFileData(data, start, data.length - start);
/*  88:    */           } else {
/*  89:154 */             field.parseFromCentralDirectoryData(data, start, data.length - start);
/*  90:    */           }
/*  91:157 */           v.add(field);
/*  92:    */         case 1: 
/*  93:    */           break;
/*  94:    */         default: 
/*  95:165 */           throw new ZipException("unknown UnparseableExtraField key: " + onUnparseableData.getKey());
/*  96:    */         }
/*  97:    */       }
/*  98:    */       try
/*  99:    */       {
/* 100:170 */         ZipExtraField ze = createExtraField(headerId);
/* 101:171 */         if (local) {
/* 102:172 */           ze.parseFromLocalFileData(data, start + 4, length);
/* 103:    */         } else {
/* 104:174 */           ze.parseFromCentralDirectoryData(data, start + 4, length);
/* 105:    */         }
/* 106:177 */         v.add(ze);
/* 107:    */       }
/* 108:    */       catch (InstantiationException ie)
/* 109:    */       {
/* 110:179 */         throw ((ZipException)new ZipException(ie.getMessage()).initCause(ie));
/* 111:    */       }
/* 112:    */       catch (IllegalAccessException iae)
/* 113:    */       {
/* 114:181 */         throw ((ZipException)new ZipException(iae.getMessage()).initCause(iae));
/* 115:    */       }
/* 116:183 */       start += length + 4;
/* 117:    */     }
/* 118:186 */     ZipExtraField[] result = new ZipExtraField[v.size()];
/* 119:187 */     return (ZipExtraField[])v.toArray(result);
/* 120:    */   }
/* 121:    */   
/* 122:    */   public static byte[] mergeLocalFileDataData(ZipExtraField[] data)
/* 123:    */   {
/* 124:196 */     boolean lastIsUnparseableHolder = (data.length > 0) && ((data[(data.length - 1)] instanceof UnparseableExtraFieldData));
/* 125:    */     
/* 126:198 */     int regularExtraFieldCount = lastIsUnparseableHolder ? data.length - 1 : data.length;
/* 127:    */     
/* 128:    */ 
/* 129:201 */     int sum = 4 * regularExtraFieldCount;
/* 130:202 */     for (ZipExtraField element : data) {
/* 131:203 */       sum += element.getLocalFileDataLength().getValue();
/* 132:    */     }
/* 133:206 */     byte[] result = new byte[sum];
/* 134:207 */     int start = 0;
/* 135:208 */     for (int i = 0; i < regularExtraFieldCount; i++)
/* 136:    */     {
/* 137:209 */       System.arraycopy(data[i].getHeaderId().getBytes(), 0, result, start, 2);
/* 138:    */       
/* 139:211 */       System.arraycopy(data[i].getLocalFileDataLength().getBytes(), 0, result, start + 2, 2);
/* 140:    */       
/* 141:213 */       start += 4;
/* 142:214 */       byte[] local = data[i].getLocalFileDataData();
/* 143:215 */       if (local != null)
/* 144:    */       {
/* 145:216 */         System.arraycopy(local, 0, result, start, local.length);
/* 146:217 */         start += local.length;
/* 147:    */       }
/* 148:    */     }
/* 149:220 */     if (lastIsUnparseableHolder)
/* 150:    */     {
/* 151:221 */       byte[] local = data[(data.length - 1)].getLocalFileDataData();
/* 152:222 */       if (local != null) {
/* 153:223 */         System.arraycopy(local, 0, result, start, local.length);
/* 154:    */       }
/* 155:    */     }
/* 156:226 */     return result;
/* 157:    */   }
/* 158:    */   
/* 159:    */   public static byte[] mergeCentralDirectoryData(ZipExtraField[] data)
/* 160:    */   {
/* 161:235 */     boolean lastIsUnparseableHolder = (data.length > 0) && ((data[(data.length - 1)] instanceof UnparseableExtraFieldData));
/* 162:    */     
/* 163:237 */     int regularExtraFieldCount = lastIsUnparseableHolder ? data.length - 1 : data.length;
/* 164:    */     
/* 165:    */ 
/* 166:240 */     int sum = 4 * regularExtraFieldCount;
/* 167:241 */     for (ZipExtraField element : data) {
/* 168:242 */       sum += element.getCentralDirectoryLength().getValue();
/* 169:    */     }
/* 170:244 */     byte[] result = new byte[sum];
/* 171:245 */     int start = 0;
/* 172:246 */     for (int i = 0; i < regularExtraFieldCount; i++)
/* 173:    */     {
/* 174:247 */       System.arraycopy(data[i].getHeaderId().getBytes(), 0, result, start, 2);
/* 175:    */       
/* 176:249 */       System.arraycopy(data[i].getCentralDirectoryLength().getBytes(), 0, result, start + 2, 2);
/* 177:    */       
/* 178:251 */       start += 4;
/* 179:252 */       byte[] local = data[i].getCentralDirectoryData();
/* 180:253 */       if (local != null)
/* 181:    */       {
/* 182:254 */         System.arraycopy(local, 0, result, start, local.length);
/* 183:255 */         start += local.length;
/* 184:    */       }
/* 185:    */     }
/* 186:258 */     if (lastIsUnparseableHolder)
/* 187:    */     {
/* 188:259 */       byte[] local = data[(data.length - 1)].getCentralDirectoryData();
/* 189:260 */       if (local != null) {
/* 190:261 */         System.arraycopy(local, 0, result, start, local.length);
/* 191:    */       }
/* 192:    */     }
/* 193:264 */     return result;
/* 194:    */   }
/* 195:    */   
/* 196:    */   public static final class UnparseableExtraField
/* 197:    */   {
/* 198:    */     public static final int THROW_KEY = 0;
/* 199:    */     public static final int SKIP_KEY = 1;
/* 200:    */     public static final int READ_KEY = 2;
/* 201:290 */     public static final UnparseableExtraField THROW = new UnparseableExtraField(0);
/* 202:297 */     public static final UnparseableExtraField SKIP = new UnparseableExtraField(1);
/* 203:304 */     public static final UnparseableExtraField READ = new UnparseableExtraField(2);
/* 204:    */     private final int key;
/* 205:    */     
/* 206:    */     private UnparseableExtraField(int k)
/* 207:    */     {
/* 208:310 */       this.key = k;
/* 209:    */     }
/* 210:    */     
/* 211:    */     public int getKey()
/* 212:    */     {
/* 213:317 */       return this.key;
/* 214:    */     }
/* 215:    */   }
/* 216:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.ExtraFieldUtils
 * JD-Core Version:    0.7.0.1
 */