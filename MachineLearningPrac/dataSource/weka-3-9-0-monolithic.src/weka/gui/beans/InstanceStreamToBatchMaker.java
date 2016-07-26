/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.beans.EventSetDescriptor;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.LinkedList;
/*   7:    */ import java.util.List;
/*   8:    */ import javax.swing.JPanel;
/*   9:    */ import weka.core.Instance;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.gui.Logger;
/*  12:    */ 
/*  13:    */ @KFStep(category="Flow", toolTipText="Converts an incoming instance stream into a data set batch")
/*  14:    */ public class InstanceStreamToBatchMaker
/*  15:    */   extends JPanel
/*  16:    */   implements BeanCommon, Visible, InstanceListener, EventConstraints, DataSource
/*  17:    */ {
/*  18:    */   private static final long serialVersionUID = -7037141087208627799L;
/*  19: 52 */   protected BeanVisual m_visual = new BeanVisual("InstanceStreamToBatchMaker", "weka/gui/beans/icons/InstanceStreamToBatchMaker.gif", "weka/gui/beans/icons/InstanceStreamToBatchMaker_animated.gif");
/*  20:    */   private transient Logger m_log;
/*  21:    */   private Object m_listenee;
/*  22: 66 */   private final ArrayList<DataSourceListener> m_dataListeners = new ArrayList();
/*  23:    */   private List<Instance> m_batch;
/*  24:    */   private Instances m_structure;
/*  25:    */   
/*  26:    */   public InstanceStreamToBatchMaker()
/*  27:    */   {
/*  28: 76 */     setLayout(new BorderLayout());
/*  29: 77 */     add(this.m_visual, "Center");
/*  30:    */   }
/*  31:    */   
/*  32:    */   public void acceptInstance(InstanceEvent e)
/*  33:    */   {
/*  34: 87 */     if (e.getStatus() == 0)
/*  35:    */     {
/*  36: 88 */       this.m_batch = new LinkedList();
/*  37: 89 */       this.m_structure = e.getStructure();
/*  38: 92 */       if (this.m_log != null) {
/*  39: 93 */         this.m_log.logMessage("[InstanceStreamToBatch] passing on structure.");
/*  40:    */       }
/*  41: 95 */       DataSetEvent dse = new DataSetEvent(this, this.m_structure);
/*  42: 96 */       notifyDataListeners(dse);
/*  43:    */     }
/*  44: 97 */     else if (e.getStatus() == 1)
/*  45:    */     {
/*  46: 98 */       this.m_batch.add(e.getInstance());
/*  47:    */     }
/*  48:    */     else
/*  49:    */     {
/*  50:102 */       if (e.getInstance() != null) {
/*  51:104 */         this.m_batch.add(e.getInstance());
/*  52:    */       }
/*  53:108 */       Instances dataSet = new Instances(this.m_structure, this.m_batch.size());
/*  54:109 */       for (Instance i : this.m_batch) {
/*  55:110 */         dataSet.add(i);
/*  56:    */       }
/*  57:112 */       dataSet.compactify();
/*  58:    */       
/*  59:    */ 
/*  60:115 */       this.m_batch = null;
/*  61:117 */       if (this.m_log != null) {
/*  62:118 */         this.m_log.logMessage("[InstanceStreamToBatch] sending batch to listeners.");
/*  63:    */       }
/*  64:122 */       DataSetEvent dse = new DataSetEvent(this, dataSet);
/*  65:123 */       notifyDataListeners(dse);
/*  66:    */     }
/*  67:    */   }
/*  68:    */   
/*  69:    */   public boolean connectionAllowed(EventSetDescriptor esd)
/*  70:    */   {
/*  71:136 */     return connectionAllowed(esd.getName());
/*  72:    */   }
/*  73:    */   
/*  74:    */   public boolean connectionAllowed(String eventName)
/*  75:    */   {
/*  76:148 */     if ((this.m_listenee != null) || (!eventName.equals("instance"))) {
/*  77:149 */       return false;
/*  78:    */     }
/*  79:151 */     return true;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public void connectionNotification(String eventName, Object source)
/*  83:    */   {
/*  84:164 */     if (connectionAllowed(eventName)) {
/*  85:165 */       this.m_listenee = source;
/*  86:    */     }
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void disconnectionNotification(String eventName, Object source)
/*  90:    */   {
/*  91:179 */     this.m_listenee = null;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public boolean eventGeneratable(String eventName)
/*  95:    */   {
/*  96:190 */     if (!eventName.equals("dataSet")) {
/*  97:191 */       return false;
/*  98:    */     }
/*  99:194 */     if (this.m_listenee == null) {
/* 100:195 */       return false;
/* 101:    */     }
/* 102:198 */     if (((this.m_listenee instanceof EventConstraints)) && 
/* 103:199 */       (!((EventConstraints)this.m_listenee).eventGeneratable("instance"))) {
/* 104:200 */       return false;
/* 105:    */     }
/* 106:204 */     return true;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public String getCustomName()
/* 110:    */   {
/* 111:214 */     return this.m_visual.getText();
/* 112:    */   }
/* 113:    */   
/* 114:    */   public void setCustomName(String name)
/* 115:    */   {
/* 116:224 */     this.m_visual.setText(name);
/* 117:    */   }
/* 118:    */   
/* 119:    */   public void setLog(Logger logger)
/* 120:    */   {
/* 121:234 */     this.m_log = logger;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public boolean isBusy()
/* 125:    */   {
/* 126:245 */     return false;
/* 127:    */   }
/* 128:    */   
/* 129:    */   public void stop() {}
/* 130:    */   
/* 131:    */   public BeanVisual getVisual()
/* 132:    */   {
/* 133:261 */     return this.m_visual;
/* 134:    */   }
/* 135:    */   
/* 136:    */   public void setVisual(BeanVisual newVisual)
/* 137:    */   {
/* 138:271 */     this.m_visual = newVisual;
/* 139:    */   }
/* 140:    */   
/* 141:    */   public void useDefaultVisual()
/* 142:    */   {
/* 143:279 */     this.m_visual.loadIcons("weka/gui/beans/icons/InstanceStreamToBatchMaker.gif", "weka/gui/beans/icons/InstanceStreamToBatchMaker_animated.gif");
/* 144:    */   }
/* 145:    */   
/* 146:    */   protected void notifyDataListeners(DataSetEvent tse)
/* 147:    */   {
/* 148:    */     ArrayList<DataSourceListener> l;
/* 149:291 */     synchronized (this)
/* 150:    */     {
/* 151:292 */       l = (ArrayList)this.m_dataListeners.clone();
/* 152:    */     }
/* 153:294 */     if (l.size() > 0) {
/* 154:295 */       for (int i = 0; i < l.size(); i++) {
/* 155:296 */         ((DataSourceListener)l.get(i)).acceptDataSet(tse);
/* 156:    */       }
/* 157:    */     }
/* 158:    */   }
/* 159:    */   
/* 160:    */   public synchronized void addDataSourceListener(DataSourceListener tsl)
/* 161:    */   {
/* 162:303 */     this.m_dataListeners.add(tsl);
/* 163:305 */     if (this.m_structure != null)
/* 164:    */     {
/* 165:306 */       DataSetEvent e = new DataSetEvent(this, this.m_structure);
/* 166:307 */       tsl.acceptDataSet(e);
/* 167:    */     }
/* 168:    */   }
/* 169:    */   
/* 170:    */   public synchronized void removeDataSourceListener(DataSourceListener tsl)
/* 171:    */   {
/* 172:313 */     this.m_dataListeners.remove(tsl);
/* 173:    */   }
/* 174:    */   
/* 175:    */   public synchronized void addInstanceListener(InstanceListener il) {}
/* 176:    */   
/* 177:    */   public synchronized void removeInstanceListener(InstanceListener il) {}
/* 178:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.InstanceStreamToBatchMaker
 * JD-Core Version:    0.7.0.1
 */