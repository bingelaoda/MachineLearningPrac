/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.InputStream;
/*   4:    */ import java.io.InputStreamReader;
/*   5:    */ import java.io.LineNumberReader;
/*   6:    */ import java.io.PrintStream;
/*   7:    */ 
/*   8:    */ public class Version
/*   9:    */   implements Comparable<String>, RevisionHandler
/*  10:    */ {
/*  11:    */   public static final String VERSION_FILE = "weka/core/version.txt";
/*  12: 45 */   public static int MAJOR = 3;
/*  13: 48 */   public static int MINOR = 4;
/*  14: 51 */   public static int REVISION = 3;
/*  15: 54 */   public static int POINT = 0;
/*  16: 57 */   public static boolean SNAPSHOT = false;
/*  17:    */   protected static final String SNAPSHOT_STRING = "-SNAPSHOT";
/*  18:    */   
/*  19:    */   static
/*  20:    */   {
/*  21:    */     try
/*  22:    */     {
/*  23: 63 */       InputStream inR = new Version().getClass().getClassLoader().getResourceAsStream("weka/core/version.txt");
/*  24:    */       
/*  25:    */ 
/*  26: 66 */       LineNumberReader lnr = new LineNumberReader(new InputStreamReader(inR));
/*  27:    */       
/*  28: 68 */       String line = lnr.readLine();
/*  29: 69 */       int[] maj = new int[1];
/*  30: 70 */       int[] min = new int[1];
/*  31: 71 */       int[] rev = new int[1];
/*  32: 72 */       int[] point = new int[1];
/*  33: 73 */       SNAPSHOT = parseVersion(line, maj, min, rev, point);
/*  34: 74 */       MAJOR = maj[0];
/*  35: 75 */       MINOR = min[0];
/*  36: 76 */       REVISION = rev[0];
/*  37: 77 */       POINT = point[0];
/*  38: 78 */       lnr.close();
/*  39:    */     }
/*  40:    */     catch (Exception e)
/*  41:    */     {
/*  42: 80 */       System.err.println(Version.class.getName() + ": Unable to load version information!");
/*  43:    */     }
/*  44:    */   }
/*  45:    */   
/*  46: 86 */   public static String VERSION = MAJOR + "." + MINOR + "." + REVISION + (POINT > 0 ? "." + POINT : "") + (SNAPSHOT ? "-SNAPSHOT" : "");
/*  47:    */   
/*  48:    */   private static boolean parseVersion(String version, int[] maj, int[] min, int[] rev, int[] point)
/*  49:    */   {
/*  50: 99 */     int major = 0;
/*  51:100 */     int minor = 0;
/*  52:101 */     int revision = 0;
/*  53:102 */     int pnt = 0;
/*  54:103 */     boolean isSnapshot = false;
/*  55:    */     try
/*  56:    */     {
/*  57:106 */       String tmpStr = version;
/*  58:107 */       if (tmpStr.toLowerCase().endsWith("-snapshot"))
/*  59:    */       {
/*  60:108 */         tmpStr = tmpStr.substring(0, tmpStr.toLowerCase().indexOf("-snapshot"));
/*  61:109 */         isSnapshot = true;
/*  62:    */       }
/*  63:111 */       tmpStr = tmpStr.replace('-', '.');
/*  64:112 */       if (tmpStr.indexOf(".") > -1)
/*  65:    */       {
/*  66:113 */         major = Integer.parseInt(tmpStr.substring(0, tmpStr.indexOf(".")));
/*  67:114 */         tmpStr = tmpStr.substring(tmpStr.indexOf(".") + 1);
/*  68:115 */         if (tmpStr.indexOf(".") > -1)
/*  69:    */         {
/*  70:116 */           minor = Integer.parseInt(tmpStr.substring(0, tmpStr.indexOf(".")));
/*  71:117 */           tmpStr = tmpStr.substring(tmpStr.indexOf(".") + 1);
/*  72:118 */           if (tmpStr.indexOf(".") > 0)
/*  73:    */           {
/*  74:119 */             revision = Integer.parseInt(tmpStr.substring(0, tmpStr.indexOf(".")));
/*  75:    */             
/*  76:121 */             tmpStr = tmpStr.substring(tmpStr.indexOf(".") + 1);
/*  77:123 */             if (!tmpStr.equals("")) {
/*  78:124 */               pnt = Integer.parseInt(tmpStr);
/*  79:    */             } else {
/*  80:126 */               pnt = 0;
/*  81:    */             }
/*  82:    */           }
/*  83:129 */           else if (!tmpStr.equals(""))
/*  84:    */           {
/*  85:130 */             revision = Integer.parseInt(tmpStr);
/*  86:    */           }
/*  87:    */           else
/*  88:    */           {
/*  89:132 */             revision = 0;
/*  90:    */           }
/*  91:    */         }
/*  92:136 */         else if (!tmpStr.equals(""))
/*  93:    */         {
/*  94:137 */           minor = Integer.parseInt(tmpStr);
/*  95:    */         }
/*  96:    */         else
/*  97:    */         {
/*  98:139 */           minor = 0;
/*  99:    */         }
/* 100:    */       }
/* 101:143 */       else if (!tmpStr.equals(""))
/* 102:    */       {
/* 103:144 */         major = Integer.parseInt(tmpStr);
/* 104:    */       }
/* 105:    */       else
/* 106:    */       {
/* 107:146 */         major = 0;
/* 108:    */       }
/* 109:    */     }
/* 110:    */     catch (Exception e)
/* 111:    */     {
/* 112:150 */       e.printStackTrace();
/* 113:151 */       major = -1;
/* 114:152 */       minor = -1;
/* 115:153 */       revision = -1;
/* 116:    */     }
/* 117:    */     finally
/* 118:    */     {
/* 119:155 */       maj[0] = major;
/* 120:156 */       min[0] = minor;
/* 121:157 */       rev[0] = revision;
/* 122:158 */       point[0] = pnt;
/* 123:    */     }
/* 124:161 */     return isSnapshot;
/* 125:    */   }
/* 126:    */   
/* 127:    */   public int compareTo(String o)
/* 128:    */   {
/* 129:178 */     int[] maj = new int[1];
/* 130:179 */     int[] min = new int[1];
/* 131:180 */     int[] rev = new int[1];
/* 132:181 */     int[] point = new int[1];
/* 133:    */     
/* 134:    */ 
/* 135:    */ 
/* 136:185 */     parseVersion(o, maj, min, rev, point);
/* 137:186 */     int major = maj[0];
/* 138:187 */     int minor = min[0];
/* 139:188 */     int revision = rev[0];
/* 140:189 */     int pnt = point[0];
/* 141:    */     int result;
/* 142:    */     int result;
/* 143:191 */     if (MAJOR < major)
/* 144:    */     {
/* 145:192 */       result = -1;
/* 146:    */     }
/* 147:    */     else
/* 148:    */     {
/* 149:    */       int result;
/* 150:193 */       if (MAJOR == major)
/* 151:    */       {
/* 152:    */         int result;
/* 153:194 */         if (MINOR < minor)
/* 154:    */         {
/* 155:195 */           result = -1;
/* 156:    */         }
/* 157:    */         else
/* 158:    */         {
/* 159:    */           int result;
/* 160:196 */           if (MINOR == minor)
/* 161:    */           {
/* 162:    */             int result;
/* 163:197 */             if (REVISION < revision)
/* 164:    */             {
/* 165:198 */               result = -1;
/* 166:    */             }
/* 167:    */             else
/* 168:    */             {
/* 169:    */               int result;
/* 170:199 */               if (REVISION == revision)
/* 171:    */               {
/* 172:    */                 int result;
/* 173:200 */                 if (POINT < pnt)
/* 174:    */                 {
/* 175:201 */                   result = -1;
/* 176:    */                 }
/* 177:    */                 else
/* 178:    */                 {
/* 179:    */                   int result;
/* 180:202 */                   if (POINT == pnt) {
/* 181:203 */                     result = 0;
/* 182:    */                   } else {
/* 183:205 */                     result = 1;
/* 184:    */                   }
/* 185:    */                 }
/* 186:    */               }
/* 187:    */               else
/* 188:    */               {
/* 189:208 */                 result = 1;
/* 190:    */               }
/* 191:    */             }
/* 192:    */           }
/* 193:    */           else
/* 194:    */           {
/* 195:211 */             result = 1;
/* 196:    */           }
/* 197:    */         }
/* 198:    */       }
/* 199:    */       else
/* 200:    */       {
/* 201:214 */         result = 1;
/* 202:    */       }
/* 203:    */     }
/* 204:217 */     return result;
/* 205:    */   }
/* 206:    */   
/* 207:    */   public boolean equals(Object o)
/* 208:    */   {
/* 209:228 */     return compareTo((String)o) == 0;
/* 210:    */   }
/* 211:    */   
/* 212:    */   public boolean isOlder(String o)
/* 213:    */   {
/* 214:239 */     return compareTo(o) == -1;
/* 215:    */   }
/* 216:    */   
/* 217:    */   public boolean isNewer(String o)
/* 218:    */   {
/* 219:250 */     return compareTo(o) == 1;
/* 220:    */   }
/* 221:    */   
/* 222:    */   public String toString()
/* 223:    */   {
/* 224:260 */     return VERSION;
/* 225:    */   }
/* 226:    */   
/* 227:    */   public String getRevision()
/* 228:    */   {
/* 229:270 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 230:    */   }
/* 231:    */   
/* 232:    */   public static void main(String[] args)
/* 233:    */   {
/* 234:283 */     System.out.println(VERSION + "\n");
/* 235:    */     
/* 236:    */ 
/* 237:286 */     Version v = new Version();
/* 238:287 */     System.out.println("-1? " + v.compareTo("5.0.1"));
/* 239:288 */     System.out.println(" 0? " + v.compareTo(VERSION));
/* 240:289 */     System.out.println("+1? " + v.compareTo("3.4.0"));
/* 241:    */     
/* 242:291 */     String tmpStr = "5.0.1";
/* 243:292 */     System.out.println("\ncomparing with " + tmpStr);
/* 244:293 */     System.out.println("isOlder? " + v.isOlder(tmpStr));
/* 245:294 */     System.out.println("equals ? " + v.equals(tmpStr));
/* 246:295 */     System.out.println("isNewer? " + v.isNewer(tmpStr));
/* 247:    */     
/* 248:297 */     tmpStr = VERSION;
/* 249:298 */     System.out.println("\ncomparing with " + tmpStr);
/* 250:299 */     System.out.println("isOlder? " + v.isOlder(tmpStr));
/* 251:300 */     System.out.println("equals ? " + v.equals(tmpStr));
/* 252:301 */     System.out.println("isNewer? " + v.isNewer(tmpStr));
/* 253:    */     
/* 254:303 */     tmpStr = "3.4.0";
/* 255:304 */     System.out.println("\ncomparing with " + tmpStr);
/* 256:305 */     System.out.println("isOlder? " + v.isOlder(tmpStr));
/* 257:306 */     System.out.println("equals ? " + v.equals(tmpStr));
/* 258:307 */     System.out.println("isNewer? " + v.isNewer(tmpStr));
/* 259:    */     
/* 260:309 */     tmpStr = "3.4";
/* 261:310 */     System.out.println("\ncomparing with " + tmpStr);
/* 262:311 */     System.out.println("isOlder? " + v.isOlder(tmpStr));
/* 263:312 */     System.out.println("equals ? " + v.equals(tmpStr));
/* 264:313 */     System.out.println("isNewer? " + v.isNewer(tmpStr));
/* 265:    */     
/* 266:315 */     tmpStr = "5";
/* 267:316 */     System.out.println("\ncomparing with " + tmpStr);
/* 268:317 */     System.out.println("isOlder? " + v.isOlder(tmpStr));
/* 269:318 */     System.out.println("equals ? " + v.equals(tmpStr));
/* 270:319 */     System.out.println("isNewer? " + v.isNewer(tmpStr));
/* 271:    */   }
/* 272:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.Version
 * JD-Core Version:    0.7.0.1
 */