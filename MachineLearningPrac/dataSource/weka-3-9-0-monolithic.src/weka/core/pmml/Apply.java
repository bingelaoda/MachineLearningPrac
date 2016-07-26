/*   1:    */ package weka.core.pmml;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import org.w3c.dom.Element;
/*   5:    */ import org.w3c.dom.Node;
/*   6:    */ import org.w3c.dom.NodeList;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ 
/*   9:    */ class Apply
/*  10:    */   extends Expression
/*  11:    */ {
/*  12:    */   private static final long serialVersionUID = -2790648331300695083L;
/*  13: 46 */   protected ArrayList<Expression> m_arguments = new ArrayList();
/*  14: 49 */   protected Function m_function = null;
/*  15: 52 */   protected Attribute m_outputStructure = null;
/*  16:    */   
/*  17:    */   protected Apply(Element apply, FieldMetaInfo.Optype opType, ArrayList<Attribute> fieldDefs, TransformationDictionary transDict)
/*  18:    */     throws Exception
/*  19:    */   {
/*  20: 70 */     super(opType, fieldDefs);
/*  21:    */     
/*  22: 72 */     String functionName = apply.getAttribute("function");
/*  23: 73 */     if ((functionName == null) || (functionName.length() == 0)) {
/*  24: 77 */       functionName = apply.getAttribute("name");
/*  25:    */     }
/*  26: 80 */     if ((functionName == null) || (functionName.length() == 0)) {
/*  27: 81 */       throw new Exception("[Apply] No function name specified!!");
/*  28:    */     }
/*  29: 84 */     this.m_function = Function.getFunction(functionName, transDict);
/*  30:    */     
/*  31:    */ 
/*  32: 87 */     NodeList children = apply.getChildNodes();
/*  33: 88 */     for (int i = 0; i < children.getLength(); i++)
/*  34:    */     {
/*  35: 89 */       Node child = children.item(i);
/*  36: 90 */       if (child.getNodeType() == 1)
/*  37:    */       {
/*  38: 91 */         String tagName = ((Element)child).getTagName();
/*  39: 92 */         if (!tagName.equals("Extension"))
/*  40:    */         {
/*  41: 94 */           Expression tempExpression = Expression.getExpression(tagName, child, this.m_opType, this.m_fieldDefs, transDict);
/*  42: 96 */           if (tempExpression != null) {
/*  43: 97 */             this.m_arguments.add(tempExpression);
/*  44:    */           }
/*  45:    */         }
/*  46:    */       }
/*  47:    */     }
/*  48:103 */     if (fieldDefs != null) {
/*  49:104 */       updateDefsForArgumentsAndFunction();
/*  50:    */     }
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void setFieldDefs(ArrayList<Attribute> fieldDefs)
/*  54:    */     throws Exception
/*  55:    */   {
/*  56:109 */     super.setFieldDefs(fieldDefs);
/*  57:110 */     updateDefsForArgumentsAndFunction();
/*  58:    */   }
/*  59:    */   
/*  60:    */   private void updateDefsForArgumentsAndFunction()
/*  61:    */     throws Exception
/*  62:    */   {
/*  63:114 */     for (int i = 0; i < this.m_arguments.size(); i++) {
/*  64:115 */       ((Expression)this.m_arguments.get(i)).setFieldDefs(this.m_fieldDefs);
/*  65:    */     }
/*  66:120 */     ArrayList<Attribute> functionFieldDefs = new ArrayList(this.m_arguments.size());
/*  67:121 */     for (int i = 0; i < this.m_arguments.size(); i++) {
/*  68:122 */       functionFieldDefs.add(((Expression)this.m_arguments.get(i)).getOutputDef());
/*  69:    */     }
/*  70:124 */     this.m_function.setParameterDefs(functionFieldDefs);
/*  71:125 */     this.m_outputStructure = this.m_function.getOutputDef();
/*  72:    */   }
/*  73:    */   
/*  74:    */   public double getResult(double[] incoming)
/*  75:    */     throws Exception
/*  76:    */   {
/*  77:143 */     double[] functionIncoming = new double[this.m_arguments.size()];
/*  78:145 */     for (int i = 0; i < this.m_arguments.size(); i++) {
/*  79:146 */       functionIncoming[i] = ((Expression)this.m_arguments.get(i)).getResult(incoming);
/*  80:    */     }
/*  81:150 */     double result = this.m_function.getResult(functionIncoming);
/*  82:    */     
/*  83:152 */     return result;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public String getResultCategorical(double[] incoming)
/*  87:    */     throws Exception
/*  88:    */   {
/*  89:167 */     if (this.m_opType == FieldMetaInfo.Optype.CONTINUOUS) {
/*  90:168 */       throw new IllegalArgumentException("[Apply] Can't return result as categorical/ordinal because optype is continuous!");
/*  91:    */     }
/*  92:172 */     double result = getResult(incoming);
/*  93:173 */     return this.m_outputStructure.value((int)result);
/*  94:    */   }
/*  95:    */   
/*  96:    */   public Attribute getOutputDef()
/*  97:    */   {
/*  98:184 */     if (this.m_outputStructure == null) {
/*  99:188 */       return (this.m_opType == FieldMetaInfo.Optype.CATEGORICAL) || (this.m_opType == FieldMetaInfo.Optype.ORDINAL) ? new Attribute("Placeholder", new ArrayList()) : new Attribute("Placeholder");
/* 100:    */     }
/* 101:193 */     return this.m_outputStructure;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public String toString(String pad)
/* 105:    */   {
/* 106:197 */     StringBuffer buff = new StringBuffer();
/* 107:    */     
/* 108:    */ 
/* 109:    */ 
/* 110:201 */     String[] parameterNames = null;
/* 111:    */     
/* 112:203 */     buff.append(pad + "Apply [" + this.m_function.toString() + "]:\n");
/* 113:204 */     buff.append(pad + "args:");
/* 114:205 */     if ((this.m_function instanceof DefineFunction)) {
/* 115:206 */       parameterNames = this.m_function.getParameterNames();
/* 116:    */     }
/* 117:208 */     for (int i = 0; i < this.m_arguments.size(); i++)
/* 118:    */     {
/* 119:209 */       Expression e = (Expression)this.m_arguments.get(i);
/* 120:210 */       buff.append("\n" + (parameterNames != null ? pad + parameterNames[i] + " = " : "") + e.toString(new StringBuilder().append(pad).append("  ").toString()));
/* 121:    */     }
/* 122:217 */     return buff.toString();
/* 123:    */   }
/* 124:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.Apply
 * JD-Core Version:    0.7.0.1
 */