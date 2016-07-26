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
/*  13:    */ @XmlType(name="", propOrder={"extension", "comparisons"})
/*  14:    */ @XmlRootElement(name="ClusteringField")
/*  15:    */ public class ClusteringField
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="Comparisons", namespace="http://www.dmg.org/PMML-4_1")
/*  20:    */   protected Comparisons comparisons;
/*  21:    */   @XmlAttribute
/*  22:    */   protected COMPAREFUNCTION compareFunction;
/*  23:    */   @XmlAttribute(required=true)
/*  24:    */   protected String field;
/*  25:    */   @XmlAttribute
/*  26:    */   protected Double fieldWeight;
/*  27:    */   @XmlAttribute
/*  28:    */   protected String isCenterField;
/*  29:    */   @XmlAttribute
/*  30:    */   protected Double similarityScale;
/*  31:    */   
/*  32:    */   public List<Extension> getExtension()
/*  33:    */   {
/*  34:101 */     if (this.extension == null) {
/*  35:102 */       this.extension = new ArrayList();
/*  36:    */     }
/*  37:104 */     return this.extension;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public Comparisons getComparisons()
/*  41:    */   {
/*  42:116 */     return this.comparisons;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void setComparisons(Comparisons value)
/*  46:    */   {
/*  47:128 */     this.comparisons = value;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public COMPAREFUNCTION getCompareFunction()
/*  51:    */   {
/*  52:140 */     return this.compareFunction;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public void setCompareFunction(COMPAREFUNCTION value)
/*  56:    */   {
/*  57:152 */     this.compareFunction = value;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public String getField()
/*  61:    */   {
/*  62:164 */     return this.field;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public void setField(String value)
/*  66:    */   {
/*  67:176 */     this.field = value;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public double getFieldWeight()
/*  71:    */   {
/*  72:188 */     if (this.fieldWeight == null) {
/*  73:189 */       return 1.0D;
/*  74:    */     }
/*  75:191 */     return this.fieldWeight.doubleValue();
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void setFieldWeight(Double value)
/*  79:    */   {
/*  80:204 */     this.fieldWeight = value;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public String getIsCenterField()
/*  84:    */   {
/*  85:216 */     if (this.isCenterField == null) {
/*  86:217 */       return "true";
/*  87:    */     }
/*  88:219 */     return this.isCenterField;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public void setIsCenterField(String value)
/*  92:    */   {
/*  93:232 */     this.isCenterField = value;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public Double getSimilarityScale()
/*  97:    */   {
/*  98:244 */     return this.similarityScale;
/*  99:    */   }
/* 100:    */   
/* 101:    */   public void setSimilarityScale(Double value)
/* 102:    */   {
/* 103:256 */     this.similarityScale = value;
/* 104:    */   }
/* 105:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.ClusteringField
 * JD-Core Version:    0.7.0.1
 */