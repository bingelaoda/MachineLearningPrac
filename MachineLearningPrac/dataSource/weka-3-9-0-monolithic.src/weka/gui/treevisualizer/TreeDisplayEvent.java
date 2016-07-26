/*  1:   */ package weka.gui.treevisualizer;
/*  2:   */ 
/*  3:   */ public class TreeDisplayEvent
/*  4:   */ {
/*  5:   */   public static final int NO_COMMAND = 0;
/*  6:   */   public static final int ADD_CHILDREN = 1;
/*  7:   */   public static final int REMOVE_CHILDREN = 2;
/*  8:   */   public static final int ACCEPT = 3;
/*  9:   */   public static final int CLASSIFY_CHILD = 4;
/* 10:   */   public static final int SEND_INSTANCES = 5;
/* 11:   */   private int m_command;
/* 12:   */   private String m_nodeId;
/* 13:   */   
/* 14:   */   public TreeDisplayEvent(int ar, String id)
/* 15:   */   {
/* 16:59 */     this.m_command = 0;
/* 17:60 */     if ((ar == 1) || (ar == 2) || (ar == 3) || (ar == 4) || (ar == 5)) {
/* 18:62 */       this.m_command = ar;
/* 19:   */     }
/* 20:64 */     this.m_nodeId = id;
/* 21:   */   }
/* 22:   */   
/* 23:   */   public int getCommand()
/* 24:   */   {
/* 25:71 */     return this.m_command;
/* 26:   */   }
/* 27:   */   
/* 28:   */   public String getID()
/* 29:   */   {
/* 30:78 */     return this.m_nodeId;
/* 31:   */   }
/* 32:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.treevisualizer.TreeDisplayEvent
 * JD-Core Version:    0.7.0.1
 */