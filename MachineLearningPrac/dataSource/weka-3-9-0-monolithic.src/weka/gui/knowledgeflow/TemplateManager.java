/*   1:    */ package weka.gui.knowledgeflow;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.List;
/*   7:    */ import java.util.Map;
/*   8:    */ import java.util.Properties;
/*   9:    */ import javax.swing.JOptionPane;
/*  10:    */ import weka.core.PluginManager;
/*  11:    */ import weka.core.Utils;
/*  12:    */ import weka.core.WekaException;
/*  13:    */ import weka.knowledgeflow.Flow;
/*  14:    */ import weka.knowledgeflow.JSONFlowLoader;
/*  15:    */ 
/*  16:    */ public class TemplateManager
/*  17:    */ {
/*  18:    */   static
/*  19:    */   {
/*  20:    */     try
/*  21:    */     {
/*  22: 49 */       Properties templateProps = Utils.readProperties("weka/gui/knowledgeflow/templates/templates.props");
/*  23:    */       
/*  24: 51 */       PluginManager.addFromProperties(templateProps, true);
/*  25:    */     }
/*  26:    */     catch (Exception ex)
/*  27:    */     {
/*  28: 53 */       JOptionPane.showMessageDialog(null, ex.getMessage(), "KnowledgeFlow", 0);
/*  29:    */       
/*  30: 55 */       ex.printStackTrace();
/*  31:    */     }
/*  32:    */   }
/*  33:    */   
/*  34:    */   public int numTemplates()
/*  35:    */   {
/*  36: 65 */     return numBuiltinTemplates() + numPluginTemplates();
/*  37:    */   }
/*  38:    */   
/*  39:    */   public int numBuiltinTemplates()
/*  40:    */   {
/*  41: 74 */     return PluginManager.numResourcesForWithGroupID("weka.knowledgeflow.templates");
/*  42:    */   }
/*  43:    */   
/*  44:    */   public int numPluginTemplates()
/*  45:    */   {
/*  46: 84 */     return PluginManager.numResourcesForWithGroupID("weka.knowledgeflow.plugin.templates");
/*  47:    */   }
/*  48:    */   
/*  49:    */   public List<String> getBuiltinTemplateDescriptions()
/*  50:    */   {
/*  51: 94 */     List<String> result = new ArrayList();
/*  52:    */     
/*  53: 96 */     Map<String, String> builtin = PluginManager.getResourcesWithGroupID("weka.knowledgeflow.templates");
/*  54: 98 */     if (builtin != null) {
/*  55: 99 */       for (String desc : builtin.keySet()) {
/*  56:100 */         result.add(desc);
/*  57:    */       }
/*  58:    */     }
/*  59:104 */     return result;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public List<String> getPluginTemplateDescriptions()
/*  63:    */   {
/*  64:113 */     List<String> result = new ArrayList();
/*  65:    */     
/*  66:115 */     Map<String, String> plugin = PluginManager.getResourcesWithGroupID("weka.knowledgeflow.plugin.templates");
/*  67:117 */     if (plugin != null) {
/*  68:118 */       for (String desc : plugin.keySet()) {
/*  69:119 */         result.add(desc);
/*  70:    */       }
/*  71:    */     }
/*  72:123 */     return result;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public Flow getTemplateFlow(String flowDescription)
/*  76:    */     throws WekaException
/*  77:    */   {
/*  78:134 */     Flow result = null;
/*  79:    */     try
/*  80:    */     {
/*  81:137 */       result = getBuiltinTemplateFlow(flowDescription);
/*  82:    */     }
/*  83:    */     catch (IOException ex) {}
/*  84:142 */     if (result == null) {
/*  85:    */       try
/*  86:    */       {
/*  87:145 */         result = getPluginTemplateFlow(flowDescription);
/*  88:    */       }
/*  89:    */       catch (IOException ex)
/*  90:    */       {
/*  91:147 */         throw new WekaException("The template flow '" + flowDescription + "' " + "does not seem to exist as a builtin or plugin template");
/*  92:    */       }
/*  93:    */     }
/*  94:152 */     return result;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public Flow getBuiltinTemplateFlow(String flowDescription)
/*  98:    */     throws IOException, WekaException
/*  99:    */   {
/* 100:165 */     InputStream flowStream = PluginManager.getPluginResourceAsStream("weka.knowledgeflow.templates", flowDescription);
/* 101:    */     
/* 102:    */ 
/* 103:168 */     JSONFlowLoader loader = new JSONFlowLoader();
/* 104:169 */     return loader.readFlow(flowStream);
/* 105:    */   }
/* 106:    */   
/* 107:    */   public Flow getPluginTemplateFlow(String flowDescription)
/* 108:    */     throws IOException, WekaException
/* 109:    */   {
/* 110:182 */     InputStream flowStream = PluginManager.getPluginResourceAsStream("weka.knowledgeflow.plugin.templates", flowDescription);
/* 111:    */     
/* 112:    */ 
/* 113:185 */     JSONFlowLoader loader = new JSONFlowLoader();
/* 114:186 */     return loader.readFlow(flowStream);
/* 115:    */   }
/* 116:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.TemplateManager
 * JD-Core Version:    0.7.0.1
 */