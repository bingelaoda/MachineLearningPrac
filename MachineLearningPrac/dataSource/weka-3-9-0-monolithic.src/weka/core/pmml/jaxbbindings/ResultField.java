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
/*  14:    */ @XmlRootElement(name="ResultField")
/*  15:    */ public class ResultField
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlAttribute
/*  20:    */   protected DATATYPE dataType;
/*  21:    */   @XmlAttribute
/*  22:    */   protected String displayName;
/*  23:    */   @XmlAttribute
/*  24:    */   protected RESULTFEATURE feature;
/*  25:    */   @XmlAttribute(required=true)
/*  26:    */   protected String name;
/*  27:    */   @XmlAttribute
/*  28:    */   protected OPTYPE optype;
/*  29:    */   @XmlAttribute
/*  30:    */   protected String value;
/*  31:    */   
/*  32:    */   public List<Extension> getExtension()
/*  33:    */   {
/*  34: 93 */     if (this.extension == null) {
/*  35: 94 */       this.extension = new ArrayList();
/*  36:    */     }
/*  37: 96 */     return this.extension;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public DATATYPE getDataType()
/*  41:    */   {
/*  42:108 */     return this.dataType;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void setDataType(DATATYPE value)
/*  46:    */   {
/*  47:120 */     this.dataType = value;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public String getDisplayName()
/*  51:    */   {
/*  52:132 */     return this.displayName;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public void setDisplayName(String value)
/*  56:    */   {
/*  57:144 */     this.displayName = value;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public RESULTFEATURE getFeature()
/*  61:    */   {
/*  62:156 */     return this.feature;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public void setFeature(RESULTFEATURE value)
/*  66:    */   {
/*  67:168 */     this.feature = value;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public String getName()
/*  71:    */   {
/*  72:180 */     return this.name;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public void setName(String value)
/*  76:    */   {
/*  77:192 */     this.name = value;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public OPTYPE getOptype()
/*  81:    */   {
/*  82:204 */     return this.optype;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public void setOptype(OPTYPE value)
/*  86:    */   {
/*  87:216 */     this.optype = value;
/*  88:    */   }
/*  89:    */   
/*  90:    */   public String getValue()
/*  91:    */   {
/*  92:228 */     return this.value;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public void setValue(String value)
/*  96:    */   {
/*  97:240 */     this.value = value;
/*  98:    */   }
/*  99:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.ResultField
 * JD-Core Version:    0.7.0.1
 */