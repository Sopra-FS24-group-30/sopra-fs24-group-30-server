package ch.uzh.ifi.hase.soprafs24.logic.Returns;

public class UltimateData {

    String ultimateName;
    Boolean isActive;

    public String getUltimateName() {
        return ultimateName;
    }

    public void setUltimateName(String ultimateName) {
        this.ultimateName = ultimateName;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public static UltimateData prepareData(String ultimateName, Boolean isActive){
        UltimateData ultimateData = new UltimateData();
        ultimateData.setUltimateName(ultimateName);
        ultimateData.setActive(isActive);
        return ultimateData;
    }
}
