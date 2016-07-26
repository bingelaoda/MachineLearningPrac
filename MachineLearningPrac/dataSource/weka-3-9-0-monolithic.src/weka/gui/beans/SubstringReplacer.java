/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.beans.EventSetDescriptor;
/*   5:    */ import java.io.Serializable;
/*   6:    */ import java.util.ArrayList;
/*   7:    */ import java.util.List;
/*   8:    */ import javax.swing.JPanel;
/*   9:    */ import weka.core.Environment;
/*  10:    */ import weka.core.EnvironmentHandler;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.gui.Logger;
/*  14:    */ 
/*  15:    */ @KFStep(category="Tools", toolTipText="Replace substrings in String attributes")
/*  16:    */ public class SubstringReplacer
/*  17:    */   extends JPanel
/*  18:    */   implements BeanCommon, Visible, Serializable, InstanceListener, EventConstraints, EnvironmentHandler, DataSource
/*  19:    */ {
/*  20:    */   private static final long serialVersionUID = 5636877747903965818L;
/*  21:    */   protected transient Environment m_env;
/*  22: 64 */   protected String m_matchReplaceDetails = "";
/*  23:    */   protected transient SubstringReplacerRules m_mr;
/*  24:    */   protected transient Logger m_log;
/*  25:    */   protected transient boolean m_busy;
/*  26:    */   protected Object m_listenee;
/*  27: 81 */   protected ArrayList<InstanceListener> m_instanceListeners = new ArrayList();
/*  28: 84 */   protected InstanceEvent m_ie = new InstanceEvent(this);
/*  29: 89 */   protected BeanVisual m_visual = new BeanVisual("SubstringReplacer", "weka/gui/beans/icons/DefaultFilter.gif", "weka/gui/beans/icons/DefaultFilter_animated.gif");
/*  30:    */   protected transient StreamThroughput m_throughput;
/*  31:    */   
/*  32:    */   public SubstringReplacer()
/*  33:    */   {
/*  34: 97 */     useDefaultVisual();
/*  35: 98 */     setLayout(new BorderLayout());
/*  36: 99 */     add(this.m_visual, "Center");
/*  37:    */     
/*  38:101 */     this.m_env = Environment.getSystemWide();
/*  39:    */   }
/*  40:    */   
/*  41:    */   public String globalInfo()
/*  42:    */   {
/*  43:110 */     return "Replaces substrings in String attribute values using either literal match and replace or regular expression matching. The attributesto apply the match and replace rules to can be selected via a range string (e.g 1-5,6,last) or by a comma separated list of attribute names (/first and /last can be used to indicate the first and last attribute respectively)";
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void setMatchReplaceDetails(String details)
/*  47:    */   {
/*  48:126 */     this.m_matchReplaceDetails = details;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public String getMatchReplaceDetails()
/*  52:    */   {
/*  53:135 */     return this.m_matchReplaceDetails;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public boolean eventGeneratable(String eventName)
/*  57:    */   {
/*  58:147 */     if (this.m_listenee == null) {
/*  59:148 */       return false;
/*  60:    */     }
/*  61:151 */     if (!eventName.equals("instance")) {
/*  62:152 */       return false;
/*  63:    */     }
/*  64:155 */     if (((this.m_listenee instanceof EventConstraints)) && 
/*  65:156 */       (!((EventConstraints)this.m_listenee).eventGeneratable(eventName))) {
/*  66:157 */       return false;
/*  67:    */     }
/*  68:161 */     return true;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public synchronized void acceptInstance(InstanceEvent e)
/*  72:    */   {
/*  73:173 */     this.m_busy = true;
/*  74:174 */     if (e.getStatus() == 0)
/*  75:    */     {
/*  76:175 */       this.m_throughput = new StreamThroughput(statusMessagePrefix());
/*  77:176 */       Instances structure = e.getStructure();
/*  78:    */       
/*  79:178 */       this.m_mr = new SubstringReplacerRules(this.m_matchReplaceDetails, structure, statusMessagePrefix(), this.m_log, this.m_env);
/*  80:196 */       if ((!e.m_formatNotificationOnly) && 
/*  81:197 */         (this.m_log != null)) {
/*  82:198 */         this.m_log.statusMessage(statusMessagePrefix() + "Processing stream...");
/*  83:    */       }
/*  84:203 */       this.m_ie.setStructure(structure);
/*  85:204 */       this.m_ie.m_formatNotificationOnly = e.m_formatNotificationOnly;
/*  86:205 */       notifyInstanceListeners(this.m_ie);
/*  87:    */     }
/*  88:    */     else
/*  89:    */     {
/*  90:207 */       Instance inst = e.getInstance();
/*  91:209 */       if (inst != null)
/*  92:    */       {
/*  93:210 */         this.m_throughput.updateStart();
/*  94:211 */         inst = this.m_mr.makeOutputInstance(inst);
/*  95:    */         
/*  96:    */ 
/*  97:    */ 
/*  98:    */ 
/*  99:216 */         this.m_throughput.updateEnd(this.m_log);
/* 100:    */       }
/* 101:220 */       this.m_ie.setInstance(inst);
/* 102:221 */       this.m_ie.setStatus(e.getStatus());
/* 103:222 */       notifyInstanceListeners(this.m_ie);
/* 104:224 */       if ((e.getStatus() == 2) || (inst == null)) {
/* 105:226 */         this.m_throughput.finished(this.m_log);
/* 106:    */       }
/* 107:    */     }
/* 108:230 */     this.m_busy = false;
/* 109:    */   }
/* 110:    */   
/* 111:    */   public void useDefaultVisual()
/* 112:    */   {
/* 113:238 */     this.m_visual.loadIcons("weka/gui/beans/icons/DefaultFilter.gif", "weka/gui/beans/icons/DefaultFilter_animated.gif");
/* 114:    */     
/* 115:240 */     this.m_visual.setText("SubstringReplacer");
/* 116:    */   }
/* 117:    */   
/* 118:    */   public void setVisual(BeanVisual newVisual)
/* 119:    */   {
/* 120:251 */     this.m_visual = newVisual;
/* 121:    */   }
/* 122:    */   
/* 123:    */   public BeanVisual getVisual()
/* 124:    */   {
/* 125:262 */     return this.m_visual;
/* 126:    */   }
/* 127:    */   
/* 128:    */   public void setCustomName(String name)
/* 129:    */   {
/* 130:272 */     this.m_visual.setText(name);
/* 131:    */   }
/* 132:    */   
/* 133:    */   public String getCustomName()
/* 134:    */   {
/* 135:282 */     return this.m_visual.getText();
/* 136:    */   }
/* 137:    */   
/* 138:    */   public void stop()
/* 139:    */   {
/* 140:290 */     if ((this.m_listenee != null) && 
/* 141:291 */       ((this.m_listenee instanceof BeanCommon))) {
/* 142:292 */       ((BeanCommon)this.m_listenee).stop();
/* 143:    */     }
/* 144:296 */     if (this.m_log != null) {
/* 145:297 */       this.m_log.statusMessage(statusMessagePrefix() + "Stopped");
/* 146:    */     }
/* 147:300 */     this.m_busy = false;
/* 148:    */   }
/* 149:    */   
/* 150:    */   public boolean isBusy()
/* 151:    */   {
/* 152:311 */     return this.m_busy;
/* 153:    */   }
/* 154:    */   
/* 155:    */   public void setLog(Logger logger)
/* 156:    */   {
/* 157:321 */     this.m_log = logger;
/* 158:    */   }
/* 159:    */   
/* 160:    */   public boolean connectionAllowed(EventSetDescriptor esd)
/* 161:    */   {
/* 162:333 */     return connectionAllowed(esd.getName());
/* 163:    */   }
/* 164:    */   
/* 165:    */   public boolean connectionAllowed(String eventName)
/* 166:    */   {
/* 167:346 */     if (!eventName.equals("instance")) {
/* 168:347 */       return false;
/* 169:    */     }
/* 170:350 */     if (this.m_listenee != null) {
/* 171:351 */       return false;
/* 172:    */     }
/* 173:354 */     return true;
/* 174:    */   }
/* 175:    */   
/* 176:    */   public void connectionNotification(String eventName, Object source)
/* 177:    */   {
/* 178:368 */     if (connectionAllowed(eventName)) {
/* 179:369 */       this.m_listenee = source;
/* 180:    */     }
/* 181:    */   }
/* 182:    */   
/* 183:    */   public void disconnectionNotification(String eventName, Object source)
/* 184:    */   {
/* 185:383 */     if (source == this.m_listenee) {
/* 186:384 */       this.m_listenee = null;
/* 187:    */     }
/* 188:    */   }
/* 189:    */   
/* 190:    */   public void setEnvironment(Environment env)
/* 191:    */   {
/* 192:393 */     this.m_env = env;
/* 193:    */   }
/* 194:    */   
/* 195:    */   protected String statusMessagePrefix()
/* 196:    */   {
/* 197:397 */     return getCustomName() + "$" + hashCode() + "|";
/* 198:    */   }
/* 199:    */   
/* 200:    */   private void notifyInstanceListeners(InstanceEvent e)
/* 201:    */   {
/* 202:    */     List<InstanceListener> l;
/* 203:403 */     synchronized (this)
/* 204:    */     {
/* 205:404 */       l = (List)this.m_instanceListeners.clone();
/* 206:    */     }
/* 207:406 */     if (l.size() > 0) {
/* 208:407 */       for (InstanceListener il : l) {
/* 209:408 */         il.acceptInstance(e);
/* 210:    */       }
/* 211:    */     }
/* 212:    */   }
/* 213:    */   
/* 214:    */   public synchronized void addInstanceListener(InstanceListener tsl)
/* 215:    */   {
/* 216:420 */     this.m_instanceListeners.add(tsl);
/* 217:    */   }
/* 218:    */   
/* 219:    */   public synchronized void removeInstanceListener(InstanceListener tsl)
/* 220:    */   {
/* 221:430 */     this.m_instanceListeners.remove(tsl);
/* 222:    */   }
/* 223:    */   
/* 224:    */   public void addDataSourceListener(DataSourceListener dsl) {}
/* 225:    */   
/* 226:    */   public void removeDataSourceListener(DataSourceListener dsl) {}
/* 227:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.SubstringReplacer
 * JD-Core Version:    0.7.0.1
 */