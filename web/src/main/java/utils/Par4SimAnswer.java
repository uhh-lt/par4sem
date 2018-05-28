package utils;

/**
 * 
 * Possible substitutes for each complex phrases by each workers
 *
 */

public class Par4SimAnswer {
    String candidate;
    String target;
    int count;
    
    public Par4SimAnswer() {
        // TODO Auto-generated constructor stub
    }

    public Par4SimAnswer(String target,String candidate, int count) {
       this.candidate = candidate;
       this.target = target;
       this.count = count;
    }
    public String getCandidate() {
        return candidate;
    }

    public void setCandidate(String candidate) {
        this.candidate = candidate;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((candidate == null) ? 0 : candidate.hashCode());
        result = prime * result + ((target == null) ? 0 : target.hashCode());
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
        Par4SimAnswer other = (Par4SimAnswer) obj;
        if (candidate == null) {
            if (other.candidate != null)
                return false;
        } else if (!candidate.equals(other.candidate))
            return false;
        if (target == null) {
            if (other.target != null)
                return false;
        } else if (!target.equals(other.target))
            return false;
        return true;
    }
    
    

}