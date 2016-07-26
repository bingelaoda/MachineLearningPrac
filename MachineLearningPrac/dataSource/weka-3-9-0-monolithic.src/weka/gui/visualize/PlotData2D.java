/*   1:    */ package weka.gui.visualize;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import weka.core.Attribute;
/*   7:    */ import weka.core.Instance;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.filters.Filter;
/*  10:    */ import weka.filters.unsupervised.attribute.Add;
/*  11:    */ 
/*  12:    */ public class PlotData2D
/*  13:    */   implements Serializable
/*  14:    */ {
/*  15:    */   private static final long serialVersionUID = -3979972167982697979L;
/*  16: 48 */   protected Instances m_plotInstances = null;
/*  17: 51 */   protected String m_plotName = "new plot";
/*  18: 57 */   protected String m_plotNameHTML = null;
/*  19: 60 */   public boolean m_useCustomColour = false;
/*  20: 61 */   public Color m_customColour = null;
/*  21: 64 */   public boolean m_displayAllPoints = false;
/*  22: 70 */   public int m_alwaysDisplayPointsOfThisSize = -1;
/*  23:    */   protected double[][] m_pointLookup;
/*  24:    */   protected int[] m_shapeSize;
/*  25:    */   protected int[] m_shapeType;
/*  26:    */   protected boolean[] m_connectPoints;
/*  27:    */   private int m_xIndex;
/*  28:    */   private int m_yIndex;
/*  29:    */   private int m_cIndex;
/*  30:    */   protected double m_maxX;
/*  31:    */   protected double m_minX;
/*  32:    */   protected double m_maxY;
/*  33:    */   protected double m_minY;
/*  34:    */   protected double m_maxC;
/*  35:    */   protected double m_minC;
/*  36:    */   
/*  37:    */   public PlotData2D(Instances insts)
/*  38:    */   {
/*  39:124 */     this.m_plotInstances = insts;
/*  40:125 */     this.m_xIndex = (this.m_yIndex = this.m_cIndex = 0);
/*  41:126 */     this.m_pointLookup = new double[this.m_plotInstances.numInstances()][4];
/*  42:127 */     this.m_shapeSize = new int[this.m_plotInstances.numInstances()];
/*  43:128 */     this.m_shapeType = new int[this.m_plotInstances.numInstances()];
/*  44:129 */     this.m_connectPoints = new boolean[this.m_plotInstances.numInstances()];
/*  45:130 */     for (int i = 0; i < this.m_plotInstances.numInstances(); i++)
/*  46:    */     {
/*  47:131 */       this.m_shapeSize[i] = 2;
/*  48:132 */       this.m_shapeType[i] = -1;
/*  49:    */     }
/*  50:135 */     determineBounds();
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void addInstanceNumberAttribute()
/*  54:    */   {
/*  55:142 */     String originalRelationName = this.m_plotInstances.relationName();
/*  56:143 */     int originalClassIndex = this.m_plotInstances.classIndex();
/*  57:    */     try
/*  58:    */     {
/*  59:145 */       Add addF = new Add();
/*  60:146 */       addF.setAttributeName("Instance_number");
/*  61:147 */       addF.setAttributeIndex("first");
/*  62:148 */       addF.setInputFormat(this.m_plotInstances);
/*  63:149 */       this.m_plotInstances = Filter.useFilter(this.m_plotInstances, addF);
/*  64:150 */       this.m_plotInstances.setClassIndex(originalClassIndex + 1);
/*  65:151 */       for (int i = 0; i < this.m_plotInstances.numInstances(); i++) {
/*  66:152 */         this.m_plotInstances.instance(i).setValue(0, i);
/*  67:    */       }
/*  68:154 */       this.m_plotInstances.setRelationName(originalRelationName);
/*  69:    */     }
/*  70:    */     catch (Exception ex)
/*  71:    */     {
/*  72:156 */       ex.printStackTrace();
/*  73:    */     }
/*  74:    */   }
/*  75:    */   
/*  76:    */   public Instances getPlotInstances()
/*  77:    */   {
/*  78:166 */     return new Instances(this.m_plotInstances);
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void setPlotName(String name)
/*  82:    */   {
/*  83:175 */     this.m_plotName = name;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public String getPlotName()
/*  87:    */   {
/*  88:184 */     return this.m_plotName;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public void setPlotNameHTML(String name)
/*  92:    */   {
/*  93:194 */     this.m_plotNameHTML = name;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public String getPlotNameHTML()
/*  97:    */   {
/*  98:205 */     if (this.m_plotNameHTML == null) {
/*  99:206 */       return this.m_plotName;
/* 100:    */     }
/* 101:209 */     return this.m_plotNameHTML;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public void setShapeType(int[] st)
/* 105:    */     throws Exception
/* 106:    */   {
/* 107:219 */     this.m_shapeType = st;
/* 108:220 */     if (this.m_shapeType.length != this.m_plotInstances.numInstances()) {
/* 109:221 */       throw new Exception("PlotData2D: Shape type array must have the same number of entries as number of data points!");
/* 110:    */     }
/* 111:    */   }
/* 112:    */   
/* 113:    */   public int[] getShapeType()
/* 114:    */   {
/* 115:236 */     return this.m_shapeType;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public void setShapeType(ArrayList<Integer> st)
/* 119:    */     throws Exception
/* 120:    */   {
/* 121:246 */     if (st.size() != this.m_plotInstances.numInstances()) {
/* 122:247 */       throw new Exception("PlotData2D: Shape type vector must have the same number of entries as number of data points!");
/* 123:    */     }
/* 124:250 */     this.m_shapeType = new int[st.size()];
/* 125:251 */     for (int i = 0; i < st.size(); i++) {
/* 126:252 */       this.m_shapeType[i] = ((Integer)st.get(i)).intValue();
/* 127:    */     }
/* 128:    */   }
/* 129:    */   
/* 130:    */   public void setShapeSize(int[] ss)
/* 131:    */     throws Exception
/* 132:    */   {
/* 133:265 */     this.m_shapeSize = ss;
/* 134:266 */     if (this.m_shapeType.length != this.m_plotInstances.numInstances()) {
/* 135:267 */       throw new Exception("PlotData2D: Shape size array must have the same number of entries as number of data points!");
/* 136:    */     }
/* 137:    */   }
/* 138:    */   
/* 139:    */   public int[] getShapeSize()
/* 140:    */   {
/* 141:278 */     return this.m_shapeSize;
/* 142:    */   }
/* 143:    */   
/* 144:    */   public void setShapeSize(ArrayList<Object> ss)
/* 145:    */     throws Exception
/* 146:    */   {
/* 147:287 */     if (ss.size() != this.m_plotInstances.numInstances()) {
/* 148:288 */       throw new Exception("PlotData2D: Shape size vector must have the same number of entries as number of data points!");
/* 149:    */     }
/* 150:292 */     this.m_shapeSize = new int[ss.size()];
/* 151:293 */     for (int i = 0; i < ss.size(); i++) {
/* 152:294 */       this.m_shapeSize[i] = ((Integer)ss.get(i)).intValue();
/* 153:    */     }
/* 154:    */   }
/* 155:    */   
/* 156:    */   public void setConnectPoints(boolean[] cp)
/* 157:    */     throws Exception
/* 158:    */   {
/* 159:305 */     this.m_connectPoints = cp;
/* 160:306 */     if (this.m_connectPoints.length != this.m_plotInstances.numInstances()) {
/* 161:307 */       throw new Exception("PlotData2D: connect points array must have the same number of entries as number of data points!");
/* 162:    */     }
/* 163:310 */     this.m_connectPoints[0] = false;
/* 164:    */   }
/* 165:    */   
/* 166:    */   public void setConnectPoints(ArrayList<Boolean> cp)
/* 167:    */     throws Exception
/* 168:    */   {
/* 169:320 */     if (cp.size() != this.m_plotInstances.numInstances()) {
/* 170:321 */       throw new Exception("PlotData2D: connect points array must have the same number of entries as number of data points!");
/* 171:    */     }
/* 172:325 */     this.m_shapeSize = new int[cp.size()];
/* 173:326 */     for (int i = 0; i < cp.size(); i++) {
/* 174:327 */       this.m_connectPoints[i] = ((Boolean)cp.get(i)).booleanValue();
/* 175:    */     }
/* 176:329 */     this.m_connectPoints[0] = false;
/* 177:    */   }
/* 178:    */   
/* 179:    */   public void setCustomColour(Color c)
/* 180:    */   {
/* 181:341 */     this.m_customColour = c;
/* 182:342 */     if (c != null) {
/* 183:343 */       this.m_useCustomColour = true;
/* 184:    */     } else {
/* 185:345 */       this.m_useCustomColour = false;
/* 186:    */     }
/* 187:    */   }
/* 188:    */   
/* 189:    */   public void setXindex(int x)
/* 190:    */   {
/* 191:355 */     this.m_xIndex = x;
/* 192:356 */     determineBounds();
/* 193:    */   }
/* 194:    */   
/* 195:    */   public void setYindex(int y)
/* 196:    */   {
/* 197:365 */     this.m_yIndex = y;
/* 198:366 */     determineBounds();
/* 199:    */   }
/* 200:    */   
/* 201:    */   public void setCindex(int c)
/* 202:    */   {
/* 203:375 */     this.m_cIndex = c;
/* 204:376 */     determineBounds();
/* 205:    */   }
/* 206:    */   
/* 207:    */   public int getXindex()
/* 208:    */   {
/* 209:385 */     return this.m_xIndex;
/* 210:    */   }
/* 211:    */   
/* 212:    */   public int getYindex()
/* 213:    */   {
/* 214:394 */     return this.m_yIndex;
/* 215:    */   }
/* 216:    */   
/* 217:    */   public int getCindex()
/* 218:    */   {
/* 219:403 */     return this.m_cIndex;
/* 220:    */   }
/* 221:    */   
/* 222:    */   private void determineBounds()
/* 223:    */   {
/* 224:412 */     if ((this.m_plotInstances != null) && (this.m_plotInstances.numAttributes() > 0) && (this.m_plotInstances.numInstances() > 0))
/* 225:    */     {
/* 226:415 */       double min = (1.0D / 0.0D);
/* 227:416 */       double max = (-1.0D / 0.0D);
/* 228:417 */       if (this.m_plotInstances.attribute(this.m_xIndex).isNominal())
/* 229:    */       {
/* 230:418 */         this.m_minX = 0.0D;
/* 231:419 */         this.m_maxX = (this.m_plotInstances.attribute(this.m_xIndex).numValues() - 1);
/* 232:    */       }
/* 233:    */       else
/* 234:    */       {
/* 235:421 */         for (int i = 0; i < this.m_plotInstances.numInstances(); i++) {
/* 236:422 */           if (!this.m_plotInstances.instance(i).isMissing(this.m_xIndex))
/* 237:    */           {
/* 238:423 */             double value = this.m_plotInstances.instance(i).value(this.m_xIndex);
/* 239:424 */             if (value < min) {
/* 240:425 */               min = value;
/* 241:    */             }
/* 242:427 */             if (value > max) {
/* 243:428 */               max = value;
/* 244:    */             }
/* 245:    */           }
/* 246:    */         }
/* 247:434 */         if (min == (1.0D / 0.0D)) {
/* 248:435 */           min = max = 0.0D;
/* 249:    */         }
/* 250:438 */         this.m_minX = min;
/* 251:439 */         this.m_maxX = max;
/* 252:440 */         if (min == max)
/* 253:    */         {
/* 254:441 */           this.m_maxX += 0.05D;
/* 255:442 */           this.m_minX -= 0.05D;
/* 256:    */         }
/* 257:    */       }
/* 258:447 */       min = (1.0D / 0.0D);
/* 259:448 */       max = (-1.0D / 0.0D);
/* 260:449 */       if (this.m_plotInstances.attribute(this.m_yIndex).isNominal())
/* 261:    */       {
/* 262:450 */         this.m_minY = 0.0D;
/* 263:451 */         this.m_maxY = (this.m_plotInstances.attribute(this.m_yIndex).numValues() - 1);
/* 264:    */       }
/* 265:    */       else
/* 266:    */       {
/* 267:453 */         for (int i = 0; i < this.m_plotInstances.numInstances(); i++) {
/* 268:454 */           if (!this.m_plotInstances.instance(i).isMissing(this.m_yIndex))
/* 269:    */           {
/* 270:455 */             double value = this.m_plotInstances.instance(i).value(this.m_yIndex);
/* 271:456 */             if (value < min) {
/* 272:457 */               min = value;
/* 273:    */             }
/* 274:459 */             if (value > max) {
/* 275:460 */               max = value;
/* 276:    */             }
/* 277:    */           }
/* 278:    */         }
/* 279:466 */         if (min == (1.0D / 0.0D)) {
/* 280:467 */           min = max = 0.0D;
/* 281:    */         }
/* 282:470 */         this.m_minY = min;
/* 283:471 */         this.m_maxY = max;
/* 284:472 */         if (min == max)
/* 285:    */         {
/* 286:473 */           this.m_maxY += 0.05D;
/* 287:474 */           this.m_minY -= 0.05D;
/* 288:    */         }
/* 289:    */       }
/* 290:479 */       min = (1.0D / 0.0D);
/* 291:480 */       max = (-1.0D / 0.0D);
/* 292:482 */       for (int i = 0; i < this.m_plotInstances.numInstances(); i++) {
/* 293:483 */         if (!this.m_plotInstances.instance(i).isMissing(this.m_cIndex))
/* 294:    */         {
/* 295:484 */           double value = this.m_plotInstances.instance(i).value(this.m_cIndex);
/* 296:485 */           if (value < min) {
/* 297:486 */             min = value;
/* 298:    */           }
/* 299:488 */           if (value > max) {
/* 300:489 */             max = value;
/* 301:    */           }
/* 302:    */         }
/* 303:    */       }
/* 304:495 */       if (min == (1.0D / 0.0D)) {
/* 305:496 */         min = max = 0.0D;
/* 306:    */       }
/* 307:499 */       this.m_minC = min;
/* 308:500 */       this.m_maxC = max;
/* 309:    */     }
/* 310:    */   }
/* 311:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.visualize.PlotData2D
 * JD-Core Version:    0.7.0.1
 */