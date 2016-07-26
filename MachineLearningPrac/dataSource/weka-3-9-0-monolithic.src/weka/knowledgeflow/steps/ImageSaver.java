/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.awt.image.BufferedImage;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.IOException;
/*   6:    */ import java.util.Arrays;
/*   7:    */ import java.util.List;
/*   8:    */ import java.util.Map;
/*   9:    */ import javax.imageio.ImageIO;
/*  10:    */ import weka.core.Defaults;
/*  11:    */ import weka.core.Environment;
/*  12:    */ import weka.core.OptionMetadata;
/*  13:    */ import weka.core.Settings;
/*  14:    */ import weka.core.Settings.SettingKey;
/*  15:    */ import weka.core.WekaException;
/*  16:    */ import weka.gui.FilePropertyMetadata;
/*  17:    */ import weka.knowledgeflow.Data;
/*  18:    */ import weka.knowledgeflow.ExecutionEnvironment;
/*  19:    */ import weka.knowledgeflow.StepManager;
/*  20:    */ 
/*  21:    */ @KFStep(name="ImageSaver", category="DataSinks", toolTipText="Save static images to a file", iconPath="weka/gui/knowledgeflow/icons/SerializedModelSaver.gif")
/*  22:    */ public class ImageSaver
/*  23:    */   extends BaseStep
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = -8766164679635957891L;
/*  26:    */   protected File m_file;
/*  27:    */   protected String m_defaultFile;
/*  28:    */   protected ImageFormat m_format;
/*  29:    */   protected ImageFormat m_defaultFormat;
/*  30:    */   protected int m_imageCounter;
/*  31:    */   
/*  32:    */   protected static enum ImageFormat
/*  33:    */   {
/*  34: 55 */     DEFAULT,  PNG,  GIF;
/*  35:    */     
/*  36:    */     private ImageFormat() {}
/*  37:    */   }
/*  38:    */   
/*  39:    */   public ImageSaver()
/*  40:    */   {
/*  41: 59 */     this.m_file = new File("");
/*  42:    */     
/*  43:    */ 
/*  44: 62 */     this.m_defaultFile = "";
/*  45:    */     
/*  46:    */ 
/*  47:    */ 
/*  48:    */ 
/*  49:    */ 
/*  50: 68 */     this.m_format = ImageFormat.DEFAULT;
/*  51:    */   }
/*  52:    */   
/*  53:    */   @OptionMetadata(displayName="File to save to", description="<html>The file to save an image to<br>The variable 'image_count' may be used as<br>part of the filename/path in order to differentiate<br>multiple images.</html>", displayOrder=1)
/*  54:    */   @FilePropertyMetadata(fileChooserDialogType=0, directoriesOnly=false)
/*  55:    */   public void setFile(File f)
/*  56:    */   {
/*  57: 97 */     this.m_file = f;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public File getFile()
/*  61:    */   {
/*  62:106 */     return this.m_file;
/*  63:    */   }
/*  64:    */   
/*  65:    */   @OptionMetadata(displayName="Format to save image as", description="Format to save to", displayOrder=2)
/*  66:    */   public void setFormat(ImageFormat format)
/*  67:    */   {
/*  68:117 */     this.m_format = format;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public ImageFormat getFormat()
/*  72:    */   {
/*  73:126 */     return this.m_format;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void stepInit()
/*  77:    */     throws WekaException
/*  78:    */   {
/*  79:136 */     this.m_imageCounter = 1;
/*  80:137 */     this.m_defaultFile = getFile().toString();
/*  81:138 */     if ((this.m_defaultFile == null) || (this.m_defaultFile.length() == 0))
/*  82:    */     {
/*  83:139 */       File defaultF = (File)getStepManager().getSettings().getSetting("weka.knowledgeflow.steps.imagesaver", ImageSaverDefaults.DEFAULT_FILE_KEY, ImageSaverDefaults.DEFAULT_FILE, getStepManager().getExecutionEnvironment().getEnvironmentVariables());
/*  84:    */       
/*  85:    */ 
/*  86:    */ 
/*  87:143 */       this.m_defaultFile = defaultF.toString();
/*  88:    */     }
/*  89:146 */     if (this.m_format == ImageFormat.DEFAULT)
/*  90:    */     {
/*  91:147 */       this.m_defaultFormat = ((ImageFormat)getStepManager().getSettings().getSetting("weka.knowledgeflow.steps.imagesaver", ImageSaverDefaults.DEFAULT_FORMAT_KEY, ImageSaverDefaults.DEFAULT_FORMAT, getStepManager().getExecutionEnvironment().getEnvironmentVariables()));
/*  92:153 */       if (this.m_defaultFormat == ImageFormat.DEFAULT) {
/*  93:154 */         throw new WekaException("The default format to use must be something other than 'DEFAULT'");
/*  94:    */       }
/*  95:    */     }
/*  96:    */   }
/*  97:    */   
/*  98:    */   public List<String> getIncomingConnectionTypes()
/*  99:    */   {
/* 100:171 */     return Arrays.asList(new String[] { "image" });
/* 101:    */   }
/* 102:    */   
/* 103:    */   public List<String> getOutgoingConnectionTypes()
/* 104:    */   {
/* 105:185 */     return null;
/* 106:    */   }
/* 107:    */   
/* 108:    */   public synchronized void processIncoming(Data data)
/* 109:    */     throws WekaException
/* 110:    */   {
/* 111:196 */     getStepManager().processing();
/* 112:197 */     ImageFormat formatToUse = this.m_format == ImageFormat.DEFAULT ? this.m_defaultFormat : this.m_format;
/* 113:    */     
/* 114:199 */     BufferedImage content = (BufferedImage)data.getPrimaryPayload();
/* 115:200 */     getStepManager().getExecutionEnvironment().getEnvironmentVariables().addVariable("image_count", "" + this.m_imageCounter++);
/* 116:    */     
/* 117:202 */     String fileName = getFile().toString();
/* 118:203 */     if ((fileName == null) || (fileName.length() == 0)) {
/* 119:204 */       fileName = this.m_defaultFile;
/* 120:    */     }
/* 121:206 */     fileName = environmentSubstitute(fileName);
/* 122:207 */     if (!new File(fileName).isDirectory())
/* 123:    */     {
/* 124:208 */       if (!fileName.toLowerCase().endsWith(formatToUse.toString().toLowerCase())) {
/* 125:210 */         fileName = fileName + "." + formatToUse.toString().toLowerCase();
/* 126:    */       }
/* 127:212 */       File file = new File(fileName);
/* 128:213 */       getStepManager().logDetailed("Writing image to " + fileName);
/* 129:    */       try
/* 130:    */       {
/* 131:215 */         ImageIO.write(content, formatToUse.toString().toLowerCase(), file);
/* 132:    */       }
/* 133:    */       catch (IOException ex)
/* 134:    */       {
/* 135:217 */         throw new WekaException(ex);
/* 136:    */       }
/* 137:    */     }
/* 138:    */     else
/* 139:    */     {
/* 140:220 */       getStepManager().logWarning("Unable to write image because '" + fileName + "' is a directory!");
/* 141:    */     }
/* 142:223 */     if (!isStopRequested()) {
/* 143:224 */       getStepManager().finished();
/* 144:    */     } else {
/* 145:226 */       getStepManager().interrupted();
/* 146:    */     }
/* 147:    */   }
/* 148:    */   
/* 149:    */   public Defaults getDefaultSettings()
/* 150:    */   {
/* 151:237 */     return new ImageSaverDefaults();
/* 152:    */   }
/* 153:    */   
/* 154:    */   public static final class ImageSaverDefaults
/* 155:    */     extends Defaults
/* 156:    */   {
/* 157:    */     public static final String ID = "weka.knowledgeflow.steps.imagesaver";
/* 158:244 */     public static final Settings.SettingKey DEFAULT_FILE_KEY = new Settings.SettingKey("weka.knowledgeflow.steps.imagesaver.defaultFile", "Default file to save to", "Save to this file if the user has not explicitly set one in the step");
/* 159:248 */     public static final File DEFAULT_FILE = new File("${user.dir}/image");
/* 160:250 */     public static final Settings.SettingKey DEFAULT_FORMAT_KEY = new Settings.SettingKey("weka.knowledgeflow.steps.imagesaver.defaultFormat", "Default image format to write", "Default image format to write in the case that the user has explicitly set 'DEFAULT' in the step's options");
/* 161:254 */     public static final ImageSaver.ImageFormat DEFAULT_FORMAT = ImageSaver.ImageFormat.PNG;
/* 162:    */     private static final long serialVersionUID = -2739579935119189195L;
/* 163:    */     
/* 164:    */     public ImageSaverDefaults()
/* 165:    */     {
/* 166:259 */       super();
/* 167:260 */       this.m_defaults.put(DEFAULT_FILE_KEY, DEFAULT_FILE);
/* 168:261 */       this.m_defaults.put(DEFAULT_FORMAT_KEY, DEFAULT_FORMAT);
/* 169:    */     }
/* 170:    */   }
/* 171:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.ImageSaver
 * JD-Core Version:    0.7.0.1
 */