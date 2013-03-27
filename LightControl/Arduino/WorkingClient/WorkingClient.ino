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

// Enter a MAC address for your controller below.
// Newer Ethernet shields have a MAC address printed on a sticker on the shield
byte mac[] = {  
  0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
IPAddress server(192,168,0,243); // Hue Bridge

boolean connectSuccess = false;
boolean responseDelivered = false;
int testConnects = 0;

// Initialize the Ethernet client library
// with the IP address and port of the server 
// that you want to connect to (port 80 is default for HTTP):
EthernetClient client;


//ensures that exact length of json is sent
void contentBlock(char json[ ]){

      client.print("Content-Length:");
      int length = strlen(json);
      client.println(length);
      client.println();
      client.print(json);
     
}

//after one second writes response
void writeResponse(){
  delay(500);
          while (client.available()) {
            char c = client.read();
            Serial.print(c);
            responseDelivered = true;
          }
}

//sends request address is without ip
void sendRequest(char requestType[], char address[], char json[]){
  //set connectSuccess to false
  connectSuccess = false;
  Serial.println("connecting...");
  while(!connectSuccess){
    if (client.connect(server, 80)) {
      client.print(requestType);
      client.print(" ");
      client.print(address);
      client.println(" HTTP/1.0");
      contentBlock(json);
      connectSuccess = true;
      //connection successful waiting for response
      writeResponse();
    }else{
      Serial.println("connection failed");
    }
  }
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
      sendRequest("PUT", "/api/stephan123/lights/2/state", "{\"on\":false}");
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
      sendRequest("PUT", "/api/stephan123/lights/2/state", "{\"on\":true, \"ct\":153}");
            
      client.stop();

      Serial.println();
      Serial.print("Test connect count:");
      Serial.println(testConnects);
    }
    
    if(testConnects==4){

      sendRequest("PUT", "/api/stephan123/lights/2/state", "{\"alert\":\"lselect\"}");

      client.stop();

      Serial.println();
      Serial.print("Test connect count:");
      Serial.println(testConnects);
      for(;;)
        ;
      }

    }
}


