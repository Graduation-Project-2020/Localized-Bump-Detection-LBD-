# Localized Bump Detection (LBD)

Localized Bump Detection (LBD) Graduation Project 2020

Video: https://www.youtube.com/watch?v=6Nk2jFn6L6k

Book: https://drive.google.com/file/d/1sHLtBGrI8MkGyAJ4rMYM7KWyafRsVT3F/view?usp=sharing

### Sponsors: 
Brightskies Company (Mentorship and Funding)

Valeo Company (Mentorship)

### Project Idea: 
The role of the project is solving the problem of road bumps by alerting the driver. If a bump is detected for the first time, our device can detect it using a deep learning model and send a signal to the HMI system to alert the driver and when the car passes over the detected bump, our device saves the location by using IMU and GPS then added to the database connected to an IoT cloud. If there is a bump is encountered for a second time, our device will alert the driver from a further distance even if the deep learning model does not detect it. There is also a mobile applicaton that sends a notification to the driver if he does not have the device.

### Hardware Components: 
![5](https://user-images.githubusercontent.com/42329717/90502555-5223d480-e14e-11ea-9446-fb3ea2d1f5a0.png)

- NVIDIA Jetson Nano 
- ZED Stereo Camera
- GPS 
- IMU
- Tiva C 
- TFT Touch Screen
- Audio Speaker

### The Required Fields: 
![1](https://user-images.githubusercontent.com/42329717/90502483-2ef92500-e14e-11ea-83a7-00914e05607e.png)

- Deep Learning: to detect bumps on the road 
- Localization: to get locations of bumps by using GPS and IMU
- HMI System AUTOSAR Based: Human Machine Interface (HMI) System to notify the user when there is a bump by TFT Touch Screen and Audio Speaker
                            The user can use TFT Touch Screen to enter the password of his WIFI or hotspot.
- IoT: After collecting locations of bumps, we created a database to upload on cloud (Herkou) so the device does not depend on the memory of the Jetson Nano and if there are more than one device in more cars, these devices should connect with each other through IoT Cloud.
- Mobile Application: If the user does not have the device, he can download the mobile application and get a notification if there is a bump nearby.
                      But the user that only has the mobile application can not get a notification if the bump is not saved in the database.
                      
A Graduation Project from the Electronics and Communication Department at Faculty of Engineering, Alexandria University.

Supervisor: Dr. Aida A. Elshafie

Mentor: Eng. Mahmoud Serour (ADAS R&D Engineer – Brightskies technologies)

Mentor: Eng. Mohamed ElAwady (Senior Software Engineer – Valeo Egypt)




       
