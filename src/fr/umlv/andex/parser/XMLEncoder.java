package fr.umlv.andex.parser;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import fr.umlv.andex.data.Answer;
import fr.umlv.andex.data.AnswerCheck;
import fr.umlv.andex.data.AnswerPhoto;
import fr.umlv.andex.data.AnswerRadio;
import fr.umlv.andex.data.AnswerSchema;
import fr.umlv.andex.data.AnswerText;
import fr.umlv.andex.data.TypeAnswer;

public class XMLEncoder {
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
	private static String TIME_A 			= "temps";
	private static String NAME_A 			= "nom";
	private static String SUBJECT_A 		= "matiere";
	private static String EXAMINERID_A 		= "examinateurId";
	private static String TITLE_A 			= "titre";
	private static String SCALE_A 			= "bareme";
	private static String TYPE_A 			= "type";
	private static String CHOICEID_A		= "id";

	public XMLEncoder(File file) {
		this.examFile = file;
	}

	public boolean putAnswer(long questionID, Answer answer) throws JDOMException, IOException {
		Element racine;
		Document document =null;

		SAXBuilder sxb = new SAXBuilder();
		document = sxb.build(examFile);

		racine = document.getRootElement();

		boolean goodJob = visitElement(racine, questionID, 0, 1, answer);

		return goodJob;
	}

	private boolean visitElement(Element elem, long targetId, long internalId, int level, Answer answer) {

		List<Element> listParts = elem.getChildren();
		internalId*=10;
		long levelIndex = getIndexOfLevel(targetId, level);
		System.out.println("Target : " + targetId + ", intern : " + internalId);

		for(Element p : listParts) {
			if(p.getName().compareTo(STATEMENT_B) != 0) {
				internalId++;
				System.out.println("\tTarget : " + targetId + ", intern : " + internalId);
				if(levelIndex == internalId) {
					System.out.println("\t\tSame level ID : " + p.getAttribute(NAME_A));

					if(targetId == internalId) {
						System.out.println("\t\t\tWrite \"" + answer + "\" here :)");

						for(Element child: p.getChildren()) {
							System.out.println(child.getName());
							if(child.getName().compareTo(ANSWERS_B) == 0) {
								Element reponse = child.getChild(ANSWER_B);

								TypeAnswer ta = TypeAnswer.valueOf(reponse.getAttributeValue(TYPE_A));
								System.out.println(ta);

								switch(answer.getTypeAnswer()) {
								case TYPE_ANSWER_TEXT: 
									if(ta.equals(answer.getTypeAnswer())) {
										System.out.println(((AnswerText)answer).getValue());
										reponse.setText(((AnswerText)answer).getValue()); 
									} else {
										System.out.println("Bad answer type. Question ID type :" + ta + ", Answer ID type + " + answer.getTypeAnswer());
									}
									break;
								case TYPE_ANSWER_RADIO: //TODO Tester si les ID choisies existent bel et bien
									if(ta.equals(answer.getTypeAnswer())) {
										Element choix = new Element(CHOICE_B);
										Attribute attrib = new Attribute(CHOICEID_A, String.valueOf(((AnswerRadio)answer).getValue()));
										choix.setAttribute(attrib);
										System.out.println(String.valueOf(((AnswerRadio)answer).getValue()));
										reponse.setContent(choix);
									} else {
										System.out.println("Bad answer type. Question ID type :" + ta + ", Answer ID type + " + answer.getTypeAnswer());
									}
									break;
								case TYPE_ANSWER_PHOTO:
									if(ta.equals(answer.getTypeAnswer())) {
										System.out.println(((AnswerPhoto)answer).getPath());
										reponse.setText(((AnswerPhoto)answer).getPath());
									} else {
										System.out.println("Bad answer type. Question ID type :" + ta + ", Answer ID type + " + answer.getTypeAnswer());
									}
									break;
								case TYPE_ANSWER_SCHEMA:
									if(ta.equals(answer.getTypeAnswer())) {
										System.out.println(((AnswerSchema)answer).getPath());
										reponse.setText(((AnswerSchema)answer).getPath());
									} else {
										System.out.println("Bad answer type. Question ID type :" + ta + ", Answer ID type + " + answer.getTypeAnswer());
									}
									break;
								case TYPE_ANSWER_CHECK: //TODO Tester si les ID choisies existent bel et bien
									if(ta.equals(answer.getTypeAnswer())) {
										Element choixMultiple;
										for(Integer val: ((AnswerCheck)answer).getValues()) {
											choixMultiple = new Element(CHOICE_B);
											Attribute attribute = new Attribute(CHOICEID_A, String.valueOf(val));
											choixMultiple.setAttribute(attribute);
											reponse.addContent(choixMultiple);

										}
									} else {
										System.out.println("Bad answer type. Question ID type :" + ta + ", Answer ID type + " + answer.getTypeAnswer());
									}
									break;
								default: System.out.println("err");
								}

							}
							save(examFile.getName(), p.getDocument());
						}

						return true;
					} else {
						return visitElement(p, targetId, internalId, level+1, answer);
					}
				}
			}
		}

		System.out.println("Return false =D");

		return false;
	}

	private int getIndexOfLevel(long id, int level) {
		Integer comparableInt = Integer.valueOf(String.valueOf(id).substring(0, level));
		System.out.println(comparableInt);

		return comparableInt;
	}

	static void save(String fichier, Document doc)
	{
		try
		{
			XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
			sortie.output(doc, new FileOutputStream(fichier));
		}
		catch (java.io.IOException e){}
	}

	/**
	 * For internal Test
	 * 
	 * @param args No args
	 */
	public static void main(String[] args) {
		

		File f = new File("examC.xml");

		System.out.println(f.canWrite());

		XMLEncoder encoder = new XMLEncoder(f);

		//		AnswerCheck answer = new AnswerCheck();
		//		List<Integer> values = new ArrayList<Integer>();
		//		values.add(1);
		//		values.add(2);
		//		answer.setValues(values);

		//		AnswerPhoto answer = new AnswerPhoto();
		//		answer.setPath("MyPictures/monImage");

		//		AnswerText answer = new AnswerText();
		//		answer.setValue("Testing Value");

		AnswerRadio answer = new AnswerRadio();
		answer.setValue(1);

		try {
			System.out.println("Find question ? " + encoder.putAnswer(11, answer));
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
