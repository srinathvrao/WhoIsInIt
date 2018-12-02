# WhoIsInIt - Fastest Face recognition algorithm, coupled with the slowest Internet speed
Android application to recognize faces using OpenCV and Clarifai's API

This application uses the 1024-dimensional Face Embedding obtained from the Clarifai API, for each face, and performs a series of calculations on the phone, to check if 2 faces are close to each other, within a threshold distance.

The best feature about this application is it does not require a phone with a robust GPU. You need a good internet upload speed.

Clarifai's Face Embedding model is still in beta. It might not be the fastest. 
<br/>It took 5.94 seconds to recognize my face with a 2.75 Mbps upload speed (I live under a rock)

References given at the end.

<img src="https://raw.githubusercontent.com/srinath10101/WhoIsInIt/master/screenshots/1.png?token=AbDP358VvgIOz-fRqzw1mNExLi-VaVntks5cDRFXwA%3D%3D" data-canonical-src="https://gyazo.com/eb5c5741b6a9a16c692170a41a49c858.png" width="250" height="444" />

The first step is to select images that contain only your face. This way, the model can be trained to recognize your face.

<img src="https://raw.githubusercontent.com/srinath10101/WhoIsInIt/master/screenshots/2.png?token=AbDP3732Op52SH_j47e6Q8EdPBu56awZks5cDRF5wA%3D%3D" data-canonical-src="https://gyazo.com/eb5c5741b6a9a16c692170a41a49c858.png" width="250" height="444" />

You can select upto 25 images of yourself to train your face.

Training 25 images of my face took approximately 3 minutes, with a 2.75 Mbps upload speed (I'm living under a rock)

Training my face..           |  Done!
:---------------------------:|:-------------------------:
<img src="https://raw.githubusercontent.com/srinath10101/WhoIsInIt/master/screenshots/4.png?token=AbDP36qt2JRSFulJAGYWMt1HAvJD3TNyks5cDRGewA%3D%3D" data-canonical-src="https://gyazo.com/eb5c5741b6a9a16c692170a41a49c858.png" width="250" height="444" />    |  <img src="https://raw.githubusercontent.com/srinath10101/WhoIsInIt/master/screenshots/5.png?token=AbDP30oGH3AVGUj-zO8J5LxYLZLtEJ3Eks5cDRGswA%3D%3D" data-canonical-src="https://gyazo.com/eb5c5741b6a9a16c692170a41a49c858.png" width="250" height="444" />


Now that training is complete, we can test it out.

This takes 5-6 seconds to recognize me, because of my potato internet speed. With faster speeds, the API calls will naturally be faster.

![Alt Text](https://raw.githubusercontent.com/srinath10101/WhoIsInIt/master/screenshots/vid.gif?token=AbDP34X6IXR7erfQ8rwzEKbf6F2jrQvyks5cDRCJwA%3D%3D)


References:<br />
<a href="https://clarifai.com/models/face-embedding-image-recognition-model-d02b4508df58432fbb84e800597b8959">Clarifai's Face Embedding</a>
<br/>
<a href="http://blog.codeonion.com/2015/11/25/creating-a-new-opencv-project-in-android-studio/">Setting up OpenCV on Android Studio</a>