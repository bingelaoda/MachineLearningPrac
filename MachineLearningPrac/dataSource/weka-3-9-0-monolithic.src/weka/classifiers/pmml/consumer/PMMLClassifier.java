/*   1:    */ package weka.classifiers.pmml.consumer;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import org.w3c.dom.Document;
/*   5:    */ import org.w3c.dom.Element;
/*   6:    */ import org.w3c.dom.Node;
/*   7:    */ import org.w3c.dom.NodeList;
/*   8:    */ import weka.classifiers.AbstractClassifier;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.pmml.MappingInfo;
/*  11:    */ import weka.core.pmml.MiningSchema;
/*  12:    */ import weka.core.pmml.PMMLModel;
/*  13:    */ import weka.gui.Logger;
/*  14:    */ 
/*  15:    */ public abstract class PMMLClassifier
/*  16:    */   extends AbstractClassifier
/*  17:    */   implements Serializable, PMMLModel
/*  18:    */ {
/*  19:    */   private static final long serialVersionUID = -5371600590320702971L;
/*  20: 51 */   protected String m_pmmlVersion = "?";
/*  21: 54 */   protected String m_creatorApplication = "?";
/*  22: 57 */   protected Logger m_log = null;
/*  23:    */   protected Instances m_dataDictionary;
/*  24:    */   protected MiningSchema m_miningSchema;
/*  25:    */   protected transient MappingInfo m_fieldsMap;
/*  26: 71 */   protected transient boolean m_initialized = false;
/*  27:    */   
/*  28:    */   PMMLClassifier(Instances dataDictionary, MiningSchema miningSchema)
/*  29:    */   {
/*  30: 81 */     this.m_dataDictionary = dataDictionary;
/*  31: 82 */     this.m_miningSchema = miningSchema;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public void setPMMLVersion(Document doc)
/*  35:    */   {
/*  36: 91 */     NodeList tempL = doc.getElementsByTagName("PMML");
/*  37: 92 */     Node pmml = tempL.item(0);
/*  38: 93 */     if (pmml.getNodeType() == 1)
/*  39:    */     {
/*  40: 94 */       String version = ((Element)pmml).getAttribute("version");
/*  41: 95 */       if (version.length() > 0) {
/*  42: 96 */         this.m_pmmlVersion = version;
/*  43:    */       }
/*  44:    */     }
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void setCreatorApplication(Document doc)
/*  48:    */   {
/*  49:108 */     NodeList tempL = doc.getElementsByTagName("Header");
/*  50:109 */     Node header = tempL.item(0);
/*  51:110 */     if (header.getNodeType() == 1)
/*  52:    */     {
/*  53:111 */       NodeList appL = ((Element)header).getElementsByTagName("Application");
/*  54:112 */       if (appL.getLength() > 0)
/*  55:    */       {
/*  56:113 */         Node app = appL.item(0);
/*  57:114 */         if (app.getNodeType() == 1)
/*  58:    */         {
/*  59:115 */           String appName = ((Element)app).getAttribute("name");
/*  60:116 */           if ((appName != null) && (appName.length() > 0))
/*  61:    */           {
/*  62:117 */             String version = ((Element)app).getAttribute("version");
/*  63:118 */             if ((version != null) && (version.length() > 0)) {
/*  64:119 */               appName = appName + " v. " + version;
/*  65:    */             }
/*  66:121 */             this.m_creatorApplication = appName;
/*  67:    */           }
/*  68:    */         }
/*  69:    */       }
/*  70:    */     }
/*  71:    */   }
/*  72:    */   
/*  73:    */   public Instances getDataDictionary()
/*  74:    */   {
/*  75:134 */     return this.m_dataDictionary;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public MiningSchema getMiningSchema()
/*  79:    */   {
/*  80:143 */     return this.m_miningSchema;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public String getPMMLVersion()
/*  84:    */   {
/*  85:152 */     return this.m_pmmlVersion;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public String getCreatorApplication()
/*  89:    */   {
/*  90:162 */     return this.m_creatorApplication;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public void setLog(Logger log)
/*  94:    */   {
/*  95:171 */     this.m_log = log;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public Logger getLog()
/*  99:    */   {
/* 100:180 */     return this.m_log;
/* 101:    */   }
/* 102:    */   
/* 103:    */   public void buildClassifier(Instances data)
/* 104:    */     throws Exception
/* 105:    */   {
/* 106:190 */     throw new Exception("[PMMLClassifier] PMML models are pre-built and static!");
/* 107:    */   }
/* 108:    */   
/* 109:    */   public void done()
/* 110:    */   {
/* 111:203 */     this.m_initialized = false;
/* 112:204 */     this.m_fieldsMap = null;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public void mapToMiningSchema(Instances dataSet)
/* 116:    */     throws Exception
/* 117:    */   {
/* 118:214 */     if (this.m_fieldsMap == null)
/* 119:    */     {
/* 120:216 */       this.m_fieldsMap = new MappingInfo(dataSet, this.m_miningSchema, this.m_log);
/* 121:217 */       this.m_initialized = true;
/* 122:    */     }
/* 123:    */   }
/* 124:    */   
/* 125:    */   public String getFieldsMappingString()
/* 126:    */   {
/* 127:229 */     if (!this.m_initialized) {
/* 128:230 */       return null;
/* 129:    */     }
/* 130:232 */     return this.m_fieldsMap.getFieldsMappingString();
/* 131:    */   }
/* 132:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.pmml.consumer.PMMLClassifier
 * JD-Core Version:    0.7.0.1
 */