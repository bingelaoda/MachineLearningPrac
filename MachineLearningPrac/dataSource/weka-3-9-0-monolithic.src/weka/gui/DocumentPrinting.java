/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.Dimension;
/*   4:    */ import java.awt.Graphics;
/*   5:    */ import java.awt.Graphics2D;
/*   6:    */ import java.awt.Rectangle;
/*   7:    */ import java.awt.Shape;
/*   8:    */ import java.awt.print.PageFormat;
/*   9:    */ import java.awt.print.Printable;
/*  10:    */ import java.awt.print.PrinterException;
/*  11:    */ import java.awt.print.PrinterJob;
/*  12:    */ import java.io.PrintStream;
/*  13:    */ import javax.swing.JTextPane;
/*  14:    */ import javax.swing.plaf.TextUI;
/*  15:    */ import javax.swing.text.Document;
/*  16:    */ import javax.swing.text.View;
/*  17:    */ 
/*  18:    */ public class DocumentPrinting
/*  19:    */   implements Printable
/*  20:    */ {
/*  21: 48 */   protected int m_CurrentPage = -1;
/*  22:    */   protected JTextPane m_PrintPane;
/*  23: 54 */   protected double m_PageEndY = 0.0D;
/*  24: 57 */   protected double m_PageStartY = 0.0D;
/*  25: 60 */   protected boolean m_ScaleWidthToFit = true;
/*  26:    */   protected PageFormat m_PageFormat;
/*  27:    */   protected PrinterJob m_PrinterJob;
/*  28:    */   
/*  29:    */   public DocumentPrinting()
/*  30:    */   {
/*  31: 72 */     this.m_PrintPane = new JTextPane();
/*  32: 73 */     this.m_PageFormat = new PageFormat();
/*  33: 74 */     this.m_PrinterJob = PrinterJob.getPrinterJob();
/*  34:    */   }
/*  35:    */   
/*  36:    */   protected void pageDialog()
/*  37:    */   {
/*  38: 81 */     this.m_PageFormat = this.m_PrinterJob.pageDialog(this.m_PageFormat);
/*  39:    */   }
/*  40:    */   
/*  41:    */   public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
/*  42:    */   {
/*  43: 95 */     double scale = 1.0D;
/*  44:    */     
/*  45:    */ 
/*  46:    */ 
/*  47: 99 */     Graphics2D graphics2D = (Graphics2D)graphics;
/*  48:100 */     this.m_PrintPane.setSize((int)pageFormat.getImageableWidth(), 2147483647);
/*  49:101 */     this.m_PrintPane.validate();
/*  50:    */     
/*  51:103 */     View rootView = this.m_PrintPane.getUI().getRootView(this.m_PrintPane);
/*  52:105 */     if ((this.m_ScaleWidthToFit) && (this.m_PrintPane.getMinimumSize().getWidth() > pageFormat.getImageableWidth()))
/*  53:    */     {
/*  54:106 */       scale = pageFormat.getImageableWidth() / this.m_PrintPane.getMinimumSize().getWidth();
/*  55:    */       
/*  56:108 */       graphics2D.scale(scale, scale);
/*  57:    */     }
/*  58:111 */     graphics2D.setClip((int)(pageFormat.getImageableX() / scale), (int)(pageFormat.getImageableY() / scale), (int)(pageFormat.getImageableWidth() / scale), (int)(pageFormat.getImageableHeight() / scale));
/*  59:117 */     if (pageIndex > this.m_CurrentPage)
/*  60:    */     {
/*  61:118 */       this.m_CurrentPage = pageIndex;
/*  62:119 */       this.m_PageStartY += this.m_PageEndY;
/*  63:120 */       this.m_PageEndY = graphics2D.getClipBounds().getHeight();
/*  64:    */     }
/*  65:123 */     graphics2D.translate(graphics2D.getClipBounds().getX(), graphics2D.getClipBounds().getY());
/*  66:    */     
/*  67:    */ 
/*  68:126 */     Rectangle allocation = new Rectangle(0, (int)-this.m_PageStartY, (int)this.m_PrintPane.getMinimumSize().getWidth(), (int)this.m_PrintPane.getPreferredSize().getHeight());
/*  69:132 */     if (printView(graphics2D, allocation, rootView)) {
/*  70:133 */       return 0;
/*  71:    */     }
/*  72:136 */     this.m_PageStartY = 0.0D;
/*  73:137 */     this.m_PageEndY = 0.0D;
/*  74:138 */     this.m_CurrentPage = -1;
/*  75:139 */     return 1;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void print(JTextPane pane)
/*  79:    */   {
/*  80:149 */     setDocument(pane);
/*  81:150 */     printDialog();
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void printDialog()
/*  85:    */   {
/*  86:157 */     if (this.m_PrinterJob.printDialog())
/*  87:    */     {
/*  88:158 */       this.m_PrinterJob.setPrintable(this, this.m_PageFormat);
/*  89:    */       try
/*  90:    */       {
/*  91:160 */         this.m_PrinterJob.print();
/*  92:    */       }
/*  93:    */       catch (PrinterException printerException)
/*  94:    */       {
/*  95:163 */         this.m_PageStartY = 0.0D;
/*  96:164 */         this.m_PageEndY = 0.0D;
/*  97:165 */         this.m_CurrentPage = -1;
/*  98:166 */         System.out.println("Error Printing Document");
/*  99:    */       }
/* 100:    */     }
/* 101:    */   }
/* 102:    */   
/* 103:    */   protected boolean printView(Graphics2D graphics2D, Shape allocation, View view)
/* 104:    */   {
/* 105:180 */     boolean pageExists = false;
/* 106:181 */     Rectangle clipRectangle = graphics2D.getClipBounds();
/* 107:185 */     if (view.getViewCount() > 0)
/* 108:    */     {
/* 109:186 */       for (int i = 0; i < view.getViewCount(); i++)
/* 110:    */       {
/* 111:187 */         Shape childAllocation = view.getChildAllocation(i, allocation);
/* 112:188 */         if (childAllocation != null)
/* 113:    */         {
/* 114:189 */           View childView = view.getView(i);
/* 115:190 */           if (printView(graphics2D, childAllocation, childView)) {
/* 116:191 */             pageExists = true;
/* 117:    */           }
/* 118:    */         }
/* 119:    */       }
/* 120:    */     }
/* 121:196 */     else if (allocation.getBounds().getMaxY() >= clipRectangle.getY())
/* 122:    */     {
/* 123:197 */       pageExists = true;
/* 124:198 */       if ((allocation.getBounds().getHeight() > clipRectangle.getHeight()) && (allocation.intersects(clipRectangle))) {
/* 125:200 */         view.paint(graphics2D, allocation);
/* 126:202 */       } else if (allocation.getBounds().getY() >= clipRectangle.getY()) {
/* 127:203 */         if (allocation.getBounds().getMaxY() <= clipRectangle.getMaxY()) {
/* 128:204 */           view.paint(graphics2D, allocation);
/* 129:206 */         } else if (allocation.getBounds().getY() < this.m_PageEndY) {
/* 130:207 */           this.m_PageEndY = allocation.getBounds().getY();
/* 131:    */         }
/* 132:    */       }
/* 133:    */     }
/* 134:214 */     return pageExists;
/* 135:    */   }
/* 136:    */   
/* 137:    */   public void setContentType(String type)
/* 138:    */   {
/* 139:223 */     this.m_PrintPane.setContentType(type);
/* 140:    */   }
/* 141:    */   
/* 142:    */   public Document getDocument()
/* 143:    */   {
/* 144:232 */     if (this.m_PrintPane != null) {
/* 145:233 */       return this.m_PrintPane.getDocument();
/* 146:    */     }
/* 147:235 */     return null;
/* 148:    */   }
/* 149:    */   
/* 150:    */   public void setDocument(JTextPane pane)
/* 151:    */   {
/* 152:244 */     this.m_PrintPane = new JTextPane();
/* 153:245 */     setDocument(pane.getContentType(), pane.getDocument());
/* 154:    */   }
/* 155:    */   
/* 156:    */   public void setDocument(String type, Document document)
/* 157:    */   {
/* 158:255 */     setContentType(type);
/* 159:256 */     this.m_PrintPane.setDocument(document);
/* 160:    */   }
/* 161:    */   
/* 162:    */   public void setScaleWidthToFit(boolean scaleWidth)
/* 163:    */   {
/* 164:265 */     this.m_ScaleWidthToFit = scaleWidth;
/* 165:    */   }
/* 166:    */   
/* 167:    */   public boolean getScaleWidthToFit()
/* 168:    */   {
/* 169:274 */     return this.m_ScaleWidthToFit;
/* 170:    */   }
/* 171:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.DocumentPrinting
 * JD-Core Version:    0.7.0.1
 */