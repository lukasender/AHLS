
/*
  Arduino/Hue Client
 
 This sketch connects to a website (http://www.google.com)
 using an Arduino Wiznet Ethernet shield. 
 
 Circuit:
 * Ethernet shield attached to pins 10, 11, 12, 13
 
 @Author: Matthias Amann, Lukas Ender, Stephan Svoboda
 @created 27.3.2013
 */

#include <SPI.h>
#include <Ethernet.h>

//test address
char hueTestAddress[] = "/api/stephan123/lights/3/state";

//SERVER INFO
IPAddress baseServer(192, 168, 0, 242);
int baseServerPort = 8080;

//HUE BRIDGE INFO
char hueUser[] = "stephan123";
IPAddress hueServer(192, 168, 0, 243);
int hueServerPort = 80;

//REDIRECT JSON
int lightId = 3;
String currResponse;

//REST CLIENT
// Enter a MAC address for your controller below.
// Newer Ethernet shields have a MAC address printed on a sticker on the shield
byte mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };

boolean connectSuccess = false;
boolean responseDelivered = false;
int testConnects = 0;

// Initialize the Ethernet client library
// with the IP address and port of the server 
// that you want to connect to (port 80 is default for HTTP):
EthernetClient client;

//MOTION DETECTION
const int analogInPin = A0;  //PIR Read
const int analogOutPin = 9; // PIR Write

int sensorValue = 0;        // value read from PIR
int oldSensorValue = 0;      // old value from PIR
int outputValue = 0;        // value output to the PWM (analog out)

//REST CLIENT
// Json
String sensorTag = String("{\"@sensor-id\":");
String usernameTag = String(",\"@username\":\"");
String dataTag = String("\",\"@data\":");
String endTag = String ("}");

//ensures that exact length of json is sent
void contentBlock(char json[]) {
  int length = strlen(json);
  if(length >= 0) {
    client.print("Content-Length:");
    client.println(length);
    Serial.println(length);
    client.println("Content-type: application/json");
  }
  client.println();
  if(length >= 0) {
    client.print(json);
  }

}

//writes response to currResponse
void writeResponse() {
  Serial.println("---start---writeResponse---");
  //while client is connected or has unread buffered bytes
  while(client.connected()) {
    //String response;
    currResponse = "";
    char oc = ' ';
    char ooc = ' ';
    int currResponseLength = 0;
    boolean messageFound = false;

    while (client.available()) {     
      char cc = client.read();
      
      if(messageFound == true) {

      switch (oc) {
      case '{':
        if (cc == '\"') {
          // write oc
          currResponse.concat(oc);
          break;
        }
      case '"':
        if (cc == '@') {
          // write oc
          currResponse.concat(oc);
          break;
        }
        if (cc == ':') {
          // write oc
          currResponse.concat(oc);
          break;
        }
        if (cc == ',') {
          // do not write oc
          break;
        }
        if (cc == '}'){
          //do write cc
          currResponse.concat(cc);
          break;
        }
      case ':':
        if (cc == '\"') {
          // write oc
          currResponse.concat(oc);
          break;
        }
      case '@':
        // do not write oc
        break;
      case ',':
        // write oc
        currResponse.concat(oc);
        break;
      case '}': 
        // write oc
        currResponse.concat(oc);
        break;
      case '\n':{
        //do not write oc
        break;
      }
      default:
        currResponse.concat(oc);
        break;
      }
      ++currResponseLength;
    }

      if(ooc == '\n' && oc == '\r' && cc == '\n'){
        messageFound = true;        
      }
      responseDelivered = true;

      ooc = oc;
      oc = cc;
    } 
  }
}

//sends request address is without ip
void sendRequest(char requestType[], char address[], char json[], IPAddress ip, int port){
  //set connectSuccess to false
  connectSuccess = false;
  Serial.println("connecting...");
  while(!connectSuccess){
    if (client.connect(ip, port)) {
      client.print(requestType);
      client.print(" ");
      client.print(address);
      client.println(" HTTP/1.0");
      contentBlock(json);
      connectSuccess = true;
      //connection successful waiting for response
      writeResponse();
      client.stop();
    } else {
      Serial.println(client.available());
      Serial.println("connection failed");
    }
  }
}

void executeResponse(){
  Serial.println("executeResponse");
  Serial.println(currResponse);
  String json = currResponse;
  String hueAddress;
  Serial.print("JSON for light id ");
  Serial.print(lightId);
  Serial.print(": ");
  Serial.println(json);
  char jsonChar[json.length() + 1];
  json.toCharArray(jsonChar, json.length() + 1);

  //construct light state address
  hueAddress.concat("/api/");
  hueAddress.concat(hueUser);
  hueAddress.concat("/lights/");
  hueAddress.concat(lightId);
  hueAddress.concat("/state");
  Serial.print("JSON sent: ");
  Serial.println(jsonChar);
  char hueChar[hueAddress.length() + 1];
  hueAddress.toCharArray(hueChar, hueAddress.length() + 1);
  Serial.print("JSON sent to: ");
  Serial.println(hueChar);
  sendRequest("PUT", hueChar, jsonChar, hueServer, hueServerPort);
}

//MOTION DETECTION

void readSensor(){
  // read the analog in value:
  sensorValue = analogRead(analogInPin);            
  // map it to the range of the analog out:
  
  //outputValue = map(sensorValue, 0, 1023, 0, 255);
  //oldSensorValue = (oldSensorValue + sensorValue) / 2;
}

void postActivityData(int sensorId, String username) {
  String json = String(sensorTag + sensorId + usernameTag + username + dataTag + sensorValue + endTag);
  char jsonChar[json.length() + 1];
  json.toCharArray(jsonChar, json.length() + 1);
  Serial.println(json);
  currResponse = "";

  sendRequest("POST", "/AHLSWebService/ahls/log", jsonChar, baseServer, baseServerPort);
}

void setup() {
  // Open serial communications and wait for port to open:
  Serial.begin(9600);

  // start the Ethernet connection:
  if (Ethernet.begin(mac) == 0) {
    Serial.println("Failed to configure Ethernet using DHCP");
    // no point in carrying on, so do nothing forevermore:
    for(;;)
      ;
  }
  // give the Ethernet shield a second to initialize:
  delay(1000);
  
  Serial.println("---START SENSOR READING---");
}

void loop()
{
  // if the server's disconnected, stop the client:
  if (!client.connected()) {

    // do sensor reading forever:
    readSensor();
    postActivityData(1, "031796799e76cf794757b4cd59bd4eb7d0970abb");
    //executeResponse();
    //sendRequest("PUT", hueTestAddress, "{\"on\":false}", hueServer, hueServerPort);
  }
}








