# Localized-Bump-Detection-LBD-

Localized Bump Detection (LBD) Graduation Project 2020

Video: https://www.youtube.com/watch?v=6Nk2jFn6L6k

Project Idea: 

The project role is solving the problem of bumps by alerting the driver. If there is a bump for a first time, our device can detect it by deep learning and send a signal to the HMI system to alert the driver and when the car is on the detected bump, our device saves its location by using IMU and GPS and added to the database that uploading on IoT cloud. If there is a bump that saved on the database, our device will alert the driver from away distance even if the deep learning model does not detect it.  

Hardware Components: 
- NVIDIA Jetson Nano 
- ZED Stereo Camera
- GPS 
- IMU
- Tiva C 
- TFT Touch Screen
- Audio Speaker

The Required Fields: 

![1](https://user-images.githubusercontent.com/42329717/90502483-2ef92500-e14e-11ea-83a7-00914e05607e.png)

- Deep Learning: to detect bumps on road 
- Localization: to get locations of bumps by using GPS and IMU
- HMI System AUTOSAR Based: Human Machine Interface (HMI) System to notify the user when there is a bump by TFT Touch Screen and Audio Speaker
                            The user can use TFT Touch Screen to enter the password of his Wifi or hotspot.
- IoT: After collecting locations of bumps, creating database to upload on cloud (Herkou) to let the device does not depend on Jetson Nano memory and if there are more than
       one device in more cars, these devices should connect with each other through IoT Cloud. 
- Mobile Application: If the user does not want    
    
