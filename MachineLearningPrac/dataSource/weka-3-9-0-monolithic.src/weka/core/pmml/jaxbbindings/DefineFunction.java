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
/*  13:    */ @XmlType(name="", propOrder={"extension", "parameterField", "constant", "fieldRef", "normContinuous", "normDiscrete", "discretize", "mapValues", "apply", "aggregate"})
/*  14:    */ @XmlRootElement(name="DefineFunction")
/*  15:    */ public class DefineFunction
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="ParameterField", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected List<ParameterField> parameterField;
/*  21:    */   @XmlElement(name="Constant", namespace="http://www.dmg.org/PMML-4_1")
/*  22:    */   protected Constant constant;
/*  23:    */   @XmlElement(name="FieldRef", namespace="http://www.dmg.org/PMML-4_1")
/*  24:    */   protected FieldRef fieldRef;
/*  25:    */   @XmlElement(name="NormContinuous", namespace="http://www.dmg.org/PMML-4_1")
/*  26:    */   protected NormContinuous normContinuous;
/*  27:    */   @XmlElement(name="NormDiscrete", namespace="http://www.dmg.org/PMML-4_1")
/*  28:    */   protected NormDiscrete normDiscrete;
/*  29:    */   @XmlElement(name="Discretize", namespace="http://www.dmg.org/PMML-4_1")
/*  30:    */   protected Discretize discretize;
/*  31:    */   @XmlElement(name="MapValues", namespace="http://www.dmg.org/PMML-4_1")
/*  32:    */   protected MapValues mapValues;
/*  33:    */   @XmlElement(name="Apply", namespace="http://www.dmg.org/PMML-4_1")
/*  34:    */   protected Apply apply;
/*  35:    */   @XmlElement(name="Aggregate", namespace="http://www.dmg.org/PMML-4_1")
/*  36:    */   protected Aggregate aggregate;
/*  37:    */   @XmlAttribute
/*  38:    */   protected DATATYPE dataType;
/*  39:    */   @XmlAttribute(required=true)
/*  40:    */   protected String name;
/*  41:    */   @XmlAttribute(required=true)
/*  42:    */   protected OPTYPE optype;
/*  43:    */   
/*  44:    */   public List<Extension> getExtension()
/*  45:    */   {
/*  46:113 */     if (this.extension == null) {
/*  47:114 */       this.extension = new ArrayList();
/*  48:    */     }
/*  49:116 */     return this.extension;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public List<ParameterField> getParameterField()
/*  53:    */   {
/*  54:142 */     if (this.parameterField == null) {
/*  55:143 */       this.parameterField = new ArrayList();
/*  56:    */     }
/*  57:145 */     return this.parameterField;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public Constant getConstant()
/*  61:    */   {
/*  62:157 */     return this.constant;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public void setConstant(Constant value)
/*  66:    */   {
/*  67:169 */     this.constant = value;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public FieldRef getFieldRef()
/*  71:    */   {
/*  72:181 */     return this.fieldRef;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public void setFieldRef(FieldRef value)
/*  76:    */   {
/*  77:193 */     this.fieldRef = value;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public NormContinuous getNormContinuous()
/*  81:    */   {
/*  82:205 */     return this.normContinuous;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public void setNormContinuous(NormContinuous value)
/*  86:    */   {
/*  87:217 */     this.normContinuous = value;
/*  88:    */   }
/*  89:    */   
/*  90:    */   public NormDiscrete getNormDiscrete()
/*  91:    */   {
/*  92:229 */     return this.normDiscrete;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public void setNormDiscrete(NormDiscrete value)
/*  96:    */   {
/*  97:241 */     this.normDiscrete = value;
/*  98:    */   }
/*  99:    */   
/* 100:    */   public Discretize getDiscretize()
/* 101:    */   {
/* 102:253 */     return this.discretize;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public void setDiscretize(Discretize value)
/* 106:    */   {
/* 107:265 */     this.discretize = value;
/* 108:    */   }
/* 109:    */   
/* 110:    */   public MapValues getMapValues()
/* 111:    */   {
/* 112:277 */     return this.mapValues;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public void setMapValues(MapValues value)
/* 116:    */   {
/* 117:289 */     this.mapValues = value;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public Apply getApply()
/* 121:    */   {
/* 122:301 */     return this.apply;
/* 123:    */   }
/* 124:    */   
/* 125:    */   public void setApply(Apply value)
/* 126:    */   {
/* 127:313 */     this.apply = value;
/* 128:    */   }
/* 129:    */   
/* 130:    */   public Aggregate getAggregate()
/* 131:    */   {
/* 132:325 */     return this.aggregate;
/* 133:    */   }
/* 134:    */   
/* 135:    */   public void setAggregate(Aggregate value)
/* 136:    */   {
/* 137:337 */     this.aggregate = value;
/* 138:    */   }
/* 139:    */   
/* 140:    */   public DATATYPE getDataType()
/* 141:    */   {
/* 142:349 */     return this.dataType;
/* 143:    */   }
/* 144:    */   
/* 145:    */   public void setDataType(DATATYPE value)
/* 146:    */   {
/* 147:361 */     this.dataType = value;
/* 148:    */   }
/* 149:    */   
/* 150:    */   public String getName()
/* 151:    */   {
/* 152:373 */     return this.name;
/* 153:    */   }
/* 154:    */   
/* 155:    */   public void setName(String value)
/* 156:    */   {
/* 157:385 */     this.name = value;
/* 158:    */   }
/* 159:    */   
/* 160:    */   public OPTYPE getOptype()
/* 161:    */   {
/* 162:397 */     return this.optype;
/* 163:    */   }
/* 164:    */   
/* 165:    */   public void setOptype(OPTYPE value)
/* 166:    */   {
/* 167:409 */     this.optype = value;
/* 168:    */   }
/* 169:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.DefineFunction
 * JD-Core Version:    0.7.0.1
 */