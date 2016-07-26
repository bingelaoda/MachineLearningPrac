/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Component;
/*   5:    */ import java.awt.Container;
/*   6:    */ import java.awt.Dialog;
/*   7:    */ import java.awt.Dialog.ModalityType;
/*   8:    */ import java.awt.Frame;
/*   9:    */ import java.awt.GraphicsConfiguration;
/*  10:    */ import java.awt.Rectangle;
/*  11:    */ import java.awt.Window;
/*  12:    */ import java.awt.event.WindowAdapter;
/*  13:    */ import java.awt.event.WindowEvent;
/*  14:    */ import java.beans.PropertyEditor;
/*  15:    */ import javax.swing.JDialog;
/*  16:    */ import javax.swing.JInternalFrame;
/*  17:    */ 
/*  18:    */ public class PropertyDialog
/*  19:    */   extends JDialog
/*  20:    */ {
/*  21:    */   private static final long serialVersionUID = -2314850859392433539L;
/*  22:    */   private PropertyEditor m_Editor;
/*  23:    */   private Component m_EditorComponent;
/*  24:    */   
/*  25:    */   /**
/*  26:    */    * @deprecated
/*  27:    */    */
/*  28:    */   public PropertyDialog(PropertyEditor pe, int x, int y)
/*  29:    */   {
/*  30: 68 */     this((Frame)null, pe, x, y);
/*  31: 69 */     setVisible(true);
/*  32:    */   }
/*  33:    */   
/*  34:    */   public PropertyDialog(Dialog owner, PropertyEditor pe)
/*  35:    */   {
/*  36: 80 */     this(owner, pe, -1, -1);
/*  37:    */   }
/*  38:    */   
/*  39:    */   public PropertyDialog(Dialog owner, PropertyEditor pe, int x, int y)
/*  40:    */   {
/*  41: 93 */     super(owner, pe.getClass().getName(), Dialog.ModalityType.DOCUMENT_MODAL);
/*  42: 94 */     initialize(pe, x, y);
/*  43:    */   }
/*  44:    */   
/*  45:    */   public PropertyDialog(Frame owner, PropertyEditor pe)
/*  46:    */   {
/*  47:105 */     this(owner, pe, -1, -1);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public PropertyDialog(Frame owner, PropertyEditor pe, int x, int y)
/*  51:    */   {
/*  52:118 */     super(owner, pe.getClass().getName(), Dialog.ModalityType.DOCUMENT_MODAL);
/*  53:    */     
/*  54:120 */     initialize(pe, x, y);
/*  55:    */   }
/*  56:    */   
/*  57:    */   protected void initialize(PropertyEditor pe, int x, int y)
/*  58:    */   {
/*  59:131 */     addWindowListener(new WindowAdapter()
/*  60:    */     {
/*  61:    */       public void windowClosing(WindowEvent e)
/*  62:    */       {
/*  63:133 */         e.getWindow().dispose();
/*  64:    */       }
/*  65:135 */     });
/*  66:136 */     getContentPane().setLayout(new BorderLayout());
/*  67:    */     
/*  68:138 */     this.m_Editor = pe;
/*  69:139 */     this.m_EditorComponent = pe.getCustomEditor();
/*  70:140 */     getContentPane().add(this.m_EditorComponent, "Center");
/*  71:    */     
/*  72:142 */     pack();
/*  73:    */     
/*  74:144 */     int screenWidth = getGraphicsConfiguration().getBounds().width;
/*  75:145 */     int screenHeight = getGraphicsConfiguration().getBounds().height;
/*  76:148 */     if (getHeight() > screenHeight * 0.95D) {
/*  77:149 */       setSize(getWidth(), (int)(screenHeight * 0.95D));
/*  78:    */     }
/*  79:151 */     if ((x == -1) && (y == -1))
/*  80:    */     {
/*  81:152 */       setLocationRelativeTo(null);
/*  82:    */     }
/*  83:    */     else
/*  84:    */     {
/*  85:156 */       if (x + getWidth() > screenWidth) {
/*  86:157 */         x = screenWidth - getWidth();
/*  87:    */       }
/*  88:158 */       if (y + getHeight() > screenHeight) {
/*  89:159 */         y = screenHeight - getHeight();
/*  90:    */       }
/*  91:160 */       setLocation(x, y);
/*  92:    */     }
/*  93:    */   }
/*  94:    */   
/*  95:    */   public PropertyEditor getEditor()
/*  96:    */   {
/*  97:170 */     return this.m_Editor;
/*  98:    */   }
/*  99:    */   
/* 100:    */   public static Frame getParentFrame(Container c)
/* 101:    */   {
/* 102:183 */     Frame result = null;
/* 103:    */     
/* 104:185 */     Container parent = c;
/* 105:186 */     while (parent != null)
/* 106:    */     {
/* 107:187 */       if ((parent instanceof Frame))
/* 108:    */       {
/* 109:188 */         result = (Frame)parent;
/* 110:189 */         break;
/* 111:    */       }
/* 112:192 */       parent = parent.getParent();
/* 113:    */     }
/* 114:196 */     return result;
/* 115:    */   }
/* 116:    */   
/* 117:    */   public static JInternalFrame getParentInternalFrame(Container c)
/* 118:    */   {
/* 119:209 */     JInternalFrame result = null;
/* 120:    */     
/* 121:211 */     Container parent = c;
/* 122:212 */     while (parent != null)
/* 123:    */     {
/* 124:213 */       if ((parent instanceof JInternalFrame))
/* 125:    */       {
/* 126:214 */         result = (JInternalFrame)parent;
/* 127:215 */         break;
/* 128:    */       }
/* 129:218 */       parent = parent.getParent();
/* 130:    */     }
/* 131:222 */     return result;
/* 132:    */   }
/* 133:    */   
/* 134:    */   public static Dialog getParentDialog(Container c)
/* 135:    */   {
/* 136:235 */     Dialog result = null;
/* 137:    */     
/* 138:237 */     Container parent = c;
/* 139:238 */     while (parent != null)
/* 140:    */     {
/* 141:239 */       if ((parent instanceof Dialog))
/* 142:    */       {
/* 143:240 */         result = (Dialog)parent;
/* 144:241 */         break;
/* 145:    */       }
/* 146:244 */       parent = parent.getParent();
/* 147:    */     }
/* 148:248 */     return result;
/* 149:    */   }
/* 150:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.PropertyDialog
 * JD-Core Version:    0.7.0.1
 */