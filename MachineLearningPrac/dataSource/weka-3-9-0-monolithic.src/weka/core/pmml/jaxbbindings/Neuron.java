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
/*  13:    */ @XmlType(name="", propOrder={"extension", "con"})
/*  14:    */ @XmlRootElement(name="Neuron")
/*  15:    */ public class Neuron
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="Con", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected List<Con1> con;
/*  21:    */   @XmlAttribute
/*  22:    */   protected Double altitude;
/*  23:    */   @XmlAttribute
/*  24:    */   protected Double bias;
/*  25:    */   @XmlAttribute(required=true)
/*  26:    */   protected String id;
/*  27:    */   @XmlAttribute
/*  28:    */   protected Double width;
/*  29:    */   
/*  30:    */   public List<Extension> getExtension()
/*  31:    */   {
/*  32: 91 */     if (this.extension == null) {
/*  33: 92 */       this.extension = new ArrayList();
/*  34:    */     }
/*  35: 94 */     return this.extension;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public List<Con1> getCon()
/*  39:    */   {
/*  40:120 */     if (this.con == null) {
/*  41:121 */       this.con = new ArrayList();
/*  42:    */     }
/*  43:123 */     return this.con;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public Double getAltitude()
/*  47:    */   {
/*  48:135 */     return this.altitude;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void setAltitude(Double value)
/*  52:    */   {
/*  53:147 */     this.altitude = value;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public Double getBias()
/*  57:    */   {
/*  58:159 */     return this.bias;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void setBias(Double value)
/*  62:    */   {
/*  63:171 */     this.bias = value;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public String getId()
/*  67:    */   {
/*  68:183 */     return this.id;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void setId(String value)
/*  72:    */   {
/*  73:195 */     this.id = value;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public Double getWidth()
/*  77:    */   {
/*  78:207 */     return this.width;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void setWidth(Double value)
/*  82:    */   {
/*  83:219 */     this.width = value;
/*  84:    */   }
/*  85:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Neuron
 * JD-Core Version:    0.7.0.1
 */