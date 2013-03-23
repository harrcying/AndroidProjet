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

import fr.umlv.andex.data.Answer;
import fr.umlv.andex.data.AnswerCheck;
import fr.umlv.andex.data.AnswerPhoto;
import fr.umlv.andex.data.AnswerRadio;
import fr.umlv.andex.data.AnswerSchema;
import fr.umlv.andex.data.AnswerText;
import fr.umlv.andex.data.NodeQuestion;
import fr.umlv.andex.data.Option;
import fr.umlv.andex.data.Question;
import fr.umlv.andex.data.Quiz;
import fr.umlv.andex.data.StateQuiz;
import fr.umlv.andex.data.TreeQuestion;
import fr.umlv.andex.data.TypeAnswer;

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

	public void /*Quiz*/ getInfo() throws XMLException, JDOMException, IOException {
		Element racine;
		Document document=null;

		SAXBuilder sxb = new SAXBuilder();
		document = sxb.build(examFile);

		racine = document.getRootElement(); // recupere <examen>
		Quiz quiz = new Quiz();
		long id = racine.getAttribute(EXAMID_A).getLongValue();
		String matiere = racine.getAttributeValue(SUBJECT_A);
		@SuppressWarnings("unused") /* pour une utilisation ulterieure */
		String examinateur = racine.getAttributeValue(EXAMINERID_A);
		String type = racine.getAttributeValue(TYPE_A);
		String title = racine.getAttributeValue(TITLE_A);
		quiz.setDescription("Examen de " + matiere);
		quiz.setIdQuiz(id);
		quiz.setTitle(title);
		if (type.compareTo("NOW") == 0) {
			quiz.setState(StateQuiz.IN_PROGRESS);
		} else if (type.compareTo("PAST") == 0) {
			quiz.setState(StateQuiz.DONE);
		} else {
			throw new XMLException("Wrong type Exam");
		}
		TreeQuestion tree = new TreeQuestion();
		tree.setNodes(new ArrayList<NodeQuestion>());
		quiz.setTree(tree);
		visitElemt(racine, 0, 0, "", tree.getNodes(), quiz.getQuestionsByOrder(), null);
		return;
	}

	private void visitElemt (Element elmt, int idRoot, int level, String statmtRoot, List<NodeQuestion> listNodes, List<Question> listQuestions, NodeQuestion parentNode) throws XMLException {
		List<Element> listParts = elmt.getChildren();
		int i = 1;
		for (Element p : listParts) {
			String tagName = p.getName();
			if (tagName.compareTo(STATEMENT_B) != 0) {
				int id = idRoot * 10 + i;
				if (tagName.compareTo(QUESTION_B)!=0) {
					String statement = "";
					String title = "";
					String name = p.getAttributeValue(NAME_A);
					NodeQuestion node = new NodeQuestion();
					node.setId(id);
					if (tagName.compareTo(QUESTIONGROUP_B)==0) {
						statement = p.getChildText(STATEMENT_B);
						if (statement != null && statement.length()!=0) {
							statement = formateStatement(statement);
							statement = statement + "\n\n";
						} else {
							statement = "";
						}
						node.setTitle(name);
					} else {
						title = p.getAttributeValue(TITLE_A);
						node.setTitle(name + " : " + title);
					}
					listNodes.add(node);
					visitElemt(p, id, level+1, statement, node.getNodes(), listQuestions, node);
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
					Question quest = new Question();
					quest.setIdQuestion(id);
					quest.setTitle(name);
					quest.setText(statement);
					quest.setScale(scale);
					parentNode.setQuestion(quest);
					listQuestions.add(quest);
					
					Element answers = p.getChild(ANSWERS_B);
					String type;
					TypeAnswer tq;
					List<Option> choices = null;
					for (Element a : answers.getChildren(ANSWER_B)) {
						type = a.getAttributeValue(TYPE_A);
						tq = TypeAnswer.valueOf(type);
						if (choices!=null && (tq==TypeAnswer.TYPE_ANSWER_RADIO || tq==TypeAnswer.TYPE_ANSWER_CHECK)){
							throw new XMLException("multiple mcq answer : "+id);
						}
						int choiceId;
						Answer answer;
						Element mcq;
						
						switch (tq) {
						case TYPE_ANSWER_RADIO :
							AnswerRadio radio = new AnswerRadio();
							Element opt = a.getChild(CHOICE_B);
							if (opt != null) {
								try {
									choiceId = opt.getAttribute(CHOICEID_A).getIntValue();
									radio.setValue(choiceId);
								} catch (DataConversionException e1) {
									throw new XMLException("Answer id isn't an integer : "+id);
								}
							}
							
							mcq = p.getChild(STATEMENT_B).getChild(MCQ_B);
							choices = new ArrayList<Option>(); 
							for (Element choice : mcq.getChildren()) {
								String c = choice.getText();
								try {
									choiceId = choice.getAttribute(CHOICEID_A).getIntValue();
								} catch (DataConversionException e1) {
									throw new XMLException("Choice id isn't an integer : "+id);
								}
								Option option = new Option();
								option.setDescription(c);
								option.setId(choiceId);
								choices.add(option);

							}
							radio.setOptions(choices);
							
							answer = radio;
							answer.setTypeAnswer(TypeAnswer.TYPE_ANSWER_RADIO);
							break;
							
						case TYPE_ANSWER_CHECK : 
							AnswerCheck check = new AnswerCheck();
							List<Element> listSelectChoices = a.getChildren(CHOICE_B);
							List<Integer> listC = new ArrayList<Integer>();
							for (Element e : listSelectChoices) {
								try {
									choiceId = e.getAttribute(CHOICE_B).getIntValue();
									listC.add(choiceId);
								} catch (DataConversionException e1) {
									throw new XMLException("Answer id isn't an integer : "+id);
								}
							}
							check.setValues(listC);
							
							mcq = p.getChild(STATEMENT_B).getChild(MCQ_B);
							choices = new ArrayList<Option>(); 
							for (Element choice : mcq.getChildren()) {
								String c = choice.getText();
								try {
									choiceId = choice.getAttribute(CHOICEID_A).getIntValue();
								} catch (DataConversionException e1) {
									throw new XMLException("Choice id isn't an integer : "+id);
								}
								Option option = new Option();
								option.setDescription(c);
								option.setId(choiceId);
								choices.add(option);

							}
							check.setOptions(choices);
							
							answer = check;
							answer.setTypeAnswer(TypeAnswer.TYPE_ANSWER_CHECK);
							break;
							
						case TYPE_ANSWER_TEXT :
							AnswerText text = new AnswerText();
							text.setValue(a.getText());
							answer = text;
							answer.setTypeAnswer(TypeAnswer.TYPE_ANSWER_TEXT);
							break;
							
						case TYPE_ANSWER_PHOTO :
							AnswerPhoto photo = new AnswerPhoto();
							photo.setPath(a.getText());
							answer = photo;
							answer.setTypeAnswer(TypeAnswer.TYPE_ANSWER_PHOTO);
							break;
							
						case TYPE_ANSWER_SCHEMA :
							AnswerSchema schema = new AnswerSchema();
							schema.setPath(a.getText());
							answer = schema;
							answer.setTypeAnswer(TypeAnswer.TYPE_ANSWER_SCHEMA);
							break;
							
						default :
							throw new XMLException("Wrong answer type : "+id);
						}
						quest.getAnswers().add(answer);
					}
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
