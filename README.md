# Image Comparator

Image comparator which computes similarity between images to detect duplicates on the Android platform. It uses the OpenCV manager and different algorithms to compare images and help you find an optimized method to detect duplicate images for your application.

Image comparison is optimized with the following parameters:
1. Minimum distance between keypoints described to be accepted as duplicates. The lower the minimum distance matches, the higher the chances of the images being duplicate.
2. Different algorithms for varying speed and accuracy. SIFT, SURF, ORB, BRIEF, BRISK and FREAK. Only FAST FeatureDetector is being used as of now to increase speed.

It is used in my application [WhatsAppDuplicateMediaRemover] (http://play.google.com/store/apps/details?id=com.torcellite.whatsappduplicatemediaremover)

# To be added/fixed

 [fix]Fatal Exception caused by onNewIntent in MainActivity has to be resolved
 
 [add]Graphing log files on a server

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