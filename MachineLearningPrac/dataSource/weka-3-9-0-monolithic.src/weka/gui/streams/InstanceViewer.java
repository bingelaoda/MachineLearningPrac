/*   1:    */ package weka.gui.streams;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import javax.swing.JPanel;
/*   6:    */ import javax.swing.JScrollPane;
/*   7:    */ import javax.swing.JTextArea;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ 
/*  11:    */ public class InstanceViewer
/*  12:    */   extends JPanel
/*  13:    */   implements InstanceListener
/*  14:    */ {
/*  15:    */   private static final long serialVersionUID = -4925729441294121772L;
/*  16:    */   private JTextArea m_OutputTex;
/*  17:    */   private boolean m_Debug;
/*  18:    */   private boolean m_Clear;
/*  19:    */   private String m_UpdateString;
/*  20:    */   
/*  21:    */   private void updateOutput()
/*  22:    */   {
/*  23: 55 */     this.m_OutputTex.append(this.m_UpdateString);
/*  24: 56 */     this.m_UpdateString = "";
/*  25:    */   }
/*  26:    */   
/*  27:    */   private void clearOutput()
/*  28:    */   {
/*  29: 61 */     this.m_UpdateString = "";
/*  30: 62 */     this.m_OutputTex.setText("");
/*  31:    */   }
/*  32:    */   
/*  33:    */   public void inputFormat(Instances instanceInfo)
/*  34:    */   {
/*  35: 67 */     if (this.m_Debug) {
/*  36: 68 */       System.err.println("InstanceViewer::inputFormat()\n" + instanceInfo.toString());
/*  37:    */     }
/*  38: 71 */     if (this.m_Clear) {
/*  39: 72 */       clearOutput();
/*  40:    */     }
/*  41: 74 */     this.m_UpdateString += instanceInfo.toString();
/*  42: 75 */     updateOutput();
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void input(Instance instance)
/*  46:    */     throws Exception
/*  47:    */   {
/*  48: 80 */     if (this.m_Debug) {
/*  49: 81 */       System.err.println("InstanceViewer::input(" + instance + ")");
/*  50:    */     }
/*  51: 83 */     this.m_UpdateString = (this.m_UpdateString + instance.toString() + "\n");
/*  52: 84 */     updateOutput();
/*  53:    */   }
/*  54:    */   
/*  55:    */   public void batchFinished()
/*  56:    */   {
/*  57: 89 */     updateOutput();
/*  58: 90 */     if (this.m_Debug) {
/*  59: 91 */       System.err.println("InstanceViewer::batchFinished()");
/*  60:    */     }
/*  61:    */   }
/*  62:    */   
/*  63:    */   public InstanceViewer()
/*  64:    */   {
/*  65: 97 */     setLayout(new BorderLayout());
/*  66: 98 */     this.m_UpdateString = "";
/*  67: 99 */     setClearEachDataset(true);
/*  68:100 */     this.m_OutputTex = new JTextArea(10, 20);
/*  69:101 */     this.m_OutputTex.setEditable(false);
/*  70:102 */     add("Center", new JScrollPane(this.m_OutputTex));
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void setClearEachDataset(boolean clear)
/*  74:    */   {
/*  75:107 */     this.m_Clear = clear;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public boolean getClearEachDataset()
/*  79:    */   {
/*  80:112 */     return this.m_Clear;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public void setDebug(boolean debug)
/*  84:    */   {
/*  85:117 */     this.m_Debug = debug;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public boolean getDebug()
/*  89:    */   {
/*  90:122 */     return this.m_Debug;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public void instanceProduced(InstanceEvent e)
/*  94:    */   {
/*  95:127 */     Object source = e.getSource();
/*  96:128 */     if ((source instanceof InstanceProducer)) {
/*  97:    */       try
/*  98:    */       {
/*  99:130 */         InstanceProducer a = (InstanceProducer)source;
/* 100:131 */         switch (e.getID())
/* 101:    */         {
/* 102:    */         case 1: 
/* 103:133 */           inputFormat(a.outputFormat());
/* 104:134 */           break;
/* 105:    */         case 2: 
/* 106:136 */           input(a.outputPeek());
/* 107:137 */           break;
/* 108:    */         case 3: 
/* 109:139 */           batchFinished();
/* 110:140 */           break;
/* 111:    */         default: 
/* 112:142 */           System.err.println("InstanceViewer::instanceProduced() - unknown event type");
/* 113:    */         }
/* 114:    */       }
/* 115:    */       catch (Exception ex)
/* 116:    */       {
/* 117:147 */         System.err.println(ex.getMessage());
/* 118:    */       }
/* 119:    */     } else {
/* 120:150 */       System.err.println("InstanceViewer::instanceProduced() - Unknown source object type");
/* 121:    */     }
/* 122:    */   }
/* 123:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.streams.InstanceViewer
 * JD-Core Version:    0.7.0.1
 */