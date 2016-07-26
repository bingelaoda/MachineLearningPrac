/*   1:    */ package weka.gui.streams;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Color;
/*   5:    */ import java.awt.event.ActionEvent;
/*   6:    */ import java.awt.event.ActionListener;
/*   7:    */ import java.io.BufferedReader;
/*   8:    */ import java.io.FileReader;
/*   9:    */ import java.io.PrintStream;
/*  10:    */ import java.io.Reader;
/*  11:    */ import java.util.Vector;
/*  12:    */ import javax.swing.JButton;
/*  13:    */ import javax.swing.JPanel;
/*  14:    */ import javax.swing.JTextField;
/*  15:    */ import weka.core.Instance;
/*  16:    */ import weka.core.Instances;
/*  17:    */ 
/*  18:    */ public class InstanceLoader
/*  19:    */   extends JPanel
/*  20:    */   implements ActionListener, InstanceProducer
/*  21:    */ {
/*  22:    */   private static final long serialVersionUID = -8725567310271862492L;
/*  23:    */   private final Vector<InstanceListener> m_Listeners;
/*  24:    */   private Thread m_LoaderThread;
/*  25:    */   private Instance m_OutputInstance;
/*  26:    */   private Instances m_OutputInstances;
/*  27:    */   private boolean m_Debug;
/*  28:    */   private final JButton m_StartBut;
/*  29:    */   private final JTextField m_FileNameTex;
/*  30:    */   
/*  31:    */   private class LoadThread
/*  32:    */     extends Thread
/*  33:    */   {
/*  34:    */     private final InstanceProducer m_IP;
/*  35:    */     
/*  36:    */     public LoadThread(InstanceProducer ip)
/*  37:    */     {
/*  38: 66 */       this.m_IP = ip;
/*  39:    */     }
/*  40:    */     
/*  41:    */     public void run()
/*  42:    */     {
/*  43:    */       try
/*  44:    */       {
/*  45: 74 */         InstanceLoader.this.m_StartBut.setText("Stop");
/*  46: 75 */         InstanceLoader.this.m_StartBut.setBackground(Color.red);
/*  47: 76 */         if (InstanceLoader.this.m_Debug) {
/*  48: 77 */           System.err.println("InstanceLoader::LoadThread::run()");
/*  49:    */         }
/*  50: 80 */         Reader input = new BufferedReader(new FileReader(InstanceLoader.this.m_FileNameTex.getText()));
/*  51:    */         
/*  52: 82 */         InstanceLoader.this.m_OutputInstances = new Instances(input, 1);
/*  53: 83 */         if (InstanceLoader.this.m_Debug) {
/*  54: 84 */           System.err.println("InstanceLoader::LoadThread::run() - Instances opened from: " + InstanceLoader.this.m_FileNameTex.getText());
/*  55:    */         }
/*  56: 87 */         InstanceEvent ie = new InstanceEvent(this.m_IP, 1);
/*  57:    */         
/*  58: 89 */         InstanceLoader.this.notifyInstanceProduced(ie);
/*  59: 90 */         while (InstanceLoader.this.m_OutputInstances.readInstance(input))
/*  60:    */         {
/*  61: 91 */           if (InstanceLoader.this.m_LoaderThread != this) {
/*  62:    */             return;
/*  63:    */           }
/*  64: 94 */           if (InstanceLoader.this.m_Debug) {
/*  65: 95 */             System.err.println("InstanceLoader::LoadThread::run() - read instance");
/*  66:    */           }
/*  67: 99 */           InstanceLoader.this.m_OutputInstance = InstanceLoader.this.m_OutputInstances.instance(0);
/*  68:100 */           InstanceLoader.this.m_OutputInstances.delete(0);
/*  69:101 */           ie = new InstanceEvent(this.m_IP, 2);
/*  70:102 */           InstanceLoader.this.notifyInstanceProduced(ie);
/*  71:    */         }
/*  72:104 */         ie = new InstanceEvent(this.m_IP, 3);
/*  73:105 */         InstanceLoader.this.notifyInstanceProduced(ie);
/*  74:    */       }
/*  75:    */       catch (Exception ex)
/*  76:    */       {
/*  77:107 */         System.err.println(ex.getMessage());
/*  78:    */       }
/*  79:    */       finally
/*  80:    */       {
/*  81:109 */         InstanceLoader.this.m_LoaderThread = null;
/*  82:110 */         InstanceLoader.this.m_StartBut.setText("Start");
/*  83:111 */         InstanceLoader.this.m_StartBut.setBackground(Color.green);
/*  84:    */       }
/*  85:    */     }
/*  86:    */   }
/*  87:    */   
/*  88:    */   public InstanceLoader()
/*  89:    */   {
/*  90:117 */     setLayout(new BorderLayout());
/*  91:118 */     this.m_StartBut = new JButton("Start");
/*  92:119 */     this.m_StartBut.setBackground(Color.green);
/*  93:120 */     add("West", this.m_StartBut);
/*  94:121 */     this.m_StartBut.addActionListener(this);
/*  95:122 */     this.m_FileNameTex = new JTextField("/home/trigg/datasets/UCI/iris.arff");
/*  96:123 */     add("Center", this.m_FileNameTex);
/*  97:124 */     this.m_Listeners = new Vector();
/*  98:    */   }
/*  99:    */   
/* 100:    */   public void setDebug(boolean debug)
/* 101:    */   {
/* 102:130 */     this.m_Debug = debug;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public boolean getDebug()
/* 106:    */   {
/* 107:135 */     return this.m_Debug;
/* 108:    */   }
/* 109:    */   
/* 110:    */   public void setArffFile(String newArffFile)
/* 111:    */   {
/* 112:140 */     this.m_FileNameTex.setText(newArffFile);
/* 113:    */   }
/* 114:    */   
/* 115:    */   public String getArffFile()
/* 116:    */   {
/* 117:144 */     return this.m_FileNameTex.getText();
/* 118:    */   }
/* 119:    */   
/* 120:    */   public synchronized void addInstanceListener(InstanceListener ipl)
/* 121:    */   {
/* 122:150 */     this.m_Listeners.addElement(ipl);
/* 123:    */   }
/* 124:    */   
/* 125:    */   public synchronized void removeInstanceListener(InstanceListener ipl)
/* 126:    */   {
/* 127:156 */     this.m_Listeners.removeElement(ipl);
/* 128:    */   }
/* 129:    */   
/* 130:    */   protected void notifyInstanceProduced(InstanceEvent e)
/* 131:    */   {
/* 132:162 */     if (this.m_Debug) {
/* 133:163 */       System.err.println("InstanceLoader::notifyInstanceProduced()");
/* 134:    */     }
/* 135:    */     Vector<InstanceListener> l;
/* 136:166 */     synchronized (this)
/* 137:    */     {
/* 138:167 */       l = (Vector)this.m_Listeners.clone();
/* 139:    */     }
/* 140:169 */     if (l.size() > 0)
/* 141:    */     {
/* 142:170 */       for (int i = 0; i < l.size(); i++) {
/* 143:171 */         ((InstanceListener)l.elementAt(i)).instanceProduced(e);
/* 144:    */       }
/* 145:173 */       if (e.getID() == 2) {
/* 146:174 */         this.m_OutputInstance = null;
/* 147:    */       }
/* 148:    */     }
/* 149:    */   }
/* 150:    */   
/* 151:    */   public Instances outputFormat()
/* 152:    */     throws Exception
/* 153:    */   {
/* 154:182 */     if (this.m_OutputInstances == null) {
/* 155:183 */       throw new Exception("No output format defined.");
/* 156:    */     }
/* 157:185 */     return new Instances(this.m_OutputInstances, 0);
/* 158:    */   }
/* 159:    */   
/* 160:    */   public Instance outputPeek()
/* 161:    */     throws Exception
/* 162:    */   {
/* 163:191 */     if ((this.m_OutputInstances == null) || (this.m_OutputInstance == null)) {
/* 164:192 */       return null;
/* 165:    */     }
/* 166:194 */     return (Instance)this.m_OutputInstance.copy();
/* 167:    */   }
/* 168:    */   
/* 169:    */   public void actionPerformed(ActionEvent e)
/* 170:    */   {
/* 171:200 */     Object source = e.getSource();
/* 172:202 */     if (source == this.m_StartBut) {
/* 173:204 */       if (this.m_LoaderThread == null)
/* 174:    */       {
/* 175:205 */         this.m_LoaderThread = new LoadThread(this);
/* 176:206 */         this.m_LoaderThread.setPriority(1);
/* 177:207 */         this.m_LoaderThread.start();
/* 178:    */       }
/* 179:    */       else
/* 180:    */       {
/* 181:209 */         this.m_LoaderThread = null;
/* 182:    */       }
/* 183:    */     }
/* 184:    */   }
/* 185:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.streams.InstanceLoader
 * JD-Core Version:    0.7.0.1
 */