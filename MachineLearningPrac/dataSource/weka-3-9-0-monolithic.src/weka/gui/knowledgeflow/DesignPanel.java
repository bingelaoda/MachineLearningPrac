/*  1:   */ package weka.gui.knowledgeflow;
/*  2:   */ 
/*  3:   */ import java.awt.BorderLayout;
/*  4:   */ import java.awt.event.KeyAdapter;
/*  5:   */ import java.awt.event.KeyEvent;
/*  6:   */ import java.util.ArrayList;
/*  7:   */ import java.util.List;
/*  8:   */ import java.util.Map;
/*  9:   */ import java.util.Map.Entry;
/* 10:   */ import javax.swing.BorderFactory;
/* 11:   */ import javax.swing.JPanel;
/* 12:   */ import javax.swing.JScrollPane;
/* 13:   */ import javax.swing.JTextField;
/* 14:   */ import javax.swing.tree.DefaultMutableTreeNode;
/* 15:   */ import javax.swing.tree.DefaultTreeModel;
/* 16:   */ import javax.swing.tree.TreeNode;
/* 17:   */ import javax.swing.tree.TreePath;
/* 18:   */ 
/* 19:   */ public class DesignPanel
/* 20:   */   extends JPanel
/* 21:   */ {
/* 22:   */   private static final long serialVersionUID = 3324733191950871564L;
/* 23:   */   
/* 24:   */   public DesignPanel(final StepTree stepTree)
/* 25:   */   {
/* 26:57 */     setLayout(new BorderLayout());
/* 27:58 */     JScrollPane treeView = new JScrollPane(stepTree);
/* 28:59 */     setBorder(BorderFactory.createTitledBorder("Design"));
/* 29:60 */     add(treeView, "Center");
/* 30:61 */     final JTextField searchField = new JTextField();
/* 31:62 */     add(searchField, "North");
/* 32:63 */     searchField.setToolTipText("Search (clear field to reset)");
/* 33:   */     
/* 34:65 */     searchField.addKeyListener(new KeyAdapter()
/* 35:   */     {
/* 36:   */       public void keyReleased(KeyEvent e)
/* 37:   */       {
/* 38:68 */         String searchTerm = searchField.getText();
/* 39:69 */         List<DefaultMutableTreeNode> nonhits = new ArrayList();
/* 40:   */         
/* 41:71 */         List<DefaultMutableTreeNode> hits = new ArrayList();
/* 42:   */         
/* 43:73 */         DefaultTreeModel model = (DefaultTreeModel)stepTree.getModel();
/* 44:   */         
/* 45:75 */         model.reload();
/* 46:77 */         for (Map.Entry<String, DefaultMutableTreeNode> entry : stepTree.getNodeTextIndex().entrySet())
/* 47:   */         {
/* 48:80 */           if ((entry.getValue() instanceof InvisibleNode)) {
/* 49:81 */             ((InvisibleNode)entry.getValue()).setVisible(true);
/* 50:   */           }
/* 51:84 */           if ((searchTerm != null) && (searchTerm.length() > 0)) {
/* 52:85 */             if (((String)entry.getKey()).contains(searchTerm.toLowerCase())) {
/* 53:86 */               hits.add(entry.getValue());
/* 54:   */             } else {
/* 55:88 */               nonhits.add(entry.getValue());
/* 56:   */             }
/* 57:   */           }
/* 58:   */         }
/* 59:93 */         if ((searchTerm == null) || (searchTerm.length() == 0)) {
/* 60:94 */           model.reload();
/* 61:   */         }
/* 62:97 */         if (hits.size() > 0)
/* 63:   */         {
/* 64:98 */           for (DefaultMutableTreeNode h : nonhits) {
/* 65:99 */             if ((h instanceof InvisibleNode)) {
/* 66::0 */               ((InvisibleNode)h).setVisible(false);
/* 67:   */             }
/* 68:   */           }
/* 69::3 */           model.reload();
/* 70::6 */           for (DefaultMutableTreeNode h : hits)
/* 71:   */           {
/* 72::7 */             TreeNode[] path = model.getPathToRoot(h);
/* 73::8 */             TreePath tpath = new TreePath(path);
/* 74::9 */             tpath = tpath.getParentPath();
/* 75:;0 */             stepTree.expandPath(tpath);
/* 76:   */           }
/* 77:   */         }
/* 78:   */       }
/* 79:   */     });
/* 80:   */   }
/* 81:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.DesignPanel
 * JD-Core Version:    0.7.0.1
 */