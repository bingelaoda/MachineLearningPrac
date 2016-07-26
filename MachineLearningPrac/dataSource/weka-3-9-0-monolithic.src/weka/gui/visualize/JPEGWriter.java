/*   1:    */ package weka.gui.visualize;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Graphics;
/*   5:    */ import java.awt.Graphics2D;
/*   6:    */ import java.awt.image.BufferedImage;
/*   7:    */ import java.io.File;
/*   8:    */ import java.io.PrintStream;
/*   9:    */ import java.io.StringReader;
/*  10:    */ import java.util.Iterator;
/*  11:    */ import java.util.Locale;
/*  12:    */ import javax.imageio.IIOImage;
/*  13:    */ import javax.imageio.ImageIO;
/*  14:    */ import javax.imageio.ImageWriteParam;
/*  15:    */ import javax.imageio.ImageWriter;
/*  16:    */ import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
/*  17:    */ import javax.imageio.stream.ImageOutputStream;
/*  18:    */ import javax.swing.JComponent;
/*  19:    */ import weka.gui.treevisualizer.Node;
/*  20:    */ import weka.gui.treevisualizer.NodePlace;
/*  21:    */ import weka.gui.treevisualizer.PlaceNode2;
/*  22:    */ import weka.gui.treevisualizer.TreeBuild;
/*  23:    */ import weka.gui.treevisualizer.TreeVisualizer;
/*  24:    */ 
/*  25:    */ public class JPEGWriter
/*  26:    */   extends JComponentWriter
/*  27:    */ {
/*  28:    */   protected float m_Quality;
/*  29:    */   protected Color m_Background;
/*  30:    */   
/*  31:    */   public JPEGWriter() {}
/*  32:    */   
/*  33:    */   public JPEGWriter(JComponent c)
/*  34:    */   {
/*  35: 68 */     super(c);
/*  36:    */   }
/*  37:    */   
/*  38:    */   public JPEGWriter(JComponent c, File f)
/*  39:    */   {
/*  40: 78 */     super(c, f);
/*  41:    */     
/*  42: 80 */     this.m_Quality = 1.0F;
/*  43: 81 */     this.m_Background = Color.WHITE;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void initialize()
/*  47:    */   {
/*  48: 89 */     super.initialize();
/*  49:    */     
/*  50: 91 */     this.m_Quality = 1.0F;
/*  51: 92 */     this.m_Background = Color.WHITE;
/*  52: 93 */     setScalingEnabled(false);
/*  53:    */   }
/*  54:    */   
/*  55:    */   public String getDescription()
/*  56:    */   {
/*  57:104 */     return "JPEG-Image";
/*  58:    */   }
/*  59:    */   
/*  60:    */   public String getExtension()
/*  61:    */   {
/*  62:115 */     return ".jpg";
/*  63:    */   }
/*  64:    */   
/*  65:    */   public Color getBackground()
/*  66:    */   {
/*  67:124 */     return this.m_Background;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public void setBackground(Color c)
/*  71:    */   {
/*  72:133 */     this.m_Background = c;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public float getQuality()
/*  76:    */   {
/*  77:142 */     return this.m_Quality;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public void setQuality(float q)
/*  81:    */   {
/*  82:151 */     this.m_Quality = q;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public void generateOutput()
/*  86:    */     throws Exception
/*  87:    */   {
/*  88:169 */     BufferedImage bi = new BufferedImage(getComponent().getWidth(), getComponent().getHeight(), 1);
/*  89:    */     
/*  90:171 */     Graphics g = bi.getGraphics();
/*  91:172 */     g.setPaintMode();
/*  92:173 */     g.setColor(getBackground());
/*  93:174 */     if ((g instanceof Graphics2D)) {
/*  94:175 */       ((Graphics2D)g).scale(getXScale(), getYScale());
/*  95:    */     }
/*  96:177 */     g.fillRect(0, 0, getComponent().getWidth(), getComponent().getHeight());
/*  97:178 */     getComponent().printAll(g);
/*  98:    */     
/*  99:    */ 
/* 100:181 */     ImageWriter writer = null;
/* 101:182 */     Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName(getExtension().replace(".", ""));
/* 102:183 */     if (iter.hasNext()) {
/* 103:184 */       writer = (ImageWriter)iter.next();
/* 104:    */     } else {
/* 105:186 */       throw new Exception("No writer available for " + getDescription() + "!");
/* 106:    */     }
/* 107:190 */     ImageOutputStream ios = ImageIO.createImageOutputStream(getFile());
/* 108:191 */     writer.setOutput(ios);
/* 109:    */     
/* 110:    */ 
/* 111:194 */     ImageWriteParam param = new JPEGImageWriteParam(Locale.getDefault());
/* 112:195 */     param.setCompressionMode(2);
/* 113:196 */     param.setCompressionQuality(getQuality());
/* 114:    */     
/* 115:    */ 
/* 116:199 */     writer.write(null, new IIOImage(bi, null, null), param);
/* 117:    */     
/* 118:    */ 
/* 119:202 */     ios.flush();
/* 120:203 */     writer.dispose();
/* 121:204 */     ios.close();
/* 122:    */   }
/* 123:    */   
/* 124:    */   public static void main(String[] args)
/* 125:    */     throws Exception
/* 126:    */   {
/* 127:214 */     System.out.println("building TreeVisualizer...");
/* 128:215 */     TreeBuild builder = new TreeBuild();
/* 129:216 */     NodePlace arrange = new PlaceNode2();
/* 130:217 */     Node top = builder.create(new StringReader("digraph atree { top [label=\"the top\"] a [label=\"the first node\"] b [label=\"the second nodes\"] c [label=\"comes off of first\"] top->a top->b b->c }"));
/* 131:    */     
/* 132:    */ 
/* 133:220 */     TreeVisualizer tv = new TreeVisualizer(null, top, arrange);
/* 134:    */     
/* 135:222 */     tv.setSize(800, 600);
/* 136:    */     
/* 137:224 */     String filename = System.getProperty("java.io.tmpdir") + File.separator + "test.jpg";
/* 138:    */     
/* 139:226 */     System.out.println("outputting to '" + filename + "'...");
/* 140:227 */     toOutput(new JPEGWriter(), tv, new File(filename));
/* 141:    */     
/* 142:229 */     System.out.println("done!");
/* 143:    */   }
/* 144:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.visualize.JPEGWriter
 * JD-Core Version:    0.7.0.1
 */