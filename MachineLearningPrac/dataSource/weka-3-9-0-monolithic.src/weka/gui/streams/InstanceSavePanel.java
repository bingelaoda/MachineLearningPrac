/*   1:    */ package weka.gui.streams;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Color;
/*   5:    */ import java.awt.Label;
/*   6:    */ import java.awt.Panel;
/*   7:    */ import java.awt.TextField;
/*   8:    */ import java.io.FileOutputStream;
/*   9:    */ import java.io.PrintStream;
/*  10:    */ import java.io.PrintWriter;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ 
/*  14:    */ public class InstanceSavePanel
/*  15:    */   extends Panel
/*  16:    */   implements InstanceListener
/*  17:    */ {
/*  18:    */   private static final long serialVersionUID = -6061005366989295026L;
/*  19:    */   private Label count_Lab;
/*  20:    */   private int m_Count;
/*  21:    */   private TextField arffFile_Tex;
/*  22:    */   private boolean b_Debug;
/*  23:    */   private PrintWriter outputWriter;
/*  24:    */   
/*  25:    */   public void input(Instance instance)
/*  26:    */     throws Exception
/*  27:    */   {
/*  28: 56 */     if (this.b_Debug) {
/*  29: 57 */       System.err.println("InstanceSavePanel::input(" + instance + ")");
/*  30:    */     }
/*  31: 58 */     this.m_Count += 1;
/*  32: 59 */     this.count_Lab.setText("" + this.m_Count + " instances");
/*  33: 60 */     if (this.outputWriter != null) {
/*  34: 61 */       this.outputWriter.println(instance.toString());
/*  35:    */     }
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void inputFormat(Instances instanceInfo)
/*  39:    */   {
/*  40: 66 */     if (this.b_Debug) {
/*  41: 67 */       System.err.println("InstanceSavePanel::inputFormat()\n" + instanceInfo.toString());
/*  42:    */     }
/*  43: 69 */     this.m_Count = 0;
/*  44: 70 */     this.count_Lab.setText("" + this.m_Count + " instances");
/*  45:    */     try
/*  46:    */     {
/*  47: 72 */       this.outputWriter = new PrintWriter(new FileOutputStream(this.arffFile_Tex.getText()));
/*  48: 73 */       this.outputWriter.println(instanceInfo.toString());
/*  49: 74 */       if (this.b_Debug) {
/*  50: 75 */         System.err.println("InstanceSavePanel::inputFormat() - written header");
/*  51:    */       }
/*  52:    */     }
/*  53:    */     catch (Exception ex)
/*  54:    */     {
/*  55: 77 */       this.outputWriter = null;
/*  56: 78 */       System.err.println("InstanceSavePanel::inputFormat(): " + ex.getMessage());
/*  57:    */     }
/*  58:    */   }
/*  59:    */   
/*  60:    */   public void batchFinished()
/*  61:    */   {
/*  62: 84 */     if (this.b_Debug) {
/*  63: 85 */       System.err.println("InstanceSavePanel::batchFinished()");
/*  64:    */     }
/*  65: 86 */     if (this.outputWriter != null) {
/*  66: 87 */       this.outputWriter.close();
/*  67:    */     }
/*  68:    */   }
/*  69:    */   
/*  70:    */   public InstanceSavePanel()
/*  71:    */   {
/*  72: 92 */     setLayout(new BorderLayout());
/*  73: 93 */     this.arffFile_Tex = new TextField("arffoutput.arff");
/*  74: 94 */     add("Center", this.arffFile_Tex);
/*  75: 95 */     this.count_Lab = new Label("0 instances");
/*  76: 96 */     add("East", this.count_Lab);
/*  77:    */     
/*  78: 98 */     setBackground(Color.lightGray);
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void setDebug(boolean debug)
/*  82:    */   {
/*  83:102 */     this.b_Debug = debug;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public boolean getDebug()
/*  87:    */   {
/*  88:106 */     return this.b_Debug;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public void setArffFile(String newArffFile)
/*  92:    */   {
/*  93:110 */     this.arffFile_Tex.setText(newArffFile);
/*  94:    */   }
/*  95:    */   
/*  96:    */   public String getArffFile()
/*  97:    */   {
/*  98:114 */     return this.arffFile_Tex.getText();
/*  99:    */   }
/* 100:    */   
/* 101:    */   public void instanceProduced(InstanceEvent e)
/* 102:    */   {
/* 103:119 */     Object source = e.getSource();
/* 104:120 */     if ((source instanceof InstanceProducer)) {
/* 105:    */       try
/* 106:    */       {
/* 107:122 */         InstanceProducer a = (InstanceProducer)source;
/* 108:123 */         switch (e.getID())
/* 109:    */         {
/* 110:    */         case 1: 
/* 111:125 */           inputFormat(a.outputFormat());
/* 112:126 */           break;
/* 113:    */         case 2: 
/* 114:128 */           input(a.outputPeek());
/* 115:129 */           break;
/* 116:    */         case 3: 
/* 117:131 */           batchFinished();
/* 118:132 */           break;
/* 119:    */         default: 
/* 120:134 */           System.err.println("InstanceSavePanel::instanceProduced() - unknown event type");
/* 121:    */         }
/* 122:    */       }
/* 123:    */       catch (Exception ex)
/* 124:    */       {
/* 125:138 */         System.err.println(ex.getMessage());
/* 126:    */       }
/* 127:    */     } else {
/* 128:141 */       System.err.println("InstanceSavePanel::instanceProduced() - Unknown source object type");
/* 129:    */     }
/* 130:    */   }
/* 131:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.streams.InstanceSavePanel
 * JD-Core Version:    0.7.0.1
 */