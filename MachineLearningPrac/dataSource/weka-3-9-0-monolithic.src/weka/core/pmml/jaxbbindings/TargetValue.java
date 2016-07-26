/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.math.BigDecimal;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.List;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   7:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   8:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   9:    */ import javax.xml.bind.annotation.XmlElement;
/*  10:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  11:    */ import javax.xml.bind.annotation.XmlType;
/*  12:    */ 
/*  13:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  14:    */ @XmlType(name="", propOrder={"extension", "partition"})
/*  15:    */ @XmlRootElement(name="TargetValue")
/*  16:    */ public class TargetValue
/*  17:    */ {
/*  18:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected List<Extension> extension;
/*  20:    */   @XmlElement(name="Partition", namespace="http://www.dmg.org/PMML-4_1")
/*  21:    */   protected Partition partition;
/*  22:    */   @XmlAttribute
/*  23:    */   protected Double defaultValue;
/*  24:    */   @XmlAttribute
/*  25:    */   protected String displayValue;
/*  26:    */   @XmlAttribute
/*  27:    */   protected BigDecimal priorProbability;
/*  28:    */   @XmlAttribute
/*  29:    */   protected String value;
/*  30:    */   
/*  31:    */   public List<Extension> getExtension()
/*  32:    */   {
/*  33: 92 */     if (this.extension == null) {
/*  34: 93 */       this.extension = new ArrayList();
/*  35:    */     }
/*  36: 95 */     return this.extension;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public Partition getPartition()
/*  40:    */   {
/*  41:107 */     return this.partition;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public void setPartition(Partition value)
/*  45:    */   {
/*  46:119 */     this.partition = value;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public Double getDefaultValue()
/*  50:    */   {
/*  51:131 */     return this.defaultValue;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public void setDefaultValue(Double value)
/*  55:    */   {
/*  56:143 */     this.defaultValue = value;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public String getDisplayValue()
/*  60:    */   {
/*  61:155 */     return this.displayValue;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public void setDisplayValue(String value)
/*  65:    */   {
/*  66:167 */     this.displayValue = value;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public BigDecimal getPriorProbability()
/*  70:    */   {
/*  71:179 */     return this.priorProbability;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void setPriorProbability(BigDecimal value)
/*  75:    */   {
/*  76:191 */     this.priorProbability = value;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public String getValue()
/*  80:    */   {
/*  81:203 */     return this.value;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void setValue(String value)
/*  85:    */   {
/*  86:215 */     this.value = value;
/*  87:    */   }
/*  88:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.TargetValue
 * JD-Core Version:    0.7.0.1
 */