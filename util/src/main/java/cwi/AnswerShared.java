package cwi;

public class AnswerShared {
	String answer;
	int begin;
	int end;

	public AnswerShared(String aAnswer, int aBegin, int aEnd) {
		this.answer = aAnswer;
		this.end = aEnd;
		this.begin = aBegin;
	}
	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((answer == null) ? 0 : answer.hashCode());
		result = prime * result + begin;
		result = prime * result + end;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AnswerShared other = (AnswerShared) obj;
		if (answer == null) {
			if (other.answer != null)
				return false;
		} else if (!answer.equals(other.answer))
			return false;
		if (begin != other.begin)
			return false;
		if (end != other.end)
			return false;
		return true;
	}
	public int compareTo(AnswerShared b) {
		if (this.getBegin()<b.getBegin()){
			return -1;
		}
		else if(this.getBegin() > b.getBegin()){
			return 1;
		}
		else {
			return 0;
		}
	}

}
