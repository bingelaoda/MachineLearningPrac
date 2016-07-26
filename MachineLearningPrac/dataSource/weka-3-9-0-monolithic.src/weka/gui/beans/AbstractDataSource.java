/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.beans.PropertyChangeListener;
/*   5:    */ import java.beans.VetoableChangeListener;
/*   6:    */ import java.beans.beancontext.BeanContext;
/*   7:    */ import java.beans.beancontext.BeanContextChild;
/*   8:    */ import java.beans.beancontext.BeanContextChildSupport;
/*   9:    */ import java.io.Serializable;
/*  10:    */ import java.util.EventListener;
/*  11:    */ import java.util.Vector;
/*  12:    */ import javax.swing.JPanel;
/*  13:    */ 
/*  14:    */ public abstract class AbstractDataSource
/*  15:    */   extends JPanel
/*  16:    */   implements DataSource, Visible, Serializable, BeanContextChild
/*  17:    */ {
/*  18:    */   private static final long serialVersionUID = -4127257701890044793L;
/*  19:    */   protected boolean m_design;
/*  20: 60 */   protected transient BeanContext m_beanContext = null;
/*  21: 65 */   protected BeanContextChildSupport m_bcSupport = new BeanContextChildSupport(this);
/*  22: 71 */   protected BeanVisual m_visual = new BeanVisual("AbstractDataSource", "weka/gui/beans/icons/DefaultDataSource.gif", "weka/gui/beans/icons/DefaultDataSource_animated.gif");
/*  23:    */   protected Vector<EventListener> m_listeners;
/*  24:    */   
/*  25:    */   public AbstractDataSource()
/*  26:    */   {
/*  27: 85 */     useDefaultVisual();
/*  28: 86 */     setLayout(new BorderLayout());
/*  29: 87 */     add(this.m_visual, "Center");
/*  30: 88 */     this.m_listeners = new Vector();
/*  31:    */   }
/*  32:    */   
/*  33:    */   public synchronized void addDataSourceListener(DataSourceListener dsl)
/*  34:    */   {
/*  35: 98 */     this.m_listeners.addElement(dsl);
/*  36:    */   }
/*  37:    */   
/*  38:    */   public synchronized void removeDataSourceListener(DataSourceListener dsl)
/*  39:    */   {
/*  40:108 */     this.m_listeners.remove(dsl);
/*  41:    */   }
/*  42:    */   
/*  43:    */   public synchronized void addInstanceListener(InstanceListener dsl)
/*  44:    */   {
/*  45:118 */     this.m_listeners.add(dsl);
/*  46:    */   }
/*  47:    */   
/*  48:    */   public synchronized void removeInstanceListener(InstanceListener dsl)
/*  49:    */   {
/*  50:128 */     this.m_listeners.remove(dsl);
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void setVisual(BeanVisual newVisual)
/*  54:    */   {
/*  55:138 */     this.m_visual = newVisual;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public BeanVisual getVisual()
/*  59:    */   {
/*  60:147 */     return this.m_visual;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void useDefaultVisual()
/*  64:    */   {
/*  65:156 */     this.m_visual.loadIcons("weka/gui/beans/icons/DefaultDataSource.gif", "weka/gui/beans/icons/DefaultDataSource_animated.gif");
/*  66:    */   }
/*  67:    */   
/*  68:    */   public void setBeanContext(BeanContext bc)
/*  69:    */   {
/*  70:167 */     this.m_beanContext = bc;
/*  71:168 */     this.m_design = this.m_beanContext.isDesignTime();
/*  72:    */   }
/*  73:    */   
/*  74:    */   public BeanContext getBeanContext()
/*  75:    */   {
/*  76:178 */     return this.m_beanContext;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public void addPropertyChangeListener(String name, PropertyChangeListener pcl)
/*  80:    */   {
/*  81:189 */     this.m_bcSupport.addPropertyChangeListener(name, pcl);
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void removePropertyChangeListener(String name, PropertyChangeListener pcl)
/*  85:    */   {
/*  86:201 */     this.m_bcSupport.removePropertyChangeListener(name, pcl);
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void addVetoableChangeListener(String name, VetoableChangeListener vcl)
/*  90:    */   {
/*  91:212 */     this.m_bcSupport.addVetoableChangeListener(name, vcl);
/*  92:    */   }
/*  93:    */   
/*  94:    */   public void removeVetoableChangeListener(String name, VetoableChangeListener vcl)
/*  95:    */   {
/*  96:224 */     this.m_bcSupport.removeVetoableChangeListener(name, vcl);
/*  97:    */   }
/*  98:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.AbstractDataSource
 * JD-Core Version:    0.7.0.1
 */