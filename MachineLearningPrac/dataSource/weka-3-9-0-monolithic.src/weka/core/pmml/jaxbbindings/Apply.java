/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   7:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   8:    */ import javax.xml.bind.annotation.XmlElement;
/*   9:    */ import javax.xml.bind.annotation.XmlElements;
/*  10:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  11:    */ import javax.xml.bind.annotation.XmlType;
/*  12:    */ 
/*  13:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  14:    */ @XmlType(name="", propOrder={"extension", "expression"})
/*  15:    */ @XmlRootElement(name="Apply")
/*  16:    */ public class Apply
/*  17:    */ {
/*  18:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected List<Extension> extension;
/*  20:    */   @XmlElements({@XmlElement(name="Apply", namespace="http://www.dmg.org/PMML-4_1", required=true, type=Apply.class), @XmlElement(name="Discretize", namespace="http://www.dmg.org/PMML-4_1", required=true, type=Discretize.class), @XmlElement(name="NormContinuous", namespace="http://www.dmg.org/PMML-4_1", required=true, type=NormContinuous.class), @XmlElement(name="MapValues", namespace="http://www.dmg.org/PMML-4_1", required=true, type=MapValues.class), @XmlElement(name="Constant", namespace="http://www.dmg.org/PMML-4_1", required=true, type=Constant.class), @XmlElement(name="Aggregate", namespace="http://www.dmg.org/PMML-4_1", required=true, type=Aggregate.class), @XmlElement(name="FieldRef", namespace="http://www.dmg.org/PMML-4_1", required=true, type=FieldRef.class), @XmlElement(name="NormDiscrete", namespace="http://www.dmg.org/PMML-4_1", required=true, type=NormDiscrete.class)})
/*  21:    */   protected List<Object> expression;
/*  22:    */   @XmlAttribute(required=true)
/*  23:    */   protected String function;
/*  24:    */   @XmlAttribute
/*  25:    */   protected INVALIDVALUETREATMENTMETHOD invalidValueTreatment;
/*  26:    */   @XmlAttribute
/*  27:    */   protected String mapMissingTo;
/*  28:    */   
/*  29:    */   public List<Extension> getExtension()
/*  30:    */   {
/*  31: 98 */     if (this.extension == null) {
/*  32: 99 */       this.extension = new ArrayList();
/*  33:    */     }
/*  34:101 */     return this.extension;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public List<Object> getEXPRESSION()
/*  38:    */   {
/*  39:134 */     if (this.expression == null) {
/*  40:135 */       this.expression = new ArrayList();
/*  41:    */     }
/*  42:137 */     return this.expression;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public String getFunction()
/*  46:    */   {
/*  47:149 */     return this.function;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void setFunction(String value)
/*  51:    */   {
/*  52:161 */     this.function = value;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public INVALIDVALUETREATMENTMETHOD getInvalidValueTreatment()
/*  56:    */   {
/*  57:173 */     if (this.invalidValueTreatment == null) {
/*  58:174 */       return INVALIDVALUETREATMENTMETHOD.RETURN_INVALID;
/*  59:    */     }
/*  60:176 */     return this.invalidValueTreatment;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void setInvalidValueTreatment(INVALIDVALUETREATMENTMETHOD value)
/*  64:    */   {
/*  65:189 */     this.invalidValueTreatment = value;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public String getMapMissingTo()
/*  69:    */   {
/*  70:201 */     return this.mapMissingTo;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void setMapMissingTo(String value)
/*  74:    */   {
/*  75:213 */     this.mapMissingTo = value;
/*  76:    */   }
/*  77:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Apply
 * JD-Core Version:    0.7.0.1
 */