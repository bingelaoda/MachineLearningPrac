/*   1:    */ package weka.core.pmml;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import org.w3c.dom.Element;
/*   5:    */ import org.w3c.dom.Node;
/*   6:    */ import org.w3c.dom.NodeList;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ 
/*   9:    */ public class DefineFunction
/*  10:    */   extends Function
/*  11:    */ {
/*  12:    */   private static final long serialVersionUID = -1976646917527243888L;
/*  13:    */   
/*  14:    */   protected class ParameterField
/*  15:    */     extends FieldMetaInfo
/*  16:    */   {
/*  17:    */     private static final long serialVersionUID = 3918895902507585558L;
/*  18:    */     
/*  19:    */     protected ParameterField(Element field)
/*  20:    */     {
/*  21: 55 */       super();
/*  22:    */     }
/*  23:    */     
/*  24:    */     public Attribute getFieldAsAttribute()
/*  25:    */     {
/*  26: 59 */       if (this.m_optype == FieldMetaInfo.Optype.CONTINUOUS) {
/*  27: 60 */         return new Attribute(this.m_fieldName);
/*  28:    */       }
/*  29: 63 */       return new Attribute(this.m_fieldName, (ArrayList)null);
/*  30:    */     }
/*  31:    */   }
/*  32:    */   
/*  33: 71 */   protected ArrayList<ParameterField> m_parameters = new ArrayList();
/*  34: 74 */   FieldMetaInfo.Optype m_optype = FieldMetaInfo.Optype.NONE;
/*  35: 77 */   protected Expression m_expression = null;
/*  36:    */   
/*  37:    */   public DefineFunction(Element container, TransformationDictionary transDict)
/*  38:    */     throws Exception
/*  39:    */   {
/*  40: 81 */     this.m_functionName = container.getAttribute("name");
/*  41:    */     
/*  42:    */ 
/*  43: 84 */     String opType = container.getAttribute("optype");
/*  44: 85 */     if ((opType != null) && (opType.length() > 0)) {
/*  45: 86 */       for (FieldMetaInfo.Optype o : FieldMetaInfo.Optype.values()) {
/*  46: 87 */         if (o.toString().equals(opType))
/*  47:    */         {
/*  48: 88 */           this.m_optype = o;
/*  49: 89 */           break;
/*  50:    */         }
/*  51:    */       }
/*  52:    */     } else {
/*  53: 93 */       throw new Exception("[DefineFunction] no optype specified!!");
/*  54:    */     }
/*  55: 96 */     this.m_parameterDefs = new ArrayList();
/*  56:    */     
/*  57:    */ 
/*  58: 99 */     NodeList paramL = container.getElementsByTagName("ParameterField");
/*  59:100 */     for (int i = 0; i < paramL.getLength(); i++)
/*  60:    */     {
/*  61:101 */       Node paramN = paramL.item(i);
/*  62:102 */       if (paramN.getNodeType() == 1)
/*  63:    */       {
/*  64:103 */         ParameterField newP = new ParameterField((Element)paramN);
/*  65:104 */         this.m_parameters.add(newP);
/*  66:    */         
/*  67:    */ 
/*  68:    */ 
/*  69:    */ 
/*  70:109 */         this.m_parameterDefs.add(newP.getFieldAsAttribute());
/*  71:    */       }
/*  72:    */     }
/*  73:113 */     this.m_expression = Expression.getExpression(container, this.m_optype, this.m_parameterDefs, transDict);
/*  74:116 */     if ((this.m_optype == FieldMetaInfo.Optype.CONTINUOUS) && (this.m_expression.getOptype() != this.m_optype)) {
/*  75:118 */       throw new Exception("[DefineFunction] optype is continuous but our Expression's optype is not.");
/*  76:    */     }
/*  77:122 */     if (((this.m_optype == FieldMetaInfo.Optype.CATEGORICAL) || (this.m_optype == FieldMetaInfo.Optype.ORDINAL) ? 1 : 0) != ((this.m_expression.getOptype() == FieldMetaInfo.Optype.CATEGORICAL) || (this.m_expression.getOptype() == FieldMetaInfo.Optype.ORDINAL) ? 1 : 0)) {
/*  78:125 */       throw new Exception("[DefineFunction] optype is categorical/ordinal but our Expression's optype is not.");
/*  79:    */     }
/*  80:    */   }
/*  81:    */   
/*  82:    */   public void pushParameterDefs()
/*  83:    */     throws Exception
/*  84:    */   {
/*  85:131 */     if (this.m_parameterDefs == null) {
/*  86:132 */       throw new Exception("[DefineFunction] parameter definitions are null! Can't push them to encapsulated expression.");
/*  87:    */     }
/*  88:136 */     this.m_expression.setFieldDefs(this.m_parameterDefs);
/*  89:    */   }
/*  90:    */   
/*  91:    */   public Attribute getOutputDef()
/*  92:    */   {
/*  93:145 */     return this.m_expression.getOutputDef();
/*  94:    */   }
/*  95:    */   
/*  96:    */   public String[] getParameterNames()
/*  97:    */   {
/*  98:157 */     String[] result = new String[this.m_parameters.size()];
/*  99:158 */     for (int i = 0; i < this.m_parameters.size(); i++) {
/* 100:159 */       result[i] = ((ParameterField)this.m_parameters.get(i)).getFieldName();
/* 101:    */     }
/* 102:162 */     return result;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public double getResult(double[] incoming)
/* 106:    */     throws Exception
/* 107:    */   {
/* 108:177 */     if (incoming.length != this.m_parameters.size()) {
/* 109:178 */       throw new IllegalArgumentException("[DefineFunction] wrong number of arguments: expected " + this.m_parameters.size() + ", recieved " + incoming.length);
/* 110:    */     }
/* 111:182 */     return this.m_expression.getResult(incoming);
/* 112:    */   }
/* 113:    */   
/* 114:    */   public void setParameterDefs(ArrayList<Attribute> paramDefs)
/* 115:    */     throws Exception
/* 116:    */   {
/* 117:194 */     if (paramDefs.size() != this.m_parameters.size()) {
/* 118:195 */       throw new Exception("[DefineFunction] number of parameter definitions does not match number of parameters!");
/* 119:    */     }
/* 120:200 */     for (int i = 0; i < this.m_parameters.size(); i++) {
/* 121:201 */       if (((ParameterField)this.m_parameters.get(i)).getOptype() == FieldMetaInfo.Optype.CONTINUOUS)
/* 122:    */       {
/* 123:202 */         if (!((Attribute)paramDefs.get(i)).isNumeric()) {
/* 124:203 */           throw new Exception("[DefineFunction] parameter " + ((ParameterField)this.m_parameters.get(i)).getFieldName() + " is continuous, but corresponding " + "supplied parameter def " + ((Attribute)paramDefs.get(i)).name() + " is not!");
/* 125:    */         }
/* 126:    */       }
/* 127:208 */       else if ((!((Attribute)paramDefs.get(i)).isNominal()) && (!((Attribute)paramDefs.get(i)).isString())) {
/* 128:209 */         throw new Exception("[DefineFunction] parameter " + ((ParameterField)this.m_parameters.get(i)).getFieldName() + " is categorical/ordinal, but corresponding " + "supplied parameter def " + ((Attribute)paramDefs.get(i)).name() + " is not!");
/* 129:    */       }
/* 130:    */     }
/* 131:218 */     ArrayList<Attribute> newParamDefs = new ArrayList();
/* 132:219 */     for (int i = 0; i < paramDefs.size(); i++)
/* 133:    */     {
/* 134:220 */       Attribute a = (Attribute)paramDefs.get(i);
/* 135:221 */       newParamDefs.add(a.copy(((ParameterField)this.m_parameters.get(i)).getFieldName()));
/* 136:    */     }
/* 137:224 */     this.m_parameterDefs = newParamDefs;
/* 138:    */     
/* 139:    */ 
/* 140:227 */     this.m_expression.setFieldDefs(this.m_parameterDefs);
/* 141:    */   }
/* 142:    */   
/* 143:    */   public String toString()
/* 144:    */   {
/* 145:231 */     return toString("");
/* 146:    */   }
/* 147:    */   
/* 148:    */   public String toString(String pad)
/* 149:    */   {
/* 150:235 */     StringBuffer buff = new StringBuffer();
/* 151:    */     
/* 152:237 */     buff.append(pad + "DefineFunction (" + this.m_functionName + "):\n" + pad + "nparameters:\n");
/* 153:240 */     for (ParameterField p : this.m_parameters) {
/* 154:241 */       buff.append(pad + p.getFieldAsAttribute() + "\n");
/* 155:    */     }
/* 156:244 */     buff.append(pad + "expression:\n" + this.m_expression.toString(new StringBuilder().append(pad).append("  ").toString()));
/* 157:245 */     return buff.toString();
/* 158:    */   }
/* 159:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.DefineFunction
 * JD-Core Version:    0.7.0.1
 */