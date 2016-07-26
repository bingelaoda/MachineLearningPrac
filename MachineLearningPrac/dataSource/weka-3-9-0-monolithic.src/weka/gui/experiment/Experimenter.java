/*   1:    */ package weka.gui.experiment;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.Image;
/*   6:    */ import java.awt.Toolkit;
/*   7:    */ import java.awt.event.WindowAdapter;
/*   8:    */ import java.awt.event.WindowEvent;
/*   9:    */ import java.beans.PropertyChangeEvent;
/*  10:    */ import java.beans.PropertyChangeListener;
/*  11:    */ import java.io.PrintStream;
/*  12:    */ import javax.swing.JFrame;
/*  13:    */ import javax.swing.JTabbedPane;
/*  14:    */ import weka.core.Memory;
/*  15:    */ import weka.core.logging.Logger;
/*  16:    */ import weka.core.logging.Logger.Level;
/*  17:    */ import weka.experiment.Experiment;
/*  18:    */ import weka.gui.AbstractPerspective;
/*  19:    */ import weka.gui.GUIApplication;
/*  20:    */ import weka.gui.GenericObjectEditor;
/*  21:    */ import weka.gui.LookAndFeel;
/*  22:    */ import weka.gui.PerspectiveInfo;
/*  23:    */ 
/*  24:    */ @PerspectiveInfo(ID="weka.gui.experimenter", title="Experiment", toolTipText="Run large scale experiments", iconPath="weka/gui/weka_icon_new_small.png")
/*  25:    */ public class Experimenter
/*  26:    */   extends AbstractPerspective
/*  27:    */ {
/*  28:    */   private static final long serialVersionUID = -5751617505738193788L;
/*  29:    */   protected SetupModePanel m_SetupPanel;
/*  30:    */   protected RunPanel m_RunPanel;
/*  31:    */   protected ResultsPanel m_ResultsPanel;
/*  32: 65 */   protected JTabbedPane m_TabbedPane = new JTabbedPane();
/*  33: 71 */   protected boolean m_ClassFirst = false;
/*  34:    */   private static Experimenter m_experimenter;
/*  35:    */   
/*  36:    */   public Experimenter()
/*  37:    */   {
/*  38: 77 */     this(false);
/*  39:    */   }
/*  40:    */   
/*  41:    */   public Experimenter(boolean classFirst)
/*  42:    */   {
/*  43: 85 */     this.m_SetupPanel = new SetupModePanel();
/*  44: 86 */     this.m_ResultsPanel = new ResultsPanel();
/*  45: 87 */     this.m_RunPanel = new RunPanel();
/*  46: 88 */     this.m_RunPanel.setResultsPanel(this.m_ResultsPanel);
/*  47:    */     
/*  48: 90 */     this.m_ClassFirst = classFirst;
/*  49:    */     
/*  50: 92 */     this.m_TabbedPane.addTab("Setup", null, this.m_SetupPanel, "Set up the experiment");
/*  51: 93 */     this.m_TabbedPane.addTab("Run", null, this.m_RunPanel, "Run the experiment");
/*  52: 94 */     this.m_TabbedPane.addTab("Analyse", null, this.m_ResultsPanel, "Analyse experiment results");
/*  53:    */     
/*  54: 96 */     this.m_TabbedPane.setSelectedIndex(0);
/*  55: 97 */     this.m_TabbedPane.setEnabledAt(1, false);
/*  56: 98 */     this.m_SetupPanel.addPropertyChangeListener(new PropertyChangeListener()
/*  57:    */     {
/*  58:    */       public void propertyChange(PropertyChangeEvent e)
/*  59:    */       {
/*  60:102 */         Experiment exp = Experimenter.this.m_SetupPanel.getExperiment();
/*  61:103 */         exp.classFirst(Experimenter.this.m_ClassFirst);
/*  62:104 */         Experimenter.this.m_RunPanel.setExperiment(exp);
/*  63:    */         
/*  64:106 */         Experimenter.this.m_TabbedPane.setEnabledAt(1, true);
/*  65:    */       }
/*  66:108 */     });
/*  67:109 */     setLayout(new BorderLayout());
/*  68:110 */     add(this.m_TabbedPane, "Center");
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void instantiationComplete()
/*  72:    */   {
/*  73:119 */     this.m_ResultsPanel.setMainPerspective(getMainApplication().getMainPerspective());
/*  74:    */   }
/*  75:    */   
/*  76:130 */   protected static Memory m_Memory = new Memory(true);
/*  77:    */   
/*  78:    */   public static void main(String[] args)
/*  79:    */   {
/*  80:138 */     Logger.log(Logger.Level.INFO, "Logging started");
/*  81:    */     
/*  82:    */ 
/*  83:    */ 
/*  84:    */ 
/*  85:143 */     GenericObjectEditor.determineClasses();
/*  86:    */     
/*  87:145 */     LookAndFeel.setLookAndFeel();
/*  88:    */     try
/*  89:    */     {
/*  90:151 */       boolean classFirst = false;
/*  91:152 */       if (args.length > 0) {
/*  92:153 */         classFirst = args[0].equals("CLASS_FIRST");
/*  93:    */       }
/*  94:155 */       m_experimenter = new Experimenter(classFirst);
/*  95:156 */       JFrame jf = new JFrame("Weka Experiment Environment");
/*  96:157 */       jf.getContentPane().setLayout(new BorderLayout());
/*  97:158 */       jf.getContentPane().add(m_experimenter, "Center");
/*  98:159 */       jf.addWindowListener(new WindowAdapter()
/*  99:    */       {
/* 100:    */         public void windowClosing(WindowEvent e)
/* 101:    */         {
/* 102:162 */           this.val$jf.dispose();
/* 103:163 */           System.exit(0);
/* 104:    */         }
/* 105:165 */       });
/* 106:166 */       jf.pack();
/* 107:167 */       jf.setSize(800, 600);
/* 108:168 */       jf.setVisible(true);
/* 109:    */       
/* 110:170 */       Image icon = Toolkit.getDefaultToolkit().getImage(m_experimenter.getClass().getClassLoader().getResource("weka/gui/weka_icon_new_48.png"));
/* 111:    */       
/* 112:    */ 
/* 113:    */ 
/* 114:174 */       jf.setIconImage(icon);
/* 115:    */       
/* 116:176 */       Thread memMonitor = new Thread()
/* 117:    */       {
/* 118:    */         public void run()
/* 119:    */         {
/* 120:    */           for (;;)
/* 121:    */           {
/* 122:183 */             if (Experimenter.m_Memory.isOutOfMemory())
/* 123:    */             {
/* 124:185 */               this.val$jf.dispose();
/* 125:186 */               Experimenter.access$002(null);
/* 126:187 */               System.gc();
/* 127:    */               
/* 128:    */ 
/* 129:190 */               System.err.println("\ndisplayed message:");
/* 130:191 */               Experimenter.m_Memory.showOutOfMemory();
/* 131:192 */               System.err.println("\nexiting");
/* 132:193 */               System.exit(-1);
/* 133:    */             }
/* 134:    */           }
/* 135:    */         }
/* 136:202 */       };
/* 137:203 */       memMonitor.setPriority(5);
/* 138:204 */       memMonitor.start();
/* 139:    */     }
/* 140:    */     catch (Exception ex)
/* 141:    */     {
/* 142:206 */       ex.printStackTrace();
/* 143:207 */       System.err.println(ex.getMessage());
/* 144:    */     }
/* 145:    */   }
/* 146:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.experiment.Experimenter
 * JD-Core Version:    0.7.0.1
 */