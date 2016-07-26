/*   1:    */ package weka.gui.scripting;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Color;
/*   5:    */ import java.awt.Container;
/*   6:    */ import java.awt.Dialog;
/*   7:    */ import java.awt.Dimension;
/*   8:    */ import java.awt.Frame;
/*   9:    */ import java.io.File;
/*  10:    */ import java.io.InputStreamReader;
/*  11:    */ import java.io.PipedInputStream;
/*  12:    */ import java.io.PipedOutputStream;
/*  13:    */ import java.io.PrintStream;
/*  14:    */ import java.io.Reader;
/*  15:    */ import java.util.HashSet;
/*  16:    */ import java.util.Iterator;
/*  17:    */ import javax.swing.ImageIcon;
/*  18:    */ import javax.swing.JFrame;
/*  19:    */ import javax.swing.JMenuBar;
/*  20:    */ import javax.swing.JPanel;
/*  21:    */ import javax.swing.JTextPane;
/*  22:    */ import weka.core.Tee;
/*  23:    */ import weka.gui.PropertyDialog;
/*  24:    */ import weka.gui.ReaderToTextPane;
/*  25:    */ import weka.gui.scripting.event.TitleUpdatedEvent;
/*  26:    */ import weka.gui.scripting.event.TitleUpdatedListener;
/*  27:    */ 
/*  28:    */ public abstract class ScriptingPanel
/*  29:    */   extends JPanel
/*  30:    */   implements TitleUpdatedListener
/*  31:    */ {
/*  32:    */   private static final long serialVersionUID = 7593091442691911406L;
/*  33:    */   protected PipedOutputStream m_POO;
/*  34:    */   protected PipedOutputStream m_POE;
/*  35:    */   protected ReaderToTextPane m_OutRedirector;
/*  36:    */   protected ReaderToTextPane m_ErrRedirector;
/*  37:    */   protected boolean m_Debug;
/*  38:    */   protected HashSet<TitleUpdatedListener> m_TitleUpdatedListeners;
/*  39:    */   
/*  40:    */   public ScriptingPanel()
/*  41:    */   {
/*  42: 84 */     initialize();
/*  43: 85 */     initGUI();
/*  44: 86 */     initFinish();
/*  45:    */   }
/*  46:    */   
/*  47:    */   protected void initialize()
/*  48:    */   {
/*  49: 93 */     this.m_POO = new PipedOutputStream();
/*  50: 94 */     this.m_POE = new PipedOutputStream();
/*  51: 95 */     this.m_Debug = false;
/*  52: 96 */     this.m_TitleUpdatedListeners = new HashSet();
/*  53:    */   }
/*  54:    */   
/*  55:    */   protected void initGUI() {}
/*  56:    */   
/*  57:    */   protected void initFinish()
/*  58:    */   {
/*  59:    */     try
/*  60:    */     {
/*  61:120 */       PipedInputStream pio = new PipedInputStream(this.m_POO);
/*  62:121 */       Tee teeOut = new Tee(System.out);
/*  63:122 */       System.setOut(teeOut);
/*  64:123 */       teeOut.add(new PrintStream(this.m_POO));
/*  65:124 */       Reader reader = new InputStreamReader(pio);
/*  66:125 */       this.m_OutRedirector = new ReaderToTextPane(reader, getOutput(), Color.BLACK);
/*  67:126 */       this.m_OutRedirector.start();
/*  68:    */     }
/*  69:    */     catch (Exception e)
/*  70:    */     {
/*  71:129 */       System.err.println("Error redirecting stdout");
/*  72:130 */       e.printStackTrace();
/*  73:131 */       this.m_OutRedirector = null;
/*  74:    */     }
/*  75:    */     try
/*  76:    */     {
/*  77:136 */       PipedInputStream pie = new PipedInputStream(this.m_POE);
/*  78:137 */       Tee teeErr = new Tee(System.err);
/*  79:138 */       System.setErr(teeErr);
/*  80:139 */       teeErr.add(new PrintStream(this.m_POE));
/*  81:140 */       Reader reader = new InputStreamReader(pie);
/*  82:141 */       this.m_ErrRedirector = new ReaderToTextPane(reader, getOutput(), Color.RED);
/*  83:142 */       this.m_ErrRedirector.start();
/*  84:    */     }
/*  85:    */     catch (Exception e)
/*  86:    */     {
/*  87:145 */       System.err.println("Error redirecting stderr");
/*  88:146 */       e.printStackTrace();
/*  89:147 */       this.m_ErrRedirector = null;
/*  90:    */     }
/*  91:150 */     addTitleUpdatedListener(this);
/*  92:    */   }
/*  93:    */   
/*  94:    */   public abstract ImageIcon getIcon();
/*  95:    */   
/*  96:    */   public abstract String getTitle();
/*  97:    */   
/*  98:    */   public abstract JTextPane getOutput();
/*  99:    */   
/* 100:    */   public abstract JMenuBar getMenuBar();
/* 101:    */   
/* 102:    */   public void setDebug(boolean value)
/* 103:    */   {
/* 104:188 */     this.m_Debug = value;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public boolean getDebug()
/* 108:    */   {
/* 109:197 */     return this.m_Debug;
/* 110:    */   }
/* 111:    */   
/* 112:    */   public void addTitleUpdatedListener(TitleUpdatedListener l)
/* 113:    */   {
/* 114:206 */     this.m_TitleUpdatedListeners.add(l);
/* 115:    */   }
/* 116:    */   
/* 117:    */   public void removeTitleUpdatedListener(TitleUpdatedListener l)
/* 118:    */   {
/* 119:215 */     this.m_TitleUpdatedListeners.remove(l);
/* 120:    */   }
/* 121:    */   
/* 122:    */   protected void notifyTitleUpdatedListeners(TitleUpdatedEvent e)
/* 123:    */   {
/* 124:226 */     Iterator<TitleUpdatedListener> iter = this.m_TitleUpdatedListeners.iterator();
/* 125:227 */     while (iter.hasNext()) {
/* 126:228 */       ((TitleUpdatedListener)iter.next()).titleUpdated(e);
/* 127:    */     }
/* 128:    */   }
/* 129:    */   
/* 130:    */   public void titleUpdated(TitleUpdatedEvent event)
/* 131:    */   {
/* 132:237 */     if (PropertyDialog.getParentDialog(this) != null) {
/* 133:238 */       PropertyDialog.getParentDialog(this).setTitle(getTitle());
/* 134:239 */     } else if (PropertyDialog.getParentFrame(this) != null) {
/* 135:240 */       PropertyDialog.getParentFrame(this).setTitle(getTitle());
/* 136:    */     }
/* 137:    */   }
/* 138:    */   
/* 139:    */   public static void showPanel(ScriptingPanel panel, String[] args)
/* 140:    */   {
/* 141:250 */     showPanel(panel, args, 800, 600);
/* 142:    */   }
/* 143:    */   
/* 144:    */   public static void showPanel(ScriptingPanel panel, String[] args, int width, int height)
/* 145:    */   {
/* 146:    */     try
/* 147:    */     {
/* 148:263 */       JFrame frame = new JFrame();
/* 149:264 */       frame.getContentPane().setLayout(new BorderLayout());
/* 150:265 */       frame.getContentPane().add(panel, "Center");
/* 151:266 */       frame.setJMenuBar(panel.getMenuBar());
/* 152:267 */       frame.setSize(new Dimension(width, height));
/* 153:268 */       frame.setDefaultCloseOperation(3);
/* 154:269 */       frame.setTitle(panel.getTitle());
/* 155:270 */       frame.setIconImage(panel.getIcon().getImage());
/* 156:271 */       frame.setLocationRelativeTo(null);
/* 157:272 */       if ((args.length > 0) && ((panel instanceof FileScriptingPanel))) {
/* 158:273 */         ((FileScriptingPanel)panel).open(new File(args[0]));
/* 159:    */       }
/* 160:274 */       frame.setVisible(true);
/* 161:    */     }
/* 162:    */     catch (Exception e)
/* 163:    */     {
/* 164:277 */       e.printStackTrace();
/* 165:    */     }
/* 166:    */   }
/* 167:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.scripting.ScriptingPanel
 * JD-Core Version:    0.7.0.1
 */