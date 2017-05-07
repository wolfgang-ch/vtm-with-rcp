# VTM with Eclipse RCP

Using the OpenGL vector map [VTM](https://github.com/mapsforge/vtm) with an Eclipse RCP application

![VTM](https://github.com/wolfgang-ch/vtm-with-rcp/raw/master/html-resources/vtm-with-rcp.png)

## To get it Running in an Eclipse IDE

This is tested with Eclipse Neon.3 (4.6.3)

* First clone VTM repo <https://github.com/mapsforge/vtm> and use the folder name **vtm-parent**

* Import only these projects from the VTM repo 
  
  vtm  
  vtm-themes  
  vtm-gdx  
  vtm-desktop  

* Clone this repo <https://github.com/wolfgang-ch/vtm-with-rcp> in the same parent folder as the VTM repo

* Import all Eclipse projects from this repo  

* Build VTM\_RCP\_App with gradle by using EGradle <https://github.com/de-jcup/egradle>

* When running the app you also must select these plugins and their required plugins

  org.eclipse.equinox.ds  
  org.eclipse.equinox.event
