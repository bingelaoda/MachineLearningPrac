/*  1:   */ package weka.gui.beans;
/*  2:   */ 
/*  3:   */ import java.awt.image.BufferedImage;
/*  4:   */ import java.util.EventObject;
/*  5:   */ 
/*  6:   */ public class ImageEvent
/*  7:   */   extends EventObject
/*  8:   */ {
/*  9:   */   private static final long serialVersionUID = -8126533743311557969L;
/* 10:   */   protected BufferedImage m_image;
/* 11:42 */   protected String m_imageName = "";
/* 12:   */   
/* 13:   */   public ImageEvent(Object source, BufferedImage image)
/* 14:   */   {
/* 15:51 */     this(source, image, "");
/* 16:   */   }
/* 17:   */   
/* 18:   */   public ImageEvent(Object source, BufferedImage image, String imageName)
/* 19:   */   {
/* 20:62 */     super(source);
/* 21:   */     
/* 22:64 */     this.m_image = image;
/* 23:65 */     this.m_imageName = imageName;
/* 24:   */   }
/* 25:   */   
/* 26:   */   public BufferedImage getImage()
/* 27:   */   {
/* 28:74 */     return this.m_image;
/* 29:   */   }
/* 30:   */   
/* 31:   */   public String getImageName()
/* 32:   */   {
/* 33:83 */     return this.m_imageName;
/* 34:   */   }
/* 35:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.ImageEvent
 * JD-Core Version:    0.7.0.1
 */