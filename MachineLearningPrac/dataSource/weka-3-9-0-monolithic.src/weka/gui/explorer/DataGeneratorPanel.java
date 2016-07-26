/*   1:    */ package weka.gui.explorer;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.beans.PropertyChangeEvent;
/*   5:    */ import java.beans.PropertyChangeListener;
/*   6:    */ import java.io.PrintWriter;
/*   7:    */ import java.io.StringReader;
/*   8:    */ import java.io.StringWriter;
/*   9:    */ import javax.swing.JOptionPane;
/*  10:    */ import javax.swing.JPanel;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.OptionHandler;
/*  13:    */ import weka.core.Utils;
/*  14:    */ import weka.datagenerators.DataGenerator;
/*  15:    */ import weka.datagenerators.classifiers.classification.RDG1;
/*  16:    */ import weka.gui.GenericObjectEditor;
/*  17:    */ import weka.gui.Logger;
/*  18:    */ import weka.gui.PropertyPanel;
/*  19:    */ import weka.gui.SysErrLog;
/*  20:    */ 
/*  21:    */ public class DataGeneratorPanel
/*  22:    */   extends JPanel
/*  23:    */ {
/*  24:    */   private static final long serialVersionUID = -2520408165350629380L;
/*  25: 56 */   protected GenericObjectEditor m_GeneratorEditor = new GenericObjectEditor();
/*  26: 59 */   protected Instances m_Instances = null;
/*  27: 62 */   protected StringWriter m_Output = new StringWriter();
/*  28: 65 */   protected Logger m_Log = new SysErrLog();
/*  29:    */   
/*  30:    */   public DataGeneratorPanel()
/*  31:    */   {
/*  32: 76 */     setLayout(new BorderLayout());
/*  33:    */     
/*  34: 78 */     add(new PropertyPanel(this.m_GeneratorEditor), "Center");
/*  35:    */     
/*  36:    */ 
/*  37: 81 */     this.m_GeneratorEditor.setClassType(DataGenerator.class);
/*  38: 82 */     this.m_GeneratorEditor.addPropertyChangeListener(new PropertyChangeListener()
/*  39:    */     {
/*  40:    */       public void propertyChange(PropertyChangeEvent e)
/*  41:    */       {
/*  42: 84 */         DataGeneratorPanel.this.repaint();
/*  43:    */       }
/*  44: 88 */     });
/*  45: 89 */     setGenerator(null);
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void setLog(Logger value)
/*  49:    */   {
/*  50: 98 */     this.m_Log = value;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public Instances getInstances()
/*  54:    */   {
/*  55:107 */     return this.m_Instances;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public String getOutput()
/*  59:    */   {
/*  60:116 */     return this.m_Output.toString();
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void setGenerator(DataGenerator value)
/*  64:    */   {
/*  65:125 */     if (value != null) {
/*  66:126 */       this.m_GeneratorEditor.setValue(value);
/*  67:    */     } else {
/*  68:128 */       this.m_GeneratorEditor.setValue(new RDG1());
/*  69:    */     }
/*  70:    */   }
/*  71:    */   
/*  72:    */   public DataGenerator getGenerator()
/*  73:    */   {
/*  74:138 */     return (DataGenerator)this.m_GeneratorEditor.getValue();
/*  75:    */   }
/*  76:    */   
/*  77:    */   public boolean execute()
/*  78:    */   {
/*  79:154 */     boolean result = true;
/*  80:155 */     DataGenerator generator = (DataGenerator)this.m_GeneratorEditor.getValue();
/*  81:156 */     String relName = generator.getRelationName();
/*  82:    */     
/*  83:158 */     String cname = generator.getClass().getName().replaceAll(".*\\.", "");
/*  84:159 */     String cmd = generator.getClass().getName();
/*  85:160 */     if ((generator instanceof OptionHandler)) {
/*  86:161 */       cmd = cmd + " " + Utils.joinOptions(generator.getOptions());
/*  87:    */     }
/*  88:    */     try
/*  89:    */     {
/*  90:164 */       this.m_Log.logMessage("Started " + cname);
/*  91:165 */       this.m_Log.logMessage("Command: " + cmd);
/*  92:166 */       this.m_Output = new StringWriter();
/*  93:167 */       generator.setOutput(new PrintWriter(this.m_Output));
/*  94:168 */       DataGenerator.makeData(generator, generator.getOptions());
/*  95:169 */       this.m_Instances = new Instances(new StringReader(getOutput()));
/*  96:170 */       this.m_Log.logMessage("Finished " + cname);
/*  97:    */     }
/*  98:    */     catch (Exception e)
/*  99:    */     {
/* 100:173 */       e.printStackTrace();
/* 101:174 */       JOptionPane.showMessageDialog(this, "Error generating data:\n" + e.getMessage(), "Error", 0);
/* 102:    */       
/* 103:    */ 
/* 104:177 */       this.m_Instances = null;
/* 105:178 */       this.m_Output = new StringWriter();
/* 106:179 */       result = false;
/* 107:    */     }
/* 108:182 */     generator.setRelationName(relName);
/* 109:    */     
/* 110:184 */     return result;
/* 111:    */   }
/* 112:    */   
/* 113:    */   static {}
/* 114:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.explorer.DataGeneratorPanel
 * JD-Core Version:    0.7.0.1
 */