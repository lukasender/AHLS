/*
  Web client
 
 This sketch connects to a website (http://www.google.com)
 using an Arduino Wiznet Ethernet shield. 
 
 Circuit:
 * Ethernet shield attached to pins 10, 11, 12, 13
 
 created 18 Dec 2009
 modified 9 Apr 2012
 by David A. Mellis
 
 */

#include <SPI.h>
#include <Ethernet.h>

//REST CLIENT
// Enter a MAC address for your controller below.
// Newer Ethernet shields have a MAC address printed on a sticker on the shield
byte mac[] = {  
  0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
IPAddress server(192,168,0,242); // Hue Bridge

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

//after one second writes response
void writeResponse(){
  //while client is connected or has unread buffered bytes
  while(client.connected()){
          while (client.available()) {
            char c = client.read();
            Serial.print(c);
            responseDelivered = true;
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
    }else{
      Serial.println(client.available());
      Serial.println("connection failed");
    }
  }
}

//MOTION DETECTION

void doSensorReading(){
  
  IPAddress ip(192,168,0,245);
  // read the analog in value:
  sensorValue = analogRead(analogInPin);            
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
  char jsonBuffer[100];
  String jsonStart = String("{\"@data\":\"");
  String jsonEnd = String("\"}}"); //second "}" because strlen recognizes one char to less
  String json=String(jsonStart+oldSensorValue+jsonEnd);
  char jsonChar[json.length()];
  json.toCharArray(jsonChar, json.length());
  Serial.println(json);
  sendRequest("POST", "/AHLSWebService/ahls/log", jsonChar, ip, 8080);
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
  
    while(connectSuccess == false){
      sendRequest("PUT", "/api/stephan123/lights/1/state", "{\"on\":false}", server, 80);
  }

}

void loop()
{

  // if the server's disconnected, stop the client:
  if (!client.connected()) {
    Serial.println();
    Serial.println("disconnecting.");
    client.stop();
    
    // do nothing forevermore:
    for(; testConnects<4; testConnects++){
      sendRequest("PUT", "/api/stephan123/lights/1/state", "{\"on\":true, \"ct\":153}", server, 80);
            
      client.stop();

      Serial.println();
      Serial.print("Test connect count:");
      Serial.println(testConnects);
    }
    
    if(testConnects==4){

      sendRequest("PUT", "/api/stephan123/lights/1/state", "{\"alert\":\"lselect\"}", server, 80);

      client.stop();

      Serial.println();
      Serial.print("Test connect count:");
      Serial.println(testConnects);
      for(;;)
        doSensorReading();
      }

    }
}


