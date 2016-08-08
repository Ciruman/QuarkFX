# QuarkFX

#### Feature Extension to Gluon, 2016-07-15

#### By **Andreas Zimmerer, Luca Dombetzki**

## Table of Contents
# Table of Contents
1. [Description](#description)
2. [Installation Requirements](#installation-requirements)
3. [Features](#features)
3. [Setup](#setup)
3. [Usage](#usage)
3. [Technologies Used](#technologies-used)
3. [License](#license)

<div id='description' />

## Description

This project is designed to add some basic features to JavaFXPorts
and handles a lot of platform specific code just to make your life easier.

<div id='installation-requirements' />

## Installation Requirements

The project only makes sense to use if you use Gluon to create JavaFX apps!
This implies that this project depends on Gluon.

**This project is compiled with Gluon Version: _3.0.0_**

Any non backward compatible changes in a newer Gluon version may or may not _break_ this project.

<div id='features' />

## Features

- platform-specific logging
- Intent support on Desktop and Android (iOS might come in future)
- ViewHandler for (automatically) switching between Views 
- Orientation and Size Listener for responsive Layout
- fine touch (1px) for android
- Loading screen (right now waits for one second for demonstration purposes)

<div id='setup' />

## Setup

1. create/open a Gluon Project
2. add this project as a dependency
3. change a little bit of your code to make it use this project

#### Create/Open a Gluon Project
This should be easy :D

#### Add this Project as dependency
Add these lines to your dependencies section in your `build.gradle`:

[//]: # (TODO Remeber to update all Versions)
```groovy
compile 'com.zeiss.quarkfx:QuarkFX-Main:1.0.0-alpha'
desktopRuntime 'com.zeiss.quarkfx:QuarkFX-Desktop:1.0.0-alpha'
androidRuntime 'com.zeiss.quarkfx:QuarkFX-Android:1.0.0-alpha'
iosRuntime 'com.zeiss.quarkfx:QuarkFX-Ios:1.0.0-alpha'
```

**! To use Desktop Intents you need to create a runnable Jar and make sure also the desktop-specific dependencies are compiled !**

Example using the [javafx-gradle-plugin](https://github.com/FibreFoX/javafx-gradle-plugin):

[//]: # (TODO Remeber to update all Versions)
```groovy
apply plugin: 'javafx-gradle-plugin'

jfx {
    //minimal configuration
    verbose = true
    mainClass = "{package.to.gluon.class.MainClass}"
    jfxAppOutputDir = "build/jfx/app"
    jfxMainAppJarName = "{OutputJarName.jar}"
    deployDir = "src/gluon/deploy"
}

jfxJar.doFirst {
    //for now manually add (there might be a better way)
    println("adding desktop dependency")
    project.dependencies.add("runtime", "com.gluonhq:charm-desktop:3.0.0")
    project.dependencies.add("runtime", 'com.zeiss.quarkfx:QuarkFX-Desktop:1.0.0-alpha')
}
```

#### Change a little bit of your code

To make this Project work you have to pipe the basic JavaFX methods through the QuarkFXApplication

```java
//Extend QuarkFXApplication and the rest is self explanatory
public abstract class YourMainClass extends QuarkFXApplication {

    //IMPORTANT change the init method to this: (you can use the viewhandler to initialize your views)
    @Override
    public void initApp(ViewHandler viewmanager){
        //initialize your Views here (with the ViewHandler - explained below)
    }

    //IMPORTANT change the postInit method to this:
    @Override
    public void postInitApp(Scene scene){
        //post init stuff here
    }

    //IMPORTANT change your stop method (if you have one) to this
    @Override
    public void stopApp(){
        //javafx's stop method (called when application closes)
    }

    //called when this app was launched via intent
    @Override
    public void handleIntent(IntentP intent){
        //handle this intent please (intent is not null)
    }
}
```

<div id='usage' />

## Usage

### Logger

To log some stuff:
```java
Log.verbose(CurrentClass.class.getSimpleName(), "Message");
Log.debug(CurrentClass.class.getSimpleName(), "Message");
Log.info(CurrentClass.class.getSimpleName(), "Message");
Log.warning(CurrentClass.class.getSimpleName(), "Message");
Log.error(CurrentClass.class.getSimpleName(), "Message");
Log.exception(CurrentClass.class.getSimpleName(), "Message", throwable);
Log.log("myTag", "Message", LogLevel.error);
```

Get and set the LogLevel cap (verbose<debug<info<warning<error):
```java
LogLevel level = Log.getMinimumLogLevel();
Log.setMinimumLogLevel(level);
```
Only messages with a higher priority will be logged.

### ViewHandler

The ViewHandler works a little bit different than the one implemented in Gluon.
You have three steps

1. register your View-Packages (a package of all views for the different orientations and sizes)
2. set the HomeView (the first view to switch to after the loading screen)
3. switch Views whenever you like

#### Registering a View-Package:

A View Package is defined over the `IViewPackage` Interface.
A View Package defines its children depending on the Layout.
A child has to implement `IView`.

Here is a code example:
```java
public class SomeViewPackage implements IViewPackage {

    //the unique ID like in Gluon (but there is no predefined HOME_VIEW any more)
    public static final String ID = "SomeViewPackage";

    //with this method you define when which View is shown (depending on current Screen orientation and size)
    @Override
    public Class<? extends FXMLView> getView(OrientationListener.Size size, OrientationListener.Orientation orientation) {
        switch (orientation)
        {
            case LANDSCAPE:
                switch (size)
                {
                    case NORMAL: return NormalLandscapeView.class;
                    case XLARGE: return XLargeLandscapeView.class;
                }
                break;

            case PORTRAIT:
                switch (size)
                {
                    case NORMAL: return NormalPortraitView.class;
                    case XLARGE: return XLargePortraitView.class;
                }
                break;
        }
        //this tells the viewhandler to fallback to the defaults
        return null;
    }

    //returns the unique ID for this package
    @Override
    public String getID() {
        return ID;
    }

    //The default Orientation when a View was not found
    @Override
    public OrientationListener.Orientation getDefaultOrientation() {
        return OrientationListener.Orientation.PORTRAIT;
    }

    //The default Size when a View was not found
    @Override
    public OrientationListener.Size getDefaultSize() {
        return OrientationListener.Size.NORMAL;
    }
}
```

Example View using AfterburnerFX:
```java
public class NormalLandscapeView extends FXMLView implements IView{
    //nothing here because Afterburner :D
}
```

Example initApp method
```java
@Override
public void initApp(ViewHandler viewmanager){
    viewmanager.registerView(new SomeViewPackage1());
    viewmanager.registerView(new SomeViewPackage2());
    viewmanager.setHomeView(SomeViewPackage1.ID); //this explains the static variable in SomeViewPackage
    //That's it
}
```

View-Switching works like this:
```java
viewManager.switchView(SomeViewPackage2.ID, ViewStackPolicy.USE);
```

### Intents

Currently doesn't support sending/retrieving Custom URL Schemes (Intents) on iOS.

#### Sending:
```java
//create a platform-independant Intent (that's where the 'P' comes from)
IntentP intent = new IntentP();
//Use it like a normal android intent
intent.setAction("ACTION_VIEW");
intent.setData("http://google.com");
//in future not necessary (fix for desktop)
intent.setMimeType("html/html");
//optional (for explicit intents only)
intent.setReceiverMainClass("package.to.Main.class.of.app.MainClass");
//send it via the native service
QuarkFXApplication.getInstance().getNativePlatform().getNativeService().sendIntent(intent);
```

#### Receiving:

In the class that extends QuarkFXApplication just implement the method `public void handleIntent(IntentP intent){ }` and do whatever you want with it.

Next up, you have to tell Android that you want to receive an intent (Like it is done in "real Android"):

AndroidManifest.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<manifest ... >
    ...
    <application ... >
        <activity ... >
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
            <intent-filter>
                <action android:name="android.intent.action.SEND"/>
                <category android:name="android.intent.category.DEFAULT"/>
                <!-- specify the categories you want to listen on here -->
                <data android:mimeType="*/*"/>
            </intent-filter>
        </activity>
    </application>
</manifest>
```

And finally the same for the Desktop:

DesktopManifest.json (yes its a json, because it's just easier :D)
```json
{
  "label": "{Application Name}",
  "version": "{Application Version}",
  "description": "{Application Description}",
  "iconPath": "path/to/logo.png",
  "intentfilters": [
    {
      "action": "MAIN",
      "categories": [
        "LAUNCHER"
      ]
    },
    {
      "data": "*/*",
      "action": "SEND",
      "categories": [
        "DEFAULT"
      ]
    }
  ]
}
```


<div id='technologies-used' />

## Technologies Used

- Java / JavaFX
- GluonHQ (+ JavaFXPorts)

<div id='license' />

### License

*This software uses the 3-clause BSD license. See the accompanying LICENSE file for more details or download it [here](https://opensource.org/licenses/BSD-3-Clause) (https://opensource.org/licenses/BSD-3-Clause).*

Copyright (c) 2016 **Carl Zeiss Meditec AG**