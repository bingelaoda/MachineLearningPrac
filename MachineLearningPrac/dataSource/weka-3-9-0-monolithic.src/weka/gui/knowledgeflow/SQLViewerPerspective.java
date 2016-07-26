/*   1:    */ package weka.gui.knowledgeflow;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.event.ActionEvent;
/*   5:    */ import java.awt.event.ActionListener;
/*   6:    */ import javax.swing.JButton;
/*   7:    */ import javax.swing.JPanel;
/*   8:    */ import weka.core.Defaults;
/*   9:    */ import weka.core.converters.DatabaseLoader;
/*  10:    */ import weka.gui.AbstractPerspective;
/*  11:    */ import weka.gui.GUIApplication;
/*  12:    */ import weka.gui.PerspectiveInfo;
/*  13:    */ import weka.gui.PerspectiveManager;
/*  14:    */ import weka.gui.sql.SqlViewer;
/*  15:    */ import weka.gui.sql.event.ConnectionEvent;
/*  16:    */ import weka.gui.sql.event.ConnectionListener;
/*  17:    */ import weka.knowledgeflow.StepManagerImpl;
/*  18:    */ import weka.knowledgeflow.steps.Loader;
/*  19:    */ 
/*  20:    */ @PerspectiveInfo(ID="sqlviewer", title="SQL Viewer", toolTipText="Explore database tables with SQL", iconPath="weka/gui/knowledgeflow/icons/database.png")
/*  21:    */ public class SQLViewerPerspective
/*  22:    */   extends AbstractPerspective
/*  23:    */ {
/*  24:    */   private static final long serialVersionUID = -4771310190331379801L;
/*  25:    */   protected SqlViewer m_viewer;
/*  26:    */   protected JButton m_newFlowBut;
/*  27:    */   protected MainKFPerspective m_mainKFPerspective;
/*  28:    */   protected JPanel m_buttonHolder;
/*  29:    */   
/*  30:    */   public SQLViewerPerspective()
/*  31:    */   {
/*  32: 69 */     setLayout(new BorderLayout());
/*  33: 70 */     this.m_viewer = new SqlViewer(null);
/*  34: 71 */     add(this.m_viewer, "Center");
/*  35:    */     
/*  36: 73 */     this.m_newFlowBut = new JButton("New Flow");
/*  37: 74 */     this.m_newFlowBut.setToolTipText("Set up a new Knowledge Flow with the current connection and query");
/*  38:    */     
/*  39: 76 */     this.m_buttonHolder = new JPanel();
/*  40: 77 */     this.m_buttonHolder.add(this.m_newFlowBut);
/*  41: 78 */     add(this.m_buttonHolder, "South");
/*  42:    */     
/*  43: 80 */     this.m_newFlowBut.addActionListener(new ActionListener()
/*  44:    */     {
/*  45:    */       public void actionPerformed(ActionEvent e)
/*  46:    */       {
/*  47: 83 */         if (SQLViewerPerspective.this.m_mainKFPerspective != null) {
/*  48: 84 */           SQLViewerPerspective.this.newFlow();
/*  49:    */         }
/*  50:    */       }
/*  51: 87 */     });
/*  52: 88 */     this.m_newFlowBut.setEnabled(false);
/*  53:    */     
/*  54: 90 */     this.m_viewer.addConnectionListener(new ConnectionListener()
/*  55:    */     {
/*  56:    */       public void connectionChange(ConnectionEvent evt)
/*  57:    */       {
/*  58: 94 */         if (evt.getType() == 1) {
/*  59: 95 */           SQLViewerPerspective.this.m_newFlowBut.setEnabled(false);
/*  60:    */         } else {
/*  61: 97 */           SQLViewerPerspective.this.m_newFlowBut.setEnabled(true);
/*  62:    */         }
/*  63:    */       }
/*  64:    */     });
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void setMainApplication(GUIApplication application)
/*  68:    */   {
/*  69:111 */     super.setMainApplication(application);
/*  70:    */     
/*  71:113 */     this.m_mainKFPerspective = ((MainKFPerspective)this.m_mainApplication.getPerspectiveManager().getPerspective("knowledgeflow"));
/*  72:116 */     if (this.m_mainKFPerspective == null) {
/*  73:117 */       remove(this.m_buttonHolder);
/*  74:    */     }
/*  75:    */   }
/*  76:    */   
/*  77:    */   protected void newFlow()
/*  78:    */   {
/*  79:126 */     this.m_newFlowBut.setEnabled(false);
/*  80:    */     
/*  81:128 */     String user = this.m_viewer.getUser();
/*  82:129 */     String password = this.m_viewer.getPassword();
/*  83:130 */     String uRL = this.m_viewer.getURL();
/*  84:131 */     String query = this.m_viewer.getQuery();
/*  85:133 */     if (query == null) {
/*  86:134 */       query = "";
/*  87:    */     }
/*  88:    */     try
/*  89:    */     {
/*  90:138 */       DatabaseLoader dbl = new DatabaseLoader();
/*  91:139 */       dbl.setUser(user);
/*  92:140 */       dbl.setPassword(password);
/*  93:141 */       dbl.setUrl(uRL);
/*  94:142 */       dbl.setQuery(query);
/*  95:    */       
/*  96:144 */       Loader loaderStep = new Loader();
/*  97:145 */       loaderStep.setLoader(dbl);
/*  98:    */       
/*  99:147 */       StepManagerImpl manager = new StepManagerImpl(loaderStep);
/* 100:148 */       this.m_mainKFPerspective.addTab("DBSource");
/* 101:149 */       this.m_mainKFPerspective.getCurrentLayout().addStep(manager, 50, 50);
/* 102:150 */       this.m_mainApplication.getPerspectiveManager().setActivePerspective("knowledgeflow");
/* 103:    */       
/* 104:    */ 
/* 105:153 */       this.m_newFlowBut.setEnabled(true);
/* 106:    */     }
/* 107:    */     catch (Exception ex)
/* 108:    */     {
/* 109:155 */       ex.printStackTrace();
/* 110:156 */       this.m_mainApplication.showErrorDialog(ex);
/* 111:    */     }
/* 112:    */   }
/* 113:    */   
/* 114:    */   protected static class SQLDefaults
/* 115:    */     extends Defaults
/* 116:    */   {
/* 117:    */     public static final String ID = "sqlviewer";
/* 118:    */     private static final long serialVersionUID = 5907476861935295960L;
/* 119:    */     
/* 120:    */     public SQLDefaults()
/* 121:    */     {
/* 122:169 */       super();
/* 123:    */     }
/* 124:    */   }
/* 125:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.SQLViewerPerspective
 * JD-Core Version:    0.7.0.1
 */