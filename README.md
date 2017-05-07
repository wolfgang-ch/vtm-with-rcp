# VTM with Eclipse RCP

Using the OpenGL vector map [VTM](https://github.com/mapsforge/vtm) with an Eclipse RCP application

![VTM](https://github.com/wolfgang-ch/vtm-with-rcp/raw/master/html-resources/vtm-with-rcp.png)

## How to get it running in an Eclipse IDE

This is tested with Eclipse Neon.3 (4.6.3) for Committers

* First clone [VTM repo](https://github.com/mapsforge/vtm) and use the folder name **vtm-parent**

* Import only these projects from the VTM repo 
  
  vtm  
  vtm-desktop  
  vtm-gdx  
  vtm-themes  

* Second clone [this repo](https://github.com/wolfgang-ch/vtm-with-rcp) into the same parent folder as the VTM repo

* Import all Eclipse projects from this repo  

* Build VTM\_RCP\_App with gradle by using EGradle <https://github.com/de-jcup/egradle> with [``Import gradle root project with all subprojects``](https://github.com/wolfgang-ch/vtm-with-rcp/raw/master/html-resources/vtm-rcp-import-03.png)

* When running the app you also must select these plugins and their required plugins

  org.eclipse.equinox.ds  
  org.eclipse.equinox.event
