/*    1:     */ package weka.classifiers.mi;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import weka.core.Optimization;
/*    5:     */ import weka.core.RevisionUtils;
/*    6:     */ 
/*    7:     */ class TLD_Optm
/*    8:     */   extends Optimization
/*    9:     */ {
/*   10:     */   private double[] num;
/*   11:     */   private double[] sSq;
/*   12:     */   private double[] xBar;
/*   13:     */   
/*   14:     */   public void setNum(double[] n)
/*   15:     */   {
/*   16: 944 */     this.num = n;
/*   17:     */   }
/*   18:     */   
/*   19:     */   public void setSSquare(double[] s)
/*   20:     */   {
/*   21: 948 */     this.sSq = s;
/*   22:     */   }
/*   23:     */   
/*   24:     */   public void setXBar(double[] x)
/*   25:     */   {
/*   26: 952 */     this.xBar = x;
/*   27:     */   }
/*   28:     */   
/*   29:     */   public static double diffLnGamma(double b)
/*   30:     */   {
/*   31: 962 */     double[] coef = { 76.180091729471457D, -86.505320329416776D, 24.014098240830911D, -1.231739572450155D, 0.00120865097386618D, -5.395239384953E-006D };
/*   32:     */     
/*   33: 964 */     double rt = -0.5D;
/*   34: 965 */     rt += (b + 1.0D) * Math.log(b + 6.0D) - (b + 0.5D) * Math.log(b + 5.5D);
/*   35: 966 */     double series1 = 1.000000000190015D;double series2 = 1.000000000190015D;
/*   36: 967 */     for (int i = 0; i < 6; i++)
/*   37:     */     {
/*   38: 968 */       series1 += coef[i] / (b + 1.5D + i);
/*   39: 969 */       series2 += coef[i] / (b + 1.0D + i);
/*   40:     */     }
/*   41: 972 */     rt += Math.log(series1 * b) - Math.log(series2 * (b + 0.5D));
/*   42: 973 */     return rt;
/*   43:     */   }
/*   44:     */   
/*   45:     */   protected double diffFstDervLnGamma(double x)
/*   46:     */   {
/*   47: 983 */     double rt = 0.0D;double series = 1.0D;
/*   48: 984 */     for (int i = 0; series >= m_Zero * 0.001D; i++)
/*   49:     */     {
/*   50: 985 */       series = 0.5D / ((x + i) * (x + i + 0.5D));
/*   51: 986 */       rt += series;
/*   52:     */     }
/*   53: 988 */     return rt;
/*   54:     */   }
/*   55:     */   
/*   56:     */   protected double diffSndDervLnGamma(double x)
/*   57:     */   {
/*   58: 998 */     double rt = 0.0D;double series = 1.0D;
/*   59: 999 */     for (int i = 0; series >= m_Zero * 0.001D; i++)
/*   60:     */     {
/*   61:1000 */       series = (x + i + 0.25D) / ((x + i) * (x + i) * (x + i + 0.5D) * (x + i + 0.5D));
/*   62:     */       
/*   63:1002 */       rt -= series;
/*   64:     */     }
/*   65:1004 */     return rt;
/*   66:     */   }
/*   67:     */   
/*   68:     */   protected double objectiveFunction(double[] x)
/*   69:     */   {
/*   70:1012 */     int numExs = this.num.length;
/*   71:1013 */     double NLL = 0.0D;
/*   72:     */     
/*   73:1015 */     double a = x[0];double b = x[1];double w = x[2];double m = x[3];
/*   74:1016 */     for (int j = 0; j < numExs; j++) {
/*   75:1018 */       if (!Double.isNaN(this.xBar[j]))
/*   76:     */       {
/*   77:1022 */         NLL += 0.5D * (b + this.num[j]) * Math.log((1.0D + this.num[j] * w) * (a + this.sSq[j]) + this.num[j] * (this.xBar[j] - m) * (this.xBar[j] - m));
/*   78:1027 */         if ((Double.isNaN(NLL)) && (this.m_Debug))
/*   79:     */         {
/*   80:1028 */           System.err.println("???????????1: " + a + " " + b + " " + w + " " + m + "|x-: " + this.xBar[j] + "|n: " + this.num[j] + "|S^2: " + this.sSq[j]);
/*   81:     */           
/*   82:1030 */           System.exit(1);
/*   83:     */         }
/*   84:1036 */         NLL -= 0.5D * (b + this.num[j] - 1.0D) * Math.log(1.0D + this.num[j] * w);
/*   85:1038 */         if ((Double.isNaN(NLL)) && (this.m_Debug))
/*   86:     */         {
/*   87:1039 */           System.err.println("???????????2: " + a + " " + b + " " + w + " " + m + "|x-: " + this.xBar[j] + "|n: " + this.num[j] + "|S^2: " + this.sSq[j]);
/*   88:     */           
/*   89:1041 */           System.exit(1);
/*   90:     */         }
/*   91:1044 */         int halfNum = (int)this.num[j] / 2;
/*   92:1045 */         for (int z = 1; z <= halfNum; z++) {
/*   93:1046 */           NLL -= Math.log(0.5D * b + 0.5D * this.num[j] - z);
/*   94:     */         }
/*   95:1049 */         if (0.5D * this.num[j] > halfNum) {
/*   96:1050 */           NLL -= diffLnGamma(0.5D * b);
/*   97:     */         }
/*   98:1053 */         if ((Double.isNaN(NLL)) && (this.m_Debug))
/*   99:     */         {
/*  100:1054 */           System.err.println("???????????3: " + a + " " + b + " " + w + " " + m + "|x-: " + this.xBar[j] + "|n: " + this.num[j] + "|S^2: " + this.sSq[j]);
/*  101:     */           
/*  102:1056 */           System.exit(1);
/*  103:     */         }
/*  104:1059 */         NLL -= 0.5D * Math.log(a) * b;
/*  105:1060 */         if ((Double.isNaN(NLL)) && (this.m_Debug))
/*  106:     */         {
/*  107:1061 */           System.err.println("???????????4:" + a + " " + b + " " + w + " " + m);
/*  108:1062 */           System.exit(1);
/*  109:     */         }
/*  110:     */       }
/*  111:     */     }
/*  112:1065 */     if (this.m_Debug) {
/*  113:1066 */       System.err.println("?????????????5: " + NLL);
/*  114:     */     }
/*  115:1068 */     if (Double.isNaN(NLL)) {
/*  116:1069 */       System.exit(1);
/*  117:     */     }
/*  118:1072 */     return NLL;
/*  119:     */   }
/*  120:     */   
/*  121:     */   protected double[] evaluateGradient(double[] x)
/*  122:     */   {
/*  123:1081 */     double[] g = new double[x.length];
/*  124:1082 */     int numExs = this.num.length;
/*  125:     */     
/*  126:1084 */     double a = x[0];double b = x[1];double w = x[2];double m = x[3];
/*  127:     */     
/*  128:1086 */     double da = 0.0D;double db = 0.0D;double dw = 0.0D;double dm = 0.0D;
/*  129:1087 */     for (int j = 0; j < numExs; j++) {
/*  130:1089 */       if (!Double.isNaN(this.xBar[j]))
/*  131:     */       {
/*  132:1093 */         double denorm = (1.0D + this.num[j] * w) * (a + this.sSq[j]) + this.num[j] * (this.xBar[j] - m) * (this.xBar[j] - m);
/*  133:     */         
/*  134:     */ 
/*  135:1096 */         da += 0.5D * (b + this.num[j]) * (1.0D + this.num[j] * w) / denorm - 0.5D * b / a;
/*  136:     */         
/*  137:1098 */         db += 0.5D * Math.log(denorm) - 0.5D * Math.log(1.0D + this.num[j] * w) - 0.5D * Math.log(a);
/*  138:     */         
/*  139:     */ 
/*  140:1101 */         int halfNum = (int)this.num[j] / 2;
/*  141:1102 */         for (int z = 1; z <= halfNum; z++) {
/*  142:1103 */           db -= 1.0D / (b + this.num[j] - 2.0D * z);
/*  143:     */         }
/*  144:1105 */         if (this.num[j] / 2.0D > halfNum) {
/*  145:1106 */           db -= 0.5D * diffFstDervLnGamma(0.5D * b);
/*  146:     */         }
/*  147:1109 */         dw += 0.5D * (b + this.num[j]) * (a + this.sSq[j]) * this.num[j] / denorm - 0.5D * (b + this.num[j] - 1.0D) * this.num[j] / (1.0D + this.num[j] * w);
/*  148:     */         
/*  149:     */ 
/*  150:1112 */         dm += this.num[j] * (b + this.num[j]) * (m - this.xBar[j]) / denorm;
/*  151:     */       }
/*  152:     */     }
/*  153:1115 */     g[0] = da;
/*  154:1116 */     g[1] = db;
/*  155:1117 */     g[2] = dw;
/*  156:1118 */     g[3] = dm;
/*  157:1119 */     return g;
/*  158:     */   }
/*  159:     */   
/*  160:     */   protected double[] evaluateHessian(double[] x, int index)
/*  161:     */   {
/*  162:1128 */     double[] h = new double[x.length];
/*  163:     */     
/*  164:     */ 
/*  165:     */ 
/*  166:1132 */     int numExs = this.num.length;
/*  167:     */     double a;
/*  168:     */     double b;
/*  169:     */     double w;
/*  170:     */     double m;
/*  171:1135 */     switch (index)
/*  172:     */     {
/*  173:     */     case 0: 
/*  174:1137 */       a = x[0];
/*  175:1138 */       b = x[1];
/*  176:1139 */       w = x[2];
/*  177:1140 */       m = x[3];
/*  178:1142 */       for (int j = 0; j < numExs; j++) {
/*  179:1143 */         if (!Double.isNaN(this.xBar[j]))
/*  180:     */         {
/*  181:1146 */           double denorm = (1.0D + this.num[j] * w) * (a + this.sSq[j]) + this.num[j] * (this.xBar[j] - m) * (this.xBar[j] - m);
/*  182:     */           
/*  183:     */ 
/*  184:1149 */           h[0] += 0.5D * b / (a * a) - 0.5D * (b + this.num[j]) * (1.0D + this.num[j] * w) * (1.0D + this.num[j] * w) / (denorm * denorm);
/*  185:     */           
/*  186:     */ 
/*  187:1152 */           h[1] += 0.5D * (1.0D + this.num[j] * w) / denorm - 0.5D / a;
/*  188:     */           
/*  189:1154 */           h[2] += 0.5D * this.num[j] * this.num[j] * (b + this.num[j]) * (this.xBar[j] - m) * (this.xBar[j] - m) / (denorm * denorm);
/*  190:     */           
/*  191:     */ 
/*  192:1157 */           h[3] -= this.num[j] * (b + this.num[j]) * (m - this.xBar[j]) * (1.0D + this.num[j] * w) / (denorm * denorm);
/*  193:     */         }
/*  194:     */       }
/*  195:1160 */       break;
/*  196:     */     case 1: 
/*  197:1163 */       a = x[0];
/*  198:1164 */       b = x[1];
/*  199:1165 */       w = x[2];
/*  200:1166 */       m = x[3];
/*  201:1168 */       for (int j = 0; j < numExs; j++) {
/*  202:1169 */         if (!Double.isNaN(this.xBar[j]))
/*  203:     */         {
/*  204:1172 */           double denorm = (1.0D + this.num[j] * w) * (a + this.sSq[j]) + this.num[j] * (this.xBar[j] - m) * (this.xBar[j] - m);
/*  205:     */           
/*  206:     */ 
/*  207:1175 */           h[0] += 0.5D * (1.0D + this.num[j] * w) / denorm - 0.5D / a;
/*  208:     */           
/*  209:1177 */           int halfNum = (int)this.num[j] / 2;
/*  210:1178 */           for (int z = 1; z <= halfNum; z++) {
/*  211:1179 */             h[1] += 1.0D / ((b + this.num[j] - 2.0D * z) * (b + this.num[j] - 2.0D * z));
/*  212:     */           }
/*  213:1181 */           if (this.num[j] / 2.0D > halfNum) {
/*  214:1182 */             h[1] -= 0.25D * diffSndDervLnGamma(0.5D * b);
/*  215:     */           }
/*  216:1185 */           h[2] += 0.5D * (a + this.sSq[j]) * this.num[j] / denorm - 0.5D * this.num[j] / (1.0D + this.num[j] * w);
/*  217:     */           
/*  218:     */ 
/*  219:1188 */           h[3] += this.num[j] * (m - this.xBar[j]) / denorm;
/*  220:     */         }
/*  221:     */       }
/*  222:1190 */       break;
/*  223:     */     case 2: 
/*  224:1193 */       a = x[0];
/*  225:1194 */       b = x[1];
/*  226:1195 */       w = x[2];
/*  227:1196 */       m = x[3];
/*  228:1198 */       for (int j = 0; j < numExs; j++) {
/*  229:1199 */         if (!Double.isNaN(this.xBar[j]))
/*  230:     */         {
/*  231:1202 */           double denorm = (1.0D + this.num[j] * w) * (a + this.sSq[j]) + this.num[j] * (this.xBar[j] - m) * (this.xBar[j] - m);
/*  232:     */           
/*  233:     */ 
/*  234:1205 */           h[0] += 0.5D * this.num[j] * this.num[j] * (b + this.num[j]) * (this.xBar[j] - m) * (this.xBar[j] - m) / (denorm * denorm);
/*  235:     */           
/*  236:     */ 
/*  237:1208 */           h[1] += 0.5D * (a + this.sSq[j]) * this.num[j] / denorm - 0.5D * this.num[j] / (1.0D + this.num[j] * w);
/*  238:     */           
/*  239:     */ 
/*  240:1211 */           h[2] += 0.5D * (b + this.num[j] - 1.0D) * this.num[j] * this.num[j] / ((1.0D + this.num[j] * w) * (1.0D + this.num[j] * w)) - 0.5D * (b + this.num[j]) * (a + this.sSq[j]) * (a + this.sSq[j]) * this.num[j] * this.num[j] / (denorm * denorm);
/*  241:     */           
/*  242:     */ 
/*  243:     */ 
/*  244:1215 */           h[3] -= this.num[j] * this.num[j] * (b + this.num[j]) * (m - this.xBar[j]) * (a + this.sSq[j]) / (denorm * denorm);
/*  245:     */         }
/*  246:     */       }
/*  247:1218 */       break;
/*  248:     */     case 3: 
/*  249:1221 */       a = x[0];
/*  250:1222 */       b = x[1];
/*  251:1223 */       w = x[2];
/*  252:1224 */       m = x[3];
/*  253:1226 */       for (int j = 0; j < numExs; j++) {
/*  254:1227 */         if (!Double.isNaN(this.xBar[j]))
/*  255:     */         {
/*  256:1230 */           double denorm = (1.0D + this.num[j] * w) * (a + this.sSq[j]) + this.num[j] * (this.xBar[j] - m) * (this.xBar[j] - m);
/*  257:     */           
/*  258:     */ 
/*  259:1233 */           h[0] -= this.num[j] * (b + this.num[j]) * (m - this.xBar[j]) * (1.0D + this.num[j] * w) / (denorm * denorm);
/*  260:     */           
/*  261:     */ 
/*  262:1236 */           h[1] += this.num[j] * (m - this.xBar[j]) / denorm;
/*  263:     */           
/*  264:1238 */           h[2] -= this.num[j] * this.num[j] * (b + this.num[j]) * (m - this.xBar[j]) * (a + this.sSq[j]) / (denorm * denorm);
/*  265:     */           
/*  266:     */ 
/*  267:1241 */           h[3] += this.num[j] * (b + this.num[j]) * ((1.0D + this.num[j] * w) * (a + this.sSq[j]) - this.num[j] * (m - this.xBar[j]) * (m - this.xBar[j])) / (denorm * denorm);
/*  268:     */         }
/*  269:     */       }
/*  270:     */     }
/*  271:1248 */     return h;
/*  272:     */   }
/*  273:     */   
/*  274:     */   public String getRevision()
/*  275:     */   {
/*  276:1258 */     return RevisionUtils.extract("$Revision: 10369 $");
/*  277:     */   }
/*  278:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.TLD_Optm
 * JD-Core Version:    0.7.0.1
 */