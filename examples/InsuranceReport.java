public class InsuranceReport {
    private int policyNumber;
    private String insuredDriverName;
    private String insuredDriverLicenseNumber;
    private LocalDateTime accidentDateTime;
    private String accidentLocation;
    private String otherDriverName;
    private String otherDriverLicenseNumber;
    private String otherDriverInsuranceCompany;
    private String otherDriverInsurancePolicyNumber;
    private String descriptionOfDamage;
    private String policeReportNumber;

    public int getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(int policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getInsuredDriverName() {
        return insuredDriverName;
    }

    public void setInsuredDriverName(String insuredDriverName) {
        this.insuredDriverName = insuredDriverName;
    }

    public String getInsuredDriverLicenseNumber() {
        return insuredDriverLicenseNumber;
    }

    public void setInsuredDriverLicenseNumber(String insuredDriverLicenseNumber) {
        this.insuredDriverLicenseNumber = insuredDriverLicenseNumber;
    }

    public LocalDateTime getAccidentDateTime() {
        return accidentDateTime;
    }

    public void setAccidentDateTime(LocalDateTime accidentDateTime) {
        this.accidentDateTime = accidentDateTime;
    }

    public String getAccidentLocation() {
        return accidentLocation;
    }

    public void setAccidentLocation(String accidentLocation) {
        this.accidentLocation = accidentLocation;
    }

    public String getOtherDriverName() {
        return otherDriverName;
    }

    public void setOtherDriverName(String otherDriverName) {
        this.otherDriverName = otherDriverName;
    }

    public String getOtherDriverLicenseNumber() {
        return otherDriverLicenseNumber;
    }

    public void setOtherDriverLicenseNumber(String otherDriverLicenseNumber) {
        this.otherDriverLicenseNumber = otherDriverLicenseNumber;
    }

    public String getOtherDriverInsuranceCompany() {
        return otherDriverInsuranceCompany;
    }

    public void setOtherDriverInsuranceCompany(String otherDriverInsuranceCompany) {
        this.otherDriverInsuranceCompany = otherDriverInsuranceCompany;
    }

    public String getOtherDriverInsurancePolicyNumber() {
        return otherDriverInsurancePolicyNumber;
    }

    public void setOtherDriverInsurancePolicyNumber(String otherDriverInsurancePolicyNumber) {
        this.otherDriverInsurancePolicyNumber = otherDriverInsurancePolicyNumber;
    }

    public String getDescriptionOfDamage() {
        return descriptionOfDamage;
    }

    public void setDescriptionOfDamage(String descriptionOfDamage) {
        this.descriptionOfDamage = descriptionOfDamage;
    }

    public String getPoliceReportNumber() {
        return policeReportNumber;
    }

    public void setPoliceReportNumber(String policeReportNumber) {
        this.policeReportNumber = policeReportNumber;
    }
}