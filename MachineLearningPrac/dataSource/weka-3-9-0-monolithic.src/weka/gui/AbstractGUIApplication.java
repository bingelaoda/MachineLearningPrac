/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import javax.swing.JFrame;
/*   6:    */ import javax.swing.JOptionPane;
/*   7:    */ import javax.swing.JPanel;
/*   8:    */ import javax.swing.JScrollPane;
/*   9:    */ import javax.swing.JTextArea;
/*  10:    */ import weka.core.Settings;
/*  11:    */ import weka.knowledgeflow.LogManager;
/*  12:    */ 
/*  13:    */ public abstract class AbstractGUIApplication
/*  14:    */   extends JPanel
/*  15:    */   implements GUIApplication
/*  16:    */ {
/*  17:    */   private static final long serialVersionUID = -2116770422043462730L;
/*  18:    */   protected PerspectiveManager m_perspectiveManager;
/*  19:    */   protected Settings m_applicationSettings;
/*  20:    */   
/*  21:    */   public AbstractGUIApplication()
/*  22:    */   {
/*  23: 51 */     this(true, new String[0]);
/*  24:    */   }
/*  25:    */   
/*  26:    */   public AbstractGUIApplication(boolean layoutComponent, String[] allowedPerspectiveClassPrefixes, String[] disallowedPerspectiveClassPrefixes)
/*  27:    */   {
/*  28: 74 */     this.m_perspectiveManager = new PerspectiveManager(this, allowedPerspectiveClassPrefixes, disallowedPerspectiveClassPrefixes);
/*  29:    */     
/*  30:    */ 
/*  31: 77 */     this.m_perspectiveManager.setMainApplicationForAllPerspectives();
/*  32: 78 */     if (layoutComponent)
/*  33:    */     {
/*  34: 79 */       setLayout(new BorderLayout());
/*  35: 80 */       add(this.m_perspectiveManager, "Center");
/*  36: 81 */       if (this.m_perspectiveManager.perspectiveToolBarIsVisible()) {
/*  37: 82 */         add(this.m_perspectiveManager.getPerspectiveToolBar(), "North");
/*  38:    */       }
/*  39:    */     }
/*  40:    */   }
/*  41:    */   
/*  42:    */   public AbstractGUIApplication(boolean layoutComponent, String... allowedPerspectiveClassPrefixes)
/*  43:    */   {
/*  44:101 */     this(layoutComponent, allowedPerspectiveClassPrefixes, new String[0]);
/*  45:    */   }
/*  46:    */   
/*  47:    */   public PerspectiveManager getPerspectiveManager()
/*  48:    */   {
/*  49:111 */     return this.m_perspectiveManager;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public Settings getApplicationSettings()
/*  53:    */   {
/*  54:121 */     if (this.m_applicationSettings == null)
/*  55:    */     {
/*  56:122 */       this.m_applicationSettings = new Settings("weka", getApplicationID());
/*  57:123 */       this.m_applicationSettings.applyDefaults(getApplicationDefaults());
/*  58:    */     }
/*  59:125 */     return this.m_applicationSettings;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public boolean isPerspectivesToolBarVisible()
/*  63:    */   {
/*  64:135 */     return this.m_perspectiveManager.perspectiveToolBarIsVisible();
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void hidePerspectivesToolBar()
/*  68:    */   {
/*  69:143 */     if (isPerspectivesToolBarVisible())
/*  70:    */     {
/*  71:144 */       this.m_perspectiveManager.setPerspectiveToolBarIsVisible(false);
/*  72:145 */       remove(this.m_perspectiveManager.getPerspectiveToolBar());
/*  73:    */     }
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void showPerspectivesToolBar()
/*  77:    */   {
/*  78:154 */     if (!isPerspectivesToolBarVisible())
/*  79:    */     {
/*  80:155 */       this.m_perspectiveManager.setPerspectiveToolBarIsVisible(true);
/*  81:156 */       add(this.m_perspectiveManager.getPerspectiveToolBar(), "North");
/*  82:    */     }
/*  83:    */   }
/*  84:    */   
/*  85:    */   public void settingsChanged() {}
/*  86:    */   
/*  87:    */   public void showMenuBar(JFrame topLevelAncestor)
/*  88:    */   {
/*  89:175 */     this.m_perspectiveManager.showMenuBar(topLevelAncestor);
/*  90:    */   }
/*  91:    */   
/*  92:    */   public void showErrorDialog(Exception cause)
/*  93:    */   {
/*  94:185 */     String stackTrace = LogManager.stackTraceToString(cause);
/*  95:    */     
/*  96:187 */     Object[] options = null;
/*  97:189 */     if ((stackTrace != null) && (stackTrace.length() > 0))
/*  98:    */     {
/*  99:190 */       options = new Object[2];
/* 100:191 */       options[0] = "OK";
/* 101:192 */       options[1] = "Show error";
/* 102:    */     }
/* 103:    */     else
/* 104:    */     {
/* 105:194 */       options = new Object[1];
/* 106:195 */       options[0] = "OK";
/* 107:    */     }
/* 108:197 */     int result = JOptionPane.showOptionDialog(this, "An error has occurred: " + cause.getMessage(), getApplicationName(), 0, 0, null, options, options[0]);
/* 109:203 */     if (result == 1)
/* 110:    */     {
/* 111:204 */       JTextArea jt = new JTextArea(stackTrace, 10, 40);
/* 112:205 */       JOptionPane.showMessageDialog(this, new JScrollPane(jt), getApplicationName(), 0);
/* 113:    */     }
/* 114:    */   }
/* 115:    */   
/* 116:    */   public void showInfoDialog(Object information, String title, boolean isWarning)
/* 117:    */   {
/* 118:220 */     JOptionPane.showMessageDialog(this, information, title, isWarning ? 2 : 1);
/* 119:    */   }
/* 120:    */   
/* 121:    */   public void revalidate()
/* 122:    */   {
/* 123:231 */     if (getTopLevelAncestor() != null)
/* 124:    */     {
/* 125:232 */       getTopLevelAncestor().revalidate();
/* 126:233 */       getTopLevelAncestor().repaint();
/* 127:    */     }
/* 128:    */     else
/* 129:    */     {
/* 130:235 */       super.revalidate();
/* 131:    */     }
/* 132:237 */     repaint();
/* 133:    */   }
/* 134:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.AbstractGUIApplication
 * JD-Core Version:    0.7.0.1
 */