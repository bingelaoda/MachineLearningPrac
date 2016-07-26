/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BasicStroke;
/*   4:    */ import java.awt.Color;
/*   5:    */ import java.awt.Component;
/*   6:    */ import java.awt.Dimension;
/*   7:    */ import java.awt.FlowLayout;
/*   8:    */ import java.awt.Graphics;
/*   9:    */ import java.awt.Graphics2D;
/*  10:    */ import java.awt.event.ActionEvent;
/*  11:    */ import java.awt.event.ActionListener;
/*  12:    */ import java.awt.event.MouseAdapter;
/*  13:    */ import java.awt.event.MouseEvent;
/*  14:    */ import javax.swing.AbstractButton;
/*  15:    */ import javax.swing.BorderFactory;
/*  16:    */ import javax.swing.ButtonModel;
/*  17:    */ import javax.swing.JButton;
/*  18:    */ import javax.swing.JLabel;
/*  19:    */ import javax.swing.JPanel;
/*  20:    */ import javax.swing.JTabbedPane;
/*  21:    */ import javax.swing.plaf.basic.BasicButtonUI;
/*  22:    */ 
/*  23:    */ public class CloseableTabTitle
/*  24:    */   extends JPanel
/*  25:    */ {
/*  26:    */   private static final long serialVersionUID = 9178081197757118130L;
/*  27:    */   private final JTabbedPane m_enclosingPane;
/*  28:    */   private JLabel m_tabLabel;
/*  29:    */   private TabButton m_tabButton;
/*  30:    */   private ClosingCallback m_callback;
/*  31: 80 */   private String m_closeAccelleratorText = "";
/*  32:    */   
/*  33:    */   public CloseableTabTitle(JTabbedPane pane, String closeAccelleratorText, ClosingCallback callback)
/*  34:    */   {
/*  35: 90 */     super(new FlowLayout(0, 0, 0));
/*  36: 92 */     if (closeAccelleratorText != null) {
/*  37: 93 */       this.m_closeAccelleratorText = closeAccelleratorText;
/*  38:    */     }
/*  39: 95 */     this.m_enclosingPane = pane;
/*  40: 96 */     setOpaque(false);
/*  41: 97 */     setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
/*  42:    */     
/*  43:    */ 
/*  44:100 */     this.m_tabLabel = new JLabel()
/*  45:    */     {
/*  46:    */       private static final long serialVersionUID = 8515052190461050324L;
/*  47:    */       
/*  48:    */       public String getText()
/*  49:    */       {
/*  50:106 */         int index = CloseableTabTitle.this.m_enclosingPane.indexOfTabComponent(CloseableTabTitle.this);
/*  51:107 */         if (index >= 0) {
/*  52:108 */           return CloseableTabTitle.this.m_enclosingPane.getTitleAt(index);
/*  53:    */         }
/*  54:110 */         return null;
/*  55:    */       }
/*  56:113 */     };
/*  57:114 */     add(this.m_tabLabel);
/*  58:115 */     this.m_tabLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
/*  59:116 */     this.m_tabButton = new TabButton();
/*  60:117 */     add(this.m_tabButton);
/*  61:118 */     this.m_callback = callback;
/*  62:119 */     this.m_tabLabel.setEnabled(false);
/*  63:    */   }
/*  64:    */   
/*  65:    */   public void setBold(boolean bold)
/*  66:    */   {
/*  67:128 */     this.m_tabLabel.setEnabled(bold);
/*  68:    */   }
/*  69:    */   
/*  70:    */   public void setButtonEnabled(boolean enabled)
/*  71:    */   {
/*  72:137 */     this.m_tabButton.setEnabled(enabled);
/*  73:    */   }
/*  74:    */   
/*  75:    */   private class TabButton
/*  76:    */     extends JButton
/*  77:    */     implements ActionListener
/*  78:    */   {
/*  79:    */     private static final long serialVersionUID = -4915800749132175968L;
/*  80:    */     
/*  81:    */     public TabButton()
/*  82:    */     {
/*  83:149 */       int size = 17;
/*  84:150 */       setPreferredSize(new Dimension(size, size));
/*  85:151 */       setToolTipText("close this tab");
/*  86:    */       
/*  87:153 */       setUI(new BasicButtonUI());
/*  88:    */       
/*  89:155 */       setContentAreaFilled(false);
/*  90:    */       
/*  91:157 */       setFocusable(false);
/*  92:158 */       setBorder(BorderFactory.createEtchedBorder());
/*  93:159 */       setBorderPainted(false);
/*  94:    */       
/*  95:    */ 
/*  96:162 */       addMouseListener(new MouseAdapter()
/*  97:    */       {
/*  98:    */         public void mouseEntered(MouseEvent e)
/*  99:    */         {
/* 100:165 */           Component component = e.getComponent();
/* 101:167 */           if ((component instanceof AbstractButton))
/* 102:    */           {
/* 103:168 */             AbstractButton button = (AbstractButton)component;
/* 104:169 */             button.setBorderPainted(true);
/* 105:    */             
/* 106:171 */             int i = CloseableTabTitle.this.m_enclosingPane.indexOfTabComponent(CloseableTabTitle.this);
/* 107:172 */             if (i == CloseableTabTitle.this.m_enclosingPane.getSelectedIndex()) {
/* 108:173 */               button.setToolTipText("close this tab " + CloseableTabTitle.this.m_closeAccelleratorText);
/* 109:    */             } else {
/* 110:176 */               button.setToolTipText("close this tab");
/* 111:    */             }
/* 112:    */           }
/* 113:    */         }
/* 114:    */         
/* 115:    */         public void mouseExited(MouseEvent e)
/* 116:    */         {
/* 117:183 */           Component component = e.getComponent();
/* 118:184 */           if ((component instanceof AbstractButton))
/* 119:    */           {
/* 120:185 */             AbstractButton button = (AbstractButton)component;
/* 121:186 */             button.setBorderPainted(false);
/* 122:    */           }
/* 123:    */         }
/* 124:189 */       });
/* 125:190 */       setRolloverEnabled(true);
/* 126:    */       
/* 127:192 */       addActionListener(this);
/* 128:    */     }
/* 129:    */     
/* 130:    */     public void actionPerformed(ActionEvent e)
/* 131:    */     {
/* 132:197 */       int i = CloseableTabTitle.this.m_enclosingPane.indexOfTabComponent(CloseableTabTitle.this);
/* 133:198 */       if ((i >= 0) && 
/* 134:199 */         (CloseableTabTitle.this.m_callback != null)) {
/* 135:200 */         CloseableTabTitle.this.m_callback.tabClosing(i);
/* 136:    */       }
/* 137:    */     }
/* 138:    */     
/* 139:    */     public void updateUI() {}
/* 140:    */     
/* 141:    */     protected void paintComponent(Graphics g)
/* 142:    */     {
/* 143:213 */       super.paintComponent(g);
/* 144:214 */       Graphics2D g2 = (Graphics2D)g.create();
/* 145:216 */       if (getModel().isPressed()) {
/* 146:217 */         g2.translate(1, 1);
/* 147:    */       }
/* 148:219 */       g2.setStroke(new BasicStroke(2.0F));
/* 149:220 */       g2.setColor(Color.BLACK);
/* 150:221 */       if (!isEnabled()) {
/* 151:222 */         g2.setColor(Color.GRAY);
/* 152:    */       }
/* 153:224 */       if (getModel().isRollover()) {
/* 154:225 */         g2.setColor(Color.MAGENTA);
/* 155:    */       }
/* 156:227 */       int delta = 6;
/* 157:228 */       g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
/* 158:229 */       g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
/* 159:230 */       g2.dispose();
/* 160:    */     }
/* 161:    */   }
/* 162:    */   
/* 163:    */   public static abstract interface ClosingCallback
/* 164:    */   {
/* 165:    */     public abstract void tabClosing(int paramInt);
/* 166:    */   }
/* 167:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.CloseableTabTitle
 * JD-Core Version:    0.7.0.1
 */