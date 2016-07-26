/*   1:    */ package org.boon.config;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import java.util.Map;
/*   6:    */ import org.boon.core.Predicate;
/*   7:    */ import org.boon.di.Context;
/*   8:    */ import org.boon.di.Inject;
/*   9:    */ 
/*  10:    */ public class ContextConfigReader
/*  11:    */ {
/*  12: 43 */   private static final String CONFIG_CLASSPATH_RESOURCE = System.getProperty("BOON_CONFIG_CLASSPATH_RESOURCE", "classpath://etc/boon/");
/*  13: 45 */   private static final String CONFIG_DIR = System.getProperty("BOON_CONFIG_DIR", "/etc/boon/");
/*  14: 47 */   private static final String RUNTIME_MODULE_NAME = System.getProperty("BOON_RUNTIME_MODULE_NAME", "boon");
/*  15: 49 */   private static final String MODULE_CONFIG_DIR = System.getProperty("BOON_MODULE_CONFIG_DIR", CONFIG_DIR + RUNTIME_MODULE_NAME + "/");
/*  16:    */   @Inject
/*  17: 53 */   private boolean useNameSpacePrefix = false;
/*  18:    */   @Inject
/*  19: 57 */   private ContextConfig contextConfig = ContextConfig.JSON;
/*  20:    */   @Inject
/*  21:    */   private String namespace;
/*  22:    */   @Inject
/*  23: 65 */   private List<String> resources = new ArrayList();
/*  24:    */   @Inject
/*  25:    */   private Predicate rules;
/*  26:    */   
/*  27:    */   public Context read()
/*  28:    */   {
/*  29: 74 */     if (this.resources.size() == 0)
/*  30:    */     {
/*  31: 75 */       this.resources.add(MODULE_CONFIG_DIR);
/*  32: 76 */       this.resources.add(CONFIG_CLASSPATH_RESOURCE);
/*  33:    */     }
/*  34: 79 */     MetaConfigEvents metaConfigEvents = null;
/*  35: 80 */     if (this.rules != null) {
/*  36: 82 */       metaConfigEvents = new MetaConfigEvents()
/*  37:    */       {
/*  38:    */         public boolean parsedMeta(Map<String, Object> meta)
/*  39:    */         {
/*  40: 85 */           return ContextConfigReader.this.handleMeta(meta);
/*  41:    */         }
/*  42:    */       };
/*  43:    */     }
/*  44: 90 */     return this.contextConfig.createContext(this.namespace, this.useNameSpacePrefix, metaConfigEvents, this.resources);
/*  45:    */   }
/*  46:    */   
/*  47:    */   private boolean handleMeta(Map<String, Object> meta)
/*  48:    */   {
/*  49: 95 */     return this.rules.test(meta);
/*  50:    */   }
/*  51:    */   
/*  52:    */   public ContextConfigReader resource(String resource)
/*  53:    */   {
/*  54:100 */     this.resources.add(resource);
/*  55:101 */     return this;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public ContextConfigReader resources(String... resources)
/*  59:    */   {
/*  60:106 */     for (String resource : resources) {
/*  61:107 */       this.resources.add(resource);
/*  62:    */     }
/*  63:109 */     return this;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public ContextConfigReader userNamespacePrefix()
/*  67:    */   {
/*  68:115 */     this.useNameSpacePrefix = true;
/*  69:116 */     return this;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public ContextConfigReader rule(Predicate criteria)
/*  73:    */   {
/*  74:122 */     this.rules = criteria;
/*  75:123 */     return this;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public ContextConfigReader namespace(String namespace)
/*  79:    */   {
/*  80:128 */     this.namespace = namespace;
/*  81:129 */     return this;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public static ContextConfigReader config()
/*  85:    */   {
/*  86:134 */     return new ContextConfigReader();
/*  87:    */   }
/*  88:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.config.ContextConfigReader
 * JD-Core Version:    0.7.0.1
 */