/*   1:    */ package weka.core.pmml;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import org.w3c.dom.Element;
/*   6:    */ import org.w3c.dom.Node;
/*   7:    */ import org.w3c.dom.NodeList;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.SerializedObject;
/*  11:    */ 
/*  12:    */ class TransformationDictionary
/*  13:    */   implements Serializable
/*  14:    */ {
/*  15:    */   private static final long serialVersionUID = -4649092421002319829L;
/*  16: 48 */   protected ArrayList<DefineFunction> m_defineFunctions = new ArrayList();
/*  17: 51 */   protected ArrayList<DerivedFieldMetaInfo> m_derivedFields = new ArrayList();
/*  18:    */   
/*  19:    */   protected TransformationDictionary(Element dictionary, Instances dataDictionary)
/*  20:    */     throws Exception
/*  21:    */   {
/*  22: 72 */     NodeList derivedL = dictionary.getChildNodes();
/*  23: 73 */     for (int i = 0; i < derivedL.getLength(); i++)
/*  24:    */     {
/*  25: 74 */       Node child = derivedL.item(i);
/*  26: 75 */       if (child.getNodeType() == 1)
/*  27:    */       {
/*  28: 76 */         String tagName = ((Element)child).getTagName();
/*  29: 77 */         if (tagName.equals("DerivedField"))
/*  30:    */         {
/*  31: 78 */           DerivedFieldMetaInfo df = new DerivedFieldMetaInfo((Element)child, null, null);
/*  32:    */           
/*  33: 80 */           this.m_derivedFields.add(df);
/*  34:    */         }
/*  35: 81 */         else if (tagName.equals("DefineFunction"))
/*  36:    */         {
/*  37: 82 */           DefineFunction defF = new DefineFunction((Element)child, null);
/*  38: 83 */           this.m_defineFunctions.add(defF);
/*  39:    */         }
/*  40:    */       }
/*  41:    */     }
/*  42:    */   }
/*  43:    */   
/*  44:    */   protected void setFieldDefsForDerivedFields(ArrayList<Attribute> fieldDefs)
/*  45:    */     throws Exception
/*  46:    */   {
/*  47:113 */     for (int i = 0; i < this.m_derivedFields.size(); i++) {
/*  48:114 */       ((DerivedFieldMetaInfo)this.m_derivedFields.get(i)).setFieldDefs(fieldDefs);
/*  49:    */     }
/*  50:126 */     for (int i = 0; i < this.m_defineFunctions.size(); i++) {
/*  51:127 */       ((DefineFunction)this.m_defineFunctions.get(i)).pushParameterDefs();
/*  52:    */     }
/*  53:    */   }
/*  54:    */   
/*  55:    */   protected void setFieldDefsForDerivedFields(Instances fieldDefs)
/*  56:    */     throws Exception
/*  57:    */   {
/*  58:148 */     ArrayList<Attribute> tempDefs = new ArrayList();
/*  59:149 */     for (int i = 0; i < fieldDefs.numAttributes(); i++) {
/*  60:150 */       tempDefs.add(fieldDefs.attribute(i));
/*  61:    */     }
/*  62:152 */     setFieldDefsForDerivedFields(tempDefs);
/*  63:    */   }
/*  64:    */   
/*  65:    */   protected ArrayList<DerivedFieldMetaInfo> getDerivedFields()
/*  66:    */   {
/*  67:156 */     return new ArrayList(this.m_derivedFields);
/*  68:    */   }
/*  69:    */   
/*  70:    */   protected DefineFunction getFunction(String functionName)
/*  71:    */     throws Exception
/*  72:    */   {
/*  73:168 */     DefineFunction copy = null;
/*  74:169 */     DefineFunction match = null;
/*  75:170 */     for (DefineFunction f : this.m_defineFunctions) {
/*  76:171 */       if (f.getName().equals(functionName))
/*  77:    */       {
/*  78:172 */         match = f;
/*  79:    */         
/*  80:174 */         break;
/*  81:    */       }
/*  82:    */     }
/*  83:178 */     if (match != null)
/*  84:    */     {
/*  85:179 */       SerializedObject so = new SerializedObject(match, false);
/*  86:180 */       copy = (DefineFunction)so.getObject();
/*  87:    */     }
/*  88:184 */     return copy;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public String toString()
/*  92:    */   {
/*  93:189 */     StringBuffer buff = new StringBuffer();
/*  94:    */     
/*  95:191 */     buff.append("Transformation dictionary:\n");
/*  96:193 */     if (this.m_derivedFields.size() > 0)
/*  97:    */     {
/*  98:194 */       buff.append("derived fields:\n");
/*  99:195 */       for (DerivedFieldMetaInfo d : this.m_derivedFields) {
/* 100:196 */         buff.append("" + d.getFieldAsAttribute() + "\n");
/* 101:    */       }
/* 102:    */     }
/* 103:200 */     if (this.m_defineFunctions.size() > 0)
/* 104:    */     {
/* 105:201 */       buff.append("\nfunctions:\n");
/* 106:202 */       for (DefineFunction f : this.m_defineFunctions) {
/* 107:203 */         buff.append(f.toString("  ") + "\n");
/* 108:    */       }
/* 109:    */     }
/* 110:207 */     buff.append("\n");
/* 111:    */     
/* 112:209 */     return buff.toString();
/* 113:    */   }
/* 114:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.TransformationDictionary
 * JD-Core Version:    0.7.0.1
 */