/*   1:    */ package weka.gui.visualize;
/*   2:    */ 
/*   3:    */ import java.io.BufferedOutputStream;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.FileOutputStream;
/*   6:    */ import java.io.PrintStream;
/*   7:    */ import java.io.StringReader;
/*   8:    */ import javax.swing.JComponent;
/*   9:    */ import weka.gui.treevisualizer.Node;
/*  10:    */ import weka.gui.treevisualizer.NodePlace;
/*  11:    */ import weka.gui.treevisualizer.PlaceNode2;
/*  12:    */ import weka.gui.treevisualizer.TreeBuild;
/*  13:    */ import weka.gui.treevisualizer.TreeVisualizer;
/*  14:    */ 
/*  15:    */ public class PostscriptWriter
/*  16:    */   extends JComponentWriter
/*  17:    */ {
/*  18:    */   public PostscriptWriter()
/*  19:    */   {
/*  20: 50 */     super(null);
/*  21:    */   }
/*  22:    */   
/*  23:    */   public PostscriptWriter(JComponent c)
/*  24:    */   {
/*  25: 59 */     super(c);
/*  26:    */   }
/*  27:    */   
/*  28:    */   public PostscriptWriter(JComponent c, File f)
/*  29:    */   {
/*  30: 69 */     super(c, f);
/*  31:    */   }
/*  32:    */   
/*  33:    */   public String getDescription()
/*  34:    */   {
/*  35: 77 */     return "Postscript-File";
/*  36:    */   }
/*  37:    */   
/*  38:    */   public String getExtension()
/*  39:    */   {
/*  40: 86 */     return ".eps";
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void generateOutput()
/*  44:    */     throws Exception
/*  45:    */   {
/*  46: 98 */     BufferedOutputStream ostrm = null;
/*  47:    */     try
/*  48:    */     {
/*  49:101 */       ostrm = new BufferedOutputStream(new FileOutputStream(getFile()));
/*  50:102 */       PostscriptGraphics psg = new PostscriptGraphics(getComponent().getHeight(), getComponent().getWidth(), ostrm);
/*  51:103 */       psg.setFont(getComponent().getFont());
/*  52:104 */       psg.scale(getXScale(), getYScale());
/*  53:105 */       getComponent().printAll(psg);
/*  54:106 */       psg.finished(); return;
/*  55:    */     }
/*  56:    */     catch (Exception e)
/*  57:    */     {
/*  58:109 */       System.err.println(e);
/*  59:    */     }
/*  60:    */     finally
/*  61:    */     {
/*  62:112 */       if (ostrm != null) {
/*  63:    */         try
/*  64:    */         {
/*  65:114 */           ostrm.close();
/*  66:    */         }
/*  67:    */         catch (Exception e) {}
/*  68:    */       }
/*  69:    */     }
/*  70:    */   }
/*  71:    */   
/*  72:    */   public static void main(String[] args)
/*  73:    */     throws Exception
/*  74:    */   {
/*  75:126 */     System.out.println("building TreeVisualizer...");
/*  76:127 */     TreeBuild builder = new TreeBuild();
/*  77:128 */     NodePlace arrange = new PlaceNode2();
/*  78:129 */     Node top = builder.create(new StringReader("digraph atree { top [label=\"the top\"] a [label=\"the first node\"] b [label=\"the second nodes\"] c [label=\"comes off of first\"] top->a top->b b->c }"));
/*  79:130 */     TreeVisualizer tv = new TreeVisualizer(null, top, arrange);
/*  80:131 */     tv.setSize(800, 600);
/*  81:    */     
/*  82:133 */     String filename = System.getProperty("java.io.tmpdir") + "test.eps";
/*  83:134 */     System.out.println("outputting to '" + filename + "'...");
/*  84:135 */     toOutput(new PostscriptWriter(), tv, new File(filename));
/*  85:    */     
/*  86:137 */     System.out.println("done!");
/*  87:    */   }
/*  88:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.visualize.PostscriptWriter
 * JD-Core Version:    0.7.0.1
 */