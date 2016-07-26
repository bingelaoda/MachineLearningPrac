/*   1:    */ package weka.estimators;
/*   2:    */ 
/*   3:    */ import java.io.FileOutputStream;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.io.PrintWriter;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.RevisionHandler;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ 
/*  13:    */ public class EstimatorUtils
/*  14:    */   implements RevisionHandler
/*  15:    */ {
/*  16:    */   public static double findMinDistance(Instances inst, int attrIndex)
/*  17:    */   {
/*  18: 51 */     double min = 1.7976931348623157E+308D;
/*  19: 52 */     int numInst = inst.numInstances();
/*  20: 54 */     if (numInst < 2) {
/*  21: 55 */       return min;
/*  22:    */     }
/*  23: 57 */     int begin = -1;
/*  24: 58 */     Instance instance = null;
/*  25:    */     do
/*  26:    */     {
/*  27: 60 */       begin++;
/*  28: 61 */       if (begin < numInst) {
/*  29: 62 */         instance = inst.instance(begin);
/*  30:    */       }
/*  31: 64 */     } while ((begin < numInst) && (instance.isMissing(attrIndex)));
/*  32: 66 */     double secondValue = inst.instance(begin).value(attrIndex);
/*  33: 67 */     for (int i = begin; (i < numInst) && (!inst.instance(i).isMissing(attrIndex)); i++)
/*  34:    */     {
/*  35: 68 */       double firstValue = secondValue;
/*  36: 69 */       secondValue = inst.instance(i).value(attrIndex);
/*  37: 70 */       if (secondValue != firstValue)
/*  38:    */       {
/*  39: 71 */         double diff = secondValue - firstValue;
/*  40: 72 */         if ((diff < min) && (diff > 0.0D)) {
/*  41: 73 */           min = diff;
/*  42:    */         }
/*  43:    */       }
/*  44:    */     }
/*  45: 77 */     return min;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public static int getMinMax(Instances inst, int attrIndex, double[] minMax)
/*  49:    */     throws Exception
/*  50:    */   {
/*  51: 92 */     double min = (0.0D / 0.0D);
/*  52: 93 */     double max = (0.0D / 0.0D);
/*  53: 94 */     Instance instance = null;
/*  54: 95 */     int numNotMissing = 0;
/*  55: 96 */     if ((minMax == null) || (minMax.length < 2)) {
/*  56: 97 */       throw new Exception("Error in Program, privat method getMinMax");
/*  57:    */     }
/*  58:100 */     Enumeration<Instance> enumInst = inst.enumerateInstances();
/*  59:101 */     if (enumInst.hasMoreElements())
/*  60:    */     {
/*  61:    */       do
/*  62:    */       {
/*  63:103 */         instance = (Instance)enumInst.nextElement();
/*  64:104 */       } while ((instance.isMissing(attrIndex)) && (enumInst.hasMoreElements()));
/*  65:107 */       if (!instance.isMissing(attrIndex))
/*  66:    */       {
/*  67:108 */         numNotMissing++;
/*  68:109 */         min = instance.value(attrIndex);
/*  69:110 */         max = instance.value(attrIndex);
/*  70:    */       }
/*  71:112 */       while (enumInst.hasMoreElements())
/*  72:    */       {
/*  73:113 */         instance = (Instance)enumInst.nextElement();
/*  74:114 */         if (!instance.isMissing(attrIndex))
/*  75:    */         {
/*  76:115 */           numNotMissing++;
/*  77:116 */           if (instance.value(attrIndex) < min) {
/*  78:117 */             min = instance.value(attrIndex);
/*  79:119 */           } else if (instance.value(attrIndex) > max) {
/*  80:120 */             max = instance.value(attrIndex);
/*  81:    */           }
/*  82:    */         }
/*  83:    */       }
/*  84:    */     }
/*  85:126 */     minMax[0] = min;
/*  86:127 */     minMax[1] = max;
/*  87:128 */     return numNotMissing;
/*  88:    */   }
/*  89:    */   
/*  90:    */   public static Vector<Object> getInstancesFromClass(Instances data, int attrIndex, int classIndex, double classValue, Instances workData)
/*  91:    */   {
/*  92:143 */     Vector<Object> dataPlusInfo = new Vector(0);
/*  93:144 */     int num = 0;
/*  94:145 */     int numClassValue = 0;
/*  95:147 */     for (int i = 0; i < data.numInstances(); i++) {
/*  96:148 */       if (!data.instance(i).isMissing(attrIndex))
/*  97:    */       {
/*  98:149 */         num++;
/*  99:150 */         if (data.instance(i).value(classIndex) == classValue)
/* 100:    */         {
/* 101:151 */           workData.add(data.instance(i));
/* 102:152 */           numClassValue++;
/* 103:    */         }
/* 104:    */       }
/* 105:    */     }
/* 106:157 */     Double alphaFactor = new Double(numClassValue / num);
/* 107:158 */     dataPlusInfo.add(workData);
/* 108:159 */     dataPlusInfo.add(alphaFactor);
/* 109:160 */     return dataPlusInfo;
/* 110:    */   }
/* 111:    */   
/* 112:    */   public static Instances getInstancesFromClass(Instances data, int classIndex, double classValue)
/* 113:    */   {
/* 114:173 */     Instances workData = new Instances(data, 0);
/* 115:174 */     for (int i = 0; i < data.numInstances(); i++) {
/* 116:175 */       if (data.instance(i).value(classIndex) == classValue) {
/* 117:176 */         workData.add(data.instance(i));
/* 118:    */       }
/* 119:    */     }
/* 120:180 */     return workData;
/* 121:    */   }
/* 122:    */   
/* 123:    */   public static void writeCurve(String f, Estimator est, double min, double max, int numPoints)
/* 124:    */     throws Exception
/* 125:    */   {
/* 126:197 */     PrintWriter output = null;
/* 127:198 */     StringBuffer text = new StringBuffer("");
/* 128:200 */     if (f.length() != 0)
/* 129:    */     {
/* 130:202 */       String name = f + ".curv";
/* 131:203 */       output = new PrintWriter(new FileOutputStream(name));
/* 132:    */     }
/* 133:    */     else
/* 134:    */     {
/* 135:205 */       return;
/* 136:    */     }
/* 137:208 */     double diff = (max - min) / (numPoints - 1.0D);
/* 138:    */     try
/* 139:    */     {
/* 140:210 */       text.append("" + min + " " + est.getProbability(min) + " \n");
/* 141:212 */       for (double value = min + diff; value < max; value += diff) {
/* 142:213 */         text.append("" + value + " " + est.getProbability(value) + " \n");
/* 143:    */       }
/* 144:215 */       text.append("" + max + " " + est.getProbability(max) + " \n");
/* 145:    */     }
/* 146:    */     catch (Exception ex)
/* 147:    */     {
/* 148:217 */       ex.printStackTrace();
/* 149:218 */       System.out.println(ex.getMessage());
/* 150:    */     }
/* 151:220 */     output.println(text.toString());
/* 152:223 */     if (output != null) {
/* 153:224 */       output.close();
/* 154:    */     }
/* 155:    */   }
/* 156:    */   
/* 157:    */   public static void writeCurve(String f, Estimator est, Estimator classEst, double classIndex, double min, double max, int numPoints)
/* 158:    */     throws Exception
/* 159:    */   {
/* 160:244 */     PrintWriter output = null;
/* 161:245 */     StringBuffer text = new StringBuffer("");
/* 162:247 */     if (f.length() != 0)
/* 163:    */     {
/* 164:249 */       String name = f + ".curv";
/* 165:250 */       output = new PrintWriter(new FileOutputStream(name));
/* 166:    */     }
/* 167:    */     else
/* 168:    */     {
/* 169:252 */       return;
/* 170:    */     }
/* 171:255 */     double diff = (max - min) / (numPoints - 1.0D);
/* 172:    */     try
/* 173:    */     {
/* 174:257 */       text.append("" + min + " " + est.getProbability(min) * classEst.getProbability(classIndex) + " \n");
/* 175:260 */       for (double value = min + diff; value < max; value += diff) {
/* 176:261 */         text.append("" + value + " " + est.getProbability(value) * classEst.getProbability(classIndex) + " \n");
/* 177:    */       }
/* 178:264 */       text.append("" + max + " " + est.getProbability(max) * classEst.getProbability(classIndex) + " \n");
/* 179:    */     }
/* 180:    */     catch (Exception ex)
/* 181:    */     {
/* 182:267 */       ex.printStackTrace();
/* 183:268 */       System.out.println(ex.getMessage());
/* 184:    */     }
/* 185:270 */     output.println(text.toString());
/* 186:273 */     if (output != null) {
/* 187:274 */       output.close();
/* 188:    */     }
/* 189:    */   }
/* 190:    */   
/* 191:    */   public static Instances getInstancesFromValue(Instances data, int index, double v)
/* 192:    */   {
/* 193:289 */     Instances workData = new Instances(data, 0);
/* 194:290 */     for (int i = 0; i < data.numInstances(); i++) {
/* 195:291 */       if (data.instance(i).value(index) == v) {
/* 196:292 */         workData.add(data.instance(i));
/* 197:    */       }
/* 198:    */     }
/* 199:295 */     return workData;
/* 200:    */   }
/* 201:    */   
/* 202:    */   public static String cutpointsToString(double[] cutPoints, boolean[] cutAndLeft)
/* 203:    */   {
/* 204:303 */     StringBuffer text = new StringBuffer("");
/* 205:304 */     if (cutPoints == null)
/* 206:    */     {
/* 207:305 */       text.append("\n# no cutpoints found - attribute \n");
/* 208:    */     }
/* 209:    */     else
/* 210:    */     {
/* 211:307 */       text.append("\n#* " + cutPoints.length + " cutpoint(s) -\n");
/* 212:308 */       for (int i = 0; i < cutPoints.length; i++)
/* 213:    */       {
/* 214:309 */         text.append("# " + cutPoints[i] + " ");
/* 215:310 */         text.append("" + cutAndLeft[i] + "\n");
/* 216:    */       }
/* 217:312 */       text.append("# end\n");
/* 218:    */     }
/* 219:314 */     return text.toString();
/* 220:    */   }
/* 221:    */   
/* 222:    */   public String getRevision()
/* 223:    */   {
/* 224:324 */     return RevisionUtils.extract("$Revision: 10153 $");
/* 225:    */   }
/* 226:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.estimators.EstimatorUtils
 * JD-Core Version:    0.7.0.1
 */