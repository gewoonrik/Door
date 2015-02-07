# Door
Server application which communicates with an Arduino to open the door and sends notifications when the doorbell is rang.


Communicates with Android apps via a XMPP connection with the Google Cloud Messaging servers 

## Config
Create a config.json file including the following JSON:
`{
		"senderId" : YOUR_GCM_SENDER_ID_AS_INT,
		"password" : "YOUR_GCM_PASSWORD"
}`

## Running
run `mvn scala:compile` to compile and `mvn scala:run` to run.
