package org.apache.mahout.classifier.naivebayes;

import java.util.Iterator;

import org.apache.mahout.common.MahoutTestCase;
import org.apache.mahout.math.DenseMatrix;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.Vector.Element;

public class NaiveBayesTestBase extends MahoutTestCase {
  
  private NaiveBayesModel model;
  
  @Override
  public void setUp() throws Exception {
    super.setUp();
    model = createNaiveBayesModel();
    
    // make sure the model is valid :)
    NaiveBayesModel.validate(model);
  }
  
  protected NaiveBayesModel getModel() {
    return model;
  }
  
  public double complementaryNaiveBayesThetaWeight(int label,
                                                   Matrix weightMatrix,
                                                   Vector labelSum,
                                                   Vector featureSum) {
    double weight = 0.0;
    double alpha = 1.0d;
    for (int i = 0; i < featureSum.size(); i++) {
      double score = weightMatrix.get(i, label);
      double lSum = labelSum.get(label);
      double fSum = featureSum.get(i);
      double totalSum = featureSum.zSum();
      double numerator = fSum - score + alpha;
      double denominator = totalSum - lSum + featureSum.size();
      weight += Math.log(numerator / denominator);
    }
    return weight;
  }
  
  public double naiveBayesThetaWeight(int label,
                                      Matrix weightMatrix,
                                      Vector labelSum,
                                      Vector featureSum) {
    double weight = 0.0;
    double alpha = 1.0d;
    for (int i = 0; i < featureSum.size(); i++) {
      double score = weightMatrix.get(i, label);
      double lSum = labelSum.get(label);
      double numerator = score + alpha;
      double denominator = lSum + featureSum.size();
      weight += Math.log(numerator / denominator);
    }
    return weight;
  }

  public NaiveBayesModel createNaiveBayesModel() {
    double[][] matrix = { {0.7, 0.1, 0.1, 0.3}, {0.4, 0.4, 0.1, 0.1},
                         {0.1, 0.0, 0.8, 0.1}, {0.1, 0.1, 0.1, 0.7}};
    double[] labelSumArray = {1.2, 1.0, 1.0, 1.0};
    double[] featureSumArray = {1.3, 0.6, 1.1, 1.2};
    
    DenseMatrix weightMatrix = new DenseMatrix(matrix);
    DenseVector labelSum = new DenseVector(labelSumArray);
    DenseVector featureSum = new DenseVector(featureSumArray);
    
    double[] thetaNormalizerSum = {naiveBayesThetaWeight(0, weightMatrix, labelSum, featureSum), 
                                   naiveBayesThetaWeight(1, weightMatrix, labelSum, featureSum),
                                   naiveBayesThetaWeight(2, weightMatrix, labelSum, featureSum),
                                   naiveBayesThetaWeight(3, weightMatrix, labelSum, featureSum)};
    // now generate the model
    return new NaiveBayesModel(weightMatrix, featureSum,
        labelSum, new DenseVector(thetaNormalizerSum), 1.0f);
  }
  
  public NaiveBayesModel createComplementaryNaiveBayesModel() {
    double[][] matrix = { {0.7, 0.1, 0.1, 0.3}, {0.4, 0.4, 0.1, 0.1},
                         {0.1, 0.0, 0.8, 0.1}, {0.1, 0.1, 0.1, 0.7}};
    double[] labelSumArray = {1.2, 1.0, 1.0, 1.0};
    double[] featureSumArray = {1.3, 0.6, 1.1, 1.2};
    
    DenseMatrix weightMatrix = new DenseMatrix(matrix);
    DenseVector labelSum = new DenseVector(labelSumArray);
    DenseVector featureSum = new DenseVector(featureSumArray);
    
    double[] thetaNormalizerSum = {complementaryNaiveBayesThetaWeight(0, weightMatrix, labelSum, featureSum), 
                                   complementaryNaiveBayesThetaWeight(1, weightMatrix, labelSum, featureSum),
                                   complementaryNaiveBayesThetaWeight(2, weightMatrix, labelSum, featureSum),
                                   complementaryNaiveBayesThetaWeight(3, weightMatrix, labelSum, featureSum)};
    // now generate the model
    return new NaiveBayesModel(weightMatrix, featureSum,
        labelSum, new DenseVector(thetaNormalizerSum), 1.0f);
  }
  
  public int maxIndex(Vector instance) {
    Iterator<Element> it = instance.iterator();
    int maxIndex = -1;
    double val = Integer.MIN_VALUE;
    while (it.hasNext()) {
      Element e = it.next();
      if (val <= e.get()) {
        maxIndex = e.index();
        val = e.get();
      }
    }
    return maxIndex;
  }
}
