# WhoIsInIt
Android application to recognize faces using OpenCV and Clarifai's API

This application uses the Face Embedding obtained from the Clarifai API, for each face, and performs a series of calculations on the phone, to check if 2 faces are close to each other, within a threshold distance.

The only Neural Net that is used in this application is in Clarifai's API, to get the Face Embedding. It isn't used anywhere else, as evaluating the distance between 2 faces can be executed in no time on the phone's CPU itself.

Both Training your face and Recognizing your face is heavily dependent on a robust Internet upload speed. So if your internet isn't great, don't expect blazing fast results.
Clarifai's Neural Net also takes a while to return the face Embedding.

Other than these 2 factors, your face should be recognized in less than half of a second.

Sources given at the end.

<img src="https://raw.githubusercontent.com/srinath10101/WhoIsInIt/master/screenshots/1.png?token=AbDP358VvgIOz-fRqzw1mNExLi-VaVntks5cDRFXwA%3D%3D" data-canonical-src="https://gyazo.com/eb5c5741b6a9a16c692170a41a49c858.png" width="250" height="444" />

The first step is to select images that contain only your face. This way, the model can be trained to recognize your face.

<img src="https://raw.githubusercontent.com/srinath10101/WhoIsInIt/master/screenshots/2.png?token=AbDP3732Op52SH_j47e6Q8EdPBu56awZks5cDRF5wA%3D%3D" data-canonical-src="https://gyazo.com/eb5c5741b6a9a16c692170a41a49c858.png" width="250" height="444" />

You can select upto 25 images of yourself to train your face.

<img src="https://raw.githubusercontent.com/srinath10101/WhoIsInIt/master/screenshots/3.png?token=AbDP3zdR8yVgOxjjcUmqdbmsRl25Wsrqks5cDRGIwA%3D%3D" data-canonical-src="https://gyazo.com/eb5c5741b6a9a16c692170a41a49c858.png" width="250" height="650" />

Training 25 images of my face took approximately 3 minutes, with a 2.75 Mbps upload speed (I'm living under a rock)

Training my face..           |  Done!
:---------------------------:|:-------------------------:
<img src="https://raw.githubusercontent.com/srinath10101/WhoIsInIt/master/screenshots/4.png?token=AbDP36qt2JRSFulJAGYWMt1HAvJD3TNyks5cDRGewA%3D%3D" data-canonical-src="https://gyazo.com/eb5c5741b6a9a16c692170a41a49c858.png" width="250" height="444" />    |  <img src="https://raw.githubusercontent.com/srinath10101/WhoIsInIt/master/screenshots/5.png?token=AbDP30oGH3AVGUj-zO8J5LxYLZLtEJ3Eks5cDRGswA%3D%3D" data-canonical-src="https://gyazo.com/eb5c5741b6a9a16c692170a41a49c858.png" width="250" height="444" />





Now that training is complete, we can test it out.

<img src="https://raw.githubusercontent.com/srinath10101/WhoIsInIt/master/screenshots/6.png?token=AbDP3xtQuHjhibFzuzoQCQqcHASimA8Hks5cDRHAwA%3D%3D" data-canonical-src="https://gyazo.com/eb5c5741b6a9a16c692170a41a49c858.png" width="250" height="444" />

This takes 5-6 seconds to recognize me, because of my potato internet speed. With faster speeds, the API calls will naturally be faster.

![Alt Text](https://raw.githubusercontent.com/srinath10101/WhoIsInIt/master/screenshots/vid.gif?token=AbDP34X6IXR7erfQ8rwzEKbf6F2jrQvyks5cDRCJwA%3D%3D)


Sources:
<a href="https://clarifai.com/models/face-embedding-image-recognition-model-d02b4508df58432fbb84e800597b8959">Clarifai's Face Embedding</a>
<a href="http://blog.codeonion.com/2015/11/25/creating-a-new-opencv-project-in-android-studio/">Setting up OpenCV on Android Studio</a>