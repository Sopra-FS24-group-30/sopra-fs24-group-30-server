package ch.uzh.ifi.hase.soprafs24.logic.Returns;

public class Cash {

    private static final String type = "cash";
    private CashData data;

    public String getType() {
        return type;
    }

    public CashData getData() {
        return data;
    }

    public void setData(CashData data) {
        this.data = data;
    }
}
