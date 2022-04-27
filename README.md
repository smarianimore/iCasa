# SM NOTES

In this repo thre are two things:
 - data generation
 - online intervention

Experiments workflow:
 1. Python scripts ask for interventions to `InterventionManager` through embedded server (see below) or to `DevicePropertyREST`
 2. `InterventionManager` or `DevicePropertyREST` execute interventions
 3. Python scripts sample new state via REST calls

`SystemManager` + `DeviceComponent` are used to generate the observational data (they are not used while online learning)

Main components are:
 - `SystemManager`: samples devices (snapshot), writes on dataset (csv file), generates new random values for variables (both doable and non-doable)
 - `DeviceComponent`: checks if variables values are physically realistic (e.g. if cooler is on temperature goes down), implements such behaviour, and actually takes the snapshot in JSON format
 - `InterventionManager`: carries out interventions by starting embedded server (TCP socket) exploited by Python scripts to ask for interventions ([companion Python repo here](https://github.com/smarianimore/multiagent_algorithm))
 - `DevicePropertyREST`: utility class wrapping REST calls to modify device properties, as an alternative to `InterventionManager` (credits to German Vega)

# JAVA iCASA WORKSPACE

The files in this repository contains my personal code for iCasa simulation software.

Author:
Pasquale Roseti

The scripts folder contains the scripts usable with the software:
- room.bhv : script for a single room environment
- paper_script.bhv : script for a multi-room environment
The program runs with both the scripts, just remember to change the name of the dataset file when changin environment, if you want to not override it.
It is possible to change it in the datasetManager\src\iCasa\dataset\manager\datasetManagerImpl.java file.
