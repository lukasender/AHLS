# AHLS

As of now, we do not provide an 'easy way' for setting up things.
This setup is intended to be a development environment. There is some effort required.
This process may change in the future.

## Requirements
- JRE 6
- Apache Tomcat 7.x (http://tomcat.apache.org/)
- MySQL 5.5 or greater
- Arduino UNO
- Philips Hue (http://www.meethue.com/en-US)
- PIR movement sensors (TODO: provide a description)
- A network router or bridge.

## Setup

### Database
- run `SQL/ahls_v1_prototype.sql`.
- If you instead want to have some test data inserted already, run `SQL/ahls_v1_backup_of_luis_db.sql`.
- Created on OS X 10.8. Tested on OS X 10.8 and Windows 7. Though, we had some problems on a Windows machine with `PASSWORD('ahls')` function.
Make sure you can connect to your MySQL database with a user 'ahls' and a password 'ahls'.

### Web Service
- We provide a deployable WAR (optimized for Apache Tomcat 7.x) which can be found in the `/bin` folder.
- You can also set up a web server like folows:
- AHLSWebService is an Eclipse project. Deploy it via Eclipse on your Apache Tomcat installation.
- AHLSWebService provides a REST API. Refer to `AHLSWebService/src/at/ahls/web/rest/api/AHLS.java` for the complete API.
- For a quick test fire up a web browser (or curl) and process an ordinary `GET http://localhost:8080/AHLSWebService/ahls/log/10`. This should give you some database entries (if some data entries were made already) as a result in either JSON or XML formatting.
- Connect the machine which is running the web server to your network.

### Arduino
- Compile and push `AHLS/LightControl/Arduino/WorkingClient/WorkingClient.ino` to your Arduino.
- Attention: You may need to adjust the IP address of you Philips Hue Bridge!
- - You can either adjust the IP in the code or in the IP settings of your router (at this time, we use `192.168.0.244`)
- - The same is true for the web server. We use `192.168.0.242` at this time.
- Connect to movement sensors to your Arduino (5V, GND, GND and A0 for data signals).
- Connect it to your network.

### Philips Hue
- Set it up according the the Philips Hue instructions.
