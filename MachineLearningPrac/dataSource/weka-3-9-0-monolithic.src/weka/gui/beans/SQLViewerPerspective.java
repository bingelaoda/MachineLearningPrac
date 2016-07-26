/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.Image;
/*   6:    */ import java.awt.Toolkit;
/*   7:    */ import java.awt.event.ActionEvent;
/*   8:    */ import java.awt.event.ActionListener;
/*   9:    */ import java.awt.event.WindowAdapter;
/*  10:    */ import java.awt.event.WindowEvent;
/*  11:    */ import java.beans.beancontext.BeanContextSupport;
/*  12:    */ import java.net.URL;
/*  13:    */ import javax.swing.Icon;
/*  14:    */ import javax.swing.ImageIcon;
/*  15:    */ import javax.swing.JButton;
/*  16:    */ import javax.swing.JFrame;
/*  17:    */ import javax.swing.JPanel;
/*  18:    */ import weka.core.Instances;
/*  19:    */ import weka.core.converters.DatabaseLoader;
/*  20:    */ import weka.gui.sql.SqlViewer;
/*  21:    */ import weka.gui.sql.event.ConnectionEvent;
/*  22:    */ import weka.gui.sql.event.ConnectionListener;
/*  23:    */ 
/*  24:    */ public class SQLViewerPerspective
/*  25:    */   extends JPanel
/*  26:    */   implements KnowledgeFlowApp.KFPerspective
/*  27:    */ {
/*  28:    */   private static final long serialVersionUID = 3684166225482042972L;
/*  29:    */   protected KnowledgeFlowApp.MainKFPerspective m_mainPerspective;
/*  30:    */   protected SqlViewer m_viewer;
/*  31:    */   protected JButton m_newFlowBut;
/*  32:    */   
/*  33:    */   public SQLViewerPerspective()
/*  34:    */   {
/*  35: 62 */     setLayout(new BorderLayout());
/*  36:    */     
/*  37: 64 */     this.m_viewer = new SqlViewer(null);
/*  38: 65 */     add(this.m_viewer, "Center");
/*  39:    */     
/*  40: 67 */     this.m_newFlowBut = new JButton("New Flow");
/*  41: 68 */     this.m_newFlowBut.setToolTipText("Set up a new Knowledge Flow with the current connection and query");
/*  42:    */     
/*  43: 70 */     JPanel butHolder = new JPanel();
/*  44: 71 */     butHolder.add(this.m_newFlowBut);
/*  45: 72 */     add(butHolder, "South");
/*  46:    */     
/*  47: 74 */     this.m_newFlowBut.addActionListener(new ActionListener()
/*  48:    */     {
/*  49:    */       public void actionPerformed(ActionEvent e)
/*  50:    */       {
/*  51: 77 */         if (SQLViewerPerspective.this.m_mainPerspective != null) {
/*  52: 78 */           SQLViewerPerspective.this.newFlow();
/*  53:    */         }
/*  54:    */       }
/*  55: 81 */     });
/*  56: 82 */     this.m_newFlowBut.setEnabled(false);
/*  57:    */     
/*  58: 84 */     this.m_viewer.addConnectionListener(new ConnectionListener()
/*  59:    */     {
/*  60:    */       public void connectionChange(ConnectionEvent evt)
/*  61:    */       {
/*  62: 88 */         if (evt.getType() == 1) {
/*  63: 89 */           SQLViewerPerspective.this.m_newFlowBut.setEnabled(false);
/*  64:    */         } else {
/*  65: 91 */           SQLViewerPerspective.this.m_newFlowBut.setEnabled(true);
/*  66:    */         }
/*  67:    */       }
/*  68:    */     });
/*  69:    */   }
/*  70:    */   
/*  71:    */   protected void newFlow()
/*  72:    */   {
/*  73: 98 */     this.m_newFlowBut.setEnabled(false);
/*  74:    */     
/*  75:100 */     String user = this.m_viewer.getUser();
/*  76:101 */     String password = this.m_viewer.getPassword();
/*  77:102 */     String uRL = this.m_viewer.getURL();
/*  78:103 */     String query = this.m_viewer.getQuery();
/*  79:105 */     if (query == null) {
/*  80:106 */       query = "";
/*  81:    */     }
/*  82:    */     try
/*  83:    */     {
/*  84:110 */       DatabaseLoader dbl = new DatabaseLoader();
/*  85:111 */       dbl.setUser(user);
/*  86:112 */       dbl.setPassword(password);
/*  87:113 */       dbl.setUrl(uRL);
/*  88:114 */       dbl.setQuery(query);
/*  89:    */       
/*  90:116 */       BeanContextSupport bc = new BeanContextSupport();
/*  91:117 */       bc.setDesignTime(true);
/*  92:    */       
/*  93:119 */       Loader loaderComp = new Loader();
/*  94:120 */       bc.add(loaderComp);
/*  95:121 */       loaderComp.setLoader(dbl);
/*  96:    */       
/*  97:123 */       KnowledgeFlowApp singleton = KnowledgeFlowApp.getSingleton();
/*  98:124 */       this.m_mainPerspective.addTab("DBSource");
/*  99:    */       
/* 100:    */ 
/* 101:    */ 
/* 102:128 */       new BeanInstance(this.m_mainPerspective.getBeanLayout(this.m_mainPerspective.getNumTabs() - 1), loaderComp, 50, 50, new Integer[] { Integer.valueOf(this.m_mainPerspective.getNumTabs() - 1) });
/* 103:    */       
/* 104:    */ 
/* 105:    */ 
/* 106:    */ 
/* 107:    */ 
/* 108:    */ 
/* 109:    */ 
/* 110:    */ 
/* 111:    */ 
/* 112:138 */       singleton.setActivePerspective(0);
/* 113:    */       
/* 114:140 */       this.m_newFlowBut.setEnabled(true);
/* 115:    */     }
/* 116:    */     catch (Exception ex)
/* 117:    */     {
/* 118:143 */       ex.printStackTrace();
/* 119:    */     }
/* 120:    */   }
/* 121:    */   
/* 122:    */   public void setInstances(Instances insts)
/* 123:    */     throws Exception
/* 124:    */   {}
/* 125:    */   
/* 126:    */   public boolean acceptsInstances()
/* 127:    */   {
/* 128:165 */     return false;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public String getPerspectiveTitle()
/* 132:    */   {
/* 133:175 */     return "SQL Viewer";
/* 134:    */   }
/* 135:    */   
/* 136:    */   public String getPerspectiveTipText()
/* 137:    */   {
/* 138:185 */     return "Explore database tables with SQL";
/* 139:    */   }
/* 140:    */   
/* 141:    */   public Icon getPerspectiveIcon()
/* 142:    */   {
/* 143:196 */     Image pic = null;
/* 144:197 */     URL imageURL = getClass().getClassLoader().getResource("weka/gui/beans/icons/database.png");
/* 145:200 */     if (imageURL != null) {
/* 146:202 */       pic = Toolkit.getDefaultToolkit().getImage(imageURL);
/* 147:    */     }
/* 148:204 */     return new ImageIcon(pic);
/* 149:    */   }
/* 150:    */   
/* 151:    */   public void setActive(boolean active) {}
/* 152:    */   
/* 153:    */   public void setLoaded(boolean loaded) {}
/* 154:    */   
/* 155:    */   public void setMainKFPerspective(KnowledgeFlowApp.MainKFPerspective main)
/* 156:    */   {
/* 157:243 */     this.m_mainPerspective = main;
/* 158:    */   }
/* 159:    */   
/* 160:    */   public static void main(String[] args)
/* 161:    */   {
/* 162:252 */     JFrame jf = new JFrame();
/* 163:253 */     jf.getContentPane().setLayout(new BorderLayout());
/* 164:254 */     SQLViewerPerspective p = new SQLViewerPerspective();
/* 165:    */     
/* 166:256 */     jf.getContentPane().add(p, "Center");
/* 167:257 */     jf.addWindowListener(new WindowAdapter()
/* 168:    */     {
/* 169:    */       public void windowClosing(WindowEvent e)
/* 170:    */       {
/* 171:260 */         this.val$jf.dispose();
/* 172:261 */         System.exit(0);
/* 173:    */       }
/* 174:263 */     });
/* 175:264 */     jf.setSize(800, 600);
/* 176:265 */     jf.setVisible(true);
/* 177:    */   }
/* 178:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.SQLViewerPerspective
 * JD-Core Version:    0.7.0.1
 */