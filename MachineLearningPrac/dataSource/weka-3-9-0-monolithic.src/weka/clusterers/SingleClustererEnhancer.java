/*   1:    */ package weka.clusterers;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Capabilities;
/*   7:    */ import weka.core.Capabilities.Capability;
/*   8:    */ import weka.core.Option;
/*   9:    */ import weka.core.OptionHandler;
/*  10:    */ import weka.core.Utils;
/*  11:    */ 
/*  12:    */ public abstract class SingleClustererEnhancer
/*  13:    */   extends AbstractClusterer
/*  14:    */   implements OptionHandler
/*  15:    */ {
/*  16:    */   private static final long serialVersionUID = 4893928362926428671L;
/*  17: 47 */   protected Clusterer m_Clusterer = new SimpleKMeans();
/*  18:    */   
/*  19:    */   protected String defaultClustererString()
/*  20:    */   {
/*  21: 55 */     return SimpleKMeans.class.getName();
/*  22:    */   }
/*  23:    */   
/*  24:    */   public Enumeration<Option> listOptions()
/*  25:    */   {
/*  26: 65 */     Vector<Option> result = new Vector();
/*  27:    */     
/*  28: 67 */     result.addElement(new Option("\tFull name of base clusterer.\n\t(default: " + defaultClustererString() + ")", "W", 1, "-W"));
/*  29:    */     
/*  30:    */ 
/*  31: 70 */     result.addAll(Collections.list(super.listOptions()));
/*  32: 72 */     if ((this.m_Clusterer instanceof OptionHandler))
/*  33:    */     {
/*  34: 73 */       result.addElement(new Option("", "", 0, "\nOptions specific to clusterer " + this.m_Clusterer.getClass().getName() + ":"));
/*  35:    */       
/*  36:    */ 
/*  37:    */ 
/*  38: 77 */       result.addAll(Collections.list(((OptionHandler)this.m_Clusterer).listOptions()));
/*  39:    */     }
/*  40: 81 */     return result.elements();
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void setOptions(String[] options)
/*  44:    */     throws Exception
/*  45:    */   {
/*  46: 94 */     String tmpStr = Utils.getOption('W', options);
/*  47: 95 */     super.setOptions(options);
/*  48: 96 */     if (tmpStr.length() > 0)
/*  49:    */     {
/*  50: 97 */       setClusterer(AbstractClusterer.forName(tmpStr, null));
/*  51: 98 */       setClusterer(AbstractClusterer.forName(tmpStr, Utils.partitionOptions(options)));
/*  52:    */     }
/*  53:    */     else
/*  54:    */     {
/*  55:101 */       setClusterer(AbstractClusterer.forName(defaultClustererString(), null));
/*  56:102 */       setClusterer(AbstractClusterer.forName(defaultClustererString(), Utils.partitionOptions(options)));
/*  57:    */     }
/*  58:    */   }
/*  59:    */   
/*  60:    */   public String[] getOptions()
/*  61:    */   {
/*  62:114 */     Vector<String> result = new Vector();
/*  63:    */     
/*  64:116 */     result.add("-W");
/*  65:117 */     result.add(getClusterer().getClass().getName());
/*  66:    */     
/*  67:119 */     Collections.addAll(result, super.getOptions());
/*  68:121 */     if ((getClusterer() instanceof OptionHandler))
/*  69:    */     {
/*  70:122 */       String[] options = ((OptionHandler)getClusterer()).getOptions();
/*  71:124 */       if (options.length > 0) {
/*  72:125 */         result.add("--");
/*  73:    */       }
/*  74:127 */       Collections.addAll(result, options);
/*  75:    */     }
/*  76:130 */     return (String[])result.toArray(new String[result.size()]);
/*  77:    */   }
/*  78:    */   
/*  79:    */   public String clustererTipText()
/*  80:    */   {
/*  81:140 */     return "The base clusterer to be used.";
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void setClusterer(Clusterer value)
/*  85:    */   {
/*  86:149 */     this.m_Clusterer = value;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public Clusterer getClusterer()
/*  90:    */   {
/*  91:158 */     return this.m_Clusterer;
/*  92:    */   }
/*  93:    */   
/*  94:    */   protected String getClustererSpec()
/*  95:    */   {
/*  96:171 */     Clusterer clusterer = getClusterer();
/*  97:172 */     String result = clusterer.getClass().getName();
/*  98:174 */     if ((clusterer instanceof OptionHandler)) {
/*  99:175 */       result = result + " " + Utils.joinOptions(((OptionHandler)clusterer).getOptions());
/* 100:    */     }
/* 101:179 */     return result;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public Capabilities getCapabilities()
/* 105:    */   {
/* 106:    */     Capabilities result;
/* 107:    */     Capabilities result;
/* 108:191 */     if (getClusterer() == null) {
/* 109:192 */       result = super.getCapabilities();
/* 110:    */     } else {
/* 111:194 */       result = getClusterer().getCapabilities();
/* 112:    */     }
/* 113:198 */     for (Capabilities.Capability cap : Capabilities.Capability.values()) {
/* 114:199 */       result.enableDependency(cap);
/* 115:    */     }
/* 116:202 */     return result;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public int numberOfClusters()
/* 120:    */     throws Exception
/* 121:    */   {
/* 122:213 */     return this.m_Clusterer.numberOfClusters();
/* 123:    */   }
/* 124:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.clusterers.SingleClustererEnhancer
 * JD-Core Version:    0.7.0.1
 */