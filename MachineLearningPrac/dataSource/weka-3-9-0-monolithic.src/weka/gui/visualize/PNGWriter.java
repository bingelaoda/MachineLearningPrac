/*   1:    */ package weka.gui.visualize;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Graphics;
/*   5:    */ import java.awt.Graphics2D;
/*   6:    */ import java.awt.image.BufferedImage;
/*   7:    */ import java.io.File;
/*   8:    */ import java.io.PrintStream;
/*   9:    */ import java.io.StringReader;
/*  10:    */ import javax.imageio.ImageIO;
/*  11:    */ import javax.swing.JComponent;
/*  12:    */ import weka.gui.treevisualizer.Node;
/*  13:    */ import weka.gui.treevisualizer.NodePlace;
/*  14:    */ import weka.gui.treevisualizer.PlaceNode2;
/*  15:    */ import weka.gui.treevisualizer.TreeBuild;
/*  16:    */ import weka.gui.treevisualizer.TreeVisualizer;
/*  17:    */ 
/*  18:    */ public class PNGWriter
/*  19:    */   extends JComponentWriter
/*  20:    */ {
/*  21:    */   protected Color m_Background;
/*  22:    */   
/*  23:    */   public PNGWriter() {}
/*  24:    */   
/*  25:    */   public PNGWriter(JComponent c)
/*  26:    */   {
/*  27: 58 */     super(c);
/*  28:    */   }
/*  29:    */   
/*  30:    */   public PNGWriter(JComponent c, File f)
/*  31:    */   {
/*  32: 68 */     super(c, f);
/*  33:    */   }
/*  34:    */   
/*  35:    */   public void initialize()
/*  36:    */   {
/*  37: 75 */     super.initialize();
/*  38:    */     
/*  39: 77 */     setScalingEnabled(false);
/*  40:    */   }
/*  41:    */   
/*  42:    */   public String getDescription()
/*  43:    */   {
/*  44: 87 */     return "PNG-Image";
/*  45:    */   }
/*  46:    */   
/*  47:    */   public String getExtension()
/*  48:    */   {
/*  49: 97 */     return ".png";
/*  50:    */   }
/*  51:    */   
/*  52:    */   public Color getBackground()
/*  53:    */   {
/*  54:106 */     return this.m_Background;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void setBackground(Color c)
/*  58:    */   {
/*  59:115 */     this.m_Background = c;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void generateOutput()
/*  63:    */     throws Exception
/*  64:    */   {
/*  65:127 */     BufferedImage bi = new BufferedImage(getComponent().getWidth(), getComponent().getHeight(), 1);
/*  66:128 */     Graphics g = bi.getGraphics();
/*  67:129 */     g.setPaintMode();
/*  68:130 */     g.setColor(getBackground());
/*  69:131 */     if ((g instanceof Graphics2D)) {
/*  70:132 */       ((Graphics2D)g).scale(getXScale(), getYScale());
/*  71:    */     }
/*  72:133 */     g.fillRect(0, 0, getComponent().getWidth(), getComponent().getHeight());
/*  73:134 */     getComponent().printAll(g);
/*  74:135 */     ImageIO.write(bi, "png", getFile());
/*  75:    */   }
/*  76:    */   
/*  77:    */   public static void main(String[] args)
/*  78:    */     throws Exception
/*  79:    */   {
/*  80:145 */     System.out.println("building TreeVisualizer...");
/*  81:146 */     TreeBuild builder = new TreeBuild();
/*  82:147 */     NodePlace arrange = new PlaceNode2();
/*  83:148 */     Node top = builder.create(new StringReader("digraph atree { top [label=\"the top\"] a [label=\"the first node\"] b [label=\"the second nodes\"] c [label=\"comes off of first\"] top->a top->b b->c }"));
/*  84:149 */     TreeVisualizer tv = new TreeVisualizer(null, top, arrange);
/*  85:150 */     tv.setSize(800, 600);
/*  86:    */     
/*  87:152 */     String filename = System.getProperty("java.io.tmpdir") + File.separator + "test.png";
/*  88:153 */     System.out.println("outputting to '" + filename + "'...");
/*  89:154 */     toOutput(new PNGWriter(), tv, new File(filename));
/*  90:    */     
/*  91:156 */     System.out.println("done!");
/*  92:    */   }
/*  93:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.visualize.PNGWriter
 * JD-Core Version:    0.7.0.1
 */