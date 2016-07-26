/*   1:    */ package weka.classifiers.trees.ht;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.LinkedHashMap;
/*   5:    */ import java.util.Map;
/*   6:    */ import java.util.Map.Entry;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Utils;
/*  10:    */ 
/*  11:    */ public abstract class HNode
/*  12:    */   implements Serializable
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = 197233928177240264L;
/*  15: 46 */   public Map<String, WeightMass> m_classDistribution = new LinkedHashMap();
/*  16:    */   protected int m_leafNum;
/*  17:    */   protected int m_nodeNum;
/*  18:    */   
/*  19:    */   public HNode() {}
/*  20:    */   
/*  21:    */   public HNode(Map<String, WeightMass> classDistrib)
/*  22:    */   {
/*  23: 66 */     this.m_classDistribution = classDistrib;
/*  24:    */   }
/*  25:    */   
/*  26:    */   public boolean isLeaf()
/*  27:    */   {
/*  28: 75 */     return true;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public int numEntriesInClassDistribution()
/*  32:    */   {
/*  33: 84 */     return this.m_classDistribution.size();
/*  34:    */   }
/*  35:    */   
/*  36:    */   public boolean classDistributionIsPure()
/*  37:    */   {
/*  38: 93 */     int count = 0;
/*  39: 94 */     for (Map.Entry<String, WeightMass> el : this.m_classDistribution.entrySet()) {
/*  40: 95 */       if (((WeightMass)el.getValue()).m_weight > 0.0D)
/*  41:    */       {
/*  42: 96 */         count++;
/*  43: 98 */         if (count > 1) {
/*  44:    */           break;
/*  45:    */         }
/*  46:    */       }
/*  47:    */     }
/*  48:104 */     return count < 2;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void updateDistribution(Instance inst)
/*  52:    */   {
/*  53:113 */     if (inst.classIsMissing()) {
/*  54:114 */       return;
/*  55:    */     }
/*  56:116 */     String classVal = inst.stringValue(inst.classAttribute());
/*  57:    */     
/*  58:118 */     WeightMass m = (WeightMass)this.m_classDistribution.get(classVal);
/*  59:119 */     if (m == null)
/*  60:    */     {
/*  61:120 */       m = new WeightMass();
/*  62:121 */       m.m_weight = 1.0D;
/*  63:    */       
/*  64:123 */       this.m_classDistribution.put(classVal, m);
/*  65:    */     }
/*  66:125 */     m.m_weight += inst.weight();
/*  67:    */   }
/*  68:    */   
/*  69:    */   public double[] getDistribution(Instance inst, Attribute classAtt)
/*  70:    */     throws Exception
/*  71:    */   {
/*  72:139 */     double[] dist = new double[classAtt.numValues()];
/*  73:141 */     for (int i = 0; i < classAtt.numValues(); i++)
/*  74:    */     {
/*  75:142 */       WeightMass w = (WeightMass)this.m_classDistribution.get(classAtt.value(i));
/*  76:143 */       if (w != null) {
/*  77:144 */         dist[i] = w.m_weight;
/*  78:    */       } else {
/*  79:146 */         dist[i] = 1.0D;
/*  80:    */       }
/*  81:    */     }
/*  82:150 */     Utils.normalize(dist);
/*  83:151 */     return dist;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public int installNodeNums(int nodeNum)
/*  87:    */   {
/*  88:155 */     nodeNum++;
/*  89:156 */     this.m_nodeNum = nodeNum;
/*  90:    */     
/*  91:158 */     return nodeNum;
/*  92:    */   }
/*  93:    */   
/*  94:    */   protected int dumpTree(int depth, int leafCount, StringBuffer buff)
/*  95:    */   {
/*  96:163 */     double max = -1.0D;
/*  97:164 */     String classVal = "";
/*  98:165 */     for (Map.Entry<String, WeightMass> e : this.m_classDistribution.entrySet()) {
/*  99:166 */       if (((WeightMass)e.getValue()).m_weight > max)
/* 100:    */       {
/* 101:167 */         max = ((WeightMass)e.getValue()).m_weight;
/* 102:168 */         classVal = (String)e.getKey();
/* 103:    */       }
/* 104:    */     }
/* 105:171 */     buff.append(classVal + " (" + String.format("%-9.3f", new Object[] { Double.valueOf(max) }).trim() + ")");
/* 106:172 */     leafCount++;
/* 107:173 */     this.m_leafNum = leafCount;
/* 108:    */     
/* 109:175 */     return leafCount;
/* 110:    */   }
/* 111:    */   
/* 112:    */   protected void printLeafModels(StringBuffer buff) {}
/* 113:    */   
/* 114:    */   public void graphTree(StringBuffer text)
/* 115:    */   {
/* 116:183 */     double max = -1.0D;
/* 117:184 */     String classVal = "";
/* 118:185 */     for (Map.Entry<String, WeightMass> e : this.m_classDistribution.entrySet()) {
/* 119:186 */       if (((WeightMass)e.getValue()).m_weight > max)
/* 120:    */       {
/* 121:187 */         max = ((WeightMass)e.getValue()).m_weight;
/* 122:188 */         classVal = (String)e.getKey();
/* 123:    */       }
/* 124:    */     }
/* 125:192 */     text.append("N" + this.m_nodeNum + " [label=\"" + classVal + " (" + String.format("%-9.3f", new Object[] { Double.valueOf(max) }).trim() + ")\" shape=box style=filled]\n");
/* 126:    */   }
/* 127:    */   
/* 128:    */   public String toString(boolean printLeaf)
/* 129:    */   {
/* 130:204 */     installNodeNums(0);
/* 131:    */     
/* 132:206 */     StringBuffer buff = new StringBuffer();
/* 133:    */     
/* 134:208 */     dumpTree(0, 0, buff);
/* 135:210 */     if (printLeaf)
/* 136:    */     {
/* 137:211 */       buff.append("\n\n");
/* 138:212 */       printLeafModels(buff);
/* 139:    */     }
/* 140:215 */     return buff.toString();
/* 141:    */   }
/* 142:    */   
/* 143:    */   public double totalWeight()
/* 144:    */   {
/* 145:224 */     double tw = 0.0D;
/* 146:226 */     for (Map.Entry<String, WeightMass> e : this.m_classDistribution.entrySet()) {
/* 147:227 */       tw += ((WeightMass)e.getValue()).m_weight;
/* 148:    */     }
/* 149:230 */     return tw;
/* 150:    */   }
/* 151:    */   
/* 152:    */   public LeafNode leafForInstance(Instance inst, SplitNode parent, String parentBranch)
/* 153:    */   {
/* 154:243 */     return new LeafNode(this, parent, parentBranch);
/* 155:    */   }
/* 156:    */   
/* 157:    */   public abstract void updateNode(Instance paramInstance)
/* 158:    */     throws Exception;
/* 159:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.ht.HNode
 * JD-Core Version:    0.7.0.1
 */