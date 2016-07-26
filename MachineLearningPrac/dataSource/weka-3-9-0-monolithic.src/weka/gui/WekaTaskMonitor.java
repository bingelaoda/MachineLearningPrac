/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.Dimension;
/*   6:    */ import java.awt.Image;
/*   7:    */ import java.awt.Toolkit;
/*   8:    */ import java.awt.event.WindowAdapter;
/*   9:    */ import java.awt.event.WindowEvent;
/*  10:    */ import java.io.PrintStream;
/*  11:    */ import java.net.URL;
/*  12:    */ import javax.swing.BorderFactory;
/*  13:    */ import javax.swing.ImageIcon;
/*  14:    */ import javax.swing.JFrame;
/*  15:    */ import javax.swing.JLabel;
/*  16:    */ import javax.swing.JPanel;
/*  17:    */ 
/*  18:    */ public class WekaTaskMonitor
/*  19:    */   extends JPanel
/*  20:    */   implements TaskLogger
/*  21:    */ {
/*  22:    */   private static final long serialVersionUID = 508309816292197578L;
/*  23: 50 */   private int m_ActiveTasks = 0;
/*  24:    */   private JLabel m_MonitorLabel;
/*  25:    */   private ImageIcon m_iconStationary;
/*  26:    */   private ImageIcon m_iconAnimated;
/*  27: 62 */   private boolean m_animating = false;
/*  28:    */   
/*  29:    */   public WekaTaskMonitor()
/*  30:    */   {
/*  31: 68 */     URL imageURL = getClass().getClassLoader().getResource("weka/gui/weka_stationary.gif");
/*  32: 71 */     if (imageURL != null)
/*  33:    */     {
/*  34: 72 */       Image pic = Toolkit.getDefaultToolkit().getImage(imageURL);
/*  35: 73 */       imageURL = getClass().getClassLoader().getResource("weka/gui/weka_animated.gif");
/*  36:    */       
/*  37: 75 */       Image pic2 = Toolkit.getDefaultToolkit().getImage(imageURL);
/*  38:    */       
/*  39:    */ 
/*  40:    */ 
/*  41:    */ 
/*  42:    */ 
/*  43:    */ 
/*  44: 82 */       this.m_iconStationary = new ImageIcon(pic);
/*  45: 83 */       this.m_iconAnimated = new ImageIcon(pic2);
/*  46:    */     }
/*  47: 86 */     this.m_MonitorLabel = new JLabel(" x " + this.m_ActiveTasks, this.m_iconStationary, 0);
/*  48:    */     
/*  49:    */ 
/*  50:    */ 
/*  51:    */ 
/*  52:    */ 
/*  53:    */ 
/*  54: 93 */     setLayout(new BorderLayout());
/*  55: 94 */     Dimension d = this.m_MonitorLabel.getPreferredSize();
/*  56: 95 */     this.m_MonitorLabel.setPreferredSize(new Dimension(d.width + 15, d.height));
/*  57: 96 */     this.m_MonitorLabel.setMinimumSize(new Dimension(d.width + 15, d.height));
/*  58: 97 */     add(this.m_MonitorLabel, "Center");
/*  59:    */   }
/*  60:    */   
/*  61:    */   public synchronized void taskStarted()
/*  62:    */   {
/*  63:106 */     this.m_ActiveTasks += 1;
/*  64:107 */     updateMonitor();
/*  65:    */   }
/*  66:    */   
/*  67:    */   public synchronized void taskFinished()
/*  68:    */   {
/*  69:114 */     this.m_ActiveTasks -= 1;
/*  70:115 */     if (this.m_ActiveTasks < 0) {
/*  71:116 */       this.m_ActiveTasks = 0;
/*  72:    */     }
/*  73:118 */     updateMonitor();
/*  74:    */   }
/*  75:    */   
/*  76:    */   private void updateMonitor()
/*  77:    */   {
/*  78:126 */     this.m_MonitorLabel.setText(" x " + this.m_ActiveTasks);
/*  79:127 */     if ((this.m_ActiveTasks > 0) && (!this.m_animating))
/*  80:    */     {
/*  81:128 */       this.m_MonitorLabel.setIcon(this.m_iconAnimated);
/*  82:129 */       this.m_animating = true;
/*  83:    */     }
/*  84:132 */     if ((this.m_ActiveTasks == 0) && (this.m_animating))
/*  85:    */     {
/*  86:133 */       this.m_MonitorLabel.setIcon(this.m_iconStationary);
/*  87:134 */       this.m_animating = false;
/*  88:    */     }
/*  89:    */   }
/*  90:    */   
/*  91:    */   public static void main(String[] args)
/*  92:    */   {
/*  93:    */     try
/*  94:    */     {
/*  95:144 */       JFrame jf = new JFrame();
/*  96:145 */       jf.getContentPane().setLayout(new BorderLayout());
/*  97:146 */       WekaTaskMonitor tm = new WekaTaskMonitor();
/*  98:147 */       tm.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Weka Tasks"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
/*  99:    */       
/* 100:    */ 
/* 101:    */ 
/* 102:151 */       jf.getContentPane().add(tm, "Center");
/* 103:152 */       jf.addWindowListener(new WindowAdapter()
/* 104:    */       {
/* 105:    */         public void windowClosing(WindowEvent e)
/* 106:    */         {
/* 107:154 */           this.val$jf.dispose();
/* 108:155 */           System.exit(0);
/* 109:    */         }
/* 110:157 */       });
/* 111:158 */       jf.pack();
/* 112:159 */       jf.setVisible(true);
/* 113:160 */       tm.taskStarted();
/* 114:    */     }
/* 115:    */     catch (Exception ex)
/* 116:    */     {
/* 117:162 */       ex.printStackTrace();
/* 118:163 */       System.err.println(ex.getMessage());
/* 119:    */     }
/* 120:    */   }
/* 121:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.WekaTaskMonitor
 * JD-Core Version:    0.7.0.1
 */