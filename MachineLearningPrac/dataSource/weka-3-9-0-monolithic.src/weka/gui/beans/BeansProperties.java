/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.FileInputStream;
/*   5:    */ import java.io.Serializable;
/*   6:    */ import java.util.ArrayList;
/*   7:    */ import java.util.Enumeration;
/*   8:    */ import java.util.List;
/*   9:    */ import java.util.Properties;
/*  10:    */ import java.util.SortedSet;
/*  11:    */ import java.util.StringTokenizer;
/*  12:    */ import java.util.TreeSet;
/*  13:    */ import javax.swing.JOptionPane;
/*  14:    */ import weka.core.PluginManager;
/*  15:    */ import weka.core.Utils;
/*  16:    */ import weka.core.WekaPackageManager;
/*  17:    */ import weka.core.logging.Logger;
/*  18:    */ import weka.core.logging.Logger.Level;
/*  19:    */ import weka.core.metastore.MetaStore;
/*  20:    */ import weka.core.metastore.XMLFileBasedMetaStore;
/*  21:    */ 
/*  22:    */ public class BeansProperties
/*  23:    */   implements Serializable
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = 7183413615090695785L;
/*  26:    */   protected static final String PROPERTY_FILE = "weka/gui/beans/Beans.props";
/*  27:    */   protected static final String TEMPLATE_PROPERTY_FILE = "weka/gui/beans/templates/templates.props";
/*  28:    */   protected static List<String> TEMPLATE_PATHS;
/*  29:    */   protected static List<String> TEMPLATE_DESCRIPTIONS;
/*  30:    */   protected static Properties BEAN_PROPERTIES;
/*  31: 72 */   protected static ArrayList<Properties> BEAN_PLUGINS_PROPERTIES = new ArrayList();
/*  32: 79 */   protected static String VISIBLE_PERSPECTIVES_PROPERTIES_FILE = "weka/gui/beans/VisiblePerspectives.props";
/*  33:    */   protected static SortedSet<String> VISIBLE_PERSPECTIVES;
/*  34: 85 */   private static boolean s_pluginManagerIntialized = false;
/*  35: 88 */   protected static MetaStore s_kfMetaStore = new XMLFileBasedMetaStore();
/*  36:    */   
/*  37:    */   public static MetaStore getMetaStore()
/*  38:    */   {
/*  39: 96 */     return s_kfMetaStore;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public static void addToPluginBeanProps(File beanPropsFile)
/*  43:    */     throws Exception
/*  44:    */   {
/*  45:100 */     Properties tempP = new Properties();
/*  46:    */     
/*  47:102 */     tempP.load(new FileInputStream(beanPropsFile));
/*  48:103 */     if (!BEAN_PLUGINS_PROPERTIES.contains(tempP)) {
/*  49:104 */       BEAN_PLUGINS_PROPERTIES.add(tempP);
/*  50:    */     }
/*  51:    */   }
/*  52:    */   
/*  53:    */   public static void removeFromPluginBeanProps(File beanPropsFile)
/*  54:    */     throws Exception
/*  55:    */   {
/*  56:110 */     Properties tempP = new Properties();
/*  57:    */     
/*  58:112 */     tempP.load(new FileInputStream(beanPropsFile));
/*  59:113 */     if (BEAN_PLUGINS_PROPERTIES.contains(tempP)) {
/*  60:114 */       BEAN_PLUGINS_PROPERTIES.remove(tempP);
/*  61:    */     }
/*  62:    */   }
/*  63:    */   
/*  64:    */   public static synchronized void loadProperties()
/*  65:    */   {
/*  66:122 */     if (BEAN_PROPERTIES == null)
/*  67:    */     {
/*  68:123 */       WekaPackageManager.loadPackages(false);
/*  69:124 */       Logger.log(Logger.Level.INFO, "[KnowledgeFlow] Loading properties and plugins...");
/*  70:    */       try
/*  71:    */       {
/*  72:130 */         BEAN_PROPERTIES = Utils.readProperties("weka/gui/beans/Beans.props");
/*  73:131 */         Enumeration<?> keys = BEAN_PROPERTIES.propertyNames();
/*  74:132 */         if (!keys.hasMoreElements()) {
/*  75:133 */           throw new Exception("Could not read a configuration file for the bean\npanel. An example file is included with the Weka distribution.\nThis file should be named \"weka/gui/beans/Beans.props\" and\nshould be placed either in your user home (which is set\nto \"" + System.getProperties().getProperty("user.home") + "\")\n" + "or the directory that java was started from\n");
/*  76:    */         }
/*  77:    */       }
/*  78:    */       catch (Exception ex)
/*  79:    */       {
/*  80:142 */         JOptionPane.showMessageDialog(null, ex.getMessage(), "KnowledgeFlow", 0);
/*  81:    */       }
/*  82:146 */       if (VISIBLE_PERSPECTIVES == null)
/*  83:    */       {
/*  84:148 */         Properties pp = new Properties();
/*  85:149 */         pp.setProperty("weka.gui.beans.KnowledgeFlow.Perspectives", "weka.gui.beans.ScatterPlotMatrix,weka.gui.beans.AttributeSummarizer,weka.gui.beans.SQLViewerPerspective");
/*  86:    */         
/*  87:    */ 
/*  88:    */ 
/*  89:153 */         BEAN_PLUGINS_PROPERTIES.add(pp);
/*  90:    */         
/*  91:155 */         VISIBLE_PERSPECTIVES = new TreeSet();
/*  92:    */         try
/*  93:    */         {
/*  94:158 */           Properties visible = Utils.readProperties(VISIBLE_PERSPECTIVES_PROPERTIES_FILE);
/*  95:    */           
/*  96:160 */           Enumeration<?> keys = visible.propertyNames();
/*  97:161 */           if (keys.hasMoreElements())
/*  98:    */           {
/*  99:162 */             String listedPerspectives = visible.getProperty("weka.gui.beans.KnowledgeFlow.SelectedPerspectives");
/* 100:164 */             if ((listedPerspectives != null) && (listedPerspectives.length() > 0))
/* 101:    */             {
/* 102:167 */               StringTokenizer st = new StringTokenizer(listedPerspectives, ", ");
/* 103:170 */               while (st.hasMoreTokens())
/* 104:    */               {
/* 105:171 */                 String perspectiveName = st.nextToken().trim();
/* 106:172 */                 Logger.log(Logger.Level.INFO, "Adding perspective " + perspectiveName + " to visible list");
/* 107:    */                 
/* 108:    */ 
/* 109:    */ 
/* 110:176 */                 VISIBLE_PERSPECTIVES.add(perspectiveName);
/* 111:    */               }
/* 112:    */             }
/* 113:    */           }
/* 114:    */         }
/* 115:    */         catch (Exception ex)
/* 116:    */         {
/* 117:181 */           JOptionPane.showMessageDialog(null, ex.getMessage(), "KnowledgeFlow", 0);
/* 118:    */         }
/* 119:    */       }
/* 120:    */     }
/* 121:189 */     if (TEMPLATE_PATHS == null)
/* 122:    */     {
/* 123:190 */       TEMPLATE_PATHS = new ArrayList();
/* 124:191 */       TEMPLATE_DESCRIPTIONS = new ArrayList();
/* 125:    */       try
/* 126:    */       {
/* 127:194 */         Properties templateProps = Utils.readProperties("weka/gui/beans/templates/templates.props");
/* 128:195 */         String paths = templateProps.getProperty("weka.gui.beans.KnowledgeFlow.templates");
/* 129:    */         
/* 130:197 */         String descriptions = templateProps.getProperty("weka.gui.beans.KnowledgeFlow.templates.desc");
/* 131:199 */         if ((paths == null) || (paths.length() == 0))
/* 132:    */         {
/* 133:200 */           Logger.log(Logger.Level.WARNING, "[KnowledgeFlow] WARNING: no templates found in classpath");
/* 134:    */         }
/* 135:    */         else
/* 136:    */         {
/* 137:203 */           String[] templates = paths.split(",");
/* 138:204 */           String[] desc = descriptions.split(",");
/* 139:205 */           if (templates.length != desc.length) {
/* 140:206 */             throw new Exception("Number of template descriptions does not match number of templates.");
/* 141:    */           }
/* 142:209 */           for (String template : templates) {
/* 143:210 */             TEMPLATE_PATHS.add(template.trim());
/* 144:    */           }
/* 145:212 */           for (String d : desc) {
/* 146:213 */             TEMPLATE_DESCRIPTIONS.add(d.trim());
/* 147:    */           }
/* 148:    */         }
/* 149:    */       }
/* 150:    */       catch (Exception ex)
/* 151:    */       {
/* 152:217 */         JOptionPane.showMessageDialog(null, ex.getMessage(), "KnowledgeFlow", 0);
/* 153:    */       }
/* 154:    */     }
/* 155:222 */     if ((!s_pluginManagerIntialized) && (BEAN_PLUGINS_PROPERTIES != null) && (BEAN_PLUGINS_PROPERTIES.size() > 0))
/* 156:    */     {
/* 157:224 */       for (int i = 0; i < BEAN_PLUGINS_PROPERTIES.size(); i++)
/* 158:    */       {
/* 159:225 */         Properties tempP = (Properties)BEAN_PLUGINS_PROPERTIES.get(i);
/* 160:    */         
/* 161:227 */         String offscreenRenderers = tempP.getProperty("weka.gui.beans.OffscreenChartRenderer");
/* 162:229 */         if ((offscreenRenderers != null) && (offscreenRenderers.length() > 0))
/* 163:    */         {
/* 164:230 */           String[] parts = offscreenRenderers.split(",");
/* 165:231 */           for (String renderer : parts)
/* 166:    */           {
/* 167:232 */             renderer = renderer.trim();
/* 168:    */             try
/* 169:    */             {
/* 170:235 */               Object p = Class.forName(renderer).newInstance();
/* 171:236 */               if ((p instanceof OffscreenChartRenderer))
/* 172:    */               {
/* 173:237 */                 String name = ((OffscreenChartRenderer)p).rendererName();
/* 174:238 */                 PluginManager.addPlugin("weka.gui.beans.OffscreenChartRenderer", name, renderer);
/* 175:    */                 
/* 176:240 */                 Logger.log(Logger.Level.INFO, "[KnowledgeFlow] registering chart rendering plugin: " + renderer);
/* 177:    */               }
/* 178:    */             }
/* 179:    */             catch (Exception ex)
/* 180:    */             {
/* 181:247 */               Logger.log(Logger.Level.WARNING, "[KnowledgeFlow] WARNING: unable to instantiate chart renderer \"" + renderer + "\"");
/* 182:    */               
/* 183:    */ 
/* 184:    */ 
/* 185:    */ 
/* 186:252 */               ex.printStackTrace();
/* 187:    */             }
/* 188:    */           }
/* 189:    */         }
/* 190:259 */         String templatePaths = tempP.getProperty("weka.gui.beans.KnowledgeFlow.templates");
/* 191:    */         
/* 192:261 */         String templateDesc = tempP.getProperty("weka.gui.beans.KnowledgeFlow.templates.desc");
/* 193:263 */         if ((templatePaths != null) && (templatePaths.length() > 0) && (templateDesc != null) && (templateDesc.length() > 0))
/* 194:    */         {
/* 195:265 */           String[] templates = templatePaths.split(",");
/* 196:266 */           String[] desc = templateDesc.split(",");
/* 197:268 */           if (templates.length == desc.length) {
/* 198:270 */             for (int kk = 0; kk < templates.length; kk++)
/* 199:    */             {
/* 200:271 */               String pth = templates[kk];
/* 201:272 */               String d = desc[kk];
/* 202:273 */               if (!TEMPLATE_PATHS.contains(pth))
/* 203:    */               {
/* 204:274 */                 TEMPLATE_PATHS.add(pth.trim());
/* 205:275 */                 TEMPLATE_DESCRIPTIONS.add(d.trim());
/* 206:    */               }
/* 207:    */             }
/* 208:    */           }
/* 209:    */         }
/* 210:    */       }
/* 211:281 */       s_pluginManagerIntialized = true;
/* 212:    */     }
/* 213:    */   }
/* 214:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.BeansProperties
 * JD-Core Version:    0.7.0.1
 */