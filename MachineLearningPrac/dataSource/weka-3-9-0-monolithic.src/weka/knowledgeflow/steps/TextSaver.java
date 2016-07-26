/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.io.BufferedWriter;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.FileOutputStream;
/*   6:    */ import java.io.IOException;
/*   7:    */ import java.io.OutputStreamWriter;
/*   8:    */ import java.io.Writer;
/*   9:    */ import java.util.Arrays;
/*  10:    */ import java.util.List;
/*  11:    */ import java.util.Map;
/*  12:    */ import weka.core.Defaults;
/*  13:    */ import weka.core.OptionMetadata;
/*  14:    */ import weka.core.Settings;
/*  15:    */ import weka.core.Settings.SettingKey;
/*  16:    */ import weka.core.WekaException;
/*  17:    */ import weka.gui.FilePropertyMetadata;
/*  18:    */ import weka.knowledgeflow.Data;
/*  19:    */ import weka.knowledgeflow.ExecutionEnvironment;
/*  20:    */ import weka.knowledgeflow.StepManager;
/*  21:    */ 
/*  22:    */ @KFStep(name="TextSaver", category="DataSinks", toolTipText="Save text output to a file", iconPath="weka/gui/knowledgeflow/icons/DefaultText.gif")
/*  23:    */ public class TextSaver
/*  24:    */   extends BaseStep
/*  25:    */ {
/*  26:    */   private static final long serialVersionUID = -1434752243260858338L;
/*  27:    */   protected File m_file;
/*  28:    */   protected boolean m_append;
/*  29:    */   protected boolean m_writeTitleString;
/*  30:    */   protected String m_defaultFile;
/*  31:    */   
/*  32:    */   public TextSaver()
/*  33:    */   {
/*  34: 56 */     this.m_file = new File("");
/*  35:    */     
/*  36:    */ 
/*  37: 59 */     this.m_append = true;
/*  38:    */     
/*  39:    */ 
/*  40:    */ 
/*  41:    */ 
/*  42:    */ 
/*  43: 65 */     this.m_defaultFile = "";
/*  44:    */   }
/*  45:    */   
/*  46:    */   @OptionMetadata(displayName="File to save to", description="The file to save textual results to", displayOrder=1)
/*  47:    */   @FilePropertyMetadata(fileChooserDialogType=0, directoriesOnly=false)
/*  48:    */   public void setFile(File f)
/*  49:    */   {
/*  50: 77 */     this.m_file = f;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public File getFile()
/*  54:    */   {
/*  55: 86 */     return this.m_file;
/*  56:    */   }
/*  57:    */   
/*  58:    */   @OptionMetadata(displayName="Append to file", description="Append to file, rather than re-create for each incoming texual result", displayOrder=2)
/*  59:    */   public void setAppend(boolean append)
/*  60:    */   {
/*  61: 98 */     this.m_append = append;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public boolean getAppend()
/*  65:    */   {
/*  66:107 */     return this.m_append;
/*  67:    */   }
/*  68:    */   
/*  69:    */   @OptionMetadata(displayName="Write title string", description="Whether to output the title string associated with each textual result", displayOrder=3)
/*  70:    */   public void setWriteTitleString(boolean w)
/*  71:    */   {
/*  72:119 */     this.m_writeTitleString = w;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public boolean getWriteTitleString()
/*  76:    */   {
/*  77:128 */     return this.m_writeTitleString;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public void stepInit()
/*  81:    */     throws WekaException
/*  82:    */   {
/*  83:138 */     this.m_defaultFile = getFile().toString();
/*  84:139 */     if ((this.m_defaultFile == null) || (this.m_defaultFile.length() == 0))
/*  85:    */     {
/*  86:140 */       File defaultF = (File)getStepManager().getSettings().getSetting("weka.knowledgeflow.steps.textsaver", TextSaverDefaults.DEFAULT_FILE_KEY, TextSaverDefaults.DEFAULT_FILE, getStepManager().getExecutionEnvironment().getEnvironmentVariables());
/*  87:    */       
/*  88:    */ 
/*  89:    */ 
/*  90:144 */       this.m_defaultFile = defaultF.toString();
/*  91:    */     }
/*  92:    */   }
/*  93:    */   
/*  94:    */   public List<String> getIncomingConnectionTypes()
/*  95:    */   {
/*  96:159 */     return Arrays.asList(new String[] { "text" });
/*  97:    */   }
/*  98:    */   
/*  99:    */   public List<String> getOutgoingConnectionTypes()
/* 100:    */   {
/* 101:173 */     return null;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public synchronized void processIncoming(Data data)
/* 105:    */     throws WekaException
/* 106:    */   {
/* 107:184 */     getStepManager().processing();
/* 108:185 */     String content = (String)data.getPrimaryPayload();
/* 109:186 */     String title = (String)data.getPayloadElement("aux_textTitle");
/* 110:187 */     String fileName = getFile().toString();
/* 111:188 */     if ((fileName == null) || (fileName.length() == 0)) {
/* 112:189 */       fileName = this.m_defaultFile;
/* 113:    */     }
/* 114:191 */     fileName = environmentSubstitute(fileName);
/* 115:193 */     if ((title != null) && (title.length() > 0)) {
/* 116:194 */       title = environmentSubstitute(title);
/* 117:    */     } else {
/* 118:196 */       title = null;
/* 119:    */     }
/* 120:199 */     if (!new File(fileName).isDirectory())
/* 121:    */     {
/* 122:200 */       if (!fileName.toLowerCase().endsWith(".txt")) {
/* 123:201 */         fileName = fileName + ".txt";
/* 124:    */       }
/* 125:203 */       File file = new File(fileName);
/* 126:    */       
/* 127:205 */       getStepManager().logDetailed("Writing " + (title != null ? title : new StringBuilder().append("file to ").append(file.toString()).toString()));
/* 128:    */       
/* 129:207 */       Writer writer = null;
/* 130:    */       try
/* 131:    */       {
/* 132:209 */         writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, this.m_append), "utf-8"));
/* 133:212 */         if ((title != null) && (getWriteTitleString())) {
/* 134:213 */           writer.write(title + "\n\n");
/* 135:    */         }
/* 136:215 */         writer.write(content);
/* 137:    */       }
/* 138:    */       catch (IOException e)
/* 139:    */       {
/* 140:217 */         throw new WekaException(e);
/* 141:    */       }
/* 142:    */       finally
/* 143:    */       {
/* 144:219 */         if (writer != null) {
/* 145:    */           try
/* 146:    */           {
/* 147:221 */             writer.flush();
/* 148:222 */             writer.close();
/* 149:    */           }
/* 150:    */           catch (IOException e)
/* 151:    */           {
/* 152:224 */             throw new WekaException(e);
/* 153:    */           }
/* 154:    */         }
/* 155:    */       }
/* 156:    */     }
/* 157:    */     else
/* 158:    */     {
/* 159:229 */       getStepManager().logWarning("Supplied file is a directory! Unable to write.");
/* 160:    */     }
/* 161:233 */     if (!isStopRequested()) {
/* 162:234 */       getStepManager().finished();
/* 163:    */     } else {
/* 164:236 */       getStepManager().interrupted();
/* 165:    */     }
/* 166:    */   }
/* 167:    */   
/* 168:    */   public Defaults getDefaultSettings()
/* 169:    */   {
/* 170:248 */     return new TextSaverDefaults();
/* 171:    */   }
/* 172:    */   
/* 173:    */   public static final class TextSaverDefaults
/* 174:    */     extends Defaults
/* 175:    */   {
/* 176:    */     public static final String ID = "weka.knowledgeflow.steps.textsaver";
/* 177:258 */     public static final Settings.SettingKey DEFAULT_FILE_KEY = new Settings.SettingKey("weka.knowledgeflow.steps.textsaver.defaultFile", "Default file to save to", "Save to this file if the user has not explicitly set one in the step");
/* 178:262 */     public static final File DEFAULT_FILE = new File("${user.dir}/textout.txt");
/* 179:    */     private static final long serialVersionUID = -2739579935119189195L;
/* 180:    */     
/* 181:    */     public TextSaverDefaults()
/* 182:    */     {
/* 183:270 */       super();
/* 184:271 */       this.m_defaults.put(DEFAULT_FILE_KEY, DEFAULT_FILE);
/* 185:    */     }
/* 186:    */   }
/* 187:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.TextSaver
 * JD-Core Version:    0.7.0.1
 */