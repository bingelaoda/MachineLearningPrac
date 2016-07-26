/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.beans.EventSetDescriptor;
/*   5:    */ import java.io.Serializable;
/*   6:    */ import javax.swing.JPanel;
/*   7:    */ import weka.gui.Logger;
/*   8:    */ 
/*   9:    */ public abstract class AbstractEvaluator
/*  10:    */   extends JPanel
/*  11:    */   implements Visible, BeanCommon, Serializable
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = 3983303541814121632L;
/*  14: 53 */   protected BeanVisual m_visual = new BeanVisual("AbstractEvaluator", "weka/gui/beans/icons/DefaultEvaluator.gif", "weka/gui/beans/icons/DefaultEvaluator_animated.gif");
/*  15: 58 */   protected Object m_listenee = null;
/*  16: 60 */   protected transient Logger m_logger = null;
/*  17:    */   
/*  18:    */   public AbstractEvaluator()
/*  19:    */   {
/*  20: 66 */     setLayout(new BorderLayout());
/*  21: 67 */     add(this.m_visual, "Center");
/*  22:    */   }
/*  23:    */   
/*  24:    */   public void setVisual(BeanVisual newVisual)
/*  25:    */   {
/*  26: 76 */     this.m_visual = newVisual;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public BeanVisual getVisual()
/*  30:    */   {
/*  31: 85 */     return this.m_visual;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public void useDefaultVisual()
/*  35:    */   {
/*  36: 92 */     this.m_visual.loadIcons("weka/gui/beans/icons/DefaultEvaluator.gif", "weka/gui/beans/icons/DefaultEvaluator_animated.gif");
/*  37:    */   }
/*  38:    */   
/*  39:    */   public boolean connectionAllowed(String eventName)
/*  40:    */   {
/*  41:105 */     return this.m_listenee == null;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public boolean connectionAllowed(EventSetDescriptor esd)
/*  45:    */   {
/*  46:117 */     return connectionAllowed(esd.getName());
/*  47:    */   }
/*  48:    */   
/*  49:    */   public synchronized void connectionNotification(String eventName, Object source)
/*  50:    */   {
/*  51:130 */     if (connectionAllowed(eventName)) {
/*  52:131 */       this.m_listenee = source;
/*  53:    */     }
/*  54:    */   }
/*  55:    */   
/*  56:    */   public synchronized void disconnectionNotification(String eventName, Object source)
/*  57:    */   {
/*  58:145 */     if (this.m_listenee == source) {
/*  59:146 */       this.m_listenee = null;
/*  60:    */     }
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void setLog(Logger logger)
/*  64:    */   {
/*  65:156 */     this.m_logger = logger;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public abstract void stop();
/*  69:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.AbstractEvaluator
 * JD-Core Version:    0.7.0.1
 */