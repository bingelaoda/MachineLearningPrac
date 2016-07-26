/*   1:    */ package weka.core.packageManagement;
/*   2:    */ 
/*   3:    */ public class VersionPackageConstraint
/*   4:    */   extends PackageConstraint
/*   5:    */ {
/*   6: 34 */   public static String VERSION_KEY = "Version";
/*   7:    */   
/*   8:    */   public static abstract enum VersionComparison
/*   9:    */   {
/*  10: 38 */     EQUAL("="),  GREATERTHAN(">"),  GREATERTHANOREQUAL(">="),  LESSTHAN("<"),  LESSTHANOREQUAL("<=");
/*  11:    */     
/*  12:    */     private final String m_stringVal;
/*  13:    */     
/*  14:    */     private VersionComparison(String name)
/*  15:    */     {
/*  16: 89 */       this.m_stringVal = name;
/*  17:    */     }
/*  18:    */     
/*  19:    */     abstract boolean compatibleWith(VersionComparison paramVersionComparison);
/*  20:    */     
/*  21:    */     public String toString()
/*  22:    */     {
/*  23: 96 */       return this.m_stringVal;
/*  24:    */     }
/*  25:    */   }
/*  26:    */   
/*  27:101 */   protected VersionComparison m_constraint = null;
/*  28:    */   
/*  29:    */   protected static VersionComparison getVersionComparison(String compOpp)
/*  30:    */   {
/*  31:110 */     for (VersionComparison v : ) {
/*  32:111 */       if (v.toString().equalsIgnoreCase(compOpp)) {
/*  33:112 */         return v;
/*  34:    */       }
/*  35:    */     }
/*  36:115 */     return null;
/*  37:    */   }
/*  38:    */   
/*  39:    */   protected static int[] parseVersion(String version)
/*  40:    */   {
/*  41:127 */     int major = 0;
/*  42:128 */     int minor = 0;
/*  43:129 */     int revision = 0;
/*  44:130 */     int point = 0;
/*  45:131 */     int[] majMinRev = new int[4];
/*  46:    */     try
/*  47:    */     {
/*  48:134 */       String tmpStr = version;
/*  49:135 */       tmpStr = tmpStr.toLowerCase().replace("-snapshot", "");
/*  50:136 */       tmpStr = tmpStr.replace('-', '.');
/*  51:137 */       if (tmpStr.indexOf(".") > -1)
/*  52:    */       {
/*  53:138 */         major = Integer.parseInt(tmpStr.substring(0, tmpStr.indexOf(".")));
/*  54:139 */         tmpStr = tmpStr.substring(tmpStr.indexOf(".") + 1);
/*  55:140 */         if (tmpStr.indexOf(".") > -1)
/*  56:    */         {
/*  57:141 */           minor = Integer.parseInt(tmpStr.substring(0, tmpStr.indexOf(".")));
/*  58:142 */           tmpStr = tmpStr.substring(tmpStr.indexOf(".") + 1);
/*  59:144 */           if (tmpStr.indexOf(".") > 0)
/*  60:    */           {
/*  61:145 */             revision = Integer.parseInt(tmpStr.substring(0, tmpStr.indexOf(".")));
/*  62:    */             
/*  63:147 */             tmpStr = tmpStr.substring(tmpStr.indexOf(".") + 1);
/*  64:149 */             if (!tmpStr.equals("")) {
/*  65:150 */               point = Integer.parseInt(tmpStr);
/*  66:    */             } else {
/*  67:152 */               point = 0;
/*  68:    */             }
/*  69:    */           }
/*  70:155 */           else if (!tmpStr.equals(""))
/*  71:    */           {
/*  72:156 */             revision = Integer.parseInt(tmpStr);
/*  73:    */           }
/*  74:    */           else
/*  75:    */           {
/*  76:158 */             revision = 0;
/*  77:    */           }
/*  78:    */         }
/*  79:162 */         else if (!tmpStr.equals(""))
/*  80:    */         {
/*  81:163 */           minor = Integer.parseInt(tmpStr);
/*  82:    */         }
/*  83:    */         else
/*  84:    */         {
/*  85:165 */           minor = 0;
/*  86:    */         }
/*  87:    */       }
/*  88:169 */       else if (!tmpStr.equals(""))
/*  89:    */       {
/*  90:170 */         major = Integer.parseInt(tmpStr);
/*  91:    */       }
/*  92:    */       else
/*  93:    */       {
/*  94:172 */         major = 0;
/*  95:    */       }
/*  96:    */     }
/*  97:    */     catch (Exception e)
/*  98:    */     {
/*  99:176 */       e.printStackTrace();
/* 100:177 */       major = -1;
/* 101:178 */       minor = -1;
/* 102:179 */       revision = -1;
/* 103:    */     }
/* 104:    */     finally
/* 105:    */     {
/* 106:181 */       majMinRev[0] = major;
/* 107:182 */       majMinRev[1] = minor;
/* 108:183 */       majMinRev[2] = revision;
/* 109:    */     }
/* 110:186 */     return majMinRev;
/* 111:    */   }
/* 112:    */   
/* 113:    */   protected static boolean checkConstraint(String version1, VersionComparison constraint, String version2)
/* 114:    */   {
/* 115:202 */     VersionComparison c = compare(version1, version2);
/* 116:    */     
/* 117:204 */     return constraint.compatibleWith(c);
/* 118:    */   }
/* 119:    */   
/* 120:    */   public static VersionComparison compare(String version1, String version2)
/* 121:    */   {
/* 122:218 */     int[] majMinRev1 = parseVersion(version1);
/* 123:219 */     int[] majMinRev2 = parseVersion(version2);
/* 124:    */     VersionComparison result;
/* 125:    */     VersionComparison result;
/* 126:223 */     if (majMinRev1[0] < majMinRev2[0])
/* 127:    */     {
/* 128:224 */       result = VersionComparison.LESSTHAN;
/* 129:    */     }
/* 130:    */     else
/* 131:    */     {
/* 132:    */       VersionComparison result;
/* 133:225 */       if (majMinRev1[0] == majMinRev2[0])
/* 134:    */       {
/* 135:    */         VersionComparison result;
/* 136:226 */         if (majMinRev1[1] < majMinRev2[1])
/* 137:    */         {
/* 138:227 */           result = VersionComparison.LESSTHAN;
/* 139:    */         }
/* 140:    */         else
/* 141:    */         {
/* 142:    */           VersionComparison result;
/* 143:228 */           if (majMinRev1[1] == majMinRev2[1])
/* 144:    */           {
/* 145:    */             VersionComparison result;
/* 146:229 */             if (majMinRev1[2] < majMinRev2[2])
/* 147:    */             {
/* 148:230 */               result = VersionComparison.LESSTHAN;
/* 149:    */             }
/* 150:    */             else
/* 151:    */             {
/* 152:    */               VersionComparison result;
/* 153:231 */               if (majMinRev1[2] == majMinRev2[2])
/* 154:    */               {
/* 155:    */                 VersionComparison result;
/* 156:232 */                 if (majMinRev1[3] == majMinRev2[3]) {
/* 157:233 */                   result = VersionComparison.EQUAL;
/* 158:    */                 } else {
/* 159:235 */                   result = VersionComparison.GREATERTHAN;
/* 160:    */                 }
/* 161:    */               }
/* 162:    */               else
/* 163:    */               {
/* 164:238 */                 result = VersionComparison.GREATERTHAN;
/* 165:    */               }
/* 166:    */             }
/* 167:    */           }
/* 168:    */           else
/* 169:    */           {
/* 170:241 */             result = VersionComparison.GREATERTHAN;
/* 171:    */           }
/* 172:    */         }
/* 173:    */       }
/* 174:    */       else
/* 175:    */       {
/* 176:244 */         result = VersionComparison.GREATERTHAN;
/* 177:    */       }
/* 178:    */     }
/* 179:247 */     return result;
/* 180:    */   }
/* 181:    */   
/* 182:    */   public VersionPackageConstraint(Package p)
/* 183:    */   {
/* 184:251 */     setPackage(p);
/* 185:    */   }
/* 186:    */   
/* 187:    */   public void setVersionConstraint(VersionComparison c)
/* 188:    */   {
/* 189:255 */     this.m_constraint = c;
/* 190:    */   }
/* 191:    */   
/* 192:    */   public VersionComparison getVersionComparison()
/* 193:    */   {
/* 194:259 */     return this.m_constraint;
/* 195:    */   }
/* 196:    */   
/* 197:    */   public void setVersionConstraint(String constraint)
/* 198:    */   {
/* 199:263 */     for (VersionComparison v : ) {
/* 200:264 */       if (v.toString().equalsIgnoreCase(constraint))
/* 201:    */       {
/* 202:265 */         this.m_constraint = v;
/* 203:266 */         break;
/* 204:    */       }
/* 205:    */     }
/* 206:    */   }
/* 207:    */   
/* 208:    */   public PackageConstraint checkConstraint(PackageConstraint target)
/* 209:    */     throws Exception
/* 210:    */   {
/* 211:287 */     if (this.m_constraint == null) {
/* 212:288 */       throw new Exception("[VersionPackageConstraint] No constraint has been set!");
/* 213:    */     }
/* 214:293 */     if ((target instanceof VersionRangePackageConstraint)) {
/* 215:294 */       return target.checkConstraint(this);
/* 216:    */     }
/* 217:297 */     String targetVersion = target.getPackage().getPackageMetaDataElement(VERSION_KEY).toString();
/* 218:    */     
/* 219:299 */     String thisVersion = this.m_thePackage.getPackageMetaDataElement(VERSION_KEY).toString();
/* 220:    */     
/* 221:    */ 
/* 222:302 */     VersionComparison comp = compare(thisVersion, targetVersion);
/* 223:303 */     if (comp == VersionComparison.EQUAL)
/* 224:    */     {
/* 225:305 */       if (this.m_constraint == ((VersionPackageConstraint)target).getVersionComparison()) {
/* 226:307 */         return this;
/* 227:    */       }
/* 228:308 */       if ((this.m_constraint == VersionComparison.GREATERTHAN) && ((((VersionPackageConstraint)target).getVersionComparison() == VersionComparison.GREATERTHAN) || (((VersionPackageConstraint)target).getVersionComparison() == VersionComparison.GREATERTHANOREQUAL))) {
/* 229:311 */         return this;
/* 230:    */       }
/* 231:312 */       if (((this.m_constraint == VersionComparison.GREATERTHANOREQUAL) || (this.m_constraint == VersionComparison.GREATERTHAN)) && (((VersionPackageConstraint)target).getVersionComparison() == VersionComparison.GREATERTHAN)) {
/* 232:314 */         return target;
/* 233:    */       }
/* 234:317 */       return null;
/* 235:    */     }
/* 236:321 */     if ((((VersionPackageConstraint)target).getVersionComparison() == VersionComparison.GREATERTHAN) || (((VersionPackageConstraint)target).getVersionComparison() == VersionComparison.GREATERTHANOREQUAL))
/* 237:    */     {
/* 238:323 */       if ((this.m_constraint == VersionComparison.EQUAL) || (this.m_constraint == VersionComparison.GREATERTHAN) || (this.m_constraint == VersionComparison.GREATERTHANOREQUAL))
/* 239:    */       {
/* 240:328 */         if (comp == VersionComparison.GREATERTHAN) {
/* 241:329 */           return this;
/* 242:    */         }
/* 243:331 */         return target;
/* 244:    */       }
/* 245:335 */       return null;
/* 246:    */     }
/* 247:338 */     if ((((VersionPackageConstraint)target).getVersionComparison() == VersionComparison.LESSTHAN) || (((VersionPackageConstraint)target).getVersionComparison() == VersionComparison.LESSTHANOREQUAL))
/* 248:    */     {
/* 249:340 */       if ((this.m_constraint == VersionComparison.EQUAL) || (this.m_constraint == VersionComparison.LESSTHAN) || (this.m_constraint == VersionComparison.LESSTHANOREQUAL))
/* 250:    */       {
/* 251:345 */         if (comp == VersionComparison.GREATERTHAN) {
/* 252:346 */           return target;
/* 253:    */         }
/* 254:348 */         return this;
/* 255:    */       }
/* 256:352 */       return null;
/* 257:    */     }
/* 258:355 */     return null;
/* 259:    */   }
/* 260:    */   
/* 261:    */   public boolean checkConstraint(Package target)
/* 262:    */     throws Exception
/* 263:    */   {
/* 264:376 */     if (this.m_constraint == null) {
/* 265:377 */       throw new Exception("[VersionPackageConstraint] No constraint has been set!");
/* 266:    */     }
/* 267:381 */     String targetVersion = target.getPackageMetaDataElement(VERSION_KEY).toString();
/* 268:    */     
/* 269:383 */     String thisVersion = this.m_thePackage.getPackageMetaDataElement(VERSION_KEY).toString();
/* 270:    */     
/* 271:    */ 
/* 272:386 */     return checkConstraint(targetVersion, this.m_constraint, thisVersion);
/* 273:    */   }
/* 274:    */   
/* 275:    */   public String toString()
/* 276:    */   {
/* 277:391 */     String result = this.m_thePackage.getPackageMetaDataElement("PackageName").toString() + " (" + this.m_constraint + this.m_thePackage.getPackageMetaDataElement("Version").toString() + ")";
/* 278:    */     
/* 279:    */ 
/* 280:    */ 
/* 281:    */ 
/* 282:396 */     return result;
/* 283:    */   }
/* 284:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.packageManagement.VersionPackageConstraint
 * JD-Core Version:    0.7.0.1
 */