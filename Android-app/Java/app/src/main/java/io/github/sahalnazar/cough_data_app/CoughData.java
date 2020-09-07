package io.github.sahalnazar.cough_data_app;

public class CoughData {


    public String covid;
    public String thermometerReading;
    public String diabetesReading;
    public String bpReading;
    public String fever;
    public String headache;
    public String fatigue;
    public String runnyNose;
    public String diarrhea;
    public String shortBreath;
    public String contactCovid;
    public String smoking;
    public String heartDisease;
    public String asthma;
    public String outside;
    public String cough;
    public String covidAudioUrl;

    public CoughData(){

    }

    public CoughData(String covid, String thermometerReading, String diabetesReading,
                     String bpReading, String fever, String headache, String fatigue,
                     String runnyNose, String diarrhea, String shortBreath, String contactCovid,
                     String smoking, String heartDisease, String asthma, String outside,
                     String cough, String covidAudioUrl){

        this.covid = covid;
        this.thermometerReading = thermometerReading;
        this.diabetesReading = diabetesReading;
        this.bpReading = bpReading;
        this.fever = fever;
        this.headache = headache;
        this.fatigue = fatigue;
        this.runnyNose = runnyNose;
        this.diarrhea = diarrhea;
        this.shortBreath = shortBreath;
        this.contactCovid = contactCovid;
        this.smoking = smoking;
        this.heartDisease = heartDisease;
        this.asthma = asthma;
        this.outside = outside;
        this.cough = cough;
        this.covidAudioUrl = covidAudioUrl;

    }


}
