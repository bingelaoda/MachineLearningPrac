/*   1:    */ package org.netlib.util;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.DataInputStream;
/*   5:    */ import java.io.IOException;
/*   6:    */ import java.io.InputStreamReader;
/*   7:    */ import java.io.PrintStream;
/*   8:    */ import java.util.Enumeration;
/*   9:    */ import java.util.Vector;
/*  10:    */ import org.j_paine.formatter.EndOfFileWhenStartingReadException;
/*  11:    */ import org.j_paine.formatter.Formatter;
/*  12:    */ 
/*  13:    */ public class Util
/*  14:    */ {
/*  15:    */   public static String stringInsert(String paramString1, String paramString2, int paramInt1, int paramInt2)
/*  16:    */   {
/*  17: 63 */     String str = new String(paramString1.substring(0, paramInt1 - 1) + paramString2.substring(0, paramInt2 - paramInt1 + 1) + paramString1.substring(paramInt2, paramString1.length()));
/*  18:    */     
/*  19:    */ 
/*  20:    */ 
/*  21: 67 */     return str;
/*  22:    */   }
/*  23:    */   
/*  24:    */   public static String stringInsert(String paramString1, String paramString2, int paramInt)
/*  25:    */   {
/*  26: 81 */     return stringInsert(paramString1, paramString2, paramInt, paramInt);
/*  27:    */   }
/*  28:    */   
/*  29:    */   public static String strCharAt(String paramString, int paramInt)
/*  30:    */   {
/*  31: 94 */     return String.valueOf(paramString.charAt(paramInt - 1));
/*  32:    */   }
/*  33:    */   
/*  34:    */   public static int max(int paramInt1, int paramInt2, int paramInt3)
/*  35:    */   {
/*  36:107 */     return Math.max(paramInt1 > paramInt2 ? paramInt1 : paramInt2, Math.max(paramInt2, paramInt3));
/*  37:    */   }
/*  38:    */   
/*  39:    */   public static float max(float paramFloat1, float paramFloat2, float paramFloat3)
/*  40:    */   {
/*  41:120 */     return Math.max(paramFloat1 > paramFloat2 ? paramFloat1 : paramFloat2, Math.max(paramFloat2, paramFloat3));
/*  42:    */   }
/*  43:    */   
/*  44:    */   public static double max(double paramDouble1, double paramDouble2, double paramDouble3)
/*  45:    */   {
/*  46:133 */     return Math.max(paramDouble1 > paramDouble2 ? paramDouble1 : paramDouble2, Math.max(paramDouble2, paramDouble3));
/*  47:    */   }
/*  48:    */   
/*  49:    */   public static int min(int paramInt1, int paramInt2, int paramInt3)
/*  50:    */   {
/*  51:146 */     return Math.min(paramInt1 < paramInt2 ? paramInt1 : paramInt2, Math.min(paramInt2, paramInt3));
/*  52:    */   }
/*  53:    */   
/*  54:    */   public static float min(float paramFloat1, float paramFloat2, float paramFloat3)
/*  55:    */   {
/*  56:159 */     return Math.min(paramFloat1 < paramFloat2 ? paramFloat1 : paramFloat2, Math.min(paramFloat2, paramFloat3));
/*  57:    */   }
/*  58:    */   
/*  59:    */   public static double min(double paramDouble1, double paramDouble2, double paramDouble3)
/*  60:    */   {
/*  61:172 */     return Math.min(paramDouble1 < paramDouble2 ? paramDouble1 : paramDouble2, Math.min(paramDouble2, paramDouble3));
/*  62:    */   }
/*  63:    */   
/*  64:    */   public static double log10(double paramDouble)
/*  65:    */   {
/*  66:183 */     return Math.log(paramDouble) / 2.30258509D;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public static float log10(float paramFloat)
/*  70:    */   {
/*  71:194 */     return (float)(Math.log(paramFloat) / 2.30258509D);
/*  72:    */   }
/*  73:    */   
/*  74:    */   public static int nint(float paramFloat)
/*  75:    */   {
/*  76:211 */     return (int)(paramFloat >= 0.0F ? paramFloat + 0.5D : paramFloat - 0.5D);
/*  77:    */   }
/*  78:    */   
/*  79:    */   public static int idnint(double paramDouble)
/*  80:    */   {
/*  81:228 */     return (int)(paramDouble >= 0.0D ? paramDouble + 0.5D : paramDouble - 0.5D);
/*  82:    */   }
/*  83:    */   
/*  84:    */   public static float sign(float paramFloat1, float paramFloat2)
/*  85:    */   {
/*  86:246 */     return paramFloat2 >= 0.0F ? Math.abs(paramFloat1) : -Math.abs(paramFloat1);
/*  87:    */   }
/*  88:    */   
/*  89:    */   public static int isign(int paramInt1, int paramInt2)
/*  90:    */   {
/*  91:264 */     return paramInt2 >= 0 ? Math.abs(paramInt1) : -Math.abs(paramInt1);
/*  92:    */   }
/*  93:    */   
/*  94:    */   public static double dsign(double paramDouble1, double paramDouble2)
/*  95:    */   {
/*  96:282 */     return paramDouble2 >= 0.0D ? Math.abs(paramDouble1) : -Math.abs(paramDouble1);
/*  97:    */   }
/*  98:    */   
/*  99:    */   public static float dim(float paramFloat1, float paramFloat2)
/* 100:    */   {
/* 101:300 */     return paramFloat1 > paramFloat2 ? paramFloat1 - paramFloat2 : 0.0F;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public static int idim(int paramInt1, int paramInt2)
/* 105:    */   {
/* 106:318 */     return paramInt1 > paramInt2 ? paramInt1 - paramInt2 : 0;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public static double ddim(double paramDouble1, double paramDouble2)
/* 110:    */   {
/* 111:336 */     return paramDouble1 > paramDouble2 ? paramDouble1 - paramDouble2 : 0.0D;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public static double sinh(double paramDouble)
/* 115:    */   {
/* 116:347 */     return (Math.exp(paramDouble) - Math.exp(-paramDouble)) * 0.5D;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public static double cosh(double paramDouble)
/* 120:    */   {
/* 121:358 */     return (Math.exp(paramDouble) + Math.exp(-paramDouble)) * 0.5D;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public static double tanh(double paramDouble)
/* 125:    */   {
/* 126:369 */     return sinh(paramDouble) / cosh(paramDouble);
/* 127:    */   }
/* 128:    */   
/* 129:    */   public static void pause()
/* 130:    */   {
/* 131:378 */     pause(null);
/* 132:    */   }
/* 133:    */   
/* 134:    */   public static void pause(String paramString)
/* 135:    */   {
/* 136:390 */     if (paramString != null) {
/* 137:391 */       System.err.println("PAUSE: " + paramString);
/* 138:    */     } else {
/* 139:393 */       System.err.print("PAUSE: ");
/* 140:    */     }
/* 141:395 */     System.err.println("To resume execution, type:   go");
/* 142:396 */     System.err.println("Any other input will terminate the program.");
/* 143:    */     
/* 144:398 */     BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(System.in));
/* 145:    */     
/* 146:400 */     String str = null;
/* 147:    */     try
/* 148:    */     {
/* 149:403 */       str = localBufferedReader.readLine();
/* 150:    */     }
/* 151:    */     catch (IOException localIOException)
/* 152:    */     {
/* 153:405 */       str = null;
/* 154:    */     }
/* 155:408 */     if ((str == null) || (!str.equals("go")))
/* 156:    */     {
/* 157:409 */       System.err.println("STOP");
/* 158:410 */       System.exit(0);
/* 159:    */     }
/* 160:    */   }
/* 161:    */   
/* 162:    */   public static void f77write(String paramString, Vector paramVector)
/* 163:    */   {
/* 164:423 */     if (paramString == null)
/* 165:    */     {
/* 166:424 */       f77write(paramVector);
/* 167:425 */       return;
/* 168:    */     }
/* 169:    */     try
/* 170:    */     {
/* 171:429 */       Formatter localFormatter = new Formatter(paramString);
/* 172:430 */       localObject = processVector(paramVector);
/* 173:431 */       localFormatter.write((Vector)localObject, System.out);
/* 174:432 */       System.out.println();
/* 175:    */     }
/* 176:    */     catch (Exception localException)
/* 177:    */     {
/* 178:435 */       Object localObject = localException.getMessage();
/* 179:437 */       if (localObject != null) {
/* 180:438 */         System.out.println((String)localObject);
/* 181:    */       } else {
/* 182:440 */         System.out.println();
/* 183:    */       }
/* 184:    */     }
/* 185:    */   }
/* 186:    */   
/* 187:    */   public static void f77write(Vector paramVector)
/* 188:    */   {
/* 189:455 */     Vector localVector = processVector(paramVector);
/* 190:    */     
/* 191:457 */     Enumeration localEnumeration = localVector.elements();
/* 192:465 */     if (localEnumeration.hasMoreElements())
/* 193:    */     {
/* 194:466 */       Object localObject = localEnumeration.nextElement();
/* 195:467 */       if ((localObject instanceof String)) {
/* 196:468 */         System.out.print(" ");
/* 197:    */       }
/* 198:469 */       output_unformatted_element(localObject);
/* 199:    */     }
/* 200:472 */     while (localEnumeration.hasMoreElements()) {
/* 201:473 */       output_unformatted_element(localEnumeration.nextElement());
/* 202:    */     }
/* 203:475 */     System.out.println();
/* 204:    */   }
/* 205:    */   
/* 206:    */   private static void output_unformatted_element(Object paramObject)
/* 207:    */   {
/* 208:479 */     if ((paramObject instanceof Boolean))
/* 209:    */     {
/* 210:481 */       if (((Boolean)paramObject).booleanValue()) {
/* 211:482 */         System.out.print(" T");
/* 212:    */       } else {
/* 213:484 */         System.out.print(" F");
/* 214:    */       }
/* 215:    */     }
/* 216:486 */     else if (((paramObject instanceof Float)) || ((paramObject instanceof Double))) {
/* 217:487 */       System.out.print("  " + paramObject);
/* 218:488 */     } else if ((paramObject instanceof String)) {
/* 219:489 */       System.out.print(paramObject);
/* 220:    */     } else {
/* 221:491 */       System.out.print(" " + paramObject);
/* 222:    */     }
/* 223:    */   }
/* 224:    */   
/* 225:    */   public static int f77read(String paramString, Vector paramVector)
/* 226:    */   {
/* 227:    */     try
/* 228:    */     {
/* 229:504 */       Formatter localFormatter = new Formatter(paramString);
/* 230:505 */       localFormatter.read(paramVector, new DataInputStream(System.in));
/* 231:    */     }
/* 232:    */     catch (EndOfFileWhenStartingReadException localEndOfFileWhenStartingReadException)
/* 233:    */     {
/* 234:508 */       return 0;
/* 235:    */     }
/* 236:    */     catch (Exception localException)
/* 237:    */     {
/* 238:511 */       String str = localException.getMessage();
/* 239:513 */       if (str != null) {
/* 240:514 */         System.out.println(str);
/* 241:    */       } else {
/* 242:516 */         System.out.println("Warning: READ exception.");
/* 243:    */       }
/* 244:518 */       return -1;
/* 245:    */     }
/* 246:521 */     return paramVector.size();
/* 247:    */   }
/* 248:    */   
/* 249:    */   static Vector processVector(Vector paramVector)
/* 250:    */   {
/* 251:532 */     Vector localVector = new Vector();
/* 252:534 */     for (Enumeration localEnumeration = paramVector.elements(); localEnumeration.hasMoreElements();)
/* 253:    */     {
/* 254:535 */       Object localObject = localEnumeration.nextElement();
/* 255:537 */       if ((localObject instanceof ArraySpec)) {
/* 256:538 */         localVector.addAll(((ArraySpec)localObject).get_vec());
/* 257:    */       } else {
/* 258:540 */         localVector.addElement(localObject);
/* 259:    */       }
/* 260:    */     }
/* 261:543 */     return localVector;
/* 262:    */   }
/* 263:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.util.Util
 * JD-Core Version:    0.7.0.1
 */