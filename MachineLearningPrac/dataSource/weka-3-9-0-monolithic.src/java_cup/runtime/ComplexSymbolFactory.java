/*   1:    */ package java_cup.runtime;
/*   2:    */ 
/*   3:    */ import javax.xml.stream.XMLStreamException;
/*   4:    */ import javax.xml.stream.XMLStreamWriter;
/*   5:    */ 
/*   6:    */ public class ComplexSymbolFactory
/*   7:    */   implements SymbolFactory
/*   8:    */ {
/*   9:    */   public static class Location
/*  10:    */   {
/*  11: 20 */     private String unit = "unknown";
/*  12:    */     private int line;
/*  13:    */     private int column;
/*  14: 21 */     private int offset = -1;
/*  15:    */     
/*  16:    */     public Location(Location other)
/*  17:    */     {
/*  18: 27 */       this(other.unit, other.line, other.column, other.offset);
/*  19:    */     }
/*  20:    */     
/*  21:    */     public Location(String unit, int line, int column, int offset)
/*  22:    */     {
/*  23: 38 */       this(unit, line, column);
/*  24: 39 */       this.offset = offset;
/*  25:    */     }
/*  26:    */     
/*  27:    */     public Location(String unit, int line, int column)
/*  28:    */     {
/*  29: 49 */       this.unit = unit;
/*  30: 50 */       this.line = line;
/*  31: 51 */       this.column = column;
/*  32:    */     }
/*  33:    */     
/*  34:    */     public Location(int line, int column, int offset)
/*  35:    */     {
/*  36: 61 */       this(line, column);
/*  37: 62 */       this.offset = offset;
/*  38:    */     }
/*  39:    */     
/*  40:    */     public Location(int line, int column)
/*  41:    */     {
/*  42: 71 */       this.line = line;
/*  43: 72 */       this.column = column;
/*  44:    */     }
/*  45:    */     
/*  46:    */     public int getColumn()
/*  47:    */     {
/*  48: 79 */       return this.column;
/*  49:    */     }
/*  50:    */     
/*  51:    */     public int getLine()
/*  52:    */     {
/*  53: 86 */       return this.line;
/*  54:    */     }
/*  55:    */     
/*  56:    */     public void move(int linediff, int coldiff, int offsetdiff)
/*  57:    */     {
/*  58: 95 */       if (this.line >= 0) {
/*  59: 96 */         this.line += linediff;
/*  60:    */       }
/*  61: 97 */       if (this.column >= 0) {
/*  62: 98 */         this.column += coldiff;
/*  63:    */       }
/*  64: 99 */       if (this.offset >= 0) {
/*  65:100 */         this.offset += offsetdiff;
/*  66:    */       }
/*  67:    */     }
/*  68:    */     
/*  69:    */     public static Location clone(Location other)
/*  70:    */     {
/*  71:108 */       return new Location(other);
/*  72:    */     }
/*  73:    */     
/*  74:    */     public String getUnit()
/*  75:    */     {
/*  76:115 */       return this.unit;
/*  77:    */     }
/*  78:    */     
/*  79:    */     public String toString()
/*  80:    */     {
/*  81:122 */       return getUnit() + ":" + getLine() + "/" + getColumn() + "(" + this.offset + ")";
/*  82:    */     }
/*  83:    */     
/*  84:    */     public void toXML(XMLStreamWriter writer, String orientation)
/*  85:    */       throws XMLStreamException
/*  86:    */     {
/*  87:131 */       writer.writeStartElement("location");
/*  88:132 */       writer.writeAttribute("compilationunit", this.unit);
/*  89:133 */       writer.writeAttribute("orientation", orientation);
/*  90:134 */       writer.writeAttribute("linenumber", this.line + "");
/*  91:135 */       writer.writeAttribute("columnnumber", this.column + "");
/*  92:136 */       writer.writeAttribute("offset", this.offset + "");
/*  93:137 */       writer.writeEndElement();
/*  94:    */     }
/*  95:    */     
/*  96:    */     public int getOffset()
/*  97:    */     {
/*  98:144 */       return this.offset;
/*  99:    */     }
/* 100:    */   }
/* 101:    */   
/* 102:    */   public static class ComplexSymbol
/* 103:    */     extends Symbol
/* 104:    */   {
/* 105:    */     protected String name;
/* 106:    */     public ComplexSymbolFactory.Location xleft;
/* 107:    */     public ComplexSymbolFactory.Location xright;
/* 108:    */     
/* 109:    */     public ComplexSymbol(String name, int id)
/* 110:    */     {
/* 111:154 */       super();
/* 112:155 */       this.name = name;
/* 113:    */     }
/* 114:    */     
/* 115:    */     public ComplexSymbol(String name, int id, Object value)
/* 116:    */     {
/* 117:158 */       super(value);
/* 118:159 */       this.name = name;
/* 119:    */     }
/* 120:    */     
/* 121:    */     public String toString()
/* 122:    */     {
/* 123:162 */       if ((this.xleft == null) || (this.xright == null)) {
/* 124:162 */         return "Symbol: " + this.name;
/* 125:    */       }
/* 126:163 */       return "Symbol: " + this.name + " (" + this.xleft + " - " + this.xright + ")";
/* 127:    */     }
/* 128:    */     
/* 129:    */     public String getName()
/* 130:    */     {
/* 131:166 */       return this.name;
/* 132:    */     }
/* 133:    */     
/* 134:    */     public ComplexSymbol(String name, int id, int state)
/* 135:    */     {
/* 136:169 */       super(state);
/* 137:170 */       this.name = name;
/* 138:    */     }
/* 139:    */     
/* 140:    */     public ComplexSymbol(String name, int id, Symbol left, Symbol right)
/* 141:    */     {
/* 142:173 */       super(left, right);
/* 143:174 */       this.name = name;
/* 144:175 */       if (left != null) {
/* 145:175 */         this.xleft = ((ComplexSymbol)left).xleft;
/* 146:    */       }
/* 147:176 */       if (right != null) {
/* 148:176 */         this.xright = ((ComplexSymbol)right).xright;
/* 149:    */       }
/* 150:    */     }
/* 151:    */     
/* 152:    */     public ComplexSymbol(String name, int id, ComplexSymbolFactory.Location left, ComplexSymbolFactory.Location right)
/* 153:    */     {
/* 154:179 */       super(ComplexSymbolFactory.Location.access$000(left), ComplexSymbolFactory.Location.access$000(right));
/* 155:180 */       this.name = name;
/* 156:181 */       this.xleft = left;
/* 157:182 */       this.xright = right;
/* 158:    */     }
/* 159:    */     
/* 160:    */     public ComplexSymbol(String name, int id, Symbol left, Symbol right, Object value)
/* 161:    */     {
/* 162:185 */       super(left.left, right.right, value);
/* 163:186 */       this.name = name;
/* 164:187 */       if (left != null) {
/* 165:187 */         this.xleft = ((ComplexSymbol)left).xleft;
/* 166:    */       }
/* 167:188 */       if (right != null) {
/* 168:188 */         this.xright = ((ComplexSymbol)right).xright;
/* 169:    */       }
/* 170:    */     }
/* 171:    */     
/* 172:    */     public ComplexSymbol(String name, int id, Symbol left, Object value)
/* 173:    */     {
/* 174:191 */       super(left.right, left.right, value);
/* 175:192 */       this.name = name;
/* 176:193 */       if (left != null)
/* 177:    */       {
/* 178:194 */         this.xleft = ((ComplexSymbol)left).xright;
/* 179:195 */         this.xright = ((ComplexSymbol)left).xright;
/* 180:    */       }
/* 181:    */     }
/* 182:    */     
/* 183:    */     public ComplexSymbol(String name, int id, ComplexSymbolFactory.Location left, ComplexSymbolFactory.Location right, Object value)
/* 184:    */     {
/* 185:199 */       super(ComplexSymbolFactory.Location.access$000(left), ComplexSymbolFactory.Location.access$000(right), value);
/* 186:200 */       this.name = name;
/* 187:201 */       this.xleft = left;
/* 188:202 */       this.xright = right;
/* 189:    */     }
/* 190:    */     
/* 191:    */     public ComplexSymbolFactory.Location getLeft()
/* 192:    */     {
/* 193:205 */       return this.xleft;
/* 194:    */     }
/* 195:    */     
/* 196:    */     public ComplexSymbolFactory.Location getRight()
/* 197:    */     {
/* 198:208 */       return this.xright;
/* 199:    */     }
/* 200:    */   }
/* 201:    */   
/* 202:    */   public Symbol newSymbol(String name, int id, Location left, Location right, Object value)
/* 203:    */   {
/* 204:220 */     return new ComplexSymbol(name, id, left, right, value);
/* 205:    */   }
/* 206:    */   
/* 207:    */   public Symbol newSymbol(String name, int id, Location left, Location right)
/* 208:    */   {
/* 209:228 */     return new ComplexSymbol(name, id, left, right);
/* 210:    */   }
/* 211:    */   
/* 212:    */   public Symbol newSymbol(String name, int id, Symbol left, Object value)
/* 213:    */   {
/* 214:231 */     return new ComplexSymbol(name, id, left, value);
/* 215:    */   }
/* 216:    */   
/* 217:    */   public Symbol newSymbol(String name, int id, Symbol left, Symbol right, Object value)
/* 218:    */   {
/* 219:234 */     return new ComplexSymbol(name, id, left, right, value);
/* 220:    */   }
/* 221:    */   
/* 222:    */   public Symbol newSymbol(String name, int id, Symbol left, Symbol right)
/* 223:    */   {
/* 224:237 */     return new ComplexSymbol(name, id, left, right);
/* 225:    */   }
/* 226:    */   
/* 227:    */   public Symbol newSymbol(String name, int id)
/* 228:    */   {
/* 229:240 */     return new ComplexSymbol(name, id);
/* 230:    */   }
/* 231:    */   
/* 232:    */   public Symbol newSymbol(String name, int id, Object value)
/* 233:    */   {
/* 234:243 */     return new ComplexSymbol(name, id, value);
/* 235:    */   }
/* 236:    */   
/* 237:    */   public Symbol startSymbol(String name, int id, int state)
/* 238:    */   {
/* 239:246 */     return new ComplexSymbol(name, id, state);
/* 240:    */   }
/* 241:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     java_cup.runtime.ComplexSymbolFactory
 * JD-Core Version:    0.7.0.1
 */