# VTM with Eclipse RCP

Using the OpenGL vector map [VTM](https://github.com/mapsforge/vtm) with an Eclipse RCP application

![VTM](https://github.com/wolfgang-ch/vtm-with-rcp/raw/master/html-resources/vtm-with-rcp.png)

## How to get it running in an Eclipse IDE

This is tested with Eclipse Oxygen.2 (4.7.2)

* First clone [VTM repo](https://github.com/mapsforge/vtm) and use the folder name **vtm-parent**

* Import only these projects from the VTM repo 
  
  vtm  
  vtm-desktop  
  vtm-gdx  
  vtm-themes  

* Second clone [this repo](https://github.com/wolfgang-ch/vtm-with-rcp) into the same parent folder as the VTM repo

* Import all Eclipse projects from this repo  

* Build VTM\_RCP\_App with gradle by using EGradle <https://github.com/de-jcup/egradle> with [``Import gradle root project with all subprojects``](https://github.com/wolfgang-ch/vtm-with-rcp/raw/master/html-resources/vtm-rcp-import-03.png)  
  Uncheck *Detect and configure project natures* in the import project dialog

* When running the app you also must select these plugins and their required plugins

  org.eclipse.equinox.ds  
  org.eclipse.equinox.event


* Some includes in the file `\vtm-parent\settings.gradle`  must be disabled

```
rootProject.name = 'vtm-parent'
include ':vtm'
//include ':vtm-android'
//include ':vtm-android-example'
//include ':vtm-android-gdx'
//include ':vtm-app'
include ':vtm-desktop'
include ':vtm-extras'
include ':vtm-gdx'
include ':vtm-http'
//include ':vtm-ios'
//include ':vtm-ios-example'
//include ':vtm-jeo'
include ':vtm-json'
include ':vtm-jts'
//include ':vtm-playground'
//include ':vtm-tests'
//include ':vtm-theme-comparator'
include ':vtm-themes'
//include ':vtm-web'
//include ':vtm-web-app'
//include ':vtm-web-js'  
``` 

