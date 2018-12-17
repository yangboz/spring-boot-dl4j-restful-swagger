package info.smartkit.dl4j.utils;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import info.smartkit.dl4j.dto.Prediction;

import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.modelimport.keras.trainedmodels.Utils.ImageNetLabels;
import org.deeplearning4j.zoo.PretrainedType;
import org.deeplearning4j.zoo.ZooModel;
import org.deeplearning4j.zoo.model.VGG16;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.VGG16ImagePreProcessor;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class ImageClassifier {
    private static final int HEIGHT = 224;
    private static final int WIDTH = 224;
    private static final int CHANNELS = 3;
    private ComputationGraph vgg16;
    private NativeImageLoader nativeImageLoader;

    ImageClassifier() {
        try {
            //Setup the VGG16 model from the DL4J ModelZoo
            ZooModel zooModel = new VGG16();
            vgg16 = (ComputationGraph) new VGG16().initPretrained(PretrainedType.IMAGENET);
        } catch (IOException e) {
            e.printStackTrace();
        }
        nativeImageLoader = new NativeImageLoader(HEIGHT, WIDTH, CHANNELS);
    }

    /**
     * Classify the image with the VGG16 model
     *
     * @param inputStream
     * @return
     */
    public String classify(InputStream inputStream) {

        INDArray image = loadImage(inputStream);

        normalizeImage(image);

        INDArray output = processImage(image);

        List<Prediction> predictions = decodePredictions(output);

        return predictionsToString(predictions);
    }

    private INDArray processImage(final INDArray image) {
        INDArray[] output = vgg16.output(false, image);
        return output[0];
    }

    private INDArray loadImage(final InputStream inputStream) {
        INDArray image = null;
        try {
            image = nativeImageLoader.asMatrix(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * Normalize the image
     *
     * @param image
     */
    private void normalizeImage(final INDArray image) {
        DataNormalization scaler = new VGG16ImagePreProcessor();
        scaler.transform(image);
    }

    /**
     * Rank the activation of the output nodes to create a top 5 of predictions
     */
    private List<Prediction> decodePredictions(INDArray encodedPredictions) {
        List<Prediction> decodedPredictions = new ArrayList<Prediction>();
        int[] top5 = new int[5];
        float[] top5Prob = new float[5];

        ArrayList<String> labels = ImageNetLabels.getLabels();
        int i = 0;

        for (INDArray currentBatch = encodedPredictions.getRow(0).dup(); i < 5; ++i) {

            top5[i] = Nd4j.argMax(currentBatch, 1).getInt(0, 0);
            top5Prob[i] = currentBatch.getFloat(0, top5[i]);
            currentBatch.putScalar(0, top5[i], 0.0D);

            decodedPredictions.add(new Prediction(labels.get(top5[i]), (top5Prob[i] * 100.0F)));
        }

        return decodedPredictions;
    }

    private String predictionsToString(List<Prediction> predictions) {
        StringBuilder builder = new StringBuilder();
        for (Prediction prediction : predictions) {
            builder.append(prediction.toString());
            builder.append('\n');
        }
        return builder.toString();

    }

}