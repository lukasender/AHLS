
/*
  Arduino/Hue Client
 
 This sketch connects to a website (http://www.google.com)
 using an Arduino Wiznet Ethernet shield. 
 
 Circuit:
 * Ethernet shield attached to pins 10, 11, 12, 13
 
 @Author: Stephan Svoboda
 @created 27.3.2013
 */

#include <SPI.h>
#include <Ethernet.h>

//test address
char hueTestAddress[]="/api/stephan123/lights/3/state";

//SERVER INFO

//HUE BRIDGE INFO
char hueUser[] = "stephan123";
IPAddress server(192,168,0,243); // Hue Bridge

//REDIRECT JSON
int lightId = 3;
String currResponse;

//REST CLIENT
// Enter a MAC address for your controller below.
// Newer Ethernet shields have a MAC address printed on a sticker on the shield
byte mac[] = {  
  0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };

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


//ensures that exact length of json is sent
void contentBlock(char json[ ]){

  int length = strlen(json);
  if(length>=0){
    client.print("Content-Length:");
    client.println(length);
    Serial.println(length);
    client.println("Content-type: application/json");
  }
  client.println();
  if(length>=0){
    client.print(json);
  }

}

//writes response to currResponse
void writeResponse(){
  Serial.println("---start---writeResponse---");
  //while client is connected or has unread buffered bytes
  while(client.connected()){
    //String response;
    currResponse = "";
    char oc = ' ';
    char ooc = ' ';
    int currResponseLength = 0;
    boolean messageFound = false;

    while (client.available()) {     
      char cc = client.read();
      
      if(messageFound == true){
        currResponse = currResponse + cc;
        currResponseLength++;
        Serial.print("currResponse length: ");
        Serial.println(currResponseLength);
      }
      
      if(ooc == '\n' && oc == '\r' && cc == '\n'){
        messageFound = true;        
      }
      responseDelivered = true;
      
      Serial.print(cc);
      ooc = oc;
      oc = cc;
    }
    //currResponse = response;
    Serial.println();
    Serial.println("---start---currResponse---");
    Serial.println(currResponse);
    Serial.println("---end---currResponse---");
  }
   Serial.println("---end---writeResponse");
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
    }
    else{
      Serial.println(client.available());
      Serial.println("connection failed");
    }
  }
}

void executeResponse(){
    //replace @ in json
    currResponse.replace("\"@", "\"");
    //correct type from string to int/bool
    currResponse.replace(":\"",":");
    currResponse.replace("\",",",");
    currResponse.replace("\"}","}");
  
    Serial.println("Extracted message from response:");
    Serial.println(currResponse);
    String json;
    String hueAddress;
    json = currResponse;
    Serial.print("JSON for light id ");
    Serial.print(lightId);
    Serial.print(": ");
    Serial.println(json);
    char jsonChar[json.length()+1];
    json.toCharArray(jsonChar, json.length()+1);
    
    //construct light state address
    hueAddress.concat("/api/");//concat(hueUser);// + "/lights" + light + "/state";
    hueAddress.concat(hueUser);// + "/lights" + light + "/state";
    hueAddress.concat("/lights/");// + light + "/state";
    hueAddress.concat(lightId);
    hueAddress.concat("/state");
    Serial.print("JSON sent: ");
    Serial.println(jsonChar);
    char hueChar[hueAddress.length()+1];
    hueAddress.toCharArray(hueChar, hueAddress.length()+1);
    Serial.print("JSON sent to: ");
    Serial.println(hueChar);
    sendRequest("PUT", hueChar, jsonChar, server, 80);
}

//MOTION DETECTION

void doSensorReading(){

  IPAddress ip(192,168,0,242);
  // read the analog in value:
  sensorValue = 512; //analogRead(analogInPin);            
  // map it to the range of the analog out:
  outputValue = map(sensorValue, 0, 1023, 0, 255);  
  // change the analog out value:
  analogWrite(analogOutPin, outputValue);           

  Serial.print("\t median = ");      
  Serial.println((oldSensorValue+sensorValue)/2);   
  oldSensorValue = (oldSensorValue+sensorValue)/2;

  // wait 5 milliseconds before the next loop
  // for the analog-to-digital converter to settle
  // after the last reading:
  delay(5);
  String jsonStart = String("{\"@sensor-id\":1,\"@username\":\"031796799e76cf794757b4cd59bd4eb7d0970abb\",\"@data\":\"");
  String jsonEnd = String("\"}"); //second "}" because strlen recognizes one char to less
  String json=String(jsonStart+oldSensorValue+jsonEnd);
  char jsonChar[json.length()+1];
  json.toCharArray(jsonChar, json.length()+1);
  Serial.println(json);
  currResponse = "";
  sendRequest("POST", "/AHLSWebService/ahls/log", jsonChar, ip, 8080);
  executeResponse();
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
//  Serial.println("---FIRST TEST REQUEST START---");  
//  while(connectSuccess == false){
//    sendRequest("PUT", hueTestAddress, "{\"on\":false}", server, 80);
//  }
//  Serial.println("---FIRST TEST REQUEST END---");

   Serial.println();
   Serial.println("---START SENSOR READING---");

}

void loop()
{

  // if the server's disconnected, stop the client:
  if (!client.connected()) {

    // do sensor reading forever:
        doSensorReading();
        delay(50);//break time
        //sendRequest("PUT", hueTestAddress, "{\"on\":false}", server, 80);
    }
}





