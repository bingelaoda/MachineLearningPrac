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
/*  13:    */ @XmlType(name="", propOrder={"extension", "constant", "fieldRef", "normContinuous", "normDiscrete", "discretize", "mapValues", "apply", "aggregate", "value"})
/*  14:    */ @XmlRootElement(name="DerivedField")
/*  15:    */ public class DerivedField
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="Constant", namespace="http://www.dmg.org/PMML-4_1")
/*  20:    */   protected Constant constant;
/*  21:    */   @XmlElement(name="FieldRef", namespace="http://www.dmg.org/PMML-4_1")
/*  22:    */   protected FieldRef fieldRef;
/*  23:    */   @XmlElement(name="NormContinuous", namespace="http://www.dmg.org/PMML-4_1")
/*  24:    */   protected NormContinuous normContinuous;
/*  25:    */   @XmlElement(name="NormDiscrete", namespace="http://www.dmg.org/PMML-4_1")
/*  26:    */   protected NormDiscrete normDiscrete;
/*  27:    */   @XmlElement(name="Discretize", namespace="http://www.dmg.org/PMML-4_1")
/*  28:    */   protected Discretize discretize;
/*  29:    */   @XmlElement(name="MapValues", namespace="http://www.dmg.org/PMML-4_1")
/*  30:    */   protected MapValues mapValues;
/*  31:    */   @XmlElement(name="Apply", namespace="http://www.dmg.org/PMML-4_1")
/*  32:    */   protected Apply apply;
/*  33:    */   @XmlElement(name="Aggregate", namespace="http://www.dmg.org/PMML-4_1")
/*  34:    */   protected Aggregate aggregate;
/*  35:    */   @XmlElement(name="Value", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  36:    */   protected List<Value> value;
/*  37:    */   @XmlAttribute(required=true)
/*  38:    */   protected DATATYPE dataType;
/*  39:    */   @XmlAttribute
/*  40:    */   protected String displayName;
/*  41:    */   @XmlAttribute
/*  42:    */   protected String name;
/*  43:    */   @XmlAttribute(required=true)
/*  44:    */   protected OPTYPE optype;
/*  45:    */   
/*  46:    */   public DerivedField() {}
/*  47:    */   
/*  48:    */   public DerivedField(String name, DATATYPE dataType, OPTYPE optype)
/*  49:    */   {
/*  50: 96 */     this.name = name;
/*  51: 97 */     this.dataType = dataType;
/*  52: 98 */     this.optype = optype;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public List<Extension> getExtension()
/*  56:    */   {
/*  57:124 */     if (this.extension == null) {
/*  58:125 */       this.extension = new ArrayList();
/*  59:    */     }
/*  60:127 */     return this.extension;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public Constant getConstant()
/*  64:    */   {
/*  65:139 */     return this.constant;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public void setConstant(Constant value)
/*  69:    */   {
/*  70:151 */     this.constant = value;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public FieldRef getFieldRef()
/*  74:    */   {
/*  75:163 */     return this.fieldRef;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void setFieldRef(FieldRef value)
/*  79:    */   {
/*  80:175 */     this.fieldRef = value;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public NormContinuous getNormContinuous()
/*  84:    */   {
/*  85:187 */     return this.normContinuous;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public void setNormContinuous(NormContinuous value)
/*  89:    */   {
/*  90:199 */     this.normContinuous = value;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public NormDiscrete getNormDiscrete()
/*  94:    */   {
/*  95:211 */     return this.normDiscrete;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public void setNormDiscrete(NormDiscrete value)
/*  99:    */   {
/* 100:223 */     this.normDiscrete = value;
/* 101:    */   }
/* 102:    */   
/* 103:    */   public Discretize getDiscretize()
/* 104:    */   {
/* 105:235 */     return this.discretize;
/* 106:    */   }
/* 107:    */   
/* 108:    */   public void setDiscretize(Discretize value)
/* 109:    */   {
/* 110:247 */     this.discretize = value;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public MapValues getMapValues()
/* 114:    */   {
/* 115:259 */     return this.mapValues;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public void setMapValues(MapValues value)
/* 119:    */   {
/* 120:271 */     this.mapValues = value;
/* 121:    */   }
/* 122:    */   
/* 123:    */   public Apply getApply()
/* 124:    */   {
/* 125:283 */     return this.apply;
/* 126:    */   }
/* 127:    */   
/* 128:    */   public void setApply(Apply value)
/* 129:    */   {
/* 130:295 */     this.apply = value;
/* 131:    */   }
/* 132:    */   
/* 133:    */   public Aggregate getAggregate()
/* 134:    */   {
/* 135:307 */     return this.aggregate;
/* 136:    */   }
/* 137:    */   
/* 138:    */   public void setAggregate(Aggregate value)
/* 139:    */   {
/* 140:319 */     this.aggregate = value;
/* 141:    */   }
/* 142:    */   
/* 143:    */   public List<Value> getValue()
/* 144:    */   {
/* 145:345 */     if (this.value == null) {
/* 146:346 */       this.value = new ArrayList();
/* 147:    */     }
/* 148:348 */     return this.value;
/* 149:    */   }
/* 150:    */   
/* 151:    */   public DATATYPE getDataType()
/* 152:    */   {
/* 153:360 */     return this.dataType;
/* 154:    */   }
/* 155:    */   
/* 156:    */   public void setDataType(DATATYPE value)
/* 157:    */   {
/* 158:372 */     this.dataType = value;
/* 159:    */   }
/* 160:    */   
/* 161:    */   public String getDisplayName()
/* 162:    */   {
/* 163:384 */     return this.displayName;
/* 164:    */   }
/* 165:    */   
/* 166:    */   public void setDisplayName(String value)
/* 167:    */   {
/* 168:396 */     this.displayName = value;
/* 169:    */   }
/* 170:    */   
/* 171:    */   public String getName()
/* 172:    */   {
/* 173:408 */     return this.name;
/* 174:    */   }
/* 175:    */   
/* 176:    */   public void setName(String value)
/* 177:    */   {
/* 178:420 */     this.name = value;
/* 179:    */   }
/* 180:    */   
/* 181:    */   public OPTYPE getOptype()
/* 182:    */   {
/* 183:432 */     return this.optype;
/* 184:    */   }
/* 185:    */   
/* 186:    */   public void setOptype(OPTYPE value)
/* 187:    */   {
/* 188:444 */     this.optype = value;
/* 189:    */   }
/* 190:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.DerivedField
 * JD-Core Version:    0.7.0.1
 */