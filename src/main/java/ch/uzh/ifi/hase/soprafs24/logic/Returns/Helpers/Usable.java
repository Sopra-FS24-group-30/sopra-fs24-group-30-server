package ch.uzh.ifi.hase.soprafs24.logic.Returns.Helpers;

public class Usable {

    private static final String type = "usables";
    private UsableData data;

    public String getType(){
        return type;
    }

    public UsableData getData() {
        return data;
    }

    public void setData(UsableData data) {
        this.data = data;
    }
}
