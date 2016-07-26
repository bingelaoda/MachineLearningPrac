/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.awt.image.BufferedImage;
/*   4:    */ import java.io.ByteArrayInputStream;
/*   5:    */ import java.io.ByteArrayOutputStream;
/*   6:    */ import java.io.IOException;
/*   7:    */ import java.text.SimpleDateFormat;
/*   8:    */ import java.util.ArrayList;
/*   9:    */ import java.util.Arrays;
/*  10:    */ import java.util.Date;
/*  11:    */ import java.util.LinkedHashMap;
/*  12:    */ import java.util.List;
/*  13:    */ import java.util.Map;
/*  14:    */ import java.util.Map.Entry;
/*  15:    */ import javax.imageio.ImageIO;
/*  16:    */ import weka.core.WekaException;
/*  17:    */ import weka.knowledgeflow.Data;
/*  18:    */ import weka.knowledgeflow.StepManager;
/*  19:    */ 
/*  20:    */ @KFStep(name="ImageViewer", category="Visualization", toolTipText="View images", iconPath="weka/gui/knowledgeflow/icons/StripChart.gif")
/*  21:    */ public class ImageViewer
/*  22:    */   extends BaseStep
/*  23:    */   implements DataCollector
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = -4055716444227948343L;
/*  26: 56 */   protected Map<String, BufferedImage> m_images = new LinkedHashMap();
/*  27:    */   
/*  28:    */   public void stepInit() {}
/*  29:    */   
/*  30:    */   public List<String> getIncomingConnectionTypes()
/*  31:    */   {
/*  32: 75 */     return Arrays.asList(new String[] { "image" });
/*  33:    */   }
/*  34:    */   
/*  35:    */   public List<String> getOutgoingConnectionTypes()
/*  36:    */   {
/*  37: 87 */     return getStepManager().numIncomingConnectionsOfType("image") > 0 ? Arrays.asList(new String[] { "image" }) : new ArrayList();
/*  38:    */   }
/*  39:    */   
/*  40:    */   public synchronized void processIncoming(Data data)
/*  41:    */     throws WekaException
/*  42:    */   {
/*  43: 99 */     getStepManager().processing();
/*  44:100 */     String imageTitle = (String)data.getPayloadElement("aux_textTitle");
/*  45:    */     
/*  46:102 */     BufferedImage image = (BufferedImage)data.getPrimaryPayload();
/*  47:104 */     if (image == null) {
/*  48:105 */       throw new WekaException("Data does not seem to contain an image!");
/*  49:    */     }
/*  50:108 */     String date = new SimpleDateFormat("HH:mm:ss.SSS - ").format(new Date());
/*  51:109 */     if (imageTitle != null) {
/*  52:110 */       imageTitle = date + imageTitle;
/*  53:    */     } else {
/*  54:112 */       imageTitle = date + "Untitled";
/*  55:    */     }
/*  56:115 */     this.m_images.put(imageTitle, image);
/*  57:    */     
/*  58:117 */     getStepManager().logDetailed("Storing image: " + imageTitle);
/*  59:    */     
/*  60:    */ 
/*  61:120 */     getStepManager().outputData(new Data[] { data });
/*  62:121 */     getStepManager().finished();
/*  63:    */   }
/*  64:    */   
/*  65:    */   public Map<String, BufferedImage> getImages()
/*  66:    */   {
/*  67:130 */     return this.m_images;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public Map<String, String> getInteractiveViewers()
/*  71:    */   {
/*  72:142 */     Map<String, String> views = new LinkedHashMap();
/*  73:144 */     if (this.m_images.size() > 0) {
/*  74:145 */       views.put("Show images", "weka.gui.knowledgeflow.steps.ImageViewerInteractiveView");
/*  75:    */     }
/*  76:149 */     return views;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public Object retrieveData()
/*  80:    */   {
/*  81:162 */     return bufferedImageMapToSerializableByteMap(this.m_images);
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void restoreData(Object data)
/*  85:    */     throws WekaException
/*  86:    */   {
/*  87:175 */     if (!(data instanceof Map)) {
/*  88:176 */       throw new IllegalArgumentException("Argument for restoring data must be a map");
/*  89:    */     }
/*  90:    */     try
/*  91:    */     {
/*  92:181 */       this.m_images = byteArrayImageMapToBufferedImageMap((Map)data);
/*  93:    */     }
/*  94:    */     catch (IOException e)
/*  95:    */     {
/*  96:183 */       throw new WekaException(e);
/*  97:    */     }
/*  98:    */   }
/*  99:    */   
/* 100:    */   public static Map<String, BufferedImage> byteArrayImageMapToBufferedImageMap(Map<String, byte[]> dataMap)
/* 101:    */     throws IOException
/* 102:    */   {
/* 103:197 */     Map<String, BufferedImage> restored = new LinkedHashMap();
/* 104:    */     
/* 105:    */ 
/* 106:200 */     Map<String, byte[]> serializableMap = dataMap;
/* 107:201 */     for (Map.Entry<String, byte[]> e : serializableMap.entrySet())
/* 108:    */     {
/* 109:202 */       String title = (String)e.getKey();
/* 110:203 */       byte[] png = (byte[])e.getValue();
/* 111:204 */       ByteArrayInputStream bais = new ByteArrayInputStream(png);
/* 112:    */       
/* 113:206 */       BufferedImage bi = ImageIO.read(bais);
/* 114:207 */       if (bi != null) {
/* 115:208 */         restored.put(title, bi);
/* 116:    */       }
/* 117:    */     }
/* 118:211 */     return restored;
/* 119:    */   }
/* 120:    */   
/* 121:    */   public static Map<String, byte[]> bufferedImageMapToSerializableByteMap(Map<String, BufferedImage> images)
/* 122:    */   {
/* 123:223 */     Map<String, byte[]> serializableMap = new LinkedHashMap();
/* 124:224 */     for (Map.Entry<String, BufferedImage> e : images.entrySet())
/* 125:    */     {
/* 126:225 */       String title = (String)e.getKey();
/* 127:226 */       BufferedImage b = (BufferedImage)e.getValue();
/* 128:227 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 129:    */       try
/* 130:    */       {
/* 131:229 */         ImageIO.write(b, "png", baos);
/* 132:230 */         serializableMap.put(title, baos.toByteArray());
/* 133:    */       }
/* 134:    */       catch (IOException ex)
/* 135:    */       {
/* 136:232 */         ex.printStackTrace();
/* 137:    */       }
/* 138:    */     }
/* 139:236 */     return serializableMap;
/* 140:    */   }
/* 141:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.ImageViewer
 * JD-Core Version:    0.7.0.1
 */