## Setup

On your development machine:

1. Clone into agv_v1: git clone git@bitbucket.org:mochilafulfillment/agv_v1.git
2. Build: `$ cd agv_v1 && mvn clean install`


On AGV robot computer:

1.`$ ssh username@ipAddress` 
* For prototype agv computer on iPhone hotspot `$ ssh @user1@172.20.10.5`                     
* For prototype agv computer on AT&T hotspot: `$ ssh @user1@192.168.1.46`          
* For AT&T network: `$ ssh @user1@192.168.1.70`
* To find ipAddress on host 
    * Show a list of all available networks`$ nmcli dev wifi`
    * Choose the network you wish to connect to and run `$ nmcli dev wifi connect ESSID_NAME password ESSID_PASSWORD`
* Note: password = `mochila`

2. Create a `/agv` directory from root.
3. Set up static USB ports for `ttyUSBMOT` and `ttyUSBSCAN`. See https://msadowski.github.io/linux-static-port/ and https://unix.stackexchange.com/questions/66901/how-to-bind-usb-device-under-a-static-name for help
4. If not yet set on AGV computer environmental variables must be set on AGV computer
   * `$export MOCHILA_ENV=TEST` for no Rollbar logging or `$export MOCHILA_ENV=PRODUCTION` for Rollbar logging

---

## Editing and running AGV code

Make changes and compile on development computer (client) and file transfer over ssh to AGV computer (server):

1. Once changes are complete, run `cd/agv_v1 && mvn clean install`
2. Over the ssh connection, transfer the target directory from the server module to the server machine`cd sub-project-server && scp -r target user1@ipAdresss:~/agv`
3. Run the jar file now on the server:
`java -jar sub-project-server-1.0-SNAPSHOT-jar-with-dependencies.jar`
4. Run the client jar on the client computer: `cd sub-project-client && java -jar agv/target/agv-1.0-SNAPSHOT.jar` 



---


## View logs stored on AGV computer

Logging configuration in: `/src/main/resources/logback.xml`


From root on agv computer:

* Open all logs from current day: `$ vim agv_logs/log`

* See log files from past days: `$cd agv_logs && ls`


From your computer:

* Send logs to your machine: `$ scp -r user1@ipAddress:/home/user1/agv/logs/* /home/jacob/agv_v1/sub-project-server/logs/main/`

Rollbar/Logback:
* JSON log data sent to Rollbar
* Logging configuration file found in logback.xml
* Unit testing logging file is logback-test.xml

---


## Sensors

Position Barcode Scanner

* Pepperl+Fuchs PGV100-F200A-R4-V19 with PCV-USB-RS485-Converter Set
* Comm protocol: RS-485

Motor Controller

* Technosoft iPOS 4850 
  * 50 Amp continuous controllers @ 48V
  * 2 PNP outputs per controller
  * 3 digital outputs per controller
  * Additional brake output per controller which can be configured as regular output
  * (1) EasyMotion Studio software license purchased to set up all controllers
* Comm protocol to Computer: RS-232
* Comm protocol between controllers: CAN

Digital inputs into motor controller

*  (3) Fork Lift sensors (Top, Middle/Pick, Bottom)

Digital outputs from motor controller
* (2) outputs to select mode of the IDEC SE2L-H05LP safety scanner
* (2) fork lift outputs (motor up and down)
* (1) controller failure emergency stop



---
## Notes

Logging

* Logs from most recent test saved in logs/main/lastrunlog.log
* Logs from test logs saved in logs/test and have separate logback.xml in src/test/resources
* Each AGV must be configured with the following environmental variables
  * "${MOCHILA_ENV}" = PRODUCTION or TEST
  * "${MOCHILA_ROLE}" = AGV
  * "${MOCHILA_NAME}" = specific to each AGV (ID tag placed on each AGV)

Dependencies

* The org.scream3r/jssc package is used to send ASCII hexadecimal commands to the robot computer's physical serial ports via Java
* Logback is used for logging to text files and to JSONs to be sent to Rollbar
* Rollbar is used for tracking errors from AGV instances on the cloud

Sensors - Position Scanner

* Byte mapping from user manual: https://drive.google.com/file/d/1tRos_yL4IRyXl9nA0DmMmhB__HzXdcRR/view?usp=sharing
* Vision configurator reference: https://drive.google.com/file/d/1tUj5WDIL02kuj9hED7liZ31fU5Lu9k9p/view?usp=sharing

Sensors - Safety

* E stops + IDEC SE2L safety scanner are configured through IDEC FS1A safety controller and are independent of program
* Two Safety Category 3/SIL2 contactors are between FS1A and the power supply to motor controller

---
## AGV computer details:

Hardware: Ultra Small Fanless Quad-Core PC, Dual LAN, 8 GB Dual Channel Memory. https://www.onlogic.com/cl210g-11/

username: jacob
computer name: agv
password: mochila
Serial: U859323
Model: CL210G-11
IPv4 Address: 172.20.10.5
Remote Access Token: jacob@172.20.10.5

$ java -version  
openjdk version "11.0.10" 2021-01-19  
OpenJDK Runtime Environment (build 11.0.10+9-Ubuntu-0ubuntu1.18.04)  
OpenJDK 64-Bit Server VM (build 11.0.10+9-Ubuntu-0ubuntu1.18.04, mixed mode, sharing)  

$ cat /etc/os-release  
NAME="Ubuntu"  
VERSION="18.04.2 LTS (Bionic Beaver)"  
ID=ubuntu  
ID_LIKE=debian  
PRETTY_NAME="Ubuntu 18.04.2 LTS"  
VERSION_ID="18.04"  
HOME_URL="https://www.ubuntu.com/"  
SUPPORT_URL="https://help.ubuntu.com/"  
BUG_REPORT_URL="https://bugs.launchpad.net/ubuntu/"  
PRIVACY_POLICY_URL="https://www.ubuntu.com/legal/terms-and-policies/privacy-policy"  
VERSION_CODENAME=bionic  
UBUNTU_CODENAME=bionic  