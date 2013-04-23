# Image Comparator

Image comparator which computes similarity between images to detect duplicates on the Android platform. It uses the OpenCV manager and different algorithms to compare images and help you find an optimized method to detect duplicate images for your application.

It is used in my application [WhatsAppDuplicateMediaRemover] (http://play.google.com/store/apps/details?id=com.torcellite.whatsappduplicatemediaremover)

Image comparison is optimized with the following parameters:
1. Minimum distance between keypoints described to be accepted as duplicates. The lower the minimum distance matches, the higher the chances of the images being duplicate.
2. Different algorithms for varying speed and accuracy. ORB, BRIEF, BRISK and FREAK. Only [PYRAMID_FAST](http://computer-vision-talks.com/2011/01/comparison-of-the-opencvs-feature-detection-algorithms-2/) FeatureDetector is being used as of now to increase speed.
3. SURF and SIFT aren't available in the open source package since they're patented algorithms.

# How to use

Download the code and export it as an Android application or download it from [here] (http://dl.dropboxusercontent.com/u/28378535/imageComparator.apk). Once you have installed imageComparator, please download OpenCV Manager2.4.4 for Android.
Select the images you'd like to compare and change the minimum distance and algorithm to be used in settings and start processing. Once done you can add the results to the log. After enough input, you can statistically find out what the best algorithm and minimum distance for your implementation would be.

It is used in my application [WhatsAppDuplicateMediaRemover] (http://play.google.com/store/apps/details?id=com.torcellite.whatsappduplicatemediaremover)

# To be added/fixed 
 [ADD]Graphing log files on a server

# Screenshots

![Screenshot](http://github.com/torcellite/imageComparator/raw/master/screenshot.png)
![Screenshot](http://github.com/torcellite/imageComparator/raw/master/screenshot1.png)
![Screenshot](http://github.com/torcellite/imageComparator/raw/master/screenshot2.png)

# License

You have all rights to use this project, but please feel obligated to give due credit.

# Contribute

Fork the project and do a pull request. I will merge your changes back into the main project. Many reforms are needed so feel free.


--
Torcellite