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
IPAddress server(192,168,0,242); // Hue Bridge

boolean connectSuccess = false;
int testConnects = 0;

// Initialize the Ethernet client library
// with the IP address and port of the server 
// that you want to connect to (port 80 is default for HTTP):
EthernetClient client;

void contentBlock(char json[ ]){

      client.print("Content-Length:");
      int length = strlen(json);
      client.println(length);
      client.println();
      client.print(json);
     
}

void sendRequest(char requestType[], char address[], char json[]){
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
    }else{
      Serial.println("connection failed");
    }
  }
}

void setup() {
  // Open serial communications and wait for port to open:
  Serial.begin(9600);
  while (!Serial) {
    ; // wait for serial port to connect. Needed for Leonardo only
  }

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

    Serial.println("connecting...");

    // if you get a connection, report back via serial:
    if (client.connect(server, 80)) {
      Serial.println("connected");
      // Make a HTTP request:
      client.println("PUT /api/stephan123/lights/3/state HTTP/1.0");
      client.println("Content-Type:text/plain");
//      contentBlock("{\"hue\":62100, \"sat\":255, \"bri\":64}");
//      contentBlock("{\"hue\":46920, \"sat\":16, \"bri\":255, \"transitiontime\":20}");
//      contentBlock("{\"alert\":\"select\"}");
      contentBlock("{\"on\":false, \"transitiontime\":20}");
//      contentBlock("{\"on\"=false"};
      connectSuccess = true;
    } 
    else {
      // kf you didn't get a connection to the server:
      Serial.println("connection failed");
    }
  }

}

void loop()
{
  // if there are incoming bytes available 
  // from the server, read them and print them:
  if (client.available()) {
    char c = client.read();
    Serial.print(c);
  }

  // if the server's disconnected, stop the client:
  if (!client.connected()) {
    Serial.println();
    Serial.println("disconnecting.");
    client.stop();
    

    // do nothing forevermore:
    for(; testConnects<4; testConnects++){
//      sendRequest("PUT", "/api/stephan123/lights/3/state", "{\"bri\":64}");
//      client.stop();
//      delay(1000);
//      Serial.print("Test connect count:");
//      Serial.println(testConnects);
    }
    
    if(testConnects==4){
      //sendRequest("PUT", "/api/stephan123/lights/3/state", "{\"on\":false}");
      for(;;)
        ;
    }
  
  }
}


