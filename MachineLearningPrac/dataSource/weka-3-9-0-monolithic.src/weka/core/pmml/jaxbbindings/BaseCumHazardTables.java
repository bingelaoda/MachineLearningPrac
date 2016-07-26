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
/*  13:    */ @XmlType(name="", propOrder={"extension", "baselineStratum", "baselineCell"})
/*  14:    */ @XmlRootElement(name="BaseCumHazardTables")
/*  15:    */ public class BaseCumHazardTables
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="BaselineStratum", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected List<BaselineStratum> baselineStratum;
/*  21:    */   @XmlElement(name="BaselineCell", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  22:    */   protected List<BaselineCell> baselineCell;
/*  23:    */   @XmlAttribute
/*  24:    */   protected Double maxTime;
/*  25:    */   
/*  26:    */   public List<Extension> getExtension()
/*  27:    */   {
/*  28: 88 */     if (this.extension == null) {
/*  29: 89 */       this.extension = new ArrayList();
/*  30:    */     }
/*  31: 91 */     return this.extension;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public List<BaselineStratum> getBaselineStratum()
/*  35:    */   {
/*  36:117 */     if (this.baselineStratum == null) {
/*  37:118 */       this.baselineStratum = new ArrayList();
/*  38:    */     }
/*  39:120 */     return this.baselineStratum;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public List<BaselineCell> getBaselineCell()
/*  43:    */   {
/*  44:146 */     if (this.baselineCell == null) {
/*  45:147 */       this.baselineCell = new ArrayList();
/*  46:    */     }
/*  47:149 */     return this.baselineCell;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public Double getMaxTime()
/*  51:    */   {
/*  52:161 */     return this.maxTime;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public void setMaxTime(Double value)
/*  56:    */   {
/*  57:173 */     this.maxTime = value;
/*  58:    */   }
/*  59:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.BaseCumHazardTables
 * JD-Core Version:    0.7.0.1
 */