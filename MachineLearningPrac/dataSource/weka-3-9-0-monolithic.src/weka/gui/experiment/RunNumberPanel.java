/*   1:    */ package weka.gui.experiment;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.GridLayout;
/*   6:    */ import java.awt.event.FocusAdapter;
/*   7:    */ import java.awt.event.FocusEvent;
/*   8:    */ import java.awt.event.KeyAdapter;
/*   9:    */ import java.awt.event.KeyEvent;
/*  10:    */ import java.awt.event.WindowAdapter;
/*  11:    */ import java.awt.event.WindowEvent;
/*  12:    */ import java.io.PrintStream;
/*  13:    */ import javax.swing.BorderFactory;
/*  14:    */ import javax.swing.Box;
/*  15:    */ import javax.swing.JFrame;
/*  16:    */ import javax.swing.JLabel;
/*  17:    */ import javax.swing.JPanel;
/*  18:    */ import javax.swing.JTextField;
/*  19:    */ import weka.experiment.Experiment;
/*  20:    */ 
/*  21:    */ public class RunNumberPanel
/*  22:    */   extends JPanel
/*  23:    */ {
/*  24:    */   private static final long serialVersionUID = -1644336658426067852L;
/*  25: 61 */   protected JTextField m_LowerText = new JTextField("1");
/*  26: 64 */   protected JTextField m_UpperText = new JTextField("10");
/*  27:    */   protected Experiment m_Exp;
/*  28:    */   
/*  29:    */   public RunNumberPanel()
/*  30:    */   {
/*  31: 76 */     this.m_LowerText.addKeyListener(new KeyAdapter()
/*  32:    */     {
/*  33:    */       public void keyReleased(KeyEvent e)
/*  34:    */       {
/*  35: 78 */         RunNumberPanel.this.m_Exp.setRunLower(RunNumberPanel.this.getLower());
/*  36:    */       }
/*  37: 80 */     });
/*  38: 81 */     this.m_LowerText.addFocusListener(new FocusAdapter()
/*  39:    */     {
/*  40:    */       public void focusLost(FocusEvent e)
/*  41:    */       {
/*  42: 83 */         RunNumberPanel.this.m_Exp.setRunLower(RunNumberPanel.this.getLower());
/*  43:    */       }
/*  44: 85 */     });
/*  45: 86 */     this.m_UpperText.addKeyListener(new KeyAdapter()
/*  46:    */     {
/*  47:    */       public void keyReleased(KeyEvent e)
/*  48:    */       {
/*  49: 88 */         RunNumberPanel.this.m_Exp.setRunUpper(RunNumberPanel.this.getUpper());
/*  50:    */       }
/*  51: 90 */     });
/*  52: 91 */     this.m_UpperText.addFocusListener(new FocusAdapter()
/*  53:    */     {
/*  54:    */       public void focusLost(FocusEvent e)
/*  55:    */       {
/*  56: 93 */         RunNumberPanel.this.m_Exp.setRunUpper(RunNumberPanel.this.getUpper());
/*  57:    */       }
/*  58: 95 */     });
/*  59: 96 */     this.m_LowerText.setEnabled(false);
/*  60: 97 */     this.m_UpperText.setEnabled(false);
/*  61:    */     
/*  62:    */ 
/*  63:100 */     setLayout(new GridLayout(1, 2));
/*  64:101 */     setBorder(BorderFactory.createTitledBorder("Runs"));
/*  65:102 */     Box b1 = new Box(0);
/*  66:103 */     b1.add(Box.createHorizontalStrut(10));
/*  67:104 */     b1.add(new JLabel("From:", 4));
/*  68:105 */     b1.add(Box.createHorizontalStrut(5));
/*  69:106 */     b1.add(this.m_LowerText);
/*  70:107 */     add(b1);
/*  71:108 */     Box b2 = new Box(0);
/*  72:109 */     b2.add(Box.createHorizontalStrut(10));
/*  73:110 */     b2.add(new JLabel("To:", 4));
/*  74:111 */     b2.add(Box.createHorizontalStrut(5));
/*  75:112 */     b2.add(this.m_UpperText);
/*  76:113 */     add(b2);
/*  77:    */   }
/*  78:    */   
/*  79:    */   public RunNumberPanel(Experiment exp)
/*  80:    */   {
/*  81:123 */     this();
/*  82:124 */     setExperiment(exp);
/*  83:    */   }
/*  84:    */   
/*  85:    */   public void setExperiment(Experiment exp)
/*  86:    */   {
/*  87:134 */     this.m_Exp = exp;
/*  88:135 */     this.m_LowerText.setText("" + this.m_Exp.getRunLower());
/*  89:136 */     this.m_UpperText.setText("" + this.m_Exp.getRunUpper());
/*  90:137 */     this.m_LowerText.setEnabled(true);
/*  91:138 */     this.m_UpperText.setEnabled(true);
/*  92:    */   }
/*  93:    */   
/*  94:    */   public int getLower()
/*  95:    */   {
/*  96:148 */     int result = 1;
/*  97:    */     try
/*  98:    */     {
/*  99:150 */       result = Integer.parseInt(this.m_LowerText.getText());
/* 100:    */     }
/* 101:    */     catch (Exception ex) {}
/* 102:153 */     return Math.max(1, result);
/* 103:    */   }
/* 104:    */   
/* 105:    */   public int getUpper()
/* 106:    */   {
/* 107:163 */     int result = 1;
/* 108:    */     try
/* 109:    */     {
/* 110:165 */       result = Integer.parseInt(this.m_UpperText.getText());
/* 111:    */     }
/* 112:    */     catch (Exception ex) {}
/* 113:168 */     return Math.max(1, result);
/* 114:    */   }
/* 115:    */   
/* 116:    */   public static void main(String[] args)
/* 117:    */   {
/* 118:    */     try
/* 119:    */     {
/* 120:179 */       JFrame jf = new JFrame("Dataset List Editor");
/* 121:180 */       jf.getContentPane().setLayout(new BorderLayout());
/* 122:181 */       jf.getContentPane().add(new RunNumberPanel(new Experiment()), "Center");
/* 123:    */       
/* 124:183 */       jf.addWindowListener(new WindowAdapter()
/* 125:    */       {
/* 126:    */         public void windowClosing(WindowEvent e)
/* 127:    */         {
/* 128:185 */           this.val$jf.dispose();
/* 129:186 */           System.exit(0);
/* 130:    */         }
/* 131:188 */       });
/* 132:189 */       jf.pack();
/* 133:190 */       jf.setVisible(true);
/* 134:    */     }
/* 135:    */     catch (Exception ex)
/* 136:    */     {
/* 137:192 */       ex.printStackTrace();
/* 138:193 */       System.err.println(ex.getMessage());
/* 139:    */     }
/* 140:    */   }
/* 141:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.experiment.RunNumberPanel
 * JD-Core Version:    0.7.0.1
 */