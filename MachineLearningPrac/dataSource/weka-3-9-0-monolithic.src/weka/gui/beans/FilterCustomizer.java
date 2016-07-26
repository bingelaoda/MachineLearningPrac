/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.GridLayout;
/*   5:    */ import java.awt.Window;
/*   6:    */ import java.awt.event.ActionEvent;
/*   7:    */ import java.awt.event.ActionListener;
/*   8:    */ import java.beans.PropertyChangeListener;
/*   9:    */ import java.beans.PropertyChangeSupport;
/*  10:    */ import javax.swing.BorderFactory;
/*  11:    */ import javax.swing.JButton;
/*  12:    */ import javax.swing.JPanel;
/*  13:    */ import weka.gui.GenericObjectEditor;
/*  14:    */ import weka.gui.PropertySheetPanel;
/*  15:    */ 
/*  16:    */ public class FilterCustomizer
/*  17:    */   extends JPanel
/*  18:    */   implements BeanCustomizer, CustomizerCloseRequester
/*  19:    */ {
/*  20:    */   private static final long serialVersionUID = 2049895469240109738L;
/*  21: 55 */   private final PropertyChangeSupport m_pcSupport = new PropertyChangeSupport(this);
/*  22:    */   private Filter m_filter;
/*  23:    */   private weka.filters.Filter m_backup;
/*  24: 66 */   private final PropertySheetPanel m_filterEditor = new PropertySheetPanel();
/*  25:    */   private Window m_parentWindow;
/*  26:    */   private BeanCustomizer.ModifyListener m_modifyListener;
/*  27:    */   
/*  28:    */   public FilterCustomizer()
/*  29:    */   {
/*  30: 73 */     this.m_filterEditor.setBorder(BorderFactory.createTitledBorder("Filter options"));
/*  31:    */     
/*  32:    */ 
/*  33: 76 */     setLayout(new BorderLayout());
/*  34: 77 */     add(this.m_filterEditor, "Center");
/*  35:    */     
/*  36: 79 */     JPanel butHolder = new JPanel();
/*  37: 80 */     butHolder.setLayout(new GridLayout(1, 2));
/*  38: 81 */     JButton OKBut = new JButton("OK");
/*  39: 82 */     OKBut.addActionListener(new ActionListener()
/*  40:    */     {
/*  41:    */       public void actionPerformed(ActionEvent e)
/*  42:    */       {
/*  43: 88 */         FilterCustomizer.this.m_filterEditor.closingOK();
/*  44: 90 */         if (FilterCustomizer.this.m_modifyListener != null) {
/*  45: 91 */           FilterCustomizer.this.m_modifyListener.setModifiedStatus(FilterCustomizer.this, true);
/*  46:    */         }
/*  47: 94 */         FilterCustomizer.this.m_parentWindow.dispose();
/*  48:    */       }
/*  49: 97 */     });
/*  50: 98 */     JButton CancelBut = new JButton("Cancel");
/*  51: 99 */     CancelBut.addActionListener(new ActionListener()
/*  52:    */     {
/*  53:    */       public void actionPerformed(ActionEvent e)
/*  54:    */       {
/*  55:105 */         FilterCustomizer.this.m_filterEditor.closingCancel();
/*  56:109 */         if (FilterCustomizer.this.m_backup != null) {
/*  57:110 */           FilterCustomizer.this.m_filter.setFilter(FilterCustomizer.this.m_backup);
/*  58:    */         }
/*  59:113 */         if (FilterCustomizer.this.m_modifyListener != null) {
/*  60:114 */           FilterCustomizer.this.m_modifyListener.setModifiedStatus(FilterCustomizer.this, false);
/*  61:    */         }
/*  62:116 */         FilterCustomizer.this.m_parentWindow.dispose();
/*  63:    */       }
/*  64:119 */     });
/*  65:120 */     butHolder.add(OKBut);
/*  66:121 */     butHolder.add(CancelBut);
/*  67:122 */     add(butHolder, "South");
/*  68:    */   }
/*  69:    */   
/*  70:    */   public void setObject(Object object)
/*  71:    */   {
/*  72:132 */     this.m_filter = ((Filter)object);
/*  73:    */     try
/*  74:    */     {
/*  75:134 */       this.m_backup = ((weka.filters.Filter)GenericObjectEditor.makeCopy(this.m_filter.getFilter()));
/*  76:    */     }
/*  77:    */     catch (Exception ex) {}
/*  78:139 */     this.m_filterEditor.setTarget(this.m_filter.getFilter());
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void addPropertyChangeListener(PropertyChangeListener pcl)
/*  82:    */   {
/*  83:149 */     this.m_pcSupport.addPropertyChangeListener(pcl);
/*  84:    */   }
/*  85:    */   
/*  86:    */   public void removePropertyChangeListener(PropertyChangeListener pcl)
/*  87:    */   {
/*  88:159 */     this.m_pcSupport.removePropertyChangeListener(pcl);
/*  89:    */   }
/*  90:    */   
/*  91:    */   public void setParentWindow(Window parent)
/*  92:    */   {
/*  93:164 */     this.m_parentWindow = parent;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public void setModifiedListener(BeanCustomizer.ModifyListener l)
/*  97:    */   {
/*  98:169 */     this.m_modifyListener = l;
/*  99:    */   }
/* 100:    */   
/* 101:    */   static {}
/* 102:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.FilterCustomizer
 * JD-Core Version:    0.7.0.1
 */