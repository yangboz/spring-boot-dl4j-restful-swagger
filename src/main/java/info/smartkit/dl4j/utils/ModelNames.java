package info.smartkit.dl4j.utils;

public enum ModelNames {
    SIMPLE_MLP("simple_mlp.h5"),
    VGG16("vgg_16");

    private String filename;

    ModelNames(String filename) {
        this.filename = filename;
    }

    public String filename() {
        return filename;
    }
}
