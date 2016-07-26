/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.awt.image.BufferedImage;
/*   4:    */ import java.text.SimpleDateFormat;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Arrays;
/*   7:    */ import java.util.Date;
/*   8:    */ import java.util.LinkedHashMap;
/*   9:    */ import java.util.List;
/*  10:    */ import java.util.Map;
/*  11:    */ import weka.core.Attribute;
/*  12:    */ import weka.core.Instance;
/*  13:    */ import weka.core.Instances;
/*  14:    */ import weka.core.OptionMetadata;
/*  15:    */ import weka.core.PluginManager;
/*  16:    */ import weka.core.WekaException;
/*  17:    */ import weka.gui.ProgrammaticProperty;
/*  18:    */ import weka.gui.beans.OffscreenChartRenderer;
/*  19:    */ import weka.gui.beans.WekaOffscreenChartRenderer;
/*  20:    */ import weka.gui.visualize.PlotData2D;
/*  21:    */ import weka.knowledgeflow.Data;
/*  22:    */ import weka.knowledgeflow.StepManager;
/*  23:    */ 
/*  24:    */ @KFStep(name="DataVisualizer", category="Visualization", toolTipText="Visualize training/test sets in a 2D scatter plot.", iconPath="weka/gui/knowledgeflow/icons/DefaultDataVisualizer.gif")
/*  25:    */ public class DataVisualizer
/*  26:    */   extends BaseStep
/*  27:    */   implements DataCollector
/*  28:    */ {
/*  29:    */   private static final long serialVersionUID = -8013077913672918384L;
/*  30: 61 */   protected List<PlotData2D> m_plots = new ArrayList();
/*  31:    */   protected transient OffscreenChartRenderer m_offscreenRenderer;
/*  32: 66 */   protected String m_offscreenRendererName = "Weka Chart Renderer";
/*  33: 72 */   protected String m_xAxis = "";
/*  34: 78 */   protected String m_yAxis = "";
/*  35: 83 */   protected String m_additionalOptions = "";
/*  36: 86 */   protected String m_width = "500";
/*  37: 89 */   protected String m_height = "400";
/*  38:    */   
/*  39:    */   @OptionMetadata(displayName="X-axis attribute", description="Attribute name or /first, /last or /<index>", displayOrder=1)
/*  40:    */   public void setOffscreenXAxis(String xAxis)
/*  41:    */   {
/*  42:101 */     this.m_xAxis = xAxis;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public String getOffscreenXAxis()
/*  46:    */   {
/*  47:110 */     return this.m_xAxis;
/*  48:    */   }
/*  49:    */   
/*  50:    */   @OptionMetadata(displayName="Y-axis attribute", description="Attribute name or /first, /last or /<index>", displayOrder=2)
/*  51:    */   public void setOffscreenYAxis(String yAxis)
/*  52:    */   {
/*  53:123 */     this.m_yAxis = yAxis;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public String getOffscreenYAxis()
/*  57:    */   {
/*  58:132 */     return this.m_yAxis;
/*  59:    */   }
/*  60:    */   
/*  61:    */   @OptionMetadata(displayName="Chart width (pixels)", description="Width of the rendered chart", displayOrder=3)
/*  62:    */   public void setOffscreenWidth(String width)
/*  63:    */   {
/*  64:143 */     this.m_width = width;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public String getOffscreenWidth()
/*  68:    */   {
/*  69:152 */     return this.m_width;
/*  70:    */   }
/*  71:    */   
/*  72:    */   @OptionMetadata(displayName="Chart height (pixels)", description="Height of the rendered chart", displayOrder=4)
/*  73:    */   public void setOffscreenHeight(String height)
/*  74:    */   {
/*  75:163 */     this.m_height = height;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public String getOffscreenHeight()
/*  79:    */   {
/*  80:172 */     return this.m_height;
/*  81:    */   }
/*  82:    */   
/*  83:    */   @ProgrammaticProperty
/*  84:    */   public void setOffscreenRendererName(String rendererName)
/*  85:    */   {
/*  86:183 */     this.m_offscreenRendererName = rendererName;
/*  87:184 */     this.m_offscreenRenderer = null;
/*  88:    */   }
/*  89:    */   
/*  90:    */   public String getOffscreenRendererName()
/*  91:    */   {
/*  92:194 */     return this.m_offscreenRendererName;
/*  93:    */   }
/*  94:    */   
/*  95:    */   @ProgrammaticProperty
/*  96:    */   public void setOffscreenAdditionalOpts(String additional)
/*  97:    */   {
/*  98:204 */     this.m_additionalOptions = additional;
/*  99:    */   }
/* 100:    */   
/* 101:    */   public String getOffscreenAdditionalOpts()
/* 102:    */   {
/* 103:213 */     return this.m_additionalOptions;
/* 104:    */   }
/* 105:    */   
/* 106:    */   protected void setupOffscreenRenderer()
/* 107:    */   {
/* 108:220 */     getStepManager().logDetailed("Initializing offscreen renderer: " + getOffscreenRendererName());
/* 109:222 */     if (this.m_offscreenRenderer == null)
/* 110:    */     {
/* 111:223 */       if ((this.m_offscreenRendererName == null) || (this.m_offscreenRendererName.length() == 0))
/* 112:    */       {
/* 113:225 */         this.m_offscreenRenderer = new WekaOffscreenChartRenderer();
/* 114:226 */         return;
/* 115:    */       }
/* 116:229 */       if (this.m_offscreenRendererName.equalsIgnoreCase("weka chart renderer")) {
/* 117:230 */         this.m_offscreenRenderer = new WekaOffscreenChartRenderer();
/* 118:    */       } else {
/* 119:    */         try
/* 120:    */         {
/* 121:233 */           Object r = PluginManager.getPluginInstance("weka.gui.beans.OffscreenChartRenderer", this.m_offscreenRendererName);
/* 122:236 */           if ((r != null) && ((r instanceof OffscreenChartRenderer)))
/* 123:    */           {
/* 124:237 */             this.m_offscreenRenderer = ((OffscreenChartRenderer)r);
/* 125:    */           }
/* 126:    */           else
/* 127:    */           {
/* 128:240 */             getStepManager().logWarning("Offscreen renderer '" + getOffscreenRendererName() + "' is not available, using default weka chart renderer " + "instead");
/* 129:    */             
/* 130:    */ 
/* 131:    */ 
/* 132:244 */             this.m_offscreenRenderer = new WekaOffscreenChartRenderer();
/* 133:    */           }
/* 134:    */         }
/* 135:    */         catch (Exception ex)
/* 136:    */         {
/* 137:247 */           ex.printStackTrace();
/* 138:    */           
/* 139:249 */           getStepManager().logWarning("Offscreen renderer '" + getOffscreenRendererName() + "' is not available, using default weka chart renderer " + "instead");
/* 140:    */           
/* 141:    */ 
/* 142:    */ 
/* 143:253 */           this.m_offscreenRenderer = new WekaOffscreenChartRenderer();
/* 144:    */         }
/* 145:    */       }
/* 146:    */     }
/* 147:    */   }
/* 148:    */   
/* 149:    */   public void stepInit()
/* 150:    */     throws WekaException
/* 151:    */   {}
/* 152:    */   
/* 153:    */   public synchronized void processIncoming(Data data)
/* 154:    */     throws WekaException
/* 155:    */   {
/* 156:266 */     getStepManager().processing();
/* 157:267 */     Instances toPlot = (Instances)data.getPrimaryPayload();
/* 158:268 */     String name = new SimpleDateFormat("HH:mm:ss.SSS - ").format(new Date());
/* 159:269 */     String relationName = toPlot.relationName();
/* 160:270 */     PlotData2D pd = new PlotData2D(toPlot);
/* 161:271 */     if (relationName.startsWith("__"))
/* 162:    */     {
/* 163:272 */       boolean[] connect = new boolean[toPlot.numInstances()];
/* 164:273 */       for (int i = 1; i < toPlot.numInstances(); i++) {
/* 165:274 */         connect[i] = true;
/* 166:    */       }
/* 167:    */       try
/* 168:    */       {
/* 169:277 */         pd.setConnectPoints(connect);
/* 170:    */       }
/* 171:    */       catch (Exception ex)
/* 172:    */       {
/* 173:279 */         throw new WekaException(ex);
/* 174:    */       }
/* 175:282 */       relationName = relationName.substring(2);
/* 176:    */     }
/* 177:285 */     String title = name + relationName;
/* 178:286 */     getStepManager().logDetailed("Processing " + title);
/* 179:287 */     pd.setPlotName(title);
/* 180:288 */     this.m_plots.add(pd);
/* 181:290 */     if (getStepManager().numOutgoingConnectionsOfType("image") > 0)
/* 182:    */     {
/* 183:291 */       setupOffscreenRenderer();
/* 184:292 */       BufferedImage osi = createOffscreenPlot(pd);
/* 185:    */       
/* 186:294 */       Data imageData = new Data("image", osi);
/* 187:295 */       if (relationName.length() > 10) {
/* 188:296 */         relationName = relationName.substring(0, 10);
/* 189:    */       }
/* 190:298 */       imageData.setPayloadElement("aux_textTitle", relationName + ":" + this.m_xAxis + "," + this.m_yAxis);
/* 191:    */       
/* 192:300 */       getStepManager().outputData(new Data[] { imageData });
/* 193:    */     }
/* 194:302 */     getStepManager().finished();
/* 195:    */   }
/* 196:    */   
/* 197:    */   protected BufferedImage createOffscreenPlot(PlotData2D pd)
/* 198:    */     throws WekaException
/* 199:    */   {
/* 200:307 */     setupOffscreenRenderer();
/* 201:    */     
/* 202:309 */     List<Instances> offscreenPlotInstances = new ArrayList();
/* 203:310 */     Instances predictedI = pd.getPlotInstances();
/* 204:311 */     if ((predictedI.classIndex() >= 0) && (predictedI.classAttribute().isNominal()))
/* 205:    */     {
/* 206:313 */       Instances[] classes = new Instances[predictedI.numClasses()];
/* 207:314 */       for (int i = 0; i < predictedI.numClasses(); i++)
/* 208:    */       {
/* 209:315 */         classes[i] = new Instances(predictedI, 0);
/* 210:316 */         classes[i].setRelationName(predictedI.classAttribute().value(i));
/* 211:    */       }
/* 212:318 */       for (int i = 0; i < predictedI.numInstances(); i++)
/* 213:    */       {
/* 214:319 */         Instance current = predictedI.instance(i);
/* 215:320 */         classes[((int)current.classValue())].add((Instance)current.copy());
/* 216:    */       }
/* 217:322 */       for (Instances classe : classes) {
/* 218:323 */         offscreenPlotInstances.add(classe);
/* 219:    */       }
/* 220:    */     }
/* 221:    */     else
/* 222:    */     {
/* 223:326 */       offscreenPlotInstances.add(new Instances(predictedI));
/* 224:    */     }
/* 225:329 */     List<String> options = new ArrayList();
/* 226:330 */     String additional = this.m_additionalOptions;
/* 227:331 */     if ((this.m_additionalOptions != null) && (this.m_additionalOptions.length() > 0)) {
/* 228:332 */       additional = environmentSubstitute(additional);
/* 229:    */     }
/* 230:334 */     if ((additional != null) && (!additional.contains("-color")))
/* 231:    */     {
/* 232:336 */       if (additional.length() > 0) {
/* 233:337 */         additional = additional + ",";
/* 234:    */       }
/* 235:339 */       if (predictedI.classIndex() >= 0) {
/* 236:340 */         additional = additional + "-color=" + predictedI.classAttribute().name();
/* 237:    */       } else {
/* 238:342 */         additional = additional + "-color=/last";
/* 239:    */       }
/* 240:    */     }
/* 241:345 */     String[] optionsParts = additional.split(",");
/* 242:346 */     for (String p : optionsParts) {
/* 243:347 */       options.add(p.trim());
/* 244:    */     }
/* 245:350 */     String xAxis = this.m_xAxis;
/* 246:351 */     xAxis = environmentSubstitute(xAxis);
/* 247:    */     
/* 248:353 */     String yAxis = this.m_yAxis;
/* 249:    */     
/* 250:355 */     yAxis = environmentSubstitute(yAxis);
/* 251:    */     
/* 252:357 */     String width = this.m_width;
/* 253:358 */     String height = this.m_height;
/* 254:359 */     int defWidth = 500;
/* 255:360 */     int defHeight = 400;
/* 256:    */     
/* 257:362 */     width = environmentSubstitute(width);
/* 258:363 */     height = environmentSubstitute(height);
/* 259:    */     
/* 260:365 */     defWidth = Integer.parseInt(width);
/* 261:366 */     defHeight = Integer.parseInt(height);
/* 262:    */     
/* 263:368 */     getStepManager().logDetailed("Creating image");
/* 264:    */     try
/* 265:    */     {
/* 266:371 */       return predictedI.relationName().startsWith("__") ? this.m_offscreenRenderer.renderXYLineChart(defWidth, defHeight, offscreenPlotInstances, xAxis, yAxis, options) : this.m_offscreenRenderer.renderXYScatterPlot(defWidth, defHeight, offscreenPlotInstances, xAxis, yAxis, options);
/* 267:    */     }
/* 268:    */     catch (Exception e)
/* 269:    */     {
/* 270:376 */       throw new WekaException(e);
/* 271:    */     }
/* 272:    */   }
/* 273:    */   
/* 274:    */   public List<PlotData2D> getPlots()
/* 275:    */   {
/* 276:381 */     return this.m_plots;
/* 277:    */   }
/* 278:    */   
/* 279:    */   public void clearPlotData()
/* 280:    */   {
/* 281:385 */     this.m_plots.clear();
/* 282:    */   }
/* 283:    */   
/* 284:    */   public Object retrieveData()
/* 285:    */   {
/* 286:390 */     return getPlots();
/* 287:    */   }
/* 288:    */   
/* 289:    */   public void restoreData(Object data)
/* 290:    */     throws WekaException
/* 291:    */   {
/* 292:396 */     if (!(data instanceof List)) {
/* 293:397 */       throw new WekaException("Argument must be a List<PlotData2D>");
/* 294:    */     }
/* 295:399 */     this.m_plots = ((List)data);
/* 296:402 */     for (PlotData2D pd : this.m_plots) {
/* 297:403 */       createOffscreenPlot(pd);
/* 298:    */     }
/* 299:    */   }
/* 300:    */   
/* 301:    */   public List<String> getIncomingConnectionTypes()
/* 302:    */   {
/* 303:409 */     return Arrays.asList(new String[] { "dataSet", "trainingSet", "testSet" });
/* 304:    */   }
/* 305:    */   
/* 306:    */   public List<String> getOutgoingConnectionTypes()
/* 307:    */   {
/* 308:415 */     return getStepManager().numIncomingConnections() > 0 ? Arrays.asList(new String[] { "image" }) : new ArrayList();
/* 309:    */   }
/* 310:    */   
/* 311:    */   public String getCustomEditorForStep()
/* 312:    */   {
/* 313:421 */     return "weka.gui.knowledgeflow.steps.DataVisualizerStepEditorDialog";
/* 314:    */   }
/* 315:    */   
/* 316:    */   public Map<String, String> getInteractiveViewers()
/* 317:    */   {
/* 318:426 */     Map<String, String> views = new LinkedHashMap();
/* 319:428 */     if (this.m_plots.size() > 0) {
/* 320:429 */       views.put("Show charts", "weka.gui.knowledgeflow.steps.DataVisualizerInteractiveView");
/* 321:    */     }
/* 322:433 */     return views;
/* 323:    */   }
/* 324:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.DataVisualizer
 * JD-Core Version:    0.7.0.1
 */