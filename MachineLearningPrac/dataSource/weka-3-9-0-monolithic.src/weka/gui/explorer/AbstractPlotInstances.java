/*   1:    */ package weka.gui.explorer;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Instances;
/*   7:    */ import weka.core.Option;
/*   8:    */ import weka.core.OptionHandler;
/*   9:    */ import weka.gui.visualize.PlotData2D;
/*  10:    */ 
/*  11:    */ public abstract class AbstractPlotInstances
/*  12:    */   implements Serializable, OptionHandler
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = 2375155184845160908L;
/*  15:    */   protected Instances m_Instances;
/*  16:    */   protected Instances m_PlotInstances;
/*  17:    */   protected boolean m_FinishUpCalled;
/*  18:    */   
/*  19:    */   public AbstractPlotInstances()
/*  20:    */   {
/*  21: 57 */     initialize();
/*  22:    */   }
/*  23:    */   
/*  24:    */   protected void initialize()
/*  25:    */   {
/*  26: 64 */     this.m_Instances = null;
/*  27: 65 */     this.m_PlotInstances = null;
/*  28: 66 */     this.m_FinishUpCalled = false;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public Enumeration<Option> listOptions()
/*  32:    */   {
/*  33: 76 */     return new Vector().elements();
/*  34:    */   }
/*  35:    */   
/*  36:    */   public void setOptions(String[] options)
/*  37:    */     throws Exception
/*  38:    */   {}
/*  39:    */   
/*  40:    */   public String[] getOptions()
/*  41:    */   {
/*  42: 98 */     return new String[0];
/*  43:    */   }
/*  44:    */   
/*  45:    */   protected abstract void determineFormat();
/*  46:    */   
/*  47:    */   public void setInstances(Instances value)
/*  48:    */   {
/*  49:112 */     this.m_Instances = value;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public Instances getInstances()
/*  53:    */   {
/*  54:121 */     return this.m_Instances;
/*  55:    */   }
/*  56:    */   
/*  57:    */   protected void check()
/*  58:    */   {
/*  59:128 */     if (this.m_Instances == null) {
/*  60:129 */       throw new IllegalStateException("No instances set!");
/*  61:    */     }
/*  62:    */   }
/*  63:    */   
/*  64:    */   public void setUp()
/*  65:    */   {
/*  66:137 */     this.m_FinishUpCalled = false;
/*  67:    */     
/*  68:139 */     check();
/*  69:140 */     determineFormat();
/*  70:    */   }
/*  71:    */   
/*  72:    */   protected void finishUp()
/*  73:    */   {
/*  74:147 */     this.m_FinishUpCalled = true;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public boolean canPlot(boolean setup)
/*  78:    */   {
/*  79:    */     try
/*  80:    */     {
/*  81:159 */       if (setup) {
/*  82:160 */         setUp();
/*  83:    */       }
/*  84:162 */       return getPlotInstances().numInstances() > 0;
/*  85:    */     }
/*  86:    */     catch (Exception e) {}
/*  87:164 */     return false;
/*  88:    */   }
/*  89:    */   
/*  90:    */   public Instances getPlotInstances()
/*  91:    */   {
/*  92:174 */     if (!this.m_FinishUpCalled) {
/*  93:175 */       finishUp();
/*  94:    */     }
/*  95:178 */     return this.m_PlotInstances;
/*  96:    */   }
/*  97:    */   
/*  98:    */   protected abstract PlotData2D createPlotData(String paramString)
/*  99:    */     throws Exception;
/* 100:    */   
/* 101:    */   public PlotData2D getPlotData(String name)
/* 102:    */     throws Exception
/* 103:    */   {
/* 104:200 */     if (!this.m_FinishUpCalled) {
/* 105:201 */       finishUp();
/* 106:    */     }
/* 107:204 */     return createPlotData(name);
/* 108:    */   }
/* 109:    */   
/* 110:    */   public void cleanUp()
/* 111:    */   {
/* 112:211 */     this.m_Instances = null;
/* 113:212 */     this.m_PlotInstances = null;
/* 114:213 */     this.m_FinishUpCalled = false;
/* 115:    */   }
/* 116:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.explorer.AbstractPlotInstances
 * JD-Core Version:    0.7.0.1
 */