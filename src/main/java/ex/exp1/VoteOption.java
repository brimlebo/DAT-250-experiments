package ex.exp1;

public class VoteOption {
    private String caption;
    private int presentationOrder;
    private Poll poll;

    public VoteOption() {}

    public VoteOption(String caption, int presentationOrder, Poll poll) {
        this.caption = caption;
        this.presentationOrder = presentationOrder;
        this.poll = poll;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setPresentationOrder(int presentationOrder) {
        this.presentationOrder = presentationOrder;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    public String getCaption() {
        return caption;
    }

    public int getPresentationOrder() {
        return presentationOrder;
    }

    public Poll getPoll() {
        return poll;
    }
}
