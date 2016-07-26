/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.GraphicsEnvironment;
/*   6:    */ import java.awt.Image;
/*   7:    */ import java.awt.Toolkit;
/*   8:    */ import java.awt.event.WindowAdapter;
/*   9:    */ import java.awt.event.WindowEvent;
/*  10:    */ import java.io.BufferedReader;
/*  11:    */ import java.io.FileReader;
/*  12:    */ import java.io.PrintStream;
/*  13:    */ import java.io.Reader;
/*  14:    */ import java.net.URL;
/*  15:    */ import javax.swing.Icon;
/*  16:    */ import javax.swing.ImageIcon;
/*  17:    */ import javax.swing.JFrame;
/*  18:    */ import weka.core.Instances;
/*  19:    */ import weka.gui.visualize.MatrixPanel;
/*  20:    */ 
/*  21:    */ public class ScatterPlotMatrix
/*  22:    */   extends DataVisualizer
/*  23:    */   implements KnowledgeFlowApp.KFPerspective
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = -657856527563507491L;
/*  26:    */   protected MatrixPanel m_matrixPanel;
/*  27:    */   
/*  28:    */   public ScatterPlotMatrix()
/*  29:    */   {
/*  30: 48 */     GraphicsEnvironment.getLocalGraphicsEnvironment();
/*  31: 49 */     if (!GraphicsEnvironment.isHeadless()) {
/*  32: 50 */       appearanceFinal();
/*  33:    */     }
/*  34:    */   }
/*  35:    */   
/*  36:    */   public String globalInfo()
/*  37:    */   {
/*  38: 61 */     return "Visualize incoming data/training/test sets in a scatter plot matrix.";
/*  39:    */   }
/*  40:    */   
/*  41:    */   protected void appearanceDesign()
/*  42:    */   {
/*  43: 67 */     this.m_matrixPanel = null;
/*  44: 68 */     removeAll();
/*  45: 69 */     this.m_visual = new BeanVisual("ScatterPlotMatrix", "weka/gui/beans/icons/ScatterPlotMatrix.gif", "weka/gui/beans/icons/ScatterPlotMatrix_animated.gif");
/*  46:    */     
/*  47:    */ 
/*  48: 72 */     setLayout(new BorderLayout());
/*  49: 73 */     add(this.m_visual, "Center");
/*  50:    */   }
/*  51:    */   
/*  52:    */   protected void appearanceFinal()
/*  53:    */   {
/*  54: 78 */     removeAll();
/*  55: 79 */     setLayout(new BorderLayout());
/*  56: 80 */     setUpFinal();
/*  57:    */   }
/*  58:    */   
/*  59:    */   protected void setUpFinal()
/*  60:    */   {
/*  61: 85 */     if (this.m_matrixPanel == null) {
/*  62: 86 */       this.m_matrixPanel = new MatrixPanel();
/*  63:    */     }
/*  64: 88 */     add(this.m_matrixPanel, "Center");
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void setInstances(Instances inst)
/*  68:    */     throws Exception
/*  69:    */   {
/*  70:100 */     if (this.m_design) {
/*  71:101 */       throw new Exception("This method is not to be used during design time. It is meant to be used if this bean is being used programatically as as stand alone component.");
/*  72:    */     }
/*  73:106 */     this.m_visualizeDataSet = inst;
/*  74:107 */     this.m_matrixPanel.setInstances(this.m_visualizeDataSet);
/*  75:    */   }
/*  76:    */   
/*  77:    */   public boolean acceptsInstances()
/*  78:    */   {
/*  79:117 */     return true;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public String getPerspectiveTitle()
/*  83:    */   {
/*  84:127 */     return "Scatter plot matrix";
/*  85:    */   }
/*  86:    */   
/*  87:    */   public String getPerspectiveTipText()
/*  88:    */   {
/*  89:137 */     return "Scatter plot matrix";
/*  90:    */   }
/*  91:    */   
/*  92:    */   public Icon getPerspectiveIcon()
/*  93:    */   {
/*  94:148 */     Image pic = null;
/*  95:149 */     URL imageURL = getClass().getClassLoader().getResource("weka/gui/beans/icons/application_view_tile.png");
/*  96:152 */     if (imageURL != null) {
/*  97:154 */       pic = Toolkit.getDefaultToolkit().getImage(imageURL);
/*  98:    */     }
/*  99:156 */     return new ImageIcon(pic);
/* 100:    */   }
/* 101:    */   
/* 102:    */   public void setActive(boolean active) {}
/* 103:    */   
/* 104:    */   public void setLoaded(boolean loaded) {}
/* 105:    */   
/* 106:    */   public void setMainKFPerspective(KnowledgeFlowApp.MainKFPerspective main) {}
/* 107:    */   
/* 108:    */   public void performRequest(String request)
/* 109:    */   {
/* 110:203 */     if (request.compareTo("Show plot") == 0) {
/* 111:    */       try
/* 112:    */       {
/* 113:206 */         if (!this.m_framePoppedUp)
/* 114:    */         {
/* 115:207 */           this.m_framePoppedUp = true;
/* 116:208 */           MatrixPanel vis = new MatrixPanel();
/* 117:209 */           vis.setInstances(this.m_visualizeDataSet);
/* 118:    */           
/* 119:211 */           final JFrame jf = new JFrame("Visualize");
/* 120:212 */           jf.setSize(800, 600);
/* 121:213 */           jf.getContentPane().setLayout(new BorderLayout());
/* 122:214 */           jf.getContentPane().add(vis, "Center");
/* 123:215 */           jf.addWindowListener(new WindowAdapter()
/* 124:    */           {
/* 125:    */             public void windowClosing(WindowEvent e)
/* 126:    */             {
/* 127:218 */               jf.dispose();
/* 128:219 */               ScatterPlotMatrix.this.m_framePoppedUp = false;
/* 129:    */             }
/* 130:221 */           });
/* 131:222 */           jf.setVisible(true);
/* 132:223 */           this.m_popupFrame = jf;
/* 133:    */         }
/* 134:    */         else
/* 135:    */         {
/* 136:225 */           this.m_popupFrame.toFront();
/* 137:    */         }
/* 138:    */       }
/* 139:    */       catch (Exception ex)
/* 140:    */       {
/* 141:228 */         ex.printStackTrace();
/* 142:229 */         this.m_framePoppedUp = false;
/* 143:    */       }
/* 144:    */     } else {
/* 145:232 */       throw new IllegalArgumentException(request + " not supported (ScatterPlotMatrix)");
/* 146:    */     }
/* 147:    */   }
/* 148:    */   
/* 149:    */   public static void main(String[] args)
/* 150:    */   {
/* 151:    */     try
/* 152:    */     {
/* 153:239 */       if (args.length != 1)
/* 154:    */       {
/* 155:240 */         System.err.println("Usage: ScatterPlotMatrix <dataset>");
/* 156:241 */         System.exit(1);
/* 157:    */       }
/* 158:243 */       Reader r = new BufferedReader(new FileReader(args[0]));
/* 159:    */       
/* 160:245 */       Instances inst = new Instances(r);
/* 161:246 */       JFrame jf = new JFrame();
/* 162:247 */       jf.getContentPane().setLayout(new BorderLayout());
/* 163:248 */       ScatterPlotMatrix as = new ScatterPlotMatrix();
/* 164:249 */       as.setInstances(inst);
/* 165:    */       
/* 166:251 */       jf.getContentPane().add(as, "Center");
/* 167:252 */       jf.addWindowListener(new WindowAdapter()
/* 168:    */       {
/* 169:    */         public void windowClosing(WindowEvent e)
/* 170:    */         {
/* 171:255 */           this.val$jf.dispose();
/* 172:256 */           System.exit(0);
/* 173:    */         }
/* 174:258 */       });
/* 175:259 */       jf.setSize(800, 600);
/* 176:260 */       jf.setVisible(true);
/* 177:    */     }
/* 178:    */     catch (Exception ex)
/* 179:    */     {
/* 180:262 */       ex.printStackTrace();
/* 181:263 */       System.err.println(ex.getMessage());
/* 182:    */     }
/* 183:    */   }
/* 184:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.ScatterPlotMatrix
 * JD-Core Version:    0.7.0.1
 */