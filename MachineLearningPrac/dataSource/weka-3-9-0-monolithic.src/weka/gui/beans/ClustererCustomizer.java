/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.GridLayout;
/*   5:    */ import java.awt.Window;
/*   6:    */ import java.awt.event.ActionEvent;
/*   7:    */ import java.awt.event.ActionListener;
/*   8:    */ import java.beans.PropertyChangeListener;
/*   9:    */ import java.beans.PropertyChangeSupport;
/*  10:    */ import javax.swing.JButton;
/*  11:    */ import javax.swing.JPanel;
/*  12:    */ import weka.gui.GenericObjectEditor;
/*  13:    */ import weka.gui.PropertySheetPanel;
/*  14:    */ 
/*  15:    */ public class ClustererCustomizer
/*  16:    */   extends JPanel
/*  17:    */   implements BeanCustomizer, CustomizerCloseRequester
/*  18:    */ {
/*  19:    */   private static final long serialVersionUID = -2035688458149534161L;
/*  20: 55 */   private PropertyChangeSupport m_pcSupport = new PropertyChangeSupport(this);
/*  21:    */   private Clusterer m_dsClusterer;
/*  22: 60 */   private PropertySheetPanel m_ClustererEditor = new PropertySheetPanel();
/*  23:    */   private Window m_parentWindow;
/*  24:    */   private weka.clusterers.Clusterer m_backup;
/*  25:    */   private BeanCustomizer.ModifyListener m_modifyListener;
/*  26:    */   
/*  27:    */   public ClustererCustomizer()
/*  28:    */   {
/*  29: 73 */     setLayout(new BorderLayout());
/*  30: 74 */     add(this.m_ClustererEditor, "Center");
/*  31:    */     
/*  32: 76 */     JPanel butHolder = new JPanel();
/*  33: 77 */     butHolder.setLayout(new GridLayout(1, 2));
/*  34: 78 */     JButton OKBut = new JButton("OK");
/*  35: 79 */     OKBut.addActionListener(new ActionListener()
/*  36:    */     {
/*  37:    */       public void actionPerformed(ActionEvent e)
/*  38:    */       {
/*  39: 81 */         if (ClustererCustomizer.this.m_modifyListener != null) {
/*  40: 82 */           ClustererCustomizer.this.m_modifyListener.setModifiedStatus(ClustererCustomizer.this, true);
/*  41:    */         }
/*  42: 85 */         ClustererCustomizer.this.m_parentWindow.dispose();
/*  43:    */       }
/*  44: 88 */     });
/*  45: 89 */     JButton CancelBut = new JButton("Cancel");
/*  46: 90 */     CancelBut.addActionListener(new ActionListener()
/*  47:    */     {
/*  48:    */       public void actionPerformed(ActionEvent e)
/*  49:    */       {
/*  50: 94 */         if (ClustererCustomizer.this.m_backup != null) {
/*  51: 95 */           ClustererCustomizer.this.m_dsClusterer.setClusterer(ClustererCustomizer.this.m_backup);
/*  52:    */         }
/*  53: 98 */         if (ClustererCustomizer.this.m_modifyListener != null) {
/*  54: 99 */           ClustererCustomizer.this.m_modifyListener.setModifiedStatus(ClustererCustomizer.this, false);
/*  55:    */         }
/*  56:102 */         ClustererCustomizer.this.m_parentWindow.dispose();
/*  57:    */       }
/*  58:105 */     });
/*  59:106 */     butHolder.add(OKBut);
/*  60:107 */     butHolder.add(CancelBut);
/*  61:108 */     add(butHolder, "South");
/*  62:    */   }
/*  63:    */   
/*  64:    */   public void setObject(Object object)
/*  65:    */   {
/*  66:117 */     this.m_dsClusterer = ((Clusterer)object);
/*  67:    */     try
/*  68:    */     {
/*  69:119 */       this.m_backup = ((weka.clusterers.Clusterer)GenericObjectEditor.makeCopy(this.m_dsClusterer.getClusterer()));
/*  70:    */     }
/*  71:    */     catch (Exception ex) {}
/*  72:125 */     this.m_ClustererEditor.setTarget(this.m_dsClusterer.getClusterer());
/*  73:    */   }
/*  74:    */   
/*  75:    */   public void addPropertyChangeListener(PropertyChangeListener pcl)
/*  76:    */   {
/*  77:135 */     this.m_pcSupport.addPropertyChangeListener(pcl);
/*  78:    */   }
/*  79:    */   
/*  80:    */   public void removePropertyChangeListener(PropertyChangeListener pcl)
/*  81:    */   {
/*  82:144 */     this.m_pcSupport.removePropertyChangeListener(pcl);
/*  83:    */   }
/*  84:    */   
/*  85:    */   public void setParentWindow(Window parent)
/*  86:    */   {
/*  87:148 */     this.m_parentWindow = parent;
/*  88:    */   }
/*  89:    */   
/*  90:    */   public void setModifiedListener(BeanCustomizer.ModifyListener l)
/*  91:    */   {
/*  92:153 */     this.m_modifyListener = l;
/*  93:    */   }
/*  94:    */   
/*  95:    */   static {}
/*  96:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.ClustererCustomizer
 * JD-Core Version:    0.7.0.1
 */