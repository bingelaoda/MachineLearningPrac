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
/*  13:    */ @XmlType(name="", propOrder={"extension"})
/*  14:    */ @XmlRootElement(name="Aggregate")
/*  15:    */ public class Aggregate
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlAttribute(required=true)
/*  20:    */   protected String field;
/*  21:    */   @XmlAttribute(required=true)
/*  22:    */   protected String function;
/*  23:    */   @XmlAttribute
/*  24:    */   protected String groupField;
/*  25:    */   @XmlAttribute
/*  26:    */   protected String sqlWhere;
/*  27:    */   
/*  28:    */   public List<Extension> getExtension()
/*  29:    */   {
/*  30: 98 */     if (this.extension == null) {
/*  31: 99 */       this.extension = new ArrayList();
/*  32:    */     }
/*  33:101 */     return this.extension;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public String getField()
/*  37:    */   {
/*  38:113 */     return this.field;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void setField(String value)
/*  42:    */   {
/*  43:125 */     this.field = value;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public String getFunction()
/*  47:    */   {
/*  48:137 */     return this.function;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void setFunction(String value)
/*  52:    */   {
/*  53:149 */     this.function = value;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public String getGroupField()
/*  57:    */   {
/*  58:161 */     return this.groupField;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void setGroupField(String value)
/*  62:    */   {
/*  63:173 */     this.groupField = value;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public String getSqlWhere()
/*  67:    */   {
/*  68:185 */     return this.sqlWhere;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void setSqlWhere(String value)
/*  72:    */   {
/*  73:197 */     this.sqlWhere = value;
/*  74:    */   }
/*  75:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Aggregate
 * JD-Core Version:    0.7.0.1
 */