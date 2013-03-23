package fr.umlv.andex.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import fr.umlv.andex.data.TypeAnswer;

import android.util.Log;

public class XMLParser {
	private final File examFile;

	private static String EXAM_B 			= "examen";
	private static String PART_B			= "partie";
	private static String EXERCICE_B 		= "exercice";
	private static String EXOPART_B 		= "partieExo";
	private static String QUESTIONGROUP_B 	= "questionGroupe";
	private static String QUESTION_B 		= "question";
	private static String STATEMENT_B 		= "enonce";
	private static String MCQ_B 			= "qcm";
	private static String CHOICE_B 			= "choix";
	private static String ANSWERS_B			= "reponses";
	private static String ANSWER_B			= "reponse";
	private static String EXAMID_A 			= "examId";
	private static String NAME_A 			= "nom";
	private static String SUBJECT_A 		= "matiere";
	private static String EXAMINERID_A 		= "examinateurId";
	private static String TITLE_A 			= "titre";
	private static String SCALE_A 			= "bareme";
	private static String TYPE_A 			= "type";
	private static String CHOICEID_A		= "id";


	public XMLParser(File file) {
		this.examFile = file;
	}

	public void /*TreeQuestion*/ getInfo() throws XMLException, JDOMException, IOException {
		Element racine;
		Document document=null;

		SAXBuilder sxb = new SAXBuilder();
		document = sxb.build(examFile);

		racine = document.getRootElement(); // recupere <examen>
		// new Quiz
		long id = racine.getAttribute(EXAMID_A).getLongValue();
		String matiere = racine.getAttributeValue(SUBJECT_A);
		String examinateur = racine.getAttributeValue(EXAMINERID_A);
		Log.d("", "id : " + id + " matiere : " + matiere + " examinateur : " + examinateur);
		visitElemt(racine, 0, 0, "");
		return;
	}

	private void visitElemt (Element elmt, int idRoot, int level, String statmtRoot) throws XMLException {
		List<Element> listParts = elmt.getChildren();
		int i = 1;
		/* new list */
		for (Element p : listParts) {
			String tagName = p.getName();
			if (tagName.compareTo(STATEMENT_B) != 0) {
				int id = idRoot * 10 + i;
				if (tagName.compareTo(QUESTION_B)!=0) {
					String statement = "";
					String title = "";
					//System.out.println(tagName + " " + id);
					Log.d("", tagName + " " + id);
					String name = p.getAttributeValue(NAME_A);
					System.out.println("Nom : " + name);
					if (tagName.compareTo(QUESTIONGROUP_B)==0) {
						statement = p.getChildText(STATEMENT_B);
						if (statement != null && statement.length()!=0) {
							statement = formateStatement(statement);
							statement = statement + "\n\n";
						} else {
							statement = "";
						}
						System.out.println("enonce : " + statement);
					} else {
						title = p.getAttributeValue(TITLE_A);
						System.out.println("titre = " + title);
					}
					/* new NodeQuestion */
					visitElemt(p, id, level+1, statement);
				} else {
					String name = p.getAttributeValue(NAME_A);
					float scale;
					try {
						scale = p.getAttribute(SCALE_A).getFloatValue();
					} catch (Exception e) {
						throw new XMLException(e.getMessage() + "err convertion : "+id);
					}
					String statement = p.getChild(STATEMENT_B).getText();
					if (statement == null) {
						throw new XMLException("statement null : "+id);
					}
					statement = statmtRoot + formateStatement(statement);
					System.out.println("nom : " + name + " bareme : " + scale + "\nenonce : " + statement);
					/* new Question : pas les choix dedans */
					Element answers = p.getChild(ANSWERS_B);
					String type;
					TypeAnswer tq;
					List<String> choices = null;	// -> liste de Option
					for (Element a : answers.getChildren(ANSWER_B)) {
						type = a.getAttributeValue(TYPE_A);
						tq = TypeAnswer.valueOf(type);
						int choiceId;
						// Answer answer;


						/* Création des réponses et Récupération des réponses données : */
						switch (tq) {
						case TYPE_ANSWER_RADIO :
							// answer = new AnswerRadio();
							Element opt = a.getChild(CHOICE_B);
							if (opt != null) {
								try {
									//choiceId = a.getChild(CHOICE_B).getAttribute(CHOICEID_A).getIntValue();
									choiceId = opt.getAttribute(CHOICEID_A).getIntValue();
									// setValue
									System.out.println(choiceId);
								} catch (DataConversionException e1) {
									throw new XMLException("Answer id isn't an integer : "+id);
								}
							}
							break;
						case TYPE_ANSWER_CHECK : 
							// answer = new AnswerCheck();
							List<Element> listSelectChoices = a.getChildren(CHOICE_B);
							List<Integer> listC = new ArrayList<Integer>();
							for (Element e : listSelectChoices) {
								try {
									choiceId = e.getAttribute(CHOICE_B).getIntValue();
									listC.add(choiceId);
									System.out.println(choiceId);
								} catch (DataConversionException e1) {
									throw new XMLException("Answer id isn't an integer : "+id);
								}
							}
							// setValues
							break;
						case TYPE_ANSWER_TEXT :
							// answer = new AnswerTest();
							String asw = a.getText();
							// setValue
							System.out.println(asw);
							break;
						case TYPE_ANSWER_PHOTO :
							// answer = new AnswerPhoto();
							String pictPath = a.getText();
							// setValue
							System.out.println(pictPath);
							break;
						case TYPE_ANSWER_SCHEMA :
							// answer = new AnswerSchema();
							String schPath = a.getText();
							// setValue
							System.out.println(schPath);
							break;
						default :
							throw new XMLException("Wrong answer type : "+id);
						}

						/* récupération des choix de l'énoncé : */
						if (choices!=null && (tq==TypeAnswer.TYPE_ANSWER_RADIO || tq==TypeAnswer.TYPE_ANSWER_CHECK)){
							throw new XMLException("multiple mcq answer : "+id);
						}
						switch (tq) {
						case TYPE_ANSWER_RADIO :
						case TYPE_ANSWER_CHECK :
							Element mcq = p.getChild(STATEMENT_B).getChild(MCQ_B);
							choices = new ArrayList<String>(); 
							for (Element choice : mcq.getChildren()) {
								String c = choice.getText();
								try {
									choiceId = choice.getAttribute(CHOICEID_A).getIntValue();
								} catch (DataConversionException e1) {
									throw new XMLException("Choice id isn't an integer : "+id);
								}
								choices.add(c); // TODO : type option

							}
							break;
						default :
							break;
						}
						if (choices != null) { // a supprimer
							for (String s : choices) {
								System.out.println("- " + s);
							}
						}
					}
					// TODO return;
				}
				i++;
			}
		}
	}


	private static String formateStatement (String stmt) {
		int i = 0;
		char c = stmt.charAt(0);
		while (c == '\t' || c == ' ' || c == '\n' || c == '\r') {
			i ++;
			c = stmt.charAt(i);
		}
		stmt = stmt.substring(i);

		i = stmt.length()-1;
		c = stmt.charAt(i);
		while (c == '\t' || c == ' ' || c == '\n' || c == '\r') {
			i --;
			c = stmt.charAt(i);
		}
		stmt = stmt.substring(0,i+1);

		return stmt;
	}

}
