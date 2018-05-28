package cwi;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// Geting Answers grouped by number of workers
// SELECT hitId, begin, end, answer, count(answer)  FROM ComplexHIT.answers group by hitId, begin, end, answer;
//====////
//group by language too
public class Answer {
	public int count;
	public String answer;
	public String lemma;
	public int begin;
	public int end;

	public Answer(String aAnswer, String aLemma, int aBegin, int aEnd, int aCount) {
		this.count = aCount;
		this.answer = aAnswer;
		this.lemma = aLemma;
		this.begin = aBegin;
		this.end = aEnd;
	}

	//TODO make sure to exclude count of 1
	public static void Add(Answer aAnswer, Set<Answer> aAnswers) {
		boolean contained = false;
		List<Answer> containes = new ArrayList<>();
		for (Answer answer : aAnswers) {
			
			if (aAnswer.begin == answer.begin && aAnswer.end == answer.end) {
				continue;
			}
			if (aAnswer.begin >= answer.begin && aAnswer.end <= answer.end) {
				contained = true;
				answer.count = Math.max(aAnswer.count, answer.count);
				//break; 
			}
			if (answer.begin > aAnswer.begin && answer.end < aAnswer.end) {
				containes.add(answer);
				aAnswer.count =  Math.max(aAnswer.count, answer.count);
			}
		}

		if (!contained) {
			aAnswers.add(aAnswer);
		}
		aAnswers.removeAll(containes);
	}

	public static void AddAll(Answer aAnswer, Set<Answer> aAnswers) {
		boolean contained = false;
		List<Answer> containes = new ArrayList<>();
		for (Answer answer : aAnswers) {
			if (answer.answer.equals(aAnswer.answer)) {
				if (answer.count < aAnswer.count) {
					answer.count = aAnswer.count;
					contained = true;
					break;
				}
				else{
					contained = true;
					break;
				}
			} else if (answer.answer.contains(" ") && (answer.answer.contains(" " + aAnswer.answer + " ")
					|| answer.answer.startsWith(aAnswer.answer + " ")
					|| answer.answer.endsWith(" " + aAnswer.answer))) {
				if (answer.count < aAnswer.count) {
					answer.count = aAnswer.count;
					//contained = true;
					break;
				}
			} else if (aAnswer.answer.contains(" ") && (aAnswer.answer.contains(" " + answer.answer + " ")
					|| aAnswer.answer.startsWith(answer.answer + " ")
					|| aAnswer.answer.endsWith(" " + answer.answer))) {
				if (answer.count < aAnswer.count) {
					answer.count = aAnswer.count;
					//contained = true;
					break;
				}
			}

		}
		if (!contained) {
			aAnswers.add(aAnswer);
		}
		aAnswers.removeAll(containes);
	}
	
	public static Answer getAnswer(Answer aAnswer, Set<Answer> aAnswers) {
		for (Answer answer : aAnswers) {
			if (aAnswer.begin >= answer.begin && aAnswer.end <= answer.end) {
				return answer;
			}
		}
		return null;
	}
	// used to set the same answer even if it is not selected in this document1
	public static Answer getAnswer(String aAnswer,String aLemma, Set<Answer> aAnswers) {
		for (Answer answer : aAnswers) {
			if(aAnswer.equals(answer.answer)){
				answer.lemma = aLemma;
				return answer;
			}
			else if(aLemma!=null && answer.lemma!=null &&  aLemma.equals(answer.lemma)){
				return answer;
			}
		}
		return null;
	}
	
	public static Answer getAnswerMwe(String aAnswer,String aLemma, Set<Answer> aAnswers) {
		for (Answer answer : aAnswers) {
			if(aAnswer.equals(answer.answer)){
				answer.lemma = aLemma;
				return answer;
			}
			else if(aLemma!=null && answer.lemma!=null && aLemma.equals(answer.lemma)){
				return answer;
			}
			// If this answer is part of a multword expressions
			//else if(answer.answer.contains(" ") && answer.answer.contains(aAnswer)){
			 else if (answer.answer.contains(" ") && (answer.answer.contains(" " + aAnswer + " ")
						|| answer.answer.startsWith(aAnswer + " ")
						|| answer.answer.endsWith(" " + aAnswer))) {
				return answer;
			}
		}
		return null;
	}
	
	public static boolean contains(String aAnswer, String aLemma,  Set<Answer> aAnswers){
		for(Answer answer:aAnswers){
			if(aAnswer.equals(answer.answer)){
				answer.lemma = aLemma;
				return true;
			}
			else if(aLemma!=null && answer.lemma!=null &&  aLemma.equals(answer.lemma)){
				System.out.println("same lemma");
				return true;
			}
		}
		return false;
	}
}