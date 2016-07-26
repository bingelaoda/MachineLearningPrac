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
/*  13:    */ @XmlType(name="", propOrder={"extension", "interval", "value"})
/*  14:    */ @XmlRootElement(name="DataField")
/*  15:    */ public class DataField
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="Interval", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected List<Interval> interval;
/*  21:    */   @XmlElement(name="Value", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  22:    */   protected List<Value> value;
/*  23:    */   @XmlAttribute(required=true)
/*  24:    */   protected DATATYPE dataType;
/*  25:    */   @XmlAttribute
/*  26:    */   protected String displayName;
/*  27:    */   @XmlAttribute
/*  28:    */   protected String isCyclic;
/*  29:    */   @XmlAttribute(required=true)
/*  30:    */   protected String name;
/*  31:    */   @XmlAttribute(required=true)
/*  32:    */   protected OPTYPE optype;
/*  33:    */   @XmlAttribute
/*  34:    */   protected String taxonomy;
/*  35:    */   
/*  36:    */   public DataField() {}
/*  37:    */   
/*  38:    */   public DataField(String name, OPTYPE optype)
/*  39:    */   {
/*  40: 90 */     this.name = name;
/*  41: 91 */     this.optype = optype;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public List<Extension> getExtension()
/*  45:    */   {
/*  46:117 */     if (this.extension == null) {
/*  47:118 */       this.extension = new ArrayList();
/*  48:    */     }
/*  49:120 */     return this.extension;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public List<Interval> getInterval()
/*  53:    */   {
/*  54:146 */     if (this.interval == null) {
/*  55:147 */       this.interval = new ArrayList();
/*  56:    */     }
/*  57:149 */     return this.interval;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public List<Value> getValues()
/*  61:    */   {
/*  62:174 */     if (this.value == null) {
/*  63:175 */       this.value = new ArrayList();
/*  64:    */     }
/*  65:177 */     return this.value;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public void addValue(Value v)
/*  69:    */   {
/*  70:181 */     if (this.value == null) {
/*  71:182 */       this.value = new ArrayList();
/*  72:    */     }
/*  73:184 */     this.value.add(v);
/*  74:    */   }
/*  75:    */   
/*  76:    */   public DATATYPE getDataType()
/*  77:    */   {
/*  78:196 */     return this.dataType;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void setDataType(DATATYPE value)
/*  82:    */   {
/*  83:208 */     this.dataType = value;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public String getDisplayName()
/*  87:    */   {
/*  88:220 */     return this.displayName;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public void setDisplayName(String value)
/*  92:    */   {
/*  93:232 */     this.displayName = value;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public String getIsCyclic()
/*  97:    */   {
/*  98:244 */     if (this.isCyclic == null) {
/*  99:245 */       return "0";
/* 100:    */     }
/* 101:247 */     return this.isCyclic;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public void setIsCyclic(String value)
/* 105:    */   {
/* 106:260 */     this.isCyclic = value;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public String getName()
/* 110:    */   {
/* 111:272 */     return this.name;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public void setName(String value)
/* 115:    */   {
/* 116:284 */     this.name = value;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public OPTYPE getOptype()
/* 120:    */   {
/* 121:296 */     return this.optype;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public void setOptype(OPTYPE value)
/* 125:    */   {
/* 126:308 */     this.optype = value;
/* 127:    */   }
/* 128:    */   
/* 129:    */   public String getTaxonomy()
/* 130:    */   {
/* 131:320 */     return this.taxonomy;
/* 132:    */   }
/* 133:    */   
/* 134:    */   public void setTaxonomy(String value)
/* 135:    */   {
/* 136:332 */     this.taxonomy = value;
/* 137:    */   }
/* 138:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.DataField
 * JD-Core Version:    0.7.0.1
 */