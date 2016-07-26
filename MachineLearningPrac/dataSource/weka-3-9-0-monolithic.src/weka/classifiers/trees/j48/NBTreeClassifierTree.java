/*   1:    */ package weka.classifiers.trees.j48;
/*   2:    */ 
/*   3:    */ import weka.core.Capabilities;
/*   4:    */ import weka.core.Capabilities.Capability;
/*   5:    */ import weka.core.Instances;
/*   6:    */ import weka.core.RevisionUtils;
/*   7:    */ import weka.core.Utils;
/*   8:    */ 
/*   9:    */ public class NBTreeClassifierTree
/*  10:    */   extends ClassifierTree
/*  11:    */ {
/*  12:    */   private static final long serialVersionUID = -4472639447877404786L;
/*  13:    */   
/*  14:    */   public NBTreeClassifierTree(ModelSelection toSelectLocModel)
/*  15:    */   {
/*  16: 42 */     super(toSelectLocModel);
/*  17:    */   }
/*  18:    */   
/*  19:    */   public Capabilities getCapabilities()
/*  20:    */   {
/*  21: 52 */     Capabilities result = super.getCapabilities();
/*  22: 53 */     result.disableAll();
/*  23:    */     
/*  24:    */ 
/*  25: 56 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  26: 57 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  27: 58 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  28: 59 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  29:    */     
/*  30:    */ 
/*  31: 62 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  32: 63 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  33:    */     
/*  34:    */ 
/*  35: 66 */     result.setMinimumNumberInstances(0);
/*  36:    */     
/*  37: 68 */     return result;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public void buildClassifier(Instances data)
/*  41:    */     throws Exception
/*  42:    */   {
/*  43: 78 */     super.buildClassifier(data);
/*  44: 79 */     cleanup(new Instances(data, 0));
/*  45: 80 */     assignIDs(-1);
/*  46:    */   }
/*  47:    */   
/*  48:    */   protected ClassifierTree getNewTree(Instances data)
/*  49:    */     throws Exception
/*  50:    */   {
/*  51:104 */     ClassifierTree newTree = new NBTreeClassifierTree(this.m_toSelectModel);
/*  52:105 */     newTree.buildTree(data, false);
/*  53:    */     
/*  54:107 */     return newTree;
/*  55:    */   }
/*  56:    */   
/*  57:    */   protected ClassifierTree getNewTree(Instances train, Instances test)
/*  58:    */     throws Exception
/*  59:    */   {
/*  60:121 */     ClassifierTree newTree = new NBTreeClassifierTree(this.m_toSelectModel);
/*  61:122 */     newTree.buildTree(train, test, false);
/*  62:    */     
/*  63:124 */     return newTree;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public String printLeafModels()
/*  67:    */   {
/*  68:133 */     StringBuffer text = new StringBuffer();
/*  69:135 */     if (this.m_isLeaf)
/*  70:    */     {
/*  71:136 */       text.append("\nLeaf number: " + this.m_id + " ");
/*  72:137 */       text.append(this.m_localModel.toString());
/*  73:138 */       text.append("\n");
/*  74:    */     }
/*  75:    */     else
/*  76:    */     {
/*  77:140 */       for (ClassifierTree m_son : this.m_sons) {
/*  78:141 */         text.append(((NBTreeClassifierTree)m_son).printLeafModels());
/*  79:    */       }
/*  80:    */     }
/*  81:144 */     return text.toString();
/*  82:    */   }
/*  83:    */   
/*  84:    */   public String toString()
/*  85:    */   {
/*  86:    */     try
/*  87:    */     {
/*  88:154 */       StringBuffer text = new StringBuffer();
/*  89:156 */       if (this.m_isLeaf)
/*  90:    */       {
/*  91:157 */         text.append(": NB");
/*  92:158 */         text.append(this.m_id);
/*  93:    */       }
/*  94:    */       else
/*  95:    */       {
/*  96:160 */         dumpTreeNB(0, text);
/*  97:    */       }
/*  98:163 */       text.append("\n" + printLeafModels());
/*  99:164 */       text.append("\n\nNumber of Leaves  : \t" + numLeaves() + "\n");
/* 100:165 */       text.append("\nSize of the tree : \t" + numNodes() + "\n");
/* 101:    */       
/* 102:167 */       return text.toString();
/* 103:    */     }
/* 104:    */     catch (Exception e)
/* 105:    */     {
/* 106:169 */       e.printStackTrace();
/* 107:    */     }
/* 108:170 */     return "Can't print nb tree.";
/* 109:    */   }
/* 110:    */   
/* 111:    */   private void dumpTreeNB(int depth, StringBuffer text)
/* 112:    */     throws Exception
/* 113:    */   {
/* 114:183 */     for (int i = 0; i < this.m_sons.length; i++)
/* 115:    */     {
/* 116:184 */       text.append("\n");
/* 117:186 */       for (int j = 0; j < depth; j++) {
/* 118:187 */         text.append("|   ");
/* 119:    */       }
/* 120:189 */       text.append(this.m_localModel.leftSide(this.m_train));
/* 121:190 */       text.append(this.m_localModel.rightSide(i, this.m_train));
/* 122:191 */       if (this.m_sons[i].m_isLeaf)
/* 123:    */       {
/* 124:192 */         text.append(": NB ");
/* 125:193 */         text.append(this.m_sons[i].m_id);
/* 126:    */       }
/* 127:    */       else
/* 128:    */       {
/* 129:195 */         ((NBTreeClassifierTree)this.m_sons[i]).dumpTreeNB(depth + 1, text);
/* 130:    */       }
/* 131:    */     }
/* 132:    */   }
/* 133:    */   
/* 134:    */   public String graph()
/* 135:    */     throws Exception
/* 136:    */   {
/* 137:208 */     StringBuffer text = new StringBuffer();
/* 138:    */     
/* 139:210 */     text.append("digraph J48Tree {\n");
/* 140:211 */     if (this.m_isLeaf)
/* 141:    */     {
/* 142:212 */       text.append("N" + this.m_id + " [label=\"" + "NB model" + "\" " + "shape=box style=filled ");
/* 143:214 */       if ((this.m_train != null) && (this.m_train.numInstances() > 0))
/* 144:    */       {
/* 145:215 */         text.append("data =\n" + this.m_train + "\n");
/* 146:216 */         text.append(",\n");
/* 147:    */       }
/* 148:219 */       text.append("]\n");
/* 149:    */     }
/* 150:    */     else
/* 151:    */     {
/* 152:221 */       text.append("N" + this.m_id + " [label=\"" + Utils.backQuoteChars(this.m_localModel.leftSide(this.m_train)) + "\" ");
/* 153:223 */       if ((this.m_train != null) && (this.m_train.numInstances() > 0))
/* 154:    */       {
/* 155:224 */         text.append("data =\n" + this.m_train + "\n");
/* 156:225 */         text.append(",\n");
/* 157:    */       }
/* 158:227 */       text.append("]\n");
/* 159:228 */       graphTree(text);
/* 160:    */     }
/* 161:231 */     return text.toString() + "}\n";
/* 162:    */   }
/* 163:    */   
/* 164:    */   private void graphTree(StringBuffer text)
/* 165:    */     throws Exception
/* 166:    */   {
/* 167:241 */     for (int i = 0; i < this.m_sons.length; i++)
/* 168:    */     {
/* 169:242 */       text.append("N" + this.m_id + "->" + "N" + this.m_sons[i].m_id + " [label=\"" + Utils.backQuoteChars(this.m_localModel.rightSide(i, this.m_train).trim()) + "\"]\n");
/* 170:245 */       if (this.m_sons[i].m_isLeaf)
/* 171:    */       {
/* 172:246 */         text.append("N" + this.m_sons[i].m_id + " [label=\"" + "NB Model" + "\" " + "shape=box style=filled ");
/* 173:248 */         if ((this.m_train != null) && (this.m_train.numInstances() > 0))
/* 174:    */         {
/* 175:249 */           text.append("data =\n" + this.m_sons[i].m_train + "\n");
/* 176:250 */           text.append(",\n");
/* 177:    */         }
/* 178:252 */         text.append("]\n");
/* 179:    */       }
/* 180:    */       else
/* 181:    */       {
/* 182:254 */         text.append("N" + this.m_sons[i].m_id + " [label=\"" + Utils.backQuoteChars(this.m_sons[i].m_localModel.leftSide(this.m_train)) + "\" ");
/* 183:257 */         if ((this.m_train != null) && (this.m_train.numInstances() > 0))
/* 184:    */         {
/* 185:258 */           text.append("data =\n" + this.m_sons[i].m_train + "\n");
/* 186:259 */           text.append(",\n");
/* 187:    */         }
/* 188:261 */         text.append("]\n");
/* 189:262 */         ((NBTreeClassifierTree)this.m_sons[i]).graphTree(text);
/* 190:    */       }
/* 191:    */     }
/* 192:    */   }
/* 193:    */   
/* 194:    */   public String getRevision()
/* 195:    */   {
/* 196:274 */     return RevisionUtils.extract("$Revision: 11006 $");
/* 197:    */   }
/* 198:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.j48.NBTreeClassifierTree
 * JD-Core Version:    0.7.0.1
 */