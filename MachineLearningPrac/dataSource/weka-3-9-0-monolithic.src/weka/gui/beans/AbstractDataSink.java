/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.beans.EventSetDescriptor;
/*   5:    */ import java.io.Serializable;
/*   6:    */ import javax.swing.JPanel;
/*   7:    */ import weka.gui.Logger;
/*   8:    */ 
/*   9:    */ public abstract class AbstractDataSink
/*  10:    */   extends JPanel
/*  11:    */   implements DataSink, BeanCommon, Visible, DataSourceListener, TrainingSetListener, TestSetListener, InstanceListener, ThresholdDataListener, Serializable
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = 3956528599473814287L;
/*  14: 53 */   protected BeanVisual m_visual = new BeanVisual("AbstractDataSink", "weka/gui/beans/icons/DefaultDataSink.gif", "weka/gui/beans/icons/DefaultDataSink_animated.gif");
/*  15: 64 */   protected Object m_listenee = null;
/*  16: 66 */   protected transient Logger m_logger = null;
/*  17:    */   
/*  18:    */   public AbstractDataSink()
/*  19:    */   {
/*  20: 69 */     useDefaultVisual();
/*  21: 70 */     setLayout(new BorderLayout());
/*  22: 71 */     add(this.m_visual, "Center");
/*  23:    */   }
/*  24:    */   
/*  25:    */   public abstract void acceptTrainingSet(TrainingSetEvent paramTrainingSetEvent);
/*  26:    */   
/*  27:    */   public abstract void acceptTestSet(TestSetEvent paramTestSetEvent);
/*  28:    */   
/*  29:    */   public abstract void acceptDataSet(DataSetEvent paramDataSetEvent);
/*  30:    */   
/*  31:    */   public abstract void acceptDataSet(ThresholdDataEvent paramThresholdDataEvent);
/*  32:    */   
/*  33:    */   public abstract void acceptInstance(InstanceEvent paramInstanceEvent);
/*  34:    */   
/*  35:    */   public void setVisual(BeanVisual newVisual)
/*  36:    */   {
/*  37:115 */     this.m_visual = newVisual;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public BeanVisual getVisual()
/*  41:    */   {
/*  42:123 */     return this.m_visual;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void useDefaultVisual()
/*  46:    */   {
/*  47:131 */     this.m_visual.loadIcons("weka/gui/beans/icons/DefaultDataSink.gif", "weka/gui/beans/icons/DefaultDataSink_animated.gif");
/*  48:    */   }
/*  49:    */   
/*  50:    */   public boolean connectionAllowed(EventSetDescriptor esd)
/*  51:    */   {
/*  52:144 */     return connectionAllowed(esd.getName());
/*  53:    */   }
/*  54:    */   
/*  55:    */   public boolean connectionAllowed(String eventName)
/*  56:    */   {
/*  57:156 */     return this.m_listenee == null;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public synchronized void connectionNotification(String eventName, Object source)
/*  61:    */   {
/*  62:169 */     if (connectionAllowed(eventName)) {
/*  63:170 */       this.m_listenee = source;
/*  64:    */     }
/*  65:    */   }
/*  66:    */   
/*  67:    */   public synchronized void disconnectionNotification(String eventName, Object source)
/*  68:    */   {
/*  69:184 */     if (this.m_listenee == source) {
/*  70:185 */       this.m_listenee = null;
/*  71:    */     }
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void setLog(Logger logger)
/*  75:    */   {
/*  76:195 */     this.m_logger = logger;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public abstract void stop();
/*  80:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.AbstractDataSink
 * JD-Core Version:    0.7.0.1
 */