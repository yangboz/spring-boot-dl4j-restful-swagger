package info.smartkit.dl4j.dto;

public class Prediction {
    private String label;
    private double percentage;

    public Prediction(String label, double percentage) {
        this.label = label;
        this.percentage = percentage;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public String toString() {
        return String.format("%s: %.2f ", this.label, this.percentage);
    }
}
