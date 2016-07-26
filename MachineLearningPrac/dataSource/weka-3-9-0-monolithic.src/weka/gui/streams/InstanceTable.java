/*   1:    */ package weka.gui.streams;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import javax.swing.JPanel;
/*   6:    */ import javax.swing.JScrollPane;
/*   7:    */ import javax.swing.JTable;
/*   8:    */ import javax.swing.table.AbstractTableModel;
/*   9:    */ import javax.swing.table.TableModel;
/*  10:    */ import weka.core.Attribute;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ 
/*  14:    */ public class InstanceTable
/*  15:    */   extends JPanel
/*  16:    */   implements InstanceListener
/*  17:    */ {
/*  18:    */   private static final long serialVersionUID = -2462533698100834803L;
/*  19:    */   private final JTable m_InstanceTable;
/*  20:    */   private boolean m_Debug;
/*  21:    */   private Instances m_Instances;
/*  22:    */   
/*  23:    */   public void inputFormat(Instances instanceInfo)
/*  24:    */   {
/*  25: 54 */     if (this.m_Debug) {
/*  26: 55 */       System.err.println("InstanceTable::inputFormat()\n" + instanceInfo.toString());
/*  27:    */     }
/*  28: 58 */     this.m_Instances = instanceInfo;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public void input(Instance instance)
/*  32:    */     throws Exception
/*  33:    */   {
/*  34: 63 */     if (this.m_Debug) {
/*  35: 64 */       System.err.println("InstanceTable::input(" + instance + ")");
/*  36:    */     }
/*  37: 66 */     this.m_Instances.add(instance);
/*  38:    */   }
/*  39:    */   
/*  40:    */   public void batchFinished()
/*  41:    */   {
/*  42: 71 */     TableModel newModel = new AbstractTableModel()
/*  43:    */     {
/*  44:    */       private static final long serialVersionUID = 5447106383000555291L;
/*  45:    */       
/*  46:    */       public String getColumnName(int col)
/*  47:    */       {
/*  48: 76 */         return InstanceTable.this.m_Instances.attribute(col).name();
/*  49:    */       }
/*  50:    */       
/*  51:    */       public Class<?> getColumnClass(int col)
/*  52:    */       {
/*  53: 81 */         return "".getClass();
/*  54:    */       }
/*  55:    */       
/*  56:    */       public int getColumnCount()
/*  57:    */       {
/*  58: 86 */         return InstanceTable.this.m_Instances.numAttributes();
/*  59:    */       }
/*  60:    */       
/*  61:    */       public int getRowCount()
/*  62:    */       {
/*  63: 91 */         return InstanceTable.this.m_Instances.numInstances();
/*  64:    */       }
/*  65:    */       
/*  66:    */       public Object getValueAt(int row, int col)
/*  67:    */       {
/*  68: 96 */         return new String(InstanceTable.this.m_Instances.instance(row).toString(col));
/*  69:    */       }
/*  70: 98 */     };
/*  71: 99 */     this.m_InstanceTable.setModel(newModel);
/*  72:100 */     if (this.m_Debug) {
/*  73:101 */       System.err.println("InstanceTable::batchFinished()");
/*  74:    */     }
/*  75:    */   }
/*  76:    */   
/*  77:    */   public InstanceTable()
/*  78:    */   {
/*  79:107 */     setLayout(new BorderLayout());
/*  80:108 */     this.m_InstanceTable = new JTable();
/*  81:109 */     add("Center", new JScrollPane(this.m_InstanceTable));
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void setDebug(boolean debug)
/*  85:    */   {
/*  86:114 */     this.m_Debug = debug;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public boolean getDebug()
/*  90:    */   {
/*  91:119 */     return this.m_Debug;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public void instanceProduced(InstanceEvent e)
/*  95:    */   {
/*  96:125 */     Object source = e.getSource();
/*  97:126 */     if ((source instanceof InstanceProducer)) {
/*  98:    */       try
/*  99:    */       {
/* 100:128 */         InstanceProducer a = (InstanceProducer)source;
/* 101:129 */         switch (e.getID())
/* 102:    */         {
/* 103:    */         case 1: 
/* 104:131 */           inputFormat(a.outputFormat());
/* 105:132 */           break;
/* 106:    */         case 2: 
/* 107:134 */           input(a.outputPeek());
/* 108:135 */           break;
/* 109:    */         case 3: 
/* 110:137 */           batchFinished();
/* 111:138 */           break;
/* 112:    */         default: 
/* 113:140 */           System.err.println("InstanceTable::instanceProduced() - unknown event type");
/* 114:    */         }
/* 115:    */       }
/* 116:    */       catch (Exception ex)
/* 117:    */       {
/* 118:145 */         System.err.println(ex.getMessage());
/* 119:    */       }
/* 120:    */     } else {
/* 121:148 */       System.err.println("InstanceTable::instanceProduced() - Unknown source object type");
/* 122:    */     }
/* 123:    */   }
/* 124:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.streams.InstanceTable
 * JD-Core Version:    0.7.0.1
 */