/*   1:    */ package weka.gui.knowledgeflow;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import java.util.Vector;
/*   5:    */ import javax.swing.tree.DefaultMutableTreeNode;
/*   6:    */ import javax.swing.tree.TreeNode;
/*   7:    */ import weka.core.WekaEnumeration;
/*   8:    */ 
/*   9:    */ public class InvisibleNode
/*  10:    */   extends DefaultMutableTreeNode
/*  11:    */ {
/*  12:    */   private static final long serialVersionUID = -9064396835384819887L;
/*  13:    */   protected boolean m_isVisible;
/*  14:    */   
/*  15:    */   public InvisibleNode()
/*  16:    */   {
/*  17: 51 */     this(null);
/*  18:    */   }
/*  19:    */   
/*  20:    */   public InvisibleNode(Object userObject)
/*  21:    */   {
/*  22: 60 */     this(userObject, true, true);
/*  23:    */   }
/*  24:    */   
/*  25:    */   public InvisibleNode(Object userObject, boolean allowsChildren, boolean isVisible)
/*  26:    */   {
/*  27: 72 */     super(userObject, allowsChildren);
/*  28: 73 */     this.m_isVisible = isVisible;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public TreeNode getChildAt(int index, boolean filterIsActive)
/*  32:    */   {
/*  33: 84 */     if (!filterIsActive) {
/*  34: 85 */       return super.getChildAt(index);
/*  35:    */     }
/*  36: 87 */     if (this.children == null) {
/*  37: 88 */       throw new ArrayIndexOutOfBoundsException("node has no children");
/*  38:    */     }
/*  39: 91 */     int realIndex = -1;
/*  40: 92 */     int visibleIndex = -1;
/*  41:    */     
/*  42: 94 */     Enumeration<InvisibleNode> e = new WekaEnumeration(this.children);
/*  43: 95 */     while (e.hasMoreElements())
/*  44:    */     {
/*  45: 96 */       InvisibleNode node = (InvisibleNode)e.nextElement();
/*  46: 97 */       if (node.isVisible()) {
/*  47: 98 */         visibleIndex++;
/*  48:    */       }
/*  49:100 */       realIndex++;
/*  50:101 */       if (visibleIndex == index) {
/*  51:102 */         return (TreeNode)this.children.elementAt(realIndex);
/*  52:    */       }
/*  53:    */     }
/*  54:106 */     throw new ArrayIndexOutOfBoundsException("index unmatched");
/*  55:    */   }
/*  56:    */   
/*  57:    */   public int getChildCount(boolean filterIsActive)
/*  58:    */   {
/*  59:117 */     if (!filterIsActive) {
/*  60:118 */       return super.getChildCount();
/*  61:    */     }
/*  62:120 */     if (this.children == null) {
/*  63:121 */       return 0;
/*  64:    */     }
/*  65:124 */     int count = 0;
/*  66:    */     
/*  67:126 */     Enumeration<InvisibleNode> e = new WekaEnumeration(this.children);
/*  68:127 */     while (e.hasMoreElements())
/*  69:    */     {
/*  70:128 */       InvisibleNode node = (InvisibleNode)e.nextElement();
/*  71:129 */       if (node.isVisible()) {
/*  72:130 */         count++;
/*  73:    */       }
/*  74:    */     }
/*  75:134 */     return count;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void setVisible(boolean visible)
/*  79:    */   {
/*  80:143 */     this.m_isVisible = visible;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public boolean isVisible()
/*  84:    */   {
/*  85:152 */     return this.m_isVisible;
/*  86:    */   }
/*  87:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.InvisibleNode
 * JD-Core Version:    0.7.0.1
 */