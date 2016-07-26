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
/*  13:    */ @XmlType(name="", propOrder={"extension", "interval", "numarray"})
/*  14:    */ @XmlRootElement(name="ContStats")
/*  15:    */ public class ContStats
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="Interval", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected List<Interval> interval;
/*  21:    */   @XmlElement(name="Array", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  22:    */   protected List<ArrayType> numarray;
/*  23:    */   @XmlAttribute
/*  24:    */   protected Double totalSquaresSum;
/*  25:    */   @XmlAttribute
/*  26:    */   protected Double totalValuesSum;
/*  27:    */   
/*  28:    */   public List<Extension> getExtension()
/*  29:    */   {
/*  30: 89 */     if (this.extension == null) {
/*  31: 90 */       this.extension = new ArrayList();
/*  32:    */     }
/*  33: 92 */     return this.extension;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public List<Interval> getInterval()
/*  37:    */   {
/*  38:118 */     if (this.interval == null) {
/*  39:119 */       this.interval = new ArrayList();
/*  40:    */     }
/*  41:121 */     return this.interval;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public List<ArrayType> getNUMARRAY()
/*  45:    */   {
/*  46:147 */     if (this.numarray == null) {
/*  47:148 */       this.numarray = new ArrayList();
/*  48:    */     }
/*  49:150 */     return this.numarray;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public Double getTotalSquaresSum()
/*  53:    */   {
/*  54:162 */     return this.totalSquaresSum;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void setTotalSquaresSum(Double value)
/*  58:    */   {
/*  59:174 */     this.totalSquaresSum = value;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public Double getTotalValuesSum()
/*  63:    */   {
/*  64:186 */     return this.totalValuesSum;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void setTotalValuesSum(Double value)
/*  68:    */   {
/*  69:198 */     this.totalValuesSum = value;
/*  70:    */   }
/*  71:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.ContStats
 * JD-Core Version:    0.7.0.1
 */