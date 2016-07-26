/*   1:    */ package weka.classifiers.trees.ht;
/*   2:    */ 
/*   3:    */ import java.util.LinkedHashMap;
/*   4:    */ import java.util.Map;
/*   5:    */ import java.util.Map.Entry;
/*   6:    */ import weka.core.Instance;
/*   7:    */ 
/*   8:    */ public class SplitNode
/*   9:    */   extends HNode
/*  10:    */ {
/*  11:    */   private static final long serialVersionUID = 1558033628618451073L;
/*  12:    */   protected Split m_split;
/*  13: 47 */   protected Map<String, HNode> m_children = new LinkedHashMap();
/*  14:    */   
/*  15:    */   public SplitNode(Map<String, WeightMass> classDistrib, Split split)
/*  16:    */   {
/*  17: 56 */     super(classDistrib);
/*  18:    */     
/*  19: 58 */     this.m_split = split;
/*  20:    */   }
/*  21:    */   
/*  22:    */   public String branchForInstance(Instance inst)
/*  23:    */   {
/*  24: 68 */     return this.m_split.branchForInstance(inst);
/*  25:    */   }
/*  26:    */   
/*  27:    */   public boolean isLeaf()
/*  28:    */   {
/*  29: 73 */     return false;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public int numChildred()
/*  33:    */   {
/*  34: 82 */     return this.m_children.size();
/*  35:    */   }
/*  36:    */   
/*  37:    */   public void setChild(String branch, HNode child)
/*  38:    */   {
/*  39: 92 */     this.m_children.put(branch, child);
/*  40:    */   }
/*  41:    */   
/*  42:    */   public LeafNode leafForInstance(Instance inst, SplitNode parent, String parentBranch)
/*  43:    */   {
/*  44: 99 */     String branch = branchForInstance(inst);
/*  45:100 */     if (branch != null)
/*  46:    */     {
/*  47:101 */       HNode child = (HNode)this.m_children.get(branch);
/*  48:102 */       if (child != null) {
/*  49:103 */         return child.leafForInstance(inst, this, branch);
/*  50:    */       }
/*  51:105 */       return new LeafNode(null, this, branch);
/*  52:    */     }
/*  53:107 */     return new LeafNode(this, parent, parentBranch);
/*  54:    */   }
/*  55:    */   
/*  56:    */   public void updateNode(Instance inst) {}
/*  57:    */   
/*  58:    */   protected int dumpTree(int depth, int leafCount, StringBuffer buff)
/*  59:    */   {
/*  60:118 */     for (Map.Entry<String, HNode> e : this.m_children.entrySet())
/*  61:    */     {
/*  62:120 */       HNode child = (HNode)e.getValue();
/*  63:121 */       String branch = (String)e.getKey();
/*  64:123 */       if (child != null)
/*  65:    */       {
/*  66:125 */         buff.append("\n");
/*  67:127 */         for (int i = 0; i < depth; i++) {
/*  68:128 */           buff.append("|   ");
/*  69:    */         }
/*  70:131 */         buff.append(this.m_split.conditionForBranch(branch).trim());
/*  71:132 */         buff.append(": ");
/*  72:133 */         leafCount = child.dumpTree(depth + 1, leafCount, buff);
/*  73:    */       }
/*  74:    */     }
/*  75:136 */     return leafCount;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public int installNodeNums(int nodeNum)
/*  79:    */   {
/*  80:141 */     nodeNum = super.installNodeNums(nodeNum);
/*  81:143 */     for (Map.Entry<String, HNode> e : this.m_children.entrySet())
/*  82:    */     {
/*  83:145 */       HNode child = (HNode)e.getValue();
/*  84:147 */       if (child != null) {
/*  85:148 */         nodeNum = child.installNodeNums(nodeNum);
/*  86:    */       }
/*  87:    */     }
/*  88:152 */     return nodeNum;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public void graphTree(StringBuffer buff)
/*  92:    */   {
/*  93:157 */     boolean first = true;
/*  94:158 */     for (Map.Entry<String, HNode> e : this.m_children.entrySet())
/*  95:    */     {
/*  96:160 */       HNode child = (HNode)e.getValue();
/*  97:161 */       String branch = (String)e.getKey();
/*  98:163 */       if (child != null)
/*  99:    */       {
/* 100:164 */         String conditionForBranch = this.m_split.conditionForBranch(branch);
/* 101:165 */         if (first)
/* 102:    */         {
/* 103:166 */           String testAttName = null;
/* 104:168 */           if (conditionForBranch.indexOf("<=") < 0) {
/* 105:169 */             testAttName = conditionForBranch.substring(0, conditionForBranch.indexOf("=")).trim();
/* 106:    */           } else {
/* 107:172 */             testAttName = conditionForBranch.substring(0, conditionForBranch.indexOf("<")).trim();
/* 108:    */           }
/* 109:175 */           first = false;
/* 110:176 */           buff.append("N" + this.m_nodeNum + " [label=\"" + testAttName + "\"]\n");
/* 111:    */         }
/* 112:179 */         int startIndex = 0;
/* 113:180 */         if (conditionForBranch.indexOf("<=") > 0) {
/* 114:181 */           startIndex = conditionForBranch.indexOf("<") - 1;
/* 115:182 */         } else if (conditionForBranch.indexOf("=") > 0) {
/* 116:183 */           startIndex = conditionForBranch.indexOf("=") - 1;
/* 117:    */         } else {
/* 118:185 */           startIndex = conditionForBranch.indexOf(">") - 1;
/* 119:    */         }
/* 120:187 */         conditionForBranch = conditionForBranch.substring(startIndex, conditionForBranch.length()).trim();
/* 121:    */         
/* 122:    */ 
/* 123:190 */         buff.append("N" + this.m_nodeNum + "->" + "N" + child.m_nodeNum + "[label=\"" + conditionForBranch + "\"]\n").append("\n");
/* 124:    */       }
/* 125:    */     }
/* 126:197 */     for (Map.Entry<String, HNode> e : this.m_children.entrySet())
/* 127:    */     {
/* 128:198 */       HNode child = (HNode)e.getValue();
/* 129:200 */       if (child != null) {
/* 130:201 */         child.graphTree(buff);
/* 131:    */       }
/* 132:    */     }
/* 133:    */   }
/* 134:    */   
/* 135:    */   protected void printLeafModels(StringBuffer buff)
/* 136:    */   {
/* 137:208 */     for (Map.Entry<String, HNode> e : this.m_children.entrySet())
/* 138:    */     {
/* 139:210 */       HNode child = (HNode)e.getValue();
/* 140:212 */       if (child != null) {
/* 141:213 */         child.printLeafModels(buff);
/* 142:    */       }
/* 143:    */     }
/* 144:    */   }
/* 145:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.ht.SplitNode
 * JD-Core Version:    0.7.0.1
 */