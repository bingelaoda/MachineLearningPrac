/*   1:    */ package weka.core.pmml;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import org.w3c.dom.Element;
/*   6:    */ import org.w3c.dom.Node;
/*   7:    */ import org.w3c.dom.NodeList;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ 
/*  10:    */ public abstract class Expression
/*  11:    */   implements Serializable
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = 4448840549804800321L;
/*  14:    */   protected FieldMetaInfo.Optype m_opType;
/*  15: 44 */   protected ArrayList<Attribute> m_fieldDefs = null;
/*  16:    */   
/*  17:    */   public Expression(FieldMetaInfo.Optype opType, ArrayList<Attribute> fieldDefs)
/*  18:    */   {
/*  19: 49 */     this.m_opType = opType;
/*  20: 50 */     this.m_fieldDefs = fieldDefs;
/*  21:    */   }
/*  22:    */   
/*  23:    */   public void setFieldDefs(ArrayList<Attribute> fieldDefs)
/*  24:    */     throws Exception
/*  25:    */   {
/*  26: 60 */     this.m_fieldDefs = fieldDefs;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public abstract double getResult(double[] paramArrayOfDouble)
/*  30:    */     throws Exception;
/*  31:    */   
/*  32:    */   public double getResultContinuous(double[] incoming)
/*  33:    */     throws Exception
/*  34:    */   {
/*  35: 86 */     if (this.m_opType != FieldMetaInfo.Optype.CONTINUOUS) {
/*  36: 87 */       throw new Exception("[Expression] Can't return continuous result as optype is not continuous");
/*  37:    */     }
/*  38: 90 */     return getResult(incoming);
/*  39:    */   }
/*  40:    */   
/*  41:    */   public abstract String getResultCategorical(double[] paramArrayOfDouble)
/*  42:    */     throws Exception;
/*  43:    */   
/*  44:    */   protected abstract Attribute getOutputDef();
/*  45:    */   
/*  46:    */   public static Expression getExpression(Node container, FieldMetaInfo.Optype opType, ArrayList<Attribute> fieldDefs, TransformationDictionary transDict)
/*  47:    */     throws Exception
/*  48:    */   {
/*  49:140 */     Expression result = null;
/*  50:141 */     String tagName = "";
/*  51:    */     
/*  52:143 */     NodeList children = container.getChildNodes();
/*  53:144 */     if (children.getLength() == 0) {
/*  54:145 */       throw new Exception("[Expression] container has no children!");
/*  55:    */     }
/*  56:150 */     for (int i = 0; i < children.getLength(); i++)
/*  57:    */     {
/*  58:151 */       Node child = children.item(i);
/*  59:152 */       if (child.getNodeType() == 1)
/*  60:    */       {
/*  61:153 */         tagName = ((Element)child).getTagName();
/*  62:154 */         result = getExpression(tagName, child, opType, fieldDefs, transDict);
/*  63:155 */         if (result != null) {
/*  64:    */           break;
/*  65:    */         }
/*  66:    */       }
/*  67:    */     }
/*  68:161 */     return result;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public static Expression getExpression(String name, Node expression, FieldMetaInfo.Optype opType, ArrayList<Attribute> fieldDefs, TransformationDictionary transDict)
/*  72:    */     throws Exception
/*  73:    */   {
/*  74:187 */     Expression result = null;
/*  75:189 */     if (name.equals("Constant")) {
/*  76:191 */       result = new Constant((Element)expression, opType, fieldDefs);
/*  77:192 */     } else if (name.equals("FieldRef")) {
/*  78:194 */       result = new FieldRef((Element)expression, opType, fieldDefs);
/*  79:195 */     } else if (name.equals("Apply")) {
/*  80:197 */       result = new Apply((Element)expression, opType, fieldDefs, transDict);
/*  81:198 */     } else if (name.equals("NormDiscrete")) {
/*  82:199 */       result = new NormDiscrete((Element)expression, opType, fieldDefs);
/*  83:200 */     } else if (name.equals("NormContinuous")) {
/*  84:201 */       result = new NormContinuous((Element)expression, opType, fieldDefs);
/*  85:202 */     } else if (name.equals("Discretize")) {
/*  86:203 */       result = new Discretize((Element)expression, opType, fieldDefs);
/*  87:204 */     } else if ((name.equals("MapValues")) || (name.equals("Aggregate"))) {
/*  88:206 */       throw new Exception("[Expression] Unhandled Expression type " + name);
/*  89:    */     }
/*  90:208 */     return result;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public Attribute getFieldDef(String attName)
/*  94:    */   {
/*  95:218 */     Attribute returnV = null;
/*  96:219 */     for (int i = 0; i < this.m_fieldDefs.size(); i++) {
/*  97:220 */       if (((Attribute)this.m_fieldDefs.get(i)).name().equals(attName))
/*  98:    */       {
/*  99:221 */         returnV = (Attribute)this.m_fieldDefs.get(i);
/* 100:222 */         break;
/* 101:    */       }
/* 102:    */     }
/* 103:225 */     return returnV;
/* 104:    */   }
/* 105:    */   
/* 106:    */   public int getFieldDefIndex(String attName)
/* 107:    */   {
/* 108:229 */     int returnV = -1;
/* 109:230 */     for (int i = 0; i < this.m_fieldDefs.size(); i++) {
/* 110:231 */       if (((Attribute)this.m_fieldDefs.get(i)).name().equals(attName))
/* 111:    */       {
/* 112:232 */         returnV = i;
/* 113:233 */         break;
/* 114:    */       }
/* 115:    */     }
/* 116:236 */     return returnV;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public FieldMetaInfo.Optype getOptype()
/* 120:    */   {
/* 121:245 */     return this.m_opType;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public String toString()
/* 125:    */   {
/* 126:249 */     return toString("");
/* 127:    */   }
/* 128:    */   
/* 129:    */   public String toString(String pad)
/* 130:    */   {
/* 131:253 */     return pad + getClass().getName();
/* 132:    */   }
/* 133:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.Expression
 * JD-Core Version:    0.7.0.1
 */