/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Hashtable;
/*   7:    */ import java.util.Vector;
/*   8:    */ 
/*   9:    */ public class TechnicalInformation
/*  10:    */   implements RevisionHandler
/*  11:    */ {
/*  12:    */   protected static final String MISSING_ID = "missing_id";
/*  13:    */   
/*  14:    */   public static enum Type
/*  15:    */   {
/*  16: 50 */     ARTICLE("article", "An article from a journal or magazine."),  BOOK("book", "A book with an explicit publisher."),  BOOKLET("booklet", "A work that is printed and bound, but without a named publisher or sponsoring institution."),  CONFERENCE("conference", "The same as inproceedings."),  INBOOK("inbook", "A part of a book, which may be a chapter (or section or whatever) and/or a range of pages."),  INCOLLECTION("incollection", "A part of a book having its own title."),  INPROCEEDINGS("inproceedings", "An article in a conference proceedings."),  MANUAL("manual", "Technical documentation."),  MASTERSTHESIS("mastersthesis", "A Master's thesis."),  MISC("misc", "Use this type when nothing else fits."),  PHDTHESIS("phdthesis", "A PhD thesis."),  PROCEEDINGS("proceedings", "The proceedings of a conference."),  TECHREPORT("techreport", "A report published by a school or other institution, usually numbered within a series."),  UNPUBLISHED("unpublished", "A document having an author and title, but not formally published.");
/*  17:    */     
/*  18:    */     protected String m_Display;
/*  19:    */     protected String m_Comment;
/*  20:    */     
/*  21:    */     private Type(String display, String comment)
/*  22:    */     {
/*  23:107 */       this.m_Display = display;
/*  24:108 */       this.m_Comment = comment;
/*  25:    */     }
/*  26:    */     
/*  27:    */     public String getDisplay()
/*  28:    */     {
/*  29:117 */       return this.m_Display;
/*  30:    */     }
/*  31:    */     
/*  32:    */     public String getComment()
/*  33:    */     {
/*  34:126 */       return this.m_Comment;
/*  35:    */     }
/*  36:    */     
/*  37:    */     public String toString()
/*  38:    */     {
/*  39:136 */       return this.m_Display;
/*  40:    */     }
/*  41:    */   }
/*  42:    */   
/*  43:    */   public static enum Field
/*  44:    */   {
/*  45:148 */     ADDRESS("address", "Usually the address of the publisher or other type of institution. For major publishing houses, van Leunen recommends omitting the information entirely. For small publishers, on the other hand, you can help the reader by giving the complete address."),  ANNOTE("annote", "An annotation. It is not used by the standard bibliography styles, but may be used by others that produce an annotated bibliography."),  AUTHOR("author", "The name(s) of the author(s), in the format described in the LaTeX book."),  BOOKTITLE("booktitle", "Title of a book, part of which is being cited. See the LaTeX book for how to type titles. For book entries, use the title field instead."),  CHAPTER("chapter", "A chapter (or section or whatever) number."),  CROSSREF("crossref", "The database key of the entry being cross referenced. Any fields that are missing from the current record are inherited from the field being cross referenced."),  EDITION("edition", "The edition of a book---for example, ``Second''. This should be an ordinal, and should have the first letter capitalized, as shown here; the standard styles convert to lower case when necessary."),  EDITOR("editor", "Name(s) of editor(s), typed as indicated in the LaTeX book. If there is also an author field, then the editor field gives the editor of the book or collection in which the reference appears."),  HOWPUBLISHED("howpublished", "How something strange has been published. The first word should be capitalized."),  INSTITUTION("institution", "The sponsoring institution of a technical report."),  JOURNAL("journal", "A journal name. Abbreviations are provided for many journals."),  KEY("key", "Used for alphabetizing, cross referencing, and creating a label when the ``author'' information is missing. This field should not be confused with the key that appears in the cite command and at the beginning of the database entry."),  MONTH("month", "The month in which the work was published or, for an unpublished work, in which it was written. You should use the standard three-letter abbreviation, as described in Appendix B.1.3 of the LaTeX book."),  NOTE("note", "Any additional information that can help the reader. The first word should be capitalized."),  NUMBER("number", "The number of a journal, magazine, technical report, or of a work in a series. An issue of a journal or magazine is usually identified by its volume and number; the organization that issues a technical report usually gives it a number; and sometimes books are given numbers in a named series."),  ORGANIZATION("organization", "The organization that sponsors a conference or that publishes a manual."),  PAGES("pages", "One or more page numbers or range of numbers, such as 42--111 or 7,41,73--97 or 43+ (the `+' in this last example indicates pages following that don't form a simple range). To make it easier to maintain Scribe-compatible databases, the standard styles convert a single dash (as in 7-33) to the double dash used in TeX to denote number ranges (as in 7--33)."),  PUBLISHER("publisher", "The publisher's name."),  SCHOOL("school", "The name of the school where a thesis was written."),  SERIES("series", "The name of a series or set of books. When citing an entire book, the the title field gives its title and an optional series field gives the name of a series or multi-volume set in which the book is published."),  TITLE("title", "The work's title, typed as explained in the LaTeX book."),  TYPE("type", "The type of a technical report---for example, ``Research Note''."),  VOLUME("volume", "The volume of a journal or multi-volume book."),  YEAR("year", "The year of publication or, for an unpublished work, the year it was written. Generally it should consist of four numerals, such as 1984, although the standard styles can handle any year whose last four nonpunctuation characters are numerals, such as `\\hbox{(about 1984)}'."),  AFFILIATION("affiliation", "The authors affiliation."),  ABSTRACT("abstract", "An abstract of the work."),  CONTENTS("contents", "A Table of Contents "),  COPYRIGHT("copyright", "Copyright information."),  ISBN("ISBN", "The International Standard Book Number (10 digits)."),  ISBN13("ISBN-13", "The International Standard Book Number (13 digits)."),  ISSN("ISSN", "The International Standard Serial Number. Used to identify a journal."),  KEYWORDS("keywords", "Key words used for searching or possibly for annotation."),  LANGUAGE("language", "The language the document is in."),  LOCATION("location", "A location associated with the entry, such as the city in which a conference took place."),  LCCN("LCCN", "The Library of Congress Call Number. I've also seen this as lib-congress."),  MRNUMBER("mrnumber", "The Mathematical Reviews number."),  PRICE("price", "The price of the document."),  SIZE("size", "The physical dimensions of a work."),  URL("URL", "The WWW Universal Resource Locator that points to the item being referenced. This often is used for technical reports to point to the ftp site where the postscript source of the report is located."),  PS("PS", "A link to a postscript file."),  PDF("PDF", "A link to a PDF file."),  HTTP("HTTP", "A hyperlink to a resource.");
/*  46:    */     
/*  47:    */     protected String m_Display;
/*  48:    */     protected String m_Comment;
/*  49:    */     
/*  50:    */     private Field(String display, String comment)
/*  51:    */     {
/*  52:351 */       this.m_Display = display;
/*  53:352 */       this.m_Comment = comment;
/*  54:    */     }
/*  55:    */     
/*  56:    */     public String getDisplay()
/*  57:    */     {
/*  58:361 */       return this.m_Display;
/*  59:    */     }
/*  60:    */     
/*  61:    */     public String getComment()
/*  62:    */     {
/*  63:370 */       return this.m_Comment;
/*  64:    */     }
/*  65:    */     
/*  66:    */     public String toString()
/*  67:    */     {
/*  68:380 */       return this.m_Display;
/*  69:    */     }
/*  70:    */   }
/*  71:    */   
/*  72:388 */   protected Type m_Type = null;
/*  73:394 */   protected String m_ID = "";
/*  74:397 */   protected Hashtable<Field, String> m_Values = new Hashtable();
/*  75:400 */   protected Vector<TechnicalInformation> m_Additional = new Vector();
/*  76:    */   
/*  77:    */   public TechnicalInformation(Type type)
/*  78:    */   {
/*  79:409 */     this(type, "");
/*  80:    */   }
/*  81:    */   
/*  82:    */   public TechnicalInformation(Type type, String id)
/*  83:    */   {
/*  84:420 */     this.m_Type = type;
/*  85:421 */     this.m_ID = id;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public Type getType()
/*  89:    */   {
/*  90:430 */     return this.m_Type;
/*  91:    */   }
/*  92:    */   
/*  93:    */   protected String[] getAuthors()
/*  94:    */   {
/*  95:439 */     return getValue(Field.AUTHOR).split(" and ");
/*  96:    */   }
/*  97:    */   
/*  98:    */   protected String generateID()
/*  99:    */   {
/* 100:454 */     String result = this.m_ID;
/* 101:457 */     if ((result.length() == 0) && 
/* 102:458 */       (exists(Field.AUTHOR)) && (exists(Field.YEAR)))
/* 103:    */     {
/* 104:459 */       String[] authors = getAuthors();
/* 105:460 */       if (authors[0].indexOf(",") > -1)
/* 106:    */       {
/* 107:461 */         String[] parts = authors[0].split(",");
/* 108:462 */         result = parts[0];
/* 109:    */       }
/* 110:    */       else
/* 111:    */       {
/* 112:464 */         String[] parts = authors[0].split(" ");
/* 113:465 */         if (parts.length == 1) {
/* 114:466 */           result = parts[0];
/* 115:    */         } else {
/* 116:468 */           result = parts[(parts.length - 1)];
/* 117:    */         }
/* 118:    */       }
/* 119:471 */       result = result + getValue(Field.YEAR);
/* 120:472 */       result = result.replaceAll(" ", "");
/* 121:    */     }
/* 122:477 */     if (result.length() == 0) {
/* 123:478 */       result = "missing_id";
/* 124:    */     }
/* 125:481 */     return result;
/* 126:    */   }
/* 127:    */   
/* 128:    */   public String getID()
/* 129:    */   {
/* 130:491 */     return generateID();
/* 131:    */   }
/* 132:    */   
/* 133:    */   public void setValue(Field field, String value)
/* 134:    */   {
/* 135:501 */     this.m_Values.put(field, value);
/* 136:    */   }
/* 137:    */   
/* 138:    */   public String getValue(Field field)
/* 139:    */   {
/* 140:512 */     if (this.m_Values.containsKey(field)) {
/* 141:513 */       return (String)this.m_Values.get(field);
/* 142:    */     }
/* 143:515 */     return "";
/* 144:    */   }
/* 145:    */   
/* 146:    */   public boolean exists(Field field)
/* 147:    */   {
/* 148:527 */     return (this.m_Values.containsKey(field)) && (((String)this.m_Values.get(field)).length() != 0);
/* 149:    */   }
/* 150:    */   
/* 151:    */   public Enumeration<Field> fields()
/* 152:    */   {
/* 153:536 */     return this.m_Values.keys();
/* 154:    */   }
/* 155:    */   
/* 156:    */   public boolean hasAdditional()
/* 157:    */   {
/* 158:545 */     return this.m_Additional.size() > 0;
/* 159:    */   }
/* 160:    */   
/* 161:    */   public Enumeration<TechnicalInformation> additional()
/* 162:    */   {
/* 163:555 */     return this.m_Additional.elements();
/* 164:    */   }
/* 165:    */   
/* 166:    */   public void add(TechnicalInformation value)
/* 167:    */   {
/* 168:564 */     if (value == this) {
/* 169:565 */       throw new IllegalArgumentException("Can't add object to itself!");
/* 170:    */     }
/* 171:567 */     this.m_Additional.add(value);
/* 172:    */   }
/* 173:    */   
/* 174:    */   public TechnicalInformation add(Type type)
/* 175:    */   {
/* 176:580 */     TechnicalInformation result = new TechnicalInformation(type);
/* 177:581 */     add(result);
/* 178:    */     
/* 179:583 */     return result;
/* 180:    */   }
/* 181:    */   
/* 182:    */   public String toString()
/* 183:    */   {
/* 184:600 */     String result = "";
/* 185:601 */     String[] authors = getAuthors();
/* 186:604 */     if (getType() == Type.BOOK)
/* 187:    */     {
/* 188:605 */       for (int i = 0; i < authors.length; i++)
/* 189:    */       {
/* 190:606 */         if (i > 0) {
/* 191:607 */           result = result + ", ";
/* 192:    */         }
/* 193:609 */         result = result + authors[i];
/* 194:    */       }
/* 195:611 */       if (exists(Field.YEAR)) {
/* 196:612 */         result = result + " (" + getValue(Field.YEAR) + ").";
/* 197:    */       } else {
/* 198:614 */         result = result + ".";
/* 199:    */       }
/* 200:616 */       result = result + " " + getValue(Field.TITLE) + ".";
/* 201:617 */       result = result + " " + getValue(Field.PUBLISHER);
/* 202:618 */       if (exists(Field.ADDRESS)) {
/* 203:619 */         result = result + ", " + getValue(Field.ADDRESS);
/* 204:    */       }
/* 205:621 */       result = result + ".";
/* 206:    */     }
/* 207:624 */     else if (getType() == Type.ARTICLE)
/* 208:    */     {
/* 209:625 */       for (int i = 0; i < authors.length; i++)
/* 210:    */       {
/* 211:626 */         if (i > 0) {
/* 212:627 */           result = result + ", ";
/* 213:    */         }
/* 214:629 */         result = result + authors[i];
/* 215:    */       }
/* 216:631 */       if (exists(Field.YEAR)) {
/* 217:632 */         result = result + " (" + getValue(Field.YEAR) + ").";
/* 218:    */       } else {
/* 219:634 */         result = result + ".";
/* 220:    */       }
/* 221:636 */       result = result + " " + getValue(Field.TITLE) + ".";
/* 222:639 */       if (exists(Field.JOURNAL))
/* 223:    */       {
/* 224:640 */         result = result + " " + getValue(Field.JOURNAL) + ".";
/* 225:642 */         if (exists(Field.VOLUME)) {
/* 226:643 */           result = result + " " + getValue(Field.VOLUME);
/* 227:    */         }
/* 228:645 */         if (exists(Field.NUMBER)) {
/* 229:646 */           result = result + "(" + getValue(Field.NUMBER) + ")";
/* 230:    */         }
/* 231:648 */         if (exists(Field.PAGES)) {
/* 232:649 */           result = result + ":" + getValue(Field.PAGES);
/* 233:    */         }
/* 234:652 */         result = result + ".";
/* 235:    */       }
/* 236:658 */       if (exists(Field.URL)) {
/* 237:659 */         result = result + " URL " + getValue(Field.URL) + ".";
/* 238:    */       }
/* 239:    */     }
/* 240:663 */     else if ((getType() == Type.CONFERENCE) || (getType() == Type.INPROCEEDINGS))
/* 241:    */     {
/* 242:665 */       for (int i = 0; i < authors.length; i++)
/* 243:    */       {
/* 244:666 */         if (i > 0) {
/* 245:667 */           result = result + ", ";
/* 246:    */         }
/* 247:669 */         result = result + authors[i];
/* 248:    */       }
/* 249:671 */       result = result + ": " + getValue(Field.TITLE) + ".";
/* 250:672 */       result = result + " In: " + getValue(Field.BOOKTITLE);
/* 251:674 */       if (exists(Field.ADDRESS)) {
/* 252:675 */         result = result + ", " + getValue(Field.ADDRESS);
/* 253:    */       }
/* 254:677 */       if (exists(Field.PAGES)) {
/* 255:678 */         result = result + ", " + getValue(Field.PAGES);
/* 256:    */       }
/* 257:681 */       if (exists(Field.YEAR)) {
/* 258:682 */         result = result + ", " + getValue(Field.YEAR) + ".";
/* 259:    */       } else {
/* 260:684 */         result = result + ".";
/* 261:    */       }
/* 262:    */     }
/* 263:688 */     else if (getType() == Type.INCOLLECTION)
/* 264:    */     {
/* 265:689 */       for (int i = 0; i < authors.length; i++)
/* 266:    */       {
/* 267:690 */         if (i > 0) {
/* 268:691 */           result = result + ", ";
/* 269:    */         }
/* 270:693 */         result = result + authors[i];
/* 271:    */       }
/* 272:695 */       result = result + ": " + getValue(Field.TITLE) + ".";
/* 273:696 */       result = result + " In ";
/* 274:697 */       if (exists(Field.EDITOR)) {
/* 275:698 */         result = result + getValue(Field.EDITOR) + ", editors, ";
/* 276:    */       }
/* 277:700 */       result = result + getValue(Field.BOOKTITLE);
/* 278:702 */       if (exists(Field.ADDRESS)) {
/* 279:703 */         result = result + ", " + getValue(Field.ADDRESS);
/* 280:    */       }
/* 281:705 */       if (exists(Field.PAGES)) {
/* 282:706 */         result = result + ", " + getValue(Field.PAGES);
/* 283:    */       }
/* 284:709 */       if (exists(Field.YEAR)) {
/* 285:710 */         result = result + ", " + getValue(Field.YEAR) + ".";
/* 286:    */       } else {
/* 287:712 */         result = result + ".";
/* 288:    */       }
/* 289:    */     }
/* 290:    */     else
/* 291:    */     {
/* 292:717 */       for (int i = 0; i < authors.length; i++)
/* 293:    */       {
/* 294:718 */         if (i > 0) {
/* 295:719 */           result = result + ", ";
/* 296:    */         }
/* 297:721 */         result = result + authors[i];
/* 298:    */       }
/* 299:723 */       if (exists(Field.YEAR)) {
/* 300:724 */         result = result + " (" + getValue(Field.YEAR) + ").";
/* 301:    */       } else {
/* 302:726 */         result = result + ".";
/* 303:    */       }
/* 304:728 */       result = result + " " + getValue(Field.TITLE) + ".";
/* 305:729 */       if (exists(Field.ADDRESS)) {
/* 306:730 */         result = result + " " + getValue(Field.ADDRESS) + ".";
/* 307:    */       }
/* 308:732 */       if (exists(Field.URL)) {
/* 309:733 */         result = result + " URL " + getValue(Field.URL) + ".";
/* 310:    */       }
/* 311:    */     }
/* 312:738 */     Enumeration<TechnicalInformation> enm = additional();
/* 313:739 */     while (enm.hasMoreElements()) {
/* 314:740 */       result = result + "\n\n" + ((TechnicalInformation)enm.nextElement()).toString();
/* 315:    */     }
/* 316:743 */     return result;
/* 317:    */   }
/* 318:    */   
/* 319:    */   public String toBibTex()
/* 320:    */   {
/* 321:760 */     String result = "@" + getType() + "{" + getID() + "";
/* 322:    */     
/* 323:    */ 
/* 324:763 */     Vector<Field> list = new Vector();
/* 325:764 */     Enumeration<Field> enm = fields();
/* 326:765 */     while (enm.hasMoreElements()) {
/* 327:766 */       list.add(enm.nextElement());
/* 328:    */     }
/* 329:768 */     Collections.sort(list);
/* 330:771 */     for (int i = 0; i < list.size(); i++)
/* 331:    */     {
/* 332:772 */       Field field = (Field)list.get(i);
/* 333:773 */       if (exists(field))
/* 334:    */       {
/* 335:776 */         String value = getValue(field);
/* 336:777 */         value = value.replaceAll("\\~", "\\\\~");
/* 337:778 */         result = result + ",\n   " + field + " = {" + value + "}";
/* 338:    */       }
/* 339:    */     }
/* 340:781 */     result = result + "\n}";
/* 341:    */     
/* 342:    */ 
/* 343:784 */     Enumeration<TechnicalInformation> enm2 = additional();
/* 344:785 */     while (enm2.hasMoreElements()) {
/* 345:786 */       result = result + "\n\n" + ((TechnicalInformation)enm2.nextElement()).toBibTex();
/* 346:    */     }
/* 347:789 */     return result;
/* 348:    */   }
/* 349:    */   
/* 350:    */   public String getRevision()
/* 351:    */   {
/* 352:799 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 353:    */   }
/* 354:    */   
/* 355:    */   public static void main(String[] args)
/* 356:    */     throws Exception
/* 357:    */   {
/* 358:835 */     if (args.length != 0)
/* 359:    */     {
/* 360:836 */       TechnicalInformation info = null;
/* 361:    */       
/* 362:838 */       String tmpStr = Utils.getOption('W', args);
/* 363:839 */       if (tmpStr.length() != 0)
/* 364:    */       {
/* 365:840 */         Class<?> cls = Class.forName(tmpStr);
/* 366:841 */         TechnicalInformationHandler handler = (TechnicalInformationHandler)cls.newInstance();
/* 367:842 */         info = handler.getTechnicalInformation();
/* 368:    */       }
/* 369:    */       else
/* 370:    */       {
/* 371:844 */         throw new IllegalArgumentException("A classname has to be provided with the -W option!");
/* 372:    */       }
/* 373:    */       TechnicalInformationHandler handler;
/* 374:    */       Class<?> cls;
/* 375:848 */       if (Utils.getFlag("bibtex", args)) {
/* 376:849 */         System.out.println("\n" + handler.getClass().getName() + ":\n" + info.toBibTex());
/* 377:    */       }
/* 378:853 */       if (Utils.getFlag("plaintext", args)) {
/* 379:854 */         System.out.println("\n" + handler.getClass().getName() + ":\n" + info.toString());
/* 380:    */       }
/* 381:    */     }
/* 382:    */     else
/* 383:    */     {
/* 384:859 */       TechnicalInformation info = new TechnicalInformation(Type.BOOK);
/* 385:860 */       info.setValue(Field.AUTHOR, "Ross Quinlan");
/* 386:861 */       info.setValue(Field.YEAR, "1993");
/* 387:862 */       info.setValue(Field.TITLE, "C4.5: Programs for Machine Learning");
/* 388:863 */       info.setValue(Field.PUBLISHER, "Morgan Kaufmann Publishers");
/* 389:864 */       info.setValue(Field.ADDRESS, "San Mateo, CA");
/* 390:865 */       TechnicalInformation additional = info;
/* 391:    */       
/* 392:867 */       System.out.println("\ntoString():\n" + info.toString());
/* 393:868 */       System.out.println("\ntoBibTex():\n" + info.toBibTex());
/* 394:    */       
/* 395:    */ 
/* 396:871 */       info = new TechnicalInformation(Type.INPROCEEDINGS);
/* 397:872 */       info.setValue(Field.AUTHOR, "Freund, Y. and Mason, L.");
/* 398:873 */       info.setValue(Field.YEAR, "1999");
/* 399:874 */       info.setValue(Field.TITLE, "The alternating decision tree learning algorithm");
/* 400:    */       
/* 401:876 */       info.setValue(Field.BOOKTITLE, "Proceeding of the Sixteenth International Conference on Machine Learning");
/* 402:    */       
/* 403:    */ 
/* 404:879 */       info.setValue(Field.ADDRESS, "Bled, Slovenia");
/* 405:880 */       info.setValue(Field.PAGES, "124-133");
/* 406:    */       
/* 407:882 */       System.out.println("\ntoString():\n" + info.toString());
/* 408:883 */       System.out.println("\ntoBibTex():\n" + info.toBibTex());
/* 409:    */       
/* 410:    */ 
/* 411:886 */       info = new TechnicalInformation(Type.ARTICLE);
/* 412:887 */       info.setValue(Field.AUTHOR, "R. Quinlan");
/* 413:888 */       info.setValue(Field.YEAR, "1986");
/* 414:889 */       info.setValue(Field.TITLE, "Induction of decision trees");
/* 415:890 */       info.setValue(Field.JOURNAL, "Machine Learning");
/* 416:891 */       info.setValue(Field.VOLUME, "1");
/* 417:892 */       info.setValue(Field.NUMBER, "1");
/* 418:893 */       info.setValue(Field.PAGES, "81-106");
/* 419:    */       
/* 420:895 */       additional = new TechnicalInformation(Type.BOOK);
/* 421:896 */       additional.setValue(Field.AUTHOR, "Ross Quinlan");
/* 422:897 */       additional.setValue(Field.YEAR, "1993");
/* 423:898 */       additional.setValue(Field.TITLE, "C4.5: Programs for Machine Learning");
/* 424:899 */       additional.setValue(Field.PUBLISHER, "Morgan Kaufmann Publishers");
/* 425:900 */       additional.setValue(Field.ADDRESS, "San Mateo, CA");
/* 426:901 */       info.add(additional);
/* 427:    */       
/* 428:903 */       System.out.println("\ntoString():\n" + info.toString());
/* 429:904 */       System.out.println("\ntoBibTex():\n" + info.toBibTex());
/* 430:    */     }
/* 431:    */   }
/* 432:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.TechnicalInformation
 * JD-Core Version:    0.7.0.1
 */