/*  1:   */ package weka.gui.beans;
/*  2:   */ 
/*  3:   */ import java.awt.BorderLayout;
/*  4:   */ import java.beans.Customizer;
/*  5:   */ import java.beans.PropertyChangeListener;
/*  6:   */ import java.beans.PropertyChangeSupport;
/*  7:   */ import javax.swing.BorderFactory;
/*  8:   */ import javax.swing.JLabel;
/*  9:   */ import javax.swing.JPanel;
/* 10:   */ import weka.gui.PropertySheetPanel;
/* 11:   */ 
/* 12:   */ public class StripChartCustomizer
/* 13:   */   extends JPanel
/* 14:   */   implements Customizer
/* 15:   */ {
/* 16:   */   private static final long serialVersionUID = 7441741530975984608L;
/* 17:46 */   private PropertyChangeSupport m_pcSupport = new PropertyChangeSupport(this);
/* 18:49 */   private PropertySheetPanel m_cvEditor = new PropertySheetPanel();
/* 19:   */   
/* 20:   */   public StripChartCustomizer()
/* 21:   */   {
/* 22:53 */     setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
/* 23:   */     
/* 24:55 */     setLayout(new BorderLayout());
/* 25:56 */     add(this.m_cvEditor, "Center");
/* 26:57 */     add(new JLabel("StripChartCustomizer"), "North");
/* 27:   */   }
/* 28:   */   
/* 29:   */   public void setObject(Object object)
/* 30:   */   {
/* 31:67 */     this.m_cvEditor.setTarget((StripChart)object);
/* 32:   */   }
/* 33:   */   
/* 34:   */   public void addPropertyChangeListener(PropertyChangeListener pcl)
/* 35:   */   {
/* 36:76 */     this.m_pcSupport.addPropertyChangeListener(pcl);
/* 37:   */   }
/* 38:   */   
/* 39:   */   public void removePropertyChangeListener(PropertyChangeListener pcl)
/* 40:   */   {
/* 41:85 */     this.m_pcSupport.removePropertyChangeListener(pcl);
/* 42:   */   }
/* 43:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.StripChartCustomizer
 * JD-Core Version:    0.7.0.1
 */