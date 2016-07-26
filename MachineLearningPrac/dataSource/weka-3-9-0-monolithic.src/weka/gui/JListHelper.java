/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import javax.swing.DefaultListModel;
/*   5:    */ import javax.swing.JList;
/*   6:    */ import javax.swing.ListModel;
/*   7:    */ 
/*   8:    */ public class JListHelper
/*   9:    */ {
/*  10:    */   public static final int MOVE_UP = 0;
/*  11:    */   public static final int MOVE_DOWN = 1;
/*  12:    */   
/*  13:    */   protected static void moveItems(JList list, int moveby, int direction)
/*  14:    */   {
/*  15: 60 */     DefaultListModel model = (DefaultListModel)list.getModel();
/*  16:    */     int[] indices;
/*  17:    */     int i;
/*  18: 62 */     switch (direction)
/*  19:    */     {
/*  20:    */     case 0: 
/*  21: 64 */       indices = list.getSelectedIndices();
/*  22: 65 */       for (i = 0; i < indices.length; i++) {
/*  23: 66 */         if (indices[i] != 0)
/*  24:    */         {
/*  25: 68 */           Object o = model.remove(indices[i]);
/*  26: 69 */           indices[i] -= moveby;
/*  27: 70 */           model.insertElementAt(o, indices[i]);
/*  28:    */         }
/*  29:    */       }
/*  30: 72 */       list.setSelectedIndices(indices);
/*  31: 73 */       break;
/*  32:    */     case 1: 
/*  33: 76 */       indices = list.getSelectedIndices();
/*  34: 77 */       for (i = indices.length - 1; i >= 0; i--) {
/*  35: 78 */         if (indices[i] != model.getSize() - 1)
/*  36:    */         {
/*  37: 80 */           Object o = model.remove(indices[i]);
/*  38: 81 */           indices[i] += moveby;
/*  39: 82 */           model.insertElementAt(o, indices[i]);
/*  40:    */         }
/*  41:    */       }
/*  42: 84 */       list.setSelectedIndices(indices);
/*  43: 85 */       break;
/*  44:    */     default: 
/*  45: 88 */       System.err.println(JListHelper.class.getName() + ": direction '" + direction + "' is unknown!");
/*  46:    */     }
/*  47:    */   }
/*  48:    */   
/*  49:    */   public static void moveUp(JList list)
/*  50:    */   {
/*  51:100 */     if (canMoveUp(list)) {
/*  52:101 */       moveItems(list, 1, 0);
/*  53:    */     }
/*  54:    */   }
/*  55:    */   
/*  56:    */   public static void moveDown(JList list)
/*  57:    */   {
/*  58:110 */     if (canMoveDown(list)) {
/*  59:111 */       moveItems(list, 1, 1);
/*  60:    */     }
/*  61:    */   }
/*  62:    */   
/*  63:    */   public static void moveTop(JList list)
/*  64:    */   {
/*  65:123 */     if (canMoveUp(list))
/*  66:    */     {
/*  67:124 */       int[] indices = list.getSelectedIndices();
/*  68:125 */       int diff = indices[0];
/*  69:126 */       moveItems(list, diff, 0);
/*  70:    */     }
/*  71:    */   }
/*  72:    */   
/*  73:    */   public static void moveBottom(JList list)
/*  74:    */   {
/*  75:139 */     if (canMoveDown(list))
/*  76:    */     {
/*  77:140 */       int[] indices = list.getSelectedIndices();
/*  78:141 */       int diff = list.getModel().getSize() - 1 - indices[(indices.length - 1)];
/*  79:142 */       moveItems(list, diff, 1);
/*  80:    */     }
/*  81:    */   }
/*  82:    */   
/*  83:    */   public static boolean canMoveUp(JList list)
/*  84:    */   {
/*  85:155 */     boolean result = false;
/*  86:    */     
/*  87:157 */     int[] indices = list.getSelectedIndices();
/*  88:158 */     if ((indices.length > 0) && 
/*  89:159 */       (indices[0] > 0)) {
/*  90:160 */       result = true;
/*  91:    */     }
/*  92:163 */     return result;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public static boolean canMoveDown(JList list)
/*  96:    */   {
/*  97:175 */     boolean result = false;
/*  98:    */     
/*  99:177 */     int[] indices = list.getSelectedIndices();
/* 100:178 */     if ((indices.length > 0) && 
/* 101:179 */       (indices[(indices.length - 1)] < list.getModel().getSize() - 1)) {
/* 102:180 */       result = true;
/* 103:    */     }
/* 104:183 */     return result;
/* 105:    */   }
/* 106:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.JListHelper
 * JD-Core Version:    0.7.0.1
 */