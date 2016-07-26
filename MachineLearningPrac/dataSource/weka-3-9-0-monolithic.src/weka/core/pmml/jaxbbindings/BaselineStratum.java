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
/*  13:    */ @XmlType(name="", propOrder={"extension", "baselineCell"})
/*  14:    */ @XmlRootElement(name="BaselineStratum")
/*  15:    */ public class BaselineStratum
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="BaselineCell", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected List<BaselineCell> baselineCell;
/*  21:    */   @XmlAttribute
/*  22:    */   protected String label;
/*  23:    */   @XmlAttribute(required=true)
/*  24:    */   protected double maxTime;
/*  25:    */   @XmlAttribute(required=true)
/*  26:    */   protected String value;
/*  27:    */   
/*  28:    */   public List<Extension> getExtension()
/*  29:    */   {
/*  30: 88 */     if (this.extension == null) {
/*  31: 89 */       this.extension = new ArrayList();
/*  32:    */     }
/*  33: 91 */     return this.extension;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public List<BaselineCell> getBaselineCell()
/*  37:    */   {
/*  38:117 */     if (this.baselineCell == null) {
/*  39:118 */       this.baselineCell = new ArrayList();
/*  40:    */     }
/*  41:120 */     return this.baselineCell;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public String getLabel()
/*  45:    */   {
/*  46:132 */     return this.label;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void setLabel(String value)
/*  50:    */   {
/*  51:144 */     this.label = value;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public double getMaxTime()
/*  55:    */   {
/*  56:152 */     return this.maxTime;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public void setMaxTime(double value)
/*  60:    */   {
/*  61:160 */     this.maxTime = value;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public String getValue()
/*  65:    */   {
/*  66:172 */     return this.value;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public void setValue(String value)
/*  70:    */   {
/*  71:184 */     this.value = value;
/*  72:    */   }
/*  73:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.BaselineStratum
 * JD-Core Version:    0.7.0.1
 */