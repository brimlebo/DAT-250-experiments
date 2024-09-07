package ex.exp1;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.Objects;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "caption")
public class VoteOption {
    private String caption;
    private int presentationOrder;

    @JsonBackReference
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VoteOption that = (VoteOption) o;
        return presentationOrder == that.presentationOrder &&
                Objects.equals(caption, that.caption);
    }

    @Override
    public int hashCode() {
        return Objects.hash(caption, presentationOrder);
    }
}
