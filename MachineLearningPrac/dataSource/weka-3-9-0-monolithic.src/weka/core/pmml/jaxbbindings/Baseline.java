/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   7:    */ import javax.xml.bind.annotation.XmlElement;
/*   8:    */ import javax.xml.bind.annotation.XmlRootElement;
/*   9:    */ import javax.xml.bind.annotation.XmlType;
/*  10:    */ 
/*  11:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  12:    */ @XmlType(name="", propOrder={"anyDistribution", "gaussianDistribution", "poissonDistribution", "uniformDistribution", "extension", "countTable", "normalizedCountTable", "fieldRef"})
/*  13:    */ @XmlRootElement(name="Baseline")
/*  14:    */ public class Baseline
/*  15:    */ {
/*  16:    */   @XmlElement(name="AnyDistribution", namespace="http://www.dmg.org/PMML-4_1")
/*  17:    */   protected AnyDistribution anyDistribution;
/*  18:    */   @XmlElement(name="GaussianDistribution", namespace="http://www.dmg.org/PMML-4_1")
/*  19:    */   protected GaussianDistribution gaussianDistribution;
/*  20:    */   @XmlElement(name="PoissonDistribution", namespace="http://www.dmg.org/PMML-4_1")
/*  21:    */   protected PoissonDistribution poissonDistribution;
/*  22:    */   @XmlElement(name="UniformDistribution", namespace="http://www.dmg.org/PMML-4_1")
/*  23:    */   protected UniformDistribution uniformDistribution;
/*  24:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  25:    */   protected List<Extension> extension;
/*  26:    */   @XmlElement(name="CountTable", namespace="http://www.dmg.org/PMML-4_1")
/*  27:    */   protected COUNTTABLETYPE countTable;
/*  28:    */   @XmlElement(name="NormalizedCountTable", namespace="http://www.dmg.org/PMML-4_1")
/*  29:    */   protected COUNTTABLETYPE normalizedCountTable;
/*  30:    */   @XmlElement(name="FieldRef", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  31:    */   protected List<FieldRef> fieldRef;
/*  32:    */   
/*  33:    */   public AnyDistribution getAnyDistribution()
/*  34:    */   {
/*  35: 82 */     return this.anyDistribution;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void setAnyDistribution(AnyDistribution value)
/*  39:    */   {
/*  40: 94 */     this.anyDistribution = value;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public GaussianDistribution getGaussianDistribution()
/*  44:    */   {
/*  45:106 */     return this.gaussianDistribution;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void setGaussianDistribution(GaussianDistribution value)
/*  49:    */   {
/*  50:118 */     this.gaussianDistribution = value;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public PoissonDistribution getPoissonDistribution()
/*  54:    */   {
/*  55:130 */     return this.poissonDistribution;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public void setPoissonDistribution(PoissonDistribution value)
/*  59:    */   {
/*  60:142 */     this.poissonDistribution = value;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public UniformDistribution getUniformDistribution()
/*  64:    */   {
/*  65:154 */     return this.uniformDistribution;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public void setUniformDistribution(UniformDistribution value)
/*  69:    */   {
/*  70:166 */     this.uniformDistribution = value;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public List<Extension> getExtension()
/*  74:    */   {
/*  75:192 */     if (this.extension == null) {
/*  76:193 */       this.extension = new ArrayList();
/*  77:    */     }
/*  78:195 */     return this.extension;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public COUNTTABLETYPE getCountTable()
/*  82:    */   {
/*  83:207 */     return this.countTable;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public void setCountTable(COUNTTABLETYPE value)
/*  87:    */   {
/*  88:219 */     this.countTable = value;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public COUNTTABLETYPE getNormalizedCountTable()
/*  92:    */   {
/*  93:231 */     return this.normalizedCountTable;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public void setNormalizedCountTable(COUNTTABLETYPE value)
/*  97:    */   {
/*  98:243 */     this.normalizedCountTable = value;
/*  99:    */   }
/* 100:    */   
/* 101:    */   public List<FieldRef> getFieldRef()
/* 102:    */   {
/* 103:269 */     if (this.fieldRef == null) {
/* 104:270 */       this.fieldRef = new ArrayList();
/* 105:    */     }
/* 106:272 */     return this.fieldRef;
/* 107:    */   }
/* 108:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Baseline
 * JD-Core Version:    0.7.0.1
 */