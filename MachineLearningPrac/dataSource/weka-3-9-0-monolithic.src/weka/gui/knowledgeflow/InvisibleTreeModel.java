/*   1:    */ package weka.gui.knowledgeflow;
/*   2:    */ 
/*   3:    */ import javax.swing.tree.DefaultTreeModel;
/*   4:    */ import javax.swing.tree.TreeNode;
/*   5:    */ 
/*   6:    */ public class InvisibleTreeModel
/*   7:    */   extends DefaultTreeModel
/*   8:    */ {
/*   9:    */   private static final long serialVersionUID = 6940101211275068260L;
/*  10:    */   protected boolean m_filterIsActive;
/*  11:    */   
/*  12:    */   public InvisibleTreeModel(TreeNode root)
/*  13:    */   {
/*  14: 49 */     this(root, false);
/*  15:    */   }
/*  16:    */   
/*  17:    */   public InvisibleTreeModel(TreeNode root, boolean asksAllowsChildren)
/*  18:    */   {
/*  19: 61 */     this(root, false, false);
/*  20:    */   }
/*  21:    */   
/*  22:    */   public InvisibleTreeModel(TreeNode root, boolean asksAllowsChildren, boolean filterIsActive)
/*  23:    */   {
/*  24: 75 */     super(root, asksAllowsChildren);
/*  25: 76 */     this.m_filterIsActive = filterIsActive;
/*  26:    */   }
/*  27:    */   
/*  28:    */   public void activateFilter(boolean newValue)
/*  29:    */   {
/*  30: 85 */     this.m_filterIsActive = newValue;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public boolean isActivatedFilter()
/*  34:    */   {
/*  35: 94 */     return this.m_filterIsActive;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public Object getChild(Object parent, int index)
/*  39:    */   {
/*  40: 99 */     if ((this.m_filterIsActive) && 
/*  41:100 */       ((parent instanceof InvisibleNode))) {
/*  42:101 */       return ((InvisibleNode)parent).getChildAt(index, this.m_filterIsActive);
/*  43:    */     }
/*  44:104 */     return ((TreeNode)parent).getChildAt(index);
/*  45:    */   }
/*  46:    */   
/*  47:    */   public int getChildCount(Object parent)
/*  48:    */   {
/*  49:109 */     if ((this.m_filterIsActive) && 
/*  50:110 */       ((parent instanceof InvisibleNode))) {
/*  51:111 */       return ((InvisibleNode)parent).getChildCount(this.m_filterIsActive);
/*  52:    */     }
/*  53:114 */     return ((TreeNode)parent).getChildCount();
/*  54:    */   }
/*  55:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.InvisibleTreeModel
 * JD-Core Version:    0.7.0.1
 */