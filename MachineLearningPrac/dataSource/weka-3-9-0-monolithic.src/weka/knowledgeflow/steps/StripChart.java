/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.LinkedHashMap;
/*   6:    */ import java.util.List;
/*   7:    */ import java.util.Map;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Instance;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.WekaException;
/*  12:    */ import weka.knowledgeflow.Data;
/*  13:    */ import weka.knowledgeflow.StepManager;
/*  14:    */ 
/*  15:    */ @KFStep(name="StripChart", category="Visualization", toolTipText="Plot streaming data", iconPath="weka/gui/knowledgeflow/icons/StripChart.gif")
/*  16:    */ public class StripChart
/*  17:    */   extends BaseStep
/*  18:    */ {
/*  19:    */   private static final long serialVersionUID = -2569383350174947630L;
/*  20:    */   protected List<PlotNotificationListener> m_plotListeners;
/*  21:    */   protected int m_xValFreq;
/*  22:    */   private int m_refreshFrequency;
/*  23:    */   private int m_userRefreshWidth;
/*  24:    */   protected boolean m_reset;
/*  25:    */   protected int m_instanceWidth;
/*  26:    */   
/*  27:    */   public StripChart()
/*  28:    */   {
/*  29: 51 */     this.m_plotListeners = new ArrayList();
/*  30:    */     
/*  31:    */ 
/*  32:    */ 
/*  33: 55 */     this.m_xValFreq = 500;
/*  34:    */     
/*  35:    */ 
/*  36:    */ 
/*  37:    */ 
/*  38: 60 */     this.m_refreshFrequency = 5;
/*  39:    */     
/*  40: 62 */     this.m_userRefreshWidth = 1;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public String xLabelFreqTipText()
/*  44:    */   {
/*  45: 79 */     return "Show x axis labels this often";
/*  46:    */   }
/*  47:    */   
/*  48:    */   public int getXLabelFreq()
/*  49:    */   {
/*  50: 88 */     return this.m_xValFreq;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void setXLabelFreq(int freq)
/*  54:    */   {
/*  55: 97 */     this.m_xValFreq = freq;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public String refreshFreqTipText()
/*  59:    */   {
/*  60:106 */     return "Plot every x'th data point";
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void setRefreshFreq(int freq)
/*  64:    */   {
/*  65:115 */     this.m_refreshFrequency = freq;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public int getRefreshFreq()
/*  69:    */   {
/*  70:124 */     return this.m_refreshFrequency;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public String refreshWidthTipText()
/*  74:    */   {
/*  75:133 */     return "The number of pixels to shift the plot by every time a point is plotted.";
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void setRefreshWidth(int width)
/*  79:    */   {
/*  80:143 */     if (width > 0) {
/*  81:144 */       this.m_userRefreshWidth = width;
/*  82:    */     }
/*  83:    */   }
/*  84:    */   
/*  85:    */   public int getRefreshWidth()
/*  86:    */   {
/*  87:154 */     return this.m_userRefreshWidth;
/*  88:    */   }
/*  89:    */   
/*  90:    */   public void stepInit()
/*  91:    */     throws WekaException
/*  92:    */   {
/*  93:159 */     this.m_reset = true;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public synchronized void processIncoming(Data data)
/*  97:    */     throws WekaException
/*  98:    */   {
/*  99:171 */     if (isStopRequested()) {
/* 100:172 */       return;
/* 101:    */     }
/* 102:175 */     if (getStepManager().isStreamFinished(data))
/* 103:    */     {
/* 104:178 */       Data d = new Data(data.getConnectionName());
/* 105:179 */       getStepManager().throughputFinished(new Data[] { d });
/* 106:180 */       return;
/* 107:    */     }
/* 108:183 */     getStepManager().throughputUpdateStart();
/* 109:    */     double[] dataPoint;
/* 110:184 */     if (this.m_plotListeners.size() > 0)
/* 111:    */     {
/* 112:    */       double[] dataPoint;
/* 113:185 */       if (getStepManager().numIncomingConnectionsOfType("instance") > 0)
/* 114:    */       {
/* 115:187 */         Instance instance = (Instance)data.getPayloadElement("instance");
/* 116:    */         List<String> legendEntries;
/* 117:189 */         if (this.m_reset)
/* 118:    */         {
/* 119:190 */           this.m_reset = false;
/* 120:191 */           legendEntries = new ArrayList();
/* 121:193 */           for (int i = 0; (i < instance.dataset().numAttributes()) && (i < 10); i++) {
/* 122:194 */             legendEntries.add(instance.dataset().attribute(i).name());
/* 123:    */           }
/* 124:196 */           this.m_instanceWidth = i;
/* 125:198 */           for (PlotNotificationListener l : this.m_plotListeners) {
/* 126:199 */             l.setLegend(legendEntries, 0.0D, 1.0D);
/* 127:    */           }
/* 128:    */         }
/* 129:203 */         dataPoint = new double[this.m_instanceWidth];
/* 130:204 */         for (int i = 0; i < dataPoint.length; i++) {
/* 131:205 */           if (!instance.isMissing(i)) {
/* 132:206 */             dataPoint[i] = instance.value(i);
/* 133:    */           }
/* 134:    */         }
/* 135:209 */         for (PlotNotificationListener l : this.m_plotListeners) {
/* 136:210 */           l.acceptDataPoint(dataPoint);
/* 137:    */         }
/* 138:    */       }
/* 139:213 */       else if (getStepManager().numIncomingConnectionsOfType("chart") > 0)
/* 140:    */       {
/* 141:    */         double min;
/* 142:    */         double max;
/* 143:    */         List<String> legend;
/* 144:215 */         if (this.m_reset)
/* 145:    */         {
/* 146:216 */           this.m_reset = false;
/* 147:217 */           min = ((Double)data.getPayloadElement("chart_min", Double.valueOf(0.0D))).doubleValue();
/* 148:    */           
/* 149:219 */           max = ((Double)data.getPayloadElement("chart_max", Double.valueOf(1.0D))).doubleValue();
/* 150:    */           
/* 151:221 */           legend = (List)data.getPayloadElement("chart_legend");
/* 152:224 */           for (PlotNotificationListener l : this.m_plotListeners) {
/* 153:225 */             l.setLegend(legend, min, max);
/* 154:    */           }
/* 155:    */         }
/* 156:228 */         dataPoint = (double[])data.getPayloadElement("chart_data_point");
/* 157:231 */         for (PlotNotificationListener l : this.m_plotListeners) {
/* 158:232 */           l.acceptDataPoint(dataPoint);
/* 159:    */         }
/* 160:    */       }
/* 161:    */     }
/* 162:236 */     getStepManager().throughputUpdateEnd();
/* 163:    */   }
/* 164:    */   
/* 165:    */   public List<String> getIncomingConnectionTypes()
/* 166:    */   {
/* 167:250 */     if (getStepManager().numIncomingConnections() == 0) {
/* 168:251 */       return Arrays.asList(new String[] { "instance", "chart" });
/* 169:    */     }
/* 170:254 */     return new ArrayList();
/* 171:    */   }
/* 172:    */   
/* 173:    */   public synchronized void addPlotNotificationListener(PlotNotificationListener listener)
/* 174:    */   {
/* 175:264 */     this.m_plotListeners.add(listener);
/* 176:    */   }
/* 177:    */   
/* 178:    */   public synchronized void removePlotNotificationListener(PlotNotificationListener l)
/* 179:    */   {
/* 180:274 */     this.m_plotListeners.remove(l);
/* 181:    */   }
/* 182:    */   
/* 183:    */   public List<String> getOutgoingConnectionTypes()
/* 184:    */   {
/* 185:288 */     return new ArrayList();
/* 186:    */   }
/* 187:    */   
/* 188:    */   public Map<String, String> getInteractiveViewers()
/* 189:    */   {
/* 190:313 */     Map<String, String> views = new LinkedHashMap();
/* 191:    */     
/* 192:315 */     views.put("Show chart", "weka.gui.knowledgeflow.steps.StripChartInteractiveView");
/* 193:    */     
/* 194:    */ 
/* 195:318 */     return views;
/* 196:    */   }
/* 197:    */   
/* 198:    */   public static abstract interface PlotNotificationListener
/* 199:    */   {
/* 200:    */     public abstract void setLegend(List<String> paramList, double paramDouble1, double paramDouble2);
/* 201:    */     
/* 202:    */     public abstract void acceptDataPoint(double[] paramArrayOfDouble);
/* 203:    */   }
/* 204:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.StripChart
 * JD-Core Version:    0.7.0.1
 */