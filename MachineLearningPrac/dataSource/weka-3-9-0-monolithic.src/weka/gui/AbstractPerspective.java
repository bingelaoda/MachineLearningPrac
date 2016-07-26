/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import javax.swing.Icon;
/*   6:    */ import javax.swing.JMenu;
/*   7:    */ import javax.swing.JPanel;
/*   8:    */ import weka.core.Defaults;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.gui.knowledgeflow.StepVisual;
/*  11:    */ 
/*  12:    */ public abstract class AbstractPerspective
/*  13:    */   extends JPanel
/*  14:    */   implements Perspective
/*  15:    */ {
/*  16:    */   private static final long serialVersionUID = 1919714661641262879L;
/*  17:    */   protected boolean m_isActive;
/*  18:    */   protected boolean m_isLoaded;
/*  19:    */   protected GUIApplication m_mainApplication;
/*  20: 57 */   protected String m_perspectiveTitle = "";
/*  21: 60 */   protected String m_perspectiveID = "";
/*  22: 63 */   protected String m_perspectiveTipText = "";
/*  23:    */   protected Icon m_perspectiveIcon;
/*  24:    */   protected Logger m_log;
/*  25:    */   
/*  26:    */   public AbstractPerspective() {}
/*  27:    */   
/*  28:    */   public AbstractPerspective(String ID, String title)
/*  29:    */   {
/*  30: 84 */     this.m_perspectiveTitle = title;
/*  31: 85 */     this.m_perspectiveID = ID;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public void instantiationComplete() {}
/*  35:    */   
/*  36:    */   public boolean okToBeActive()
/*  37:    */   {
/*  38:109 */     return true;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void setActive(boolean active)
/*  42:    */   {
/*  43:120 */     this.m_isActive = active;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void setLoaded(boolean loaded)
/*  47:    */   {
/*  48:136 */     this.m_isLoaded = loaded;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void setMainApplication(GUIApplication main)
/*  52:    */   {
/*  53:147 */     this.m_mainApplication = main;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public GUIApplication getMainApplication()
/*  57:    */   {
/*  58:157 */     return this.m_mainApplication;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public String getPerspectiveID()
/*  62:    */   {
/*  63:167 */     if ((this.m_perspectiveID != null) && (this.m_perspectiveID.length() > 0)) {
/*  64:168 */       return this.m_perspectiveID;
/*  65:    */     }
/*  66:171 */     PerspectiveInfo perspectiveA = (PerspectiveInfo)getClass().getAnnotation(PerspectiveInfo.class);
/*  67:173 */     if (perspectiveA != null) {
/*  68:174 */       this.m_perspectiveID = perspectiveA.ID();
/*  69:    */     }
/*  70:177 */     return this.m_perspectiveID;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public String getPerspectiveTitle()
/*  74:    */   {
/*  75:187 */     if ((this.m_perspectiveTitle != null) && (this.m_perspectiveTitle.length() > 0)) {
/*  76:188 */       return this.m_perspectiveTitle;
/*  77:    */     }
/*  78:191 */     PerspectiveInfo perspectiveA = (PerspectiveInfo)getClass().getAnnotation(PerspectiveInfo.class);
/*  79:193 */     if (perspectiveA != null) {
/*  80:194 */       this.m_perspectiveTitle = perspectiveA.title();
/*  81:    */     }
/*  82:197 */     return this.m_perspectiveTitle;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public String getPerspectiveTipText()
/*  86:    */   {
/*  87:207 */     if ((this.m_perspectiveTipText != null) && (this.m_perspectiveTipText.length() > 0)) {
/*  88:208 */       return this.m_perspectiveTipText;
/*  89:    */     }
/*  90:211 */     PerspectiveInfo perspectiveA = (PerspectiveInfo)getClass().getAnnotation(PerspectiveInfo.class);
/*  91:213 */     if (perspectiveA != null) {
/*  92:214 */       this.m_perspectiveTipText = perspectiveA.toolTipText();
/*  93:    */     }
/*  94:217 */     return this.m_perspectiveTipText;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public Icon getPerspectiveIcon()
/*  98:    */   {
/*  99:227 */     if (this.m_perspectiveIcon != null) {
/* 100:228 */       return this.m_perspectiveIcon;
/* 101:    */     }
/* 102:231 */     PerspectiveInfo perspectiveA = (PerspectiveInfo)getClass().getAnnotation(PerspectiveInfo.class);
/* 103:233 */     if ((perspectiveA != null) && (perspectiveA.iconPath() != null) && (perspectiveA.iconPath().length() > 0)) {
/* 104:235 */       this.m_perspectiveIcon = StepVisual.loadIcon(perspectiveA.iconPath());
/* 105:    */     }
/* 106:238 */     return this.m_perspectiveIcon;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public List<JMenu> getMenus()
/* 110:    */   {
/* 111:249 */     return new ArrayList();
/* 112:    */   }
/* 113:    */   
/* 114:    */   public void setInstances(Instances instances) {}
/* 115:    */   
/* 116:    */   public boolean acceptsInstances()
/* 117:    */   {
/* 118:271 */     return false;
/* 119:    */   }
/* 120:    */   
/* 121:    */   public boolean requiresLog()
/* 122:    */   {
/* 123:282 */     return false;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public Defaults getDefaultSettings()
/* 127:    */   {
/* 128:294 */     return null;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public void settingsChanged() {}
/* 132:    */   
/* 133:    */   public void setLog(Logger log)
/* 134:    */   {
/* 135:313 */     this.m_log = log;
/* 136:    */   }
/* 137:    */   
/* 138:    */   public String toString()
/* 139:    */   {
/* 140:322 */     return getPerspectiveTitle();
/* 141:    */   }
/* 142:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.AbstractPerspective
 * JD-Core Version:    0.7.0.1
 */