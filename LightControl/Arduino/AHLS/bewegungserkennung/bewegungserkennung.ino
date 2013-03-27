//Made By TrustFm for Hw2Sw.com
 
int Motion_Led_Pin = 13;                // choose the pin for the LED
int PIR_Sensor_Pin = A0;                 // choose the input pin (for PIR sensor)
boolean Motion_Already_Detected = false;// we start, assuming that no motion is detected
int PIR_Sensor_Status = 0;              // variable for reading the current PIR_Sensor_Status
unsigned long Duration_Of_Motion;
String Message;
 
void setup() {
  pinMode(Motion_Led_Pin, OUTPUT);      // declare LED as output
  pinMode(PIR_Sensor_Pin, INPUT);     // declare sensor as input
 
  Serial.begin(9600);
}
 
void loop(){
 
  PIR_Sensor_Status = digitalRead(PIR_Sensor_Pin);  // read PIR Sensor status
 
  if (PIR_Sensor_Status == LOW) {                     // if the PIR Sensor is low (motion)
 
    digitalWrite(Motion_Led_Pin, HIGH);  // turn LED ON
    if (Motion_Already_Detected  == false) { //If we did not have motion before -> then change the current motion state to true
      Serial.println("Motion detected! [start]");
      Motion_Already_Detected  = true;
      Duration_Of_Motion = millis();
    }
 
  } else {                     // if the PIR Sensor status is HIGH (no motion)
 
    digitalWrite(Motion_Led_Pin, LOW); // turn LED OFF
    if (Motion_Already_Detected  == true){ //We had motion before so -> change the current motion state to false
      Duration_Of_Motion = millis() - Duration_Of_Motion;
 
      Message = "The motion detected ended! Motion duration :  ";
      Message += Duration_Of_Motion;
      Message += " ms";
      Serial.println(Message);
      Motion_Already_Detected = false;
    }
  }
 delay(20);
}
