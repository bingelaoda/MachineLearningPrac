/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   7:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   8:    */ import javax.xml.bind.annotation.XmlElement;
/*   9:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  10:    */ import javax.xml.bind.annotation.XmlType;
/*  11:    */ 
/*  12:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  13:    */ @XmlType(name="", propOrder={"extension", "counts", "numericInfo", "discrStats", "contStats", "anova"})
/*  14:    */ @XmlRootElement(name="UnivariateStats")
/*  15:    */ public class UnivariateStats
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="Counts", namespace="http://www.dmg.org/PMML-4_1")
/*  20:    */   protected Counts counts;
/*  21:    */   @XmlElement(name="NumericInfo", namespace="http://www.dmg.org/PMML-4_1")
/*  22:    */   protected NumericInfo numericInfo;
/*  23:    */   @XmlElement(name="DiscrStats", namespace="http://www.dmg.org/PMML-4_1")
/*  24:    */   protected DiscrStats discrStats;
/*  25:    */   @XmlElement(name="ContStats", namespace="http://www.dmg.org/PMML-4_1")
/*  26:    */   protected ContStats contStats;
/*  27:    */   @XmlElement(name="Anova", namespace="http://www.dmg.org/PMML-4_1")
/*  28:    */   protected Anova anova;
/*  29:    */   @XmlAttribute
/*  30:    */   protected String field;
/*  31:    */   @XmlAttribute
/*  32:    */   protected String weighted;
/*  33:    */   
/*  34:    */   public List<Extension> getExtension()
/*  35:    */   {
/*  36:108 */     if (this.extension == null) {
/*  37:109 */       this.extension = new ArrayList();
/*  38:    */     }
/*  39:111 */     return this.extension;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public Counts getCounts()
/*  43:    */   {
/*  44:123 */     return this.counts;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void setCounts(Counts value)
/*  48:    */   {
/*  49:135 */     this.counts = value;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public NumericInfo getNumericInfo()
/*  53:    */   {
/*  54:147 */     return this.numericInfo;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void setNumericInfo(NumericInfo value)
/*  58:    */   {
/*  59:159 */     this.numericInfo = value;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public DiscrStats getDiscrStats()
/*  63:    */   {
/*  64:171 */     return this.discrStats;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void setDiscrStats(DiscrStats value)
/*  68:    */   {
/*  69:183 */     this.discrStats = value;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public ContStats getContStats()
/*  73:    */   {
/*  74:195 */     return this.contStats;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public void setContStats(ContStats value)
/*  78:    */   {
/*  79:207 */     this.contStats = value;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public Anova getAnova()
/*  83:    */   {
/*  84:219 */     return this.anova;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public void setAnova(Anova value)
/*  88:    */   {
/*  89:231 */     this.anova = value;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public String getField()
/*  93:    */   {
/*  94:243 */     return this.field;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public void setField(String value)
/*  98:    */   {
/*  99:255 */     this.field = value;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public String getWeighted()
/* 103:    */   {
/* 104:267 */     if (this.weighted == null) {
/* 105:268 */       return "0";
/* 106:    */     }
/* 107:270 */     return this.weighted;
/* 108:    */   }
/* 109:    */   
/* 110:    */   public void setWeighted(String value)
/* 111:    */   {
/* 112:283 */     this.weighted = value;
/* 113:    */   }
/* 114:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.UnivariateStats
 * JD-Core Version:    0.7.0.1
 */