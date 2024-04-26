package ch.uzh.ifi.hase.soprafs24.logic.Returns;

public class Move {

    private static final String type = "move";
    private MoveData data;

    public String getType() {
        return type;
    }

    public MoveData getData() {
        return data;
    }

    public void setData(MoveData data) {
        this.data = data;
    }
}
