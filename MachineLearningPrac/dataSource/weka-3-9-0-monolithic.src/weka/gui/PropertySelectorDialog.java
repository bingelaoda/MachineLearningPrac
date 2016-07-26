/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.Dialog.ModalityType;
/*   6:    */ import java.awt.Frame;
/*   7:    */ import java.awt.event.ActionEvent;
/*   8:    */ import java.awt.event.ActionListener;
/*   9:    */ import java.beans.BeanInfo;
/*  10:    */ import java.beans.IntrospectionException;
/*  11:    */ import java.beans.Introspector;
/*  12:    */ import java.beans.PropertyDescriptor;
/*  13:    */ import java.beans.PropertyEditor;
/*  14:    */ import java.beans.PropertyEditorManager;
/*  15:    */ import java.io.PrintStream;
/*  16:    */ import java.lang.reflect.InvocationTargetException;
/*  17:    */ import java.lang.reflect.Method;
/*  18:    */ import javax.swing.Box;
/*  19:    */ import javax.swing.JButton;
/*  20:    */ import javax.swing.JDialog;
/*  21:    */ import javax.swing.JScrollPane;
/*  22:    */ import javax.swing.JTree;
/*  23:    */ import javax.swing.tree.DefaultMutableTreeNode;
/*  24:    */ import javax.swing.tree.TreePath;
/*  25:    */ import javax.swing.tree.TreeSelectionModel;
/*  26:    */ import weka.experiment.AveragingResultProducer;
/*  27:    */ import weka.experiment.PropertyNode;
/*  28:    */ 
/*  29:    */ public class PropertySelectorDialog
/*  30:    */   extends JDialog
/*  31:    */ {
/*  32:    */   private static final long serialVersionUID = -3155058124137930518L;
/*  33: 63 */   protected JButton m_SelectBut = new JButton("Select");
/*  34: 66 */   protected JButton m_CancelBut = new JButton("Cancel");
/*  35:    */   protected DefaultMutableTreeNode m_Root;
/*  36:    */   protected Object m_RootObject;
/*  37:    */   protected int m_Result;
/*  38:    */   protected Object[] m_ResultPath;
/*  39:    */   protected JTree m_Tree;
/*  40:    */   public static final int APPROVE_OPTION = 0;
/*  41:    */   public static final int CANCEL_OPTION = 1;
/*  42:    */   
/*  43:    */   public PropertySelectorDialog(Frame parentFrame, Object rootObject)
/*  44:    */   {
/*  45: 97 */     super(parentFrame, "Select a property", Dialog.ModalityType.DOCUMENT_MODAL);
/*  46: 98 */     this.m_CancelBut.addActionListener(new ActionListener()
/*  47:    */     {
/*  48:    */       public void actionPerformed(ActionEvent e)
/*  49:    */       {
/*  50:101 */         PropertySelectorDialog.this.m_Result = 1;
/*  51:102 */         PropertySelectorDialog.this.setVisible(false);
/*  52:    */       }
/*  53:104 */     });
/*  54:105 */     this.m_SelectBut.addActionListener(new ActionListener()
/*  55:    */     {
/*  56:    */       public void actionPerformed(ActionEvent e)
/*  57:    */       {
/*  58:109 */         TreePath tPath = PropertySelectorDialog.this.m_Tree.getSelectionPath();
/*  59:110 */         if (tPath == null)
/*  60:    */         {
/*  61:111 */           PropertySelectorDialog.this.m_Result = 1;
/*  62:    */         }
/*  63:    */         else
/*  64:    */         {
/*  65:113 */           PropertySelectorDialog.this.m_ResultPath = tPath.getPath();
/*  66:114 */           if ((PropertySelectorDialog.this.m_ResultPath == null) || (PropertySelectorDialog.this.m_ResultPath.length < 2)) {
/*  67:115 */             PropertySelectorDialog.this.m_Result = 1;
/*  68:    */           } else {
/*  69:117 */             PropertySelectorDialog.this.m_Result = 0;
/*  70:    */           }
/*  71:    */         }
/*  72:120 */         PropertySelectorDialog.this.setVisible(false);
/*  73:    */       }
/*  74:122 */     });
/*  75:123 */     this.m_RootObject = rootObject;
/*  76:124 */     this.m_Root = new DefaultMutableTreeNode(new PropertyNode(this.m_RootObject));
/*  77:125 */     createNodes(this.m_Root);
/*  78:    */     
/*  79:127 */     Container c = getContentPane();
/*  80:128 */     c.setLayout(new BorderLayout());
/*  81:    */     
/*  82:130 */     Box b1 = new Box(0);
/*  83:131 */     b1.add(this.m_SelectBut);
/*  84:132 */     b1.add(Box.createHorizontalStrut(10));
/*  85:133 */     b1.add(this.m_CancelBut);
/*  86:134 */     c.add(b1, "South");
/*  87:135 */     this.m_Tree = new JTree(this.m_Root);
/*  88:136 */     this.m_Tree.getSelectionModel().setSelectionMode(1);
/*  89:    */     
/*  90:138 */     c.add(new JScrollPane(this.m_Tree), "Center");
/*  91:139 */     pack();
/*  92:    */   }
/*  93:    */   
/*  94:    */   public int showDialog()
/*  95:    */   {
/*  96:149 */     this.m_Result = 1;
/*  97:150 */     setVisible(true);
/*  98:151 */     return this.m_Result;
/*  99:    */   }
/* 100:    */   
/* 101:    */   public PropertyNode[] getPath()
/* 102:    */   {
/* 103:161 */     PropertyNode[] result = new PropertyNode[this.m_ResultPath.length - 1];
/* 104:162 */     for (int i = 0; i < result.length; i++) {
/* 105:163 */       result[i] = ((PropertyNode)((DefaultMutableTreeNode)this.m_ResultPath[(i + 1)]).getUserObject());
/* 106:    */     }
/* 107:166 */     return result;
/* 108:    */   }
/* 109:    */   
/* 110:    */   protected void createNodes(DefaultMutableTreeNode localNode)
/* 111:    */   {
/* 112:176 */     PropertyNode pNode = (PropertyNode)localNode.getUserObject();
/* 113:177 */     Object localObject = pNode.value;
/* 114:    */     PropertyDescriptor[] localProperties;
/* 115:    */     try
/* 116:    */     {
/* 117:181 */       BeanInfo bi = Introspector.getBeanInfo(localObject.getClass());
/* 118:182 */       localProperties = bi.getPropertyDescriptors();
/* 119:    */     }
/* 120:    */     catch (IntrospectionException ex)
/* 121:    */     {
/* 122:184 */       System.err.println("PropertySelectorDialog: Couldn't introspect");
/* 123:185 */       return;
/* 124:    */     }
/* 125:189 */     for (PropertyDescriptor localPropertie : localProperties) {
/* 126:191 */       if ((!localPropertie.isHidden()) && (!localPropertie.isExpert()))
/* 127:    */       {
/* 128:194 */         String name = localPropertie.getDisplayName();
/* 129:195 */         Class<?> type = localPropertie.getPropertyType();
/* 130:196 */         Method getter = localPropertie.getReadMethod();
/* 131:197 */         Method setter = localPropertie.getWriteMethod();
/* 132:198 */         Object value = null;
/* 133:200 */         if ((getter != null) && (setter != null))
/* 134:    */         {
/* 135:    */           try
/* 136:    */           {
/* 137:204 */             Object[] args = new Object[0];
/* 138:205 */             value = getter.invoke(localObject, args);
/* 139:206 */             PropertyEditor editor = null;
/* 140:207 */             Class<?> pec = localPropertie.getPropertyEditorClass();
/* 141:208 */             if (pec != null) {
/* 142:    */               try
/* 143:    */               {
/* 144:210 */                 editor = (PropertyEditor)pec.newInstance();
/* 145:    */               }
/* 146:    */               catch (Exception ex) {}
/* 147:    */             }
/* 148:214 */             if (editor == null) {
/* 149:215 */               editor = PropertyEditorManager.findEditor(type);
/* 150:    */             }
/* 151:217 */             if ((editor == null) || (value == null)) {
/* 152:    */               continue;
/* 153:    */             }
/* 154:    */           }
/* 155:    */           catch (InvocationTargetException ex)
/* 156:    */           {
/* 157:221 */             System.err.println("Skipping property " + name + " ; exception on target: " + ex.getTargetException());
/* 158:    */             
/* 159:223 */             ex.getTargetException().printStackTrace();
/* 160:224 */             continue;
/* 161:    */           }
/* 162:    */           catch (Exception ex)
/* 163:    */           {
/* 164:226 */             System.err.println("Skipping property " + name + " ; exception: " + ex);
/* 165:227 */             ex.printStackTrace();
/* 166:228 */             continue;
/* 167:    */           }
/* 168:231 */           DefaultMutableTreeNode child = new DefaultMutableTreeNode(new PropertyNode(value, localPropertie, localObject.getClass()));
/* 169:    */           
/* 170:233 */           localNode.add(child);
/* 171:234 */           createNodes(child);
/* 172:    */         }
/* 173:    */       }
/* 174:    */     }
/* 175:    */   }
/* 176:    */   
/* 177:    */   public static void main(String[] args)
/* 178:    */   {
/* 179:    */     try
/* 180:    */     {
/* 181:246 */       GenericObjectEditor.registerEditors();
/* 182:    */       
/* 183:248 */       Object rp = new AveragingResultProducer();
/* 184:249 */       PropertySelectorDialog jd = new PropertySelectorDialog(null, rp);
/* 185:250 */       int result = jd.showDialog();
/* 186:251 */       if (result == 0)
/* 187:    */       {
/* 188:252 */         System.err.println("Property Selected");
/* 189:253 */         PropertyNode[] path = jd.getPath();
/* 190:254 */         for (int i = 0; i < path.length; i++)
/* 191:    */         {
/* 192:255 */           PropertyNode pn = path[i];
/* 193:256 */           System.err.println("" + (i + 1) + "  " + pn.toString() + " " + pn.value.toString());
/* 194:    */         }
/* 195:    */       }
/* 196:    */       else
/* 197:    */       {
/* 198:260 */         System.err.println("Cancelled");
/* 199:    */       }
/* 200:262 */       System.exit(0);
/* 201:    */     }
/* 202:    */     catch (Exception ex)
/* 203:    */     {
/* 204:264 */       ex.printStackTrace();
/* 205:265 */       System.err.println(ex.getMessage());
/* 206:    */     }
/* 207:    */   }
/* 208:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.PropertySelectorDialog
 * JD-Core Version:    0.7.0.1
 */