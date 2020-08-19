# Road Bump Detection -- using a Deep Learning Model

### Check the output of the object detection model here: 
https://www.youtube.com/watch?v=kqT52hL7pLM&feature=youtu.be

## Files Overview

### Training_Colab_MobileNetV2.ipynb
- This is where we trained the model on COLAB using the TensorFlow object detection API.

### TRT_Optimize.ipynb
- This is where we convert the fronzen TensorFlow graph to a TensorRT accelerated model.

### TRT_Inference_ZED.ipynb
- This is where we use the model during the inference operation (final deployment).
- This file uses imports the **helper_functions.py** file


