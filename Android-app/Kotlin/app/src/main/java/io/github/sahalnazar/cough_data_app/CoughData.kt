package io.github.sahalnazar.cough_data_app

class CoughData {
    var covid: String? = null
    var thermometerReading: String? = null
    var diabetesReading: String? = null
    var bpReading: String? = null
    var fever: String? = null
    var headache: String? = null
    var fatigue: String? = null
    var runnyNose: String? = null
    var diarrhea: String? = null
    var shortBreath: String? = null
    var contactCovid: String? = null
    var smoking: String? = null
    var heartDisease: String? = null
    var asthma: String? = null
    var outside: String? = null
    var cough: String? = null
    var covidAudioUrl: String? = null

    constructor() {}
    constructor(covid: String?, thermometerReading: String?, diabetesReading: String?,
                bpReading: String?, fever: String?, headache: String?, fatigue: String?,
                runnyNose: String?, diarrhea: String?, shortBreath: String?, contactCovid: String?,
                smoking: String?, heartDisease: String?, asthma: String?, outside: String?,
                cough: String?, covidAudioUrl: String?) {
        this.covid = covid
        this.thermometerReading = thermometerReading
        this.diabetesReading = diabetesReading
        this.bpReading = bpReading
        this.fever = fever
        this.headache = headache
        this.fatigue = fatigue
        this.runnyNose = runnyNose
        this.diarrhea = diarrhea
        this.shortBreath = shortBreath
        this.contactCovid = contactCovid
        this.smoking = smoking
        this.heartDisease = heartDisease
        this.asthma = asthma
        this.outside = outside
        this.cough = cough
        this.covidAudioUrl = covidAudioUrl
    }
}