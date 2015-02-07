# Door
Server application which communicates with an Arduino to open the door and sends notifications when the doorbell is rang.


Communicates with Android apps via a XMPP connection with the Google Cloud Messaging servers 

## Config
Create a config.json file including the following code:
`{
	"GCM" : {
		"senderId" : 0,
		"password" : ""
	}
}`

## Running
run `mvn scala:compile` to compile and `mvn scala:run` to run.
