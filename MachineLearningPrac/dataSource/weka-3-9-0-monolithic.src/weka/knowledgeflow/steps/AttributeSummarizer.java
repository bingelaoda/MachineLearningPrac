/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.awt.image.BufferedImage;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Arrays;
/*   6:    */ import java.util.LinkedHashMap;
/*   7:    */ import java.util.List;
/*   8:    */ import java.util.Map;
/*   9:    */ import weka.core.Attribute;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.OptionMetadata;
/*  13:    */ import weka.core.PluginManager;
/*  14:    */ import weka.gui.ProgrammaticProperty;
/*  15:    */ import weka.gui.beans.OffscreenChartRenderer;
/*  16:    */ import weka.gui.beans.WekaOffscreenChartRenderer;
/*  17:    */ import weka.knowledgeflow.Data;
/*  18:    */ import weka.knowledgeflow.StepManager;
/*  19:    */ 
/*  20:    */ @KFStep(name="AttributeSummarizer", category="Visualization", toolTipText="Visualize datasets in a matrix of histograms", iconPath="weka/gui/knowledgeflow/icons/AttributeSummarizer.gif")
/*  21:    */ public class AttributeSummarizer
/*  22:    */   extends BaseSimpleDataVisualizer
/*  23:    */ {
/*  24:    */   private static final long serialVersionUID = 2313372820072708102L;
/*  25: 57 */   protected String m_xAxis = "";
/*  26:    */   protected transient OffscreenChartRenderer m_offscreenRenderer;
/*  27: 63 */   protected String m_offscreenRendererName = "Weka Chart Renderer";
/*  28: 68 */   protected String m_additionalOptions = "";
/*  29: 71 */   protected String m_width = "500";
/*  30: 74 */   protected String m_height = "400";
/*  31:    */   
/*  32:    */   @OptionMetadata(displayName="X-axis attribute", description="Attribute name or /first, /last or /<index>", displayOrder=1)
/*  33:    */   public void setOffscreenXAxis(String xAxis)
/*  34:    */   {
/*  35: 86 */     this.m_xAxis = xAxis;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public String getOffscreenXAxis()
/*  39:    */   {
/*  40: 95 */     return this.m_xAxis;
/*  41:    */   }
/*  42:    */   
/*  43:    */   @OptionMetadata(displayName="Chart width (pixels)", description="Width of the rendered chart", displayOrder=2)
/*  44:    */   public void setOffscreenWidth(String width)
/*  45:    */   {
/*  46:106 */     this.m_width = width;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public String getOffscreenWidth()
/*  50:    */   {
/*  51:115 */     return this.m_width;
/*  52:    */   }
/*  53:    */   
/*  54:    */   @OptionMetadata(displayName="Chart height (pixels)", description="Height of the rendered chart", displayOrder=3)
/*  55:    */   public void setOffscreenHeight(String height)
/*  56:    */   {
/*  57:126 */     this.m_height = height;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public String getOffscreenHeight()
/*  61:    */   {
/*  62:135 */     return this.m_height;
/*  63:    */   }
/*  64:    */   
/*  65:    */   @ProgrammaticProperty
/*  66:    */   public void setOffscreenRendererName(String rendererName)
/*  67:    */   {
/*  68:146 */     this.m_offscreenRendererName = rendererName;
/*  69:147 */     this.m_offscreenRenderer = null;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public String getOffscreenRendererName()
/*  73:    */   {
/*  74:157 */     return this.m_offscreenRendererName;
/*  75:    */   }
/*  76:    */   
/*  77:    */   @ProgrammaticProperty
/*  78:    */   public void setOffscreenAdditionalOpts(String additional)
/*  79:    */   {
/*  80:167 */     this.m_additionalOptions = additional;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public String getOffscreenAdditionalOpts()
/*  84:    */   {
/*  85:176 */     return this.m_additionalOptions;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public synchronized void processIncoming(Data data)
/*  89:    */   {
/*  90:186 */     super.processIncoming(data, false);
/*  91:188 */     if (getStepManager().numOutgoingConnectionsOfType("image") > 0)
/*  92:    */     {
/*  93:189 */       setupOffscreenRenderer();
/*  94:190 */       createOffscreenPlot(data);
/*  95:    */     }
/*  96:192 */     getStepManager().finished();
/*  97:    */   }
/*  98:    */   
/*  99:    */   protected void createOffscreenPlot(Data data)
/* 100:    */   {
/* 101:201 */     List<Instances> offscreenPlotData = new ArrayList();
/* 102:202 */     Instances predictedI = (Instances)data.getPrimaryPayload();
/* 103:204 */     if ((predictedI.classIndex() >= 0) && (predictedI.classAttribute().isNominal()))
/* 104:    */     {
/* 105:206 */       Instances[] classes = new Instances[predictedI.numClasses()];
/* 106:207 */       for (int i = 0; i < predictedI.numClasses(); i++)
/* 107:    */       {
/* 108:208 */         classes[i] = new Instances(predictedI, 0);
/* 109:209 */         classes[i].setRelationName(predictedI.classAttribute().value(i));
/* 110:    */       }
/* 111:211 */       for (int i = 0; i < predictedI.numInstances(); i++)
/* 112:    */       {
/* 113:212 */         Instance current = predictedI.instance(i);
/* 114:213 */         classes[((int)current.classValue())].add((Instance)current.copy());
/* 115:    */       }
/* 116:215 */       for (Instances classe : classes) {
/* 117:216 */         offscreenPlotData.add(classe);
/* 118:    */       }
/* 119:    */     }
/* 120:    */     else
/* 121:    */     {
/* 122:219 */       offscreenPlotData.add(new Instances(predictedI));
/* 123:    */     }
/* 124:222 */     List<String> options = new ArrayList();
/* 125:223 */     String additional = this.m_additionalOptions;
/* 126:224 */     if (this.m_additionalOptions.length() > 0) {
/* 127:225 */       additional = environmentSubstitute(additional);
/* 128:    */     }
/* 129:228 */     if (additional.contains("-color"))
/* 130:    */     {
/* 131:230 */       if (additional.length() > 0) {
/* 132:231 */         additional = additional + ",";
/* 133:    */       }
/* 134:233 */       if (predictedI.classIndex() >= 0) {
/* 135:234 */         additional = additional + "-color=" + predictedI.classAttribute().name();
/* 136:    */       } else {
/* 137:236 */         additional = additional + "-color=/last";
/* 138:    */       }
/* 139:    */     }
/* 140:240 */     String[] optionsParts = additional.split(",");
/* 141:241 */     for (String p : optionsParts) {
/* 142:242 */       options.add(p.trim());
/* 143:    */     }
/* 144:246 */     String xAxis = this.m_xAxis;
/* 145:247 */     xAxis = environmentSubstitute(xAxis);
/* 146:    */     
/* 147:249 */     String width = this.m_width;
/* 148:250 */     String height = this.m_height;
/* 149:251 */     int defWidth = 500;
/* 150:252 */     int defHeight = 400;
/* 151:253 */     width = environmentSubstitute(width);
/* 152:254 */     height = environmentSubstitute(height);
/* 153:    */     
/* 154:256 */     defWidth = Integer.parseInt(width);
/* 155:257 */     defHeight = Integer.parseInt(height);
/* 156:    */     try
/* 157:    */     {
/* 158:260 */       getStepManager().logDetailed("Creating image");
/* 159:261 */       BufferedImage osi = this.m_offscreenRenderer.renderHistogram(defWidth, defHeight, offscreenPlotData, xAxis, options);
/* 160:    */       
/* 161:    */ 
/* 162:    */ 
/* 163:265 */       Data imageData = new Data("image", osi);
/* 164:266 */       String relationName = predictedI.relationName();
/* 165:267 */       if (relationName.length() > 10) {
/* 166:268 */         relationName = relationName.substring(0, 10);
/* 167:    */       }
/* 168:270 */       imageData.setPayloadElement("aux_textTitle", relationName + ":" + this.m_xAxis);
/* 169:    */       
/* 170:272 */       getStepManager().outputData(new Data[] { imageData });
/* 171:    */     }
/* 172:    */     catch (Exception e1)
/* 173:    */     {
/* 174:274 */       e1.printStackTrace();
/* 175:    */     }
/* 176:    */   }
/* 177:    */   
/* 178:    */   public Map<String, String> getInteractiveViewers()
/* 179:    */   {
/* 180:286 */     Map<String, String> views = new LinkedHashMap();
/* 181:288 */     if (this.m_data.size() > 0) {
/* 182:289 */       views.put("Show plots", "weka.gui.knowledgeflow.steps.AttributeSummarizerInteractiveView");
/* 183:    */     }
/* 184:293 */     return views;
/* 185:    */   }
/* 186:    */   
/* 187:    */   protected void setupOffscreenRenderer()
/* 188:    */   {
/* 189:300 */     getStepManager().logDetailed("Initializing offscreen renderer: " + getOffscreenRendererName());
/* 190:302 */     if (this.m_offscreenRenderer == null)
/* 191:    */     {
/* 192:303 */       if ((this.m_offscreenRendererName == null) || (this.m_offscreenRendererName.length() == 0))
/* 193:    */       {
/* 194:305 */         this.m_offscreenRenderer = new WekaOffscreenChartRenderer();
/* 195:306 */         return;
/* 196:    */       }
/* 197:309 */       if (this.m_offscreenRendererName.equalsIgnoreCase("weka chart renderer")) {
/* 198:310 */         this.m_offscreenRenderer = new WekaOffscreenChartRenderer();
/* 199:    */       } else {
/* 200:    */         try
/* 201:    */         {
/* 202:313 */           Object r = PluginManager.getPluginInstance("weka.gui.beans.OffscreenChartRenderer", this.m_offscreenRendererName);
/* 203:316 */           if ((r != null) && ((r instanceof OffscreenChartRenderer)))
/* 204:    */           {
/* 205:317 */             this.m_offscreenRenderer = ((OffscreenChartRenderer)r);
/* 206:    */           }
/* 207:    */           else
/* 208:    */           {
/* 209:320 */             getStepManager().logWarning("Offscreen renderer '" + getOffscreenRendererName() + "' is not available, using default weka chart renderer " + "instead");
/* 210:    */             
/* 211:    */ 
/* 212:    */ 
/* 213:    */ 
/* 214:    */ 
/* 215:326 */             this.m_offscreenRenderer = new WekaOffscreenChartRenderer();
/* 216:    */           }
/* 217:    */         }
/* 218:    */         catch (Exception ex)
/* 219:    */         {
/* 220:330 */           getStepManager().logWarning("Offscreen renderer '" + getOffscreenRendererName() + "' is not available, using default weka chart renderer " + "instead");
/* 221:    */           
/* 222:    */ 
/* 223:    */ 
/* 224:    */ 
/* 225:    */ 
/* 226:336 */           this.m_offscreenRenderer = new WekaOffscreenChartRenderer();
/* 227:    */         }
/* 228:    */       }
/* 229:    */     }
/* 230:    */   }
/* 231:    */   
/* 232:    */   public List<String> getOutgoingConnectionTypes()
/* 233:    */   {
/* 234:349 */     return getStepManager().numIncomingConnections() > 0 ? Arrays.asList(new String[] { "image" }) : new ArrayList();
/* 235:    */   }
/* 236:    */   
/* 237:    */   public String getCustomEditorForStep()
/* 238:    */   {
/* 239:360 */     return "weka.gui.knowledgeflow.steps.AttributeSummarizerStepEditorDialog";
/* 240:    */   }
/* 241:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.AttributeSummarizer
 * JD-Core Version:    0.7.0.1
 */