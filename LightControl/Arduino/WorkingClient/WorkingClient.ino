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

//test address
char hueTestAddress[]="/api/stephan123/lights/3/state";

//SERVER INFO

//HUE BRIDGE INFO
char hueUser[] = "stephan123";
IPAddress server(192,168,0,243); // Hue Bridge

//REDIRECT JSON
// const int MAXBULBS = 50;
// int commandCount = 0;
// int startCommand = 0;
// int endCommand = 0;
// int startIndices[MAXBULBS];
// int endIndices[MAXBULBS];
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

//extracts commands of AHLS light-data
//void extractCommands(String response, int endIndices[]){
//  int bodylength = response.length();
//  int curr = 0;
//  int commandStart = 0;
//  int commandEnd = 0;
//  commandCount = 0;

//  while(curr < bodylength){

//    switch(response[curr]){
//    case '{': 
//      startCommand = curr;//start found
//      endIndices[startCommand] = endCommand;
//      break;
//    case  '}':
//      endCommand = curr;
//      endIndices[commandCount] = endCommand;
//      commandCount++;
//      break;
      //default:
      //reserved
//    }
//    curr++;
//  }
  //return endIndices;
//}

//returns light id and start and end of json with id
//int getNextCommand(String message, int *msgStart, int *msgEnd, int *idStart, int *idEnd){
//  boolean commandFound = false;
//  boolean openBraceFound = false;
//  int curr=*msgEnd;
//  while(!commandFound && curr > message.length()){
//    switch(message[curr]){
//    case '{':
//      openBraceFound = true;
//      *msgStart = curr;
//      break;
//    case '}':
//      if(openBraceFound){
//        *msgEnd = curr;
//        commandFound = true;
//      }
//      else{
//        return -1;
//      }
//      break; 
//    }
//    curr++;
//  }
  //get light id
//  boolean lightIdFound = false;
//  int offset = *msgStart+8;
//  offset = message.indexOf("\"light-id\":\"", offset); //"light-id":" 12 chars
//  *idStart = offset;
//  offset += 12;
//  String id;
//  while(message[12] != '\"' && isDigit(message[12])){
//    id += message[12];
//    offset++;
//  }
//  *idEnd = offset;
//  return id.toInt();
//}

//writes response to currResponse
void writeResponse(){
  //while client is connected or has unread buffered bytes
  while(client.connected()){
    String response;
    while (client.available()) {
      char oc = ' ';
      boolean messageFound = false;
      char cc = client.read();
      if(&oc == "\n" && &cc == "\n"){
        messageFound = true;
      }
      if(messageFound == true){
        response.concat(cc);
      }
      responseDelivered = true;
      Serial.print(cc);
    }
    currResponse = response;
    Serial.println();
    Serial.println("---start---currResponse---");
    Serial.println(currResponse);
    Serial.println("---end---currResponse---");
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
    }
    else{
      Serial.println(client.available());
      Serial.println("connection failed");
    }
  }
}

void executeResponse(){
    //clear currResponse for testint purpose
  String testResponse;
  //test header
  //testResponse += "HTTP \n";
  //testResponse += "Content-length: \n";
  //testResponse += "MIM-Type \n";
  //testResponse += "\n";
  //test message
  testResponse += "{\"@on\":true, \"@transitiontime\":50, \"@bri\":255, \"@ct\":153}";
  currResponse = "";
  for(int i = 0; i < testResponse.length(); i++){
    if(testResponse[i] != '@'){
      Serial.print("Character ");
      Serial.print(testResponse[i]);
      Serial.println(" added");
      currResponse.concat(testResponse[i]);
    } 
  }
//  int startMessage = currResponse.indexOf("\n\n")+2;//findStartOfMessage()
//  String message = currResponse.substring(startMessage, currResponse.length());
  
    Serial.println("Extracted message from response:");
  Serial.println(currResponse);
//  int *pStartJson = 0;
//  int *pEndJson = 0;
//  int *pIdStart = 0;
//  int *pIdEnd = 0;
//  int light = 0; //-->fix value
//  while( light >= 0){
    String json;
//    String cut;
    String hueAddress;
//    light = getNextCommand (message, pStartJson, pEndJson, pIdStart, pIdEnd);
//    cut = message.substring(*pIdStart, *pIdEnd);
    json = currResponse;
//    json.replace(cut, "");
    Serial.print("JSON for light id ");
    Serial.print(lightId);
    Serial.print(": ");
    Serial.println(json);
    char jsonChar[json.length()+1];
    json.toCharArray(jsonChar, json.length()+1);

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
//  }
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
  char jsonBuffer[256];
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
  while(connectSuccess == false){
    sendRequest("PUT", hueTestAddress, "{\"on\":false}", server, 80);
  }
//  Serial.println("---FIRST TEST REQUEST END---");

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
      sendRequest("PUT", hueTestAddress, "{\"on\":true, \"ct\":400}", server, 80);

      client.stop();

      Serial.println();
      Serial.print("Test connect count:");
      Serial.println(testConnects);
    }

    if(testConnects==4){

      sendRequest("PUT", hueTestAddress, "{\"ct\":500}", server, 80);
      client.stop();

      sendRequest("PUT", hueTestAddress, "{\"on\":false, \"ct\":350, \"bri\":255}", server, 80);
      client.stop();

      Serial.println();
      Serial.print("Test connect count:");
      Serial.println(testConnects);
      Serial.println("---START SENSOR READING---");
      for(;;)
        doSensorReading();
    }

  }
}





