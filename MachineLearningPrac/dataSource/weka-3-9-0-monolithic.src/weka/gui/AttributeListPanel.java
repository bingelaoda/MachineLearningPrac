/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.Dimension;
/*   6:    */ import java.awt.event.WindowAdapter;
/*   7:    */ import java.awt.event.WindowEvent;
/*   8:    */ import java.io.BufferedReader;
/*   9:    */ import java.io.FileReader;
/*  10:    */ import java.io.PrintStream;
/*  11:    */ import javax.swing.JFrame;
/*  12:    */ import javax.swing.JPanel;
/*  13:    */ import javax.swing.JScrollPane;
/*  14:    */ import javax.swing.JTable;
/*  15:    */ import javax.swing.ListSelectionModel;
/*  16:    */ import javax.swing.table.AbstractTableModel;
/*  17:    */ import javax.swing.table.TableColumn;
/*  18:    */ import javax.swing.table.TableColumnModel;
/*  19:    */ import weka.core.Attribute;
/*  20:    */ import weka.core.Instances;
/*  21:    */ 
/*  22:    */ public class AttributeListPanel
/*  23:    */   extends JPanel
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = -2030706987910400362L;
/*  26:    */   
/*  27:    */   class AttributeTableModel
/*  28:    */     extends AbstractTableModel
/*  29:    */   {
/*  30:    */     private static final long serialVersionUID = -7345701953670327707L;
/*  31:    */     protected Instances m_Instances;
/*  32:    */     
/*  33:    */     public AttributeTableModel(Instances instances)
/*  34:    */     {
/*  35: 67 */       setInstances(instances);
/*  36:    */     }
/*  37:    */     
/*  38:    */     public void setInstances(Instances instances)
/*  39:    */     {
/*  40: 77 */       this.m_Instances = instances;
/*  41:    */     }
/*  42:    */     
/*  43:    */     public int getRowCount()
/*  44:    */     {
/*  45: 88 */       return this.m_Instances.numAttributes();
/*  46:    */     }
/*  47:    */     
/*  48:    */     public int getColumnCount()
/*  49:    */     {
/*  50: 99 */       return 2;
/*  51:    */     }
/*  52:    */     
/*  53:    */     public Object getValueAt(int row, int column)
/*  54:    */     {
/*  55:112 */       switch (column)
/*  56:    */       {
/*  57:    */       case 0: 
/*  58:114 */         return new Integer(row + 1);
/*  59:    */       case 1: 
/*  60:116 */         return this.m_Instances.attribute(row).name();
/*  61:    */       }
/*  62:118 */       return null;
/*  63:    */     }
/*  64:    */     
/*  65:    */     public String getColumnName(int column)
/*  66:    */     {
/*  67:131 */       switch (column)
/*  68:    */       {
/*  69:    */       case 0: 
/*  70:133 */         return new String("No.");
/*  71:    */       case 1: 
/*  72:135 */         return new String("Name");
/*  73:    */       }
/*  74:137 */       return null;
/*  75:    */     }
/*  76:    */     
/*  77:    */     public Class<?> getColumnClass(int col)
/*  78:    */     {
/*  79:149 */       return getValueAt(0, col).getClass();
/*  80:    */     }
/*  81:    */     
/*  82:    */     public boolean isCellEditable(int row, int col)
/*  83:    */     {
/*  84:162 */       return false;
/*  85:    */     }
/*  86:    */   }
/*  87:    */   
/*  88:167 */   protected JTable m_Table = new JTable();
/*  89:    */   protected AttributeTableModel m_Model;
/*  90:    */   
/*  91:    */   public AttributeListPanel()
/*  92:    */   {
/*  93:177 */     this.m_Table.setSelectionMode(0);
/*  94:178 */     this.m_Table.setColumnSelectionAllowed(false);
/*  95:179 */     this.m_Table.setPreferredScrollableViewportSize(new Dimension(250, 150));
/*  96:    */     
/*  97:181 */     setLayout(new BorderLayout());
/*  98:182 */     add(new JScrollPane(this.m_Table), "Center");
/*  99:    */   }
/* 100:    */   
/* 101:    */   public void setInstances(Instances newInstances)
/* 102:    */   {
/* 103:192 */     if (this.m_Model == null)
/* 104:    */     {
/* 105:193 */       this.m_Model = new AttributeTableModel(newInstances);
/* 106:194 */       this.m_Table.setModel(this.m_Model);
/* 107:195 */       TableColumnModel tcm = this.m_Table.getColumnModel();
/* 108:196 */       tcm.getColumn(0).setMaxWidth(60);
/* 109:197 */       tcm.getColumn(1).setMinWidth(100);
/* 110:    */     }
/* 111:    */     else
/* 112:    */     {
/* 113:199 */       this.m_Model.setInstances(newInstances);
/* 114:    */     }
/* 115:201 */     this.m_Table.sizeColumnsToFit(-1);
/* 116:202 */     this.m_Table.revalidate();
/* 117:203 */     this.m_Table.repaint();
/* 118:    */   }
/* 119:    */   
/* 120:    */   public ListSelectionModel getSelectionModel()
/* 121:    */   {
/* 122:213 */     return this.m_Table.getSelectionModel();
/* 123:    */   }
/* 124:    */   
/* 125:    */   public static void main(String[] args)
/* 126:    */   {
/* 127:    */     try
/* 128:    */     {
/* 129:224 */       if (args.length == 0) {
/* 130:225 */         throw new Exception("supply the name of an arff file");
/* 131:    */       }
/* 132:227 */       Instances i = new Instances(new BufferedReader(new FileReader(args[0])));
/* 133:    */       
/* 134:229 */       AttributeListPanel asp = new AttributeListPanel();
/* 135:230 */       JFrame jf = new JFrame("Attribute List Panel");
/* 136:    */       
/* 137:232 */       jf.getContentPane().setLayout(new BorderLayout());
/* 138:233 */       jf.getContentPane().add(asp, "Center");
/* 139:234 */       jf.addWindowListener(new WindowAdapter()
/* 140:    */       {
/* 141:    */         public void windowClosing(WindowEvent e)
/* 142:    */         {
/* 143:237 */           this.val$jf.dispose();
/* 144:238 */           System.exit(0);
/* 145:    */         }
/* 146:240 */       });
/* 147:241 */       jf.pack();
/* 148:242 */       jf.setVisible(true);
/* 149:243 */       asp.setInstances(i);
/* 150:    */     }
/* 151:    */     catch (Exception ex)
/* 152:    */     {
/* 153:245 */       ex.printStackTrace();
/* 154:246 */       System.err.println(ex.getMessage());
/* 155:    */     }
/* 156:    */   }
/* 157:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.AttributeListPanel
 * JD-Core Version:    0.7.0.1
 */