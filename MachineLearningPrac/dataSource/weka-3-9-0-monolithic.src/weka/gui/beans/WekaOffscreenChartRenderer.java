/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.Graphics;
/*   4:    */ import java.awt.image.BufferedImage;
/*   5:    */ import java.util.List;
/*   6:    */ import weka.core.Instance;
/*   7:    */ import weka.core.Instances;
/*   8:    */ import weka.gui.AttributeVisualizationPanel;
/*   9:    */ import weka.gui.visualize.Plot2D;
/*  10:    */ import weka.gui.visualize.PlotData2D;
/*  11:    */ 
/*  12:    */ public class WekaOffscreenChartRenderer
/*  13:    */   extends AbstractOffscreenChartRenderer
/*  14:    */ {
/*  15:    */   public String rendererName()
/*  16:    */   {
/*  17: 48 */     return "Weka Chart Renderer";
/*  18:    */   }
/*  19:    */   
/*  20:    */   public String optionsTipTextHTML()
/*  21:    */   {
/*  22: 58 */     return "<html><ul><li>-title=[chart title]</li><li>-color=[coloring/class attribute name]</li></html>";
/*  23:    */   }
/*  24:    */   
/*  25:    */   public BufferedImage renderXYLineChart(int width, int height, List<Instances> series, String xAxis, String yAxis, List<String> optionalArgs)
/*  26:    */     throws Exception
/*  27:    */   {
/*  28: 80 */     BufferedImage osi = new BufferedImage(width, height, 1);
/*  29:    */     
/*  30:    */ 
/*  31: 83 */     String plotTitle = "Line chart";
/*  32: 84 */     String userTitle = getOption(optionalArgs, "-title");
/*  33: 85 */     plotTitle = userTitle != null ? userTitle : plotTitle;
/*  34:    */     
/*  35: 87 */     Plot2D offScreenPlot = new Plot2D();
/*  36: 88 */     offScreenPlot.setSize(width, height);
/*  37:    */     
/*  38:    */ 
/*  39: 91 */     PlotData2D master = new PlotData2D((Instances)series.get(0));
/*  40: 92 */     master.setPlotName(plotTitle);
/*  41: 93 */     boolean[] connectPoints = new boolean[((Instances)series.get(0)).numInstances()];
/*  42: 94 */     for (int i = 0; i < connectPoints.length; i++) {
/*  43: 95 */       connectPoints[i] = true;
/*  44:    */     }
/*  45: 97 */     master.setConnectPoints(connectPoints);
/*  46: 98 */     offScreenPlot.setMasterPlot(master);
/*  47:    */     
/*  48:100 */     Instances masterInstances = (Instances)series.get(0);
/*  49:101 */     int xAx = getIndexOfAttribute(masterInstances, xAxis);
/*  50:102 */     int yAx = getIndexOfAttribute(masterInstances, yAxis);
/*  51:103 */     if (xAx < 0) {
/*  52:104 */       xAx = 0;
/*  53:    */     }
/*  54:106 */     if (yAx < 0) {
/*  55:107 */       yAx = 0;
/*  56:    */     }
/*  57:111 */     offScreenPlot.setXindex(xAx);
/*  58:112 */     offScreenPlot.setYindex(yAx);
/*  59:113 */     offScreenPlot.setCindex(masterInstances.numAttributes() - 1);
/*  60:114 */     String colorAtt = getOption(optionalArgs, "-color");
/*  61:115 */     int tempC = getIndexOfAttribute(masterInstances, colorAtt);
/*  62:116 */     if (tempC >= 0) {
/*  63:117 */       offScreenPlot.setCindex(tempC);
/*  64:    */     }
/*  65:121 */     if (series.size() > 1) {
/*  66:122 */       for (Instances plotI : series)
/*  67:    */       {
/*  68:123 */         PlotData2D plotD = new PlotData2D(plotI);
/*  69:124 */         connectPoints = new boolean[plotI.numInstances()];
/*  70:125 */         for (int i = 0; i < connectPoints.length; i++) {
/*  71:126 */           connectPoints[i] = true;
/*  72:    */         }
/*  73:128 */         plotD.setConnectPoints(connectPoints);
/*  74:129 */         offScreenPlot.addPlot(plotD);
/*  75:    */       }
/*  76:    */     }
/*  77:134 */     Graphics g = osi.getGraphics();
/*  78:135 */     offScreenPlot.paintComponent(g);
/*  79:    */     
/*  80:137 */     return osi;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public BufferedImage renderXYScatterPlot(int width, int height, List<Instances> series, String xAxis, String yAxis, List<String> optionalArgs)
/*  84:    */     throws Exception
/*  85:    */   {
/*  86:159 */     BufferedImage osi = new BufferedImage(width, height, 1);
/*  87:    */     
/*  88:    */ 
/*  89:162 */     String plotTitle = "Scatter plot";
/*  90:163 */     String userTitle = getOption(optionalArgs, "-title");
/*  91:164 */     plotTitle = userTitle != null ? userTitle : plotTitle;
/*  92:    */     
/*  93:166 */     Plot2D offScreenPlot = new Plot2D();
/*  94:167 */     offScreenPlot.setSize(width, height);
/*  95:    */     
/*  96:    */ 
/*  97:170 */     PlotData2D master = new PlotData2D((Instances)series.get(0));
/*  98:171 */     master.setPlotName(plotTitle);
/*  99:172 */     master.m_displayAllPoints = true;
/* 100:    */     
/* 101:174 */     offScreenPlot.setMasterPlot(master);
/* 102:    */     
/* 103:176 */     Instances masterInstances = (Instances)series.get(0);
/* 104:177 */     int xAx = getIndexOfAttribute(masterInstances, xAxis);
/* 105:178 */     int yAx = getIndexOfAttribute(masterInstances, yAxis);
/* 106:179 */     if (xAx < 0) {
/* 107:180 */       xAx = 0;
/* 108:    */     }
/* 109:182 */     if (yAx < 0) {
/* 110:183 */       yAx = 0;
/* 111:    */     }
/* 112:187 */     offScreenPlot.setXindex(xAx);
/* 113:188 */     offScreenPlot.setYindex(yAx);
/* 114:189 */     offScreenPlot.setCindex(masterInstances.numAttributes() - 1);
/* 115:190 */     String colorAtt = getOption(optionalArgs, "-color");
/* 116:191 */     int tempC = getIndexOfAttribute(masterInstances, colorAtt);
/* 117:192 */     if (tempC >= 0) {
/* 118:193 */       offScreenPlot.setCindex(tempC);
/* 119:    */     }
/* 120:196 */     String hasErrors = getOption(optionalArgs, "-hasErrors");
/* 121:198 */     if (hasErrors != null)
/* 122:    */     {
/* 123:199 */       int[] plotShapes = new int[masterInstances.numInstances()];
/* 124:200 */       for (int i = 0; i < plotShapes.length; i++) {
/* 125:201 */         plotShapes[i] = 1000;
/* 126:    */       }
/* 127:203 */       master.setShapeType(plotShapes);
/* 128:    */     }
/* 129:208 */     String shapeSize = getOption(optionalArgs, "-shapeSize");
/* 130:209 */     if ((shapeSize != null) && (shapeSize.length() > 0))
/* 131:    */     {
/* 132:210 */       int shapeSizeI = getIndexOfAttribute(masterInstances, shapeSize);
/* 133:212 */       if (shapeSizeI >= 0)
/* 134:    */       {
/* 135:213 */         int[] plotSizes = new int[masterInstances.numInstances()];
/* 136:214 */         for (int i = 0; i < masterInstances.numInstances(); i++) {
/* 137:215 */           plotSizes[i] = ((int)masterInstances.instance(i).value(shapeSizeI));
/* 138:    */         }
/* 139:217 */         master.setShapeSize(plotSizes);
/* 140:    */       }
/* 141:    */     }
/* 142:222 */     if (series.size() > 1) {
/* 143:223 */       for (Instances plotI : series)
/* 144:    */       {
/* 145:224 */         PlotData2D plotD = new PlotData2D(plotI);
/* 146:225 */         plotD.m_displayAllPoints = true;
/* 147:    */         
/* 148:227 */         offScreenPlot.addPlot(plotD);
/* 149:229 */         if ((shapeSize != null) && (shapeSize.length() > 0))
/* 150:    */         {
/* 151:230 */           int shapeSizeI = getIndexOfAttribute(plotI, shapeSize);
/* 152:231 */           if (shapeSizeI >= 0)
/* 153:    */           {
/* 154:232 */             int[] plotSizes = new int[plotI.numInstances()];
/* 155:233 */             for (int i = 0; i < plotI.numInstances(); i++) {
/* 156:234 */               plotSizes[i] = ((int)plotI.instance(i).value(shapeSizeI));
/* 157:    */             }
/* 158:236 */             plotD.setShapeSize(plotSizes);
/* 159:    */           }
/* 160:    */         }
/* 161:241 */         if (hasErrors != null)
/* 162:    */         {
/* 163:242 */           int[] plotShapes = new int[plotI.numInstances()];
/* 164:243 */           for (int i = 0; i < plotShapes.length; i++) {
/* 165:244 */             plotShapes[i] = 0;
/* 166:    */           }
/* 167:246 */           plotD.setShapeType(plotShapes);
/* 168:    */         }
/* 169:    */       }
/* 170:    */     }
/* 171:252 */     Graphics g = osi.getGraphics();
/* 172:253 */     offScreenPlot.paintComponent(g);
/* 173:    */     
/* 174:255 */     return osi;
/* 175:    */   }
/* 176:    */   
/* 177:    */   public BufferedImage renderHistogram(int width, int height, List<Instances> series, String attToPlot, List<String> optionalArgs)
/* 178:    */     throws Exception
/* 179:    */   {
/* 180:282 */     BufferedImage osi = new BufferedImage(width, height, 1);
/* 181:    */     
/* 182:    */ 
/* 183:    */ 
/* 184:286 */     Instances toPlot = new Instances((Instances)series.get(0));
/* 185:287 */     for (int i = 1; i < series.size(); i++)
/* 186:    */     {
/* 187:288 */       Instances additional = (Instances)series.get(i);
/* 188:289 */       for (Instance temp : additional) {
/* 189:290 */         toPlot.add(temp);
/* 190:    */       }
/* 191:    */     }
/* 192:294 */     int attIndex = getIndexOfAttribute(toPlot, attToPlot);
/* 193:295 */     if (attIndex < 0) {
/* 194:296 */       attIndex = 0;
/* 195:    */     }
/* 196:299 */     String colorAtt = getOption(optionalArgs, "-color");
/* 197:300 */     int tempC = getIndexOfAttribute(toPlot, colorAtt);
/* 198:    */     
/* 199:302 */     AttributeVisualizationPanel offScreenPlot = new AttributeVisualizationPanel();
/* 200:    */     
/* 201:304 */     offScreenPlot.setSize(width, height);
/* 202:305 */     offScreenPlot.setInstances(toPlot);
/* 203:306 */     offScreenPlot.setAttribute(attIndex);
/* 204:307 */     if (tempC >= 0) {
/* 205:308 */       offScreenPlot.setColoringIndex(tempC);
/* 206:    */     }
/* 207:312 */     Graphics g = osi.getGraphics();
/* 208:313 */     offScreenPlot.paintComponent(g);
/* 209:    */     
/* 210:315 */     Thread.sleep(2000L);
/* 211:316 */     offScreenPlot.paintComponent(g);
/* 212:    */     
/* 213:    */ 
/* 214:    */ 
/* 215:320 */     return osi;
/* 216:    */   }
/* 217:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.WekaOffscreenChartRenderer
 * JD-Core Version:    0.7.0.1
 */