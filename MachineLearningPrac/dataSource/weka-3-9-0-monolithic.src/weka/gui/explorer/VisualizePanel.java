/*   1:    */ package weka.gui.explorer;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.event.WindowAdapter;
/*   6:    */ import java.awt.event.WindowEvent;
/*   7:    */ import java.io.BufferedReader;
/*   8:    */ import java.io.FileReader;
/*   9:    */ import java.io.PrintStream;
/*  10:    */ import java.io.Reader;
/*  11:    */ import java.util.Map;
/*  12:    */ import javax.swing.JFrame;
/*  13:    */ import weka.core.Defaults;
/*  14:    */ import weka.core.Instances;
/*  15:    */ import weka.core.Settings.SettingKey;
/*  16:    */ import weka.gui.AbstractPerspective;
/*  17:    */ import weka.gui.GUIApplication;
/*  18:    */ import weka.gui.PerspectiveInfo;
/*  19:    */ import weka.gui.visualize.MatrixPanel;
/*  20:    */ import weka.gui.visualize.VisualizeUtils.VisualizeDefaults;
/*  21:    */ 
/*  22:    */ @PerspectiveInfo(ID="weka.gui.workbench.visualizepanel", title="Visualize", toolTipText="Explore the data", iconPath="weka/gui/weka_icon_new_small.png")
/*  23:    */ public class VisualizePanel
/*  24:    */   extends AbstractPerspective
/*  25:    */   implements Explorer.ExplorerPanel
/*  26:    */ {
/*  27:    */   private static final long serialVersionUID = 6084015036853918846L;
/*  28: 51 */   protected Explorer m_Explorer = null;
/*  29: 53 */   protected MatrixPanel m_matrixPanel = new MatrixPanel();
/*  30:    */   protected boolean m_hasInstancesSet;
/*  31:    */   
/*  32:    */   public VisualizePanel()
/*  33:    */   {
/*  34: 59 */     setLayout(new BorderLayout());
/*  35: 60 */     add(this.m_matrixPanel, "Center");
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void setInstances(Instances instances)
/*  39:    */   {
/*  40: 65 */     this.m_matrixPanel.setInstances(instances);
/*  41: 66 */     this.m_hasInstancesSet = true;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public void setExplorer(Explorer parent)
/*  45:    */   {
/*  46: 77 */     this.m_Explorer = parent;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public Explorer getExplorer()
/*  50:    */   {
/*  51: 87 */     return this.m_Explorer;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public String getTabTitle()
/*  55:    */   {
/*  56: 97 */     return "Visualize";
/*  57:    */   }
/*  58:    */   
/*  59:    */   public String getTabTitleToolTip()
/*  60:    */   {
/*  61:107 */     return "Explore the data";
/*  62:    */   }
/*  63:    */   
/*  64:    */   public boolean acceptsInstances()
/*  65:    */   {
/*  66:117 */     return true;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public Defaults getDefaultSettings()
/*  70:    */   {
/*  71:127 */     Defaults d = new ScatterDefaults();
/*  72:128 */     d.add(new VisualizeUtils.VisualizeDefaults());
/*  73:129 */     return d;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public boolean okToBeActive()
/*  77:    */   {
/*  78:134 */     return this.m_hasInstancesSet;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void setActive(boolean active)
/*  82:    */   {
/*  83:144 */     super.setActive(active);
/*  84:145 */     if (this.m_isActive) {
/*  85:146 */       settingsChanged();
/*  86:    */     }
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void settingsChanged()
/*  90:    */   {
/*  91:152 */     if (getMainApplication() != null)
/*  92:    */     {
/*  93:153 */       this.m_matrixPanel.applySettings(this.m_mainApplication.getApplicationSettings(), "weka.gui.workbench.visualizepanel");
/*  94:155 */       if (this.m_isActive) {
/*  95:156 */         this.m_matrixPanel.updatePanel();
/*  96:    */       }
/*  97:    */     }
/*  98:    */   }
/*  99:    */   
/* 100:    */   public static class ScatterDefaults
/* 101:    */     extends Defaults
/* 102:    */   {
/* 103:    */     public static final String ID = "weka.gui.workbench.visualizepanel";
/* 104:168 */     public static final Settings.SettingKey POINT_SIZE_KEY = new Settings.SettingKey("weka.gui.workbench.visualizepanel.pointSize", "Point size for scatter plots", "");
/* 105:    */     public static final int POINT_SIZE = 1;
/* 106:173 */     public static final Settings.SettingKey PLOT_SIZE_KEY = new Settings.SettingKey("weka.gui.workbench.visualizepanel.plotSize", "Size (in pixels) of the cells in the matrix", "");
/* 107:    */     public static final int PLOT_SIZE = 100;
/* 108:    */     public static final long serialVersionUID = -6890761195767034507L;
/* 109:    */     
/* 110:    */     public ScatterDefaults()
/* 111:    */     {
/* 112:181 */       super();
/* 113:    */       
/* 114:183 */       this.m_defaults.put(POINT_SIZE_KEY, Integer.valueOf(1));
/* 115:184 */       this.m_defaults.put(PLOT_SIZE_KEY, Integer.valueOf(100));
/* 116:    */     }
/* 117:    */   }
/* 118:    */   
/* 119:    */   public static void main(String[] args)
/* 120:    */   {
/* 121:    */     try
/* 122:    */     {
/* 123:196 */       JFrame jf = new JFrame("Weka Explorer: Visualize");
/* 124:    */       
/* 125:198 */       jf.getContentPane().setLayout(new BorderLayout());
/* 126:199 */       VisualizePanel sp = new VisualizePanel();
/* 127:200 */       jf.getContentPane().add(sp, "Center");
/* 128:201 */       jf.addWindowListener(new WindowAdapter()
/* 129:    */       {
/* 130:    */         public void windowClosing(WindowEvent e)
/* 131:    */         {
/* 132:203 */           this.val$jf.dispose();
/* 133:204 */           System.exit(0);
/* 134:    */         }
/* 135:206 */       });
/* 136:207 */       jf.pack();
/* 137:208 */       jf.setSize(800, 600);
/* 138:209 */       jf.setVisible(true);
/* 139:210 */       if (args.length == 1)
/* 140:    */       {
/* 141:211 */         System.err.println("Loading instances from " + args[0]);
/* 142:212 */         Reader r = new BufferedReader(new FileReader(args[0]));
/* 143:    */         
/* 144:214 */         Instances i = new Instances(r);
/* 145:215 */         sp.setInstances(i);
/* 146:    */       }
/* 147:    */     }
/* 148:    */     catch (Exception ex)
/* 149:    */     {
/* 150:218 */       ex.printStackTrace();
/* 151:219 */       System.err.println(ex.getMessage());
/* 152:    */     }
/* 153:    */   }
/* 154:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.explorer.VisualizePanel
 * JD-Core Version:    0.7.0.1
 */