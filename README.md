# Vault app

## Mobile application made with Android Studio

This is the repository that you are going to use **individually** for developing your project. Please use the resources provided in the module to learn about **plagiarism** and how plagiarism awareness can foster your learning.

Regarding the use of this repository, once a feature (or part of it) is developed and **working** or parts of your system are integrated and **working**, define a commit and push it to the remote repository. You may find yourself making a commit after a productive hour of work (or even after 20 minutes!), for example. Choose commit message wisely and be concise.

Please choose the structure of the contents of this repository that suits the needs of your project but do indicate in this file where the main software artefacts are located.

---------------------------------------------------------------------------------------------------------------------------------

# Installation process

## 1: Installing Java

In order to Install and run Android Studio, the windows operating machine must have a Java JDK. *You can check if your system has java SDK by opening the command prompt and entering the command* :

> java -version

If it says the java version "xx.x.x" you have java installed and can skip this step. If not proceed with visiting the website:

> https://www.oracle.com/java/technologies/downloads/

Select windows or any operating machine and download the exe or deb file (depending on the machine). For windows this is the file to download

> x64 Installer

Run the executable and finish the installation process

Now, we have to setup the Environment Variables. Search for the java folder downloaded. Default path should be:


> C:\Program Files:\Java

Enter the bin folder and copy the path. Should look like this:

JDK version may be different

> C:\Program Files\Java\jdk-11.0.9\bin

In the windows search bar, enter: **Edit Environmental variables for your account** and click on it

Under **System variables** you are going to notice **path** variable. Click on it to mark, and after its marked click on **Edit**. Click on **New** and paste the path you have copied before.

## 2: Installing Android Studio

Visit the website and download the Android Studio:

> https://developer.android.com/studio

Finish the setup. You can leave everything at default options. Open the Android Studio. It is perfect time to clone the project

>git clone https://campus.cs.le.ac.uk/gitlab/ug_project/21-22/ed182.git

On the Welcome to Android Studio window you can press the *Open an existing Android Studio project* and select the ed182 folder in the directory where u have cloned the project. You should be able to see the code I have created.

## 3: Create Android Emulator

At the right handside click on the **Device Manager** text, the text itself is flipped 90 degrees. Proceed with **Create device**, select **Phone** -> **Pixel 5** -> **Next** -> Download the Release Name **S** with API Level **31** and ABI **x86_64**. AFter its downloaded select that and click **Next**, proceed with **Finish** 

## 4: Launch the emulator

in the Device Manager tab you can notice that you have created the Emulator, click on the *green triangle* below **Actions tab**.

## 5: Create fingerprint

After its launched, it is necessary to create the biometric fingerprint in order to access the application. Go to: **Settings** -> **Security** -> **Pixel Imprint** -> **Pixel Imprint + PIN** -> *Enter pin* -> *Follow instructions* -> Now, when it says touch the sensor you got to click on the 3 bubbles besides the emulator (it can appear on top or on the right handside) it is going to extend the *Extended Controls*, in the extended tab, click on the **Fingerprint**, choose any fingerprint from the list and click **Touch Sensor**, look at the emulator, follow instructions.

## 6: Launch the Application

After the finger print is registered, you can launch the app. Launch the application from the top of the screen by clicking on the green triangle, or simply entering **Shift+F10**. Congratulations, you have launched my project!

> **DO NOT FORGET TO HAVE EXTENDED CONTROLS IN ORDER TO TOUCH THE SENSOR AFTER THE PROMPT!** Click on the *Touch Sensor* after the prompt. If the prompt does not appear when clicking on the icon, you have not registered your fingerprint!

# Some known bugs. NOTE: THESE BUGS ARE NOT RELATED TO THE PROJECT. THESE BUGS APPEAR TO EVERYONE WHO IS USING BUMBLEBEE IDE.

I have encountered silly bugs after the release of Android Studio bumblebee which as few errors. I made a list to keep an eye for.

## After launching the emulator, a prompt appears with a text "Emulator has been terminated" Possible fixes:

Check if you have enough space in your main disk. Sometimes the bug appears when running on low memory space. If the bug has not fixed, easiest way is to delete that device and repeat **3** and **5** process.

## Emulator randomly dissapears

After browsing the application or doing various clicks, sometimes the emulator dissapears, and instead of the emulator, the box with text is **"No emulators are currently running. To launch the emulator, use the Device Manager or run your app while targeting a virtual device"**. If you see that do:

> File -> Settings -> Tools -> Emulator and deselect *Launch in a tool window*

## When trying to launch the program error appears: Activity class does not exist

Simply reinstall the emulator
