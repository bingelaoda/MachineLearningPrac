/*   1:    */ package weka.gui.experiment;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Component;
/*   5:    */ import java.awt.Container;
/*   6:    */ import java.awt.Dimension;
/*   7:    */ import java.awt.GridBagConstraints;
/*   8:    */ import java.awt.GridBagLayout;
/*   9:    */ import java.awt.Insets;
/*  10:    */ import java.awt.event.ActionEvent;
/*  11:    */ import java.awt.event.ActionListener;
/*  12:    */ import java.awt.event.WindowAdapter;
/*  13:    */ import java.awt.event.WindowEvent;
/*  14:    */ import java.beans.PropertyChangeEvent;
/*  15:    */ import java.beans.PropertyChangeListener;
/*  16:    */ import java.beans.PropertyDescriptor;
/*  17:    */ import java.io.PrintStream;
/*  18:    */ import java.lang.reflect.Array;
/*  19:    */ import java.util.ArrayList;
/*  20:    */ import javax.swing.BorderFactory;
/*  21:    */ import javax.swing.ComboBoxModel;
/*  22:    */ import javax.swing.DefaultComboBoxModel;
/*  23:    */ import javax.swing.JButton;
/*  24:    */ import javax.swing.JComboBox;
/*  25:    */ import javax.swing.JComponent;
/*  26:    */ import javax.swing.JFrame;
/*  27:    */ import javax.swing.JPanel;
/*  28:    */ import weka.experiment.Experiment;
/*  29:    */ import weka.experiment.PropertyNode;
/*  30:    */ import weka.gui.GenericArrayEditor;
/*  31:    */ import weka.gui.PropertySelectorDialog;
/*  32:    */ 
/*  33:    */ public class GeneratorPropertyIteratorPanel
/*  34:    */   extends JPanel
/*  35:    */   implements ActionListener
/*  36:    */ {
/*  37:    */   private static final long serialVersionUID = -6026938995241632139L;
/*  38: 67 */   protected JButton m_ConfigureBut = new JButton("Select property...");
/*  39: 70 */   protected JComboBox m_StatusBox = new JComboBox();
/*  40: 73 */   protected GenericArrayEditor m_ArrayEditor = new GenericArrayEditor();
/*  41:    */   protected Experiment m_Exp;
/*  42: 81 */   protected ArrayList<ActionListener> m_Listeners = new ArrayList();
/*  43:    */   
/*  44:    */   public GeneratorPropertyIteratorPanel()
/*  45:    */   {
/*  46: 88 */     String[] options = { "Disabled", "Enabled" };
/*  47: 89 */     ComboBoxModel cbm = new DefaultComboBoxModel(options);
/*  48: 90 */     this.m_StatusBox.setModel(cbm);
/*  49: 91 */     this.m_StatusBox.setSelectedIndex(0);
/*  50: 92 */     this.m_StatusBox.addActionListener(this);
/*  51: 93 */     this.m_StatusBox.setEnabled(false);
/*  52: 94 */     this.m_ConfigureBut.setEnabled(false);
/*  53: 95 */     this.m_ConfigureBut.addActionListener(this);
/*  54: 96 */     JPanel buttons = new JPanel();
/*  55: 97 */     GridBagLayout gb = new GridBagLayout();
/*  56: 98 */     GridBagConstraints constraints = new GridBagConstraints();
/*  57: 99 */     buttons.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
/*  58:    */     
/*  59:101 */     buttons.setLayout(gb);
/*  60:102 */     constraints.gridx = 0;
/*  61:103 */     constraints.gridy = 0;
/*  62:104 */     constraints.weightx = 5.0D;
/*  63:105 */     constraints.fill = 2;
/*  64:106 */     constraints.gridwidth = 1;
/*  65:107 */     constraints.gridheight = 1;
/*  66:108 */     constraints.insets = new Insets(0, 2, 0, 2);
/*  67:109 */     buttons.add(this.m_StatusBox, constraints);
/*  68:110 */     constraints.gridx = 1;
/*  69:111 */     constraints.gridy = 0;
/*  70:112 */     constraints.weightx = 5.0D;
/*  71:113 */     constraints.gridwidth = 1;
/*  72:114 */     constraints.gridheight = 1;
/*  73:115 */     buttons.add(this.m_ConfigureBut, constraints);
/*  74:116 */     buttons.setMaximumSize(new Dimension(buttons.getMaximumSize().width, buttons.getMinimumSize().height));
/*  75:    */     
/*  76:118 */     setBorder(BorderFactory.createTitledBorder("Generator properties"));
/*  77:119 */     setLayout(new BorderLayout());
/*  78:120 */     add(buttons, "North");
/*  79:    */     
/*  80:122 */     ((JComponent)this.m_ArrayEditor.getCustomEditor()).setBorder(BorderFactory.createEtchedBorder());
/*  81:    */     
/*  82:124 */     this.m_ArrayEditor.addPropertyChangeListener(new PropertyChangeListener()
/*  83:    */     {
/*  84:    */       public void propertyChange(PropertyChangeEvent e)
/*  85:    */       {
/*  86:127 */         System.err.println("Updating experiment property iterator array");
/*  87:128 */         GeneratorPropertyIteratorPanel.this.m_Exp.setPropertyArray(GeneratorPropertyIteratorPanel.this.m_ArrayEditor.getValue());
/*  88:    */       }
/*  89:130 */     });
/*  90:131 */     add(this.m_ArrayEditor.getCustomEditor(), "Center");
/*  91:    */   }
/*  92:    */   
/*  93:    */   public GeneratorPropertyIteratorPanel(Experiment exp)
/*  94:    */   {
/*  95:141 */     this();
/*  96:142 */     setExperiment(exp);
/*  97:    */   }
/*  98:    */   
/*  99:    */   public boolean getEditorActive()
/* 100:    */   {
/* 101:152 */     if (this.m_StatusBox.getSelectedIndex() == 0) {
/* 102:153 */       return false;
/* 103:    */     }
/* 104:156 */     return true;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public void setExperiment(Experiment exp)
/* 108:    */   {
/* 109:166 */     this.m_Exp = exp;
/* 110:167 */     this.m_StatusBox.setEnabled(true);
/* 111:168 */     this.m_ArrayEditor.setValue(this.m_Exp.getPropertyArray());
/* 112:169 */     if (this.m_Exp.getPropertyArray() == null)
/* 113:    */     {
/* 114:170 */       this.m_StatusBox.setSelectedIndex(0);
/* 115:171 */       this.m_ConfigureBut.setEnabled(false);
/* 116:    */     }
/* 117:    */     else
/* 118:    */     {
/* 119:173 */       this.m_StatusBox.setSelectedIndex(this.m_Exp.getUsePropertyIterator() ? 1 : 0);
/* 120:174 */       this.m_ConfigureBut.setEnabled(this.m_Exp.getUsePropertyIterator());
/* 121:    */     }
/* 122:176 */     validate();
/* 123:    */   }
/* 124:    */   
/* 125:    */   protected int selectProperty()
/* 126:    */   {
/* 127:187 */     PropertySelectorDialog jd = new PropertySelectorDialog(null, this.m_Exp.getResultProducer());
/* 128:    */     
/* 129:189 */     jd.setLocationRelativeTo(this);
/* 130:190 */     int result = jd.showDialog();
/* 131:191 */     if (result == 0)
/* 132:    */     {
/* 133:192 */       System.err.println("Property Selected");
/* 134:193 */       PropertyNode[] path = jd.getPath();
/* 135:194 */       Object value = path[(path.length - 1)].value;
/* 136:195 */       PropertyDescriptor property = path[(path.length - 1)].property;
/* 137:    */       
/* 138:197 */       Class<?> propertyClass = property.getPropertyType();
/* 139:198 */       this.m_Exp.setPropertyPath(path);
/* 140:199 */       this.m_Exp.setPropertyArray(Array.newInstance(propertyClass, 1));
/* 141:200 */       Array.set(this.m_Exp.getPropertyArray(), 0, value);
/* 142:    */       
/* 143:202 */       this.m_ArrayEditor.setValue(this.m_Exp.getPropertyArray());
/* 144:203 */       this.m_ArrayEditor.getCustomEditor().repaint();
/* 145:204 */       System.err.println("Set new array to array editor");
/* 146:    */     }
/* 147:    */     else
/* 148:    */     {
/* 149:206 */       System.err.println("Cancelled");
/* 150:    */     }
/* 151:208 */     return result;
/* 152:    */   }
/* 153:    */   
/* 154:    */   public void actionPerformed(ActionEvent e)
/* 155:    */   {
/* 156:219 */     if (e.getSource() == this.m_ConfigureBut)
/* 157:    */     {
/* 158:220 */       selectProperty();
/* 159:    */     }
/* 160:221 */     else if (e.getSource() == this.m_StatusBox)
/* 161:    */     {
/* 162:223 */       for (int i = 0; i < this.m_Listeners.size(); i++)
/* 163:    */       {
/* 164:224 */         ActionListener temp = (ActionListener)this.m_Listeners.get(i);
/* 165:225 */         temp.actionPerformed(new ActionEvent(this, 1001, "Editor status change"));
/* 166:    */       }
/* 167:230 */       if (this.m_StatusBox.getSelectedIndex() == 0)
/* 168:    */       {
/* 169:231 */         this.m_Exp.setUsePropertyIterator(false);
/* 170:232 */         this.m_ConfigureBut.setEnabled(false);
/* 171:233 */         this.m_ArrayEditor.getCustomEditor().setEnabled(false);
/* 172:234 */         this.m_ArrayEditor.setValue(null);
/* 173:235 */         validate();
/* 174:    */       }
/* 175:    */       else
/* 176:    */       {
/* 177:237 */         if (this.m_Exp.getPropertyArray() == null) {
/* 178:238 */           selectProperty();
/* 179:    */         }
/* 180:240 */         if (this.m_Exp.getPropertyArray() == null)
/* 181:    */         {
/* 182:241 */           this.m_StatusBox.setSelectedIndex(0);
/* 183:    */         }
/* 184:    */         else
/* 185:    */         {
/* 186:243 */           this.m_Exp.setUsePropertyIterator(true);
/* 187:244 */           this.m_ConfigureBut.setEnabled(true);
/* 188:245 */           this.m_ArrayEditor.getCustomEditor().setEnabled(true);
/* 189:    */         }
/* 190:247 */         validate();
/* 191:    */       }
/* 192:    */     }
/* 193:    */   }
/* 194:    */   
/* 195:    */   public void addActionListener(ActionListener newA)
/* 196:    */   {
/* 197:258 */     this.m_Listeners.add(newA);
/* 198:    */   }
/* 199:    */   
/* 200:    */   public static void main(String[] args)
/* 201:    */   {
/* 202:    */     try
/* 203:    */     {
/* 204:269 */       JFrame jf = new JFrame("Generator Property Iterator");
/* 205:270 */       jf.getContentPane().setLayout(new BorderLayout());
/* 206:271 */       GeneratorPropertyIteratorPanel gp = new GeneratorPropertyIteratorPanel();
/* 207:272 */       jf.getContentPane().add(gp, "Center");
/* 208:273 */       jf.addWindowListener(new WindowAdapter()
/* 209:    */       {
/* 210:    */         public void windowClosing(WindowEvent e)
/* 211:    */         {
/* 212:276 */           this.val$jf.dispose();
/* 213:277 */           System.exit(0);
/* 214:    */         }
/* 215:279 */       });
/* 216:280 */       jf.pack();
/* 217:281 */       jf.setVisible(true);
/* 218:282 */       System.err.println("Short nap");
/* 219:283 */       Thread.sleep(3000L);
/* 220:284 */       System.err.println("Done");
/* 221:285 */       gp.setExperiment(new Experiment());
/* 222:    */     }
/* 223:    */     catch (Exception ex)
/* 224:    */     {
/* 225:287 */       ex.printStackTrace();
/* 226:288 */       System.err.println(ex.getMessage());
/* 227:    */     }
/* 228:    */   }
/* 229:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.experiment.GeneratorPropertyIteratorPanel
 * JD-Core Version:    0.7.0.1
 */